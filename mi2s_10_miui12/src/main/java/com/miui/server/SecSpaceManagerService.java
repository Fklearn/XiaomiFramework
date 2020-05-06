package com.miui.server;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.os.Handler;
import android.provider.Settings;
import android.util.Slog;
import java.util.ArrayList;
import java.util.List;
import miui.util.OldmanUtil;

public class SecSpaceManagerService {
    public static int KID_SPACE_ID = ScreenRotationAnimationInjector.BLACK_SURFACE_INVALID_POSITION;
    public static int SECOND_USER_ID = ScreenRotationAnimationInjector.BLACK_SURFACE_INVALID_POSITION;
    public static final String TAG = "SecSpaceManagerService";
    private static List<String> sDataTransferPackageNames = new ArrayList();

    static {
        sDataTransferPackageNames.add("com.android.fileexplorer");
        sDataTransferPackageNames.add("com.mi.android.globalFileexplorer");
        sDataTransferPackageNames.add("com.miui.securitycore");
        sDataTransferPackageNames.add(AccessController.PACKAGE_GALLERY);
    }

    public static void init(Context context) {
        Slog.d(TAG, "init SecSpaceManagerService");
        if (!OldmanUtil.IS_ELDER_MODE) {
            SECOND_USER_ID = getSecondSpaceId(context);
            if (SECOND_USER_ID != -10000) {
                startSecSpace(context);
            }
            KID_SPACE_ID = getKidSpaceId(context);
            if (KID_SPACE_ID != -10000) {
                Slog.d(TAG, "start KidModeSpaceService");
                startKidSpace(context);
            }
            registerContentObserver(context);
        }
    }

    private static void startSecSpace(Context context) {
        Intent intent = new Intent();
        intent.setComponent(new ComponentName("com.miui.securitycore", "com.miui.securityspace.service.SecondSpaceService"));
        context.startService(intent);
    }

    private static void startKidSpace(Context context) {
        try {
            Intent intent = new Intent();
            intent.setComponent(new ComponentName("com.miui.securitycore", "com.miui.securityspace.service.KidModeSpaceService"));
            context.startService(intent);
        } catch (Exception e) {
            Slog.e(TAG, "start KidModeSpaceService", e);
        }
    }

    private static void registerContentObserver(final Context context) {
        context.getContentResolver().registerContentObserver(Settings.Secure.getUriFor("second_user_id"), true, new ContentObserver((Handler) null) {
            public void onChange(boolean selfChange) {
                SecSpaceManagerService.SECOND_USER_ID = SecSpaceManagerService.getSecondSpaceId(context);
            }
        }, 0);
    }

    /* access modifiers changed from: private */
    public static int getSecondSpaceId(Context context) {
        return Settings.Secure.getInt(context.getContentResolver(), "second_user_id", ScreenRotationAnimationInjector.BLACK_SURFACE_INVALID_POSITION);
    }

    private static int getKidSpaceId(Context context) {
        return Settings.Secure.getIntForUser(context.getContentResolver(), "kid_user_id", ScreenRotationAnimationInjector.BLACK_SURFACE_INVALID_POSITION, 0);
    }

    public static boolean isDataTransferProcess(int userId, String packageName) {
        return userId == SECOND_USER_ID && sDataTransferPackageNames.contains(packageName);
    }
}
