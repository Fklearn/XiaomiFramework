package com.miui.luckymoney.ui.view;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import com.miui.luckymoney.model.config.BaseConfiguration;
import com.miui.luckymoney.model.message.AppMessage;
import com.miui.luckymoney.stats.MiStatUtil;
import com.miui.luckymoney.ui.activity.LuckySettingActivity;
import com.miui.luckymoney.ui.view.messageview.MessageView;
import com.miui.luckymoney.utils.NotificationUtil;
import com.miui.luckymoney.utils.ScreenUtil;
import com.miui.securitycenter.R;

public class LockScreenMessageView implements MessageView {
    /* access modifiers changed from: private */
    public final BaseConfiguration configuration;
    /* access modifiers changed from: private */
    public final LockScreenView lockScreenView;
    /* access modifiers changed from: private */
    public AppMessage showedMessage = null;

    public LockScreenMessageView(BaseConfiguration baseConfiguration) {
        this.configuration = baseConfiguration;
        this.lockScreenView = new LockScreenView(baseConfiguration.context());
    }

    private String getTitle(AppMessage appMessage) {
        Context context;
        int i;
        Object[] objArr;
        if (appMessage.isBusinessMessage()) {
            context = this.configuration.context();
            i = R.string.lock_screen_business_hongbao_message;
            objArr = new Object[]{appMessage.getName()};
        } else {
            context = this.configuration.context();
            i = R.string.lock_screen_hongbao_message;
            objArr = new Object[]{appMessage.getName()};
        }
        return context.getString(i, objArr);
    }

    private void showFirstly(AppMessage appMessage) {
        this.showedMessage = appMessage;
        this.lockScreenView.setPositiveClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                MiStatUtil.recordLuckyMoneyLockedNoti(LockScreenMessageView.this.configuration.getLuckyMoneyEventKeyPostfix(), true);
                NotificationUtil.stopNotification(LockScreenMessageView.this.configuration.context(), LockScreenMessageView.this.configuration.getSoundResId().intValue());
                LockScreenMessageView.this.lockScreenView.dismiss();
                ScreenUtil.unlockKeyguard(LockScreenMessageView.this.configuration.context(), LockScreenMessageView.this.showedMessage.getAction());
            }
        });
        this.lockScreenView.setNegativeClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                NotificationUtil.stopNotification(LockScreenMessageView.this.configuration.context(), LockScreenMessageView.this.configuration.getSoundResId().intValue());
                LockScreenMessageView.this.lockScreenView.dismiss();
                MiStatUtil.recordLuckyMoneyLockedNoti(MiStatUtil.CLOSE, true);
            }
        });
        this.lockScreenView.setSettingsClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                NotificationUtil.stopNotification(LockScreenMessageView.this.configuration.context(), LockScreenMessageView.this.configuration.getSoundResId().intValue());
                LockScreenMessageView.this.lockScreenView.dismiss();
                Intent intent = new Intent(LockScreenMessageView.this.configuration.context(), LuckySettingActivity.class);
                intent.setFlags(268435456);
                ScreenUtil.unlockKeyguard(LockScreenMessageView.this.configuration.context(), PendingIntent.getActivity(LockScreenMessageView.this.configuration.context(), 0, intent, 1073741824));
                MiStatUtil.recordLuckyMoneyLockedNoti("settings", true);
            }
        });
        this.lockScreenView.show(this.configuration, getTitle(appMessage));
    }

    private void update(AppMessage appMessage) {
        this.showedMessage = appMessage;
        this.lockScreenView.update(this.configuration, getTitle(appMessage));
    }

    public void hide() {
        this.lockScreenView.dismiss();
    }

    public boolean isAlive() {
        return this.lockScreenView.isAlive();
    }

    public void show(AppMessage appMessage) {
        if (appMessage != null) {
            if (!isAlive()) {
                showFirstly(appMessage);
            } else {
                update(appMessage);
            }
            MiStatUtil.recordLuckyMoneyLockedNoti(this.configuration.getLuckyMoneyEventKeyPostfix(), false);
        }
    }
}
