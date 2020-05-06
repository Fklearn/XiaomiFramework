package com.android.server.wifi;

import android.content.ContentResolver;
import android.content.Context;
import android.provider.Settings;
import java.io.FileDescriptor;
import java.io.PrintWriter;

public class WifiSettingsStore {
    static final int WIFI_DISABLED = 0;
    private static final int WIFI_DISABLED_AIRPLANE_ON = 3;
    static final int WIFI_ENABLED = 1;
    private static final int WIFI_ENABLED_AIRPLANE_OVERRIDE = 2;
    private boolean mAirplaneModeOn = false;
    private boolean mCheckSavedStateAtBoot = false;
    private final Context mContext;
    private int mPersistWifiState = 0;
    private boolean mScanAlwaysAvailable;

    WifiSettingsStore(Context context) {
        this.mContext = context;
        this.mAirplaneModeOn = getPersistedAirplaneModeOn();
        this.mPersistWifiState = getPersistedWifiState();
        this.mScanAlwaysAvailable = getPersistedScanAlwaysAvailable();
    }

    /* JADX WARNING: Code restructure failed: missing block: B:16:0x001d, code lost:
        return r1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:22:0x0025, code lost:
        return r1;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public synchronized boolean isWifiToggleEnabled() {
        /*
            r4 = this;
            monitor-enter(r4)
            boolean r0 = r4.mCheckSavedStateAtBoot     // Catch:{ all -> 0x0026 }
            r1 = 1
            if (r0 != 0) goto L_0x0010
            r4.mCheckSavedStateAtBoot = r1     // Catch:{ all -> 0x0026 }
            boolean r0 = r4.testAndClearWifiSavedState()     // Catch:{ all -> 0x0026 }
            if (r0 == 0) goto L_0x0010
            monitor-exit(r4)
            return r1
        L_0x0010:
            boolean r0 = r4.mAirplaneModeOn     // Catch:{ all -> 0x0026 }
            r2 = 0
            if (r0 == 0) goto L_0x001e
            int r0 = r4.mPersistWifiState     // Catch:{ all -> 0x0026 }
            r3 = 2
            if (r0 != r3) goto L_0x001b
            goto L_0x001c
        L_0x001b:
            r1 = r2
        L_0x001c:
            monitor-exit(r4)
            return r1
        L_0x001e:
            int r0 = r4.mPersistWifiState     // Catch:{ all -> 0x0026 }
            if (r0 == 0) goto L_0x0023
            goto L_0x0024
        L_0x0023:
            r1 = r2
        L_0x0024:
            monitor-exit(r4)
            return r1
        L_0x0026:
            r0 = move-exception
            monitor-exit(r4)
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.wifi.WifiSettingsStore.isWifiToggleEnabled():boolean");
    }

    public synchronized boolean isAirplaneModeOn() {
        return this.mAirplaneModeOn;
    }

    public synchronized boolean isScanAlwaysAvailable() {
        return !this.mAirplaneModeOn && this.mScanAlwaysAvailable;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:17:0x0025, code lost:
        return true;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public synchronized boolean handleWifiToggled(boolean r3) {
        /*
            r2 = this;
            monitor-enter(r2)
            boolean r0 = r2.mAirplaneModeOn     // Catch:{ all -> 0x0026 }
            r1 = 0
            if (r0 == 0) goto L_0x000e
            boolean r0 = r2.isAirplaneToggleable()     // Catch:{ all -> 0x0026 }
            if (r0 != 0) goto L_0x000e
            monitor-exit(r2)
            return r1
        L_0x000e:
            r0 = 1
            if (r3 == 0) goto L_0x001e
            boolean r1 = r2.mAirplaneModeOn     // Catch:{ all -> 0x0026 }
            if (r1 == 0) goto L_0x001a
            r1 = 2
            r2.persistWifiState(r1)     // Catch:{ all -> 0x0026 }
            goto L_0x0024
        L_0x001a:
            r2.persistWifiState(r0)     // Catch:{ all -> 0x0026 }
            goto L_0x0024
        L_0x001e:
            r2.persistWifiState(r1)     // Catch:{ all -> 0x0026 }
            r2.setWifiSavedState(r1)     // Catch:{ all -> 0x0026 }
        L_0x0024:
            monitor-exit(r2)
            return r0
        L_0x0026:
            r3 = move-exception
            monitor-exit(r2)
            throw r3
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.wifi.WifiSettingsStore.handleWifiToggled(boolean):boolean");
    }

    /* access modifiers changed from: package-private */
    /* JADX WARNING: Code restructure failed: missing block: B:21:0x0031, code lost:
        return true;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public synchronized boolean handleAirplaneModeToggled() {
        /*
            r4 = this;
            monitor-enter(r4)
            boolean r0 = r4.isAirplaneSensitive()     // Catch:{ all -> 0x0032 }
            if (r0 != 0) goto L_0x000a
            r0 = 0
            monitor-exit(r4)
            return r0
        L_0x000a:
            boolean r0 = r4.getPersistedAirplaneModeOn()     // Catch:{ all -> 0x0032 }
            r4.mAirplaneModeOn = r0     // Catch:{ all -> 0x0032 }
            boolean r0 = r4.mAirplaneModeOn     // Catch:{ all -> 0x0032 }
            r1 = 3
            r2 = 1
            if (r0 == 0) goto L_0x001e
            int r0 = r4.mPersistWifiState     // Catch:{ all -> 0x0032 }
            if (r0 != r2) goto L_0x0030
            r4.persistWifiState(r1)     // Catch:{ all -> 0x0032 }
            goto L_0x0030
        L_0x001e:
            boolean r0 = r4.testAndClearWifiSavedState()     // Catch:{ all -> 0x0032 }
            if (r0 != 0) goto L_0x002d
            int r0 = r4.mPersistWifiState     // Catch:{ all -> 0x0032 }
            r3 = 2
            if (r0 == r3) goto L_0x002d
            int r0 = r4.mPersistWifiState     // Catch:{ all -> 0x0032 }
            if (r0 != r1) goto L_0x0030
        L_0x002d:
            r4.persistWifiState(r2)     // Catch:{ all -> 0x0032 }
        L_0x0030:
            monitor-exit(r4)
            return r2
        L_0x0032:
            r0 = move-exception
            monitor-exit(r4)
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.wifi.WifiSettingsStore.handleAirplaneModeToggled():boolean");
    }

    /* access modifiers changed from: package-private */
    public synchronized void handleWifiScanAlwaysAvailableToggled() {
        this.mScanAlwaysAvailable = getPersistedScanAlwaysAvailable();
    }

    /* access modifiers changed from: package-private */
    public void dump(FileDescriptor fd, PrintWriter pw, String[] args) {
        pw.println("mPersistWifiState " + this.mPersistWifiState);
        pw.println("mAirplaneModeOn " + this.mAirplaneModeOn);
    }

    private void persistWifiState(int state) {
        ContentResolver cr = this.mContext.getContentResolver();
        this.mPersistWifiState = state;
        Settings.Global.putInt(cr, "wifi_on", state);
    }

    private boolean isAirplaneSensitive() {
        String airplaneModeRadios = Settings.Global.getString(this.mContext.getContentResolver(), "airplane_mode_radios");
        return airplaneModeRadios == null || airplaneModeRadios.contains("wifi");
    }

    private boolean isAirplaneToggleable() {
        String toggleableRadios = Settings.Global.getString(this.mContext.getContentResolver(), "airplane_mode_toggleable_radios");
        return toggleableRadios != null && toggleableRadios.contains("wifi");
    }

    private boolean testAndClearWifiSavedState() {
        int wifiSavedState = getWifiSavedState();
        if (wifiSavedState == 1) {
            setWifiSavedState(0);
        }
        if (wifiSavedState == 1) {
            return true;
        }
        return false;
    }

    public void setWifiSavedState(int state) {
        Settings.Global.putInt(this.mContext.getContentResolver(), "wifi_saved_state", state);
    }

    public int getWifiSavedState() {
        try {
            return Settings.Global.getInt(this.mContext.getContentResolver(), "wifi_saved_state");
        } catch (Settings.SettingNotFoundException e) {
            return 0;
        }
    }

    private int getPersistedWifiState() {
        ContentResolver cr = this.mContext.getContentResolver();
        try {
            return Settings.Global.getInt(cr, "wifi_on");
        } catch (Settings.SettingNotFoundException e) {
            Settings.Global.putInt(cr, "wifi_on", 0);
            return 0;
        }
    }

    private boolean getPersistedAirplaneModeOn() {
        return Settings.Global.getInt(this.mContext.getContentResolver(), "airplane_mode_on", 0) == 1;
    }

    private boolean getPersistedScanAlwaysAvailable() {
        return Settings.Global.getInt(this.mContext.getContentResolver(), "wifi_scan_always_enabled", 0) == 1;
    }
}
