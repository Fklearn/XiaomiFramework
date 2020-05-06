package com.miui.luckymoney.model.config;

import android.content.Context;
import com.miui.luckymoney.config.CommonConfig;

public abstract class BaseConfiguration {
    private Context context;
    protected CommonConfig mCommonConfig;

    public enum NotifyType {
        NONE,
        NOTIFICATION
    }

    public BaseConfiguration(Context context2) {
        this.context = context2;
        this.mCommonConfig = CommonConfig.getInstance(context2);
    }

    public Context context() {
        return this.context;
    }

    public abstract int getHeadsUpViewBgResId();

    public abstract int getLockScreenViewBgResId();

    public abstract String getLuckyMoneyEventKeyPostfix();

    public abstract NotifyType getNotifyType();

    public abstract Integer getSoundResId();

    public abstract boolean justForGroupMessage();

    public abstract boolean needPlaySource();
}
