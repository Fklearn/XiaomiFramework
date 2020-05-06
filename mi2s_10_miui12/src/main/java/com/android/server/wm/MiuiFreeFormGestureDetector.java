package com.android.server.wm;

import android.content.Context;
import android.os.Build;
import android.os.UserHandle;
import android.view.ViewConfiguration;
import com.android.server.pm.PackageManagerService;
import java.util.HashMap;
import miui.securityspace.CrossUserUtils;

public class MiuiFreeFormGestureDetector {
    private static boolean DEBUG = MiuiFreeFormGestureController.DEBUG;
    public static final String TAG = "MiuiFreeFormGestureDetector";
    public static boolean mIsMtbfOrMonkeyRunning = false;
    private static HashMap<String, Integer> sRadiusBottomMap = new HashMap<>();
    private static HashMap<String, Integer> sRadiusTopMap = new HashMap<>();
    private MiuiFreeFormGesturePointerEventListener mListener;

    public MiuiFreeFormGestureDetector(MiuiFreeFormGesturePointerEventListener listener) {
        this.mListener = listener;
    }

    public boolean passedSlop(float x, float y, float startDragX, float startDragY) {
        int dragSlop = ViewConfiguration.get(this.mListener.mService.mContext).getScaledTouchSlop();
        return Math.abs(x - startDragX) > ((float) dragSlop) || Math.abs(y - startDragY) > ((float) dragSlop);
    }

    public static boolean isCurrentUser(WindowState win) {
        return win != null && UserHandle.getUserId(win.getOwningUid()) == CrossUserUtils.getCurrentUserId();
    }

    public static int getScreenRoundCornerRadiusTop(Context context) {
        Integer radius = sRadiusTopMap.get(Build.DEVICE);
        if (radius != null) {
            return radius.intValue();
        }
        int resourceId = context.getResources().getIdentifier("rounded_corner_radius_top", "dimen", PackageManagerService.PLATFORM_PACKAGE_NAME);
        if (resourceId > 0) {
            return context.getResources().getDimensionPixelSize(resourceId);
        }
        return -1;
    }

    public static int getScreenRoundCornerRadiusBottom(Context context) {
        Integer radius = sRadiusBottomMap.get(Build.DEVICE);
        if (radius != null) {
            return radius.intValue();
        }
        int resourceId = context.getResources().getIdentifier("rounded_corner_radius_bottom", "dimen", PackageManagerService.PLATFORM_PACKAGE_NAME);
        if (resourceId > 0) {
            return context.getResources().getDimensionPixelSize(resourceId);
        }
        return -1;
    }
}
