package com.android.server.policy;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.media.AudioManager;
import android.miui.R;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.util.Slog;
import android.view.ContextThemeWrapper;
import android.view.IWindowManager;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.FrameLayout;
import com.android.server.wm.WindowManagerService;
import miui.util.FeatureParser;
import miui.util.ProximitySensorWrapper;

public class MiuiScreenOnProximityLock {
    private static final boolean DEBUG = true;
    private static final int EVENT_PREPARE_VIEW = 1;
    private static final int EVENT_RELEASE = 0;
    private static final int EVENT_RELEASE_VIEW = 3;
    private static final int EVENT_SHOW_VIEW = 2;
    private static final int FIRST_CHANGE_TIMEOUT = 1500;
    private static final String LOG_TAG = "MiuiScreenOnProximityLock";
    private long mAquiredTime = 0;
    private Context mContext;
    protected boolean mFrontFingerprintSensor;
    /* access modifiers changed from: private */
    public Handler mHandler;
    protected boolean mHasNavigationBar;
    private boolean mHideNavigationBarWhenForceShow;
    protected ViewGroup mHintContainer;
    protected View mHintView;
    protected MiuiKeyguardServiceDelegate mKeyguardDelegate;
    private ProximitySensorWrapper mProximitySensorWrapper;
    private final ProximitySensorWrapper.ProximitySensorChangeListener mSensorListener = new ProximitySensorWrapper.ProximitySensorChangeListener() {
        public void onSensorChanged(boolean tooClose) {
            if (tooClose) {
                MiuiScreenOnProximityLock.this.mHandler.removeMessages(0);
            } else {
                MiuiScreenOnProximityLock.this.mHandler.sendEmptyMessage(0);
            }
        }
    };

    public MiuiScreenOnProximityLock(Context context, MiuiKeyguardServiceDelegate keyguardDelegate, Looper looper) {
        this.mContext = context;
        this.mKeyguardDelegate = keyguardDelegate;
        this.mHandler = new Handler(looper) {
            public void handleMessage(Message msg) {
                synchronized (MiuiScreenOnProximityLock.this) {
                    int i = msg.what;
                    if (i == 0) {
                        Slog.d(MiuiScreenOnProximityLock.LOG_TAG, "far from the screen for a certain time, release proximity sensor...");
                        MiuiScreenOnProximityLock.this.release(false);
                    } else if (i == 1) {
                        MiuiScreenOnProximityLock.this.prepareHintWindow();
                    } else if (i == 2) {
                        MiuiScreenOnProximityLock.this.showHint();
                    } else if (i == 3) {
                        MiuiScreenOnProximityLock.this.releaseHintWindow(((Boolean) msg.obj).booleanValue());
                    }
                }
            }
        };
        this.mFrontFingerprintSensor = FeatureParser.getBoolean("front_fingerprint_sensor", false);
        this.mHasNavigationBar = hasNavigationBar();
    }

    public synchronized boolean isHeld() {
        return this.mAquiredTime != 0;
    }

    public synchronized void aquire() {
        if (!isHeld()) {
            Slog.d(LOG_TAG, "aquire");
            this.mAquiredTime = System.currentTimeMillis();
            this.mHandler.sendEmptyMessage(1);
            this.mHandler.sendEmptyMessageDelayed(0, 1500);
            this.mProximitySensorWrapper = new ProximitySensorWrapper(this.mContext);
            this.mProximitySensorWrapper.registerListener(this.mSensorListener);
        }
    }

    public synchronized boolean release(boolean isNowRelease) {
        if (!isHeld()) {
            return false;
        }
        Slog.d(LOG_TAG, "release");
        this.mAquiredTime = 0;
        this.mProximitySensorWrapper.unregisterAllListeners();
        this.mHandler.removeMessages(1);
        this.mHandler.removeMessages(2);
        Message releaseViewMessage = this.mHandler.obtainMessage(3);
        releaseViewMessage.obj = Boolean.valueOf(isNowRelease);
        this.mHandler.sendMessage(releaseViewMessage);
        return true;
    }

    public boolean shouldBeBlocked(boolean ScreenOnFully, KeyEvent event) {
        if (!shouldBeBlockedInternal(event, ScreenOnFully)) {
            return false;
        }
        forceShow();
        return true;
    }

    private boolean shouldBeBlockedInternal(KeyEvent event, boolean ScreenOnFully) {
        if (event == null || !isHeld() || !ScreenOnFully || event.getAction() == 1) {
            return false;
        }
        int keyCode = event.getKeyCode();
        if (keyCode == 3) {
            return !this.mFrontFingerprintSensor;
        }
        if (!(keyCode == 79 || keyCode == 126 || keyCode == 127)) {
            switch (keyCode) {
                case WindowManagerService.H.WAITING_FOR_DRAWN_TIMEOUT /*24*/:
                case WindowManagerService.H.SHOW_STRICT_MODE_VIOLATION /*25*/:
                    return !((AudioManager) this.mContext.getSystemService("audio")).isMusicActive();
                case 26:
                    break;
                default:
                    switch (keyCode) {
                        case HdmiCecKeycode.CEC_KEYCODE_INITIAL_CONFIGURATION:
                        case HdmiCecKeycode.CEC_KEYCODE_SELECT_BROADCAST_TYPE:
                        case HdmiCecKeycode.CEC_KEYCODE_SELECT_SOUND_PRESENTATION:
                            break;
                        default:
                            return true;
                    }
            }
        }
        return false;
    }

    public void forceShow() {
        ViewGroup viewGroup = this.mHintContainer;
        if (viewGroup != null && (viewGroup.getSystemUiVisibility() & 4) == 0) {
            this.mHintContainer.setSystemUiVisibility(3842);
            this.mHideNavigationBarWhenForceShow = true;
        }
        this.mHandler.sendEmptyMessageDelayed(2, 300);
    }

    /* access modifiers changed from: private */
    public void prepareHintWindow() {
        this.mHintContainer = new FrameLayout(new ContextThemeWrapper(this.mContext, 16973931));
        this.mHintContainer.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                MiuiScreenOnProximityLock.this.forceShow();
                return true;
            }
        });
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams(-1, -1, 2009, 25366784, -3);
        layoutParams.inputFeatures |= 4;
        layoutParams.layoutInDisplayCutoutMode = 1;
        layoutParams.gravity = 17;
        layoutParams.setTitle("ScreenOnProximitySensorGuide");
        ((WindowManager) this.mContext.getSystemService("window")).addView(this.mHintContainer, layoutParams);
        this.mKeyguardDelegate.enableUserActivity(false);
    }

    /* access modifiers changed from: private */
    public void releaseHintWindow(boolean isNowRelease) {
        final View container = this.mHintContainer;
        if (container != null) {
            View view = this.mHintView;
            if (view == null) {
                ((WindowManager) this.mContext.getSystemService("window")).removeView(container);
            } else if (isNowRelease) {
                releaseReset(container);
                this.mHintView = null;
            } else {
                ObjectAnimator animator = ObjectAnimator.ofFloat(view, View.ALPHA, new float[]{1.0f, 0.0f});
                animator.setDuration(500);
                animator.addListener(new Animator.AnimatorListener() {
                    public void onAnimationStart(Animator animation) {
                    }

                    public void onAnimationRepeat(Animator animation) {
                    }

                    public void onAnimationEnd(Animator animation) {
                        MiuiScreenOnProximityLock.this.releaseReset(container);
                    }

                    public void onAnimationCancel(Animator animation) {
                    }
                });
                animator.start();
                this.mHintView = null;
            }
            if (!this.mKeyguardDelegate.isShowingAndNotHidden()) {
                this.mKeyguardDelegate.enableUserActivity(true);
            }
            this.mHintContainer = null;
        }
    }

    /* access modifiers changed from: private */
    public void releaseReset(View container) {
        if (this.mHideNavigationBarWhenForceShow) {
            container.setSystemUiVisibility(3840);
            this.mHideNavigationBarWhenForceShow = false;
        }
        ((WindowManager) this.mContext.getSystemService("window")).removeView(container);
    }

    /* access modifiers changed from: private */
    public void showHint() {
        if (isHeld() && this.mHintView == null) {
            Slog.d(LOG_TAG, "show hint...");
            int resource = R.layout.screen_on_proximity_sensor_guide;
            if (this.mHasNavigationBar) {
                resource = R.layout.screen_on_proximity_sensor_guide_has_navigation_bar;
            }
            this.mHintView = View.inflate(new ContextThemeWrapper(this.mContext, 16973931), resource, this.mHintContainer);
            ObjectAnimator animator = ObjectAnimator.ofFloat(this.mHintView, View.ALPHA, new float[]{0.0f, 1.0f});
            animator.setDuration(500);
            animator.start();
            Animation animation = new AlphaAnimation(1.0f, 0.0f);
            animation.setDuration(500);
            animation.setRepeatCount(-1);
            animation.setRepeatMode(2);
            animation.setStartOffset(500);
            this.mHintView.findViewById(R.id.screen_on_proximity_sensor_hint_animation).startAnimation(animation);
        }
    }

    private boolean hasNavigationBar() {
        try {
            this.mHasNavigationBar = IWindowManager.Stub.asInterface(ServiceManager.getService("window")).hasNavigationBar(0);
        } catch (RemoteException e) {
        }
        return this.mHasNavigationBar;
    }
}
