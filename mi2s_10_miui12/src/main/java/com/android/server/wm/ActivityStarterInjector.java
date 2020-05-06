package com.android.server.wm;

import android.app.ActivityOptions;
import android.graphics.Rect;
import android.server.am.SplitScreenReporter;
import android.util.MiuiMultiWindowUtils;
import android.util.Slog;
import com.miui.server.greeze.GreezeManagerService;
import java.lang.reflect.Method;
import java.util.ArrayList;
import miui.os.SystemProperties;

class ActivityStarterInjector {
    private static final String TAG = "ActivityStarter";

    ActivityStarterInjector() {
    }

    public static void checkFreeformSupport(ActivityTaskManagerService service, ActivityOptions options) {
        if (!service.mSupportsFreeformWindowManagement && options != null && options.getLaunchWindowingMode() == 5) {
            service.mSupportsFreeformWindowManagement = SystemProperties.getBoolean("persist.sys.miui_optimization", !SplitScreenReporter.ACTION_ENTER_SPLIT.equals(SystemProperties.get("ro.miui.cts")));
        }
    }

    public static ActivityOptions modifyLaunchActivityOptionIfNeed(ActivityTaskManagerService service, RootActivityContainer rootActivityContainer, ActivityRecord startingActivity, ActivityOptions options) {
        ActivityStack focusedStack = rootActivityContainer.getTopDisplayFocusedStack();
        ActivityRecord topActivity = focusedStack.getTopActivity();
        if (focusedStack.getWindowingMode() == 5 && topActivity != null && topActivity.supportsFreeform() && topActivity.packageName.equals(startingActivity.launchedFromPackage)) {
            if (options == null) {
                options = ActivityOptions.makeBasic();
            }
            options.setLaunchWindowingMode(5);
            options.setLaunchBounds(MiuiMultiWindowUtils.getFreeformRect(service.mContext, false));
        }
        return options;
    }

    public static ActivityOptions modifyLaunchActivityOptionIfNeed(ActivityTaskManagerService service, RootActivityContainer rootActivityContainer, String callingPackgae, ActivityOptions options, WindowProcessController callerApp) {
        ActivityStack focusedStack = rootActivityContainer.getTopDisplayFocusedStack();
        ActivityRecord topActivity = focusedStack.getTopActivity();
        boolean needUpdateOption = false;
        Rect rect = new Rect();
        if (focusedStack.getWindowingMode() == 5 && topActivity != null && topActivity.supportsFreeform() && topActivity.packageName.equals(callingPackgae)) {
            rect = topActivity.getBounds();
            needUpdateOption = true;
        } else if (callerApp != null) {
            ArrayList<ActivityRecord> callerActivities = callerApp.getAllActivities();
            if (!callerActivities.isEmpty() && callerActivities.get(0).getWindowingMode() == 5) {
                rect = callerActivities.get(0).getBounds();
                needUpdateOption = true;
            }
        }
        if (needUpdateOption) {
            if (options == null) {
                options = ActivityOptions.makeBasic();
            }
            options.setLaunchWindowingMode(5);
            Slog.d(TAG, "ActivityStarterInjector::modifyLaunchActivityOptionIfNeed::rect = " + rect);
            options.setLaunchBounds(rect);
            try {
                Method method = MiuiMultiWindowUtils.isMethodExist(options, "getActivityOptionsInjector", (Object[]) null);
                if (method != null) {
                    MiuiMultiWindowUtils.invoke(method.invoke(options, new Object[0]), "setFreeformScale", new Object[]{Float.valueOf(MiuiMultiWindowUtils.sScale)});
                }
            } catch (Exception e) {
            }
        }
        return options;
    }

    public static ActivityOptions modifyLaunchActivityOptionIfNeed(ActivityTaskManagerService service, ActivityRecord sourceRecord, ActivityOptions options) {
        if (!(sourceRecord == null || sourceRecord.getActivityStack() == null || sourceRecord.getActivityStack().getWindowingMode() != 5)) {
            if (options == null) {
                options = ActivityOptions.makeBasic();
            }
            options.setLaunchWindowingMode(5);
            options.setLaunchBounds(MiuiMultiWindowUtils.getFreeformRect(service.mContext, false));
        }
        return options;
    }

    public static void initFreefomLaunchParameters(ActivityOptions options, RootActivityContainer rootActivityContainer) {
        if (options != null) {
            Method method = MiuiMultiWindowUtils.isMethodExist(options, "getActivityOptionsInjector", (Object[]) null);
            if (options.getLaunchWindowingMode() == 5 && method != null) {
                try {
                    MiuiMultiWindowUtils.mIsMiniFreeformMode = ((Boolean) MiuiMultiWindowUtils.invoke(method.invoke(options, new Object[0]), "getMiniFreeformMode", (Object[]) null)).booleanValue();
                    MiuiMultiWindowUtils.sScale = ((Float) MiuiMultiWindowUtils.invoke(method.invoke(options, new Object[0]), "getFreeformScale", (Object[]) null)).floatValue();
                    MiuiMultiWindowUtils.needAnimation = ((Boolean) MiuiMultiWindowUtils.invoke(method.invoke(options, new Object[0]), "getFreeformAnimation", (Object[]) null)).booleanValue();
                } catch (Exception e) {
                }
                Slog.d(TAG, "MiuiMultiWindowUtils.needAnimation = " + MiuiMultiWindowUtils.needAnimation + " MiuiMultiWindowUtils.sScale = " + MiuiMultiWindowUtils.sScale + " MiuiMultiWindowUtils.mIsMiniFreeformMode = " + MiuiMultiWindowUtils.mIsMiniFreeformMode);
            }
            if (options.getLaunchWindowingMode() == 5 && MiuiMultiWindowUtils.mIsMiniFreeformMode) {
                ActivityRecord homeActivity = rootActivityContainer.getDefaultDisplayHomeActivity();
                Slog.d(TAG, "homeActivity: " + homeActivity + "homeActivity.setDummyVisible(false, false)");
                if (homeActivity != null) {
                    homeActivity.setDummyVisible(false, false);
                }
            }
        }
    }

    public static boolean getLastFrame(String name) {
        if (name.contains("com.tencent.mobileqq/com.tencent.av.ui.VideoInviteActivity") || name.contains("com.tencent.mm/.plugin.voip.ui.VideoActivity") || name.contains("com.tencent.mobileqq/com.tencent.av.ui.AVActivity") || name.contains("com.tencent.mobileqq/com.tencent.av.ui.AVLoadingDialogActivity") || name.contains("com.android.incallui/.InCallActivity") || name.contains("com.google.android.dialer/com.android.incallui.InCallActivity") || name.contains("voipcalling.VoipActivityV2")) {
            return true;
        }
        return false;
    }

    public static void startActivityUncheckedBefore(ActivityRecord r, boolean isFromHome) {
        try {
            GreezeManagerService.getService().gzLaunchBoost(r.appInfo.uid, r.shortComponentName, r.launchedFromUid, r.launchedFromPackage);
        } catch (Exception e) {
            Slog.w(TAG, "Failed to GzBoost Activity", e);
        }
    }
}
