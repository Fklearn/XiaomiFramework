package com.android.server.wm;

import android.animation.Animator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.content.ClipData;
import android.content.ClipDescription;
import android.graphics.Point;
import android.graphics.Rect;
import android.hardware.input.InputManager;
import android.os.Binder;
import android.os.IBinder;
import android.os.Process;
import android.os.RemoteException;
import android.os.UserHandle;
import android.os.UserManagerInternal;
import android.util.Slog;
import android.view.Display;
import android.view.DragEvent;
import android.view.IWindow;
import android.view.InputApplicationHandle;
import android.view.InputChannel;
import android.view.InputWindowHandle;
import android.view.SurfaceControl;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import com.android.internal.view.IDragAndDropPermissions;
import com.android.server.LocalServices;
import com.android.server.usb.descriptors.UsbACInterface;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.function.Consumer;

class DragState {
    private static final String ANIMATED_PROPERTY_ALPHA = "alpha";
    private static final String ANIMATED_PROPERTY_SCALE = "scale";
    private static final String ANIMATED_PROPERTY_X = "x";
    private static final String ANIMATED_PROPERTY_Y = "y";
    private static final int DRAG_FLAGS_URI_ACCESS = 3;
    private static final int DRAG_FLAGS_URI_PERMISSIONS = 195;
    private static final long MAX_ANIMATION_DURATION_MS = 375;
    private static final long MIN_ANIMATION_DURATION_MS = 195;
    volatile boolean mAnimationCompleted = false;
    private ValueAnimator mAnimator;
    boolean mCrossProfileCopyAllowed;
    private final Interpolator mCubicEaseOutInterpolator = new DecelerateInterpolator(1.5f);
    float mCurrentX;
    float mCurrentY;
    ClipData mData;
    ClipDescription mDataDescription;
    DisplayContent mDisplayContent;
    /* access modifiers changed from: private */
    public Point mDisplaySize = new Point();
    final DragDropController mDragDropController;
    boolean mDragInProgress;
    boolean mDragResult;
    int mFlags;
    InputInterceptor mInputInterceptor;
    SurfaceControl mInputSurface;
    private boolean mIsClosing;
    IBinder mLocalWin;
    ArrayList<WindowState> mNotifiedWindows;
    float mOriginalAlpha;
    float mOriginalX;
    float mOriginalY;
    int mPid;
    final WindowManagerService mService;
    int mSourceUserId;
    SurfaceControl mSurfaceControl;
    WindowState mTargetWindow;
    float mThumbOffsetX;
    float mThumbOffsetY;
    private final Rect mTmpClipRect = new Rect();
    IBinder mToken;
    int mTouchSource;
    private final SurfaceControl.Transaction mTransaction;
    IBinder mTransferTouchFromToken;
    int mUid;

    DragState(WindowManagerService service, DragDropController controller, IBinder token, SurfaceControl surface, int flags, IBinder localWin) {
        this.mService = service;
        this.mDragDropController = controller;
        this.mToken = token;
        this.mSurfaceControl = surface;
        this.mFlags = flags;
        this.mLocalWin = localWin;
        this.mNotifiedWindows = new ArrayList<>();
        this.mTransaction = service.mTransactionFactory.make();
    }

    /* access modifiers changed from: package-private */
    public boolean isClosing() {
        return this.mIsClosing;
    }

    private void hideInputSurface() {
        SurfaceControl surfaceControl = this.mInputSurface;
        if (surfaceControl != null) {
            this.mTransaction.hide(surfaceControl).apply();
        }
    }

    private void showInputSurface() {
        if (this.mInputSurface == null) {
            WindowManagerService windowManagerService = this.mService;
            this.mInputSurface = windowManagerService.makeSurfaceBuilder(windowManagerService.mRoot.getDisplayContent(this.mDisplayContent.getDisplayId()).getSession()).setContainerLayer().setName("Drag and Drop Input Consumer").build();
        }
        InputWindowHandle h = getInputWindowHandle();
        if (h == null) {
            Slog.w(DisplayPolicy.TAG, "Drag is in progress but there is no drag window handle.");
            return;
        }
        this.mTransaction.show(this.mInputSurface);
        this.mTransaction.setInputWindowInfo(this.mInputSurface, h);
        this.mTransaction.setLayer(this.mInputSurface, Integer.MAX_VALUE);
        this.mTmpClipRect.set(0, 0, this.mDisplaySize.x, this.mDisplaySize.y);
        this.mTransaction.setWindowCrop(this.mInputSurface, this.mTmpClipRect);
        this.mTransaction.transferTouchFocus(this.mTransferTouchFromToken, h.token);
        this.mTransferTouchFromToken = null;
        this.mTransaction.syncInputWindows();
        this.mTransaction.apply();
    }

    /* access modifiers changed from: package-private */
    public void closeLocked() {
        float y;
        float y2;
        this.mIsClosing = true;
        InputInterceptor inputInterceptor = this.mInputInterceptor;
        if (inputInterceptor != null) {
            this.mDragDropController.sendHandlerMessage(1, inputInterceptor);
            this.mInputInterceptor = null;
        }
        hideInputSurface();
        if (this.mDragInProgress) {
            int myPid = Process.myPid();
            Iterator<WindowState> it = this.mNotifiedWindows.iterator();
            while (it.hasNext()) {
                WindowState ws = it.next();
                if (this.mDragResult || ws.mSession.mPid != this.mPid) {
                    y = 0.0f;
                    y2 = 0.0f;
                } else {
                    float x = this.mCurrentX;
                    y = this.mCurrentY;
                    y2 = x;
                }
                DragEvent evt = DragEvent.obtain(4, y2, y, (Object) null, (ClipDescription) null, (ClipData) null, (IDragAndDropPermissions) null, this.mDragResult);
                try {
                    ws.mClient.dispatchDragEvent(evt);
                } catch (RemoteException e) {
                    Slog.w(DisplayPolicy.TAG, "Unable to drag-end window " + ws);
                }
                if (myPid != ws.mSession.mPid) {
                    evt.recycle();
                }
            }
            this.mNotifiedWindows.clear();
            this.mDragInProgress = false;
        }
        if (isFromSource(UsbACInterface.FORMAT_III_IEC1937_MPEG1_Layer1)) {
            this.mService.restorePointerIconLocked(this.mDisplayContent, this.mCurrentX, this.mCurrentY);
            this.mTouchSource = 0;
        }
        SurfaceControl surfaceControl = this.mSurfaceControl;
        if (surfaceControl != null) {
            this.mTransaction.reparent(surfaceControl, (SurfaceControl) null).apply();
            this.mSurfaceControl = null;
        }
        if (this.mAnimator != null && !this.mAnimationCompleted) {
            Slog.wtf(DisplayPolicy.TAG, "Unexpectedly destroying mSurfaceControl while animation is running");
        }
        this.mFlags = 0;
        this.mLocalWin = null;
        this.mToken = null;
        this.mData = null;
        this.mThumbOffsetY = 0.0f;
        this.mThumbOffsetX = 0.0f;
        this.mNotifiedWindows = null;
        this.mDragDropController.onDragStateClosedLocked(this);
    }

    class InputInterceptor {
        InputChannel mClientChannel;
        InputApplicationHandle mDragApplicationHandle = new InputApplicationHandle(new Binder());
        InputWindowHandle mDragWindowHandle;
        DragInputEventReceiver mInputEventReceiver;
        InputChannel mServerChannel;

        InputInterceptor(Display display) {
            InputChannel[] channels = InputChannel.openInputChannelPair("drag");
            this.mServerChannel = channels[0];
            this.mClientChannel = channels[1];
            DragState.this.mService.mInputManager.registerInputChannel(this.mServerChannel, (IBinder) null);
            this.mInputEventReceiver = new DragInputEventReceiver(this.mClientChannel, DragState.this.mService.mH.getLooper(), DragState.this.mDragDropController);
            InputApplicationHandle inputApplicationHandle = this.mDragApplicationHandle;
            inputApplicationHandle.name = "drag";
            inputApplicationHandle.dispatchingTimeoutNanos = 8000000000L;
            this.mDragWindowHandle = new InputWindowHandle(inputApplicationHandle, (IWindow) null, display.getDisplayId());
            InputWindowHandle inputWindowHandle = this.mDragWindowHandle;
            inputWindowHandle.name = "drag";
            inputWindowHandle.token = this.mServerChannel.getToken();
            this.mDragWindowHandle.layer = DragState.this.getDragLayerLocked();
            InputWindowHandle inputWindowHandle2 = this.mDragWindowHandle;
            inputWindowHandle2.layoutParamsFlags = 0;
            inputWindowHandle2.layoutParamsType = 2016;
            inputWindowHandle2.dispatchingTimeoutNanos = 8000000000L;
            inputWindowHandle2.visible = true;
            inputWindowHandle2.canReceiveKeys = false;
            inputWindowHandle2.hasFocus = true;
            inputWindowHandle2.hasWallpaper = false;
            inputWindowHandle2.paused = false;
            inputWindowHandle2.ownerPid = Process.myPid();
            this.mDragWindowHandle.ownerUid = Process.myUid();
            InputWindowHandle inputWindowHandle3 = this.mDragWindowHandle;
            inputWindowHandle3.inputFeatures = 0;
            inputWindowHandle3.scaleFactor = 1.0f;
            inputWindowHandle3.touchableRegion.setEmpty();
            InputWindowHandle inputWindowHandle4 = this.mDragWindowHandle;
            inputWindowHandle4.frameLeft = 0;
            inputWindowHandle4.frameTop = 0;
            inputWindowHandle4.frameRight = DragState.this.mDisplaySize.x;
            this.mDragWindowHandle.frameBottom = DragState.this.mDisplaySize.y;
            DragState.this.mDisplayContent.pauseRotationLocked();
        }

        /* access modifiers changed from: package-private */
        public void tearDown() {
            DragState.this.mService.mInputManager.unregisterInputChannel(this.mServerChannel);
            this.mInputEventReceiver.dispose();
            this.mInputEventReceiver = null;
            this.mClientChannel.dispose();
            this.mServerChannel.dispose();
            this.mClientChannel = null;
            this.mServerChannel = null;
            this.mDragWindowHandle = null;
            this.mDragApplicationHandle = null;
            DragState.this.mDisplayContent.resumeRotationLocked();
        }
    }

    /* access modifiers changed from: package-private */
    public InputChannel getInputChannel() {
        InputInterceptor inputInterceptor = this.mInputInterceptor;
        if (inputInterceptor == null) {
            return null;
        }
        return inputInterceptor.mServerChannel;
    }

    /* access modifiers changed from: package-private */
    public InputWindowHandle getInputWindowHandle() {
        InputInterceptor inputInterceptor = this.mInputInterceptor;
        if (inputInterceptor == null) {
            return null;
        }
        return inputInterceptor.mDragWindowHandle;
    }

    /* access modifiers changed from: package-private */
    public void register(Display display) {
        display.getRealSize(this.mDisplaySize);
        if (this.mInputInterceptor != null) {
            Slog.e(DisplayPolicy.TAG, "Duplicate register of drag input channel");
            return;
        }
        this.mInputInterceptor = new InputInterceptor(display);
        showInputSurface();
    }

    /* access modifiers changed from: package-private */
    public int getDragLayerLocked() {
        return (this.mService.mPolicy.getWindowLayerFromTypeLw(2016) * 10000) + 1000;
    }

    /* access modifiers changed from: package-private */
    public void broadcastDragStartedLocked(float touchX, float touchY) {
        this.mCurrentX = touchX;
        this.mOriginalX = touchX;
        this.mCurrentY = touchY;
        this.mOriginalY = touchY;
        ClipData clipData = this.mData;
        this.mDataDescription = clipData != null ? clipData.getDescription() : null;
        this.mNotifiedWindows.clear();
        this.mDragInProgress = true;
        this.mSourceUserId = UserHandle.getUserId(this.mUid);
        this.mCrossProfileCopyAllowed = true ^ ((UserManagerInternal) LocalServices.getService(UserManagerInternal.class)).getUserRestriction(this.mSourceUserId, "no_cross_profile_copy_paste");
        this.mDisplayContent.forAllWindows((Consumer<WindowState>) new Consumer(touchX, touchY) {
            private final /* synthetic */ float f$1;
            private final /* synthetic */ float f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void accept(Object obj) {
                DragState.this.lambda$broadcastDragStartedLocked$0$DragState(this.f$1, this.f$2, (WindowState) obj);
            }
        }, false);
    }

    public /* synthetic */ void lambda$broadcastDragStartedLocked$0$DragState(float touchX, float touchY, WindowState w) {
        sendDragStartedLocked(w, touchX, touchY, this.mDataDescription);
    }

    private void sendDragStartedLocked(WindowState newWin, float touchX, float touchY, ClipDescription desc) {
        if (this.mDragInProgress && isValidDropTarget(newWin)) {
            DragEvent event = obtainDragEvent(newWin, 1, touchX, touchY, (Object) null, desc, (ClipData) null, (IDragAndDropPermissions) null, false);
            try {
                newWin.mClient.dispatchDragEvent(event);
                this.mNotifiedWindows.add(newWin);
                if (Process.myPid() == newWin.mSession.mPid) {
                    return;
                }
            } catch (RemoteException e) {
                Slog.w(DisplayPolicy.TAG, "Unable to drag-start window " + newWin);
                if (Process.myPid() == newWin.mSession.mPid) {
                    return;
                }
            } catch (Throwable th) {
                if (Process.myPid() != newWin.mSession.mPid) {
                    event.recycle();
                }
                throw th;
            }
            event.recycle();
        }
    }

    private boolean isValidDropTarget(WindowState targetWin) {
        if (targetWin == null || !targetWin.isPotentialDragTarget()) {
            return false;
        }
        if (((this.mFlags & 256) == 0 || !targetWindowSupportsGlobalDrag(targetWin)) && this.mLocalWin != targetWin.mClient.asBinder()) {
            return false;
        }
        if (this.mCrossProfileCopyAllowed || this.mSourceUserId == UserHandle.getUserId(targetWin.getOwningUid())) {
            return true;
        }
        return false;
    }

    private boolean targetWindowSupportsGlobalDrag(WindowState targetWin) {
        return targetWin.mAppToken == null || targetWin.mAppToken.mTargetSdk >= 24;
    }

    /* access modifiers changed from: package-private */
    public void sendDragStartedIfNeededLocked(WindowState newWin) {
        if (this.mDragInProgress && !isWindowNotified(newWin)) {
            sendDragStartedLocked(newWin, this.mCurrentX, this.mCurrentY, this.mDataDescription);
        }
    }

    private boolean isWindowNotified(WindowState newWin) {
        Iterator<WindowState> it = this.mNotifiedWindows.iterator();
        while (it.hasNext()) {
            if (it.next() == newWin) {
                return true;
            }
        }
        return false;
    }

    /* access modifiers changed from: package-private */
    public void endDragLocked() {
        if (this.mAnimator == null) {
            if (!this.mDragResult) {
                this.mAnimator = createReturnAnimationLocked();
            } else {
                closeLocked();
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void cancelDragLocked(boolean skipAnimation) {
        if (this.mAnimator == null) {
            if (!this.mDragInProgress || skipAnimation) {
                closeLocked();
            } else {
                this.mAnimator = createCancelAnimationLocked();
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void notifyMoveLocked(float x, float y) {
        if (this.mAnimator == null) {
            this.mCurrentX = x;
            this.mCurrentY = y;
            this.mTransaction.setPosition(this.mSurfaceControl, x - this.mThumbOffsetX, y - this.mThumbOffsetY).apply();
            notifyLocationLocked(x, y);
        }
    }

    /* access modifiers changed from: package-private */
    public void notifyLocationLocked(float x, float y) {
        WindowState touchedWin;
        WindowState touchedWin2 = this.mDisplayContent.getTouchableWinAtPointLocked(x, y);
        if (touchedWin2 == null || isWindowNotified(touchedWin2)) {
            touchedWin = touchedWin2;
        } else {
            touchedWin = null;
        }
        try {
            int myPid = Process.myPid();
            if (!(touchedWin == this.mTargetWindow || this.mTargetWindow == null)) {
                DragEvent evt = obtainDragEvent(this.mTargetWindow, 6, 0.0f, 0.0f, (Object) null, (ClipDescription) null, (ClipData) null, (IDragAndDropPermissions) null, false);
                this.mTargetWindow.mClient.dispatchDragEvent(evt);
                if (myPid != this.mTargetWindow.mSession.mPid) {
                    evt.recycle();
                }
            }
            if (touchedWin != null) {
                DragEvent evt2 = obtainDragEvent(touchedWin, 2, x, y, (Object) null, (ClipDescription) null, (ClipData) null, (IDragAndDropPermissions) null, false);
                touchedWin.mClient.dispatchDragEvent(evt2);
                if (myPid != touchedWin.mSession.mPid) {
                    evt2.recycle();
                }
            }
        } catch (RemoteException e) {
            Slog.w(DisplayPolicy.TAG, "can't send drag notification to windows");
        }
        this.mTargetWindow = touchedWin;
    }

    /* access modifiers changed from: package-private */
    /* JADX WARNING: Code restructure failed: missing block: B:24:0x0089, code lost:
        if (r11 != r13.mSession.mPid) goto L_0x008b;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:25:0x008b, code lost:
        r2.recycle();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:32:0x00af, code lost:
        if (r11 == r13.mSession.mPid) goto L_0x00b2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:33:0x00b2, code lost:
        r1.mToken = r14;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:34:0x00b4, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void notifyDropLocked(float r18, float r19) {
        /*
            r17 = this;
            r1 = r17
            r11 = r18
            r12 = r19
            android.animation.ValueAnimator r0 = r1.mAnimator
            if (r0 == 0) goto L_0x000b
            return
        L_0x000b:
            r1.mCurrentX = r11
            r1.mCurrentY = r12
            com.android.server.wm.DisplayContent r0 = r1.mDisplayContent
            com.android.server.wm.WindowState r13 = r0.getTouchableWinAtPointLocked(r11, r12)
            boolean r0 = r1.isWindowNotified(r13)
            r14 = 0
            if (r0 != 0) goto L_0x0022
            r1.mDragResult = r14
            r17.endDragLocked()
            return
        L_0x0022:
            int r0 = r13.getOwningUid()
            int r15 = android.os.UserHandle.getUserId(r0)
            int r0 = r1.mFlags
            r2 = r0 & 256(0x100, float:3.59E-43)
            if (r2 == 0) goto L_0x004e
            r0 = r0 & 3
            if (r0 == 0) goto L_0x004e
            android.content.ClipData r3 = r1.mData
            if (r3 == 0) goto L_0x004e
            com.android.server.wm.DragAndDropPermissionsHandler r0 = new com.android.server.wm.DragAndDropPermissionsHandler
            int r4 = r1.mUid
            java.lang.String r5 = r13.getOwningPackage()
            int r2 = r1.mFlags
            r6 = r2 & 195(0xc3, float:2.73E-43)
            int r7 = r1.mSourceUserId
            r2 = r0
            r8 = r15
            r2.<init>(r3, r4, r5, r6, r7, r8)
            r16 = r0
            goto L_0x0051
        L_0x004e:
            r0 = 0
            r16 = r0
        L_0x0051:
            int r0 = r1.mSourceUserId
            if (r0 == r15) goto L_0x005c
            android.content.ClipData r2 = r1.mData
            if (r2 == 0) goto L_0x005c
            r2.fixUris(r0)
        L_0x005c:
            int r10 = android.os.Process.myPid()
            android.view.IWindow r0 = r13.mClient
            android.os.IBinder r9 = r0.asBinder()
            r3 = 3
            r6 = 0
            r7 = 0
            android.content.ClipData r8 = r1.mData
            r0 = 0
            r2 = r13
            r4 = r18
            r5 = r19
            r14 = r9
            r9 = r16
            r11 = r10
            r10 = r0
            android.view.DragEvent r2 = obtainDragEvent(r2, r3, r4, r5, r6, r7, r8, r9, r10)
            android.view.IWindow r0 = r13.mClient     // Catch:{ RemoteException -> 0x0091 }
            r0.dispatchDragEvent(r2)     // Catch:{ RemoteException -> 0x0091 }
            com.android.server.wm.DragDropController r0 = r1.mDragDropController     // Catch:{ RemoteException -> 0x0091 }
            r3 = 0
            r0.sendTimeoutMessage(r3, r14)     // Catch:{ RemoteException -> 0x0091 }
            com.android.server.wm.Session r0 = r13.mSession
            int r0 = r0.mPid
            if (r11 == r0) goto L_0x00b2
        L_0x008b:
            r2.recycle()
            goto L_0x00b2
        L_0x008f:
            r0 = move-exception
            goto L_0x00b5
        L_0x0091:
            r0 = move-exception
            java.lang.String r3 = "WindowManager"
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ all -> 0x008f }
            r4.<init>()     // Catch:{ all -> 0x008f }
            java.lang.String r5 = "can't send drop notification to win "
            r4.append(r5)     // Catch:{ all -> 0x008f }
            r4.append(r13)     // Catch:{ all -> 0x008f }
            java.lang.String r4 = r4.toString()     // Catch:{ all -> 0x008f }
            android.util.Slog.w(r3, r4)     // Catch:{ all -> 0x008f }
            r17.endDragLocked()     // Catch:{ all -> 0x008f }
            com.android.server.wm.Session r0 = r13.mSession
            int r0 = r0.mPid
            if (r11 == r0) goto L_0x00b2
            goto L_0x008b
        L_0x00b2:
            r1.mToken = r14
            return
        L_0x00b5:
            com.android.server.wm.Session r3 = r13.mSession
            int r3 = r3.mPid
            if (r11 == r3) goto L_0x00be
            r2.recycle()
        L_0x00be:
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.wm.DragState.notifyDropLocked(float, float):void");
    }

    /* access modifiers changed from: package-private */
    public boolean isInProgress() {
        return this.mDragInProgress;
    }

    private static DragEvent obtainDragEvent(WindowState win, int action, float x, float y, Object localState, ClipDescription description, ClipData data, IDragAndDropPermissions dragAndDropPermissions, boolean result) {
        WindowState windowState = win;
        float f = x;
        return DragEvent.obtain(action, win.translateToWindowX(x), win.translateToWindowY(y), localState, description, data, dragAndDropPermissions, result);
    }

    private ValueAnimator createReturnAnimationLocked() {
        float f = this.mCurrentX;
        float f2 = this.mThumbOffsetX;
        float[] fArr = {f - f2, this.mOriginalX - f2};
        float f3 = this.mCurrentY;
        float f4 = this.mThumbOffsetY;
        float[] fArr2 = {f3 - f4, this.mOriginalY - f4};
        float f5 = this.mOriginalAlpha;
        ValueAnimator animator = ValueAnimator.ofPropertyValuesHolder(new PropertyValuesHolder[]{PropertyValuesHolder.ofFloat(ANIMATED_PROPERTY_X, fArr), PropertyValuesHolder.ofFloat(ANIMATED_PROPERTY_Y, fArr2), PropertyValuesHolder.ofFloat(ANIMATED_PROPERTY_SCALE, new float[]{1.0f, 1.0f}), PropertyValuesHolder.ofFloat(ANIMATED_PROPERTY_ALPHA, new float[]{f5, f5 / 2.0f})});
        float translateX = this.mOriginalX - this.mCurrentX;
        float translateY = this.mOriginalY - this.mCurrentY;
        long duration = ((long) ((Math.sqrt((double) ((translateX * translateX) + (translateY * translateY))) / Math.sqrt((double) ((this.mDisplaySize.x * this.mDisplaySize.x) + (this.mDisplaySize.y * this.mDisplaySize.y)))) * 180.0d)) + MIN_ANIMATION_DURATION_MS;
        AnimationListener listener = new AnimationListener();
        animator.setDuration(duration);
        animator.setInterpolator(this.mCubicEaseOutInterpolator);
        animator.addListener(listener);
        animator.addUpdateListener(listener);
        this.mService.mAnimationHandler.post(new Runnable(animator) {
            private final /* synthetic */ ValueAnimator f$0;

            {
                this.f$0 = r1;
            }

            public final void run() {
                this.f$0.start();
            }
        });
        return animator;
    }

    private ValueAnimator createCancelAnimationLocked() {
        float f = this.mCurrentX;
        float[] fArr = {f - this.mThumbOffsetX, f};
        float f2 = this.mCurrentY;
        ValueAnimator animator = ValueAnimator.ofPropertyValuesHolder(new PropertyValuesHolder[]{PropertyValuesHolder.ofFloat(ANIMATED_PROPERTY_X, fArr), PropertyValuesHolder.ofFloat(ANIMATED_PROPERTY_Y, new float[]{f2 - this.mThumbOffsetY, f2}), PropertyValuesHolder.ofFloat(ANIMATED_PROPERTY_SCALE, new float[]{1.0f, 0.0f}), PropertyValuesHolder.ofFloat(ANIMATED_PROPERTY_ALPHA, new float[]{this.mOriginalAlpha, 0.0f})});
        AnimationListener listener = new AnimationListener();
        animator.setDuration(MIN_ANIMATION_DURATION_MS);
        animator.setInterpolator(this.mCubicEaseOutInterpolator);
        animator.addListener(listener);
        animator.addUpdateListener(listener);
        this.mService.mAnimationHandler.post(new Runnable(animator) {
            private final /* synthetic */ ValueAnimator f$0;

            {
                this.f$0 = r1;
            }

            public final void run() {
                this.f$0.start();
            }
        });
        return animator;
    }

    private boolean isFromSource(int source) {
        return (this.mTouchSource & source) == source;
    }

    /* access modifiers changed from: package-private */
    public void overridePointerIconLocked(int touchSource) {
        this.mTouchSource = touchSource;
        if (isFromSource(UsbACInterface.FORMAT_III_IEC1937_MPEG1_Layer1)) {
            InputManager.getInstance().setPointerIconType(1021);
        }
    }

    private class AnimationListener implements ValueAnimator.AnimatorUpdateListener, Animator.AnimatorListener {
        private AnimationListener() {
        }

        /* Debug info: failed to restart local var, previous not found, register: 8 */
        /* JADX WARNING: Code restructure failed: missing block: B:10:?, code lost:
            r1.close();
         */
        /* JADX WARNING: Code restructure failed: missing block: B:11:0x0065, code lost:
            r3 = move-exception;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:12:0x0066, code lost:
            r0.addSuppressed(r3);
         */
        /* JADX WARNING: Code restructure failed: missing block: B:13:0x0069, code lost:
            throw r2;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:8:0x0060, code lost:
            r2 = move-exception;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void onAnimationUpdate(android.animation.ValueAnimator r9) {
            /*
                r8 = this;
                java.lang.String r0 = "scale"
                android.view.SurfaceControl$Transaction r1 = new android.view.SurfaceControl$Transaction
                r1.<init>()
                com.android.server.wm.DragState r2 = com.android.server.wm.DragState.this     // Catch:{ all -> 0x005e }
                android.view.SurfaceControl r2 = r2.mSurfaceControl     // Catch:{ all -> 0x005e }
                java.lang.String r3 = "x"
                java.lang.Object r3 = r9.getAnimatedValue(r3)     // Catch:{ all -> 0x005e }
                java.lang.Float r3 = (java.lang.Float) r3     // Catch:{ all -> 0x005e }
                float r3 = r3.floatValue()     // Catch:{ all -> 0x005e }
                java.lang.String r4 = "y"
                java.lang.Object r4 = r9.getAnimatedValue(r4)     // Catch:{ all -> 0x005e }
                java.lang.Float r4 = (java.lang.Float) r4     // Catch:{ all -> 0x005e }
                float r4 = r4.floatValue()     // Catch:{ all -> 0x005e }
                r1.setPosition(r2, r3, r4)     // Catch:{ all -> 0x005e }
                com.android.server.wm.DragState r2 = com.android.server.wm.DragState.this     // Catch:{ all -> 0x005e }
                android.view.SurfaceControl r2 = r2.mSurfaceControl     // Catch:{ all -> 0x005e }
                java.lang.String r3 = "alpha"
                java.lang.Object r3 = r9.getAnimatedValue(r3)     // Catch:{ all -> 0x005e }
                java.lang.Float r3 = (java.lang.Float) r3     // Catch:{ all -> 0x005e }
                float r3 = r3.floatValue()     // Catch:{ all -> 0x005e }
                r1.setAlpha(r2, r3)     // Catch:{ all -> 0x005e }
                com.android.server.wm.DragState r2 = com.android.server.wm.DragState.this     // Catch:{ all -> 0x005e }
                android.view.SurfaceControl r3 = r2.mSurfaceControl     // Catch:{ all -> 0x005e }
                java.lang.Object r2 = r9.getAnimatedValue(r0)     // Catch:{ all -> 0x005e }
                java.lang.Float r2 = (java.lang.Float) r2     // Catch:{ all -> 0x005e }
                float r4 = r2.floatValue()     // Catch:{ all -> 0x005e }
                r5 = 0
                r6 = 0
                java.lang.Object r0 = r9.getAnimatedValue(r0)     // Catch:{ all -> 0x005e }
                java.lang.Float r0 = (java.lang.Float) r0     // Catch:{ all -> 0x005e }
                float r7 = r0.floatValue()     // Catch:{ all -> 0x005e }
                r2 = r1
                r2.setMatrix(r3, r4, r5, r6, r7)     // Catch:{ all -> 0x005e }
                r1.apply()     // Catch:{ all -> 0x005e }
                r1.close()
                return
            L_0x005e:
                r0 = move-exception
                throw r0     // Catch:{ all -> 0x0060 }
            L_0x0060:
                r2 = move-exception
                r1.close()     // Catch:{ all -> 0x0065 }
                goto L_0x0069
            L_0x0065:
                r3 = move-exception
                r0.addSuppressed(r3)
            L_0x0069:
                throw r2
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.server.wm.DragState.AnimationListener.onAnimationUpdate(android.animation.ValueAnimator):void");
        }

        public void onAnimationStart(Animator animator) {
        }

        public void onAnimationCancel(Animator animator) {
        }

        public void onAnimationRepeat(Animator animator) {
        }

        public void onAnimationEnd(Animator animator) {
            DragState dragState = DragState.this;
            dragState.mAnimationCompleted = true;
            dragState.mDragDropController.sendHandlerMessage(2, (Object) null);
        }
    }
}
