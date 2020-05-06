package com.android.server.wm;

import android.graphics.Point;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.os.UserHandle;
import android.util.Slog;
import com.android.server.UiThread;
import java.util.HashMap;
import java.util.function.Consumer;
import miui.os.Build;
import miui.securityspace.CrossUserUtils;

public class MiuiGestureDetector {
    private static int ASSISTANT_WIDTH = 0;
    private static final int CHECK_GAP = 20;
    private static HashMap<String, Float> DEVICE_BOTTOM_EDGE_HEIGHTS = new HashMap<>();
    private static final float DEVICE_BOTTOM_EDGE_HEIGHT_STANDARD = 4.5f;
    private static final int DISTANCE_CANCEL_TO_RECENTS = 140;
    private static final int DISTANCE_GO_TO_RECENTS = 160;
    private static final int DISTANCE_LIMIT = 20;
    public static final int GESTURE_READY_THRESHOLD = 20;
    private static final int HOT_SPACE_DEFAULT_HEIGHT = 13;
    private static final int MSG_CHECK_TAIL_DISTANCE = 1;
    private static final String TAG = "MiuiGesture";
    public static final int WINDOW_SEARCH_RESULT_NORMAL = 1;
    public static final int WINDOW_SEARCH_RESULT_NOT_FOUND = 0;
    public static final int WINDOW_SEARCH_RESULT_SPECIAL = 2;
    public static final int WINDOW_SEARCH_RESULT_UNKNOWN = 3;
    static int sCurrentX;
    static int sCurrentY;
    private static int sDownX;
    private static int sDownY;
    private static boolean sFirstTouch;
    private static MiuiGestureController sGestureController;
    /* access modifiers changed from: private */
    public static MiuiGesturePointerEventListener sGestureListener;
    private static WindowState sGestureStubWindow;
    private static H sHandler;
    private static int sHotSpaceHeightCache = -1;
    private static volatile boolean sIsPortrait;
    private static long sLastTouchTime;
    static volatile int sScreenHeight;
    static volatile int sScreenWidth;
    private static double sTailDistance;
    private static int sTailX;
    private static int sTailY;
    private static WindowManagerService sWindowManagerService;

    static {
        DEVICE_BOTTOM_EDGE_HEIGHTS.put("perseus", Float.valueOf(DEVICE_BOTTOM_EDGE_HEIGHT_STANDARD));
        DEVICE_BOTTOM_EDGE_HEIGHTS.put("cepheus", Float.valueOf(3.6f));
        DEVICE_BOTTOM_EDGE_HEIGHTS.put("dipper", Float.valueOf(6.4f));
    }

    private MiuiGestureDetector() {
        throw new RuntimeException("Utility class not for instance");
    }

    static void init(WindowManagerService service, MiuiGesturePointerEventListener listener, MiuiGestureController gestureController) {
        sWindowManagerService = service;
        sGestureListener = listener;
        sGestureController = gestureController;
        sHandler = new H(UiThread.getHandler().getLooper());
        ASSISTANT_WIDTH = (int) ((service.mContext.getResources().getDisplayMetrics().density * 48.0f) + 0.5f);
    }

    static void updateScreen(int width, int height, boolean isPortrait) {
        sScreenWidth = width;
        sScreenHeight = height;
        sIsPortrait = isPortrait;
        getGestureHotSpaceHeight(true);
    }

    static void onActionDown(int x, int y) {
        sCurrentX = x;
        sCurrentY = y;
        sDownX = x;
        sDownY = y;
        sTailX = x;
        sTailY = y;
        if (MiuiGestureController.DEBUG_DETECT || MiuiGestureController.DEBUG_INPUT) {
            Slog.d("MiuiGesture", "onActionDown, x=" + x + ",y=" + y);
        }
    }

    static void onActionMove(int x, int y) {
        sCurrentX = x;
        sCurrentY = y;
        if (MiuiGestureController.DEBUG_STEP) {
            Slog.d("MiuiGesture", "onActionMove, x=" + x + ",y=" + y);
        }
    }

    static void onActionUp(int x, int y) {
        sCurrentX = x;
        sCurrentY = y;
        sHandler.removeMessages(1);
        if (MiuiGestureController.DEBUG_DETECT || MiuiGestureController.DEBUG_INPUT) {
            Slog.d("MiuiGesture", "onActionUp, x=" + x + ",y=" + y);
        }
    }

    static boolean isPortrait() {
        return sIsPortrait;
    }

    static boolean canGestureBegin() {
        return isInHotSpace() && !isForbidGestureLocked() && checkMistakeTouchLocked();
    }

    static boolean canGestureReady() {
        int offset = sDownY - sCurrentY;
        if (offset < 20) {
            return false;
        }
        double angle = Math.atan((((double) offset) * 1.0d) / ((double) Math.abs(sCurrentX - sDownX)));
        if (angle >= 0.5d) {
            return true;
        }
        Slog.d("MiuiGesture", "gesture cancel with angle = " + angle);
        return false;
    }

    static boolean shouldGoRecents() {
        return sCurrentY < sScreenHeight + -160;
    }

    static boolean shouldCancelGoRecents() {
        return sCurrentY > sScreenHeight + -140;
    }

    static void setGestureStubWindow(WindowState win) {
        sGestureStubWindow = win;
    }

    static boolean isOutSideHotSpace(int y) {
        return y > sScreenHeight || y < sScreenHeight - getGestureHotSpaceHeight(false);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:2:0x0008, code lost:
        r0 = sCurrentX;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static boolean shouldTriggerAssistant() {
        /*
            com.android.server.wm.MiuiGestureController r0 = sGestureController
            boolean r0 = r0.isAssistAvailable()
            if (r0 == 0) goto L_0x0017
            int r0 = sCurrentX
            int r1 = ASSISTANT_WIDTH
            if (r0 < r1) goto L_0x0015
            int r1 = sScreenWidth
            int r2 = ASSISTANT_WIDTH
            int r1 = r1 - r2
            if (r0 <= r1) goto L_0x0017
        L_0x0015:
            r0 = 1
            goto L_0x0018
        L_0x0017:
            r0 = 0
        L_0x0018:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.wm.MiuiGestureDetector.shouldTriggerAssistant():boolean");
    }

    private static boolean isInHotSpace() {
        if (sCurrentY < sScreenHeight - getGestureHotSpaceHeight(false) || sCurrentY > sScreenHeight) {
            return false;
        }
        return true;
    }

    private static boolean checkMistakeTouchLocked() {
        if (!sGestureController.isMistakeTouch() || sIsPortrait || !isFullScreenModeLocked()) {
            return true;
        }
        if (isActiveRegion()) {
            long now = SystemClock.uptimeMillis();
            if (now - sLastTouchTime > 2000) {
                sLastTouchTime = now;
                sFirstTouch = true;
                return false;
            }
            sLastTouchTime = now;
            if (!sFirstTouch) {
                sFirstTouch = true;
                return false;
            }
            sFirstTouch = false;
            return true;
        }
        Slog.w("MiuiGesture", "not in ActiveRegion");
        return false;
    }

    private static boolean isForbidGestureLocked() {
        if (isGestureStubWindowInvisible()) {
            Slog.w("MiuiGesture", "ForbidGesture: now GestureStubWindow isn't visible");
            return true;
        } else if (shouldTriggerAssistant()) {
            Slog.w("MiuiGesture", "ForbidGesture: now should trigger assistant");
            return true;
        } else if (sGestureListener.isGestureRunning()) {
            Slog.w("MiuiGesture", "ForbidGesture: last gesture not finish yet");
            return true;
        } else if (sWindowManagerService.mPolicy.isKeyguardLocked()) {
            Slog.w("MiuiGesture", "ForbidGesture: now KeyguardLocked");
            return true;
        } else if (!isHomeWindowEnterAnimatingLocked()) {
            return false;
        } else {
            Slog.w("MiuiGesture", "ForbidGesture: now home window doing enter animating");
            return true;
        }
    }

    private static boolean isHomeWindowEnterAnimatingLocked() {
        return sWindowManagerService.getDefaultDisplayContentLocked().forAllWindows($$Lambda$MiuiGestureDetector$x53zlRRapHCItnfw3vn17tu8mfc.INSTANCE, true);
    }

    static /* synthetic */ boolean lambda$isHomeWindowEnterAnimatingLocked$0(WindowState w) {
        return (w.mAppToken != null && w.getActivityType() == 2 && w.mAppToken.mEnteringAnimation && w.mAppToken.hasCommittedReparentToAnimationLeash()) || sGestureListener.mLoadBackHomeAnimation;
    }

    private static boolean isGestureStubWindowInvisible() {
        WindowState windowState = sGestureStubWindow;
        if (windowState == null) {
            return false;
        }
        if (!isCurrentUser(windowState)) {
            findGestureStubWindowLocked();
            return isGestureStubWindowInvisible();
        } else if (sGestureStubWindow.mViewVisibility != 0) {
            return true;
        } else {
            return false;
        }
    }

    private static void findGestureStubWindowLocked() {
        sGestureStubWindow = null;
        sWindowManagerService.getDefaultDisplayContentLocked().forAllWindows((Consumer<WindowState>) $$Lambda$MiuiGestureDetector$7UMp7VdAz6e08RrgDZuO9Fd8CE.INSTANCE, true);
    }

    static /* synthetic */ void lambda$findGestureStubWindowLocked$1(WindowState win) {
        if (isCurrentUser(win)) {
            sGestureController.tryToSetGestureStubWindow(win);
        }
    }

    public static boolean isCurrentUser(WindowState win) {
        return win != null && UserHandle.getUserId(win.getOwningUid()) == CrossUserUtils.getCurrentUserId();
    }

    private static boolean isActiveRegion() {
        if (sIsPortrait) {
            return false;
        }
        float offsetX = (float) (sCurrentX - ((sScreenWidth - sScreenHeight) / 2));
        if (offsetX < 0.01f || offsetX > ((float) sScreenHeight)) {
            return false;
        }
        return true;
    }

    private static boolean isFullScreenModeLocked() {
        return sWindowManagerService.getDefaultDisplayContentLocked().forAllWindows($$Lambda$MiuiGestureDetector$OIH61KVndrm8CU_nDdqfeNqspdM.INSTANCE, true);
    }

    static /* synthetic */ boolean lambda$isFullScreenModeLocked$2(WindowState w) {
        return w.mAttrs.type == 2000 && w.mWinAnimator != null && !w.mWinAnimator.getShown();
    }

    static int getGestureHotSpaceHeight(boolean force) {
        Float bottomEdgeHeight;
        int i = sHotSpaceHeightCache;
        if (i != -1 && !force) {
            return i;
        }
        int dpi = sWindowManagerService.getDefaultDisplayContentLocked().mBaseDisplayDensity;
        float height = (((float) dpi) / 160.0f) * 13.0f;
        if (sIsPortrait && (bottomEdgeHeight = DEVICE_BOTTOM_EDGE_HEIGHTS.get(Build.DEVICE)) != null && bottomEdgeHeight.floatValue() < DEVICE_BOTTOM_EDGE_HEIGHT_STANDARD) {
            height = (float) (((double) height) + (((((double) (DEVICE_BOTTOM_EDGE_HEIGHT_STANDARD - bottomEdgeHeight.floatValue())) / 10.0d) / 2.45d) * ((double) dpi)));
        }
        if (MiuiGestureController.DEBUG_DETECT) {
            Slog.d("MiuiGesture", "hot space height:" + height);
        }
        sHotSpaceHeightCache = (int) height;
        return sHotSpaceHeightCache;
    }

    static int isAppSurfaceShownLocked() {
        Task topTask = findTopTask();
        if (topTask == null) {
            return 0;
        }
        if (MiuiGestureController.DEBUG_DETECT) {
            Slog.d("MiuiGesture", "found task:" + topTask);
        }
        if (topTask.isActivityTypeHome() || topTask.isActivityTypeRecents()) {
            return 2;
        }
        return 1;
    }

    static Task findTopTask() {
        WindowList<TaskStack> stacks = sWindowManagerService.getDefaultDisplayContentLocked().getStacks();
        for (int i = stacks.size() - 1; i >= 0; i--) {
            TaskStack stack = (TaskStack) stacks.get(i);
            if (!stack.inPinnedWindowingMode() && !stack.inFreeformWindowingMode() && !stack.inSplitScreenPrimaryWindowingMode()) {
                return stack.getTopVisibleTask();
            }
        }
        return null;
    }

    private static class H extends Handler {
        public H(Looper looper) {
            super(looper);
        }

        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                MiuiGestureDetector.updateTail();
                if (MiuiGestureDetector.isOverSpeed()) {
                    sendEmptyMessageDelayed(1, 20);
                } else if (MiuiGestureDetector.shouldGoRecents()) {
                    MiuiGestureDetector.sGestureListener.launchRecentsBehind();
                }
            }
        }
    }

    static void resetTail() {
        resendTailMessage();
        sTailDistance = 30.0d;
    }

    static void resendTailMessage() {
        sHandler.removeMessages(1);
        sHandler.sendEmptyMessage(1);
    }

    /* access modifiers changed from: private */
    public static void updateTail() {
        int i = sTailX;
        int i2 = sCurrentX;
        sTailX = i + ((i2 - i) / 4);
        int i3 = sTailY;
        sTailY = i3 + ((sCurrentY - i3) / 4);
        sTailDistance = Math.sqrt(Math.pow((double) (i2 - sTailX), 2.0d) + Math.pow((double) (sCurrentY - sTailY), 2.0d));
    }

    static boolean isOverSpeed() {
        return sTailDistance > 20.0d;
    }

    static boolean isRightDirection() {
        return sCurrentY < sTailY;
    }

    static Point calculateInertia() {
        updateTail();
        return new Point(sCurrentX - sTailX, sCurrentY - sTailY);
    }
}
