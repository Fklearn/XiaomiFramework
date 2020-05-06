package com.android.server.wm;

import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.util.Slog;
import android.view.IWindow;
import com.android.internal.util.Preconditions;
import com.android.server.wm.DragState;
import com.android.server.wm.WindowManagerInternal;
import java.util.concurrent.atomic.AtomicReference;

class DragDropController {
    private static final float DRAG_SHADOW_ALPHA_TRANSPARENT = 0.7071f;
    private static final long DRAG_TIMEOUT_MS = 5000;
    static final int MSG_ANIMATION_END = 2;
    static final int MSG_DRAG_END_TIMEOUT = 0;
    static final int MSG_TEAR_DOWN_DRAG_AND_DROP_INPUT = 1;
    private AtomicReference<WindowManagerInternal.IDragDropCallback> mCallback = new AtomicReference<>(new WindowManagerInternal.IDragDropCallback() {
    });
    /* access modifiers changed from: private */
    public DragState mDragState;
    private final Handler mHandler;
    private WindowManagerService mService;

    /* access modifiers changed from: package-private */
    public boolean dragDropActiveLocked() {
        DragState dragState = this.mDragState;
        return dragState != null && !dragState.isClosing();
    }

    /* access modifiers changed from: package-private */
    public void registerCallback(WindowManagerInternal.IDragDropCallback callback) {
        Preconditions.checkNotNull(callback);
        this.mCallback.set(callback);
    }

    DragDropController(WindowManagerService service, Looper looper) {
        this.mService = service;
        this.mHandler = new DragHandler(service, looper);
    }

    /* access modifiers changed from: package-private */
    public void sendDragStartedIfNeededLocked(WindowState window) {
        this.mDragState.sendDragStartedIfNeededLocked(window);
    }

    /* Debug info: failed to restart local var, previous not found, register: 19 */
    /* access modifiers changed from: package-private */
    /* JADX WARNING: Code restructure failed: missing block: B:106:0x01b2, code lost:
        com.android.server.wm.WindowManagerService.resetPriorityAfterLockedSection();
        r8.mCallback.get().postPerformDrag();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:107:0x01c1, code lost:
        return null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:131:?, code lost:
        com.android.server.wm.WindowManagerService.resetPriorityAfterLockedSection();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:132:0x0234, code lost:
        r8.mCallback.get().postPerformDrag();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:133:0x0240, code lost:
        return r9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:161:0x0291, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:20:0x0058, code lost:
        com.android.server.wm.WindowManagerService.resetPriorityAfterLockedSection();
        r8.mCallback.get().postPerformDrag();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:21:0x0066, code lost:
        return null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:41:0x00a6, code lost:
        com.android.server.wm.WindowManagerService.resetPriorityAfterLockedSection();
        r8.mCallback.get().postPerformDrag();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:42:0x00b4, code lost:
        return null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:59:0x00ed, code lost:
        com.android.server.wm.WindowManagerService.resetPriorityAfterLockedSection();
        r8.mCallback.get().postPerformDrag();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:60:0x00fb, code lost:
        return null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:77:0x0122, code lost:
        com.android.server.wm.WindowManagerService.resetPriorityAfterLockedSection();
        r8.mCallback.get().postPerformDrag();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:78:0x0130, code lost:
        return null;
     */
    /* JADX WARNING: Removed duplicated region for block: B:148:0x0275 A[SYNTHETIC, Splitter:B:148:0x0275] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public android.os.IBinder performDrag(android.view.SurfaceSession r20, int r21, int r22, android.view.IWindow r23, int r24, android.view.SurfaceControl r25, int r26, float r27, float r28, float r29, float r30, android.content.ClipData r31) {
        /*
            r19 = this;
            r8 = r19
            r6 = r23
            r5 = r27
            r3 = r28
            r2 = r29
            r1 = r30
            android.os.Binder r0 = new android.os.Binder
            r0.<init>()
            r4 = r0
            java.util.concurrent.atomic.AtomicReference<com.android.server.wm.WindowManagerInternal$IDragDropCallback> r0 = r8.mCallback
            java.lang.Object r0 = r0.get()
            r9 = r0
            com.android.server.wm.WindowManagerInternal$IDragDropCallback r9 = (com.android.server.wm.WindowManagerInternal.IDragDropCallback) r9
            r10 = r23
            r11 = r4
            r12 = r26
            r13 = r27
            r14 = r28
            r15 = r29
            r16 = r30
            r17 = r31
            boolean r9 = r9.prePerformDrag(r10, r11, r12, r13, r14, r15, r16, r17)
            com.android.server.wm.WindowManagerService r0 = r8.mService     // Catch:{ all -> 0x0293 }
            com.android.server.wm.WindowManagerGlobalLock r10 = r0.mGlobalLock     // Catch:{ all -> 0x0293 }
            monitor-enter(r10)     // Catch:{ all -> 0x0293 }
            com.android.server.wm.WindowManagerService.boostPriorityForLockedSection()     // Catch:{ all -> 0x0268 }
            r0 = 0
            if (r9 != 0) goto L_0x0081
            java.lang.String r7 = "WindowManager"
            java.lang.String r11 = "IDragDropCallback rejects the performDrag request"
            android.util.Slog.w(r7, r11)     // Catch:{ all -> 0x0074 }
            if (r25 == 0) goto L_0x0046
            r25.release()     // Catch:{ all -> 0x0067 }
        L_0x0046:
            com.android.server.wm.DragState r7 = r8.mDragState     // Catch:{ all -> 0x0067 }
            if (r7 == 0) goto L_0x0057
            com.android.server.wm.DragState r7 = r8.mDragState     // Catch:{ all -> 0x0067 }
            boolean r7 = r7.isInProgress()     // Catch:{ all -> 0x0067 }
            if (r7 != 0) goto L_0x0057
            com.android.server.wm.DragState r7 = r8.mDragState     // Catch:{ all -> 0x0067 }
            r7.closeLocked()     // Catch:{ all -> 0x0067 }
        L_0x0057:
            monitor-exit(r10)     // Catch:{ all -> 0x0067 }
            com.android.server.wm.WindowManagerService.resetPriorityAfterLockedSection()
            java.util.concurrent.atomic.AtomicReference<com.android.server.wm.WindowManagerInternal$IDragDropCallback> r7 = r8.mCallback
            java.lang.Object r7 = r7.get()
            com.android.server.wm.WindowManagerInternal$IDragDropCallback r7 = (com.android.server.wm.WindowManagerInternal.IDragDropCallback) r7
            r7.postPerformDrag()
            return r0
        L_0x0067:
            r0 = move-exception
            r15 = r26
            r13 = r3
            r6 = r5
            r16 = r9
            r3 = r1
            r9 = r4
            r1 = r25
            goto L_0x028c
        L_0x0074:
            r0 = move-exception
            r15 = r26
            r13 = r3
            r6 = r5
            r16 = r9
            r3 = r1
            r9 = r4
            r1 = r25
            goto L_0x0273
        L_0x0081:
            boolean r7 = r19.dragDropActiveLocked()     // Catch:{ all -> 0x0268 }
            if (r7 == 0) goto L_0x00b5
            java.lang.String r7 = "WindowManager"
            java.lang.String r11 = "Drag already in progress"
            android.util.Slog.w(r7, r11)     // Catch:{ all -> 0x0074 }
            if (r25 == 0) goto L_0x0094
            r25.release()     // Catch:{ all -> 0x0067 }
        L_0x0094:
            com.android.server.wm.DragState r7 = r8.mDragState     // Catch:{ all -> 0x0067 }
            if (r7 == 0) goto L_0x00a5
            com.android.server.wm.DragState r7 = r8.mDragState     // Catch:{ all -> 0x0067 }
            boolean r7 = r7.isInProgress()     // Catch:{ all -> 0x0067 }
            if (r7 != 0) goto L_0x00a5
            com.android.server.wm.DragState r7 = r8.mDragState     // Catch:{ all -> 0x0067 }
            r7.closeLocked()     // Catch:{ all -> 0x0067 }
        L_0x00a5:
            monitor-exit(r10)     // Catch:{ all -> 0x0067 }
            com.android.server.wm.WindowManagerService.resetPriorityAfterLockedSection()
            java.util.concurrent.atomic.AtomicReference<com.android.server.wm.WindowManagerInternal$IDragDropCallback> r7 = r8.mCallback
            java.lang.Object r7 = r7.get()
            com.android.server.wm.WindowManagerInternal$IDragDropCallback r7 = (com.android.server.wm.WindowManagerInternal.IDragDropCallback) r7
            r7.postPerformDrag()
            return r0
        L_0x00b5:
            com.android.server.wm.WindowManagerService r7 = r8.mService     // Catch:{ all -> 0x0268 }
            r11 = 0
            com.android.server.wm.WindowState r7 = r7.windowForClientLocked((com.android.server.wm.Session) r0, (android.view.IWindow) r6, (boolean) r11)     // Catch:{ all -> 0x0268 }
            r11 = r7
            if (r11 != 0) goto L_0x00fc
            java.lang.String r7 = "WindowManager"
            java.lang.StringBuilder r12 = new java.lang.StringBuilder     // Catch:{ all -> 0x0074 }
            r12.<init>()     // Catch:{ all -> 0x0074 }
            java.lang.String r13 = "Bad requesting window "
            r12.append(r13)     // Catch:{ all -> 0x0074 }
            r12.append(r6)     // Catch:{ all -> 0x0074 }
            java.lang.String r12 = r12.toString()     // Catch:{ all -> 0x0074 }
            android.util.Slog.w(r7, r12)     // Catch:{ all -> 0x0074 }
            if (r25 == 0) goto L_0x00db
            r25.release()     // Catch:{ all -> 0x0067 }
        L_0x00db:
            com.android.server.wm.DragState r7 = r8.mDragState     // Catch:{ all -> 0x0067 }
            if (r7 == 0) goto L_0x00ec
            com.android.server.wm.DragState r7 = r8.mDragState     // Catch:{ all -> 0x0067 }
            boolean r7 = r7.isInProgress()     // Catch:{ all -> 0x0067 }
            if (r7 != 0) goto L_0x00ec
            com.android.server.wm.DragState r7 = r8.mDragState     // Catch:{ all -> 0x0067 }
            r7.closeLocked()     // Catch:{ all -> 0x0067 }
        L_0x00ec:
            monitor-exit(r10)     // Catch:{ all -> 0x0067 }
            com.android.server.wm.WindowManagerService.resetPriorityAfterLockedSection()
            java.util.concurrent.atomic.AtomicReference<com.android.server.wm.WindowManagerInternal$IDragDropCallback> r7 = r8.mCallback
            java.lang.Object r7 = r7.get()
            com.android.server.wm.WindowManagerInternal$IDragDropCallback r7 = (com.android.server.wm.WindowManagerInternal.IDragDropCallback) r7
            r7.postPerformDrag()
            return r0
        L_0x00fc:
            com.android.server.wm.DisplayContent r7 = r11.getDisplayContent()     // Catch:{ all -> 0x0268 }
            r12 = r7
            if (r12 != 0) goto L_0x0131
            java.lang.String r7 = "WindowManager"
            java.lang.String r13 = "display content is null"
            android.util.Slog.w(r7, r13)     // Catch:{ all -> 0x0074 }
            if (r25 == 0) goto L_0x0110
            r25.release()     // Catch:{ all -> 0x0067 }
        L_0x0110:
            com.android.server.wm.DragState r7 = r8.mDragState     // Catch:{ all -> 0x0067 }
            if (r7 == 0) goto L_0x0121
            com.android.server.wm.DragState r7 = r8.mDragState     // Catch:{ all -> 0x0067 }
            boolean r7 = r7.isInProgress()     // Catch:{ all -> 0x0067 }
            if (r7 != 0) goto L_0x0121
            com.android.server.wm.DragState r7 = r8.mDragState     // Catch:{ all -> 0x0067 }
            r7.closeLocked()     // Catch:{ all -> 0x0067 }
        L_0x0121:
            monitor-exit(r10)     // Catch:{ all -> 0x0067 }
            com.android.server.wm.WindowManagerService.resetPriorityAfterLockedSection()
            java.util.concurrent.atomic.AtomicReference<com.android.server.wm.WindowManagerInternal$IDragDropCallback> r7 = r8.mCallback
            java.lang.Object r7 = r7.get()
            com.android.server.wm.WindowManagerInternal$IDragDropCallback r7 = (com.android.server.wm.WindowManagerInternal.IDragDropCallback) r7
            r7.postPerformDrag()
            return r0
        L_0x0131:
            r13 = r24
            r7 = r13 & 512(0x200, float:7.175E-43)
            if (r7 != 0) goto L_0x013b
            r7 = 1060439169(0x3f350481, float:0.7071)
            goto L_0x013d
        L_0x013b:
            r7 = 1065353216(0x3f800000, float:1.0)
        L_0x013d:
            r14 = r7
            android.os.IBinder r7 = r23.asBinder()     // Catch:{ all -> 0x0268 }
            android.os.Binder r15 = new android.os.Binder     // Catch:{ all -> 0x0268 }
            r15.<init>()     // Catch:{ all -> 0x0268 }
            r16 = r9
            r9 = r4
            r4 = r15
            com.android.server.wm.DragState r15 = new com.android.server.wm.DragState     // Catch:{ all -> 0x025f }
            com.android.server.wm.WindowManagerService r0 = r8.mService     // Catch:{ all -> 0x025f }
            r13 = r1
            r1 = r15
            r13 = r2
            r2 = r0
            r13 = r3
            r3 = r19
            r13 = r5
            r5 = r25
            r6 = r24
            r1.<init>(r2, r3, r4, r5, r6, r7)     // Catch:{ all -> 0x0254 }
            r8.mDragState = r15     // Catch:{ all -> 0x0254 }
            r1 = 0
            com.android.server.wm.DragState r0 = r8.mDragState     // Catch:{ all -> 0x024b }
            r2 = r21
            r0.mPid = r2     // Catch:{ all -> 0x024b }
            com.android.server.wm.DragState r0 = r8.mDragState     // Catch:{ all -> 0x024b }
            r3 = r22
            r0.mUid = r3     // Catch:{ all -> 0x024b }
            com.android.server.wm.DragState r0 = r8.mDragState     // Catch:{ all -> 0x024b }
            r0.mOriginalAlpha = r14     // Catch:{ all -> 0x024b }
            com.android.server.wm.DragState r0 = r8.mDragState     // Catch:{ all -> 0x024b }
            r0.mToken = r9     // Catch:{ all -> 0x024b }
            com.android.server.wm.DragState r0 = r8.mDragState     // Catch:{ all -> 0x024b }
            r0.mDisplayContent = r12     // Catch:{ all -> 0x024b }
            android.view.Display r0 = r12.getDisplay()     // Catch:{ all -> 0x024b }
            java.util.concurrent.atomic.AtomicReference<com.android.server.wm.WindowManagerInternal$IDragDropCallback> r5 = r8.mCallback     // Catch:{ all -> 0x024b }
            java.lang.Object r5 = r5.get()     // Catch:{ all -> 0x024b }
            com.android.server.wm.WindowManagerInternal$IDragDropCallback r5 = (com.android.server.wm.WindowManagerInternal.IDragDropCallback) r5     // Catch:{ all -> 0x024b }
            com.android.server.wm.DragState r6 = r8.mDragState     // Catch:{ all -> 0x024b }
            com.android.server.wm.WindowManagerService r15 = r8.mService     // Catch:{ all -> 0x024b }
            com.android.server.input.InputManagerService r15 = r15.mInputManager     // Catch:{ all -> 0x024b }
            android.view.InputChannel r2 = r11.mInputChannel     // Catch:{ all -> 0x024b }
            boolean r2 = r5.registerInputChannel(r6, r0, r15, r2)     // Catch:{ all -> 0x024b }
            if (r2 != 0) goto L_0x01cc
            java.lang.String r2 = "WindowManager"
            java.lang.String r5 = "Unable to transfer touch focus"
            android.util.Slog.e(r2, r5)     // Catch:{ all -> 0x024b }
            if (r1 == 0) goto L_0x01a0
            r1.release()     // Catch:{ all -> 0x01c2 }
        L_0x01a0:
            com.android.server.wm.DragState r2 = r8.mDragState     // Catch:{ all -> 0x01c2 }
            if (r2 == 0) goto L_0x01b1
            com.android.server.wm.DragState r2 = r8.mDragState     // Catch:{ all -> 0x01c2 }
            boolean r2 = r2.isInProgress()     // Catch:{ all -> 0x01c2 }
            if (r2 != 0) goto L_0x01b1
            com.android.server.wm.DragState r2 = r8.mDragState     // Catch:{ all -> 0x01c2 }
            r2.closeLocked()     // Catch:{ all -> 0x01c2 }
        L_0x01b1:
            monitor-exit(r10)     // Catch:{ all -> 0x01c2 }
            com.android.server.wm.WindowManagerService.resetPriorityAfterLockedSection()
            java.util.concurrent.atomic.AtomicReference<com.android.server.wm.WindowManagerInternal$IDragDropCallback> r2 = r8.mCallback
            java.lang.Object r2 = r2.get()
            com.android.server.wm.WindowManagerInternal$IDragDropCallback r2 = (com.android.server.wm.WindowManagerInternal.IDragDropCallback) r2
            r2.postPerformDrag()
            r2 = 0
            return r2
        L_0x01c2:
            r0 = move-exception
            r15 = r26
            r3 = r30
            r6 = r13
            r13 = r28
            goto L_0x028c
        L_0x01cc:
            com.android.server.wm.DragState r2 = r8.mDragState     // Catch:{ all -> 0x024b }
            r5 = r31
            r2.mData = r5     // Catch:{ all -> 0x024b }
            com.android.server.wm.DragState r2 = r8.mDragState     // Catch:{ all -> 0x024b }
            r6 = r13
            r13 = r28
            r2.broadcastDragStartedLocked(r6, r13)     // Catch:{ all -> 0x0245 }
            com.android.server.wm.DragState r2 = r8.mDragState     // Catch:{ all -> 0x0245 }
            r15 = r26
            r2.overridePointerIconLocked(r15)     // Catch:{ all -> 0x0243 }
            com.android.server.wm.DragState r2 = r8.mDragState     // Catch:{ all -> 0x0243 }
            r3 = r29
            r2.mThumbOffsetX = r3     // Catch:{ all -> 0x0243 }
            com.android.server.wm.DragState r2 = r8.mDragState     // Catch:{ all -> 0x0243 }
            r3 = r30
            r2.mThumbOffsetY = r3     // Catch:{ all -> 0x0241 }
            com.android.server.wm.DragState r2 = r8.mDragState     // Catch:{ all -> 0x0241 }
            android.view.SurfaceControl r2 = r2.mSurfaceControl     // Catch:{ all -> 0x0241 }
            android.view.SurfaceControl$Transaction r17 = r11.getPendingTransaction()     // Catch:{ all -> 0x0241 }
            r25 = r17
            r17 = r0
            com.android.server.wm.DragState r0 = r8.mDragState     // Catch:{ all -> 0x0241 }
            float r0 = r0.mOriginalAlpha     // Catch:{ all -> 0x0241 }
            r18 = r4
            r4 = r25
            r4.setAlpha(r2, r0)     // Catch:{ all -> 0x0241 }
            float r0 = r6 - r29
            float r5 = r13 - r3
            r4.setPosition(r2, r0, r5)     // Catch:{ all -> 0x0241 }
            r4.show(r2)     // Catch:{ all -> 0x0241 }
            r12.reparentToOverlay(r4, r2)     // Catch:{ all -> 0x0241 }
            r11.scheduleAnimation()     // Catch:{ all -> 0x0241 }
            com.android.server.wm.DragState r0 = r8.mDragState     // Catch:{ all -> 0x0241 }
            r0.notifyLocationLocked(r6, r13)     // Catch:{ all -> 0x0241 }
            if (r1 == 0) goto L_0x021f
            r1.release()     // Catch:{ all -> 0x028b }
        L_0x021f:
            com.android.server.wm.DragState r0 = r8.mDragState     // Catch:{ all -> 0x028b }
            if (r0 == 0) goto L_0x0230
            com.android.server.wm.DragState r0 = r8.mDragState     // Catch:{ all -> 0x028b }
            boolean r0 = r0.isInProgress()     // Catch:{ all -> 0x028b }
            if (r0 != 0) goto L_0x0230
            com.android.server.wm.DragState r0 = r8.mDragState     // Catch:{ all -> 0x028b }
            r0.closeLocked()     // Catch:{ all -> 0x028b }
        L_0x0230:
            monitor-exit(r10)     // Catch:{ all -> 0x028b }
            com.android.server.wm.WindowManagerService.resetPriorityAfterLockedSection()     // Catch:{ all -> 0x0291 }
            java.util.concurrent.atomic.AtomicReference<com.android.server.wm.WindowManagerInternal$IDragDropCallback> r0 = r8.mCallback
            java.lang.Object r0 = r0.get()
            com.android.server.wm.WindowManagerInternal$IDragDropCallback r0 = (com.android.server.wm.WindowManagerInternal.IDragDropCallback) r0
            r0.postPerformDrag()
            return r9
        L_0x0241:
            r0 = move-exception
            goto L_0x0273
        L_0x0243:
            r0 = move-exception
            goto L_0x0248
        L_0x0245:
            r0 = move-exception
            r15 = r26
        L_0x0248:
            r3 = r30
            goto L_0x0273
        L_0x024b:
            r0 = move-exception
            r15 = r26
            r3 = r30
            r6 = r13
            r13 = r28
            goto L_0x0273
        L_0x0254:
            r0 = move-exception
            r15 = r26
            r3 = r30
            r6 = r13
            r13 = r28
            r1 = r25
            goto L_0x0273
        L_0x025f:
            r0 = move-exception
            r15 = r26
            r13 = r3
            r6 = r5
            r3 = r1
            r1 = r25
            goto L_0x0273
        L_0x0268:
            r0 = move-exception
            r15 = r26
            r13 = r3
            r6 = r5
            r16 = r9
            r3 = r1
            r9 = r4
            r1 = r25
        L_0x0273:
            if (r1 == 0) goto L_0x0278
            r1.release()     // Catch:{ all -> 0x028b }
        L_0x0278:
            com.android.server.wm.DragState r2 = r8.mDragState     // Catch:{ all -> 0x028b }
            if (r2 == 0) goto L_0x0289
            com.android.server.wm.DragState r2 = r8.mDragState     // Catch:{ all -> 0x028b }
            boolean r2 = r2.isInProgress()     // Catch:{ all -> 0x028b }
            if (r2 != 0) goto L_0x0289
            com.android.server.wm.DragState r2 = r8.mDragState     // Catch:{ all -> 0x028b }
            r2.closeLocked()     // Catch:{ all -> 0x028b }
        L_0x0289:
            throw r0     // Catch:{ all -> 0x028b }
        L_0x028b:
            r0 = move-exception
        L_0x028c:
            monitor-exit(r10)     // Catch:{ all -> 0x028b }
            com.android.server.wm.WindowManagerService.resetPriorityAfterLockedSection()     // Catch:{ all -> 0x0291 }
            throw r0     // Catch:{ all -> 0x0291 }
        L_0x0291:
            r0 = move-exception
            goto L_0x029e
        L_0x0293:
            r0 = move-exception
            r15 = r26
            r13 = r3
            r6 = r5
            r16 = r9
            r3 = r1
            r9 = r4
            r1 = r25
        L_0x029e:
            java.util.concurrent.atomic.AtomicReference<com.android.server.wm.WindowManagerInternal$IDragDropCallback> r2 = r8.mCallback
            java.lang.Object r2 = r2.get()
            com.android.server.wm.WindowManagerInternal$IDragDropCallback r2 = (com.android.server.wm.WindowManagerInternal.IDragDropCallback) r2
            r2.postPerformDrag()
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.wm.DragDropController.performDrag(android.view.SurfaceSession, int, int, android.view.IWindow, int, android.view.SurfaceControl, int, float, float, float, float, android.content.ClipData):android.os.IBinder");
    }

    /* Debug info: failed to restart local var, previous not found, register: 6 */
    /* access modifiers changed from: package-private */
    public void reportDropResult(IWindow window, boolean consumed) {
        IBinder token = window.asBinder();
        this.mCallback.get().preReportDropResult(window, consumed);
        try {
            synchronized (this.mService.mGlobalLock) {
                WindowManagerService.boostPriorityForLockedSection();
                if (this.mDragState == null) {
                    Slog.w(DisplayPolicy.TAG, "Drop result given but no drag in progress");
                    WindowManagerService.resetPriorityAfterLockedSection();
                    this.mCallback.get().postReportDropResult();
                } else if (this.mDragState.mToken == token) {
                    this.mHandler.removeMessages(0, window.asBinder());
                    if (this.mService.windowForClientLocked((Session) null, window, false) == null) {
                        Slog.w(DisplayPolicy.TAG, "Bad result-reporting window " + window);
                        WindowManagerService.resetPriorityAfterLockedSection();
                        this.mCallback.get().postReportDropResult();
                        return;
                    }
                    this.mDragState.mDragResult = consumed;
                    this.mDragState.endDragLocked();
                    WindowManagerService.resetPriorityAfterLockedSection();
                    this.mCallback.get().postReportDropResult();
                } else {
                    Slog.w(DisplayPolicy.TAG, "Invalid drop-result claim by " + window);
                    throw new IllegalStateException("reportDropResult() by non-recipient");
                }
            }
        } catch (Throwable th) {
            this.mCallback.get().postReportDropResult();
            throw th;
        }
    }

    /* Debug info: failed to restart local var, previous not found, register: 3 */
    /* access modifiers changed from: package-private */
    public void cancelDragAndDrop(IBinder dragToken, boolean skipAnimation) {
        this.mCallback.get().preCancelDragAndDrop(dragToken);
        try {
            synchronized (this.mService.mGlobalLock) {
                WindowManagerService.boostPriorityForLockedSection();
                if (this.mDragState == null) {
                    Slog.w(DisplayPolicy.TAG, "cancelDragAndDrop() without prepareDrag()");
                    throw new IllegalStateException("cancelDragAndDrop() without prepareDrag()");
                } else if (this.mDragState.mToken == dragToken) {
                    this.mDragState.mDragResult = false;
                    this.mDragState.cancelDragLocked(skipAnimation);
                } else {
                    Slog.w(DisplayPolicy.TAG, "cancelDragAndDrop() does not match prepareDrag()");
                    throw new IllegalStateException("cancelDragAndDrop() does not match prepareDrag()");
                }
            }
            WindowManagerService.resetPriorityAfterLockedSection();
            this.mCallback.get().postCancelDragAndDrop();
        } catch (Throwable th) {
            this.mCallback.get().postCancelDragAndDrop();
            throw th;
        }
    }

    /* access modifiers changed from: package-private */
    /* JADX WARNING: Code restructure failed: missing block: B:13:0x0021, code lost:
        com.android.server.wm.WindowManagerService.resetPriorityAfterLockedSection();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:14:0x0024, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void handleMotionEvent(boolean r3, float r4, float r5) {
        /*
            r2 = this;
            com.android.server.wm.WindowManagerService r0 = r2.mService
            com.android.server.wm.WindowManagerGlobalLock r0 = r0.mGlobalLock
            monitor-enter(r0)
            com.android.server.wm.WindowManagerService.boostPriorityForLockedSection()     // Catch:{ all -> 0x0025 }
            boolean r1 = r2.dragDropActiveLocked()     // Catch:{ all -> 0x0025 }
            if (r1 != 0) goto L_0x0013
            monitor-exit(r0)     // Catch:{ all -> 0x0025 }
            com.android.server.wm.WindowManagerService.resetPriorityAfterLockedSection()
            return
        L_0x0013:
            if (r3 == 0) goto L_0x001b
            com.android.server.wm.DragState r1 = r2.mDragState     // Catch:{ all -> 0x0025 }
            r1.notifyMoveLocked(r4, r5)     // Catch:{ all -> 0x0025 }
            goto L_0x0020
        L_0x001b:
            com.android.server.wm.DragState r1 = r2.mDragState     // Catch:{ all -> 0x0025 }
            r1.notifyDropLocked(r4, r5)     // Catch:{ all -> 0x0025 }
        L_0x0020:
            monitor-exit(r0)     // Catch:{ all -> 0x0025 }
            com.android.server.wm.WindowManagerService.resetPriorityAfterLockedSection()
            return
        L_0x0025:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x0025 }
            com.android.server.wm.WindowManagerService.resetPriorityAfterLockedSection()
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.wm.DragDropController.handleMotionEvent(boolean, float, float):void");
    }

    /* access modifiers changed from: package-private */
    public void dragRecipientEntered(IWindow window) {
    }

    /* access modifiers changed from: package-private */
    public void dragRecipientExited(IWindow window) {
    }

    /* access modifiers changed from: package-private */
    public void sendHandlerMessage(int what, Object arg) {
        this.mHandler.obtainMessage(what, arg).sendToTarget();
    }

    /* access modifiers changed from: package-private */
    public void sendTimeoutMessage(int what, Object arg) {
        this.mHandler.removeMessages(what, arg);
        this.mHandler.sendMessageDelayed(this.mHandler.obtainMessage(what, arg), DRAG_TIMEOUT_MS);
    }

    /* access modifiers changed from: package-private */
    public void onDragStateClosedLocked(DragState dragState) {
        if (this.mDragState != dragState) {
            Slog.wtf(DisplayPolicy.TAG, "Unknown drag state is closed");
        } else {
            this.mDragState = null;
        }
    }

    private class DragHandler extends Handler {
        private final WindowManagerService mService;

        DragHandler(WindowManagerService service, Looper looper) {
            super(looper);
            this.mService = service;
        }

        public void handleMessage(Message msg) {
            int i = msg.what;
            if (i == 0) {
                IBinder iBinder = (IBinder) msg.obj;
                synchronized (this.mService.mGlobalLock) {
                    try {
                        WindowManagerService.boostPriorityForLockedSection();
                        if (DragDropController.this.mDragState != null) {
                            DragDropController.this.mDragState.mDragResult = false;
                            DragDropController.this.mDragState.endDragLocked();
                        }
                    } catch (Throwable th) {
                        while (true) {
                            WindowManagerService.resetPriorityAfterLockedSection();
                            throw th;
                        }
                    }
                }
                WindowManagerService.resetPriorityAfterLockedSection();
            } else if (i == 1) {
                DragState.InputInterceptor interceptor = (DragState.InputInterceptor) msg.obj;
                if (interceptor != null) {
                    synchronized (this.mService.mGlobalLock) {
                        try {
                            WindowManagerService.boostPriorityForLockedSection();
                            interceptor.tearDown();
                        } catch (Throwable th2) {
                            while (true) {
                                WindowManagerService.resetPriorityAfterLockedSection();
                                throw th2;
                            }
                        }
                    }
                    WindowManagerService.resetPriorityAfterLockedSection();
                }
            } else if (i == 2) {
                synchronized (this.mService.mGlobalLock) {
                    try {
                        WindowManagerService.boostPriorityForLockedSection();
                        if (DragDropController.this.mDragState == null) {
                            Slog.wtf(DisplayPolicy.TAG, "mDragState unexpectedly became null while plyaing animation");
                            WindowManagerService.resetPriorityAfterLockedSection();
                            return;
                        }
                        DragDropController.this.mDragState.closeLocked();
                        WindowManagerService.resetPriorityAfterLockedSection();
                    } catch (Throwable th3) {
                        while (true) {
                            WindowManagerService.resetPriorityAfterLockedSection();
                            throw th3;
                        }
                    }
                }
            }
        }
    }
}
