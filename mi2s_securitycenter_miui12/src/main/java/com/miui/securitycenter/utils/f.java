package com.miui.securitycenter.utils;

import android.content.Context;
import android.util.Log;
import b.b.c.j.d;
import b.b.o.g.e;
import com.miui.securitycenter.h;
import java.util.ArrayList;
import java.util.List;

public class f {
    public static List<String> a(int i) {
        ArrayList arrayList = new ArrayList();
        try {
            return (List) e.a(Class.forName("miui.process.ProcessManager"), List.class, "getLockedApplication", (Class<?>[]) new Class[]{Integer.TYPE}, Integer.valueOf(i));
        } catch (Exception e) {
            Log.e("ProcessManagerHelper", "getLockedApplication exception: ", e);
            return arrayList;
        }
    }

    public static void b(Context context) {
        if (h.e(context)) {
            Log.d("ProcessManagerHelper", "The app lock state data has been migrated ");
        } else {
            d.a(new e(context));
        }
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Removed duplicated region for block: B:24:0x0099  */
    /* JADX WARNING: Removed duplicated region for block: B:35:0x001a A[SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static void c(android.content.Context r9) {
        /*
            java.lang.String r0 = "ProcessManagerHelper"
            java.lang.String r1 = "The app lock state data start migration "
            android.util.Log.d(r0, r1)
            r1 = 0
            java.lang.String r2 = "memory_check"
            android.content.SharedPreferences r9 = r9.getSharedPreferences(r2, r1)
            java.util.Map r9 = r9.getAll()
            java.util.Set r2 = r9.keySet()
            java.util.Iterator r2 = r2.iterator()
        L_0x001a:
            boolean r3 = r2.hasNext()
            if (r3 == 0) goto L_0x00d4
            java.lang.Object r3 = r2.next()
            java.lang.String r3 = (java.lang.String) r3
            java.lang.String r4 = "pref_locked_pkgs"
            boolean r5 = r4.equals(r3)
            java.lang.String r6 = " ;  key : "
            java.lang.String r7 = "userId : "
            r8 = 1
            if (r5 == 0) goto L_0x004e
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            r4.append(r7)
            r4.append(r1)
            r4.append(r6)
            r4.append(r3)
            java.lang.String r4 = r4.toString()
            android.util.Log.d(r0, r4)
            r6 = r1
        L_0x004c:
            r4 = r8
            goto L_0x0097
        L_0x004e:
            if (r3 == 0) goto L_0x0095
            boolean r5 = r3.startsWith(r4)
            if (r5 == 0) goto L_0x0095
            java.lang.String r5 = ""
            java.lang.String r4 = r3.replaceAll(r4, r5)
            java.lang.String r4 = r4.trim()
            boolean r5 = android.text.TextUtils.isEmpty(r4)
            if (r5 != 0) goto L_0x0095
            java.lang.Integer r4 = java.lang.Integer.valueOf(r4)     // Catch:{ Exception -> 0x008c }
            int r4 = r4.intValue()     // Catch:{ Exception -> 0x008c }
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0088 }
            r5.<init>()     // Catch:{ Exception -> 0x0088 }
            r5.append(r7)     // Catch:{ Exception -> 0x0088 }
            r5.append(r4)     // Catch:{ Exception -> 0x0088 }
            r5.append(r6)     // Catch:{ Exception -> 0x0088 }
            r5.append(r3)     // Catch:{ Exception -> 0x0088 }
            java.lang.String r5 = r5.toString()     // Catch:{ Exception -> 0x0088 }
            android.util.Log.d(r0, r5)     // Catch:{ Exception -> 0x0088 }
            r6 = r4
            goto L_0x004c
        L_0x0088:
            r5 = move-exception
            r6 = r4
            r4 = r8
            goto L_0x008f
        L_0x008c:
            r5 = move-exception
            r4 = r1
            r6 = r4
        L_0x008f:
            java.lang.String r7 = "migrate error :"
            android.util.Log.e(r0, r7, r5)
            goto L_0x0097
        L_0x0095:
            r4 = r1
            r6 = r4
        L_0x0097:
            if (r4 == 0) goto L_0x001a
            java.lang.Object r3 = r9.get(r3)
            boolean r4 = r3 instanceof java.util.Set
            if (r4 == 0) goto L_0x001a
            java.util.Set r3 = (java.util.Set) r3
            java.util.Iterator r3 = r3.iterator()
        L_0x00a7:
            boolean r4 = r3.hasNext()
            if (r4 == 0) goto L_0x001a
            java.lang.Object r4 = r3.next()
            boolean r5 = r4 instanceof java.lang.String
            if (r5 == 0) goto L_0x00a7
            java.lang.String r4 = (java.lang.String) r4
            b.b.c.j.e.a((java.lang.String) r4, (int) r6, (boolean) r8)
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>()
            java.lang.String r7 = "packageName : "
            r5.append(r7)
            r5.append(r4)
            java.lang.String r4 = " ;   isLocked : true"
            r5.append(r4)
            java.lang.String r4 = r5.toString()
            android.util.Log.d(r0, r4)
            goto L_0x00a7
        L_0x00d4:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.securitycenter.utils.f.c(android.content.Context):void");
    }
}
