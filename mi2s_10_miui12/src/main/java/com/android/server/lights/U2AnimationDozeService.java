package com.android.server.lights;

import android.media.AudioManager;
import android.media.AudioPlaybackConfiguration;
import android.os.Handler;
import android.os.PowerManager;
import android.service.dreams.DreamService;
import android.view.View;
import android.view.WindowManager;
import com.android.server.am.ExtraActivityManagerService;
import java.util.List;

public class U2AnimationDozeService extends DreamService {
    private static final String TAG = "U2AnimationDozeService";
    /* access modifiers changed from: private */
    public boolean mAcquire;
    private boolean mAddView;
    private AudioManager mAudioManager;
    private boolean mDreamStart = false;
    private Handler mHandler;
    private PowerManager mPowerManager;
    private Runnable mRunnable = new Runnable() {
        public void run() {
            U2AnimationDozeService.this.start();
        }
    };
    private View mView;
    /* access modifiers changed from: private */
    public PowerManager.WakeLock mWakeLock;
    private WindowManager mWindowManager;

    public void onCreate() {
        super.onCreate();
        this.mHandler = new Handler();
        this.mPowerManager = (PowerManager) getSystemService("power");
        this.mWakeLock = this.mPowerManager.newWakeLock(1, TAG);
        this.mWindowManager = (WindowManager) getSystemService("window");
        this.mView = new View(this);
        this.mView.setBackgroundColor(-16777216);
        this.mAudioManager = new AudioManager(this);
        addWindow();
        if (!isPlayingMusic()) {
            this.mHandler.postDelayed(new Runnable() {
                public void run() {
                    U2AnimationDozeService.this.finish();
                }
            }, 1000);
        }
    }

    public void startDozing() {
        if (!this.mAcquire) {
            this.mWakeLock.acquire();
            this.mAcquire = true;
        }
        this.mHandler.removeCallbacks(this.mRunnable);
        this.mHandler.postDelayed(this.mRunnable, 300);
    }

    /* access modifiers changed from: private */
    public void start() {
        super.startDozing();
        if (this.mAcquire) {
            this.mHandler.postDelayed(new Runnable() {
                public void run() {
                    U2AnimationDozeService.this.mWakeLock.release();
                    boolean unused = U2AnimationDozeService.this.mAcquire = false;
                }
            }, 1000);
        }
    }

    private boolean isPlayingMusic() {
        List<AudioPlaybackConfiguration> configs = this.mAudioManager.getActivePlaybackConfigurations();
        if (configs == null || configs.size() <= 0) {
            return false;
        }
        for (AudioPlaybackConfiguration config : configs) {
            if (config.getPlayerState() == 2) {
                if (VisualizerHolder.getInstance().isAllowed(ExtraActivityManagerService.getProcessNameByPid(config.getClientPid()))) {
                    return true;
                }
            }
        }
        return false;
    }

    private void addWindow() {
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams(-1, -1, 2026, 66872, -1);
        lp.layoutInDisplayCutoutMode = 1;
        this.mView.setSystemUiVisibility(4868);
        lp.screenOrientation = 1;
        if (!this.mAddView) {
            this.mWindowManager.addView(this.mView, lp);
            this.mAddView = true;
        }
    }

    public void onDreamingStarted() {
        super.onDreamingStarted();
        this.mDreamStart = true;
        startDozing();
    }

    public void setDozeScreenBrightness(int brightness) {
        if (this.mDreamStart) {
            super.setDozeScreenBrightness(brightness);
        }
    }

    public void onDreamingStopped() {
        super.onDreamingStopped();
        this.mDreamStart = false;
    }

    public void onDestroy() {
        if (this.mAddView) {
            this.mWindowManager.removeView(this.mView);
            this.mAddView = false;
        }
        super.onDestroy();
    }
}
