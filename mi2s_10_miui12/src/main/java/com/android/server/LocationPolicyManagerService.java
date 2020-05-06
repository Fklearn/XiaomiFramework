package com.android.server;

import android.app.INotificationManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.UserInfo;
import android.content.res.Resources;
import android.location.ILocationManager;
import android.location.ILocationPolicyListener;
import android.location.ILocationPolicyManager;
import android.location.Location;
import android.location.LocationPolicy;
import android.location.LocationPolicyManager;
import android.miui.Manifest;
import android.miui.R;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.os.PowerManager;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.os.UserHandle;
import android.os.UserManager;
import android.provider.Settings;
import android.util.AtomicFile;
import android.util.Slog;
import android.util.SparseBooleanArray;
import android.util.SparseIntArray;
import android.util.Xml;
import com.android.internal.app.IUidStateChangeCallback;
import com.android.internal.util.ArrayUtils;
import com.android.internal.util.FastXmlSerializer;
import com.android.internal.util.XmlUtils;
import com.android.server.location.LocationOpHandler;
import com.android.server.power.PowerManagerServiceInjector;
import com.google.android.collect.Lists;
import com.google.android.collect.Maps;
import com.google.android.collect.Sets;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import libcore.io.IoUtils;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

public class LocationPolicyManagerService extends ILocationPolicyManager.Stub {
    private static final String ACTION_ALLOW_BACKGROUND = "com.android.server.location.action.ALLOW_BACKGROUND";
    private static final String ATTR_APP_ID = "appId";
    private static final String ATTR_HIGH_POWER = "high-power";
    private static final String ATTR_LOCATION_PROVIDER = "provider";
    private static final String ATTR_MIN_INTERVAL = "minIntervalMs";
    private static final String ATTR_POLICY = "policy";
    private static final String ATTR_RESTRICT_BACKGROUND = "restrictBackground";
    private static final String ATTR_UID = "uid";
    private static final String ATTR_VERSION = "version";
    private static final boolean LOGD = true;
    private static final boolean LOGV = true;
    private static final int MSG_RESTRICT_BACKGROUND_CHANGED = 2;
    private static final int MSG_RULES_CHANGED = 1;
    private static final String TAG = "LocationPolicy";
    private static final String TAG_ALLOW_BACKGROUND = "LocationPolicy:allowBackground";
    private static final String TAG_APP_POLICY = "app-policy";
    private static final String TAG_LOCATION_POLICY = "location-policy";
    private static final String TAG_POLICY_LIST = "policy-list";
    private static final String TAG_UID_POLICY = "uid-policy";
    private static final int VERSION_INIT = 1;
    private static final int VERSION_LATEST = 1;
    static LocationPolicyManagerService sLocationPolicyService;
    private String DEVICE_IDLE_CHANGE;
    private HashSet<String> mActiveNotifs = Sets.newHashSet();
    private BroadcastReceiver mAllowReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            Slog.v(LocationPolicyManagerService.TAG, "user cancel restrict all background notification");
            LocationPolicyManagerService.this.setRestrictBackground(false);
        }
    };
    private final Context mContext;
    /* access modifiers changed from: private */
    public volatile boolean mDeviceIdle;
    private BroadcastReceiver mDeviceIdleChangeReceiver = new BroadcastReceiver() {
        /* Debug info: failed to restart local var, previous not found, register: 6 */
        public void onReceive(Context context, Intent intent) {
            try {
                synchronized (LocationPolicyManagerService.this.mRulesLock) {
                    boolean unused = LocationPolicyManagerService.this.mDeviceIdle = ((Boolean) PowerManager.class.getDeclaredMethod("isDeviceIdleMode", new Class[0]).invoke(LocationPolicyManagerService.this.mPowerManager, new Object[0])).booleanValue();
                    Slog.d(LocationPolicyManagerService.TAG, "DeviceIdleMode changed to " + LocationPolicyManagerService.this.mDeviceIdle);
                    LocationPolicyManagerService.this.updateRulesForDeviceIdleLocked();
                }
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    };
    private FakeGpsStrategy mFakeGpsStrategy;
    private final Handler mHandler;
    private Handler.Callback mHandlerCallback = new Handler.Callback() {
        public boolean handleMessage(Message msg) {
            int i = msg.what;
            if (i != 1) {
                boolean z = false;
                if (i != 2) {
                    return false;
                }
                if (msg.arg1 != 0) {
                    z = true;
                }
                boolean restrictBackground = z;
                int length = LocationPolicyManagerService.this.mListeners.beginBroadcast();
                for (int i2 = 0; i2 < length; i2++) {
                    ILocationPolicyListener listener = LocationPolicyManagerService.this.mListeners.getBroadcastItem(i2);
                    if (listener != null) {
                        try {
                            listener.onRestrictBackgroundChanged(restrictBackground);
                        } catch (RemoteException e) {
                        }
                    }
                }
                LocationPolicyManagerService.this.mListeners.finishBroadcast();
                return true;
            }
            int uid = msg.arg1;
            int uidRules = msg.arg2;
            int length2 = LocationPolicyManagerService.this.mListeners.beginBroadcast();
            for (int i3 = 0; i3 < length2; i3++) {
                ILocationPolicyListener listener2 = LocationPolicyManagerService.this.mListeners.getBroadcastItem(i3);
                if (listener2 != null) {
                    try {
                        listener2.onUidRulesChanged(uid, uidRules);
                    } catch (RemoteException e2) {
                    }
                }
            }
            LocationPolicyManagerService.this.mListeners.finishBroadcast();
            return true;
        }
    };
    private final HandlerThread mHandlerThread;
    /* access modifiers changed from: private */
    public final RemoteCallbackList<ILocationPolicyListener> mListeners = new RemoteCallbackList<>();
    private final ILocationManager mLocationManager;
    /* access modifiers changed from: private */
    public volatile int mLocationMode;
    private BroadcastReceiver mLocationModeReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            Slog.d(LocationPolicyManagerService.TAG, "location mode changed");
            ContentResolver cr = context.getContentResolver();
            synchronized (LocationPolicyManagerService.this.mRulesLock) {
                int unused = LocationPolicyManagerService.this.mLocationMode = Settings.Secure.getInt(cr, "location_mode", 0);
                LocationPolicyManagerService.this.updateLocationModeChangeLocked();
                LocationPolicyManagerService.this.updateLocationRulesLocked();
                LocationPolicyManagerService.this.updateNotificationsLocked();
            }
        }
    };
    private final LocationOpHandler mLocationOpHandler;
    private HashMap<String, LocationPolicy> mLocationPolicies = Maps.newHashMap();
    private HashMap<LocationPolicy, String[]> mLocationRules = Maps.newHashMap();
    private final INotificationManager mNotifManager;
    private BroadcastReceiver mPackageReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            int uid = intent.getIntExtra("android.intent.extra.UID", -1);
            if (uid != -1 && "android.intent.action.PACKAGE_ADDED".equals(action)) {
                Slog.v(LocationPolicyManagerService.TAG, "ACTION_PACKAGE_ADDED for uid=" + uid);
                synchronized (LocationPolicyManagerService.this.mRulesLock) {
                    LocationPolicyManagerService.this.updateRulesForUidLocked(uid);
                }
            }
        }
    };
    private final AtomicFile mPolicyFile;
    /* access modifiers changed from: private */
    public PowerManager mPowerManager;
    private volatile boolean mRestrictBackground;
    /* access modifiers changed from: private */
    public final Object mRulesLock = new Object();
    /* access modifiers changed from: private */
    public volatile boolean mScreenOn;
    private BroadcastReceiver mScreenReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            Slog.v(LocationPolicyManagerService.TAG, "Screen state changed");
            synchronized (LocationPolicyManagerService.this.mRulesLock) {
                boolean unused = LocationPolicyManagerService.this.mScreenOn = LocationPolicyManagerService.this.isScreenOn();
                LocationPolicyManagerService.this.updateRulesForScreenLocked();
            }
        }
    };
    private SparseBooleanArray mUidInNavigation = new SparseBooleanArray();
    /* access modifiers changed from: private */
    public SparseIntArray mUidPolicies = new SparseIntArray();
    private BroadcastReceiver mUidRemovedReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            int uid = intent.getIntExtra("android.intent.extra.UID", -1);
            if (uid != -1) {
                Slog.v(LocationPolicyManagerService.TAG, "ACTION_UID_REMOVED for uid=" + uid);
                synchronized (LocationPolicyManagerService.this.mRulesLock) {
                    LocationPolicyManagerService.this.mUidPolicies.delete(uid);
                    LocationPolicyManagerService.this.updateRulesForUidLocked(uid);
                    LocationPolicyManagerService.this.writePolicyLocked();
                }
            }
        }
    };
    private SparseIntArray mUidRules = new SparseIntArray();
    private IUidStateChangeCallback mUidStateChangeCallback = new IUidStateChangeCallback.Stub() {
        public void onUidStateChange(int uid, int state) {
            synchronized (LocationPolicyManagerService.this.mRulesLock) {
                LocationPolicyManagerService.this.updateRulesForUidLocked(uid);
            }
        }
    };
    private UidStateHelper mUidStateHelper;
    private BroadcastReceiver mUserReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            int userId = intent.getIntExtra("android.intent.extra.user_handle", -1);
            if (userId != -1) {
                Slog.v(LocationPolicyManagerService.TAG, "USER_ADDED/USER_REMOVED for uid=" + userId);
                synchronized (LocationPolicyManagerService.this.mRulesLock) {
                    LocationPolicyManagerService.this.removePoliciesForUserLocked(userId);
                    LocationPolicyManagerService.this.updateRulesForRestrictBackgroundLocked();
                }
            }
        }
    };

    public interface FakeGpsStationaryListener {
        void onStationaryChanged(boolean z);
    }

    static LocationPolicyManagerService newDefaultService(Context context, ILocationManager locationManager) {
        sLocationPolicyService = new LocationPolicyManagerService(context, locationManager);
        return getDefaultService();
    }

    public static LocationPolicyManagerService getDefaultService() {
        LocationPolicyManagerService locationPolicyManagerService = sLocationPolicyService;
        if (locationPolicyManagerService != null) {
            return locationPolicyManagerService;
        }
        throw new RuntimeException("LocationPolicyManagerService has not been initialized ");
    }

    private LocationPolicyManagerService(Context context, ILocationManager locationManager) {
        this.mContext = context;
        this.mNotifManager = NotificationManager.getService();
        this.mLocationManager = locationManager;
        this.mHandlerThread = new HandlerThread(TAG);
        this.mHandlerThread.start();
        this.mHandler = new Handler(this.mHandlerThread.getLooper(), this.mHandlerCallback);
        this.mLocationOpHandler = new LocationOpHandler(this.mContext, this.mHandlerThread.getLooper());
        this.mPolicyFile = new AtomicFile(new File(getSystemDir(), "locationpolicy.xml"));
        this.mRestrictBackground = false;
        this.mFakeGpsStrategy = new FakeGpsStrategy();
    }

    private static File getSystemDir() {
        return new File(Environment.getDataDirectory(), "system");
    }

    public void systemRunning() {
        Slog.d(TAG, "systemRunning()");
        synchronized (this.mRulesLock) {
            this.mUidStateHelper = UidStateHelper.get();
            this.mUidStateHelper.registerUidStateObserver(this.mUidStateChangeCallback);
            this.mPowerManager = (PowerManager) this.mContext.getSystemService("power");
            readPolicyLocked();
            if (this.mRestrictBackground) {
                updateRulesForRestrictBackgroundLocked();
                updateNotificationsLocked();
            }
            this.mScreenOn = isScreenOn();
            updateRulesForScreenLocked();
            this.mDeviceIdle = isDeviceIdle();
        }
        IntentFilter screenFilter = new IntentFilter();
        screenFilter.addAction("android.intent.action.SCREEN_ON");
        screenFilter.addAction("android.intent.action.SCREEN_OFF");
        this.mContext.registerReceiver(this.mScreenReceiver, screenFilter);
        IntentFilter packageFilter = new IntentFilter();
        packageFilter.addAction("android.intent.action.PACKAGE_ADDED");
        packageFilter.addDataScheme(com.android.server.pm.Settings.ATTR_PACKAGE);
        this.mContext.registerReceiver(this.mPackageReceiver, packageFilter, (String) null, this.mHandler);
        this.mContext.registerReceiver(this.mUidRemovedReceiver, new IntentFilter("android.intent.action.UID_REMOVED"), (String) null, this.mHandler);
        IntentFilter userFilter = new IntentFilter();
        userFilter.addAction("android.intent.action.USER_ADDED");
        userFilter.addAction("android.intent.action.USER_REMOVED");
        this.mContext.registerReceiver(this.mUserReceiver, userFilter, (String) null, this.mHandler);
        this.mContext.registerReceiver(this.mAllowReceiver, new IntentFilter(ACTION_ALLOW_BACKGROUND), Manifest.permission.MANAGE_LOCATION_POLICY, this.mHandler);
        this.mContext.registerReceiver(this.mLocationModeReceiver, new IntentFilter("android.location.MODE_CHANGED"), (String) null, this.mHandler);
        try {
            this.DEVICE_IDLE_CHANGE = (String) PowerManager.class.getDeclaredField("ACTION_DEVICE_IDLE_MODE_CHANGED").get((Object) null);
            this.mContext.registerReceiver(this.mDeviceIdleChangeReceiver, new IntentFilter(this.DEVICE_IDLE_CHANGE), (String) null, this.mHandler);
        } catch (ReflectiveOperationException e) {
        } catch (Throwable e2) {
            e2.printStackTrace();
        }
    }

    /* access modifiers changed from: private */
    public boolean isScreenOn() {
        try {
            if (Build.VERSION.SDK_INT < 21) {
                return ((Boolean) PowerManager.class.getDeclaredMethod("isScreenOn", new Class[0]).invoke(this.mPowerManager, new Object[0])).booleanValue();
            }
            return ((Boolean) PowerManager.class.getDeclaredMethod("isInteractive", new Class[0]).invoke(this.mPowerManager, new Object[0])).booleanValue();
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
            return true;
        }
    }

    private boolean isDeviceIdle() {
        try {
            return ((Boolean) PowerManager.class.getDeclaredMethod("isDeviceIdleMode", new Class[0]).invoke(this.mPowerManager, new Object[0])).booleanValue();
        } catch (ReflectiveOperationException e) {
            return false;
        }
    }

    /* access modifiers changed from: private */
    public void updateNotificationsLocked() {
        Slog.v(TAG, "updateNotificationsLocked()");
        HashSet<String> beforeNotifs = Sets.newHashSet();
        beforeNotifs.addAll(this.mActiveNotifs);
        this.mActiveNotifs.clear();
        if (this.mRestrictBackground) {
            enqueueRestrictedNotification(TAG_ALLOW_BACKGROUND);
        }
        Iterator<String> it = beforeNotifs.iterator();
        while (it.hasNext()) {
            String tag = it.next();
            if (!this.mActiveNotifs.contains(tag)) {
                cancelNotification(tag);
            }
        }
    }

    private void enqueueRestrictedNotification(String tag) {
        Resources res = this.mContext.getResources();
        Notification.Builder builder = new Notification.Builder(this.mContext);
        CharSequence title = res.getText(R.string.location_usage_restricted_title);
        CharSequence body = res.getString(R.string.location_usage_restricted_body);
        builder.setOnlyAlertOnce(true);
        builder.setOngoing(true);
        builder.setSmallIcon(17301624);
        builder.setTicker(title);
        builder.setContentTitle(title);
        builder.setContentText(body);
        builder.setContentIntent(PendingIntent.getBroadcast(this.mContext, 0, new Intent(ACTION_ALLOW_BACKGROUND), 134217728));
        try {
            String packageName = this.mContext.getPackageName();
            int[] iArr = new int[1];
            this.mNotifManager.enqueueNotificationWithTag(packageName, packageName, tag, 0, builder.getNotification(), 0);
            this.mActiveNotifs.add(tag);
        } catch (RemoteException e) {
        }
    }

    private void cancelNotification(String tag) {
        try {
            this.mNotifManager.cancelNotificationWithTag(this.mContext.getPackageName(), tag, 0, 0);
        } catch (RemoteException e) {
        }
    }

    /* access modifiers changed from: private */
    public void updateLocationModeChangeLocked() {
        Slog.v(TAG, "updateLocationModeChangeLocked()");
    }

    /* access modifiers changed from: private */
    public void updateRulesForScreenLocked() {
        int size = this.mUidPolicies.size();
        for (int i = 0; i < size; i++) {
            if (this.mUidPolicies.valueAt(i) == 255) {
                int uid = this.mUidPolicies.keyAt(i);
                if (this.mUidStateHelper.isUidForeground(uid)) {
                    updateRulesForUidLocked(uid);
                }
            }
        }
    }

    /* access modifiers changed from: private */
    public void updateRulesForDeviceIdleLocked() {
        List<Integer> currentLrUids = LocationManagerServiceInjector.getCurrentLocationRequestUids();
        if (currentLrUids.size() > 0) {
            int size = this.mUidPolicies.size();
            for (int i = 0; i < size; i++) {
                if (this.mUidPolicies.valueAt(i) == 255) {
                    int uid = this.mUidPolicies.keyAt(i);
                    if (currentLrUids.contains(Integer.valueOf(uid))) {
                        updateRulesForUidLocked(uid);
                    }
                }
            }
        }
    }

    /* access modifiers changed from: private */
    public void updateLocationRulesLocked() {
        Slog.v(TAG, "updateLocationRulesLocked()");
        try {
            List<String> allProviders = this.mLocationManager.getAllProviders();
            this.mLocationRules.clear();
            ArrayList<String> providerList = Lists.newArrayList();
            for (LocationPolicy policy : this.mLocationPolicies.values()) {
                providerList.clear();
                for (String provider : allProviders) {
                    if (policy.matches(provider)) {
                        providerList.add(provider);
                    }
                }
                if (providerList.size() > 0) {
                    this.mLocationRules.put(policy, (String[]) providerList.toArray(new String[providerList.size()]));
                }
            }
        } catch (RemoteException e) {
        }
    }

    private void readPolicyLocked() {
        Slog.v(TAG, "readPolicyLocked()");
        this.mLocationPolicies.clear();
        this.mUidPolicies.clear();
        FileInputStream fis = null;
        try {
            fis = this.mPolicyFile.openRead();
            XmlPullParser in = Xml.newPullParser();
            in.setInput(fis, (String) null);
            while (true) {
                int next = in.next();
                int type = next;
                if (next == 1) {
                    break;
                }
                String tag = in.getName();
                if (type == 2) {
                    if (TAG_POLICY_LIST.equals(tag)) {
                        int version = XmlUtils.readIntAttribute(in, ATTR_VERSION);
                        this.mRestrictBackground = XmlUtils.readBooleanAttribute(in, ATTR_RESTRICT_BACKGROUND);
                    } else if (TAG_LOCATION_POLICY.equals(tag)) {
                        String provider = in.getAttributeValue((String) null, ATTR_LOCATION_PROVIDER);
                        boolean readBooleanAttribute = XmlUtils.readBooleanAttribute(in, ATTR_HIGH_POWER);
                        this.mLocationPolicies.put(provider, LocationPolicy.getLocationPolicy(provider, XmlUtils.readIntAttribute(in, ATTR_MIN_INTERVAL)));
                    } else if (TAG_UID_POLICY.equals(tag)) {
                        int uid = XmlUtils.readIntAttribute(in, "uid");
                        int policy = XmlUtils.readIntAttribute(in, ATTR_POLICY);
                        if (UserHandle.isApp(uid)) {
                            setUidPolicyUnchecked(uid, policy, false);
                        } else {
                            Slog.w(TAG, "unable to apply policy to UID " + uid + "; ignoring");
                        }
                    } else if (TAG_APP_POLICY.equals(tag)) {
                        int appId = XmlUtils.readIntAttribute(in, ATTR_APP_ID);
                        int policy2 = XmlUtils.readIntAttribute(in, ATTR_POLICY);
                        int uid2 = UserHandle.getUid(0, appId);
                        if (UserHandle.isApp(uid2)) {
                            setUidPolicyUnchecked(uid2, policy2, false);
                        } else {
                            Slog.w(TAG, "unable to apply policy to UID " + uid2 + "; ignoring");
                        }
                    }
                }
            }
        } catch (FileNotFoundException e) {
        } catch (IOException e2) {
            Slog.wtf(TAG, "problem reading location policy", e2);
        } catch (XmlPullParserException e3) {
            Slog.wtf(TAG, "problem reading location policy", e3);
        } catch (Exception e4) {
            Slog.wtf(TAG, "problem reading location policy", e4);
        } catch (Throwable th) {
            IoUtils.closeQuietly(fis);
            throw th;
        }
        IoUtils.closeQuietly(fis);
    }

    /* access modifiers changed from: private */
    public void removePoliciesForUserLocked(int userId) {
        Slog.v(TAG, "removePoliciesForUserLocked()");
        int[] uids = new int[0];
        for (int i = 0; i < this.mUidPolicies.size(); i++) {
            int uid = this.mUidPolicies.keyAt(i);
            if (UserHandle.getUserId(uid) == userId) {
                uids = ArrayUtils.appendInt(uids, uid);
            }
        }
        if (uids.length > 0) {
            for (int uid2 : uids) {
                this.mUidPolicies.delete(uid2);
                updateRulesForUidLocked(uid2);
            }
            writePolicyLocked();
        }
    }

    /* access modifiers changed from: private */
    public void writePolicyLocked() {
        Slog.v(TAG, "writePolicyLocked()");
        try {
            FileOutputStream fos = this.mPolicyFile.startWrite();
            XmlSerializer out = new FastXmlSerializer();
            out.setOutput(fos, "utf-8");
            out.startDocument((String) null, true);
            out.startTag((String) null, TAG_POLICY_LIST);
            XmlUtils.writeIntAttribute(out, ATTR_VERSION, 1);
            XmlUtils.writeBooleanAttribute(out, ATTR_RESTRICT_BACKGROUND, this.mRestrictBackground);
            for (LocationPolicy policy : this.mLocationPolicies.values()) {
                out.startTag((String) null, TAG_LOCATION_POLICY);
                out.attribute((String) null, ATTR_LOCATION_PROVIDER, policy.mProvider);
                XmlUtils.writeBooleanAttribute(out, ATTR_HIGH_POWER, policy.mHighCost);
                XmlUtils.writeIntAttribute(out, ATTR_MIN_INTERVAL, policy.mMinIntervalMs);
                out.endTag((String) null, TAG_LOCATION_POLICY);
            }
            for (int i = 0; i < this.mUidPolicies.size(); i++) {
                int uid = this.mUidPolicies.keyAt(i);
                int policy2 = this.mUidPolicies.valueAt(i);
                if (policy2 != 0) {
                    out.startTag((String) null, TAG_UID_POLICY);
                    XmlUtils.writeIntAttribute(out, "uid", uid);
                    XmlUtils.writeIntAttribute(out, ATTR_POLICY, policy2);
                    out.endTag((String) null, TAG_UID_POLICY);
                }
            }
            out.endTag((String) null, TAG_POLICY_LIST);
            out.endDocument();
            this.mPolicyFile.finishWrite(fos);
        } catch (IOException e) {
            if (0 != 0) {
                this.mPolicyFile.failWrite((FileOutputStream) null);
            }
        }
    }

    public void setUidPolicy(int uid, int policy) {
        this.mContext.enforceCallingOrSelfPermission(Manifest.permission.MANAGE_LOCATION_POLICY, TAG);
        if (UserHandle.isApp(uid)) {
            setUidPolicyUnchecked(uid, policy, true);
            return;
        }
        throw new IllegalArgumentException("cannot apply policy to UID " + uid);
    }

    private void setUidPolicyUnchecked(int uid, int policy, boolean persist) {
        synchronized (this.mRulesLock) {
            int uidPolicy = getUidPolicy(uid);
            this.mUidPolicies.put(uid, policy);
            updateRulesForUidLocked(uid);
            if (persist) {
                writePolicyLocked();
            }
        }
    }

    public int getUidPolicy(int uid) {
        int i;
        this.mContext.enforceCallingOrSelfPermission(Manifest.permission.MANAGE_LOCATION_POLICY, TAG);
        synchronized (this.mRulesLock) {
            i = this.mUidPolicies.get(uid, 0);
        }
        return i;
    }

    public int[] getUidsWithPolicy(int policy) {
        this.mContext.enforceCallingOrSelfPermission(Manifest.permission.MANAGE_LOCATION_POLICY, TAG);
        int[] uids = new int[0];
        synchronized (this.mRulesLock) {
            for (int i = 0; i < this.mUidPolicies.size(); i++) {
                int uid = this.mUidPolicies.keyAt(i);
                if (this.mUidPolicies.valueAt(i) == policy) {
                    uids = ArrayUtils.appendInt(uids, uid);
                }
            }
        }
        return uids;
    }

    public void setUidNavigationStart(int uid) {
        Slog.d(TAG, "uid " + uid + " navigation start");
        synchronized (this.mRulesLock) {
            this.mUidInNavigation.put(uid, true);
            if (this.mUidRules.get(uid, 0) != 0) {
                updateRulesForUidLocked(uid);
            }
        }
    }

    public void setUidNavigationStop(int uid) {
        Slog.d(TAG, "uid " + uid + " navigation stopped");
        synchronized (this.mRulesLock) {
            this.mUidInNavigation.put(uid, false);
            if (this.mUidRules.get(uid, 0) == 0) {
                updateRulesForUidLocked(uid);
            }
        }
    }

    public boolean checkUidNavigationScreenLock(int uid) {
        return PowerManagerServiceInjector.getScreenWakeLockHoldByUid(uid) > 0;
    }

    public void registerListener(ILocationPolicyListener listener) {
        this.mContext.enforceCallingOrSelfPermission(Manifest.permission.LOCATION_POLICY_INTERNAL, TAG);
        this.mListeners.register(listener);
    }

    public void unregisterListener(ILocationPolicyListener listener) {
        this.mContext.enforceCallingOrSelfPermission(Manifest.permission.LOCATION_POLICY_INTERNAL, TAG);
        this.mListeners.unregister(listener);
    }

    public void setLocationPolicies(LocationPolicy[] policies) {
        this.mContext.enforceCallingOrSelfPermission(Manifest.permission.MANAGE_LOCATION_POLICY, TAG);
        int length = policies.length;
        int i = 0;
        while (i < length) {
            LocationPolicy policy = policies[i];
            int matchRule = policy.getMatchRule();
            if (matchRule == 1 || matchRule == 2 || matchRule == 3 || matchRule == 4) {
                i++;
            } else {
                throw new IllegalArgumentException("unexpected provider " + policy.getMatchRule());
            }
        }
        synchronized (this.mRulesLock) {
            this.mLocationPolicies.clear();
            for (LocationPolicy policy2 : policies) {
                this.mLocationPolicies.put(policy2.mProvider, policy2);
            }
            updateLocationRulesLocked();
            updateNotificationsLocked();
            writePolicyLocked();
        }
    }

    public LocationPolicy[] getLocationPolicies() {
        LocationPolicy[] locationPolicyArr;
        this.mContext.enforceCallingOrSelfPermission(Manifest.permission.MANAGE_LOCATION_POLICY, TAG);
        synchronized (this.mRulesLock) {
            locationPolicyArr = (LocationPolicy[]) this.mLocationPolicies.values().toArray(new LocationPolicy[this.mLocationPolicies.size()]);
        }
        return locationPolicyArr;
    }

    public void setRestrictBackground(boolean restrictBackground) {
        this.mContext.enforceCallingOrSelfPermission(Manifest.permission.MANAGE_LOCATION_POLICY, TAG);
        synchronized (this.mRulesLock) {
            this.mRestrictBackground = restrictBackground;
            updateRulesForRestrictBackgroundLocked();
            updateNotificationsLocked();
            writePolicyLocked();
        }
        this.mHandler.obtainMessage(2, restrictBackground, 0).sendToTarget();
    }

    public boolean getRestrictBackground() {
        boolean z;
        this.mContext.enforceCallingOrSelfPermission(Manifest.permission.MANAGE_LOCATION_POLICY, TAG);
        synchronized (this.mRulesLock) {
            z = this.mRestrictBackground;
        }
        return z;
    }

    public boolean checkUidLocationOp(int uid, int op) {
        boolean granted = false;
        long now = System.currentTimeMillis();
        synchronized (this.mRulesLock) {
            if (!UserHandle.isApp(uid)) {
                granted = true;
            } else if (this.mUidStateHelper.isUidForeground(uid)) {
                granted = true;
                int minInterval = getOpMinIntervalMsLocked(op);
                if (minInterval != 0 && this.mLocationOpHandler.isFrequenctlyOp(uid, op, now, minInterval)) {
                    granted = false;
                    this.mLocationOpHandler.setFollowupAction(uid, op, now, minInterval);
                }
            } else if (op != 0) {
                int rule = this.mUidRules.get(uid, 0);
                if (rule == 1 || rule == 0) {
                    granted = true;
                }
            } else if (this.mUidRules.get(uid, 0) == 0) {
                granted = true;
            }
            if (granted) {
                this.mLocationOpHandler.updateLastLocationOp(uid, op, now);
            } else {
                Slog.d(TAG, "[uid = " + uid + ", op = " + op + "] is blocked by location policy");
            }
        }
        return granted;
    }

    private int getOpMinIntervalMsLocked(int op) {
        if (op != 0) {
            if ((op == 1 || op == 2) && this.mLocationPolicies.containsKey("network")) {
                return this.mLocationPolicies.get("network").mMinIntervalMs;
            }
            return 0;
        } else if (this.mLocationPolicies.containsKey("gps")) {
            return this.mLocationPolicies.get("gps").mMinIntervalMs;
        } else {
            return 0;
        }
    }

    public boolean isUidForeground(int uid) {
        boolean isUidForegroundLocked;
        if (!UserHandle.isApp(uid)) {
            return true;
        }
        synchronized (this.mRulesLock) {
            isUidForegroundLocked = isUidForegroundLocked(uid);
        }
        return isUidForegroundLocked;
    }

    private boolean isUidForegroundLocked(int uid) {
        return this.mUidStateHelper.isUidForeground(uid) && this.mScreenOn;
    }

    /* access modifiers changed from: protected */
    public void dump(FileDescriptor fd, PrintWriter pw, String[] args) {
        this.mContext.enforceCallingOrSelfPermission("android.permission.DUMP", TAG);
        HashSet<String> argSet = new HashSet<>();
        for (String arg : args) {
            argSet.add(arg);
        }
        synchronized (this.mRulesLock) {
            pw.println("mLocationMode=" + this.mLocationMode);
            pw.println("mScreenOn=" + this.mScreenOn);
            pw.println("mPolicyFile=" + this.mPolicyFile.getBaseFile().getPath());
            pw.println("mRestrictBackground=" + this.mRestrictBackground);
            pw.println("mLocationPolicies size=" + this.mLocationPolicies.size());
            pw.println("mLocationRules size=" + this.mLocationRules.size());
            pw.println("mUidPolicies size=" + this.mUidPolicies.size());
            for (int i = 0; i < this.mUidPolicies.size(); i++) {
                pw.print("uid=" + this.mUidPolicies.keyAt(i) + ", ");
                LocationPolicyManager.dumpPolicy(pw, this.mUidPolicies.valueAt(i));
                pw.println();
            }
            pw.println("mUidRules size=" + this.mUidRules.size());
            for (int i2 = 0; i2 < this.mUidRules.size(); i2++) {
                pw.print("uid=" + this.mUidRules.keyAt(i2) + ", ");
                LocationPolicyManager.dumpRules(pw, this.mUidRules.valueAt(i2));
                pw.println();
            }
            pw.println("mUidInNavigation size =" + this.mUidInNavigation.size());
            for (int i3 = 0; i3 < this.mUidInNavigation.size(); i3++) {
                pw.println("uid=" + this.mUidInNavigation.keyAt(i3) + ", inNavigation=" + this.mUidInNavigation.valueAt(i3));
            }
        }
        if (this.mFakeGpsStrategy != null) {
            Slog.d(TAG, "miui gps strategy dump");
            this.mFakeGpsStrategy.dump(fd, pw, args);
        }
    }

    /* access modifiers changed from: private */
    public void updateRulesForRestrictBackgroundLocked() {
        PackageManager pm = this.mContext.getPackageManager();
        List<UserInfo> users = ((UserManager) this.mContext.getSystemService("user")).getUsers();
        List<ApplicationInfo> apps = pm.getInstalledApplications(8704);
        for (UserInfo user : users) {
            for (ApplicationInfo app : apps) {
                updateRulesForUidLocked(UserHandle.getUid(user.id, app.uid));
            }
        }
    }

    private static boolean isUidValidForRules(int uid) {
        if (uid == 1001 || UserHandle.isApp(uid)) {
            return true;
        }
        return false;
    }

    /* access modifiers changed from: private */
    public void updateRulesForUidLocked(int uid) {
        if (isUidValidForRules(uid)) {
            int uidPolicy = this.mUidPolicies.get(uid, 0);
            boolean uidForeground = isUidForegroundLocked(uid);
            int uidRules = 0;
            if (!uidForeground && (uidPolicy & 255) != 0) {
                uidRules = 255;
            }
            if (!uidForeground && this.mRestrictBackground) {
                uidRules = 255;
            }
            if (this.mUidInNavigation.get(uid, false)) {
                uidRules = 0;
            }
            if (uidRules == 0) {
                this.mUidRules.delete(uid);
            } else {
                this.mUidRules.put(uid, uidRules);
            }
            this.mHandler.obtainMessage(1, uid, uidRules).sendToTarget();
        }
    }

    public void setFakeGpsFeatureOnState(boolean on) {
        this.mContext.enforceCallingOrSelfPermission(Manifest.permission.MANAGE_LOCATION_POLICY, TAG);
        this.mFakeGpsStrategy.setOnState(on);
    }

    public void setPhoneStationary(boolean stationary, Location location) {
        this.mContext.enforceCallingOrSelfPermission(Manifest.permission.MANAGE_LOCATION_POLICY, TAG);
        this.mFakeGpsStrategy.setPhoneStationay(stationary, location);
    }

    /* access modifiers changed from: package-private */
    public boolean getFakeGpsFeatureOnState() {
        this.mContext.enforceCallingOrSelfPermission(Manifest.permission.MANAGE_LOCATION_POLICY, TAG);
        FakeGpsStrategy fakeGpsStrategy = this.mFakeGpsStrategy;
        if (fakeGpsStrategy != null) {
            return fakeGpsStrategy.getOnState();
        }
        return false;
    }

    /* access modifiers changed from: package-private */
    public boolean getPhoneStationary() {
        this.mContext.enforceCallingOrSelfPermission(Manifest.permission.MANAGE_LOCATION_POLICY, TAG);
        FakeGpsStrategy fakeGpsStrategy = this.mFakeGpsStrategy;
        if (fakeGpsStrategy != null) {
            return fakeGpsStrategy.getPhoneStationary();
        }
        return false;
    }

    /* access modifiers changed from: package-private */
    public Location getFakeGpsLocation() {
        this.mContext.enforceCallingOrSelfPermission(Manifest.permission.MANAGE_LOCATION_POLICY, TAG);
        FakeGpsStrategy fakeGpsStrategy = this.mFakeGpsStrategy;
        if (fakeGpsStrategy != null) {
            return fakeGpsStrategy.getLocation();
        }
        return null;
    }

    /* access modifiers changed from: package-private */
    public void registerFakeGpsStatus(FakeGpsStationaryListener listener) {
        this.mContext.enforceCallingOrSelfPermission(Manifest.permission.MANAGE_LOCATION_POLICY, TAG);
        FakeGpsStrategy fakeGpsStrategy = this.mFakeGpsStrategy;
        if (fakeGpsStrategy != null) {
            fakeGpsStrategy.registerFakeGpsStatus(listener);
        }
    }

    private class FakeGpsStrategy {
        private boolean mFakeGpsOn = false;
        private boolean mHasLocation = false;
        private List<FakeGpsStationaryListener> mListener = new ArrayList();
        private Location mLocation = null;
        private boolean mPhoneStationary = false;

        public FakeGpsStrategy() {
        }

        /* access modifiers changed from: package-private */
        public void setOnState(boolean on) {
            if (this.mFakeGpsOn != on) {
                this.mFakeGpsOn = on;
            }
        }

        /* access modifiers changed from: package-private */
        public void setPhoneStationay(boolean stationary, Location location) {
            if (this.mPhoneStationary != stationary) {
                this.mPhoneStationary = stationary;
                notifyListeners(stationary);
                if (stationary) {
                    setLocation(location);
                    return;
                }
                this.mHasLocation = false;
                this.mLocation = null;
            }
        }

        private void setLocation(Location location) {
            if (this.mFakeGpsOn && location != null) {
                this.mHasLocation = true;
                this.mLocation = location;
            } else if (!this.mFakeGpsOn) {
                this.mHasLocation = false;
            }
        }

        /* access modifiers changed from: package-private */
        public boolean getOnState() {
            return this.mFakeGpsOn;
        }

        /* access modifiers changed from: package-private */
        public boolean getPhoneStationary() {
            return this.mPhoneStationary;
        }

        public Location getLocation() {
            if (this.mHasLocation) {
                return this.mLocation;
            }
            return null;
        }

        /* access modifiers changed from: package-private */
        public void dump(FileDescriptor fd, PrintWriter pw, String[] args) {
            StringBuilder builder = new StringBuilder();
            builder.append("miui gps provider info:");
            builder.append("\n            on: " + this.mFakeGpsOn);
            builder.append("\n    stationary: " + this.mPhoneStationary);
            builder.append("\n  mHasLocation: " + this.mHasLocation);
            if (Build.IS_DEBUGGABLE) {
                builder.append("\n  mLocation: " + this.mLocation);
            }
            pw.print(builder);
        }

        /* access modifiers changed from: package-private */
        public void registerFakeGpsStatus(FakeGpsStationaryListener listener) {
            if (listener != null) {
                synchronized (this.mListener) {
                    Slog.d(LocationPolicyManagerService.TAG, "register Status in strategy");
                    this.mListener.add(listener);
                }
            }
        }

        private void notifyListeners(boolean stationary) {
            synchronized (this.mListener) {
                for (FakeGpsStationaryListener listener : this.mListener) {
                    listener.onStationaryChanged(stationary);
                }
            }
        }
    }
}
