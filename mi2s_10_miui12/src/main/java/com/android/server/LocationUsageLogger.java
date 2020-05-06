package com.android.server;

import android.os.SystemClock;
import android.util.Log;
import android.util.StatsLog;
import com.android.server.am.ActivityManagerServiceInjector;
import com.android.server.job.controllers.JobStatus;
import java.time.Instant;

class LocationUsageLogger {
    private static final int API_USAGE_LOG_HOURLY_CAP = 60;
    private static final boolean D = Log.isLoggable(TAG, 3);
    private static final int ONE_HOUR_IN_MILLIS = 3600000;
    private static final int ONE_MINUTE_IN_MILLIS = 60000;
    private static final int ONE_SEC_IN_MILLIS = 1000;
    private static final String TAG = "LocationUsageLogger";
    private int mApiUsageLogHourlyCount = 0;
    private long mLastApiUsageLogHour = 0;

    LocationUsageLogger() {
    }

    private static int providerNameToStatsdEnum(String provider) {
        if ("network".equals(provider)) {
            return 1;
        }
        if ("gps".equals(provider)) {
            return 2;
        }
        if ("passive".equals(provider)) {
            return 3;
        }
        if ("fused".equals(provider)) {
            return 4;
        }
        return 0;
    }

    private static int bucketizeIntervalToStatsdEnum(long interval) {
        if (interval < 1000) {
            return 1;
        }
        if (interval < 5000) {
            return 2;
        }
        if (interval < 60000) {
            return 3;
        }
        if (interval < 600000) {
            return 4;
        }
        if (interval < 3600000) {
            return 5;
        }
        return 6;
    }

    private static int bucketizeSmallestDisplacementToStatsdEnum(float smallestDisplacement) {
        if (smallestDisplacement == 0.0f) {
            return 1;
        }
        if (smallestDisplacement <= 0.0f || smallestDisplacement > 100.0f) {
            return 3;
        }
        return 2;
    }

    private static int bucketizeRadiusToStatsdEnum(float radius) {
        if (radius < 0.0f) {
            return 7;
        }
        if (radius < 100.0f) {
            return 1;
        }
        if (radius < 200.0f) {
            return 2;
        }
        if (radius < 300.0f) {
            return 3;
        }
        if (radius < 1000.0f) {
            return 4;
        }
        if (radius < 10000.0f) {
            return 5;
        }
        return 6;
    }

    private static int getBucketizedExpireIn(long expireAt) {
        if (expireAt == JobStatus.NO_LATEST_RUNTIME) {
            return 6;
        }
        long expireIn = Math.max(0, expireAt - SystemClock.elapsedRealtime());
        if (expireIn < ActivityManagerServiceInjector.KEEP_FOREGROUND_DURATION) {
            return 1;
        }
        if (expireIn < 60000) {
            return 2;
        }
        if (expireIn < 600000) {
            return 3;
        }
        if (expireIn < 3600000) {
            return 4;
        }
        return 5;
    }

    private static int categorizeActivityImportance(int importance) {
        if (importance == 100) {
            return 1;
        }
        if (importance == 125) {
            return 2;
        }
        return 3;
    }

    private static int getCallbackType(int apiType, boolean hasListener, boolean hasIntent) {
        if (apiType == 5) {
            return 1;
        }
        if (hasIntent) {
            return 3;
        }
        if (hasListener) {
            return 2;
        }
        return 0;
    }

    private boolean checkApiUsageLogCap() {
        if (D) {
            Log.d(TAG, "checking APIUsage log cap.");
        }
        long currentHour = Instant.now().toEpochMilli() / 3600000;
        if (currentHour > this.mLastApiUsageLogHour) {
            this.mLastApiUsageLogHour = currentHour;
            this.mApiUsageLogHourlyCount = 0;
            return true;
        }
        this.mApiUsageLogHourlyCount = Math.min(this.mApiUsageLogHourlyCount + 1, 60);
        if (this.mApiUsageLogHourlyCount < 60) {
            return true;
        }
        return false;
    }

    /* JADX WARNING: Removed duplicated region for block: B:61:0x00e8 A[Catch:{ Exception -> 0x0112 }] */
    /* JADX WARNING: Removed duplicated region for block: B:62:0x00eb A[Catch:{ Exception -> 0x0112 }] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void logLocationApiUsage(int r25, int r26, java.lang.String r27, android.location.LocationRequest r28, boolean r29, boolean r30, android.location.Geofence r31, int r32) {
        /*
            r24 = this;
            r15 = r25
            r14 = r26
            r13 = r29
            r12 = r30
            java.lang.String r11 = "LocationUsageLogger"
            boolean r0 = r24.checkApiUsageLogCap()     // Catch:{ Exception -> 0x0112 }
            if (r0 != 0) goto L_0x0011
            return
        L_0x0011:
            r0 = 1
            r1 = 0
            if (r28 != 0) goto L_0x0017
            r2 = r0
            goto L_0x0018
        L_0x0017:
            r2 = r1
        L_0x0018:
            r16 = r2
            if (r31 != 0) goto L_0x001e
            r2 = r0
            goto L_0x001f
        L_0x001e:
            r2 = r1
        L_0x001f:
            r17 = r2
            boolean r2 = D     // Catch:{ Exception -> 0x0112 }
            if (r2 == 0) goto L_0x0091
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x008c }
            r2.<init>()     // Catch:{ Exception -> 0x008c }
            java.lang.String r3 = "log API Usage to statsd. usageType: "
            r2.append(r3)     // Catch:{ Exception -> 0x008c }
            r2.append(r15)     // Catch:{ Exception -> 0x008c }
            java.lang.String r3 = ", apiInUse: "
            r2.append(r3)     // Catch:{ Exception -> 0x008c }
            r2.append(r14)     // Catch:{ Exception -> 0x008c }
            java.lang.String r3 = ", packageName: "
            r2.append(r3)     // Catch:{ Exception -> 0x008c }
            java.lang.String r3 = ""
            if (r27 != 0) goto L_0x0046
            r4 = r3
            goto L_0x0048
        L_0x0046:
            r4 = r27
        L_0x0048:
            r2.append(r4)     // Catch:{ Exception -> 0x008c }
            java.lang.String r4 = ", locationRequest: "
            r2.append(r4)     // Catch:{ Exception -> 0x008c }
            if (r16 == 0) goto L_0x0054
            r4 = r3
            goto L_0x0058
        L_0x0054:
            java.lang.String r4 = r28.toString()     // Catch:{ Exception -> 0x008c }
        L_0x0058:
            r2.append(r4)     // Catch:{ Exception -> 0x008c }
            java.lang.String r4 = ", hasListener: "
            r2.append(r4)     // Catch:{ Exception -> 0x008c }
            r2.append(r13)     // Catch:{ Exception -> 0x008c }
            java.lang.String r4 = ", hasIntent: "
            r2.append(r4)     // Catch:{ Exception -> 0x008c }
            r2.append(r12)     // Catch:{ Exception -> 0x008c }
            java.lang.String r4 = ", geofence: "
            r2.append(r4)     // Catch:{ Exception -> 0x008c }
            if (r17 == 0) goto L_0x0073
            goto L_0x0077
        L_0x0073:
            java.lang.String r3 = r31.toString()     // Catch:{ Exception -> 0x008c }
        L_0x0077:
            r2.append(r3)     // Catch:{ Exception -> 0x008c }
            java.lang.String r3 = ", importance: "
            r2.append(r3)     // Catch:{ Exception -> 0x008c }
            r9 = r32
            r2.append(r9)     // Catch:{ Exception -> 0x0112 }
            java.lang.String r2 = r2.toString()     // Catch:{ Exception -> 0x0112 }
            android.util.Log.d(r11, r2)     // Catch:{ Exception -> 0x0112 }
            goto L_0x0093
        L_0x008c:
            r0 = move-exception
            r9 = r32
            goto L_0x0113
        L_0x0091:
            r9 = r32
        L_0x0093:
            r2 = 210(0xd2, float:2.94E-43)
            if (r16 == 0) goto L_0x0099
            r5 = r1
            goto L_0x00a2
        L_0x0099:
            java.lang.String r3 = r28.getProvider()     // Catch:{ Exception -> 0x0112 }
            int r3 = providerNameToStatsdEnum(r3)     // Catch:{ Exception -> 0x0112 }
            r5 = r3
        L_0x00a2:
            if (r16 == 0) goto L_0x00a6
            r6 = r1
            goto L_0x00ab
        L_0x00a6:
            int r3 = r28.getQuality()     // Catch:{ Exception -> 0x0112 }
            r6 = r3
        L_0x00ab:
            if (r16 == 0) goto L_0x00af
            r7 = r1
            goto L_0x00b8
        L_0x00af:
            long r3 = r28.getInterval()     // Catch:{ Exception -> 0x0112 }
            int r3 = bucketizeIntervalToStatsdEnum(r3)     // Catch:{ Exception -> 0x0112 }
            r7 = r3
        L_0x00b8:
            if (r16 == 0) goto L_0x00bc
            r8 = r1
            goto L_0x00c6
        L_0x00bc:
            float r3 = r28.getSmallestDisplacement()     // Catch:{ Exception -> 0x0112 }
            int r3 = bucketizeSmallestDisplacementToStatsdEnum(r3)     // Catch:{ Exception -> 0x0112 }
            r8 = r3
        L_0x00c6:
            if (r16 == 0) goto L_0x00cd
            r3 = 0
        L_0x00ca:
            r18 = r3
            goto L_0x00d3
        L_0x00cd:
            int r3 = r28.getNumUpdates()     // Catch:{ Exception -> 0x0112 }
            long r3 = (long) r3     // Catch:{ Exception -> 0x0112 }
            goto L_0x00ca
        L_0x00d3:
            if (r16 != 0) goto L_0x00e1
            if (r15 != r0) goto L_0x00d8
            goto L_0x00e1
        L_0x00d8:
            long r3 = r28.getExpireAt()     // Catch:{ Exception -> 0x0112 }
            int r0 = getBucketizedExpireIn(r3)     // Catch:{ Exception -> 0x0112 }
            goto L_0x00e2
        L_0x00e1:
            r0 = r1
        L_0x00e2:
            int r20 = getCallbackType(r14, r13, r12)     // Catch:{ Exception -> 0x0112 }
            if (r17 == 0) goto L_0x00eb
            r21 = r1
            goto L_0x00f5
        L_0x00eb:
            float r1 = r31.getRadius()     // Catch:{ Exception -> 0x0112 }
            int r1 = bucketizeRadiusToStatsdEnum(r1)     // Catch:{ Exception -> 0x0112 }
            r21 = r1
        L_0x00f5:
            int r22 = categorizeActivityImportance(r32)     // Catch:{ Exception -> 0x0112 }
            r1 = r2
            r2 = r25
            r3 = r26
            r4 = r27
            r9 = r18
            r23 = r11
            r11 = r0
            r12 = r20
            r13 = r21
            r14 = r22
            android.util.StatsLog.write(r1, r2, r3, r4, r5, r6, r7, r8, r9, r11, r12, r13, r14)     // Catch:{ Exception -> 0x0110 }
            goto L_0x011c
        L_0x0110:
            r0 = move-exception
            goto L_0x0115
        L_0x0112:
            r0 = move-exception
        L_0x0113:
            r23 = r11
        L_0x0115:
            java.lang.String r1 = "Failed to log API usage to statsd."
            r2 = r23
            android.util.Log.w(r2, r1, r0)
        L_0x011c:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.LocationUsageLogger.logLocationApiUsage(int, int, java.lang.String, android.location.LocationRequest, boolean, boolean, android.location.Geofence, int):void");
    }

    public void logLocationApiUsage(int usageType, int apiInUse, String providerName) {
        String str;
        StringBuilder sb;
        int i = apiInUse;
        try {
            if (checkApiUsageLogCap()) {
                if (D) {
                    try {
                        sb = new StringBuilder();
                        sb.append("log API Usage to statsd. usageType: ");
                    } catch (Exception e) {
                        e = e;
                        int i2 = usageType;
                        String str2 = providerName;
                        str = TAG;
                        Log.w(str, "Failed to log API usage to statsd.", e);
                    }
                    try {
                        sb.append(usageType);
                        sb.append(", apiInUse: ");
                        sb.append(i);
                        sb.append(", providerName: ");
                        sb.append(providerName);
                        Log.d(TAG, sb.toString());
                    } catch (Exception e2) {
                        e = e2;
                        String str22 = providerName;
                        str = TAG;
                        Log.w(str, "Failed to log API usage to statsd.", e);
                    }
                } else {
                    int i3 = usageType;
                    String str3 = providerName;
                }
                int providerNameToStatsdEnum = providerNameToStatsdEnum(providerName);
                int i4 = usageType;
                int i5 = apiInUse;
                int callbackType = getCallbackType(i, true, true);
                str = TAG;
                try {
                    StatsLog.write(210, i4, i5, (String) null, providerNameToStatsdEnum, 0, 0, 0, 0, 0, callbackType, 0, 0);
                } catch (Exception e3) {
                    e = e3;
                }
            }
        } catch (Exception e4) {
            e = e4;
            str = TAG;
            Log.w(str, "Failed to log API usage to statsd.", e);
        }
    }
}
