package com.android.server.pm;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageParser;
import android.os.FileUtils;
import android.os.PowerManager;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.os.WorkSource;
import android.util.Log;
import android.util.Slog;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.util.IndentingPrintWriter;
import com.android.server.pm.CompilerStats;
import com.android.server.pm.Installer;
import com.android.server.pm.dex.ArtManagerService;
import com.android.server.pm.dex.DexManager;
import com.android.server.pm.dex.DexoptOptions;
import com.android.server.pm.dex.PackageDexUsage;
import dalvik.system.DexFile;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

public class PackageDexOptimizer {
    public static final int DEX_OPT_FAILED = -1;
    public static final int DEX_OPT_PERFORMED = 1;
    public static final int DEX_OPT_SKIPPED = 0;
    static final String OAT_DIR_NAME = "oat";
    private static final String TAG = "PackageManager.DexOptimizer";
    private static final long WAKELOCK_TIMEOUT_MS = 660000;
    @GuardedBy({"mInstallLock"})
    private final PowerManager.WakeLock mDexoptWakeLock;
    private final Object mInstallLock;
    @GuardedBy({"mInstallLock"})
    private final Installer mInstaller;
    private volatile boolean mSystemReady;

    PackageDexOptimizer(Installer installer, Object installLock, Context context, String wakeLockTag) {
        this.mInstaller = installer;
        this.mInstallLock = installLock;
        this.mDexoptWakeLock = ((PowerManager) context.getSystemService("power")).newWakeLock(1, wakeLockTag);
    }

    protected PackageDexOptimizer(PackageDexOptimizer from) {
        this.mInstaller = from.mInstaller;
        this.mInstallLock = from.mInstallLock;
        this.mDexoptWakeLock = from.mDexoptWakeLock;
        this.mSystemReady = from.mSystemReady;
    }

    static boolean canOptimizePackage(PackageParser.Package pkg) {
        if ((pkg.applicationInfo.flags & 4) == 0) {
            return false;
        }
        return true;
    }

    /* Debug info: failed to restart local var, previous not found, register: 4 */
    /* access modifiers changed from: package-private */
    public int performDexOpt(PackageParser.Package pkg, String[] instructionSets, CompilerStats.PackageStats packageStats, PackageDexUsage.PackageUseInfo packageUseInfo, DexoptOptions options) {
        int performDexOptLI;
        if (pkg.applicationInfo.uid == -1) {
            throw new IllegalArgumentException("Dexopt for " + pkg.packageName + " has invalid uid.");
        } else if (!canOptimizePackage(pkg)) {
            return 0;
        } else {
            synchronized (this.mInstallLock) {
                long acquireTime = acquireWakeLockLI(pkg.applicationInfo.uid, pkg.packageName);
                try {
                    performDexOptLI = performDexOptLI(pkg, instructionSets, packageStats, packageUseInfo, options);
                } finally {
                    releaseWakeLockLI(acquireTime);
                }
            }
            return performDexOptLI;
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:62:0x0181  */
    /* JADX WARNING: Removed duplicated region for block: B:63:0x0184  */
    /* JADX WARNING: Removed duplicated region for block: B:66:0x0194  */
    /* JADX WARNING: Removed duplicated region for block: B:67:0x01b5  */
    /* JADX WARNING: Removed duplicated region for block: B:70:0x01be  */
    @com.android.internal.annotations.GuardedBy({"mInstallLock"})
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private int performDexOptLI(android.content.pm.PackageParser.Package r39, java.lang.String[] r40, com.android.server.pm.CompilerStats.PackageStats r41, com.android.server.pm.dex.PackageDexUsage.PackageUseInfo r42, com.android.server.pm.dex.DexoptOptions r43) {
        /*
            r38 = this;
            r14 = r38
            r15 = r39
            java.util.ArrayList r13 = r15.usesLibraryInfos
            if (r40 == 0) goto L_0x000b
            r0 = r40
            goto L_0x0011
        L_0x000b:
            android.content.pm.ApplicationInfo r0 = r15.applicationInfo
            java.lang.String[] r0 = com.android.server.pm.InstructionSets.getAppDexInstructionSets((android.content.pm.ApplicationInfo) r0)
        L_0x0011:
            r16 = r0
            java.lang.String[] r12 = com.android.server.pm.InstructionSets.getDexCodeInstructionSets(r16)
            java.util.List r11 = r39.getAllCodePaths()
            android.content.pm.ApplicationInfo r0 = r15.applicationInfo
            int r0 = r0.uid
            int r0 = android.os.UserHandle.getSharedAppGid(r0)
            java.lang.String r10 = "PackageManager.DexOptimizer"
            r9 = -1
            if (r0 != r9) goto L_0x0055
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r2 = "Well this is awkward; package "
            r1.append(r2)
            android.content.pm.ApplicationInfo r2 = r15.applicationInfo
            java.lang.String r2 = r2.name
            r1.append(r2)
            java.lang.String r2 = " had UID "
            r1.append(r2)
            android.content.pm.ApplicationInfo r2 = r15.applicationInfo
            int r2 = r2.uid
            r1.append(r2)
            java.lang.String r1 = r1.toString()
            java.lang.Throwable r2 = new java.lang.Throwable
            r2.<init>()
            android.util.Slog.wtf(r10, r1, r2)
            r0 = 9999(0x270f, float:1.4012E-41)
            r8 = r0
            goto L_0x0056
        L_0x0055:
            r8 = r0
        L_0x0056:
            int r0 = r11.size()
            boolean[] r7 = new boolean[r0]
            android.content.pm.ApplicationInfo r0 = r15.applicationInfo
            int r0 = r0.flags
            r0 = r0 & 4
            r17 = 0
            r18 = 1
            if (r0 == 0) goto L_0x006b
            r0 = r18
            goto L_0x006d
        L_0x006b:
            r0 = r17
        L_0x006d:
            r7[r17] = r0
            r0 = 1
        L_0x0070:
            int r1 = r11.size()
            if (r0 >= r1) goto L_0x008a
            int[] r1 = r15.splitFlags
            int r2 = r0 + -1
            r1 = r1[r2]
            r1 = r1 & 4
            if (r1 == 0) goto L_0x0083
            r1 = r18
            goto L_0x0085
        L_0x0083:
            r1 = r17
        L_0x0085:
            r7[r0] = r1
            int r0 = r0 + 1
            goto L_0x0070
        L_0x008a:
            android.content.pm.ApplicationInfo r0 = r15.applicationInfo
            java.lang.String[] r6 = com.android.server.pm.dex.DexoptUtils.getClassLoaderContexts(r0, r13, r7)
            int r0 = r11.size()
            int r1 = r6.length
            if (r0 == r1) goto L_0x00d4
            android.content.pm.ApplicationInfo r0 = r15.applicationInfo
            java.lang.String[] r0 = r0.getSplitCodePaths()
            java.lang.IllegalStateException r1 = new java.lang.IllegalStateException
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r3 = "Inconsistent information between PackageParser.Package and its ApplicationInfo. pkg.getAllCodePaths="
            r2.append(r3)
            r2.append(r11)
            java.lang.String r3 = " pkg.applicationInfo.getBaseCodePath="
            r2.append(r3)
            android.content.pm.ApplicationInfo r3 = r15.applicationInfo
            java.lang.String r3 = r3.getBaseCodePath()
            r2.append(r3)
            java.lang.String r3 = " pkg.applicationInfo.getSplitCodePaths="
            r2.append(r3)
            if (r0 != 0) goto L_0x00c5
            java.lang.String r3 = "null"
            goto L_0x00c9
        L_0x00c5:
            java.lang.String r3 = java.util.Arrays.toString(r0)
        L_0x00c9:
            r2.append(r3)
            java.lang.String r2 = r2.toString()
            r1.<init>(r2)
            throw r1
        L_0x00d4:
            r0 = 0
            r1 = 0
            r4 = r1
        L_0x00d7:
            int r1 = r11.size()
            if (r4 >= r1) goto L_0x0281
            boolean r1 = r7[r4]
            if (r1 != 0) goto L_0x00e2
            goto L_0x0107
        L_0x00e2:
            r1 = r6[r4]
            if (r1 == 0) goto L_0x0258
            java.lang.Object r1 = r11.get(r4)
            r3 = r1
            java.lang.String r3 = (java.lang.String) r3
            java.lang.String r1 = r43.getSplitName()
            if (r1 == 0) goto L_0x011a
            java.lang.String r1 = r43.getSplitName()
            java.io.File r2 = new java.io.File
            r2.<init>(r3)
            java.lang.String r2 = r2.getName()
            boolean r1 = r1.equals(r2)
            if (r1 != 0) goto L_0x011a
        L_0x0107:
            r34 = r4
            r32 = r6
            r26 = r7
            r33 = r8
            r15 = r9
            r35 = r10
            r36 = r11
            r27 = r12
            r21 = r13
            goto L_0x0241
        L_0x011a:
            if (r4 != 0) goto L_0x011e
            r2 = 0
            goto L_0x0124
        L_0x011e:
            java.lang.String[] r2 = r15.splitNames
            int r5 = r4 + -1
            r2 = r2[r5]
        L_0x0124:
            java.lang.String r2 = android.content.pm.dex.ArtManager.getProfileName(r2)
            r5 = 0
            boolean r19 = r43.isDexoptInstallWithDexMetadata()
            if (r19 == 0) goto L_0x0145
            java.io.File r1 = new java.io.File
            r1.<init>(r3)
            java.io.File r1 = android.content.pm.dex.DexMetadataHelper.findDexMetadataForFile(r1)
            if (r1 != 0) goto L_0x013d
            r19 = 0
            goto L_0x0141
        L_0x013d:
            java.lang.String r19 = r1.getAbsolutePath()
        L_0x0141:
            r5 = r19
            r1 = r5
            goto L_0x0146
        L_0x0145:
            r1 = r5
        L_0x0146:
            boolean r5 = r43.isDexoptAsSharedLibrary()
            if (r5 != 0) goto L_0x0158
            r5 = r42
            boolean r19 = r5.isUsedByOtherApps(r3)
            if (r19 == 0) goto L_0x0155
            goto L_0x015a
        L_0x0155:
            r19 = r17
            goto L_0x015c
        L_0x0158:
            r5 = r42
        L_0x015a:
            r19 = r18
        L_0x015c:
            r20 = r19
            android.content.pm.ApplicationInfo r9 = r15.applicationInfo
            java.lang.String r5 = r43.getCompilerFilter()
            r21 = r13
            r13 = r20
            java.lang.String r5 = r14.getRealCompilerFilter(r9, r5, r13)
            int r9 = r43.getCompilationReason()
            java.lang.String r9 = com.android.server.pm.PackageManagerServiceInjector.maybeAdjustCompilerFilter(r15, r9, r5, r2, r1)
            boolean r5 = r43.isCheckForProfileUpdates()
            if (r5 == 0) goto L_0x0184
            boolean r5 = r14.isProfileUpdated(r15, r8, r2, r9)
            if (r5 == 0) goto L_0x0184
            r5 = r18
            goto L_0x0186
        L_0x0184:
            r5 = r17
        L_0x0186:
            r20 = r5
            r5 = r43
            int r22 = r14.getDexFlags((android.content.pm.PackageParser.Package) r15, (java.lang.String) r9, (com.android.server.pm.dex.DexoptOptions) r5)
            boolean r23 = r43.isDexoptBgCpuset()
            if (r23 == 0) goto L_0x01b5
            r23 = r1
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r24 = r2
            java.lang.String r2 = "dexopt-bg: bg flag is added to pkg = "
            r1.append(r2)
            r1.append(r15)
            java.lang.String r2 = " path = "
            r1.append(r2)
            r1.append(r3)
            java.lang.String r1 = r1.toString()
            android.util.Log.d(r10, r1)
            goto L_0x01b9
        L_0x01b5:
            r23 = r1
            r24 = r2
        L_0x01b9:
            int r2 = r12.length
            r1 = r17
        L_0x01bc:
            if (r1 >= r2) goto L_0x0229
            r25 = r12[r1]
            r26 = r6[r4]
            boolean r27 = r43.isDowngrade()
            int r28 = r43.getCompilationReason()
            r14 = r0
            r0 = r38
            r29 = r1
            r1 = r39
            r30 = r2
            r2 = r3
            r31 = r3
            r3 = r25
            r15 = r4
            r4 = r9
            r32 = r6
            r6 = r26
            r26 = r7
            r7 = r22
            r33 = r8
            r19 = r9
            r34 = r15
            r15 = -1
            r9 = r41
            r35 = r10
            r10 = r27
            r36 = r11
            r11 = r24
            r27 = r12
            r12 = r23
            r37 = r13
            r13 = r28
            r5 = r20
            int r0 = r0.dexOptPath(r1, r2, r3, r4, r5, r6, r7, r8, r9, r10, r11, r12, r13)
            if (r14 == r15) goto L_0x0207
            if (r0 == 0) goto L_0x0207
            r1 = r0
            goto L_0x0208
        L_0x0207:
            r0 = r14
        L_0x0208:
            int r1 = r29 + 1
            r14 = r38
            r15 = r39
            r20 = r5
            r9 = r19
            r7 = r26
            r12 = r27
            r2 = r30
            r3 = r31
            r6 = r32
            r8 = r33
            r4 = r34
            r10 = r35
            r11 = r36
            r13 = r37
            r5 = r43
            goto L_0x01bc
        L_0x0229:
            r14 = r0
            r31 = r3
            r34 = r4
            r32 = r6
            r26 = r7
            r33 = r8
            r19 = r9
            r35 = r10
            r36 = r11
            r27 = r12
            r37 = r13
            r5 = r20
            r15 = -1
        L_0x0241:
            int r4 = r34 + 1
            r14 = r38
            r9 = r15
            r13 = r21
            r7 = r26
            r12 = r27
            r6 = r32
            r8 = r33
            r10 = r35
            r11 = r36
            r15 = r39
            goto L_0x00d7
        L_0x0258:
            r36 = r11
            java.lang.IllegalStateException r1 = new java.lang.IllegalStateException
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r3 = "Inconsistent information in the package structure. A split is marked to contain code but has no dependency listed. Index="
            r2.append(r3)
            r2.append(r4)
            java.lang.String r3 = " path="
            r2.append(r3)
            r3 = r36
            java.lang.Object r5 = r3.get(r4)
            java.lang.String r5 = (java.lang.String) r5
            r2.append(r5)
            java.lang.String r2 = r2.toString()
            r1.<init>(r2)
            throw r1
        L_0x0281:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.pm.PackageDexOptimizer.performDexOptLI(android.content.pm.PackageParser$Package, java.lang.String[], com.android.server.pm.CompilerStats$PackageStats, com.android.server.pm.dex.PackageDexUsage$PackageUseInfo, com.android.server.pm.dex.DexoptOptions):int");
    }

    @GuardedBy({"mInstallLock"})
    private int dexOptPath(PackageParser.Package pkg, String path, String isa, String compilerFilter, boolean profileUpdated, String classLoaderContext, int dexoptFlags, int uid, CompilerStats.PackageStats packageStats, boolean downgrade, String profileName, String dexMetadataPath, int compilationReason) {
        String str;
        PackageParser.Package packageR = pkg;
        String str2 = path;
        String str3 = isa;
        CompilerStats.PackageStats packageStats2 = packageStats;
        String str4 = dexMetadataPath;
        int i = compilationReason;
        int dexoptNeeded = getDexoptNeeded(path, isa, compilerFilter, classLoaderContext, profileUpdated, downgrade);
        if (Math.abs(dexoptNeeded) == 0) {
            return 0;
        }
        String oatDir = createOatDirIfSupported(packageR, str3);
        String compilerFilter2 = PackageManagerServiceInjector.maybeAdjustCompilerFilter(packageR, i, compilerFilter, profileName, str4);
        Log.i(TAG, "Running dexopt (dexoptNeeded=" + dexoptNeeded + ") on: " + str2 + " pkg=" + packageR.applicationInfo.packageName + " isa=" + str3 + " dexoptFlags=" + printDexoptFlags(dexoptFlags) + " targetFilter=" + compilerFilter2 + " oatDir=" + oatDir + " classLoaderContext=" + classLoaderContext);
        try {
            long startTime = System.currentTimeMillis();
            Installer installer = this.mInstaller;
            String str5 = packageR.packageName;
            String str6 = packageR.volumeUuid;
            String str7 = packageR.applicationInfo.seInfo;
            int i2 = packageR.applicationInfo.targetSdkVersion;
            String augmentedReasonName = getAugmentedReasonName(i, str4 != null);
            str = TAG;
            CompilerStats.PackageStats packageStats3 = packageStats2;
            String str8 = str5;
            String str9 = str2;
            try {
                installer.dexopt(path, uid, str8, isa, dexoptNeeded, oatDir, dexoptFlags, compilerFilter2, str6, classLoaderContext, str7, false, i2, profileName, dexMetadataPath, augmentedReasonName);
                if (packageStats3 != null) {
                    packageStats3.setCompileTime(str9, (long) ((int) (System.currentTimeMillis() - startTime)));
                }
                return 1;
            } catch (Installer.InstallerException e) {
                e = e;
                Slog.w(str, "Failed to dexopt", e);
                return -1;
            }
        } catch (Installer.InstallerException e2) {
            e = e2;
            str = TAG;
            CompilerStats.PackageStats packageStats4 = packageStats2;
            String str10 = str2;
            Slog.w(str, "Failed to dexopt", e);
            return -1;
        }
    }

    private String getAugmentedReasonName(int compilationReason, boolean useDexMetadata) {
        String annotation = useDexMetadata ? ArtManagerService.DEXOPT_REASON_WITH_DEX_METADATA_ANNOTATION : "";
        return PackageManagerServiceCompilerMapping.getReasonName(compilationReason) + annotation;
    }

    /* Debug info: failed to restart local var, previous not found, register: 4 */
    public int dexOptSecondaryDexPath(ApplicationInfo info, String path, PackageDexUsage.DexUseInfo dexUseInfo, DexoptOptions options) {
        int dexOptSecondaryDexPathLI;
        if (info.uid != -1) {
            synchronized (this.mInstallLock) {
                long acquireTime = acquireWakeLockLI(info.uid, info.packageName);
                try {
                    dexOptSecondaryDexPathLI = dexOptSecondaryDexPathLI(info, path, dexUseInfo, options);
                } finally {
                    releaseWakeLockLI(acquireTime);
                }
            }
            return dexOptSecondaryDexPathLI;
        }
        throw new IllegalArgumentException("Dexopt for path " + path + " has invalid uid.");
    }

    @GuardedBy({"mInstallLock"})
    private long acquireWakeLockLI(int uid) {
        if (!this.mSystemReady) {
            return -1;
        }
        this.mDexoptWakeLock.setWorkSource(new WorkSource(uid));
        this.mDexoptWakeLock.acquire(WAKELOCK_TIMEOUT_MS);
        return SystemClock.elapsedRealtime();
    }

    private long acquireWakeLockLI(int uid, String pkgName) {
        if (!this.mSystemReady) {
            return -1;
        }
        this.mDexoptWakeLock.setWorkSource(new WorkSource(uid, pkgName));
        this.mDexoptWakeLock.acquire(WAKELOCK_TIMEOUT_MS);
        return SystemClock.elapsedRealtime();
    }

    @GuardedBy({"mInstallLock"})
    private void releaseWakeLockLI(long acquireTime) {
        if (acquireTime >= 0) {
            try {
                if (this.mDexoptWakeLock.isHeld()) {
                    this.mDexoptWakeLock.release();
                }
                long duration = SystemClock.elapsedRealtime() - acquireTime;
                if (duration >= WAKELOCK_TIMEOUT_MS) {
                    Slog.wtf(TAG, "WakeLock " + this.mDexoptWakeLock.getTag() + " time out. Operation took " + duration + " ms. Thread: " + Thread.currentThread().getName());
                }
            } catch (Exception e) {
                Slog.wtf(TAG, "Error while releasing " + this.mDexoptWakeLock.getTag() + " lock", e);
            }
        }
    }

    @GuardedBy({"mInstallLock"})
    private int dexOptSecondaryDexPathLI(ApplicationInfo info, String path, PackageDexUsage.DexUseInfo dexUseInfo, DexoptOptions options) {
        int dexoptFlags;
        String compilerFilter;
        String classLoaderContext;
        String str;
        String str2;
        ApplicationInfo applicationInfo = info;
        String str3 = path;
        if (options.isDexoptOnlySharedDex() && !dexUseInfo.isUsedByOtherApps()) {
            return 0;
        }
        String compilerFilter2 = getRealCompilerFilter(applicationInfo, options.getCompilerFilter(), dexUseInfo.isUsedByOtherApps());
        int dexoptFlags2 = getDexFlags(applicationInfo, compilerFilter2, options) | 32;
        String str4 = applicationInfo.deviceProtectedDataDir;
        String str5 = TAG;
        if (str4 == null || !FileUtils.contains(applicationInfo.deviceProtectedDataDir, str3)) {
            if (applicationInfo.credentialProtectedDataDir == null) {
                str2 = str5;
            } else if (FileUtils.contains(applicationInfo.credentialProtectedDataDir, str3)) {
                dexoptFlags = dexoptFlags2 | 128;
            } else {
                str2 = str5;
            }
            Slog.e(str2, "Could not infer CE/DE storage for package " + applicationInfo.packageName);
            return -1;
        }
        dexoptFlags = dexoptFlags2 | 256;
        if (dexUseInfo.isUnknownClassLoaderContext() || dexUseInfo.isVariableClassLoaderContext()) {
            compilerFilter = "extract";
            classLoaderContext = null;
        } else {
            compilerFilter = compilerFilter2;
            classLoaderContext = dexUseInfo.getClassLoaderContext();
        }
        int reason = options.getCompilationReason();
        Log.d(str5, "Running dexopt on: " + str3 + " pkg=" + applicationInfo.packageName + " isa=" + dexUseInfo.getLoaderIsas() + " reason=" + PackageManagerServiceCompilerMapping.getReasonName(reason) + " dexoptFlags=" + printDexoptFlags(dexoptFlags) + " target-filter=" + compilerFilter + " class-loader-context=" + classLoaderContext);
        try {
            for (String isa : dexUseInfo.getLoaderIsas()) {
                String classLoaderContext2 = classLoaderContext;
                String compilerFilter3 = compilerFilter;
                int dexoptFlags3 = dexoptFlags;
                str = str5;
                try {
                    this.mInstaller.dexopt(path, applicationInfo.uid, applicationInfo.packageName, isa, 0, (String) null, dexoptFlags, compilerFilter, applicationInfo.volumeUuid, classLoaderContext2, applicationInfo.seInfo, options.isDowngrade(), applicationInfo.targetSdkVersion, (String) null, (String) null, PackageManagerServiceCompilerMapping.getReasonName(reason));
                    String str6 = path;
                    DexoptOptions dexoptOptions = options;
                    classLoaderContext = classLoaderContext2;
                    compilerFilter = compilerFilter3;
                    dexoptFlags = dexoptFlags3;
                    str5 = str;
                } catch (Installer.InstallerException e) {
                    e = e;
                    Slog.w(str, "Failed to dexopt", e);
                    return -1;
                }
            }
            return 1;
        } catch (Installer.InstallerException e2) {
            e = e2;
            String str7 = classLoaderContext;
            String str8 = compilerFilter;
            int i = dexoptFlags;
            str = str5;
            Slog.w(str, "Failed to dexopt", e);
            return -1;
        }
    }

    /* access modifiers changed from: protected */
    public int adjustDexoptNeeded(int dexoptNeeded) {
        return dexoptNeeded;
    }

    /* access modifiers changed from: protected */
    public int adjustDexoptFlags(int dexoptFlags) {
        return dexoptFlags;
    }

    /* access modifiers changed from: package-private */
    public void dumpDexoptState(IndentingPrintWriter pw, PackageParser.Package pkg, PackageDexUsage.PackageUseInfo useInfo) {
        IndentingPrintWriter indentingPrintWriter = pw;
        PackageDexUsage.PackageUseInfo packageUseInfo = useInfo;
        String[] dexCodeInstructionSets = InstructionSets.getDexCodeInstructionSets(InstructionSets.getAppDexInstructionSets(pkg.applicationInfo));
        for (String path : pkg.getAllCodePathsExcludingResourceOnly()) {
            indentingPrintWriter.println("path: " + path);
            pw.increaseIndent();
            int length = dexCodeInstructionSets.length;
            for (int i = 0; i < length; i++) {
                String isa = dexCodeInstructionSets[i];
                try {
                    DexFile.OptimizationInfo info = DexFile.getDexFileOptimizationInfo(path, isa);
                    indentingPrintWriter.println(isa + ": [status=" + info.getStatus() + "] [reason=" + info.getReason() + "]");
                } catch (IOException ioe) {
                    indentingPrintWriter.println(isa + ": [Exception]: " + ioe.getMessage());
                }
            }
            if (packageUseInfo.isUsedByOtherApps(path)) {
                indentingPrintWriter.println("used by other apps: " + packageUseInfo.getLoadingPackages(path));
            }
            Map<String, PackageDexUsage.DexUseInfo> dexUseInfoMap = useInfo.getDexUseInfoMap();
            if (!dexUseInfoMap.isEmpty()) {
                indentingPrintWriter.println("known secondary dex files:");
                pw.increaseIndent();
                for (Map.Entry<String, PackageDexUsage.DexUseInfo> e : dexUseInfoMap.entrySet()) {
                    PackageDexUsage.DexUseInfo dexUseInfo = e.getValue();
                    indentingPrintWriter.println(e.getKey());
                    pw.increaseIndent();
                    indentingPrintWriter.println("class loader context: " + dexUseInfo.getClassLoaderContext());
                    if (dexUseInfo.isUsedByOtherApps()) {
                        indentingPrintWriter.println("used by other apps: " + dexUseInfo.getLoadingPackages());
                    }
                    pw.decreaseIndent();
                }
                pw.decreaseIndent();
            }
            pw.decreaseIndent();
        }
    }

    private String getRealCompilerFilter(ApplicationInfo info, String targetCompilerFilter, boolean isUsedByOtherApps) {
        if (info.isEmbeddedDexUsed()) {
            return "verify";
        }
        if (info.isPrivilegedApp() && DexManager.isPackageSelectedToRunOob(info.packageName)) {
            return "verify";
        }
        if (((info.flags & 16384) == 0 && (info.flags & 2) == 0) ? false : true) {
            return DexFile.getSafeModeCompilerFilter(targetCompilerFilter);
        }
        if (!DexFile.isProfileGuidedCompilerFilter(targetCompilerFilter) || !isUsedByOtherApps) {
            return targetCompilerFilter;
        }
        return PackageManagerServiceCompilerMapping.getCompilerFilterForReason(6);
    }

    private int getDexFlags(PackageParser.Package pkg, String compilerFilter, DexoptOptions options) {
        return getDexFlags(pkg.applicationInfo, compilerFilter, options);
    }

    private boolean isAppImageEnabled() {
        return SystemProperties.get("dalvik.vm.appimageformat", "").length() > 0;
    }

    private int getDexFlags(ApplicationInfo info, String compilerFilter, DexoptOptions options) {
        int hiddenApiFlag;
        boolean generateAppImage = true;
        int i = 0;
        boolean debuggable = (info.flags & 2) != 0;
        boolean isProfileGuidedFilter = DexFile.isProfileGuidedCompilerFilter(compilerFilter);
        boolean isPublic = !isProfileGuidedFilter || options.isDexoptInstallWithDexMetadata();
        int profileFlag = isProfileGuidedFilter ? 16 : 0;
        if (info.getHiddenApiEnforcementPolicy() == 0) {
            hiddenApiFlag = 0;
        } else {
            hiddenApiFlag = 1024;
        }
        int compilationReason = options.getCompilationReason();
        boolean generateCompactDex = true;
        int i2 = 2;
        if (compilationReason == 0 || compilationReason == 1 || compilationReason == 2) {
            generateCompactDex = false;
        }
        if (!isProfileGuidedFilter || ((info.splitDependencies != null && info.requestsIsolatedSplitLoading()) || !isAppImageEnabled())) {
            generateAppImage = false;
        }
        if (!isPublic) {
            i2 = 0;
        }
        int i3 = i2 | (debuggable ? 4 : 0) | profileFlag | (options.isBootComplete() ? 8 : 0) | (options.isDexoptIdleBackgroundJob() ? 512 : 0) | (options.isDexoptBgCpuset() ? 16384 : 0) | (generateCompactDex ? 2048 : 0);
        if (generateAppImage) {
            i = 4096;
        }
        return adjustDexoptFlags(i | i3 | hiddenApiFlag);
    }

    private int getDexoptNeeded(String path, String isa, String compilerFilter, String classLoaderContext, boolean newProfile, boolean downgrade) {
        try {
            return adjustDexoptNeeded(DexFile.getDexOptNeeded(path, isa, compilerFilter, classLoaderContext, newProfile, downgrade));
        } catch (IOException ioe) {
            Slog.w(TAG, "IOException reading apk: " + path, ioe);
            return -1;
        }
    }

    private boolean isProfileUpdated(PackageParser.Package pkg, int uid, String profileName, String compilerFilter) {
        if (!DexFile.isProfileGuidedCompilerFilter(compilerFilter)) {
            return false;
        }
        try {
            return this.mInstaller.mergeProfiles(uid, pkg.packageName, profileName);
        } catch (Installer.InstallerException e) {
            Slog.w(TAG, "Failed to merge profiles", e);
            return false;
        }
    }

    private String createOatDirIfSupported(PackageParser.Package pkg, String dexInstructionSet) {
        if (!pkg.canHaveOatDir()) {
            return null;
        }
        File codePath = new File(pkg.codePath);
        if (!codePath.isDirectory()) {
            return null;
        }
        File oatDir = getOatDir(codePath);
        try {
            this.mInstaller.createOatDir(oatDir.getAbsolutePath(), dexInstructionSet);
            return oatDir.getAbsolutePath();
        } catch (Installer.InstallerException e) {
            Slog.w(TAG, "Failed to create oat dir", e);
            return null;
        }
    }

    static File getOatDir(File codePath) {
        return new File(codePath, OAT_DIR_NAME);
    }

    /* access modifiers changed from: package-private */
    public void systemReady() {
        this.mSystemReady = true;
    }

    private String printDexoptFlags(int flags) {
        ArrayList<String> flagsList = new ArrayList<>();
        if ((flags & 8) == 8) {
            flagsList.add("boot_complete");
        }
        if ((flags & 4) == 4) {
            flagsList.add("debuggable");
        }
        if ((flags & 16) == 16) {
            flagsList.add("profile_guided");
        }
        if ((flags & 2) == 2) {
            flagsList.add("public");
        }
        if ((flags & 32) == 32) {
            flagsList.add("secondary");
        }
        if ((flags & 64) == 64) {
            flagsList.add("force");
        }
        if ((flags & 128) == 128) {
            flagsList.add("storage_ce");
        }
        if ((flags & 256) == 256) {
            flagsList.add("storage_de");
        }
        if ((flags & 512) == 512) {
            flagsList.add("idle_background_job");
        }
        if ((flags & 16384) == 16384) {
            flagsList.add("cpuset_bg");
        }
        if ((flags & 1024) == 1024) {
            flagsList.add("enable_hidden_api_checks");
        }
        return String.join(",", flagsList);
    }

    public static class ForcedUpdatePackageDexOptimizer extends PackageDexOptimizer {
        public ForcedUpdatePackageDexOptimizer(Installer installer, Object installLock, Context context, String wakeLockTag) {
            super(installer, installLock, context, wakeLockTag);
        }

        public ForcedUpdatePackageDexOptimizer(PackageDexOptimizer from) {
            super(from);
        }

        /* access modifiers changed from: protected */
        public int adjustDexoptNeeded(int dexoptNeeded) {
            if (dexoptNeeded == 0) {
                return -3;
            }
            return dexoptNeeded;
        }

        /* access modifiers changed from: protected */
        public int adjustDexoptFlags(int flags) {
            return flags | 64;
        }
    }
}
