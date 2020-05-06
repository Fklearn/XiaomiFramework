package com.android.server.pm.dex;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageManager;
import android.content.pm.PackageInfo;
import android.os.RemoteException;
import android.os.SystemProperties;
import android.provider.DeviceConfig;
import android.util.Log;
import android.util.Slog;
import android.util.jar.StrictJarFile;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.annotations.VisibleForTesting;
import com.android.server.pm.Installer;
import com.android.server.pm.InstructionSets;
import com.android.server.pm.PackageDexOptimizer;
import com.android.server.pm.PackageManagerServiceInjector;
import com.android.server.pm.PackageManagerServiceUtils;
import com.android.server.pm.dex.PackageDexUsage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;

public class DexManager {
    private static final boolean DEBUG = Log.isLoggable(TAG, 3);
    private static final PackageDexUsage.PackageUseInfo DEFAULT_USE_INFO = new PackageDexUsage.PackageUseInfo();
    /* access modifiers changed from: private */
    public static int DEX_SEARCH_FOUND_PRIMARY = 1;
    /* access modifiers changed from: private */
    public static int DEX_SEARCH_FOUND_SECONDARY = 3;
    /* access modifiers changed from: private */
    public static int DEX_SEARCH_FOUND_SPLIT = 2;
    /* access modifiers changed from: private */
    public static int DEX_SEARCH_NOT_FOUND = 0;
    private static final String PRIV_APPS_OOB_ENABLED = "priv_apps_oob_enabled";
    private static final String PRIV_APPS_OOB_WHITELIST = "priv_apps_oob_whitelist";
    private static final String PROPERTY_NAME_PM_DEXOPT_PRIV_APPS_OOB = "pm.dexopt.priv-apps-oob";
    private static final String PROPERTY_NAME_PM_DEXOPT_PRIV_APPS_OOB_LIST = "pm.dexopt.priv-apps-oob-list";
    private static final String TAG = "DexManager";
    private final Context mContext;
    private final DynamicCodeLogger mDynamicCodeLogger;
    private final Object mInstallLock;
    @GuardedBy({"mInstallLock"})
    private final Installer mInstaller;
    @GuardedBy({"mPackageCodeLocationsCache"})
    private final Map<String, PackageCodeLocations> mPackageCodeLocationsCache = new HashMap();
    private final PackageDexOptimizer mPackageDexOptimizer;
    private final PackageDexUsage mPackageDexUsage = new PackageDexUsage();
    private final IPackageManager mPackageManager;

    public DexManager(Context context, IPackageManager pms, PackageDexOptimizer pdo, Installer installer, Object installLock) {
        this.mContext = context;
        this.mPackageManager = pms;
        this.mPackageDexOptimizer = pdo;
        this.mInstaller = installer;
        this.mInstallLock = installLock;
        this.mDynamicCodeLogger = new DynamicCodeLogger(pms, installer);
    }

    public DynamicCodeLogger getDynamicCodeLogger() {
        return this.mDynamicCodeLogger;
    }

    public void notifyDexLoad(ApplicationInfo loadingAppInfo, List<String> classLoadersNames, List<String> classPaths, String loaderIsa, int loaderUserId) {
        notifyDexLoadWithStatus(loadingAppInfo, classLoadersNames, classPaths, loaderIsa, loaderUserId, (int[]) null);
    }

    public void notifyDexLoadWithStatus(ApplicationInfo loadingAppInfo, List<String> classLoadersNames, List<String> classPaths, String loaderIsa, int loaderUserId, int[] status) {
        try {
            notifyDexLoadInternalWithStatus(loadingAppInfo, classLoadersNames, classPaths, loaderIsa, loaderUserId, status);
        } catch (Exception e) {
            Slog.w(TAG, "Exception while notifying dex load for package " + loadingAppInfo.packageName, e);
        }
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public void notifyDexLoadInternal(ApplicationInfo loadingAppInfo, List<String> classLoaderNames, List<String> classPaths, String loaderIsa, int loaderUserId) {
        notifyDexLoadInternalWithStatus(loadingAppInfo, classLoaderNames, classPaths, loaderIsa, loaderUserId, (int[]) null);
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public void notifyDexLoadInternalWithStatus(ApplicationInfo loadingAppInfo, List<String> classLoaderNames, List<String> classPaths, String loaderIsa, int loaderUserId, int[] status) {
        String[] dexPathsToRegister;
        int i;
        int i2;
        String firstClassPath;
        ApplicationInfo applicationInfo = loadingAppInfo;
        List<String> list = classPaths;
        int i3 = loaderUserId;
        if (classLoaderNames.size() != classPaths.size()) {
            Slog.wtf(TAG, "Bad call to noitfyDexLoad: args have different size");
        } else if (classLoaderNames.isEmpty()) {
            Slog.wtf(TAG, "Bad call to notifyDexLoad: class loaders list is empty");
        } else if (!PackageManagerServiceUtils.checkISA(loaderIsa)) {
            Slog.w(TAG, "Loading dex files " + list + " in unsupported ISA: " + loaderIsa + "?");
        } else {
            String str = loaderIsa;
            boolean z = false;
            String firstClassPath2 = list.get(0);
            if (firstClassPath2 != null) {
                String[] dexPathsToRegister2 = firstClassPath2.split(File.pathSeparator);
                String[] classLoaderContexts = DexoptUtils.processContextForDexLoad(classLoaderNames, classPaths);
                if (classLoaderContexts != null) {
                    List<String> list2 = classLoaderNames;
                } else if (DEBUG) {
                    Slog.i(TAG, applicationInfo.packageName + " uses unsupported class loader in " + classLoaderNames);
                } else {
                    List<String> list3 = classLoaderNames;
                }
                int length = dexPathsToRegister2.length;
                int dexPathIndex = 0;
                int i4 = 0;
                while (i4 < length) {
                    String dexPath = dexPathsToRegister2[i4];
                    DexSearchResult searchResult = getDexPackage(applicationInfo, dexPath, i3);
                    if (DEBUG) {
                        Slog.i(TAG, applicationInfo.packageName + " loads from " + searchResult + " : " + i3 + " : " + dexPath);
                    }
                    if (searchResult.mOutcome != DEX_SEARCH_NOT_FOUND) {
                        boolean isUsedByOtherApps = !applicationInfo.packageName.equals(searchResult.mOwningPackageName);
                        boolean primaryOrSplit = (searchResult.mOutcome == DEX_SEARCH_FOUND_PRIMARY || searchResult.mOutcome == DEX_SEARCH_FOUND_SPLIT) ? true : z;
                        if (!primaryOrSplit || isUsedByOtherApps) {
                            if (!primaryOrSplit) {
                                this.mDynamicCodeLogger.recordDex(i3, dexPath, searchResult.mOwningPackageName, applicationInfo.packageName);
                            }
                            if (classLoaderContexts != null) {
                                DexSearchResult dexSearchResult = searchResult;
                                String dexPath2 = dexPath;
                                i2 = i4;
                                i = length;
                                dexPathsToRegister = dexPathsToRegister2;
                                firstClassPath = firstClassPath2;
                                if (this.mPackageDexUsage.record(searchResult.mOwningPackageName, dexPath, loaderUserId, loaderIsa, isUsedByOtherApps, primaryOrSplit, applicationInfo.packageName, classLoaderContexts[dexPathIndex])) {
                                    this.mPackageDexUsage.maybeWriteAsync();
                                }
                                if (!(status == null || Math.abs(status[dexPathIndex]) == 0)) {
                                    PackageDexUsage.PackageUseInfo pkgUseInfo = this.mPackageDexUsage.getPackageUseInfo(applicationInfo.packageName);
                                    if (pkgUseInfo != null) {
                                        PackageManagerServiceInjector.processTopAppLoadSecondaryDexReason(this.mPackageDexOptimizer, applicationInfo, pkgUseInfo.getDexUseInfoMap().get(dexPath2), dexPath2, 7);
                                    } else {
                                        Slog.d(TAG, "Oops, can't get pkgUseInfo with " + dexPath2 + ", skip dexopt on it.");
                                    }
                                }
                            } else {
                                String str2 = dexPath;
                                i2 = i4;
                                i = length;
                                dexPathsToRegister = dexPathsToRegister2;
                                firstClassPath = firstClassPath2;
                            }
                        } else {
                            i2 = i4;
                            i = length;
                            dexPathsToRegister = dexPathsToRegister2;
                            firstClassPath = firstClassPath2;
                            i4 = i2 + 1;
                            List<String> list4 = classLoaderNames;
                            i3 = loaderUserId;
                            firstClassPath2 = firstClassPath;
                            length = i;
                            dexPathsToRegister2 = dexPathsToRegister;
                            z = false;
                            List<String> list5 = classPaths;
                        }
                    } else {
                        String dexPath3 = dexPath;
                        i2 = i4;
                        i = length;
                        dexPathsToRegister = dexPathsToRegister2;
                        firstClassPath = firstClassPath2;
                        if (DEBUG) {
                            Slog.i(TAG, "Could not find owning package for dex file: " + dexPath3);
                        }
                    }
                    dexPathIndex++;
                    i4 = i2 + 1;
                    List<String> list42 = classLoaderNames;
                    i3 = loaderUserId;
                    firstClassPath2 = firstClassPath;
                    length = i;
                    dexPathsToRegister2 = dexPathsToRegister;
                    z = false;
                    List<String> list52 = classPaths;
                }
            }
        }
    }

    public void load(Map<Integer, List<PackageInfo>> existingPackages) {
        try {
            loadInternal(existingPackages);
        } catch (Exception e) {
            this.mPackageDexUsage.clear();
            this.mDynamicCodeLogger.clear();
            Slog.w(TAG, "Exception while loading. Starting with a fresh state.", e);
        }
    }

    public void notifyPackageInstalled(PackageInfo pi, int userId) {
        if (userId != -1) {
            cachePackageInfo(pi, userId);
            return;
        }
        throw new IllegalArgumentException("notifyPackageInstalled called with USER_ALL");
    }

    public void notifyPackageUpdated(String packageName, String baseCodePath, String[] splitCodePaths) {
        cachePackageCodeLocation(packageName, baseCodePath, splitCodePaths, (String[]) null, -1);
        if (this.mPackageDexUsage.clearUsedByOtherApps(packageName)) {
            this.mPackageDexUsage.maybeWriteAsync();
        }
    }

    public void notifyPackageDataDestroyed(String packageName, int userId) {
        if (userId == -1) {
            if (this.mPackageDexUsage.removePackage(packageName)) {
                this.mPackageDexUsage.maybeWriteAsync();
            }
            this.mDynamicCodeLogger.removePackage(packageName);
            return;
        }
        if (this.mPackageDexUsage.removeUserPackage(packageName, userId)) {
            this.mPackageDexUsage.maybeWriteAsync();
        }
        this.mDynamicCodeLogger.removeUserPackage(packageName, userId);
    }

    private void cachePackageInfo(PackageInfo pi, int userId) {
        ApplicationInfo ai = pi.applicationInfo;
        cachePackageCodeLocation(pi.packageName, ai.sourceDir, ai.splitSourceDirs, new String[]{ai.dataDir, ai.deviceProtectedDataDir, ai.credentialProtectedDataDir}, userId);
    }

    private void cachePackageCodeLocation(String packageName, String baseCodePath, String[] splitCodePaths, String[] dataDirs, int userId) {
        synchronized (this.mPackageCodeLocationsCache) {
            PackageCodeLocations pcl = (PackageCodeLocations) putIfAbsent(this.mPackageCodeLocationsCache, packageName, new PackageCodeLocations(packageName, baseCodePath, splitCodePaths));
            pcl.updateCodeLocation(baseCodePath, splitCodePaths);
            if (dataDirs != null) {
                for (String dataDir : dataDirs) {
                    if (dataDir != null) {
                        pcl.mergeAppDataDirs(dataDir, userId);
                    }
                }
            }
        }
    }

    private void loadInternal(Map<Integer, List<PackageInfo>> existingPackages) {
        HashMap hashMap = new HashMap();
        HashMap hashMap2 = new HashMap();
        for (Map.Entry<Integer, List<PackageInfo>> entry : existingPackages.entrySet()) {
            int userId = entry.getKey().intValue();
            for (PackageInfo pi : entry.getValue()) {
                cachePackageInfo(pi, userId);
                ((Set) putIfAbsent(hashMap, pi.packageName, new HashSet())).add(Integer.valueOf(userId));
                Set<String> codePaths = (Set) putIfAbsent(hashMap2, pi.packageName, new HashSet());
                codePaths.add(pi.applicationInfo.sourceDir);
                if (pi.applicationInfo.splitSourceDirs != null) {
                    Collections.addAll(codePaths, pi.applicationInfo.splitSourceDirs);
                }
            }
        }
        try {
            this.mPackageDexUsage.read();
            this.mPackageDexUsage.syncData(hashMap, hashMap2);
        } catch (Exception e) {
            this.mPackageDexUsage.clear();
            Slog.w(TAG, "Exception while loading package dex usage. Starting with a fresh state.", e);
        }
        try {
            this.mDynamicCodeLogger.readAndSync(hashMap);
        } catch (Exception e2) {
            this.mDynamicCodeLogger.clear();
            Slog.w(TAG, "Exception while loading package dynamic code usage. Starting with a fresh state.", e2);
        }
    }

    public PackageDexUsage.PackageUseInfo getPackageUseInfoOrDefault(String packageName) {
        PackageDexUsage.PackageUseInfo useInfo = this.mPackageDexUsage.getPackageUseInfo(packageName);
        return useInfo == null ? DEFAULT_USE_INFO : useInfo;
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public boolean hasInfoOnPackage(String packageName) {
        return this.mPackageDexUsage.getPackageUseInfo(packageName) != null;
    }

    public boolean dexoptSecondaryDex(DexoptOptions options) {
        PackageDexOptimizer pdo;
        if (options.isForce()) {
            pdo = new PackageDexOptimizer.ForcedUpdatePackageDexOptimizer(this.mPackageDexOptimizer);
        } else {
            pdo = this.mPackageDexOptimizer;
        }
        String packageName = options.getPackageName();
        PackageDexUsage.PackageUseInfo useInfo = getPackageUseInfoOrDefault(packageName);
        if (useInfo.getDexUseInfoMap().isEmpty()) {
            if (DEBUG) {
                Slog.d(TAG, "No secondary dex use for package:" + packageName);
            }
            return true;
        }
        boolean success = true;
        for (Map.Entry<String, PackageDexUsage.DexUseInfo> entry : useInfo.getDexUseInfoMap().entrySet()) {
            String dexPath = entry.getKey();
            PackageDexUsage.DexUseInfo dexUseInfo = entry.getValue();
            try {
                boolean z = false;
                PackageInfo pkg = this.mPackageManager.getPackageInfo(packageName, 0, dexUseInfo.getOwnerUserId());
                if (pkg == null) {
                    Slog.d(TAG, "Could not find package when compiling secondary dex " + packageName + " for user " + dexUseInfo.getOwnerUserId());
                    this.mPackageDexUsage.removeUserPackage(packageName, dexUseInfo.getOwnerUserId());
                } else {
                    int result = pdo.dexOptSecondaryDexPath(pkg.applicationInfo, dexPath, dexUseInfo, options);
                    if (success && result != -1) {
                        z = true;
                    }
                    success = z;
                }
            } catch (RemoteException e) {
                throw new AssertionError(e);
            }
        }
        return success;
    }

    /* JADX WARNING: Removed duplicated region for block: B:49:0x012e  */
    /* JADX WARNING: Removed duplicated region for block: B:70:0x003b A[SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void reconcileSecondaryDexFiles(java.lang.String r24) {
        /*
            r23 = this;
            r1 = r23
            r9 = r24
            com.android.server.pm.dex.PackageDexUsage$PackageUseInfo r10 = r23.getPackageUseInfoOrDefault(r24)
            java.util.Map r0 = r10.getDexUseInfoMap()
            boolean r0 = r0.isEmpty()
            if (r0 == 0) goto L_0x002d
            boolean r0 = DEBUG
            if (r0 == 0) goto L_0x002c
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r2 = "No secondary dex use for package:"
            r0.append(r2)
            r0.append(r9)
            java.lang.String r0 = r0.toString()
            java.lang.String r2 = "DexManager"
            android.util.Slog.d(r2, r0)
        L_0x002c:
            return
        L_0x002d:
            r0 = 0
            java.util.Map r2 = r10.getDexUseInfoMap()
            java.util.Set r2 = r2.entrySet()
            java.util.Iterator r11 = r2.iterator()
            r12 = r0
        L_0x003b:
            boolean r0 = r11.hasNext()
            if (r0 == 0) goto L_0x017f
            java.lang.Object r0 = r11.next()
            r13 = r0
            java.util.Map$Entry r13 = (java.util.Map.Entry) r13
            java.lang.Object r0 = r13.getKey()
            r14 = r0
            java.lang.String r14 = (java.lang.String) r14
            java.lang.Object r0 = r13.getValue()
            r15 = r0
            com.android.server.pm.dex.PackageDexUsage$DexUseInfo r15 = (com.android.server.pm.dex.PackageDexUsage.DexUseInfo) r15
            r2 = 0
            r8 = 0
            android.content.pm.IPackageManager r0 = r1.mPackageManager     // Catch:{ RemoteException -> 0x0065 }
            int r3 = r15.getOwnerUserId()     // Catch:{ RemoteException -> 0x0065 }
            android.content.pm.PackageInfo r0 = r0.getPackageInfo(r9, r8, r3)     // Catch:{ RemoteException -> 0x0065 }
            r2 = r0
            r7 = r2
            goto L_0x0067
        L_0x0065:
            r0 = move-exception
            r7 = r2
        L_0x0067:
            r16 = 1
            if (r7 != 0) goto L_0x00a1
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r2 = "Could not find package when compiling secondary dex "
            r0.append(r2)
            r0.append(r9)
            java.lang.String r2 = " for user "
            r0.append(r2)
            int r2 = r15.getOwnerUserId()
            r0.append(r2)
            java.lang.String r0 = r0.toString()
            java.lang.String r2 = "DexManager"
            android.util.Slog.d(r2, r0)
            com.android.server.pm.dex.PackageDexUsage r0 = r1.mPackageDexUsage
            int r2 = r15.getOwnerUserId()
            boolean r0 = r0.removeUserPackage(r9, r2)
            if (r0 != 0) goto L_0x009e
            if (r12 == 0) goto L_0x009c
            goto L_0x009e
        L_0x009c:
            r16 = r8
        L_0x009e:
            r12 = r16
            goto L_0x003b
        L_0x00a1:
            android.content.pm.ApplicationInfo r5 = r7.applicationInfo
            r0 = 0
            java.lang.String r2 = r5.deviceProtectedDataDir
            if (r2 == 0) goto L_0x00b5
            java.lang.String r2 = r5.deviceProtectedDataDir
            boolean r2 = android.os.FileUtils.contains(r2, r14)
            if (r2 == 0) goto L_0x00b5
            r0 = r0 | 1
            r17 = r0
            goto L_0x00c5
        L_0x00b5:
            java.lang.String r2 = r5.credentialProtectedDataDir
            if (r2 == 0) goto L_0x014e
            java.lang.String r2 = r5.credentialProtectedDataDir
            boolean r2 = android.os.FileUtils.contains(r2, r14)
            if (r2 == 0) goto L_0x0147
            r0 = r0 | 2
            r17 = r0
        L_0x00c5:
            r18 = 1
            java.lang.Object r4 = r1.mInstallLock
            monitor-enter(r4)
            java.util.Set r0 = r15.getLoaderIsas()     // Catch:{ InstallerException -> 0x0100, all -> 0x00f8 }
            java.lang.String[] r2 = new java.lang.String[r8]     // Catch:{ InstallerException -> 0x0100, all -> 0x00f8 }
            java.lang.Object[] r0 = r0.toArray(r2)     // Catch:{ InstallerException -> 0x0100, all -> 0x00f8 }
            r6 = r0
            java.lang.String[] r6 = (java.lang.String[]) r6     // Catch:{ InstallerException -> 0x0100, all -> 0x00f8 }
            com.android.server.pm.Installer r2 = r1.mInstaller     // Catch:{ InstallerException -> 0x0100, all -> 0x00f8 }
            int r0 = r5.uid     // Catch:{ InstallerException -> 0x0100, all -> 0x00f8 }
            java.lang.String r3 = r5.volumeUuid     // Catch:{ InstallerException -> 0x0100, all -> 0x00f8 }
            r19 = r3
            r3 = r14
            r20 = r4
            r4 = r24
            r21 = r5
            r5 = r0
            r22 = r7
            r7 = r19
            r19 = r8
            r8 = r17
            boolean r0 = r2.reconcileSecondaryDexFile(r3, r4, r5, r6, r7, r8)     // Catch:{ InstallerException -> 0x00f6 }
            r18 = r0
            goto L_0x012b
        L_0x00f6:
            r0 = move-exception
            goto L_0x0109
        L_0x00f8:
            r0 = move-exception
            r20 = r4
            r21 = r5
            r22 = r7
            goto L_0x0145
        L_0x0100:
            r0 = move-exception
            r20 = r4
            r21 = r5
            r22 = r7
            r19 = r8
        L_0x0109:
            java.lang.String r2 = "DexManager"
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ all -> 0x0144 }
            r3.<init>()     // Catch:{ all -> 0x0144 }
            java.lang.String r4 = "Got InstallerException when reconciling dex "
            r3.append(r4)     // Catch:{ all -> 0x0144 }
            r3.append(r14)     // Catch:{ all -> 0x0144 }
            java.lang.String r4 = " : "
            r3.append(r4)     // Catch:{ all -> 0x0144 }
            java.lang.String r4 = r0.getMessage()     // Catch:{ all -> 0x0144 }
            r3.append(r4)     // Catch:{ all -> 0x0144 }
            java.lang.String r3 = r3.toString()     // Catch:{ all -> 0x0144 }
            android.util.Slog.e(r2, r3)     // Catch:{ all -> 0x0144 }
        L_0x012b:
            monitor-exit(r20)     // Catch:{ all -> 0x0144 }
            if (r18 != 0) goto L_0x0142
            com.android.server.pm.dex.PackageDexUsage r0 = r1.mPackageDexUsage
            int r2 = r15.getOwnerUserId()
            boolean r0 = r0.removeDexFile((java.lang.String) r9, (java.lang.String) r14, (int) r2)
            if (r0 != 0) goto L_0x013f
            if (r12 == 0) goto L_0x013d
            goto L_0x013f
        L_0x013d:
            r16 = r19
        L_0x013f:
            r0 = r16
            r12 = r0
        L_0x0142:
            goto L_0x003b
        L_0x0144:
            r0 = move-exception
        L_0x0145:
            monitor-exit(r20)     // Catch:{ all -> 0x0144 }
            throw r0
        L_0x0147:
            r21 = r5
            r22 = r7
            r19 = r8
            goto L_0x0154
        L_0x014e:
            r21 = r5
            r22 = r7
            r19 = r8
        L_0x0154:
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r3 = "Could not infer CE/DE storage for path "
            r2.append(r3)
            r2.append(r14)
            java.lang.String r2 = r2.toString()
            java.lang.String r3 = "DexManager"
            android.util.Slog.e(r3, r2)
            com.android.server.pm.dex.PackageDexUsage r2 = r1.mPackageDexUsage
            int r3 = r15.getOwnerUserId()
            boolean r2 = r2.removeDexFile((java.lang.String) r9, (java.lang.String) r14, (int) r3)
            if (r2 != 0) goto L_0x017b
            if (r12 == 0) goto L_0x0179
            goto L_0x017b
        L_0x0179:
            r16 = r19
        L_0x017b:
            r12 = r16
            goto L_0x003b
        L_0x017f:
            if (r12 == 0) goto L_0x0186
            com.android.server.pm.dex.PackageDexUsage r0 = r1.mPackageDexUsage
            r0.maybeWriteAsync()
        L_0x0186:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.pm.dex.DexManager.reconcileSecondaryDexFiles(java.lang.String):void");
    }

    public RegisterDexModuleResult registerDexModule(ApplicationInfo info, String dexPath, boolean isUsedByOtherApps, int userId) {
        ApplicationInfo applicationInfo = info;
        String str = dexPath;
        DexSearchResult searchResult = getDexPackage(applicationInfo, str, userId);
        if (searchResult.mOutcome == DEX_SEARCH_NOT_FOUND) {
            return new RegisterDexModuleResult(false, "Package not found");
        }
        if (!applicationInfo.packageName.equals(searchResult.mOwningPackageName)) {
            return new RegisterDexModuleResult(false, "Dex path does not belong to package");
        }
        if (searchResult.mOutcome == DEX_SEARCH_FOUND_PRIMARY || searchResult.mOutcome == DEX_SEARCH_FOUND_SPLIT) {
            return new RegisterDexModuleResult(false, "Main apks cannot be registered");
        }
        String[] appDexInstructionSets = InstructionSets.getAppDexInstructionSets(info);
        boolean update = false;
        int i = 0;
        for (int length = appDexInstructionSets.length; i < length; length = length) {
            update |= this.mPackageDexUsage.record(searchResult.mOwningPackageName, dexPath, userId, appDexInstructionSets[i], isUsedByOtherApps, false, searchResult.mOwningPackageName, "=UnknownClassLoaderContext=");
            i++;
        }
        if (update) {
            this.mPackageDexUsage.maybeWriteAsync();
        }
        if (this.mPackageDexOptimizer.dexOptSecondaryDexPath(applicationInfo, str, this.mPackageDexUsage.getPackageUseInfo(searchResult.mOwningPackageName).getDexUseInfoMap().get(str), new DexoptOptions(applicationInfo.packageName, 2, 0)) != -1) {
            Slog.e(TAG, "Failed to optimize dex module " + str);
        }
        return new RegisterDexModuleResult(true, "Dex module registered successfully");
    }

    public Set<String> getAllPackagesWithSecondaryDexFiles() {
        return this.mPackageDexUsage.getAllPackagesWithSecondaryDexFiles();
    }

    /* JADX WARNING: Code restructure failed: missing block: B:22:0x005a, code lost:
        if (DEBUG == false) goto L_0x008b;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:24:?, code lost:
        r2 = com.android.server.pm.PackageManagerServiceUtils.realpath(new java.io.File(r8));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:25:0x0069, code lost:
        if (r8.equals(r2) != false) goto L_0x008b;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:26:0x006b, code lost:
        android.util.Slog.d(TAG, "Dex loaded with symlink. dexPath=" + r8 + " dexPathReal=" + r2);
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private com.android.server.pm.dex.DexManager.DexSearchResult getDexPackage(android.content.pm.ApplicationInfo r7, java.lang.String r8, int r9) {
        /*
            r6 = this;
            java.lang.String r0 = "/system/framework/"
            boolean r0 = r8.startsWith(r0)
            if (r0 == 0) goto L_0x0012
            com.android.server.pm.dex.DexManager$DexSearchResult r0 = new com.android.server.pm.dex.DexManager$DexSearchResult
            int r1 = DEX_SEARCH_NOT_FOUND
            java.lang.String r2 = "framework"
            r0.<init>(r2, r1)
            return r0
        L_0x0012:
            com.android.server.pm.dex.DexManager$PackageCodeLocations r0 = new com.android.server.pm.dex.DexManager$PackageCodeLocations
            r0.<init>(r7, r9)
            int r1 = r0.searchDex(r8, r9)
            int r2 = DEX_SEARCH_NOT_FOUND
            if (r1 == r2) goto L_0x0029
            com.android.server.pm.dex.DexManager$DexSearchResult r2 = new com.android.server.pm.dex.DexManager$DexSearchResult
            java.lang.String r3 = r0.mPackageName
            r2.<init>(r3, r1)
            return r2
        L_0x0029:
            java.util.Map<java.lang.String, com.android.server.pm.dex.DexManager$PackageCodeLocations> r2 = r6.mPackageCodeLocationsCache
            monitor-enter(r2)
            java.util.Map<java.lang.String, com.android.server.pm.dex.DexManager$PackageCodeLocations> r3 = r6.mPackageCodeLocationsCache     // Catch:{ all -> 0x0094 }
            java.util.Collection r3 = r3.values()     // Catch:{ all -> 0x0094 }
            java.util.Iterator r3 = r3.iterator()     // Catch:{ all -> 0x0094 }
        L_0x0036:
            boolean r4 = r3.hasNext()     // Catch:{ all -> 0x0094 }
            if (r4 == 0) goto L_0x0057
            java.lang.Object r4 = r3.next()     // Catch:{ all -> 0x0094 }
            com.android.server.pm.dex.DexManager$PackageCodeLocations r4 = (com.android.server.pm.dex.DexManager.PackageCodeLocations) r4     // Catch:{ all -> 0x0094 }
            int r5 = r4.searchDex(r8, r9)     // Catch:{ all -> 0x0094 }
            r1 = r5
            int r5 = DEX_SEARCH_NOT_FOUND     // Catch:{ all -> 0x0094 }
            if (r1 == r5) goto L_0x0056
            com.android.server.pm.dex.DexManager$DexSearchResult r3 = new com.android.server.pm.dex.DexManager$DexSearchResult     // Catch:{ all -> 0x0094 }
            java.lang.String r5 = r4.mPackageName     // Catch:{ all -> 0x0094 }
            r3.<init>(r5, r1)     // Catch:{ all -> 0x0094 }
            monitor-exit(r2)     // Catch:{ all -> 0x0094 }
            return r3
        L_0x0056:
            goto L_0x0036
        L_0x0057:
            monitor-exit(r2)     // Catch:{ all -> 0x0094 }
            boolean r2 = DEBUG
            if (r2 == 0) goto L_0x008b
            java.io.File r2 = new java.io.File     // Catch:{ IOException -> 0x008a }
            r2.<init>(r8)     // Catch:{ IOException -> 0x008a }
            java.lang.String r2 = com.android.server.pm.PackageManagerServiceUtils.realpath(r2)     // Catch:{ IOException -> 0x008a }
            boolean r3 = r8.equals(r2)     // Catch:{ IOException -> 0x008a }
            if (r3 != 0) goto L_0x0089
            java.lang.String r3 = "DexManager"
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ IOException -> 0x008a }
            r4.<init>()     // Catch:{ IOException -> 0x008a }
            java.lang.String r5 = "Dex loaded with symlink. dexPath="
            r4.append(r5)     // Catch:{ IOException -> 0x008a }
            r4.append(r8)     // Catch:{ IOException -> 0x008a }
            java.lang.String r5 = " dexPathReal="
            r4.append(r5)     // Catch:{ IOException -> 0x008a }
            r4.append(r2)     // Catch:{ IOException -> 0x008a }
            java.lang.String r4 = r4.toString()     // Catch:{ IOException -> 0x008a }
            android.util.Slog.d(r3, r4)     // Catch:{ IOException -> 0x008a }
        L_0x0089:
            goto L_0x008b
        L_0x008a:
            r2 = move-exception
        L_0x008b:
            com.android.server.pm.dex.DexManager$DexSearchResult r2 = new com.android.server.pm.dex.DexManager$DexSearchResult
            r3 = 0
            int r4 = DEX_SEARCH_NOT_FOUND
            r2.<init>(r3, r4)
            return r2
        L_0x0094:
            r3 = move-exception
            monitor-exit(r2)     // Catch:{ all -> 0x0094 }
            throw r3
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.pm.dex.DexManager.getDexPackage(android.content.pm.ApplicationInfo, java.lang.String, int):com.android.server.pm.dex.DexManager$DexSearchResult");
    }

    /* access modifiers changed from: private */
    public static <K, V> V putIfAbsent(Map<K, V> map, K key, V newValue) {
        V existingValue = map.putIfAbsent(key, newValue);
        return existingValue == null ? newValue : existingValue;
    }

    public void writePackageDexUsageNow() {
        this.mPackageDexUsage.writeNow();
        this.mDynamicCodeLogger.writeNow();
    }

    public static boolean isPackageSelectedToRunOob(String packageName) {
        return isPackageSelectedToRunOob((Collection<String>) Arrays.asList(new String[]{packageName}));
    }

    public static boolean isPackageSelectedToRunOob(Collection<String> packageNamesInSameProcess) {
        return isPackageSelectedToRunOobInternal(SystemProperties.getBoolean(PROPERTY_NAME_PM_DEXOPT_PRIV_APPS_OOB, false), SystemProperties.get(PROPERTY_NAME_PM_DEXOPT_PRIV_APPS_OOB_LIST, "ALL"), DeviceConfig.getProperty("dex_boot", PRIV_APPS_OOB_ENABLED), DeviceConfig.getProperty("dex_boot", PRIV_APPS_OOB_WHITELIST), packageNamesInSameProcess);
    }

    @VisibleForTesting
    static boolean isPackageSelectedToRunOobInternal(boolean isDefaultEnabled, String defaultWhitelist, String overrideEnabled, String overrideWhitelist, Collection<String> packageNamesInSameProcess) {
        boolean enabled;
        if (overrideEnabled != null) {
            enabled = overrideEnabled.equals("true");
        } else {
            enabled = isDefaultEnabled;
        }
        if (!enabled) {
            return false;
        }
        String whitelist = overrideWhitelist != null ? overrideWhitelist : defaultWhitelist;
        if ("ALL".equals(whitelist)) {
            return true;
        }
        for (String oobPkgName : whitelist.split(",")) {
            if (packageNamesInSameProcess.contains(oobPkgName)) {
                return true;
            }
        }
        return false;
    }

    public static boolean auditUncompressedDexInApk(String fileName) {
        StrictJarFile jarFile = null;
        try {
            jarFile = new StrictJarFile(fileName, false, false);
            Iterator<ZipEntry> it = jarFile.iterator();
            boolean allCorrect = true;
            while (it.hasNext()) {
                ZipEntry entry = it.next();
                if (entry.getName().endsWith(".dex")) {
                    if (entry.getMethod() != 0) {
                        allCorrect = false;
                        Slog.w(TAG, "APK " + fileName + " has compressed dex code " + entry.getName());
                    } else if ((entry.getDataOffset() & 3) != 0) {
                        allCorrect = false;
                        Slog.w(TAG, "APK " + fileName + " has unaligned dex code " + entry.getName());
                    }
                }
            }
            try {
                jarFile.close();
            } catch (IOException e) {
            }
            return allCorrect;
        } catch (IOException e2) {
            Slog.wtf(TAG, "Error when parsing APK " + fileName);
            if (jarFile != null) {
                try {
                    jarFile.close();
                } catch (IOException e3) {
                }
            }
            return false;
        } catch (Throwable th) {
            if (jarFile != null) {
                try {
                    jarFile.close();
                } catch (IOException e4) {
                }
            }
            throw th;
        }
    }

    public static class RegisterDexModuleResult {
        public final String message;
        public final boolean success;

        public RegisterDexModuleResult() {
            this(false, (String) null);
        }

        public RegisterDexModuleResult(boolean success2, String message2) {
            this.success = success2;
            this.message = message2;
        }
    }

    private static class PackageCodeLocations {
        private final Map<Integer, Set<String>> mAppDataDirs;
        private String mBaseCodePath;
        /* access modifiers changed from: private */
        public final String mPackageName;
        private final Set<String> mSplitCodePaths;

        public PackageCodeLocations(ApplicationInfo ai, int userId) {
            this(ai.packageName, ai.sourceDir, ai.splitSourceDirs);
            mergeAppDataDirs(ai.dataDir, userId);
        }

        public PackageCodeLocations(String packageName, String baseCodePath, String[] splitCodePaths) {
            this.mPackageName = packageName;
            this.mSplitCodePaths = new HashSet();
            this.mAppDataDirs = new HashMap();
            updateCodeLocation(baseCodePath, splitCodePaths);
        }

        public void updateCodeLocation(String baseCodePath, String[] splitCodePaths) {
            this.mBaseCodePath = baseCodePath;
            this.mSplitCodePaths.clear();
            if (splitCodePaths != null) {
                for (String split : splitCodePaths) {
                    this.mSplitCodePaths.add(split);
                }
            }
        }

        public void mergeAppDataDirs(String dataDir, int userId) {
            ((Set) DexManager.putIfAbsent(this.mAppDataDirs, Integer.valueOf(userId), new HashSet())).add(dataDir);
        }

        public int searchDex(String dexPath, int userId) {
            Set<String> userDataDirs = this.mAppDataDirs.get(Integer.valueOf(userId));
            if (userDataDirs == null) {
                return DexManager.DEX_SEARCH_NOT_FOUND;
            }
            if (this.mBaseCodePath.equals(dexPath)) {
                return DexManager.DEX_SEARCH_FOUND_PRIMARY;
            }
            if (this.mSplitCodePaths.contains(dexPath)) {
                return DexManager.DEX_SEARCH_FOUND_SPLIT;
            }
            for (String dataDir : userDataDirs) {
                if (dexPath.startsWith(dataDir)) {
                    return DexManager.DEX_SEARCH_FOUND_SECONDARY;
                }
            }
            return DexManager.DEX_SEARCH_NOT_FOUND;
        }
    }

    private class DexSearchResult {
        /* access modifiers changed from: private */
        public int mOutcome;
        /* access modifiers changed from: private */
        public String mOwningPackageName;

        public DexSearchResult(String owningPackageName, int outcome) {
            this.mOwningPackageName = owningPackageName;
            this.mOutcome = outcome;
        }

        public String toString() {
            return this.mOwningPackageName + "-" + this.mOutcome;
        }
    }
}
