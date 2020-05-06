package com.android.server.lights;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.PowerManager;
import android.view.WindowManager;
import com.android.server.lights.view.MessageView;
import com.android.server.lights.view.MusicView;

public class AnimationController {
    public static final int ANIMATION_TYPE_START_MESSAGE = 1;
    public static final int ANIMATION_TYPE_START_MUSIC = 2;
    public static final int ANIMATION_TYPE_STOP_MESSAGE = 3;
    public static final int ANIMATION_TYPE_STOP_MUSIC = 4;
    public static final int TYPE_MESSAGE = 1;
    public static final int TYPE_MUSIC = 2;
    /* access modifiers changed from: private */
    public boolean mAddedMessageAnimationView = false;
    /* access modifiers changed from: private */
    public boolean mAddedMusicAnimationView = false;
    private Context mContext;
    private Handler mHandler;
    /* access modifiers changed from: private */
    public MessageView mMessageView = new MessageView(this.mContext);
    /* access modifiers changed from: private */
    public MusicView mMusicView = new MusicView(this.mContext);
    private PowerManager mPowerManager = ((PowerManager) this.mContext.getSystemService("power"));
    /* access modifiers changed from: private */
    public PowerManager.WakeLock mWakeLock = this.mPowerManager.newWakeLock(1, "u2-animation");
    /* access modifiers changed from: private */
    public WindowManager mWindowManager = ((WindowManager) this.mContext.getSystemService("window"));

    public AnimationController(Context context, Looper looper) {
        this.mContext = context;
        this.mHandler = new H(looper);
    }

    public void startAnimation(int type) {
        this.mHandler.sendEmptyMessage(type);
    }

    public void stopAnimation(int type) {
        this.mHandler.sendEmptyMessage(type);
    }

    public boolean isAnimationRunning(int type) {
        MessageView messageView;
        MusicView musicView;
        if (type == 2 && (musicView = this.mMusicView) != null) {
            return musicView.isAnimationRunning();
        }
        if (type != 1 || (messageView = this.mMessageView) == null) {
            return false;
        }
        return messageView.isAnimationRunning();
    }

    private class H extends Handler {
        public H(Looper looper) {
            super(looper);
        }

        public void handleMessage(Message msg) {
            try {
                int i = msg.what;
                if (i == 1 || i == 2) {
                    AnimationController.this.mWakeLock.acquire(30000);
                    if (AnimationController.this.mAddedMessageAnimationView) {
                        AnimationController.this.mWindowManager.removeView(AnimationController.this.mMessageView);
                        boolean unused = AnimationController.this.mAddedMessageAnimationView = false;
                    }
                    if (AnimationController.this.mAddedMusicAnimationView) {
                        AnimationController.this.mWindowManager.removeView(AnimationController.this.mMusicView);
                        boolean unused2 = AnimationController.this.mAddedMusicAnimationView = false;
                    }
                    WindowManager.LayoutParams layoutParams = AnimationController.this.getWindowParam();
                    if (msg.what == 2) {
                        AnimationController.this.mWindowManager.addView(AnimationController.this.mMusicView, layoutParams);
                        AnimationController.this.mMusicView.startAnimation();
                        boolean unused3 = AnimationController.this.mAddedMusicAnimationView = true;
                    } else if (msg.what == 1) {
                        AnimationController.this.mWindowManager.addView(AnimationController.this.mMessageView, layoutParams);
                        AnimationController.this.mMessageView.startAnimation();
                        boolean unused4 = AnimationController.this.mAddedMessageAnimationView = true;
                    }
                } else if (i == 3 || i == 4) {
                    if (AnimationController.this.mAddedMessageAnimationView && msg.what == 3) {
                        AnimationController.this.mMessageView.stopAnimation();
                        AnimationController.this.mWindowManager.removeView(AnimationController.this.mMessageView);
                        boolean unused5 = AnimationController.this.mAddedMessageAnimationView = false;
                    }
                    if (AnimationController.this.mAddedMusicAnimationView && msg.what == 4) {
                        AnimationController.this.mMusicView.stopAnimation();
                        AnimationController.this.mWindowManager.removeView(AnimationController.this.mMusicView);
                        boolean unused6 = AnimationController.this.mAddedMusicAnimationView = false;
                    }
                    if (AnimationController.this.mWakeLock.isHeld()) {
                        AnimationController.this.mWakeLock.release();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /* access modifiers changed from: private */
    public WindowManager.LayoutParams getWindowParam() {
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams(-1, -1, 2060, 17448, -3);
        lp.setTitle("U2-Animation");
        lp.screenOrientation = 1;
        lp.windowAnimations = 0;
        lp.x = 0;
        lp.y = 0;
        lp.width = 2048;
        lp.height = 2250;
        return lp;
    }
}
