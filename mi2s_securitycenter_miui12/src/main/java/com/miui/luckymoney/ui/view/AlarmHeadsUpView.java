package com.miui.luckymoney.ui.view;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import b.b.c.j.B;
import b.b.c.j.g;
import b.b.c.j.i;
import com.miui.luckymoney.config.AppConstants;
import com.miui.luckymoney.ui.activity.LuckyAlarmActivity;
import com.miui.luckymoney.utils.NotificationUtil;
import com.miui.securitycenter.R;

public class AlarmHeadsUpView extends FrameLayout implements GestureDetector.OnGestureListener {
    private static final long SHOW_ANIM_DURATION = 500;
    private static final long SHOW_DURATION = 5000;
    private final Runnable autoDismissRunnable = new Runnable() {
        public void run() {
            AlarmHeadsUpView.this.dismiss();
        }
    };
    private GestureDetector gestureDetector;
    private final int height = getResources().getDimensionPixelSize(R.dimen.headsup_view_height);
    private boolean isShown = false;
    private ImageView logoView;
    private View negativeAction;
    private View positiveAction;
    private View settingsAction;
    private TextView titleView;
    private final Handler uiHandler = new Handler(Looper.getMainLooper());

    public AlarmHeadsUpView(Context context) {
        super(context);
        init(context);
    }

    public AlarmHeadsUpView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        init(context);
    }

    public AlarmHeadsUpView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        init(context);
    }

    private void dismiss(boolean z) {
        NotificationUtil.stopNotification(getContext(), R.raw.lucky_alarm);
        if (this.isShown) {
            this.isShown = false;
            this.uiHandler.removeCallbacks(this.autoDismissRunnable);
            ObjectAnimator ofFloat = ObjectAnimator.ofFloat(this, View.TRANSLATION_Y, new float[]{0.0f, (float) (-this.height)});
            if (z) {
                ofFloat.setDuration(SHOW_ANIM_DURATION);
                ofFloat.start();
                ofFloat.addListener(new Animator.AnimatorListener() {
                    public void onAnimationCancel(Animator animator) {
                    }

                    public void onAnimationEnd(Animator animator) {
                        AlarmHeadsUpView.this.removeMessageView();
                    }

                    public void onAnimationRepeat(Animator animator) {
                    }

                    public void onAnimationStart(Animator animator) {
                    }
                });
                return;
            }
            removeMessageView();
        }
    }

    private int getDrawableByPackageName(String str) {
        return AppConstants.Package.PACKAGE_NAME_MM.equals(str) ? R.drawable.alarm_headsup_wechat : AppConstants.Package.PACKAGE_NAME_MITALK.equals(str) ? R.drawable.alarm_headsup_mi : AppConstants.Package.PACKAGE_NAME_QQ.equals(str) ? R.drawable.alarm_headsup_qq : AppConstants.Package.PACKAGE_NAME_ALIPAY.equals(str) ? R.drawable.alarm_headsup_alipay : R.drawable.alarm_headsup_mi;
    }

    private void init(final Context context) {
        this.gestureDetector = new GestureDetector(context, this, this.uiHandler);
        View inflate = LayoutInflater.from(context).inflate(R.layout.alarm_headsup_layout, (ViewGroup) null);
        this.titleView = (TextView) inflate.findViewById(R.id.title);
        this.logoView = (ImageView) inflate.findViewById(R.id.logo);
        View findViewById = inflate.findViewById(R.id.content_bg);
        if (i.e()) {
            findViewById.setBackground(context.getResources().getDrawable(R.drawable.heads_up_hongbao_alarm_bg_notch));
        }
        this.positiveAction = inflate.findViewById(R.id.ok);
        this.settingsAction = inflate.findViewById(R.id.settings);
        this.settingsAction.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent(context, LuckyAlarmActivity.class);
                intent.setFlags(268435456);
                g.b(context, intent, B.b());
                NotificationUtil.stopNotification(context, R.raw.lucky_alarm);
                AlarmHeadsUpView.this.dismiss();
            }
        });
        this.negativeAction = inflate.findViewById(R.id.later);
        this.negativeAction.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                AlarmHeadsUpView.this.dismiss();
            }
        });
        addView(inflate);
    }

    /* access modifiers changed from: private */
    public void removeMessageView() {
        this.titleView.setText("");
        MessageViewUtil.removeMessageView(this);
    }

    private void showMessageView(int i, String str) {
        this.titleView.setText(str);
        this.logoView.setImageResource(i);
        MessageViewUtil.showMessageView(this, -1, this.height, 2010);
        this.uiHandler.removeCallbacks(this.autoDismissRunnable);
        this.uiHandler.postDelayed(this.autoDismissRunnable, 5000);
    }

    public void dismiss() {
        dismiss(false);
    }

    public boolean dispatchKeyEvent(KeyEvent keyEvent) {
        if (keyEvent.getKeyCode() != 4) {
            return super.dispatchKeyEvent(keyEvent);
        }
        dismiss();
        return true;
    }

    public boolean isAlive() {
        return this.isShown;
    }

    public boolean onDown(MotionEvent motionEvent) {
        return false;
    }

    public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent2, float f, float f2) {
        float dimensionPixelOffset = (float) getContext().getResources().getDimensionPixelOffset(R.dimen.flow_close_min_distance);
        float axisValue = motionEvent2.getAxisValue(0) - motionEvent.getAxisValue(0);
        float axisValue2 = motionEvent2.getAxisValue(1) - motionEvent.getAxisValue(1);
        if (axisValue2 > 0.0f || ((float) Math.sqrt((double) ((axisValue * axisValue) + (axisValue2 * axisValue2)))) < dimensionPixelOffset) {
            return false;
        }
        dismiss(true);
        return true;
    }

    public void onLongPress(MotionEvent motionEvent) {
    }

    public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent2, float f, float f2) {
        return false;
    }

    public void onShowPress(MotionEvent motionEvent) {
    }

    public boolean onSingleTapUp(MotionEvent motionEvent) {
        return false;
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        super.onTouchEvent(motionEvent);
        return this.gestureDetector.onTouchEvent(motionEvent);
    }

    public void setPositiveOnClickListener(View.OnClickListener onClickListener) {
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
