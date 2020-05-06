package com.android.server.pm;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.ParceledListSlice;
import android.os.ServiceManager;
import android.provider.Settings;
import android.util.Slog;
import java.io.File;
import java.io.IOException;

public class ExtraPackageManagerService {
    static final String RESTORECON_FLAG_FILE = "restorecon_flag";
    private static String TAG = "ExtraPackageManagerService";

    public static void restoreconData(int userId) {
        String str = TAG;
        Slog.i(str, "Restorecon for user: " + userId);
        File flagFile = getExtraRestoreconFlagFile();
        if (!flagFile.exists()) {
            Slog.i(TAG, "There's no restorecon flag file");
            return;
        }
        ParceledListSlice<ApplicationInfo> applicationInfos = ((PackageManagerService) ServiceManager.getService(Settings.ATTR_PACKAGE)).getInstalledApplications(0, userId);
        if (applicationInfos != null) {
            for (ApplicationInfo next : applicationInfos.getList()) {
            }
        }
        String str2 = TAG;
        Slog.i(str2, "Restorecon done, delete flag file: " + flagFile.toString());
        flagFile.delete();
    }

    private static File getExtraRestoreconFlagFile() {
        return new File("/data/system/restorecon_flag");
    }

    static void setExtraRestoreconFlag() {
        Slog.d(TAG, "Set extra restorecon flag");
        File flagFile = getExtraRestoreconFlagFile();
        if (!flagFile.exists()) {
            try {
                flagFile.createNewFile();
                String str = TAG;
                Slog.d(str, "Create flag file:" + flagFile.toString());
            } catch (IOException ioe) {
                String str2 = TAG;
                Slog.e(str2, "Create file:" + flagFile.toString() + " failed", ioe);
            }
        }
    }

    public static void checkExtraRestoreconFlag(Context context) {
        Slog.d(TAG, "Check extra restorecon flag");
        if (Settings.Secure.getIntForUser(context.getContentResolver(), "second_user_id", ScreenRotationAnimationInjector.BLACK_SURFACE_INVALID_POSITION, 0) == -10000) {
            Slog.d(TAG, "There's no second space, delete the flag file");
            File flagFile = getExtraRestoreconFlagFile();
            if (flagFile.exists()) {
                flagFile.delete();
            }
        }
    }
}
