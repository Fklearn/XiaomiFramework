package com.miui.luckymoney.ui.view;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import com.miui.luckymoney.model.config.BaseConfiguration;
import com.miui.luckymoney.stats.MiStatUtil;
import com.miui.luckymoney.utils.NotificationUtil;
import com.miui.luckymoney.utils.ScreenUtil;
import com.miui.luckymoney.webapi.AdsHelper;
import com.miui.securitycenter.R;

public class LockScreenView extends FrameLayout {
    private static final int CLEAR_KEYGUARD_NOTIFICATIONS_DURATION = 500;
    private static final long SHOW_DURATION = 300000;
    private final Runnable autoDismissRunnable = new Runnable() {
        public void run() {
            LockScreenView.this.dismiss();
        }
    };
    /* access modifiers changed from: private */
    public Runnable clearKeyguardNotificationsRunnable = null;
    private View closeView;
    private View contentView;
    private TextView descriptionView;
    private ImageView hongbaoView;
    private ImageView imgAds;
    private boolean isShown = false;
    private View layoutAds;
    private View settingsView;
    private TextView txvAds;
    /* access modifiers changed from: private */
    public final Handler uiHandler = new Handler(Looper.getMainLooper());

    public LockScreenView(Context context) {
        super(context);
        init(context);
    }

    public LockScreenView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        init(context);
    }

    public LockScreenView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        init(context);
    }

    private void clearKeyguardNotifications(final int i) {
        Runnable runnable = this.clearKeyguardNotificationsRunnable;
        if (runnable != null) {
            this.uiHandler.removeCallbacks(runnable);
            this.clearKeyguardNotificationsRunnable = null;
        }
        this.clearKeyguardNotificationsRunnable = new Runnable() {
            private int passedMilliseconds = 0;

            public void run() {
                ScreenUtil.clearKeyguardNotifications(LockScreenView.this.getContext());
                this.passedMilliseconds += 20;
                if (this.passedMilliseconds < i) {
                    LockScreenView.this.uiHandler.postDelayed(this, 20);
                } else {
                    Runnable unused = LockScreenView.this.clearKeyguardNotificationsRunnable = null;
                }
            }
        };
        ScreenUtil.clearKeyguardNotifications(getContext());
        this.uiHandler.postDelayed(this.clearKeyguardNotificationsRunnable, 20);
    }

    private void init(Context context) {
        LayoutInflater from = LayoutInflater.from(context);
        setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
            }
        });
        this.contentView = from.inflate(R.layout.hongbao_lockscreen_layout, (ViewGroup) null);
        this.hongbaoView = (ImageView) this.contentView.findViewById(R.id.lock_screen_hongbao_message_bg);
        this.descriptionView = (TextView) this.contentView.findViewById(R.id.description);
        this.closeView = this.contentView.findViewById(R.id.later);
        this.settingsView = this.contentView.findViewById(R.id.settings);
        this.layoutAds = this.contentView.findViewById(R.id.layoutAds);
        this.imgAds = (ImageView) this.contentView.findViewById(R.id.imgAdsLogo);
        this.txvAds = (TextView) this.contentView.findViewById(R.id.txvAdsText);
        AdsHelper.AdsItem currentAdsItem = AdsHelper.getCurrentAdsItem(context);
        if (currentAdsItem != null) {
            this.imgAds.setImageBitmap(currentAdsItem.icon);
            this.txvAds.setText(currentAdsItem.text);
            MiStatUtil.recordAds(currentAdsItem.startTime);
        }
        addView(this.contentView);
    }

    private void showMessageView(BaseConfiguration baseConfiguration, String str) {
        this.descriptionView.setText(str);
        this.hongbaoView.setImageResource(baseConfiguration.getLockScreenViewBgResId());
        MessageViewUtil.showMessageView(this, -1, -1, 2010);
        this.uiHandler.removeCallbacks(this.autoDismissRunnable);
        this.uiHandler.postDelayed(this.autoDismissRunnable, 300000);
        clearKeyguardNotifications(CLEAR_KEYGUARD_NOTIFICATIONS_DURATION);
    }

    public void dismiss() {
        NotificationUtil.stopNotification(getContext(), R.raw.hongbao_arrived);
        if (this.isShown) {
            this.isShown = false;
            this.uiHandler.removeCallbacks(this.autoDismissRunnable);
            Runnable runnable = this.clearKeyguardNotificationsRunnable;
            if (runnable != null) {
                this.uiHandler.removeCallbacks(runnable);
                this.clearKeyguardNotificationsRunnable = null;
            }
            MessageViewUtil.removeMessageView(this);
        }
    }

    public boolean isAlive() {
        return this.isShown;
    }

    public void setNegativeClickListener(View.OnClickListener onClickListener) {
        this.closeView.setOnClickListener(onClickListener);
    }

    public void setPositiveClickListener(View.OnClickListener onClickListener) {
        this.hongbaoView.setOnClickListener(onClickListener);
    }

    public void setSettingsClickListener(View.OnClickListener onClickListener) {
        this.settingsView.setOnClickListener(onClickListener);
    }

    public void show(BaseConfiguration baseConfiguration, String str) {
        if (!this.isShown) {
            this.isShown = true;
            showMessageView(baseConfiguration, str);
        }
    }

    public void update(BaseConfiguration baseConfiguration, String str) {
        if (this.isShown) {
            showMessageView(baseConfiguration, str);
        }
    }
}
