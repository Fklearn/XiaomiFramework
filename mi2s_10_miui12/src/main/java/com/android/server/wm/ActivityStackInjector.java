package com.android.server.wm;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Trace;
import android.os.UserHandle;
import com.android.server.pm.PackageManagerServiceInjector;
import miui.R;
import miui.os.Environment;
import miui.securityspace.CrossUserUtils;
import miui.util.AttributeResolver;

class ActivityStackInjector {
    private static final String TAG = "ActivityStackInjector";

    private ActivityStackInjector() {
    }

    static boolean isStartingWindowSupported(ActivityRecord record, Context context) {
        Trace.traceBegin(64, "isStartingWindowSupported");
        boolean ret = false;
        boolean z = false;
        if (context != null) {
            try {
                context = context.createPackageContextAsUser(record.packageName, 0, new UserHandle(record.mUserId));
                if (context != null) {
                    context.setTheme(record.getRealTheme());
                    ret = Environment.isUsingMiui(context);
                }
            } catch (Exception e) {
            }
        }
        if (ret) {
            if (!AttributeResolver.resolveBoolean(context, R.attr.windowDisablePreview, false)) {
                z = true;
            }
            ret = z;
        }
        Trace.traceEnd(64);
        return ret;
    }

    static int getStartingWindowLabelRes(ActivityRecord record, Context context) {
        Trace.traceBegin(64, "getStartingWindowLabelRes");
        int res = 0;
        if (isStartingWindowSupported(record, context)) {
            try {
                res = new Intent(record.intent).getIntExtra(":android:show_fragment_title", 0);
            } catch (Exception e) {
            }
        }
        Trace.traceEnd(64);
        return res;
    }

    static CharSequence getStartingWindowLabel(ActivityRecord record, Context context) {
        Trace.traceBegin(64, "getStartingWindowLabel");
        CharSequence label = null;
        if (isStartingWindowSupported(record, context)) {
            try {
                Intent intent = new Intent(record.intent);
                Bundle bundle = intent.getBundleExtra(":android:show_fragment_args");
                if (bundle != null) {
                    label = bundle.getCharSequence(":miui:starting_window_label");
                }
                if (label == null) {
                    label = intent.getCharSequenceExtra(":miui:starting_window_label");
                }
            } catch (Exception e) {
                label = "";
            }
        }
        Trace.traceEnd(64);
        return label;
    }

    /* JADX WARNING: Removed duplicated region for block: B:17:0x002d  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    static boolean moveTaskIfNeed(com.android.server.wm.TaskRecord r7, java.util.ArrayList<com.android.server.wm.TaskRecord> r8) {
        /*
            com.android.server.wm.ActivityRecord r0 = r7.getTopActivity()
            boolean r1 = com.android.server.wm.MiuiMultiTaskManager.isMultiTaskSupport(r0)
            r2 = 0
            if (r1 != 0) goto L_0x000c
            return r2
        L_0x000c:
            r1 = 0
            if (r0 == 0) goto L_0x001e
            android.content.Intent r3 = r0.intent     // Catch:{ Exception -> 0x001c }
            if (r3 == 0) goto L_0x001e
            android.content.Intent r3 = r0.intent     // Catch:{ Exception -> 0x001c }
            java.lang.String r4 = "miui_task_return_to_target"
            android.os.Parcelable r3 = r3.getParcelableExtra(r4)     // Catch:{ Exception -> 0x001c }
            goto L_0x001f
        L_0x001c:
            r3 = move-exception
            goto L_0x0021
        L_0x001e:
            r3 = 0
        L_0x001f:
            r1 = r3
        L_0x0021:
            int r3 = r8.indexOf(r7)
            if (r1 == 0) goto L_0x0042
            if (r3 <= 0) goto L_0x0042
            int r4 = r3 + -1
        L_0x002b:
            if (r4 < 0) goto L_0x0042
            java.lang.Object r5 = r8.get(r4)
            com.android.server.wm.TaskRecord r5 = (com.android.server.wm.TaskRecord) r5
            if (r5 == 0) goto L_0x003f
            android.content.ComponentName r6 = r5.realActivity
            boolean r6 = r1.equals(r6)
            if (r6 == 0) goto L_0x003f
            r2 = 1
            return r2
        L_0x003f:
            int r4 = r4 + -1
            goto L_0x002b
        L_0x0042:
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.wm.ActivityStackInjector.moveTaskIfNeed(com.android.server.wm.TaskRecord, java.util.ArrayList):boolean");
    }

    /* JADX WARNING: Removed duplicated region for block: B:22:0x003f A[RETURN] */
    /* JADX WARNING: Removed duplicated region for block: B:23:0x0040 A[RETURN] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    static boolean findMatchTask(com.android.server.wm.ActivityRecord r6, com.android.server.wm.TaskRecord r7, java.util.ArrayList<com.android.server.wm.TaskRecord> r8) {
        /*
            r0 = 0
            if (r6 == 0) goto L_0x0040
            if (r7 == 0) goto L_0x0040
            com.android.server.wm.ActivityRecord r1 = r7.getTopActivity()
            boolean r2 = com.android.server.wm.MiuiMultiTaskManager.checkMultiTaskAffinity(r6, r1)
            if (r2 != 0) goto L_0x0010
            return r0
        L_0x0010:
            r2 = 0
            r3 = 1
            if (r1 == 0) goto L_0x0026
            android.content.Intent r4 = r1.intent     // Catch:{ Exception -> 0x0024 }
            if (r4 == 0) goto L_0x0026
            android.content.Intent r4 = r1.intent     // Catch:{ Exception -> 0x0024 }
            java.lang.String r5 = "miui_launch_app_in_one_task_group"
            boolean r4 = r4.getBooleanExtra(r5, r0)     // Catch:{ Exception -> 0x0024 }
            if (r4 == 0) goto L_0x0026
            r4 = r3
            goto L_0x0027
        L_0x0024:
            r4 = move-exception
            goto L_0x0029
        L_0x0026:
            r4 = r0
        L_0x0027:
            r2 = r4
        L_0x0029:
            if (r2 == 0) goto L_0x0040
            java.lang.String r4 = r6.packageName
            java.lang.String r5 = r6.launchedFromPackage
            boolean r4 = java.util.Objects.equals(r4, r5)
            if (r4 != 0) goto L_0x0040
            java.lang.String r4 = r6.taskAffinity
            java.lang.String r5 = r7.rootAffinity
            boolean r4 = java.util.Objects.equals(r4, r5)
            if (r4 == 0) goto L_0x0040
            return r3
        L_0x0040:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.wm.ActivityStackInjector.findMatchTask(com.android.server.wm.ActivityRecord, com.android.server.wm.TaskRecord, java.util.ArrayList):boolean");
    }

    static boolean isAllowCross(int userId, int targetUserId) {
        return (userId == 0 && targetUserId == 999) || (targetUserId == 0 && userId == 999);
    }

    static boolean isMiuiHome(ActivityRecord r) {
        return r != null && ("com.miui.home".equals(r.mActivityComponent.getPackageName()) || "com.mi.android.globallauncher".equals(r.mActivityComponent.getPackageName())) && r.intent != null && r.intent.hasCategory("android.intent.category.HOME");
    }

    static boolean isCurrentUser(int userId) {
        return userId == CrossUserUtils.getCurrentUserId();
    }

    static boolean ignoreWallpaper(ActivityRecord r) {
        return r != null && r.getActivityType() == 3;
    }

    static boolean isDefaultHome(AppWindowToken token) {
        if (token == null || token.mActivityRecord == null) {
            return false;
        }
        String packageName = token.mActivityRecord.packageName;
        String homePackage = PackageManagerServiceInjector.getDefaultHome(token.mActivityRecord.mUserId);
        if (homePackage == null) {
            return "com.miui.home".equals(packageName);
        }
        return homePackage.equals(packageName);
    }

    static void onWindowingModeChanged(ActivityStack stack, int fromMode, int toMode) {
        if (stack != null && fromMode == 5 && toMode == 1) {
            ActivityTaskManagerServiceInjector.onFreeFormToFullScreen(stack.topRunningActivityLocked());
        }
    }
}
