package com.miui.server;

import android.app.usage.StorageStats;
import android.app.usage.StorageStatsManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageManager;
import android.content.pm.PackageStats;
import android.os.Binder;
import android.os.ParcelFileDescriptor;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.UserHandle;
import android.util.Slog;
import com.android.server.BatteryService;

class BackupManagerServiceProxy {
    public static final String TAG = "BackupManagerServiceProxy";

    BackupManagerServiceProxy() {
    }

    static void fullBackup(ParcelFileDescriptor outFileDescriptor, String[] pkgs, boolean includeApk) throws RemoteException {
        ServiceManager.getService(BatteryService.HealthServiceWrapper.INSTANCE_HEALTHD).adbBackup(0, outFileDescriptor, includeApk, true, false, false, false, false, false, false, pkgs);
    }

    static void fullRestore(ParcelFileDescriptor fd) throws RemoteException {
        ServiceManager.getService(BatteryService.HealthServiceWrapper.INSTANCE_HEALTHD).adbRestore(0, fd);
    }

    static void fullCancel() throws RemoteException {
        ServiceManager.getService(BatteryService.HealthServiceWrapper.INSTANCE_HEALTHD).cancelMiuiBackupsForUser(0);
    }

    public static void getPackageSizeInfo(Context context, PackageManager pm, String pkg, int userId, IPackageStatsObserver observer) {
        StorageStatsManager ssm = (StorageStatsManager) context.getSystemService("storagestats");
        long oldId = Binder.clearCallingIdentity();
        try {
            ApplicationInfo appInfo = pm.getPackageInfoAsUser(pkg, 0, userId).applicationInfo;
            StorageStats stats = ssm.queryStatsForPackage(appInfo.storageUuid, appInfo.packageName, UserHandle.getUserHandleForUid(appInfo.uid));
            PackageStats legacy = new PackageStats(pkg, appInfo.uid);
            legacy.codeSize = stats.getCodeBytes();
            legacy.dataSize = stats.getDataBytes();
            legacy.cacheSize = stats.getCacheBytes();
            try {
                observer.onGetStatsCompleted(legacy, true);
            } catch (RemoteException e) {
            }
        } catch (Exception e2) {
            Slog.e(TAG, "getPackageSizeInfo error", e2);
            try {
                observer.onGetStatsCompleted(new PackageStats(pkg, userId), false);
            } catch (RemoteException e3) {
            }
        } catch (Throwable th) {
            Binder.restoreCallingIdentity(oldId);
            throw th;
        }
        Binder.restoreCallingIdentity(oldId);
    }

    public static boolean isPackageStateProtected(PackageManager pm, String packageName, int userId) {
        boolean isProtected = false;
        try {
            isProtected = pm.isPackageStateProtected(packageName, userId);
        } catch (UnsupportedOperationException e) {
            e.printStackTrace();
        }
        Slog.d(TAG, "isPackageStateProtected, packageName:" + packageName + " userId:" + userId + " isProtected:" + isProtected);
        return isProtected;
    }
}
