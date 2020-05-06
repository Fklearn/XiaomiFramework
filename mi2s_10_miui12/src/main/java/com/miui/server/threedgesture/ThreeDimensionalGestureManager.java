package com.miui.server.threedgesture;

import android.app.KeyguardManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManagerInternal;
import android.content.pm.ResolveInfo;
import android.database.ContentObserver;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.Process;
import android.os.UserHandle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Slog;
import com.android.server.LocalServices;
import com.miui.server.AccessController;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import miui.process.ForegroundInfo;
import miui.process.IForegroundInfoListener;
import miui.process.ProcessManager;
import miui.util.Log;

public class ThreeDimensionalGestureManager {
    private static final boolean DBG = true;
    private static final int GESTURE_C = 3;
    private static final int GESTURE_M = 1;
    private static final int GESTURE_W = 2;
    private static final int GESTURE_Z = 4;
    public static final String MIUI_3D_GESTURE_OPEN = "miui_3d_gesture_open";
    public static final String MIUI_GESTURE_C = "miui_3d_gesture_c";
    public static final String MIUI_GESTURE_M = "miui_3d_gesture_m";
    public static final String MIUI_GESTURE_W = "miui_3d_gesture_w";
    public static final String MIUI_GESTURE_Z = "miui_3d_gesture_z";
    public static final String TAG = "3DGestureManager";
    private static final int THREE_DIMENSIONAL_GESTURE_SENSOR_ID = 33171032;
    public static final ArrayList<String> sMonitorList = new ArrayList<>();
    /* access modifiers changed from: private */
    public Context mContext;
    private int mCurrentUserId = 0;
    private IForegroundInfoListener.Stub mForegroundInfoChangeListener = new IForegroundInfoListener.Stub() {
        public void onForegroundInfoChanged(ForegroundInfo foregroundInfo) {
            String unused = ThreeDimensionalGestureManager.this.mForegroundPackage = foregroundInfo.mForegroundPackageName;
        }
    };
    /* access modifiers changed from: private */
    public String mForegroundPackage;
    /* access modifiers changed from: private */
    public String mGestureCLaunchApp;
    private boolean mGestureEnabled;
    /* access modifiers changed from: private */
    public String mGestureMLaunchApp;
    private final Sensor mGestureSensor;
    private SensorEventListener mGestureSensorListener = new SensorEventListener() {
        public void onSensorChanged(SensorEvent event) {
            if (event.sensor.getType() == ThreeDimensionalGestureManager.THREE_DIMENSIONAL_GESTURE_SENSOR_ID) {
                Log.d(ThreeDimensionalGestureManager.TAG, "event.values[0] = " + event.values[0] + " isOkToMonitorGesture = " + ThreeDimensionalGestureManager.this.isOkToMonitorGesture());
                if (ThreeDimensionalGestureManager.this.isOkToMonitorGesture() && ThreeDimensionalGestureManager.sMonitorList.contains(ThreeDimensionalGestureManager.this.mForegroundPackage)) {
                    int i = (int) event.values[0];
                    if (i != 1) {
                        if (i != 2) {
                            if (i != 3) {
                                if (i == 4 && !TextUtils.isEmpty(ThreeDimensionalGestureManager.this.mGestureZLaunchApp)) {
                                    ThreeDimensionalGestureManager threeDimensionalGestureManager = ThreeDimensionalGestureManager.this;
                                    threeDimensionalGestureManager.launchApp(threeDimensionalGestureManager.mGestureZLaunchApp);
                                }
                            } else if (!TextUtils.isEmpty(ThreeDimensionalGestureManager.this.mGestureCLaunchApp)) {
                                ThreeDimensionalGestureManager.this.launchFrontCamera();
                            }
                        } else if (!TextUtils.isEmpty(ThreeDimensionalGestureManager.this.mGestureWLaunchApp)) {
                            ThreeDimensionalGestureManager threeDimensionalGestureManager2 = ThreeDimensionalGestureManager.this;
                            threeDimensionalGestureManager2.launchApp(threeDimensionalGestureManager2.mGestureWLaunchApp);
                        }
                    } else if (!TextUtils.isEmpty(ThreeDimensionalGestureManager.this.mGestureMLaunchApp)) {
                        ThreeDimensionalGestureManager threeDimensionalGestureManager3 = ThreeDimensionalGestureManager.this;
                        threeDimensionalGestureManager3.launchApp(threeDimensionalGestureManager3.mGestureMLaunchApp);
                    }
                }
            }
        }

        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    };
    /* access modifiers changed from: private */
    public String mGestureWLaunchApp;
    /* access modifiers changed from: private */
    public String mGestureZLaunchApp;
    private Handler mHandler;
    /* access modifiers changed from: private */
    public boolean mIsGestureOpen;
    /* access modifiers changed from: private */
    public boolean mScreenOn = true;
    private SettingsObserver mSettingsObserver;
    private final SensorManager mSm;

    static {
        sMonitorList.add("com.miui.home");
    }

    public ThreeDimensionalGestureManager(Context ctx) {
        this.mContext = ctx;
        this.mGestureEnabled = false;
        this.mSm = (SensorManager) ctx.getSystemService("sensor");
        this.mGestureSensor = this.mSm.getDefaultSensor(THREE_DIMENSIONAL_GESTURE_SENSOR_ID);
        this.mHandler = new Handler();
        this.mSettingsObserver = new SettingsObserver(this.mHandler);
    }

    public void systemReady() {
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.intent.action.SCREEN_ON");
        filter.addAction("android.intent.action.SCREEN_OFF");
        filter.addAction("android.intent.action.USER_PRESENT");
        this.mContext.registerReceiver(new SystemReceiver(), filter);
        registerForegroundInfoChangeListener();
        registerGestureSettingsListener();
    }

    /* access modifiers changed from: private */
    public void launchApp(String pkg) {
        Intent intent = getLaunchIntentForPackageAsUser(pkg, this.mCurrentUserId);
        if (intent == null) {
            Log.e(TAG, "error to launch " + pkg);
            return;
        }
        this.mContext.startActivity(intent);
    }

    public static Intent getLaunchIntentForPackageAsUser(String packageName, int userId) {
        Intent intentToResolve = new Intent("android.intent.action.MAIN");
        intentToResolve.addCategory("android.intent.category.INFO");
        intentToResolve.setPackage(packageName);
        List<ResolveInfo> ris = queryIntentActivitiesAsUser(intentToResolve, 0, userId);
        if (ris == null || ris.size() <= 0) {
            intentToResolve.removeCategory("android.intent.category.INFO");
            intentToResolve.addCategory("android.intent.category.LAUNCHER");
            intentToResolve.setPackage(packageName);
            ris = queryIntentActivitiesAsUser(intentToResolve, 0, userId);
        }
        if (ris == null || ris.size() <= 0) {
            return null;
        }
        Intent intent = new Intent(intentToResolve);
        intent.setFlags(268435456);
        intent.setClassName(ris.get(0).activityInfo.packageName, ris.get(0).activityInfo.name);
        return intent;
    }

    public static List<ResolveInfo> queryIntentActivitiesAsUser(Intent intent, int flags, int userId) {
        List<ResolveInfo> infos = ((PackageManagerInternal) LocalServices.getService(PackageManagerInternal.class)).queryIntentActivities(intent, flags, Process.myUid(), userId);
        if (infos == null) {
            return Collections.emptyList();
        }
        return infos;
    }

    /* access modifiers changed from: private */
    public void launchFrontCamera() {
        Intent intent = new Intent();
        intent.setFlags(268468224);
        intent.putExtra("ShowCameraWhenLocked", true);
        intent.putExtra("StartActivityWhenLocked", true);
        intent.setAction("android.media.action.STILL_IMAGE_CAMERA");
        intent.putExtra("android.intent.extras.CAMERA_FACING", 0);
        intent.putExtra("autofocus", true);
        intent.putExtra("fullScreen", false);
        intent.putExtra("showActionIcons", false);
        intent.putExtra("android.intent.extras.SCREEN_SLIDE", true);
        intent.setComponent(new ComponentName(AccessController.PACKAGE_CAMERA, "com.android.camera.Camera"));
        Slog.d(TAG, "launchFrontCamera");
        this.mContext.startActivityAsUser(intent, UserHandle.CURRENT);
    }

    /* access modifiers changed from: private */
    public boolean isOkToMonitorGesture() {
        return this.mScreenOn && !isKeyguardLocked() && isGestureOpen();
    }

    private boolean isKeyguardLocked() {
        KeyguardManager km = (KeyguardManager) this.mContext.getSystemService("keyguard");
        if (km != null) {
            return km.isKeyguardLocked();
        }
        return false;
    }

    public void registerForegroundInfoChangeListener() {
        ProcessManager.unregisterForegroundInfoListener(this.mForegroundInfoChangeListener);
        ProcessManager.registerForegroundInfoListener(this.mForegroundInfoChangeListener);
    }

    public void enableGesture(boolean enabled) {
        if (this.mGestureEnabled != enabled) {
            if (enabled) {
                registerGestureSensorListener();
            } else {
                unregisterGestureSensorListener();
            }
            this.mGestureEnabled = enabled;
        }
    }

    public class SystemReceiver extends BroadcastReceiver {
        public SystemReceiver() {
        }

        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if ("android.intent.action.SCREEN_ON".equals(action)) {
                boolean unused = ThreeDimensionalGestureManager.this.mScreenOn = true;
                if (ThreeDimensionalGestureManager.this.isOkToMonitorGesture()) {
                    ThreeDimensionalGestureManager.this.enableGesture(true);
                }
            } else if ("android.intent.action.SCREEN_OFF".equals(action)) {
                boolean unused2 = ThreeDimensionalGestureManager.this.mScreenOn = false;
                ThreeDimensionalGestureManager.this.enableGesture(false);
            } else if ("android.intent.action.USER_PRESENT".equals(action)) {
                if (TextUtils.isEmpty(ThreeDimensionalGestureManager.this.mForegroundPackage) && ProcessManager.getForegroundInfo() != null) {
                    String unused3 = ThreeDimensionalGestureManager.this.mForegroundPackage = ProcessManager.getForegroundInfo().mForegroundPackageName;
                }
                if (ThreeDimensionalGestureManager.this.isOkToMonitorGesture()) {
                    ThreeDimensionalGestureManager.this.enableGesture(true);
                }
            }
        }
    }

    private void registerGestureSettingsListener() {
        ContentResolver resolver = this.mContext.getContentResolver();
        resolver.registerContentObserver(Settings.System.getUriFor(MIUI_3D_GESTURE_OPEN), false, this.mSettingsObserver, -1);
        resolver.registerContentObserver(Settings.System.getUriFor(MIUI_GESTURE_M), false, this.mSettingsObserver, -1);
        resolver.registerContentObserver(Settings.System.getUriFor(MIUI_GESTURE_W), false, this.mSettingsObserver, -1);
        resolver.registerContentObserver(Settings.System.getUriFor(MIUI_GESTURE_C), false, this.mSettingsObserver, -1);
        resolver.registerContentObserver(Settings.System.getUriFor(MIUI_GESTURE_Z), false, this.mSettingsObserver, -1);
        initSettings(resolver);
    }

    private void initSettings(ContentResolver resolver) {
        boolean z = false;
        if (Settings.System.getIntForUser(resolver, MIUI_3D_GESTURE_OPEN, 0, -2) == 1) {
            z = true;
        }
        this.mIsGestureOpen = z;
        this.mGestureMLaunchApp = Settings.System.getStringForUser(resolver, MIUI_GESTURE_M, -2);
        this.mGestureWLaunchApp = Settings.System.getStringForUser(resolver, MIUI_GESTURE_W, -2);
        this.mGestureCLaunchApp = Settings.System.getStringForUser(resolver, MIUI_GESTURE_C, -2);
        this.mGestureZLaunchApp = Settings.System.getStringForUser(resolver, MIUI_GESTURE_Z, -2);
    }

    private final class SettingsObserver extends ContentObserver {
        public SettingsObserver(Handler handler) {
            super(handler);
        }

        /* JADX WARNING: Can't fix incorrect switch cases order */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void onChange(boolean r16, android.net.Uri r17) {
            /*
                r15 = this;
                r0 = r15
                java.lang.String r1 = r17.getLastPathSegment()
                com.miui.server.threedgesture.ThreeDimensionalGestureManager r2 = com.miui.server.threedgesture.ThreeDimensionalGestureManager.this
                android.content.Context r2 = r2.mContext
                android.content.ContentResolver r2 = r2.getContentResolver()
                int r3 = r1.hashCode()
                java.lang.String r4 = "miui_3d_gesture_open"
                java.lang.String r5 = "miui_3d_gesture_z"
                java.lang.String r6 = "miui_3d_gesture_w"
                java.lang.String r7 = "miui_3d_gesture_m"
                java.lang.String r8 = "miui_3d_gesture_c"
                r9 = 4
                r10 = 3
                r11 = 2
                r12 = 0
                r13 = 1
                switch(r3) {
                    case -1587204754: goto L_0x0046;
                    case -1587204744: goto L_0x003e;
                    case -1587204734: goto L_0x0036;
                    case -1587204731: goto L_0x002e;
                    case -1121396385: goto L_0x0026;
                    default: goto L_0x0025;
                }
            L_0x0025:
                goto L_0x004e
            L_0x0026:
                boolean r3 = r1.equals(r4)
                if (r3 == 0) goto L_0x0025
                r3 = r12
                goto L_0x004f
            L_0x002e:
                boolean r3 = r1.equals(r5)
                if (r3 == 0) goto L_0x0025
                r3 = r9
                goto L_0x004f
            L_0x0036:
                boolean r3 = r1.equals(r6)
                if (r3 == 0) goto L_0x0025
                r3 = r11
                goto L_0x004f
            L_0x003e:
                boolean r3 = r1.equals(r7)
                if (r3 == 0) goto L_0x0025
                r3 = r13
                goto L_0x004f
            L_0x0046:
                boolean r3 = r1.equals(r8)
                if (r3 == 0) goto L_0x0025
                r3 = r10
                goto L_0x004f
            L_0x004e:
                r3 = -1
            L_0x004f:
                r14 = -2
                if (r3 == 0) goto L_0x0083
                if (r3 == r13) goto L_0x0079
                if (r3 == r11) goto L_0x006f
                if (r3 == r10) goto L_0x0065
                if (r3 == r9) goto L_0x005b
                goto L_0x009a
            L_0x005b:
                com.miui.server.threedgesture.ThreeDimensionalGestureManager r3 = com.miui.server.threedgesture.ThreeDimensionalGestureManager.this
                java.lang.String r4 = android.provider.Settings.System.getStringForUser(r2, r5, r14)
                java.lang.String unused = r3.mGestureZLaunchApp = r4
                goto L_0x009a
            L_0x0065:
                com.miui.server.threedgesture.ThreeDimensionalGestureManager r3 = com.miui.server.threedgesture.ThreeDimensionalGestureManager.this
                java.lang.String r4 = android.provider.Settings.System.getStringForUser(r2, r8, r14)
                java.lang.String unused = r3.mGestureCLaunchApp = r4
                goto L_0x009a
            L_0x006f:
                com.miui.server.threedgesture.ThreeDimensionalGestureManager r3 = com.miui.server.threedgesture.ThreeDimensionalGestureManager.this
                java.lang.String r4 = android.provider.Settings.System.getStringForUser(r2, r6, r14)
                java.lang.String unused = r3.mGestureWLaunchApp = r4
                goto L_0x009a
            L_0x0079:
                com.miui.server.threedgesture.ThreeDimensionalGestureManager r3 = com.miui.server.threedgesture.ThreeDimensionalGestureManager.this
                java.lang.String r4 = android.provider.Settings.System.getStringForUser(r2, r7, r14)
                java.lang.String unused = r3.mGestureMLaunchApp = r4
                goto L_0x009a
            L_0x0083:
                com.miui.server.threedgesture.ThreeDimensionalGestureManager r3 = com.miui.server.threedgesture.ThreeDimensionalGestureManager.this
                int r4 = android.provider.Settings.System.getIntForUser(r2, r4, r12, r14)
                if (r4 != r13) goto L_0x008c
                goto L_0x008d
            L_0x008c:
                r13 = r12
            L_0x008d:
                boolean unused = r3.mIsGestureOpen = r13
                com.miui.server.threedgesture.ThreeDimensionalGestureManager r3 = com.miui.server.threedgesture.ThreeDimensionalGestureManager.this
                boolean r4 = r3.mIsGestureOpen
                r3.enableGesture(r4)
            L_0x009a:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: com.miui.server.threedgesture.ThreeDimensionalGestureManager.SettingsObserver.onChange(boolean, android.net.Uri):void");
        }
    }

    private void registerGestureSensorListener() {
        Sensor sensor;
        SensorManager sensorManager = this.mSm;
        if (sensorManager == null || (sensor = this.mGestureSensor) == null) {
            Slog.w(TAG, "registerGestureSensorListener failed.");
            return;
        }
        sensorManager.registerListener(this.mGestureSensorListener, sensor, 3, this.mHandler);
        Slog.d(TAG, "registerGestureSensorListener successed.");
    }

    private void unregisterGestureSensorListener() {
        SensorManager sensorManager = this.mSm;
        if (sensorManager != null) {
            sensorManager.unregisterListener(this.mGestureSensorListener);
            Slog.d(TAG, "unregisterGestureSensorListener successed.");
        }
    }

    public boolean isGestureOpen() {
        return this.mIsGestureOpen;
    }
}
