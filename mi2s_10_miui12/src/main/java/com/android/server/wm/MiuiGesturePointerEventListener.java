package com.android.server.wm;

import android.content.res.Configuration;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.util.Slog;
import android.view.InputChannel;
import android.view.InputEvent;
import android.view.InputEventReceiver;
import android.view.SurfaceControl;
import android.view.WindowManager;
import com.android.internal.os.SomeArgs;
import com.android.server.LocalServices;
import com.android.server.MiuiFgThread;
import com.android.server.am.ProcessPolicy;
import com.android.server.inputmethod.InputMethodManagerInternal;
import com.android.server.pm.DumpState;
import com.android.server.wm.IGestureStrategy;
import com.android.server.wm.MiuiGestureStrategy;
import com.android.server.wm.WindowManagerService;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class MiuiGesturePointerEventListener implements MiuiGestureStrategy.GestureStrategyCallback {
    public static final int MIX_INITIAL_SIZE = 14;
    static final int MSG_ACTION_DOWN = 0;
    static final int MSG_ACTION_MOVE = 1;
    static final int MSG_ACTION_UP = 2;
    private static final int STATUS_DETECTED = 1;
    private static final int STATUS_FOLLOW = 2;
    private static final int STATUS_SLIDING = 3;
    private static final int STATUS_UNKNOWN = 0;
    public static final String TAG = "MiuiGesture";
    TreeSet<AppWindowToken> cancelGoHomeAppWindowToken = new TreeSet<>();
    final MiuiGestureCancelStrategy mCancelStrategy;
    TreeSet<AppWindowToken> mClosingAppTokens = new TreeSet<>();
    private final ConcurrentHashMap<WindowState, IGestureStrategy.WindowStateInfo> mClosingAppWindows = new ConcurrentHashMap<>();
    private int mConvertX;
    private int mConvertY;
    IGestureStrategy mCurrentAnimator;
    private float mCurrentScale;
    private int mCurrentX;
    private int mCurrentY;
    private float mDownX;
    private float mDownY;
    private MiuiGestureController mGestureController;
    private int mGestureStatus = 0;
    private InputEventReceiver mGesutreEventReceiver;
    final Set<WindowState> mHandleByGestureWindows = new HashSet();
    /* access modifiers changed from: private */
    public Handler mHandler;
    AppWindowToken mHomeAppToken;
    final MiuiGestureHomeStrategy mHomeStrategy;
    WindowState mHomeWindow;
    private InputEventReceiver mInputEventReceiver;
    private InputMethodManagerInternal mInputMethodManagerInternal;
    private boolean mIsEnterRecents;
    private boolean mIsFirstMove;
    private boolean mIsPortrait;
    boolean mLoadBackHomeAnimation;
    /* access modifiers changed from: private */
    public boolean mOutsideEvent;
    private int mPosX;
    private int mPosY;
    AppWindowToken mRecentsAppToken;
    final MiuiGestureRecentsStrategy mRecentsStrategy;
    private WindowState mRecentsWindow = null;
    private int mScreenHeight;
    private int mScreenWidth;
    private WindowManagerService mService;
    private final MiuiGestureAnimator mSurfaceAnimator;
    final MiuiGestureSurfaceRunner mSurfaceRunner;
    private IBinder mToken = new Binder();
    AppWindowToken mTopAppWindowToken;
    Task mTopTask;
    WindowState mTopWindow;
    final float[] tmpFloats = new float[9];
    final Matrix tmpMatrix = new Matrix();

    MiuiGesturePointerEventListener(WindowManagerService wms, MiuiGestureController controller) {
        this.mService = wms;
        this.mGestureController = controller;
        this.mHandler = new H(this.mGestureController.mHandler.getLooper());
        this.mInputMethodManagerInternal = (InputMethodManagerInternal) LocalServices.getService(InputMethodManagerInternal.class);
        this.mSurfaceAnimator = new MiuiGestureAnimator(this.mService);
        MiuiGestureDetector.init(this.mService, this, this.mGestureController);
        this.mRecentsStrategy = new MiuiGestureRecentsStrategy(this.mSurfaceAnimator, this.mService, this, this.mGestureController, this);
        this.mHomeStrategy = new MiuiGestureHomeStrategy(this.mSurfaceAnimator, this.mService, this, this.mGestureController, this);
        this.mCancelStrategy = new MiuiGestureCancelStrategy(this.mSurfaceAnimator, this.mService, this, this.mGestureController, this);
        this.mSurfaceRunner = new MiuiGestureSurfaceRunner(this.mHandler);
        synchronized (this.mService.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                updateScreenParams(this.mService.getDefaultDisplayContentLocked(), this.mService.mContext.getResources().getConfiguration());
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
    public void initMonitor(InputChannel channel) {
        this.mGesutreEventReceiver = new GestureInputEventReceiver(channel, this.mHandler.getLooper());
    }

    /* access modifiers changed from: package-private */
    public void clear() {
        InputEventReceiver inputEventReceiver = this.mGesutreEventReceiver;
        if (inputEventReceiver != null) {
            inputEventReceiver.dispose();
            this.mGesutreEventReceiver = null;
        }
    }

    class GestureInputEventReceiver extends InputEventReceiver {
        public GestureInputEventReceiver(InputChannel inputChannel, Looper looper) {
            super(inputChannel, looper);
        }

        /* JADX WARNING: Code restructure failed: missing block: B:9:0x0034, code lost:
            if (r3 != 3) goto L_0x008c;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void onInputEvent(android.view.InputEvent r8) {
            /*
                r7 = this;
                boolean r0 = r8 instanceof android.view.MotionEvent
                r1 = 1
                if (r0 == 0) goto L_0x008c
                int r0 = r8.getSource()
                r2 = 2
                r0 = r0 & r2
                if (r0 == 0) goto L_0x008c
                r0 = r8
                android.view.MotionEvent r0 = (android.view.MotionEvent) r0
                int r3 = r0.getActionMasked()
                com.android.internal.os.SomeArgs r4 = com.android.internal.os.SomeArgs.obtain()
                float r5 = r0.getRawX()
                java.lang.Float r5 = java.lang.Float.valueOf(r5)
                r4.arg1 = r5
                float r5 = r0.getRawY()
                java.lang.Float r5 = java.lang.Float.valueOf(r5)
                r4.arg2 = r5
                r5 = 0
                if (r3 == 0) goto L_0x0069
                if (r3 == r1) goto L_0x004d
                if (r3 == r2) goto L_0x0037
                r6 = 3
                if (r3 == r6) goto L_0x004d
                goto L_0x008c
            L_0x0037:
                com.android.server.wm.MiuiGesturePointerEventListener r2 = com.android.server.wm.MiuiGesturePointerEventListener.this
                boolean r2 = r2.mOutsideEvent
                if (r2 != 0) goto L_0x008c
                com.android.server.wm.MiuiGesturePointerEventListener r2 = com.android.server.wm.MiuiGesturePointerEventListener.this
                android.os.Handler r2 = r2.mHandler
                android.os.Message r2 = r2.obtainMessage(r1, r4)
                r2.sendToTarget()
                goto L_0x008c
            L_0x004d:
                com.android.server.wm.MiuiGesturePointerEventListener r6 = com.android.server.wm.MiuiGesturePointerEventListener.this
                boolean r6 = r6.mOutsideEvent
                if (r6 == 0) goto L_0x005b
                com.android.server.wm.MiuiGesturePointerEventListener r2 = com.android.server.wm.MiuiGesturePointerEventListener.this
                boolean unused = r2.mOutsideEvent = r5
                goto L_0x008c
            L_0x005b:
                com.android.server.wm.MiuiGesturePointerEventListener r5 = com.android.server.wm.MiuiGesturePointerEventListener.this
                android.os.Handler r5 = r5.mHandler
                android.os.Message r2 = r5.obtainMessage(r2, r4)
                r2.sendToTarget()
                goto L_0x008c
            L_0x0069:
                float r2 = r0.getY()
                int r2 = (int) r2
                boolean r2 = com.android.server.wm.MiuiGestureDetector.isOutSideHotSpace(r2)
                if (r2 == 0) goto L_0x007a
                com.android.server.wm.MiuiGesturePointerEventListener r2 = com.android.server.wm.MiuiGesturePointerEventListener.this
                boolean unused = r2.mOutsideEvent = r1
                goto L_0x008c
            L_0x007a:
                com.android.server.wm.MiuiGesturePointerEventListener r2 = com.android.server.wm.MiuiGesturePointerEventListener.this
                boolean unused = r2.mOutsideEvent = r5
                com.android.server.wm.MiuiGesturePointerEventListener r2 = com.android.server.wm.MiuiGesturePointerEventListener.this
                android.os.Handler r2 = r2.mHandler
                android.os.Message r2 = r2.obtainMessage(r5, r4)
                r2.sendToTarget()
            L_0x008c:
                r7.finishInputEvent(r8, r1)
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.server.wm.MiuiGesturePointerEventListener.GestureInputEventReceiver.onInputEvent(android.view.InputEvent):void");
        }
    }

    private class H extends Handler {
        private H(Looper looper) {
            super(looper);
        }

        public void handleMessage(Message msg) {
            SomeArgs args = (SomeArgs) msg.obj;
            float x = ((Float) args.arg1).floatValue();
            float y = ((Float) args.arg2).floatValue();
            args.recycle();
            FullScreenEventReporter.startActionEventTrace(msg.what, x, y);
            int i = msg.what;
            if (i == 0) {
                MiuiGesturePointerEventListener.this.onActionDown(x, y);
            } else if (i == 1) {
                MiuiGesturePointerEventListener.this.onActionMove(x, y);
            } else if (i == 2) {
                MiuiGesturePointerEventListener.this.onActionUp(x, y);
            }
            FullScreenEventReporter.endActionEventTrace(msg.what, x, y);
        }
    }

    /* access modifiers changed from: package-private */
    public void setLoadBackHomeAnimation(boolean loadBackHomeAnimation) {
        this.mLoadBackHomeAnimation = loadBackHomeAnimation;
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Code restructure failed: missing block: B:16:0x0044, code lost:
        com.android.server.wm.WindowManagerService.resetPriorityAfterLockedSection();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:17:0x0047, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onActionDown(float r5, float r6) {
        /*
            r4 = this;
            r4.mDownX = r5
            r4.mDownY = r6
            float r0 = r4.mDownX
            int r0 = (int) r0
            r4.mCurrentX = r0
            float r0 = r4.mDownY
            int r0 = (int) r0
            r4.mCurrentY = r0
            com.android.server.wm.WindowManagerService r0 = r4.mService
            com.android.server.wm.WindowManagerGlobalLock r0 = r0.mGlobalLock
            monitor-enter(r0)
            com.android.server.wm.WindowManagerService.boostPriorityForLockedSection()     // Catch:{ all -> 0x0048 }
            int r1 = r4.mCurrentX     // Catch:{ all -> 0x0048 }
            int r2 = r4.mCurrentY     // Catch:{ all -> 0x0048 }
            com.android.server.wm.MiuiGestureDetector.onActionDown(r1, r2)     // Catch:{ all -> 0x0048 }
            boolean r1 = com.android.server.wm.MiuiGestureDetector.canGestureBegin()     // Catch:{ all -> 0x0048 }
            if (r1 == 0) goto L_0x0043
            int r1 = com.android.server.wm.MiuiGestureDetector.isAppSurfaceShownLocked()     // Catch:{ all -> 0x0048 }
            r2 = 1
            if (r1 != r2) goto L_0x0035
            com.android.server.wm.MiuiGestureDetector.resetTail()     // Catch:{ all -> 0x0048 }
            r4.updateStatus(r2)     // Catch:{ all -> 0x0048 }
            monitor-exit(r0)     // Catch:{ all -> 0x0048 }
            com.android.server.wm.WindowManagerService.resetPriorityAfterLockedSection()
            return
        L_0x0035:
            if (r1 != 0) goto L_0x0043
            java.lang.String r2 = "MiuiGesture"
            java.lang.String r3 = "foreground app crash, go to home"
            android.util.Slog.w(r2, r3)     // Catch:{ all -> 0x0048 }
            com.android.server.wm.MiuiGestureHomeStrategy r2 = r4.mHomeStrategy     // Catch:{ all -> 0x0048 }
            r2.launchHome()     // Catch:{ all -> 0x0048 }
        L_0x0043:
            monitor-exit(r0)     // Catch:{ all -> 0x0048 }
            com.android.server.wm.WindowManagerService.resetPriorityAfterLockedSection()
            return
        L_0x0048:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x0048 }
            com.android.server.wm.WindowManagerService.resetPriorityAfterLockedSection()
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.wm.MiuiGesturePointerEventListener.onActionDown(float, float):void");
    }

    /* access modifiers changed from: private */
    public void onActionMove(float x, float y) {
        WindowStateAnimator winAnimator;
        this.mCurrentX = (int) x;
        this.mCurrentY = (int) y;
        MiuiGestureDetector.onActionMove(this.mCurrentX, this.mCurrentY);
        int i = this.mGestureStatus;
        if (i == 2 || (i == 1 && MiuiGestureDetector.canGestureReady())) {
            MiuiGestureDetector.resendTailMessage();
            if (this.mGestureStatus == 1) {
                gestureReady();
            }
            if (!this.mGestureController.isSuperSavePowerMode()) {
                if (MiuiGestureDetector.shouldCancelGoRecents() && this.mIsEnterRecents) {
                    this.mIsEnterRecents = false;
                    WindowState windowState = this.mRecentsWindow;
                    if (!(windowState == null || (winAnimator = windowState.mWinAnimator) == null || !winAnimator.getShown())) {
                        winAnimator.hide("hide recent window");
                    }
                    this.mGestureController.setLaunchRecentsBehind(false);
                    this.mGestureController.cancelRecents();
                }
                stepFollowAnimation(x, y);
            }
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:28:0x007f, code lost:
        r13 = r7.mAppToken;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void stepFollowAnimation(float r22, float r23) {
        /*
            r21 = this;
            r1 = r21
            r2 = 32
            java.lang.String r0 = "gesture follow animation"
            android.os.Trace.traceBegin(r2, r0)
            r21.calculateWindowPosition(r22, r23)
            java.util.concurrent.ConcurrentHashMap<com.android.server.wm.WindowState, com.android.server.wm.IGestureStrategy$WindowStateInfo> r0 = r1.mClosingAppWindows
            java.util.Set r4 = r0.entrySet()
            java.util.Iterator r0 = r4.iterator()
        L_0x0016:
            boolean r5 = r0.hasNext()
            if (r5 == 0) goto L_0x0188
            java.lang.Object r5 = r0.next()
            java.util.Map$Entry r5 = (java.util.Map.Entry) r5
            java.lang.Object r6 = r5.getKey()
            com.android.server.wm.WindowState r6 = (com.android.server.wm.WindowState) r6
            java.lang.Object r7 = r5.getValue()
            com.android.server.wm.IGestureStrategy$WindowStateInfo r7 = (com.android.server.wm.IGestureStrategy.WindowStateInfo) r7
            if (r7 != 0) goto L_0x0031
            goto L_0x0016
        L_0x0031:
            android.graphics.Rect r8 = new android.graphics.Rect
            com.android.server.wm.WindowFrames r9 = r6.mWindowFrames
            android.graphics.Rect r9 = r9.mFrame
            r8.<init>(r9)
            com.android.server.wm.WindowFrames r9 = r6.mWindowFrames
            android.graphics.Rect r9 = r9.mFrame
            int r9 = r9.bottom
            int r10 = r1.mConvertY
            r11 = 1
            if (r9 <= r10) goto L_0x0049
            r7.mNeedClip = r11
            r8.bottom = r10
        L_0x0049:
            boolean r9 = r1.mIsFirstMove
            r10 = 0
            if (r9 == 0) goto L_0x0066
            com.android.server.wm.WindowManagerService r9 = r1.mService
            com.android.server.wm.WindowManagerGlobalLock r9 = r9.mGlobalLock
            monitor-enter(r9)
            com.android.server.wm.WindowManagerService.boostPriorityForLockedSection()     // Catch:{ all -> 0x0060 }
            com.android.server.wm.WindowStateAnimator r12 = r6.mWinAnimator     // Catch:{ all -> 0x0060 }
            r12.setOpaqueLocked(r10)     // Catch:{ all -> 0x0060 }
            monitor-exit(r9)     // Catch:{ all -> 0x0060 }
            com.android.server.wm.WindowManagerService.resetPriorityAfterLockedSection()
            goto L_0x0066
        L_0x0060:
            r0 = move-exception
            monitor-exit(r9)     // Catch:{ all -> 0x0060 }
            com.android.server.wm.WindowManagerService.resetPriorityAfterLockedSection()
            throw r0
        L_0x0066:
            android.graphics.Rect r9 = r7.mNowFrame
            r9.set(r8)
            android.view.WindowManager$LayoutParams r9 = r6.mAttrs
            int r9 = r9.type
            r12 = 3
            if (r9 == r11) goto L_0x0074
            if (r9 != r12) goto L_0x0184
        L_0x0074:
            com.android.server.wm.AppWindowToken r13 = r6.mAppToken
            if (r13 != 0) goto L_0x0079
            goto L_0x0016
        L_0x0079:
            com.android.server.wm.WindowStateAnimator r14 = r6.mWinAnimator
            com.android.server.wm.WindowSurfaceController r14 = r14.mSurfaceController
            if (r9 != r12) goto L_0x009b
            com.android.server.wm.AppWindowToken r13 = r7.mAppToken
            com.android.server.wm.WindowState r12 = r13.findMainWindow(r10)
            if (r12 == 0) goto L_0x009b
            com.android.server.wm.WindowStateAnimator r15 = r12.mWinAnimator
            boolean r15 = r15.getShown()
            if (r15 == 0) goto L_0x009b
            r7.mHasShowStartingWindow = r11
            java.util.concurrent.ConcurrentHashMap<com.android.server.wm.WindowState, com.android.server.wm.IGestureStrategy$WindowStateInfo> r11 = r1.mClosingAppWindows
            r11.remove(r6)
            java.util.concurrent.ConcurrentHashMap<com.android.server.wm.WindowState, com.android.server.wm.IGestureStrategy$WindowStateInfo> r11 = r1.mClosingAppWindows
            r11.put(r12, r7)
        L_0x009b:
            if (r14 != 0) goto L_0x009f
            goto L_0x0016
        L_0x009f:
            boolean r11 = r6.inMultiWindowMode()
            r12 = 1073741824(0x40000000, float:2.0)
            if (r11 == 0) goto L_0x00d3
            android.graphics.Rect r11 = new android.graphics.Rect
            r11.<init>()
            com.android.server.wm.Task r15 = r13.getTask()
            r15.getBounds(r11)
            int r15 = r1.mConvertX
            float r15 = (float) r15
            float r2 = r1.mCurrentScale
            int r3 = r11.width()
            float r3 = (float) r3
            float r2 = r2 * r3
            float r2 = r2 / r12
            float r15 = r15 - r2
            int r2 = (int) r15
            r1.mPosX = r2
            int r2 = r1.mConvertY
            float r2 = (float) r2
            float r3 = r1.mCurrentScale
            int r12 = r11.height()
            float r12 = (float) r12
            float r3 = r3 * r12
            float r2 = r2 - r3
            int r2 = (int) r2
            r1.mPosY = r2
            goto L_0x00ec
        L_0x00d3:
            int r2 = r1.mConvertX
            float r2 = (float) r2
            float r3 = r1.mCurrentScale
            int r11 = r1.mScreenWidth
            float r11 = (float) r11
            float r11 = r11 * r3
            float r11 = r11 / r12
            float r2 = r2 - r11
            int r2 = (int) r2
            r1.mPosX = r2
            int r2 = r1.mConvertY
            float r2 = (float) r2
            int r11 = r1.mScreenHeight
            float r11 = (float) r11
            float r3 = r3 * r11
            float r2 = r2 - r3
            int r2 = (int) r2
            r1.mPosY = r2
        L_0x00ec:
            boolean r2 = com.android.server.wm.MiuiGestureController.DEBUG_STEP
            if (r2 == 0) goto L_0x0112
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r3 = "mPosX="
            r2.append(r3)
            int r3 = r1.mPosX
            r2.append(r3)
            java.lang.String r3 = ", mPosY="
            r2.append(r3)
            int r3 = r1.mPosY
            r2.append(r3)
            java.lang.String r2 = r2.toString()
            java.lang.String r3 = "MiuiGesture"
            android.util.Slog.d(r3, r2)
        L_0x0112:
            boolean r2 = r7.mNeedClip
            if (r2 == 0) goto L_0x011e
            r8.offsetTo(r10, r10)
            com.android.server.wm.MiuiGestureAnimator r2 = r1.mSurfaceAnimator
            r2.setWindowCropInTransaction(r13, r8)
        L_0x011e:
            com.android.server.wm.MiuiGestureAnimator r2 = r1.mSurfaceAnimator
            int r3 = r1.mPosX
            float r3 = (float) r3
            int r10 = r1.mPosY
            float r10 = (float) r10
            r2.setPositionInTransaction(r13, r3, r10)
            boolean r2 = com.android.server.wm.MiuiGestureController.DEBUG_STEP
            if (r2 == 0) goto L_0x0153
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r3 = "win.mWinAnimator.mDtDx="
            r2.append(r3)
            com.android.server.wm.WindowStateAnimator r3 = r6.mWinAnimator
            float r3 = r3.mDtDx
            r2.append(r3)
            java.lang.String r3 = ",win.mWinAnimator.mDtDy="
            r2.append(r3)
            com.android.server.wm.WindowStateAnimator r3 = r6.mWinAnimator
            float r3 = r3.mDtDy
            r2.append(r3)
            java.lang.String r2 = r2.toString()
            java.lang.String r3 = "MiuiGesture"
            android.util.Slog.d(r3, r2)
        L_0x0153:
            com.android.server.wm.MiuiGestureAnimator r15 = r1.mSurfaceAnimator
            float r2 = r1.mCurrentScale
            com.android.server.wm.WindowStateAnimator r3 = r6.mWinAnimator
            float r3 = r3.mDtDx
            com.android.server.wm.WindowStateAnimator r10 = r6.mWinAnimator
            float r10 = r10.mDtDy
            float r11 = r1.mCurrentScale
            r16 = r13
            r17 = r2
            r18 = r3
            r19 = r10
            r20 = r11
            r15.setMatrixInTransaction(r16, r17, r18, r19, r20)
            com.android.server.wm.MiuiGestureAnimator r2 = r1.mSurfaceAnimator
            r2.setDefaultRoundCorner(r13)
            com.android.server.wm.MiuiGestureAnimator r2 = r1.mSurfaceAnimator
            r2.applyTransaction()
            int r2 = r1.mPosX
            r7.mNowPosX = r2
            int r2 = r1.mPosY
            r7.mNowPosY = r2
            float r2 = r1.mCurrentScale
            r7.mNowScale = r2
        L_0x0184:
            r2 = 32
            goto L_0x0016
        L_0x0188:
            r2 = 32
            android.os.Trace.traceEnd(r2)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.wm.MiuiGesturePointerEventListener.stepFollowAnimation(float, float):void");
    }

    /* access modifiers changed from: private */
    public void onActionUp(float x, float y) {
        MiuiGestureDetector.onActionUp((int) x, (int) y);
        if (this.mGestureStatus == 2) {
            if (MiuiGestureDetector.isOverSpeed()) {
                if (MiuiGestureDetector.isRightDirection()) {
                    if (this.mIsEnterRecents) {
                        this.mGestureController.setLaunchRecentsBehind(false);
                        this.mGestureController.setSkipAppTransition();
                        this.mGestureController.cancelRecents();
                    }
                    startAnimation(0);
                } else {
                    startAnimation(2);
                }
            } else if (!MiuiGestureDetector.shouldGoRecents()) {
                startAnimation(2);
            } else if (this.mIsEnterRecents) {
                WindowState topAppWindow = this.mTopWindow;
                if (topAppWindow == null || !topAppWindow.mWinAnimator.getShown()) {
                    startAnimation(2);
                } else {
                    startAnimation(1);
                }
            } else {
                Slog.w("MiuiGesture", "onActionUp: go to home when recents not show behind");
                startAnimation(0);
            }
        }
        this.mIsEnterRecents = false;
        if (!isGestureRunning() && this.mGestureStatus == 1) {
            updateStatus(0);
        }
    }

    private void gestureReady() {
        if (this.mGestureController.isSuperSavePowerMode()) {
            updateStatus(2);
            return;
        }
        this.mClosingAppTokens.clear();
        this.mHomeAppToken = null;
        if (findTarget()) {
            updateStatus(2);
            this.mGestureController.setKeepWallpaperShowing(true);
            updateVisibleWindowsForGestureLocked();
            synchronized (this.mService.mGlobalLock) {
                try {
                    WindowManagerService.boostPriorityForLockedSection();
                    if (this.mTopAppWindowToken != null) {
                        this.mGestureController.notifyGestureReady(this.mTopAppWindowToken);
                        if (this.mIsPortrait) {
                            this.mHomeStrategy.getSpec(this.mTopAppWindowToken);
                        }
                        this.mIsFirstMove = true;
                    }
                    hideInputMethodWindowIfNeeded();
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

    private void updateStatus(int status) {
        Slog.i("MiuiGesture", "status: " + getStatusString(this.mGestureStatus) + " -> " + getStatusString(status));
        this.mGestureStatus = status;
    }

    private String getStatusString(int status) {
        if (status == 1) {
            return "Detected";
        }
        if (status == 2) {
            return "follow";
        }
        if (status != 3) {
            return ProcessPolicy.REASON_UNKNOWN;
        }
        return "Sliding";
    }

    private boolean findTarget() {
        synchronized (this.mService.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                Task topTask = MiuiGestureDetector.findTopTask();
                if (topTask != null && !topTask.isActivityTypeHome()) {
                    if (!topTask.isActivityTypeRecents()) {
                        this.mTopTask = topTask;
                        this.mTopTask.forAllWindows((Consumer<WindowState>) new Consumer() {
                            public final void accept(Object obj) {
                                MiuiGesturePointerEventListener.this.lambda$findTarget$0$MiuiGesturePointerEventListener((WindowState) obj);
                            }
                        }, true);
                        if (this.mTopWindow == null) {
                            Slog.e("MiuiGesture", "can't find visible window in " + topTask);
                            WindowManagerService.resetPriorityAfterLockedSection();
                            return false;
                        }
                        WindowManagerService.resetPriorityAfterLockedSection();
                        return true;
                    }
                }
                Slog.e("MiuiGesture", "can't find top app");
                WindowManagerService.resetPriorityAfterLockedSection();
                return false;
            } catch (Throwable th) {
                while (true) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
        }
    }

    public /* synthetic */ void lambda$findTarget$0$MiuiGesturePointerEventListener(WindowState w) {
        WindowStateAnimator animator = w.mWinAnimator;
        if (!w.mDestroying && !w.mAnimatingExit && w.mAppToken != null && animator.getShown() && animator.hasSurface()) {
            Slog.d("MiuiGesture", "add " + w + ", type=" + w.mAttrs.type);
            IGestureStrategy.WindowStateInfo wInfo = new IGestureStrategy.WindowStateInfo();
            wInfo.mOriFrame.set(w.mWindowFrames.mFrame);
            wInfo.mOriPosX = w.mWindowFrames.mFrame.left;
            wInfo.mOriPosY = w.mWindowFrames.mFrame.top;
            wInfo.mAppToken = w.mAppToken;
            this.mGestureController.startGestureAnimation(animator);
            try {
                if (!this.mClosingAppTokens.contains(w.mAppToken)) {
                    this.mSurfaceAnimator.createLeash(w.mAppToken);
                }
            } catch (RuntimeException e) {
                e.printStackTrace();
            }
            this.mClosingAppWindows.put(w, wInfo);
            this.mClosingAppTokens.add(w.mAppToken);
            if (this.mTopWindow == null) {
                this.mTopWindow = w;
                this.mTopAppWindowToken = w.mAppToken;
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void launchRecentsBehind() {
        if (!this.mIsEnterRecents && !this.mGestureController.isSuperSavePowerMode()) {
            this.mIsEnterRecents = true;
            this.mGestureController.setLaunchRecentsBehind(true);
            this.mGestureController.setSkipAppTransition();
            this.mGestureController.notifyGestureStartRecents();
        }
    }

    private void updateScreenParams(DisplayContent displayContent, Configuration configuration) {
        int ori = configuration.orientation;
        if (ori == 1) {
            this.mScreenHeight = displayContent.mBaseDisplayHeight;
            this.mScreenWidth = displayContent.mBaseDisplayWidth;
            this.mIsPortrait = true;
        } else if (ori == 2) {
            this.mScreenHeight = displayContent.mBaseDisplayWidth;
            this.mScreenWidth = displayContent.mBaseDisplayHeight;
            this.mIsPortrait = false;
        }
        MiuiGestureDetector.updateScreen(this.mScreenWidth, this.mScreenHeight, this.mIsPortrait);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:33:0x00ba, code lost:
        com.android.server.wm.WindowManagerService.resetPriorityAfterLockedSection();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:34:0x00bd, code lost:
        if (r10 != 2) goto L_0x00d4;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:36:0x00c1, code lost:
        if (r9.mIsEnterRecents == false) goto L_0x00cd;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:37:0x00c3, code lost:
        r9.mGestureController.setLaunchRecentsBehind(false);
        r9.mGestureController.cancelRecents();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:38:0x00cd, code lost:
        r9.mGestureController.notifyGestureAnimationCancel();
        r9.mIsEnterRecents = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:39:0x00d4, code lost:
        updateStatus(3);
        r9.mSurfaceRunner.startAnimation(r9.mCurrentAnimator);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:40:0x00df, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void startAnimation(int r10) {
        /*
            r9 = this;
            boolean r0 = r9.handleSuperPowerMode(r10)
            if (r0 == 0) goto L_0x0007
            return
        L_0x0007:
            r0 = 2
            if (r10 == 0) goto L_0x001a
            r1 = 1
            if (r10 == r1) goto L_0x0015
            if (r10 == r0) goto L_0x0010
            goto L_0x001f
        L_0x0010:
            com.android.server.wm.MiuiGestureCancelStrategy r1 = r9.mCancelStrategy
            r9.mCurrentAnimator = r1
            goto L_0x001f
        L_0x0015:
            com.android.server.wm.MiuiGestureRecentsStrategy r1 = r9.mRecentsStrategy
            r9.mCurrentAnimator = r1
            goto L_0x001f
        L_0x001a:
            com.android.server.wm.MiuiGestureHomeStrategy r1 = r9.mHomeStrategy
            r9.mCurrentAnimator = r1
        L_0x001f:
            com.android.server.wm.IGestureStrategy r1 = r9.mCurrentAnimator
            if (r1 != 0) goto L_0x002b
            java.lang.String r0 = "MiuiGesture"
            java.lang.String r1 = "no animator to run"
            android.util.Slog.e(r0, r1)
            return
        L_0x002b:
            android.graphics.Rect r1 = new android.graphics.Rect
            int r2 = r9.mPosX
            int r3 = r9.mPosY
            com.android.server.wm.WindowState r4 = r9.mTopWindow
            android.graphics.Rect r4 = r4.getContainingFrame()
            int r4 = r4.width()
            float r4 = (float) r4
            float r5 = r9.mCurrentScale
            float r4 = r4 * r5
            int r4 = (int) r4
            int r4 = r4 + r2
            int r5 = r9.mPosY
            com.android.server.wm.WindowState r6 = r9.mTopWindow
            android.graphics.Rect r6 = r6.getContainingFrame()
            int r6 = r6.height()
            float r6 = (float) r6
            float r7 = r9.mCurrentScale
            float r6 = r6 * r7
            int r6 = (int) r6
            int r5 = r5 + r6
            r1.<init>(r2, r3, r4, r5)
            boolean r2 = com.android.server.wm.MiuiGestureController.DEBUG_FOLLOW
            if (r2 == 0) goto L_0x0070
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r3 = "closingAppStartRect="
            r2.append(r3)
            r2.append(r1)
            java.lang.String r2 = r2.toString()
            java.lang.String r3 = "MiuiGesture"
            android.util.Slog.d(r3, r2)
        L_0x0070:
            java.util.TreeSet<com.android.server.wm.AppWindowToken> r2 = r9.mClosingAppTokens
            java.util.Iterator r2 = r2.iterator()
        L_0x0076:
            boolean r3 = r2.hasNext()
            r4 = 0
            if (r3 == 0) goto L_0x008c
            java.lang.Object r3 = r2.next()
            com.android.server.wm.AppWindowToken r3 = (com.android.server.wm.AppWindowToken) r3
            com.android.server.wm.-$$Lambda$MiuiGesturePointerEventListener$Tk7gp3IwiRgZtSE8ERHvHgK8_8g r5 = new com.android.server.wm.-$$Lambda$MiuiGesturePointerEventListener$Tk7gp3IwiRgZtSE8ERHvHgK8_8g
            r5.<init>()
            r3.forAllWindows((java.util.function.Consumer<com.android.server.wm.WindowState>) r5, (boolean) r4)
            goto L_0x0076
        L_0x008c:
            com.android.server.wm.WindowManagerService r2 = r9.mService
            com.android.server.wm.WindowManagerGlobalLock r2 = r2.mGlobalLock
            monitor-enter(r2)
            com.android.server.wm.WindowManagerService.boostPriorityForLockedSection()     // Catch:{ all -> 0x00e0 }
            java.lang.String r3 = "gesture create animation"
            r5 = 32
            android.os.Trace.traceBegin(r5, r3)     // Catch:{ all -> 0x00e0 }
            com.android.server.wm.IGestureStrategy r3 = r9.mCurrentAnimator     // Catch:{ all -> 0x00e0 }
            java.util.TreeSet<com.android.server.wm.AppWindowToken> r7 = r9.mClosingAppTokens     // Catch:{ all -> 0x00e0 }
            java.util.concurrent.ConcurrentHashMap<com.android.server.wm.WindowState, com.android.server.wm.IGestureStrategy$WindowStateInfo> r8 = r9.mClosingAppWindows     // Catch:{ all -> 0x00e0 }
            boolean r3 = r3.createAnimation(r7, r8, r1)     // Catch:{ all -> 0x00e0 }
            android.os.Trace.traceEnd(r5)     // Catch:{ all -> 0x00e0 }
            if (r3 != 0) goto L_0x00b9
            java.lang.String r0 = "MiuiGesture"
            java.lang.String r4 = "failed to create animation"
            android.util.Slog.e(r0, r4)     // Catch:{ all -> 0x00e0 }
            r9.onStrategyFinish()     // Catch:{ all -> 0x00e0 }
            monitor-exit(r2)     // Catch:{ all -> 0x00e0 }
            com.android.server.wm.WindowManagerService.resetPriorityAfterLockedSection()
            return
        L_0x00b9:
            monitor-exit(r2)     // Catch:{ all -> 0x00e0 }
            com.android.server.wm.WindowManagerService.resetPriorityAfterLockedSection()
            if (r10 != r0) goto L_0x00d4
            boolean r0 = r9.mIsEnterRecents
            if (r0 == 0) goto L_0x00cd
            com.android.server.wm.MiuiGestureController r0 = r9.mGestureController
            r0.setLaunchRecentsBehind(r4)
            com.android.server.wm.MiuiGestureController r0 = r9.mGestureController
            r0.cancelRecents()
        L_0x00cd:
            com.android.server.wm.MiuiGestureController r0 = r9.mGestureController
            r0.notifyGestureAnimationCancel()
            r9.mIsEnterRecents = r4
        L_0x00d4:
            r0 = 3
            r9.updateStatus(r0)
            com.android.server.wm.MiuiGestureSurfaceRunner r0 = r9.mSurfaceRunner
            com.android.server.wm.IGestureStrategy r2 = r9.mCurrentAnimator
            r0.startAnimation(r2)
            return
        L_0x00e0:
            r0 = move-exception
            monitor-exit(r2)     // Catch:{ all -> 0x00e0 }
            com.android.server.wm.WindowManagerService.resetPriorityAfterLockedSection()
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.wm.MiuiGesturePointerEventListener.startAnimation(int):void");
    }

    public /* synthetic */ void lambda$startAnimation$1$MiuiGesturePointerEventListener(WindowState w) {
        if (w != null && w.mWinAnimator != null && w.mWinAnimator.hasSurface() && w.mWinAnimator.mSurfaceController != null) {
            this.mGestureController.startGestureAnimation(w.mWinAnimator);
            this.mHandleByGestureWindows.add(w);
        }
    }

    public void onStrategyFinish() {
        synchronized (MiuiGestureController.mGestureLock) {
            Slog.d("MiuiGesture", "strategy finish");
            this.mCurrentAnimator = null;
            this.mClosingAppTokens.clear();
            this.mClosingAppWindows.clear();
            for (WindowState w : this.mHandleByGestureWindows) {
                this.mGestureController.stopGestureAnimation(w.mWinAnimator);
            }
            this.mHandleByGestureWindows.clear();
            this.mSurfaceAnimator.reset();
            this.mTopTask = null;
            this.mTopAppWindowToken = null;
            this.mTopWindow = null;
            this.cancelGoHomeAppWindowToken.clear();
            updateStatus(0);
            this.mGestureController.unsetAppTransitionSkipped();
            WindowManagerService.H h = this.mService.mH;
            WindowManagerService windowManagerService = this.mService;
            Objects.requireNonNull(windowManagerService);
            h.post(new Runnable() {
                public final void run() {
                    WindowManagerService.this.requestTraversal();
                }
            });
        }
    }

    /* access modifiers changed from: package-private */
    public void notifyStartFromRecents(AppWindowToken token) {
        if (MiuiGestureController.DEBUG_RECENTS) {
            Slog.d("MiuiGesture", "start from recents:" + token);
        }
        this.mRecentsStrategy.cancelAnimation(token);
    }

    /* access modifiers changed from: package-private */
    public void ensurePinnedStackVisible(TaskStack taskStack) {
        if (taskStack != null) {
            taskStack.forAllAppWindows(new Consumer() {
                public final void accept(Object obj) {
                    MiuiGesturePointerEventListener.this.lambda$ensurePinnedStackVisible$2$MiuiGesturePointerEventListener((AppWindowToken) obj);
                }
            });
            this.mSurfaceAnimator.applyTransaction();
        }
    }

    public /* synthetic */ void lambda$ensurePinnedStackVisible$2$MiuiGesturePointerEventListener(AppWindowToken windowState) {
        this.mSurfaceAnimator.showWindow(windowState);
    }

    public void displayConfigurationChange(DisplayContent displayContent, Configuration configuration) {
        if (displayContent.getDisplayId() == 0) {
            updateScreenParams(displayContent, configuration);
        }
    }

    private boolean handleSuperPowerMode(int animationType) {
        if (!this.mGestureController.isSuperSavePowerMode()) {
            return false;
        }
        if (animationType == 0) {
            this.mHomeStrategy.launchHome();
        }
        updateStatus(0);
        return true;
    }

    private void calculateWindowPosition(float rowX, float rowY) {
        float f = this.mDownX;
        this.mConvertX = (int) (((rowX + f) / 2.0f) + (((float) (this.mScreenWidth / 2)) - f));
        if (MiuiGestureController.DEBUG_STEP) {
            Slog.d("MiuiGesture", "mConvertX=" + this.mConvertX);
        }
        if (this.mIsPortrait) {
            int i = this.mScreenHeight;
            this.mConvertY = (int) (((float) i) - ((linearToCubic(rowY, (float) i, 0.0f, 3.0f) * 540.0f) * (((float) this.mScreenWidth) / 1080.0f)));
        } else {
            int i2 = this.mScreenHeight;
            this.mConvertY = (int) (((float) i2) - ((linearToCubic(rowY, (float) i2, 0.0f, 3.0f) * 330.0f) * (((float) this.mScreenHeight) / 1080.0f)));
        }
        if (MiuiGestureController.DEBUG_STEP) {
            Slog.d("MiuiGesture", "mConvertY=" + this.mConvertY);
        }
        this.mCurrentScale = 1.0f - (linearToCubic(rowY, (float) this.mScreenHeight, 0.0f, 3.0f) * 0.385f);
        if (MiuiGestureController.DEBUG_STEP) {
            Slog.d("MiuiGesture", "mCurrentScale=" + this.mCurrentScale);
        }
    }

    private void updateVisibleWindowsForGestureLocked() {
        synchronized (this.mService.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                this.mService.mAtmService.getLockTaskController().stopLockTaskMode((TaskRecord) null, true, Process.myUid());
                this.mService.getDefaultDisplayContentLocked().forAllWindows((Consumer<WindowState>) new Consumer() {
                    public final void accept(Object obj) {
                        MiuiGesturePointerEventListener.this.lambda$updateVisibleWindowsForGestureLocked$3$MiuiGesturePointerEventListener((WindowState) obj);
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

    public /* synthetic */ void lambda$updateVisibleWindowsForGestureLocked$3$MiuiGesturePointerEventListener(WindowState w) {
        SurfaceControl sc;
        if (w != null) {
            int type = w.mAttrs.type;
            if (!WindowManager.LayoutParams.isSystemAlertWindowType(type) && this.mService.mPolicy.getWindowLayerFromTypeLw(2000) > this.mService.mPolicy.getWindowLayerFromTypeLw(type)) {
                int windowMode = w.getWindowingMode();
                int activityType = w.getActivityType();
                if (w.mAppToken != null && activityType == 2 && this.mGestureController.isHomeAppToken(w.mAppToken) && MiuiGestureDetector.isCurrentUser(w)) {
                    this.mHomeAppToken = w.mAppToken;
                    this.mHomeWindow = w;
                    this.mHomeStrategy.setHomeAppToken(this.mHomeAppToken);
                } else if (w.mAppToken != null && activityType == 3 && w.mAttrs.type == 1 && MiuiGestureDetector.isCurrentUser(w)) {
                    this.mRecentsAppToken = w.mAppToken;
                    this.mRecentsWindow = w;
                } else if (windowMode != 3 && windowMode != 2 && (w.mAttrs.extraFlags & DumpState.DUMP_APEX) == 0) {
                    WindowStateAnimator animator = w.mWinAnimator;
                    if (animator.mIsWallpaper) {
                        WindowState win = animator.mWin;
                        if (!(animator.mSurfaceController == null || (sc = animator.mSurfaceController.mSurfaceControl) == null)) {
                            if (this.mIsPortrait) {
                                win.mWindowFrames.mCompatFrame.set(new Rect(0, 0, win.mWindowFrames.mFrame.right, win.mWindowFrames.mFrame.bottom));
                            } else if (win.mWindowFrames.mFrame.width() > this.mScreenWidth) {
                                win.mWindowFrames.mCompatFrame.set(new Rect(0, 0, win.mWindowFrames.mFrame.right, this.mScreenHeight));
                            } else {
                                win.mWindowFrames.mCompatFrame.set(new Rect(0, 0, this.mScreenWidth, this.mScreenHeight));
                            }
                            sc.setOverrideScalingMode(1);
                        }
                        animator.prepareSurfaceLocked(true);
                    }
                }
            }
        }
    }

    private float linearToCubic(float now, float original, float end, float pow) {
        if (pow == original) {
            return now;
        }
        float percent = (now - original) / (pow - original);
        if (pow != 0.0f) {
            return (float) (1.0d - Math.pow((double) (1.0f - percent), (double) pow));
        }
        return 0.0f;
    }

    /* access modifiers changed from: package-private */
    public void setRecentsWindowState(WindowState w) {
        if (MiuiGestureController.DEBUG_RECENTS) {
            Slog.w("MiuiGesture", "setRecentsWindowState w = " + w);
        }
        this.mRecentsWindow = w;
    }

    /* access modifiers changed from: package-private */
    public void setRecentsAppWindowToken(AppWindowToken token) {
        if (MiuiGestureController.DEBUG_RECENTS) {
            Slog.w("MiuiGesture", "setRecentsAppWindowToken token = " + token);
        }
        this.mRecentsAppToken = token;
    }

    /* access modifiers changed from: package-private */
    public boolean isGestureRunning() {
        int i = this.mGestureStatus;
        return i == 2 || i == 3;
    }

    public void dump(PrintWriter pw, String prefix) {
        synchronized (MiuiGestureController.mGestureLock) {
            pw.print(prefix);
            pw.println("mGestureStatus=" + this.mGestureStatus);
            pw.print(prefix);
            pw.println("mIsEnterRecents=" + this.mIsEnterRecents);
        }
    }

    /* access modifiers changed from: package-private */
    public void cancelGoHomeAnimationIfNeeded(AppWindowToken token) {
        try {
            if (this.mHomeStrategy.isAnimating() && this.mClosingAppTokens.contains(token)) {
                if (MiuiGestureController.DEBUG_HOME) {
                    Slog.w("MiuiGesture", "cancel go home animation token = " + token);
                }
                this.mSurfaceRunner.cancelAnimation();
            }
        } catch (RuntimeException e) {
            this.cancelGoHomeAppWindowToken.add(token);
            Slog.e("MiuiGesture", "failed to compare " + token);
            e.printStackTrace();
        }
    }

    private void registerInputConsumer() {
        if (this.mInputEventReceiver == null) {
            InputChannel inputChanel = new InputChannel();
            int displayId = this.mService.getDefaultDisplayContentLocked().getDisplayId();
            this.mService.destroyInputConsumer("gesture_input_consumer", displayId);
            Slog.w("MiuiGesture", "register inputConsumer");
            this.mService.createInputConsumer(this.mToken, "gesture_input_consumer", displayId, inputChanel);
            this.mInputEventReceiver = new InputEventReceiver(inputChanel, Looper.myLooper()) {
                public void onInputEvent(InputEvent event) {
                    Slog.d("MiuiGesture", "input consumer receive:" + event);
                    MiuiGesturePointerEventListener.super.onInputEvent(event);
                }
            };
        }
    }

    private void unregisterInputConsumer() {
        MiuiFgThread.getHandler().postAtFrontOfQueue(new Runnable() {
            public final void run() {
                MiuiGesturePointerEventListener.this.lambda$unregisterInputConsumer$4$MiuiGesturePointerEventListener();
            }
        });
    }

    public /* synthetic */ void lambda$unregisterInputConsumer$4$MiuiGesturePointerEventListener() {
        Slog.w("MiuiGesture", "unregister inputConsumer");
        if (this.mInputEventReceiver != null) {
            this.mService.destroyInputConsumer("gesture_input_consumer", this.mService.getDefaultDisplayContentLocked().getDisplayId());
            this.mInputEventReceiver.dispose();
            this.mInputEventReceiver = null;
        }
    }

    private void hideInputMethodWindowIfNeeded() {
        DisplayContent displayContent = this.mService.getDefaultDisplayContentLocked();
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
}
