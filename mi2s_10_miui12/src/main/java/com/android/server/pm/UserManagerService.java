package com.android.server.pm;

import android.app.ActivityManager;
import android.app.ActivityManagerNative;
import android.app.IActivityManager;
import android.app.KeyguardManager;
import android.app.PendingIntent;
import android.app.admin.DevicePolicyEventLogger;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.content.pm.ShortcutServiceInternal;
import android.content.pm.UserInfo;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.Binder;
import android.os.Bundle;
import android.os.Environment;
import android.os.FileUtils;
import android.os.Handler;
import android.os.IBinder;
import android.os.IProgressListener;
import android.os.IUserManager;
import android.os.Message;
import android.os.ParcelFileDescriptor;
import android.os.Parcelable;
import android.os.PersistableBundle;
import android.os.RemoteException;
import android.os.SELinux;
import android.os.ServiceManager;
import android.os.ShellCommand;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.os.UserManager;
import android.os.UserManagerInternal;
import android.os.storage.StorageManager;
import android.provider.Settings;
import android.security.GateKeeper;
import android.service.gatekeeper.IGateKeeperService;
import android.util.AtomicFile;
import android.util.IntArray;
import android.util.Log;
import android.util.Slog;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import android.util.SparseIntArray;
import android.util.TimeUtils;
import android.util.Xml;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.app.IAppOpsService;
import com.android.internal.os.BackgroundThread;
import com.android.internal.util.FastXmlSerializer;
import com.android.internal.util.Preconditions;
import com.android.internal.util.XmlUtils;
import com.android.internal.widget.LockPatternUtils;
import com.android.server.LocalServices;
import com.android.server.LockGuard;
import com.android.server.SystemService;
import com.android.server.pm.UserManagerService;
import com.android.server.wm.ActivityTaskManagerInternal;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import libcore.io.IoUtils;
import miui.os.Build;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

public class UserManagerService extends IUserManager.Stub {
    private static final int ALLOWED_FLAGS_FOR_CREATE_USERS_PERMISSION = 812;
    private static final String ATTR_CREATION_TIME = "created";
    private static final String ATTR_FLAGS = "flags";
    private static final String ATTR_GUEST_TO_REMOVE = "guestToRemove";
    private static final String ATTR_ICON_PATH = "icon";
    private static final String ATTR_ID = "id";
    private static final String ATTR_KEY = "key";
    private static final String ATTR_LAST_LOGGED_IN_FINGERPRINT = "lastLoggedInFingerprint";
    private static final String ATTR_LAST_LOGGED_IN_TIME = "lastLoggedIn";
    private static final String ATTR_MULTIPLE = "m";
    private static final String ATTR_NEXT_SERIAL_NO = "nextSerialNumber";
    private static final String ATTR_PARTIAL = "partial";
    private static final String ATTR_PROFILE_BADGE = "profileBadge";
    private static final String ATTR_PROFILE_GROUP_ID = "profileGroupId";
    private static final String ATTR_RESTRICTED_PROFILE_PARENT_ID = "restrictedProfileParentId";
    private static final String ATTR_SEED_ACCOUNT_NAME = "seedAccountName";
    private static final String ATTR_SEED_ACCOUNT_TYPE = "seedAccountType";
    private static final String ATTR_SERIAL_NO = "serialNumber";
    private static final String ATTR_TYPE_BOOLEAN = "b";
    private static final String ATTR_TYPE_BUNDLE = "B";
    private static final String ATTR_TYPE_BUNDLE_ARRAY = "BA";
    private static final String ATTR_TYPE_INTEGER = "i";
    private static final String ATTR_TYPE_STRING = "s";
    private static final String ATTR_TYPE_STRING_ARRAY = "sa";
    private static final String ATTR_USER_VERSION = "version";
    private static final String ATTR_VALUE_TYPE = "type";
    static final boolean DBG = false;
    private static final boolean DBG_WITH_STACKTRACE = false;
    private static final long EPOCH_PLUS_30_YEARS = 946080000000L;
    private static final String LOG_TAG = "UserManagerService";
    private static final int MAX_MANAGED_PROFILES = (Build.IS_CTS_BUILD ? 1 : 2);
    @VisibleForTesting
    static final int MAX_RECENTLY_REMOVED_IDS_SIZE = 100;
    @VisibleForTesting
    static final int MAX_USER_ID = 21474;
    @VisibleForTesting
    static final int MIN_USER_ID = 10;
    private static final boolean RELEASE_DELETED_USER_ID = false;
    private static final String RESTRICTIONS_FILE_PREFIX = "res_";
    private static final String TAG_ACCOUNT = "account";
    private static final String TAG_DEVICE_OWNER_USER_ID = "deviceOwnerUserId";
    private static final String TAG_DEVICE_POLICY_GLOBAL_RESTRICTIONS = "device_policy_global_restrictions";
    private static final String TAG_DEVICE_POLICY_RESTRICTIONS = "device_policy_restrictions";
    private static final String TAG_ENTRY = "entry";
    private static final String TAG_GLOBAL_RESTRICTION_OWNER_ID = "globalRestrictionOwnerUserId";
    private static final String TAG_GUEST_RESTRICTIONS = "guestRestrictions";
    private static final String TAG_LAST_REQUEST_QUIET_MODE_ENABLED_CALL = "lastRequestQuietModeEnabledCall";
    private static final String TAG_NAME = "name";
    private static final String TAG_RESTRICTIONS = "restrictions";
    private static final String TAG_SEED_ACCOUNT_OPTIONS = "seedAccountOptions";
    private static final String TAG_USER = "user";
    private static final String TAG_USERS = "users";
    private static final String TAG_VALUE = "value";
    private static final String TRON_DEMO_CREATED = "users_demo_created";
    private static final String TRON_GUEST_CREATED = "users_guest_created";
    private static final String TRON_USER_CREATED = "users_user_created";
    private static final String USER_INFO_DIR = ("system" + File.separator + "users");
    private static final String USER_LIST_FILENAME = "userlist.xml";
    private static final String USER_PHOTO_FILENAME = "photo.png";
    private static final String USER_PHOTO_FILENAME_TMP = "photo.png.tmp";
    private static final int USER_VERSION = 7;
    static final int WRITE_USER_DELAY = 2000;
    static final int WRITE_USER_MSG = 1;
    private static final String XML_SUFFIX = ".xml";
    /* access modifiers changed from: private */
    public static final IBinder mUserRestriconToken = new Binder();
    private static UserManagerService sInstance;
    private final String ACTION_DISABLE_QUIET_MODE_AFTER_UNLOCK;
    /* access modifiers changed from: private */
    public IAppOpsService mAppOpsService;
    private final Object mAppRestrictionsLock;
    @GuardedBy({"mRestrictionsLock"})
    private final SparseArray<Bundle> mAppliedUserRestrictions;
    /* access modifiers changed from: private */
    @GuardedBy({"mRestrictionsLock"})
    public final SparseArray<Bundle> mBaseUserRestrictions;
    @GuardedBy({"mRestrictionsLock"})
    private final SparseArray<Bundle> mCachedEffectiveUserRestrictions;
    /* access modifiers changed from: private */
    public final Context mContext;
    @GuardedBy({"mRestrictionsLock"})
    private int mDeviceOwnerUserId;
    @GuardedBy({"mRestrictionsLock"})
    private final SparseArray<Bundle> mDevicePolicyGlobalUserRestrictions;
    @GuardedBy({"mRestrictionsLock"})
    private final SparseArray<Bundle> mDevicePolicyLocalUserRestrictions;
    private final BroadcastReceiver mDisableQuietModeCallback;
    /* access modifiers changed from: private */
    @GuardedBy({"mUsersLock"})
    public boolean mForceEphemeralUsers;
    @GuardedBy({"mGuestRestrictions"})
    private final Bundle mGuestRestrictions;
    /* access modifiers changed from: private */
    public final Handler mHandler;
    /* access modifiers changed from: private */
    @GuardedBy({"mUsersLock"})
    public boolean mIsDeviceManaged;
    /* access modifiers changed from: private */
    @GuardedBy({"mUsersLock"})
    public final SparseBooleanArray mIsUserManaged;
    private final LocalService mLocalService;
    private final LockPatternUtils mLockPatternUtils;
    @GuardedBy({"mPackagesLock"})
    private int mNextSerialNumber;
    /* access modifiers changed from: private */
    public final Object mPackagesLock;
    private final PackageManagerService mPm;
    @GuardedBy({"mUsersLock"})
    private final LinkedList<Integer> mRecentlyRemovedIds;
    @GuardedBy({"mUsersLock"})
    private final SparseBooleanArray mRemovingUserIds;
    /* access modifiers changed from: private */
    public final Object mRestrictionsLock;
    private final UserDataPreparer mUserDataPreparer;
    @GuardedBy({"mUsersLock"})
    private int[] mUserIds;
    private final File mUserListFile;
    /* access modifiers changed from: private */
    @GuardedBy({"mUserRestrictionsListeners"})
    public final ArrayList<UserManagerInternal.UserRestrictionsListener> mUserRestrictionsListeners;
    /* access modifiers changed from: private */
    @GuardedBy({"mUserStates"})
    public final SparseIntArray mUserStates;
    private int mUserVersion;
    @GuardedBy({"mUsersLock"})
    private final SparseArray<UserData> mUsers;
    private final File mUsersDir;
    /* access modifiers changed from: private */
    public final Object mUsersLock;

    @VisibleForTesting
    static class UserData {
        String account;
        UserInfo info;
        private long mLastRequestQuietModeEnabledMillis;
        boolean persistSeedData;
        String seedAccountName;
        PersistableBundle seedAccountOptions;
        String seedAccountType;
        long startRealtime;
        long unlockRealtime;

        UserData() {
        }

        /* access modifiers changed from: package-private */
        public void setLastRequestQuietModeEnabledMillis(long millis) {
            this.mLastRequestQuietModeEnabledMillis = millis;
        }

        /* access modifiers changed from: package-private */
        public long getLastRequestQuietModeEnabledMillis() {
            return this.mLastRequestQuietModeEnabledMillis;
        }

        /* access modifiers changed from: package-private */
        public void clearSeedAccountData() {
            this.seedAccountName = null;
            this.seedAccountType = null;
            this.seedAccountOptions = null;
            this.persistSeedData = false;
        }
    }

    private class DisableQuietModeUserUnlockedCallback extends IProgressListener.Stub {
        private final IntentSender mTarget;

        public DisableQuietModeUserUnlockedCallback(IntentSender target) {
            Preconditions.checkNotNull(target);
            this.mTarget = target;
        }

        public void onStarted(int id, Bundle extras) {
        }

        public void onProgress(int id, int progress, Bundle extras) {
        }

        public void onFinished(int id, Bundle extras) {
            try {
                UserManagerService.this.mContext.startIntentSender(this.mTarget, (Intent) null, 0, 0, 0);
            } catch (IntentSender.SendIntentException e) {
                Slog.e(UserManagerService.LOG_TAG, "Failed to start the target in the callback", e);
            }
        }
    }

    public static UserManagerService getInstance() {
        UserManagerService userManagerService;
        synchronized (UserManagerService.class) {
            userManagerService = sInstance;
        }
        return userManagerService;
    }

    public static class LifeCycle extends SystemService {
        private UserManagerService mUms;

        public LifeCycle(Context context) {
            super(context);
        }

        /* JADX WARNING: type inference failed for: r0v1, types: [com.android.server.pm.UserManagerService, android.os.IBinder] */
        public void onStart() {
            this.mUms = UserManagerService.getInstance();
            publishBinderService(UserManagerService.TAG_USER, this.mUms);
        }

        public void onBootPhase(int phase) {
            if (phase == 550) {
                this.mUms.cleanupPartialUsers();
            }
        }

        public void onStartUser(int userHandle) {
            synchronized (this.mUms.mUsersLock) {
                UserData user = this.mUms.getUserDataLU(userHandle);
                if (user != null) {
                    user.startRealtime = SystemClock.elapsedRealtime();
                }
            }
        }

        public void onUnlockUser(int userHandle) {
            synchronized (this.mUms.mUsersLock) {
                UserData user = this.mUms.getUserDataLU(userHandle);
                if (user != null) {
                    user.unlockRealtime = SystemClock.elapsedRealtime();
                }
            }
        }

        public void onStopUser(int userHandle) {
            synchronized (this.mUms.mUsersLock) {
                UserData user = this.mUms.getUserDataLU(userHandle);
                if (user != null) {
                    user.startRealtime = 0;
                    user.unlockRealtime = 0;
                }
            }
        }
    }

    @VisibleForTesting
    UserManagerService(Context context) {
        this(context, (PackageManagerService) null, (UserDataPreparer) null, new Object(), context.getCacheDir());
    }

    UserManagerService(Context context, PackageManagerService pm, UserDataPreparer userDataPreparer, Object packagesLock) {
        this(context, pm, userDataPreparer, packagesLock, Environment.getDataDirectory());
    }

    private UserManagerService(Context context, PackageManagerService pm, UserDataPreparer userDataPreparer, Object packagesLock, File dataDir) {
        this.mUsersLock = LockGuard.installNewLock(2);
        this.mRestrictionsLock = new Object();
        this.mAppRestrictionsLock = new Object();
        this.mUsers = new SparseArray<>();
        this.mBaseUserRestrictions = new SparseArray<>();
        this.mCachedEffectiveUserRestrictions = new SparseArray<>();
        this.mAppliedUserRestrictions = new SparseArray<>();
        this.mDevicePolicyGlobalUserRestrictions = new SparseArray<>();
        this.mDeviceOwnerUserId = ScreenRotationAnimationInjector.BLACK_SURFACE_INVALID_POSITION;
        this.mDevicePolicyLocalUserRestrictions = new SparseArray<>();
        this.mGuestRestrictions = new Bundle();
        this.mRemovingUserIds = new SparseBooleanArray();
        this.mRecentlyRemovedIds = new LinkedList<>();
        this.mUserVersion = 0;
        this.mIsUserManaged = new SparseBooleanArray();
        this.mUserRestrictionsListeners = new ArrayList<>();
        this.ACTION_DISABLE_QUIET_MODE_AFTER_UNLOCK = "com.android.server.pm.DISABLE_QUIET_MODE_AFTER_UNLOCK";
        this.mDisableQuietModeCallback = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                if ("com.android.server.pm.DISABLE_QUIET_MODE_AFTER_UNLOCK".equals(intent.getAction())) {
                    BackgroundThread.getHandler().post(new Runnable(intent.getIntExtra("android.intent.extra.USER_ID", ScreenRotationAnimationInjector.BLACK_SURFACE_INVALID_POSITION), (IntentSender) intent.getParcelableExtra("android.intent.extra.INTENT")) {
                        private final /* synthetic */ int f$1;
                        private final /* synthetic */ IntentSender f$2;

                        {
                            this.f$1 = r2;
                            this.f$2 = r3;
                        }

                        public final void run() {
                            UserManagerService.AnonymousClass1.this.lambda$onReceive$0$UserManagerService$1(this.f$1, this.f$2);
                        }
                    });
                }
            }

            public /* synthetic */ void lambda$onReceive$0$UserManagerService$1(int userHandle, IntentSender target) {
                UserManagerService.this.setQuietModeEnabled(userHandle, false, target, (String) null);
            }
        };
        this.mUserStates = new SparseIntArray();
        this.mContext = context;
        this.mPm = pm;
        this.mPackagesLock = packagesLock;
        this.mHandler = new MainHandler();
        this.mUserDataPreparer = userDataPreparer;
        synchronized (this.mPackagesLock) {
            this.mUsersDir = new File(dataDir, USER_INFO_DIR);
            this.mUsersDir.mkdirs();
            new File(this.mUsersDir, String.valueOf(0)).mkdirs();
            FileUtils.setPermissions(this.mUsersDir.toString(), 509, -1, -1);
            this.mUserListFile = new File(this.mUsersDir, USER_LIST_FILENAME);
            initDefaultGuestRestrictions();
            readUserListLP();
            sInstance = this;
        }
        this.mLocalService = new LocalService();
        LocalServices.addService(UserManagerInternal.class, this.mLocalService);
        this.mLockPatternUtils = new LockPatternUtils(this.mContext);
        this.mUserStates.put(0, 0);
    }

    /* access modifiers changed from: package-private */
    public void systemReady() {
        this.mAppOpsService = IAppOpsService.Stub.asInterface(ServiceManager.getService("appops"));
        synchronized (this.mRestrictionsLock) {
            applyUserRestrictionsLR(0);
        }
        UserInfo currentGuestUser = findCurrentGuestUser();
        if (currentGuestUser != null && !hasUserRestriction("no_config_wifi", currentGuestUser.id)) {
            setUserRestriction("no_config_wifi", true, currentGuestUser.id);
        }
        this.mContext.registerReceiver(this.mDisableQuietModeCallback, new IntentFilter("com.android.server.pm.DISABLE_QUIET_MODE_AFTER_UNLOCK"), (String) null, this.mHandler);
    }

    /* access modifiers changed from: package-private */
    public void cleanupPartialUsers() {
        ArrayList<UserInfo> partials = new ArrayList<>();
        synchronized (this.mUsersLock) {
            int userSize = this.mUsers.size();
            for (int i = 0; i < userSize; i++) {
                UserInfo ui = this.mUsers.valueAt(i).info;
                if ((ui.partial || ui.guestToRemove || ui.isEphemeral()) && i != 0) {
                    partials.add(ui);
                    addRemovingUserIdLocked(ui.id);
                    ui.partial = true;
                }
            }
        }
        int partialsSize = partials.size();
        for (int i2 = 0; i2 < partialsSize; i2++) {
            UserInfo ui2 = partials.get(i2);
            Slog.w(LOG_TAG, "Removing partially created user " + ui2.id + " (name=" + ui2.name + ")");
            removeUserState(ui2.id);
        }
    }

    public String getUserAccount(int userId) {
        String str;
        checkManageUserAndAcrossUsersFullPermission("get user account");
        synchronized (this.mUsersLock) {
            str = this.mUsers.get(userId).account;
        }
        return str;
    }

    /* Debug info: failed to restart local var, previous not found, register: 7 */
    /* JADX WARNING: Code restructure failed: missing block: B:18:0x003c, code lost:
        if (r0 == null) goto L_0x0041;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:20:?, code lost:
        writeUserLP(r0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:22:0x0042, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void setUserAccount(int r8, java.lang.String r9) {
        /*
            r7 = this;
            java.lang.String r0 = "set user account"
            checkManageUserAndAcrossUsersFullPermission(r0)
            r0 = 0
            java.lang.Object r1 = r7.mPackagesLock
            monitor-enter(r1)
            java.lang.Object r2 = r7.mUsersLock     // Catch:{ all -> 0x0046 }
            monitor-enter(r2)     // Catch:{ all -> 0x0046 }
            android.util.SparseArray<com.android.server.pm.UserManagerService$UserData> r3 = r7.mUsers     // Catch:{ all -> 0x0043 }
            java.lang.Object r3 = r3.get(r8)     // Catch:{ all -> 0x0043 }
            com.android.server.pm.UserManagerService$UserData r3 = (com.android.server.pm.UserManagerService.UserData) r3     // Catch:{ all -> 0x0043 }
            if (r3 != 0) goto L_0x0030
            java.lang.String r4 = "UserManagerService"
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ all -> 0x0043 }
            r5.<init>()     // Catch:{ all -> 0x0043 }
            java.lang.String r6 = "User not found for setting user account: u"
            r5.append(r6)     // Catch:{ all -> 0x0043 }
            r5.append(r8)     // Catch:{ all -> 0x0043 }
            java.lang.String r5 = r5.toString()     // Catch:{ all -> 0x0043 }
            android.util.Slog.e(r4, r5)     // Catch:{ all -> 0x0043 }
            monitor-exit(r2)     // Catch:{ all -> 0x0043 }
            monitor-exit(r1)     // Catch:{ all -> 0x0046 }
            return
        L_0x0030:
            java.lang.String r4 = r3.account     // Catch:{ all -> 0x0043 }
            boolean r5 = java.util.Objects.equals(r4, r9)     // Catch:{ all -> 0x0043 }
            if (r5 != 0) goto L_0x003b
            r3.account = r9     // Catch:{ all -> 0x0043 }
            r0 = r3
        L_0x003b:
            monitor-exit(r2)     // Catch:{ all -> 0x0043 }
            if (r0 == 0) goto L_0x0041
            r7.writeUserLP(r0)     // Catch:{ all -> 0x0046 }
        L_0x0041:
            monitor-exit(r1)     // Catch:{ all -> 0x0046 }
            return
        L_0x0043:
            r3 = move-exception
            monitor-exit(r2)     // Catch:{ all -> 0x0043 }
            throw r3     // Catch:{ all -> 0x0046 }
        L_0x0046:
            r2 = move-exception
            monitor-exit(r1)     // Catch:{ all -> 0x0046 }
            throw r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.pm.UserManagerService.setUserAccount(int, java.lang.String):void");
    }

    public UserInfo getPrimaryUser() {
        checkManageUsersPermission("query users");
        synchronized (this.mUsersLock) {
            int userSize = this.mUsers.size();
            for (int i = 0; i < userSize; i++) {
                UserInfo ui = this.mUsers.valueAt(i).info;
                if (ui.isPrimary() && !this.mRemovingUserIds.get(ui.id)) {
                    return ui;
                }
            }
            return null;
        }
    }

    public List<UserInfo> getUsers(boolean excludeDying) {
        ArrayList<UserInfo> users;
        checkManageOrCreateUsersPermission("query users");
        synchronized (this.mUsersLock) {
            users = new ArrayList<>(this.mUsers.size());
            int userSize = this.mUsers.size();
            for (int i = 0; i < userSize; i++) {
                UserInfo ui = this.mUsers.valueAt(i).info;
                if (!ui.partial) {
                    if (!excludeDying || !this.mRemovingUserIds.get(ui.id)) {
                        users.add(userWithName(ui));
                    }
                }
            }
        }
        return users;
    }

    /* Debug info: failed to restart local var, previous not found, register: 5 */
    public List<UserInfo> getProfiles(int userId, boolean enabledOnly) {
        List<UserInfo> profilesLU;
        boolean returnFullInfo = true;
        if (userId != UserHandle.getCallingUserId()) {
            checkManageOrCreateUsersPermission("getting profiles related to user " + userId);
        } else {
            returnFullInfo = hasManageUsersPermission();
        }
        long ident = Binder.clearCallingIdentity();
        try {
            synchronized (this.mUsersLock) {
                profilesLU = getProfilesLU(userId, enabledOnly, returnFullInfo);
            }
            Binder.restoreCallingIdentity(ident);
            return profilesLU;
        } catch (Throwable th) {
            Binder.restoreCallingIdentity(ident);
            throw th;
        }
    }

    /* Debug info: failed to restart local var, previous not found, register: 4 */
    public int[] getProfileIds(int userId, boolean enabledOnly) {
        int[] array;
        if (userId != UserHandle.getCallingUserId()) {
            checkManageOrCreateUsersPermission("getting profiles related to user " + userId);
        }
        long ident = Binder.clearCallingIdentity();
        try {
            synchronized (this.mUsersLock) {
                array = getProfileIdsLU(userId, enabledOnly).toArray();
            }
            Binder.restoreCallingIdentity(ident);
            return array;
        } catch (Throwable th) {
            Binder.restoreCallingIdentity(ident);
            throw th;
        }
    }

    @GuardedBy({"mUsersLock"})
    private List<UserInfo> getProfilesLU(int userId, boolean enabledOnly, boolean fullInfo) {
        UserInfo userInfo;
        IntArray profileIds = getProfileIdsLU(userId, enabledOnly);
        ArrayList<UserInfo> users = new ArrayList<>(profileIds.size());
        for (int i = 0; i < profileIds.size(); i++) {
            UserInfo userInfo2 = this.mUsers.get(profileIds.get(i)).info;
            if (!fullInfo) {
                userInfo = new UserInfo(userInfo2);
                userInfo.name = null;
                userInfo.iconPath = null;
            } else {
                userInfo = userWithName(userInfo2);
            }
            users.add(userInfo);
        }
        return users;
    }

    @GuardedBy({"mUsersLock"})
    private IntArray getProfileIdsLU(int userId, boolean enabledOnly) {
        UserInfo user = getUserInfoLU(userId);
        IntArray result = new IntArray(this.mUsers.size());
        if (user == null) {
            return result;
        }
        int userSize = this.mUsers.size();
        for (int i = 0; i < userSize; i++) {
            UserInfo profile = this.mUsers.valueAt(i).info;
            if (isProfileOf(user, profile) && ((!enabledOnly || profile.isEnabled()) && !this.mRemovingUserIds.get(profile.id) && !profile.partial)) {
                result.add(profile.id);
            }
        }
        return result;
    }

    public int getCredentialOwnerProfile(int userHandle) {
        checkManageUsersPermission("get the credential owner");
        if (!this.mLockPatternUtils.isSeparateProfileChallengeEnabled(userHandle)) {
            synchronized (this.mUsersLock) {
                UserInfo profileParent = getProfileParentLU(userHandle);
                if (profileParent != null) {
                    int i = profileParent.id;
                    return i;
                }
            }
        }
        return userHandle;
    }

    public boolean isSameProfileGroup(int userId, int otherUserId) {
        if (userId == otherUserId) {
            return true;
        }
        checkManageUsersPermission("check if in the same profile group");
        return isSameProfileGroupNoChecks(userId, otherUserId);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:17:0x0024, code lost:
        return r2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:19:0x0026, code lost:
        return false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:21:0x0028, code lost:
        return false;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private boolean isSameProfileGroupNoChecks(int r7, int r8) {
        /*
            r6 = this;
            java.lang.Object r0 = r6.mUsersLock
            monitor-enter(r0)
            android.content.pm.UserInfo r1 = r6.getUserInfoLU(r7)     // Catch:{ all -> 0x0029 }
            r2 = 0
            if (r1 == 0) goto L_0x0027
            int r3 = r1.profileGroupId     // Catch:{ all -> 0x0029 }
            r4 = -10000(0xffffffffffffd8f0, float:NaN)
            if (r3 != r4) goto L_0x0011
            goto L_0x0027
        L_0x0011:
            android.content.pm.UserInfo r3 = r6.getUserInfoLU(r8)     // Catch:{ all -> 0x0029 }
            if (r3 == 0) goto L_0x0025
            int r5 = r3.profileGroupId     // Catch:{ all -> 0x0029 }
            if (r5 != r4) goto L_0x001c
            goto L_0x0025
        L_0x001c:
            int r4 = r1.profileGroupId     // Catch:{ all -> 0x0029 }
            int r5 = r3.profileGroupId     // Catch:{ all -> 0x0029 }
            if (r4 != r5) goto L_0x0023
            r2 = 1
        L_0x0023:
            monitor-exit(r0)     // Catch:{ all -> 0x0029 }
            return r2
        L_0x0025:
            monitor-exit(r0)     // Catch:{ all -> 0x0029 }
            return r2
        L_0x0027:
            monitor-exit(r0)     // Catch:{ all -> 0x0029 }
            return r2
        L_0x0029:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x0029 }
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.pm.UserManagerService.isSameProfileGroupNoChecks(int, int):boolean");
    }

    public UserInfo getProfileParent(int userHandle) {
        UserInfo profileParentLU;
        checkManageUsersPermission("get the profile parent");
        synchronized (this.mUsersLock) {
            profileParentLU = getProfileParentLU(userHandle);
        }
        return profileParentLU;
    }

    public int getProfileParentId(int userHandle) {
        checkManageUsersPermission("get the profile parent");
        return this.mLocalService.getProfileParentId(userHandle);
    }

    /* access modifiers changed from: private */
    @GuardedBy({"mUsersLock"})
    public UserInfo getProfileParentLU(int userHandle) {
        int parentUserId;
        UserInfo profile = getUserInfoLU(userHandle);
        if (profile == null || (parentUserId = profile.profileGroupId) == userHandle || parentUserId == -10000) {
            return null;
        }
        return getUserInfoLU(parentUserId);
    }

    private static boolean isProfileOf(UserInfo user, UserInfo profile) {
        return user.id == profile.id || (user.profileGroupId != -10000 && user.profileGroupId == profile.profileGroupId);
    }

    private void broadcastProfileAvailabilityChanges(UserHandle profileHandle, UserHandle parentHandle, boolean inQuietMode) {
        Intent intent = new Intent();
        if (inQuietMode) {
            intent.setAction("android.intent.action.MANAGED_PROFILE_UNAVAILABLE");
        } else {
            intent.setAction("android.intent.action.MANAGED_PROFILE_AVAILABLE");
        }
        intent.putExtra("android.intent.extra.QUIET_MODE", inQuietMode);
        intent.putExtra("android.intent.extra.USER", profileHandle);
        intent.putExtra("android.intent.extra.user_handle", profileHandle.getIdentifier());
        intent.addFlags(1073741824);
        this.mContext.sendBroadcastAsUser(intent, parentHandle);
    }

    public boolean requestQuietModeEnabled(String callingPackage, boolean enableQuietMode, int userHandle, IntentSender target) {
        Preconditions.checkNotNull(callingPackage);
        if (!enableQuietMode || target == null) {
            boolean needToShowConfirmCredential = true;
            ensureCanModifyQuietMode(callingPackage, Binder.getCallingUid(), target != null);
            long identity = Binder.clearCallingIdentity();
            boolean result = false;
            if (enableQuietMode) {
                try {
                    setQuietModeEnabled(userHandle, true, target, callingPackage);
                    result = true;
                } catch (Throwable th) {
                    Binder.restoreCallingIdentity(identity);
                    throw th;
                }
            } else {
                if (!this.mLockPatternUtils.isSecure(userHandle) || StorageManager.isUserKeyUnlocked(userHandle)) {
                    needToShowConfirmCredential = false;
                }
                if (needToShowConfirmCredential) {
                    showConfirmCredentialToDisableQuietMode(userHandle, target);
                } else {
                    setQuietModeEnabled(userHandle, false, target, callingPackage);
                    result = true;
                }
            }
            Binder.restoreCallingIdentity(identity);
            return result;
        }
        throw new IllegalArgumentException("target should only be specified when we are disabling quiet mode.");
    }

    private void ensureCanModifyQuietMode(String callingPackage, int callingUid, boolean startIntent) {
        if (!hasManageUsersPermission()) {
            if (startIntent) {
                throw new SecurityException("MANAGE_USERS permission is required to start intent after disabling quiet mode.");
            } else if (!hasPermissionGranted("android.permission.MODIFY_QUIET_MODE", callingUid)) {
                verifyCallingPackage(callingPackage, callingUid);
                ShortcutServiceInternal shortcutInternal = (ShortcutServiceInternal) LocalServices.getService(ShortcutServiceInternal.class);
                if (shortcutInternal == null || !shortcutInternal.isForegroundDefaultLauncher(callingPackage, callingUid)) {
                    throw new SecurityException("Can't modify quiet mode, caller is neither foreground default launcher nor has MANAGE_USERS/MODIFY_QUIET_MODE permission");
                }
            }
        }
    }

    /* Debug info: failed to restart local var, previous not found, register: 6 */
    /* access modifiers changed from: private */
    /* JADX WARNING: Code restructure failed: missing block: B:14:0x003e, code lost:
        r4 = r6.mPackagesLock;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:15:0x0040, code lost:
        monitor-enter(r4);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:17:?, code lost:
        writeUserLP(r3);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:18:0x0044, code lost:
        monitor-exit(r4);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:19:0x0045, code lost:
        r0 = null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:20:0x0046, code lost:
        if (r8 == false) goto L_0x005e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:22:?, code lost:
        android.app.ActivityManager.getService().stopUser(r7, true, (android.app.IStopUserCallback) null);
        ((android.app.ActivityManagerInternal) com.android.server.LocalServices.getService(android.app.ActivityManagerInternal.class)).killForegroundAppsForUser(r7);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:23:0x005c, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:25:0x005e, code lost:
        if (r9 == null) goto L_0x0067;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:26:0x0060, code lost:
        r0 = new com.android.server.pm.UserManagerService.DisableQuietModeUserUnlockedCallback(r6, r9);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:27:0x0067, code lost:
        android.app.ActivityManager.getService().startUserInBackgroundWithListener(r7, r0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:28:0x006f, code lost:
        logQuietModeEnabled(r7, r8, r10);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:29:0x0073, code lost:
        r0.rethrowAsRuntimeException();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:30:0x0076, code lost:
        broadcastProfileAvailabilityChanges(r1.getUserHandle(), r2.getUserHandle(), r8);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:31:0x0081, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void setQuietModeEnabled(int r7, boolean r8, android.content.IntentSender r9, java.lang.String r10) {
        /*
            r6 = this;
            java.lang.Object r0 = r6.mUsersLock
            monitor-enter(r0)
            android.content.pm.UserInfo r1 = r6.getUserInfoLU(r7)     // Catch:{ all -> 0x00a1 }
            android.content.pm.UserInfo r2 = r6.getProfileParentLU(r7)     // Catch:{ all -> 0x00a1 }
            if (r1 == 0) goto L_0x0085
            boolean r3 = r1.isManagedProfile()     // Catch:{ all -> 0x00a1 }
            if (r3 == 0) goto L_0x0085
            boolean r3 = r1.isQuietModeEnabled()     // Catch:{ all -> 0x00a1 }
            if (r3 != r8) goto L_0x0031
            java.lang.String r3 = "UserManagerService"
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ all -> 0x00a1 }
            r4.<init>()     // Catch:{ all -> 0x00a1 }
            java.lang.String r5 = "Quiet mode is already "
            r4.append(r5)     // Catch:{ all -> 0x00a1 }
            r4.append(r8)     // Catch:{ all -> 0x00a1 }
            java.lang.String r4 = r4.toString()     // Catch:{ all -> 0x00a1 }
            android.util.Slog.i(r3, r4)     // Catch:{ all -> 0x00a1 }
            monitor-exit(r0)     // Catch:{ all -> 0x00a1 }
            return
        L_0x0031:
            int r3 = r1.flags     // Catch:{ all -> 0x00a1 }
            r3 = r3 ^ 128(0x80, float:1.794E-43)
            r1.flags = r3     // Catch:{ all -> 0x00a1 }
            int r3 = r1.id     // Catch:{ all -> 0x00a1 }
            com.android.server.pm.UserManagerService$UserData r3 = r6.getUserDataLU(r3)     // Catch:{ all -> 0x00a1 }
            monitor-exit(r0)     // Catch:{ all -> 0x00a1 }
            java.lang.Object r4 = r6.mPackagesLock
            monitor-enter(r4)
            r6.writeUserLP(r3)     // Catch:{ all -> 0x0082 }
            monitor-exit(r4)     // Catch:{ all -> 0x0082 }
            r0 = 0
            if (r8 == 0) goto L_0x005e
            android.app.IActivityManager r4 = android.app.ActivityManager.getService()     // Catch:{ RemoteException -> 0x005c }
            r5 = 1
            r4.stopUser(r7, r5, r0)     // Catch:{ RemoteException -> 0x005c }
            java.lang.Class<android.app.ActivityManagerInternal> r0 = android.app.ActivityManagerInternal.class
            java.lang.Object r0 = com.android.server.LocalServices.getService(r0)     // Catch:{ RemoteException -> 0x005c }
            android.app.ActivityManagerInternal r0 = (android.app.ActivityManagerInternal) r0     // Catch:{ RemoteException -> 0x005c }
            r0.killForegroundAppsForUser(r7)     // Catch:{ RemoteException -> 0x005c }
            goto L_0x006f
        L_0x005c:
            r0 = move-exception
            goto L_0x0073
        L_0x005e:
            if (r9 == 0) goto L_0x0066
            com.android.server.pm.UserManagerService$DisableQuietModeUserUnlockedCallback r0 = new com.android.server.pm.UserManagerService$DisableQuietModeUserUnlockedCallback     // Catch:{ RemoteException -> 0x005c }
            r0.<init>(r9)     // Catch:{ RemoteException -> 0x005c }
            goto L_0x0067
        L_0x0066:
        L_0x0067:
            android.app.IActivityManager r4 = android.app.ActivityManager.getService()     // Catch:{ RemoteException -> 0x005c }
            r4.startUserInBackgroundWithListener(r7, r0)     // Catch:{ RemoteException -> 0x005c }
        L_0x006f:
            r6.logQuietModeEnabled(r7, r8, r10)     // Catch:{ RemoteException -> 0x005c }
            goto L_0x0076
        L_0x0073:
            r0.rethrowAsRuntimeException()
        L_0x0076:
            android.os.UserHandle r0 = r1.getUserHandle()
            android.os.UserHandle r4 = r2.getUserHandle()
            r6.broadcastProfileAvailabilityChanges(r0, r4, r8)
            return
        L_0x0082:
            r0 = move-exception
            monitor-exit(r4)     // Catch:{ all -> 0x0082 }
            throw r0
        L_0x0085:
            java.lang.IllegalArgumentException r3 = new java.lang.IllegalArgumentException     // Catch:{ all -> 0x00a1 }
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ all -> 0x00a1 }
            r4.<init>()     // Catch:{ all -> 0x00a1 }
            java.lang.String r5 = "User "
            r4.append(r5)     // Catch:{ all -> 0x00a1 }
            r4.append(r7)     // Catch:{ all -> 0x00a1 }
            java.lang.String r5 = " is not a profile"
            r4.append(r5)     // Catch:{ all -> 0x00a1 }
            java.lang.String r4 = r4.toString()     // Catch:{ all -> 0x00a1 }
            r3.<init>(r4)     // Catch:{ all -> 0x00a1 }
            throw r3     // Catch:{ all -> 0x00a1 }
        L_0x00a1:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x00a1 }
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.pm.UserManagerService.setQuietModeEnabled(int, boolean, android.content.IntentSender, java.lang.String):void");
    }

    private void logQuietModeEnabled(int userHandle, boolean enableQuietMode, String callingPackage) {
        UserData userData;
        long period;
        synchronized (this.mUsersLock) {
            userData = getUserDataLU(userHandle);
        }
        if (userData != null) {
            long now = System.currentTimeMillis();
            if (userData.getLastRequestQuietModeEnabledMillis() != 0) {
                period = now - userData.getLastRequestQuietModeEnabledMillis();
            } else {
                period = now - userData.info.creationTime;
            }
            DevicePolicyEventLogger.createEvent(55).setStrings(new String[]{callingPackage}).setBoolean(enableQuietMode).setTimePeriod(period).write();
            userData.setLastRequestQuietModeEnabledMillis(now);
        }
    }

    /* Debug info: failed to restart local var, previous not found, register: 3 */
    public boolean isQuietModeEnabled(int userHandle) {
        UserInfo info;
        synchronized (this.mPackagesLock) {
            synchronized (this.mUsersLock) {
                info = getUserInfoLU(userHandle);
            }
            if (info != null) {
                if (info.isManagedProfile()) {
                    boolean isQuietModeEnabled = info.isQuietModeEnabled();
                    return isQuietModeEnabled;
                }
            }
            return false;
        }
    }

    private void showConfirmCredentialToDisableQuietMode(int userHandle, IntentSender target) {
        Intent unlockIntent = ((KeyguardManager) this.mContext.getSystemService("keyguard")).createConfirmDeviceCredentialIntent((CharSequence) null, (CharSequence) null, userHandle);
        if (unlockIntent != null) {
            Intent callBackIntent = new Intent("com.android.server.pm.DISABLE_QUIET_MODE_AFTER_UNLOCK");
            if (target != null) {
                callBackIntent.putExtra("android.intent.extra.INTENT", target);
            }
            callBackIntent.putExtra("android.intent.extra.USER_ID", userHandle);
            callBackIntent.setPackage(this.mContext.getPackageName());
            callBackIntent.addFlags(268435456);
            unlockIntent.putExtra("android.intent.extra.INTENT", PendingIntent.getBroadcast(this.mContext, 0, callBackIntent, 1409286144).getIntentSender());
            unlockIntent.setFlags(276824064);
            this.mContext.startActivity(unlockIntent);
        }
    }

    /* Debug info: failed to restart local var, previous not found, register: 3 */
    public void setUserEnabled(int userId) {
        UserInfo info;
        checkManageUsersPermission("enable user");
        synchronized (this.mPackagesLock) {
            synchronized (this.mUsersLock) {
                info = getUserInfoLU(userId);
            }
            if (info != null) {
                if (!info.isEnabled()) {
                    info.flags ^= 64;
                    writeUserLP(getUserDataLU(info.id));
                }
            }
        }
    }

    /* Debug info: failed to restart local var, previous not found, register: 3 */
    public void setUserAdmin(int userId) {
        UserInfo info;
        checkManageUserAndAcrossUsersFullPermission("set user admin");
        synchronized (this.mPackagesLock) {
            synchronized (this.mUsersLock) {
                info = getUserInfoLU(userId);
            }
            if (info != null) {
                if (!info.isAdmin()) {
                    info.flags ^= 2;
                    writeUserLP(getUserDataLU(info.id));
                    setUserRestriction("no_sms", false, userId);
                    setUserRestriction("no_outgoing_calls", false, userId);
                }
            }
        }
    }

    /* Debug info: failed to restart local var, previous not found, register: 5 */
    public void evictCredentialEncryptionKey(int userId) {
        checkManageUsersPermission("evict CE key");
        IActivityManager am = ActivityManagerNative.getDefault();
        long identity = Binder.clearCallingIdentity();
        try {
            am.restartUserInBackground(userId);
            Binder.restoreCallingIdentity(identity);
        } catch (RemoteException re) {
            throw re.rethrowAsRuntimeException();
        } catch (Throwable th) {
            Binder.restoreCallingIdentity(identity);
            throw th;
        }
    }

    public UserInfo getUserInfo(int userId) {
        UserInfo userWithName;
        checkManageOrCreateUsersPermission("query user");
        synchronized (this.mUsersLock) {
            userWithName = userWithName(getUserInfoLU(userId));
        }
        return userWithName;
    }

    private UserInfo userWithName(UserInfo orig) {
        if (orig == null || orig.name != null || orig.id != 0) {
            return orig;
        }
        UserInfo withName = new UserInfo(orig);
        withName.name = getOwnerName();
        return withName;
    }

    public int getManagedProfileBadge(int userId) {
        int i;
        checkManageOrInteractPermIfCallerInOtherProfileGroup(userId, "getManagedProfileBadge");
        synchronized (this.mUsersLock) {
            UserInfo userInfo = getUserInfoLU(userId);
            i = userInfo != null ? userInfo.profileBadge : 0;
        }
        return i;
    }

    public boolean isManagedProfile(int userId) {
        boolean z;
        checkManageOrInteractPermIfCallerInOtherProfileGroup(userId, "isManagedProfile");
        synchronized (this.mUsersLock) {
            UserInfo userInfo = getUserInfoLU(userId);
            z = userInfo != null && userInfo.isManagedProfile();
        }
        return z;
    }

    public boolean isUserUnlockingOrUnlocked(int userId) {
        checkManageOrInteractPermIfCallerInOtherProfileGroup(userId, "isUserUnlockingOrUnlocked");
        return this.mLocalService.isUserUnlockingOrUnlocked(userId);
    }

    public boolean isUserUnlocked(int userId) {
        checkManageOrInteractPermIfCallerInOtherProfileGroup(userId, "isUserUnlocked");
        return this.mLocalService.isUserUnlocked(userId);
    }

    public boolean isUserRunning(int userId) {
        checkManageOrInteractPermIfCallerInOtherProfileGroup(userId, "isUserRunning");
        return this.mLocalService.isUserRunning(userId);
    }

    public String getUserName() {
        String str;
        if (hasManageUsersOrPermission("android.permission.GET_ACCOUNTS_PRIVILEGED")) {
            int userId = UserHandle.getUserId(Binder.getCallingUid());
            synchronized (this.mUsersLock) {
                UserInfo userInfo = userWithName(getUserInfoLU(userId));
                str = userInfo == null ? "" : userInfo.name;
            }
            return str;
        }
        throw new SecurityException("You need MANAGE_USERS or GET_ACCOUNTS_PRIVILEGED permissions to: get user name");
    }

    public long getUserStartRealtime() {
        int userId = UserHandle.getUserId(Binder.getCallingUid());
        synchronized (this.mUsersLock) {
            UserData user = getUserDataLU(userId);
            if (user == null) {
                return 0;
            }
            long j = user.startRealtime;
            return j;
        }
    }

    public long getUserUnlockRealtime() {
        synchronized (this.mUsersLock) {
            UserData user = getUserDataLU(UserHandle.getUserId(Binder.getCallingUid()));
            if (user == null) {
                return 0;
            }
            long j = user.unlockRealtime;
            return j;
        }
    }

    private void checkManageOrInteractPermIfCallerInOtherProfileGroup(int userId, String name) {
        int callingUserId = UserHandle.getCallingUserId();
        if (callingUserId != userId && !isSameProfileGroupNoChecks(callingUserId, userId) && !hasManageUsersPermission() && !hasPermissionGranted("android.permission.INTERACT_ACROSS_USERS", Binder.getCallingUid())) {
            throw new SecurityException("You need INTERACT_ACROSS_USERS or MANAGE_USERS permission to: check " + name);
        }
    }

    public boolean isDemoUser(int userId) {
        boolean z;
        if (UserHandle.getCallingUserId() == userId || hasManageUsersPermission()) {
            synchronized (this.mUsersLock) {
                UserInfo userInfo = getUserInfoLU(userId);
                z = userInfo != null && userInfo.isDemo();
            }
            return z;
        }
        throw new SecurityException("You need MANAGE_USERS permission to query if u=" + userId + " is a demo user");
    }

    public boolean isRestricted() {
        boolean isRestricted;
        synchronized (this.mUsersLock) {
            isRestricted = getUserInfoLU(UserHandle.getCallingUserId()).isRestricted();
        }
        return isRestricted;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:18:0x002c, code lost:
        return r2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:20:0x002e, code lost:
        return false;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean canHaveRestrictedProfile(int r5) {
        /*
            r4 = this;
            java.lang.String r0 = "canHaveRestrictedProfile"
            checkManageUsersPermission(r0)
            java.lang.Object r0 = r4.mUsersLock
            monitor-enter(r0)
            android.content.pm.UserInfo r1 = r4.getUserInfoLU(r5)     // Catch:{ all -> 0x002f }
            r2 = 0
            if (r1 == 0) goto L_0x002d
            boolean r3 = r1.canHaveProfile()     // Catch:{ all -> 0x002f }
            if (r3 != 0) goto L_0x0016
            goto L_0x002d
        L_0x0016:
            boolean r3 = r1.isAdmin()     // Catch:{ all -> 0x002f }
            if (r3 != 0) goto L_0x001e
            monitor-exit(r0)     // Catch:{ all -> 0x002f }
            return r2
        L_0x001e:
            boolean r3 = r4.mIsDeviceManaged     // Catch:{ all -> 0x002f }
            if (r3 != 0) goto L_0x002b
            android.util.SparseBooleanArray r3 = r4.mIsUserManaged     // Catch:{ all -> 0x002f }
            boolean r3 = r3.get(r5)     // Catch:{ all -> 0x002f }
            if (r3 != 0) goto L_0x002b
            r2 = 1
        L_0x002b:
            monitor-exit(r0)     // Catch:{ all -> 0x002f }
            return r2
        L_0x002d:
            monitor-exit(r0)     // Catch:{ all -> 0x002f }
            return r2
        L_0x002f:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x002f }
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.pm.UserManagerService.canHaveRestrictedProfile(int):boolean");
    }

    public boolean hasRestrictedProfiles() {
        checkManageUsersPermission("hasRestrictedProfiles");
        int callingUserId = UserHandle.getCallingUserId();
        synchronized (this.mUsersLock) {
            int userSize = this.mUsers.size();
            for (int i = 0; i < userSize; i++) {
                UserInfo profile = this.mUsers.valueAt(i).info;
                if (callingUserId != profile.id && profile.restrictedProfileParentId == callingUserId) {
                    return true;
                }
            }
            return false;
        }
    }

    /* access modifiers changed from: private */
    @GuardedBy({"mUsersLock"})
    public UserInfo getUserInfoLU(int userId) {
        UserData userData = this.mUsers.get(userId);
        if (userData != null && userData.info.partial && !this.mRemovingUserIds.get(userId)) {
            Slog.w(LOG_TAG, "getUserInfo: unknown user #" + userId);
            return null;
        } else if (userData != null) {
            return userData.info;
        } else {
            return null;
        }
    }

    /* access modifiers changed from: private */
    @GuardedBy({"mUsersLock"})
    public UserData getUserDataLU(int userId) {
        UserData userData = this.mUsers.get(userId);
        if (userData == null || !userData.info.partial || this.mRemovingUserIds.get(userId)) {
            return userData;
        }
        return null;
    }

    /* access modifiers changed from: private */
    public UserInfo getUserInfoNoChecks(int userId) {
        UserInfo userInfo;
        synchronized (this.mUsersLock) {
            UserData userData = this.mUsers.get(userId);
            userInfo = userData != null ? userData.info : null;
        }
        return userInfo;
    }

    /* access modifiers changed from: private */
    public UserData getUserDataNoChecks(int userId) {
        UserData userData;
        synchronized (this.mUsersLock) {
            userData = this.mUsers.get(userId);
        }
        return userData;
    }

    public boolean exists(int userId) {
        return this.mLocalService.exists(userId);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:13:0x002c, code lost:
        if (r0 == false) goto L_?;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:14:0x002e, code lost:
        r1 = android.os.Binder.clearCallingIdentity();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:16:?, code lost:
        sendUserInfoChangedBroadcast(r7);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:18:0x0039, code lost:
        r3 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:19:0x003a, code lost:
        android.os.Binder.restoreCallingIdentity(r1);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:20:0x003d, code lost:
        throw r3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:29:?, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:30:?, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void setUserName(int r7, java.lang.String r8) {
        /*
            r6 = this;
            java.lang.String r0 = "rename users"
            checkManageUsersPermission(r0)
            r0 = 0
            java.lang.Object r1 = r6.mPackagesLock
            monitor-enter(r1)
            com.android.server.pm.UserManagerService$UserData r2 = r6.getUserDataNoChecks(r7)     // Catch:{ all -> 0x0058 }
            if (r2 == 0) goto L_0x003f
            android.content.pm.UserInfo r3 = r2.info     // Catch:{ all -> 0x0058 }
            boolean r3 = r3.partial     // Catch:{ all -> 0x0058 }
            if (r3 == 0) goto L_0x0017
            goto L_0x003f
        L_0x0017:
            if (r8 == 0) goto L_0x002b
            android.content.pm.UserInfo r3 = r2.info     // Catch:{ all -> 0x0058 }
            java.lang.String r3 = r3.name     // Catch:{ all -> 0x0058 }
            boolean r3 = r8.equals(r3)     // Catch:{ all -> 0x0058 }
            if (r3 != 0) goto L_0x002b
            android.content.pm.UserInfo r3 = r2.info     // Catch:{ all -> 0x0058 }
            r3.name = r8     // Catch:{ all -> 0x0058 }
            r6.writeUserLP(r2)     // Catch:{ all -> 0x0058 }
            r0 = 1
        L_0x002b:
            monitor-exit(r1)     // Catch:{ all -> 0x0058 }
            if (r0 == 0) goto L_0x003e
            long r1 = android.os.Binder.clearCallingIdentity()
            r6.sendUserInfoChangedBroadcast(r7)     // Catch:{ all -> 0x0039 }
            android.os.Binder.restoreCallingIdentity(r1)
            goto L_0x003e
        L_0x0039:
            r3 = move-exception
            android.os.Binder.restoreCallingIdentity(r1)
            throw r3
        L_0x003e:
            return
        L_0x003f:
            java.lang.String r3 = "UserManagerService"
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ all -> 0x0058 }
            r4.<init>()     // Catch:{ all -> 0x0058 }
            java.lang.String r5 = "setUserName: unknown user #"
            r4.append(r5)     // Catch:{ all -> 0x0058 }
            r4.append(r7)     // Catch:{ all -> 0x0058 }
            java.lang.String r4 = r4.toString()     // Catch:{ all -> 0x0058 }
            android.util.Slog.w(r3, r4)     // Catch:{ all -> 0x0058 }
            monitor-exit(r1)     // Catch:{ all -> 0x0058 }
            return
        L_0x0058:
            r2 = move-exception
            monitor-exit(r1)     // Catch:{ all -> 0x0058 }
            throw r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.pm.UserManagerService.setUserName(int, java.lang.String):void");
    }

    public void setUserIcon(int userId, Bitmap bitmap) {
        checkManageUsersPermission("update users");
        if (hasUserRestriction("no_set_user_icon", userId)) {
            Log.w(LOG_TAG, "Cannot set user icon. DISALLOW_SET_USER_ICON is enabled.");
        } else {
            this.mLocalService.setUserIcon(userId, bitmap);
        }
    }

    /* access modifiers changed from: private */
    public void sendUserInfoChangedBroadcast(int userId) {
        Intent changedIntent = new Intent("android.intent.action.USER_INFO_CHANGED");
        changedIntent.putExtra("android.intent.extra.user_handle", userId);
        changedIntent.addFlags(1073741824);
        this.mContext.sendBroadcastAsUser(changedIntent, UserHandle.ALL);
    }

    public ParcelFileDescriptor getUserIcon(int targetUserId) {
        if (hasManageUsersOrPermission("android.permission.GET_ACCOUNTS_PRIVILEGED")) {
            synchronized (this.mPackagesLock) {
                UserInfo targetUserInfo = getUserInfoNoChecks(targetUserId);
                if (targetUserInfo != null) {
                    if (!targetUserInfo.partial) {
                        int callingUserId = UserHandle.getCallingUserId();
                        int callingGroupId = getUserInfoNoChecks(callingUserId).profileGroupId;
                        boolean sameGroup = callingGroupId != -10000 && callingGroupId == targetUserInfo.profileGroupId;
                        if (callingUserId != targetUserId && !sameGroup) {
                            checkManageUsersPermission("get the icon of a user who is not related");
                        }
                        if (targetUserInfo.iconPath == null) {
                            return null;
                        }
                        UserInfo targetUserInfo2 = targetUserInfo.iconPath;
                        try {
                            return ParcelFileDescriptor.open(new File(targetUserInfo2), 268435456);
                        } catch (FileNotFoundException e) {
                            Log.e(LOG_TAG, "Couldn't find icon file", e);
                            return null;
                        }
                    }
                }
                Slog.w(LOG_TAG, "getUserIcon: unknown user #" + targetUserId);
                return null;
            }
        }
        throw new SecurityException("You need MANAGE_USERS or GET_ACCOUNTS_PRIVILEGED permissions to: get user icon");
    }

    /* JADX WARNING: Code restructure failed: missing block: B:12:0x002d, code lost:
        if (r0 == false) goto L_?;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:13:0x002f, code lost:
        scheduleWriteUser(r2);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:22:?, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:23:?, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void makeInitialized(int r7) {
        /*
            r6 = this;
            java.lang.String r0 = "makeInitialized"
            checkManageUsersPermission(r0)
            r0 = 0
            java.lang.Object r1 = r6.mUsersLock
            monitor-enter(r1)
            android.util.SparseArray<com.android.server.pm.UserManagerService$UserData> r2 = r6.mUsers     // Catch:{ all -> 0x004c }
            java.lang.Object r2 = r2.get(r7)     // Catch:{ all -> 0x004c }
            com.android.server.pm.UserManagerService$UserData r2 = (com.android.server.pm.UserManagerService.UserData) r2     // Catch:{ all -> 0x004c }
            if (r2 == 0) goto L_0x0033
            android.content.pm.UserInfo r3 = r2.info     // Catch:{ all -> 0x004c }
            boolean r3 = r3.partial     // Catch:{ all -> 0x004c }
            if (r3 == 0) goto L_0x001b
            goto L_0x0033
        L_0x001b:
            android.content.pm.UserInfo r3 = r2.info     // Catch:{ all -> 0x004c }
            int r3 = r3.flags     // Catch:{ all -> 0x004c }
            r3 = r3 & 16
            if (r3 != 0) goto L_0x002c
            android.content.pm.UserInfo r3 = r2.info     // Catch:{ all -> 0x004c }
            int r4 = r3.flags     // Catch:{ all -> 0x004c }
            r4 = r4 | 16
            r3.flags = r4     // Catch:{ all -> 0x004c }
            r0 = 1
        L_0x002c:
            monitor-exit(r1)     // Catch:{ all -> 0x004c }
            if (r0 == 0) goto L_0x0032
            r6.scheduleWriteUser(r2)
        L_0x0032:
            return
        L_0x0033:
            java.lang.String r3 = "UserManagerService"
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ all -> 0x004c }
            r4.<init>()     // Catch:{ all -> 0x004c }
            java.lang.String r5 = "makeInitialized: unknown user #"
            r4.append(r5)     // Catch:{ all -> 0x004c }
            r4.append(r7)     // Catch:{ all -> 0x004c }
            java.lang.String r4 = r4.toString()     // Catch:{ all -> 0x004c }
            android.util.Slog.w(r3, r4)     // Catch:{ all -> 0x004c }
            monitor-exit(r1)     // Catch:{ all -> 0x004c }
            return
        L_0x004c:
            r2 = move-exception
            monitor-exit(r1)     // Catch:{ all -> 0x004c }
            throw r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.pm.UserManagerService.makeInitialized(int):void");
    }

    private void initDefaultGuestRestrictions() {
        synchronized (this.mGuestRestrictions) {
            if (this.mGuestRestrictions.isEmpty()) {
                this.mGuestRestrictions.putBoolean("no_config_wifi", true);
                this.mGuestRestrictions.putBoolean("no_install_unknown_sources", true);
                this.mGuestRestrictions.putBoolean("no_outgoing_calls", true);
                this.mGuestRestrictions.putBoolean("no_sms", true);
            }
        }
    }

    public Bundle getDefaultGuestRestrictions() {
        Bundle bundle;
        checkManageUsersPermission("getDefaultGuestRestrictions");
        synchronized (this.mGuestRestrictions) {
            bundle = new Bundle(this.mGuestRestrictions);
        }
        return bundle;
    }

    public void setDefaultGuestRestrictions(Bundle restrictions) {
        checkManageUsersPermission("setDefaultGuestRestrictions");
        synchronized (this.mGuestRestrictions) {
            this.mGuestRestrictions.clear();
            this.mGuestRestrictions.putAll(restrictions);
        }
        synchronized (this.mPackagesLock) {
            writeUserListLP();
        }
    }

    /* access modifiers changed from: private */
    public void setDevicePolicyUserRestrictionsInner(int userId, Bundle restrictions, boolean isDeviceOwner, int cameraRestrictionScope) {
        boolean globalChanged;
        boolean localChanged;
        Bundle global = new Bundle();
        Bundle local = new Bundle();
        UserRestrictionsUtils.sortToGlobalAndLocal(restrictions, isDeviceOwner, cameraRestrictionScope, global, local);
        synchronized (this.mRestrictionsLock) {
            globalChanged = updateRestrictionsIfNeededLR(userId, global, this.mDevicePolicyGlobalUserRestrictions);
            localChanged = updateRestrictionsIfNeededLR(userId, local, this.mDevicePolicyLocalUserRestrictions);
            if (isDeviceOwner) {
                this.mDeviceOwnerUserId = userId;
            } else if (this.mDeviceOwnerUserId == userId) {
                this.mDeviceOwnerUserId = ScreenRotationAnimationInjector.BLACK_SURFACE_INVALID_POSITION;
            }
        }
        synchronized (this.mPackagesLock) {
            if (localChanged || globalChanged) {
                writeUserLP(getUserDataNoChecks(userId));
            }
        }
        synchronized (this.mRestrictionsLock) {
            if (globalChanged) {
                try {
                    applyUserRestrictionsForAllUsersLR();
                } catch (Throwable th) {
                    throw th;
                }
            } else if (localChanged) {
                applyUserRestrictionsLR(userId);
            }
        }
    }

    /* access modifiers changed from: private */
    public boolean updateRestrictionsIfNeededLR(int userId, Bundle restrictions, SparseArray<Bundle> restrictionsArray) {
        boolean changed = !UserRestrictionsUtils.areEqual(restrictionsArray.get(userId), restrictions);
        if (changed) {
            if (!UserRestrictionsUtils.isEmpty(restrictions)) {
                restrictionsArray.put(userId, restrictions);
            } else {
                restrictionsArray.delete(userId);
            }
        }
        return changed;
    }

    @GuardedBy({"mRestrictionsLock"})
    private Bundle computeEffectiveUserRestrictionsLR(int userId) {
        Bundle baseRestrictions = UserRestrictionsUtils.nonNull(this.mBaseUserRestrictions.get(userId));
        Bundle global = UserRestrictionsUtils.mergeAll(this.mDevicePolicyGlobalUserRestrictions);
        Bundle local = this.mDevicePolicyLocalUserRestrictions.get(userId);
        if (UserRestrictionsUtils.isEmpty(global) && UserRestrictionsUtils.isEmpty(local)) {
            return baseRestrictions;
        }
        Bundle effective = UserRestrictionsUtils.clone(baseRestrictions);
        UserRestrictionsUtils.merge(effective, global);
        UserRestrictionsUtils.merge(effective, local);
        return effective;
    }

    /* access modifiers changed from: private */
    @GuardedBy({"mRestrictionsLock"})
    public void invalidateEffectiveUserRestrictionsLR(int userId) {
        this.mCachedEffectiveUserRestrictions.remove(userId);
    }

    private Bundle getEffectiveUserRestrictions(int userId) {
        Bundle restrictions;
        synchronized (this.mRestrictionsLock) {
            restrictions = this.mCachedEffectiveUserRestrictions.get(userId);
            if (restrictions == null) {
                restrictions = computeEffectiveUserRestrictionsLR(userId);
                this.mCachedEffectiveUserRestrictions.put(userId, restrictions);
            }
        }
        return restrictions;
    }

    public boolean hasUserRestriction(String restrictionKey, int userId) {
        Bundle restrictions;
        if (UserRestrictionsUtils.isValidRestriction(restrictionKey) && (restrictions = getEffectiveUserRestrictions(userId)) != null && restrictions.getBoolean(restrictionKey)) {
            return true;
        }
        return false;
    }

    public boolean hasUserRestrictionOnAnyUser(String restrictionKey) {
        if (!UserRestrictionsUtils.isValidRestriction(restrictionKey)) {
            return false;
        }
        List<UserInfo> users = getUsers(true);
        for (int i = 0; i < users.size(); i++) {
            Bundle restrictions = getEffectiveUserRestrictions(users.get(i).id);
            if (restrictions != null && restrictions.getBoolean(restrictionKey)) {
                return true;
            }
        }
        return false;
    }

    public int getUserRestrictionSource(String restrictionKey, int userId) {
        List<UserManager.EnforcingUser> enforcingUsers = getUserRestrictionSources(restrictionKey, userId);
        int result = 0;
        for (int i = enforcingUsers.size() - 1; i >= 0; i--) {
            result |= enforcingUsers.get(i).getUserRestrictionSource();
        }
        return result;
    }

    public List<UserManager.EnforcingUser> getUserRestrictionSources(String restrictionKey, int userId) {
        checkManageUsersPermission("getUserRestrictionSource");
        if (!hasUserRestriction(restrictionKey, userId)) {
            return Collections.emptyList();
        }
        List<UserManager.EnforcingUser> result = new ArrayList<>();
        if (hasBaseUserRestriction(restrictionKey, userId)) {
            result.add(new UserManager.EnforcingUser(ScreenRotationAnimationInjector.BLACK_SURFACE_INVALID_POSITION, 1));
        }
        synchronized (this.mRestrictionsLock) {
            if (UserRestrictionsUtils.contains(this.mDevicePolicyLocalUserRestrictions.get(userId), restrictionKey)) {
                result.add(getEnforcingUserLocked(userId));
            }
            for (int i = this.mDevicePolicyGlobalUserRestrictions.size() - 1; i >= 0; i--) {
                int profileUserId = this.mDevicePolicyGlobalUserRestrictions.keyAt(i);
                if (UserRestrictionsUtils.contains(this.mDevicePolicyGlobalUserRestrictions.valueAt(i), restrictionKey)) {
                    result.add(getEnforcingUserLocked(profileUserId));
                }
            }
        }
        return result;
    }

    @GuardedBy({"mRestrictionsLock"})
    private UserManager.EnforcingUser getEnforcingUserLocked(int userId) {
        int source;
        if (this.mDeviceOwnerUserId == userId) {
            source = 2;
        } else {
            source = 4;
        }
        return new UserManager.EnforcingUser(userId, source);
    }

    public Bundle getUserRestrictions(int userId) {
        return UserRestrictionsUtils.clone(getEffectiveUserRestrictions(userId));
    }

    public boolean hasBaseUserRestriction(String restrictionKey, int userId) {
        checkManageUsersPermission("hasBaseUserRestriction");
        boolean z = false;
        if (!UserRestrictionsUtils.isValidRestriction(restrictionKey)) {
            return false;
        }
        synchronized (this.mRestrictionsLock) {
            Bundle bundle = this.mBaseUserRestrictions.get(userId);
            if (bundle != null && bundle.getBoolean(restrictionKey, false)) {
                z = true;
            }
        }
        return z;
    }

    public void setUserRestriction(String key, boolean value, int userId) {
        checkManageUsersPermission("setUserRestriction");
        if (UserRestrictionsUtils.isValidRestriction(key)) {
            synchronized (this.mRestrictionsLock) {
                Bundle newRestrictions = UserRestrictionsUtils.clone(this.mBaseUserRestrictions.get(userId));
                newRestrictions.putBoolean(key, value);
                updateUserRestrictionsInternalLR(newRestrictions, userId);
            }
        }
    }

    @GuardedBy({"mRestrictionsLock"})
    private void updateUserRestrictionsInternalLR(Bundle newBaseRestrictions, final int userId) {
        Bundle prevAppliedRestrictions = UserRestrictionsUtils.nonNull(this.mAppliedUserRestrictions.get(userId));
        if (newBaseRestrictions != null) {
            boolean z = true;
            Preconditions.checkState(this.mBaseUserRestrictions.get(userId) != newBaseRestrictions);
            if (this.mCachedEffectiveUserRestrictions.get(userId) == newBaseRestrictions) {
                z = false;
            }
            Preconditions.checkState(z);
            if (updateRestrictionsIfNeededLR(userId, newBaseRestrictions, this.mBaseUserRestrictions)) {
                scheduleWriteUser(getUserDataNoChecks(userId));
            }
        }
        final Bundle effective = computeEffectiveUserRestrictionsLR(userId);
        this.mCachedEffectiveUserRestrictions.put(userId, effective);
        if (this.mAppOpsService != null) {
            this.mHandler.post(new Runnable() {
                public void run() {
                    try {
                        UserManagerService.this.mAppOpsService.setUserRestrictions(effective, UserManagerService.mUserRestriconToken, userId);
                    } catch (RemoteException e) {
                        Log.w(UserManagerService.LOG_TAG, "Unable to notify AppOpsService of UserRestrictions");
                    }
                }
            });
        }
        propagateUserRestrictionsLR(userId, effective, prevAppliedRestrictions);
        this.mAppliedUserRestrictions.put(userId, new Bundle(effective));
    }

    private void propagateUserRestrictionsLR(final int userId, Bundle newRestrictions, Bundle prevRestrictions) {
        if (!UserRestrictionsUtils.areEqual(newRestrictions, prevRestrictions)) {
            final Bundle newRestrictionsFinal = new Bundle(newRestrictions);
            final Bundle prevRestrictionsFinal = new Bundle(prevRestrictions);
            this.mHandler.post(new Runnable() {
                public void run() {
                    UserManagerInternal.UserRestrictionsListener[] listeners;
                    UserRestrictionsUtils.applyUserRestrictions(UserManagerService.this.mContext, userId, newRestrictionsFinal, prevRestrictionsFinal);
                    synchronized (UserManagerService.this.mUserRestrictionsListeners) {
                        listeners = new UserManagerInternal.UserRestrictionsListener[UserManagerService.this.mUserRestrictionsListeners.size()];
                        UserManagerService.this.mUserRestrictionsListeners.toArray(listeners);
                    }
                    for (UserManagerInternal.UserRestrictionsListener onUserRestrictionsChanged : listeners) {
                        onUserRestrictionsChanged.onUserRestrictionsChanged(userId, newRestrictionsFinal, prevRestrictionsFinal);
                    }
                    UserManagerService.this.mContext.sendBroadcastAsUser(new Intent("android.os.action.USER_RESTRICTIONS_CHANGED").setFlags(1073741824), UserHandle.of(userId));
                }
            });
        }
    }

    /* access modifiers changed from: package-private */
    @GuardedBy({"mRestrictionsLock"})
    public void applyUserRestrictionsLR(int userId) {
        updateUserRestrictionsInternalLR((Bundle) null, userId);
    }

    /* access modifiers changed from: package-private */
    @GuardedBy({"mRestrictionsLock"})
    public void applyUserRestrictionsForAllUsersLR() {
        this.mCachedEffectiveUserRestrictions.clear();
        this.mHandler.post(new Runnable() {
            public void run() {
                try {
                    int[] runningUsers = ActivityManager.getService().getRunningUserIds();
                    synchronized (UserManagerService.this.mRestrictionsLock) {
                        for (int applyUserRestrictionsLR : runningUsers) {
                            UserManagerService.this.applyUserRestrictionsLR(applyUserRestrictionsLR);
                        }
                    }
                } catch (RemoteException e) {
                    Log.w(UserManagerService.LOG_TAG, "Unable to access ActivityManagerService");
                }
            }
        });
    }

    private boolean isUserLimitReached() {
        int count;
        synchronized (this.mUsersLock) {
            count = getAliveUsersExcludingGuestsCountLU();
        }
        return count >= UserManager.getMaxSupportedUsers();
    }

    /* JADX WARNING: Code restructure failed: missing block: B:30:0x005f, code lost:
        return r1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:32:0x0061, code lost:
        return false;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean canAddMoreManagedProfiles(int r10, boolean r11) {
        /*
            r9 = this;
            java.lang.String r0 = "check if more managed profiles can be added."
            checkManageUsersPermission(r0)
            boolean r0 = android.app.ActivityManager.isLowRamDeviceStatic()
            r1 = 0
            if (r0 == 0) goto L_0x000d
            return r1
        L_0x000d:
            android.content.Context r0 = r9.mContext
            android.content.pm.PackageManager r0 = r0.getPackageManager()
            java.lang.String r2 = "android.software.managed_users"
            boolean r0 = r0.hasSystemFeature(r2)
            if (r0 != 0) goto L_0x001c
            return r1
        L_0x001c:
            android.util.IntArray r0 = r9.getProfileIdsLU(r10, r1)
            int r2 = r0.size()
            r3 = 1
            int r2 = r2 - r3
            r4 = 999(0x3e7, float:1.4E-42)
            int r4 = r0.indexOf(r4)
            if (r4 < 0) goto L_0x0030
            int r2 = r2 + -1
        L_0x0030:
            if (r2 <= 0) goto L_0x0036
            if (r11 == 0) goto L_0x0036
            r4 = r3
            goto L_0x0037
        L_0x0036:
            r4 = r1
        L_0x0037:
            int r5 = r2 - r4
            int r6 = getMaxManagedProfiles()
            if (r5 < r6) goto L_0x0040
            return r1
        L_0x0040:
            java.lang.Object r5 = r9.mUsersLock
            monitor-enter(r5)
            android.content.pm.UserInfo r6 = r9.getUserInfoLU(r10)     // Catch:{ all -> 0x0062 }
            if (r6 == 0) goto L_0x0060
            boolean r7 = r6.canHaveProfile()     // Catch:{ all -> 0x0062 }
            if (r7 != 0) goto L_0x0050
            goto L_0x0060
        L_0x0050:
            int r7 = r9.getAliveUsersExcludingGuestsCountLU()     // Catch:{ all -> 0x0062 }
            int r7 = r7 - r4
            if (r7 == r3) goto L_0x005d
            int r8 = android.os.UserManager.getMaxSupportedUsers()     // Catch:{ all -> 0x0062 }
            if (r7 >= r8) goto L_0x005e
        L_0x005d:
            r1 = r3
        L_0x005e:
            monitor-exit(r5)     // Catch:{ all -> 0x0062 }
            return r1
        L_0x0060:
            monitor-exit(r5)     // Catch:{ all -> 0x0062 }
            return r1
        L_0x0062:
            r1 = move-exception
            monitor-exit(r5)     // Catch:{ all -> 0x0062 }
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.pm.UserManagerService.canAddMoreManagedProfiles(int, boolean):boolean");
    }

    @GuardedBy({"mUsersLock"})
    private int getAliveUsersExcludingGuestsCountLU() {
        int aliveUserCount = 0;
        int totalUserCount = this.mUsers.size();
        for (int i = 0; i < totalUserCount; i++) {
            UserInfo user = this.mUsers.valueAt(i).info;
            if (!this.mRemovingUserIds.get(user.id) && !user.isGuest()) {
                aliveUserCount++;
            }
        }
        return aliveUserCount;
    }

    private static final void checkManageUserAndAcrossUsersFullPermission(String message) {
        int uid = Binder.getCallingUid();
        if (uid != 1000 && uid != 0) {
            if (!hasPermissionGranted("android.permission.MANAGE_USERS", uid) || !hasPermissionGranted("android.permission.INTERACT_ACROSS_USERS_FULL", uid)) {
                throw new SecurityException("You need MANAGE_USERS and INTERACT_ACROSS_USERS_FULL permission to: " + message);
            }
        }
    }

    private static boolean hasPermissionGranted(String permission, int uid) {
        return ActivityManager.checkComponentPermission(permission, uid, -1, true) == 0;
    }

    private static final void checkManageUsersPermission(String message) {
        if (!hasManageUsersPermission()) {
            throw new SecurityException("You need MANAGE_USERS permission to: " + message);
        }
    }

    private static final void checkManageOrCreateUsersPermission(String message) {
        if (!hasManageOrCreateUsersPermission()) {
            throw new SecurityException("You either need MANAGE_USERS or CREATE_USERS permission to: " + message);
        }
    }

    private static final void checkManageOrCreateUsersPermission(int creationFlags) {
        if ((creationFlags & -813) == 0) {
            if (!hasManageOrCreateUsersPermission()) {
                throw new SecurityException("You either need MANAGE_USERS or CREATE_USERS permission to create an user with flags: " + creationFlags);
            }
        } else if (!hasManageUsersPermission()) {
            throw new SecurityException("You need MANAGE_USERS permission to create an user  with flags: " + creationFlags);
        }
    }

    private static final boolean hasManageUsersPermission() {
        int callingUid = Binder.getCallingUid();
        return UserHandle.isSameApp(callingUid, 1000) || callingUid == 0 || hasPermissionGranted("android.permission.MANAGE_USERS", callingUid);
    }

    private static final boolean hasManageUsersOrPermission(String alternativePermission) {
        int callingUid = Binder.getCallingUid();
        return UserHandle.isSameApp(callingUid, 1000) || callingUid == 0 || hasPermissionGranted("android.permission.MANAGE_USERS", callingUid) || hasPermissionGranted(alternativePermission, callingUid);
    }

    private static final boolean hasManageOrCreateUsersPermission() {
        return hasManageUsersOrPermission("android.permission.CREATE_USERS");
    }

    private static void checkSystemOrRoot(String message) {
        int uid = Binder.getCallingUid();
        if (!UserHandle.isSameApp(uid, 1000) && uid != 0) {
            throw new SecurityException("Only system may: " + message);
        }
    }

    /* access modifiers changed from: private */
    public void writeBitmapLP(UserInfo info, Bitmap bitmap) {
        try {
            File dir = new File(this.mUsersDir, Integer.toString(info.id));
            File file = new File(dir, USER_PHOTO_FILENAME);
            File tmp = new File(dir, USER_PHOTO_FILENAME_TMP);
            if (!dir.exists()) {
                dir.mkdir();
                FileUtils.setPermissions(dir.getPath(), 505, -1, -1);
            }
            Bitmap.CompressFormat compressFormat = Bitmap.CompressFormat.PNG;
            FileOutputStream fileOutputStream = new FileOutputStream(tmp);
            FileOutputStream os = fileOutputStream;
            if (bitmap.compress(compressFormat, 100, fileOutputStream) && tmp.renameTo(file) && SELinux.restorecon(file)) {
                info.iconPath = file.getAbsolutePath();
            }
            try {
                os.close();
            } catch (IOException e) {
            }
            tmp.delete();
        } catch (FileNotFoundException e2) {
            Slog.w(LOG_TAG, "Error setting photo for user ", e2);
        }
    }

    public int[] getUserIds() {
        int[] iArr;
        synchronized (this.mUsersLock) {
            iArr = this.mUserIds;
        }
        return iArr;
    }

    /* Debug info: failed to restart local var, previous not found, register: 14 */
    /* JADX WARNING: Code restructure failed: missing block: B:56:0x00dd, code lost:
        if (r2.getName().equals(TAG_RESTRICTIONS) == false) goto L_0x011c;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:57:0x00df, code lost:
        r9 = r14.mGuestRestrictions;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:58:0x00e1, code lost:
        monitor-enter(r9);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:60:?, code lost:
        com.android.server.pm.UserRestrictionsUtils.readRestrictions(r2, r14.mGuestRestrictions);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:61:0x00e7, code lost:
        monitor-exit(r9);
     */
    /* JADX WARNING: Removed duplicated region for block: B:12:0x0034 A[Catch:{ IOException | XmlPullParserException -> 0x0127 }] */
    /* JADX WARNING: Removed duplicated region for block: B:15:0x0042  */
    @com.android.internal.annotations.GuardedBy({"mRestrictionsLock", "mPackagesLock"})
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void readUserListLP() {
        /*
            r14 = this;
            java.io.File r0 = r14.mUserListFile
            boolean r0 = r0.exists()
            if (r0 != 0) goto L_0x000c
            r14.fallbackToSingleUserLP()
            return
        L_0x000c:
            r0 = 0
            android.util.AtomicFile r1 = new android.util.AtomicFile
            java.io.File r2 = r14.mUserListFile
            r1.<init>(r2)
            java.io.FileInputStream r2 = r1.openRead()     // Catch:{ IOException | XmlPullParserException -> 0x0127 }
            r0 = r2
            org.xmlpull.v1.XmlPullParser r2 = android.util.Xml.newPullParser()     // Catch:{ IOException | XmlPullParserException -> 0x0127 }
            java.nio.charset.Charset r3 = java.nio.charset.StandardCharsets.UTF_8     // Catch:{ IOException | XmlPullParserException -> 0x0127 }
            java.lang.String r3 = r3.name()     // Catch:{ IOException | XmlPullParserException -> 0x0127 }
            r2.setInput(r0, r3)     // Catch:{ IOException | XmlPullParserException -> 0x0127 }
        L_0x0026:
            int r3 = r2.next()     // Catch:{ IOException | XmlPullParserException -> 0x0127 }
            r4 = r3
            r5 = 1
            r6 = 2
            if (r3 == r6) goto L_0x0032
            if (r4 == r5) goto L_0x0032
            goto L_0x0026
        L_0x0032:
            if (r4 == r6) goto L_0x0042
            java.lang.String r3 = "UserManagerService"
            java.lang.String r5 = "Unable to read user list"
            android.util.Slog.e(r3, r5)     // Catch:{ IOException | XmlPullParserException -> 0x0127 }
            r14.fallbackToSingleUserLP()     // Catch:{ IOException | XmlPullParserException -> 0x0127 }
            libcore.io.IoUtils.closeQuietly(r0)
            return
        L_0x0042:
            r3 = -1
            r14.mNextSerialNumber = r3     // Catch:{ IOException | XmlPullParserException -> 0x0127 }
            java.lang.String r3 = r2.getName()     // Catch:{ IOException | XmlPullParserException -> 0x0127 }
            java.lang.String r7 = "users"
            boolean r3 = r3.equals(r7)     // Catch:{ IOException | XmlPullParserException -> 0x0127 }
            r7 = 0
            if (r3 == 0) goto L_0x0071
            java.lang.String r3 = "nextSerialNumber"
            java.lang.String r3 = r2.getAttributeValue(r7, r3)     // Catch:{ IOException | XmlPullParserException -> 0x0127 }
            if (r3 == 0) goto L_0x0062
            int r8 = java.lang.Integer.parseInt(r3)     // Catch:{ IOException | XmlPullParserException -> 0x0127 }
            r14.mNextSerialNumber = r8     // Catch:{ IOException | XmlPullParserException -> 0x0127 }
        L_0x0062:
            java.lang.String r8 = "version"
            java.lang.String r8 = r2.getAttributeValue(r7, r8)     // Catch:{ IOException | XmlPullParserException -> 0x0127 }
            if (r8 == 0) goto L_0x0071
            int r9 = java.lang.Integer.parseInt(r8)     // Catch:{ IOException | XmlPullParserException -> 0x0127 }
            r14.mUserVersion = r9     // Catch:{ IOException | XmlPullParserException -> 0x0127 }
        L_0x0071:
            r3 = r7
        L_0x0072:
            int r8 = r2.next()     // Catch:{ IOException | XmlPullParserException -> 0x0127 }
            r4 = r8
            if (r8 == r5) goto L_0x011e
            if (r4 != r6) goto L_0x0072
            java.lang.String r8 = r2.getName()     // Catch:{ IOException | XmlPullParserException -> 0x0127 }
            java.lang.String r9 = "user"
            boolean r9 = r8.equals(r9)     // Catch:{ IOException | XmlPullParserException -> 0x0127 }
            if (r9 == 0) goto L_0x00be
            java.lang.String r9 = "id"
            java.lang.String r9 = r2.getAttributeValue(r7, r9)     // Catch:{ IOException | XmlPullParserException -> 0x0127 }
            int r10 = java.lang.Integer.parseInt(r9)     // Catch:{ IOException | XmlPullParserException -> 0x0127 }
            com.android.server.pm.UserManagerService$UserData r10 = r14.readUserLP(r10)     // Catch:{ IOException | XmlPullParserException -> 0x0127 }
            if (r10 == 0) goto L_0x00bd
            java.lang.Object r11 = r14.mUsersLock     // Catch:{ IOException | XmlPullParserException -> 0x0127 }
            monitor-enter(r11)     // Catch:{ IOException | XmlPullParserException -> 0x0127 }
            android.util.SparseArray<com.android.server.pm.UserManagerService$UserData> r12 = r14.mUsers     // Catch:{ all -> 0x00ba }
            android.content.pm.UserInfo r13 = r10.info     // Catch:{ all -> 0x00ba }
            int r13 = r13.id     // Catch:{ all -> 0x00ba }
            r12.put(r13, r10)     // Catch:{ all -> 0x00ba }
            int r12 = r14.mNextSerialNumber     // Catch:{ all -> 0x00ba }
            if (r12 < 0) goto L_0x00b1
            int r12 = r14.mNextSerialNumber     // Catch:{ all -> 0x00ba }
            android.content.pm.UserInfo r13 = r10.info     // Catch:{ all -> 0x00ba }
            int r13 = r13.id     // Catch:{ all -> 0x00ba }
            if (r12 > r13) goto L_0x00b8
        L_0x00b1:
            android.content.pm.UserInfo r12 = r10.info     // Catch:{ all -> 0x00ba }
            int r12 = r12.id     // Catch:{ all -> 0x00ba }
            int r12 = r12 + r5
            r14.mNextSerialNumber = r12     // Catch:{ all -> 0x00ba }
        L_0x00b8:
            monitor-exit(r11)     // Catch:{ all -> 0x00ba }
            goto L_0x00bd
        L_0x00ba:
            r5 = move-exception
            monitor-exit(r11)     // Catch:{ all -> 0x00ba }
            throw r5     // Catch:{ IOException | XmlPullParserException -> 0x0127 }
        L_0x00bd:
            goto L_0x011c
        L_0x00be:
            java.lang.String r9 = "guestRestrictions"
            boolean r9 = r8.equals(r9)     // Catch:{ IOException | XmlPullParserException -> 0x0127 }
            if (r9 == 0) goto L_0x00ec
        L_0x00c6:
            int r9 = r2.next()     // Catch:{ IOException | XmlPullParserException -> 0x0127 }
            r4 = r9
            if (r9 == r5) goto L_0x011c
            r9 = 3
            if (r4 == r9) goto L_0x011c
            if (r4 != r6) goto L_0x00c6
            java.lang.String r9 = r2.getName()     // Catch:{ IOException | XmlPullParserException -> 0x0127 }
            java.lang.String r10 = "restrictions"
            boolean r9 = r9.equals(r10)     // Catch:{ IOException | XmlPullParserException -> 0x0127 }
            if (r9 == 0) goto L_0x011c
            android.os.Bundle r9 = r14.mGuestRestrictions     // Catch:{ IOException | XmlPullParserException -> 0x0127 }
            monitor-enter(r9)     // Catch:{ IOException | XmlPullParserException -> 0x0127 }
            android.os.Bundle r10 = r14.mGuestRestrictions     // Catch:{ all -> 0x00e9 }
            com.android.server.pm.UserRestrictionsUtils.readRestrictions(r2, r10)     // Catch:{ all -> 0x00e9 }
            monitor-exit(r9)     // Catch:{ all -> 0x00e9 }
            goto L_0x011c
        L_0x00e9:
            r5 = move-exception
            monitor-exit(r9)     // Catch:{ all -> 0x00e9 }
            throw r5     // Catch:{ IOException | XmlPullParserException -> 0x0127 }
        L_0x00ec:
            java.lang.String r9 = "deviceOwnerUserId"
            boolean r9 = r8.equals(r9)     // Catch:{ IOException | XmlPullParserException -> 0x0127 }
            if (r9 != 0) goto L_0x010c
            java.lang.String r9 = "globalRestrictionOwnerUserId"
            boolean r9 = r8.equals(r9)     // Catch:{ IOException | XmlPullParserException -> 0x0127 }
            if (r9 == 0) goto L_0x00fd
            goto L_0x010c
        L_0x00fd:
            java.lang.String r9 = "device_policy_restrictions"
            boolean r9 = r8.equals(r9)     // Catch:{ IOException | XmlPullParserException -> 0x0127 }
            if (r9 == 0) goto L_0x011b
            android.os.Bundle r9 = com.android.server.pm.UserRestrictionsUtils.readRestrictions(r2)     // Catch:{ IOException | XmlPullParserException -> 0x0127 }
            r3 = r9
            goto L_0x011c
        L_0x010c:
            java.lang.String r9 = "id"
            java.lang.String r9 = r2.getAttributeValue(r7, r9)     // Catch:{ IOException | XmlPullParserException -> 0x0127 }
            if (r9 == 0) goto L_0x011b
            int r10 = java.lang.Integer.parseInt(r9)     // Catch:{ IOException | XmlPullParserException -> 0x0127 }
            r14.mDeviceOwnerUserId = r10     // Catch:{ IOException | XmlPullParserException -> 0x0127 }
        L_0x011b:
        L_0x011c:
            goto L_0x0072
        L_0x011e:
            r14.updateUserIds()     // Catch:{ IOException | XmlPullParserException -> 0x0127 }
            r14.upgradeIfNecessaryLP(r3)     // Catch:{ IOException | XmlPullParserException -> 0x0127 }
            goto L_0x012b
        L_0x0125:
            r2 = move-exception
            goto L_0x0130
        L_0x0127:
            r2 = move-exception
            r14.fallbackToSingleUserLP()     // Catch:{ all -> 0x0125 }
        L_0x012b:
            libcore.io.IoUtils.closeQuietly(r0)
            return
        L_0x0130:
            libcore.io.IoUtils.closeQuietly(r0)
            throw r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.pm.UserManagerService.readUserListLP():void");
    }

    @GuardedBy({"mRestrictionsLock", "mPackagesLock"})
    private void upgradeIfNecessaryLP(Bundle oldGlobalUserRestrictions) {
        int originalVersion = this.mUserVersion;
        int userVersion = this.mUserVersion;
        if (userVersion < 1) {
            UserData userData = getUserDataNoChecks(0);
            if ("Primary".equals(userData.info.name)) {
                userData.info.name = this.mContext.getResources().getString(17040604);
                scheduleWriteUser(userData);
            }
            userVersion = 1;
        }
        if (userVersion < 2) {
            UserData userData2 = getUserDataNoChecks(0);
            if ((userData2.info.flags & 16) == 0) {
                userData2.info.flags |= 16;
                scheduleWriteUser(userData2);
            }
            userVersion = 2;
        }
        if (userVersion < 4) {
            userVersion = 4;
        }
        if (userVersion < 5) {
            initDefaultGuestRestrictions();
            userVersion = 5;
        }
        if (userVersion < 6) {
            boolean splitSystemUser = UserManager.isSplitSystemUser();
            synchronized (this.mUsersLock) {
                for (int i = 0; i < this.mUsers.size(); i++) {
                    UserData userData3 = this.mUsers.valueAt(i);
                    if (!splitSystemUser && userData3.info.isRestricted() && userData3.info.restrictedProfileParentId == -10000) {
                        userData3.info.restrictedProfileParentId = 0;
                        scheduleWriteUser(userData3);
                    }
                }
            }
            userVersion = 6;
        }
        if (userVersion < 7) {
            synchronized (this.mRestrictionsLock) {
                if (!UserRestrictionsUtils.isEmpty(oldGlobalUserRestrictions) && this.mDeviceOwnerUserId != -10000) {
                    this.mDevicePolicyGlobalUserRestrictions.put(this.mDeviceOwnerUserId, oldGlobalUserRestrictions);
                }
                UserRestrictionsUtils.moveRestriction("ensure_verify_apps", this.mDevicePolicyLocalUserRestrictions, this.mDevicePolicyGlobalUserRestrictions);
            }
            userVersion = 7;
        }
        if (userVersion < 7) {
            Slog.w(LOG_TAG, "User version " + this.mUserVersion + " didn't upgrade as expected to " + 7);
            return;
        }
        this.mUserVersion = userVersion;
        if (originalVersion < this.mUserVersion) {
            writeUserListLP();
        }
    }

    @GuardedBy({"mPackagesLock", "mRestrictionsLock"})
    private void fallbackToSingleUserLP() {
        int flags = 16;
        if (!UserManager.isSplitSystemUser()) {
            flags = 16 | 3;
        }
        UserData userData = putUserInfo(new UserInfo(0, (String) null, (String) null, flags));
        this.mNextSerialNumber = 10;
        this.mUserVersion = 7;
        Bundle restrictions = new Bundle();
        try {
            for (String userRestriction : this.mContext.getResources().getStringArray(17236005)) {
                if (UserRestrictionsUtils.isValidRestriction(userRestriction)) {
                    restrictions.putBoolean(userRestriction, true);
                }
            }
        } catch (Resources.NotFoundException e) {
            Log.e(LOG_TAG, "Couldn't find resource: config_defaultFirstUserRestrictions", e);
        }
        if (!restrictions.isEmpty()) {
            synchronized (this.mRestrictionsLock) {
                this.mBaseUserRestrictions.append(0, restrictions);
            }
        }
        updateUserIds();
        initDefaultGuestRestrictions();
        writeUserLP(userData);
        writeUserListLP();
    }

    private String getOwnerName() {
        return this.mContext.getResources().getString(17040604);
    }

    private void scheduleWriteUser(UserData UserData2) {
        if (!this.mHandler.hasMessages(1, UserData2)) {
            this.mHandler.sendMessageDelayed(this.mHandler.obtainMessage(1, UserData2), 2000);
        }
    }

    /* access modifiers changed from: private */
    public void writeUserLP(UserData userData) {
        FileOutputStream fos = null;
        File file = this.mUsersDir;
        AtomicFile userFile = new AtomicFile(new File(file, userData.info.id + XML_SUFFIX));
        try {
            fos = userFile.startWrite();
            writeUserLP(userData, new BufferedOutputStream(fos));
            userFile.finishWrite(fos);
        } catch (Exception ioe) {
            Slog.e(LOG_TAG, "Error writing user info " + userData.info.id, ioe);
            userFile.failWrite(fos);
        }
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public void writeUserLP(UserData userData, OutputStream os) throws IOException, XmlPullParserException {
        XmlSerializer serializer = new FastXmlSerializer();
        serializer.setOutput(os, StandardCharsets.UTF_8.name());
        serializer.startDocument((String) null, true);
        serializer.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);
        UserInfo userInfo = userData.info;
        serializer.startTag((String) null, TAG_USER);
        serializer.attribute((String) null, ATTR_ID, Integer.toString(userInfo.id));
        serializer.attribute((String) null, ATTR_SERIAL_NO, Integer.toString(userInfo.serialNumber));
        serializer.attribute((String) null, ATTR_FLAGS, Integer.toString(userInfo.flags));
        serializer.attribute((String) null, ATTR_CREATION_TIME, Long.toString(userInfo.creationTime));
        serializer.attribute((String) null, ATTR_LAST_LOGGED_IN_TIME, Long.toString(userInfo.lastLoggedInTime));
        if (userInfo.lastLoggedInFingerprint != null) {
            serializer.attribute((String) null, ATTR_LAST_LOGGED_IN_FINGERPRINT, userInfo.lastLoggedInFingerprint);
        }
        if (userInfo.iconPath != null) {
            serializer.attribute((String) null, ATTR_ICON_PATH, userInfo.iconPath);
        }
        if (userInfo.partial) {
            serializer.attribute((String) null, ATTR_PARTIAL, "true");
        }
        if (userInfo.guestToRemove) {
            serializer.attribute((String) null, ATTR_GUEST_TO_REMOVE, "true");
        }
        if (userInfo.profileGroupId != -10000) {
            serializer.attribute((String) null, ATTR_PROFILE_GROUP_ID, Integer.toString(userInfo.profileGroupId));
        }
        serializer.attribute((String) null, ATTR_PROFILE_BADGE, Integer.toString(userInfo.profileBadge));
        if (userInfo.restrictedProfileParentId != -10000) {
            serializer.attribute((String) null, ATTR_RESTRICTED_PROFILE_PARENT_ID, Integer.toString(userInfo.restrictedProfileParentId));
        }
        if (userData.persistSeedData) {
            if (userData.seedAccountName != null) {
                serializer.attribute((String) null, ATTR_SEED_ACCOUNT_NAME, userData.seedAccountName);
            }
            if (userData.seedAccountType != null) {
                serializer.attribute((String) null, ATTR_SEED_ACCOUNT_TYPE, userData.seedAccountType);
            }
        }
        if (userInfo.name != null) {
            serializer.startTag((String) null, "name");
            serializer.text(userInfo.name);
            serializer.endTag((String) null, "name");
        }
        synchronized (this.mRestrictionsLock) {
            UserRestrictionsUtils.writeRestrictions(serializer, this.mBaseUserRestrictions.get(userInfo.id), TAG_RESTRICTIONS);
            UserRestrictionsUtils.writeRestrictions(serializer, this.mDevicePolicyLocalUserRestrictions.get(userInfo.id), TAG_DEVICE_POLICY_RESTRICTIONS);
            UserRestrictionsUtils.writeRestrictions(serializer, this.mDevicePolicyGlobalUserRestrictions.get(userInfo.id), TAG_DEVICE_POLICY_GLOBAL_RESTRICTIONS);
        }
        if (userData.account != null) {
            serializer.startTag((String) null, TAG_ACCOUNT);
            serializer.text(userData.account);
            serializer.endTag((String) null, TAG_ACCOUNT);
        }
        if (userData.persistSeedData && userData.seedAccountOptions != null) {
            serializer.startTag((String) null, TAG_SEED_ACCOUNT_OPTIONS);
            userData.seedAccountOptions.saveToXml(serializer);
            serializer.endTag((String) null, TAG_SEED_ACCOUNT_OPTIONS);
        }
        if (userData.getLastRequestQuietModeEnabledMillis() != 0) {
            serializer.startTag((String) null, TAG_LAST_REQUEST_QUIET_MODE_ENABLED_CALL);
            serializer.text(String.valueOf(userData.getLastRequestQuietModeEnabledMillis()));
            serializer.endTag((String) null, TAG_LAST_REQUEST_QUIET_MODE_ENABLED_CALL);
        }
        serializer.endTag((String) null, TAG_USER);
        serializer.endDocument();
    }

    /* Debug info: failed to restart local var, previous not found, register: 11 */
    @GuardedBy({"mRestrictionsLock", "mPackagesLock"})
    private void writeUserListLP() {
        int[] userIdsToWrite;
        int i;
        FileOutputStream fos = null;
        AtomicFile userListFile = new AtomicFile(this.mUserListFile);
        try {
            fos = userListFile.startWrite();
            BufferedOutputStream bos = new BufferedOutputStream(fos);
            XmlSerializer serializer = new FastXmlSerializer();
            serializer.setOutput(bos, StandardCharsets.UTF_8.name());
            serializer.startDocument((String) null, true);
            serializer.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);
            serializer.startTag((String) null, "users");
            serializer.attribute((String) null, ATTR_NEXT_SERIAL_NO, Integer.toString(this.mNextSerialNumber));
            serializer.attribute((String) null, ATTR_USER_VERSION, Integer.toString(this.mUserVersion));
            serializer.startTag((String) null, TAG_GUEST_RESTRICTIONS);
            synchronized (this.mGuestRestrictions) {
                UserRestrictionsUtils.writeRestrictions(serializer, this.mGuestRestrictions, TAG_RESTRICTIONS);
            }
            serializer.endTag((String) null, TAG_GUEST_RESTRICTIONS);
            serializer.startTag((String) null, TAG_DEVICE_OWNER_USER_ID);
            serializer.attribute((String) null, ATTR_ID, Integer.toString(this.mDeviceOwnerUserId));
            serializer.endTag((String) null, TAG_DEVICE_OWNER_USER_ID);
            synchronized (this.mUsersLock) {
                userIdsToWrite = new int[this.mUsers.size()];
                for (int i2 = 0; i2 < userIdsToWrite.length; i2++) {
                    userIdsToWrite[i2] = this.mUsers.valueAt(i2).info.id;
                }
            }
            for (int id : userIdsToWrite) {
                serializer.startTag((String) null, TAG_USER);
                serializer.attribute((String) null, ATTR_ID, Integer.toString(id));
                serializer.endTag((String) null, TAG_USER);
            }
            serializer.endTag((String) null, "users");
            serializer.endDocument();
            userListFile.finishWrite(fos);
        } catch (Exception e) {
            userListFile.failWrite(fos);
            Slog.e(LOG_TAG, "Error writing user list");
        }
    }

    private UserData readUserLP(int id) {
        try {
            File file = this.mUsersDir;
            FileInputStream fis = new AtomicFile(new File(file, Integer.toString(id) + XML_SUFFIX)).openRead();
            UserData readUserLP = readUserLP(id, fis);
            IoUtils.closeQuietly(fis);
            return readUserLP;
        } catch (IOException e) {
            Slog.e(LOG_TAG, "Error reading user list");
        } catch (XmlPullParserException e2) {
            Slog.e(LOG_TAG, "Error reading user list");
        } catch (Throwable th) {
            IoUtils.closeQuietly((AutoCloseable) null);
            throw th;
        }
        IoUtils.closeQuietly((AutoCloseable) null);
        return null;
    }

    /* access modifiers changed from: package-private */
    /* JADX WARNING: Code restructure failed: missing block: B:95:0x02f1, code lost:
        return r1;
     */
    @com.android.internal.annotations.VisibleForTesting
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public com.android.server.pm.UserManagerService.UserData readUserLP(int r48, java.io.InputStream r49) throws java.io.IOException, org.xmlpull.v1.XmlPullParserException {
        /*
            r47 = this;
            r1 = r47
            r2 = r48
            r0 = 0
            r3 = r48
            r4 = 0
            r5 = 0
            r6 = 0
            r7 = 0
            r9 = 0
            r11 = 0
            r13 = 0
            r14 = -10000(0xffffffffffffd8f0, float:NaN)
            r15 = 0
            r16 = -10000(0xffffffffffffd8f0, float:NaN)
            r17 = 0
            r18 = 0
            r19 = 0
            r20 = 0
            r21 = 0
            r22 = 0
            r23 = 0
            r24 = 0
            r25 = 0
            r26 = r3
            org.xmlpull.v1.XmlPullParser r3 = android.util.Xml.newPullParser()
            java.nio.charset.Charset r27 = java.nio.charset.StandardCharsets.UTF_8
            r28 = r0
            java.lang.String r0 = r27.name()
            r27 = r4
            r4 = r49
            r3.setInput(r4, r0)
        L_0x003d:
            int r0 = r3.next()
            r29 = r0
            r4 = 2
            if (r0 == r4) goto L_0x004e
            r0 = r29
            r4 = 1
            if (r0 == r4) goto L_0x0050
            r4 = r49
            goto L_0x003d
        L_0x004e:
            r0 = r29
        L_0x0050:
            r4 = 2
            if (r0 == r4) goto L_0x006d
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            r32 = r5
            java.lang.String r5 = "Unable to read user "
            r4.append(r5)
            r4.append(r2)
            java.lang.String r4 = r4.toString()
            java.lang.String r5 = "UserManagerService"
            android.util.Slog.e(r5, r4)
            r4 = 0
            return r4
        L_0x006d:
            r32 = r5
            r4 = 2
            if (r0 != r4) goto L_0x0241
            java.lang.String r4 = r3.getName()
            java.lang.String r5 = "user"
            boolean r4 = r4.equals(r5)
            if (r4 == 0) goto L_0x0241
            r4 = -1
            java.lang.String r5 = "id"
            int r4 = r1.readIntAttribute(r3, r5, r4)
            if (r4 == r2) goto L_0x0094
            java.lang.String r5 = "UserManagerService"
            r29 = r0
            java.lang.String r0 = "User id does not match the file name"
            android.util.Slog.e(r5, r0)
            r0 = 0
            return r0
        L_0x0094:
            r29 = r0
            r0 = 0
            java.lang.String r5 = "serialNumber"
            int r5 = r1.readIntAttribute(r3, r5, r2)
            r0 = 0
            r33 = r4
            java.lang.String r4 = "flags"
            int r4 = r1.readIntAttribute(r3, r4, r0)
            java.lang.String r0 = "icon"
            r28 = r4
            r4 = 0
            java.lang.String r6 = r3.getAttributeValue(r4, r0)
            r0 = r5
            r4 = 0
            r34 = r0
            java.lang.String r0 = "created"
            long r7 = r1.readLongAttribute(r3, r0, r4)
            java.lang.String r0 = "lastLoggedIn"
            long r9 = r1.readLongAttribute(r3, r0, r4)
            java.lang.String r0 = "lastLoggedInFingerprint"
            r4 = 0
            java.lang.String r13 = r3.getAttributeValue(r4, r0)
            r0 = -10000(0xffffffffffffd8f0, float:NaN)
            java.lang.String r4 = "profileGroupId"
            int r14 = r1.readIntAttribute(r3, r4, r0)
            java.lang.String r4 = "profileBadge"
            r5 = 0
            int r15 = r1.readIntAttribute(r3, r4, r5)
            java.lang.String r4 = "restrictedProfileParentId"
            int r16 = r1.readIntAttribute(r3, r4, r0)
            java.lang.String r0 = "partial"
            r4 = 0
            java.lang.String r0 = r3.getAttributeValue(r4, r0)
            java.lang.String r4 = "true"
            boolean r4 = r4.equals(r0)
            if (r4 == 0) goto L_0x00f7
            r4 = 1
            r17 = r4
        L_0x00f7:
            java.lang.String r4 = "guestToRemove"
            r5 = 0
            java.lang.String r0 = r3.getAttributeValue(r5, r4)
            java.lang.String r4 = "true"
            boolean r4 = r4.equals(r0)
            if (r4 == 0) goto L_0x010a
            r4 = 1
            r18 = r4
        L_0x010a:
            java.lang.String r4 = "seedAccountName"
            r5 = 0
            java.lang.String r20 = r3.getAttributeValue(r5, r4)
            java.lang.String r4 = "seedAccountType"
            java.lang.String r21 = r3.getAttributeValue(r5, r4)
            if (r20 != 0) goto L_0x011d
            if (r21 == 0) goto L_0x011f
        L_0x011d:
            r19 = 1
        L_0x011f:
            int r4 = r3.getDepth()
            r5 = r32
        L_0x0125:
            r26 = r0
            int r0 = r3.next()
            r31 = r0
            r29 = r5
            r5 = 1
            if (r0 == r5) goto L_0x0216
            r0 = 3
            r5 = r31
            if (r5 != r0) goto L_0x013d
            int r0 = r3.getDepth()
            if (r0 <= r4) goto L_0x021a
        L_0x013d:
            r0 = 3
            if (r5 == r0) goto L_0x0208
            r0 = 4
            if (r5 != r0) goto L_0x0147
            r35 = r4
            goto L_0x020a
        L_0x0147:
            java.lang.String r0 = r3.getName()
            r35 = r4
            java.lang.String r4 = "name"
            boolean r4 = r4.equals(r0)
            if (r4 == 0) goto L_0x016d
            int r4 = r3.next()
            r5 = 4
            if (r4 != r5) goto L_0x0167
            java.lang.String r27 = r3.getText()
            r5 = r29
            r29 = r4
            goto L_0x0202
        L_0x0167:
            r5 = r29
            r29 = r4
            goto L_0x0202
        L_0x016d:
            java.lang.String r4 = "restrictions"
            boolean r4 = r4.equals(r0)
            if (r4 == 0) goto L_0x0182
            android.os.Bundle r23 = com.android.server.pm.UserRestrictionsUtils.readRestrictions(r3)
            r46 = r29
            r29 = r5
            r5 = r46
            goto L_0x0202
        L_0x0182:
            java.lang.String r4 = "device_policy_restrictions"
            boolean r4 = r4.equals(r0)
            if (r4 == 0) goto L_0x0196
            android.os.Bundle r24 = com.android.server.pm.UserRestrictionsUtils.readRestrictions(r3)
            r46 = r29
            r29 = r5
            r5 = r46
            goto L_0x0202
        L_0x0196:
            java.lang.String r4 = "device_policy_global_restrictions"
            boolean r4 = r4.equals(r0)
            if (r4 == 0) goto L_0x01a9
            android.os.Bundle r25 = com.android.server.pm.UserRestrictionsUtils.readRestrictions(r3)
            r46 = r29
            r29 = r5
            r5 = r46
            goto L_0x0202
        L_0x01a9:
            java.lang.String r4 = "account"
            boolean r4 = r4.equals(r0)
            if (r4 == 0) goto L_0x01c4
            int r4 = r3.next()
            r5 = 4
            if (r4 != r5) goto L_0x01bf
            java.lang.String r5 = r3.getText()
            r29 = r4
            goto L_0x0202
        L_0x01bf:
            r5 = r29
            r29 = r4
            goto L_0x0202
        L_0x01c4:
            java.lang.String r4 = "seedAccountOptions"
            boolean r4 = r4.equals(r0)
            if (r4 == 0) goto L_0x01da
            android.os.PersistableBundle r22 = android.os.PersistableBundle.restoreFromXml(r3)
            r19 = 1
            r46 = r29
            r29 = r5
            r5 = r46
            goto L_0x0202
        L_0x01da:
            java.lang.String r4 = "lastRequestQuietModeEnabledCall"
            boolean r4 = r4.equals(r0)
            if (r4 == 0) goto L_0x01fc
            int r4 = r3.next()
            r5 = 4
            if (r4 != r5) goto L_0x01f7
            java.lang.String r5 = r3.getText()
            long r11 = java.lang.Long.parseLong(r5)
            r5 = r29
            r29 = r4
            goto L_0x0202
        L_0x01f7:
            r5 = r29
            r29 = r4
            goto L_0x0202
        L_0x01fc:
            r46 = r29
            r29 = r5
            r5 = r46
        L_0x0202:
            r0 = r26
            r4 = r35
            goto L_0x0125
        L_0x0208:
            r35 = r4
        L_0x020a:
            r0 = r26
            r4 = r35
            r46 = r29
            r29 = r5
            r5 = r46
            goto L_0x0125
        L_0x0216:
            r35 = r4
            r5 = r31
        L_0x021a:
            r41 = r11
            r1 = r16
            r12 = r17
            r39 = r19
            r37 = r20
            r38 = r21
            r40 = r22
            r43 = r23
            r44 = r24
            r45 = r25
            r4 = r27
            r36 = r29
            r16 = r3
            r29 = r5
            r10 = r9
            r3 = r15
            r5 = r28
            r8 = r7
            r15 = r14
            r14 = r18
            r7 = r34
            goto L_0x0267
        L_0x0241:
            r29 = r0
            r41 = r11
            r1 = r16
            r12 = r17
            r39 = r19
            r37 = r20
            r38 = r21
            r40 = r22
            r43 = r23
            r44 = r24
            r45 = r25
            r4 = r27
            r5 = r28
            r36 = r32
            r16 = r3
            r10 = r9
            r3 = r15
            r8 = r7
            r15 = r14
            r14 = r18
            r7 = r26
        L_0x0267:
            android.content.pm.UserInfo r0 = new android.content.pm.UserInfo
            r0.<init>(r2, r4, r6, r5)
            r17 = r0
            r18 = r4
            r4 = r17
            r4.serialNumber = r7
            r4.creationTime = r8
            r4.lastLoggedInTime = r10
            r4.lastLoggedInFingerprint = r13
            r4.partial = r12
            r4.guestToRemove = r14
            r4.profileGroupId = r15
            r4.profileBadge = r3
            r4.restrictedProfileParentId = r1
            com.android.server.pm.UserManagerService$UserData r0 = new com.android.server.pm.UserManagerService$UserData
            r0.<init>()
            r17 = r0
            r19 = r1
            r1 = r17
            r1.info = r4
            r17 = r3
            r3 = r36
            r1.account = r3
            r20 = r3
            r3 = r37
            r1.seedAccountName = r3
            r21 = r3
            r3 = r38
            r1.seedAccountType = r3
            r22 = r3
            r3 = r39
            r1.persistSeedData = r3
            r23 = r3
            r3 = r40
            r1.seedAccountOptions = r3
            r25 = r3
            r24 = r4
            r3 = r41
            r1.setLastRequestQuietModeEnabledMillis(r3)
            r26 = r3
            r3 = r47
            java.lang.Object r4 = r3.mRestrictionsLock
            monitor-enter(r4)
            r28 = r5
            r5 = r43
            if (r5 == 0) goto L_0x02d3
            android.util.SparseArray<android.os.Bundle> r0 = r3.mBaseUserRestrictions     // Catch:{ all -> 0x02cb }
            r0.put(r2, r5)     // Catch:{ all -> 0x02cb }
            goto L_0x02d3
        L_0x02cb:
            r0 = move-exception
            r30 = r5
            r31 = r44
            r5 = r45
            goto L_0x02f3
        L_0x02d3:
            r30 = r5
            r5 = r44
            if (r5 == 0) goto L_0x02e5
            android.util.SparseArray<android.os.Bundle> r0 = r3.mDevicePolicyLocalUserRestrictions     // Catch:{ all -> 0x02df }
            r0.put(r2, r5)     // Catch:{ all -> 0x02df }
            goto L_0x02e5
        L_0x02df:
            r0 = move-exception
            r31 = r5
            r5 = r45
            goto L_0x02f3
        L_0x02e5:
            r31 = r5
            r5 = r45
            if (r5 == 0) goto L_0x02f0
            android.util.SparseArray<android.os.Bundle> r0 = r3.mDevicePolicyGlobalUserRestrictions     // Catch:{ all -> 0x02f2 }
            r0.put(r2, r5)     // Catch:{ all -> 0x02f2 }
        L_0x02f0:
            monitor-exit(r4)     // Catch:{ all -> 0x02f2 }
            return r1
        L_0x02f2:
            r0 = move-exception
        L_0x02f3:
            monitor-exit(r4)     // Catch:{ all -> 0x02f2 }
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.pm.UserManagerService.readUserLP(int, java.io.InputStream):com.android.server.pm.UserManagerService$UserData");
    }

    private int readIntAttribute(XmlPullParser parser, String attr, int defaultValue) {
        String valueString = parser.getAttributeValue((String) null, attr);
        if (valueString == null) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(valueString);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    private long readLongAttribute(XmlPullParser parser, String attr, long defaultValue) {
        String valueString = parser.getAttributeValue((String) null, attr);
        if (valueString == null) {
            return defaultValue;
        }
        try {
            return Long.parseLong(valueString);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    private static void cleanAppRestrictionsForPackageLAr(String pkg, int userId) {
        File resFile = new File(Environment.getUserSystemDirectory(userId), packageToRestrictionsFileName(pkg));
        if (resFile.exists()) {
            resFile.delete();
        }
    }

    public UserInfo createProfileForUser(String name, int flags, int userId, String[] disallowedPackages) {
        checkManageOrCreateUsersPermission(flags);
        return createUserInternal(name, flags, userId, disallowedPackages);
    }

    public UserInfo createProfileForUserEvenWhenDisallowed(String name, int flags, int userId, String[] disallowedPackages) {
        checkManageOrCreateUsersPermission(flags);
        return createUserInternalUnchecked(name, flags, userId, disallowedPackages);
    }

    public boolean removeUserEvenWhenDisallowed(int userHandle) {
        checkManageOrCreateUsersPermission("Only the system can remove users");
        return removeUserUnchecked(userHandle);
    }

    public UserInfo createUser(String name, int flags) {
        checkManageOrCreateUsersPermission(flags);
        return createUserInternal(name, flags, ScreenRotationAnimationInjector.BLACK_SURFACE_INVALID_POSITION);
    }

    private UserInfo createUserInternal(String name, int flags, int parentId) {
        return createUserInternal(name, flags, parentId, (String[]) null);
    }

    private UserInfo createUserInternal(String name, int flags, int parentId, String[] disallowedPackages) {
        String restriction;
        if ((flags & 32) != 0) {
            restriction = "no_add_managed_profile";
        } else {
            restriction = "no_add_user";
        }
        if (!hasUserRestriction(restriction, UserHandle.getCallingUserId())) {
            return createUserInternalUnchecked(name, flags, parentId, disallowedPackages);
        }
        Log.w(LOG_TAG, "Cannot add user. " + restriction + " is enabled.");
        return null;
    }

    /* Debug info: failed to restart local var, previous not found, register: 25 */
    /* access modifiers changed from: private */
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
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
        	at jadx.core.dex.visitors.regions.RegionMaker.processMonitorEnter(RegionMaker.java:561)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverse(RegionMaker.java:133)
        	at jadx.core.dex.visitors.regions.RegionMaker.makeRegion(RegionMaker.java:86)
        	at jadx.core.dex.visitors.regions.RegionMaker.processIf(RegionMaker.java:693)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverse(RegionMaker.java:123)
        	at jadx.core.dex.visitors.regions.RegionMaker.makeRegion(RegionMaker.java:86)
        	at jadx.core.dex.visitors.regions.RegionMaker.processMonitorEnter(RegionMaker.java:598)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverse(RegionMaker.java:133)
        	at jadx.core.dex.visitors.regions.RegionMaker.makeRegion(RegionMaker.java:86)
        	at jadx.core.dex.visitors.regions.RegionMaker.processIf(RegionMaker.java:698)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverse(RegionMaker.java:123)
        	at jadx.core.dex.visitors.regions.RegionMaker.makeRegion(RegionMaker.java:86)
        	at jadx.core.dex.visitors.regions.RegionMakerVisitor.visit(RegionMakerVisitor.java:49)
        */
    public android.content.pm.UserInfo createUserInternalUnchecked(java.lang.String r26, int r27, int r28, java.lang.String[] r29) {
        /*
            r25 = this;
            r1 = r25
            r2 = r27
            r3 = r28
            java.lang.Class<com.android.server.storage.DeviceStorageMonitorInternal> r0 = com.android.server.storage.DeviceStorageMonitorInternal.class
            java.lang.Object r0 = com.android.server.LocalServices.getService(r0)
            r4 = r0
            com.android.server.storage.DeviceStorageMonitorInternal r4 = (com.android.server.storage.DeviceStorageMonitorInternal) r4
            boolean r0 = r4.isMemoryLow()
            r5 = 0
            if (r0 == 0) goto L_0x001e
            java.lang.String r0 = "UserManagerService"
            java.lang.String r6 = "Cannot add user. Not enough space on disk."
            android.util.Log.w(r0, r6)
            return r5
        L_0x001e:
            r0 = r2 & 4
            r6 = 0
            if (r0 == 0) goto L_0x0025
            r0 = 1
            goto L_0x0026
        L_0x0025:
            r0 = r6
        L_0x0026:
            r8 = r0
            r0 = 8388640(0x800020, float:1.1754988E-38)
            r0 = r0 & r2
            if (r0 == 0) goto L_0x002f
            r0 = 1
            goto L_0x0030
        L_0x002f:
            r0 = r6
        L_0x0030:
            r9 = r0
            r0 = r2 & 8
            if (r0 == 0) goto L_0x0037
            r0 = 1
            goto L_0x0038
        L_0x0037:
            r0 = r6
        L_0x0038:
            r10 = r0
            r0 = r2 & 512(0x200, float:7.175E-43)
            if (r0 == 0) goto L_0x003f
            r0 = 1
            goto L_0x0040
        L_0x003f:
            r0 = r6
        L_0x0040:
            r11 = r0
            long r12 = android.os.Binder.clearCallingIdentity()
            java.lang.Object r14 = r1.mPackagesLock     // Catch:{ all -> 0x0325 }
            monitor-enter(r14)     // Catch:{ all -> 0x0325 }
            r15 = 0
            r0 = -10000(0xffffffffffffd8f0, float:NaN)
            if (r3 == r0) goto L_0x006c
            java.lang.Object r7 = r1.mUsersLock     // Catch:{ all -> 0x0061 }
            monitor-enter(r7)     // Catch:{ all -> 0x0061 }
            com.android.server.pm.UserManagerService$UserData r16 = r1.getUserDataLU(r3)     // Catch:{ all -> 0x005e }
            r15 = r16
            monitor-exit(r7)     // Catch:{ all -> 0x005e }
            if (r15 != 0) goto L_0x006c
            monitor-exit(r14)     // Catch:{ all -> 0x0061 }
            android.os.Binder.restoreCallingIdentity(r12)
            return r5
        L_0x005e:
            r0 = move-exception
            monitor-exit(r7)     // Catch:{ all -> 0x005e }
            throw r0     // Catch:{ all -> 0x0061 }
        L_0x0061:
            r0 = move-exception
            r18 = r4
            r23 = r12
            r4 = r26
            r12 = r29
            goto L_0x031d
        L_0x006c:
            if (r9 == 0) goto L_0x0094
            boolean r7 = r1.canAddMoreManagedProfiles(r3, r6)     // Catch:{ all -> 0x0061 }
            if (r7 != 0) goto L_0x0094
            r7 = 8388608(0x800000, float:1.17549435E-38)
            r7 = r7 & r2
            if (r7 != 0) goto L_0x0094
            java.lang.String r0 = "UserManagerService"
            java.lang.StringBuilder r6 = new java.lang.StringBuilder     // Catch:{ all -> 0x0061 }
            r6.<init>()     // Catch:{ all -> 0x0061 }
            java.lang.String r7 = "Cannot add more managed profiles for user "
            r6.append(r7)     // Catch:{ all -> 0x0061 }
            r6.append(r3)     // Catch:{ all -> 0x0061 }
            java.lang.String r6 = r6.toString()     // Catch:{ all -> 0x0061 }
            android.util.Log.e(r0, r6)     // Catch:{ all -> 0x0061 }
            monitor-exit(r14)     // Catch:{ all -> 0x0061 }
            android.os.Binder.restoreCallingIdentity(r12)
            return r5
        L_0x0094:
            if (r8 != 0) goto L_0x00ac
            if (r9 != 0) goto L_0x00ac
            if (r11 != 0) goto L_0x00ac
            boolean r7 = r25.isUserLimitReached()     // Catch:{ all -> 0x0061 }
            if (r7 == 0) goto L_0x00ac
            java.lang.String r0 = "UserManagerService"
            java.lang.String r6 = "Cannot add user. Maximum user limit is reached."
            android.util.Log.e(r0, r6)     // Catch:{ all -> 0x0061 }
            monitor-exit(r14)     // Catch:{ all -> 0x0061 }
            android.os.Binder.restoreCallingIdentity(r12)
            return r5
        L_0x00ac:
            if (r8 == 0) goto L_0x00c0
            android.content.pm.UserInfo r7 = r25.findCurrentGuestUser()     // Catch:{ all -> 0x0061 }
            if (r7 == 0) goto L_0x00c0
            java.lang.String r0 = "UserManagerService"
            java.lang.String r6 = "Cannot add guest user. Guest user already exists."
            android.util.Log.e(r0, r6)     // Catch:{ all -> 0x0061 }
            monitor-exit(r14)     // Catch:{ all -> 0x0061 }
            android.os.Binder.restoreCallingIdentity(r12)
            return r5
        L_0x00c0:
            if (r10 == 0) goto L_0x00d6
            boolean r7 = android.os.UserManager.isSplitSystemUser()     // Catch:{ all -> 0x0061 }
            if (r7 != 0) goto L_0x00d6
            if (r3 == 0) goto L_0x00d6
            java.lang.String r0 = "UserManagerService"
            java.lang.String r6 = "Cannot add restricted profile - parent user must be owner"
            android.util.Log.w(r0, r6)     // Catch:{ all -> 0x0061 }
            monitor-exit(r14)     // Catch:{ all -> 0x0061 }
            android.os.Binder.restoreCallingIdentity(r12)
            return r5
        L_0x00d6:
            if (r10 == 0) goto L_0x010f
            boolean r7 = android.os.UserManager.isSplitSystemUser()     // Catch:{ all -> 0x0061 }
            if (r7 == 0) goto L_0x010f
            if (r15 != 0) goto L_0x00ec
            java.lang.String r0 = "UserManagerService"
            java.lang.String r6 = "Cannot add restricted profile - parent user must be specified"
            android.util.Log.w(r0, r6)     // Catch:{ all -> 0x0061 }
            monitor-exit(r14)     // Catch:{ all -> 0x0061 }
            android.os.Binder.restoreCallingIdentity(r12)
            return r5
        L_0x00ec:
            android.content.pm.UserInfo r7 = r15.info     // Catch:{ all -> 0x0061 }
            boolean r7 = r7.canHaveProfile()     // Catch:{ all -> 0x0061 }
            if (r7 != 0) goto L_0x010f
            java.lang.String r0 = "UserManagerService"
            java.lang.StringBuilder r6 = new java.lang.StringBuilder     // Catch:{ all -> 0x0061 }
            r6.<init>()     // Catch:{ all -> 0x0061 }
            java.lang.String r7 = "Cannot add restricted profile - profiles cannot be created for the specified parent user id "
            r6.append(r7)     // Catch:{ all -> 0x0061 }
            r6.append(r3)     // Catch:{ all -> 0x0061 }
            java.lang.String r6 = r6.toString()     // Catch:{ all -> 0x0061 }
            android.util.Log.w(r0, r6)     // Catch:{ all -> 0x0061 }
            monitor-exit(r14)     // Catch:{ all -> 0x0061 }
            android.os.Binder.restoreCallingIdentity(r12)
            return r5
        L_0x010f:
            boolean r7 = android.os.UserManager.isSplitSystemUser()     // Catch:{ all -> 0x0314 }
            if (r7 == 0) goto L_0x012f
            if (r8 != 0) goto L_0x012f
            if (r9 != 0) goto L_0x012f
            android.content.pm.UserInfo r7 = r25.getPrimaryUser()     // Catch:{ all -> 0x0061 }
            if (r7 != 0) goto L_0x012f
            r2 = r2 | 1
            java.lang.Object r7 = r1.mUsersLock     // Catch:{ all -> 0x015f }
            monitor-enter(r7)     // Catch:{ all -> 0x015f }
            boolean r6 = r1.mIsDeviceManaged     // Catch:{ all -> 0x012c }
            if (r6 != 0) goto L_0x012a
            r2 = r2 | 2
        L_0x012a:
            monitor-exit(r7)     // Catch:{ all -> 0x012c }
            goto L_0x012f
        L_0x012c:
            r0 = move-exception
            monitor-exit(r7)     // Catch:{ all -> 0x012c }
            throw r0     // Catch:{ all -> 0x015f }
        L_0x012f:
            int r6 = r25.getNextAvailableId()     // Catch:{ all -> 0x030a }
            int r6 = com.android.server.pm.UserManagerServiceInjector.checkAndGetNewUserId(r2, r6)     // Catch:{ all -> 0x030a }
            boolean r7 = r1.exists(r6)     // Catch:{ all -> 0x030a }
            if (r7 == 0) goto L_0x016a
            java.lang.String r0 = "UserManagerService"
            java.lang.StringBuilder r7 = new java.lang.StringBuilder     // Catch:{ all -> 0x015f }
            r7.<init>()     // Catch:{ all -> 0x015f }
            java.lang.String r5 = "user "
            r7.append(r5)     // Catch:{ all -> 0x015f }
            r7.append(r6)     // Catch:{ all -> 0x015f }
            java.lang.String r5 = " is exist, we could not create same user"
            r7.append(r5)     // Catch:{ all -> 0x015f }
            java.lang.String r5 = r7.toString()     // Catch:{ all -> 0x015f }
            android.util.Log.e(r0, r5)     // Catch:{ all -> 0x015f }
            monitor-exit(r14)     // Catch:{ all -> 0x015f }
            android.os.Binder.restoreCallingIdentity(r12)
            r0 = 0
            return r0
        L_0x015f:
            r0 = move-exception
            r18 = r4
            r23 = r12
            r4 = r26
            r12 = r29
            goto L_0x031d
        L_0x016a:
            java.io.File r5 = android.os.Environment.getUserSystemDirectory(r6)     // Catch:{ all -> 0x030a }
            r5.mkdirs()     // Catch:{ all -> 0x030a }
            android.content.res.Resources r5 = android.content.res.Resources.getSystem()     // Catch:{ all -> 0x030a }
            r7 = 17891464(0x1110088, float:2.6632675E-38)
            boolean r5 = r5.getBoolean(r7)     // Catch:{ all -> 0x030a }
            java.lang.Object r7 = r1.mUsersLock     // Catch:{ all -> 0x030a }
            monitor-enter(r7)     // Catch:{ all -> 0x030a }
            if (r8 == 0) goto L_0x0183
            if (r5 != 0) goto L_0x01a1
        L_0x0183:
            boolean r0 = r1.mForceEphemeralUsers     // Catch:{ all -> 0x02f5 }
            if (r0 != 0) goto L_0x01a1
            if (r15 == 0) goto L_0x01a4
            android.content.pm.UserInfo r0 = r15.info     // Catch:{ all -> 0x0192 }
            boolean r0 = r0.isEphemeral()     // Catch:{ all -> 0x0192 }
            if (r0 == 0) goto L_0x01a4
            goto L_0x01a1
        L_0x0192:
            r0 = move-exception
            r17 = r2
            r18 = r4
            r27 = r5
            r23 = r12
            r4 = r26
            r12 = r29
            goto L_0x0302
        L_0x01a1:
            r0 = r2 | 256(0x100, float:3.59E-43)
            r2 = r0
        L_0x01a4:
            android.content.pm.UserInfo r0 = new android.content.pm.UserInfo     // Catch:{ all -> 0x02e7 }
            r18 = r4
            r27 = r5
            r5 = 0
            r4 = r26
            r0.<init>(r6, r4, r5, r2)     // Catch:{ all -> 0x02df }
            r5 = r0
            int r0 = r1.mNextSerialNumber     // Catch:{ all -> 0x02df }
            r17 = r2
            int r2 = r0 + 1
            r1.mNextSerialNumber = r2     // Catch:{ all -> 0x02d9 }
            r5.serialNumber = r0     // Catch:{ all -> 0x02d9 }
            long r19 = java.lang.System.currentTimeMillis()     // Catch:{ all -> 0x02d9 }
            r21 = 946080000000(0xdc46c32800, double:4.674256262175E-312)
            int r0 = (r19 > r21 ? 1 : (r19 == r21 ? 0 : -1))
            if (r0 <= 0) goto L_0x01cd
            r23 = r12
            r12 = r19
            goto L_0x01d3
        L_0x01cd:
            r21 = 0
            r23 = r12
            r12 = r21
        L_0x01d3:
            r5.creationTime = r12     // Catch:{ all -> 0x02d5 }
            r0 = 1
            r5.partial = r0     // Catch:{ all -> 0x02d5 }
            java.lang.String r0 = android.os.Build.FINGERPRINT     // Catch:{ all -> 0x02d5 }
            r5.lastLoggedInFingerprint = r0     // Catch:{ all -> 0x02d5 }
            if (r9 == 0) goto L_0x01e8
            r0 = -10000(0xffffffffffffd8f0, float:NaN)
            if (r3 == r0) goto L_0x01e8
            int r0 = r1.getFreeProfileBadgeLU(r3)     // Catch:{ all -> 0x02d5 }
            r5.profileBadge = r0     // Catch:{ all -> 0x02d5 }
        L_0x01e8:
            com.android.server.pm.UserManagerService$UserData r0 = new com.android.server.pm.UserManagerService$UserData     // Catch:{ all -> 0x02d5 }
            r0.<init>()     // Catch:{ all -> 0x02d5 }
            r2 = r0
            r2.info = r5     // Catch:{ all -> 0x02d5 }
            android.util.SparseArray<com.android.server.pm.UserManagerService$UserData> r0 = r1.mUsers     // Catch:{ all -> 0x02d5 }
            r0.put(r6, r2)     // Catch:{ all -> 0x02d5 }
            monitor-exit(r7)     // Catch:{ all -> 0x02d5 }
            r1.writeUserLP(r2)     // Catch:{ all -> 0x02d1 }
            r25.writeUserListLP()     // Catch:{ all -> 0x02d1 }
            if (r15 == 0) goto L_0x0235
            if (r9 == 0) goto L_0x021a
            android.content.pm.UserInfo r0 = r15.info     // Catch:{ all -> 0x02d1 }
            int r0 = r0.profileGroupId     // Catch:{ all -> 0x02d1 }
            r7 = -10000(0xffffffffffffd8f0, float:NaN)
            if (r0 != r7) goto L_0x0213
            android.content.pm.UserInfo r0 = r15.info     // Catch:{ all -> 0x02d1 }
            android.content.pm.UserInfo r7 = r15.info     // Catch:{ all -> 0x02d1 }
            int r7 = r7.id     // Catch:{ all -> 0x02d1 }
            r0.profileGroupId = r7     // Catch:{ all -> 0x02d1 }
            r1.writeUserLP(r15)     // Catch:{ all -> 0x02d1 }
        L_0x0213:
            android.content.pm.UserInfo r0 = r15.info     // Catch:{ all -> 0x02d1 }
            int r0 = r0.profileGroupId     // Catch:{ all -> 0x02d1 }
            r5.profileGroupId = r0     // Catch:{ all -> 0x02d1 }
            goto L_0x0235
        L_0x021a:
            if (r10 == 0) goto L_0x0235
            android.content.pm.UserInfo r0 = r15.info     // Catch:{ all -> 0x02d1 }
            int r0 = r0.restrictedProfileParentId     // Catch:{ all -> 0x02d1 }
            r7 = -10000(0xffffffffffffd8f0, float:NaN)
            if (r0 != r7) goto L_0x022f
            android.content.pm.UserInfo r0 = r15.info     // Catch:{ all -> 0x02d1 }
            android.content.pm.UserInfo r7 = r15.info     // Catch:{ all -> 0x02d1 }
            int r7 = r7.id     // Catch:{ all -> 0x02d1 }
            r0.restrictedProfileParentId = r7     // Catch:{ all -> 0x02d1 }
            r1.writeUserLP(r15)     // Catch:{ all -> 0x02d1 }
        L_0x022f:
            android.content.pm.UserInfo r0 = r15.info     // Catch:{ all -> 0x02d1 }
            int r0 = r0.restrictedProfileParentId     // Catch:{ all -> 0x02d1 }
            r5.restrictedProfileParentId = r0     // Catch:{ all -> 0x02d1 }
        L_0x0235:
            monitor-exit(r14)     // Catch:{ all -> 0x02d1 }
            android.content.Context r0 = r1.mContext     // Catch:{ all -> 0x02cc }
            java.lang.Class<android.os.storage.StorageManager> r7 = android.os.storage.StorageManager.class
            java.lang.Object r0 = r0.getSystemService(r7)     // Catch:{ all -> 0x02cc }
            android.os.storage.StorageManager r0 = (android.os.storage.StorageManager) r0     // Catch:{ all -> 0x02cc }
            r7 = r0
            int r0 = r5.serialNumber     // Catch:{ all -> 0x02cc }
            boolean r12 = r5.isEphemeral()     // Catch:{ all -> 0x02cc }
            r7.createUserKey(r6, r0, r12)     // Catch:{ all -> 0x02cc }
            com.android.server.pm.UserDataPreparer r0 = r1.mUserDataPreparer     // Catch:{ all -> 0x02cc }
            int r12 = r5.serialNumber     // Catch:{ all -> 0x02cc }
            r13 = 3
            r0.prepareUserData(r6, r12, r13)     // Catch:{ all -> 0x02cc }
            com.android.server.pm.PackageManagerService r0 = r1.mPm     // Catch:{ all -> 0x02cc }
            r12 = r29
            r0.createNewUser(r6, r12)     // Catch:{ all -> 0x02c9 }
            r0 = 0
            r5.partial = r0     // Catch:{ all -> 0x02c9 }
            java.lang.Object r13 = r1.mPackagesLock     // Catch:{ all -> 0x02c9 }
            monitor-enter(r13)     // Catch:{ all -> 0x02c9 }
            r1.writeUserLP(r2)     // Catch:{ all -> 0x02c2 }
            monitor-exit(r13)     // Catch:{ all -> 0x02c2 }
            r25.updateUserIds()     // Catch:{ all -> 0x02c9 }
            android.os.Bundle r0 = new android.os.Bundle     // Catch:{ all -> 0x02c9 }
            r0.<init>()     // Catch:{ all -> 0x02c9 }
            r13 = r0
            if (r8 == 0) goto L_0x027b
            android.os.Bundle r14 = r1.mGuestRestrictions     // Catch:{ all -> 0x02c9 }
            monitor-enter(r14)     // Catch:{ all -> 0x02c9 }
            android.os.Bundle r0 = r1.mGuestRestrictions     // Catch:{ all -> 0x0278 }
            r13.putAll(r0)     // Catch:{ all -> 0x0278 }
            monitor-exit(r14)     // Catch:{ all -> 0x0278 }
            goto L_0x027b
        L_0x0278:
            r0 = move-exception
            monitor-exit(r14)     // Catch:{ all -> 0x0278 }
            throw r0     // Catch:{ all -> 0x02c9 }
        L_0x027b:
            java.lang.Object r14 = r1.mRestrictionsLock     // Catch:{ all -> 0x02c9 }
            monitor-enter(r14)     // Catch:{ all -> 0x02c9 }
            android.util.SparseArray<android.os.Bundle> r0 = r1.mBaseUserRestrictions     // Catch:{ all -> 0x02bb }
            r0.append(r6, r13)     // Catch:{ all -> 0x02bb }
            monitor-exit(r14)     // Catch:{ all -> 0x02bb }
            com.android.server.pm.PackageManagerService r0 = r1.mPm     // Catch:{ all -> 0x02c9 }
            r0.onNewUserCreated(r6)     // Catch:{ all -> 0x02c9 }
            android.content.Intent r0 = new android.content.Intent     // Catch:{ all -> 0x02c9 }
            java.lang.String r14 = "android.intent.action.USER_ADDED"
            r0.<init>(r14)     // Catch:{ all -> 0x02c9 }
            java.lang.String r14 = "android.intent.extra.user_handle"
            r0.putExtra(r14, r6)     // Catch:{ all -> 0x02c9 }
            android.content.Context r14 = r1.mContext     // Catch:{ all -> 0x02c9 }
            android.os.UserHandle r15 = android.os.UserHandle.ALL     // Catch:{ all -> 0x02c9 }
            r16 = r2
            java.lang.String r2 = "android.permission.MANAGE_USERS"
            r14.sendBroadcastAsUser(r0, r15, r2)     // Catch:{ all -> 0x02c9 }
            android.content.Context r2 = r1.mContext     // Catch:{ all -> 0x02c9 }
            if (r8 == 0) goto L_0x02a8
            java.lang.String r14 = "users_guest_created"
            goto L_0x02b1
        L_0x02a8:
            if (r11 == 0) goto L_0x02ae
            java.lang.String r14 = "users_demo_created"
            goto L_0x02b1
        L_0x02ae:
            java.lang.String r14 = "users_user_created"
        L_0x02b1:
            r15 = 1
            com.android.internal.logging.MetricsLogger.count(r2, r14, r15)     // Catch:{ all -> 0x02c9 }
            android.os.Binder.restoreCallingIdentity(r23)
            return r5
        L_0x02bb:
            r0 = move-exception
            r16 = r2
        L_0x02be:
            monitor-exit(r14)     // Catch:{ all -> 0x02c0 }
            throw r0     // Catch:{ all -> 0x02c9 }
        L_0x02c0:
            r0 = move-exception
            goto L_0x02be
        L_0x02c2:
            r0 = move-exception
            r16 = r2
        L_0x02c5:
            monitor-exit(r13)     // Catch:{ all -> 0x02c7 }
            throw r0     // Catch:{ all -> 0x02c9 }
        L_0x02c7:
            r0 = move-exception
            goto L_0x02c5
        L_0x02c9:
            r0 = move-exception
            goto L_0x0330
        L_0x02cc:
            r0 = move-exception
            r12 = r29
            goto L_0x0330
        L_0x02d1:
            r0 = move-exception
            r12 = r29
            goto L_0x0305
        L_0x02d5:
            r0 = move-exception
            r12 = r29
            goto L_0x0302
        L_0x02d9:
            r0 = move-exception
            r23 = r12
            r12 = r29
            goto L_0x0302
        L_0x02df:
            r0 = move-exception
            r17 = r2
            r23 = r12
            r12 = r29
            goto L_0x0302
        L_0x02e7:
            r0 = move-exception
            r17 = r2
            r18 = r4
            r27 = r5
            r23 = r12
            r4 = r26
            r12 = r29
            goto L_0x0302
        L_0x02f5:
            r0 = move-exception
            r18 = r4
            r27 = r5
            r23 = r12
            r4 = r26
            r12 = r29
            r17 = r2
        L_0x0302:
            monitor-exit(r7)     // Catch:{ all -> 0x0308 }
            throw r0     // Catch:{ all -> 0x0304 }
        L_0x0304:
            r0 = move-exception
        L_0x0305:
            r2 = r17
            goto L_0x031d
        L_0x0308:
            r0 = move-exception
            goto L_0x0302
        L_0x030a:
            r0 = move-exception
            r18 = r4
            r23 = r12
            r4 = r26
            r12 = r29
            goto L_0x031d
        L_0x0314:
            r0 = move-exception
            r18 = r4
            r23 = r12
            r4 = r26
            r12 = r29
        L_0x031d:
            monitor-exit(r14)     // Catch:{ all -> 0x0323 }
            throw r0     // Catch:{ all -> 0x031f }
        L_0x031f:
            r0 = move-exception
            r17 = r2
            goto L_0x0330
        L_0x0323:
            r0 = move-exception
            goto L_0x031d
        L_0x0325:
            r0 = move-exception
            r18 = r4
            r23 = r12
            r4 = r26
            r12 = r29
            r17 = r2
        L_0x0330:
            android.os.Binder.restoreCallingIdentity(r23)
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.pm.UserManagerService.createUserInternalUnchecked(java.lang.String, int, int, java.lang.String[]):android.content.pm.UserInfo");
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public UserData putUserInfo(UserInfo userInfo) {
        UserData userData = new UserData();
        userData.info = userInfo;
        synchronized (this.mUsers) {
            this.mUsers.put(userInfo.id, userData);
        }
        return userData;
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public void removeUserInfo(int userId) {
        synchronized (this.mUsers) {
            this.mUsers.remove(userId);
        }
    }

    public UserInfo createRestrictedProfile(String name, int parentUserId) {
        checkManageOrCreateUsersPermission("setupRestrictedProfile");
        UserInfo user = createProfileForUser(name, 8, parentUserId, (String[]) null);
        if (user == null) {
            return null;
        }
        long identity = Binder.clearCallingIdentity();
        try {
            setUserRestriction("no_modify_accounts", true, user.id);
            Settings.Secure.putIntForUser(this.mContext.getContentResolver(), "location_mode", 0, user.id);
            setUserRestriction("no_share_location", true, user.id);
            return user;
        } finally {
            Binder.restoreCallingIdentity(identity);
        }
    }

    private UserInfo findCurrentGuestUser() {
        synchronized (this.mUsersLock) {
            int size = this.mUsers.size();
            for (int i = 0; i < size; i++) {
                UserInfo user = this.mUsers.valueAt(i).info;
                if (user.isGuest() && !user.guestToRemove && !this.mRemovingUserIds.get(user.id)) {
                    return user;
                }
            }
            return null;
        }
    }

    /* Debug info: failed to restart local var, previous not found, register: 7 */
    /* JADX WARNING: Code restructure failed: missing block: B:21:0x0045, code lost:
        if (r5.info.isGuest() != false) goto L_0x004c;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:23:0x0048, code lost:
        android.os.Binder.restoreCallingIdentity(r2);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:24:0x004b, code lost:
        return false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:26:?, code lost:
        r5.info.guestToRemove = true;
        r5.info.flags |= 64;
        writeUserLP(r5);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:28:0x005d, code lost:
        android.os.Binder.restoreCallingIdentity(r2);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:29:0x0061, code lost:
        return true;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean markGuestForDeletion(int r8) {
        /*
            r7 = this;
            java.lang.String r0 = "Only the system can remove users"
            checkManageUsersPermission(r0)
            int r0 = android.os.UserHandle.getCallingUserId()
            android.os.Bundle r0 = r7.getUserRestrictions(r0)
            r1 = 0
            java.lang.String r2 = "no_remove_user"
            boolean r0 = r0.getBoolean(r2, r1)
            if (r0 == 0) goto L_0x001f
            java.lang.String r0 = "UserManagerService"
            java.lang.String r2 = "Cannot remove user. DISALLOW_REMOVE_USER is enabled."
            android.util.Log.w(r0, r2)
            return r1
        L_0x001f:
            long r2 = android.os.Binder.clearCallingIdentity()
            java.lang.Object r0 = r7.mPackagesLock     // Catch:{ all -> 0x006e }
            monitor-enter(r0)     // Catch:{ all -> 0x006e }
            java.lang.Object r4 = r7.mUsersLock     // Catch:{ all -> 0x006b }
            monitor-enter(r4)     // Catch:{ all -> 0x006b }
            android.util.SparseArray<com.android.server.pm.UserManagerService$UserData> r5 = r7.mUsers     // Catch:{ all -> 0x0068 }
            java.lang.Object r5 = r5.get(r8)     // Catch:{ all -> 0x0068 }
            com.android.server.pm.UserManagerService$UserData r5 = (com.android.server.pm.UserManagerService.UserData) r5     // Catch:{ all -> 0x0068 }
            if (r8 == 0) goto L_0x0062
            if (r5 == 0) goto L_0x0062
            android.util.SparseBooleanArray r6 = r7.mRemovingUserIds     // Catch:{ all -> 0x0068 }
            boolean r6 = r6.get(r8)     // Catch:{ all -> 0x0068 }
            if (r6 == 0) goto L_0x003e
            goto L_0x0062
        L_0x003e:
            monitor-exit(r4)     // Catch:{ all -> 0x0068 }
            android.content.pm.UserInfo r4 = r5.info     // Catch:{ all -> 0x006b }
            boolean r4 = r4.isGuest()     // Catch:{ all -> 0x006b }
            if (r4 != 0) goto L_0x004c
            monitor-exit(r0)     // Catch:{ all -> 0x006b }
            android.os.Binder.restoreCallingIdentity(r2)
            return r1
        L_0x004c:
            android.content.pm.UserInfo r1 = r5.info     // Catch:{ all -> 0x006b }
            r4 = 1
            r1.guestToRemove = r4     // Catch:{ all -> 0x006b }
            android.content.pm.UserInfo r1 = r5.info     // Catch:{ all -> 0x006b }
            int r6 = r1.flags     // Catch:{ all -> 0x006b }
            r6 = r6 | 64
            r1.flags = r6     // Catch:{ all -> 0x006b }
            r7.writeUserLP(r5)     // Catch:{ all -> 0x006b }
            monitor-exit(r0)     // Catch:{ all -> 0x006b }
            android.os.Binder.restoreCallingIdentity(r2)
            return r4
        L_0x0062:
            monitor-exit(r4)     // Catch:{ all -> 0x0068 }
            monitor-exit(r0)     // Catch:{ all -> 0x006b }
            android.os.Binder.restoreCallingIdentity(r2)
            return r1
        L_0x0068:
            r1 = move-exception
            monitor-exit(r4)     // Catch:{ all -> 0x0068 }
            throw r1     // Catch:{ all -> 0x006b }
        L_0x006b:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x006b }
            throw r1     // Catch:{ all -> 0x006e }
        L_0x006e:
            r0 = move-exception
            android.os.Binder.restoreCallingIdentity(r2)
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.pm.UserManagerService.markGuestForDeletion(int):boolean");
    }

    public boolean removeUser(int userHandle) {
        boolean isManagedProfile;
        Slog.i(LOG_TAG, "removeUser u" + userHandle);
        checkManageOrCreateUsersPermission("Only the system can remove users");
        synchronized (this.mUsersLock) {
            UserInfo userInfo = getUserInfoLU(userHandle);
            isManagedProfile = userInfo != null && userInfo.isManagedProfile();
        }
        String restriction = isManagedProfile ? "no_remove_managed_profile" : "no_remove_user";
        if (!getUserRestrictions(UserHandle.getCallingUserId()).getBoolean(restriction, false)) {
            return removeUserUnchecked(userHandle);
        }
        Log.w(LOG_TAG, "Cannot remove user. " + restriction + " is enabled.");
        return false;
    }

    /* Debug info: failed to restart local var, previous not found, register: 11 */
    /* access modifiers changed from: private */
    /* JADX WARNING: Code restructure failed: missing block: B:45:?, code lost:
        r6.info.partial = true;
        r6.info.flags |= 64;
        writeUserLP(r6);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:48:?, code lost:
        r11.mAppOpsService.removeUser(r12);
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean removeUserUnchecked(int r12) {
        /*
            r11 = this;
            long r0 = android.os.Binder.clearCallingIdentity()
            int r2 = android.app.ActivityManager.getCurrentUser()     // Catch:{ all -> 0x00d6 }
            r3 = 0
            if (r2 != r12) goto L_0x0017
            java.lang.String r4 = "UserManagerService"
            java.lang.String r5 = "Current user cannot be removed."
            android.util.Log.w(r4, r5)     // Catch:{ all -> 0x00d6 }
            android.os.Binder.restoreCallingIdentity(r0)
            return r3
        L_0x0017:
            java.lang.Object r4 = r11.mPackagesLock     // Catch:{ all -> 0x00d6 }
            monitor-enter(r4)     // Catch:{ all -> 0x00d6 }
            java.lang.Object r5 = r11.mUsersLock     // Catch:{ all -> 0x00d3 }
            monitor-enter(r5)     // Catch:{ all -> 0x00d3 }
            android.util.SparseArray<com.android.server.pm.UserManagerService$UserData> r6 = r11.mUsers     // Catch:{ all -> 0x00d0 }
            java.lang.Object r6 = r6.get(r12)     // Catch:{ all -> 0x00d0 }
            com.android.server.pm.UserManagerService$UserData r6 = (com.android.server.pm.UserManagerService.UserData) r6     // Catch:{ all -> 0x00d0 }
            if (r12 != 0) goto L_0x0034
            java.lang.String r7 = "UserManagerService"
            java.lang.String r8 = "System user cannot be removed."
            android.util.Log.e(r7, r8)     // Catch:{ all -> 0x00d0 }
            monitor-exit(r5)     // Catch:{ all -> 0x00d0 }
            monitor-exit(r4)     // Catch:{ all -> 0x00d3 }
            android.os.Binder.restoreCallingIdentity(r0)
            return r3
        L_0x0034:
            r7 = 1
            if (r6 != 0) goto L_0x0050
            java.lang.String r8 = "UserManagerService"
            java.lang.String r9 = "Cannot remove user %d, invalid user id provided."
            java.lang.Object[] r7 = new java.lang.Object[r7]     // Catch:{ all -> 0x00d0 }
            java.lang.Integer r10 = java.lang.Integer.valueOf(r12)     // Catch:{ all -> 0x00d0 }
            r7[r3] = r10     // Catch:{ all -> 0x00d0 }
            java.lang.String r7 = java.lang.String.format(r9, r7)     // Catch:{ all -> 0x00d0 }
            android.util.Log.e(r8, r7)     // Catch:{ all -> 0x00d0 }
            monitor-exit(r5)     // Catch:{ all -> 0x00d0 }
            monitor-exit(r4)     // Catch:{ all -> 0x00d3 }
            android.os.Binder.restoreCallingIdentity(r0)
            return r3
        L_0x0050:
            android.util.SparseBooleanArray r8 = r11.mRemovingUserIds     // Catch:{ all -> 0x00d0 }
            boolean r8 = r8.get(r12)     // Catch:{ all -> 0x00d0 }
            if (r8 == 0) goto L_0x0071
            java.lang.String r8 = "UserManagerService"
            java.lang.String r9 = "User %d is already scheduled for removal."
            java.lang.Object[] r7 = new java.lang.Object[r7]     // Catch:{ all -> 0x00d0 }
            java.lang.Integer r10 = java.lang.Integer.valueOf(r12)     // Catch:{ all -> 0x00d0 }
            r7[r3] = r10     // Catch:{ all -> 0x00d0 }
            java.lang.String r7 = java.lang.String.format(r9, r7)     // Catch:{ all -> 0x00d0 }
            android.util.Log.e(r8, r7)     // Catch:{ all -> 0x00d0 }
            monitor-exit(r5)     // Catch:{ all -> 0x00d0 }
            monitor-exit(r4)     // Catch:{ all -> 0x00d3 }
            android.os.Binder.restoreCallingIdentity(r0)
            return r3
        L_0x0071:
            r11.addRemovingUserIdLocked(r12)     // Catch:{ all -> 0x00d0 }
            monitor-exit(r5)     // Catch:{ all -> 0x00d0 }
            android.content.pm.UserInfo r5 = r6.info     // Catch:{ all -> 0x00d3 }
            r5.partial = r7     // Catch:{ all -> 0x00d3 }
            android.content.pm.UserInfo r5 = r6.info     // Catch:{ all -> 0x00d3 }
            int r8 = r5.flags     // Catch:{ all -> 0x00d3 }
            r8 = r8 | 64
            r5.flags = r8     // Catch:{ all -> 0x00d3 }
            r11.writeUserLP(r6)     // Catch:{ all -> 0x00d3 }
            monitor-exit(r4)     // Catch:{ all -> 0x00d3 }
            com.android.internal.app.IAppOpsService r4 = r11.mAppOpsService     // Catch:{ RemoteException -> 0x008b }
            r4.removeUser(r12)     // Catch:{ RemoteException -> 0x008b }
            goto L_0x0093
        L_0x008b:
            r4 = move-exception
            java.lang.String r5 = "UserManagerService"
            java.lang.String r8 = "Unable to notify AppOpsService of removing user."
            android.util.Log.w(r5, r8, r4)     // Catch:{ all -> 0x00d6 }
        L_0x0093:
            android.content.pm.UserInfo r4 = r6.info     // Catch:{ all -> 0x00d6 }
            int r4 = r4.profileGroupId     // Catch:{ all -> 0x00d6 }
            r5 = -10000(0xffffffffffffd8f0, float:NaN)
            if (r4 == r5) goto L_0x00ae
            android.content.pm.UserInfo r4 = r6.info     // Catch:{ all -> 0x00d6 }
            boolean r4 = r4.isManagedProfile()     // Catch:{ all -> 0x00d6 }
            if (r4 == 0) goto L_0x00ae
            android.content.pm.UserInfo r4 = r6.info     // Catch:{ all -> 0x00d6 }
            int r4 = r4.profileGroupId     // Catch:{ all -> 0x00d6 }
            android.content.pm.UserInfo r5 = r6.info     // Catch:{ all -> 0x00d6 }
            int r5 = r5.id     // Catch:{ all -> 0x00d6 }
            r11.sendProfileRemovedBroadcast(r4, r5)     // Catch:{ all -> 0x00d6 }
        L_0x00ae:
            android.app.IActivityManager r4 = android.app.ActivityManager.getService()     // Catch:{ RemoteException -> 0x00c3 }
            com.android.server.pm.UserManagerService$5 r5 = new com.android.server.pm.UserManagerService$5     // Catch:{ RemoteException -> 0x00c3 }
            r5.<init>()     // Catch:{ RemoteException -> 0x00c3 }
            int r4 = r4.stopUser(r12, r7, r5)     // Catch:{ RemoteException -> 0x00c3 }
            if (r4 != 0) goto L_0x00bf
            r3 = r7
        L_0x00bf:
            android.os.Binder.restoreCallingIdentity(r0)
            return r3
        L_0x00c3:
            r4 = move-exception
            java.lang.String r5 = "UserManagerService"
            java.lang.String r7 = "Failed to stop user during removal."
            android.util.Log.w(r5, r7, r4)     // Catch:{ all -> 0x00d6 }
            android.os.Binder.restoreCallingIdentity(r0)
            return r3
        L_0x00d0:
            r3 = move-exception
            monitor-exit(r5)     // Catch:{ all -> 0x00d0 }
            throw r3     // Catch:{ all -> 0x00d3 }
        L_0x00d3:
            r3 = move-exception
            monitor-exit(r4)     // Catch:{ all -> 0x00d3 }
            throw r3     // Catch:{ all -> 0x00d6 }
        L_0x00d6:
            r2 = move-exception
            android.os.Binder.restoreCallingIdentity(r0)
            throw r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.pm.UserManagerService.removeUserUnchecked(int):boolean");
    }

    /* access modifiers changed from: package-private */
    @GuardedBy({"mUsersLock"})
    @VisibleForTesting
    public void addRemovingUserIdLocked(int userId) {
        this.mRemovingUserIds.put(userId, true);
        this.mRecentlyRemovedIds.add(Integer.valueOf(userId));
        if (this.mRecentlyRemovedIds.size() > 100) {
            this.mRecentlyRemovedIds.removeFirst();
        }
    }

    /* access modifiers changed from: package-private */
    public void finishRemoveUser(final int userHandle) {
        long ident = Binder.clearCallingIdentity();
        try {
            Intent addedIntent = new Intent("android.intent.action.USER_REMOVED");
            addedIntent.putExtra("android.intent.extra.user_handle", userHandle);
            this.mContext.sendOrderedBroadcastAsUser(addedIntent, UserHandle.ALL, "android.permission.MANAGE_USERS", new BroadcastReceiver() {
                public void onReceive(Context context, Intent intent) {
                    new Thread() {
                        public void run() {
                            ((ActivityTaskManagerInternal) LocalServices.getService(ActivityTaskManagerInternal.class)).onUserStopped(userHandle);
                            UserManagerService.this.removeUserState(userHandle);
                        }
                    }.start();
                }
            }, (Handler) null, -1, (String) null, (Bundle) null);
        } finally {
            Binder.restoreCallingIdentity(ident);
        }
    }

    /* access modifiers changed from: private */
    public void removeUserState(int userHandle) {
        try {
            ((StorageManager) this.mContext.getSystemService(StorageManager.class)).destroyUserKey(userHandle);
        } catch (IllegalStateException e) {
            Slog.i(LOG_TAG, "Destroying key for user " + userHandle + " failed, continuing anyway", e);
        }
        try {
            IGateKeeperService gk = GateKeeper.getService();
            if (gk != null) {
                gk.clearSecureUserId(userHandle);
            }
        } catch (Exception e2) {
            Slog.w(LOG_TAG, "unable to clear GK secure user id");
        }
        this.mPm.cleanUpUser(this, userHandle);
        this.mUserDataPreparer.destroyUserData(userHandle, 3);
        synchronized (this.mUsersLock) {
            this.mUsers.remove(userHandle);
            this.mIsUserManaged.delete(userHandle);
        }
        synchronized (this.mUserStates) {
            this.mUserStates.delete(userHandle);
        }
        synchronized (this.mRestrictionsLock) {
            this.mBaseUserRestrictions.remove(userHandle);
            this.mAppliedUserRestrictions.remove(userHandle);
            this.mCachedEffectiveUserRestrictions.remove(userHandle);
            this.mDevicePolicyLocalUserRestrictions.remove(userHandle);
            if (this.mDevicePolicyGlobalUserRestrictions.get(userHandle) != null) {
                this.mDevicePolicyGlobalUserRestrictions.remove(userHandle);
                applyUserRestrictionsForAllUsersLR();
            }
        }
        synchronized (this.mPackagesLock) {
            writeUserListLP();
        }
        File file = this.mUsersDir;
        new AtomicFile(new File(file, userHandle + XML_SUFFIX)).delete();
        updateUserIds();
    }

    private void sendProfileRemovedBroadcast(int parentUserId, int removedUserId) {
        Intent managedProfileIntent = new Intent("android.intent.action.MANAGED_PROFILE_REMOVED");
        managedProfileIntent.addFlags(1342177280);
        managedProfileIntent.putExtra("android.intent.extra.USER", new UserHandle(removedUserId));
        managedProfileIntent.putExtra("android.intent.extra.user_handle", removedUserId);
        this.mContext.sendBroadcastAsUser(managedProfileIntent, new UserHandle(parentUserId), (String) null);
    }

    public Bundle getApplicationRestrictions(String packageName) {
        return getApplicationRestrictionsForUser(packageName, UserHandle.getCallingUserId());
    }

    public Bundle getApplicationRestrictionsForUser(String packageName, int userId) {
        Bundle readApplicationRestrictionsLAr;
        if (UserHandle.getCallingUserId() != userId || !UserHandle.isSameApp(Binder.getCallingUid(), getUidForPackage(packageName))) {
            checkSystemOrRoot("get application restrictions for other user/app " + packageName);
        }
        synchronized (this.mAppRestrictionsLock) {
            readApplicationRestrictionsLAr = readApplicationRestrictionsLAr(packageName, userId);
        }
        return readApplicationRestrictionsLAr;
    }

    public void setApplicationRestrictions(String packageName, Bundle restrictions, int userId) {
        checkSystemOrRoot("set application restrictions");
        if (restrictions != null) {
            restrictions.setDefusable(true);
        }
        synchronized (this.mAppRestrictionsLock) {
            if (restrictions != null) {
                if (!restrictions.isEmpty()) {
                    writeApplicationRestrictionsLAr(packageName, restrictions, userId);
                }
            }
            cleanAppRestrictionsForPackageLAr(packageName, userId);
        }
        Intent changeIntent = new Intent("android.intent.action.APPLICATION_RESTRICTIONS_CHANGED");
        changeIntent.setPackage(packageName);
        changeIntent.addFlags(1073741824);
        this.mContext.sendBroadcastAsUser(changeIntent, UserHandle.of(userId));
    }

    private int getUidForPackage(String packageName) {
        long ident = Binder.clearCallingIdentity();
        try {
            return this.mContext.getPackageManager().getApplicationInfo(packageName, DumpState.DUMP_CHANGES).uid;
        } catch (PackageManager.NameNotFoundException e) {
            return -1;
        } finally {
            Binder.restoreCallingIdentity(ident);
        }
    }

    @GuardedBy({"mAppRestrictionsLock"})
    private static Bundle readApplicationRestrictionsLAr(String packageName, int userId) {
        return readApplicationRestrictionsLAr(new AtomicFile(new File(Environment.getUserSystemDirectory(userId), packageToRestrictionsFileName(packageName))));
    }

    @GuardedBy({"mAppRestrictionsLock"})
    @VisibleForTesting
    static Bundle readApplicationRestrictionsLAr(AtomicFile restrictionsFile) {
        Bundle restrictions = new Bundle();
        ArrayList<String> values = new ArrayList<>();
        if (!restrictionsFile.getBaseFile().exists()) {
            return restrictions;
        }
        FileInputStream fis = null;
        try {
            fis = restrictionsFile.openRead();
            XmlPullParser parser = Xml.newPullParser();
            parser.setInput(fis, StandardCharsets.UTF_8.name());
            XmlUtils.nextElement(parser);
            if (parser.getEventType() != 2) {
                Slog.e(LOG_TAG, "Unable to read restrictions file " + restrictionsFile.getBaseFile());
                IoUtils.closeQuietly(fis);
                return restrictions;
            }
            while (parser.next() != 1) {
                readEntry(restrictions, values, parser);
            }
            IoUtils.closeQuietly(fis);
            return restrictions;
        } catch (IOException | XmlPullParserException e) {
            Log.w(LOG_TAG, "Error parsing " + restrictionsFile.getBaseFile(), e);
        } catch (Throwable th) {
            IoUtils.closeQuietly(fis);
            throw th;
        }
    }

    private static void readEntry(Bundle restrictions, ArrayList<String> values, XmlPullParser parser) throws XmlPullParserException, IOException {
        if (parser.getEventType() == 2 && parser.getName().equals(TAG_ENTRY)) {
            String key = parser.getAttributeValue((String) null, ATTR_KEY);
            String valType = parser.getAttributeValue((String) null, "type");
            String multiple = parser.getAttributeValue((String) null, ATTR_MULTIPLE);
            if (multiple != null) {
                values.clear();
                int count = Integer.parseInt(multiple);
                while (count > 0) {
                    int next = parser.next();
                    int type = next;
                    if (next == 1) {
                        break;
                    } else if (type == 2 && parser.getName().equals(TAG_VALUE)) {
                        values.add(parser.nextText().trim());
                        count--;
                    }
                }
                String[] valueStrings = new String[values.size()];
                values.toArray(valueStrings);
                restrictions.putStringArray(key, valueStrings);
            } else if (ATTR_TYPE_BUNDLE.equals(valType)) {
                restrictions.putBundle(key, readBundleEntry(parser, values));
            } else if (ATTR_TYPE_BUNDLE_ARRAY.equals(valType)) {
                int outerDepth = parser.getDepth();
                ArrayList<Bundle> bundleList = new ArrayList<>();
                while (XmlUtils.nextElementWithin(parser, outerDepth)) {
                    bundleList.add(readBundleEntry(parser, values));
                }
                restrictions.putParcelableArray(key, (Parcelable[]) bundleList.toArray(new Bundle[bundleList.size()]));
            } else {
                String value = parser.nextText().trim();
                if (ATTR_TYPE_BOOLEAN.equals(valType)) {
                    restrictions.putBoolean(key, Boolean.parseBoolean(value));
                } else if (ATTR_TYPE_INTEGER.equals(valType)) {
                    restrictions.putInt(key, Integer.parseInt(value));
                } else {
                    restrictions.putString(key, value);
                }
            }
        }
    }

    private static Bundle readBundleEntry(XmlPullParser parser, ArrayList<String> values) throws IOException, XmlPullParserException {
        Bundle childBundle = new Bundle();
        int outerDepth = parser.getDepth();
        while (XmlUtils.nextElementWithin(parser, outerDepth)) {
            readEntry(childBundle, values, parser);
        }
        return childBundle;
    }

    @GuardedBy({"mAppRestrictionsLock"})
    private static void writeApplicationRestrictionsLAr(String packageName, Bundle restrictions, int userId) {
        writeApplicationRestrictionsLAr(restrictions, new AtomicFile(new File(Environment.getUserSystemDirectory(userId), packageToRestrictionsFileName(packageName))));
    }

    @GuardedBy({"mAppRestrictionsLock"})
    @VisibleForTesting
    static void writeApplicationRestrictionsLAr(Bundle restrictions, AtomicFile restrictionsFile) {
        FileOutputStream fos = null;
        try {
            fos = restrictionsFile.startWrite();
            BufferedOutputStream bos = new BufferedOutputStream(fos);
            XmlSerializer serializer = new FastXmlSerializer();
            serializer.setOutput(bos, StandardCharsets.UTF_8.name());
            serializer.startDocument((String) null, true);
            serializer.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);
            serializer.startTag((String) null, TAG_RESTRICTIONS);
            writeBundle(restrictions, serializer);
            serializer.endTag((String) null, TAG_RESTRICTIONS);
            serializer.endDocument();
            restrictionsFile.finishWrite(fos);
        } catch (Exception e) {
            restrictionsFile.failWrite(fos);
            Slog.e(LOG_TAG, "Error writing application restrictions list", e);
        }
    }

    private static void writeBundle(Bundle restrictions, XmlSerializer serializer) throws IOException {
        for (String key : restrictions.keySet()) {
            Object value = restrictions.get(key);
            serializer.startTag((String) null, TAG_ENTRY);
            serializer.attribute((String) null, ATTR_KEY, key);
            if (value instanceof Boolean) {
                serializer.attribute((String) null, "type", ATTR_TYPE_BOOLEAN);
                serializer.text(value.toString());
            } else if (value instanceof Integer) {
                serializer.attribute((String) null, "type", ATTR_TYPE_INTEGER);
                serializer.text(value.toString());
            } else {
                String str = "";
                if (value == null || (value instanceof String)) {
                    serializer.attribute((String) null, "type", ATTR_TYPE_STRING);
                    if (value != null) {
                        str = (String) value;
                    }
                    serializer.text(str);
                } else if (value instanceof Bundle) {
                    serializer.attribute((String) null, "type", ATTR_TYPE_BUNDLE);
                    writeBundle((Bundle) value, serializer);
                } else {
                    int i = 0;
                    if (value instanceof Parcelable[]) {
                        serializer.attribute((String) null, "type", ATTR_TYPE_BUNDLE_ARRAY);
                        Parcelable[] array = (Parcelable[]) value;
                        int length = array.length;
                        while (i < length) {
                            Parcelable parcelable = array[i];
                            if (parcelable instanceof Bundle) {
                                serializer.startTag((String) null, TAG_ENTRY);
                                serializer.attribute((String) null, "type", ATTR_TYPE_BUNDLE);
                                writeBundle((Bundle) parcelable, serializer);
                                serializer.endTag((String) null, TAG_ENTRY);
                                i++;
                            } else {
                                throw new IllegalArgumentException("bundle-array can only hold Bundles");
                            }
                        }
                        continue;
                    } else {
                        serializer.attribute((String) null, "type", ATTR_TYPE_STRING_ARRAY);
                        String[] values = (String[]) value;
                        serializer.attribute((String) null, ATTR_MULTIPLE, Integer.toString(values.length));
                        int length2 = values.length;
                        while (i < length2) {
                            String choice = values[i];
                            serializer.startTag((String) null, TAG_VALUE);
                            serializer.text(choice != null ? choice : str);
                            serializer.endTag((String) null, TAG_VALUE);
                            i++;
                        }
                    }
                }
            }
            serializer.endTag((String) null, TAG_ENTRY);
        }
    }

    public int getUserSerialNumber(int userHandle) {
        int i;
        synchronized (this.mUsersLock) {
            UserInfo userInfo = getUserInfoLU(userHandle);
            i = userInfo != null ? userInfo.serialNumber : -1;
        }
        return i;
    }

    public boolean isUserNameSet(int userHandle) {
        boolean z;
        synchronized (this.mUsersLock) {
            UserInfo userInfo = getUserInfoLU(userHandle);
            z = (userInfo == null || userInfo.name == null) ? false : true;
        }
        return z;
    }

    public int getUserHandle(int userSerialNumber) {
        synchronized (this.mUsersLock) {
            for (int userId : this.mUserIds) {
                UserInfo info = getUserInfoLU(userId);
                if (info != null && info.serialNumber == userSerialNumber) {
                    return userId;
                }
            }
            return -1;
        }
    }

    public long getUserCreationTime(int userHandle) {
        int callingUserId = UserHandle.getCallingUserId();
        UserInfo userInfo = null;
        synchronized (this.mUsersLock) {
            if (callingUserId == userHandle) {
                userInfo = getUserInfoLU(userHandle);
            } else {
                UserInfo parent = getProfileParentLU(userHandle);
                if (parent != null && parent.id == callingUserId) {
                    userInfo = getUserInfoLU(userHandle);
                }
            }
        }
        if (userInfo != null) {
            return userInfo.creationTime;
        }
        throw new SecurityException("userHandle can only be the calling user or a managed profile associated with this user");
    }

    private void updateUserIds() {
        int num = 0;
        synchronized (this.mUsersLock) {
            int userSize = this.mUsers.size();
            for (int i = 0; i < userSize; i++) {
                if (!this.mUsers.valueAt(i).info.partial) {
                    num++;
                }
            }
            int[] newUsers = new int[num];
            int n = 0;
            for (int i2 = 0; i2 < userSize; i2++) {
                if (!this.mUsers.valueAt(i2).info.partial) {
                    newUsers[n] = this.mUsers.keyAt(i2);
                    n++;
                }
            }
            this.mUserIds = newUsers;
        }
    }

    public void onBeforeStartUser(int userId) {
        UserInfo userInfo = getUserInfo(userId);
        if (userInfo != null) {
            int userSerial = userInfo.serialNumber;
            String reason = "prepare DE data for user " + userId;
            BackgroundDexOptService.pauseBgDexOpt(reason);
            this.mUserDataPreparer.prepareUserData(userId, userSerial, 1);
            this.mPm.reconcileAppsData(userId, 1, !android.os.Build.FINGERPRINT.equals(userInfo.lastLoggedInFingerprint));
            BackgroundDexOptService.resumeBgDexOpt(reason);
            if (userId != 0) {
                synchronized (this.mRestrictionsLock) {
                    applyUserRestrictionsLR(userId);
                }
            }
        }
    }

    public void onBeforeUnlockUser(int userId) {
        UserInfo userInfo = getUserInfo(userId);
        if (userInfo != null) {
            int userSerial = userInfo.serialNumber;
            String reason = "prepare CE data for user " + userId;
            BackgroundDexOptService.pauseBgDexOpt(reason);
            this.mUserDataPreparer.prepareUserData(userId, userSerial, 2);
            this.mPm.reconcileAppsData(userId, 2, !android.os.Build.FINGERPRINT.equals(userInfo.lastLoggedInFingerprint));
            BackgroundDexOptService.resumeBgDexOpt(reason);
        }
    }

    /* access modifiers changed from: package-private */
    public void reconcileUsers(String volumeUuid) {
        this.mUserDataPreparer.reconcileUsers(volumeUuid, getUsers(true));
    }

    public void onUserLoggedIn(int userId) {
        UserData userData = getUserDataNoChecks(userId);
        if (userData == null || userData.info.partial) {
            Slog.w(LOG_TAG, "userForeground: unknown user #" + userId);
            return;
        }
        long now = System.currentTimeMillis();
        if (now > EPOCH_PLUS_30_YEARS) {
            userData.info.lastLoggedInTime = now;
        }
        userData.info.lastLoggedInFingerprint = android.os.Build.FINGERPRINT;
        scheduleWriteUser(userData);
    }

    /* access modifiers changed from: package-private */
    /* JADX WARNING: Code restructure failed: missing block: B:15:0x0042, code lost:
        if (r1 < 0) goto L_0x0045;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:16:0x0044, code lost:
        return r1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:18:0x004c, code lost:
        throw new java.lang.IllegalStateException("No user id available!");
     */
    @com.android.internal.annotations.VisibleForTesting
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public int getNextAvailableId() {
        /*
            r7 = this;
            java.lang.Object r0 = r7.mUsersLock
            monitor-enter(r0)
            int r1 = r7.scanNextAvailableIdLocked()     // Catch:{ all -> 0x004d }
            if (r1 < 0) goto L_0x000b
            monitor-exit(r0)     // Catch:{ all -> 0x004d }
            return r1
        L_0x000b:
            android.util.SparseBooleanArray r2 = r7.mRemovingUserIds     // Catch:{ all -> 0x004d }
            int r2 = r2.size()     // Catch:{ all -> 0x004d }
            if (r2 <= 0) goto L_0x0041
            java.lang.String r2 = "UserManagerService"
            java.lang.String r3 = "All available IDs are used. Recycling LRU ids."
            android.util.Slog.i(r2, r3)     // Catch:{ all -> 0x004d }
            android.util.SparseBooleanArray r2 = r7.mRemovingUserIds     // Catch:{ all -> 0x004d }
            r2.clear()     // Catch:{ all -> 0x004d }
            java.util.LinkedList<java.lang.Integer> r2 = r7.mRecentlyRemovedIds     // Catch:{ all -> 0x004d }
            java.util.Iterator r2 = r2.iterator()     // Catch:{ all -> 0x004d }
        L_0x0025:
            boolean r3 = r2.hasNext()     // Catch:{ all -> 0x004d }
            if (r3 == 0) goto L_0x003c
            java.lang.Object r3 = r2.next()     // Catch:{ all -> 0x004d }
            java.lang.Integer r3 = (java.lang.Integer) r3     // Catch:{ all -> 0x004d }
            android.util.SparseBooleanArray r4 = r7.mRemovingUserIds     // Catch:{ all -> 0x004d }
            int r5 = r3.intValue()     // Catch:{ all -> 0x004d }
            r6 = 1
            r4.put(r5, r6)     // Catch:{ all -> 0x004d }
            goto L_0x0025
        L_0x003c:
            int r2 = r7.scanNextAvailableIdLocked()     // Catch:{ all -> 0x004d }
            r1 = r2
        L_0x0041:
            monitor-exit(r0)     // Catch:{ all -> 0x004d }
            if (r1 < 0) goto L_0x0045
            return r1
        L_0x0045:
            java.lang.IllegalStateException r0 = new java.lang.IllegalStateException
            java.lang.String r2 = "No user id available!"
            r0.<init>(r2)
            throw r0
        L_0x004d:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x004d }
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.pm.UserManagerService.getNextAvailableId():int");
    }

    @GuardedBy({"mUsersLock"})
    private int scanNextAvailableIdLocked() {
        for (int i = 10; i < MAX_USER_ID; i++) {
            if (this.mUsers.indexOfKey(i) < 0 && !this.mRemovingUserIds.get(i)) {
                return i;
            }
        }
        return -1;
    }

    private static String packageToRestrictionsFileName(String packageName) {
        return RESTRICTIONS_FILE_PREFIX + packageName + XML_SUFFIX;
    }

    /* Debug info: failed to restart local var, previous not found, register: 6 */
    /* JADX WARNING: Code restructure failed: missing block: B:16:0x0033, code lost:
        if (r11 == false) goto L_0x0038;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:18:?, code lost:
        writeUserLP(r2);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:20:0x0039, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void setSeedAccountData(int r7, java.lang.String r8, java.lang.String r9, android.os.PersistableBundle r10, boolean r11) {
        /*
            r6 = this;
            java.lang.String r0 = "Require MANAGE_USERS permission to set user seed data"
            checkManageUsersPermission(r0)
            java.lang.Object r0 = r6.mPackagesLock
            monitor-enter(r0)
            java.lang.Object r1 = r6.mUsersLock     // Catch:{ all -> 0x003d }
            monitor-enter(r1)     // Catch:{ all -> 0x003d }
            com.android.server.pm.UserManagerService$UserData r2 = r6.getUserDataLU(r7)     // Catch:{ all -> 0x003a }
            if (r2 != 0) goto L_0x002a
            java.lang.String r3 = "UserManagerService"
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ all -> 0x003a }
            r4.<init>()     // Catch:{ all -> 0x003a }
            java.lang.String r5 = "No such user for settings seed data u="
            r4.append(r5)     // Catch:{ all -> 0x003a }
            r4.append(r7)     // Catch:{ all -> 0x003a }
            java.lang.String r4 = r4.toString()     // Catch:{ all -> 0x003a }
            android.util.Slog.e(r3, r4)     // Catch:{ all -> 0x003a }
            monitor-exit(r1)     // Catch:{ all -> 0x003a }
            monitor-exit(r0)     // Catch:{ all -> 0x003d }
            return
        L_0x002a:
            r2.seedAccountName = r8     // Catch:{ all -> 0x003a }
            r2.seedAccountType = r9     // Catch:{ all -> 0x003a }
            r2.seedAccountOptions = r10     // Catch:{ all -> 0x003a }
            r2.persistSeedData = r11     // Catch:{ all -> 0x003a }
            monitor-exit(r1)     // Catch:{ all -> 0x003a }
            if (r11 == 0) goto L_0x0038
            r6.writeUserLP(r2)     // Catch:{ all -> 0x003d }
        L_0x0038:
            monitor-exit(r0)     // Catch:{ all -> 0x003d }
            return
        L_0x003a:
            r2 = move-exception
            monitor-exit(r1)     // Catch:{ all -> 0x003a }
            throw r2     // Catch:{ all -> 0x003d }
        L_0x003d:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x003d }
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.pm.UserManagerService.setSeedAccountData(int, java.lang.String, java.lang.String, android.os.PersistableBundle, boolean):void");
    }

    public String getSeedAccountName() throws RemoteException {
        String str;
        checkManageUsersPermission("Cannot get seed account information");
        synchronized (this.mUsersLock) {
            str = getUserDataLU(UserHandle.getCallingUserId()).seedAccountName;
        }
        return str;
    }

    public String getSeedAccountType() throws RemoteException {
        String str;
        checkManageUsersPermission("Cannot get seed account information");
        synchronized (this.mUsersLock) {
            str = getUserDataLU(UserHandle.getCallingUserId()).seedAccountType;
        }
        return str;
    }

    public PersistableBundle getSeedAccountOptions() throws RemoteException {
        PersistableBundle persistableBundle;
        checkManageUsersPermission("Cannot get seed account information");
        synchronized (this.mUsersLock) {
            persistableBundle = getUserDataLU(UserHandle.getCallingUserId()).seedAccountOptions;
        }
        return persistableBundle;
    }

    /* Debug info: failed to restart local var, previous not found, register: 3 */
    public void clearSeedAccountData() throws RemoteException {
        checkManageUsersPermission("Cannot clear seed account information");
        synchronized (this.mPackagesLock) {
            synchronized (this.mUsersLock) {
                UserData userData = getUserDataLU(UserHandle.getCallingUserId());
                if (userData != null) {
                    userData.clearSeedAccountData();
                    writeUserLP(userData);
                }
            }
        }
    }

    public boolean someUserHasSeedAccount(String accountName, String accountType) throws RemoteException {
        checkManageUsersPermission("Cannot check seed account information");
        synchronized (this.mUsersLock) {
            int userSize = this.mUsers.size();
            for (int i = 0; i < userSize; i++) {
                UserData data = this.mUsers.valueAt(i);
                if (!data.info.isInitialized()) {
                    if (data.seedAccountName == null) {
                        continue;
                    } else if (data.seedAccountName.equals(accountName)) {
                        if (data.seedAccountType == null) {
                            continue;
                        } else if (data.seedAccountType.equals(accountType)) {
                            return true;
                        }
                    }
                }
            }
            return false;
        }
    }

    /* JADX WARNING: type inference failed for: r1v1, types: [android.os.Binder] */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onShellCommand(java.io.FileDescriptor r9, java.io.FileDescriptor r10, java.io.FileDescriptor r11, java.lang.String[] r12, android.os.ShellCallback r13, android.os.ResultReceiver r14) {
        /*
            r8 = this;
            com.android.server.pm.UserManagerService$Shell r0 = new com.android.server.pm.UserManagerService$Shell
            r1 = 0
            r0.<init>()
            r1 = r8
            r2 = r9
            r3 = r10
            r4 = r11
            r5 = r12
            r6 = r13
            r7 = r14
            r0.exec(r1, r2, r3, r4, r5, r6, r7)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.pm.UserManagerService.onShellCommand(java.io.FileDescriptor, java.io.FileDescriptor, java.io.FileDescriptor, java.lang.String[], android.os.ShellCallback, android.os.ResultReceiver):void");
    }

    /* access modifiers changed from: package-private */
    public int onShellCommand(Shell shell, String cmd) {
        if (cmd == null) {
            return shell.handleDefaultCommands(cmd);
        }
        PrintWriter pw = shell.getOutPrintWriter();
        try {
            if ((cmd.hashCode() == 3322014 && cmd.equals("list")) ? false : true) {
                return shell.handleDefaultCommands(cmd);
            }
            return runList(pw);
        } catch (RemoteException e) {
            pw.println("Remote exception: " + e);
            return -1;
        }
    }

    private int runList(PrintWriter pw) throws RemoteException {
        IActivityManager am = ActivityManager.getService();
        List<UserInfo> users = getUsers(false);
        if (users == null) {
            pw.println("Error: couldn't get users");
            return 1;
        }
        pw.println("Users:");
        for (int i = 0; i < users.size(); i++) {
            String running = am.isUserRunning(users.get(i).id, 0) ? " running" : "";
            pw.println("\t" + users.get(i).toString() + running);
        }
        return 0;
    }

    /* Debug info: failed to restart local var, previous not found, register: 22 */
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
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
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
        	at jadx.core.dex.visitors.regions.RegionMaker.processMonitorEnter(RegionMaker.java:598)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverse(RegionMaker.java:133)
        	at jadx.core.dex.visitors.regions.RegionMaker.makeRegion(RegionMaker.java:86)
        	at jadx.core.dex.visitors.regions.RegionMaker.processIf(RegionMaker.java:698)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverse(RegionMaker.java:123)
        	at jadx.core.dex.visitors.regions.RegionMaker.makeRegion(RegionMaker.java:86)
        	at jadx.core.dex.visitors.regions.RegionMakerVisitor.visit(RegionMakerVisitor.java:49)
        */
    protected void dump(java.io.FileDescriptor r23, java.io.PrintWriter r24, java.lang.String[] r25) {
        /*
            r22 = this;
            r1 = r22
            r10 = r24
            android.content.Context r0 = r1.mContext
            java.lang.String r2 = "UserManagerService"
            boolean r0 = com.android.internal.util.DumpUtils.checkDumpPermission(r0, r2, r10)
            if (r0 != 0) goto L_0x000f
            return
        L_0x000f:
            long r11 = java.lang.System.currentTimeMillis()
            long r13 = android.os.SystemClock.elapsedRealtime()
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            java.lang.Object r15 = r1.mPackagesLock
            monitor-enter(r15)
            java.lang.Object r8 = r1.mUsersLock     // Catch:{ all -> 0x02c8 }
            monitor-enter(r8)     // Catch:{ all -> 0x02c8 }
            java.lang.String r0 = "Users:"
            r10.println(r0)     // Catch:{ all -> 0x02bf }
            r0 = 0
            r9 = r0
        L_0x0029:
            android.util.SparseArray<com.android.server.pm.UserManagerService$UserData> r0 = r1.mUsers     // Catch:{ all -> 0x02bf }
            int r0 = r0.size()     // Catch:{ all -> 0x02bf }
            if (r9 >= r0) goto L_0x01d7
            android.util.SparseArray<com.android.server.pm.UserManagerService$UserData> r0 = r1.mUsers     // Catch:{ all -> 0x02bf }
            java.lang.Object r0 = r0.valueAt(r9)     // Catch:{ all -> 0x02bf }
            com.android.server.pm.UserManagerService$UserData r0 = (com.android.server.pm.UserManagerService.UserData) r0     // Catch:{ all -> 0x02bf }
            r6 = r0
            if (r6 != 0) goto L_0x0044
            r20 = r8
            r21 = r9
            r18 = r13
            goto L_0x01ae
        L_0x0044:
            android.content.pm.UserInfo r0 = r6.info     // Catch:{ all -> 0x02bf }
            r7 = r0
            int r0 = r7.id     // Catch:{ all -> 0x02bf }
            r4 = r0
            java.lang.String r0 = "  "
            r10.print(r0)     // Catch:{ all -> 0x02bf }
            r10.print(r7)     // Catch:{ all -> 0x02bf }
            java.lang.String r0 = " serialNo="
            r10.print(r0)     // Catch:{ all -> 0x02bf }
            int r0 = r7.serialNumber     // Catch:{ all -> 0x02bf }
            r10.print(r0)     // Catch:{ all -> 0x02bf }
            android.util.SparseBooleanArray r0 = r1.mRemovingUserIds     // Catch:{ all -> 0x02bf }
            boolean r0 = r0.get(r4)     // Catch:{ all -> 0x02bf }
            if (r0 == 0) goto L_0x0071
            java.lang.String r0 = " <removing> "
            r10.print(r0)     // Catch:{ all -> 0x006a }
            goto L_0x0071
        L_0x006a:
            r0 = move-exception
            r20 = r8
            r18 = r13
            goto L_0x02c4
        L_0x0071:
            boolean r0 = r7.partial     // Catch:{ all -> 0x02bf }
            if (r0 == 0) goto L_0x007a
            java.lang.String r0 = " <partial>"
            r10.print(r0)     // Catch:{ all -> 0x006a }
        L_0x007a:
            r24.println()     // Catch:{ all -> 0x02bf }
            java.lang.String r0 = "    State: "
            r10.print(r0)     // Catch:{ all -> 0x02bf }
            android.util.SparseIntArray r2 = r1.mUserStates     // Catch:{ all -> 0x02bf }
            monitor-enter(r2)     // Catch:{ all -> 0x02bf }
            android.util.SparseIntArray r0 = r1.mUserStates     // Catch:{ all -> 0x01ca }
            r5 = -1
            int r0 = r0.get(r4, r5)     // Catch:{ all -> 0x01ca }
            r16 = r0
            monitor-exit(r2)     // Catch:{ all -> 0x01ca }
            java.lang.String r0 = com.android.server.am.UserState.stateToString(r16)     // Catch:{ all -> 0x02bf }
            r10.println(r0)     // Catch:{ all -> 0x02bf }
            java.lang.String r0 = "    Created: "
            r10.print(r0)     // Catch:{ all -> 0x02bf }
            long r0 = r7.creationTime     // Catch:{ all -> 0x01c5 }
            r2 = r24
            r17 = r4
            r4 = r11
            r18 = r13
            r13 = r6
            r14 = r7
            r6 = r0
            dumpTimeAgo(r2, r3, r4, r6)     // Catch:{ all -> 0x01be }
            java.lang.String r0 = "    Last logged in: "
            r10.print(r0)     // Catch:{ all -> 0x01be }
            long r0 = r14.lastLoggedInTime     // Catch:{ all -> 0x01be }
            r4 = r24
            r5 = r3
            r6 = r11
            r20 = r8
            r21 = r9
            r8 = r0
            dumpTimeAgo(r4, r5, r6, r8)     // Catch:{ all -> 0x01b9 }
            java.lang.String r0 = "    Last logged in fingerprint: "
            r10.print(r0)     // Catch:{ all -> 0x01b9 }
            java.lang.String r0 = r14.lastLoggedInFingerprint     // Catch:{ all -> 0x01b9 }
            r10.println(r0)     // Catch:{ all -> 0x01b9 }
            java.lang.String r0 = "    Start time: "
            r10.print(r0)     // Catch:{ all -> 0x01b9 }
            long r8 = r13.startRealtime     // Catch:{ all -> 0x01b9 }
            r4 = r24
            r5 = r3
            r6 = r18
            dumpTimeAgo(r4, r5, r6, r8)     // Catch:{ all -> 0x01b9 }
            java.lang.String r0 = "    Unlock time: "
            r10.print(r0)     // Catch:{ all -> 0x01b9 }
            long r8 = r13.unlockRealtime     // Catch:{ all -> 0x01b9 }
            r4 = r24
            r5 = r3
            r6 = r18
            dumpTimeAgo(r4, r5, r6, r8)     // Catch:{ all -> 0x01b9 }
            java.lang.String r0 = "    Has profile owner: "
            r10.print(r0)     // Catch:{ all -> 0x01b9 }
            r1 = r22
            android.util.SparseBooleanArray r0 = r1.mIsUserManaged     // Catch:{ all -> 0x02c6 }
            r4 = r17
            boolean r0 = r0.get(r4)     // Catch:{ all -> 0x02c6 }
            r10.println(r0)     // Catch:{ all -> 0x02c6 }
            java.lang.String r0 = "    Restrictions:"
            r10.println(r0)     // Catch:{ all -> 0x02c6 }
            java.lang.Object r2 = r1.mRestrictionsLock     // Catch:{ all -> 0x02c6 }
            monitor-enter(r2)     // Catch:{ all -> 0x02c6 }
            java.lang.String r0 = "      "
            android.util.SparseArray<android.os.Bundle> r5 = r1.mBaseUserRestrictions     // Catch:{ all -> 0x01b6 }
            int r6 = r14.id     // Catch:{ all -> 0x01b6 }
            java.lang.Object r5 = r5.get(r6)     // Catch:{ all -> 0x01b6 }
            android.os.Bundle r5 = (android.os.Bundle) r5     // Catch:{ all -> 0x01b6 }
            com.android.server.pm.UserRestrictionsUtils.dumpRestrictions(r10, r0, r5)     // Catch:{ all -> 0x01b6 }
            java.lang.String r0 = "    Device policy global restrictions:"
            r10.println(r0)     // Catch:{ all -> 0x01b6 }
            java.lang.String r0 = "      "
            android.util.SparseArray<android.os.Bundle> r5 = r1.mDevicePolicyGlobalUserRestrictions     // Catch:{ all -> 0x01b6 }
            int r6 = r14.id     // Catch:{ all -> 0x01b6 }
            java.lang.Object r5 = r5.get(r6)     // Catch:{ all -> 0x01b6 }
            android.os.Bundle r5 = (android.os.Bundle) r5     // Catch:{ all -> 0x01b6 }
            com.android.server.pm.UserRestrictionsUtils.dumpRestrictions(r10, r0, r5)     // Catch:{ all -> 0x01b6 }
            java.lang.String r0 = "    Device policy local restrictions:"
            r10.println(r0)     // Catch:{ all -> 0x01b6 }
            java.lang.String r0 = "      "
            android.util.SparseArray<android.os.Bundle> r5 = r1.mDevicePolicyLocalUserRestrictions     // Catch:{ all -> 0x01b6 }
            int r6 = r14.id     // Catch:{ all -> 0x01b6 }
            java.lang.Object r5 = r5.get(r6)     // Catch:{ all -> 0x01b6 }
            android.os.Bundle r5 = (android.os.Bundle) r5     // Catch:{ all -> 0x01b6 }
            com.android.server.pm.UserRestrictionsUtils.dumpRestrictions(r10, r0, r5)     // Catch:{ all -> 0x01b6 }
            java.lang.String r0 = "    Effective restrictions:"
            r10.println(r0)     // Catch:{ all -> 0x01b6 }
            java.lang.String r0 = "      "
            android.util.SparseArray<android.os.Bundle> r5 = r1.mCachedEffectiveUserRestrictions     // Catch:{ all -> 0x01b6 }
            int r6 = r14.id     // Catch:{ all -> 0x01b6 }
            java.lang.Object r5 = r5.get(r6)     // Catch:{ all -> 0x01b6 }
            android.os.Bundle r5 = (android.os.Bundle) r5     // Catch:{ all -> 0x01b6 }
            com.android.server.pm.UserRestrictionsUtils.dumpRestrictions(r10, r0, r5)     // Catch:{ all -> 0x01b6 }
            monitor-exit(r2)     // Catch:{ all -> 0x01b6 }
            java.lang.String r0 = r13.account     // Catch:{ all -> 0x02c6 }
            if (r0 == 0) goto L_0x0168
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ all -> 0x02c6 }
            r0.<init>()     // Catch:{ all -> 0x02c6 }
            java.lang.String r2 = "    Account name: "
            r0.append(r2)     // Catch:{ all -> 0x02c6 }
            java.lang.String r2 = r13.account     // Catch:{ all -> 0x02c6 }
            r0.append(r2)     // Catch:{ all -> 0x02c6 }
            java.lang.String r0 = r0.toString()     // Catch:{ all -> 0x02c6 }
            r10.print(r0)     // Catch:{ all -> 0x02c6 }
            r24.println()     // Catch:{ all -> 0x02c6 }
        L_0x0168:
            java.lang.String r0 = r13.seedAccountName     // Catch:{ all -> 0x02c6 }
            if (r0 == 0) goto L_0x01ae
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ all -> 0x02c6 }
            r0.<init>()     // Catch:{ all -> 0x02c6 }
            java.lang.String r2 = "    Seed account name: "
            r0.append(r2)     // Catch:{ all -> 0x02c6 }
            java.lang.String r2 = r13.seedAccountName     // Catch:{ all -> 0x02c6 }
            r0.append(r2)     // Catch:{ all -> 0x02c6 }
            java.lang.String r0 = r0.toString()     // Catch:{ all -> 0x02c6 }
            r10.print(r0)     // Catch:{ all -> 0x02c6 }
            r24.println()     // Catch:{ all -> 0x02c6 }
            java.lang.String r0 = r13.seedAccountType     // Catch:{ all -> 0x02c6 }
            if (r0 == 0) goto L_0x01a2
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ all -> 0x02c6 }
            r0.<init>()     // Catch:{ all -> 0x02c6 }
            java.lang.String r2 = "         account type: "
            r0.append(r2)     // Catch:{ all -> 0x02c6 }
            java.lang.String r2 = r13.seedAccountType     // Catch:{ all -> 0x02c6 }
            r0.append(r2)     // Catch:{ all -> 0x02c6 }
            java.lang.String r0 = r0.toString()     // Catch:{ all -> 0x02c6 }
            r10.print(r0)     // Catch:{ all -> 0x02c6 }
            r24.println()     // Catch:{ all -> 0x02c6 }
        L_0x01a2:
            android.os.PersistableBundle r0 = r13.seedAccountOptions     // Catch:{ all -> 0x02c6 }
            if (r0 == 0) goto L_0x01ae
            java.lang.String r0 = "         account options exist"
            r10.print(r0)     // Catch:{ all -> 0x02c6 }
            r24.println()     // Catch:{ all -> 0x02c6 }
        L_0x01ae:
            int r9 = r21 + 1
            r13 = r18
            r8 = r20
            goto L_0x0029
        L_0x01b6:
            r0 = move-exception
            monitor-exit(r2)     // Catch:{ all -> 0x01b6 }
            throw r0     // Catch:{ all -> 0x02c6 }
        L_0x01b9:
            r0 = move-exception
            r1 = r22
            goto L_0x02c4
        L_0x01be:
            r0 = move-exception
            r1 = r22
            r20 = r8
            goto L_0x02c4
        L_0x01c5:
            r0 = move-exception
            r1 = r22
            goto L_0x02c0
        L_0x01ca:
            r0 = move-exception
            r20 = r8
            r21 = r9
            r18 = r13
            r13 = r6
            r14 = r7
        L_0x01d3:
            monitor-exit(r2)     // Catch:{ all -> 0x01d5 }
            throw r0     // Catch:{ all -> 0x02c6 }
        L_0x01d5:
            r0 = move-exception
            goto L_0x01d3
        L_0x01d7:
            r20 = r8
            r21 = r9
            r18 = r13
            monitor-exit(r20)     // Catch:{ all -> 0x02c6 }
            r24.println()     // Catch:{ all -> 0x02cd }
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ all -> 0x02cd }
            r0.<init>()     // Catch:{ all -> 0x02cd }
            java.lang.String r2 = "  Device owner id:"
            r0.append(r2)     // Catch:{ all -> 0x02cd }
            int r2 = r1.mDeviceOwnerUserId     // Catch:{ all -> 0x02cd }
            r0.append(r2)     // Catch:{ all -> 0x02cd }
            java.lang.String r0 = r0.toString()     // Catch:{ all -> 0x02cd }
            r10.println(r0)     // Catch:{ all -> 0x02cd }
            r24.println()     // Catch:{ all -> 0x02cd }
            java.lang.String r0 = "  Guest restrictions:"
            r10.println(r0)     // Catch:{ all -> 0x02cd }
            android.os.Bundle r2 = r1.mGuestRestrictions     // Catch:{ all -> 0x02cd }
            monitor-enter(r2)     // Catch:{ all -> 0x02cd }
            java.lang.String r0 = "    "
            android.os.Bundle r4 = r1.mGuestRestrictions     // Catch:{ all -> 0x02bc }
            com.android.server.pm.UserRestrictionsUtils.dumpRestrictions(r10, r0, r4)     // Catch:{ all -> 0x02bc }
            monitor-exit(r2)     // Catch:{ all -> 0x02bc }
            java.lang.Object r2 = r1.mUsersLock     // Catch:{ all -> 0x02cd }
            monitor-enter(r2)     // Catch:{ all -> 0x02cd }
            r24.println()     // Catch:{ all -> 0x02b9 }
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ all -> 0x02b9 }
            r0.<init>()     // Catch:{ all -> 0x02b9 }
            java.lang.String r4 = "  Device managed: "
            r0.append(r4)     // Catch:{ all -> 0x02b9 }
            boolean r4 = r1.mIsDeviceManaged     // Catch:{ all -> 0x02b9 }
            r0.append(r4)     // Catch:{ all -> 0x02b9 }
            java.lang.String r0 = r0.toString()     // Catch:{ all -> 0x02b9 }
            r10.println(r0)     // Catch:{ all -> 0x02b9 }
            android.util.SparseBooleanArray r0 = r1.mRemovingUserIds     // Catch:{ all -> 0x02b9 }
            int r0 = r0.size()     // Catch:{ all -> 0x02b9 }
            if (r0 <= 0) goto L_0x0247
            r24.println()     // Catch:{ all -> 0x02b9 }
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ all -> 0x02b9 }
            r0.<init>()     // Catch:{ all -> 0x02b9 }
            java.lang.String r4 = "  Recently removed userIds: "
            r0.append(r4)     // Catch:{ all -> 0x02b9 }
            java.util.LinkedList<java.lang.Integer> r4 = r1.mRecentlyRemovedIds     // Catch:{ all -> 0x02b9 }
            r0.append(r4)     // Catch:{ all -> 0x02b9 }
            java.lang.String r0 = r0.toString()     // Catch:{ all -> 0x02b9 }
            r10.println(r0)     // Catch:{ all -> 0x02b9 }
        L_0x0247:
            monitor-exit(r2)     // Catch:{ all -> 0x02b9 }
            android.util.SparseIntArray r2 = r1.mUserStates     // Catch:{ all -> 0x02cd }
            monitor-enter(r2)     // Catch:{ all -> 0x02cd }
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ all -> 0x02b6 }
            r0.<init>()     // Catch:{ all -> 0x02b6 }
            java.lang.String r4 = "  Started users state: "
            r0.append(r4)     // Catch:{ all -> 0x02b6 }
            android.util.SparseIntArray r4 = r1.mUserStates     // Catch:{ all -> 0x02b6 }
            r0.append(r4)     // Catch:{ all -> 0x02b6 }
            java.lang.String r0 = r0.toString()     // Catch:{ all -> 0x02b6 }
            r10.println(r0)     // Catch:{ all -> 0x02b6 }
            monitor-exit(r2)     // Catch:{ all -> 0x02b6 }
            r24.println()     // Catch:{ all -> 0x02cd }
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ all -> 0x02cd }
            r0.<init>()     // Catch:{ all -> 0x02cd }
            java.lang.String r2 = "  Max users: "
            r0.append(r2)     // Catch:{ all -> 0x02cd }
            int r2 = android.os.UserManager.getMaxSupportedUsers()     // Catch:{ all -> 0x02cd }
            r0.append(r2)     // Catch:{ all -> 0x02cd }
            java.lang.String r0 = r0.toString()     // Catch:{ all -> 0x02cd }
            r10.println(r0)     // Catch:{ all -> 0x02cd }
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ all -> 0x02cd }
            r0.<init>()     // Catch:{ all -> 0x02cd }
            java.lang.String r2 = "  Supports switchable users: "
            r0.append(r2)     // Catch:{ all -> 0x02cd }
            boolean r2 = android.os.UserManager.supportsMultipleUsers()     // Catch:{ all -> 0x02cd }
            r0.append(r2)     // Catch:{ all -> 0x02cd }
            java.lang.String r0 = r0.toString()     // Catch:{ all -> 0x02cd }
            r10.println(r0)     // Catch:{ all -> 0x02cd }
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ all -> 0x02cd }
            r0.<init>()     // Catch:{ all -> 0x02cd }
            java.lang.String r2 = "  All guests ephemeral: "
            r0.append(r2)     // Catch:{ all -> 0x02cd }
            android.content.res.Resources r2 = android.content.res.Resources.getSystem()     // Catch:{ all -> 0x02cd }
            r4 = 17891464(0x1110088, float:2.6632675E-38)
            boolean r2 = r2.getBoolean(r4)     // Catch:{ all -> 0x02cd }
            r0.append(r2)     // Catch:{ all -> 0x02cd }
            java.lang.String r0 = r0.toString()     // Catch:{ all -> 0x02cd }
            r10.println(r0)     // Catch:{ all -> 0x02cd }
            monitor-exit(r15)     // Catch:{ all -> 0x02cd }
            return
        L_0x02b6:
            r0 = move-exception
            monitor-exit(r2)     // Catch:{ all -> 0x02b6 }
            throw r0     // Catch:{ all -> 0x02cd }
        L_0x02b9:
            r0 = move-exception
            monitor-exit(r2)     // Catch:{ all -> 0x02b9 }
            throw r0     // Catch:{ all -> 0x02cd }
        L_0x02bc:
            r0 = move-exception
            monitor-exit(r2)     // Catch:{ all -> 0x02bc }
            throw r0     // Catch:{ all -> 0x02cd }
        L_0x02bf:
            r0 = move-exception
        L_0x02c0:
            r20 = r8
            r18 = r13
        L_0x02c4:
            monitor-exit(r20)     // Catch:{ all -> 0x02c6 }
            throw r0     // Catch:{ all -> 0x02cd }
        L_0x02c6:
            r0 = move-exception
            goto L_0x02c4
        L_0x02c8:
            r0 = move-exception
            r18 = r13
        L_0x02cb:
            monitor-exit(r15)     // Catch:{ all -> 0x02cd }
            throw r0
        L_0x02cd:
            r0 = move-exception
            goto L_0x02cb
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.pm.UserManagerService.dump(java.io.FileDescriptor, java.io.PrintWriter, java.lang.String[]):void");
    }

    private static void dumpTimeAgo(PrintWriter pw, StringBuilder sb, long nowTime, long time) {
        if (time == 0) {
            pw.println("<unknown>");
            return;
        }
        sb.setLength(0);
        TimeUtils.formatDuration(nowTime - time, sb);
        sb.append(" ago");
        pw.println(sb);
    }

    final class MainHandler extends Handler {
        MainHandler() {
        }

        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                removeMessages(1, msg.obj);
                synchronized (UserManagerService.this.mPackagesLock) {
                    UserData userData = UserManagerService.this.getUserDataNoChecks(((UserData) msg.obj).info.id);
                    if (userData != null) {
                        UserManagerService.this.writeUserLP(userData);
                    }
                }
            }
        }
    }

    /* access modifiers changed from: package-private */
    public boolean isUserInitialized(int userId) {
        return this.mLocalService.isUserInitialized(userId);
    }

    private class LocalService extends UserManagerInternal {
        private LocalService() {
        }

        public void setDevicePolicyUserRestrictions(int userId, Bundle restrictions, boolean isDeviceOwner, int cameraRestrictionScope) {
            UserManagerService.this.setDevicePolicyUserRestrictionsInner(userId, restrictions, isDeviceOwner, cameraRestrictionScope);
        }

        public Bundle getBaseUserRestrictions(int userId) {
            Bundle bundle;
            synchronized (UserManagerService.this.mRestrictionsLock) {
                bundle = (Bundle) UserManagerService.this.mBaseUserRestrictions.get(userId);
            }
            return bundle;
        }

        public void setBaseUserRestrictionsByDpmsForMigration(int userId, Bundle baseRestrictions) {
            synchronized (UserManagerService.this.mRestrictionsLock) {
                if (UserManagerService.this.updateRestrictionsIfNeededLR(userId, new Bundle(baseRestrictions), UserManagerService.this.mBaseUserRestrictions)) {
                    UserManagerService.this.invalidateEffectiveUserRestrictionsLR(userId);
                }
            }
            UserData userData = UserManagerService.this.getUserDataNoChecks(userId);
            synchronized (UserManagerService.this.mPackagesLock) {
                if (userData != null) {
                    UserManagerService.this.writeUserLP(userData);
                } else {
                    Slog.w(UserManagerService.LOG_TAG, "UserInfo not found for " + userId);
                }
            }
        }

        public boolean getUserRestriction(int userId, String key) {
            return UserManagerService.this.getUserRestrictions(userId).getBoolean(key);
        }

        public void addUserRestrictionsListener(UserManagerInternal.UserRestrictionsListener listener) {
            synchronized (UserManagerService.this.mUserRestrictionsListeners) {
                UserManagerService.this.mUserRestrictionsListeners.add(listener);
            }
        }

        public void removeUserRestrictionsListener(UserManagerInternal.UserRestrictionsListener listener) {
            synchronized (UserManagerService.this.mUserRestrictionsListeners) {
                UserManagerService.this.mUserRestrictionsListeners.remove(listener);
            }
        }

        public void setDeviceManaged(boolean isManaged) {
            synchronized (UserManagerService.this.mUsersLock) {
                boolean unused = UserManagerService.this.mIsDeviceManaged = isManaged;
            }
        }

        public void setUserManaged(int userId, boolean isManaged) {
            synchronized (UserManagerService.this.mUsersLock) {
                UserManagerService.this.mIsUserManaged.put(userId, isManaged);
            }
        }

        /* Debug info: failed to restart local var, previous not found, register: 7 */
        public void setUserIcon(int userId, Bitmap bitmap) {
            long ident = Binder.clearCallingIdentity();
            try {
                synchronized (UserManagerService.this.mPackagesLock) {
                    UserData userData = UserManagerService.this.getUserDataNoChecks(userId);
                    if (userData != null) {
                        if (!userData.info.partial) {
                            UserManagerService.this.writeBitmapLP(userData.info, bitmap);
                            UserManagerService.this.writeUserLP(userData);
                            UserManagerService.this.sendUserInfoChangedBroadcast(userId);
                            Binder.restoreCallingIdentity(ident);
                            return;
                        }
                    }
                    Slog.w(UserManagerService.LOG_TAG, "setUserIcon: unknown user #" + userId);
                    Binder.restoreCallingIdentity(ident);
                }
            } catch (Throwable th) {
                Binder.restoreCallingIdentity(ident);
                throw th;
            }
        }

        public void setForceEphemeralUsers(boolean forceEphemeralUsers) {
            synchronized (UserManagerService.this.mUsersLock) {
                boolean unused = UserManagerService.this.mForceEphemeralUsers = forceEphemeralUsers;
            }
        }

        public void removeAllUsers() {
            if (ActivityManager.getCurrentUser() == 0) {
                UserManagerService.this.removeNonSystemUsers();
                return;
            }
            BroadcastReceiver userSwitchedReceiver = new BroadcastReceiver() {
                public void onReceive(Context context, Intent intent) {
                    if (intent.getIntExtra("android.intent.extra.user_handle", ScreenRotationAnimationInjector.BLACK_SURFACE_INVALID_POSITION) == 0) {
                        UserManagerService.this.mContext.unregisterReceiver(this);
                        UserManagerService.this.removeNonSystemUsers();
                    }
                }
            };
            IntentFilter userSwitchedFilter = new IntentFilter();
            userSwitchedFilter.addAction("android.intent.action.USER_SWITCHED");
            UserManagerService.this.mContext.registerReceiver(userSwitchedReceiver, userSwitchedFilter, (String) null, UserManagerService.this.mHandler);
            ((ActivityManager) UserManagerService.this.mContext.getSystemService("activity")).switchUser(0);
        }

        public void onEphemeralUserStop(int userId) {
            synchronized (UserManagerService.this.mUsersLock) {
                UserInfo userInfo = UserManagerService.this.getUserInfoLU(userId);
                if (userInfo != null && userInfo.isEphemeral()) {
                    userInfo.flags |= 64;
                    if (userInfo.isGuest()) {
                        userInfo.guestToRemove = true;
                    }
                }
            }
        }

        public UserInfo createUserEvenWhenDisallowed(String name, int flags, String[] disallowedPackages) {
            UserInfo user = UserManagerService.this.createUserInternalUnchecked(name, flags, ScreenRotationAnimationInjector.BLACK_SURFACE_INVALID_POSITION, disallowedPackages);
            if (user != null && !user.isAdmin() && !user.isDemo()) {
                UserManagerService.this.setUserRestriction("no_sms", true, user.id);
                UserManagerService.this.setUserRestriction("no_outgoing_calls", true, user.id);
            }
            return user;
        }

        public boolean removeUserEvenWhenDisallowed(int userId) {
            return UserManagerService.this.removeUserUnchecked(userId);
        }

        public boolean isUserRunning(int userId) {
            boolean z;
            synchronized (UserManagerService.this.mUserStates) {
                z = UserManagerService.this.mUserStates.get(userId, -1) >= 0;
            }
            return z;
        }

        public void setUserState(int userId, int userState) {
            synchronized (UserManagerService.this.mUserStates) {
                UserManagerService.this.mUserStates.put(userId, userState);
            }
        }

        public void removeUserState(int userId) {
            synchronized (UserManagerService.this.mUserStates) {
                UserManagerService.this.mUserStates.delete(userId);
            }
        }

        public int[] getUserIds() {
            return UserManagerService.this.getUserIds();
        }

        public boolean isUserUnlockingOrUnlocked(int userId) {
            int state;
            synchronized (UserManagerService.this.mUserStates) {
                state = UserManagerService.this.mUserStates.get(userId, -1);
            }
            if (state == 4 || state == 5) {
                return StorageManager.isUserKeyUnlocked(userId);
            }
            return state == 2 || state == 3;
        }

        public boolean isUserUnlocked(int userId) {
            int state;
            synchronized (UserManagerService.this.mUserStates) {
                state = UserManagerService.this.mUserStates.get(userId, -1);
            }
            if (state == 4 || state == 5) {
                return StorageManager.isUserKeyUnlocked(userId);
            }
            return state == 3;
        }

        public boolean isUserInitialized(int userId) {
            return (UserManagerService.this.getUserInfo(userId).flags & 16) != 0;
        }

        public boolean exists(int userId) {
            return UserManagerService.this.getUserInfoNoChecks(userId) != null;
        }

        /* Debug info: failed to restart local var, previous not found, register: 7 */
        /* JADX WARNING: Code restructure failed: missing block: B:31:0x007c, code lost:
            return false;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public boolean isProfileAccessible(int r8, int r9, java.lang.String r10, boolean r11) {
            /*
                r7 = this;
                r0 = 1
                if (r9 != r8) goto L_0x0004
                return r0
            L_0x0004:
                com.android.server.pm.UserManagerService r1 = com.android.server.pm.UserManagerService.this
                java.lang.Object r1 = r1.mUsersLock
                monitor-enter(r1)
                com.android.server.pm.UserManagerService r2 = com.android.server.pm.UserManagerService.this     // Catch:{ all -> 0x009f }
                android.content.pm.UserInfo r2 = r2.getUserInfoLU(r8)     // Catch:{ all -> 0x009f }
                if (r2 == 0) goto L_0x0019
                boolean r3 = r2.isManagedProfile()     // Catch:{ all -> 0x009f }
                if (r3 == 0) goto L_0x001b
            L_0x0019:
                if (r11 != 0) goto L_0x007d
            L_0x001b:
                com.android.server.pm.UserManagerService r3 = com.android.server.pm.UserManagerService.this     // Catch:{ all -> 0x009f }
                android.content.pm.UserInfo r3 = r3.getUserInfoLU(r9)     // Catch:{ all -> 0x009f }
                r4 = 0
                if (r3 == 0) goto L_0x0058
                boolean r5 = r3.isEnabled()     // Catch:{ all -> 0x009f }
                if (r5 != 0) goto L_0x002b
                goto L_0x0058
            L_0x002b:
                int r5 = r3.profileGroupId     // Catch:{ all -> 0x009f }
                r6 = -10000(0xffffffffffffd8f0, float:NaN)
                if (r5 == r6) goto L_0x003a
                int r5 = r3.profileGroupId     // Catch:{ all -> 0x009f }
                int r6 = r2.profileGroupId     // Catch:{ all -> 0x009f }
                if (r5 == r6) goto L_0x0038
                goto L_0x003a
            L_0x0038:
                monitor-exit(r1)     // Catch:{ all -> 0x009f }
                return r0
            L_0x003a:
                if (r11 != 0) goto L_0x003e
                monitor-exit(r1)     // Catch:{ all -> 0x009f }
                return r4
            L_0x003e:
                java.lang.SecurityException r0 = new java.lang.SecurityException     // Catch:{ all -> 0x009f }
                java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ all -> 0x009f }
                r4.<init>()     // Catch:{ all -> 0x009f }
                r4.append(r10)     // Catch:{ all -> 0x009f }
                java.lang.String r5 = " for unrelated profile "
                r4.append(r5)     // Catch:{ all -> 0x009f }
                r4.append(r9)     // Catch:{ all -> 0x009f }
                java.lang.String r4 = r4.toString()     // Catch:{ all -> 0x009f }
                r0.<init>(r4)     // Catch:{ all -> 0x009f }
                throw r0     // Catch:{ all -> 0x009f }
            L_0x0058:
                if (r11 == 0) goto L_0x007b
                java.lang.String r0 = "UserManagerService"
                java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ all -> 0x009f }
                r5.<init>()     // Catch:{ all -> 0x009f }
                r5.append(r10)     // Catch:{ all -> 0x009f }
                java.lang.String r6 = " for disabled profile "
                r5.append(r6)     // Catch:{ all -> 0x009f }
                r5.append(r9)     // Catch:{ all -> 0x009f }
                java.lang.String r6 = " from "
                r5.append(r6)     // Catch:{ all -> 0x009f }
                r5.append(r8)     // Catch:{ all -> 0x009f }
                java.lang.String r5 = r5.toString()     // Catch:{ all -> 0x009f }
                android.util.Slog.w(r0, r5)     // Catch:{ all -> 0x009f }
            L_0x007b:
                monitor-exit(r1)     // Catch:{ all -> 0x009f }
                return r4
            L_0x007d:
                java.lang.SecurityException r0 = new java.lang.SecurityException     // Catch:{ all -> 0x009f }
                java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ all -> 0x009f }
                r3.<init>()     // Catch:{ all -> 0x009f }
                r3.append(r10)     // Catch:{ all -> 0x009f }
                java.lang.String r4 = " for another profile "
                r3.append(r4)     // Catch:{ all -> 0x009f }
                r3.append(r9)     // Catch:{ all -> 0x009f }
                java.lang.String r4 = " from "
                r3.append(r4)     // Catch:{ all -> 0x009f }
                r3.append(r8)     // Catch:{ all -> 0x009f }
                java.lang.String r3 = r3.toString()     // Catch:{ all -> 0x009f }
                r0.<init>(r3)     // Catch:{ all -> 0x009f }
                throw r0     // Catch:{ all -> 0x009f }
            L_0x009f:
                r0 = move-exception
                monitor-exit(r1)     // Catch:{ all -> 0x009f }
                throw r0
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.server.pm.UserManagerService.LocalService.isProfileAccessible(int, int, java.lang.String, boolean):boolean");
        }

        public int getProfileParentId(int userId) {
            synchronized (UserManagerService.this.mUsersLock) {
                UserInfo profileParent = UserManagerService.this.getProfileParentLU(userId);
                if (profileParent == null) {
                    return userId;
                }
                int i = profileParent.id;
                return i;
            }
        }

        public boolean isSettingRestrictedForUser(String setting, int userId, String value, int callingUid) {
            return UserRestrictionsUtils.isSettingRestrictedForUser(UserManagerService.this.mContext, setting, userId, value, callingUid);
        }
    }

    /* access modifiers changed from: private */
    public void removeNonSystemUsers() {
        ArrayList<UserInfo> usersToRemove = new ArrayList<>();
        synchronized (this.mUsersLock) {
            int userSize = this.mUsers.size();
            for (int i = 0; i < userSize; i++) {
                UserInfo ui = this.mUsers.valueAt(i).info;
                if (ui.id != 0) {
                    usersToRemove.add(ui);
                }
            }
        }
        Iterator<UserInfo> it = usersToRemove.iterator();
        while (it.hasNext()) {
            removeUser(it.next().id);
        }
    }

    private class Shell extends ShellCommand {
        private Shell() {
        }

        public int onCommand(String cmd) {
            return UserManagerService.this.onShellCommand(this, cmd);
        }

        public void onHelp() {
            PrintWriter pw = getOutPrintWriter();
            pw.println("User manager (user) commands:");
            pw.println("  help");
            pw.println("    Print this help text.");
            pw.println("");
            pw.println("  list");
            pw.println("    Prints all users on the system.");
        }
    }

    private static void debug(String message) {
        Log.d(LOG_TAG, message + "");
    }

    @VisibleForTesting
    static int getMaxManagedProfiles() {
        if (!android.os.Build.IS_DEBUGGABLE) {
            return MAX_MANAGED_PROFILES;
        }
        return SystemProperties.getInt("persist.sys.max_profiles", MAX_MANAGED_PROFILES);
    }

    /* access modifiers changed from: package-private */
    @GuardedBy({"mUsersLock"})
    @VisibleForTesting
    public int getFreeProfileBadgeLU(int parentUserId) {
        int maxManagedProfiles = getMaxManagedProfiles();
        boolean[] usedBadges = new boolean[maxManagedProfiles];
        int userSize = this.mUsers.size();
        for (int i = 0; i < userSize; i++) {
            UserInfo ui = this.mUsers.valueAt(i).info;
            if (ui.isManagedProfile() && ui.profileGroupId == parentUserId && !this.mRemovingUserIds.get(ui.id) && ui.profileBadge < maxManagedProfiles) {
                usedBadges[ui.profileBadge] = true;
            }
        }
        for (int i2 = 0; i2 < maxManagedProfiles; i2++) {
            if (!usedBadges[i2]) {
                return i2;
            }
        }
        return 0;
    }

    /* access modifiers changed from: package-private */
    public boolean hasManagedProfile(int userId) {
        synchronized (this.mUsersLock) {
            UserInfo userInfo = getUserInfoLU(userId);
            int userSize = this.mUsers.size();
            for (int i = 0; i < userSize; i++) {
                UserInfo profile = this.mUsers.valueAt(i).info;
                if (userId != profile.id && isProfileOf(userInfo, profile)) {
                    return true;
                }
            }
            return false;
        }
    }

    private void verifyCallingPackage(String callingPackage, int callingUid) {
        if (this.mPm.getPackageUid(callingPackage, 0, UserHandle.getUserId(callingUid)) != callingUid) {
            throw new SecurityException("Specified package " + callingPackage + " does not match the calling uid " + callingUid);
        }
    }
}
