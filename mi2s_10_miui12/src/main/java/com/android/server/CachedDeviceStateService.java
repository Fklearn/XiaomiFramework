package com.android.server;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.os.BatteryManagerInternal;
import android.os.PowerManager;
import android.util.Slog;
import com.android.internal.os.CachedDeviceState;

public class CachedDeviceStateService extends SystemService {
    private static final String TAG = "CachedDeviceStateService";
    private final BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        /* JADX WARNING: Removed duplicated region for block: B:17:0x003c  */
        /* JADX WARNING: Removed duplicated region for block: B:21:0x0055  */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void onReceive(android.content.Context r7, android.content.Intent r8) {
            /*
                r6 = this;
                java.lang.String r0 = r8.getAction()
                int r1 = r0.hashCode()
                r2 = -2128145023(0xffffffff81271581, float:-3.0688484E-38)
                r3 = 2
                r4 = 0
                r5 = 1
                if (r1 == r2) goto L_0x002f
                r2 = -1538406691(0xffffffffa44dc6dd, float:-4.4620733E-17)
                if (r1 == r2) goto L_0x0025
                r2 = -1454123155(0xffffffffa953d76d, float:-4.7038264E-14)
                if (r1 == r2) goto L_0x001b
            L_0x001a:
                goto L_0x0039
            L_0x001b:
                java.lang.String r1 = "android.intent.action.SCREEN_ON"
                boolean r0 = r0.equals(r1)
                if (r0 == 0) goto L_0x001a
                r0 = r5
                goto L_0x003a
            L_0x0025:
                java.lang.String r1 = "android.intent.action.BATTERY_CHANGED"
                boolean r0 = r0.equals(r1)
                if (r0 == 0) goto L_0x001a
                r0 = r4
                goto L_0x003a
            L_0x002f:
                java.lang.String r1 = "android.intent.action.SCREEN_OFF"
                boolean r0 = r0.equals(r1)
                if (r0 == 0) goto L_0x001a
                r0 = r3
                goto L_0x003a
            L_0x0039:
                r0 = -1
            L_0x003a:
                if (r0 == 0) goto L_0x0055
                if (r0 == r5) goto L_0x004b
                if (r0 == r3) goto L_0x0041
                goto L_0x0069
            L_0x0041:
                com.android.server.CachedDeviceStateService r0 = com.android.server.CachedDeviceStateService.this
                com.android.internal.os.CachedDeviceState r0 = r0.mDeviceState
                r0.setScreenInteractive(r4)
                goto L_0x0069
            L_0x004b:
                com.android.server.CachedDeviceStateService r0 = com.android.server.CachedDeviceStateService.this
                com.android.internal.os.CachedDeviceState r0 = r0.mDeviceState
                r0.setScreenInteractive(r5)
                goto L_0x0069
            L_0x0055:
                com.android.server.CachedDeviceStateService r0 = com.android.server.CachedDeviceStateService.this
                com.android.internal.os.CachedDeviceState r0 = r0.mDeviceState
                java.lang.String r1 = "plugged"
                int r1 = r8.getIntExtra(r1, r4)
                if (r1 == 0) goto L_0x0065
                r4 = r5
            L_0x0065:
                r0.setCharging(r4)
            L_0x0069:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.server.CachedDeviceStateService.AnonymousClass1.onReceive(android.content.Context, android.content.Intent):void");
        }
    };
    /* access modifiers changed from: private */
    public final CachedDeviceState mDeviceState = new CachedDeviceState();

    public CachedDeviceStateService(Context context) {
        super(context);
    }

    public void onStart() {
        publishLocalService(CachedDeviceState.Readonly.class, this.mDeviceState.getReadonlyClient());
    }

    public void onBootPhase(int phase) {
        if (500 == phase) {
            IntentFilter filter = new IntentFilter();
            filter.addAction("android.intent.action.BATTERY_CHANGED");
            filter.addAction("android.intent.action.SCREEN_ON");
            filter.addAction("android.intent.action.SCREEN_OFF");
            filter.setPriority(1000);
            getContext().registerReceiver(this.mBroadcastReceiver, filter);
            this.mDeviceState.setCharging(queryIsCharging());
            this.mDeviceState.setScreenInteractive(queryScreenInteractive(getContext()));
        }
    }

    private boolean queryIsCharging() {
        BatteryManagerInternal batteryManager = (BatteryManagerInternal) LocalServices.getService(BatteryManagerInternal.class);
        if (batteryManager == null) {
            Slog.wtf(TAG, "BatteryManager null while starting CachedDeviceStateService");
            return true;
        } else if (batteryManager.getPlugType() != 0) {
            return true;
        } else {
            return false;
        }
    }

    private boolean queryScreenInteractive(Context context) {
        PowerManager powerManager = (PowerManager) context.getSystemService(PowerManager.class);
        if (powerManager != null) {
            return powerManager.isInteractive();
        }
        Slog.wtf(TAG, "PowerManager null while starting CachedDeviceStateService");
        return false;
    }
}
