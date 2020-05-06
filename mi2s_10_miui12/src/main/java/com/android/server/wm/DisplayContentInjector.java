package com.android.server.wm;

import android.os.RemoteException;
import android.os.SystemProperties;
import java.util.Iterator;
import miui.hardware.display.DisplayFeatureManager;

public class DisplayContentInjector {
    private static final String DESCRIPTOR = "miui.systemui.keyguard.Wallpaper";
    private static final int FPS_COMMON = 60;
    private static final int SCREEN_DPI_MODE = 24;
    public static int sCurrentRefreshRate = -1;
    public static int sLastUserRefreshRate = -1;

    static int getFullScreenIndex(TaskStack stack, WindowList<TaskStack> children, int targetPosition) {
        if (stack.getWindowingMode() != 1) {
            return targetPosition;
        }
        Iterator it = children.iterator();
        while (it.hasNext()) {
            TaskStack tStack = (TaskStack) it.next();
            if (tStack.getWindowingMode() == 5 && children.indexOf(tStack) > 0) {
                return children.indexOf(tStack) - 1;
            }
        }
        return targetPosition;
    }

    static int getFullScreenIndex(boolean toTop, TaskStack stack, WindowList<TaskStack> children, int targetPosition, boolean adding) {
        if (!toTop || stack.getWindowingMode() != 1) {
            return targetPosition;
        }
        Iterator it = children.iterator();
        while (it.hasNext()) {
            TaskStack tStack = (TaskStack) it.next();
            if (tStack.getWindowingMode() == 5) {
                int topChildPosition = children.indexOf(tStack);
                return adding ? topChildPosition : topChildPosition > 0 ? topChildPosition - 1 : 0;
            }
        }
        return targetPosition;
    }

    static void updateRefreshRateIfNeed(boolean isInMultiWindow) {
        int currentFps = getCurrentRefreshRate();
        if (sCurrentRefreshRate != currentFps) {
            sCurrentRefreshRate = currentFps;
        }
        if (isInMultiWindow) {
            int i = sCurrentRefreshRate;
            if (i > 60) {
                sLastUserRefreshRate = i;
                setCurrentRefreshRate(60);
                return;
            }
            return;
        }
        int i2 = sLastUserRefreshRate;
        if (i2 >= 60) {
            setCurrentRefreshRate(i2);
            sLastUserRefreshRate = -1;
        }
    }

    private static int getCurrentRefreshRate() {
        int fps = SystemProperties.getInt("persist.vendor.dfps.level", 60);
        int powerFps = SystemProperties.getInt("persist.vendor.power.dfps.level", 0);
        if (powerFps != 0) {
            return powerFps;
        }
        return fps;
    }

    private static void setCurrentRefreshRate(int fps) {
        sCurrentRefreshRate = fps;
        DisplayFeatureManager.getInstance().setScreenEffect(24, fps);
    }

    static int compare(WindowManagerService mWmService, WindowToken token1, WindowToken token2) {
        try {
            if (token1.windowType == token2.windowType && token1.windowType == 2013) {
                if (token1.token != null) {
                    if (DESCRIPTOR.equals(token1.token.getInterfaceDescriptor())) {
                        return 1;
                    }
                }
                if (token2.token != null && DESCRIPTOR.equals(token2.token.getInterfaceDescriptor())) {
                    return -1;
                }
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        if (mWmService.mPolicy.getWindowLayerFromTypeLw(token1.windowType, token1.mOwnerCanManageAppTokens) < mWmService.mPolicy.getWindowLayerFromTypeLw(token2.windowType, token2.mOwnerCanManageAppTokens)) {
            return -1;
        }
        return 1;
    }
}
