package com.miui.securityscan.a;

import android.content.Context;
import android.text.TextUtils;
import b.b.c.c.a.a;
import b.b.c.j.d;
import com.miui.analytics.AnalyticsUtil;
import com.miui.common.card.GridFunctionData;
import com.miui.common.card.functions.BaseFunction;
import com.miui.common.card.functions.FuncTopBannerScrollData;
import com.miui.common.card.models.BaseCardModel;
import com.miui.common.card.models.FunctionCardModel;
import com.miui.luckymoney.config.Constants;
import com.miui.luckymoney.stats.MiStatUtil;
import com.miui.monthreport.g;
import com.miui.securitycenter.h;
import com.miui.securityscan.M;
import com.miui.securityscan.model.AbsModel;
import com.miui.securityscan.shortcut.e;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class G {
    /* access modifiers changed from: private */
    public static void A(String str) {
        AnalyticsUtil.recordCountEvent("securitycenter", str, (Map<String, String>) null);
    }

    /* access modifiers changed from: private */
    public static void B(String str) {
        HashMap hashMap = new HashMap(1);
        hashMap.put("module_show", str);
        d("newcheck_result_action_activity", hashMap);
    }

    /* access modifiers changed from: private */
    public static void C(String str) {
        HashMap hashMap = new HashMap(1);
        hashMap.put("module_show", str);
        d("newcheck_result_action_ad", hashMap);
    }

    /* access modifiers changed from: private */
    public static void D(String str) {
        HashMap hashMap = new HashMap(1);
        hashMap.put("module_show", str);
        d("newcheck_result_action_f1", hashMap);
    }

    /* access modifiers changed from: private */
    public static void E(String str) {
        HashMap hashMap = new HashMap(1);
        hashMap.put("module_show", str);
        d("newcheck_result_action_news", hashMap);
    }

    /* access modifiers changed from: private */
    public static void F(String str) {
        HashMap hashMap = new HashMap(1);
        hashMap.put("module_show", str);
        d("slide_down_action_activity", hashMap);
    }

    /* access modifiers changed from: private */
    public static void G(String str) {
        HashMap hashMap = new HashMap(1);
        hashMap.put("module_show", str);
        d("slide_down_action_ad", hashMap);
    }

    /* access modifiers changed from: private */
    public static void H(String str) {
        HashMap hashMap = new HashMap(1);
        hashMap.put("module_show", str);
        d("slide_down_action_news", hashMap);
    }

    public static void a() {
        d.a(new v());
    }

    public static void a(int i) {
        d.a(new u(i));
    }

    public static void a(Context context, FuncTopBannerScrollData funcTopBannerScrollData) {
        String statKey = funcTopBannerScrollData.getStatKey();
        if (!TextUtils.isEmpty(statKey)) {
            HashMap hashMap = new HashMap(1);
            hashMap.put("module_show", statKey);
            d("slide_down_action_f", hashMap);
        }
        if ("#Intent;action=com.miui.gamebooster.action.ACCESS_MAINACTIVITY;S.jump_target=gamebox;end".equals(funcTopBannerScrollData.getAction())) {
            d(context);
        }
    }

    public static void a(Context context, BaseCardModel baseCardModel) {
        d.a(new C0541g(baseCardModel, context));
    }

    public static void a(Context context, List<GridFunctionData> list) {
        a.a(new z(list));
    }

    public static void a(FuncTopBannerScrollData funcTopBannerScrollData) {
        String statKey = funcTopBannerScrollData.getStatKey();
        if (!TextUtils.isEmpty(statKey)) {
            HashMap hashMap = new HashMap(1);
            hashMap.put("module_show", statKey);
            d("phone_manage_show_click", hashMap);
        }
    }

    public static void a(BaseCardModel baseCardModel) {
        d.a(new y(baseCardModel));
    }

    public static void a(boolean z) {
        HashMap hashMap = new HashMap(1);
        hashMap.put("module_click", z ? "close_network_dialog_ok" : "close_network_dialog_cancel");
        d("close_network_dialog", hashMap);
    }

    public static boolean a(long j) {
        return j > 0 && j < 3600;
    }

    public static void b() {
        d.a(new w());
    }

    public static void b(long j) {
        d.a(new q(j));
    }

    public static void b(Context context) {
        d.a(new F(context));
    }

    /* access modifiers changed from: private */
    public static void b(Context context, FunctionCardModel functionCardModel) {
        String statKey = functionCardModel.getStatKey();
        if (!TextUtils.isEmpty(statKey)) {
            HashMap hashMap = new HashMap(1);
            hashMap.put("module_show", statKey);
            d("slide_down_action_f", hashMap);
        }
        BaseFunction function = functionCardModel.getFunction();
        if (function != null && "#Intent;action=com.miui.gamebooster.action.ACCESS_MAINACTIVITY;S.jump_target=gamebox;end".equals(function.getAction())) {
            d(context);
        }
    }

    public static void b(Context context, List<GridFunctionData> list) {
        a.a(new C0539e(list, context));
    }

    public static void b(BaseCardModel baseCardModel) {
        d.a(new h(baseCardModel));
    }

    public static void b(String str, long j) {
        AnalyticsUtil.recordNumericEvent("securitycenter", str, j);
    }

    /* access modifiers changed from: private */
    public static void b(List<AbsModel> list) {
        for (AbsModel next : list) {
            if (next.isSafe() != AbsModel.State.SAFE) {
                HashMap hashMap = new HashMap(1);
                hashMap.put("module_show", next.getTrackStr());
                d("newcheck_result_action_f1", hashMap);
            }
        }
    }

    public static void c() {
        d.a(new x());
    }

    public static void c(long j) {
        d.a(new n(j));
    }

    public static void c(Context context) {
        long j = 1;
        b("toggle_allow_networking", h.i() ? 1 : 0);
        b("toggle_receive_monthly_report", g.b() ? 1 : 0);
        b("toggle_display_on_notification_bar", h.a(context.getContentResolver()) ? 1 : 0);
        b("toggle_shortcut_onekey_clean", e.b(context, e.a.QUICk_CLEANUP) ? 1 : 0);
        b("toggle_shortcut_powerclean", e.b(context, e.a.POWER_CLEANUP) ? 1 : 0);
        b("toggle_shortcut_antispam", e.b(context, e.a.ANTISPAM) ? 1 : 0);
        b("toggle_shortcut_virusscan", e.b(context, e.a.VIRUS_CENTER) ? 1 : 0);
        b("toggle_shortcut_trashclean", e.b(context, e.a.CLEANMASTER) ? 1 : 0);
        b("toggle_shortcut_network_diagnostics", e.b(context, e.a.NETWORK_DIAGNOSTICS) ? 1 : 0);
        b("toggle_shortcut_networkassistant", e.b(context, e.a.NETWORK_ASSISTANT) ? 1 : 0);
        b("toggle_shortcut_permissionmanage", e.b(context, e.a.PERM_CENTER) ? 1 : 0);
        b("toggle_shortcut_powersaving", e.b(context, e.a.POWER_CENTER) ? 1 : 0);
        b("toggle_shortcut_lucky_money", e.b(context, e.a.LUCKY_MONEY) ? 1 : 0);
        boolean l = M.l();
        boolean m = M.m();
        b("toggle_news_onlywlan", l ? 1 : 0);
        if (!m) {
            j = 0;
        }
        b("toggle_recommend_news", j);
        e(context);
    }

    /* access modifiers changed from: private */
    public static void c(String str, long j) {
        AnalyticsUtil.recordCalculateEvent("securitycenter", str, j, (Map<String, String>) null);
    }

    /* access modifiers changed from: private */
    public static void c(String str, Map<String, String> map) {
        AnalyticsUtil.recordCalculateEvent("securitycenter", str, map);
    }

    public static void d() {
        d.a(new D());
    }

    public static void d(long j) {
        d.a(new r(j));
    }

    /* access modifiers changed from: private */
    public static void d(Context context) {
        d.a(new C0538d(context));
    }

    /* access modifiers changed from: private */
    public static void d(String str, Map<String, String> map) {
        AnalyticsUtil.recordCountEvent("securitycenter", str, map);
    }

    public static void e() {
        d.a(new E());
    }

    public static void e(long j) {
        d.a(new p(j));
    }

    private static void e(Context context) {
        new t(context).execute(new Void[0]);
    }

    public static void f() {
        d.a(new m());
    }

    public static void f(long j) {
        d.a(new o(j));
    }

    public static void g() {
        d.a(new s());
    }

    public static void g(long j) {
        d.a(new l(j));
    }

    public static void h() {
        AnalyticsUtil.recordCountEvent("securitycenter", "phonemanage_list_scorll");
    }

    public static void h(long j) {
        d.a(new i(j));
    }

    public static void i() {
        d.a(new A());
    }

    public static void i(long j) {
        d.a(new k(j));
    }

    public static void i(String str) {
        HashMap hashMap = new HashMap(1);
        hashMap.put("module_click", str);
        d("first_aid_ad", hashMap);
    }

    public static void j() {
        d.a(new C());
    }

    public static void j(long j) {
        d.a(new j(j));
    }

    public static void j(String str) {
        HashMap hashMap = new HashMap(1);
        hashMap.put("module_click", str);
        d("first_aid_activity", hashMap);
    }

    public static void k() {
        d.a(new B());
    }

    public static void k(String str) {
        HashMap hashMap = new HashMap(1);
        hashMap.put("module_click", str);
        d("firstaidkit_resultpage_function", hashMap);
    }

    public static void l() {
        AnalyticsUtil.recordCountEvent(MiStatUtil.KEY_LUCK_MONEY_REMINDED_WEIXIN_POSTFIX, "alert_notification", (Map<String, String>) null);
    }

    public static void l(String str) {
        HashMap hashMap = new HashMap(1);
        hashMap.put("module_click", str);
        d("first_aid_news", hashMap);
    }

    public static void m() {
        AnalyticsUtil.recordCountEvent("whatsapp", "size_notificaiton", (Map<String, String>) null);
    }

    public static void m(String str) {
        A(str);
    }

    public static void n() {
        HashMap hashMap = new HashMap(1);
        hashMap.put("module_show", "close_network_dialog_show");
        d("close_network_dialog", hashMap);
    }

    public static void n(String str) {
        AnalyticsUtil.recordCountEvent(com.xiaomi.stat.a.f8359d, str, (Map<String, String>) null);
    }

    public static void o() {
        A("settings_cloud_data_update");
    }

    public static void o(String str) {
        d.a(new C0537c(str));
    }

    public static void p(String str) {
        HashMap hashMap = new HashMap(1);
        hashMap.put("module_click", str);
        d("newcheck_result_action_activity", hashMap);
    }

    public static void q(String str) {
        HashMap hashMap = new HashMap(1);
        hashMap.put("module_click", str);
        d("newcheck_result_action_ad", hashMap);
    }

    public static void r(String str) {
        HashMap hashMap = new HashMap(1);
        hashMap.put("module_click", str);
        d("newcheck_result_action_f1", hashMap);
    }

    public static void s(String str) {
        HashMap hashMap = new HashMap(1);
        hashMap.put("module_click", str);
        d("newcheck_result_action_news", hashMap);
    }

    public static void t(String str) {
        HashMap hashMap = new HashMap(1);
        hashMap.put("module_click", str);
        d("phone_manage_show_click", hashMap);
    }

    public static void u(String str) {
        HashMap hashMap = new HashMap(1);
        hashMap.put("module_click", str);
        d("slide_down_action_activity", hashMap);
    }

    public static void v(String str) {
        HashMap hashMap = new HashMap(1);
        hashMap.put("module_click", str);
        d("slide_down_action_ad", hashMap);
    }

    public static void w(String str) {
        HashMap hashMap = new HashMap(1);
        hashMap.put("module_click", str);
        d("slide_down_action_f", hashMap);
    }

    public static void x(String str) {
        d.a(new C0540f(str));
    }

    public static void y(String str) {
        HashMap hashMap = new HashMap();
        hashMap.put("page", str);
        d("tab_switch_page", hashMap);
    }

    public static void z(String str) {
        HashMap hashMap = new HashMap(1);
        hashMap.put(Constants.JSON_KEY_MODULE, str);
        d("notification_click", hashMap);
    }
}
