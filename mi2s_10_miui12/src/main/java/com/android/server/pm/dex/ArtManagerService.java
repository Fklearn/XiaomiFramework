package com.android.server.pm.dex;

import android.app.AppOpsManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageManager;
import android.content.pm.PackageInfo;
import android.content.pm.PackageParser;
import android.content.pm.dex.ArtManager;
import android.content.pm.dex.ArtManagerInternal;
import android.content.pm.dex.DexMetadataHelper;
import android.content.pm.dex.IArtManager;
import android.content.pm.dex.ISnapshotRuntimeProfileCallback;
import android.content.pm.dex.PackageOptimizationInfo;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.ParcelFileDescriptor;
import android.os.RemoteException;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.system.Os;
import android.util.ArrayMap;
import android.util.Log;
import android.util.Slog;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.os.BackgroundThread;
import com.android.internal.os.RoSystemProperties;
import com.android.internal.util.ArrayUtils;
import com.android.internal.util.Preconditions;
import com.android.server.LocalServices;
import com.android.server.pm.Installer;
import com.android.server.pm.PackageManagerServiceCompilerMapping;
import dalvik.system.DexFile;
import dalvik.system.VMRuntime;
import java.io.File;
import java.io.FileNotFoundException;
import libcore.io.IoUtils;

public class ArtManagerService extends IArtManager.Stub {
    private static final String BOOT_IMAGE_ANDROID_PACKAGE = "android";
    private static final String BOOT_IMAGE_PROFILE_NAME = "android.prof";
    private static final boolean DEBUG = Log.isLoggable(TAG, 3);
    public static final String DEXOPT_REASON_WITH_DEX_METADATA_ANNOTATION = "-dm";
    private static final String TAG = "ArtManagerService";
    private static final int TRON_COMPILATION_FILTER_ASSUMED_VERIFIED = 2;
    private static final int TRON_COMPILATION_FILTER_ERROR = 0;
    private static final int TRON_COMPILATION_FILTER_EVERYTHING = 11;
    private static final int TRON_COMPILATION_FILTER_EVERYTHING_PROFILE = 10;
    private static final int TRON_COMPILATION_FILTER_EXTRACT = 3;
    private static final int TRON_COMPILATION_FILTER_FAKE_RUN_FROM_APK = 12;
    private static final int TRON_COMPILATION_FILTER_FAKE_RUN_FROM_APK_FALLBACK = 13;
    private static final int TRON_COMPILATION_FILTER_FAKE_RUN_FROM_VDEX_FALLBACK = 14;
    private static final int TRON_COMPILATION_FILTER_QUICKEN = 5;
    private static final int TRON_COMPILATION_FILTER_SPACE = 7;
    private static final int TRON_COMPILATION_FILTER_SPACE_PROFILE = 6;
    private static final int TRON_COMPILATION_FILTER_SPEED = 9;
    private static final int TRON_COMPILATION_FILTER_SPEED_PROFILE = 8;
    private static final int TRON_COMPILATION_FILTER_UNKNOWN = 1;
    private static final int TRON_COMPILATION_FILTER_VERIFY = 4;
    private static final int TRON_COMPILATION_REASON_AB_OTA = 6;
    private static final int TRON_COMPILATION_REASON_BG_DEXOPT = 5;
    private static final int TRON_COMPILATION_REASON_BOOT = 3;
    private static final int TRON_COMPILATION_REASON_BOOTING = 11;
    private static final int TRON_COMPILATION_REASON_ERROR = 0;
    private static final int TRON_COMPILATION_REASON_FIRST_BOOT = 2;
    private static final int TRON_COMPILATION_REASON_INACTIVE = 7;
    private static final int TRON_COMPILATION_REASON_INSTALL = 4;
    private static final int TRON_COMPILATION_REASON_INSTALL_WITH_DEX_METADATA = 9;
    private static final int TRON_COMPILATION_REASON_SECONDARY = 10;
    private static final int TRON_COMPILATION_REASON_SHARED = 8;
    private static final int TRON_COMPILATION_REASON_UNKNOWN = 1;
    private final Context mContext;
    private final Handler mHandler = new Handler(BackgroundThread.getHandler().getLooper());
    private final Object mInstallLock;
    @GuardedBy({"mInstallLock"})
    private final Installer mInstaller;
    private final IPackageManager mPackageManager;

    static {
        verifyTronLoggingConstants();
    }

    public ArtManagerService(Context context, IPackageManager pm, Installer installer, Object installLock) {
        this.mContext = context;
        this.mPackageManager = pm;
        this.mInstaller = installer;
        this.mInstallLock = installLock;
        LocalServices.addService(ArtManagerInternal.class, new ArtManagerInternalImpl());
    }

    private boolean checkAndroidPermissions(int callingUid, String callingPackage) {
        this.mContext.enforceCallingOrSelfPermission("android.permission.READ_RUNTIME_PROFILES", TAG);
        int noteOp = ((AppOpsManager) this.mContext.getSystemService(AppOpsManager.class)).noteOp(43, callingUid, callingPackage);
        if (noteOp == 0) {
            return true;
        }
        if (noteOp != 3) {
            return false;
        }
        this.mContext.enforceCallingOrSelfPermission("android.permission.PACKAGE_USAGE_STATS", TAG);
        return true;
    }

    private boolean checkShellPermissions(int profileType, String packageName, int callingUid) {
        if (callingUid != 2000) {
            return false;
        }
        if (RoSystemProperties.DEBUGGABLE) {
            return true;
        }
        if (profileType == 1) {
            return false;
        }
        PackageInfo info = null;
        try {
            info = this.mPackageManager.getPackageInfo(packageName, 0, 0);
        } catch (RemoteException e) {
        }
        if (info != null && (info.applicationInfo.flags & 2) == 2) {
            return true;
        }
        return false;
    }

    public void snapshotRuntimeProfile(int profileType, String packageName, String codePath, ISnapshotRuntimeProfileCallback callback, String callingPackage) {
        int callingUid = Binder.getCallingUid();
        if (checkShellPermissions(profileType, packageName, callingUid) || checkAndroidPermissions(callingUid, callingPackage)) {
            Preconditions.checkNotNull(callback);
            boolean bootImageProfile = true;
            if (profileType != 1) {
                bootImageProfile = false;
            }
            if (!bootImageProfile) {
                Preconditions.checkStringNotEmpty(codePath);
                Preconditions.checkStringNotEmpty(packageName);
            }
            if (isRuntimeProfilingEnabled(profileType, callingPackage)) {
                if (DEBUG) {
                    Slog.d(TAG, "Requested snapshot for " + packageName + ":" + codePath);
                }
                if (bootImageProfile) {
                    snapshotBootImageProfile(callback);
                } else {
                    snapshotAppProfile(packageName, codePath, callback);
                }
            } else {
                throw new IllegalStateException("Runtime profiling is not enabled for " + profileType);
            }
        } else {
            try {
                callback.onError(2);
            } catch (RemoteException e) {
            }
        }
    }

    private void snapshotAppProfile(String packageName, String codePath, ISnapshotRuntimeProfileCallback callback) {
        PackageInfo info = null;
        try {
            info = this.mPackageManager.getPackageInfo(packageName, 0, 0);
        } catch (RemoteException e) {
        }
        if (info == null) {
            postError(callback, packageName, 0);
            return;
        }
        boolean pathFound = info.applicationInfo.getBaseCodePath().equals(codePath);
        String splitName = null;
        String[] splitCodePaths = info.applicationInfo.getSplitCodePaths();
        if (!pathFound && splitCodePaths != null) {
            int i = splitCodePaths.length - 1;
            while (true) {
                if (i < 0) {
                    break;
                } else if (splitCodePaths[i].equals(codePath)) {
                    pathFound = true;
                    splitName = info.applicationInfo.splitNames[i];
                    break;
                } else {
                    i--;
                }
            }
        }
        if (!pathFound) {
            postError(callback, packageName, 1);
            return;
        }
        int appId = UserHandle.getAppId(info.applicationInfo.uid);
        if (appId < 0) {
            postError(callback, packageName, 2);
            Slog.wtf(TAG, "AppId is -1 for package: " + packageName);
            return;
        }
        createProfileSnapshot(packageName, ArtManager.getProfileName(splitName), codePath, appId, callback);
        destroyProfileSnapshot(packageName, ArtManager.getProfileName(splitName));
    }

    /* JADX WARNING: Code restructure failed: missing block: B:11:0x0013, code lost:
        r0 = android.content.pm.dex.ArtManager.getProfileSnapshotFileForName(r7, r8);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:13:?, code lost:
        r2 = android.os.ParcelFileDescriptor.open(r0, 268435456);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:14:0x001f, code lost:
        if (r2 == null) goto L_0x0030;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:16:0x0029, code lost:
        if (r2.getFileDescriptor().valid() != false) goto L_0x002c;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:18:0x002c, code lost:
        postSuccess(r7, r2, r11);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:19:0x0030, code lost:
        r4 = new java.lang.StringBuilder();
        r4.append("ParcelFileDescriptor.open returned an invalid descriptor for ");
        r4.append(r7);
        r4.append(":");
        r4.append(r0);
        r4.append(". isNull=");
     */
    /* JADX WARNING: Code restructure failed: missing block: B:20:0x004c, code lost:
        if (r2 != null) goto L_0x0050;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:21:0x004e, code lost:
        r5 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:22:0x0050, code lost:
        r5 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:23:0x0051, code lost:
        r4.append(r5);
        android.util.Slog.wtf(TAG, r4.toString());
        postError(r11, r7, 2);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:24:0x005f, code lost:
        r3 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:25:0x0060, code lost:
        android.util.Slog.w(TAG, "Could not open snapshot profile for " + r7 + ":" + r0, r3);
        postError(r11, r7, 2);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:34:?, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:35:?, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:36:?, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void createProfileSnapshot(java.lang.String r7, java.lang.String r8, java.lang.String r9, int r10, android.content.pm.dex.ISnapshotRuntimeProfileCallback r11) {
        /*
            r6 = this;
            java.lang.Object r0 = r6.mInstallLock
            monitor-enter(r0)
            r1 = 2
            com.android.server.pm.Installer r2 = r6.mInstaller     // Catch:{ InstallerException -> 0x0084 }
            boolean r2 = r2.createProfileSnapshot(r10, r7, r8, r9)     // Catch:{ InstallerException -> 0x0084 }
            if (r2 != 0) goto L_0x0011
            r6.postError(r11, r7, r1)     // Catch:{ InstallerException -> 0x0084 }
            monitor-exit(r0)     // Catch:{ all -> 0x0082 }
            return
        L_0x0011:
            monitor-exit(r0)     // Catch:{ all -> 0x0082 }
            java.io.File r0 = android.content.pm.dex.ArtManager.getProfileSnapshotFileForName(r7, r8)
            r2 = 0
            r3 = 268435456(0x10000000, float:2.5243549E-29)
            android.os.ParcelFileDescriptor r3 = android.os.ParcelFileDescriptor.open(r0, r3)     // Catch:{ FileNotFoundException -> 0x005f }
            r2 = r3
            if (r2 == 0) goto L_0x0030
            java.io.FileDescriptor r3 = r2.getFileDescriptor()     // Catch:{ FileNotFoundException -> 0x005f }
            boolean r3 = r3.valid()     // Catch:{ FileNotFoundException -> 0x005f }
            if (r3 != 0) goto L_0x002c
            goto L_0x0030
        L_0x002c:
            r6.postSuccess(r7, r2, r11)     // Catch:{ FileNotFoundException -> 0x005f }
            goto L_0x005e
        L_0x0030:
            java.lang.String r3 = "ArtManagerService"
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ FileNotFoundException -> 0x005f }
            r4.<init>()     // Catch:{ FileNotFoundException -> 0x005f }
            java.lang.String r5 = "ParcelFileDescriptor.open returned an invalid descriptor for "
            r4.append(r5)     // Catch:{ FileNotFoundException -> 0x005f }
            r4.append(r7)     // Catch:{ FileNotFoundException -> 0x005f }
            java.lang.String r5 = ":"
            r4.append(r5)     // Catch:{ FileNotFoundException -> 0x005f }
            r4.append(r0)     // Catch:{ FileNotFoundException -> 0x005f }
            java.lang.String r5 = ". isNull="
            r4.append(r5)     // Catch:{ FileNotFoundException -> 0x005f }
            if (r2 != 0) goto L_0x0050
            r5 = 1
            goto L_0x0051
        L_0x0050:
            r5 = 0
        L_0x0051:
            r4.append(r5)     // Catch:{ FileNotFoundException -> 0x005f }
            java.lang.String r4 = r4.toString()     // Catch:{ FileNotFoundException -> 0x005f }
            android.util.Slog.wtf(r3, r4)     // Catch:{ FileNotFoundException -> 0x005f }
            r6.postError(r11, r7, r1)     // Catch:{ FileNotFoundException -> 0x005f }
        L_0x005e:
            goto L_0x0081
        L_0x005f:
            r3 = move-exception
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            java.lang.String r5 = "Could not open snapshot profile for "
            r4.append(r5)
            r4.append(r7)
            java.lang.String r5 = ":"
            r4.append(r5)
            r4.append(r0)
            java.lang.String r4 = r4.toString()
            java.lang.String r5 = "ArtManagerService"
            android.util.Slog.w(r5, r4, r3)
            r6.postError(r11, r7, r1)
        L_0x0081:
            return
        L_0x0082:
            r1 = move-exception
            goto L_0x008a
        L_0x0084:
            r2 = move-exception
            r6.postError(r11, r7, r1)     // Catch:{ all -> 0x0082 }
            monitor-exit(r0)     // Catch:{ all -> 0x0082 }
            return
        L_0x008a:
            monitor-exit(r0)     // Catch:{ all -> 0x0082 }
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.pm.dex.ArtManagerService.createProfileSnapshot(java.lang.String, java.lang.String, java.lang.String, int, android.content.pm.dex.ISnapshotRuntimeProfileCallback):void");
    }

    private void destroyProfileSnapshot(String packageName, String profileName) {
        if (DEBUG) {
            Slog.d(TAG, "Destroying profile snapshot for" + packageName + ":" + profileName);
        }
        synchronized (this.mInstallLock) {
            try {
                this.mInstaller.destroyProfileSnapshot(packageName, profileName);
            } catch (Installer.InstallerException e) {
                Slog.e(TAG, "Failed to destroy profile snapshot for " + packageName + ":" + profileName, e);
            }
        }
    }

    public boolean isRuntimeProfilingEnabled(int profileType, String callingPackage) {
        int callingUid = Binder.getCallingUid();
        if (callingUid != 2000 && !checkAndroidPermissions(callingUid, callingPackage)) {
            return false;
        }
        if (profileType == 0) {
            return SystemProperties.getBoolean("dalvik.vm.usejitprofiles", false);
        }
        if (profileType != 1) {
            throw new IllegalArgumentException("Invalid profile type:" + profileType);
        } else if ((Build.IS_USERDEBUG || Build.IS_ENG) && SystemProperties.getBoolean("dalvik.vm.usejitprofiles", false) && SystemProperties.getBoolean("dalvik.vm.profilebootimage", false)) {
            return true;
        } else {
            return false;
        }
    }

    private void snapshotBootImageProfile(ISnapshotRuntimeProfileCallback callback) {
        createProfileSnapshot("android", BOOT_IMAGE_PROFILE_NAME, String.join(":", new CharSequence[]{Os.getenv("BOOTCLASSPATH"), Os.getenv("SYSTEMSERVERCLASSPATH")}), -1, callback);
        destroyProfileSnapshot("android", BOOT_IMAGE_PROFILE_NAME);
    }

    private void postError(ISnapshotRuntimeProfileCallback callback, String packageName, int errCode) {
        if (DEBUG) {
            Slog.d(TAG, "Failed to snapshot profile for " + packageName + " with error: " + errCode);
        }
        this.mHandler.post(new Runnable(callback, errCode, packageName) {
            private final /* synthetic */ ISnapshotRuntimeProfileCallback f$0;
            private final /* synthetic */ int f$1;
            private final /* synthetic */ String f$2;

            {
                this.f$0 = r1;
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void run() {
                ArtManagerService.lambda$postError$0(this.f$0, this.f$1, this.f$2);
            }
        });
    }

    static /* synthetic */ void lambda$postError$0(ISnapshotRuntimeProfileCallback callback, int errCode, String packageName) {
        try {
            callback.onError(errCode);
        } catch (Exception e) {
            Slog.w(TAG, "Failed to callback after profile snapshot for " + packageName, e);
        }
    }

    private void postSuccess(String packageName, ParcelFileDescriptor fd, ISnapshotRuntimeProfileCallback callback) {
        if (DEBUG) {
            Slog.d(TAG, "Successfully snapshot profile for " + packageName);
        }
        this.mHandler.post(new Runnable(fd, callback, packageName) {
            private final /* synthetic */ ParcelFileDescriptor f$0;
            private final /* synthetic */ ISnapshotRuntimeProfileCallback f$1;
            private final /* synthetic */ String f$2;

            {
                this.f$0 = r1;
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void run() {
                ArtManagerService.lambda$postSuccess$1(this.f$0, this.f$1, this.f$2);
            }
        });
    }

    static /* synthetic */ void lambda$postSuccess$1(ParcelFileDescriptor fd, ISnapshotRuntimeProfileCallback callback, String packageName) {
        try {
            if (fd.getFileDescriptor().valid()) {
                callback.onSuccess(fd);
            } else {
                Slog.wtf(TAG, "The snapshot FD became invalid before posting the result for " + packageName);
                callback.onError(2);
            }
        } catch (Exception e) {
            Slog.w(TAG, "Failed to call onSuccess after profile snapshot for " + packageName, e);
        } catch (Throwable th) {
            IoUtils.closeQuietly(fd);
            throw th;
        }
        IoUtils.closeQuietly(fd);
    }

    /* Debug info: failed to restart local var, previous not found, register: 17 */
    public void prepareAppProfiles(PackageParser.Package pkg, int user, boolean updateReferenceProfileContent) {
        String dexMetadataPath;
        Installer installer;
        PackageParser.Package packageR = pkg;
        int i = user;
        int appId = UserHandle.getAppId(packageR.applicationInfo.uid);
        if (i < 0) {
            Slog.wtf(TAG, "Invalid user id: " + i);
        } else if (appId < 0) {
            Slog.wtf(TAG, "Invalid app id: " + appId);
        } else {
            try {
                ArrayMap<String, String> codePathsProfileNames = getPackageProfileNames(pkg);
                int i2 = codePathsProfileNames.size() - 1;
                while (i2 >= 0) {
                    String codePath = codePathsProfileNames.keyAt(i2);
                    String profileName = codePathsProfileNames.valueAt(i2);
                    if (updateReferenceProfileContent) {
                        File dexMetadata = DexMetadataHelper.findDexMetadataForFile(new File(codePath));
                        dexMetadataPath = dexMetadata == null ? null : dexMetadata.getAbsolutePath();
                    } else {
                        dexMetadataPath = null;
                    }
                    Installer installer2 = this.mInstaller;
                    synchronized (installer2) {
                        try {
                            installer = installer2;
                            if (!this.mInstaller.prepareAppProfile(packageR.packageName, user, appId, profileName, codePath, dexMetadataPath)) {
                                Slog.e(TAG, "Failed to prepare profile for " + packageR.packageName + ":" + codePath);
                            }
                            i2--;
                        } catch (Throwable th) {
                            th = th;
                            throw th;
                        }
                    }
                }
            } catch (Installer.InstallerException e) {
                Slog.e(TAG, "Failed to prepare profile for " + packageR.packageName, e);
            }
        }
    }

    public void prepareAppProfiles(PackageParser.Package pkg, int[] user, boolean updateReferenceProfileContent) {
        for (int prepareAppProfiles : user) {
            prepareAppProfiles(pkg, prepareAppProfiles, updateReferenceProfileContent);
        }
    }

    public void clearAppProfiles(PackageParser.Package pkg) {
        try {
            ArrayMap<String, String> packageProfileNames = getPackageProfileNames(pkg);
            for (int i = packageProfileNames.size() - 1; i >= 0; i--) {
                this.mInstaller.clearAppProfiles(pkg.packageName, packageProfileNames.valueAt(i));
            }
        } catch (Installer.InstallerException e) {
            Slog.w(TAG, String.valueOf(e));
        }
    }

    /* Debug info: failed to restart local var, previous not found, register: 8 */
    public void dumpProfiles(PackageParser.Package pkg) {
        int sharedGid = UserHandle.getSharedAppGid(pkg.applicationInfo.uid);
        try {
            ArrayMap<String, String> packageProfileNames = getPackageProfileNames(pkg);
            for (int i = packageProfileNames.size() - 1; i >= 0; i--) {
                String codePath = packageProfileNames.keyAt(i);
                String profileName = packageProfileNames.valueAt(i);
                synchronized (this.mInstallLock) {
                    this.mInstaller.dumpProfiles(sharedGid, pkg.packageName, profileName, codePath);
                }
            }
        } catch (Installer.InstallerException e) {
            Slog.w(TAG, "Failed to dump profiles", e);
        }
    }

    /* Debug info: failed to restart local var, previous not found, register: 10 */
    public boolean compileLayouts(PackageParser.Package pkg) {
        boolean compileLayouts;
        try {
            String packageName = pkg.packageName;
            String apkPath = pkg.baseCodePath;
            ApplicationInfo appInfo = pkg.applicationInfo;
            String outDexFile = appInfo.dataDir + "/code_cache/compiled_view.dex";
            if (!appInfo.isPrivilegedApp() && !appInfo.isEmbeddedDexUsed()) {
                if (!appInfo.isDefaultToDeviceProtectedStorage()) {
                    Log.i("PackageManager", "Compiling layouts in " + packageName + " (" + apkPath + ") to " + outDexFile);
                    long callingId = Binder.clearCallingIdentity();
                    try {
                        synchronized (this.mInstallLock) {
                            compileLayouts = this.mInstaller.compileLayouts(apkPath, packageName, outDexFile, appInfo.uid);
                        }
                        Binder.restoreCallingIdentity(callingId);
                        return compileLayouts;
                    } catch (Throwable th) {
                        Binder.restoreCallingIdentity(callingId);
                        throw th;
                    }
                }
            }
            return false;
        } catch (Throwable e) {
            Log.e("PackageManager", "Failed to compile layouts", e);
            return false;
        }
    }

    private ArrayMap<String, String> getPackageProfileNames(PackageParser.Package pkg) {
        ArrayMap<String, String> result = new ArrayMap<>();
        if ((pkg.applicationInfo.flags & 4) != 0) {
            result.put(pkg.baseCodePath, ArtManager.getProfileName((String) null));
        }
        if (!ArrayUtils.isEmpty(pkg.splitCodePaths)) {
            for (int i = 0; i < pkg.splitCodePaths.length; i++) {
                if ((pkg.splitFlags[i] & 4) != 0) {
                    result.put(pkg.splitCodePaths[i], ArtManager.getProfileName(pkg.splitNames[i]));
                }
            }
        }
        return result;
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Can't fix incorrect switch cases order */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static int getCompilationReasonTronValue(java.lang.String r13) {
        /*
            int r0 = r13.hashCode()
            r1 = 9
            r2 = 11
            r3 = 10
            r4 = 8
            r5 = 7
            r6 = 6
            r7 = 5
            r8 = 4
            r9 = 3
            r10 = 2
            r11 = 0
            r12 = 1
            switch(r0) {
                case -1968171580: goto L_0x0090;
                case -1425983632: goto L_0x0086;
                case -903566235: goto L_0x007b;
                case -817598092: goto L_0x0070;
                case -284840886: goto L_0x0065;
                case -207505425: goto L_0x005b;
                case 3029746: goto L_0x0051;
                case 24665195: goto L_0x0046;
                case 64954288: goto L_0x003c;
                case 96784904: goto L_0x0031;
                case 900392443: goto L_0x0025;
                case 1957569947: goto L_0x0019;
                default: goto L_0x0017;
            }
        L_0x0017:
            goto L_0x009a
        L_0x0019:
            java.lang.String r0 = "install"
            boolean r0 = r13.equals(r0)
            if (r0 == 0) goto L_0x0017
            r0 = r8
            goto L_0x009b
        L_0x0025:
            java.lang.String r0 = "install-dm"
            boolean r0 = r13.equals(r0)
            if (r0 == 0) goto L_0x0017
            r0 = r2
            goto L_0x009b
        L_0x0031:
            java.lang.String r0 = "error"
            boolean r0 = r13.equals(r0)
            if (r0 == 0) goto L_0x0017
            r0 = r12
            goto L_0x009b
        L_0x003c:
            java.lang.String r0 = "booting"
            boolean r0 = r13.equals(r0)
            if (r0 == 0) goto L_0x0017
            r0 = r3
            goto L_0x009b
        L_0x0046:
            java.lang.String r0 = "inactive"
            boolean r0 = r13.equals(r0)
            if (r0 == 0) goto L_0x0017
            r0 = r5
            goto L_0x009b
        L_0x0051:
            java.lang.String r0 = "boot"
            boolean r0 = r13.equals(r0)
            if (r0 == 0) goto L_0x0017
            r0 = r9
            goto L_0x009b
        L_0x005b:
            java.lang.String r0 = "first-boot"
            boolean r0 = r13.equals(r0)
            if (r0 == 0) goto L_0x0017
            r0 = r10
            goto L_0x009b
        L_0x0065:
            java.lang.String r0 = "unknown"
            boolean r0 = r13.equals(r0)
            if (r0 == 0) goto L_0x0017
            r0 = r11
            goto L_0x009b
        L_0x0070:
            java.lang.String r0 = "secondary"
            boolean r0 = r13.equals(r0)
            if (r0 == 0) goto L_0x0017
            r0 = r1
            goto L_0x009b
        L_0x007b:
            java.lang.String r0 = "shared"
            boolean r0 = r13.equals(r0)
            if (r0 == 0) goto L_0x0017
            r0 = r4
            goto L_0x009b
        L_0x0086:
            java.lang.String r0 = "ab-ota"
            boolean r0 = r13.equals(r0)
            if (r0 == 0) goto L_0x0017
            r0 = r6
            goto L_0x009b
        L_0x0090:
            java.lang.String r0 = "bg-dexopt"
            boolean r0 = r13.equals(r0)
            if (r0 == 0) goto L_0x0017
            r0 = r7
            goto L_0x009b
        L_0x009a:
            r0 = -1
        L_0x009b:
            switch(r0) {
                case 0: goto L_0x00aa;
                case 1: goto L_0x00a9;
                case 2: goto L_0x00a8;
                case 3: goto L_0x00a7;
                case 4: goto L_0x00a6;
                case 5: goto L_0x00a5;
                case 6: goto L_0x00a4;
                case 7: goto L_0x00a3;
                case 8: goto L_0x00a2;
                case 9: goto L_0x00a1;
                case 10: goto L_0x00a0;
                case 11: goto L_0x009f;
                default: goto L_0x009e;
            }
        L_0x009e:
            return r12
        L_0x009f:
            return r1
        L_0x00a0:
            return r2
        L_0x00a1:
            return r3
        L_0x00a2:
            return r4
        L_0x00a3:
            return r5
        L_0x00a4:
            return r6
        L_0x00a5:
            return r7
        L_0x00a6:
            return r8
        L_0x00a7:
            return r9
        L_0x00a8:
            return r10
        L_0x00a9:
            return r11
        L_0x00aa:
            return r12
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.pm.dex.ArtManagerService.getCompilationReasonTronValue(java.lang.String):int");
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Can't fix incorrect switch cases order */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static int getCompilationFilterTronValue(java.lang.String r17) {
        /*
            r0 = r17
            int r1 = r17.hashCode()
            r2 = 14
            r3 = 13
            r4 = 12
            r5 = 11
            r6 = 10
            r7 = 9
            r8 = 8
            r9 = 7
            r10 = 6
            r11 = 5
            r12 = 4
            r13 = 3
            r14 = 2
            r15 = 0
            r16 = 1
            switch(r1) {
                case -1957514039: goto L_0x00bf;
                case -1803365233: goto L_0x00b5;
                case -1305289599: goto L_0x00ab;
                case -1129892317: goto L_0x00a0;
                case -902315795: goto L_0x0095;
                case -819951495: goto L_0x008a;
                case -284840886: goto L_0x007e;
                case 96784904: goto L_0x0074;
                case 109637894: goto L_0x0069;
                case 109641799: goto L_0x005d;
                case 348518370: goto L_0x0051;
                case 401590963: goto L_0x0046;
                case 658336598: goto L_0x003a;
                case 922064507: goto L_0x002e;
                case 1906552308: goto L_0x0022;
                default: goto L_0x0020;
            }
        L_0x0020:
            goto L_0x00c9
        L_0x0022:
            java.lang.String r1 = "run-from-apk-fallback"
            boolean r1 = r0.equals(r1)
            if (r1 == 0) goto L_0x0020
            r1 = r3
            goto L_0x00ca
        L_0x002e:
            java.lang.String r1 = "run-from-apk"
            boolean r1 = r0.equals(r1)
            if (r1 == 0) goto L_0x0020
            r1 = r4
            goto L_0x00ca
        L_0x003a:
            java.lang.String r1 = "quicken"
            boolean r1 = r0.equals(r1)
            if (r1 == 0) goto L_0x0020
            r1 = r11
            goto L_0x00ca
        L_0x0046:
            java.lang.String r1 = "everything"
            boolean r1 = r0.equals(r1)
            if (r1 == 0) goto L_0x0020
            r1 = r5
            goto L_0x00ca
        L_0x0051:
            java.lang.String r1 = "space-profile"
            boolean r1 = r0.equals(r1)
            if (r1 == 0) goto L_0x0020
            r1 = r10
            goto L_0x00ca
        L_0x005d:
            java.lang.String r1 = "speed"
            boolean r1 = r0.equals(r1)
            if (r1 == 0) goto L_0x0020
            r1 = r7
            goto L_0x00ca
        L_0x0069:
            java.lang.String r1 = "space"
            boolean r1 = r0.equals(r1)
            if (r1 == 0) goto L_0x0020
            r1 = r9
            goto L_0x00ca
        L_0x0074:
            java.lang.String r1 = "error"
            boolean r1 = r0.equals(r1)
            if (r1 == 0) goto L_0x0020
            r1 = r15
            goto L_0x00ca
        L_0x007e:
            java.lang.String r1 = "unknown"
            boolean r1 = r0.equals(r1)
            if (r1 == 0) goto L_0x0020
            r1 = r16
            goto L_0x00ca
        L_0x008a:
            java.lang.String r1 = "verify"
            boolean r1 = r0.equals(r1)
            if (r1 == 0) goto L_0x0020
            r1 = r12
            goto L_0x00ca
        L_0x0095:
            java.lang.String r1 = "run-from-vdex-fallback"
            boolean r1 = r0.equals(r1)
            if (r1 == 0) goto L_0x0020
            r1 = r2
            goto L_0x00ca
        L_0x00a0:
            java.lang.String r1 = "speed-profile"
            boolean r1 = r0.equals(r1)
            if (r1 == 0) goto L_0x0020
            r1 = r8
            goto L_0x00ca
        L_0x00ab:
            java.lang.String r1 = "extract"
            boolean r1 = r0.equals(r1)
            if (r1 == 0) goto L_0x0020
            r1 = r13
            goto L_0x00ca
        L_0x00b5:
            java.lang.String r1 = "everything-profile"
            boolean r1 = r0.equals(r1)
            if (r1 == 0) goto L_0x0020
            r1 = r6
            goto L_0x00ca
        L_0x00bf:
            java.lang.String r1 = "assume-verified"
            boolean r1 = r0.equals(r1)
            if (r1 == 0) goto L_0x0020
            r1 = r14
            goto L_0x00ca
        L_0x00c9:
            r1 = -1
        L_0x00ca:
            switch(r1) {
                case 0: goto L_0x00dc;
                case 1: goto L_0x00db;
                case 2: goto L_0x00da;
                case 3: goto L_0x00d9;
                case 4: goto L_0x00d8;
                case 5: goto L_0x00d7;
                case 6: goto L_0x00d6;
                case 7: goto L_0x00d5;
                case 8: goto L_0x00d4;
                case 9: goto L_0x00d3;
                case 10: goto L_0x00d2;
                case 11: goto L_0x00d1;
                case 12: goto L_0x00d0;
                case 13: goto L_0x00cf;
                case 14: goto L_0x00ce;
                default: goto L_0x00cd;
            }
        L_0x00cd:
            return r16
        L_0x00ce:
            return r2
        L_0x00cf:
            return r3
        L_0x00d0:
            return r4
        L_0x00d1:
            return r5
        L_0x00d2:
            return r6
        L_0x00d3:
            return r7
        L_0x00d4:
            return r8
        L_0x00d5:
            return r9
        L_0x00d6:
            return r10
        L_0x00d7:
            return r11
        L_0x00d8:
            return r12
        L_0x00d9:
            return r13
        L_0x00da:
            return r14
        L_0x00db:
            return r16
        L_0x00dc:
            return r15
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.pm.dex.ArtManagerService.getCompilationFilterTronValue(java.lang.String):int");
    }

    private static void verifyTronLoggingConstants() {
        for (String reason : PackageManagerServiceCompilerMapping.REASON_STRINGS) {
            int value = getCompilationReasonTronValue(reason);
            if (value == 0 || value == 1) {
                throw new IllegalArgumentException("Compilation reason not configured for TRON logging: " + reason);
            }
        }
    }

    private class ArtManagerInternalImpl extends ArtManagerInternal {
        private ArtManagerInternalImpl() {
        }

        public PackageOptimizationInfo getPackageOptimizationInfo(ApplicationInfo info, String abi) {
            String compilationFilter;
            String compilationReason;
            try {
                DexFile.OptimizationInfo optInfo = DexFile.getDexFileOptimizationInfo(info.getBaseCodePath(), VMRuntime.getInstructionSet(abi));
                compilationFilter = optInfo.getStatus();
                compilationReason = optInfo.getReason();
            } catch (FileNotFoundException e) {
                Slog.e(ArtManagerService.TAG, "Could not get optimizations status for " + info.getBaseCodePath(), e);
                compilationFilter = "error";
                compilationReason = "error";
            } catch (IllegalArgumentException e2) {
                Slog.wtf(ArtManagerService.TAG, "Requested optimization status for " + info.getBaseCodePath() + " due to an invalid abi " + abi, e2);
                compilationFilter = "error";
                compilationReason = "error";
            }
            return new PackageOptimizationInfo(ArtManagerService.getCompilationFilterTronValue(compilationFilter), ArtManagerService.getCompilationReasonTronValue(compilationReason));
        }
    }
}
