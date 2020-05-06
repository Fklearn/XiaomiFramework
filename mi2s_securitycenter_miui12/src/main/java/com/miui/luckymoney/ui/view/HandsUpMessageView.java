package com.miui.luckymoney.ui.view;

import android.content.Intent;
import android.view.View;
import b.b.c.j.B;
import b.b.c.j.g;
import com.miui.luckymoney.model.config.BaseConfiguration;
import com.miui.luckymoney.model.message.AppMessage;
import com.miui.luckymoney.stats.MiStatUtil;
import com.miui.luckymoney.ui.activity.LuckySettingActivity;
import com.miui.luckymoney.ui.view.messageview.MessageView;
import com.miui.luckymoney.utils.NotificationUtil;
import com.miui.luckymoney.utils.ScreenUtil;
import com.miui.luckymoney.utils.StringUtil;
import com.miui.securitycenter.R;

public class HandsUpMessageView implements MessageView {
    /* access modifiers changed from: private */
    public final BaseConfiguration configuration;
    /* access modifiers changed from: private */
    public final HeadsUpView headsUpView;
    /* access modifiers changed from: private */
    public AppMessage mMessage;
    private int mMessageCount = 0;

    public HandsUpMessageView(BaseConfiguration baseConfiguration) {
        this.configuration = baseConfiguration;
        this.headsUpView = new HeadsUpView(baseConfiguration.context());
    }

    private String getTitle() {
        String maxLengthLimitedString = StringUtil.getMaxLengthLimitedString(this.mMessage.getName(), this.configuration.context().getResources().getConfiguration().orientation == 2 ? 5 : 10);
        if (this.mMessage.isGroupMessage()) {
            return this.configuration.context().getString(R.string.group_have_hongbao, new Object[]{maxLengthLimitedString, Integer.valueOf(this.mMessageCount)});
        } else if (this.mMessage.isBusinessMessage()) {
            return this.configuration.context().getString(R.string.business_have_hongbao, new Object[]{maxLengthLimitedString});
        } else {
            return this.configuration.context().getString(R.string.person_have_hongbao, new Object[]{maxLengthLimitedString});
        }
    }

    private void showFirstly() {
        this.headsUpView.setPositiveClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                MiStatUtil.recordLuckyMoneyNoti(HandsUpMessageView.this.configuration.getLuckyMoneyEventKeyPostfix(), true);
                NotificationUtil.stopNotification(HandsUpMessageView.this.configuration.context(), HandsUpMessageView.this.configuration.getSoundResId().intValue());
                HandsUpMessageView.this.headsUpView.dismiss();
                ScreenUtil.unlockKeyguard(HandsUpMessageView.this.configuration.context(), HandsUpMessageView.this.mMessage.getAction());
            }
        });
        this.headsUpView.setNegativeClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                NotificationUtil.stopNotification(HandsUpMessageView.this.configuration.context(), HandsUpMessageView.this.configuration.getSoundResId().intValue());
                HandsUpMessageView.this.headsUpView.dismiss();
                MiStatUtil.recordLuckyMoneyNoti(MiStatUtil.CLOSE, true);
            }
        });
        this.headsUpView.setSettingsActionListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent(HandsUpMessageView.this.configuration.context(), LuckySettingActivity.class);
                intent.setFlags(268435456);
                g.b(HandsUpMessageView.this.configuration.context(), intent, B.b());
                NotificationUtil.stopNotification(HandsUpMessageView.this.configuration.context(), HandsUpMessageView.this.configuration.getSoundResId().intValue());
                HandsUpMessageView.this.headsUpView.dismiss();
                MiStatUtil.recordLuckyMoneyNoti("settings", true);
            }
        });
        this.headsUpView.show(this.configuration, getTitle());
    }

    private void update() {
        this.headsUpView.update(this.configuration, getTitle());
    }

    public void hide() {
        this.headsUpView.dismiss();
    }

    public boolean isAlive() {
        return this.headsUpView.isAlive();
    }

    public void show(AppMessage appMessage) {
        if (appMessage != null) {
            this.mMessageCount++;
            this.mMessage = appMessage;
            if (this.mMessageCount == 1) {
                showFirstly();
            } else {
                update();
            }
            MiStatUtil.recordLuckyMoneyNoti(this.configuration.getLuckyMoneyEventKeyPostfix(), false);
        }
    }
}
