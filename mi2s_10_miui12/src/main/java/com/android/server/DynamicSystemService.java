package com.android.server;

import android.content.Context;
import android.gsi.GsiInstallParams;
import android.gsi.GsiProgress;
import android.gsi.IGsiService;
import android.net.INetd;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.os.image.IDynamicSystemService;
import android.os.storage.StorageManager;
import android.os.storage.StorageVolume;
import android.util.Slog;
import java.io.File;

public class DynamicSystemService extends IDynamicSystemService.Stub implements IBinder.DeathRecipient {
    private static final int GSID_ROUGH_TIMEOUT_MS = 8192;
    private static final String NO_SERVICE_ERROR = "no gsiservice";
    private static final String PATH_DEFAULT = "/data/gsi";
    private static final String TAG = "DynamicSystemService";
    private Context mContext;
    private volatile IGsiService mGsiService;

    DynamicSystemService(Context context) {
        this.mContext = context;
    }

    private static IGsiService connect(IBinder.DeathRecipient recipient) throws RemoteException {
        IBinder binder = ServiceManager.getService("gsiservice");
        if (binder == null) {
            return null;
        }
        binder.linkToDeath(recipient, 0);
        return IGsiService.Stub.asInterface(binder);
    }

    public void binderDied() {
        Slog.w(TAG, "gsiservice died; reconnecting");
        synchronized (this) {
            this.mGsiService = null;
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:19:?, code lost:
        android.util.Slog.d(TAG, "GsiService is not ready, wait for " + r0 + "ms");
        java.lang.Thread.sleep((long) r0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:20:0x0054, code lost:
        r0 = r0 << 1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:22:0x0059, code lost:
        android.util.Slog.e(TAG, "Interrupted when waiting for GSID");
     */
    /* JADX WARNING: Code restructure failed: missing block: B:23:0x0061, code lost:
        return null;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private android.gsi.IGsiService getGsiService() throws android.os.RemoteException {
        /*
            r4 = this;
            r4.checkPermission()
            java.lang.String r0 = "init.svc.gsid"
            java.lang.String r0 = android.os.SystemProperties.get(r0)
            java.lang.String r1 = "running"
            boolean r0 = r1.equals(r0)
            if (r0 != 0) goto L_0x001a
            java.lang.String r0 = "ctl.start"
            java.lang.String r1 = "gsid"
            android.os.SystemProperties.set(r0, r1)
        L_0x001a:
            r0 = 64
        L_0x001c:
            r1 = 16384(0x4000, float:2.2959E-41)
            if (r0 > r1) goto L_0x0065
            monitor-enter(r4)
            android.gsi.IGsiService r1 = r4.mGsiService     // Catch:{ all -> 0x0062 }
            if (r1 != 0) goto L_0x002b
            android.gsi.IGsiService r1 = connect(r4)     // Catch:{ all -> 0x0062 }
            r4.mGsiService = r1     // Catch:{ all -> 0x0062 }
        L_0x002b:
            android.gsi.IGsiService r1 = r4.mGsiService     // Catch:{ all -> 0x0062 }
            if (r1 == 0) goto L_0x0033
            android.gsi.IGsiService r1 = r4.mGsiService     // Catch:{ all -> 0x0062 }
            monitor-exit(r4)     // Catch:{ all -> 0x0062 }
            return r1
        L_0x0033:
            monitor-exit(r4)     // Catch:{ all -> 0x0062 }
            java.lang.String r1 = "DynamicSystemService"
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ InterruptedException -> 0x0058 }
            r2.<init>()     // Catch:{ InterruptedException -> 0x0058 }
            java.lang.String r3 = "GsiService is not ready, wait for "
            r2.append(r3)     // Catch:{ InterruptedException -> 0x0058 }
            r2.append(r0)     // Catch:{ InterruptedException -> 0x0058 }
            java.lang.String r3 = "ms"
            r2.append(r3)     // Catch:{ InterruptedException -> 0x0058 }
            java.lang.String r2 = r2.toString()     // Catch:{ InterruptedException -> 0x0058 }
            android.util.Slog.d(r1, r2)     // Catch:{ InterruptedException -> 0x0058 }
            long r1 = (long) r0     // Catch:{ InterruptedException -> 0x0058 }
            java.lang.Thread.sleep(r1)     // Catch:{ InterruptedException -> 0x0058 }
            int r0 = r0 << 1
            goto L_0x001c
        L_0x0058:
            r1 = move-exception
            java.lang.String r2 = "DynamicSystemService"
            java.lang.String r3 = "Interrupted when waiting for GSID"
            android.util.Slog.e(r2, r3)
            r2 = 0
            return r2
        L_0x0062:
            r1 = move-exception
            monitor-exit(r4)     // Catch:{ all -> 0x0062 }
            throw r1
        L_0x0065:
            android.os.RemoteException r0 = new android.os.RemoteException
            java.lang.String r1 = "no gsiservice"
            r0.<init>(r1)
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.DynamicSystemService.getGsiService():android.gsi.IGsiService");
    }

    private void checkPermission() {
        if (this.mContext.checkCallingOrSelfPermission("android.permission.MANAGE_DYNAMIC_SYSTEM") != 0) {
            throw new SecurityException("Requires MANAGE_DYNAMIC_SYSTEM permission");
        }
    }

    public boolean startInstallation(long systemSize, long userdataSize) throws RemoteException {
        String path = SystemProperties.get("os.aot.path");
        if (path.isEmpty()) {
            StorageVolume[] volumes = StorageManager.getVolumeList(UserHandle.myUserId(), 256);
            int length = volumes.length;
            int i = 0;
            while (true) {
                if (i >= length) {
                    break;
                }
                StorageVolume volume = volumes[i];
                if (!volume.isEmulated() && volume.isRemovable() && "mounted".equals(volume.getState())) {
                    File sdCard = volume.getPathFile();
                    if (sdCard.isDirectory()) {
                        path = sdCard.getPath();
                        break;
                    }
                }
                i++;
            }
            if (path.isEmpty()) {
                path = PATH_DEFAULT;
            }
            Slog.i(TAG, "startInstallation -> " + path);
        }
        GsiInstallParams installParams = new GsiInstallParams();
        installParams.installDir = path;
        installParams.gsiSize = systemSize;
        installParams.userdataSize = userdataSize;
        if (getGsiService().beginGsiInstall(installParams) == 0) {
            return true;
        }
        return false;
    }

    public GsiProgress getInstallationProgress() throws RemoteException {
        return getGsiService().getInstallProgress();
    }

    public boolean abort() throws RemoteException {
        return getGsiService().cancelGsiInstall();
    }

    public boolean isInUse() throws RemoteException {
        boolean gsidWasRunning = INetd.IF_FLAG_RUNNING.equals(SystemProperties.get("init.svc.gsid"));
        boolean isInUse = false;
        try {
            isInUse = getGsiService().isGsiRunning();
            return isInUse;
        } finally {
            if (!gsidWasRunning && !isInUse) {
                SystemProperties.set("ctl.stop", "gsid");
            }
        }
    }

    public boolean isInstalled() throws RemoteException {
        return getGsiService().isGsiInstalled();
    }

    public boolean isEnabled() throws RemoteException {
        return getGsiService().isGsiEnabled();
    }

    public boolean remove() throws RemoteException {
        return getGsiService().removeGsiInstall();
    }

    public boolean setEnable(boolean enable) throws RemoteException {
        IGsiService gsiService = getGsiService();
        if (!enable) {
            return gsiService.disableGsiInstall();
        }
        if (gsiService.setGsiBootable(gsiService.getGsiBootStatus() == 2) == 0) {
            return true;
        }
        return false;
    }

    public boolean write(byte[] buf) throws RemoteException {
        return getGsiService().commitGsiChunkFromMemory(buf);
    }

    public boolean commit() throws RemoteException {
        return getGsiService().setGsiBootable(true) == 0;
    }
}
