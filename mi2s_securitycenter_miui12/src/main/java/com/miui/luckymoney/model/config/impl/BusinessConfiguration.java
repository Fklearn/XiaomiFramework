package com.miui.luckymoney.model.config.impl;

import android.content.Context;
import com.miui.luckymoney.model.config.BaseConfiguration;
import com.miui.luckymoney.stats.MiStatUtil;
import com.miui.securitycenter.R;

public class BusinessConfiguration extends DefaultConfiguration {
    public BusinessConfiguration(Context context) {
        super(context);
    }

    public int getHeadsUpViewBgResId() {
        return R.drawable.icon_headsup_luckymoney;
    }

    public int getLockScreenViewBgResId() {
        return R.drawable.lockscreen_message_bg_business;
    }

    public String getLuckyMoneyEventKeyPostfix() {
        return MiStatUtil.KEY_LUCK_MONEY_REMINDED_BUSINESS_POSTFIX;
    }

    public BaseConfiguration.NotifyType getNotifyType() {
        return !this.mCommonConfig.getXiaomiLuckyMoneyEnable() ? BaseConfiguration.NotifyType.NONE : !this.mCommonConfig.getBusinessLuckyWarningEnable() ? BaseConfiguration.NotifyType.NONE : (!this.mCommonConfig.isDNDModeEffective() || !isDNDModeNoEverything()) ? getNotifyTypeByMode() : BaseConfiguration.NotifyType.NONE;
    }
}
