package com.android.server.pm;

import android.app.ActivityManager;
import android.app.AppGlobals;
import android.app.AppOpsManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PackageDeleteObserver;
import android.app.PackageInstallObserver;
import android.app.admin.DevicePolicyEventLogger;
import android.app.admin.DevicePolicyManagerInternal;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.IPackageInstaller;
import android.content.pm.IPackageInstallerCallback;
import android.content.pm.IPackageInstallerSession;
import android.content.pm.PackageInfo;
import android.content.pm.PackageInstaller;
import android.content.pm.PackageManager;
import android.content.pm.ParceledListSlice;
import android.content.pm.VersionedPackage;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.os.SELinux;
import android.os.storage.StorageManager;
import android.system.ErrnoException;
import android.system.Os;
import android.text.TextUtils;
import android.util.ArraySet;
import android.util.AtomicFile;
import android.util.ExceptionUtils;
import android.util.Slog;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import android.util.SparseIntArray;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.notification.SystemNotificationChannels;
import com.android.internal.util.FastXmlSerializer;
import com.android.internal.util.ImageUtils;
import com.android.internal.util.IndentingPrintWriter;
import com.android.server.IoThread;
import com.android.server.LocalServices;
import com.android.server.am.BroadcastQueueInjector;
import com.android.server.pm.permission.PermissionManagerServiceInternal;
import java.io.CharArrayWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.function.IntPredicate;
import org.xmlpull.v1.XmlSerializer;

public class PackageInstallerService extends IPackageInstaller.Stub implements PackageSessionProvider {
    private static final boolean LOGD = false;
    private static final long MAX_ACTIVE_SESSIONS = 1024;
    private static final long MAX_AGE_MILLIS = 259200000;
    private static final long MAX_HISTORICAL_SESSIONS = 1048576;
    private static final long MAX_TIME_SINCE_UPDATE_MILLIS = 604800000;
    private static final String TAG = "PackageInstaller";
    private static final String TAG_SESSIONS = "sessions";
    private static final FilenameFilter sStageFilter = new FilenameFilter() {
        public boolean accept(File dir, String name) {
            return PackageInstallerService.isStageName(name);
        }
    };
    @GuardedBy({"mSessions"})
    private final SparseBooleanArray mAllocatedSessions = new SparseBooleanArray();
    private final ApexManager mApexManager;
    private AppOpsManager mAppOps;
    /* access modifiers changed from: private */
    public final Callbacks mCallbacks;
    private final Context mContext;
    @GuardedBy({"mSessions"})
    private final List<String> mHistoricalSessions = new ArrayList();
    @GuardedBy({"mSessions"})
    private final SparseIntArray mHistoricalSessionsByInstaller = new SparseIntArray();
    /* access modifiers changed from: private */
    public final Handler mInstallHandler;
    private final HandlerThread mInstallThread;
    private final InternalCallback mInternalCallback = new InternalCallback();
    @GuardedBy({"mSessions"})
    private final SparseBooleanArray mLegacySessions = new SparseBooleanArray();
    /* access modifiers changed from: private */
    public volatile boolean mOkToSendBroadcasts = false;
    private final PermissionManagerServiceInternal mPermissionManager;
    /* access modifiers changed from: private */
    public final PackageManagerService mPm;
    private final Random mRandom = new SecureRandom();
    /* access modifiers changed from: private */
    @GuardedBy({"mSessions"})
    public final SparseArray<PackageInstallerSession> mSessions = new SparseArray<>();
    private final File mSessionsDir;
    private final AtomicFile mSessionsFile;
    /* access modifiers changed from: private */
    public final StagingManager mStagingManager;

    public PackageInstallerService(Context context, PackageManagerService pm, ApexManager am) {
        this.mContext = context;
        this.mPm = pm;
        this.mPermissionManager = (PermissionManagerServiceInternal) LocalServices.getService(PermissionManagerServiceInternal.class);
        this.mInstallThread = new HandlerThread(TAG);
        this.mInstallThread.start();
        this.mInstallHandler = new Handler(this.mInstallThread.getLooper());
        this.mCallbacks = new Callbacks(this.mInstallThread.getLooper());
        this.mSessionsFile = new AtomicFile(new File(Environment.getDataSystemDirectory(), "install_sessions.xml"), "package-session");
        this.mSessionsDir = new File(Environment.getDataSystemDirectory(), "install_sessions");
        this.mSessionsDir.mkdirs();
        this.mApexManager = am;
        this.mStagingManager = new StagingManager(this, am, context);
    }

    /* access modifiers changed from: package-private */
    public boolean okToSendBroadcasts() {
        return this.mOkToSendBroadcasts;
    }

    public void systemReady() {
        this.mAppOps = (AppOpsManager) this.mContext.getSystemService(AppOpsManager.class);
        synchronized (this.mSessions) {
            readSessionsLocked();
            reconcileStagesLocked(StorageManager.UUID_PRIVATE_INTERNAL);
            ArraySet<File> unclaimedIcons = newArraySet(this.mSessionsDir.listFiles());
            for (int i = 0; i < this.mSessions.size(); i++) {
                unclaimedIcons.remove(buildAppIconFile(this.mSessions.valueAt(i).sessionId));
            }
            Iterator<File> it = unclaimedIcons.iterator();
            while (it.hasNext()) {
                File icon = it.next();
                Slog.w(TAG, "Deleting orphan icon " + icon);
                icon.delete();
            }
            writeSessionsLocked();
        }
    }

    /* access modifiers changed from: package-private */
    public void restoreAndApplyStagedSessionIfNeeded() {
        List<PackageInstallerSession> stagedSessionsToRestore = new ArrayList<>();
        synchronized (this.mSessions) {
            for (int i = 0; i < this.mSessions.size(); i++) {
                PackageInstallerSession session = this.mSessions.valueAt(i);
                if (session.isStaged()) {
                    stagedSessionsToRestore.add(session);
                }
            }
        }
        for (PackageInstallerSession session2 : stagedSessionsToRestore) {
            this.mStagingManager.restoreSession(session2);
        }
        this.mOkToSendBroadcasts = true;
    }

    @GuardedBy({"mSessions"})
    private void reconcileStagesLocked(String volumeUuid) {
        ArraySet<File> unclaimedStages = newArraySet(getTmpSessionDir(volumeUuid).listFiles(sStageFilter));
        for (int i = 0; i < this.mSessions.size(); i++) {
            unclaimedStages.remove(this.mSessions.valueAt(i).stageDir);
        }
        Iterator<File> it = unclaimedStages.iterator();
        while (it.hasNext()) {
            File stage = it.next();
            Slog.w(TAG, "Deleting orphan stage " + stage);
            synchronized (this.mPm.mInstallLock) {
                this.mPm.removeCodePathLI(stage);
            }
        }
    }

    public void onPrivateVolumeMounted(String volumeUuid) {
        synchronized (this.mSessions) {
            reconcileStagesLocked(volumeUuid);
        }
    }

    public static boolean isStageName(String name) {
        boolean isFile = name.startsWith("vmdl") && name.endsWith(".tmp");
        boolean isContainer = name.startsWith("smdl") && name.endsWith(".tmp");
        boolean isLegacyContainer = name.startsWith("smdl2tmp");
        if (isFile || isContainer || isLegacyContainer) {
            return true;
        }
        return false;
    }

    /* Debug info: failed to restart local var, previous not found, register: 4 */
    @Deprecated
    public File allocateStageDirLegacy(String volumeUuid, boolean isEphemeral) throws IOException {
        File sessionStageDir;
        synchronized (this.mSessions) {
            try {
                int sessionId = allocateSessionIdLocked();
                this.mLegacySessions.put(sessionId, true);
                sessionStageDir = buildTmpSessionDir(sessionId, volumeUuid);
                prepareStageDir(sessionStageDir);
            } catch (IllegalStateException e) {
                throw new IOException(e);
            } catch (Throwable th) {
                throw th;
            }
        }
        return sessionStageDir;
    }

    @Deprecated
    public String allocateExternalStageCidLegacy() {
        String str;
        synchronized (this.mSessions) {
            int sessionId = allocateSessionIdLocked();
            this.mLegacySessions.put(sessionId, true);
            str = "smdl" + sessionId + ".tmp";
        }
        return str;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:28:0x00a8, code lost:
        r3 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:29:0x00a9, code lost:
        android.util.Slog.e(TAG, "Could not read session", r3);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:30:0x00b3, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:31:0x00b5, code lost:
        r2 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:34:?, code lost:
        android.util.Slog.wtf(TAG, "Failed reading install sessions", r2);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:35:0x00bd, code lost:
        libcore.io.IoUtils.closeQuietly(r1);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:36:0x00c0, code lost:
        throw r0;
     */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Removed duplicated region for block: B:31:0x00b5 A[ExcHandler: IOException | XmlPullParserException (r2v5 'e' java.lang.Exception A[CUSTOM_DECLARE]), PHI: r1 
      PHI: (r1v4 'fis' java.io.FileInputStream) = (r1v1 'fis' java.io.FileInputStream), (r1v5 'fis' java.io.FileInputStream), (r1v5 'fis' java.io.FileInputStream) binds: [B:1:0x0008, B:9:0x0035, B:11:0x004c] A[DONT_GENERATE, DONT_INLINE], Splitter:B:1:0x0008] */
    @com.android.internal.annotations.GuardedBy({"mSessions"})
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void readSessionsLocked() {
        /*
            r14 = this;
            java.lang.String r0 = "PackageInstaller"
            android.util.SparseArray<com.android.server.pm.PackageInstallerSession> r1 = r14.mSessions
            r1.clear()
            r1 = 0
            android.util.AtomicFile r2 = r14.mSessionsFile     // Catch:{ FileNotFoundException -> 0x00c1, IOException | XmlPullParserException -> 0x00b5 }
            java.io.FileInputStream r2 = r2.openRead()     // Catch:{ FileNotFoundException -> 0x00c1, IOException | XmlPullParserException -> 0x00b5 }
            r1 = r2
            org.xmlpull.v1.XmlPullParser r2 = android.util.Xml.newPullParser()     // Catch:{ FileNotFoundException -> 0x00c1, IOException | XmlPullParserException -> 0x00b5 }
            java.nio.charset.Charset r3 = java.nio.charset.StandardCharsets.UTF_8     // Catch:{ FileNotFoundException -> 0x00c1, IOException | XmlPullParserException -> 0x00b5 }
            java.lang.String r3 = r3.name()     // Catch:{ FileNotFoundException -> 0x00c1, IOException | XmlPullParserException -> 0x00b5 }
            r2.setInput(r1, r3)     // Catch:{ FileNotFoundException -> 0x00c1, IOException | XmlPullParserException -> 0x00b5 }
        L_0x001c:
            int r3 = r2.next()     // Catch:{ FileNotFoundException -> 0x00c1, IOException | XmlPullParserException -> 0x00b5 }
            r11 = r3
            r12 = 1
            if (r3 == r12) goto L_0x00b2
            r3 = 2
            if (r11 != r3) goto L_0x001c
            java.lang.String r3 = r2.getName()     // Catch:{ FileNotFoundException -> 0x00c1, IOException | XmlPullParserException -> 0x00b5 }
            r13 = r3
            java.lang.String r3 = "session"
            boolean r3 = r3.equals(r13)     // Catch:{ FileNotFoundException -> 0x00c1, IOException | XmlPullParserException -> 0x00b5 }
            if (r3 == 0) goto L_0x00b0
            com.android.server.pm.PackageInstallerService$InternalCallback r4 = r14.mInternalCallback     // Catch:{ Exception -> 0x00a8, IOException | XmlPullParserException -> 0x00b5 }
            android.content.Context r5 = r14.mContext     // Catch:{ Exception -> 0x00a8, IOException | XmlPullParserException -> 0x00b5 }
            com.android.server.pm.PackageManagerService r6 = r14.mPm     // Catch:{ Exception -> 0x00a8, IOException | XmlPullParserException -> 0x00b5 }
            android.os.HandlerThread r3 = r14.mInstallThread     // Catch:{ Exception -> 0x00a8, IOException | XmlPullParserException -> 0x00b5 }
            android.os.Looper r7 = r3.getLooper()     // Catch:{ Exception -> 0x00a8, IOException | XmlPullParserException -> 0x00b5 }
            com.android.server.pm.StagingManager r8 = r14.mStagingManager     // Catch:{ Exception -> 0x00a8, IOException | XmlPullParserException -> 0x00b5 }
            java.io.File r9 = r14.mSessionsDir     // Catch:{ Exception -> 0x00a8, IOException | XmlPullParserException -> 0x00b5 }
            r3 = r2
            r10 = r14
            com.android.server.pm.PackageInstallerSession r3 = com.android.server.pm.PackageInstallerSession.readFromXml(r3, r4, r5, r6, r7, r8, r9, r10)     // Catch:{ Exception -> 0x00a8, IOException | XmlPullParserException -> 0x00b5 }
            long r4 = java.lang.System.currentTimeMillis()     // Catch:{ FileNotFoundException -> 0x00c1, IOException | XmlPullParserException -> 0x00b5 }
            long r6 = r3.createdMillis     // Catch:{ FileNotFoundException -> 0x00c1, IOException | XmlPullParserException -> 0x00b5 }
            long r4 = r4 - r6
            long r6 = java.lang.System.currentTimeMillis()     // Catch:{ FileNotFoundException -> 0x00c1, IOException | XmlPullParserException -> 0x00b5 }
            long r8 = r3.getUpdatedMillis()     // Catch:{ FileNotFoundException -> 0x00c1, IOException | XmlPullParserException -> 0x00b5 }
            long r6 = r6 - r8
            boolean r8 = r3.isStaged()     // Catch:{ FileNotFoundException -> 0x00c1, IOException | XmlPullParserException -> 0x00b5 }
            if (r8 == 0) goto L_0x0073
            r8 = 604800000(0x240c8400, double:2.988109026E-315)
            int r8 = (r6 > r8 ? 1 : (r6 == r8 ? 0 : -1))
            if (r8 < 0) goto L_0x0071
            boolean r8 = r3.isStagedAndInTerminalState()     // Catch:{ FileNotFoundException -> 0x00c1, IOException | XmlPullParserException -> 0x00b5 }
            if (r8 == 0) goto L_0x0071
            r8 = 0
            goto L_0x0093
        L_0x0071:
            r8 = 1
            goto L_0x0093
        L_0x0073:
            r8 = 259200000(0xf731400, double:1.280618154E-315)
            int r8 = (r4 > r8 ? 1 : (r4 == r8 ? 0 : -1))
            if (r8 < 0) goto L_0x0092
            java.lang.StringBuilder r8 = new java.lang.StringBuilder     // Catch:{ FileNotFoundException -> 0x00c1, IOException | XmlPullParserException -> 0x00b5 }
            r8.<init>()     // Catch:{ FileNotFoundException -> 0x00c1, IOException | XmlPullParserException -> 0x00b5 }
            java.lang.String r9 = "Abandoning old session created at "
            r8.append(r9)     // Catch:{ FileNotFoundException -> 0x00c1, IOException | XmlPullParserException -> 0x00b5 }
            long r9 = r3.createdMillis     // Catch:{ FileNotFoundException -> 0x00c1, IOException | XmlPullParserException -> 0x00b5 }
            r8.append(r9)     // Catch:{ FileNotFoundException -> 0x00c1, IOException | XmlPullParserException -> 0x00b5 }
            java.lang.String r8 = r8.toString()     // Catch:{ FileNotFoundException -> 0x00c1, IOException | XmlPullParserException -> 0x00b5 }
            android.util.Slog.w(r0, r8)     // Catch:{ FileNotFoundException -> 0x00c1, IOException | XmlPullParserException -> 0x00b5 }
            r8 = 0
            goto L_0x0093
        L_0x0092:
            r8 = 1
        L_0x0093:
            if (r8 == 0) goto L_0x009d
            android.util.SparseArray<com.android.server.pm.PackageInstallerSession> r9 = r14.mSessions     // Catch:{ FileNotFoundException -> 0x00c1, IOException | XmlPullParserException -> 0x00b5 }
            int r10 = r3.sessionId     // Catch:{ FileNotFoundException -> 0x00c1, IOException | XmlPullParserException -> 0x00b5 }
            r9.put(r10, r3)     // Catch:{ FileNotFoundException -> 0x00c1, IOException | XmlPullParserException -> 0x00b5 }
            goto L_0x00a0
        L_0x009d:
            r14.addHistoricalSessionLocked(r3)     // Catch:{ FileNotFoundException -> 0x00c1, IOException | XmlPullParserException -> 0x00b5 }
        L_0x00a0:
            android.util.SparseBooleanArray r9 = r14.mAllocatedSessions     // Catch:{ FileNotFoundException -> 0x00c1, IOException | XmlPullParserException -> 0x00b5 }
            int r10 = r3.sessionId     // Catch:{ FileNotFoundException -> 0x00c1, IOException | XmlPullParserException -> 0x00b5 }
            r9.put(r10, r12)     // Catch:{ FileNotFoundException -> 0x00c1, IOException | XmlPullParserException -> 0x00b5 }
            goto L_0x00b0
        L_0x00a8:
            r3 = move-exception
            java.lang.String r4 = "Could not read session"
            android.util.Slog.e(r0, r4, r3)     // Catch:{ FileNotFoundException -> 0x00c1, IOException | XmlPullParserException -> 0x00b5 }
            goto L_0x001c
        L_0x00b0:
            goto L_0x001c
        L_0x00b2:
            goto L_0x00c2
        L_0x00b3:
            r0 = move-exception
            goto L_0x00bd
        L_0x00b5:
            r2 = move-exception
            java.lang.String r3 = "Failed reading install sessions"
            android.util.Slog.wtf(r0, r3, r2)     // Catch:{ all -> 0x00b3 }
            goto L_0x00c3
        L_0x00bd:
            libcore.io.IoUtils.closeQuietly(r1)
            throw r0
        L_0x00c1:
            r0 = move-exception
        L_0x00c2:
        L_0x00c3:
            libcore.io.IoUtils.closeQuietly(r1)
            r0 = 0
        L_0x00c8:
            android.util.SparseArray<com.android.server.pm.PackageInstallerSession> r2 = r14.mSessions
            int r2 = r2.size()
            if (r0 >= r2) goto L_0x00de
            android.util.SparseArray<com.android.server.pm.PackageInstallerSession> r2 = r14.mSessions
            java.lang.Object r2 = r2.valueAt(r0)
            com.android.server.pm.PackageInstallerSession r2 = (com.android.server.pm.PackageInstallerSession) r2
            r2.sealAndValidateIfNecessary()
            int r0 = r0 + 1
            goto L_0x00c8
        L_0x00de:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.pm.PackageInstallerService.readSessionsLocked():void");
    }

    /* access modifiers changed from: private */
    @GuardedBy({"mSessions"})
    public void addHistoricalSessionLocked(PackageInstallerSession session) {
        CharArrayWriter writer = new CharArrayWriter();
        session.dump(new IndentingPrintWriter(writer, "    "));
        this.mHistoricalSessions.add(writer.toString());
        int installerUid = session.getInstallerUid();
        SparseIntArray sparseIntArray = this.mHistoricalSessionsByInstaller;
        sparseIntArray.put(installerUid, sparseIntArray.get(installerUid) + 1);
    }

    /* access modifiers changed from: private */
    @GuardedBy({"mSessions"})
    public void writeSessionsLocked() {
        try {
            FileOutputStream fos = this.mSessionsFile.startWrite();
            XmlSerializer out = new FastXmlSerializer();
            out.setOutput(fos, StandardCharsets.UTF_8.name());
            out.startDocument((String) null, true);
            out.startTag((String) null, TAG_SESSIONS);
            int size = this.mSessions.size();
            for (int i = 0; i < size; i++) {
                this.mSessions.valueAt(i).write(out, this.mSessionsDir);
            }
            out.endTag((String) null, TAG_SESSIONS);
            out.endDocument();
            this.mSessionsFile.finishWrite(fos);
        } catch (IOException e) {
            if (0 != 0) {
                this.mSessionsFile.failWrite((FileOutputStream) null);
            }
        }
    }

    /* access modifiers changed from: private */
    public File buildAppIconFile(int sessionId) {
        File file = this.mSessionsDir;
        return new File(file, "app_icon." + sessionId + ".png");
    }

    /* access modifiers changed from: private */
    public void writeSessionsAsync() {
        IoThread.getHandler().post(new Runnable() {
            public void run() {
                synchronized (PackageInstallerService.this.mSessions) {
                    PackageInstallerService.this.writeSessionsLocked();
                }
            }
        });
    }

    public int createSession(PackageInstaller.SessionParams params, String installerPackageName, int userId) {
        try {
            return createSessionInternal(params, installerPackageName, userId);
        } catch (IOException e) {
            throw ExceptionUtils.wrap(e);
        }
    }

    /* Debug info: failed to restart local var, previous not found, register: 34 */
    /*  JADX ERROR: IndexOutOfBoundsException in pass: RegionMakerVisitor
        java.lang.IndexOutOfBoundsException: Index: 0, Size: 0
        	at java.util.ArrayList.rangeCheck(ArrayList.java:657)
        	at java.util.ArrayList.get(ArrayList.java:433)
        	at jadx.core.dex.nodes.InsnNode.getArg(InsnNode.java:101)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:611)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
        	at jadx.core.dex.visitors.regions.RegionMaker.processMonitorEnter(RegionMaker.java:561)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverse(RegionMaker.java:133)
        	at jadx.core.dex.visitors.regions.RegionMaker.makeRegion(RegionMaker.java:86)
        	at jadx.core.dex.visitors.regions.RegionMaker.processIf(RegionMaker.java:693)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverse(RegionMaker.java:123)
        	at jadx.core.dex.visitors.regions.RegionMaker.makeRegion(RegionMaker.java:86)
        	at jadx.core.dex.visitors.regions.RegionMaker.processIf(RegionMaker.java:693)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverse(RegionMaker.java:123)
        	at jadx.core.dex.visitors.regions.RegionMaker.makeRegion(RegionMaker.java:86)
        	at jadx.core.dex.visitors.regions.RegionMakerVisitor.visit(RegionMakerVisitor.java:49)
        */
    /* JADX WARNING: Removed duplicated region for block: B:23:0x008c  */
    /* JADX WARNING: Removed duplicated region for block: B:24:0x009b  */
    /* JADX WARNING: Removed duplicated region for block: B:27:0x00a6  */
    /* JADX WARNING: Removed duplicated region for block: B:30:0x00b6  */
    /* JADX WARNING: Removed duplicated region for block: B:31:0x00b8  */
    /* JADX WARNING: Removed duplicated region for block: B:37:0x00cc  */
    /* JADX WARNING: Removed duplicated region for block: B:47:0x00ed  */
    private int createSessionInternal(android.content.pm.PackageInstaller.SessionParams r35, java.lang.String r36, int r37) throws java.io.IOException {
        /*
            r34 = this;
            r15 = r34
            r13 = r35
            r0 = 90
            android.util.SeempLog.record(r0)
            int r14 = android.os.Binder.getCallingUid()
            com.android.server.pm.permission.PermissionManagerServiceInternal r1 = r15.mPermissionManager
            r4 = 1
            r5 = 1
            java.lang.String r6 = "createSession"
            r2 = r14
            r3 = r37
            r1.enforceCrossUserPermission(r2, r3, r4, r5, r6)
            com.android.server.pm.PackageManagerService r0 = r15.mPm
            boolean r0 = r0.mSystemReady
            if (r0 == 0) goto L_0x02a7
            com.android.server.pm.PackageManagerService r0 = r15.mPm
            java.lang.String r1 = "no_install_apps"
            r12 = r37
            boolean r0 = r0.isUserRestricted(r12, r1)
            if (r0 != 0) goto L_0x029f
            r0 = 2000(0x7d0, float:2.803E-42)
            r1 = 2
            if (r14 == r0) goto L_0x0079
            if (r14 != 0) goto L_0x0036
            r11 = r36
            goto L_0x007b
        L_0x0036:
            android.content.Context r0 = r15.mContext
            java.lang.String r2 = "android.permission.INSTALL_PACKAGES"
            int r0 = r0.checkCallingOrSelfPermission(r2)
            if (r0 == 0) goto L_0x0048
            android.app.AppOpsManager r0 = r15.mAppOps
            r11 = r36
            r0.checkPackage(r14, r11)
            goto L_0x004a
        L_0x0048:
            r11 = r36
        L_0x004a:
            int r0 = r13.installFlags
            r0 = r0 & -33
            r13.installFlags = r0
            int r0 = r13.installFlags
            r0 = r0 & -65
            r13.installFlags = r0
            int r0 = r13.installFlags
            r0 = r0 & -5
            r13.installFlags = r0
            int r0 = r13.installFlags
            r0 = r0 | r1
            r13.installFlags = r0
            int r0 = r13.installFlags
            r2 = 65536(0x10000, float:9.18355E-41)
            r0 = r0 & r2
            if (r0 == 0) goto L_0x0081
            com.android.server.pm.PackageManagerService r0 = r15.mPm
            boolean r0 = r0.isCallerVerifier(r14)
            if (r0 != 0) goto L_0x0081
            int r0 = r13.installFlags
            r2 = -65537(0xfffffffffffeffff, float:NaN)
            r0 = r0 & r2
            r13.installFlags = r0
            goto L_0x0081
        L_0x0079:
            r11 = r36
        L_0x007b:
            int r0 = r13.installFlags
            r0 = r0 | 32
            r13.installFlags = r0
        L_0x0081:
            boolean r0 = android.os.Build.IS_DEBUGGABLE
            if (r0 != 0) goto L_0x009b
            boolean r0 = r15.isDowngradeAllowedForCaller(r14)
            if (r0 == 0) goto L_0x008c
            goto L_0x009b
        L_0x008c:
            int r0 = r13.installFlags
            r2 = -1048577(0xffffffffffefffff, float:NaN)
            r0 = r0 & r2
            r13.installFlags = r0
            int r0 = r13.installFlags
            r0 = r0 & -129(0xffffffffffffff7f, float:NaN)
            r13.installFlags = r0
            goto L_0x00a2
        L_0x009b:
            int r0 = r13.installFlags
            r2 = 1048576(0x100000, float:1.469368E-39)
            r0 = r0 | r2
            r13.installFlags = r0
        L_0x00a2:
            r0 = 1000(0x3e8, float:1.401E-42)
            if (r14 == r0) goto L_0x00ae
            int r0 = r13.installFlags
            r2 = -524289(0xfffffffffff7ffff, float:NaN)
            r0 = r0 & r2
            r13.installFlags = r0
        L_0x00ae:
            int r0 = r13.installFlags
            r2 = 131072(0x20000, float:1.83671E-40)
            r0 = r0 & r2
            r2 = 1
            if (r0 == 0) goto L_0x00b8
            r0 = r2
            goto L_0x00b9
        L_0x00b8:
            r0 = 0
        L_0x00b9:
            r27 = r0
            boolean r0 = r13.isStaged
            if (r0 != 0) goto L_0x00c1
            if (r27 == 0) goto L_0x00ca
        L_0x00c1:
            android.content.Context r0 = r15.mContext
            java.lang.String r3 = "android.permission.INSTALL_PACKAGES"
            java.lang.String r4 = "PackageInstaller"
            r0.enforceCallingOrSelfPermission(r3, r4)
        L_0x00ca:
            if (r27 == 0) goto L_0x00e9
            com.android.server.pm.ApexManager r0 = r15.mApexManager
            boolean r0 = r0.isApexSupported()
            if (r0 == 0) goto L_0x00e1
            boolean r0 = r13.isStaged
            if (r0 == 0) goto L_0x00d9
            goto L_0x00e9
        L_0x00d9:
            java.lang.IllegalArgumentException r0 = new java.lang.IllegalArgumentException
            java.lang.String r1 = "APEX files can only be installed as part of a staged session."
            r0.<init>(r1)
            throw r0
        L_0x00e1:
            java.lang.IllegalArgumentException r0 = new java.lang.IllegalArgumentException
            java.lang.String r1 = "This device doesn't support the installation of APEX files"
            r0.<init>(r1)
            throw r0
        L_0x00e9:
            boolean r0 = r13.isMultiPackage
            if (r0 != 0) goto L_0x0195
            int r0 = r13.installFlags
            r0 = r0 & 256(0x100, float:3.59E-43)
            if (r0 == 0) goto L_0x0107
            android.content.Context r0 = r15.mContext
            java.lang.String r3 = "android.permission.INSTALL_GRANT_RUNTIME_PERMISSIONS"
            int r0 = r0.checkCallingOrSelfPermission(r3)
            r3 = -1
            if (r0 == r3) goto L_0x00ff
            goto L_0x0107
        L_0x00ff:
            java.lang.SecurityException r0 = new java.lang.SecurityException
            java.lang.String r1 = "You need the android.permission.INSTALL_GRANT_RUNTIME_PERMISSIONS permission to use the PackageManager.INSTALL_GRANT_RUNTIME_PERMISSIONS flag"
            r0.<init>(r1)
            throw r0
        L_0x0107:
            android.graphics.Bitmap r0 = r13.appIcon
            if (r0 == 0) goto L_0x0135
            android.content.Context r0 = r15.mContext
            java.lang.String r3 = "activity"
            java.lang.Object r0 = r0.getSystemService(r3)
            android.app.ActivityManager r0 = (android.app.ActivityManager) r0
            int r3 = r0.getLauncherLargeIconSize()
            android.graphics.Bitmap r4 = r13.appIcon
            int r4 = r4.getWidth()
            int r5 = r3 * 2
            if (r4 > r5) goto L_0x012d
            android.graphics.Bitmap r4 = r13.appIcon
            int r4 = r4.getHeight()
            int r5 = r3 * 2
            if (r4 <= r5) goto L_0x0135
        L_0x012d:
            android.graphics.Bitmap r4 = r13.appIcon
            android.graphics.Bitmap r4 = android.graphics.Bitmap.createScaledBitmap(r4, r3, r3, r2)
            r13.appIcon = r4
        L_0x0135:
            int r0 = r13.mode
            if (r0 == r2) goto L_0x0155
            if (r0 != r1) goto L_0x013c
            goto L_0x0155
        L_0x013c:
            java.lang.IllegalArgumentException r0 = new java.lang.IllegalArgumentException
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r2 = "Invalid install mode: "
            r1.append(r2)
            int r2 = r13.mode
            r1.append(r2)
            java.lang.String r1 = r1.toString()
            r0.<init>(r1)
            throw r0
        L_0x0155:
            int r0 = r13.installFlags
            r0 = r0 & 16
            if (r0 == 0) goto L_0x016d
            android.content.Context r0 = r15.mContext
            boolean r0 = com.android.internal.content.PackageHelper.fitsOnInternal(r0, r13)
            if (r0 == 0) goto L_0x0165
            goto L_0x0195
        L_0x0165:
            java.io.IOException r0 = new java.io.IOException
            java.lang.String r1 = "No suitable internal storage available"
            r0.<init>(r1)
            throw r0
        L_0x016d:
            int r0 = r13.installFlags
            r0 = r0 & 512(0x200, float:7.175E-43)
            if (r0 == 0) goto L_0x017a
            int r0 = r13.installFlags
            r0 = r0 | 16
            r13.installFlags = r0
            goto L_0x0195
        L_0x017a:
            int r0 = r13.installFlags
            r0 = r0 | 16
            r13.installFlags = r0
            long r1 = android.os.Binder.clearCallingIdentity()
            android.content.Context r0 = r15.mContext     // Catch:{ all -> 0x0190 }
            java.lang.String r0 = com.android.internal.content.PackageHelper.resolveInstallVolume(r0, r13)     // Catch:{ all -> 0x0190 }
            r13.volumeUuid = r0     // Catch:{ all -> 0x0190 }
            android.os.Binder.restoreCallingIdentity(r1)
            goto L_0x0195
        L_0x0190:
            r0 = move-exception
            android.os.Binder.restoreCallingIdentity(r1)
            throw r0
        L_0x0195:
            android.util.SparseArray<com.android.server.pm.PackageInstallerSession> r1 = r15.mSessions
            monitor-enter(r1)
            android.util.SparseArray<com.android.server.pm.PackageInstallerSession> r0 = r15.mSessions     // Catch:{ all -> 0x0297 }
            int r0 = getSessionCount(r0, r14)     // Catch:{ all -> 0x0297 }
            long r2 = (long) r0     // Catch:{ all -> 0x0297 }
            r4 = 1024(0x400, double:5.06E-321)
            int r2 = (r2 > r4 ? 1 : (r2 == r4 ? 0 : -1))
            if (r2 >= 0) goto L_0x027d
            android.util.SparseIntArray r2 = r15.mHistoricalSessionsByInstaller     // Catch:{ all -> 0x0297 }
            int r2 = r2.get(r14)     // Catch:{ all -> 0x0297 }
            long r3 = (long) r2
            r5 = 1048576(0x100000, double:5.180654E-318)
            int r3 = (r3 > r5 ? 1 : (r3 == r5 ? 0 : -1))
            if (r3 >= 0) goto L_0x025c
            int r3 = r34.allocateSessionIdLocked()     // Catch:{ all -> 0x0257 }
            r10 = r3
            monitor-exit(r1)     // Catch:{ all -> 0x0257 }
            long r28 = java.lang.System.currentTimeMillis()
            r0 = 0
            r1 = 0
            boolean r2 = r13.isMultiPackage
            if (r2 != 0) goto L_0x01db
            int r2 = r13.installFlags
            r2 = r2 & 16
            if (r2 == 0) goto L_0x01d2
            java.io.File r0 = r15.buildSessionDir(r10, r13)
            r30 = r0
            r31 = r1
            goto L_0x01df
        L_0x01d2:
            java.lang.String r1 = r15.buildExternalStageCid(r10)
            r30 = r0
            r31 = r1
            goto L_0x01df
        L_0x01db:
            r30 = r0
            r31 = r1
        L_0x01df:
            com.android.server.pm.PackageInstallerSession r0 = new com.android.server.pm.PackageInstallerSession
            r1 = r0
            com.android.server.pm.PackageInstallerService$InternalCallback r2 = r15.mInternalCallback
            android.content.Context r3 = r15.mContext
            com.android.server.pm.PackageManagerService r4 = r15.mPm
            android.os.HandlerThread r5 = r15.mInstallThread
            android.os.Looper r6 = r5.getLooper()
            com.android.server.pm.StagingManager r7 = r15.mStagingManager
            r17 = 0
            r18 = 0
            r19 = 0
            r20 = 0
            r21 = -1
            r22 = 0
            r23 = 0
            r24 = 0
            r25 = 0
            java.lang.String r26 = ""
            r5 = r34
            r8 = r10
            r9 = r37
            r32 = r10
            r10 = r36
            r11 = r14
            r12 = r35
            r33 = r14
            r13 = r28
            r15 = r30
            r16 = r31
            r1.<init>(r2, r3, r4, r5, r6, r7, r8, r9, r10, r11, r12, r13, r15, r16, r17, r18, r19, r20, r21, r22, r23, r24, r25, r26)
            r2 = r0
            r3 = r34
            android.util.SparseArray<com.android.server.pm.PackageInstallerSession> r4 = r3.mSessions
            monitor-enter(r4)
            android.util.SparseArray<com.android.server.pm.PackageInstallerSession> r0 = r3.mSessions     // Catch:{ all -> 0x024e }
            r5 = r32
            r0.put(r5, r2)     // Catch:{ all -> 0x024a }
            monitor-exit(r4)     // Catch:{ all -> 0x024a }
            r6 = r35
            boolean r0 = r6.isStaged
            if (r0 == 0) goto L_0x0234
            com.android.server.pm.StagingManager r0 = r3.mStagingManager
            r0.createSession(r2)
        L_0x0234:
            android.content.pm.PackageInstaller$SessionParams r0 = r2.params
            int r0 = r0.installFlags
            r1 = 8388608(0x800000, float:1.17549435E-38)
            r0 = r0 & r1
            if (r0 != 0) goto L_0x0246
            com.android.server.pm.PackageInstallerService$Callbacks r0 = r3.mCallbacks
            int r1 = r2.sessionId
            int r4 = r2.userId
            r0.notifySessionCreated(r1, r4)
        L_0x0246:
            r34.writeSessionsAsync()
            return r5
        L_0x024a:
            r0 = move-exception
            r6 = r35
            goto L_0x0253
        L_0x024e:
            r0 = move-exception
            r6 = r35
            r5 = r32
        L_0x0253:
            monitor-exit(r4)     // Catch:{ all -> 0x0255 }
            throw r0
        L_0x0255:
            r0 = move-exception
            goto L_0x0253
        L_0x0257:
            r0 = move-exception
            r6 = r13
            r3 = r15
            r7 = r14
            goto L_0x029b
        L_0x025c:
            r6 = r13
            r33 = r14
            r3 = r15
            java.lang.IllegalStateException r4 = new java.lang.IllegalStateException     // Catch:{ all -> 0x0279 }
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ all -> 0x0279 }
            r5.<init>()     // Catch:{ all -> 0x0279 }
            java.lang.String r7 = "Too many historical sessions for UID "
            r5.append(r7)     // Catch:{ all -> 0x0279 }
            r7 = r33
            r5.append(r7)     // Catch:{ all -> 0x029d }
            java.lang.String r5 = r5.toString()     // Catch:{ all -> 0x029d }
            r4.<init>(r5)     // Catch:{ all -> 0x029d }
            throw r4     // Catch:{ all -> 0x029d }
        L_0x0279:
            r0 = move-exception
            r7 = r33
            goto L_0x029b
        L_0x027d:
            r6 = r13
            r7 = r14
            r3 = r15
            java.lang.IllegalStateException r2 = new java.lang.IllegalStateException     // Catch:{ all -> 0x029d }
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ all -> 0x029d }
            r4.<init>()     // Catch:{ all -> 0x029d }
            java.lang.String r5 = "Too many active sessions for UID "
            r4.append(r5)     // Catch:{ all -> 0x029d }
            r4.append(r7)     // Catch:{ all -> 0x029d }
            java.lang.String r4 = r4.toString()     // Catch:{ all -> 0x029d }
            r2.<init>(r4)     // Catch:{ all -> 0x029d }
            throw r2     // Catch:{ all -> 0x029d }
        L_0x0297:
            r0 = move-exception
            r6 = r13
            r7 = r14
            r3 = r15
        L_0x029b:
            monitor-exit(r1)     // Catch:{ all -> 0x029d }
            throw r0
        L_0x029d:
            r0 = move-exception
            goto L_0x029b
        L_0x029f:
            java.lang.SecurityException r0 = new java.lang.SecurityException
            java.lang.String r1 = "User restriction prevents installing"
            r0.<init>(r1)
            throw r0
        L_0x02a7:
            java.lang.SecurityException r0 = new java.lang.SecurityException
            java.lang.String r1 = "Failed to create session when system is not ready"
            r0.<init>(r1)
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.pm.PackageInstallerService.createSessionInternal(android.content.pm.PackageInstaller$SessionParams, java.lang.String, int):int");
    }

    private boolean isDowngradeAllowedForCaller(int callingUid) {
        return callingUid == 1000 || callingUid == 0 || callingUid == 2000;
    }

    /* Debug info: failed to restart local var, previous not found, register: 6 */
    public void updateSessionAppIcon(int sessionId, Bitmap appIcon) {
        synchronized (this.mSessions) {
            PackageInstallerSession session = this.mSessions.get(sessionId);
            if (session == null || !isCallingUidOwner(session)) {
                throw new SecurityException("Caller has no access to session " + sessionId);
            }
            if (appIcon != null) {
                int iconSize = ((ActivityManager) this.mContext.getSystemService("activity")).getLauncherLargeIconSize();
                if (appIcon.getWidth() > iconSize * 2 || appIcon.getHeight() > iconSize * 2) {
                    appIcon = Bitmap.createScaledBitmap(appIcon, iconSize, iconSize, true);
                }
            }
            session.params.appIcon = appIcon;
            session.params.appIconLastModified = -1;
            this.mInternalCallback.onSessionBadgingChanged(session);
        }
    }

    /* Debug info: failed to restart local var, previous not found, register: 5 */
    public void updateSessionAppLabel(int sessionId, String appLabel) {
        synchronized (this.mSessions) {
            PackageInstallerSession session = this.mSessions.get(sessionId);
            if (session == null || !isCallingUidOwner(session)) {
                throw new SecurityException("Caller has no access to session " + sessionId);
            }
            session.params.appLabel = appLabel;
            this.mInternalCallback.onSessionBadgingChanged(session);
        }
    }

    /* Debug info: failed to restart local var, previous not found, register: 5 */
    public void abandonSession(int sessionId) {
        synchronized (this.mSessions) {
            PackageInstallerSession session = this.mSessions.get(sessionId);
            if (session == null || !isCallingUidOwner(session)) {
                throw new SecurityException("Caller has no access to session " + sessionId);
            }
            session.abandon();
        }
    }

    public IPackageInstallerSession openSession(int sessionId) {
        try {
            return openSessionInternal(sessionId);
        } catch (IOException e) {
            throw ExceptionUtils.wrap(e);
        }
    }

    /* Debug info: failed to restart local var, previous not found, register: 5 */
    private IPackageInstallerSession openSessionInternal(int sessionId) throws IOException {
        PackageInstallerSession session;
        synchronized (this.mSessions) {
            session = this.mSessions.get(sessionId);
            if (session == null || !isCallingUidOwner(session)) {
                throw new SecurityException("Caller has no access to session " + sessionId);
            }
            session.open();
        }
        return session;
    }

    @GuardedBy({"mSessions"})
    private int allocateSessionIdLocked() {
        int n = 0;
        while (true) {
            int sessionId = this.mRandom.nextInt(2147483646) + 1;
            if (!this.mAllocatedSessions.get(sessionId, false)) {
                this.mAllocatedSessions.put(sessionId, true);
                return sessionId;
            }
            int n2 = n + 1;
            if (n < 32) {
                n = n2;
            } else {
                throw new IllegalStateException("Failed to allocate session ID");
            }
        }
    }

    private File getTmpSessionDir(String volumeUuid) {
        return Environment.getDataAppDirectory(volumeUuid);
    }

    private File buildTmpSessionDir(int sessionId, String volumeUuid) {
        File sessionStagingDir = getTmpSessionDir(volumeUuid);
        return new File(sessionStagingDir, "vmdl" + sessionId + ".tmp");
    }

    private File buildSessionDir(int sessionId, PackageInstaller.SessionParams params) {
        if (!params.isStaged) {
            return buildTmpSessionDir(sessionId, params.volumeUuid);
        }
        File sessionStagingDir = Environment.getDataStagingDirectory(params.volumeUuid);
        return new File(sessionStagingDir, "session_" + sessionId);
    }

    static void prepareStageDir(File stageDir) throws IOException {
        if (!stageDir.exists()) {
            try {
                Os.mkdir(stageDir.getAbsolutePath(), 509);
                Os.chmod(stageDir.getAbsolutePath(), 509);
                if (!SELinux.restorecon(stageDir)) {
                    throw new IOException("Failed to restorecon session dir: " + stageDir);
                }
            } catch (ErrnoException e) {
                throw new IOException("Failed to prepare session dir: " + stageDir, e);
            }
        } else {
            throw new IOException("Session dir already exists: " + stageDir);
        }
    }

    private String buildExternalStageCid(int sessionId) {
        return "smdl" + sessionId + ".tmp";
    }

    public PackageInstaller.SessionInfo getSessionInfo(int sessionId) {
        PackageInstaller.SessionInfo generateInfo;
        synchronized (this.mSessions) {
            PackageInstallerSession session = this.mSessions.get(sessionId);
            generateInfo = session != null ? session.generateInfo() : null;
        }
        return generateInfo;
    }

    public ParceledListSlice<PackageInstaller.SessionInfo> getStagedSessions() {
        return this.mStagingManager.getSessions();
    }

    public ParceledListSlice<PackageInstaller.SessionInfo> getAllSessions(int userId) {
        this.mPermissionManager.enforceCrossUserPermission(Binder.getCallingUid(), userId, true, false, "getAllSessions");
        List<PackageInstaller.SessionInfo> result = new ArrayList<>();
        synchronized (this.mSessions) {
            for (int i = 0; i < this.mSessions.size(); i++) {
                PackageInstallerSession session = this.mSessions.valueAt(i);
                if (session.userId == userId && !session.hasParentSessionId()) {
                    result.add(session.generateInfo(false));
                }
            }
        }
        return new ParceledListSlice<>(result);
    }

    public ParceledListSlice<PackageInstaller.SessionInfo> getMySessions(String installerPackageName, int userId) {
        this.mPermissionManager.enforceCrossUserPermission(Binder.getCallingUid(), userId, true, false, "getMySessions");
        this.mAppOps.checkPackage(Binder.getCallingUid(), installerPackageName);
        List<PackageInstaller.SessionInfo> result = new ArrayList<>();
        synchronized (this.mSessions) {
            for (int i = 0; i < this.mSessions.size(); i++) {
                PackageInstallerSession session = this.mSessions.valueAt(i);
                PackageInstaller.SessionInfo info = session.generateInfo(false);
                if (Objects.equals(info.getInstallerPackageName(), installerPackageName) && session.userId == userId && !session.hasParentSessionId()) {
                    result.add(info);
                }
            }
        }
        return new ParceledListSlice<>(result);
    }

    /* JADX INFO: finally extract failed */
    public void uninstall(VersionedPackage versionedPackage, String callerPackageName, int flags, IntentSender statusReceiver, int userId) {
        VersionedPackage versionedPackage2 = versionedPackage;
        String str = callerPackageName;
        int i = flags;
        int i2 = userId;
        int callingUid = Binder.getCallingUid();
        this.mPermissionManager.enforceCrossUserPermission(callingUid, userId, true, true, "uninstall");
        if (!(callingUid == 2000 || callingUid == 0)) {
            this.mAppOps.checkPackage(callingUid, str);
        }
        DevicePolicyManagerInternal dpmi = (DevicePolicyManagerInternal) LocalServices.getService(DevicePolicyManagerInternal.class);
        boolean canSilentlyInstallPackage = dpmi != null && dpmi.canSilentlyInstallPackage(str, callingUid);
        PackageDeleteObserverAdapter adapter = new PackageDeleteObserverAdapter(this.mContext, statusReceiver, versionedPackage.getPackageName(), canSilentlyInstallPackage, userId);
        if (this.mContext.checkCallingOrSelfPermission("android.permission.DELETE_PACKAGES") == 0) {
            this.mPm.deletePackageVersioned(versionedPackage2, adapter.getBinder(), i2, i);
        } else if (canSilentlyInstallPackage) {
            long ident = Binder.clearCallingIdentity();
            try {
                this.mPm.deletePackageVersioned(versionedPackage2, adapter.getBinder(), i2, i);
                Binder.restoreCallingIdentity(ident);
                DevicePolicyEventLogger.createEvent(HdmiCecKeycode.CEC_KEYCODE_F1_BLUE).setAdmin(str).write();
            } catch (Throwable th) {
                Binder.restoreCallingIdentity(ident);
                throw th;
            }
        } else {
            if (this.mPm.getApplicationInfo(str, 0, i2).targetSdkVersion >= 28) {
                this.mContext.enforceCallingOrSelfPermission("android.permission.REQUEST_DELETE_PACKAGES", (String) null);
            }
            Intent intent = new Intent("android.intent.action.UNINSTALL_PACKAGE");
            intent.setData(Uri.fromParts(Settings.ATTR_PACKAGE, versionedPackage.getPackageName(), (String) null));
            intent.putExtra("android.content.pm.extra.CALLBACK", adapter.getBinder().asBinder());
            adapter.onUserActionRequired(intent);
        }
    }

    public void installExistingPackage(String packageName, int installFlags, int installReason, IntentSender statusReceiver, int userId, List<String> whiteListedPermissions) {
        this.mPm.installExistingPackageAsUser(packageName, userId, installFlags, installReason, whiteListedPermissions, statusReceiver);
    }

    public void setPermissionsResult(int sessionId, boolean accepted) {
        this.mContext.enforceCallingOrSelfPermission("android.permission.INSTALL_PACKAGES", TAG);
        synchronized (this.mSessions) {
            PackageInstallerSession session = this.mSessions.get(sessionId);
            if (session != null) {
                session.setPermissionsResult(accepted);
            }
        }
    }

    public void registerCallback(IPackageInstallerCallback callback, int userId) {
        this.mPermissionManager.enforceCrossUserPermission(Binder.getCallingUid(), userId, true, false, "registerCallback");
        registerCallback(callback, (IntPredicate) new IntPredicate(userId) {
            private final /* synthetic */ int f$0;

            {
                this.f$0 = r1;
            }

            public final boolean test(int i) {
                return PackageInstallerService.lambda$registerCallback$0(this.f$0, i);
            }
        });
    }

    static /* synthetic */ boolean lambda$registerCallback$0(int userId, int eventUserId) {
        return userId == eventUserId;
    }

    public void registerCallback(IPackageInstallerCallback callback, IntPredicate userCheck) {
        this.mCallbacks.register(callback, userCheck);
    }

    public void unregisterCallback(IPackageInstallerCallback callback) {
        this.mCallbacks.unregister(callback);
    }

    public PackageInstallerSession getSession(int sessionId) {
        PackageInstallerSession packageInstallerSession;
        synchronized (this.mSessions) {
            packageInstallerSession = this.mSessions.get(sessionId);
        }
        return packageInstallerSession;
    }

    private static int getSessionCount(SparseArray<PackageInstallerSession> sessions, int installerUid) {
        int count = 0;
        int size = sessions.size();
        for (int i = 0; i < size; i++) {
            if (sessions.valueAt(i).getInstallerUid() == installerUid) {
                count++;
            }
        }
        return count;
    }

    private boolean isCallingUidOwner(PackageInstallerSession session) {
        int callingUid = Binder.getCallingUid();
        if (callingUid == 0) {
            return true;
        }
        if (session == null || callingUid != session.getInstallerUid()) {
            return false;
        }
        return true;
    }

    static class PackageDeleteObserverAdapter extends PackageDeleteObserver {
        private final Context mContext;
        private final Notification mNotification;
        private final String mPackageName;
        private final IntentSender mTarget;

        public PackageDeleteObserverAdapter(Context context, IntentSender target, String packageName, boolean showNotification, int userId) {
            this.mContext = context;
            this.mTarget = target;
            this.mPackageName = packageName;
            if (showNotification) {
                Context context2 = this.mContext;
                this.mNotification = PackageInstallerService.buildSuccessNotification(context2, context2.getResources().getString(17040605), packageName, userId);
                return;
            }
            this.mNotification = null;
        }

        public void onUserActionRequired(Intent intent) {
            if (this.mTarget != null) {
                Intent fillIn = new Intent();
                fillIn.putExtra("android.content.pm.extra.PACKAGE_NAME", this.mPackageName);
                fillIn.putExtra("android.content.pm.extra.STATUS", -1);
                fillIn.putExtra("android.intent.extra.INTENT", intent);
                try {
                    this.mTarget.sendIntent(this.mContext, 0, fillIn, (IntentSender.OnFinished) null, (Handler) null);
                } catch (IntentSender.SendIntentException e) {
                }
            }
        }

        public void onPackageDeleted(String basePackageName, int returnCode, String msg) {
            if (1 == returnCode && this.mNotification != null) {
                ((NotificationManager) this.mContext.getSystemService("notification")).notify(basePackageName, 21, this.mNotification);
            }
            if (this.mTarget != null) {
                Intent fillIn = new Intent();
                fillIn.putExtra("android.content.pm.extra.PACKAGE_NAME", this.mPackageName);
                fillIn.putExtra("android.content.pm.extra.STATUS", PackageManager.deleteStatusToPublicStatus(returnCode));
                fillIn.putExtra("android.content.pm.extra.STATUS_MESSAGE", PackageManager.deleteStatusToString(returnCode, msg));
                fillIn.putExtra("android.content.pm.extra.LEGACY_STATUS", returnCode);
                try {
                    this.mTarget.sendIntent(this.mContext, 0, fillIn, (IntentSender.OnFinished) null, (Handler) null);
                } catch (IntentSender.SendIntentException e) {
                }
            }
        }
    }

    static class PackageInstallObserverAdapter extends PackageInstallObserver {
        private final Context mContext;
        private final int mSessionId;
        private final boolean mShowNotification;
        private final IntentSender mTarget;
        private final int mUserId;

        public PackageInstallObserverAdapter(Context context, IntentSender target, int sessionId, boolean showNotification, int userId) {
            this.mContext = context;
            this.mTarget = target;
            this.mSessionId = sessionId;
            this.mShowNotification = showNotification;
            this.mUserId = userId;
        }

        public void onUserActionRequired(Intent intent) {
            Intent fillIn = new Intent();
            fillIn.putExtra("android.content.pm.extra.SESSION_ID", this.mSessionId);
            fillIn.putExtra("android.content.pm.extra.STATUS", -1);
            fillIn.putExtra("android.intent.extra.INTENT", intent);
            try {
                this.mTarget.sendIntent(this.mContext, 0, fillIn, (IntentSender.OnFinished) null, (Handler) null);
            } catch (IntentSender.SendIntentException e) {
            }
        }

        public void onPackageInstalled(String basePackageName, int returnCode, String msg, Bundle extras) {
            int i;
            boolean update = true;
            if (1 == returnCode && this.mShowNotification) {
                if (extras == null || !extras.getBoolean("android.intent.extra.REPLACING")) {
                    update = false;
                }
                Context context = this.mContext;
                Resources resources = context.getResources();
                if (update) {
                    i = 17040607;
                } else {
                    i = 17040606;
                }
                Notification notification = PackageInstallerService.buildSuccessNotification(context, resources.getString(i), basePackageName, this.mUserId);
                if (notification != null) {
                    ((NotificationManager) this.mContext.getSystemService("notification")).notify(basePackageName, 21, notification);
                }
            }
            Intent fillIn = new Intent();
            fillIn.putExtra("android.content.pm.extra.PACKAGE_NAME", basePackageName);
            fillIn.putExtra("android.content.pm.extra.SESSION_ID", this.mSessionId);
            fillIn.putExtra("android.content.pm.extra.STATUS", PackageManager.installStatusToPublicStatus(returnCode));
            fillIn.putExtra("android.content.pm.extra.STATUS_MESSAGE", PackageManager.installStatusToString(returnCode, msg));
            fillIn.putExtra("android.content.pm.extra.LEGACY_STATUS", returnCode);
            if (extras != null) {
                String existing = extras.getString("android.content.pm.extra.FAILURE_EXISTING_PACKAGE");
                if (!TextUtils.isEmpty(existing)) {
                    fillIn.putExtra("android.content.pm.extra.OTHER_PACKAGE_NAME", existing);
                }
            }
            try {
                this.mTarget.sendIntent(this.mContext, 0, fillIn, (IntentSender.OnFinished) null, (Handler) null);
            } catch (IntentSender.SendIntentException e) {
            }
        }
    }

    /* access modifiers changed from: private */
    public static Notification buildSuccessNotification(Context context, String contentText, String basePackageName, int userId) {
        PackageInfo packageInfo = null;
        try {
            packageInfo = AppGlobals.getPackageManager().getPackageInfo(basePackageName, BroadcastQueueInjector.FLAG_IMMUTABLE, userId);
        } catch (RemoteException e) {
        }
        if (packageInfo == null || packageInfo.applicationInfo == null) {
            Slog.w(TAG, "Notification not built for package: " + basePackageName);
            return null;
        }
        PackageManager pm = context.getPackageManager();
        return new Notification.Builder(context, SystemNotificationChannels.DEVICE_ADMIN).setSmallIcon(17302335).setColor(context.getResources().getColor(17170460)).setContentTitle(packageInfo.applicationInfo.loadLabel(pm)).setContentText(contentText).setStyle(new Notification.BigTextStyle().bigText(contentText)).setLargeIcon(ImageUtils.buildScaledBitmap(packageInfo.applicationInfo.loadIcon(pm), context.getResources().getDimensionPixelSize(17104901), context.getResources().getDimensionPixelSize(17104902))).build();
    }

    public static <E> ArraySet<E> newArraySet(E... elements) {
        ArraySet<E> set = new ArraySet<>();
        if (elements != null) {
            set.ensureCapacity(elements.length);
            Collections.addAll(set, elements);
        }
        return set;
    }

    private static class Callbacks extends Handler {
        private static final int MSG_SESSION_ACTIVE_CHANGED = 3;
        private static final int MSG_SESSION_BADGING_CHANGED = 2;
        private static final int MSG_SESSION_CREATED = 1;
        private static final int MSG_SESSION_FINISHED = 5;
        private static final int MSG_SESSION_PROGRESS_CHANGED = 4;
        private final RemoteCallbackList<IPackageInstallerCallback> mCallbacks = new RemoteCallbackList<>();

        public Callbacks(Looper looper) {
            super(looper);
        }

        public void register(IPackageInstallerCallback callback, IntPredicate userCheck) {
            this.mCallbacks.register(callback, userCheck);
        }

        public void unregister(IPackageInstallerCallback callback) {
            this.mCallbacks.unregister(callback);
        }

        public void handleMessage(Message msg) {
            int userId = msg.arg2;
            int n = this.mCallbacks.beginBroadcast();
            for (int i = 0; i < n; i++) {
                IPackageInstallerCallback callback = this.mCallbacks.getBroadcastItem(i);
                if (((IntPredicate) this.mCallbacks.getBroadcastCookie(i)).test(userId)) {
                    try {
                        invokeCallback(callback, msg);
                    } catch (RemoteException e) {
                    }
                }
            }
            this.mCallbacks.finishBroadcast();
        }

        private void invokeCallback(IPackageInstallerCallback callback, Message msg) throws RemoteException {
            int sessionId = msg.arg1;
            int i = msg.what;
            if (i == 1) {
                callback.onSessionCreated(sessionId);
            } else if (i == 2) {
                callback.onSessionBadgingChanged(sessionId);
            } else if (i == 3) {
                callback.onSessionActiveChanged(sessionId, ((Boolean) msg.obj).booleanValue());
            } else if (i == 4) {
                callback.onSessionProgressChanged(sessionId, ((Float) msg.obj).floatValue());
            } else if (i == 5) {
                callback.onSessionFinished(sessionId, ((Boolean) msg.obj).booleanValue());
            }
        }

        /* access modifiers changed from: private */
        public void notifySessionCreated(int sessionId, int userId) {
            obtainMessage(1, sessionId, userId).sendToTarget();
        }

        /* access modifiers changed from: private */
        public void notifySessionBadgingChanged(int sessionId, int userId) {
            obtainMessage(2, sessionId, userId).sendToTarget();
        }

        /* access modifiers changed from: private */
        public void notifySessionActiveChanged(int sessionId, int userId, boolean active) {
            obtainMessage(3, sessionId, userId, Boolean.valueOf(active)).sendToTarget();
        }

        /* access modifiers changed from: private */
        public void notifySessionProgressChanged(int sessionId, int userId, float progress) {
            obtainMessage(4, sessionId, userId, Float.valueOf(progress)).sendToTarget();
        }

        public void notifySessionFinished(int sessionId, int userId, boolean success) {
            obtainMessage(5, sessionId, userId, Boolean.valueOf(success)).sendToTarget();
        }
    }

    /* access modifiers changed from: package-private */
    public void dump(IndentingPrintWriter pw) {
        synchronized (this.mSessions) {
            pw.println("Active install sessions:");
            pw.increaseIndent();
            int N = this.mSessions.size();
            for (int i = 0; i < N; i++) {
                this.mSessions.valueAt(i).dump(pw);
                pw.println();
            }
            pw.println();
            pw.decreaseIndent();
            pw.println("Historical install sessions:");
            pw.increaseIndent();
            int N2 = this.mHistoricalSessions.size();
            for (int i2 = 0; i2 < N2; i2++) {
                pw.print(this.mHistoricalSessions.get(i2));
                pw.println();
            }
            pw.println();
            pw.decreaseIndent();
            pw.println("Legacy install sessions:");
            pw.increaseIndent();
            pw.println(this.mLegacySessions.toString());
            pw.decreaseIndent();
        }
    }

    class InternalCallback {
        InternalCallback() {
        }

        public void onSessionBadgingChanged(PackageInstallerSession session) {
            if ((session.params.installFlags & DumpState.DUMP_VOLUMES) == 0) {
                PackageInstallerService.this.mCallbacks.notifySessionBadgingChanged(session.sessionId, session.userId);
            }
            PackageInstallerService.this.writeSessionsAsync();
        }

        public void onSessionActiveChanged(PackageInstallerSession session, boolean active) {
            if ((session.params.installFlags & DumpState.DUMP_VOLUMES) == 0) {
                PackageInstallerService.this.mCallbacks.notifySessionActiveChanged(session.sessionId, session.userId, active);
            }
        }

        public void onSessionProgressChanged(PackageInstallerSession session, float progress) {
            if ((session.params.installFlags & DumpState.DUMP_VOLUMES) == 0) {
                PackageInstallerService.this.mCallbacks.notifySessionProgressChanged(session.sessionId, session.userId, progress);
            }
        }

        public void onStagedSessionChanged(PackageInstallerSession session) {
            session.markUpdated();
            PackageInstallerService.this.writeSessionsAsync();
            if (PackageInstallerService.this.mOkToSendBroadcasts) {
                PackageInstallerService.this.mPm.sendSessionUpdatedBroadcast(session.generateInfo(false), session.userId);
            }
        }

        public void onSessionFinished(final PackageInstallerSession session, final boolean success) {
            if ((session.params.installFlags & DumpState.DUMP_VOLUMES) == 0) {
                PackageInstallerService.this.mCallbacks.notifySessionFinished(session.sessionId, session.userId, success);
            }
            PackageInstallerService.this.mInstallHandler.post(new Runnable() {
                public void run() {
                    if (session.isStaged() && !success) {
                        PackageInstallerService.this.mStagingManager.abortSession(session);
                    }
                    synchronized (PackageInstallerService.this.mSessions) {
                        if (!session.isStaged() || !success) {
                            PackageInstallerService.this.mSessions.remove(session.sessionId);
                        }
                        PackageInstallerService.this.addHistoricalSessionLocked(session);
                        File appIconFile = PackageInstallerService.this.buildAppIconFile(session.sessionId);
                        if (appIconFile.exists()) {
                            appIconFile.delete();
                        }
                        PackageInstallerService.this.writeSessionsLocked();
                    }
                }
            });
        }

        public void onSessionPrepared(PackageInstallerSession session) {
            PackageInstallerService.this.writeSessionsAsync();
        }

        public void onSessionSealedBlocking(PackageInstallerSession session) {
            synchronized (PackageInstallerService.this.mSessions) {
                PackageInstallerService.this.writeSessionsLocked();
            }
        }
    }
}
