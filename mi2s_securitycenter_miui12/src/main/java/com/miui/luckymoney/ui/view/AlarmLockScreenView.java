package com.miui.luckymoney.ui.view;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import com.miui.luckymoney.config.AppConstants;
import com.miui.luckymoney.ui.activity.LuckyAlarmActivity;
import com.miui.luckymoney.utils.NotificationUtil;
import com.miui.luckymoney.utils.ScreenUtil;
import com.miui.securitycenter.R;

public class AlarmLockScreenView extends FrameLayout {
    private static final int CLEAR_KEYGUARD_NOTIFICATIONS_DURATION = 500;
    private static final long SHOW_ANIM_DURATION = 500;
    private static final long SHOW_DURATION = 300000;
    private final Runnable autoDismissRunnable = new Runnable() {
        public void run() {
            AlarmLockScreenView.this.dismiss();
        }
    };
    /* access modifiers changed from: private */
    public Runnable clearKeyguardNotificationsRunnable = null;
    private View contentView;
    private TextView descriptionView;
    private boolean isShown = false;
    private ImageView logoView;
    private View negativeAction;
    private View positiveAction;
    private View settingsAction;
    /* access modifiers changed from: private */
    public final Handler uiHandler = new Handler(Looper.getMainLooper());

    public AlarmLockScreenView(Context context) {
        super(context);
        init(context);
    }

    public AlarmLockScreenView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        init(context);
    }

    public AlarmLockScreenView(Context context, AttributeSet attributeSet, int i) {
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
                ScreenUtil.clearKeyguardNotifications(AlarmLockScreenView.this.getContext());
                this.passedMilliseconds += 20;
                if (this.passedMilliseconds < i) {
                    AlarmLockScreenView.this.uiHandler.postDelayed(this, 20);
                } else {
                    Runnable unused = AlarmLockScreenView.this.clearKeyguardNotificationsRunnable = null;
                }
            }
        };
        ScreenUtil.clearKeyguardNotifications(getContext());
        this.uiHandler.postDelayed(this.clearKeyguardNotificationsRunnable, 20);
    }

    private int getDrawableByPackageName(String str) {
        return AppConstants.Package.PACKAGE_NAME_MM.equals(str) ? R.drawable.alarm_lock_wechat : AppConstants.Package.PACKAGE_NAME_MITALK.equals(str) ? R.drawable.alarm_lock_mi : AppConstants.Package.PACKAGE_NAME_QQ.equals(str) ? R.drawable.alarm_lock_qq : AppConstants.Package.PACKAGE_NAME_ALIPAY.equals(str) ? R.drawable.alarm_lock_alipay : R.drawable.alarm_lock_mi;
    }

    private void init(final Context context) {
        LayoutInflater from = LayoutInflater.from(context);
        setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
            }
        });
        this.contentView = from.inflate(R.layout.alarm_lockscreen_layout, (ViewGroup) null);
        this.logoView = (ImageView) this.contentView.findViewById(R.id.logo);
        this.descriptionView = (TextView) this.contentView.findViewById(R.id.description);
        this.negativeAction = this.contentView.findViewById(R.id.later);
        this.settingsAction = this.contentView.findViewById(R.id.settings);
        this.positiveAction = this.contentView.findViewById(R.id.ok);
        this.negativeAction.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                NotificationUtil.stopNotification(context, R.raw.lucky_alarm);
                AlarmLockScreenView.this.dismiss();
            }
        });
        this.settingsAction.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                AlarmLockScreenView.this.dismiss();
                Intent intent = new Intent(context, LuckyAlarmActivity.class);
                intent.setFlags(268435456);
                ScreenUtil.unlockKeyguard(context, PendingIntent.getActivity(context, 0, intent, 1073741824));
                NotificationUtil.stopNotification(context, R.raw.lucky_alarm);
                AlarmLockScreenView.this.dismiss();
            }
        });
        addView(this.contentView);
    }

    private void showMessageView(int i, String str) {
        this.descriptionView.setText(str);
        this.logoView.setImageResource(i);
        MessageViewUtil.showMessageView(this, -1, -1, 2010);
        this.uiHandler.removeCallbacks(this.autoDismissRunnable);
        this.uiHandler.postDelayed(this.autoDismissRunnable, 300000);
        clearKeyguardNotifications(CLEAR_KEYGUARD_NOTIFICATIONS_DURATION);
    }

    public void dismiss() {
        NotificationUtil.stopNotification(getContext(), R.raw.lucky_alarm);
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

    public void setPositiveClickListener(View.OnClickListener onClickListener) {
        this.positiveAction.setOnClickListener(onClickListener);
    }

    public void show(int i, String str) {
        if (!this.isShown) {
            this.isShown = true;
            showMessageView(i, str);
        }
    }

    public void show(String str, String str2) {
        show(getDrawableByPackageName(str), str2);
    }
}
