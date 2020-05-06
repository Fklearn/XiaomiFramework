package com.android.server.display;

import android.content.res.Resources;
import android.miui.R;
import android.os.ServiceManager;
import android.os.SystemClock;
import android.util.Slog;
import android.view.MotionEvent;
import android.view.WindowManagerPolicyConstants;
import com.android.server.job.JobPackageTracker;
import com.android.server.wm.WindowManagerService;
import java.io.PrintWriter;

public class AutomaticBrightnessTouchHelper {
    /* access modifiers changed from: private */
    public static boolean DEBUG = false;
    /* access modifiers changed from: private */
    public static float SCREEN_HEIGHT = ((float) Resources.getSystem().getDisplayMetrics().heightPixels);
    /* access modifiers changed from: private */
    public static float SCREEN_WIDTH = ((float) Resources.getSystem().getDisplayMetrics().widthPixels);
    private static final String TAG = "AutomaticBrightnessTouchHelper";
    /* access modifiers changed from: private */
    public static final float TOUCHBOTTOM = ((float) Resources.getSystem().getInteger(R.integer.config_automatic_brightness_touchbottom));
    /* access modifiers changed from: private */
    public static final float TOUCHLEFT = ((float) Resources.getSystem().getInteger(R.integer.config_automatic_brightness_touchleft));
    /* access modifiers changed from: private */
    public static final float TOUCHRIGHT = ((float) Resources.getSystem().getInteger(R.integer.config_automatic_brightness_touchright));
    /* access modifiers changed from: private */
    public static final float TOUCHTOP = ((float) Resources.getSystem().getInteger(R.integer.config_automatic_brightness_touchtop));
    private static final int TOUCH_EVENT_DEBOUNCE = Resources.getSystem().getInteger(R.integer.config_auto_brightness_touch_event_debounce);
    private static final boolean USE_TOUCH_ENABLED = Resources.getSystem().getBoolean(R.bool.config_automatic_brightness_usetouch);
    private static TouchPositionTracker sTouchPositionTracker;
    private static boolean sTouchTrackingEnabled = false;
    static WindowManagerService sWms;

    static void initialize() {
        if (USE_TOUCH_ENABLED) {
            sWms = (WindowManagerService) ServiceManager.getService("window");
            sTouchPositionTracker = new TouchPositionTracker();
            sTouchPositionTracker.updateTouchBorder(TOUCHLEFT, TOUCHRIGHT, TOUCHTOP, TOUCHBOTTOM);
        }
    }

    static void configure(boolean enable) {
        if (USE_TOUCH_ENABLED) {
            setTouchTrackingEnabled(enable);
        }
    }

    static boolean checkTouchStatus(float lux, boolean luxValid) {
        if (!USE_TOUCH_ENABLED) {
            return false;
        }
        if (DEBUG || !luxValid) {
            Slog.d(TAG, "sTouchTrackingEnabled=" + sTouchTrackingEnabled + ", lux=" + lux);
        }
        if (sTouchPositionTracker.getTouchStatus() != 1 || !luxValid) {
            long mNowTime = SystemClock.elapsedRealtimeNanos();
            if (sTouchPositionTracker.getTouchStatus() != 0 || mNowTime - sTouchPositionTracker.mLastObservedTouchTime >= ((long) (TOUCH_EVENT_DEBOUNCE * 1000000000)) || !luxValid) {
                return false;
            }
            if (DEBUG) {
                Slog.d(TAG, "checkTouchStatus: { mow time: " + mNowTime + " last positive time: " + sTouchPositionTracker.mLastObservedTouchTime + "}");
            }
            Slog.w(TAG, "drop the lightsensor event due to touch event timeout not reached! lux=" + lux);
            return true;
        }
        Slog.d(TAG, "drop the lightsensor event due to touch event occured! lux=" + lux);
        return true;
    }

    static void dump(PrintWriter pw, boolean enableDebug) {
        if (USE_TOUCH_ENABLED) {
            pw.println("  SCREEN_WIDTH=" + SCREEN_WIDTH);
            pw.println("  SCREEN_HEIGHT=" + SCREEN_HEIGHT);
            pw.println("  TOUCHLEFT=" + TOUCHLEFT);
            pw.println("  TOUCHRIGHT=" + TOUCHRIGHT);
            pw.println("  TOUCHTOP=" + TOUCHTOP);
            pw.println("  TOUCHBOTTOM=" + TOUCHBOTTOM);
            pw.println("  TOUCH_EVENT_DEBOUNCE=" + TOUCH_EVENT_DEBOUNCE);
            DEBUG = enableDebug;
        }
    }

    private static void setTouchTrackingEnabled(boolean enable) {
        if (!enable) {
            TouchPositionTracker touchPositionTracker = sTouchPositionTracker;
            if (touchPositionTracker != null && sTouchTrackingEnabled) {
                sWms.unregisterPointerEventListener(touchPositionTracker, 0);
                sTouchTrackingEnabled = false;
                Slog.d(TAG, "touch pointer listener has unregistered!");
            }
        } else if (!sTouchTrackingEnabled) {
            sWms.registerPointerEventListener(sTouchPositionTracker, 0);
            sTouchTrackingEnabled = true;
            Slog.d(TAG, "touch pointer listener has registered!");
        }
    }

    private static class TouchPositionTracker implements WindowManagerPolicyConstants.PointerEventListener {
        private static final int TOUCH_NEGATIVE = 0;
        private static final int TOUCH_POSITIVE = 1;
        private static final int TOUCH_UNKNOWN = -1;
        /* access modifiers changed from: private */
        public long mLastObservedTouchTime;
        private short mPointerIndexTriggerBitMask;
        private float mTouchBottom;
        private float mTouchLeft;
        private float mTouchRight;
        int mTouchStatus;
        private float mTouchTop;

        private TouchPositionTracker() {
            this.mTouchStatus = -1;
            this.mTouchLeft = 0.0f;
            this.mTouchRight = 500.0f;
            this.mTouchTop = 0.0f;
            this.mTouchBottom = 500.0f;
            this.mLastObservedTouchTime = 0;
            this.mPointerIndexTriggerBitMask = 0;
        }

        /* access modifiers changed from: package-private */
        public void updateTouchBorder(float left, float right, float top, float bottom) {
            this.mTouchLeft = left;
            this.mTouchRight = right;
            this.mTouchTop = top;
            this.mTouchBottom = bottom;
            if (AutomaticBrightnessTouchHelper.DEBUG) {
                Slog.d(AutomaticBrightnessTouchHelper.TAG, "updateTouchBorder!!");
            }
        }

        /* access modifiers changed from: package-private */
        public void updatePosition(MotionEvent.PointerCoords mPointerCoords, int pointerId) {
            if (AutomaticBrightnessTouchHelper.DEBUG) {
                Slog.d(AutomaticBrightnessTouchHelper.TAG, "touch event updatePosition [ID: " + pointerId + ", Ori: " + mPointerCoords.getAxisValue(8) + "],  { " + mPointerCoords.getAxisValue(0) + ", " + mPointerCoords.getAxisValue(1) + "}");
            }
            if (mPointerCoords.getAxisValue(8) == 0.0f) {
                updateTouchBorder(AutomaticBrightnessTouchHelper.TOUCHLEFT, AutomaticBrightnessTouchHelper.TOUCHRIGHT, AutomaticBrightnessTouchHelper.TOUCHTOP, AutomaticBrightnessTouchHelper.TOUCHBOTTOM);
            } else if (mPointerCoords.getAxisValue(8) < 0.0f) {
                updateTouchBorder(AutomaticBrightnessTouchHelper.TOUCHTOP, AutomaticBrightnessTouchHelper.TOUCHBOTTOM, AutomaticBrightnessTouchHelper.SCREEN_WIDTH - AutomaticBrightnessTouchHelper.TOUCHRIGHT, AutomaticBrightnessTouchHelper.SCREEN_WIDTH - AutomaticBrightnessTouchHelper.TOUCHLEFT);
            } else if (mPointerCoords.getAxisValue(8) > 0.0f) {
                updateTouchBorder(AutomaticBrightnessTouchHelper.SCREEN_HEIGHT - AutomaticBrightnessTouchHelper.TOUCHBOTTOM, AutomaticBrightnessTouchHelper.SCREEN_HEIGHT - AutomaticBrightnessTouchHelper.TOUCHTOP, AutomaticBrightnessTouchHelper.TOUCHLEFT, AutomaticBrightnessTouchHelper.TOUCHRIGHT);
            }
            if (mPointerCoords.getAxisValue(0) <= this.mTouchLeft || mPointerCoords.getAxisValue(0) >= this.mTouchRight || mPointerCoords.getAxisValue(1) <= this.mTouchTop || mPointerCoords.getAxisValue(1) >= this.mTouchBottom) {
                this.mPointerIndexTriggerBitMask = (short) (this.mPointerIndexTriggerBitMask & (~(1 << pointerId)));
            } else {
                this.mPointerIndexTriggerBitMask = (short) (this.mPointerIndexTriggerBitMask | (1 << pointerId));
                if (AutomaticBrightnessTouchHelper.DEBUG) {
                    Slog.w(AutomaticBrightnessTouchHelper.TAG, "touch: { time: " + this.mLastObservedTouchTime + "}");
                }
            }
            if (this.mPointerIndexTriggerBitMask != 0) {
                this.mTouchStatus = 1;
            } else {
                this.mTouchStatus = 0;
            }
        }

        /* access modifiers changed from: package-private */
        public int getTouchStatus() {
            if (AutomaticBrightnessTouchHelper.DEBUG) {
                Slog.d(AutomaticBrightnessTouchHelper.TAG, "touch status=" + this.mTouchStatus);
            }
            return this.mTouchStatus;
        }

        public void onPointerEvent(MotionEvent motionEvent) {
            if (motionEvent.isTouchEvent()) {
                int action = motionEvent.getAction();
                int mPointerCount = motionEvent.getPointerCount();
                MotionEvent.PointerCoords mPointerCoords = new MotionEvent.PointerCoords();
                if (AutomaticBrightnessTouchHelper.DEBUG) {
                    for (int i = 0; i < mPointerCount; i++) {
                        int mPointerId = motionEvent.getPointerId(i);
                        Slog.d(AutomaticBrightnessTouchHelper.TAG, "touch onPointerEvent: index: " + i + ", id:  " + mPointerId);
                    }
                }
                int i2 = action & 255;
                if (i2 != 0) {
                    if (i2 != 1) {
                        if (i2 != 2) {
                            if (AutomaticBrightnessTouchHelper.DEBUG) {
                                Slog.d(AutomaticBrightnessTouchHelper.TAG, "touch onPointerEvent action: " + action);
                                return;
                            }
                            return;
                        }
                    } else if (action == 1) {
                        if (AutomaticBrightnessTouchHelper.DEBUG) {
                            Slog.d(AutomaticBrightnessTouchHelper.TAG, "touch onPointerEvent ACTION_UP, mTouchStatus = " + this.mTouchStatus);
                        }
                        if (this.mTouchStatus == 1) {
                            this.mLastObservedTouchTime = SystemClock.elapsedRealtimeNanos();
                        }
                        this.mTouchStatus = 0;
                        return;
                    } else {
                        this.mPointerIndexTriggerBitMask = (short) (this.mPointerIndexTriggerBitMask & (~((action & JobPackageTracker.EVENT_STOP_REASON_MASK) >> 8)));
                        if (AutomaticBrightnessTouchHelper.DEBUG) {
                            Slog.d(AutomaticBrightnessTouchHelper.TAG, "touch onPointerEvent ACTION_POINTER_UP: " + ((65280 & action) >> 8) + ".");
                            return;
                        }
                        return;
                    }
                }
                for (int i3 = 0; i3 < mPointerCount; i3++) {
                    motionEvent.getPointerCoords(i3, mPointerCoords);
                    if (AutomaticBrightnessTouchHelper.DEBUG) {
                        Slog.d(AutomaticBrightnessTouchHelper.TAG, "touch getPointerCoords: ORIENTATION = " + mPointerCoords.getAxisValue(8) + ", x = " + mPointerCoords.getAxisValue(0) + ", y = " + mPointerCoords.getAxisValue(1));
                    }
                    updatePosition(mPointerCoords, motionEvent.getPointerId(i3));
                }
            }
        }
    }
}
