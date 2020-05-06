package com.android.server.locksettings;

import android.app.ActivityManager;
import android.app.IActivityManager;
import android.app.KeyguardManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.admin.DevicePolicyManager;
import android.app.admin.DevicePolicyManagerInternal;
import android.app.admin.PasswordMetrics;
import android.app.backup.BackupManager;
import android.app.trust.IStrongAuthTracker;
import android.app.trust.TrustManager;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.UserInfo;
import android.content.res.Resources;
import android.database.ContentObserver;
import android.database.sqlite.SQLiteDatabase;
import android.hardware.authsecret.V1_0.IAuthSecret;
import android.hardware.biometrics.BiometricManager;
import android.hardware.face.FaceManager;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.IProgressListener;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.StrictMode;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.os.UserManager;
import android.os.storage.IStorageManager;
import android.os.storage.StorageManager;
import android.provider.Settings;
import android.security.KeyStore;
import android.security.MiuiLockPatternUtils;
import android.security.keystore.AndroidKeyStoreProvider;
import android.security.keystore.KeyProtection;
import android.security.keystore.UserNotAuthenticatedException;
import android.security.keystore.recovery.KeyChainProtectionParams;
import android.security.keystore.recovery.KeyChainSnapshot;
import android.security.keystore.recovery.RecoveryCertPath;
import android.security.keystore.recovery.WrappedApplicationKey;
import android.server.am.SplitScreenReporter;
import android.service.gatekeeper.GateKeeperResponse;
import android.service.gatekeeper.IGateKeeperService;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.ArraySet;
import android.util.EventLog;
import android.util.Log;
import android.util.Slog;
import android.util.SparseArray;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.notification.SystemNotificationChannels;
import com.android.internal.util.ArrayUtils;
import com.android.internal.util.DumpUtils;
import com.android.internal.util.Preconditions;
import com.android.internal.widget.ICheckCredentialProgressCallback;
import com.android.internal.widget.ILockSettings;
import com.android.internal.widget.LockPatternUtils;
import com.android.internal.widget.LockSettingsInternal;
import com.android.internal.widget.VerifyCredentialResponse;
import com.android.server.LocalServices;
import com.android.server.SystemService;
import com.android.server.backup.UserBackupManagerService;
import com.android.server.locksettings.LockSettingsStorage;
import com.android.server.locksettings.SyntheticPasswordManager;
import com.android.server.locksettings.recoverablekeystore.RecoverableKeyStoreManager;
import com.android.server.pm.DumpState;
import com.android.server.wm.WindowManagerInternal;
import java.io.ByteArrayOutputStream;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import libcore.util.HexEncoding;
import miui.securityspace.XSpaceUserHandle;

public class LockSettingsService extends ILockSettings.Stub {
    private static final boolean DEBUG = false;
    private static final String DEFAULT_PASSWORD = "default_password";
    private static final String GSI_RUNNING_PROP = "ro.gsid.image_running";
    private static final String PERMISSION = "android.permission.ACCESS_KEYGUARD_SECURE_STORAGE";
    private static final int PROFILE_KEY_IV_SIZE = 12;
    private static final String[] READ_CONTACTS_PROTECTED_SETTINGS = {"lock_screen_owner_info_enabled", "lock_screen_owner_info"};
    private static final String[] READ_PASSWORD_PROTECTED_SETTINGS = {"lockscreen.password_salt", "lockscreen.passwordhistory", "lockscreen.password_type", SEPARATE_PROFILE_CHALLENGE_KEY};
    private static final String SEPARATE_PROFILE_CHALLENGE_KEY = "lockscreen.profilechallenge";
    private static final String[] SETTINGS_TO_BACKUP = {"lock_screen_owner_info_enabled", "lock_screen_owner_info", "lock_pattern_visible_pattern", "lockscreen.power_button_instantly_locks"};
    private static final int SYNTHETIC_PASSWORD_ENABLED_BY_DEFAULT = 1;
    private static final int[] SYSTEM_CREDENTIAL_UIDS = {1010, 1016, 0, 1000};
    private static final String TAG = "LockSettingsService";
    private static final String[] VALID_SETTINGS = {"lockscreen.lockedoutpermanently", "lockscreen.patterneverchosen", "lockscreen.password_type", "lockscreen.password_type_alternate", "lockscreen.password_salt", "lockscreen.disabled", "lockscreen.options", "lockscreen.biometric_weak_fallback", "lockscreen.biometricweakeverchosen", "lockscreen.power_button_instantly_locks", "lockscreen.passwordhistory", "lock_pattern_autolock", "lock_biometric_weak_flags", "lock_pattern_visible_pattern", "lock_pattern_tactile_feedback_enabled"};
    private static String mSavePassword = DEFAULT_PASSWORD;
    private final IActivityManager mActivityManager;
    protected IAuthSecret mAuthSecretService;
    private final BroadcastReceiver mBroadcastReceiver;
    /* access modifiers changed from: private */
    public final Context mContext;
    private final DeviceProvisionedObserver mDeviceProvisionedObserver;
    private boolean mFirstCallToVold;
    protected IGateKeeperService mGateKeeperService;
    @VisibleForTesting
    protected final Handler mHandler;
    private final Injector mInjector;
    private final KeyStore mKeyStore;
    /* access modifiers changed from: private */
    public final LockPatternUtils mLockPatternUtils;
    private final NotificationManager mNotificationManager;
    private final RecoverableKeyStoreManager mRecoverableKeyStoreManager;
    private final Object mSeparateChallengeLock;
    @GuardedBy({"mSpManager"})
    private SparseArray<SyntheticPasswordManager.AuthenticationToken> mSpCache;
    private final SyntheticPasswordManager mSpManager;
    @VisibleForTesting
    protected final LockSettingsStorage mStorage;
    private final IStorageManager mStorageManager;
    private final LockSettingsStrongAuth mStrongAuth;
    private final SynchronizedStrongAuthTracker mStrongAuthTracker;
    /* access modifiers changed from: private */
    public final UserManager mUserManager;

    public static final class Lifecycle extends SystemService {
        private LockSettingsService mLockSettingsService;

        public Lifecycle(Context context) {
            super(context);
        }

        /* JADX WARNING: type inference failed for: r0v1, types: [com.android.server.locksettings.LockSettingsService, android.os.IBinder] */
        public void onStart() {
            AndroidKeyStoreProvider.install();
            this.mLockSettingsService = new LockSettingsService(getContext());
            publishBinderService("lock_settings", this.mLockSettingsService);
        }

        public void onBootPhase(int phase) {
            super.onBootPhase(phase);
            if (phase == 550) {
                this.mLockSettingsService.migrateOldDataAfterSystemReady();
            }
        }

        public void onStartUser(int userHandle) {
            this.mLockSettingsService.onStartUser(userHandle);
        }

        public void onUnlockUser(int userHandle) {
            this.mLockSettingsService.onUnlockUser(userHandle);
        }

        public void onCleanupUser(int userHandle) {
            this.mLockSettingsService.onCleanupUser(userHandle);
        }
    }

    @VisibleForTesting
    protected static class SynchronizedStrongAuthTracker extends LockPatternUtils.StrongAuthTracker {
        public SynchronizedStrongAuthTracker(Context context) {
            super(context);
        }

        /* access modifiers changed from: protected */
        public void handleStrongAuthRequiredChanged(int strongAuthFlags, int userId) {
            synchronized (this) {
                LockSettingsService.super.handleStrongAuthRequiredChanged(strongAuthFlags, userId);
            }
        }

        public int getStrongAuthForUser(int userId) {
            int strongAuthForUser;
            synchronized (this) {
                strongAuthForUser = LockSettingsService.super.getStrongAuthForUser(userId);
            }
            return strongAuthForUser;
        }

        /* access modifiers changed from: package-private */
        public void register(LockSettingsStrongAuth strongAuth) {
            strongAuth.registerStrongAuthTracker(this.mStub);
        }
    }

    public void tieManagedProfileLockIfNecessary(int managedUserId, byte[] managedUserPassword) {
        int i = managedUserId;
        if ((this.mUserManager.getUserInfo(i).isManagedProfile() || XSpaceUserHandle.isXSpaceUserId(managedUserId)) && !this.mLockPatternUtils.isSeparateProfileChallengeEnabled(i) && !this.mStorage.hasChildProfileLock(i)) {
            int parentId = this.mUserManager.getProfileParent(i).id;
            if (isUserSecure(parentId)) {
                try {
                    if (getGateKeeperService().getSecureUserId(parentId) != 0) {
                        try {
                            try {
                                char[] newPasswordChars = HexEncoding.encode(SecureRandom.getInstance("SHA1PRNG").generateSeed(40));
                                byte[] newPassword = new byte[newPasswordChars.length];
                                for (int i2 = 0; i2 < newPasswordChars.length; i2++) {
                                    newPassword[i2] = (byte) newPasswordChars[i2];
                                }
                                Arrays.fill(newPasswordChars, 0);
                                setLockCredentialInternal(newPassword, 2, managedUserPassword, 327680, managedUserId, false, true);
                                setLong("lockscreen.password_type", 327680, i);
                                tieProfileLockToParent(i, newPassword);
                                Arrays.fill(newPassword, (byte) 0);
                            } catch (RemoteException | NoSuchAlgorithmException e) {
                                e = e;
                                Slog.e(TAG, "Fail to tie managed profile", e);
                            }
                        } catch (RemoteException | NoSuchAlgorithmException e2) {
                            e = e2;
                            byte[] bArr = new byte[0];
                            Slog.e(TAG, "Fail to tie managed profile", e);
                        }
                    }
                } catch (RemoteException e3) {
                    Slog.e(TAG, "Failed to talk to GateKeeper service", e3);
                }
            }
        }
    }

    static class Injector {
        protected Context mContext;

        public Injector(Context context) {
            this.mContext = context;
        }

        public Context getContext() {
            return this.mContext;
        }

        public Handler getHandler() {
            return new Handler();
        }

        public LockSettingsStorage getStorage() {
            final LockSettingsStorage storage = new LockSettingsStorage(this.mContext);
            storage.setDatabaseOnCreateCallback(new LockSettingsStorage.Callback() {
                public void initialize(SQLiteDatabase db) {
                    if (SystemProperties.getBoolean("ro.lockscreen.disable.default", false)) {
                        storage.writeKeyValue(db, "lockscreen.disabled", SplitScreenReporter.ACTION_ENTER_SPLIT, 0);
                    }
                }
            });
            return storage;
        }

        public LockSettingsStrongAuth getStrongAuth() {
            return new LockSettingsStrongAuth(this.mContext);
        }

        public SynchronizedStrongAuthTracker getStrongAuthTracker() {
            return new SynchronizedStrongAuthTracker(this.mContext);
        }

        public IActivityManager getActivityManager() {
            return ActivityManager.getService();
        }

        public LockPatternUtils getLockPatternUtils() {
            return new LockPatternUtils(this.mContext);
        }

        public NotificationManager getNotificationManager() {
            return (NotificationManager) this.mContext.getSystemService("notification");
        }

        public UserManager getUserManager() {
            return (UserManager) this.mContext.getSystemService("user");
        }

        public DevicePolicyManager getDevicePolicyManager() {
            return (DevicePolicyManager) this.mContext.getSystemService("device_policy");
        }

        public KeyStore getKeyStore() {
            return KeyStore.getInstance();
        }

        public RecoverableKeyStoreManager getRecoverableKeyStoreManager(KeyStore keyStore) {
            return RecoverableKeyStoreManager.getInstance(this.mContext, keyStore);
        }

        public IStorageManager getStorageManager() {
            IBinder service = ServiceManager.getService("mount");
            if (service != null) {
                return IStorageManager.Stub.asInterface(service);
            }
            return null;
        }

        public SyntheticPasswordManager getSyntheticPasswordManager(LockSettingsStorage storage) {
            return new SyntheticPasswordManager(getContext(), storage, getUserManager(), new PasswordSlotManager());
        }

        public boolean hasEnrolledBiometrics() {
            return ((BiometricManager) this.mContext.getSystemService(BiometricManager.class)).canAuthenticate() == 0;
        }

        public int binderGetCallingUid() {
            return Binder.getCallingUid();
        }

        public boolean isGsiRunning() {
            return SystemProperties.getInt(LockSettingsService.GSI_RUNNING_PROP, 0) > 0;
        }
    }

    public LockSettingsService(Context context) {
        this(new Injector(context));
    }

    @VisibleForTesting
    protected LockSettingsService(Injector injector) {
        this.mSeparateChallengeLock = new Object();
        this.mDeviceProvisionedObserver = new DeviceProvisionedObserver();
        this.mBroadcastReceiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                int userHandle;
                if ("android.intent.action.USER_ADDED".equals(intent.getAction())) {
                    int userHandle2 = intent.getIntExtra("android.intent.extra.user_handle", 0);
                    if (userHandle2 > 0) {
                        LockSettingsService.this.removeUser(userHandle2, true);
                    }
                    KeyStore ks = KeyStore.getInstance();
                    UserInfo parentInfo = LockSettingsService.this.mUserManager.getProfileParent(userHandle2);
                    ks.onUserAdded(userHandle2, parentInfo != null ? parentInfo.id : -1);
                } else if ("android.intent.action.USER_STARTING".equals(intent.getAction())) {
                    LockSettingsService.this.mStorage.prefetchUser(intent.getIntExtra("android.intent.extra.user_handle", 0));
                } else if ("android.intent.action.USER_REMOVED".equals(intent.getAction()) && (userHandle = intent.getIntExtra("android.intent.extra.user_handle", 0)) > 0) {
                    LockSettingsService.this.removeUser(userHandle, false);
                }
            }
        };
        this.mSpCache = new SparseArray<>();
        this.mInjector = injector;
        this.mContext = injector.getContext();
        this.mKeyStore = injector.getKeyStore();
        this.mRecoverableKeyStoreManager = injector.getRecoverableKeyStoreManager(this.mKeyStore);
        this.mHandler = injector.getHandler();
        this.mStrongAuth = injector.getStrongAuth();
        this.mActivityManager = injector.getActivityManager();
        this.mLockPatternUtils = injector.getLockPatternUtils();
        this.mFirstCallToVold = true;
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.intent.action.USER_ADDED");
        filter.addAction("android.intent.action.USER_STARTING");
        filter.addAction("android.intent.action.USER_REMOVED");
        injector.getContext().registerReceiverAsUser(this.mBroadcastReceiver, UserHandle.ALL, filter, (String) null, (Handler) null);
        this.mStorage = injector.getStorage();
        this.mNotificationManager = injector.getNotificationManager();
        this.mUserManager = injector.getUserManager();
        this.mStorageManager = injector.getStorageManager();
        this.mStrongAuthTracker = injector.getStrongAuthTracker();
        this.mStrongAuthTracker.register(this.mStrongAuth);
        this.mSpManager = injector.getSyntheticPasswordManager(this.mStorage);
        LocalServices.addService(LockSettingsInternal.class, new LocalService());
    }

    private void maybeShowEncryptionNotificationForUser(int userId) {
        UserInfo parent;
        UserInfo user = this.mUserManager.getUserInfo(userId);
        if (user.isManagedProfile() && !isUserKeyUnlocked(userId)) {
            UserHandle userHandle = user.getUserHandle();
            if (isUserSecure(userId) && !this.mUserManager.isUserUnlockingOrUnlocked(userHandle) && (parent = this.mUserManager.getProfileParent(userId)) != null && this.mUserManager.isUserUnlockingOrUnlocked(parent.getUserHandle()) && !this.mUserManager.isQuietModeEnabled(userHandle)) {
                showEncryptionNotificationForProfile(userHandle);
            }
        }
    }

    private void showEncryptionNotificationForProfile(UserHandle user) {
        Resources r = this.mContext.getResources();
        CharSequence title = r.getText(17040979);
        CharSequence message = r.getText(17040978);
        CharSequence detail = r.getText(17040977);
        Intent unlockIntent = ((KeyguardManager) this.mContext.getSystemService("keyguard")).createConfirmDeviceCredentialIntent((CharSequence) null, (CharSequence) null, user.getIdentifier());
        if (unlockIntent != null) {
            unlockIntent.setFlags(276824064);
            showEncryptionNotification(user, title, message, detail, PendingIntent.getActivity(this.mContext, 0, unlockIntent, 134217728));
        }
    }

    private void showEncryptionNotification(UserHandle user, CharSequence title, CharSequence message, CharSequence detail, PendingIntent intent) {
        if (StorageManager.isFileEncryptedNativeOrEmulated()) {
            this.mNotificationManager.notifyAsUser((String) null, 9, new Notification.Builder(this.mContext, SystemNotificationChannels.DEVICE_ADMIN).setSmallIcon(17302846).setWhen(0).setOngoing(true).setTicker(title).setColor(this.mContext.getColor(17170460)).setContentTitle(title).setContentText(message).setSubText(detail).setVisibility(1).setContentIntent(intent).build(), user);
        }
    }

    /* access modifiers changed from: private */
    public void hideEncryptionNotification(UserHandle userHandle) {
        this.mNotificationManager.cancelAsUser((String) null, 9, userHandle);
    }

    public void onCleanupUser(int userId) {
        hideEncryptionNotification(new UserHandle(userId));
        requireStrongAuth(LockPatternUtils.StrongAuthTracker.getDefaultFlags(this.mContext), userId);
    }

    public void onStartUser(int userId) {
        maybeShowEncryptionNotificationForUser(userId);
    }

    /* access modifiers changed from: private */
    public void ensureProfileKeystoreUnlocked(int userId) {
        if (KeyStore.getInstance().state(userId) == KeyStore.State.LOCKED && tiedManagedProfileReadyToUnlock(this.mUserManager.getUserInfo(userId))) {
            Slog.i(TAG, "Managed profile got unlocked, will unlock its keystore");
            try {
                unlockChildProfile(userId, true);
            } catch (RemoteException e) {
                Slog.e(TAG, "Failed to unlock child profile");
            }
        }
    }

    public void onUnlockUser(final int userId) {
        this.mHandler.post(new Runnable() {
            public void run() {
                LockSettingsService.this.ensureProfileKeystoreUnlocked(userId);
                LockSettingsService.this.hideEncryptionNotification(new UserHandle(userId));
                if (XSpaceUserHandle.isXSpaceUserId(userId)) {
                    LockSettingsService.this.tieManagedProfileLockIfNecessary(userId, (byte[]) null);
                }
                if (LockSettingsService.this.mUserManager.getUserInfo(userId).isManagedProfile()) {
                    LockSettingsService.this.tieManagedProfileLockIfNecessary(userId, (byte[]) null);
                }
                if (LockSettingsService.this.mUserManager.getUserInfo(userId).isPrimary() && !LockSettingsService.this.isUserSecure(userId)) {
                    LockSettingsService.this.tryDeriveAuthTokenForUnsecuredPrimaryUser(userId);
                }
            }
        });
    }

    /* access modifiers changed from: private */
    public void tryDeriveAuthTokenForUnsecuredPrimaryUser(int userId) {
        synchronized (this.mSpManager) {
            if (isSyntheticPasswordBasedCredentialLocked(userId)) {
                try {
                    SyntheticPasswordManager.AuthenticationResult result = this.mSpManager.unwrapPasswordBasedSyntheticPassword(getGateKeeperService(), getSyntheticPasswordHandleLocked(userId), (byte[]) null, userId, (ICheckCredentialProgressCallback) null);
                    if (result.authToken != null) {
                        Slog.i(TAG, "Retrieved auth token for user " + userId);
                        onAuthTokenKnownForUser(userId, result.authToken);
                    } else {
                        Slog.e(TAG, "Auth token not available for user " + userId);
                    }
                } catch (RemoteException e) {
                    Slog.e(TAG, "Failure retrieving auth token", e);
                }
            }
        }
    }

    public void systemReady() {
        if (this.mContext.checkCallingOrSelfPermission(PERMISSION) != 0) {
            EventLog.writeEvent(1397638484, new Object[]{"28251513", Integer.valueOf(getCallingUid()), ""});
        }
        checkWritePermission(0);
        migrateOldData();
        try {
            getGateKeeperService();
            this.mSpManager.initWeaverService();
        } catch (RemoteException e) {
            Slog.e(TAG, "Failure retrieving IGateKeeperService", e);
        }
        try {
            this.mAuthSecretService = IAuthSecret.getService();
        } catch (NoSuchElementException e2) {
            Slog.i(TAG, "Device doesn't implement AuthSecret HAL");
        } catch (RemoteException e3) {
            Slog.w(TAG, "Failed to get AuthSecret HAL", e3);
        }
        this.mDeviceProvisionedObserver.onSystemReady();
        this.mStorage.prefetchUser(0);
    }

    private void migrateOldData() {
        boolean z;
        int i;
        boolean z2 = false;
        if (getString("migrated", (String) null, 0) == null) {
            ContentResolver cr = this.mContext.getContentResolver();
            for (String validSetting : VALID_SETTINGS) {
                String value = Settings.Secure.getString(cr, validSetting);
                if (value != null) {
                    setString(validSetting, value, 0);
                }
            }
            setString("migrated", "true", 0);
            Slog.i(TAG, "Migrated lock settings to new location");
        }
        if (getString("migrated_user_specific", (String) null, 0) == null) {
            ContentResolver cr2 = this.mContext.getContentResolver();
            List<UserInfo> users = this.mUserManager.getUsers();
            int user = 0;
            while (user < users.size()) {
                int userId = users.get(user).id;
                String ownerInfo = Settings.Secure.getStringForUser(cr2, "lock_screen_owner_info", userId);
                if (!TextUtils.isEmpty(ownerInfo)) {
                    setString("lock_screen_owner_info", ownerInfo, userId);
                    Settings.Secure.putStringForUser(cr2, "lock_screen_owner_info", "", userId);
                }
                try {
                    setLong("lock_screen_owner_info_enabled", Settings.Secure.getIntForUser(cr2, "lock_screen_owner_info_enabled", userId) != 0 ? true : z2 ? 1 : 0, userId);
                } catch (Settings.SettingNotFoundException e) {
                    if (!TextUtils.isEmpty(ownerInfo)) {
                        setLong("lock_screen_owner_info_enabled", 1, userId);
                    }
                }
                Settings.Secure.putIntForUser(cr2, "lock_screen_owner_info_enabled", 0, userId);
                user++;
                z2 = false;
            }
            z = z2;
            setString("migrated_user_specific", "true", z);
            Slog.i(TAG, "Migrated per-user lock settings to new location");
        } else {
            z = false;
        }
        if (getString("migrated_biometric_weak", (String) null, z) == null) {
            List<UserInfo> users2 = this.mUserManager.getUsers();
            for (int i2 = 0; i2 < users2.size(); i2++) {
                int userId2 = users2.get(i2).id;
                long type = getLong("lockscreen.password_type", 0, userId2);
                long alternateType = getLong("lockscreen.password_type_alternate", 0, userId2);
                if (type == 32768) {
                    setLong("lockscreen.password_type", alternateType, userId2);
                }
                setLong("lockscreen.password_type_alternate", 0, userId2);
            }
            i = 0;
            setString("migrated_biometric_weak", "true", 0);
            Slog.i(TAG, "Migrated biometric weak to use the fallback instead");
        } else {
            i = 0;
        }
        if (getString("migrated_lockscreen_disabled", (String) null, i) == null) {
            List<UserInfo> users3 = this.mUserManager.getUsers();
            int userCount = users3.size();
            int switchableUsers = 0;
            for (int i3 = 0; i3 < userCount; i3++) {
                if (users3.get(i3).supportsSwitchTo()) {
                    switchableUsers++;
                }
            }
            if (switchableUsers > 1) {
                for (int i4 = 0; i4 < userCount; i4++) {
                    int id = users3.get(i4).id;
                    if (getBoolean("lockscreen.disabled", false, id)) {
                        setBoolean("lockscreen.disabled", false, id);
                    }
                }
            }
            setString("migrated_lockscreen_disabled", "true", 0);
            Slog.i(TAG, "Migrated lockscreen disabled flag");
        }
        List<UserInfo> users4 = this.mUserManager.getUsers();
        for (int i5 = 0; i5 < users4.size(); i5++) {
            UserInfo userInfo = users4.get(i5);
            if (userInfo.isManagedProfile() && this.mStorage.hasChildProfileLock(userInfo.id)) {
                long quality = getLong("lockscreen.password_type", 0, userInfo.id);
                if (quality == 0) {
                    Slog.i(TAG, "Migrated tied profile lock type");
                    setLong("lockscreen.password_type", 327680, userInfo.id);
                } else if (quality != 327680) {
                    Slog.e(TAG, "Invalid tied profile lock type: " + quality);
                }
            }
            try {
                String alias = "profile_key_name_encrypt_" + userInfo.id;
                java.security.KeyStore keyStore = java.security.KeyStore.getInstance("AndroidKeyStore");
                keyStore.load((KeyStore.LoadStoreParameter) null);
                if (keyStore.containsAlias(alias)) {
                    keyStore.deleteEntry(alias);
                }
            } catch (IOException | KeyStoreException | NoSuchAlgorithmException | CertificateException e2) {
                Slog.e(TAG, "Unable to remove tied profile key", e2);
            }
        }
        if (this.mContext.getPackageManager().hasSystemFeature("android.hardware.type.watch") && getString("migrated_wear_lockscreen_disabled", (String) null, 0) == null) {
            int userCount2 = users4.size();
            for (int i6 = 0; i6 < userCount2; i6++) {
                setBoolean("lockscreen.disabled", false, users4.get(i6).id);
            }
            setString("migrated_wear_lockscreen_disabled", "true", 0);
            Slog.i(TAG, "Migrated lockscreen_disabled for Wear devices");
        }
    }

    /* access modifiers changed from: private */
    public void migrateOldDataAfterSystemReady() {
        try {
            if (LockPatternUtils.frpCredentialEnabled(this.mContext) && !getBoolean("migrated_frp", false, 0)) {
                migrateFrpCredential();
                setBoolean("migrated_frp", true, 0);
                Slog.i(TAG, "Migrated migrated_frp.");
            }
        } catch (RemoteException e) {
            Slog.e(TAG, "Unable to migrateOldDataAfterSystemReady", e);
        }
    }

    private void migrateFrpCredential() throws RemoteException {
        if (this.mStorage.readPersistentDataBlock() == LockSettingsStorage.PersistentData.NONE) {
            for (UserInfo userInfo : this.mUserManager.getUsers()) {
                if (LockPatternUtils.userOwnsFrpCredential(this.mContext, userInfo) && isUserSecure(userInfo.id)) {
                    synchronized (this.mSpManager) {
                        if (isSyntheticPasswordBasedCredentialLocked(userInfo.id)) {
                            this.mSpManager.migrateFrpPasswordLocked(getSyntheticPasswordHandleLocked(userInfo.id), userInfo, redactActualQualityToMostLenientEquivalentQuality((int) getLong("lockscreen.password_type", 0, userInfo.id)));
                        }
                    }
                    return;
                }
            }
        }
    }

    private int redactActualQualityToMostLenientEquivalentQuality(int quality) {
        if (quality == 131072 || quality == 196608) {
            return 131072;
        }
        return (quality == 262144 || quality == 327680 || quality == 393216) ? DumpState.DUMP_DOMAIN_PREFERRED : quality;
    }

    private final void checkWritePermission(int userId) {
        this.mContext.enforceCallingOrSelfPermission(PERMISSION, "LockSettingsWrite");
    }

    private final void checkPasswordReadPermission(int userId) {
        this.mContext.enforceCallingOrSelfPermission(PERMISSION, "LockSettingsRead");
    }

    private final void checkPasswordHavePermission(int userId) {
        if (this.mContext.checkCallingOrSelfPermission(PERMISSION) != 0) {
            EventLog.writeEvent(1397638484, new Object[]{"28251513", Integer.valueOf(getCallingUid()), ""});
        }
        this.mContext.enforceCallingOrSelfPermission(PERMISSION, "LockSettingsHave");
    }

    private final void checkReadPermission(String requestedKey, int userId) {
        int callingUid = Binder.getCallingUid();
        int i = 0;
        while (true) {
            String[] strArr = READ_CONTACTS_PROTECTED_SETTINGS;
            if (i >= strArr.length) {
                int i2 = 0;
                while (true) {
                    String[] strArr2 = READ_PASSWORD_PROTECTED_SETTINGS;
                    if (i2 >= strArr2.length) {
                        return;
                    }
                    if (!strArr2[i2].equals(requestedKey) || this.mContext.checkCallingOrSelfPermission(PERMISSION) == 0) {
                        i2++;
                    } else {
                        throw new SecurityException("uid=" + callingUid + " needs permission " + PERMISSION + " to read " + requestedKey + " for user " + userId);
                    }
                }
            } else if (!strArr[i].equals(requestedKey) || this.mContext.checkCallingOrSelfPermission("android.permission.READ_CONTACTS") == 0) {
                i++;
            } else {
                throw new SecurityException("uid=" + callingUid + " needs permission " + "android.permission.READ_CONTACTS" + " to read " + requestedKey + " for user " + userId);
            }
        }
    }

    public boolean getSeparateProfileChallengeEnabled(int userId) {
        boolean z;
        checkReadPermission(SEPARATE_PROFILE_CHALLENGE_KEY, userId);
        synchronized (this.mSeparateChallengeLock) {
            z = getBoolean(SEPARATE_PROFILE_CHALLENGE_KEY, false, userId);
        }
        return z;
    }

    public void setSeparateProfileChallengeEnabled(int userId, boolean enabled, byte[] managedUserPassword) {
        checkWritePermission(userId);
        if (this.mLockPatternUtils.hasSecureLockScreen()) {
            synchronized (this.mSeparateChallengeLock) {
                setSeparateProfileChallengeEnabledLocked(userId, enabled, managedUserPassword);
            }
            notifySeparateProfileChallengeChanged(userId);
            return;
        }
        throw new UnsupportedOperationException("This operation requires secure lock screen feature.");
    }

    @GuardedBy({"mSeparateChallengeLock"})
    private void setSeparateProfileChallengeEnabledLocked(int userId, boolean enabled, byte[] managedUserPassword) {
        boolean old = getBoolean(SEPARATE_PROFILE_CHALLENGE_KEY, false, userId);
        setBoolean(SEPARATE_PROFILE_CHALLENGE_KEY, enabled, userId);
        if (enabled) {
            try {
                this.mStorage.removeChildProfileLock(userId);
                removeKeystoreProfileKey(userId);
            } catch (IllegalStateException e) {
                setBoolean(SEPARATE_PROFILE_CHALLENGE_KEY, old, userId);
                throw e;
            }
        } else {
            tieManagedProfileLockIfNecessary(userId, managedUserPassword);
        }
    }

    private void notifySeparateProfileChallengeChanged(int userId) {
        DevicePolicyManagerInternal dpmi = (DevicePolicyManagerInternal) LocalServices.getService(DevicePolicyManagerInternal.class);
        if (dpmi != null) {
            dpmi.reportSeparateProfileChallengeChanged(userId);
        }
    }

    public void setBoolean(String key, boolean value, int userId) {
        checkWritePermission(userId);
        setStringUnchecked(key, userId, value ? SplitScreenReporter.ACTION_ENTER_SPLIT : "0");
    }

    public void setLong(String key, long value, int userId) {
        checkWritePermission(userId);
        setStringUnchecked(key, userId, Long.toString(value));
    }

    public void setString(String key, String value, int userId) {
        checkWritePermission(userId);
        setStringUnchecked(key, userId, value);
    }

    private void setStringUnchecked(String key, int userId, String value) {
        Preconditions.checkArgument(userId != -9999, "cannot store lock settings for FRP user");
        this.mStorage.writeKeyValue(key, value, userId);
        if (ArrayUtils.contains(SETTINGS_TO_BACKUP, key)) {
            BackupManager.dataChanged(UserBackupManagerService.SETTINGS_PACKAGE);
        }
    }

    public boolean getBoolean(String key, boolean defaultValue, int userId) {
        checkReadPermission(key, userId);
        String value = getStringUnchecked(key, (String) null, userId);
        if (TextUtils.isEmpty(value)) {
            return defaultValue;
        }
        return value.equals(SplitScreenReporter.ACTION_ENTER_SPLIT) || value.equals("true");
    }

    public long getLong(String key, long defaultValue, int userId) {
        checkReadPermission(key, userId);
        String value = getStringUnchecked(key, (String) null, userId);
        return TextUtils.isEmpty(value) ? defaultValue : Long.parseLong(value);
    }

    public String getString(String key, String defaultValue, int userId) {
        checkReadPermission(key, userId);
        return getStringUnchecked(key, defaultValue, userId);
    }

    public String getStringUnchecked(String key, String defaultValue, int userId) {
        if ("lock_pattern_autolock".equals(key)) {
            long ident = Binder.clearCallingIdentity();
            try {
                return this.mLockPatternUtils.isLockPatternEnabled(userId) ? SplitScreenReporter.ACTION_ENTER_SPLIT : "0";
            } finally {
                Binder.restoreCallingIdentity(ident);
            }
        } else if (userId == -9999) {
            return getFrpStringUnchecked(key);
        } else {
            if ("legacy_lock_pattern_enabled".equals(key)) {
                key = "lock_pattern_autolock";
            }
            return this.mStorage.readKeyValue(key, defaultValue, userId);
        }
    }

    private String getFrpStringUnchecked(String key) {
        if ("lockscreen.password_type".equals(key)) {
            return String.valueOf(readFrpPasswordQuality());
        }
        return null;
    }

    private int readFrpPasswordQuality() {
        return this.mStorage.readPersistentDataBlock().qualityForUi;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:10:0x001d, code lost:
        return r3;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean havePassword(int r6) {
        /*
            r5 = this;
            r5.checkPasswordHavePermission(r6)
            com.android.server.locksettings.SyntheticPasswordManager r0 = r5.mSpManager
            monitor-enter(r0)
            boolean r1 = r5.isSyntheticPasswordBasedCredentialLocked(r6)     // Catch:{ all -> 0x0026 }
            if (r1 == 0) goto L_0x001e
            long r1 = r5.getSyntheticPasswordHandleLocked(r6)     // Catch:{ all -> 0x0026 }
            com.android.server.locksettings.SyntheticPasswordManager r3 = r5.mSpManager     // Catch:{ all -> 0x0026 }
            int r3 = r3.getCredentialType(r1, r6)     // Catch:{ all -> 0x0026 }
            r4 = 2
            if (r3 != r4) goto L_0x001b
            r3 = 1
            goto L_0x001c
        L_0x001b:
            r3 = 0
        L_0x001c:
            monitor-exit(r0)     // Catch:{ all -> 0x0026 }
            return r3
        L_0x001e:
            monitor-exit(r0)     // Catch:{ all -> 0x0026 }
            com.android.server.locksettings.LockSettingsStorage r0 = r5.mStorage
            boolean r0 = r0.hasPassword(r6)
            return r0
        L_0x0026:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x0026 }
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.locksettings.LockSettingsService.havePassword(int):boolean");
    }

    /* JADX WARNING: Code restructure failed: missing block: B:10:0x001c, code lost:
        return r4;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean havePattern(int r6) {
        /*
            r5 = this;
            r5.checkPasswordHavePermission(r6)
            com.android.server.locksettings.SyntheticPasswordManager r0 = r5.mSpManager
            monitor-enter(r0)
            boolean r1 = r5.isSyntheticPasswordBasedCredentialLocked(r6)     // Catch:{ all -> 0x0025 }
            if (r1 == 0) goto L_0x001d
            long r1 = r5.getSyntheticPasswordHandleLocked(r6)     // Catch:{ all -> 0x0025 }
            com.android.server.locksettings.SyntheticPasswordManager r3 = r5.mSpManager     // Catch:{ all -> 0x0025 }
            int r3 = r3.getCredentialType(r1, r6)     // Catch:{ all -> 0x0025 }
            r4 = 1
            if (r3 != r4) goto L_0x001a
            goto L_0x001b
        L_0x001a:
            r4 = 0
        L_0x001b:
            monitor-exit(r0)     // Catch:{ all -> 0x0025 }
            return r4
        L_0x001d:
            monitor-exit(r0)     // Catch:{ all -> 0x0025 }
            com.android.server.locksettings.LockSettingsStorage r0 = r5.mStorage
            boolean r0 = r0.hasPattern(r6)
            return r0
        L_0x0025:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x0025 }
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.locksettings.LockSettingsService.havePattern(int):boolean");
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Code restructure failed: missing block: B:10:0x001a, code lost:
        return r3;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean isUserSecure(int r6) {
        /*
            r5 = this;
            com.android.server.locksettings.SyntheticPasswordManager r0 = r5.mSpManager
            monitor-enter(r0)
            boolean r1 = r5.isSyntheticPasswordBasedCredentialLocked(r6)     // Catch:{ all -> 0x0023 }
            if (r1 == 0) goto L_0x001b
            long r1 = r5.getSyntheticPasswordHandleLocked(r6)     // Catch:{ all -> 0x0023 }
            com.android.server.locksettings.SyntheticPasswordManager r3 = r5.mSpManager     // Catch:{ all -> 0x0023 }
            int r3 = r3.getCredentialType(r1, r6)     // Catch:{ all -> 0x0023 }
            r4 = -1
            if (r3 == r4) goto L_0x0018
            r3 = 1
            goto L_0x0019
        L_0x0018:
            r3 = 0
        L_0x0019:
            monitor-exit(r0)     // Catch:{ all -> 0x0023 }
            return r3
        L_0x001b:
            monitor-exit(r0)     // Catch:{ all -> 0x0023 }
            com.android.server.locksettings.LockSettingsStorage r0 = r5.mStorage
            boolean r0 = r0.hasCredential(r6)
            return r0
        L_0x0023:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x0023 }
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.locksettings.LockSettingsService.isUserSecure(int):boolean");
    }

    public void retainPassword(String password) {
        if (!LockPatternUtils.isDeviceEncryptionEnabled()) {
            return;
        }
        if (password != null) {
            mSavePassword = password;
        } else {
            mSavePassword = DEFAULT_PASSWORD;
        }
    }

    public void sanitizePassword() {
        if (LockPatternUtils.isDeviceEncryptionEnabled()) {
            mSavePassword = DEFAULT_PASSWORD;
        }
    }

    private boolean checkCryptKeeperPermissions() {
        try {
            this.mContext.enforceCallingOrSelfPermission("android.permission.CRYPT_KEEPER", "no permission to get the password");
            return false;
        } catch (SecurityException e) {
            return true;
        }
    }

    public String getPassword() {
        if (checkCryptKeeperPermissions()) {
            this.mContext.enforceCallingOrSelfPermission("android.permission.MANAGE_DEVICE_ADMINS", "no crypt_keeper or admin permission to get the password");
        }
        return mSavePassword;
    }

    private void setKeystorePassword(byte[] password, int userHandle) {
        android.security.KeyStore.getInstance().onUserPasswordChanged(userHandle, password == null ? null : new String(password));
    }

    private void unlockKeystore(byte[] password, int userHandle) {
        android.security.KeyStore.getInstance().unlock(userHandle, password == null ? null : new String(password));
    }

    /* access modifiers changed from: protected */
    @VisibleForTesting
    public byte[] getDecryptedPasswordForTiedProfile(int userId) throws KeyStoreException, UnrecoverableKeyException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, CertificateException, IOException {
        byte[] storedData = this.mStorage.readChildProfileLock(userId);
        if (storedData != null) {
            byte[] iv = Arrays.copyOfRange(storedData, 0, 12);
            byte[] encryptedPassword = Arrays.copyOfRange(storedData, 12, storedData.length);
            java.security.KeyStore keyStore = java.security.KeyStore.getInstance("AndroidKeyStore");
            keyStore.load((KeyStore.LoadStoreParameter) null);
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(2, (SecretKey) keyStore.getKey("profile_key_name_decrypt_" + userId, (char[]) null), new GCMParameterSpec(128, iv));
            return cipher.doFinal(encryptedPassword);
        }
        throw new FileNotFoundException("Child profile lock file not found");
    }

    private void unlockChildProfile(int profileHandle, boolean ignoreUserNotAuthenticated) throws RemoteException {
        try {
            doVerifyCredential(getDecryptedPasswordForTiedProfile(profileHandle), 2, false, 0, profileHandle, (ICheckCredentialProgressCallback) null);
        } catch (IOException | InvalidAlgorithmParameterException | InvalidKeyException | KeyStoreException | NoSuchAlgorithmException | UnrecoverableKeyException | CertificateException | BadPaddingException | IllegalBlockSizeException | NoSuchPaddingException e) {
            if (e instanceof FileNotFoundException) {
                Slog.i(TAG, "Child profile key not found");
            } else if (!ignoreUserNotAuthenticated || !(e instanceof UserNotAuthenticatedException)) {
                Slog.e(TAG, "Failed to decrypt child profile key", e);
            } else {
                Slog.i(TAG, "Parent keystore seems locked, ignoring");
            }
        }
    }

    private void unlockUser(int userId, byte[] token, byte[] secret) {
        boolean alreadyUnlocked = this.mUserManager.isUserUnlockingOrUnlocked(userId);
        final CountDownLatch latch = new CountDownLatch(1);
        try {
            this.mActivityManager.unlockUser(userId, token, secret, new IProgressListener.Stub() {
                public void onStarted(int id, Bundle extras) throws RemoteException {
                    Log.d(LockSettingsService.TAG, "unlockUser started");
                }

                public void onProgress(int id, int progress, Bundle extras) throws RemoteException {
                    Log.d(LockSettingsService.TAG, "unlockUser progress " + progress);
                }

                public void onFinished(int id, Bundle extras) throws RemoteException {
                    Log.d(LockSettingsService.TAG, "unlockUser finished");
                    latch.countDown();
                }
            });
            try {
                latch.await(15, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            if (!this.mUserManager.getUserInfo(userId).isManagedProfile() && !XSpaceUserHandle.isXSpaceUserId(userId)) {
                for (UserInfo profile : this.mUserManager.getProfiles(userId)) {
                    if (tiedManagedProfileReadyToUnlock(profile)) {
                        try {
                            unlockChildProfile(profile.id, false);
                        } catch (RemoteException e2) {
                            Log.d(TAG, "Failed to unlock child profile", e2);
                        }
                    }
                    if (!alreadyUnlocked) {
                        long ident = clearCallingIdentity();
                        try {
                            maybeShowEncryptionNotificationForUser(profile.id);
                        } finally {
                            restoreCallingIdentity(ident);
                        }
                    }
                }
            }
        } catch (RemoteException e3) {
            throw e3.rethrowAsRuntimeException();
        }
    }

    private boolean tiedManagedProfileReadyToUnlock(UserInfo userInfo) {
        return (userInfo.isManagedProfile() || XSpaceUserHandle.isXSpaceUserId(userInfo.id)) && !this.mLockPatternUtils.isSeparateProfileChallengeEnabled(userInfo.id) && this.mStorage.hasChildProfileLock(userInfo.id) && (this.mUserManager.isUserRunning(userInfo.id) || XSpaceUserHandle.isXSpaceUserId(userInfo.id));
    }

    private Map<Integer, byte[]> getDecryptedPasswordsForAllTiedProfiles(int userId) {
        if (this.mUserManager.getUserInfo(userId).isManagedProfile()) {
            return null;
        }
        Map<Integer, byte[]> result = new ArrayMap<>();
        List<UserInfo> profiles = this.mUserManager.getProfiles(userId);
        int size = profiles.size();
        for (int i = 0; i < size; i++) {
            UserInfo profile = profiles.get(i);
            if (profile.isManagedProfile() || XSpaceUserHandle.isXSpaceUser(profile)) {
                int managedUserId = profile.id;
                if (!this.mLockPatternUtils.isSeparateProfileChallengeEnabled(managedUserId)) {
                    try {
                        result.put(Integer.valueOf(managedUserId), getDecryptedPasswordForTiedProfile(managedUserId));
                    } catch (IOException | InvalidAlgorithmParameterException | InvalidKeyException | KeyStoreException | NoSuchAlgorithmException | UnrecoverableKeyException | CertificateException | BadPaddingException | IllegalBlockSizeException | NoSuchPaddingException e) {
                        Slog.e(TAG, "getDecryptedPasswordsForAllTiedProfiles failed for user " + managedUserId, e);
                    }
                }
            }
        }
        return result;
    }

    private void synchronizeUnifiedWorkChallengeForProfiles(int userId, Map<Integer, byte[]> profilePasswordMap) throws RemoteException {
        int managedUserId;
        int i = userId;
        Map<Integer, byte[]> map = profilePasswordMap;
        if (!XSpaceUserHandle.isXSpaceUserId(userId) && !this.mUserManager.getUserInfo(i).isManagedProfile()) {
            boolean isSecure = isUserSecure(userId);
            List<UserInfo> profiles = this.mUserManager.getProfiles(i);
            int size = profiles.size();
            for (int i2 = 0; i2 < size; i2++) {
                UserInfo profile = profiles.get(i2);
                if (profile.isManagedProfile() || XSpaceUserHandle.isXSpaceUserId(profile.id)) {
                    int managedUserId2 = profile.id;
                    if (!this.mLockPatternUtils.isSeparateProfileChallengeEnabled(managedUserId2)) {
                        if (isSecure) {
                            tieManagedProfileLockIfNecessary(managedUserId2, (byte[]) null);
                        } else {
                            if (map == null || !map.containsKey(Integer.valueOf(managedUserId2))) {
                                managedUserId = managedUserId2;
                                Slog.wtf(TAG, "clear tied profile challenges, but no password supplied.");
                                setLockCredentialInternal((byte[]) null, -1, (byte[]) null, 0, managedUserId, true, true);
                            } else {
                                managedUserId = managedUserId2;
                                setLockCredentialInternal((byte[]) null, -1, map.get(Integer.valueOf(managedUserId2)), 0, managedUserId2, false, true);
                            }
                            int managedUserId3 = managedUserId;
                            this.mStorage.removeChildProfileLock(managedUserId3);
                            removeKeystoreProfileKey(managedUserId3);
                        }
                    }
                }
            }
        }
    }

    private boolean isManagedProfileWithUnifiedLock(int userId) {
        if (XSpaceUserHandle.isXSpaceUserId(userId)) {
            return !this.mLockPatternUtils.isSeparateProfileChallengeEnabled(userId);
        }
        if (!this.mUserManager.getUserInfo(userId).isManagedProfile() || this.mLockPatternUtils.isSeparateProfileChallengeEnabled(userId)) {
            return false;
        }
        return true;
    }

    private boolean isManagedProfileWithSeparatedLock(int userId) {
        if (XSpaceUserHandle.isXSpaceUserId(userId)) {
            return this.mLockPatternUtils.isSeparateProfileChallengeEnabled(userId);
        }
        return this.mUserManager.getUserInfo(userId).isManagedProfile() && this.mLockPatternUtils.isSeparateProfileChallengeEnabled(userId);
    }

    private void sendCredentialsOnUnlockIfRequired(int credentialType, byte[] credential, int userId) {
        if (userId != -9999 && !isManagedProfileWithUnifiedLock(userId)) {
            for (Integer intValue : getProfilesWithSameLockScreen(userId)) {
                this.mRecoverableKeyStoreManager.lockScreenSecretAvailable(credentialType, credential, intValue.intValue());
            }
        }
    }

    private void sendCredentialsOnChangeIfRequired(int credentialType, byte[] credential, int userId, boolean isLockTiedToParent) {
        if (!isLockTiedToParent) {
            for (Integer intValue : getProfilesWithSameLockScreen(userId)) {
                this.mRecoverableKeyStoreManager.lockScreenSecretChanged(credentialType, credential, intValue.intValue());
            }
        }
    }

    private Set<Integer> getProfilesWithSameLockScreen(int userId) {
        Set<Integer> profiles = new ArraySet<>();
        for (UserInfo profile : this.mUserManager.getProfiles(userId)) {
            if (profile.id == userId || (profile.profileGroupId == userId && isManagedProfileWithUnifiedLock(profile.id))) {
                profiles.add(Integer.valueOf(profile.id));
            }
        }
        return profiles;
    }

    public void setLockCredential(byte[] credential, int type, byte[] savedCredential, int requestedQuality, int userId, boolean allowUntrustedChange) throws RemoteException {
        if (this.mLockPatternUtils.hasSecureLockScreen()) {
            checkWritePermission(userId);
            synchronized (this.mSeparateChallengeLock) {
                setLockCredentialInternal(credential, type, savedCredential, requestedQuality, userId, allowUntrustedChange, false);
                setSeparateProfileChallengeEnabledLocked(userId, true, (byte[]) null);
                notifyPasswordChanged(userId);
            }
            if (this.mUserManager.getUserInfo(userId).isManagedProfile()) {
                setDeviceUnlockedForUser(userId);
            }
            notifySeparateProfileChallengeChanged(userId);
            return;
        }
        throw new UnsupportedOperationException("This operation requires secure lock screen feature");
    }

    /* JADX WARNING: Code restructure failed: missing block: B:21:0x003f, code lost:
        if (r10 != -1) goto L_0x006d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:22:0x0041, code lost:
        if (r14 == null) goto L_0x004a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:23:0x0043, code lost:
        android.util.Slog.wtf(TAG, "CredentialType is none, but credential is non-null.");
     */
    /* JADX WARNING: Code restructure failed: missing block: B:24:0x004a, code lost:
        clearUserKeyProtection(r11);
        getGateKeeperService().clearSecureUserId(r11);
        r9.mStorage.writeCredentialHash(com.android.server.locksettings.LockSettingsStorage.CredentialHash.createEmptyHash(), r11);
        setKeystorePassword((byte[]) null, r11);
        fixateNewestUserKeyAuth(r11);
        synchronizeUnifiedWorkChallengeForProfiles(r11, (java.util.Map<java.lang.Integer, byte[]>) null);
        notifyActivePasswordMetricsAvailable(-1, (byte[]) null, r11);
        sendCredentialsOnChangeIfRequired(r10, r14, r11, r12);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:25:0x006c, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:26:0x006d, code lost:
        if (r14 == null) goto L_0x015d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:27:0x006f, code lost:
        r8 = r9.mStorage.readCredentialHash(r11);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:28:0x0079, code lost:
        if (isManagedProfileWithUnifiedLock(r11) == false) goto L_0x0097;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:29:0x007b, code lost:
        if (r13 != null) goto L_0x00a6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:32:0x0081, code lost:
        r13 = getDecryptedPasswordForTiedProfile(r11);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:33:0x0083, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:34:0x0084, code lost:
        android.util.Slog.e(TAG, "Failed to decrypt child profile key", r0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:35:0x008c, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:36:0x008d, code lost:
        r0 = r0;
        android.util.Slog.i(TAG, "Child profile key not found");
     */
    /* JADX WARNING: Code restructure failed: missing block: B:38:0x0099, code lost:
        if (r8.hash != null) goto L_0x00a6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:39:0x009b, code lost:
        if (r13 == null) goto L_0x00a4;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:40:0x009d, code lost:
        android.util.Slog.w(TAG, "Saved credential provided, but none stored");
     */
    /* JADX WARNING: Code restructure failed: missing block: B:41:0x00a4, code lost:
        r13 = null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:80:0x0164, code lost:
        throw new android.os.RemoteException("Null credential with mismatched credential type");
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void setLockCredentialInternal(byte[] r21, int r22, byte[] r23, int r24, int r25, boolean r26, boolean r27) throws android.os.RemoteException {
        /*
            r20 = this;
            r9 = r20
            r0 = r21
            r10 = r22
            r1 = r23
            r11 = r25
            r12 = r27
            if (r1 == 0) goto L_0x0014
            int r2 = r1.length
            if (r2 != 0) goto L_0x0012
            goto L_0x0014
        L_0x0012:
            r13 = r1
            goto L_0x0016
        L_0x0014:
            r1 = 0
            r13 = r1
        L_0x0016:
            if (r0 == 0) goto L_0x001e
            int r1 = r0.length
            if (r1 != 0) goto L_0x001c
            goto L_0x001e
        L_0x001c:
            r14 = r0
            goto L_0x0020
        L_0x001e:
            r0 = 0
            r14 = r0
        L_0x0020:
            com.android.server.locksettings.SyntheticPasswordManager r15 = r9.mSpManager
            monitor-enter(r15)
            boolean r0 = r9.isSyntheticPasswordBasedCredentialLocked(r11)     // Catch:{ all -> 0x0165 }
            if (r0 == 0) goto L_0x003c
            r1 = r20
            r2 = r14
            r3 = r22
            r4 = r13
            r5 = r24
            r6 = r25
            r7 = r26
            r8 = r27
            r1.spBasedSetLockCredentialInternalLocked(r2, r3, r4, r5, r6, r7, r8)     // Catch:{ all -> 0x0165 }
            monitor-exit(r15)     // Catch:{ all -> 0x0165 }
            return
        L_0x003c:
            monitor-exit(r15)     // Catch:{ all -> 0x0165 }
            r0 = -1
            r15 = 0
            if (r10 != r0) goto L_0x006d
            if (r14 == 0) goto L_0x004a
            java.lang.String r1 = "LockSettingsService"
            java.lang.String r2 = "CredentialType is none, but credential is non-null."
            android.util.Slog.wtf(r1, r2)
        L_0x004a:
            r9.clearUserKeyProtection(r11)
            android.service.gatekeeper.IGateKeeperService r1 = r20.getGateKeeperService()
            r1.clearSecureUserId(r11)
            com.android.server.locksettings.LockSettingsStorage r1 = r9.mStorage
            com.android.server.locksettings.LockSettingsStorage$CredentialHash r2 = com.android.server.locksettings.LockSettingsStorage.CredentialHash.createEmptyHash()
            r1.writeCredentialHash(r2, r11)
            r9.setKeystorePassword(r15, r11)
            r9.fixateNewestUserKeyAuth(r11)
            r9.synchronizeUnifiedWorkChallengeForProfiles(r11, r15)
            r9.notifyActivePasswordMetricsAvailable(r0, r15, r11)
            r9.sendCredentialsOnChangeIfRequired(r10, r14, r11, r12)
            return
        L_0x006d:
            if (r14 == 0) goto L_0x015d
            com.android.server.locksettings.LockSettingsStorage r0 = r9.mStorage
            com.android.server.locksettings.LockSettingsStorage$CredentialHash r8 = r0.readCredentialHash(r11)
            boolean r0 = r9.isManagedProfileWithUnifiedLock(r11)
            if (r0 == 0) goto L_0x0097
            if (r13 != 0) goto L_0x00a6
            byte[] r0 = r9.getDecryptedPasswordForTiedProfile(r11)     // Catch:{ FileNotFoundException -> 0x008c, IOException | InvalidAlgorithmParameterException | InvalidKeyException | KeyStoreException | NoSuchAlgorithmException | UnrecoverableKeyException | CertificateException | BadPaddingException | IllegalBlockSizeException | NoSuchPaddingException -> 0x0083 }
            r13 = r0
            goto L_0x00a6
        L_0x0083:
            r0 = move-exception
            java.lang.String r1 = "LockSettingsService"
            java.lang.String r2 = "Failed to decrypt child profile key"
            android.util.Slog.e(r1, r2, r0)
            goto L_0x0096
        L_0x008c:
            r0 = move-exception
            r1 = r0
            r0 = r1
            java.lang.String r1 = "LockSettingsService"
            java.lang.String r2 = "Child profile key not found"
            android.util.Slog.i(r1, r2)
        L_0x0096:
            goto L_0x00a6
        L_0x0097:
            byte[] r0 = r8.hash
            if (r0 != 0) goto L_0x00a6
            if (r13 == 0) goto L_0x00a4
            java.lang.String r0 = "LockSettingsService"
            java.lang.String r1 = "Saved credential provided, but none stored"
            android.util.Slog.w(r0, r1)
        L_0x00a4:
            r0 = 0
            r13 = r0
        L_0x00a6:
            com.android.server.locksettings.SyntheticPasswordManager r7 = r9.mSpManager
            monitor-enter(r7)
            boolean r0 = r9.shouldMigrateToSyntheticPasswordLocked(r11)     // Catch:{ all -> 0x0154 }
            if (r0 == 0) goto L_0x00df
            byte[] r2 = r8.hash     // Catch:{ all -> 0x00d8 }
            int r4 = r8.type     // Catch:{ all -> 0x00d8 }
            r1 = r20
            r3 = r13
            r5 = r24
            r6 = r25
            r1.initializeSyntheticPasswordLocked(r2, r3, r4, r5, r6)     // Catch:{ all -> 0x00d8 }
            r1 = r20
            r2 = r14
            r3 = r22
            r4 = r13
            r5 = r24
            r6 = r25
            r16 = r7
            r7 = r26
            r15 = r8
            r8 = r27
            r1.spBasedSetLockCredentialInternalLocked(r2, r3, r4, r5, r6, r7, r8)     // Catch:{ all -> 0x00d3 }
            monitor-exit(r16)     // Catch:{ all -> 0x00d3 }
            return
        L_0x00d3:
            r0 = move-exception
            r17 = r15
            goto L_0x0159
        L_0x00d8:
            r0 = move-exception
            r16 = r7
            r17 = r8
            goto L_0x0159
        L_0x00df:
            r16 = r7
            monitor-exit(r16)     // Catch:{ all -> 0x0152 }
            byte[] r0 = r8.hash
            byte[] r7 = r9.enrollCredential(r0, r13, r14, r11)
            if (r7 == 0) goto L_0x012d
            com.android.server.locksettings.LockSettingsStorage$CredentialHash r6 = com.android.server.locksettings.LockSettingsStorage.CredentialHash.create(r7, r10)
            com.android.server.locksettings.LockSettingsStorage r0 = r9.mStorage
            r0.writeCredentialHash(r6, r11)
            android.service.gatekeeper.IGateKeeperService r0 = r20.getGateKeeperService()
            r2 = 0
            byte[] r4 = r6.hash
            r1 = r25
            r5 = r14
            android.service.gatekeeper.GateKeeperResponse r0 = r0.verifyChallenge(r1, r2, r4, r5)
            com.android.internal.widget.VerifyCredentialResponse r1 = r9.convertResponse(r0)
            r9.setUserKeyProtection(r11, r14, r1)
            r9.fixateNewestUserKeyAuth(r11)
            r4 = 1
            r16 = 0
            r18 = 0
            r1 = r20
            r2 = r14
            r3 = r22
            r19 = r6
            r5 = r16
            r16 = r7
            r7 = r25
            r17 = r8
            r8 = r18
            r1.doVerifyCredential(r2, r3, r4, r5, r7, r8)
            r9.synchronizeUnifiedWorkChallengeForProfiles(r11, r15)
            r9.sendCredentialsOnChangeIfRequired(r10, r14, r11, r12)
            return
        L_0x012d:
            r16 = r7
            r17 = r8
            android.os.RemoteException r0 = new android.os.RemoteException
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r2 = "Failed to enroll "
            r1.append(r2)
            r2 = 2
            if (r10 != r2) goto L_0x0144
            java.lang.String r2 = "password"
            goto L_0x0147
        L_0x0144:
            java.lang.String r2 = "pattern"
        L_0x0147:
            r1.append(r2)
            java.lang.String r1 = r1.toString()
            r0.<init>(r1)
            throw r0
        L_0x0152:
            r0 = move-exception
            goto L_0x0157
        L_0x0154:
            r0 = move-exception
            r16 = r7
        L_0x0157:
            r17 = r8
        L_0x0159:
            monitor-exit(r16)     // Catch:{ all -> 0x015b }
            throw r0
        L_0x015b:
            r0 = move-exception
            goto L_0x0159
        L_0x015d:
            android.os.RemoteException r0 = new android.os.RemoteException
            java.lang.String r1 = "Null credential with mismatched credential type"
            r0.<init>(r1)
            throw r0
        L_0x0165:
            r0 = move-exception
            monitor-exit(r15)     // Catch:{ all -> 0x0165 }
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.locksettings.LockSettingsService.setLockCredentialInternal(byte[], int, byte[], int, int, boolean, boolean):void");
    }

    private VerifyCredentialResponse convertResponse(GateKeeperResponse gateKeeperResponse) {
        return VerifyCredentialResponse.fromGateKeeperResponse(gateKeeperResponse);
    }

    /* Debug info: failed to restart local var, previous not found, register: 12 */
    /* access modifiers changed from: protected */
    @VisibleForTesting
    public void tieProfileLockToParent(int userId, byte[] password) {
        java.security.KeyStore keyStore;
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
            keyGenerator.init(new SecureRandom());
            SecretKey secretKey = keyGenerator.generateKey();
            keyStore = java.security.KeyStore.getInstance("AndroidKeyStore");
            keyStore.load((KeyStore.LoadStoreParameter) null);
            keyStore.setEntry("profile_key_name_encrypt_" + userId, new KeyStore.SecretKeyEntry(secretKey), new KeyProtection.Builder(1).setBlockModes(new String[]{"GCM"}).setEncryptionPaddings(new String[]{"NoPadding"}).build());
            keyStore.setEntry("profile_key_name_decrypt_" + userId, new KeyStore.SecretKeyEntry(secretKey), new KeyProtection.Builder(2).setBlockModes(new String[]{"GCM"}).setEncryptionPaddings(new String[]{"NoPadding"}).setUserAuthenticationRequired(true).setUserAuthenticationValidityDurationSeconds(30).setCriticalToDeviceEncryption(true).build());
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(1, (SecretKey) keyStore.getKey("profile_key_name_encrypt_" + userId, (char[]) null));
            byte[] encryptionResult = cipher.doFinal(password);
            byte[] iv = cipher.getIV();
            keyStore.deleteEntry("profile_key_name_encrypt_" + userId);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            try {
                if (iv.length == 12) {
                    outputStream.write(iv);
                    outputStream.write(encryptionResult);
                    this.mStorage.writeChildProfileLock(userId, outputStream.toByteArray());
                    return;
                }
                throw new RuntimeException("Invalid iv length: " + iv.length);
            } catch (IOException e) {
                throw new RuntimeException("Failed to concatenate byte arrays", e);
            }
        } catch (IOException | InvalidKeyException | KeyStoreException | NoSuchAlgorithmException | UnrecoverableKeyException | CertificateException | BadPaddingException | IllegalBlockSizeException | NoSuchPaddingException e2) {
            throw new RuntimeException("Failed to encrypt key", e2);
        } catch (Throwable th) {
            keyStore.deleteEntry("profile_key_name_encrypt_" + userId);
            throw th;
        }
    }

    private byte[] enrollCredential(byte[] enrolledHandle, byte[] enrolledCredential, byte[] toEnroll, int userId) throws RemoteException {
        checkWritePermission(userId);
        GateKeeperResponse response = getGateKeeperService().enroll(userId, enrolledHandle, enrolledCredential, toEnroll);
        if (response == null) {
            return null;
        }
        byte[] hash = response.getPayload();
        if (hash != null) {
            setKeystorePassword(toEnroll, userId);
        } else {
            Slog.e(TAG, "Throttled while enrolling a password");
        }
        return hash;
    }

    private void setAuthlessUserKeyProtection(int userId, byte[] key) throws RemoteException {
        addUserKeyAuth(userId, (byte[]) null, key);
    }

    private void setUserKeyProtection(int userId, byte[] credential, VerifyCredentialResponse vcr) throws RemoteException {
        if (vcr == null) {
            throw new RemoteException("Null response verifying a credential we just set");
        } else if (vcr.getResponseCode() == 0) {
            byte[] token = vcr.getPayload();
            if (token != null) {
                addUserKeyAuth(userId, token, secretFromCredential(credential));
                return;
            }
            throw new RemoteException("Empty payload verifying a credential we just set");
        } else {
            throw new RemoteException("Non-OK response verifying a credential we just set: " + vcr.getResponseCode());
        }
    }

    private void clearUserKeyProtection(int userId) throws RemoteException {
        addUserKeyAuth(userId, (byte[]) null, (byte[]) null);
    }

    private void clearUserKeyAuth(int userId, byte[] token, byte[] secret) throws RemoteException {
        UserInfo userInfo = this.mUserManager.getUserInfo(userId);
        IStorageManager storageManager = this.mInjector.getStorageManager();
        long callingId = Binder.clearCallingIdentity();
        try {
            storageManager.clearUserKeyAuth(userId, userInfo.serialNumber, token, secret);
        } finally {
            Binder.restoreCallingIdentity(callingId);
        }
    }

    private static byte[] secretFromCredential(byte[] credential) throws RemoteException {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-512");
            digest.update(Arrays.copyOf("Android FBE credential hash".getBytes(), 128));
            digest.update(credential);
            return digest.digest();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("NoSuchAlgorithmException for SHA-512");
        }
    }

    private boolean isUserKeyUnlocked(int userId) {
        try {
            return this.mStorageManager.isUserKeyUnlocked(userId);
        } catch (RemoteException e) {
            Log.e(TAG, "failed to check user key locked state", e);
            return false;
        }
    }

    private void unlockUserKey(int userId, byte[] token, byte[] secret) throws RemoteException {
        this.mStorageManager.unlockUserKey(userId, this.mUserManager.getUserInfo(userId).serialNumber, token, secret);
    }

    private void addUserKeyAuth(int userId, byte[] token, byte[] secret) throws RemoteException {
        UserInfo userInfo = this.mUserManager.getUserInfo(userId);
        long callingId = Binder.clearCallingIdentity();
        try {
            this.mStorageManager.addUserKeyAuth(userId, userInfo.serialNumber, token, secret);
        } finally {
            Binder.restoreCallingIdentity(callingId);
        }
    }

    private void fixateNewestUserKeyAuth(int userId) throws RemoteException {
        long callingId = Binder.clearCallingIdentity();
        try {
            this.mStorageManager.fixateNewestUserKeyAuth(userId);
        } finally {
            Binder.restoreCallingIdentity(callingId);
        }
    }

    public void resetKeyStore(int userId) throws RemoteException {
        int i = userId;
        checkWritePermission(userId);
        byte[] managedUserDecryptedPassword = null;
        int managedUserId = -1;
        for (UserInfo pi : this.mUserManager.getProfiles(i)) {
            if (pi.isManagedProfile() && !this.mLockPatternUtils.isSeparateProfileChallengeEnabled(pi.id) && this.mStorage.hasChildProfileLock(pi.id)) {
                if (managedUserId == -1) {
                    try {
                        managedUserDecryptedPassword = getDecryptedPasswordForTiedProfile(pi.id);
                        managedUserId = pi.id;
                    } catch (IOException | InvalidAlgorithmParameterException | InvalidKeyException | KeyStoreException | NoSuchAlgorithmException | UnrecoverableKeyException | CertificateException | BadPaddingException | IllegalBlockSizeException | NoSuchPaddingException e) {
                        Slog.e(TAG, "Failed to decrypt child profile key", e);
                    }
                } else {
                    Slog.e(TAG, "More than one managed profile, uid1:" + managedUserId + ", uid2:" + pi.id);
                }
            }
        }
        try {
            for (int profileId : this.mUserManager.getProfileIdsWithDisabled(i)) {
                for (int uid : SYSTEM_CREDENTIAL_UIDS) {
                    this.mKeyStore.clearUid(UserHandle.getUid(profileId, uid));
                }
            }
            if (managedUserDecryptedPassword != null && managedUserDecryptedPassword.length > 0) {
                Arrays.fill(managedUserDecryptedPassword, (byte) 0);
            }
        } finally {
            if (!(managedUserId == -1 || managedUserDecryptedPassword == null)) {
                tieProfileLockToParent(managedUserId, managedUserDecryptedPassword);
            }
        }
    }

    public VerifyCredentialResponse checkCredential(byte[] credential, int type, int userId, ICheckCredentialProgressCallback progressCallback) throws RemoteException {
        checkPasswordReadPermission(userId);
        VerifyCredentialResponse response = doVerifyCredential(credential, type, false, 0, userId, progressCallback);
        if (response.getResponseCode() == 0 && userId == 0) {
            retainPassword(credential == null ? null : new String(credential));
        }
        return response;
    }

    public VerifyCredentialResponse verifyCredential(byte[] credential, int type, long challenge, int userId) throws RemoteException {
        checkPasswordReadPermission(userId);
        return doVerifyCredential(credential, type, true, challenge, userId, (ICheckCredentialProgressCallback) null);
    }

    private VerifyCredentialResponse doVerifyCredential(byte[] credential, int credentialType, boolean hasChallenge, long challenge, int userId, ICheckCredentialProgressCallback progressCallback) throws RemoteException {
        byte[] credentialToVerify;
        byte[] bArr = credential;
        int i = credentialType;
        int i2 = userId;
        if (bArr == null || bArr.length == 0) {
            throw new IllegalArgumentException("Credential can't be null or empty");
        }
        boolean z = false;
        if (i2 != -9999 || Settings.Global.getInt(this.mContext.getContentResolver(), "device_provisioned", 0) == 0) {
            VerifyCredentialResponse response = spBasedDoVerifyCredential(credential, credentialType, hasChallenge, challenge, userId, progressCallback);
            if (response != null) {
                if (response.getResponseCode() == 0) {
                    sendCredentialsOnUnlockIfRequired(i, bArr, i2);
                }
                return response;
            } else if (i2 == -9999) {
                Slog.wtf(TAG, "Unexpected FRP credential type, should be SP based.");
                return VerifyCredentialResponse.ERROR;
            } else {
                LockSettingsStorage.CredentialHash storedHash = this.mStorage.readCredentialHash(i2);
                if (storedHash.type != i) {
                    Slog.wtf(TAG, "doVerifyCredential type mismatch with stored credential?? stored: " + storedHash.type + " passed in: " + i);
                    return VerifyCredentialResponse.ERROR;
                }
                if (storedHash.type == 1 && storedHash.isBaseZeroPattern) {
                    z = true;
                }
                boolean shouldReEnrollBaseZero = z;
                if (shouldReEnrollBaseZero) {
                    credentialToVerify = LockPatternUtils.patternByteArrayToBaseZero(credential);
                } else {
                    credentialToVerify = credential;
                }
                VerifyCredentialResponse response2 = verifyCredential(userId, storedHash, credentialToVerify, hasChallenge, challenge, progressCallback);
                if (response2.getResponseCode() == 0) {
                    this.mStrongAuth.reportSuccessfulStrongAuthUnlock(i2);
                    if (shouldReEnrollBaseZero) {
                        setLockCredentialInternal(credential, storedHash.type, credentialToVerify, 65536, userId, false, false);
                    }
                }
                return response2;
            }
        } else {
            Slog.e(TAG, "FRP credential can only be verified prior to provisioning.");
            return VerifyCredentialResponse.ERROR;
        }
    }

    public VerifyCredentialResponse verifyTiedProfileChallenge(byte[] credential, int type, long challenge, int userId) throws RemoteException {
        int i = userId;
        checkPasswordReadPermission(i);
        if (isManagedProfileWithUnifiedLock(i)) {
            VerifyCredentialResponse parentResponse = doVerifyCredential(credential, type, true, challenge, this.mUserManager.getProfileParent(i).id, (ICheckCredentialProgressCallback) null);
            if (parentResponse.getResponseCode() != 0) {
                return parentResponse;
            }
            try {
                return doVerifyCredential(getDecryptedPasswordForTiedProfile(i), 2, true, challenge, userId, (ICheckCredentialProgressCallback) null);
            } catch (IOException | InvalidAlgorithmParameterException | InvalidKeyException | KeyStoreException | NoSuchAlgorithmException | UnrecoverableKeyException | CertificateException | BadPaddingException | IllegalBlockSizeException | NoSuchPaddingException e) {
                Slog.e(TAG, "Failed to decrypt child profile key", e);
                throw new RemoteException("Unable to get tied profile token");
            }
        } else {
            throw new RemoteException("User id must be managed profile with unified lock");
        }
    }

    private VerifyCredentialResponse verifyCredential(int userId, LockSettingsStorage.CredentialHash storedHash, byte[] credential, boolean hasChallenge, long challenge, ICheckCredentialProgressCallback progressCallback) throws RemoteException {
        int reEnrollQuality;
        byte[] hash;
        int i;
        int i2 = userId;
        LockSettingsStorage.CredentialHash credentialHash = storedHash;
        byte[] bArr = credential;
        if ((credentialHash == null || credentialHash.hash.length == 0) && (bArr == null || bArr.length == 0)) {
            return VerifyCredentialResponse.OK;
        }
        if (credentialHash == null || bArr == null || bArr.length == 0) {
            return VerifyCredentialResponse.ERROR;
        }
        StrictMode.noteDiskRead();
        if (credentialHash.version == 0) {
            if (credentialHash.type == 1) {
                hash = LockPatternUtils.patternToHash(LockPatternUtils.byteArrayToPattern(credential));
            } else {
                hash = this.mLockPatternUtils.legacyPasswordToHash(bArr, i2).getBytes();
            }
            if (!Arrays.equals(hash, credentialHash.hash)) {
                return VerifyCredentialResponse.ERROR;
            }
            if (credentialHash.type == 1) {
                unlockKeystore(LockPatternUtils.patternByteArrayToBaseZero(credential), i2);
            } else {
                unlockKeystore(bArr, i2);
            }
            Slog.i(TAG, "Unlocking user with fake token: " + i2);
            byte[] fakeToken = String.valueOf(userId).getBytes();
            unlockUser(i2, fakeToken, fakeToken);
            int i3 = credentialHash.type;
            if (credentialHash.type == 1) {
                i = 65536;
            } else {
                i = 327680;
            }
            byte[] bArr2 = fakeToken;
            setLockCredentialInternal(credential, i3, (byte[]) null, i, userId, false, false);
            if (!hasChallenge) {
                notifyActivePasswordMetricsAvailable(credentialHash.type, bArr, i2);
                sendCredentialsOnUnlockIfRequired(credentialHash.type, bArr, i2);
                return VerifyCredentialResponse.OK;
            }
        }
        GateKeeperResponse gateKeeperResponse = getGateKeeperService().verifyChallenge(userId, challenge, credentialHash.hash, credential);
        VerifyCredentialResponse response = convertResponse(gateKeeperResponse);
        boolean shouldReEnroll = gateKeeperResponse.getShouldReEnroll();
        if (response.getResponseCode() == 0) {
            if (progressCallback != null) {
                progressCallback.onCredentialVerified();
            }
            notifyActivePasswordMetricsAvailable(credentialHash.type, bArr, i2);
            unlockKeystore(bArr, i2);
            Slog.i(TAG, "Unlocking user " + i2 + " with token length " + response.getPayload().length);
            unlockUser(i2, response.getPayload(), secretFromCredential(credential));
            if (isManagedProfileWithSeparatedLock(userId)) {
                setDeviceUnlockedForUser(userId);
            }
            if (credentialHash.type == 1) {
                reEnrollQuality = 65536;
            } else {
                reEnrollQuality = 327680;
            }
            if (shouldReEnroll) {
                GateKeeperResponse gateKeeperResponse2 = gateKeeperResponse;
                setLockCredentialInternal(credential, credentialHash.type, credential, reEnrollQuality, userId, false, false);
            } else {
                synchronized (this.mSpManager) {
                    if (shouldMigrateToSyntheticPasswordLocked(userId)) {
                        activateEscrowTokens(initializeSyntheticPasswordLocked(credentialHash.hash, credential, credentialHash.type, reEnrollQuality, userId), i2);
                    }
                }
            }
            sendCredentialsOnUnlockIfRequired(credentialHash.type, bArr, i2);
        } else {
            if (response.getResponseCode() == 1 && response.getTimeout() > 0) {
                requireStrongAuth(8, i2);
            }
        }
        return response;
    }

    private void notifyActivePasswordMetricsAvailable(int credentialType, byte[] password, int userId) {
        this.mHandler.post(new Runnable(PasswordMetrics.computeForCredential(credentialType, password), userId) {
            private final /* synthetic */ PasswordMetrics f$1;
            private final /* synthetic */ int f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void run() {
                LockSettingsService.this.lambda$notifyActivePasswordMetricsAvailable$0$LockSettingsService(this.f$1, this.f$2);
            }
        });
    }

    public /* synthetic */ void lambda$notifyActivePasswordMetricsAvailable$0$LockSettingsService(PasswordMetrics metrics, int userId) {
        ((DevicePolicyManager) this.mContext.getSystemService("device_policy")).setActivePasswordState(metrics, userId);
    }

    private void notifyPasswordChanged(int userId) {
        this.mHandler.post(new Runnable(userId) {
            private final /* synthetic */ int f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                LockSettingsService.this.lambda$notifyPasswordChanged$1$LockSettingsService(this.f$1);
            }
        });
    }

    public /* synthetic */ void lambda$notifyPasswordChanged$1$LockSettingsService(int userId) {
        ((DevicePolicyManager) this.mContext.getSystemService("device_policy")).reportPasswordChanged(userId);
        ((WindowManagerInternal) LocalServices.getService(WindowManagerInternal.class)).reportPasswordChanged(userId);
    }

    public boolean checkVoldPassword(int userId) throws RemoteException {
        if (!this.mFirstCallToVold) {
            return false;
        }
        this.mFirstCallToVold = false;
        checkPasswordReadPermission(userId);
        IStorageManager service = this.mInjector.getStorageManager();
        long identity = Binder.clearCallingIdentity();
        try {
            String password = service.getPassword();
            service.clearPassword();
            if (password == null) {
                return false;
            }
            try {
                if (this.mLockPatternUtils.isLockPatternEnabled(userId) && checkCredential(password.getBytes(), 1, userId, (ICheckCredentialProgressCallback) null).getResponseCode() == 0) {
                    return true;
                }
            } catch (Exception e) {
            }
            try {
                if (!this.mLockPatternUtils.isLockPasswordEnabled(userId) || checkCredential(password.getBytes(), 2, userId, (ICheckCredentialProgressCallback) null).getResponseCode() != 0) {
                    return false;
                }
                return true;
            } catch (Exception e2) {
            }
        } finally {
            Binder.restoreCallingIdentity(identity);
        }
    }

    /* access modifiers changed from: private */
    public void removeUser(int userId, boolean unknownUser) {
        this.mSpManager.removeUser(userId);
        this.mStorage.removeUser(userId);
        this.mStrongAuth.removeUser(userId);
        tryRemoveUserFromSpCacheLater(userId);
        android.security.KeyStore.getInstance().onUserRemoved(userId);
        try {
            IGateKeeperService gk = getGateKeeperService();
            if (gk != null) {
                gk.clearSecureUserId(userId);
            }
        } catch (RemoteException e) {
            Slog.w(TAG, "unable to clear GK secure user id");
        }
        if (unknownUser || this.mUserManager.getUserInfo(userId).isManagedProfile()) {
            removeKeystoreProfileKey(userId);
        }
    }

    private void removeKeystoreProfileKey(int targetUserId) {
        try {
            java.security.KeyStore keyStore = java.security.KeyStore.getInstance("AndroidKeyStore");
            keyStore.load((KeyStore.LoadStoreParameter) null);
            keyStore.deleteEntry("profile_key_name_encrypt_" + targetUserId);
            keyStore.deleteEntry("profile_key_name_decrypt_" + targetUserId);
        } catch (IOException | KeyStoreException | NoSuchAlgorithmException | CertificateException e) {
            Slog.e(TAG, "Unable to remove keystore profile key for user:" + targetUserId, e);
        }
    }

    public void registerStrongAuthTracker(IStrongAuthTracker tracker) {
        checkPasswordReadPermission(-1);
        this.mStrongAuth.registerStrongAuthTracker(tracker);
    }

    public void unregisterStrongAuthTracker(IStrongAuthTracker tracker) {
        checkPasswordReadPermission(-1);
        this.mStrongAuth.unregisterStrongAuthTracker(tracker);
    }

    public void requireStrongAuth(int strongAuthReason, int userId) {
        checkWritePermission(userId);
        this.mStrongAuth.requireStrongAuth(strongAuthReason, userId);
    }

    public void userPresent(int userId) {
        checkWritePermission(userId);
        this.mStrongAuth.reportUnlock(userId);
    }

    public int getStrongAuthForUser(int userId) {
        checkPasswordReadPermission(userId);
        return this.mStrongAuthTracker.getStrongAuthForUser(userId);
    }

    private boolean isCallerShell() {
        int callingUid = Binder.getCallingUid();
        return callingUid == 2000 || callingUid == 0;
    }

    private void enforceShell() {
        if (!isCallerShell()) {
            throw new SecurityException("Caller must be shell");
        }
    }

    /* JADX WARNING: type inference failed for: r4v1, types: [android.os.Binder] */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onShellCommand(java.io.FileDescriptor r13, java.io.FileDescriptor r14, java.io.FileDescriptor r15, java.lang.String[] r16, android.os.ShellCallback r17, android.os.ResultReceiver r18) throws android.os.RemoteException {
        /*
            r12 = this;
            r12.enforceShell()
            long r1 = android.os.Binder.clearCallingIdentity()
            com.android.server.locksettings.LockSettingsShellCommand r3 = new com.android.server.locksettings.LockSettingsShellCommand     // Catch:{ all -> 0x0028 }
            com.android.internal.widget.LockPatternUtils r0 = new com.android.internal.widget.LockPatternUtils     // Catch:{ all -> 0x0028 }
            r11 = r12
            android.content.Context r4 = r11.mContext     // Catch:{ all -> 0x0026 }
            r0.<init>(r4)     // Catch:{ all -> 0x0026 }
            r3.<init>(r0)     // Catch:{ all -> 0x0026 }
            r4 = r12
            r5 = r13
            r6 = r14
            r7 = r15
            r8 = r16
            r9 = r17
            r10 = r18
            r3.exec(r4, r5, r6, r7, r8, r9, r10)     // Catch:{ all -> 0x0026 }
            android.os.Binder.restoreCallingIdentity(r1)
            return
        L_0x0026:
            r0 = move-exception
            goto L_0x002a
        L_0x0028:
            r0 = move-exception
            r11 = r12
        L_0x002a:
            android.os.Binder.restoreCallingIdentity(r1)
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.locksettings.LockSettingsService.onShellCommand(java.io.FileDescriptor, java.io.FileDescriptor, java.io.FileDescriptor, java.lang.String[], android.os.ShellCallback, android.os.ResultReceiver):void");
    }

    public void initRecoveryServiceWithSigFile(String rootCertificateAlias, byte[] recoveryServiceCertFile, byte[] recoveryServiceSigFile) throws RemoteException {
        this.mRecoverableKeyStoreManager.initRecoveryServiceWithSigFile(rootCertificateAlias, recoveryServiceCertFile, recoveryServiceSigFile);
    }

    public KeyChainSnapshot getKeyChainSnapshot() throws RemoteException {
        return this.mRecoverableKeyStoreManager.getKeyChainSnapshot();
    }

    public void setSnapshotCreatedPendingIntent(PendingIntent intent) throws RemoteException {
        this.mRecoverableKeyStoreManager.setSnapshotCreatedPendingIntent(intent);
    }

    public void setServerParams(byte[] serverParams) throws RemoteException {
        this.mRecoverableKeyStoreManager.setServerParams(serverParams);
    }

    public void setRecoveryStatus(String alias, int status) throws RemoteException {
        this.mRecoverableKeyStoreManager.setRecoveryStatus(alias, status);
    }

    public Map getRecoveryStatus() throws RemoteException {
        return this.mRecoverableKeyStoreManager.getRecoveryStatus();
    }

    public void setRecoverySecretTypes(int[] secretTypes) throws RemoteException {
        this.mRecoverableKeyStoreManager.setRecoverySecretTypes(secretTypes);
    }

    public int[] getRecoverySecretTypes() throws RemoteException {
        return this.mRecoverableKeyStoreManager.getRecoverySecretTypes();
    }

    public byte[] startRecoverySessionWithCertPath(String sessionId, String rootCertificateAlias, RecoveryCertPath verifierCertPath, byte[] vaultParams, byte[] vaultChallenge, List<KeyChainProtectionParams> secrets) throws RemoteException {
        return this.mRecoverableKeyStoreManager.startRecoverySessionWithCertPath(sessionId, rootCertificateAlias, verifierCertPath, vaultParams, vaultChallenge, secrets);
    }

    public Map<String, String> recoverKeyChainSnapshot(String sessionId, byte[] recoveryKeyBlob, List<WrappedApplicationKey> applicationKeys) throws RemoteException {
        return this.mRecoverableKeyStoreManager.recoverKeyChainSnapshot(sessionId, recoveryKeyBlob, applicationKeys);
    }

    public void closeSession(String sessionId) throws RemoteException {
        this.mRecoverableKeyStoreManager.closeSession(sessionId);
    }

    public void removeKey(String alias) throws RemoteException {
        this.mRecoverableKeyStoreManager.removeKey(alias);
    }

    public String generateKey(String alias) throws RemoteException {
        return this.mRecoverableKeyStoreManager.generateKey(alias);
    }

    public String generateKeyWithMetadata(String alias, byte[] metadata) throws RemoteException {
        return this.mRecoverableKeyStoreManager.generateKeyWithMetadata(alias, metadata);
    }

    public String importKey(String alias, byte[] keyBytes) throws RemoteException {
        return this.mRecoverableKeyStoreManager.importKey(alias, keyBytes);
    }

    public String importKeyWithMetadata(String alias, byte[] keyBytes, byte[] metadata) throws RemoteException {
        return this.mRecoverableKeyStoreManager.importKeyWithMetadata(alias, keyBytes, metadata);
    }

    public String getKey(String alias) throws RemoteException {
        return this.mRecoverableKeyStoreManager.getKey(alias);
    }

    private class GateKeeperDiedRecipient implements IBinder.DeathRecipient {
        private GateKeeperDiedRecipient() {
        }

        public void binderDied() {
            LockSettingsService.this.mGateKeeperService.asBinder().unlinkToDeath(this, 0);
            LockSettingsService.this.mGateKeeperService = null;
        }
    }

    /* access modifiers changed from: protected */
    public synchronized IGateKeeperService getGateKeeperService() throws RemoteException {
        if (this.mGateKeeperService != null) {
            return this.mGateKeeperService;
        }
        IBinder service = ServiceManager.getService("android.service.gatekeeper.IGateKeeperService");
        if (service != null) {
            service.linkToDeath(new GateKeeperDiedRecipient(), 0);
            this.mGateKeeperService = IGateKeeperService.Stub.asInterface(service);
            return this.mGateKeeperService;
        }
        Slog.e(TAG, "Unable to acquire GateKeeperService");
        return null;
    }

    private void onAuthTokenKnownForUser(int userId, SyntheticPasswordManager.AuthenticationToken auth) {
        Slog.i(TAG, "Caching SP for user " + userId);
        synchronized (this.mSpManager) {
            this.mSpCache.put(userId, auth);
        }
        tryRemoveUserFromSpCacheLater(userId);
        if (this.mInjector.isGsiRunning()) {
            Slog.w(TAG, "AuthSecret disabled in GSI");
        } else if (this.mAuthSecretService != null && this.mUserManager.getUserInfo(userId).isPrimary()) {
            try {
                byte[] rawSecret = auth.deriveVendorAuthSecret();
                ArrayList<Byte> secret = new ArrayList<>(rawSecret.length);
                for (byte valueOf : rawSecret) {
                    secret.add(Byte.valueOf(valueOf));
                }
                this.mAuthSecretService.primaryUserCredential(secret);
            } catch (RemoteException e) {
                Slog.w(TAG, "Failed to pass primary user secret to AuthSecret HAL", e);
            }
        }
    }

    /* access modifiers changed from: private */
    public void tryRemoveUserFromSpCacheLater(int userId) {
        this.mHandler.post(new Runnable(userId) {
            private final /* synthetic */ int f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                LockSettingsService.this.lambda$tryRemoveUserFromSpCacheLater$2$LockSettingsService(this.f$1);
            }
        });
    }

    public /* synthetic */ void lambda$tryRemoveUserFromSpCacheLater$2$LockSettingsService(int userId) {
        if (!shouldCacheSpForUser(userId)) {
            Slog.i(TAG, "Removing SP from cache for user " + userId);
            synchronized (this.mSpManager) {
                this.mSpCache.remove(userId);
            }
        }
    }

    private boolean shouldCacheSpForUser(int userId) {
        if (Settings.Secure.getIntForUser(this.mContext.getContentResolver(), "user_setup_complete", 0, userId) == 0) {
            return true;
        }
        DevicePolicyManagerInternal dpmi = (DevicePolicyManagerInternal) LocalServices.getService(DevicePolicyManagerInternal.class);
        if (dpmi == null) {
            return false;
        }
        return dpmi.canUserHaveUntrustedCredentialReset(userId);
    }

    /* access modifiers changed from: protected */
    @GuardedBy({"mSpManager"})
    @VisibleForTesting
    public SyntheticPasswordManager.AuthenticationToken initializeSyntheticPasswordLocked(byte[] credentialHash, byte[] credential, int credentialType, int requestedQuality, int userId) throws RemoteException {
        byte[] bArr = credentialHash;
        byte[] bArr2 = credential;
        int i = userId;
        Slog.i(TAG, "Initialize SyntheticPassword for user: " + i);
        SyntheticPasswordManager.AuthenticationToken auth = this.mSpManager.newSyntheticPasswordAndSid(getGateKeeperService(), bArr, bArr2, i);
        onAuthTokenKnownForUser(i, auth);
        if (auth == null) {
            Slog.wtf(TAG, "initializeSyntheticPasswordLocked returns null auth token");
            return null;
        }
        long handle = this.mSpManager.createPasswordBasedSyntheticPassword(getGateKeeperService(), credential, credentialType, auth, requestedQuality, userId);
        if (bArr2 != null) {
            if (bArr == null) {
                this.mSpManager.newSidForUser(getGateKeeperService(), auth, i);
            }
            this.mSpManager.verifyChallenge(getGateKeeperService(), auth, 0, userId);
            setAuthlessUserKeyProtection(i, auth.deriveDiskEncryptionKey());
            setKeystorePassword(auth.deriveKeyStorePassword(), i);
        } else {
            clearUserKeyProtection(i);
            setKeystorePassword((byte[]) null, i);
            getGateKeeperService().clearSecureUserId(i);
        }
        fixateNewestUserKeyAuth(i);
        setLong("sp-handle", handle, i);
        return auth;
    }

    private long getSyntheticPasswordHandleLocked(int userId) {
        return getLong("sp-handle", 0, userId);
    }

    private boolean isSyntheticPasswordBasedCredentialLocked(int userId) {
        if (userId == -9999) {
            int type = this.mStorage.readPersistentDataBlock().type;
            if (type == 1 || type == 2) {
                return true;
            }
            return false;
        }
        long handle = getSyntheticPasswordHandleLocked(userId);
        if (getLong("enable-sp", 1, 0) == 0 || handle == 0) {
            return false;
        }
        return true;
    }

    /* access modifiers changed from: protected */
    @VisibleForTesting
    public boolean shouldMigrateToSyntheticPasswordLocked(int userId) {
        long handle = getSyntheticPasswordHandleLocked(userId);
        if (getLong("enable-sp", 1, 0) == 0 || handle != 0) {
            return false;
        }
        return true;
    }

    private void enableSyntheticPasswordLocked() {
        setLong("enable-sp", 1, 0);
    }

    private VerifyCredentialResponse spBasedDoVerifyCredential(byte[] userCredential, int credentialType, boolean hasChallenge, long challenge, int userId, ICheckCredentialProgressCallback progressCallback) throws RemoteException {
        byte[] userCredential2;
        long challenge2;
        SyntheticPasswordManager syntheticPasswordManager;
        byte[] bArr;
        int i = credentialType;
        int i2 = userId;
        if (i == -1) {
            userCredential2 = null;
        } else {
            userCredential2 = userCredential;
        }
        PackageManager pm = this.mContext.getPackageManager();
        if (hasChallenge || !pm.hasSystemFeature("android.hardware.biometrics.face") || !this.mInjector.hasEnrolledBiometrics()) {
            challenge2 = challenge;
        } else {
            challenge2 = ((FaceManager) this.mContext.getSystemService(FaceManager.class)).generateChallenge();
        }
        SyntheticPasswordManager syntheticPasswordManager2 = this.mSpManager;
        synchronized (syntheticPasswordManager2) {
            try {
                if (!isSyntheticPasswordBasedCredentialLocked(i2)) {
                    return null;
                }
                if (i2 == -9999) {
                    VerifyCredentialResponse verifyFrpCredential = this.mSpManager.verifyFrpCredential(getGateKeeperService(), userCredential2, i, progressCallback);
                    return verifyFrpCredential;
                }
                ICheckCredentialProgressCallback iCheckCredentialProgressCallback = progressCallback;
                SyntheticPasswordManager.AuthenticationResult authResult = this.mSpManager.unwrapPasswordBasedSyntheticPassword(getGateKeeperService(), getSyntheticPasswordHandleLocked(i2), userCredential2, userId, progressCallback);
                if (authResult.credentialType != i) {
                    Slog.e(TAG, "Credential type mismatch.");
                    VerifyCredentialResponse verifyCredentialResponse = VerifyCredentialResponse.ERROR;
                    return verifyCredentialResponse;
                }
                VerifyCredentialResponse response = authResult.gkResponse;
                if (response.getResponseCode() == 0) {
                    bArr = null;
                    syntheticPasswordManager = syntheticPasswordManager2;
                    try {
                        response = this.mSpManager.verifyChallenge(getGateKeeperService(), authResult.authToken, challenge2, userId);
                        if (response.getResponseCode() != 0) {
                            Slog.wtf(TAG, "verifyChallenge with SP failed.");
                            VerifyCredentialResponse verifyCredentialResponse2 = VerifyCredentialResponse.ERROR;
                            return verifyCredentialResponse2;
                        }
                    } catch (Throwable th) {
                        th = th;
                        throw th;
                    }
                } else {
                    bArr = null;
                    syntheticPasswordManager = syntheticPasswordManager2;
                }
                if (response.getResponseCode() == 0) {
                    notifyActivePasswordMetricsAvailable(i, userCredential2, i2);
                    unlockKeystore(authResult.authToken.deriveKeyStorePassword(), i2);
                    if (this.mInjector.hasEnrolledBiometrics()) {
                        Slog.i(TAG, "Resetting lockout, length: " + authResult.gkResponse.getPayload().length);
                        ((BiometricManager) this.mContext.getSystemService(BiometricManager.class)).resetLockout(authResult.gkResponse.getPayload());
                        if (!hasChallenge && pm.hasSystemFeature("android.hardware.biometrics.face")) {
                            ((FaceManager) this.mContext.getSystemService(FaceManager.class)).revokeChallenge();
                        }
                    }
                    byte[] secret = authResult.authToken.deriveDiskEncryptionKey();
                    Slog.i(TAG, "Unlocking user " + i2 + " with secret only, length " + secret.length);
                    unlockUser(i2, bArr, secret);
                    activateEscrowTokens(authResult.authToken, i2);
                    if (isManagedProfileWithSeparatedLock(i2)) {
                        setDeviceUnlockedForUser(i2);
                    }
                    this.mStrongAuth.reportSuccessfulStrongAuthUnlock(i2);
                    onAuthTokenKnownForUser(i2, authResult.authToken);
                } else if (response.getResponseCode() == 1 && response.getTimeout() > 0) {
                    requireStrongAuth(8, i2);
                }
                return response;
            } catch (Throwable th2) {
                th = th2;
                syntheticPasswordManager = syntheticPasswordManager2;
                throw th;
            }
        }
    }

    private void setDeviceUnlockedForUser(int userId) {
        ((TrustManager) this.mContext.getSystemService(TrustManager.class)).setDeviceLockedForUser(userId, false);
    }

    @GuardedBy({"mSpManager"})
    private long setLockCredentialWithAuthTokenLocked(byte[] credential, int credentialType, SyntheticPasswordManager.AuthenticationToken auth, int requestedQuality, int userId) throws RemoteException {
        Map<Integer, byte[]> profilePasswords;
        byte[] bArr = credential;
        int i = userId;
        long newHandle = this.mSpManager.createPasswordBasedSyntheticPassword(getGateKeeperService(), credential, credentialType, auth, requestedQuality, userId);
        if (bArr != null) {
            profilePasswords = null;
            if (this.mSpManager.hasSidForUser(i)) {
                this.mSpManager.verifyChallenge(getGateKeeperService(), auth, 0, userId);
                SyntheticPasswordManager.AuthenticationToken authenticationToken = auth;
            } else {
                this.mSpManager.newSidForUser(getGateKeeperService(), auth, i);
                this.mSpManager.verifyChallenge(getGateKeeperService(), auth, 0, userId);
                setAuthlessUserKeyProtection(i, auth.deriveDiskEncryptionKey());
                fixateNewestUserKeyAuth(i);
                setKeystorePassword(auth.deriveKeyStorePassword(), i);
            }
        } else {
            SyntheticPasswordManager.AuthenticationToken authenticationToken2 = auth;
            profilePasswords = getDecryptedPasswordsForAllTiedProfiles(i);
            this.mSpManager.clearSidForUser(i);
            getGateKeeperService().clearSecureUserId(i);
            unlockUserKey(i, (byte[]) null, auth.deriveDiskEncryptionKey());
            clearUserKeyAuth(i, (byte[]) null, auth.deriveDiskEncryptionKey());
            fixateNewestUserKeyAuth(i);
            unlockKeystore(auth.deriveKeyStorePassword(), i);
            setKeystorePassword((byte[]) null, i);
        }
        setLong("sp-handle", newHandle, i);
        synchronizeUnifiedWorkChallengeForProfiles(i, profilePasswords);
        int i2 = credentialType;
        notifyActivePasswordMetricsAvailable(credentialType, credential, i);
        if (profilePasswords != null) {
            for (Map.Entry<Integer, byte[]> entry : profilePasswords.entrySet()) {
                Arrays.fill(entry.getValue(), (byte) 0);
            }
        }
        return newHandle;
    }

    /* JADX WARNING: Removed duplicated region for block: B:20:0x0063  */
    /* JADX WARNING: Removed duplicated region for block: B:21:0x0069  */
    /* JADX WARNING: Removed duplicated region for block: B:34:0x00a6  */
    /* JADX WARNING: Removed duplicated region for block: B:38:0x00ce  */
    @com.android.internal.annotations.GuardedBy({"mSpManager"})
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void spBasedSetLockCredentialInternalLocked(byte[] r17, int r18, byte[] r19, int r20, int r21, boolean r22, boolean r23) throws android.os.RemoteException {
        /*
            r16 = this;
            r7 = r16
            r8 = r18
            r6 = r21
            boolean r0 = r7.isManagedProfileWithUnifiedLock(r6)
            java.lang.String r1 = "LockSettingsService"
            if (r0 == 0) goto L_0x0022
            byte[] r0 = r7.getDecryptedPasswordForTiedProfile(r6)     // Catch:{ FileNotFoundException -> 0x001a, IOException | InvalidAlgorithmParameterException | InvalidKeyException | KeyStoreException | NoSuchAlgorithmException | UnrecoverableKeyException | CertificateException | BadPaddingException | IllegalBlockSizeException | NoSuchPaddingException -> 0x0013 }
            goto L_0x0024
        L_0x0013:
            r0 = move-exception
            java.lang.String r2 = "Failed to decrypt child profile key"
            android.util.Slog.e(r1, r2, r0)
            goto L_0x0022
        L_0x001a:
            r0 = move-exception
            r2 = r0
            r0 = r2
            java.lang.String r2 = "Child profile key not found"
            android.util.Slog.i(r1, r2)
        L_0x0022:
            r0 = r19
        L_0x0024:
            long r4 = r7.getSyntheticPasswordHandleLocked(r6)
            com.android.server.locksettings.SyntheticPasswordManager r9 = r7.mSpManager
            android.service.gatekeeper.IGateKeeperService r10 = r16.getGateKeeperService()
            r15 = 0
            r11 = r4
            r13 = r0
            r14 = r21
            com.android.server.locksettings.SyntheticPasswordManager$AuthenticationResult r9 = r9.unwrapPasswordBasedSyntheticPassword(r10, r11, r13, r14, r15)
            com.android.internal.widget.VerifyCredentialResponse r10 = r9.gkResponse
            com.android.server.locksettings.SyntheticPasswordManager$AuthenticationToken r2 = r9.authToken
            if (r0 == 0) goto L_0x0060
            if (r2 != 0) goto L_0x0060
            java.lang.IllegalStateException r1 = new java.lang.IllegalStateException
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            java.lang.String r11 = "Failed to enroll "
            r3.append(r11)
            r11 = 2
            if (r8 != r11) goto L_0x0052
            java.lang.String r11 = "password"
            goto L_0x0055
        L_0x0052:
            java.lang.String r11 = "pattern"
        L_0x0055:
            r3.append(r11)
            java.lang.String r3 = r3.toString()
            r1.<init>(r3)
            throw r1
        L_0x0060:
            r3 = 0
            if (r2 == 0) goto L_0x0069
            r7.onAuthTokenKnownForUser(r6, r2)
            r11 = r2
            r12 = r3
            goto L_0x00a4
        L_0x0069:
            if (r10 == 0) goto L_0x00df
            int r11 = r10.getResponseCode()
            r12 = -1
            if (r11 != r12) goto L_0x00d6
            java.lang.String r11 = "Untrusted credential change invoked"
            android.util.Slog.w(r1, r11)
            android.util.SparseArray<com.android.server.locksettings.SyntheticPasswordManager$AuthenticationToken> r1 = r7.mSpCache
            java.lang.Object r1 = r1.get(r6)
            com.android.server.locksettings.SyntheticPasswordManager$AuthenticationToken r1 = (com.android.server.locksettings.SyntheticPasswordManager.AuthenticationToken) r1
            if (r22 != 0) goto L_0x00a1
            java.lang.IllegalStateException r2 = new java.lang.IllegalStateException
            java.lang.StringBuilder r11 = new java.lang.StringBuilder
            r11.<init>()
            java.lang.String r12 = "Untrusted credential change was invoked but it was not allowed. This is likely a bug. Auth token is null: "
            r11.append(r12)
            if (r1 != 0) goto L_0x0091
            r12 = 1
            goto L_0x0092
        L_0x0091:
            r12 = 0
        L_0x0092:
            java.lang.String r12 = java.lang.Boolean.toString(r12)
            r11.append(r12)
            java.lang.String r11 = r11.toString()
            r2.<init>(r11)
            throw r2
        L_0x00a1:
            r3 = 1
            r11 = r1
            r12 = r3
        L_0x00a4:
            if (r11 == 0) goto L_0x00ce
            if (r12 == 0) goto L_0x00b1
            com.android.server.locksettings.SyntheticPasswordManager r1 = r7.mSpManager
            android.service.gatekeeper.IGateKeeperService r2 = r16.getGateKeeperService()
            r1.newSidForUser(r2, r11, r6)
        L_0x00b1:
            r1 = r16
            r2 = r17
            r3 = r18
            r13 = r4
            r4 = r11
            r5 = r20
            r15 = r6
            r6 = r21
            r1.setLockCredentialWithAuthTokenLocked(r2, r3, r4, r5, r6)
            com.android.server.locksettings.SyntheticPasswordManager r1 = r7.mSpManager
            r1.destroyPasswordBasedSyntheticPassword(r13, r15)
            r1 = r17
            r4 = r23
            r7.sendCredentialsOnChangeIfRequired(r8, r1, r15, r4)
            return
        L_0x00ce:
            java.lang.IllegalStateException r2 = new java.lang.IllegalStateException
            java.lang.String r3 = "Untrusted credential reset not possible without cached SP"
            r2.<init>(r3)
            throw r2
        L_0x00d6:
            r15 = r6
            java.lang.IllegalStateException r5 = new java.lang.IllegalStateException
            java.lang.String r6 = "Rate limit exceeded, so password was not changed."
            r5.<init>(r6)
            throw r5
        L_0x00df:
            r15 = r6
            java.lang.IllegalStateException r5 = new java.lang.IllegalStateException
            java.lang.String r6 = "Password change failed."
            r5.<init>(r6)
            throw r5
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.locksettings.LockSettingsService.spBasedSetLockCredentialInternalLocked(byte[], int, byte[], int, int, boolean, boolean):void");
    }

    public byte[] getHashFactor(byte[] currentCredential, int userId) throws RemoteException {
        checkPasswordReadPermission(userId);
        if (currentCredential == null || currentCredential.length == 0) {
            currentCredential = null;
        }
        if (isManagedProfileWithUnifiedLock(userId)) {
            try {
                currentCredential = getDecryptedPasswordForTiedProfile(userId);
            } catch (Exception e) {
                Slog.e(TAG, "Failed to get work profile credential", e);
                return null;
            }
        }
        synchronized (this.mSpManager) {
            if (!isSyntheticPasswordBasedCredentialLocked(userId)) {
                Slog.w(TAG, "Synthetic password not enabled");
                return null;
            }
            SyntheticPasswordManager.AuthenticationResult auth = this.mSpManager.unwrapPasswordBasedSyntheticPassword(getGateKeeperService(), getSyntheticPasswordHandleLocked(userId), currentCredential, userId, (ICheckCredentialProgressCallback) null);
            if (auth.authToken == null) {
                Slog.w(TAG, "Current credential is incorrect");
                return null;
            }
            byte[] derivePasswordHashFactor = auth.authToken.derivePasswordHashFactor();
            return derivePasswordHashFactor;
        }
    }

    /* Debug info: failed to restart local var, previous not found, register: 9 */
    /* access modifiers changed from: private */
    public long addEscrowToken(byte[] token, int userId, LockPatternUtils.EscrowTokenStateChangeCallback callback) throws RemoteException {
        long handle;
        synchronized (this.mSpManager) {
            enableSyntheticPasswordLocked();
            SyntheticPasswordManager.AuthenticationToken auth = null;
            if (!isUserSecure(userId)) {
                if (shouldMigrateToSyntheticPasswordLocked(userId)) {
                    auth = initializeSyntheticPasswordLocked((byte[]) null, (byte[]) null, -1, 0, userId);
                } else {
                    auth = this.mSpManager.unwrapPasswordBasedSyntheticPassword(getGateKeeperService(), getSyntheticPasswordHandleLocked(userId), (byte[]) null, userId, (ICheckCredentialProgressCallback) null).authToken;
                }
            }
            if (isSyntheticPasswordBasedCredentialLocked(userId)) {
                disableEscrowTokenOnNonManagedDevicesIfNeeded(userId);
                if (!this.mSpManager.hasEscrowData(userId)) {
                    throw new SecurityException("Escrow token is disabled on the current user");
                }
            }
            handle = this.mSpManager.createTokenBasedSyntheticPassword(token, userId, callback);
            if (auth != null) {
                this.mSpManager.activateTokenBasedSyntheticPassword(handle, auth, userId);
            }
        }
        return handle;
    }

    private void activateEscrowTokens(SyntheticPasswordManager.AuthenticationToken auth, int userId) {
        synchronized (this.mSpManager) {
            disableEscrowTokenOnNonManagedDevicesIfNeeded(userId);
            for (Long longValue : this.mSpManager.getPendingTokensForUser(userId)) {
                long handle = longValue.longValue();
                Slog.i(TAG, String.format("activateEscrowTokens: %x %d ", new Object[]{Long.valueOf(handle), Integer.valueOf(userId)}));
                this.mSpManager.activateTokenBasedSyntheticPassword(handle, auth, userId);
            }
        }
    }

    /* access modifiers changed from: private */
    public boolean isEscrowTokenActive(long handle, int userId) {
        boolean existsHandle;
        synchronized (this.mSpManager) {
            existsHandle = this.mSpManager.existsHandle(handle, userId);
        }
        return existsHandle;
    }

    public boolean hasPendingEscrowToken(int userId) {
        boolean z;
        checkPasswordReadPermission(userId);
        synchronized (this.mSpManager) {
            z = !this.mSpManager.getPendingTokensForUser(userId).isEmpty();
        }
        return z;
    }

    /* access modifiers changed from: private */
    public boolean removeEscrowToken(long handle, int userId) {
        synchronized (this.mSpManager) {
            if (handle == getSyntheticPasswordHandleLocked(userId)) {
                Slog.w(TAG, "Cannot remove password handle");
                return false;
            } else if (this.mSpManager.removePendingToken(handle, userId)) {
                return true;
            } else {
                if (!this.mSpManager.existsHandle(handle, userId)) {
                    return false;
                }
                this.mSpManager.destroyTokenBasedSyntheticPassword(handle, userId);
                return true;
            }
        }
    }

    /* Debug info: failed to restart local var, previous not found, register: 4 */
    /* access modifiers changed from: private */
    public boolean setLockCredentialWithToken(byte[] credential, int type, long tokenHandle, byte[] token, int requestedQuality, int userId) throws RemoteException {
        boolean result;
        synchronized (this.mSpManager) {
            if (this.mSpManager.hasEscrowData(userId)) {
                result = setLockCredentialWithTokenInternalLocked(credential, type, tokenHandle, token, requestedQuality, userId);
            } else {
                throw new SecurityException("Escrow token is disabled on the current user");
            }
        }
        if (result) {
            synchronized (this.mSeparateChallengeLock) {
                setSeparateProfileChallengeEnabledLocked(userId, true, (byte[]) null);
            }
            if (credential == null) {
                this.mHandler.post(new Runnable(userId) {
                    private final /* synthetic */ int f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final void run() {
                        LockSettingsService.this.lambda$setLockCredentialWithToken$3$LockSettingsService(this.f$1);
                    }
                });
            }
            notifyPasswordChanged(userId);
            notifySeparateProfileChallengeChanged(userId);
        }
        return result;
    }

    public /* synthetic */ void lambda$setLockCredentialWithToken$3$LockSettingsService(int userId) {
        unlockUser(userId, (byte[]) null, (byte[]) null);
    }

    @GuardedBy({"mSpManager"})
    private boolean setLockCredentialWithTokenInternalLocked(byte[] credential, int type, long tokenHandle, byte[] token, int requestedQuality, int userId) throws RemoteException {
        int i = userId;
        SyntheticPasswordManager.AuthenticationResult result = this.mSpManager.unwrapTokenBasedSyntheticPassword(getGateKeeperService(), tokenHandle, token, userId);
        if (result.authToken == null) {
            Slog.w(TAG, "Invalid escrow token supplied");
            return false;
        } else if (result.gkResponse.getResponseCode() != 0) {
            Slog.e(TAG, "Obsolete token: synthetic password derived but it fails GK verification.");
            return false;
        } else {
            setLong("lockscreen.password_type", (long) requestedQuality, i);
            long oldHandle = getSyntheticPasswordHandleLocked(i);
            setLockCredentialWithAuthTokenLocked(credential, type, result.authToken, requestedQuality, userId);
            this.mSpManager.destroyPasswordBasedSyntheticPassword(oldHandle, i);
            onAuthTokenKnownForUser(i, result.authToken);
            return true;
        }
    }

    /* Debug info: failed to restart local var, previous not found, register: 8 */
    /* access modifiers changed from: private */
    public boolean unlockUserWithToken(long tokenHandle, byte[] token, int userId) throws RemoteException {
        synchronized (this.mSpManager) {
            if (this.mSpManager.hasEscrowData(userId)) {
                SyntheticPasswordManager.AuthenticationResult authResult = this.mSpManager.unwrapTokenBasedSyntheticPassword(getGateKeeperService(), tokenHandle, token, userId);
                if (authResult.authToken == null) {
                    Slog.w(TAG, "Invalid escrow token supplied");
                    return false;
                }
                unlockUser(userId, (byte[]) null, authResult.authToken.deriveDiskEncryptionKey());
                onAuthTokenKnownForUser(userId, authResult.authToken);
                return true;
            }
            throw new SecurityException("Escrow token is disabled on the current user");
        }
    }

    /* access modifiers changed from: protected */
    public void dump(FileDescriptor fd, PrintWriter pw, String[] args) {
        if (DumpUtils.checkDumpPermission(this.mContext, TAG, pw)) {
            pw.println("Current lock settings service state:");
            pw.println(String.format("SP Enabled = %b", new Object[]{Boolean.valueOf(this.mLockPatternUtils.isSyntheticPasswordEnabled())}));
            List<UserInfo> users = this.mUserManager.getUsers();
            for (int user = 0; user < users.size(); user++) {
                int userId = users.get(user).id;
                pw.println("    User " + userId);
                synchronized (this.mSpManager) {
                    pw.println(String.format("        SP Handle = %x", new Object[]{Long.valueOf(getSyntheticPasswordHandleLocked(userId))}));
                }
                try {
                    pw.println(String.format("        SID = %x", new Object[]{Long.valueOf(getGateKeeperService().getSecureUserId(userId))}));
                } catch (RemoteException e) {
                }
            }
        }
    }

    private void disableEscrowTokenOnNonManagedDevicesIfNeeded(int userId) {
        long ident = Binder.clearCallingIdentity();
        try {
            if (this.mUserManager.getUserInfo(userId).isManagedProfile()) {
                Slog.i(TAG, "Managed profile can have escrow token");
                return;
            }
            DevicePolicyManager dpm = this.mInjector.getDevicePolicyManager();
            if (dpm.getDeviceOwnerComponentOnAnyUser() != null) {
                Slog.i(TAG, "Corp-owned device can have escrow token");
                Binder.restoreCallingIdentity(ident);
            } else if (dpm.getProfileOwnerAsUser(userId) != null) {
                Slog.i(TAG, "User with profile owner can have escrow token");
                Binder.restoreCallingIdentity(ident);
            } else if (!dpm.isDeviceProvisioned()) {
                Slog.i(TAG, "Postpone disabling escrow tokens until device is provisioned");
                Binder.restoreCallingIdentity(ident);
            } else if (this.mContext.getPackageManager().hasSystemFeature("android.hardware.type.automotive")) {
                Binder.restoreCallingIdentity(ident);
            } else {
                Slog.i(TAG, "Disabling escrow token on user " + userId);
                if (isSyntheticPasswordBasedCredentialLocked(userId)) {
                    this.mSpManager.destroyEscrowData(userId);
                }
                Binder.restoreCallingIdentity(ident);
            }
        } finally {
            Binder.restoreCallingIdentity(ident);
        }
    }

    private class DeviceProvisionedObserver extends ContentObserver {
        private final Uri mDeviceProvisionedUri = Settings.Global.getUriFor("device_provisioned");
        private boolean mRegistered;
        private final Uri mUserSetupCompleteUri = Settings.Secure.getUriFor("user_setup_complete");

        public DeviceProvisionedObserver() {
            super((Handler) null);
        }

        public void onChange(boolean selfChange, Uri uri, int userId) {
            if (this.mDeviceProvisionedUri.equals(uri)) {
                updateRegistration();
                if (isProvisioned()) {
                    Slog.i(LockSettingsService.TAG, "Reporting device setup complete to IGateKeeperService");
                    reportDeviceSetupComplete();
                    clearFrpCredentialIfOwnerNotSecure();
                }
            } else if (this.mUserSetupCompleteUri.equals(uri)) {
                LockSettingsService.this.tryRemoveUserFromSpCacheLater(userId);
            }
        }

        public void onSystemReady() {
            if (LockPatternUtils.frpCredentialEnabled(LockSettingsService.this.mContext)) {
                updateRegistration();
            } else if (!isProvisioned()) {
                Slog.i(LockSettingsService.TAG, "FRP credential disabled, reporting device setup complete to Gatekeeper immediately");
                reportDeviceSetupComplete();
            }
        }

        private void reportDeviceSetupComplete() {
            try {
                LockSettingsService.this.getGateKeeperService().reportDeviceSetupComplete();
            } catch (RemoteException e) {
                Slog.e(LockSettingsService.TAG, "Failure reporting to IGateKeeperService", e);
            }
        }

        private void clearFrpCredentialIfOwnerNotSecure() {
            for (UserInfo user : LockSettingsService.this.mUserManager.getUsers()) {
                if (LockPatternUtils.userOwnsFrpCredential(LockSettingsService.this.mContext, user)) {
                    if (!LockSettingsService.this.isUserSecure(user.id)) {
                        LockSettingsService.this.mStorage.writePersistentDataBlock(0, user.id, 0, (byte[]) null);
                        return;
                    }
                    return;
                }
            }
        }

        private void updateRegistration() {
            boolean register = !isProvisioned();
            if (register != this.mRegistered) {
                if (register) {
                    LockSettingsService.this.mContext.getContentResolver().registerContentObserver(this.mDeviceProvisionedUri, false, this);
                    LockSettingsService.this.mContext.getContentResolver().registerContentObserver(this.mUserSetupCompleteUri, false, this, -1);
                } else {
                    LockSettingsService.this.mContext.getContentResolver().unregisterContentObserver(this);
                }
                this.mRegistered = register;
            }
        }

        private boolean isProvisioned() {
            return Settings.Global.getInt(LockSettingsService.this.mContext.getContentResolver(), "device_provisioned", 0) != 0;
        }
    }

    private final class LocalService extends LockSettingsInternal {
        private LocalService() {
        }

        public long addEscrowToken(byte[] token, int userId, LockPatternUtils.EscrowTokenStateChangeCallback callback) {
            try {
                return LockSettingsService.this.addEscrowToken(token, userId, callback);
            } catch (RemoteException re) {
                throw re.rethrowFromSystemServer();
            }
        }

        public boolean removeEscrowToken(long handle, int userId) {
            return LockSettingsService.this.removeEscrowToken(handle, userId);
        }

        public boolean isEscrowTokenActive(long handle, int userId) {
            return LockSettingsService.this.isEscrowTokenActive(handle, userId);
        }

        public boolean setLockCredentialWithToken(byte[] credential, int type, long tokenHandle, byte[] token, int requestedQuality, int userId) {
            if (LockSettingsService.this.mLockPatternUtils.hasSecureLockScreen()) {
                try {
                    return LockSettingsService.this.setLockCredentialWithToken(credential, type, tokenHandle, token, requestedQuality, userId);
                } catch (RemoteException re) {
                    throw re.rethrowFromSystemServer();
                }
            } else {
                throw new UnsupportedOperationException("This operation requires secure lock screen feature.");
            }
        }

        public boolean unlockUserWithToken(long tokenHandle, byte[] token, int userId) {
            try {
                return LockSettingsService.this.unlockUserWithToken(tokenHandle, token, userId);
            } catch (RemoteException re) {
                throw re.rethrowFromSystemServer();
            }
        }
    }

    public void setRawLockPassword(byte[] hash, int userId) throws RemoteException {
        checkWritePermission(userId);
        this.mStorage.writeCredentialHash(LockSettingsStorage.CredentialHash.create(hash, 2), userId);
    }

    public void savePrivacyPasswordPattern(String pattern, String filename, int userId) {
        checkWritePermission(userId);
        MiuiLockPatternUtils.savePrivacyPasswordPattern(pattern, filename, userId);
    }

    public boolean checkPrivacyPasswordPattern(String pattern, String filename, int userId) {
        checkPasswordReadPermission(userId);
        return MiuiLockPatternUtils.checkPrivacyPasswordPattern(pattern, filename, userId);
    }
}
