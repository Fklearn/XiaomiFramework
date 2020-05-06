package com.miui.gamebooster.i.a;

import android.content.Context;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import com.miui.analytics.AnalyticsUtil;
import com.miui.applicationlock.c.K;
import com.miui.luckymoney.stats.MiStatUtil;
import com.miui.networkassistant.ui.activity.NetworkDiagnosticsTipActivity;
import java.util.HashMap;
import java.util.Map;

public class b extends a {

    /* renamed from: a  reason: collision with root package name */
    private static final String f4433a = "add";

    /* renamed from: b  reason: collision with root package name */
    private static final String f4434b = "success";

    /* renamed from: c  reason: collision with root package name */
    private static final String f4435c = "fail";

    public static void a(Context context) {
        HashMap hashMap = new HashMap();
        a(context, (Map<String, String>) hashMap);
        AnalyticsUtil.recordCountEvent("gamebooster", "exposure_main", hashMap);
        a.a("event:exposeMain", "exposure_main", hashMap);
    }

    public static void a(Context context, int i) {
        HashMap hashMap = new HashMap();
        a(context, (Map<String, String>) hashMap);
        hashMap.put("display", String.valueOf(i));
        AnalyticsUtil.recordCountEvent("gamebooster", "game_count", hashMap);
        a.a("event:exposureGameCount", "game_count", hashMap);
    }

    public static void a(Context context, @Nullable String str, int i, boolean z) {
        HashMap hashMap = new HashMap();
        a(context, (Map<String, String>) hashMap);
        hashMap.put(NetworkDiagnosticsTipActivity.TITLE_KEY_NAME, TextUtils.isEmpty(str) ? "" : f4433a);
        hashMap.put("position", String.valueOf(i));
        String str2 = z ? "game_click_icon" : "game_click_start";
        AnalyticsUtil.recordCountEvent("gamebooster", str2, hashMap);
        a.a("event:exposureGameClick", str2, hashMap);
    }

    public static void a(Context context, String str, boolean z) {
        HashMap hashMap = new HashMap();
        a(context, (Map<String, String>) hashMap);
        hashMap.put(NetworkDiagnosticsTipActivity.TITLE_KEY_NAME, str);
        String str2 = z ? "game_add" : "game_remove";
        AnalyticsUtil.recordCountEvent("gamebooster", str2, hashMap);
        a.a("event:exposureGameStatusChange", str2, hashMap);
    }

    private static void a(Context context, Map<String, String> map) {
        a.a(map);
        map.put("isLogin", String.valueOf(K.c(context) ? 1 : 0));
    }

    public static void a(Context context, boolean z) {
        HashMap hashMap = new HashMap();
        a(context, (Map<String, String>) hashMap);
        hashMap.put("result", z ? f4434b : f4435c);
        AnalyticsUtil.recordCountEvent("gamebooster", "feed_request", hashMap);
        a.a("event:exposureFeedRequest", "feed_request", hashMap);
    }

    public static void b(Context context) {
        HashMap hashMap = new HashMap();
        a(context, (Map<String, String>) hashMap);
        AnalyticsUtil.recordCountEvent("gamebooster", "exposure_add_game", hashMap);
        a.a("event:exposureAddGame", "exposure_add_game", hashMap);
    }

    public static void c(Context context) {
        HashMap hashMap = new HashMap();
        a(context, (Map<String, String>) hashMap);
        AnalyticsUtil.recordCountEvent("gamebooster", "feed_entry_show", hashMap);
        a.a("event:exposureFeedEntryShow", "feed_entry_show", hashMap);
    }

    public static void d(Context context) {
        HashMap hashMap = new HashMap();
        a(context, (Map<String, String>) hashMap);
        hashMap.put("operate", "back_to_top");
        AnalyticsUtil.recordCountEvent("gamebooster", "feed_operate", hashMap);
        a.a("event:exposureFeedOperateBackToTop", "feed_operate", hashMap);
    }

    public static void e(Context context) {
        HashMap hashMap = new HashMap();
        a(context, (Map<String, String>) hashMap);
        hashMap.put("operate", MiStatUtil.CLOSE);
        AnalyticsUtil.recordCountEvent("gamebooster", "feed_operate", hashMap);
        a.a("event:exposureFeedOperateClose", "feed_operate", hashMap);
    }

    public static void f(Context context) {
        HashMap hashMap = new HashMap();
        a(context, (Map<String, String>) hashMap);
        hashMap.put("operate", "expand");
        AnalyticsUtil.recordCountEvent("gamebooster", "feed_operate", hashMap);
        a.a("event:exposureFeedOperateExpand", "feed_operate", hashMap);
    }
}
