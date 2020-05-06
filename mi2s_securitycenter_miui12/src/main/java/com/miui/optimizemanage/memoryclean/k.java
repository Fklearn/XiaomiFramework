package com.miui.optimizemanage.memoryclean;

import android.content.Context;
import com.miui.securitycenter.Application;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class k {

    /* renamed from: a  reason: collision with root package name */
    private Context f5979a = Application.d();

    private Set<String> a() {
        return this.f5979a.getSharedPreferences("proc_filter", 0).getStringSet("locked_pkg_list", (Set) null);
    }

    private boolean a(Set<String> set, String str) {
        if (set == null) {
            return false;
        }
        return set.contains(str);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:54:0x0103, code lost:
        if (a(r7, r8.f5972a) == false) goto L_0x0088;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public java.util.List<com.miui.optimizemanage.memoryclean.j> a(java.util.List<com.miui.optimizemanage.memoryclean.j> r20) {
        /*
            r19 = this;
            r0 = r19
            android.content.Context r1 = r0.f5979a
            java.util.List r1 = com.miui.optimizemanage.d.e.a(r1)
            java.util.Iterator r2 = r20.iterator()
            long r3 = com.miui.optimizemanage.d.e.a()
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>()
            java.lang.String r6 = "cleanedMemory elapsed time "
            r5.append(r6)
            r5.append(r3)
            java.lang.String r5 = r5.toString()
            java.lang.String r6 = "RunningProcFilter"
            android.util.Log.i(r6, r5)
            long r5 = android.os.SystemClock.elapsedRealtime()
            java.util.Set r7 = r19.a()
        L_0x002e:
            boolean r8 = r2.hasNext()
            if (r8 == 0) goto L_0x010d
            java.lang.Object r8 = r2.next()
            com.miui.optimizemanage.memoryclean.j r8 = (com.miui.optimizemanage.memoryclean.j) r8
            int[] r9 = r8.f5974c
            r10 = 1
            r11 = 0
            if (r9 != 0) goto L_0x0061
            r9 = r11
        L_0x0041:
            java.util.List<java.lang.Integer> r12 = r8.i
            int r12 = r12.size()
            if (r9 >= r12) goto L_0x005d
            java.util.List<java.lang.Integer> r12 = r8.i
            java.lang.Object r12 = r12.get(r9)
            java.lang.Integer r12 = (java.lang.Integer) r12
            int r12 = r12.intValue()
            if (r12 <= 0) goto L_0x005a
            r8.j = r11
            goto L_0x005e
        L_0x005a:
            int r9 = r9 + 1
            goto L_0x0041
        L_0x005d:
            r10 = r11
        L_0x005e:
            if (r10 != 0) goto L_0x002e
        L_0x0060:
            goto L_0x0088
        L_0x0061:
            android.content.Context r9 = r0.f5979a
            java.lang.String r12 = r8.f5972a
            boolean r9 = b.b.c.j.e.a((android.content.Context) r9, (java.lang.String) r12)
            if (r9 == 0) goto L_0x008c
            java.lang.StringBuilder r9 = new java.lang.StringBuilder
            r9.<init>()
            java.lang.String r10 = "Package "
            r9.append(r10)
            java.lang.String r8 = r8.f5972a
            r9.append(r8)
            java.lang.String r8 = " should keep alive"
            r9.append(r8)
            java.lang.String r8 = r9.toString()
            java.lang.String r9 = "Enterprise"
            android.util.Log.d(r9, r8)
        L_0x0088:
            r2.remove()
            goto L_0x002e
        L_0x008c:
            java.lang.String r9 = r8.f5972a
            boolean r9 = r1.contains(r9)
            if (r9 != 0) goto L_0x00a1
            android.content.Context r9 = r0.f5979a
            java.lang.String r12 = r8.f5972a
            boolean r9 = com.miui.optimizemanage.d.e.a(r9, r12)
            if (r9 != 0) goto L_0x009f
            goto L_0x00a1
        L_0x009f:
            r9 = r11
            goto L_0x00a2
        L_0x00a1:
            r9 = r10
        L_0x00a2:
            r8.j = r9
            boolean r9 = r8.j
            if (r9 != 0) goto L_0x00ad
            boolean r9 = r8.g
            if (r9 != 0) goto L_0x00ad
            goto L_0x0060
        L_0x00ad:
            int[] r9 = r8.f5974c
            long[] r9 = miui.securitycenter.utils.SecurityCenterHelper.getProcessPss(r9)
            if (r9 == 0) goto L_0x00c7
            r12 = r11
        L_0x00b6:
            int r13 = r9.length
            if (r12 >= r13) goto L_0x00c7
            long r13 = r8.f5975d
            r15 = r9[r12]
            r17 = 1024(0x400, double:5.06E-321)
            long r15 = r15 * r17
            long r13 = r13 + r15
            r8.f5975d = r13
            int r12 = r12 + 1
            goto L_0x00b6
        L_0x00c7:
            long r12 = r8.f5975d
            r14 = 0
            int r9 = (r12 > r14 ? 1 : (r12 == r14 ? 0 : -1))
            if (r9 > 0) goto L_0x00d0
            goto L_0x0060
        L_0x00d0:
            int r9 = (r3 > r14 ? 1 : (r3 == r14 ? 0 : -1))
            if (r9 == 0) goto L_0x002e
            r12 = 120000(0x1d4c0, double:5.9288E-319)
            int r9 = (r3 > r12 ? 1 : (r3 == r12 ? 0 : -1))
            if (r9 >= 0) goto L_0x002e
            boolean r9 = r8.e
            if (r9 != 0) goto L_0x002e
            long[] r9 = r8.k
            r11 = r9[r11]
        L_0x00e3:
            long[] r9 = r8.k
            int r13 = r9.length
            if (r10 >= r13) goto L_0x00f3
            r16 = r9[r10]
            int r13 = (r11 > r16 ? 1 : (r11 == r16 ? 0 : -1))
            if (r13 <= 0) goto L_0x00f0
            r11 = r9[r10]
        L_0x00f0:
            int r10 = r10 + 1
            goto L_0x00e3
        L_0x00f3:
            int r9 = (r11 > r14 ? 1 : (r11 == r14 ? 0 : -1))
            if (r9 == 0) goto L_0x002e
            long r9 = r5 - r11
            int r9 = (r9 > r3 ? 1 : (r9 == r3 ? 0 : -1))
            if (r9 <= 0) goto L_0x0105
            java.lang.String r10 = r8.f5972a
            boolean r10 = r0.a(r7, r10)
            if (r10 == 0) goto L_0x0088
        L_0x0105:
            if (r9 >= 0) goto L_0x002e
            boolean r8 = r8.f
            if (r8 != 0) goto L_0x002e
            goto L_0x0060
        L_0x010d:
            return r20
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.optimizemanage.memoryclean.k.a(java.util.List):java.util.List");
    }

    public void b(List<String> list) {
        this.f5979a.getSharedPreferences("proc_filter", 0).edit().putStringSet("locked_pkg_list", new HashSet(list)).apply();
    }
}
