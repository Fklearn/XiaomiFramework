package com.miui.luckymoney.stats;

import android.content.Context;
import com.miui.activityutil.o;
import com.miui.analytics.AnalyticsUtil;
import com.miui.luckymoney.config.AppConstants;
import com.miui.luckymoney.config.CommonConfig;
import java.util.HashMap;
import java.util.Map;

public class MiStatUtil {
    private static final String CATEGORY_LUCKY_MONEY = "luckymoney";
    public static final String CLOSE = "close";
    private static final String EVENT_LUCKY_MM_MONEY = "luckymoney_wechat_sum";
    private static final String EVENT_LUCKY_QQ_MONEY = "luckymoney_qq_sum";
    private static final String EVENT_LUCKY_TOGGLESTATE = "luckymoney_toggle_state";
    private static final String GUIDE_PAGE_VISIT_OPEN = "guide_page_visit_open";
    private static final String KEY_ADS = "luckymoney_ad_show";
    private static final String KEY_BUSSNESS_SWITCH = "toggle_enterprise";
    private static final String KEY_DND_MODE_SWITCH = "toggle_no_disturb";
    private static final String KEY_DND_WAY = "toggle_no_disturb_way";
    private static final String KEY_FAST_OPEN_SHOW = "quickly_model_show";
    private static final String KEY_FAST_OPEN_SWITCH = "toggle_quickly_model";
    private static final String KEY_FLOAT_TIPS = "mi_rabbit_bubble";
    private static final String KEY_FLOAT_WINDOW = "toggle_suspension_window";
    private static final String KEY_FLOAT_WINDOW_CLICK = "mi_rabbit_click";
    private static final String KEY_FLOAT_WINDOW_FUNC_CLICK = "mi_rabbit_detail";
    private static final String KEY_FLOAT_WINDOW_HOT_CLICK = "red_packet_party";
    private static final String KEY_FUNC_NO_WORK = "remind_abnormal_click";
    private static final String KEY_LUCKY_ALARM_ALIPAY = "toggle_alarm_alipay";
    private static final String KEY_LUCKY_ALARM_LOCKED_NOTI = "alarm_popup_1";
    private static final String KEY_LUCKY_ALARM_MI = "toggle_alarm_mi";
    private static final String KEY_LUCKY_ALARM_NOTI = "alarm_popup_2";
    private static final String KEY_LUCKY_ALARM_QQ = "toggle_alarm_qq";
    private static final String KEY_LUCKY_ALARM_SOUND = "toggle_alarm_sound";
    private static final String KEY_LUCKY_ALARM_SWITCH = "toggle_alarm";
    private static final String KEY_LUCKY_ALARM_WECHAT = "toggle_alarm_wechat";
    private static final String KEY_LUCKY_FAST_OPEN = "quickly_model_packet_show";
    private static final String KEY_LUCKY_MONEY_LOCKED_NOTI = "red_packet_popup_1";
    private static final String KEY_LUCKY_MONEY_NOTI = "red_packet_popup_2";
    private static final String KEY_LUCKY_SOUND_SWITCH = "red_packet_sound_remind";
    public static final String KEY_LUCK_MONEY_REMINDED_BUSINESS_POSTFIX = "enterprise";
    public static final String KEY_LUCK_MONEY_REMINDED_MITALK_POSTFIX = "mitalk";
    public static final String KEY_LUCK_MONEY_REMINDED_QQ_POSTFIX = "qq";
    public static final String KEY_LUCK_MONEY_REMINDED_WEIXIN_POSTFIX = "wechat";
    private static final String KEY_MASTER_SWITCH = "toggle_red_packet";
    private static final String KEY_MILIAO_SWITCH = "toggle_mitalk";
    private static final String KEY_MONEY = "luckymoney_sum";
    private static final String KEY_ONLY_GROUP_MESSAGE_SWITCH = "toggle_group_only";
    private static final String KEY_QQ_SWITCH = "toggle_qq";
    private static final String KEY_SHAKE_AND_SHAKE = "toggle_shake_expression";
    private static final String KEY_SHAKE_RANDOM_EXPRESSION = "shake_expression";
    private static final String KEY_SHARE_FUNC = "share_click";
    private static final String KEY_WECHAT_SWITCH = "toggle_wechat";
    private static final String MODULE = "module";
    private static final String MODULE_CLICK = "module_click";
    private static final String MODULE_SHOW = "module_show";
    public static final String SETTINGS = "settings";

    public static void recordAds(long j) {
        HashMap hashMap = new HashMap();
        hashMap.put("ad_show", Long.valueOf(j));
        recordCountEvent(CATEGORY_LUCKY_MONEY, KEY_ADS, hashMap);
    }

    private static void recordCalculateEvent(String str, long j) {
        AnalyticsUtil.recordCalculateEvent(CATEGORY_LUCKY_MONEY, str, j);
    }

    private static void recordCountEvent(String str, String str2) {
        AnalyticsUtil.recordCountEvent(str, str2, (Map<String, String>) null);
    }

    private static void recordCountEvent(String str, String str2, Map map) {
        AnalyticsUtil.recordCountEvent(str, str2, map);
    }

    public static void recordFastOpenShow() {
        recordCountEvent(CATEGORY_LUCKY_MONEY, KEY_FAST_OPEN_SHOW);
    }

    public static void recordFloatTips(String str, boolean z) {
        HashMap hashMap = new HashMap();
        hashMap.put(!z ? MODULE_SHOW : MODULE_CLICK, str);
        recordCountEvent(CATEGORY_LUCKY_MONEY, KEY_FLOAT_TIPS, hashMap);
    }

    public static void recordFloatWindowClick() {
        recordCountEvent(CATEGORY_LUCKY_MONEY, KEY_FLOAT_WINDOW_CLICK);
    }

    public static void recordFloatWindowFuncClick(String str) {
        HashMap hashMap = new HashMap();
        hashMap.put(MODULE_CLICK, str);
        recordCountEvent(CATEGORY_LUCKY_MONEY, KEY_FLOAT_WINDOW_FUNC_CLICK, hashMap);
    }

    public static void recordFloatWindowHot(String str, boolean z) {
        HashMap hashMap = new HashMap();
        hashMap.put(!z ? MODULE_SHOW : MODULE_CLICK, str);
        recordCountEvent(CATEGORY_LUCKY_MONEY, KEY_FLOAT_WINDOW_HOT_CLICK, hashMap);
    }

    public static void recordFuncNoWork() {
        recordCountEvent(CATEGORY_LUCKY_MONEY, KEY_FUNC_NO_WORK);
    }

    public static void recordGuidePage(boolean z) {
        HashMap hashMap = new HashMap();
        hashMap.put("module", z ? "open" : "visit");
        recordCountEvent(CATEGORY_LUCKY_MONEY, GUIDE_PAGE_VISIT_OPEN, hashMap);
    }

    public static void recordLuckyAlarmLockedNoti(String str, boolean z) {
        HashMap hashMap = new HashMap();
        hashMap.put(z ? MODULE_CLICK : MODULE_SHOW, str);
        recordCountEvent(CATEGORY_LUCKY_MONEY, KEY_LUCKY_ALARM_LOCKED_NOTI, hashMap);
    }

    public static void recordLuckyAlarmNoti(String str, boolean z) {
        HashMap hashMap = new HashMap();
        hashMap.put(z ? MODULE_CLICK : MODULE_SHOW, str);
        recordCountEvent(CATEGORY_LUCKY_MONEY, KEY_LUCKY_ALARM_NOTI, hashMap);
    }

    public static void recordLuckyMoneyFastOpen(String str) {
        HashMap hashMap = new HashMap();
        hashMap.put(MODULE_SHOW, str);
        recordCountEvent(CATEGORY_LUCKY_MONEY, KEY_LUCKY_FAST_OPEN, hashMap);
    }

    public static void recordLuckyMoneyLockedNoti(String str, boolean z) {
        HashMap hashMap = new HashMap();
        hashMap.put(z ? MODULE_CLICK : MODULE_SHOW, str);
        recordCountEvent(CATEGORY_LUCKY_MONEY, KEY_LUCKY_MONEY_LOCKED_NOTI, hashMap);
    }

    public static void recordLuckyMoneyNoti(String str, boolean z) {
        HashMap hashMap = new HashMap();
        hashMap.put(z ? MODULE_CLICK : MODULE_SHOW, str);
        recordCountEvent(CATEGORY_LUCKY_MONEY, KEY_LUCKY_MONEY_NOTI, hashMap);
    }

    public static void recordMMMoney(long j) {
        AnalyticsUtil.trackEvent(EVENT_LUCKY_MM_MONEY, KEY_MONEY, j);
    }

    private static void recordNumericEvent(String str, long j) {
        recordNumericEvent(CATEGORY_LUCKY_MONEY, str, j);
    }

    private static void recordNumericEvent(String str, String str2, long j) {
        AnalyticsUtil.recordNumericEvent(str, str2, j);
    }

    public static void recordPageEnd(String str) {
        AnalyticsUtil.recordPageEnd(str);
    }

    public static void recordPageStart(String str) {
        AnalyticsUtil.recordPageStart(str);
    }

    public static void recordQQMoney(long j) {
        AnalyticsUtil.trackEvent(EVENT_LUCKY_QQ_MONEY, KEY_MONEY, j);
    }

    public static void recordShakeRandomExpression() {
        recordCountEvent(CATEGORY_LUCKY_MONEY, KEY_SHAKE_RANDOM_EXPRESSION);
    }

    public static void recordShare(boolean z) {
        HashMap hashMap = new HashMap();
        hashMap.put("module", z ? "homepage_click" : "second_click");
        recordCountEvent(CATEGORY_LUCKY_MONEY, KEY_SHARE_FUNC, hashMap);
    }

    private static void recordStringEvent(String str, String str2) {
        AnalyticsUtil.recordStringPropertyEvent(CATEGORY_LUCKY_MONEY, str, str2);
    }

    public static void trackSettingSwitchState(Context context) {
        CommonConfig instance = CommonConfig.getInstance(context);
        long currentTimeMillis = System.currentTimeMillis();
        if (instance.getSettingSwitchUpdateTime() + 604800000 < currentTimeMillis) {
            HashMap hashMap = new HashMap();
            boolean xiaomiLuckyMoneyEnable = instance.getXiaomiLuckyMoneyEnable();
            String str = o.f2310b;
            hashMap.put(KEY_MASTER_SWITCH, xiaomiLuckyMoneyEnable ? str : o.f2309a);
            if (instance.getXiaomiLuckyMoneyEnable()) {
                hashMap.put(KEY_WECHAT_SWITCH, instance.getWeChatLuckyWarningEnable() ? str : o.f2309a);
                hashMap.put(KEY_QQ_SWITCH, instance.getQQLuckyWarningEnable() ? str : o.f2309a);
                hashMap.put(KEY_MILIAO_SWITCH, instance.getMiliaoLuckyWarningEnable() ? str : o.f2309a);
                hashMap.put(KEY_BUSSNESS_SWITCH, instance.getBusinessLuckyWarningEnable() ? str : o.f2309a);
                hashMap.put(KEY_ONLY_GROUP_MESSAGE_SWITCH, instance.getOnlyNotiGroupLuckuMoneyConfig() ? str : o.f2309a);
                hashMap.put(KEY_LUCKY_SOUND_SWITCH, String.valueOf(instance.getLuckySoundWarningLevel()));
                hashMap.put(KEY_DND_MODE_SWITCH, instance.isDNDModeOpen() ? str : o.f2309a);
                hashMap.put(KEY_FAST_OPEN_SWITCH, instance.isFastOpenEnable() ? str : o.f2309a);
                if (instance.isDNDModeOpen()) {
                    hashMap.put(KEY_DND_WAY, instance.getDNDModeLevel() == 0 ? "only_no_sound" : "no_remind");
                }
                hashMap.put(KEY_FLOAT_WINDOW, instance.isDesktopFloatWindowEnable() ? str : o.f2309a);
                hashMap.put(KEY_LUCKY_ALARM_SWITCH, instance.getLuckyAlarmEnable() ? str : o.f2309a);
                if (instance.getLuckyAlarmEnable()) {
                    hashMap.put(KEY_LUCKY_ALARM_SOUND, instance.getLuckyAlarmSoundEnable() ? str : o.f2309a);
                    hashMap.put(KEY_LUCKY_ALARM_WECHAT, instance.getLuckyAlarmPackageOpen(AppConstants.Package.PACKAGE_NAME_MM) ? str : o.f2309a);
                    hashMap.put(KEY_LUCKY_ALARM_QQ, instance.getLuckyAlarmPackageOpen(AppConstants.Package.PACKAGE_NAME_QQ) ? str : o.f2309a);
                    hashMap.put(KEY_LUCKY_ALARM_ALIPAY, instance.getLuckyAlarmPackageOpen(AppConstants.Package.PACKAGE_NAME_ALIPAY) ? str : o.f2309a);
                    if (!instance.getLuckyAlarmPackageOpen(AppConstants.Package.PACKAGE_NAME_MITALK)) {
                        str = o.f2309a;
                    }
                    hashMap.put(KEY_LUCKY_ALARM_MI, str);
                }
            }
            AnalyticsUtil.trackEvent(EVENT_LUCKY_TOGGLESTATE, hashMap);
            instance.setSettingSwitchUpdateTime(currentTimeMillis);
        }
    }
}
