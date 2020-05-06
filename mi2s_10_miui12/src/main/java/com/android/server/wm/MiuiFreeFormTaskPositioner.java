package com.android.server.wm;

import android.graphics.Rect;
import android.util.MiuiMultiWindowUtils;
import android.util.Slog;
import com.android.server.wm.MiuiFreeFormGestureAnimator;
import java.util.Iterator;

public class MiuiFreeFormTaskPositioner {
    private static final String TAG = "MiuiFreeFormTaskPositioner";
    private MiuiFreeFormWindowMotionHelper mFreeFormWindowMotionHelper;
    final Rect mLastWindowDragBounds = new Rect();
    private MiuiFreeFormGesturePointerEventListener mListener;
    final Rect mWindowDragBounds = new Rect();

    public MiuiFreeFormTaskPositioner(MiuiFreeFormGesturePointerEventListener listener) {
        this.mListener = listener;
        this.mFreeFormWindowMotionHelper = this.mListener.mFreeFormWindowMotionHelper;
    }

    public Rect getDragWindowBounds() {
        return this.mWindowDragBounds;
    }

    public void updateWindowMoveBounds(float x, float y, float startDragX, float startDragY, boolean isVertical, Rect windowOriginalBounds) {
        int offsetX = Math.round(x - startDragX);
        int offsetY = Math.round(y - startDragY);
        if (this.mWindowDragBounds.isEmpty()) {
            this.mLastWindowDragBounds.set(windowOriginalBounds);
        } else {
            this.mLastWindowDragBounds.set(this.mWindowDragBounds);
        }
        this.mWindowDragBounds.set(windowOriginalBounds);
        this.mWindowDragBounds.offsetTo(windowOriginalBounds.left + offsetX, windowOriginalBounds.top + offsetY);
        if (this.mListener.mIsPortrait && this.mWindowDragBounds.top < MiuiMultiWindowUtils.getStatusBarHeight(this.mListener.mService.mContext, this.mWindowDragBounds.top)) {
            Rect rect = this.mWindowDragBounds;
            rect.offsetTo(rect.left, MiuiMultiWindowUtils.getStatusBarHeight(this.mListener.mService.mContext, this.mWindowDragBounds.top));
        }
        this.mFreeFormWindowMotionHelper.mCurrentCenterPositionX = (float) this.mWindowDragBounds.centerX();
        this.mFreeFormWindowMotionHelper.mCurrentCenterPositionY = (float) this.mWindowDragBounds.centerY();
        if (MiuiFreeFormGestureController.DEBUG) {
            Slog.d(TAG, "updateWindowMoveBounds startDragX" + startDragX + " startDragY:" + startDragY + " x:" + x + " y:" + y + "offsetX:" + offsetX + " offsetY:" + offsetY + " windowDragBounds:" + this.mWindowDragBounds + " windowOriginalBounds:" + windowOriginalBounds + " LastWindowDragBounds:" + this.mLastWindowDragBounds);
        }
        if (!windowOriginalBounds.equals(this.mWindowDragBounds)) {
            translateScaleAnimal(this.mLastWindowDragBounds, this.mWindowDragBounds, 8, false);
        }
    }

    /* JADX INFO: finally extract failed */
    /* JADX WARNING: Code restructure failed: missing block: B:45:0x01b4, code lost:
        r0 = th;
     */
    /* JADX WARNING: Removed duplicated region for block: B:10:0x0047  */
    /* JADX WARNING: Removed duplicated region for block: B:11:0x006a  */
    /* JADX WARNING: Removed duplicated region for block: B:14:0x00ca  */
    /* JADX WARNING: Removed duplicated region for block: B:17:0x00e2  */
    /* JADX WARNING: Removed duplicated region for block: B:20:0x00fa  */
    /* JADX WARNING: Removed duplicated region for block: B:23:0x0112  */
    /* JADX WARNING: Removed duplicated region for block: B:26:0x012c  */
    /* JADX WARNING: Removed duplicated region for block: B:29:0x013a  */
    /* JADX WARNING: Removed duplicated region for block: B:46:0x01b6  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void updateWindowUpBounds(float r18, float r19, float r20, float r21, boolean r22, android.graphics.Rect r23) {
        /*
            r17 = this;
            r1 = r17
            r2 = r18
            r3 = r19
            r4 = r20
            r5 = r21
            r6 = r23
            com.android.server.wm.MiuiFreeFormGesturePointerEventListener r0 = r1.mListener
            android.graphics.Rect r0 = r0.mStackBounds
            int r0 = r0.left
            com.android.server.wm.MiuiFreeFormGesturePointerEventListener r7 = r1.mListener
            android.graphics.Rect r7 = r7.mStackBounds
            int r7 = r7.right
            float r7 = (float) r7
            int r8 = r23.width()
            float r8 = (float) r8
            float r9 = android.util.MiuiMultiWindowUtils.sScale
            float r8 = r8 * r9
            float r7 = r7 - r8
            int r7 = (int) r7
            com.android.server.wm.MiuiFreeFormGesturePointerEventListener r8 = r1.mListener
            boolean r8 = r8.mIsNotchScreen
            r9 = 1
            if (r8 == 0) goto L_0x0043
            com.android.server.wm.MiuiFreeFormGesturePointerEventListener r8 = r1.mListener
            int r8 = r8.mDisplayRotation
            if (r8 != r9) goto L_0x0036
            int r8 = android.util.MiuiMultiWindowUtils.NOTCH_MARGIN
            int r0 = r0 + r8
            r8 = r7
            r7 = r0
            goto L_0x0045
        L_0x0036:
            com.android.server.wm.MiuiFreeFormGesturePointerEventListener r8 = r1.mListener
            int r8 = r8.mDisplayRotation
            r10 = 3
            if (r8 != r10) goto L_0x0043
            int r8 = android.util.MiuiMultiWindowUtils.NOTCH_MARGIN
            int r7 = r7 - r8
            r8 = r7
            r7 = r0
            goto L_0x0045
        L_0x0043:
            r8 = r7
            r7 = r0
        L_0x0045:
            if (r22 == 0) goto L_0x006a
            com.android.server.wm.MiuiFreeFormGesturePointerEventListener r0 = r1.mListener
            android.graphics.Rect r0 = r0.mStackBounds
            int r0 = r0.bottom
            float r0 = (float) r0
            int r10 = r23.height()
            float r10 = (float) r10
            float r11 = android.util.MiuiMultiWindowUtils.sScale
            float r10 = r10 * r11
            float r0 = r0 - r10
            int r0 = (int) r0
            com.android.server.wm.MiuiFreeFormGesturePointerEventListener r10 = r1.mListener
            com.android.server.wm.WindowManagerService r10 = r10.mService
            android.content.Context r10 = r10.mContext
            com.android.server.wm.MiuiFreeFormGesturePointerEventListener r11 = r1.mListener
            android.graphics.Rect r11 = r11.mStackBounds
            int r11 = r11.top
            int r10 = android.util.MiuiMultiWindowUtils.getStatusBarHeight(r10, r11)
            r11 = r0
            goto L_0x00a8
        L_0x006a:
            r0 = 1108439204(0x421170a4, float:36.36)
            com.android.server.wm.MiuiFreeFormGesturePointerEventListener r10 = r1.mListener
            com.android.server.wm.WindowManagerService r10 = r10.mService
            android.content.Context r10 = r10.mContext
            android.content.res.Resources r10 = r10.getResources()
            android.util.DisplayMetrics r10 = r10.getDisplayMetrics()
            float r10 = r10.density
            float r10 = r10 * r0
            r0 = 1056964608(0x3f000000, float:0.5)
            float r10 = r10 + r0
            int r0 = (int) r10
            com.android.server.wm.MiuiFreeFormGesturePointerEventListener r10 = r1.mListener
            android.graphics.Rect r10 = r10.mStackBounds
            int r10 = r10.height()
            int r11 = android.util.MiuiMultiWindowUtils.FREEFORM_LANDCAPE_HEIGHT
            int r10 = r10 - r11
            int r10 = r10 / 2
            float r10 = (float) r10
            com.android.server.wm.MiuiFreeFormGesturePointerEventListener r11 = r1.mListener
            android.graphics.Rect r11 = r11.mStackBounds
            int r11 = r11.bottom
            float r11 = (float) r11
            int r12 = r23.height()
            float r12 = (float) r12
            float r13 = android.util.MiuiMultiWindowUtils.sScale
            float r12 = r12 * r13
            float r11 = r11 - r12
            float r11 = r11 - r10
            int r11 = (int) r11
            com.android.server.wm.MiuiFreeFormGesturePointerEventListener r12 = r1.mListener
            android.graphics.Rect r12 = r12.mStackBounds
            int r10 = r12.top
        L_0x00a8:
            float r0 = r2 - r4
            int r12 = java.lang.Math.round(r0)
            float r0 = r3 - r5
            int r13 = java.lang.Math.round(r0)
            android.graphics.Rect r0 = r1.mWindowDragBounds
            r0.set(r6)
            android.graphics.Rect r0 = r1.mWindowDragBounds
            int r14 = r6.left
            int r14 = r14 + r12
            int r15 = r6.top
            int r15 = r15 + r13
            r0.offsetTo(r14, r15)
            android.graphics.Rect r0 = r1.mWindowDragBounds
            int r0 = r0.left
            if (r0 >= r7) goto L_0x00dc
            android.graphics.Rect r0 = r1.mWindowDragBounds
            int r14 = r0.top
            android.graphics.Rect r15 = r1.mWindowDragBounds
            int r15 = r15.width()
            int r15 = r15 + r7
            android.graphics.Rect r9 = r1.mWindowDragBounds
            int r9 = r9.bottom
            r0.set(r7, r14, r15, r9)
        L_0x00dc:
            android.graphics.Rect r0 = r1.mWindowDragBounds
            int r0 = r0.top
            if (r0 >= r10) goto L_0x00f4
            android.graphics.Rect r0 = r1.mWindowDragBounds
            int r9 = r0.left
            android.graphics.Rect r14 = r1.mWindowDragBounds
            int r14 = r14.right
            android.graphics.Rect r15 = r1.mWindowDragBounds
            int r15 = r15.height()
            int r15 = r15 + r10
            r0.set(r9, r10, r14, r15)
        L_0x00f4:
            android.graphics.Rect r0 = r1.mWindowDragBounds
            int r0 = r0.left
            if (r0 <= r8) goto L_0x010c
            android.graphics.Rect r0 = r1.mWindowDragBounds
            int r9 = r0.top
            android.graphics.Rect r14 = r1.mWindowDragBounds
            int r14 = r14.width()
            int r14 = r14 + r8
            android.graphics.Rect r15 = r1.mWindowDragBounds
            int r15 = r15.bottom
            r0.set(r8, r9, r14, r15)
        L_0x010c:
            android.graphics.Rect r0 = r1.mWindowDragBounds
            int r0 = r0.top
            if (r0 <= r11) goto L_0x0124
            android.graphics.Rect r0 = r1.mWindowDragBounds
            int r9 = r0.left
            android.graphics.Rect r14 = r1.mWindowDragBounds
            int r14 = r14.right
            android.graphics.Rect r15 = r1.mWindowDragBounds
            int r15 = r15.height()
            int r15 = r15 + r11
            r0.set(r9, r11, r14, r15)
        L_0x0124:
            android.graphics.Rect r0 = r1.mLastWindowDragBounds
            boolean r0 = r0.isEmpty()
            if (r0 == 0) goto L_0x0133
            android.graphics.Rect r0 = r1.mLastWindowDragBounds
            android.graphics.Rect r9 = r1.mWindowDragBounds
            r0.set(r9)
        L_0x0133:
            r0 = 0
            com.android.server.wm.MiuiFreeFormWindowMotionHelper r9 = r1.mFreeFormWindowMotionHelper
            boolean r9 = r9.mEnteredHotArea
            if (r9 == 0) goto L_0x01b6
            com.android.server.wm.MiuiFreeFormGesturePointerEventListener r9 = r1.mListener
            com.android.server.wm.WindowManagerService r9 = r9.mService
            android.content.Context r9 = r9.mContext
            r14 = -1
            com.android.server.wm.MiuiFreeFormGesturePointerEventListener r15 = r1.mListener
            boolean r15 = r15.mIsLandcapeFreeform
            android.graphics.Rect r9 = android.util.MiuiMultiWindowUtils.findNearestCorner(r9, r2, r3, r14, r15)
            com.android.server.wm.MiuiFreeFormGesturePointerEventListener r0 = r1.mListener
            java.util.Set<com.android.server.wm.AppWindowToken> r0 = r0.mFreeFormAppTokens
            java.util.Iterator r0 = r0.iterator()
        L_0x0151:
            boolean r14 = r0.hasNext()
            if (r14 == 0) goto L_0x0161
            java.lang.Object r14 = r0.next()
            com.android.server.wm.AppWindowToken r14 = (com.android.server.wm.AppWindowToken) r14
            r15 = 1
            r14.mIgnoreInput = r15
            goto L_0x0151
        L_0x0161:
            com.android.server.wm.MiuiFreeFormGesturePointerEventListener r0 = r1.mListener
            r0.registerInputConsumer()
            com.android.server.wm.MiuiFreeFormGesturePointerEventListener r0 = r1.mListener
            com.android.server.wm.WindowManagerService r0 = r0.mService
            com.android.server.wm.WindowManagerGlobalLock r14 = r0.mGlobalLock
            monitor-enter(r14)
            com.android.server.wm.WindowManagerService.boostPriorityForLockedSection()     // Catch:{ all -> 0x01ac }
            com.android.server.wm.MiuiFreeFormGesturePointerEventListener r0 = r1.mListener     // Catch:{ all -> 0x01ac }
            r0.setSmallFreeFormWindowBounds(r9)     // Catch:{ all -> 0x01ac }
            com.android.server.wm.MiuiFreeFormGesturePointerEventListener r0 = r1.mListener     // Catch:{ all -> 0x01ac }
            com.android.server.wm.MiuiFreeFormGestureController r0 = r0.mGestureController     // Catch:{ all -> 0x01ac }
            com.android.server.wm.DisplayContent r0 = r0.mDisplayContent     // Catch:{ all -> 0x01ac }
            com.android.server.wm.InputMonitor r15 = r0.getInputMonitor()     // Catch:{ all -> 0x01ac }
            r15.forceUpdateImmediately()     // Catch:{ all -> 0x01ac }
            monitor-exit(r14)     // Catch:{ all -> 0x01ac }
            com.android.server.wm.WindowManagerService.resetPriorityAfterLockedSection()
            int r0 = r9.left
            float r0 = (float) r0
            com.android.server.wm.MiuiFreeFormWindowMotionHelper r14 = r1.mFreeFormWindowMotionHelper
            float r14 = r14.mWindowWidth
            r15 = 1065353216(0x3f800000, float:1.0)
            r16 = r7
            com.android.server.wm.MiuiFreeFormWindowMotionHelper r7 = r1.mFreeFormWindowMotionHelper
            float r7 = r7.mSmallWindowTargetWScale
            float r15 = r15 - r7
            float r14 = r14 * r15
            r7 = 1073741824(0x40000000, float:2.0)
            float r14 = r14 / r7
            float r0 = r0 - r14
            int r0 = (int) r0
            android.graphics.Rect r7 = r1.mWindowDragBounds
            int r14 = r9.top
            r7.offsetTo(r0, r14)
            com.android.server.wm.MiuiFreeFormGesturePointerEventListener r7 = r1.mListener
            r14 = 1
            com.android.server.wm.MiuiFreeFormGesturePointerEventListener.mCurrentWindowMode = r14
            r7.hideCaptionView()
            goto L_0x01b9
        L_0x01ac:
            r0 = move-exception
            r16 = r7
        L_0x01af:
            monitor-exit(r14)     // Catch:{ all -> 0x01b4 }
            com.android.server.wm.WindowManagerService.resetPriorityAfterLockedSection()
            throw r0
        L_0x01b4:
            r0 = move-exception
            goto L_0x01af
        L_0x01b6:
            r16 = r7
            r9 = r0
        L_0x01b9:
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r7 = "updateWindowUpBounds startDragX"
            r0.append(r7)
            r0.append(r4)
            java.lang.String r7 = " startDragY:"
            r0.append(r7)
            r0.append(r5)
            java.lang.String r7 = " x:"
            r0.append(r7)
            r0.append(r2)
            java.lang.String r7 = " y:"
            r0.append(r7)
            r0.append(r3)
            java.lang.String r7 = "offsetX:"
            r0.append(r7)
            r0.append(r12)
            java.lang.String r7 = " offsetY:"
            r0.append(r7)
            r0.append(r13)
            java.lang.String r7 = " WindowDragBounds:"
            r0.append(r7)
            android.graphics.Rect r7 = r1.mWindowDragBounds
            r0.append(r7)
            java.lang.String r7 = " windowOriginalBounds:"
            r0.append(r7)
            r0.append(r6)
            java.lang.String r7 = " LastWindowDragBounds:"
            r0.append(r7)
            android.graphics.Rect r7 = r1.mLastWindowDragBounds
            r0.append(r7)
            java.lang.String r7 = " cornerPoint:"
            r0.append(r7)
            r0.append(r9)
            java.lang.String r7 = " enteredHotArea:"
            r0.append(r7)
            com.android.server.wm.MiuiFreeFormWindowMotionHelper r7 = r1.mFreeFormWindowMotionHelper
            boolean r7 = r7.mEnteredHotArea
            r0.append(r7)
            java.lang.String r0 = r0.toString()
            java.lang.String r7 = "MiuiFreeFormTaskPositioner"
            android.util.Slog.d(r7, r0)
            android.graphics.Rect r0 = r1.mLastWindowDragBounds
            android.graphics.Rect r7 = r1.mWindowDragBounds
            r14 = 8
            r15 = 1
            r1.translateScaleAnimal(r0, r7, r14, r15)
            android.graphics.Rect r0 = r1.mLastWindowDragBounds
            r0.setEmpty()
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.wm.MiuiFreeFormTaskPositioner.updateWindowUpBounds(float, float, float, float, boolean, android.graphics.Rect):void");
    }

    public void updateSmallWindowMoveBounds(float x, float y, float startDragX, float startDragY, boolean isVertical, Rect windowOriginalBounds) {
        int offsetX = Math.round(x - startDragX);
        int offsetY = Math.round(y - startDragY);
        if (this.mLastWindowDragBounds.isEmpty()) {
            this.mLastWindowDragBounds.set(windowOriginalBounds);
        } else {
            this.mLastWindowDragBounds.set(this.mWindowDragBounds);
        }
        this.mWindowDragBounds.set(windowOriginalBounds);
        this.mWindowDragBounds.offsetTo(windowOriginalBounds.left + offsetX, windowOriginalBounds.top + offsetY);
        if (MiuiFreeFormGestureController.DEBUG) {
            Slog.d(TAG, "updateSmallWindowMoveBounds startDragX" + startDragX + " startDragY:" + startDragY + " x:" + x + " y:" + y + "offsetX:" + offsetX + " offsetY:" + offsetY + " windowDragBounds:" + this.mWindowDragBounds + " windowOriginalBounds:" + windowOriginalBounds);
        }
        if (!windowOriginalBounds.equals(this.mWindowDragBounds)) {
            translateAnimal(this.mLastWindowDragBounds, this.mWindowDragBounds, 7, false);
        }
    }

    public void updateSmallWindowUpBounds(float x, float y, float startDragX, float startDragY, boolean isVertical, Rect windowOriginalBounds, Rect windowTargetBounds) {
        Slog.d(TAG, "updateSmallWindowUpBounds startDragX" + startDragX + " startDragY:" + startDragY + " x:" + x + " y:" + y + " windowOriginalBounds:" + windowOriginalBounds + " windowTargetBounds:" + windowTargetBounds);
        this.mWindowDragBounds.set(windowTargetBounds);
        translateAnimal(windowOriginalBounds, this.mWindowDragBounds, 9, true);
        this.mLastWindowDragBounds.setEmpty();
    }

    /* JADX WARNING: Code restructure failed: missing block: B:33:0x0261, code lost:
        r4 = r2;
        r0 = r17;
        r2 = r19;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void translateScaleAnimal(android.graphics.Rect r19, android.graphics.Rect r20, int r21, boolean r22) {
        /*
            r18 = this;
            r1 = r18
            r2 = r19
            r3 = r20
            r4 = r21
            r5 = r22
            com.android.server.wm.MiuiFreeFormGesturePointerEventListener r0 = r1.mListener
            java.util.ArrayList<com.android.server.wm.WindowState> r0 = r0.mFreeFormAppWindows
            java.util.Iterator r0 = r0.iterator()
        L_0x0012:
            boolean r6 = r0.hasNext()
            if (r6 == 0) goto L_0x0272
            java.lang.Object r6 = r0.next()
            com.android.server.wm.WindowState r6 = (com.android.server.wm.WindowState) r6
            android.view.WindowManager$LayoutParams r7 = r6.mAttrs
            int r8 = r7.privateFlags
            r8 = r8 | 64
            r7.privateFlags = r8
            monitor-enter(r6)
            com.android.server.wm.MiuiFreeFormWindowMotionHelper r7 = r1.mFreeFormWindowMotionHelper     // Catch:{ all -> 0x026c }
            java.util.concurrent.ConcurrentHashMap<com.android.server.wm.WindowState, com.android.server.wm.MiuiFreeFormGestureAnimator$AnimalLock> r7 = r7.mWindowLocks     // Catch:{ all -> 0x026c }
            java.lang.Object r7 = r7.get(r6)     // Catch:{ all -> 0x026c }
            com.android.server.wm.MiuiFreeFormGestureAnimator$AnimalLock r7 = (com.android.server.wm.MiuiFreeFormGestureAnimator.AnimalLock) r7     // Catch:{ all -> 0x026c }
            r15 = r7
            r14 = 1065353216(0x3f800000, float:1.0)
            if (r15 != 0) goto L_0x017b
            java.lang.String r7 = "MiuiFreeFormTaskPositioner"
            java.lang.StringBuilder r8 = new java.lang.StringBuilder     // Catch:{ all -> 0x0268 }
            r8.<init>()     // Catch:{ all -> 0x0268 }
            java.lang.String r9 = "translateScaleAnimal create animation isUp "
            r8.append(r9)     // Catch:{ all -> 0x0268 }
            r8.append(r5)     // Catch:{ all -> 0x0268 }
            java.lang.String r8 = r8.toString()     // Catch:{ all -> 0x0268 }
            android.util.Slog.d(r7, r8)     // Catch:{ all -> 0x0268 }
            com.android.server.wm.MiuiFreeFormGesturePointerEventListener r7 = r1.mListener     // Catch:{ all -> 0x0268 }
            com.android.server.wm.MiuiFreeFormGestureAnimator r7 = r7.mGestureAnimator     // Catch:{ all -> 0x0268 }
            int r8 = r2.left     // Catch:{ all -> 0x0268 }
            float r9 = (float) r8     // Catch:{ all -> 0x0268 }
            int r8 = r3.left     // Catch:{ all -> 0x0268 }
            float r10 = (float) r8     // Catch:{ all -> 0x0268 }
            r11 = 1148633088(0x4476c000, float:987.0)
            r12 = 1065185444(0x3f7d70a4, float:0.99)
            r13 = 0
            r16 = 2
            r8 = r6
            r17 = r0
            r0 = r14
            r14 = r16
            com.android.server.wm.MiuiFreeFormSpringAnimation r7 = r7.createSpringAnimation(r8, r9, r10, r11, r12, r13, r14)     // Catch:{ all -> 0x0268 }
            r14 = r7
            com.android.server.wm.MiuiFreeFormGesturePointerEventListener r7 = r1.mListener     // Catch:{ all -> 0x0268 }
            com.android.server.wm.MiuiFreeFormGestureAnimator r7 = r7.mGestureAnimator     // Catch:{ all -> 0x0268 }
            int r8 = r2.top     // Catch:{ all -> 0x0268 }
            float r9 = (float) r8     // Catch:{ all -> 0x0268 }
            int r8 = r3.top     // Catch:{ all -> 0x0268 }
            float r10 = (float) r8     // Catch:{ all -> 0x0268 }
            r11 = 1148633088(0x4476c000, float:987.0)
            r12 = 1065185444(0x3f7d70a4, float:0.99)
            r13 = 0
            r16 = 3
            r8 = r6
            r0 = r14
            r14 = r16
            com.android.server.wm.MiuiFreeFormSpringAnimation r7 = r7.createSpringAnimation(r8, r9, r10, r11, r12, r13, r14)     // Catch:{ all -> 0x0268 }
            r14 = r7
            com.android.server.wm.MiuiFreeFormGesturePointerEventListener r7 = r1.mListener     // Catch:{ all -> 0x0268 }
            com.android.server.wm.MiuiFreeFormGestureAnimator r7 = r7.mGestureAnimator     // Catch:{ all -> 0x0268 }
            r9 = 1065353216(0x3f800000, float:1.0)
            r10 = 1065353216(0x3f800000, float:1.0)
            r11 = 1142811853(0x441deccd, float:631.7)
            r12 = 1060320051(0x3f333333, float:0.7)
            r13 = 0
            r16 = 5
            r8 = r6
            r2 = r14
            r14 = r16
            com.android.server.wm.MiuiFreeFormSpringAnimation r7 = r7.createSpringAnimation(r8, r9, r10, r11, r12, r13, r14)     // Catch:{ all -> 0x0268 }
            r14 = r7
            com.android.server.wm.MiuiFreeFormGesturePointerEventListener r7 = r1.mListener     // Catch:{ all -> 0x0268 }
            com.android.server.wm.MiuiFreeFormGestureAnimator r7 = r7.mGestureAnimator     // Catch:{ all -> 0x0268 }
            r9 = 1065353216(0x3f800000, float:1.0)
            r10 = 1065353216(0x3f800000, float:1.0)
            r11 = 1142811853(0x441deccd, float:631.7)
            r12 = 1060320051(0x3f333333, float:0.7)
            r13 = 0
            r16 = 6
            r8 = r6
            r4 = r14
            r14 = r16
            com.android.server.wm.MiuiFreeFormSpringAnimation r7 = r7.createSpringAnimation(r8, r9, r10, r11, r12, r13, r14)     // Catch:{ all -> 0x0268 }
            com.android.server.wm.MiuiFreeFormGestureAnimator$AnimalLock r8 = new com.android.server.wm.MiuiFreeFormGestureAnimator$AnimalLock     // Catch:{ all -> 0x0268 }
            r8.<init>(r6)     // Catch:{ all -> 0x0268 }
            r15 = r8
            com.android.server.wm.MiuiFreeFormWindowMotionHelper r8 = r1.mFreeFormWindowMotionHelper     // Catch:{ all -> 0x0268 }
            java.util.concurrent.ConcurrentHashMap<com.android.server.wm.WindowState, com.android.server.wm.MiuiFreeFormGestureAnimator$AnimalLock> r8 = r8.mWindowLocks     // Catch:{ all -> 0x0268 }
            r8.put(r6, r15)     // Catch:{ all -> 0x0268 }
            com.android.server.wm.MiuiFreeFormWindowMotionHelper r8 = r1.mFreeFormWindowMotionHelper     // Catch:{ all -> 0x0268 }
            java.util.concurrent.ConcurrentHashMap<com.android.server.wm.MiuiFreeFormDynamicAnimation, com.android.server.wm.MiuiFreeFormGestureAnimator$AnimalLock> r8 = r8.mAnimalLocks     // Catch:{ all -> 0x0268 }
            r8.put(r0, r15)     // Catch:{ all -> 0x0268 }
            com.android.server.wm.MiuiFreeFormWindowMotionHelper r8 = r1.mFreeFormWindowMotionHelper     // Catch:{ all -> 0x0268 }
            java.util.concurrent.ConcurrentHashMap<com.android.server.wm.MiuiFreeFormDynamicAnimation, com.android.server.wm.MiuiFreeFormGestureAnimator$AnimalLock> r8 = r8.mAnimalLocks     // Catch:{ all -> 0x0268 }
            r8.put(r2, r15)     // Catch:{ all -> 0x0268 }
            com.android.server.wm.MiuiFreeFormWindowMotionHelper r8 = r1.mFreeFormWindowMotionHelper     // Catch:{ all -> 0x0268 }
            java.util.concurrent.ConcurrentHashMap<com.android.server.wm.MiuiFreeFormDynamicAnimation, com.android.server.wm.MiuiFreeFormGestureAnimator$AnimalLock> r8 = r8.mAnimalLocks     // Catch:{ all -> 0x0268 }
            r8.put(r4, r15)     // Catch:{ all -> 0x0268 }
            com.android.server.wm.MiuiFreeFormWindowMotionHelper r8 = r1.mFreeFormWindowMotionHelper     // Catch:{ all -> 0x0268 }
            java.util.concurrent.ConcurrentHashMap<com.android.server.wm.MiuiFreeFormDynamicAnimation, com.android.server.wm.MiuiFreeFormGestureAnimator$AnimalLock> r8 = r8.mAnimalLocks     // Catch:{ all -> 0x0268 }
            r8.put(r7, r15)     // Catch:{ all -> 0x0268 }
            r15.mTranslateXAnimation = r0     // Catch:{ all -> 0x0268 }
            r15.mTranslateYAnimation = r2     // Catch:{ all -> 0x0268 }
            r15.mScaleXAnimation = r4     // Catch:{ all -> 0x0268 }
            r15.mScaleYAnimation = r7     // Catch:{ all -> 0x0268 }
            com.android.server.wm.MiuiFreeFormWindowMotionHelper r8 = r1.mFreeFormWindowMotionHelper     // Catch:{ all -> 0x0268 }
            boolean r8 = r8.mEnteredHotArea     // Catch:{ all -> 0x0268 }
            if (r8 == 0) goto L_0x0119
            com.android.server.wm.MiuiFreeFormWindowMotionHelper r8 = r1.mFreeFormWindowMotionHelper     // Catch:{ all -> 0x0268 }
            float r8 = r8.mSmallWindowTargetHScale     // Catch:{ all -> 0x0268 }
            com.android.server.wm.MiuiFreeFormWindowMotionHelper r9 = r1.mFreeFormWindowMotionHelper     // Catch:{ all -> 0x0268 }
            float r9 = r9.mSmallWindowTargetWScale     // Catch:{ all -> 0x0268 }
            com.android.server.wm.MiuiFreeFormSpringAnimation r10 = r15.mScaleXAnimation     // Catch:{ all -> 0x0268 }
            com.android.server.wm.MiuiFreeFormSpringAnimation r11 = r15.mScaleXAnimation     // Catch:{ all -> 0x0268 }
            float r11 = r11.getCurrentValue()     // Catch:{ all -> 0x0268 }
            r10.setStartValue(r11)     // Catch:{ all -> 0x0268 }
            com.android.server.wm.MiuiFreeFormSpringAnimation r10 = r15.mScaleXAnimation     // Catch:{ all -> 0x0268 }
            r10.animateToFinalPosition(r9)     // Catch:{ all -> 0x0268 }
            com.android.server.wm.MiuiFreeFormSpringAnimation r10 = r15.mScaleYAnimation     // Catch:{ all -> 0x0268 }
            com.android.server.wm.MiuiFreeFormSpringAnimation r11 = r15.mScaleYAnimation     // Catch:{ all -> 0x0268 }
            float r11 = r11.getCurrentValue()     // Catch:{ all -> 0x0268 }
            r10.setStartValue(r11)     // Catch:{ all -> 0x0268 }
            com.android.server.wm.MiuiFreeFormSpringAnimation r10 = r15.mScaleYAnimation     // Catch:{ all -> 0x0268 }
            r10.animateToFinalPosition(r8)     // Catch:{ all -> 0x0268 }
            goto L_0x0141
        L_0x0119:
            r8 = 1065353216(0x3f800000, float:1.0)
            r9 = 1065353216(0x3f800000, float:1.0)
            com.android.server.wm.MiuiFreeFormSpringAnimation r10 = r15.mScaleXAnimation     // Catch:{ all -> 0x0268 }
            com.android.server.wm.MiuiFreeFormSpringAnimation r11 = r15.mScaleXAnimation     // Catch:{ all -> 0x0268 }
            float r11 = r11.getCurrentValue()     // Catch:{ all -> 0x0268 }
            r10.setStartValue(r11)     // Catch:{ all -> 0x0268 }
            com.android.server.wm.MiuiFreeFormSpringAnimation r10 = r15.mScaleXAnimation     // Catch:{ all -> 0x0268 }
            r11 = 1065353216(0x3f800000, float:1.0)
            r10.animateToFinalPosition(r11)     // Catch:{ all -> 0x0268 }
            com.android.server.wm.MiuiFreeFormSpringAnimation r10 = r15.mScaleYAnimation     // Catch:{ all -> 0x0268 }
            com.android.server.wm.MiuiFreeFormSpringAnimation r11 = r15.mScaleYAnimation     // Catch:{ all -> 0x0268 }
            float r11 = r11.getCurrentValue()     // Catch:{ all -> 0x0268 }
            r10.setStartValue(r11)     // Catch:{ all -> 0x0268 }
            com.android.server.wm.MiuiFreeFormSpringAnimation r10 = r15.mScaleYAnimation     // Catch:{ all -> 0x0268 }
            r11 = 1065353216(0x3f800000, float:1.0)
            r10.animateToFinalPosition(r11)     // Catch:{ all -> 0x0268 }
        L_0x0141:
            if (r5 == 0) goto L_0x0177
            com.android.server.wm.MiuiFreeFormGesturePointerEventListener r8 = r1.mListener     // Catch:{ all -> 0x0268 }
            com.android.server.wm.MiuiFreeFormGestureAnimator r8 = r8.mGestureAnimator     // Catch:{ all -> 0x0268 }
            java.lang.String r9 = "TRANSLATE_X_END_LISTENER"
            com.android.server.wm.MiuiFreeFormDynamicAnimation$OnAnimationEndListener r8 = r8.createAnimationEndListener(r9)     // Catch:{ all -> 0x0268 }
            r0.addEndListener(r8)     // Catch:{ all -> 0x0268 }
            com.android.server.wm.MiuiFreeFormGesturePointerEventListener r8 = r1.mListener     // Catch:{ all -> 0x0268 }
            com.android.server.wm.MiuiFreeFormGestureAnimator r8 = r8.mGestureAnimator     // Catch:{ all -> 0x0268 }
            java.lang.String r9 = "TRANSLATE_Y_END_LISTENER"
            com.android.server.wm.MiuiFreeFormDynamicAnimation$OnAnimationEndListener r8 = r8.createAnimationEndListener(r9)     // Catch:{ all -> 0x0268 }
            r2.addEndListener(r8)     // Catch:{ all -> 0x0268 }
            com.android.server.wm.MiuiFreeFormGesturePointerEventListener r8 = r1.mListener     // Catch:{ all -> 0x0268 }
            com.android.server.wm.MiuiFreeFormGestureAnimator r8 = r8.mGestureAnimator     // Catch:{ all -> 0x0268 }
            java.lang.String r9 = "SCALE_X_END_LISTENER"
            com.android.server.wm.MiuiFreeFormDynamicAnimation$OnAnimationEndListener r8 = r8.createAnimationEndListener(r9)     // Catch:{ all -> 0x0268 }
            r4.addEndListener(r8)     // Catch:{ all -> 0x0268 }
            com.android.server.wm.MiuiFreeFormGesturePointerEventListener r8 = r1.mListener     // Catch:{ all -> 0x0268 }
            com.android.server.wm.MiuiFreeFormGestureAnimator r8 = r8.mGestureAnimator     // Catch:{ all -> 0x0268 }
            java.lang.String r9 = "SCALE_Y_END_LISTENER"
            com.android.server.wm.MiuiFreeFormDynamicAnimation$OnAnimationEndListener r8 = r8.createAnimationEndListener(r9)     // Catch:{ all -> 0x0268 }
            r7.addEndListener(r8)     // Catch:{ all -> 0x0268 }
        L_0x0177:
            r2 = r21
            goto L_0x025d
        L_0x017b:
            r17 = r0
            boolean r0 = com.android.server.wm.MiuiFreeFormGestureController.DEBUG     // Catch:{ all -> 0x0268 }
            if (r0 == 0) goto L_0x0197
            java.lang.String r0 = "MiuiFreeFormTaskPositioner"
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ all -> 0x0268 }
            r2.<init>()     // Catch:{ all -> 0x0268 }
            java.lang.String r4 = "translateScaleAnimal update isUp:"
            r2.append(r4)     // Catch:{ all -> 0x0268 }
            r2.append(r5)     // Catch:{ all -> 0x0268 }
            java.lang.String r2 = r2.toString()     // Catch:{ all -> 0x0268 }
            android.util.Slog.d(r0, r2)     // Catch:{ all -> 0x0268 }
        L_0x0197:
            com.android.server.wm.MiuiFreeFormSpringAnimation r0 = r15.mTranslateXAnimation     // Catch:{ all -> 0x0268 }
            com.android.server.wm.MiuiFreeFormSpringAnimation r2 = r15.mTranslateXAnimation     // Catch:{ all -> 0x0268 }
            float r2 = r2.getCurrentValue()     // Catch:{ all -> 0x0268 }
            r0.setStartValue(r2)     // Catch:{ all -> 0x0268 }
            com.android.server.wm.MiuiFreeFormSpringAnimation r0 = r15.mTranslateXAnimation     // Catch:{ all -> 0x0268 }
            int r2 = r3.left     // Catch:{ all -> 0x0268 }
            float r2 = (float) r2     // Catch:{ all -> 0x0268 }
            r0.animateToFinalPosition(r2)     // Catch:{ all -> 0x0268 }
            com.android.server.wm.MiuiFreeFormSpringAnimation r0 = r15.mTranslateYAnimation     // Catch:{ all -> 0x0268 }
            com.android.server.wm.MiuiFreeFormSpringAnimation r2 = r15.mTranslateYAnimation     // Catch:{ all -> 0x0268 }
            float r2 = r2.getCurrentValue()     // Catch:{ all -> 0x0268 }
            r0.setStartValue(r2)     // Catch:{ all -> 0x0268 }
            com.android.server.wm.MiuiFreeFormSpringAnimation r0 = r15.mTranslateYAnimation     // Catch:{ all -> 0x0268 }
            int r2 = r3.top     // Catch:{ all -> 0x0268 }
            float r2 = (float) r2     // Catch:{ all -> 0x0268 }
            r0.animateToFinalPosition(r2)     // Catch:{ all -> 0x0268 }
            com.android.server.wm.MiuiFreeFormWindowMotionHelper r0 = r1.mFreeFormWindowMotionHelper     // Catch:{ all -> 0x0268 }
            boolean r0 = r0.mEnteredHotArea     // Catch:{ all -> 0x0268 }
            if (r0 == 0) goto L_0x01ec
            com.android.server.wm.MiuiFreeFormWindowMotionHelper r0 = r1.mFreeFormWindowMotionHelper     // Catch:{ all -> 0x0268 }
            float r0 = r0.mSmallWindowTargetHScale     // Catch:{ all -> 0x0268 }
            com.android.server.wm.MiuiFreeFormWindowMotionHelper r2 = r1.mFreeFormWindowMotionHelper     // Catch:{ all -> 0x0268 }
            float r2 = r2.mSmallWindowTargetWScale     // Catch:{ all -> 0x0268 }
            com.android.server.wm.MiuiFreeFormSpringAnimation r4 = r15.mScaleXAnimation     // Catch:{ all -> 0x0268 }
            com.android.server.wm.MiuiFreeFormSpringAnimation r7 = r15.mScaleXAnimation     // Catch:{ all -> 0x0268 }
            float r7 = r7.getCurrentValue()     // Catch:{ all -> 0x0268 }
            r4.setStartValue(r7)     // Catch:{ all -> 0x0268 }
            com.android.server.wm.MiuiFreeFormSpringAnimation r4 = r15.mScaleXAnimation     // Catch:{ all -> 0x0268 }
            r4.animateToFinalPosition(r2)     // Catch:{ all -> 0x0268 }
            com.android.server.wm.MiuiFreeFormSpringAnimation r4 = r15.mScaleYAnimation     // Catch:{ all -> 0x0268 }
            com.android.server.wm.MiuiFreeFormSpringAnimation r7 = r15.mScaleYAnimation     // Catch:{ all -> 0x0268 }
            float r7 = r7.getCurrentValue()     // Catch:{ all -> 0x0268 }
            r4.setStartValue(r7)     // Catch:{ all -> 0x0268 }
            com.android.server.wm.MiuiFreeFormSpringAnimation r4 = r15.mScaleYAnimation     // Catch:{ all -> 0x0268 }
            r4.animateToFinalPosition(r0)     // Catch:{ all -> 0x0268 }
            goto L_0x0214
        L_0x01ec:
            r0 = 1065353216(0x3f800000, float:1.0)
            r2 = 1065353216(0x3f800000, float:1.0)
            com.android.server.wm.MiuiFreeFormSpringAnimation r4 = r15.mScaleXAnimation     // Catch:{ all -> 0x0268 }
            com.android.server.wm.MiuiFreeFormSpringAnimation r7 = r15.mScaleXAnimation     // Catch:{ all -> 0x0268 }
            float r7 = r7.getCurrentValue()     // Catch:{ all -> 0x0268 }
            r4.setStartValue(r7)     // Catch:{ all -> 0x0268 }
            com.android.server.wm.MiuiFreeFormSpringAnimation r4 = r15.mScaleXAnimation     // Catch:{ all -> 0x0268 }
            r7 = 1065353216(0x3f800000, float:1.0)
            r4.animateToFinalPosition(r7)     // Catch:{ all -> 0x0268 }
            com.android.server.wm.MiuiFreeFormSpringAnimation r4 = r15.mScaleYAnimation     // Catch:{ all -> 0x0268 }
            com.android.server.wm.MiuiFreeFormSpringAnimation r7 = r15.mScaleYAnimation     // Catch:{ all -> 0x0268 }
            float r7 = r7.getCurrentValue()     // Catch:{ all -> 0x0268 }
            r4.setStartValue(r7)     // Catch:{ all -> 0x0268 }
            com.android.server.wm.MiuiFreeFormSpringAnimation r4 = r15.mScaleYAnimation     // Catch:{ all -> 0x0268 }
            r7 = 1065353216(0x3f800000, float:1.0)
            r4.animateToFinalPosition(r7)     // Catch:{ all -> 0x0268 }
        L_0x0214:
            if (r5 == 0) goto L_0x025b
            com.android.server.wm.MiuiFreeFormGesturePointerEventListener r0 = r1.mListener     // Catch:{ all -> 0x0268 }
            com.android.server.wm.MiuiFreeFormWindowMotionHelper r0 = r0.mFreeFormWindowMotionHelper     // Catch:{ all -> 0x0268 }
            r2 = r21
            r0.mCurrentAnimation = r2     // Catch:{ all -> 0x0270 }
            com.android.server.wm.MiuiFreeFormSpringAnimation r0 = r15.mTranslateXAnimation     // Catch:{ all -> 0x0270 }
            com.android.server.wm.MiuiFreeFormGesturePointerEventListener r4 = r1.mListener     // Catch:{ all -> 0x0270 }
            com.android.server.wm.MiuiFreeFormGestureAnimator r4 = r4.mGestureAnimator     // Catch:{ all -> 0x0270 }
            java.lang.String r7 = "TRANSLATE_X_END_LISTENER"
            com.android.server.wm.MiuiFreeFormDynamicAnimation$OnAnimationEndListener r4 = r4.createAnimationEndListener(r7)     // Catch:{ all -> 0x0270 }
            r0.addEndListener(r4)     // Catch:{ all -> 0x0270 }
            com.android.server.wm.MiuiFreeFormSpringAnimation r0 = r15.mTranslateYAnimation     // Catch:{ all -> 0x0270 }
            com.android.server.wm.MiuiFreeFormGesturePointerEventListener r4 = r1.mListener     // Catch:{ all -> 0x0270 }
            com.android.server.wm.MiuiFreeFormGestureAnimator r4 = r4.mGestureAnimator     // Catch:{ all -> 0x0270 }
            java.lang.String r7 = "TRANSLATE_Y_END_LISTENER"
            com.android.server.wm.MiuiFreeFormDynamicAnimation$OnAnimationEndListener r4 = r4.createAnimationEndListener(r7)     // Catch:{ all -> 0x0270 }
            r0.addEndListener(r4)     // Catch:{ all -> 0x0270 }
            com.android.server.wm.MiuiFreeFormSpringAnimation r0 = r15.mScaleXAnimation     // Catch:{ all -> 0x0270 }
            com.android.server.wm.MiuiFreeFormGesturePointerEventListener r4 = r1.mListener     // Catch:{ all -> 0x0270 }
            com.android.server.wm.MiuiFreeFormGestureAnimator r4 = r4.mGestureAnimator     // Catch:{ all -> 0x0270 }
            java.lang.String r7 = "SCALE_X_END_LISTENER"
            com.android.server.wm.MiuiFreeFormDynamicAnimation$OnAnimationEndListener r4 = r4.createAnimationEndListener(r7)     // Catch:{ all -> 0x0270 }
            r0.addEndListener(r4)     // Catch:{ all -> 0x0270 }
            com.android.server.wm.MiuiFreeFormSpringAnimation r0 = r15.mScaleYAnimation     // Catch:{ all -> 0x0270 }
            com.android.server.wm.MiuiFreeFormGesturePointerEventListener r4 = r1.mListener     // Catch:{ all -> 0x0270 }
            com.android.server.wm.MiuiFreeFormGestureAnimator r4 = r4.mGestureAnimator     // Catch:{ all -> 0x0270 }
            java.lang.String r7 = "SCALE_Y_END_LISTENER"
            com.android.server.wm.MiuiFreeFormDynamicAnimation$OnAnimationEndListener r4 = r4.createAnimationEndListener(r7)     // Catch:{ all -> 0x0270 }
            r0.addEndListener(r4)     // Catch:{ all -> 0x0270 }
            goto L_0x025d
        L_0x025b:
            r2 = r21
        L_0x025d:
            r15.start(r2)     // Catch:{ all -> 0x0270 }
            monitor-exit(r6)     // Catch:{ all -> 0x0270 }
            r4 = r2
            r0 = r17
            r2 = r19
            goto L_0x0012
        L_0x0268:
            r0 = move-exception
            r2 = r21
            goto L_0x026e
        L_0x026c:
            r0 = move-exception
            r2 = r4
        L_0x026e:
            monitor-exit(r6)     // Catch:{ all -> 0x0270 }
            throw r0
        L_0x0270:
            r0 = move-exception
            goto L_0x026e
        L_0x0272:
            r2 = r4
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.wm.MiuiFreeFormTaskPositioner.translateScaleAnimal(android.graphics.Rect, android.graphics.Rect, int, boolean):void");
    }

    public void translateAnimal(Rect startWindowDragBounds, Rect finalWindowDragBounds, int animationType, boolean isUp) {
        Rect rect = startWindowDragBounds;
        Rect rect2 = finalWindowDragBounds;
        Iterator<WindowState> it = this.mListener.mFreeFormAppWindows.iterator();
        while (it.hasNext()) {
            WindowState w = it.next();
            w.mAttrs.privateFlags |= 64;
            synchronized (w) {
                try {
                    MiuiFreeFormGestureAnimator.AnimalLock animalLock = this.mFreeFormWindowMotionHelper.mWindowLocks.get(w);
                    if (animalLock == null) {
                        Slog.d(TAG, "translateAnimal create Animation");
                        MiuiFreeFormSpringAnimation tXSpringAnimation = this.mListener.mGestureAnimator.createSpringAnimation(w, (float) rect.left, (float) rect2.left, 3947.8f, 0.99f, 0.0f, 2);
                        MiuiFreeFormSpringAnimation tYSpringAnimation = this.mListener.mGestureAnimator.createSpringAnimation(w, (float) rect.top, (float) rect2.top, 3947.8f, 0.99f, 0.0f, 3);
                        if (isUp) {
                            tXSpringAnimation.addEndListener(this.mListener.mGestureAnimator.createAnimationEndListener(MiuiFreeFormGestureAnimator.TRANSLATE_X_END_LISTENER));
                            tYSpringAnimation.addEndListener(this.mListener.mGestureAnimator.createAnimationEndListener(MiuiFreeFormGestureAnimator.TRANSLATE_Y_END_LISTENER));
                            tXSpringAnimation.getSpring().setStiffness(130.5f);
                            tXSpringAnimation.getSpring().setDampingRatio(0.75f);
                            tYSpringAnimation.getSpring().setStiffness(130.5f);
                            tYSpringAnimation.getSpring().setDampingRatio(0.75f);
                            this.mFreeFormWindowMotionHelper.mIsAnimating = true;
                        }
                        animalLock = new MiuiFreeFormGestureAnimator.AnimalLock(w);
                        this.mFreeFormWindowMotionHelper.mWindowLocks.put(w, animalLock);
                        this.mFreeFormWindowMotionHelper.mAnimalLocks.put(tXSpringAnimation, animalLock);
                        this.mFreeFormWindowMotionHelper.mAnimalLocks.put(tYSpringAnimation, animalLock);
                        animalLock.mTranslateXAnimation = tXSpringAnimation;
                        animalLock.mTranslateYAnimation = tYSpringAnimation;
                    } else {
                        if (MiuiFreeFormGestureController.DEBUG) {
                            Slog.d(TAG, "translateAnimal update");
                        }
                        animalLock.mTranslateXAnimation.getSpring().setStiffness(3947.8f);
                        animalLock.mTranslateXAnimation.getSpring().setDampingRatio(0.99f);
                        animalLock.mTranslateYAnimation.getSpring().setStiffness(3947.8f);
                        animalLock.mTranslateYAnimation.getSpring().setDampingRatio(0.99f);
                        animalLock.mTranslateXAnimation.setStartValue(animalLock.mTranslateXAnimation.getCurrentValue());
                        animalLock.mTranslateXAnimation.animateToFinalPosition((float) rect2.left);
                        animalLock.mTranslateYAnimation.setStartValue(animalLock.mTranslateYAnimation.getCurrentValue());
                        animalLock.mTranslateYAnimation.animateToFinalPosition((float) rect2.top);
                        if (isUp) {
                            animalLock.mTranslateXAnimation.getSpring().setStiffness(130.5f);
                            animalLock.mTranslateXAnimation.getSpring().setDampingRatio(0.75f);
                            animalLock.mTranslateYAnimation.getSpring().setStiffness(130.5f);
                            animalLock.mTranslateYAnimation.getSpring().setDampingRatio(0.75f);
                            animalLock.mTranslateXAnimation.addEndListener(this.mListener.mGestureAnimator.createAnimationEndListener(MiuiFreeFormGestureAnimator.TRANSLATE_X_END_LISTENER));
                            animalLock.mTranslateYAnimation.addEndListener(this.mListener.mGestureAnimator.createAnimationEndListener(MiuiFreeFormGestureAnimator.TRANSLATE_Y_END_LISTENER));
                            this.mFreeFormWindowMotionHelper.mIsAnimating = true;
                        }
                    }
                    animalLock.start(animationType);
                } catch (Throwable th) {
                    th = th;
                    throw th;
                }
            }
        }
        int i = animationType;
    }
}
