package com.android.server.wm;

import android.app.AppOpsManager;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Region;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.os.SystemClock;
import android.os.Trace;
import android.os.UserHandle;
import android.os.WorkSource;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.MergedConfiguration;
import android.util.MiuiMultiWindowUtils;
import android.util.Slog;
import android.util.TimeUtils;
import android.util.proto.ProtoOutputStream;
import android.view.DisplayCutout;
import android.view.DisplayInfo;
import android.view.Gravity;
import android.view.IApplicationToken;
import android.view.IWindow;
import android.view.IWindowFocusObserver;
import android.view.IWindowId;
import android.view.InputChannel;
import android.view.InputEvent;
import android.view.InputEventReceiver;
import android.view.InputWindowHandle;
import android.view.SurfaceControl;
import android.view.SurfaceSession;
import android.view.WindowInfo;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.util.ToBooleanFunction;
import com.android.server.am.ActivityManagerService;
import com.android.server.job.controllers.JobStatus;
import com.android.server.pm.DumpState;
import com.android.server.policy.WindowManagerPolicy;
import com.android.server.usb.descriptors.UsbACInterface;
import com.android.server.wm.LocalAnimationAdapter;
import com.android.server.wm.WindowManagerService;
import com.android.server.wm.utils.InsetUtils;
import com.android.server.wm.utils.WmDisplayCutout;
import java.io.PrintWriter;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

class WindowState extends WindowContainer<WindowState> implements WindowManagerPolicy.WindowState {
    private static final float DEFAULT_DIM_AMOUNT_DEAD_WINDOW = 0.5f;
    static final int LEGACY_POLICY_VISIBILITY = 1;
    static final int MINIMUM_VISIBLE_HEIGHT_IN_DP = 32;
    static final int MINIMUM_VISIBLE_WIDTH_IN_DP = 48;
    private static final int POLICY_VISIBILITY_ALL = 3;
    static final int RESIZE_HANDLE_WIDTH_IN_DP = 0;
    static final String TAG = "WindowManager";
    private static final int VISIBLE_FOR_USER = 2;
    private static final StringBuilder sTmpSB = new StringBuilder();
    private static final Comparator<WindowState> sWindowSubLayerComparator = new Comparator<WindowState>() {
        public int compare(WindowState w1, WindowState w2) {
            int layer1 = w1.mSubLayer;
            int layer2 = w2.mSubLayer;
            if (layer1 < layer2) {
                return -1;
            }
            if (layer1 != layer2 || layer2 >= 0) {
                return 1;
            }
            return -1;
        }
    };
    private boolean mAnimateReplacingWindow;
    boolean mAnimatingExit;
    boolean mAppDied;
    boolean mAppFreezing;
    final int mAppOp;
    private boolean mAppOpVisibility;
    AppWindowToken mAppToken;
    final WindowManager.LayoutParams mAttrs;
    final int mBaseLayer;
    boolean mBlurCurrentFlagChanged;
    boolean mBlurFlagChanged;
    private int mBlurMode;
    private float mBlurRatio;
    final IWindow mClient;
    private InputChannel mClientChannel;
    final Context mContext;
    private DeadWindowEventReceiver mDeadWindowEventReceiver;
    final DeathRecipient mDeathRecipient;
    boolean mDestroying;
    private boolean mDragResizing;
    private boolean mDragResizingChangeReported;
    private PowerManager.WakeLock mDrawLock;
    private boolean mDrawnStateEvaluated;
    private final List<Rect> mExclusionRects;
    long mFinishSeamlessRotateFrameNumber;
    private RemoteCallbackList<IWindowFocusObserver> mFocusCallbacks;
    private boolean mForceHideNonSystemOverlayWindow;
    final boolean mForceSeamlesslyRotate;
    private long mFrameNumber;
    final Rect mGivenContentInsets;
    boolean mGivenInsetsPending;
    final Region mGivenTouchableRegion;
    final Rect mGivenVisibleInsets;
    float mGlobalScale;
    float mHScale;
    boolean mHasSurface;
    boolean mHasSurfaceView;
    boolean mHaveFrame;
    boolean mHidden;
    private boolean mHiddenWhileSuspended;
    boolean mInRelayout;
    InputChannel mInputChannel;
    final InputWindowHandle mInputWindowHandle;
    private final Rect mInsetFrame;
    private InsetsSourceProvider mInsetProvider;
    float mInvGlobalScale;
    private boolean mIsChildWindow;
    private boolean mIsDimming;
    private final boolean mIsFloatingLayer;
    final boolean mIsImWindow;
    final boolean mIsWallpaper;
    private boolean mLastConfigReportedToClient;
    int mLastFreezeDuration;
    float mLastHScale;
    final Rect mLastRelayoutContentInsets;
    private final MergedConfiguration mLastReportedConfiguration;
    private int mLastRequestedHeight;
    private int mLastRequestedWidth;
    final Rect mLastSurfaceInsets;
    private CharSequence mLastTitle;
    float mLastVScale;
    int mLastVisibleLayoutRotation;
    int mLayer;
    final boolean mLayoutAttached;
    boolean mLayoutNeeded;
    int mLayoutSeq;
    boolean mLegacyPolicyVisibilityAfterAnim;
    boolean mMiuiNotFocusable;
    boolean mMiuiNotTouchModal;
    private boolean mMovedByResize;
    boolean mObscured;
    private boolean mOrientationChangeTimedOut;
    private boolean mOrientationChanging;
    final boolean mOwnerCanAddInternalSystemWindow;
    final int mOwnerUid;
    SeamlessRotator mPendingSeamlessRotate;
    boolean mPermanentlyHidden;
    final WindowManagerPolicy mPolicy;
    private int mPolicyVisibility;
    private PowerManagerWrapper mPowerManagerWrapper;
    boolean mRelayoutCalled;
    boolean mRemoveOnExit;
    boolean mRemoved;
    private WindowState mReplacementWindow;
    private boolean mReplacingRemoveRequested;
    boolean mReportOrientationChanged;
    Object mRequestTraversalOnceContext;
    int mRequestedHeight;
    int mRequestedWidth;
    private int mResizeMode;
    boolean mResizedWhileGone;
    /* access modifiers changed from: package-private */
    public boolean mSeamlesslyRotated;
    int mSeq;
    final Session mSession;
    private boolean mShowToOwnerOnly;
    boolean mSkipEnterAnimationForSeamlessReplacement;
    private String mStringNameCache;
    final int mSubLayer;
    private final Point mSurfacePosition;
    int mSystemUiVisibility;
    private TapExcludeRegionHolder mTapExcludeRegionHolder;
    private final Configuration mTempConfiguration;
    final Matrix mTmpMatrix;
    private final Point mTmpPoint;
    private final Rect mTmpRect;
    WindowToken mToken;
    int mTouchableInsets;
    float mVScale;
    int mViewVisibility;
    int mWallpaperDisplayOffsetX;
    int mWallpaperDisplayOffsetY;
    boolean mWallpaperVisible;
    float mWallpaperX;
    float mWallpaperXStep;
    float mWallpaperY;
    float mWallpaperYStep;
    private boolean mWasExiting;
    private boolean mWasVisibleBeforeClientHidden;
    boolean mWillReplaceWindow;
    final WindowStateAnimator mWinAnimator;
    final WindowFrames mWindowFrames;
    final WindowId mWindowId;
    boolean mWindowRemovalAllowed;

    interface PowerManagerWrapper {
        boolean isInteractive();

        void wakeUp(long j, int i, String str);
    }

    public boolean isHoldOn() {
        return this.mToken.mIsHoldOn;
    }

    public void setHoldOn(boolean holdOn) {
        this.mToken.mIsHoldOn = holdOn;
    }

    /* access modifiers changed from: package-private */
    public void seamlesslyRotateIfAllowed(SurfaceControl.Transaction transaction, int oldRotation, int rotation, boolean requested) {
        if (isVisibleNow() && !this.mIsWallpaper) {
            SeamlessRotator seamlessRotator = this.mPendingSeamlessRotate;
            if (seamlessRotator != null) {
                oldRotation = seamlessRotator.getOldRotation();
            }
            if (this.mForceSeamlesslyRotate || requested) {
                this.mPendingSeamlessRotate = new SeamlessRotator(oldRotation, rotation, getDisplayInfo());
                this.mPendingSeamlessRotate.unrotate(transaction, this);
                this.mWmService.markForSeamlessRotation(this, true);
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void finishSeamlessRotation(boolean timeout) {
        SeamlessRotator seamlessRotator = this.mPendingSeamlessRotate;
        if (seamlessRotator != null) {
            seamlessRotator.finish(this, timeout);
            this.mFinishSeamlessRotateFrameNumber = getFrameNumber();
            this.mPendingSeamlessRotate = null;
            this.mWmService.markForSeamlessRotation(this, false);
        }
    }

    /* access modifiers changed from: package-private */
    public List<Rect> getSystemGestureExclusion() {
        return this.mExclusionRects;
    }

    /* access modifiers changed from: package-private */
    public boolean setSystemGestureExclusion(List<Rect> exclusionRects) {
        if (this.mExclusionRects.equals(exclusionRects)) {
            return false;
        }
        this.mExclusionRects.clear();
        this.mExclusionRects.addAll(exclusionRects);
        return true;
    }

    /* access modifiers changed from: package-private */
    public boolean isImplicitlyExcludingAllSystemGestures() {
        AppWindowToken appWindowToken;
        if (!((this.mSystemUiVisibility & UsbACInterface.FORMAT_II_AC3) == 4098) || !this.mWmService.mSystemGestureExcludedByPreQStickyImmersive || (appWindowToken = this.mAppToken) == null || appWindowToken.mTargetSdk >= 29) {
            return false;
        }
        return true;
    }

    /* JADX INFO: this call moved to the top of the method (can break code semantics) */
    WindowState(final WindowManagerService service, Session s, IWindow c, WindowToken token, WindowState parentWindow, int appOp, int seq, WindowManager.LayoutParams a, int viewVisibility, int ownerId, boolean ownerCanAddInternalSystemWindow) {
        this(service, s, c, token, parentWindow, appOp, seq, a, viewVisibility, ownerId, ownerCanAddInternalSystemWindow, new PowerManagerWrapper() {
            public void wakeUp(long time, int reason, String details) {
                WindowManagerService.this.mPowerManager.wakeUp(time, reason, details);
            }

            public boolean isInteractive() {
                return WindowManagerService.this.mPowerManager.isInteractive();
            }
        });
        WindowManagerService windowManagerService = service;
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    /* JADX WARNING: Removed duplicated region for block: B:46:0x01f8  */
    /* JADX WARNING: Removed duplicated region for block: B:47:0x01fb  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    WindowState(com.android.server.wm.WindowManagerService r17, com.android.server.wm.Session r18, android.view.IWindow r19, com.android.server.wm.WindowToken r20, com.android.server.wm.WindowState r21, int r22, int r23, android.view.WindowManager.LayoutParams r24, int r25, int r26, boolean r27, com.android.server.wm.WindowState.PowerManagerWrapper r28) {
        /*
            r16 = this;
            r1 = r16
            r2 = r19
            r3 = r20
            r4 = r21
            r5 = r24
            r16.<init>(r17)
            android.view.WindowManager$LayoutParams r0 = new android.view.WindowManager$LayoutParams
            r0.<init>()
            r1.mAttrs = r0
            r0 = 3
            r1.mPolicyVisibility = r0
            r0 = 1
            r1.mLegacyPolicyVisibilityAfterAnim = r0
            r1.mAppOpVisibility = r0
            r1.mHidden = r0
            r1.mDragResizingChangeReported = r0
            r6 = -1
            r1.mLayoutSeq = r6
            r7 = -1082130432(0xffffffffbf800000, float:-1.0)
            r1.mBlurRatio = r7
            r1.mBlurMode = r6
            android.util.MergedConfiguration r8 = new android.util.MergedConfiguration
            r8.<init>()
            r1.mLastReportedConfiguration = r8
            android.content.res.Configuration r8 = new android.content.res.Configuration
            r8.<init>()
            r1.mTempConfiguration = r8
            android.graphics.Rect r8 = new android.graphics.Rect
            r8.<init>()
            r1.mLastRelayoutContentInsets = r8
            android.graphics.Rect r8 = new android.graphics.Rect
            r8.<init>()
            r1.mGivenContentInsets = r8
            android.graphics.Rect r8 = new android.graphics.Rect
            r8.<init>()
            r1.mGivenVisibleInsets = r8
            android.graphics.Region r8 = new android.graphics.Region
            r8.<init>()
            r1.mGivenTouchableRegion = r8
            r8 = 0
            r1.mTouchableInsets = r8
            r9 = 1065353216(0x3f800000, float:1.0)
            r1.mGlobalScale = r9
            r1.mInvGlobalScale = r9
            r1.mHScale = r9
            r1.mVScale = r9
            r1.mLastHScale = r9
            r1.mLastVScale = r9
            android.graphics.Matrix r9 = new android.graphics.Matrix
            r9.<init>()
            r1.mTmpMatrix = r9
            com.android.server.wm.WindowFrames r9 = new com.android.server.wm.WindowFrames
            r9.<init>()
            r1.mWindowFrames = r9
            android.graphics.Rect r9 = new android.graphics.Rect
            r9.<init>()
            r1.mInsetFrame = r9
            java.util.ArrayList r9 = new java.util.ArrayList
            r9.<init>()
            r1.mExclusionRects = r9
            r1.mWallpaperX = r7
            r1.mWallpaperY = r7
            r1.mWallpaperXStep = r7
            r1.mWallpaperYStep = r7
            r7 = -2147483648(0xffffffff80000000, float:-0.0)
            r1.mWallpaperDisplayOffsetX = r7
            r1.mWallpaperDisplayOffsetY = r7
            r1.mBlurFlagChanged = r8
            r1.mMiuiNotFocusable = r8
            r1.mMiuiNotTouchModal = r8
            r1.mHasSurfaceView = r8
            r1.mBlurCurrentFlagChanged = r8
            r1.mLastVisibleLayoutRotation = r6
            r1.mHasSurface = r8
            r1.mWillReplaceWindow = r8
            r1.mReplacingRemoveRequested = r8
            r1.mAnimateReplacingWindow = r8
            r6 = 0
            r1.mReplacementWindow = r6
            r1.mSkipEnterAnimationForSeamlessReplacement = r8
            android.graphics.Rect r7 = new android.graphics.Rect
            r7.<init>()
            r1.mTmpRect = r7
            android.graphics.Point r7 = new android.graphics.Point
            r7.<init>()
            r1.mTmpPoint = r7
            r1.mResizedWhileGone = r8
            r1.mSeamlesslyRotated = r8
            android.graphics.Rect r7 = new android.graphics.Rect
            r7.<init>()
            r1.mLastSurfaceInsets = r7
            android.graphics.Point r7 = new android.graphics.Point
            r7.<init>()
            r1.mSurfacePosition = r7
            r9 = -1
            r1.mFrameNumber = r9
            r1.mIsDimming = r8
            r7 = r18
            r1.mSession = r7
            r1.mClient = r2
            r9 = r22
            r1.mAppOp = r9
            r1.mToken = r3
            com.android.server.wm.WindowToken r10 = r1.mToken
            com.android.server.wm.AppWindowToken r10 = r10.asAppWindowToken()
            r1.mAppToken = r10
            r10 = r26
            r1.mOwnerUid = r10
            r11 = r27
            r1.mOwnerCanAddInternalSystemWindow = r11
            com.android.server.wm.WindowState$WindowId r12 = new com.android.server.wm.WindowState$WindowId
            r12.<init>()
            r1.mWindowId = r12
            android.view.WindowManager$LayoutParams r12 = r1.mAttrs
            r12.copyFrom(r5)
            android.graphics.Rect r12 = r1.mLastSurfaceInsets
            android.view.WindowManager$LayoutParams r13 = r1.mAttrs
            android.graphics.Rect r13 = r13.surfaceInsets
            r12.set(r13)
            r12 = r25
            r1.mViewVisibility = r12
            com.android.server.wm.WindowManagerService r13 = r1.mWmService
            com.android.server.policy.WindowManagerPolicy r13 = r13.mPolicy
            r1.mPolicy = r13
            com.android.server.wm.WindowManagerService r13 = r1.mWmService
            android.content.Context r13 = r13.mContext
            r1.mContext = r13
            com.android.server.wm.WindowState$DeathRecipient r13 = new com.android.server.wm.WindowState$DeathRecipient
            r13.<init>()
            r14 = r23
            r1.mSeq = r14
            r15 = r28
            r1.mPowerManagerWrapper = r15
            boolean r6 = r3.mRoundedCornerOverlay
            r1.mForceSeamlesslyRotate = r6
            android.os.IBinder r6 = r19.asBinder()     // Catch:{ RemoteException -> 0x0206 }
            r6.linkToDeath(r13, r8)     // Catch:{ RemoteException -> 0x0206 }
            r1.mDeathRecipient = r13
            android.view.WindowManager$LayoutParams r6 = r1.mAttrs
            int r6 = r6.type
            r8 = 1000(0x3e8, float:1.401E-42)
            if (r6 < r8) goto L_0x0188
            android.view.WindowManager$LayoutParams r6 = r1.mAttrs
            int r6 = r6.type
            r0 = 1999(0x7cf, float:2.801E-42)
            if (r6 > r0) goto L_0x0187
            com.android.server.policy.WindowManagerPolicy r0 = r1.mPolicy
            int r0 = r0.getWindowLayerLw(r4)
            int r0 = r0 * 10000
            int r0 = r0 + r8
            r1.mBaseLayer = r0
            com.android.server.policy.WindowManagerPolicy r0 = r1.mPolicy
            int r6 = r5.type
            int r0 = r0.getSubWindowLayerFromTypeLw(r6)
            r1.mSubLayer = r0
            r0 = 1
            r1.mIsChildWindow = r0
            java.util.Comparator<com.android.server.wm.WindowState> r6 = sWindowSubLayerComparator
            r4.addChild(r1, r6)
            android.view.WindowManager$LayoutParams r6 = r1.mAttrs
            int r6 = r6.type
            r8 = 1003(0x3eb, float:1.406E-42)
            if (r6 == r8) goto L_0x0160
            r6 = r0
            goto L_0x0161
        L_0x0160:
            r6 = 0
        L_0x0161:
            r1.mLayoutAttached = r6
            android.view.WindowManager$LayoutParams r6 = r4.mAttrs
            int r6 = r6.type
            r8 = 2011(0x7db, float:2.818E-42)
            if (r6 == r8) goto L_0x0176
            android.view.WindowManager$LayoutParams r6 = r4.mAttrs
            int r6 = r6.type
            r8 = 2012(0x7dc, float:2.82E-42)
            if (r6 != r8) goto L_0x0174
            goto L_0x0176
        L_0x0174:
            r6 = 0
            goto L_0x0177
        L_0x0176:
            r6 = r0
        L_0x0177:
            r1.mIsImWindow = r6
            android.view.WindowManager$LayoutParams r6 = r4.mAttrs
            int r6 = r6.type
            r8 = 2013(0x7dd, float:2.821E-42)
            if (r6 != r8) goto L_0x0183
            r6 = r0
            goto L_0x0184
        L_0x0183:
            r6 = 0
        L_0x0184:
            r1.mIsWallpaper = r6
            goto L_0x01bd
        L_0x0187:
            r0 = 1
        L_0x0188:
            com.android.server.policy.WindowManagerPolicy r6 = r1.mPolicy
            int r6 = r6.getWindowLayerLw(r1)
            int r6 = r6 * 10000
            int r6 = r6 + r8
            r1.mBaseLayer = r6
            r6 = 0
            r1.mSubLayer = r6
            r1.mIsChildWindow = r6
            r1.mLayoutAttached = r6
            android.view.WindowManager$LayoutParams r6 = r1.mAttrs
            int r6 = r6.type
            r8 = 2011(0x7db, float:2.818E-42)
            if (r6 == r8) goto L_0x01ad
            android.view.WindowManager$LayoutParams r6 = r1.mAttrs
            int r6 = r6.type
            r8 = 2012(0x7dc, float:2.82E-42)
            if (r6 != r8) goto L_0x01ab
            goto L_0x01ad
        L_0x01ab:
            r6 = 0
            goto L_0x01ae
        L_0x01ad:
            r6 = r0
        L_0x01ae:
            r1.mIsImWindow = r6
            android.view.WindowManager$LayoutParams r6 = r1.mAttrs
            int r6 = r6.type
            r8 = 2013(0x7dd, float:2.821E-42)
            if (r6 != r8) goto L_0x01ba
            r6 = r0
            goto L_0x01bb
        L_0x01ba:
            r6 = 0
        L_0x01bb:
            r1.mIsWallpaper = r6
        L_0x01bd:
            boolean r6 = r1.mIsImWindow
            if (r6 != 0) goto L_0x01c7
            boolean r6 = r1.mIsWallpaper
            if (r6 == 0) goto L_0x01c6
            goto L_0x01c7
        L_0x01c6:
            r0 = 0
        L_0x01c7:
            r1.mIsFloatingLayer = r0
            com.android.server.wm.AppWindowToken r0 = r1.mAppToken
            if (r0 == 0) goto L_0x01da
            boolean r0 = r0.mShowForAllUsers
            if (r0 == 0) goto L_0x01da
            android.view.WindowManager$LayoutParams r0 = r1.mAttrs
            int r6 = r0.flags
            r8 = 524288(0x80000, float:7.34684E-40)
            r6 = r6 | r8
            r0.flags = r6
        L_0x01da:
            com.android.server.wm.WindowStateAnimator r0 = new com.android.server.wm.WindowStateAnimator
            r0.<init>(r1)
            r1.mWinAnimator = r0
            com.android.server.wm.WindowStateAnimator r0 = r1.mWinAnimator
            float r6 = r5.alpha
            r0.mAlpha = r6
            r6 = 0
            r1.mRequestedWidth = r6
            r1.mRequestedHeight = r6
            r1.mLastRequestedWidth = r6
            r1.mLastRequestedHeight = r6
            r1.mLayer = r6
            android.view.InputWindowHandle r0 = new android.view.InputWindowHandle
            com.android.server.wm.AppWindowToken r6 = r1.mAppToken
            if (r6 == 0) goto L_0x01fb
            android.view.InputApplicationHandle r6 = r6.mInputApplicationHandle
            goto L_0x01fc
        L_0x01fb:
            r6 = 0
        L_0x01fc:
            int r8 = r16.getDisplayId()
            r0.<init>(r6, r2, r8)
            r1.mInputWindowHandle = r0
            return
        L_0x0206:
            r0 = move-exception
            r6 = 0
            r1.mDeathRecipient = r6
            r6 = 0
            r1.mIsChildWindow = r6
            r1.mLayoutAttached = r6
            r1.mIsImWindow = r6
            r1.mIsWallpaper = r6
            r1.mIsFloatingLayer = r6
            r1.mBaseLayer = r6
            r1.mSubLayer = r6
            r6 = 0
            r1.mInputWindowHandle = r6
            r1.mWinAnimator = r6
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.wm.WindowState.<init>(com.android.server.wm.WindowManagerService, com.android.server.wm.Session, android.view.IWindow, com.android.server.wm.WindowToken, com.android.server.wm.WindowState, int, int, android.view.WindowManager$LayoutParams, int, int, boolean, com.android.server.wm.WindowState$PowerManagerWrapper):void");
    }

    /* access modifiers changed from: package-private */
    public void attach() {
        this.mSession.windowAddedLocked(this.mAttrs.packageName);
    }

    /* access modifiers changed from: package-private */
    /* JADX WARNING: Code restructure failed: missing block: B:2:0x0008, code lost:
        r0 = r2.mAppToken;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean inSizeCompatMode() {
        /*
            r2 = this;
            android.view.WindowManager$LayoutParams r0 = r2.mAttrs
            int r0 = r0.privateFlags
            r0 = r0 & 128(0x80, float:1.794E-43)
            if (r0 != 0) goto L_0x001c
            com.android.server.wm.AppWindowToken r0 = r2.mAppToken
            if (r0 == 0) goto L_0x001a
            boolean r0 = r0.inSizeCompatMode()
            if (r0 == 0) goto L_0x001a
            android.view.WindowManager$LayoutParams r0 = r2.mAttrs
            int r0 = r0.type
            r1 = 3
            if (r0 == r1) goto L_0x001a
            goto L_0x001c
        L_0x001a:
            r0 = 0
            goto L_0x001d
        L_0x001c:
            r0 = 1
        L_0x001d:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.wm.WindowState.inSizeCompatMode():boolean");
    }

    /* access modifiers changed from: package-private */
    public boolean getDrawnStateEvaluated() {
        return this.mDrawnStateEvaluated;
    }

    /* access modifiers changed from: package-private */
    public void setDrawnStateEvaluated(boolean evaluated) {
        this.mDrawnStateEvaluated = evaluated;
    }

    /* access modifiers changed from: package-private */
    public void onParentChanged() {
        super.onParentChanged();
        setDrawnStateEvaluated(false);
        getDisplayContent().reapplyMagnificationSpec();
    }

    public int getOwningUid() {
        return this.mOwnerUid;
    }

    public String getOwningPackage() {
        return this.mAttrs.packageName;
    }

    public boolean canAddInternalSystemWindow() {
        return this.mOwnerCanAddInternalSystemWindow;
    }

    public boolean canAcquireSleepToken() {
        return this.mSession.mCanAcquireSleepToken;
    }

    private void subtractInsets(Rect frame, Rect layoutFrame, Rect insetFrame, Rect displayFrame) {
        frame.inset(Math.max(0, insetFrame.left - Math.max(layoutFrame.left, displayFrame.left)), Math.max(0, insetFrame.top - Math.max(layoutFrame.top, displayFrame.top)), Math.max(0, Math.min(layoutFrame.right, displayFrame.right) - insetFrame.right), Math.max(0, Math.min(layoutFrame.bottom, displayFrame.bottom) - insetFrame.bottom));
    }

    public Rect getDisplayedBounds() {
        Task task = getTask();
        if (task != null) {
            Rect bounds = task.getOverrideDisplayedBounds();
            if (!bounds.isEmpty()) {
                return bounds;
            }
        }
        return super.getDisplayedBounds();
    }

    public void computeFrameLw() {
        Rect layoutContainingFrame;
        int layoutYDiff;
        int layoutXDiff;
        Rect layoutDisplayFrame;
        DisplayContent displayContent;
        if (!this.mWillReplaceWindow || (!this.mAnimatingExit && this.mReplacingRemoveRequested)) {
            this.mHaveFrame = true;
            Task task = getTask();
            boolean isFullscreenAndFillsDisplay = !inMultiWindowMode() && matchesDisplayBounds();
            boolean windowsAreFloating = task != null && task.isFloating();
            DisplayContent dc = getDisplayContent();
            this.mInsetFrame.set(getBounds());
            if (inFreeformWindowingMode()) {
                WindowStateInjector.adjuestScaleAndFrame(this, task, this.mWindowFrames.mParentFrame, this.mWindowFrames.mDisplayFrame, this.mWindowFrames.mOverscanFrame, this.mWindowFrames.mContentFrame, this.mWindowFrames.mVisibleFrame, this.mWindowFrames.mDecorFrame, this.mWindowFrames.mStableFrame, this.mWindowFrames.mOutsetFrame);
            }
            WindowState imeWin = this.mWmService.mRoot.getCurrentInputMethodWindow();
            boolean isImeTarget = imeWin != null && imeWin.isVisibleNow() && isInputMethodTarget();
            if (isFullscreenAndFillsDisplay || layoutInParentFrame()) {
                this.mWindowFrames.mContainingFrame.set(this.mWindowFrames.mParentFrame);
                layoutDisplayFrame = this.mWindowFrames.mDisplayFrame;
                layoutContainingFrame = this.mWindowFrames.mParentFrame;
                layoutXDiff = 0;
                layoutYDiff = 0;
            } else {
                this.mWindowFrames.mContainingFrame.set(getDisplayedBounds());
                AppWindowToken appWindowToken = this.mAppToken;
                if (appWindowToken != null && !appWindowToken.mFrozenBounds.isEmpty()) {
                    Rect frozen = this.mAppToken.mFrozenBounds.peek();
                    this.mWindowFrames.mContainingFrame.right = this.mWindowFrames.mContainingFrame.left + frozen.width();
                    this.mWindowFrames.mContainingFrame.bottom = this.mWindowFrames.mContainingFrame.top + frozen.height();
                }
                if (isImeTarget) {
                    if (inFreeformWindowingMode()) {
                        int bottomOverlap = this.mWindowFrames.mContainingFrame.bottom - this.mWindowFrames.mVisibleFrame.bottom;
                        if (bottomOverlap > 0) {
                            this.mWindowFrames.mContainingFrame.top -= Math.min(bottomOverlap, Math.max(this.mWindowFrames.mContainingFrame.top - this.mWindowFrames.mDisplayFrame.top, 0));
                        }
                    } else if (inPinnedWindowingMode() == 0 && this.mWindowFrames.mContainingFrame.bottom > this.mWindowFrames.mParentFrame.bottom) {
                        this.mWindowFrames.mContainingFrame.bottom = this.mWindowFrames.mParentFrame.bottom;
                    }
                }
                if (windowsAreFloating && this.mWindowFrames.mContainingFrame.isEmpty()) {
                    this.mWindowFrames.mContainingFrame.set(this.mWindowFrames.mContentFrame);
                }
                TaskStack stack = getStack();
                if (inPinnedWindowingMode() && stack != null && stack.lastAnimatingBoundsWasToFullscreen()) {
                    this.mInsetFrame.intersectUnchecked(this.mWindowFrames.mParentFrame);
                    this.mWindowFrames.mContainingFrame.intersectUnchecked(this.mWindowFrames.mParentFrame);
                }
                layoutDisplayFrame = new Rect(this.mWindowFrames.mDisplayFrame);
                this.mWindowFrames.mDisplayFrame.set(this.mWindowFrames.mContainingFrame);
                layoutXDiff = this.mInsetFrame.left - this.mWindowFrames.mContainingFrame.left;
                layoutYDiff = this.mInsetFrame.top - this.mWindowFrames.mContainingFrame.top;
                layoutContainingFrame = this.mInsetFrame;
                this.mTmpRect.set(0, 0, dc.getDisplayInfo().logicalWidth, dc.getDisplayInfo().logicalHeight);
                subtractInsets(this.mWindowFrames.mDisplayFrame, layoutContainingFrame, layoutDisplayFrame, this.mTmpRect);
                if (!layoutInParentFrame()) {
                    subtractInsets(this.mWindowFrames.mContainingFrame, layoutContainingFrame, this.mWindowFrames.mParentFrame, this.mTmpRect);
                    subtractInsets(this.mInsetFrame, layoutContainingFrame, this.mWindowFrames.mParentFrame, this.mTmpRect);
                }
                layoutDisplayFrame.intersect(layoutContainingFrame);
            }
            int pw = this.mWindowFrames.mContainingFrame.width();
            int height = this.mWindowFrames.mContainingFrame.height();
            if (!(this.mRequestedWidth == this.mLastRequestedWidth && this.mRequestedHeight == this.mLastRequestedHeight)) {
                this.mLastRequestedWidth = this.mRequestedWidth;
                this.mLastRequestedHeight = this.mRequestedHeight;
                this.mWindowFrames.setContentChanged(true);
            }
            int fw = this.mWindowFrames.mFrame.width();
            int fh = this.mWindowFrames.mFrame.height();
            applyGravityAndUpdateFrame(layoutContainingFrame, layoutDisplayFrame);
            this.mWindowFrames.calculateOutsets();
            if (!windowsAreFloating || this.mWindowFrames.mFrame.isEmpty()) {
                int i = pw;
                if (this.mAttrs.type == 2034) {
                    dc.getDockedDividerController().positionDockedStackedDivider(this.mWindowFrames.mFrame);
                    this.mWindowFrames.mContentFrame.set(this.mWindowFrames.mFrame);
                    if (!this.mWindowFrames.mFrame.equals(this.mWindowFrames.mLastFrame)) {
                        this.mMovedByResize = true;
                        boolean z = isImeTarget;
                        Rect rect = layoutDisplayFrame;
                    } else {
                        boolean z2 = isImeTarget;
                        Rect rect2 = layoutDisplayFrame;
                    }
                } else {
                    boolean z3 = isImeTarget;
                    Rect rect3 = layoutDisplayFrame;
                    this.mWindowFrames.mContentFrame.set(Math.max(this.mWindowFrames.mContentFrame.left, this.mWindowFrames.mFrame.left), Math.max(this.mWindowFrames.mContentFrame.top, this.mWindowFrames.mFrame.top), Math.min(this.mWindowFrames.mContentFrame.right, this.mWindowFrames.mFrame.right), Math.min(this.mWindowFrames.mContentFrame.bottom, this.mWindowFrames.mFrame.bottom));
                    this.mWindowFrames.mVisibleFrame.set(Math.max(this.mWindowFrames.mVisibleFrame.left, this.mWindowFrames.mFrame.left), Math.max(this.mWindowFrames.mVisibleFrame.top, this.mWindowFrames.mFrame.top), Math.min(this.mWindowFrames.mVisibleFrame.right, this.mWindowFrames.mFrame.right), Math.min(this.mWindowFrames.mVisibleFrame.bottom, this.mWindowFrames.mFrame.bottom));
                    this.mWindowFrames.mStableFrame.set(Math.max(this.mWindowFrames.mStableFrame.left, this.mWindowFrames.mFrame.left), Math.max(this.mWindowFrames.mStableFrame.top, this.mWindowFrames.mFrame.top), Math.min(this.mWindowFrames.mStableFrame.right, this.mWindowFrames.mFrame.right), Math.min(this.mWindowFrames.mStableFrame.bottom, this.mWindowFrames.mFrame.bottom));
                }
            } else {
                int visBottom = this.mWindowFrames.mVisibleFrame.bottom;
                int contentBottom = this.mWindowFrames.mContentFrame.bottom;
                WindowState windowState = imeWin;
                int i2 = pw;
                this.mWindowFrames.mContentFrame.set(this.mWindowFrames.mFrame);
                this.mWindowFrames.mVisibleFrame.set(this.mWindowFrames.mContentFrame);
                this.mWindowFrames.mStableFrame.set(this.mWindowFrames.mContentFrame);
                if (isImeTarget && inFreeformWindowingMode()) {
                    if (contentBottom + layoutYDiff < this.mWindowFrames.mContentFrame.bottom) {
                        this.mWindowFrames.mContentFrame.bottom = contentBottom + layoutYDiff;
                    }
                    if (visBottom + layoutYDiff < this.mWindowFrames.mVisibleFrame.bottom) {
                        this.mWindowFrames.mVisibleFrame.bottom = visBottom + layoutYDiff;
                    }
                }
                boolean z4 = isImeTarget;
                Rect rect4 = layoutDisplayFrame;
            }
            if (isFullscreenAndFillsDisplay && !windowsAreFloating) {
                InsetUtils.insetsBetweenFrames(layoutContainingFrame, this.mWindowFrames.mOverscanFrame, this.mWindowFrames.mOverscanInsets);
            }
            if (this.mAttrs.type == 2034) {
                this.mWindowFrames.calculateDockedDividerInsets(this.mWindowFrames.mDisplayCutout.calculateRelativeTo(this.mWindowFrames.mDisplayFrame).getDisplayCutout().getSafeInsets());
            } else {
                getDisplayContent().getBounds(this.mTmpRect);
                this.mWindowFrames.calculateInsets(windowsAreFloating, isFullscreenAndFillsDisplay, this.mTmpRect);
            }
            WindowFrames windowFrames = this.mWindowFrames;
            windowFrames.setDisplayCutout(windowFrames.mDisplayCutout.calculateRelativeTo(this.mWindowFrames.mFrame));
            this.mWindowFrames.offsetFrames(-layoutXDiff, -layoutYDiff);
            this.mWindowFrames.mCompatFrame.set(this.mWindowFrames.mFrame);
            if (inSizeCompatMode()) {
                this.mWindowFrames.scaleInsets(this.mInvGlobalScale);
                this.mWindowFrames.mCompatFrame.scale(this.mInvGlobalScale);
            }
            if (this.mIsWallpaper && !((fw == this.mWindowFrames.mFrame.width() && fh == this.mWindowFrames.mFrame.height()) || (displayContent = getDisplayContent()) == null)) {
                DisplayInfo displayInfo = displayContent.getDisplayInfo();
                getDisplayContent().mWallpaperController.updateWallpaperOffset(this, displayInfo.logicalWidth, displayInfo.logicalHeight, false);
            }
            if (inFreeformWindowingMode()) {
                WindowStateInjector.adjuestFrameAndInsets(this);
            }
        }
    }

    public Rect getBounds() {
        AppWindowToken appWindowToken = this.mAppToken;
        if (appWindowToken != null) {
            return appWindowToken.getBounds();
        }
        return super.getBounds();
    }

    public Rect getFrameLw() {
        return this.mWindowFrames.mFrame;
    }

    public Rect getDisplayFrameLw() {
        return this.mWindowFrames.mDisplayFrame;
    }

    public Rect getOverscanFrameLw() {
        return this.mWindowFrames.mOverscanFrame;
    }

    public Rect getContentFrameLw() {
        return this.mWindowFrames.mContentFrame;
    }

    public Rect getVisibleFrameLw() {
        return this.mWindowFrames.mVisibleFrame;
    }

    /* access modifiers changed from: package-private */
    public Rect getStableFrameLw() {
        return this.mWindowFrames.mStableFrame;
    }

    /* access modifiers changed from: package-private */
    public Rect getDecorFrame() {
        return this.mWindowFrames.mDecorFrame;
    }

    /* access modifiers changed from: package-private */
    public Rect getParentFrame() {
        return this.mWindowFrames.mParentFrame;
    }

    /* access modifiers changed from: package-private */
    public Rect getContainingFrame() {
        return this.mWindowFrames.mContainingFrame;
    }

    /* access modifiers changed from: package-private */
    public WmDisplayCutout getWmDisplayCutout() {
        return this.mWindowFrames.mDisplayCutout;
    }

    /* access modifiers changed from: package-private */
    public void getCompatFrame(Rect outFrame) {
        outFrame.set(this.mWindowFrames.mCompatFrame);
    }

    /* access modifiers changed from: package-private */
    public void setCompatFrame(Rect inFrame) {
        this.mWindowFrames.mCompatFrame.set(inFrame);
    }

    /* access modifiers changed from: package-private */
    public void getCompatFrameSize(Rect outFrame) {
        outFrame.set(0, 0, this.mWindowFrames.mCompatFrame.width(), this.mWindowFrames.mCompatFrame.height());
    }

    public boolean getGivenInsetsPendingLw() {
        return this.mGivenInsetsPending;
    }

    public Rect getGivenContentInsetsLw() {
        return this.mGivenContentInsets;
    }

    public Rect getGivenVisibleInsetsLw() {
        return this.mGivenVisibleInsets;
    }

    public WindowManager.LayoutParams getAttrs() {
        return this.mAttrs;
    }

    public boolean getNeedsMenuLw(WindowManagerPolicy.WindowState bottom) {
        return getDisplayContent().getNeedsMenu(this, bottom);
    }

    public int getSystemUiVisibility() {
        return this.mSystemUiVisibility;
    }

    public int getSurfaceLayer() {
        return this.mLayer;
    }

    public int getBaseType() {
        return getTopParentWindow().mAttrs.type;
    }

    public IApplicationToken getAppToken() {
        AppWindowToken appWindowToken = this.mAppToken;
        if (appWindowToken != null) {
            return appWindowToken.appToken;
        }
        return null;
    }

    public boolean isVoiceInteraction() {
        AppWindowToken appWindowToken = this.mAppToken;
        return appWindowToken != null && appWindowToken.mVoiceInteraction;
    }

    /* access modifiers changed from: package-private */
    public boolean setReportResizeHints() {
        return this.mWindowFrames.setReportResizeHints();
    }

    /* access modifiers changed from: package-private */
    public void updateResizingWindowIfNeeded() {
        WindowStateAnimator winAnimator = this.mWinAnimator;
        boolean isWindowDisappear = !this.mHasSurface || getDisplayContent().mLayoutSeq != this.mLayoutSeq || isGoneForLayoutLw() || this.mWmService.mDestroySurface.contains(this);
        if (this.mWmService.isGestureOpen()) {
            if (!this.mIsWallpaper && isWindowDisappear) {
                return;
            }
        } else if (isWindowDisappear) {
            return;
        }
        Task task = getTask();
        if (task == null || !task.mStack.isAnimatingBounds()) {
            boolean didFrameInsetsChange = setReportResizeHints();
            boolean configChanged = !isLastConfigReportedToClient();
            boolean dragResizingChanged = isDragResizeChanged() && !isDragResizingChangeReported();
            this.mWindowFrames.mLastFrame.set(this.mWindowFrames.mFrame);
            if (didFrameInsetsChange || winAnimator.mSurfaceResized || configChanged || dragResizingChanged || this.mReportOrientationChanged) {
                AppWindowToken appWindowToken = this.mAppToken;
                if (appWindowToken == null || !this.mAppDied) {
                    updateLastInsetValues();
                    this.mWmService.makeWindowFreezingScreenIfNeededLocked(this);
                    if (getOrientationChanging() || dragResizingChanged) {
                        winAnimator.mDrawState = 1;
                        AppWindowToken appWindowToken2 = this.mAppToken;
                        if (appWindowToken2 != null) {
                            appWindowToken2.clearAllDrawn();
                        }
                    }
                    if (!this.mWmService.mResizingWindows.contains(this)) {
                        this.mWmService.mResizingWindows.add(this);
                        return;
                    }
                    return;
                }
                appWindowToken.removeDeadWindows();
            } else if (getOrientationChanging() && isDrawnLw()) {
                setOrientationChanging(false);
                this.mLastFreezeDuration = (int) (SystemClock.elapsedRealtime() - this.mWmService.mDisplayFreezeTime);
            }
        }
    }

    /* access modifiers changed from: package-private */
    public boolean getOrientationChanging() {
        return (this.mOrientationChanging || (isVisible() && getConfiguration().orientation != getLastReportedConfiguration().orientation)) && !this.mSeamlesslyRotated && !this.mOrientationChangeTimedOut;
    }

    /* access modifiers changed from: package-private */
    public void setOrientationChanging(boolean changing) {
        this.mOrientationChanging = changing;
        this.mOrientationChangeTimedOut = false;
    }

    /* access modifiers changed from: package-private */
    public void orientationChangeTimedOut() {
        this.mOrientationChangeTimedOut = true;
    }

    /* access modifiers changed from: package-private */
    public DisplayContent getDisplayContent() {
        return this.mToken.getDisplayContent();
    }

    /* access modifiers changed from: package-private */
    public void onDisplayChanged(DisplayContent dc) {
        super.onDisplayChanged(dc);
        if (dc != null && this.mInputWindowHandle.displayId != dc.getDisplayId()) {
            this.mLayoutSeq = dc.mLayoutSeq - 1;
            this.mInputWindowHandle.displayId = dc.getDisplayId();
        }
    }

    /* access modifiers changed from: package-private */
    public DisplayInfo getDisplayInfo() {
        DisplayContent displayContent = getDisplayContent();
        if (displayContent != null) {
            return displayContent.getDisplayInfo();
        }
        return null;
    }

    public int getDisplayId() {
        DisplayContent displayContent = getDisplayContent();
        if (displayContent == null) {
            return -1;
        }
        return displayContent.getDisplayId();
    }

    /* access modifiers changed from: package-private */
    public Task getTask() {
        AppWindowToken appWindowToken = this.mAppToken;
        if (appWindowToken != null) {
            return appWindowToken.getTask();
        }
        return null;
    }

    /* access modifiers changed from: package-private */
    public TaskStack getStack() {
        Task task = getTask();
        if (task != null && task.mStack != null) {
            return task.mStack;
        }
        DisplayContent dc = getDisplayContent();
        if (this.mAttrs.type < 2000 || dc == null) {
            return null;
        }
        return dc.getHomeStack();
    }

    /* access modifiers changed from: package-private */
    public void getVisibleBounds(Rect bounds) {
        Task task = getTask();
        boolean intersectWithStackBounds = task != null && task.cropWindowsToStackBounds();
        bounds.setEmpty();
        this.mTmpRect.setEmpty();
        if (intersectWithStackBounds) {
            TaskStack stack = task.mStack;
            if (stack != null) {
                stack.getDimBounds(this.mTmpRect);
            } else {
                intersectWithStackBounds = false;
            }
        }
        bounds.set(this.mWindowFrames.mVisibleFrame);
        if (intersectWithStackBounds) {
            bounds.intersect(this.mTmpRect);
        }
        if (bounds.isEmpty()) {
            bounds.set(this.mWindowFrames.mFrame);
            if (intersectWithStackBounds) {
                bounds.intersect(this.mTmpRect);
            }
        }
    }

    public long getInputDispatchingTimeoutNanos() {
        AppWindowToken appWindowToken = this.mAppToken;
        if (appWindowToken != null) {
            return appWindowToken.mInputDispatchingTimeoutNanos;
        }
        return 8000000000L;
    }

    public boolean hasAppShownWindows() {
        AppWindowToken appWindowToken = this.mAppToken;
        return appWindowToken != null && (appWindowToken.firstWindowDrawn || this.mAppToken.startingDisplayed);
    }

    /* access modifiers changed from: package-private */
    public boolean isIdentityMatrix(float dsdx, float dtdx, float dsdy, float dtdy) {
        if (dsdx < 0.99999f || dsdx > 1.00001f || dtdy < 0.99999f || dtdy > 1.00001f || dtdx < -1.0E-6f || dtdx > 1.0E-6f || dsdy < -1.0E-6f || dsdy > 1.0E-6f) {
            return false;
        }
        return true;
    }

    /* access modifiers changed from: package-private */
    public void prelayout() {
        if (inSizeCompatMode()) {
            this.mGlobalScale = this.mToken.getSizeCompatScale();
            this.mInvGlobalScale = 1.0f / this.mGlobalScale;
            return;
        }
        this.mInvGlobalScale = 1.0f;
        this.mGlobalScale = 1.0f;
    }

    /* access modifiers changed from: package-private */
    public boolean hasContentToDisplay() {
        if (!this.mAppFreezing && isDrawnLw()) {
            if (this.mViewVisibility == 0) {
                return true;
            }
            if (isAnimating() && !getDisplayContent().mAppTransition.isTransitionSet()) {
                return true;
            }
        }
        return super.hasContentToDisplay();
    }

    /* access modifiers changed from: package-private */
    /* JADX WARNING: Code restructure failed: missing block: B:4:0x000c, code lost:
        r0 = r1.mInsetProvider;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean isVisible() {
        /*
            r1 = this;
            boolean r0 = r1.wouldBeVisibleIfPolicyIgnored()
            if (r0 == 0) goto L_0x0018
            boolean r0 = r1.isVisibleByPolicy()
            if (r0 == 0) goto L_0x0018
            com.android.server.wm.InsetsSourceProvider r0 = r1.mInsetProvider
            if (r0 == 0) goto L_0x0016
            boolean r0 = r0.isClientVisible()
            if (r0 == 0) goto L_0x0018
        L_0x0016:
            r0 = 1
            goto L_0x0019
        L_0x0018:
            r0 = 0
        L_0x0019:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.wm.WindowState.isVisible():boolean");
    }

    /* access modifiers changed from: package-private */
    public boolean isVisibleByPolicy() {
        return (this.mPolicyVisibility & 3) == 3;
    }

    /* access modifiers changed from: package-private */
    public void clearPolicyVisibilityFlag(int policyVisibilityFlag) {
        this.mPolicyVisibility &= ~policyVisibilityFlag;
        this.mWmService.scheduleAnimationLocked();
    }

    /* access modifiers changed from: package-private */
    public void setPolicyVisibilityFlag(int policyVisibilityFlag) {
        this.mPolicyVisibility |= policyVisibilityFlag;
        this.mWmService.scheduleAnimationLocked();
    }

    private boolean isLegacyPolicyVisibility() {
        return (this.mPolicyVisibility & 1) != 0;
    }

    /* access modifiers changed from: package-private */
    public boolean wouldBeVisibleIfPolicyIgnored() {
        return this.mHasSurface && !isParentWindowHidden() && !this.mAnimatingExit && !this.mDestroying && (!this.mIsWallpaper || this.mWallpaperVisible);
    }

    public boolean isVisibleLw() {
        return isVisible();
    }

    /* access modifiers changed from: package-private */
    public boolean isWinVisibleLw() {
        AppWindowToken appWindowToken = this.mAppToken;
        return (appWindowToken == null || !appWindowToken.hiddenRequested || this.mAppToken.isSelfAnimating()) && isVisible();
    }

    /* access modifiers changed from: package-private */
    public boolean isVisibleNow() {
        return (!this.mToken.isHidden() || this.mAttrs.type == 3) && isVisible();
    }

    /* access modifiers changed from: package-private */
    public boolean isPotentialDragTarget() {
        return isVisibleNow() && !this.mRemoved && this.mInputChannel != null && this.mInputWindowHandle != null;
    }

    /* access modifiers changed from: package-private */
    public boolean isVisibleOrAdding() {
        AppWindowToken atoken = this.mAppToken;
        return (this.mHasSurface || (!this.mRelayoutCalled && this.mViewVisibility == 0)) && isVisibleByPolicy() && !isParentWindowHidden() && (atoken == null || !atoken.hiddenRequested) && !this.mAnimatingExit && !this.mDestroying;
    }

    /* access modifiers changed from: package-private */
    public boolean isOnScreen() {
        if (!this.mHasSurface || this.mDestroying || !isVisibleByPolicy()) {
            return false;
        }
        AppWindowToken atoken = this.mAppToken;
        if (atoken != null) {
            if ((isParentWindowHidden() || atoken.hiddenRequested) && !isAnimating()) {
                return false;
            }
            return true;
        } else if (!isParentWindowHidden() || isAnimating()) {
            return true;
        } else {
            return false;
        }
    }

    /* access modifiers changed from: package-private */
    public boolean mightAffectAllDrawn() {
        boolean isAppType = this.mWinAnimator.mAttrType == 1 || this.mWinAnimator.mAttrType == 4;
        if ((isOnScreen() || isAppType) && !this.mAnimatingExit && !this.mDestroying) {
            return true;
        }
        return false;
    }

    /* access modifiers changed from: package-private */
    public boolean isInteresting() {
        AppWindowToken appWindowToken = this.mAppToken;
        return appWindowToken != null && !this.mAppDied && (!appWindowToken.isFreezingScreen() || !this.mAppFreezing) && this.mViewVisibility == 0;
    }

    /* access modifiers changed from: package-private */
    public boolean isReadyForDisplay() {
        if (this.mToken.waitingToShow && getDisplayContent().mAppTransition.isTransitionSet()) {
            return false;
        }
        boolean parentAndClientVisible = !isParentWindowHidden() && this.mViewVisibility == 0 && !this.mToken.isHidden();
        if (!this.mHasSurface || !isVisibleByPolicy() || this.mDestroying) {
            return false;
        }
        if (parentAndClientVisible || isAnimating()) {
            return true;
        }
        return false;
    }

    public boolean canAffectSystemUiFlags() {
        if (this.mAttrs.alpha == 0.0f) {
            return false;
        }
        boolean isDummyVisible = MiuiGestureController.isAppDummyVisible(this.mAppToken);
        if (this.mAppToken == null) {
            boolean shown = this.mWinAnimator.getShown();
            boolean exiting = this.mAnimatingExit || this.mDestroying;
            if (!shown || exiting) {
                return false;
            }
            return true;
        }
        Task task = getTask();
        if (!(task != null && task.canAffectSystemUiFlags()) || this.mAppToken.isHidden() || isDummyVisible) {
            return false;
        }
        return true;
    }

    public boolean isDisplayedLw() {
        AppWindowToken atoken = this.mAppToken;
        return isDrawnLw() && isVisibleByPolicy() && ((!isParentWindowHidden() && (atoken == null || !atoken.hiddenRequested)) || isAnimating());
    }

    public boolean isAnimatingLw() {
        return isAnimating();
    }

    public boolean isGoneForLayoutLw() {
        AppWindowToken atoken = this.mAppToken;
        return (this.mViewVisibility == 8 || !this.mRelayoutCalled || ((atoken == null && this.mToken.isHidden()) || ((atoken != null && atoken.hiddenRequested) || isParentWindowGoneForLayout() || ((this.mAnimatingExit && !isAnimatingLw()) || this.mDestroying)))) && (atoken == null || !atoken.mIsCastMode);
    }

    public boolean isDrawFinishedLw() {
        return this.mHasSurface && !this.mDestroying && (this.mWinAnimator.mDrawState == 2 || this.mWinAnimator.mDrawState == 3 || this.mWinAnimator.mDrawState == 4);
    }

    public boolean isDrawnLw() {
        return this.mHasSurface && !this.mDestroying && (this.mWinAnimator.mDrawState == 3 || this.mWinAnimator.mDrawState == 4);
    }

    private boolean isOpaqueDrawn() {
        return ((!this.mIsWallpaper && this.mAttrs.format == -1) || (this.mIsWallpaper && this.mWallpaperVisible)) && isDrawnLw() && !isAnimating();
    }

    /* access modifiers changed from: package-private */
    public void onMovedByResize() {
        this.mMovedByResize = true;
        super.onMovedByResize();
    }

    /* access modifiers changed from: package-private */
    public boolean onAppVisibilityChanged(boolean visible, boolean runningAppAnimation) {
        boolean changed = false;
        for (int i = this.mChildren.size() - 1; i >= 0; i--) {
            changed |= ((WindowState) this.mChildren.get(i)).onAppVisibilityChanged(visible, runningAppAnimation);
        }
        if (this.mAttrs.type == 3) {
            if (!visible && isVisibleNow() && this.mAppToken.isSelfAnimating()) {
                this.mAnimatingExit = true;
                this.mRemoveOnExit = true;
                this.mWindowRemovalAllowed = true;
            }
            return changed;
        }
        boolean isVisibleNow = isVisibleNow();
        if (visible == isVisibleNow) {
            return changed;
        }
        if (!runningAppAnimation && isVisibleNow) {
            AccessibilityController accessibilityController = this.mWmService.mAccessibilityController;
            this.mWinAnimator.applyAnimationLocked(2, false);
            if (accessibilityController != null) {
                accessibilityController.onWindowTransitionLocked(this, 2);
            }
        }
        setDisplayLayoutNeeded();
        return true;
    }

    /* access modifiers changed from: package-private */
    public boolean onSetAppExiting() {
        DisplayContent displayContent = getDisplayContent();
        boolean changed = false;
        if (isVisibleNow()) {
            this.mWinAnimator.applyAnimationLocked(2, false);
            if (this.mWmService.mAccessibilityController != null) {
                this.mWmService.mAccessibilityController.onWindowTransitionLocked(this, 2);
            }
            changed = true;
            if (displayContent != null) {
                displayContent.setLayoutNeeded();
            }
        }
        for (int i = this.mChildren.size() - 1; i >= 0; i--) {
            changed |= ((WindowState) this.mChildren.get(i)).onSetAppExiting();
        }
        return changed;
    }

    /* access modifiers changed from: package-private */
    public void onResize() {
        ArrayList<WindowState> resizingWindows = this.mWmService.mResizingWindows;
        if (this.mHasSurface && !isGoneForLayoutLw() && !resizingWindows.contains(this)) {
            resizingWindows.add(this);
        }
        if (isGoneForLayoutLw()) {
            this.mResizedWhileGone = true;
        }
        super.onResize();
    }

    /* access modifiers changed from: package-private */
    public void onUnfreezeBounds() {
        for (int i = this.mChildren.size() - 1; i >= 0; i--) {
            ((WindowState) this.mChildren.get(i)).onUnfreezeBounds();
        }
        if (this.mHasSurface != 0) {
            this.mLayoutNeeded = true;
            setDisplayLayoutNeeded();
            if (!this.mWmService.mResizingWindows.contains(this)) {
                this.mWmService.mResizingWindows.add(this);
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void handleWindowMovedIfNeeded() {
        if (hasMoved()) {
            int left = this.mWindowFrames.mFrame.left;
            int top = this.mWindowFrames.mFrame.top;
            Task task = getTask();
            boolean adjustedForMinimizedDockOrIme = task != null && (task.mStack.isAdjustedForMinimizedDockedStack() || task.mStack.isAdjustedForIme());
            if (this.mToken.okToAnimate() && (this.mAttrs.privateFlags & 64) == 0 && !isDragResizing() && !adjustedForMinimizedDockOrIme && getWindowConfiguration().hasMovementAnimations() && !this.mWinAnimator.mLastHidden && !this.mSeamlesslyRotated && !inFreeformWindowingMode()) {
                startMoveAnimation(left, top);
            }
            if (this.mWmService.mAccessibilityController != null && getDisplayContent().getDisplayId() == 0) {
                this.mWmService.mAccessibilityController.onSomeWindowResizedOrMovedLocked();
            }
            try {
                this.mClient.moved(left, top);
            } catch (RemoteException e) {
            }
            this.mMovedByResize = false;
        }
    }

    private boolean hasMoved() {
        return this.mHasSurface && (this.mWindowFrames.hasContentChanged() || this.mMovedByResize) && !this.mAnimatingExit && (!(this.mWindowFrames.mFrame.top == this.mWindowFrames.mLastFrame.top && this.mWindowFrames.mFrame.left == this.mWindowFrames.mLastFrame.left) && (!this.mIsChildWindow || !getParentWindow().hasMoved()));
    }

    /* access modifiers changed from: package-private */
    public boolean isObscuringDisplay() {
        Task task = getTask();
        if ((task == null || task.mStack == null || task.mStack.fillsParent()) && isOpaqueDrawn() && fillsDisplay()) {
            return true;
        }
        return false;
    }

    /* access modifiers changed from: package-private */
    public boolean fillsDisplay() {
        DisplayInfo displayInfo = getDisplayInfo();
        return this.mWindowFrames.mFrame.left <= 0 && this.mWindowFrames.mFrame.top <= 0 && this.mWindowFrames.mFrame.right >= displayInfo.appWidth && this.mWindowFrames.mFrame.bottom >= displayInfo.appHeight;
    }

    private boolean matchesDisplayBounds() {
        return getDisplayContent().getBounds().equals(getBounds());
    }

    /* access modifiers changed from: package-private */
    public boolean isLastConfigReportedToClient() {
        return this.mLastConfigReportedToClient;
    }

    /* access modifiers changed from: package-private */
    public void onMergedOverrideConfigurationChanged() {
        super.onMergedOverrideConfigurationChanged();
        this.mLastConfigReportedToClient = false;
    }

    /* access modifiers changed from: package-private */
    public void onWindowReplacementTimeout() {
        if (this.mWillReplaceWindow) {
            removeImmediately();
            return;
        }
        for (int i = this.mChildren.size() - 1; i >= 0; i--) {
            ((WindowState) this.mChildren.get(i)).onWindowReplacementTimeout();
        }
    }

    /* access modifiers changed from: package-private */
    public void forceWindowsScaleableInTransaction(boolean force) {
        WindowStateAnimator windowStateAnimator = this.mWinAnimator;
        if (windowStateAnimator != null && windowStateAnimator.hasSurface()) {
            this.mWinAnimator.mSurfaceController.forceScaleableInTransaction(force);
        }
        super.forceWindowsScaleableInTransaction(force);
    }

    /* access modifiers changed from: package-private */
    public void removeImmediately() {
        super.removeImmediately();
        if (!this.mRemoved) {
            this.mRemoved = true;
            this.mWillReplaceWindow = false;
            WindowState windowState = this.mReplacementWindow;
            if (windowState != null) {
                windowState.mSkipEnterAnimationForSeamlessReplacement = false;
            }
            DisplayContent dc = getDisplayContent();
            if (isInputMethodTarget()) {
                dc.computeImeTarget(true);
            }
            if (WindowManagerService.excludeWindowTypeFromTapOutTask(this.mAttrs.type)) {
                dc.mTapExcludedWindows.remove(this);
            }
            if (this.mTapExcludeRegionHolder != null) {
                dc.mTapExcludeProvidingWindows.remove(this);
            }
            dc.getDisplayPolicy().removeWindowLw(this);
            disposeInputChannel();
            this.mWinAnimator.destroyDeferredSurfaceLocked();
            this.mWinAnimator.destroySurfaceLocked();
            this.mSession.windowRemovedLocked();
            try {
                this.mClient.asBinder().unlinkToDeath(this.mDeathRecipient, 0);
            } catch (RuntimeException e) {
            }
            this.mWmService.postWindowRemoveCleanupLocked(this);
        }
    }

    /* access modifiers changed from: package-private */
    public void removeIfPossible() {
        super.removeIfPossible();
        removeIfPossible(false);
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Code restructure failed: missing block: B:28:0x0066, code lost:
        if (inFreeformWindowingMode() == false) goto L_0x0068;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void removeIfPossible(boolean r11) {
        /*
            r10 = this;
            r0 = 1
            r10.mWindowRemovalAllowed = r0
            android.view.WindowManager$LayoutParams r1 = r10.mAttrs
            int r1 = r1.type
            r2 = 0
            r3 = 3
            if (r1 != r3) goto L_0x000d
            r1 = r0
            goto L_0x000e
        L_0x000d:
            r1 = r2
        L_0x000e:
            long r3 = android.os.Binder.clearCallingIdentity()
            r10.disposeInputChannel()     // Catch:{ all -> 0x00f4 }
            r5 = 0
            int r6 = r10.getDisplayId()     // Catch:{ all -> 0x00f4 }
            boolean r7 = r10.mHasSurface     // Catch:{ all -> 0x00f4 }
            if (r7 == 0) goto L_0x00cd
            com.android.server.wm.WindowToken r7 = r10.mToken     // Catch:{ all -> 0x00f4 }
            boolean r7 = r7.okToAnimate()     // Catch:{ all -> 0x00f4 }
            if (r7 == 0) goto L_0x00cd
            boolean r7 = r10.mWillReplaceWindow     // Catch:{ all -> 0x00f4 }
            if (r7 == 0) goto L_0x0033
            r10.mAnimatingExit = r0     // Catch:{ all -> 0x00f4 }
            r10.mReplacingRemoveRequested = r0     // Catch:{ all -> 0x00f4 }
            android.os.Binder.restoreCallingIdentity(r3)
            return
        L_0x0033:
            boolean r7 = r10.isWinVisibleLw()     // Catch:{ all -> 0x00f4 }
            r5 = r7
            if (r11 == 0) goto L_0x0059
            r10.mAppDied = r0     // Catch:{ all -> 0x00f4 }
            r10.setDisplayLayoutNeeded()     // Catch:{ all -> 0x00f4 }
            com.android.server.wm.WindowManagerService r2 = r10.mWmService     // Catch:{ all -> 0x00f4 }
            com.android.server.wm.WindowSurfacePlacer r2 = r2.mWindowPlacerLocked     // Catch:{ all -> 0x00f4 }
            r2.performSurfacePlacement()     // Catch:{ all -> 0x00f4 }
            r2 = 0
            r10.openInputChannel(r2)     // Catch:{ all -> 0x00f4 }
            com.android.server.wm.DisplayContent r2 = r10.getDisplayContent()     // Catch:{ all -> 0x00f4 }
            com.android.server.wm.InputMonitor r2 = r2.getInputMonitor()     // Catch:{ all -> 0x00f4 }
            r2.updateInputWindowsLw(r0)     // Catch:{ all -> 0x00f4 }
            android.os.Binder.restoreCallingIdentity(r3)
            return
        L_0x0059:
            if (r5 == 0) goto L_0x0087
            if (r1 != 0) goto L_0x005f
            r7 = 2
            goto L_0x0060
        L_0x005f:
            r7 = 5
        L_0x0060:
            if (r1 == 0) goto L_0x0068
            boolean r8 = r10.inFreeformWindowingMode()     // Catch:{ all -> 0x00f4 }
            if (r8 != 0) goto L_0x007a
        L_0x0068:
            com.android.server.wm.WindowStateAnimator r8 = r10.mWinAnimator     // Catch:{ all -> 0x00f4 }
            boolean r8 = r8.applyAnimationLocked(r7, r2)     // Catch:{ all -> 0x00f4 }
            if (r8 == 0) goto L_0x007a
            r10.mAnimatingExit = r0     // Catch:{ all -> 0x00f4 }
            r10.setDisplayLayoutNeeded()     // Catch:{ all -> 0x00f4 }
            com.android.server.wm.WindowManagerService r8 = r10.mWmService     // Catch:{ all -> 0x00f4 }
            r8.requestTraversal()     // Catch:{ all -> 0x00f4 }
        L_0x007a:
            com.android.server.wm.WindowManagerService r8 = r10.mWmService     // Catch:{ all -> 0x00f4 }
            com.android.server.wm.AccessibilityController r8 = r8.mAccessibilityController     // Catch:{ all -> 0x00f4 }
            if (r8 == 0) goto L_0x0087
            com.android.server.wm.WindowManagerService r8 = r10.mWmService     // Catch:{ all -> 0x00f4 }
            com.android.server.wm.AccessibilityController r8 = r8.mAccessibilityController     // Catch:{ all -> 0x00f4 }
            r8.onWindowTransitionLocked(r10, r7)     // Catch:{ all -> 0x00f4 }
        L_0x0087:
            boolean r7 = r10.isAnimating()     // Catch:{ all -> 0x00f4 }
            if (r7 == 0) goto L_0x009b
            com.android.server.wm.AppWindowToken r7 = r10.mAppToken     // Catch:{ all -> 0x00f4 }
            if (r7 == 0) goto L_0x0099
            com.android.server.wm.AppWindowToken r7 = r10.mAppToken     // Catch:{ all -> 0x00f4 }
            boolean r7 = r7.isWaitingForTransitionStart()     // Catch:{ all -> 0x00f4 }
            if (r7 != 0) goto L_0x009b
        L_0x0099:
            r7 = r0
            goto L_0x009c
        L_0x009b:
            r7 = r2
        L_0x009c:
            if (r1 == 0) goto L_0x00ac
            com.android.server.wm.AppWindowToken r8 = r10.mAppToken     // Catch:{ all -> 0x00f4 }
            if (r8 == 0) goto L_0x00ac
            com.android.server.wm.AppWindowToken r8 = r10.mAppToken     // Catch:{ all -> 0x00f4 }
            boolean r8 = r8.isLastWindow(r10)     // Catch:{ all -> 0x00f4 }
            if (r8 == 0) goto L_0x00ac
            r8 = r0
            goto L_0x00ad
        L_0x00ac:
            r8 = r2
        L_0x00ad:
            com.android.server.wm.WindowStateAnimator r9 = r10.mWinAnimator     // Catch:{ all -> 0x00f4 }
            boolean r9 = r9.getShown()     // Catch:{ all -> 0x00f4 }
            if (r9 == 0) goto L_0x00cd
            boolean r9 = r10.mAnimatingExit     // Catch:{ all -> 0x00f4 }
            if (r9 == 0) goto L_0x00cd
            if (r8 == 0) goto L_0x00bd
            if (r7 == 0) goto L_0x00cd
        L_0x00bd:
            r10.setupWindowForRemoveOnExit()     // Catch:{ all -> 0x00f4 }
            com.android.server.wm.AppWindowToken r0 = r10.mAppToken     // Catch:{ all -> 0x00f4 }
            if (r0 == 0) goto L_0x00c9
            com.android.server.wm.AppWindowToken r0 = r10.mAppToken     // Catch:{ all -> 0x00f4 }
            r0.updateReportedVisibilityLocked()     // Catch:{ all -> 0x00f4 }
        L_0x00c9:
            android.os.Binder.restoreCallingIdentity(r3)
            return
        L_0x00cd:
            r10.removeImmediately()     // Catch:{ all -> 0x00f4 }
            if (r5 == 0) goto L_0x00df
            com.android.server.wm.DisplayContent r7 = r10.getDisplayContent()     // Catch:{ all -> 0x00f4 }
            boolean r8 = r7.updateOrientationFromAppTokens()     // Catch:{ all -> 0x00f4 }
            if (r8 == 0) goto L_0x00df
            r7.sendNewConfiguration()     // Catch:{ all -> 0x00f4 }
        L_0x00df:
            com.android.server.wm.WindowManagerService r7 = r10.mWmService     // Catch:{ all -> 0x00f4 }
            boolean r8 = r10.isFocused()     // Catch:{ all -> 0x00f4 }
            if (r8 == 0) goto L_0x00e9
            r2 = 4
            goto L_0x00ea
        L_0x00e9:
        L_0x00ea:
            r7.updateFocusedWindowLocked(r2, r0)     // Catch:{ all -> 0x00f4 }
            android.os.Binder.restoreCallingIdentity(r3)
            return
        L_0x00f4:
            r0 = move-exception
            android.os.Binder.restoreCallingIdentity(r3)
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.wm.WindowState.removeIfPossible(boolean):void");
    }

    private void setupWindowForRemoveOnExit() {
        this.mRemoveOnExit = true;
        setDisplayLayoutNeeded();
        boolean focusChanged = this.mWmService.updateFocusedWindowLocked(3, false);
        this.mWmService.mWindowPlacerLocked.performSurfacePlacement();
        if (focusChanged) {
            getDisplayContent().getInputMonitor().updateInputWindowsLw(false);
        }
    }

    /* access modifiers changed from: package-private */
    public void setHasSurface(boolean hasSurface) {
        this.mHasSurface = hasSurface;
    }

    /* access modifiers changed from: package-private */
    public boolean canBeImeTarget() {
        if (this.mIsImWindow) {
            return false;
        }
        AppWindowToken appWindowToken = this.mAppToken;
        if (!(appWindowToken == null || appWindowToken.windowsAreFocusable())) {
            return false;
        }
        int fl = this.mAttrs.flags & 131080;
        int type = this.mAttrs.type;
        if (fl == 0 || fl == 131080 || type == 3) {
            return isVisibleOrAdding();
        }
        return false;
    }

    private final class DeadWindowEventReceiver extends InputEventReceiver {
        DeadWindowEventReceiver(InputChannel inputChannel) {
            super(inputChannel, WindowState.this.mWmService.mH.getLooper());
        }

        public void onInputEvent(InputEvent event) {
            finishInputEvent(event, true);
        }
    }

    /* access modifiers changed from: package-private */
    public void openInputChannel(InputChannel outInputChannel) {
        if (this.mInputChannel == null) {
            InputChannel[] inputChannels = InputChannel.openInputChannelPair(getName());
            this.mInputChannel = inputChannels[0];
            this.mClientChannel = inputChannels[1];
            this.mInputWindowHandle.token = this.mClient.asBinder();
            if (outInputChannel != null) {
                this.mClientChannel.transferTo(outInputChannel);
                this.mClientChannel.dispose();
                this.mClientChannel = null;
            } else {
                this.mDeadWindowEventReceiver = new DeadWindowEventReceiver(this.mClientChannel);
            }
            this.mWmService.mInputManager.registerInputChannel(this.mInputChannel, this.mClient.asBinder());
            return;
        }
        throw new IllegalStateException("Window already has an input channel.");
    }

    /* access modifiers changed from: package-private */
    public void disposeInputChannel() {
        DeadWindowEventReceiver deadWindowEventReceiver = this.mDeadWindowEventReceiver;
        if (deadWindowEventReceiver != null) {
            deadWindowEventReceiver.dispose();
            this.mDeadWindowEventReceiver = null;
        }
        if (this.mInputChannel != null) {
            this.mWmService.mInputManager.unregisterInputChannel(this.mInputChannel);
            this.mInputChannel.dispose();
            this.mInputChannel = null;
        }
        InputChannel inputChannel = this.mClientChannel;
        if (inputChannel != null) {
            inputChannel.dispose();
            this.mClientChannel = null;
        }
        this.mInputWindowHandle.token = null;
    }

    /* access modifiers changed from: package-private */
    public boolean removeReplacedWindowIfNeeded(WindowState replacement) {
        if (!this.mWillReplaceWindow || this.mReplacementWindow != replacement || !replacement.hasDrawnLw()) {
            for (int i = this.mChildren.size() - 1; i >= 0; i--) {
                if (((WindowState) this.mChildren.get(i)).removeReplacedWindowIfNeeded(replacement)) {
                    return true;
                }
            }
            return false;
        }
        replacement.mSkipEnterAnimationForSeamlessReplacement = false;
        removeReplacedWindow();
        return true;
    }

    private void removeReplacedWindow() {
        this.mWillReplaceWindow = false;
        this.mAnimateReplacingWindow = false;
        this.mReplacingRemoveRequested = false;
        this.mReplacementWindow = null;
        if (this.mAnimatingExit || !this.mAnimateReplacingWindow) {
            removeImmediately();
        }
    }

    /* access modifiers changed from: package-private */
    public boolean setReplacementWindowIfNeeded(WindowState replacementCandidate) {
        boolean replacementSet = false;
        if (this.mWillReplaceWindow && this.mReplacementWindow == null && getWindowTag().toString().equals(replacementCandidate.getWindowTag().toString())) {
            this.mReplacementWindow = replacementCandidate;
            replacementCandidate.mSkipEnterAnimationForSeamlessReplacement = !this.mAnimateReplacingWindow;
            replacementSet = true;
        }
        for (int i = this.mChildren.size() - 1; i >= 0; i--) {
            replacementSet |= ((WindowState) this.mChildren.get(i)).setReplacementWindowIfNeeded(replacementCandidate);
        }
        return replacementSet;
    }

    /* access modifiers changed from: package-private */
    public void setDisplayLayoutNeeded() {
        DisplayContent dc = getDisplayContent();
        if (dc != null) {
            dc.setLayoutNeeded();
        }
    }

    /* access modifiers changed from: package-private */
    public void applyAdjustForImeIfNeeded() {
        Task task = getTask();
        if (task != null && task.mStack != null && task.mStack.isAdjustedForIme()) {
            task.mStack.applyAdjustForImeIfNeeded(task);
        }
    }

    /* access modifiers changed from: package-private */
    public void switchUser() {
        super.switchUser();
        if (isHiddenFromUserLocked()) {
            clearPolicyVisibilityFlag(2);
        } else {
            setPolicyVisibilityFlag(2);
        }
    }

    /* access modifiers changed from: package-private */
    public int getSurfaceTouchableRegion(InputWindowHandle inputWindowHandle, int flags) {
        AppWindowToken appWindowToken;
        int surfaceOffsetX = 0;
        boolean modal = (flags & 40) == 0;
        Region region = inputWindowHandle.touchableRegion;
        setTouchableRegionCropIfNeeded(inputWindowHandle);
        AppWindowToken appWindowToken2 = this.mAppToken;
        Rect appOverrideBounds = appWindowToken2 != null ? appWindowToken2.getResolvedOverrideBounds() : null;
        if (appOverrideBounds == null || appOverrideBounds.isEmpty()) {
            if (modal && (appWindowToken = this.mAppToken) != null) {
                flags |= 32;
                appWindowToken.getLetterboxInnerBounds(this.mTmpRect);
                if (this.mTmpRect.isEmpty()) {
                    Task task = getTask();
                    if (task != null) {
                        task.getDimBounds(this.mTmpRect);
                    } else {
                        getStack().getDimBounds(this.mTmpRect);
                    }
                }
                if (inFreeformWindowingMode()) {
                    int delta = WindowManagerService.dipToPixel(0, getDisplayContent().getDisplayMetrics());
                    this.mTmpRect.inset(-delta, -delta);
                }
                region.set(this.mTmpRect);
                cropRegionToStackBoundsIfNeeded(region);
                subtractTouchExcludeRegionIfNeeded(region);
                if (!hasMoved() && inFreeformWindowingMode()) {
                    WindowStateInjector.adjuestFreeFormTouchRegion(this, region);
                }
            } else if (!modal || this.mTapExcludeRegionHolder == null) {
                getTouchableRegion(region);
            } else {
                Region touchExcludeRegion = Region.obtain();
                amendTapExcludeRegion(touchExcludeRegion);
                if (!touchExcludeRegion.isEmpty()) {
                    flags |= 32;
                    getDisplayContent().getBounds(this.mTmpRect);
                    int dw = this.mTmpRect.width();
                    int dh = this.mTmpRect.height();
                    region.set(-dw, -dh, dw + dw, dh + dh);
                    region.op(touchExcludeRegion, Region.Op.DIFFERENCE);
                    inputWindowHandle.setTouchableRegionCrop((SurfaceControl) null);
                }
                touchExcludeRegion.recycle();
            }
            region.translate(-this.mWindowFrames.mFrame.left, -this.mWindowFrames.mFrame.top);
            return flags;
        }
        if (modal) {
            flags |= 32;
            this.mTmpRect.set(0, 0, appOverrideBounds.width(), appOverrideBounds.height());
        } else {
            this.mTmpRect.set(this.mWindowFrames.mCompatFrame);
        }
        if (this.mAppToken.inSizeCompatMode()) {
            surfaceOffsetX = this.mAppToken.getBounds().left;
        }
        this.mTmpRect.offset(surfaceOffsetX - this.mWindowFrames.mFrame.left, -this.mWindowFrames.mFrame.top);
        region.set(this.mTmpRect);
        return flags;
    }

    /* access modifiers changed from: package-private */
    public void checkPolicyVisibilityChange() {
        boolean isLegacyPolicyVisibility = isLegacyPolicyVisibility();
        boolean z = this.mLegacyPolicyVisibilityAfterAnim;
        if (isLegacyPolicyVisibility != z) {
            if (z) {
                setPolicyVisibilityFlag(1);
            } else {
                clearPolicyVisibilityFlag(1);
            }
            if (!isVisibleByPolicy()) {
                this.mWinAnimator.hide("checkPolicyVisibilityChange");
                if (isFocused()) {
                    this.mWmService.mFocusMayChange = true;
                }
                setDisplayLayoutNeeded();
                this.mWmService.enableScreenIfNeededLocked();
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void setRequestedSize(int requestedWidth, int requestedHeight) {
        if (this.mRequestedWidth != requestedWidth || this.mRequestedHeight != requestedHeight) {
            this.mLayoutNeeded = true;
            this.mRequestedWidth = requestedWidth;
            this.mRequestedHeight = requestedHeight;
        }
    }

    /* access modifiers changed from: package-private */
    public void prepareWindowToDisplayDuringRelayout(boolean wasVisible) {
        if ((this.mAttrs.flags & DumpState.DUMP_COMPILER_STATS) != 0) {
            boolean allowTheaterMode = this.mWmService.mAllowTheaterModeWakeFromLayout || Settings.Global.getInt(this.mWmService.mContext.getContentResolver(), "theater_mode_on", 0) == 0;
            AppWindowToken appWindowToken = this.mAppToken;
            boolean canTurnScreenOn = appWindowToken == null || appWindowToken.canTurnScreenOn();
            if (allowTheaterMode && canTurnScreenOn && !this.mPowerManagerWrapper.isInteractive()) {
                Slog.v("WindowManager", "Relayout window turning screen on: " + this);
                this.mPowerManagerWrapper.wakeUp(SystemClock.uptimeMillis(), 2, "android.server.wm:SCREEN_ON_FLAG");
            }
            AppWindowToken appWindowToken2 = this.mAppToken;
            if (appWindowToken2 != null) {
                appWindowToken2.setCanTurnScreenOn(false);
            }
        }
        if (!wasVisible) {
            if ((this.mAttrs.softInputMode & 240) == 16) {
                this.mLayoutNeeded = true;
            }
            if (isDrawnLw() && this.mToken.okToAnimate()) {
                this.mWinAnimator.applyEnterAnimationLocked();
            }
        }
    }

    private Configuration getProcessGlobalConfiguration() {
        WindowState parentWindow = getParentWindow();
        return this.mWmService.mAtmService.getGlobalConfigurationForPid((parentWindow != null ? parentWindow.mSession : this.mSession).mPid);
    }

    /* access modifiers changed from: package-private */
    public void getMergedConfiguration(MergedConfiguration outConfiguration) {
        outConfiguration.setConfiguration(getProcessGlobalConfiguration(), getMergedOverrideConfiguration());
    }

    /* access modifiers changed from: package-private */
    public void setLastReportedMergedConfiguration(MergedConfiguration config) {
        this.mLastReportedConfiguration.setTo(config);
        this.mLastConfigReportedToClient = true;
    }

    /* access modifiers changed from: package-private */
    public void getLastReportedMergedConfiguration(MergedConfiguration config) {
        config.setTo(this.mLastReportedConfiguration);
    }

    private Configuration getLastReportedConfiguration() {
        return this.mLastReportedConfiguration.getMergedConfiguration();
    }

    /* access modifiers changed from: package-private */
    public void adjustStartingWindowFlags() {
        AppWindowToken appWindowToken;
        if (this.mAttrs.type == 1 && (appWindowToken = this.mAppToken) != null && appWindowToken.startingWindow != null) {
            WindowManager.LayoutParams sa = this.mAppToken.startingWindow.mAttrs;
            sa.flags = (sa.flags & -4718594) | (this.mAttrs.flags & 4718593);
        }
    }

    /* access modifiers changed from: package-private */
    public void setWindowScale(int requestedWidth, int requestedHeight) {
        float f = 1.0f;
        if ((this.mAttrs.flags & 16384) != 0) {
            this.mHScale = this.mAttrs.width != requestedWidth ? ((float) this.mAttrs.width) / ((float) requestedWidth) : 1.0f;
            if (this.mAttrs.height != requestedHeight) {
                f = ((float) this.mAttrs.height) / ((float) requestedHeight);
            }
            this.mVScale = f;
            return;
        }
        this.mVScale = 1.0f;
        this.mHScale = 1.0f;
    }

    private class DeathRecipient implements IBinder.DeathRecipient {
        private DeathRecipient() {
        }

        /* Debug info: failed to restart local var, previous not found, register: 7 */
        public void binderDied() {
            boolean resetSplitScreenResizing = false;
            try {
                synchronized (WindowState.this.mWmService.mGlobalLock) {
                    WindowManagerService.boostPriorityForLockedSection();
                    WindowState win = WindowState.this.mWmService.windowForClientLocked(WindowState.this.mSession, WindowState.this.mClient, false);
                    Slog.i("WindowManager", "WIN DEATH: " + win);
                    if (win != null) {
                        if (win.mAppToken != null && win.mAppToken.mIsCastMode) {
                            win.mAppToken.setCastMode(false);
                        }
                        DisplayContent dc = WindowState.this.getDisplayContent();
                        if (win.mAppToken != null && win.mAppToken.findMainWindow() == win) {
                            WindowState.this.mWmService.mTaskSnapshotController.onAppDied(win.mAppToken);
                        }
                        win.removeIfPossible(WindowState.this.shouldKeepVisibleDeadAppWindow());
                        if (win.mAttrs.type == 2034) {
                            TaskStack stack = dc.getSplitScreenPrimaryStackIgnoringVisibility();
                            if (stack != null) {
                                stack.resetDockedStackToMiddle();
                            }
                            resetSplitScreenResizing = true;
                        }
                    } else if (WindowState.this.mHasSurface) {
                        Slog.e("WindowManager", "!!! LEAK !!! Window removed but surface still valid.");
                        WindowState.this.removeIfPossible();
                    }
                }
                WindowManagerService.resetPriorityAfterLockedSection();
                if (resetSplitScreenResizing) {
                    WindowState.this.mWmService.mActivityTaskManager.setSplitScreenResizing(false);
                }
            } catch (RemoteException e) {
                throw e.rethrowAsRuntimeException();
            } catch (IllegalArgumentException e2) {
            } catch (Throwable th) {
                while (true) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
        }
    }

    /* access modifiers changed from: private */
    public boolean shouldKeepVisibleDeadAppWindow() {
        AppWindowToken appWindowToken;
        if (!isWinVisibleLw() || (appWindowToken = this.mAppToken) == null || appWindowToken.isClientHidden() || this.mAttrs.token != this.mClient.asBinder() || this.mAttrs.type == 3) {
            return false;
        }
        return getWindowConfiguration().keepVisibleDeadAppWindowOnScreen();
    }

    public boolean canReceiveKeys() {
        if ((inFreeformWindowingMode() && this.mWmService.getCurrentFreeFormWindowMode() == 1) || !isVisibleOrAdding() || this.mViewVisibility != 0 || this.mRemoveOnExit || (this.mAttrs.flags & 8) != 0) {
            return false;
        }
        AppWindowToken appWindowToken = this.mAppToken;
        if ((appWindowToken == null || appWindowToken.windowsAreFocusable()) && !cantReceiveTouchInput()) {
            return true;
        }
        return false;
    }

    public boolean canShowWhenLocked() {
        AppWindowToken appWindowToken = this.mAppToken;
        boolean showBecauseOfActivity = appWindowToken != null && appWindowToken.mActivityRecord.canShowWhenLocked();
        boolean showBecauseOfWindow = (getAttrs().flags & DumpState.DUMP_FROZEN) != 0;
        if (showBecauseOfActivity || showBecauseOfWindow) {
            return true;
        }
        return false;
    }

    /* access modifiers changed from: package-private */
    public boolean cantReceiveTouchInput() {
        AppWindowToken appWindowToken = this.mAppToken;
        return (appWindowToken == null || appWindowToken.getTask() == null || (!this.mAppToken.getTask().mStack.shouldIgnoreInput() && !this.mAppToken.hiddenRequested)) ? false : true;
    }

    public boolean hasDrawnLw() {
        return this.mWinAnimator.mDrawState == 4;
    }

    public boolean showLw(boolean doAnimation) {
        return showLw(doAnimation, true);
    }

    /* access modifiers changed from: package-private */
    public boolean showLw(boolean doAnimation, boolean requestAnim) {
        if ((isLegacyPolicyVisibility() && this.mLegacyPolicyVisibilityAfterAnim) || isHiddenFromUserLocked() || !this.mAppOpVisibility || this.mPermanentlyHidden || this.mHiddenWhileSuspended || this.mForceHideNonSystemOverlayWindow) {
            return false;
        }
        if (doAnimation) {
            if (!this.mToken.okToAnimate()) {
                doAnimation = false;
            } else if (isLegacyPolicyVisibility() && !isAnimating()) {
                doAnimation = false;
            }
        }
        setPolicyVisibilityFlag(1);
        this.mLegacyPolicyVisibilityAfterAnim = true;
        if (doAnimation) {
            this.mWinAnimator.applyAnimationLocked(1, true);
        }
        if (requestAnim) {
            this.mWmService.scheduleAnimationLocked();
        }
        if ((this.mAttrs.flags & 8) == 0) {
            this.mWmService.updateFocusedWindowLocked(0, false);
        }
        return true;
    }

    public boolean hideLw(boolean doAnimation) {
        return hideLw(doAnimation, true);
    }

    /* access modifiers changed from: package-private */
    public boolean hideLw(boolean doAnimation, boolean requestAnim) {
        if (doAnimation && !this.mToken.okToAnimate()) {
            doAnimation = false;
        }
        if (!(doAnimation ? this.mLegacyPolicyVisibilityAfterAnim : isLegacyPolicyVisibility())) {
            return false;
        }
        if (doAnimation) {
            this.mWinAnimator.applyAnimationLocked(2, false);
            if (!isAnimating()) {
                doAnimation = false;
            }
        }
        this.mLegacyPolicyVisibilityAfterAnim = false;
        boolean isFocused = isFocused();
        if (!doAnimation) {
            clearPolicyVisibilityFlag(1);
            this.mWmService.enableScreenIfNeededLocked();
            if (isFocused) {
                this.mWmService.mFocusMayChange = true;
            }
        }
        if (requestAnim) {
            this.mWmService.scheduleAnimationLocked();
        }
        if (isFocused) {
            this.mWmService.updateFocusedWindowLocked(0, false);
        }
        return true;
    }

    /* access modifiers changed from: package-private */
    public void setForceHideNonSystemOverlayWindowIfNeeded(boolean forceHide) {
        if (this.mOwnerCanAddInternalSystemWindow) {
            return;
        }
        if ((WindowManager.LayoutParams.isSystemAlertWindowType(this.mAttrs.type) || this.mAttrs.type == 2005) && this.mForceHideNonSystemOverlayWindow != forceHide) {
            this.mForceHideNonSystemOverlayWindow = forceHide;
            if (forceHide) {
                hideLw(true, true);
            } else {
                showLw(true, true);
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void setHiddenWhileSuspended(boolean hide) {
        if (this.mOwnerCanAddInternalSystemWindow) {
            return;
        }
        if ((WindowManager.LayoutParams.isSystemAlertWindowType(this.mAttrs.type) || this.mAttrs.type == 2005) && this.mHiddenWhileSuspended != hide) {
            this.mHiddenWhileSuspended = hide;
            if (hide) {
                hideLw(true, true);
            } else {
                showLw(true, true);
            }
        }
    }

    private void setAppOpVisibilityLw(boolean state) {
        if (this.mAppOpVisibility != state) {
            this.mAppOpVisibility = state;
            if (state) {
                showLw(true, true);
            } else {
                hideLw(true, true);
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void initAppOpsState() {
        int mode;
        if (this.mAppOp != -1 && this.mAppOpVisibility && (mode = this.mWmService.mAppOps.startOpNoThrow(this.mAppOp, getOwningUid(), getOwningPackage(), true)) != 0 && mode != 3) {
            setAppOpVisibilityLw(false);
        }
    }

    /* access modifiers changed from: package-private */
    public void resetAppOpsState() {
        if (this.mAppOp != -1 && this.mAppOpVisibility) {
            this.mWmService.mAppOps.finishOp(this.mAppOp, getOwningUid(), getOwningPackage());
        }
    }

    /* access modifiers changed from: package-private */
    public void updateAppOpsState() {
        if (this.mAppOp != -1) {
            int uid = getOwningUid();
            String packageName = getOwningPackage();
            if (this.mAppOpVisibility) {
                int mode = this.mWmService.mAppOps.checkOpNoThrow(this.mAppOp, uid, packageName);
                if (mode != 0 && mode != 3) {
                    this.mWmService.mAppOps.finishOp(this.mAppOp, uid, packageName);
                    setAppOpVisibilityLw(false);
                    return;
                }
                return;
            }
            int mode2 = this.mWmService.mAppOps.startOpNoThrow(this.mAppOp, uid, packageName, true);
            if (mode2 == 0 || mode2 == 3) {
                setAppOpVisibilityLw(true);
            }
        }
    }

    public void hidePermanentlyLw() {
        if (!this.mPermanentlyHidden) {
            this.mPermanentlyHidden = true;
            hideLw(true, true);
        }
    }

    public void pokeDrawLockLw(long timeout) {
        if (isVisibleOrAdding()) {
            if (this.mDrawLock == null) {
                CharSequence tag = getWindowTag();
                PowerManager powerManager = this.mWmService.mPowerManager;
                this.mDrawLock = powerManager.newWakeLock(128, "Window:" + tag);
                this.mDrawLock.setReferenceCounted(false);
                this.mDrawLock.setWorkSource(new WorkSource(this.mOwnerUid, this.mAttrs.packageName));
            }
            this.mDrawLock.acquire(timeout);
        }
    }

    public boolean isAlive() {
        return this.mClient.asBinder().isBinderAlive();
    }

    /* access modifiers changed from: package-private */
    /* JADX WARNING: Code restructure failed: missing block: B:2:0x0004, code lost:
        r0 = r1.mAppToken;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean isClosing() {
        /*
            r1 = this;
            boolean r0 = r1.mAnimatingExit
            if (r0 != 0) goto L_0x0011
            com.android.server.wm.AppWindowToken r0 = r1.mAppToken
            if (r0 == 0) goto L_0x000f
            boolean r0 = r0.isClosingOrEnteringPip()
            if (r0 == 0) goto L_0x000f
            goto L_0x0011
        L_0x000f:
            r0 = 0
            goto L_0x0012
        L_0x0011:
            r0 = 1
        L_0x0012:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.wm.WindowState.isClosing():boolean");
    }

    /* access modifiers changed from: package-private */
    public void addWinAnimatorToList(ArrayList<WindowStateAnimator> animators) {
        animators.add(this.mWinAnimator);
        for (int i = this.mChildren.size() - 1; i >= 0; i--) {
            ((WindowState) this.mChildren.get(i)).addWinAnimatorToList(animators);
        }
    }

    /* access modifiers changed from: package-private */
    public void sendAppVisibilityToClients() {
        super.sendAppVisibilityToClients();
        boolean clientHidden = this.mAppToken.isClientHidden();
        if (this.mAttrs.type != 3 || !clientHidden) {
            boolean z = true;
            if (clientHidden) {
                for (int i = this.mChildren.size() - 1; i >= 0; i--) {
                    ((WindowState) this.mChildren.get(i)).mWinAnimator.detachChildren();
                }
                this.mWinAnimator.detachChildren();
            }
            try {
                IWindow iWindow = this.mClient;
                if (clientHidden) {
                    z = false;
                }
                iWindow.dispatchAppVisibility(z);
            } catch (RemoteException e) {
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void onStartFreezingScreen() {
        this.mAppFreezing = true;
        for (int i = this.mChildren.size() - 1; i >= 0; i--) {
            ((WindowState) this.mChildren.get(i)).onStartFreezingScreen();
        }
    }

    /* access modifiers changed from: package-private */
    public boolean onStopFreezingScreen() {
        boolean unfrozeWindows = false;
        for (int i = this.mChildren.size() - 1; i >= 0; i--) {
            unfrozeWindows |= ((WindowState) this.mChildren.get(i)).onStopFreezingScreen();
        }
        if (this.mAppFreezing == 0) {
            return unfrozeWindows;
        }
        this.mAppFreezing = false;
        if (this.mHasSurface && !getOrientationChanging() && this.mWmService.mWindowsFreezingScreen != 2) {
            setOrientationChanging(true);
            this.mWmService.mRoot.mOrientationChangeComplete = false;
        }
        this.mLastFreezeDuration = 0;
        setDisplayLayoutNeeded();
        return true;
    }

    /* access modifiers changed from: package-private */
    public boolean destroySurface(boolean cleanupOnResume, boolean appStopped) {
        WindowStateAnimator windowStateAnimator = this.mWinAnimator;
        if (windowStateAnimator != null && windowStateAnimator.mHandleByGesture) {
            if (this.mAttrs.type != 3) {
                return false;
            }
            this.mWinAnimator.mHandleByGesture = false;
        }
        AppWindowToken appWindowToken = this.mAppToken;
        if (appWindowToken != null && appWindowToken.mIsCastMode) {
            return false;
        }
        boolean destroyedSomething = false;
        ArrayList<WindowState> childWindows = new ArrayList<>(this.mChildren);
        for (int i = childWindows.size() - 1; i >= 0; i--) {
            destroyedSomething |= childWindows.get(i).destroySurface(cleanupOnResume, appStopped);
        }
        if (!appStopped && !this.mWindowRemovalAllowed && !cleanupOnResume) {
            return destroyedSomething;
        }
        if (appStopped || this.mWindowRemovalAllowed) {
            this.mWinAnimator.destroyPreservedSurfaceLocked();
        }
        if (this.mDestroying) {
            if (!cleanupOnResume || this.mRemoveOnExit) {
                destroySurfaceUnchecked();
            }
            if (this.mRemoveOnExit) {
                removeImmediately();
            }
            if (cleanupOnResume) {
                requestUpdateWallpaperIfNeeded();
            }
            this.mDestroying = false;
            destroyedSomething = true;
            if (getDisplayContent().mAppTransition.isTransitionSet() && getDisplayContent().mOpeningApps.contains(this.mAppToken)) {
                this.mWmService.mWindowPlacerLocked.requestTraversal();
            }
        }
        return destroyedSomething;
    }

    /* access modifiers changed from: package-private */
    public void destroySurfaceUnchecked() {
        this.mWinAnimator.destroySurfaceLocked();
        this.mAnimatingExit = false;
    }

    public boolean isDefaultDisplay() {
        DisplayContent displayContent = getDisplayContent();
        if (displayContent == null) {
            return false;
        }
        return displayContent.isDefaultDisplay;
    }

    /* access modifiers changed from: package-private */
    public void setShowToOwnerOnlyLocked(boolean showToOwnerOnly) {
        this.mShowToOwnerOnly = showToOwnerOnly;
    }

    private boolean isHiddenFromUserLocked() {
        AppWindowToken appWindowToken;
        WindowState win = getTopParentWindow();
        if ((win.mAttrs.type >= 2000 || (appWindowToken = win.mAppToken) == null || !appWindowToken.mShowForAllUsers || win.getFrameLw().left > win.getDisplayFrameLw().left || win.getFrameLw().top > win.getDisplayFrameLw().top || win.getFrameLw().right < win.getStableFrameLw().right || win.getFrameLw().bottom < win.getStableFrameLw().bottom) && win.mShowToOwnerOnly && !this.mWmService.isCurrentProfileLocked(UserHandle.getUserId(win.mOwnerUid))) {
            return true;
        }
        return false;
    }

    private static void applyInsets(Region outRegion, Rect frame, Rect inset) {
        outRegion.set(frame.left + inset.left, frame.top + inset.top, frame.right - inset.right, frame.bottom - inset.bottom);
    }

    /* access modifiers changed from: package-private */
    public void getTouchableRegion(Region outRegion) {
        Rect frame = this.mWindowFrames.mFrame;
        int i = this.mTouchableInsets;
        if (i == 1) {
            applyInsets(outRegion, frame, this.mGivenContentInsets);
        } else if (i == 2) {
            applyInsets(outRegion, frame, this.mGivenVisibleInsets);
        } else if (i != 3) {
            outRegion.set(frame);
        } else {
            outRegion.set(this.mGivenTouchableRegion);
            outRegion.translate(frame.left, frame.top);
        }
        cropRegionToStackBoundsIfNeeded(outRegion);
        subtractTouchExcludeRegionIfNeeded(outRegion);
        if (!hasMoved() && inFreeformWindowingMode()) {
            WindowStateInjector.adjuestFreeFormTouchRegion(this, outRegion);
        }
    }

    /* access modifiers changed from: package-private */
    public void getEffectiveTouchableRegion(Region outRegion) {
        boolean modal = (this.mAttrs.flags & 40) == 0;
        DisplayContent dc = getDisplayContent();
        if (!modal || dc == null) {
            getTouchableRegion(outRegion);
            return;
        }
        outRegion.set(dc.getBounds());
        cropRegionToStackBoundsIfNeeded(outRegion);
        subtractTouchExcludeRegionIfNeeded(outRegion);
    }

    private void setTouchableRegionCropIfNeeded(InputWindowHandle handle) {
        TaskStack stack;
        Task task = getTask();
        if (task != null && task.cropWindowsToStackBounds() && (stack = task.mStack) != null) {
            handle.setTouchableRegionCrop(stack.getSurfaceControl());
        }
    }

    private void cropRegionToStackBoundsIfNeeded(Region region) {
        TaskStack stack;
        Task task = getTask();
        if (task != null && task.cropWindowsToStackBounds() && (stack = task.mStack) != null) {
            stack.getDimBounds(this.mTmpRect);
            region.op(this.mTmpRect, Region.Op.INTERSECT);
        }
    }

    private void subtractTouchExcludeRegionIfNeeded(Region touchableRegion) {
        if (this.mTapExcludeRegionHolder != null) {
            Region touchExcludeRegion = Region.obtain();
            amendTapExcludeRegion(touchExcludeRegion);
            if (!touchExcludeRegion.isEmpty()) {
                touchableRegion.op(touchExcludeRegion, Region.Op.DIFFERENCE);
            }
            touchExcludeRegion.recycle();
        }
    }

    /* access modifiers changed from: package-private */
    public void reportFocusChangedSerialized(boolean focused, boolean inTouchMode) {
        try {
            this.mClient.windowFocusChanged(focused, inTouchMode);
        } catch (RemoteException e) {
        }
        RemoteCallbackList<IWindowFocusObserver> remoteCallbackList = this.mFocusCallbacks;
        if (remoteCallbackList != null) {
            int N = remoteCallbackList.beginBroadcast();
            for (int i = 0; i < N; i++) {
                IWindowFocusObserver obs = this.mFocusCallbacks.getBroadcastItem(i);
                if (focused) {
                    try {
                        obs.focusGained(this.mWindowId.asBinder());
                    } catch (RemoteException e2) {
                    }
                } else {
                    obs.focusLost(this.mWindowId.asBinder());
                }
            }
            this.mFocusCallbacks.finishBroadcast();
        }
    }

    public void setMiuiProjection(boolean projectioned) {
        if (this.mProjectioned != projectioned) {
            super.setMiuiProjection(projectioned);
            reportIsEnteredProjectionMode(projectioned);
        }
    }

    /* access modifiers changed from: package-private */
    public void reportIsEnteredProjectionMode(boolean projectioned) {
        try {
            this.mClient.notifyProjectionMode(projectioned);
        } catch (RemoteException e) {
        }
    }

    /* access modifiers changed from: package-private */
    public void reportEnteredCastMode(boolean enter) {
        try {
            if (isDimming()) {
                getDimmer().setCastFlags(this, enter);
            }
            this.mClient.notifyCastMode(enter);
        } catch (RemoteException e) {
        }
    }

    /* access modifiers changed from: package-private */
    public void reportRotationChanged(boolean changed) {
        try {
            this.mClient.notifyRotationChanged(changed);
        } catch (RemoteException e) {
        }
    }

    /* access modifiers changed from: package-private */
    public void setDimmerPrivacyFlag(boolean isPrivacy) {
        if (isDimming()) {
            getDimmer().setPrivacyFlags(this, isPrivacy);
        }
    }

    public Configuration getConfiguration() {
        AppWindowToken appWindowToken = this.mAppToken;
        if (appWindowToken != null && appWindowToken.mFrozenMergedConfig.size() > 0) {
            return this.mAppToken.mFrozenMergedConfig.peek();
        }
        if (!registeredForDisplayConfigChanges()) {
            return super.getConfiguration();
        }
        this.mTempConfiguration.setTo(getProcessGlobalConfiguration());
        this.mTempConfiguration.updateFrom(getMergedOverrideConfiguration());
        return this.mTempConfiguration;
    }

    private boolean registeredForDisplayConfigChanges() {
        WindowProcessController app;
        WindowState parentWindow = getParentWindow();
        Session session = parentWindow != null ? parentWindow.mSession : this.mSession;
        if (session.mPid == ActivityManagerService.MY_PID || session.mPid < 0 || (app = this.mWmService.mAtmService.getProcessController(session.mPid, session.mUid)) == null || !app.registeredForDisplayConfigChanges()) {
            return false;
        }
        return true;
    }

    /* access modifiers changed from: package-private */
    public void reportResized() {
        long j;
        WindowState windowState;
        boolean z;
        Trace.traceBegin(32, "wm.reportResized_" + getWindowTag());
        try {
            MergedConfiguration mergedConfiguration = new MergedConfiguration(this.mWmService.mRoot.getConfiguration(), getMergedOverrideConfiguration());
            setLastReportedMergedConfiguration(mergedConfiguration);
            final Rect frame = this.mWindowFrames.mCompatFrame;
            final Rect overscanInsets = this.mWindowFrames.mLastOverscanInsets;
            final Rect contentInsets = this.mWindowFrames.mLastContentInsets;
            final Rect visibleInsets = this.mWindowFrames.mLastVisibleInsets;
            final Rect stableInsets = this.mWindowFrames.mLastStableInsets;
            final Rect outsets = this.mWindowFrames.mLastOutsets;
            final boolean reportDraw = this.mWinAnimator.mDrawState == 1;
            final boolean reportOrientation = this.mReportOrientationChanged;
            final int displayId = getDisplayId();
            final DisplayCutout displayCutout = getWmDisplayCutout().getDisplayCutout();
            if (this.mAttrs.type != 3) {
                try {
                    if (this.mClient instanceof IWindow.Stub) {
                        j = 32;
                        AnonymousClass3 r14 = r1;
                        WindowManagerService.H h = this.mWmService.mH;
                        final MergedConfiguration mergedConfiguration2 = mergedConfiguration;
                        try {
                            AnonymousClass3 r1 = new Runnable() {
                                public void run() {
                                    try {
                                        WindowState.this.dispatchResized(frame, overscanInsets, contentInsets, visibleInsets, stableInsets, outsets, reportDraw, mergedConfiguration2, reportOrientation, displayId, displayCutout);
                                    } catch (RemoteException e) {
                                    }
                                }
                            };
                            h.post(r14);
                            z = false;
                            windowState = this;
                            if (windowState.mWmService.mAccessibilityController != null && (getDisplayId() == 0 || getDisplayContent().getParentWindow() != null)) {
                                windowState.mWmService.mAccessibilityController.onSomeWindowResizedOrMovedLocked();
                            }
                            windowState.mWindowFrames.resetInsetsChanged();
                            windowState.mWinAnimator.mSurfaceResized = z;
                            windowState.mReportOrientationChanged = z;
                        } catch (RemoteException e) {
                            z = false;
                            windowState = this;
                            windowState.setOrientationChanging(z);
                            windowState.mLastFreezeDuration = (int) (SystemClock.elapsedRealtime() - windowState.mWmService.mDisplayFreezeTime);
                            Slog.w("WindowManager", "Failed to report 'resized' to the client of " + windowState + ", removing this window.");
                            windowState.mWmService.mPendingRemove.add(windowState);
                            windowState.mWmService.mWindowPlacerLocked.requestTraversal();
                            Trace.traceEnd(j);
                        }
                        Trace.traceEnd(j);
                    }
                } catch (RemoteException e2) {
                    j = 32;
                    z = false;
                    windowState = this;
                    windowState.setOrientationChanging(z);
                    windowState.mLastFreezeDuration = (int) (SystemClock.elapsedRealtime() - windowState.mWmService.mDisplayFreezeTime);
                    Slog.w("WindowManager", "Failed to report 'resized' to the client of " + windowState + ", removing this window.");
                    windowState.mWmService.mPendingRemove.add(windowState);
                    windowState.mWmService.mWindowPlacerLocked.requestTraversal();
                    Trace.traceEnd(j);
                }
            }
            j = 32;
            z = false;
            windowState = this;
            try {
                dispatchResized(frame, overscanInsets, contentInsets, visibleInsets, stableInsets, outsets, reportDraw, mergedConfiguration, reportOrientation, displayId, displayCutout);
                windowState.mWmService.mAccessibilityController.onSomeWindowResizedOrMovedLocked();
                windowState.mWindowFrames.resetInsetsChanged();
                windowState.mWinAnimator.mSurfaceResized = z;
                windowState.mReportOrientationChanged = z;
            } catch (RemoteException e3) {
                windowState.setOrientationChanging(z);
                windowState.mLastFreezeDuration = (int) (SystemClock.elapsedRealtime() - windowState.mWmService.mDisplayFreezeTime);
                Slog.w("WindowManager", "Failed to report 'resized' to the client of " + windowState + ", removing this window.");
                windowState.mWmService.mPendingRemove.add(windowState);
                windowState.mWmService.mWindowPlacerLocked.requestTraversal();
                Trace.traceEnd(j);
            }
        } catch (RemoteException e4) {
            j = 32;
            windowState = this;
            z = false;
            windowState.setOrientationChanging(z);
            windowState.mLastFreezeDuration = (int) (SystemClock.elapsedRealtime() - windowState.mWmService.mDisplayFreezeTime);
            Slog.w("WindowManager", "Failed to report 'resized' to the client of " + windowState + ", removing this window.");
            windowState.mWmService.mPendingRemove.add(windowState);
            windowState.mWmService.mWindowPlacerLocked.requestTraversal();
            Trace.traceEnd(j);
        }
        Trace.traceEnd(j);
    }

    /* access modifiers changed from: package-private */
    public void notifyInsetsChanged() {
        try {
            this.mClient.insetsChanged(getDisplayContent().getInsetsStateController().getInsetsForDispatch(this));
        } catch (RemoteException e) {
            Slog.w("WindowManager", "Failed to deliver inset state change", e);
        }
    }

    /* access modifiers changed from: package-private */
    public void notifyInsetsControlChanged() {
        InsetsStateController stateController = getDisplayContent().getInsetsStateController();
        try {
            this.mClient.insetsControlChanged(stateController.getInsetsForDispatch(this), stateController.getControlsForDispatch(this));
        } catch (RemoteException e) {
            Slog.w("WindowManager", "Failed to deliver inset state change", e);
        }
    }

    /* access modifiers changed from: package-private */
    public Rect getBackdropFrame(Rect frame) {
        boolean resizing = isDragResizing() || isDragResizeChanged();
        if (getWindowConfiguration().useWindowFrameForBackdrop() || !resizing) {
            this.mTmpRect.set(frame);
            this.mTmpRect.offsetTo(0, 0);
            return this.mTmpRect;
        }
        DisplayInfo displayInfo = getDisplayInfo();
        this.mTmpRect.set(0, 0, displayInfo.logicalWidth, displayInfo.logicalHeight);
        return this.mTmpRect;
    }

    private int getStackId() {
        TaskStack stack = getStack();
        if (stack == null) {
            return -1;
        }
        return stack.mStackId;
    }

    /* access modifiers changed from: private */
    public void dispatchResized(Rect frame, Rect overscanInsets, Rect contentInsets, Rect visibleInsets, Rect stableInsets, Rect outsets, boolean reportDraw, MergedConfiguration mergedConfiguration, boolean reportOrientation, int displayId, DisplayCutout displayCutout) throws RemoteException {
        this.mClient.resized(frame, overscanInsets, contentInsets, visibleInsets, stableInsets, outsets, reportDraw, mergedConfiguration, getBackdropFrame(frame), isDragResizeChanged() || reportOrientation, getDisplayContent().getDisplayPolicy().areSystemBarsForcedShownLw(this), displayId, new DisplayCutout.ParcelableWrapper(displayCutout));
        this.mDragResizingChangeReported = true;
    }

    public void registerFocusObserver(IWindowFocusObserver observer) {
        synchronized (this.mWmService.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                if (this.mFocusCallbacks == null) {
                    this.mFocusCallbacks = new RemoteCallbackList<>();
                }
                this.mFocusCallbacks.register(observer);
            } catch (Throwable th) {
                while (true) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
        }
        WindowManagerService.resetPriorityAfterLockedSection();
    }

    public void unregisterFocusObserver(IWindowFocusObserver observer) {
        synchronized (this.mWmService.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                if (this.mFocusCallbacks != null) {
                    this.mFocusCallbacks.unregister(observer);
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

    /* access modifiers changed from: package-private */
    public boolean isFocused() {
        return getDisplayContent().mCurrentFocus == this;
    }

    private boolean inAppWindowThatMatchesParentBounds() {
        AppWindowToken appWindowToken = this.mAppToken;
        return appWindowToken == null || (appWindowToken.matchParentBounds() && !inMultiWindowMode());
    }

    /* access modifiers changed from: package-private */
    public boolean isLetterboxedAppWindow() {
        return (!inMultiWindowMode() && !matchesDisplayBounds()) || isLetterboxedForDisplayCutoutLw();
    }

    public boolean isLetterboxedForDisplayCutoutLw() {
        if (this.mAppToken != null && this.mWindowFrames.parentFrameWasClippedByDisplayCutout() && this.mAttrs.layoutInDisplayCutoutMode != 1 && this.mAttrs.isFullscreen()) {
            return !frameCoversEntireAppTokenBounds();
        }
        return false;
    }

    private boolean frameCoversEntireAppTokenBounds() {
        this.mTmpRect.set(this.mAppToken.getBounds());
        this.mTmpRect.intersectUnchecked(this.mWindowFrames.mFrame);
        return this.mAppToken.getBounds().equals(this.mTmpRect);
    }

    public boolean isLetterboxedOverlappingWith(Rect rect) {
        AppWindowToken appWindowToken = this.mAppToken;
        return appWindowToken != null && appWindowToken.isLetterboxOverlappingWith(rect);
    }

    /* access modifiers changed from: package-private */
    public boolean isDragResizeChanged() {
        return this.mDragResizing != computeDragResizing();
    }

    /* access modifiers changed from: package-private */
    public void setWaitingForDrawnIfResizingChanged() {
        if (isDragResizeChanged()) {
            this.mWmService.mWaitingForDrawn.add(this);
        }
        super.setWaitingForDrawnIfResizingChanged();
    }

    private boolean isDragResizingChangeReported() {
        return this.mDragResizingChangeReported;
    }

    /* access modifiers changed from: package-private */
    public void resetDragResizingChangeReported() {
        this.mDragResizingChangeReported = false;
        super.resetDragResizingChangeReported();
    }

    /* access modifiers changed from: package-private */
    public int getResizeMode() {
        return this.mResizeMode;
    }

    private boolean computeDragResizing() {
        AppWindowToken appWindowToken;
        Task task = getTask();
        if (task == null) {
            return false;
        }
        if ((!inSplitScreenWindowingMode() && !inFreeformWindowingMode()) || this.mAttrs.width != -1 || this.mAttrs.height != -1) {
            return false;
        }
        if (task.isDragResizing()) {
            return true;
        }
        if ((getDisplayContent().mDividerControllerLocked.isResizing() || ((appWindowToken = this.mAppToken) != null && !appWindowToken.mFrozenBounds.isEmpty())) && !task.inFreeformWindowingMode() && !isGoneForLayoutLw()) {
            return true;
        }
        return false;
    }

    /* access modifiers changed from: package-private */
    public void setDragResizing() {
        int i;
        boolean resizing = computeDragResizing();
        if (resizing != this.mDragResizing) {
            this.mDragResizing = resizing;
            Task task = getTask();
            if (task == null || !task.isDragResizing()) {
                if (!this.mDragResizing || !getDisplayContent().mDividerControllerLocked.isResizing()) {
                    i = 0;
                } else {
                    i = 1;
                }
                this.mResizeMode = i;
                return;
            }
            this.mResizeMode = task.getDragResizeMode();
        }
    }

    /* access modifiers changed from: package-private */
    public boolean isDragResizing() {
        return this.mDragResizing;
    }

    /* access modifiers changed from: package-private */
    public boolean isDockedResizing() {
        if (this.mDragResizing && getResizeMode() == 1) {
            return true;
        }
        if (!isChildWindow() || !getParentWindow().isDockedResizing()) {
            return false;
        }
        return true;
    }

    public void writeToProto(ProtoOutputStream proto, long fieldId, int logLevel) {
        boolean isVisible = isVisible();
        if (logLevel != 2 || isVisible) {
            long token = proto.start(fieldId);
            super.writeToProto(proto, 1146756268033L, logLevel);
            writeIdentifierToProto(proto, 1146756268034L);
            proto.write(1120986464259L, getDisplayId());
            proto.write(1120986464260L, getStackId());
            this.mAttrs.writeToProto(proto, 1146756268037L);
            this.mGivenContentInsets.writeToProto(proto, 1146756268038L);
            this.mWindowFrames.writeToProto(proto, 1146756268073L);
            this.mAttrs.surfaceInsets.writeToProto(proto, 1146756268044L);
            this.mSurfacePosition.writeToProto(proto, 1146756268048L);
            this.mWinAnimator.writeToProto(proto, 1146756268045L);
            proto.write(1133871366158L, this.mAnimatingExit);
            for (int i = 0; i < this.mChildren.size(); i++) {
                ((WindowState) this.mChildren.get(i)).writeToProto(proto, 2246267895823L, logLevel);
            }
            proto.write(1120986464274L, this.mRequestedWidth);
            proto.write(1120986464275L, this.mRequestedHeight);
            proto.write(1120986464276L, this.mViewVisibility);
            proto.write(1120986464277L, this.mSystemUiVisibility);
            proto.write(1133871366166L, this.mHasSurface);
            proto.write(1133871366167L, isReadyForDisplay());
            proto.write(1133871366178L, this.mRemoveOnExit);
            proto.write(1133871366179L, this.mDestroying);
            proto.write(1133871366180L, this.mRemoved);
            proto.write(1133871366181L, isOnScreen());
            proto.write(1133871366182L, isVisible);
            proto.write(1133871366183L, this.mPendingSeamlessRotate != null);
            proto.write(1112396529704L, this.mFinishSeamlessRotateFrameNumber);
            proto.write(1133871366186L, this.mForceSeamlesslyRotate);
            proto.end(token);
        }
    }

    public void writeIdentifierToProto(ProtoOutputStream proto, long fieldId) {
        long token = proto.start(fieldId);
        proto.write(1120986464257L, System.identityHashCode(this));
        proto.write(1120986464258L, UserHandle.getUserId(this.mOwnerUid));
        CharSequence title = getWindowTag();
        if (title != null) {
            proto.write(1138166333443L, title.toString());
        }
        proto.end(token);
    }

    /* access modifiers changed from: package-private */
    public void dump(PrintWriter pw, String prefix, boolean dumpAll) {
        TaskStack stack = getStack();
        pw.print(prefix + "mDisplayId=" + getDisplayId());
        if (stack != null) {
            pw.print(" stackId=" + stack.mStackId);
        }
        pw.println(" mSession=" + this.mSession + " mClient=" + this.mClient.asBinder());
        pw.println(prefix + "mOwnerUid=" + this.mOwnerUid + " mShowToOwnerOnly=" + this.mShowToOwnerOnly + " package=" + this.mAttrs.packageName + " appop=" + AppOpsManager.opToName(this.mAppOp));
        StringBuilder sb = new StringBuilder();
        sb.append(prefix);
        sb.append("mAttrs=");
        sb.append(this.mAttrs.toString(prefix));
        pw.println(sb.toString());
        pw.println(prefix + "Requested w=" + this.mRequestedWidth + " h=" + this.mRequestedHeight + " mLayoutSeq=" + this.mLayoutSeq);
        if (!(this.mRequestedWidth == this.mLastRequestedWidth && this.mRequestedHeight == this.mLastRequestedHeight)) {
            pw.println(prefix + "LastRequested w=" + this.mLastRequestedWidth + " h=" + this.mLastRequestedHeight);
        }
        if (this.mIsChildWindow || this.mLayoutAttached) {
            pw.println(prefix + "mParentWindow=" + getParentWindow() + " mLayoutAttached=" + this.mLayoutAttached);
        }
        if (this.mIsImWindow || this.mIsWallpaper || this.mIsFloatingLayer) {
            pw.println(prefix + "mIsImWindow=" + this.mIsImWindow + " mIsWallpaper=" + this.mIsWallpaper + " mIsFloatingLayer=" + this.mIsFloatingLayer + " mWallpaperVisible=" + this.mWallpaperVisible);
        }
        if (dumpAll) {
            pw.print(prefix);
            pw.print("mBaseLayer=");
            pw.print(this.mBaseLayer);
            pw.print(" mSubLayer=");
            pw.print(this.mSubLayer);
        }
        if (dumpAll) {
            pw.println(prefix + "mToken=" + this.mToken);
            if (this.mAppToken != null) {
                pw.println(prefix + "mAppToken=" + this.mAppToken);
                pw.print(prefix + "mAppDied=" + this.mAppDied);
                pw.print(prefix + "drawnStateEvaluated=" + getDrawnStateEvaluated());
                pw.println(prefix + "mightAffectAllDrawn=" + mightAffectAllDrawn());
            }
            pw.println(prefix + "mViewVisibility=0x" + Integer.toHexString(this.mViewVisibility) + " mHaveFrame=" + this.mHaveFrame + " mObscured=" + this.mObscured);
            StringBuilder sb2 = new StringBuilder();
            sb2.append(prefix);
            sb2.append("mSeq=");
            sb2.append(this.mSeq);
            sb2.append(" mSystemUiVisibility=0x");
            sb2.append(Integer.toHexString(this.mSystemUiVisibility));
            pw.println(sb2.toString());
        }
        if (!isVisibleByPolicy() || !this.mLegacyPolicyVisibilityAfterAnim || !this.mAppOpVisibility || isParentWindowHidden() || this.mPermanentlyHidden || this.mForceHideNonSystemOverlayWindow || this.mHiddenWhileSuspended) {
            pw.println(prefix + "mPolicyVisibility=" + isVisibleByPolicy() + " mLegacyPolicyVisibilityAfterAnim=" + this.mLegacyPolicyVisibilityAfterAnim + " mAppOpVisibility=" + this.mAppOpVisibility + " parentHidden=" + isParentWindowHidden() + " mPermanentlyHidden=" + this.mPermanentlyHidden + " mHiddenWhileSuspended=" + this.mHiddenWhileSuspended + " mForceHideNonSystemOverlayWindow=" + this.mForceHideNonSystemOverlayWindow);
        }
        if (!this.mRelayoutCalled || this.mLayoutNeeded) {
            pw.println(prefix + "mRelayoutCalled=" + this.mRelayoutCalled + " mLayoutNeeded=" + this.mLayoutNeeded);
        }
        if (dumpAll) {
            pw.println(prefix + "mGivenContentInsets=" + this.mGivenContentInsets.toShortString(sTmpSB) + " mGivenVisibleInsets=" + this.mGivenVisibleInsets.toShortString(sTmpSB));
            if (this.mTouchableInsets != 0 || this.mGivenInsetsPending) {
                pw.println(prefix + "mTouchableInsets=" + this.mTouchableInsets + " mGivenInsetsPending=" + this.mGivenInsetsPending);
                Region region = new Region();
                getTouchableRegion(region);
                pw.println(prefix + "touchable region=" + region);
            }
            pw.println(prefix + "mFullConfiguration=" + getConfiguration());
            pw.println(prefix + "mLastReportedConfiguration=" + getLastReportedConfiguration());
        }
        pw.println(prefix + "mHasSurface=" + this.mHasSurface + " isReadyForDisplay()=" + isReadyForDisplay() + " mWindowRemovalAllowed=" + this.mWindowRemovalAllowed);
        if (inSizeCompatMode()) {
            pw.println(prefix + "mCompatFrame=" + this.mWindowFrames.mCompatFrame.toShortString(sTmpSB));
        }
        if (dumpAll) {
            this.mWindowFrames.dump(pw, prefix);
            pw.println(prefix + " surface=" + this.mAttrs.surfaceInsets.toShortString(sTmpSB));
        }
        super.dump(pw, prefix, dumpAll);
        pw.println(prefix + this.mWinAnimator + ":");
        WindowStateAnimator windowStateAnimator = this.mWinAnimator;
        windowStateAnimator.dump(pw, prefix + "  ", dumpAll);
        if (this.mAnimatingExit || this.mRemoveOnExit || this.mDestroying || this.mRemoved) {
            pw.println(prefix + "mAnimatingExit=" + this.mAnimatingExit + " mRemoveOnExit=" + this.mRemoveOnExit + " mDestroying=" + this.mDestroying + " mRemoved=" + this.mRemoved);
        }
        if (getOrientationChanging() || this.mAppFreezing || this.mReportOrientationChanged) {
            StringBuilder sb3 = new StringBuilder();
            sb3.append(prefix);
            sb3.append("mOrientationChanging=");
            sb3.append(this.mOrientationChanging);
            sb3.append(" configOrientationChanging=");
            sb3.append(getLastReportedConfiguration().orientation != getConfiguration().orientation);
            sb3.append(" mAppFreezing=");
            sb3.append(this.mAppFreezing);
            sb3.append(" mReportOrientationChanged=");
            sb3.append(this.mReportOrientationChanged);
            pw.println(sb3.toString());
        }
        if (this.mLastFreezeDuration != 0) {
            pw.print(prefix + "mLastFreezeDuration=");
            TimeUtils.formatDuration((long) this.mLastFreezeDuration, pw);
            pw.println();
        }
        pw.print(prefix + "mForceSeamlesslyRotate=" + this.mForceSeamlesslyRotate + " seamlesslyRotate: pending=");
        SeamlessRotator seamlessRotator = this.mPendingSeamlessRotate;
        if (seamlessRotator != null) {
            seamlessRotator.dump(pw);
        } else {
            pw.print("null");
        }
        pw.println(" finishedFrameNumber=" + this.mFinishSeamlessRotateFrameNumber);
        if (!(this.mHScale == 1.0f && this.mVScale == 1.0f)) {
            pw.println(prefix + "mHScale=" + this.mHScale + " mVScale=" + this.mVScale);
        }
        if (!(this.mWallpaperX == -1.0f && this.mWallpaperY == -1.0f)) {
            pw.println(prefix + "mWallpaperX=" + this.mWallpaperX + " mWallpaperY=" + this.mWallpaperY);
        }
        if (!(this.mWallpaperXStep == -1.0f && this.mWallpaperYStep == -1.0f)) {
            pw.println(prefix + "mWallpaperXStep=" + this.mWallpaperXStep + " mWallpaperYStep=" + this.mWallpaperYStep);
        }
        if (!(this.mWallpaperDisplayOffsetX == Integer.MIN_VALUE && this.mWallpaperDisplayOffsetY == Integer.MIN_VALUE)) {
            pw.println(prefix + "mWallpaperDisplayOffsetX=" + this.mWallpaperDisplayOffsetX + " mWallpaperDisplayOffsetY=" + this.mWallpaperDisplayOffsetY);
        }
        if (this.mDrawLock != null) {
            pw.println(prefix + "mDrawLock=" + this.mDrawLock);
        }
        if (isDragResizing()) {
            pw.println(prefix + "isDragResizing=" + isDragResizing());
        }
        if (computeDragResizing()) {
            pw.println(prefix + "computeDragResizing=" + computeDragResizing());
        }
        pw.println(prefix + "isOnScreen=" + isOnScreen());
        pw.println(prefix + "isVisible=" + isVisible());
        if (this.mAppToken != null) {
            pw.println(prefix + "mIgnoreInput=" + this.mAppToken.mIgnoreInput);
        }
        pw.print(prefix);
        pw.println("mMiuiNotFocusable=" + this.mMiuiNotFocusable);
        pw.print(prefix);
        pw.println("mMiuiNotTouchModal=" + this.mMiuiNotTouchModal);
        pw.print(prefix);
        pw.println("mHasSurfaceView=" + this.mHasSurfaceView);
    }

    /* access modifiers changed from: package-private */
    public String getName() {
        return Integer.toHexString(System.identityHashCode(this)) + " " + getWindowTag();
    }

    /* access modifiers changed from: package-private */
    public CharSequence getWindowTag() {
        CharSequence tag = this.mAttrs.getTitle();
        if (tag == null || tag.length() <= 0) {
            return this.mAttrs.packageName;
        }
        return tag;
    }

    public String toString() {
        CharSequence title = getWindowTag();
        if (!(this.mStringNameCache != null && this.mLastTitle == title && this.mWasExiting == this.mAnimatingExit)) {
            this.mLastTitle = title;
            this.mWasExiting = this.mAnimatingExit;
            StringBuilder sb = new StringBuilder();
            sb.append("Window{");
            sb.append(Integer.toHexString(System.identityHashCode(this)));
            sb.append(" u");
            sb.append(UserHandle.getUserId(this.mOwnerUid));
            sb.append(" ");
            sb.append(this.mLastTitle);
            sb.append(this.mAnimatingExit ? " EXITING}" : "}");
            this.mStringNameCache = sb.toString();
        }
        return this.mStringNameCache;
    }

    /* access modifiers changed from: package-private */
    public void transformClipRectFromScreenToSurfaceSpace(Rect clipRect) {
        if (this.mHScale != 1.0f || this.mVScale != 1.0f) {
            if (this.mHScale >= 0.0f) {
                clipRect.left = (int) (((float) clipRect.left) / this.mHScale);
                clipRect.right = (int) Math.ceil((double) (((float) clipRect.right) / this.mHScale));
            }
            if (this.mVScale >= 0.0f) {
                clipRect.top = (int) (((float) clipRect.top) / this.mVScale);
                clipRect.bottom = (int) Math.ceil((double) (((float) clipRect.bottom) / this.mVScale));
            }
        }
    }

    private void applyGravityAndUpdateFrame(Rect containingFrame, Rect displayFrame) {
        int h;
        int w;
        float y;
        float x;
        int w2;
        int pw = containingFrame.width();
        int ph = containingFrame.height();
        Task task = getTask();
        boolean fitToDisplay = true;
        boolean inNonFullscreenContainer = !inAppWindowThatMatchesParentBounds();
        boolean noLimits = (this.mAttrs.flags & 512) != 0;
        if (task != null && inNonFullscreenContainer && (this.mAttrs.type == 1 || noLimits)) {
            fitToDisplay = false;
        }
        boolean inSizeCompatMode = inSizeCompatMode();
        if ((this.mAttrs.flags & 16384) != 0) {
            if (this.mAttrs.width < 0) {
                w = pw;
            } else if (inSizeCompatMode) {
                w = (int) ((((float) this.mAttrs.width) * this.mGlobalScale) + 0.5f);
            } else {
                w = this.mAttrs.width;
            }
            if (this.mAttrs.height < 0) {
                h = ph;
            } else if (inSizeCompatMode) {
                h = (int) ((((float) this.mAttrs.height) * this.mGlobalScale) + 0.5f);
            } else {
                h = this.mAttrs.height;
            }
        } else {
            if (this.mAttrs.width == -1) {
                w2 = pw;
            } else if (inSizeCompatMode) {
                w2 = (int) ((((float) this.mRequestedWidth) * this.mGlobalScale) + 0.5f);
            } else {
                w2 = this.mRequestedWidth;
            }
            if (this.mAttrs.height == -1) {
                h = ph;
            } else if (inSizeCompatMode) {
                h = (int) ((((float) this.mRequestedHeight) * this.mGlobalScale) + 0.5f);
            } else {
                h = this.mRequestedHeight;
            }
        }
        if (inSizeCompatMode) {
            x = ((float) this.mAttrs.x) * this.mGlobalScale;
            y = ((float) this.mAttrs.y) * this.mGlobalScale;
        } else {
            x = (float) this.mAttrs.x;
            y = (float) this.mAttrs.y;
        }
        if (inNonFullscreenContainer && !layoutInParentFrame()) {
            w = Math.min(w, pw);
            h = Math.min(h, ph);
        }
        Gravity.apply(this.mAttrs.gravity, w, h, containingFrame, (int) ((this.mAttrs.horizontalMargin * ((float) pw)) + x), (int) ((this.mAttrs.verticalMargin * ((float) ph)) + y), this.mWindowFrames.mFrame);
        if (fitToDisplay) {
            Gravity.applyDisplay(this.mAttrs.gravity, displayFrame, this.mWindowFrames.mFrame);
        } else {
            Rect rect = displayFrame;
        }
        this.mWindowFrames.mCompatFrame.set(this.mWindowFrames.mFrame);
        if (inSizeCompatMode) {
            this.mWindowFrames.mCompatFrame.scale(this.mInvGlobalScale);
        }
    }

    /* access modifiers changed from: package-private */
    public boolean isChildWindow() {
        return this.mIsChildWindow;
    }

    /* access modifiers changed from: package-private */
    public boolean layoutInParentFrame() {
        return this.mIsChildWindow && (this.mAttrs.privateFlags & 65536) != 0;
    }

    /* access modifiers changed from: package-private */
    public boolean hideNonSystemOverlayWindowsWhenVisible() {
        return (this.mAttrs.privateFlags & DumpState.DUMP_FROZEN) != 0 && this.mSession.mCanHideNonSystemOverlayWindows;
    }

    /* access modifiers changed from: package-private */
    public WindowState getParentWindow() {
        if (this.mIsChildWindow) {
            return (WindowState) super.getParent();
        }
        return null;
    }

    /* access modifiers changed from: package-private */
    public WindowState getTopParentWindow() {
        WindowState current = this;
        WindowState topParent = current;
        while (current != null && current.mIsChildWindow) {
            current = current.getParentWindow();
            if (current != null) {
                topParent = current;
            }
        }
        return topParent;
    }

    /* access modifiers changed from: package-private */
    public boolean isParentWindowHidden() {
        WindowState parent = getParentWindow();
        return parent != null && parent.mHidden;
    }

    private boolean isParentWindowGoneForLayout() {
        WindowState parent = getParentWindow();
        return parent != null && parent.isGoneForLayoutLw();
    }

    /* access modifiers changed from: package-private */
    public void setWillReplaceWindow(boolean animate) {
        for (int i = this.mChildren.size() - 1; i >= 0; i--) {
            ((WindowState) this.mChildren.get(i)).setWillReplaceWindow(animate);
        }
        if ((this.mAttrs.privateFlags & 32768) == 0 && this.mAttrs.type != 3) {
            this.mWillReplaceWindow = true;
            this.mReplacementWindow = null;
            this.mAnimateReplacingWindow = animate;
        }
    }

    /* access modifiers changed from: package-private */
    public void clearWillReplaceWindow() {
        this.mWillReplaceWindow = false;
        this.mReplacementWindow = null;
        this.mAnimateReplacingWindow = false;
        for (int i = this.mChildren.size() - 1; i >= 0; i--) {
            ((WindowState) this.mChildren.get(i)).clearWillReplaceWindow();
        }
    }

    /* access modifiers changed from: package-private */
    public boolean waitingForReplacement() {
        if (this.mWillReplaceWindow) {
            return true;
        }
        for (int i = this.mChildren.size() - 1; i >= 0; i--) {
            if (((WindowState) this.mChildren.get(i)).waitingForReplacement()) {
                return true;
            }
        }
        return false;
    }

    /* access modifiers changed from: package-private */
    public void requestUpdateWallpaperIfNeeded() {
        DisplayContent dc = getDisplayContent();
        if (!(dc == null || (this.mAttrs.flags & DumpState.DUMP_DEXOPT) == 0)) {
            dc.pendingLayoutChanges |= 4;
            dc.setLayoutNeeded();
            this.mWmService.mWindowPlacerLocked.requestTraversal();
        }
        for (int i = this.mChildren.size() - 1; i >= 0; i--) {
            ((WindowState) this.mChildren.get(i)).requestUpdateWallpaperIfNeeded();
        }
    }

    /* access modifiers changed from: package-private */
    public float translateToWindowX(float x) {
        float winX = x - ((float) this.mWindowFrames.mFrame.left);
        if (inSizeCompatMode()) {
            return winX * this.mGlobalScale;
        }
        return winX;
    }

    /* access modifiers changed from: package-private */
    public float translateToWindowY(float y) {
        float winY = y - ((float) this.mWindowFrames.mFrame.top);
        if (inSizeCompatMode()) {
            return winY * this.mGlobalScale;
        }
        return winY;
    }

    /* access modifiers changed from: package-private */
    public boolean shouldBeReplacedWithChildren() {
        return this.mIsChildWindow || this.mAttrs.type == 2 || this.mAttrs.type == 4;
    }

    /* access modifiers changed from: package-private */
    public void setWillReplaceChildWindows() {
        if (shouldBeReplacedWithChildren()) {
            setWillReplaceWindow(false);
        }
        for (int i = this.mChildren.size() - 1; i >= 0; i--) {
            ((WindowState) this.mChildren.get(i)).setWillReplaceChildWindows();
        }
    }

    /* access modifiers changed from: package-private */
    public WindowState getReplacingWindow() {
        if (this.mAnimatingExit && this.mWillReplaceWindow && this.mAnimateReplacingWindow) {
            return this;
        }
        for (int i = this.mChildren.size() - 1; i >= 0; i--) {
            WindowState replacing = ((WindowState) this.mChildren.get(i)).getReplacingWindow();
            if (replacing != null) {
                return replacing;
            }
        }
        return null;
    }

    public int getRotationAnimationHint() {
        AppWindowToken appWindowToken = this.mAppToken;
        if (appWindowToken != null) {
            return appWindowToken.mRotationAnimationHint;
        }
        return -1;
    }

    public boolean isInputMethodWindow() {
        return this.mIsImWindow;
    }

    /* access modifiers changed from: package-private */
    public boolean performShowLocked() {
        AppWindowToken appWindowToken;
        if (isHiddenFromUserLocked()) {
            clearPolicyVisibilityFlag(2);
            return false;
        }
        logPerformShow("performShow on ");
        int drawState = this.mWinAnimator.mDrawState;
        if (!((drawState != 4 && drawState != 3) || this.mAttrs.type == 3 || (appWindowToken = this.mAppToken) == null)) {
            appWindowToken.onFirstWindowDrawn(this, this.mWinAnimator);
        }
        if (this.mWinAnimator.mDrawState != 3 || !isReadyForDisplay()) {
            return false;
        }
        logPerformShow("Showing ");
        this.mWmService.enableScreenIfNeededLocked();
        this.mWinAnimator.applyEnterAnimationLocked();
        WindowStateAnimator windowStateAnimator = this.mWinAnimator;
        windowStateAnimator.mLastAlpha = -1.0f;
        windowStateAnimator.mDrawState = 4;
        this.mWmService.scheduleAnimationLocked();
        if (this.mHidden) {
            this.mHidden = false;
            DisplayContent displayContent = getDisplayContent();
            for (int i = this.mChildren.size() - 1; i >= 0; i--) {
                WindowState c = (WindowState) this.mChildren.get(i);
                if (c.mWinAnimator.mSurfaceController != null) {
                    c.performShowLocked();
                    if (displayContent != null) {
                        displayContent.setLayoutNeeded();
                    }
                }
            }
        }
        if (this.mAttrs.type == 2011) {
            getDisplayContent().mDividerControllerLocked.resetImeHideRequested();
        }
        return true;
    }

    private void logPerformShow(String prefix) {
    }

    /* access modifiers changed from: package-private */
    public WindowInfo getWindowInfo() {
        WindowInfo windowInfo = WindowInfo.obtain();
        windowInfo.type = this.mAttrs.type;
        windowInfo.layer = this.mLayer;
        windowInfo.token = this.mClient.asBinder();
        AppWindowToken appWindowToken = this.mAppToken;
        if (appWindowToken != null) {
            windowInfo.activityToken = appWindowToken.appToken.asBinder();
        }
        windowInfo.title = this.mAttrs.accessibilityTitle;
        boolean z = false;
        boolean isPanelWindow = this.mAttrs.type >= 1000 && this.mAttrs.type <= 1999;
        boolean isAccessibilityOverlay = windowInfo.type == 2032;
        if (TextUtils.isEmpty(windowInfo.title) && (isPanelWindow || isAccessibilityOverlay)) {
            CharSequence title = this.mAttrs.getTitle();
            windowInfo.title = TextUtils.isEmpty(title) ? null : title;
        }
        windowInfo.accessibilityIdOfAnchor = this.mAttrs.accessibilityIdOfAnchor;
        windowInfo.focused = isFocused();
        Task task = getTask();
        windowInfo.inPictureInPicture = task != null && task.inPinnedWindowingMode();
        if ((this.mAttrs.flags & DumpState.DUMP_DOMAIN_PREFERRED) != 0) {
            z = true;
        }
        windowInfo.hasFlagWatchOutsideTouch = z;
        if (this.mIsChildWindow) {
            windowInfo.parentToken = getParentWindow().mClient.asBinder();
        }
        int childCount = this.mChildren.size();
        if (childCount > 0) {
            if (windowInfo.childTokens == null) {
                windowInfo.childTokens = new ArrayList(childCount);
            }
            for (int j = 0; j < childCount; j++) {
                windowInfo.childTokens.add(((WindowState) this.mChildren.get(j)).mClient.asBinder());
            }
        }
        return windowInfo;
    }

    /* access modifiers changed from: package-private */
    public boolean forAllWindows(ToBooleanFunction<WindowState> callback, boolean traverseTopToBottom) {
        if (this.mChildren.isEmpty()) {
            return applyInOrderWithImeWindows(callback, traverseTopToBottom);
        }
        if (traverseTopToBottom) {
            return forAllWindowTopToBottom(callback);
        }
        return forAllWindowBottomToTop(callback);
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v3, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v5, resolved type: com.android.server.wm.WindowState} */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private boolean forAllWindowBottomToTop(com.android.internal.util.ToBooleanFunction<com.android.server.wm.WindowState> r7) {
        /*
            r6 = this;
            r0 = 0
            com.android.server.wm.WindowList r1 = r6.mChildren
            int r1 = r1.size()
            com.android.server.wm.WindowList r2 = r6.mChildren
            java.lang.Object r2 = r2.get(r0)
            com.android.server.wm.WindowState r2 = (com.android.server.wm.WindowState) r2
        L_0x000f:
            r3 = 0
            r4 = 1
            if (r0 >= r1) goto L_0x002d
            int r5 = r2.mSubLayer
            if (r5 >= 0) goto L_0x002d
            boolean r5 = r2.applyInOrderWithImeWindows(r7, r3)
            if (r5 == 0) goto L_0x001e
            return r4
        L_0x001e:
            int r0 = r0 + 1
            if (r0 < r1) goto L_0x0023
            goto L_0x002d
        L_0x0023:
            com.android.server.wm.WindowList r3 = r6.mChildren
            java.lang.Object r3 = r3.get(r0)
            r2 = r3
            com.android.server.wm.WindowState r2 = (com.android.server.wm.WindowState) r2
            goto L_0x000f
        L_0x002d:
            boolean r5 = r6.applyInOrderWithImeWindows(r7, r3)
            if (r5 == 0) goto L_0x0034
            return r4
        L_0x0034:
            if (r0 >= r1) goto L_0x004c
            boolean r5 = r2.applyInOrderWithImeWindows(r7, r3)
            if (r5 == 0) goto L_0x003d
            return r4
        L_0x003d:
            int r0 = r0 + 1
            if (r0 < r1) goto L_0x0042
            goto L_0x004c
        L_0x0042:
            com.android.server.wm.WindowList r5 = r6.mChildren
            java.lang.Object r5 = r5.get(r0)
            r2 = r5
            com.android.server.wm.WindowState r2 = (com.android.server.wm.WindowState) r2
            goto L_0x0034
        L_0x004c:
            return r3
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.wm.WindowState.forAllWindowBottomToTop(com.android.internal.util.ToBooleanFunction):boolean");
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v3, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v5, resolved type: com.android.server.wm.WindowState} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v7, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v7, resolved type: com.android.server.wm.WindowState} */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private boolean forAllWindowTopToBottom(com.android.internal.util.ToBooleanFunction<com.android.server.wm.WindowState> r5) {
        /*
            r4 = this;
            com.android.server.wm.WindowList r0 = r4.mChildren
            int r0 = r0.size()
            r1 = 1
            int r0 = r0 - r1
            com.android.server.wm.WindowList r2 = r4.mChildren
            java.lang.Object r2 = r2.get(r0)
            com.android.server.wm.WindowState r2 = (com.android.server.wm.WindowState) r2
        L_0x0010:
            if (r0 < 0) goto L_0x002c
            int r3 = r2.mSubLayer
            if (r3 < 0) goto L_0x002c
            boolean r3 = r2.applyInOrderWithImeWindows(r5, r1)
            if (r3 == 0) goto L_0x001d
            return r1
        L_0x001d:
            int r0 = r0 + -1
            if (r0 >= 0) goto L_0x0022
            goto L_0x002c
        L_0x0022:
            com.android.server.wm.WindowList r3 = r4.mChildren
            java.lang.Object r3 = r3.get(r0)
            r2 = r3
            com.android.server.wm.WindowState r2 = (com.android.server.wm.WindowState) r2
            goto L_0x0010
        L_0x002c:
            boolean r3 = r4.applyInOrderWithImeWindows(r5, r1)
            if (r3 == 0) goto L_0x0033
            return r1
        L_0x0033:
            if (r0 < 0) goto L_0x004b
            boolean r3 = r2.applyInOrderWithImeWindows(r5, r1)
            if (r3 == 0) goto L_0x003c
            return r1
        L_0x003c:
            int r0 = r0 + -1
            if (r0 >= 0) goto L_0x0041
            goto L_0x004b
        L_0x0041:
            com.android.server.wm.WindowList r3 = r4.mChildren
            java.lang.Object r3 = r3.get(r0)
            r2 = r3
            com.android.server.wm.WindowState r2 = (com.android.server.wm.WindowState) r2
            goto L_0x0033
        L_0x004b:
            r1 = 0
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.wm.WindowState.forAllWindowTopToBottom(com.android.internal.util.ToBooleanFunction):boolean");
    }

    private boolean applyImeWindowsIfNeeded(ToBooleanFunction<WindowState> callback, boolean traverseTopToBottom) {
        if (!isInputMethodTarget() || inSplitScreenWindowingMode() || !getDisplayContent().forAllImeWindows(callback, traverseTopToBottom)) {
            return false;
        }
        return true;
    }

    private boolean applyInOrderWithImeWindows(ToBooleanFunction<WindowState> callback, boolean traverseTopToBottom) {
        if (traverseTopToBottom) {
            if (applyImeWindowsIfNeeded(callback, traverseTopToBottom) || callback.apply(this)) {
                return true;
            }
            return false;
        } else if (callback.apply(this) || applyImeWindowsIfNeeded(callback, traverseTopToBottom)) {
            return true;
        } else {
            return false;
        }
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v3, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v5, resolved type: com.android.server.wm.WindowState} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v7, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v7, resolved type: com.android.server.wm.WindowState} */
    /* access modifiers changed from: package-private */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public com.android.server.wm.WindowState getWindow(java.util.function.Predicate<com.android.server.wm.WindowState> r5) {
        /*
            r4 = this;
            com.android.server.wm.WindowList r0 = r4.mChildren
            boolean r0 = r0.isEmpty()
            r1 = 0
            if (r0 == 0) goto L_0x0011
            boolean r0 = r5.test(r4)
            if (r0 == 0) goto L_0x0010
            r1 = r4
        L_0x0010:
            return r1
        L_0x0011:
            com.android.server.wm.WindowList r0 = r4.mChildren
            int r0 = r0.size()
            int r0 = r0 + -1
            com.android.server.wm.WindowList r2 = r4.mChildren
            java.lang.Object r2 = r2.get(r0)
            com.android.server.wm.WindowState r2 = (com.android.server.wm.WindowState) r2
        L_0x0021:
            if (r0 < 0) goto L_0x003d
            int r3 = r2.mSubLayer
            if (r3 < 0) goto L_0x003d
            boolean r3 = r5.test(r2)
            if (r3 == 0) goto L_0x002e
            return r2
        L_0x002e:
            int r0 = r0 + -1
            if (r0 >= 0) goto L_0x0033
            goto L_0x003d
        L_0x0033:
            com.android.server.wm.WindowList r3 = r4.mChildren
            java.lang.Object r3 = r3.get(r0)
            r2 = r3
            com.android.server.wm.WindowState r2 = (com.android.server.wm.WindowState) r2
            goto L_0x0021
        L_0x003d:
            boolean r3 = r5.test(r4)
            if (r3 == 0) goto L_0x0044
            return r4
        L_0x0044:
            if (r0 < 0) goto L_0x005c
            boolean r3 = r5.test(r2)
            if (r3 == 0) goto L_0x004d
            return r2
        L_0x004d:
            int r0 = r0 + -1
            if (r0 >= 0) goto L_0x0052
            goto L_0x005c
        L_0x0052:
            com.android.server.wm.WindowList r3 = r4.mChildren
            java.lang.Object r3 = r3.get(r0)
            r2 = r3
            com.android.server.wm.WindowState r2 = (com.android.server.wm.WindowState) r2
            goto L_0x0044
        L_0x005c:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.wm.WindowState.getWindow(java.util.function.Predicate):com.android.server.wm.WindowState");
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public boolean isSelfOrAncestorWindowAnimatingExit() {
        WindowState window = this;
        while (!window.mAnimatingExit) {
            window = window.getParentWindow();
            if (window == null) {
                return false;
            }
        }
        return true;
    }

    /* access modifiers changed from: package-private */
    public void onExitAnimationDone() {
        if (!this.mChildren.isEmpty()) {
            ArrayList<WindowState> childWindows = new ArrayList<>(this.mChildren);
            for (int i = childWindows.size() - 1; i >= 0; i--) {
                childWindows.get(i).onExitAnimationDone();
            }
        }
        if (this.mWinAnimator.mEnteringAnimation) {
            this.mWinAnimator.mEnteringAnimation = false;
            this.mWmService.requestTraversal();
            if (this.mAppToken == null) {
                try {
                    this.mClient.dispatchWindowShown();
                } catch (RemoteException e) {
                }
            }
        }
        if (!isSelfAnimating()) {
            if (this.mWmService.mAccessibilityController != null && (getDisplayId() == 0 || getDisplayContent().getParentWindow() != null)) {
                this.mWmService.mAccessibilityController.onSomeWindowResizedOrMovedLocked();
            }
            if (isSelfOrAncestorWindowAnimatingExit()) {
                this.mDestroying = true;
                boolean hasSurface = this.mWinAnimator.hasSurface();
                this.mWinAnimator.hide(getPendingTransaction(), "onExitAnimationDone");
                AppWindowToken appWindowToken = this.mAppToken;
                if (appWindowToken != null) {
                    appWindowToken.destroySurfaces();
                } else {
                    if (hasSurface) {
                        this.mWmService.mDestroySurface.add(this);
                    }
                    if (this.mRemoveOnExit) {
                        this.mWmService.mPendingRemove.add(this);
                        this.mRemoveOnExit = false;
                    }
                }
                this.mAnimatingExit = false;
                getDisplayContent().mWallpaperController.hideWallpapers(this);
            }
        }
    }

    /* access modifiers changed from: package-private */
    public boolean clearAnimatingFlags() {
        boolean didSomething = false;
        if (!this.mWillReplaceWindow && !this.mRemoveOnExit) {
            if (this.mAnimatingExit) {
                this.mAnimatingExit = false;
                didSomething = true;
            }
            if (this.mDestroying) {
                this.mDestroying = false;
                this.mWmService.mDestroySurface.remove(this);
                didSomething = true;
            }
        }
        for (int i = this.mChildren.size() - 1; i >= 0; i--) {
            didSomething |= ((WindowState) this.mChildren.get(i)).clearAnimatingFlags();
        }
        return didSomething;
    }

    public boolean isRtl() {
        return getConfiguration().getLayoutDirection() == 1;
    }

    /* access modifiers changed from: package-private */
    public void hideWallpaperWindow(boolean wasDeferred, String reason) {
        for (int j = this.mChildren.size() - 1; j >= 0; j--) {
            ((WindowState) this.mChildren.get(j)).hideWallpaperWindow(wasDeferred, reason);
        }
        if (!this.mWinAnimator.mLastHidden || wasDeferred) {
            this.mWinAnimator.hide(reason);
            getDisplayContent().mWallpaperController.mDeferredHideWallpaper = null;
            dispatchWallpaperVisibility(false);
            DisplayContent displayContent = getDisplayContent();
            if (displayContent != null) {
                displayContent.pendingLayoutChanges |= 4;
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void dispatchWallpaperVisibility(boolean visible) {
        boolean hideAllowed = getDisplayContent().mWallpaperController.mDeferredHideWallpaper == null;
        if (this.mWallpaperVisible == visible) {
            return;
        }
        if (hideAllowed || visible) {
            this.mWallpaperVisible = visible;
            try {
                this.mClient.dispatchAppVisibility(visible);
            } catch (RemoteException e) {
            }
        }
    }

    /* access modifiers changed from: package-private */
    public boolean hasVisibleNotDrawnWallpaper() {
        if (this.mWallpaperVisible && !isDrawnLw()) {
            return true;
        }
        for (int j = this.mChildren.size() - 1; j >= 0; j--) {
            if (((WindowState) this.mChildren.get(j)).hasVisibleNotDrawnWallpaper()) {
                return true;
            }
        }
        return false;
    }

    /* access modifiers changed from: package-private */
    public void updateReportedVisibility(UpdateReportedVisibilityResults results) {
        for (int i = this.mChildren.size() - 1; i >= 0; i--) {
            ((WindowState) this.mChildren.get(i)).updateReportedVisibility(results);
        }
        if (this.mAppFreezing == 0 && this.mViewVisibility == 0 && this.mAttrs.type != 3 && !this.mDestroying) {
            results.numInteresting++;
            if (isDrawnLw()) {
                results.numDrawn++;
                if (!isAnimating()) {
                    results.numVisible++;
                }
                results.nowGone = false;
            } else if (isAnimating()) {
                results.nowGone = false;
            }
        }
    }

    private boolean skipDecorCrop() {
        if (this.mWindowFrames.mDecorFrame.isEmpty()) {
            return true;
        }
        if (this.mAppToken != null) {
            return false;
        }
        return this.mToken.canLayerAboveSystemBars();
    }

    /* access modifiers changed from: package-private */
    public void calculatePolicyCrop(Rect policyCrop) {
        DisplayContent displayContent = getDisplayContent();
        if (!displayContent.isDefaultDisplay && !displayContent.supportsSystemDecorations()) {
            DisplayInfo displayInfo = displayContent.getDisplayInfo();
            policyCrop.set(0, 0, this.mWindowFrames.mCompatFrame.width(), this.mWindowFrames.mCompatFrame.height());
            policyCrop.intersect(-this.mWindowFrames.mCompatFrame.left, -this.mWindowFrames.mCompatFrame.top, displayInfo.logicalWidth - this.mWindowFrames.mCompatFrame.left, displayInfo.logicalHeight - this.mWindowFrames.mCompatFrame.top);
        } else if (skipDecorCrop()) {
            policyCrop.set(0, 0, this.mWindowFrames.mCompatFrame.width(), this.mWindowFrames.mCompatFrame.height());
        } else {
            calculateSystemDecorRect(policyCrop);
        }
    }

    private void calculateSystemDecorRect(Rect systemDecorRect) {
        Rect decorRect = this.mWindowFrames.mDecorFrame;
        int width = this.mWindowFrames.mFrame.width();
        int height = this.mWindowFrames.mFrame.height();
        int left = this.mWindowFrames.mFrame.left;
        int top = this.mWindowFrames.mFrame.top;
        boolean cropToDecor = false;
        if (isDockedResizing()) {
            DisplayInfo displayInfo = getDisplayContent().getDisplayInfo();
            systemDecorRect.set(0, 0, Math.max(width, displayInfo.logicalWidth), Math.max(height, displayInfo.logicalHeight));
        } else {
            systemDecorRect.set(0, 0, width, height);
        }
        if ((!inFreeformWindowingMode() || !isAnimatingLw()) && !isDockedResizing()) {
            cropToDecor = true;
        }
        if (cropToDecor) {
            systemDecorRect.intersect(decorRect.left - left, decorRect.top - top, decorRect.right - left, decorRect.bottom - top);
        }
        if (this.mInvGlobalScale != 1.0f && inSizeCompatMode()) {
            float scale = this.mInvGlobalScale;
            systemDecorRect.left = (int) ((((float) systemDecorRect.left) * scale) - 0.5f);
            systemDecorRect.top = (int) ((((float) systemDecorRect.top) * scale) - 0.5f);
            systemDecorRect.right = (int) ((((float) (systemDecorRect.right + 1)) * scale) - 0.5f);
            systemDecorRect.bottom = (int) ((((float) (systemDecorRect.bottom + 1)) * scale) - 0.5f);
        }
    }

    /* access modifiers changed from: package-private */
    public void expandForSurfaceInsets(Rect r) {
        r.inset(-this.mAttrs.surfaceInsets.left, -this.mAttrs.surfaceInsets.top, -this.mAttrs.surfaceInsets.right, -this.mAttrs.surfaceInsets.bottom);
    }

    /* access modifiers changed from: package-private */
    public boolean surfaceInsetsChanging() {
        return !this.mLastSurfaceInsets.equals(this.mAttrs.surfaceInsets);
    }

    /* JADX INFO: finally extract failed */
    /* access modifiers changed from: package-private */
    public int relayoutVisibleWindow(int result, int attrChanges) {
        boolean wasVisible = isVisibleLw();
        int i = 0;
        int result2 = result | ((!wasVisible || !isDrawnLw()) ? 2 : 0);
        if (this.mAnimatingExit) {
            Slog.d("WindowManager", "relayoutVisibleWindow: " + this + " mAnimatingExit=true, mRemoveOnExit=" + this.mRemoveOnExit + ", mDestroying=" + this.mDestroying);
            if (isSelfAnimating()) {
                cancelAnimation();
                if (this.mAppToken == null) {
                    destroySurfaceUnchecked();
                }
            }
            this.mAnimatingExit = false;
        }
        if (this.mDestroying) {
            this.mDestroying = false;
            this.mWmService.mDestroySurface.remove(this);
        }
        boolean dockedResizing = true;
        if (!wasVisible) {
            this.mWinAnimator.mEnterAnimationPending = true;
        }
        this.mLastVisibleLayoutRotation = getDisplayContent().getRotation();
        this.mWinAnimator.mEnteringAnimation = true;
        Trace.traceBegin(32, "prepareToDisplay");
        try {
            prepareWindowToDisplayDuringRelayout(wasVisible);
            Trace.traceEnd(32);
            if ((attrChanges & 8) != 0 && !this.mWinAnimator.tryChangeFormatInPlaceLocked()) {
                this.mWinAnimator.preserveSurfaceLocked();
                result2 |= 6;
            }
            if (isDragResizeChanged()) {
                setDragResizing();
                if (this.mHasSurface && !isChildWindow()) {
                    this.mWinAnimator.preserveSurfaceLocked();
                    result2 |= 6;
                }
            }
            boolean freeformResizing = isDragResizing() && getResizeMode() == 0;
            if (!isDragResizing() || getResizeMode() != 1) {
                dockedResizing = false;
            }
            int result3 = result2 | (freeformResizing ? 16 : 0);
            if (dockedResizing) {
                i = 8;
            }
            return result3 | i;
        } catch (Throwable th) {
            Trace.traceEnd(32);
            throw th;
        }
    }

    /* access modifiers changed from: package-private */
    public boolean isLaidOut() {
        return this.mLayoutSeq != -1;
    }

    /* access modifiers changed from: package-private */
    public void updateLastInsetValues() {
        this.mWindowFrames.updateLastInsetValues();
    }

    /* access modifiers changed from: package-private */
    public void startAnimation(Animation anim) {
        InsetsSourceProvider insetsSourceProvider = this.mInsetProvider;
        if (insetsSourceProvider == null || !insetsSourceProvider.isControllable()) {
            DisplayInfo displayInfo = getDisplayContent().getDisplayInfo();
            anim.initialize(this.mWindowFrames.mFrame.width(), this.mWindowFrames.mFrame.height(), displayInfo.appWidth, displayInfo.appHeight);
            anim.restrictDuration(JobStatus.DEFAULT_TRIGGER_UPDATE_DELAY);
            anim.scaleCurrentDuration(this.mWmService.getWindowAnimationScaleLocked());
            startAnimation(getPendingTransaction(), new LocalAnimationAdapter(new WindowAnimationSpec(anim, this.mSurfacePosition, false, 0.0f), this.mWmService.mSurfaceAnimationRunner));
            commitPendingTransaction();
        }
    }

    private void startMoveAnimation(int left, int top) {
        InsetsSourceProvider insetsSourceProvider = this.mInsetProvider;
        if (insetsSourceProvider == null || !insetsSourceProvider.isControllable()) {
            Point oldPosition = new Point();
            Point newPosition = new Point();
            transformFrameToSurfacePosition(this.mWindowFrames.mLastFrame.left, this.mWindowFrames.mLastFrame.top, oldPosition);
            transformFrameToSurfacePosition(left, top, newPosition);
            startAnimation(getPendingTransaction(), new LocalAnimationAdapter(new MoveAnimationSpec(oldPosition.x, oldPosition.y, newPosition.x, newPosition.y), this.mWmService.mSurfaceAnimationRunner));
        }
    }

    private void startAnimation(SurfaceControl.Transaction t, AnimationAdapter adapter) {
        startAnimation(t, adapter, this.mWinAnimator.mLastHidden);
    }

    /* access modifiers changed from: protected */
    public void onAnimationFinished() {
        super.onAnimationFinished();
        this.mWinAnimator.onAnimationFinished();
    }

    /* access modifiers changed from: package-private */
    public void getTransformationMatrix(float[] float9, Matrix outMatrix) {
        float9[0] = this.mWinAnimator.mDsDx;
        float9[3] = this.mWinAnimator.mDtDx;
        float9[1] = this.mWinAnimator.mDtDy;
        float9[4] = this.mWinAnimator.mDsDy;
        int x = this.mSurfacePosition.x;
        int y = this.mSurfacePosition.y;
        DisplayContent dc = getDisplayContent();
        while (dc != null && dc.getParentWindow() != null) {
            WindowState displayParent = dc.getParentWindow();
            x = (int) (((float) x) + ((float) (displayParent.mWindowFrames.mFrame.left - displayParent.mAttrs.surfaceInsets.left)) + (((float) dc.getLocationInParentWindow().x) * displayParent.mGlobalScale) + 0.5f);
            y = (int) (((float) y) + ((float) (displayParent.mWindowFrames.mFrame.top - displayParent.mAttrs.surfaceInsets.top)) + (((float) dc.getLocationInParentWindow().y) * displayParent.mGlobalScale) + 0.5f);
            dc = displayParent.getDisplayContent();
        }
        WindowContainer parent = getParent();
        if (isChildWindow()) {
            WindowState parentWindow = getParentWindow();
            x += parentWindow.mWindowFrames.mFrame.left - parentWindow.mAttrs.surfaceInsets.left;
            y += parentWindow.mWindowFrames.mFrame.top - parentWindow.mAttrs.surfaceInsets.top;
        } else if (parent != null) {
            Rect parentBounds = parent.getBounds();
            x += parentBounds.left;
            y += parentBounds.top;
        }
        float9[2] = (float) x;
        float9[5] = (float) y;
        float9[6] = 0.0f;
        float9[7] = 0.0f;
        float9[8] = 1.0f;
        outMatrix.setValues(float9);
    }

    static final class UpdateReportedVisibilityResults {
        boolean nowGone = true;
        int numDrawn;
        int numInteresting;
        int numVisible;

        UpdateReportedVisibilityResults() {
        }

        /* access modifiers changed from: package-private */
        public void reset() {
            this.numInteresting = 0;
            this.numVisible = 0;
            this.numDrawn = 0;
            this.nowGone = true;
        }
    }

    private static final class WindowId extends IWindowId.Stub {
        private final WeakReference<WindowState> mOuter;

        private WindowId(WindowState outer) {
            this.mOuter = new WeakReference<>(outer);
        }

        public void registerFocusObserver(IWindowFocusObserver observer) {
            WindowState outer = (WindowState) this.mOuter.get();
            if (outer != null) {
                outer.registerFocusObserver(observer);
            }
        }

        public void unregisterFocusObserver(IWindowFocusObserver observer) {
            WindowState outer = (WindowState) this.mOuter.get();
            if (outer != null) {
                outer.unregisterFocusObserver(observer);
            }
        }

        public boolean isFocused() {
            boolean isFocused;
            WindowState outer = (WindowState) this.mOuter.get();
            if (outer == null) {
                return false;
            }
            synchronized (outer.mWmService.mGlobalLock) {
                try {
                    WindowManagerService.boostPriorityForLockedSection();
                    isFocused = outer.isFocused();
                } catch (Throwable th) {
                    while (true) {
                        WindowManagerService.resetPriorityAfterLockedSection();
                        throw th;
                    }
                }
            }
            WindowManagerService.resetPriorityAfterLockedSection();
            return isFocused;
        }
    }

    /* access modifiers changed from: package-private */
    public boolean shouldMagnify() {
        if (this.mAttrs.type == 2011 || this.mAttrs.type == 2012 || this.mAttrs.type == 2027 || this.mAttrs.type == 2019 || this.mAttrs.type == 2024) {
            return false;
        }
        return true;
    }

    /* access modifiers changed from: package-private */
    public SurfaceSession getSession() {
        if (this.mSession.mSurfaceSession != null) {
            return this.mSession.mSurfaceSession;
        }
        return getParent().getSession();
    }

    /* access modifiers changed from: package-private */
    public boolean needsZBoost() {
        AppWindowToken appToken;
        WindowState inputMethodTarget = getDisplayContent().mInputMethodTarget;
        if (!this.mIsImWindow || inputMethodTarget == null || (appToken = inputMethodTarget.mAppToken) == null) {
            return this.mWillReplaceWindow;
        }
        return appToken.needsZBoost();
    }

    private void applyDims(Dimmer dimmer) {
        if (!this.mAnimatingExit && this.mAppDied) {
            this.mIsDimming = true;
            dimmer.dimAbove(getPendingTransaction(), this, 0.5f);
        } else if ((this.mAttrs.flags & 2) != 0 && isVisibleNow() && !this.mHidden) {
            this.mIsDimming = true;
            dimmer.dimBelow(getPendingTransaction(), this, this.mAttrs.dimAmount);
        }
    }

    /* access modifiers changed from: package-private */
    public void prepareSurfaces() {
        Dimmer dimmer = getDimmer();
        this.mIsDimming = false;
        if (dimmer != null) {
            applyDims(dimmer);
        }
        updateSurfacePosition();
        super.prepareSurfaces();
        this.mWinAnimator.prepareSurfaceLocked(true);
    }

    public void onAnimationLeashCreated(SurfaceControl.Transaction t, SurfaceControl leash) {
        super.onAnimationLeashCreated(t, leash);
        t.setPosition(this.mSurfaceControl, 0.0f, 0.0f);
        this.mLastSurfacePosition.set(0, 0);
    }

    public void onAnimationLeashLost(SurfaceControl.Transaction t) {
        super.onAnimationLeashLost(t);
        updateSurfacePosition(t);
    }

    /* access modifiers changed from: package-private */
    public void updateSurfacePosition() {
        updateSurfacePosition(getPendingTransaction());
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public void updateSurfacePosition(SurfaceControl.Transaction t) {
        if (this.mSurfaceControl != null) {
            transformFrameToSurfacePosition(this.mWindowFrames.mFrame.left, this.mWindowFrames.mFrame.top, this.mSurfacePosition);
            if (!this.mSurfaceAnimator.hasLeash() && this.mPendingSeamlessRotate == null && !this.mLastSurfacePosition.equals(this.mSurfacePosition)) {
                t.setPosition(this.mSurfaceControl, (float) this.mSurfacePosition.x, (float) this.mSurfacePosition.y);
                this.mLastSurfacePosition.set(this.mSurfacePosition.x, this.mSurfacePosition.y);
                if (surfaceInsetsChanging() && this.mWinAnimator.hasSurface()) {
                    this.mLastSurfaceInsets.set(this.mAttrs.surfaceInsets);
                    t.deferTransactionUntil(this.mSurfaceControl, this.mWinAnimator.mSurfaceController.mSurfaceControl.getHandle(), getFrameNumber());
                }
            }
        }
    }

    private void transformFrameToSurfacePosition(int left, int top, Point outPoint) {
        outPoint.set(left, top);
        WindowContainer parentWindowContainer = getParent();
        if (isChildWindow()) {
            WindowState parent = getParentWindow();
            transformSurfaceInsetsPosition(this.mTmpPoint, parent.mAttrs.surfaceInsets);
            outPoint.offset((-parent.mWindowFrames.mFrame.left) + this.mTmpPoint.x, (-parent.mWindowFrames.mFrame.top) + this.mTmpPoint.y);
        } else if (parentWindowContainer != null) {
            Rect parentBounds = parentWindowContainer.getDisplayedBounds();
            outPoint.offset(-parentBounds.left, -parentBounds.top);
        }
        TaskStack stack = getStack();
        if (stack != null) {
            int outset = stack.getStackOutset();
            outPoint.offset(outset, outset);
        }
        transformSurfaceInsetsPosition(this.mTmpPoint, this.mAttrs.surfaceInsets);
        outPoint.offset(-this.mTmpPoint.x, -this.mTmpPoint.y);
    }

    private void transformSurfaceInsetsPosition(Point outPos, Rect surfaceInsets) {
        if (!inSizeCompatMode()) {
            outPos.x = surfaceInsets.left;
            outPos.y = surfaceInsets.top;
            return;
        }
        outPos.x = (int) ((((float) surfaceInsets.left) * this.mGlobalScale) + 0.5f);
        outPos.y = (int) ((((float) surfaceInsets.top) * this.mGlobalScale) + 0.5f);
    }

    /* access modifiers changed from: package-private */
    public boolean needsRelativeLayeringToIme() {
        WindowState imeTarget;
        if (!inSplitScreenWindowingMode()) {
            return false;
        }
        if (isChildWindow()) {
            if (getParentWindow().isInputMethodTarget()) {
                return true;
            }
        } else if (this.mAppToken == null || (imeTarget = getDisplayContent().mInputMethodTarget) == null || imeTarget == this || imeTarget.mToken != this.mToken || imeTarget.compareTo((WindowContainer) this) > 0) {
            return false;
        } else {
            return true;
        }
        return false;
    }

    /* access modifiers changed from: package-private */
    public void assignLayer(SurfaceControl.Transaction t, int layer) {
        if (needsRelativeLayeringToIme()) {
            getDisplayContent().assignRelativeLayerForImeTargetChild(t, this);
        } else {
            super.assignLayer(t, layer);
        }
    }

    public boolean isDimming() {
        return this.mIsDimming;
    }

    public void assignChildLayers(SurfaceControl.Transaction t) {
        int layer = 2;
        for (int i = 0; i < this.mChildren.size(); i++) {
            WindowState w = (WindowState) this.mChildren.get(i);
            if (w.mAttrs.type == 1001) {
                w.assignLayer(t, -2);
            } else if (w.mAttrs.type == 1004) {
                w.assignLayer(t, -1);
            } else {
                w.assignLayer(t, layer);
            }
            w.assignChildLayers(t);
            layer++;
        }
    }

    /* access modifiers changed from: package-private */
    public void updateTapExcludeRegion(int regionId, Region region) {
        DisplayContent currentDisplay = getDisplayContent();
        if (currentDisplay != null) {
            if (this.mTapExcludeRegionHolder == null) {
                this.mTapExcludeRegionHolder = new TapExcludeRegionHolder();
                currentDisplay.mTapExcludeProvidingWindows.add(this);
            }
            this.mTapExcludeRegionHolder.updateRegion(regionId, region);
            currentDisplay.updateTouchExcludeRegion();
            currentDisplay.getInputMonitor().updateInputWindowsLw(true);
            return;
        }
        throw new IllegalStateException("Trying to update window not attached to any display.");
    }

    /* access modifiers changed from: package-private */
    public void amendTapExcludeRegion(Region region) {
        Region tempRegion = Region.obtain();
        this.mTmpRect.set(this.mWindowFrames.mFrame);
        this.mTmpRect.offsetTo(0, 0);
        this.mTapExcludeRegionHolder.amendRegion(tempRegion, this.mTmpRect);
        tempRegion.translate(this.mWindowFrames.mFrame.left, this.mWindowFrames.mFrame.top);
        region.op(tempRegion, Region.Op.UNION);
        tempRegion.recycle();
    }

    public boolean isInputMethodTarget() {
        return getDisplayContent().mInputMethodTarget == this;
    }

    /* access modifiers changed from: package-private */
    public long getFrameNumber() {
        return this.mFrameNumber;
    }

    /* access modifiers changed from: package-private */
    public void setFrameNumber(long frameNumber) {
        this.mFrameNumber = frameNumber;
    }

    public void getMaxVisibleBounds(Rect out) {
        if (out.isEmpty()) {
            out.set(this.mWindowFrames.mVisibleFrame);
            return;
        }
        if (this.mWindowFrames.mVisibleFrame.left < out.left) {
            out.left = this.mWindowFrames.mVisibleFrame.left;
        }
        if (this.mWindowFrames.mVisibleFrame.top < out.top) {
            out.top = this.mWindowFrames.mVisibleFrame.top;
        }
        if (this.mWindowFrames.mVisibleFrame.right > out.right) {
            out.right = this.mWindowFrames.mVisibleFrame.right;
        }
        if (this.mWindowFrames.mVisibleFrame.bottom > out.bottom) {
            out.bottom = this.mWindowFrames.mVisibleFrame.bottom;
        }
    }

    /* access modifiers changed from: package-private */
    public void getInsetsForRelayout(Rect outOverscanInsets, Rect outContentInsets, Rect outVisibleInsets, Rect outStableInsets, Rect outOutsets) {
        outOverscanInsets.set(this.mWindowFrames.mOverscanInsets);
        outContentInsets.set(this.mWindowFrames.mContentInsets);
        outVisibleInsets.set(this.mWindowFrames.mVisibleInsets);
        outStableInsets.set(this.mWindowFrames.mStableInsets);
        outOutsets.set(this.mWindowFrames.mOutsets);
        this.mLastRelayoutContentInsets.set(this.mWindowFrames.mContentInsets);
    }

    /* access modifiers changed from: package-private */
    public void getContentInsets(Rect outContentInsets) {
        outContentInsets.set(this.mWindowFrames.mContentInsets);
    }

    /* access modifiers changed from: package-private */
    public Rect getContentInsets() {
        return this.mWindowFrames.mContentInsets;
    }

    /* access modifiers changed from: package-private */
    public void getStableInsets(Rect outStableInsets) {
        outStableInsets.set(this.mWindowFrames.mStableInsets);
    }

    /* access modifiers changed from: package-private */
    public Rect getStableInsets() {
        return this.mWindowFrames.mStableInsets;
    }

    /* access modifiers changed from: package-private */
    public void resetLastContentInsets() {
        this.mWindowFrames.resetLastContentInsets();
    }

    /* access modifiers changed from: package-private */
    public Rect getVisibleInsets() {
        return this.mWindowFrames.mVisibleInsets;
    }

    public WindowFrames getWindowFrames() {
        return this.mWindowFrames;
    }

    /* access modifiers changed from: package-private */
    public void resetContentChanged() {
        this.mWindowFrames.setContentChanged(false);
    }

    /* access modifiers changed from: package-private */
    public void setInsetProvider(InsetsSourceProvider insetProvider) {
        this.mInsetProvider = insetProvider;
    }

    /* access modifiers changed from: package-private */
    public InsetsSourceProvider getInsetProvider() {
        return this.mInsetProvider;
    }

    private final class MoveAnimationSpec implements LocalAnimationAdapter.AnimationSpec {
        private final long mDuration;
        private Point mFrom;
        private Interpolator mInterpolator;
        private Point mTo;

        private MoveAnimationSpec(int fromX, int fromY, int toX, int toY) {
            this.mFrom = new Point();
            this.mTo = new Point();
            Animation anim = AnimationUtils.loadAnimation(WindowState.this.mContext, 17432918);
            this.mDuration = (long) (((float) anim.computeDurationHint()) * WindowState.this.mWmService.getWindowAnimationScaleLocked());
            this.mInterpolator = anim.getInterpolator();
            this.mFrom.set(fromX, fromY);
            this.mTo.set(toX, toY);
        }

        public long getDuration() {
            return this.mDuration;
        }

        public void apply(SurfaceControl.Transaction t, SurfaceControl leash, long currentPlayTime) {
            float v = this.mInterpolator.getInterpolation(((float) currentPlayTime) / ((float) getDuration()));
            if (WindowState.this.inFreeformWindowingMode()) {
                t.setWindowCrop(leash, new Rect(0, 0, (int) (((float) WindowState.this.getFrameLw().width()) * MiuiMultiWindowUtils.sScale), (int) (((float) WindowState.this.getFrameLw().height()) * MiuiMultiWindowUtils.sScale)));
                t.setCornerRadius(leash, MiuiMultiWindowUtils.FREEFORM_ROUND_CORNER * MiuiMultiWindowUtils.sScale);
            }
            t.setPosition(leash, ((float) this.mFrom.x) + (((float) (this.mTo.x - this.mFrom.x)) * v), ((float) this.mFrom.y) + (((float) (this.mTo.y - this.mFrom.y)) * v));
        }

        public void dump(PrintWriter pw, String prefix) {
            pw.println(prefix + "from=" + this.mFrom + " to=" + this.mTo + " duration=" + this.mDuration);
        }

        public void writeToProtoInner(ProtoOutputStream proto) {
            long token = proto.start(1146756268034L);
            this.mFrom.writeToProto(proto, 1146756268033L);
            this.mTo.writeToProto(proto, 1146756268034L);
            proto.write(1112396529667L, this.mDuration);
            proto.end(token);
        }
    }

    /* access modifiers changed from: package-private */
    public boolean requestTraversalOnceLw(Object context) {
        if (this.mRequestTraversalOnceContext == context) {
            return false;
        }
        if (this.mWmService != null) {
            this.mWmService.requestTraversal();
        }
        this.mRequestTraversalOnceContext = context;
        return true;
    }

    public boolean isBlurStateChanged() {
        if (this.mBlurRatio == this.mAttrs.blurRatio && this.mBlurMode == this.mAttrs.blurMode) {
            return false;
        }
        this.mBlurRatio = this.mAttrs.blurRatio;
        this.mBlurMode = this.mAttrs.blurMode;
        return true;
    }
}
