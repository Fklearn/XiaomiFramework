package com.android.server.wm;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.os.SystemClock;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.MiuiMultiWindowUtils;
import android.util.Slog;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceControl;
import android.view.VelocityTracker;
import android.view.ViewConfiguration;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.ScaleAnimation;
import com.android.server.wm.MiuiFreeFormDynamicAnimation;
import com.android.server.wm.MiuiFreeFormGestureAnimator;
import com.android.server.wm.MiuiFreeFormGesturePointerEventListener;
import com.android.server.wm.MiuiFreeFormWindowMotionHelper;
import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class MiuiFreeFormWindowMotionHelper {
    private static final String ALPHA = "ALPHA";
    public static final int ANIMATION_ALPHA = 13;
    public static final int ANIMATION_CLOSE = 0;
    public static final int ANIMATION_DIRECT_OPEN = 17;
    public static final int ANIMATION_ENLARGE_FOR_INPUTMETHOD = 15;
    public static final int ANIMATION_FREEFORM_WINDOW_TRANSLATE = 8;
    public static final int ANIMATION_FROM_SMALL_TO_FREEFORM_WINDOW = 5;
    public static final int ANIMATION_FROM_SMALL_TO_FULL_WINDOW = 6;
    public static final int ANIMATION_LAUNCH_SMALL_FREEFORM_WINDOW = 12;
    public static final int ANIMATION_NARROW_FOR_INPUTMETHOD = 16;
    public static final int ANIMATION_OPEN = 1;
    public static final int ANIMATION_RESET = 2;
    public static final int ANIMATION_RESIZE_BACK_LEFT_BOTTOM = 10;
    public static final int ANIMATION_RESIZE_BACK_RIGHT_BOTTOM = 11;
    public static final int ANIMATION_SCALE_HOME = 14;
    public static final int ANIMATION_SHOW_FREEFORM_WINDOW = 4;
    public static final int ANIMATION_SHOW_SMALL_WINDOW = 3;
    public static final int ANIMATION_SMALL_TO_CORNER = 9;
    public static final int ANIMATION_SMALL_WINDOW_TRANSLATE = 7;
    public static final int ANIMATION_UNDEFINED = -1;
    private static final float CLOSE_OPERATION_THRESHOLD = 300.0f;
    private static final int CYCLE_WAITING_MS = 50;
    private static final int GESTURE_ACTION_DOWN = 0;
    public static final int GESTURE_ACTION_UNDEFINED = -1;
    private static final int GESTURE_ACTION_UP = 1;
    private static final int MAX_WAITING_RESIZE_COMPLE_TIME = 100;
    private static final float OPEN_OPERATION_THRESHOLD = 300.0f;
    private static final String POSITION_BOTTOM = "BOTTOM";
    private static final String POSITION_LEFT = "LEFT";
    private static final String POSITION_RIGHT = "RIGHT";
    private static final String POSITION_TOP = "TOP";
    private static final int RESIZE_BACK_TO_MAXSIZE = 0;
    private static final int RESIZE_BACK_TO_MINSIZE = 1;
    private static final int RESIZE_BACK_UNDEFINED = -1;
    private static final String SCALE = "SCALE";
    private static final String SCALE_HEIGTH = "HEIGHT_SCALE";
    private static final String SCALE_WIDTH = "WIDTH_SCALE";
    private static final String TAG = "MiuiFreeFormWindowMotionHelper";
    private static final int TOUCH_BOTTOM = 1;
    private static final int TOUCH_RESIZE_LEFT = 2;
    private static final int TOUCH_RESIZE_RIGHT = 3;
    private static final int TOUCH_TOP = 0;
    private static final int TOUCH_UNDEFINED = -1;
    private static final String TRANSLATE_X = "TRANSLATE_X";
    private static final String TRANSLATE_Y = "TRANSLATE_Y";
    private static final float WINDOW_NEED_OFFSETY = 200.0f;
    private final int SCREEN_FREEZE_LAYER_BASE = 2010000;
    private final int SCREEN_FREEZE_LAYER_SCREENSHOT = 2010001;
    private AnimalFinishedActionsRunnable mAnimalFinishedActionsRunnable;
    final ConcurrentHashMap<MiuiFreeFormDynamicAnimation, MiuiFreeFormGestureAnimator.AnimalLock> mAnimalLocks = new ConcurrentHashMap<>();
    /* access modifiers changed from: private */
    public long mAnimationStartTime;
    private Rect mBottomGestureDetectBounds = new Rect();
    private int mCurrentAction;
    private volatile float mCurrentAlpha;
    public int mCurrentAnimation = -1;
    public volatile float mCurrentCenterPositionX;
    public volatile float mCurrentCenterPositionY;
    private int mCurrentGestureAction = -1;
    private volatile float mCurrentScale;
    private volatile float mCurrentTempX;
    private volatile float mCurrentTempY;
    private int mCurrentTouchedMode;
    private float mCurrentX;
    private volatile float mCurrentXScale;
    private float mCurrentY;
    private volatile float mCurrentYScale;
    private final Interpolator mDecelerateInterpolator = new DecelerateInterpolator(1.5f);
    private SurfaceControl mDimmerLayer;
    private int mDownNumbers = 0;
    private volatile float mDownX;
    private volatile float mDownY;
    public boolean mEnteredHotArea = false;
    private long mFirstDownTime = 0;
    private AnimatorSet mGestureAnimatorSet = new AnimatorSet();
    private boolean mHadBeenCenterWindow = false;
    private boolean mHadChangedForInputMethod = false;
    private boolean mHadVibrator = false;
    public volatile int mHotSpotNum = -1;
    private float mHotSpotTouchX;
    private float mHotSpotTouchY;
    private Rect mHotSpotWindowBounds = new Rect();
    private boolean mInResizeMaxSize;
    private boolean mInResizeMinSize;
    public boolean mIsAnimating = false;
    private AtomicInteger mLandDropAnimation = new AtomicInteger(-1);
    public Rect mLastFreeFormWindowStartBounds = new Rect();
    private volatile float mLastMoveX;
    private volatile float mLastMoveY;
    private Rect mLeftResizeGestureDetectBounds = new Rect();
    /* access modifiers changed from: private */
    public MiuiFreeFormGesturePointerEventListener mListener;
    private int mOffSetYForInputMethod;
    private Rect mOriginalBounds = new Rect();
    private boolean mOutFreeFormTouchRegion = false;
    public volatile float mPosX;
    public volatile float mPosY;
    private MiuiFreeFormTaskPositioner mPositioner;
    private int mResizeBackMode = -1;
    private float mResizeCurrentScale;
    private int mResizeLastMoveBottom = -1;
    private int mResizeLastMoveLeft = -1;
    private int mResizeLastMoveRight = -1;
    private int mResizeLastMoveTop = -1;
    private float mResizeLeft = -1.0f;
    private float mResizeLeftPosition;
    private float mResizeMaxX;
    private float mResizeMinX;
    private float mResizeScale;
    private long mResizeStartTime;
    private float mResizeTop = -1.0f;
    private float mResizeTopPosition;
    private Rect mRightResizeGestureDetectBounds = new Rect();
    /* access modifiers changed from: private */
    public Animation mScaleAnimator;
    private boolean mShowedHotSpotView = false;
    private Rect mSmallWindowBounds = new Rect();
    public float mSmallWindowTargetHScale;
    public float mSmallWindowTargetWScale;
    /* access modifiers changed from: private */
    public SurfaceControl mSurfaceControl;
    private Rect mTaskBounds = new Rect();
    private WindowState mTopFullScreenWindow;
    private Rect mTopGestureDetectBounds = new Rect();
    private VelocityTracker mVelocityTracker;
    public float mWindowHeight;
    final ConcurrentHashMap<WindowState, MiuiFreeFormGestureAnimator.AnimalLock> mWindowLocks = new ConcurrentHashMap<>();
    private final Rect mWindowOriginalBounds = new Rect();
    public float mWindowWidth;

    public MiuiFreeFormWindowMotionHelper(MiuiFreeFormGesturePointerEventListener listener) {
        this.mListener = listener;
    }

    public boolean notifyDownLocked(MotionEvent motionEvent) {
        if (this.mListener.mFreeFormAppWindows.isEmpty() || this.mCurrentAnimation != -1) {
            return false;
        }
        try {
            this.mDownX = motionEvent.getX();
            this.mDownY = motionEvent.getY();
            DisplayContent displayContent = this.mListener.mGestureController.mDisplayContent;
            this.mListener.updateScreenParams(displayContent, displayContent.getConfiguration());
            this.mCurrentAction = 0;
            this.mWindowHeight = ((float) this.mListener.mWindowBounds.height()) * MiuiMultiWindowUtils.sScale;
            this.mWindowWidth = ((float) this.mListener.mWindowBounds.width()) * MiuiMultiWindowUtils.sScale;
            initGestureDetecteSpace();
            if (this.mListener.mStatusBarWindow == null || this.mListener.mStatusBarWindow.mWindowFrames.mFrame.height() != this.mListener.mScreenHeight || !this.mTopGestureDetectBounds.contains((int) this.mDownX, (int) this.mDownY)) {
                this.mAnimalLocks.clear();
                this.mWindowOriginalBounds.set(this.mListener.mWindowBounds);
                this.mLastFreeFormWindowStartBounds.set(this.mListener.mWindowBounds);
                this.mListener.mTaskPositioner.getDragWindowBounds().setEmpty();
                this.mCurrentAnimation = -1;
                this.mResizeBackMode = -1;
                this.mCurrentGestureAction = -1;
                this.mEnteredHotArea = false;
                if (this.mTopGestureDetectBounds.contains((int) this.mDownX, (int) this.mDownY) || this.mBottomGestureDetectBounds.contains((int) this.mDownX, (int) this.mDownY)) {
                    this.mDownNumbers++;
                    if (this.mDownNumbers == 1) {
                        this.mFirstDownTime = motionEvent.getDownTime();
                    }
                    if (this.mDownNumbers == 2 && motionEvent.getDownTime() - this.mFirstDownTime > 1500) {
                        this.mDownNumbers = 1;
                        this.mFirstDownTime = motionEvent.getDownTime();
                    }
                    if (this.mDownNumbers == 3) {
                        long during = motionEvent.getDownTime() - this.mFirstDownTime;
                        if (during < 1500) {
                            MiuiFreeFormGesturePointerEventListener miuiFreeFormGesturePointerEventListener = this.mListener;
                            miuiFreeFormGesturePointerEventListener.showTipWindow(1, miuiFreeFormGesturePointerEventListener.mWindowBounds);
                            Slog.d(TAG, "notifyDownLocked showTipWindow during=" + during);
                        }
                        this.mDownNumbers = 0;
                        this.mFirstDownTime = 0;
                    }
                }
                this.mCurrentX = this.mDownX;
                this.mCurrentY = this.mDownY;
                this.mLastMoveX = this.mDownX;
                this.mLastMoveY = this.mDownY;
                this.mOutFreeFormTouchRegion = false;
                this.mHadVibrator = false;
                if (this.mTopGestureDetectBounds.contains((int) this.mDownX, (int) this.mDownY)) {
                    for (int i = this.mListener.mFreeFormAppWindows.size() - 1; i >= 0; i--) {
                        WindowState win = this.mListener.mFreeFormAppWindows.get(i);
                        win.mAttrs.privateFlags |= 64;
                        this.mListener.mGestureAnimator.recreateLeashIfNeeded(win.mAppToken);
                    }
                    this.mCurrentTouchedMode = 0;
                } else if (this.mBottomGestureDetectBounds.contains((int) this.mDownX, (int) this.mDownY)) {
                    for (int i2 = this.mListener.mFreeFormAppWindows.size() - 1; i2 >= 0; i2--) {
                        WindowState win2 = this.mListener.mFreeFormAppWindows.get(i2);
                        win2.mAttrs.privateFlags |= 64;
                        this.mListener.mGestureAnimator.recreateLeashIfNeeded(win2.mAppToken);
                    }
                    this.mCurrentTouchedMode = 1;
                    if (!this.mListener.mIsPortrait || (this.mListener.mIsPortrait && this.mListener.mIsLandcapeFreeform)) {
                        VelocityTracker velocityTracker = this.mVelocityTracker;
                        if (velocityTracker == null) {
                            this.mVelocityTracker = VelocityTracker.obtain();
                        } else {
                            velocityTracker.clear();
                        }
                        this.mVelocityTracker.addMovement(motionEvent);
                    }
                } else if (this.mLeftResizeGestureDetectBounds.contains((int) this.mDownX, (int) this.mDownY)) {
                    this.mListener.mGestureController.notifyFreeFormApplicationResizeStart();
                    this.mResizeStartTime = SystemClock.uptimeMillis();
                    for (int i3 = this.mListener.mFreeFormAppWindows.size() - 1; i3 >= 0; i3--) {
                        WindowState win3 = this.mListener.mFreeFormAppWindows.get(i3);
                        win3.mAttrs.privateFlags |= 64;
                        this.mListener.mGestureAnimator.recreateLeashIfNeeded(win3.mAppToken);
                    }
                    this.mResizeLeftPosition = (float) this.mListener.mWindowBounds.left;
                    this.mResizeTopPosition = (float) this.mListener.mWindowBounds.top;
                    this.mResizeLastMoveLeft = this.mListener.mWindowBounds.left;
                    this.mResizeLastMoveTop = this.mListener.mWindowBounds.top;
                    this.mResizeLastMoveRight = this.mResizeLastMoveLeft + ((int) this.mWindowWidth);
                    this.mResizeLastMoveBottom = this.mResizeLastMoveTop + ((int) this.mWindowHeight);
                    this.mInResizeMinSize = false;
                    this.mInResizeMaxSize = false;
                    this.mResizeScale = MiuiMultiWindowUtils.sScale;
                    this.mResizeLeft = (float) this.mResizeLastMoveLeft;
                    this.mResizeTop = (float) this.mResizeLastMoveTop;
                    this.mCurrentTouchedMode = 2;
                    Slog.d(TAG, "mCurrentTouchedMode = TOUCH_RESIZE_LEFT");
                } else if (this.mRightResizeGestureDetectBounds.contains((int) this.mDownX, (int) this.mDownY)) {
                    this.mListener.mGestureController.notifyFreeFormApplicationResizeStart();
                    this.mResizeStartTime = SystemClock.uptimeMillis();
                    for (int i4 = this.mListener.mFreeFormAppWindows.size() - 1; i4 >= 0; i4--) {
                        WindowState win4 = this.mListener.mFreeFormAppWindows.get(i4);
                        win4.mAttrs.privateFlags |= 64;
                        this.mListener.mGestureAnimator.recreateLeashIfNeeded(win4.mAppToken);
                    }
                    this.mResizeLastMoveLeft = this.mListener.mWindowBounds.left;
                    this.mResizeLastMoveTop = this.mListener.mWindowBounds.top;
                    this.mResizeLastMoveRight = this.mResizeLastMoveLeft + ((int) this.mWindowWidth);
                    this.mResizeLastMoveBottom = this.mResizeLastMoveTop + ((int) this.mWindowHeight);
                    this.mInResizeMinSize = false;
                    this.mInResizeMaxSize = false;
                    this.mResizeScale = MiuiMultiWindowUtils.sScale;
                    this.mResizeLeft = (float) this.mResizeLastMoveLeft;
                    this.mResizeTop = (float) this.mResizeLastMoveTop;
                    this.mCurrentTouchedMode = 3;
                    Slog.d(TAG, "mCurrentTouchedMode = TOUCH_RESIZE_RIGHT");
                } else {
                    this.mCurrentTouchedMode = -1;
                    this.mOutFreeFormTouchRegion = true;
                    Slog.d(TAG, "mOutFreeFormTouchRegion:" + this.mOutFreeFormTouchRegion + " mDownX:" + this.mDownX + " mDownY:" + this.mDownY);
                }
                return true;
            }
            Slog.d(TAG, "statusBarWindow is fullScreen and top Gesture is prohibited");
            this.mOutFreeFormTouchRegion = true;
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean notifyMoveLocked(MotionEvent motionEvent) {
        float offsetY;
        float offsetY2;
        int i;
        if (MiuiFreeFormGestureController.DEBUG) {
            Slog.d(TAG, "notifyMoveLocked mOutFreeFormTouchRegion:" + this.mOutFreeFormTouchRegion);
        }
        if (this.mListener.mFreeFormAppWindows.isEmpty()) {
            MotionEvent motionEvent2 = motionEvent;
            return false;
        } else if (this.mOutFreeFormTouchRegion) {
            MotionEvent motionEvent3 = motionEvent;
            return false;
        } else {
            this.mCurrentAction = 2;
            try {
                float offsetY3 = motionEvent.getY() - this.mDownY;
                float offsetX = motionEvent.getX() - this.mDownX;
                this.mCurrentX = motionEvent.getX();
                this.mCurrentY = motionEvent.getY();
                float absSetY = Math.abs(offsetY3);
                float absSetX = Math.abs(offsetX);
                int i2 = this.mCurrentTouchedMode;
                if (i2 == 0) {
                    if (this.mListener.mGestureDetector.passedSlop(this.mCurrentX, this.mCurrentY, this.mDownX, this.mDownY)) {
                        if (MiuiFreeFormGestureController.DEBUG) {
                            Slog.d(TAG, "notifyMoveLocked");
                        }
                        int isEnterHotArea = MiuiMultiWindowUtils.isEnterHotArea(this.mListener.mService.mContext, this.mCurrentX, this.mCurrentY, true);
                        this.mHotSpotNum = isEnterHotArea;
                        if (isEnterHotArea != -1) {
                            this.mListener.mGestureController.mWindowController.inHotSpotArea(this.mHotSpotNum, this.mCurrentX, this.mCurrentY);
                            if (!this.mShowedHotSpotView) {
                                this.mListener.mGestureController.mWindowController.showHotSpotView();
                                this.mShowedHotSpotView = true;
                            }
                        } else if (this.mShowedHotSpotView) {
                            this.mListener.mGestureController.mWindowController.hideHotSpotView();
                            this.mShowedHotSpotView = false;
                        }
                        int isEnterHotArea2 = MiuiMultiWindowUtils.isEnterHotArea(this.mListener.mService.mContext, this.mCurrentX, this.mCurrentY);
                        this.mHotSpotNum = isEnterHotArea2;
                        if (isEnterHotArea2 != -1) {
                            if (MiuiFreeFormGestureController.DEBUG) {
                                Slog.d(TAG, "mEnteredHotArea:" + this.mEnteredHotArea + " mHotSpotNum:" + this.mHotSpotNum);
                            }
                            if (!this.mEnteredHotArea) {
                                this.mListener.hideInputMethodWindowIfNeeded();
                                this.mHotSpotTouchX = this.mCurrentX;
                                this.mHotSpotTouchY = this.mCurrentY;
                                this.mListener.mGestureController.mWindowController.enterSmallWindow();
                                for (AppWindowToken aToken : this.mListener.mFreeFormAppTokens) {
                                    aToken.mIgnoreInput = true;
                                }
                                this.mEnteredHotArea = true;
                                this.mHotSpotWindowBounds.set((int) (this.mCurrentX - ((float) (this.mWindowOriginalBounds.width() / 2))), (int) this.mCurrentY, (int) (this.mCurrentX + ((float) (this.mWindowOriginalBounds.width() / 2))), (int) (this.mCurrentY + ((float) this.mWindowOriginalBounds.height())));
                                ((Vibrator) this.mListener.mService.mContext.getSystemService("vibrator")).vibrate(VibrationEffect.get(0));
                                this.mListener.hideCaptionView();
                            }
                        } else {
                            if (MiuiFreeFormGestureController.DEBUG) {
                                Slog.d(TAG, "mEnteredHotArea:" + this.mEnteredHotArea);
                            }
                            if (this.mEnteredHotArea) {
                                this.mListener.mGestureController.mWindowController.outSmallWindow();
                                this.mListener.showCaptionView();
                                this.mEnteredHotArea = false;
                                for (AppWindowToken aToken2 : this.mListener.mFreeFormAppTokens) {
                                    aToken2.mIgnoreInput = false;
                                }
                            }
                        }
                        if (this.mListener.mTopFreeFormAppToken != null) {
                            Task task = this.mListener.mTopFreeFormAppToken.getTask();
                            if (task != null) {
                                task.getBounds(this.mTaskBounds);
                            } else {
                                this.mListener.mTopFreeFormAppToken.getBounds(this.mTaskBounds);
                            }
                        }
                        this.mDownX = ((float) this.mTaskBounds.left) + (this.mWindowWidth / 2.0f);
                        this.mListener.mTaskPositioner.updateWindowMoveBounds(this.mCurrentX, this.mCurrentY, this.mDownX, this.mDownY, this.mListener.mIsPortrait, this.mWindowOriginalBounds);
                        return true;
                    }
                    MotionEvent motionEvent4 = motionEvent;
                    offsetY = offsetY3;
                    float f = offsetX;
                } else if (i2 != 1) {
                    MotionEvent motionEvent5 = motionEvent;
                    offsetY = offsetY3;
                    float f2 = offsetX;
                    if (i2 == 2) {
                        float dx = this.mCurrentX - this.mLastMoveX;
                        float dy = this.mCurrentY - this.mLastMoveY;
                        if (MiuiFreeFormGestureController.DEBUG) {
                            Slog.i(TAG, "Move mCurrentTouchedMode == TOUCH_RESIZE_LEFT　dx: " + dx + "dy: " + dy + " dx * 1.0f / dy:" + ((dx * 1.0f) / dy) + " mListener.mWidthHeightScale:" + this.mListener.mWidthHeightScale);
                        }
                        if (dy == 0.0f || (dx * 1.0f) / dy < 0.0f) {
                            if (dy == 0.0f || Math.abs((1.0f * dx) / dy) > this.mListener.mWidthHeightScale) {
                                leftBottom(dx, this.mCurrentX, this.mCurrentY);
                            } else {
                                bottomLeft(dy, this.mCurrentX, this.mCurrentY);
                            }
                        }
                    } else if (i2 == 3) {
                        float dx2 = this.mCurrentX - this.mLastMoveX;
                        float dy2 = this.mCurrentY - this.mLastMoveY;
                        if (MiuiFreeFormGestureController.DEBUG) {
                            Slog.i(TAG, "Move mCurrentTouchedMode == TOUCH_RESIZE_RIGHT　dx: " + dx2 + " dy: " + dy2 + " dx * 1.0f / dy:" + ((dx2 * 1.0f) / dy2) + " mListener.mWidthHeightScale:" + this.mListener.mWidthHeightScale);
                        }
                        if (dy2 == 0.0f || (dx2 * 1.0f) / dy2 > 0.0f) {
                            if (dy2 == 0.0f || Math.abs((1.0f * dx2) / dy2) > this.mListener.mWidthHeightScale) {
                                rightBottom(dx2, this.mCurrentX, this.mCurrentY);
                            } else {
                                bottomRight(dy2, this.mCurrentX, this.mCurrentY);
                            }
                        }
                    }
                } else if (absSetY <= absSetX || absSetY <= ((float) ViewConfiguration.getTouchSlop())) {
                    MotionEvent motionEvent6 = motionEvent;
                    offsetY = offsetY3;
                    float f3 = offsetX;
                } else {
                    float windowCenterWith = ((float) this.mListener.mWindowBounds.left) + (this.mWindowWidth / 2.0f);
                    float xVelocity = 0.0f;
                    float yVelocity = 0.0f;
                    VelocityTracker velocityTracker = this.mVelocityTracker;
                    if (velocityTracker != null) {
                        velocityTracker.addMovement(motionEvent);
                        this.mVelocityTracker.computeCurrentVelocity(1000);
                        xVelocity = this.mVelocityTracker.getXVelocity();
                        yVelocity = this.mVelocityTracker.getYVelocity();
                    } else {
                        MotionEvent motionEvent7 = motionEvent;
                    }
                    if (offsetY3 <= 0.0f) {
                        if (offsetY3 < 0.0f) {
                            Slog.w(TAG, "GESTURE_ACTION_UP");
                            if (this.mLandDropAnimation.get() == 2) {
                                offsetY2 = offsetY3;
                                this.mListener.mGestureController.mWindowController.setStartBounds(new Rect((int) this.mPosX, (int) this.mPosY, (int) (this.mPosX + (this.mWindowWidth * this.mCurrentScale)), (int) (this.mPosY + (this.mWindowHeight * this.mCurrentScale))));
                                this.mListener.mGestureController.mWindowController.startBorderAnimation(false);
                                this.mLandDropAnimation.set(-1);
                            } else {
                                offsetY2 = offsetY3;
                            }
                            this.mCurrentCenterPositionX = ((float) this.mListener.mWindowBounds.left) + (this.mWindowWidth / 2.0f);
                            this.mCurrentCenterPositionY = ((float) this.mListener.mWindowBounds.top) + (this.mWindowHeight / 2.0f);
                            this.mCurrentGestureAction = 1;
                            if (!this.mHadVibrator && absSetY > 300.0f) {
                                this.mHadVibrator = true;
                                ((Vibrator) this.mListener.mService.mContext.getSystemService("vibrator")).vibrate(VibrationEffect.get(0));
                            } else if (absSetY <= 300.0f) {
                                this.mHadVibrator = false;
                            }
                            dealWithWindowGestureAnimal(this.mCurrentX, this.mCurrentY, 1);
                        } else {
                            offsetY2 = offsetY3;
                        }
                    } else if (!this.mListener.mIsPortrait) {
                        if (MiuiFreeFormGestureController.DEBUG) {
                            Slog.d(TAG, "notifyMoveLocked xVelocity:" + xVelocity + " yVelocity:" + yVelocity + " absSetY:" + absSetY + " mLandDropAnimation:" + this.mLandDropAnimation);
                        }
                        this.mCurrentCenterPositionX = ((float) this.mListener.mWindowBounds.left) + (this.mWindowWidth / 2.0f);
                        this.mCurrentCenterPositionY = ((float) this.mListener.mWindowBounds.top) + (this.mWindowHeight / 2.0f);
                        this.mCurrentGestureAction = 0;
                        dealWithWindowGestureAnimal(this.mCurrentX, this.mCurrentY, 0);
                        if (yVelocity >= 400.0f || absSetY < 25.0f || this.mLandDropAnimation.get() != -1) {
                            if (absSetY >= 25.0f) {
                                offsetY2 = offsetY3;
                            } else if (this.mLandDropAnimation.get() == 2) {
                                this.mListener.mGestureController.mWindowController.setStartBounds(new Rect((int) this.mPosX, (int) this.mPosY, (int) (this.mPosX + (this.mWindowWidth * this.mCurrentScale)), (int) (this.mPosY + (this.mWindowHeight * this.mCurrentScale))));
                                this.mListener.mGestureController.mWindowController.startBorderAnimation(false);
                                this.mLandDropAnimation.set(-1);
                                offsetY2 = offsetY3;
                            } else {
                                offsetY2 = offsetY3;
                            }
                        } else {
                            this.mLandDropAnimation.set(2);
                            float f4 = offsetX;
                            this.mListener.mGestureController.mWindowController.setStartBounds(new Rect((int) this.mPosX, (int) this.mPosY, (int) (this.mPosX + (this.mWindowWidth * this.mCurrentScale)), (int) (this.mPosY + (this.mWindowHeight * this.mCurrentScale))));
                            this.mListener.mGestureController.mWindowController.startBorderAnimation(true);
                            offsetY2 = offsetY3;
                        }
                    } else {
                        if (offsetY3 > 200.0f) {
                            offsetY3 = 200.0f;
                        }
                        if (offsetY3 <= 200.0f) {
                            this.mHadBeenCenterWindow = false;
                            if (windowCenterWith > ((float) (this.mListener.mScreenWidth / 2))) {
                                this.mCurrentCenterPositionY = ((float) this.mListener.mWindowBounds.top) + (this.mWindowHeight / 2.0f) + offsetY3;
                                this.mCurrentCenterPositionX = windowCenterWith - ((offsetY3 / 200.0f) * (windowCenterWith - ((float) (this.mListener.mScreenWidth / 2))));
                            } else if (windowCenterWith < ((float) (this.mListener.mScreenWidth / 2))) {
                                this.mCurrentCenterPositionY = ((float) this.mListener.mWindowBounds.top) + (this.mWindowHeight / 2.0f) + offsetY3;
                                this.mCurrentCenterPositionX = ((offsetY3 / 200.0f) * (((float) (this.mListener.mScreenWidth / 2)) - windowCenterWith)) + windowCenterWith;
                            } else if (windowCenterWith == ((float) (this.mListener.mScreenWidth / 2))) {
                                this.mCurrentCenterPositionY = ((float) this.mListener.mWindowBounds.top) + (this.mWindowHeight / 2.0f);
                                this.mCurrentCenterPositionX = windowCenterWith;
                            }
                            if (offsetY3 == 200.0f) {
                                this.mHadBeenCenterWindow = true;
                            }
                        }
                        if (MiuiFreeFormGestureController.DEBUG) {
                            Slog.w(TAG, "GESTURE_ACTION_DOWN top:" + this.mListener.mWindowBounds.top + " left:" + this.mListener.mWindowBounds.left + " currentCenterPositionY:" + this.mCurrentCenterPositionY + " currentCenterPositionX:" + this.mCurrentCenterPositionX);
                        }
                        this.mCurrentGestureAction = 0;
                        if (!this.mHadVibrator && absSetY > 300.0f) {
                            this.mHadVibrator = true;
                            ((Vibrator) this.mListener.mService.mContext.getSystemService("vibrator")).vibrate(VibrationEffect.get(0));
                            i = 0;
                        } else if (absSetY <= 300.0f) {
                            i = 0;
                            this.mHadVibrator = false;
                        } else {
                            i = 0;
                        }
                        dealWithWindowGestureAnimal(this.mCurrentX, this.mCurrentY, i);
                        if (this.mListener.mIsPortrait && this.mListener.mIsLandcapeFreeform) {
                            if (yVelocity < 400.0f && absSetY >= 100.0f && this.mLandDropAnimation.get() == -1) {
                                this.mLandDropAnimation.set(2);
                                this.mListener.mGestureController.mWindowController.setStartBounds(new Rect((int) this.mPosX, (int) this.mPosY, (int) (this.mPosX + (this.mWindowWidth * this.mCurrentScale)), (int) (this.mPosY + (this.mWindowHeight * this.mCurrentScale))));
                                this.mListener.mGestureController.mWindowController.startBorderAnimation(true);
                            } else if (absSetY < 100.0f && this.mLandDropAnimation.get() == 2) {
                                this.mListener.mGestureController.mWindowController.setStartBounds(new Rect((int) this.mPosX, (int) this.mPosY, (int) (this.mPosX + (this.mWindowWidth * this.mCurrentScale)), (int) (this.mPosY + (this.mWindowHeight * this.mCurrentScale))));
                                this.mListener.mGestureController.mWindowController.startBorderAnimation(false);
                                this.mLandDropAnimation.set(-1);
                            }
                            if (this.mLandDropAnimation.get() == 2) {
                                this.mListener.mGestureController.mWindowController.setStartBounds(new Rect((int) this.mPosX, (int) this.mPosY, (int) (this.mPosX + (this.mWindowWidth * this.mCurrentScale)), (int) (this.mPosY + (this.mWindowHeight * this.mCurrentScale))));
                            }
                        }
                        this.mLastMoveX = this.mCurrentX;
                        this.mLastMoveY = this.mCurrentY;
                        return true;
                    }
                    this.mLastMoveX = this.mCurrentX;
                    this.mLastMoveY = this.mCurrentY;
                    return true;
                }
                this.mLastMoveX = this.mCurrentX;
                this.mLastMoveY = this.mCurrentY;
                return true;
            } catch (Exception e) {
                MotionEvent motionEvent8 = motionEvent;
                return false;
            }
        }
    }

    private void leftBottom(float dx, float x, float y) {
        int oriLeft = this.mResizeLastMoveLeft;
        int oriTop = this.mResizeLastMoveTop;
        int oriRight = this.mResizeLastMoveRight;
        int i = this.mResizeLastMoveBottom;
        int bottomMargin = (this.mListener.mScreenHeight - MiuiMultiWindowUtils.FREEFORM_LANDCAPE_HEIGHT) / 2;
        int oriLeft2 = (int) (((float) oriLeft) + dx);
        int oriBottom = ((int) ((((float) (oriRight - oriLeft2)) / this.mListener.mWidthHeightScale) + 0.5f)) + oriTop;
        if (oriLeft2 < this.mListener.mNotchBar) {
            oriLeft2 = this.mListener.mNotchBar;
            oriBottom = oriTop + ((int) ((((float) (oriRight - oriLeft2)) / this.mListener.mWidthHeightScale) + 0.5f));
        } else if (!this.mListener.mIsLandcapeFreeform && oriBottom > this.mListener.mScreenHeight) {
            oriBottom = this.mListener.mScreenHeight;
            oriLeft2 = oriRight - ((int) ((((float) (oriBottom - oriTop)) * this.mListener.mWidthHeightScale) + 0.5f));
        } else if (this.mListener.mIsLandcapeFreeform && oriBottom > this.mListener.mScreenHeight - bottomMargin) {
            oriBottom = this.mListener.mScreenHeight - bottomMargin;
            oriLeft2 = oriRight - ((int) ((((float) (oriBottom - oriTop)) * this.mListener.mWidthHeightScale) + 0.5f));
        }
        if (oriRight - oriLeft2 < this.mListener.mMinWidthSize) {
            if (!this.mInResizeMinSize) {
                this.mInResizeMinSize = true;
                this.mResizeMinX = x;
            }
            int adjustedWidth = this.mListener.mMinWidthSize - ((int) afterFriction(Math.abs(x - this.mResizeMinX), (float) this.mListener.mMinWidthSize));
            oriLeft2 = oriRight - adjustedWidth;
            oriBottom = oriTop + ((int) ((((float) adjustedWidth) / this.mListener.mWidthHeightScale) + 0.5f));
        } else if (oriRight - oriLeft2 > this.mListener.mMaxWidthSize) {
            if (!this.mInResizeMaxSize) {
                this.mInResizeMaxSize = true;
                this.mResizeMaxX = x;
            }
            int adjustedWidth2 = this.mListener.mMaxWidthSize + ((int) afterFriction(Math.abs(x - this.mResizeMaxX), (float) this.mListener.mMaxWidthSize));
            oriLeft2 = oriRight - adjustedWidth2;
            oriBottom = oriTop + ((int) ((((float) adjustedWidth2) / this.mListener.mWidthHeightScale) + 0.5f));
        } else {
            this.mInResizeMinSize = false;
            this.mInResizeMaxSize = false;
        }
        this.mResizeLastMoveLeft = oriLeft2;
        this.mResizeLastMoveTop = oriTop;
        this.mResizeLastMoveRight = oriRight;
        this.mResizeLastMoveBottom = oriBottom;
        Slog.i(TAG, "leftBottom");
        setLeashPositionAndScale(new Rect(oriLeft2, oriTop, oriRight, oriBottom));
    }

    private void bottomLeft(float dy, float x, float y) {
        int i = this.mResizeLastMoveLeft;
        int oriTop = this.mResizeLastMoveTop;
        int oriRight = this.mResizeLastMoveRight;
        int oriBottom = this.mResizeLastMoveBottom;
        int bottomMargin = (this.mListener.mScreenHeight - MiuiMultiWindowUtils.FREEFORM_LANDCAPE_HEIGHT) / 2;
        int oriBottom2 = (int) (((float) oriBottom) + dy);
        int oriLeft = oriRight - ((int) ((((float) (oriBottom2 - oriTop)) * this.mListener.mWidthHeightScale) + 0.5f));
        if (!this.mListener.mIsLandcapeFreeform && oriBottom2 > this.mListener.mScreenHeight) {
            oriBottom2 = this.mListener.mScreenHeight;
            oriLeft = oriRight - ((int) ((((float) (oriBottom2 - oriTop)) * this.mListener.mWidthHeightScale) + 0.5f));
        } else if (this.mListener.mIsLandcapeFreeform && oriBottom2 > this.mListener.mScreenHeight - bottomMargin) {
            oriBottom2 = this.mListener.mScreenHeight - bottomMargin;
            oriLeft = oriRight - ((int) ((((float) (oriBottom2 - oriTop)) * this.mListener.mWidthHeightScale) + 0.5f));
        } else if (oriLeft < this.mListener.mNotchBar) {
            oriLeft = this.mListener.mNotchBar;
            oriBottom2 = oriTop + ((int) ((((float) (oriRight - oriLeft)) / this.mListener.mWidthHeightScale) + 0.5f));
        }
        if (oriRight - oriLeft < this.mListener.mMinWidthSize) {
            if (!this.mInResizeMinSize) {
                this.mInResizeMinSize = true;
                this.mResizeMinX = x;
            }
            int adjustedWidth = this.mListener.mMinWidthSize - ((int) afterFriction(Math.abs(x - this.mResizeMinX), (float) this.mListener.mMinWidthSize));
            oriLeft = oriRight - adjustedWidth;
            oriBottom2 = oriTop + ((int) ((((float) adjustedWidth) / this.mListener.mWidthHeightScale) + 0.5f));
        } else if (oriRight - oriLeft > this.mListener.mMaxWidthSize) {
            if (!this.mInResizeMaxSize) {
                this.mInResizeMaxSize = true;
                this.mResizeMaxX = x;
            }
            int adjustedWidth2 = this.mListener.mMaxWidthSize + ((int) afterFriction(Math.abs(x - this.mResizeMaxX), (float) this.mListener.mMaxWidthSize));
            oriLeft = oriRight - adjustedWidth2;
            oriBottom2 = oriTop + ((int) ((((float) adjustedWidth2) / this.mListener.mWidthHeightScale) + 0.5f));
        } else {
            this.mInResizeMinSize = false;
            this.mInResizeMaxSize = false;
        }
        this.mResizeLastMoveLeft = oriLeft;
        this.mResizeLastMoveTop = oriTop;
        this.mResizeLastMoveRight = oriRight;
        this.mResizeLastMoveBottom = oriBottom2;
        Slog.i(TAG, "bottomLeft");
        setLeashPositionAndScale(new Rect(oriLeft, oriTop, oriRight, oriBottom2));
    }

    private void rightBottom(float dx, float x, float y) {
        int oriLeft = this.mResizeLastMoveLeft;
        int oriTop = this.mResizeLastMoveTop;
        int oriRight = this.mResizeLastMoveRight;
        int i = this.mResizeLastMoveBottom;
        int bottomMargin = (this.mListener.mScreenHeight - MiuiMultiWindowUtils.FREEFORM_LANDCAPE_HEIGHT) / 2;
        int oriRight2 = (int) (((float) oriRight) + dx);
        int oriBottom = ((int) ((((float) (oriRight2 - oriLeft)) / this.mListener.mWidthHeightScale) + 0.5f)) + oriTop;
        if (oriRight2 > this.mListener.mScreenWidth) {
            oriRight2 = this.mListener.mScreenWidth;
            oriBottom = oriTop + ((int) ((((float) (oriRight2 - oriLeft)) / this.mListener.mWidthHeightScale) + 0.5f));
        } else if (!this.mListener.mIsLandcapeFreeform && oriBottom > this.mListener.mScreenHeight) {
            oriBottom = this.mListener.mScreenHeight;
            oriRight2 = oriLeft + ((int) ((((float) (oriBottom - oriTop)) * this.mListener.mWidthHeightScale) + 0.5f));
        } else if (this.mListener.mIsLandcapeFreeform && oriBottom > this.mListener.mScreenHeight - bottomMargin) {
            oriBottom = this.mListener.mScreenHeight - bottomMargin;
            oriRight2 = oriLeft + ((int) ((((float) (oriBottom - oriTop)) * this.mListener.mWidthHeightScale) + 0.5f));
        }
        if (oriRight2 - oriLeft < this.mListener.mMinWidthSize) {
            if (!this.mInResizeMinSize) {
                this.mInResizeMinSize = true;
                this.mResizeMinX = x;
            }
            int adjustedWidth = this.mListener.mMinWidthSize - ((int) afterFriction(Math.abs(x - this.mResizeMinX), (float) this.mListener.mMinWidthSize));
            oriRight2 = oriLeft + adjustedWidth;
            oriBottom = oriTop + ((int) ((((float) adjustedWidth) / this.mListener.mWidthHeightScale) + 0.5f));
        } else if (oriRight2 - oriLeft > this.mListener.mMaxWidthSize) {
            if (!this.mInResizeMaxSize) {
                this.mInResizeMaxSize = true;
                this.mResizeMaxX = x;
            }
            int adjustedWidth2 = this.mListener.mMaxWidthSize + ((int) afterFriction(Math.abs(x - this.mResizeMaxX), (float) this.mListener.mMaxWidthSize));
            oriRight2 = oriLeft + adjustedWidth2;
            oriBottom = oriTop + ((int) ((((float) adjustedWidth2) / this.mListener.mWidthHeightScale) + 0.5f));
        } else {
            this.mInResizeMinSize = false;
            this.mInResizeMaxSize = false;
        }
        this.mResizeLastMoveLeft = oriLeft;
        this.mResizeLastMoveTop = oriTop;
        this.mResizeLastMoveRight = oriRight2;
        this.mResizeLastMoveBottom = oriBottom;
        Slog.i(TAG, "rightBottom");
        setLeashPositionAndScale(new Rect(oriLeft, oriTop, oriRight2, oriBottom));
    }

    private void bottomRight(float dy, float x, float y) {
        int oriLeft = this.mResizeLastMoveLeft;
        int oriTop = this.mResizeLastMoveTop;
        int i = this.mResizeLastMoveRight;
        int oriBottom = this.mResizeLastMoveBottom;
        int bottomMargin = (this.mListener.mScreenHeight - MiuiMultiWindowUtils.FREEFORM_LANDCAPE_HEIGHT) / 2;
        int oriBottom2 = (int) (((float) oriBottom) + dy);
        int oriRight = ((int) ((((float) (oriBottom2 - oriTop)) * this.mListener.mWidthHeightScale) + 0.5f)) + oriLeft;
        if (!this.mListener.mIsLandcapeFreeform && oriBottom2 > this.mListener.mScreenHeight) {
            oriBottom2 = this.mListener.mScreenHeight;
            oriRight = oriLeft + ((int) ((((float) (oriBottom2 - oriTop)) * this.mListener.mWidthHeightScale) + 0.5f));
        } else if (this.mListener.mIsLandcapeFreeform && oriBottom2 > this.mListener.mScreenHeight - bottomMargin) {
            oriBottom2 = this.mListener.mScreenHeight - bottomMargin;
            oriRight = oriLeft + ((int) ((((float) (oriBottom2 - oriTop)) * this.mListener.mWidthHeightScale) + 0.5f));
        } else if (oriRight > this.mListener.mScreenWidth) {
            oriRight = this.mListener.mScreenWidth;
            oriBottom2 = oriTop + ((int) ((((float) (oriRight - oriLeft)) / this.mListener.mWidthHeightScale) + 0.5f));
        }
        if (oriRight - oriLeft < this.mListener.mMinWidthSize || oriRight - oriLeft > this.mListener.mMaxWidthSize) {
            if (oriRight - oriLeft < this.mListener.mMinWidthSize) {
                if (!this.mInResizeMinSize) {
                    this.mInResizeMinSize = true;
                    this.mResizeMinX = x;
                }
                int adjustedWidth = this.mListener.mMinWidthSize - ((int) afterFriction(Math.abs(x - this.mResizeMinX), (float) this.mListener.mMinWidthSize));
                oriRight = oriLeft + adjustedWidth;
                oriBottom2 = oriTop + ((int) ((((float) adjustedWidth) / this.mListener.mWidthHeightScale) + 0.5f));
            } else if (oriRight - oriLeft > this.mListener.mMaxWidthSize) {
                if (!this.mInResizeMaxSize) {
                    this.mInResizeMaxSize = true;
                    this.mResizeMaxX = x;
                }
                int adjustedWidth2 = this.mListener.mMaxWidthSize + ((int) afterFriction(Math.abs(x - this.mResizeMaxX), (float) this.mListener.mMaxWidthSize));
                oriRight = oriLeft + adjustedWidth2;
                oriBottom2 = oriTop + ((int) ((((float) adjustedWidth2) / this.mListener.mWidthHeightScale) + 0.5f));
            } else {
                this.mInResizeMinSize = false;
                this.mInResizeMaxSize = false;
            }
        }
        this.mResizeLastMoveLeft = oriLeft;
        this.mResizeLastMoveTop = oriTop;
        this.mResizeLastMoveRight = oriRight;
        this.mResizeLastMoveBottom = oriBottom2;
        Slog.i(TAG, "bottomRight");
        setLeashPositionAndScale(new Rect(oriLeft, oriTop, oriRight, oriBottom2));
    }

    private void setLeashPositionAndScale(Rect currentPosition) {
        float resizeScale;
        Set<Map.Entry<WindowState, MiuiFreeFormGesturePointerEventListener.WindowStateInfo>> entrySet;
        AppWindowToken aToken;
        Rect rect = currentPosition;
        float leashScale = ((float) currentPosition.width()) / this.mWindowWidth;
        if (this.mListener.mIsLandcapeFreeform) {
            resizeScale = (((((float) currentPosition.width()) * 1.0f) / ((float) this.mListener.mScreenWidth)) + ((((float) currentPosition.height()) * 1.0f) / ((float) this.mListener.mScreenHeight))) / 2.0f;
        } else {
            resizeScale = (((float) currentPosition.width()) * 1.0f) / ((float) this.mListener.shortSide);
        }
        Set<Map.Entry<WindowState, MiuiFreeFormGesturePointerEventListener.WindowStateInfo>> entrySet2 = this.mListener.mScalingWindows.entrySet();
        for (Map.Entry<WindowState, MiuiFreeFormGesturePointerEventListener.WindowStateInfo> winEntry : entrySet2) {
            WindowState win = winEntry.getKey();
            MiuiFreeFormGesturePointerEventListener.WindowStateInfo wInfo = winEntry.getValue();
            if (wInfo != null) {
                int type = win.mAttrs.type;
                if (type == 1 || type == 2 || type == 3) {
                    AppWindowToken aToken2 = win.mAppToken;
                    if (aToken2 != null) {
                        WindowSurfaceController sc = win.mWinAnimator.mSurfaceController;
                        if (type != 3 || wInfo.mAppToken == null) {
                            aToken = aToken2;
                        } else {
                            AppWindowToken aToken3 = wInfo.mAppToken;
                            WindowState mainWin = aToken3.findMainWindow(false);
                            if (mainWin != null && mainWin.mWinAnimator.getShown()) {
                                wInfo.mHasShowStartingWindow = true;
                                this.mListener.mScalingWindows.remove(win);
                                this.mListener.mScalingWindows.put(mainWin, wInfo);
                                this.mListener.mFreeFormAppWindows.add(mainWin);
                            }
                            aToken = aToken3;
                        }
                        if (sc != null) {
                            this.mListener.mGestureAnimator.setPositionInTransaction(aToken, (float) rect.left, (float) rect.top);
                            entrySet = entrySet2;
                            AppWindowToken aToken4 = aToken;
                            WindowSurfaceController windowSurfaceController = sc;
                            this.mListener.mGestureAnimator.setMatrixInTransaction(aToken, leashScale, win.mWinAnimator.mDtDx, win.mWinAnimator.mDtDy, leashScale);
                            win.mAttrs.privateFlags |= 64;
                            this.mListener.mGestureAnimator.setDefaultRoundCorner(aToken4);
                            this.mListener.mGestureAnimator.applyTransaction();
                            updateResizeParams((float) rect.left, (float) rect.top, resizeScale);
                            wInfo.mNowPosX = (float) rect.left;
                            wInfo.mNowPosY = (float) rect.top;
                            wInfo.mNowScale = leashScale;
                            Slog.i(TAG, "setLeashPosition mResizeLeft:" + this.mResizeLeft + " ResizeTop: " + this.mResizeTop + " ResizeScale:" + this.mResizeScale + " currentScale:" + leashScale);
                        }
                    }
                } else {
                    entrySet = entrySet2;
                }
                entrySet2 = entrySet;
            }
        }
    }

    private void updateResizeParams(float left, float top, float scale) {
        this.mResizeLeft = left;
        this.mPosX = left;
        this.mResizeTop = top;
        this.mPosY = top;
        this.mResizeScale = scale;
    }

    public boolean notifyUpLocked(MotionEvent motionEvent) {
        int i;
        if (this.mListener.mFreeFormAppWindows.isEmpty() || this.mOutFreeFormTouchRegion) {
            this.mOutFreeFormTouchRegion = false;
            return false;
        }
        float yVelocity = 0.0f;
        VelocityTracker velocityTracker = this.mVelocityTracker;
        if (velocityTracker != null) {
            try {
                velocityTracker.addMovement(motionEvent);
                this.mVelocityTracker.computeCurrentVelocity(1000);
                float xVelocity = this.mVelocityTracker.getXVelocity();
                yVelocity = this.mVelocityTracker.getYVelocity();
                this.mVelocityTracker.recycle();
                this.mVelocityTracker = null;
            } catch (Exception e) {
            }
        }
        this.mCurrentAction = 1;
        try {
            this.mCurrentX = motionEvent.getX();
            this.mCurrentY = motionEvent.getY();
            int i2 = this.mCurrentTouchedMode;
            if (i2 == 0) {
                if (this.mShowedHotSpotView) {
                    try {
                        this.mListener.mGestureController.mWindowController.hideHotSpotView();
                    } catch (Exception e2) {
                    }
                    this.mShowedHotSpotView = false;
                }
                this.mListener.mTaskPositioner.updateWindowUpBounds((float) ((int) this.mCurrentX), (float) ((int) this.mCurrentY), this.mDownX, this.mDownY, this.mListener.mIsPortrait, this.mWindowOriginalBounds);
            } else if (i2 == 1) {
                float absSetY = Math.abs(this.mCurrentY - this.mDownY);
                Slog.d(TAG, "notifyUpLocked absSetY:" + absSetY + " currentGesture:" + this.mCurrentGestureAction);
                int i3 = this.mCurrentGestureAction;
                if (i3 == 0) {
                    if (this.mListener.mIsPortrait) {
                        if (this.mListener.mIsPortrait && this.mListener.mIsLandcapeFreeform && (absSetY >= 100.0f || yVelocity > 400.0f)) {
                            if (this.mLandDropAnimation.get() == 2) {
                                if (this.mListener.mComponentName != null) {
                                    this.mListener.mGestureController.mWindowController.startContentAnimation(1, this.mListener.mComponentName.getPackageName());
                                    this.mLandDropAnimation.set(-1);
                                    MiuiFreeFormWindowController.DropWindowType = 0;
                                    return true;
                                }
                            } else if (!(this.mListener.mTopFreeFormAppToken == null || this.mListener.mTopFreeFormAppToken.token == null)) {
                                this.mListener.mGestureController.mWindowController.setStartBounds(new Rect((int) this.mPosX, (int) this.mPosY, (int) (this.mPosX + (this.mWindowWidth * this.mCurrentScale)), (int) (this.mPosY + (this.mWindowHeight * this.mCurrentScale))));
                                if (this.mListener.mComponentName != null) {
                                    this.mListener.mGestureController.mWindowController.startContentAnimation(1, this.mListener.mComponentName.getPackageName());
                                }
                                MiuiFreeFormWindowController.DropWindowType = 0;
                                this.mLandDropAnimation.set(-1);
                                return true;
                            }
                        }
                        if (absSetY > 300.0f) {
                            this.mCurrentAnimation = 1;
                        } else {
                            this.mCurrentAnimation = 2;
                        }
                    } else if (absSetY >= 25.0f || yVelocity > 400.0f) {
                        if (this.mLandDropAnimation.get() == 2) {
                            if (this.mListener.mComponentName != null) {
                                this.mListener.mGestureController.mWindowController.startContentAnimation(1, this.mListener.mComponentName.getPackageName());
                                this.mLandDropAnimation.set(-1);
                                return true;
                            }
                        } else if (!(this.mListener.mTopFreeFormAppToken == null || this.mListener.mTopFreeFormAppToken.token == null)) {
                            this.mListener.mGestureController.mWindowController.setStartBounds(new Rect((int) this.mPosX, (int) this.mPosY, (int) (this.mPosX + (this.mWindowWidth * this.mCurrentScale)), (int) (this.mPosY + (this.mWindowHeight * this.mCurrentScale))));
                            if (this.mListener.mComponentName != null) {
                                this.mListener.mGestureController.mWindowController.startContentAnimation(1, this.mListener.mComponentName.getPackageName());
                                this.mLandDropAnimation.set(-1);
                                return true;
                            }
                        }
                        this.mCurrentAnimation = 1;
                    } else {
                        this.mCurrentAnimation = 2;
                    }
                } else if (i3 == 1) {
                    if (absSetY > 300.0f) {
                        this.mCurrentAnimation = 0;
                    } else {
                        this.mCurrentAnimation = 2;
                    }
                }
                startGestureAnimation(this.mCurrentAnimation);
            } else if (i2 == 2 || i2 == 3) {
                Slog.i(TAG, "Up mCurrentTouchedMode == TOUCH_RESIZE_LEFT || TOUCH_RESIZE_RIGHT ResizeLeft:" + this.mResizeLeft + " ResizeTop: " + this.mResizeTop + " ResizeScale:" + this.mResizeScale);
                this.mListener.mGestureController.notifyFreeFormApplicationResizeEnd(SystemClock.uptimeMillis() - this.mResizeStartTime);
                int resizeBackMode = needResizeBack();
                if (resizeBackMode == 0 || resizeBackMode == 1) {
                    if (this.mCurrentTouchedMode == 2) {
                        i = 10;
                    } else {
                        i = 11;
                    }
                    this.mCurrentAnimation = i;
                    this.mResizeBackMode = resizeBackMode;
                    startGestureAnimation(this.mCurrentAnimation);
                } else {
                    try {
                        MiuiMultiWindowUtils.sScale = this.mResizeScale;
                        Rect position = new Rect((int) this.mResizeLeft, (int) this.mResizeTop, (int) (this.mResizeLeft + ((float) this.mListener.mWindowBounds.width())), (int) (this.mResizeTop + ((float) this.mListener.mWindowBounds.height())));
                        Slog.i(TAG, "Up mCurrentTouchedMode == TOUCH_RESIZE_LEFT || TOUCH_RESIZE_RIGHT position:" + position);
                        this.mListener.mService.mActivityManager.resizeTask(this.mListener.mTaskId, position, 2);
                    } catch (Exception e3) {
                        e3.printStackTrace();
                    }
                    this.mListener.mGestureAnimator.reset();
                }
            }
            this.mCurrentTouchedMode = -1;
            return true;
        } catch (Exception e4) {
            return false;
        }
    }

    private int needResizeBack() {
        if (this.mInResizeMaxSize) {
            return 0;
        }
        if (this.mInResizeMinSize) {
            return 1;
        }
        return -1;
    }

    public void initGestureDetecteSpace() {
        int mTopDetecteLeftPoint = this.mListener.mWindowBounds.left + this.mListener.mResizeWidth;
        int mTopDetecteTopPoint = this.mListener.mWindowBounds.top - MiuiMultiWindowUtils.HOT_SPACE_TOP_OFFSITE;
        int mTopDetecteBottomPoint = MiuiMultiWindowUtils.TOP_DECOR_CAPTIONVIEW_HEIGHT + mTopDetecteTopPoint + MiuiMultiWindowUtils.HOT_SPACE_TOP_OFFSITE;
        int mLeftResizeDetecteLeftPoint = this.mListener.mWindowBounds.left;
        int mLeftResizeDetecteRightPoint = this.mListener.mResizeWidth + mLeftResizeDetecteLeftPoint;
        int mLeftResizeDetecteBottomPoint = ((int) (((float) this.mListener.mWindowBounds.top) + this.mWindowHeight)) + MiuiMultiWindowUtils.HOT_SPACE_BOTTOM_OFFSITE;
        int mBottomDetecteRightPoint = (int) (((((float) this.mListener.mWindowBounds.left) + this.mWindowWidth) - ((float) this.mListener.mResizeWidth)) - 1.0f);
        int mBottomDetecteBottomPoint = ((int) (((float) this.mListener.mWindowBounds.top) + this.mWindowHeight)) + MiuiMultiWindowUtils.HOT_SPACE_BOTTOM_OFFSITE;
        int mRightResizeDetecteLeftPoint = mBottomDetecteRightPoint + 1;
        int mRightResizeDetecteRightPoint = this.mListener.mResizeWidth + mRightResizeDetecteLeftPoint;
        int mRightResizeDetecteLeftPoint2 = mRightResizeDetecteLeftPoint;
        int mRightResizeDetecteTopPoint = (int) (((((float) this.mListener.mWindowBounds.top) + this.mWindowHeight) - ((float) MiuiMultiWindowUtils.HOT_SPACE_BOTTOM_HEIGHT)) + ((float) MiuiMultiWindowUtils.HOT_SPACE_BOTTOM_OFFSITE));
        int mRightResizeDetecteBottomPoint = ((int) (((float) this.mListener.mWindowBounds.top) + this.mWindowHeight)) + MiuiMultiWindowUtils.HOT_SPACE_BOTTOM_OFFSITE;
        this.mTopGestureDetectBounds.set(mTopDetecteLeftPoint, mTopDetecteTopPoint, (int) ((((float) mTopDetecteLeftPoint) + this.mWindowWidth) - ((float) this.mListener.mResizeWidth)), mTopDetecteBottomPoint);
        this.mBottomGestureDetectBounds.set(mLeftResizeDetecteRightPoint + 1, (int) (((((float) this.mListener.mWindowBounds.top) + this.mWindowHeight) - ((float) MiuiMultiWindowUtils.HOT_SPACE_BOTTOM_HEIGHT)) + ((float) MiuiMultiWindowUtils.HOT_SPACE_BOTTOM_OFFSITE)), mBottomDetecteRightPoint, mBottomDetecteBottomPoint);
        this.mLeftResizeGestureDetectBounds.set(mLeftResizeDetecteLeftPoint, (int) (((((float) this.mListener.mWindowBounds.top) + this.mWindowHeight) - ((float) MiuiMultiWindowUtils.HOT_SPACE_BOTTOM_HEIGHT)) + ((float) MiuiMultiWindowUtils.HOT_SPACE_BOTTOM_OFFSITE)), mLeftResizeDetecteRightPoint, mLeftResizeDetecteBottomPoint);
        int i = mTopDetecteLeftPoint;
        int mRightResizeDetecteLeftPoint3 = mRightResizeDetecteLeftPoint2;
        int mRightResizeDetecteLeftPoint4 = mTopDetecteTopPoint;
        this.mRightResizeGestureDetectBounds.set(mRightResizeDetecteLeftPoint3, mRightResizeDetecteTopPoint, mRightResizeDetecteRightPoint, mRightResizeDetecteBottomPoint);
        StringBuilder sb = new StringBuilder();
        int i2 = mRightResizeDetecteLeftPoint3;
        sb.append("TopGestureDetectBounds:");
        sb.append(this.mTopGestureDetectBounds);
        sb.append(" BottomGestureDetectBounds:");
        sb.append(this.mBottomGestureDetectBounds);
        sb.append(" LeftResizeGestureDetectBounds:");
        sb.append(this.mLeftResizeGestureDetectBounds);
        sb.append(" RightResizeGestureDetectBounds:");
        sb.append(this.mRightResizeGestureDetectBounds);
        sb.append(" windowBounds:");
        sb.append(this.mListener.mWindowBounds);
        sb.append(" sScale:");
        sb.append(MiuiMultiWindowUtils.sScale);
        Slog.w(TAG, sb.toString());
    }

    private void dealWithWindowPosition(float x, float y) {
        this.mPosY = y - ((this.mWindowHeight * this.mCurrentScale) / 2.0f);
        this.mPosX = x - ((this.mWindowWidth * this.mCurrentScale) / 2.0f);
        Slog.d(TAG, "dealWithWindowPosition x:" + x + " y:" + y + " Listener.mTopFreeFormAppToken:" + this.mListener.mTopFreeFormAppToken + " PosY:" + this.mPosY + " PosX:" + this.mPosX + " currentScale:" + this.mCurrentScale);
        this.mListener.mGestureAnimator.setPositionInTransaction(this.mListener.mTopFreeFormAppToken, this.mPosX, this.mPosY);
        this.mListener.mGestureAnimator.applyTransaction();
    }

    /* JADX WARNING: Code restructure failed: missing block: B:14:0x0069, code lost:
        r13 = r8.mAppToken;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void dealWithWindowGestureAnimal(float r22, float r23, int r24) {
        /*
            r21 = this;
            r0 = r21
            r1 = r22
            r2 = r23
            r3 = r24
            float r4 = r0.mWindowHeight
            r0.calculateWindowScale(r1, r2, r4, r3)
            float r4 = r0.mWindowHeight
            r0.calculateWindowAlpha(r1, r2, r4, r3)
            float r4 = r0.mCurrentScale
            float r5 = r0.mWindowHeight
            float r6 = r0.mWindowWidth
            r0.calculateWindowPosition(r3, r4, r5, r6)
            com.android.server.wm.MiuiFreeFormGesturePointerEventListener r4 = r0.mListener
            java.util.concurrent.ConcurrentHashMap<com.android.server.wm.WindowState, com.android.server.wm.MiuiFreeFormGesturePointerEventListener$WindowStateInfo> r4 = r4.mScalingWindows
            java.util.Set r4 = r4.entrySet()
            java.util.Iterator r5 = r4.iterator()
        L_0x0027:
            boolean r6 = r5.hasNext()
            if (r6 == 0) goto L_0x0147
            java.lang.Object r6 = r5.next()
            java.util.Map$Entry r6 = (java.util.Map.Entry) r6
            java.lang.Object r7 = r6.getKey()
            com.android.server.wm.WindowState r7 = (com.android.server.wm.WindowState) r7
            java.lang.Object r8 = r6.getValue()
            com.android.server.wm.MiuiFreeFormGesturePointerEventListener$WindowStateInfo r8 = (com.android.server.wm.MiuiFreeFormGesturePointerEventListener.WindowStateInfo) r8
            if (r8 != 0) goto L_0x0042
            goto L_0x0027
        L_0x0042:
            android.graphics.Rect r9 = new android.graphics.Rect
            com.android.server.wm.WindowFrames r10 = r7.mWindowFrames
            android.graphics.Rect r10 = r10.mFrame
            r9.<init>(r10)
            android.graphics.Rect r10 = r8.mNowFrame
            r10.set(r9)
            android.view.WindowManager$LayoutParams r10 = r7.mAttrs
            int r10 = r10.type
            r11 = 3
            r12 = 1
            if (r10 == r12) goto L_0x005a
            if (r10 != r11) goto L_0x0141
        L_0x005a:
            com.android.server.wm.AppWindowToken r13 = r7.mAppToken
            if (r13 != 0) goto L_0x005f
            goto L_0x0027
        L_0x005f:
            com.android.server.wm.WindowStateAnimator r14 = r7.mWinAnimator
            com.android.server.wm.WindowSurfaceController r14 = r14.mSurfaceController
            if (r10 != r11) goto L_0x0091
            com.android.server.wm.AppWindowToken r11 = r8.mAppToken
            if (r11 == 0) goto L_0x0091
            com.android.server.wm.AppWindowToken r13 = r8.mAppToken
            r11 = 0
            com.android.server.wm.WindowState r11 = r13.findMainWindow(r11)
            if (r11 == 0) goto L_0x0091
            com.android.server.wm.WindowStateAnimator r15 = r11.mWinAnimator
            boolean r15 = r15.getShown()
            if (r15 == 0) goto L_0x0091
            r8.mHasShowStartingWindow = r12
            com.android.server.wm.MiuiFreeFormGesturePointerEventListener r12 = r0.mListener
            java.util.concurrent.ConcurrentHashMap<com.android.server.wm.WindowState, com.android.server.wm.MiuiFreeFormGesturePointerEventListener$WindowStateInfo> r12 = r12.mScalingWindows
            r12.remove(r7)
            com.android.server.wm.MiuiFreeFormGesturePointerEventListener r12 = r0.mListener
            java.util.concurrent.ConcurrentHashMap<com.android.server.wm.WindowState, com.android.server.wm.MiuiFreeFormGesturePointerEventListener$WindowStateInfo> r12 = r12.mScalingWindows
            r12.put(r11, r8)
            com.android.server.wm.MiuiFreeFormGesturePointerEventListener r12 = r0.mListener
            java.util.ArrayList<com.android.server.wm.WindowState> r12 = r12.mFreeFormAppWindows
            r12.add(r11)
        L_0x0091:
            if (r14 != 0) goto L_0x0094
            goto L_0x0027
        L_0x0094:
            float r11 = r0.mCurrentTempX
            int r11 = (int) r11
            float r11 = (float) r11
            r0.mPosX = r11
            float r11 = r0.mCurrentTempY
            int r11 = (int) r11
            float r11 = (float) r11
            r0.mPosY = r11
            boolean r11 = com.android.server.wm.MiuiFreeFormGestureController.DEBUG
            if (r11 == 0) goto L_0x00ea
            java.lang.StringBuilder r11 = new java.lang.StringBuilder
            r11.<init>()
            java.lang.String r12 = " notifyMoveLocked: mNowFrame:"
            r11.append(r12)
            android.graphics.Rect r12 = r8.mNowFrame
            r11.append(r12)
            java.lang.String r12 = " mFrame:"
            r11.append(r12)
            com.android.server.wm.WindowFrames r12 = r7.mWindowFrames
            android.graphics.Rect r12 = r12.mFrame
            r11.append(r12)
            java.lang.String r12 = " mCurrentScale:"
            r11.append(r12)
            float r12 = r0.mCurrentScale
            r11.append(r12)
            java.lang.String r12 = " mDtDx:"
            r11.append(r12)
            com.android.server.wm.WindowStateAnimator r12 = r7.mWinAnimator
            float r12 = r12.mDtDx
            r11.append(r12)
            java.lang.String r12 = " mDtDy:"
            r11.append(r12)
            com.android.server.wm.WindowStateAnimator r12 = r7.mWinAnimator
            float r12 = r12.mDtDy
            r11.append(r12)
            java.lang.String r11 = r11.toString()
            java.lang.String r12 = "MiuiFreeFormWindowMotionHelper"
            android.util.Slog.d(r12, r11)
        L_0x00ea:
            com.android.server.wm.MiuiFreeFormGesturePointerEventListener r11 = r0.mListener
            com.android.server.wm.MiuiFreeFormGestureAnimator r11 = r11.mGestureAnimator
            float r12 = r0.mPosX
            float r15 = r0.mPosY
            r11.setPositionInTransaction(r13, r12, r15)
            com.android.server.wm.MiuiFreeFormGesturePointerEventListener r11 = r0.mListener
            com.android.server.wm.MiuiFreeFormGestureAnimator r11 = r11.mGestureAnimator
            float r12 = r0.mCurrentAlpha
            r11.setAlphaInTransaction((com.android.server.wm.AppWindowToken) r13, (float) r12)
            com.android.server.wm.MiuiFreeFormGesturePointerEventListener r11 = r0.mListener
            com.android.server.wm.MiuiFreeFormGestureAnimator r15 = r11.mGestureAnimator
            float r11 = r0.mCurrentScale
            com.android.server.wm.WindowStateAnimator r12 = r7.mWinAnimator
            float r12 = r12.mDtDx
            com.android.server.wm.WindowStateAnimator r1 = r7.mWinAnimator
            float r1 = r1.mDtDy
            float r2 = r0.mCurrentScale
            r16 = r13
            r17 = r11
            r18 = r12
            r19 = r1
            r20 = r2
            r15.setMatrixInTransaction(r16, r17, r18, r19, r20)
            android.view.WindowManager$LayoutParams r1 = r7.mAttrs
            int r2 = r1.privateFlags
            r2 = r2 | 64
            r1.privateFlags = r2
            com.android.server.wm.MiuiFreeFormGesturePointerEventListener r1 = r0.mListener
            com.android.server.wm.MiuiFreeFormGestureAnimator r1 = r1.mGestureAnimator
            r1.setDefaultRoundCorner(r13)
            com.android.server.wm.MiuiFreeFormGesturePointerEventListener r1 = r0.mListener
            com.android.server.wm.MiuiFreeFormGestureAnimator r1 = r1.mGestureAnimator
            r1.applyTransaction()
            float r1 = r0.mPosX
            r8.mNowPosX = r1
            float r1 = r0.mPosY
            r8.mNowPosY = r1
            float r1 = r0.mCurrentScale
            r8.mNowScale = r1
            float r1 = r0.mCurrentAlpha
            r8.mNowAlpha = r1
        L_0x0141:
            r1 = r22
            r2 = r23
            goto L_0x0027
        L_0x0147:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.wm.MiuiFreeFormWindowMotionHelper.dealWithWindowGestureAnimal(float, float, int):void");
    }

    public void turnFreeFormToSmallWindow() {
        this.mListener.hideCaptionView();
        startGestureAnimation(3);
    }

    public void startFreeFormWindowAnimal() {
        startGestureAnimation(4);
    }

    public void inputMethodVisibleChanged(int inputMethodHeight) {
        int bottom;
        Slog.d(TAG, "inputMethodVisibleChanged inputMethodHeight:" + inputMethodHeight);
        if (inputMethodHeight == 0) {
            if (this.mHadChangedForInputMethod) {
                this.mHadChangedForInputMethod = false;
                startGestureAnimation(15);
            }
        } else if (!this.mHadChangedForInputMethod) {
            if (this.mListener.mTaskPositioner.mWindowDragBounds.isEmpty()) {
                bottom = this.mListener.mWindowBounds.top + this.mListener.mWindowBounds.height();
            } else {
                bottom = this.mListener.mTaskPositioner.mWindowDragBounds.top + this.mListener.mTaskPositioner.mWindowDragBounds.height();
            }
            if (bottom >= (this.mListener.mScreenHeight - inputMethodHeight) - 100) {
                this.mHadChangedForInputMethod = true;
                this.mOffSetYForInputMethod = bottom - inputMethodHeight;
                startGestureAnimation(16);
            }
        }
    }

    public void startSmallWindowTranslateAnimal(int lastOrientation) {
        cancelAllSpringAnimal();
        Rect finalWindowBounds = MiuiMultiWindowUtils.findNearestCorner(this.mListener.mService.mContext, 0.0f, 0.0f, MiuiMultiWindowUtils.mCurrentSmallWindowCorner, this.mListener.mIsLandcapeFreeform);
        this.mListener.setSmallFreeFormWindowBounds(finalWindowBounds);
        this.mPosY = (float) finalWindowBounds.top;
        this.mPosX = (float) finalWindowBounds.left;
        Iterator<WindowState> it = this.mListener.mFreeFormAppWindows.iterator();
        while (it.hasNext()) {
            WindowState win = it.next();
            this.mListener.mGestureAnimator.setPositionInTransaction(win.mAppToken, (float) finalWindowBounds.left, (float) finalWindowBounds.top);
            this.mListener.mGestureAnimator.setMatrixInTransaction(win.mAppToken, this.mSmallWindowTargetWScale, 0.0f, 0.0f, this.mSmallWindowTargetHScale);
            this.mListener.mGestureAnimator.applyTransaction();
        }
        Slog.d(TAG, "startSmallWindowTranslateAnimal finalWindowBounds:" + finalWindowBounds);
        synchronized (this.mListener.mService.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                this.mListener.setSmallFreeFormWindowBounds(finalWindowBounds);
                this.mListener.mGestureController.mDisplayContent.getInputMonitor().forceUpdateImmediately();
            } catch (Throwable th) {
                while (true) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
        }
        WindowManagerService.resetPriorityAfterLockedSection();
        this.mListener.mGestureController.mHandler.postDelayed(new Runnable() {
            public void run() {
                MiuiFreeFormWindowMotionHelper.this.mListener.hideCaptionView();
            }
        }, 400);
    }

    public void cancelAllSpringAnimal() {
        if (!this.mWindowLocks.isEmpty()) {
            for (Map.Entry<WindowState, MiuiFreeFormGestureAnimator.AnimalLock> winEntry : this.mWindowLocks.entrySet()) {
                winEntry.getValue().cancel();
            }
            this.mWindowLocks.clear();
            this.mAnimalLocks.clear();
        }
    }

    public void startShowFullScreenWindow() {
        this.mListener.startShowFullScreenWindow();
    }

    public void startExitApplication() {
        this.mListener.startExitApplication();
    }

    public void registerInputConsumer() {
        this.mListener.registerInputConsumer();
    }

    private void applyShowSmallWindowAnimation(WindowState w, MiuiFreeFormGesturePointerEventListener.WindowStateInfo info, int animationType) {
        WindowState windowState = w;
        this.mListener.mGestureAnimator.recreateLeashIfNeeded(windowState.mAppToken);
        float targetHeightScale = this.mSmallWindowTargetHScale;
        float targetWidthScale = this.mSmallWindowTargetWScale;
        MiuiFreeFormGestureAnimator.AnimalLock animalLock = new MiuiFreeFormGestureAnimator.AnimalLock(windowState);
        Rect targetCorner = MiuiMultiWindowUtils.findNearestCorner(this.mListener.mService.mContext, (float) this.mListener.mScreenWidth, (float) (this.mListener.mScreenHeight / 2), -1, this.mListener.mIsLandcapeFreeform);
        Slog.d(TAG, "mPosX:" + this.mPosX + " mPosY:" + this.mPosY + " targetCorner:" + targetCorner);
        WindowState windowState2 = w;
        MiuiFreeFormSpringAnimation scaleXSpringAnimation = this.mListener.mGestureAnimator.createSpringAnimation(windowState2, 1.0f, targetWidthScale, 631.7f, 0.7f, 0.0f, 5);
        MiuiFreeFormSpringAnimation scaleYSpringAnimation = this.mListener.mGestureAnimator.createSpringAnimation(windowState2, 1.0f, targetHeightScale, 631.7f, 0.7f, 0.0f, 6);
        float f = targetHeightScale;
        MiuiFreeFormSpringAnimation scaleYSpringAnimation2 = scaleYSpringAnimation;
        MiuiFreeFormSpringAnimation tXSpringAnimation = this.mListener.mGestureAnimator.createSpringAnimation(w, (float) this.mLastFreeFormWindowStartBounds.left, (float) targetCorner.left, 987.0f, 0.99f, 0.0f, 2);
        MiuiFreeFormSpringAnimation tXSpringAnimation2 = tXSpringAnimation;
        MiuiFreeFormSpringAnimation tYSpringAnimation = this.mListener.mGestureAnimator.createSpringAnimation(w, (float) this.mLastFreeFormWindowStartBounds.top, (float) targetCorner.top, 987.0f, 0.99f, 0.0f, 3);
        scaleXSpringAnimation.addEndListener(this.mListener.mGestureAnimator.createAnimationEndListener(MiuiFreeFormGestureAnimator.SCALE_X_END_LISTENER));
        scaleYSpringAnimation2.addEndListener(this.mListener.mGestureAnimator.createAnimationEndListener(MiuiFreeFormGestureAnimator.SCALE_Y_END_LISTENER));
        tXSpringAnimation2.addEndListener(this.mListener.mGestureAnimator.createAnimationEndListener(MiuiFreeFormGestureAnimator.TRANSLATE_X_END_LISTENER));
        tYSpringAnimation.addEndListener(this.mListener.mGestureAnimator.createAnimationEndListener(MiuiFreeFormGestureAnimator.TRANSLATE_Y_END_LISTENER));
        this.mWindowLocks.put(windowState, animalLock);
        this.mAnimalLocks.put(scaleXSpringAnimation, animalLock);
        this.mAnimalLocks.put(scaleYSpringAnimation2, animalLock);
        this.mAnimalLocks.put(tXSpringAnimation2, animalLock);
        this.mAnimalLocks.put(tYSpringAnimation, animalLock);
        animalLock.mScaleXAnimation = scaleXSpringAnimation;
        animalLock.mScaleYAnimation = scaleYSpringAnimation2;
        animalLock.mTranslateXAnimation = tXSpringAnimation2;
        animalLock.mTranslateYAnimation = tYSpringAnimation;
        animalLock.start(animationType);
    }

    private void applyResetAnimation(WindowState w, MiuiFreeFormGesturePointerEventListener.WindowStateInfo info, int animationType) {
        WindowState windowState = w;
        MiuiFreeFormGesturePointerEventListener.WindowStateInfo windowStateInfo = info;
        if (this.mCurrentGestureAction == 1) {
            WindowState windowState2 = w;
            MiuiFreeFormSpringAnimation alphaSpringAnimation = this.mListener.mGestureAnimator.createSpringAnimation(windowState2, windowStateInfo.mNowAlpha, 1.0f, 987.0f, 0.99f, 0.0f, 1);
            MiuiFreeFormSpringAnimation scaleSpringAnimation = this.mListener.mGestureAnimator.createSpringAnimation(windowState2, windowStateInfo.mNowScale, 1.0f, 987.0f, 0.99f, 0.0f, 4);
            alphaSpringAnimation.addEndListener(this.mListener.mGestureAnimator.createAnimationEndListener(MiuiFreeFormGestureAnimator.ALPHA_END_LISTENER));
            scaleSpringAnimation.addEndListener(this.mListener.mGestureAnimator.createAnimationEndListener(MiuiFreeFormGestureAnimator.SCALE_END_LISTENER));
            MiuiFreeFormGestureAnimator.AnimalLock animalLock = new MiuiFreeFormGestureAnimator.AnimalLock(windowState);
            this.mWindowLocks.put(windowState, animalLock);
            this.mAnimalLocks.put(alphaSpringAnimation, animalLock);
            this.mAnimalLocks.put(scaleSpringAnimation, animalLock);
            animalLock.mAlphaAnimation = alphaSpringAnimation;
            animalLock.mScaleAnimation = scaleSpringAnimation;
            animalLock.start(2);
            return;
        }
        float finalPosX = windowStateInfo.mOriPosX;
        float startPosX = windowStateInfo.mNowPosX;
        float finalPosY = windowStateInfo.mOriPosY;
        WindowState windowState3 = w;
        float startPosY = windowStateInfo.mNowPosY;
        float finalPosY2 = finalPosY;
        MiuiFreeFormSpringAnimation alphaSpringAnimation2 = this.mListener.mGestureAnimator.createSpringAnimation(windowState3, windowStateInfo.mNowAlpha, 1.0f, 987.0f, 0.99f, 0.0f, 1);
        MiuiFreeFormSpringAnimation scaleSpringAnimation2 = this.mListener.mGestureAnimator.createSpringAnimation(windowState3, windowStateInfo.mNowScale, 1.0f, 987.0f, 0.99f, 0.0f, 4);
        float f = finalPosX;
        MiuiFreeFormSpringAnimation tXSpringAnimation = this.mListener.mGestureAnimator.createSpringAnimation(windowState3, startPosX, finalPosX, 987.0f, 0.99f, 0.0f, 2);
        MiuiFreeFormSpringAnimation tYSpringAnimation = this.mListener.mGestureAnimator.createSpringAnimation(windowState3, startPosY, finalPosY2, 987.0f, 0.99f, 0.0f, 3);
        alphaSpringAnimation2.addEndListener(this.mListener.mGestureAnimator.createAnimationEndListener(MiuiFreeFormGestureAnimator.ALPHA_END_LISTENER));
        scaleSpringAnimation2.addEndListener(this.mListener.mGestureAnimator.createAnimationEndListener(MiuiFreeFormGestureAnimator.SCALE_END_LISTENER));
        tXSpringAnimation.addEndListener(this.mListener.mGestureAnimator.createAnimationEndListener(MiuiFreeFormGestureAnimator.TRANSLATE_X_END_LISTENER));
        tYSpringAnimation.addEndListener(this.mListener.mGestureAnimator.createAnimationEndListener(MiuiFreeFormGestureAnimator.TRANSLATE_Y_END_LISTENER));
        MiuiFreeFormGestureAnimator.AnimalLock animalLock2 = new MiuiFreeFormGestureAnimator.AnimalLock(windowState);
        this.mWindowLocks.put(windowState, animalLock2);
        this.mAnimalLocks.put(alphaSpringAnimation2, animalLock2);
        this.mAnimalLocks.put(scaleSpringAnimation2, animalLock2);
        this.mAnimalLocks.put(tXSpringAnimation, animalLock2);
        this.mAnimalLocks.put(tYSpringAnimation, animalLock2);
        animalLock2.mAlphaAnimation = alphaSpringAnimation2;
        animalLock2.mScaleAnimation = scaleSpringAnimation2;
        animalLock2.mTranslateXAnimation = tXSpringAnimation;
        animalLock2.mTranslateYAnimation = tYSpringAnimation;
        animalLock2.start(2);
        this.mPosX = startPosX;
        this.mPosY = startPosY;
    }

    private void applyResizeBackAnimation(WindowState w, MiuiFreeFormGesturePointerEventListener.WindowStateInfo info, int resizeOrientation) {
        float f;
        float resizeScale;
        float resizeScale2;
        float finalPosX;
        float finalPosY;
        WindowState windowState = w;
        MiuiFreeFormGesturePointerEventListener.WindowStateInfo windowStateInfo = info;
        int i = resizeOrientation;
        MiuiFreeFormGestureAnimator.AnimalLock animalLock = new MiuiFreeFormGestureAnimator.AnimalLock(windowState);
        float startPosX = windowStateInfo.mNowPosX;
        float startPosY = windowStateInfo.mNowPosY;
        float startScale = windowStateInfo.mNowScale;
        if (this.mResizeBackMode == 0) {
            f = ((float) this.mListener.mMaxWidthSize) / this.mWindowWidth;
        } else {
            f = ((float) this.mListener.mMinWidthSize) / this.mWindowWidth;
        }
        float finalScale = f;
        if (this.mListener.mIsLandcapeFreeform) {
            resizeScale = (((this.mWindowWidth * finalScale) / ((float) this.mListener.mScreenWidth)) + ((this.mWindowHeight * finalScale) / ((float) this.mListener.mScreenHeight))) / 2.0f;
        } else {
            resizeScale = (this.mWindowWidth * finalScale) / ((float) this.mListener.shortSide);
        }
        Slog.d(TAG, "applyResizeBackAnimation() mResizeScale:" + this.mResizeScale);
        MiuiFreeFormGestureAnimator.AnimalLock animalLock2 = animalLock;
        if (i == 10) {
            float f2 = this.mWindowWidth;
            finalPosX = (startPosX + (startScale * f2)) - (f2 * finalScale);
            resizeScale2 = resizeScale;
            Slog.i(TAG, "resizeOrientation == ANIMATION_RESIZE_BACK_LEFT_BOTTOM startScale:" + startScale + "finalScale:" + finalScale + "startPosX" + startPosX + "finalPosX:" + finalPosX + "startPosY" + startPosY + "mResizeScale:" + this.mResizeScale + "w: " + windowState);
            finalPosY = startPosY;
        } else {
            resizeScale2 = resizeScale;
            if (resizeOrientation == 11) {
                float finalPosX2 = startPosX;
                Slog.i(TAG, "resizeOrientation == ANIMATION_RESIZE_BACK_RIGHT_BOTTOM startScale:" + startScale + "finalScale:" + finalScale + "startPosX" + startPosX + "finalPosX:" + finalPosX2 + "startPosY" + startPosY + "mResizeScale:" + this.mResizeScale + "w: " + windowState);
                finalPosX = finalPosX2;
                finalPosY = startPosY;
            } else {
                finalPosX = 0.0f;
                finalPosY = 0.0f;
            }
        }
        float resizeScale3 = resizeScale2;
        updateResizeParams(finalPosX, finalPosY, resizeScale3);
        float f3 = resizeScale3;
        float f4 = finalScale;
        MiuiFreeFormSpringAnimation scaleSpringAnimation = this.mListener.mGestureAnimator.createSpringAnimation(w, startScale, finalScale, 987.0f, 0.99f, 0.0f, 4);
        scaleSpringAnimation.addEndListener(this.mListener.mGestureAnimator.createAnimationEndListener(MiuiFreeFormGestureAnimator.SCALE_END_LISTENER));
        MiuiFreeFormGestureAnimator.AnimalLock animalLock3 = animalLock2;
        this.mWindowLocks.put(windowState, animalLock3);
        this.mAnimalLocks.put(scaleSpringAnimation, animalLock3);
        animalLock3.mScaleAnimation = scaleSpringAnimation;
        animalLock3.start(resizeOrientation);
    }

    private void applyShowSmallToFreeFormWindowAnimation(WindowState w, MiuiFreeFormGesturePointerEventListener.WindowStateInfo info, int animationType) {
        WindowState windowState = w;
        MiuiFreeFormGesturePointerEventListener.WindowStateInfo windowStateInfo = info;
        Slog.d(TAG, "applyShowSmallToFreeFormWindowAnimation");
        float nowHeightScale = this.mSmallWindowTargetHScale;
        float nowWidthScale = this.mSmallWindowTargetWScale;
        float nowY = (float) this.mListener.getSmallFreeFormWindowBounds().top;
        float targetY = (float) this.mLastFreeFormWindowStartBounds.top;
        windowStateInfo.mTargetPosY = targetY;
        windowStateInfo.mNowPosY = nowY;
        float nowX = (float) this.mListener.getSmallFreeFormWindowBounds().left;
        float targetX = (float) this.mLastFreeFormWindowStartBounds.left;
        windowStateInfo.mTargetPosX = targetX;
        windowStateInfo.mNowPosX = nowX;
        WindowState windowState2 = w;
        float targetY2 = targetY;
        MiuiFreeFormSpringAnimation scaleXSpringAnimation = this.mListener.mGestureAnimator.createSpringAnimation(windowState2, nowWidthScale, 1.0f, 987.0f, 0.99f, 0.0f, 5);
        float f = nowHeightScale;
        MiuiFreeFormSpringAnimation scaleYSpringAnimation = this.mListener.mGestureAnimator.createSpringAnimation(windowState2, nowHeightScale, 1.0f, 987.0f, 0.99f, 0.0f, 6);
        MiuiFreeFormSpringAnimation tXSpringAnimation = this.mListener.mGestureAnimator.createSpringAnimation(windowState2, nowX, targetX, 987.0f, 0.99f, 0.0f, 2);
        MiuiFreeFormSpringAnimation tYSpringAnimation = this.mListener.mGestureAnimator.createSpringAnimation(windowState2, nowY, targetY2, 987.0f, 0.99f, 0.0f, 3);
        scaleXSpringAnimation.addEndListener(this.mListener.mGestureAnimator.createAnimationEndListener(MiuiFreeFormGestureAnimator.SCALE_X_END_LISTENER));
        scaleYSpringAnimation.addEndListener(this.mListener.mGestureAnimator.createAnimationEndListener(MiuiFreeFormGestureAnimator.SCALE_Y_END_LISTENER));
        tXSpringAnimation.addEndListener(this.mListener.mGestureAnimator.createAnimationEndListener(MiuiFreeFormGestureAnimator.TRANSLATE_X_END_LISTENER));
        tYSpringAnimation.addEndListener(this.mListener.mGestureAnimator.createAnimationEndListener(MiuiFreeFormGestureAnimator.TRANSLATE_Y_END_LISTENER));
        MiuiFreeFormGestureAnimator.AnimalLock animalLock = new MiuiFreeFormGestureAnimator.AnimalLock(windowState);
        this.mWindowLocks.put(windowState, animalLock);
        this.mAnimalLocks.put(scaleXSpringAnimation, animalLock);
        this.mAnimalLocks.put(scaleYSpringAnimation, animalLock);
        this.mAnimalLocks.put(tXSpringAnimation, animalLock);
        this.mAnimalLocks.put(tYSpringAnimation, animalLock);
        animalLock.mScaleXAnimation = scaleXSpringAnimation;
        animalLock.mScaleYAnimation = scaleYSpringAnimation;
        animalLock.mTranslateXAnimation = tXSpringAnimation;
        animalLock.mTranslateYAnimation = tYSpringAnimation;
        animalLock.start(5);
    }

    private void applyLaunchSmallFreeFormWindow(WindowState w, MiuiFreeFormGesturePointerEventListener.WindowStateInfo info, int animationType) {
        float endScaleY;
        float endScaleX;
        WindowState windowState = w;
        Slog.d(TAG, "applyLaunchSmallFreeFormWindow");
        Rect corner = MiuiMultiWindowUtils.findNearestCorner(this.mListener.mService.mContext, 0.0f, 0.0f, 2, this.mListener.mIsLandcapeFreeform);
        float startScaleX = (((float) this.mOriginalBounds.width()) * 1.0f) / ((float) this.mListener.mScreenWidth);
        float startScaleY = (((float) this.mOriginalBounds.height()) * 1.0f) / ((float) this.mListener.mScreenHeight);
        float startLeft = (float) (this.mOriginalBounds.left + (this.mOriginalBounds.width() / 2));
        float startTop = (float) (this.mOriginalBounds.top + (this.mOriginalBounds.height() / 2));
        float endLeft = (float) (corner.left + (corner.width() / 2));
        float endTop = (float) (corner.top + (corner.height() / 2));
        if (this.mListener.mIsLandcapeFreeform) {
            endScaleX = MiuiMultiWindowUtils.LANDCAPE_SMALL_FREEFORM_WIDTH / ((float) this.mListener.mScreenHeight);
            endScaleY = MiuiMultiWindowUtils.LANDCAPE_SMALL_FREEFORM_HEIGHT / ((float) this.mListener.mScreenWidth);
        } else {
            endScaleX = MiuiMultiWindowUtils.SMALL_FREEFORM_WIDTH / ((float) this.mListener.mScreenWidth);
            endScaleY = MiuiMultiWindowUtils.SMALL_FREEFORM_HEIGHT / ((float) this.mListener.mScreenHeight);
        }
        MiuiFreeFormGestureAnimator.AnimalLock animalLock = new MiuiFreeFormGestureAnimator.AnimalLock(windowState);
        WindowState windowState2 = w;
        float endTop2 = endTop;
        Rect rect = corner;
        MiuiFreeFormSpringAnimation scaleXSpringAnimation = this.mListener.mGestureAnimator.createSpringAnimation(windowState2, startScaleX, endScaleX, 438.6491f, 0.95f, 0.0f, 5);
        float f = startScaleX;
        MiuiFreeFormSpringAnimation scaleYSpringAnimation = this.mListener.mGestureAnimator.createSpringAnimation(windowState2, startScaleY, endScaleY, 438.6491f, 0.95f, 0.0f, 6);
        float f2 = startScaleY;
        MiuiFreeFormSpringAnimation tXSpringAnimation = this.mListener.mGestureAnimator.createSpringAnimation(windowState2, startLeft, endLeft, 438.6491f, 0.9f, 0.0f, 2);
        MiuiFreeFormSpringAnimation tYSpringAnimation = this.mListener.mGestureAnimator.createSpringAnimation(windowState2, startTop, endTop2, 246.7401f, 0.9f, 0.0f, 3);
        scaleXSpringAnimation.addEndListener(this.mListener.mGestureAnimator.createAnimationEndListener(MiuiFreeFormGestureAnimator.SCALE_X_END_LISTENER));
        scaleYSpringAnimation.addEndListener(this.mListener.mGestureAnimator.createAnimationEndListener(MiuiFreeFormGestureAnimator.SCALE_Y_END_LISTENER));
        tXSpringAnimation.addEndListener(this.mListener.mGestureAnimator.createAnimationEndListener(MiuiFreeFormGestureAnimator.TRANSLATE_X_END_LISTENER));
        tYSpringAnimation.addEndListener(this.mListener.mGestureAnimator.createAnimationEndListener(MiuiFreeFormGestureAnimator.TRANSLATE_Y_END_LISTENER));
        tYSpringAnimation.addUpdateListener(new MiuiFreeFormDynamicAnimation.OnAnimationUpdateListener() {
            public void onAnimationUpdate(MiuiFreeFormDynamicAnimation animation, float value, float velocity) {
                MiuiFreeFormWindowMotionHelper.this.hideScreenSurface();
            }
        });
        MiuiFreeFormGestureAnimator.AnimalLock animalLock2 = animalLock;
        this.mWindowLocks.put(windowState, animalLock2);
        this.mAnimalLocks.put(scaleXSpringAnimation, animalLock2);
        this.mAnimalLocks.put(scaleYSpringAnimation, animalLock2);
        this.mAnimalLocks.put(tXSpringAnimation, animalLock2);
        this.mAnimalLocks.put(tYSpringAnimation, animalLock2);
        animalLock2.mScaleXAnimation = scaleXSpringAnimation;
        animalLock2.mScaleYAnimation = scaleYSpringAnimation;
        animalLock2.mTranslateXAnimation = tXSpringAnimation;
        animalLock2.mTranslateYAnimation = tYSpringAnimation;
        animalLock2.start(animationType);
    }

    private void applyShowFreeFormWindowAnimation(WindowState w, MiuiFreeFormGesturePointerEventListener.WindowStateInfo info, int animationType) {
        WindowState windowState = w;
        MiuiFreeFormGesturePointerEventListener.WindowStateInfo windowStateInfo = info;
        Slog.d(TAG, "applyShowFreeFormWindowAnimation");
        float nowHeightScale = windowStateInfo.mTargetHeightScale;
        float targetHeightScale = windowStateInfo.mNowHeightScale;
        windowStateInfo.mNowHeightScale = nowHeightScale;
        windowStateInfo.mTargetHeightScale = targetHeightScale;
        float nowWidthScale = windowStateInfo.mTargetWidthScale;
        float targetWidthScale = windowStateInfo.mNowWidthScale;
        windowStateInfo.mNowWidthScale = nowWidthScale;
        windowStateInfo.mTargetWidthScale = targetWidthScale;
        float nowY = windowStateInfo.mTargetPosY;
        float tempNowPosY = windowStateInfo.mNowPosY;
        float targetY = tempNowPosY;
        windowStateInfo.mTargetPosY = targetY;
        windowStateInfo.mNowPosY = nowY;
        float nowX = windowStateInfo.mTargetPosX;
        float tempNowPosX = windowStateInfo.mNowPosX;
        float targetX = tempNowPosX;
        windowStateInfo.mTargetPosX = targetX;
        windowStateInfo.mNowPosX = nowX;
        MiuiFreeFormGestureAnimator.AnimalLock animalLock = this.mWindowLocks.get(windowState);
        if (animalLock == null) {
            WindowState windowState2 = w;
            MiuiFreeFormGestureAnimator.AnimalLock animalLock2 = animalLock;
            float targetX2 = targetX;
            float f = tempNowPosX;
            float nowX2 = nowX;
            float targetY2 = targetY;
            float f2 = tempNowPosY;
            MiuiFreeFormSpringAnimation scaleXSpringAnimation = this.mListener.mGestureAnimator.createSpringAnimation(windowState2, nowWidthScale, targetWidthScale, 987.0f, 0.99f, 0.0f, 5);
            float targetHeightScale2 = targetHeightScale;
            MiuiFreeFormSpringAnimation scaleYSpringAnimation = this.mListener.mGestureAnimator.createSpringAnimation(windowState2, nowHeightScale, targetHeightScale, 987.0f, 0.99f, 0.0f, 6);
            float nowHeightScale2 = nowHeightScale;
            MiuiFreeFormSpringAnimation tXSpringAnimation = this.mListener.mGestureAnimator.createSpringAnimation(windowState2, nowX2, targetX2, 987.0f, 0.99f, 0.0f, 2);
            MiuiFreeFormSpringAnimation tYSpringAnimation = this.mListener.mGestureAnimator.createSpringAnimation(windowState2, nowY, targetY2, 987.0f, 0.99f, 0.0f, 3);
            MiuiFreeFormGestureAnimator.AnimalLock animalLock3 = new MiuiFreeFormGestureAnimator.AnimalLock(windowState);
            scaleXSpringAnimation.addEndListener(this.mListener.mGestureAnimator.createAnimationEndListener(MiuiFreeFormGestureAnimator.SCALE_X_END_LISTENER));
            scaleYSpringAnimation.addEndListener(this.mListener.mGestureAnimator.createAnimationEndListener(MiuiFreeFormGestureAnimator.SCALE_Y_END_LISTENER));
            tXSpringAnimation.addEndListener(this.mListener.mGestureAnimator.createAnimationEndListener(MiuiFreeFormGestureAnimator.TRANSLATE_X_END_LISTENER));
            tYSpringAnimation.addEndListener(this.mListener.mGestureAnimator.createAnimationEndListener(MiuiFreeFormGestureAnimator.TRANSLATE_Y_END_LISTENER));
            this.mWindowLocks.put(windowState, animalLock3);
            this.mAnimalLocks.put(scaleXSpringAnimation, animalLock3);
            this.mAnimalLocks.put(scaleYSpringAnimation, animalLock3);
            this.mAnimalLocks.put(tXSpringAnimation, animalLock3);
            this.mAnimalLocks.put(tYSpringAnimation, animalLock3);
            animalLock3.mScaleXAnimation = scaleXSpringAnimation;
            animalLock3.mScaleYAnimation = scaleYSpringAnimation;
            animalLock3.mTranslateXAnimation = tXSpringAnimation;
            animalLock3.mTranslateYAnimation = tYSpringAnimation;
            animalLock3.start(4);
            MiuiFreeFormGestureAnimator.AnimalLock animalLock4 = animalLock3;
            float f3 = nowX2;
            float f4 = targetY2;
            float f5 = targetX2;
            float f6 = targetHeightScale2;
            float f7 = nowHeightScale2;
            return;
        }
        float f8 = tempNowPosX;
        float f9 = nowX;
        float f10 = tempNowPosY;
        MiuiFreeFormGestureAnimator.AnimalLock animalLock5 = animalLock;
        animalLock5.mTranslateXAnimation.getSpring().setStiffness(987.0f);
        animalLock5.mTranslateXAnimation.getSpring().setDampingRatio(0.99f);
        animalLock5.mTranslateXAnimation.setStartValue(nowX);
        animalLock5.mTranslateXAnimation.animateToFinalPosition(targetX);
        animalLock5.mTranslateYAnimation.getSpring().setStiffness(987.0f);
        animalLock5.mTranslateYAnimation.getSpring().setDampingRatio(0.99f);
        animalLock5.mTranslateYAnimation.setStartValue(nowY);
        animalLock5.mTranslateYAnimation.animateToFinalPosition(targetY);
        animalLock5.mScaleXAnimation.getSpring().setStiffness(987.0f);
        animalLock5.mScaleXAnimation.getSpring().setDampingRatio(0.99f);
        animalLock5.mScaleXAnimation.setStartValue(nowWidthScale);
        animalLock5.mScaleXAnimation.animateToFinalPosition(targetWidthScale);
        animalLock5.mScaleYAnimation.getSpring().setStiffness(987.0f);
        animalLock5.mScaleYAnimation.getSpring().setDampingRatio(0.99f);
        animalLock5.mScaleYAnimation.setStartValue(nowHeightScale);
        animalLock5.mScaleYAnimation.animateToFinalPosition(targetHeightScale);
        animalLock5.start(4);
    }

    private void applyCloseAnimation(WindowState w, MiuiFreeFormGesturePointerEventListener.WindowStateInfo info, int animationType) {
        WindowState windowState = w;
        MiuiFreeFormGesturePointerEventListener.WindowStateInfo windowStateInfo = info;
        Slog.d(TAG, "applyCloseAnimation");
        float nowScale = windowStateInfo.mNowScale;
        windowStateInfo.mTargetScale = 0.0f;
        float nowAlpha = windowStateInfo.mNowAlpha;
        windowStateInfo.mTargetAlpha = 0.0f;
        WindowState windowState2 = w;
        MiuiFreeFormSpringAnimation scaleSpringAnimation = this.mListener.mGestureAnimator.createSpringAnimation(windowState2, nowScale, 0.0f, 987.0f, 0.99f, 0.0f, 4);
        MiuiFreeFormSpringAnimation alphaSpringAnimation = this.mListener.mGestureAnimator.createSpringAnimation(windowState2, nowAlpha, 0.0f, 987.0f, 0.99f, 0.0f, 1);
        scaleSpringAnimation.addEndListener(this.mListener.mGestureAnimator.createAnimationEndListener(MiuiFreeFormGestureAnimator.SCALE_END_LISTENER));
        alphaSpringAnimation.addEndListener(this.mListener.mGestureAnimator.createAnimationEndListener(MiuiFreeFormGestureAnimator.ALPHA_END_LISTENER));
        MiuiFreeFormGestureAnimator.AnimalLock animalLock = new MiuiFreeFormGestureAnimator.AnimalLock(windowState);
        this.mWindowLocks.put(windowState, animalLock);
        this.mAnimalLocks.put(scaleSpringAnimation, animalLock);
        this.mAnimalLocks.put(alphaSpringAnimation, animalLock);
        animalLock.mScaleAnimation = scaleSpringAnimation;
        animalLock.mAlphaAnimation = alphaSpringAnimation;
        animalLock.start(animationType);
    }

    private void applyScaleHomeAnimation(WindowState w, MiuiFreeFormGesturePointerEventListener.WindowStateInfo info, int animationType) {
        Slog.d(TAG, "applyCloseAnimation");
        if (w != null) {
            this.mListener.mGestureAnimator.createLeash(w.mAppToken);
            MiuiFreeFormSpringAnimation scaleSpringAnimation = this.mListener.mGestureAnimator.createSpringAnimation(w, 0.9f, 1.0f, 438.6491f, 0.9f, 0.0f, 4);
            scaleSpringAnimation.addEndListener(this.mListener.mGestureAnimator.createAnimationEndListener(MiuiFreeFormGestureAnimator.SCALE_END_LISTENER));
            MiuiFreeFormGestureAnimator.AnimalLock animalLock = new MiuiFreeFormGestureAnimator.AnimalLock(w);
            this.mWindowLocks.put(w, animalLock);
            this.mAnimalLocks.put(scaleSpringAnimation, animalLock);
            animalLock.mScaleAnimation = scaleSpringAnimation;
            animalLock.start(animationType);
        }
    }

    private void applyEnlargeForInputMethodAnimation(WindowState w, MiuiFreeFormGesturePointerEventListener.WindowStateInfo info, int animationType) {
        WindowState windowState = w;
        Slog.d(TAG, "applyNarrowAnimation");
        WindowState windowState2 = w;
        MiuiFreeFormSpringAnimation tYSpringAnimation = this.mListener.mGestureAnimator.createSpringAnimation(windowState2, (float) this.mLastFreeFormWindowStartBounds.centerY(), (float) (this.mLastFreeFormWindowStartBounds.centerY() + this.mOffSetYForInputMethod), 438.64f, 0.9f, 0.0f, 3);
        MiuiFreeFormSpringAnimation scaleXSpringAnimation = this.mListener.mGestureAnimator.createSpringAnimation(windowState2, 1.0f, 1.0f, 246.74f, 0.9f, 0.0f, 5);
        MiuiFreeFormSpringAnimation scaleYSpringAnimation = this.mListener.mGestureAnimator.createSpringAnimation(windowState2, 1.0f, 1.0f, 246.74f, 0.9f, 0.0f, 6);
        MiuiFreeFormGestureAnimator.AnimalLock animalLock = new MiuiFreeFormGestureAnimator.AnimalLock(windowState);
        this.mWindowLocks.put(windowState, animalLock);
        this.mAnimalLocks.put(tYSpringAnimation, animalLock);
        this.mAnimalLocks.put(scaleXSpringAnimation, animalLock);
        this.mAnimalLocks.put(scaleYSpringAnimation, animalLock);
        animalLock.mTranslateYAnimation = tYSpringAnimation;
        animalLock.mScaleXAnimation = scaleXSpringAnimation;
        animalLock.mScaleYAnimation = scaleYSpringAnimation;
        animalLock.mTranslateYAnimation.addEndListener(this.mListener.mGestureAnimator.createAnimationEndListener(MiuiFreeFormGestureAnimator.TRANSLATE_Y_END_LISTENER));
        animalLock.mScaleXAnimation.addEndListener(this.mListener.mGestureAnimator.createAnimationEndListener(MiuiFreeFormGestureAnimator.SCALE_X_END_LISTENER));
        animalLock.mScaleYAnimation.addEndListener(this.mListener.mGestureAnimator.createAnimationEndListener(MiuiFreeFormGestureAnimator.SCALE_Y_END_LISTENER));
        animalLock.start(animationType);
    }

    private void applyNarrowForInputMethodAnimation(WindowState w, MiuiFreeFormGesturePointerEventListener.WindowStateInfo info, int animationType) {
        MiuiFreeFormSpringAnimation tYSpringAnimation;
        WindowState windowState = w;
        Slog.d(TAG, "applyNarrowAnimation");
        if (this.mListener.mTaskPositioner.mWindowDragBounds.isEmpty()) {
            MiuiFreeFormSpringAnimation tYSpringAnimation2 = this.mListener.mGestureAnimator.createSpringAnimation(w, (float) this.mListener.mWindowBounds.centerY(), (float) (this.mListener.mWindowBounds.centerY() - this.mOffSetYForInputMethod), 438.64f, 0.9f, 0.0f, 3);
            this.mLastFreeFormWindowStartBounds.set(this.mListener.mWindowBounds);
            tYSpringAnimation = tYSpringAnimation2;
        } else {
            MiuiFreeFormSpringAnimation tYSpringAnimation3 = this.mListener.mGestureAnimator.createSpringAnimation(w, (float) this.mListener.mTaskPositioner.mWindowDragBounds.centerY(), (float) (this.mListener.mTaskPositioner.mWindowDragBounds.centerY() + this.mOffSetYForInputMethod), 438.64f, 0.9f, 0.0f, 3);
            this.mLastFreeFormWindowStartBounds.set(this.mListener.mTaskPositioner.mWindowDragBounds);
            tYSpringAnimation = tYSpringAnimation3;
        }
        WindowState windowState2 = w;
        MiuiFreeFormSpringAnimation scaleXSpringAnimation = this.mListener.mGestureAnimator.createSpringAnimation(windowState2, 1.0f, 1.0f, 246.74f, 0.9f, 0.0f, 5);
        MiuiFreeFormSpringAnimation scaleYSpringAnimation = this.mListener.mGestureAnimator.createSpringAnimation(windowState2, 1.0f, 1.0f, 246.74f, 0.9f, 0.0f, 6);
        MiuiFreeFormGestureAnimator.AnimalLock animalLock = new MiuiFreeFormGestureAnimator.AnimalLock(windowState);
        this.mWindowLocks.put(windowState, animalLock);
        this.mAnimalLocks.put(tYSpringAnimation, animalLock);
        this.mAnimalLocks.put(scaleXSpringAnimation, animalLock);
        this.mAnimalLocks.put(scaleYSpringAnimation, animalLock);
        animalLock.mTranslateYAnimation = tYSpringAnimation;
        animalLock.mScaleXAnimation = scaleXSpringAnimation;
        animalLock.mScaleYAnimation = scaleYSpringAnimation;
        animalLock.mTranslateYAnimation.addEndListener(this.mListener.mGestureAnimator.createAnimationEndListener(MiuiFreeFormGestureAnimator.TRANSLATE_Y_END_LISTENER));
        animalLock.mScaleXAnimation.addEndListener(this.mListener.mGestureAnimator.createAnimationEndListener(MiuiFreeFormGestureAnimator.SCALE_X_END_LISTENER));
        animalLock.mScaleYAnimation.addEndListener(this.mListener.mGestureAnimator.createAnimationEndListener(MiuiFreeFormGestureAnimator.SCALE_Y_END_LISTENER));
        animalLock.start(animationType);
    }

    private ValueAnimator applyAlphaAnimation(WindowState w, MiuiFreeFormGesturePointerEventListener.WindowStateInfo info, int animationType) {
        Slog.d(TAG, "applyAlphaAnimation");
        ValueAnimator animator = ValueAnimator.ofPropertyValuesHolder(new PropertyValuesHolder[]{PropertyValuesHolder.ofFloat("ALPHA", new float[]{1.0f, 0.0f})});
        animator.setInterpolator(new LinearInterpolator());
        animator.setDuration(300);
        animator.addUpdateListener(new GestureAnimatorUpdateListener(animator, w, info, animationType));
        return animator;
    }

    private ValueAnimator applyOpenAnimation(WindowState w, MiuiFreeFormGesturePointerEventListener.WindowStateInfo info, int animationType) {
        boolean z;
        WindowState windowState = w;
        MiuiFreeFormGesturePointerEventListener.WindowStateInfo windowStateInfo = info;
        Slog.d(TAG, "applyOpenAnimation");
        float nowHeight = this.mWindowHeight * windowStateInfo.mNowScale;
        float nowWidth = this.mWindowWidth * windowStateInfo.mNowScale;
        float nowHeightScale = nowHeight / ((float) this.mListener.mScreenHeight);
        windowStateInfo.mNowHeightScale = nowHeightScale;
        windowStateInfo.mTargetHeightScale = 1.0f;
        float nowWidthScale = nowWidth / ((float) this.mListener.mScreenWidth);
        windowStateInfo.mNowWidthScale = nowWidthScale;
        windowStateInfo.mTargetWidthScale = 1.0f;
        float nowX = windowStateInfo.mNowPosX;
        float nowY = windowStateInfo.mNowPosY;
        try {
            showScreenSurface();
            this.mListener.mGestureAnimator.hideAppWindowToken(windowState.mAppToken);
            MiuiMultiWindowUtils.sScale = 1.0f;
            windowState.mAttrs.privateFlags |= 64;
            z = true;
            try {
                this.mListener.mService.mActivityManager.resizeTask(this.mListener.mTaskId, new Rect(0, 0, this.mListener.mScreenWidth, this.mListener.mScreenHeight), 0);
                long endTime = System.currentTimeMillis() + 100;
                while (true) {
                    long timeRemaining = endTime - System.currentTimeMillis();
                    if (timeRemaining <= 0) {
                        break;
                    }
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                    }
                    long j = timeRemaining;
                }
                this.mListener.mGestureAnimator.setMatrixInTransaction(windowState.mAppToken, nowWidthScale, 0.0f, 0.0f, nowHeightScale);
                this.mListener.mGestureAnimator.applyTransaction();
                this.mListener.mGestureAnimator.showAppWindowToken(windowState.mAppToken);
                hideScreenSurface();
            } catch (Exception e2) {
                e = e2;
            }
        } catch (Exception e3) {
            e = e3;
            z = true;
            e.printStackTrace();
            this.mScaleAnimator = new ScaleAnimation(nowWidthScale, 1.0f, nowHeightScale, 1.0f, nowX / (1.0f - nowWidthScale), nowY / (1.0f - nowHeightScale));
            this.mScaleAnimator.setDuration((long) 350);
            this.mScaleAnimator.setZAdjustment(1);
            this.mScaleAnimator.setInterpolator(MiuiFreeFormGestureAnimator.QUINT_EASE_OUT_INTERPOLATOR);
            this.mScaleAnimator.initialize(this.mListener.mScreenWidth, this.mListener.mScreenHeight, this.mListener.mScreenWidth, this.mListener.mScreenHeight);
            MiuiFreeFormGestureAnimator.SfValueAnimator sfValueAnimator = new MiuiFreeFormGestureAnimator.SfValueAnimator();
            MiuiFreeFormGestureAnimator.SfValueAnimator sfValueAnimator2 = sfValueAnimator;
            sfValueAnimator2.setDuration((long) 350);
            float f = nowY;
            float f2 = nowX;
            boolean z2 = z;
            float f3 = nowWidthScale;
            float f4 = nowHeightScale;
            sfValueAnimator2.addUpdateListener(new GestureAnimatorUpdateListener(sfValueAnimator, w, info, animationType));
            return sfValueAnimator2;
        }
        this.mScaleAnimator = new ScaleAnimation(nowWidthScale, 1.0f, nowHeightScale, 1.0f, nowX / (1.0f - nowWidthScale), nowY / (1.0f - nowHeightScale));
        this.mScaleAnimator.setDuration((long) 350);
        this.mScaleAnimator.setZAdjustment(1);
        this.mScaleAnimator.setInterpolator(MiuiFreeFormGestureAnimator.QUINT_EASE_OUT_INTERPOLATOR);
        this.mScaleAnimator.initialize(this.mListener.mScreenWidth, this.mListener.mScreenHeight, this.mListener.mScreenWidth, this.mListener.mScreenHeight);
        MiuiFreeFormGestureAnimator.SfValueAnimator sfValueAnimator3 = new MiuiFreeFormGestureAnimator.SfValueAnimator();
        MiuiFreeFormGestureAnimator.SfValueAnimator sfValueAnimator22 = sfValueAnimator3;
        sfValueAnimator22.setDuration((long) 350);
        float f5 = nowY;
        float f22 = nowX;
        boolean z22 = z;
        float f32 = nowWidthScale;
        float f42 = nowHeightScale;
        sfValueAnimator22.addUpdateListener(new GestureAnimatorUpdateListener(sfValueAnimator3, w, info, animationType));
        return sfValueAnimator22;
    }

    private ValueAnimator applyDirectOpenAnimation(WindowState w, MiuiFreeFormGesturePointerEventListener.WindowStateInfo info, int animationType) {
        WindowState windowState = w;
        Slog.d(TAG, "applyDirectOpenAnimation");
        float nowHeightScale = this.mWindowHeight / ((float) this.mListener.mScreenHeight);
        float nowWidthScale = this.mWindowWidth / ((float) this.mListener.mScreenWidth);
        float nowX = ((float) this.mListener.mWindowBounds.left) + (this.mWindowWidth / 2.0f);
        float nowY = ((float) this.mListener.mWindowBounds.top) + (this.mWindowHeight / 2.0f);
        this.mTopFullScreenWindow = getTopWindowInFullScreenMode();
        try {
            showScreenSurface();
            this.mListener.mGestureAnimator.hideAppWindowToken(windowState.mAppToken);
            MiuiMultiWindowUtils.sScale = 1.0f;
            windowState.mAttrs.privateFlags |= 64;
            this.mListener.mService.mActivityManager.resizeTask(this.mListener.mTaskId, new Rect(0, 0, this.mListener.mScreenWidth, this.mListener.mScreenHeight), 0);
            long endTime = System.currentTimeMillis() + 100;
            while (true) {
                long timeRemaining = endTime - System.currentTimeMillis();
                if (timeRemaining <= 0) {
                    break;
                }
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                }
                long j = timeRemaining;
            }
            this.mListener.mGestureAnimator.setMatrixInTransaction(windowState.mAppToken, nowWidthScale, 0.0f, 0.0f, nowHeightScale);
            this.mListener.mGestureAnimator.applyTransaction();
            this.mListener.mGestureAnimator.showAppWindowToken(windowState.mAppToken);
            hideScreenSurface();
        } catch (Exception e2) {
            e2.printStackTrace();
        }
        MiuiFreeFormSpringAnimation tXSpringAnimation = this.mListener.mGestureAnimator.createSpringAnimation(w, nowX, (float) (this.mListener.mScreenWidth / 2), 438.64f, 0.9f, 0.0f, 2);
        WindowState windowState2 = w;
        float f = nowWidthScale;
        float f2 = nowWidthScale;
        MiuiFreeFormSpringAnimation tYSpringAnimation = this.mListener.mGestureAnimator.createSpringAnimation(windowState2, nowY, (float) (this.mListener.mScreenHeight / 2), 438.64f, 0.9f, 0.0f, 3);
        MiuiFreeFormSpringAnimation scaleXSpringAnimation = this.mListener.mGestureAnimator.createSpringAnimation(windowState2, f, 1.0f, 246.74f, 0.9f, 0.0f, 5);
        float f3 = nowHeightScale;
        float f4 = nowHeightScale;
        MiuiFreeFormSpringAnimation scaleXSpringAnimation2 = scaleXSpringAnimation;
        MiuiFreeFormSpringAnimation scaleYSpringAnimation = this.mListener.mGestureAnimator.createSpringAnimation(windowState2, f3, 1.0f, 246.74f, 0.9f, 0.0f, 6);
        MiuiFreeFormGestureAnimator.AnimalLock animalLock = new MiuiFreeFormGestureAnimator.AnimalLock(windowState);
        this.mWindowLocks.put(windowState, animalLock);
        this.mAnimalLocks.put(tXSpringAnimation, animalLock);
        this.mAnimalLocks.put(tYSpringAnimation, animalLock);
        this.mAnimalLocks.put(scaleXSpringAnimation2, animalLock);
        this.mAnimalLocks.put(scaleYSpringAnimation, animalLock);
        animalLock.mTranslateXAnimation = tXSpringAnimation;
        animalLock.mTranslateYAnimation = tYSpringAnimation;
        animalLock.mScaleXAnimation = scaleXSpringAnimation2;
        animalLock.mScaleYAnimation = scaleYSpringAnimation;
        animalLock.mTranslateXAnimation.addEndListener(this.mListener.mGestureAnimator.createAnimationEndListener(MiuiFreeFormGestureAnimator.TRANSLATE_X_END_LISTENER));
        animalLock.mTranslateYAnimation.addEndListener(this.mListener.mGestureAnimator.createAnimationEndListener(MiuiFreeFormGestureAnimator.TRANSLATE_Y_END_LISTENER));
        animalLock.mScaleXAnimation.addEndListener(this.mListener.mGestureAnimator.createAnimationEndListener(MiuiFreeFormGestureAnimator.SCALE_X_END_LISTENER));
        animalLock.mScaleYAnimation.addEndListener(this.mListener.mGestureAnimator.createAnimationEndListener(MiuiFreeFormGestureAnimator.SCALE_Y_END_LISTENER));
        animalLock.start(animationType);
        return null;
    }

    private WindowState getTopWindowInFullScreenMode() {
        Task task;
        AppWindowToken appWindowToken;
        try {
            TaskStack taskStack = this.mListener.mGestureController.mDisplayContent.getTopStackInWindowingMode(1);
            if (taskStack == null || (task = (Task) taskStack.getTopChild()) == null || (appWindowToken = (AppWindowToken) task.getTopChild()) == null) {
                return null;
            }
            return appWindowToken.findMainWindow(false);
        } catch (Exception e) {
            e.toString();
            return null;
        }
    }

    private void applyDims(float dimAmount, WindowState window) {
        if (window != null) {
            SurfaceControl.Transaction t = window.getPendingTransaction();
            if (MiuiFreeFormGestureController.DEBUG) {
                Slog.d(TAG, "applyDims dimAmount=" + dimAmount + " window:" + window + " dimmerLayer:" + this.mDimmerLayer);
            }
            if (this.mDimmerLayer != null && window.getSurfaceControl() != null) {
                Rect dimBounds = window.getBounds();
                window.getPendingTransaction().setRelativeLayer(this.mDimmerLayer, window.getSurfaceControl(), 1);
                t.setPosition(this.mDimmerLayer, (float) dimBounds.left, (float) dimBounds.top);
                t.setWindowCrop(this.mDimmerLayer, dimBounds.width(), dimBounds.height());
                t.show(this.mDimmerLayer);
                t.setAlpha(this.mDimmerLayer, dimAmount);
                t.apply();
            }
        }
    }

    private void removeDimLayer() {
        SurfaceControl.Transaction t = this.mListener.mService.mTransactionFactory.make();
        if (this.mDimmerLayer != null) {
            Slog.d(TAG, "removeDimLayer dimmerLayer:" + this.mDimmerLayer);
            t.remove(this.mDimmerLayer);
            t.apply();
        }
    }

    public SurfaceControl makeDimLayer(WindowState window) {
        removeDimLayer();
        if (window == null || window.getTask() == null) {
            return null;
        }
        SurfaceControl.Builder colorLayer = window.getTask().makeChildSurface((WindowContainer) null).setParent(window.getTask().getSurfaceControl()).setColorLayer();
        return colorLayer.setName("freeform Dim Layer for - " + window.getName()).build();
    }

    private ValueAnimator applySmallWindowOpenAnimation(WindowState w, MiuiFreeFormGesturePointerEventListener.WindowStateInfo info, int animationType) {
        WindowState windowState = w;
        MiuiFreeFormGesturePointerEventListener.WindowStateInfo windowStateInfo = info;
        Slog.d(TAG, "applySmallWindowOpenAnimation");
        float nowHeightScale = MiuiMultiWindowUtils.SMALL_FREEFORM_HEIGHT / ((float) this.mListener.mScreenHeight);
        windowStateInfo.mNowHeightScale = nowHeightScale;
        windowStateInfo.mTargetHeightScale = 1.0f;
        float nowWidthScale = MiuiMultiWindowUtils.SMALL_FREEFORM_WIDTH / ((float) this.mListener.mScreenWidth);
        windowStateInfo.mNowWidthScale = nowWidthScale;
        windowStateInfo.mTargetWidthScale = 1.0f;
        this.mTopFullScreenWindow = getTopWindowInFullScreenMode();
        try {
            showScreenSurface();
            this.mListener.mGestureAnimator.hideAppWindowToken(windowState.mAppToken);
            MiuiMultiWindowUtils.sScale = 1.0f;
            windowState.mAttrs.privateFlags |= 64;
            this.mListener.mService.mActivityManager.resizeTask(this.mListener.mTaskId, new Rect(0, 0, this.mListener.mScreenWidth, this.mListener.mScreenHeight), 0);
            long endTime = System.currentTimeMillis() + 100;
            while (true) {
                long timeRemaining = endTime - System.currentTimeMillis();
                if (timeRemaining <= 0) {
                    break;
                }
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                }
                long j = timeRemaining;
            }
            this.mListener.mGestureAnimator.setMatrixInTransaction(windowState.mAppToken, nowWidthScale, 0.0f, 0.0f, nowHeightScale);
            this.mListener.mGestureAnimator.applyTransaction();
            this.mListener.mGestureAnimator.showAppWindowToken(windowState.mAppToken);
            hideScreenSurface();
        } catch (Exception e2) {
            e2.printStackTrace();
        }
        MiuiFreeFormSpringAnimation tXSpringAnimation = this.mListener.mGestureAnimator.createSpringAnimation(w, (float) (this.mListener.getSmallFreeFormWindowBounds().left + (this.mListener.getSmallFreeFormWindowBounds().width() / 2)), (float) (this.mListener.mScreenWidth / 2), 438.64f, 0.9f, 0.0f, 2);
        WindowState windowState2 = w;
        MiuiFreeFormSpringAnimation tYSpringAnimation = this.mListener.mGestureAnimator.createSpringAnimation(windowState2, (float) (this.mListener.getSmallFreeFormWindowBounds().top + (this.mListener.getSmallFreeFormWindowBounds().height() / 2)), (float) (this.mListener.mScreenHeight / 2), 438.64f, 0.9f, 0.0f, 3);
        float f = nowHeightScale;
        float f2 = nowHeightScale;
        MiuiFreeFormSpringAnimation scaleXSpringAnimation = this.mListener.mGestureAnimator.createSpringAnimation(windowState2, nowWidthScale, 1.0f, 246.74f, 0.9f, 0.0f, 5);
        MiuiFreeFormSpringAnimation scaleYSpringAnimation = this.mListener.mGestureAnimator.createSpringAnimation(windowState2, f, 1.0f, 246.74f, 0.9f, 0.0f, 6);
        MiuiFreeFormSpringAnimation alphaSpringAnimation = this.mListener.mGestureAnimator.createSpringAnimation(windowState2, 0.0f, 1.0f, 246.74f, 0.9f, 0.0f, 1);
        MiuiFreeFormGestureAnimator.AnimalLock animalLock = new MiuiFreeFormGestureAnimator.AnimalLock(windowState);
        this.mWindowLocks.put(windowState, animalLock);
        this.mAnimalLocks.put(tXSpringAnimation, animalLock);
        this.mAnimalLocks.put(tYSpringAnimation, animalLock);
        this.mAnimalLocks.put(scaleXSpringAnimation, animalLock);
        this.mAnimalLocks.put(scaleYSpringAnimation, animalLock);
        this.mAnimalLocks.put(alphaSpringAnimation, animalLock);
        animalLock.mTranslateXAnimation = tXSpringAnimation;
        animalLock.mTranslateYAnimation = tYSpringAnimation;
        animalLock.mScaleXAnimation = scaleXSpringAnimation;
        animalLock.mScaleYAnimation = scaleYSpringAnimation;
        animalLock.mAlphaAnimation = alphaSpringAnimation;
        animalLock.mTranslateXAnimation.addEndListener(this.mListener.mGestureAnimator.createAnimationEndListener(MiuiFreeFormGestureAnimator.TRANSLATE_X_END_LISTENER));
        animalLock.mTranslateYAnimation.addEndListener(this.mListener.mGestureAnimator.createAnimationEndListener(MiuiFreeFormGestureAnimator.TRANSLATE_Y_END_LISTENER));
        animalLock.mScaleXAnimation.addEndListener(this.mListener.mGestureAnimator.createAnimationEndListener(MiuiFreeFormGestureAnimator.SCALE_X_END_LISTENER));
        animalLock.mScaleYAnimation.addEndListener(this.mListener.mGestureAnimator.createAnimationEndListener(MiuiFreeFormGestureAnimator.SCALE_Y_END_LISTENER));
        animalLock.mAlphaAnimation.addEndListener(this.mListener.mGestureAnimator.createAnimationEndListener(MiuiFreeFormGestureAnimator.ALPHA_END_LISTENER));
        animalLock.start(animationType);
        return null;
    }

    private class GestureAnimatorUpdateListener implements ValueAnimator.AnimatorUpdateListener {
        private int mAnimationType;
        private ValueAnimator mAnimator;
        private MiuiFreeFormGesturePointerEventListener.WindowStateInfo mInfo;
        private WindowState mWin;

        GestureAnimatorUpdateListener(ValueAnimator animator, WindowState w, MiuiFreeFormGesturePointerEventListener.WindowStateInfo info, int animationType) {
            this.mAnimator = animator;
            this.mWin = w;
            this.mInfo = info;
            this.mAnimationType = animationType;
        }

        public void onAnimationUpdate(ValueAnimator animation) {
            AppWindowToken aToken;
            long currentPlayTime;
            long currentPlayTime2;
            ValueAnimator valueAnimator = animation;
            try {
                Slog.d(MiuiFreeFormWindowMotionHelper.TAG, "onAnimationUpdate animation:" + valueAnimator);
                if (this.mWin == null) {
                    return;
                }
                if (this.mAnimator != null) {
                    int type = this.mWin.mAttrs.type;
                    if ((type == 1 || type == 3) && (aToken = this.mWin.mAppToken) != null) {
                        if (this.mAnimationType == 0) {
                            int i = type;
                        } else {
                            if (MiuiFreeFormWindowMotionHelper.this.mCurrentAnimation == 1) {
                                long duration = animation.getDuration();
                                long currentPlayTime3 = animation.getCurrentPlayTime();
                                if (currentPlayTime3 != 0) {
                                    if (currentPlayTime3 != 0 && MiuiFreeFormWindowMotionHelper.this.mAnimationStartTime == 0) {
                                        long unused = MiuiFreeFormWindowMotionHelper.this.mAnimationStartTime = currentPlayTime3;
                                    }
                                    long currentPlayTime4 = currentPlayTime3 - MiuiFreeFormWindowMotionHelper.this.mAnimationStartTime;
                                    if (currentPlayTime4 > duration) {
                                        currentPlayTime2 = duration;
                                    } else {
                                        currentPlayTime2 = currentPlayTime4;
                                    }
                                    MiuiFreeFormGestureAnimator.TmpValues tmp = new MiuiFreeFormGestureAnimator.TmpValues();
                                    MiuiFreeFormWindowMotionHelper.this.mScaleAnimator.getTransformation(currentPlayTime2, tmp.transformation);
                                    Matrix matrix = tmp.transformation.getMatrix();
                                    matrix.getValues(tmp.floats);
                                    Slog.d(MiuiFreeFormWindowMotionHelper.TAG, "apply currentPlayTime:" + currentPlayTime2 + " Matrix:" + matrix + " x:" + tmp.floats[2] + " y:" + tmp.floats[5] + " scaleY:" + tmp.floats[4] + " scaleX:" + tmp.floats[0]);
                                    MiuiFreeFormWindowMotionHelper.this.mListener.mGestureAnimator.setMatrixInTransaction(aToken, matrix, tmp.floats);
                                } else {
                                    return;
                                }
                            } else if (this.mAnimationType != 2) {
                                if (this.mAnimationType == 13) {
                                    if (MiuiFreeFormWindowMotionHelper.this.mSurfaceControl != null) {
                                        MiuiFreeFormWindowMotionHelper.this.mListener.mGestureAnimator.setAlphaInTransaction(MiuiFreeFormWindowMotionHelper.this.mSurfaceControl, ((Float) valueAnimator.getAnimatedValue("ALPHA")).floatValue());
                                        int i2 = type;
                                    } else {
                                        int i3 = type;
                                    }
                                } else if (this.mAnimationType == 3) {
                                    int i4 = type;
                                } else if (this.mAnimationType == 4) {
                                    int i5 = type;
                                } else if (this.mAnimationType == 5) {
                                    int i6 = type;
                                } else if (this.mAnimationType == 6) {
                                    long duration2 = animation.getDuration();
                                    long currentPlayTime5 = animation.getCurrentPlayTime();
                                    if (currentPlayTime5 != 0) {
                                        if (currentPlayTime5 != 0 && MiuiFreeFormWindowMotionHelper.this.mAnimationStartTime == 0) {
                                            long unused2 = MiuiFreeFormWindowMotionHelper.this.mAnimationStartTime = currentPlayTime5;
                                        }
                                        long currentPlayTime6 = currentPlayTime5 - MiuiFreeFormWindowMotionHelper.this.mAnimationStartTime;
                                        if (currentPlayTime6 > duration2) {
                                            currentPlayTime = duration2;
                                        } else {
                                            currentPlayTime = currentPlayTime6;
                                        }
                                        MiuiFreeFormGestureAnimator.TmpValues tmp2 = new MiuiFreeFormGestureAnimator.TmpValues();
                                        int i7 = type;
                                        MiuiFreeFormWindowMotionHelper.this.mScaleAnimator.getTransformation(currentPlayTime, tmp2.transformation);
                                        Matrix matrix2 = tmp2.transformation.getMatrix();
                                        matrix2.getValues(tmp2.floats);
                                        Slog.d(MiuiFreeFormWindowMotionHelper.TAG, "apply currentPlayTime:" + currentPlayTime + " Matrix:" + matrix2 + " x:" + tmp2.floats[2] + " y:" + tmp2.floats[5] + " scaleY:" + tmp2.floats[4] + " scaleX:" + tmp2.floats[0]);
                                        MiuiFreeFormWindowMotionHelper.this.mListener.mGestureAnimator.setMatrixInTransaction(aToken, matrix2, tmp2.floats);
                                    } else {
                                        return;
                                    }
                                }
                            }
                            int i8 = type;
                        }
                        MiuiFreeFormWindowMotionHelper.this.mListener.mGestureAnimator.applyTransaction();
                    }
                }
            } catch (Exception e) {
                Slog.w(MiuiFreeFormWindowMotionHelper.TAG, "onAnimationUpdate exception", e);
            }
        }
    }

    public void startGestureAnimation(int gestureAnimation) {
        WindowState w;
        MiuiFreeFormGesturePointerEventListener.WindowStateInfo wInfo;
        Collection<Animator> animatorItems = new HashSet<>();
        if (this.mListener.mScalingWindows != null && !this.mListener.mScalingWindows.isEmpty()) {
            Iterator<Map.Entry<WindowState, MiuiFreeFormGesturePointerEventListener.WindowStateInfo>> it = this.mListener.mScalingWindows.entrySet().iterator();
            while (true) {
                if (!it.hasNext()) {
                    break;
                }
                Map.Entry<WindowState, MiuiFreeFormGesturePointerEventListener.WindowStateInfo> winEntry = it.next();
                w = winEntry.getKey();
                wInfo = winEntry.getValue();
                ValueAnimator a = null;
                if (gestureAnimation == 1) {
                    if (this.mListener.mIsPortrait) {
                        a = applyOpenAnimation(w, wInfo, 1);
                    }
                } else if (gestureAnimation == 0) {
                    applyCloseAnimation(w, wInfo, 0);
                } else if (gestureAnimation == 2) {
                    applyResetAnimation(w, wInfo, 2);
                } else if (gestureAnimation == 3) {
                    applyShowSmallWindowAnimation(w, wInfo, 3);
                } else if (gestureAnimation == 4) {
                    applyShowFreeFormWindowAnimation(w, wInfo, 4);
                } else if (gestureAnimation == 5) {
                    applyShowSmallToFreeFormWindowAnimation(w, wInfo, 5);
                } else if (gestureAnimation == 6) {
                    applySmallWindowOpenAnimation(w, wInfo, 6);
                } else if (gestureAnimation == 10 || gestureAnimation == 11) {
                    applyResizeBackAnimation(w, wInfo, gestureAnimation);
                } else if (gestureAnimation == 12) {
                    applyLaunchSmallFreeFormWindow(w, wInfo, gestureAnimation);
                } else if (gestureAnimation == 13) {
                    a = applyAlphaAnimation(w, wInfo, gestureAnimation);
                } else if (gestureAnimation == 14) {
                    applyScaleHomeAnimation(this.mListener.mHomeWindow, wInfo, gestureAnimation);
                    break;
                } else if (gestureAnimation == 16) {
                    applyNarrowForInputMethodAnimation(w, wInfo, gestureAnimation);
                    break;
                } else if (gestureAnimation == 15) {
                    applyEnlargeForInputMethodAnimation(w, wInfo, gestureAnimation);
                    break;
                } else if (gestureAnimation == 17) {
                    applyDirectOpenAnimation(w, wInfo, gestureAnimation);
                }
                if (a != null) {
                    animatorItems.add(a);
                }
            }
            applyResizeBackAnimation(w, wInfo, gestureAnimation);
            this.mCurrentAnimation = gestureAnimation;
            if (gestureAnimation == 1 && !this.mListener.mIsPortrait) {
                this.mCurrentAnimation = -1;
            }
            Slog.w(TAG, "startGestureAnimation mCurrentAnimation:" + this.mCurrentAnimation);
        }
        if (animatorItems.size() > 0) {
            this.mGestureAnimatorSet = new AnimatorSet();
            this.mGestureAnimatorSet.addListener(new Animator.AnimatorListener() {
                public void onAnimationStart(Animator animation) {
                    Slog.w(MiuiFreeFormWindowMotionHelper.TAG, "start animation");
                }

                public void onAnimationRepeat(Animator animation) {
                }

                public void onAnimationEnd(Animator animation) {
                    try {
                        if (MiuiFreeFormWindowMotionHelper.this.mCurrentAnimation != 0) {
                            if (MiuiFreeFormWindowMotionHelper.this.mCurrentAnimation == 1) {
                                MiuiFreeFormWindowMotionHelper.this.startShowFullScreenWindow();
                            } else if (MiuiFreeFormWindowMotionHelper.this.mCurrentAnimation != 2) {
                                if (MiuiFreeFormWindowMotionHelper.this.mCurrentAnimation != 3) {
                                    if (MiuiFreeFormWindowMotionHelper.this.mCurrentAnimation != 4) {
                                        if (MiuiFreeFormWindowMotionHelper.this.mCurrentAnimation != 5) {
                                            if (MiuiFreeFormWindowMotionHelper.this.mCurrentAnimation != 6) {
                                                if (MiuiFreeFormWindowMotionHelper.this.mCurrentAnimation == 13) {
                                                    MiuiFreeFormWindowMotionHelper.this.hideScreenSurface();
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        Slog.w(MiuiFreeFormWindowMotionHelper.TAG, "mCurrentAnimation" + MiuiFreeFormWindowMotionHelper.this.mCurrentAnimation + "end");
                        MiuiFreeFormWindowMotionHelper.this.mCurrentAnimation = -1;
                    } catch (Exception e) {
                        Slog.w(MiuiFreeFormWindowMotionHelper.TAG, "Animation end exception", e);
                    }
                    long unused = MiuiFreeFormWindowMotionHelper.this.mAnimationStartTime = 0;
                }

                public void onAnimationCancel(Animator animation) {
                    Slog.w(MiuiFreeFormWindowMotionHelper.TAG, "cancel go back animation");
                }
            });
            this.mGestureAnimatorSet.playTogether(animatorItems);
            this.mGestureAnimatorSet.start();
        }
    }

    /* access modifiers changed from: package-private */
    public float afterFriction(float x, float range) {
        float per = Math.min(x / range, 1.0f);
        return ((((((per * 13.0f) * per) * per) / 75.0f) - (((per * 13.0f) * per) / 25.0f)) + ((13.0f * per) / 25.0f)) * range;
    }

    private void calculateWindowScale(float rowX, float rowY, float windowHeight, int gestureAction) {
        float offsetY = Math.abs(rowY - this.mDownY);
        float range = 0.0f;
        float maxScale = 0.0f;
        if (gestureAction == 0) {
            if (this.mListener.mIsPortrait) {
                range = (float) ((this.mListener.mScreenHeight - this.mListener.mWindowBounds.top) - MiuiMultiWindowUtils.FREEFORM_PORTRAIT_HEIGHT);
                this.mCurrentScale = (afterFriction(offsetY, range) / range) + 1.0f;
            } else {
                maxScale = (((((float) this.mListener.mScreenHeight) * 1.0f) - MiuiMultiWindowUtils.FREEFORM_LANDCAPE_RESERVE_MARGINS) / ((float) MiuiMultiWindowUtils.FREEFORM_LANDCAPE_HEIGHT)) * 1.0f;
                this.mCurrentScale = ((((maxScale - 1.0f) * offsetY) * 2.0f) / (((((float) this.mListener.mScreenHeight) * 1.0f) - (((float) MiuiMultiWindowUtils.FREEFORM_LANDCAPE_HEIGHT) * 1.0f)) - MiuiMultiWindowUtils.FREEFORM_LANDCAPE_RESERVE_MARGINS)) + 1.0f;
                if (this.mCurrentScale > maxScale) {
                    this.mCurrentScale = maxScale;
                }
            }
            if (MiuiFreeFormGestureController.DEBUG) {
                Slog.d(TAG, "isPortrait" + this.mListener.mIsPortrait + "mCurrentScale:" + this.mCurrentScale + " offsetY:" + offsetY + " maxScale:" + maxScale + " range:" + range);
            }
        } else if (gestureAction == 1) {
            if (this.mListener.mIsPortrait) {
                this.mCurrentScale = 1.0f - (afterFriction(offsetY, (float) this.mListener.mScreenHeight) / ((float) MiuiMultiWindowUtils.FREEFORM_PORTRAIT_HEIGHT));
            } else {
                this.mCurrentScale = 1.0f - (afterFriction(offsetY, (float) this.mListener.mScreenHeight) / ((float) MiuiMultiWindowUtils.FREEFORM_LANDCAPE_HEIGHT));
            }
            if (MiuiFreeFormGestureController.DEBUG) {
                Slog.d(TAG, "calculateWindowScale currentScale:" + this.mCurrentScale);
            }
        }
    }

    private void calculateWindowAlpha(float rowX, float rowY, float windowHeight, int gestureAction) {
        if (gestureAction == 0) {
            this.mCurrentAlpha = 1.0f;
        } else if (gestureAction == 1) {
            this.mCurrentAlpha = Math.min((this.mCurrentScale - 1.0f) * 2.0f, 0.0f) + 1.0f;
        }
        if (MiuiFreeFormGestureController.DEBUG) {
            Slog.d(TAG, "calculateWindowAlpha currentAlpha" + this.mCurrentAlpha);
        }
    }

    private void calculateWindowPosition(int gestureAction, float currentScale, float windowHeight, float windowWidth) {
        if (gestureAction == 0) {
            this.mCurrentTempY = this.mCurrentCenterPositionY - ((windowHeight * currentScale) / 2.0f);
            this.mCurrentTempX = this.mCurrentCenterPositionX - ((windowWidth * currentScale) / 2.0f);
        } else if (gestureAction == 1) {
            this.mCurrentTempY = this.mCurrentCenterPositionY - ((windowHeight * currentScale) / 2.0f);
            this.mCurrentTempX = this.mCurrentCenterPositionX - ((windowWidth * currentScale) / 2.0f);
        }
        if (MiuiFreeFormGestureController.DEBUG) {
            Slog.d(TAG, "calculateWindowAlpha mCurrentTempY" + this.mCurrentTempY + " mCurrentTempX:" + this.mCurrentTempX);
        }
    }

    public void setAlpha(WindowState w, float alpha) {
        if (MiuiFreeFormGestureController.DEBUG) {
            Slog.d(TAG, "setAlpha value:" + alpha + " w:" + w);
        }
        MiuiFreeFormGestureAnimator.AnimalLock animalLock = this.mWindowLocks.get(w);
        if (animalLock != null && animalLock.mCurrentAnimation != 6) {
            this.mListener.mGestureAnimator.setAlphaInTransaction(w.mAppToken, alpha);
            this.mListener.mGestureAnimator.applyTransaction();
        }
    }

    public void setScale(WindowState w, float scale) {
        if (MiuiFreeFormGestureController.DEBUG) {
            Slog.d(TAG, "setScale value:" + scale + " w:" + w);
        }
        MiuiFreeFormGestureAnimator.AnimalLock animalLock = this.mWindowLocks.get(w);
        if (animalLock != null) {
            AppWindowToken aToken = w.mAppToken;
            if (animalLock.mCurrentAnimation == 0) {
                this.mPosX = this.mCurrentCenterPositionX - ((this.mWindowWidth * scale) / 2.0f);
                this.mPosY = this.mCurrentCenterPositionY - ((this.mWindowHeight * scale) / 2.0f);
                this.mListener.mGestureAnimator.setPositionInTransaction(aToken, this.mPosX, this.mPosY);
            } else if (animalLock.mCurrentAnimation == 2) {
                if (this.mCurrentGestureAction == 1) {
                    this.mPosX = this.mCurrentCenterPositionX - ((this.mWindowWidth * scale) / 2.0f);
                    this.mPosY = this.mCurrentCenterPositionY - ((this.mWindowHeight * scale) / 2.0f);
                    this.mListener.mGestureAnimator.setPositionInTransaction(aToken, this.mPosX, this.mPosY);
                }
            } else if (animalLock.mCurrentAnimation == 10) {
                float f = this.mResizeLeftPosition;
                float f2 = this.mWindowWidth;
                this.mListener.mGestureAnimator.setPositionInTransaction(aToken, (f + f2) - (f2 * scale), this.mResizeTopPosition);
                Slog.d(TAG, "setScale ANIMATION_RESIZE_BACK_LEFT_BOTTOM");
            } else if (animalLock.mCurrentAnimation == 11) {
                Slog.d(TAG, "setScale ANIMATION_RESIZE_BACK_RIGHT_BOTTOM");
            } else if (animalLock.mCurrentAnimation == 14) {
                this.mListener.mGestureAnimator.setPositionInTransaction(aToken, (((float) this.mListener.mScreenWidth) / 2.0f) - ((((float) this.mListener.mScreenWidth) * scale) / 2.0f), (((float) this.mListener.mScreenHeight) / 2.0f) - ((((float) this.mListener.mScreenHeight) * scale) / 2.0f));
            }
            this.mListener.mGestureAnimator.setMatrixInTransaction(aToken, scale, 0.0f, 0.0f, scale);
            this.mListener.mGestureAnimator.applyTransaction();
        }
    }

    public void setXScale(WindowState w, float scale) {
        MiuiFreeFormGestureAnimator.AnimalLock animalLock = this.mWindowLocks.get(w);
        if (animalLock != null) {
            if (MiuiFreeFormGestureController.DEBUG) {
                Slog.d(TAG, "setXScale value:" + scale + " w:" + w + " animalLock:" + animalLock);
            }
            AppWindowToken aToken = w.mAppToken;
            this.mCurrentXScale = scale;
            if (!(animalLock.mCurrentAnimation == 3 || animalLock.mCurrentAnimation == 4 || animalLock.mCurrentAnimation == 8 || animalLock.mCurrentAnimation != 17 || this.mCurrentTempX != ((float) (this.mListener.mScreenWidth / 2)))) {
                this.mPosX = this.mCurrentTempX - ((((float) this.mListener.mScreenWidth) * this.mCurrentXScale) / 2.0f);
                this.mListener.mGestureAnimator.setPositionInTransaction(aToken, this.mPosX, this.mPosY);
            }
            this.mListener.mGestureAnimator.setMatrixInTransaction(aToken, this.mCurrentXScale, 0.0f, 0.0f, this.mCurrentYScale);
            this.mListener.mGestureAnimator.applyTransaction();
        }
    }

    public void setYScale(WindowState w, float scale) {
        MiuiFreeFormGestureAnimator.AnimalLock animalLock = this.mWindowLocks.get(w);
        if (animalLock != null) {
            if (MiuiFreeFormGestureController.DEBUG) {
                Slog.d(TAG, "setYScale value:" + scale + " w:" + w + " animalLock:" + animalLock);
            }
            this.mCurrentYScale = scale;
            AppWindowToken aToken = w.mAppToken;
            if (!(animalLock.mCurrentAnimation == 3 || animalLock.mCurrentAnimation == 4 || animalLock.mCurrentAnimation == 8 || animalLock.mCurrentAnimation != 17 || this.mCurrentTempY != ((float) (this.mListener.mScreenHeight / 2)))) {
                this.mPosY = this.mCurrentTempY - ((((float) this.mListener.mScreenHeight) * this.mCurrentYScale) / 2.0f);
                this.mListener.mGestureAnimator.setPositionInTransaction(aToken, this.mPosX, this.mPosY);
            }
            this.mListener.mGestureAnimator.setMatrixInTransaction(aToken, this.mCurrentXScale, 0.0f, 0.0f, this.mCurrentYScale);
            this.mListener.mGestureAnimator.applyTransaction();
        }
    }

    public void setPositionX(WindowState w, float x) {
        MiuiFreeFormGestureAnimator.AnimalLock animalLock = this.mWindowLocks.get(w);
        if (animalLock != null) {
            if (MiuiFreeFormGestureController.DEBUG) {
                Slog.d(TAG, "setPositionX x:" + x + " w:" + w + " mPosY:" + this.mPosY + " animalLock:" + animalLock + " mCurrentAnimation:" + animalLock.mCurrentAnimation);
            }
            AppWindowToken aToken = w.mAppToken;
            if (animalLock.mCurrentAnimation == 8) {
                this.mPosX = ((this.mWindowWidth * (1.0f - this.mCurrentXScale)) / 2.0f) + x;
            } else if (animalLock.mCurrentAnimation == 3) {
                this.mPosX = x;
            } else if (animalLock.mCurrentAnimation != 4) {
                if (animalLock.mCurrentAnimation == 6) {
                    this.mPosX = x - ((((float) this.mListener.mScreenWidth) * this.mCurrentXScale) / 2.0f);
                } else if (animalLock.mCurrentAnimation == 17) {
                    this.mCurrentTempX = x;
                    this.mPosX = x - ((((float) this.mListener.mScreenWidth) * this.mCurrentXScale) / 2.0f);
                } else if (animalLock.mCurrentAnimation == 12) {
                    if (!this.mListener.mIsLandcapeFreeform) {
                        this.mPosX = x - ((((float) this.mListener.mScreenWidth) * this.mCurrentXScale) / 2.0f);
                    } else {
                        this.mPosX = x - ((((float) this.mListener.mScreenHeight) * this.mCurrentXScale) / 2.0f);
                    }
                } else if (animalLock.mCurrentAnimation == 7) {
                    this.mPosX = x;
                    this.mListener.mGestureAnimator.setMatrixInTransaction(aToken, this.mSmallWindowTargetWScale, 0.0f, 0.0f, this.mSmallWindowTargetHScale);
                    synchronized (this.mListener.mService.mGlobalLock) {
                        try {
                            WindowManagerService.boostPriorityForLockedSection();
                            if (this.mListener.mIsLandcapeFreeform) {
                                this.mListener.setSmallFreeFormWindowBounds(new Rect((int) this.mPosX, (int) this.mPosY, (int) (this.mPosX + MiuiMultiWindowUtils.LANDCAPE_SMALL_FREEFORM_WIDTH), (int) (this.mPosY + MiuiMultiWindowUtils.LANDCAPE_SMALL_FREEFORM_HEIGHT)));
                            } else {
                                this.mListener.setSmallFreeFormWindowBounds(new Rect((int) this.mPosX, (int) this.mPosY, (int) (this.mPosX + MiuiMultiWindowUtils.SMALL_FREEFORM_WIDTH), (int) (this.mPosY + MiuiMultiWindowUtils.SMALL_FREEFORM_HEIGHT)));
                            }
                            w.getDisplayContent().getInputMonitor().forceUpdateImmediately();
                        } catch (Throwable th) {
                            while (true) {
                                WindowManagerService.resetPriorityAfterLockedSection();
                                throw th;
                            }
                        }
                    }
                    WindowManagerService.resetPriorityAfterLockedSection();
                } else if (animalLock.mCurrentAnimation == 9) {
                    this.mPosX = x;
                    synchronized (this.mListener.mService.mGlobalLock) {
                        try {
                            WindowManagerService.boostPriorityForLockedSection();
                            if (this.mListener.mIsLandcapeFreeform) {
                                this.mListener.setSmallFreeFormWindowBounds(new Rect((int) this.mPosX, (int) this.mPosY, (int) (this.mPosX + MiuiMultiWindowUtils.LANDCAPE_SMALL_FREEFORM_WIDTH), (int) (this.mPosY + MiuiMultiWindowUtils.LANDCAPE_SMALL_FREEFORM_HEIGHT)));
                            } else {
                                this.mListener.setSmallFreeFormWindowBounds(new Rect((int) this.mPosX, (int) this.mPosY, (int) (this.mPosX + MiuiMultiWindowUtils.SMALL_FREEFORM_WIDTH), (int) (this.mPosY + MiuiMultiWindowUtils.SMALL_FREEFORM_HEIGHT)));
                            }
                            w.getDisplayContent().getInputMonitor().forceUpdateImmediately();
                        } catch (Throwable th2) {
                            while (true) {
                                WindowManagerService.resetPriorityAfterLockedSection();
                                throw th2;
                            }
                        }
                    }
                    WindowManagerService.resetPriorityAfterLockedSection();
                } else if (animalLock.mCurrentAnimation == 11 || animalLock.mCurrentAnimation == 10) {
                    this.mPosX = x;
                    this.mResizeLeft = (float) ((int) x);
                } else {
                    this.mPosX = x;
                }
            }
            Slog.d(TAG, "setPositionX mPosX:" + this.mPosX + " mPosY:" + this.mPosY);
            this.mListener.mGestureAnimator.setPositionInTransaction(aToken, this.mPosX, this.mPosY);
            this.mListener.mGestureAnimator.applyTransaction();
        }
    }

    public void setPositionY(WindowState w, float y) {
        MiuiFreeFormGestureAnimator.AnimalLock animalLock = this.mWindowLocks.get(w);
        if (animalLock != null) {
            if (MiuiFreeFormGestureController.DEBUG) {
                Slog.d(TAG, "setPositionY value:" + y + " w:" + w + " mPosX:" + this.mPosX + " animalLock:" + animalLock + " mCurrentAnimation:" + animalLock.mCurrentAnimation);
            }
            AppWindowToken aToken = w.mAppToken;
            if (animalLock.mCurrentAnimation == 8) {
                this.mPosY = y;
            } else if (animalLock.mCurrentAnimation == 3) {
                this.mPosY = y;
            } else if (animalLock.mCurrentAnimation != 4) {
                if (animalLock.mCurrentAnimation == 6) {
                    this.mPosY = y - ((((float) this.mListener.mScreenHeight) * this.mCurrentYScale) / 2.0f);
                } else if (animalLock.mCurrentAnimation == 17) {
                    this.mCurrentTempY = y;
                    this.mPosY = y - ((((float) this.mListener.mScreenHeight) * this.mCurrentYScale) / 2.0f);
                } else if (animalLock.mCurrentAnimation == 12) {
                    if (!this.mListener.mIsLandcapeFreeform) {
                        this.mPosY = y - ((((float) this.mListener.mScreenHeight) * this.mCurrentYScale) / 2.0f);
                    } else {
                        this.mPosY = y - ((((float) this.mListener.mScreenWidth) * this.mCurrentYScale) / 2.0f);
                    }
                } else if (animalLock.mCurrentAnimation == 7) {
                    this.mPosY = y;
                    this.mListener.mGestureAnimator.setMatrixInTransaction(aToken, this.mSmallWindowTargetWScale, 0.0f, 0.0f, this.mSmallWindowTargetHScale);
                    synchronized (this.mListener.mService.mGlobalLock) {
                        try {
                            WindowManagerService.boostPriorityForLockedSection();
                            if (this.mListener.mIsLandcapeFreeform) {
                                this.mListener.setSmallFreeFormWindowBounds(new Rect((int) this.mPosX, (int) this.mPosY, (int) (this.mPosX + MiuiMultiWindowUtils.LANDCAPE_SMALL_FREEFORM_WIDTH), (int) (this.mPosY + MiuiMultiWindowUtils.LANDCAPE_SMALL_FREEFORM_HEIGHT)));
                            } else {
                                this.mListener.setSmallFreeFormWindowBounds(new Rect((int) this.mPosX, (int) this.mPosY, (int) (this.mPosX + MiuiMultiWindowUtils.SMALL_FREEFORM_WIDTH), (int) (this.mPosY + MiuiMultiWindowUtils.SMALL_FREEFORM_HEIGHT)));
                            }
                            w.getDisplayContent().getInputMonitor().forceUpdateImmediately();
                        } catch (Throwable th) {
                            while (true) {
                                WindowManagerService.resetPriorityAfterLockedSection();
                                throw th;
                            }
                        }
                    }
                    WindowManagerService.resetPriorityAfterLockedSection();
                } else if (animalLock.mCurrentAnimation == 9) {
                    this.mPosY = y;
                    synchronized (this.mListener.mService.mGlobalLock) {
                        try {
                            WindowManagerService.boostPriorityForLockedSection();
                            if (this.mListener.mIsLandcapeFreeform) {
                                this.mListener.setSmallFreeFormWindowBounds(new Rect((int) this.mPosX, (int) this.mPosY, (int) (this.mPosX + MiuiMultiWindowUtils.LANDCAPE_SMALL_FREEFORM_WIDTH), (int) (this.mPosY + MiuiMultiWindowUtils.LANDCAPE_SMALL_FREEFORM_HEIGHT)));
                            } else {
                                this.mListener.setSmallFreeFormWindowBounds(new Rect((int) this.mPosX, (int) this.mPosY, (int) (this.mPosX + MiuiMultiWindowUtils.SMALL_FREEFORM_WIDTH), (int) (this.mPosY + MiuiMultiWindowUtils.SMALL_FREEFORM_HEIGHT)));
                            }
                            w.getDisplayContent().getInputMonitor().forceUpdateImmediately();
                        } catch (Throwable th2) {
                            while (true) {
                                WindowManagerService.resetPriorityAfterLockedSection();
                                throw th2;
                            }
                        }
                    }
                    WindowManagerService.resetPriorityAfterLockedSection();
                } else if (animalLock.mCurrentAnimation == 11 || animalLock.mCurrentAnimation == 10) {
                    this.mPosY = y;
                    this.mResizeTop = (float) ((int) y);
                } else {
                    this.mPosY = y;
                }
            }
            Slog.d(TAG, "setPositionY mPosX:" + this.mPosX + " mPosY:" + this.mPosY);
            this.mListener.mGestureAnimator.setPositionInTransaction(aToken, this.mPosX, this.mPosY);
            this.mListener.mGestureAnimator.applyTransaction();
        }
    }

    public void onAnimationEnd(MiuiFreeFormGestureAnimator.AnimalLock animalLock, MiuiFreeFormDynamicAnimation dynamicAnimation) {
        Slog.d(TAG, "onAnimationEnd currentAnimation:" + animalLock.mCurrentAnimation + " isAnimalFinished:" + animalLock.isAnimalFinished());
        if (animalLock.isAnimalFinished()) {
            if (animalLock.mWindow != null) {
                synchronized (animalLock.mWindow) {
                    this.mWindowLocks.remove(animalLock.mWindow);
                    removeAllAnimalByAnimalLock(animalLock);
                }
            }
            boolean isFinished = isAllAnimalFinished(animalLock);
            if (isFinished) {
                this.mListener.mGestureController.mHandler.removeCallbacks(this.mAnimalFinishedActionsRunnable);
                allAnimalFinishedActions(animalLock);
            } else {
                this.mListener.mGestureController.mHandler.removeCallbacks(this.mAnimalFinishedActionsRunnable);
                this.mAnimalFinishedActionsRunnable = new AnimalFinishedActionsRunnable(new WeakReference(animalLock));
                this.mListener.mGestureController.mHandler.postDelayed(this.mAnimalFinishedActionsRunnable, 500);
            }
            animalLock.resetAnimalState();
            this.mIsAnimating = !isFinished;
            return;
        }
        this.mIsAnimating = true;
    }

    /* access modifiers changed from: private */
    public void allAnimalFinishedActions(MiuiFreeFormGestureAnimator.AnimalLock animalLock) {
        MiuiFreeFormGestureAnimator.AnimalLock animalLock2 = animalLock;
        if (animalLock2 != null) {
            if (animalLock2.mCurrentAnimation == 2) {
                this.mListener.mGestureAnimator.reset();
            } else if (animalLock2.mCurrentAnimation == 0) {
                this.mListener.mGestureAnimator.setAlphaInTransaction(animalLock2.mWindow.mAppToken, 1.0f);
                this.mListener.mGestureAnimator.applyTransaction();
                startExitApplication();
            } else if (animalLock2.mCurrentAnimation != 4) {
                if (animalLock2.mCurrentAnimation == 14) {
                    this.mListener.mGestureAnimator.reset(animalLock2.mWindow.mAppToken);
                } else if (animalLock2.mCurrentAnimation == 5) {
                    if (Settings.Secure.getInt(this.mListener.mService.mContext.getContentResolver(), "first_use_freeform", 0) == 0) {
                        MiuiFreeFormGesturePointerEventListener miuiFreeFormGesturePointerEventListener = this.mListener;
                        miuiFreeFormGesturePointerEventListener.showTipWindow(1, miuiFreeFormGesturePointerEventListener.mWindowBounds);
                        Settings.Secure.putIntForUser(this.mListener.mService.mContext.getContentResolver(), "first_use_freeform", 1, -2);
                    }
                    Iterator<WindowState> it = this.mListener.mFreeFormAppWindows.iterator();
                    while (it.hasNext()) {
                        this.mListener.mGestureAnimator.setShadowRadiusParas(it.next(), 4.0f, 0.0f, 8.0f, 249.0f, 0.05f);
                        this.mListener.mGestureAnimator.applyTransaction();
                    }
                    this.mListener.startShowFreeFormWindow();
                    this.mEnteredHotArea = false;
                } else if (animalLock2.mCurrentAnimation == 3) {
                    synchronized (this.mListener.mService.mGlobalLock) {
                        try {
                            WindowManagerService.boostPriorityForLockedSection();
                            if (this.mListener.mIsLandcapeFreeform) {
                                this.mListener.setSmallFreeFormWindowBounds(new Rect((int) this.mPosX, (int) this.mPosY, (int) (this.mPosX + MiuiMultiWindowUtils.LANDCAPE_SMALL_FREEFORM_WIDTH), (int) (this.mPosY + MiuiMultiWindowUtils.LANDCAPE_SMALL_FREEFORM_HEIGHT)));
                            } else {
                                this.mListener.setSmallFreeFormWindowBounds(new Rect((int) this.mPosX, (int) this.mPosY, (int) (this.mPosX + MiuiMultiWindowUtils.SMALL_FREEFORM_WIDTH), (int) (this.mPosY + MiuiMultiWindowUtils.SMALL_FREEFORM_HEIGHT)));
                            }
                            this.mListener.mGestureController.mDisplayContent.getInputMonitor().forceUpdateImmediately();
                        } catch (Throwable th) {
                            while (true) {
                                WindowManagerService.resetPriorityAfterLockedSection();
                                throw th;
                            }
                        }
                    }
                    WindowManagerService.resetPriorityAfterLockedSection();
                    Iterator<WindowState> it2 = this.mListener.mFreeFormAppWindows.iterator();
                    while (it2.hasNext()) {
                        this.mListener.mGestureAnimator.setShadowRadiusParas(it2.next(), 4.0f, 0.0f, 8.0f, 249.0f, 0.03f);
                        this.mListener.mGestureAnimator.applyTransaction();
                    }
                    this.mListener.startShowSmallFreeFormWindow();
                } else if (animalLock2.mCurrentAnimation == 6 || animalLock2.mCurrentAnimation == 17) {
                    this.mTopFullScreenWindow = null;
                    startShowFullScreenWindow();
                    this.mEnteredHotArea = false;
                    this.mCurrentAnimation = -1;
                } else if (animalLock2.mCurrentAnimation == 8) {
                    if (this.mCurrentAction == 1) {
                        if (this.mEnteredHotArea) {
                            synchronized (this.mListener.mService.mGlobalLock) {
                                try {
                                    WindowManagerService.boostPriorityForLockedSection();
                                    this.mSmallWindowBounds.set((int) this.mPosX, (int) this.mPosY, (int) (this.mPosX + (this.mWindowWidth * this.mCurrentXScale)), (int) (this.mPosY + (this.mWindowHeight * this.mCurrentYScale)));
                                    this.mListener.setSmallFreeFormWindowBounds(this.mSmallWindowBounds);
                                } catch (Throwable th2) {
                                    while (true) {
                                        WindowManagerService.resetPriorityAfterLockedSection();
                                        throw th2;
                                    }
                                }
                            }
                            WindowManagerService.resetPriorityAfterLockedSection();
                            Iterator<WindowState> it3 = this.mListener.mFreeFormAppWindows.iterator();
                            while (it3.hasNext()) {
                                this.mListener.mGestureAnimator.setShadowRadiusParas(it3.next(), 4.0f, 0.0f, 8.0f, 249.0f, 0.03f);
                                this.mListener.mGestureAnimator.applyTransaction();
                            }
                            this.mListener.startShowSmallFreeFormWindow();
                        } else {
                            try {
                                this.mListener.mService.mActivityManager.resizeTask(this.mListener.mTaskId, this.mListener.mTaskPositioner.mWindowDragBounds, 0);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            this.mListener.mGestureAnimator.reset();
                            this.mListener.resetState();
                        }
                    }
                } else if (animalLock2.mCurrentAnimation == 9) {
                    synchronized (this.mListener.mService.mGlobalLock) {
                        try {
                            WindowManagerService.boostPriorityForLockedSection();
                            if (this.mListener.mIsLandcapeFreeform) {
                                this.mListener.setSmallFreeFormWindowBounds(new Rect((int) this.mPosX, (int) this.mPosY, (int) (this.mPosX + MiuiMultiWindowUtils.LANDCAPE_SMALL_FREEFORM_WIDTH), (int) (this.mPosY + MiuiMultiWindowUtils.LANDCAPE_SMALL_FREEFORM_HEIGHT)));
                            } else {
                                this.mListener.setSmallFreeFormWindowBounds(new Rect((int) this.mPosX, (int) this.mPosY, (int) (this.mPosX + MiuiMultiWindowUtils.SMALL_FREEFORM_WIDTH), (int) (this.mPosY + MiuiMultiWindowUtils.SMALL_FREEFORM_HEIGHT)));
                            }
                            this.mListener.mGestureController.mDisplayContent.getInputMonitor().forceUpdateImmediately();
                        } catch (Throwable th3) {
                            while (true) {
                                WindowManagerService.resetPriorityAfterLockedSection();
                                throw th3;
                            }
                        }
                    }
                    WindowManagerService.resetPriorityAfterLockedSection();
                    Iterator<WindowState> it4 = this.mListener.mFreeFormAppWindows.iterator();
                    while (it4.hasNext()) {
                        this.mListener.mGestureAnimator.setShadowRadiusParas(it4.next(), 4.0f, 0.0f, 8.0f, 249.0f, 0.03f);
                        this.mListener.mGestureAnimator.applyTransaction();
                    }
                    if (this.mPosY < 0.0f) {
                        this.mListener.startExitSmallFreeformApplication();
                    }
                } else if (animalLock2.mCurrentAnimation == 16) {
                    try {
                        Rect rect = new Rect(this.mLastFreeFormWindowStartBounds);
                        rect.offset(0, -this.mOffSetYForInputMethod);
                        this.mListener.mService.mActivityManager.resizeTask(this.mListener.mTaskId, rect, 0);
                    } catch (Exception e2) {
                        e2.printStackTrace();
                    }
                } else if (animalLock2.mCurrentAnimation == 15) {
                    try {
                        Rect rect2 = new Rect(this.mLastFreeFormWindowStartBounds);
                        rect2.offset(0, this.mOffSetYForInputMethod);
                        this.mListener.mService.mActivityManager.resizeTask(this.mListener.mTaskId, rect2, 0);
                    } catch (Exception e3) {
                        e3.printStackTrace();
                    }
                } else if (animalLock2.mCurrentAnimation == 12) {
                    synchronized (this.mListener.mService.mGlobalLock) {
                        try {
                            WindowManagerService.boostPriorityForLockedSection();
                            if (this.mListener.mIsLandcapeFreeform) {
                                this.mListener.setSmallFreeFormWindowBounds(new Rect((int) this.mPosX, (int) this.mPosY, (int) (this.mPosX + MiuiMultiWindowUtils.LANDCAPE_SMALL_FREEFORM_WIDTH), (int) (this.mPosY + MiuiMultiWindowUtils.LANDCAPE_SMALL_FREEFORM_HEIGHT)));
                            } else {
                                this.mListener.setSmallFreeFormWindowBounds(new Rect((int) this.mPosX, (int) this.mPosY, (int) (this.mPosX + MiuiMultiWindowUtils.SMALL_FREEFORM_WIDTH), (int) (this.mPosY + MiuiMultiWindowUtils.SMALL_FREEFORM_HEIGHT)));
                            }
                            this.mListener.mGestureController.mDisplayContent.getInputMonitor().forceUpdateImmediately();
                        } catch (Throwable th4) {
                            while (true) {
                                WindowManagerService.resetPriorityAfterLockedSection();
                                throw th4;
                            }
                        }
                    }
                    WindowManagerService.resetPriorityAfterLockedSection();
                    Rect lastBounds = MiuiMultiWindowUtils.getFreeformRect(this.mListener.mService.mContext, false, false, false, this.mListener.mIsLandcapeFreeform);
                    this.mLastFreeFormWindowStartBounds.set(lastBounds);
                    showScreenSurface();
                    this.mWindowHeight = ((float) lastBounds.height()) * MiuiMultiWindowUtils.sScale;
                    this.mWindowWidth = ((float) lastBounds.width()) * MiuiMultiWindowUtils.sScale;
                    if (this.mListener.mIsLandcapeFreeform) {
                        this.mSmallWindowTargetHScale = MiuiMultiWindowUtils.LANDCAPE_SMALL_FREEFORM_HEIGHT / this.mWindowHeight;
                        this.mSmallWindowTargetWScale = MiuiMultiWindowUtils.LANDCAPE_SMALL_FREEFORM_WIDTH / this.mWindowWidth;
                    } else {
                        this.mSmallWindowTargetHScale = MiuiMultiWindowUtils.SMALL_FREEFORM_HEIGHT / this.mWindowHeight;
                        this.mSmallWindowTargetWScale = MiuiMultiWindowUtils.SMALL_FREEFORM_WIDTH / this.mWindowWidth;
                    }
                    Iterator<WindowState> it5 = this.mListener.mFreeFormAppWindows.iterator();
                    while (it5.hasNext()) {
                        WindowState win = it5.next();
                        this.mListener.mGestureAnimator.hideAppWindowToken(win.mAppToken);
                        this.mListener.mGestureAnimator.setMatrixInTransaction(win.mAppToken, this.mSmallWindowTargetWScale, 0.0f, 0.0f, this.mSmallWindowTargetHScale);
                        this.mListener.mGestureAnimator.applyTransaction();
                    }
                    MiuiMultiWindowUtils.mIsMiniFreeformMode = false;
                    try {
                        this.mListener.mService.mActivityManager.resizeTask(this.mListener.mTaskId, lastBounds, 0);
                    } catch (Exception e4) {
                        e4.printStackTrace();
                    }
                    long endTime = System.currentTimeMillis() + 100;
                    while (endTime - System.currentTimeMillis() > 0) {
                        try {
                            Thread.sleep(50);
                        } catch (InterruptedException e5) {
                        }
                    }
                    Iterator<WindowState> it6 = this.mListener.mFreeFormAppWindows.iterator();
                    while (it6.hasNext()) {
                        WindowState window = it6.next();
                        this.mListener.mGestureAnimator.showAppWindowToken(window.mAppToken);
                        this.mListener.mGestureAnimator.setShadowRadiusParas(window, 4.0f, 0.0f, 8.0f, 249.0f, 0.03f);
                        this.mListener.mGestureAnimator.applyTransaction();
                    }
                    startGestureAnimation(13);
                    this.mListener.hideCaptionView();
                    this.mListener.startShowSmallFreeFormWindow();
                } else if (animalLock2.mCurrentAnimation == 10 || animalLock2.mCurrentAnimation == 11) {
                    try {
                        MiuiMultiWindowUtils.sScale = this.mResizeScale;
                        Rect position = new Rect((int) this.mResizeLeft, (int) this.mResizeTop, (int) (this.mResizeLeft + ((float) this.mListener.mWindowBounds.width())), (int) (this.mResizeTop + ((float) this.mListener.mWindowBounds.height())));
                        Slog.i(TAG, "RESIZE_BACK mCurrentAnimation == ANIMATION_RESIZE_BACK_LEFT_BOTTOM || ANIMATION_RESIZE_BACK_RIGHT_BOTTOM position:" + position + " width: " + position.width() + " height: " + position.height() + "mResizeScale: " + this.mResizeScale);
                        this.mListener.mService.mActivityManager.resizeTask(this.mListener.mTaskId, position, 2);
                    } catch (Exception e6) {
                        e6.printStackTrace();
                    }
                    this.mListener.mGestureAnimator.reset();
                }
            }
            int i = this.mCurrentAnimation;
            if (i != 12 && i != 14) {
                this.mCurrentAnimation = -1;
                this.mIsAnimating = false;
            }
        }
    }

    public boolean isAllAnimalFinished(MiuiFreeFormGestureAnimator.AnimalLock animalLock) {
        if (animalLock == null) {
            return true;
        }
        int animationType = animalLock.mCurrentAnimation;
        for (MiuiFreeFormGestureAnimator.AnimalLock animalL : this.mWindowLocks.values()) {
            if (animalL.mCurrentAnimation == animationType) {
                return false;
            }
        }
        return true;
    }

    public void removeAllAnimalByAnimalLock(MiuiFreeFormGestureAnimator.AnimalLock animalLock) {
        if (animalLock.mAlphaAnimation != null && this.mAnimalLocks.contains(animalLock.mAlphaAnimation)) {
            this.mAnimalLocks.remove(animalLock.mAlphaAnimation);
        }
        if (animalLock.mScaleXAnimation != null && this.mAnimalLocks.contains(animalLock.mScaleXAnimation)) {
            this.mAnimalLocks.remove(animalLock.mScaleXAnimation);
        }
        if (animalLock.mScaleYAnimation != null && this.mAnimalLocks.contains(animalLock.mScaleYAnimation)) {
            this.mAnimalLocks.remove(animalLock.mScaleYAnimation);
        }
        if (animalLock.mTranslateYAnimation != null && this.mAnimalLocks.contains(animalLock.mTranslateYAnimation)) {
            this.mAnimalLocks.remove(animalLock.mTranslateYAnimation);
        }
        if (animalLock.mTranslateXAnimation != null && this.mAnimalLocks.contains(animalLock.mTranslateXAnimation)) {
            this.mAnimalLocks.remove(animalLock.mTranslateXAnimation);
        }
        if (animalLock.mScaleAnimation != null && this.mAnimalLocks.contains(animalLock.mScaleAnimation)) {
            this.mAnimalLocks.remove(animalLock.mScaleAnimation);
        }
    }

    public void onAlphaAnimationEnd(MiuiFreeFormDynamicAnimation dynamicAnimation) {
        MiuiFreeFormGestureAnimator.AnimalLock animalLock = this.mAnimalLocks.get(dynamicAnimation);
        if (animalLock != null) {
            if (MiuiFreeFormGestureController.DEBUG) {
                Slog.d(TAG, "onAlphaAnimationEnd　animalLock:" + animalLock);
            }
            animalLock.mAlphaEnd = true;
            onAnimationEnd(animalLock, dynamicAnimation);
        }
    }

    public void onScaleAnimationEnd(MiuiFreeFormDynamicAnimation dynamicAnimation) {
        MiuiFreeFormGestureAnimator.AnimalLock animalLock = this.mAnimalLocks.get(dynamicAnimation);
        if (animalLock != null) {
            if (MiuiFreeFormGestureController.DEBUG) {
                Slog.d(TAG, "onScaleAnimationEnd　animalLock:" + animalLock);
            }
            animalLock.mScaleEnd = true;
            onAnimationEnd(animalLock, dynamicAnimation);
        }
    }

    public void onScaleXAnimationEnd(MiuiFreeFormDynamicAnimation dynamicAnimation) {
        MiuiFreeFormGestureAnimator.AnimalLock animalLock = this.mAnimalLocks.get(dynamicAnimation);
        if (animalLock != null) {
            if (MiuiFreeFormGestureController.DEBUG) {
                Slog.d(TAG, "onScaleXAnimationEnd　animalLock:" + animalLock + " dynamicAnimation:" + dynamicAnimation);
            }
            animalLock.mScaleXEnd = true;
            onAnimationEnd(animalLock, dynamicAnimation);
        }
    }

    public void onScaleYAnimationEnd(MiuiFreeFormDynamicAnimation dynamicAnimation) {
        MiuiFreeFormGestureAnimator.AnimalLock animalLock = this.mAnimalLocks.get(dynamicAnimation);
        if (animalLock != null) {
            if (MiuiFreeFormGestureController.DEBUG) {
                Slog.d(TAG, "onScaleYAnimationEnd　animalLock:" + animalLock);
            }
            animalLock.mScaleYEnd = true;
            onAnimationEnd(animalLock, dynamicAnimation);
        }
    }

    public void onTranslateYAnimationEnd(MiuiFreeFormDynamicAnimation dynamicAnimation) {
        MiuiFreeFormGestureAnimator.AnimalLock animalLock = this.mAnimalLocks.get(dynamicAnimation);
        if (animalLock != null) {
            if (MiuiFreeFormGestureController.DEBUG) {
                Slog.d(TAG, "onTranslateYAnimationEnd　animalLock:" + animalLock);
            }
            animalLock.mTranslateYEnd = true;
            onAnimationEnd(animalLock, dynamicAnimation);
        }
    }

    public void onTranslateXAnimationEnd(MiuiFreeFormDynamicAnimation dynamicAnimation) {
        MiuiFreeFormGestureAnimator.AnimalLock animalLock = this.mAnimalLocks.get(dynamicAnimation);
        if (animalLock != null) {
            if (MiuiFreeFormGestureController.DEBUG) {
                Slog.d(TAG, "onTranslateXAnimationEnd animalLock:" + animalLock);
            }
            animalLock.mTranslateXEnd = true;
            onAnimationEnd(animalLock, dynamicAnimation);
        }
    }

    public void showScreenSurface() {
        hideScreenSurface();
        DisplayContent displayContent = this.mListener.mGestureController.mDisplayContent;
        this.mListener.mScreenHeight = displayContent.mBaseDisplayHeight;
        this.mListener.mScreenWidth = displayContent.mBaseDisplayWidth;
        MiuiFreeFormGesturePointerEventListener miuiFreeFormGesturePointerEventListener = this.mListener;
        miuiFreeFormGesturePointerEventListener.mIsPortrait = miuiFreeFormGesturePointerEventListener.mScreenHeight > this.mListener.mScreenWidth;
        SurfaceControl.Transaction t = this.mListener.mService.mTransactionFactory.make();
        try {
            if (this.mListener.mIsPortrait) {
                this.mSurfaceControl = this.mListener.mGestureController.mDisplayContent.makeOverlay().setName("Freeform_ScreenshotSurface").setBufferSize(this.mListener.mScreenWidth, this.mListener.mScreenHeight).setSecure(false).build();
            } else {
                this.mSurfaceControl = this.mListener.mGestureController.mDisplayContent.makeOverlay().setName("Freeform_ScreenshotSurface").setBufferSize(this.mListener.mScreenHeight, this.mListener.mScreenWidth).setSecure(false).build();
            }
            Slog.d(TAG, "showScreenSurface:" + this.mSurfaceControl);
            SurfaceControl.Transaction t2 = this.mListener.mService.mTransactionFactory.make();
            t2.setOverrideScalingMode(this.mSurfaceControl, 1);
            t2.apply(true);
            Surface surface = this.mListener.mService.mSurfaceFactory.make();
            surface.copyFrom(this.mSurfaceControl);
            SurfaceControl.ScreenshotGraphicBuffer gb = this.mListener.mService.mDisplayManagerInternal.screenshot(0);
            if (gb != null) {
                try {
                    surface.attachAndQueueBuffer(gb.getGraphicBuffer());
                } catch (RuntimeException e) {
                    Slog.w(TAG, "Failed to attach screenshot - " + e.getMessage());
                }
                if (gb.containsSecureLayers()) {
                    t.setSecure(this.mSurfaceControl, true);
                }
                t.setLayer(this.mSurfaceControl, 2010001);
                t.show(this.mSurfaceControl);
            } else {
                Slog.w(TAG, "Unable to take screenshot of display");
            }
            surface.destroy();
        } catch (Surface.OutOfResourcesException e2) {
            Slog.w(TAG, "Unable to allocate freeze surface", e2);
        }
        if (this.mListener.mIsPortrait && this.mListener.mIsLandcapeFreeform) {
            setRotation(t, this.mListener.mService.getDefaultDisplayRotation());
        }
        t.apply();
        this.mListener.mGestureController.mHandler.postDelayed(new Runnable() {
            public void run() {
                MiuiFreeFormWindowMotionHelper.this.hideScreenSurface();
            }
        }, 500);
    }

    public void hideScreenSurface() {
        Slog.d(TAG, "hideScreenSurface:" + this.mSurfaceControl);
        if (this.mSurfaceControl != null) {
            this.mListener.mService.mTransactionFactory.make().remove(this.mSurfaceControl).apply();
            this.mSurfaceControl = null;
        }
    }

    public void setOriginalBounds(Rect rect) {
        this.mOriginalBounds = rect;
        this.mListener.mIsLandcapeFreeform = this.mOriginalBounds.width() > this.mOriginalBounds.height();
    }

    private final class AnimalFinishedActionsRunnable implements Runnable {
        WeakReference<MiuiFreeFormGestureAnimator.AnimalLock> mAnimalLock;

        AnimalFinishedActionsRunnable(WeakReference<MiuiFreeFormGestureAnimator.AnimalLock> animalLock) {
            this.mAnimalLock = animalLock;
        }

        public void run() {
            MiuiFreeFormWindowMotionHelper.this.allAnimalFinishedActions((MiuiFreeFormGestureAnimator.AnimalLock) this.mAnimalLock.get());
        }
    }

    private void setRotation(SurfaceControl.Transaction t, int rotation) {
        Matrix snapshotInitialMatrix = new Matrix();
        int delta = DisplayContent.deltaRotation(rotation, 0);
        if (this.mListener.mIsPortrait) {
            createRotationMatrix(delta, this.mListener.mScreenWidth, this.mListener.mScreenHeight, snapshotInitialMatrix);
        } else {
            createRotationMatrix(delta, this.mListener.mScreenHeight, this.mListener.mScreenWidth, snapshotInitialMatrix);
        }
        setSnapshotTransform(t, snapshotInitialMatrix, 1.0f);
    }

    private void setSnapshotTransform(SurfaceControl.Transaction t, Matrix matrix, float alpha) {
        if (this.mSurfaceControl != null) {
            float[] mTmpFloats = new float[9];
            matrix.getValues(mTmpFloats);
            t.setPosition(this.mSurfaceControl, mTmpFloats[2], mTmpFloats[5]);
            t.setMatrix(this.mSurfaceControl, mTmpFloats[0], mTmpFloats[3], mTmpFloats[1], mTmpFloats[4]);
            t.setAlpha(this.mSurfaceControl, alpha);
        }
    }

    public static void createRotationMatrix(int rotation, int width, int height, Matrix outMatrix) {
        if (rotation == 0) {
            outMatrix.reset();
        } else if (rotation == 1) {
            outMatrix.setRotate(90.0f, 0.0f, 0.0f);
            outMatrix.postTranslate((float) height, 0.0f);
        } else if (rotation == 2) {
            outMatrix.setRotate(180.0f, 0.0f, 0.0f);
            outMatrix.postTranslate((float) width, (float) height);
        } else if (rotation == 3) {
            outMatrix.setRotate(270.0f, 0.0f, 0.0f);
            outMatrix.postTranslate(0.0f, (float) width);
        }
    }

    public void setRequestedOrientation(int requestedOrientation, TaskRecord taskRecord) {
        int heightAfterScale;
        int top;
        float scale;
        int heightOri;
        int widthAfterScale;
        int top2;
        TaskRecord taskRecord2 = taskRecord;
        if (MiuiMultiWindowUtils.isOrientationLandscape(requestedOrientation) != this.mListener.mIsLandcapeFreeform) {
            WindowManager wm = (WindowManager) taskRecord2.mService.mContext.getSystemService("window");
            DisplayMetrics outMetrics = new DisplayMetrics();
            wm.getDefaultDisplay().getRealMetrics(outMetrics);
            Rect rect = taskRecord.getBounds();
            int heightCenter = rect.top + ((int) (((((float) rect.height()) * MiuiMultiWindowUtils.sScale) / 2.0f) + 0.5f));
            int widthCenter = rect.left + ((int) (((((float) rect.width()) * MiuiMultiWindowUtils.sScale) / 2.0f) + 0.5f));
            boolean isPortrait = outMetrics.heightPixels > outMetrics.widthPixels;
            if (!MiuiMultiWindowUtils.isOrientationLandscape(requestedOrientation)) {
                if (isPortrait) {
                    int widthAfterScale2 = MiuiMultiWindowUtils.FREEFORM_PORTRAIT_WIDTH;
                    int heightAfterScale2 = MiuiMultiWindowUtils.FREEFORM_PORTRAIT_HEIGHT;
                    scale = (((float) MiuiMultiWindowUtils.FREEFORM_PORTRAIT_WIDTH) * 1.0f) / ((float) outMetrics.widthPixels);
                    int widthOri = (int) ((((float) widthAfterScale2) / scale) + 0.5f);
                    int heightOri2 = (int) ((((float) heightAfterScale2) / scale) + 0.5f);
                    int top3 = heightCenter - (heightAfterScale2 / 2);
                    if (top3 + heightAfterScale2 > outMetrics.heightPixels) {
                        top3 = outMetrics.heightPixels - heightAfterScale2;
                    }
                    if (top3 < MiuiMultiWindowUtils.TOP_DECOR_CAPTIONVIEW_HEIGHT) {
                        top = MiuiMultiWindowUtils.TOP_DECOR_CAPTIONVIEW_HEIGHT;
                    } else {
                        top = top3;
                    }
                    heightAfterScale = widthCenter - (widthAfterScale2 / 2);
                    widthAfterScale = heightAfterScale + widthOri;
                    heightOri = top + heightOri2;
                } else {
                    int right = MiuiMultiWindowUtils.FREEFORM_LANDCAPE_WIDTH;
                    int heightAfterScale3 = MiuiMultiWindowUtils.FREEFORM_LANDCAPE_HEIGHT;
                    scale = (((float) MiuiMultiWindowUtils.FREEFORM_LANDCAPE_WIDTH) * 1.0f) / ((float) outMetrics.heightPixels);
                    int widthOri2 = (int) ((((float) right) / scale) + 0.5f);
                    int heightOri3 = (int) ((((float) heightAfterScale3) / scale) + 0.5f);
                    int top4 = heightCenter - (heightAfterScale3 / 2);
                    if (top4 < 0) {
                        top2 = 0;
                    } else {
                        top2 = top4;
                    }
                    heightAfterScale = widthCenter - (right / 2);
                    widthAfterScale = heightAfterScale + widthOri2;
                    heightOri = top + heightOri3;
                }
            } else if (isPortrait) {
                int widthAfterScale3 = MiuiMultiWindowUtils.LANDCAPE_FREEFORM_WIDTH;
                int heightAfterScale4 = MiuiMultiWindowUtils.LANDCAPE_FREEFORM_HEIGHT;
                scale = (((((float) MiuiMultiWindowUtils.LANDCAPE_FREEFORM_WIDTH) * 1.0f) / ((float) outMetrics.widthPixels)) + ((((float) MiuiMultiWindowUtils.LANDCAPE_FREEFORM_HEIGHT) * 1.0f) / ((float) outMetrics.heightPixels))) / 2.0f;
                int widthOri3 = (int) ((((float) widthAfterScale3) / scale) + 0.5f);
                int heightOri4 = (int) ((((float) heightAfterScale4) / scale) + 0.5f);
                int left = widthCenter - (widthAfterScale3 / 2);
                WindowManager windowManager = wm;
                if (left + widthAfterScale3 > outMetrics.widthPixels) {
                    left = outMetrics.widthPixels - widthAfterScale3;
                }
                if (left < 0) {
                    left = 0;
                }
                Slog.d(TAG, "setRequestedOrientation heightAfterScale = " + heightAfterScale4 + "setRequestedOrientation FREEFORM_PORTRAIT_LANDCAPE_HEIGHT = " + MiuiMultiWindowUtils.LANDCAPE_FREEFORM_HEIGHT);
                widthAfterScale = left + widthOri3;
                top = heightCenter - (heightAfterScale4 / 2);
                heightOri = heightOri4 + top;
                heightAfterScale = left;
            } else {
                int widthAfterScale4 = MiuiMultiWindowUtils.LANDCAPE_FREEFORM_WIDTH;
                int heightAfterScale5 = MiuiMultiWindowUtils.LANDCAPE_FREEFORM_HEIGHT;
                scale = (((((float) MiuiMultiWindowUtils.LANDCAPE_FREEFORM_WIDTH) * 1.0f) / ((float) outMetrics.widthPixels)) + ((((float) MiuiMultiWindowUtils.LANDCAPE_FREEFORM_HEIGHT) * 1.0f) / ((float) outMetrics.heightPixels))) / 2.0f;
                int widthOri4 = (int) ((((float) widthAfterScale4) / scale) + 0.5f);
                int heightOri5 = (int) ((((float) heightAfterScale5) / scale) + 0.5f);
                int left2 = widthCenter - (widthAfterScale4 / 2);
                if (left2 + widthAfterScale4 > outMetrics.widthPixels) {
                    left2 = outMetrics.widthPixels - widthAfterScale4;
                }
                if (left2 < MiuiMultiWindowUtils.FREEFORM_TO_LEFT) {
                    left2 = MiuiMultiWindowUtils.FREEFORM_TO_LEFT;
                }
                int top5 = heightCenter - (heightAfterScale5 / 2);
                heightOri = heightOri5 + top5;
                widthAfterScale = left2 + widthOri4;
                top = top5;
                heightAfterScale = left2;
            }
            Rect setBounds = new Rect(heightAfterScale, top, widthAfterScale, heightOri);
            Slog.d(TAG, "setRequestedOrientation rect = " + setBounds);
            this.mListener.mIsLandcapeFreeform = setBounds.width() > setBounds.height();
            applyAlphaHideAnimation(taskRecord2, setBounds, scale);
            if (this.mListener.mIsPortrait && this.mListener.mIsLandcapeFreeform) {
                this.mListener.mGestureController.mWindowController.removeOverlayView();
                this.mListener.mGestureController.mWindowController.addOverlayView();
            }
        }
    }

    public void applyAlphaHideAnimation(TaskRecord task, Rect setBounds, float scale) {
        final Rect bounds = task.getBounds();
        final float f = scale;
        final TaskRecord taskRecord = task;
        final Rect rect = setBounds;
        this.mListener.mGestureController.mHandler.post(new Runnable() {
            public void run() {
                Iterator<WindowState> it = MiuiFreeFormWindowMotionHelper.this.mListener.mFreeFormAppWindows.iterator();
                while (it.hasNext()) {
                    final WindowState windowState = it.next();
                    AppWindowToken aToken = windowState.mAppToken;
                    if (aToken != null) {
                        MiuiFreeFormWindowMotionHelper.this.mListener.mGestureAnimator.recreateLeashIfNeeded(aToken);
                        ValueAnimator animator = ValueAnimator.ofFloat(new float[]{1.0f, 0.0f});
                        animator.setDuration(300);
                        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener(aToken, bounds) {
                            private final /* synthetic */ AppWindowToken f$1;
                            private final /* synthetic */ Rect f$2;

                            {
                                this.f$1 = r2;
                                this.f$2 = r3;
                            }

                            public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                                MiuiFreeFormWindowMotionHelper.AnonymousClass5.this.lambda$run$0$MiuiFreeFormWindowMotionHelper$5(this.f$1, this.f$2, valueAnimator);
                            }
                        });
                        animator.addListener(new Animator.AnimatorListener() {
                            public void onAnimationStart(Animator animation) {
                            }

                            public void onAnimationEnd(Animator animation) {
                                MiuiMultiWindowUtils.sScale = f;
                                taskRecord.requestResize(rect, 2);
                                MiuiFreeFormWindowMotionHelper.this.applyAlphaShowAnimation(taskRecord, windowState);
                            }

                            public void onAnimationCancel(Animator animation) {
                            }

                            public void onAnimationRepeat(Animator animation) {
                            }
                        });
                        animator.start();
                    }
                }
            }

            public /* synthetic */ void lambda$run$0$MiuiFreeFormWindowMotionHelper$5(AppWindowToken aToken, Rect rect, ValueAnimator valueAnimator) {
                float value = ((Float) valueAnimator.getAnimatedValue()).floatValue();
                MiuiFreeFormWindowMotionHelper.this.mListener.mGestureAnimator.setPositionInTransaction(aToken, (float) rect.left, (float) rect.top);
                MiuiFreeFormWindowMotionHelper.this.mListener.mGestureAnimator.setAlphaInTransaction(aToken, value);
                MiuiFreeFormWindowMotionHelper.this.mListener.mGestureAnimator.applyTransaction();
            }
        });
    }

    public void applyAlphaShowAnimation(TaskRecord task, final WindowState windowState) {
        final Rect rect = task.getBounds();
        this.mListener.mGestureController.mHandler.postDelayed(new Runnable() {
            public void run() {
                AppWindowToken aToken = windowState.mAppToken;
                MiuiFreeFormWindowMotionHelper.this.mListener.mGestureAnimator.recreateLeashIfNeeded(aToken);
                ValueAnimator animator = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
                animator.setDuration(300);
                animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener(aToken, rect) {
                    private final /* synthetic */ AppWindowToken f$1;
                    private final /* synthetic */ Rect f$2;

                    {
                        this.f$1 = r2;
                        this.f$2 = r3;
                    }

                    public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                        MiuiFreeFormWindowMotionHelper.AnonymousClass6.this.lambda$run$0$MiuiFreeFormWindowMotionHelper$6(this.f$1, this.f$2, valueAnimator);
                    }
                });
                animator.start();
            }

            public /* synthetic */ void lambda$run$0$MiuiFreeFormWindowMotionHelper$6(AppWindowToken aToken, Rect rect, ValueAnimator valueAnimator) {
                float value = ((Float) valueAnimator.getAnimatedValue()).floatValue();
                MiuiFreeFormWindowMotionHelper.this.mListener.mGestureAnimator.setPositionInTransaction(aToken, (float) rect.left, (float) rect.top);
                MiuiFreeFormWindowMotionHelper.this.mListener.mGestureAnimator.setAlphaInTransaction(aToken, value);
                MiuiFreeFormWindowMotionHelper.this.mListener.mGestureAnimator.applyTransaction();
            }
        }, 200);
    }
}
