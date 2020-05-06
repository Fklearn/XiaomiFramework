package com.android.server.wm;

import android.app.ActivityManager;
import android.app.WindowConfiguration;
import android.content.ComponentName;
import android.content.res.Configuration;
import android.graphics.GraphicBuffer;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Binder;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.SystemClock;
import android.os.Trace;
import android.util.ArraySet;
import android.util.EventLog;
import android.util.MiuiMultiWindowUtils;
import android.util.Slog;
import android.util.proto.ProtoOutputStream;
import android.view.DisplayInfo;
import android.view.IApplicationToken;
import android.view.InputApplicationHandle;
import android.view.RemoteAnimationAdapter;
import android.view.RemoteAnimationDefinition;
import android.view.SurfaceControl;
import android.view.WindowManager;
import android.view.animation.Animation;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.util.ToBooleanFunction;
import com.android.server.LocalServices;
import com.android.server.am.EventLogTags;
import com.android.server.display.color.ColorDisplayService;
import com.android.server.pm.DumpState;
import com.android.server.policy.WindowManagerPolicy;
import com.android.server.wm.RemoteAnimationController;
import com.android.server.wm.WindowManagerService;
import com.android.server.wm.WindowState;
import java.io.PrintWriter;
import java.lang.ref.WeakReference;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;

class AppWindowToken extends WindowToken implements WindowManagerService.AppFreezeListener, ConfigurationContainerListener {
    private static final int STARTING_WINDOW_TYPE_NONE = 0;
    private static final int STARTING_WINDOW_TYPE_SNAPSHOT = 1;
    private static final int STARTING_WINDOW_TYPE_SPLASH_SCREEN = 2;
    private static final String TAG = "WindowManager";
    @VisibleForTesting
    static final int Z_BOOST_BASE = 800570000;
    boolean allDrawn;
    final IApplicationToken appToken;
    boolean deferClearAllDrawn;
    boolean firstWindowDrawn;
    boolean hiddenRequested;
    boolean inPendingTransaction;
    final ComponentName mActivityComponent;
    ActivityRecord mActivityRecord;
    private final Runnable mAddStartingWindow;
    private boolean mAlwaysFocusable;
    private AnimatingAppWindowTokenRegistry mAnimatingAppWindowTokenRegistry;
    SurfaceControl mAnimationBoundsLayer;
    private AnimationDimmer mAnimationDimmer;
    boolean mAppStopped;
    private boolean mCanTurnScreenOn;
    private boolean mClientHidden;
    private final ColorDisplayService.ColorTransformController mColorTransformController;
    boolean mDeferHidingClient;
    private boolean mDisablePreviewScreenshots;
    boolean mEnteringAnimation;
    private boolean mFillsParent;
    private boolean mFreezingScreen;
    ArrayDeque<Rect> mFrozenBounds;
    ArrayDeque<Configuration> mFrozenMergedConfig;
    boolean mHandleByGesture;
    private boolean mHiddenSetFromTransferredStartingWindow;
    boolean mIgnoreInput;
    final InputApplicationHandle mInputApplicationHandle;
    long mInputDispatchingTimeoutNanos;
    boolean mIsCastMode;
    boolean mIsDummyAnimating;
    boolean mIsDummyVisible;
    boolean mIsExiting;
    boolean mIsMiuiActivityThumbnail;
    boolean mIsMiuiThumbnail;
    private boolean mLastAllDrawn;
    private AppSaturationInfo mLastAppSaturationInfo;
    private boolean mLastContainsDismissKeyguardWindow;
    private boolean mLastContainsShowWhenLockedWindow;
    private Task mLastParent;
    private boolean mLastSurfaceShowing;
    private long mLastTransactionSequence;
    boolean mLaunchTaskBehind;
    private Letterbox mLetterbox;
    boolean mNeedsAnimationBoundsLayer;
    @VisibleForTesting
    boolean mNeedsZBoost;
    private int mNumDrawnWindows;
    private int mNumInterestingWindows;
    private int mPendingRelaunchCount;
    private RemoteAnimationDefinition mRemoteAnimationDefinition;
    private boolean mRemovingFromDisplay;
    private boolean mReparenting;
    private final WindowState.UpdateReportedVisibilityResults mReportedVisibilityResults;
    int mRotationAnimationHint;
    boolean mShouldActivityTransitionRoundCorner;
    boolean mShouldAppTransitionRoundCorner;
    boolean mShowForAllUsers;
    private Rect mSizeCompatBounds;
    private float mSizeCompatScale;
    StartingData mStartingData;
    long mStartingWindowOpenTimeMillis;
    int mTargetSdk;
    private AppWindowThumbnail mThumbnail;
    private final Point mTmpPoint;
    private final Rect mTmpPrevBounds;
    private final Rect mTmpRect;
    private int mTransit;
    private SurfaceControl mTransitChangeLeash;
    private int mTransitFlags;
    private final Rect mTransitStartRect;
    private boolean mUseTransferredAnimation;
    final boolean mVoiceInteraction;
    private boolean mWillCloseOrEnterPip;
    WindowAnimationSpec mWindowAnimationSpec;
    boolean removed;
    private boolean reportedDrawn;
    boolean reportedVisible;
    boolean startingDisplayed;
    boolean startingMoved;
    WindowManagerPolicy.StartingSurface startingSurface;
    WindowState startingWindow;

    public /* synthetic */ void lambda$new$1$AppWindowToken(float[] matrix, float[] translation) {
        this.mWmService.mH.post(new Runnable(matrix, translation) {
            private final /* synthetic */ float[] f$1;
            private final /* synthetic */ float[] f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void run() {
                AppWindowToken.this.lambda$new$0$AppWindowToken(this.f$1, this.f$2);
            }
        });
    }

    public /* synthetic */ void lambda$new$0$AppWindowToken(float[] matrix, float[] translation) {
        synchronized (this.mWmService.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                if (this.mLastAppSaturationInfo == null) {
                    this.mLastAppSaturationInfo = new AppSaturationInfo();
                }
                this.mLastAppSaturationInfo.setSaturation(matrix, translation);
                updateColorTransform();
            } catch (Throwable th) {
                while (true) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
        }
        WindowManagerService.resetPriorityAfterLockedSection();
    }

    /* JADX INFO: this call moved to the top of the method (can break code semantics) */
    AppWindowToken(WindowManagerService service, IApplicationToken token, ComponentName activityComponent, boolean voiceInteraction, DisplayContent dc, long inputDispatchingTimeoutNanos, boolean fullscreen, boolean showForAllUsers, int targetSdk, int orientation, int rotationAnimationHint, boolean launchTaskBehind, boolean alwaysFocusable, ActivityRecord activityRecord) {
        this(service, token, activityComponent, voiceInteraction, dc, fullscreen);
        ActivityRecord activityRecord2 = activityRecord;
        this.mActivityRecord = activityRecord2;
        this.mActivityRecord.registerConfigurationChangeListener(this);
        this.mInputDispatchingTimeoutNanos = inputDispatchingTimeoutNanos;
        this.mShowForAllUsers = showForAllUsers;
        this.mTargetSdk = targetSdk;
        this.mOrientation = orientation;
        this.mLaunchTaskBehind = launchTaskBehind;
        this.mAlwaysFocusable = alwaysFocusable;
        this.mRotationAnimationHint = rotationAnimationHint;
        setHidden(true);
        this.hiddenRequested = true;
        ((ColorDisplayService.ColorDisplayServiceInternal) LocalServices.getService(ColorDisplayService.ColorDisplayServiceInternal.class)).attachColorTransformController(activityRecord2.packageName, activityRecord2.mUserId, new WeakReference(this.mColorTransformController));
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    AppWindowToken(WindowManagerService service, IApplicationToken token, ComponentName activityComponent, boolean voiceInteraction, DisplayContent dc, boolean fillsParent) {
        super(service, token != null ? token.asBinder() : null, 2, true, dc, false);
        this.mRemovingFromDisplay = false;
        this.mLastTransactionSequence = Long.MIN_VALUE;
        this.mReportedVisibilityResults = new WindowState.UpdateReportedVisibilityResults();
        this.mFrozenBounds = new ArrayDeque<>();
        this.mFrozenMergedConfig = new ArrayDeque<>();
        this.mSizeCompatScale = 1.0f;
        this.mCanTurnScreenOn = true;
        this.mLastSurfaceShowing = true;
        this.mTransitStartRect = new Rect();
        this.mTransitChangeLeash = null;
        this.mTmpPoint = new Point();
        this.mTmpRect = new Rect();
        this.mTmpPrevBounds = new Rect();
        this.mColorTransformController = new ColorDisplayService.ColorTransformController() {
            public final void applyAppSaturation(float[] fArr, float[] fArr2) {
                AppWindowToken.this.lambda$new$1$AppWindowToken(fArr, fArr2);
            }
        };
        this.mAddStartingWindow = new Runnable() {
            /* JADX WARNING: Code restructure failed: missing block: B:22:0x0041, code lost:
                com.android.server.wm.WindowManagerService.resetPriorityAfterLockedSection();
                r6.this$0.mStartingWindowOpenTimeMillis = android.os.SystemClock.uptimeMillis();
                r0 = null;
             */
            /* JADX WARNING: Code restructure failed: missing block: B:25:0x0053, code lost:
                r0 = r1.createStartingSurface(r6.this$0);
             */
            /* JADX WARNING: Code restructure failed: missing block: B:26:0x0055, code lost:
                r3 = move-exception;
             */
            /* JADX WARNING: Code restructure failed: missing block: B:27:0x0056, code lost:
                android.util.Slog.w("WindowManager", "Exception when adding starting window", r3);
             */
            /* Code decompiled incorrectly, please refer to instructions dump. */
            public void run() {
                /*
                    r6 = this;
                    com.android.server.wm.AppWindowToken r0 = com.android.server.wm.AppWindowToken.this
                    com.android.server.wm.WindowManagerService r0 = r0.mWmService
                    com.android.server.wm.WindowManagerGlobalLock r0 = r0.mGlobalLock
                    monitor-enter(r0)
                    com.android.server.wm.WindowManagerService.boostPriorityForLockedSection()     // Catch:{ all -> 0x0096 }
                    com.android.server.wm.AppWindowToken r1 = com.android.server.wm.AppWindowToken.this     // Catch:{ all -> 0x0096 }
                    com.android.server.wm.WindowManagerService r1 = r1.mWmService     // Catch:{ all -> 0x0096 }
                    android.os.Handler r1 = r1.mAnimationHandler     // Catch:{ all -> 0x0096 }
                    r1.removeCallbacks(r6)     // Catch:{ all -> 0x0096 }
                    com.android.server.wm.AppWindowToken r1 = com.android.server.wm.AppWindowToken.this     // Catch:{ all -> 0x0096 }
                    com.android.server.wm.StartingData r1 = r1.mStartingData     // Catch:{ all -> 0x0096 }
                    if (r1 != 0) goto L_0x001e
                    monitor-exit(r0)     // Catch:{ all -> 0x0096 }
                    com.android.server.wm.WindowManagerService.resetPriorityAfterLockedSection()
                    return
                L_0x001e:
                    com.android.server.wm.AppWindowToken r1 = com.android.server.wm.AppWindowToken.this     // Catch:{ all -> 0x0096 }
                    com.android.server.wm.WindowState r1 = r1.findMainWindow()     // Catch:{ all -> 0x0096 }
                    r2 = 0
                    if (r1 == 0) goto L_0x003b
                    com.android.server.wm.WindowStateAnimator r3 = r1.mWinAnimator     // Catch:{ all -> 0x0096 }
                    if (r3 == 0) goto L_0x003b
                    com.android.server.wm.WindowStateAnimator r3 = r1.mWinAnimator     // Catch:{ all -> 0x0096 }
                    int r3 = r3.mDrawState     // Catch:{ all -> 0x0096 }
                    r4 = 4
                    if (r3 != r4) goto L_0x003b
                    com.android.server.wm.AppWindowToken r3 = com.android.server.wm.AppWindowToken.this     // Catch:{ all -> 0x0096 }
                    r3.mStartingData = r2     // Catch:{ all -> 0x0096 }
                    monitor-exit(r0)     // Catch:{ all -> 0x0096 }
                    com.android.server.wm.WindowManagerService.resetPriorityAfterLockedSection()
                    return
                L_0x003b:
                    com.android.server.wm.AppWindowToken r3 = com.android.server.wm.AppWindowToken.this     // Catch:{ all -> 0x0096 }
                    com.android.server.wm.StartingData r3 = r3.mStartingData     // Catch:{ all -> 0x0096 }
                    r1 = r3
                    monitor-exit(r0)     // Catch:{ all -> 0x0096 }
                    com.android.server.wm.WindowManagerService.resetPriorityAfterLockedSection()
                    com.android.server.wm.AppWindowToken r0 = com.android.server.wm.AppWindowToken.this
                    long r3 = android.os.SystemClock.uptimeMillis()
                    r0.mStartingWindowOpenTimeMillis = r3
                    r0 = 0
                    com.android.server.wm.AppWindowToken r3 = com.android.server.wm.AppWindowToken.this     // Catch:{ Exception -> 0x0055 }
                    com.android.server.policy.WindowManagerPolicy$StartingSurface r3 = r1.createStartingSurface(r3)     // Catch:{ Exception -> 0x0055 }
                    r0 = r3
                    goto L_0x005d
                L_0x0055:
                    r3 = move-exception
                    java.lang.String r4 = "WindowManager"
                    java.lang.String r5 = "Exception when adding starting window"
                    android.util.Slog.w(r4, r5, r3)
                L_0x005d:
                    if (r0 == 0) goto L_0x0095
                    r3 = 0
                    com.android.server.wm.AppWindowToken r4 = com.android.server.wm.AppWindowToken.this
                    com.android.server.wm.WindowManagerService r4 = r4.mWmService
                    com.android.server.wm.WindowManagerGlobalLock r4 = r4.mGlobalLock
                    monitor-enter(r4)
                    com.android.server.wm.WindowManagerService.boostPriorityForLockedSection()     // Catch:{ all -> 0x008f }
                    com.android.server.wm.AppWindowToken r5 = com.android.server.wm.AppWindowToken.this     // Catch:{ all -> 0x008f }
                    boolean r5 = r5.removed     // Catch:{ all -> 0x008f }
                    if (r5 != 0) goto L_0x007c
                    com.android.server.wm.AppWindowToken r5 = com.android.server.wm.AppWindowToken.this     // Catch:{ all -> 0x008f }
                    com.android.server.wm.StartingData r5 = r5.mStartingData     // Catch:{ all -> 0x008f }
                    if (r5 != 0) goto L_0x0077
                    goto L_0x007c
                L_0x0077:
                    com.android.server.wm.AppWindowToken r2 = com.android.server.wm.AppWindowToken.this     // Catch:{ all -> 0x008f }
                    r2.startingSurface = r0     // Catch:{ all -> 0x008f }
                    goto L_0x0085
                L_0x007c:
                    com.android.server.wm.AppWindowToken r5 = com.android.server.wm.AppWindowToken.this     // Catch:{ all -> 0x008f }
                    r5.startingWindow = r2     // Catch:{ all -> 0x008f }
                    com.android.server.wm.AppWindowToken r5 = com.android.server.wm.AppWindowToken.this     // Catch:{ all -> 0x008f }
                    r5.mStartingData = r2     // Catch:{ all -> 0x008f }
                    r3 = 1
                L_0x0085:
                    monitor-exit(r4)     // Catch:{ all -> 0x008f }
                    com.android.server.wm.WindowManagerService.resetPriorityAfterLockedSection()
                    if (r3 == 0) goto L_0x0095
                    r0.remove()
                    goto L_0x0095
                L_0x008f:
                    r2 = move-exception
                    monitor-exit(r4)     // Catch:{ all -> 0x008f }
                    com.android.server.wm.WindowManagerService.resetPriorityAfterLockedSection()
                    throw r2
                L_0x0095:
                    return
                L_0x0096:
                    r1 = move-exception
                    monitor-exit(r0)     // Catch:{ all -> 0x0096 }
                    com.android.server.wm.WindowManagerService.resetPriorityAfterLockedSection()
                    throw r1
                */
                throw new UnsupportedOperationException("Method not decompiled: com.android.server.wm.AppWindowToken.AnonymousClass1.run():void");
            }
        };
        this.appToken = token;
        this.mActivityComponent = activityComponent;
        this.mVoiceInteraction = voiceInteraction;
        this.mFillsParent = fillsParent;
        this.mInputApplicationHandle = new InputApplicationHandle(this.appToken.asBinder());
    }

    /* access modifiers changed from: package-private */
    public void onFirstWindowDrawn(WindowState win, WindowStateAnimator winAnimator) {
        this.firstWindowDrawn = true;
        removeDeadWindows();
        if (this.startingWindow != null) {
            win.cancelAnimation();
        }
        removeStartingWindow();
        updateReportedVisibilityLocked();
    }

    /* access modifiers changed from: package-private */
    public void updateReportedVisibilityLocked() {
        if (this.appToken != null) {
            int count = this.mChildren.size();
            this.mReportedVisibilityResults.reset();
            for (int i = 0; i < count; i++) {
                ((WindowState) this.mChildren.get(i)).updateReportedVisibility(this.mReportedVisibilityResults);
            }
            int numInteresting = this.mReportedVisibilityResults.numInteresting;
            int numVisible = this.mReportedVisibilityResults.numVisible;
            int numDrawn = this.mReportedVisibilityResults.numDrawn;
            boolean nowGone = this.mReportedVisibilityResults.nowGone;
            boolean nowVisible = false;
            boolean nowDrawn = numInteresting > 0 && numDrawn >= numInteresting;
            if (numInteresting > 0 && numVisible >= numInteresting && !isHidden()) {
                nowVisible = true;
            }
            if (!nowGone) {
                if (!nowDrawn) {
                    nowDrawn = this.reportedDrawn;
                }
                if (!nowVisible) {
                    nowVisible = this.reportedVisible;
                }
            }
            if (nowDrawn != this.reportedDrawn) {
                ActivityRecord activityRecord = this.mActivityRecord;
                if (activityRecord != null) {
                    activityRecord.onWindowsDrawn(nowDrawn, SystemClock.uptimeMillis());
                }
                this.reportedDrawn = nowDrawn;
            }
            if (nowVisible != this.reportedVisible) {
                this.reportedVisible = nowVisible;
                if (this.mActivityRecord == null) {
                    return;
                }
                if (nowVisible) {
                    onWindowsVisible();
                } else {
                    onWindowsGone();
                }
            }
        }
    }

    private void onWindowsGone() {
        ActivityRecord activityRecord = this.mActivityRecord;
        if (activityRecord != null) {
            activityRecord.onWindowsGone();
        }
    }

    private void onWindowsVisible() {
        ActivityRecord activityRecord = this.mActivityRecord;
        if (activityRecord != null) {
            activityRecord.onWindowsVisible();
        }
    }

    /* access modifiers changed from: package-private */
    public boolean isClientHidden() {
        return this.mClientHidden;
    }

    /* access modifiers changed from: package-private */
    public void setClientHidden(boolean hideClient) {
        if (this.mClientHidden == hideClient) {
            return;
        }
        if (!hideClient || !this.mDeferHidingClient) {
            this.mClientHidden = hideClient;
            sendAppVisibilityToClients();
        }
    }

    /* access modifiers changed from: package-private */
    public void setVisibility(boolean visible, boolean deferHidingClient) {
        WindowState win;
        AppWindowToken focusedToken;
        AppTransition appTransition = getDisplayContent().mAppTransition;
        if (visible || !this.hiddenRequested) {
            DisplayContent displayContent = getDisplayContent();
            displayContent.mOpeningApps.remove(this);
            displayContent.mClosingApps.remove(this);
            if (isInChangeTransition()) {
                clearChangeLeash(getPendingTransaction(), true);
            }
            displayContent.mChangingApps.remove(this);
            this.waitingToShow = false;
            this.hiddenRequested = !visible;
            this.mDeferHidingClient = deferHidingClient;
            if (!visible) {
                removeDeadWindows();
            } else {
                if (!appTransition.isTransitionSet() && appTransition.isReady()) {
                    displayContent.mOpeningApps.add(this);
                }
                this.startingMoved = false;
                if (isHidden() || this.mAppStopped) {
                    clearAllDrawn();
                    if (isHidden()) {
                        this.waitingToShow = true;
                        forAllWindows((Consumer<WindowState>) new Consumer() {
                            public final void accept(Object obj) {
                                AppWindowToken.this.lambda$setVisibility$2$AppWindowToken((WindowState) obj);
                            }
                        }, true);
                    }
                }
                setClientHidden(false);
                requestUpdateWallpaperIfNeeded();
                this.mAppStopped = false;
                transferStartingWindowFromHiddenAboveTokenIfNeeded();
            }
            if (!okToAnimate() || !appTransition.isTransitionSet()) {
                commitVisibility((WindowManager.LayoutParams) null, visible, -1, true, this.mVoiceInteraction);
                updateReportedVisibilityLocked();
                return;
            }
            this.inPendingTransaction = true;
            if (visible) {
                displayContent.mOpeningApps.add(this);
                this.mEnteringAnimation = true;
            } else {
                displayContent.mClosingApps.add(this);
                this.mEnteringAnimation = false;
            }
            if (!(appTransition.getAppTransition() != 16 || (win = getDisplayContent().findFocusedWindow()) == null || (focusedToken = win.mAppToken) == null)) {
                focusedToken.setHidden(true);
                displayContent.mOpeningApps.add(focusedToken);
            }
            reportDescendantOrientationChangeIfNeeded();
        } else if (!deferHidingClient && this.mDeferHidingClient) {
            this.mDeferHidingClient = deferHidingClient;
            setClientHidden(true);
        }
    }

    public /* synthetic */ void lambda$setVisibility$2$AppWindowToken(WindowState w) {
        boolean isKeyguardSaveToken = (w.mAppToken == null || this.mWmService.mSaveSurfaceByKeyguardToken == null || w.mAppToken != this.mWmService.mSaveSurfaceByKeyguardToken) ? false : true;
        if (w.mWinAnimator.mDrawState == 4 && !isKeyguardSaveToken) {
            w.mWinAnimator.resetDrawState();
            w.resetLastContentInsets();
        }
    }

    /* access modifiers changed from: package-private */
    public boolean commitVisibility(WindowManager.LayoutParams lp, boolean visible, int transit, boolean performLayout, boolean isVoiceInteraction) {
        boolean z = visible;
        int i = transit;
        boolean delayed = false;
        this.inPendingTransaction = false;
        this.mHiddenSetFromTransferredStartingWindow = false;
        boolean visibilityChanged = false;
        if (isHidden() == z || ((isHidden() && this.mIsExiting) || (z && waitingForReplacement()))) {
            AccessibilityController accessibilityController = this.mWmService.mAccessibilityController;
            boolean changed = false;
            boolean runningAppAnimation = false;
            if (i != -1) {
                if (this.mUseTransferredAnimation) {
                    runningAppAnimation = isReallyAnimating();
                    WindowManager.LayoutParams layoutParams = lp;
                    boolean z2 = isVoiceInteraction;
                } else if (applyAnimationLocked(lp, i, z, isVoiceInteraction)) {
                    runningAppAnimation = true;
                }
                delayed = runningAppAnimation;
                WindowState window = findMainWindow();
                if (!(window == null || accessibilityController == null)) {
                    accessibilityController.onAppWindowTransitionLocked(window, i);
                }
                changed = true;
            } else {
                WindowManager.LayoutParams layoutParams2 = lp;
                boolean z3 = isVoiceInteraction;
            }
            int windowsCount = this.mChildren.size();
            for (int i2 = 0; i2 < windowsCount; i2++) {
                changed |= ((WindowState) this.mChildren.get(i2)).onAppVisibilityChanged(z, runningAppAnimation);
            }
            setHidden(!z);
            this.hiddenRequested = !z;
            visibilityChanged = true;
            if (!z) {
                stopFreezingScreen(true, true);
            } else {
                WindowState windowState = this.startingWindow;
                if (windowState != null && !windowState.isDrawnLw()) {
                    this.startingWindow.clearPolicyVisibilityFlag(1);
                    this.startingWindow.mLegacyPolicyVisibilityAfterAnim = false;
                }
                WindowManagerService windowManagerService = this.mWmService;
                Objects.requireNonNull(windowManagerService);
                forAllWindows((Consumer<WindowState>) new Consumer() {
                    public final void accept(Object obj) {
                        WindowManagerService.this.makeWindowFreezingScreenIfNeededLocked((WindowState) obj);
                    }
                }, true);
            }
            if (changed) {
                getDisplayContent().getInputMonitor().setUpdateInputWindowsNeededLw();
                if (performLayout) {
                    this.mWmService.updateFocusedWindowLocked(3, false);
                    this.mWmService.mWindowPlacerLocked.performSurfacePlacement();
                }
                getDisplayContent().getInputMonitor().updateInputWindowsLw(false);
            }
        } else {
            WindowManager.LayoutParams layoutParams3 = lp;
            boolean z4 = isVoiceInteraction;
        }
        this.mUseTransferredAnimation = false;
        if (isReallyAnimating()) {
            delayed = true;
        } else {
            onAnimationFinished();
        }
        for (int i3 = this.mChildren.size() - 1; i3 >= 0 && !delayed; i3--) {
            if (((WindowState) this.mChildren.get(i3)).isSelfOrChildAnimating()) {
                delayed = true;
            }
        }
        if (visibilityChanged) {
            if (z && !delayed) {
                this.mEnteringAnimation = true;
                this.mWmService.mActivityManagerAppTransitionNotifier.onAppTransitionFinishedLocked(this.token);
            }
            if (z || !isReallyAnimating()) {
                setClientHidden(!z);
            }
            if (!getDisplayContent().mClosingApps.contains(this) && !getDisplayContent().mOpeningApps.contains(this)) {
                getDisplayContent().getDockedDividerController().notifyAppVisibilityChanged();
                this.mWmService.mTaskSnapshotController.notifyAppVisibilityChanged(this, z);
            }
            if (isHidden() && !delayed && !getDisplayContent().mAppTransition.isTransitionSet()) {
                SurfaceControl.openTransaction();
                for (int i4 = this.mChildren.size() - 1; i4 >= 0; i4--) {
                    ((WindowState) this.mChildren.get(i4)).mWinAnimator.hide("immediately hidden");
                }
                SurfaceControl.closeTransaction();
            }
            reportDescendantOrientationChangeIfNeeded();
        }
        return delayed;
    }

    private void reportDescendantOrientationChangeIfNeeded() {
        if (this.mActivityRecord.getRequestedConfigurationOrientation() != getConfiguration().orientation && getOrientationIgnoreVisibility() != -2) {
            ActivityRecord activityRecord = this.mActivityRecord;
            onDescendantOrientationChanged(activityRecord.mayFreezeScreenLocked(activityRecord.app) ? this.mActivityRecord.appToken : null, this.mActivityRecord);
        }
    }

    /* access modifiers changed from: package-private */
    public WindowState getTopFullscreenWindow() {
        for (int i = this.mChildren.size() - 1; i >= 0; i--) {
            WindowState win = (WindowState) this.mChildren.get(i);
            if (win != null && win.mAttrs.isFullscreen()) {
                return win;
            }
        }
        return null;
    }

    /* access modifiers changed from: package-private */
    public WindowState findMainWindow() {
        return findMainWindow(true);
    }

    /* access modifiers changed from: package-private */
    public WindowState findMainWindow(boolean includeStartingApp) {
        WindowState candidate = null;
        for (int j = this.mChildren.size() - 1; j >= 0; j--) {
            WindowState win = (WindowState) this.mChildren.get(j);
            int type = win.mAttrs.type;
            if (type == 1 || (includeStartingApp && type == 3)) {
                if (!win.mAnimatingExit) {
                    return win;
                }
                candidate = win;
            }
        }
        return candidate;
    }

    /* access modifiers changed from: package-private */
    public boolean windowsAreFocusable() {
        if (this.mTargetSdk < 29) {
            ActivityRecord activityRecord = this.mActivityRecord;
            AppWindowToken topFocusedAppOfMyProcess = this.mWmService.mRoot.mTopFocusedAppByProcess.get(Integer.valueOf((activityRecord == null || activityRecord.app == null) ? 0 : this.mActivityRecord.app.getPid()));
            if (!(topFocusedAppOfMyProcess == null || topFocusedAppOfMyProcess == this)) {
                return false;
            }
        }
        if (getWindowConfiguration().canReceiveKeys() || this.mAlwaysFocusable) {
            return true;
        }
        return false;
    }

    /* access modifiers changed from: package-private */
    public boolean isVisible() {
        return !isHidden();
    }

    /* access modifiers changed from: package-private */
    public void removeImmediately() {
        onRemovedFromDisplay();
        ActivityRecord activityRecord = this.mActivityRecord;
        if (activityRecord != null) {
            activityRecord.unregisterConfigurationChangeListener(this);
        }
        super.removeImmediately();
    }

    /* access modifiers changed from: package-private */
    public void removeIfPossible() {
        if (inFreeformWindowingMode()) {
            this.mWmService.removeFreeformSurface();
        }
        this.mIsExiting = false;
        removeAllWindowsIfPossible();
        removeImmediately();
    }

    /* access modifiers changed from: package-private */
    public boolean checkCompleteDeferredRemoval() {
        if (this.mIsExiting) {
            removeIfPossible();
        }
        return super.checkCompleteDeferredRemoval();
    }

    /* access modifiers changed from: package-private */
    public void onRemovedFromDisplay() {
        if (!this.mRemovingFromDisplay) {
            this.mRemovingFromDisplay = true;
            boolean delayed = commitVisibility((WindowManager.LayoutParams) null, false, -1, true, this.mVoiceInteraction);
            if (this.mWmService.mDummyVisibleApp == this) {
                this.mWmService.mDummyVisibleApp = null;
            }
            getDisplayContent().mOpeningApps.remove(this);
            getDisplayContent().mChangingApps.remove(this);
            getDisplayContent().mUnknownAppVisibilityController.appRemovedOrHidden(this);
            this.mWmService.mTaskSnapshotController.onAppRemoved(this);
            this.waitingToShow = false;
            if (getDisplayContent().mClosingApps.contains(this)) {
                delayed = true;
            } else if (getDisplayContent().mAppTransition.isTransitionSet()) {
                getDisplayContent().mClosingApps.add(this);
                delayed = true;
            }
            if (this.mStartingData != null) {
                removeStartingWindow();
            }
            if (isSelfAnimating()) {
                getDisplayContent().mNoAnimationNotifyOnTransitionFinished.add(this.token);
            }
            TaskStack stack = getStack();
            if (!delayed || isEmpty()) {
                cancelAnimation();
                if (stack != null) {
                    stack.mExitingAppTokens.remove(this);
                }
                removeIfPossible();
            } else {
                if (stack != null) {
                    stack.mExitingAppTokens.add(this);
                }
                this.mIsExiting = true;
            }
            this.removed = true;
            stopFreezingScreen(true, true);
            DisplayContent dc = getDisplayContent();
            if (dc.mFocusedApp == this) {
                dc.setFocusedApp((AppWindowToken) null);
                this.mWmService.updateFocusedWindowLocked(0, true);
            }
            Letterbox letterbox = this.mLetterbox;
            if (letterbox != null) {
                letterbox.destroy();
                this.mLetterbox = null;
            }
            if (!delayed) {
                updateReportedVisibilityLocked();
            }
            this.mRemovingFromDisplay = false;
        }
    }

    /* access modifiers changed from: package-private */
    public void clearAnimatingFlags() {
        boolean wallpaperMightChange = false;
        for (int i = this.mChildren.size() - 1; i >= 0; i--) {
            wallpaperMightChange |= ((WindowState) this.mChildren.get(i)).clearAnimatingFlags();
        }
        if (wallpaperMightChange) {
            requestUpdateWallpaperIfNeeded();
        }
    }

    /* access modifiers changed from: package-private */
    public void destroySurfaces() {
        destroySurfaces(false);
    }

    private void destroySurfaces(boolean cleanupOnResume) {
        boolean destroyedSomething = false;
        ArrayList<WindowState> children = new ArrayList<>(this.mChildren);
        for (int i = children.size() - 1; i >= 0; i--) {
            destroyedSomething |= children.get(i).destroySurface(cleanupOnResume, this.mAppStopped);
        }
        if (destroyedSomething) {
            getDisplayContent().assignWindowLayers(true);
            updateLetterboxSurface((WindowState) null);
        }
    }

    /* access modifiers changed from: package-private */
    public void notifyAppResumed(boolean wasStopped) {
        this.mAppStopped = false;
        setCanTurnScreenOn(true);
        if (!wasStopped) {
            destroySurfaces(true);
        }
    }

    /* access modifiers changed from: package-private */
    public void notifyAppStopped() {
        this.mAppStopped = true;
        destroySurfaces();
        removeStartingWindow();
    }

    /* access modifiers changed from: package-private */
    public void clearAllDrawn() {
        this.allDrawn = false;
        this.deferClearAllDrawn = false;
    }

    /* access modifiers changed from: package-private */
    public Task getTask() {
        return (Task) getParent();
    }

    /* access modifiers changed from: package-private */
    public TaskStack getStack() {
        Task task = getTask();
        if (task != null) {
            return task.mStack;
        }
        return null;
    }

    /* access modifiers changed from: package-private */
    public void onParentChanged() {
        AnimatingAppWindowTokenRegistry animatingAppWindowTokenRegistry;
        super.onParentChanged();
        Task task = getTask();
        if (!this.mReparenting) {
            if (task == null) {
                getDisplayContent().mClosingApps.remove(this);
            } else {
                Task task2 = this.mLastParent;
                if (!(task2 == null || task2.mStack == null)) {
                    task.mStack.mExitingAppTokens.remove(this);
                }
            }
        }
        TaskStack stack = getStack();
        AnimatingAppWindowTokenRegistry animatingAppWindowTokenRegistry2 = this.mAnimatingAppWindowTokenRegistry;
        if (animatingAppWindowTokenRegistry2 != null) {
            animatingAppWindowTokenRegistry2.notifyFinished(this);
        }
        if (stack != null) {
            animatingAppWindowTokenRegistry = stack.getAnimatingAppWindowTokenRegistry();
        } else {
            animatingAppWindowTokenRegistry = null;
        }
        this.mAnimatingAppWindowTokenRegistry = animatingAppWindowTokenRegistry;
        this.mLastParent = task;
        updateColorTransform();
    }

    /* access modifiers changed from: package-private */
    public void postWindowRemoveStartingWindowCleanup(WindowState win) {
        if (this.startingWindow == win) {
            removeStartingWindow();
        } else if (this.mChildren.size() == 0) {
            this.mStartingData = null;
            if (this.mHiddenSetFromTransferredStartingWindow) {
                setHidden(true);
            }
        } else if (this.mChildren.size() == 1 && this.startingSurface != null && !isRelaunching()) {
            removeStartingWindow();
        }
    }

    /* access modifiers changed from: package-private */
    public void removeDeadWindows() {
        for (int winNdx = this.mChildren.size() - 1; winNdx >= 0; winNdx--) {
            WindowState win = (WindowState) this.mChildren.get(winNdx);
            if (win.mAppDied) {
                win.mDestroying = true;
                win.removeIfPossible();
            }
        }
    }

    /* access modifiers changed from: package-private */
    public boolean hasWindowsAlive() {
        for (int i = this.mChildren.size() - 1; i >= 0; i--) {
            if (!((WindowState) this.mChildren.get(i)).mAppDied) {
                return true;
            }
        }
        return false;
    }

    /* access modifiers changed from: package-private */
    public void setWillReplaceWindows(boolean animate) {
        for (int i = this.mChildren.size() - 1; i >= 0; i--) {
            ((WindowState) this.mChildren.get(i)).setWillReplaceWindow(animate);
        }
    }

    /* access modifiers changed from: package-private */
    public void setWillReplaceChildWindows() {
        for (int i = this.mChildren.size() - 1; i >= 0; i--) {
            ((WindowState) this.mChildren.get(i)).setWillReplaceChildWindows();
        }
    }

    /* access modifiers changed from: package-private */
    public void clearWillReplaceWindows() {
        for (int i = this.mChildren.size() - 1; i >= 0; i--) {
            ((WindowState) this.mChildren.get(i)).clearWillReplaceWindow();
        }
    }

    /* access modifiers changed from: package-private */
    public void requestUpdateWallpaperIfNeeded() {
        for (int i = this.mChildren.size() - 1; i >= 0; i--) {
            ((WindowState) this.mChildren.get(i)).requestUpdateWallpaperIfNeeded();
        }
    }

    /* access modifiers changed from: package-private */
    public boolean isRelaunching() {
        return this.mPendingRelaunchCount > 0;
    }

    /* access modifiers changed from: package-private */
    public boolean shouldFreezeBounds() {
        Task task = getTask();
        if (task == null || task.inFreeformWindowingMode()) {
            return false;
        }
        return getTask().isDragResizing();
    }

    /* access modifiers changed from: package-private */
    public void startRelaunching() {
        if (shouldFreezeBounds()) {
            freezeBounds();
        }
        detachChildren();
        this.mPendingRelaunchCount++;
    }

    /* access modifiers changed from: package-private */
    public void detachChildren() {
        SurfaceControl.openTransaction();
        for (int i = this.mChildren.size() - 1; i >= 0; i--) {
            ((WindowState) this.mChildren.get(i)).mWinAnimator.detachChildren();
        }
        SurfaceControl.closeTransaction();
    }

    /* access modifiers changed from: package-private */
    public void finishRelaunching() {
        unfreezeBounds();
        int i = this.mPendingRelaunchCount;
        if (i > 0) {
            this.mPendingRelaunchCount = i - 1;
        } else {
            checkKeyguardFlagsChanged();
        }
    }

    /* access modifiers changed from: package-private */
    public void clearRelaunching() {
        if (this.mPendingRelaunchCount != 0) {
            unfreezeBounds();
            this.mPendingRelaunchCount = 0;
        }
    }

    /* access modifiers changed from: protected */
    public boolean isFirstChildWindowGreaterThanSecond(WindowState newWindow, WindowState existingWindow) {
        int type1 = newWindow.mAttrs.type;
        int type2 = existingWindow.mAttrs.type;
        if (type1 == 1 && type2 != 1) {
            return false;
        }
        if (type1 == 1 || type2 != 1) {
            return (type1 == 3 && type2 != 3) || type1 == 3 || type2 != 3;
        }
        return true;
    }

    /* access modifiers changed from: package-private */
    public void addWindow(WindowState w) {
        super.addWindow(w);
        boolean gotReplacementWindow = false;
        for (int i = this.mChildren.size() - 1; i >= 0; i--) {
            gotReplacementWindow |= ((WindowState) this.mChildren.get(i)).setReplacementWindowIfNeeded(w);
        }
        if (gotReplacementWindow) {
            this.mWmService.scheduleWindowReplacementTimeouts(this);
        }
        checkKeyguardFlagsChanged();
    }

    /* access modifiers changed from: package-private */
    public void removeChild(WindowState child) {
        if (this.mChildren.contains(child)) {
            super.removeChild(child);
            checkKeyguardFlagsChanged();
            updateLetterboxSurface(child);
        }
    }

    private boolean waitingForReplacement() {
        for (int i = this.mChildren.size() - 1; i >= 0; i--) {
            if (((WindowState) this.mChildren.get(i)).waitingForReplacement()) {
                return true;
            }
        }
        return false;
    }

    /* access modifiers changed from: package-private */
    public void onWindowReplacementTimeout() {
        for (int i = this.mChildren.size() - 1; i >= 0; i--) {
            ((WindowState) this.mChildren.get(i)).onWindowReplacementTimeout();
        }
    }

    /* access modifiers changed from: package-private */
    public void reparent(Task task, int position) {
        if (task != null) {
            Task currentTask = getTask();
            if (task == currentTask) {
                throw new IllegalArgumentException("window token=" + this + " already child of task=" + currentTask);
            } else if (currentTask.mStack == task.mStack) {
                DisplayContent prevDisplayContent = getDisplayContent();
                this.mReparenting = true;
                getParent().removeChild(this);
                task.addChild(this, position);
                this.mReparenting = false;
                DisplayContent displayContent = task.getDisplayContent();
                displayContent.setLayoutNeeded();
                if (prevDisplayContent != displayContent) {
                    onDisplayChanged(displayContent);
                    prevDisplayContent.setLayoutNeeded();
                }
                getDisplayContent().layoutAndAssignWindowLayersIfNeeded();
            } else {
                throw new IllegalArgumentException("window token=" + this + " current task=" + currentTask + " belongs to a different stack than " + task);
            }
        } else {
            throw new IllegalArgumentException("reparent: could not find task");
        }
    }

    /* access modifiers changed from: package-private */
    public void onDisplayChanged(DisplayContent dc) {
        Task task;
        DisplayContent prevDc = this.mDisplayContent;
        super.onDisplayChanged(dc);
        if (prevDc != null && prevDc != this.mDisplayContent) {
            if (prevDc.mOpeningApps.remove(this)) {
                this.mDisplayContent.mOpeningApps.add(this);
                this.mDisplayContent.prepareAppTransition(prevDc.mAppTransition.getAppTransition(), true);
                this.mDisplayContent.executeAppTransition();
            }
            if (prevDc.mChangingApps.remove(this)) {
                clearChangeLeash(getPendingTransaction(), true);
            }
            prevDc.mClosingApps.remove(this);
            if (prevDc.mFocusedApp == this) {
                prevDc.setFocusedApp((AppWindowToken) null);
                TaskStack stack = dc.getTopStack();
                if (!(stack == null || (task = (Task) stack.getTopChild()) == null || task.getTopChild() != this)) {
                    dc.setFocusedApp(this);
                }
            }
            Letterbox letterbox = this.mLetterbox;
            if (letterbox != null) {
                letterbox.onMovedToDisplay(this.mDisplayContent.getDisplayId());
            }
        }
    }

    private void freezeBounds() {
        Task task = getTask();
        this.mFrozenBounds.offer(new Rect(task.mPreparedFrozenBounds));
        if (task.mPreparedFrozenMergedConfig.equals(Configuration.EMPTY)) {
            this.mFrozenMergedConfig.offer(new Configuration(task.getConfiguration()));
        } else {
            this.mFrozenMergedConfig.offer(new Configuration(task.mPreparedFrozenMergedConfig));
        }
        task.mPreparedFrozenMergedConfig.unset();
    }

    private void unfreezeBounds() {
        if (!this.mFrozenBounds.isEmpty()) {
            this.mFrozenBounds.remove();
            if (!this.mFrozenMergedConfig.isEmpty()) {
                this.mFrozenMergedConfig.remove();
            }
            for (int i = this.mChildren.size() - 1; i >= 0; i--) {
                ((WindowState) this.mChildren.get(i)).onUnfreezeBounds();
            }
            this.mWmService.mWindowPlacerLocked.performSurfacePlacement();
        }
    }

    /* access modifiers changed from: package-private */
    public void setAppLayoutChanges(int changes, String reason) {
        if (!this.mChildren.isEmpty()) {
            getDisplayContent().pendingLayoutChanges |= changes;
        }
    }

    /* access modifiers changed from: package-private */
    public void removeReplacedWindowIfNeeded(WindowState replacement) {
        int i = this.mChildren.size() - 1;
        while (i >= 0 && !((WindowState) this.mChildren.get(i)).removeReplacedWindowIfNeeded(replacement)) {
            i--;
        }
    }

    /* access modifiers changed from: package-private */
    public void startFreezingScreen() {
        if (!this.hiddenRequested) {
            if (!this.mFreezingScreen) {
                this.mFreezingScreen = true;
                this.mWmService.registerAppFreezeListener(this);
                this.mWmService.mAppsFreezingScreen++;
                if (this.mWmService.mAppsFreezingScreen == 1) {
                    this.mWmService.startFreezingDisplayLocked(0, 0, getDisplayContent());
                    this.mWmService.mH.removeMessages(17);
                    this.mWmService.mH.sendEmptyMessageDelayed(17, 2000);
                }
            }
            int count = this.mChildren.size();
            for (int i = 0; i < count; i++) {
                ((WindowState) this.mChildren.get(i)).onStartFreezingScreen();
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void stopFreezingScreen(boolean unfreezeSurfaceNow, boolean force) {
        if (this.mFreezingScreen) {
            int count = this.mChildren.size();
            boolean unfrozeWindows = false;
            for (int i = 0; i < count; i++) {
                unfrozeWindows |= ((WindowState) this.mChildren.get(i)).onStopFreezingScreen();
            }
            if (force || unfrozeWindows) {
                this.mFreezingScreen = false;
                this.mWmService.unregisterAppFreezeListener(this);
                WindowManagerService windowManagerService = this.mWmService;
                windowManagerService.mAppsFreezingScreen--;
                this.mWmService.mLastFinishedFreezeSource = this;
            }
            if (unfreezeSurfaceNow) {
                if (unfrozeWindows) {
                    this.mWmService.mWindowPlacerLocked.performSurfacePlacement();
                }
                this.mWmService.stopFreezingDisplayLocked();
            }
        }
    }

    public void onAppFreezeTimeout() {
        Slog.w("WindowManager", "Force clearing freeze: " + this);
        stopFreezingScreen(true, true);
    }

    /* access modifiers changed from: package-private */
    public void transferStartingWindowFromHiddenAboveTokenIfNeeded() {
        Task task = getTask();
        int i = task.mChildren.size() - 1;
        while (i >= 0) {
            AppWindowToken fromToken = (AppWindowToken) task.mChildren.get(i);
            if (fromToken != this) {
                if (!fromToken.hiddenRequested || !transferStartingWindow(fromToken.token)) {
                    i--;
                } else {
                    return;
                }
            } else {
                return;
            }
        }
    }

    /* access modifiers changed from: package-private */
    public boolean transferStartingWindow(IBinder transferFrom) {
        AppWindowToken fromToken = getDisplayContent().getAppWindowToken(transferFrom);
        if (fromToken == null) {
            return false;
        }
        WindowState tStartingWindow = fromToken.startingWindow;
        if (tStartingWindow == null || fromToken.startingSurface == null) {
            StartingData startingData = fromToken.mStartingData;
            if (startingData == null) {
                return false;
            }
            this.mStartingData = startingData;
            fromToken.mStartingData = null;
            fromToken.startingMoved = true;
            scheduleAddStartingWindow();
            return true;
        }
        WindowState mainWin = findMainWindow();
        if (this.mStartingData != null || this.startingWindow != null || (mainWin != null && mainWin.mWinAnimator != null && mainWin.mWinAnimator.mDrawState == 4)) {
            return false;
        }
        getDisplayContent().mSkipAppTransitionAnimation = true;
        long origId = Binder.clearCallingIdentity();
        try {
            this.mStartingWindowOpenTimeMillis = fromToken.mStartingWindowOpenTimeMillis;
            this.mStartingData = fromToken.mStartingData;
            this.startingSurface = fromToken.startingSurface;
            this.startingDisplayed = fromToken.startingDisplayed;
            fromToken.startingDisplayed = false;
            this.startingWindow = tStartingWindow;
            this.reportedVisible = fromToken.reportedVisible;
            fromToken.mStartingData = null;
            fromToken.startingSurface = null;
            fromToken.startingWindow = null;
            fromToken.startingMoved = true;
            tStartingWindow.mToken = this;
            tStartingWindow.mAppToken = this;
            fromToken.removeChild(tStartingWindow);
            fromToken.postWindowRemoveStartingWindowCleanup(tStartingWindow);
            fromToken.mHiddenSetFromTransferredStartingWindow = false;
            addWindow(tStartingWindow);
            if (fromToken.allDrawn) {
                this.allDrawn = true;
                this.deferClearAllDrawn = fromToken.deferClearAllDrawn;
            }
            if (fromToken.firstWindowDrawn) {
                this.firstWindowDrawn = true;
            }
            if (!fromToken.isHidden()) {
                setHidden(false);
                this.hiddenRequested = false;
                this.mHiddenSetFromTransferredStartingWindow = true;
            }
            setClientHidden(fromToken.mClientHidden);
            transferAnimation(fromToken);
            this.mUseTransferredAnimation = true;
            this.mWmService.updateFocusedWindowLocked(3, true);
            getDisplayContent().setLayoutNeeded();
            this.mWmService.mWindowPlacerLocked.performSurfacePlacement();
            return true;
        } finally {
            Binder.restoreCallingIdentity(origId);
        }
    }

    /* access modifiers changed from: package-private */
    public boolean isLastWindow(WindowState win) {
        return this.mChildren.size() == 1 && this.mChildren.get(0) == win;
    }

    /* access modifiers changed from: package-private */
    public void onAppTransitionDone() {
        this.sendingToBottom = false;
    }

    /* access modifiers changed from: package-private */
    public int getOrientation(int candidate) {
        if (candidate == 3 && isVisible() && !this.mIsDummyVisible) {
            return this.mOrientation;
        }
        if (this.mIsCastMode && this.mWmService.getCastRotationMode()) {
            if (this.mWmService.mCastRotation == 0 || this.mWmService.mCastRotation == 2) {
                return 1;
            }
            if (this.mWmService.mCastRotation == 1 || this.mWmService.mCastRotation == 3) {
                return 0;
            }
        }
        if (this.sendingToBottom || getDisplayContent().mClosingApps.contains(this)) {
            return -2;
        }
        if ((!isVisible() || this.mIsDummyVisible) && !getDisplayContent().mOpeningApps.contains(this)) {
            return -2;
        }
        return WindowManagerServiceInjector.getForceOrientation(this, this.mOrientation);
    }

    /* access modifiers changed from: package-private */
    public int getOrientationIgnoreVisibility() {
        return this.mOrientation;
    }

    /* access modifiers changed from: package-private */
    public boolean inSizeCompatMode() {
        return this.mSizeCompatBounds != null;
    }

    /* access modifiers changed from: package-private */
    public float getSizeCompatScale() {
        return inSizeCompatMode() ? this.mSizeCompatScale : super.getSizeCompatScale();
    }

    /* access modifiers changed from: package-private */
    public Rect getResolvedOverrideBounds() {
        return getResolvedOverrideConfiguration().windowConfiguration.getBounds();
    }

    public void onConfigurationChanged(Configuration newParentConfig) {
        Rect stackBounds;
        int prevWinMode = getWindowingMode();
        this.mTmpPrevBounds.set(getBounds());
        super.onConfigurationChanged(newParentConfig);
        Task task = getTask();
        Rect overrideBounds = getResolvedOverrideBounds();
        if (task != null && !overrideBounds.isEmpty() && (task.mTaskRecord == null || task.mTaskRecord.getConfiguration().orientation == newParentConfig.orientation)) {
            Rect taskBounds = task.getBounds();
            if (overrideBounds.width() != taskBounds.width() || overrideBounds.height() > taskBounds.height()) {
                calculateCompatBoundsTransformation(newParentConfig);
                updateSurfacePosition();
            } else if (this.mSizeCompatBounds != null) {
                this.mSizeCompatBounds = null;
                this.mSizeCompatScale = 1.0f;
                updateSurfacePosition();
            }
        }
        int winMode = getWindowingMode();
        if (prevWinMode != winMode) {
            if (prevWinMode != 0 && winMode == 2) {
                this.mDisplayContent.mPinnedStackControllerLocked.resetReentrySnapFraction(this);
            } else if (prevWinMode == 2 && winMode != 0 && !isHidden()) {
                TaskStack pinnedStack = this.mDisplayContent.getPinnedStack();
                if (pinnedStack != null) {
                    if (pinnedStack.lastAnimatingBoundsWasToFullscreen()) {
                        stackBounds = pinnedStack.mPreAnimationBounds;
                    } else {
                        stackBounds = this.mTmpRect;
                        pinnedStack.getBounds(stackBounds);
                    }
                    this.mDisplayContent.mPinnedStackControllerLocked.saveReentrySnapFraction(this, stackBounds);
                }
            } else if (shouldStartChangeTransition(prevWinMode, winMode)) {
                initializeChangeTransition(this.mTmpPrevBounds);
            }
        }
    }

    private boolean shouldStartChangeTransition(int prevWinMode, int newWinMode) {
        if (this.mWmService.mDisableTransitionAnimation || !isVisible() || getDisplayContent().mAppTransition.isTransitionSet() || getSurfaceControl() == null) {
            return false;
        }
        if (prevWinMode == 1 && newWinMode == 5 && MiuiMultiWindowUtils.mIsMiniFreeformMode) {
            return false;
        }
        if ((prevWinMode == 5) != (newWinMode == 5)) {
            return true;
        }
        return false;
    }

    private void initializeChangeTransition(Rect startBounds) {
        SurfaceControl.ScreenshotGraphicBuffer snapshot;
        this.mDisplayContent.prepareAppTransition(27, false, 0, false);
        this.mDisplayContent.mChangingApps.add(this);
        this.mTransitStartRect.set(startBounds);
        SurfaceControl.Builder parent = makeAnimationLeash().setParent(getAnimationLeashParent());
        this.mTransitChangeLeash = parent.setName(getSurfaceControl() + " - interim-change-leash").build();
        SurfaceControl.Transaction t = getPendingTransaction();
        t.setWindowCrop(this.mTransitChangeLeash, startBounds.width(), startBounds.height());
        t.setPosition(this.mTransitChangeLeash, (float) startBounds.left, (float) startBounds.top);
        t.show(this.mTransitChangeLeash);
        t.reparent(getSurfaceControl(), this.mTransitChangeLeash);
        onAnimationLeashCreated(t, this.mTransitChangeLeash);
        ArraySet<Integer> activityTypes = new ArraySet<>();
        activityTypes.add(Integer.valueOf(getActivityType()));
        RemoteAnimationAdapter adapter = this.mDisplayContent.mAppTransitionController.getRemoteAnimationOverride(this, 27, activityTypes);
        if (adapter == null || adapter.getChangeNeedsSnapshot()) {
            Task task = getTask();
            if (this.mThumbnail == null && task != null && !hasCommittedReparentToAnimationLeash() && (snapshot = this.mWmService.mTaskSnapshotController.createTaskSnapshot(task, 1.0f)) != null && !this.mWmService.mMiuiFreeFormGestureController.isScreenRotationDisabled()) {
                this.mThumbnail = new AppWindowThumbnail(t, this, snapshot.getGraphicBuffer(), true);
            }
        }
    }

    /* access modifiers changed from: package-private */
    public boolean isInChangeTransition() {
        return this.mTransitChangeLeash != null || AppTransition.isChangeTransit(this.mTransit);
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public AppWindowThumbnail getThumbnail() {
        return this.mThumbnail;
    }

    private void calculateCompatBoundsTransformation(Configuration newParentConfig) {
        Rect parentAppBounds = newParentConfig.windowConfiguration.getAppBounds();
        Rect parentBounds = newParentConfig.windowConfiguration.getBounds();
        Rect viewportBounds = parentAppBounds != null ? parentAppBounds : parentBounds;
        Rect appBounds = getWindowConfiguration().getAppBounds();
        Rect contentBounds = appBounds != null ? appBounds : getResolvedOverrideBounds();
        float contentW = (float) contentBounds.width();
        float contentH = (float) contentBounds.height();
        float viewportW = (float) viewportBounds.width();
        float viewportH = (float) viewportBounds.height();
        boolean isLand = newParentConfig.orientation == 2;
        this.mSizeCompatScale = (contentW > viewportW || contentH > viewportH) ? Math.min(viewportW / contentW, viewportH / contentH) : 1.0f;
        int offsetX = ((int) (((viewportW - (this.mSizeCompatScale * contentW)) + 1.0f) * 0.5f)) + viewportBounds.left;
        if (this.mSizeCompatBounds == null) {
            this.mSizeCompatBounds = new Rect();
        }
        this.mSizeCompatBounds.set(contentBounds);
        this.mSizeCompatBounds.offsetTo(0, 0);
        this.mSizeCompatBounds.scale(this.mSizeCompatScale);
        this.mSizeCompatBounds.top = parentBounds.top;
        this.mSizeCompatBounds.bottom += viewportBounds.top;
        if (isLand) {
            this.mSizeCompatBounds.bottom = viewportBounds.bottom;
        }
        this.mSizeCompatBounds.left += offsetX;
        this.mSizeCompatBounds.right += offsetX;
    }

    public Rect getBounds() {
        Rect rect = this.mSizeCompatBounds;
        if (rect != null) {
            return rect;
        }
        return super.getBounds();
    }

    public boolean matchParentBounds() {
        WindowContainer parent;
        if (!super.matchParentBounds() && (parent = getParent()) != null && !parent.getBounds().equals(getResolvedOverrideBounds())) {
            return false;
        }
        return true;
    }

    /* access modifiers changed from: package-private */
    public void checkAppWindowsReadyToShow() {
        boolean z = this.allDrawn;
        if (z != this.mLastAllDrawn) {
            this.mLastAllDrawn = z;
            if (z) {
                if (this.mFreezingScreen) {
                    showAllWindowsLocked();
                    stopFreezingScreen(false, true);
                    setAppLayoutChanges(4, "checkAppWindowsReadyToShow: freezingScreen");
                    return;
                }
                setAppLayoutChanges(8, "checkAppWindowsReadyToShow");
                if (!getDisplayContent().mOpeningApps.contains(this) && canShowWindows()) {
                    showAllWindowsLocked();
                }
            }
        }
    }

    private boolean allDrawnStatesConsidered() {
        for (int i = this.mChildren.size() - 1; i >= 0; i--) {
            WindowState child = (WindowState) this.mChildren.get(i);
            if (child.mightAffectAllDrawn() && !child.getDrawnStateEvaluated()) {
                return false;
            }
        }
        return true;
    }

    /* access modifiers changed from: package-private */
    public void updateAllDrawn() {
        int numInteresting;
        if (!this.allDrawn && (numInteresting = this.mNumInterestingWindows) > 0 && allDrawnStatesConsidered() && this.mNumDrawnWindows >= numInteresting && !isRelaunching()) {
            this.allDrawn = true;
            if (this.mDisplayContent != null) {
                this.mDisplayContent.setLayoutNeeded();
            }
            this.mWmService.mH.obtainMessage(32, this.token).sendToTarget();
            TaskStack pinnedStack = this.mDisplayContent.getPinnedStack();
            if (pinnedStack != null) {
                pinnedStack.onAllWindowsDrawn();
            }
        }
    }

    /* access modifiers changed from: package-private */
    public boolean keyDispatchingTimedOut(String reason, int windowPid) {
        ActivityRecord activityRecord = this.mActivityRecord;
        return activityRecord != null && activityRecord.keyDispatchingTimedOut(reason, windowPid);
    }

    /* access modifiers changed from: package-private */
    public boolean updateDrawnWindowStates(WindowState w) {
        w.setDrawnStateEvaluated(true);
        if (this.allDrawn && !this.mFreezingScreen) {
            return false;
        }
        if (this.mLastTransactionSequence != ((long) this.mWmService.mTransactionSequence)) {
            this.mLastTransactionSequence = (long) this.mWmService.mTransactionSequence;
            this.mNumDrawnWindows = 0;
            this.startingDisplayed = false;
            this.mNumInterestingWindows = findMainWindow(false) != null ? 1 : 0;
        }
        WindowStateAnimator windowStateAnimator = w.mWinAnimator;
        if (this.allDrawn || !w.mightAffectAllDrawn()) {
            return false;
        }
        if (w != this.startingWindow) {
            if (!w.isInteresting()) {
                return false;
            }
            if (findMainWindow(false) != w) {
                this.mNumInterestingWindows++;
            }
            if (!w.isDrawnLw()) {
                return false;
            }
            this.mNumDrawnWindows++;
            return true;
        } else if (!w.isDrawnLw()) {
            return false;
        } else {
            ActivityRecord activityRecord = this.mActivityRecord;
            if (activityRecord != null) {
                activityRecord.onStartingWindowDrawn(SystemClock.uptimeMillis());
            }
            this.startingDisplayed = true;
            return false;
        }
    }

    /* access modifiers changed from: package-private */
    public void layoutLetterbox(WindowState winHint) {
        WindowState w = findMainWindow();
        if (w == null) {
            return;
        }
        if (winHint == null || w == winHint) {
            boolean needsLetterbox = false;
            if ((w.isDrawnLw() || w.mWinAnimator.mSurfaceDestroyDeferred || w.isDragResizeChanged()) && w.isLetterboxedAppWindow() && fillsParent()) {
                needsLetterbox = true;
            }
            if (needsLetterbox) {
                if (this.mLetterbox == null) {
                    this.mLetterbox = new Letterbox(new Supplier() {
                        public final Object get() {
                            return AppWindowToken.this.lambda$layoutLetterbox$3$AppWindowToken();
                        }
                    });
                    this.mLetterbox.attachInput(w);
                }
                getPosition(this.mTmpPoint);
                this.mLetterbox.layout((inMultiWindowMode() || getStack() == null) ? getTask().getDisplayedBounds() : getStack().getDisplayedBounds(), w.getFrameLw(), this.mTmpPoint);
                return;
            }
            Letterbox letterbox = this.mLetterbox;
            if (letterbox != null) {
                letterbox.hide();
            }
        }
    }

    public /* synthetic */ SurfaceControl.Builder lambda$layoutLetterbox$3$AppWindowToken() {
        return makeChildSurface((WindowContainer) null);
    }

    /* access modifiers changed from: package-private */
    public void updateLetterboxSurface(WindowState winHint) {
        Letterbox letterbox;
        Letterbox letterbox2;
        WindowState w = findMainWindow();
        if (w == winHint || winHint == null || w == null) {
            layoutLetterbox(winHint);
            Letterbox letterbox3 = this.mLetterbox;
            if (letterbox3 != null && letterbox3.needsApplySurfaceChanges()) {
                this.mLetterbox.applySurfaceChanges(getPendingTransaction());
            }
            if (this.mIsCastMode && (letterbox2 = this.mLetterbox) != null) {
                letterbox2.hide();
                this.mLetterbox.applySurfaceChanges(getPendingTransaction());
            }
            if (this.mHandleByGesture && (letterbox = this.mLetterbox) != null) {
                letterbox.hide();
            }
        }
    }

    /* access modifiers changed from: package-private */
    public boolean forAllWindows(ToBooleanFunction<WindowState> callback, boolean traverseTopToBottom) {
        if (!this.mIsExiting || waitingForReplacement()) {
            return forAllWindowsUnchecked(callback, traverseTopToBottom);
        }
        return false;
    }

    /* access modifiers changed from: package-private */
    public void forAllAppWindows(Consumer<AppWindowToken> callback) {
        callback.accept(this);
    }

    /* access modifiers changed from: package-private */
    public boolean forAllWindowsUnchecked(ToBooleanFunction<WindowState> callback, boolean traverseTopToBottom) {
        return super.forAllWindows(callback, traverseTopToBottom);
    }

    /* access modifiers changed from: package-private */
    public AppWindowToken asAppWindowToken() {
        return this;
    }

    /* access modifiers changed from: package-private */
    /* JADX WARNING: Removed duplicated region for block: B:45:0x00cb A[RETURN] */
    /* JADX WARNING: Removed duplicated region for block: B:46:0x00cc  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean addStartingWindow(java.lang.String r22, int r23, android.content.res.CompatibilityInfo r24, java.lang.CharSequence r25, int r26, int r27, int r28, int r29, android.os.IBinder r30, boolean r31, boolean r32, boolean r33, boolean r34, boolean r35, boolean r36) {
        /*
            r21 = this;
            r8 = r21
            r15 = r22
            r14 = r23
            boolean r0 = r21.okToDisplay()
            r9 = 0
            if (r0 != 0) goto L_0x000e
            return r9
        L_0x000e:
            com.android.server.wm.StartingData r0 = r8.mStartingData
            if (r0 == 0) goto L_0x0013
            return r9
        L_0x0013:
            com.android.server.wm.WindowState r13 = r21.findMainWindow()
            if (r13 == 0) goto L_0x0022
            com.android.server.wm.WindowStateAnimator r0 = r13.mWinAnimator
            boolean r0 = r0.getShown()
            if (r0 == 0) goto L_0x0022
            return r9
        L_0x0022:
            com.android.server.wm.WindowManagerService r0 = r8.mWmService
            com.android.server.wm.TaskSnapshotController r0 = r0.mTaskSnapshotController
            com.android.server.wm.Task r1 = r21.getTask()
            int r1 = r1.mTaskId
            com.android.server.wm.Task r2 = r21.getTask()
            int r2 = r2.mUserId
            android.app.ActivityManager$TaskSnapshot r12 = r0.getSnapshot(r1, r2, r9, r9)
            boolean r0 = com.android.server.wm.AppTransitionInjector.disableSnapshot(r22)
            if (r0 == 0) goto L_0x003e
            r0 = 0
            goto L_0x0040
        L_0x003e:
            r0 = r34
        L_0x0040:
            com.android.server.wm.ActivityRecord r1 = r8.mActivityRecord
            if (r1 == 0) goto L_0x0056
            int r1 = r1.getUid()
            boolean r1 = com.android.server.wm.AppTransitionInjector.disableSnapshotForApplock(r15, r1)
            if (r1 == 0) goto L_0x0051
            r1 = r9
            goto L_0x0052
        L_0x0051:
            r1 = r0
        L_0x0052:
            r0 = r1
            r20 = r0
            goto L_0x0058
        L_0x0056:
            r20 = r0
        L_0x0058:
            r0 = r21
            r1 = r31
            r2 = r32
            r3 = r33
            r4 = r20
            r5 = r35
            r6 = r36
            r7 = r12
            int r0 = r0.getStartingWindowType(r1, r2, r3, r4, r5, r6, r7)
            r1 = 1
            if (r0 != r1) goto L_0x0073
            boolean r1 = r8.createSnapshot(r12)
            return r1
        L_0x0073:
            if (r14 == 0) goto L_0x00c1
            com.android.server.AttributeCache r2 = com.android.server.AttributeCache.instance()
            int[] r3 = com.android.internal.R.styleable.Window
            com.android.server.wm.WindowManagerService r4 = r8.mWmService
            int r4 = r4.mCurrentUserId
            com.android.server.AttributeCache$Entry r2 = r2.get(r15, r14, r3, r4)
            if (r2 != 0) goto L_0x0086
            return r9
        L_0x0086:
            android.content.res.TypedArray r3 = r2.array
            r4 = 5
            boolean r3 = r3.getBoolean(r4, r9)
            android.content.res.TypedArray r4 = r2.array
            r5 = 4
            boolean r4 = r4.getBoolean(r5, r9)
            android.content.res.TypedArray r5 = r2.array
            r6 = 14
            boolean r5 = r5.getBoolean(r6, r9)
            android.content.res.TypedArray r6 = r2.array
            r7 = 12
            boolean r6 = r6.getBoolean(r7, r9)
            if (r3 == 0) goto L_0x00a7
            return r9
        L_0x00a7:
            if (r4 != 0) goto L_0x00c0
            if (r6 == 0) goto L_0x00ac
            goto L_0x00c0
        L_0x00ac:
            if (r5 == 0) goto L_0x00c1
            com.android.server.wm.DisplayContent r7 = r21.getDisplayContent()
            com.android.server.wm.WallpaperController r7 = r7.mWallpaperController
            com.android.server.wm.WindowState r7 = r7.getWallpaperTarget()
            if (r7 != 0) goto L_0x00bf
            r7 = 1048576(0x100000, float:1.469368E-39)
            r7 = r29 | r7
            goto L_0x00c3
        L_0x00bf:
            return r9
        L_0x00c0:
            return r9
        L_0x00c1:
            r7 = r29
        L_0x00c3:
            r2 = r30
            boolean r3 = r8.transferStartingWindow(r2)
            if (r3 == 0) goto L_0x00cc
            return r1
        L_0x00cc:
            r3 = 2
            if (r0 == r3) goto L_0x00d0
            return r9
        L_0x00d0:
            com.android.server.wm.SplashScreenStartingData r3 = new com.android.server.wm.SplashScreenStartingData
            com.android.server.wm.WindowManagerService r10 = r8.mWmService
            android.content.res.Configuration r19 = r21.getMergedOverrideConfiguration()
            r9 = r3
            r11 = r22
            r4 = r12
            r12 = r23
            r5 = r13
            r13 = r24
            r14 = r25
            r15 = r26
            r16 = r27
            r17 = r28
            r18 = r7
            r9.<init>(r10, r11, r12, r13, r14, r15, r16, r17, r18, r19)
            r8.mStartingData = r3
            r21.scheduleAddStartingWindow()
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.wm.AppWindowToken.addStartingWindow(java.lang.String, int, android.content.res.CompatibilityInfo, java.lang.CharSequence, int, int, int, int, android.os.IBinder, boolean, boolean, boolean, boolean, boolean, boolean):boolean");
    }

    private boolean createSnapshot(ActivityManager.TaskSnapshot snapshot) {
        if (snapshot == null) {
            return false;
        }
        this.mStartingData = new SnapshotStartingData(this.mWmService, snapshot);
        scheduleAddStartingWindow();
        return true;
    }

    /* access modifiers changed from: package-private */
    public void scheduleAddStartingWindow() {
        if (!this.mWmService.mAnimationHandler.hasCallbacks(this.mAddStartingWindow)) {
            this.mWmService.mAnimationHandler.postAtFrontOfQueue(this.mAddStartingWindow);
        }
    }

    private int getStartingWindowType(boolean newTask, boolean taskSwitch, boolean processRunning, boolean allowTaskSnapshot, boolean activityCreated, boolean fromRecents, ActivityManager.TaskSnapshot snapshot) {
        if (getDisplayContent().mAppTransition.getAppTransition() == 19) {
            return 0;
        }
        if (newTask || !processRunning || (taskSwitch && !activityCreated)) {
            return 2;
        }
        if (!taskSwitch || !allowTaskSnapshot) {
            return 0;
        }
        if (this.mWmService.mLowRamTaskSnapshotsAndRecents) {
            return 2;
        }
        if (snapshot == null) {
            return 0;
        }
        return (snapshotOrientationSameAsTask(snapshot) || fromRecents) ? 1 : 2;
    }

    private boolean snapshotOrientationSameAsTask(ActivityManager.TaskSnapshot snapshot) {
        if (snapshot != null && getTask().getConfiguration().orientation == snapshot.getOrientation()) {
            return true;
        }
        return false;
    }

    /* access modifiers changed from: package-private */
    public void removeStartingWindow() {
        if (this.startingWindow == null) {
            if (this.mStartingData != null) {
                this.mStartingData = null;
            }
        } else if (this.mStartingData != null) {
            WindowManagerPolicy.StartingSurface surface = this.startingSurface;
            this.mStartingData = null;
            this.startingSurface = null;
            this.startingWindow = null;
            this.startingDisplayed = false;
            if (surface != null) {
                if (this.mStartingWindowOpenTimeMillis != 0) {
                    EventLog.writeEvent(EventLogTags.AM_STARTING_WINDOW, new Object[]{getName(), Long.valueOf(SystemClock.uptimeMillis() - this.mStartingWindowOpenTimeMillis)});
                }
                this.mWmService.mAnimationHandler.post(new Runnable() {
                    public final void run() {
                        AppWindowToken.lambda$removeStartingWindow$4(WindowManagerPolicy.StartingSurface.this);
                    }
                });
            }
        }
    }

    static /* synthetic */ void lambda$removeStartingWindow$4(WindowManagerPolicy.StartingSurface surface) {
        try {
            surface.remove();
        } catch (Exception e) {
            Slog.w("WindowManager", "Exception when removing starting window", e);
        }
    }

    /* access modifiers changed from: package-private */
    public boolean fillsParent() {
        return this.mFillsParent;
    }

    /* access modifiers changed from: package-private */
    public void setFillsParent(boolean fillsParent) {
        this.mFillsParent = fillsParent;
    }

    /* access modifiers changed from: package-private */
    public boolean containsDismissKeyguardWindow() {
        if (isRelaunching()) {
            return this.mLastContainsDismissKeyguardWindow;
        }
        for (int i = this.mChildren.size() - 1; i >= 0; i--) {
            if ((((WindowState) this.mChildren.get(i)).mAttrs.flags & DumpState.DUMP_CHANGES) != 0) {
                return true;
            }
        }
        return false;
    }

    /* access modifiers changed from: package-private */
    public boolean containsShowWhenLockedWindow() {
        if (isRelaunching()) {
            return this.mLastContainsShowWhenLockedWindow;
        }
        for (int i = this.mChildren.size() - 1; i >= 0; i--) {
            if ((((WindowState) this.mChildren.get(i)).mAttrs.flags & DumpState.DUMP_FROZEN) != 0) {
                return true;
            }
        }
        return false;
    }

    /* access modifiers changed from: package-private */
    public void checkKeyguardFlagsChanged() {
        boolean containsDismissKeyguard = containsDismissKeyguardWindow();
        boolean containsShowWhenLocked = containsShowWhenLockedWindow();
        if (!(containsDismissKeyguard == this.mLastContainsDismissKeyguardWindow && containsShowWhenLocked == this.mLastContainsShowWhenLockedWindow)) {
            this.mWmService.notifyKeyguardFlagsChanged((Runnable) null, getDisplayContent().getDisplayId());
        }
        this.mLastContainsDismissKeyguardWindow = containsDismissKeyguard;
        this.mLastContainsShowWhenLockedWindow = containsShowWhenLocked;
    }

    /* access modifiers changed from: package-private */
    public WindowState getImeTargetBelowWindow(WindowState w) {
        int index = this.mChildren.indexOf(w);
        if (index <= 0) {
            return null;
        }
        WindowState target = (WindowState) this.mChildren.get(index - 1);
        if (target.canBeImeTarget()) {
            return target;
        }
        return null;
    }

    /* access modifiers changed from: package-private */
    public WindowState getHighestAnimLayerWindow(WindowState currentTarget) {
        WindowState candidate = null;
        for (int i = this.mChildren.indexOf(currentTarget); i >= 0; i--) {
            WindowState w = (WindowState) this.mChildren.get(i);
            if (!w.mRemoved && candidate == null) {
                candidate = w;
            }
        }
        return candidate;
    }

    /* access modifiers changed from: package-private */
    public void setDisablePreviewScreenshots(boolean disable) {
        this.mDisablePreviewScreenshots = disable;
    }

    /* access modifiers changed from: package-private */
    public void setCanTurnScreenOn(boolean canTurnScreenOn) {
        this.mCanTurnScreenOn = canTurnScreenOn;
    }

    /* access modifiers changed from: package-private */
    public boolean canTurnScreenOn() {
        return this.mCanTurnScreenOn;
    }

    static /* synthetic */ boolean lambda$shouldUseAppThemeSnapshot$5(WindowState w) {
        return (w.mAttrs.flags & 8192) != 0;
    }

    /* access modifiers changed from: package-private */
    public boolean shouldUseAppThemeSnapshot() {
        return this.mDisablePreviewScreenshots || forAllWindows($$Lambda$AppWindowToken$Zf9XP8X2PGWYnn5VrENXlB2pEI.INSTANCE, true);
    }

    /* access modifiers changed from: package-private */
    public SurfaceControl getAppAnimationLayer() {
        int i;
        if (isActivityTypeHome()) {
            i = 2;
        } else if (needsZBoost()) {
            i = 1;
        } else {
            i = 0;
        }
        return getAppAnimationLayer(i);
    }

    public SurfaceControl getAnimationLeashParent() {
        if (!inPinnedWindowingMode()) {
            return getAppAnimationLayer();
        }
        return getStack().getSurfaceControl();
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public boolean shouldAnimate(int transit) {
        boolean isSplitScreenPrimary = getWindowingMode() == 3;
        boolean allowSplitScreenPrimaryAnimation = transit != 13;
        RecentsAnimationController controller = this.mWmService.getRecentsAnimationController();
        if (controller != null && controller.isAnimatingTask(getTask()) && controller.shouldCancelWithDeferredScreenshot()) {
            return false;
        }
        if (!isSplitScreenPrimary || allowSplitScreenPrimaryAnimation) {
            return true;
        }
        return false;
    }

    /* access modifiers changed from: package-private */
    public SurfaceControl createAnimationBoundsLayer(SurfaceControl.Transaction t) {
        SurfaceControl.Builder parent = makeAnimationLeash().setParent(getAnimationLeashParent());
        SurfaceControl boundsLayer = parent.setName(getSurfaceControl() + " - animation-bounds").build();
        t.show(boundsLayer);
        return boundsLayer;
    }

    /* access modifiers changed from: package-private */
    public Rect getDisplayedBounds() {
        Task task = getTask();
        if (task != null) {
            Rect overrideDisplayedBounds = task.getOverrideDisplayedBounds();
            if (!overrideDisplayedBounds.isEmpty()) {
                return overrideDisplayedBounds;
            }
        }
        return getBounds();
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public Rect getAnimationBounds(int appStackClipMode) {
        if (appStackClipMode != 1 || getStack() == null) {
            return getTask() != null ? getTask().getBounds() : getBounds();
        }
        return getStack().getBounds();
    }

    /* access modifiers changed from: package-private */
    public boolean applyAnimationLocked(WindowManager.LayoutParams lp, int transit, boolean enter, boolean isVoiceInteraction) {
        AnimationAdapter adapter;
        float windowCornerRadius;
        int i = transit;
        boolean z = false;
        if (this.mWmService.mDisableTransitionAnimation || !shouldAnimate(i)) {
            cancelAnimation();
            return false;
        }
        Trace.traceBegin(32, "AWT#applyAnimationLocked");
        if (okToAnimate()) {
            AnimationAdapter thumbnailAdapter = null;
            int appStackClipMode = getDisplayContent().mAppTransition.getAppStackClipMode();
            if (inFreeformWindowingMode()) {
                this.mTmpRect.set(getAnimationBounds(0));
            } else {
                this.mTmpRect.set(getAnimationBounds(appStackClipMode));
            }
            this.mTmpPoint.set(this.mTmpRect.left, this.mTmpRect.top);
            this.mTmpRect.offsetTo(0, 0);
            boolean isChanging = AppTransition.isChangeTransit(transit) && enter && getDisplayContent().mChangingApps.contains(this);
            if (getDisplayContent().mAppTransition.getRemoteAnimationController() != null && !this.mSurfaceAnimator.isAnimationStartDelayed() && !inFreeformWindowingMode()) {
                RemoteAnimationController.RemoteAnimationRecord adapters = getDisplayContent().mAppTransition.getRemoteAnimationController().createRemoteAnimationRecord(this, this.mTmpPoint, this.mTmpRect, isChanging ? this.mTransitStartRect : null);
                adapter = adapters.mAdapter;
                thumbnailAdapter = adapters.mThumbnailAdapter;
            } else if (isChanging) {
                float durationScale = this.mWmService.getTransitionAnimationScaleLocked();
                this.mTmpRect.offsetTo(this.mTmpPoint.x, this.mTmpPoint.y);
                WindowChangeAnimationSpec windowChangeAnimationSpec = r7;
                WindowChangeAnimationSpec windowChangeAnimationSpec2 = new WindowChangeAnimationSpec(this.mTransitStartRect, this.mTmpRect, getDisplayContent().getDisplayInfo(), durationScale, true, false);
                AnimationAdapter adapter2 = new LocalAnimationAdapter(windowChangeAnimationSpec, this.mWmService.mSurfaceAnimationRunner);
                if (this.mThumbnail != null) {
                    thumbnailAdapter = new LocalAnimationAdapter(new WindowChangeAnimationSpec(this.mTransitStartRect, this.mTmpRect, getDisplayContent().getDisplayInfo(), durationScale, true, true), this.mWmService.mSurfaceAnimationRunner);
                }
                this.mTransit = i;
                this.mTransitFlags = getDisplayContent().mAppTransition.getTransitFlags();
                adapter = adapter2;
            } else {
                this.mNeedsAnimationBoundsLayer = appStackClipMode == 0;
                Animation a = loadAnimation(lp, transit, enter, isVoiceInteraction);
                if (a != null) {
                    if (!inMultiWindowMode()) {
                        windowCornerRadius = getDisplayContent().getWindowCornerRadius();
                    } else {
                        windowCornerRadius = 0.0f;
                    }
                    this.mWindowAnimationSpec = new WindowAnimationSpec(a, this.mTmpPoint, this.mTmpRect, getDisplayContent().mAppTransition.canSkipFirstFrame(), appStackClipMode, true, windowCornerRadius);
                    if (AppTransitionInjector.isNextAppTransitionWallpaperClose(transit) && !enter) {
                        attachAppOpenWithDimmerAnimation();
                    }
                    this.mWindowAnimationSpec.mWindowToken = this;
                    int heightDp = this.mWmService.getDefaultDisplayContentLocked().mBaseDisplayHeight;
                    WindowState mainWindow = findMainWindow(true);
                    if ((this.mWmService.mMiuiGestureController.isGestureOpen() || (mainWindow != null && mainWindow.mWindowFrames.mFrame.bottom >= heightDp)) && getDisplayContent().mAppTransition.shouldAppTransitionRoundCorner(i)) {
                        this.mShouldAppTransitionRoundCorner = true;
                    } else {
                        this.mShouldAppTransitionRoundCorner = false;
                    }
                    this.mShouldActivityTransitionRoundCorner = getDisplayContent().mAppTransition.shouldActivityTransitionRoundCorner();
                    AnimationAdapter adapter3 = new LocalAnimationAdapter(this.mWindowAnimationSpec, this.mWmService.mSurfaceAnimationRunner);
                    if (a.getZAdjustment() == 1) {
                        z = true;
                    }
                    this.mNeedsZBoost = z;
                    this.mTransit = i;
                    this.mTransitFlags = getDisplayContent().mAppTransition.getTransitFlags();
                    adapter = adapter3;
                } else {
                    adapter = null;
                }
            }
            if (adapter != null) {
                startAnimation(getPendingTransaction(), adapter, !isVisible());
                if (adapter.getShowWallpaper()) {
                    this.mDisplayContent.pendingLayoutChanges |= 4;
                }
                if (thumbnailAdapter != null) {
                    this.mThumbnail.startAnimation(getPendingTransaction(), thumbnailAdapter, !isVisible());
                }
            }
        } else {
            cancelAnimation();
        }
        Trace.traceEnd(32);
        return isReallyAnimating();
    }

    private Animation loadAnimation(WindowManager.LayoutParams lp, int transit, boolean enter, boolean isVoiceInteraction) {
        Rect surfaceInsets;
        boolean enter2;
        DisplayContent displayContent = getTask().getDisplayContent();
        DisplayInfo displayInfo = displayContent.getDisplayInfo();
        int width = displayInfo.appWidth;
        int height = displayInfo.appHeight;
        WindowState win = findMainWindow();
        boolean freeform = false;
        Rect frame = new Rect(0, 0, width, height);
        Rect displayFrame = new Rect(0, 0, displayInfo.logicalWidth, displayInfo.logicalHeight);
        Rect insets = new Rect();
        Rect stableInsets = new Rect();
        if (win != null && win.inFreeformWindowingMode()) {
            freeform = true;
        }
        if (win != null) {
            if (freeform) {
                frame.set(win.getFrameLw());
            } else if (win.isLetterboxedAppWindow()) {
                frame.set(getTask().getBounds());
            } else if (win.isDockedResizing()) {
                frame.set(getTask().getParent().getBounds());
            } else {
                frame.set(win.getContainingFrame());
            }
            Rect surfaceInsets2 = win.getAttrs().surfaceInsets;
            win.getContentInsets(insets);
            win.getStableInsets(stableInsets);
            surfaceInsets = surfaceInsets2;
        } else {
            surfaceInsets = null;
        }
        if (this.mLaunchTaskBehind) {
            enter2 = false;
        } else {
            enter2 = enter;
        }
        Configuration displayConfig = displayContent.getConfiguration();
        AppTransition appTransition = getDisplayContent().mAppTransition;
        int i = displayConfig.uiMode;
        int i2 = i;
        Configuration configuration = displayConfig;
        Rect insets2 = insets;
        Animation a = appTransition.loadAnimation(lp, transit, enter2, i2, displayConfig.orientation, frame, displayFrame, insets2, surfaceInsets, stableInsets, isVoiceInteraction, freeform, getTask().mTaskId);
        if (a != null) {
            a.initialize(frame.width(), frame.height(), width, height);
            a.scaleCurrentDuration(this.mWmService.getTransitionAnimationScaleLocked());
        }
        return a;
    }

    public boolean shouldDeferAnimationFinish(Runnable endDeferFinishCallback) {
        AnimatingAppWindowTokenRegistry animatingAppWindowTokenRegistry = this.mAnimatingAppWindowTokenRegistry;
        return animatingAppWindowTokenRegistry != null && animatingAppWindowTokenRegistry.notifyAboutToFinish(this, endDeferFinishCallback);
    }

    public void onAnimationLeashLost(SurfaceControl.Transaction t) {
        super.onAnimationLeashLost(t);
        SurfaceControl surfaceControl = this.mAnimationBoundsLayer;
        if (surfaceControl != null) {
            t.remove(surfaceControl);
            this.mAnimationBoundsLayer = null;
        }
        AnimatingAppWindowTokenRegistry animatingAppWindowTokenRegistry = this.mAnimatingAppWindowTokenRegistry;
        if (animatingAppWindowTokenRegistry != null) {
            animatingAppWindowTokenRegistry.notifyFinished(this);
        }
    }

    /* access modifiers changed from: protected */
    public void setLayer(SurfaceControl.Transaction t, int layer) {
        if (!this.mSurfaceAnimator.hasLeash()) {
            t.setLayer(this.mSurfaceControl, layer);
        }
    }

    /* access modifiers changed from: protected */
    public void setRelativeLayer(SurfaceControl.Transaction t, SurfaceControl relativeTo, int layer) {
        if (!this.mSurfaceAnimator.hasLeash()) {
            t.setRelativeLayer(this.mSurfaceControl, relativeTo, layer);
        }
    }

    /* access modifiers changed from: protected */
    public void reparentSurfaceControl(SurfaceControl.Transaction t, SurfaceControl newParent) {
        if (!this.mSurfaceAnimator.hasLeash()) {
            t.reparent(this.mSurfaceControl, newParent);
        }
    }

    public void onAnimationLeashCreated(SurfaceControl.Transaction t, SurfaceControl leash) {
        int layer;
        if (!inPinnedWindowingMode()) {
            layer = getPrefixOrderIndex();
        } else {
            layer = getParent().getPrefixOrderIndex();
        }
        if (this.mNeedsZBoost) {
            layer += Z_BOOST_BASE;
        }
        if (!this.mNeedsAnimationBoundsLayer) {
            leash.setLayer(layer);
        }
        getDisplayContent().assignStackOrdering();
        SurfaceControl surfaceControl = this.mTransitChangeLeash;
        if (leash != surfaceControl) {
            if (surfaceControl != null) {
                clearChangeLeash(t, false);
            }
            AnimatingAppWindowTokenRegistry animatingAppWindowTokenRegistry = this.mAnimatingAppWindowTokenRegistry;
            if (animatingAppWindowTokenRegistry != null) {
                animatingAppWindowTokenRegistry.notifyStarting(this);
            }
            if (this.mNeedsAnimationBoundsLayer) {
                this.mTmpRect.setEmpty();
                Task task = getTask();
                if (getDisplayContent().mAppTransitionController.isTransitWithinTask(getTransit(), task)) {
                    task.getBounds(this.mTmpRect);
                } else {
                    TaskStack stack = getStack();
                    if (stack != null) {
                        stack.getBounds(this.mTmpRect);
                    } else {
                        return;
                    }
                }
                this.mAnimationBoundsLayer = createAnimationBoundsLayer(t);
                t.setWindowCrop(this.mAnimationBoundsLayer, this.mTmpRect);
                t.setLayer(this.mAnimationBoundsLayer, layer);
                t.reparent(leash, this.mAnimationBoundsLayer);
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void showAllWindowsLocked() {
        forAllWindows((Consumer<WindowState>) $$Lambda$AppWindowToken$GO_44j7HKFWrNpwWGQ4totlKXW8.INSTANCE, false);
    }

    /* access modifiers changed from: protected */
    public void onAnimationFinished() {
        super.onAnimationFinished();
        Trace.traceBegin(32, "AWT#onAnimationFinished");
        this.mTransit = -1;
        this.mTransitFlags = 0;
        this.mNeedsZBoost = false;
        this.mNeedsAnimationBoundsLayer = false;
        setAppLayoutChanges(12, "AppWindowToken");
        clearMiuiThumbnailAnimation();
        clearThumbnail();
        clearTransitionDimmer();
        setClientHidden(isHidden() && this.hiddenRequested);
        getDisplayContent().computeImeTargetIfNeeded(this);
        AppWindowThumbnail appWindowThumbnail = this.mThumbnail;
        if (appWindowThumbnail != null) {
            appWindowThumbnail.destroy();
            this.mThumbnail = null;
        }
        ArrayList<WindowState> children = new ArrayList<>(this.mChildren);
        if (this.mIsDummyAnimating && !this.mWmService.mMiuiGestureController.isGestureRunning()) {
            this.mWmService.mMiuiGestureController.setKeepWallpaperShowing(false);
        }
        this.mIsDummyAnimating = false;
        if (this.mIsDummyVisible) {
            children.forEach($$Lambda$AppWindowToken$Llgbg9OfEfErb2x8SXCq3kwFAzY.INSTANCE);
        }
        children.forEach($$Lambda$01bPtngJg5AqEoOWfW3rWfV7MH4.INSTANCE);
        getDisplayContent().mAppTransition.notifyAppTransitionFinishedLocked(this.token);
        scheduleAnimation();
        this.mActivityRecord.onAnimationFinished();
        Trace.traceEnd(32);
    }

    static /* synthetic */ void lambda$onAnimationFinished$7(WindowState win) {
        if (win.mWinAnimator != null) {
            win.mWinAnimator.hide("hide by gesture");
        }
    }

    /* access modifiers changed from: package-private */
    public boolean isAppAnimating() {
        return isSelfAnimating();
    }

    /* access modifiers changed from: package-private */
    public boolean isSelfAnimating() {
        return isWaitingForTransitionStart() || isReallyAnimating();
    }

    private boolean isReallyAnimating() {
        return super.isSelfAnimating();
    }

    private void clearChangeLeash(SurfaceControl.Transaction t, boolean cancel) {
        if (this.mTransitChangeLeash != null) {
            if (cancel) {
                clearThumbnail();
                SurfaceControl sc = getSurfaceControl();
                if (!(getParentSurfaceControl() == null || sc == null)) {
                    t.reparent(sc, getParentSurfaceControl());
                }
            }
            t.hide(this.mTransitChangeLeash);
            t.remove(this.mTransitChangeLeash);
            this.mTransitChangeLeash = null;
            if (cancel) {
                onAnimationLeashLost(t);
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void cancelAnimation() {
        cancelAnimationOnly();
        clearThumbnail();
        clearChangeLeash(getPendingTransaction(), true);
    }

    /* access modifiers changed from: package-private */
    public void cancelAnimationOnly() {
        super.cancelAnimation();
    }

    /* access modifiers changed from: package-private */
    public boolean isWaitingForTransitionStart() {
        return getDisplayContent().mAppTransition.isTransitionSet() && (getDisplayContent().mOpeningApps.contains(this) || getDisplayContent().mClosingApps.contains(this) || getDisplayContent().mChangingApps.contains(this));
    }

    public int getTransit() {
        return this.mTransit;
    }

    /* access modifiers changed from: package-private */
    public int getTransitFlags() {
        return this.mTransitFlags;
    }

    /* access modifiers changed from: package-private */
    public void attachThumbnailAnimation(boolean isMiuiThumbnail) {
        this.mIsMiuiThumbnail = isMiuiThumbnail;
        attachThumbnailAnimation();
    }

    /* access modifiers changed from: package-private */
    public void attachThumbnailAnimation() {
        GraphicBuffer thumbnailHeader;
        Rect appRect;
        Rect appRect2;
        Rect appRect3;
        if (isReallyAnimating() && (thumbnailHeader = getDisplayContent().mAppTransition.getAppTransitionThumbnailHeader(getTask().mTaskId)) != null) {
            clearThumbnail();
            if (this.mIsMiuiThumbnail) {
                DisplayInfo displayInfo = this.mWmService.getDefaultDisplayContentLocked().getDisplayInfo();
                WindowState win = findMainWindow();
                if (win != null) {
                    appRect3 = win.mWindowFrames.mContainingFrame;
                } else {
                    appRect3 = new Rect(0, 0, displayInfo.appWidth, displayInfo.appHeight);
                }
                this.mThumbnail = new AppWindowThumbnail(getPendingTransaction(), this, thumbnailHeader, false, new AppWindowAnimatorHelper(appRect3, this.mWmService.mMiuiAppTransitionAnimationHelper));
            } else if (this.mIsMiuiActivityThumbnail) {
                Rect appRect4 = null;
                Letterbox letterbox = this.mLetterbox;
                if (letterbox != null) {
                    appRect4 = letterbox.getOuter();
                }
                if (appRect4 == null || appRect4.width() <= 0 || appRect4.height() <= 0) {
                    DisplayInfo displayInfo2 = this.mWmService.getDefaultDisplayContentLocked().getDisplayInfo();
                    WindowState win2 = findMainWindow();
                    if (win2 != null) {
                        appRect2 = win2.mWindowFrames.mContainingFrame;
                    } else {
                        appRect2 = new Rect(0, 0, displayInfo2.appWidth, displayInfo2.appHeight);
                    }
                    appRect = appRect2;
                } else {
                    appRect = appRect4;
                }
                this.mThumbnail = new AppWindowThumbnail(getPendingTransaction(), this, thumbnailHeader, false, new AppWindowAnimatorHelper(appRect, this.mWmService.mMiuiAppTransitionAnimationHelper), getDisplayContent().mAppTransition.getForeGroundColor());
            } else {
                this.mThumbnail = new AppWindowThumbnail(getPendingTransaction(), this, thumbnailHeader);
            }
            this.mThumbnail.startAnimation(getPendingTransaction(), loadThumbnailAnimation(thumbnailHeader));
        }
    }

    /* access modifiers changed from: package-private */
    public void attachAppOpenWithDimmerAnimation() {
        if (this.mAnimationDimmer == null) {
            this.mAnimationDimmer = new AnimationDimmer(getDisplayContent());
        }
        this.mAnimationDimmer.dimAbove(getPendingTransaction(), this, 0.0f);
        WindowAnimationSpec windowAnimationSpec = this.mWindowAnimationSpec;
        if (windowAnimationSpec != null) {
            windowAnimationSpec.mAnimationDimmer = this.mAnimationDimmer;
        }
    }

    /* access modifiers changed from: package-private */
    public void attachCrossProfileAppsThumbnailAnimation() {
        int thumbnailDrawableRes;
        if (isReallyAnimating()) {
            clearThumbnail();
            WindowState win = findMainWindow();
            if (win != null) {
                Rect frame = win.getFrameLw();
                if (getTask().mUserId == this.mWmService.mCurrentUserId) {
                    thumbnailDrawableRes = 17302285;
                } else {
                    thumbnailDrawableRes = 17302370;
                }
                GraphicBuffer thumbnail = getDisplayContent().mAppTransition.createCrossProfileAppsThumbnail(thumbnailDrawableRes, frame);
                if (thumbnail != null) {
                    this.mThumbnail = new AppWindowThumbnail(getPendingTransaction(), this, thumbnail);
                    this.mThumbnail.startAnimation(getPendingTransaction(), getDisplayContent().mAppTransition.createCrossProfileAppsThumbnailAnimationLocked(win.getFrameLw()), new Point(frame.left, frame.top));
                }
            }
        }
    }

    private Animation loadThumbnailAnimation(GraphicBuffer thumbnailHeader) {
        Rect appRect;
        DisplayInfo displayInfo = this.mDisplayContent.getDisplayInfo();
        WindowState win = findMainWindow();
        if (win != null) {
            appRect = win.getContentFrameLw();
        } else {
            appRect = new Rect(0, 0, displayInfo.appWidth, displayInfo.appHeight);
        }
        Rect insets = win != null ? win.getContentInsets() : null;
        Configuration displayConfig = this.mDisplayContent.getConfiguration();
        return getDisplayContent().mAppTransition.createThumbnailAspectScaleAnimationLocked(appRect, insets, thumbnailHeader, getTask().mTaskId, displayConfig.uiMode, displayConfig.orientation);
    }

    private void clearThumbnail() {
        AppWindowThumbnail appWindowThumbnail = this.mThumbnail;
        if (appWindowThumbnail != null) {
            appWindowThumbnail.destroy();
            this.mThumbnail = null;
        }
    }

    private void clearTransitionDimmer() {
        AnimationDimmer animationDimmer = this.mAnimationDimmer;
        if (animationDimmer != null && animationDimmer.isVisible) {
            this.mAnimationDimmer.stopDim(getPendingTransaction());
            this.mAnimationDimmer = null;
            WindowAnimationSpec windowAnimationSpec = this.mWindowAnimationSpec;
            if (windowAnimationSpec != null) {
                windowAnimationSpec.mAnimationDimmer = null;
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void registerRemoteAnimations(RemoteAnimationDefinition definition) {
        this.mRemoteAnimationDefinition = definition;
    }

    /* access modifiers changed from: package-private */
    public RemoteAnimationDefinition getRemoteAnimationDefinition() {
        return this.mRemoteAnimationDefinition;
    }

    /* access modifiers changed from: package-private */
    public void dump(PrintWriter pw, String prefix, boolean dumpAll) {
        String str;
        super.dump(pw, prefix, dumpAll);
        if (this.appToken != null) {
            pw.println(prefix + "app=true mVoiceInteraction=" + this.mVoiceInteraction);
        }
        pw.println(prefix + "component=" + this.mActivityComponent.flattenToShortString());
        pw.print(prefix);
        pw.print("task=");
        pw.println(getTask());
        pw.print(prefix);
        pw.print(" mFillsParent=");
        pw.print(this.mFillsParent);
        pw.print(" mOrientation=");
        pw.println(this.mOrientation);
        StringBuilder sb = new StringBuilder();
        sb.append(prefix);
        sb.append("hiddenRequested=");
        sb.append(this.hiddenRequested);
        sb.append(" mClientHidden=");
        sb.append(this.mClientHidden);
        if (this.mDeferHidingClient) {
            str = " mDeferHidingClient=" + this.mDeferHidingClient;
        } else {
            str = "";
        }
        sb.append(str);
        sb.append(" reportedDrawn=");
        sb.append(this.reportedDrawn);
        sb.append(" reportedVisible=");
        sb.append(this.reportedVisible);
        pw.println(sb.toString());
        if (this.paused) {
            pw.print(prefix);
            pw.print("paused=");
            pw.println(this.paused);
        }
        if (this.mAppStopped) {
            pw.print(prefix);
            pw.print("mAppStopped=");
            pw.println(this.mAppStopped);
        }
        if (this.mNumInterestingWindows != 0 || this.mNumDrawnWindows != 0 || this.allDrawn || this.mLastAllDrawn) {
            pw.print(prefix);
            pw.print("mNumInterestingWindows=");
            pw.print(this.mNumInterestingWindows);
            pw.print(" mNumDrawnWindows=");
            pw.print(this.mNumDrawnWindows);
            pw.print(" inPendingTransaction=");
            pw.print(this.inPendingTransaction);
            pw.print(" allDrawn=");
            pw.print(this.allDrawn);
            pw.print(" lastAllDrawn=");
            pw.print(this.mLastAllDrawn);
            pw.print(" mIsCastMode=");
            pw.print(this.mIsCastMode);
            pw.println(")");
        }
        if (this.inPendingTransaction) {
            pw.print(prefix);
            pw.print("inPendingTransaction=");
            pw.println(this.inPendingTransaction);
        }
        if (this.mStartingData != null || this.removed || this.firstWindowDrawn || this.mIsExiting) {
            pw.print(prefix);
            pw.print("startingData=");
            pw.print(this.mStartingData);
            pw.print(" removed=");
            pw.print(this.removed);
            pw.print(" firstWindowDrawn=");
            pw.print(this.firstWindowDrawn);
            pw.print(" mIsExiting=");
            pw.println(this.mIsExiting);
        }
        if (this.startingWindow != null || this.startingSurface != null || this.startingDisplayed || this.startingMoved || this.mHiddenSetFromTransferredStartingWindow) {
            pw.print(prefix);
            pw.print("startingWindow=");
            pw.print(this.startingWindow);
            pw.print(" startingSurface=");
            pw.print(this.startingSurface);
            pw.print(" startingDisplayed=");
            pw.print(this.startingDisplayed);
            pw.print(" startingMoved=");
            pw.print(this.startingMoved);
            pw.println(" mHiddenSetFromTransferredStartingWindow=" + this.mHiddenSetFromTransferredStartingWindow);
        }
        if (!this.mFrozenBounds.isEmpty()) {
            pw.print(prefix);
            pw.print("mFrozenBounds=");
            pw.println(this.mFrozenBounds);
            pw.print(prefix);
            pw.print("mFrozenMergedConfig=");
            pw.println(this.mFrozenMergedConfig);
        }
        if (this.mPendingRelaunchCount != 0) {
            pw.print(prefix);
            pw.print("mPendingRelaunchCount=");
            pw.println(this.mPendingRelaunchCount);
        }
        if (!(this.mSizeCompatScale == 1.0f && this.mSizeCompatBounds == null)) {
            pw.println(prefix + "mSizeCompatScale=" + this.mSizeCompatScale + " mSizeCompatBounds=" + this.mSizeCompatBounds);
        }
        if (this.mRemovingFromDisplay) {
            pw.println(prefix + "mRemovingFromDisplay=" + this.mRemovingFromDisplay);
        }
        pw.println(prefix + "mMiuiConfigFlag=" + this.mMiuiConfigFlag);
    }

    /* access modifiers changed from: package-private */
    public void setHidden(boolean hidden) {
        super.setHidden(hidden);
        if (hidden) {
            this.mDisplayContent.mPinnedStackControllerLocked.resetReentrySnapFraction(this);
        }
        scheduleAnimation();
    }

    /* access modifiers changed from: package-private */
    public void prepareSurfaces() {
        boolean show = !isHidden() || super.isSelfAnimating();
        if (this.mSurfaceControl != null) {
            if (show && !this.mLastSurfaceShowing) {
                getPendingTransaction().show(this.mSurfaceControl);
            } else if (!show && this.mLastSurfaceShowing && !this.mHandleByGesture && !this.mIsCastMode) {
                getPendingTransaction().hide(this.mSurfaceControl);
            }
        }
        AppWindowThumbnail appWindowThumbnail = this.mThumbnail;
        if (appWindowThumbnail != null) {
            appWindowThumbnail.setShowing(getPendingTransaction(), show);
        }
        this.mLastSurfaceShowing = show;
        super.prepareSurfaces();
    }

    /* access modifiers changed from: package-private */
    public boolean isSurfaceShowing() {
        return this.mLastSurfaceShowing;
    }

    /* access modifiers changed from: package-private */
    public boolean isFreezingScreen() {
        return this.mFreezingScreen;
    }

    /* access modifiers changed from: package-private */
    public boolean needsZBoost() {
        return this.mNeedsZBoost || super.needsZBoost();
    }

    public void writeToProto(ProtoOutputStream proto, long fieldId, int logLevel) {
        if (logLevel != 2 || isVisible()) {
            long token = proto.start(fieldId);
            writeNameToProto(proto, 1138166333441L);
            super.writeToProto(proto, 1146756268034L, logLevel);
            proto.write(1133871366147L, this.mLastSurfaceShowing);
            proto.write(1133871366148L, isWaitingForTransitionStart());
            proto.write(1133871366149L, isReallyAnimating());
            AppWindowThumbnail appWindowThumbnail = this.mThumbnail;
            if (appWindowThumbnail != null) {
                appWindowThumbnail.writeToProto(proto, 1146756268038L);
            }
            proto.write(1133871366151L, this.mFillsParent);
            proto.write(1133871366152L, this.mAppStopped);
            proto.write(1133871366153L, this.hiddenRequested);
            proto.write(1133871366154L, this.mClientHidden);
            proto.write(1133871366155L, this.mDeferHidingClient);
            proto.write(1133871366156L, this.reportedDrawn);
            proto.write(1133871366157L, this.reportedVisible);
            proto.write(1120986464270L, this.mNumInterestingWindows);
            proto.write(1120986464271L, this.mNumDrawnWindows);
            proto.write(1133871366160L, this.allDrawn);
            proto.write(1133871366161L, this.mLastAllDrawn);
            proto.write(1133871366162L, this.removed);
            WindowState windowState = this.startingWindow;
            if (windowState != null) {
                windowState.writeIdentifierToProto(proto, 1146756268051L);
            }
            proto.write(1133871366164L, this.startingDisplayed);
            proto.write(1133871366165L, this.startingMoved);
            proto.write(1133871366166L, this.mHiddenSetFromTransferredStartingWindow);
            Iterator<Rect> it = this.mFrozenBounds.iterator();
            while (it.hasNext()) {
                it.next().writeToProto(proto, 2246267895831L);
            }
            proto.end(token);
        }
    }

    /* access modifiers changed from: package-private */
    public void writeNameToProto(ProtoOutputStream proto, long fieldId) {
        IApplicationToken iApplicationToken = this.appToken;
        if (iApplicationToken != null) {
            try {
                proto.write(fieldId, iApplicationToken.getName());
            } catch (RemoteException e) {
                Slog.e("WindowManager", e.toString());
            }
        }
    }

    public String toString() {
        if (this.stringName == null) {
            this.stringName = "AppWindowToken{" + Integer.toHexString(System.identityHashCode(this)) + " token=" + this.token + '}';
        }
        StringBuilder sb = new StringBuilder();
        sb.append(this.stringName);
        sb.append(this.mIsExiting ? " mIsExiting=" : "");
        return sb.toString();
    }

    /* access modifiers changed from: package-private */
    public Rect getLetterboxInsets() {
        Letterbox letterbox = this.mLetterbox;
        if (letterbox != null) {
            return letterbox.getInsets();
        }
        return new Rect();
    }

    /* access modifiers changed from: package-private */
    public void getLetterboxInnerBounds(Rect outBounds) {
        Letterbox letterbox = this.mLetterbox;
        if (letterbox != null) {
            outBounds.set(letterbox.getInnerFrame());
        } else {
            outBounds.setEmpty();
        }
    }

    /* access modifiers changed from: package-private */
    public boolean isLetterboxOverlappingWith(Rect rect) {
        Letterbox letterbox = this.mLetterbox;
        return letterbox != null && letterbox.isOverlappingWith(rect);
    }

    /* access modifiers changed from: package-private */
    public void setWillCloseOrEnterPip(boolean willCloseOrEnterPip) {
        this.mWillCloseOrEnterPip = willCloseOrEnterPip;
    }

    /* access modifiers changed from: package-private */
    public boolean isClosingOrEnteringPip() {
        return (isAnimating() && this.hiddenRequested) || this.mWillCloseOrEnterPip;
    }

    /* access modifiers changed from: package-private */
    public boolean canShowWindows() {
        return this.allDrawn && (!isReallyAnimating() || !hasNonDefaultColorWindow());
    }

    private boolean hasNonDefaultColorWindow() {
        return forAllWindows($$Lambda$AppWindowToken$SZuTALf66_uOyp2mcyUQYEnNMBM.INSTANCE, true);
    }

    static /* synthetic */ boolean lambda$hasNonDefaultColorWindow$8(WindowState ws) {
        return ws.mAttrs.getColorMode() != 0;
    }

    private void updateColorTransform() {
        if (this.mSurfaceControl != null && this.mLastAppSaturationInfo != null) {
            getPendingTransaction().setColorTransform(this.mSurfaceControl, this.mLastAppSaturationInfo.mMatrix, this.mLastAppSaturationInfo.mTranslation);
            this.mWmService.scheduleAnimationLocked();
        }
    }

    private static class AppSaturationInfo {
        float[] mMatrix;
        float[] mTranslation;

        private AppSaturationInfo() {
            this.mMatrix = new float[9];
            this.mTranslation = new float[3];
        }

        /* access modifiers changed from: package-private */
        public void setSaturation(float[] matrix, float[] translation) {
            float[] fArr = this.mMatrix;
            System.arraycopy(matrix, 0, fArr, 0, fArr.length);
            float[] fArr2 = this.mTranslation;
            System.arraycopy(translation, 0, fArr2, 0, fArr2.length);
        }
    }

    public void setDummyVisible(boolean dummyVisible, boolean reallyVisible, boolean curVisible) {
        boolean enter;
        synchronized (this.mWmService.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                this.mIsDummyVisible = dummyVisible;
                if (curVisible) {
                    if (dummyVisible) {
                        enter = false;
                        this.mWmService.getDefaultDisplayContentLocked().mClosingApps.remove(this);
                    } else if (reallyVisible) {
                        enter = true;
                        this.mWmService.getDefaultDisplayContentLocked().mOpeningApps.remove(this);
                    } else {
                        WindowManagerService.resetPriorityAfterLockedSection();
                        return;
                    }
                    this.mWmService.mMiuiGestureController.setKeepWallpaperShowing(true);
                    this.mIsDummyAnimating = true;
                    this.mEnteringAnimation = enter;
                    this.mWmService.mDummyVisibleApp = this;
                    this.mWmService.mDummyVisibleAppEnter = enter;
                }
                WindowManagerService.resetPriorityAfterLockedSection();
            } catch (Throwable th) {
                while (true) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void clearMiuiThumbnailAnimation() {
        WindowAnimationSpec windowAnimationSpec;
        if (!(!this.mIsMiuiThumbnail || (windowAnimationSpec = this.mWindowAnimationSpec) == null || this.mThumbnail == null || windowAnimationSpec.mThumbnailHelper == null)) {
            this.mWindowAnimationSpec.mThumbnailHelper.clearMiuiThumbnail();
            this.mWindowAnimationSpec.mThumbnailHelper = null;
            this.mThumbnail.destroy();
            this.mThumbnail = null;
        }
        this.mIsMiuiThumbnail = false;
        this.mWindowAnimationSpec = null;
    }

    public void setCastMode(boolean enterCast) {
        synchronized (this.mWmService.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                if (findMainWindow() != null) {
                    this.mIsCastMode = enterCast;
                    this.mWmService.setCastStackId(getStack().mStackId);
                    this.mWmService.setCastMode(enterCast);
                    forAllWindows((Consumer<WindowState>) new Consumer(enterCast) {
                        private final /* synthetic */ boolean f$0;

                        {
                            this.f$0 = r1;
                        }

                        public final void accept(Object obj) {
                            ((WindowState) obj).mWinAnimator.setCastMode(this.f$0);
                        }
                    }, true);
                    findMainWindow().mWinAnimator.setCastMainWindow(enterCast);
                } else {
                    Slog.e("WindowManager", "no main window to cast");
                }
            } catch (Throwable th) {
                while (true) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
        }
        WindowManagerService.resetPriorityAfterLockedSection();
    }

    public void setMiuiConfigFlag(@WindowConfiguration.MiuiConfigFlag int miuiConfigFlag, boolean isSetToStack) {
        super.setMiuiConfigFlag(miuiConfigFlag, false);
        if (isSetToStack && (miuiConfigFlag & 2) != 0) {
            getStack().setMiuiConfigFlag(miuiConfigFlag, isSetToStack);
        }
    }
}
