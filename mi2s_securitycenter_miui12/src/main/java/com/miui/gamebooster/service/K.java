package com.miui.gamebooster.service;

import com.miui.gamebooster.mutiwindow.j;
import com.miui.gamebooster.mutiwindow.l;

class K implements l.a {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ VideoToolBoxService f4789a;

    K(VideoToolBoxService videoToolBoxService) {
        this.f4789a = videoToolBoxService;
    }

    public j getId() {
        return j.VIDEO_TOOLBOX;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:40:0x00ef, code lost:
        return false;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean onForegroundInfoChanged(miui.process.ForegroundInfo r7) {
        /*
            r6 = this;
            int r0 = r7.mForegroundUid
            int r0 = b.b.c.j.B.c(r0)
            int r1 = b.b.c.j.B.j()
            r2 = 2
            r3 = 999(0x3e7, float:1.4E-42)
            r4 = 0
            if (r0 != r3) goto L_0x0014
            r5 = 10
            if (r1 == r5) goto L_0x0018
        L_0x0014:
            if (r0 == r3) goto L_0x002a
            if (r1 == r0) goto L_0x002a
        L_0x0018:
            com.miui.gamebooster.service.VideoToolBoxService r7 = r6.f4789a
            boolean r7 = r7.f
            if (r7 == 0) goto L_0x0029
            com.miui.gamebooster.service.VideoToolBoxService r7 = r6.f4789a
            android.os.Handler r7 = r7.f4799b
            r7.sendEmptyMessage(r2)
        L_0x0029:
            return r4
        L_0x002a:
            java.util.ArrayList r0 = com.miui.gamebooster.service.VideoToolBoxService.f4798a
            java.lang.String r1 = r7.mForegroundPackageName
            boolean r0 = r0.contains(r1)
            if (r0 != 0) goto L_0x00f3
            java.lang.String r0 = r7.mForegroundPackageName
            java.lang.String r1 = "com.xiaomi.gamecenter"
            boolean r0 = r0.startsWith(r1)
            if (r0 == 0) goto L_0x0042
            goto L_0x00f3
        L_0x0042:
            java.lang.String r0 = r7.mForegroundPackageName
            java.lang.String r1 = "com.android.systemui"
            boolean r0 = r0.startsWith(r1)
            if (r0 == 0) goto L_0x0057
            java.lang.String r0 = r7.mLastForegroundPackageName
            java.lang.String r1 = "com.miui.screenrecorder"
            boolean r0 = r0.startsWith(r1)
            if (r0 == 0) goto L_0x0057
            return r4
        L_0x0057:
            com.miui.gamebooster.service.VideoToolBoxService r0 = r6.f4789a
            java.lang.String r1 = r7.mForegroundPackageName
            java.lang.String unused = r0.k = r1
            boolean r0 = com.miui.gamebooster.videobox.utils.e.a()
            if (r0 != 0) goto L_0x006c
            java.lang.String r7 = "VideoToolBoxService"
            java.lang.String r0 = "vtb not support but service running"
            android.util.Log.e(r7, r0)
            return r4
        L_0x006c:
            com.miui.gamebooster.service.VideoToolBoxService r0 = r6.f4789a
            java.lang.Object r0 = r0.h
            monitor-enter(r0)
            com.miui.gamebooster.service.VideoToolBoxService r1 = r6.f4789a     // Catch:{ all -> 0x00f0 }
            boolean r1 = r1.f     // Catch:{ all -> 0x00f0 }
            if (r1 != 0) goto L_0x0084
            java.lang.String r7 = "VideoToolBoxService"
            java.lang.String r1 = "vtb is closed!!!"
            android.util.Log.i(r7, r1)     // Catch:{ all -> 0x00f0 }
            monitor-exit(r0)     // Catch:{ all -> 0x00f0 }
            return r4
        L_0x0084:
            com.miui.gamebooster.service.VideoToolBoxService r1 = r6.f4789a     // Catch:{ all -> 0x00f0 }
            java.util.ArrayList r1 = r1.i     // Catch:{ all -> 0x00f0 }
            java.lang.String r3 = r7.mForegroundPackageName     // Catch:{ all -> 0x00f0 }
            boolean r1 = r1.contains(r3)     // Catch:{ all -> 0x00f0 }
            if (r1 == 0) goto L_0x00d4
            com.miui.gamebooster.service.VideoToolBoxService r1 = r6.f4789a     // Catch:{ all -> 0x00f0 }
            android.content.Context r1 = r1.e     // Catch:{ all -> 0x00f0 }
            com.miui.gamebooster.service.VideoToolBoxService r2 = r6.f4789a     // Catch:{ all -> 0x00f0 }
            android.os.Handler r2 = r2.f4799b     // Catch:{ all -> 0x00f0 }
            com.miui.gamebooster.gbservices.H r1 = com.miui.gamebooster.gbservices.H.a((android.content.Context) r1, (android.os.Handler) r2)     // Catch:{ all -> 0x00f0 }
            boolean r2 = r7.isColdStart()     // Catch:{ all -> 0x00f0 }
            r1.a((boolean) r2)     // Catch:{ all -> 0x00f0 }
            java.lang.String r2 = r7.mForegroundPackageName     // Catch:{ all -> 0x00f0 }
            int r3 = r7.mForegroundUid     // Catch:{ all -> 0x00f0 }
            r1.a((java.lang.String) r2, (int) r3)     // Catch:{ all -> 0x00f0 }
            com.miui.gamebooster.service.VideoToolBoxService r1 = r6.f4789a     // Catch:{ all -> 0x00f0 }
            android.os.Handler r1 = r1.f4799b     // Catch:{ all -> 0x00f0 }
            r2 = 1
            r1.sendEmptyMessage(r2)     // Catch:{ all -> 0x00f0 }
            java.lang.String r1 = "VideoToolBoxService"
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ all -> 0x00f0 }
            r3.<init>()     // Catch:{ all -> 0x00f0 }
            java.lang.String r4 = "onForegroundInfoChanged: Enter Vtb pkg="
            r3.append(r4)     // Catch:{ all -> 0x00f0 }
            java.lang.String r7 = r7.mForegroundPackageName     // Catch:{ all -> 0x00f0 }
            r3.append(r7)     // Catch:{ all -> 0x00f0 }
            java.lang.String r7 = r3.toString()     // Catch:{ all -> 0x00f0 }
            android.util.Log.i(r1, r7)     // Catch:{ all -> 0x00f0 }
            monitor-exit(r0)     // Catch:{ all -> 0x00f0 }
            return r2
        L_0x00d4:
            com.miui.gamebooster.service.VideoToolBoxService r1 = r6.f4789a     // Catch:{ all -> 0x00f0 }
            java.lang.String r7 = r7.mForegroundPackageName     // Catch:{ all -> 0x00f0 }
            boolean r7 = r1.a((java.lang.String) r7)     // Catch:{ all -> 0x00f0 }
            if (r7 != 0) goto L_0x00ee
            com.miui.gamebooster.service.VideoToolBoxService r7 = r6.f4789a     // Catch:{ all -> 0x00f0 }
            android.os.Handler r7 = r7.f4799b     // Catch:{ all -> 0x00f0 }
            r7.sendEmptyMessage(r2)     // Catch:{ all -> 0x00f0 }
            java.lang.String r7 = "VideoToolBoxService"
            java.lang.String r1 = "onForegroundInfoChanged: Exit Vtb"
            android.util.Log.i(r7, r1)     // Catch:{ all -> 0x00f0 }
        L_0x00ee:
            monitor-exit(r0)     // Catch:{ all -> 0x00f0 }
            return r4
        L_0x00f0:
            r7 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x00f0 }
            throw r7
        L_0x00f3:
            return r4
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.gamebooster.service.K.onForegroundInfoChanged(miui.process.ForegroundInfo):boolean");
    }
}
