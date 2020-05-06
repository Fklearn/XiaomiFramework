package com.miui.gamebooster.c;

import android.content.Context;
import com.miui.common.persistence.b;
import com.miui.securityscan.c.e;
import miui.os.Build;

public class a {

    /* renamed from: a  reason: collision with root package name */
    private static a f4094a;

    /* renamed from: b  reason: collision with root package name */
    private static e f4095b;

    /* renamed from: c  reason: collision with root package name */
    private Context f4096c;

    private a(Context context) {
        this.f4096c = context.getApplicationContext();
        f4095b = e.a(this.f4096c, "common");
    }

    public static void A(boolean z) {
        b.b(x(false) ? "pref_video_anti_disturb_msg_mode" : "pref_anti_disturb_msg_quick_answer_mode", z);
    }

    public static void B(boolean z) {
        b.b("pref_anti_keyboard", z);
    }

    public static void C(boolean z) {
        b.b("pref_app_self_start_state", z);
    }

    public static void D(boolean z) {
        b.b("pref_gamebooster_competition", z);
    }

    public static void E(boolean z) {
        b.b("pref_gamebooster_databooster", z);
    }

    public static void F(boolean z) {
        b.b("pref_function_shiled_voicetrigger", z);
    }

    public static void G(boolean z) {
        b.b("flag_gamebooster_signed_first_click", z);
    }

    public static void H(boolean z) {
        b.b("pref_first_open_game_booster", z);
    }

    public static void I(boolean z) {
        b.b("gb_first_window_has_create_icon", z);
    }

    public static void J(boolean z) {
        b.b("pref_gamebooster_function_shield", z);
    }

    public static void K(boolean z) {
        b.b("pref_function_gwsd_status", z);
    }

    public static void L(boolean z) {
        b.b("pref_open_game_booster", z);
    }

    public static void M(boolean z) {
        b.b("pref_gamebox_turbo", z);
    }

    public static void N(boolean z) {
        b.b("pref_handsfree_status", z);
    }

    public static void O(boolean z) {
        b.b("pref_handsfree_mute_status", z);
    }

    public static void P(boolean z) {
        b.b("has_open_first_window", z);
    }

    public static void Q(boolean z) {
        b.b("pref_first_jobservice_load_game", z);
    }

    public static void R(boolean z) {
        b.b("gb_video_sound_status", z);
    }

    public static void S(boolean z) {
        b.b("pref_function_connect_status", z);
    }

    public static void T(boolean z) {
        b.b("pref_net_booster_status", z);
    }

    public static void U(boolean z) {
        b.b("pref_net_booster_wifi_status", z);
    }

    public static void V(boolean z) {
        b.b("pref_game_net_priority_state", z);
    }

    public static void W(boolean z) {
        b.b("pref_game_performance_model_state", z);
    }

    public static void X(boolean z) {
        b.b("pref_function_shiled_auto_bright", z);
    }

    public static void Y(boolean z) {
        b.b("pref_function_shiled_eye_shield", z);
    }

    public static void Z(boolean z) {
        b.b("pref_function_shiled_pull_notification_bar", z);
    }

    public static int a(int i) {
        return b.a("pref_function_shiled_num", i);
    }

    public static synchronized a a(Context context) {
        a aVar;
        synchronized (a.class) {
            if (f4094a == null) {
                f4094a = new a(context);
            }
            aVar = f4094a;
        }
        return aVar;
    }

    public static boolean a() {
        return b.a("gb_first_window_has_create_icon", false);
    }

    public static boolean a(boolean z) {
        return b.a("pref_gamebox_turbo", z);
    }

    public static void aa(boolean z) {
        b.b("pref_function_shiled_three_finger", z);
    }

    public static int b() {
        return b.a("pref_gamebooster_show_way", Build.IS_INTERNATIONAL_BUILD ? 1 : 0);
    }

    public static void b(int i) {
        b.b("pref_function_shiled_num", i);
    }

    public static boolean b(boolean z) {
        return b.a(x(false) ? "pref_video_anti_disturb_msg_mode" : "pref_anti_disturb_msg_quick_answer_mode", z);
    }

    public static void ba(boolean z) {
        b.b("pref_sign_notification_status", z);
    }

    public static void c(int i) {
        b.b("pref_gamebooster_show_way", i);
    }

    public static boolean c() {
        return b.a("gb_video_sound_status", false);
    }

    public static boolean c(boolean z) {
        return b.a("pref_anti_disturb_msg_mode", f4095b.a("pref_anti_disturb_msg_mode", z));
    }

    public static void ca(boolean z) {
        b.b("pref_gamebooster_slip_status", z);
    }

    public static boolean d() {
        return b.a("has_open_first_window", true);
    }

    public static boolean d(boolean z) {
        return b.a("pref_anti_keyboard", f4095b.a("pref_anti_keyboard", z));
    }

    public static void da(boolean z) {
        b.b("pref_wlan_change_protection", z);
    }

    public static boolean e() {
        return b.a("pref_first_open_game_booster", f4095b.a("pref_first_open_game_booster", true));
    }

    public static boolean e(boolean z) {
        return b.a("pref_gamebooster_competition", z);
    }

    public static void ea(boolean z) {
        b.b("pref_xunyou_user", z);
        if (z) {
            b.b("xunyou_alert_dialog_first_count", 0);
            b.b("xunyou_alert_dialog_overdue_gift_count", 0);
        }
    }

    public static boolean f() {
        return b.a("pref_first_jobservice_load_game", true);
    }

    public static boolean f(boolean z) {
        return b.a("pref_gamebooster_databooster", z);
    }

    public static boolean g(boolean z) {
        return b.a("pref_function_shiled_voicetrigger", z);
    }

    public static boolean h(boolean z) {
        return b.a("flag_gamebooster_signed_first_click", z);
    }

    public static boolean i(boolean z) {
        return b.a("pref_gamebooster_function_shield", z);
    }

    public static boolean j(boolean z) {
        return b.a("pref_function_gwsd_status", z);
    }

    public static boolean l(boolean z) {
        return b.a("pref_handsfree_status", z);
    }

    public static boolean m(boolean z) {
        return b.a("pref_handsfree_mute_status", z);
    }

    public static boolean n(boolean z) {
        return b.a("pref_function_connect_status", z);
    }

    public static boolean o(boolean z) {
        return b.a("pref_net_booster_status", z);
    }

    public static boolean p(boolean z) {
        return b.a("pref_net_booster_wifi_status", z);
    }

    public static boolean q(boolean z) {
        return b.a("pref_game_performance_model_state", f4095b.a("pref_game_performance_model_state", z));
    }

    public static boolean r(boolean z) {
        return b.a("pref_function_shiled_auto_bright", z);
    }

    public static boolean s(boolean z) {
        return b.a("pref_function_shiled_eye_shield", z);
    }

    public static boolean t(boolean z) {
        return b.a("pref_function_shiled_pull_notification_bar", z);
    }

    public static boolean u(boolean z) {
        return b.a("pref_function_shiled_three_finger", z);
    }

    public static boolean v(boolean z) {
        return b.a("pref_sign_notification_status", z);
    }

    public static boolean w(boolean z) {
        return b.a("pref_gamebooster_slip_status", z);
    }

    public static boolean x(boolean z) {
        return b.a("pref_video_booster_status", z);
    }

    public static boolean y(boolean z) {
        return b.a("pref_xunyou_user", z);
    }

    public static void z(boolean z) {
        b.b("pref_anti_disturb_msg_mode", z);
    }

    public boolean k(boolean z) {
        return b.a("pref_open_game_booster", f4095b.a("pref_open_game_booster", z));
    }
}
