package com.android.server.wm;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.util.BoostFramework;
import android.util.proto.ProtoOutputStream;
import android.view.Surface;
import android.view.SurfaceControl;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import com.android.server.job.controllers.JobStatus;
import java.io.PrintWriter;

class ScreenRotationAnimation {
    static final boolean DEBUG_STATE = false;
    static final boolean DEBUG_TRANSFORMS = false;
    static final int SCREEN_FREEZE_LAYER_BASE = 2010000;
    static final int SCREEN_FREEZE_LAYER_CUSTOM = 2010003;
    static final int SCREEN_FREEZE_LAYER_ENTER = 2010000;
    static final int SCREEN_FREEZE_LAYER_EXIT = 2010002;
    static final int SCREEN_FREEZE_LAYER_SCREENSHOT = 2010001;
    static final String TAG = "WindowManager";
    static final boolean TWO_PHASE_ANIMATION = false;
    static final boolean USE_CUSTOM_BLACK_FRAME = false;
    boolean mAnimRunning;
    final Context mContext;
    int mCurRotation;
    Rect mCurrentDisplayRect = new Rect();
    BlackFrame mCustomBlackFrame;
    final DisplayContent mDisplayContent;
    final Transformation mEnterTransformation = new Transformation();
    BlackFrame mEnteringBlackFrame;
    final Matrix mExitFrameFinalMatrix = new Matrix();
    final Transformation mExitTransformation = new Transformation();
    BlackFrame mExitingBlackFrame;
    boolean mFinishAnimReady;
    long mFinishAnimStartTime;
    Animation mFinishEnterAnimation;
    final Transformation mFinishEnterTransformation = new Transformation();
    Animation mFinishExitAnimation;
    final Transformation mFinishExitTransformation = new Transformation();
    Animation mFinishFrameAnimation;
    final Transformation mFinishFrameTransformation = new Transformation();
    boolean mForceDefaultOrientation;
    final Matrix mFrameInitialMatrix = new Matrix();
    final Transformation mFrameTransformation = new Transformation();
    long mHalfwayPoint;
    int mHeight;
    private boolean mIsPerfLockAcquired = false;
    Animation mLastRotateEnterAnimation;
    final Transformation mLastRotateEnterTransformation = new Transformation();
    Animation mLastRotateExitAnimation;
    final Transformation mLastRotateExitTransformation = new Transformation();
    Animation mLastRotateFrameAnimation;
    final Transformation mLastRotateFrameTransformation = new Transformation();
    private boolean mMoreFinishEnter;
    private boolean mMoreFinishExit;
    private boolean mMoreFinishFrame;
    private boolean mMoreRotateEnter;
    private boolean mMoreRotateExit;
    private boolean mMoreRotateFrame;
    private boolean mMoreStartEnter;
    private boolean mMoreStartExit;
    private boolean mMoreStartFrame;
    private boolean mNeedRestoreGestureLine;
    Rect mOriginalDisplayRect = new Rect();
    int mOriginalHeight;
    int mOriginalRotation;
    int mOriginalWidth;
    private BoostFramework mPerf = null;
    Animation mRotateEnterAnimation;
    final Transformation mRotateEnterTransformation = new Transformation();
    Animation mRotateExitAnimation;
    final Transformation mRotateExitTransformation = new Transformation();
    Animation mRotateFrameAnimation;
    final Transformation mRotateFrameTransformation = new Transformation();
    private final WindowManagerService mService;
    final Matrix mSnapshotFinalMatrix = new Matrix();
    final Matrix mSnapshotInitialMatrix = new Matrix();
    Animation mStartEnterAnimation;
    final Transformation mStartEnterTransformation = new Transformation();
    Animation mStartExitAnimation;
    final Transformation mStartExitTransformation = new Transformation();
    Animation mStartFrameAnimation;
    final Transformation mStartFrameTransformation = new Transformation();
    boolean mStarted;
    Surface mSurfaceBg;
    SurfaceControl mSurfaceControl;
    SurfaceControl mSurfaceControlBg;
    SurfaceControl mSurfaceControlBgCoverBgBotom;
    SurfaceControl mSurfaceControlBgCoverBgLeft;
    SurfaceControl mSurfaceControlBgCoverBgRight;
    SurfaceControl mSurfaceControlBgCoverBgTop;
    final float[] mTmpFloats = new float[9];
    final Matrix mTmpMatrix = new Matrix();
    int mWidth;
    boolean mWithinApp;

    public void printTo(String prefix, PrintWriter pw) {
        pw.print(prefix);
        pw.print("mSurface=");
        pw.print(this.mSurfaceControl);
        pw.print(" mWidth=");
        pw.print(this.mWidth);
        pw.print(" mHeight=");
        pw.println(this.mHeight);
        pw.print(prefix);
        pw.print("mExitingBlackFrame=");
        pw.println(this.mExitingBlackFrame);
        BlackFrame blackFrame = this.mExitingBlackFrame;
        if (blackFrame != null) {
            blackFrame.printTo(prefix + "  ", pw);
        }
        pw.print(prefix);
        pw.print("mEnteringBlackFrame=");
        pw.println(this.mEnteringBlackFrame);
        BlackFrame blackFrame2 = this.mEnteringBlackFrame;
        if (blackFrame2 != null) {
            blackFrame2.printTo(prefix + "  ", pw);
        }
        pw.print(prefix);
        pw.print("mCurRotation=");
        pw.print(this.mCurRotation);
        pw.print(" mOriginalRotation=");
        pw.println(this.mOriginalRotation);
        pw.print(prefix);
        pw.print("mOriginalWidth=");
        pw.print(this.mOriginalWidth);
        pw.print(" mOriginalHeight=");
        pw.println(this.mOriginalHeight);
        pw.print(prefix);
        pw.print("mStarted=");
        pw.print(this.mStarted);
        pw.print(" mAnimRunning=");
        pw.print(this.mAnimRunning);
        pw.print(" mFinishAnimReady=");
        pw.print(this.mFinishAnimReady);
        pw.print(" mFinishAnimStartTime=");
        pw.println(this.mFinishAnimStartTime);
        pw.print(prefix);
        pw.print("mStartExitAnimation=");
        pw.print(this.mStartExitAnimation);
        pw.print(" ");
        this.mStartExitTransformation.printShortString(pw);
        pw.println();
        pw.print(prefix);
        pw.print("mStartEnterAnimation=");
        pw.print(this.mStartEnterAnimation);
        pw.print(" ");
        this.mStartEnterTransformation.printShortString(pw);
        pw.println();
        pw.print(prefix);
        pw.print("mStartFrameAnimation=");
        pw.print(this.mStartFrameAnimation);
        pw.print(" ");
        this.mStartFrameTransformation.printShortString(pw);
        pw.println();
        pw.print(prefix);
        pw.print("mFinishExitAnimation=");
        pw.print(this.mFinishExitAnimation);
        pw.print(" ");
        this.mFinishExitTransformation.printShortString(pw);
        pw.println();
        pw.print(prefix);
        pw.print("mFinishEnterAnimation=");
        pw.print(this.mFinishEnterAnimation);
        pw.print(" ");
        this.mFinishEnterTransformation.printShortString(pw);
        pw.println();
        pw.print(prefix);
        pw.print("mFinishFrameAnimation=");
        pw.print(this.mFinishFrameAnimation);
        pw.print(" ");
        this.mFinishFrameTransformation.printShortString(pw);
        pw.println();
        pw.print(prefix);
        pw.print("mRotateExitAnimation=");
        pw.print(this.mRotateExitAnimation);
        pw.print(" ");
        this.mRotateExitTransformation.printShortString(pw);
        pw.println();
        pw.print(prefix);
        pw.print("mRotateEnterAnimation=");
        pw.print(this.mRotateEnterAnimation);
        pw.print(" ");
        this.mRotateEnterTransformation.printShortString(pw);
        pw.println();
        pw.print(prefix);
        pw.print("mRotateFrameAnimation=");
        pw.print(this.mRotateFrameAnimation);
        pw.print(" ");
        this.mRotateFrameTransformation.printShortString(pw);
        pw.println();
        pw.print(prefix);
        pw.print("mExitTransformation=");
        this.mExitTransformation.printShortString(pw);
        pw.println();
        pw.print(prefix);
        pw.print("mEnterTransformation=");
        this.mEnterTransformation.printShortString(pw);
        pw.println();
        pw.print(prefix);
        pw.print("mFrameTransformation=");
        this.mFrameTransformation.printShortString(pw);
        pw.println();
        pw.print(prefix);
        pw.print("mFrameInitialMatrix=");
        this.mFrameInitialMatrix.printShortString(pw);
        pw.println();
        pw.print(prefix);
        pw.print("mSnapshotInitialMatrix=");
        this.mSnapshotInitialMatrix.printShortString(pw);
        pw.print(" mSnapshotFinalMatrix=");
        this.mSnapshotFinalMatrix.printShortString(pw);
        pw.println();
        pw.print(prefix);
        pw.print("mExitFrameFinalMatrix=");
        this.mExitFrameFinalMatrix.printShortString(pw);
        pw.println();
        pw.print(prefix);
        pw.print("mForceDefaultOrientation=");
        pw.print(this.mForceDefaultOrientation);
        if (this.mForceDefaultOrientation) {
            pw.print(" mOriginalDisplayRect=");
            pw.print(this.mOriginalDisplayRect.toShortString());
            pw.print(" mCurrentDisplayRect=");
            pw.println(this.mCurrentDisplayRect.toShortString());
        }
    }

    public void writeToProto(ProtoOutputStream proto, long fieldId) {
        long token = proto.start(fieldId);
        proto.write(1133871366145L, this.mStarted);
        proto.write(1133871366146L, this.mAnimRunning);
        proto.end(token);
    }

    /* JADX WARNING: Removed duplicated region for block: B:54:0x0214  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public ScreenRotationAnimation(android.content.Context r18, com.android.server.wm.DisplayContent r19, boolean r20, boolean r21, com.android.server.wm.WindowManagerService r22) {
        /*
            r17 = this;
            r1 = r17
            r2 = r19
            java.lang.String r3 = "WindowManager"
            r17.<init>()
            r0 = 0
            r1.mPerf = r0
            r4 = 0
            r1.mIsPerfLockAcquired = r4
            android.graphics.Rect r5 = new android.graphics.Rect
            r5.<init>()
            r1.mOriginalDisplayRect = r5
            android.graphics.Rect r5 = new android.graphics.Rect
            r5.<init>()
            r1.mCurrentDisplayRect = r5
            android.view.animation.Transformation r5 = new android.view.animation.Transformation
            r5.<init>()
            r1.mStartExitTransformation = r5
            android.view.animation.Transformation r5 = new android.view.animation.Transformation
            r5.<init>()
            r1.mStartEnterTransformation = r5
            android.view.animation.Transformation r5 = new android.view.animation.Transformation
            r5.<init>()
            r1.mStartFrameTransformation = r5
            android.view.animation.Transformation r5 = new android.view.animation.Transformation
            r5.<init>()
            r1.mFinishExitTransformation = r5
            android.view.animation.Transformation r5 = new android.view.animation.Transformation
            r5.<init>()
            r1.mFinishEnterTransformation = r5
            android.view.animation.Transformation r5 = new android.view.animation.Transformation
            r5.<init>()
            r1.mFinishFrameTransformation = r5
            android.view.animation.Transformation r5 = new android.view.animation.Transformation
            r5.<init>()
            r1.mRotateExitTransformation = r5
            android.view.animation.Transformation r5 = new android.view.animation.Transformation
            r5.<init>()
            r1.mRotateEnterTransformation = r5
            android.view.animation.Transformation r5 = new android.view.animation.Transformation
            r5.<init>()
            r1.mRotateFrameTransformation = r5
            android.view.animation.Transformation r5 = new android.view.animation.Transformation
            r5.<init>()
            r1.mLastRotateExitTransformation = r5
            android.view.animation.Transformation r5 = new android.view.animation.Transformation
            r5.<init>()
            r1.mLastRotateEnterTransformation = r5
            android.view.animation.Transformation r5 = new android.view.animation.Transformation
            r5.<init>()
            r1.mLastRotateFrameTransformation = r5
            android.view.animation.Transformation r5 = new android.view.animation.Transformation
            r5.<init>()
            r1.mExitTransformation = r5
            android.view.animation.Transformation r5 = new android.view.animation.Transformation
            r5.<init>()
            r1.mEnterTransformation = r5
            android.view.animation.Transformation r5 = new android.view.animation.Transformation
            r5.<init>()
            r1.mFrameTransformation = r5
            android.graphics.Matrix r5 = new android.graphics.Matrix
            r5.<init>()
            r1.mFrameInitialMatrix = r5
            android.graphics.Matrix r5 = new android.graphics.Matrix
            r5.<init>()
            r1.mSnapshotInitialMatrix = r5
            android.graphics.Matrix r5 = new android.graphics.Matrix
            r5.<init>()
            r1.mSnapshotFinalMatrix = r5
            android.graphics.Matrix r5 = new android.graphics.Matrix
            r5.<init>()
            r1.mExitFrameFinalMatrix = r5
            android.graphics.Matrix r5 = new android.graphics.Matrix
            r5.<init>()
            r1.mTmpMatrix = r5
            r5 = 9
            float[] r5 = new float[r5]
            r1.mTmpFloats = r5
            r5 = r22
            r1.mService = r5
            r6 = r18
            r1.mContext = r6
            r1.mDisplayContent = r2
            android.graphics.Rect r7 = r1.mOriginalDisplayRect
            r2.getBounds(r7)
            android.util.BoostFramework r7 = new android.util.BoostFramework
            r7.<init>()
            r1.mPerf = r7
            android.view.Display r7 = r19.getDisplay()
            int r8 = r7.getRotation()
            android.view.DisplayInfo r9 = r19.getDisplayInfo()
            r10 = 1
            if (r20 == 0) goto L_0x00db
            r1.mForceDefaultOrientation = r10
            int r11 = r2.mBaseDisplayWidth
            int r12 = r2.mBaseDisplayHeight
            goto L_0x00df
        L_0x00db:
            int r11 = r9.logicalWidth
            int r12 = r9.logicalHeight
        L_0x00df:
            if (r8 == r10) goto L_0x00ea
            r13 = 3
            if (r8 != r13) goto L_0x00e5
            goto L_0x00ea
        L_0x00e5:
            r1.mWidth = r11
            r1.mHeight = r12
            goto L_0x00ee
        L_0x00ea:
            r1.mWidth = r12
            r1.mHeight = r11
        L_0x00ee:
            com.android.server.wm.DisplayContent r13 = r1.mDisplayContent
            android.util.ArraySet<com.android.server.wm.AppWindowToken> r13 = r13.mOpeningApps
            boolean r13 = r13.isEmpty()
            if (r13 == 0) goto L_0x0104
            com.android.server.wm.DisplayContent r13 = r1.mDisplayContent
            android.util.ArraySet<com.android.server.wm.AppWindowToken> r13 = r13.mClosingApps
            boolean r13 = r13.isEmpty()
            if (r13 == 0) goto L_0x0104
            r13 = r10
            goto L_0x0105
        L_0x0104:
            r13 = r4
        L_0x0105:
            r1.mWithinApp = r13
            r1.mNeedRestoreGestureLine = r4
            boolean r4 = r1.mWithinApp
            if (r4 == 0) goto L_0x0139
            com.android.server.wm.DisplayContent r4 = r1.mDisplayContent
            com.android.server.wm.DisplayPolicy r4 = r4.getDisplayPolicy()
            com.android.server.wm.WindowState r4 = r4.mNavigationBar
            if (r4 == 0) goto L_0x0139
            boolean r13 = r4.isVisibleLw()
            if (r13 == 0) goto L_0x0139
            com.android.server.wm.WindowStateAnimator r13 = r4.mWinAnimator
            if (r13 == 0) goto L_0x0139
            com.android.server.wm.WindowStateAnimator r13 = r4.mWinAnimator
            com.android.server.wm.WindowSurfaceController r13 = r13.mSurfaceController
            if (r13 == 0) goto L_0x0139
            android.content.Context r13 = r1.mContext
            com.android.server.wm.WindowManagerService r14 = r1.mService
            com.android.server.wm.DisplayContent r15 = r1.mDisplayContent
            com.android.server.wm.WindowStateAnimator r10 = r4.mWinAnimator
            com.android.server.wm.WindowSurfaceController r10 = r10.mSurfaceController
            com.android.server.wm.MiuiSurfaceControllerHelper r10 = r10.mSurfaceControllerHelper
            boolean r10 = com.android.server.wm.ScreenRotationAnimationInjector.hideGestureLineIfNeed(r13, r14, r15, r10)
            r1.mNeedRestoreGestureLine = r10
        L_0x0139:
            r1.mOriginalRotation = r8
            r1.mOriginalWidth = r11
            r1.mOriginalHeight = r12
            com.android.server.wm.WindowManagerService r4 = r1.mService
            com.android.server.wm.TransactionFactory r4 = r4.mTransactionFactory
            android.view.SurfaceControl$Transaction r4 = r4.make()
            android.view.SurfaceControl$Builder r10 = r19.makeOverlay()     // Catch:{ OutOfResourcesException -> 0x0202 }
            java.lang.String r13 = "ScreenshotSurface"
            android.view.SurfaceControl$Builder r10 = r10.setName(r13)     // Catch:{ OutOfResourcesException -> 0x0202 }
            int r13 = r1.mWidth     // Catch:{ OutOfResourcesException -> 0x0202 }
            int r14 = r1.mHeight     // Catch:{ OutOfResourcesException -> 0x0202 }
            android.view.SurfaceControl$Builder r10 = r10.setBufferSize(r13, r14)     // Catch:{ OutOfResourcesException -> 0x0202 }
            r13 = r21
            android.view.SurfaceControl$Builder r10 = r10.setSecure(r13)     // Catch:{ OutOfResourcesException -> 0x0200 }
            android.view.SurfaceControl$Builder r0 = r10.setParent(r0)     // Catch:{ OutOfResourcesException -> 0x0200 }
            android.view.SurfaceControl r0 = r0.build()     // Catch:{ OutOfResourcesException -> 0x0200 }
            r1.mSurfaceControl = r0     // Catch:{ OutOfResourcesException -> 0x0200 }
            com.android.server.wm.WindowManagerService r0 = r1.mService     // Catch:{ OutOfResourcesException -> 0x0200 }
            boolean r0 = r0.mIsInScreenProjection     // Catch:{ OutOfResourcesException -> 0x0200 }
            if (r0 == 0) goto L_0x0176
            android.view.SurfaceControl r0 = r1.mSurfaceControl     // Catch:{ OutOfResourcesException -> 0x0200 }
            r10 = 16777216(0x1000000, float:2.3509887E-38)
            r4.setScreenProjection(r0, r10)     // Catch:{ OutOfResourcesException -> 0x0200 }
        L_0x0176:
            com.android.server.wm.WindowManagerService r0 = r1.mService     // Catch:{ OutOfResourcesException -> 0x0200 }
            com.android.server.wm.TransactionFactory r0 = r0.mTransactionFactory     // Catch:{ OutOfResourcesException -> 0x0200 }
            android.view.SurfaceControl$Transaction r0 = r0.make()     // Catch:{ OutOfResourcesException -> 0x0200 }
            r10 = r0
            android.view.SurfaceControl r0 = r1.mSurfaceControl     // Catch:{ OutOfResourcesException -> 0x0200 }
            r14 = 1
            r10.setOverrideScalingMode(r0, r14)     // Catch:{ OutOfResourcesException -> 0x0200 }
            r10.apply(r14)     // Catch:{ OutOfResourcesException -> 0x0200 }
            int r0 = r7.getDisplayId()     // Catch:{ OutOfResourcesException -> 0x0200 }
            r14 = r0
            com.android.server.wm.WindowManagerService r0 = r1.mService     // Catch:{ OutOfResourcesException -> 0x0200 }
            com.android.server.wm.SurfaceFactory r0 = r0.mSurfaceFactory     // Catch:{ OutOfResourcesException -> 0x0200 }
            android.view.Surface r0 = r0.make()     // Catch:{ OutOfResourcesException -> 0x0200 }
            r15 = r0
            android.view.SurfaceControl r0 = r1.mSurfaceControl     // Catch:{ OutOfResourcesException -> 0x0200 }
            r15.copyFrom(r0)     // Catch:{ OutOfResourcesException -> 0x0200 }
            com.android.server.wm.WindowManagerService r0 = r1.mService     // Catch:{ OutOfResourcesException -> 0x0200 }
            android.hardware.display.DisplayManagerInternal r0 = r0.mDisplayManagerInternal     // Catch:{ OutOfResourcesException -> 0x0200 }
            android.view.SurfaceControl$ScreenshotGraphicBuffer r0 = r0.screenshot(r14)     // Catch:{ OutOfResourcesException -> 0x0200 }
            r16 = r0
            if (r16 == 0) goto L_0x01e8
            android.graphics.GraphicBuffer r0 = r16.getGraphicBuffer()     // Catch:{ RuntimeException -> 0x01af }
            r15.attachAndQueueBuffer(r0)     // Catch:{ RuntimeException -> 0x01af }
            goto L_0x01c8
        L_0x01af:
            r0 = move-exception
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ OutOfResourcesException -> 0x0200 }
            r2.<init>()     // Catch:{ OutOfResourcesException -> 0x0200 }
            java.lang.String r5 = "Failed to attach screenshot - "
            r2.append(r5)     // Catch:{ OutOfResourcesException -> 0x0200 }
            java.lang.String r5 = r0.getMessage()     // Catch:{ OutOfResourcesException -> 0x0200 }
            r2.append(r5)     // Catch:{ OutOfResourcesException -> 0x0200 }
            java.lang.String r2 = r2.toString()     // Catch:{ OutOfResourcesException -> 0x0200 }
            android.util.Slog.w(r3, r2)     // Catch:{ OutOfResourcesException -> 0x0200 }
        L_0x01c8:
            boolean r0 = r16.containsSecureLayers()     // Catch:{ OutOfResourcesException -> 0x0200 }
            if (r0 == 0) goto L_0x01d4
            android.view.SurfaceControl r0 = r1.mSurfaceControl     // Catch:{ OutOfResourcesException -> 0x0200 }
            r2 = 1
            r4.setSecure(r0, r2)     // Catch:{ OutOfResourcesException -> 0x0200 }
        L_0x01d4:
            android.view.SurfaceControl r0 = r1.mSurfaceControl     // Catch:{ OutOfResourcesException -> 0x0200 }
            r2 = 2010001(0x1eab91, float:2.816611E-39)
            r4.setLayer(r0, r2)     // Catch:{ OutOfResourcesException -> 0x0200 }
            android.view.SurfaceControl r0 = r1.mSurfaceControl     // Catch:{ OutOfResourcesException -> 0x0200 }
            r2 = 0
            r4.setAlpha(r0, r2)     // Catch:{ OutOfResourcesException -> 0x0200 }
            android.view.SurfaceControl r0 = r1.mSurfaceControl     // Catch:{ OutOfResourcesException -> 0x0200 }
            r4.show(r0)     // Catch:{ OutOfResourcesException -> 0x0200 }
            goto L_0x01fc
        L_0x01e8:
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ OutOfResourcesException -> 0x0200 }
            r0.<init>()     // Catch:{ OutOfResourcesException -> 0x0200 }
            java.lang.String r2 = "Unable to take screenshot of display "
            r0.append(r2)     // Catch:{ OutOfResourcesException -> 0x0200 }
            r0.append(r14)     // Catch:{ OutOfResourcesException -> 0x0200 }
            java.lang.String r0 = r0.toString()     // Catch:{ OutOfResourcesException -> 0x0200 }
            android.util.Slog.w(r3, r0)     // Catch:{ OutOfResourcesException -> 0x0200 }
        L_0x01fc:
            r15.destroy()     // Catch:{ OutOfResourcesException -> 0x0200 }
            goto L_0x020a
        L_0x0200:
            r0 = move-exception
            goto L_0x0205
        L_0x0202:
            r0 = move-exception
            r13 = r21
        L_0x0205:
            java.lang.String r2 = "Unable to allocate freeze surface"
            android.util.Slog.w(r3, r2, r0)
        L_0x020a:
            com.android.server.wm.WindowManagerService r0 = r1.mService
            com.android.server.wm.MiuiFreeFormGestureController r0 = r0.mMiuiFreeFormGestureController
            boolean r0 = r0.isScreenRotationDisabled()
            if (r0 != 0) goto L_0x0217
            r1.setRotation(r4, r8)
        L_0x0217:
            r4.apply()
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.wm.ScreenRotationAnimation.<init>(android.content.Context, com.android.server.wm.DisplayContent, boolean, boolean, com.android.server.wm.WindowManagerService):void");
    }

    /* access modifiers changed from: package-private */
    public boolean hasScreenshot() {
        return this.mSurfaceControl != null;
    }

    private void setSnapshotTransform(SurfaceControl.Transaction t, Matrix matrix, float alpha) {
        if (this.mSurfaceControl != null) {
            matrix.getValues(this.mTmpFloats);
            float[] fArr = this.mTmpFloats;
            float x = fArr[2];
            float y = fArr[5];
            if (this.mForceDefaultOrientation) {
                this.mDisplayContent.getBounds(this.mCurrentDisplayRect);
                x -= (float) this.mCurrentDisplayRect.left;
                y -= (float) this.mCurrentDisplayRect.top;
            }
            t.setPosition(this.mSurfaceControl, x, y);
            SurfaceControl surfaceControl = this.mSurfaceControl;
            float[] fArr2 = this.mTmpFloats;
            t.setMatrix(surfaceControl, fArr2[0], fArr2[3], fArr2[1], fArr2[4]);
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

    private void setRotation(SurfaceControl.Transaction t, int rotation) {
        this.mCurRotation = rotation;
        createRotationMatrix(DisplayContent.deltaRotation(rotation, 0), this.mWidth, this.mHeight, this.mSnapshotInitialMatrix);
        setSnapshotTransform(t, this.mSnapshotInitialMatrix, 1.0f);
    }

    public boolean setRotation(SurfaceControl.Transaction t, int rotation, long maxAnimationDuration, float animationScale, int finalWidth, int finalHeight) {
        setRotation(t, rotation);
        return false;
    }

    /* JADX WARNING: Removed duplicated region for block: B:75:0x026d  */
    /* JADX WARNING: Removed duplicated region for block: B:79:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private boolean startAnimation(android.view.SurfaceControl.Transaction r28, long r29, float r31, int r32, int r33, boolean r34, int r35, int r36) {
        /*
            r27 = this;
            r1 = r27
            r9 = r28
            r10 = r29
            r12 = r31
            r13 = r32
            r14 = r33
            r15 = r35
            r8 = r36
            android.view.SurfaceControl r0 = r1.mSurfaceControl
            r7 = 0
            if (r0 != 0) goto L_0x0016
            return r7
        L_0x0016:
            boolean r0 = r1.mStarted
            r6 = 1
            if (r0 != 0) goto L_0x0286
            com.android.server.wm.WindowManagerService r0 = r1.mService
            com.android.server.wm.MiuiFreeFormGestureController r0 = r0.mMiuiFreeFormGestureController
            boolean r0 = r0.isScreenRotationDisabled()
            if (r0 == 0) goto L_0x0028
            r2 = r6
            goto L_0x0287
        L_0x0028:
            r1.mStarted = r6
            r16 = 0
            int r0 = r1.mCurRotation
            int r2 = r1.mOriginalRotation
            int r5 = com.android.server.wm.DisplayContent.deltaRotation(r0, r2)
            if (r15 == 0) goto L_0x004d
            if (r8 == 0) goto L_0x004d
            r2 = 1
            android.content.Context r3 = r1.mContext
            android.view.animation.Animation r3 = android.view.animation.AnimationUtils.loadAnimation(r3, r15)
            r1.mRotateExitAnimation = r3
            android.content.Context r3 = r1.mContext
            android.view.animation.Animation r3 = android.view.animation.AnimationUtils.loadAnimation(r3, r8)
            r1.mRotateEnterAnimation = r3
            r20 = r2
            goto L_0x0163
        L_0x004d:
            r2 = 0
            r3 = 3
            if (r5 == r6) goto L_0x0058
            if (r5 != r3) goto L_0x0054
            goto L_0x0058
        L_0x0054:
            r20 = r2
            goto L_0x00e6
        L_0x0058:
            boolean r4 = r1.mWithinApp
            if (r4 == 0) goto L_0x00e4
            int r3 = r1.mWidth
            int r4 = r1.mHeight
            int r3 = java.lang.Math.max(r3, r4)
            com.android.server.wm.DisplayContent r4 = r1.mDisplayContent
            android.view.SurfaceControl$Builder r4 = r4.makeBackgroundLayer()
            java.lang.StringBuilder r6 = new java.lang.StringBuilder
            r6.<init>()
            java.lang.String r7 = "ScreenRotationBgLayer for - "
            r6.append(r7)
            com.android.server.wm.DisplayContent r7 = r1.mDisplayContent
            java.lang.String r7 = r7.getName()
            r6.append(r7)
            java.lang.String r6 = r6.toString()
            android.view.SurfaceControl$Builder r4 = r4.setName(r6)
            android.view.SurfaceControl$Builder r4 = r4.setBufferSize(r3, r3)
            android.view.SurfaceControl r4 = r4.build()
            r1.mSurfaceControlBg = r4
            int r4 = r3 + 500
            com.android.server.wm.DisplayContent r6 = r1.mDisplayContent
            android.view.SurfaceControl r6 = com.android.server.wm.ScreenRotationAnimationInjector.initializeBackgroundBt(r6, r9, r4)
            r1.mSurfaceControlBgCoverBgBotom = r6
            com.android.server.wm.DisplayContent r6 = r1.mDisplayContent
            android.view.SurfaceControl r6 = com.android.server.wm.ScreenRotationAnimationInjector.initializeBackgroundRt(r6, r9, r4)
            r1.mSurfaceControlBgCoverBgRight = r6
            com.android.server.wm.DisplayContent r6 = r1.mDisplayContent
            android.view.SurfaceControl r6 = com.android.server.wm.ScreenRotationAnimationInjector.initializeBackgroundLt(r6, r9, r4)
            r1.mSurfaceControlBgCoverBgLeft = r6
            com.android.server.wm.DisplayContent r6 = r1.mDisplayContent
            android.view.SurfaceControl r6 = com.android.server.wm.ScreenRotationAnimationInjector.initializeBackgroundTop(r6, r9, r4)
            r1.mSurfaceControlBgCoverBgTop = r6
            android.view.Surface r6 = new android.view.Surface
            r6.<init>()
            r1.mSurfaceBg = r6
            android.view.Surface r6 = r1.mSurfaceBg
            android.view.SurfaceControl r7 = r1.mSurfaceControlBg
            r6.copyFrom(r7)
            android.view.Surface r6 = r1.mSurfaceBg
            com.android.server.wm.ScreenRotationAnimationInjector.drawBackgroud(r6, r3)
            int r6 = r1.mWidth
            int r7 = r1.mHeight
            int r0 = r1.mOriginalRotation
            r20 = r2
            int r2 = r1.mCurRotation
            android.view.animation.Animation r0 = com.android.server.wm.ScreenRotationAnimationInjector.createRotationExit(r6, r7, r0, r2)
            r1.mRotateExitAnimation = r0
            int r0 = r1.mWidth
            int r2 = r1.mHeight
            int r6 = r1.mOriginalRotation
            int r7 = r1.mCurRotation
            android.view.animation.Animation r0 = com.android.server.wm.ScreenRotationAnimationInjector.createRotationEnter(r0, r2, r6, r7)
            r1.mRotateEnterAnimation = r0
            goto L_0x0163
        L_0x00e4:
            r20 = r2
        L_0x00e6:
            r0 = 2
            if (r5 != r0) goto L_0x00f9
            android.view.animation.Animation r0 = com.android.server.wm.ScreenRotationAnimationInjector.createRotation180Exit()
            r1.mRotateExitAnimation = r0
            android.view.animation.Animation r0 = com.android.server.wm.ScreenRotationAnimationInjector.createRotation180Enter()
            r1.mRotateEnterAnimation = r0
            r2 = 0
            r1.mWithinApp = r2
            goto L_0x0163
        L_0x00f9:
            r2 = 0
            r1.mWithinApp = r2
            if (r5 == 0) goto L_0x014c
            r2 = 1
            if (r5 == r2) goto L_0x0135
            r0 = 2
            if (r5 == r0) goto L_0x011e
            if (r5 == r3) goto L_0x0107
            goto L_0x0163
        L_0x0107:
            android.content.Context r0 = r1.mContext
            r2 = 17432861(0x10a011d, float:2.5347396E-38)
            android.view.animation.Animation r0 = android.view.animation.AnimationUtils.loadAnimation(r0, r2)
            r1.mRotateExitAnimation = r0
            android.content.Context r0 = r1.mContext
            r2 = 17432860(0x10a011c, float:2.5347393E-38)
            android.view.animation.Animation r0 = android.view.animation.AnimationUtils.loadAnimation(r0, r2)
            r1.mRotateEnterAnimation = r0
            goto L_0x0163
        L_0x011e:
            android.content.Context r0 = r1.mContext
            r2 = 17432855(0x10a0117, float:2.534738E-38)
            android.view.animation.Animation r0 = android.view.animation.AnimationUtils.loadAnimation(r0, r2)
            r1.mRotateExitAnimation = r0
            android.content.Context r0 = r1.mContext
            r2 = 17432854(0x10a0116, float:2.5347376E-38)
            android.view.animation.Animation r0 = android.view.animation.AnimationUtils.loadAnimation(r0, r2)
            r1.mRotateEnterAnimation = r0
            goto L_0x0163
        L_0x0135:
            android.content.Context r0 = r1.mContext
            r2 = 17432864(0x10a0120, float:2.5347404E-38)
            android.view.animation.Animation r0 = android.view.animation.AnimationUtils.loadAnimation(r0, r2)
            r1.mRotateExitAnimation = r0
            android.content.Context r0 = r1.mContext
            r2 = 17432863(0x10a011f, float:2.53474E-38)
            android.view.animation.Animation r0 = android.view.animation.AnimationUtils.loadAnimation(r0, r2)
            r1.mRotateEnterAnimation = r0
            goto L_0x0163
        L_0x014c:
            android.content.Context r0 = r1.mContext
            r2 = 17432852(0x10a0114, float:2.534737E-38)
            android.view.animation.Animation r0 = android.view.animation.AnimationUtils.loadAnimation(r0, r2)
            r1.mRotateExitAnimation = r0
            android.content.Context r0 = r1.mContext
            r2 = 17432851(0x10a0113, float:2.5347368E-38)
            android.view.animation.Animation r0 = android.view.animation.AnimationUtils.loadAnimation(r0, r2)
            r1.mRotateEnterAnimation = r0
        L_0x0163:
            android.view.animation.Animation r0 = r1.mRotateEnterAnimation
            int r2 = r1.mOriginalWidth
            int r3 = r1.mOriginalHeight
            r0.initialize(r13, r14, r2, r3)
            android.view.animation.Animation r0 = r1.mRotateExitAnimation
            int r2 = r1.mOriginalWidth
            int r3 = r1.mOriginalHeight
            r0.initialize(r13, r14, r2, r3)
            r2 = 0
            r1.mAnimRunning = r2
            r1.mFinishAnimReady = r2
            r2 = -1
            r1.mFinishAnimStartTime = r2
            android.view.animation.Animation r0 = r1.mRotateExitAnimation
            r0.restrictDuration(r10)
            android.view.animation.Animation r0 = r1.mRotateExitAnimation
            r0.scaleCurrentDuration(r12)
            android.view.animation.Animation r0 = r1.mRotateEnterAnimation
            r0.restrictDuration(r10)
            android.view.animation.Animation r0 = r1.mRotateEnterAnimation
            r0.scaleCurrentDuration(r12)
            com.android.server.wm.DisplayContent r0 = r1.mDisplayContent
            android.view.Display r0 = r0.getDisplay()
            int r21 = r0.getLayerStack()
            java.lang.String r7 = "Unable to allocate black surface"
            java.lang.String r6 = "WindowManager"
            if (r20 != 0) goto L_0x0232
            com.android.server.wm.BlackFrame r0 = r1.mExitingBlackFrame
            if (r0 != 0) goto L_0x0232
            boolean r0 = r1.mWithinApp
            if (r0 != 0) goto L_0x0232
            int r0 = r1.mOriginalWidth     // Catch:{ OutOfResourcesException -> 0x0229 }
            int r2 = r1.mOriginalHeight     // Catch:{ OutOfResourcesException -> 0x0229 }
            android.graphics.Matrix r3 = r1.mFrameInitialMatrix     // Catch:{ OutOfResourcesException -> 0x0229 }
            createRotationMatrix(r5, r0, r2, r3)     // Catch:{ OutOfResourcesException -> 0x0229 }
            boolean r0 = r1.mForceDefaultOrientation     // Catch:{ OutOfResourcesException -> 0x0229 }
            if (r0 == 0) goto L_0x01ca
            android.graphics.Rect r0 = r1.mCurrentDisplayRect     // Catch:{ OutOfResourcesException -> 0x01c3 }
            android.graphics.Rect r2 = r1.mOriginalDisplayRect     // Catch:{ OutOfResourcesException -> 0x01c3 }
            r18 = r2
            r22 = r5
            r5 = 0
            r17 = 1
            goto L_0x01f3
        L_0x01c3:
            r0 = move-exception
            r22 = r5
            r10 = r6
            r11 = r7
            goto L_0x022e
        L_0x01ca:
            android.graphics.Rect r0 = new android.graphics.Rect     // Catch:{ OutOfResourcesException -> 0x0229 }
            int r2 = r1.mOriginalWidth     // Catch:{ OutOfResourcesException -> 0x0229 }
            int r2 = -r2
            r17 = 1
            int r2 = r2 * 1
            int r3 = r1.mOriginalHeight     // Catch:{ OutOfResourcesException -> 0x0229 }
            int r3 = -r3
            int r3 = r3 * 1
            int r4 = r1.mOriginalWidth     // Catch:{ OutOfResourcesException -> 0x0229 }
            r19 = 2
            int r4 = r4 * 2
            r22 = r5
            int r5 = r1.mOriginalHeight     // Catch:{ OutOfResourcesException -> 0x0225 }
            int r5 = r5 * 2
            r0.<init>(r2, r3, r4, r5)     // Catch:{ OutOfResourcesException -> 0x0225 }
            android.graphics.Rect r2 = new android.graphics.Rect     // Catch:{ OutOfResourcesException -> 0x0225 }
            int r3 = r1.mOriginalWidth     // Catch:{ OutOfResourcesException -> 0x0225 }
            int r4 = r1.mOriginalHeight     // Catch:{ OutOfResourcesException -> 0x0225 }
            r5 = 0
            r2.<init>(r5, r5, r3, r4)     // Catch:{ OutOfResourcesException -> 0x0225 }
            r18 = r2
        L_0x01f3:
            com.android.server.wm.BlackFrame r4 = new com.android.server.wm.BlackFrame     // Catch:{ OutOfResourcesException -> 0x0225 }
            r19 = 2010002(0x1eab92, float:2.816613E-39)
            com.android.server.wm.DisplayContent r3 = r1.mDisplayContent     // Catch:{ OutOfResourcesException -> 0x0225 }
            boolean r2 = r1.mForceDefaultOrientation     // Catch:{ OutOfResourcesException -> 0x0225 }
            r23 = r2
            r2 = r4
            r24 = r3
            r3 = r28
            r25 = r4
            r4 = r0
            r26 = r5
            r5 = r18
            r10 = r6
            r11 = r17
            r6 = r19
            r11 = r7
            r7 = r24
            r8 = r23
            r2.<init>(r3, r4, r5, r6, r7, r8)     // Catch:{ OutOfResourcesException -> 0x0223 }
            r2 = r25
            r1.mExitingBlackFrame = r2     // Catch:{ OutOfResourcesException -> 0x0223 }
            com.android.server.wm.BlackFrame r2 = r1.mExitingBlackFrame     // Catch:{ OutOfResourcesException -> 0x0223 }
            android.graphics.Matrix r3 = r1.mFrameInitialMatrix     // Catch:{ OutOfResourcesException -> 0x0223 }
            r2.setMatrix(r9, r3)     // Catch:{ OutOfResourcesException -> 0x0223 }
            goto L_0x0236
        L_0x0223:
            r0 = move-exception
            goto L_0x022e
        L_0x0225:
            r0 = move-exception
            r10 = r6
            r11 = r7
            goto L_0x022e
        L_0x0229:
            r0 = move-exception
            r22 = r5
            r10 = r6
            r11 = r7
        L_0x022e:
            android.util.Slog.w(r10, r11, r0)
            goto L_0x0236
        L_0x0232:
            r22 = r5
            r10 = r6
            r11 = r7
        L_0x0236:
            if (r20 == 0) goto L_0x0265
            com.android.server.wm.BlackFrame r0 = r1.mEnteringBlackFrame
            if (r0 != 0) goto L_0x0265
            android.graphics.Rect r4 = new android.graphics.Rect     // Catch:{ OutOfResourcesException -> 0x0261 }
            int r0 = -r13
            r2 = 1
            int r0 = r0 * r2
            int r3 = -r14
            int r3 = r3 * r2
            int r2 = r13 * 2
            int r5 = r14 * 2
            r4.<init>(r0, r3, r2, r5)     // Catch:{ OutOfResourcesException -> 0x0261 }
            android.graphics.Rect r5 = new android.graphics.Rect     // Catch:{ OutOfResourcesException -> 0x0261 }
            r2 = 0
            r5.<init>(r2, r2, r13, r14)     // Catch:{ OutOfResourcesException -> 0x0261 }
            com.android.server.wm.BlackFrame r0 = new com.android.server.wm.BlackFrame     // Catch:{ OutOfResourcesException -> 0x0261 }
            r6 = 2010000(0x1eab90, float:2.81661E-39)
            com.android.server.wm.DisplayContent r7 = r1.mDisplayContent     // Catch:{ OutOfResourcesException -> 0x0261 }
            r8 = 0
            r2 = r0
            r3 = r28
            r2.<init>(r3, r4, r5, r6, r7, r8)     // Catch:{ OutOfResourcesException -> 0x0261 }
            r1.mEnteringBlackFrame = r0     // Catch:{ OutOfResourcesException -> 0x0261 }
            goto L_0x0265
        L_0x0261:
            r0 = move-exception
            android.util.Slog.w(r10, r11, r0)
        L_0x0265:
            boolean r0 = r1.mWithinApp
            if (r0 == 0) goto L_0x0284
            android.view.SurfaceControl r0 = r1.mSurfaceControlBg
            if (r0 == 0) goto L_0x0284
            r0.show()
            android.view.SurfaceControl r0 = r1.mSurfaceControlBgCoverBgBotom
            r0.show()
            android.view.SurfaceControl r0 = r1.mSurfaceControlBgCoverBgRight
            r0.show()
            android.view.SurfaceControl r0 = r1.mSurfaceControlBgCoverBgLeft
            r0.show()
            android.view.SurfaceControl r0 = r1.mSurfaceControlBgCoverBgTop
            r0.show()
        L_0x0284:
            r2 = 1
            return r2
        L_0x0286:
            r2 = r6
        L_0x0287:
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.wm.ScreenRotationAnimation.startAnimation(android.view.SurfaceControl$Transaction, long, float, int, int, boolean, int, int):boolean");
    }

    public boolean dismiss(SurfaceControl.Transaction t, long maxAnimationDuration, float animationScale, int finalWidth, int finalHeight, int exitAnim, int enterAnim) {
        if (this.mSurfaceControl == null) {
            return false;
        }
        if (!this.mStarted) {
            startAnimation(t, maxAnimationDuration, animationScale, finalWidth, finalHeight, true, exitAnim, enterAnim);
        }
        if (!this.mStarted) {
            return false;
        }
        this.mFinishAnimReady = true;
        return true;
    }

    public void kill() {
        SurfaceControl surfaceControl = this.mSurfaceControl;
        if (surfaceControl != null) {
            surfaceControl.remove();
            this.mSurfaceControl = null;
        }
        BlackFrame blackFrame = this.mCustomBlackFrame;
        if (blackFrame != null) {
            blackFrame.kill();
            this.mCustomBlackFrame = null;
        }
        BlackFrame blackFrame2 = this.mExitingBlackFrame;
        if (blackFrame2 != null) {
            blackFrame2.kill();
            this.mExitingBlackFrame = null;
        }
        BlackFrame blackFrame3 = this.mEnteringBlackFrame;
        if (blackFrame3 != null) {
            blackFrame3.kill();
            this.mEnteringBlackFrame = null;
        }
        Animation animation = this.mRotateExitAnimation;
        if (animation != null) {
            animation.cancel();
            this.mRotateExitAnimation = null;
        }
        Animation animation2 = this.mRotateEnterAnimation;
        if (animation2 != null) {
            animation2.cancel();
            this.mRotateEnterAnimation = null;
        }
        BoostFramework boostFramework = this.mPerf;
        if (boostFramework != null && this.mIsPerfLockAcquired) {
            boostFramework.perfLockRelease();
            this.mIsPerfLockAcquired = false;
        }
        SurfaceControl surfaceControl2 = this.mSurfaceControlBg;
        if (surfaceControl2 != null) {
            surfaceControl2.remove();
            this.mSurfaceControlBgCoverBgBotom.remove();
            this.mSurfaceControlBgCoverBgRight.remove();
            this.mSurfaceControlBgCoverBgTop.remove();
            this.mSurfaceControlBgCoverBgLeft.remove();
            this.mSurfaceControlBgCoverBgRight = null;
            this.mSurfaceControlBgCoverBgBotom = null;
            this.mSurfaceControlBgCoverBgTop = null;
            this.mSurfaceControlBgCoverBgLeft = null;
            this.mSurfaceControlBg = null;
        }
        if (this.mNeedRestoreGestureLine) {
            WindowState navgationBar = this.mDisplayContent.getDisplayPolicy().mNavigationBar;
            if (!(navgationBar == null || navgationBar.mWinAnimator == null || navgationBar.mWinAnimator.mSurfaceController == null)) {
                ScreenRotationAnimationInjector.showGestureLineIfNeed(this.mDisplayContent, this.mService, navgationBar.mWinAnimator.mSurfaceController.mSurfaceControllerHelper);
            }
            this.mNeedRestoreGestureLine = false;
        }
    }

    public boolean isAnimating() {
        return hasAnimations();
    }

    public boolean isRotating() {
        return this.mCurRotation != this.mOriginalRotation;
    }

    private boolean hasAnimations() {
        return (this.mRotateEnterAnimation == null && this.mRotateExitAnimation == null) ? false : true;
    }

    private boolean stepAnimation(long now) {
        if (now > this.mHalfwayPoint) {
            this.mHalfwayPoint = JobStatus.NO_LATEST_RUNTIME;
        }
        long j = 0;
        if (this.mFinishAnimReady && this.mFinishAnimStartTime < 0) {
            this.mFinishAnimStartTime = now;
        }
        if (this.mFinishAnimReady) {
            j = now - this.mFinishAnimStartTime;
        }
        long j2 = j;
        boolean more = false;
        this.mMoreRotateExit = false;
        Animation animation = this.mRotateExitAnimation;
        if (animation != null) {
            this.mMoreRotateExit = animation.getTransformation(now, this.mRotateExitTransformation);
        }
        this.mMoreRotateEnter = false;
        Animation animation2 = this.mRotateEnterAnimation;
        if (animation2 != null) {
            this.mMoreRotateEnter = animation2.getTransformation(now, this.mRotateEnterTransformation);
        }
        if (!this.mMoreRotateExit) {
            Animation animation3 = this.mRotateExitAnimation;
            if (animation3 != null) {
                animation3.cancel();
                this.mRotateExitAnimation = null;
                this.mRotateExitTransformation.clear();
            }
            BoostFramework boostFramework = this.mPerf;
            if (boostFramework != null && this.mIsPerfLockAcquired) {
                boostFramework.perfLockRelease();
                this.mIsPerfLockAcquired = false;
            }
        }
        if (!this.mMoreRotateEnter) {
            Animation animation4 = this.mRotateEnterAnimation;
            if (animation4 != null) {
                animation4.cancel();
                this.mRotateEnterAnimation = null;
                this.mRotateEnterTransformation.clear();
            }
            BoostFramework boostFramework2 = this.mPerf;
            if (boostFramework2 != null && this.mIsPerfLockAcquired) {
                boostFramework2.perfLockRelease();
                this.mIsPerfLockAcquired = false;
            }
        }
        this.mExitTransformation.set(this.mRotateExitTransformation);
        this.mEnterTransformation.set(this.mRotateEnterTransformation);
        if (this.mMoreRotateEnter || this.mMoreRotateExit || !this.mFinishAnimReady) {
            more = true;
        }
        if (this.mWithinApp) {
            this.mSnapshotFinalMatrix.set(this.mExitTransformation.getMatrix());
        } else {
            this.mSnapshotFinalMatrix.setConcat(this.mExitTransformation.getMatrix(), this.mSnapshotInitialMatrix);
        }
        return more;
    }

    /* access modifiers changed from: package-private */
    public void updateSurfaces(SurfaceControl.Transaction t) {
        if (this.mStarted) {
            SurfaceControl surfaceControl = this.mSurfaceControl;
            if (surfaceControl != null && !this.mMoreStartExit && !this.mMoreFinishExit && !this.mMoreRotateExit) {
                t.hide(surfaceControl);
            }
            BlackFrame blackFrame = this.mCustomBlackFrame;
            if (blackFrame != null) {
                if (this.mMoreStartFrame || this.mMoreFinishFrame || this.mMoreRotateFrame) {
                    this.mCustomBlackFrame.setMatrix(t, this.mFrameTransformation.getMatrix());
                } else {
                    blackFrame.hide(t);
                }
            }
            BlackFrame blackFrame2 = this.mExitingBlackFrame;
            if (blackFrame2 != null) {
                if (this.mMoreStartExit || this.mMoreFinishExit || this.mMoreRotateExit) {
                    this.mExitFrameFinalMatrix.setConcat(this.mExitTransformation.getMatrix(), this.mFrameInitialMatrix);
                    this.mExitingBlackFrame.setMatrix(t, this.mExitFrameFinalMatrix);
                    if (this.mForceDefaultOrientation) {
                        this.mExitingBlackFrame.setAlpha(t, this.mExitTransformation.getAlpha());
                    }
                } else {
                    blackFrame2.hide(t);
                }
            }
            BlackFrame blackFrame3 = this.mEnteringBlackFrame;
            if (blackFrame3 != null) {
                if (this.mMoreStartEnter || this.mMoreFinishEnter || this.mMoreRotateEnter) {
                    this.mEnteringBlackFrame.setMatrix(t, this.mEnterTransformation.getMatrix());
                } else {
                    blackFrame3.hide(t);
                }
            }
            t.setEarlyWakeup();
            setSnapshotTransform(t, this.mSnapshotFinalMatrix, this.mExitTransformation.getAlpha());
            SurfaceControl surfaceControl2 = this.mSurfaceControlBg;
            if (surfaceControl2 != null) {
                ScreenRotationAnimationInjector.setBackgroundTransform(t, this.mEnterTransformation, surfaceControl2, this.mSurfaceControlBgCoverBgBotom, this.mSurfaceControlBgCoverBgRight, this.mSurfaceControlBgCoverBgLeft, this.mSurfaceControlBgCoverBgTop);
            }
        }
    }

    public boolean stepAnimationLocked(long now) {
        if (!hasAnimations()) {
            this.mFinishAnimReady = false;
            return false;
        }
        if (!this.mAnimRunning) {
            Animation animation = this.mRotateEnterAnimation;
            if (animation != null) {
                animation.setStartTime(now);
            }
            Animation animation2 = this.mRotateExitAnimation;
            if (animation2 != null) {
                animation2.setStartTime(now);
            }
            this.mAnimRunning = true;
            this.mHalfwayPoint = (this.mRotateEnterAnimation.getDuration() / 2) + now;
            BoostFramework boostFramework = this.mPerf;
            if (boostFramework != null && !this.mIsPerfLockAcquired) {
                boostFramework.perfHint(4240, (String) null);
                this.mIsPerfLockAcquired = true;
            }
        }
        return stepAnimation(now);
    }

    public Transformation getEnterTransformation() {
        return this.mEnterTransformation;
    }
}
