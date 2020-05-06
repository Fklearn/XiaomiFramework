package com.miui.luckymoney.config;

import android.content.Context;
import android.text.TextUtils;
import android.util.Base64;
import com.miui.common.persistence.b;
import com.miui.luckymoney.config.CommonPerConstants;
import com.miui.luckymoney.utils.DateUtil;
import java.util.ArrayList;

public class CommonConfig {
    private static CommonConfig sInstance;
    private Context mContext;

    private CommonConfig(Context context) {
        this.mContext = context.getApplicationContext();
    }

    private String base64EncodeFrom(String str) {
        return TextUtils.isEmpty(str) ? "" : Base64.encodeToString(str.getBytes(), 11);
    }

    public static synchronized CommonConfig getInstance(Context context) {
        CommonConfig commonConfig;
        synchronized (CommonConfig.class) {
            if (sInstance == null) {
                sInstance = new CommonConfig(context);
            }
            commonConfig = sInstance;
        }
        return commonConfig;
    }

    public String getAdsConfig() {
        return b.a(CommonPerConstants.KEY.ADS_CONFIG, "");
    }

    public boolean getBusinessLuckyWarningEnable() {
        return b.a(CommonPerConstants.KEY.BUSINESS_LUCKY_WARNING_ENABLE, true);
    }

    public int getDNDModeLevel() {
        return b.a(CommonPerConstants.KEY.DO_NOT_DISTURB_MODE_LEVEL, CommonPerConstants.DEFAULT.DO_NOT_DISTURB_MODE_LEVEL_DEFAULT);
    }

    public long getDNDStartTime() {
        return b.a(CommonPerConstants.KEY.DND_START_TIME, 0);
    }

    public long getDNDStopTime() {
        return b.a(CommonPerConstants.KEY.DND_STOP_TIME, 25200000);
    }

    public long getDefaultUpdateFrequency() {
        return b.a(CommonPerConstants.KEY.DEFAULT_UPDATE_FREQUENCY, (long) CommonPerConstants.DEFAULT.DEFAULT_UPDATE_FREQUENCY_DEFAULT);
    }

    public boolean getDesktopFloatWindowEnable() {
        return b.a(CommonPerConstants.KEY.DESKTOP_FLOAT_WINDOW_ENABLE, false);
    }

    public synchronized ArrayList<String> getFastOpenConfig() {
        if (isFastOpenConfigFirstLoad()) {
            setFastOpenConfigFirstLoad(false);
            return null;
        }
        return b.a(CommonPerConstants.KEY.FAST_OPEN_CONFIG, (ArrayList<String>) new ArrayList());
    }

    public String getFloatActivityDefaultConfig() {
        return b.a(CommonPerConstants.KEY.FLOAT_ACTIVITY_DEFAULT_CONFIG, (String) null);
    }

    public String getFloatAssistantConfig() {
        return b.a(CommonPerConstants.KEY.FLOAT_ASSISTANT_VIEW_CONFIG_DATA, (String) null);
    }

    public String getFloatTipsConfig() {
        return b.a(CommonPerConstants.KEY.FLOAT_TIPS_ALARM_CONFIG_DATA, (String) null);
    }

    public long getFloatTipsDuration() {
        return 300000;
    }

    public String getFloatTipsImageLeft() {
        return b.a(CommonPerConstants.KEY.FLOAT_TIPS_IMAGE_LEFT, (String) null);
    }

    public String getFloatTipsImageRight() {
        return b.a(CommonPerConstants.KEY.FLOAT_TIPS_IMAGE_RIGHT, (String) null);
    }

    public long getFloatTipsStartTime() {
        return b.a(CommonPerConstants.KEY.FLOAT_TIPS_START_TIME, 0);
    }

    public long getFloatTipsStopTime() {
        return b.a(CommonPerConstants.KEY.FLOAT_TIPS_STOP_TIME, 1);
    }

    public long getFloatTipsUpdateTime() {
        return b.a(CommonPerConstants.KEY.FLOAT_TIPS_UPDATE_TIME, 0);
    }

    public long getHotEndTime() {
        return b.a(CommonPerConstants.KEY.HOT_END_TIME, 1);
    }

    public long getHotFrequency() {
        return b.a(CommonPerConstants.KEY.HOT_UPDATE_FREQUENCY, 21600000);
    }

    public long getHotStartTime() {
        return b.a(CommonPerConstants.KEY.HOT_START_TIME, 0);
    }

    public int getLastFloatViewXPos() {
        return b.a(CommonPerConstants.KEY.LAST_FLOAT_VIEW_X_POS, (int) CommonPerConstants.DEFAULT.LAST_FLOAT_VIEW_X_POS_DEFAULT);
    }

    public int getLastFloatViewYPos() {
        return b.a(CommonPerConstants.KEY.LAST_FLOAT_VIEW_Y_POS, (int) CommonPerConstants.DEFAULT.LAST_FLOAT_VIEW_Y_POS_DEFAULT);
    }

    public long getLastRecordMoneyTime() {
        return b.a(CommonPerConstants.KEY.LAST_RECORD_MONEY_TIME, 0);
    }

    public long getLastTimeCheckUpdateConfig() {
        return b.a(CommonPerConstants.KEY.LAST_TIME_CHECK_UPDATE_CONFIG, 0);
    }

    public String getLuckyAlarmConfig() {
        return b.a(CommonPerConstants.KEY.LUCKY_ALARM_CONFIG, (String) null);
    }

    public boolean getLuckyAlarmEnable() {
        return b.a(CommonPerConstants.KEY.LUCKY_ALARM_ENABLE, true);
    }

    public boolean getLuckyAlarmPackageOpen(String str) {
        return b.a(CommonPerConstants.KEY.LUCKY_ALARM_SETTINGS_PACKAGE + base64EncodeFrom(str), true);
    }

    public boolean getLuckyAlarmSoundEnable() {
        return b.a(CommonPerConstants.KEY.LUCKY_ALARM_SOUND_ENABLE, true);
    }

    public int getLuckyCountFrom(String str) {
        return b.a(CommonPerConstants.KEY.PREX_LUCKY_COUNT_FROM + base64EncodeFrom(str), 0);
    }

    public String getLuckyMaxSource() {
        return b.a(CommonPerConstants.KEY.LUCKY_MAX_SOURCE, CommonPerConstants.DEFAULT.LUCKY_MAX_SOURCE_DEFAULT);
    }

    public boolean getLuckySoundWarningEnable() {
        return b.a(CommonPerConstants.KEY.LUCKY_SOUND_WARNING_ENABLE, true);
    }

    public int getLuckySoundWarningLevel() {
        return b.a(CommonPerConstants.KEY.LUCKY_SOUND_WARNING_LEVEL, 1);
    }

    public long getMMMoney() {
        return b.a(CommonPerConstants.KEY.MM_MONEY, 0);
    }

    public String getMasterSwitchConfig() {
        return b.a(CommonPerConstants.KEY.MASTER_SWITCH_CONFIG, (String) null);
    }

    public boolean getMiliaoLuckyWarningEnable() {
        return b.a(CommonPerConstants.KEY.MILIAO_LUCKY_WARNING_ENABLE, true);
    }

    public boolean getOnlyNotiGroupLuckuMoneyConfig() {
        return b.a(CommonPerConstants.KEY.ONLY_NOTI_GROUP_LUCKY_MONEY, false);
    }

    public int getPersonalLuckyCountFrom(String str) {
        return b.a(CommonPerConstants.KEY.PREX_PERSONAL_LUCKY_COUNT_FROM + base64EncodeFrom(str), 0);
    }

    public String getPersonalLuckyMaxSource() {
        return b.a(CommonPerConstants.KEY.PERSONAL_LUCKY_MAX_SOURCE, CommonPerConstants.DEFAULT.LUCKY_MAX_SOURCE_DEFAULT);
    }

    public boolean getQQLuckyWarningEnable() {
        return b.a(CommonPerConstants.KEY.QQ_LUCKY_WARNING_ENABLE, true);
    }

    public long getQQMoney() {
        return b.a(CommonPerConstants.KEY.QQ_MONEY, 0);
    }

    public long getReceiveTotalLuckyMoney() {
        return b.a(CommonPerConstants.KEY.RECEIVE_TOTAL_LUCKY_MONEY, 0);
    }

    public long getSettingSwitchUpdateTime() {
        return b.a(CommonPerConstants.KEY.SETTING_SWITCH_STATE_UPLOAD_TIME, 0);
    }

    public long getTodayMMMoney() {
        return b.a(CommonPerConstants.KEY.MM_MONEY_TODAY, 0);
    }

    public long getTodayQQMoney() {
        return b.a(CommonPerConstants.KEY.QQ_MONEY_TODAY, 0);
    }

    public long getWarningLuckyMoneyCount() {
        return b.a(CommonPerConstants.KEY.WARNING_LUCKY_MONEY_COUNT, 0);
    }

    public boolean getWeChatLuckyWarningEnable() {
        return b.a(CommonPerConstants.KEY.LUCKY_WARNING_ENABLE, true);
    }

    public boolean getXiaomiLuckyMoneyEnable() {
        return b.a(CommonPerConstants.KEY.XIAOMI_LUCKY_MONEY_ENABLE, false);
    }

    public boolean isConfigChanged() {
        return b.a(CommonPerConstants.KEY.IS_CONFIG_CHANGED, false);
    }

    public boolean isDNDModeEffective() {
        return isDNDModeOpen() && isDuringDNDTime();
    }

    public boolean isDNDModeOpen() {
        return b.a(CommonPerConstants.KEY.DO_NOT_DISTURB_MODE, false);
    }

    public boolean isDesktopFloatWindowEnable() {
        return getDesktopFloatWindowEnable() && getXiaomiLuckyMoneyEnable();
    }

    public boolean isDuringDNDTime() {
        long currentTimeMillis = System.currentTimeMillis() - DateUtil.getTodayTimeMillis();
        long dNDStartTime = getDNDStartTime();
        long dNDStopTime = getDNDStopTime();
        return dNDStartTime <= dNDStopTime ? currentTimeMillis >= dNDStartTime && currentTimeMillis <= dNDStopTime : (currentTimeMillis >= dNDStartTime && currentTimeMillis < 86400000) || currentTimeMillis <= dNDStopTime;
    }

    public synchronized boolean isFastOpenConfigFirstLoad() {
        return b.a(CommonPerConstants.KEY.FAST_OPEN_CONFIG_FIRST_LOAD, true);
    }

    public boolean isFastOpenEnable() {
        return b.a(CommonPerConstants.KEY.FAST_OPEN_MODE, false);
    }

    public boolean isFirstStartUp() {
        return b.a(CommonPerConstants.KEY.FIRST_START_UP, true);
    }

    public boolean isShouldUserTips() {
        return b.a(CommonPerConstants.KEY.SHOULD_TIPS, true);
    }

    public void saveReceiveTotalLuckyMoney(long j) {
        b.b(CommonPerConstants.KEY.RECEIVE_TOTAL_LUCKY_MONEY, j);
    }

    public void setAdsConfig(String str) {
        b.b(CommonPerConstants.KEY.ADS_CONFIG, str);
    }

    public void setBusinessLuckyWarningEnable(boolean z) {
        b.b(CommonPerConstants.KEY.BUSINESS_LUCKY_WARNING_ENABLE, z);
    }

    public void setConfigChanged(boolean z) {
        b.b(CommonPerConstants.KEY.IS_CONFIG_CHANGED, z);
    }

    public void setDNDModeEnable(boolean z) {
        b.b(CommonPerConstants.KEY.DO_NOT_DISTURB_MODE, z);
    }

    public void setDNDModeLevel(int i) {
        b.b(CommonPerConstants.KEY.DO_NOT_DISTURB_MODE_LEVEL, i);
    }

    public void setDNDStartTime(long j) {
        b.b(CommonPerConstants.KEY.DND_START_TIME, j);
    }

    public void setDNDStopTime(long j) {
        b.b(CommonPerConstants.KEY.DND_STOP_TIME, j);
    }

    public void setDefaultUpdateFrequency(long j) {
        b.b(CommonPerConstants.KEY.DEFAULT_UPDATE_FREQUENCY, j);
    }

    public void setDesktopFloatWindowEnable(boolean z) {
        b.b(CommonPerConstants.KEY.DESKTOP_FLOAT_WINDOW_ENABLE, z);
    }

    public synchronized void setFastOpenConfig(ArrayList<String> arrayList) {
        b.b(CommonPerConstants.KEY.FAST_OPEN_CONFIG, arrayList);
    }

    public synchronized void setFastOpenConfigFirstLoad(boolean z) {
        b.b(CommonPerConstants.KEY.FAST_OPEN_CONFIG_FIRST_LOAD, z);
    }

    public void setFastOpenEnable(boolean z) {
        b.b(CommonPerConstants.KEY.FAST_OPEN_MODE, z);
    }

    public void setFirstStartUp(boolean z) {
        b.b(CommonPerConstants.KEY.FIRST_START_UP, z);
    }

    public void setFloatActivityDefaultConfig(String str) {
        b.b(CommonPerConstants.KEY.FLOAT_ACTIVITY_DEFAULT_CONFIG, str);
    }

    public void setFloatAssistantConfig(String str) {
        b.b(CommonPerConstants.KEY.FLOAT_ASSISTANT_VIEW_CONFIG_DATA, str);
    }

    public void setFloatTipsConfig(String str) {
        b.b(CommonPerConstants.KEY.FLOAT_TIPS_ALARM_CONFIG_DATA, str);
    }

    public void setFloatTipsImageLeft(String str) {
        b.b(CommonPerConstants.KEY.FLOAT_TIPS_IMAGE_LEFT, str);
    }

    public void setFloatTipsImageRight(String str) {
        b.b(CommonPerConstants.KEY.FLOAT_TIPS_IMAGE_RIGHT, str);
    }

    public void setFloatTipsStartTime(long j) {
        b.b(CommonPerConstants.KEY.FLOAT_TIPS_START_TIME, j);
    }

    public void setFloatTipsStopTime(long j) {
        b.b(CommonPerConstants.KEY.FLOAT_TIPS_STOP_TIME, j);
    }

    public void setFloatTipsUpdateTime(long j) {
        b.b(CommonPerConstants.KEY.FLOAT_TIPS_UPDATE_TIME, j);
    }

    public void setHotEndTime(long j) {
        b.b(CommonPerConstants.KEY.HOT_END_TIME, j);
    }

    public void setHotFrequency(long j) {
        b.b(CommonPerConstants.KEY.HOT_UPDATE_FREQUENCY, j);
    }

    public void setHotStartTime(long j) {
        b.b(CommonPerConstants.KEY.HOT_START_TIME, j);
    }

    public void setLastFloatViewXPos(int i) {
        b.b(CommonPerConstants.KEY.LAST_FLOAT_VIEW_X_POS, i);
    }

    public void setLastFloatViewYPos(int i) {
        b.b(CommonPerConstants.KEY.LAST_FLOAT_VIEW_Y_POS, i);
    }

    public void setLastRecordMoneyTime(long j) {
        b.b(CommonPerConstants.KEY.LAST_RECORD_MONEY_TIME, j);
    }

    public void setLastTimeCheckUpdateConfig(long j) {
        b.b(CommonPerConstants.KEY.LAST_TIME_CHECK_UPDATE_CONFIG, j);
    }

    public void setLuckyAlarmConfig(String str) {
        b.b(CommonPerConstants.KEY.LUCKY_ALARM_CONFIG, str);
    }

    public void setLuckyAlarmEnable(boolean z) {
        if (getLuckyAlarmEnable() ^ z) {
            setConfigChanged(true);
        }
        b.b(CommonPerConstants.KEY.LUCKY_ALARM_ENABLE, z);
    }

    public void setLuckyAlarmPackageOpen(String str, boolean z) {
        b.b(CommonPerConstants.KEY.LUCKY_ALARM_SETTINGS_PACKAGE + base64EncodeFrom(str), z);
    }

    public void setLuckyAlarmSoundEnable(boolean z) {
        b.b(CommonPerConstants.KEY.LUCKY_ALARM_SOUND_ENABLE, z);
    }

    public void setLuckyCountFrom(String str, int i) {
        b.b(CommonPerConstants.KEY.PREX_LUCKY_COUNT_FROM + base64EncodeFrom(str), i);
    }

    public void setLuckyMaxSource(String str) {
        b.b(CommonPerConstants.KEY.LUCKY_MAX_SOURCE, str);
    }

    public void setLuckySoundWarningEnable(boolean z) {
        b.b(CommonPerConstants.KEY.LUCKY_SOUND_WARNING_ENABLE, z);
    }

    public void setLuckySoundWarningLevel(int i) {
        b.b(CommonPerConstants.KEY.LUCKY_SOUND_WARNING_LEVEL, i);
    }

    public void setMMMoney(long j) {
        b.b(CommonPerConstants.KEY.MM_MONEY, j);
    }

    public void setMasterSwitchConfig(String str) {
        b.b(CommonPerConstants.KEY.MASTER_SWITCH_CONFIG, str);
    }

    public void setMiliaoLuckyWarningEnable(boolean z) {
        b.b(CommonPerConstants.KEY.MILIAO_LUCKY_WARNING_ENABLE, z);
    }

    public void setOnlyNotiGroupLuckuMoneyConfig(boolean z) {
        b.b(CommonPerConstants.KEY.ONLY_NOTI_GROUP_LUCKY_MONEY, z);
    }

    public void setPersonalLuckyCountFrom(String str, int i) {
        b.b(CommonPerConstants.KEY.PREX_PERSONAL_LUCKY_COUNT_FROM + base64EncodeFrom(str), i);
    }

    public void setPersonalLuckyMaxSource(String str) {
        b.b(CommonPerConstants.KEY.PERSONAL_LUCKY_MAX_SOURCE, str);
    }

    public void setQQLuckyWarningEnable(boolean z) {
        b.b(CommonPerConstants.KEY.QQ_LUCKY_WARNING_ENABLE, z);
    }

    public void setQQMoney(long j) {
        b.b(CommonPerConstants.KEY.QQ_MONEY, j);
    }

    public void setSettingSwitchUpdateTime(long j) {
        b.b(CommonPerConstants.KEY.SETTING_SWITCH_STATE_UPLOAD_TIME, j);
    }

    public void setShouldCleanResDir(boolean z) {
        b.b(CommonPerConstants.KEY.SHOULD_CLEAN_RES_DIR, z);
    }

    public void setShouldUserTips(boolean z) {
        b.b(CommonPerConstants.KEY.SHOULD_TIPS, z);
    }

    public void setTodayMMMoney(long j) {
        b.b(CommonPerConstants.KEY.MM_MONEY_TODAY, j);
    }

    public void setTodayQQMoney(long j) {
        b.b(CommonPerConstants.KEY.QQ_MONEY_TODAY, j);
    }

    public void setWarningLuckyMoneyCount(long j) {
        b.b(CommonPerConstants.KEY.WARNING_LUCKY_MONEY_COUNT, j);
    }

    public void setWeChatLuckyWarningEnable(boolean z) {
        b.b(CommonPerConstants.KEY.LUCKY_WARNING_ENABLE, z);
    }

    public void setXiaomiLuckyMoneyEnable(boolean z) {
        if (getXiaomiLuckyMoneyEnable() ^ z) {
            setConfigChanged(true);
        }
        b.b(CommonPerConstants.KEY.XIAOMI_LUCKY_MONEY_ENABLE, z);
    }

    public boolean shouldCleanResDir() {
        return b.a(CommonPerConstants.KEY.SHOULD_CLEAN_RES_DIR, true);
    }
}
