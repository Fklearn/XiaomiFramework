package com.android.server.wm;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Region;
import android.os.Trace;
import android.util.MiuiMultiWindowUtils;
import android.util.Slog;
import android.util.proto.ProtoOutputStream;
import android.view.DisplayInfo;
import android.view.SurfaceControl;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import com.android.server.pm.DumpState;
import com.android.server.policy.WindowManagerPolicy;
import java.io.PrintWriter;

class WindowStateAnimator {
    static final int COMMIT_DRAW_PENDING = 2;
    static final int DRAW_PENDING = 1;
    static final int HAS_DRAWN = 4;
    static final int NO_SURFACE = 0;
    static final int PRESERVED_SURFACE_LAYER = 1;
    static final int READY_TO_SHOW = 3;
    static final int STACK_CLIP_AFTER_ANIM = 0;
    static final int STACK_CLIP_BEFORE_ANIM = 1;
    static final int STACK_CLIP_NONE = 2;
    static final String TAG = "WindowManager";
    static final int WINDOW_FREEZE_LAYER = 2000000;
    float mAlpha = 0.0f;
    boolean mAnimationIsEntrance;
    final WindowAnimator mAnimator;
    int mAttrType;
    boolean mChildrenDetached = false;
    final Context mContext;
    private boolean mDestroyPreservedSurfaceUponRedraw;
    int mDrawState;
    float mDsDx = 1.0f;
    float mDsDy = 0.0f;
    float mDtDx = 0.0f;
    float mDtDy = 1.0f;
    boolean mEnterAnimationPending;
    boolean mEnteringAnimation;
    float mExtraHScale = 1.0f;
    float mExtraVScale = 1.0f;
    boolean mForceScaleUntilResize;
    boolean mHandleByGesture;
    boolean mHaveMatrix;
    final boolean mIsWallpaper;
    float mLastAlpha = 0.0f;
    Rect mLastClipRect = new Rect();
    private float mLastDsDx = 1.0f;
    private float mLastDsDy = 0.0f;
    private float mLastDtDx = 0.0f;
    private float mLastDtDy = 1.0f;
    Rect mLastFinalClipRect = new Rect();
    boolean mLastHidden;
    private boolean mOffsetPositionForStackResize;
    private WindowSurfaceController mPendingDestroySurface;
    boolean mPipAnimationStarted = false;
    final WindowManagerPolicy mPolicy;
    private final SurfaceControl.Transaction mReparentTransaction = new SurfaceControl.Transaction();
    boolean mReportSurfaceResized;
    final WindowManagerService mService;
    final Session mSession;
    float mShownAlpha = 0.0f;
    WindowSurfaceController mSurfaceController;
    boolean mSurfaceDestroyDeferred;
    int mSurfaceFormat;
    boolean mSurfaceResized;
    private final Rect mSystemDecorRect = new Rect();
    private Rect mTmpAnimatingBounds = new Rect();
    Rect mTmpClipRect = new Rect();
    private final Point mTmpPos = new Point();
    private final Rect mTmpSize = new Rect();
    private Rect mTmpSourceBounds = new Rect();
    Rect mTmpStackBounds = new Rect();
    private final SurfaceControl.Transaction mTmpTransaction = new SurfaceControl.Transaction();
    private final WallpaperController mWallpaperControllerLocked;
    final WindowState mWin;
    int mXOffset = 0;
    int mYOffset = 0;

    /* access modifiers changed from: package-private */
    public String drawStateToString() {
        int i = this.mDrawState;
        if (i == 0) {
            return "NO_SURFACE";
        }
        if (i == 1) {
            return "DRAW_PENDING";
        }
        if (i == 2) {
            return "COMMIT_DRAW_PENDING";
        }
        if (i == 3) {
            return "READY_TO_SHOW";
        }
        if (i != 4) {
            return Integer.toString(i);
        }
        return "HAS_DRAWN";
    }

    WindowStateAnimator(WindowState win) {
        WindowManagerService service = win.mWmService;
        this.mService = service;
        this.mAnimator = service.mAnimator;
        this.mPolicy = service.mPolicy;
        this.mContext = service.mContext;
        this.mWin = win;
        this.mSession = win.mSession;
        this.mAttrType = win.mAttrs.type;
        this.mIsWallpaper = win.mIsWallpaper;
        this.mWallpaperControllerLocked = win.getDisplayContent().mWallpaperController;
    }

    /* access modifiers changed from: package-private */
    public void onAnimationFinished() {
        this.mWin.checkPolicyVisibilityChange();
        DisplayContent displayContent = this.mWin.getDisplayContent();
        if (this.mAttrType == 2000 && this.mWin.isVisibleByPolicy() && displayContent != null) {
            displayContent.setLayoutNeeded();
        }
        this.mWin.onExitAnimationDone();
        int displayId = this.mWin.getDisplayId();
        int pendingLayoutChanges = 8;
        if (displayContent.mWallpaperController.isWallpaperTarget(this.mWin)) {
            pendingLayoutChanges = 8 | 4;
        }
        this.mAnimator.setPendingLayoutChanges(displayId, pendingLayoutChanges);
        if (this.mWin.mAppToken != null) {
            this.mWin.mAppToken.updateReportedVisibilityLocked();
        }
    }

    /* access modifiers changed from: package-private */
    public void hide(SurfaceControl.Transaction transaction, String reason) {
        if (allowSpecialWindowHide()) {
            if ((this.mWin.mAppToken == null || !this.mWin.mAppToken.mIsCastMode) && !this.mLastHidden) {
                this.mLastHidden = true;
                markPreservedSurfaceForDestroy();
                WindowSurfaceController windowSurfaceController = this.mSurfaceController;
                if (windowSurfaceController != null) {
                    windowSurfaceController.hide(transaction, reason);
                }
            }
        }
    }

    private boolean allowSpecialWindowHide() {
        if (!this.mIsWallpaper || !this.mService.mMiuiGestureController.keepWallpaperShowing() || this.mSurfaceController == null) {
            return true;
        }
        this.mLastHidden = false;
        showSurfaceRobustlyLocked();
        return false;
    }

    /* access modifiers changed from: package-private */
    public void hide(String reason) {
        hide(this.mTmpTransaction, reason);
        SurfaceControl.mergeToGlobalTransaction(this.mTmpTransaction);
    }

    /* access modifiers changed from: package-private */
    public boolean finishDrawingLocked() {
        if (this.mWin.mAttrs.type == 3) {
        }
        if (this.mDrawState != 1) {
            return false;
        }
        this.mDrawState = 2;
        return true;
    }

    /* access modifiers changed from: package-private */
    public boolean commitFinishDrawingLocked() {
        int i = this.mDrawState;
        if (i != 2 && i != 3) {
            return false;
        }
        this.mDrawState = 3;
        AppWindowToken atoken = this.mWin.mAppToken;
        if (atoken == null || atoken.canShowWindows() || this.mWin.mAttrs.type == 3) {
            return this.mWin.performShowLocked();
        }
        return false;
    }

    /* access modifiers changed from: package-private */
    public void preserveSurfaceLocked() {
        if (this.mDestroyPreservedSurfaceUponRedraw) {
            this.mSurfaceDestroyDeferred = false;
            destroySurfaceLocked();
            this.mSurfaceDestroyDeferred = true;
            return;
        }
        WindowSurfaceController windowSurfaceController = this.mSurfaceController;
        if (windowSurfaceController != null) {
            windowSurfaceController.mSurfaceControl.setLayer(1);
        }
        this.mDestroyPreservedSurfaceUponRedraw = true;
        this.mSurfaceDestroyDeferred = true;
        destroySurfaceLocked();
    }

    /* access modifiers changed from: package-private */
    public void destroyPreservedSurfaceLocked() {
        if (this.mDestroyPreservedSurfaceUponRedraw) {
            if (!(this.mSurfaceController == null || this.mPendingDestroySurface == null || (this.mWin.mAppToken != null && this.mWin.mAppToken.isRelaunching()))) {
                this.mReparentTransaction.reparentChildren(this.mPendingDestroySurface.mSurfaceControl, this.mSurfaceController.mSurfaceControl.getHandle()).apply();
            }
            destroyDeferredSurfaceLocked();
            this.mDestroyPreservedSurfaceUponRedraw = false;
        }
    }

    /* access modifiers changed from: package-private */
    public void markPreservedSurfaceForDestroy() {
        if (this.mDestroyPreservedSurfaceUponRedraw && !this.mService.mDestroyPreservedSurface.contains(this.mWin)) {
            this.mService.mDestroyPreservedSurface.add(this.mWin);
        }
    }

    private int getLayerStack() {
        return this.mWin.getDisplayContent().getDisplay().getLayerStack();
    }

    /* access modifiers changed from: package-private */
    public void resetDrawState() {
        this.mDrawState = 1;
        if (this.mWin.mAppToken != null) {
            if (!this.mWin.mAppToken.isSelfAnimating()) {
                this.mWin.mAppToken.clearAllDrawn();
            } else {
                this.mWin.mAppToken.deferClearAllDrawn = true;
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void setCastMode(boolean enable) {
        WindowSurfaceController windowSurfaceController = this.mSurfaceController;
        if (windowSurfaceController != null) {
            windowSurfaceController.setCastMode(enable);
            this.mWin.reportEnteredCastMode(enable);
        }
    }

    /* access modifiers changed from: package-private */
    public void setCastMainWindow(boolean enable) {
        this.mService.setCastWindow(this.mWin);
        if (enable) {
            this.mWin.mAttrs.extraFlags |= DumpState.DUMP_APEX;
            return;
        }
        this.mWin.mAttrs.extraFlags &= -33554433;
    }

    /* JADX INFO: finally extract failed */
    /* access modifiers changed from: package-private */
    /* JADX WARNING: Code restructure failed: missing block: B:79:0x017c, code lost:
        r13 = r10;
        r21 = r1;
        r2 = "WindowManager";
     */
    /* JADX WARNING: Code restructure failed: missing block: B:80:0x0184, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:81:0x0185, code lost:
        r22 = "WindowManager";
        r13 = r10;
        r21 = r1;
     */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Removed duplicated region for block: B:49:0x00e2 A[Catch:{ OutOfResourcesException -> 0x016b, Exception -> 0x0169 }] */
    /* JADX WARNING: Removed duplicated region for block: B:50:0x00e4 A[Catch:{ OutOfResourcesException -> 0x016b, Exception -> 0x0169 }] */
    /* JADX WARNING: Removed duplicated region for block: B:53:0x00f0 A[Catch:{ OutOfResourcesException -> 0x016b, Exception -> 0x0169 }] */
    /* JADX WARNING: Removed duplicated region for block: B:59:0x0114 A[Catch:{ all -> 0x0162 }] */
    /* JADX WARNING: Removed duplicated region for block: B:62:0x0132 A[Catch:{ all -> 0x0162 }] */
    /* JADX WARNING: Removed duplicated region for block: B:65:0x013e A[Catch:{ all -> 0x0162 }] */
    /* JADX WARNING: Removed duplicated region for block: B:80:0x0184 A[ExcHandler: Exception (e java.lang.Exception), Splitter:B:14:0x0063] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public com.android.server.wm.WindowSurfaceController createSurfaceLocked(int r24, int r25) {
        /*
            r23 = this;
            r11 = r23
            java.lang.String r12 = "createSurfaceLocked"
            java.lang.String r13 = "WindowManager"
            com.android.server.wm.WindowState r14 = r11.mWin
            com.android.server.wm.WindowSurfaceController r0 = r11.mSurfaceController
            if (r0 == 0) goto L_0x000d
            return r0
        L_0x000d:
            r15 = 0
            r11.mChildrenDetached = r15
            com.android.server.wm.WindowState r0 = r11.mWin
            android.view.WindowManager$LayoutParams r0 = r0.mAttrs
            int r0 = r0.privateFlags
            r1 = 1048576(0x100000, float:1.469368E-39)
            r0 = r0 & r1
            if (r0 != 0) goto L_0x002a
            com.android.server.wm.WindowState r0 = r11.mWin
            android.view.WindowManager$LayoutParams r0 = r0.mAttrs
            int r0 = r0.extraFlags
            r1 = 8388608(0x800000, float:1.17549435E-38)
            r0 = r0 & r1
            if (r0 == 0) goto L_0x0027
            goto L_0x002a
        L_0x0027:
            r16 = r24
            goto L_0x002f
        L_0x002a:
            r0 = 441731(0x6bd83, float:6.18997E-40)
            r16 = r0
        L_0x002f:
            r14.setHasSurface(r15)
            r23.resetDrawState()
            com.android.server.wm.WindowManagerService r0 = r11.mService
            r0.makeWindowFreezingScreenIfNeededLocked(r14)
            r0 = 4
            android.view.WindowManager$LayoutParams r10 = r14.mAttrs
            com.android.server.wm.WindowManagerService r1 = r11.mService
            boolean r1 = r1.isSecureLocked(r14)
            if (r1 == 0) goto L_0x0049
            r0 = r0 | 128(0x80, float:1.794E-43)
            r1 = r0
            goto L_0x004a
        L_0x0049:
            r1 = r0
        L_0x004a:
            android.graphics.Rect r0 = r11.mTmpSize
            r11.calculateSurfaceBounds(r14, r10, r0)
            android.graphics.Rect r0 = r11.mTmpSize
            int r17 = r0.width()
            android.graphics.Rect r0 = r11.mTmpSize
            int r18 = r0.height()
            android.graphics.Rect r0 = r11.mLastClipRect
            r0.set(r15, r15, r15, r15)
            r19 = 0
            r9 = 1
            int r0 = r10.flags     // Catch:{ OutOfResourcesException -> 0x0195, Exception -> 0x0184 }
            r20 = 16777216(0x1000000, float:2.3509887E-38)
            r0 = r0 & r20
            if (r0 == 0) goto L_0x006d
            r0 = r9
            goto L_0x006e
        L_0x006d:
            r0 = r15
        L_0x006e:
            if (r0 == 0) goto L_0x0072
            r2 = -3
            goto L_0x0074
        L_0x0072:
            int r2 = r10.format     // Catch:{ OutOfResourcesException -> 0x017b, Exception -> 0x0184 }
        L_0x0074:
            r8 = r2
            int r2 = r10.format     // Catch:{ OutOfResourcesException -> 0x017b, Exception -> 0x0184 }
            boolean r2 = android.graphics.PixelFormat.formatHasAlpha(r2)     // Catch:{ OutOfResourcesException -> 0x017b, Exception -> 0x0184 }
            if (r2 != 0) goto L_0x00af
            android.graphics.Rect r2 = r10.surfaceInsets     // Catch:{ OutOfResourcesException -> 0x00a8, Exception -> 0x00a0 }
            int r2 = r2.left     // Catch:{ OutOfResourcesException -> 0x00a8, Exception -> 0x00a0 }
            if (r2 != 0) goto L_0x00af
            android.graphics.Rect r2 = r10.surfaceInsets     // Catch:{ OutOfResourcesException -> 0x00a8, Exception -> 0x00a0 }
            int r2 = r2.top     // Catch:{ OutOfResourcesException -> 0x00a8, Exception -> 0x00a0 }
            if (r2 != 0) goto L_0x00af
            android.graphics.Rect r2 = r10.surfaceInsets     // Catch:{ OutOfResourcesException -> 0x00a8, Exception -> 0x00a0 }
            int r2 = r2.right     // Catch:{ OutOfResourcesException -> 0x00a8, Exception -> 0x00a0 }
            if (r2 != 0) goto L_0x00af
            android.graphics.Rect r2 = r10.surfaceInsets     // Catch:{ OutOfResourcesException -> 0x00a8, Exception -> 0x00a0 }
            int r2 = r2.bottom     // Catch:{ OutOfResourcesException -> 0x00a8, Exception -> 0x00a0 }
            if (r2 != 0) goto L_0x00af
            boolean r2 = r14.isDragResizing()     // Catch:{ OutOfResourcesException -> 0x00a8, Exception -> 0x00a0 }
            if (r2 != 0) goto L_0x00af
            r1 = r1 | 1024(0x400, float:1.435E-42)
            r21 = r1
            goto L_0x00b1
        L_0x00a0:
            r0 = move-exception
            r21 = r1
            r22 = r13
            r13 = r10
            goto L_0x018a
        L_0x00a8:
            r0 = move-exception
            r21 = r1
            r2 = r13
            r13 = r10
            goto L_0x019a
        L_0x00af:
            r21 = r1
        L_0x00b1:
            com.android.server.wm.WindowSurfaceController r7 = new com.android.server.wm.WindowSurfaceController     // Catch:{ OutOfResourcesException -> 0x0174, Exception -> 0x016f }
            com.android.server.wm.Session r1 = r11.mSession     // Catch:{ OutOfResourcesException -> 0x0174, Exception -> 0x016f }
            android.view.SurfaceSession r2 = r1.mSurfaceSession     // Catch:{ OutOfResourcesException -> 0x0174, Exception -> 0x016f }
            java.lang.CharSequence r1 = r10.getTitle()     // Catch:{ OutOfResourcesException -> 0x0174, Exception -> 0x016f }
            java.lang.String r3 = r1.toString()     // Catch:{ OutOfResourcesException -> 0x0174, Exception -> 0x016f }
            r1 = r7
            r4 = r17
            r5 = r18
            r6 = r8
            r15 = r7
            r7 = r21
            r24 = r0
            r0 = r8
            r8 = r23
            r22 = r13
            r13 = r9
            r9 = r16
            r13 = r10
            r10 = r25
            r1.<init>(r2, r3, r4, r5, r6, r7, r8, r9, r10)     // Catch:{ OutOfResourcesException -> 0x016b, Exception -> 0x0169 }
            r11.mSurfaceController = r15     // Catch:{ OutOfResourcesException -> 0x016b, Exception -> 0x0169 }
            com.android.server.wm.WindowSurfaceController r1 = r11.mSurfaceController     // Catch:{ OutOfResourcesException -> 0x016b, Exception -> 0x0169 }
            int r2 = r13.privateFlags     // Catch:{ OutOfResourcesException -> 0x016b, Exception -> 0x0169 }
            r2 = r2 & r20
            if (r2 == 0) goto L_0x00e4
            r2 = 1
            goto L_0x00e5
        L_0x00e4:
            r2 = 0
        L_0x00e5:
            r1.setColorSpaceAgnostic(r2)     // Catch:{ OutOfResourcesException -> 0x016b, Exception -> 0x0169 }
            com.android.server.wm.WindowState r1 = r11.mWin     // Catch:{ OutOfResourcesException -> 0x016b, Exception -> 0x0169 }
            boolean r1 = r1.inFreeformWindowingMode()     // Catch:{ OutOfResourcesException -> 0x016b, Exception -> 0x0169 }
            if (r1 == 0) goto L_0x00fe
            com.android.server.wm.WindowSurfaceController r1 = r11.mSurfaceController     // Catch:{ OutOfResourcesException -> 0x016b, Exception -> 0x0169 }
            android.view.SurfaceControl r1 = r1.mSurfaceControl     // Catch:{ OutOfResourcesException -> 0x016b, Exception -> 0x0169 }
            float r2 = android.util.MiuiMultiWindowUtils.FREEFORM_ROUND_CORNER     // Catch:{ OutOfResourcesException -> 0x016b, Exception -> 0x0169 }
            r1.setCornerRadius(r2)     // Catch:{ OutOfResourcesException -> 0x016b, Exception -> 0x0169 }
            com.android.server.wm.WindowManagerService r1 = r11.mService     // Catch:{ OutOfResourcesException -> 0x016b, Exception -> 0x0169 }
            r1.createFreeformSurfaceCompleted(r14)     // Catch:{ OutOfResourcesException -> 0x016b, Exception -> 0x0169 }
        L_0x00fe:
            r1 = 0
            r11.setOffsetPositionForStackResize(r1)     // Catch:{ OutOfResourcesException -> 0x016b, Exception -> 0x0169 }
            r11.mSurfaceFormat = r0     // Catch:{ OutOfResourcesException -> 0x016b, Exception -> 0x0169 }
            r1 = 1
            r14.setHasSurface(r1)     // Catch:{ OutOfResourcesException -> 0x016b, Exception -> 0x0169 }
            com.android.server.wm.WindowManagerService r0 = r11.mService
            r0.openSurfaceTransaction()
            int r0 = r13.flags     // Catch:{ all -> 0x0162 }
            r0 = r0 & 4
            if (r0 == 0) goto L_0x012c
            com.android.server.wm.WindowSurfaceController r0 = r11.mSurfaceController     // Catch:{ all -> 0x0162 }
            r1 = 1
            r0.setBlur(r1)     // Catch:{ all -> 0x0162 }
            com.android.server.wm.WindowSurfaceController r0 = r11.mSurfaceController     // Catch:{ all -> 0x0162 }
            android.view.WindowManager$LayoutParams r1 = r14.mAttrs     // Catch:{ all -> 0x0162 }
            float r1 = r1.blurRatio     // Catch:{ all -> 0x0162 }
            r0.setBlurRatioInTransaction(r1)     // Catch:{ all -> 0x0162 }
            com.android.server.wm.WindowSurfaceController r0 = r11.mSurfaceController     // Catch:{ all -> 0x0162 }
            android.view.WindowManager$LayoutParams r1 = r14.mAttrs     // Catch:{ all -> 0x0162 }
            int r1 = r1.blurMode     // Catch:{ all -> 0x0162 }
            r0.setBlurModeInTransaction(r1)     // Catch:{ all -> 0x0162 }
        L_0x012c:
            int r0 = r13.flags     // Catch:{ all -> 0x0162 }
            r0 = r0 & 4096(0x1000, float:5.74E-42)
            if (r0 == 0) goto L_0x0138
            com.android.server.wm.WindowSurfaceController r0 = r11.mSurfaceController     // Catch:{ all -> 0x0162 }
            r1 = 1
            r0.setRecordHide(r1)     // Catch:{ all -> 0x0162 }
        L_0x0138:
            int r0 = r13.privateFlags     // Catch:{ all -> 0x0162 }
            r0 = r0 & 8
            if (r0 == 0) goto L_0x0156
            com.android.server.wm.WindowSurfaceController r0 = r11.mSurfaceController     // Catch:{ all -> 0x0162 }
            r1 = 1
            r0.setBlurCurrent(r1)     // Catch:{ all -> 0x0162 }
            com.android.server.wm.WindowSurfaceController r0 = r11.mSurfaceController     // Catch:{ all -> 0x0162 }
            android.view.WindowManager$LayoutParams r1 = r14.mAttrs     // Catch:{ all -> 0x0162 }
            int r1 = r1.blurMode     // Catch:{ all -> 0x0162 }
            r0.setBlurModeInTransaction(r1)     // Catch:{ all -> 0x0162 }
            com.android.server.wm.WindowSurfaceController r0 = r11.mSurfaceController     // Catch:{ all -> 0x0162 }
            android.view.WindowManager$LayoutParams r1 = r14.mAttrs     // Catch:{ all -> 0x0162 }
            float r1 = r1.blurRatio     // Catch:{ all -> 0x0162 }
            r0.setBlurRatioInTransaction(r1)     // Catch:{ all -> 0x0162 }
        L_0x0156:
            com.android.server.wm.WindowManagerService r0 = r11.mService
            r0.closeSurfaceTransaction(r12)
            r1 = 1
            r11.mLastHidden = r1
            com.android.server.wm.WindowSurfaceController r0 = r11.mSurfaceController
            return r0
        L_0x0162:
            r0 = move-exception
            com.android.server.wm.WindowManagerService r1 = r11.mService
            r1.closeSurfaceTransaction(r12)
            throw r0
        L_0x0169:
            r0 = move-exception
            goto L_0x018a
        L_0x016b:
            r0 = move-exception
            r2 = r22
            goto L_0x019a
        L_0x016f:
            r0 = move-exception
            r22 = r13
            r13 = r10
            goto L_0x018a
        L_0x0174:
            r0 = move-exception
            r22 = r13
            r13 = r10
            r2 = r22
            goto L_0x019a
        L_0x017b:
            r0 = move-exception
            r22 = r13
            r13 = r10
            r21 = r1
            r2 = r22
            goto L_0x019a
        L_0x0184:
            r0 = move-exception
            r22 = r13
            r13 = r10
            r21 = r1
        L_0x018a:
            java.lang.String r1 = "Exception creating surface (parent dead?)"
            r2 = r22
            android.util.Slog.e(r2, r1, r0)
            r1 = 0
            r11.mDrawState = r1
            return r19
        L_0x0195:
            r0 = move-exception
            r2 = r13
            r13 = r10
            r21 = r1
        L_0x019a:
            java.lang.String r1 = "OutOfResourcesException creating surface"
            android.util.Slog.w(r2, r1)
            com.android.server.wm.WindowManagerService r1 = r11.mService
            com.android.server.wm.RootWindowContainer r1 = r1.mRoot
            java.lang.String r2 = "create"
            r3 = 1
            r1.reclaimSomeSurfaceMemory(r11, r2, r3)
            r1 = 0
            r11.mDrawState = r1
            return r19
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.wm.WindowStateAnimator.createSurfaceLocked(int, int):com.android.server.wm.WindowSurfaceController");
    }

    private void calculateSurfaceBounds(WindowState w, WindowManager.LayoutParams attrs, Rect outSize) {
        outSize.setEmpty();
        if ((attrs.flags & 16384) != 0) {
            outSize.right = w.mRequestedWidth;
            outSize.bottom = w.mRequestedHeight;
        } else if (w.isDragResizing()) {
            DisplayInfo displayInfo = w.getDisplayInfo();
            outSize.right = displayInfo.logicalWidth;
            outSize.bottom = displayInfo.logicalHeight;
        } else {
            w.getCompatFrameSize(outSize);
        }
        if (outSize.width() < 1) {
            outSize.right = 1;
        }
        if (outSize.height() < 1) {
            outSize.bottom = 1;
        }
        outSize.inset(-attrs.surfaceInsets.left, -attrs.surfaceInsets.top, -attrs.surfaceInsets.right, -attrs.surfaceInsets.bottom);
    }

    /* access modifiers changed from: package-private */
    public boolean hasSurface() {
        WindowSurfaceController windowSurfaceController = this.mSurfaceController;
        return windowSurfaceController != null && windowSurfaceController.hasSurface();
    }

    /* access modifiers changed from: package-private */
    public void destroySurfaceLocked() {
        AppWindowToken wtoken = this.mWin.mAppToken;
        if (wtoken != null && this.mWin == wtoken.startingWindow) {
            wtoken.startingDisplayed = false;
        }
        if (this.mSurfaceController != null) {
            if (!this.mDestroyPreservedSurfaceUponRedraw) {
                this.mWin.mHidden = true;
            }
            try {
                if (!this.mSurfaceDestroyDeferred) {
                    destroySurface();
                } else if (!(this.mSurfaceController == null || this.mPendingDestroySurface == this.mSurfaceController)) {
                    if (this.mPendingDestroySurface != null) {
                        this.mPendingDestroySurface.destroyNotInTransaction();
                    }
                    this.mPendingDestroySurface = this.mSurfaceController;
                }
                if (!this.mDestroyPreservedSurfaceUponRedraw) {
                    this.mWallpaperControllerLocked.hideWallpapers(this.mWin);
                }
            } catch (RuntimeException e) {
                Slog.w("WindowManager", "Exception thrown when destroying Window " + this + " surface " + this.mSurfaceController + " session " + this.mSession + ": " + e.toString());
            }
            this.mWin.setHasSurface(false);
            WindowSurfaceController windowSurfaceController = this.mSurfaceController;
            if (windowSurfaceController != null) {
                windowSurfaceController.setShown(false);
            }
            this.mSurfaceController = null;
            this.mDrawState = 0;
        }
    }

    /* access modifiers changed from: package-private */
    public void destroyDeferredSurfaceLocked() {
        try {
            if (this.mPendingDestroySurface != null) {
                this.mPendingDestroySurface.destroyNotInTransaction();
                if (!this.mDestroyPreservedSurfaceUponRedraw) {
                    this.mWallpaperControllerLocked.hideWallpapers(this.mWin);
                }
            }
        } catch (RuntimeException e) {
            Slog.w("WindowManager", "Exception thrown when destroying Window " + this + " surface " + this.mPendingDestroySurface + " session " + this.mSession + ": " + e.toString());
        }
        this.mSurfaceDestroyDeferred = false;
        this.mPendingDestroySurface = null;
    }

    /* access modifiers changed from: package-private */
    public void computeShownFrameLocked() {
        ScreenRotationAnimation screenRotationAnimation = this.mAnimator.getScreenRotationAnimationLocked(this.mWin.getDisplayId());
        boolean screenAnimation = screenRotationAnimation != null && screenRotationAnimation.isAnimating() && (this.mWin.mForceSeamlesslyRotate ^ true);
        if (screenAnimation) {
            Rect frame = this.mWin.getFrameLw();
            float[] tmpFloats = this.mService.mTmpFloats;
            Matrix tmpMatrix = this.mWin.mTmpMatrix;
            if (screenRotationAnimation.isRotating()) {
                float w = (float) frame.width();
                float h = (float) frame.height();
                if (w < 1.0f || h < 1.0f) {
                    tmpMatrix.reset();
                } else {
                    tmpMatrix.setScale((2.0f / w) + 1.0f, (2.0f / h) + 1.0f, w / 2.0f, h / 2.0f);
                }
            } else {
                tmpMatrix.reset();
            }
            if (!this.mWin.inFreeformWindowingMode() || !MiuiMultiWindowUtils.mIsMiniFreeformMode) {
                tmpMatrix.postScale(this.mWin.mGlobalScale, this.mWin.mGlobalScale);
            } else {
                tmpMatrix.postScale(1.0f, 1.0f);
            }
            tmpMatrix.postTranslate((float) this.mWin.mAttrs.surfaceInsets.left, (float) this.mWin.mAttrs.surfaceInsets.top);
            this.mHaveMatrix = true;
            tmpMatrix.getValues(tmpFloats);
            this.mDsDx = tmpFloats[0];
            this.mDtDx = tmpFloats[3];
            this.mDtDy = tmpFloats[1];
            this.mDsDy = tmpFloats[4];
            this.mShownAlpha = this.mAlpha;
            if ((!this.mService.mLimitedAlphaCompositing || !PixelFormat.formatHasAlpha(this.mWin.mAttrs.format) || this.mWin.isIdentityMatrix(this.mDsDx, this.mDtDx, this.mDtDy, this.mDsDy)) && screenAnimation) {
                this.mShownAlpha *= screenRotationAnimation.getEnterTransformation().getAlpha();
            }
        } else if ((!this.mIsWallpaper || !this.mService.mRoot.mWallpaperActionPending) && !this.mWin.isDragResizeChanged()) {
            this.mShownAlpha = this.mAlpha;
            this.mHaveMatrix = false;
            if (!this.mWin.inFreeformWindowingMode() || !MiuiMultiWindowUtils.mIsMiniFreeformMode) {
                this.mDsDx = this.mWin.mGlobalScale;
                this.mDtDx = 0.0f;
                this.mDtDy = 0.0f;
                this.mDsDy = this.mWin.mGlobalScale;
                return;
            }
            this.mDsDx = 1.0f;
            this.mDtDx = 0.0f;
            this.mDtDy = 0.0f;
            this.mDsDy = 1.0f;
        }
    }

    private boolean calculateCrop(Rect clipRect) {
        WindowState w = this.mWin;
        DisplayContent displayContent = w.getDisplayContent();
        clipRect.setEmpty();
        if (displayContent == null) {
            return false;
        }
        if ((w.getWindowConfiguration().tasksAreFloating() && !w.inFreeformWindowingMode()) || w.mForceSeamlesslyRotate || w.mAttrs.type == 2013) {
            return false;
        }
        w.calculatePolicyCrop(this.mSystemDecorRect);
        clipRect.set(this.mSystemDecorRect);
        w.expandForSurfaceInsets(clipRect);
        clipRect.offset(w.mAttrs.surfaceInsets.left, w.mAttrs.surfaceInsets.top);
        w.transformClipRectFromScreenToSurfaceSpace(clipRect);
        return true;
    }

    private void applyCrop(Rect clipRect, boolean recoveringMemory) {
        if (clipRect == null) {
            this.mSurfaceController.clearCropInTransaction(recoveringMemory);
        } else if (!clipRect.equals(this.mLastClipRect)) {
            this.mLastClipRect.set(clipRect);
            this.mSurfaceController.setCropInTransaction(clipRect, recoveringMemory);
        }
    }

    /* access modifiers changed from: package-private */
    public void setSurfaceBoundariesLocked(boolean recoveringMemory) {
        boolean wasForceScaled;
        Rect clipRect;
        Rect clipRect2;
        boolean allowStretching;
        Rect clipRect3;
        float th;
        boolean z = recoveringMemory;
        if (this.mSurfaceController != null) {
            WindowState w = this.mWin;
            WindowManager.LayoutParams attrs = this.mWin.getAttrs();
            Task task = w.getTask();
            calculateSurfaceBounds(w, attrs, this.mTmpSize);
            this.mExtraHScale = 1.0f;
            this.mExtraVScale = 1.0f;
            boolean wasForceScaled2 = this.mForceScaleUntilResize;
            boolean relayout = (w.mAppToken == null || !w.mAppToken.mIsDummyVisible) && (!w.inPinnedWindowingMode() || !w.mRelayoutCalled || w.mInRelayout);
            if (relayout) {
                this.mSurfaceResized = this.mSurfaceController.setBufferSizeInTransaction(this.mTmpSize.width(), this.mTmpSize.height(), z);
            } else {
                this.mSurfaceResized = false;
            }
            this.mForceScaleUntilResize = this.mForceScaleUntilResize && !this.mSurfaceResized;
            Rect clipRect4 = null;
            if (calculateCrop(this.mTmpClipRect)) {
                clipRect4 = this.mTmpClipRect;
            }
            float surfaceWidth = (float) this.mSurfaceController.getWidth();
            float surfaceHeight = (float) this.mSurfaceController.getHeight();
            Rect insets = attrs.surfaceInsets;
            if (isForceScaled()) {
                int hInsets = insets.left + insets.right;
                int vInsets = insets.top + insets.bottom;
                float surfaceContentWidth = surfaceWidth - ((float) hInsets);
                float surfaceContentHeight = surfaceHeight - ((float) vInsets);
                if (!this.mForceScaleUntilResize) {
                    int i = hInsets;
                    this.mSurfaceController.forceScaleableInTransaction(true);
                }
                int posX = 0;
                int posY = 0;
                int i2 = vInsets;
                wasForceScaled = wasForceScaled2;
                task.mStack.getDimBounds(this.mTmpStackBounds);
                task.mStack.getFinalAnimationSourceHintBounds(this.mTmpSourceBounds);
                if (!this.mTmpSourceBounds.isEmpty() || ((this.mWin.mLastRelayoutContentInsets.width() <= 0 && this.mWin.mLastRelayoutContentInsets.height() <= 0) || task.mStack.lastAnimatingBoundsWasToFullscreen())) {
                    allowStretching = false;
                } else {
                    this.mTmpSourceBounds.set(task.mStack.mPreAnimationBounds);
                    this.mTmpSourceBounds.inset(this.mWin.mLastRelayoutContentInsets);
                    allowStretching = true;
                }
                Rect rect = clipRect4;
                this.mTmpStackBounds.intersectUnchecked(w.getParentFrame());
                this.mTmpSourceBounds.intersectUnchecked(w.getParentFrame());
                this.mTmpAnimatingBounds.intersectUnchecked(w.getParentFrame());
                if (!this.mTmpSourceBounds.isEmpty()) {
                    task.mStack.getFinalAnimationBounds(this.mTmpAnimatingBounds);
                    float finalWidth = (float) this.mTmpAnimatingBounds.width();
                    float initialWidth = (float) this.mTmpSourceBounds.width();
                    Task task2 = task;
                    boolean z2 = relayout;
                    float tw = (surfaceContentWidth - ((float) this.mTmpStackBounds.width())) / (surfaceContentWidth - ((float) this.mTmpAnimatingBounds.width()));
                    if (Float.compare(tw, Float.NaN) == 0) {
                        tw = 0.0f;
                    }
                    float th2 = tw;
                    float f = finalWidth;
                    this.mExtraHScale = (initialWidth + ((finalWidth - initialWidth) * tw)) / initialWidth;
                    if (allowStretching) {
                        boolean z3 = allowStretching;
                        float initialHeight = (float) this.mTmpSourceBounds.height();
                        float f2 = initialWidth;
                        float f3 = th2;
                        float th3 = (surfaceContentHeight - ((float) this.mTmpStackBounds.height())) / (surfaceContentHeight - ((float) this.mTmpAnimatingBounds.height()));
                        this.mExtraVScale = (((((float) this.mTmpAnimatingBounds.height()) - initialHeight) * tw) + initialHeight) / initialHeight;
                        th = th3;
                    } else {
                        float f4 = initialWidth;
                        th = th2;
                        this.mExtraVScale = this.mExtraHScale;
                    }
                    int posX2 = 0 - ((int) ((this.mExtraHScale * tw) * ((float) this.mTmpSourceBounds.left)));
                    posY = 0 - ((int) ((this.mExtraVScale * th) * ((float) this.mTmpSourceBounds.top)));
                    clipRect3 = this.mTmpClipRect;
                    float f5 = tw;
                    clipRect3.set((int) (((float) (insets.left + this.mTmpSourceBounds.left)) * tw), (int) (((float) (insets.top + this.mTmpSourceBounds.top)) * th), insets.left + ((int) (surfaceWidth - ((surfaceWidth - ((float) this.mTmpSourceBounds.right)) * tw))), insets.top + ((int) (surfaceHeight - ((surfaceHeight - ((float) this.mTmpSourceBounds.bottom)) * th))));
                    posX = posX2;
                } else {
                    Task task3 = task;
                    boolean z4 = relayout;
                    this.mExtraHScale = ((float) this.mTmpStackBounds.width()) / surfaceContentWidth;
                    this.mExtraVScale = ((float) this.mTmpStackBounds.height()) / surfaceContentHeight;
                    clipRect3 = null;
                }
                this.mSurfaceController.setPositionInTransaction((float) Math.floor((double) ((int) (((float) (posX - ((int) (((float) attrs.x) * (1.0f - this.mExtraHScale))))) + (((float) insets.left) * (1.0f - this.mExtraHScale))))), (float) Math.floor((double) ((int) (((float) (posY - ((int) (((float) attrs.y) * (1.0f - this.mExtraVScale))))) + (((float) insets.top) * (1.0f - this.mExtraVScale))))), z);
                if (!this.mPipAnimationStarted) {
                    this.mForceScaleUntilResize = true;
                    this.mPipAnimationStarted = true;
                }
                clipRect = clipRect3;
            } else {
                Rect clipRect5 = clipRect4;
                Task task4 = task;
                wasForceScaled = wasForceScaled2;
                boolean relayout2 = relayout;
                this.mPipAnimationStarted = false;
                if (!w.mSeamlesslyRotated) {
                    int xOffset = this.mXOffset;
                    int yOffset = this.mYOffset;
                    if (!this.mOffsetPositionForStackResize) {
                        clipRect2 = clipRect5;
                    } else if (relayout2) {
                        setOffsetPositionForStackResize(false);
                        WindowSurfaceController windowSurfaceController = this.mSurfaceController;
                        windowSurfaceController.deferTransactionUntil(windowSurfaceController.getHandle(), this.mWin.getFrameNumber());
                        clipRect2 = clipRect5;
                    } else {
                        TaskStack stack = this.mWin.getStack();
                        Point point = this.mTmpPos;
                        point.x = 0;
                        point.y = 0;
                        if (stack != null) {
                            stack.getRelativeDisplayedPosition(point);
                        }
                        xOffset = -this.mTmpPos.x;
                        yOffset = -this.mTmpPos.y;
                        if (clipRect5 != null) {
                            clipRect2 = clipRect5;
                            clipRect2.right += this.mTmpPos.x;
                            clipRect2.bottom += this.mTmpPos.y;
                        } else {
                            clipRect2 = clipRect5;
                        }
                    }
                    this.mSurfaceController.setPositionInTransaction((float) xOffset, (float) yOffset, z);
                } else {
                    clipRect2 = clipRect5;
                }
                clipRect = clipRect2;
            }
            if (wasForceScaled && !this.mForceScaleUntilResize) {
                WindowSurfaceController windowSurfaceController2 = this.mSurfaceController;
                windowSurfaceController2.deferTransactionUntil(windowSurfaceController2.getHandle(), this.mWin.getFrameNumber());
                this.mSurfaceController.forceScaleableInTransaction(false);
            }
            if (!w.mSeamlesslyRotated) {
                applyCrop(clipRect, z);
                Rect rect2 = insets;
                this.mSurfaceController.setMatrixInTransaction(this.mDsDx * w.mHScale * this.mExtraHScale, this.mDtDx * w.mVScale * this.mExtraVScale, this.mDtDy * w.mHScale * this.mExtraHScale, this.mDsDy * w.mVScale * this.mExtraVScale, recoveringMemory);
            }
            if (this.mSurfaceResized) {
                this.mReportSurfaceResized = true;
                this.mAnimator.setPendingLayoutChanges(w.getDisplayId(), 4);
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void getContainerRect(Rect rect) {
        Task task = this.mWin.getTask();
        if (task != null) {
            task.getDimBounds(rect);
            return;
        }
        rect.bottom = 0;
        rect.right = 0;
        rect.top = 0;
        rect.left = 0;
    }

    /* access modifiers changed from: package-private */
    public void prepareSurfaceLocked(boolean recoveringMemory) {
        WindowState w = this.mWin;
        if (!hasSurface()) {
            if (w.getOrientationChanging() && w.isGoneForLayoutLw()) {
                w.setOrientationChanging(false);
            }
        } else if (!this.mHandleByGesture && !MiuiGestureController.isWindowDummyVisible(this.mWin)) {
            boolean displayed = false;
            computeShownFrameLocked();
            setSurfaceBoundariesLocked(recoveringMemory);
            if (this.mIsWallpaper && !w.mWallpaperVisible && !MiuiMultiWindowUtils.mIsMiniFreeformMode) {
                hide("prepareSurfaceLocked");
            } else if ((w.isParentWindowHidden() || (!w.isOnScreen() && (w.mAppToken == null || w.mAppToken.hiddenRequested || w.mAppToken != this.mService.mSaveSurfaceByKeyguardToken))) && !MiuiMultiWindowUtils.mIsMiniFreeformMode) {
                hide("prepareSurfaceLocked");
                if (!this.mService.mMiuiGestureController.keepWallpaperShowing()) {
                    this.mWallpaperControllerLocked.hideWallpapers(w);
                }
                if (w.getOrientationChanging() && w.isGoneForLayoutLw()) {
                    w.setOrientationChanging(false);
                }
            } else if (this.mLastAlpha == this.mShownAlpha && this.mLastDsDx == this.mDsDx && this.mLastDtDx == this.mDtDx && this.mLastDsDy == this.mDsDy && this.mLastDtDy == this.mDtDy && w.mLastHScale == w.mHScale && w.mLastVScale == w.mVScale && !this.mLastHidden) {
                displayed = true;
            } else {
                displayed = true;
                this.mLastAlpha = this.mShownAlpha;
                this.mLastDsDx = this.mDsDx;
                this.mLastDtDx = this.mDtDx;
                this.mLastDsDy = this.mDsDy;
                this.mLastDtDy = this.mDtDy;
                w.mLastHScale = w.mHScale;
                w.mLastVScale = w.mVScale;
                if (this.mSurfaceController.prepareToShowInTransaction(this.mShownAlpha, this.mDsDx * w.mHScale * this.mExtraHScale, this.mDtDx * w.mVScale * this.mExtraVScale, this.mDtDy * w.mHScale * this.mExtraHScale, this.mDsDy * w.mVScale * this.mExtraVScale, recoveringMemory) && this.mDrawState == 4 && this.mLastHidden) {
                    if (showSurfaceRobustlyLocked()) {
                        markPreservedSurfaceForDestroy();
                        this.mAnimator.requestRemovalOfReplacedWindows(w);
                        this.mLastHidden = false;
                        if (this.mIsWallpaper) {
                            w.dispatchWallpaperVisibility(true);
                        }
                        if (!w.getDisplayContent().getLastHasContent()) {
                            this.mAnimator.setPendingLayoutChanges(w.getDisplayId(), 8);
                        }
                    } else {
                        w.setOrientationChanging(false);
                    }
                }
                if (hasSurface()) {
                    w.mToken.hasVisible = true;
                }
            }
            if (w.getOrientationChanging()) {
                if (!w.isDrawnLw()) {
                    this.mAnimator.mBulkUpdateParams &= -5;
                    this.mAnimator.mLastWindowFreezeSource = w;
                } else {
                    w.setOrientationChanging(false);
                }
            }
            if (displayed) {
                w.mToken.hasVisible = true;
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void setTransparentRegionHintLocked(Region region) {
        WindowSurfaceController windowSurfaceController = this.mSurfaceController;
        if (windowSurfaceController == null) {
            Slog.w("WindowManager", "setTransparentRegionHint: null mSurface after mHasSurface true");
        } else {
            windowSurfaceController.setTransparentRegionHint(region);
        }
    }

    /* access modifiers changed from: package-private */
    public boolean setWallpaperOffset(int dx, int dy) {
        if (this.mXOffset == dx && this.mYOffset == dy) {
            return false;
        }
        this.mXOffset = dx;
        this.mYOffset = dy;
        try {
            this.mService.openSurfaceTransaction();
            this.mSurfaceController.setPositionInTransaction((float) dx, (float) dy, false);
            applyCrop((Rect) null, false);
        } catch (RuntimeException e) {
            Slog.w("WindowManager", "Error positioning surface of " + this.mWin + " pos=(" + dx + "," + dy + ")", e);
        } catch (Throwable th) {
        }
        this.mService.closeSurfaceTransaction("setWallpaperOffset");
        return true;
    }

    /* access modifiers changed from: package-private */
    public boolean tryChangeFormatInPlaceLocked() {
        if (this.mSurfaceController == null) {
            return false;
        }
        WindowManager.LayoutParams attrs = this.mWin.getAttrs();
        if (((attrs.flags & DumpState.DUMP_SERVICE_PERMISSIONS) != 0 ? -3 : attrs.format) != this.mSurfaceFormat) {
            return false;
        }
        setOpaqueLocked(!PixelFormat.formatHasAlpha(attrs.format));
        return true;
    }

    /* access modifiers changed from: package-private */
    public void setOpaqueLocked(boolean isOpaque) {
        WindowSurfaceController windowSurfaceController = this.mSurfaceController;
        if (windowSurfaceController != null) {
            windowSurfaceController.setOpaque(isOpaque);
        }
    }

    /* access modifiers changed from: package-private */
    public void setSecureLocked(boolean isSecure) {
        WindowSurfaceController windowSurfaceController = this.mSurfaceController;
        if (windowSurfaceController != null) {
            windowSurfaceController.setSecure(isSecure);
        }
    }

    /* access modifiers changed from: package-private */
    public void setColorSpaceAgnosticLocked(boolean agnostic) {
        WindowSurfaceController windowSurfaceController = this.mSurfaceController;
        if (windowSurfaceController != null) {
            windowSurfaceController.setColorSpaceAgnostic(agnostic);
        }
    }

    private boolean showSurfaceRobustlyLocked() {
        if (this.mWin.getWindowConfiguration().windowsAreScaleable()) {
            this.mSurfaceController.forceScaleableInTransaction(true);
        }
        if (!this.mSurfaceController.showRobustlyInTransaction()) {
            return false;
        }
        WindowSurfaceController windowSurfaceController = this.mPendingDestroySurface;
        if (windowSurfaceController != null && this.mDestroyPreservedSurfaceUponRedraw) {
            windowSurfaceController.mSurfaceControl.hide();
            this.mPendingDestroySurface.reparentChildrenInTransaction(this.mSurfaceController);
        }
        return true;
    }

    /* access modifiers changed from: package-private */
    public void applyEnterAnimationLocked() {
        int transit;
        if (!this.mWin.mSkipEnterAnimationForSeamlessReplacement) {
            if (this.mEnterAnimationPending) {
                this.mEnterAnimationPending = false;
                transit = 1;
            } else {
                transit = 3;
            }
            if (this.mAttrType != 1) {
                applyAnimationLocked(transit, true);
            }
            if (this.mService.mAccessibilityController != null) {
                this.mService.mAccessibilityController.onWindowTransitionLocked(this.mWin, transit);
            }
        }
    }

    /* access modifiers changed from: package-private */
    public boolean applyAnimationLocked(int transit, boolean isEntrance) {
        if (this.mService.isAppTransitionSkipped() && this.mWin.mAttrs.type != 2000) {
            return false;
        }
        if (this.mWin.isSelfAnimating() && this.mAnimationIsEntrance == isEntrance) {
            return true;
        }
        if (isEntrance && this.mWin.mAttrs.type == 2011) {
            this.mWin.getDisplayContent().adjustForImeIfNeeded();
            this.mWin.setDisplayLayoutNeeded();
            this.mService.mWindowPlacerLocked.requestTraversal();
        }
        Trace.traceBegin(32, "WSA#applyAnimationLocked");
        if (this.mWin.mToken.okToAnimate()) {
            int anim = this.mWin.getDisplayContent().getDisplayPolicy().selectAnimationLw(this.mWin, transit);
            int attr = -1;
            Animation a = null;
            if (anim != 0) {
                a = anim != -1 ? AnimationUtils.loadAnimation(this.mContext, anim) : null;
            } else {
                if (transit == 1) {
                    attr = 0;
                } else if (transit == 2) {
                    attr = 1;
                } else if (transit == 3) {
                    attr = 2;
                } else if (transit == 4) {
                    attr = 3;
                }
                if (attr >= 0) {
                    a = this.mWin.getDisplayContent().mAppTransition.loadAnimationAttr(this.mWin.mAttrs, attr, 0);
                }
            }
            if (a != null) {
                if (a.getClass().getName().contains("android.view.animation.AlphaAnimation")) {
                    a.setInterpolator(new AccelerateDecelerateInterpolator());
                    a.setDuration(300);
                }
                this.mWin.startAnimation(a);
                this.mAnimationIsEntrance = isEntrance;
            }
        } else {
            this.mWin.cancelAnimation();
        }
        if (!isEntrance && this.mWin.mAttrs.type == 2011) {
            this.mWin.getDisplayContent().adjustForImeIfNeeded();
        }
        Trace.traceEnd(32);
        return this.mWin.isAnimating();
    }

    /* access modifiers changed from: package-private */
    public void writeToProto(ProtoOutputStream proto, long fieldId) {
        long token = proto.start(fieldId);
        this.mLastClipRect.writeToProto(proto, 1146756268033L);
        WindowSurfaceController windowSurfaceController = this.mSurfaceController;
        if (windowSurfaceController != null) {
            windowSurfaceController.writeToProto(proto, 1146756268034L);
        }
        proto.write(1159641169923L, this.mDrawState);
        this.mSystemDecorRect.writeToProto(proto, 1146756268036L);
        proto.end(token);
    }

    public void dump(PrintWriter pw, String prefix, boolean dumpAll) {
        if (this.mAnimationIsEntrance) {
            pw.print(prefix);
            pw.print(" mAnimationIsEntrance=");
            pw.print(this.mAnimationIsEntrance);
        }
        WindowSurfaceController windowSurfaceController = this.mSurfaceController;
        if (windowSurfaceController != null) {
            windowSurfaceController.dump(pw, prefix, dumpAll);
        }
        if (dumpAll) {
            pw.print(prefix);
            pw.print("mDrawState=");
            pw.print(drawStateToString());
            pw.print(prefix);
            pw.print(" mLastHidden=");
            pw.println(this.mLastHidden);
            pw.print(prefix);
            pw.print("mSystemDecorRect=");
            this.mSystemDecorRect.printShortString(pw);
            pw.print(" mLastClipRect=");
            this.mLastClipRect.printShortString(pw);
            if (!this.mLastFinalClipRect.isEmpty()) {
                pw.print(" mLastFinalClipRect=");
                this.mLastFinalClipRect.printShortString(pw);
            }
            pw.println();
        }
        if (this.mPendingDestroySurface != null) {
            pw.print(prefix);
            pw.print("mPendingDestroySurface=");
            pw.println(this.mPendingDestroySurface);
        }
        if (this.mSurfaceResized || this.mSurfaceDestroyDeferred) {
            pw.print(prefix);
            pw.print("mSurfaceResized=");
            pw.print(this.mSurfaceResized);
            pw.print(" mSurfaceDestroyDeferred=");
            pw.println(this.mSurfaceDestroyDeferred);
        }
        if (!(this.mShownAlpha == 1.0f && this.mAlpha == 1.0f && this.mLastAlpha == 1.0f)) {
            pw.print(prefix);
            pw.print("mShownAlpha=");
            pw.print(this.mShownAlpha);
            pw.print(" mAlpha=");
            pw.print(this.mAlpha);
            pw.print(" mLastAlpha=");
            pw.println(this.mLastAlpha);
        }
        if (this.mHaveMatrix || this.mWin.mGlobalScale != 1.0f) {
            pw.print(prefix);
            pw.print("mGlobalScale=");
            pw.print(this.mWin.mGlobalScale);
            pw.print(" mDsDx=");
            pw.print(this.mDsDx);
            pw.print(" mDtDx=");
            pw.print(this.mDtDx);
            pw.print(" mDtDy=");
            pw.print(this.mDtDy);
            pw.print(" mDsDy=");
            pw.println(this.mDsDy);
        }
        pw.print(prefix);
        pw.print("mHandleByGesture=");
        pw.println(this.mHandleByGesture);
    }

    public String toString() {
        StringBuffer sb = new StringBuffer("WindowStateAnimator{");
        sb.append(Integer.toHexString(System.identityHashCode(this)));
        sb.append(' ');
        sb.append(this.mWin.mAttrs.getTitle());
        sb.append('}');
        return sb.toString();
    }

    /* access modifiers changed from: package-private */
    public void reclaimSomeSurfaceMemory(String operation, boolean secure) {
        this.mService.mRoot.reclaimSomeSurfaceMemory(this, operation, secure);
    }

    /* access modifiers changed from: package-private */
    public boolean getShown() {
        WindowSurfaceController windowSurfaceController = this.mSurfaceController;
        if (windowSurfaceController != null) {
            return windowSurfaceController.getShown();
        }
        return false;
    }

    /* access modifiers changed from: package-private */
    public void destroySurface() {
        try {
            if (this.mSurfaceController != null) {
                this.mSurfaceController.destroyNotInTransaction();
            }
        } catch (RuntimeException e) {
            Slog.w("WindowManager", "Exception thrown when destroying surface " + this + " surface " + this.mSurfaceController + " session " + this.mSession + ": " + e);
        } catch (Throwable th) {
            this.mWin.setHasSurface(false);
            this.mSurfaceController = null;
            this.mDrawState = 0;
            throw th;
        }
        this.mWin.setHasSurface(false);
        this.mSurfaceController = null;
        this.mDrawState = 0;
    }

    /* access modifiers changed from: package-private */
    public boolean isForceScaled() {
        Task task = this.mWin.getTask();
        if (task == null || !task.mStack.isForceScaled()) {
            return this.mForceScaleUntilResize;
        }
        return true;
    }

    /* access modifiers changed from: package-private */
    public void detachChildren() {
        WindowSurfaceController windowSurfaceController = this.mSurfaceController;
        if (windowSurfaceController != null) {
            windowSurfaceController.detachChildren();
        }
        this.mChildrenDetached = true;
    }

    /* access modifiers changed from: package-private */
    public void setOffsetPositionForStackResize(boolean offsetPositionForStackResize) {
        this.mOffsetPositionForStackResize = offsetPositionForStackResize;
    }
}
