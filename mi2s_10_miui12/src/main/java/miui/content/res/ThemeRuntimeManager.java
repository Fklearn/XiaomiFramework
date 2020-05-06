package miui.content.res;

import android.app.WallpaperManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.MiuiConfiguration;
import android.graphics.Bitmap;
import android.media.ExtraRingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Process;
import android.provider.Settings;
import android.util.Log;
import android.util.Pair;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;
import miui.app.constants.ThemeManagerConstants;
import miui.drm.DrmManager;
import miui.os.FileUtils;
import miui.reflect.Method;
import miui.system.R;

public class ThemeRuntimeManager {
    public static final String BUILTIN_ALARM_PATH = "/system/media/audio/alarms/";
    public static final String BUILTIN_LOCKSCREEN_PATH = "/system/media/lockscreen/";
    public static final String BUILTIN_NOTIFICATION_PATH = "/system/media/audio/notifications/";
    public static final String BUILTIN_RINGTONE_PATH = "/system/media/audio/ringtones/";
    public static final String BUILTIN_ROOT_PATH = "/system/media/";
    public static final String BUILTIN_WALLPAPER_PATH = "/system/media/wallpaper/";
    public static final int DEFAULT_ALARM_FILE_PATH_RES_ID = R.string.def_alarm_alert;
    public static final int DEFAULT_NOTIFICATION_FILE_PATH_RES_ID = R.string.def_notification_sound;
    public static final int DEFAULT_RINGTONE_FILE_PATH_RES_ID = R.string.def_ringtone;
    public static final int DEFAULT_SMS_DELIVERED_SOUND_FILE_PATH_RES_ID = R.string.def_sms_delivered_sound;
    public static final int DEFAULT_SMS_RECEIVED_SOUND_FILE_PATH_RES_ID = R.string.def_sms_received_sound;
    public static final String RUNTIME_PATH_BOOT_ANIMATION = (Build.VERSION.SDK_INT > 19 ? "/data/system/theme/boots/bootanimation.zip" : "/data/local/bootanimation.zip");
    public static final String RUNTIME_PATH_LOCKSCREEN = "/data/system/theme/lock_wallpaper";
    public static final String RUNTIME_PATH_WALLPAPER = "/data/system/theme/wallpaper";
    public static final String RUNTIME_PIC_FOLDER = "/data/system/theme/";
    private static final int SAVE_ICON_MAX_SIZE = 163840;
    private static final String TAG = "ThemeRuntimeManager";
    private static final String TEMP_ICON_FOLDER = (ThemeResources.THEME_MAGIC_PATH + "tempIcon/");
    private static final String THEME_PACKAGE_NAME = "com.android.thememanager";
    private static Set<String> sWhiteList = new HashSet();
    private Context mContext;
    /* access modifiers changed from: private */
    public byte[] mJobLocker = new byte[0];
    /* access modifiers changed from: private */
    public Stack<Pair<String, Bitmap>> mPendingJobs = new Stack<>();
    /* access modifiers changed from: private */
    public Object mSecurityManager;
    /* access modifiers changed from: private */
    public byte[] mServiceLocker = new byte[0];
    /* access modifiers changed from: private */
    public boolean mThreadFinished = true;

    public ThemeRuntimeManager(Context context) {
        this.mContext = context;
    }

    public static String createTempIconFile(Context context, String fileName, Bitmap icon) {
        String path = null;
        FileOutputStream outputStream = null;
        try {
            if (!ThemeResources.FRAMEWORK_PACKAGE.equals(context.getPackageName())) {
                path = context.getCacheDir() + "/" + fileName;
                outputStream = getFileOutputStream(path);
            }
            if (outputStream == null) {
                new File(TEMP_ICON_FOLDER).mkdirs();
                path = TEMP_ICON_FOLDER + fileName;
                outputStream = getFileOutputStream(path);
            }
            if (outputStream == null) {
                Log.e(TAG, "can't get icon cache folder");
                return null;
            }
            icon.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
            outputStream.flush();
            outputStream.close();
            if (new File(path).exists()) {
                return path;
            }
            return null;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e2) {
            e2.printStackTrace();
            return null;
        }
    }

    private static FileOutputStream getFileOutputStream(String path) {
        File file = new File(path);
        FileOutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(file);
            FileUtils.chmod(file.getPath(), 436);
            return outputStream;
        } catch (FileNotFoundException e) {
            return outputStream;
        }
    }

    /* access modifiers changed from: private */
    public void bindService() {
        synchronized (this.mServiceLocker) {
            this.mSecurityManager = this.mContext.getSystemService("security");
        }
        if (this.mSecurityManager == null) {
            Log.e(TAG, "can't bind SecurityManager");
        }
    }

    private boolean existIntentService(Intent intent) {
        List<ResolveInfo> list = this.mContext.getPackageManager().queryIntentServices(intent, 0);
        if (list == null || list.isEmpty()) {
            return false;
        }
        return true;
    }

    /* access modifiers changed from: private */
    public void unbindService() {
        synchronized (this.mServiceLocker) {
            this.mSecurityManager = null;
        }
    }

    private class ThemeServiceThread extends Thread {
        private ThemeServiceThread() {
        }

        /* Debug info: failed to restart local var, previous not found, register: 6 */
        public void run() {
            loop0:
            while (true) {
                ThemeRuntimeManager.this.bindService();
                synchronized (ThemeRuntimeManager.this.mJobLocker) {
                    while (true) {
                        if (!ThemeRuntimeManager.this.mPendingJobs.isEmpty()) {
                            synchronized (ThemeRuntimeManager.this.mServiceLocker) {
                                if (ThemeRuntimeManager.this.mSecurityManager != null) {
                                    Pair<String, Bitmap> pair = (Pair) ThemeRuntimeManager.this.mPendingJobs.pop();
                                    ThemeRuntimeManager.this.saveIconInner((String) pair.first, (Bitmap) pair.second);
                                }
                            }
                        }
                    }
                    try {
                        ThemeRuntimeManager.this.mJobLocker.wait(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (ThemeRuntimeManager.this.mPendingJobs.isEmpty()) {
                        ThemeRuntimeManager.this.unbindService();
                        boolean unused = ThemeRuntimeManager.this.mThreadFinished = true;
                        return;
                    }
                }
            }
        }
    }

    /* access modifiers changed from: private */
    public void saveIconInner(String fileName, Bitmap icon) {
        Log.i(TAG, "saving icon for " + fileName);
        Method method = Method.of(this.mSecurityManager.getClass(), "saveIcon", Void.TYPE, new Class[]{String.class, Bitmap.class});
        if (method != null) {
            method.invoke(this.mSecurityManager.getClass(), this.mSecurityManager, new Object[]{fileName, icon});
        }
    }

    public void saveIcon(String fileName, Bitmap icon) {
        ApplicationInfo appInfo;
        try {
            if (Process.myUid() != 1000 && ((appInfo = this.mContext.getPackageManager().getApplicationInfo(this.mContext.getPackageName(), 0)) == null || (appInfo.flags & 1) != 1)) {
                return;
            }
            if (icon == null || icon.getByteCount() <= SAVE_ICON_MAX_SIZE) {
                synchronized (this.mJobLocker) {
                    Log.i(TAG, "add pending job " + fileName);
                    this.mPendingJobs.push(new Pair(fileName, icon));
                    this.mJobLocker.notifyAll();
                    if (this.mThreadFinished) {
                        this.mThreadFinished = false;
                        new ThemeServiceThread().start();
                    }
                }
                return;
            }
            Log.d(TAG, "saveIcon fail because icon bitmap is too large " + fileName);
        } catch (PackageManager.NameNotFoundException e) {
            Log.w(TAG, "fail to find package: " + this.mContext.getPackageName(), e);
        }
    }

    public void markGadgetUpdated() {
        long time = System.currentTimeMillis();
        ContentResolver contentResolver = this.mContext.getContentResolver();
        Settings.System.putLong(contentResolver, "clock_changed_time_" + ThemeManagerConstants.GADGET_SIZE_1x2, time);
        ContentResolver contentResolver2 = this.mContext.getContentResolver();
        Settings.System.putLong(contentResolver2, "clock_changed_time_" + ThemeManagerConstants.GADGET_SIZE_2x2, time);
        ContentResolver contentResolver3 = this.mContext.getContentResolver();
        Settings.System.putLong(contentResolver3, "clock_changed_time_" + ThemeManagerConstants.GADGET_SIZE_2x4, time);
        ContentResolver contentResolver4 = this.mContext.getContentResolver();
        Settings.System.putLong(contentResolver4, "clock_changed_time_" + ThemeManagerConstants.GADGET_SIZE_4x4, time);
        ContentResolver contentResolver5 = this.mContext.getContentResolver();
        Settings.System.putLong(contentResolver5, "clock_changed_time_" + ThemeManagerConstants.GADGET_SIZE_3x4, time);
    }

    public void restoreDefault() {
        File folder = new File("/data/system/theme/");
        if (folder.exists() && folder.isDirectory()) {
            for (File file : folder.listFiles()) {
                if (!sWhiteList.contains(file.getAbsolutePath())) {
                    ThemeNativeUtils.remove(file.getAbsolutePath());
                }
            }
        }
        ThemeNativeUtils.remove(RUNTIME_PATH_BOOT_ANIMATION);
        Intent intent = new Intent(ThemeManagerConstants.ACTION_CLEAR_THEME_RUNTIME_DATA);
        intent.setPackage(THEME_PACKAGE_NAME);
        this.mContext.sendBroadcast(intent);
        try {
            ((WallpaperManager) this.mContext.getSystemService("wallpaper")).clear();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (isRestoreIndependentComponents()) {
            Context context = this.mContext;
            ExtraRingtoneManager.saveDefaultSound(context, 1, Uri.fromFile(new File(context.getString(DEFAULT_RINGTONE_FILE_PATH_RES_ID))));
            Context context2 = this.mContext;
            ExtraRingtoneManager.saveDefaultSound(context2, 2, Uri.fromFile(new File(context2.getString(DEFAULT_NOTIFICATION_FILE_PATH_RES_ID))));
            Context context3 = this.mContext;
            ExtraRingtoneManager.saveDefaultSound(context3, 4, Uri.fromFile(new File(context3.getString(DEFAULT_ALARM_FILE_PATH_RES_ID))));
            Context context4 = this.mContext;
            ExtraRingtoneManager.saveDefaultSound(context4, 8, Uri.fromFile(new File(context4.getString(DEFAULT_SMS_DELIVERED_SOUND_FILE_PATH_RES_ID))));
            Context context5 = this.mContext;
            ExtraRingtoneManager.saveDefaultSound(context5, 16, Uri.fromFile(new File(context5.getString(DEFAULT_SMS_RECEIVED_SOUND_FILE_PATH_RES_ID))));
        }
        IconCustomizer.clearCustomizedIcons((String) null);
        ThemeResources.getSystem().resetIcons();
        markGadgetUpdated();
        DrmManager.setSupportAd(this.mContext, false);
        MiuiConfiguration.sendThemeConfigurationChangeMsg(MiuiConfiguration.SYSTEM_INTEREST_CHANGE_FLAG);
    }

    private boolean isRestoreIndependentComponents() {
        return false;
    }
}
