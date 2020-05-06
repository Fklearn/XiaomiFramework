package com.miui.applicationlock.a;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.os.Build;
import android.text.TextUtils;
import b.b.c.j.d;
import com.miui.analytics.AnalyticsUtil;
import com.miui.applicationlock.c.C;
import com.miui.applicationlock.c.C0259c;
import com.miui.applicationlock.c.E;
import com.miui.applicationlock.c.K;
import com.miui.applicationlock.c.o;
import com.miui.common.persistence.b;
import com.miui.securityscan.i.c;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import miui.security.SecurityManager;

public class h {
    public static void a() {
        l("material_copy_failed");
    }

    private static void a(long j) {
        AnalyticsUtil.recordCalculateEvent("applicationlock", "locked_app_quantity1", j, (Map<String, String>) null);
    }

    public static void a(long j, long j2) {
        d.a(new g(j, j2));
    }

    public static void a(Context context) {
        C0259c b2 = C0259c.b(context);
        b(context, b2);
        if (b2.e()) {
            c(b2);
            c(context);
            o();
            b(context);
            d(context, b2);
            c(context, b2);
            a(b2);
            a(context, b2);
            if (b2.a() == 1) {
                b(b2);
            }
            if (b.a("locked_app_quantity1", 0) != 0) {
                n();
            }
            List<ApplicationInfo> c2 = o.c();
            SecurityManager securityManager = (SecurityManager) context.getSystemService("security");
            for (ApplicationInfo next : c2) {
                if (!(securityManager == null || next == null || !securityManager.getApplicationAccessControlEnabled(next.packageName))) {
                    g(next.packageName);
                }
            }
        }
    }

    private static void a(Context context, C0259c cVar) {
        d("toggle_applock_binding", cVar.b() != null ? "on" : K.c(context) ? "off_logged_in" : "off_not_logged");
    }

    private static void a(C0259c cVar) {
        a("toggle_lock_all", cVar.f() ? 1 : 0);
    }

    public static void a(String str) {
        HashMap hashMap = new HashMap(1);
        hashMap.put("account_status", str);
        b("applock_mistake_reach_max", (Map<String, String>) hashMap);
    }

    private static void a(String str, long j) {
        AnalyticsUtil.recordNumericEvent("applicationlock", str, j);
    }

    public static void a(String str, String str2) {
        HashMap hashMap = new HashMap(1);
        hashMap.put(str, str2);
        b("set_forget_page_binding_result", (Map<String, String>) hashMap);
    }

    public static void a(SecurityManager securityManager) {
        if ("development".equals(c.a())) {
            List<ApplicationInfo> b2 = o.b(securityManager);
            if (b2.size() != 0) {
                for (ApplicationInfo next : b2) {
                    if (!TextUtils.isEmpty(next.packageName)) {
                        HashMap hashMap = new HashMap(1);
                        hashMap.put("app_name", next.packageName);
                        b("applock_message_mask_name", (Map<String, String>) hashMap);
                    }
                }
            }
        }
    }

    public static void b() {
        l("material_copy_succeed");
    }

    private static void b(Context context) {
        a("applock_message_mask", (long) o.b((SecurityManager) context.getSystemService("security")).size());
    }

    private static void b(Context context, C0259c cVar) {
        a("toggle_applock_main", C0259c.b(context).d() ? cVar.e() ? 1 : 0 : -1);
    }

    private static void b(C0259c cVar) {
        a("toggle_convenient_mode", cVar.g() ? 1 : 0);
    }

    public static void b(String str) {
        d.a(new f(str));
    }

    public static void b(String str, String str2) {
        HashMap hashMap = new HashMap(1);
        hashMap.put(str, str2);
        b("bind_xiaomi_account_dialog_login", (Map<String, String>) hashMap);
    }

    /* access modifiers changed from: private */
    public static void b(String str, Map<String, String> map) {
        AnalyticsUtil.recordCountEvent("applicationlock", str, map);
    }

    public static void c() {
        l("material_md5_check_failed");
    }

    private static void c(Context context) {
        a("toggle_applock_showdrawing", o.i(context) ? 1 : 0);
    }

    private static void c(Context context, C0259c cVar) {
        if (C.a(context).c()) {
            a("toggle_face_unlock", cVar.h() ? 1 : 0);
        }
    }

    private static void c(C0259c cVar) {
        String str;
        int a2 = cVar.a();
        if (a2 == 0) {
            str = "lockscreen_quit_app";
        } else if (a2 == 1) {
            str = "lockscreen";
        } else if (a2 == 2) {
            str = "lockscreen_quit_1min_app";
        } else {
            return;
        }
        d("toggle_lock_time", str);
    }

    public static void c(String str) {
        d.a(new a(str));
    }

    public static void c(String str, String str2) {
        HashMap hashMap = new HashMap(1);
        hashMap.put(str, str2);
        b("bind_xiaomi_account_dialog_unlogin", (Map<String, String>) hashMap);
    }

    public static void d() {
        l("material_md5_check_succeed");
    }

    private static void d(Context context, C0259c cVar) {
        if ((Build.VERSION.SDK_INT >= 23 ? E.a(context).d() : false) || Build.DEVICE.equals("hennessy") || Build.DEVICE.equals("ido") || Build.DEVICE.equals("aqua")) {
            a("toggle_finger_mark", cVar.i() ? 1 : 0);
        }
    }

    public static void d(String str) {
        d.a(new b(str));
    }

    private static void d(String str, String str2) {
        AnalyticsUtil.recordStringPropertyEvent("applicationlock", str, str2);
    }

    public static void e() {
        l("skin_display");
    }

    public static void e(String str) {
        HashMap hashMap = new HashMap(1);
        hashMap.put("module_click", str);
        b("applock_homepage_click", (Map<String, String>) hashMap);
    }

    public static void f() {
        l("skin_flag_top");
    }

    /* JADX WARNING: Removed duplicated region for block: B:12:0x0027  */
    /* JADX WARNING: Removed duplicated region for block: B:15:0x002f  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static void f(java.lang.String r3) {
        /*
            int r0 = r3.hashCode()
            r1 = -2000413939(0xffffffff88c41b0d, float:-1.18026805E-33)
            r2 = 1
            if (r0 == r1) goto L_0x001a
            r1 = 103910395(0x6318bfb, float:3.339284E-35)
            if (r0 == r1) goto L_0x0010
            goto L_0x0024
        L_0x0010:
            java.lang.String r0 = "mixed"
            boolean r3 = r3.equals(r0)
            if (r3 == 0) goto L_0x0024
            r3 = 0
            goto L_0x0025
        L_0x001a:
            java.lang.String r0 = "numeric"
            boolean r3 = r3.equals(r0)
            if (r3 == 0) goto L_0x0024
            r3 = r2
            goto L_0x0025
        L_0x0024:
            r3 = -1
        L_0x0025:
            if (r3 == 0) goto L_0x002f
            if (r3 == r2) goto L_0x002c
            java.lang.String r3 = "pattern"
            goto L_0x0031
        L_0x002c:
            java.lang.String r3 = "number"
            goto L_0x0031
        L_0x002f:
            java.lang.String r3 = "mix_password"
        L_0x0031:
            com.miui.applicationlock.a.c r0 = new com.miui.applicationlock.a.c
            r0.<init>(r3)
            b.b.c.j.d.a(r0)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.applicationlock.a.h.f(java.lang.String):void");
    }

    public static void g() {
        HashMap hashMap = new HashMap();
        hashMap.put("applock_mini_card_params", "applock_mini_card_cancel_click");
        b("applock_mini_card", (Map<String, String>) hashMap);
    }

    public static void g(String str) {
        HashMap hashMap = new HashMap(1);
        hashMap.put("choose_package_name", str);
        b("choose_locked_app_name", (Map<String, String>) hashMap);
    }

    public static void h() {
        HashMap hashMap = new HashMap();
        hashMap.put("applock_mini_card_params", "applock_mini_card_ignore_click");
        b("applock_mini_card", (Map<String, String>) hashMap);
    }

    public static void h(String str) {
        d.a(new d(str));
    }

    public static void i() {
        HashMap hashMap = new HashMap();
        hashMap.put("applock_mini_card_params", "applock_mini_card_ok_click");
        b("applock_mini_card", (Map<String, String>) hashMap);
    }

    public static void i(String str) {
        d.a(new e(str));
    }

    public static void j() {
        HashMap hashMap = new HashMap();
        hashMap.put("applock_mini_card_params", "applock_mini_card_show");
        b("applock_mini_card", (Map<String, String>) hashMap);
    }

    public static void j(String str) {
        HashMap hashMap = new HashMap(1);
        hashMap.put("enter_way", str);
        b("noti_mask_enter_way", (Map<String, String>) hashMap);
    }

    public static void k() {
        HashMap hashMap = new HashMap();
        hashMap.put("applock_mini_card_params", "applock_mini_card_show_install");
        b("applock_mini_card", (Map<String, String>) hashMap);
    }

    public static void k(String str) {
        HashMap hashMap = new HashMap(1);
        hashMap.put("module_click", str);
        b("recommend_page_action", (Map<String, String>) hashMap);
    }

    public static void l() {
        HashMap hashMap = new HashMap();
        hashMap.put("applock_mini_card_params", "applock_mini_card_show_use");
        b("applock_mini_card", (Map<String, String>) hashMap);
    }

    private static void l(String str) {
        AnalyticsUtil.recordCountEvent("applicationlock", str, (Map<String, String>) null);
    }

    public static void m() {
        l("privacy_apps_tutorial_click");
    }

    private static void n() {
        long a2 = b.a("locked_app_quantity1", 0);
        if (a2 > 0) {
            a(a2);
        }
    }

    private static void o() {
        a("toggle_applock_haveapps", (long) (b.a("locked_app_quantity1", 0) != 0 ? 1 : 0));
    }
}
