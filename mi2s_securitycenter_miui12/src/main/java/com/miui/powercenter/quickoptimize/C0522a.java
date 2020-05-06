package com.miui.powercenter.quickoptimize;

import android.content.Context;
import com.miui.permcenter.a;
import com.miui.permcenter.n;
import com.miui.permission.PermissionManager;
import java.util.ArrayList;
import java.util.List;

/* renamed from: com.miui.powercenter.quickoptimize.a  reason: case insensitive filesystem */
public class C0522a {
    public static List<String> a(Context context) {
        ArrayList arrayList = new ArrayList();
        for (a next : n.a(context, (long) PermissionManager.PERM_ID_AUTOSTART)) {
            if (next.f().get(Long.valueOf(PermissionManager.PERM_ID_AUTOSTART)).intValue() == 3) {
                arrayList.add(next.e());
            }
        }
        return arrayList;
    }

    public static void a(Context context, List<String> list) {
        n.a(context, (long) PermissionManager.PERM_ID_AUTOSTART, (String[]) list.toArray(new String[list.size()]));
    }

    public static List<String> b(Context context) {
        ArrayList arrayList = new ArrayList();
        for (a next : n.b(context, (long) PermissionManager.PERM_ID_AUTOSTART)) {
            if (!"com.mi.health".equals(next.e()) && next.f().get(Long.valueOf(PermissionManager.PERM_ID_AUTOSTART)).intValue() == 3) {
                arrayList.add(next.e());
            }
        }
        return arrayList;
    }

    public static void b(Context context, List<String> list) {
        n.b(context, PermissionManager.PERM_ID_AUTOSTART, (String[]) list.toArray(new String[list.size()]));
    }

    public static void c(Context context, List<String> list) {
        n.c(context, PermissionManager.PERM_ID_AUTOSTART, (String[]) list.toArray(new String[list.size()]));
    }
}
