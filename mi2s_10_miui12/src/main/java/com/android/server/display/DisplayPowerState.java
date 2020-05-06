package com.android.server.display;

import android.content.Context;
import android.os.Handler;
import android.os.PowerManager;
import android.os.Trace;
import android.util.FloatProperty;
import android.util.IntProperty;
import android.util.Slog;
import android.view.Choreographer;
import android.view.Display;
import java.io.PrintWriter;

final class DisplayPowerState {
    public static final FloatProperty<DisplayPowerState> COLOR_FADE_LEVEL = new FloatProperty<DisplayPowerState>("electronBeamLevel") {
        public void setValue(DisplayPowerState object, float value) {
            object.setColorFadeLevel(value);
        }

        public Float get(DisplayPowerState object) {
            return Float.valueOf(object.getColorFadeLevel());
        }
    };
    /* access modifiers changed from: private */
    public static String COUNTER_COLOR_FADE = "ColorFadeLevel";
    /* access modifiers changed from: private */
    public static boolean DEBUG = false;
    public static final IntProperty<DisplayPowerState> SCREEN_BRIGHTNESS = new IntProperty<DisplayPowerState>("screenBrightness") {
        public void setValue(DisplayPowerState object, int value) {
            object.setScreenBrightness(value);
        }

        public Integer get(DisplayPowerState object) {
            return Integer.valueOf(object.getScreenBrightness());
        }
    };
    private static final String TAG = "DisplayPowerState";
    /* access modifiers changed from: private */
    public final DisplayBlanker mBlanker;
    private final Choreographer mChoreographer = Choreographer.getInstance();
    private Runnable mCleanListener;
    /* access modifiers changed from: private */
    public final ColorFade mColorFade;
    /* access modifiers changed from: private */
    public boolean mColorFadeDrawPending;
    private final Runnable mColorFadeDrawRunnable = new Runnable() {
        public void run() {
            boolean unused = DisplayPowerState.this.mColorFadeDrawPending = false;
            if (DisplayPowerState.this.mColorFadePrepared) {
                DisplayPowerState.this.mColorFade.draw(DisplayPowerState.this.mColorFadeLevel);
                Trace.traceCounter(131072, DisplayPowerState.COUNTER_COLOR_FADE, Math.round(DisplayPowerState.this.mColorFadeLevel * 100.0f));
            }
            boolean unused2 = DisplayPowerState.this.mColorFadeReady = true;
            DisplayPowerState.this.invokeCleanListenerIfNeeded();
        }
    };
    /* access modifiers changed from: private */
    public float mColorFadeLevel;
    /* access modifiers changed from: private */
    public boolean mColorFadePrepared;
    /* access modifiers changed from: private */
    public boolean mColorFadeReady;
    private final Handler mHandler = new Handler(true);
    /* access modifiers changed from: private */
    public final PhotonicModulator mPhotonicModulator;
    /* access modifiers changed from: private */
    public int mScreenBrightness;
    /* access modifiers changed from: private */
    public boolean mScreenReady;
    /* access modifiers changed from: private */
    public int mScreenState;
    /* access modifiers changed from: private */
    public boolean mScreenUpdatePending;
    private final Runnable mScreenUpdateRunnable = new Runnable() {
        public void run() {
            int brightness = 0;
            boolean unused = DisplayPowerState.this.mScreenUpdatePending = false;
            if (DisplayPowerState.this.mScreenState != 1 && DisplayPowerState.this.mColorFadeLevel > 0.0f) {
                brightness = DisplayPowerState.this.mScreenBrightness;
            }
            if (DisplayPowerState.this.mPhotonicModulator.setState(DisplayPowerState.this.mScreenState, brightness)) {
                if (DisplayPowerState.DEBUG) {
                    Slog.d(DisplayPowerState.TAG, "Screen ready");
                }
                boolean unused2 = DisplayPowerState.this.mScreenReady = true;
                DisplayPowerState.this.invokeCleanListenerIfNeeded();
            } else if (DisplayPowerState.DEBUG) {
                Slog.d(DisplayPowerState.TAG, "Screen not ready");
            }
        }
    };

    public DisplayPowerState(DisplayBlanker blanker, ColorFade colorFade) {
        this.mBlanker = blanker;
        this.mColorFade = colorFade;
        this.mPhotonicModulator = new PhotonicModulator();
        this.mPhotonicModulator.start();
        this.mScreenState = 2;
        this.mScreenBrightness = PowerManager.BRIGHTNESS_ON;
        scheduleScreenUpdate();
        this.mColorFadePrepared = false;
        this.mColorFadeLevel = 1.0f;
        this.mColorFadeReady = true;
    }

    public void setScreenState(int state) {
        if (this.mScreenState != state) {
            if (DEBUG) {
                Slog.d(TAG, "setScreenState: state=" + state);
            }
            this.mScreenState = state;
            this.mScreenReady = false;
            scheduleScreenUpdate();
        }
    }

    public int getScreenState() {
        return this.mScreenState;
    }

    public void setScreenBrightness(int brightness) {
        if (this.mScreenBrightness != brightness) {
            if (DEBUG) {
                Slog.d(TAG, "setScreenBrightness: brightness=" + brightness);
            }
            this.mScreenBrightness = brightness;
            if (this.mScreenState != 1) {
                this.mScreenReady = false;
                scheduleScreenUpdate();
            }
        }
    }

    public int getScreenBrightness() {
        return this.mScreenBrightness;
    }

    public boolean prepareColorFade(Context context, int mode) {
        ColorFade colorFade = this.mColorFade;
        if (colorFade == null || !colorFade.prepare(context, mode)) {
            this.mColorFadePrepared = false;
            this.mColorFadeReady = true;
            return false;
        }
        this.mColorFadePrepared = true;
        this.mColorFadeReady = false;
        scheduleColorFadeDraw();
        return true;
    }

    public void dismissColorFade() {
        Trace.traceCounter(131072, COUNTER_COLOR_FADE, 100);
        ColorFade colorFade = this.mColorFade;
        if (colorFade != null) {
            colorFade.dismiss();
        }
        this.mColorFadePrepared = false;
        this.mColorFadeReady = true;
    }

    public void dismissColorFadeResources() {
        ColorFade colorFade = this.mColorFade;
        if (colorFade != null) {
            colorFade.dismissResources();
        }
    }

    public void setColorFadeLevel(float level) {
        if (this.mColorFadeLevel != level) {
            if (DEBUG) {
                Slog.d(TAG, "setColorFadeLevel: level=" + level);
            }
            this.mColorFadeLevel = level;
            if (this.mScreenState != 1) {
                this.mScreenReady = false;
                scheduleScreenUpdate();
            }
            if (this.mColorFadePrepared) {
                this.mColorFadeReady = false;
                scheduleColorFadeDraw();
            }
        }
    }

    public float getColorFadeLevel() {
        return this.mColorFadeLevel;
    }

    public boolean waitUntilClean(Runnable listener) {
        if (!this.mScreenReady || !this.mColorFadeReady) {
            this.mCleanListener = listener;
            return false;
        }
        this.mCleanListener = null;
        return true;
    }

    public void dump(PrintWriter pw) {
        pw.println();
        pw.println("Display Power State:");
        pw.println("  mScreenState=" + Display.stateToString(this.mScreenState));
        pw.println("  mScreenBrightness=" + this.mScreenBrightness);
        pw.println("  mScreenReady=" + this.mScreenReady);
        pw.println("  mScreenUpdatePending=" + this.mScreenUpdatePending);
        pw.println("  mColorFadePrepared=" + this.mColorFadePrepared);
        pw.println("  mColorFadeLevel=" + this.mColorFadeLevel);
        pw.println("  mColorFadeReady=" + this.mColorFadeReady);
        pw.println("  mColorFadeDrawPending=" + this.mColorFadeDrawPending);
        this.mPhotonicModulator.dump(pw);
        ColorFade colorFade = this.mColorFade;
        if (colorFade != null) {
            colorFade.dump(pw);
        }
    }

    private void scheduleScreenUpdate() {
        if (!this.mScreenUpdatePending) {
            this.mScreenUpdatePending = true;
            postScreenUpdateThreadSafe();
        }
    }

    /* access modifiers changed from: private */
    public void postScreenUpdateThreadSafe() {
        this.mHandler.removeCallbacks(this.mScreenUpdateRunnable);
        this.mHandler.post(this.mScreenUpdateRunnable);
    }

    private void scheduleColorFadeDraw() {
        if (!this.mColorFadeDrawPending) {
            this.mColorFadeDrawPending = true;
            this.mChoreographer.postCallback(3, this.mColorFadeDrawRunnable, (Object) null);
        }
    }

    /* access modifiers changed from: private */
    public void invokeCleanListenerIfNeeded() {
        Runnable listener = this.mCleanListener;
        if (listener != null && this.mScreenReady && this.mColorFadeReady) {
            this.mCleanListener = null;
            listener.run();
        }
    }

    private final class PhotonicModulator extends Thread {
        private static final int INITIAL_BACKLIGHT = -1;
        private static final int INITIAL_SCREEN_STATE = 1;
        private int mActualBacklight = -1;
        private int mActualState = 1;
        private boolean mBacklightChangeInProgress;
        private final Object mLock = new Object();
        private int mPendingBacklight = -1;
        private int mPendingState = 1;
        private boolean mStateChangeInProgress;

        public PhotonicModulator() {
            super("PhotonicModulator");
        }

        /* JADX WARNING: Removed duplicated region for block: B:24:0x0051  */
        /* JADX WARNING: Removed duplicated region for block: B:31:0x005d  */
        /* JADX WARNING: Removed duplicated region for block: B:38:0x0069  */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public boolean setState(int r9, int r10) {
            /*
                r8 = this;
                java.lang.Object r0 = r8.mLock
                monitor-enter(r0)
                int r1 = r8.mPendingState     // Catch:{ all -> 0x0076 }
                r2 = 1
                r3 = 0
                if (r9 == r1) goto L_0x000b
                r1 = r2
                goto L_0x000c
            L_0x000b:
                r1 = r3
            L_0x000c:
                int r4 = r8.mPendingBacklight     // Catch:{ all -> 0x0076 }
                if (r10 == r4) goto L_0x0012
                r4 = r2
                goto L_0x0013
            L_0x0012:
                r4 = r3
            L_0x0013:
                if (r1 != 0) goto L_0x0017
                if (r4 == 0) goto L_0x006e
            L_0x0017:
                boolean r5 = com.android.server.display.DisplayPowerState.DEBUG     // Catch:{ all -> 0x0076 }
                if (r5 == 0) goto L_0x003f
                java.lang.String r5 = "DisplayPowerState"
                java.lang.StringBuilder r6 = new java.lang.StringBuilder     // Catch:{ all -> 0x0076 }
                r6.<init>()     // Catch:{ all -> 0x0076 }
                java.lang.String r7 = "Requesting new screen state: state="
                r6.append(r7)     // Catch:{ all -> 0x0076 }
                java.lang.String r7 = android.view.Display.stateToString(r9)     // Catch:{ all -> 0x0076 }
                r6.append(r7)     // Catch:{ all -> 0x0076 }
                java.lang.String r7 = ", backlight="
                r6.append(r7)     // Catch:{ all -> 0x0076 }
                r6.append(r10)     // Catch:{ all -> 0x0076 }
                java.lang.String r6 = r6.toString()     // Catch:{ all -> 0x0076 }
                android.util.Slog.d(r5, r6)     // Catch:{ all -> 0x0076 }
            L_0x003f:
                r8.mPendingState = r9     // Catch:{ all -> 0x0076 }
                r8.mPendingBacklight = r10     // Catch:{ all -> 0x0076 }
                boolean r5 = r8.mStateChangeInProgress     // Catch:{ all -> 0x0076 }
                if (r5 != 0) goto L_0x004e
                boolean r5 = r8.mBacklightChangeInProgress     // Catch:{ all -> 0x0076 }
                if (r5 == 0) goto L_0x004c
                goto L_0x004e
            L_0x004c:
                r5 = r3
                goto L_0x004f
            L_0x004e:
                r5 = r2
            L_0x004f:
                if (r1 != 0) goto L_0x0058
                boolean r6 = r8.mStateChangeInProgress     // Catch:{ all -> 0x0076 }
                if (r6 == 0) goto L_0x0056
                goto L_0x0058
            L_0x0056:
                r6 = r3
                goto L_0x0059
            L_0x0058:
                r6 = r2
            L_0x0059:
                r8.mStateChangeInProgress = r6     // Catch:{ all -> 0x0076 }
                if (r4 != 0) goto L_0x0064
                boolean r6 = r8.mBacklightChangeInProgress     // Catch:{ all -> 0x0076 }
                if (r6 == 0) goto L_0x0062
                goto L_0x0064
            L_0x0062:
                r6 = r3
                goto L_0x0065
            L_0x0064:
                r6 = r2
            L_0x0065:
                r8.mBacklightChangeInProgress = r6     // Catch:{ all -> 0x0076 }
                if (r5 != 0) goto L_0x006e
                java.lang.Object r6 = r8.mLock     // Catch:{ all -> 0x0076 }
                r6.notifyAll()     // Catch:{ all -> 0x0076 }
            L_0x006e:
                boolean r5 = r8.mStateChangeInProgress     // Catch:{ all -> 0x0076 }
                if (r5 != 0) goto L_0x0073
                goto L_0x0074
            L_0x0073:
                r2 = r3
            L_0x0074:
                monitor-exit(r0)     // Catch:{ all -> 0x0076 }
                return r2
            L_0x0076:
                r1 = move-exception
                monitor-exit(r0)     // Catch:{ all -> 0x0076 }
                throw r1
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.server.display.DisplayPowerState.PhotonicModulator.setState(int, int):boolean");
        }

        public void dump(PrintWriter pw) {
            synchronized (this.mLock) {
                pw.println();
                pw.println("Photonic Modulator State:");
                pw.println("  mPendingState=" + Display.stateToString(this.mPendingState));
                pw.println("  mPendingBacklight=" + this.mPendingBacklight);
                pw.println("  mActualState=" + Display.stateToString(this.mActualState));
                pw.println("  mActualBacklight=" + this.mActualBacklight);
                pw.println("  mStateChangeInProgress=" + this.mStateChangeInProgress);
                pw.println("  mBacklightChangeInProgress=" + this.mBacklightChangeInProgress);
            }
        }

        public void run() {
            while (true) {
                synchronized (this.mLock) {
                    int state = this.mPendingState;
                    boolean backlightChanged = true;
                    boolean stateChanged = state != this.mActualState;
                    int backlight = this.mPendingBacklight;
                    if (backlight == this.mActualBacklight) {
                        backlightChanged = false;
                    }
                    if (!stateChanged) {
                        DisplayPowerState.this.postScreenUpdateThreadSafe();
                        this.mStateChangeInProgress = false;
                    }
                    if (!backlightChanged) {
                        this.mBacklightChangeInProgress = false;
                    }
                    if (stateChanged || backlightChanged) {
                        this.mActualState = state;
                        this.mActualBacklight = backlight;
                        if (DisplayPowerState.DEBUG) {
                            Slog.d(DisplayPowerState.TAG, "Updating screen state: state=" + Display.stateToString(state) + ", backlight=" + backlight);
                        }
                        DisplayPowerState.this.mBlanker.requestDisplayState(state, backlight);
                    } else {
                        try {
                            this.mLock.wait();
                        } catch (InterruptedException e) {
                        }
                    }
                }
            }
        }
    }
}
