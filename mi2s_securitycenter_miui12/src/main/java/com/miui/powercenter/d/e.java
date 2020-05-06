package com.miui.powercenter.d;

import b.b.c.j.B;
import miui.os.SystemProperties;
import miui.util.FeatureParser;

public class e implements d {

    /* renamed from: a  reason: collision with root package name */
    private static final boolean f6989a;

    /* renamed from: b  reason: collision with root package name */
    private Object f6990b;

    static {
        int[] intArray;
        boolean z = false;
        if (B.j() == 0 && (intArray = FeatureParser.getIntArray("fpsList")) != null && intArray.length > 0) {
            z = true;
        }
        f6989a = z;
    }

    private Object a() {
        if (this.f6990b == null) {
            try {
                this.f6990b = b.b.o.g.e.a(Class.forName("miui.hardware.display.DisplayFeatureManager"), "getInstance", (Class<?>[]) null, new Object[0]);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return this.f6990b;
    }

    private void a(int i) {
        try {
            Object a2 = a();
            if (a2 != null) {
                b.b.o.g.e.a(a2, "setScreenEffect", (Class<?>[]) new Class[]{Integer.TYPE, Integer.TYPE, Integer.TYPE}, 24, Integer.valueOf(i), 250);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean b() {
        return SystemProperties.getInt("persist.vendor.power.dfps.level", 0) == 60;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:12:0x0015, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void a(android.content.Context r2) {
        /*
            r1 = this;
            java.lang.Class<com.miui.powercenter.d.e> r2 = com.miui.powercenter.d.e.class
            monitor-enter(r2)
            boolean r0 = f6989a     // Catch:{ all -> 0x0016 }
            if (r0 == 0) goto L_0x0014
            boolean r0 = r1.b()     // Catch:{ all -> 0x0016 }
            if (r0 != 0) goto L_0x000e
            goto L_0x0014
        L_0x000e:
            r0 = 0
            r1.a((int) r0)     // Catch:{ all -> 0x0016 }
            monitor-exit(r2)     // Catch:{ all -> 0x0016 }
            return
        L_0x0014:
            monitor-exit(r2)     // Catch:{ all -> 0x0016 }
            return
        L_0x0016:
            r0 = move-exception
            monitor-exit(r2)     // Catch:{ all -> 0x0016 }
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.powercenter.d.e.a(android.content.Context):void");
    }

    /* JADX WARNING: Code restructure failed: missing block: B:12:0x0016, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void b(android.content.Context r2) {
        /*
            r1 = this;
            java.lang.Class<com.miui.powercenter.d.e> r2 = com.miui.powercenter.d.e.class
            monitor-enter(r2)
            boolean r0 = f6989a     // Catch:{ all -> 0x0017 }
            if (r0 == 0) goto L_0x0015
            boolean r0 = r1.b()     // Catch:{ all -> 0x0017 }
            if (r0 == 0) goto L_0x000e
            goto L_0x0015
        L_0x000e:
            r0 = 60
            r1.a((int) r0)     // Catch:{ all -> 0x0017 }
            monitor-exit(r2)     // Catch:{ all -> 0x0017 }
            return
        L_0x0015:
            monitor-exit(r2)     // Catch:{ all -> 0x0017 }
            return
        L_0x0017:
            r0 = move-exception
            monitor-exit(r2)     // Catch:{ all -> 0x0017 }
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.powercenter.d.e.b(android.content.Context):void");
    }
}
