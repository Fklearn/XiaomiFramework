package com.miui.luckymoney.ui.view;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import b.b.c.j.i;
import com.miui.gamebooster.m.D;
import com.miui.luckymoney.model.config.BaseConfiguration;
import com.miui.luckymoney.stats.MiStatUtil;
import com.miui.luckymoney.utils.NotificationUtil;
import com.miui.luckymoney.webapi.AdsHelper;
import com.miui.securitycenter.R;

public class HeadsUpView extends FrameLayout implements GestureDetector.OnGestureListener {
    private static final long SHOW_ANIM_DURATION = 500;
    private static final long SHOW_DURATION = 6000;
    private final Runnable autoDismissRunnable = new Runnable() {
        public void run() {
            HeadsUpView.this.dismiss();
        }
    };
    private View contentLayout;
    private GestureDetector gestureDetector;
    private final int height = getResources().getDimensionPixelSize(R.dimen.headsup_view_height);
    private ImageView imgAds;
    private ImageView imgIcon;
    private boolean isShown = false;
    private View negativeAction;
    private View positiveAction;
    private View settingsAction;
    private TextView titleView;
    private TextView txvAds;
    private final Handler uiHandler = new Handler(Looper.getMainLooper());

    public HeadsUpView(Context context) {
        super(context);
        init(context);
    }

    public HeadsUpView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        init(context);
    }

    public HeadsUpView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        init(context);
    }

    private void dismiss(boolean z) {
        NotificationUtil.stopNotification(getContext(), R.raw.hongbao_arrived);
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
                        HeadsUpView.this.removeMessageView();
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

    private void init(Context context) {
        this.gestureDetector = new GestureDetector(context, this, this.uiHandler);
        boolean z = getResources().getConfiguration().orientation == 2;
        boolean b2 = D.b(context);
        View inflate = LayoutInflater.from(context).inflate((z || b2) ? R.layout.hongbao_headsup_small_layout : R.layout.hongbao_headsup_layout, (ViewGroup) null);
        this.contentLayout = inflate.findViewById(R.id.content);
        View findViewById = inflate.findViewById(R.id.container_view);
        if (!z && i.e()) {
            findViewById.setBackground(context.getResources().getDrawable(R.drawable.heads_up_hongbao_message_big_bg_notch));
        }
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) this.contentLayout.getLayoutParams();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((WindowManager) context.getSystemService("window")).getDefaultDisplay().getMetrics(displayMetrics);
        layoutParams.width = displayMetrics.widthPixels;
        if (z) {
            layoutParams.width = (int) (((float) context.getResources().getDimensionPixelSize(R.dimen.heads_up_hongbao_width)) + (displayMetrics.density * 70.0f));
        } else {
            float f = displayMetrics.density;
            layoutParams.width = (int) (((float) layoutParams.width) + (40.0f * f));
            layoutParams.topMargin = (int) (f * -6.0f);
            layoutParams.rightMargin = (int) (f * -6.0f);
            layoutParams.leftMargin = (int) (f * -6.0f);
        }
        if (b2) {
            layoutParams.height = context.getResources().getDimensionPixelOffset(R.dimen.heads_up_hongbao_fullscreen_height);
        }
        this.contentLayout.setLayoutParams(layoutParams);
        this.contentLayout.requestLayout();
        this.imgAds = (ImageView) inflate.findViewById(R.id.imgAdsLogo);
        this.imgIcon = (ImageView) inflate.findViewById(R.id.icon);
        this.txvAds = (TextView) inflate.findViewById(R.id.txvAdsText);
        this.titleView = (TextView) inflate.findViewById(R.id.title);
        this.positiveAction = inflate.findViewById(R.id.ok);
        this.settingsAction = inflate.findViewById(R.id.settings);
        this.negativeAction = inflate.findViewById(R.id.later);
        this.negativeAction.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                HeadsUpView.this.dismiss();
            }
        });
        AdsHelper.AdsItem currentAdsItem = AdsHelper.getCurrentAdsItem(context);
        if (currentAdsItem != null) {
            this.imgAds.setImageBitmap(currentAdsItem.icon);
            this.txvAds.setText(currentAdsItem.text);
            MiStatUtil.recordAds(currentAdsItem.startTime);
        }
        addView(inflate);
    }

    /* access modifiers changed from: private */
    public void removeMessageView() {
        this.titleView.setText("");
        MessageViewUtil.removeMessageView(this);
    }

    private void showMessageView(BaseConfiguration baseConfiguration, String str) {
        this.titleView.setText(str);
        this.imgIcon.setImageDrawable(getContext().getResources().getDrawable(baseConfiguration.getHeadsUpViewBgResId()));
        MessageViewUtil.showMoneyMessageView(this, -1, this.height, 2010);
        this.uiHandler.removeCallbacks(this.autoDismissRunnable);
        this.uiHandler.postDelayed(this.autoDismissRunnable, SHOW_DURATION);
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
        if (motionEvent == null || motionEvent2 == null) {
            return false;
        }
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

    public void setNegativeClickListener(View.OnClickListener onClickListener) {
        this.negativeAction.setOnClickListener(onClickListener);
    }

    public void setPositiveClickListener(View.OnClickListener onClickListener) {
        this.positiveAction.setOnClickListener(onClickListener);
    }

    public void setSettingsActionListener(View.OnClickListener onClickListener) {
        this.settingsAction.setOnClickListener(onClickListener);
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
