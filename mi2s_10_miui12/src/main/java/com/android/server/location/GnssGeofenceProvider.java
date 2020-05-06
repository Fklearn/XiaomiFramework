package com.android.server.location;

import android.location.IGpsGeofenceHardware;
import android.util.Log;
import android.util.SparseArray;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.annotations.VisibleForTesting;

class GnssGeofenceProvider extends IGpsGeofenceHardware.Stub {
    private static final boolean DEBUG = Log.isLoggable(TAG, 3);
    private static final String TAG = "GnssGeofenceProvider";
    @GuardedBy({"mLock"})
    private final SparseArray<GeofenceEntry> mGeofenceEntries;
    private final Object mLock;
    @GuardedBy({"mLock"})
    private final GnssGeofenceProviderNative mNative;

    /* access modifiers changed from: private */
    public static native boolean native_add_geofence(int i, double d, double d2, double d3, int i2, int i3, int i4, int i5);

    /* access modifiers changed from: private */
    public static native boolean native_is_geofence_supported();

    /* access modifiers changed from: private */
    public static native boolean native_pause_geofence(int i);

    /* access modifiers changed from: private */
    public static native boolean native_remove_geofence(int i);

    /* access modifiers changed from: private */
    public static native boolean native_resume_geofence(int i, int i2);

    private static class GeofenceEntry {
        public int geofenceId;
        public int lastTransition;
        public double latitude;
        public double longitude;
        public int monitorTransitions;
        public int notificationResponsiveness;
        public boolean paused;
        public double radius;
        public int unknownTimer;

        private GeofenceEntry() {
        }
    }

    GnssGeofenceProvider() {
        this(new GnssGeofenceProviderNative());
    }

    @VisibleForTesting
    GnssGeofenceProvider(GnssGeofenceProviderNative gnssGeofenceProviderNative) {
        this.mLock = new Object();
        this.mGeofenceEntries = new SparseArray<>();
        this.mNative = gnssGeofenceProviderNative;
    }

    /* access modifiers changed from: package-private */
    public void resumeIfStarted() {
        if (DEBUG) {
            Log.d(TAG, "resumeIfStarted");
        }
        synchronized (this.mLock) {
            for (int i = 0; i < this.mGeofenceEntries.size(); i++) {
                GeofenceEntry entry = this.mGeofenceEntries.valueAt(i);
                if (this.mNative.addGeofence(entry.geofenceId, entry.latitude, entry.longitude, entry.radius, entry.lastTransition, entry.monitorTransitions, entry.notificationResponsiveness, entry.unknownTimer) && entry.paused) {
                    this.mNative.pauseGeofence(entry.geofenceId);
                }
            }
        }
    }

    public boolean isHardwareGeofenceSupported() {
        boolean isGeofenceSupported;
        synchronized (this.mLock) {
            isGeofenceSupported = this.mNative.isGeofenceSupported();
        }
        return isGeofenceSupported;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:41:0x0064, code lost:
        return r0;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean addCircularHardwareGeofence(int r17, double r18, double r20, double r22, int r24, int r25, int r26, int r27) {
        /*
            r16 = this;
            r1 = r16
            r14 = r17
            java.lang.Object r15 = r1.mLock
            monitor-enter(r15)
            com.android.server.location.GnssGeofenceProvider$GnssGeofenceProviderNative r2 = r1.mNative     // Catch:{ all -> 0x0065 }
            r3 = r17
            r4 = r18
            r6 = r20
            r8 = r22
            r10 = r24
            r11 = r25
            r12 = r26
            r13 = r27
            boolean r0 = r2.addGeofence(r3, r4, r6, r8, r10, r11, r12, r13)     // Catch:{ all -> 0x0065 }
            if (r0 == 0) goto L_0x0055
            com.android.server.location.GnssGeofenceProvider$GeofenceEntry r2 = new com.android.server.location.GnssGeofenceProvider$GeofenceEntry     // Catch:{ all -> 0x0065 }
            r3 = 0
            r2.<init>()     // Catch:{ all -> 0x0065 }
            r2.geofenceId = r14     // Catch:{ all -> 0x0065 }
            r3 = r18
            r2.latitude = r3     // Catch:{ all -> 0x0053 }
            r5 = r20
            r2.longitude = r5     // Catch:{ all -> 0x0051 }
            r7 = r22
            r2.radius = r7     // Catch:{ all -> 0x004f }
            r9 = r24
            r2.lastTransition = r9     // Catch:{ all -> 0x004d }
            r10 = r25
            r2.monitorTransitions = r10     // Catch:{ all -> 0x004b }
            r11 = r26
            r2.notificationResponsiveness = r11     // Catch:{ all -> 0x0049 }
            r12 = r27
            r2.unknownTimer = r12     // Catch:{ all -> 0x0076 }
            android.util.SparseArray<com.android.server.location.GnssGeofenceProvider$GeofenceEntry> r13 = r1.mGeofenceEntries     // Catch:{ all -> 0x0076 }
            r13.put(r14, r2)     // Catch:{ all -> 0x0076 }
            goto L_0x0063
        L_0x0049:
            r0 = move-exception
            goto L_0x0072
        L_0x004b:
            r0 = move-exception
            goto L_0x0070
        L_0x004d:
            r0 = move-exception
            goto L_0x006e
        L_0x004f:
            r0 = move-exception
            goto L_0x006c
        L_0x0051:
            r0 = move-exception
            goto L_0x006a
        L_0x0053:
            r0 = move-exception
            goto L_0x0068
        L_0x0055:
            r3 = r18
            r5 = r20
            r7 = r22
            r9 = r24
            r10 = r25
            r11 = r26
            r12 = r27
        L_0x0063:
            monitor-exit(r15)     // Catch:{ all -> 0x0076 }
            return r0
        L_0x0065:
            r0 = move-exception
            r3 = r18
        L_0x0068:
            r5 = r20
        L_0x006a:
            r7 = r22
        L_0x006c:
            r9 = r24
        L_0x006e:
            r10 = r25
        L_0x0070:
            r11 = r26
        L_0x0072:
            r12 = r27
        L_0x0074:
            monitor-exit(r15)     // Catch:{ all -> 0x0076 }
            throw r0
        L_0x0076:
            r0 = move-exception
            goto L_0x0074
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.location.GnssGeofenceProvider.addCircularHardwareGeofence(int, double, double, double, int, int, int, int):boolean");
    }

    public boolean removeHardwareGeofence(int geofenceId) {
        boolean removed;
        synchronized (this.mLock) {
            removed = this.mNative.removeGeofence(geofenceId);
            if (removed) {
                this.mGeofenceEntries.remove(geofenceId);
            }
        }
        return removed;
    }

    public boolean pauseHardwareGeofence(int geofenceId) {
        boolean paused;
        GeofenceEntry entry;
        synchronized (this.mLock) {
            paused = this.mNative.pauseGeofence(geofenceId);
            if (paused && (entry = this.mGeofenceEntries.get(geofenceId)) != null) {
                entry.paused = true;
            }
        }
        return paused;
    }

    public boolean resumeHardwareGeofence(int geofenceId, int monitorTransitions) {
        boolean resumed;
        GeofenceEntry entry;
        synchronized (this.mLock) {
            resumed = this.mNative.resumeGeofence(geofenceId, monitorTransitions);
            if (resumed && (entry = this.mGeofenceEntries.get(geofenceId)) != null) {
                entry.paused = false;
                entry.monitorTransitions = monitorTransitions;
            }
        }
        return resumed;
    }

    @VisibleForTesting
    static class GnssGeofenceProviderNative {
        GnssGeofenceProviderNative() {
        }

        public boolean isGeofenceSupported() {
            return GnssGeofenceProvider.native_is_geofence_supported();
        }

        public boolean addGeofence(int geofenceId, double latitude, double longitude, double radius, int lastTransition, int monitorTransitions, int notificationResponsiveness, int unknownTimer) {
            return GnssGeofenceProvider.native_add_geofence(geofenceId, latitude, longitude, radius, lastTransition, monitorTransitions, notificationResponsiveness, unknownTimer);
        }

        public boolean removeGeofence(int geofenceId) {
            return GnssGeofenceProvider.native_remove_geofence(geofenceId);
        }

        public boolean resumeGeofence(int geofenceId, int transitions) {
            return GnssGeofenceProvider.native_resume_geofence(geofenceId, transitions);
        }

        public boolean pauseGeofence(int geofenceId) {
            return GnssGeofenceProvider.native_pause_geofence(geofenceId);
        }
    }
}
