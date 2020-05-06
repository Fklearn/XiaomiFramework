package com.miui.applicationlock.a;

import java.util.HashMap;
import java.util.Map;
import miui.util.Log;

class g implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ long f3248a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ long f3249b;

    g(long j, long j2) {
        this.f3248a = j;
        this.f3249b = j2;
    }

    public void run() {
        long j = this.f3248a;
        if (j > 0) {
            long j2 = this.f3249b;
            if (j2 > 0) {
                float f = ((float) j) / ((float) j2);
                float f2 = 1.0f;
                if (f <= 0.0f || ((double) f) > 0.05d) {
                    double d2 = (double) f;
                    if (d2 > 0.05d && d2 <= 0.1d) {
                        f2 = 0.1f;
                    } else if (d2 > 0.1d && d2 <= 0.15d) {
                        f2 = 0.15f;
                    } else if (d2 > 0.15d && d2 <= 0.2d) {
                        f2 = 0.2f;
                    } else if (d2 > 0.2d && d2 <= 0.25d) {
                        f2 = 0.25f;
                    } else if (d2 > 0.25d && d2 <= 0.3d) {
                        f2 = 0.3f;
                    } else if (d2 > 0.3d && d2 <= 0.35d) {
                        f2 = 0.35f;
                    } else if (d2 > 0.35d && d2 <= 0.4d) {
                        f2 = 0.4f;
                    } else if (d2 > 0.4d && d2 <= 0.45d) {
                        f2 = 0.45f;
                    } else if (d2 > 0.45d && d2 <= 0.5d) {
                        f2 = 0.5f;
                    } else if (d2 > 0.5d && d2 <= 0.55d) {
                        f2 = 0.55f;
                    } else if (d2 > 0.55d && d2 <= 0.6d) {
                        f2 = 0.6f;
                    } else if (d2 > 0.6d && d2 <= 0.65d) {
                        f2 = 0.65f;
                    } else if (d2 > 0.65d && d2 <= 0.7d) {
                        f2 = 0.7f;
                    } else if (d2 > 0.7d && d2 <= 0.75d) {
                        f2 = 0.75f;
                    } else if (d2 > 0.75d && d2 <= 0.8d) {
                        f2 = 0.8f;
                    } else if (d2 > 0.8d && d2 <= 0.85d) {
                        f2 = 0.85f;
                    } else if (d2 > 0.85d && d2 <= 0.9d) {
                        f2 = 0.9f;
                    } else if (d2 > 0.9d && d2 <= 0.95d) {
                        f2 = 0.95f;
                    }
                } else {
                    f2 = 0.05f;
                }
                Log.d("AnalyticHelper", "watched percent: " + f2);
                HashMap hashMap = new HashMap(1);
                hashMap.put("video_watched_percent", String.valueOf(f2));
                h.b("applock_video_watched", (Map<String, String>) hashMap);
            }
        }
    }
}
