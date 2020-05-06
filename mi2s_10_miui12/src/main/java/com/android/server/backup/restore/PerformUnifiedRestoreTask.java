package com.android.server.backup.restore;

import android.app.IBackupAgent;
import android.app.backup.BackupDataInput;
import android.app.backup.BackupDataOutput;
import android.app.backup.IBackupManagerMonitor;
import android.app.backup.IRestoreObserver;
import android.app.backup.RestoreDescription;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManagerInternal;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.os.RemoteException;
import android.os.SystemClock;
import android.os.UserHandle;
import android.util.EventLog;
import android.util.Slog;
import com.android.internal.backup.IBackupTransport;
import com.android.internal.util.Preconditions;
import com.android.server.AppWidgetBackupBridge;
import com.android.server.EventLogTags;
import com.android.server.LocalServices;
import com.android.server.backup.BackupAgentTimeoutParameters;
import com.android.server.backup.BackupManagerService;
import com.android.server.backup.BackupRestoreTask;
import com.android.server.backup.BackupUtils;
import com.android.server.backup.PackageManagerBackupAgent;
import com.android.server.backup.TransportManager;
import com.android.server.backup.UserBackupManagerService;
import com.android.server.backup.internal.OnTaskFinishedListener;
import com.android.server.backup.keyvalue.KeyValueBackupTask;
import com.android.server.backup.transport.TransportClient;
import com.android.server.backup.utils.AppBackupUtils;
import com.android.server.backup.utils.BackupManagerMonitorUtils;
import com.android.server.job.JobSchedulerShellCommand;
import com.android.server.pm.PackageManagerService;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import libcore.io.IoUtils;

public class PerformUnifiedRestoreTask implements BackupRestoreTask {
    /* access modifiers changed from: private */
    public UserBackupManagerService backupManagerService;
    private List<PackageInfo> mAcceptSet;
    /* access modifiers changed from: private */
    public IBackupAgent mAgent;
    private final BackupAgentTimeoutParameters mAgentTimeoutParameters;
    ParcelFileDescriptor mBackupData;
    private File mBackupDataName;
    private int mCount;
    /* access modifiers changed from: private */
    public PackageInfo mCurrentPackage;
    /* access modifiers changed from: private */
    public boolean mDidLaunch;
    private final int mEphemeralOpToken;
    private boolean mFinished;
    private boolean mIsSystemRestore;
    private final OnTaskFinishedListener mListener;
    /* access modifiers changed from: private */
    public IBackupManagerMonitor mMonitor;
    ParcelFileDescriptor mNewState;
    private File mNewStateName;
    private IRestoreObserver mObserver;
    private PackageManagerBackupAgent mPmAgent;
    private int mPmToken;
    private RestoreDescription mRestoreDescription;
    private File mSavedStateName;
    private File mStageName;
    private long mStartRealtime = SystemClock.elapsedRealtime();
    private UnifiedRestoreState mState = UnifiedRestoreState.INITIAL;
    File mStateDir;
    private int mStatus;
    private PackageInfo mTargetPackage;
    private long mToken;
    /* access modifiers changed from: private */
    public final TransportClient mTransportClient;
    private final TransportManager mTransportManager;
    private final int mUserId;
    /* access modifiers changed from: private */
    public byte[] mWidgetData;

    public PerformUnifiedRestoreTask(UserBackupManagerService backupManagerService2, TransportClient transportClient, IRestoreObserver observer, IBackupManagerMonitor monitor, long restoreSetToken, PackageInfo targetPackage, int pmToken, boolean isFullSystemRestore, String[] filterSet, OnTaskFinishedListener listener) {
        String[] filterSet2;
        PackageInfo packageInfo = targetPackage;
        this.backupManagerService = backupManagerService2;
        this.mUserId = backupManagerService2.getUserId();
        this.mTransportManager = backupManagerService2.getTransportManager();
        this.mEphemeralOpToken = backupManagerService2.generateRandomIntegerToken();
        this.mTransportClient = transportClient;
        this.mObserver = observer;
        this.mMonitor = monitor;
        this.mToken = restoreSetToken;
        this.mPmToken = pmToken;
        this.mTargetPackage = packageInfo;
        this.mIsSystemRestore = isFullSystemRestore;
        this.mFinished = false;
        this.mDidLaunch = false;
        this.mListener = listener;
        this.mAgentTimeoutParameters = (BackupAgentTimeoutParameters) Preconditions.checkNotNull(backupManagerService2.getAgentTimeoutParameters(), "Timeout parameters cannot be null");
        if (packageInfo != null) {
            this.mAcceptSet = new ArrayList();
            this.mAcceptSet.add(packageInfo);
            String[] strArr = filterSet;
        } else {
            if (filterSet == null) {
                filterSet2 = packagesToNames(PackageManagerBackupAgent.getStorableApplications(backupManagerService2.getPackageManager(), this.mUserId));
                Slog.i(BackupManagerService.TAG, "Full restore; asking about " + filterSet2.length + " apps");
            } else {
                filterSet2 = filterSet;
            }
            this.mAcceptSet = new ArrayList(filterSet2.length);
            int i = 0;
            boolean hasSettings = false;
            boolean hasSystem = false;
            while (i < filterSet2.length) {
                try {
                    PackageInfo info = backupManagerService2.getPackageManager().getPackageInfoAsUser(filterSet2[i], 0, this.mUserId);
                    if (PackageManagerService.PLATFORM_PACKAGE_NAME.equals(info.packageName)) {
                        hasSystem = true;
                    } else if (UserBackupManagerService.SETTINGS_PACKAGE.equals(info.packageName)) {
                        hasSettings = true;
                    } else if (AppBackupUtils.appIsEligibleForBackup(info.applicationInfo, this.mUserId)) {
                        this.mAcceptSet.add(info);
                    }
                } catch (PackageManager.NameNotFoundException e) {
                }
                i++;
                UserBackupManagerService userBackupManagerService = backupManagerService2;
                TransportClient transportClient2 = transportClient;
                IRestoreObserver iRestoreObserver = observer;
                IBackupManagerMonitor iBackupManagerMonitor = monitor;
                PackageInfo packageInfo2 = targetPackage;
            }
            if (hasSystem) {
                try {
                    this.mAcceptSet.add(0, backupManagerService2.getPackageManager().getPackageInfoAsUser(PackageManagerService.PLATFORM_PACKAGE_NAME, 0, this.mUserId));
                } catch (PackageManager.NameNotFoundException e2) {
                }
            }
            if (hasSettings) {
                try {
                    this.mAcceptSet.add(backupManagerService2.getPackageManager().getPackageInfoAsUser(UserBackupManagerService.SETTINGS_PACKAGE, 0, this.mUserId));
                } catch (PackageManager.NameNotFoundException e3) {
                }
            }
        }
        Slog.v(BackupManagerService.TAG, "Restore; accept set size is " + this.mAcceptSet.size());
        for (PackageInfo info2 : this.mAcceptSet) {
            Slog.v(BackupManagerService.TAG, "   " + info2.packageName);
        }
    }

    private String[] packagesToNames(List<PackageInfo> apps) {
        int N = apps.size();
        String[] names = new String[N];
        for (int i = 0; i < N; i++) {
            names[i] = apps.get(i).packageName;
        }
        return names;
    }

    public void execute() {
        Slog.v(BackupManagerService.TAG, "*** Executing restore step " + this.mState);
        switch (this.mState) {
            case INITIAL:
                startRestore();
                return;
            case RUNNING_QUEUE:
                dispatchNextRestore();
                return;
            case RESTORE_KEYVALUE:
                restoreKeyValue();
                return;
            case RESTORE_FULL:
                restoreFull();
                return;
            case RESTORE_FINISHED:
                restoreFinished();
                return;
            case FINAL:
                if (!this.mFinished) {
                    finalizeRestore();
                } else {
                    Slog.e(BackupManagerService.TAG, "Duplicate finish");
                }
                this.mFinished = true;
                return;
            default:
                return;
        }
    }

    private void startRestore() {
        sendStartRestore(this.mAcceptSet.size());
        if (this.mIsSystemRestore) {
            AppWidgetBackupBridge.restoreStarting(this.mUserId);
        }
        try {
            this.mStateDir = new File(this.backupManagerService.getBaseStateDir(), this.mTransportManager.getTransportDirName(this.mTransportClient.getTransportComponent()));
            PackageInfo pmPackage = new PackageInfo();
            pmPackage.packageName = UserBackupManagerService.PACKAGE_MANAGER_SENTINEL;
            this.mAcceptSet.add(0, pmPackage);
            IBackupTransport transport = this.mTransportClient.connectOrThrow("PerformUnifiedRestoreTask.startRestore()");
            this.mStatus = transport.startRestore(this.mToken, (PackageInfo[]) this.mAcceptSet.toArray(new PackageInfo[0]));
            if (this.mStatus != 0) {
                Slog.e(BackupManagerService.TAG, "Transport error " + this.mStatus + "; no restore possible");
                this.mStatus = JobSchedulerShellCommand.CMD_ERR_NO_PACKAGE;
                executeNextState(UnifiedRestoreState.FINAL);
                return;
            }
            RestoreDescription desc = transport.nextRestorePackage();
            if (desc == null) {
                Slog.e(BackupManagerService.TAG, "No restore metadata available; halting");
                this.mMonitor = BackupManagerMonitorUtils.monitorEvent(this.mMonitor, 22, this.mCurrentPackage, 3, (Bundle) null);
                this.mStatus = JobSchedulerShellCommand.CMD_ERR_NO_PACKAGE;
                executeNextState(UnifiedRestoreState.FINAL);
            } else if (!UserBackupManagerService.PACKAGE_MANAGER_SENTINEL.equals(desc.getPackageName())) {
                Slog.e(BackupManagerService.TAG, "Required package metadata but got " + desc.getPackageName());
                this.mMonitor = BackupManagerMonitorUtils.monitorEvent(this.mMonitor, 23, this.mCurrentPackage, 3, (Bundle) null);
                this.mStatus = JobSchedulerShellCommand.CMD_ERR_NO_PACKAGE;
                executeNextState(UnifiedRestoreState.FINAL);
            } else {
                this.mCurrentPackage = new PackageInfo();
                this.mCurrentPackage.packageName = UserBackupManagerService.PACKAGE_MANAGER_SENTINEL;
                this.mPmAgent = this.backupManagerService.makeMetadataAgent((List<PackageInfo>) null);
                this.mAgent = IBackupAgent.Stub.asInterface(this.mPmAgent.onBind());
                Slog.v(BackupManagerService.TAG, "initiating restore for PMBA");
                initiateOneRestore(this.mCurrentPackage, 0);
                this.backupManagerService.getBackupHandler().removeMessages(18);
                if (!this.mPmAgent.hasMetadata()) {
                    Slog.e(BackupManagerService.TAG, "PM agent has no metadata, so not restoring");
                    this.mMonitor = BackupManagerMonitorUtils.monitorEvent(this.mMonitor, 24, this.mCurrentPackage, 3, (Bundle) null);
                    EventLog.writeEvent(EventLogTags.RESTORE_AGENT_FAILURE, new Object[]{UserBackupManagerService.PACKAGE_MANAGER_SENTINEL, "Package manager restore metadata missing"});
                    this.mStatus = JobSchedulerShellCommand.CMD_ERR_NO_PACKAGE;
                    this.backupManagerService.getBackupHandler().removeMessages(20, this);
                    executeNextState(UnifiedRestoreState.FINAL);
                }
            }
        } catch (Exception e) {
            Slog.e(BackupManagerService.TAG, "Unable to contact transport for restore: " + e.getMessage());
            this.mMonitor = BackupManagerMonitorUtils.monitorEvent(this.mMonitor, 25, (PackageInfo) null, 1, (Bundle) null);
            this.mStatus = JobSchedulerShellCommand.CMD_ERR_NO_PACKAGE;
            this.backupManagerService.getBackupHandler().removeMessages(20, this);
            executeNextState(UnifiedRestoreState.FINAL);
        }
    }

    private void dispatchNextRestore() {
        UnifiedRestoreState nextState;
        UnifiedRestoreState nextState2 = UnifiedRestoreState.FINAL;
        try {
            IBackupTransport transport = this.mTransportClient.connectOrThrow("PerformUnifiedRestoreTask.dispatchNextRestore()");
            this.mRestoreDescription = transport.nextRestorePackage();
            String pkgName = this.mRestoreDescription != null ? this.mRestoreDescription.getPackageName() : null;
            if (pkgName == null) {
                Slog.e(BackupManagerService.TAG, "Failure getting next package name");
                EventLog.writeEvent(EventLogTags.RESTORE_TRANSPORT_FAILURE, new Object[0]);
                nextState2 = UnifiedRestoreState.FINAL;
            } else if (this.mRestoreDescription == RestoreDescription.NO_MORE_PACKAGES) {
                Slog.v(BackupManagerService.TAG, "No more packages; finishing restore");
                EventLog.writeEvent(EventLogTags.RESTORE_SUCCESS, new Object[]{Integer.valueOf(this.mCount), Integer.valueOf((int) (SystemClock.elapsedRealtime() - this.mStartRealtime))});
                executeNextState(UnifiedRestoreState.FINAL);
            } else {
                Slog.i(BackupManagerService.TAG, "Next restore package: " + this.mRestoreDescription);
                sendOnRestorePackage(pkgName);
                PackageManagerBackupAgent.Metadata metaInfo = this.mPmAgent.getRestoredMetadata(pkgName);
                if (metaInfo == null) {
                    Slog.e(BackupManagerService.TAG, "No metadata for " + pkgName);
                    EventLog.writeEvent(EventLogTags.RESTORE_AGENT_FAILURE, new Object[]{pkgName, "Package metadata missing"});
                    executeNextState(UnifiedRestoreState.RUNNING_QUEUE);
                    return;
                }
                try {
                    this.mCurrentPackage = this.backupManagerService.getPackageManager().getPackageInfoAsUser(pkgName, 134217728, this.mUserId);
                    if (metaInfo.versionCode <= this.mCurrentPackage.getLongVersionCode()) {
                    } else if ((this.mCurrentPackage.applicationInfo.flags & 131072) == 0) {
                        StringBuilder sb = new StringBuilder();
                        sb.append("Source version ");
                        IBackupTransport iBackupTransport = transport;
                        sb.append(metaInfo.versionCode);
                        sb.append(" > installed version ");
                        sb.append(this.mCurrentPackage.getLongVersionCode());
                        String message = sb.toString();
                        Slog.w(BackupManagerService.TAG, "Package " + pkgName + ": " + message);
                        this.mMonitor = BackupManagerMonitorUtils.monitorEvent(this.mMonitor, 27, this.mCurrentPackage, 3, BackupManagerMonitorUtils.putMonitoringExtra(BackupManagerMonitorUtils.putMonitoringExtra((Bundle) null, "android.app.backup.extra.LOG_RESTORE_VERSION", metaInfo.versionCode), "android.app.backup.extra.LOG_RESTORE_ANYWAY", false));
                        EventLog.writeEvent(EventLogTags.RESTORE_AGENT_FAILURE, new Object[]{pkgName, message});
                        executeNextState(UnifiedRestoreState.RUNNING_QUEUE);
                        return;
                    } else {
                        Slog.v(BackupManagerService.TAG, "Source version " + metaInfo.versionCode + " > installed version " + this.mCurrentPackage.getLongVersionCode() + " but restoreAnyVersion");
                        this.mMonitor = BackupManagerMonitorUtils.monitorEvent(this.mMonitor, 27, this.mCurrentPackage, 3, BackupManagerMonitorUtils.putMonitoringExtra(BackupManagerMonitorUtils.putMonitoringExtra((Bundle) null, "android.app.backup.extra.LOG_RESTORE_VERSION", metaInfo.versionCode), "android.app.backup.extra.LOG_RESTORE_ANYWAY", true));
                    }
                    Slog.v(BackupManagerService.TAG, "Package " + pkgName + " restore version [" + metaInfo.versionCode + "] is compatible with installed version [" + this.mCurrentPackage.getLongVersionCode() + "]");
                    this.mWidgetData = null;
                    int type = this.mRestoreDescription.getDataType();
                    if (type == 1) {
                        nextState = UnifiedRestoreState.RESTORE_KEYVALUE;
                    } else if (type == 2) {
                        nextState = UnifiedRestoreState.RESTORE_FULL;
                    } else {
                        Slog.e(BackupManagerService.TAG, "Unrecognized restore type " + type);
                        executeNextState(UnifiedRestoreState.RUNNING_QUEUE);
                        return;
                    }
                    executeNextState(nextState);
                } catch (PackageManager.NameNotFoundException e) {
                    IBackupTransport iBackupTransport2 = transport;
                    Slog.e(BackupManagerService.TAG, "Package not present: " + pkgName);
                    this.mMonitor = BackupManagerMonitorUtils.monitorEvent(this.mMonitor, 26, this.mCurrentPackage, 3, (Bundle) null);
                    EventLog.writeEvent(EventLogTags.RESTORE_AGENT_FAILURE, new Object[]{pkgName, "Package missing on device"});
                    executeNextState(UnifiedRestoreState.RUNNING_QUEUE);
                }
            }
        } catch (Exception e2) {
            Slog.e(BackupManagerService.TAG, "Can't get next restore target from transport; halting: " + e2.getMessage());
            EventLog.writeEvent(EventLogTags.RESTORE_TRANSPORT_FAILURE, new Object[0]);
            nextState2 = UnifiedRestoreState.FINAL;
        } finally {
            executeNextState(nextState2);
        }
    }

    private void restoreKeyValue() {
        String packageName = this.mCurrentPackage.packageName;
        if (this.mCurrentPackage.applicationInfo.backupAgentName == null || "".equals(this.mCurrentPackage.applicationInfo.backupAgentName)) {
            Slog.i(BackupManagerService.TAG, "Data exists for package " + packageName + " but app has no agent; skipping");
            this.mMonitor = BackupManagerMonitorUtils.monitorEvent(this.mMonitor, 28, this.mCurrentPackage, 2, (Bundle) null);
            EventLog.writeEvent(EventLogTags.RESTORE_AGENT_FAILURE, new Object[]{packageName, "Package has no agent"});
            executeNextState(UnifiedRestoreState.RUNNING_QUEUE);
            return;
        }
        PackageManagerBackupAgent.Metadata metaInfo = this.mPmAgent.getRestoredMetadata(packageName);
        if (!BackupUtils.signaturesMatch(metaInfo.sigHashes, this.mCurrentPackage, (PackageManagerInternal) LocalServices.getService(PackageManagerInternal.class))) {
            Slog.w(BackupManagerService.TAG, "Signature mismatch restoring " + packageName);
            this.mMonitor = BackupManagerMonitorUtils.monitorEvent(this.mMonitor, 29, this.mCurrentPackage, 3, (Bundle) null);
            EventLog.writeEvent(EventLogTags.RESTORE_AGENT_FAILURE, new Object[]{packageName, "Signature mismatch"});
            executeNextState(UnifiedRestoreState.RUNNING_QUEUE);
            return;
        }
        this.mAgent = this.backupManagerService.bindToAgentSynchronous(this.mCurrentPackage.applicationInfo, 0);
        if (this.mAgent == null) {
            Slog.w(BackupManagerService.TAG, "Can't find backup agent for " + packageName);
            this.mMonitor = BackupManagerMonitorUtils.monitorEvent(this.mMonitor, 30, this.mCurrentPackage, 3, (Bundle) null);
            EventLog.writeEvent(EventLogTags.RESTORE_AGENT_FAILURE, new Object[]{packageName, "Restore agent missing"});
            executeNextState(UnifiedRestoreState.RUNNING_QUEUE);
            return;
        }
        this.mDidLaunch = true;
        try {
            initiateOneRestore(this.mCurrentPackage, metaInfo.versionCode);
            this.mCount++;
        } catch (Exception e) {
            Slog.e(BackupManagerService.TAG, "Error when attempting restore: " + e.toString());
            keyValueAgentErrorCleanup(false);
            executeNextState(UnifiedRestoreState.RUNNING_QUEUE);
        }
    }

    /* access modifiers changed from: package-private */
    public void initiateOneRestore(PackageInfo app, long appVersionCode) {
        ParcelFileDescriptor stage;
        String packageName = app.packageName;
        Slog.d(BackupManagerService.TAG, "initiateOneRestore packageName=" + packageName);
        File dataDir = this.backupManagerService.getDataDir();
        this.mBackupDataName = new File(dataDir, packageName + ".restore");
        File dataDir2 = this.backupManagerService.getDataDir();
        this.mStageName = new File(dataDir2, packageName + ".stage");
        File file = this.mStateDir;
        this.mNewStateName = new File(file, packageName + KeyValueBackupTask.NEW_STATE_FILE_SUFFIX);
        this.mSavedStateName = new File(this.mStateDir, packageName);
        boolean staging = packageName.equals(PackageManagerService.PLATFORM_PACKAGE_NAME) ^ true;
        File downloadFile = staging ? this.mStageName : this.mBackupDataName;
        try {
            IBackupTransport transport = this.mTransportClient.connectOrThrow("PerformUnifiedRestoreTask.initiateOneRestore()");
            ParcelFileDescriptor stage2 = ParcelFileDescriptor.open(downloadFile, 1006632960);
            if (transport.getRestoreData(stage2) != 0) {
                Slog.e(BackupManagerService.TAG, "Error getting restore data for " + packageName);
                EventLog.writeEvent(EventLogTags.RESTORE_TRANSPORT_FAILURE, new Object[0]);
                stage2.close();
                downloadFile.delete();
                executeNextState(UnifiedRestoreState.FINAL);
                return;
            }
            if (staging) {
                stage2.close();
                ParcelFileDescriptor stage3 = ParcelFileDescriptor.open(downloadFile, 268435456);
                this.mBackupData = ParcelFileDescriptor.open(this.mBackupDataName, 1006632960);
                BackupDataInput in = new BackupDataInput(stage3.getFileDescriptor());
                BackupDataOutput out = new BackupDataOutput(this.mBackupData.getFileDescriptor());
                byte[] buffer = new byte[8192];
                while (in.readNextHeader()) {
                    String key = in.getKey();
                    int size = in.getDataSize();
                    String key2 = key;
                    if (key2.equals(UserBackupManagerService.KEY_WIDGET_STATE)) {
                        Slog.i(BackupManagerService.TAG, "Restoring widget state for " + packageName);
                        int size2 = size;
                        this.mWidgetData = new byte[size2];
                        in.readEntityData(this.mWidgetData, 0, size2);
                    } else {
                        int size3 = size;
                        if (size3 > buffer.length) {
                            buffer = new byte[size3];
                        }
                        in.readEntityData(buffer, 0, size3);
                        out.writeEntityHeader(key2, size3);
                        out.writeEntityData(buffer, size3);
                    }
                }
                this.mBackupData.close();
                stage = stage3;
            } else {
                stage = stage2;
            }
            stage.close();
            this.mBackupData = ParcelFileDescriptor.open(this.mBackupDataName, 268435456);
            this.mNewState = ParcelFileDescriptor.open(this.mNewStateName, 1006632960);
            this.backupManagerService.prepareOperationTimeout(this.mEphemeralOpToken, this.mAgentTimeoutParameters.getRestoreAgentTimeoutMillis(), this, 1);
            this.mAgent.doRestore(this.mBackupData, appVersionCode, this.mNewState, this.mEphemeralOpToken, this.backupManagerService.getBackupManagerBinder());
        } catch (Exception e) {
            Slog.e(BackupManagerService.TAG, "Unable to call app for restore: " + packageName, e);
            EventLog.writeEvent(EventLogTags.RESTORE_AGENT_FAILURE, new Object[]{packageName, e.toString()});
            keyValueAgentErrorCleanup(false);
            executeNextState(UnifiedRestoreState.RUNNING_QUEUE);
        }
    }

    private void restoreFull() {
        try {
            StreamFeederThread feeder = new StreamFeederThread();
            Slog.i(BackupManagerService.TAG, "Spinning threads for stream restore of " + this.mCurrentPackage.packageName);
            new Thread(feeder, "unified-stream-feeder").start();
        } catch (IOException e) {
            Slog.e(BackupManagerService.TAG, "Unable to construct pipes for stream restore!");
            executeNextState(UnifiedRestoreState.RUNNING_QUEUE);
        }
    }

    private void restoreFinished() {
        Slog.d(BackupManagerService.TAG, "restoreFinished packageName=" + this.mCurrentPackage.packageName);
        try {
            this.backupManagerService.prepareOperationTimeout(this.mEphemeralOpToken, this.mAgentTimeoutParameters.getRestoreAgentFinishedTimeoutMillis(), this, 1);
            this.mAgent.doRestoreFinished(this.mEphemeralOpToken, this.backupManagerService.getBackupManagerBinder());
        } catch (Exception e) {
            String packageName = this.mCurrentPackage.packageName;
            Slog.e(BackupManagerService.TAG, "Unable to finalize restore of " + packageName);
            EventLog.writeEvent(EventLogTags.RESTORE_AGENT_FAILURE, new Object[]{packageName, e.toString()});
            keyValueAgentErrorCleanup(true);
            executeNextState(UnifiedRestoreState.RUNNING_QUEUE);
        }
    }

    class StreamFeederThread extends RestoreEngine implements Runnable, BackupRestoreTask {
        final String TAG = "StreamFeederThread";
        FullRestoreEngine mEngine;
        ParcelFileDescriptor[] mEnginePipes;
        FullRestoreEngineThread mEngineThread;
        private final int mEphemeralOpToken;
        ParcelFileDescriptor[] mTransportPipes;

        public StreamFeederThread() throws IOException {
            this.mEphemeralOpToken = PerformUnifiedRestoreTask.this.backupManagerService.generateRandomIntegerToken();
            this.mTransportPipes = ParcelFileDescriptor.createPipe();
            this.mEnginePipes = ParcelFileDescriptor.createPipe();
            setRunning(true);
        }

        /* JADX WARNING: Code restructure failed: missing block: B:79:0x02d1, code lost:
            if (r2 == 64536) goto L_0x02d3;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:80:0x02d3, code lost:
            r0 = com.android.server.backup.restore.UnifiedRestoreState.FINAL;
            r15 = r2;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:81:0x02d8, code lost:
            r0 = com.android.server.backup.restore.UnifiedRestoreState.RUNNING_QUEUE;
            r15 = r2;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:98:0x0372, code lost:
            if (r2 == 64536) goto L_0x02d3;
         */
        /* JADX WARNING: Removed duplicated region for block: B:104:0x03a7  */
        /* JADX WARNING: Removed duplicated region for block: B:107:0x03ad A[SYNTHETIC, Splitter:B:107:0x03ad] */
        /* JADX WARNING: Removed duplicated region for block: B:115:0x03f1  */
        /* JADX WARNING: Removed duplicated region for block: B:69:0x0273  */
        /* JADX WARNING: Removed duplicated region for block: B:70:0x0275  */
        /* JADX WARNING: Removed duplicated region for block: B:74:0x0296 A[SYNTHETIC, Splitter:B:74:0x0296] */
        /* JADX WARNING: Removed duplicated region for block: B:89:0x032d  */
        /* JADX WARNING: Removed duplicated region for block: B:90:0x032f  */
        /* JADX WARNING: Removed duplicated region for block: B:93:0x0337 A[SYNTHETIC, Splitter:B:93:0x0337] */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void run() {
            /*
                r21 = this;
                r11 = r21
                java.lang.String r12 = "Transport threw from abortFullRestore: "
                java.lang.String r13 = "StreamFeederThread"
                com.android.server.backup.restore.UnifiedRestoreState r14 = com.android.server.backup.restore.UnifiedRestoreState.RUNNING_QUEUE
                r15 = 0
                com.android.server.backup.restore.PerformUnifiedRestoreTask r0 = com.android.server.backup.restore.PerformUnifiedRestoreTask.this
                android.content.pm.PackageInfo r0 = r0.mCurrentPackage
                java.lang.String r0 = r0.packageName
                r1 = 2844(0xb1c, float:3.985E-42)
                android.util.EventLog.writeEvent(r1, r0)
                com.android.server.backup.restore.FullRestoreEngine r0 = new com.android.server.backup.restore.FullRestoreEngine
                com.android.server.backup.restore.PerformUnifiedRestoreTask r1 = com.android.server.backup.restore.PerformUnifiedRestoreTask.this
                com.android.server.backup.UserBackupManagerService r2 = r1.backupManagerService
                com.android.server.backup.restore.PerformUnifiedRestoreTask r1 = com.android.server.backup.restore.PerformUnifiedRestoreTask.this
                android.app.backup.IBackupManagerMonitor r5 = r1.mMonitor
                com.android.server.backup.restore.PerformUnifiedRestoreTask r1 = com.android.server.backup.restore.PerformUnifiedRestoreTask.this
                android.content.pm.PackageInfo r6 = r1.mCurrentPackage
                int r9 = r11.mEphemeralOpToken
                r4 = 0
                r7 = 0
                r8 = 0
                r10 = 0
                r1 = r0
                r3 = r21
                r1.<init>(r2, r3, r4, r5, r6, r7, r8, r9, r10)
                r11.mEngine = r0
                com.android.server.backup.restore.FullRestoreEngineThread r0 = new com.android.server.backup.restore.FullRestoreEngineThread
                com.android.server.backup.restore.FullRestoreEngine r1 = r11.mEngine
                android.os.ParcelFileDescriptor[] r2 = r11.mEnginePipes
                r3 = 0
                r2 = r2[r3]
                r0.<init>((com.android.server.backup.restore.FullRestoreEngine) r1, (android.os.ParcelFileDescriptor) r2)
                r11.mEngineThread = r0
                android.os.ParcelFileDescriptor[] r0 = r11.mEnginePipes
                r1 = 1
                r2 = r0[r1]
                android.os.ParcelFileDescriptor[] r0 = r11.mTransportPipes
                r4 = r0[r3]
                r5 = r0[r1]
                r6 = 32768(0x8000, float:4.5918E-41)
                byte[] r7 = new byte[r6]
                java.io.FileOutputStream r0 = new java.io.FileOutputStream
                java.io.FileDescriptor r8 = r2.getFileDescriptor()
                r0.<init>(r8)
                r8 = r0
                java.io.FileInputStream r0 = new java.io.FileInputStream
                java.io.FileDescriptor r9 = r4.getFileDescriptor()
                r0.<init>(r9)
                r9 = r0
                java.lang.Thread r0 = new java.lang.Thread
                com.android.server.backup.restore.FullRestoreEngineThread r10 = r11.mEngineThread
                java.lang.String r1 = "unified-restore-engine"
                r0.<init>(r10, r1)
                r0.start()
                java.lang.String r1 = "PerformUnifiedRestoreTask$StreamFeederThread.run()"
                com.android.server.backup.restore.PerformUnifiedRestoreTask r0 = com.android.server.backup.restore.PerformUnifiedRestoreTask.this     // Catch:{ IOException -> 0x02dd, Exception -> 0x021f, all -> 0x0217 }
                com.android.server.backup.transport.TransportClient r0 = r0.mTransportClient     // Catch:{ IOException -> 0x02dd, Exception -> 0x021f, all -> 0x0217 }
                com.android.internal.backup.IBackupTransport r0 = r0.connectOrThrow(r1)     // Catch:{ IOException -> 0x02dd, Exception -> 0x021f, all -> 0x0217 }
            L_0x0083:
                if (r15 != 0) goto L_0x016b
                int r16 = r0.getNextFullRestoreDataChunk(r5)     // Catch:{ IOException -> 0x02dd, Exception -> 0x021f, all -> 0x0217 }
                r17 = r16
                r10 = r17
                if (r10 <= 0) goto L_0x010c
                java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ IOException -> 0x02dd, Exception -> 0x021f, all -> 0x0217 }
                r3.<init>()     // Catch:{ IOException -> 0x02dd, Exception -> 0x021f, all -> 0x0217 }
                r18 = r0
                java.lang.String r0 = "  <- transport provided chunk size "
                r3.append(r0)     // Catch:{ IOException -> 0x02dd, Exception -> 0x021f, all -> 0x0217 }
                r3.append(r10)     // Catch:{ IOException -> 0x02dd, Exception -> 0x021f, all -> 0x0217 }
                java.lang.String r0 = r3.toString()     // Catch:{ IOException -> 0x02dd, Exception -> 0x021f, all -> 0x0217 }
                android.util.Slog.v(r13, r0)     // Catch:{ IOException -> 0x02dd, Exception -> 0x021f, all -> 0x0217 }
                if (r10 <= r6) goto L_0x00c2
                r6 = r10
                byte[] r0 = new byte[r6]     // Catch:{ IOException -> 0x00bb, Exception -> 0x00b4, all -> 0x00ac }
                r7 = r0
                goto L_0x00c2
            L_0x00ac:
                r0 = move-exception
                r20 = r2
                r19 = r4
                r2 = r0
                goto L_0x0379
            L_0x00b4:
                r0 = move-exception
                r20 = r2
                r19 = r4
                goto L_0x0224
            L_0x00bb:
                r0 = move-exception
                r20 = r2
                r19 = r4
                goto L_0x02e2
            L_0x00c2:
                r0 = r10
            L_0x00c3:
                if (r0 <= 0) goto L_0x0107
                r3 = 0
                int r17 = r9.read(r7, r3, r0)     // Catch:{ IOException -> 0x02dd, Exception -> 0x021f, all -> 0x0217 }
                r19 = r17
                r20 = r2
                r2 = r19
                r8.write(r7, r3, r2)     // Catch:{ IOException -> 0x0102, Exception -> 0x00fd, all -> 0x00f7 }
                int r0 = r0 - r2
                java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ IOException -> 0x0102, Exception -> 0x00fd, all -> 0x00f7 }
                r3.<init>()     // Catch:{ IOException -> 0x0102, Exception -> 0x00fd, all -> 0x00f7 }
                r19 = r4
                java.lang.String r4 = "  -> wrote "
                r3.append(r4)     // Catch:{ IOException -> 0x0214, Exception -> 0x0212 }
                r3.append(r2)     // Catch:{ IOException -> 0x0214, Exception -> 0x0212 }
                java.lang.String r4 = " to engine, left="
                r3.append(r4)     // Catch:{ IOException -> 0x0214, Exception -> 0x0212 }
                r3.append(r0)     // Catch:{ IOException -> 0x0214, Exception -> 0x0212 }
                java.lang.String r3 = r3.toString()     // Catch:{ IOException -> 0x0214, Exception -> 0x0212 }
                android.util.Slog.v(r13, r3)     // Catch:{ IOException -> 0x0214, Exception -> 0x0212 }
                r4 = r19
                r2 = r20
                goto L_0x00c3
            L_0x00f7:
                r0 = move-exception
                r19 = r4
                r2 = r0
                goto L_0x0379
            L_0x00fd:
                r0 = move-exception
                r19 = r4
                goto L_0x0224
            L_0x0102:
                r0 = move-exception
                r19 = r4
                goto L_0x02e2
            L_0x0107:
                r20 = r2
                r19 = r4
                goto L_0x0162
            L_0x010c:
                r18 = r0
                r20 = r2
                r19 = r4
                r0 = -1
                if (r10 != r0) goto L_0x0134
                java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ IOException -> 0x0214, Exception -> 0x0212 }
                r0.<init>()     // Catch:{ IOException -> 0x0214, Exception -> 0x0212 }
                java.lang.String r2 = "Got clean full-restore EOF for "
                r0.append(r2)     // Catch:{ IOException -> 0x0214, Exception -> 0x0212 }
                com.android.server.backup.restore.PerformUnifiedRestoreTask r2 = com.android.server.backup.restore.PerformUnifiedRestoreTask.this     // Catch:{ IOException -> 0x0214, Exception -> 0x0212 }
                android.content.pm.PackageInfo r2 = r2.mCurrentPackage     // Catch:{ IOException -> 0x0214, Exception -> 0x0212 }
                java.lang.String r2 = r2.packageName     // Catch:{ IOException -> 0x0214, Exception -> 0x0212 }
                r0.append(r2)     // Catch:{ IOException -> 0x0214, Exception -> 0x0212 }
                java.lang.String r0 = r0.toString()     // Catch:{ IOException -> 0x0214, Exception -> 0x0212 }
                android.util.Slog.i(r13, r0)     // Catch:{ IOException -> 0x0214, Exception -> 0x0212 }
                r0 = 0
                r15 = r0
                goto L_0x0171
            L_0x0134:
                java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ IOException -> 0x0214, Exception -> 0x0212 }
                r0.<init>()     // Catch:{ IOException -> 0x0214, Exception -> 0x0212 }
                java.lang.String r2 = "Error "
                r0.append(r2)     // Catch:{ IOException -> 0x0214, Exception -> 0x0212 }
                r0.append(r10)     // Catch:{ IOException -> 0x0214, Exception -> 0x0212 }
                java.lang.String r2 = " streaming restore for "
                r0.append(r2)     // Catch:{ IOException -> 0x0214, Exception -> 0x0212 }
                com.android.server.backup.restore.PerformUnifiedRestoreTask r2 = com.android.server.backup.restore.PerformUnifiedRestoreTask.this     // Catch:{ IOException -> 0x0214, Exception -> 0x0212 }
                android.content.pm.PackageInfo r2 = r2.mCurrentPackage     // Catch:{ IOException -> 0x0214, Exception -> 0x0212 }
                java.lang.String r2 = r2.packageName     // Catch:{ IOException -> 0x0214, Exception -> 0x0212 }
                r0.append(r2)     // Catch:{ IOException -> 0x0214, Exception -> 0x0212 }
                java.lang.String r0 = r0.toString()     // Catch:{ IOException -> 0x0214, Exception -> 0x0212 }
                android.util.Slog.e(r13, r0)     // Catch:{ IOException -> 0x0214, Exception -> 0x0212 }
                r2 = 0
                java.lang.Object[] r0 = new java.lang.Object[r2]     // Catch:{ IOException -> 0x0214, Exception -> 0x0212 }
                r2 = 2831(0xb0f, float:3.967E-42)
                android.util.EventLog.writeEvent(r2, r0)     // Catch:{ IOException -> 0x0214, Exception -> 0x0212 }
                r0 = r10
                r15 = r0
            L_0x0162:
                r0 = r18
                r4 = r19
                r2 = r20
                r3 = 0
                goto L_0x0083
            L_0x016b:
                r18 = r0
                r20 = r2
                r19 = r4
            L_0x0171:
                java.lang.String r0 = "Done copying to engine, falling through"
                android.util.Slog.v(r13, r0)     // Catch:{ IOException -> 0x0214, Exception -> 0x0212 }
                android.os.ParcelFileDescriptor[] r0 = r11.mEnginePipes
                r2 = 1
                r0 = r0[r2]
                libcore.io.IoUtils.closeQuietly(r0)
                android.os.ParcelFileDescriptor[] r0 = r11.mTransportPipes
                r3 = 0
                r0 = r0[r3]
                libcore.io.IoUtils.closeQuietly(r0)
                android.os.ParcelFileDescriptor[] r0 = r11.mTransportPipes
                r0 = r0[r2]
                libcore.io.IoUtils.closeQuietly(r0)
                com.android.server.backup.restore.FullRestoreEngineThread r0 = r11.mEngineThread
                r0.waitForResult()
                android.os.ParcelFileDescriptor[] r0 = r11.mEnginePipes
                r0 = r0[r3]
                libcore.io.IoUtils.closeQuietly(r0)
                com.android.server.backup.restore.PerformUnifiedRestoreTask r0 = com.android.server.backup.restore.PerformUnifiedRestoreTask.this
                com.android.server.backup.restore.FullRestoreEngine r2 = r11.mEngine
                android.app.IBackupAgent r2 = r2.getAgent()
                if (r2 == 0) goto L_0x01a6
                r2 = 1
                goto L_0x01a7
            L_0x01a6:
                r2 = 0
            L_0x01a7:
                boolean unused = r0.mDidLaunch = r2
                if (r15 != 0) goto L_0x01c5
                com.android.server.backup.restore.UnifiedRestoreState r0 = com.android.server.backup.restore.UnifiedRestoreState.RESTORE_FINISHED
                com.android.server.backup.restore.PerformUnifiedRestoreTask r2 = com.android.server.backup.restore.PerformUnifiedRestoreTask.this
                com.android.server.backup.restore.FullRestoreEngine r3 = r11.mEngine
                android.app.IBackupAgent r3 = r3.getAgent()
                android.app.IBackupAgent unused = r2.mAgent = r3
                com.android.server.backup.restore.PerformUnifiedRestoreTask r2 = com.android.server.backup.restore.PerformUnifiedRestoreTask.this
                com.android.server.backup.restore.FullRestoreEngine r3 = r11.mEngine
                byte[] r3 = r3.getWidgetData()
                byte[] unused = r2.mWidgetData = r3
                goto L_0x0207
            L_0x01c5:
                com.android.server.backup.restore.PerformUnifiedRestoreTask r0 = com.android.server.backup.restore.PerformUnifiedRestoreTask.this     // Catch:{ Exception -> 0x01d4 }
                com.android.server.backup.transport.TransportClient r0 = r0.mTransportClient     // Catch:{ Exception -> 0x01d4 }
                com.android.internal.backup.IBackupTransport r0 = r0.connectOrThrow(r1)     // Catch:{ Exception -> 0x01d4 }
                r0.abortFullRestore()     // Catch:{ Exception -> 0x01d4 }
                goto L_0x01ed
            L_0x01d4:
                r0 = move-exception
                java.lang.StringBuilder r2 = new java.lang.StringBuilder
                r2.<init>()
                r2.append(r12)
                java.lang.String r3 = r0.getMessage()
                r2.append(r3)
                java.lang.String r2 = r2.toString()
                android.util.Slog.e(r13, r2)
                r15 = -1000(0xfffffffffffffc18, float:NaN)
            L_0x01ed:
                com.android.server.backup.restore.PerformUnifiedRestoreTask r0 = com.android.server.backup.restore.PerformUnifiedRestoreTask.this
                com.android.server.backup.UserBackupManagerService r0 = r0.backupManagerService
                com.android.server.backup.restore.PerformUnifiedRestoreTask r2 = com.android.server.backup.restore.PerformUnifiedRestoreTask.this
                android.content.pm.PackageInfo r2 = r2.mCurrentPackage
                java.lang.String r2 = r2.packageName
                r0.clearApplicationDataAfterRestoreFailure(r2)
                r2 = -1000(0xfffffffffffffc18, float:NaN)
                if (r15 != r2) goto L_0x0205
                com.android.server.backup.restore.UnifiedRestoreState r0 = com.android.server.backup.restore.UnifiedRestoreState.FINAL
                goto L_0x0207
            L_0x0205:
                com.android.server.backup.restore.UnifiedRestoreState r0 = com.android.server.backup.restore.UnifiedRestoreState.RUNNING_QUEUE
            L_0x0207:
                com.android.server.backup.restore.PerformUnifiedRestoreTask r2 = com.android.server.backup.restore.PerformUnifiedRestoreTask.this
                r2.executeNextState(r0)
                r2 = 0
                r11.setRunning(r2)
                goto L_0x0376
            L_0x0212:
                r0 = move-exception
                goto L_0x0224
            L_0x0214:
                r0 = move-exception
                goto L_0x02e2
            L_0x0217:
                r0 = move-exception
                r20 = r2
                r19 = r4
                r2 = r0
                goto L_0x0379
            L_0x021f:
                r0 = move-exception
                r20 = r2
                r19 = r4
            L_0x0224:
                java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ all -> 0x0377 }
                r2.<init>()     // Catch:{ all -> 0x0377 }
                java.lang.String r3 = "Transport failed during restore: "
                r2.append(r3)     // Catch:{ all -> 0x0377 }
                java.lang.String r3 = r0.getMessage()     // Catch:{ all -> 0x0377 }
                r2.append(r3)     // Catch:{ all -> 0x0377 }
                java.lang.String r2 = r2.toString()     // Catch:{ all -> 0x0377 }
                android.util.Slog.e(r13, r2)     // Catch:{ all -> 0x0377 }
                r2 = 0
                java.lang.Object[] r3 = new java.lang.Object[r2]     // Catch:{ all -> 0x0377 }
                r2 = 2831(0xb0f, float:3.967E-42)
                android.util.EventLog.writeEvent(r2, r3)     // Catch:{ all -> 0x0377 }
                r2 = -1000(0xfffffffffffffc18, float:NaN)
                android.os.ParcelFileDescriptor[] r0 = r11.mEnginePipes
                r3 = 1
                r0 = r0[r3]
                libcore.io.IoUtils.closeQuietly(r0)
                android.os.ParcelFileDescriptor[] r0 = r11.mTransportPipes
                r4 = 0
                r0 = r0[r4]
                libcore.io.IoUtils.closeQuietly(r0)
                android.os.ParcelFileDescriptor[] r0 = r11.mTransportPipes
                r0 = r0[r3]
                libcore.io.IoUtils.closeQuietly(r0)
                com.android.server.backup.restore.FullRestoreEngineThread r0 = r11.mEngineThread
                r0.waitForResult()
                android.os.ParcelFileDescriptor[] r0 = r11.mEnginePipes
                r0 = r0[r4]
                libcore.io.IoUtils.closeQuietly(r0)
                com.android.server.backup.restore.PerformUnifiedRestoreTask r0 = com.android.server.backup.restore.PerformUnifiedRestoreTask.this
                com.android.server.backup.restore.FullRestoreEngine r3 = r11.mEngine
                android.app.IBackupAgent r3 = r3.getAgent()
                if (r3 == 0) goto L_0x0275
                r3 = 1
                goto L_0x0276
            L_0x0275:
                r3 = 0
            L_0x0276:
                boolean unused = r0.mDidLaunch = r3
                if (r2 != 0) goto L_0x0296
            L_0x027b:
                com.android.server.backup.restore.UnifiedRestoreState r0 = com.android.server.backup.restore.UnifiedRestoreState.RESTORE_FINISHED
                com.android.server.backup.restore.PerformUnifiedRestoreTask r3 = com.android.server.backup.restore.PerformUnifiedRestoreTask.this
                com.android.server.backup.restore.FullRestoreEngine r4 = r11.mEngine
                android.app.IBackupAgent r4 = r4.getAgent()
                android.app.IBackupAgent unused = r3.mAgent = r4
                com.android.server.backup.restore.PerformUnifiedRestoreTask r3 = com.android.server.backup.restore.PerformUnifiedRestoreTask.this
                com.android.server.backup.restore.FullRestoreEngine r4 = r11.mEngine
                byte[] r4 = r4.getWidgetData()
                byte[] unused = r3.mWidgetData = r4
                r15 = r2
                goto L_0x0207
            L_0x0296:
                com.android.server.backup.restore.PerformUnifiedRestoreTask r0 = com.android.server.backup.restore.PerformUnifiedRestoreTask.this     // Catch:{ Exception -> 0x02a5 }
                com.android.server.backup.transport.TransportClient r0 = r0.mTransportClient     // Catch:{ Exception -> 0x02a5 }
                com.android.internal.backup.IBackupTransport r0 = r0.connectOrThrow(r1)     // Catch:{ Exception -> 0x02a5 }
                r0.abortFullRestore()     // Catch:{ Exception -> 0x02a5 }
                goto L_0x02be
            L_0x02a5:
                r0 = move-exception
                java.lang.StringBuilder r3 = new java.lang.StringBuilder
                r3.<init>()
                r3.append(r12)
                java.lang.String r4 = r0.getMessage()
                r3.append(r4)
                java.lang.String r3 = r3.toString()
                android.util.Slog.e(r13, r3)
                r2 = -1000(0xfffffffffffffc18, float:NaN)
            L_0x02be:
                com.android.server.backup.restore.PerformUnifiedRestoreTask r0 = com.android.server.backup.restore.PerformUnifiedRestoreTask.this
                com.android.server.backup.UserBackupManagerService r0 = r0.backupManagerService
                com.android.server.backup.restore.PerformUnifiedRestoreTask r3 = com.android.server.backup.restore.PerformUnifiedRestoreTask.this
                android.content.pm.PackageInfo r3 = r3.mCurrentPackage
                java.lang.String r3 = r3.packageName
                r0.clearApplicationDataAfterRestoreFailure(r3)
                r3 = -1000(0xfffffffffffffc18, float:NaN)
                if (r2 != r3) goto L_0x02d8
            L_0x02d3:
                com.android.server.backup.restore.UnifiedRestoreState r0 = com.android.server.backup.restore.UnifiedRestoreState.FINAL
                r15 = r2
                goto L_0x0207
            L_0x02d8:
                com.android.server.backup.restore.UnifiedRestoreState r0 = com.android.server.backup.restore.UnifiedRestoreState.RUNNING_QUEUE
                r15 = r2
                goto L_0x0207
            L_0x02dd:
                r0 = move-exception
                r20 = r2
                r19 = r4
            L_0x02e2:
                java.lang.String r2 = "Unable to route data for restore"
                android.util.Slog.e(r13, r2)     // Catch:{ all -> 0x0377 }
                r2 = 2832(0xb10, float:3.968E-42)
                r3 = 2
                java.lang.Object[] r3 = new java.lang.Object[r3]     // Catch:{ all -> 0x0377 }
                com.android.server.backup.restore.PerformUnifiedRestoreTask r4 = com.android.server.backup.restore.PerformUnifiedRestoreTask.this     // Catch:{ all -> 0x0377 }
                android.content.pm.PackageInfo r4 = r4.mCurrentPackage     // Catch:{ all -> 0x0377 }
                java.lang.String r4 = r4.packageName     // Catch:{ all -> 0x0377 }
                r10 = 0
                r3[r10] = r4     // Catch:{ all -> 0x0377 }
                java.lang.String r4 = "I/O error on pipes"
                r10 = 1
                r3[r10] = r4     // Catch:{ all -> 0x0377 }
                android.util.EventLog.writeEvent(r2, r3)     // Catch:{ all -> 0x0377 }
                r2 = -1003(0xfffffffffffffc15, float:NaN)
                android.os.ParcelFileDescriptor[] r0 = r11.mEnginePipes
                r0 = r0[r10]
                libcore.io.IoUtils.closeQuietly(r0)
                android.os.ParcelFileDescriptor[] r0 = r11.mTransportPipes
                r3 = 0
                r0 = r0[r3]
                libcore.io.IoUtils.closeQuietly(r0)
                android.os.ParcelFileDescriptor[] r0 = r11.mTransportPipes
                r0 = r0[r10]
                libcore.io.IoUtils.closeQuietly(r0)
                com.android.server.backup.restore.FullRestoreEngineThread r0 = r11.mEngineThread
                r0.waitForResult()
                android.os.ParcelFileDescriptor[] r0 = r11.mEnginePipes
                r0 = r0[r3]
                libcore.io.IoUtils.closeQuietly(r0)
                com.android.server.backup.restore.PerformUnifiedRestoreTask r0 = com.android.server.backup.restore.PerformUnifiedRestoreTask.this
                com.android.server.backup.restore.FullRestoreEngine r3 = r11.mEngine
                android.app.IBackupAgent r3 = r3.getAgent()
                if (r3 == 0) goto L_0x032f
                r3 = 1
                goto L_0x0330
            L_0x032f:
                r3 = 0
            L_0x0330:
                boolean unused = r0.mDidLaunch = r3
                if (r2 != 0) goto L_0x0337
                goto L_0x027b
            L_0x0337:
                com.android.server.backup.restore.PerformUnifiedRestoreTask r0 = com.android.server.backup.restore.PerformUnifiedRestoreTask.this     // Catch:{ Exception -> 0x0346 }
                com.android.server.backup.transport.TransportClient r0 = r0.mTransportClient     // Catch:{ Exception -> 0x0346 }
                com.android.internal.backup.IBackupTransport r0 = r0.connectOrThrow(r1)     // Catch:{ Exception -> 0x0346 }
                r0.abortFullRestore()     // Catch:{ Exception -> 0x0346 }
                goto L_0x035f
            L_0x0346:
                r0 = move-exception
                java.lang.StringBuilder r3 = new java.lang.StringBuilder
                r3.<init>()
                r3.append(r12)
                java.lang.String r4 = r0.getMessage()
                r3.append(r4)
                java.lang.String r3 = r3.toString()
                android.util.Slog.e(r13, r3)
                r2 = -1000(0xfffffffffffffc18, float:NaN)
            L_0x035f:
                com.android.server.backup.restore.PerformUnifiedRestoreTask r0 = com.android.server.backup.restore.PerformUnifiedRestoreTask.this
                com.android.server.backup.UserBackupManagerService r0 = r0.backupManagerService
                com.android.server.backup.restore.PerformUnifiedRestoreTask r3 = com.android.server.backup.restore.PerformUnifiedRestoreTask.this
                android.content.pm.PackageInfo r3 = r3.mCurrentPackage
                java.lang.String r3 = r3.packageName
                r0.clearApplicationDataAfterRestoreFailure(r3)
                r3 = -1000(0xfffffffffffffc18, float:NaN)
                if (r2 != r3) goto L_0x02d8
                goto L_0x02d3
            L_0x0376:
                return
            L_0x0377:
                r0 = move-exception
                r2 = r0
            L_0x0379:
                android.os.ParcelFileDescriptor[] r0 = r11.mEnginePipes
                r3 = 1
                r0 = r0[r3]
                libcore.io.IoUtils.closeQuietly(r0)
                android.os.ParcelFileDescriptor[] r0 = r11.mTransportPipes
                r4 = 0
                r0 = r0[r4]
                libcore.io.IoUtils.closeQuietly(r0)
                android.os.ParcelFileDescriptor[] r0 = r11.mTransportPipes
                r0 = r0[r3]
                libcore.io.IoUtils.closeQuietly(r0)
                com.android.server.backup.restore.FullRestoreEngineThread r0 = r11.mEngineThread
                r0.waitForResult()
                android.os.ParcelFileDescriptor[] r0 = r11.mEnginePipes
                r0 = r0[r4]
                libcore.io.IoUtils.closeQuietly(r0)
                com.android.server.backup.restore.PerformUnifiedRestoreTask r0 = com.android.server.backup.restore.PerformUnifiedRestoreTask.this
                com.android.server.backup.restore.FullRestoreEngine r4 = r11.mEngine
                android.app.IBackupAgent r4 = r4.getAgent()
                if (r4 == 0) goto L_0x03a7
                goto L_0x03a8
            L_0x03a7:
                r3 = 0
            L_0x03a8:
                boolean unused = r0.mDidLaunch = r3
                if (r15 == 0) goto L_0x03f1
                com.android.server.backup.restore.PerformUnifiedRestoreTask r0 = com.android.server.backup.restore.PerformUnifiedRestoreTask.this     // Catch:{ Exception -> 0x03bc }
                com.android.server.backup.transport.TransportClient r0 = r0.mTransportClient     // Catch:{ Exception -> 0x03bc }
                com.android.internal.backup.IBackupTransport r0 = r0.connectOrThrow(r1)     // Catch:{ Exception -> 0x03bc }
                r0.abortFullRestore()     // Catch:{ Exception -> 0x03bc }
                goto L_0x03d6
            L_0x03bc:
                r0 = move-exception
                java.lang.StringBuilder r3 = new java.lang.StringBuilder
                r3.<init>()
                r3.append(r12)
                java.lang.String r4 = r0.getMessage()
                r3.append(r4)
                java.lang.String r3 = r3.toString()
                android.util.Slog.e(r13, r3)
                r3 = -1000(0xfffffffffffffc18, float:NaN)
                r15 = r3
            L_0x03d6:
                com.android.server.backup.restore.PerformUnifiedRestoreTask r0 = com.android.server.backup.restore.PerformUnifiedRestoreTask.this
                com.android.server.backup.UserBackupManagerService r0 = r0.backupManagerService
                com.android.server.backup.restore.PerformUnifiedRestoreTask r3 = com.android.server.backup.restore.PerformUnifiedRestoreTask.this
                android.content.pm.PackageInfo r3 = r3.mCurrentPackage
                java.lang.String r3 = r3.packageName
                r0.clearApplicationDataAfterRestoreFailure(r3)
                r3 = -1000(0xfffffffffffffc18, float:NaN)
                if (r15 != r3) goto L_0x03ee
                com.android.server.backup.restore.UnifiedRestoreState r0 = com.android.server.backup.restore.UnifiedRestoreState.FINAL
                goto L_0x0409
            L_0x03ee:
                com.android.server.backup.restore.UnifiedRestoreState r0 = com.android.server.backup.restore.UnifiedRestoreState.RUNNING_QUEUE
                goto L_0x0409
            L_0x03f1:
                com.android.server.backup.restore.UnifiedRestoreState r0 = com.android.server.backup.restore.UnifiedRestoreState.RESTORE_FINISHED
                com.android.server.backup.restore.PerformUnifiedRestoreTask r3 = com.android.server.backup.restore.PerformUnifiedRestoreTask.this
                com.android.server.backup.restore.FullRestoreEngine r4 = r11.mEngine
                android.app.IBackupAgent r4 = r4.getAgent()
                android.app.IBackupAgent unused = r3.mAgent = r4
                com.android.server.backup.restore.PerformUnifiedRestoreTask r3 = com.android.server.backup.restore.PerformUnifiedRestoreTask.this
                com.android.server.backup.restore.FullRestoreEngine r4 = r11.mEngine
                byte[] r4 = r4.getWidgetData()
                byte[] unused = r3.mWidgetData = r4
            L_0x0409:
                com.android.server.backup.restore.PerformUnifiedRestoreTask r3 = com.android.server.backup.restore.PerformUnifiedRestoreTask.this
                r3.executeNextState(r0)
                r3 = 0
                r11.setRunning(r3)
                throw r2
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.server.backup.restore.PerformUnifiedRestoreTask.StreamFeederThread.run():void");
        }

        public void execute() {
        }

        public void operationComplete(long result) {
        }

        public void handleCancel(boolean cancelAll) {
            PerformUnifiedRestoreTask.this.backupManagerService.removeOperation(this.mEphemeralOpToken);
            Slog.w("StreamFeederThread", "Full-data restore target timed out; shutting down");
            PerformUnifiedRestoreTask performUnifiedRestoreTask = PerformUnifiedRestoreTask.this;
            IBackupManagerMonitor unused = performUnifiedRestoreTask.mMonitor = BackupManagerMonitorUtils.monitorEvent(performUnifiedRestoreTask.mMonitor, 45, PerformUnifiedRestoreTask.this.mCurrentPackage, 2, (Bundle) null);
            this.mEngineThread.handleTimeout();
            IoUtils.closeQuietly(this.mEnginePipes[1]);
            ParcelFileDescriptor[] parcelFileDescriptorArr = this.mEnginePipes;
            parcelFileDescriptorArr[1] = null;
            IoUtils.closeQuietly(parcelFileDescriptorArr[0]);
            this.mEnginePipes[0] = null;
        }
    }

    private void finalizeRestore() {
        PackageManagerBackupAgent packageManagerBackupAgent;
        Slog.d(BackupManagerService.TAG, "finishing restore mObserver=" + this.mObserver);
        try {
            this.mTransportClient.connectOrThrow("PerformUnifiedRestoreTask.finalizeRestore()").finishRestore();
        } catch (Exception e) {
            Slog.e(BackupManagerService.TAG, "Error finishing restore", e);
        }
        Exception e2 = this.mObserver;
        if (e2 != null) {
            try {
                e2.restoreFinished(this.mStatus);
            } catch (RemoteException e3) {
                Slog.d(BackupManagerService.TAG, "Restore observer died at restoreFinished");
            }
        }
        this.backupManagerService.getBackupHandler().removeMessages(8);
        if (this.mPmToken > 0) {
            Slog.v(BackupManagerService.TAG, "finishing PM token " + this.mPmToken);
            try {
                this.backupManagerService.getPackageManagerBinder().finishPackageInstall(this.mPmToken, this.mDidLaunch);
            } catch (RemoteException e4) {
            }
        } else {
            this.backupManagerService.getBackupHandler().sendEmptyMessageDelayed(8, this.mAgentTimeoutParameters.getRestoreAgentTimeoutMillis());
        }
        AppWidgetBackupBridge.restoreFinished(this.mUserId);
        if (this.mIsSystemRestore && (packageManagerBackupAgent = this.mPmAgent) != null) {
            this.backupManagerService.setAncestralPackages(packageManagerBackupAgent.getRestoredPackages());
            this.backupManagerService.setAncestralToken(this.mToken);
            this.backupManagerService.writeRestoreTokens();
        }
        synchronized (this.backupManagerService.getPendingRestores()) {
            if (this.backupManagerService.getPendingRestores().size() > 0) {
                Slog.d(BackupManagerService.TAG, "Starting next pending restore.");
                this.backupManagerService.getBackupHandler().sendMessage(this.backupManagerService.getBackupHandler().obtainMessage(20, this.backupManagerService.getPendingRestores().remove()));
            } else {
                this.backupManagerService.setRestoreInProgress(false);
                Slog.d(BackupManagerService.TAG, "No pending restores.");
            }
        }
        Slog.i(BackupManagerService.TAG, "Restore complete.");
        this.mListener.onFinished("PerformUnifiedRestoreTask.finalizeRestore()");
    }

    /* access modifiers changed from: package-private */
    public void keyValueAgentErrorCleanup(boolean clearAppData) {
        if (clearAppData) {
            this.backupManagerService.clearApplicationDataAfterRestoreFailure(this.mCurrentPackage.packageName);
        }
        keyValueAgentCleanup();
    }

    /* access modifiers changed from: package-private */
    public void keyValueAgentCleanup() {
        this.mBackupDataName.delete();
        this.mStageName.delete();
        try {
            if (this.mBackupData != null) {
                this.mBackupData.close();
            }
        } catch (IOException e) {
        }
        try {
            if (this.mNewState != null) {
                this.mNewState.close();
            }
        } catch (IOException e2) {
        }
        this.mNewState = null;
        this.mBackupData = null;
        this.mNewStateName.delete();
        if (this.mCurrentPackage.applicationInfo != null) {
            try {
                this.backupManagerService.getActivityManager().unbindBackupAgent(this.mCurrentPackage.applicationInfo);
                boolean killAfterRestore = !UserHandle.isCore(this.mCurrentPackage.applicationInfo.uid) && (this.mRestoreDescription.getDataType() == 2 || (65536 & this.mCurrentPackage.applicationInfo.flags) != 0);
                if (this.mTargetPackage == null && killAfterRestore) {
                    Slog.d(BackupManagerService.TAG, "Restore complete, killing host process of " + this.mCurrentPackage.applicationInfo.processName);
                    this.backupManagerService.getActivityManager().killApplicationProcess(this.mCurrentPackage.applicationInfo.processName, this.mCurrentPackage.applicationInfo.uid);
                }
            } catch (RemoteException e3) {
            }
        }
        this.backupManagerService.getBackupHandler().removeMessages(18, this);
    }

    public void operationComplete(long unusedResult) {
        UnifiedRestoreState nextState;
        this.backupManagerService.removeOperation(this.mEphemeralOpToken);
        Slog.i(BackupManagerService.TAG, "operationComplete() during restore: target=" + this.mCurrentPackage.packageName + " state=" + this.mState);
        int i = AnonymousClass1.$SwitchMap$com$android$server$backup$restore$UnifiedRestoreState[this.mState.ordinal()];
        if (i == 1) {
            nextState = UnifiedRestoreState.RUNNING_QUEUE;
        } else if (i == 3 || i == 4) {
            nextState = UnifiedRestoreState.RESTORE_FINISHED;
        } else if (i != 5) {
            Slog.e(BackupManagerService.TAG, "Unexpected restore callback into state " + this.mState);
            keyValueAgentErrorCleanup(true);
            nextState = UnifiedRestoreState.FINAL;
        } else {
            EventLog.writeEvent(EventLogTags.RESTORE_PACKAGE, new Object[]{this.mCurrentPackage.packageName, Integer.valueOf((int) this.mBackupDataName.length())});
            keyValueAgentCleanup();
            if (this.mWidgetData != null) {
                this.backupManagerService.restoreWidgetData(this.mCurrentPackage.packageName, this.mWidgetData);
            }
            nextState = UnifiedRestoreState.RUNNING_QUEUE;
        }
        executeNextState(nextState);
    }

    public void handleCancel(boolean cancelAll) {
        this.backupManagerService.removeOperation(this.mEphemeralOpToken);
        Slog.e(BackupManagerService.TAG, "Timeout restoring application " + this.mCurrentPackage.packageName);
        this.mMonitor = BackupManagerMonitorUtils.monitorEvent(this.mMonitor, 31, this.mCurrentPackage, 2, (Bundle) null);
        EventLog.writeEvent(EventLogTags.RESTORE_AGENT_FAILURE, new Object[]{this.mCurrentPackage.packageName, "restore timeout"});
        keyValueAgentErrorCleanup(true);
        executeNextState(UnifiedRestoreState.RUNNING_QUEUE);
    }

    /* access modifiers changed from: package-private */
    public void executeNextState(UnifiedRestoreState nextState) {
        Slog.i(BackupManagerService.TAG, " => executing next step on " + this + " nextState=" + nextState);
        this.mState = nextState;
        this.backupManagerService.getBackupHandler().sendMessage(this.backupManagerService.getBackupHandler().obtainMessage(20, this));
    }

    /* access modifiers changed from: package-private */
    public void sendStartRestore(int numPackages) {
        IRestoreObserver iRestoreObserver = this.mObserver;
        if (iRestoreObserver != null) {
            try {
                iRestoreObserver.restoreStarting(numPackages);
            } catch (RemoteException e) {
                Slog.w(BackupManagerService.TAG, "Restore observer went away: startRestore");
                this.mObserver = null;
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void sendOnRestorePackage(String name) {
        IRestoreObserver iRestoreObserver = this.mObserver;
        if (iRestoreObserver != null) {
            try {
                iRestoreObserver.onUpdate(this.mCount, name);
            } catch (RemoteException e) {
                Slog.d(BackupManagerService.TAG, "Restore observer died in onUpdate");
                this.mObserver = null;
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void sendEndRestore() {
        IRestoreObserver iRestoreObserver = this.mObserver;
        if (iRestoreObserver != null) {
            try {
                iRestoreObserver.restoreFinished(this.mStatus);
            } catch (RemoteException e) {
                Slog.w(BackupManagerService.TAG, "Restore observer went away: endRestore");
                this.mObserver = null;
            }
        }
    }
}
