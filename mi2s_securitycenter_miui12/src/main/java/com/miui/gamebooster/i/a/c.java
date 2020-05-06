package com.miui.gamebooster.i.a;

import android.support.annotation.Nullable;
import com.market.sdk.utils.b;
import com.miui.analytics.AnalyticsUtil;
import com.miui.gamebooster.globalgame.module.BannerCardBean;
import com.miui.gamebooster.globalgame.module.CardType;
import com.miui.gamebooster.globalgame.module.GameListItem;
import com.miui.gamebooster.globalgame.util.Utils;
import com.miui.networkassistant.ui.activity.NetworkDiagnosticsTipActivity;
import java.util.HashMap;

public class c extends a {
    private static String a(@CardType.Type int i) {
        switch (i) {
            case 1:
                return "8_icon_card";
            case 2:
                return "3_icon_card";
            case 3:
                return "rec_big_card";
            case 4:
                return "rec_small_card";
            case 5:
                return "pic_banner";
            case 8:
            case 10:
            case 11:
                return "H5_game_card";
            case 9:
                return "text_banner";
            default:
                return "ignored";
        }
    }

    public static void a() {
        HashMap hashMap = new HashMap(3);
        a.a(hashMap);
        hashMap.put(NetworkDiagnosticsTipActivity.DETAIL_KEY_NAME, "error");
        AnalyticsUtil.recordCountEvent("gamebooster", "exposure_info_main", hashMap);
        a.a("event:detailError", "exposure_info_main", hashMap);
    }

    public static void a(int i, BannerCardBean bannerCardBean) {
        GameListItem gameListItem = b.a(bannerCardBean.gameList) ? null : bannerCardBean.gameList.get(0);
        a(i, bannerCardBean.title, bannerCardBean.type, gameListItem == null ? "" : gameListItem.name);
    }

    private static void a(int i, @Nullable String str, @CardType.Type int i2, @Nullable String str2) {
        HashMap hashMap = new HashMap();
        a.a(hashMap);
        hashMap.put("index", String.valueOf(i + 1));
        String str3 = "";
        if (str == null) {
            str = str3;
        }
        hashMap.put("card_name", str);
        hashMap.put("card_type", a(i2));
        if (str2 != null) {
            str3 = str2;
        }
        hashMap.put("game_name", str3);
        AnalyticsUtil.recordCountEvent("gamebooster", "info_card_show", hashMap);
        a.a("event:itemShown", "info_card_show", hashMap);
    }

    public static void a(int i, @Nullable String str, @CardType.Type int i2, @Nullable String str2, int i3) {
        HashMap hashMap = new HashMap();
        a.a(hashMap);
        hashMap.put("index", String.valueOf(i + 1));
        String str3 = "";
        if (str == null) {
            str = str3;
        }
        hashMap.put("card_name", str);
        hashMap.put("card_type", a(i2));
        hashMap.put("game_index", String.valueOf(i3 + 1));
        if (str2 != null) {
            str3 = str2;
        }
        hashMap.put("game_name", str3);
        AnalyticsUtil.recordCountEvent("gamebooster", "info_card_click", hashMap);
        a.a("event:cardClick", "info_card_click", hashMap);
    }

    public static void b() {
        HashMap hashMap = new HashMap(3);
        a.a(hashMap);
        hashMap.put(NetworkDiagnosticsTipActivity.DETAIL_KEY_NAME, "bottom");
        AnalyticsUtil.recordCountEvent("gamebooster", "exposure_info_main", hashMap);
        a.a("event:detailScrolledToBottom", "exposure_info_main", hashMap);
    }

    public static void c() {
        HashMap hashMap = new HashMap(3);
        a.a(hashMap);
        hashMap.put(NetworkDiagnosticsTipActivity.DETAIL_KEY_NAME, "show");
        AnalyticsUtil.recordCountEvent("gamebooster", "exposure_info_main", hashMap);
        a.a("event:detailShown", "exposure_info_main", hashMap);
        Utils.p();
    }
}
