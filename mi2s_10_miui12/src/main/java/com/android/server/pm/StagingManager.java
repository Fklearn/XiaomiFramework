package com.android.server.pm;

import android.apex.ApexInfo;
import android.apex.ApexInfoList;
import android.apex.ApexSessionInfo;
import android.content.Context;
import android.content.IIntentReceiver;
import android.content.IIntentSender;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageInfo;
import android.content.pm.PackageInstaller;
import android.content.pm.PackageParser;
import android.content.pm.ParceledListSlice;
import android.content.pm.Signature;
import android.content.rollback.IRollbackManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.ParcelFileDescriptor;
import android.os.PowerManager;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.util.Slog;
import android.util.SparseArray;
import android.util.apk.ApkSignatureVerifier;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.os.BackgroundThread;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class StagingManager {
    private static final String TAG = "StagingManager";
    private final ApexManager mApexManager;
    private final Handler mBgHandler;
    private final PackageInstallerService mPi;
    private final PowerManager mPowerManager;
    @GuardedBy({"mStagedSessions"})
    private final SparseArray<PackageInstallerSession> mStagedSessions = new SparseArray<>();

    StagingManager(PackageInstallerService pi, ApexManager am, Context context) {
        this.mPi = pi;
        this.mApexManager = am;
        this.mPowerManager = (PowerManager) context.getSystemService("power");
        this.mBgHandler = BackgroundThread.getHandler();
    }

    private void updateStoredSession(PackageInstallerSession sessionInfo) {
        synchronized (this.mStagedSessions) {
            if (this.mStagedSessions.get(sessionInfo.sessionId) != null) {
                this.mStagedSessions.put(sessionInfo.sessionId, sessionInfo);
            }
        }
    }

    /* access modifiers changed from: package-private */
    public ParceledListSlice<PackageInstaller.SessionInfo> getSessions() {
        List<PackageInstaller.SessionInfo> result = new ArrayList<>();
        synchronized (this.mStagedSessions) {
            for (int i = 0; i < this.mStagedSessions.size(); i++) {
                result.add(this.mStagedSessions.valueAt(i).generateInfo(false));
            }
        }
        return new ParceledListSlice<>(result);
    }

    private boolean validateApexSignature(String apexPath, String packageName) {
        try {
            PackageParser.SigningDetails signingDetails = ApkSignatureVerifier.verify(apexPath, 1);
            PackageInfo packageInfo = this.mApexManager.getPackageInfoForApexName(packageName);
            if (packageInfo == null) {
                Slog.e(TAG, "Attempted to install a new apex " + packageName + ". Rejecting");
                return false;
            }
            try {
                return Signature.areExactMatch(ApkSignatureVerifier.verify(packageInfo.applicationInfo.sourceDir, 1).signatures, signingDetails.signatures);
            } catch (PackageParser.PackageParserException e) {
                Slog.e(TAG, "Unable to parse APEX package: " + packageInfo.applicationInfo.sourceDir, e);
                return false;
            }
        } catch (PackageParser.PackageParserException e2) {
            Slog.e(TAG, "Unable to parse APEX package: " + apexPath, e2);
            return false;
        }
    }

    private boolean submitSessionToApexService(PackageInstallerSession session, List<PackageInstallerSession> childSessions, ApexInfoList apexInfoList) {
        int[] iArr;
        ApexInfo[] apexInfoArr;
        boolean z;
        PackageInstallerSession packageInstallerSession = session;
        ApexInfoList apexInfoList2 = apexInfoList;
        ApexManager apexManager = this.mApexManager;
        int i = packageInstallerSession.sessionId;
        boolean z2 = false;
        if (childSessions != null) {
            iArr = childSessions.stream().mapToInt($$Lambda$StagingManager$oxu05b9FQec8uLfg6h5LkmV4gk.INSTANCE).toArray();
        } else {
            iArr = new int[0];
        }
        boolean submittedToApexd = apexManager.submitStagedSession(i, iArr, apexInfoList2);
        int i2 = 1;
        if (!submittedToApexd) {
            packageInstallerSession.setStagedSessionFailed(1, "APEX staging failed, check logcat messages from apexd for more details.");
            return false;
        }
        ApexInfo[] apexInfoArr2 = apexInfoList2.apexInfos;
        int length = apexInfoArr2.length;
        int i3 = 0;
        while (i3 < length) {
            ApexInfo newPackage = apexInfoArr2[i3];
            PackageInfo activePackage = this.mApexManager.getPackageInfoForApexName(newPackage.packageName);
            if (activePackage == null) {
                z = z2;
                apexInfoArr = apexInfoArr2;
            } else {
                long activeVersion = activePackage.applicationInfo.longVersionCode;
                if (packageInstallerSession.params.requiredInstalledVersionCode != -1) {
                    apexInfoArr = apexInfoArr2;
                    if (activeVersion != packageInstallerSession.params.requiredInstalledVersionCode) {
                        packageInstallerSession.setStagedSessionFailed(i2, "Installed version of APEX package " + newPackage.packageName + " does not match required. Active version: " + activeVersion + " required: " + packageInstallerSession.params.requiredInstalledVersionCode);
                        if (this.mApexManager.abortActiveSession()) {
                            return false;
                        }
                        Slog.e(TAG, "Failed to abort apex session " + packageInstallerSession.sessionId);
                        return false;
                    }
                } else {
                    apexInfoArr = apexInfoArr2;
                }
                boolean allowsDowngrade = PackageManagerServiceUtils.isDowngradePermitted(packageInstallerSession.params.installFlags, activePackage.applicationInfo.flags);
                if (activeVersion <= newPackage.versionCode || allowsDowngrade) {
                    z = false;
                } else {
                    packageInstallerSession.setStagedSessionFailed(1, "Downgrade of APEX package " + newPackage.packageName + " is not allowed. Active version: " + activeVersion + " attempted: " + newPackage.versionCode);
                    if (this.mApexManager.abortActiveSession()) {
                        return false;
                    }
                    Slog.e(TAG, "Failed to abort apex session " + packageInstallerSession.sessionId);
                    return false;
                }
            }
            i3++;
            z2 = z;
            apexInfoArr2 = apexInfoArr;
            i2 = 1;
        }
        return true;
    }

    /* access modifiers changed from: private */
    public static boolean isApexSession(PackageInstallerSession session) {
        return (session.params.installFlags & 131072) != 0;
    }

    /* access modifiers changed from: private */
    /* renamed from: preRebootVerification */
    public void lambda$resumeSession$7$StagingManager(PackageInstallerSession session) {
        boolean success = true;
        ApexInfoList apexInfoList = new ApexInfoList();
        if (!session.isMultiPackage() && isApexSession(session)) {
            success = submitSessionToApexService(session, (List<PackageInstallerSession>) null, apexInfoList);
        } else if (session.isMultiPackage()) {
            List<PackageInstallerSession> childSessions = (List) Arrays.stream(session.getChildSessionIds()).mapToObj(new IntFunction() {
                public final Object apply(int i) {
                    return StagingManager.this.lambda$preRebootVerification$1$StagingManager(i);
                }
            }).filter($$Lambda$StagingManager$AgaT69AQKjTcEHdOPat7Y2rDy90.INSTANCE).collect(Collectors.toList());
            if (!childSessions.isEmpty()) {
                success = submitSessionToApexService(session, childSessions, apexInfoList);
            }
        }
        if (success) {
            if (!sessionContainsApk(session) || installApksInSession(session, true)) {
                if (apexInfoList.apexInfos != null && apexInfoList.apexInfos.length > 0) {
                    for (ApexInfo apexPackage : apexInfoList.apexInfos) {
                        if (!validateApexSignature(apexPackage.packagePath, apexPackage.packageName)) {
                            session.setStagedSessionFailed(1, "APK-container signature verification failed for package " + apexPackage.packageName + ". Signature of file " + apexPackage.packagePath + " does not match the signature of  the package already installed.");
                            return;
                        }
                    }
                }
                if ((session.params.installFlags & DumpState.DUMP_DOMAIN_PREFERRED) != 0) {
                    try {
                        if (!IRollbackManager.Stub.asInterface(ServiceManager.getService("rollback")).notifyStagedSession(session.sessionId)) {
                            Slog.e(TAG, "Unable to enable rollback for session: " + session.sessionId);
                        }
                    } catch (RemoteException e) {
                    }
                }
                session.setStagedSessionReady();
                if (sessionContainsApex(session) && !this.mApexManager.markStagedSessionReady(session.sessionId)) {
                    session.setStagedSessionFailed(1, "APEX staging failed, check logcat messages from apexd for more details.");
                    return;
                }
                return;
            }
            session.setStagedSessionFailed(1, "APK verification failed. Check logcat messages for more information.");
        }
    }

    public /* synthetic */ PackageInstallerSession lambda$preRebootVerification$1$StagingManager(int i) {
        return this.mStagedSessions.get(i);
    }

    private boolean sessionContains(PackageInstallerSession session, Predicate<PackageInstallerSession> filter) {
        boolean z;
        if (!session.isMultiPackage()) {
            return filter.test(session);
        }
        synchronized (this.mStagedSessions) {
            z = !((List) Arrays.stream(session.getChildSessionIds()).mapToObj(
            /*  JADX ERROR: Method code generation error
                jadx.core.utils.exceptions.CodegenException: Error generate insn: ?: TERNARY(r1v8 'z' boolean) = ((wrap: boolean : 0x0032: INVOKE  (r1v7 boolean) = 
                  (wrap: java.util.List : 0x0030: CHECK_CAST  (r1v6 java.util.List) = (java.util.List) (wrap: java.lang.Object : 0x002c: INVOKE  (r1v5 java.lang.Object) = 
                  (wrap: java.util.stream.Stream : 0x0024: INVOKE  (r1v4 java.util.stream.Stream) = 
                  (wrap: java.util.stream.Stream : 0x001b: INVOKE  (r1v3 java.util.stream.Stream) = 
                  (wrap: java.util.stream.IntStream : 0x0012: INVOKE  (r1v2 java.util.stream.IntStream) = 
                  (wrap: int[] : 0x000e: INVOKE  (r1v1 int[]) = 
                  (r4v0 'session' com.android.server.pm.PackageInstallerSession)
                 com.android.server.pm.PackageInstallerSession.getChildSessionIds():int[] type: VIRTUAL)
                 java.util.Arrays.stream(int[]):java.util.stream.IntStream type: STATIC)
                  (wrap: com.android.server.pm.-$$Lambda$StagingManager$zPvhMKF7o6jzlVNzE42Fq_qJt9I : 0x0018: CONSTRUCTOR  (r2v0 com.android.server.pm.-$$Lambda$StagingManager$zPvhMKF7o6jzlVNzE42Fq_qJt9I) = (r3v0 'this' com.android.server.pm.StagingManager A[THIS]) call: com.android.server.pm.-$$Lambda$StagingManager$zPvhMKF7o6jzlVNzE42Fq_qJt9I.<init>(com.android.server.pm.StagingManager):void type: CONSTRUCTOR)
                 java.util.stream.IntStream.mapToObj(java.util.function.IntFunction):java.util.stream.Stream type: INTERFACE)
                  (wrap: com.android.server.pm.-$$Lambda$StagingManager$lOH9gVOKGitWaFqixZa09s5PphU : 0x0021: CONSTRUCTOR  (r2v1 com.android.server.pm.-$$Lambda$StagingManager$lOH9gVOKGitWaFqixZa09s5PphU) = 
                  (r5v0 'filter' java.util.function.Predicate<com.android.server.pm.PackageInstallerSession>)
                 call: com.android.server.pm.-$$Lambda$StagingManager$lOH9gVOKGitWaFqixZa09s5PphU.<init>(java.util.function.Predicate):void type: CONSTRUCTOR)
                 java.util.stream.Stream.filter(java.util.function.Predicate):java.util.stream.Stream type: INTERFACE)
                  (wrap: java.util.stream.Collector : 0x0028: INVOKE  (r2v2 java.util.stream.Collector) =  java.util.stream.Collectors.toList():java.util.stream.Collector type: STATIC)
                 java.util.stream.Stream.collect(java.util.stream.Collector):java.lang.Object type: INTERFACE))
                 java.util.List.isEmpty():boolean type: INTERFACE) == false) ? true : false in method: com.android.server.pm.StagingManager.sessionContains(com.android.server.pm.PackageInstallerSession, java.util.function.Predicate):boolean, dex: classes.dex
                	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:256)
                	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:221)
                	at jadx.core.codegen.RegionGen.makeSimpleBlock(RegionGen.java:109)
                	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:55)
                	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:92)
                	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:58)
                	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:92)
                	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:58)
                	at jadx.core.codegen.RegionGen.makeRegionIndent(RegionGen.java:98)
                	at jadx.core.codegen.RegionGen.makeSynchronizedRegion(RegionGen.java:260)
                	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:70)
                	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:92)
                	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:58)
                	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:92)
                	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:58)
                	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:92)
                	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:58)
                	at jadx.core.codegen.MethodGen.addRegionInsns(MethodGen.java:211)
                	at jadx.core.codegen.MethodGen.addInstructions(MethodGen.java:204)
                	at jadx.core.codegen.ClassGen.addMethodCode(ClassGen.java:318)
                	at jadx.core.codegen.ClassGen.addMethod(ClassGen.java:271)
                	at jadx.core.codegen.ClassGen.lambda$addInnerClsAndMethods$2(ClassGen.java:240)
                	at java.util.stream.ForEachOps$ForEachOp$OfRef.accept(ForEachOps.java:184)
                	at java.util.ArrayList.forEach(ArrayList.java:1257)
                	at java.util.stream.SortedOps$RefSortingSink.end(SortedOps.java:390)
                	at java.util.stream.Sink$ChainedReference.end(Sink.java:258)
                	at java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:482)
                	at java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:471)
                	at java.util.stream.ForEachOps$ForEachOp.evaluateSequential(ForEachOps.java:151)
                	at java.util.stream.ForEachOps$ForEachOp$OfRef.evaluateSequential(ForEachOps.java:174)
                	at java.util.stream.AbstractPipeline.evaluate(AbstractPipeline.java:234)
                	at java.util.stream.ReferencePipeline.forEach(ReferencePipeline.java:418)
                	at jadx.core.codegen.ClassGen.addInnerClsAndMethods(ClassGen.java:236)
                	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:227)
                	at jadx.core.codegen.ClassGen.addClassCode(ClassGen.java:112)
                	at jadx.core.codegen.ClassGen.makeClass(ClassGen.java:78)
                	at jadx.core.codegen.CodeGen.wrapCodeGen(CodeGen.java:44)
                	at jadx.core.codegen.CodeGen.generateJavaCode(CodeGen.java:33)
                	at jadx.core.codegen.CodeGen.generate(CodeGen.java:21)
                	at jadx.core.ProcessClass.generateCode(ProcessClass.java:61)
                	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:273)
                Caused by: jadx.core.utils.exceptions.CodegenException: Error generate insn: 0x0032: INVOKE  (r1v7 boolean) = 
                  (wrap: java.util.List : 0x0030: CHECK_CAST  (r1v6 java.util.List) = (java.util.List) (wrap: java.lang.Object : 0x002c: INVOKE  (r1v5 java.lang.Object) = 
                  (wrap: java.util.stream.Stream : 0x0024: INVOKE  (r1v4 java.util.stream.Stream) = 
                  (wrap: java.util.stream.Stream : 0x001b: INVOKE  (r1v3 java.util.stream.Stream) = 
                  (wrap: java.util.stream.IntStream : 0x0012: INVOKE  (r1v2 java.util.stream.IntStream) = 
                  (wrap: int[] : 0x000e: INVOKE  (r1v1 int[]) = 
                  (r4v0 'session' com.android.server.pm.PackageInstallerSession)
                 com.android.server.pm.PackageInstallerSession.getChildSessionIds():int[] type: VIRTUAL)
                 java.util.Arrays.stream(int[]):java.util.stream.IntStream type: STATIC)
                  (wrap: com.android.server.pm.-$$Lambda$StagingManager$zPvhMKF7o6jzlVNzE42Fq_qJt9I : 0x0018: CONSTRUCTOR  (r2v0 com.android.server.pm.-$$Lambda$StagingManager$zPvhMKF7o6jzlVNzE42Fq_qJt9I) = (r3v0 'this' com.android.server.pm.StagingManager A[THIS]) call: com.android.server.pm.-$$Lambda$StagingManager$zPvhMKF7o6jzlVNzE42Fq_qJt9I.<init>(com.android.server.pm.StagingManager):void type: CONSTRUCTOR)
                 java.util.stream.IntStream.mapToObj(java.util.function.IntFunction):java.util.stream.Stream type: INTERFACE)
                  (wrap: com.android.server.pm.-$$Lambda$StagingManager$lOH9gVOKGitWaFqixZa09s5PphU : 0x0021: CONSTRUCTOR  (r2v1 com.android.server.pm.-$$Lambda$StagingManager$lOH9gVOKGitWaFqixZa09s5PphU) = 
                  (r5v0 'filter' java.util.function.Predicate<com.android.server.pm.PackageInstallerSession>)
                 call: com.android.server.pm.-$$Lambda$StagingManager$lOH9gVOKGitWaFqixZa09s5PphU.<init>(java.util.function.Predicate):void type: CONSTRUCTOR)
                 java.util.stream.Stream.filter(java.util.function.Predicate):java.util.stream.Stream type: INTERFACE)
                  (wrap: java.util.stream.Collector : 0x0028: INVOKE  (r2v2 java.util.stream.Collector) =  java.util.stream.Collectors.toList():java.util.stream.Collector type: STATIC)
                 java.util.stream.Stream.collect(java.util.stream.Collector):java.lang.Object type: INTERFACE))
                 java.util.List.isEmpty():boolean type: INTERFACE in method: com.android.server.pm.StagingManager.sessionContains(com.android.server.pm.PackageInstallerSession, java.util.function.Predicate):boolean, dex: classes.dex
                	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:256)
                	at jadx.core.codegen.InsnGen.addWrappedArg(InsnGen.java:123)
                	at jadx.core.codegen.InsnGen.addArg(InsnGen.java:107)
                	at jadx.core.codegen.ConditionGen.wrap(ConditionGen.java:95)
                	at jadx.core.codegen.ConditionGen.addCompare(ConditionGen.java:123)
                	at jadx.core.codegen.ConditionGen.add(ConditionGen.java:57)
                	at jadx.core.codegen.ConditionGen.add(ConditionGen.java:46)
                	at jadx.core.codegen.InsnGen.makeTernary(InsnGen.java:948)
                	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:476)
                	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:250)
                	... 40 more
                Caused by: jadx.core.utils.exceptions.CodegenException: Error generate insn: 0x0030: CHECK_CAST  (r1v6 java.util.List) = (java.util.List) (wrap: java.lang.Object : 0x002c: INVOKE  (r1v5 java.lang.Object) = 
                  (wrap: java.util.stream.Stream : 0x0024: INVOKE  (r1v4 java.util.stream.Stream) = 
                  (wrap: java.util.stream.Stream : 0x001b: INVOKE  (r1v3 java.util.stream.Stream) = 
                  (wrap: java.util.stream.IntStream : 0x0012: INVOKE  (r1v2 java.util.stream.IntStream) = 
                  (wrap: int[] : 0x000e: INVOKE  (r1v1 int[]) = 
                  (r4v0 'session' com.android.server.pm.PackageInstallerSession)
                 com.android.server.pm.PackageInstallerSession.getChildSessionIds():int[] type: VIRTUAL)
                 java.util.Arrays.stream(int[]):java.util.stream.IntStream type: STATIC)
                  (wrap: com.android.server.pm.-$$Lambda$StagingManager$zPvhMKF7o6jzlVNzE42Fq_qJt9I : 0x0018: CONSTRUCTOR  (r2v0 com.android.server.pm.-$$Lambda$StagingManager$zPvhMKF7o6jzlVNzE42Fq_qJt9I) = (r3v0 'this' com.android.server.pm.StagingManager A[THIS]) call: com.android.server.pm.-$$Lambda$StagingManager$zPvhMKF7o6jzlVNzE42Fq_qJt9I.<init>(com.android.server.pm.StagingManager):void type: CONSTRUCTOR)
                 java.util.stream.IntStream.mapToObj(java.util.function.IntFunction):java.util.stream.Stream type: INTERFACE)
                  (wrap: com.android.server.pm.-$$Lambda$StagingManager$lOH9gVOKGitWaFqixZa09s5PphU : 0x0021: CONSTRUCTOR  (r2v1 com.android.server.pm.-$$Lambda$StagingManager$lOH9gVOKGitWaFqixZa09s5PphU) = 
                  (r5v0 'filter' java.util.function.Predicate<com.android.server.pm.PackageInstallerSession>)
                 call: com.android.server.pm.-$$Lambda$StagingManager$lOH9gVOKGitWaFqixZa09s5PphU.<init>(java.util.function.Predicate):void type: CONSTRUCTOR)
                 java.util.stream.Stream.filter(java.util.function.Predicate):java.util.stream.Stream type: INTERFACE)
                  (wrap: java.util.stream.Collector : 0x0028: INVOKE  (r2v2 java.util.stream.Collector) =  java.util.stream.Collectors.toList():java.util.stream.Collector type: STATIC)
                 java.util.stream.Stream.collect(java.util.stream.Collector):java.lang.Object type: INTERFACE) in method: com.android.server.pm.StagingManager.sessionContains(com.android.server.pm.PackageInstallerSession, java.util.function.Predicate):boolean, dex: classes.dex
                	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:256)
                	at jadx.core.codegen.InsnGen.addWrappedArg(InsnGen.java:123)
                	at jadx.core.codegen.InsnGen.addArg(InsnGen.java:107)
                	at jadx.core.codegen.InsnGen.addArgDot(InsnGen.java:91)
                	at jadx.core.codegen.InsnGen.makeInvoke(InsnGen.java:697)
                	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:368)
                	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:231)
                	... 49 more
                Caused by: jadx.core.utils.exceptions.CodegenException: Error generate insn: 0x002c: INVOKE  (r1v5 java.lang.Object) = 
                  (wrap: java.util.stream.Stream : 0x0024: INVOKE  (r1v4 java.util.stream.Stream) = 
                  (wrap: java.util.stream.Stream : 0x001b: INVOKE  (r1v3 java.util.stream.Stream) = 
                  (wrap: java.util.stream.IntStream : 0x0012: INVOKE  (r1v2 java.util.stream.IntStream) = 
                  (wrap: int[] : 0x000e: INVOKE  (r1v1 int[]) = 
                  (r4v0 'session' com.android.server.pm.PackageInstallerSession)
                 com.android.server.pm.PackageInstallerSession.getChildSessionIds():int[] type: VIRTUAL)
                 java.util.Arrays.stream(int[]):java.util.stream.IntStream type: STATIC)
                  (wrap: com.android.server.pm.-$$Lambda$StagingManager$zPvhMKF7o6jzlVNzE42Fq_qJt9I : 0x0018: CONSTRUCTOR  (r2v0 com.android.server.pm.-$$Lambda$StagingManager$zPvhMKF7o6jzlVNzE42Fq_qJt9I) = (r3v0 'this' com.android.server.pm.StagingManager A[THIS]) call: com.android.server.pm.-$$Lambda$StagingManager$zPvhMKF7o6jzlVNzE42Fq_qJt9I.<init>(com.android.server.pm.StagingManager):void type: CONSTRUCTOR)
                 java.util.stream.IntStream.mapToObj(java.util.function.IntFunction):java.util.stream.Stream type: INTERFACE)
                  (wrap: com.android.server.pm.-$$Lambda$StagingManager$lOH9gVOKGitWaFqixZa09s5PphU : 0x0021: CONSTRUCTOR  (r2v1 com.android.server.pm.-$$Lambda$StagingManager$lOH9gVOKGitWaFqixZa09s5PphU) = 
                  (r5v0 'filter' java.util.function.Predicate<com.android.server.pm.PackageInstallerSession>)
                 call: com.android.server.pm.-$$Lambda$StagingManager$lOH9gVOKGitWaFqixZa09s5PphU.<init>(java.util.function.Predicate):void type: CONSTRUCTOR)
                 java.util.stream.Stream.filter(java.util.function.Predicate):java.util.stream.Stream type: INTERFACE)
                  (wrap: java.util.stream.Collector : 0x0028: INVOKE  (r2v2 java.util.stream.Collector) =  java.util.stream.Collectors.toList():java.util.stream.Collector type: STATIC)
                 java.util.stream.Stream.collect(java.util.stream.Collector):java.lang.Object type: INTERFACE in method: com.android.server.pm.StagingManager.sessionContains(com.android.server.pm.PackageInstallerSession, java.util.function.Predicate):boolean, dex: classes.dex
                	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:256)
                	at jadx.core.codegen.InsnGen.addWrappedArg(InsnGen.java:123)
                	at jadx.core.codegen.InsnGen.addArg(InsnGen.java:107)
                	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:291)
                	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:231)
                	... 55 more
                Caused by: jadx.core.utils.exceptions.CodegenException: Error generate insn: 0x0024: INVOKE  (r1v4 java.util.stream.Stream) = 
                  (wrap: java.util.stream.Stream : 0x001b: INVOKE  (r1v3 java.util.stream.Stream) = 
                  (wrap: java.util.stream.IntStream : 0x0012: INVOKE  (r1v2 java.util.stream.IntStream) = 
                  (wrap: int[] : 0x000e: INVOKE  (r1v1 int[]) = 
                  (r4v0 'session' com.android.server.pm.PackageInstallerSession)
                 com.android.server.pm.PackageInstallerSession.getChildSessionIds():int[] type: VIRTUAL)
                 java.util.Arrays.stream(int[]):java.util.stream.IntStream type: STATIC)
                  (wrap: com.android.server.pm.-$$Lambda$StagingManager$zPvhMKF7o6jzlVNzE42Fq_qJt9I : 0x0018: CONSTRUCTOR  (r2v0 com.android.server.pm.-$$Lambda$StagingManager$zPvhMKF7o6jzlVNzE42Fq_qJt9I) = (r3v0 'this' com.android.server.pm.StagingManager A[THIS]) call: com.android.server.pm.-$$Lambda$StagingManager$zPvhMKF7o6jzlVNzE42Fq_qJt9I.<init>(com.android.server.pm.StagingManager):void type: CONSTRUCTOR)
                 java.util.stream.IntStream.mapToObj(java.util.function.IntFunction):java.util.stream.Stream type: INTERFACE)
                  (wrap: com.android.server.pm.-$$Lambda$StagingManager$lOH9gVOKGitWaFqixZa09s5PphU : 0x0021: CONSTRUCTOR  (r2v1 com.android.server.pm.-$$Lambda$StagingManager$lOH9gVOKGitWaFqixZa09s5PphU) = 
                  (r5v0 'filter' java.util.function.Predicate<com.android.server.pm.PackageInstallerSession>)
                 call: com.android.server.pm.-$$Lambda$StagingManager$lOH9gVOKGitWaFqixZa09s5PphU.<init>(java.util.function.Predicate):void type: CONSTRUCTOR)
                 java.util.stream.Stream.filter(java.util.function.Predicate):java.util.stream.Stream type: INTERFACE in method: com.android.server.pm.StagingManager.sessionContains(com.android.server.pm.PackageInstallerSession, java.util.function.Predicate):boolean, dex: classes.dex
                	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:256)
                	at jadx.core.codegen.InsnGen.addWrappedArg(InsnGen.java:123)
                	at jadx.core.codegen.InsnGen.addArg(InsnGen.java:107)
                	at jadx.core.codegen.InsnGen.addArgDot(InsnGen.java:91)
                	at jadx.core.codegen.InsnGen.makeInvoke(InsnGen.java:697)
                	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:368)
                	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:231)
                	... 59 more
                Caused by: jadx.core.utils.exceptions.CodegenException: Error generate insn: 0x001b: INVOKE  (r1v3 java.util.stream.Stream) = 
                  (wrap: java.util.stream.IntStream : 0x0012: INVOKE  (r1v2 java.util.stream.IntStream) = 
                  (wrap: int[] : 0x000e: INVOKE  (r1v1 int[]) = 
                  (r4v0 'session' com.android.server.pm.PackageInstallerSession)
                 com.android.server.pm.PackageInstallerSession.getChildSessionIds():int[] type: VIRTUAL)
                 java.util.Arrays.stream(int[]):java.util.stream.IntStream type: STATIC)
                  (wrap: com.android.server.pm.-$$Lambda$StagingManager$zPvhMKF7o6jzlVNzE42Fq_qJt9I : 0x0018: CONSTRUCTOR  (r2v0 com.android.server.pm.-$$Lambda$StagingManager$zPvhMKF7o6jzlVNzE42Fq_qJt9I) = (r3v0 'this' com.android.server.pm.StagingManager A[THIS]) call: com.android.server.pm.-$$Lambda$StagingManager$zPvhMKF7o6jzlVNzE42Fq_qJt9I.<init>(com.android.server.pm.StagingManager):void type: CONSTRUCTOR)
                 java.util.stream.IntStream.mapToObj(java.util.function.IntFunction):java.util.stream.Stream type: INTERFACE in method: com.android.server.pm.StagingManager.sessionContains(com.android.server.pm.PackageInstallerSession, java.util.function.Predicate):boolean, dex: classes.dex
                	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:256)
                	at jadx.core.codegen.InsnGen.addWrappedArg(InsnGen.java:123)
                	at jadx.core.codegen.InsnGen.addArg(InsnGen.java:107)
                	at jadx.core.codegen.InsnGen.addArgDot(InsnGen.java:91)
                	at jadx.core.codegen.InsnGen.makeInvoke(InsnGen.java:697)
                	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:368)
                	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:231)
                	... 65 more
                Caused by: jadx.core.utils.exceptions.CodegenException: Error generate insn: 0x0018: CONSTRUCTOR  (r2v0 com.android.server.pm.-$$Lambda$StagingManager$zPvhMKF7o6jzlVNzE42Fq_qJt9I) = (r3v0 'this' com.android.server.pm.StagingManager A[THIS]) call: com.android.server.pm.-$$Lambda$StagingManager$zPvhMKF7o6jzlVNzE42Fq_qJt9I.<init>(com.android.server.pm.StagingManager):void type: CONSTRUCTOR in method: com.android.server.pm.StagingManager.sessionContains(com.android.server.pm.PackageInstallerSession, java.util.function.Predicate):boolean, dex: classes.dex
                	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:256)
                	at jadx.core.codegen.InsnGen.addWrappedArg(InsnGen.java:123)
                	at jadx.core.codegen.InsnGen.addArg(InsnGen.java:107)
                	at jadx.core.codegen.InsnGen.generateMethodArguments(InsnGen.java:787)
                	at jadx.core.codegen.InsnGen.makeInvoke(InsnGen.java:728)
                	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:368)
                	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:231)
                	... 71 more
                Caused by: jadx.core.utils.exceptions.JadxRuntimeException: Expected class to be processed at this point, class: com.android.server.pm.-$$Lambda$StagingManager$zPvhMKF7o6jzlVNzE42Fq_qJt9I, state: NOT_LOADED
                	at jadx.core.dex.nodes.ClassNode.ensureProcessed(ClassNode.java:260)
                	at jadx.core.codegen.InsnGen.makeConstructor(InsnGen.java:606)
                	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:364)
                	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:231)
                	... 77 more
                */
            /*
                this = this;
                boolean r0 = r4.isMultiPackage()
                if (r0 != 0) goto L_0x000b
                boolean r0 = r5.test(r4)
                return r0
            L_0x000b:
                android.util.SparseArray<com.android.server.pm.PackageInstallerSession> r0 = r3.mStagedSessions
                monitor-enter(r0)
                int[] r1 = r4.getChildSessionIds()     // Catch:{ all -> 0x003d }
                java.util.stream.IntStream r1 = java.util.Arrays.stream(r1)     // Catch:{ all -> 0x003d }
                com.android.server.pm.-$$Lambda$StagingManager$zPvhMKF7o6jzlVNzE42Fq_qJt9I r2 = new com.android.server.pm.-$$Lambda$StagingManager$zPvhMKF7o6jzlVNzE42Fq_qJt9I     // Catch:{ all -> 0x003d }
                r2.<init>(r3)     // Catch:{ all -> 0x003d }
                java.util.stream.Stream r1 = r1.mapToObj(r2)     // Catch:{ all -> 0x003d }
                com.android.server.pm.-$$Lambda$StagingManager$lOH9gVOKGitWaFqixZa09s5PphU r2 = new com.android.server.pm.-$$Lambda$StagingManager$lOH9gVOKGitWaFqixZa09s5PphU     // Catch:{ all -> 0x003d }
                r2.<init>(r5)     // Catch:{ all -> 0x003d }
                java.util.stream.Stream r1 = r1.filter(r2)     // Catch:{ all -> 0x003d }
                java.util.stream.Collector r2 = java.util.stream.Collectors.toList()     // Catch:{ all -> 0x003d }
                java.lang.Object r1 = r1.collect(r2)     // Catch:{ all -> 0x003d }
                java.util.List r1 = (java.util.List) r1     // Catch:{ all -> 0x003d }
                boolean r1 = r1.isEmpty()     // Catch:{ all -> 0x003d }
                if (r1 != 0) goto L_0x003a
                r1 = 1
                goto L_0x003b
            L_0x003a:
                r1 = 0
            L_0x003b:
                monitor-exit(r0)     // Catch:{ all -> 0x003d }
                return r1
            L_0x003d:
                r1 = move-exception
                monitor-exit(r0)     // Catch:{ all -> 0x003d }
                throw r1
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.server.pm.StagingManager.sessionContains(com.android.server.pm.PackageInstallerSession, java.util.function.Predicate):boolean");
        }

        public /* synthetic */ PackageInstallerSession lambda$sessionContains$3$StagingManager(int i) {
            return this.mStagedSessions.get(i);
        }

        private boolean sessionContainsApex(PackageInstallerSession session) {
            return sessionContains(session, $$Lambda$StagingManager$HJyijsQNJwcPQ102tU6415xlVo.INSTANCE);
        }

        static /* synthetic */ boolean lambda$sessionContainsApk$6(PackageInstallerSession s) {
            return !isApexSession(s);
        }

        private boolean sessionContainsApk(PackageInstallerSession session) {
            return sessionContains(session, $$Lambda$StagingManager$j1RpPmMrsxcldNpyt2n2wcJbVA0.INSTANCE);
        }

        private void resumeSession(PackageInstallerSession session) {
            boolean hasApex = sessionContainsApex(session);
            if (hasApex) {
                ApexSessionInfo apexSessionInfo = this.mApexManager.getStagedSessionInfo(session.sessionId);
                if (apexSessionInfo == null) {
                    session.setStagedSessionFailed(2, "apexd did not know anything about a staged session supposed to beactivated");
                    return;
                } else if (isApexSessionFailed(apexSessionInfo)) {
                    session.setStagedSessionFailed(2, "APEX activation failed. Check logcat messages from apexd for more information.");
                    return;
                } else if (apexSessionInfo.isVerified) {
                    Slog.d(TAG, "Found pending staged session " + session.sessionId + " still to be verified, resuming pre-reboot verification");
                    this.mBgHandler.post(new Runnable(session) {
                        private final /* synthetic */ PackageInstallerSession f$1;

                        {
                            this.f$1 = r2;
                        }

                        public final void run() {
                            StagingManager.this.lambda$resumeSession$7$StagingManager(this.f$1);
                        }
                    });
                    return;
                } else if (!apexSessionInfo.isActivated && !apexSessionInfo.isSuccess) {
                    Slog.w(TAG, "Staged session " + session.sessionId + " scheduled to be applied at boot didn't activate nor fail. This usually means that apexd will retry at next reboot.");
                    return;
                }
            }
            if (!installApksInSession(session, false)) {
                session.setStagedSessionFailed(2, "Staged installation of APKs failed. Check logcat messages formore information.");
                if (hasApex) {
                    if (!this.mApexManager.abortActiveSession()) {
                        Slog.e(TAG, "Failed to abort APEXd session");
                        return;
                    }
                    Slog.e(TAG, "Successfully aborted apexd session. Rebooting device in order to revert to the previous state of APEXd.");
                    this.mPowerManager.reboot((String) null);
                    return;
                }
                return;
            }
            session.setStagedSessionApplied();
            if (hasApex) {
                this.mApexManager.markStagedSessionSuccessful(session.sessionId);
            }
        }

        private List<String> findAPKsInDir(File stageDir) {
            List<String> ret = new ArrayList<>();
            if (stageDir != null && stageDir.exists()) {
                for (File file : stageDir.listFiles()) {
                    if (file.getAbsolutePath().toLowerCase().endsWith(".apk")) {
                        ret.add(file.getAbsolutePath());
                    }
                }
            }
            return ret;
        }

        private PackageInstallerSession createAndWriteApkSession(PackageInstallerSession originalSession, boolean preReboot) {
            PackageInstallerSession packageInstallerSession = originalSession;
            if (packageInstallerSession.stageDir == null) {
                Slog.wtf(TAG, "Attempting to install a staged APK session with no staging dir");
                return null;
            }
            List<String> apkFilePaths = findAPKsInDir(packageInstallerSession.stageDir);
            if (apkFilePaths.isEmpty()) {
                Slog.w(TAG, "Can't find staged APK in " + packageInstallerSession.stageDir.getAbsolutePath());
                return null;
            }
            PackageInstaller.SessionParams params = packageInstallerSession.params.copy();
            params.isStaged = false;
            params.installFlags |= DumpState.DUMP_COMPILER_STATS;
            if (preReboot) {
                params.installFlags &= -262145;
                params.installFlags |= DumpState.DUMP_VOLUMES;
            } else {
                params.installFlags |= DumpState.DUMP_FROZEN;
            }
            PackageInstallerSession apkSession = this.mPi.getSession(this.mPi.createSession(params, originalSession.getInstallerPackageName(), 0));
            try {
                apkSession.open();
                for (String apkFilePath : apkFilePaths) {
                    File apkFile = new File(apkFilePath);
                    ParcelFileDescriptor pfd = ParcelFileDescriptor.open(apkFile, 268435456);
                    long sizeBytes = pfd.getStatSize();
                    if (sizeBytes < 0) {
                        Slog.e(TAG, "Unable to get size of: " + apkFilePath);
                        return null;
                    }
                    String str = apkFilePath;
                    File file = apkFile;
                    apkSession.write(apkFile.getName(), 0, sizeBytes, pfd);
                }
                return apkSession;
            } catch (IOException e) {
                Slog.e(TAG, "Failure to install APK staged session " + packageInstallerSession.sessionId, e);
                return null;
            }
        }

        private boolean commitApkSession(PackageInstallerSession apkSession, int originalSessionId, boolean preReboot) {
            if (!preReboot && (apkSession.params.installFlags & DumpState.DUMP_DOMAIN_PREFERRED) != 0) {
                try {
                    IRollbackManager.Stub.asInterface(ServiceManager.getService("rollback")).notifyStagedApkSession(originalSessionId, apkSession.sessionId);
                } catch (RemoteException e) {
                }
            }
            LocalIntentReceiver receiver = new LocalIntentReceiver();
            apkSession.commit(receiver.getIntentSender(), false);
            Intent result = receiver.getResult();
            if (result.getIntExtra("android.content.pm.extra.STATUS", 1) == 0) {
                return true;
            }
            Slog.e(TAG, "Failure to install APK staged session " + originalSessionId + " [" + result.getStringExtra("android.content.pm.extra.STATUS_MESSAGE") + "]");
            return false;
        }

        private boolean installApksInSession(PackageInstallerSession session, boolean preReboot) {
            List<PackageInstallerSession> childSessions;
            if (!session.isMultiPackage() && !isApexSession(session)) {
                PackageInstallerSession apkSession = createAndWriteApkSession(session, preReboot);
                if (apkSession == null) {
                    return false;
                }
                return commitApkSession(apkSession, session.sessionId, preReboot);
            } else if (!session.isMultiPackage()) {
                return true;
            } else {
                synchronized (this.mStagedSessions) {
                    childSessions = (List) Arrays.stream(session.getChildSessionIds()).mapToObj(new IntFunction() {
                        public final Object apply(int i) {
                            return StagingManager.this.lambda$installApksInSession$8$StagingManager(i);
                        }
                    }).filter($$Lambda$StagingManager$W4xn2etqxcpB6KS2WmEUcUMWK4M.INSTANCE).collect(Collectors.toList());
                }
                if (childSessions.isEmpty()) {
                    return true;
                }
                PackageInstaller.SessionParams params = session.params.copy();
                params.isStaged = false;
                if (preReboot) {
                    params.installFlags &= -262145;
                }
                PackageInstallerSession apkParentSession = this.mPi.getSession(this.mPi.createSession(params, session.getInstallerPackageName(), 0));
                try {
                    apkParentSession.open();
                    for (PackageInstallerSession sessionToClone : childSessions) {
                        PackageInstallerSession apkChildSession = createAndWriteApkSession(sessionToClone, preReboot);
                        if (apkChildSession == null) {
                            return false;
                        }
                        try {
                            apkParentSession.addChildSessionId(apkChildSession.sessionId);
                        } catch (IllegalStateException e) {
                            Slog.e(TAG, "Failed to add a child session for installing the APK files", e);
                            return false;
                        }
                    }
                    return commitApkSession(apkParentSession, session.sessionId, preReboot);
                } catch (IOException e2) {
                    Slog.e(TAG, "Unable to prepare multi-package session for staged session " + session.sessionId);
                    return false;
                }
            }
        }

        public /* synthetic */ PackageInstallerSession lambda$installApksInSession$8$StagingManager(int i) {
            return this.mStagedSessions.get(i);
        }

        static /* synthetic */ boolean lambda$installApksInSession$9(PackageInstallerSession childSession) {
            return !isApexSession(childSession);
        }

        /* access modifiers changed from: package-private */
        public void commitSession(PackageInstallerSession session) {
            updateStoredSession(session);
            this.mBgHandler.post(new Runnable(session) {
                private final /* synthetic */ PackageInstallerSession f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    StagingManager.this.lambda$commitSession$10$StagingManager(this.f$1);
                }
            });
        }

        /* access modifiers changed from: package-private */
        public PackageInstallerSession getActiveSession() {
            synchronized (this.mStagedSessions) {
                for (int i = 0; i < this.mStagedSessions.size(); i++) {
                    PackageInstallerSession session = this.mStagedSessions.valueAt(i);
                    if (session.isCommitted()) {
                        if (!session.hasParentSessionId()) {
                            if (!session.isStagedSessionApplied() && !session.isStagedSessionFailed()) {
                                return session;
                            }
                        }
                    }
                }
                return null;
            }
        }

        /* access modifiers changed from: package-private */
        public void createSession(PackageInstallerSession sessionInfo) {
            synchronized (this.mStagedSessions) {
                this.mStagedSessions.append(sessionInfo.sessionId, sessionInfo);
            }
        }

        /* access modifiers changed from: package-private */
        public void abortSession(PackageInstallerSession session) {
            synchronized (this.mStagedSessions) {
                this.mStagedSessions.remove(session.sessionId);
            }
        }

        /* access modifiers changed from: package-private */
        public void abortCommittedSession(PackageInstallerSession session) {
            if (session.isStagedSessionApplied()) {
                Slog.w(TAG, "Cannot abort applied session : " + session.sessionId);
                return;
            }
            abortSession(session);
            if (sessionContainsApex(session)) {
                ApexSessionInfo apexSession = this.mApexManager.getStagedSessionInfo(session.sessionId);
                if (apexSession == null || isApexSessionFinalized(apexSession)) {
                    Slog.w(TAG, "Cannot abort session because it is not active or APEXD is not reachable");
                } else {
                    this.mApexManager.abortActiveSession();
                }
            }
        }

        private boolean isApexSessionFinalized(ApexSessionInfo session) {
            return session.isUnknown || session.isActivationFailed || session.isSuccess || session.isRolledBack;
        }

        private static boolean isApexSessionFailed(ApexSessionInfo apexSessionInfo) {
            return apexSessionInfo.isActivationFailed || apexSessionInfo.isUnknown || apexSessionInfo.isRolledBack || apexSessionInfo.isRollbackInProgress || apexSessionInfo.isRollbackFailed;
        }

        @GuardedBy({"mStagedSessions"})
        private boolean isMultiPackageSessionComplete(PackageInstallerSession session) {
            if (session.isMultiPackage()) {
                for (int childSession : session.getChildSessionIds()) {
                    if (this.mStagedSessions.get(childSession) == null) {
                        return false;
                    }
                }
                return true;
            } else if (session.hasParentSessionId()) {
                PackageInstallerSession parent = this.mStagedSessions.get(session.getParentSessionId());
                if (parent == null) {
                    return false;
                }
                return isMultiPackageSessionComplete(parent);
            } else {
                Slog.wtf(TAG, "Attempting to restore an invalid multi-package session.");
                return false;
            }
        }

        /* access modifiers changed from: package-private */
        /* JADX WARNING: Code restructure failed: missing block: B:15:0x0033, code lost:
            checkStateAndResume(r0);
         */
        /* JADX WARNING: Code restructure failed: missing block: B:16:0x0036, code lost:
            return;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void restoreSession(com.android.server.pm.PackageInstallerSession r5) {
            /*
                r4 = this;
                r0 = r5
                android.util.SparseArray<com.android.server.pm.PackageInstallerSession> r1 = r4.mStagedSessions
                monitor-enter(r1)
                android.util.SparseArray<com.android.server.pm.PackageInstallerSession> r2 = r4.mStagedSessions     // Catch:{ all -> 0x0037 }
                int r3 = r5.sessionId     // Catch:{ all -> 0x0037 }
                r2.append(r3, r5)     // Catch:{ all -> 0x0037 }
                boolean r2 = r5.isMultiPackage()     // Catch:{ all -> 0x0037 }
                if (r2 != 0) goto L_0x0017
                boolean r2 = r5.hasParentSessionId()     // Catch:{ all -> 0x0037 }
                if (r2 == 0) goto L_0x0032
            L_0x0017:
                boolean r2 = r4.isMultiPackageSessionComplete(r5)     // Catch:{ all -> 0x0037 }
                if (r2 != 0) goto L_0x001f
                monitor-exit(r1)     // Catch:{ all -> 0x0037 }
                return
            L_0x001f:
                boolean r2 = r5.hasParentSessionId()     // Catch:{ all -> 0x0037 }
                if (r2 == 0) goto L_0x0032
                android.util.SparseArray<com.android.server.pm.PackageInstallerSession> r2 = r4.mStagedSessions     // Catch:{ all -> 0x0037 }
                int r3 = r5.getParentSessionId()     // Catch:{ all -> 0x0037 }
                java.lang.Object r2 = r2.get(r3)     // Catch:{ all -> 0x0037 }
                com.android.server.pm.PackageInstallerSession r2 = (com.android.server.pm.PackageInstallerSession) r2     // Catch:{ all -> 0x0037 }
                r0 = r2
            L_0x0032:
                monitor-exit(r1)     // Catch:{ all -> 0x0037 }
                r4.checkStateAndResume(r0)
                return
            L_0x0037:
                r2 = move-exception
                monitor-exit(r1)     // Catch:{ all -> 0x0037 }
                throw r2
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.server.pm.StagingManager.restoreSession(com.android.server.pm.PackageInstallerSession):void");
        }

        private void checkStateAndResume(PackageInstallerSession session) {
            if (!session.isCommitted() || session.isStagedSessionFailed() || session.isStagedSessionApplied()) {
                return;
            }
            if (!session.isStagedSessionReady()) {
                this.mBgHandler.post(new Runnable(session) {
                    private final /* synthetic */ PackageInstallerSession f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final void run() {
                        StagingManager.this.lambda$checkStateAndResume$11$StagingManager(this.f$1);
                    }
                });
            } else {
                resumeSession(session);
            }
        }

        private static class LocalIntentReceiver {
            private IIntentSender.Stub mLocalSender;
            /* access modifiers changed from: private */
            public final LinkedBlockingQueue<Intent> mResult;

            private LocalIntentReceiver() {
                this.mResult = new LinkedBlockingQueue<>();
                this.mLocalSender = new IIntentSender.Stub() {
                    public void send(int code, Intent intent, String resolvedType, IBinder whitelistToken, IIntentReceiver finishedReceiver, String requiredPermission, Bundle options) {
                        try {
                            LocalIntentReceiver.this.mResult.offer(intent, 5, TimeUnit.SECONDS);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                };
            }

            public IntentSender getIntentSender() {
                return new IntentSender(this.mLocalSender);
            }

            public Intent getResult() {
                try {
                    return this.mResult.take();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
