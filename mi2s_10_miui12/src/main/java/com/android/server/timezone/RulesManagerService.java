package com.android.server.timezone;

import android.app.timezone.DistroFormatVersion;
import android.app.timezone.DistroRulesVersion;
import android.app.timezone.ICallback;
import android.app.timezone.IRulesManager;
import android.app.timezone.RulesState;
import android.content.Context;
import android.os.ParcelFileDescriptor;
import android.os.RemoteException;
import android.util.Slog;
import com.android.internal.annotations.VisibleForTesting;
import com.android.server.EventLogTags;
import com.android.server.SystemService;
import com.android.server.am.ProcessPolicy;
import com.android.timezone.distro.DistroException;
import com.android.timezone.distro.DistroVersion;
import com.android.timezone.distro.StagedDistroOperation;
import com.android.timezone.distro.installer.TimeZoneDistroInstaller;
import java.io.File;
import java.io.FileDescriptor;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicBoolean;
import libcore.icu.ICU;
import libcore.timezone.TimeZoneDataFiles;
import libcore.timezone.TimeZoneFinder;
import libcore.timezone.TzDataSetVersion;
import libcore.timezone.ZoneInfoDB;

public final class RulesManagerService extends IRulesManager.Stub {
    @VisibleForTesting(visibility = VisibleForTesting.Visibility.PRIVATE)
    static final DistroFormatVersion DISTRO_FORMAT_VERSION_SUPPORTED = new DistroFormatVersion(TzDataSetVersion.currentFormatMajorVersion(), TzDataSetVersion.currentFormatMinorVersion());
    @VisibleForTesting(visibility = VisibleForTesting.Visibility.PRIVATE)
    static final String REQUIRED_QUERY_PERMISSION = "android.permission.QUERY_TIME_ZONE_RULES";
    @VisibleForTesting(visibility = VisibleForTesting.Visibility.PRIVATE)
    static final String REQUIRED_UPDATER_PERMISSION = "android.permission.UPDATE_TIME_ZONE_RULES";
    private static final String TAG = "timezone.RulesManagerService";
    private final Executor mExecutor;
    /* access modifiers changed from: private */
    public final TimeZoneDistroInstaller mInstaller;
    /* access modifiers changed from: private */
    public final RulesManagerIntentHelper mIntentHelper;
    /* access modifiers changed from: private */
    public final AtomicBoolean mOperationInProgress = new AtomicBoolean(false);
    /* access modifiers changed from: private */
    public final PackageTracker mPackageTracker;
    private final PermissionHelper mPermissionHelper;

    public static class Lifecycle extends SystemService {
        public Lifecycle(Context context) {
            super(context);
        }

        /* JADX WARNING: type inference failed for: r0v1, types: [com.android.server.timezone.RulesManagerService, java.lang.Object, android.os.IBinder] */
        public void onStart() {
            ? access$000 = RulesManagerService.create(getContext());
            access$000.start();
            publishBinderService("timezone", access$000);
            publishLocalService(RulesManagerService.class, access$000);
        }
    }

    /* access modifiers changed from: private */
    public static RulesManagerService create(Context context) {
        RulesManagerServiceHelperImpl helper = new RulesManagerServiceHelperImpl(context);
        return new RulesManagerService(helper, helper, helper, PackageTracker.create(context), new TimeZoneDistroInstaller(TAG, new File(TimeZoneDataFiles.getRuntimeModuleTzVersionFile()), new File(TimeZoneDataFiles.getDataTimeZoneRootDir())));
    }

    @VisibleForTesting(visibility = VisibleForTesting.Visibility.PRIVATE)
    RulesManagerService(PermissionHelper permissionHelper, Executor executor, RulesManagerIntentHelper intentHelper, PackageTracker packageTracker, TimeZoneDistroInstaller timeZoneDistroInstaller) {
        this.mPermissionHelper = permissionHelper;
        this.mExecutor = executor;
        this.mIntentHelper = intentHelper;
        this.mPackageTracker = packageTracker;
        this.mInstaller = timeZoneDistroInstaller;
    }

    public void start() {
        this.mPackageTracker.start();
    }

    public RulesState getRulesState() {
        this.mPermissionHelper.enforceCallerHasPermission(REQUIRED_QUERY_PERMISSION);
        return getRulesStateInternal();
    }

    private RulesState getRulesStateInternal() {
        RulesState rulesState;
        synchronized (this) {
            try {
                TzDataSetVersion baseVersion = this.mInstaller.readBaseVersion();
                int distroStatus = 0;
                DistroRulesVersion installedDistroRulesVersion = null;
                try {
                    DistroVersion installedDistroVersion = this.mInstaller.getInstalledDistroVersion();
                    if (installedDistroVersion == null) {
                        distroStatus = 1;
                        installedDistroRulesVersion = null;
                    } else {
                        distroStatus = 2;
                        installedDistroRulesVersion = new DistroRulesVersion(installedDistroVersion.rulesVersion, installedDistroVersion.revision);
                    }
                } catch (DistroException | IOException e) {
                    Slog.w(TAG, "Failed to read installed distro.", e);
                } catch (Throwable baseVersion2) {
                    throw baseVersion2;
                }
                boolean operationInProgress = this.mOperationInProgress.get();
                DistroRulesVersion stagedDistroRulesVersion = null;
                int stagedOperationStatus = 0;
                if (!operationInProgress) {
                    try {
                        StagedDistroOperation stagedDistroOperation = this.mInstaller.getStagedDistroOperation();
                        if (stagedDistroOperation == null) {
                            stagedOperationStatus = 1;
                        } else if (stagedDistroOperation.isUninstall) {
                            stagedOperationStatus = 2;
                        } else {
                            stagedOperationStatus = 3;
                            DistroVersion stagedDistroVersion = stagedDistroOperation.distroVersion;
                            stagedDistroRulesVersion = new DistroRulesVersion(stagedDistroVersion.rulesVersion, stagedDistroVersion.revision);
                        }
                    } catch (DistroException | IOException e2) {
                        Slog.w(TAG, "Failed to read staged distro.", e2);
                    }
                }
                rulesState = new RulesState(baseVersion.rulesVersion, DISTRO_FORMAT_VERSION_SUPPORTED, operationInProgress, stagedOperationStatus, stagedDistroRulesVersion, distroStatus, installedDistroRulesVersion);
            } catch (IOException e3) {
                Slog.w(TAG, "Failed to read base rules version", e3);
                return null;
            }
        }
        return rulesState;
    }

    /* Debug info: failed to restart local var, previous not found, register: 6 */
    /* JADX WARNING: Code restructure failed: missing block: B:13:0x0026, code lost:
        if (1 == 0) goto L_0x0035;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:15:?, code lost:
        r7.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:16:0x002c, code lost:
        r2 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:17:0x002d, code lost:
        android.util.Slog.w(TAG, "Failed to close distroParcelFileDescriptor", r2);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:22:0x0048, code lost:
        if (0 == 0) goto L_0x0057;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:24:?, code lost:
        r7.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:25:0x004e, code lost:
        r3 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:26:0x004f, code lost:
        android.util.Slog.w(TAG, "Failed to close distroParcelFileDescriptor", r3);
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public int requestInstall(android.os.ParcelFileDescriptor r7, byte[] r8, android.app.timezone.ICallback r9) {
        /*
            r6 = this;
            r0 = 1
            com.android.server.timezone.PermissionHelper r1 = r6.mPermissionHelper     // Catch:{ all -> 0x006c }
            java.lang.String r2 = "android.permission.UPDATE_TIME_ZONE_RULES"
            r1.enforceCallerHasPermission(r2)     // Catch:{ all -> 0x006c }
            r1 = 0
            if (r8 == 0) goto L_0x0010
            com.android.server.timezone.CheckToken r2 = createCheckTokenOrThrow(r8)     // Catch:{ all -> 0x006c }
            r1 = r2
        L_0x0010:
            java.lang.String r2 = toStringOrNull(r1)     // Catch:{ all -> 0x006c }
            com.android.server.EventLogTags.writeTimezoneRequestInstall(r2)     // Catch:{ all -> 0x006c }
            monitor-enter(r6)     // Catch:{ all -> 0x006c }
            if (r7 == 0) goto L_0x0061
            if (r9 == 0) goto L_0x0058
            java.util.concurrent.atomic.AtomicBoolean r2 = r6.mOperationInProgress     // Catch:{ all -> 0x0069 }
            boolean r2 = r2.get()     // Catch:{ all -> 0x0069 }
            r3 = 1
            if (r2 == 0) goto L_0x0036
            monitor-exit(r6)     // Catch:{ all -> 0x0069 }
            if (r0 == 0) goto L_0x0035
            r7.close()     // Catch:{ IOException -> 0x002c }
            goto L_0x0035
        L_0x002c:
            r2 = move-exception
            java.lang.String r4 = "timezone.RulesManagerService"
            java.lang.String r5 = "Failed to close distroParcelFileDescriptor"
            android.util.Slog.w(r4, r5, r2)
        L_0x0035:
            return r3
        L_0x0036:
            java.util.concurrent.atomic.AtomicBoolean r2 = r6.mOperationInProgress     // Catch:{ all -> 0x0069 }
            r2.set(r3)     // Catch:{ all -> 0x0069 }
            java.util.concurrent.Executor r2 = r6.mExecutor     // Catch:{ all -> 0x0069 }
            com.android.server.timezone.RulesManagerService$InstallRunnable r3 = new com.android.server.timezone.RulesManagerService$InstallRunnable     // Catch:{ all -> 0x0069 }
            r3.<init>(r7, r1, r9)     // Catch:{ all -> 0x0069 }
            r2.execute(r3)     // Catch:{ all -> 0x0069 }
            r0 = 0
            r2 = 0
            monitor-exit(r6)     // Catch:{ all -> 0x0069 }
            if (r0 == 0) goto L_0x0057
            r7.close()     // Catch:{ IOException -> 0x004e }
            goto L_0x0057
        L_0x004e:
            r3 = move-exception
            java.lang.String r4 = "timezone.RulesManagerService"
            java.lang.String r5 = "Failed to close distroParcelFileDescriptor"
            android.util.Slog.w(r4, r5, r3)
        L_0x0057:
            return r2
        L_0x0058:
            java.lang.NullPointerException r2 = new java.lang.NullPointerException     // Catch:{ all -> 0x0069 }
            java.lang.String r3 = "observer == null"
            r2.<init>(r3)     // Catch:{ all -> 0x0069 }
            throw r2     // Catch:{ all -> 0x0069 }
        L_0x0061:
            java.lang.NullPointerException r2 = new java.lang.NullPointerException     // Catch:{ all -> 0x0069 }
            java.lang.String r3 = "distroParcelFileDescriptor == null"
            r2.<init>(r3)     // Catch:{ all -> 0x0069 }
            throw r2     // Catch:{ all -> 0x0069 }
        L_0x0069:
            r2 = move-exception
            monitor-exit(r6)     // Catch:{ all -> 0x0069 }
            throw r2     // Catch:{ all -> 0x006c }
        L_0x006c:
            r1 = move-exception
            if (r7 == 0) goto L_0x007e
            if (r0 == 0) goto L_0x007e
            r7.close()     // Catch:{ IOException -> 0x0075 }
            goto L_0x007e
        L_0x0075:
            r2 = move-exception
            java.lang.String r3 = "timezone.RulesManagerService"
            java.lang.String r4 = "Failed to close distroParcelFileDescriptor"
            android.util.Slog.w(r3, r4, r2)
        L_0x007e:
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.timezone.RulesManagerService.requestInstall(android.os.ParcelFileDescriptor, byte[], android.app.timezone.ICallback):int");
    }

    private class InstallRunnable implements Runnable {
        private final ICallback mCallback;
        private final CheckToken mCheckToken;
        private final ParcelFileDescriptor mDistroParcelFileDescriptor;

        InstallRunnable(ParcelFileDescriptor distroParcelFileDescriptor, CheckToken checkToken, ICallback callback) {
            this.mDistroParcelFileDescriptor = distroParcelFileDescriptor;
            this.mCheckToken = checkToken;
            this.mCallback = callback;
        }

        /* Debug info: failed to restart local var, previous not found, register: 10 */
        /* JADX WARNING: Code restructure failed: missing block: B:12:0x0044, code lost:
            r4 = move-exception;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:13:0x0045, code lost:
            if (r2 != null) goto L_0x0047;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:15:?, code lost:
            r2.close();
         */
        /* JADX WARNING: Code restructure failed: missing block: B:19:0x004f, code lost:
            throw r4;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void run() {
            /*
                r10 = this;
                com.android.server.timezone.CheckToken r0 = r10.mCheckToken
                java.lang.String r0 = com.android.server.timezone.RulesManagerService.toStringOrNull(r0)
                com.android.server.EventLogTags.writeTimezoneInstallStarted(r0)
                r0 = 0
                r1 = 0
                android.os.ParcelFileDescriptor r2 = r10.mDistroParcelFileDescriptor     // Catch:{ Exception -> 0x0052 }
                r3 = 0
                java.io.FileInputStream r4 = new java.io.FileInputStream     // Catch:{ all -> 0x0042 }
                java.io.FileDescriptor r5 = r2.getFileDescriptor()     // Catch:{ all -> 0x0042 }
                r4.<init>(r5, r1)     // Catch:{ all -> 0x0042 }
                com.android.timezone.distro.TimeZoneDistro r5 = new com.android.timezone.distro.TimeZoneDistro     // Catch:{ all -> 0x0042 }
                r5.<init>((java.io.InputStream) r4)     // Catch:{ all -> 0x0042 }
                com.android.server.timezone.RulesManagerService r6 = com.android.server.timezone.RulesManagerService.this     // Catch:{ all -> 0x0042 }
                com.android.timezone.distro.installer.TimeZoneDistroInstaller r6 = r6.mInstaller     // Catch:{ all -> 0x0042 }
                int r6 = r6.stageInstallWithErrorCode(r5)     // Catch:{ all -> 0x0042 }
                r10.sendInstallNotificationIntentIfRequired(r6)     // Catch:{ all -> 0x0042 }
                int r7 = r10.mapInstallerResultToApiCode(r6)     // Catch:{ all -> 0x0042 }
                com.android.server.timezone.CheckToken r8 = r10.mCheckToken     // Catch:{ all -> 0x0042 }
                java.lang.String r8 = com.android.server.timezone.RulesManagerService.toStringOrNull(r8)     // Catch:{ all -> 0x0042 }
                com.android.server.EventLogTags.writeTimezoneInstallComplete(r8, r7)     // Catch:{ all -> 0x0042 }
                com.android.server.timezone.RulesManagerService r8 = com.android.server.timezone.RulesManagerService.this     // Catch:{ all -> 0x0042 }
                android.app.timezone.ICallback r9 = r10.mCallback     // Catch:{ all -> 0x0042 }
                r8.sendFinishedStatus(r9, r7)     // Catch:{ all -> 0x0042 }
                r0 = 1
                r2.close()     // Catch:{ Exception -> 0x0052 }
                goto L_0x006c
            L_0x0042:
                r3 = move-exception
                throw r3     // Catch:{ all -> 0x0044 }
            L_0x0044:
                r4 = move-exception
                if (r2 == 0) goto L_0x004f
                r2.close()     // Catch:{ all -> 0x004b }
                goto L_0x004f
            L_0x004b:
                r5 = move-exception
                r3.addSuppressed(r5)     // Catch:{ Exception -> 0x0052 }
            L_0x004f:
                throw r4     // Catch:{ Exception -> 0x0052 }
            L_0x0050:
                r2 = move-exception
                goto L_0x0082
            L_0x0052:
                r2 = move-exception
                java.lang.String r3 = "timezone.RulesManagerService"
                java.lang.String r4 = "Failed to install distro."
                android.util.Slog.w(r3, r4, r2)     // Catch:{ all -> 0x0050 }
                com.android.server.timezone.CheckToken r3 = r10.mCheckToken     // Catch:{ all -> 0x0050 }
                java.lang.String r3 = com.android.server.timezone.RulesManagerService.toStringOrNull(r3)     // Catch:{ all -> 0x0050 }
                r4 = 1
                com.android.server.EventLogTags.writeTimezoneInstallComplete(r3, r4)     // Catch:{ all -> 0x0050 }
                com.android.server.timezone.RulesManagerService r3 = com.android.server.timezone.RulesManagerService.this     // Catch:{ all -> 0x0050 }
                android.app.timezone.ICallback r5 = r10.mCallback     // Catch:{ all -> 0x0050 }
                r3.sendFinishedStatus(r5, r4)     // Catch:{ all -> 0x0050 }
            L_0x006c:
                com.android.server.timezone.RulesManagerService r2 = com.android.server.timezone.RulesManagerService.this
                com.android.server.timezone.PackageTracker r2 = r2.mPackageTracker
                com.android.server.timezone.CheckToken r3 = r10.mCheckToken
                r2.recordCheckResult(r3, r0)
                com.android.server.timezone.RulesManagerService r2 = com.android.server.timezone.RulesManagerService.this
                java.util.concurrent.atomic.AtomicBoolean r2 = r2.mOperationInProgress
                r2.set(r1)
                return
            L_0x0082:
                com.android.server.timezone.RulesManagerService r3 = com.android.server.timezone.RulesManagerService.this
                com.android.server.timezone.PackageTracker r3 = r3.mPackageTracker
                com.android.server.timezone.CheckToken r4 = r10.mCheckToken
                r3.recordCheckResult(r4, r0)
                com.android.server.timezone.RulesManagerService r3 = com.android.server.timezone.RulesManagerService.this
                java.util.concurrent.atomic.AtomicBoolean r3 = r3.mOperationInProgress
                r3.set(r1)
                throw r2
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.server.timezone.RulesManagerService.InstallRunnable.run():void");
        }

        private void sendInstallNotificationIntentIfRequired(int installerResult) {
            if (installerResult == 0) {
                RulesManagerService.this.mIntentHelper.sendTimeZoneOperationStaged();
            }
        }

        private int mapInstallerResultToApiCode(int installerResult) {
            if (installerResult == 0) {
                return 0;
            }
            if (installerResult == 1) {
                return 2;
            }
            if (installerResult == 2) {
                return 3;
            }
            if (installerResult == 3) {
                return 4;
            }
            if (installerResult != 4) {
                return 1;
            }
            return 5;
        }
    }

    /* Debug info: failed to restart local var, previous not found, register: 3 */
    public int requestUninstall(byte[] checkTokenBytes, ICallback callback) {
        this.mPermissionHelper.enforceCallerHasPermission(REQUIRED_UPDATER_PERMISSION);
        CheckToken checkToken = null;
        if (checkTokenBytes != null) {
            checkToken = createCheckTokenOrThrow(checkTokenBytes);
        }
        EventLogTags.writeTimezoneRequestUninstall(toStringOrNull(checkToken));
        synchronized (this) {
            if (callback != null) {
                try {
                    if (this.mOperationInProgress.get()) {
                        return 1;
                    }
                    this.mOperationInProgress.set(true);
                    this.mExecutor.execute(new UninstallRunnable(checkToken, callback));
                    return 0;
                } catch (Throwable th) {
                    throw th;
                }
            } else {
                throw new NullPointerException("callback == null");
            }
        }
    }

    private class UninstallRunnable implements Runnable {
        private final ICallback mCallback;
        private final CheckToken mCheckToken;

        UninstallRunnable(CheckToken checkToken, ICallback callback) {
            this.mCheckToken = checkToken;
            this.mCallback = callback;
        }

        /* JADX WARNING: Removed duplicated region for block: B:10:0x0024 A[Catch:{ Exception -> 0x003a, all -> 0x0038 }] */
        /* JADX WARNING: Removed duplicated region for block: B:11:0x0026 A[Catch:{ Exception -> 0x003a, all -> 0x0038 }] */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void run() {
            /*
                r7 = this;
                com.android.server.timezone.CheckToken r0 = r7.mCheckToken
                java.lang.String r0 = com.android.server.timezone.RulesManagerService.toStringOrNull(r0)
                com.android.server.EventLogTags.writeTimezoneUninstallStarted(r0)
                r0 = 0
                r1 = 1
                r2 = 0
                com.android.server.timezone.RulesManagerService r3 = com.android.server.timezone.RulesManagerService.this     // Catch:{ Exception -> 0x003a }
                com.android.timezone.distro.installer.TimeZoneDistroInstaller r3 = r3.mInstaller     // Catch:{ Exception -> 0x003a }
                int r3 = r3.stageUninstall()     // Catch:{ Exception -> 0x003a }
                r7.sendUninstallNotificationIntentIfRequired(r3)     // Catch:{ Exception -> 0x003a }
                if (r3 == 0) goto L_0x0020
                if (r3 != r1) goto L_0x001e
                goto L_0x0020
            L_0x001e:
                r4 = r2
                goto L_0x0021
            L_0x0020:
                r4 = r1
            L_0x0021:
                r0 = r4
                if (r0 == 0) goto L_0x0026
                r4 = r2
                goto L_0x0027
            L_0x0026:
                r4 = r1
            L_0x0027:
                com.android.server.timezone.CheckToken r5 = r7.mCheckToken     // Catch:{ Exception -> 0x003a }
                java.lang.String r5 = com.android.server.timezone.RulesManagerService.toStringOrNull(r5)     // Catch:{ Exception -> 0x003a }
                com.android.server.EventLogTags.writeTimezoneUninstallComplete(r5, r4)     // Catch:{ Exception -> 0x003a }
                com.android.server.timezone.RulesManagerService r5 = com.android.server.timezone.RulesManagerService.this     // Catch:{ Exception -> 0x003a }
                android.app.timezone.ICallback r6 = r7.mCallback     // Catch:{ Exception -> 0x003a }
                r5.sendFinishedStatus(r6, r4)     // Catch:{ Exception -> 0x003a }
                goto L_0x0053
            L_0x0038:
                r1 = move-exception
                goto L_0x0069
            L_0x003a:
                r3 = move-exception
                com.android.server.timezone.CheckToken r4 = r7.mCheckToken     // Catch:{ all -> 0x0038 }
                java.lang.String r4 = com.android.server.timezone.RulesManagerService.toStringOrNull(r4)     // Catch:{ all -> 0x0038 }
                com.android.server.EventLogTags.writeTimezoneUninstallComplete(r4, r1)     // Catch:{ all -> 0x0038 }
                java.lang.String r4 = "timezone.RulesManagerService"
                java.lang.String r5 = "Failed to uninstall distro."
                android.util.Slog.w(r4, r5, r3)     // Catch:{ all -> 0x0038 }
                com.android.server.timezone.RulesManagerService r4 = com.android.server.timezone.RulesManagerService.this     // Catch:{ all -> 0x0038 }
                android.app.timezone.ICallback r5 = r7.mCallback     // Catch:{ all -> 0x0038 }
                r4.sendFinishedStatus(r5, r1)     // Catch:{ all -> 0x0038 }
            L_0x0053:
                com.android.server.timezone.RulesManagerService r1 = com.android.server.timezone.RulesManagerService.this
                com.android.server.timezone.PackageTracker r1 = r1.mPackageTracker
                com.android.server.timezone.CheckToken r3 = r7.mCheckToken
                r1.recordCheckResult(r3, r0)
                com.android.server.timezone.RulesManagerService r1 = com.android.server.timezone.RulesManagerService.this
                java.util.concurrent.atomic.AtomicBoolean r1 = r1.mOperationInProgress
                r1.set(r2)
                return
            L_0x0069:
                com.android.server.timezone.RulesManagerService r3 = com.android.server.timezone.RulesManagerService.this
                com.android.server.timezone.PackageTracker r3 = r3.mPackageTracker
                com.android.server.timezone.CheckToken r4 = r7.mCheckToken
                r3.recordCheckResult(r4, r0)
                com.android.server.timezone.RulesManagerService r3 = com.android.server.timezone.RulesManagerService.this
                java.util.concurrent.atomic.AtomicBoolean r3 = r3.mOperationInProgress
                r3.set(r2)
                throw r1
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.server.timezone.RulesManagerService.UninstallRunnable.run():void");
        }

        private void sendUninstallNotificationIntentIfRequired(int uninstallResult) {
            if (uninstallResult == 0) {
                RulesManagerService.this.mIntentHelper.sendTimeZoneOperationStaged();
            } else if (uninstallResult == 1) {
                RulesManagerService.this.mIntentHelper.sendTimeZoneOperationUnstaged();
            }
        }
    }

    /* access modifiers changed from: private */
    public void sendFinishedStatus(ICallback callback, int resultCode) {
        try {
            callback.onFinished(resultCode);
        } catch (RemoteException e) {
            Slog.e(TAG, "Unable to notify observer of result", e);
        }
    }

    public void requestNothing(byte[] checkTokenBytes, boolean success) {
        this.mPermissionHelper.enforceCallerHasPermission(REQUIRED_UPDATER_PERMISSION);
        CheckToken checkToken = null;
        if (checkTokenBytes != null) {
            checkToken = createCheckTokenOrThrow(checkTokenBytes);
        }
        EventLogTags.writeTimezoneRequestNothing(toStringOrNull(checkToken));
        this.mPackageTracker.recordCheckResult(checkToken, success);
        EventLogTags.writeTimezoneNothingComplete(toStringOrNull(checkToken));
    }

    /* access modifiers changed from: protected */
    public void dump(FileDescriptor fd, PrintWriter pw, String[] args) {
        if (this.mPermissionHelper.checkDumpPermission(TAG, pw)) {
            RulesState rulesState = getRulesStateInternal();
            if (args != null && args.length == 2) {
                if ("-format_state".equals(args[0]) && args[1] != null) {
                    for (char c : args[1].toCharArray()) {
                        if (c == 'i') {
                            String value = ProcessPolicy.REASON_UNKNOWN;
                            if (rulesState != null) {
                                DistroRulesVersion installedRulesVersion = rulesState.getInstalledDistroRulesVersion();
                                if (installedRulesVersion == null) {
                                    value = "<None>";
                                } else {
                                    value = installedRulesVersion.toDumpString();
                                }
                            }
                            pw.println("Installed rules version: " + value);
                        } else if (c == 't') {
                            String value2 = ProcessPolicy.REASON_UNKNOWN;
                            if (rulesState != null) {
                                DistroRulesVersion stagedDistroRulesVersion = rulesState.getStagedDistroRulesVersion();
                                if (stagedDistroRulesVersion == null) {
                                    value2 = "<None>";
                                } else {
                                    value2 = stagedDistroRulesVersion.toDumpString();
                                }
                            }
                            pw.println("Staged rules version: " + value2);
                        } else if (c == 'o') {
                            String value3 = ProcessPolicy.REASON_UNKNOWN;
                            if (rulesState != null) {
                                value3 = stagedOperationToString(rulesState.getStagedOperationType());
                            }
                            pw.println("Staged operation: " + value3);
                        } else if (c != 'p') {
                            switch (c) {
                                case HdmiCecKeycode.CEC_KEYCODE_PAUSE_PLAY_FUNCTION:
                                    pw.println("Active rules version (ICU, ZoneInfoDB, TimeZoneFinder): " + ICU.getTZDataVersion() + "," + ZoneInfoDB.getInstance().getVersion() + "," + TimeZoneFinder.getInstance().getIanaVersion());
                                    break;
                                case HdmiCecKeycode.CEC_KEYCODE_RECORD_FUNCTION:
                                    String value4 = ProcessPolicy.REASON_UNKNOWN;
                                    if (rulesState != null) {
                                        value4 = rulesState.getBaseRulesVersion();
                                    }
                                    pw.println("Base rules version: " + value4);
                                    break;
                                case 'c':
                                    String value5 = ProcessPolicy.REASON_UNKNOWN;
                                    if (rulesState != null) {
                                        value5 = distroStatusToString(rulesState.getDistroStatus());
                                    }
                                    pw.println("Current install state: " + value5);
                                    break;
                                default:
                                    pw.println("Unknown option: " + c);
                                    break;
                            }
                        } else {
                            String value6 = ProcessPolicy.REASON_UNKNOWN;
                            if (rulesState != null) {
                                value6 = Boolean.toString(rulesState.isOperationInProgress());
                            }
                            pw.println("Operation in progress: " + value6);
                        }
                    }
                    return;
                }
            }
            pw.println("RulesManagerService state: " + toString());
            pw.println("Active rules version (ICU, ZoneInfoDB, TimeZoneFinder): " + ICU.getTZDataVersion() + "," + ZoneInfoDB.getInstance().getVersion() + "," + TimeZoneFinder.getInstance().getIanaVersion());
            StringBuilder sb = new StringBuilder();
            sb.append("Distro state: ");
            sb.append(rulesState.toString());
            pw.println(sb.toString());
            this.mPackageTracker.dump(pw);
        }
    }

    /* access modifiers changed from: package-private */
    public void notifyIdle() {
        this.mPackageTracker.triggerUpdateIfNeeded(false);
    }

    public String toString() {
        return "RulesManagerService{mOperationInProgress=" + this.mOperationInProgress + '}';
    }

    private static CheckToken createCheckTokenOrThrow(byte[] checkTokenBytes) {
        try {
            return CheckToken.fromByteArray(checkTokenBytes);
        } catch (IOException e) {
            throw new IllegalArgumentException("Unable to read token bytes " + Arrays.toString(checkTokenBytes), e);
        }
    }

    private static String distroStatusToString(int distroStatus) {
        if (distroStatus == 1) {
            return "None";
        }
        if (distroStatus != 2) {
            return ProcessPolicy.REASON_UNKNOWN;
        }
        return "Installed";
    }

    private static String stagedOperationToString(int stagedOperationType) {
        if (stagedOperationType == 1) {
            return "None";
        }
        if (stagedOperationType == 2) {
            return "Uninstall";
        }
        if (stagedOperationType != 3) {
            return ProcessPolicy.REASON_UNKNOWN;
        }
        return "Install";
    }

    /* access modifiers changed from: private */
    public static String toStringOrNull(Object obj) {
        if (obj == null) {
            return null;
        }
        return obj.toString();
    }
}
