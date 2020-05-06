package com.android.server.devicepolicy;

import android.app.admin.DevicePolicyEventLogger;
import android.app.admin.StartInstallingUpdateCallback;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Environment;
import android.os.FileUtils;
import android.os.ParcelFileDescriptor;
import android.os.RemoteException;
import android.util.Log;
import com.android.server.devicepolicy.DevicePolicyManagerService;
import java.io.File;
import java.io.IOException;

abstract class UpdateInstaller {
    static final String TAG = "UpdateInstaller";
    private StartInstallingUpdateCallback mCallback;
    private DevicePolicyConstants mConstants;
    protected Context mContext;
    protected File mCopiedUpdateFile;
    private DevicePolicyManagerService.Injector mInjector;
    private ParcelFileDescriptor mUpdateFileDescriptor;

    public abstract void installUpdateInThread();

    protected UpdateInstaller(Context context, ParcelFileDescriptor updateFileDescriptor, StartInstallingUpdateCallback callback, DevicePolicyManagerService.Injector injector, DevicePolicyConstants constants) {
        this.mContext = context;
        this.mCallback = callback;
        this.mUpdateFileDescriptor = updateFileDescriptor;
        this.mInjector = injector;
        this.mConstants = constants;
    }

    public void startInstallUpdate() {
        this.mCopiedUpdateFile = null;
        if (!isBatteryLevelSufficient()) {
            notifyCallbackOnError(5, "The battery level must be above " + this.mConstants.BATTERY_THRESHOLD_NOT_CHARGING + " while not charging orabove " + this.mConstants.BATTERY_THRESHOLD_CHARGING + " while charging");
            return;
        }
        Thread thread = new Thread(new Runnable() {
            public final void run() {
                UpdateInstaller.this.lambda$startInstallUpdate$0$UpdateInstaller();
            }
        });
        thread.setPriority(10);
        thread.start();
    }

    public /* synthetic */ void lambda$startInstallUpdate$0$UpdateInstaller() {
        this.mCopiedUpdateFile = copyUpdateFileToDataOtaPackageDir();
        if (this.mCopiedUpdateFile == null) {
            notifyCallbackOnError(1, "Error while copying file.");
        } else {
            installUpdateInThread();
        }
    }

    private boolean isBatteryLevelSufficient() {
        Intent batteryStatus = this.mContext.registerReceiver((BroadcastReceiver) null, new IntentFilter("android.intent.action.BATTERY_CHANGED"));
        float batteryPercentage = calculateBatteryPercentage(batteryStatus);
        if (batteryStatus.getIntExtra("plugged", -1) > 0) {
            if (batteryPercentage >= ((float) this.mConstants.BATTERY_THRESHOLD_CHARGING)) {
                return true;
            }
            return false;
        } else if (batteryPercentage >= ((float) this.mConstants.BATTERY_THRESHOLD_NOT_CHARGING)) {
            return true;
        } else {
            return false;
        }
    }

    private float calculateBatteryPercentage(Intent batteryStatus) {
        return ((float) (batteryStatus.getIntExtra("level", -1) * 100)) / ((float) batteryStatus.getIntExtra("scale", -1));
    }

    private File copyUpdateFileToDataOtaPackageDir() {
        try {
            File destination = createNewFileWithPermissions();
            copyToFile(destination);
            return destination;
        } catch (IOException e) {
            Log.w(TAG, "Failed to copy update file to OTA directory", e);
            notifyCallbackOnError(1, Log.getStackTraceString(e));
            return null;
        }
    }

    private File createNewFileWithPermissions() throws IOException {
        File destination = File.createTempFile("update", ".zip", new File(Environment.getDataDirectory() + "/ota_package"));
        FileUtils.setPermissions(destination, 484, -1, -1);
        return destination;
    }

    /* Debug info: failed to restart local var, previous not found, register: 4 */
    /* JADX WARNING: Code restructure failed: missing block: B:13:0x001a, code lost:
        r3 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:15:?, code lost:
        $closeResource(r2, r1);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:16:0x001e, code lost:
        throw r3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:20:0x0021, code lost:
        r2 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:21:0x0022, code lost:
        $closeResource(r1, r0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:22:0x0025, code lost:
        throw r2;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void copyToFile(java.io.File r5) throws java.io.IOException {
        /*
            r4 = this;
            java.io.FileOutputStream r0 = new java.io.FileOutputStream
            r0.<init>(r5)
            android.os.ParcelFileDescriptor$AutoCloseInputStream r1 = new android.os.ParcelFileDescriptor$AutoCloseInputStream     // Catch:{ all -> 0x001f }
            android.os.ParcelFileDescriptor r2 = r4.mUpdateFileDescriptor     // Catch:{ all -> 0x001f }
            r1.<init>(r2)     // Catch:{ all -> 0x001f }
            android.os.FileUtils.copy(r1, r0)     // Catch:{ all -> 0x0018 }
            r2 = 0
            $closeResource(r2, r1)     // Catch:{ all -> 0x001f }
            $closeResource(r2, r0)
            return
        L_0x0018:
            r2 = move-exception
            throw r2     // Catch:{ all -> 0x001a }
        L_0x001a:
            r3 = move-exception
            $closeResource(r2, r1)     // Catch:{ all -> 0x001f }
            throw r3     // Catch:{ all -> 0x001f }
        L_0x001f:
            r1 = move-exception
            throw r1     // Catch:{ all -> 0x0021 }
        L_0x0021:
            r2 = move-exception
            $closeResource(r1, r0)
            throw r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.devicepolicy.UpdateInstaller.copyToFile(java.io.File):void");
    }

    private static /* synthetic */ void $closeResource(Throwable x0, AutoCloseable x1) {
        if (x0 != null) {
            try {
                x1.close();
            } catch (Throwable th) {
                x0.addSuppressed(th);
            }
        } else {
            x1.close();
        }
    }

    /* access modifiers changed from: package-private */
    public void cleanupUpdateFile() {
        File file = this.mCopiedUpdateFile;
        if (file != null && file.exists()) {
            this.mCopiedUpdateFile.delete();
        }
    }

    /* access modifiers changed from: protected */
    public void notifyCallbackOnError(int errorCode, String errorMessage) {
        cleanupUpdateFile();
        DevicePolicyEventLogger.createEvent(74).setInt(errorCode).write();
        try {
            this.mCallback.onStartInstallingUpdateError(errorCode, errorMessage);
        } catch (RemoteException e) {
            Log.d(TAG, "Error while calling callback", e);
        }
    }

    /* access modifiers changed from: protected */
    public void notifyCallbackOnSuccess() {
        cleanupUpdateFile();
        this.mInjector.powerManagerReboot("deviceowner");
    }
}
