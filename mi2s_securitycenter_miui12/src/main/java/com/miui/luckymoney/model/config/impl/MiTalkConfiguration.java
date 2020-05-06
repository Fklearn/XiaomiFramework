package com.miui.luckymoney.model.config.impl;

import android.content.Context;
import com.miui.luckymoney.model.config.BaseConfiguration;
import com.miui.luckymoney.stats.MiStatUtil;
import com.miui.securitycenter.R;

public class MiTalkConfiguration extends DefaultConfiguration {
    public MiTalkConfiguration(Context context) {
        super(context);
    }

    public int getHeadsUpViewBgResId() {
        return R.drawable.icon_headsup_mitalk;
    }

    public int getLockScreenViewBgResId() {
        return R.drawable.lockscreen_message_bg_mitalk;
    }

    public String getLuckyMoneyEventKeyPostfix() {
        return MiStatUtil.KEY_LUCK_MONEY_REMINDED_MITALK_POSTFIX;
    }

    public BaseConfiguration.NotifyType getNotifyType() {
        return !this.mCommonConfig.getXiaomiLuckyMoneyEnable() ? BaseConfiguration.NotifyType.NONE : !this.mCommonConfig.getMiliaoLuckyWarningEnable() ? BaseConfiguration.NotifyType.NONE : (!this.mCommonConfig.isDNDModeEffective() || !isDNDModeNoEverything()) ? getNotifyTypeByMode() : BaseConfiguration.NotifyType.NONE;
    }
}
