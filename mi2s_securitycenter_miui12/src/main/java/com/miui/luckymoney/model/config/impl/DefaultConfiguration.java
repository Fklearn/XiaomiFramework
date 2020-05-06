package com.miui.luckymoney.model.config.impl;

import android.content.Context;
import com.miui.luckymoney.config.DoNotDisturbConstants;
import com.miui.luckymoney.config.LuckySoundConstants;
import com.miui.luckymoney.model.config.BaseConfiguration;
import com.miui.luckymoney.stats.MiStatUtil;
import com.miui.securitycenter.R;

public class DefaultConfiguration extends BaseConfiguration {
    public DefaultConfiguration(Context context) {
        super(context);
    }

    public int getHeadsUpViewBgResId() {
        return R.drawable.icon_headsup_wechat;
    }

    public int getLockScreenViewBgResId() {
        return R.drawable.lockscreen_message_bg_wx;
    }

    public String getLuckyMoneyEventKeyPostfix() {
        return MiStatUtil.KEY_LUCK_MONEY_REMINDED_WEIXIN_POSTFIX;
    }

    public BaseConfiguration.NotifyType getNotifyType() {
        return !this.mCommonConfig.getXiaomiLuckyMoneyEnable() ? BaseConfiguration.NotifyType.NONE : !this.mCommonConfig.getWeChatLuckyWarningEnable() ? BaseConfiguration.NotifyType.NONE : (!this.mCommonConfig.isDNDModeEffective() || !isDNDModeNoEverything()) ? getNotifyTypeByMode() : BaseConfiguration.NotifyType.NONE;
    }

    /* access modifiers changed from: protected */
    public BaseConfiguration.NotifyType getNotifyTypeByMode() {
        return BaseConfiguration.NotifyType.NOTIFICATION;
    }

    public Integer getSoundResId() {
        return Integer.valueOf(LuckySoundConstants.SOUND_RES_ID[this.mCommonConfig.getLuckySoundWarningLevel()]);
    }

    /* access modifiers changed from: protected */
    public boolean isDNDModeNoEverything() {
        return this.mCommonConfig.getDNDModeLevel() == DoNotDisturbConstants.DND_LEVEL_NO_EVERYTHING;
    }

    public boolean justForGroupMessage() {
        return this.mCommonConfig.getOnlyNotiGroupLuckuMoneyConfig();
    }

    public boolean needPlaySource() {
        return this.mCommonConfig.getLuckySoundWarningLevel() != 0 && !this.mCommonConfig.isDNDModeEffective();
    }
}
