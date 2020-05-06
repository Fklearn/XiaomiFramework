package com.miui.securitycenter.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.graphics.drawable.Icon;
import android.os.Build;
import b.b.c.j.e;
import com.miui.securitycenter.R;
import java.util.List;

public class i {
    /* JADX WARNING: Removed duplicated region for block: B:11:0x0033 A[RETURN] */
    /* JADX WARNING: Removed duplicated region for block: B:12:0x0034  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static android.content.pm.ShortcutInfo a(android.content.Context r8, java.lang.String r9, int r10, int r11, android.graphics.drawable.Icon r12, android.content.Intent r13) {
        /*
            java.lang.String r0 = "ShortcutUtils"
            java.lang.String r1 = "android.content.pm.ShortcutInfo$Builder"
            r2 = 0
            r3 = 1
            r4 = 0
            java.lang.Class r1 = java.lang.Class.forName(r1)     // Catch:{ Exception -> 0x002b }
            if (r1 == 0) goto L_0x0030
            r5 = 2
            java.lang.Class[] r6 = new java.lang.Class[r5]     // Catch:{ Exception -> 0x002b }
            java.lang.Class<android.content.Context> r7 = android.content.Context.class
            r6[r4] = r7     // Catch:{ Exception -> 0x002b }
            java.lang.Class<java.lang.String> r7 = java.lang.String.class
            r6[r3] = r7     // Catch:{ Exception -> 0x002b }
            java.lang.reflect.Constructor r1 = r1.getDeclaredConstructor(r6)     // Catch:{ Exception -> 0x002b }
            if (r1 == 0) goto L_0x0030
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ Exception -> 0x002b }
            r5[r4] = r8     // Catch:{ Exception -> 0x002b }
            r5[r3] = r9     // Catch:{ Exception -> 0x002b }
            java.lang.Object r8 = r1.newInstance(r5)     // Catch:{ Exception -> 0x002b }
            android.content.pm.ShortcutInfo$Builder r8 = (android.content.pm.ShortcutInfo.Builder) r8     // Catch:{ Exception -> 0x002b }
            goto L_0x0031
        L_0x002b:
            java.lang.String r8 = "construct shortcutinfo builder error "
            android.util.Log.i(r0, r8)
        L_0x0030:
            r8 = r2
        L_0x0031:
            if (r8 != 0) goto L_0x0034
            return r2
        L_0x0034:
            java.lang.String r9 = "setShortLabelResId"
            java.lang.Class[] r1 = new java.lang.Class[r3]     // Catch:{ Exception -> 0x006b }
            java.lang.Class r5 = java.lang.Integer.TYPE     // Catch:{ Exception -> 0x006b }
            r1[r4] = r5     // Catch:{ Exception -> 0x006b }
            java.lang.Object[] r5 = new java.lang.Object[r3]     // Catch:{ Exception -> 0x006b }
            java.lang.Integer r10 = java.lang.Integer.valueOf(r10)     // Catch:{ Exception -> 0x006b }
            r5[r4] = r10     // Catch:{ Exception -> 0x006b }
            b.b.o.g.e.a((java.lang.Object) r8, (java.lang.String) r9, (java.lang.Class<?>[]) r1, (java.lang.Object[]) r5)     // Catch:{ Exception -> 0x006b }
            java.lang.String r9 = "setLongLabelResId"
            java.lang.Class[] r10 = new java.lang.Class[r3]     // Catch:{ Exception -> 0x005b }
            java.lang.Class<java.lang.String> r1 = java.lang.String.class
            r10[r4] = r1     // Catch:{ Exception -> 0x005b }
            java.lang.Object[] r1 = new java.lang.Object[r3]     // Catch:{ Exception -> 0x005b }
            java.lang.Integer r11 = java.lang.Integer.valueOf(r11)     // Catch:{ Exception -> 0x005b }
            r1[r4] = r11     // Catch:{ Exception -> 0x005b }
            b.b.o.g.e.a((java.lang.Object) r8, (java.lang.String) r9, (java.lang.Class<?>[]) r10, (java.lang.Object[]) r1)     // Catch:{ Exception -> 0x005b }
            goto L_0x0060
        L_0x005b:
            java.lang.String r9 = "shortcutInfo setLongLabel error"
            android.util.Log.i(r0, r9)
        L_0x0060:
            r8.setIcon(r12)
            r8.setIntent(r13)
            android.content.pm.ShortcutInfo r8 = r8.build()
            return r8
        L_0x006b:
            java.lang.String r8 = "shortcutInfo setShortLabel error"
            android.util.Log.i(r0, r8)
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.securitycenter.utils.i.a(android.content.Context, java.lang.String, int, int, android.graphics.drawable.Icon, android.content.Intent):android.content.pm.ShortcutInfo");
    }

    /* JADX WARNING: Code restructure failed: missing block: B:31:0x0074, code lost:
        if (r6 != null) goto L_0x0076;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:9:0x001f, code lost:
        if (r6 != null) goto L_0x0076;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static java.util.List<android.content.pm.ShortcutInfo> a(android.content.Context r6, java.util.List<android.content.pm.ShortcutInfo> r7, java.lang.String r8) {
        /*
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            if (r7 == 0) goto L_0x0079
            boolean r1 = miui.os.Build.IS_INTERNATIONAL_BUILD
            if (r1 == 0) goto L_0x0022
            java.lang.String r1 = "com.whatsapp"
            boolean r8 = r1.equals(r8)
            if (r8 == 0) goto L_0x0079
            java.lang.String r8 = "whatsapp_clean"
            boolean r7 = a((java.util.List<android.content.pm.ShortcutInfo>) r7, (java.lang.String) r8)
            if (r7 != 0) goto L_0x0079
            android.content.pm.ShortcutInfo r6 = c(r6, r8)
            if (r6 == 0) goto L_0x0079
            goto L_0x0076
        L_0x0022:
            java.lang.String r1 = "com.tencent.mm"
            boolean r2 = r1.equals(r8)
            java.lang.String r3 = "com.miui.cleanmaster"
            java.lang.String r4 = "wechat_lean"
            if (r2 == 0) goto L_0x0043
            boolean r2 = b.b.c.j.x.h(r6, r3)
            if (r2 == 0) goto L_0x0043
            boolean r2 = a((java.util.List<android.content.pm.ShortcutInfo>) r7, (java.lang.String) r4)
            if (r2 != 0) goto L_0x0043
            android.content.pm.ShortcutInfo r2 = c(r6, r4)
            if (r2 == 0) goto L_0x0043
            r0.add(r2)
        L_0x0043:
            boolean r8 = r3.equals(r8)
            if (r8 == 0) goto L_0x0079
            java.lang.String r8 = "clean_master"
            boolean r2 = a((java.util.List<android.content.pm.ShortcutInfo>) r7, (java.lang.String) r8)
            if (r2 != 0) goto L_0x0064
            android.content.Intent r2 = new android.content.Intent
            java.lang.String r5 = "miui.intent.action.GARBAGE_CLEANUP"
            r2.<init>(r5)
            r2.setPackage(r3)
            android.content.pm.ShortcutInfo r8 = c(r6, r8)
            if (r8 == 0) goto L_0x0064
            r0.add(r8)
        L_0x0064:
            boolean r8 = b.b.c.j.x.h(r6, r1)
            if (r8 == 0) goto L_0x0079
            boolean r7 = a((java.util.List<android.content.pm.ShortcutInfo>) r7, (java.lang.String) r4)
            if (r7 != 0) goto L_0x0079
            android.content.pm.ShortcutInfo r6 = c(r6, r4)
            if (r6 == 0) goto L_0x0079
        L_0x0076:
            r0.add(r6)
        L_0x0079:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.securitycenter.utils.i.a(android.content.Context, java.util.List, java.lang.String):java.util.List");
    }

    /* JADX WARNING: Code restructure failed: missing block: B:24:0x0053, code lost:
        if (r2 != null) goto L_0x0055;
     */
    /* JADX WARNING: Removed duplicated region for block: B:33:0x006c A[Catch:{ Exception -> 0x0089 }] */
    /* JADX WARNING: Removed duplicated region for block: B:35:0x0074 A[Catch:{ Exception -> 0x0089 }] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static void a(android.content.Context r10) {
        /*
            java.lang.String r0 = "com.tencent.mm"
            java.lang.String r1 = "com.whatsapp"
            java.lang.String r2 = "security_scan"
            java.lang.String r3 = "anti_virus"
            java.lang.String r4 = "clean_master"
            boolean r5 = a()
            if (r5 != 0) goto L_0x0011
            return
        L_0x0011:
            java.lang.Class<android.content.pm.ShortcutManager> r5 = android.content.pm.ShortcutManager.class
            java.lang.Object r5 = r10.getSystemService(r5)     // Catch:{ Exception -> 0x0089 }
            android.content.pm.ShortcutManager r5 = (android.content.pm.ShortcutManager) r5     // Catch:{ Exception -> 0x0089 }
            java.util.List r6 = r5.getDynamicShortcuts()     // Catch:{ Exception -> 0x0089 }
            java.util.ArrayList r7 = new java.util.ArrayList     // Catch:{ Exception -> 0x0089 }
            r7.<init>()     // Catch:{ Exception -> 0x0089 }
            if (r6 == 0) goto L_0x007f
            boolean r8 = a((java.util.List<android.content.pm.ShortcutInfo>) r6, (java.lang.String) r4)     // Catch:{ Exception -> 0x0089 }
            java.lang.String r9 = "com.miui.cleanmaster"
            if (r8 != 0) goto L_0x003c
            boolean r8 = b.b.c.j.x.h(r10, r9)     // Catch:{ Exception -> 0x0089 }
            if (r8 == 0) goto L_0x0045
            android.content.pm.ShortcutInfo r4 = c(r10, r4)     // Catch:{ Exception -> 0x0089 }
            if (r4 == 0) goto L_0x0045
            r7.add(r4)     // Catch:{ Exception -> 0x0089 }
            goto L_0x0045
        L_0x003c:
            boolean r4 = b.b.c.j.x.h(r10, r9)     // Catch:{ Exception -> 0x0089 }
            if (r4 != 0) goto L_0x0045
            b(r10, r9)     // Catch:{ Exception -> 0x0089 }
        L_0x0045:
            boolean r4 = miui.os.Build.IS_INTERNATIONAL_BUILD     // Catch:{ Exception -> 0x0089 }
            if (r4 == 0) goto L_0x0059
            boolean r3 = a((java.util.List<android.content.pm.ShortcutInfo>) r6, (java.lang.String) r2)     // Catch:{ Exception -> 0x0089 }
            if (r3 != 0) goto L_0x0066
            android.content.pm.ShortcutInfo r2 = c(r10, r2)     // Catch:{ Exception -> 0x0089 }
            if (r2 == 0) goto L_0x0066
        L_0x0055:
            r7.add(r2)     // Catch:{ Exception -> 0x0089 }
            goto L_0x0066
        L_0x0059:
            boolean r2 = a((java.util.List<android.content.pm.ShortcutInfo>) r6, (java.lang.String) r3)     // Catch:{ Exception -> 0x0089 }
            if (r2 != 0) goto L_0x0066
            android.content.pm.ShortcutInfo r2 = c(r10, r3)     // Catch:{ Exception -> 0x0089 }
            if (r2 == 0) goto L_0x0066
            goto L_0x0055
        L_0x0066:
            boolean r2 = b.b.c.j.x.h(r10, r1)     // Catch:{ Exception -> 0x0089 }
            if (r2 == 0) goto L_0x0074
            java.util.List r10 = a(r10, r6, r1)     // Catch:{ Exception -> 0x0089 }
        L_0x0070:
            r7.addAll(r10)     // Catch:{ Exception -> 0x0089 }
            goto L_0x007f
        L_0x0074:
            boolean r1 = b.b.c.j.x.h(r10, r0)     // Catch:{ Exception -> 0x0089 }
            if (r1 == 0) goto L_0x007f
            java.util.List r10 = a(r10, r6, r0)     // Catch:{ Exception -> 0x0089 }
            goto L_0x0070
        L_0x007f:
            boolean r10 = r7.isEmpty()     // Catch:{ Exception -> 0x0089 }
            if (r10 != 0) goto L_0x0091
            r5.addDynamicShortcuts(r7)     // Catch:{ Exception -> 0x0089 }
            goto L_0x0091
        L_0x0089:
            r10 = move-exception
            java.lang.String r0 = "ShortcutUtils"
            java.lang.String r1 = "add shortcuts error "
            android.util.Log.e(r0, r1, r10)
        L_0x0091:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.securitycenter.utils.i.a(android.content.Context):void");
    }

    public static void a(Context context, String str) {
        if (a()) {
            ShortcutManager shortcutManager = (ShortcutManager) context.getSystemService(ShortcutManager.class);
            List<ShortcutInfo> a2 = a(context, shortcutManager.getDynamicShortcuts(), str);
            if (!a2.isEmpty()) {
                shortcutManager.addDynamicShortcuts(a2);
            }
        }
    }

    private static boolean a() {
        if (Build.VERSION.SDK_INT >= 25) {
            return e.b() > 8 || !miui.os.Build.IS_STABLE_VERSION;
        }
        return false;
    }

    private static boolean a(List<ShortcutInfo> list, String str) {
        if (list.isEmpty()) {
            return false;
        }
        for (ShortcutInfo id : list) {
            if (str.equals(id.getId())) {
                return true;
            }
        }
        return false;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:3:0x0007, code lost:
        r3 = (android.content.pm.ShortcutManager) r3.getSystemService(android.content.pm.ShortcutManager.class);
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static void b(android.content.Context r3, java.lang.String r4) {
        /*
            boolean r0 = a()
            if (r0 != 0) goto L_0x0007
            return
        L_0x0007:
            java.lang.Class<android.content.pm.ShortcutManager> r0 = android.content.pm.ShortcutManager.class
            java.lang.Object r3 = r3.getSystemService(r0)
            android.content.pm.ShortcutManager r3 = (android.content.pm.ShortcutManager) r3
            java.util.List r0 = r3.getDynamicShortcuts()
            if (r0 == 0) goto L_0x007c
            int r1 = r0.size()
            if (r1 <= 0) goto L_0x007c
            boolean r1 = miui.os.Build.IS_INTERNATIONAL_BUILD
            if (r1 == 0) goto L_0x003b
            java.lang.String r1 = "com.whatsapp"
            boolean r4 = r1.equals(r4)
            if (r4 == 0) goto L_0x007c
            java.lang.String r4 = "whatsapp_clean"
            boolean r0 = a((java.util.List<android.content.pm.ShortcutInfo>) r0, (java.lang.String) r4)
            if (r0 == 0) goto L_0x007c
            java.lang.String[] r4 = new java.lang.String[]{r4}
        L_0x0033:
            java.util.List r4 = java.util.Arrays.asList(r4)
            r3.removeDynamicShortcuts(r4)
            goto L_0x007c
        L_0x003b:
            java.lang.String r1 = "com.tencent.mm"
            boolean r1 = r1.equals(r4)
            java.lang.String r2 = "wechat_lean"
            if (r1 == 0) goto L_0x0056
            boolean r1 = a((java.util.List<android.content.pm.ShortcutInfo>) r0, (java.lang.String) r2)
            if (r1 == 0) goto L_0x0056
            java.lang.String[] r1 = new java.lang.String[]{r2}
            java.util.List r1 = java.util.Arrays.asList(r1)
            r3.removeDynamicShortcuts(r1)
        L_0x0056:
            java.lang.String r1 = "com.miui.cleanmaster"
            boolean r4 = r1.equals(r4)
            if (r4 == 0) goto L_0x007c
            java.lang.String r4 = "clean_master"
            boolean r1 = a((java.util.List<android.content.pm.ShortcutInfo>) r0, (java.lang.String) r4)
            if (r1 == 0) goto L_0x0071
            java.lang.String[] r4 = new java.lang.String[]{r4}
            java.util.List r4 = java.util.Arrays.asList(r4)
            r3.removeDynamicShortcuts(r4)
        L_0x0071:
            boolean r4 = a((java.util.List<android.content.pm.ShortcutInfo>) r0, (java.lang.String) r2)
            if (r4 == 0) goto L_0x007c
            java.lang.String[] r4 = new java.lang.String[]{r2}
            goto L_0x0033
        L_0x007c:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.securitycenter.utils.i.b(android.content.Context, java.lang.String):void");
    }

    private static ShortcutInfo c(Context context, String str) {
        Intent intent;
        int i;
        int i2;
        Icon createWithResource;
        String str2;
        if ("clean_master".equals(str)) {
            intent = new Intent("miui.intent.action.GARBAGE_CLEANUP");
            intent.setPackage("com.miui.cleanmaster");
            i = R.string.menu_text_garbage_cleanup;
            i2 = R.string.menu_text_garbage_cleanup;
            createWithResource = Icon.createWithResource(context, R.drawable.ic_shortcut_clean_master);
            str2 = "clean_master";
        } else if ("whatsapp_clean".equals(str)) {
            intent = new Intent("miui.intent.action.GARBAGE_DEEPCLEAN_WHATSAPP");
            intent.setPackage("com.miui.cleanmaster");
            i = R.string.shortcut_title_whatsapp_clean;
            i2 = R.string.shortcut_title_whatsapp_clean;
            createWithResource = Icon.createWithResource(context, R.drawable.ic_shortcut_whatsapp_clean);
            str2 = "whatsapp_clean";
        } else if ("wechat_lean".equals(str)) {
            intent = new Intent("miui.intent.action.GARBAGE_DEEPCLEAN_WECHAT");
            intent.setPackage("com.miui.cleanmaster");
            i = R.string.card_main_deepclean_wechat_title;
            i2 = R.string.card_main_deepclean_wechat_title;
            createWithResource = Icon.createWithResource(context, R.drawable.ic_shortcut_wechat_clean);
            str2 = "wechat_lean";
        } else if ("security_scan".equals(str)) {
            intent = new Intent("miui.intent.action.SECURITY_CENTER");
            intent.setPackage("com.miui.securitycenter");
            intent.putExtra("extra_auto_optimize", true);
            i = R.string.shortcut_title_scan;
            i2 = R.string.shortcut_title_scan;
            createWithResource = Icon.createWithResource(context, R.drawable.ic_shortcut_securityscan);
            str2 = "security_scan";
        } else if (!"anti_virus".equals(str)) {
            return null;
        } else {
            intent = new Intent("miui.intent.action.ANTI_VIRUS");
            intent.setPackage("com.miui.securitycenter");
            i = R.string.menu_text_antivirus;
            i2 = R.string.menu_text_antivirus;
            createWithResource = Icon.createWithResource(context, R.drawable.ic_shortcut_antivirus);
            str2 = "anti_virus";
        }
        return a(context, str2, i, i2, createWithResource, intent);
    }
}
