package com.android.server.wm;

import android.graphics.Rect;
import android.graphics.Region;
import android.os.SystemProperties;
import android.util.MiuiMultiWindowAdapter;
import android.util.MiuiMultiWindowUtils;
import android.util.Slog;
import android.view.WindowManager;
import java.lang.reflect.Field;

public class WindowStateInjector {
    static boolean DISABLE_SAVE_SURFACE = SystemProperties.getBoolean("persist.sys.disableSaveSurface", false);
    static final String TAG = "WindowManager";

    static boolean shouldSaveSurface(WindowState win) {
        return DISABLE_SAVE_SURFACE ? false : false;
    }

    public static void adjuestScaleAndFrame(WindowState win, Task task, Rect parentFrame, Rect displayFrame, Rect overscanFrame, Rect contentFrame, Rect visibleFrame, Rect decorFrame, Rect stableFrame, Rect outsetFrame) {
        win.mGlobalScale = MiuiMultiWindowUtils.sScale;
        win.mInvGlobalScale = 1.0f / win.mGlobalScale;
        if (task != null) {
            Rect freeformRect = task.getBounds();
            parentFrame.set(freeformRect);
            displayFrame.set(freeformRect);
            overscanFrame.set(freeformRect);
            contentFrame.set(freeformRect);
            visibleFrame.set(freeformRect);
            decorFrame.set(freeformRect);
            stableFrame.set(freeformRect);
            outsetFrame.set(freeformRect);
        }
    }

    public static void adjuestFrameAndInsets(WindowState win) {
        Task task = win.getTask();
        boolean isChildWindow = false;
        if (task != null) {
            Rect rect = task.getBounds();
            if (rect.left < win.mWindowFrames.mFrame.left || win.mAttrs.type >= 1000) {
                int relativeLeft = win.mWindowFrames.mFrame.left - rect.left;
                int weightOffset = relativeLeft - ((int) (((float) relativeLeft) * win.mGlobalScale));
                win.mWindowFrames.mFrame.left -= weightOffset;
                win.mWindowFrames.mFrame.right -= weightOffset;
                isChildWindow = true;
            }
            if (rect.top < win.mWindowFrames.mFrame.top || win.mAttrs.type >= 1000) {
                int relativeTop = win.mWindowFrames.mFrame.top - rect.top;
                int heightOffset = relativeTop - ((int) (((float) relativeTop) * win.mGlobalScale));
                win.mWindowFrames.mFrame.top -= heightOffset;
                win.mWindowFrames.mFrame.bottom -= heightOffset;
                isChildWindow = true;
            }
        }
        MiuiMultiWindowAdapter.updateInsets(win.mWindowFrames.mContentInsets, win.mWindowFrames.mVisibleInsets, win.mWindowFrames.mStableInsets, win.mAppToken.mActivityComponent, isChildWindow, win.mWindowFrames.mFrame.width() < win.mWindowFrames.mFrame.height());
    }

    public static void adjuestFreeFormTouchRegion(WindowState win, Region outRegion) {
        Task task = win.getTask();
        if (task != null) {
            Rect taskBounds = task.getBounds();
            outRegion.set(taskBounds.left, taskBounds.top - MiuiMultiWindowUtils.HOT_SPACE_TOP_OFFSITE, taskBounds.left + ((int) ((((float) taskBounds.width()) * win.mGlobalScale) / MiuiMultiWindowUtils.sScale)), taskBounds.top + ((int) ((((float) taskBounds.height()) * win.mGlobalScale) / MiuiMultiWindowUtils.sScale)) + MiuiMultiWindowUtils.HOT_SPACE_BOTTOM_OFFSITE);
        }
    }

    public static boolean adjustFlagsForOnePixelWindow(WindowState win, int requestedWidth, int requestedHeight, WindowManager.LayoutParams attrs) {
        return false;
    }

    private static boolean setWindowStateMiuiFlag(Object object, String memberName, boolean value) {
        try {
            Field field = object.getClass().getDeclaredField(memberName);
            field.setAccessible(true);
            field.set(object, Boolean.valueOf(value));
            return true;
        } catch (Exception e) {
            Slog.d("WindowManager", "setWindowStateMiuiFlag failed" + e.toString());
            return false;
        }
    }

    private static boolean getWindowStateMiuiFlag(Object object, String memberName) {
        try {
            Field field = object.getClass().getDeclaredField(memberName);
            field.setAccessible(true);
            return ((Boolean) field.get(object)).booleanValue();
        } catch (Exception e) {
            Slog.d("WindowManager", "getWindowStateMiuiFlag failed" + e.toString());
            return false;
        }
    }
}
