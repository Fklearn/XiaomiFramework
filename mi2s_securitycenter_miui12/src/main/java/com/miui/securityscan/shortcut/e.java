package com.miui.securityscan.shortcut;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import b.b.c.j.i;
import b.b.c.j.x;
import com.miui.networkassistant.config.Constants;
import com.miui.securitycenter.Application;
import com.miui.securitycenter.R;
import java.io.Closeable;
import miui.util.IOUtils;

public class e {

    /* renamed from: a  reason: collision with root package name */
    public static final String f7958a = (i.a() + ".launcher.settings");

    /* renamed from: b  reason: collision with root package name */
    public static final String f7959b = ("content://" + f7958a + "/favorites");

    public enum a {
        QUICk_CLEANUP,
        CLEANMASTER,
        OPTIMIZE_CENTER,
        POWER_CENTER,
        VIRUS_CENTER,
        PERM_CENTER,
        NETWORK_ASSISTANT,
        NETWORK_ASSISTANT_OLD,
        ANTISPAM,
        POWER_CLEANUP,
        NETWORK_SPEED_DETAILS,
        LUCKY_MONEY,
        NETWORK_DIAGNOSTICS
    }

    /* JADX WARNING: Code restructure failed: missing block: B:14:0x00a0, code lost:
        r0.setComponent(r7);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:16:0x00a6, code lost:
        r0.setAction(r7);
        r0.addCategory(com.miui.networkassistant.config.Constants.System.CATEGORY_DEFALUT);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:17:0x00ac, code lost:
        return r0;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static android.content.Intent a(com.miui.securityscan.shortcut.e.a r7) {
        /*
            android.content.Intent r0 = new android.content.Intent
            r0.<init>()
            int[] r1 = com.miui.securityscan.shortcut.d.f7957a
            int r7 = r7.ordinal()
            r7 = r1[r7]
            java.lang.String r1 = "com.miui.networkassistant.ui.NetworkAssistantActivity"
            java.lang.String r2 = "miui.intent.action.NETWORKASSISTANT_ENTRANCE"
            java.lang.String r3 = "com.miui.optimizecenter.MainActivity"
            java.lang.String r4 = "miui.intent.action.GARBAGE_CLEANUP"
            java.lang.String r5 = "com.miui.securitycenter"
            java.lang.String r6 = "android.intent.category.DEFAULT"
            switch(r7) {
                case 1: goto L_0x00a4;
                case 2: goto L_0x0095;
                case 3: goto L_0x0087;
                case 4: goto L_0x0079;
                case 5: goto L_0x006d;
                case 6: goto L_0x005d;
                case 7: goto L_0x005a;
                case 8: goto L_0x004a;
                case 9: goto L_0x003a;
                case 10: goto L_0x0037;
                case 11: goto L_0x0033;
                case 12: goto L_0x002f;
                case 13: goto L_0x001e;
                default: goto L_0x001c;
            }
        L_0x001c:
            goto L_0x00ac
        L_0x001e:
            java.lang.String r7 = "miui.intent.action.HB_MAIN_ACTIVITY"
            r0.setAction(r7)
            r0.addCategory(r6)
            android.content.ComponentName r7 = new android.content.ComponentName
            java.lang.String r1 = "com.miui.luckymoney.ui.activity.LuckySettingActivity"
            r7.<init>(r5, r1)
            goto L_0x00a0
        L_0x002f:
            java.lang.String r7 = "miui.intent.action.NETWORK_DIAGNOSTICS"
            goto L_0x00a6
        L_0x0033:
            java.lang.String r7 = "miui.intent.action.NETWORKASSISTANT_NETWORK_SPEED_DETAIL"
            goto L_0x00a6
        L_0x0037:
            java.lang.String r7 = "com.android.securitycenter.CREATE_DEEP_CLEAN_SHORTCUT"
            goto L_0x00a6
        L_0x003a:
            java.lang.String r7 = "miui.intent.action.LICENSE_MANAGER"
            r0.setAction(r7)
            r0.addCategory(r6)
            android.content.ComponentName r7 = new android.content.ComponentName
            java.lang.String r1 = "com.miui.permcenter.MainAcitivty"
            r7.<init>(r5, r1)
            goto L_0x00a0
        L_0x004a:
            java.lang.String r7 = "miui.intent.action.ANTI_VIRUS"
            r0.setAction(r7)
            r0.addCategory(r6)
            android.content.ComponentName r7 = new android.content.ComponentName
            java.lang.String r1 = "com.miui.antivirus.activity.MainActivity"
            r7.<init>(r5, r1)
            goto L_0x00a0
        L_0x005a:
            java.lang.String r7 = "miui.intent.action.POWER_MANAGER"
            goto L_0x00a6
        L_0x005d:
            java.lang.String r7 = "miui.intent.action.SET_FIREWALL"
            r0.setAction(r7)
            r0.addCategory(r6)
            java.lang.String r7 = ":miui:starting_window_label"
            java.lang.String r1 = ""
            r0.putExtra(r7, r1)
            goto L_0x00ac
        L_0x006d:
            r0.setAction(r2)
            r0.addCategory(r6)
            android.content.ComponentName r7 = new android.content.ComponentName
            r7.<init>(r5, r1)
            goto L_0x00a0
        L_0x0079:
            r0.setAction(r2)
            r0.addCategory(r6)
            android.content.ComponentName r7 = new android.content.ComponentName
            java.lang.String r2 = "com.miui.networkassistant"
            r7.<init>(r2, r1)
            goto L_0x00a0
        L_0x0087:
            r0.setAction(r4)
            r0.addCategory(r6)
            android.content.ComponentName r7 = new android.content.ComponentName
            java.lang.String r1 = "com.miui.cleanmaster"
            r7.<init>(r1, r3)
            goto L_0x00a0
        L_0x0095:
            r0.setAction(r4)
            r0.addCategory(r6)
            android.content.ComponentName r7 = new android.content.ComponentName
            r7.<init>(r5, r3)
        L_0x00a0:
            r0.setComponent(r7)
            goto L_0x00ac
        L_0x00a4:
            java.lang.String r7 = "miui.intent.action.CREATE_QUICK_CLEANUP_SHORTCUT"
        L_0x00a6:
            r0.setAction(r7)
            r0.addCategory(r6)
        L_0x00ac:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.securityscan.shortcut.e.a(com.miui.securityscan.shortcut.e$a):android.content.Intent");
    }

    public static void a(Context context, a aVar) {
        if (Build.VERSION.SDK_INT >= 26) {
            b.a(context, aVar);
        } else {
            context.sendBroadcast(d(context, aVar));
        }
    }

    private static void a(Context context, a aVar, Intent intent) {
        intent.putExtra("duplicate", false);
        intent.putExtra("android.intent.extra.shortcut.NAME", c(aVar));
        intent.putExtra("android.intent.extra.shortcut.ICON_RESOURCE", Intent.ShortcutIconResource.fromContext(context, b(aVar)));
        intent.putExtra("android.intent.extra.shortcut.INTENT", a(aVar));
    }

    public static boolean a() {
        return x.e(Application.d(), "com.miui.home") > 41600000;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:10:0x0032, code lost:
        if (r0 != null) goto L_0x003d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:15:0x003b, code lost:
        if (r0 == null) goto L_0x0040;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:16:0x003d, code lost:
        r0.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:18:0x0045, code lost:
        if (android.os.Build.VERSION.SDK_INT < 26) goto L_0x004c;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:19:0x0047, code lost:
        r7 = com.miui.securityscan.shortcut.b.b(r7, r9);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:20:0x004c, code lost:
        r7 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:21:0x004d, code lost:
        if (r10 == false) goto L_0x0050;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:26:?, code lost:
        return false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:27:?, code lost:
        return r7;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static boolean a(android.content.Context r7, int r8, com.miui.securityscan.shortcut.e.a r9, boolean r10) {
        /*
            java.lang.String r0 = f7959b
            android.net.Uri r2 = android.net.Uri.parse(r0)
            android.content.ContentResolver r1 = r7.getContentResolver()
            r3 = 0
            r0 = 0
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0037 }
            r4.<init>()     // Catch:{ Exception -> 0x0037 }
            java.lang.String r5 = "itemType=5 AND appWidgetId= "
            r4.append(r5)     // Catch:{ Exception -> 0x0037 }
            r4.append(r8)     // Catch:{ Exception -> 0x0037 }
            java.lang.String r4 = r4.toString()     // Catch:{ Exception -> 0x0037 }
            r5 = 0
            r6 = 0
            android.database.Cursor r0 = r1.query(r2, r3, r4, r5, r6)     // Catch:{ Exception -> 0x0037 }
            if (r0 == 0) goto L_0x0032
            boolean r8 = r0.moveToFirst()     // Catch:{ Exception -> 0x0037 }
            if (r8 == 0) goto L_0x0032
            r7 = 1
            if (r0 == 0) goto L_0x0031
            r0.close()
        L_0x0031:
            return r7
        L_0x0032:
            if (r0 == 0) goto L_0x0040
            goto L_0x003d
        L_0x0035:
            r7 = move-exception
            goto L_0x0052
        L_0x0037:
            r8 = move-exception
            r8.printStackTrace()     // Catch:{ all -> 0x0035 }
            if (r0 == 0) goto L_0x0040
        L_0x003d:
            r0.close()
        L_0x0040:
            int r8 = android.os.Build.VERSION.SDK_INT
            r0 = 26
            r1 = 0
            if (r8 < r0) goto L_0x004c
            boolean r7 = com.miui.securityscan.shortcut.b.b(r7, r9)
            goto L_0x004d
        L_0x004c:
            r7 = r1
        L_0x004d:
            if (r10 == 0) goto L_0x0050
            goto L_0x0051
        L_0x0050:
            r7 = r1
        L_0x0051:
            return r7
        L_0x0052:
            if (r0 == 0) goto L_0x0057
            r0.close()
        L_0x0057:
            throw r7
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.securityscan.shortcut.e.a(android.content.Context, int, com.miui.securityscan.shortcut.e$a, boolean):boolean");
    }

    public static boolean a(Context context, a aVar, boolean z) {
        if (aVar == a.QUICk_CLEANUP) {
            return c(context, aVar, z);
        }
        if (aVar == a.POWER_CLEANUP) {
            return b(context, aVar, z);
        }
        if (aVar == a.NETWORK_DIAGNOSTICS && a(context, a.NETWORK_SPEED_DETAILS, z)) {
            return true;
        }
        Cursor cursor = null;
        try {
            cursor = context.getContentResolver().query(Uri.parse(f7959b), new String[]{"_id"}, " intent = ? ", new String[]{a(aVar).toUri(0)}, (String) null);
            if (!(cursor == null || cursor.getCount() == 0)) {
                IOUtils.closeQuietly(cursor);
                return true;
            }
        } catch (Exception e) {
            Log.e("ShortcutHelper", e.toString());
        } catch (Throwable th) {
            IOUtils.closeQuietly((Closeable) null);
            throw th;
        }
        IOUtils.closeQuietly(cursor);
        boolean b2 = Build.VERSION.SDK_INT >= 26 ? b.b(context, aVar) : false;
        if (z) {
            return b2;
        }
        return false;
    }

    public static int b(a aVar) {
        switch (d.f7957a[aVar.ordinal()]) {
            case 1:
                return a() ? R.drawable.ic_launcher_quick_clean_new : R.drawable.ic_launcher_quick_clean;
            case 2:
            case 3:
                return R.drawable.ic_launcher_rubbish_clean;
            case 4:
            case 5:
                return R.drawable.ic_launcher_network_assistant;
            case 6:
                return R.drawable.ic_launcher_anti_spam;
            case 7:
                return R.drawable.ic_launcher_power_optimize;
            case 8:
                return R.drawable.ic_launcher_virus_scan;
            case 9:
                return R.drawable.ic_launcher_license_manage;
            case 10:
                return a() ? R.drawable.icon_power_cleanup_new : R.drawable.icon_power_cleanup;
            case 11:
                return R.drawable.ic_launcher_network_speed_detail;
            case 12:
                return R.drawable.ic_launcher_network_diagnostics;
            case 13:
                return R.drawable.hongbao_launcher;
            default:
                return -1;
        }
    }

    public static boolean b(Context context, a aVar) {
        return a(context, aVar, true);
    }

    private static boolean b(Context context, a aVar, boolean z) {
        return a(context, 14, aVar, z);
    }

    public static String c(a aVar) {
        switch (d.f7957a[aVar.ordinal()]) {
            case 1:
                return "com.miui.securitycenter:string/btn_text_quick_cleanup";
            case 2:
            case 3:
                return "com.miui.securitycenter:string/activity_title_garbage_cleanup";
            case 4:
            case 5:
                return "com.miui.securitycenter:string/activity_title_networkassistants";
            case 6:
                return "com.miui.securitycenter:string/activity_title_antispam";
            case 7:
                return "com.miui.securitycenter:string/activity_title_power_manager";
            case 8:
                return "com.miui.securitycenter:string/activity_title_antivirus";
            case 9:
                return "com.miui.securitycenter:string/activity_title_license_manager";
            case 10:
                return "com.miui.securitycenter:string/btn_text_power_cleanup";
            case 11:
                return "com.miui.securitycenter:string/btn_text_network_speed_detail";
            case 12:
                return "com.miui.securitycenter:string/network_diagnostics";
            case 13:
                return "com.miui.securitycenter:string/hongbao_name";
            default:
                return null;
        }
    }

    public static void c(Context context, a aVar) {
        if (aVar == a.NETWORK_DIAGNOSTICS) {
            c(context, a.NETWORK_SPEED_DETAILS);
        }
        boolean b2 = Build.VERSION.SDK_INT >= 26 ? b.b(context, aVar) : false;
        boolean a2 = a(context, aVar, false);
        if (b2) {
            Log.d("ShortcutHelper", "unistall pinnedList shortcut : " + aVar);
            Intent intent = new Intent("com.miui.home.launcher.action.UNINSTALL_SHORTCUT");
            intent.putExtra("shortcut_id", c(aVar));
            intent.setPackage(i.a());
            context.sendBroadcast(intent);
        }
        if (a2) {
            Log.d("ShortcutHelper", "unistall database shortcut : " + aVar);
            Intent intent2 = new Intent("com.miui.home.launcher.action.UNINSTALL_SHORTCUT");
            if (Build.VERSION.SDK_INT >= 26) {
                intent2.setPackage(i.a());
            }
            a(context, aVar, intent2);
            context.sendBroadcast(intent2);
        }
    }

    private static boolean c(Context context, a aVar, boolean z) {
        return a(context, 12, aVar, z);
    }

    private static Intent d(Context context, a aVar) {
        Intent intent = new Intent(Constants.System.ACTION_INSTALL_SHORTCUT);
        a(context, aVar, intent);
        return intent;
    }
}
