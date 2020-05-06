package com.miui.gamebooster.service;

import com.miui.gamebooster.mutiwindow.j;
import com.miui.gamebooster.mutiwindow.l;

/* renamed from: com.miui.gamebooster.service.d  reason: case insensitive filesystem */
class C0403d implements l.a {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ GameBoosterService f4810a;

    C0403d(GameBoosterService gameBoosterService) {
        this.f4810a = gameBoosterService;
    }

    public j getId() {
        return j.GAMEBOOSTER;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:64:0x0223, code lost:
        return false;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean onForegroundInfoChanged(miui.process.ForegroundInfo r7) {
        /*
            r6 = this;
            boolean r0 = com.miui.securityscan.c.a.f7625a
            if (r0 == 0) goto L_0x0024
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r1 = "onForegroundInfoChanged"
            r0.append(r1)
            com.miui.gamebooster.service.GameBoosterService r1 = r6.f4810a
            java.util.ArrayList r1 = r1.r
            java.lang.String r1 = r1.toString()
            r0.append(r1)
            java.lang.String r0 = r0.toString()
            java.lang.String r1 = "GameBoosterService"
            android.util.Log.i(r1, r0)
        L_0x0024:
            int r0 = r7.mForegroundUid
            int r0 = b.b.c.j.B.c(r0)
            int r1 = r7.mLastForegroundUid
            b.b.c.j.B.c(r1)
            int r1 = b.b.c.j.B.j()
            r2 = 999(0x3e7, float:1.4E-42)
            r3 = 0
            if (r0 != r2) goto L_0x003c
            r4 = 10
            if (r1 == r4) goto L_0x0040
        L_0x003c:
            if (r0 == r2) goto L_0x0052
            if (r1 == r0) goto L_0x0052
        L_0x0040:
            com.miui.gamebooster.service.GameBoosterService r7 = r6.f4810a
            java.lang.Boolean r7 = r7.v
            boolean r7 = r7.booleanValue()
            if (r7 == 0) goto L_0x0051
            com.miui.gamebooster.service.GameBoosterService r7 = r6.f4810a
            r7.h()
        L_0x0051:
            return r3
        L_0x0052:
            boolean r0 = com.miui.gamebooster.m.C0375f.a()
            if (r0 == 0) goto L_0x0059
            return r3
        L_0x0059:
            java.util.ArrayList r0 = com.miui.gamebooster.service.GameBoosterService.f4758a
            java.lang.String r1 = r7.mForegroundPackageName
            boolean r0 = r0.contains(r1)
            if (r0 != 0) goto L_0x0229
            java.lang.String r0 = r7.mForegroundPackageName
            java.lang.String r1 = "com.xiaomi.gamecenter"
            boolean r0 = r0.startsWith(r1)
            if (r0 == 0) goto L_0x0071
            goto L_0x0229
        L_0x0071:
            java.lang.String r0 = r7.mForegroundPackageName
            java.lang.String r1 = "com.android.systemui"
            boolean r0 = r0.startsWith(r1)
            if (r0 == 0) goto L_0x0086
            java.lang.String r0 = r7.mLastForegroundPackageName
            java.lang.String r1 = "com.miui.screenrecorder"
            boolean r0 = r0.startsWith(r1)
            if (r0 == 0) goto L_0x0086
            return r3
        L_0x0086:
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r1 = "onForegroundInfoChanged: Cur="
            r0.append(r1)
            java.lang.String r1 = r7.mForegroundPackageName
            r0.append(r1)
            java.lang.String r1 = "\t last="
            r0.append(r1)
            java.lang.String r1 = r7.mLastForegroundPackageName
            r0.append(r1)
            java.lang.String r0 = r0.toString()
            java.lang.String r1 = "GameBoosterService"
            android.util.Log.i(r1, r0)
            com.miui.gamebooster.service.GameBoosterService r0 = r6.f4810a
            java.lang.Object r0 = r0.z
            monitor-enter(r0)
            com.miui.gamebooster.service.GameBoosterService r1 = r6.f4810a     // Catch:{ all -> 0x0226 }
            java.lang.Boolean r1 = r1.v     // Catch:{ all -> 0x0226 }
            boolean r1 = r1.booleanValue()     // Catch:{ all -> 0x0226 }
            if (r1 == 0) goto L_0x0224
            com.miui.gamebooster.service.GameBoosterService r1 = r6.f4810a     // Catch:{ all -> 0x0226 }
            java.util.ArrayList r1 = r1.r     // Catch:{ all -> 0x0226 }
            int r1 = r1.size()     // Catch:{ all -> 0x0226 }
            r2 = 1
            if (r1 <= 0) goto L_0x016a
            com.miui.gamebooster.service.GameBoosterService r1 = r6.f4810a     // Catch:{ all -> 0x0226 }
            java.util.ArrayList r1 = r1.r     // Catch:{ all -> 0x0226 }
            java.lang.String r4 = r7.mForegroundPackageName     // Catch:{ all -> 0x0226 }
            boolean r1 = r1.contains(r4)     // Catch:{ all -> 0x0226 }
            if (r1 == 0) goto L_0x016a
            java.lang.String r1 = "GameBoosterService"
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ all -> 0x0226 }
            r3.<init>()     // Catch:{ all -> 0x0226 }
            java.lang.String r4 = "gameStartDelay foreground:"
            r3.append(r4)     // Catch:{ all -> 0x0226 }
            java.lang.String r4 = r7.toString()     // Catch:{ all -> 0x0226 }
            r3.append(r4)     // Catch:{ all -> 0x0226 }
            java.lang.String r3 = r3.toString()     // Catch:{ all -> 0x0226 }
            android.util.Log.d(r1, r3)     // Catch:{ all -> 0x0226 }
            com.miui.gamebooster.service.GameBoosterService r1 = r6.f4810a     // Catch:{ all -> 0x0226 }
            long r3 = android.os.SystemClock.elapsedRealtime()     // Catch:{ all -> 0x0226 }
            long unused = r1.f4759b = r3     // Catch:{ all -> 0x0226 }
            com.miui.gamebooster.service.GameBoosterService r1 = r6.f4810a     // Catch:{ all -> 0x0226 }
            boolean r1 = r1.w     // Catch:{ all -> 0x0226 }
            if (r1 == 0) goto L_0x011d
            com.miui.gamebooster.service.GameBoosterService r1 = r6.f4810a     // Catch:{ all -> 0x0226 }
            boolean r1 = r1.x     // Catch:{ all -> 0x0226 }
            if (r1 == 0) goto L_0x011d
            java.lang.String r1 = r7.mForegroundPackageName     // Catch:{ all -> 0x0226 }
            int r7 = r7.mForegroundUid     // Catch:{ all -> 0x0226 }
            com.miui.gamebooster.service.GameBoosterService r3 = r6.f4810a     // Catch:{ all -> 0x0226 }
            android.os.Handler r3 = r3.i     // Catch:{ all -> 0x0226 }
            com.miui.gamebooster.service.c r4 = new com.miui.gamebooster.service.c     // Catch:{ all -> 0x0226 }
            r4.<init>(r6, r1, r7)     // Catch:{ all -> 0x0226 }
            r3.post(r4)     // Catch:{ all -> 0x0226 }
            monitor-exit(r0)     // Catch:{ all -> 0x0226 }
            return r2
        L_0x011d:
            com.miui.gamebooster.service.GameBoosterService r1 = r6.f4810a     // Catch:{ all -> 0x0226 }
            com.miui.gamebooster.service.GameBoosterService r3 = r6.f4810a     // Catch:{ all -> 0x0226 }
            android.os.Handler r3 = r3.i     // Catch:{ all -> 0x0226 }
            com.miui.gamebooster.service.r r1 = com.miui.gamebooster.service.r.a((android.content.Context) r1, (android.os.Handler) r3)     // Catch:{ all -> 0x0226 }
            com.miui.gamebooster.d.b r3 = com.miui.gamebooster.d.b.GAME     // Catch:{ all -> 0x0226 }
            r1.a((com.miui.gamebooster.d.b) r3)     // Catch:{ all -> 0x0226 }
            com.miui.gamebooster.service.GameBoosterService r1 = r6.f4810a     // Catch:{ all -> 0x0226 }
            com.miui.gamebooster.service.GameBoosterService r3 = r6.f4810a     // Catch:{ all -> 0x0226 }
            android.os.Handler r3 = r3.i     // Catch:{ all -> 0x0226 }
            com.miui.gamebooster.service.r r1 = com.miui.gamebooster.service.r.a((android.content.Context) r1, (android.os.Handler) r3)     // Catch:{ all -> 0x0226 }
            java.lang.String r3 = r7.mForegroundPackageName     // Catch:{ all -> 0x0226 }
            r1.a((java.lang.String) r3)     // Catch:{ all -> 0x0226 }
            com.miui.gamebooster.service.GameBoosterService r1 = r6.f4810a     // Catch:{ all -> 0x0226 }
            com.miui.gamebooster.service.GameBoosterService r3 = r6.f4810a     // Catch:{ all -> 0x0226 }
            android.os.Handler r3 = r3.i     // Catch:{ all -> 0x0226 }
            com.miui.gamebooster.service.r r1 = com.miui.gamebooster.service.r.a((android.content.Context) r1, (android.os.Handler) r3)     // Catch:{ all -> 0x0226 }
            int r3 = r7.mForegroundUid     // Catch:{ all -> 0x0226 }
            r1.c((int) r3)     // Catch:{ all -> 0x0226 }
            com.miui.gamebooster.service.GameBoosterService r1 = r6.f4810a     // Catch:{ all -> 0x0226 }
            com.miui.gamebooster.service.GameBoosterService r3 = r6.f4810a     // Catch:{ all -> 0x0226 }
            android.os.Handler r3 = r3.i     // Catch:{ all -> 0x0226 }
            com.miui.gamebooster.service.r r1 = com.miui.gamebooster.service.r.a((android.content.Context) r1, (android.os.Handler) r3)     // Catch:{ all -> 0x0226 }
            boolean r7 = r7.isColdStart()     // Catch:{ all -> 0x0226 }
            r1.a((boolean) r7)     // Catch:{ all -> 0x0226 }
            com.miui.gamebooster.service.GameBoosterService r7 = r6.f4810a     // Catch:{ all -> 0x0226 }
            r7.d()     // Catch:{ all -> 0x0226 }
            monitor-exit(r0)     // Catch:{ all -> 0x0226 }
            return r2
        L_0x016a:
            com.miui.gamebooster.service.GameBoosterService r1 = r6.f4810a     // Catch:{ all -> 0x0226 }
            java.lang.String r4 = r7.mForegroundPackageName     // Catch:{ all -> 0x0226 }
            boolean r1 = r1.a((java.lang.String) r4)     // Catch:{ all -> 0x0226 }
            if (r1 != 0) goto L_0x0222
            java.lang.String r1 = r7.mForegroundPackageName     // Catch:{ all -> 0x0226 }
            com.miui.gamebooster.service.GameBoosterService r4 = r6.f4810a     // Catch:{ all -> 0x0226 }
            android.content.Context r4 = r4.p     // Catch:{ all -> 0x0226 }
            boolean r1 = com.miui.gamebooster.m.C0393y.a((java.lang.String) r1, (android.content.Context) r4)     // Catch:{ all -> 0x0226 }
            if (r1 == 0) goto L_0x019a
            com.miui.gamebooster.service.GameBoosterService r1 = r6.f4810a     // Catch:{ all -> 0x0226 }
            com.miui.gamebooster.service.GameBoosterService r4 = r6.f4810a     // Catch:{ all -> 0x0226 }
            android.os.Handler r4 = r4.i     // Catch:{ all -> 0x0226 }
            com.miui.gamebooster.service.r r1 = com.miui.gamebooster.service.r.a((android.content.Context) r1, (android.os.Handler) r4)     // Catch:{ all -> 0x0226 }
            java.lang.String r1 = r1.a()     // Catch:{ all -> 0x0226 }
            boolean r1 = com.miui.gamebooster.m.C0383n.a(r1)     // Catch:{ all -> 0x0226 }
            if (r1 == 0) goto L_0x019a
            goto L_0x0222
        L_0x019a:
            int[] r1 = com.miui.gamebooster.service.C0408i.f4819a     // Catch:{ all -> 0x0226 }
            com.miui.gamebooster.service.GameBoosterService r4 = r6.f4810a     // Catch:{ all -> 0x0226 }
            com.miui.gamebooster.service.GameBoosterService r5 = r6.f4810a     // Catch:{ all -> 0x0226 }
            android.os.Handler r5 = r5.i     // Catch:{ all -> 0x0226 }
            com.miui.gamebooster.service.r r4 = com.miui.gamebooster.service.r.a((android.content.Context) r4, (android.os.Handler) r5)     // Catch:{ all -> 0x0226 }
            com.miui.gamebooster.d.b r4 = r4.b()     // Catch:{ all -> 0x0226 }
            int r4 = r4.ordinal()     // Catch:{ all -> 0x0226 }
            r1 = r1[r4]     // Catch:{ all -> 0x0226 }
            if (r1 == r2) goto L_0x01b5
            goto L_0x0214
        L_0x01b5:
            long r1 = java.lang.System.currentTimeMillis()     // Catch:{ all -> 0x0226 }
            com.miui.gamebooster.service.GameBoosterService r4 = r6.f4810a     // Catch:{ all -> 0x0226 }
            long r4 = r4.f4761d     // Catch:{ all -> 0x0226 }
            long r1 = r1 - r4
            r4 = 10800000(0xa4cb80, double:5.335909E-317)
            int r1 = (r1 > r4 ? 1 : (r1 == r4 ? 0 : -1))
            if (r1 <= 0) goto L_0x01fa
            com.miui.gamebooster.service.GameBoosterService r1 = r6.f4810a     // Catch:{ all -> 0x0226 }
            java.util.ArrayList r1 = r1.r     // Catch:{ all -> 0x0226 }
            java.lang.String r2 = r7.mLastForegroundPackageName     // Catch:{ all -> 0x0226 }
            boolean r1 = r1.contains(r2)     // Catch:{ all -> 0x0226 }
            if (r1 == 0) goto L_0x01fa
            com.miui.gamebooster.service.GameBoosterService r1 = r6.f4810a     // Catch:{ all -> 0x0226 }
            long r4 = java.lang.System.currentTimeMillis()     // Catch:{ all -> 0x0226 }
            long unused = r1.f4761d = r4     // Catch:{ all -> 0x0226 }
            java.lang.String r1 = "GameBoosterService"
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ all -> 0x0226 }
            r2.<init>()     // Catch:{ all -> 0x0226 }
            java.lang.String r4 = "pop XunyouAlertActivity:"
            r2.append(r4)     // Catch:{ all -> 0x0226 }
            com.miui.gamebooster.service.GameBoosterService r4 = r6.f4810a     // Catch:{ all -> 0x0226 }
            boolean r4 = r4.f()     // Catch:{ all -> 0x0226 }
            r2.append(r4)     // Catch:{ all -> 0x0226 }
            java.lang.String r2 = r2.toString()     // Catch:{ all -> 0x0226 }
            android.util.Log.d(r1, r2)     // Catch:{ all -> 0x0226 }
        L_0x01fa:
            java.lang.String r1 = "GameBoosterService"
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ all -> 0x0226 }
            r2.<init>()     // Catch:{ all -> 0x0226 }
            java.lang.String r4 = "onGameStatusChange foreground:"
            r2.append(r4)     // Catch:{ all -> 0x0226 }
            java.lang.String r7 = r7.toString()     // Catch:{ all -> 0x0226 }
            r2.append(r7)     // Catch:{ all -> 0x0226 }
            java.lang.String r7 = r2.toString()     // Catch:{ all -> 0x0226 }
            android.util.Log.d(r1, r7)     // Catch:{ all -> 0x0226 }
        L_0x0214:
            java.lang.String r7 = "GameBoosterService"
            java.lang.String r1 = "onForegroundInfoChanged: Exit"
            android.util.Log.i(r7, r1)     // Catch:{ all -> 0x0226 }
            com.miui.gamebooster.service.GameBoosterService r7 = r6.f4810a     // Catch:{ all -> 0x0226 }
            r7.h()     // Catch:{ all -> 0x0226 }
            monitor-exit(r0)     // Catch:{ all -> 0x0226 }
            return r3
        L_0x0222:
            monitor-exit(r0)     // Catch:{ all -> 0x0226 }
            return r3
        L_0x0224:
            monitor-exit(r0)     // Catch:{ all -> 0x0226 }
            return r3
        L_0x0226:
            r7 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x0226 }
            throw r7
        L_0x0229:
            return r3
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.gamebooster.service.C0403d.onForegroundInfoChanged(miui.process.ForegroundInfo):boolean");
    }
}
