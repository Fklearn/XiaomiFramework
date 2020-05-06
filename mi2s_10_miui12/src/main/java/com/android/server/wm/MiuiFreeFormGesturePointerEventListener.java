package com.android.server.wm;

import android.content.ComponentName;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.provider.Settings;
import android.util.MiuiMultiWindowUtils;
import android.util.Slog;
import android.view.GestureDetector;
import android.view.InputChannel;
import android.view.InputEvent;
import android.view.InputEventReceiver;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.view.WindowManagerPolicyConstants;
import com.android.server.LocalServices;
import com.android.server.inputmethod.InputMethodManagerInternal;
import com.android.server.pm.DumpState;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class MiuiFreeFormGesturePointerEventListener implements WindowManagerPolicyConstants.PointerEventListener {
    public static final int GESTURE_WINDOWING_MODE_FREEFORM = 0;
    public static final int GESTURE_WINDOWING_MODE_SMALL_FREEFORM = 1;
    public static final int GESTURE_WINDOWING_MODE_UNDEFINED = -1;
    private static final int MSG_ACTION_DOWN = 0;
    private static final int MSG_ACTION_MOVE = 1;
    private static final int MSG_ACTION_UP = 2;
    private static final int RESIZE_WIDTH = 40;
    private static final String SYSTEMUI_PACKAGE_NAME = "com.android.systemui";
    private static final String SYSTEMUI_SCREENRECORDER_LAYER_NAME = "ScreenRecorderAnimation";
    private static final String SYSTEMUI_SCREENSHOT_LAYER_NAME = "ScreenshotAnimation";
    public static final String TAG = "MiuiFreeFormGesturePointerEventListener";
    public static int mCurrentWindowMode = -1;
    private static boolean mHadDealWithDownAction = true;
    public ComponentName mComponentName;
    int mDisplayRotation;
    final Set<AppWindowToken> mFreeFormAppTokens = new HashSet();
    final ArrayList<WindowState> mFreeFormAppWindows = new ArrayList<>();
    MiuiFreeFormWindowMotionHelper mFreeFormWindowMotionHelper;
    MiuiFreeFormGestureAnimator mGestureAnimator;
    MiuiFreeFormGestureController mGestureController;
    MiuiFreeFormGestureDetector mGestureDetector;
    private Handler mHandler;
    AppWindowToken mHomeAppToken;
    WindowState mHomeWindow;
    private boolean mInMultiTouch;
    private InputEventReceiver mInputEventReceiver;
    private InputMethodManagerInternal mInputMethodManagerInternal;
    boolean mIsLandcapeFreeform = false;
    boolean mIsNotchScreen;
    boolean mIsPortrait;
    private int mLastOrientation;
    private final Object mLock = new Object();
    int mLongSide;
    int mMaxWidthSize;
    int mMinWidthSize;
    int mNotchBar;
    int mResizeWidth;
    final ConcurrentHashMap<WindowState, WindowStateInfo> mScalingWindows = new ConcurrentHashMap<>();
    int mScreenHeight;
    WindowState mScreenRecorderWindow;
    int mScreenWidth;
    WindowState mScreenshotWindow;
    WindowManagerService mService;
    MiuiFreeFormSmallWindowMotionHelper mSmallFreeFormWindowMotionHelper;
    volatile Rect mSmallWindowBounds = new Rect();
    Rect mStackBounds = new Rect();
    int mStackId;
    WindowState mStatusBarWindow;
    int mTaskId;
    MiuiFreeFormTaskPositioner mTaskPositioner;
    private IBinder mToken = new Binder();
    AppWindowToken mTopFreeFormAppToken;
    WindowState mTopFreeFormAppWindow;
    private GestureDetector mTouchGestureDetector;
    float mWidthHeightScale;
    Rect mWindowBounds = new Rect();
    int mWindowHeight;
    int mWindowWidth;
    int shortSide;

    public static final class WindowStateInfo {
        AppWindowToken mAppToken;
        boolean mHasShowStartingWindow;
        float mNowAlpha;
        Rect mNowFrame = new Rect();
        float mNowHeightScale;
        float mNowPosX;
        float mNowPosY;
        float mNowScale;
        float mNowWidthScale;
        Rect mOriFrame = new Rect();
        float mOriPosX;
        float mOriPosY;
        float mTargetAlpha;
        float mTargetHeightScale;
        float mTargetPosX;
        float mTargetPosY;
        float mTargetScale;
        float mTargetWidthScale;
    }

    MiuiFreeFormGesturePointerEventListener(WindowManagerService wms, MiuiFreeFormGestureController controller) {
        this.mService = wms;
        this.mGestureController = controller;
        this.mHandler = new H(this.mGestureController.mHandler.getLooper());
        this.mSmallFreeFormWindowMotionHelper = new MiuiFreeFormSmallWindowMotionHelper(this);
        this.mFreeFormWindowMotionHelper = new MiuiFreeFormWindowMotionHelper(this);
        this.mTaskPositioner = new MiuiFreeFormTaskPositioner(this);
        this.mTouchGestureDetector = new GestureDetector(this.mService.mContext, this.mSmallFreeFormWindowMotionHelper.mGestureListener);
        this.mGestureDetector = new MiuiFreeFormGestureDetector(this);
        this.mGestureAnimator = new MiuiFreeFormGestureAnimator(this.mService, this);
        this.mResizeWidth = getGestureResizeWidth();
        updateScreenParams(this.mGestureController.mDisplayContent, this.mService.mContext.getResources().getConfiguration());
    }

    public void onPointerEvent(MotionEvent event) {
        if (!event.isTouchEvent()) {
            return;
        }
        if (MiuiFreeFormGestureDetector.mIsMtbfOrMonkeyRunning) {
            Slog.d(TAG, "MTBF Or Monkey is Running");
        } else if (this.mFreeFormWindowMotionHelper.mCurrentAnimation == -1 || !this.mGestureController.mDisplayContent.isStackVisible(5)) {
            MotionEvent motionEvent = event;
            int action = motionEvent.getActionMasked();
            if (action == 0) {
                this.mGestureController.mWindowController.removeOpenCloseTipWindow();
                synchronizeFreeFormWindowInfo();
            }
            MiuiFreeFormWindowController miuiFreeFormWindowController = this.mGestureController.mWindowController;
            if (MiuiFreeFormWindowController.mTipShowing) {
                Slog.d(TAG, "Tip window is Showing");
            } else if (!mHadDealWithDownAction && action != 0) {
                Slog.d(TAG, "Did not DealWith DownAction");
            } else if (action == 5) {
                Slog.w(TAG, "will ignore multi-touch event");
                this.mInMultiTouch = true;
            } else {
                if (MiuiFreeFormGestureController.DEBUG) {
                    Slog.d(TAG, "onPointerEvent action:" + action + " mInMultiTouch:" + this.mInMultiTouch);
                }
                if (this.mGestureController.mKeyguardManager.isKeyguardLocked()) {
                    Slog.d(TAG, "Keyguard was Locked");
                } else if (this.mScreenshotWindow != null) {
                    Slog.d(TAG, "ScreenshotWindow is showing");
                } else if (this.mScreenRecorderWindow != null) {
                    Slog.d(TAG, "ScreenRecorderWindow is showing");
                } else {
                    if (mCurrentWindowMode == 1) {
                        boolean result = this.mTouchGestureDetector.onTouchEvent(motionEvent);
                        Slog.d(TAG, "touchGestureDetector result:" + result);
                    }
                    if (action != 0) {
                        if (action != 1) {
                            if (action != 2) {
                                if (action != 3) {
                                    return;
                                }
                            } else if (!this.mInMultiTouch) {
                                this.mHandler.obtainMessage(1, motionEvent).sendToTarget();
                                return;
                            } else {
                                return;
                            }
                        }
                        if (this.mInMultiTouch) {
                            this.mInMultiTouch = false;
                        }
                        this.mHandler.obtainMessage(2, motionEvent).sendToTarget();
                        return;
                    }
                    this.mHandler.obtainMessage(0, motionEvent).sendToTarget();
                }
            }
        } else {
            Slog.d(TAG, this.mFreeFormWindowMotionHelper.mCurrentAnimation + " animation is in progress");
        }
    }

    public void startShowSmallFreeFormWindow() {
        Slog.d(TAG, "startShowSmallFreeFormWindow");
        mCurrentWindowMode = 1;
        hideCaptionView();
        unregisterInputConsumer();
        registerInputConsumer();
        for (AppWindowToken aToken : this.mFreeFormAppTokens) {
            aToken.mIgnoreInput = true;
        }
        synchronized (this.mService.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                this.mGestureController.mDisplayContent.getInputMonitor().forceUpdateImmediately();
            } finally {
                while (true) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                }
            }
        }
        WindowManagerService.resetPriorityAfterLockedSection();
        this.mGestureController.mDisplayContent.getInputMonitor().forceUpdateImmediately();
        Settings.Secure.putIntForUser(this.mService.mContext.getContentResolver(), "freeform_window_state", 1, -2);
        synchronized (this.mService.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                this.mService.updateFocusedWindowLocked(0, true);
            } catch (Throwable th) {
                while (true) {
                    throw th;
                }
            }
        }
        WindowManagerService.resetPriorityAfterLockedSection();
    }

    public void turnFreeFormToSmallWindow() {
        Slog.d(TAG, "turnFreeFormToSmallWindow");
        if (mCurrentWindowMode == 0) {
            synchronizeFreeFormWindowInfo();
            mCurrentWindowMode = 1;
            hideCaptionView();
            this.mFreeFormWindowMotionHelper.turnFreeFormToSmallWindow();
            Settings.Secure.putIntForUser(this.mService.mContext.getContentResolver(), "freeform_window_state", 1, -2);
        }
    }

    public void startFullScreenFromFreeFormAnimation() {
        synchronizeFreeFormWindowInfo();
        for (int i = this.mFreeFormAppWindows.size() - 1; i >= 0; i--) {
            WindowState win = this.mFreeFormAppWindows.get(i);
            win.mAttrs.privateFlags |= 64;
            this.mGestureAnimator.recreateLeashIfNeeded(win.mAppToken);
        }
        if (this.mIsPortrait == 0) {
            if (!this.mFreeFormAppWindows.isEmpty()) {
                this.mGestureController.mWindowController.setStartBounds(new Rect(this.mWindowBounds.left, this.mWindowBounds.top, (int) (((float) this.mWindowBounds.left) + (((float) this.mWindowBounds.width()) * MiuiMultiWindowUtils.sScale)), (int) (((float) this.mWindowBounds.top) + (((float) this.mWindowBounds.height()) * MiuiMultiWindowUtils.sScale))));
                this.mGestureController.mWindowController.startContentAnimation(1, this.mComponentName.getPackageName());
            }
        } else if (this.mIsLandcapeFreeform) {
            MiuiFreeFormWindowController.DropWindowType = 0;
            this.mGestureController.mWindowController.setStartBounds(new Rect(this.mWindowBounds.left, this.mWindowBounds.top, (int) (((float) this.mWindowBounds.left) + (((float) this.mWindowBounds.width()) * MiuiMultiWindowUtils.sScale)), (int) (((float) this.mWindowBounds.top) + (((float) this.mWindowBounds.height()) * MiuiMultiWindowUtils.sScale))));
            this.mGestureController.mWindowController.startContentAnimation(1, this.mComponentName.getPackageName());
        } else {
            this.mFreeFormWindowMotionHelper.startGestureAnimation(17);
        }
    }

    public void startFullScreenFromSmallAnimation() {
        this.mSmallFreeFormWindowMotionHelper.startShowFullScreenWindow();
    }

    public void upwardMovementSmallWindow(int position) {
        Rect finalWindowDragBounds = MiuiMultiWindowUtils.findNearestCorner(this.mService.mContext, 0.0f, 0.0f, position, this.mIsLandcapeFreeform);
        int left = (this.mScreenWidth - MiuiMultiWindowUtils.SMALL_FREEFORM_DOWNWARD_RIGHT_MARGIN) - finalWindowDragBounds.width();
        int top = MiuiMultiWindowUtils.SMALL_FREEFORM_DOWNWARD_TOP_MARGIN;
        this.mTaskPositioner.translateAnimal(new Rect(left, top, this.mScreenWidth - MiuiMultiWindowUtils.SMALL_FREEFORM_DOWNWARD_RIGHT_MARGIN, finalWindowDragBounds.height() + top), finalWindowDragBounds, 9, true);
    }

    public void downwardMovementSmallWindow(int position) {
        Rect startWindowDragBounds = MiuiMultiWindowUtils.findNearestCorner(this.mService.mContext, 0.0f, 0.0f, position, this.mIsLandcapeFreeform);
        int left = (this.mScreenWidth - MiuiMultiWindowUtils.SMALL_FREEFORM_DOWNWARD_RIGHT_MARGIN) - startWindowDragBounds.width();
        int top = MiuiMultiWindowUtils.SMALL_FREEFORM_DOWNWARD_TOP_MARGIN;
        this.mTaskPositioner.translateAnimal(startWindowDragBounds, new Rect(left, top, this.mScreenWidth - MiuiMultiWindowUtils.SMALL_FREEFORM_DOWNWARD_RIGHT_MARGIN, startWindowDragBounds.height() + top), 9, true);
    }

    public void inputMethodVisibleChanged(int inputMethodHeight) {
        int i = mCurrentWindowMode;
        if (i == 0 || i != 1 || !this.mIsPortrait || inputMethodHeight == 0 || this.mFreeFormWindowMotionHelper.mPosY <= ((float) (this.mScreenHeight / 2))) {
            return;
        }
        if (this.mFreeFormWindowMotionHelper.mPosX > ((float) (this.mScreenWidth / 2))) {
            upwardMovementSmallWindow(2);
        } else {
            upwardMovementSmallWindow(1);
        }
    }

    public void launchSmallFreeFormWindow() {
        Slog.d(TAG, "launchSmallFreeFormWindow mCurrentWindowMode:" + mCurrentWindowMode);
        this.mHandler.postDelayed(new Runnable() {
            public void run() {
                if (MiuiFreeFormGesturePointerEventListener.mCurrentWindowMode == -1) {
                    Settings.Secure.putIntForUser(MiuiFreeFormGesturePointerEventListener.this.mService.mContext.getContentResolver(), "freeform_window_state", 1, -2);
                    MiuiFreeFormGesturePointerEventListener.mCurrentWindowMode = 1;
                    MiuiFreeFormGesturePointerEventListener.this.hideCaptionView();
                    MiuiFreeFormGesturePointerEventListener.this.synchronizeFreeFormWindowInfo();
                    if (MiuiFreeFormGesturePointerEventListener.this.mTopFreeFormAppToken != null) {
                        MiuiFreeFormGesturePointerEventListener.this.mService.mTaskSnapshotController.handleSnapshotTaskByGesture(MiuiFreeFormGesturePointerEventListener.this.mTopFreeFormAppToken);
                        if (MiuiFreeFormGesturePointerEventListener.this.mComponentName != null) {
                            MiuiFreeFormGesturePointerEventListener.this.mGestureController.setFreeformPackageName(MiuiFreeFormGesturePointerEventListener.this.mComponentName.getPackageName());
                        }
                    }
                    DisplayContent displayContent = MiuiFreeFormGesturePointerEventListener.this.mGestureController.mDisplayContent;
                    MiuiFreeFormGesturePointerEventListener.this.updateScreenParams(displayContent, displayContent.getConfiguration());
                    Iterator<WindowState> it = MiuiFreeFormGesturePointerEventListener.this.mFreeFormAppWindows.iterator();
                    while (it.hasNext()) {
                        WindowState win = it.next();
                        win.mAttrs.privateFlags |= 64;
                        MiuiFreeFormGesturePointerEventListener.this.mGestureAnimator.recreateLeashIfNeeded(win.mAppToken);
                        MiuiFreeFormGesturePointerEventListener.this.mGestureAnimator.applyTransaction();
                        win.mWinAnimator.mSurfaceController.mSurfaceControl.setCornerRadius(MiuiMultiWindowUtils.FREEFORM_ROUND_CORNER);
                        win.mAppToken.mIgnoreInput = true;
                    }
                    synchronized (MiuiFreeFormGesturePointerEventListener.this.mService.mGlobalLock) {
                        try {
                            WindowManagerService.boostPriorityForLockedSection();
                            MiuiFreeFormGesturePointerEventListener.this.mGestureController.mDisplayContent.getInputMonitor().forceUpdateImmediately();
                        } catch (Throwable th) {
                            while (true) {
                                WindowManagerService.resetPriorityAfterLockedSection();
                                throw th;
                            }
                        }
                    }
                    WindowManagerService.resetPriorityAfterLockedSection();
                    if (!MiuiFreeFormGesturePointerEventListener.this.mIsPortrait || !MiuiFreeFormGesturePointerEventListener.this.mIsLandcapeFreeform) {
                        MiuiFreeFormGesturePointerEventListener.this.mFreeFormWindowMotionHelper.startGestureAnimation(14);
                        MiuiFreeFormGesturePointerEventListener.this.mFreeFormWindowMotionHelper.startGestureAnimation(12);
                        return;
                    }
                    MiuiFreeFormGesturePointerEventListener.this.startSmallFreeformWithoutAnimation();
                }
            }
        }, 300);
    }

    public void startSmallFreeformWithoutAnimation() {
        float endY;
        float endX;
        Rect lastBounds = MiuiMultiWindowUtils.getFreeformRect(this.mService.mContext, false, false, false, this.mIsLandcapeFreeform);
        this.mFreeFormWindowMotionHelper.mLastFreeFormWindowStartBounds.set(lastBounds);
        this.mFreeFormWindowMotionHelper.mWindowHeight = ((float) lastBounds.height()) * MiuiMultiWindowUtils.sScale;
        this.mFreeFormWindowMotionHelper.mWindowWidth = ((float) lastBounds.width()) * MiuiMultiWindowUtils.sScale;
        Rect corner = MiuiMultiWindowUtils.findNearestCorner(this.mService.mContext, 0.0f, 0.0f, 2, this.mIsLandcapeFreeform);
        float endLeft = (float) (corner.left + (corner.width() / 2));
        float endTop = (float) (corner.top + (corner.height() / 2));
        if (this.mIsLandcapeFreeform) {
            float endScaleX = MiuiMultiWindowUtils.LANDCAPE_SMALL_FREEFORM_WIDTH / ((float) this.mScreenHeight);
            float endScaleX2 = MiuiMultiWindowUtils.LANDCAPE_SMALL_FREEFORM_HEIGHT;
            int i = this.mScreenWidth;
            float endScaleY = endScaleX2 / ((float) i);
            endX = endLeft - ((((float) this.mScreenHeight) * endScaleX) / 2.0f);
            endY = endTop - ((((float) i) * endScaleY) / 2.0f);
            setSmallFreeFormWindowBounds(new Rect((int) endX, (int) endY, (int) (MiuiMultiWindowUtils.LANDCAPE_SMALL_FREEFORM_WIDTH + endX), (int) (MiuiMultiWindowUtils.LANDCAPE_SMALL_FREEFORM_HEIGHT + endY)));
            this.mFreeFormWindowMotionHelper.mSmallWindowTargetHScale = MiuiMultiWindowUtils.LANDCAPE_SMALL_FREEFORM_HEIGHT / this.mFreeFormWindowMotionHelper.mWindowHeight;
            this.mFreeFormWindowMotionHelper.mSmallWindowTargetWScale = MiuiMultiWindowUtils.LANDCAPE_SMALL_FREEFORM_WIDTH / this.mFreeFormWindowMotionHelper.mWindowWidth;
            float f = endScaleY;
        } else {
            float endScaleX3 = MiuiMultiWindowUtils.SMALL_FREEFORM_WIDTH / ((float) this.mScreenWidth);
            float endScaleX4 = MiuiMultiWindowUtils.SMALL_FREEFORM_HEIGHT;
            int i2 = this.mScreenHeight;
            float endScaleY2 = endScaleX4 / ((float) i2);
            endX = endLeft - ((((float) this.mScreenWidth) * endScaleX3) / 2.0f);
            endY = endTop - ((((float) i2) * endScaleY2) / 2.0f);
            setSmallFreeFormWindowBounds(new Rect((int) endX, (int) endY, (int) (MiuiMultiWindowUtils.SMALL_FREEFORM_WIDTH + endX), (int) (MiuiMultiWindowUtils.SMALL_FREEFORM_HEIGHT + endY)));
            this.mFreeFormWindowMotionHelper.mSmallWindowTargetHScale = MiuiMultiWindowUtils.SMALL_FREEFORM_HEIGHT / this.mFreeFormWindowMotionHelper.mWindowHeight;
            this.mFreeFormWindowMotionHelper.mSmallWindowTargetWScale = MiuiMultiWindowUtils.SMALL_FREEFORM_WIDTH / this.mFreeFormWindowMotionHelper.mWindowWidth;
            float f2 = endScaleY2;
        }
        Iterator<WindowState> it = this.mFreeFormAppWindows.iterator();
        while (it.hasNext()) {
            this.mGestureAnimator.setPositionInTransaction(it.next().mAppToken, endX, endY);
            this.mGestureAnimator.applyTransaction();
        }
        Iterator<WindowState> it2 = this.mFreeFormAppWindows.iterator();
        while (it2.hasNext()) {
            WindowState win = it2.next();
            this.mGestureAnimator.hideAppWindowToken(win.mAppToken);
            this.mGestureAnimator.setMatrixInTransaction(win.mAppToken, this.mFreeFormWindowMotionHelper.mSmallWindowTargetWScale, 0.0f, 0.0f, this.mFreeFormWindowMotionHelper.mSmallWindowTargetHScale);
            this.mGestureAnimator.applyTransaction();
        }
        MiuiMultiWindowUtils.mIsMiniFreeformMode = false;
        try {
            this.mService.mActivityManager.resizeTask(this.mTaskId, lastBounds, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Iterator<WindowState> it3 = this.mFreeFormAppWindows.iterator();
        while (it3.hasNext()) {
            this.mGestureAnimator.showAppWindowToken(it3.next().mAppToken);
            this.mGestureAnimator.applyTransaction();
        }
        hideCaptionView();
        startShowSmallFreeFormWindow();
    }

    public void setRequestedOrientation(int requestedOrientation, TaskRecord taskRecord) {
        this.mFreeFormWindowMotionHelper.setRequestedOrientation(requestedOrientation, taskRecord);
    }

    public Rect getSmallFreeFormWindowBounds() {
        return this.mSmallWindowBounds;
    }

    public void setSmallFreeFormWindowBounds(Rect smallWindowBounds) {
        this.mSmallWindowBounds.set(smallWindowBounds);
    }

    public void startShowFreeFormWindow() {
        Slog.d(TAG, "startShowFreeFormWindow");
        mCurrentWindowMode = 0;
        showCaptionView();
        unregisterInputConsumer();
        for (AppWindowToken aToken : this.mFreeFormAppTokens) {
            aToken.mIgnoreInput = false;
        }
        synchronized (this.mService.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                this.mGestureController.mDisplayContent.getInputMonitor().forceUpdateImmediately();
            } finally {
                while (true) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                }
            }
        }
        WindowManagerService.resetPriorityAfterLockedSection();
        this.mGestureAnimator.reset();
        resetState();
        Settings.Secure.putIntForUser(this.mService.mContext.getContentResolver(), "freeform_window_state", 0, -2);
        synchronized (this.mService.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                this.mService.updateFocusedWindowLocked(0, true);
            } catch (Throwable th) {
                while (true) {
                    throw th;
                }
            }
        }
        WindowManagerService.resetPriorityAfterLockedSection();
    }

    public void startShowFullScreenWindow() {
        Slog.d(TAG, "startShowFullScreenWindow");
        mCurrentWindowMode = -1;
        hideCaptionView();
        this.mGestureController.notifyFullScreenWidnowModeStart();
        for (AppWindowToken aToken : this.mFreeFormAppTokens) {
            aToken.mIgnoreInput = false;
        }
        synchronized (this.mService.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                this.mGestureController.mDisplayContent.getInputMonitor().forceUpdateImmediately();
            } catch (Throwable th) {
                while (true) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
        }
        WindowManagerService.resetPriorityAfterLockedSection();
        Iterator<WindowState> it = this.mFreeFormAppWindows.iterator();
        while (it.hasNext()) {
            this.mGestureAnimator.setShadowRadiusParas(it.next(), 0.0f, 0.0f, 0.0f, 0.0f, 0.0f);
            this.mGestureAnimator.applyTransaction();
        }
        unregisterInputConsumer();
        this.mGestureController.unregisterEventListener();
        this.mGestureAnimator.reset();
        resetState();
        Settings.Secure.putIntForUser(this.mService.mContext.getContentResolver(), "freeform_window_state", -1, -2);
    }

    public void startExitApplication() {
        Slog.d(TAG, "startExitApplication");
        this.mGestureController.mWindowController.removeOverlayView();
        this.mGestureController.notifyExitFreeFormApplicationStart();
        for (AppWindowToken aToken : this.mFreeFormAppTokens) {
            this.mGestureAnimator.hideAppWindowToken(aToken);
            aToken.mIgnoreInput = false;
        }
        synchronized (this.mService.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                this.mGestureController.mDisplayContent.getInputMonitor().forceUpdateImmediately();
            } catch (Throwable th) {
                while (true) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
        }
        WindowManagerService.resetPriorityAfterLockedSection();
        Iterator<WindowState> it = this.mFreeFormAppWindows.iterator();
        while (it.hasNext()) {
            this.mGestureAnimator.setShadowRadiusParas(it.next(), 0.0f, 0.0f, 0.0f, 0.0f, 0.0f);
            this.mGestureAnimator.applyTransaction();
        }
        this.mGestureController.unregisterEventListener();
        this.mGestureAnimator.reset();
        resetState();
        mCurrentWindowMode = -1;
        Settings.Secure.putIntForUser(this.mService.mContext.getContentResolver(), "freeform_window_state", -1, -2);
    }

    public void startExitSmallFreeformApplication() {
        Slog.d(TAG, "startExitSmallFreeformApplication");
        this.mGestureController.mWindowController.removeOverlayView();
        this.mGestureController.notifyExitSmallFreeFormApplicationStart();
        for (AppWindowToken aToken : this.mFreeFormAppTokens) {
            this.mGestureAnimator.hideAppWindowToken(aToken);
            aToken.mIgnoreInput = false;
        }
        synchronized (this.mService.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                this.mGestureController.mDisplayContent.getInputMonitor().forceUpdateImmediately();
            } catch (Throwable th) {
                while (true) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
        }
        WindowManagerService.resetPriorityAfterLockedSection();
        Iterator<WindowState> it = this.mFreeFormAppWindows.iterator();
        while (it.hasNext()) {
            this.mGestureAnimator.setShadowRadiusParas(it.next(), 0.0f, 0.0f, 0.0f, 0.0f, 0.0f);
            this.mGestureAnimator.applyTransaction();
        }
        this.mGestureController.unregisterEventListener();
        unregisterInputConsumer();
        this.mGestureAnimator.reset();
        resetState();
        mCurrentWindowMode = -1;
        Settings.Secure.putIntForUser(this.mService.mContext.getContentResolver(), "freeform_window_state", -1, -2);
    }

    public void showCaptionView() {
        Slog.d(TAG, "showCaptionView");
        this.mGestureController.notifyShowCaptionView();
    }

    public void hideCaptionView() {
        Slog.d(TAG, "hideCaptionView");
        this.mGestureController.notifyHideCaptionView();
    }

    public void registerInputConsumer() {
        Slog.d(TAG, "registerInputConsumer");
        if (this.mInputEventReceiver == null) {
            InputChannel inputChanel = new InputChannel();
            int displayId = this.mGestureController.mDisplayContent.getDisplayId();
            try {
                this.mService.destroyInputConsumer(MiuiFreeFormGestureController.FREEFORM_INPUT_CONSUMER, displayId);
                this.mService.createInputConsumer(this.mToken, MiuiFreeFormGestureController.FREEFORM_INPUT_CONSUMER, displayId, inputChanel);
                this.mInputEventReceiver = new InputEventReceiver(inputChanel, Looper.getMainLooper()) {
                    public void onInputEvent(InputEvent event) {
                        MiuiFreeFormGesturePointerEventListener.super.onInputEvent(event);
                    }
                };
            } catch (Exception e) {
            }
        }
    }

    public void unregisterInputConsumer() {
        Slog.d(TAG, "unregisterInputConsumer");
        try {
            if (this.mInputEventReceiver != null) {
                this.mService.destroyInputConsumer(MiuiFreeFormGestureController.FREEFORM_INPUT_CONSUMER, this.mGestureController.mDisplayContent.getDisplayId());
                this.mInputEventReceiver.dispose();
                this.mInputEventReceiver = null;
            }
        } catch (Exception e) {
            this.mInputEventReceiver = null;
        }
    }

    /* access modifiers changed from: private */
    public void onActionDown(MotionEvent motionEvent) {
        Slog.d(TAG, "onActionDown mCurrentWindowMode " + mCurrentWindowMode);
        mHadDealWithDownAction = true;
        int i = mCurrentWindowMode;
        if (i == 1) {
            this.mSmallFreeFormWindowMotionHelper.notifyDownLocked(motionEvent);
        } else if (i == 0) {
            this.mFreeFormWindowMotionHelper.notifyDownLocked(motionEvent);
        }
    }

    /* access modifiers changed from: private */
    public void onActionMove(MotionEvent motionEvent) {
        if (MiuiFreeFormGestureController.DEBUG) {
            Slog.d(TAG, "onActionMove mCurrentWindowMode " + mCurrentWindowMode);
        }
        int i = mCurrentWindowMode;
        if (i == 1) {
            this.mSmallFreeFormWindowMotionHelper.notifyMoveLocked(motionEvent);
        } else if (i == 0) {
            this.mFreeFormWindowMotionHelper.notifyMoveLocked(motionEvent);
        }
    }

    /* access modifiers changed from: private */
    public void onActionUp(MotionEvent motionEvent) {
        Slog.d(TAG, "onActionUp mCurrentWindowMode " + mCurrentWindowMode);
        int i = mCurrentWindowMode;
        if (i == 1) {
            this.mSmallFreeFormWindowMotionHelper.notifyUpLocked(motionEvent);
        } else if (i == 0) {
            this.mFreeFormWindowMotionHelper.notifyUpLocked(motionEvent);
        }
    }

    public void notifyFreeformCouldBeenFocusWindow(boolean focus) {
    }

    public void showScreenSurface() {
        this.mFreeFormWindowMotionHelper.showScreenSurface();
    }

    public void hideScreenSurface() {
        this.mFreeFormWindowMotionHelper.hideScreenSurface();
    }

    public void synchronizeFreeFormWindowInfo() {
        if (mCurrentWindowMode != 1) {
            this.mGestureAnimator.reset();
        }
        resetState();
        findVisibleFreeFormWindowsForGestureLocked(new Consumer() {
            public final void accept(Object obj) {
                MiuiFreeFormGesturePointerEventListener.this.lambda$synchronizeFreeFormWindowInfo$0$MiuiFreeFormGesturePointerEventListener((WindowStateAnimator) obj);
            }
        });
        AppWindowToken appWindowToken = this.mTopFreeFormAppToken;
        if (appWindowToken != null) {
            this.mGestureAnimator.hideTaskDimmerLayer(appWindowToken);
            try {
                this.mComponentName = ComponentName.unflattenFromString(this.mTopFreeFormAppToken.appToken.getName());
            } catch (Exception e) {
            }
        }
        if (mCurrentWindowMode == 0) {
            for (AppWindowToken aToken : this.mFreeFormAppTokens) {
                aToken.mIgnoreInput = false;
            }
            synchronized (this.mService.mGlobalLock) {
                try {
                    WindowManagerService.boostPriorityForLockedSection();
                    this.mGestureController.mDisplayContent.getInputMonitor().forceUpdateImmediately();
                } catch (Throwable th) {
                    while (true) {
                        WindowManagerService.resetPriorityAfterLockedSection();
                        throw th;
                    }
                }
            }
            WindowManagerService.resetPriorityAfterLockedSection();
        }
    }

    public /* synthetic */ void lambda$synchronizeFreeFormWindowInfo$0$MiuiFreeFormGesturePointerEventListener(WindowStateAnimator windowStateAnimator) {
        WindowState w = windowStateAnimator.mWin;
        if (w.mWinAnimator != null && w.mWinAnimator.hasSurface()) {
            AppWindowToken aToken = w.mAppToken;
            if (aToken != null) {
                Slog.w(TAG, "add aToken: " + aToken + " window:" + w);
                this.mFreeFormAppWindows.add(w);
                if (this.mTopFreeFormAppToken == null && (w.mAttrs.type == 1 || w.mAttrs.type == 3)) {
                    this.mTopFreeFormAppWindow = w;
                    this.mTopFreeFormAppToken = aToken;
                    if (this.mTopFreeFormAppToken.getStack() != null) {
                        this.mStackId = this.mTopFreeFormAppToken.getStack().mStackId;
                        this.mStackBounds = this.mTopFreeFormAppToken.getStack().getRawBounds();
                    }
                    if (this.mTopFreeFormAppToken.getTask() != null) {
                        this.mTaskId = this.mTopFreeFormAppToken.getTask().mTaskId;
                    }
                    this.mWindowBounds = w.getBounds();
                }
                if (!this.mFreeFormAppTokens.contains(aToken)) {
                    this.mFreeFormAppTokens.add(aToken);
                }
                aToken.updateLetterboxSurface(w);
            }
            WindowStateInfo wInfo = new WindowStateInfo();
            wInfo.mOriFrame.set(w.mWindowFrames.mFrame);
            wInfo.mOriPosX = (float) w.mWindowFrames.mFrame.left;
            wInfo.mOriPosY = (float) w.mWindowFrames.mFrame.top;
            wInfo.mAppToken = aToken;
            this.mScalingWindows.put(w, wInfo);
        }
    }

    private void findVisibleFreeFormWindowsForGestureLocked(Consumer<WindowStateAnimator> callback) {
        synchronized (this.mService.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                this.mService.getDefaultDisplayContentLocked().forAllWindows((Consumer<WindowState>) new Consumer(callback) {
                    private final /* synthetic */ Consumer f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final void accept(Object obj) {
                        MiuiFreeFormGesturePointerEventListener.this.lambda$findVisibleFreeFormWindowsForGestureLocked$1$MiuiFreeFormGesturePointerEventListener(this.f$1, (WindowState) obj);
                    }
                }, true);
            } catch (Throwable th) {
                while (true) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
        }
        WindowManagerService.resetPriorityAfterLockedSection();
    }

    public /* synthetic */ void lambda$findVisibleFreeFormWindowsForGestureLocked$1$MiuiFreeFormGesturePointerEventListener(Consumer callback, WindowState w) {
        if (w != null) {
            int type = w.mAttrs.type;
            int windowMode = w.getWindowingMode();
            int activityType = w.getActivityType();
            String str = w.mAttrs.packageName;
            String title = w.mAttrs.getTitle().toString();
            if (SYSTEMUI_SCREENSHOT_LAYER_NAME.equals(title)) {
                this.mScreenshotWindow = w;
            } else if (SYSTEMUI_SCREENRECORDER_LAYER_NAME.equals(title)) {
                this.mScreenRecorderWindow = w;
            } else if (!WindowManager.LayoutParams.isSystemAlertWindowType(type)) {
                int layer = this.mService.mPolicy.getWindowLayerFromTypeLw(type);
                if (this.mService.mPolicy.getWindowLayerFromTypeLw(2000) <= layer) {
                    if (layer == this.mService.mPolicy.getWindowLayerFromTypeLw(2000)) {
                        this.mStatusBarWindow = w;
                    }
                } else if (w.mAppToken == null || activityType != 2) {
                    if ((w.mAppToken == null || activityType != 3 || w.mAttrs.type != 1) && windowMode != 3 && windowMode != 2 && (w.mAttrs.extraFlags & DumpState.DUMP_APEX) == 0) {
                        WindowStateAnimator animator = w.mWinAnimator;
                        if (windowMode == 5 && animator != null && animator.hasSurface() && animator.getShown()) {
                            if ((w.mAttrs.type == 1 || w.mAttrs.type == 2 || w.mAttrs.type == 3) && callback != null) {
                                callback.accept(animator);
                            }
                        }
                    }
                } else if (ActivityStackInjector.isDefaultHome(w.mAppToken)) {
                    MiuiFreeFormGestureDetector miuiFreeFormGestureDetector = this.mGestureDetector;
                    if (MiuiFreeFormGestureDetector.isCurrentUser(w)) {
                        this.mHomeAppToken = w.mAppToken;
                        this.mHomeWindow = w;
                    }
                }
            }
        }
    }

    public void hideInputMethodWindowIfNeeded() {
        Slog.d(TAG, "hideInputMethodWindowIfNeeded");
        DisplayContent displayContent = this.mGestureController.mDisplayContent;
        if (displayContent.mInputMethodWindow != null && displayContent.mInputMethodWindow.mWinAnimator != null && displayContent.mInputMethodWindow.mWinAnimator.getShown()) {
            if (this.mInputMethodManagerInternal == null) {
                this.mInputMethodManagerInternal = (InputMethodManagerInternal) LocalServices.getService(InputMethodManagerInternal.class);
            }
            InputMethodManagerInternal inputMethodManagerInternal = this.mInputMethodManagerInternal;
            if (inputMethodManagerInternal != null) {
                inputMethodManagerInternal.hideCurrentInputMethod();
            }
        }
    }

    private final class H extends Handler {
        private H(Looper looper) {
            super(looper);
        }

        public void handleMessage(Message msg) {
            MotionEvent motionEvent = (MotionEvent) msg.obj;
            int i = msg.what;
            if (i == 0) {
                MiuiFreeFormGesturePointerEventListener.this.onActionDown(motionEvent);
            } else if (i == 1) {
                MiuiFreeFormGesturePointerEventListener.this.onActionMove(motionEvent);
            } else if (i == 2) {
                MiuiFreeFormGesturePointerEventListener.this.onActionUp(motionEvent);
            }
        }
    }

    public void resetState() {
        synchronized (this.mLock) {
            this.mFreeFormAppWindows.clear();
            this.mFreeFormAppTokens.clear();
            this.mScalingWindows.clear();
            this.mTopFreeFormAppWindow = null;
            this.mTopFreeFormAppToken = null;
            this.mHomeAppToken = null;
            this.mHomeWindow = null;
            this.mStatusBarWindow = null;
            this.mScreenshotWindow = null;
            this.mScreenRecorderWindow = null;
        }
    }

    private int getGestureResizeWidth() {
        return dpToPx(40.0f);
    }

    public int dpToPx(float dpValue) {
        return (int) (((((float) this.mService.getDefaultDisplayContentLocked().mBaseDisplayDensity) / 160.0f) * dpValue) + 0.5f);
    }

    public void updateScreenParams(DisplayContent displayContent, Configuration configuration) {
        synchronizeFreeFormWindowInfo();
        this.mLongSide = Math.max(displayContent.mBaseDisplayHeight, displayContent.mBaseDisplayWidth);
        this.shortSide = Math.min(displayContent.mBaseDisplayHeight, displayContent.mBaseDisplayWidth);
        int ori = configuration.orientation;
        if (ori == 1) {
            this.mScreenHeight = this.mLongSide;
            this.mScreenWidth = this.shortSide;
            this.mIsPortrait = true;
            this.mNotchBar = 0;
            if (!this.mFreeFormAppWindows.isEmpty() && this.mWindowBounds.width() > this.mWindowBounds.height()) {
                this.mWidthHeightScale = (((float) MiuiMultiWindowUtils.LANDCAPE_FREEFORM_WIDTH) * 1.0f) / ((float) MiuiMultiWindowUtils.LANDCAPE_FREEFORM_HEIGHT);
                this.mMaxWidthSize = dpToPx(370.91f);
                this.mMinWidthSize = dpToPx(261.81f);
                this.mWindowWidth = (int) (((float) this.mWindowBounds.width()) * MiuiMultiWindowUtils.sScale);
                this.mWindowHeight = (int) (((float) this.mWindowBounds.height()) * MiuiMultiWindowUtils.sScale);
                this.mFreeFormWindowMotionHelper.mSmallWindowTargetHScale = MiuiMultiWindowUtils.LANDCAPE_SMALL_FREEFORM_HEIGHT / ((float) this.mWindowHeight);
                this.mFreeFormWindowMotionHelper.mSmallWindowTargetWScale = MiuiMultiWindowUtils.LANDCAPE_SMALL_FREEFORM_WIDTH / ((float) this.mWindowWidth);
            } else if (!this.mFreeFormAppWindows.isEmpty()) {
                this.mWidthHeightScale = (((float) MiuiMultiWindowUtils.FREEFORM_PORTRAIT_WIDTH) * 1.0f) / ((float) MiuiMultiWindowUtils.FREEFORM_PORTRAIT_HEIGHT);
                this.mMaxWidthSize = dpToPx(370.91f);
                this.mMinWidthSize = dpToPx(181.09f);
                this.mWindowWidth = (int) (((float) this.mWindowBounds.width()) * MiuiMultiWindowUtils.sScale);
                this.mWindowHeight = (int) (((float) this.mWindowBounds.height()) * MiuiMultiWindowUtils.sScale);
                this.mFreeFormWindowMotionHelper.mSmallWindowTargetHScale = MiuiMultiWindowUtils.SMALL_FREEFORM_HEIGHT / ((float) this.mWindowHeight);
                this.mFreeFormWindowMotionHelper.mSmallWindowTargetWScale = MiuiMultiWindowUtils.SMALL_FREEFORM_WIDTH / ((float) this.mWindowWidth);
            }
        } else {
            this.mScreenHeight = this.shortSide;
            this.mScreenWidth = this.mLongSide;
            this.mIsPortrait = false;
            this.mNotchBar = MiuiMultiWindowUtils.getNotchSize(this.mService.mContext);
            if (!this.mFreeFormAppWindows.isEmpty() && this.mWindowBounds.width() > this.mWindowBounds.height()) {
                this.mWidthHeightScale = (((float) MiuiMultiWindowUtils.LANDCAPE_FREEFORM_WIDTH) * 1.0f) / ((float) MiuiMultiWindowUtils.LANDCAPE_FREEFORM_HEIGHT);
                this.mMaxWidthSize = dpToPx(470.91f);
                this.mMinWidthSize = dpToPx(261.81f);
                this.mWindowWidth = (int) (((float) this.mWindowBounds.width()) * MiuiMultiWindowUtils.sScale);
                this.mWindowHeight = (int) (((float) this.mWindowBounds.height()) * MiuiMultiWindowUtils.sScale);
                this.mFreeFormWindowMotionHelper.mSmallWindowTargetHScale = MiuiMultiWindowUtils.LANDCAPE_SMALL_FREEFORM_HEIGHT / ((float) this.mWindowHeight);
                this.mFreeFormWindowMotionHelper.mSmallWindowTargetWScale = MiuiMultiWindowUtils.LANDCAPE_SMALL_FREEFORM_WIDTH / ((float) this.mWindowWidth);
            } else if (!this.mFreeFormAppWindows.isEmpty()) {
                this.mWidthHeightScale = (((float) MiuiMultiWindowUtils.FREEFORM_LANDCAPE_WIDTH) * 1.0f) / ((float) MiuiMultiWindowUtils.FREEFORM_LANDCAPE_HEIGHT);
                this.mMaxWidthSize = dpToPx(249.45f);
                this.mMinWidthSize = dpToPx(181.09f);
                this.mWindowWidth = (int) (((float) this.mWindowBounds.width()) * MiuiMultiWindowUtils.sScale);
                this.mWindowHeight = (int) (((float) this.mWindowBounds.height()) * MiuiMultiWindowUtils.sScale);
                this.mFreeFormWindowMotionHelper.mSmallWindowTargetHScale = MiuiMultiWindowUtils.SMALL_FREEFORM_HEIGHT / ((float) this.mWindowHeight);
                this.mFreeFormWindowMotionHelper.mSmallWindowTargetWScale = MiuiMultiWindowUtils.SMALL_FREEFORM_WIDTH / ((float) this.mWindowWidth);
            }
        }
        this.mDisplayRotation = this.mGestureController.mDisplayContent.getRotation();
        this.mIsNotchScreen = MiuiMultiWindowUtils.isNotchScreen(this.mService.mContext);
        Slog.d(TAG, "updateScreenParams ori:" + ori + " mNotchBar:" + this.mNotchBar + " mDisplayRotation:" + this.mDisplayRotation + "isNotchScreen:" + this.mIsNotchScreen);
        if (this.mLastOrientation != ori) {
            if (mCurrentWindowMode == 1 && !this.mFreeFormAppWindows.isEmpty()) {
                this.mWindowBounds = MiuiMultiWindowUtils.getFreeformRect(this.mService.mContext, true, this.mIsPortrait, false, this.mIsLandcapeFreeform);
                this.mWindowWidth = (int) (((float) this.mWindowBounds.width()) * MiuiMultiWindowUtils.sScale);
                this.mWindowHeight = (int) (((float) this.mWindowBounds.height()) * MiuiMultiWindowUtils.sScale);
                if (this.mIsLandcapeFreeform) {
                    this.mFreeFormWindowMotionHelper.mSmallWindowTargetHScale = MiuiMultiWindowUtils.LANDCAPE_SMALL_FREEFORM_HEIGHT / ((float) this.mWindowHeight);
                    this.mFreeFormWindowMotionHelper.mSmallWindowTargetWScale = MiuiMultiWindowUtils.LANDCAPE_SMALL_FREEFORM_WIDTH / ((float) this.mWindowWidth);
                } else {
                    this.mFreeFormWindowMotionHelper.mSmallWindowTargetHScale = MiuiMultiWindowUtils.SMALL_FREEFORM_HEIGHT / ((float) this.mWindowHeight);
                    this.mFreeFormWindowMotionHelper.mSmallWindowTargetWScale = MiuiMultiWindowUtils.SMALL_FREEFORM_WIDTH / ((float) this.mWindowWidth);
                }
                this.mFreeFormWindowMotionHelper.mLastFreeFormWindowStartBounds.set(this.mWindowBounds);
                this.mFreeFormWindowMotionHelper.startSmallWindowTranslateAnimal(this.mLastOrientation);
            }
            try {
                if (mCurrentWindowMode != -1) {
                    if (this.mIsPortrait) {
                        this.mGestureController.mWindowController.removeOverlayView();
                        if (this.mIsLandcapeFreeform) {
                            this.mGestureController.mWindowController.addOverlayView();
                        }
                    } else {
                        this.mGestureController.mWindowController.removeOverlayView();
                        this.mGestureController.mWindowController.addOverlayView();
                    }
                } else if (this.mIsPortrait && this.mIsLandcapeFreeform) {
                    this.mGestureController.mWindowController.removeOverlayView();
                    this.mGestureController.mWindowController.addOverlayView();
                    MiuiFreeFormWindowController.DropWindowType = 0;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        this.mLastOrientation = ori;
    }

    public void showTipWindow(int tipType, Rect windowBounds) {
        if (tipType == 1) {
            this.mGestureController.mWindowController.showTipWindow(1, windowBounds);
        }
    }

    public void setShadowRadiusParas(WindowState windowState, float shadowRadius, float shadowPosYOffset, float shadowPosZ, float shadowLightRadius, float shadowSpotColorAlpha) {
        this.mGestureAnimator.setShadowRadiusParas(windowState, shadowRadius, shadowPosYOffset, shadowPosZ, shadowLightRadius, shadowSpotColorAlpha);
    }
}
