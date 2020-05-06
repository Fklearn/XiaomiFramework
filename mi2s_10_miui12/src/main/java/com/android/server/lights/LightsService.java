package com.android.server.lights;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.PowerManager;
import android.os.Trace;
import android.provider.Settings;
import android.view.SurfaceControl;
import com.android.server.SystemService;

public class LightsService extends SystemService {
    static final boolean DEBUG = false;
    static final String TAG = "LightsService";
    /* access modifiers changed from: private */
    public Handler mH = new Handler() {
        public void handleMessage(Message msg) {
            ((LightImpl) msg.obj).stopFlashing();
        }
    };
    final LightImpl[] mLights = new LightImpl[8];
    private final LightsManager mService = new LightsManager() {
        public Light getLight(int id) {
            if (id < 0 || id >= 8) {
                return null;
            }
            return LightsService.this.mLights[id];
        }
    };

    static native void setLight_native(int i, int i2, int i3, int i4, int i5, int i6);

    public class LightImpl extends Light {
        private int mBrightnessMode;
        private int mColor;
        private final IBinder mDisplayToken;
        private boolean mFlashing;
        private int mId;
        private boolean mInitialized;
        private int mLastBrightnessMode;
        private int mLastColor;
        private int mMode;
        private int mOffMS;
        private int mOnMS;
        private final int mSurfaceControlMaximumBrightness;
        private boolean mUseLowPersistenceForVR;
        private boolean mVrModeEnabled;

        LightImpl(LightsService this$02, Context context, int id, int unused) {
            this(context, id);
        }

        private LightImpl(Context context, int id) {
            PowerManager pm;
            this.mId = id;
            this.mDisplayToken = SurfaceControl.getInternalDisplayToken();
            int maximumBrightness = 0;
            if (SurfaceControl.getDisplayBrightnessSupport(this.mDisplayToken) && (pm = (PowerManager) context.getSystemService(PowerManager.class)) != null) {
                maximumBrightness = pm.getMaximumScreenBrightnessSetting();
            }
            this.mSurfaceControlMaximumBrightness = maximumBrightness;
        }

        public void setBrightness(int brightness) {
            setBrightness(brightness, 0);
        }

        /* JADX WARNING: Code restructure failed: missing block: B:18:0x0057, code lost:
            return;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void setBrightness(int r9, int r10) {
            /*
                r8 = this;
                monitor-enter(r8)
                r0 = 2
                if (r10 != r0) goto L_0x002d
                java.lang.String r0 = "LightsService"
                java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ all -> 0x002b }
                r1.<init>()     // Catch:{ all -> 0x002b }
                java.lang.String r2 = "setBrightness with LOW_PERSISTENCE unexpected #"
                r1.append(r2)     // Catch:{ all -> 0x002b }
                int r2 = r8.mId     // Catch:{ all -> 0x002b }
                r1.append(r2)     // Catch:{ all -> 0x002b }
                java.lang.String r2 = ": brightness=0x"
                r1.append(r2)     // Catch:{ all -> 0x002b }
                java.lang.String r2 = java.lang.Integer.toHexString(r9)     // Catch:{ all -> 0x002b }
                r1.append(r2)     // Catch:{ all -> 0x002b }
                java.lang.String r1 = r1.toString()     // Catch:{ all -> 0x002b }
                android.util.Slog.w(r0, r1)     // Catch:{ all -> 0x002b }
                monitor-exit(r8)     // Catch:{ all -> 0x002b }
                return
            L_0x002b:
                r0 = move-exception
                goto L_0x0058
            L_0x002d:
                if (r10 != 0) goto L_0x0046
                boolean r0 = r8.shouldBeInLowPersistenceMode()     // Catch:{ all -> 0x002b }
                if (r0 != 0) goto L_0x0046
                int r0 = r8.mSurfaceControlMaximumBrightness     // Catch:{ all -> 0x002b }
                r1 = 255(0xff, float:3.57E-43)
                if (r0 != r1) goto L_0x0046
                android.os.IBinder r0 = r8.mDisplayToken     // Catch:{ all -> 0x002b }
                float r1 = (float) r9     // Catch:{ all -> 0x002b }
                int r2 = r8.mSurfaceControlMaximumBrightness     // Catch:{ all -> 0x002b }
                float r2 = (float) r2     // Catch:{ all -> 0x002b }
                float r1 = r1 / r2
                android.view.SurfaceControl.setDisplayBrightness(r0, r1)     // Catch:{ all -> 0x002b }
                goto L_0x0056
            L_0x0046:
                int r0 = r8.mId     // Catch:{ all -> 0x002b }
                int r1 = r8.mColor     // Catch:{ all -> 0x002b }
                int r3 = com.android.server.lights.MiuiLightsService.brightnessToColor(r0, r9, r1)     // Catch:{ all -> 0x002b }
                r4 = 0
                r5 = 0
                r6 = 0
                r2 = r8
                r7 = r10
                r2.setLightLocked(r3, r4, r5, r6, r7)     // Catch:{ all -> 0x002b }
            L_0x0056:
                monitor-exit(r8)     // Catch:{ all -> 0x002b }
                return
            L_0x0058:
                monitor-exit(r8)     // Catch:{ all -> 0x002b }
                throw r0
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.server.lights.LightsService.LightImpl.setBrightness(int, int):void");
        }

        public void setColor(int color) {
            synchronized (this) {
                setLightLocked(color, 0, 0, 0, 0);
            }
        }

        public void setFlashing(int color, int mode, int onMS, int offMS) {
            synchronized (this) {
                setLightLocked(color, mode, onMS, offMS, 0);
            }
        }

        public void pulse() {
            pulse(16777215, 7);
        }

        public void pulse(int color, int onMS) {
            synchronized (this) {
                if (this.mColor == 0 && !this.mFlashing) {
                    setLightLocked(color, 2, onMS, 1000, 0);
                    this.mColor = 0;
                    LightsService.this.mH.sendMessageDelayed(Message.obtain(LightsService.this.mH, 1, this), (long) onMS);
                }
            }
        }

        public void turnOff() {
            synchronized (this) {
                setLightLocked(0, 0, 0, 0, 0);
            }
        }

        public void setVrMode(boolean enabled) {
            synchronized (this) {
                if (this.mVrModeEnabled != enabled) {
                    this.mVrModeEnabled = enabled;
                    this.mUseLowPersistenceForVR = LightsService.this.getVrDisplayMode() == 0;
                    if (shouldBeInLowPersistenceMode()) {
                        this.mLastBrightnessMode = this.mBrightnessMode;
                    }
                }
            }
        }

        /* access modifiers changed from: private */
        public void stopFlashing() {
            synchronized (this) {
                setLightLocked(this.mColor, 0, 0, 0, 0);
            }
        }

        /* access modifiers changed from: package-private */
        public void setLightLocked(int color, int mode, int onMS, int offMS, int brightnessMode) {
            if (shouldBeInLowPersistenceMode()) {
                brightnessMode = 2;
            } else if (brightnessMode == 2) {
                brightnessMode = this.mLastBrightnessMode;
            }
            if (!this.mInitialized || color != this.mColor || mode != this.mMode || onMS != this.mOnMS || offMS != this.mOffMS || this.mBrightnessMode != brightnessMode) {
                this.mInitialized = true;
                this.mLastColor = this.mColor;
                this.mColor = color;
                this.mMode = mode;
                this.mOnMS = onMS;
                this.mOffMS = offMS;
                this.mBrightnessMode = brightnessMode;
                Trace.traceBegin(131072, "setLight(" + this.mId + ", 0x" + Integer.toHexString(color) + ")");
                try {
                    LightsService.setLight_native(this.mId, color, mode, onMS, offMS, brightnessMode);
                } finally {
                    Trace.traceEnd(131072);
                }
            }
        }

        private boolean shouldBeInLowPersistenceMode() {
            return this.mVrModeEnabled && this.mUseLowPersistenceForVR;
        }
    }

    public LightsService(Context context) {
        super(context);
        for (int i = 0; i < 8; i++) {
            this.mLights[i] = new LightImpl(context, i);
        }
    }

    public void onStart() {
        publishLocalService(LightsManager.class, this.mService);
    }

    public void onBootPhase(int phase) {
    }

    /* access modifiers changed from: private */
    public int getVrDisplayMode() {
        return Settings.Secure.getIntForUser(getContext().getContentResolver(), "vr_display_mode", 0, ActivityManager.getCurrentUser());
    }
}
