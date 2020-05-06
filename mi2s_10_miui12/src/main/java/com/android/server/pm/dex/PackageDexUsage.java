package com.android.server.pm.dex;

import android.os.Build;
import android.server.am.SplitScreenReporter;
import android.util.AtomicFile;
import android.util.Slog;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.util.FastPrintWriter;
import com.android.server.pm.AbstractStatsBase;
import dalvik.system.VMRuntime;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import libcore.io.IoUtils;

public class PackageDexUsage extends AbstractStatsBase<Void> {
    private static final String CODE_PATH_LINE_CHAR = "+";
    private static final String DEX_LINE_CHAR = "#";
    private static final String LOADING_PACKAGE_CHAR = "@";
    @VisibleForTesting
    static final int MAX_SECONDARY_FILES_PER_OWNER = 100;
    private static final int PACKAGE_DEX_USAGE_SUPPORTED_VERSION_1 = 1;
    private static final int PACKAGE_DEX_USAGE_SUPPORTED_VERSION_2 = 2;
    private static final int PACKAGE_DEX_USAGE_VERSION = 2;
    private static final String PACKAGE_DEX_USAGE_VERSION_HEADER = "PACKAGE_MANAGER__PACKAGE_DEX_USAGE__";
    private static final String SPLIT_CHAR = ",";
    private static final String TAG = "PackageDexUsage";
    static final String UNKNOWN_CLASS_LOADER_CONTEXT = "=UnknownClassLoaderContext=";
    private static final String UNSUPPORTED_CLASS_LOADER_CONTEXT = "=UnsupportedClassLoaderContext=";
    static final String VARIABLE_CLASS_LOADER_CONTEXT = "=VariableClassLoaderContext=";
    @GuardedBy({"mPackageUseInfoMap"})
    private final Map<String, PackageUseInfo> mPackageUseInfoMap = new HashMap();

    PackageDexUsage() {
        super("package-dex-usage.list", "PackageDexUsage_DiskWriter", false);
    }

    /* Debug info: failed to restart local var, previous not found, register: 16 */
    /* access modifiers changed from: package-private */
    /* JADX WARNING: Code restructure failed: missing block: B:36:0x0097, code lost:
        return r10;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean record(java.lang.String r17, java.lang.String r18, int r19, java.lang.String r20, boolean r21, boolean r22, java.lang.String r23, java.lang.String r24) {
        /*
            r16 = this;
            r1 = r16
            r2 = r17
            r3 = r18
            r4 = r19
            r5 = r20
            r6 = r21
            r7 = r23
            r8 = r24
            boolean r0 = com.android.server.pm.PackageManagerServiceUtils.checkISA(r20)
            if (r0 == 0) goto L_0x00ce
            if (r8 == 0) goto L_0x00c6
            java.util.Map<java.lang.String, com.android.server.pm.dex.PackageDexUsage$PackageUseInfo> r9 = r1.mPackageUseInfoMap
            monitor-enter(r9)
            java.util.Map<java.lang.String, com.android.server.pm.dex.PackageDexUsage$PackageUseInfo> r0 = r1.mPackageUseInfoMap     // Catch:{ all -> 0x00c3 }
            java.lang.Object r0 = r0.get(r2)     // Catch:{ all -> 0x00c3 }
            com.android.server.pm.dex.PackageDexUsage$PackageUseInfo r0 = (com.android.server.pm.dex.PackageDexUsage.PackageUseInfo) r0     // Catch:{ all -> 0x00c3 }
            r10 = 1
            if (r0 != 0) goto L_0x004d
            com.android.server.pm.dex.PackageDexUsage$PackageUseInfo r11 = new com.android.server.pm.dex.PackageDexUsage$PackageUseInfo     // Catch:{ all -> 0x00c3 }
            r11.<init>()     // Catch:{ all -> 0x00c3 }
            r0 = r11
            if (r22 == 0) goto L_0x0032
            boolean unused = r0.mergeCodePathUsedByOtherApps(r3, r6, r2, r7)     // Catch:{ all -> 0x00c3 }
            goto L_0x0046
        L_0x0032:
            com.android.server.pm.dex.PackageDexUsage$DexUseInfo r11 = new com.android.server.pm.dex.PackageDexUsage$DexUseInfo     // Catch:{ all -> 0x00c3 }
            r11.<init>(r6, r4, r8, r5)     // Catch:{ all -> 0x00c3 }
            java.util.Map r12 = r0.mDexUseInfoMap     // Catch:{ all -> 0x00c3 }
            r12.put(r3, r11)     // Catch:{ all -> 0x00c3 }
            java.util.Set r12 = r11.mLoadingPackages     // Catch:{ all -> 0x00c3 }
            r1.maybeAddLoadingPackage(r2, r7, r12)     // Catch:{ all -> 0x00c3 }
        L_0x0046:
            java.util.Map<java.lang.String, com.android.server.pm.dex.PackageDexUsage$PackageUseInfo> r11 = r1.mPackageUseInfoMap     // Catch:{ all -> 0x00c3 }
            r11.put(r2, r0)     // Catch:{ all -> 0x00c3 }
            monitor-exit(r9)     // Catch:{ all -> 0x00c3 }
            return r10
        L_0x004d:
            if (r22 == 0) goto L_0x0055
            boolean r10 = r0.mergeCodePathUsedByOtherApps(r3, r6, r2, r7)     // Catch:{ all -> 0x00c3 }
            monitor-exit(r9)     // Catch:{ all -> 0x00c3 }
            return r10
        L_0x0055:
            com.android.server.pm.dex.PackageDexUsage$DexUseInfo r11 = new com.android.server.pm.dex.PackageDexUsage$DexUseInfo     // Catch:{ all -> 0x00c3 }
            r11.<init>(r6, r4, r8, r5)     // Catch:{ all -> 0x00c3 }
            java.util.Set r12 = r11.mLoadingPackages     // Catch:{ all -> 0x00c3 }
            boolean r12 = r1.maybeAddLoadingPackage(r2, r7, r12)     // Catch:{ all -> 0x00c3 }
            java.util.Map r13 = r0.mDexUseInfoMap     // Catch:{ all -> 0x00c3 }
            java.lang.Object r13 = r13.get(r3)     // Catch:{ all -> 0x00c3 }
            com.android.server.pm.dex.PackageDexUsage$DexUseInfo r13 = (com.android.server.pm.dex.PackageDexUsage.DexUseInfo) r13     // Catch:{ all -> 0x00c3 }
            if (r13 != 0) goto L_0x0086
            java.util.Map r14 = r0.mDexUseInfoMap     // Catch:{ all -> 0x00c3 }
            int r14 = r14.size()     // Catch:{ all -> 0x00c3 }
            r15 = 100
            if (r14 >= r15) goto L_0x0084
            java.util.Map r14 = r0.mDexUseInfoMap     // Catch:{ all -> 0x00c3 }
            r14.put(r3, r11)     // Catch:{ all -> 0x00c3 }
            monitor-exit(r9)     // Catch:{ all -> 0x00c3 }
            return r10
        L_0x0084:
            monitor-exit(r9)     // Catch:{ all -> 0x00c3 }
            return r12
        L_0x0086:
            int r14 = r13.mOwnerUserId     // Catch:{ all -> 0x00c3 }
            if (r4 != r14) goto L_0x0098
            boolean r14 = r13.merge(r11)     // Catch:{ all -> 0x00c3 }
            if (r14 != 0) goto L_0x0096
            if (r12 == 0) goto L_0x0095
            goto L_0x0096
        L_0x0095:
            r10 = 0
        L_0x0096:
            monitor-exit(r9)     // Catch:{ all -> 0x00c3 }
            return r10
        L_0x0098:
            java.lang.IllegalArgumentException r10 = new java.lang.IllegalArgumentException     // Catch:{ all -> 0x00c3 }
            java.lang.StringBuilder r14 = new java.lang.StringBuilder     // Catch:{ all -> 0x00c3 }
            r14.<init>()     // Catch:{ all -> 0x00c3 }
            java.lang.String r15 = "Trying to change ownerUserId for  dex path "
            r14.append(r15)     // Catch:{ all -> 0x00c3 }
            r14.append(r3)     // Catch:{ all -> 0x00c3 }
            java.lang.String r15 = " from "
            r14.append(r15)     // Catch:{ all -> 0x00c3 }
            int r15 = r13.mOwnerUserId     // Catch:{ all -> 0x00c3 }
            r14.append(r15)     // Catch:{ all -> 0x00c3 }
            java.lang.String r15 = " to "
            r14.append(r15)     // Catch:{ all -> 0x00c3 }
            r14.append(r4)     // Catch:{ all -> 0x00c3 }
            java.lang.String r14 = r14.toString()     // Catch:{ all -> 0x00c3 }
            r10.<init>(r14)     // Catch:{ all -> 0x00c3 }
            throw r10     // Catch:{ all -> 0x00c3 }
        L_0x00c3:
            r0 = move-exception
            monitor-exit(r9)     // Catch:{ all -> 0x00c3 }
            throw r0
        L_0x00c6:
            java.lang.IllegalArgumentException r0 = new java.lang.IllegalArgumentException
            java.lang.String r9 = "Null classLoaderContext"
            r0.<init>(r9)
            throw r0
        L_0x00ce:
            java.lang.IllegalArgumentException r0 = new java.lang.IllegalArgumentException
            java.lang.StringBuilder r9 = new java.lang.StringBuilder
            r9.<init>()
            java.lang.String r10 = "loaderIsa "
            r9.append(r10)
            r9.append(r5)
            java.lang.String r10 = " is unsupported"
            r9.append(r10)
            java.lang.String r9 = r9.toString()
            r0.<init>(r9)
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.pm.dex.PackageDexUsage.record(java.lang.String, java.lang.String, int, java.lang.String, boolean, boolean, java.lang.String, java.lang.String):boolean");
    }

    /* access modifiers changed from: package-private */
    public void read() {
        read(null);
    }

    /* access modifiers changed from: package-private */
    public void maybeWriteAsync() {
        maybeWriteAsync(null);
    }

    /* access modifiers changed from: package-private */
    public void writeNow() {
        writeInternal((Void) null);
    }

    /* access modifiers changed from: protected */
    public void writeInternal(Void data) {
        AtomicFile file = getFile();
        FileOutputStream f = null;
        try {
            f = file.startWrite();
            OutputStreamWriter osw = new OutputStreamWriter(f);
            write(osw);
            osw.flush();
            file.finishWrite(f);
        } catch (IOException e) {
            if (f != null) {
                file.failWrite(f);
            }
            Slog.e(TAG, "Failed to write usage for dex files", e);
        }
    }

    /* access modifiers changed from: package-private */
    public void write(Writer out) {
        Map<String, PackageUseInfo> packageUseInfoMapClone = clonePackageUseInfoMap();
        FastPrintWriter fpw = new FastPrintWriter(out);
        fpw.print(PACKAGE_DEX_USAGE_VERSION_HEADER);
        int i = 2;
        fpw.println(2);
        for (Map.Entry<String, PackageUseInfo> pEntry : packageUseInfoMapClone.entrySet()) {
            PackageUseInfo packageUseInfo = pEntry.getValue();
            fpw.println(pEntry.getKey());
            for (Map.Entry<String, Set<String>> codeEntry : packageUseInfo.mCodePathsUsedByOtherApps.entrySet()) {
                fpw.println(CODE_PATH_LINE_CHAR + codeEntry.getKey());
                fpw.println(LOADING_PACKAGE_CHAR + String.join(SPLIT_CHAR, codeEntry.getValue()));
            }
            for (Map.Entry<String, DexUseInfo> dEntry : packageUseInfo.mDexUseInfoMap.entrySet()) {
                DexUseInfo dexUseInfo = dEntry.getValue();
                fpw.println(DEX_LINE_CHAR + dEntry.getKey());
                CharSequence[] charSequenceArr = new CharSequence[i];
                charSequenceArr[0] = Integer.toString(dexUseInfo.mOwnerUserId);
                Map<String, PackageUseInfo> packageUseInfoMapClone2 = packageUseInfoMapClone;
                charSequenceArr[1] = writeBoolean(dexUseInfo.mIsUsedByOtherApps);
                fpw.print(String.join(SPLIT_CHAR, charSequenceArr));
                for (String isa : dexUseInfo.mLoaderIsas) {
                    fpw.print(SPLIT_CHAR + isa);
                }
                fpw.println();
                fpw.println(LOADING_PACKAGE_CHAR + String.join(SPLIT_CHAR, dexUseInfo.mLoadingPackages));
                fpw.println(dexUseInfo.getClassLoaderContext());
                packageUseInfoMapClone = packageUseInfoMapClone2;
                i = 2;
            }
            Map<String, PackageUseInfo> packageUseInfoMapClone3 = packageUseInfoMapClone;
            packageUseInfoMapClone = packageUseInfoMapClone3;
            i = 2;
        }
        fpw.flush();
    }

    /* access modifiers changed from: protected */
    public void readInternal(Void data) {
        BufferedReader in = null;
        try {
            in = new BufferedReader(new InputStreamReader(getFile().openRead()));
            read(in);
        } catch (FileNotFoundException e) {
        } catch (IOException e2) {
            Slog.w(TAG, "Failed to parse package dex usage.", e2);
        } catch (Throwable th) {
            IoUtils.closeQuietly(in);
            throw th;
        }
        IoUtils.closeQuietly(in);
    }

    /* access modifiers changed from: package-private */
    public void read(Reader reader) throws IOException {
        String currentPackage;
        char c;
        Set<String> loadingPackages;
        int ownerUserId;
        Map<String, PackageUseInfo> data = new HashMap<>();
        BufferedReader in = new BufferedReader(reader);
        String versionLine = in.readLine();
        if (versionLine == null) {
            throw new IllegalStateException("No version line found.");
        } else if (versionLine.startsWith(PACKAGE_DEX_USAGE_VERSION_HEADER)) {
            int version = Integer.parseInt(versionLine.substring(PACKAGE_DEX_USAGE_VERSION_HEADER.length()));
            if (isSupportedVersion(version)) {
                Set<String> supportedIsas = new HashSet<>();
                char c2 = 0;
                for (String abi : Build.SUPPORTED_ABIS) {
                    supportedIsas.add(VMRuntime.getInstructionSet(abi));
                }
                PackageUseInfo currentPackageData = null;
                String currentPackage2 = null;
                while (true) {
                    String currentPackage3 = in.readLine();
                    String line = currentPackage3;
                    if (currentPackage3 != null) {
                        if (!line.startsWith(DEX_LINE_CHAR)) {
                            currentPackage = currentPackage2;
                            if (!line.startsWith(CODE_PATH_LINE_CHAR)) {
                                if (version >= 2) {
                                    currentPackage2 = line;
                                    currentPackageData = new PackageUseInfo();
                                    c = 0;
                                } else {
                                    String[] elems = line.split(SPLIT_CHAR);
                                    if (elems.length == 2) {
                                        c = 0;
                                        currentPackage2 = elems[0];
                                        currentPackageData = new PackageUseInfo();
                                        boolean unused = currentPackageData.mUsedByOtherAppsBeforeUpgrade = readBoolean(elems[1]);
                                    } else {
                                        throw new IllegalStateException("Invalid PackageDexUsage line: " + line);
                                    }
                                }
                                data.put(currentPackage2, currentPackageData);
                                c2 = c;
                                Reader reader2 = reader;
                            } else if (version >= 2) {
                                currentPackageData.mCodePathsUsedByOtherApps.put(line.substring(CODE_PATH_LINE_CHAR.length()), maybeReadLoadingPackages(in, version));
                            } else {
                                throw new IllegalArgumentException("Unexpected code path line when parsing PackageDexUseData: " + line);
                            }
                        } else if (currentPackage2 != null) {
                            String dexPath = line.substring(DEX_LINE_CHAR.length());
                            String line2 = in.readLine();
                            if (line2 != null) {
                                String[] elems2 = line2.split(SPLIT_CHAR);
                                if (elems2.length >= 3) {
                                    Set<String> loadingPackages2 = maybeReadLoadingPackages(in, version);
                                    String classLoaderContext = maybeReadClassLoaderContext(in, version);
                                    if (UNSUPPORTED_CLASS_LOADER_CONTEXT.equals(classLoaderContext)) {
                                        currentPackage = currentPackage2;
                                    } else {
                                        int ownerUserId2 = Integer.parseInt(elems2[c2]);
                                        boolean isUsedByOtherApps = readBoolean(elems2[1]);
                                        currentPackage = currentPackage2;
                                        DexUseInfo dexUseInfo = new DexUseInfo(isUsedByOtherApps, ownerUserId2, classLoaderContext, (String) null);
                                        dexUseInfo.mLoadingPackages.addAll(loadingPackages2);
                                        int i = 2;
                                        while (true) {
                                            boolean isUsedByOtherApps2 = isUsedByOtherApps;
                                            if (i >= elems2.length) {
                                                break;
                                            }
                                            String isa = elems2[i];
                                            if (supportedIsas.contains(isa)) {
                                                ownerUserId = ownerUserId2;
                                                loadingPackages = loadingPackages2;
                                                dexUseInfo.mLoaderIsas.add(elems2[i]);
                                            } else {
                                                ownerUserId = ownerUserId2;
                                                loadingPackages = loadingPackages2;
                                                Slog.wtf(TAG, "Unsupported ISA when parsing PackageDexUsage: " + isa);
                                            }
                                            i++;
                                            isUsedByOtherApps = isUsedByOtherApps2;
                                            ownerUserId2 = ownerUserId;
                                            loadingPackages2 = loadingPackages;
                                        }
                                        Set<String> set = loadingPackages2;
                                        if (supportedIsas.isEmpty() != 0) {
                                            Slog.wtf(TAG, "Ignore dexPath when parsing PackageDexUsage because of unsupported isas. dexPath=" + dexPath);
                                        } else {
                                            currentPackageData.mDexUseInfoMap.put(dexPath, dexUseInfo);
                                        }
                                    }
                                } else {
                                    throw new IllegalStateException("Invalid PackageDexUsage line: " + line2);
                                }
                            } else {
                                throw new IllegalStateException("Could not find dexUseInfo line");
                            }
                        } else {
                            throw new IllegalStateException("Malformed PackageDexUsage file. Expected package line before dex line.");
                        }
                        Reader reader3 = reader;
                        currentPackage2 = currentPackage;
                        c2 = 0;
                    } else {
                        synchronized (this.mPackageUseInfoMap) {
                            this.mPackageUseInfoMap.clear();
                            this.mPackageUseInfoMap.putAll(data);
                        }
                        return;
                    }
                }
            } else {
                throw new IllegalStateException("Unexpected version: " + version);
            }
        } else {
            throw new IllegalStateException("Invalid version line: " + versionLine);
        }
    }

    private String maybeReadClassLoaderContext(BufferedReader in, int version) throws IOException {
        String context = null;
        if (version < 2 || (context = in.readLine()) != null) {
            return context == null ? UNKNOWN_CLASS_LOADER_CONTEXT : context;
        }
        throw new IllegalStateException("Could not find the classLoaderContext line.");
    }

    private Set<String> maybeReadLoadingPackages(BufferedReader in, int version) throws IOException {
        if (version < 2) {
            return Collections.emptySet();
        }
        String line = in.readLine();
        if (line == null) {
            throw new IllegalStateException("Could not find the loadingPackages line.");
        } else if (line.length() == LOADING_PACKAGE_CHAR.length()) {
            return Collections.emptySet();
        } else {
            Set<String> result = new HashSet<>();
            Collections.addAll(result, line.substring(LOADING_PACKAGE_CHAR.length()).split(SPLIT_CHAR));
            return result;
        }
    }

    private boolean maybeAddLoadingPackage(String owningPackage, String loadingPackage, Set<String> loadingPackages) {
        return !owningPackage.equals(loadingPackage) && loadingPackages.add(loadingPackage);
    }

    private boolean isSupportedVersion(int version) {
        return version == 1 || version == 2;
    }

    /* access modifiers changed from: package-private */
    public void syncData(Map<String, Set<Integer>> packageToUsersMap, Map<String, Set<String>> packageToCodePaths) {
        synchronized (this.mPackageUseInfoMap) {
            Iterator<Map.Entry<String, PackageUseInfo>> pIt = this.mPackageUseInfoMap.entrySet().iterator();
            while (pIt.hasNext()) {
                Map.Entry<String, PackageUseInfo> pEntry = pIt.next();
                String packageName = pEntry.getKey();
                PackageUseInfo packageUseInfo = pEntry.getValue();
                Set<Integer> users = packageToUsersMap.get(packageName);
                if (users == null) {
                    pIt.remove();
                } else {
                    Iterator<Map.Entry<String, DexUseInfo>> dIt = packageUseInfo.mDexUseInfoMap.entrySet().iterator();
                    while (dIt.hasNext()) {
                        if (!users.contains(Integer.valueOf(((DexUseInfo) dIt.next().getValue()).mOwnerUserId))) {
                            dIt.remove();
                        }
                    }
                    Set<String> codePaths = packageToCodePaths.get(packageName);
                    Iterator<Map.Entry<String, Set<String>>> codeIt = packageUseInfo.mCodePathsUsedByOtherApps.entrySet().iterator();
                    while (codeIt.hasNext()) {
                        if (!codePaths.contains(codeIt.next().getKey())) {
                            codeIt.remove();
                        }
                    }
                    if (packageUseInfo.mUsedByOtherAppsBeforeUpgrade) {
                        for (String codePath : codePaths) {
                            boolean unused = packageUseInfo.mergeCodePathUsedByOtherApps(codePath, true, (String) null, (String) null);
                        }
                    } else if (!packageUseInfo.isAnyCodePathUsedByOtherApps() && packageUseInfo.mDexUseInfoMap.isEmpty()) {
                        pIt.remove();
                    }
                }
            }
        }
    }

    /* access modifiers changed from: package-private */
    public boolean clearUsedByOtherApps(String packageName) {
        synchronized (this.mPackageUseInfoMap) {
            PackageUseInfo packageUseInfo = this.mPackageUseInfoMap.get(packageName);
            if (packageUseInfo == null) {
                return false;
            }
            boolean clearCodePathUsedByOtherApps = packageUseInfo.clearCodePathUsedByOtherApps();
            return clearCodePathUsedByOtherApps;
        }
    }

    /* access modifiers changed from: package-private */
    public boolean removePackage(String packageName) {
        boolean z;
        synchronized (this.mPackageUseInfoMap) {
            z = this.mPackageUseInfoMap.remove(packageName) != null;
        }
        return z;
    }

    /* access modifiers changed from: package-private */
    /* JADX WARNING: Code restructure failed: missing block: B:21:0x0052, code lost:
        return r2;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean removeUserPackage(java.lang.String r7, int r8) {
        /*
            r6 = this;
            java.util.Map<java.lang.String, com.android.server.pm.dex.PackageDexUsage$PackageUseInfo> r0 = r6.mPackageUseInfoMap
            monitor-enter(r0)
            java.util.Map<java.lang.String, com.android.server.pm.dex.PackageDexUsage$PackageUseInfo> r1 = r6.mPackageUseInfoMap     // Catch:{ all -> 0x0053 }
            java.lang.Object r1 = r1.get(r7)     // Catch:{ all -> 0x0053 }
            com.android.server.pm.dex.PackageDexUsage$PackageUseInfo r1 = (com.android.server.pm.dex.PackageDexUsage.PackageUseInfo) r1     // Catch:{ all -> 0x0053 }
            if (r1 != 0) goto L_0x0010
            r2 = 0
            monitor-exit(r0)     // Catch:{ all -> 0x0053 }
            return r2
        L_0x0010:
            r2 = 0
            java.util.Map r3 = r1.mDexUseInfoMap     // Catch:{ all -> 0x0053 }
            java.util.Set r3 = r3.entrySet()     // Catch:{ all -> 0x0053 }
            java.util.Iterator r3 = r3.iterator()     // Catch:{ all -> 0x0053 }
        L_0x001e:
            boolean r4 = r3.hasNext()     // Catch:{ all -> 0x0053 }
            if (r4 == 0) goto L_0x003b
            java.lang.Object r4 = r3.next()     // Catch:{ all -> 0x0053 }
            java.util.Map$Entry r4 = (java.util.Map.Entry) r4     // Catch:{ all -> 0x0053 }
            java.lang.Object r4 = r4.getValue()     // Catch:{ all -> 0x0053 }
            com.android.server.pm.dex.PackageDexUsage$DexUseInfo r4 = (com.android.server.pm.dex.PackageDexUsage.DexUseInfo) r4     // Catch:{ all -> 0x0053 }
            int r5 = r4.mOwnerUserId     // Catch:{ all -> 0x0053 }
            if (r5 != r8) goto L_0x003a
            r3.remove()     // Catch:{ all -> 0x0053 }
            r2 = 1
        L_0x003a:
            goto L_0x001e
        L_0x003b:
            java.util.Map r4 = r1.mDexUseInfoMap     // Catch:{ all -> 0x0053 }
            boolean r4 = r4.isEmpty()     // Catch:{ all -> 0x0053 }
            if (r4 == 0) goto L_0x0051
            boolean r4 = r1.isAnyCodePathUsedByOtherApps()     // Catch:{ all -> 0x0053 }
            if (r4 != 0) goto L_0x0051
            java.util.Map<java.lang.String, com.android.server.pm.dex.PackageDexUsage$PackageUseInfo> r4 = r6.mPackageUseInfoMap     // Catch:{ all -> 0x0053 }
            r4.remove(r7)     // Catch:{ all -> 0x0053 }
            r2 = 1
        L_0x0051:
            monitor-exit(r0)     // Catch:{ all -> 0x0053 }
            return r2
        L_0x0053:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x0053 }
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.pm.dex.PackageDexUsage.removeUserPackage(java.lang.String, int):boolean");
    }

    /* access modifiers changed from: package-private */
    public boolean removeDexFile(String packageName, String dexFile, int userId) {
        synchronized (this.mPackageUseInfoMap) {
            PackageUseInfo packageUseInfo = this.mPackageUseInfoMap.get(packageName);
            if (packageUseInfo == null) {
                return false;
            }
            boolean removeDexFile = removeDexFile(packageUseInfo, dexFile, userId);
            return removeDexFile;
        }
    }

    private boolean removeDexFile(PackageUseInfo packageUseInfo, String dexFile, int userId) {
        DexUseInfo dexUseInfo = (DexUseInfo) packageUseInfo.mDexUseInfoMap.get(dexFile);
        if (dexUseInfo == null || dexUseInfo.mOwnerUserId != userId) {
            return false;
        }
        packageUseInfo.mDexUseInfoMap.remove(dexFile);
        return true;
    }

    /* access modifiers changed from: package-private */
    public PackageUseInfo getPackageUseInfo(String packageName) {
        PackageUseInfo packageUseInfo;
        synchronized (this.mPackageUseInfoMap) {
            PackageUseInfo useInfo = this.mPackageUseInfoMap.get(packageName);
            packageUseInfo = null;
            if (useInfo != null) {
                packageUseInfo = new PackageUseInfo(useInfo);
            }
        }
        return packageUseInfo;
    }

    /* access modifiers changed from: package-private */
    public Set<String> getAllPackagesWithSecondaryDexFiles() {
        Set<String> packages = new HashSet<>();
        synchronized (this.mPackageUseInfoMap) {
            for (Map.Entry<String, PackageUseInfo> entry : this.mPackageUseInfoMap.entrySet()) {
                if (!entry.getValue().mDexUseInfoMap.isEmpty()) {
                    packages.add(entry.getKey());
                }
            }
        }
        return packages;
    }

    /* access modifiers changed from: package-private */
    public void clear() {
        synchronized (this.mPackageUseInfoMap) {
            this.mPackageUseInfoMap.clear();
        }
    }

    private Map<String, PackageUseInfo> clonePackageUseInfoMap() {
        Map<String, PackageUseInfo> clone = new HashMap<>();
        synchronized (this.mPackageUseInfoMap) {
            for (Map.Entry<String, PackageUseInfo> e : this.mPackageUseInfoMap.entrySet()) {
                clone.put(e.getKey(), new PackageUseInfo(e.getValue()));
            }
        }
        return clone;
    }

    private String writeBoolean(boolean bool) {
        return bool ? SplitScreenReporter.ACTION_ENTER_SPLIT : "0";
    }

    private boolean readBoolean(String bool) {
        if ("0".equals(bool)) {
            return false;
        }
        if (SplitScreenReporter.ACTION_ENTER_SPLIT.equals(bool)) {
            return true;
        }
        throw new IllegalArgumentException("Unknown bool encoding: " + bool);
    }

    /* access modifiers changed from: package-private */
    public String dump() {
        StringWriter sw = new StringWriter();
        write(sw);
        return sw.toString();
    }

    public static class PackageUseInfo {
        /* access modifiers changed from: private */
        public final Map<String, Set<String>> mCodePathsUsedByOtherApps;
        /* access modifiers changed from: private */
        public final Map<String, DexUseInfo> mDexUseInfoMap;
        /* access modifiers changed from: private */
        public boolean mUsedByOtherAppsBeforeUpgrade;

        PackageUseInfo() {
            this.mCodePathsUsedByOtherApps = new HashMap();
            this.mDexUseInfoMap = new HashMap();
        }

        private PackageUseInfo(PackageUseInfo other) {
            this.mCodePathsUsedByOtherApps = new HashMap();
            for (Map.Entry<String, Set<String>> e : other.mCodePathsUsedByOtherApps.entrySet()) {
                this.mCodePathsUsedByOtherApps.put(e.getKey(), new HashSet(e.getValue()));
            }
            this.mDexUseInfoMap = new HashMap();
            for (Map.Entry<String, DexUseInfo> e2 : other.mDexUseInfoMap.entrySet()) {
                this.mDexUseInfoMap.put(e2.getKey(), new DexUseInfo(e2.getValue()));
            }
        }

        /* access modifiers changed from: private */
        public boolean mergeCodePathUsedByOtherApps(String codePath, boolean isUsedByOtherApps, String owningPackageName, String loadingPackage) {
            if (!isUsedByOtherApps) {
                return false;
            }
            boolean newCodePath = false;
            Set<String> loadingPackages = this.mCodePathsUsedByOtherApps.get(codePath);
            if (loadingPackages == null) {
                loadingPackages = new HashSet<>();
                this.mCodePathsUsedByOtherApps.put(codePath, loadingPackages);
                newCodePath = true;
            }
            boolean newLoadingPackage = loadingPackage != null && !loadingPackage.equals(owningPackageName) && loadingPackages.add(loadingPackage);
            if (newCodePath || newLoadingPackage) {
                return true;
            }
            return false;
        }

        public boolean isUsedByOtherApps(String codePath) {
            return this.mCodePathsUsedByOtherApps.containsKey(codePath);
        }

        public Map<String, DexUseInfo> getDexUseInfoMap() {
            return this.mDexUseInfoMap;
        }

        public Set<String> getLoadingPackages(String codePath) {
            return this.mCodePathsUsedByOtherApps.getOrDefault(codePath, (Object) null);
        }

        public boolean isAnyCodePathUsedByOtherApps() {
            return !this.mCodePathsUsedByOtherApps.isEmpty();
        }

        /* access modifiers changed from: package-private */
        public boolean clearCodePathUsedByOtherApps() {
            this.mUsedByOtherAppsBeforeUpgrade = true;
            if (this.mCodePathsUsedByOtherApps.isEmpty()) {
                return false;
            }
            this.mCodePathsUsedByOtherApps.clear();
            return true;
        }
    }

    public static class DexUseInfo {
        private String mClassLoaderContext;
        /* access modifiers changed from: private */
        public boolean mIsUsedByOtherApps;
        /* access modifiers changed from: private */
        public final Set<String> mLoaderIsas;
        /* access modifiers changed from: private */
        public final Set<String> mLoadingPackages;
        /* access modifiers changed from: private */
        public final int mOwnerUserId;

        @VisibleForTesting
        DexUseInfo(boolean isUsedByOtherApps, int ownerUserId, String classLoaderContext, String loaderIsa) {
            this.mIsUsedByOtherApps = isUsedByOtherApps;
            this.mOwnerUserId = ownerUserId;
            this.mClassLoaderContext = classLoaderContext;
            this.mLoaderIsas = new HashSet();
            if (loaderIsa != null) {
                this.mLoaderIsas.add(loaderIsa);
            }
            this.mLoadingPackages = new HashSet();
        }

        private DexUseInfo(DexUseInfo other) {
            this.mIsUsedByOtherApps = other.mIsUsedByOtherApps;
            this.mOwnerUserId = other.mOwnerUserId;
            this.mClassLoaderContext = other.mClassLoaderContext;
            this.mLoaderIsas = new HashSet(other.mLoaderIsas);
            this.mLoadingPackages = new HashSet(other.mLoadingPackages);
        }

        /* access modifiers changed from: private */
        public boolean merge(DexUseInfo dexUseInfo) {
            boolean oldIsUsedByOtherApps = this.mIsUsedByOtherApps;
            this.mIsUsedByOtherApps = this.mIsUsedByOtherApps || dexUseInfo.mIsUsedByOtherApps;
            boolean updateIsas = this.mLoaderIsas.addAll(dexUseInfo.mLoaderIsas);
            boolean updateLoadingPackages = this.mLoadingPackages.addAll(dexUseInfo.mLoadingPackages);
            String oldClassLoaderContext = this.mClassLoaderContext;
            if (PackageDexUsage.UNKNOWN_CLASS_LOADER_CONTEXT.equals(this.mClassLoaderContext)) {
                this.mClassLoaderContext = dexUseInfo.mClassLoaderContext;
            } else if (!Objects.equals(this.mClassLoaderContext, dexUseInfo.mClassLoaderContext)) {
                this.mClassLoaderContext = PackageDexUsage.VARIABLE_CLASS_LOADER_CONTEXT;
            }
            if (updateIsas || oldIsUsedByOtherApps != this.mIsUsedByOtherApps || updateLoadingPackages || !Objects.equals(oldClassLoaderContext, this.mClassLoaderContext)) {
                return true;
            }
            return false;
        }

        public boolean isUsedByOtherApps() {
            return this.mIsUsedByOtherApps;
        }

        /* access modifiers changed from: package-private */
        public int getOwnerUserId() {
            return this.mOwnerUserId;
        }

        public Set<String> getLoaderIsas() {
            return this.mLoaderIsas;
        }

        public Set<String> getLoadingPackages() {
            return this.mLoadingPackages;
        }

        public String getClassLoaderContext() {
            return this.mClassLoaderContext;
        }

        public boolean isUnknownClassLoaderContext() {
            return PackageDexUsage.UNKNOWN_CLASS_LOADER_CONTEXT.equals(this.mClassLoaderContext);
        }

        public boolean isVariableClassLoaderContext() {
            return PackageDexUsage.VARIABLE_CLASS_LOADER_CONTEXT.equals(this.mClassLoaderContext);
        }
    }
}
