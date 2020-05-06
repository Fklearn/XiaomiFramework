package com.android.server.wm;

import android.util.Slog;
import android.view.WindowManager;
import com.android.internal.util.ToBooleanFunction;

public class WindowAnimatorInjector {
    private static final String TAG = "WindowAnimatorInjector";
    /* access modifiers changed from: private */
    public static WindowState sTmpFirstAppWindow;
    /* access modifiers changed from: private */
    public static WindowState sTmpLockWindow;

    private enum LockDeviceWindowPolicy {
        HIDE,
        SHOW
    }

    static void updateLockDeviceWindowLocked(WindowManagerService wms, DisplayContent dc) {
        boolean change;
        LockDeviceWindowPolicy lockDeviceWindowPolicy;
        if (wms != null && dc != null) {
            try {
                dc.forAllWindows(new ToBooleanFunction<WindowState>() {
                    public boolean apply(WindowState win) {
                        if (win == null || win.mAttrs == null) {
                            return false;
                        }
                        int type = win.mAttrs.type;
                        if ((win.mAttrs.extraFlags & 2048) != 0) {
                            WindowState unused = WindowAnimatorInjector.sTmpLockWindow = win;
                        } else if (type >= 1 && type < 2000 && win.getParentWindow() == null) {
                            if (WindowAnimatorInjector.sTmpFirstAppWindow == null || (WindowAnimatorInjector.sTmpFirstAppWindow.mAppToken != null && WindowAnimatorInjector.sTmpFirstAppWindow.mAppToken.equals(win.mAppToken))) {
                                WindowState unused2 = WindowAnimatorInjector.sTmpFirstAppWindow = win;
                            } else if (WindowAnimatorInjector.sTmpLockWindow != null) {
                                return true;
                            }
                        }
                        return false;
                    }
                }, true);
                if (sTmpLockWindow != null) {
                    boolean hideLockWindow = false;
                    if (sTmpFirstAppWindow != null && (sTmpFirstAppWindow.mAttrs.extraFlags & 4096) != 0 && sTmpFirstAppWindow.isVisibleLw() && sTmpFirstAppWindow.hasDrawnLw() && isObscuringFullScreen(sTmpFirstAppWindow, sTmpFirstAppWindow.mAttrs)) {
                        hideLockWindow = true;
                    }
                    if (hideLockWindow) {
                        change = sTmpLockWindow.hideLw(false, false);
                    } else {
                        change = sTmpLockWindow.showLw(false, false);
                    }
                    if (change) {
                        wms.mFocusMayChange = true;
                        WindowState windowState = sTmpLockWindow;
                        if (hideLockWindow) {
                            lockDeviceWindowPolicy = LockDeviceWindowPolicy.HIDE;
                        } else {
                            lockDeviceWindowPolicy = LockDeviceWindowPolicy.SHOW;
                        }
                        if (!windowState.requestTraversalOnceLw(lockDeviceWindowPolicy)) {
                            Slog.e(TAG, "someone else changed lock window policy, there must be a bug!");
                        }
                    }
                    sTmpLockWindow = null;
                    sTmpFirstAppWindow = null;
                }
            } finally {
                sTmpLockWindow = null;
                sTmpFirstAppWindow = null;
            }
        }
    }

    private static boolean isObscuringFullScreen(WindowState win, WindowManager.LayoutParams params) {
        if (win != null && params != null && win.isObscuringDisplay() && params.x == 0 && params.y == 0 && params.width == -1 && params.height == -1) {
            return true;
        }
        return false;
    }
}
