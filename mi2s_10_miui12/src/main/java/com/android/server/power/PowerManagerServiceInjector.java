package com.android.server.power;

import android.content.ContentResolver;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.miui.R;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.os.UserHandle;
import android.os.WorkSource;
import android.provider.Settings;
import android.util.Slog;
import android.widget.Toast;
import com.android.internal.app.IUidStateChangeCallback;
import com.android.server.UiThread;
import com.android.server.UidStateHelper;
import com.android.server.power.PowerManagerService;
import com.miui.whetstone.PowerKeeperPolicy;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

public class PowerManagerServiceInjector {
    private static final boolean DEBUG = true;
    private static final String TAG = "PowerManagerServiceInjector";
    private static Handler sHandler;
    private static Sensor sLightSensor;
    /* access modifiers changed from: private */
    public static boolean sLightSensorEnabled = false;
    private static final SensorEventListener sLightSensorListener = new SensorEventListener() {
        public void onSensorChanged(SensorEvent event) {
            if (PowerManagerServiceInjector.sLightSensorEnabled) {
                long time = SystemClock.uptimeMillis();
                float lux = event.values[0];
                if (PowerManagerServiceInjector.sStartLightSensorTime == 0) {
                    long unused = PowerManagerServiceInjector.sStartLightSensorTime = time;
                } else if (time >= PowerManagerServiceInjector.sStartLightSensorTime + 2000) {
                    PowerManagerServiceInjector.setLightSensorEnabled(false);
                }
                Slog.d(PowerManagerServiceInjector.TAG, "sLightSensorListener, lux= " + lux + ", time=" + time);
            }
        }

        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    };
    private static Object sLock;
    /* access modifiers changed from: private */
    public static PowerKeeperPolicy sPolicy;
    private static PowerManagerService sPowerManagerService;
    private static SensorManager sSensorManager;
    /* access modifiers changed from: private */
    public static long sStartLightSensorTime = 0;
    private static IUidStateChangeCallback sUidStateChangeCallback = new IUidStateChangeCallback.Stub() {
        public void onUidStateChange(int uid, int state) {
            if (state > 0 && PowerManagerServiceInjector.getScreenWakeLockHoldByUid(uid) > 0) {
                PowerManagerServiceInjector.restoreScreenWakeLockDisabledState(uid);
            }
        }
    };
    /* access modifiers changed from: private */
    public static UidStateHelper sUidStateHelper;
    private static ArrayList<PowerManagerService.WakeLock> sWakeLocks;

    public static void init(PowerManagerService powerManagerService, ArrayList<PowerManagerService.WakeLock> allWakeLocks, Object lock) {
        sPowerManagerService = powerManagerService;
        sWakeLocks = allWakeLocks;
        sLock = lock;
        sHandler = new Handler(Looper.getMainLooper());
        sHandler.post(new Runnable() {
            public void run() {
                UidStateHelper unused = PowerManagerServiceInjector.sUidStateHelper = UidStateHelper.get();
                PowerKeeperPolicy unused2 = PowerManagerServiceInjector.sPolicy = PowerKeeperPolicy.getInstance();
            }
        });
    }

    public static void init(PowerManagerService powerManagerService, ArrayList<PowerManagerService.WakeLock> allWakeLocks, Object lock, SensorManager sensorManager) {
        sSensorManager = sensorManager;
        sLightSensor = sSensorManager.getDefaultSensor(5);
        init(powerManagerService, allWakeLocks, lock);
    }

    public static void setLightSensorEnabled(boolean enable) {
        if (enable && !sLightSensorEnabled) {
            Slog.d(TAG, "setLightSensorEnabled enable");
            sLightSensorEnabled = true;
            sSensorManager.registerListener(sLightSensorListener, sLightSensor, 3);
        } else if (!enable && sLightSensorEnabled) {
            Slog.d(TAG, "setLightSensorEnabled disable");
            sLightSensorEnabled = false;
            sStartLightSensorTime = 0;
            sSensorManager.unregisterListener(sLightSensorListener);
        }
    }

    private static int[] getRealOwners(PowerManagerService.WakeLock wakeLock) {
        int[] iArr = new int[0];
        if (wakeLock.mWorkSource == null) {
            return new int[]{wakeLock.mOwnerUid};
        }
        int N = wakeLock.mWorkSource.size();
        int[] realOwners = new int[N];
        for (int i = 0; i < N; i++) {
            realOwners[i] = wakeLock.mWorkSource.get(i);
        }
        return realOwners;
    }

    public static int getPartialWakeLockHoldByUid(int uid) {
        int wakeLockNum = 0;
        synchronized (sLock) {
            Iterator<PowerManagerService.WakeLock> it = sWakeLocks.iterator();
            while (it.hasNext()) {
                PowerManagerService.WakeLock wakeLock = it.next();
                WorkSource ws = wakeLock.mWorkSource;
                if (ws != null || wakeLock.mOwnerUid == uid) {
                    if (ws == null || ws.get(0) == uid) {
                        if ((wakeLock.mFlags & 65535) == 1) {
                            wakeLockNum++;
                        }
                    }
                }
            }
        }
        return wakeLockNum;
    }

    public static int getScreenWakeLockHoldByUid(int uid) {
        int wakeLockNum = 0;
        synchronized (sLock) {
            Iterator<PowerManagerService.WakeLock> it = sWakeLocks.iterator();
            while (it.hasNext()) {
                PowerManagerService.WakeLock wakeLock = it.next();
                WorkSource ws = wakeLock.mWorkSource;
                if (ws != null || wakeLock.mOwnerUid == uid) {
                    if (ws == null || ws.get(0) == uid) {
                        int wakeLockType = wakeLock.mFlags & 65535;
                        if (wakeLockType == 6 || wakeLockType == 10 || wakeLockType == 26) {
                            wakeLockNum++;
                        }
                    }
                }
            }
        }
        return wakeLockNum;
    }

    static boolean isBackgroundScreenWakelock(PowerManagerService.WakeLock wakeLock) {
        boolean foreground = false;
        for (int realOwner : getRealOwners(wakeLock)) {
            UidStateHelper uidStateHelper = sUidStateHelper;
            if (uidStateHelper != null) {
                foreground |= uidStateHelper.isUidForeground(realOwner, true);
            }
        }
        if (!foreground) {
            return true;
        }
        return false;
    }

    static boolean isWakelockDisabledByPolicy(PowerManagerService.WakeLock wakeLock) {
        int[] realOwners = getRealOwners(wakeLock);
        int length = realOwners.length;
        int i = 0;
        while (i < length) {
            int realOwner = realOwners[i];
            PowerKeeperPolicy powerKeeperPolicy = sPolicy;
            if (powerKeeperPolicy == null || !powerKeeperPolicy.isWakelockDisabledByPolicy(wakeLock.mTag, realOwner)) {
                i++;
            } else {
                Slog.d(TAG, "wakeLock:[" + wakeLock.toString() + "] is disabled by policy");
                return true;
            }
        }
        return false;
    }

    private static boolean setWakeLockDisabledStateLocked(PowerManagerService.WakeLock wakeLock, boolean disabled) {
        if (wakeLock.mDisabled == disabled) {
            return false;
        }
        wakeLock.mDisabled = disabled;
        return true;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:2:0x000b, code lost:
        r0 = setWakeLockDisabledStateLocked(r3, isWakelockDisabledByPolicy(r3));
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    static void updateWakeLockDisabledStateLocked(com.android.server.power.PowerManagerService.WakeLock r3, boolean r4) {
        /*
            r0 = 0
            int r1 = r3.mFlags
            r2 = 65535(0xffff, float:9.1834E-41)
            r1 = r1 & r2
            r2 = 1
            if (r1 == r2) goto L_0x000b
            goto L_0x001a
        L_0x000b:
            boolean r1 = isWakelockDisabledByPolicy(r3)
            boolean r0 = setWakeLockDisabledStateLocked(r3, r1)
            if (r0 == 0) goto L_0x001a
            com.android.server.power.PowerManagerService r2 = sPowerManagerService
            r2.setWakeLockDirtyLocked()
        L_0x001a:
            if (r4 == 0) goto L_0x002c
            if (r0 == 0) goto L_0x002c
            boolean r1 = r3.mDisabled
            if (r1 == 0) goto L_0x002c
            com.android.server.power.PowerManagerService r1 = sPowerManagerService
            r1.notifyWakeLockReleasedLocked(r3)
            com.android.server.power.PowerManagerService r1 = sPowerManagerService
            r1.updatePowerStateLocked()
        L_0x002c:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.power.PowerManagerServiceInjector.updateWakeLockDisabledStateLocked(com.android.server.power.PowerManagerService$WakeLock, boolean):void");
    }

    public static void updateAllPartialWakeLockDisableState() {
        synchronized (sLock) {
            boolean changed = false;
            Iterator<PowerManagerService.WakeLock> it = sWakeLocks.iterator();
            while (it.hasNext()) {
                PowerManagerService.WakeLock wakeLock = it.next();
                if ((wakeLock.mFlags & 65535) == 1) {
                    changed |= setWakeLockDisabledStateLocked(wakeLock, isWakelockDisabledByPolicy(wakeLock));
                }
            }
            if (changed) {
                sPowerManagerService.setWakeLockDirtyLocked();
                sPowerManagerService.updatePowerStateLocked();
            }
        }
    }

    static void updateAllScreenWakeLockDisabledStateLocked() {
        Iterator<PowerManagerService.WakeLock> it = sWakeLocks.iterator();
        while (it.hasNext()) {
            PowerManagerService.WakeLock wakeLock = it.next();
            int i = wakeLock.mFlags & 65535;
            if ((i == 6 || i == 10 || i == 26) && wakeLock.mOwnerUid != 1000) {
                Slog.w(TAG, "screen wakeLock:[" + wakeLock.toString() + "] not by window manager");
            }
        }
        if (0 != 0) {
            sPowerManagerService.setWakeLockDirtyLocked();
        }
    }

    static void restoreScreenWakeLockDisabledState(int uid) {
        synchronized (sLock) {
            boolean changed = false;
            Iterator<PowerManagerService.WakeLock> it = sWakeLocks.iterator();
            while (it.hasNext()) {
                PowerManagerService.WakeLock wakeLock = it.next();
                int i = wakeLock.mFlags & 65535;
                if (i == 6 || i == 10 || i == 26) {
                    int[] realOwners = getRealOwners(wakeLock);
                    int length = realOwners.length;
                    int i2 = 0;
                    while (true) {
                        if (i2 < length) {
                            if (realOwners[i2] == uid && wakeLock.mDisabled) {
                                changed |= setWakeLockDisabledStateLocked(wakeLock, false);
                                sPowerManagerService.notifyWakeLockAcquiredLocked(wakeLock);
                                Slog.d(TAG, "screen wakeLock:[" + wakeLock.toString() + "] enabled");
                                break;
                            }
                            i2++;
                        } else {
                            break;
                        }
                    }
                }
            }
            if (changed) {
                sPowerManagerService.setWakeLockDirtyLocked();
                sPowerManagerService.updatePowerStateLocked();
            }
        }
    }

    public static void setUidPartialWakeLockDisabledState(int uid, String tag, boolean disabled) {
        if (tag != null || UserHandle.isApp(uid)) {
            synchronized (sLock) {
                Iterator<PowerManagerService.WakeLock> it = sWakeLocks.iterator();
                while (it.hasNext()) {
                    PowerManagerService.WakeLock wakeLock = it.next();
                    boolean changed = false;
                    if ((wakeLock.mFlags & 65535) == 1) {
                        int[] realOwners = getRealOwners(wakeLock);
                        int length = realOwners.length;
                        int i = 0;
                        while (true) {
                            if (i >= length) {
                                break;
                            } else if (realOwners[i] != uid || (tag != null && !tag.equals(wakeLock.mTag))) {
                                i++;
                            }
                        }
                        changed = setWakeLockDisabledStateLocked(wakeLock, disabled);
                    }
                    if (changed) {
                        if (wakeLock.mDisabled) {
                            Slog.d(TAG, "set partial wakelock disabled:[" + wakeLock.toString() + "]");
                            sPowerManagerService.notifyWakeLockReleasedLocked(wakeLock);
                        } else {
                            Slog.d(TAG, "set partial wakelock enabled:[" + wakeLock.toString() + "]");
                            sPowerManagerService.notifyWakeLockAcquiredLocked(wakeLock);
                        }
                        sPowerManagerService.setWakeLockDirtyLocked();
                        sPowerManagerService.updatePowerStateLocked();
                    }
                }
            }
            return;
        }
        throw new IllegalArgumentException("can not disable all wakelock for uid " + uid);
    }

    public static void recordShutDownTime() {
        File last_utime = new File("/cache/recovery/last_utime");
        if (!last_utime.exists()) {
            Slog.e(TAG, "last_utime doesn't exist");
            return;
        }
        try {
            BufferedReader reader = new BufferedReader(new FileReader(last_utime));
            String readLine = reader.readLine();
            String str = readLine;
            if (readLine == null) {
                Slog.e(TAG, "last_utime is blank");
                reader.close();
            } else if (Long.parseLong(str) <= 0) {
                Slog.e(TAG, "last_utime has invalid content");
                reader.close();
            } else {
                reader.close();
                File last_ShutdownTime = new File("/cache/recovery/last_shutdowntime");
                BufferedWriter writer = new BufferedWriter(new FileWriter(last_ShutdownTime));
                writer.write(Long.toString(System.currentTimeMillis()));
                writer.flush();
                writer.close();
                if (!last_ShutdownTime.setReadable(true, false)) {
                    last_ShutdownTime.delete();
                    Slog.e(TAG, "set last_shutdowntime readable failed");
                }
            }
        } catch (IOException ex) {
            Slog.e(TAG, ex.getMessage());
        }
    }

    public static boolean isShutdownOrRebootPermitted(final Context context, boolean shutdown, boolean confirm, String reason, boolean wait) {
        ContentResolver cr;
        if (context == null || (cr = context.getContentResolver()) == null || !shutdown || Settings.Global.getInt(cr, "com.xiaomi.system.devicelock.locked", 0) == 0) {
            return true;
        }
        Handler h = UiThread.getHandler();
        if (h != null) {
            h.post(new Runnable() {
                public void run() {
                    Toast.makeText(context, R.string.miui_forbid_poweroff_when_device_locked, 1).show();
                }
            });
        }
        return false;
    }
}
