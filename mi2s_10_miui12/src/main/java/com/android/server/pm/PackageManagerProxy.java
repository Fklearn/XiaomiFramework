package com.android.server.pm;

import android.content.Context;
import android.content.IIntentReceiver;
import android.content.IIntentSender;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.IPackageDeleteObserver2;
import android.content.pm.IPackageManager;
import android.content.pm.PackageInstaller;
import android.content.pm.PackageParser;
import android.content.pm.VersionedPackage;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Slog;
import android.util.SparseArray;
import com.android.internal.content.PackageHelper;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import libcore.io.IoUtils;

class PackageManagerProxy {
    /* access modifiers changed from: private */
    public static final String TAG = PackageManagerProxy.class.getSimpleName();

    PackageManagerProxy() {
    }

    private static class PackageDeleteObserver extends IPackageDeleteObserver2.Stub {
        boolean finished;
        boolean result;

        private PackageDeleteObserver() {
        }

        public void onUserActionRequired(Intent intent) throws RemoteException {
        }

        public void onPackageDeleted(String packageName, int returnCode, String msg) throws RemoteException {
            synchronized (this) {
                boolean z = true;
                this.finished = true;
                if (returnCode != 1) {
                    z = false;
                }
                this.result = z;
                notifyAll();
            }
        }
    }

    static boolean deleteApp(IPackageManager pm, String pkgName, boolean keepData) {
        int flags = 2;
        if (keepData) {
            flags = 2 | 1;
        }
        PackageDeleteObserver obs = new PackageDeleteObserver();
        try {
            pm.deletePackageVersioned(new VersionedPackage(pkgName, -1), obs, 0, flags);
            synchronized (obs) {
                while (!obs.finished) {
                    try {
                        obs.wait();
                    } catch (InterruptedException e) {
                    }
                }
            }
        } catch (SecurityException e2) {
            e2.printStackTrace();
        } catch (RemoteException e3) {
            e3.printStackTrace();
        }
        return obs.result;
    }

    static Map<File, Integer> installAppList(Context context, List<File> apkFileList) {
        Map<File, Integer> installedResult = new HashMap<>();
        if (apkFileList.isEmpty()) {
            return installedResult;
        }
        SparseArray<File> sessions = new SparseArray<>();
        LocalIntentReceiver receiver = new LocalIntentReceiver(apkFileList.size());
        for (File apkFile : apkFileList) {
            int sessionId = doCreateSession(context, makeSessionParams(apkFile));
            if (sessionId != 0) {
                if (!doWriteSession(context, apkFile, sessionId) || !doCommitSession(context, sessionId, receiver.getIntentSender())) {
                    doAandonSession(context, sessionId);
                } else {
                    sessions.put(sessionId, apkFile);
                }
            }
        }
        int size = sessions.size();
        while (size > 0) {
            Intent result = receiver.getResult();
            int sessionId2 = result.getIntExtra("android.content.pm.extra.SESSION_ID", 0);
            if (sessions.indexOfKey(sessionId2) < 0) {
                Slog.i(TAG, "InstallApp received invalid sessionId:" + sessionId2);
            } else {
                String packageName = result.getStringExtra("android.content.pm.extra.PACKAGE_NAME");
                int status = result.getIntExtra("android.content.pm.extra.STATUS", 1);
                installedResult.put(sessions.get(sessionId2), Integer.valueOf(status));
                if (status != 0) {
                    String errorMsg = result.getStringExtra("android.content.pm.extra.STATUS_MESSAGE");
                    Slog.e(TAG, "InstallApp failed for id:" + sessionId2 + " pkg:" + packageName + " status:" + status + " msg:" + errorMsg);
                }
                size--;
            }
        }
        return installedResult;
    }

    private static int doCreateSession(Context context, PackageInstaller.SessionParams sessionParams) {
        try {
            return context.getPackageManager().getPackageInstaller().createSession(sessionParams);
        } catch (IOException e) {
            Slog.e(TAG, "doCreateSession failed: ", e);
            return 0;
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:34:0x006c  */
    /* JADX WARNING: Removed duplicated region for block: B:36:? A[ORIG_RETURN, RETURN, SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:38:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static boolean doWriteSession(android.content.Context r12, java.io.File r13, int r14) {
        /*
            android.content.pm.PackageManager r0 = r12.getPackageManager()
            android.content.pm.PackageInstaller r0 = r0.getPackageInstaller()
            r1 = 0
            r2 = 0
            r3 = 268435456(0x10000000, float:2.5243549E-29)
            android.os.ParcelFileDescriptor r10 = android.os.ParcelFileDescriptor.open(r13, r3)     // Catch:{ IOException -> 0x0054, RemoteException -> 0x0049, all -> 0x0045 }
            android.content.pm.IPackageManager r2 = android.app.AppGlobals.getPackageManager()     // Catch:{ IOException -> 0x0043, RemoteException -> 0x0041 }
            android.content.pm.IPackageInstaller r2 = r2.getPackageInstaller()     // Catch:{ IOException -> 0x0043, RemoteException -> 0x0041 }
            android.content.pm.IPackageInstallerSession r2 = r2.openSession(r14)     // Catch:{ IOException -> 0x0043, RemoteException -> 0x0041 }
            r4 = r2
            com.android.server.pm.PackageInstallerSession r4 = (com.android.server.pm.PackageInstallerSession) r4     // Catch:{ IOException -> 0x0043, RemoteException -> 0x0041 }
            java.lang.String r5 = "base.apk"
            r6 = 0
            long r8 = r10.getStatSize()     // Catch:{ IOException -> 0x003b, RemoteException -> 0x0038, all -> 0x0034 }
            r11 = 1
            r4.doWriteInternal(r5, r6, r8, r10, r11)     // Catch:{ IOException -> 0x003b, RemoteException -> 0x0038, all -> 0x0034 }
            r1 = 1
            libcore.io.IoUtils.closeQuietly(r10)
            r4.close()
            return r1
        L_0x0034:
            r1 = move-exception
            r2 = r1
            r1 = r4
            goto L_0x0067
        L_0x0038:
            r1 = move-exception
            r1 = r4
            goto L_0x004b
        L_0x003b:
            r1 = move-exception
            r2 = r1
            r1 = r4
            goto L_0x0057
        L_0x003f:
            r2 = move-exception
            goto L_0x0067
        L_0x0041:
            r2 = move-exception
            goto L_0x004b
        L_0x0043:
            r2 = move-exception
            goto L_0x0057
        L_0x0045:
            r3 = move-exception
            r10 = r2
            r2 = r3
            goto L_0x0067
        L_0x0049:
            r3 = move-exception
            r10 = r2
        L_0x004b:
            libcore.io.IoUtils.closeQuietly(r10)
            if (r1 == 0) goto L_0x0065
        L_0x0050:
            r1.close()
            goto L_0x0065
        L_0x0054:
            r3 = move-exception
            r10 = r2
            r2 = r3
        L_0x0057:
            java.lang.String r3 = TAG     // Catch:{ all -> 0x003f }
            java.lang.String r4 = "doWriteSession failed: "
            android.util.Slog.e(r3, r4, r2)     // Catch:{ all -> 0x003f }
            libcore.io.IoUtils.closeQuietly(r10)
            if (r1 == 0) goto L_0x0065
            goto L_0x0050
        L_0x0065:
            r2 = 0
            return r2
        L_0x0067:
            libcore.io.IoUtils.closeQuietly(r10)
            if (r1 == 0) goto L_0x006f
            r1.close()
        L_0x006f:
            throw r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.pm.PackageManagerProxy.doWriteSession(android.content.Context, java.io.File, int):boolean");
    }

    /* JADX INFO: finally extract failed */
    private static boolean doCommitSession(Context context, int sessionId, IntentSender target) {
        PackageInstaller.Session session = null;
        try {
            session = context.getPackageManager().getPackageInstaller().openSession(sessionId);
            session.commit(target);
            IoUtils.closeQuietly(session);
            return true;
        } catch (IOException e) {
            Slog.e(TAG, "doCommitSession failed: ", e);
            IoUtils.closeQuietly(session);
            return false;
        } catch (Throwable th) {
            IoUtils.closeQuietly(session);
            throw th;
        }
    }

    /* JADX INFO: finally extract failed */
    private static boolean doAandonSession(Context context, int sessionId) {
        PackageInstaller.Session session = null;
        try {
            session = context.getPackageManager().getPackageInstaller().openSession(sessionId);
            session.abandon();
            IoUtils.closeQuietly(session);
            return true;
        } catch (IOException e) {
            Slog.e(TAG, "doAandonSession failed: ", e);
            IoUtils.closeQuietly(session);
            return false;
        } catch (Throwable th) {
            IoUtils.closeQuietly(session);
            throw th;
        }
    }

    private static PackageInstaller.SessionParams makeSessionParams(File apkFile) {
        PackageInstaller.SessionParams sessionParams = new PackageInstaller.SessionParams(1);
        sessionParams.setInstallAsInstantApp(false);
        sessionParams.setInstallerPackageName(PackageManagerService.PLATFORM_PACKAGE_NAME);
        try {
            PackageParser.PackageLite pkg = PackageParser.parsePackageLite(apkFile, 0);
            sessionParams.setAppPackageName(pkg.packageName);
            sessionParams.setInstallLocation(pkg.installLocation);
            try {
                sessionParams.setSize(PackageHelper.calculateInstalledSize(pkg, (String) null));
            } catch (IOException e) {
                sessionParams.setSize(apkFile.length());
            }
            return sessionParams;
        } catch (PackageParser.PackageParserException e2) {
            String str = TAG;
            Slog.e(str, "failed to parse packageLite: " + apkFile, e2);
            return null;
        }
    }

    private static class LocalIntentReceiver {
        private IIntentSender.Stub mLocalSender = new IIntentSender.Stub() {
            public void send(int code, Intent intent, String resolvedType, IBinder whitelistToken, IIntentReceiver finishedReceiver, String requiredPermission, Bundle options) {
                LocalIntentReceiver.this.mResult.offer(intent);
            }
        };
        /* access modifiers changed from: private */
        public final LinkedBlockingQueue<Intent> mResult;

        public LocalIntentReceiver(int capacity) {
            this.mResult = new LinkedBlockingQueue<>(capacity);
        }

        public IntentSender getIntentSender() {
            return new IntentSender(this.mLocalSender);
        }

        public Intent getResult() {
            Intent intent = null;
            try {
                intent = this.mResult.poll(30, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                Slog.e(PackageManagerProxy.TAG, "LocalIntentReceiver poll timeout in 30 seconds.");
            }
            return intent != null ? intent : new Intent();
        }
    }
}
