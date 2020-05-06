package com.android.server.wm;

import android.graphics.Rect;
import android.util.MiuiMultiWindowUtils;
import android.util.Slog;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.VelocityTracker;

public class MiuiFreeFormSmallWindowMotionHelper {
    public static final String TAG = "MiuiFreeFormSmallWindowMotionHelper";
    private int SLIDE_OUT_VELOCITY_THRESHOLD_DP = -170;
    private int mCurrentAction = -1;
    public final GestureDetector.OnGestureListener mGestureListener = new GestureDetector.SimpleOnGestureListener() {
        public boolean onSingleTapConfirmed(MotionEvent motionEvent) {
            try {
                if (!MiuiFreeFormSmallWindowMotionHelper.this.mListener.getSmallFreeFormWindowBounds().contains((int) motionEvent.getX(), (int) motionEvent.getY())) {
                    return false;
                }
                Slog.d(MiuiFreeFormSmallWindowMotionHelper.TAG, "onSingleTapConfirmed");
                MiuiFreeFormSmallWindowMotionHelper.this.startShowFreeFormWindow();
                return true;
            } catch (Exception e) {
                return false;
            }
        }

        public boolean onDoubleTap(MotionEvent motionEvent) {
            try {
                if (!MiuiFreeFormSmallWindowMotionHelper.this.mListener.getSmallFreeFormWindowBounds().contains((int) motionEvent.getX(), (int) motionEvent.getY())) {
                    return false;
                }
                Slog.d(MiuiFreeFormSmallWindowMotionHelper.TAG, "onDoubleTap");
                MiuiFreeFormSmallWindowMotionHelper.this.startShowFullScreenWindow();
                return true;
            } catch (Exception e) {
                return false;
            }
        }
    };
    /* access modifiers changed from: private */
    public MiuiFreeFormGesturePointerEventListener mListener;
    private Rect mSmallWindowBounds = new Rect();
    public int mStartDragX;
    public int mStartDragY;
    private boolean mTouchedInSmallWindowBounds = false;
    private VelocityTracker mVelocityTracker;
    private float mXVelocity = 0.0f;
    private float mYVelocity = 0.0f;

    public MiuiFreeFormSmallWindowMotionHelper(MiuiFreeFormGesturePointerEventListener listener) {
        this.mListener = listener;
    }

    public void notifyDownLocked(MotionEvent motionEvent) {
        if (this.mListener.mFreeFormWindowMotionHelper.mCurrentAnimation == -1) {
            try {
                this.mStartDragX = (int) motionEvent.getX();
                this.mStartDragY = (int) motionEvent.getY();
                this.mCurrentAction = 0;
                this.mTouchedInSmallWindowBounds = false;
                this.mSmallWindowBounds.set(this.mListener.getSmallFreeFormWindowBounds());
                if (this.mSmallWindowBounds.contains(this.mStartDragX, this.mStartDragY)) {
                    Slog.d(TAG, "notifyDownLocked");
                    this.mTouchedInSmallWindowBounds = true;
                    VelocityTracker velocityTracker = this.mVelocityTracker;
                    if (velocityTracker == null) {
                        this.mVelocityTracker = VelocityTracker.obtain();
                    } else {
                        velocityTracker.clear();
                    }
                    this.mVelocityTracker.addMovement(motionEvent);
                }
            } catch (Exception e) {
            }
        }
    }

    public boolean notifyMoveLocked(MotionEvent motionEvent) {
        try {
            float x = motionEvent.getX();
            float y = motionEvent.getY();
            if (!this.mTouchedInSmallWindowBounds || !this.mListener.mGestureDetector.passedSlop(x, y, (float) this.mStartDragX, (float) this.mStartDragY)) {
                return false;
            }
            this.mCurrentAction = 2;
            if (this.mVelocityTracker == null) {
                this.mVelocityTracker = VelocityTracker.obtain();
            }
            this.mVelocityTracker.addMovement(motionEvent);
            if (MiuiFreeFormGestureController.DEBUG) {
                Slog.d(TAG, "notifyMoveLocked");
            }
            this.mListener.mTaskPositioner.updateSmallWindowMoveBounds((float) ((int) x), (float) ((int) y), (float) this.mStartDragX, (float) this.mStartDragY, this.mListener.mIsPortrait, this.mSmallWindowBounds);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean notifyUpLocked(MotionEvent motionEvent) {
        Slog.d(TAG, "notifyUpLocked ");
        VelocityTracker velocityTracker = this.mVelocityTracker;
        if (velocityTracker != null) {
            try {
                velocityTracker.addMovement(motionEvent);
                this.mVelocityTracker.computeCurrentVelocity(1000);
                this.mXVelocity = this.mVelocityTracker.getXVelocity();
                this.mYVelocity = this.mVelocityTracker.getYVelocity();
                this.mVelocityTracker.recycle();
                this.mVelocityTracker = null;
            } catch (Exception e) {
            }
        }
        if (!this.mTouchedInSmallWindowBounds || this.mCurrentAction != 2) {
            this.mTouchedInSmallWindowBounds = false;
            return false;
        }
        try {
            applyShowSmallWindowCornerAnimal((int) motionEvent.getX(), (int) motionEvent.getY());
            return true;
        } catch (Exception e2) {
            return false;
        }
    }

    public void applyShowSmallWindowCornerAnimal(int x, int y) {
        Rect smallWindowBounds = this.mListener.getSmallFreeFormWindowBounds();
        int smallWindowCenterX = smallWindowBounds.left + ((int) (MiuiMultiWindowUtils.SMALL_FREEFORM_WIDTH / 2.0f));
        int smallWindowCenterY = smallWindowBounds.top + ((int) (MiuiMultiWindowUtils.SMALL_FREEFORM_HEIGHT / 2.0f));
        Rect cornerPoint = MiuiMultiWindowUtils.findNearestCorner(this.mListener.mService.mContext, (float) smallWindowCenterX, (float) smallWindowCenterY, -1, this.mXVelocity, this.mYVelocity, this.mListener.mIsLandcapeFreeform);
        int slideOutVelocityThresholdPx = (int) (this.mListener.mService.mContext.getResources().getDisplayMetrics().density * ((float) this.SLIDE_OUT_VELOCITY_THRESHOLD_DP));
        if (this.mCurrentAction == 2 && Math.abs(this.mYVelocity) > Math.abs(this.mXVelocity) && this.mYVelocity < 0.0f && y < MiuiMultiWindowUtils.SMALL_FREEFORM_PORTRAIT_VERTICAL_MARGIN && this.mYVelocity < ((float) slideOutVelocityThresholdPx)) {
            cornerPoint.set(cornerPoint.left, (int) (-MiuiMultiWindowUtils.SMALL_FREEFORM_HEIGHT), cornerPoint.right, (int) ((-MiuiMultiWindowUtils.SMALL_FREEFORM_HEIGHT) + ((float) cornerPoint.height())));
            Slog.d(TAG, " Modify to exit cornerPoint:" + cornerPoint);
        }
        Slog.d(TAG, "smallWindowCenterX:" + smallWindowCenterX + " smallWindowCenterY:" + smallWindowCenterY + " smallWindowBounds:" + smallWindowBounds + " cornerPoint:" + cornerPoint);
        if (!cornerPoint.equals(smallWindowBounds)) {
            this.mListener.mTaskPositioner.updateSmallWindowUpBounds((float) x, (float) y, (float) this.mStartDragX, (float) this.mStartDragY, this.mListener.mIsPortrait, smallWindowBounds, cornerPoint);
        }
        this.mTouchedInSmallWindowBounds = false;
        this.mCurrentAction = -1;
        this.mYVelocity = -1.0f;
        this.mXVelocity = -1.0f;
    }

    public void startShowFullScreenWindow() {
        if (this.mListener.mFreeFormWindowMotionHelper.mCurrentAnimation == -1 && !this.mListener.mFreeFormWindowMotionHelper.mIsAnimating) {
            this.mTouchedInSmallWindowBounds = false;
            this.mListener.mFreeFormWindowMotionHelper.startGestureAnimation(6);
        }
    }

    /* access modifiers changed from: private */
    public void startShowFreeFormWindow() {
        if (this.mListener.mFreeFormWindowMotionHelper.mCurrentAnimation == -1 && !this.mListener.mFreeFormWindowMotionHelper.mIsAnimating) {
            this.mTouchedInSmallWindowBounds = false;
            MiuiFreeFormGesturePointerEventListener miuiFreeFormGesturePointerEventListener = this.mListener;
            MiuiFreeFormGesturePointerEventListener.mCurrentWindowMode = 0;
            miuiFreeFormGesturePointerEventListener.showCaptionView();
            this.mListener.mFreeFormWindowMotionHelper.startGestureAnimation(5);
        }
    }
}
