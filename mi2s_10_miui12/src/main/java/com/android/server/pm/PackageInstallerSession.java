package com.android.server.pm;

import android.app.admin.DevicePolicyEventLogger;
import android.app.admin.DevicePolicyManagerInternal;
import android.content.Context;
import android.content.IIntentReceiver;
import android.content.IIntentSender;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageInstallObserver2;
import android.content.pm.IPackageInstallerSession;
import android.content.pm.PackageInfo;
import android.content.pm.PackageInstaller;
import android.content.pm.PackageManager;
import android.content.pm.PackageParser;
import android.content.pm.dex.DexMetadataHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.biometrics.fingerprint.V2_1.RequestStatus;
import android.os.Binder;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.FileBridge;
import android.os.FileUtils;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.ParcelFileDescriptor;
import android.os.ParcelableException;
import android.os.RemoteException;
import android.os.RevocableFileDescriptor;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.os.storage.StorageManager;
import android.system.ErrnoException;
import android.system.Int64Ref;
import android.system.Os;
import android.system.OsConstants;
import android.system.StructStat;
import android.text.TextUtils;
import android.util.ArraySet;
import android.util.ExceptionUtils;
import android.util.MathUtils;
import android.util.Slog;
import android.util.SparseIntArray;
import android.util.apk.ApkSignatureVerifier;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.content.NativeLibraryHelper;
import com.android.internal.content.PackageHelper;
import com.android.internal.os.SomeArgs;
import com.android.internal.util.ArrayUtils;
import com.android.internal.util.IndentingPrintWriter;
import com.android.internal.util.Preconditions;
import com.android.internal.util.XmlUtils;
import com.android.server.LocalServices;
import com.android.server.pm.Installer;
import com.android.server.pm.PackageInstallerService;
import com.android.server.pm.PackageInstallerSession;
import com.android.server.pm.PackageManagerService;
import com.android.server.pm.dex.DexManager;
import com.android.server.security.VerityUtils;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileFilter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import libcore.io.IoUtils;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

public class PackageInstallerSession extends IPackageInstallerSession.Stub {
    private static final String ATTR_ABI_OVERRIDE = "abiOverride";
    @Deprecated
    private static final String ATTR_APP_ICON = "appIcon";
    private static final String ATTR_APP_LABEL = "appLabel";
    private static final String ATTR_APP_PACKAGE_NAME = "appPackageName";
    private static final String ATTR_COMMITTED = "committed";
    private static final String ATTR_CREATED_MILLIS = "createdMillis";
    private static final String ATTR_INSTALLER_PACKAGE_NAME = "installerPackageName";
    private static final String ATTR_INSTALLER_UID = "installerUid";
    private static final String ATTR_INSTALL_FLAGS = "installFlags";
    private static final String ATTR_INSTALL_LOCATION = "installLocation";
    private static final String ATTR_INSTALL_REASON = "installRason";
    private static final String ATTR_IS_APPLIED = "isApplied";
    private static final String ATTR_IS_FAILED = "isFailed";
    private static final String ATTR_IS_READY = "isReady";
    private static final String ATTR_MODE = "mode";
    private static final String ATTR_MULTI_PACKAGE = "multiPackage";
    private static final String ATTR_NAME = "name";
    private static final String ATTR_ORIGINATING_UID = "originatingUid";
    private static final String ATTR_ORIGINATING_URI = "originatingUri";
    private static final String ATTR_PARENT_SESSION_ID = "parentSessionId";
    private static final String ATTR_PREPARED = "prepared";
    private static final String ATTR_REFERRER_URI = "referrerUri";
    private static final String ATTR_SEALED = "sealed";
    private static final String ATTR_SESSION_ID = "sessionId";
    private static final String ATTR_SESSION_STAGE_CID = "sessionStageCid";
    private static final String ATTR_SESSION_STAGE_DIR = "sessionStageDir";
    private static final String ATTR_SIZE_BYTES = "sizeBytes";
    private static final String ATTR_STAGED_SESSION = "stagedSession";
    private static final String ATTR_STAGED_SESSION_ERROR_CODE = "errorCode";
    private static final String ATTR_STAGED_SESSION_ERROR_MESSAGE = "errorMessage";
    private static final String ATTR_UPDATED_MILLIS = "updatedMillis";
    private static final String ATTR_USER_ID = "userId";
    private static final String ATTR_VOLUME_UUID = "volumeUuid";
    private static final int[] EMPTY_CHILD_SESSION_ARRAY = new int[0];
    private static final boolean LOGD = true;
    private static final int MSG_COMMIT = 1;
    private static final int MSG_ON_PACKAGE_INSTALLED = 2;
    private static final String PROPERTY_NAME_INHERIT_NATIVE = "pi.inherit_native_on_dont_kill";
    private static final String REMOVE_SPLIT_MARKER_EXTENSION = ".removed";
    private static final String TAG = "PackageInstallerSession";
    static final String TAG_CHILD_SESSION = "childSession";
    private static final String TAG_GRANTED_RUNTIME_PERMISSION = "granted-runtime-permission";
    static final String TAG_SESSION = "session";
    private static final String TAG_WHITELISTED_RESTRICTED_PERMISSION = "whitelisted-restricted-permission";
    private static final FileFilter sAddedFilter = new FileFilter() {
        public boolean accept(File file) {
            if (!file.isDirectory() && !file.getName().endsWith(PackageInstallerSession.REMOVE_SPLIT_MARKER_EXTENSION) && !DexMetadataHelper.isDexMetadataFile(file) && !VerityUtils.isFsveritySignatureFile(file)) {
                return true;
            }
            return false;
        }
    };
    private static final FileFilter sRemovedFilter = new FileFilter() {
        public boolean accept(File file) {
            if (!file.isDirectory() && file.getName().endsWith(PackageInstallerSession.REMOVE_SPLIT_MARKER_EXTENSION)) {
                return true;
            }
            return false;
        }
    };
    final long createdMillis;
    private final AtomicInteger mActiveCount = new AtomicInteger();
    @GuardedBy({"mLock"})
    private final ArrayList<FileBridge> mBridges = new ArrayList<>();
    private final PackageInstallerService.InternalCallback mCallback;
    @GuardedBy({"mLock"})
    private SparseIntArray mChildSessionIds = new SparseIntArray();
    @GuardedBy({"mLock"})
    private float mClientProgress = 0.0f;
    @GuardedBy({"mLock"})
    private boolean mCommitted = false;
    /* access modifiers changed from: private */
    public final Context mContext;
    @GuardedBy({"mLock"})
    private boolean mDestroyed = false;
    @GuardedBy({"mLock"})
    private final ArrayList<RevocableFileDescriptor> mFds = new ArrayList<>();
    @GuardedBy({"mLock"})
    private String mFinalMessage;
    @GuardedBy({"mLock"})
    private int mFinalStatus;
    /* access modifiers changed from: private */
    public final Handler mHandler;
    private final Handler.Callback mHandlerCallback = new Handler.Callback() {
        /* Debug info: failed to restart local var, previous not found, register: 9 */
        public boolean handleMessage(Message msg) {
            PackageInfo pkgInfo = PackageInstallerSession.this.mPm.getPackageInfo(PackageInstallerSession.this.params.appPackageName, 67108928, PackageInstallerSession.this.userId);
            if (msg.what == 1 && !PackageInstallerSession.this.params.isMultiPackage) {
                try {
                    long beginMiuiVerify = SystemClock.uptimeMillis();
                    synchronized (PackageInstallerSession.this.mLock) {
                        File unused = PackageInstallerSession.this.resolveStageDirLocked();
                        if (PackageInstallerSession.this.mInstallerUid == 2000) {
                            PackageInstallerSession.this.validateApkInstallLocked(pkgInfo);
                        }
                    }
                    if (PackageManagerServiceInjector.isAllowedInstall(PackageInstallerSession.this.mContext, PackageInstallerSession.this.mResolvedStageDir, PackageInstallerSession.this.mInstallerUid, PackageInstallerSession.this.mInstallerPackageName)) {
                        PackageInstallerSession.this.mStatistic.timeMiuiSecurityCheck = SystemClock.uptimeMillis() - beginMiuiVerify;
                    } else {
                        throw new PackageManagerException(-111, "Failed INSTALL_CANCELED_BY_USER");
                    }
                } catch (IOException e) {
                    throw new PackageManagerException(-18, "Failed to resolve stage location", e);
                } catch (PackageManagerException e2) {
                    String completeMsg = ExceptionUtils.getCompleteMessage(e2);
                    Slog.e(PackageInstallerSession.TAG, "Commit of session " + PackageInstallerSession.this.sessionId + " failed: " + completeMsg);
                    PackageInstallerSession.this.destroyInternal();
                    PackageInstallerSession.this.dispatchSessionFinished(e2.error, completeMsg, (Bundle) null);
                    return true;
                }
            }
            int i = msg.what;
            if (i == 1) {
                PackageInstallerSession.this.handleCommit();
            } else if (i == 2) {
                SomeArgs args = (SomeArgs) msg.obj;
                String packageName = (String) args.arg1;
                String message = (String) args.arg2;
                Bundle extras = (Bundle) args.arg3;
                IPackageInstallObserver2 observer = (IPackageInstallObserver2) args.arg4;
                int returnCode = args.argi1;
                args.recycle();
                try {
                    observer.onPackageInstalled(packageName, returnCode, message, extras);
                } catch (RemoteException e3) {
                }
            }
            return true;
        }
    };
    @GuardedBy({"mLock"})
    private File mInheritedFilesBase;
    /* access modifiers changed from: private */
    @GuardedBy({"mLock"})
    public String mInstallerPackageName;
    /* access modifiers changed from: private */
    @GuardedBy({"mLock"})
    public int mInstallerUid;
    @GuardedBy({"mLock"})
    private float mInternalProgress = 0.0f;
    /* access modifiers changed from: private */
    public final Object mLock = new Object();
    private final int mOriginalInstallerUid;
    @GuardedBy({"mLock"})
    private String mPackageName;
    @GuardedBy({"mLock"})
    private int mParentSessionId;
    @GuardedBy({"mLock"})
    private boolean mPermissionsManuallyAccepted = false;
    /* access modifiers changed from: private */
    public final PackageManagerService mPm;
    @GuardedBy({"mLock"})
    private boolean mPrepared = false;
    @GuardedBy({"mLock"})
    private float mProgress = 0.0f;
    @GuardedBy({"mLock"})
    private boolean mRelinquished = false;
    @GuardedBy({"mLock"})
    private IPackageInstallObserver2 mRemoteObserver;
    @GuardedBy({"mLock"})
    private float mReportedProgress = -1.0f;
    @GuardedBy({"mLock"})
    private File mResolvedBaseFile;
    @GuardedBy({"mLock"})
    private final List<File> mResolvedInheritedFiles = new ArrayList();
    @GuardedBy({"mLock"})
    private final List<String> mResolvedInstructionSets = new ArrayList();
    @GuardedBy({"mLock"})
    private final List<String> mResolvedNativeLibPaths = new ArrayList();
    /* access modifiers changed from: private */
    @GuardedBy({"mLock"})
    public File mResolvedStageDir;
    @GuardedBy({"mLock"})
    private final List<File> mResolvedStagedFiles = new ArrayList();
    @GuardedBy({"mLock"})
    private boolean mSealed = false;
    private final PackageSessionProvider mSessionProvider;
    @GuardedBy({"mLock"})
    private boolean mShouldBeSealed = false;
    @GuardedBy({"mLock"})
    private PackageParser.SigningDetails mSigningDetails;
    @GuardedBy({"mLock"})
    private boolean mStagedSessionApplied;
    @GuardedBy({"mLock"})
    private int mStagedSessionErrorCode = 0;
    @GuardedBy({"mLock"})
    private String mStagedSessionErrorMessage;
    @GuardedBy({"mLock"})
    private boolean mStagedSessionFailed;
    @GuardedBy({"mLock"})
    private boolean mStagedSessionReady;
    private final StagingManager mStagingManager;
    final InstallationStatistic mStatistic = new InstallationStatistic();
    @GuardedBy({"mLock"})
    private boolean mVerityFound;
    @GuardedBy({"mLock"})
    private long mVersionCode;
    final PackageInstaller.SessionParams params;
    /* access modifiers changed from: package-private */
    public final int sessionId;
    final String stageCid;
    final File stageDir;
    @GuardedBy({"mLock"})
    private long updatedMillis;
    final int userId;

    @GuardedBy({"mLock"})
    private boolean isInstallerDeviceOwnerOrAffiliatedProfileOwnerLocked() {
        DevicePolicyManagerInternal dpmi;
        if (this.userId == UserHandle.getUserId(this.mInstallerUid) && (dpmi = (DevicePolicyManagerInternal) LocalServices.getService(DevicePolicyManagerInternal.class)) != null && dpmi.canSilentlyInstallPackage(this.mInstallerPackageName, this.mInstallerUid)) {
            return true;
        }
        return false;
    }

    @GuardedBy({"mLock"})
    private boolean needToAskForPermissionsLocked() {
        if (this.mPermissionsManuallyAccepted) {
            return false;
        }
        boolean isInstallPermissionGranted = this.mPm.checkUidPermission("android.permission.INSTALL_PACKAGES", this.mInstallerUid) == 0;
        boolean isSelfUpdatePermissionGranted = this.mPm.checkUidPermission("android.permission.INSTALL_SELF_UPDATES", this.mInstallerUid) == 0;
        boolean isUpdatePermissionGranted = this.mPm.checkUidPermission("android.permission.INSTALL_PACKAGE_UPDATES", this.mInstallerUid) == 0;
        int targetPackageUid = this.mPm.getPackageUid(this.mPackageName, 0, this.userId);
        boolean isPermissionGranted = isInstallPermissionGranted || (isUpdatePermissionGranted && targetPackageUid != -1) || (isSelfUpdatePermissionGranted && targetPackageUid == this.mInstallerUid);
        boolean isInstallerRoot = this.mInstallerUid == 0;
        boolean isInstallerSystem = this.mInstallerUid == 1000;
        if (((this.params.installFlags & 1024) != 0) || (!isPermissionGranted && !isInstallerRoot && !isInstallerSystem && !isInstallerDeviceOwnerOrAffiliatedProfileOwnerLocked())) {
            return true;
        }
        return false;
    }

    public PackageInstallerSession(PackageInstallerService.InternalCallback callback, Context context, PackageManagerService pm, PackageSessionProvider sessionProvider, Looper looper, StagingManager stagingManager, int sessionId2, int userId2, String installerPackageName, int installerUid, PackageInstaller.SessionParams params2, long createdMillis2, File stageDir2, String stageCid2, boolean prepared, boolean committed, boolean sealed, int[] childSessionIds, int parentSessionId, boolean isReady, boolean isFailed, boolean isApplied, int stagedSessionErrorCode, String stagedSessionErrorMessage) {
        boolean z;
        int i = sessionId2;
        int i2 = installerUid;
        PackageInstaller.SessionParams sessionParams = params2;
        long j = createdMillis2;
        File file = stageDir2;
        String str = stageCid2;
        int[] iArr = childSessionIds;
        this.mCallback = callback;
        this.mContext = context;
        this.mPm = pm;
        this.mSessionProvider = sessionProvider;
        this.mHandler = new Handler(looper, this.mHandlerCallback);
        this.mStagingManager = stagingManager;
        this.mStatistic.timeBeginInstall = SystemClock.uptimeMillis();
        Slog.i("InstallationStatistic", "beginInstallSession: " + i);
        this.sessionId = i;
        this.userId = userId2;
        this.mOriginalInstallerUid = i2;
        this.mInstallerPackageName = installerPackageName;
        this.mInstallerUid = i2;
        this.params = sessionParams;
        this.createdMillis = j;
        this.updatedMillis = j;
        this.stageDir = file;
        this.stageCid = str;
        this.mShouldBeSealed = sealed;
        if (iArr != null) {
            int i3 = 0;
            for (int length = iArr.length; i3 < length; length = length) {
                this.mChildSessionIds.put(iArr[i3], 0);
                i3++;
                long j2 = createdMillis2;
            }
            z = false;
        } else {
            z = false;
        }
        this.mParentSessionId = parentSessionId;
        if (!sessionParams.isMultiPackage) {
            if ((file == null ? true : z) == (str != null ? z : true)) {
                throw new IllegalArgumentException("Exactly one of stageDir or stageCid stage must be set");
            }
        }
        this.mPrepared = prepared;
        this.mCommitted = committed;
        this.mStagedSessionReady = isReady;
        this.mStagedSessionFailed = isFailed;
        this.mStagedSessionApplied = isApplied;
        this.mStagedSessionErrorCode = stagedSessionErrorCode;
        this.mStagedSessionErrorMessage = stagedSessionErrorMessage != null ? stagedSessionErrorMessage : "";
    }

    public PackageInstaller.SessionInfo generateInfo() {
        return generateInfo(true);
    }

    public PackageInstaller.SessionInfo generateInfo(boolean includeIcon) {
        PackageInstaller.SessionInfo info = new PackageInstaller.SessionInfo();
        synchronized (this.mLock) {
            info.sessionId = this.sessionId;
            info.userId = this.userId;
            info.installerPackageName = this.mInstallerPackageName;
            info.resolvedBaseCodePath = this.mResolvedBaseFile != null ? this.mResolvedBaseFile.getAbsolutePath() : null;
            info.progress = this.mProgress;
            info.sealed = this.mSealed;
            info.isCommitted = this.mCommitted;
            info.active = this.mActiveCount.get() > 0;
            info.mode = this.params.mode;
            info.installReason = this.params.installReason;
            info.sizeBytes = this.params.sizeBytes;
            info.appPackageName = this.params.appPackageName;
            if (includeIcon) {
                info.appIcon = this.params.appIcon;
            }
            info.appLabel = this.params.appLabel;
            info.installLocation = this.params.installLocation;
            info.originatingUri = this.params.originatingUri;
            info.originatingUid = this.params.originatingUid;
            info.referrerUri = this.params.referrerUri;
            info.grantedRuntimePermissions = this.params.grantedRuntimePermissions;
            info.whitelistedRestrictedPermissions = this.params.whitelistedRestrictedPermissions;
            info.installFlags = this.params.installFlags;
            info.isMultiPackage = this.params.isMultiPackage;
            info.isStaged = this.params.isStaged;
            info.parentSessionId = this.mParentSessionId;
            info.childSessionIds = this.mChildSessionIds.copyKeys();
            if (info.childSessionIds == null) {
                info.childSessionIds = EMPTY_CHILD_SESSION_ARRAY;
            }
            info.isStagedSessionApplied = this.mStagedSessionApplied;
            info.isStagedSessionReady = this.mStagedSessionReady;
            info.isStagedSessionFailed = this.mStagedSessionFailed;
            info.setStagedSessionErrorCode(this.mStagedSessionErrorCode, this.mStagedSessionErrorMessage);
            info.updatedMillis = this.updatedMillis;
        }
        return info;
    }

    public boolean isPrepared() {
        boolean z;
        synchronized (this.mLock) {
            z = this.mPrepared;
        }
        return z;
    }

    public boolean isSealed() {
        boolean z;
        synchronized (this.mLock) {
            z = this.mSealed;
        }
        return z;
    }

    /* access modifiers changed from: package-private */
    public boolean isCommitted() {
        boolean z;
        synchronized (this.mLock) {
            z = this.mCommitted;
        }
        return z;
    }

    public boolean isStagedAndInTerminalState() {
        boolean z;
        synchronized (this.mLock) {
            z = this.params.isStaged && (this.mStagedSessionApplied || this.mStagedSessionFailed);
        }
        return z;
    }

    @GuardedBy({"mLock"})
    private void assertPreparedAndNotSealedLocked(String cookie) {
        assertPreparedAndNotCommittedOrDestroyedLocked(cookie);
        if (this.mSealed) {
            throw new SecurityException(cookie + " not allowed after sealing");
        }
    }

    @GuardedBy({"mLock"})
    private void assertPreparedAndNotCommittedOrDestroyedLocked(String cookie) {
        assertPreparedAndNotDestroyedLocked(cookie);
        if (this.mCommitted) {
            throw new SecurityException(cookie + " not allowed after commit");
        }
    }

    @GuardedBy({"mLock"})
    private void assertPreparedAndNotDestroyedLocked(String cookie) {
        if (!this.mPrepared) {
            throw new IllegalStateException(cookie + " before prepared");
        } else if (this.mDestroyed) {
            throw new SecurityException(cookie + " not allowed after destruction");
        }
    }

    /* access modifiers changed from: private */
    @GuardedBy({"mLock"})
    public File resolveStageDirLocked() throws IOException {
        if (this.mResolvedStageDir == null) {
            File file = this.stageDir;
            if (file != null) {
                this.mResolvedStageDir = file;
            } else {
                throw new IOException("Missing stageDir");
            }
        }
        return this.mResolvedStageDir;
    }

    public void setClientProgress(float progress) {
        synchronized (this.mLock) {
            assertCallerIsOwnerOrRootLocked();
            boolean forcePublish = this.mClientProgress == 0.0f;
            this.mClientProgress = progress;
            computeProgressLocked(forcePublish);
        }
    }

    public void addClientProgress(float progress) {
        synchronized (this.mLock) {
            assertCallerIsOwnerOrRootLocked();
            setClientProgress(this.mClientProgress + progress);
        }
    }

    @GuardedBy({"mLock"})
    private void computeProgressLocked(boolean forcePublish) {
        this.mProgress = MathUtils.constrain(this.mClientProgress * 0.8f, 0.0f, 0.8f) + MathUtils.constrain(this.mInternalProgress * 0.2f, 0.0f, 0.2f);
        if (forcePublish || ((double) Math.abs(this.mProgress - this.mReportedProgress)) >= 0.01d) {
            float f = this.mProgress;
            this.mReportedProgress = f;
            this.mCallback.onSessionProgressChanged(this, f);
        }
    }

    /* Debug info: failed to restart local var, previous not found, register: 3 */
    public String[] getNames() {
        String[] list;
        synchronized (this.mLock) {
            assertCallerIsOwnerOrRootLocked();
            assertPreparedAndNotCommittedOrDestroyedLocked("getNames");
            try {
                list = resolveStageDirLocked().list();
            } catch (IOException e) {
                throw ExceptionUtils.wrap(e);
            }
        }
        return list;
    }

    /* Debug info: failed to restart local var, previous not found, register: 3 */
    public void removeSplit(String splitName) {
        if (!TextUtils.isEmpty(this.params.appPackageName)) {
            synchronized (this.mLock) {
                assertCallerIsOwnerOrRootLocked();
                assertPreparedAndNotCommittedOrDestroyedLocked("removeSplit");
                try {
                    createRemoveSplitMarkerLocked(splitName);
                } catch (IOException e) {
                    throw ExceptionUtils.wrap(e);
                }
            }
            return;
        }
        throw new IllegalStateException("Must specify package name to remove a split");
    }

    /* Debug info: failed to restart local var, previous not found, register: 4 */
    private void createRemoveSplitMarkerLocked(String splitName) throws IOException {
        try {
            String markerName = splitName + REMOVE_SPLIT_MARKER_EXTENSION;
            if (FileUtils.isValidExtFilename(markerName)) {
                File target = new File(resolveStageDirLocked(), markerName);
                target.createNewFile();
                Os.chmod(target.getAbsolutePath(), 0);
                return;
            }
            throw new IllegalArgumentException("Invalid marker: " + markerName);
        } catch (ErrnoException e) {
            throw e.rethrowAsIOException();
        }
    }

    public ParcelFileDescriptor openWrite(String name, long offsetBytes, long lengthBytes) {
        try {
            return doWriteInternal(name, offsetBytes, lengthBytes, (ParcelFileDescriptor) null);
        } catch (IOException e) {
            throw ExceptionUtils.wrap(e);
        }
    }

    public void write(String name, long offsetBytes, long lengthBytes, ParcelFileDescriptor fd) {
        try {
            doWriteInternal(name, offsetBytes, lengthBytes, fd);
        } catch (IOException e) {
            throw ExceptionUtils.wrap(e);
        }
    }

    private ParcelFileDescriptor doWriteInternal(String name, long offsetBytes, long lengthBytes, ParcelFileDescriptor incomingFd) throws IOException {
        return doWriteInternal(name, offsetBytes, lengthBytes, incomingFd, false);
    }

    /* Debug info: failed to restart local var, previous not found, register: 22 */
    /* access modifiers changed from: protected */
    public ParcelFileDescriptor doWriteInternal(String name, long offsetBytes, long lengthBytes, ParcelFileDescriptor incomingFd, boolean fdTrusted) throws IOException {
        FileBridge bridge;
        RevocableFileDescriptor fd;
        File stageDir2;
        FileDescriptor targetFd;
        String str = name;
        long j = offsetBytes;
        long j2 = lengthBytes;
        synchronized (this.mLock) {
            assertCallerIsOwnerOrRootLocked();
            assertPreparedAndNotSealedLocked("openWrite");
            if (PackageInstaller.ENABLE_REVOCABLE_FD) {
                RevocableFileDescriptor fd2 = new RevocableFileDescriptor();
                this.mFds.add(fd2);
                fd = fd2;
                bridge = null;
            } else {
                FileBridge bridge2 = new FileBridge();
                this.mBridges.add(bridge2);
                fd = null;
                bridge = bridge2;
            }
            stageDir2 = resolveStageDirLocked();
        }
        try {
            if (FileUtils.isValidExtFilename(name)) {
                long identity = Binder.clearCallingIdentity();
                try {
                    File target = new File(stageDir2, str);
                    Binder.restoreCallingIdentity(identity);
                    FileDescriptor targetFd2 = Os.open(target.getAbsolutePath(), OsConstants.O_CREAT | OsConstants.O_WRONLY, 420);
                    Os.chmod(target.getAbsolutePath(), 420);
                    if (stageDir2 != null && j2 > 0) {
                        try {
                            ((StorageManager) this.mContext.getSystemService(StorageManager.class)).allocateBytes(targetFd2, j2, PackageHelper.translateAllocateFlags(this.params.installFlags));
                        } catch (ErrnoException e) {
                            e = e;
                            File file = stageDir2;
                            throw e.rethrowAsIOException();
                        }
                    }
                    if (j > 0) {
                        Os.lseek(targetFd2, j, OsConstants.SEEK_SET);
                    }
                    if (incomingFd != null) {
                        int callingUid = Binder.getCallingUid();
                        if (callingUid == 0 || callingUid == 1000 || callingUid == 2000 || fdTrusted) {
                            try {
                                Int64Ref last = new Int64Ref(0);
                                targetFd = targetFd2;
                                File file2 = stageDir2;
                                try {
                                    FileUtils.copy(incomingFd.getFileDescriptor(), targetFd2, lengthBytes, (CancellationSignal) null, $$Lambda$_14QHG018Z6p13d3hzJuGTWnNeo.INSTANCE, new FileUtils.ProgressListener(last) {
                                        private final /* synthetic */ Int64Ref f$1;

                                        {
                                            this.f$1 = r2;
                                        }

                                        public final void onProgress(long j) {
                                            PackageInstallerSession.this.lambda$doWriteInternal$0$PackageInstallerSession(this.f$1, j);
                                        }
                                    });
                                    IoUtils.closeQuietly(targetFd);
                                    IoUtils.closeQuietly(incomingFd);
                                    synchronized (this.mLock) {
                                        try {
                                            if (PackageInstaller.ENABLE_REVOCABLE_FD) {
                                                this.mFds.remove(fd);
                                            } else {
                                                bridge.forceClose();
                                                this.mBridges.remove(bridge);
                                            }
                                        } catch (Throwable th) {
                                            th = th;
                                            throw th;
                                        }
                                    }
                                    return null;
                                } catch (Throwable th2) {
                                    th = th2;
                                }
                            } catch (Throwable th3) {
                                th = th3;
                                targetFd = targetFd2;
                                File file3 = stageDir2;
                                IoUtils.closeQuietly(targetFd);
                                IoUtils.closeQuietly(incomingFd);
                                synchronized (this.mLock) {
                                    try {
                                        if (PackageInstaller.ENABLE_REVOCABLE_FD) {
                                            this.mFds.remove(fd);
                                        } else {
                                            bridge.forceClose();
                                            this.mBridges.remove(bridge);
                                        }
                                    } catch (Throwable th4) {
                                        th = th4;
                                    }
                                }
                                throw th;
                            }
                        } else {
                            throw new SecurityException("Reverse mode only supported from shell or system");
                        }
                    } else {
                        FileDescriptor targetFd3 = targetFd2;
                        File file4 = stageDir2;
                        if (PackageInstaller.ENABLE_REVOCABLE_FD) {
                            fd.init(this.mContext, targetFd3);
                            return fd.getRevocableFileDescriptor();
                        }
                        bridge.setTargetFile(targetFd3);
                        bridge.start();
                        return new ParcelFileDescriptor(bridge.getClientSocket());
                    }
                } catch (ErrnoException e2) {
                    e = e2;
                    throw e.rethrowAsIOException();
                } catch (Throwable th5) {
                    File file5 = stageDir2;
                    Binder.restoreCallingIdentity(identity);
                    throw th5;
                }
            } else {
                File file6 = stageDir2;
                throw new IllegalArgumentException("Invalid name: " + str);
            }
        } catch (ErrnoException e3) {
            e = e3;
            File file7 = stageDir2;
            throw e.rethrowAsIOException();
        }
    }

    public /* synthetic */ void lambda$doWriteInternal$0$PackageInstallerSession(Int64Ref last, long progress) {
        if (this.params.sizeBytes > 0) {
            last.value = progress;
            addClientProgress(((float) (progress - last.value)) / ((float) this.params.sizeBytes));
        }
    }

    /* Debug info: failed to restart local var, previous not found, register: 3 */
    public ParcelFileDescriptor openRead(String name) {
        ParcelFileDescriptor openReadInternalLocked;
        synchronized (this.mLock) {
            assertCallerIsOwnerOrRootLocked();
            assertPreparedAndNotCommittedOrDestroyedLocked("openRead");
            try {
                openReadInternalLocked = openReadInternalLocked(name);
            } catch (IOException e) {
                throw ExceptionUtils.wrap(e);
            }
        }
        return openReadInternalLocked;
    }

    /* Debug info: failed to restart local var, previous not found, register: 4 */
    private ParcelFileDescriptor openReadInternalLocked(String name) throws IOException {
        try {
            if (FileUtils.isValidExtFilename(name)) {
                return new ParcelFileDescriptor(Os.open(new File(resolveStageDirLocked(), name).getAbsolutePath(), OsConstants.O_RDONLY, 0));
            }
            throw new IllegalArgumentException("Invalid name: " + name);
        } catch (ErrnoException e) {
            throw e.rethrowAsIOException();
        }
    }

    @GuardedBy({"mLock"})
    private void assertCallerIsOwnerOrRootLocked() {
        int callingUid = Binder.getCallingUid();
        if (callingUid != 0 && callingUid != this.mInstallerUid) {
            throw new SecurityException("Session does not belong to uid " + callingUid);
        }
    }

    @GuardedBy({"mLock"})
    private void assertNoWriteFileTransfersOpenLocked() {
        Iterator<RevocableFileDescriptor> it = this.mFds.iterator();
        while (it.hasNext()) {
            if (!it.next().isRevoked()) {
                throw new SecurityException("Files still open");
            }
        }
        Iterator<FileBridge> it2 = this.mBridges.iterator();
        while (it2.hasNext()) {
            if (!it2.next().isClosed()) {
                throw new SecurityException("Files still open");
            }
        }
    }

    public void commit(IntentSender statusReceiver, boolean forTransfer) {
        if (hasParentSessionId()) {
            throw new IllegalStateException("Session " + this.sessionId + " is a child of multi-package session " + this.mParentSessionId + " and may not be committed directly.");
        } else if (markAsCommitted(statusReceiver, forTransfer)) {
            if (isMultiPackage()) {
                IntentSender childIntentSender = new ChildStatusIntentReceiver(this.mChildSessionIds.clone(), statusReceiver).getIntentSender();
                RuntimeException commitException = null;
                boolean commitFailed = false;
                for (int i = this.mChildSessionIds.size() - 1; i >= 0; i--) {
                    try {
                        if (!this.mSessionProvider.getSession(this.mChildSessionIds.keyAt(i)).markAsCommitted(childIntentSender, forTransfer)) {
                            commitFailed = true;
                        }
                    } catch (RuntimeException e) {
                        commitException = e;
                    }
                }
                if (commitException != null) {
                    throw commitException;
                } else if (commitFailed) {
                    return;
                }
            }
            this.mHandler.obtainMessage(1).sendToTarget();
        }
    }

    private class ChildStatusIntentReceiver {
        private final SparseIntArray mChildSessionsRemaining;
        private final IIntentSender.Stub mLocalSender;
        private final IntentSender mStatusReceiver;

        private ChildStatusIntentReceiver(SparseIntArray remainingSessions, IntentSender statusReceiver) {
            this.mLocalSender = new IIntentSender.Stub() {
                public void send(int code, Intent intent, String resolvedType, IBinder whitelistToken, IIntentReceiver finishedReceiver, String requiredPermission, Bundle options) {
                    ChildStatusIntentReceiver.this.statusUpdate(intent);
                }
            };
            this.mChildSessionsRemaining = remainingSessions;
            this.mStatusReceiver = statusReceiver;
        }

        public IntentSender getIntentSender() {
            return new IntentSender(this.mLocalSender);
        }

        public void statusUpdate(Intent intent) {
            PackageInstallerSession.this.mHandler.post(new Runnable(intent) {
                private final /* synthetic */ Intent f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    PackageInstallerSession.ChildStatusIntentReceiver.this.lambda$statusUpdate$0$PackageInstallerSession$ChildStatusIntentReceiver(this.f$1);
                }
            });
        }

        public /* synthetic */ void lambda$statusUpdate$0$PackageInstallerSession$ChildStatusIntentReceiver(Intent intent) {
            if (this.mChildSessionsRemaining.size() != 0) {
                int sessionId = intent.getIntExtra("android.content.pm.extra.SESSION_ID", 0);
                int status = intent.getIntExtra("android.content.pm.extra.STATUS", 1);
                int sessionIndex = this.mChildSessionsRemaining.indexOfKey(sessionId);
                if (status == 0) {
                    this.mChildSessionsRemaining.removeAt(sessionIndex);
                    if (this.mChildSessionsRemaining.size() == 0) {
                        try {
                            intent.putExtra("android.content.pm.extra.SESSION_ID", PackageInstallerSession.this.sessionId);
                            this.mStatusReceiver.sendIntent(PackageInstallerSession.this.mContext, 0, intent, (IntentSender.OnFinished) null, (Handler) null);
                        } catch (IntentSender.SendIntentException e) {
                        }
                    }
                } else if (-1 == status) {
                    try {
                        this.mStatusReceiver.sendIntent(PackageInstallerSession.this.mContext, 0, intent, (IntentSender.OnFinished) null, (Handler) null);
                    } catch (IntentSender.SendIntentException e2) {
                    }
                } else {
                    intent.putExtra("android.content.pm.extra.SESSION_ID", PackageInstallerSession.this.sessionId);
                    this.mChildSessionsRemaining.clear();
                    try {
                        this.mStatusReceiver.sendIntent(PackageInstallerSession.this.mContext, 0, intent, (IntentSender.OnFinished) null, (Handler) null);
                    } catch (IntentSender.SendIntentException e3) {
                    }
                }
            }
        }
    }

    /* Debug info: failed to restart local var, previous not found, register: 11 */
    /* JADX WARNING: Code restructure failed: missing block: B:31:0x008c, code lost:
        if (r4 != false) goto L_0x0093;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:32:0x008e, code lost:
        r11.mCallback.onSessionSealedBlocking(r11);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:33:0x0093, code lost:
        return true;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean markAsCommitted(android.content.IntentSender r12, boolean r13) {
        /*
            r11 = this;
            com.android.internal.util.Preconditions.checkNotNull(r12)
            java.util.List r0 = r11.getChildSessions()
            java.lang.Object r1 = r11.mLock
            monitor-enter(r1)
            r11.assertCallerIsOwnerOrRootLocked()     // Catch:{ all -> 0x009c }
            java.lang.String r2 = "commit"
            r11.assertPreparedAndNotDestroyedLocked(r2)     // Catch:{ all -> 0x009c }
            com.android.server.pm.PackageInstallerService$PackageInstallObserverAdapter r2 = new com.android.server.pm.PackageInstallerService$PackageInstallObserverAdapter     // Catch:{ all -> 0x009c }
            android.content.Context r4 = r11.mContext     // Catch:{ all -> 0x009c }
            int r6 = r11.sessionId     // Catch:{ all -> 0x009c }
            boolean r7 = r11.isInstallerDeviceOwnerOrAffiliatedProfileOwnerLocked()     // Catch:{ all -> 0x009c }
            int r8 = r11.userId     // Catch:{ all -> 0x009c }
            r3 = r2
            r5 = r12
            r3.<init>(r4, r5, r6, r7, r8)     // Catch:{ all -> 0x009c }
            android.content.pm.IPackageInstallObserver2 r3 = r2.getBinder()     // Catch:{ all -> 0x009c }
            r11.mRemoteObserver = r3     // Catch:{ all -> 0x009c }
            r3 = 0
            if (r13 == 0) goto L_0x0042
            android.content.Context r4 = r11.mContext     // Catch:{ all -> 0x009c }
            java.lang.String r5 = "android.permission.INSTALL_PACKAGES"
            r4.enforceCallingOrSelfPermission(r5, r3)     // Catch:{ all -> 0x009c }
            int r4 = r11.mInstallerUid     // Catch:{ all -> 0x009c }
            int r5 = r11.mOriginalInstallerUid     // Catch:{ all -> 0x009c }
            if (r4 == r5) goto L_0x003a
            goto L_0x0048
        L_0x003a:
            java.lang.IllegalArgumentException r3 = new java.lang.IllegalArgumentException     // Catch:{ all -> 0x009c }
            java.lang.String r4 = "Session has not been transferred"
            r3.<init>(r4)     // Catch:{ all -> 0x009c }
            throw r3     // Catch:{ all -> 0x009c }
        L_0x0042:
            int r4 = r11.mInstallerUid     // Catch:{ all -> 0x009c }
            int r5 = r11.mOriginalInstallerUid     // Catch:{ all -> 0x009c }
            if (r4 != r5) goto L_0x0094
        L_0x0048:
            boolean r4 = r11.mCommitted     // Catch:{ all -> 0x009c }
            r5 = 1
            if (r4 == 0) goto L_0x004f
            monitor-exit(r1)     // Catch:{ all -> 0x009c }
            return r5
        L_0x004f:
            boolean r4 = r11.mSealed     // Catch:{ all -> 0x009c }
            boolean r6 = r11.mSealed     // Catch:{ all -> 0x009c }
            if (r6 != 0) goto L_0x007d
            com.android.server.pm.InstallationStatistic r6 = r11.mStatistic     // Catch:{ all -> 0x009c }
            long r7 = android.os.SystemClock.uptimeMillis()     // Catch:{ all -> 0x009c }
            com.android.server.pm.InstallationStatistic r9 = r11.mStatistic     // Catch:{ all -> 0x009c }
            long r9 = r9.timeBeginInstall     // Catch:{ all -> 0x009c }
            long r7 = r7 - r9
            r6.timeCopyApkConsumed = r7     // Catch:{ all -> 0x009c }
            r11.sealAndValidateLocked(r0)     // Catch:{ IOException -> 0x0076, PackageManagerException -> 0x0066 }
            goto L_0x007d
        L_0x0066:
            r5 = move-exception
            r11.destroyInternal()     // Catch:{ all -> 0x009c }
            int r6 = r5.error     // Catch:{ all -> 0x009c }
            java.lang.String r7 = android.util.ExceptionUtils.getCompleteMessage(r5)     // Catch:{ all -> 0x009c }
            r11.dispatchSessionFinished(r6, r7, r3)     // Catch:{ all -> 0x009c }
            r3 = 0
            monitor-exit(r1)     // Catch:{ all -> 0x009c }
            return r3
        L_0x0076:
            r3 = move-exception
            java.lang.IllegalArgumentException r5 = new java.lang.IllegalArgumentException     // Catch:{ all -> 0x009c }
            r5.<init>(r3)     // Catch:{ all -> 0x009c }
            throw r5     // Catch:{ all -> 0x009c }
        L_0x007d:
            r3 = 1065353216(0x3f800000, float:1.0)
            r11.mClientProgress = r3     // Catch:{ all -> 0x009c }
            r11.computeProgressLocked(r5)     // Catch:{ all -> 0x009c }
            java.util.concurrent.atomic.AtomicInteger r3 = r11.mActiveCount     // Catch:{ all -> 0x009c }
            r3.incrementAndGet()     // Catch:{ all -> 0x009c }
            r11.mCommitted = r5     // Catch:{ all -> 0x009c }
            monitor-exit(r1)     // Catch:{ all -> 0x009c }
            if (r4 != 0) goto L_0x0093
            com.android.server.pm.PackageInstallerService$InternalCallback r1 = r11.mCallback
            r1.onSessionSealedBlocking(r11)
        L_0x0093:
            return r5
        L_0x0094:
            java.lang.IllegalArgumentException r3 = new java.lang.IllegalArgumentException     // Catch:{ all -> 0x009c }
            java.lang.String r4 = "Session has been transferred"
            r3.<init>(r4)     // Catch:{ all -> 0x009c }
            throw r3     // Catch:{ all -> 0x009c }
        L_0x009c:
            r2 = move-exception
            monitor-exit(r1)     // Catch:{ all -> 0x009c }
            throw r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.pm.PackageInstallerSession.markAsCommitted(android.content.IntentSender, boolean):boolean");
    }

    private List<PackageInstallerSession> getChildSessions() {
        List<PackageInstallerSession> childSessions = null;
        if (isMultiPackage()) {
            int[] childSessionIds = getChildSessionIds();
            childSessions = new ArrayList<>(childSessionIds.length);
            for (int childSessionId : childSessionIds) {
                childSessions.add(this.mSessionProvider.getSession(childSessionId));
            }
        }
        return childSessions;
    }

    @GuardedBy({"mLock"})
    private void assertMultiPackageConsistencyLocked(List<PackageInstallerSession> childSessions) throws PackageManagerException {
        for (PackageInstallerSession childSession : childSessions) {
            if (childSession != null) {
                assertConsistencyWithLocked(childSession);
            }
        }
    }

    @GuardedBy({"mLock"})
    private void assertConsistencyWithLocked(PackageInstallerSession other) throws PackageManagerException {
        if (this.params.isStaged != other.params.isStaged) {
            throw new PackageManagerException(-120, "Multipackage Inconsistency: session " + other.sessionId + " and session " + this.sessionId + " have inconsistent staged settings");
        } else if (this.params.getEnableRollback() != other.params.getEnableRollback()) {
            throw new PackageManagerException(-120, "Multipackage Inconsistency: session " + other.sessionId + " and session " + this.sessionId + " have inconsistent rollback settings");
        }
    }

    @GuardedBy({"mLock"})
    private void sealAndValidateLocked(List<PackageInstallerSession> childSessions) throws PackageManagerException, IOException {
        int i;
        assertNoWriteFileTransfersOpenLocked();
        assertPreparedAndNotDestroyedLocked("sealing of session");
        boolean anotherSessionAlreadyInProgress = true;
        this.mSealed = true;
        if (childSessions != null) {
            assertMultiPackageConsistencyLocked(childSessions);
        }
        if (this.params.isStaged) {
            PackageInstallerSession activeSession = this.mStagingManager.getActiveSession();
            if (activeSession == null || this.sessionId == (i = activeSession.sessionId) || this.mParentSessionId == i) {
                anotherSessionAlreadyInProgress = false;
            }
            if (anotherSessionAlreadyInProgress) {
                throw new PackageManagerException(-119, "There is already in-progress committed staged session " + activeSession.sessionId, (Throwable) null);
            }
        }
        if (!this.params.isMultiPackage) {
            PackageInfo pkgInfo = this.mPm.getPackageInfo(this.params.appPackageName, 67108928, this.userId);
            resolveStageDirLocked();
            try {
                if ((this.params.installFlags & 131072) != 0) {
                    validateApexInstallLocked();
                } else {
                    validateApkInstallLocked(pkgInfo);
                }
            } catch (PackageManagerException e) {
                throw e;
            } catch (Throwable e2) {
                throw new PackageManagerException(e2);
            }
        }
    }

    /* Debug info: failed to restart local var, previous not found, register: 6 */
    /* access modifiers changed from: package-private */
    /* JADX WARNING: Code restructure failed: missing block: B:10:0x0015, code lost:
        monitor-enter(r2);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:12:?, code lost:
        sealAndValidateLocked(r1);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:14:0x001c, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:17:?, code lost:
        android.util.Slog.e(TAG, "Package not valid", r0);
        destroyInternal();
        dispatchSessionFinished(r0.error, android.util.ExceptionUtils.getCompleteMessage(r0), (android.os.Bundle) null);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:20:0x0033, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:22:0x0039, code lost:
        throw new java.lang.IllegalStateException(r0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:9:0x000f, code lost:
        r1 = getChildSessions();
        r2 = r6.mLock;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void sealAndValidateIfNecessary() {
        /*
            r6 = this;
            java.lang.Object r0 = r6.mLock
            monitor-enter(r0)
            boolean r1 = r6.mShouldBeSealed     // Catch:{ all -> 0x003e }
            if (r1 == 0) goto L_0x003c
            boolean r1 = r6.isStagedAndInTerminalState()     // Catch:{ all -> 0x003e }
            if (r1 == 0) goto L_0x000e
            goto L_0x003c
        L_0x000e:
            monitor-exit(r0)     // Catch:{ all -> 0x003e }
            java.util.List r1 = r6.getChildSessions()
            java.lang.Object r2 = r6.mLock
            monitor-enter(r2)
            r6.sealAndValidateLocked(r1)     // Catch:{ IOException -> 0x0033, PackageManagerException -> 0x001c }
            goto L_0x0031
        L_0x001a:
            r0 = move-exception
            goto L_0x003a
        L_0x001c:
            r0 = move-exception
            java.lang.String r3 = "PackageInstallerSession"
            java.lang.String r4 = "Package not valid"
            android.util.Slog.e(r3, r4, r0)     // Catch:{ all -> 0x001a }
            r6.destroyInternal()     // Catch:{ all -> 0x001a }
            int r3 = r0.error     // Catch:{ all -> 0x001a }
            java.lang.String r4 = android.util.ExceptionUtils.getCompleteMessage(r0)     // Catch:{ all -> 0x001a }
            r5 = 0
            r6.dispatchSessionFinished(r3, r4, r5)     // Catch:{ all -> 0x001a }
        L_0x0031:
            monitor-exit(r2)     // Catch:{ all -> 0x001a }
            return
        L_0x0033:
            r0 = move-exception
            java.lang.IllegalStateException r3 = new java.lang.IllegalStateException     // Catch:{ all -> 0x001a }
            r3.<init>(r0)     // Catch:{ all -> 0x001a }
            throw r3     // Catch:{ all -> 0x001a }
        L_0x003a:
            monitor-exit(r2)     // Catch:{ all -> 0x001a }
            throw r0
        L_0x003c:
            monitor-exit(r0)     // Catch:{ all -> 0x003e }
            return
        L_0x003e:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x003e }
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.pm.PackageInstallerSession.sealAndValidateIfNecessary():void");
    }

    public void markUpdated() {
        synchronized (this.mLock) {
            this.updatedMillis = System.currentTimeMillis();
        }
    }

    /* Debug info: failed to restart local var, previous not found, register: 7 */
    public void transfer(String packageName) {
        Preconditions.checkNotNull(packageName);
        ApplicationInfo newOwnerAppInfo = this.mPm.getApplicationInfo(packageName, 0, this.userId);
        if (newOwnerAppInfo == null) {
            throw new ParcelableException(new PackageManager.NameNotFoundException(packageName));
        } else if (this.mPm.checkUidPermission("android.permission.INSTALL_PACKAGES", newOwnerAppInfo.uid) != 0) {
            throw new SecurityException("Destination package " + packageName + " does not have the " + "android.permission.INSTALL_PACKAGES" + " permission");
        } else if (this.params.areHiddenOptionsSet()) {
            List<PackageInstallerSession> childSessions = getChildSessions();
            synchronized (this.mLock) {
                assertCallerIsOwnerOrRootLocked();
                assertPreparedAndNotSealedLocked("transfer");
                try {
                    sealAndValidateLocked(childSessions);
                    if (this.mPackageName.equals(this.mInstallerPackageName)) {
                        this.mInstallerPackageName = packageName;
                        this.mInstallerUid = newOwnerAppInfo.uid;
                    } else {
                        throw new SecurityException("Can only transfer sessions that update the original installer");
                    }
                } catch (IOException e) {
                    throw new IllegalStateException(e);
                } catch (PackageManagerException e2) {
                    destroyInternal();
                    dispatchSessionFinished(e2.error, ExceptionUtils.getCompleteMessage(e2), (Bundle) null);
                    throw new IllegalArgumentException("Package is not valid", e2);
                }
            }
            this.mCallback.onSessionSealedBlocking(this);
        } else {
            throw new SecurityException("Can only transfer sessions that use public options");
        }
    }

    /* Debug info: failed to restart local var, previous not found, register: 6 */
    /* access modifiers changed from: private */
    public void handleCommit() {
        if (isInstallerDeviceOwnerOrAffiliatedProfileOwnerLocked()) {
            DevicePolicyEventLogger.createEvent(HdmiCecKeycode.UI_BROADCAST_DIGITAL_CABLE).setAdmin(this.mInstallerPackageName).write();
        }
        if (this.params.isStaged) {
            this.mStagingManager.commitSession(this);
            destroyInternal();
            dispatchSessionFinished(1, "Session staged", (Bundle) null);
        } else if ((this.params.installFlags & 131072) != 0) {
            destroyInternal();
            dispatchSessionFinished(RequestStatus.SYS_ETIMEDOUT, "APEX packages can only be installed using staged sessions.", (Bundle) null);
        } else {
            List<PackageInstallerSession> childSessions = getChildSessions();
            try {
                synchronized (this.mLock) {
                    commitNonStagedLocked(childSessions);
                }
            } catch (PackageManagerException e) {
                String completeMsg = ExceptionUtils.getCompleteMessage(e);
                Slog.e(TAG, "Commit of session " + this.sessionId + " failed: " + completeMsg);
                destroyInternal();
                dispatchSessionFinished(e.error, completeMsg, (Bundle) null);
            }
        }
    }

    @GuardedBy({"mLock"})
    private void commitNonStagedLocked(List<PackageInstallerSession> childSessions) throws PackageManagerException {
        PackageManagerService.ActiveInstallSession committingSession = makeSessionActiveLocked();
        if (committingSession != null) {
            if (isMultiPackage()) {
                List<PackageManagerService.ActiveInstallSession> activeChildSessions = new ArrayList<>(childSessions.size());
                boolean success = true;
                PackageManagerException failure = null;
                for (int i = 0; i < childSessions.size(); i++) {
                    try {
                        PackageManagerService.ActiveInstallSession activeSession = childSessions.get(i).makeSessionActiveLocked();
                        if (activeSession != null) {
                            activeChildSessions.add(activeSession);
                        }
                    } catch (PackageManagerException e) {
                        failure = e;
                        success = false;
                    }
                }
                if (!success) {
                    try {
                        this.mRemoteObserver.onPackageInstalled((String) null, failure.error, failure.getLocalizedMessage(), (Bundle) null);
                    } catch (RemoteException e2) {
                    }
                } else {
                    this.mPm.installStage(activeChildSessions);
                }
            } else {
                this.mPm.installStage(committingSession);
            }
        }
    }

    /* Debug info: failed to restart local var, previous not found, register: 13 */
    @GuardedBy({"mLock"})
    private PackageManagerService.ActiveInstallSession makeSessionActiveLocked() throws PackageManagerException {
        IPackageInstallObserver2 localObserver;
        UserHandle user;
        if (this.mRelinquished) {
            throw new PackageManagerException(RequestStatus.SYS_ETIMEDOUT, "Session relinquished");
        } else if (this.mDestroyed) {
            throw new PackageManagerException(RequestStatus.SYS_ETIMEDOUT, "Session destroyed");
        } else if (this.mSealed) {
            if ((this.params.installFlags & 131072) != 0) {
                localObserver = null;
            } else {
                if (!this.params.isMultiPackage) {
                    Preconditions.checkNotNull(this.mPackageName);
                    Preconditions.checkNotNull(this.mSigningDetails);
                    Preconditions.checkNotNull(this.mResolvedBaseFile);
                    if (needToAskForPermissionsLocked()) {
                        Intent intent = new Intent("android.content.pm.action.CONFIRM_INSTALL");
                        intent.setPackage(this.mPm.getPackageInstallerPackageName());
                        intent.putExtra("android.content.pm.extra.SESSION_ID", this.sessionId);
                        try {
                            this.mRemoteObserver.onUserActionRequired(intent);
                        } catch (RemoteException e) {
                        }
                        closeInternal(false);
                        return null;
                    }
                    if (this.params.mode == 2) {
                        try {
                            List<File> fromFiles = this.mResolvedInheritedFiles;
                            File toDir = resolveStageDirLocked();
                            Slog.d(TAG, "Inherited files: " + this.mResolvedInheritedFiles);
                            if (!this.mResolvedInheritedFiles.isEmpty()) {
                                if (this.mInheritedFilesBase == null) {
                                    throw new IllegalStateException("mInheritedFilesBase == null");
                                }
                            }
                            if (isLinkPossible(fromFiles, toDir)) {
                                if (!this.mResolvedInstructionSets.isEmpty()) {
                                    createOatDirs(this.mResolvedInstructionSets, new File(toDir, "oat"));
                                }
                                if (!this.mResolvedNativeLibPaths.isEmpty()) {
                                    for (String libPath : this.mResolvedNativeLibPaths) {
                                        int splitIndex = libPath.lastIndexOf(47);
                                        if (splitIndex >= 0) {
                                            if (splitIndex < libPath.length() - 1) {
                                                File libDir = new File(toDir, libPath.substring(1, splitIndex));
                                                if (!libDir.exists()) {
                                                    NativeLibraryHelper.createNativeLibrarySubdir(libDir);
                                                }
                                                NativeLibraryHelper.createNativeLibrarySubdir(new File(libDir, libPath.substring(splitIndex + 1)));
                                            }
                                        }
                                        Slog.e(TAG, "Skipping native library creation for linking due to invalid path: " + libPath);
                                    }
                                }
                                linkFiles(fromFiles, toDir, this.mInheritedFilesBase);
                            } else {
                                copyFiles(fromFiles, toDir);
                            }
                        } catch (IOException e2) {
                            throw new PackageManagerException(-4, "Failed to inherit existing install", e2);
                        }
                    }
                    this.mInternalProgress = 0.5f;
                    computeProgressLocked(true);
                    extractNativeLibraries(this.mResolvedStageDir, this.params.abiOverride, mayInheritNativeLibs());
                }
                localObserver = new IPackageInstallObserver2.Stub() {
                    public void onUserActionRequired(Intent intent) {
                        throw new IllegalStateException();
                    }

                    public void onPackageInstalled(String basePackageName, int returnCode, String msg, Bundle extras) {
                        PackageInstallerSession.this.destroyInternal();
                        PackageInstallerSession.this.dispatchSessionFinished(returnCode, msg, extras);
                    }
                };
            }
            if ((this.params.installFlags & 64) != 0) {
                user = UserHandle.ALL;
            } else {
                user = new UserHandle(this.userId);
            }
            this.mRelinquished = true;
            return new PackageManagerService.ActiveInstallSession(this.mPackageName, this.stageDir, localObserver, this.params, this.mInstallerPackageName, this.mInstallerUid, user, this.mSigningDetails, this.mStatistic);
        } else {
            throw new PackageManagerException(RequestStatus.SYS_ETIMEDOUT, "Session not sealed");
        }
    }

    private static void maybeRenameFile(File from, File to) throws PackageManagerException {
        if (!from.equals(to) && !from.renameTo(to)) {
            throw new PackageManagerException(RequestStatus.SYS_ETIMEDOUT, "Could not rename file " + from + " to " + to);
        }
    }

    private boolean mayInheritNativeLibs() {
        return SystemProperties.getBoolean(PROPERTY_NAME_INHERIT_NATIVE, true) && this.params.mode == 2 && (this.params.installFlags & 1) != 0;
    }

    @GuardedBy({"mLock"})
    private void validateApexInstallLocked() throws PackageManagerException {
        File[] addedFiles = this.mResolvedStageDir.listFiles(sAddedFilter);
        if (ArrayUtils.isEmpty(addedFiles)) {
            throw new PackageManagerException(-2, "No packages staged");
        } else if (ArrayUtils.size(addedFiles) <= 1) {
            this.mResolvedBaseFile = addedFiles[0];
        } else {
            throw new PackageManagerException(-2, "Too many files for apex install");
        }
    }

    /* access modifiers changed from: private */
    @GuardedBy({"mLock"})
    public void validateApkInstallLocked(PackageInfo pkgInfo) throws PackageManagerException {
        List<String> removeSplitList;
        File[] removedFiles;
        File[] libDirs;
        List<File> libDirsToInherit;
        File[] removedFiles2;
        File[] fileArr;
        List<File> libDirsToInherit2;
        ApplicationInfo appInfo;
        String targetName;
        PackageInfo packageInfo = pkgInfo;
        this.mPackageName = null;
        this.mVersionCode = -1;
        this.mSigningDetails = PackageParser.SigningDetails.UNKNOWN;
        this.mResolvedBaseFile = null;
        this.mResolvedStagedFiles.clear();
        this.mResolvedInheritedFiles.clear();
        if (this.params.mode == 2 && (packageInfo == null || packageInfo.applicationInfo == null)) {
            throw new PackageManagerException(-2, "Missing existing base package");
        }
        this.mVerityFound = PackageManagerServiceUtils.isApkVerityEnabled() && this.params.mode == 2 && VerityUtils.hasFsverity(packageInfo.applicationInfo.getBaseCodePath());
        try {
            resolveStageDirLocked();
            File[] removedFiles3 = this.mResolvedStageDir.listFiles(sRemovedFilter);
            List<String> arrayList = new ArrayList<>();
            if (!ArrayUtils.isEmpty(removedFiles3)) {
                for (File removedFile : removedFiles3) {
                    String fileName = removedFile.getName();
                    arrayList.add(fileName.substring(0, fileName.length() - REMOVE_SPLIT_MARKER_EXTENSION.length()));
                }
            }
            File[] addedFiles = this.mResolvedStageDir.listFiles(sAddedFilter);
            if (!ArrayUtils.isEmpty(addedFiles) || arrayList.size() != 0) {
                ArraySet<String> stagedSplits = new ArraySet<>();
                int length = addedFiles.length;
                PackageParser.ApkLite baseApk = null;
                int i = 0;
                while (i < length) {
                    File addedFile = addedFiles[i];
                    try {
                        long begin = SystemClock.uptimeMillis();
                        PackageParser.ApkLite apk = PackageParser.parseApkLite(addedFile, 32);
                        ArrayList arrayList2 = arrayList;
                        try {
                            this.mStatistic.timeCollectingCerts += SystemClock.uptimeMillis() - begin;
                            if (stagedSplits.add(apk.splitName)) {
                                if (this.mPackageName == null) {
                                    this.mPackageName = apk.packageName;
                                    this.mVersionCode = apk.getLongVersionCode();
                                }
                                if (this.mSigningDetails == PackageParser.SigningDetails.UNKNOWN) {
                                    this.mSigningDetails = apk.signingDetails;
                                }
                                assertApkConsistentLocked(String.valueOf(addedFile), apk);
                                if (apk.splitName == null) {
                                    targetName = "base.apk";
                                } else {
                                    targetName = "split_" + apk.splitName + ".apk";
                                }
                                if (FileUtils.isValidExtFilename(targetName)) {
                                    File targetFile = new File(this.mResolvedStageDir, targetName);
                                    resolveAndStageFile(addedFile, targetFile);
                                    if (apk.splitName == null) {
                                        this.mResolvedBaseFile = targetFile;
                                        baseApk = apk;
                                    }
                                    File dexMetadataFile = DexMetadataHelper.findDexMetadataForFile(addedFile);
                                    if (dexMetadataFile != null) {
                                        if (FileUtils.isValidExtFilename(dexMetadataFile.getName())) {
                                            resolveAndStageFile(dexMetadataFile, new File(this.mResolvedStageDir, DexMetadataHelper.buildDexMetadataPathForApk(targetName)));
                                        } else {
                                            throw new PackageManagerException(-2, "Invalid filename: " + dexMetadataFile);
                                        }
                                    }
                                    i++;
                                    arrayList = arrayList2;
                                } else {
                                    throw new PackageManagerException(-2, "Invalid filename: " + targetName);
                                }
                            } else {
                                throw new PackageManagerException(-2, "Split " + apk.splitName + " was defined multiple times");
                            }
                        } catch (PackageParser.PackageParserException e) {
                            e = e;
                            throw PackageManagerException.from(e);
                        }
                    } catch (PackageParser.PackageParserException e2) {
                        e = e2;
                        ArrayList arrayList3 = arrayList;
                        throw PackageManagerException.from(e);
                    }
                }
                List<String> removeSplitList2 = arrayList;
                if (removeSplitList2.size() > 0) {
                    if (packageInfo != null) {
                        for (String splitName : removeSplitList2) {
                            if (!ArrayUtils.contains(packageInfo.splitNames, splitName)) {
                                throw new PackageManagerException(-2, "Split not found: " + splitName);
                            }
                        }
                        if (this.mPackageName == null) {
                            this.mPackageName = packageInfo.packageName;
                            this.mVersionCode = pkgInfo.getLongVersionCode();
                        }
                        if (this.mSigningDetails == PackageParser.SigningDetails.UNKNOWN) {
                            try {
                                this.mSigningDetails = ApkSignatureVerifier.unsafeGetCertsWithoutVerification(packageInfo.applicationInfo.sourceDir, 1);
                            } catch (PackageParser.PackageParserException e3) {
                                throw new PackageManagerException(-2, "Couldn't obtain signatures from base APK");
                            }
                        }
                    } else {
                        throw new PackageManagerException(-2, "Missing existing base package for " + this.mPackageName);
                    }
                }
                if (this.params.mode != 1) {
                    ApplicationInfo appInfo2 = packageInfo.applicationInfo;
                    try {
                        PackageParser.PackageLite existing = PackageParser.parsePackageLite(new File(appInfo2.getCodePath()), 0);
                        long begin2 = SystemClock.uptimeMillis();
                        PackageParser.ApkLite existingBase = PackageParser.parseApkLite(new File(appInfo2.getBaseCodePath()), 32);
                        File[] fileArr2 = addedFiles;
                        try {
                            this.mStatistic.timeCollectingCerts += SystemClock.uptimeMillis() - begin2;
                            assertApkConsistentLocked("Existing base", existingBase);
                            if (this.mResolvedBaseFile == null) {
                                this.mResolvedBaseFile = new File(appInfo2.getBaseCodePath());
                                resolveInheritedFile(this.mResolvedBaseFile);
                                File baseDexMetadataFile = DexMetadataHelper.findDexMetadataForFile(this.mResolvedBaseFile);
                                if (baseDexMetadataFile != null) {
                                    resolveInheritedFile(baseDexMetadataFile);
                                }
                                baseApk = existingBase;
                            }
                            if (!ArrayUtils.isEmpty(existing.splitNames)) {
                                int i2 = 0;
                                while (i2 < existing.splitNames.length) {
                                    String splitName2 = existing.splitNames[i2];
                                    File splitFile = new File(existing.splitCodePaths[i2]);
                                    List<String> removeSplitList3 = removeSplitList2;
                                    boolean splitRemoved = removeSplitList3.contains(splitName2);
                                    if (!stagedSplits.contains(splitName2) && !splitRemoved) {
                                        resolveInheritedFile(splitFile);
                                        File splitDexMetadataFile = DexMetadataHelper.findDexMetadataForFile(splitFile);
                                        if (splitDexMetadataFile != null) {
                                            resolveInheritedFile(splitDexMetadataFile);
                                        }
                                    }
                                    i2++;
                                    removeSplitList2 = removeSplitList3;
                                }
                                removeSplitList = removeSplitList2;
                            } else {
                                removeSplitList = removeSplitList2;
                            }
                            File packageInstallDir = new File(appInfo2.getBaseCodePath()).getParentFile();
                            this.mInheritedFilesBase = packageInstallDir;
                            File oatDir = new File(packageInstallDir, "oat");
                            if (oatDir.exists()) {
                                File[] archSubdirs = oatDir.listFiles();
                                if (archSubdirs == null || archSubdirs.length <= 0) {
                                    ApplicationInfo applicationInfo = appInfo2;
                                } else {
                                    String[] instructionSets = InstructionSets.getAllDexCodeInstructionSets();
                                    int length2 = archSubdirs.length;
                                    int i3 = 0;
                                    while (i3 < length2) {
                                        File archSubDir = archSubdirs[i3];
                                        File[] archSubdirs2 = archSubdirs;
                                        if (!ArrayUtils.contains(instructionSets, archSubDir.getName())) {
                                            appInfo = appInfo2;
                                        } else {
                                            appInfo = appInfo2;
                                            this.mResolvedInstructionSets.add(archSubDir.getName());
                                            List<File> oatFiles = Arrays.asList(archSubDir.listFiles());
                                            if (!oatFiles.isEmpty()) {
                                                this.mResolvedInheritedFiles.addAll(oatFiles);
                                            }
                                        }
                                        i3++;
                                        archSubdirs = archSubdirs2;
                                        appInfo2 = appInfo;
                                    }
                                    ApplicationInfo applicationInfo2 = appInfo2;
                                }
                            }
                            if (!mayInheritNativeLibs() || !removeSplitList.isEmpty()) {
                            } else {
                                File[] libDirs2 = {new File(packageInstallDir, "lib"), new File(packageInstallDir, "lib64")};
                                int length3 = libDirs2.length;
                                int i4 = 0;
                                while (i4 < length3) {
                                    File libDir = libDirs2[i4];
                                    if (!libDir.exists()) {
                                        libDirs = libDirs2;
                                        removedFiles = removedFiles3;
                                    } else if (!libDir.isDirectory()) {
                                        libDirs = libDirs2;
                                        removedFiles = removedFiles3;
                                    } else {
                                        List<File> libDirsToInherit3 = new LinkedList<>();
                                        File[] listFiles = libDir.listFiles();
                                        int length4 = listFiles.length;
                                        libDirs = libDirs2;
                                        int i5 = 0;
                                        while (i5 < length4) {
                                            int i6 = length4;
                                            File archSubDir2 = listFiles[i5];
                                            if (!archSubDir2.isDirectory()) {
                                                fileArr = listFiles;
                                                removedFiles2 = removedFiles3;
                                                libDirsToInherit2 = libDirsToInherit3;
                                            } else {
                                                try {
                                                    fileArr = listFiles;
                                                    removedFiles2 = removedFiles3;
                                                    String relLibPath = getRelativePath(archSubDir2, packageInstallDir);
                                                    if (!this.mResolvedNativeLibPaths.contains(relLibPath)) {
                                                        this.mResolvedNativeLibPaths.add(relLibPath);
                                                    }
                                                    File file = archSubDir2;
                                                    libDirsToInherit2 = libDirsToInherit3;
                                                    libDirsToInherit2.addAll(Arrays.asList(archSubDir2.listFiles()));
                                                } catch (IOException e4) {
                                                    File file2 = archSubDir2;
                                                    removedFiles = removedFiles3;
                                                    libDirsToInherit = libDirsToInherit3;
                                                    Slog.e(TAG, "Skipping linking of native library directory!", e4);
                                                    libDirsToInherit.clear();
                                                }
                                            }
                                            i5++;
                                            libDirsToInherit3 = libDirsToInherit2;
                                            length4 = i6;
                                            listFiles = fileArr;
                                            removedFiles3 = removedFiles2;
                                        }
                                        removedFiles = removedFiles3;
                                        libDirsToInherit = libDirsToInherit3;
                                        this.mResolvedInheritedFiles.addAll(libDirsToInherit);
                                    }
                                    i4++;
                                    libDirs2 = libDirs;
                                    removedFiles3 = removedFiles;
                                }
                                File[] fileArr3 = removedFiles3;
                            }
                        } catch (PackageParser.PackageParserException e5) {
                            e = e5;
                            ApplicationInfo applicationInfo3 = appInfo2;
                            File[] fileArr4 = removedFiles3;
                            List<String> list = removeSplitList2;
                            throw PackageManagerException.from(e);
                        }
                    } catch (PackageParser.PackageParserException e6) {
                        e = e6;
                        ApplicationInfo applicationInfo4 = appInfo2;
                        File[] fileArr5 = removedFiles3;
                        File[] fileArr6 = addedFiles;
                        List<String> list2 = removeSplitList2;
                        throw PackageManagerException.from(e);
                    }
                } else if (stagedSplits.contains((Object) null)) {
                    File[] fileArr7 = removedFiles3;
                    File[] fileArr8 = addedFiles;
                    ArrayList arrayList4 = removeSplitList2;
                } else {
                    throw new PackageManagerException(-2, "Full install must include a base package");
                }
                if (baseApk.useEmbeddedDex) {
                    for (File file3 : this.mResolvedStagedFiles) {
                        if (file3.getName().endsWith(".apk")) {
                            if (!DexManager.auditUncompressedDexInApk(file3.getPath())) {
                                throw new PackageManagerException(-2, "Some dex are not uncompressed and aligned correctly for " + this.mPackageName);
                            }
                        }
                    }
                }
                if (baseApk.isSplitRequired && stagedSplits.size() <= 1) {
                    throw new PackageManagerException(-28, "Missing split for " + this.mPackageName);
                }
                return;
            }
            throw new PackageManagerException(-2, "No packages staged");
        } catch (IOException e7) {
            throw new PackageManagerException(-18, "Failed to resolve stage location", e7);
        }
    }

    private void resolveAndStageFile(File origFile, File targetFile) throws PackageManagerException {
        this.mResolvedStagedFiles.add(targetFile);
        maybeRenameFile(origFile, targetFile);
        File originalSignature = new File(VerityUtils.getFsveritySignatureFilePath(origFile.getPath()));
        if (originalSignature.exists()) {
            if (!this.mVerityFound) {
                this.mVerityFound = true;
                if (this.mResolvedStagedFiles.size() > 1) {
                    throw new PackageManagerException(-118, "Some file is missing fs-verity signature");
                }
            }
            File stagedSignature = new File(VerityUtils.getFsveritySignatureFilePath(targetFile.getPath()));
            maybeRenameFile(originalSignature, stagedSignature);
            this.mResolvedStagedFiles.add(stagedSignature);
        } else if (this.mVerityFound) {
            throw new PackageManagerException(-118, "Missing corresponding fs-verity signature to " + origFile);
        }
    }

    private void resolveInheritedFile(File origFile) {
        this.mResolvedInheritedFiles.add(origFile);
        File fsveritySignatureFile = new File(VerityUtils.getFsveritySignatureFilePath(origFile.getPath()));
        if (fsveritySignatureFile.exists()) {
            this.mResolvedInheritedFiles.add(fsveritySignatureFile);
        }
    }

    @GuardedBy({"mLock"})
    private void assertApkConsistentLocked(String tag, PackageParser.ApkLite apk) throws PackageManagerException {
        if (!this.mPackageName.equals(apk.packageName)) {
            throw new PackageManagerException(-2, tag + " package " + apk.packageName + " inconsistent with " + this.mPackageName);
        } else if (this.params.appPackageName != null && !this.params.appPackageName.equals(apk.packageName)) {
            throw new PackageManagerException(-2, tag + " specified package " + this.params.appPackageName + " inconsistent with " + apk.packageName);
        } else if (this.mVersionCode != apk.getLongVersionCode()) {
            throw new PackageManagerException(-2, tag + " version code " + apk.versionCode + " inconsistent with " + this.mVersionCode);
        } else if (!this.mSigningDetails.signaturesMatchExactly(apk.signingDetails)) {
            throw new PackageManagerException(-2, tag + " signatures are inconsistent");
        }
    }

    private boolean isLinkPossible(List<File> fromFiles, File toDir) {
        try {
            StructStat toStat = Os.stat(toDir.getAbsolutePath());
            for (File fromFile : fromFiles) {
                if (Os.stat(fromFile.getAbsolutePath()).st_dev != toStat.st_dev) {
                    return false;
                }
            }
            return true;
        } catch (ErrnoException e) {
            Slog.w(TAG, "Failed to detect if linking possible: " + e);
            return false;
        }
    }

    public int getInstallerUid() {
        int i;
        synchronized (this.mLock) {
            i = this.mInstallerUid;
        }
        return i;
    }

    public long getUpdatedMillis() {
        long j;
        synchronized (this.mLock) {
            j = this.updatedMillis;
        }
        return j;
    }

    /* access modifiers changed from: package-private */
    public String getInstallerPackageName() {
        String str;
        synchronized (this.mLock) {
            str = this.mInstallerPackageName;
        }
        return str;
    }

    private static String getRelativePath(File file, File base) throws IOException {
        String pathStr = file.getAbsolutePath();
        String baseStr = base.getAbsolutePath();
        if (pathStr.contains("/.")) {
            throw new IOException("Invalid path (was relative) : " + pathStr);
        } else if (pathStr.startsWith(baseStr)) {
            return pathStr.substring(baseStr.length());
        } else {
            throw new IOException("File: " + pathStr + " outside base: " + baseStr);
        }
    }

    private void createOatDirs(List<String> instructionSets, File fromDir) throws PackageManagerException {
        for (String instructionSet : instructionSets) {
            try {
                this.mPm.mInstaller.createOatDir(fromDir.getAbsolutePath(), instructionSet);
            } catch (Installer.InstallerException e) {
                throw PackageManagerException.from(e);
            }
        }
    }

    private void linkFiles(List<File> fromFiles, File toDir, File fromDir) throws IOException {
        for (File fromFile : fromFiles) {
            String relativePath = getRelativePath(fromFile, fromDir);
            try {
                this.mPm.mInstaller.linkFile(relativePath, fromDir.getAbsolutePath(), toDir.getAbsolutePath());
            } catch (Installer.InstallerException e) {
                throw new IOException("failed linkOrCreateDir(" + relativePath + ", " + fromDir + ", " + toDir + ")", e);
            }
        }
        Slog.d(TAG, "Linked " + fromFiles.size() + " files into " + toDir);
    }

    private static void copyFiles(List<File> fromFiles, File toDir) throws IOException {
        for (File file : toDir.listFiles()) {
            if (file.getName().endsWith(".tmp")) {
                file.delete();
            }
        }
        for (File fromFile : fromFiles) {
            File tmpFile = File.createTempFile("inherit", ".tmp", toDir);
            Slog.d(TAG, "Copying " + fromFile + " to " + tmpFile);
            if (FileUtils.copyFile(fromFile, tmpFile)) {
                try {
                    Os.chmod(tmpFile.getAbsolutePath(), 420);
                    File toFile = new File(toDir, fromFile.getName());
                    Slog.d(TAG, "Renaming " + tmpFile + " to " + toFile);
                    if (!tmpFile.renameTo(toFile)) {
                        throw new IOException("Failed to rename " + tmpFile + " to " + toFile);
                    }
                } catch (ErrnoException e) {
                    throw new IOException("Failed to chmod " + tmpFile);
                }
            } else {
                throw new IOException("Failed to copy " + fromFile + " to " + tmpFile);
            }
        }
        Slog.d(TAG, "Copied " + fromFiles.size() + " files into " + toDir);
    }

    private static void extractNativeLibraries(File packageDir, String abiOverride, boolean inherit) throws PackageManagerException {
        File libDir = new File(packageDir, "lib");
        if (!inherit) {
            NativeLibraryHelper.removeNativeBinariesFromDirLI(libDir, true);
        }
        NativeLibraryHelper.Handle handle = null;
        try {
            handle = NativeLibraryHelper.Handle.create(packageDir);
            int res = NativeLibraryHelper.copyNativeBinariesWithOverride(handle, libDir, abiOverride);
            if (res == 1) {
                IoUtils.closeQuietly(handle);
                return;
            }
            throw new PackageManagerException(res, "Failed to extract native libraries, res=" + res);
        } catch (IOException e) {
            throw new PackageManagerException(RequestStatus.SYS_ETIMEDOUT, "Failed to extract native libraries", e);
        } catch (Throwable th) {
            IoUtils.closeQuietly(handle);
            throw th;
        }
    }

    /* access modifiers changed from: package-private */
    public void setPermissionsResult(boolean accepted) {
        if (!this.mSealed) {
            throw new SecurityException("Must be sealed to accept permissions");
        } else if (accepted) {
            synchronized (this.mLock) {
                this.mPermissionsManuallyAccepted = true;
                this.mHandler.obtainMessage(1).sendToTarget();
            }
        } else {
            destroyInternal();
            dispatchSessionFinished(-115, "User rejected permissions", (Bundle) null);
        }
    }

    /* access modifiers changed from: package-private */
    public void addChildSessionIdInternal(int sessionId2) {
        this.mChildSessionIds.put(sessionId2, 0);
    }

    /* Debug info: failed to restart local var, previous not found, register: 4 */
    public void open() throws IOException {
        boolean wasPrepared;
        if (this.mActiveCount.getAndIncrement() == 0) {
            this.mCallback.onSessionActiveChanged(this, true);
        }
        synchronized (this.mLock) {
            wasPrepared = this.mPrepared;
            if (!this.mPrepared) {
                if (this.stageDir != null) {
                    PackageInstallerService.prepareStageDir(this.stageDir);
                } else if (!this.params.isMultiPackage) {
                    throw new IllegalArgumentException("stageDir must be set");
                }
                this.mPrepared = true;
            }
        }
        if (!wasPrepared) {
            this.mCallback.onSessionPrepared(this);
        }
    }

    public void close() {
        closeInternal(true);
    }

    private void closeInternal(boolean checkCaller) {
        int activeCount;
        synchronized (this.mLock) {
            if (checkCaller) {
                assertCallerIsOwnerOrRootLocked();
            }
            activeCount = this.mActiveCount.decrementAndGet();
        }
        if (activeCount == 0) {
            this.mCallback.onSessionActiveChanged(this, false);
        }
    }

    /* Debug info: failed to restart local var, previous not found, register: 3 */
    public void abandon() {
        if (!hasParentSessionId()) {
            synchronized (this.mLock) {
                assertCallerIsOwnerOrRootLocked();
                if (!isStagedAndInTerminalState()) {
                    if (this.mCommitted && this.params.isStaged) {
                        synchronized (this.mLock) {
                            this.mDestroyed = true;
                        }
                        this.mStagingManager.abortCommittedSession(this);
                        cleanStageDir();
                    }
                    if (this.mRelinquished) {
                        Slog.d(TAG, "Ignoring abandon after commit relinquished control");
                        return;
                    }
                    destroyInternal();
                    dispatchSessionFinished(-115, "Session was abandoned", (Bundle) null);
                    return;
                }
                return;
            }
        }
        throw new IllegalStateException("Session " + this.sessionId + " is a child of multi-package session " + this.mParentSessionId + " and may not be abandoned directly.");
    }

    public boolean isMultiPackage() {
        return this.params.isMultiPackage;
    }

    public boolean isStaged() {
        return this.params.isStaged;
    }

    public int[] getChildSessionIds() {
        int[] childSessionIds = this.mChildSessionIds.copyKeys();
        if (childSessionIds != null) {
            return childSessionIds;
        }
        return EMPTY_CHILD_SESSION_ARRAY;
    }

    public void addChildSessionId(int childSessionId) {
        PackageInstallerSession childSession = this.mSessionProvider.getSession(childSessionId);
        if (childSession == null || ((childSession.hasParentSessionId() && childSession.mParentSessionId != this.sessionId) || childSession.mCommitted || childSession.mDestroyed)) {
            throw new IllegalStateException("Unable to add child session " + childSessionId + " as it does not exist or is in an invalid state.");
        }
        synchronized (this.mLock) {
            assertCallerIsOwnerOrRootLocked();
            assertPreparedAndNotSealedLocked("addChildSessionId");
            if (this.mChildSessionIds.indexOfKey(childSessionId) < 0) {
                childSession.setParentSessionId(this.sessionId);
                addChildSessionIdInternal(childSessionId);
            }
        }
    }

    public void removeChildSessionId(int sessionId2) {
        PackageInstallerSession session = this.mSessionProvider.getSession(sessionId2);
        synchronized (this.mLock) {
            int indexOfSession = this.mChildSessionIds.indexOfKey(sessionId2);
            if (session != null) {
                session.setParentSessionId(-1);
            }
            if (indexOfSession >= 0) {
                this.mChildSessionIds.removeAt(indexOfSession);
            }
        }
    }

    /* Debug info: failed to restart local var, previous not found, register: 4 */
    /* access modifiers changed from: package-private */
    public void setParentSessionId(int parentSessionId) {
        synchronized (this.mLock) {
            if (parentSessionId != -1) {
                if (this.mParentSessionId != -1) {
                    throw new IllegalStateException("The parent of " + this.sessionId + " is alreadyset to " + this.mParentSessionId);
                }
            }
            this.mParentSessionId = parentSessionId;
        }
    }

    /* access modifiers changed from: package-private */
    public boolean hasParentSessionId() {
        return this.mParentSessionId != -1;
    }

    public int getParentSessionId() {
        return this.mParentSessionId;
    }

    /* access modifiers changed from: private */
    public void dispatchSessionFinished(int returnCode, String msg, Bundle extras) {
        IPackageInstallObserver2 observer;
        String packageName;
        synchronized (this.mLock) {
            this.mFinalStatus = returnCode;
            this.mFinalMessage = msg;
            observer = this.mRemoteObserver;
            packageName = this.mPackageName;
        }
        if (observer != null) {
            SomeArgs args = SomeArgs.obtain();
            args.arg1 = packageName;
            args.arg2 = msg;
            args.arg3 = extras;
            args.arg4 = observer;
            args.argi1 = returnCode;
            this.mHandler.obtainMessage(2, args).sendToTarget();
        }
        boolean isNewInstall = false;
        boolean success = returnCode == 1;
        if (extras == null || !extras.getBoolean("android.intent.extra.REPLACING")) {
            isNewInstall = true;
        }
        if (success && isNewInstall && this.mPm.mInstallerService.okToSendBroadcasts()) {
            this.mPm.sendSessionCommitBroadcast(generateInfo(), this.userId);
        }
        this.mCallback.onSessionFinished(this, success);
    }

    /* access modifiers changed from: package-private */
    public void setStagedSessionReady() {
        synchronized (this.mLock) {
            this.mStagedSessionReady = true;
            this.mStagedSessionApplied = false;
            this.mStagedSessionFailed = false;
            this.mStagedSessionErrorCode = 0;
            this.mStagedSessionErrorMessage = "";
        }
        this.mCallback.onStagedSessionChanged(this);
    }

    /* access modifiers changed from: package-private */
    public void setStagedSessionFailed(int errorCode, String errorMessage) {
        synchronized (this.mLock) {
            this.mStagedSessionReady = false;
            this.mStagedSessionApplied = false;
            this.mStagedSessionFailed = true;
            this.mStagedSessionErrorCode = errorCode;
            this.mStagedSessionErrorMessage = errorMessage;
            Slog.d(TAG, "Marking session " + this.sessionId + " as failed: " + errorMessage);
        }
        cleanStageDir();
        this.mCallback.onStagedSessionChanged(this);
    }

    /* access modifiers changed from: package-private */
    public void setStagedSessionApplied() {
        synchronized (this.mLock) {
            this.mStagedSessionReady = false;
            this.mStagedSessionApplied = true;
            this.mStagedSessionFailed = false;
            this.mStagedSessionErrorCode = 0;
            this.mStagedSessionErrorMessage = "";
            Slog.d(TAG, "Marking session " + this.sessionId + " as applied");
        }
        cleanStageDir();
        this.mCallback.onStagedSessionChanged(this);
    }

    /* access modifiers changed from: package-private */
    public boolean isStagedSessionReady() {
        return this.mStagedSessionReady;
    }

    /* access modifiers changed from: package-private */
    public boolean isStagedSessionApplied() {
        return this.mStagedSessionApplied;
    }

    /* access modifiers changed from: package-private */
    public boolean isStagedSessionFailed() {
        return this.mStagedSessionFailed;
    }

    /* access modifiers changed from: package-private */
    public int getStagedSessionErrorCode() {
        return this.mStagedSessionErrorCode;
    }

    /* access modifiers changed from: package-private */
    public String getStagedSessionErrorMessage() {
        return this.mStagedSessionErrorMessage;
    }

    /* access modifiers changed from: private */
    public void destroyInternal() {
        synchronized (this.mLock) {
            this.mSealed = true;
            if (!this.params.isStaged || isStagedAndInTerminalState()) {
                this.mDestroyed = true;
            }
            Iterator<RevocableFileDescriptor> it = this.mFds.iterator();
            while (it.hasNext()) {
                it.next().revoke();
            }
            Iterator<FileBridge> it2 = this.mBridges.iterator();
            while (it2.hasNext()) {
                it2.next().forceClose();
            }
        }
        if (this.stageDir != null && !this.params.isStaged) {
            try {
                this.mPm.mInstaller.rmPackageDir(this.stageDir.getAbsolutePath());
            } catch (Installer.InstallerException e) {
            }
        }
    }

    private void cleanStageDir() {
        if (isMultiPackage()) {
            for (int childSessionId : getChildSessionIds()) {
                this.mSessionProvider.getSession(childSessionId).cleanStageDir();
            }
            return;
        }
        try {
            this.mPm.mInstaller.rmPackageDir(this.stageDir.getAbsolutePath());
        } catch (Installer.InstallerException e) {
        }
    }

    /* access modifiers changed from: package-private */
    public void dump(IndentingPrintWriter pw) {
        synchronized (this.mLock) {
            dumpLocked(pw);
        }
    }

    @GuardedBy({"mLock"})
    private void dumpLocked(IndentingPrintWriter pw) {
        pw.println("Session " + this.sessionId + ":");
        pw.increaseIndent();
        pw.printPair(ATTR_USER_ID, Integer.valueOf(this.userId));
        pw.printPair("mOriginalInstallerUid", Integer.valueOf(this.mOriginalInstallerUid));
        pw.printPair("mInstallerPackageName", this.mInstallerPackageName);
        pw.printPair("mInstallerUid", Integer.valueOf(this.mInstallerUid));
        pw.printPair(ATTR_CREATED_MILLIS, Long.valueOf(this.createdMillis));
        pw.printPair("stageDir", this.stageDir);
        pw.printPair("stageCid", this.stageCid);
        pw.println();
        this.params.dump(pw);
        pw.printPair("mClientProgress", Float.valueOf(this.mClientProgress));
        pw.printPair("mProgress", Float.valueOf(this.mProgress));
        pw.printPair("mCommitted", Boolean.valueOf(this.mCommitted));
        pw.printPair("mSealed", Boolean.valueOf(this.mSealed));
        pw.printPair("mPermissionsManuallyAccepted", Boolean.valueOf(this.mPermissionsManuallyAccepted));
        pw.printPair("mRelinquished", Boolean.valueOf(this.mRelinquished));
        pw.printPair("mDestroyed", Boolean.valueOf(this.mDestroyed));
        pw.printPair("mFds", Integer.valueOf(this.mFds.size()));
        pw.printPair("mBridges", Integer.valueOf(this.mBridges.size()));
        pw.printPair("mFinalStatus", Integer.valueOf(this.mFinalStatus));
        pw.printPair("mFinalMessage", this.mFinalMessage);
        pw.printPair("params.isMultiPackage", Boolean.valueOf(this.params.isMultiPackage));
        pw.printPair("params.isStaged", Boolean.valueOf(this.params.isStaged));
        if (this.mStatistic.pkg != null) {
            pw.printPair("mStatistic", this.mStatistic);
        }
        pw.println();
        pw.decreaseIndent();
    }

    private static void writeGrantedRuntimePermissionsLocked(XmlSerializer out, String[] grantedRuntimePermissions) throws IOException {
        if (grantedRuntimePermissions != null) {
            for (String permission : grantedRuntimePermissions) {
                out.startTag((String) null, TAG_GRANTED_RUNTIME_PERMISSION);
                XmlUtils.writeStringAttribute(out, "name", permission);
                out.endTag((String) null, TAG_GRANTED_RUNTIME_PERMISSION);
            }
        }
    }

    private static void writeWhitelistedRestrictedPermissionsLocked(XmlSerializer out, List<String> whitelistedRestrictedPermissions) throws IOException {
        if (whitelistedRestrictedPermissions != null) {
            int permissionCount = whitelistedRestrictedPermissions.size();
            for (int i = 0; i < permissionCount; i++) {
                out.startTag((String) null, TAG_WHITELISTED_RESTRICTED_PERMISSION);
                XmlUtils.writeStringAttribute(out, "name", whitelistedRestrictedPermissions.get(i));
                out.endTag((String) null, TAG_WHITELISTED_RESTRICTED_PERMISSION);
            }
        }
    }

    private static File buildAppIconFile(int sessionId2, File sessionsDir) {
        return new File(sessionsDir, "app_icon." + sessionId2 + ".png");
    }

    /* Debug info: failed to restart local var, previous not found, register: 8 */
    /* JADX INFO: finally extract failed */
    /* access modifiers changed from: package-private */
    public void write(XmlSerializer out, File sessionsDir) throws IOException {
        synchronized (this.mLock) {
            if (!this.mDestroyed) {
                out.startTag((String) null, TAG_SESSION);
                XmlUtils.writeIntAttribute(out, ATTR_SESSION_ID, this.sessionId);
                XmlUtils.writeIntAttribute(out, ATTR_USER_ID, this.userId);
                XmlUtils.writeStringAttribute(out, ATTR_INSTALLER_PACKAGE_NAME, this.mInstallerPackageName);
                XmlUtils.writeIntAttribute(out, ATTR_INSTALLER_UID, this.mInstallerUid);
                XmlUtils.writeLongAttribute(out, ATTR_CREATED_MILLIS, this.createdMillis);
                XmlUtils.writeLongAttribute(out, ATTR_UPDATED_MILLIS, this.updatedMillis);
                if (this.stageDir != null) {
                    XmlUtils.writeStringAttribute(out, ATTR_SESSION_STAGE_DIR, this.stageDir.getAbsolutePath());
                }
                if (this.stageCid != null) {
                    XmlUtils.writeStringAttribute(out, ATTR_SESSION_STAGE_CID, this.stageCid);
                }
                XmlUtils.writeBooleanAttribute(out, ATTR_PREPARED, isPrepared());
                XmlUtils.writeBooleanAttribute(out, ATTR_COMMITTED, isCommitted());
                XmlUtils.writeBooleanAttribute(out, ATTR_SEALED, isSealed());
                XmlUtils.writeBooleanAttribute(out, ATTR_MULTI_PACKAGE, this.params.isMultiPackage);
                XmlUtils.writeBooleanAttribute(out, ATTR_STAGED_SESSION, this.params.isStaged);
                XmlUtils.writeBooleanAttribute(out, ATTR_IS_READY, this.mStagedSessionReady);
                XmlUtils.writeBooleanAttribute(out, ATTR_IS_FAILED, this.mStagedSessionFailed);
                XmlUtils.writeBooleanAttribute(out, ATTR_IS_APPLIED, this.mStagedSessionApplied);
                XmlUtils.writeIntAttribute(out, ATTR_STAGED_SESSION_ERROR_CODE, this.mStagedSessionErrorCode);
                XmlUtils.writeStringAttribute(out, ATTR_STAGED_SESSION_ERROR_MESSAGE, this.mStagedSessionErrorMessage);
                XmlUtils.writeIntAttribute(out, ATTR_PARENT_SESSION_ID, this.mParentSessionId);
                XmlUtils.writeIntAttribute(out, ATTR_MODE, this.params.mode);
                XmlUtils.writeIntAttribute(out, ATTR_INSTALL_FLAGS, this.params.installFlags);
                XmlUtils.writeIntAttribute(out, ATTR_INSTALL_LOCATION, this.params.installLocation);
                XmlUtils.writeLongAttribute(out, ATTR_SIZE_BYTES, this.params.sizeBytes);
                XmlUtils.writeStringAttribute(out, ATTR_APP_PACKAGE_NAME, this.params.appPackageName);
                XmlUtils.writeStringAttribute(out, ATTR_APP_LABEL, this.params.appLabel);
                XmlUtils.writeUriAttribute(out, ATTR_ORIGINATING_URI, this.params.originatingUri);
                XmlUtils.writeIntAttribute(out, ATTR_ORIGINATING_UID, this.params.originatingUid);
                XmlUtils.writeUriAttribute(out, ATTR_REFERRER_URI, this.params.referrerUri);
                XmlUtils.writeStringAttribute(out, ATTR_ABI_OVERRIDE, this.params.abiOverride);
                XmlUtils.writeStringAttribute(out, ATTR_VOLUME_UUID, this.params.volumeUuid);
                XmlUtils.writeIntAttribute(out, ATTR_INSTALL_REASON, this.params.installReason);
                writeGrantedRuntimePermissionsLocked(out, this.params.grantedRuntimePermissions);
                writeWhitelistedRestrictedPermissionsLocked(out, this.params.whitelistedRestrictedPermissions);
                File appIconFile = buildAppIconFile(this.sessionId, sessionsDir);
                if (this.params.appIcon == null && appIconFile.exists()) {
                    appIconFile.delete();
                } else if (!(this.params.appIcon == null || appIconFile.lastModified() == this.params.appIconLastModified)) {
                    Slog.w(TAG, "Writing changed icon " + appIconFile);
                    FileOutputStream os = null;
                    try {
                        os = new FileOutputStream(appIconFile);
                        this.params.appIcon.compress(Bitmap.CompressFormat.PNG, 90, os);
                        IoUtils.closeQuietly(os);
                    } catch (IOException e) {
                        try {
                            Slog.w(TAG, "Failed to write icon " + appIconFile + ": " + e.getMessage());
                            IoUtils.closeQuietly(os);
                        } catch (Throwable th) {
                            IoUtils.closeQuietly(os);
                            throw th;
                        }
                    }
                    this.params.appIconLastModified = appIconFile.lastModified();
                }
                for (int childSessionId : getChildSessionIds()) {
                    out.startTag((String) null, TAG_CHILD_SESSION);
                    XmlUtils.writeIntAttribute(out, ATTR_SESSION_ID, childSessionId);
                    out.endTag((String) null, TAG_CHILD_SESSION);
                }
                out.endTag((String) null, TAG_SESSION);
            }
        }
    }

    private static boolean isStagedSessionStateValid(boolean isReady, boolean isApplied, boolean isFailed) {
        return (!isReady && !isApplied && !isFailed) || (isReady && !isApplied && !isFailed) || ((!isReady && isApplied && !isFailed) || (!isReady && !isApplied && isFailed));
    }

    public static PackageInstallerSession readFromXml(XmlPullParser in, PackageInstallerService.InternalCallback callback, Context context, PackageManagerService pm, Looper installerThread, StagingManager stagingManager, File sessionsDir, PackageSessionProvider sessionProvider) throws IOException, XmlPullParserException {
        boolean isApplied;
        int outerDepth;
        List<Integer> childSessionIds;
        int type;
        int[] childSessionIdsArray;
        XmlPullParser xmlPullParser = in;
        int sessionId2 = XmlUtils.readIntAttribute(xmlPullParser, ATTR_SESSION_ID);
        int userId2 = XmlUtils.readIntAttribute(xmlPullParser, ATTR_USER_ID);
        String installerPackageName = XmlUtils.readStringAttribute(xmlPullParser, ATTR_INSTALLER_PACKAGE_NAME);
        int installerUid = XmlUtils.readIntAttribute(xmlPullParser, ATTR_INSTALLER_UID, pm.getPackageUid(installerPackageName, 8192, userId2));
        long createdMillis2 = XmlUtils.readLongAttribute(xmlPullParser, ATTR_CREATED_MILLIS);
        long readLongAttribute = XmlUtils.readLongAttribute(xmlPullParser, ATTR_UPDATED_MILLIS);
        String stageDirRaw = XmlUtils.readStringAttribute(xmlPullParser, ATTR_SESSION_STAGE_DIR);
        File stageDir2 = stageDirRaw != null ? new File(stageDirRaw) : null;
        String stageCid2 = XmlUtils.readStringAttribute(xmlPullParser, ATTR_SESSION_STAGE_CID);
        int i = 1;
        boolean prepared = XmlUtils.readBooleanAttribute(xmlPullParser, ATTR_PREPARED, true);
        boolean committed = XmlUtils.readBooleanAttribute(xmlPullParser, ATTR_COMMITTED);
        boolean sealed = XmlUtils.readBooleanAttribute(xmlPullParser, ATTR_SEALED);
        int parentSessionId = XmlUtils.readIntAttribute(xmlPullParser, ATTR_PARENT_SESSION_ID, -1);
        PackageInstaller.SessionParams params2 = new PackageInstaller.SessionParams(-1);
        params2.isMultiPackage = XmlUtils.readBooleanAttribute(xmlPullParser, ATTR_MULTI_PACKAGE, false);
        params2.isStaged = XmlUtils.readBooleanAttribute(xmlPullParser, ATTR_STAGED_SESSION, false);
        params2.mode = XmlUtils.readIntAttribute(xmlPullParser, ATTR_MODE);
        params2.installFlags = XmlUtils.readIntAttribute(xmlPullParser, ATTR_INSTALL_FLAGS);
        params2.installLocation = XmlUtils.readIntAttribute(xmlPullParser, ATTR_INSTALL_LOCATION);
        params2.sizeBytes = XmlUtils.readLongAttribute(xmlPullParser, ATTR_SIZE_BYTES);
        params2.appPackageName = XmlUtils.readStringAttribute(xmlPullParser, ATTR_APP_PACKAGE_NAME);
        params2.appIcon = XmlUtils.readBitmapAttribute(xmlPullParser, ATTR_APP_ICON);
        params2.appLabel = XmlUtils.readStringAttribute(xmlPullParser, ATTR_APP_LABEL);
        params2.originatingUri = XmlUtils.readUriAttribute(xmlPullParser, ATTR_ORIGINATING_URI);
        params2.originatingUid = XmlUtils.readIntAttribute(xmlPullParser, ATTR_ORIGINATING_UID, -1);
        params2.referrerUri = XmlUtils.readUriAttribute(xmlPullParser, ATTR_REFERRER_URI);
        params2.abiOverride = XmlUtils.readStringAttribute(xmlPullParser, ATTR_ABI_OVERRIDE);
        params2.volumeUuid = XmlUtils.readStringAttribute(xmlPullParser, ATTR_VOLUME_UUID);
        params2.installReason = XmlUtils.readIntAttribute(xmlPullParser, ATTR_INSTALL_REASON);
        File appIconFile = buildAppIconFile(sessionId2, sessionsDir);
        if (appIconFile.exists()) {
            params2.appIcon = BitmapFactory.decodeFile(appIconFile.getAbsolutePath());
            params2.appIconLastModified = appIconFile.lastModified();
        }
        boolean isReady = XmlUtils.readBooleanAttribute(xmlPullParser, ATTR_IS_READY);
        boolean isFailed = XmlUtils.readBooleanAttribute(xmlPullParser, ATTR_IS_FAILED);
        boolean isApplied2 = XmlUtils.readBooleanAttribute(xmlPullParser, ATTR_IS_APPLIED);
        int stagedSessionErrorCode = XmlUtils.readIntAttribute(xmlPullParser, ATTR_STAGED_SESSION_ERROR_CODE, 0);
        String stagedSessionErrorMessage = XmlUtils.readStringAttribute(xmlPullParser, ATTR_STAGED_SESSION_ERROR_MESSAGE);
        if (isStagedSessionStateValid(isReady, isApplied2, isFailed)) {
            ArrayList arrayList = new ArrayList();
            ArrayList arrayList2 = new ArrayList();
            List<Integer> childSessionIds2 = new ArrayList<>();
            int outerDepth2 = in.getDepth();
            while (true) {
                isApplied = isApplied2;
                int next = in.next();
                int type2 = next;
                if (next == i) {
                    outerDepth = outerDepth2;
                    childSessionIds = childSessionIds2;
                    type = type2;
                    break;
                }
                type = type2;
                if (type == 3 && in.getDepth() <= outerDepth2) {
                    outerDepth = outerDepth2;
                    childSessionIds = childSessionIds2;
                    break;
                } else if (type == 3) {
                    List<Integer> list = childSessionIds2;
                    PackageManagerService packageManagerService = pm;
                    isApplied2 = isApplied;
                    i = 1;
                } else if (type == 4) {
                    PackageManagerService packageManagerService2 = pm;
                    isApplied2 = isApplied;
                    i = 1;
                } else {
                    if (TAG_GRANTED_RUNTIME_PERMISSION.equals(in.getName())) {
                        arrayList.add(XmlUtils.readStringAttribute(xmlPullParser, "name"));
                    }
                    int outerDepth3 = outerDepth2;
                    if (TAG_WHITELISTED_RESTRICTED_PERMISSION.equals(in.getName())) {
                        arrayList2.add(XmlUtils.readStringAttribute(xmlPullParser, "name"));
                    }
                    if (TAG_CHILD_SESSION.equals(in.getName())) {
                        childSessionIds2.add(Integer.valueOf(XmlUtils.readIntAttribute(xmlPullParser, ATTR_SESSION_ID, -1)));
                        PackageManagerService packageManagerService3 = pm;
                        isApplied2 = isApplied;
                        outerDepth2 = outerDepth3;
                        i = 1;
                    } else {
                        PackageManagerService packageManagerService4 = pm;
                        isApplied2 = isApplied;
                        outerDepth2 = outerDepth3;
                        i = 1;
                    }
                }
            }
            if (arrayList.size() > 0) {
                params2.grantedRuntimePermissions = (String[]) arrayList.stream().toArray($$Lambda$PackageInstallerSession$7SecathzbWSLkwYbdNW1Dgq0jU.INSTANCE);
            }
            if (arrayList2.size() > 0) {
                params2.whitelistedRestrictedPermissions = arrayList2;
            }
            if (childSessionIds.size() > 0) {
                childSessionIdsArray = childSessionIds.stream().mapToInt($$Lambda$PackageInstallerSession$fMSKA3sU8iwLB8uwZHGaXjhFI.INSTANCE).toArray();
            } else {
                childSessionIdsArray = EMPTY_CHILD_SESSION_ARRAY;
            }
            int i2 = outerDepth;
            ArrayList arrayList3 = arrayList2;
            ArrayList arrayList4 = arrayList;
            List<Integer> list2 = childSessionIds;
            String str = stageDirRaw;
            int i3 = type;
            String str2 = installerPackageName;
            int i4 = sessionId2;
            int i5 = userId2;
            return new PackageInstallerSession(callback, context, pm, sessionProvider, installerThread, stagingManager, sessionId2, userId2, installerPackageName, installerUid, params2, createdMillis2, stageDir2, stageCid2, prepared, committed, sealed, childSessionIdsArray, parentSessionId, isReady, isFailed, isApplied, stagedSessionErrorCode, stagedSessionErrorMessage);
        }
        throw new IllegalArgumentException("Can't restore staged session with invalid state.");
    }

    static /* synthetic */ String[] lambda$readFromXml$1(int x$0) {
        return new String[x$0];
    }

    static int readChildSessionIdFromXml(XmlPullParser in) {
        return XmlUtils.readIntAttribute(in, ATTR_SESSION_ID, -1);
    }
}
