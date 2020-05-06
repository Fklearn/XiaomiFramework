package com.miui.server;

import android.app.ActivityManager;
import android.app.ActivityManagerNative;
import android.app.AppGlobals;
import android.app.IMiuiProcessObserver;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.database.ContentObserver;
import android.graphics.Point;
import android.miui.R;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.os.UserHandle;
import android.provider.MiuiSettings;
import android.server.am.SplitScreenReporter;
import android.text.TextUtils;
import android.util.AtomicFile;
import android.util.Log;
import android.util.Slog;
import android.util.Xml;
import android.view.Display;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.util.FastXmlSerializer;
import com.android.server.MiuiFgThread;
import com.android.server.am.ExtraActivityManagerService;
import com.android.server.job.controllers.JobStatus;
import com.android.server.pm.PackageManagerService;
import com.android.server.pm.Settings;
import com.android.server.usb.descriptors.UsbTerminalTypes;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import miui.app.AlertDialog;
import miui.util.CustomizeUtil;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

public final class MiuiCompatModePackages {
    private static final String ATTR_CONFIG_NOTIFY_SUGGEST_APPS = "notifySuggestApps";
    private static final String MODULE_CUTOUT_MODE = "cutout_mode";
    private static final int MSG_DONT_SHOW_AGAIN = 105;
    private static final int MSG_ON_APP_LAUNCH = 104;
    private static final int MSG_READ = 101;
    private static final int MSG_REGISTER_OBSERVER = 102;
    private static final int MSG_UNREGISTER_OBSERVER = 103;
    private static final int MSG_UPDATE_CLOUD_DATA = 108;
    private static final int MSG_WRITE = 100;
    private static final int MSG_WRITE_CUTOUT_MODE = 107;
    private static final int MSG_WRITE_SPECIAL_MODE = 106;
    private static final String TAG = "MiuiCompatModePackages";
    private static final String TAG_NAME_CONFIG = "config";
    /* access modifiers changed from: private */
    public static final Uri URI_CLOUD_ALL_DATA_NOTIFY = Uri.parse("content://com.android.settings.cloud.CloudSettings/cloud_all_data/notify");
    /* access modifiers changed from: private */
    public AlertDialog mAlertDialog;
    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String pkg;
            String action = intent.getAction();
            Uri data = intent.getData();
            if (data != null && (pkg = data.getSchemeSpecificPart()) != null) {
                if ("android.intent.action.PACKAGE_ADDED".equals(action)) {
                    MiuiCompatModePackages.this.handleUpdatePackage(pkg);
                } else if ("android.intent.action.PACKAGE_REMOVED".equals(action) && !intent.getBooleanExtra("android.intent.extra.REPLACING", false)) {
                    MiuiCompatModePackages.this.handleRemovePackage(pkg);
                }
            }
        }
    };
    @GuardedBy({"mLock"})
    private final HashMap<String, Integer> mCloudCutoutModePackages = new HashMap<>();
    /* access modifiers changed from: private */
    public final ContentObserver mCloudDataObserver;
    /* access modifiers changed from: private */
    public final Context mContext;
    private final AtomicFile mCutoutModeFile;
    private float mDefaultAspect;
    @GuardedBy({"mLock"})
    private final HashMap<String, Integer> mDefaultType = new HashMap<>();
    private final AtomicFile mFile;
    /* access modifiers changed from: private */
    public final CompatHandler mHandler;
    private final Object mLock = new Object();
    private final Handler mMainHandler;
    @GuardedBy({"mLock"})
    private final HashMap<String, Integer> mNotchConfig = new HashMap<>();
    @GuardedBy({"mLock"})
    private final HashMap<String, Integer> mNotchSpecialModePackages = new HashMap<>();
    private boolean mNotifySuggestApps = true;
    @GuardedBy({"mLock"})
    private final HashMap<String, Integer> mPackages = new HashMap<>();
    private IMiuiProcessObserver mProcessObserver;
    private final HashSet<String> mRestrictList = new HashSet<>();
    private final AtomicFile mSpecialModeFile;
    private final HashSet<String> mSuggestList = new HashSet<>();
    private final HashSet<String> mSupportNotchList = new HashSet<>();
    @GuardedBy({"mLock"})
    private final HashMap<String, Integer> mUserCutoutModePackages = new HashMap<>();

    private final class CompatHandler extends Handler {
        public CompatHandler(Looper looper) {
            super(looper, (Handler.Callback) null, true);
        }

        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 100:
                    MiuiCompatModePackages.this.saveCompatModes();
                    return;
                case 101:
                    MiuiCompatModePackages.this.readCutoutModeConfig();
                    MiuiCompatModePackages.this.readSpecialModeConfig();
                    MiuiCompatModePackages.this.readPackagesConfig();
                    MiuiCompatModePackages.this.readSuggestApps();
                    return;
                case 102:
                    MiuiCompatModePackages.this.handleRegisterObservers();
                    return;
                case 103:
                    MiuiCompatModePackages.this.handleUnregisterObservers();
                    return;
                case 104:
                    MiuiCompatModePackages.this.handleOnAppLaunch((String) msg.obj);
                    return;
                case 105:
                    if (msg.obj != null && ((Boolean) msg.obj).booleanValue()) {
                        MiuiCompatModePackages.this.handleDontShowAgain();
                        return;
                    }
                    return;
                case 106:
                    MiuiCompatModePackages.this.saveSpecialModeFile();
                    return;
                case 107:
                    MiuiCompatModePackages.this.saveCutoutModeFile();
                    return;
                case 108:
                    MiuiCompatModePackages.this.updateCloudData();
                    return;
                default:
                    return;
            }
        }
    }

    public MiuiCompatModePackages(Context context) {
        this.mContext = context;
        this.mRestrictList.add("android.dpi.cts");
        if (Build.VERSION.SDK_INT < 28) {
            this.mSupportNotchList.add(PackageManagerService.PLATFORM_PACKAGE_NAME);
            this.mSupportNotchList.add(AccessController.PACKAGE_SYSTEMUI);
            this.mSupportNotchList.add("android.view.cts");
            this.mSupportNotchList.add("com.google.android.projection.gearhead");
            this.mSupportNotchList.add("com.google.android.apps.books");
            this.mSupportNotchList.add("com.subcast.radio.android.prod");
            this.mSupportNotchList.add("com.waze");
            this.mSupportNotchList.add("tunein.player");
            this.mSupportNotchList.add("com.google.android.apps.maps");
            this.mSupportNotchList.add("com.google.android.music");
            this.mSupportNotchList.add("com.stitcher.app");
            this.mSupportNotchList.add("org.npr.one");
            this.mSupportNotchList.add("com.gaana");
            this.mSupportNotchList.add("com.quanticapps.quranandroid");
            this.mSupportNotchList.add("com.itunestoppodcastplayer.app");
            if ("sirius".equals(Build.DEVICE) || "dipper".equals(Build.DEVICE) || "sakura".equals(Build.DEVICE)) {
                this.mSupportNotchList.add("com.tencent.tmgp.pubgmhdce");
            }
        }
        File systemDir = new File(Environment.getDataDirectory(), "system");
        this.mFile = new AtomicFile(new File(systemDir, "miui-packages-compat.xml"));
        this.mSpecialModeFile = new AtomicFile(new File(systemDir, "miui-specail-mode-v2.xml"));
        this.mCutoutModeFile = new AtomicFile(new File(systemDir, "cutout-mode.xml"));
        this.mHandler = new CompatHandler(MiuiFgThread.getHandler().getLooper());
        this.mHandler.sendEmptyMessage(101);
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.intent.action.PACKAGE_ADDED");
        filter.addAction("android.intent.action.PACKAGE_REMOVED");
        filter.addDataScheme(Settings.ATTR_PACKAGE);
        context.registerReceiverAsUser(this.mBroadcastReceiver, UserHandle.ALL, filter, (String) null, MiuiFgThread.getHandler());
        getDeviceAspect();
        this.mCloudDataObserver = new ContentObserver(this.mHandler) {
            public void onChange(boolean selfChange) {
                MiuiCompatModePackages.this.updateCloudDataAsync();
            }
        };
        this.mMainHandler = new Handler(Looper.getMainLooper());
        this.mMainHandler.post(new Runnable() {
            public void run() {
                MiuiCompatModePackages.this.mContext.getContentResolver().registerContentObserver(MiuiCompatModePackages.URI_CLOUD_ALL_DATA_NOTIFY, false, MiuiCompatModePackages.this.mCloudDataObserver, -1);
                MiuiCompatModePackages.this.mCloudDataObserver.onChange(false);
            }
        });
    }

    public void updateCloudDataAsync() {
        this.mHandler.removeMessages(108);
        this.mHandler.sendEmptyMessage(108);
    }

    /* Debug info: failed to restart local var, previous not found, register: 9 */
    /* access modifiers changed from: private */
    public void updateCloudData() {
        Log.d(TAG, "updateCloudData");
        synchronized (this.mLock) {
            this.mCloudCutoutModePackages.clear();
        }
        List<MiuiSettings.SettingsCloudData.CloudData> dataList = MiuiSettings.SettingsCloudData.getCloudDataList(this.mContext.getContentResolver(), MODULE_CUTOUT_MODE);
        if (dataList != null && dataList.size() != 0) {
            try {
                HashMap<String, Integer> pkgs = new HashMap<>();
                for (MiuiSettings.SettingsCloudData.CloudData data : dataList) {
                    String json = data.toString();
                    if (!TextUtils.isEmpty(json)) {
                        JSONObject jsonObject = new JSONObject(json);
                        String pkg = jsonObject.optString(SplitScreenReporter.STR_PKG);
                        int mode = jsonObject.optInt("mode");
                        if (!TextUtils.isEmpty(pkg)) {
                            pkgs.put(pkg, Integer.valueOf(mode));
                        }
                    }
                }
                synchronized (this.mLock) {
                    this.mCloudCutoutModePackages.putAll(pkgs);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /* Debug info: failed to restart local var, previous not found, register: 11 */
    /* access modifiers changed from: private */
    public void readSpecialModeConfig() {
        String pkg;
        FileInputStream fis = null;
        try {
            fis = this.mSpecialModeFile.openRead();
            XmlPullParser parser = Xml.newPullParser();
            parser.setInput(fis, StandardCharsets.UTF_8.name());
            int eventType = parser.getEventType();
            while (eventType != 2 && eventType != 1) {
                eventType = parser.next();
            }
            if (eventType != 1) {
                HashMap<String, Integer> pkgs = new HashMap<>();
                if ("special-mode".equals(parser.getName())) {
                    int eventType2 = parser.next();
                    do {
                        if (eventType2 == 2) {
                            String tagName = parser.getName();
                            if (parser.getDepth() == 2 && SplitScreenReporter.STR_PKG.equals(tagName) && (pkg = parser.getAttributeValue((String) null, Settings.ATTR_NAME)) != null) {
                                String mode = parser.getAttributeValue((String) null, "mode");
                                int modeInt = 0;
                                if (mode != null) {
                                    try {
                                        modeInt = Integer.parseInt(mode);
                                    } catch (NumberFormatException e) {
                                    }
                                }
                                pkgs.put(pkg, Integer.valueOf(modeInt));
                            }
                        }
                        eventType2 = parser.next();
                    } while (eventType2 != 1);
                }
                synchronized (this.mLock) {
                    this.mNotchSpecialModePackages.putAll(pkgs);
                }
                if (fis != null) {
                    try {
                        fis.close();
                    } catch (IOException e2) {
                    }
                }
            } else if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e3) {
                }
            }
        } catch (Exception e4) {
            try {
                Slog.w(TAG, "Error reading compat-packages", e4);
            } finally {
                if (fis != null) {
                    try {
                        fis.close();
                    } catch (IOException e5) {
                    }
                }
            }
        }
    }

    /* Debug info: failed to restart local var, previous not found, register: 11 */
    /* access modifiers changed from: private */
    public void readCutoutModeConfig() {
        String pkg;
        FileInputStream fis = null;
        try {
            fis = this.mCutoutModeFile.openRead();
            XmlPullParser parser = Xml.newPullParser();
            parser.setInput(fis, StandardCharsets.UTF_8.name());
            int eventType = parser.getEventType();
            while (eventType != 2 && eventType != 1) {
                eventType = parser.next();
            }
            if (eventType != 1) {
                HashMap<String, Integer> pkgs = new HashMap<>();
                if ("cutout-mode".equals(parser.getName())) {
                    int eventType2 = parser.next();
                    do {
                        if (eventType2 == 2) {
                            String tagName = parser.getName();
                            if (parser.getDepth() == 2 && SplitScreenReporter.STR_PKG.equals(tagName) && (pkg = parser.getAttributeValue((String) null, Settings.ATTR_NAME)) != null) {
                                String mode = parser.getAttributeValue((String) null, "mode");
                                if (mode != null) {
                                    try {
                                        pkgs.put(pkg, Integer.valueOf(Integer.parseInt(mode)));
                                    } catch (NumberFormatException e) {
                                    }
                                }
                            }
                        }
                        eventType2 = parser.next();
                    } while (eventType2 != 1);
                }
                synchronized (this.mLock) {
                    this.mUserCutoutModePackages.putAll(pkgs);
                }
                if (fis != null) {
                    try {
                        fis.close();
                    } catch (IOException e2) {
                    }
                }
            } else if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e3) {
                }
            }
        } catch (Exception e4) {
            try {
                Slog.w(TAG, "Error reading compat-packages", e4);
            } finally {
                if (fis != null) {
                    try {
                        fis.close();
                    } catch (IOException e5) {
                    }
                }
            }
        }
    }

    /* Debug info: failed to restart local var, previous not found, register: 11 */
    /* access modifiers changed from: private */
    public void readPackagesConfig() {
        FileInputStream fis = null;
        try {
            fis = this.mFile.openRead();
            XmlPullParser parser = Xml.newPullParser();
            parser.setInput(fis, StandardCharsets.UTF_8.name());
            int eventType = parser.getEventType();
            while (eventType != 2 && eventType != 1) {
                eventType = parser.next();
            }
            if (eventType != 1) {
                HashMap<String, Integer> pkgs = new HashMap<>();
                if ("compat-packages".equals(parser.getName())) {
                    int eventType2 = parser.next();
                    do {
                        if (eventType2 == 2) {
                            String tagName = parser.getName();
                            if (parser.getDepth() == 2) {
                                if (SplitScreenReporter.STR_PKG.equals(tagName)) {
                                    String pkg = parser.getAttributeValue((String) null, Settings.ATTR_NAME);
                                    if (pkg != null) {
                                        String mode = parser.getAttributeValue((String) null, "mode");
                                        int modeInt = 0;
                                        if (mode != null) {
                                            try {
                                                modeInt = Integer.parseInt(mode);
                                            } catch (NumberFormatException e) {
                                            }
                                        }
                                        pkgs.put(pkg, Integer.valueOf(modeInt));
                                    }
                                } else if (TAG_NAME_CONFIG.equals(tagName)) {
                                    this.mNotifySuggestApps = Boolean.valueOf(parser.getAttributeValue((String) null, ATTR_CONFIG_NOTIFY_SUGGEST_APPS)).booleanValue();
                                }
                            }
                        }
                        eventType2 = parser.next();
                    } while (eventType2 != 1);
                }
                synchronized (this.mLock) {
                    this.mPackages.putAll(pkgs);
                }
                if (fis != null) {
                    try {
                        fis.close();
                    } catch (IOException e2) {
                    }
                }
            } else if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e3) {
                }
            }
        } catch (Exception e4) {
            try {
                Slog.w(TAG, "Error reading compat-packages", e4);
            } finally {
                if (fis != null) {
                    try {
                        fis.close();
                    } catch (IOException e5) {
                    }
                }
            }
        }
    }

    /* access modifiers changed from: private */
    public void readSuggestApps() {
        Collections.addAll(this.mSuggestList, this.mContext.getResources().getStringArray(R.array.max_aspect_ratio_suggest_apps));
    }

    private float getPackageMode(String packageName) {
        Integer mode;
        synchronized (this.mLock) {
            mode = this.mPackages.get(packageName);
        }
        return (float) (mode != null ? mode.intValue() : getDefaultMode(packageName));
    }

    private int getSpecialMode(String packageName) {
        Integer mode;
        synchronized (this.mLock) {
            mode = this.mNotchSpecialModePackages.get(packageName);
        }
        if (mode != null) {
            return mode.intValue();
        }
        return 0;
    }

    private void scheduleWrite() {
        this.mHandler.removeMessages(100);
        this.mHandler.sendMessageDelayed(this.mHandler.obtainMessage(100), JobStatus.DEFAULT_TRIGGER_UPDATE_DELAY);
    }

    private void scheduleWriteSpecialMode() {
        this.mHandler.removeMessages(106);
        this.mHandler.sendMessageDelayed(this.mHandler.obtainMessage(106), JobStatus.DEFAULT_TRIGGER_UPDATE_DELAY);
    }

    private void scheduleWriteCutoutMode() {
        this.mHandler.removeMessages(107);
        this.mHandler.sendMessageDelayed(this.mHandler.obtainMessage(107), JobStatus.DEFAULT_TRIGGER_UPDATE_DELAY);
    }

    /* access modifiers changed from: package-private */
    public void saveCompatModes() {
        HashMap<String, Integer> pkgs = new HashMap<>();
        synchronized (this.mLock) {
            pkgs.putAll(this.mPackages);
        }
        FileOutputStream fos = null;
        try {
            FileOutputStream fos2 = this.mFile.startWrite();
            XmlSerializer out = new FastXmlSerializer();
            out.setOutput(fos2, StandardCharsets.UTF_8.name());
            out.startDocument((String) null, true);
            out.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);
            out.startTag((String) null, "compat-packages");
            out.startTag((String) null, TAG_NAME_CONFIG);
            out.attribute((String) null, ATTR_CONFIG_NOTIFY_SUGGEST_APPS, String.valueOf(this.mNotifySuggestApps));
            out.endTag((String) null, TAG_NAME_CONFIG);
            for (Map.Entry<String, Integer> entry : pkgs.entrySet()) {
                String pkg = entry.getKey();
                int mode = entry.getValue().intValue();
                if ((mode > 0) != isDefaultRestrict(pkg)) {
                    if (getDefaultAspectType(pkg) != 1) {
                        out.startTag((String) null, SplitScreenReporter.STR_PKG);
                        out.attribute((String) null, Settings.ATTR_NAME, pkg);
                        out.attribute((String) null, "mode", Integer.toString(mode));
                        out.endTag((String) null, SplitScreenReporter.STR_PKG);
                    }
                }
            }
            out.endTag((String) null, "compat-packages");
            out.endDocument();
            this.mFile.finishWrite(fos2);
            if (fos2 != null) {
                try {
                    fos2.close();
                } catch (IOException e) {
                }
            }
        } catch (Exception e1) {
            Slog.w(TAG, "Error writing compat packages", e1);
            if (fos != null) {
                this.mFile.failWrite(fos);
            }
            if (fos != null) {
                fos.close();
            }
        } catch (Throwable th) {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e2) {
                }
            }
            throw th;
        }
    }

    /* access modifiers changed from: package-private */
    public void saveSpecialModeFile() {
        HashMap<String, Integer> pkgs = new HashMap<>();
        synchronized (this.mLock) {
            pkgs.putAll(this.mNotchSpecialModePackages);
        }
        FileOutputStream fos = null;
        try {
            FileOutputStream fos2 = this.mSpecialModeFile.startWrite();
            XmlSerializer out = new FastXmlSerializer();
            out.setOutput(fos2, StandardCharsets.UTF_8.name());
            out.startDocument((String) null, true);
            out.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);
            out.startTag((String) null, "special-mode");
            for (Map.Entry<String, Integer> entry : pkgs.entrySet()) {
                String pkg = entry.getKey();
                int mode = entry.getValue().intValue();
                if (mode > 0) {
                    out.startTag((String) null, SplitScreenReporter.STR_PKG);
                    out.attribute((String) null, Settings.ATTR_NAME, pkg);
                    out.attribute((String) null, "mode", Integer.toString(mode));
                    out.endTag((String) null, SplitScreenReporter.STR_PKG);
                }
            }
            out.endTag((String) null, "special-mode");
            out.endDocument();
            this.mSpecialModeFile.finishWrite(fos2);
            if (fos2 != null) {
                try {
                    fos2.close();
                } catch (IOException e) {
                }
            }
        } catch (Exception e1) {
            Slog.w(TAG, "Error writing compat packages", e1);
            if (fos != null) {
                this.mSpecialModeFile.failWrite(fos);
            }
            if (fos != null) {
                fos.close();
            }
        } catch (Throwable th) {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e2) {
                }
            }
            throw th;
        }
    }

    /* access modifiers changed from: package-private */
    public void saveCutoutModeFile() {
        HashMap<String, Integer> pkgs = new HashMap<>();
        synchronized (this.mLock) {
            pkgs.putAll(this.mUserCutoutModePackages);
        }
        FileOutputStream fos = null;
        try {
            FileOutputStream fos2 = this.mCutoutModeFile.startWrite();
            XmlSerializer out = new FastXmlSerializer();
            out.setOutput(fos2, StandardCharsets.UTF_8.name());
            out.startDocument((String) null, true);
            out.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);
            out.startTag((String) null, "cutout-mode");
            for (Map.Entry<String, Integer> entry : pkgs.entrySet()) {
                int mode = entry.getValue().intValue();
                out.startTag((String) null, SplitScreenReporter.STR_PKG);
                out.attribute((String) null, Settings.ATTR_NAME, entry.getKey());
                out.attribute((String) null, "mode", Integer.toString(mode));
                out.endTag((String) null, SplitScreenReporter.STR_PKG);
            }
            out.endTag((String) null, "cutout-mode");
            out.endDocument();
            this.mCutoutModeFile.finishWrite(fos2);
            if (fos2 != null) {
                try {
                    fos2.close();
                } catch (IOException e) {
                }
            }
        } catch (Exception e1) {
            Slog.w(TAG, "Error writing cutout packages", e1);
            if (fos != null) {
                this.mCutoutModeFile.failWrite(fos);
            }
            if (fos != null) {
                fos.close();
            }
        } catch (Throwable th) {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e2) {
                }
            }
            throw th;
        }
    }

    private boolean isDefaultRestrict(String pkg) {
        int type = getDefaultAspectType(pkg);
        return type == 4 || type == 5;
    }

    private float getDeviceAspect() {
        float ratio = 0.0f;
        if (this.mDefaultAspect <= 0.0f) {
            Display display = this.mContext.getDisplay();
            Point point = new Point();
            display.getRealSize(point);
            int min = Math.min(point.x, point.y);
            int max = Math.max(point.x, point.y);
            if (min != 0) {
                ratio = (((float) max) * 1.0f) / ((float) min);
            }
            this.mDefaultAspect = ratio;
        }
        return this.mDefaultAspect;
    }

    private int getDefaultMode(String pkg) {
        return isDefaultRestrict(pkg) ? 1 : 0;
    }

    private void removePackage(String packageName) {
        boolean realRemove = false;
        synchronized (this.mLock) {
            this.mDefaultType.remove(packageName);
            if (this.mPackages.containsKey(packageName)) {
                this.mPackages.remove(packageName);
                realRemove = true;
            }
        }
        if (realRemove) {
            scheduleWrite();
        }
    }

    private void removeSpecialModePackage(String packageName) {
        boolean realRemove = false;
        synchronized (this.mLock) {
            this.mNotchConfig.remove(packageName);
            if (this.mNotchSpecialModePackages.containsKey(packageName)) {
                this.mNotchSpecialModePackages.remove(packageName);
                realRemove = true;
            }
        }
        if (realRemove) {
            scheduleWriteSpecialMode();
        }
    }

    /* access modifiers changed from: private */
    public void handleRemovePackage(String packageName) {
        removePackage(packageName);
        removeSpecialModePackage(packageName);
    }

    /* access modifiers changed from: private */
    public void handleUpdatePackage(String packageName) {
        synchronized (this.mLock) {
            this.mDefaultType.remove(packageName);
            this.mNotchConfig.remove(packageName);
        }
        if (isDefaultRestrict(packageName) == isRestrictAspect(packageName) || getDefaultAspectType(packageName) == 1) {
            Slog.i(TAG, "package " + packageName + " updated, removing config");
            removePackage(packageName);
        }
    }

    public float getAspectRatio(String pkg) {
        if (isRestrictAspect(pkg)) {
            return CustomizeUtil.RESTRICT_ASPECT_RATIO;
        }
        return 3.0f;
    }

    public int getNotchConfig(String packageName) {
        Integer mode;
        int config = 0;
        synchronized (this.mLock) {
            if (this.mNotchSpecialModePackages.containsKey(packageName) && (mode = this.mNotchSpecialModePackages.get(packageName)) != null) {
                config = mode.intValue() != 0 ? 128 : 0;
            }
        }
        return config | getDefaultNotchConfig(packageName);
    }

    private int getDefaultNotchConfig(String packageName) {
        synchronized (this.mLock) {
            if (this.mNotchConfig.containsKey(packageName)) {
                int intValue = this.mNotchConfig.get(packageName).intValue();
                return intValue;
            }
            int type = resolveNotchConfig(packageName);
            synchronized (this.mLock) {
                this.mNotchConfig.put(packageName, Integer.valueOf(type));
            }
            return type;
        }
    }

    private int resolveNotchConfig(String packageName) {
        Bundle metadata;
        if (this.mSupportNotchList.contains(packageName)) {
            return UsbTerminalTypes.TERMINAL_EMBED_UNDEFINED;
        }
        ApplicationInfo ai = null;
        try {
            ai = AppGlobals.getPackageManager().getApplicationInfo(packageName, 128, 0);
        } catch (RemoteException e) {
        }
        if (ai == null || (metadata = ai.metaData) == null) {
            return 0;
        }
        String notch = metadata.getString("notch.config");
        if (TextUtils.isEmpty(notch)) {
            return 0;
        }
        int config = 0 | 256;
        if (notch.contains("portrait")) {
            config |= 512;
        }
        if (notch.contains("landscape")) {
            return config | 1024;
        }
        return config;
    }

    public void setNotchSpecialMode(String pkg, boolean special) {
        if (special != isNotchSpecailMode(pkg)) {
            synchronized (this.mLock) {
                this.mNotchSpecialModePackages.put(pkg, Integer.valueOf(special ? 1 : 0));
            }
            scheduleWriteSpecialMode();
            ((ActivityManager) this.mContext.getSystemService("activity")).forceStopPackage(pkg);
        }
    }

    private boolean isNotchSpecailMode(String pkg) {
        return getSpecialMode(pkg) != 0;
    }

    public void setCutoutMode(String pkg, int mode) {
        synchronized (this.mLock) {
            this.mUserCutoutModePackages.put(pkg, Integer.valueOf(mode));
        }
        scheduleWriteCutoutMode();
        ((ActivityManager) this.mContext.getSystemService("activity")).forceStopPackage(pkg);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:10:0x001c, code lost:
        monitor-enter(r1);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:13:0x0023, code lost:
        if (r4.mCloudCutoutModePackages.containsKey(r5) == false) goto L_0x0033;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:14:0x0025, code lost:
        r0 = r4.mCloudCutoutModePackages.get(r5).intValue();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:15:0x0031, code lost:
        monitor-exit(r1);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:16:0x0032, code lost:
        return r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:17:0x0033, code lost:
        monitor-exit(r1);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:19:0x003d, code lost:
        if ((getDefaultNotchConfig(r5) & com.android.server.usb.descriptors.UsbTerminalTypes.TERMINAL_EMBED_UNDEFINED) != 1792) goto L_?;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:20:0x003f, code lost:
        return 1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:35:?, code lost:
        return 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:9:0x001a, code lost:
        r1 = r4.mLock;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public int getCutoutMode(java.lang.String r5) {
        /*
            r4 = this;
            java.lang.Object r0 = r4.mLock
            monitor-enter(r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r1 = r4.mUserCutoutModePackages     // Catch:{ all -> 0x0044 }
            boolean r1 = r1.containsKey(r5)     // Catch:{ all -> 0x0044 }
            if (r1 == 0) goto L_0x0019
            java.util.HashMap<java.lang.String, java.lang.Integer> r1 = r4.mUserCutoutModePackages     // Catch:{ all -> 0x0044 }
            java.lang.Object r1 = r1.get(r5)     // Catch:{ all -> 0x0044 }
            java.lang.Integer r1 = (java.lang.Integer) r1     // Catch:{ all -> 0x0044 }
            int r1 = r1.intValue()     // Catch:{ all -> 0x0044 }
            monitor-exit(r0)     // Catch:{ all -> 0x0044 }
            return r1
        L_0x0019:
            monitor-exit(r0)     // Catch:{ all -> 0x0044 }
            java.lang.Object r1 = r4.mLock
            monitor-enter(r1)
            java.util.HashMap<java.lang.String, java.lang.Integer> r0 = r4.mCloudCutoutModePackages     // Catch:{ all -> 0x0041 }
            boolean r0 = r0.containsKey(r5)     // Catch:{ all -> 0x0041 }
            if (r0 == 0) goto L_0x0033
            java.util.HashMap<java.lang.String, java.lang.Integer> r0 = r4.mCloudCutoutModePackages     // Catch:{ all -> 0x0041 }
            java.lang.Object r0 = r0.get(r5)     // Catch:{ all -> 0x0041 }
            java.lang.Integer r0 = (java.lang.Integer) r0     // Catch:{ all -> 0x0041 }
            int r0 = r0.intValue()     // Catch:{ all -> 0x0041 }
            monitor-exit(r1)     // Catch:{ all -> 0x0041 }
            return r0
        L_0x0033:
            monitor-exit(r1)     // Catch:{ all -> 0x0041 }
            int r0 = r4.getDefaultNotchConfig(r5)
            r1 = 1792(0x700, float:2.511E-42)
            r2 = 0
            r3 = r0 & r1
            if (r3 != r1) goto L_0x0040
            r2 = 1
        L_0x0040:
            return r2
        L_0x0041:
            r0 = move-exception
            monitor-exit(r1)     // Catch:{ all -> 0x0041 }
            throw r0
        L_0x0044:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x0044 }
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.server.MiuiCompatModePackages.getCutoutMode(java.lang.String):int");
    }

    public int getDefaultAspectType(String packageName) {
        synchronized (this.mLock) {
            if (this.mDefaultType.containsKey(packageName)) {
                int intValue = this.mDefaultType.get(packageName).intValue();
                return intValue;
            }
            int type = resolveDefaultAspectType(packageName);
            synchronized (this.mLock) {
                this.mDefaultType.put(packageName, Integer.valueOf(type));
            }
            return type;
        }
    }

    private int resolveDefaultAspectType(String packageName) {
        if (this.mRestrictList.contains(packageName)) {
            return 4;
        }
        ApplicationInfo ai = null;
        try {
            ai = AppGlobals.getPackageManager().getApplicationInfo(packageName, 128, 0);
        } catch (RemoteException e) {
        }
        if (ai == null) {
            return 0;
        }
        Bundle metadata = ai.metaData;
        float aspect = 0.0f;
        if (metadata != null) {
            aspect = metadata.getFloat("android.max_aspect");
        }
        if (aspect >= getDeviceAspect()) {
            return 1;
        }
        if (this.mSuggestList.contains(packageName)) {
            return 3;
        }
        return 5;
    }

    public boolean isRestrictAspect(String packageName) {
        return getPackageMode(packageName) != 0.0f;
    }

    public void setRestrictAspect(String pkg, boolean restrict) {
        if (restrict != isRestrictAspect(pkg)) {
            synchronized (this.mLock) {
                this.mPackages.put(pkg, Integer.valueOf(restrict ? 1 : 0));
            }
            scheduleWrite();
            ((ActivityManager) this.mContext.getSystemService("activity")).forceStopPackage(pkg);
        }
    }

    /* access modifiers changed from: private */
    public void handleRegisterObservers() {
        if (this.mNotifySuggestApps) {
            this.mProcessObserver = new AppLaunchObserver() {
                /* access modifiers changed from: protected */
                public void onFirstLaunch(String packageName) {
                    MiuiCompatModePackages.this.mHandler.removeMessages(104);
                    MiuiCompatModePackages.this.mHandler.sendMessageDelayed(Message.obtain(MiuiCompatModePackages.this.mHandler, 104, packageName), 500);
                }
            };
            try {
                Slog.i(TAG, "registering process observer...");
                ActivityManagerNative.getDefault().registerProcessObserver(this.mProcessObserver);
            } catch (RemoteException e) {
                this.mProcessObserver = null;
                Slog.e(TAG, "error when registering process observer", e);
            }
        }
    }

    /* access modifiers changed from: private */
    public void handleUnregisterObservers() {
        if (this.mProcessObserver != null) {
            Slog.i(TAG, "unregistering process observer...");
            try {
                ActivityManagerNative.getDefault().unregisterProcessObserver(this.mProcessObserver);
            } catch (RemoteException e) {
                Slog.e(TAG, "error when unregistering process observer", e);
            } catch (Throwable th) {
                this.mProcessObserver = null;
                throw th;
            }
            this.mProcessObserver = null;
        }
    }

    /* access modifiers changed from: private */
    public void handleOnAppLaunch(String packageName) {
        if (!isRestrictAspect(packageName) && getDefaultAspectType(packageName) == 3) {
            try {
                Slog.i(TAG, "launching suggest app: " + packageName);
                if (this.mAlertDialog == null) {
                    createDialog();
                }
                this.mAlertDialog.show();
            } catch (Exception e) {
                Slog.e(TAG, "error showing suggest dialog", e);
            }
        }
    }

    private void createDialog() {
        this.mAlertDialog = new AlertDialog.Builder(this.mContext).setTitle(R.string.miui_screen_ratio_hint).setMessage(R.string.miui_screen_ratio_hint_message).setCheckBox(true, this.mContext.getResources().getString(R.string.miui_screen_ratio_hint_dont_show_again)).setPositiveButton(R.string.miui_screen_ratio_hint_ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                if (MiuiCompatModePackages.this.mAlertDialog != null) {
                    Message.obtain(MiuiCompatModePackages.this.mHandler, 105, Boolean.valueOf(MiuiCompatModePackages.this.mAlertDialog.isChecked())).sendToTarget();
                }
            }
        }).setNegativeButton(R.string.miui_screen_ratio_hint_go_to_settings, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                if (MiuiCompatModePackages.this.mAlertDialog != null) {
                    Message.obtain(MiuiCompatModePackages.this.mHandler, 105, Boolean.valueOf(MiuiCompatModePackages.this.mAlertDialog.isChecked())).sendToTarget();
                    MiuiCompatModePackages.this.gotoMaxAspectSettings();
                }
            }
        }).create();
        this.mAlertDialog.setCanceledOnTouchOutside(false);
        this.mAlertDialog.getWindow().getAttributes().type = 2008;
        this.mAlertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            public void onDismiss(DialogInterface dialog) {
                AlertDialog unused = MiuiCompatModePackages.this.mAlertDialog = null;
            }
        });
    }

    /* access modifiers changed from: private */
    public void handleDontShowAgain() {
        this.mNotifySuggestApps = false;
        this.mHandler.sendEmptyMessage(103);
        this.mHandler.sendEmptyMessage(100);
    }

    /* access modifiers changed from: private */
    public void gotoMaxAspectSettings() {
        try {
            Intent intent = new Intent();
            intent.setClassName("com.android.settings", "com.android.settings.SubSettings");
            intent.putExtra(":settings:show_fragment", "com.android.settings.MaxAspectRatioSettings");
            intent.addFlags(268435456);
            this.mContext.startActivityAsUser(intent, UserHandle.CURRENT);
        } catch (Exception e) {
            Slog.e(TAG, "error when goto max aspect settings", e);
        }
    }

    private static abstract class AppLaunchObserver extends IMiuiProcessObserver {
        private HashSet<Integer> mRunningFgActivityProcesses;

        /* access modifiers changed from: protected */
        public abstract void onFirstLaunch(String str);

        private AppLaunchObserver() {
            this.mRunningFgActivityProcesses = new HashSet<>();
        }

        public void onForegroundActivitiesChanged(int pid, int uid, boolean foregroundActivities) {
            if (foregroundActivities && !this.mRunningFgActivityProcesses.contains(Integer.valueOf(pid))) {
                this.mRunningFgActivityProcesses.add(Integer.valueOf(pid));
                onFirstLaunch(ExtraActivityManagerService.getPackageNameByPid(pid));
            }
        }

        public void onForegroundServicesChanged(int pid, int uid, int serviceTypes) {
        }

        public void onImportanceChanged(int pid, int uid, int importance) {
        }

        public void onProcessDied(int pid, int uid) {
            this.mRunningFgActivityProcesses.remove(Integer.valueOf(pid));
        }

        public void onProcessStateChanged(int pid, int uid, int procState) {
        }
    }
}
