package com.miui.powercenter.batteryhistory;

/* renamed from: com.miui.powercenter.batteryhistory.i  reason: case insensitive filesystem */
public class C0505i {
    private static void a(C0504h hVar, int i, boolean z, boolean z2, boolean z3, boolean z4, boolean z5) {
        if (z) {
            hVar.f6893a.lineTo((float) i, (float) hVar.f6894b);
        }
        if (z2) {
            hVar.f6895c.lineTo((float) i, (float) hVar.f6896d);
        }
        if (z3) {
            hVar.e.lineTo((float) i, (float) hVar.f);
        }
        if (z4) {
            hVar.g.lineTo((float) i, (float) hVar.h);
        }
        if (z5) {
            hVar.i.lineTo((float) i, (float) hVar.j);
        }
        hVar.k.a(i);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:64:0x0116, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    static synchronized void a(com.miui.powercenter.batteryhistory.C0504h r19, java.util.List<com.miui.powercenter.batteryhistory.aa> r20, int r21, boolean r22) {
        /*
            r0 = r19
            r1 = r20
            r2 = r21
            java.lang.Class<com.miui.powercenter.batteryhistory.i> r8 = com.miui.powercenter.batteryhistory.C0505i.class
            monitor-enter(r8)
            if (r1 == 0) goto L_0x0115
            boolean r3 = r20.isEmpty()     // Catch:{ all -> 0x0112 }
            if (r3 == 0) goto L_0x0013
            goto L_0x0115
        L_0x0013:
            r3 = 0
            java.lang.Object r4 = r1.get(r3)     // Catch:{ all -> 0x0112 }
            com.miui.powercenter.batteryhistory.aa r4 = (com.miui.powercenter.batteryhistory.aa) r4     // Catch:{ all -> 0x0112 }
            long r4 = r4.a()     // Catch:{ all -> 0x0112 }
            int r6 = r20.size()     // Catch:{ all -> 0x0112 }
            int r6 = r6 + -1
            java.lang.Object r6 = r1.get(r6)     // Catch:{ all -> 0x0112 }
            com.miui.powercenter.batteryhistory.aa r6 = (com.miui.powercenter.batteryhistory.aa) r6     // Catch:{ all -> 0x0112 }
            long r6 = r6.a()     // Catch:{ all -> 0x0112 }
            long r6 = r6 - r4
            r9 = 0
            int r9 = (r6 > r9 ? 1 : (r6 == r9 ? 0 : -1))
            if (r9 != 0) goto L_0x0037
            r6 = 1
        L_0x0037:
            java.util.Iterator r1 = r20.iterator()     // Catch:{ all -> 0x0112 }
            r9 = r3
            r10 = r9
            r11 = r10
            r12 = r11
            r13 = r12
        L_0x0040:
            boolean r14 = r1.hasNext()     // Catch:{ all -> 0x0112 }
            if (r14 == 0) goto L_0x0103
            java.lang.Object r14 = r1.next()     // Catch:{ all -> 0x0112 }
            com.miui.powercenter.batteryhistory.aa r14 = (com.miui.powercenter.batteryhistory.aa) r14     // Catch:{ all -> 0x0112 }
            boolean r15 = r14.b()     // Catch:{ all -> 0x0112 }
            if (r15 == 0) goto L_0x00f8
            long r15 = r14.a()     // Catch:{ all -> 0x0112 }
            long r15 = r15 - r4
            r17 = r4
            long r3 = (long) r2     // Catch:{ all -> 0x0112 }
            long r15 = r15 * r3
            long r3 = r15 / r6
            int r3 = (int) r3     // Catch:{ all -> 0x0112 }
            if (r22 == 0) goto L_0x0062
            int r3 = r2 - r3
        L_0x0062:
            boolean r4 = r14.k     // Catch:{ all -> 0x0112 }
            if (r4 == r9) goto L_0x007c
            if (r4 == 0) goto L_0x0072
            android.graphics.Path r5 = r0.f6893a     // Catch:{ all -> 0x0112 }
            float r9 = (float) r3     // Catch:{ all -> 0x0112 }
            int r15 = r0.f6894b     // Catch:{ all -> 0x0112 }
            float r15 = (float) r15     // Catch:{ all -> 0x0112 }
            r5.moveTo(r9, r15)     // Catch:{ all -> 0x0112 }
            goto L_0x007b
        L_0x0072:
            android.graphics.Path r5 = r0.f6893a     // Catch:{ all -> 0x0112 }
            float r9 = (float) r3     // Catch:{ all -> 0x0112 }
            int r15 = r0.f6894b     // Catch:{ all -> 0x0112 }
            float r15 = (float) r15     // Catch:{ all -> 0x0112 }
            r5.lineTo(r9, r15)     // Catch:{ all -> 0x0112 }
        L_0x007b:
            r9 = r4
        L_0x007c:
            boolean r4 = r14.l     // Catch:{ all -> 0x0112 }
            if (r4 == r10) goto L_0x0096
            if (r4 == 0) goto L_0x008c
            android.graphics.Path r5 = r0.f6895c     // Catch:{ all -> 0x0112 }
            float r10 = (float) r3     // Catch:{ all -> 0x0112 }
            int r15 = r0.f6896d     // Catch:{ all -> 0x0112 }
            float r15 = (float) r15     // Catch:{ all -> 0x0112 }
            r5.moveTo(r10, r15)     // Catch:{ all -> 0x0112 }
            goto L_0x0095
        L_0x008c:
            android.graphics.Path r5 = r0.f6895c     // Catch:{ all -> 0x0112 }
            float r10 = (float) r3     // Catch:{ all -> 0x0112 }
            int r15 = r0.f6896d     // Catch:{ all -> 0x0112 }
            float r15 = (float) r15     // Catch:{ all -> 0x0112 }
            r5.lineTo(r10, r15)     // Catch:{ all -> 0x0112 }
        L_0x0095:
            r10 = r4
        L_0x0096:
            boolean r4 = r14.j     // Catch:{ all -> 0x0112 }
            if (r4 == r11) goto L_0x00b0
            if (r4 == 0) goto L_0x00a6
            android.graphics.Path r5 = r0.e     // Catch:{ all -> 0x0112 }
            float r11 = (float) r3     // Catch:{ all -> 0x0112 }
            int r15 = r0.f     // Catch:{ all -> 0x0112 }
            float r15 = (float) r15     // Catch:{ all -> 0x0112 }
            r5.moveTo(r11, r15)     // Catch:{ all -> 0x0112 }
            goto L_0x00af
        L_0x00a6:
            android.graphics.Path r5 = r0.e     // Catch:{ all -> 0x0112 }
            float r11 = (float) r3     // Catch:{ all -> 0x0112 }
            int r15 = r0.f     // Catch:{ all -> 0x0112 }
            float r15 = (float) r15     // Catch:{ all -> 0x0112 }
            r5.lineTo(r11, r15)     // Catch:{ all -> 0x0112 }
        L_0x00af:
            r11 = r4
        L_0x00b0:
            boolean r4 = r14.i     // Catch:{ all -> 0x0112 }
            if (r4 == r12) goto L_0x00ca
            if (r4 == 0) goto L_0x00c0
            android.graphics.Path r5 = r0.g     // Catch:{ all -> 0x0112 }
            float r12 = (float) r3     // Catch:{ all -> 0x0112 }
            int r15 = r0.h     // Catch:{ all -> 0x0112 }
            float r15 = (float) r15     // Catch:{ all -> 0x0112 }
            r5.moveTo(r12, r15)     // Catch:{ all -> 0x0112 }
            goto L_0x00c9
        L_0x00c0:
            android.graphics.Path r5 = r0.g     // Catch:{ all -> 0x0112 }
            float r12 = (float) r3     // Catch:{ all -> 0x0112 }
            int r15 = r0.h     // Catch:{ all -> 0x0112 }
            float r15 = (float) r15     // Catch:{ all -> 0x0112 }
            r5.lineTo(r12, r15)     // Catch:{ all -> 0x0112 }
        L_0x00c9:
            r12 = r4
        L_0x00ca:
            int r4 = android.os.Build.VERSION.SDK_INT     // Catch:{ all -> 0x0112 }
            r5 = 19
            if (r4 <= r5) goto L_0x00d3
            boolean r4 = r14.o     // Catch:{ all -> 0x0112 }
            goto L_0x00d5
        L_0x00d3:
            boolean r4 = r14.m     // Catch:{ all -> 0x0112 }
        L_0x00d5:
            boolean r5 = r14.l     // Catch:{ all -> 0x0112 }
            r4 = r4 | r5
            if (r4 == r13) goto L_0x00f0
            if (r4 == 0) goto L_0x00e6
            android.graphics.Path r5 = r0.i     // Catch:{ all -> 0x0112 }
            float r13 = (float) r3     // Catch:{ all -> 0x0112 }
            int r15 = r0.j     // Catch:{ all -> 0x0112 }
            float r15 = (float) r15     // Catch:{ all -> 0x0112 }
            r5.moveTo(r13, r15)     // Catch:{ all -> 0x0112 }
            goto L_0x00ef
        L_0x00e6:
            android.graphics.Path r5 = r0.i     // Catch:{ all -> 0x0112 }
            float r13 = (float) r3     // Catch:{ all -> 0x0112 }
            int r15 = r0.j     // Catch:{ all -> 0x0112 }
            float r15 = (float) r15     // Catch:{ all -> 0x0112 }
            r5.lineTo(r13, r15)     // Catch:{ all -> 0x0112 }
        L_0x00ef:
            r13 = r4
        L_0x00f0:
            int r4 = r14.n     // Catch:{ all -> 0x0112 }
            com.miui.powercenter.batteryhistory.Z r5 = r0.k     // Catch:{ all -> 0x0112 }
            r5.a(r3, r4)     // Catch:{ all -> 0x0112 }
            goto L_0x00fe
        L_0x00f8:
            r17 = r4
            boolean r3 = r14.c()     // Catch:{ all -> 0x0112 }
        L_0x00fe:
            r4 = r17
            r3 = 0
            goto L_0x0040
        L_0x0103:
            if (r22 == 0) goto L_0x0106
            r2 = 0
        L_0x0106:
            r1 = r19
            r3 = r9
            r4 = r10
            r5 = r11
            r6 = r12
            r7 = r13
            a(r1, r2, r3, r4, r5, r6, r7)     // Catch:{ all -> 0x0112 }
            monitor-exit(r8)
            return
        L_0x0112:
            r0 = move-exception
            monitor-exit(r8)
            throw r0
        L_0x0115:
            monitor-exit(r8)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.powercenter.batteryhistory.C0505i.a(com.miui.powercenter.batteryhistory.h, java.util.List, int, boolean):void");
    }
}
