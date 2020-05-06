package com.android.server.accounts;

import android.accounts.Account;
import android.accounts.AccountAndUser;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.AccountManagerInternal;
import android.accounts.AccountManagerResponse;
import android.accounts.AuthenticatorDescription;
import android.accounts.CantAddAccountActivity;
import android.accounts.ChooseAccountActivity;
import android.accounts.GrantCredentialsPermissionActivity;
import android.accounts.IAccountAuthenticator;
import android.accounts.IAccountAuthenticatorResponse;
import android.accounts.IAccountManager;
import android.accounts.IAccountManagerResponse;
import android.app.ActivityManager;
import android.app.ActivityThread;
import android.app.AppOpsManager;
import android.app.INotificationManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.admin.DevicePolicyManager;
import android.app.admin.DevicePolicyManagerInternal;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.ServiceConnection;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageManager;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManagerInternal;
import android.content.pm.RegisteredServicesCache;
import android.content.pm.RegisteredServicesCacheListener;
import android.content.pm.ResolveInfo;
import android.content.pm.Signature;
import android.content.pm.UserInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteStatement;
import android.os.Binder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.RemoteCallback;
import android.os.RemoteException;
import android.os.StrictMode;
import android.os.SystemClock;
import android.os.UserHandle;
import android.os.UserManager;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import android.util.SeempLog;
import android.util.Slog;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.content.PackageMonitor;
import com.android.internal.notification.SystemNotificationChannels;
import com.android.internal.util.ArrayUtils;
import com.android.internal.util.DumpUtils;
import com.android.internal.util.IndentingPrintWriter;
import com.android.internal.util.Preconditions;
import com.android.server.LocalServices;
import com.android.server.ServiceThread;
import com.android.server.SystemService;
import com.android.server.net.watchlist.WatchlistLoggingHandler;
import com.android.server.pm.DumpState;
import com.android.server.pm.PackageManagerService;
import com.android.server.pm.Settings;
import com.android.server.slice.SliceClientPermissions;
import com.google.android.collect.Lists;
import java.io.File;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicReference;

public class AccountManagerService extends IAccountManager.Stub implements RegisteredServicesCacheListener<AuthenticatorDescription> {
    private static final Intent ACCOUNTS_CHANGED_INTENT = new Intent("android.accounts.LOGIN_ACCOUNTS_CHANGED");
    private static final Account[] EMPTY_ACCOUNT_ARRAY = new Account[0];
    private static final int MESSAGE_COPY_SHARED_ACCOUNT = 4;
    private static final int MESSAGE_TIMED_OUT = 3;
    private static final String PRE_N_DATABASE_NAME = "accounts.db";
    private static final int SIGNATURE_CHECK_MATCH = 1;
    private static final int SIGNATURE_CHECK_MISMATCH = 0;
    private static final int SIGNATURE_CHECK_UID_MATCH = 2;
    private static final String TAG = "AccountManagerService";
    private static AtomicReference<AccountManagerService> sThis = new AtomicReference<>();
    /* access modifiers changed from: private */
    public final AppOpsManager mAppOpsManager;
    /* access modifiers changed from: private */
    public CopyOnWriteArrayList<AccountManagerInternal.OnAppPermissionChangeListener> mAppPermissionChangeListeners = new CopyOnWriteArrayList<>();
    /* access modifiers changed from: private */
    public final IAccountAuthenticatorCache mAuthenticatorCache;
    final Context mContext;
    /* access modifiers changed from: private */
    public final SimpleDateFormat mDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    final MessageHandler mHandler;
    private final Injector mInjector;
    private final SparseBooleanArray mLocalUnlockedUsers = new SparseBooleanArray();
    /* access modifiers changed from: private */
    public final PackageManager mPackageManager;
    /* access modifiers changed from: private */
    public final LinkedHashMap<String, Session> mSessions = new LinkedHashMap<>();
    private UserManager mUserManager;
    /* access modifiers changed from: private */
    public final SparseArray<UserAccounts> mUsers = new SparseArray<>();

    public static class Lifecycle extends SystemService {
        private AccountManagerService mService;

        public Lifecycle(Context context) {
            super(context);
        }

        /* JADX WARNING: type inference failed for: r0v1, types: [com.android.server.accounts.AccountManagerService, android.os.IBinder] */
        public void onStart() {
            this.mService = new AccountManagerService(new Injector(getContext()));
            publishBinderService("account", this.mService);
        }

        public void onUnlockUser(int userHandle) {
            this.mService.onUnlockUser(userHandle);
        }

        public void onStopUser(int userHandle) {
            Slog.i(AccountManagerService.TAG, "onStopUser " + userHandle);
            this.mService.purgeUserData(userHandle);
        }
    }

    static {
        ACCOUNTS_CHANGED_INTENT.setFlags(83886080);
    }

    static class UserAccounts {
        final HashMap<String, Account[]> accountCache = new LinkedHashMap();
        /* access modifiers changed from: private */
        public final TokenCache accountTokenCaches = new TokenCache();
        final AccountsDb accountsDb;
        /* access modifiers changed from: private */
        public final Map<Account, Map<String, String>> authTokenCache = new HashMap();
        final Object cacheLock = new Object();
        /* access modifiers changed from: private */
        public final HashMap<Pair<Pair<Account, String>, Integer>, NotificationId> credentialsPermissionNotificationIds = new HashMap<>();
        final Object dbLock = new Object();
        /* access modifiers changed from: private */
        public final Map<String, Map<String, Integer>> mReceiversForType = new HashMap();
        /* access modifiers changed from: private */
        public final HashMap<Account, AtomicReference<String>> previousNameCache = new HashMap<>();
        /* access modifiers changed from: private */
        public final HashMap<Account, NotificationId> signinRequiredNotificationIds = new HashMap<>();
        /* access modifiers changed from: private */
        public final Map<Account, Map<String, String>> userDataCache = new HashMap();
        /* access modifiers changed from: private */
        public final int userId;
        /* access modifiers changed from: private */
        public final Map<Account, Map<String, Integer>> visibilityCache = new HashMap();

        /* Debug info: failed to restart local var, previous not found, register: 3 */
        UserAccounts(Context context, int userId2, File preNDbFile, File deDbFile) {
            this.userId = userId2;
            synchronized (this.dbLock) {
                synchronized (this.cacheLock) {
                    this.accountsDb = AccountsDb.create(context, userId2, preNDbFile, deDbFile);
                }
            }
        }
    }

    public static AccountManagerService getSingleton() {
        return sThis.get();
    }

    /* JADX WARNING: type inference failed for: r4v2, types: [android.app.AppOpsManager$OnOpChangedListener, com.android.server.accounts.AccountManagerService$4] */
    public AccountManagerService(Injector injector) {
        this.mInjector = injector;
        this.mContext = injector.getContext();
        this.mPackageManager = this.mContext.getPackageManager();
        this.mAppOpsManager = (AppOpsManager) this.mContext.getSystemService(AppOpsManager.class);
        this.mHandler = new MessageHandler(injector.getMessageHandlerLooper());
        this.mAuthenticatorCache = this.mInjector.getAccountAuthenticatorCache();
        this.mAuthenticatorCache.setListener(this, (Handler) null);
        sThis.set(this);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.PACKAGE_REMOVED");
        intentFilter.addDataScheme(Settings.ATTR_PACKAGE);
        this.mContext.registerReceiver(new BroadcastReceiver() {
            public void onReceive(Context context1, Intent intent) {
                if (!intent.getBooleanExtra("android.intent.extra.REPLACING", false)) {
                    final String removedPackageName = intent.getData().getSchemeSpecificPart();
                    AccountManagerService.this.mHandler.post(new Runnable() {
                        public void run() {
                            AccountManagerService.this.purgeOldGrantsAll();
                            AccountManagerService.this.removeVisibilityValuesForPackage(removedPackageName);
                        }
                    });
                }
            }
        }, intentFilter);
        injector.addLocalService(new AccountManagerInternalImpl());
        IntentFilter userFilter = new IntentFilter();
        userFilter.addAction("android.intent.action.USER_REMOVED");
        this.mContext.registerReceiverAsUser(new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                int userId;
                if ("android.intent.action.USER_REMOVED".equals(intent.getAction()) && (userId = intent.getIntExtra("android.intent.extra.user_handle", -1)) >= 1) {
                    Slog.i(AccountManagerService.TAG, "User " + userId + " removed");
                    AccountManagerService.this.purgeUserData(userId);
                }
            }
        }, UserHandle.ALL, userFilter, (String) null, (Handler) null);
        new PackageMonitor() {
            public void onPackageAdded(String packageName, int uid) {
                AccountManagerService.this.cancelAccountAccessRequestNotificationIfNeeded(uid, true);
            }

            public void onPackageUpdateFinished(String packageName, int uid) {
                AccountManagerService.this.cancelAccountAccessRequestNotificationIfNeeded(uid, true);
            }
        }.register(this.mContext, this.mHandler.getLooper(), UserHandle.ALL, true);
        this.mAppOpsManager.startWatchingMode(62, (String) null, new AppOpsManager.OnOpChangedInternalListener() {
            /* Debug info: failed to restart local var, previous not found, register: 7 */
            public void onOpChanged(int op, String packageName) {
                long identity;
                try {
                    int uid = AccountManagerService.this.mPackageManager.getPackageUidAsUser(packageName, ActivityManager.getCurrentUser());
                    if (AccountManagerService.this.mAppOpsManager.checkOpNoThrow(62, uid, packageName) == 0) {
                        identity = Binder.clearCallingIdentity();
                        AccountManagerService.this.cancelAccountAccessRequestNotificationIfNeeded(packageName, uid, true);
                        Binder.restoreCallingIdentity(identity);
                    }
                } catch (PackageManager.NameNotFoundException e) {
                } catch (Throwable th) {
                    Binder.restoreCallingIdentity(identity);
                    throw th;
                }
            }
        });
        this.mPackageManager.addOnPermissionsChangeListener(new PackageManager.OnPermissionsChangedListener() {
            public final void onPermissionsChanged(int i) {
                AccountManagerService.this.lambda$new$0$AccountManagerService(i);
            }
        });
    }

    public /* synthetic */ void lambda$new$0$AccountManagerService(int uid) {
        Throwable th;
        String[] packageNames = this.mPackageManager.getPackagesForUid(uid);
        if (packageNames != null) {
            int userId = UserHandle.getUserId(uid);
            long identity = Binder.clearCallingIdentity();
            try {
                int length = packageNames.length;
                Account[] accounts = null;
                int i = 0;
                while (i < length) {
                    try {
                        String packageName = packageNames[i];
                        if (this.mPackageManager.checkPermission("android.permission.GET_ACCOUNTS", packageName) == 0) {
                            if (accounts == null) {
                                accounts = getAccountsAsUser((String) null, userId, PackageManagerService.PLATFORM_PACKAGE_NAME);
                                if (ArrayUtils.isEmpty(accounts)) {
                                    Binder.restoreCallingIdentity(identity);
                                    return;
                                }
                            }
                            for (Account account : accounts) {
                                cancelAccountAccessRequestNotificationIfNeeded(account, uid, packageName, true);
                            }
                        }
                        i++;
                    } catch (Throwable th2) {
                        th = th2;
                        Binder.restoreCallingIdentity(identity);
                        throw th;
                    }
                }
                Binder.restoreCallingIdentity(identity);
                Account[] accountArr = accounts;
            } catch (Throwable th3) {
                th = th3;
                Binder.restoreCallingIdentity(identity);
                throw th;
            }
        }
    }

    /* access modifiers changed from: package-private */
    public boolean getBindInstantServiceAllowed(int userId) {
        return this.mAuthenticatorCache.getBindInstantServiceAllowed(userId);
    }

    /* access modifiers changed from: package-private */
    public void setBindInstantServiceAllowed(int userId, boolean allowed) {
        this.mAuthenticatorCache.setBindInstantServiceAllowed(userId, allowed);
    }

    /* access modifiers changed from: private */
    public void cancelAccountAccessRequestNotificationIfNeeded(int uid, boolean checkAccess) {
        for (Account account : getAccountsAsUser((String) null, UserHandle.getUserId(uid), PackageManagerService.PLATFORM_PACKAGE_NAME)) {
            cancelAccountAccessRequestNotificationIfNeeded(account, uid, checkAccess);
        }
    }

    /* access modifiers changed from: private */
    public void cancelAccountAccessRequestNotificationIfNeeded(String packageName, int uid, boolean checkAccess) {
        for (Account account : getAccountsAsUser((String) null, UserHandle.getUserId(uid), PackageManagerService.PLATFORM_PACKAGE_NAME)) {
            cancelAccountAccessRequestNotificationIfNeeded(account, uid, packageName, checkAccess);
        }
    }

    private void cancelAccountAccessRequestNotificationIfNeeded(Account account, int uid, boolean checkAccess) {
        String[] packageNames = this.mPackageManager.getPackagesForUid(uid);
        if (packageNames != null) {
            for (String packageName : packageNames) {
                cancelAccountAccessRequestNotificationIfNeeded(account, uid, packageName, checkAccess);
            }
        }
    }

    private void cancelAccountAccessRequestNotificationIfNeeded(Account account, int uid, String packageName, boolean checkAccess) {
        if (!checkAccess || hasAccountAccess(account, packageName, UserHandle.getUserHandleForUid(uid))) {
            cancelNotification(getCredentialPermissionNotificationId(account, "com.android.AccountManager.ACCOUNT_ACCESS_TOKEN_TYPE", uid), packageName, UserHandle.getUserHandleForUid(uid));
        }
    }

    public boolean addAccountExplicitlyWithVisibility(Account account, String password, Bundle extras, Map packageToVisibility) {
        Account account2 = account;
        Bundle.setDefusable(extras, true);
        int callingUid = Binder.getCallingUid();
        int userId = UserHandle.getCallingUserId();
        if (Log.isLoggable(TAG, 2)) {
            Log.v(TAG, "addAccountExplicitly: " + account2 + ", caller's uid " + callingUid + ", pid " + Binder.getCallingPid());
        }
        Preconditions.checkNotNull(account2, "account cannot be null");
        if (isAccountManagedByCaller(account2.type, callingUid, userId)) {
            long identityToken = clearCallingIdentity();
            try {
                return addAccountInternal(getUserAccounts(userId), account, password, extras, callingUid, packageToVisibility);
            } finally {
                restoreCallingIdentity(identityToken);
            }
        } else {
            throw new SecurityException(String.format("uid %s cannot explicitly add accounts of type: %s", new Object[]{Integer.valueOf(callingUid), account2.type}));
        }
    }

    public Map<Account, Integer> getAccountsAndVisibilityForPackage(String packageName, String accountType) {
        int callingUid = Binder.getCallingUid();
        int userId = UserHandle.getCallingUserId();
        boolean isSystemUid = UserHandle.isSameApp(callingUid, 1000);
        List<String> managedTypes = getTypesForCaller(callingUid, userId, isSystemUid);
        if ((accountType == null || managedTypes.contains(accountType)) && (accountType != null || isSystemUid)) {
            if (accountType != null) {
                managedTypes = new ArrayList<>();
                managedTypes.add(accountType);
            }
            long identityToken = clearCallingIdentity();
            try {
                return getAccountsAndVisibilityForPackage(packageName, managedTypes, Integer.valueOf(callingUid), getUserAccounts(userId));
            } finally {
                restoreCallingIdentity(identityToken);
            }
        } else {
            throw new SecurityException("getAccountsAndVisibilityForPackage() called from unauthorized uid " + callingUid + " with packageName=" + packageName);
        }
    }

    /* Debug info: failed to restart local var, previous not found, register: 10 */
    private Map<Account, Integer> getAccountsAndVisibilityForPackage(String packageName, List<String> accountTypes, Integer callingUid, UserAccounts accounts) {
        if (!packageExistsForUser(packageName, accounts.userId)) {
            Log.d(TAG, "Package not found " + packageName);
            return new LinkedHashMap();
        }
        Map<Account, Integer> result = new LinkedHashMap<>();
        for (String accountType : accountTypes) {
            synchronized (accounts.dbLock) {
                synchronized (accounts.cacheLock) {
                    Account[] accountsOfType = accounts.accountCache.get(accountType);
                    if (accountsOfType != null) {
                        for (Account account : accountsOfType) {
                            result.put(account, resolveAccountVisibility(account, packageName, accounts));
                        }
                    }
                }
            }
        }
        return filterSharedAccounts(accounts, result, callingUid.intValue(), packageName);
    }

    /* Debug info: failed to restart local var, previous not found, register: 8 */
    public Map<String, Integer> getPackagesAndVisibilityForAccount(Account account) {
        Map<String, Integer> packagesAndVisibilityForAccountLocked;
        Preconditions.checkNotNull(account, "account cannot be null");
        int callingUid = Binder.getCallingUid();
        int userId = UserHandle.getCallingUserId();
        if (isAccountManagedByCaller(account.type, callingUid, userId) || isSystemUid(callingUid)) {
            long identityToken = clearCallingIdentity();
            try {
                UserAccounts accounts = getUserAccounts(userId);
                synchronized (accounts.dbLock) {
                    synchronized (accounts.cacheLock) {
                        packagesAndVisibilityForAccountLocked = getPackagesAndVisibilityForAccountLocked(account, accounts);
                    }
                }
                return packagesAndVisibilityForAccountLocked;
            } finally {
                restoreCallingIdentity(identityToken);
            }
        } else {
            throw new SecurityException(String.format("uid %s cannot get secrets for account %s", new Object[]{Integer.valueOf(callingUid), account}));
        }
    }

    private Map<String, Integer> getPackagesAndVisibilityForAccountLocked(Account account, UserAccounts accounts) {
        Map<String, Integer> accountVisibility = (Map) accounts.visibilityCache.get(account);
        if (accountVisibility != null) {
            return accountVisibility;
        }
        Log.d(TAG, "Visibility was not initialized");
        Map<String, Integer> accountVisibility2 = new HashMap<>();
        accounts.visibilityCache.put(account, accountVisibility2);
        return accountVisibility2;
    }

    public int getAccountVisibility(Account account, String packageName) {
        Preconditions.checkNotNull(account, "account cannot be null");
        Preconditions.checkNotNull(packageName, "packageName cannot be null");
        int callingUid = Binder.getCallingUid();
        int userId = UserHandle.getCallingUserId();
        if (isAccountManagedByCaller(account.type, callingUid, userId) || isSystemUid(callingUid)) {
            long identityToken = clearCallingIdentity();
            try {
                UserAccounts accounts = getUserAccounts(userId);
                if ("android:accounts:key_legacy_visible".equals(packageName)) {
                    int visibility = getAccountVisibilityFromCache(account, packageName, accounts);
                    if (visibility != 0) {
                        return visibility;
                    }
                    restoreCallingIdentity(identityToken);
                    return 2;
                } else if ("android:accounts:key_legacy_not_visible".equals(packageName)) {
                    int visibility2 = getAccountVisibilityFromCache(account, packageName, accounts);
                    if (visibility2 != 0) {
                        restoreCallingIdentity(identityToken);
                        return visibility2;
                    }
                    restoreCallingIdentity(identityToken);
                    return 4;
                } else {
                    int intValue = resolveAccountVisibility(account, packageName, accounts).intValue();
                    restoreCallingIdentity(identityToken);
                    return intValue;
                }
            } finally {
                restoreCallingIdentity(identityToken);
            }
        } else {
            throw new SecurityException(String.format("uid %s cannot get secrets for accounts of type: %s", new Object[]{Integer.valueOf(callingUid), account.type}));
        }
    }

    private int getAccountVisibilityFromCache(Account account, String packageName, UserAccounts accounts) {
        int intValue;
        synchronized (accounts.cacheLock) {
            Integer visibility = getPackagesAndVisibilityForAccountLocked(account, accounts).get(packageName);
            intValue = visibility != null ? visibility.intValue() : 0;
        }
        return intValue;
    }

    /* Debug info: failed to restart local var, previous not found, register: 7 */
    /* access modifiers changed from: private */
    public Integer resolveAccountVisibility(Account account, String packageName, UserAccounts accounts) {
        long identityToken;
        int visibility;
        Preconditions.checkNotNull(packageName, "packageName cannot be null");
        try {
            identityToken = clearCallingIdentity();
            int uid = this.mPackageManager.getPackageUidAsUser(packageName, accounts.userId);
            restoreCallingIdentity(identityToken);
            if (UserHandle.isSameApp(uid, 1000)) {
                return 1;
            }
            int signatureCheckResult = checkPackageSignature(account.type, uid, accounts.userId);
            if (signatureCheckResult == 2) {
                return 1;
            }
            int visibility2 = getAccountVisibilityFromCache(account, packageName, accounts);
            if (visibility2 != 0) {
                return Integer.valueOf(visibility2);
            }
            boolean isPrivileged = isPermittedForPackage(packageName, accounts.userId, "android.permission.GET_ACCOUNTS_PRIVILEGED");
            if (isProfileOwner(uid)) {
                return 1;
            }
            boolean preO = isPreOApplication(packageName);
            if (signatureCheckResult != 0 || ((preO && checkGetAccountsPermission(packageName, accounts.userId)) || ((checkReadContactsPermission(packageName, accounts.userId) && accountTypeManagesContacts(account.type, accounts.userId)) || isPrivileged))) {
                visibility = getAccountVisibilityFromCache(account, "android:accounts:key_legacy_visible", accounts);
                if (visibility == 0) {
                    visibility = 2;
                }
            } else {
                visibility = getAccountVisibilityFromCache(account, "android:accounts:key_legacy_not_visible", accounts);
                if (visibility == 0) {
                    visibility = 4;
                }
            }
            return Integer.valueOf(visibility);
        } catch (PackageManager.NameNotFoundException e) {
            Log.d(TAG, "Package not found " + e.getMessage());
            return 3;
        } catch (Throwable th) {
            restoreCallingIdentity(identityToken);
            throw th;
        }
    }

    /* Debug info: failed to restart local var, previous not found, register: 7 */
    /* JADX WARNING: No exception handlers in catch block: Catch:{  } */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private boolean isPreOApplication(java.lang.String r8) {
        /*
            r7 = this;
            r0 = 1
            long r1 = clearCallingIdentity()     // Catch:{ NameNotFoundException -> 0x0021 }
            android.content.pm.PackageManager r3 = r7.mPackageManager     // Catch:{ all -> 0x001c }
            r4 = 0
            android.content.pm.ApplicationInfo r3 = r3.getApplicationInfo(r8, r4)     // Catch:{ all -> 0x001c }
            restoreCallingIdentity(r1)     // Catch:{ NameNotFoundException -> 0x0021 }
            if (r3 == 0) goto L_0x001b
            int r5 = r3.targetSdkVersion     // Catch:{ NameNotFoundException -> 0x0021 }
            r6 = 26
            if (r5 >= r6) goto L_0x0019
            goto L_0x001a
        L_0x0019:
            r0 = r4
        L_0x001a:
            return r0
        L_0x001b:
            return r0
        L_0x001c:
            r3 = move-exception
            restoreCallingIdentity(r1)     // Catch:{ NameNotFoundException -> 0x0021 }
            throw r3     // Catch:{ NameNotFoundException -> 0x0021 }
        L_0x0021:
            r1 = move-exception
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r3 = "Package not found "
            r2.append(r3)
            java.lang.String r3 = r1.getMessage()
            r2.append(r3)
            java.lang.String r2 = r2.toString()
            java.lang.String r3 = "AccountManagerService"
            android.util.Log.d(r3, r2)
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.accounts.AccountManagerService.isPreOApplication(java.lang.String):boolean");
    }

    public boolean setAccountVisibility(Account account, String packageName, int newVisibility) {
        Preconditions.checkNotNull(account, "account cannot be null");
        Preconditions.checkNotNull(packageName, "packageName cannot be null");
        int callingUid = Binder.getCallingUid();
        int userId = UserHandle.getCallingUserId();
        if (isAccountManagedByCaller(account.type, callingUid, userId) || isSystemUid(callingUid)) {
            long identityToken = clearCallingIdentity();
            try {
                return setAccountVisibility(account, packageName, newVisibility, true, getUserAccounts(userId));
            } finally {
                restoreCallingIdentity(identityToken);
            }
        } else {
            throw new SecurityException(String.format("uid %s cannot get secrets for accounts of type: %s", new Object[]{Integer.valueOf(callingUid), account.type}));
        }
    }

    private boolean isVisible(int visibility) {
        return visibility == 1 || visibility == 2;
    }

    /* Debug info: failed to restart local var, previous not found, register: 10 */
    private boolean setAccountVisibility(Account account, String packageName, int newVisibility, boolean notify, UserAccounts accounts) {
        List<String> accountRemovedReceivers;
        Map<String, Integer> packagesToVisibility;
        synchronized (accounts.dbLock) {
            synchronized (accounts.cacheLock) {
                if (notify) {
                    if (isSpecialPackageKey(packageName)) {
                        packagesToVisibility = getRequestingPackages(account, accounts);
                        accountRemovedReceivers = getAccountRemovedReceivers(account, accounts);
                    } else if (!packageExistsForUser(packageName, accounts.userId)) {
                        return false;
                    } else {
                        packagesToVisibility = new HashMap<>();
                        packagesToVisibility.put(packageName, resolveAccountVisibility(account, packageName, accounts));
                        accountRemovedReceivers = new ArrayList<>();
                        if (shouldNotifyPackageOnAccountRemoval(account, packageName, accounts)) {
                            accountRemovedReceivers.add(packageName);
                        }
                    }
                } else if (!isSpecialPackageKey(packageName) && !packageExistsForUser(packageName, accounts.userId)) {
                    return false;
                } else {
                    packagesToVisibility = Collections.emptyMap();
                    accountRemovedReceivers = Collections.emptyList();
                }
                if (!updateAccountVisibilityLocked(account, packageName, newVisibility, accounts)) {
                    return false;
                }
                if (notify) {
                    for (Map.Entry<String, Integer> packageToVisibility : packagesToVisibility.entrySet()) {
                        if (isVisible(packageToVisibility.getValue().intValue()) != isVisible(resolveAccountVisibility(account, packageName, accounts).intValue())) {
                            notifyPackage(packageToVisibility.getKey(), accounts);
                        }
                    }
                    for (String packageNameToNotify : accountRemovedReceivers) {
                        sendAccountRemovedBroadcast(account, packageNameToNotify, accounts.userId);
                    }
                    sendAccountsChangedBroadcast(accounts.userId);
                }
                return true;
            }
        }
    }

    private boolean updateAccountVisibilityLocked(Account account, String packageName, int newVisibility, UserAccounts accounts) {
        long accountId = accounts.accountsDb.findDeAccountId(account);
        if (accountId < 0) {
            return false;
        }
        StrictMode.ThreadPolicy oldPolicy = StrictMode.allowThreadDiskWrites();
        try {
            if (!accounts.accountsDb.setAccountVisibility(accountId, packageName, newVisibility)) {
                return false;
            }
            StrictMode.setThreadPolicy(oldPolicy);
            getPackagesAndVisibilityForAccountLocked(account, accounts).put(packageName, Integer.valueOf(newVisibility));
            return true;
        } finally {
            StrictMode.setThreadPolicy(oldPolicy);
        }
    }

    public void registerAccountListener(String[] accountTypes, String opPackageName) {
        this.mAppOpsManager.checkPackage(Binder.getCallingUid(), opPackageName);
        int userId = UserHandle.getCallingUserId();
        long identityToken = clearCallingIdentity();
        try {
            registerAccountListener(accountTypes, opPackageName, getUserAccounts(userId));
        } finally {
            restoreCallingIdentity(identityToken);
        }
    }

    private void registerAccountListener(String[] accountTypes, String opPackageName, UserAccounts accounts) {
        synchronized (accounts.mReceiversForType) {
            if (accountTypes == null) {
                accountTypes = new String[]{null};
            }
            for (String type : accountTypes) {
                Map<String, Integer> receivers = (Map) accounts.mReceiversForType.get(type);
                if (receivers == null) {
                    receivers = new HashMap<>();
                    accounts.mReceiversForType.put(type, receivers);
                }
                Integer cnt = receivers.get(opPackageName);
                int i = 1;
                if (cnt != null) {
                    i = 1 + cnt.intValue();
                }
                receivers.put(opPackageName, Integer.valueOf(i));
            }
        }
    }

    public void unregisterAccountListener(String[] accountTypes, String opPackageName) {
        this.mAppOpsManager.checkPackage(Binder.getCallingUid(), opPackageName);
        int userId = UserHandle.getCallingUserId();
        long identityToken = clearCallingIdentity();
        try {
            unregisterAccountListener(accountTypes, opPackageName, getUserAccounts(userId));
        } finally {
            restoreCallingIdentity(identityToken);
        }
    }

    /* Debug info: failed to restart local var, previous not found, register: 8 */
    private void unregisterAccountListener(String[] accountTypes, String opPackageName, UserAccounts accounts) {
        synchronized (accounts.mReceiversForType) {
            if (accountTypes == null) {
                accountTypes = new String[]{null};
            }
            for (String type : accountTypes) {
                Map<String, Integer> receivers = (Map) accounts.mReceiversForType.get(type);
                if (receivers == null || receivers.get(opPackageName) == null) {
                    throw new IllegalArgumentException("attempt to unregister wrong receiver");
                }
                Integer cnt = receivers.get(opPackageName);
                if (cnt.intValue() == 1) {
                    receivers.remove(opPackageName);
                } else {
                    receivers.put(opPackageName, Integer.valueOf(cnt.intValue() - 1));
                }
            }
        }
    }

    private void sendNotificationAccountUpdated(Account account, UserAccounts accounts) {
        for (Map.Entry<String, Integer> packageToVisibility : getRequestingPackages(account, accounts).entrySet()) {
            if (!(packageToVisibility.getValue().intValue() == 3 || packageToVisibility.getValue().intValue() == 4)) {
                notifyPackage(packageToVisibility.getKey(), accounts);
            }
        }
    }

    private void notifyPackage(String packageName, UserAccounts accounts) {
        Intent intent = new Intent("android.accounts.action.VISIBLE_ACCOUNTS_CHANGED");
        intent.setPackage(packageName);
        intent.setFlags(1073741824);
        this.mContext.sendBroadcastAsUser(intent, new UserHandle(accounts.userId));
    }

    private Map<String, Integer> getRequestingPackages(Account account, UserAccounts accounts) {
        Set<String> packages = new HashSet<>();
        synchronized (accounts.mReceiversForType) {
            for (String type : new String[]{account.type, null}) {
                Map<String, Integer> receivers = (Map) accounts.mReceiversForType.get(type);
                if (receivers != null) {
                    packages.addAll(receivers.keySet());
                }
            }
        }
        Map<String, Integer> result = new HashMap<>();
        for (String packageName : packages) {
            result.put(packageName, resolveAccountVisibility(account, packageName, accounts));
        }
        return result;
    }

    private List<String> getAccountRemovedReceivers(Account account, UserAccounts accounts) {
        Intent intent = new Intent("android.accounts.action.ACCOUNT_REMOVED");
        intent.setFlags(DumpState.DUMP_SERVICE_PERMISSIONS);
        List<ResolveInfo> receivers = this.mPackageManager.queryBroadcastReceiversAsUser(intent, 0, accounts.userId);
        List<String> result = new ArrayList<>();
        if (receivers == null) {
            return result;
        }
        for (ResolveInfo resolveInfo : receivers) {
            String packageName = resolveInfo.activityInfo.applicationInfo.packageName;
            int visibility = resolveAccountVisibility(account, packageName, accounts).intValue();
            if (visibility == 1 || visibility == 2) {
                result.add(packageName);
            }
        }
        return result;
    }

    private boolean shouldNotifyPackageOnAccountRemoval(Account account, String packageName, UserAccounts accounts) {
        int visibility = resolveAccountVisibility(account, packageName, accounts).intValue();
        if (visibility != 1 && visibility != 2) {
            return false;
        }
        Intent intent = new Intent("android.accounts.action.ACCOUNT_REMOVED");
        intent.setFlags(DumpState.DUMP_SERVICE_PERMISSIONS);
        intent.setPackage(packageName);
        List<ResolveInfo> receivers = this.mPackageManager.queryBroadcastReceiversAsUser(intent, 0, accounts.userId);
        if (receivers == null || receivers.size() <= 0) {
            return false;
        }
        return true;
    }

    /* Debug info: failed to restart local var, previous not found, register: 3 */
    private boolean packageExistsForUser(String packageName, int userId) {
        long identityToken;
        try {
            identityToken = clearCallingIdentity();
            this.mPackageManager.getPackageUidAsUser(packageName, userId);
            restoreCallingIdentity(identityToken);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        } catch (Throwable th) {
            restoreCallingIdentity(identityToken);
            throw th;
        }
    }

    private boolean isSpecialPackageKey(String packageName) {
        return "android:accounts:key_legacy_visible".equals(packageName) || "android:accounts:key_legacy_not_visible".equals(packageName);
    }

    private void sendAccountsChangedBroadcast(int userId) {
        Log.i(TAG, "the accounts changed, sending broadcast of " + ACCOUNTS_CHANGED_INTENT.getAction());
        this.mContext.sendBroadcastAsUser(ACCOUNTS_CHANGED_INTENT, new UserHandle(userId));
    }

    private void sendAccountRemovedBroadcast(Account account, String packageName, int userId) {
        Intent intent = new Intent("android.accounts.action.ACCOUNT_REMOVED");
        intent.setFlags(DumpState.DUMP_SERVICE_PERMISSIONS);
        intent.setPackage(packageName);
        intent.putExtra("authAccount", account.name);
        intent.putExtra("accountType", account.type);
        this.mContext.sendBroadcastAsUser(intent, new UserHandle(userId));
    }

    public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
        try {
            return AccountManagerService.super.onTransact(code, data, reply, flags);
        } catch (RuntimeException e) {
            if (!(e instanceof SecurityException)) {
                Slog.wtf(TAG, "Account Manager Crash", e);
            }
            throw e;
        }
    }

    private UserManager getUserManager() {
        if (this.mUserManager == null) {
            this.mUserManager = UserManager.get(this.mContext);
        }
        return this.mUserManager;
    }

    public void validateAccounts(int userId) {
        validateAccountsInternal(getUserAccounts(userId), true);
    }

    /* Debug info: failed to restart local var, previous not found, register: 27 */
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
        	at jadx.core.dex.visitors.regions.RegionMakerVisitor.visit(RegionMakerVisitor.java:49)
        */
    private void validateAccountsInternal(com.android.server.accounts.AccountManagerService.UserAccounts r28, boolean r29) {
        /*
            r27 = this;
            r7 = r27
            r8 = r28
            java.lang.String r0 = "AccountManagerService"
            r1 = 3
            boolean r0 = android.util.Log.isLoggable(r0, r1)
            if (r0 == 0) goto L_0x0048
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r1 = "validateAccountsInternal "
            r0.append(r1)
            int r1 = r28.userId
            r0.append(r1)
            java.lang.String r1 = " isCeDatabaseAttached="
            r0.append(r1)
            com.android.server.accounts.AccountsDb r1 = r8.accountsDb
            boolean r1 = r1.isCeDatabaseAttached()
            r0.append(r1)
            java.lang.String r1 = " userLocked="
            r0.append(r1)
            android.util.SparseBooleanArray r1 = r7.mLocalUnlockedUsers
            int r2 = r28.userId
            boolean r1 = r1.get(r2)
            r0.append(r1)
            java.lang.String r0 = r0.toString()
            java.lang.String r1 = "AccountManagerService"
            android.util.Log.d(r1, r0)
        L_0x0048:
            if (r29 == 0) goto L_0x0053
            com.android.server.accounts.IAccountAuthenticatorCache r0 = r7.mAuthenticatorCache
            int r1 = r28.userId
            r0.invalidateCache(r1)
        L_0x0053:
            com.android.server.accounts.IAccountAuthenticatorCache r0 = r7.mAuthenticatorCache
            int r1 = r28.userId
            java.util.HashMap r9 = getAuthenticatorTypeAndUIDForUser((com.android.server.accounts.IAccountAuthenticatorCache) r0, (int) r1)
            int r0 = r28.userId
            boolean r10 = r7.isLocalUnlockedUser(r0)
            java.lang.Object r11 = r8.dbLock
            monitor-enter(r11)
            java.lang.Object r12 = r8.cacheLock     // Catch:{ all -> 0x02f0 }
            monitor-enter(r12)     // Catch:{ all -> 0x02f0 }
            r1 = 0
            com.android.server.accounts.AccountsDb r0 = r8.accountsDb     // Catch:{ all -> 0x02e7 }
            r13 = r0
            java.util.Map r0 = r13.findMetaAuthUid()     // Catch:{ all -> 0x02e7 }
            r14 = r0
            java.util.HashSet r0 = com.google.android.collect.Sets.newHashSet()     // Catch:{ all -> 0x02e7 }
            r15 = r0
            r0 = 0
            java.util.Set r2 = r14.entrySet()     // Catch:{ all -> 0x02e7 }
            java.util.Iterator r2 = r2.iterator()     // Catch:{ all -> 0x02e7 }
            r16 = r0
        L_0x0084:
            boolean r0 = r2.hasNext()     // Catch:{ all -> 0x02e7 }
            if (r0 == 0) goto L_0x00d7
            java.lang.Object r0 = r2.next()     // Catch:{ all -> 0x00d0 }
            java.util.Map$Entry r0 = (java.util.Map.Entry) r0     // Catch:{ all -> 0x00d0 }
            java.lang.Object r3 = r0.getKey()     // Catch:{ all -> 0x00d0 }
            java.lang.String r3 = (java.lang.String) r3     // Catch:{ all -> 0x00d0 }
            java.lang.Object r4 = r0.getValue()     // Catch:{ all -> 0x00d0 }
            java.lang.Integer r4 = (java.lang.Integer) r4     // Catch:{ all -> 0x00d0 }
            int r4 = r4.intValue()     // Catch:{ all -> 0x00d0 }
            java.lang.Object r5 = r9.get(r3)     // Catch:{ all -> 0x00d0 }
            java.lang.Integer r5 = (java.lang.Integer) r5     // Catch:{ all -> 0x00d0 }
            if (r5 == 0) goto L_0x00b2
            int r6 = r5.intValue()     // Catch:{ all -> 0x00d0 }
            if (r4 != r6) goto L_0x00b2
            r9.remove(r3)     // Catch:{ all -> 0x00d0 }
            goto L_0x00cf
        L_0x00b2:
            if (r16 != 0) goto L_0x00bf
            int r6 = r28.userId     // Catch:{ all -> 0x00d0 }
            android.util.SparseBooleanArray r6 = r7.getUidsOfInstalledOrUpdatedPackagesAsUser(r6)     // Catch:{ all -> 0x00d0 }
            r16 = r6
            goto L_0x00c1
        L_0x00bf:
            r6 = r16
        L_0x00c1:
            boolean r16 = r6.get(r4)     // Catch:{ all -> 0x00d0 }
            if (r16 != 0) goto L_0x00cd
            r15.add(r3)     // Catch:{ all -> 0x00d0 }
            r13.deleteMetaByAuthTypeAndUid(r3, r4)     // Catch:{ all -> 0x00d0 }
        L_0x00cd:
            r16 = r6
        L_0x00cf:
            goto L_0x0084
        L_0x00d0:
            r0 = move-exception
            r26 = r9
            r20 = r10
            goto L_0x02ec
        L_0x00d7:
            java.util.Set r0 = r9.entrySet()     // Catch:{ all -> 0x02e7 }
            java.util.Iterator r0 = r0.iterator()     // Catch:{ all -> 0x02e7 }
        L_0x00df:
            boolean r2 = r0.hasNext()     // Catch:{ all -> 0x02e7 }
            if (r2 == 0) goto L_0x0100
            java.lang.Object r2 = r0.next()     // Catch:{ all -> 0x00d0 }
            java.util.Map$Entry r2 = (java.util.Map.Entry) r2     // Catch:{ all -> 0x00d0 }
            java.lang.Object r3 = r2.getKey()     // Catch:{ all -> 0x00d0 }
            java.lang.String r3 = (java.lang.String) r3     // Catch:{ all -> 0x00d0 }
            java.lang.Object r4 = r2.getValue()     // Catch:{ all -> 0x00d0 }
            java.lang.Integer r4 = (java.lang.Integer) r4     // Catch:{ all -> 0x00d0 }
            int r4 = r4.intValue()     // Catch:{ all -> 0x00d0 }
            r13.insertOrReplaceMetaAuthTypeAndUid(r3, r4)     // Catch:{ all -> 0x00d0 }
            goto L_0x00df
        L_0x0100:
            java.util.Map r0 = r13.findAllDeAccounts()     // Catch:{ all -> 0x02e7 }
            r17 = r0
            java.util.HashMap<java.lang.String, android.accounts.Account[]> r0 = r8.accountCache     // Catch:{ all -> 0x02d7 }
            r0.clear()     // Catch:{ all -> 0x02d7 }
            java.util.LinkedHashMap r0 = new java.util.LinkedHashMap     // Catch:{ all -> 0x02d7 }
            r0.<init>()     // Catch:{ all -> 0x02d7 }
            r6 = r0
            java.util.Set r0 = r17.entrySet()     // Catch:{ all -> 0x02d7 }
            java.util.Iterator r0 = r0.iterator()     // Catch:{ all -> 0x02d7 }
        L_0x0119:
            boolean r2 = r0.hasNext()     // Catch:{ all -> 0x02d7 }
            if (r2 == 0) goto L_0x0261
            java.lang.Object r2 = r0.next()     // Catch:{ all -> 0x02d7 }
            java.util.Map$Entry r2 = (java.util.Map.Entry) r2     // Catch:{ all -> 0x02d7 }
            r18 = r2
            java.lang.Object r2 = r18.getKey()     // Catch:{ all -> 0x02d7 }
            java.lang.Long r2 = (java.lang.Long) r2     // Catch:{ all -> 0x02d7 }
            long r2 = r2.longValue()     // Catch:{ all -> 0x02d7 }
            r4 = r2
            java.lang.Object r2 = r18.getValue()     // Catch:{ all -> 0x02d7 }
            android.accounts.Account r2 = (android.accounts.Account) r2     // Catch:{ all -> 0x02d7 }
            r3 = r2
            java.lang.String r2 = r3.type     // Catch:{ all -> 0x02d7 }
            boolean r2 = r15.contains(r2)     // Catch:{ all -> 0x02d7 }
            if (r2 == 0) goto L_0x0234
            java.lang.String r2 = "AccountManagerService"
            r19 = r0
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ all -> 0x02d7 }
            r0.<init>()     // Catch:{ all -> 0x02d7 }
            r20 = r6
            java.lang.String r6 = "deleting account "
            r0.append(r6)     // Catch:{ all -> 0x02d7 }
            java.lang.String r6 = r3.toSafeString()     // Catch:{ all -> 0x02d7 }
            r0.append(r6)     // Catch:{ all -> 0x02d7 }
            java.lang.String r6 = " because type "
            r0.append(r6)     // Catch:{ all -> 0x02d7 }
            java.lang.String r6 = r3.type     // Catch:{ all -> 0x02d7 }
            r0.append(r6)     // Catch:{ all -> 0x02d7 }
            java.lang.String r6 = "'s registered authenticator no longer exist."
            r0.append(r6)     // Catch:{ all -> 0x02d7 }
            java.lang.String r0 = r0.toString()     // Catch:{ all -> 0x02d7 }
            android.util.Slog.w(r2, r0)     // Catch:{ all -> 0x02d7 }
            java.util.Map r0 = r7.getRequestingPackages(r3, r8)     // Catch:{ all -> 0x02d7 }
            r21 = r0
            java.util.List r0 = r7.getAccountRemovedReceivers(r3, r8)     // Catch:{ all -> 0x02d7 }
            r22 = r0
            r13.beginTransaction()     // Catch:{ all -> 0x02d7 }
            r13.deleteDeAccount(r4)     // Catch:{ all -> 0x0226 }
            if (r10 == 0) goto L_0x0194
            r13.deleteCeAccount(r4)     // Catch:{ all -> 0x0188 }
            goto L_0x0194
        L_0x0188:
            r0 = move-exception
            r24 = r4
            r26 = r9
            r9 = r20
            r20 = r10
            r10 = r3
            goto L_0x0230
        L_0x0194:
            r13.setTransactionSuccessful()     // Catch:{ all -> 0x0226 }
            r13.endTransaction()     // Catch:{ all -> 0x02d7 }
            r23 = 1
            java.lang.String r2 = com.android.server.accounts.AccountsDb.DEBUG_ACTION_AUTHENTICATOR_REMOVE     // Catch:{ all -> 0x021d }
            java.lang.String r0 = "accounts"
            r1 = r27
            r6 = r3
            r3 = r0
            r24 = r4
            r26 = r9
            r9 = r20
            r20 = r10
            r10 = r6
            r6 = r28
            r1.logRecord(r2, r3, r4, r6)     // Catch:{ all -> 0x0218 }
            java.util.Map r0 = r28.userDataCache     // Catch:{ all -> 0x0218 }
            r0.remove(r10)     // Catch:{ all -> 0x0218 }
            java.util.Map r0 = r28.authTokenCache     // Catch:{ all -> 0x0218 }
            r0.remove(r10)     // Catch:{ all -> 0x0218 }
            com.android.server.accounts.TokenCache r0 = r28.accountTokenCaches     // Catch:{ all -> 0x0218 }
            r0.remove(r10)     // Catch:{ all -> 0x0218 }
            java.util.Map r0 = r28.visibilityCache     // Catch:{ all -> 0x0218 }
            r0.remove(r10)     // Catch:{ all -> 0x0218 }
            java.util.Set r0 = r21.entrySet()     // Catch:{ all -> 0x0218 }
            java.util.Iterator r0 = r0.iterator()     // Catch:{ all -> 0x0218 }
        L_0x01d7:
            boolean r1 = r0.hasNext()     // Catch:{ all -> 0x0218 }
            if (r1 == 0) goto L_0x01fd
            java.lang.Object r1 = r0.next()     // Catch:{ all -> 0x0218 }
            java.util.Map$Entry r1 = (java.util.Map.Entry) r1     // Catch:{ all -> 0x0218 }
            java.lang.Object r2 = r1.getValue()     // Catch:{ all -> 0x0218 }
            java.lang.Integer r2 = (java.lang.Integer) r2     // Catch:{ all -> 0x0218 }
            int r2 = r2.intValue()     // Catch:{ all -> 0x0218 }
            boolean r2 = r7.isVisible(r2)     // Catch:{ all -> 0x0218 }
            if (r2 == 0) goto L_0x01fc
            java.lang.Object r2 = r1.getKey()     // Catch:{ all -> 0x0218 }
            java.lang.String r2 = (java.lang.String) r2     // Catch:{ all -> 0x0218 }
            r7.notifyPackage(r2, r8)     // Catch:{ all -> 0x0218 }
        L_0x01fc:
            goto L_0x01d7
        L_0x01fd:
            java.util.Iterator r0 = r22.iterator()     // Catch:{ all -> 0x0218 }
        L_0x0201:
            boolean r1 = r0.hasNext()     // Catch:{ all -> 0x0218 }
            if (r1 == 0) goto L_0x0215
            java.lang.Object r1 = r0.next()     // Catch:{ all -> 0x0218 }
            java.lang.String r1 = (java.lang.String) r1     // Catch:{ all -> 0x0218 }
            int r2 = r28.userId     // Catch:{ all -> 0x0218 }
            r7.sendAccountRemovedBroadcast(r10, r1, r2)     // Catch:{ all -> 0x0218 }
            goto L_0x0201
        L_0x0215:
            r1 = r23
            goto L_0x0258
        L_0x0218:
            r0 = move-exception
            r1 = r23
            goto L_0x02dc
        L_0x021d:
            r0 = move-exception
            r26 = r9
            r20 = r10
            r1 = r23
            goto L_0x02dc
        L_0x0226:
            r0 = move-exception
            r24 = r4
            r26 = r9
            r9 = r20
            r20 = r10
            r10 = r3
        L_0x0230:
            r13.endTransaction()     // Catch:{ all -> 0x02d5 }
            throw r0     // Catch:{ all -> 0x02d5 }
        L_0x0234:
            r19 = r0
            r24 = r4
            r26 = r9
            r20 = r10
            r10 = r3
            r9 = r6
            java.lang.String r0 = r10.type     // Catch:{ all -> 0x02d5 }
            java.lang.Object r0 = r9.get(r0)     // Catch:{ all -> 0x02d5 }
            java.util.ArrayList r0 = (java.util.ArrayList) r0     // Catch:{ all -> 0x02d5 }
            if (r0 != 0) goto L_0x0253
            java.util.ArrayList r2 = new java.util.ArrayList     // Catch:{ all -> 0x02d5 }
            r2.<init>()     // Catch:{ all -> 0x02d5 }
            r0 = r2
            java.lang.String r2 = r10.type     // Catch:{ all -> 0x02d5 }
            r9.put(r2, r0)     // Catch:{ all -> 0x02d5 }
        L_0x0253:
            java.lang.String r2 = r10.name     // Catch:{ all -> 0x02d5 }
            r0.add(r2)     // Catch:{ all -> 0x02d5 }
        L_0x0258:
            r6 = r9
            r0 = r19
            r10 = r20
            r9 = r26
            goto L_0x0119
        L_0x0261:
            r26 = r9
            r20 = r10
            r9 = r6
            java.util.Set r0 = r9.entrySet()     // Catch:{ all -> 0x02d5 }
            java.util.Iterator r0 = r0.iterator()     // Catch:{ all -> 0x02d5 }
        L_0x026e:
            boolean r2 = r0.hasNext()     // Catch:{ all -> 0x02d5 }
            if (r2 == 0) goto L_0x02be
            java.lang.Object r2 = r0.next()     // Catch:{ all -> 0x02d5 }
            java.util.Map$Entry r2 = (java.util.Map.Entry) r2     // Catch:{ all -> 0x02d5 }
            java.lang.Object r3 = r2.getKey()     // Catch:{ all -> 0x02d5 }
            java.lang.String r3 = (java.lang.String) r3     // Catch:{ all -> 0x02d5 }
            java.lang.Object r4 = r2.getValue()     // Catch:{ all -> 0x02d5 }
            java.util.ArrayList r4 = (java.util.ArrayList) r4     // Catch:{ all -> 0x02d5 }
            int r5 = r4.size()     // Catch:{ all -> 0x02d5 }
            android.accounts.Account[] r5 = new android.accounts.Account[r5]     // Catch:{ all -> 0x02d5 }
            r6 = 0
        L_0x028d:
            int r10 = r5.length     // Catch:{ all -> 0x02d5 }
            if (r6 >= r10) goto L_0x02b2
            android.accounts.Account r10 = new android.accounts.Account     // Catch:{ all -> 0x02d5 }
            java.lang.Object r18 = r4.get(r6)     // Catch:{ all -> 0x02d5 }
            r19 = r0
            r0 = r18
            java.lang.String r0 = (java.lang.String) r0     // Catch:{ all -> 0x02d5 }
            java.util.UUID r18 = java.util.UUID.randomUUID()     // Catch:{ all -> 0x02d5 }
            r21 = r2
            java.lang.String r2 = r18.toString()     // Catch:{ all -> 0x02d5 }
            r10.<init>(r0, r3, r2)     // Catch:{ all -> 0x02d5 }
            r5[r6] = r10     // Catch:{ all -> 0x02d5 }
            int r6 = r6 + 1
            r0 = r19
            r2 = r21
            goto L_0x028d
        L_0x02b2:
            r19 = r0
            r21 = r2
            java.util.HashMap<java.lang.String, android.accounts.Account[]> r0 = r8.accountCache     // Catch:{ all -> 0x02d5 }
            r0.put(r3, r5)     // Catch:{ all -> 0x02d5 }
            r0 = r19
            goto L_0x026e
        L_0x02be:
            java.util.Map r0 = r28.visibilityCache     // Catch:{ all -> 0x02d5 }
            java.util.Map r2 = r13.findAllVisibilityValues()     // Catch:{ all -> 0x02d5 }
            r0.putAll(r2)     // Catch:{ all -> 0x02d5 }
            if (r1 == 0) goto L_0x02d2
            int r0 = r28.userId     // Catch:{ all -> 0x02ee }
            r7.sendAccountsChangedBroadcast(r0)     // Catch:{ all -> 0x02ee }
        L_0x02d2:
            monitor-exit(r12)     // Catch:{ all -> 0x02ee }
            monitor-exit(r11)     // Catch:{ all -> 0x02f7 }
            return
        L_0x02d5:
            r0 = move-exception
            goto L_0x02dc
        L_0x02d7:
            r0 = move-exception
            r26 = r9
            r20 = r10
        L_0x02dc:
            if (r1 == 0) goto L_0x02e5
            int r2 = r28.userId     // Catch:{ all -> 0x02ee }
            r7.sendAccountsChangedBroadcast(r2)     // Catch:{ all -> 0x02ee }
        L_0x02e5:
            throw r0     // Catch:{ all -> 0x02ee }
        L_0x02e7:
            r0 = move-exception
            r26 = r9
            r20 = r10
        L_0x02ec:
            monitor-exit(r12)     // Catch:{ all -> 0x02ee }
            throw r0     // Catch:{ all -> 0x02f7 }
        L_0x02ee:
            r0 = move-exception
            goto L_0x02ec
        L_0x02f0:
            r0 = move-exception
            r26 = r9
            r20 = r10
        L_0x02f5:
            monitor-exit(r11)     // Catch:{ all -> 0x02f7 }
            throw r0
        L_0x02f7:
            r0 = move-exception
            goto L_0x02f5
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.accounts.AccountManagerService.validateAccountsInternal(com.android.server.accounts.AccountManagerService$UserAccounts, boolean):void");
    }

    private SparseBooleanArray getUidsOfInstalledOrUpdatedPackagesAsUser(int userId) {
        List<PackageInfo> pkgsWithData = this.mPackageManager.getInstalledPackagesAsUser(8192, userId);
        SparseBooleanArray knownUids = new SparseBooleanArray(pkgsWithData.size());
        for (PackageInfo pkgInfo : pkgsWithData) {
            if (!(pkgInfo.applicationInfo == null || (pkgInfo.applicationInfo.flags & DumpState.DUMP_VOLUMES) == 0)) {
                knownUids.put(pkgInfo.applicationInfo.uid, true);
            }
        }
        return knownUids;
    }

    static HashMap<String, Integer> getAuthenticatorTypeAndUIDForUser(Context context, int userId) {
        return getAuthenticatorTypeAndUIDForUser((IAccountAuthenticatorCache) new AccountAuthenticatorCache(context), userId);
    }

    private static HashMap<String, Integer> getAuthenticatorTypeAndUIDForUser(IAccountAuthenticatorCache authCache, int userId) {
        HashMap<String, Integer> knownAuth = new LinkedHashMap<>();
        for (RegisteredServicesCache.ServiceInfo<AuthenticatorDescription> service : authCache.getAllServices(userId)) {
            knownAuth.put(((AuthenticatorDescription) service.type).type, Integer.valueOf(service.uid));
        }
        return knownAuth;
    }

    private UserAccounts getUserAccountsForCaller() {
        return getUserAccounts(UserHandle.getCallingUserId());
    }

    /* Debug info: failed to restart local var, previous not found, register: 7 */
    /* access modifiers changed from: protected */
    public UserAccounts getUserAccounts(int userId) {
        UserAccounts accounts;
        synchronized (this.mUsers) {
            accounts = this.mUsers.get(userId);
            boolean validateAccounts = false;
            if (accounts == null) {
                accounts = new UserAccounts(this.mContext, userId, new File(this.mInjector.getPreNDatabaseName(userId)), new File(this.mInjector.getDeDatabaseName(userId)));
                this.mUsers.append(userId, accounts);
                purgeOldGrants(accounts);
                validateAccounts = true;
            }
            if (!accounts.accountsDb.isCeDatabaseAttached() && this.mLocalUnlockedUsers.get(userId)) {
                Log.i(TAG, "User " + userId + " is unlocked - opening CE database");
                synchronized (accounts.dbLock) {
                    synchronized (accounts.cacheLock) {
                        accounts.accountsDb.attachCeDatabase(new File(this.mInjector.getCeDatabaseName(userId)));
                    }
                }
                syncDeCeAccountsLocked(accounts);
            }
            if (validateAccounts) {
                validateAccountsInternal(accounts, true);
            }
        }
        return accounts;
    }

    private void syncDeCeAccountsLocked(UserAccounts accounts) {
        Preconditions.checkState(Thread.holdsLock(this.mUsers), "mUsers lock must be held");
        List<Account> accountsToRemove = accounts.accountsDb.findCeAccountsNotInDe();
        if (!accountsToRemove.isEmpty()) {
            Slog.i(TAG, accountsToRemove.size() + " accounts were previously deleted while user " + accounts.userId + " was locked. Removing accounts from CE tables");
            logRecord(accounts, AccountsDb.DEBUG_ACTION_SYNC_DE_CE_ACCOUNTS, "accounts");
            for (Account account : accountsToRemove) {
                removeAccountInternal(accounts, account, 1000);
            }
        }
    }

    /* access modifiers changed from: private */
    public void purgeOldGrantsAll() {
        synchronized (this.mUsers) {
            for (int i = 0; i < this.mUsers.size(); i++) {
                purgeOldGrants(this.mUsers.valueAt(i));
            }
        }
    }

    /* Debug info: failed to restart local var, previous not found, register: 9 */
    private void purgeOldGrants(UserAccounts accounts) {
        synchronized (accounts.dbLock) {
            synchronized (accounts.cacheLock) {
                for (Integer intValue : accounts.accountsDb.findAllUidGrants()) {
                    int uid = intValue.intValue();
                    if (!(this.mPackageManager.getPackagesForUid(uid) != null)) {
                        Log.d(TAG, "deleting grants for UID " + uid + " because its package is no longer installed");
                        accounts.accountsDb.deleteGrantsByUid(uid);
                    }
                }
            }
        }
    }

    /* Debug info: failed to restart local var, previous not found, register: 10 */
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
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
        	at jadx.core.dex.visitors.regions.RegionMaker.processMonitorEnter(RegionMaker.java:561)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverse(RegionMaker.java:133)
        	at jadx.core.dex.visitors.regions.RegionMaker.makeRegion(RegionMaker.java:86)
        	at jadx.core.dex.visitors.regions.RegionMaker.processExcHandler(RegionMaker.java:1043)
        	at jadx.core.dex.visitors.regions.RegionMaker.processTryCatchBlocks(RegionMaker.java:975)
        	at jadx.core.dex.visitors.regions.RegionMakerVisitor.visit(RegionMakerVisitor.java:52)
        */
    public void removeVisibilityValuesForPackage(java.lang.String r11) {
        /*
            r10 = this;
            boolean r0 = r10.isSpecialPackageKey(r11)
            if (r0 == 0) goto L_0x0007
            return
        L_0x0007:
            android.util.SparseArray<com.android.server.accounts.AccountManagerService$UserAccounts> r0 = r10.mUsers
            monitor-enter(r0)
            android.util.SparseArray<com.android.server.accounts.AccountManagerService$UserAccounts> r1 = r10.mUsers     // Catch:{ all -> 0x0060 }
            int r1 = r1.size()     // Catch:{ all -> 0x0060 }
            r2 = 0
        L_0x0011:
            if (r2 >= r1) goto L_0x005e
            android.util.SparseArray<com.android.server.accounts.AccountManagerService$UserAccounts> r3 = r10.mUsers     // Catch:{ all -> 0x0060 }
            java.lang.Object r3 = r3.valueAt(r2)     // Catch:{ all -> 0x0060 }
            com.android.server.accounts.AccountManagerService$UserAccounts r3 = (com.android.server.accounts.AccountManagerService.UserAccounts) r3     // Catch:{ all -> 0x0060 }
            android.content.pm.PackageManager r4 = r10.mPackageManager     // Catch:{ NameNotFoundException -> 0x0025 }
            int r5 = r3.userId     // Catch:{ NameNotFoundException -> 0x0025 }
            r4.getPackageUidAsUser(r11, r5)     // Catch:{ NameNotFoundException -> 0x0025 }
            goto L_0x0055
        L_0x0025:
            r4 = move-exception
            com.android.server.accounts.AccountsDb r5 = r3.accountsDb     // Catch:{ all -> 0x0060 }
            r5.deleteAccountVisibilityForPackage(r11)     // Catch:{ all -> 0x0060 }
            java.lang.Object r5 = r3.dbLock     // Catch:{ all -> 0x0060 }
            monitor-enter(r5)     // Catch:{ all -> 0x0060 }
            java.lang.Object r6 = r3.cacheLock     // Catch:{ all -> 0x005b }
            monitor-enter(r6)     // Catch:{ all -> 0x005b }
            java.util.Map r7 = r3.visibilityCache     // Catch:{ all -> 0x0058 }
            java.util.Set r7 = r7.keySet()     // Catch:{ all -> 0x0058 }
            java.util.Iterator r7 = r7.iterator()     // Catch:{ all -> 0x0058 }
        L_0x003d:
            boolean r8 = r7.hasNext()     // Catch:{ all -> 0x0058 }
            if (r8 == 0) goto L_0x0053
            java.lang.Object r8 = r7.next()     // Catch:{ all -> 0x0058 }
            android.accounts.Account r8 = (android.accounts.Account) r8     // Catch:{ all -> 0x0058 }
            java.util.Map r9 = r10.getPackagesAndVisibilityForAccountLocked(r8, r3)     // Catch:{ all -> 0x0058 }
            r9.remove(r11)     // Catch:{ all -> 0x0058 }
            goto L_0x003d
        L_0x0053:
            monitor-exit(r6)     // Catch:{ all -> 0x0058 }
            monitor-exit(r5)     // Catch:{ all -> 0x005b }
        L_0x0055:
            int r2 = r2 + 1
            goto L_0x0011
        L_0x0058:
            r7 = move-exception
            monitor-exit(r6)     // Catch:{ all -> 0x0058 }
            throw r7     // Catch:{ all -> 0x005b }
        L_0x005b:
            r6 = move-exception
            monitor-exit(r5)     // Catch:{ all -> 0x005b }
            throw r6     // Catch:{ all -> 0x0060 }
        L_0x005e:
            monitor-exit(r0)     // Catch:{ all -> 0x0060 }
            return
        L_0x0060:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x0060 }
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.accounts.AccountManagerService.removeVisibilityValuesForPackage(java.lang.String):void");
    }

    /* Debug info: failed to restart local var, previous not found, register: 4 */
    /* access modifiers changed from: private */
    public void purgeUserData(int userId) {
        UserAccounts accounts;
        synchronized (this.mUsers) {
            accounts = this.mUsers.get(userId);
            this.mUsers.remove(userId);
            this.mLocalUnlockedUsers.delete(userId);
        }
        if (accounts != null) {
            synchronized (accounts.dbLock) {
                synchronized (accounts.cacheLock) {
                    accounts.accountsDb.closeDebugStatement();
                    accounts.accountsDb.close();
                }
            }
        }
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public void onUserUnlocked(Intent intent) {
        onUnlockUser(intent.getIntExtra("android.intent.extra.user_handle", -1));
    }

    /* access modifiers changed from: package-private */
    public void onUnlockUser(int userId) {
        if (Log.isLoggable(TAG, 2)) {
            Log.v(TAG, "onUserUnlocked " + userId);
        }
        synchronized (this.mUsers) {
            this.mLocalUnlockedUsers.put(userId, true);
        }
        if (userId >= 1) {
            this.mHandler.post(new Runnable(userId) {
                private final /* synthetic */ int f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    AccountManagerService.this.lambda$onUnlockUser$1$AccountManagerService(this.f$1);
                }
            });
        }
    }

    public /* synthetic */ void lambda$onUnlockUser$1$AccountManagerService(int userId) {
        try {
            syncSharedAccounts(userId);
        } catch (SQLiteException e) {
            Slog.w(TAG, "failed to sync shared accounts for user " + userId, e);
        }
    }

    private void syncSharedAccounts(int userId) {
        int parentUserId;
        Account[] sharedAccounts = getSharedAccountsAsUser(userId);
        if (sharedAccounts != null && sharedAccounts.length != 0) {
            Account[] accounts = getAccountsAsUser((String) null, userId, this.mContext.getOpPackageName());
            if (UserManager.isSplitSystemUser()) {
                parentUserId = getUserManager().getUserInfo(userId).restrictedProfileParentId;
            } else {
                parentUserId = 0;
            }
            if (parentUserId < 0) {
                Log.w(TAG, "User " + userId + " has shared accounts, but no parent user");
                return;
            }
            for (Account sa : sharedAccounts) {
                if (!ArrayUtils.contains(accounts, sa)) {
                    copyAccountToUser((IAccountManagerResponse) null, sa, parentUserId, userId);
                }
            }
        }
    }

    public void onServiceChanged(AuthenticatorDescription desc, int userId, boolean removed) {
        try {
            validateAccountsInternal(getUserAccounts(userId), false);
        } catch (SQLiteException e) {
            Slog.w(TAG, "failed to validate accounts for user " + userId, e);
        }
    }

    public String getPassword(Account account) {
        SeempLog.record(14);
        int callingUid = Binder.getCallingUid();
        if (Log.isLoggable(TAG, 2)) {
            Log.v(TAG, "getPassword: " + account + ", caller's uid " + Binder.getCallingUid() + ", pid " + Binder.getCallingPid());
        }
        if (account != null) {
            int userId = UserHandle.getCallingUserId();
            if (isAccountManagedByCaller(account.type, callingUid, userId)) {
                long identityToken = clearCallingIdentity();
                try {
                    return readPasswordInternal(getUserAccounts(userId), account);
                } finally {
                    restoreCallingIdentity(identityToken);
                }
            } else {
                throw new SecurityException(String.format("uid %s cannot get secrets for accounts of type: %s", new Object[]{Integer.valueOf(callingUid), account.type}));
            }
        } else {
            throw new IllegalArgumentException("account is null");
        }
    }

    /* Debug info: failed to restart local var, previous not found, register: 5 */
    private String readPasswordInternal(UserAccounts accounts, Account account) {
        String findAccountPasswordByNameAndType;
        if (account == null) {
            return null;
        }
        if (!isLocalUnlockedUser(accounts.userId)) {
            Log.w(TAG, "Password is not available - user " + accounts.userId + " data is locked");
            return null;
        }
        synchronized (accounts.dbLock) {
            synchronized (accounts.cacheLock) {
                findAccountPasswordByNameAndType = accounts.accountsDb.findAccountPasswordByNameAndType(account.name, account.type);
            }
        }
        return findAccountPasswordByNameAndType;
    }

    public String getPreviousName(Account account) {
        if (Log.isLoggable(TAG, 2)) {
            Log.v(TAG, "getPreviousName: " + account + ", caller's uid " + Binder.getCallingUid() + ", pid " + Binder.getCallingPid());
        }
        Preconditions.checkNotNull(account, "account cannot be null");
        int userId = UserHandle.getCallingUserId();
        long identityToken = clearCallingIdentity();
        try {
            return readPreviousNameInternal(getUserAccounts(userId), account);
        } finally {
            restoreCallingIdentity(identityToken);
        }
    }

    /* Debug info: failed to restart local var, previous not found, register: 5 */
    private String readPreviousNameInternal(UserAccounts accounts, Account account) {
        if (account == null) {
            return null;
        }
        synchronized (accounts.dbLock) {
            synchronized (accounts.cacheLock) {
                AtomicReference<String> previousNameRef = (AtomicReference) accounts.previousNameCache.get(account);
                if (previousNameRef == null) {
                    String previousName = accounts.accountsDb.findDeAccountPreviousName(account);
                    accounts.previousNameCache.put(account, new AtomicReference(previousName));
                    return previousName;
                }
                String str = previousNameRef.get();
                return str;
            }
        }
    }

    public String getUserData(Account account, String key) {
        SeempLog.record(15);
        int callingUid = Binder.getCallingUid();
        if (Log.isLoggable(TAG, 2)) {
            Log.v(TAG, String.format("getUserData( account: %s, key: %s, callerUid: %s, pid: %s", new Object[]{account, key, Integer.valueOf(callingUid), Integer.valueOf(Binder.getCallingPid())}));
        }
        Preconditions.checkNotNull(account, "account cannot be null");
        Preconditions.checkNotNull(key, "key cannot be null");
        int userId = UserHandle.getCallingUserId();
        if (!isAccountManagedByCaller(account.type, callingUid, userId)) {
            throw new SecurityException(String.format("uid %s cannot get user data for accounts of type: %s", new Object[]{Integer.valueOf(callingUid), account.type}));
        } else if (!isLocalUnlockedUser(userId)) {
            Log.w(TAG, "User " + userId + " data is locked. callingUid " + callingUid);
            return null;
        } else {
            long identityToken = clearCallingIdentity();
            try {
                UserAccounts accounts = getUserAccounts(userId);
                if (!accountExistsCache(accounts, account)) {
                    return null;
                }
                String readUserDataInternal = readUserDataInternal(accounts, account, key);
                restoreCallingIdentity(identityToken);
                return readUserDataInternal;
            } finally {
                restoreCallingIdentity(identityToken);
            }
        }
    }

    public AuthenticatorDescription[] getAuthenticatorTypes(int userId) {
        int callingUid = Binder.getCallingUid();
        if (Log.isLoggable(TAG, 2)) {
            Log.v(TAG, "getAuthenticatorTypes: for user id " + userId + " caller's uid " + callingUid + ", pid " + Binder.getCallingPid());
        }
        if (!isCrossUser(callingUid, userId)) {
            long identityToken = clearCallingIdentity();
            try {
                return getAuthenticatorTypesInternal(userId);
            } finally {
                restoreCallingIdentity(identityToken);
            }
        } else {
            throw new SecurityException(String.format("User %s tying to get authenticator types for %s", new Object[]{Integer.valueOf(UserHandle.getCallingUserId()), Integer.valueOf(userId)}));
        }
    }

    private AuthenticatorDescription[] getAuthenticatorTypesInternal(int userId) {
        this.mAuthenticatorCache.updateServices(userId);
        Collection<RegisteredServicesCache.ServiceInfo<AuthenticatorDescription>> authenticatorCollection = this.mAuthenticatorCache.getAllServices(userId);
        AuthenticatorDescription[] types = new AuthenticatorDescription[authenticatorCollection.size()];
        int i = 0;
        for (RegisteredServicesCache.ServiceInfo<AuthenticatorDescription> authenticator : authenticatorCollection) {
            types[i] = (AuthenticatorDescription) authenticator.type;
            i++;
        }
        return types;
    }

    private boolean isCrossUser(int callingUid, int userId) {
        return (userId == UserHandle.getCallingUserId() || callingUid == 1000 || this.mContext.checkCallingOrSelfPermission("android.permission.INTERACT_ACROSS_USERS_FULL") == 0) ? false : true;
    }

    public boolean addAccountExplicitly(Account account, String password, Bundle extras) {
        return addAccountExplicitlyWithVisibility(account, password, extras, (Map) null);
    }

    public void copyAccountToUser(IAccountManagerResponse response, Account account, int userFrom, int userTo) {
        IAccountManagerResponse iAccountManagerResponse = response;
        Account account2 = account;
        int i = userFrom;
        int i2 = userTo;
        int callingUid = Binder.getCallingUid();
        if (!isCrossUser(callingUid, -1)) {
            UserAccounts fromAccounts = getUserAccounts(i);
            UserAccounts toAccounts = getUserAccounts(i2);
            if (fromAccounts == null) {
            } else if (toAccounts == null) {
                int i3 = callingUid;
            } else {
                Slog.d(TAG, "Copying account " + account.toSafeString() + " from user " + i + " to user " + i2);
                long identityToken = clearCallingIdentity();
                try {
                    String str = account2.type;
                    String str2 = account2.name;
                    int i4 = callingUid;
                    final Account account3 = account;
                    final IAccountManagerResponse iAccountManagerResponse2 = response;
                    final UserAccounts userAccounts = toAccounts;
                    final int i5 = userFrom;
                    try {
                        AnonymousClass5 r1 = new Session(fromAccounts, response, str, false, false, str2, false) {
                            /* access modifiers changed from: protected */
                            public String toDebugString(long now) {
                                return super.toDebugString(now) + ", getAccountCredentialsForClone, " + account3.type;
                            }

                            public void run() throws RemoteException {
                                this.mAuthenticator.getAccountCredentialsForCloning(this, account3);
                            }

                            public void onResult(Bundle result) {
                                Bundle.setDefusable(result, true);
                                if (result == null || !result.getBoolean("booleanResult", false)) {
                                    super.onResult(result);
                                    return;
                                }
                                AccountManagerService.this.completeCloningAccount(iAccountManagerResponse2, result, account3, userAccounts, i5);
                            }
                        };
                        r1.bind();
                        restoreCallingIdentity(identityToken);
                        return;
                    } catch (Throwable th) {
                        th = th;
                        restoreCallingIdentity(identityToken);
                        throw th;
                    }
                } catch (Throwable th2) {
                    th = th2;
                    int i6 = callingUid;
                    restoreCallingIdentity(identityToken);
                    throw th;
                }
            }
            if (iAccountManagerResponse != null) {
                Bundle result = new Bundle();
                result.putBoolean("booleanResult", false);
                try {
                    iAccountManagerResponse.onResult(result);
                } catch (RemoteException e) {
                    Slog.w(TAG, "Failed to report error back to the client." + e);
                }
            }
        } else {
            throw new SecurityException("Calling copyAccountToUser requires android.permission.INTERACT_ACROSS_USERS_FULL");
        }
    }

    public boolean accountAuthenticated(Account account) {
        int callingUid = Binder.getCallingUid();
        if (Log.isLoggable(TAG, 2)) {
            Log.v(TAG, String.format("accountAuthenticated( account: %s, callerUid: %s)", new Object[]{account, Integer.valueOf(callingUid)}));
        }
        Preconditions.checkNotNull(account, "account cannot be null");
        int userId = UserHandle.getCallingUserId();
        if (!isAccountManagedByCaller(account.type, callingUid, userId)) {
            throw new SecurityException(String.format("uid %s cannot notify authentication for accounts of type: %s", new Object[]{Integer.valueOf(callingUid), account.type}));
        } else if (!canUserModifyAccounts(userId, callingUid) || !canUserModifyAccountsForType(userId, account.type, callingUid)) {
            return false;
        } else {
            long identityToken = clearCallingIdentity();
            try {
                UserAccounts userAccounts = getUserAccounts(userId);
                return updateLastAuthenticatedTime(account);
            } finally {
                restoreCallingIdentity(identityToken);
            }
        }
    }

    /* Debug info: failed to restart local var, previous not found, register: 4 */
    /* access modifiers changed from: private */
    public boolean updateLastAuthenticatedTime(Account account) {
        boolean updateAccountLastAuthenticatedTime;
        UserAccounts accounts = getUserAccountsForCaller();
        synchronized (accounts.dbLock) {
            synchronized (accounts.cacheLock) {
                updateAccountLastAuthenticatedTime = accounts.accountsDb.updateAccountLastAuthenticatedTime(account);
            }
        }
        return updateAccountLastAuthenticatedTime;
    }

    /* access modifiers changed from: private */
    public void completeCloningAccount(IAccountManagerResponse response, Bundle accountCredentials, Account account, UserAccounts targetUser, int parentUserId) {
        Account account2 = account;
        Bundle.setDefusable(accountCredentials, true);
        long id = clearCallingIdentity();
        try {
            final Account account3 = account;
            final int i = parentUserId;
            final Bundle bundle = accountCredentials;
            new Session(targetUser, response, account2.type, false, false, account2.name, false) {
                /* access modifiers changed from: protected */
                public String toDebugString(long now) {
                    return super.toDebugString(now) + ", getAccountCredentialsForClone, " + account3.type;
                }

                public void run() throws RemoteException {
                    AccountManagerService accountManagerService = AccountManagerService.this;
                    for (Account acc : accountManagerService.getAccounts(i, accountManagerService.mContext.getOpPackageName())) {
                        if (acc.equals(account3)) {
                            this.mAuthenticator.addAccountFromCredentials(this, account3, bundle);
                            return;
                        }
                    }
                }

                public void onResult(Bundle result) {
                    Bundle.setDefusable(result, true);
                    super.onResult(result);
                }

                public void onError(int errorCode, String errorMessage) {
                    super.onError(errorCode, errorMessage);
                }
            }.bind();
        } finally {
            restoreCallingIdentity(id);
        }
    }

    /* Debug info: failed to restart local var, previous not found, register: 21 */
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
        	at jadx.core.dex.visitors.regions.RegionMaker.processMonitorEnter(RegionMaker.java:561)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverse(RegionMaker.java:133)
        	at jadx.core.dex.visitors.regions.RegionMaker.makeRegion(RegionMaker.java:86)
        	at jadx.core.dex.visitors.regions.RegionMaker.processMonitorEnter(RegionMaker.java:598)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverse(RegionMaker.java:133)
        	at jadx.core.dex.visitors.regions.RegionMaker.makeRegion(RegionMaker.java:86)
        	at jadx.core.dex.visitors.regions.RegionMaker.processIf(RegionMaker.java:698)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverse(RegionMaker.java:123)
        	at jadx.core.dex.visitors.regions.RegionMaker.makeRegion(RegionMaker.java:86)
        	at jadx.core.dex.visitors.regions.RegionMaker.processIf(RegionMaker.java:698)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverse(RegionMaker.java:123)
        	at jadx.core.dex.visitors.regions.RegionMaker.makeRegion(RegionMaker.java:86)
        	at jadx.core.dex.visitors.regions.RegionMakerVisitor.visit(RegionMakerVisitor.java:49)
        */
    private boolean addAccountInternal(com.android.server.accounts.AccountManagerService.UserAccounts r22, android.accounts.Account r23, java.lang.String r24, android.os.Bundle r25, int r26, java.util.Map<java.lang.String, java.lang.Integer> r27) {
        /*
            r21 = this;
            r8 = r21
            r9 = r22
            r10 = r23
            r11 = r25
            r0 = 1
            android.os.Bundle.setDefusable(r11, r0)
            r1 = 0
            if (r10 != 0) goto L_0x0010
            return r1
        L_0x0010:
            int r2 = r22.userId
            boolean r2 = r8.isLocalUnlockedUser(r2)
            if (r2 != 0) goto L_0x004b
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r2 = "Account "
            r0.append(r2)
            java.lang.String r2 = r23.toSafeString()
            r0.append(r2)
            java.lang.String r2 = " cannot be added - user "
            r0.append(r2)
            int r2 = r22.userId
            r0.append(r2)
            java.lang.String r2 = " is locked. callingUid="
            r0.append(r2)
            r12 = r26
            r0.append(r12)
            java.lang.String r0 = r0.toString()
            java.lang.String r2 = "AccountManagerService"
            android.util.Log.w(r2, r0)
            return r1
        L_0x004b:
            r12 = r26
            java.lang.Object r13 = r9.dbLock
            monitor-enter(r13)
            java.lang.Object r14 = r9.cacheLock     // Catch:{ all -> 0x01e3 }
            monitor-enter(r14)     // Catch:{ all -> 0x01e3 }
            com.android.server.accounts.AccountsDb r2 = r9.accountsDb     // Catch:{ all -> 0x01dc }
            r2.beginTransaction()     // Catch:{ all -> 0x01dc }
            com.android.server.accounts.AccountsDb r2 = r9.accountsDb     // Catch:{ all -> 0x01d3 }
            long r2 = r2.findCeAccountId(r10)     // Catch:{ all -> 0x01d3 }
            r4 = 0
            int r2 = (r2 > r4 ? 1 : (r2 == r4 ? 0 : -1))
            if (r2 < 0) goto L_0x008d
            java.lang.String r0 = "AccountManagerService"
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ all -> 0x01d3 }
            r2.<init>()     // Catch:{ all -> 0x01d3 }
            java.lang.String r3 = "insertAccountIntoDatabase: "
            r2.append(r3)     // Catch:{ all -> 0x01d3 }
            java.lang.String r3 = r23.toSafeString()     // Catch:{ all -> 0x01d3 }
            r2.append(r3)     // Catch:{ all -> 0x01d3 }
            java.lang.String r3 = ", skipping since the account already exists"
            r2.append(r3)     // Catch:{ all -> 0x01d3 }
            java.lang.String r2 = r2.toString()     // Catch:{ all -> 0x01d3 }
            android.util.Log.w(r0, r2)     // Catch:{ all -> 0x01d3 }
            com.android.server.accounts.AccountsDb r0 = r9.accountsDb     // Catch:{ all -> 0x01dc }
            r0.endTransaction()     // Catch:{ all -> 0x01dc }
            monitor-exit(r14)     // Catch:{ all -> 0x01dc }
            monitor-exit(r13)     // Catch:{ all -> 0x01e3 }
            return r1
        L_0x008d:
            com.android.server.accounts.AccountsDb r2 = r9.accountsDb     // Catch:{ all -> 0x01d3 }
            r15 = r24
            long r2 = r2.insertCeAccount(r10, r15)     // Catch:{ all -> 0x01d1 }
            r6 = r2
            int r2 = (r6 > r4 ? 1 : (r6 == r4 ? 0 : -1))
            if (r2 >= 0) goto L_0x00c3
            java.lang.String r0 = "AccountManagerService"
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ all -> 0x01d1 }
            r2.<init>()     // Catch:{ all -> 0x01d1 }
            java.lang.String r3 = "insertAccountIntoDatabase: "
            r2.append(r3)     // Catch:{ all -> 0x01d1 }
            java.lang.String r3 = r23.toSafeString()     // Catch:{ all -> 0x01d1 }
            r2.append(r3)     // Catch:{ all -> 0x01d1 }
            java.lang.String r3 = ", skipping the DB insert failed"
            r2.append(r3)     // Catch:{ all -> 0x01d1 }
            java.lang.String r2 = r2.toString()     // Catch:{ all -> 0x01d1 }
            android.util.Log.w(r0, r2)     // Catch:{ all -> 0x01d1 }
            com.android.server.accounts.AccountsDb r0 = r9.accountsDb     // Catch:{ all -> 0x01e1 }
            r0.endTransaction()     // Catch:{ all -> 0x01e1 }
            monitor-exit(r14)     // Catch:{ all -> 0x01e1 }
            monitor-exit(r13)     // Catch:{ all -> 0x01e8 }
            return r1
        L_0x00c3:
            com.android.server.accounts.AccountsDb r2 = r9.accountsDb     // Catch:{ all -> 0x01d1 }
            long r2 = r2.insertDeAccount(r10, r6)     // Catch:{ all -> 0x01d1 }
            int r2 = (r2 > r4 ? 1 : (r2 == r4 ? 0 : -1))
            if (r2 >= 0) goto L_0x00f6
            java.lang.String r0 = "AccountManagerService"
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ all -> 0x01d1 }
            r2.<init>()     // Catch:{ all -> 0x01d1 }
            java.lang.String r3 = "insertAccountIntoDatabase: "
            r2.append(r3)     // Catch:{ all -> 0x01d1 }
            java.lang.String r3 = r23.toSafeString()     // Catch:{ all -> 0x01d1 }
            r2.append(r3)     // Catch:{ all -> 0x01d1 }
            java.lang.String r3 = ", skipping the DB insert failed"
            r2.append(r3)     // Catch:{ all -> 0x01d1 }
            java.lang.String r2 = r2.toString()     // Catch:{ all -> 0x01d1 }
            android.util.Log.w(r0, r2)     // Catch:{ all -> 0x01d1 }
            com.android.server.accounts.AccountsDb r0 = r9.accountsDb     // Catch:{ all -> 0x01e1 }
            r0.endTransaction()     // Catch:{ all -> 0x01e1 }
            monitor-exit(r14)     // Catch:{ all -> 0x01e1 }
            monitor-exit(r13)     // Catch:{ all -> 0x01e8 }
            return r1
        L_0x00f6:
            if (r11 == 0) goto L_0x014f
            java.util.Set r2 = r25.keySet()     // Catch:{ all -> 0x01d1 }
            java.util.Iterator r2 = r2.iterator()     // Catch:{ all -> 0x01d1 }
        L_0x0100:
            boolean r3 = r2.hasNext()     // Catch:{ all -> 0x01d1 }
            if (r3 == 0) goto L_0x014f
            java.lang.Object r3 = r2.next()     // Catch:{ all -> 0x01d1 }
            java.lang.String r3 = (java.lang.String) r3     // Catch:{ all -> 0x01d1 }
            java.lang.String r16 = r11.getString(r3)     // Catch:{ all -> 0x01d1 }
            r17 = r16
            com.android.server.accounts.AccountsDb r0 = r9.accountsDb     // Catch:{ all -> 0x01d1 }
            r1 = r17
            long r19 = r0.insertExtra(r6, r3, r1)     // Catch:{ all -> 0x01d1 }
            int r0 = (r19 > r4 ? 1 : (r19 == r4 ? 0 : -1))
            if (r0 >= 0) goto L_0x014b
            java.lang.String r0 = "AccountManagerService"
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ all -> 0x01d1 }
            r2.<init>()     // Catch:{ all -> 0x01d1 }
            java.lang.String r4 = "insertAccountIntoDatabase: "
            r2.append(r4)     // Catch:{ all -> 0x01d1 }
            java.lang.String r4 = r23.toSafeString()     // Catch:{ all -> 0x01d1 }
            r2.append(r4)     // Catch:{ all -> 0x01d1 }
            java.lang.String r4 = ", skipping since insertExtra failed for key "
            r2.append(r4)     // Catch:{ all -> 0x01d1 }
            r2.append(r3)     // Catch:{ all -> 0x01d1 }
            java.lang.String r2 = r2.toString()     // Catch:{ all -> 0x01d1 }
            android.util.Log.w(r0, r2)     // Catch:{ all -> 0x01d1 }
            com.android.server.accounts.AccountsDb r0 = r9.accountsDb     // Catch:{ all -> 0x01e1 }
            r0.endTransaction()     // Catch:{ all -> 0x01e1 }
            monitor-exit(r14)     // Catch:{ all -> 0x01e1 }
            monitor-exit(r13)     // Catch:{ all -> 0x01e8 }
            r0 = 0
            return r0
        L_0x014b:
            r0 = 0
            r1 = r0
            r0 = 1
            goto L_0x0100
        L_0x014f:
            if (r27 == 0) goto L_0x018a
            java.util.Set r0 = r27.entrySet()     // Catch:{ all -> 0x01d1 }
            java.util.Iterator r0 = r0.iterator()     // Catch:{ all -> 0x01d1 }
        L_0x0159:
            boolean r1 = r0.hasNext()     // Catch:{ all -> 0x01d1 }
            if (r1 == 0) goto L_0x0187
            java.lang.Object r1 = r0.next()     // Catch:{ all -> 0x01d1 }
            java.util.Map$Entry r1 = (java.util.Map.Entry) r1     // Catch:{ all -> 0x01d1 }
            r17 = r1
            java.lang.Object r1 = r17.getKey()     // Catch:{ all -> 0x01d1 }
            r3 = r1
            java.lang.String r3 = (java.lang.String) r3     // Catch:{ all -> 0x01d1 }
            java.lang.Object r1 = r17.getValue()     // Catch:{ all -> 0x01d1 }
            java.lang.Integer r1 = (java.lang.Integer) r1     // Catch:{ all -> 0x01d1 }
            int r4 = r1.intValue()     // Catch:{ all -> 0x01d1 }
            r5 = 0
            r1 = r21
            r2 = r23
            r18 = r6
            r6 = r22
            r1.setAccountVisibility(r2, r3, r4, r5, r6)     // Catch:{ all -> 0x01d1 }
            r6 = r18
            goto L_0x0159
        L_0x0187:
            r18 = r6
            goto L_0x018c
        L_0x018a:
            r18 = r6
        L_0x018c:
            com.android.server.accounts.AccountsDb r0 = r9.accountsDb     // Catch:{ all -> 0x01d1 }
            r0.setTransactionSuccessful()     // Catch:{ all -> 0x01d1 }
            java.lang.String r2 = com.android.server.accounts.AccountsDb.DEBUG_ACTION_ACCOUNT_ADD     // Catch:{ all -> 0x01d1 }
            java.lang.String r3 = "accounts"
            r1 = r21
            r4 = r18
            r6 = r22
            r7 = r26
            r1.logRecord(r2, r3, r4, r6, r7)     // Catch:{ all -> 0x01d1 }
            r21.insertAccountIntoCacheLocked(r22, r23)     // Catch:{ all -> 0x01d1 }
            com.android.server.accounts.AccountsDb r0 = r9.accountsDb     // Catch:{ all -> 0x01e1 }
            r0.endTransaction()     // Catch:{ all -> 0x01e1 }
            monitor-exit(r14)     // Catch:{ all -> 0x01e1 }
            monitor-exit(r13)     // Catch:{ all -> 0x01e8 }
            android.os.UserManager r0 = r21.getUserManager()
            int r1 = r22.userId
            android.content.pm.UserInfo r0 = r0.getUserInfo(r1)
            boolean r0 = r0.canHaveProfile()
            if (r0 == 0) goto L_0x01c5
            int r0 = r22.userId
            r8.addAccountToLinkedRestrictedUsers(r10, r0)
        L_0x01c5:
            r8.sendNotificationAccountUpdated(r10, r9)
            int r0 = r22.userId
            r8.sendAccountsChangedBroadcast(r0)
            r0 = 1
            return r0
        L_0x01d1:
            r0 = move-exception
            goto L_0x01d6
        L_0x01d3:
            r0 = move-exception
            r15 = r24
        L_0x01d6:
            com.android.server.accounts.AccountsDb r1 = r9.accountsDb     // Catch:{ all -> 0x01e1 }
            r1.endTransaction()     // Catch:{ all -> 0x01e1 }
            throw r0     // Catch:{ all -> 0x01e1 }
        L_0x01dc:
            r0 = move-exception
            r15 = r24
        L_0x01df:
            monitor-exit(r14)     // Catch:{ all -> 0x01e1 }
            throw r0     // Catch:{ all -> 0x01e8 }
        L_0x01e1:
            r0 = move-exception
            goto L_0x01df
        L_0x01e3:
            r0 = move-exception
            r15 = r24
        L_0x01e6:
            monitor-exit(r13)     // Catch:{ all -> 0x01e8 }
            throw r0
        L_0x01e8:
            r0 = move-exception
            goto L_0x01e6
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.accounts.AccountManagerService.addAccountInternal(com.android.server.accounts.AccountManagerService$UserAccounts, android.accounts.Account, java.lang.String, android.os.Bundle, int, java.util.Map):boolean");
    }

    /* access modifiers changed from: private */
    public boolean isLocalUnlockedUser(int userId) {
        boolean z;
        synchronized (this.mUsers) {
            z = this.mLocalUnlockedUsers.get(userId);
        }
        return z;
    }

    private void addAccountToLinkedRestrictedUsers(Account account, int parentUserId) {
        for (UserInfo user : getUserManager().getUsers()) {
            if (user.isRestricted() && parentUserId == user.restrictedProfileParentId) {
                addSharedAccountAsUser(account, user.id);
                if (isLocalUnlockedUser(user.id)) {
                    MessageHandler messageHandler = this.mHandler;
                    messageHandler.sendMessage(messageHandler.obtainMessage(4, parentUserId, user.id, account));
                }
            }
        }
    }

    public void hasFeatures(IAccountManagerResponse response, Account account, String[] features, String opPackageName) {
        int callingUid = Binder.getCallingUid();
        this.mAppOpsManager.checkPackage(callingUid, opPackageName);
        if (Log.isLoggable(TAG, 2)) {
            Log.v(TAG, "hasFeatures: " + account + ", response " + response + ", features " + Arrays.toString(features) + ", caller's uid " + callingUid + ", pid " + Binder.getCallingPid());
        }
        boolean z = true;
        Preconditions.checkArgument(account != null, "account cannot be null");
        Preconditions.checkArgument(response != null, "response cannot be null");
        if (features == null) {
            z = false;
        }
        Preconditions.checkArgument(z, "features cannot be null");
        int userId = UserHandle.getCallingUserId();
        checkReadAccountsPermitted(callingUid, account.type, userId, opPackageName);
        long identityToken = clearCallingIdentity();
        try {
            new TestFeaturesSession(getUserAccounts(userId), response, account, features).bind();
        } finally {
            restoreCallingIdentity(identityToken);
        }
    }

    private class TestFeaturesSession extends Session {
        private final Account mAccount;
        private final String[] mFeatures;

        public TestFeaturesSession(UserAccounts accounts, IAccountManagerResponse response, Account account, String[] features) {
            super(AccountManagerService.this, accounts, response, account.type, false, true, account.name, false);
            this.mFeatures = features;
            this.mAccount = account;
        }

        public void run() throws RemoteException {
            try {
                this.mAuthenticator.hasFeatures(this, this.mAccount, this.mFeatures);
            } catch (RemoteException e) {
                onError(1, "remote exception");
            }
        }

        public void onResult(Bundle result) {
            Bundle.setDefusable(result, true);
            IAccountManagerResponse response = getResponseAndClose();
            if (response == null) {
                return;
            }
            if (result == null) {
                try {
                    response.onError(5, "null bundle");
                } catch (RemoteException e) {
                    if (Log.isLoggable(AccountManagerService.TAG, 2)) {
                        Log.v(AccountManagerService.TAG, "failure while notifying response", e);
                    }
                }
            } else {
                if (Log.isLoggable(AccountManagerService.TAG, 2)) {
                    Log.v(AccountManagerService.TAG, getClass().getSimpleName() + " calling onResult() on response " + response);
                }
                Bundle newResult = new Bundle();
                newResult.putBoolean("booleanResult", result.getBoolean("booleanResult", false));
                response.onResult(newResult);
            }
        }

        /* access modifiers changed from: protected */
        public String toDebugString(long now) {
            StringBuilder sb = new StringBuilder();
            sb.append(super.toDebugString(now));
            sb.append(", hasFeatures, ");
            sb.append(this.mAccount);
            sb.append(", ");
            String[] strArr = this.mFeatures;
            sb.append(strArr != null ? TextUtils.join(",", strArr) : null);
            return sb.toString();
        }
    }

    public void renameAccount(IAccountManagerResponse response, Account accountToRename, String newName) {
        int callingUid = Binder.getCallingUid();
        if (Log.isLoggable(TAG, 2)) {
            Log.v(TAG, "renameAccount: " + accountToRename + " -> " + newName + ", caller's uid " + callingUid + ", pid " + Binder.getCallingPid());
        }
        if (accountToRename != null) {
            int userId = UserHandle.getCallingUserId();
            if (isAccountManagedByCaller(accountToRename.type, callingUid, userId)) {
                long identityToken = clearCallingIdentity();
                try {
                    Account resultingAccount = renameAccountInternal(getUserAccounts(userId), accountToRename, newName);
                    Bundle result = new Bundle();
                    result.putString("authAccount", resultingAccount.name);
                    result.putString("accountType", resultingAccount.type);
                    result.putString("accountAccessId", resultingAccount.getAccessId());
                    response.onResult(result);
                } catch (RemoteException e) {
                    Log.w(TAG, e.getMessage());
                } catch (Throwable th) {
                    restoreCallingIdentity(identityToken);
                    throw th;
                }
                restoreCallingIdentity(identityToken);
                return;
            }
            throw new SecurityException(String.format("uid %s cannot rename accounts of type: %s", new Object[]{Integer.valueOf(callingUid), accountToRename.type}));
        }
        throw new IllegalArgumentException("account is null");
    }

    /* Debug info: failed to restart local var, previous not found, register: 17 */
    private Account renameAccountInternal(UserAccounts accounts, Account accountToRename, String newName) {
        Account renamedAccount;
        UserAccounts userAccounts = accounts;
        Account account = accountToRename;
        String str = newName;
        cancelNotification(getSigninRequiredNotificationId(accounts, accountToRename), new UserHandle(accounts.userId));
        synchronized (accounts.credentialsPermissionNotificationIds) {
            for (Pair<Pair<Account, String>, Integer> pair : accounts.credentialsPermissionNotificationIds.keySet()) {
                if (account.equals(((Pair) pair.first).first)) {
                    cancelNotification((NotificationId) accounts.credentialsPermissionNotificationIds.get(pair), new UserHandle(accounts.userId));
                }
            }
        }
        synchronized (userAccounts.dbLock) {
            synchronized (userAccounts.cacheLock) {
                List<String> accountRemovedReceivers = getAccountRemovedReceivers(account, userAccounts);
                userAccounts.accountsDb.beginTransaction();
                Account renamedAccount2 = new Account(str, account.type);
                try {
                    if (userAccounts.accountsDb.findCeAccountId(renamedAccount2) >= 0) {
                        Log.e(TAG, "renameAccount failed - account with new name already exists");
                        return null;
                    }
                    long accountId = userAccounts.accountsDb.findDeAccountId(account);
                    if (accountId >= 0) {
                        userAccounts.accountsDb.renameCeAccount(accountId, str);
                        if (userAccounts.accountsDb.renameDeAccount(accountId, str, account.name)) {
                            userAccounts.accountsDb.setTransactionSuccessful();
                            userAccounts.accountsDb.endTransaction();
                            Account renamedAccount3 = insertAccountIntoCacheLocked(userAccounts, renamedAccount2);
                            removeAccountFromCacheLocked(accounts, accountToRename);
                            accounts.userDataCache.put(renamedAccount3, (Map) accounts.userDataCache.get(account));
                            accounts.authTokenCache.put(renamedAccount3, (Map) accounts.authTokenCache.get(account));
                            accounts.visibilityCache.put(renamedAccount3, (Map) accounts.visibilityCache.get(account));
                            accounts.previousNameCache.put(renamedAccount3, new AtomicReference(account.name));
                            Account resultAccount = renamedAccount3;
                            int parentUserId = accounts.userId;
                            if (canHaveProfile(parentUserId)) {
                                for (UserInfo user : getUserManager().getUsers(true)) {
                                    if (user.isRestricted()) {
                                        renamedAccount = renamedAccount3;
                                        if (user.restrictedProfileParentId == parentUserId) {
                                            renameSharedAccountAsUser(account, str, user.id);
                                        }
                                    } else {
                                        renamedAccount = renamedAccount3;
                                    }
                                    renamedAccount3 = renamedAccount;
                                }
                            }
                            sendNotificationAccountUpdated(resultAccount, userAccounts);
                            sendAccountsChangedBroadcast(accounts.userId);
                            for (String packageName : accountRemovedReceivers) {
                                sendAccountRemovedBroadcast(account, packageName, accounts.userId);
                            }
                            return resultAccount;
                        }
                        Log.e(TAG, "renameAccount failed");
                        userAccounts.accountsDb.endTransaction();
                        return null;
                    }
                    Log.e(TAG, "renameAccount failed - old account does not exist");
                    userAccounts.accountsDb.endTransaction();
                    return null;
                } finally {
                    userAccounts.accountsDb.endTransaction();
                }
            }
        }
    }

    private boolean canHaveProfile(int parentUserId) {
        UserInfo userInfo = getUserManager().getUserInfo(parentUserId);
        return userInfo != null && userInfo.canHaveProfile();
    }

    public void removeAccount(IAccountManagerResponse response, Account account, boolean expectActivityLaunch) {
        SeempLog.record(17);
        removeAccountAsUser(response, account, expectActivityLaunch, UserHandle.getCallingUserId());
    }

    public void removeAccountAsUser(IAccountManagerResponse response, Account account, boolean expectActivityLaunch, int userId) {
        IAccountManagerResponse iAccountManagerResponse = response;
        Account account2 = account;
        int i = userId;
        int callingUid = Binder.getCallingUid();
        if (Log.isLoggable(TAG, 2)) {
            Log.v(TAG, "removeAccount: " + account2 + ", response " + iAccountManagerResponse + ", caller's uid " + callingUid + ", pid " + Binder.getCallingPid() + ", for user id " + i);
        }
        Preconditions.checkArgument(account2 != null, "account cannot be null");
        Preconditions.checkArgument(iAccountManagerResponse != null, "response cannot be null");
        if (!isCrossUser(callingUid, i)) {
            UserHandle user = UserHandle.of(userId);
            if (!isAccountManagedByCaller(account2.type, callingUid, user.getIdentifier()) && !isSystemUid(callingUid) && !isProfileOwner(callingUid)) {
                throw new SecurityException(String.format("uid %s cannot remove accounts of type: %s", new Object[]{Integer.valueOf(callingUid), account2.type}));
            } else if (!canUserModifyAccounts(i, callingUid)) {
                try {
                    iAccountManagerResponse.onError(100, "User cannot modify accounts");
                } catch (RemoteException e) {
                }
            } else if (!canUserModifyAccountsForType(i, account2.type, callingUid)) {
                try {
                    iAccountManagerResponse.onError(101, "User cannot modify accounts of this type (policy).");
                } catch (RemoteException e2) {
                }
            } else {
                long identityToken = clearCallingIdentity();
                UserAccounts accounts = getUserAccounts(i);
                cancelNotification(getSigninRequiredNotificationId(accounts, account2), user);
                synchronized (accounts.credentialsPermissionNotificationIds) {
                    try {
                        for (Pair<Pair<Account, String>, Integer> pair : accounts.credentialsPermissionNotificationIds.keySet()) {
                            try {
                                if (account2.equals(((Pair) pair.first).first)) {
                                    cancelNotification((NotificationId) accounts.credentialsPermissionNotificationIds.get(pair), user);
                                }
                            } catch (Throwable th) {
                                th = th;
                                UserAccounts userAccounts = accounts;
                                while (true) {
                                    try {
                                        break;
                                    } catch (Throwable th2) {
                                        th = th2;
                                    }
                                }
                                throw th;
                            }
                        }
                        UserAccounts accounts2 = accounts;
                        logRecord(AccountsDb.DEBUG_ACTION_CALLED_ACCOUNT_REMOVE, "accounts", accounts.accountsDb.findDeAccountId(account2), accounts, callingUid);
                        try {
                            new RemoveAccountSession(accounts2, response, account, expectActivityLaunch).bind();
                        } finally {
                            restoreCallingIdentity(identityToken);
                        }
                    } catch (Throwable th3) {
                        th = th3;
                        UserAccounts userAccounts2 = accounts;
                        while (true) {
                            break;
                        }
                        throw th;
                    }
                }
            }
        } else {
            throw new SecurityException(String.format("User %s tying remove account for %s", new Object[]{Integer.valueOf(UserHandle.getCallingUserId()), Integer.valueOf(userId)}));
        }
    }

    public boolean removeAccountExplicitly(Account account) {
        int callingUid = Binder.getCallingUid();
        if (Log.isLoggable(TAG, 2)) {
            Log.v(TAG, "removeAccountExplicitly: " + account + ", caller's uid " + callingUid + ", pid " + Binder.getCallingPid());
        }
        int userId = Binder.getCallingUserHandle().getIdentifier();
        if (account == null) {
            Log.e(TAG, "account is null");
            return false;
        } else if (isAccountManagedByCaller(account.type, callingUid, userId)) {
            UserAccounts accounts = getUserAccountsForCaller();
            logRecord(AccountsDb.DEBUG_ACTION_CALLED_ACCOUNT_REMOVE, "accounts", accounts.accountsDb.findDeAccountId(account), accounts, callingUid);
            long identityToken = clearCallingIdentity();
            try {
                return removeAccountInternal(accounts, account, callingUid);
            } finally {
                restoreCallingIdentity(identityToken);
            }
        } else {
            throw new SecurityException(String.format("uid %s cannot explicitly remove accounts of type: %s", new Object[]{Integer.valueOf(callingUid), account.type}));
        }
    }

    private class RemoveAccountSession extends Session {
        final Account mAccount;

        public RemoveAccountSession(UserAccounts accounts, IAccountManagerResponse response, Account account, boolean expectActivityLaunch) {
            super(AccountManagerService.this, accounts, response, account.type, expectActivityLaunch, true, account.name, false);
            this.mAccount = account;
        }

        /* access modifiers changed from: protected */
        public String toDebugString(long now) {
            return super.toDebugString(now) + ", removeAccount, account " + this.mAccount;
        }

        public void run() throws RemoteException {
            this.mAuthenticator.getAccountRemovalAllowed(this, this.mAccount);
        }

        public void onResult(Bundle result) {
            Bundle.setDefusable(result, true);
            if (result != null && result.containsKey("booleanResult") && !result.containsKey("intent")) {
                boolean removalAllowed = result.getBoolean("booleanResult");
                if (AccountManagerServiceInjector.isForceRemove(removalAllowed)) {
                    removalAllowed = true;
                }
                if (removalAllowed) {
                    boolean unused = AccountManagerService.this.removeAccountInternal(this.mAccounts, this.mAccount, getCallingUid());
                }
                IAccountManagerResponse response = getResponseAndClose();
                if (response != null) {
                    if (Log.isLoggable(AccountManagerService.TAG, 2)) {
                        Log.v(AccountManagerService.TAG, getClass().getSimpleName() + " calling onResult() on response " + response);
                    }
                    try {
                        response.onResult(result);
                    } catch (RemoteException e) {
                        Slog.e(AccountManagerService.TAG, "Error calling onResult()", e);
                    }
                }
            }
            super.onResult(result);
        }
    }

    /* access modifiers changed from: protected */
    @VisibleForTesting
    public void removeAccountInternal(Account account) {
        removeAccountInternal(getUserAccountsForCaller(), account, getCallingUid());
    }

    /* Debug info: failed to restart local var, previous not found, register: 18 */
    /* access modifiers changed from: private */
    /* JADX WARNING: Code restructure failed: missing block: B:100:0x01a5, code lost:
        r13 = r21;
        r1 = r15;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:56:0x0105, code lost:
        r1 = android.os.Binder.clearCallingIdentity();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:58:?, code lost:
        r3 = com.android.server.accounts.AccountManagerService.UserAccounts.access$800(r19);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:59:0x0111, code lost:
        if (canHaveProfile(r3) == false) goto L_0x0145;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:60:0x0113, code lost:
        r4 = getUserManager().getUsers(true).iterator();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:62:0x0123, code lost:
        if (r4.hasNext() == false) goto L_0x0142;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:63:0x0125, code lost:
        r5 = r4.next();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:64:0x012f, code lost:
        if (r5.isRestricted() == false) goto L_0x013f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:66:0x0133, code lost:
        if (r3 != r5.restrictedProfileParentId) goto L_0x013f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:70:?, code lost:
        removeSharedAccountAsUser(r9, r5.id, r21);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:71:0x013d, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:72:0x013f, code lost:
        r13 = r21;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:73:0x0142, code lost:
        r13 = r21;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:74:0x0145, code lost:
        r13 = r21;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:75:0x0147, code lost:
        android.os.Binder.restoreCallingIdentity(r1);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:76:0x014b, code lost:
        if (r15 == false) goto L_0x019c;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:77:0x014d, code lost:
        r3 = com.android.server.accounts.AccountManagerService.UserAccounts.access$1600(r19);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:78:0x0151, code lost:
        monitor-enter(r3);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:80:?, code lost:
        r0 = com.android.server.accounts.AccountManagerService.UserAccounts.access$1600(r19).keySet().iterator();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:82:0x0162, code lost:
        if (r0.hasNext() == false) goto L_0x0197;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:83:0x0164, code lost:
        r4 = (android.util.Pair) r0.next();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:84:0x0174, code lost:
        if (r9.equals(((android.util.Pair) r4.first).first) == false) goto L_0x0196;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:86:0x0182, code lost:
        if ("com.android.AccountManager.ACCOUNT_ACCESS_TOKEN_TYPE".equals(((android.util.Pair) r4.first).second) == false) goto L_0x0196;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:87:0x0184, code lost:
        r7.mHandler.post(new com.android.server.accounts.$$Lambda$AccountManagerService$lqbNdAUKUSipmpqby9oIO8JlNTQ(r7, r9, ((java.lang.Integer) r4.second).intValue()));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:89:0x0197, code lost:
        monitor-exit(r3);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:94:0x019c, code lost:
        return r15;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:95:0x019d, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:96:0x019e, code lost:
        r13 = r21;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:97:0x01a0, code lost:
        android.os.Binder.restoreCallingIdentity(r1);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:98:0x01a3, code lost:
        throw r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:99:0x01a4, code lost:
        r0 = th;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean removeAccountInternal(com.android.server.accounts.AccountManagerService.UserAccounts r19, android.accounts.Account r20, int r21) {
        /*
            r18 = this;
            r7 = r18
            r8 = r19
            r9 = r20
            r1 = 0
            int r0 = r19.userId
            boolean r10 = r7.isLocalUnlockedUser(r0)
            if (r10 != 0) goto L_0x003c
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r2 = "Removing account "
            r0.append(r2)
            java.lang.String r2 = r20.toSafeString()
            r0.append(r2)
            java.lang.String r2 = " while user "
            r0.append(r2)
            int r2 = r19.userId
            r0.append(r2)
            java.lang.String r2 = " is still locked. CE data will be removed later"
            r0.append(r2)
            java.lang.String r0 = r0.toString()
            java.lang.String r2 = "AccountManagerService"
            android.util.Slog.i(r2, r0)
        L_0x003c:
            java.lang.Object r11 = r8.dbLock
            monitor-enter(r11)
            java.lang.Object r12 = r8.cacheLock     // Catch:{ all -> 0x01c0 }
            monitor-enter(r12)     // Catch:{ all -> 0x01c0 }
            java.util.Map r0 = r7.getRequestingPackages(r9, r8)     // Catch:{ all -> 0x01bd }
            r13 = r0
            java.util.List r0 = r7.getAccountRemovedReceivers(r9, r8)     // Catch:{ all -> 0x01bd }
            r14 = r0
            com.android.server.accounts.AccountsDb r0 = r8.accountsDb     // Catch:{ all -> 0x01bd }
            r0.beginTransaction()     // Catch:{ all -> 0x01bd }
            r2 = -1
            com.android.server.accounts.AccountsDb r0 = r8.accountsDb     // Catch:{ all -> 0x01b1 }
            long r4 = r0.findDeAccountId(r9)     // Catch:{ all -> 0x01b1 }
            r2 = 0
            int r0 = (r4 > r2 ? 1 : (r4 == r2 ? 0 : -1))
            if (r0 < 0) goto L_0x006d
            com.android.server.accounts.AccountsDb r0 = r8.accountsDb     // Catch:{ all -> 0x0069 }
            boolean r0 = r0.deleteDeAccount(r4)     // Catch:{ all -> 0x0069 }
            r1 = r0
            r15 = r1
            goto L_0x006e
        L_0x0069:
            r0 = move-exception
            r15 = r1
            goto L_0x01b4
        L_0x006d:
            r15 = r1
        L_0x006e:
            if (r10 == 0) goto L_0x0083
            com.android.server.accounts.AccountsDb r0 = r8.accountsDb     // Catch:{ all -> 0x0080 }
            long r0 = r0.findCeAccountId(r9)     // Catch:{ all -> 0x0080 }
            int r2 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r2 < 0) goto L_0x0083
            com.android.server.accounts.AccountsDb r2 = r8.accountsDb     // Catch:{ all -> 0x0080 }
            r2.deleteCeAccount(r0)     // Catch:{ all -> 0x0080 }
            goto L_0x0083
        L_0x0080:
            r0 = move-exception
            goto L_0x01b4
        L_0x0083:
            com.android.server.accounts.AccountsDb r0 = r8.accountsDb     // Catch:{ all -> 0x01ad }
            r0.setTransactionSuccessful()     // Catch:{ all -> 0x01ad }
            com.android.server.accounts.AccountsDb r0 = r8.accountsDb     // Catch:{ all -> 0x01a9 }
            r0.endTransaction()     // Catch:{ all -> 0x01a9 }
            r0 = 1
            if (r15 == 0) goto L_0x0101
            r18.removeAccountFromCacheLocked(r19, r20)     // Catch:{ all -> 0x01ba }
            java.util.Set r1 = r13.entrySet()     // Catch:{ all -> 0x01ba }
            java.util.Iterator r1 = r1.iterator()     // Catch:{ all -> 0x01ba }
        L_0x009d:
            boolean r2 = r1.hasNext()     // Catch:{ all -> 0x01ba }
            if (r2 == 0) goto L_0x00cc
            java.lang.Object r2 = r1.next()     // Catch:{ all -> 0x01ba }
            java.util.Map$Entry r2 = (java.util.Map.Entry) r2     // Catch:{ all -> 0x01ba }
            java.lang.Object r3 = r2.getValue()     // Catch:{ all -> 0x01ba }
            java.lang.Integer r3 = (java.lang.Integer) r3     // Catch:{ all -> 0x01ba }
            int r3 = r3.intValue()     // Catch:{ all -> 0x01ba }
            if (r3 == r0) goto L_0x00c2
            java.lang.Object r3 = r2.getValue()     // Catch:{ all -> 0x01ba }
            java.lang.Integer r3 = (java.lang.Integer) r3     // Catch:{ all -> 0x01ba }
            int r3 = r3.intValue()     // Catch:{ all -> 0x01ba }
            r6 = 2
            if (r3 != r6) goto L_0x00cb
        L_0x00c2:
            java.lang.Object r3 = r2.getKey()     // Catch:{ all -> 0x01ba }
            java.lang.String r3 = (java.lang.String) r3     // Catch:{ all -> 0x01ba }
            r7.notifyPackage(r3, r8)     // Catch:{ all -> 0x01ba }
        L_0x00cb:
            goto L_0x009d
        L_0x00cc:
            int r1 = r19.userId     // Catch:{ all -> 0x01ba }
            r7.sendAccountsChangedBroadcast(r1)     // Catch:{ all -> 0x01ba }
            java.util.Iterator r1 = r14.iterator()     // Catch:{ all -> 0x01ba }
        L_0x00d7:
            boolean r2 = r1.hasNext()     // Catch:{ all -> 0x01ba }
            if (r2 == 0) goto L_0x00eb
            java.lang.Object r2 = r1.next()     // Catch:{ all -> 0x01ba }
            java.lang.String r2 = (java.lang.String) r2     // Catch:{ all -> 0x01ba }
            int r3 = r19.userId     // Catch:{ all -> 0x01ba }
            r7.sendAccountRemovedBroadcast(r9, r2, r3)     // Catch:{ all -> 0x01ba }
            goto L_0x00d7
        L_0x00eb:
            if (r10 == 0) goto L_0x00f1
            java.lang.String r1 = com.android.server.accounts.AccountsDb.DEBUG_ACTION_ACCOUNT_REMOVE     // Catch:{ all -> 0x01ba }
            r2 = r1
            goto L_0x00f4
        L_0x00f1:
            java.lang.String r1 = com.android.server.accounts.AccountsDb.DEBUG_ACTION_ACCOUNT_REMOVE_DE     // Catch:{ all -> 0x01ba }
            r2 = r1
        L_0x00f4:
            java.lang.String r3 = "accounts"
            r1 = r18
            r16 = r4
            r6 = r19
            r1.logRecord(r2, r3, r4, r6)     // Catch:{ all -> 0x01ba }
            goto L_0x0103
        L_0x0101:
            r16 = r4
        L_0x0103:
            monitor-exit(r12)     // Catch:{ all -> 0x01a9 }
            monitor-exit(r11)     // Catch:{ all -> 0x01a4 }
            long r1 = android.os.Binder.clearCallingIdentity()
            int r3 = r19.userId     // Catch:{ all -> 0x019d }
            boolean r4 = r7.canHaveProfile(r3)     // Catch:{ all -> 0x019d }
            if (r4 == 0) goto L_0x0145
            android.os.UserManager r4 = r18.getUserManager()     // Catch:{ all -> 0x019d }
            java.util.List r0 = r4.getUsers(r0)     // Catch:{ all -> 0x019d }
            java.util.Iterator r4 = r0.iterator()     // Catch:{ all -> 0x019d }
        L_0x011f:
            boolean r5 = r4.hasNext()     // Catch:{ all -> 0x019d }
            if (r5 == 0) goto L_0x0142
            java.lang.Object r5 = r4.next()     // Catch:{ all -> 0x019d }
            android.content.pm.UserInfo r5 = (android.content.pm.UserInfo) r5     // Catch:{ all -> 0x019d }
            boolean r6 = r5.isRestricted()     // Catch:{ all -> 0x019d }
            if (r6 == 0) goto L_0x013f
            int r6 = r5.restrictedProfileParentId     // Catch:{ all -> 0x019d }
            if (r3 != r6) goto L_0x013f
            int r6 = r5.id     // Catch:{ all -> 0x019d }
            r13 = r21
            r7.removeSharedAccountAsUser(r9, r6, r13)     // Catch:{ all -> 0x013d }
            goto L_0x0141
        L_0x013d:
            r0 = move-exception
            goto L_0x01a0
        L_0x013f:
            r13 = r21
        L_0x0141:
            goto L_0x011f
        L_0x0142:
            r13 = r21
            goto L_0x0147
        L_0x0145:
            r13 = r21
        L_0x0147:
            android.os.Binder.restoreCallingIdentity(r1)
            if (r15 == 0) goto L_0x019c
            java.util.HashMap r3 = r19.credentialsPermissionNotificationIds
            monitor-enter(r3)
            java.util.HashMap r0 = r19.credentialsPermissionNotificationIds     // Catch:{ all -> 0x0199 }
            java.util.Set r0 = r0.keySet()     // Catch:{ all -> 0x0199 }
            java.util.Iterator r0 = r0.iterator()     // Catch:{ all -> 0x0199 }
        L_0x015e:
            boolean r4 = r0.hasNext()     // Catch:{ all -> 0x0199 }
            if (r4 == 0) goto L_0x0197
            java.lang.Object r4 = r0.next()     // Catch:{ all -> 0x0199 }
            android.util.Pair r4 = (android.util.Pair) r4     // Catch:{ all -> 0x0199 }
            java.lang.Object r5 = r4.first     // Catch:{ all -> 0x0199 }
            android.util.Pair r5 = (android.util.Pair) r5     // Catch:{ all -> 0x0199 }
            java.lang.Object r5 = r5.first     // Catch:{ all -> 0x0199 }
            boolean r5 = r9.equals(r5)     // Catch:{ all -> 0x0199 }
            if (r5 == 0) goto L_0x0196
            java.lang.String r5 = "com.android.AccountManager.ACCOUNT_ACCESS_TOKEN_TYPE"
            java.lang.Object r6 = r4.first     // Catch:{ all -> 0x0199 }
            android.util.Pair r6 = (android.util.Pair) r6     // Catch:{ all -> 0x0199 }
            java.lang.Object r6 = r6.second     // Catch:{ all -> 0x0199 }
            boolean r5 = r5.equals(r6)     // Catch:{ all -> 0x0199 }
            if (r5 == 0) goto L_0x0196
            java.lang.Object r5 = r4.second     // Catch:{ all -> 0x0199 }
            java.lang.Integer r5 = (java.lang.Integer) r5     // Catch:{ all -> 0x0199 }
            int r5 = r5.intValue()     // Catch:{ all -> 0x0199 }
            com.android.server.accounts.AccountManagerService$MessageHandler r6 = r7.mHandler     // Catch:{ all -> 0x0199 }
            com.android.server.accounts.-$$Lambda$AccountManagerService$lqbNdAUKUSipmpqby9oIO8JlNTQ r11 = new com.android.server.accounts.-$$Lambda$AccountManagerService$lqbNdAUKUSipmpqby9oIO8JlNTQ     // Catch:{ all -> 0x0199 }
            r11.<init>(r9, r5)     // Catch:{ all -> 0x0199 }
            r6.post(r11)     // Catch:{ all -> 0x0199 }
        L_0x0196:
            goto L_0x015e
        L_0x0197:
            monitor-exit(r3)     // Catch:{ all -> 0x0199 }
            goto L_0x019c
        L_0x0199:
            r0 = move-exception
            monitor-exit(r3)     // Catch:{ all -> 0x0199 }
            throw r0
        L_0x019c:
            return r15
        L_0x019d:
            r0 = move-exception
            r13 = r21
        L_0x01a0:
            android.os.Binder.restoreCallingIdentity(r1)
            throw r0
        L_0x01a4:
            r0 = move-exception
            r13 = r21
            r1 = r15
            goto L_0x01c1
        L_0x01a9:
            r0 = move-exception
            r13 = r21
            goto L_0x01bb
        L_0x01ad:
            r0 = move-exception
            r16 = r4
            goto L_0x01b4
        L_0x01b1:
            r0 = move-exception
            r15 = r1
            r4 = r2
        L_0x01b4:
            com.android.server.accounts.AccountsDb r1 = r8.accountsDb     // Catch:{ all -> 0x01ba }
            r1.endTransaction()     // Catch:{ all -> 0x01ba }
            throw r0     // Catch:{ all -> 0x01ba }
        L_0x01ba:
            r0 = move-exception
        L_0x01bb:
            r1 = r15
            goto L_0x01be
        L_0x01bd:
            r0 = move-exception
        L_0x01be:
            monitor-exit(r12)     // Catch:{ all -> 0x01bd }
            throw r0     // Catch:{ all -> 0x01c0 }
        L_0x01c0:
            r0 = move-exception
        L_0x01c1:
            monitor-exit(r11)     // Catch:{ all -> 0x01c0 }
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.accounts.AccountManagerService.removeAccountInternal(com.android.server.accounts.AccountManagerService$UserAccounts, android.accounts.Account, int):boolean");
    }

    public /* synthetic */ void lambda$removeAccountInternal$2$AccountManagerService(Account account, int uid) {
        cancelAccountAccessRequestNotificationIfNeeded(account, uid, false);
    }

    /* Debug info: failed to restart local var, previous not found, register: 13 */
    public void invalidateAuthToken(String accountType, String authToken) {
        int callerUid = Binder.getCallingUid();
        Preconditions.checkNotNull(accountType, "accountType cannot be null");
        Preconditions.checkNotNull(authToken, "authToken cannot be null");
        if (Log.isLoggable(TAG, 2)) {
            Log.v(TAG, "invalidateAuthToken: accountType " + accountType + ", caller's uid " + callerUid + ", pid " + Binder.getCallingPid());
        }
        int userId = UserHandle.getCallingUserId();
        long identityToken = clearCallingIdentity();
        try {
            UserAccounts accounts = getUserAccounts(userId);
            synchronized (accounts.dbLock) {
                accounts.accountsDb.beginTransaction();
                try {
                    List<Pair<Account, String>> deletedTokens = invalidateAuthTokenLocked(accounts, accountType, authToken);
                    accounts.accountsDb.setTransactionSuccessful();
                    accounts.accountsDb.endTransaction();
                    synchronized (accounts.cacheLock) {
                        for (Pair<Account, String> tokenInfo : deletedTokens) {
                            writeAuthTokenIntoCacheLocked(accounts, (Account) tokenInfo.first, (String) tokenInfo.second, (String) null);
                        }
                        accounts.accountTokenCaches.remove(accountType, authToken);
                    }
                } catch (Throwable th) {
                    accounts.accountsDb.endTransaction();
                    throw th;
                }
            }
        } finally {
            restoreCallingIdentity(identityToken);
        }
    }

    private List<Pair<Account, String>> invalidateAuthTokenLocked(UserAccounts accounts, String accountType, String authToken) {
        List<Pair<Account, String>> results = new ArrayList<>();
        Cursor cursor = accounts.accountsDb.findAuthtokenForAllAccounts(accountType, authToken);
        while (cursor.moveToNext()) {
            try {
                String authTokenId = cursor.getString(0);
                String accountName = cursor.getString(1);
                String authTokenType = cursor.getString(2);
                accounts.accountsDb.deleteAuthToken(authTokenId);
                results.add(Pair.create(new Account(accountName, accountType), authTokenType));
            } finally {
                cursor.close();
            }
        }
        return results;
    }

    /* access modifiers changed from: private */
    public void saveCachedToken(UserAccounts accounts, Account account, String callerPkg, byte[] callerSigDigest, String tokenType, String token, long expiryMillis) {
        if (account == null || tokenType == null || callerPkg == null) {
            UserAccounts userAccounts = accounts;
        } else if (callerSigDigest == null) {
            UserAccounts userAccounts2 = accounts;
        } else {
            cancelNotification(getSigninRequiredNotificationId(accounts, account), UserHandle.of(accounts.userId));
            synchronized (accounts.cacheLock) {
                accounts.accountTokenCaches.put(account, token, tokenType, callerPkg, callerSigDigest, expiryMillis);
            }
        }
    }

    /* Debug info: failed to restart local var, previous not found, register: 9 */
    /* access modifiers changed from: private */
    /* JADX WARNING: Code restructure failed: missing block: B:26:0x003f, code lost:
        return false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:46:0x0069, code lost:
        return true;
     */
    /* JADX WARNING: Unknown top exception splitter block from list: {B:71:0x0092=Splitter:B:71:0x0092, B:23:0x003d=Splitter:B:23:0x003d, B:57:0x007d=Splitter:B:57:0x007d} */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean saveAuthTokenToDatabase(com.android.server.accounts.AccountManagerService.UserAccounts r10, android.accounts.Account r11, java.lang.String r12, java.lang.String r13) {
        /*
            r9 = this;
            r0 = 0
            if (r11 == 0) goto L_0x0096
            if (r12 != 0) goto L_0x0007
            goto L_0x0096
        L_0x0007:
            com.android.server.accounts.AccountManagerService$NotificationId r1 = r9.getSigninRequiredNotificationId(r10, r11)
            int r2 = r10.userId
            android.os.UserHandle r2 = android.os.UserHandle.of(r2)
            r9.cancelNotification(r1, r2)
            java.lang.Object r1 = r10.dbLock
            monitor-enter(r1)
            com.android.server.accounts.AccountsDb r2 = r10.accountsDb     // Catch:{ all -> 0x0093 }
            r2.beginTransaction()     // Catch:{ all -> 0x0093 }
            r2 = 0
            com.android.server.accounts.AccountsDb r3 = r10.accountsDb     // Catch:{ all -> 0x007f }
            long r3 = r3.findDeAccountId(r11)     // Catch:{ all -> 0x007f }
            r5 = 0
            int r7 = (r3 > r5 ? 1 : (r3 == r5 ? 0 : -1))
            if (r7 >= 0) goto L_0x0040
            com.android.server.accounts.AccountsDb r5 = r10.accountsDb     // Catch:{ all -> 0x0093 }
            r5.endTransaction()     // Catch:{ all -> 0x0093 }
            if (r2 == 0) goto L_0x003e
            java.lang.Object r5 = r10.cacheLock     // Catch:{ all -> 0x0093 }
            monitor-enter(r5)     // Catch:{ all -> 0x0093 }
            r9.writeAuthTokenIntoCacheLocked(r10, r11, r12, r13)     // Catch:{ all -> 0x003b }
            monitor-exit(r5)     // Catch:{ all -> 0x003b }
            goto L_0x003e
        L_0x003b:
            r0 = move-exception
            monitor-exit(r5)     // Catch:{ all -> 0x003b }
        L_0x003d:
            throw r0     // Catch:{ all -> 0x0093 }
        L_0x003e:
            monitor-exit(r1)     // Catch:{ all -> 0x0093 }
            return r0
        L_0x0040:
            com.android.server.accounts.AccountsDb r7 = r10.accountsDb     // Catch:{ all -> 0x007f }
            r7.deleteAuthtokensByAccountIdAndType(r3, r12)     // Catch:{ all -> 0x007f }
            com.android.server.accounts.AccountsDb r7 = r10.accountsDb     // Catch:{ all -> 0x007f }
            long r7 = r7.insertAuthToken(r3, r12, r13)     // Catch:{ all -> 0x007f }
            int r5 = (r7 > r5 ? 1 : (r7 == r5 ? 0 : -1))
            if (r5 < 0) goto L_0x006a
            com.android.server.accounts.AccountsDb r0 = r10.accountsDb     // Catch:{ all -> 0x007f }
            r0.setTransactionSuccessful()     // Catch:{ all -> 0x007f }
            r0 = 1
            r2 = 1
            com.android.server.accounts.AccountsDb r5 = r10.accountsDb     // Catch:{ all -> 0x0093 }
            r5.endTransaction()     // Catch:{ all -> 0x0093 }
            if (r0 == 0) goto L_0x0068
            java.lang.Object r5 = r10.cacheLock     // Catch:{ all -> 0x0093 }
            monitor-enter(r5)     // Catch:{ all -> 0x0093 }
            r9.writeAuthTokenIntoCacheLocked(r10, r11, r12, r13)     // Catch:{ all -> 0x0065 }
            monitor-exit(r5)     // Catch:{ all -> 0x0065 }
            goto L_0x0068
        L_0x0065:
            r2 = move-exception
            monitor-exit(r5)     // Catch:{ all -> 0x0065 }
            throw r2     // Catch:{ all -> 0x0093 }
        L_0x0068:
            monitor-exit(r1)     // Catch:{ all -> 0x0093 }
            return r2
        L_0x006a:
            com.android.server.accounts.AccountsDb r5 = r10.accountsDb     // Catch:{ all -> 0x0093 }
            r5.endTransaction()     // Catch:{ all -> 0x0093 }
            if (r2 == 0) goto L_0x007d
            java.lang.Object r5 = r10.cacheLock     // Catch:{ all -> 0x0093 }
            monitor-enter(r5)     // Catch:{ all -> 0x0093 }
            r9.writeAuthTokenIntoCacheLocked(r10, r11, r12, r13)     // Catch:{ all -> 0x007a }
            monitor-exit(r5)     // Catch:{ all -> 0x007a }
            goto L_0x007d
        L_0x007a:
            r0 = move-exception
            monitor-exit(r5)     // Catch:{ all -> 0x007a }
            goto L_0x003d
        L_0x007d:
            monitor-exit(r1)     // Catch:{ all -> 0x0093 }
            return r0
        L_0x007f:
            r0 = move-exception
            com.android.server.accounts.AccountsDb r3 = r10.accountsDb     // Catch:{ all -> 0x0093 }
            r3.endTransaction()     // Catch:{ all -> 0x0093 }
            if (r2 == 0) goto L_0x0091
            java.lang.Object r3 = r10.cacheLock     // Catch:{ all -> 0x0093 }
            monitor-enter(r3)     // Catch:{ all -> 0x0093 }
            r9.writeAuthTokenIntoCacheLocked(r10, r11, r12, r13)     // Catch:{ all -> 0x008f }
            monitor-exit(r3)     // Catch:{ all -> 0x008f }
            goto L_0x0091
        L_0x008f:
            r0 = move-exception
            monitor-exit(r3)     // Catch:{ all -> 0x008f }
        L_0x0091:
            throw r0     // Catch:{ all -> 0x0093 }
        L_0x0093:
            r0 = move-exception
            monitor-exit(r1)     // Catch:{ all -> 0x0093 }
            throw r0
        L_0x0096:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.accounts.AccountManagerService.saveAuthTokenToDatabase(com.android.server.accounts.AccountManagerService$UserAccounts, android.accounts.Account, java.lang.String, java.lang.String):boolean");
    }

    public String peekAuthToken(Account account, String authTokenType) {
        int callingUid = Binder.getCallingUid();
        if (Log.isLoggable(TAG, 2)) {
            Log.v(TAG, "peekAuthToken: " + account + ", authTokenType " + authTokenType + ", caller's uid " + callingUid + ", pid " + Binder.getCallingPid());
        }
        Preconditions.checkNotNull(account, "account cannot be null");
        Preconditions.checkNotNull(authTokenType, "authTokenType cannot be null");
        int userId = UserHandle.getCallingUserId();
        if (!isAccountManagedByCaller(account.type, callingUid, userId)) {
            throw new SecurityException(String.format("uid %s cannot peek the authtokens associated with accounts of type: %s", new Object[]{Integer.valueOf(callingUid), account.type}));
        } else if (!isLocalUnlockedUser(userId)) {
            Log.w(TAG, "Authtoken not available - user " + userId + " data is locked. callingUid " + callingUid);
            return null;
        } else {
            long identityToken = clearCallingIdentity();
            try {
                return readAuthTokenInternal(getUserAccounts(userId), account, authTokenType);
            } finally {
                restoreCallingIdentity(identityToken);
            }
        }
    }

    public void setAuthToken(Account account, String authTokenType, String authToken) {
        int callingUid = Binder.getCallingUid();
        if (Log.isLoggable(TAG, 2)) {
            Log.v(TAG, "setAuthToken: " + account + ", authTokenType " + authTokenType + ", caller's uid " + callingUid + ", pid " + Binder.getCallingPid());
        }
        Preconditions.checkNotNull(account, "account cannot be null");
        Preconditions.checkNotNull(authTokenType, "authTokenType cannot be null");
        int userId = UserHandle.getCallingUserId();
        if (isAccountManagedByCaller(account.type, callingUid, userId)) {
            long identityToken = clearCallingIdentity();
            try {
                saveAuthTokenToDatabase(getUserAccounts(userId), account, authTokenType, authToken);
            } finally {
                restoreCallingIdentity(identityToken);
            }
        } else {
            throw new SecurityException(String.format("uid %s cannot set auth tokens associated with accounts of type: %s", new Object[]{Integer.valueOf(callingUid), account.type}));
        }
    }

    public void setPassword(Account account, String password) {
        SeempLog.record(18);
        int callingUid = Binder.getCallingUid();
        if (Log.isLoggable(TAG, 2)) {
            Log.v(TAG, "setAuthToken: " + account + ", caller's uid " + callingUid + ", pid " + Binder.getCallingPid());
        }
        Preconditions.checkNotNull(account, "account cannot be null");
        int userId = UserHandle.getCallingUserId();
        if (isAccountManagedByCaller(account.type, callingUid, userId)) {
            long identityToken = clearCallingIdentity();
            try {
                setPasswordInternal(getUserAccounts(userId), account, password, callingUid);
            } finally {
                restoreCallingIdentity(identityToken);
            }
        } else {
            throw new SecurityException(String.format("uid %s cannot set secrets for accounts of type: %s", new Object[]{Integer.valueOf(callingUid), account.type}));
        }
    }

    /* Debug info: failed to restart local var, previous not found, register: 17 */
    /* JADX WARNING: Removed duplicated region for block: B:38:0x0083  */
    /* JADX WARNING: Unknown top exception splitter block from list: {B:26:0x0067=Splitter:B:26:0x0067, B:35:0x007c=Splitter:B:35:0x007c} */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void setPasswordInternal(com.android.server.accounts.AccountManagerService.UserAccounts r18, android.accounts.Account r19, java.lang.String r20, int r21) {
        /*
            r17 = this;
            r8 = r17
            r9 = r18
            r10 = r19
            r11 = r20
            if (r10 != 0) goto L_0x000b
            return
        L_0x000b:
            r1 = 0
            java.lang.Object r12 = r9.dbLock
            monitor-enter(r12)
            java.lang.Object r13 = r9.cacheLock     // Catch:{ all -> 0x0092 }
            monitor-enter(r13)     // Catch:{ all -> 0x0092 }
            com.android.server.accounts.AccountsDb r0 = r9.accountsDb     // Catch:{ all -> 0x008f }
            r0.beginTransaction()     // Catch:{ all -> 0x008f }
            com.android.server.accounts.AccountsDb r0 = r9.accountsDb     // Catch:{ all -> 0x007b }
            long r2 = r0.findDeAccountId(r10)     // Catch:{ all -> 0x007b }
            r14 = r2
            r2 = 0
            int r0 = (r14 > r2 ? 1 : (r14 == r2 ? 0 : -1))
            if (r0 < 0) goto L_0x0067
            com.android.server.accounts.AccountsDb r0 = r9.accountsDb     // Catch:{ all -> 0x007b }
            r0.updateCeAccountPassword(r14, r11)     // Catch:{ all -> 0x007b }
            com.android.server.accounts.AccountsDb r0 = r9.accountsDb     // Catch:{ all -> 0x007b }
            r0.deleteAuthTokensByAccountId(r14)     // Catch:{ all -> 0x007b }
            java.util.Map r0 = r18.authTokenCache     // Catch:{ all -> 0x007b }
            r0.remove(r10)     // Catch:{ all -> 0x007b }
            com.android.server.accounts.TokenCache r0 = r18.accountTokenCaches     // Catch:{ all -> 0x007b }
            r0.remove(r10)     // Catch:{ all -> 0x007b }
            com.android.server.accounts.AccountsDb r0 = r9.accountsDb     // Catch:{ all -> 0x007b }
            r0.setTransactionSuccessful()     // Catch:{ all -> 0x007b }
            r16 = 1
            if (r11 == 0) goto L_0x0050
            int r0 = r20.length()     // Catch:{ all -> 0x0063 }
            if (r0 != 0) goto L_0x004c
            goto L_0x0050
        L_0x004c:
            java.lang.String r0 = com.android.server.accounts.AccountsDb.DEBUG_ACTION_SET_PASSWORD     // Catch:{ all -> 0x0063 }
            r2 = r0
            goto L_0x0053
        L_0x0050:
            java.lang.String r0 = com.android.server.accounts.AccountsDb.DEBUG_ACTION_CLEAR_PASSWORD     // Catch:{ all -> 0x0063 }
            r2 = r0
        L_0x0053:
            java.lang.String r3 = "accounts"
            r1 = r17
            r4 = r14
            r6 = r18
            r7 = r21
            r1.logRecord(r2, r3, r4, r6, r7)     // Catch:{ all -> 0x0063 }
            r1 = r16
            goto L_0x0067
        L_0x0063:
            r0 = move-exception
            r1 = r16
            goto L_0x007c
        L_0x0067:
            com.android.server.accounts.AccountsDb r0 = r9.accountsDb     // Catch:{ all -> 0x008f }
            r0.endTransaction()     // Catch:{ all -> 0x008f }
            if (r1 == 0) goto L_0x0078
            r8.sendNotificationAccountUpdated(r10, r9)     // Catch:{ all -> 0x008f }
            int r0 = r18.userId     // Catch:{ all -> 0x008f }
            r8.sendAccountsChangedBroadcast(r0)     // Catch:{ all -> 0x008f }
        L_0x0078:
            monitor-exit(r13)     // Catch:{ all -> 0x008f }
            monitor-exit(r12)     // Catch:{ all -> 0x0092 }
            return
        L_0x007b:
            r0 = move-exception
        L_0x007c:
            com.android.server.accounts.AccountsDb r2 = r9.accountsDb     // Catch:{ all -> 0x008f }
            r2.endTransaction()     // Catch:{ all -> 0x008f }
            if (r1 == 0) goto L_0x008d
            r8.sendNotificationAccountUpdated(r10, r9)     // Catch:{ all -> 0x008f }
            int r2 = r18.userId     // Catch:{ all -> 0x008f }
            r8.sendAccountsChangedBroadcast(r2)     // Catch:{ all -> 0x008f }
        L_0x008d:
            throw r0     // Catch:{ all -> 0x008f }
        L_0x008f:
            r0 = move-exception
            monitor-exit(r13)     // Catch:{ all -> 0x008f }
            throw r0     // Catch:{ all -> 0x0092 }
        L_0x0092:
            r0 = move-exception
            monitor-exit(r12)     // Catch:{ all -> 0x0092 }
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.accounts.AccountManagerService.setPasswordInternal(com.android.server.accounts.AccountManagerService$UserAccounts, android.accounts.Account, java.lang.String, int):void");
    }

    public void clearPassword(Account account) {
        SeempLog.record(19);
        int callingUid = Binder.getCallingUid();
        if (Log.isLoggable(TAG, 2)) {
            Log.v(TAG, "clearPassword: " + account + ", caller's uid " + callingUid + ", pid " + Binder.getCallingPid());
        }
        Preconditions.checkNotNull(account, "account cannot be null");
        int userId = UserHandle.getCallingUserId();
        if (isAccountManagedByCaller(account.type, callingUid, userId)) {
            long identityToken = clearCallingIdentity();
            try {
                setPasswordInternal(getUserAccounts(userId), account, (String) null, callingUid);
            } finally {
                restoreCallingIdentity(identityToken);
            }
        } else {
            throw new SecurityException(String.format("uid %s cannot clear passwords for accounts of type: %s", new Object[]{Integer.valueOf(callingUid), account.type}));
        }
    }

    public void setUserData(Account account, String key, String value) {
        SeempLog.record(20);
        int callingUid = Binder.getCallingUid();
        if (Log.isLoggable(TAG, 2)) {
            Log.v(TAG, "setUserData: " + account + ", key " + key + ", caller's uid " + callingUid + ", pid " + Binder.getCallingPid());
        }
        if (key == null) {
            throw new IllegalArgumentException("key is null");
        } else if (account != null) {
            int userId = UserHandle.getCallingUserId();
            if (isAccountManagedByCaller(account.type, callingUid, userId)) {
                long identityToken = clearCallingIdentity();
                try {
                    UserAccounts accounts = getUserAccounts(userId);
                    if (accountExistsCache(accounts, account)) {
                        setUserdataInternal(accounts, account, key, value);
                        restoreCallingIdentity(identityToken);
                    }
                } finally {
                    restoreCallingIdentity(identityToken);
                }
            } else {
                throw new SecurityException(String.format("uid %s cannot set user data for accounts of type: %s", new Object[]{Integer.valueOf(callingUid), account.type}));
            }
        } else {
            throw new IllegalArgumentException("account is null");
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:13:0x002f, code lost:
        return false;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private boolean accountExistsCache(com.android.server.accounts.AccountManagerService.UserAccounts r9, android.accounts.Account r10) {
        /*
            r8 = this;
            java.lang.Object r0 = r9.cacheLock
            monitor-enter(r0)
            java.util.HashMap<java.lang.String, android.accounts.Account[]> r1 = r9.accountCache     // Catch:{ all -> 0x0030 }
            java.lang.String r2 = r10.type     // Catch:{ all -> 0x0030 }
            boolean r1 = r1.containsKey(r2)     // Catch:{ all -> 0x0030 }
            r2 = 0
            if (r1 == 0) goto L_0x002e
            java.util.HashMap<java.lang.String, android.accounts.Account[]> r1 = r9.accountCache     // Catch:{ all -> 0x0030 }
            java.lang.String r3 = r10.type     // Catch:{ all -> 0x0030 }
            java.lang.Object r1 = r1.get(r3)     // Catch:{ all -> 0x0030 }
            android.accounts.Account[] r1 = (android.accounts.Account[]) r1     // Catch:{ all -> 0x0030 }
            int r3 = r1.length     // Catch:{ all -> 0x0030 }
            r4 = r2
        L_0x001a:
            if (r4 >= r3) goto L_0x002e
            r5 = r1[r4]     // Catch:{ all -> 0x0030 }
            java.lang.String r6 = r5.name     // Catch:{ all -> 0x0030 }
            java.lang.String r7 = r10.name     // Catch:{ all -> 0x0030 }
            boolean r6 = r6.equals(r7)     // Catch:{ all -> 0x0030 }
            if (r6 == 0) goto L_0x002b
            monitor-exit(r0)     // Catch:{ all -> 0x0030 }
            r0 = 1
            return r0
        L_0x002b:
            int r4 = r4 + 1
            goto L_0x001a
        L_0x002e:
            monitor-exit(r0)     // Catch:{ all -> 0x0030 }
            return r2
        L_0x0030:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x0030 }
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.accounts.AccountManagerService.accountExistsCache(com.android.server.accounts.AccountManagerService$UserAccounts, android.accounts.Account):boolean");
    }

    /* Debug info: failed to restart local var, previous not found, register: 9 */
    private void setUserdataInternal(UserAccounts accounts, Account account, String key, String value) {
        synchronized (accounts.dbLock) {
            accounts.accountsDb.beginTransaction();
            try {
                long accountId = accounts.accountsDb.findDeAccountId(account);
                if (accountId < 0) {
                    accounts.accountsDb.endTransaction();
                    return;
                }
                long extrasId = accounts.accountsDb.findExtrasIdByAccountId(accountId, key);
                if (extrasId < 0) {
                    if (accounts.accountsDb.insertExtra(accountId, key, value) < 0) {
                        accounts.accountsDb.endTransaction();
                        return;
                    }
                } else if (!accounts.accountsDb.updateExtra(extrasId, value)) {
                    accounts.accountsDb.endTransaction();
                    return;
                }
                accounts.accountsDb.setTransactionSuccessful();
                accounts.accountsDb.endTransaction();
                synchronized (accounts.cacheLock) {
                    writeUserDataIntoCacheLocked(accounts, account, key, value);
                }
            } catch (Throwable th) {
                accounts.accountsDb.endTransaction();
                throw th;
            }
        }
    }

    private void onResult(IAccountManagerResponse response, Bundle result) {
        if (result == null) {
            Log.e(TAG, "the result is unexpectedly null", new Exception());
        }
        if (Log.isLoggable(TAG, 2)) {
            Log.v(TAG, getClass().getSimpleName() + " calling onResult() on response " + response);
        }
        try {
            response.onResult(result);
        } catch (RemoteException e) {
            if (Log.isLoggable(TAG, 2)) {
                Log.v(TAG, "failure while notifying response", e);
            }
        }
    }

    public void getAuthTokenLabel(IAccountManagerResponse response, String accountType, String authTokenType) throws RemoteException {
        boolean z = true;
        Preconditions.checkArgument(accountType != null, "accountType cannot be null");
        if (authTokenType == null) {
            z = false;
        }
        Preconditions.checkArgument(z, "authTokenType cannot be null");
        int callingUid = getCallingUid();
        clearCallingIdentity();
        if (UserHandle.getAppId(callingUid) == 1000) {
            int userId = UserHandle.getUserId(callingUid);
            long identityToken = clearCallingIdentity();
            try {
                final String str = accountType;
                final String str2 = authTokenType;
                new Session(getUserAccounts(userId), response, accountType, false, false, (String) null, false) {
                    /* access modifiers changed from: protected */
                    public String toDebugString(long now) {
                        return super.toDebugString(now) + ", getAuthTokenLabel, " + str + ", authTokenType " + str2;
                    }

                    public void run() throws RemoteException {
                        this.mAuthenticator.getAuthTokenLabel(this, str2);
                    }

                    public void onResult(Bundle result) {
                        Bundle.setDefusable(result, true);
                        if (result != null) {
                            String label = result.getString("authTokenLabelKey");
                            Bundle bundle = new Bundle();
                            bundle.putString("authTokenLabelKey", label);
                            super.onResult(bundle);
                            return;
                        }
                        super.onResult(result);
                    }
                }.bind();
            } finally {
                restoreCallingIdentity(identityToken);
            }
        } else {
            throw new SecurityException("can only call from system");
        }
    }

    public void getAuthToken(IAccountManagerResponse response, Account account, String authTokenType, boolean notifyOnAuthFailure, boolean expectActivityLaunch, Bundle loginOptions) {
        RegisteredServicesCache.ServiceInfo<AuthenticatorDescription> authenticatorInfo;
        int callerUid;
        UserAccounts accounts;
        int callerUid2;
        AnonymousClass8 r7;
        final Bundle bundle;
        final Account account2;
        final String str;
        final boolean z;
        final boolean z2;
        final int i;
        final boolean z3;
        final String str2;
        final byte[] bArr;
        final UserAccounts userAccounts;
        IAccountManagerResponse iAccountManagerResponse = response;
        Account account3 = account;
        String str3 = authTokenType;
        boolean z4 = notifyOnAuthFailure;
        Bundle bundle2 = loginOptions;
        Bundle.setDefusable(bundle2, true);
        if (Log.isLoggable(TAG, 2)) {
            Log.v(TAG, "getAuthToken: " + account3 + ", response " + iAccountManagerResponse + ", authTokenType " + str3 + ", notifyOnAuthFailure " + z4 + ", expectActivityLaunch " + expectActivityLaunch + ", caller's uid " + Binder.getCallingUid() + ", pid " + Binder.getCallingPid());
        } else {
            boolean z5 = expectActivityLaunch;
        }
        Preconditions.checkArgument(iAccountManagerResponse != null, "response cannot be null");
        if (account3 == null) {
            try {
                Slog.w(TAG, "getAuthToken called with null account");
                iAccountManagerResponse.onError(7, "account is null");
            } catch (RemoteException e) {
                Slog.w(TAG, "Failed to report error back to the client." + e);
            }
        } else if (str3 == null) {
            Slog.w(TAG, "getAuthToken called with null authTokenType");
            iAccountManagerResponse.onError(7, "authTokenType is null");
        } else {
            RegisteredServicesCache.ServiceInfo<AuthenticatorDescription> userId = UserHandle.getCallingUserId();
            long ident = Binder.clearCallingIdentity();
            try {
                UserAccounts accounts2 = getUserAccounts(userId);
                userId = this.mAuthenticatorCache.getServiceInfo(AuthenticatorDescription.newKey(account3.type), accounts2.userId);
                boolean customTokens = authenticatorInfo != null && ((AuthenticatorDescription) authenticatorInfo.type).customTokens;
                int callerUid3 = Binder.getCallingUid();
                boolean permissionGranted = customTokens || permissionIsGranted(account3, str3, callerUid3, userId);
                String callerPkg = bundle2.getString("androidPackageName");
                long ident2 = Binder.clearCallingIdentity();
                try {
                    List<String> callerOwnedPackageNames = Arrays.asList(this.mPackageManager.getPackagesForUid(callerUid3));
                    Binder.restoreCallingIdentity(ident2);
                    if (callerPkg == null || !callerOwnedPackageNames.contains(callerPkg)) {
                        List<String> list = callerOwnedPackageNames;
                        UserAccounts userAccounts2 = accounts2;
                        int i2 = userId;
                        RegisteredServicesCache.ServiceInfo<AuthenticatorDescription> serviceInfo = authenticatorInfo;
                        throw new SecurityException(String.format("Uid %s is attempting to illegally masquerade as package %s!", new Object[]{Integer.valueOf(callerUid3), callerPkg}));
                    }
                    bundle2.putInt("callerUid", callerUid3);
                    bundle2.putInt("callerPid", Binder.getCallingPid());
                    if (z4) {
                        bundle2.putBoolean("notifyOnAuthFailure", true);
                    }
                    long identityToken = clearCallingIdentity();
                    try {
                        String callerPkg2 = callerPkg;
                        byte[] callerPkgSigDigest = calculatePackageSignatureDigest(callerPkg);
                        RegisteredServicesCache.ServiceInfo<AuthenticatorDescription> authenticatorInfo2 = authenticatorInfo;
                        if (customTokens || !permissionGranted) {
                            callerUid = callerUid3;
                        } else {
                            try {
                                callerUid = callerUid3;
                                String authToken = readAuthTokenInternal(accounts2, account3, str3);
                                if (authToken != null) {
                                    try {
                                        Bundle result = new Bundle();
                                        result.putString("authtoken", authToken);
                                        result.putString("authAccount", account3.name);
                                        result.putString("accountType", account3.type);
                                        onResult(iAccountManagerResponse, result);
                                        restoreCallingIdentity(identityToken);
                                        return;
                                    } catch (Throwable th) {
                                        th = th;
                                        List<String> list2 = callerOwnedPackageNames;
                                        UserAccounts userAccounts3 = accounts2;
                                        int i3 = userId;
                                        RegisteredServicesCache.ServiceInfo<AuthenticatorDescription> serviceInfo2 = authenticatorInfo2;
                                        int i4 = callerUid;
                                        restoreCallingIdentity(identityToken);
                                        throw th;
                                    }
                                }
                            } catch (Throwable th2) {
                                th = th2;
                                int i5 = callerUid3;
                                List<String> list3 = callerOwnedPackageNames;
                                UserAccounts userAccounts4 = accounts2;
                                int i6 = userId;
                                RegisteredServicesCache.ServiceInfo<AuthenticatorDescription> serviceInfo3 = authenticatorInfo2;
                                restoreCallingIdentity(identityToken);
                                throw th;
                            }
                        }
                        if (customTokens) {
                            callerUid2 = callerUid;
                            String str4 = "authAccount";
                            List<String> list4 = callerOwnedPackageNames;
                            accounts = accounts2;
                            int i7 = userId;
                            try {
                                String token = readCachedTokenInternal(accounts2, account, authTokenType, callerPkg2, callerPkgSigDigest);
                                if (token != null) {
                                    if (Log.isLoggable(TAG, 2)) {
                                        Log.v(TAG, "getAuthToken: cache hit ofr custom token authenticator.");
                                    }
                                    Bundle result2 = new Bundle();
                                    result2.putString("authtoken", token);
                                    result2.putString(str4, account3.name);
                                    result2.putString("accountType", account3.type);
                                    onResult(iAccountManagerResponse, result2);
                                    restoreCallingIdentity(identityToken);
                                    return;
                                }
                            } catch (Throwable th3) {
                                th = th3;
                                RegisteredServicesCache.ServiceInfo<AuthenticatorDescription> serviceInfo4 = authenticatorInfo2;
                                restoreCallingIdentity(identityToken);
                                throw th;
                            }
                        } else {
                            accounts = accounts2;
                            int i8 = userId;
                            callerUid2 = callerUid;
                        }
                        try {
                            RegisteredServicesCache.ServiceInfo<AuthenticatorDescription> serviceInfo5 = authenticatorInfo2;
                            bundle = loginOptions;
                            account2 = account;
                            str = authTokenType;
                            z = notifyOnAuthFailure;
                            z2 = permissionGranted;
                            i = callerUid2;
                            z3 = customTokens;
                            str2 = callerPkg2;
                            bArr = callerPkgSigDigest;
                            userAccounts = accounts;
                        } catch (Throwable th4) {
                            th = th4;
                            RegisteredServicesCache.ServiceInfo<AuthenticatorDescription> serviceInfo6 = authenticatorInfo2;
                            restoreCallingIdentity(identityToken);
                            throw th;
                        }
                        try {
                            r7 = new Session(this, accounts, response, account3.type, expectActivityLaunch, false, account3.name, false) {
                                final /* synthetic */ AccountManagerService this$0;

                                {
                                    this.this$0 = this$0;
                                }

                                /* access modifiers changed from: protected */
                                public String toDebugString(long now) {
                                    Bundle bundle = bundle;
                                    if (bundle != null) {
                                        bundle.keySet();
                                    }
                                    return super.toDebugString(now) + ", getAuthToken, " + account2.hideNameToString() + ", authTokenType " + str + ", loginOptions " + bundle + ", notifyOnAuthFailure " + z;
                                }

                                public void run() throws RemoteException {
                                    if (!z2) {
                                        this.mAuthenticator.getAuthTokenLabel(this, str);
                                    } else {
                                        this.mAuthenticator.getAuthToken(this, account2, str, bundle);
                                    }
                                }

                                public void onResult(Bundle result) {
                                    Bundle bundle = result;
                                    Bundle.setDefusable(bundle, true);
                                    if (bundle != null) {
                                        if (bundle.containsKey("authTokenLabelKey")) {
                                            Intent intent = this.this$0.newGrantCredentialsPermissionIntent(account2, (String) null, i, new AccountAuthenticatorResponse(this), str, true);
                                            Bundle bundle2 = new Bundle();
                                            bundle2.putParcelable("intent", intent);
                                            onResult(bundle2);
                                            return;
                                        }
                                        String authToken = bundle.getString("authtoken");
                                        if (authToken != null) {
                                            String name = bundle.getString("authAccount");
                                            String type = bundle.getString("accountType");
                                            if (TextUtils.isEmpty(type) || TextUtils.isEmpty(name)) {
                                                onError(5, "the type and name should not be empty");
                                                return;
                                            }
                                            Account resultAccount = new Account(name, type);
                                            if (!z3) {
                                                boolean unused = this.this$0.saveAuthTokenToDatabase(this.mAccounts, resultAccount, str, authToken);
                                            }
                                            long expiryMillis = bundle.getLong("android.accounts.expiry", 0);
                                            if (!z3) {
                                            } else if (expiryMillis > System.currentTimeMillis()) {
                                                Account account = resultAccount;
                                                this.this$0.saveCachedToken(this.mAccounts, account2, str2, bArr, str, authToken, expiryMillis);
                                            }
                                        }
                                        Intent intent2 = (Intent) bundle.getParcelable("intent");
                                        if (intent2 != null && z && !z3) {
                                            if (!checkKeyIntent(Binder.getCallingUid(), intent2)) {
                                                onError(5, "invalid intent in bundle returned");
                                                return;
                                            }
                                            this.this$0.doNotification(this.mAccounts, account2, bundle.getString("authFailedMessage"), intent2, PackageManagerService.PLATFORM_PACKAGE_NAME, userAccounts.userId);
                                        }
                                    }
                                    super.onResult(result);
                                }
                            };
                            r7.bind();
                            restoreCallingIdentity(identityToken);
                        } catch (Throwable th5) {
                            th = th5;
                            restoreCallingIdentity(identityToken);
                            throw th;
                        }
                    } catch (Throwable th6) {
                        th = th6;
                        int i9 = callerUid3;
                        List<String> list5 = callerOwnedPackageNames;
                        UserAccounts userAccounts5 = accounts2;
                        int i10 = userId;
                        String str5 = callerPkg;
                        RegisteredServicesCache.ServiceInfo<AuthenticatorDescription> serviceInfo7 = authenticatorInfo;
                        restoreCallingIdentity(identityToken);
                        throw th;
                    }
                } catch (Throwable th7) {
                    int i11 = callerUid3;
                    UserAccounts userAccounts6 = accounts2;
                    int i12 = userId;
                    String str6 = callerPkg;
                    RegisteredServicesCache.ServiceInfo<AuthenticatorDescription> serviceInfo8 = authenticatorInfo;
                    Binder.restoreCallingIdentity(ident2);
                    throw th7;
                }
            } finally {
                authenticatorInfo = userId;
                Binder.restoreCallingIdentity(ident);
            }
        }
    }

    private byte[] calculatePackageSignatureDigest(String callerPkg) {
        MessageDigest digester;
        try {
            digester = MessageDigest.getInstance("SHA-256");
            for (Signature sig : this.mPackageManager.getPackageInfo(callerPkg, 64).signatures) {
                digester.update(sig.toByteArray());
            }
        } catch (NoSuchAlgorithmException x) {
            Log.wtf(TAG, "SHA-256 should be available", x);
            digester = null;
        } catch (PackageManager.NameNotFoundException e) {
            Log.w(TAG, "Could not find packageinfo for: " + callerPkg);
            digester = null;
        }
        if (digester == null) {
            return null;
        }
        return digester.digest();
    }

    private void createNoCredentialsPermissionNotification(Account account, Intent intent, String packageName, int userId) {
        String subtitle;
        String title;
        Account account2 = account;
        Intent intent2 = intent;
        int uid = intent2.getIntExtra(WatchlistLoggingHandler.WatchlistEventKeys.UID, -1);
        String authTokenType = intent2.getStringExtra("authTokenType");
        String titleAndSubtitle = this.mContext.getString(17040786, new Object[]{account2.name});
        int index = titleAndSubtitle.indexOf(10);
        String title2 = titleAndSubtitle;
        if (index > 0) {
            title = titleAndSubtitle.substring(0, index);
            subtitle = titleAndSubtitle.substring(index + 1);
        } else {
            title = title2;
            subtitle = "";
        }
        UserHandle user = UserHandle.of(userId);
        Context contextForUser = getContextForUser(user);
        Context context = contextForUser;
        installNotification(getCredentialPermissionNotificationId(account2, authTokenType, uid), new Notification.Builder(contextForUser, SystemNotificationChannels.ACCOUNT).setSmallIcon(17301642).setWhen(0).setColor(contextForUser.getColor(17170460)).setContentTitle(title).setContentText(subtitle).setContentIntent(PendingIntent.getActivityAsUser(this.mContext, 0, intent, 268435456, (Bundle) null, user)).build(), packageName, user.getIdentifier());
    }

    /* access modifiers changed from: private */
    public Intent newGrantCredentialsPermissionIntent(Account account, String packageName, int uid, AccountAuthenticatorResponse response, String authTokenType, boolean startInNewTask) {
        Intent intent = new Intent(this.mContext, GrantCredentialsPermissionActivity.class);
        if (startInNewTask) {
            intent.setFlags(268435456);
        }
        StringBuilder sb = new StringBuilder();
        sb.append(getCredentialPermissionNotificationId(account, authTokenType, uid).mTag);
        sb.append(packageName != null ? packageName : "");
        intent.addCategory(sb.toString());
        intent.putExtra("account", account);
        intent.putExtra("authTokenType", authTokenType);
        intent.putExtra("response", response);
        intent.putExtra(WatchlistLoggingHandler.WatchlistEventKeys.UID, uid);
        return intent;
    }

    /* access modifiers changed from: private */
    public NotificationId getCredentialPermissionNotificationId(Account account, String authTokenType, int uid) {
        NotificationId nId;
        UserAccounts accounts = getUserAccounts(UserHandle.getUserId(uid));
        synchronized (accounts.credentialsPermissionNotificationIds) {
            Pair<Pair<Account, String>, Integer> key = new Pair<>(new Pair(account, authTokenType), Integer.valueOf(uid));
            nId = (NotificationId) accounts.credentialsPermissionNotificationIds.get(key);
            if (nId == null) {
                nId = new NotificationId("AccountManagerService:38:" + account.hashCode() + ":" + authTokenType.hashCode(), 38);
                accounts.credentialsPermissionNotificationIds.put(key, nId);
            }
        }
        return nId;
    }

    /* access modifiers changed from: private */
    public NotificationId getSigninRequiredNotificationId(UserAccounts accounts, Account account) {
        NotificationId nId;
        synchronized (accounts.signinRequiredNotificationIds) {
            nId = (NotificationId) accounts.signinRequiredNotificationIds.get(account);
            if (nId == null) {
                nId = new NotificationId("AccountManagerService:37:" + account.hashCode(), 37);
                accounts.signinRequiredNotificationIds.put(account, nId);
            }
        }
        return nId;
    }

    public void addAccount(IAccountManagerResponse response, String accountType, String authTokenType, String[] requiredFeatures, boolean expectActivityLaunch, Bundle optionsIn) {
        IAccountManagerResponse iAccountManagerResponse = response;
        String str = accountType;
        Bundle bundle = optionsIn;
        SeempLog.record(16);
        Bundle.setDefusable(bundle, true);
        if (Log.isLoggable(TAG, 2)) {
            Log.v(TAG, "addAccount: accountType " + str + ", response " + iAccountManagerResponse + ", authTokenType " + authTokenType + ", requiredFeatures " + Arrays.toString(requiredFeatures) + ", expectActivityLaunch " + expectActivityLaunch + ", caller's uid " + Binder.getCallingUid() + ", pid " + Binder.getCallingPid());
        } else {
            String str2 = authTokenType;
            boolean z = expectActivityLaunch;
        }
        if (iAccountManagerResponse == null) {
            throw new IllegalArgumentException("response is null");
        } else if (str != null) {
            int uid = Binder.getCallingUid();
            int userId = UserHandle.getUserId(uid);
            if (!canUserModifyAccounts(userId, uid)) {
                try {
                    iAccountManagerResponse.onError(100, "User is not allowed to add an account!");
                } catch (RemoteException e) {
                }
                showCantAddAccount(100, userId);
            } else if (!canUserModifyAccountsForType(userId, str, uid)) {
                try {
                    iAccountManagerResponse.onError(101, "User cannot modify accounts of this type (policy).");
                } catch (RemoteException e2) {
                }
                showCantAddAccount(101, userId);
            } else {
                int pid = Binder.getCallingPid();
                Bundle options = bundle == null ? new Bundle() : bundle;
                options.putInt("callerUid", uid);
                options.putInt("callerPid", pid);
                int usrId = UserHandle.getCallingUserId();
                long identityToken = clearCallingIdentity();
                try {
                    UserAccounts accounts = getUserAccounts(usrId);
                    logRecordWithUid(accounts, AccountsDb.DEBUG_ACTION_CALLED_ACCOUNT_ADD, "accounts", uid);
                    int i = usrId;
                    int i2 = pid;
                    int i3 = userId;
                    int i4 = uid;
                    final String str3 = authTokenType;
                    final String[] strArr = requiredFeatures;
                    final Bundle bundle2 = options;
                    final String str4 = accountType;
                    try {
                        AnonymousClass9 r1 = new Session(accounts, response, accountType, expectActivityLaunch, true, (String) null, false, true) {
                            public void run() throws RemoteException {
                                this.mAuthenticator.addAccount(this, this.mAccountType, str3, strArr, bundle2);
                            }

                            /* access modifiers changed from: protected */
                            public String toDebugString(long now) {
                                return super.toDebugString(now) + ", addAccount, accountType " + str4 + ", requiredFeatures " + Arrays.toString(strArr);
                            }
                        };
                        r1.bind();
                        restoreCallingIdentity(identityToken);
                    } catch (Throwable th) {
                        th = th;
                        restoreCallingIdentity(identityToken);
                        throw th;
                    }
                } catch (Throwable th2) {
                    th = th2;
                    int i5 = usrId;
                    Bundle bundle3 = options;
                    int i6 = pid;
                    int i7 = userId;
                    int i8 = uid;
                    restoreCallingIdentity(identityToken);
                    throw th;
                }
            }
        } else {
            throw new IllegalArgumentException("accountType is null");
        }
    }

    public void addAccountAsUser(IAccountManagerResponse response, String accountType, String authTokenType, String[] requiredFeatures, boolean expectActivityLaunch, Bundle optionsIn, int userId) {
        IAccountManagerResponse iAccountManagerResponse = response;
        String str = accountType;
        Bundle bundle = optionsIn;
        int i = userId;
        Bundle.setDefusable(bundle, true);
        int callingUid = Binder.getCallingUid();
        if (Log.isLoggable(TAG, 2)) {
            Log.v(TAG, "addAccount: accountType " + str + ", response " + iAccountManagerResponse + ", authTokenType " + authTokenType + ", requiredFeatures " + Arrays.toString(requiredFeatures) + ", expectActivityLaunch " + expectActivityLaunch + ", caller's uid " + Binder.getCallingUid() + ", pid " + Binder.getCallingPid() + ", for user id " + i);
        } else {
            String str2 = authTokenType;
            boolean z = expectActivityLaunch;
        }
        Preconditions.checkArgument(iAccountManagerResponse != null, "response cannot be null");
        Preconditions.checkArgument(str != null, "accountType cannot be null");
        if (isCrossUser(callingUid, i)) {
            throw new SecurityException(String.format("User %s trying to add account for %s", new Object[]{Integer.valueOf(UserHandle.getCallingUserId()), Integer.valueOf(userId)}));
        } else if (!canUserModifyAccounts(i, callingUid)) {
            try {
                iAccountManagerResponse.onError(100, "User is not allowed to add an account!");
            } catch (RemoteException e) {
            }
            showCantAddAccount(100, i);
        } else if (!canUserModifyAccountsForType(i, str, callingUid)) {
            try {
                iAccountManagerResponse.onError(101, "User cannot modify accounts of this type (policy).");
            } catch (RemoteException e2) {
            }
            showCantAddAccount(101, i);
        } else {
            int pid = Binder.getCallingPid();
            int uid = Binder.getCallingUid();
            Bundle options = bundle == null ? new Bundle() : bundle;
            options.putInt("callerUid", uid);
            options.putInt("callerPid", pid);
            long identityToken = clearCallingIdentity();
            try {
                UserAccounts accounts = getUserAccounts(i);
                logRecordWithUid(accounts, AccountsDb.DEBUG_ACTION_CALLED_ACCOUNT_ADD, "accounts", i);
                int i2 = uid;
                int i3 = pid;
                int i4 = callingUid;
                final String str3 = authTokenType;
                final String[] strArr = requiredFeatures;
                final Bundle bundle2 = options;
                final String str4 = accountType;
                try {
                    AnonymousClass10 r1 = new Session(accounts, response, accountType, expectActivityLaunch, true, (String) null, false, true) {
                        public void run() throws RemoteException {
                            this.mAuthenticator.addAccount(this, this.mAccountType, str3, strArr, bundle2);
                        }

                        /* access modifiers changed from: protected */
                        public String toDebugString(long now) {
                            String str;
                            StringBuilder sb = new StringBuilder();
                            sb.append(super.toDebugString(now));
                            sb.append(", addAccount, accountType ");
                            sb.append(str4);
                            sb.append(", requiredFeatures ");
                            String[] strArr = strArr;
                            if (strArr != null) {
                                str = TextUtils.join(",", strArr);
                            } else {
                                str = null;
                            }
                            sb.append(str);
                            return sb.toString();
                        }
                    };
                    r1.bind();
                    restoreCallingIdentity(identityToken);
                } catch (Throwable th) {
                    th = th;
                    restoreCallingIdentity(identityToken);
                    throw th;
                }
            } catch (Throwable th2) {
                th = th2;
                Bundle bundle3 = options;
                int i5 = uid;
                int i6 = pid;
                int i7 = callingUid;
                restoreCallingIdentity(identityToken);
                throw th;
            }
        }
    }

    public void startAddAccountSession(IAccountManagerResponse response, String accountType, String authTokenType, String[] requiredFeatures, boolean expectActivityLaunch, Bundle optionsIn) {
        IAccountManagerResponse iAccountManagerResponse = response;
        String str = accountType;
        Bundle bundle = optionsIn;
        boolean z = true;
        Bundle.setDefusable(bundle, true);
        if (Log.isLoggable(TAG, 2)) {
            Log.v(TAG, "startAddAccountSession: accountType " + str + ", response " + iAccountManagerResponse + ", authTokenType " + authTokenType + ", requiredFeatures " + Arrays.toString(requiredFeatures) + ", expectActivityLaunch " + expectActivityLaunch + ", caller's uid " + Binder.getCallingUid() + ", pid " + Binder.getCallingPid());
        } else {
            String str2 = authTokenType;
            boolean z2 = expectActivityLaunch;
        }
        Preconditions.checkArgument(iAccountManagerResponse != null, "response cannot be null");
        if (str == null) {
            z = false;
        }
        Preconditions.checkArgument(z, "accountType cannot be null");
        int uid = Binder.getCallingUid();
        int userId = UserHandle.getUserId(uid);
        if (!canUserModifyAccounts(userId, uid)) {
            try {
                iAccountManagerResponse.onError(100, "User is not allowed to add an account!");
            } catch (RemoteException e) {
            }
            showCantAddAccount(100, userId);
        } else if (!canUserModifyAccountsForType(userId, str, uid)) {
            try {
                iAccountManagerResponse.onError(101, "User cannot modify accounts of this type (policy).");
            } catch (RemoteException e2) {
            }
            showCantAddAccount(101, userId);
        } else {
            int pid = Binder.getCallingPid();
            Bundle options = bundle == null ? new Bundle() : bundle;
            options.putInt("callerUid", uid);
            options.putInt("callerPid", pid);
            String callerPkg = options.getString("androidPackageName");
            boolean isPasswordForwardingAllowed = checkPermissionAndNote(callerPkg, uid, "android.permission.GET_PASSWORD");
            long identityToken = clearCallingIdentity();
            try {
                UserAccounts accounts = getUserAccounts(userId);
                logRecordWithUid(accounts, AccountsDb.DEBUG_ACTION_CALLED_START_ACCOUNT_ADD, "accounts", uid);
                String str3 = callerPkg;
                int i = pid;
                int i2 = userId;
                int i3 = uid;
                final String str4 = authTokenType;
                final String[] strArr = requiredFeatures;
                final Bundle bundle2 = options;
                final String str5 = accountType;
                try {
                    AnonymousClass11 r1 = new StartAccountSession(accounts, response, accountType, expectActivityLaunch, (String) null, false, true, isPasswordForwardingAllowed) {
                        public void run() throws RemoteException {
                            this.mAuthenticator.startAddAccountSession(this, this.mAccountType, str4, strArr, bundle2);
                        }

                        /* access modifiers changed from: protected */
                        public String toDebugString(long now) {
                            String requiredFeaturesStr = TextUtils.join(",", strArr);
                            StringBuilder sb = new StringBuilder();
                            sb.append(super.toDebugString(now));
                            sb.append(", startAddAccountSession, accountType ");
                            sb.append(str5);
                            sb.append(", requiredFeatures ");
                            sb.append(strArr != null ? requiredFeaturesStr : null);
                            return sb.toString();
                        }
                    };
                    r1.bind();
                    restoreCallingIdentity(identityToken);
                } catch (Throwable th) {
                    th = th;
                    restoreCallingIdentity(identityToken);
                    throw th;
                }
            } catch (Throwable th2) {
                th = th2;
                String str6 = callerPkg;
                Bundle bundle3 = options;
                int i4 = pid;
                int i5 = userId;
                int i6 = uid;
                restoreCallingIdentity(identityToken);
                throw th;
            }
        }
    }

    private abstract class StartAccountSession extends Session {
        private final boolean mIsPasswordForwardingAllowed;
        final /* synthetic */ AccountManagerService this$0;

        /* JADX WARNING: Illegal instructions before constructor call */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public StartAccountSession(com.android.server.accounts.AccountManagerService r12, com.android.server.accounts.AccountManagerService.UserAccounts r13, android.accounts.IAccountManagerResponse r14, java.lang.String r15, boolean r16, java.lang.String r17, boolean r18, boolean r19, boolean r20) {
            /*
                r11 = this;
                r10 = r11
                r1 = r12
                r10.this$0 = r1
                r6 = 1
                r0 = r11
                r2 = r13
                r3 = r14
                r4 = r15
                r5 = r16
                r7 = r17
                r8 = r18
                r9 = r19
                r0.<init>(r2, r3, r4, r5, r6, r7, r8, r9)
                r0 = r20
                r10.mIsPasswordForwardingAllowed = r0
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.server.accounts.AccountManagerService.StartAccountSession.<init>(com.android.server.accounts.AccountManagerService, com.android.server.accounts.AccountManagerService$UserAccounts, android.accounts.IAccountManagerResponse, java.lang.String, boolean, java.lang.String, boolean, boolean, boolean):void");
        }

        public void onResult(Bundle result) {
            IAccountManagerResponse response;
            Bundle.setDefusable(result, true);
            this.mNumResults++;
            Intent intent = null;
            if (result != null) {
                Intent intent2 = (Intent) result.getParcelable("intent");
                intent = intent2;
                if (intent2 != null && !checkKeyIntent(Binder.getCallingUid(), intent)) {
                    onError(5, "invalid intent in bundle returned");
                    return;
                }
            }
            if (!this.mExpectActivityLaunch || result == null || !result.containsKey("intent")) {
                response = getResponseAndClose();
            } else {
                response = this.mResponse;
            }
            if (response != null) {
                if (result == null) {
                    if (Log.isLoggable(AccountManagerService.TAG, 2)) {
                        Log.v(AccountManagerService.TAG, getClass().getSimpleName() + " calling onError() on response " + response);
                    }
                    this.this$0.sendErrorResponse(response, 5, "null bundle returned");
                } else if (result.getInt("errorCode", -1) <= 0 || intent != null) {
                    if (!this.mIsPasswordForwardingAllowed) {
                        result.remove("password");
                    }
                    result.remove("authtoken");
                    if (Log.isLoggable(AccountManagerService.TAG, 2)) {
                        Log.v(AccountManagerService.TAG, getClass().getSimpleName() + " calling onResult() on response " + response);
                    }
                    Bundle sessionBundle = result.getBundle("accountSessionBundle");
                    if (sessionBundle != null) {
                        String accountType = sessionBundle.getString("accountType");
                        if (TextUtils.isEmpty(accountType) || !this.mAccountType.equalsIgnoreCase(accountType)) {
                            Log.w(AccountManagerService.TAG, "Account type in session bundle doesn't match request.");
                        }
                        sessionBundle.putString("accountType", this.mAccountType);
                        try {
                            result.putBundle("accountSessionBundle", CryptoHelper.getInstance().encryptBundle(sessionBundle));
                        } catch (GeneralSecurityException e) {
                            if (Log.isLoggable(AccountManagerService.TAG, 3)) {
                                Log.v(AccountManagerService.TAG, "Failed to encrypt session bundle!", e);
                            }
                            this.this$0.sendErrorResponse(response, 5, "failed to encrypt session bundle");
                            return;
                        }
                    }
                    this.this$0.sendResponse(response, result);
                } else {
                    this.this$0.sendErrorResponse(response, result.getInt("errorCode"), result.getString("errorMessage"));
                }
            }
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:62:0x013f  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void finishSessionAsUser(android.accounts.IAccountManagerResponse r26, android.os.Bundle r27, boolean r28, android.os.Bundle r29, int r30) {
        /*
            r25 = this;
            r13 = r25
            r14 = r26
            r15 = r27
            r12 = r29
            r11 = r30
            java.lang.String r1 = "failed to decrypt session bundle"
            r0 = 1
            android.os.Bundle.setDefusable(r15, r0)
            int r10 = android.os.Binder.getCallingUid()
            r2 = 2
            java.lang.String r3 = "AccountManagerService"
            boolean r4 = android.util.Log.isLoggable(r3, r2)
            if (r4 == 0) goto L_0x0064
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            java.lang.String r5 = "finishSession: response "
            r4.append(r5)
            r4.append(r14)
            java.lang.String r5 = ", expectActivityLaunch "
            r4.append(r5)
            r9 = r28
            r4.append(r9)
            java.lang.String r5 = ", caller's uid "
            r4.append(r5)
            r4.append(r10)
            java.lang.String r5 = ", caller's user id "
            r4.append(r5)
            int r5 = android.os.UserHandle.getCallingUserId()
            r4.append(r5)
            java.lang.String r5 = ", pid "
            r4.append(r5)
            int r5 = android.os.Binder.getCallingPid()
            r4.append(r5)
            java.lang.String r5 = ", for user id "
            r4.append(r5)
            r4.append(r11)
            java.lang.String r4 = r4.toString()
            android.util.Log.v(r3, r4)
            goto L_0x0066
        L_0x0064:
            r9 = r28
        L_0x0066:
            r4 = 0
            if (r14 == 0) goto L_0x006b
            r5 = r0
            goto L_0x006c
        L_0x006b:
            r5 = r4
        L_0x006c:
            java.lang.String r6 = "response cannot be null"
            com.android.internal.util.Preconditions.checkArgument(r5, r6)
            if (r15 == 0) goto L_0x0166
            int r5 = r27.size()
            if (r5 == 0) goto L_0x0166
            boolean r5 = r13.isCrossUser(r10, r11)
            if (r5 != 0) goto L_0x0148
            boolean r0 = r13.canUserModifyAccounts(r11, r10)
            if (r0 != 0) goto L_0x0091
            r0 = 100
            java.lang.String r1 = "User is not allowed to add an account!"
            r13.sendErrorResponse(r14, r0, r1)
            r13.showCantAddAccount(r0, r11)
            return
        L_0x0091:
            int r8 = android.os.Binder.getCallingPid()
            r2 = 8
            com.android.server.accounts.CryptoHelper r0 = com.android.server.accounts.CryptoHelper.getInstance()     // Catch:{ GeneralSecurityException -> 0x0133 }
            android.os.Bundle r4 = r0.decryptBundle(r15)     // Catch:{ GeneralSecurityException -> 0x0133 }
            r7 = r4
            if (r7 != 0) goto L_0x00ad
            r13.sendErrorResponse(r14, r2, r1)     // Catch:{ GeneralSecurityException -> 0x00a6 }
            return
        L_0x00a6:
            r0 = move-exception
            r19 = r8
            r20 = r10
            goto L_0x0138
        L_0x00ad:
            java.lang.String r4 = "accountType"
            java.lang.String r4 = r7.getString(r4)     // Catch:{ GeneralSecurityException -> 0x0133 }
            r6 = r4
            boolean r4 = android.text.TextUtils.isEmpty(r6)     // Catch:{ GeneralSecurityException -> 0x0133 }
            if (r4 == 0) goto L_0x00c1
            r4 = 7
            java.lang.String r5 = "accountType is empty"
            r13.sendErrorResponse(r14, r4, r5)     // Catch:{ GeneralSecurityException -> 0x00a6 }
            return
        L_0x00c1:
            if (r12 == 0) goto L_0x00c6
            r7.putAll(r12)     // Catch:{ GeneralSecurityException -> 0x00a6 }
        L_0x00c6:
            java.lang.String r4 = "callerUid"
            r7.putInt(r4, r10)     // Catch:{ GeneralSecurityException -> 0x0133 }
            java.lang.String r4 = "callerPid"
            r7.putInt(r4, r8)     // Catch:{ GeneralSecurityException -> 0x0133 }
            boolean r0 = r13.canUserModifyAccountsForType(r11, r6, r10)
            if (r0 != 0) goto L_0x00e2
            r0 = 101(0x65, float:1.42E-43)
            java.lang.String r1 = "User cannot modify accounts of this type (policy)."
            r13.sendErrorResponse(r14, r0, r1)
            r13.showCantAddAccount(r0, r11)
            return
        L_0x00e2:
            long r16 = clearCallingIdentity()
            com.android.server.accounts.AccountManagerService$UserAccounts r0 = r13.getUserAccounts(r11)     // Catch:{ all -> 0x0126 }
            java.lang.String r1 = com.android.server.accounts.AccountsDb.DEBUG_ACTION_CALLED_ACCOUNT_SESSION_FINISH     // Catch:{ all -> 0x0126 }
            java.lang.String r2 = "accounts"
            r13.logRecordWithUid(r0, r1, r2, r10)     // Catch:{ all -> 0x0126 }
            com.android.server.accounts.AccountManagerService$12 r18 = new com.android.server.accounts.AccountManagerService$12     // Catch:{ all -> 0x0126 }
            r19 = 1
            r20 = 0
            r21 = 0
            r22 = 1
            r1 = r18
            r2 = r25
            r3 = r0
            r4 = r26
            r5 = r6
            r23 = r6
            r6 = r28
            r24 = r7
            r7 = r19
            r19 = r8
            r8 = r20
            r9 = r21
            r20 = r10
            r10 = r22
            r11 = r24
            r12 = r23
            r1.<init>(r3, r4, r5, r6, r7, r8, r9, r10, r11, r12)     // Catch:{ all -> 0x0124 }
            r18.bind()     // Catch:{ all -> 0x0124 }
            restoreCallingIdentity(r16)
            return
        L_0x0124:
            r0 = move-exception
            goto L_0x012f
        L_0x0126:
            r0 = move-exception
            r23 = r6
            r24 = r7
            r19 = r8
            r20 = r10
        L_0x012f:
            restoreCallingIdentity(r16)
            throw r0
        L_0x0133:
            r0 = move-exception
            r19 = r8
            r20 = r10
        L_0x0138:
            r4 = 3
            boolean r4 = android.util.Log.isLoggable(r3, r4)
            if (r4 == 0) goto L_0x0144
            java.lang.String r4 = "Failed to decrypt session bundle!"
            android.util.Log.v(r3, r4, r0)
        L_0x0144:
            r13.sendErrorResponse(r14, r2, r1)
            return
        L_0x0148:
            java.lang.SecurityException r1 = new java.lang.SecurityException
            java.lang.Object[] r2 = new java.lang.Object[r2]
            int r3 = android.os.UserHandle.getCallingUserId()
            java.lang.Integer r3 = java.lang.Integer.valueOf(r3)
            r2[r4] = r3
            java.lang.Integer r3 = java.lang.Integer.valueOf(r30)
            r2[r0] = r3
            java.lang.String r0 = "User %s trying to finish session for %s without cross user permission"
            java.lang.String r0 = java.lang.String.format(r0, r2)
            r1.<init>(r0)
            throw r1
        L_0x0166:
            r20 = r10
            java.lang.IllegalArgumentException r0 = new java.lang.IllegalArgumentException
            java.lang.String r1 = "sessionBundle is empty"
            r0.<init>(r1)
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.accounts.AccountManagerService.finishSessionAsUser(android.accounts.IAccountManagerResponse, android.os.Bundle, boolean, android.os.Bundle, int):void");
    }

    private void showCantAddAccount(int errorCode, int userId) {
        DevicePolicyManagerInternal dpmi = (DevicePolicyManagerInternal) LocalServices.getService(DevicePolicyManagerInternal.class);
        Intent intent = null;
        if (dpmi == null) {
            intent = getDefaultCantAddAccountIntent(errorCode);
        } else if (errorCode == 100) {
            intent = dpmi.createUserRestrictionSupportIntent(userId, "no_modify_accounts");
        } else if (errorCode == 101) {
            intent = dpmi.createShowAdminSupportIntent(userId, false);
        }
        if (intent == null) {
            intent = getDefaultCantAddAccountIntent(errorCode);
        }
        long identityToken = clearCallingIdentity();
        try {
            this.mContext.startActivityAsUser(intent, new UserHandle(userId));
        } finally {
            restoreCallingIdentity(identityToken);
        }
    }

    private Intent getDefaultCantAddAccountIntent(int errorCode) {
        Intent cantAddAccount = new Intent(this.mContext, CantAddAccountActivity.class);
        cantAddAccount.putExtra("android.accounts.extra.ERROR_CODE", errorCode);
        cantAddAccount.addFlags(268435456);
        return cantAddAccount;
    }

    public void confirmCredentialsAsUser(IAccountManagerResponse response, Account account, Bundle options, boolean expectActivityLaunch, int userId) {
        IAccountManagerResponse iAccountManagerResponse = response;
        Account account2 = account;
        int i = userId;
        Bundle.setDefusable(options, true);
        int callingUid = Binder.getCallingUid();
        if (Log.isLoggable(TAG, 2)) {
            Log.v(TAG, "confirmCredentials: " + account2 + ", response " + iAccountManagerResponse + ", expectActivityLaunch " + expectActivityLaunch + ", caller's uid " + callingUid + ", pid " + Binder.getCallingPid());
        } else {
            boolean z = expectActivityLaunch;
        }
        if (isCrossUser(callingUid, i)) {
            throw new SecurityException(String.format("User %s trying to confirm account credentials for %s", new Object[]{Integer.valueOf(UserHandle.getCallingUserId()), Integer.valueOf(userId)}));
        } else if (iAccountManagerResponse == null) {
            throw new IllegalArgumentException("response is null");
        } else if (account2 != null) {
            long identityToken = clearCallingIdentity();
            try {
                UserAccounts accounts = getUserAccounts(i);
                int i2 = callingUid;
                final Account account3 = account;
                final Bundle bundle = options;
                try {
                    AnonymousClass13 r1 = new Session(accounts, response, account2.type, expectActivityLaunch, true, account2.name, true, true) {
                        public void run() throws RemoteException {
                            this.mAuthenticator.confirmCredentials(this, account3, bundle);
                        }

                        /* access modifiers changed from: protected */
                        public String toDebugString(long now) {
                            return super.toDebugString(now) + ", confirmCredentials, " + account3.toSafeString();
                        }
                    };
                    r1.bind();
                    restoreCallingIdentity(identityToken);
                } catch (Throwable th) {
                    th = th;
                    restoreCallingIdentity(identityToken);
                    throw th;
                }
            } catch (Throwable th2) {
                th = th2;
                int i3 = callingUid;
                restoreCallingIdentity(identityToken);
                throw th;
            }
        } else {
            throw new IllegalArgumentException("account is null");
        }
    }

    public void updateCredentials(IAccountManagerResponse response, Account account, String authTokenType, boolean expectActivityLaunch, Bundle loginOptions) {
        IAccountManagerResponse iAccountManagerResponse = response;
        Account account2 = account;
        Bundle.setDefusable(loginOptions, true);
        if (Log.isLoggable(TAG, 2)) {
            Log.v(TAG, "updateCredentials: " + account2 + ", response " + iAccountManagerResponse + ", authTokenType " + authTokenType + ", expectActivityLaunch " + expectActivityLaunch + ", caller's uid " + Binder.getCallingUid() + ", pid " + Binder.getCallingPid());
        } else {
            String str = authTokenType;
            boolean z = expectActivityLaunch;
        }
        if (iAccountManagerResponse == null) {
            throw new IllegalArgumentException("response is null");
        } else if (account2 != null) {
            int userId = UserHandle.getCallingUserId();
            long identityToken = clearCallingIdentity();
            try {
                int i = userId;
                final Account account3 = account;
                final String str2 = authTokenType;
                final Bundle bundle = loginOptions;
                try {
                    AnonymousClass14 r1 = new Session(getUserAccounts(userId), response, account2.type, expectActivityLaunch, true, account2.name, false, true) {
                        public void run() throws RemoteException {
                            this.mAuthenticator.updateCredentials(this, account3, str2, bundle);
                        }

                        /* access modifiers changed from: protected */
                        public String toDebugString(long now) {
                            Bundle bundle = bundle;
                            if (bundle != null) {
                                bundle.keySet();
                            }
                            return super.toDebugString(now) + ", updateCredentials, " + account3.toSafeString() + ", authTokenType " + str2 + ", loginOptions " + bundle;
                        }
                    };
                    r1.bind();
                    restoreCallingIdentity(identityToken);
                } catch (Throwable th) {
                    th = th;
                    restoreCallingIdentity(identityToken);
                    throw th;
                }
            } catch (Throwable th2) {
                th = th2;
                int i2 = userId;
                restoreCallingIdentity(identityToken);
                throw th;
            }
        } else {
            throw new IllegalArgumentException("account is null");
        }
    }

    public void startUpdateCredentialsSession(IAccountManagerResponse response, Account account, String authTokenType, boolean expectActivityLaunch, Bundle loginOptions) {
        IAccountManagerResponse iAccountManagerResponse = response;
        Account account2 = account;
        Bundle bundle = loginOptions;
        Bundle.setDefusable(bundle, true);
        if (Log.isLoggable(TAG, 2)) {
            Log.v(TAG, "startUpdateCredentialsSession: " + account2 + ", response " + iAccountManagerResponse + ", authTokenType " + authTokenType + ", expectActivityLaunch " + expectActivityLaunch + ", caller's uid " + Binder.getCallingUid() + ", pid " + Binder.getCallingPid());
        } else {
            String str = authTokenType;
            boolean z = expectActivityLaunch;
        }
        if (iAccountManagerResponse == null) {
            throw new IllegalArgumentException("response is null");
        } else if (account2 != null) {
            int uid = Binder.getCallingUid();
            int userId = UserHandle.getCallingUserId();
            String callerPkg = bundle.getString("androidPackageName");
            boolean isPasswordForwardingAllowed = checkPermissionAndNote(callerPkg, uid, "android.permission.GET_PASSWORD");
            long identityToken = clearCallingIdentity();
            try {
                UserAccounts accounts = getUserAccounts(userId);
                String str2 = account2.type;
                IAccountManagerResponse iAccountManagerResponse2 = response;
                String str3 = callerPkg;
                String callerPkg2 = account2.name;
                int i = userId;
                int i2 = uid;
                final Account account3 = account;
                final String str4 = authTokenType;
                final Bundle bundle2 = loginOptions;
                try {
                    AnonymousClass15 r1 = new StartAccountSession(accounts, iAccountManagerResponse2, str2, expectActivityLaunch, callerPkg2, false, true, isPasswordForwardingAllowed) {
                        public void run() throws RemoteException {
                            this.mAuthenticator.startUpdateCredentialsSession(this, account3, str4, bundle2);
                        }

                        /* access modifiers changed from: protected */
                        public String toDebugString(long now) {
                            Bundle bundle = bundle2;
                            if (bundle != null) {
                                bundle.keySet();
                            }
                            return super.toDebugString(now) + ", startUpdateCredentialsSession, " + account3.toSafeString() + ", authTokenType " + str4 + ", loginOptions " + bundle2;
                        }
                    };
                    r1.bind();
                    restoreCallingIdentity(identityToken);
                } catch (Throwable th) {
                    th = th;
                    restoreCallingIdentity(identityToken);
                    throw th;
                }
            } catch (Throwable th2) {
                th = th2;
                String str5 = callerPkg;
                int i3 = userId;
                int i4 = uid;
                restoreCallingIdentity(identityToken);
                throw th;
            }
        } else {
            throw new IllegalArgumentException("account is null");
        }
    }

    public void isCredentialsUpdateSuggested(IAccountManagerResponse response, Account account, String statusToken) {
        IAccountManagerResponse iAccountManagerResponse = response;
        Account account2 = account;
        if (Log.isLoggable(TAG, 2)) {
            Log.v(TAG, "isCredentialsUpdateSuggested: " + account2 + ", response " + iAccountManagerResponse + ", caller's uid " + Binder.getCallingUid() + ", pid " + Binder.getCallingPid());
        }
        if (iAccountManagerResponse == null) {
            throw new IllegalArgumentException("response is null");
        } else if (account2 == null) {
            throw new IllegalArgumentException("account is null");
        } else if (!TextUtils.isEmpty(statusToken)) {
            int usrId = UserHandle.getCallingUserId();
            long identityToken = clearCallingIdentity();
            try {
                final Account account3 = account;
                final String str = statusToken;
                new Session(getUserAccounts(usrId), response, account2.type, false, false, account2.name, false) {
                    /* access modifiers changed from: protected */
                    public String toDebugString(long now) {
                        return super.toDebugString(now) + ", isCredentialsUpdateSuggested, " + account3.toSafeString();
                    }

                    public void run() throws RemoteException {
                        this.mAuthenticator.isCredentialsUpdateSuggested(this, account3, str);
                    }

                    public void onResult(Bundle result) {
                        Bundle.setDefusable(result, true);
                        IAccountManagerResponse response = getResponseAndClose();
                        if (response != null) {
                            if (result == null) {
                                AccountManagerService.this.sendErrorResponse(response, 5, "null bundle");
                                return;
                            }
                            if (Log.isLoggable(AccountManagerService.TAG, 2)) {
                                Log.v(AccountManagerService.TAG, getClass().getSimpleName() + " calling onResult() on response " + response);
                            }
                            if (result.getInt("errorCode", -1) > 0) {
                                AccountManagerService.this.sendErrorResponse(response, result.getInt("errorCode"), result.getString("errorMessage"));
                            } else if (!result.containsKey("booleanResult")) {
                                AccountManagerService.this.sendErrorResponse(response, 5, "no result in response");
                            } else {
                                Bundle newResult = new Bundle();
                                newResult.putBoolean("booleanResult", result.getBoolean("booleanResult", false));
                                AccountManagerService.this.sendResponse(response, newResult);
                            }
                        }
                    }
                }.bind();
            } finally {
                restoreCallingIdentity(identityToken);
            }
        } else {
            throw new IllegalArgumentException("status token is empty");
        }
    }

    public void editProperties(IAccountManagerResponse response, String accountType, boolean expectActivityLaunch) {
        IAccountManagerResponse iAccountManagerResponse = response;
        String str = accountType;
        SeempLog.record(21);
        int callingUid = Binder.getCallingUid();
        if (Log.isLoggable(TAG, 2)) {
            Log.v(TAG, "editProperties: accountType " + str + ", response " + iAccountManagerResponse + ", expectActivityLaunch " + expectActivityLaunch + ", caller's uid " + callingUid + ", pid " + Binder.getCallingPid());
        } else {
            boolean z = expectActivityLaunch;
        }
        if (iAccountManagerResponse == null) {
            throw new IllegalArgumentException("response is null");
        } else if (str != null) {
            int userId = UserHandle.getCallingUserId();
            if (isAccountManagedByCaller(str, callingUid, userId) || isSystemUid(callingUid)) {
                long identityToken = clearCallingIdentity();
                try {
                    UserAccounts accounts = getUserAccounts(userId);
                    int i = userId;
                    final String str2 = accountType;
                    try {
                        AnonymousClass17 r1 = new Session(accounts, response, accountType, expectActivityLaunch, true, (String) null, false) {
                            public void run() throws RemoteException {
                                this.mAuthenticator.editProperties(this, this.mAccountType);
                            }

                            /* access modifiers changed from: protected */
                            public String toDebugString(long now) {
                                return super.toDebugString(now) + ", editProperties, accountType " + str2;
                            }
                        };
                        r1.bind();
                        restoreCallingIdentity(identityToken);
                    } catch (Throwable th) {
                        th = th;
                        restoreCallingIdentity(identityToken);
                        throw th;
                    }
                } catch (Throwable th2) {
                    th = th2;
                    int i2 = userId;
                    restoreCallingIdentity(identityToken);
                    throw th;
                }
            } else {
                throw new SecurityException(String.format("uid %s cannot edit authenticator properites for account type: %s", new Object[]{Integer.valueOf(callingUid), str}));
            }
        } else {
            throw new IllegalArgumentException("accountType is null");
        }
    }

    public boolean hasAccountAccess(Account account, String packageName, UserHandle userHandle) {
        if (UserHandle.getAppId(Binder.getCallingUid()) == 1000) {
            Preconditions.checkNotNull(account, "account cannot be null");
            Preconditions.checkNotNull(packageName, "packageName cannot be null");
            Preconditions.checkNotNull(userHandle, "userHandle cannot be null");
            int userId = userHandle.getIdentifier();
            Preconditions.checkArgumentInRange(userId, 0, Integer.MAX_VALUE, "user must be concrete");
            try {
                return hasAccountAccess(account, packageName, this.mPackageManager.getPackageUidAsUser(packageName, userId));
            } catch (PackageManager.NameNotFoundException e) {
                Log.d(TAG, "Package not found " + e.getMessage());
                return false;
            }
        } else {
            throw new SecurityException("Can be called only by system UID");
        }
    }

    private String getPackageNameForUid(int uid) {
        int version;
        String[] packageNames = this.mPackageManager.getPackagesForUid(uid);
        if (ArrayUtils.isEmpty(packageNames)) {
            return null;
        }
        String packageName = packageNames[0];
        if (packageNames.length == 1) {
            return packageName;
        }
        int oldestVersion = Integer.MAX_VALUE;
        String packageName2 = packageName;
        for (String name : packageNames) {
            try {
                ApplicationInfo applicationInfo = this.mPackageManager.getApplicationInfo(name, 0);
                if (applicationInfo != null && (version = applicationInfo.targetSdkVersion) < oldestVersion) {
                    oldestVersion = version;
                    packageName2 = name;
                }
            } catch (PackageManager.NameNotFoundException e) {
            }
        }
        return packageName2;
    }

    /* access modifiers changed from: private */
    public boolean hasAccountAccess(Account account, String packageName, int uid) {
        if (packageName == null && (packageName = getPackageNameForUid(uid)) == null) {
            return false;
        }
        if (permissionIsGranted(account, (String) null, uid, UserHandle.getUserId(uid))) {
            return true;
        }
        int visibility = resolveAccountVisibility(account, packageName, getUserAccounts(UserHandle.getUserId(uid))).intValue();
        if (visibility == 1 || visibility == 2) {
            return true;
        }
        return false;
    }

    public IntentSender createRequestAccountAccessIntentSenderAsUser(Account account, String packageName, UserHandle userHandle) {
        if (UserHandle.getAppId(Binder.getCallingUid()) == 1000) {
            Preconditions.checkNotNull(account, "account cannot be null");
            Preconditions.checkNotNull(packageName, "packageName cannot be null");
            Preconditions.checkNotNull(userHandle, "userHandle cannot be null");
            int userId = userHandle.getIdentifier();
            Preconditions.checkArgumentInRange(userId, 0, Integer.MAX_VALUE, "user must be concrete");
            try {
                Intent intent = newRequestAccountAccessIntent(account, packageName, this.mPackageManager.getPackageUidAsUser(packageName, userId), (RemoteCallback) null);
                long identity = Binder.clearCallingIdentity();
                try {
                    return PendingIntent.getActivityAsUser(this.mContext, 0, intent, 1409286144, (Bundle) null, new UserHandle(userId)).getIntentSender();
                } finally {
                    Binder.restoreCallingIdentity(identity);
                }
            } catch (PackageManager.NameNotFoundException e) {
                Slog.e(TAG, "Unknown package " + packageName);
                return null;
            }
        } else {
            throw new SecurityException("Can be called only by system UID");
        }
    }

    /* access modifiers changed from: private */
    public Intent newRequestAccountAccessIntent(Account account, String packageName, int uid, RemoteCallback callback) {
        final Account account2 = account;
        final int i = uid;
        final String str = packageName;
        final RemoteCallback remoteCallback = callback;
        return newGrantCredentialsPermissionIntent(account, packageName, uid, new AccountAuthenticatorResponse(new IAccountAuthenticatorResponse.Stub() {
            public void onResult(Bundle value) throws RemoteException {
                handleAuthenticatorResponse(true);
            }

            public void onRequestContinued() {
            }

            public void onError(int errorCode, String errorMessage) throws RemoteException {
                handleAuthenticatorResponse(false);
            }

            private void handleAuthenticatorResponse(boolean accessGranted) throws RemoteException {
                AccountManagerService accountManagerService = AccountManagerService.this;
                accountManagerService.cancelNotification(accountManagerService.getCredentialPermissionNotificationId(account2, "com.android.AccountManager.ACCOUNT_ACCESS_TOKEN_TYPE", i), str, UserHandle.getUserHandleForUid(i));
                if (remoteCallback != null) {
                    Bundle result = new Bundle();
                    result.putBoolean("booleanResult", accessGranted);
                    remoteCallback.sendResult(result);
                }
            }
        }), "com.android.AccountManager.ACCOUNT_ACCESS_TOKEN_TYPE", false);
    }

    public boolean someUserHasAccount(Account account) {
        if (UserHandle.isSameApp(1000, Binder.getCallingUid())) {
            long token = Binder.clearCallingIdentity();
            try {
                AccountAndUser[] allAccounts = getAllAccounts();
                for (int i = allAccounts.length - 1; i >= 0; i--) {
                    if (allAccounts[i].account.equals(account)) {
                        return true;
                    }
                }
                Binder.restoreCallingIdentity(token);
                return false;
            } finally {
                Binder.restoreCallingIdentity(token);
            }
        } else {
            throw new SecurityException("Only system can check for accounts across users");
        }
    }

    private class GetAccountsByTypeAndFeatureSession extends Session {
        private volatile Account[] mAccountsOfType = null;
        private volatile ArrayList<Account> mAccountsWithFeatures = null;
        private final int mCallingUid;
        private volatile int mCurrentAccount = 0;
        private final String[] mFeatures;
        private final boolean mIncludeManagedNotVisible;
        private final String mPackageName;
        final /* synthetic */ AccountManagerService this$0;

        /* JADX WARNING: Illegal instructions before constructor call */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public GetAccountsByTypeAndFeatureSession(com.android.server.accounts.AccountManagerService r11, com.android.server.accounts.AccountManagerService.UserAccounts r12, android.accounts.IAccountManagerResponse r13, java.lang.String r14, java.lang.String[] r15, int r16, java.lang.String r17, boolean r18) {
            /*
                r10 = this;
                r9 = r10
                r1 = r11
                r9.this$0 = r1
                r5 = 0
                r6 = 1
                r7 = 0
                r8 = 0
                r0 = r10
                r2 = r12
                r3 = r13
                r4 = r14
                r0.<init>(r1, r2, r3, r4, r5, r6, r7, r8)
                r0 = 0
                r9.mAccountsOfType = r0
                r9.mAccountsWithFeatures = r0
                r0 = 0
                r9.mCurrentAccount = r0
                r0 = r16
                r9.mCallingUid = r0
                r1 = r15
                r9.mFeatures = r1
                r2 = r17
                r9.mPackageName = r2
                r3 = r18
                r9.mIncludeManagedNotVisible = r3
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.server.accounts.AccountManagerService.GetAccountsByTypeAndFeatureSession.<init>(com.android.server.accounts.AccountManagerService, com.android.server.accounts.AccountManagerService$UserAccounts, android.accounts.IAccountManagerResponse, java.lang.String, java.lang.String[], int, java.lang.String, boolean):void");
        }

        public void run() throws RemoteException {
            this.mAccountsOfType = this.this$0.getAccountsFromCache(this.mAccounts, this.mAccountType, this.mCallingUid, this.mPackageName, this.mIncludeManagedNotVisible);
            this.mAccountsWithFeatures = new ArrayList<>(this.mAccountsOfType.length);
            this.mCurrentAccount = 0;
            checkAccount();
        }

        public void checkAccount() {
            if (this.mCurrentAccount >= this.mAccountsOfType.length) {
                sendResult();
                return;
            }
            IAccountAuthenticator accountAuthenticator = this.mAuthenticator;
            if (accountAuthenticator != null) {
                try {
                    accountAuthenticator.hasFeatures(this, this.mAccountsOfType[this.mCurrentAccount], this.mFeatures);
                } catch (RemoteException e) {
                    onError(1, "remote exception");
                }
            } else if (Log.isLoggable(AccountManagerService.TAG, 2)) {
                Log.v(AccountManagerService.TAG, "checkAccount: aborting session since we are no longer connected to the authenticator, " + toDebugString());
            }
        }

        public void onResult(Bundle result) {
            Bundle.setDefusable(result, true);
            this.mNumResults++;
            if (result == null) {
                onError(5, "null bundle");
                return;
            }
            if (result.getBoolean("booleanResult", false)) {
                this.mAccountsWithFeatures.add(this.mAccountsOfType[this.mCurrentAccount]);
            }
            this.mCurrentAccount++;
            checkAccount();
        }

        public void sendResult() {
            IAccountManagerResponse response = getResponseAndClose();
            if (response != null) {
                try {
                    Account[] accounts = new Account[this.mAccountsWithFeatures.size()];
                    for (int i = 0; i < accounts.length; i++) {
                        accounts[i] = this.mAccountsWithFeatures.get(i);
                    }
                    if (Log.isLoggable(AccountManagerService.TAG, 2) != 0) {
                        Log.v(AccountManagerService.TAG, getClass().getSimpleName() + " calling onResult() on response " + response);
                    }
                    Bundle result = new Bundle();
                    result.putParcelableArray("accounts", accounts);
                    response.onResult(result);
                } catch (RemoteException e) {
                    if (Log.isLoggable(AccountManagerService.TAG, 2)) {
                        Log.v(AccountManagerService.TAG, "failure while notifying response", e);
                    }
                }
            }
        }

        /* access modifiers changed from: protected */
        public String toDebugString(long now) {
            StringBuilder sb = new StringBuilder();
            sb.append(super.toDebugString(now));
            sb.append(", getAccountsByTypeAndFeatures, ");
            String[] strArr = this.mFeatures;
            sb.append(strArr != null ? TextUtils.join(",", strArr) : null);
            return sb.toString();
        }
    }

    public Account[] getAccounts(int userId, String opPackageName) {
        int callingUid = Binder.getCallingUid();
        this.mAppOpsManager.checkPackage(callingUid, opPackageName);
        List<String> visibleAccountTypes = getTypesVisibleToCaller(callingUid, userId, opPackageName);
        if (visibleAccountTypes.isEmpty()) {
            return EMPTY_ACCOUNT_ARRAY;
        }
        long identityToken = clearCallingIdentity();
        try {
            return getAccountsInternal(getUserAccounts(userId), callingUid, opPackageName, visibleAccountTypes, false);
        } finally {
            restoreCallingIdentity(identityToken);
        }
    }

    public AccountAndUser[] getRunningAccounts() {
        try {
            return getAccounts(ActivityManager.getService().getRunningUserIds());
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public AccountAndUser[] getAllAccounts() {
        List<UserInfo> users = getUserManager().getUsers(true);
        int[] userIds = new int[users.size()];
        for (int i = 0; i < userIds.length; i++) {
            userIds[i] = users.get(i).id;
        }
        return getAccounts(userIds);
    }

    private AccountAndUser[] getAccounts(int[] userIds) {
        ArrayList<AccountAndUser> runningAccounts = Lists.newArrayList();
        for (int userId : userIds) {
            UserAccounts userAccounts = getUserAccounts(userId);
            if (userAccounts != null) {
                for (Account account : getAccountsFromCache(userAccounts, (String) null, Binder.getCallingUid(), (String) null, false)) {
                    runningAccounts.add(new AccountAndUser(account, userId));
                }
            }
        }
        return (AccountAndUser[]) runningAccounts.toArray(new AccountAndUser[runningAccounts.size()]);
    }

    public Account[] getAccountsAsUser(String type, int userId, String opPackageName) {
        this.mAppOpsManager.checkPackage(Binder.getCallingUid(), opPackageName);
        return getAccountsAsUserForPackage(type, userId, opPackageName, -1, opPackageName, false);
    }

    private Account[] getAccountsAsUserForPackage(String type, int userId, String callingPackage, int packageUid, String opPackageName, boolean includeUserManagedNotVisible) {
        String opPackageName2;
        int callingUid;
        List<String> visibleAccountTypes;
        String str = type;
        int i = userId;
        int callingUid2 = Binder.getCallingUid();
        if (i == UserHandle.getCallingUserId() || callingUid2 == 1000 || this.mContext.checkCallingOrSelfPermission("android.permission.INTERACT_ACROSS_USERS_FULL") == 0) {
            if (Log.isLoggable(TAG, 2)) {
                Log.v(TAG, "getAccounts: accountType " + str + ", caller's uid " + Binder.getCallingUid() + ", pid " + Binder.getCallingPid());
            }
            List<String> managedTypes = getTypesManagedByCaller(callingUid2, UserHandle.getUserId(callingUid2));
            if (packageUid == -1 || (!UserHandle.isSameApp(callingUid2, 1000) && (str == null || !managedTypes.contains(str)))) {
                opPackageName2 = opPackageName;
                callingUid = callingUid2;
            } else {
                callingUid = packageUid;
                opPackageName2 = callingPackage;
            }
            List<String> visibleAccountTypes2 = getTypesVisibleToCaller(callingUid, i, opPackageName2);
            if (visibleAccountTypes2.isEmpty() || (str != null && !visibleAccountTypes2.contains(str))) {
                return EMPTY_ACCOUNT_ARRAY;
            }
            if (visibleAccountTypes2.contains(str)) {
                List<String> visibleAccountTypes3 = new ArrayList<>();
                visibleAccountTypes3.add(str);
                visibleAccountTypes = visibleAccountTypes3;
            } else {
                visibleAccountTypes = visibleAccountTypes2;
            }
            long identityToken = clearCallingIdentity();
            try {
                return getAccountsInternal(getUserAccounts(i), callingUid, opPackageName2, visibleAccountTypes, includeUserManagedNotVisible);
            } finally {
                restoreCallingIdentity(identityToken);
            }
        } else {
            throw new SecurityException("User " + UserHandle.getCallingUserId() + " trying to get account for " + i);
        }
    }

    private Account[] getAccountsInternal(UserAccounts userAccounts, int callingUid, String callingPackage, List<String> visibleAccountTypes, boolean includeUserManagedNotVisible) {
        ArrayList<Account> visibleAccounts = new ArrayList<>();
        for (String visibleType : visibleAccountTypes) {
            Account[] accountsForType = getAccountsFromCache(userAccounts, visibleType, callingUid, callingPackage, includeUserManagedNotVisible);
            if (accountsForType != null) {
                visibleAccounts.addAll(Arrays.asList(accountsForType));
            }
        }
        Account[] result = new Account[visibleAccounts.size()];
        for (int i = 0; i < visibleAccounts.size(); i++) {
            result[i] = visibleAccounts.get(i);
        }
        return result;
    }

    public void addSharedAccountsFromParentUser(int parentUserId, int userId, String opPackageName) {
        checkManageOrCreateUsersPermission("addSharedAccountsFromParentUser");
        for (Account account : getAccountsAsUser((String) null, parentUserId, opPackageName)) {
            addSharedAccountAsUser(account, userId);
        }
    }

    private boolean addSharedAccountAsUser(Account account, int userId) {
        UserAccounts accounts = getUserAccounts(handleIncomingUser(userId));
        accounts.accountsDb.deleteSharedAccount(account);
        long accountId = accounts.accountsDb.insertSharedAccount(account);
        if (accountId < 0) {
            Log.w(TAG, "insertAccountIntoDatabase: " + account.toSafeString() + ", skipping the DB insert failed");
            return false;
        }
        logRecord(AccountsDb.DEBUG_ACTION_ACCOUNT_ADD, "shared_accounts", accountId, accounts);
        return true;
    }

    public boolean renameSharedAccountAsUser(Account account, String newName, int userId) {
        UserAccounts accounts = getUserAccounts(handleIncomingUser(userId));
        long sharedTableAccountId = accounts.accountsDb.findSharedAccountId(account);
        int r = accounts.accountsDb.renameSharedAccount(account, newName);
        if (r > 0) {
            logRecord(AccountsDb.DEBUG_ACTION_ACCOUNT_RENAME, "shared_accounts", sharedTableAccountId, accounts, getCallingUid());
            renameAccountInternal(accounts, account, newName);
        }
        return r > 0;
    }

    public boolean removeSharedAccountAsUser(Account account, int userId) {
        return removeSharedAccountAsUser(account, userId, getCallingUid());
    }

    private boolean removeSharedAccountAsUser(Account account, int userId, int callingUid) {
        UserAccounts accounts = getUserAccounts(handleIncomingUser(userId));
        long sharedTableAccountId = accounts.accountsDb.findSharedAccountId(account);
        boolean deleted = accounts.accountsDb.deleteSharedAccount(account);
        if (deleted) {
            logRecord(AccountsDb.DEBUG_ACTION_ACCOUNT_REMOVE, "shared_accounts", sharedTableAccountId, accounts, callingUid);
            removeAccountInternal(accounts, account, callingUid);
        }
        return deleted;
    }

    public Account[] getSharedAccountsAsUser(int userId) {
        Account[] accountArray;
        UserAccounts accounts = getUserAccounts(handleIncomingUser(userId));
        synchronized (accounts.dbLock) {
            List<Account> accountList = accounts.accountsDb.getSharedAccounts();
            accountArray = new Account[accountList.size()];
            accountList.toArray(accountArray);
        }
        return accountArray;
    }

    public Account[] getAccounts(String type, String opPackageName) {
        return getAccountsAsUser(type, UserHandle.getCallingUserId(), opPackageName);
    }

    public Account[] getAccountsForPackage(String packageName, int uid, String opPackageName) {
        int callingUid = Binder.getCallingUid();
        if (UserHandle.isSameApp(callingUid, 1000)) {
            return getAccountsAsUserForPackage((String) null, UserHandle.getCallingUserId(), packageName, uid, opPackageName, true);
        }
        throw new SecurityException("getAccountsForPackage() called from unauthorized uid " + callingUid + " with uid=" + uid);
    }

    public Account[] getAccountsByTypeForPackage(String type, String packageName, String opPackageName) {
        String str = type;
        String str2 = packageName;
        int callingUid = Binder.getCallingUid();
        int userId = UserHandle.getCallingUserId();
        this.mAppOpsManager.checkPackage(callingUid, opPackageName);
        try {
            int packageUid = this.mPackageManager.getPackageUidAsUser(str2, userId);
            if (!UserHandle.isSameApp(callingUid, 1000) && str != null && !isAccountManagedByCaller(str, callingUid, userId)) {
                return EMPTY_ACCOUNT_ARRAY;
            }
            if (!UserHandle.isSameApp(callingUid, 1000) && str == null) {
                return getAccountsAsUserForPackage(type, userId, packageName, packageUid, opPackageName, false);
            }
            int i = userId;
            int i2 = callingUid;
            return getAccountsAsUserForPackage(type, userId, packageName, packageUid, opPackageName, true);
        } catch (PackageManager.NameNotFoundException re) {
            int i3 = userId;
            int i4 = callingUid;
            Slog.e(TAG, "Couldn't determine the packageUid for " + str2 + re);
            return EMPTY_ACCOUNT_ARRAY;
        }
    }

    private boolean needToStartChooseAccountActivity(Account[] accounts, String callingPackage) {
        if (accounts.length < 1) {
            return false;
        }
        return accounts.length > 1 || resolveAccountVisibility(accounts[0], callingPackage, getUserAccounts(UserHandle.getCallingUserId())).intValue() == 4;
    }

    private void startChooseAccountActivityWithAccounts(IAccountManagerResponse response, Account[] accounts, String callingPackage) {
        Intent intent = new Intent(this.mContext, ChooseAccountActivity.class);
        intent.putExtra("accounts", accounts);
        intent.putExtra("accountManagerResponse", new AccountManagerResponse(response));
        intent.putExtra("androidPackageName", callingPackage);
        this.mContext.startActivityAsUser(intent, UserHandle.of(UserHandle.getCallingUserId()));
    }

    /* access modifiers changed from: private */
    public void handleGetAccountsResult(IAccountManagerResponse response, Account[] accounts, String callingPackage) {
        if (needToStartChooseAccountActivity(accounts, callingPackage)) {
            startChooseAccountActivityWithAccounts(response, accounts, callingPackage);
        } else if (accounts.length == 1) {
            Bundle bundle = new Bundle();
            bundle.putString("authAccount", accounts[0].name);
            bundle.putString("accountType", accounts[0].type);
            onResult(response, bundle);
        } else {
            onResult(response, new Bundle());
        }
    }

    public void getAccountByTypeAndFeatures(IAccountManagerResponse response, String accountType, String[] features, String opPackageName) {
        final IAccountManagerResponse iAccountManagerResponse = response;
        String str = accountType;
        final String str2 = opPackageName;
        int callingUid = Binder.getCallingUid();
        this.mAppOpsManager.checkPackage(callingUid, str2);
        if (Log.isLoggable(TAG, 2)) {
            Log.v(TAG, "getAccount: accountType " + str + ", response " + iAccountManagerResponse + ", features " + Arrays.toString(features) + ", caller's uid " + callingUid + ", pid " + Binder.getCallingPid());
        }
        if (iAccountManagerResponse == null) {
            throw new IllegalArgumentException("response is null");
        } else if (str != null) {
            int userId = UserHandle.getCallingUserId();
            long identityToken = clearCallingIdentity();
            try {
                UserAccounts userAccounts = getUserAccounts(userId);
                if (ArrayUtils.isEmpty(features)) {
                    try {
                        handleGetAccountsResult(iAccountManagerResponse, getAccountsFromCache(userAccounts, accountType, callingUid, opPackageName, true), str2);
                        restoreCallingIdentity(identityToken);
                    } catch (Throwable th) {
                        th = th;
                        int i = userId;
                        int i2 = callingUid;
                        restoreCallingIdentity(identityToken);
                        throw th;
                    }
                } else {
                    int i3 = userId;
                    int i4 = callingUid;
                    try {
                        GetAccountsByTypeAndFeatureSession getAccountsByTypeAndFeatureSession = new GetAccountsByTypeAndFeatureSession(this, userAccounts, new IAccountManagerResponse.Stub() {
                            public void onResult(Bundle value) throws RemoteException {
                                Parcelable[] parcelables = value.getParcelableArray("accounts");
                                Account[] accounts = new Account[parcelables.length];
                                for (int i = 0; i < parcelables.length; i++) {
                                    accounts[i] = (Account) parcelables[i];
                                }
                                AccountManagerService.this.handleGetAccountsResult(iAccountManagerResponse, accounts, str2);
                            }

                            public void onError(int errorCode, String errorMessage) throws RemoteException {
                            }
                        }, accountType, features, callingUid, opPackageName, true);
                        getAccountsByTypeAndFeatureSession.bind();
                        restoreCallingIdentity(identityToken);
                    } catch (Throwable th2) {
                        th = th2;
                        restoreCallingIdentity(identityToken);
                        throw th;
                    }
                }
            } catch (Throwable th3) {
                th = th3;
                int i5 = userId;
                int i6 = callingUid;
                restoreCallingIdentity(identityToken);
                throw th;
            }
        } else {
            throw new IllegalArgumentException("accountType is null");
        }
    }

    public void getAccountsByFeatures(IAccountManagerResponse response, String type, String[] features, String opPackageName) {
        IAccountManagerResponse iAccountManagerResponse = response;
        String str = type;
        String[] strArr = features;
        String str2 = opPackageName;
        int callingUid = Binder.getCallingUid();
        this.mAppOpsManager.checkPackage(callingUid, str2);
        if (Log.isLoggable(TAG, 2)) {
            Log.v(TAG, "getAccounts: accountType " + str + ", response " + iAccountManagerResponse + ", features " + Arrays.toString(features) + ", caller's uid " + callingUid + ", pid " + Binder.getCallingPid());
        }
        if (iAccountManagerResponse == null) {
            throw new IllegalArgumentException("response is null");
        } else if (str != null) {
            int userId = UserHandle.getCallingUserId();
            List<String> visibleAccountTypes = getTypesVisibleToCaller(callingUid, userId, str2);
            if (!visibleAccountTypes.contains(str)) {
                Bundle result = new Bundle();
                result.putParcelableArray("accounts", EMPTY_ACCOUNT_ARRAY);
                try {
                    iAccountManagerResponse.onResult(result);
                } catch (RemoteException e) {
                    Log.e(TAG, "Cannot respond to caller do to exception.", e);
                }
            } else {
                long identityToken = clearCallingIdentity();
                try {
                    UserAccounts userAccounts = getUserAccounts(userId);
                    if (strArr == null) {
                        int i = userId;
                    } else if (strArr.length == 0) {
                        List<String> list = visibleAccountTypes;
                        int i2 = userId;
                    } else {
                        List<String> list2 = visibleAccountTypes;
                        int i3 = userId;
                        try {
                            GetAccountsByTypeAndFeatureSession getAccountsByTypeAndFeatureSession = new GetAccountsByTypeAndFeatureSession(this, userAccounts, response, type, features, callingUid, opPackageName, false);
                            getAccountsByTypeAndFeatureSession.bind();
                            restoreCallingIdentity(identityToken);
                            return;
                        } catch (Throwable th) {
                            th = th;
                            restoreCallingIdentity(identityToken);
                            throw th;
                        }
                    }
                    Account[] accounts = getAccountsFromCache(userAccounts, type, callingUid, opPackageName, false);
                    Bundle result2 = new Bundle();
                    result2.putParcelableArray("accounts", accounts);
                    onResult(iAccountManagerResponse, result2);
                    restoreCallingIdentity(identityToken);
                } catch (Throwable th2) {
                    th = th2;
                    List<String> list3 = visibleAccountTypes;
                    int i4 = userId;
                    restoreCallingIdentity(identityToken);
                    throw th;
                }
            }
        } else {
            throw new IllegalArgumentException("accountType is null");
        }
    }

    public void onAccountAccessed(String token) throws RemoteException {
        int uid = Binder.getCallingUid();
        if (UserHandle.getAppId(uid) != 1000) {
            int userId = UserHandle.getCallingUserId();
            long identity = Binder.clearCallingIdentity();
            try {
                for (Account account : getAccounts(userId, this.mContext.getOpPackageName())) {
                    if (Objects.equals(account.getAccessId(), token) && !hasAccountAccess(account, (String) null, uid)) {
                        updateAppPermission(account, "com.android.AccountManager.ACCOUNT_ACCESS_TOKEN_TYPE", uid, true);
                    }
                }
            } finally {
                Binder.restoreCallingIdentity(identity);
            }
        }
    }

    /* JADX WARNING: type inference failed for: r1v0, types: [android.os.Binder] */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onShellCommand(java.io.FileDescriptor r9, java.io.FileDescriptor r10, java.io.FileDescriptor r11, java.lang.String[] r12, android.os.ShellCallback r13, android.os.ResultReceiver r14) {
        /*
            r8 = this;
            com.android.server.accounts.AccountManagerServiceShellCommand r0 = new com.android.server.accounts.AccountManagerServiceShellCommand
            r0.<init>(r8)
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
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.accounts.AccountManagerService.onShellCommand(java.io.FileDescriptor, java.io.FileDescriptor, java.io.FileDescriptor, java.lang.String[], android.os.ShellCallback, android.os.ResultReceiver):void");
    }

    private abstract class Session extends IAccountAuthenticatorResponse.Stub implements IBinder.DeathRecipient, ServiceConnection {
        final String mAccountName;
        final String mAccountType;
        protected final UserAccounts mAccounts;
        final boolean mAuthDetailsRequired;
        IAccountAuthenticator mAuthenticator;
        final long mCreationTime;
        final boolean mExpectActivityLaunch;
        private int mNumErrors;
        private int mNumRequestContinued;
        public int mNumResults;
        IAccountManagerResponse mResponse;
        private final boolean mStripAuthTokenFromResult;
        final boolean mUpdateLastAuthenticatedTime;

        public abstract void run() throws RemoteException;

        public Session(AccountManagerService accountManagerService, UserAccounts accounts, IAccountManagerResponse response, String accountType, boolean expectActivityLaunch, boolean stripAuthTokenFromResult, String accountName, boolean authDetailsRequired) {
            this(accounts, response, accountType, expectActivityLaunch, stripAuthTokenFromResult, accountName, authDetailsRequired, false);
        }

        public Session(UserAccounts accounts, IAccountManagerResponse response, String accountType, boolean expectActivityLaunch, boolean stripAuthTokenFromResult, String accountName, boolean authDetailsRequired, boolean updateLastAuthenticatedTime) {
            this.mNumResults = 0;
            this.mNumRequestContinued = 0;
            this.mNumErrors = 0;
            this.mAuthenticator = null;
            if (accountType != null) {
                this.mAccounts = accounts;
                this.mStripAuthTokenFromResult = stripAuthTokenFromResult;
                this.mResponse = response;
                this.mAccountType = accountType;
                this.mExpectActivityLaunch = expectActivityLaunch;
                this.mCreationTime = SystemClock.elapsedRealtime();
                this.mAccountName = accountName;
                this.mAuthDetailsRequired = authDetailsRequired;
                this.mUpdateLastAuthenticatedTime = updateLastAuthenticatedTime;
                synchronized (AccountManagerService.this.mSessions) {
                    AccountManagerService.this.mSessions.put(toString(), this);
                }
                if (response != null) {
                    try {
                        response.asBinder().linkToDeath(this, 0);
                    } catch (RemoteException e) {
                        this.mResponse = null;
                        binderDied();
                    }
                }
            } else {
                throw new IllegalArgumentException("accountType is null");
            }
        }

        /* access modifiers changed from: package-private */
        public IAccountManagerResponse getResponseAndClose() {
            if (this.mResponse == null) {
                return null;
            }
            IAccountManagerResponse response = this.mResponse;
            close();
            return response;
        }

        /* access modifiers changed from: protected */
        public boolean checkKeyIntent(int authUid, Intent intent) {
            Intent intent2 = intent;
            intent2.setFlags(intent.getFlags() & -196);
            long bid = Binder.clearCallingIdentity();
            try {
                ResolveInfo resolveInfo = AccountManagerService.this.mContext.getPackageManager().resolveActivityAsUser(intent2, 0, this.mAccounts.userId);
                if (resolveInfo == null) {
                    Binder.restoreCallingIdentity(bid);
                    return false;
                }
                ActivityInfo targetActivityInfo = resolveInfo.activityInfo;
                int targetUid = targetActivityInfo.applicationInfo.uid;
                PackageManagerInternal pmi = (PackageManagerInternal) LocalServices.getService(PackageManagerInternal.class);
                if (!isExportedSystemActivity(targetActivityInfo)) {
                    try {
                        if (!pmi.hasSignatureCapability(targetUid, authUid, 16)) {
                            Log.e(AccountManagerService.TAG, String.format("KEY_INTENT resolved to an Activity (%s) in a package (%s) that does not share a signature with the supplying authenticator (%s).", new Object[]{targetActivityInfo.name, targetActivityInfo.packageName, this.mAccountType}));
                            Binder.restoreCallingIdentity(bid);
                            return false;
                        }
                    } catch (Throwable th) {
                        th = th;
                        Binder.restoreCallingIdentity(bid);
                        throw th;
                    }
                } else {
                    int i = authUid;
                }
                Binder.restoreCallingIdentity(bid);
                return true;
            } catch (Throwable th2) {
                th = th2;
                int i2 = authUid;
                Binder.restoreCallingIdentity(bid);
                throw th;
            }
        }

        private boolean isExportedSystemActivity(ActivityInfo activityInfo) {
            String className = activityInfo.name;
            return PackageManagerService.PLATFORM_PACKAGE_NAME.equals(activityInfo.packageName) && (GrantCredentialsPermissionActivity.class.getName().equals(className) || CantAddAccountActivity.class.getName().equals(className));
        }

        /* JADX WARNING: Code restructure failed: missing block: B:10:0x001e, code lost:
            r0.asBinder().unlinkToDeath(r3, 0);
            r3.mResponse = null;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:11:0x0029, code lost:
            cancelTimeout();
            unbind();
         */
        /* JADX WARNING: Code restructure failed: missing block: B:12:0x002f, code lost:
            return;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:8:0x001a, code lost:
            r0 = r3.mResponse;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:9:0x001c, code lost:
            if (r0 == null) goto L_0x0029;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        private void close() {
            /*
                r3 = this;
                com.android.server.accounts.AccountManagerService r0 = com.android.server.accounts.AccountManagerService.this
                java.util.LinkedHashMap r0 = r0.mSessions
                monitor-enter(r0)
                com.android.server.accounts.AccountManagerService r1 = com.android.server.accounts.AccountManagerService.this     // Catch:{ all -> 0x0030 }
                java.util.LinkedHashMap r1 = r1.mSessions     // Catch:{ all -> 0x0030 }
                java.lang.String r2 = r3.toString()     // Catch:{ all -> 0x0030 }
                java.lang.Object r1 = r1.remove(r2)     // Catch:{ all -> 0x0030 }
                if (r1 != 0) goto L_0x0019
                monitor-exit(r0)     // Catch:{ all -> 0x0030 }
                return
            L_0x0019:
                monitor-exit(r0)     // Catch:{ all -> 0x0030 }
                android.accounts.IAccountManagerResponse r0 = r3.mResponse
                if (r0 == 0) goto L_0x0029
                android.os.IBinder r0 = r0.asBinder()
                r1 = 0
                r0.unlinkToDeath(r3, r1)
                r0 = 0
                r3.mResponse = r0
            L_0x0029:
                r3.cancelTimeout()
                r3.unbind()
                return
            L_0x0030:
                r1 = move-exception
                monitor-exit(r0)     // Catch:{ all -> 0x0030 }
                throw r1
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.server.accounts.AccountManagerService.Session.close():void");
        }

        public void binderDied() {
            this.mResponse = null;
            close();
        }

        /* access modifiers changed from: protected */
        public String toDebugString() {
            return toDebugString(SystemClock.elapsedRealtime());
        }

        /* access modifiers changed from: protected */
        public String toDebugString(long now) {
            StringBuilder sb = new StringBuilder();
            sb.append("Session: expectLaunch ");
            sb.append(this.mExpectActivityLaunch);
            sb.append(", connected ");
            sb.append(this.mAuthenticator != null);
            sb.append(", stats (");
            sb.append(this.mNumResults);
            sb.append(SliceClientPermissions.SliceAuthority.DELIMITER);
            sb.append(this.mNumRequestContinued);
            sb.append(SliceClientPermissions.SliceAuthority.DELIMITER);
            sb.append(this.mNumErrors);
            sb.append("), lifetime ");
            sb.append(((double) (now - this.mCreationTime)) / 1000.0d);
            return sb.toString();
        }

        /* access modifiers changed from: package-private */
        public void bind() {
            if (Log.isLoggable(AccountManagerService.TAG, 2)) {
                Log.v(AccountManagerService.TAG, "initiating bind to authenticator type " + this.mAccountType);
            }
            if (!bindToAuthenticator(this.mAccountType)) {
                Log.d(AccountManagerService.TAG, "bind attempt failed for " + toDebugString());
                onError(1, "bind failure");
            }
        }

        private void unbind() {
            if (this.mAuthenticator != null) {
                this.mAuthenticator = null;
                AccountManagerService.this.mContext.unbindService(this);
            }
        }

        public void cancelTimeout() {
            AccountManagerService.this.mHandler.removeMessages(3, this);
        }

        public void onServiceConnected(ComponentName name, IBinder service) {
            this.mAuthenticator = IAccountAuthenticator.Stub.asInterface(service);
            try {
                run();
            } catch (RemoteException e) {
                onError(1, "remote exception");
            }
        }

        public void onServiceDisconnected(ComponentName name) {
            this.mAuthenticator = null;
            IAccountManagerResponse response = getResponseAndClose();
            if (response != null) {
                try {
                    response.onError(1, "disconnected");
                } catch (RemoteException e) {
                    if (Log.isLoggable(AccountManagerService.TAG, 2)) {
                        Log.v(AccountManagerService.TAG, "Session.onServiceDisconnected: caught RemoteException while responding", e);
                    }
                }
            }
        }

        public void onTimedOut() {
            IAccountManagerResponse response = getResponseAndClose();
            if (response != null) {
                try {
                    response.onError(1, "timeout");
                } catch (RemoteException e) {
                    if (Log.isLoggable(AccountManagerService.TAG, 2)) {
                        Log.v(AccountManagerService.TAG, "Session.onTimedOut: caught RemoteException while responding", e);
                    }
                }
            }
        }

        public void onResult(Bundle result) {
            IAccountManagerResponse response;
            boolean needUpdate = true;
            Bundle.setDefusable(result, true);
            this.mNumResults++;
            Intent intent = null;
            if (result != null) {
                boolean isSuccessfulConfirmCreds = result.getBoolean("booleanResult", false);
                boolean isSuccessfulUpdateCredsOrAddAccount = result.containsKey("authAccount") && result.containsKey("accountType");
                if (!this.mUpdateLastAuthenticatedTime || (!isSuccessfulConfirmCreds && !isSuccessfulUpdateCredsOrAddAccount)) {
                    needUpdate = false;
                }
                if (needUpdate || this.mAuthDetailsRequired) {
                    boolean accountPresent = AccountManagerService.this.isAccountPresentForCaller(this.mAccountName, this.mAccountType);
                    if (needUpdate && accountPresent) {
                        boolean unused = AccountManagerService.this.updateLastAuthenticatedTime(new Account(this.mAccountName, this.mAccountType));
                    }
                    if (this.mAuthDetailsRequired) {
                        long lastAuthenticatedTime = -1;
                        if (accountPresent) {
                            lastAuthenticatedTime = this.mAccounts.accountsDb.findAccountLastAuthenticatedTime(new Account(this.mAccountName, this.mAccountType));
                        }
                        result.putLong("lastAuthenticatedTime", lastAuthenticatedTime);
                    }
                }
            }
            if (result != null) {
                Intent intent2 = (Intent) result.getParcelable("intent");
                intent = intent2;
                if (intent2 != null && !checkKeyIntent(Binder.getCallingUid(), intent)) {
                    onError(5, "invalid intent in bundle returned");
                    return;
                }
            }
            if (result != null && !TextUtils.isEmpty(result.getString("authtoken"))) {
                String accountName = result.getString("authAccount");
                String accountType = result.getString("accountType");
                if (!TextUtils.isEmpty(accountName) && !TextUtils.isEmpty(accountType)) {
                    Account account = new Account(accountName, accountType);
                    AccountManagerService accountManagerService = AccountManagerService.this;
                    accountManagerService.cancelNotification(accountManagerService.getSigninRequiredNotificationId(this.mAccounts, account), new UserHandle(this.mAccounts.userId));
                }
            }
            if (!this.mExpectActivityLaunch || result == null || !result.containsKey("intent")) {
                response = getResponseAndClose();
            } else {
                response = this.mResponse;
            }
            if (response == null) {
                return;
            }
            if (result == null) {
                try {
                    if (Log.isLoggable(AccountManagerService.TAG, 2)) {
                        Log.v(AccountManagerService.TAG, getClass().getSimpleName() + " calling onError() on response " + response);
                    }
                    response.onError(5, "null bundle returned");
                } catch (RemoteException e) {
                    if (Log.isLoggable(AccountManagerService.TAG, 2)) {
                        Log.v(AccountManagerService.TAG, "failure while notifying response", e);
                    }
                }
            } else {
                if (this.mStripAuthTokenFromResult) {
                    result.remove("authtoken");
                }
                if (Log.isLoggable(AccountManagerService.TAG, 2)) {
                    Log.v(AccountManagerService.TAG, getClass().getSimpleName() + " calling onResult() on response " + response);
                }
                if (result.getInt("errorCode", -1) <= 0 || intent != null) {
                    response.onResult(result);
                } else {
                    response.onError(result.getInt("errorCode"), result.getString("errorMessage"));
                }
            }
        }

        public void onRequestContinued() {
            this.mNumRequestContinued++;
        }

        public void onError(int errorCode, String errorMessage) {
            this.mNumErrors++;
            IAccountManagerResponse response = getResponseAndClose();
            if (response != null) {
                if (Log.isLoggable(AccountManagerService.TAG, 2)) {
                    Log.v(AccountManagerService.TAG, getClass().getSimpleName() + " calling onError() on response " + response);
                }
                try {
                    response.onError(errorCode, errorMessage);
                } catch (RemoteException e) {
                    if (Log.isLoggable(AccountManagerService.TAG, 2)) {
                        Log.v(AccountManagerService.TAG, "Session.onError: caught RemoteException while responding", e);
                    }
                }
            } else if (Log.isLoggable(AccountManagerService.TAG, 2)) {
                Log.v(AccountManagerService.TAG, "Session.onError: already closed");
            }
        }

        private boolean bindToAuthenticator(String authenticatorType) {
            RegisteredServicesCache.ServiceInfo<AuthenticatorDescription> authenticatorInfo = AccountManagerService.this.mAuthenticatorCache.getServiceInfo(AuthenticatorDescription.newKey(authenticatorType), this.mAccounts.userId);
            if (authenticatorInfo == null) {
                if (Log.isLoggable(AccountManagerService.TAG, 2)) {
                    Log.v(AccountManagerService.TAG, "there is no authenticator for " + authenticatorType + ", bailing out");
                }
                return false;
            } else if (AccountManagerService.this.isLocalUnlockedUser(this.mAccounts.userId) || authenticatorInfo.componentInfo.directBootAware) {
                Intent intent = new Intent();
                intent.setAction("android.accounts.AccountAuthenticator");
                intent.setComponent(authenticatorInfo.componentName);
                if (Log.isLoggable(AccountManagerService.TAG, 2)) {
                    Log.v(AccountManagerService.TAG, "performing bindService to " + authenticatorInfo.componentName);
                }
                int flags = 1;
                if (AccountManagerService.this.mAuthenticatorCache.getBindInstantServiceAllowed(this.mAccounts.userId)) {
                    flags = 1 | DumpState.DUMP_CHANGES;
                }
                if (AccountManagerService.this.mContext.bindServiceAsUser(intent, this, flags, UserHandle.of(this.mAccounts.userId))) {
                    return true;
                }
                if (Log.isLoggable(AccountManagerService.TAG, 2)) {
                    Log.v(AccountManagerService.TAG, "bindService to " + authenticatorInfo.componentName + " failed");
                }
                return false;
            } else {
                Slog.w(AccountManagerService.TAG, "Blocking binding to authenticator " + authenticatorInfo.componentName + " which isn't encryption aware");
                return false;
            }
        }
    }

    class MessageHandler extends Handler {
        MessageHandler(Looper looper) {
            super(looper);
        }

        public void handleMessage(Message msg) {
            int i = msg.what;
            if (i == 3) {
                ((Session) msg.obj).onTimedOut();
            } else if (i == 4) {
                AccountManagerService.this.copyAccountToUser((IAccountManagerResponse) null, (Account) msg.obj, msg.arg1, msg.arg2);
            } else {
                throw new IllegalStateException("unhandled message: " + msg.what);
            }
        }
    }

    private void logRecord(UserAccounts accounts, String action, String tableName) {
        logRecord(action, tableName, -1, accounts);
    }

    private void logRecordWithUid(UserAccounts accounts, String action, String tableName, int uid) {
        logRecord(action, tableName, -1, accounts, uid);
    }

    private void logRecord(String action, String tableName, long accountId, UserAccounts userAccount) {
        logRecord(action, tableName, accountId, userAccount, getCallingUid());
    }

    private void logRecord(String action, String tableName, long accountId, UserAccounts userAccount, int callingUid) {
        long insertionPoint = userAccount.accountsDb.reserveDebugDbInsertionPoint();
        if (insertionPoint != -1) {
            this.mHandler.post(new Runnable(action, tableName, accountId, userAccount, callingUid, insertionPoint) {
                private final long accountId;
                private final String action;
                private final int callingUid;
                private final String tableName;
                private final UserAccounts userAccount;
                private final long userDebugDbInsertionPoint;

                {
                    this.action = action;
                    this.tableName = tableName;
                    this.accountId = accountId;
                    this.userAccount = userAccount;
                    this.callingUid = callingUid;
                    this.userDebugDbInsertionPoint = userDebugDbInsertionPoint;
                }

                /* Debug info: failed to restart local var, previous not found, register: 7 */
                /* JADX INFO: finally extract failed */
                public void run() {
                    synchronized (this.userAccount.accountsDb.mDebugStatementLock) {
                        SQLiteStatement logStatement = this.userAccount.accountsDb.getStatementForLogging();
                        if (logStatement != null) {
                            logStatement.bindLong(1, this.accountId);
                            logStatement.bindString(2, this.action);
                            logStatement.bindString(3, AccountManagerService.this.mDateFormat.format(new Date()));
                            logStatement.bindLong(4, (long) this.callingUid);
                            logStatement.bindString(5, this.tableName);
                            logStatement.bindLong(6, this.userDebugDbInsertionPoint);
                            try {
                                logStatement.execute();
                                logStatement.clearBindings();
                            } catch (IllegalStateException e) {
                                try {
                                    Slog.w(AccountManagerService.TAG, "Failed to insert a log record. accountId=" + this.accountId + " action=" + this.action + " tableName=" + this.tableName + " Error: " + e);
                                    logStatement.clearBindings();
                                } catch (Throwable th) {
                                    logStatement.clearBindings();
                                    throw th;
                                }
                            }
                        }
                    }
                }
            });
            return;
        }
    }

    public IBinder onBind(Intent intent) {
        return asBinder();
    }

    private static boolean scanArgs(String[] args, String value) {
        if (args != null) {
            for (String arg : args) {
                if (value.equals(arg)) {
                    return true;
                }
            }
        }
        return false;
    }

    /* access modifiers changed from: protected */
    public void dump(FileDescriptor fd, PrintWriter fout, String[] args) {
        if (DumpUtils.checkDumpPermission(this.mContext, TAG, fout)) {
            boolean isCheckinRequest = scanArgs(args, "--checkin") || scanArgs(args, "-c");
            IndentingPrintWriter ipw = new IndentingPrintWriter(fout, "  ");
            for (UserInfo user : getUserManager().getUsers()) {
                ipw.println("User " + user + ":");
                ipw.increaseIndent();
                dumpUser(getUserAccounts(user.id), fd, ipw, args, isCheckinRequest);
                ipw.println();
                ipw.decreaseIndent();
            }
        }
    }

    /* JADX INFO: finally extract failed */
    /* JADX WARNING: Code restructure failed: missing block: B:58:0x0158, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:64:0x0161, code lost:
        r0 = th;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void dumpUser(com.android.server.accounts.AccountManagerService.UserAccounts r17, java.io.FileDescriptor r18, java.io.PrintWriter r19, java.lang.String[] r20, boolean r21) {
        /*
            r16 = this;
            r7 = r16
            r8 = r17
            r9 = r19
            if (r21 == 0) goto L_0x001a
            java.lang.Object r1 = r8.dbLock
            monitor-enter(r1)
            com.android.server.accounts.AccountsDb r0 = r8.accountsDb     // Catch:{ all -> 0x0017 }
            r0.dumpDeAccountsTable(r9)     // Catch:{ all -> 0x0017 }
            monitor-exit(r1)     // Catch:{ all -> 0x0017 }
            r4 = r18
            r5 = r20
            goto L_0x014a
        L_0x0017:
            r0 = move-exception
            monitor-exit(r1)     // Catch:{ all -> 0x0017 }
            throw r0
        L_0x001a:
            r3 = 0
            r4 = 1000(0x3e8, float:1.401E-42)
            r5 = 0
            r6 = 0
            r1 = r16
            r2 = r17
            android.accounts.Account[] r1 = r1.getAccountsFromCache(r2, r3, r4, r5, r6)
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r2 = "Accounts: "
            r0.append(r2)
            int r2 = r1.length
            r0.append(r2)
            java.lang.String r0 = r0.toString()
            r9.println(r0)
            int r0 = r1.length
            r2 = 0
        L_0x003e:
            if (r2 >= r0) goto L_0x005d
            r3 = r1[r2]
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            java.lang.String r5 = "  "
            r4.append(r5)
            java.lang.String r5 = r3.hideNameToString()
            r4.append(r5)
            java.lang.String r4 = r4.toString()
            r9.println(r4)
            int r2 = r2 + 1
            goto L_0x003e
        L_0x005d:
            r19.println()
            java.lang.Object r2 = r8.dbLock
            monitor-enter(r2)
            com.android.server.accounts.AccountsDb r0 = r8.accountsDb     // Catch:{ all -> 0x015a }
            r0.dumpDebugTable(r9)     // Catch:{ all -> 0x015a }
            monitor-exit(r2)     // Catch:{ all -> 0x015a }
            r19.println()
            java.util.LinkedHashMap<java.lang.String, com.android.server.accounts.AccountManagerService$Session> r3 = r7.mSessions
            monitor-enter(r3)
            long r4 = android.os.SystemClock.elapsedRealtime()     // Catch:{ all -> 0x0151 }
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ all -> 0x0151 }
            r0.<init>()     // Catch:{ all -> 0x0151 }
            java.lang.String r2 = "Active Sessions: "
            r0.append(r2)     // Catch:{ all -> 0x0151 }
            java.util.LinkedHashMap<java.lang.String, com.android.server.accounts.AccountManagerService$Session> r2 = r7.mSessions     // Catch:{ all -> 0x0151 }
            int r2 = r2.size()     // Catch:{ all -> 0x0151 }
            r0.append(r2)     // Catch:{ all -> 0x0151 }
            java.lang.String r0 = r0.toString()     // Catch:{ all -> 0x0151 }
            r9.println(r0)     // Catch:{ all -> 0x0151 }
            java.util.LinkedHashMap<java.lang.String, com.android.server.accounts.AccountManagerService$Session> r0 = r7.mSessions     // Catch:{ all -> 0x0151 }
            java.util.Collection r0 = r0.values()     // Catch:{ all -> 0x0151 }
            java.util.Iterator r0 = r0.iterator()     // Catch:{ all -> 0x0151 }
        L_0x0097:
            boolean r2 = r0.hasNext()     // Catch:{ all -> 0x0151 }
            if (r2 == 0) goto L_0x00bc
            java.lang.Object r2 = r0.next()     // Catch:{ all -> 0x0151 }
            com.android.server.accounts.AccountManagerService$Session r2 = (com.android.server.accounts.AccountManagerService.Session) r2     // Catch:{ all -> 0x0151 }
            java.lang.StringBuilder r6 = new java.lang.StringBuilder     // Catch:{ all -> 0x0151 }
            r6.<init>()     // Catch:{ all -> 0x0151 }
            java.lang.String r10 = "  "
            r6.append(r10)     // Catch:{ all -> 0x0151 }
            java.lang.String r10 = r2.toDebugString(r4)     // Catch:{ all -> 0x0151 }
            r6.append(r10)     // Catch:{ all -> 0x0151 }
            java.lang.String r6 = r6.toString()     // Catch:{ all -> 0x0151 }
            r9.println(r6)     // Catch:{ all -> 0x0151 }
            goto L_0x0097
        L_0x00bc:
            monitor-exit(r3)     // Catch:{ all -> 0x0151 }
            r19.println()
            com.android.server.accounts.IAccountAuthenticatorCache r0 = r7.mAuthenticatorCache
            int r2 = r17.userId
            r4 = r18
            r5 = r20
            r0.dump(r4, r9, r5, r2)
            android.util.SparseArray<com.android.server.accounts.AccountManagerService$UserAccounts> r2 = r7.mUsers
            monitor-enter(r2)
            int r0 = r17.userId     // Catch:{ all -> 0x014e }
            boolean r0 = r7.isLocalUnlockedUser(r0)     // Catch:{ all -> 0x014e }
            r3 = r0
            monitor-exit(r2)     // Catch:{ all -> 0x014e }
            if (r3 != 0) goto L_0x00dd
            return
        L_0x00dd:
            r19.println()
            java.lang.Object r6 = r8.dbLock
            monitor-enter(r6)
            com.android.server.accounts.AccountsDb r0 = r8.accountsDb     // Catch:{ all -> 0x014b }
            java.util.Map r0 = r0.findAllVisibilityValues()     // Catch:{ all -> 0x014b }
            java.lang.String r2 = "Account visibility:"
            r9.println(r2)     // Catch:{ all -> 0x014b }
            java.util.Set r2 = r0.keySet()     // Catch:{ all -> 0x014b }
            java.util.Iterator r2 = r2.iterator()     // Catch:{ all -> 0x014b }
        L_0x00f6:
            boolean r10 = r2.hasNext()     // Catch:{ all -> 0x014b }
            if (r10 == 0) goto L_0x0149
            java.lang.Object r10 = r2.next()     // Catch:{ all -> 0x014b }
            android.accounts.Account r10 = (android.accounts.Account) r10     // Catch:{ all -> 0x014b }
            java.lang.String r11 = "  ******"
            r9.println(r11)     // Catch:{ all -> 0x014b }
            java.lang.Object r11 = r0.get(r10)     // Catch:{ all -> 0x014b }
            java.util.Map r11 = (java.util.Map) r11     // Catch:{ all -> 0x014b }
            java.util.Set r12 = r11.entrySet()     // Catch:{ all -> 0x014b }
            java.util.Iterator r12 = r12.iterator()     // Catch:{ all -> 0x014b }
        L_0x0115:
            boolean r13 = r12.hasNext()     // Catch:{ all -> 0x014b }
            if (r13 == 0) goto L_0x0148
            java.lang.Object r13 = r12.next()     // Catch:{ all -> 0x014b }
            java.util.Map$Entry r13 = (java.util.Map.Entry) r13     // Catch:{ all -> 0x014b }
            java.lang.StringBuilder r14 = new java.lang.StringBuilder     // Catch:{ all -> 0x014b }
            r14.<init>()     // Catch:{ all -> 0x014b }
            java.lang.String r15 = "    "
            r14.append(r15)     // Catch:{ all -> 0x014b }
            java.lang.Object r15 = r13.getKey()     // Catch:{ all -> 0x014b }
            java.lang.String r15 = (java.lang.String) r15     // Catch:{ all -> 0x014b }
            r14.append(r15)     // Catch:{ all -> 0x014b }
            java.lang.String r15 = ", "
            r14.append(r15)     // Catch:{ all -> 0x014b }
            java.lang.Object r15 = r13.getValue()     // Catch:{ all -> 0x014b }
            r14.append(r15)     // Catch:{ all -> 0x014b }
            java.lang.String r14 = r14.toString()     // Catch:{ all -> 0x014b }
            r9.println(r14)     // Catch:{ all -> 0x014b }
            goto L_0x0115
        L_0x0148:
            goto L_0x00f6
        L_0x0149:
            monitor-exit(r6)     // Catch:{ all -> 0x014b }
        L_0x014a:
            return
        L_0x014b:
            r0 = move-exception
            monitor-exit(r6)     // Catch:{ all -> 0x014b }
            throw r0
        L_0x014e:
            r0 = move-exception
            monitor-exit(r2)     // Catch:{ all -> 0x014e }
            throw r0
        L_0x0151:
            r0 = move-exception
            r4 = r18
            r5 = r20
        L_0x0156:
            monitor-exit(r3)     // Catch:{ all -> 0x0158 }
            throw r0
        L_0x0158:
            r0 = move-exception
            goto L_0x0156
        L_0x015a:
            r0 = move-exception
            r4 = r18
            r5 = r20
        L_0x015f:
            monitor-exit(r2)     // Catch:{ all -> 0x0161 }
            throw r0
        L_0x0161:
            r0 = move-exception
            goto L_0x015f
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.accounts.AccountManagerService.dumpUser(com.android.server.accounts.AccountManagerService$UserAccounts, java.io.FileDescriptor, java.io.PrintWriter, java.lang.String[], boolean):void");
    }

    /* access modifiers changed from: private */
    public void doNotification(UserAccounts accounts, Account account, CharSequence message, Intent intent, String packageName, int userId) {
        Account account2 = account;
        CharSequence charSequence = message;
        Intent intent2 = intent;
        String str = packageName;
        int i = userId;
        long identityToken = clearCallingIdentity();
        try {
            if (Log.isLoggable(TAG, 2)) {
                Log.v(TAG, "doNotification: " + charSequence + " intent:" + intent2);
            }
            if (intent.getComponent() == null || !GrantCredentialsPermissionActivity.class.getName().equals(intent.getComponent().getClassName())) {
                Context contextForUser = getContextForUser(new UserHandle(i));
                NotificationId id = getSigninRequiredNotificationId(accounts, account);
                intent2.addCategory(id.mTag);
                String notificationTitleFormat = contextForUser.getText(17040586).toString();
                Notification.Builder contentText = new Notification.Builder(contextForUser, SystemNotificationChannels.ACCOUNT).setWhen(0).setSmallIcon(17301642).setColor(contextForUser.getColor(17170460)).setContentTitle(String.format(notificationTitleFormat, new Object[]{account2.name})).setContentText(charSequence);
                String str2 = notificationTitleFormat;
                Notification.Builder builder = contentText;
                installNotification(id, builder.setContentIntent(PendingIntent.getActivityAsUser(this.mContext, 0, intent, 268435456, (Bundle) null, new UserHandle(i))).build(), str, i);
            } else {
                createNoCredentialsPermissionNotification(account2, intent2, str, i);
            }
        } finally {
            restoreCallingIdentity(identityToken);
        }
    }

    private void installNotification(NotificationId id, Notification notification, String packageName, int userId) {
        long token = clearCallingIdentity();
        try {
            try {
                this.mInjector.getNotificationManager().enqueueNotificationWithTag(packageName, PackageManagerService.PLATFORM_PACKAGE_NAME, id.mTag, id.mId, notification, userId);
            } catch (RemoteException e) {
            }
        } finally {
            Binder.restoreCallingIdentity(token);
        }
    }

    /* access modifiers changed from: private */
    public void cancelNotification(NotificationId id, UserHandle user) {
        cancelNotification(id, this.mContext.getPackageName(), user);
    }

    /* access modifiers changed from: private */
    public void cancelNotification(NotificationId id, String packageName, UserHandle user) {
        long identityToken = clearCallingIdentity();
        try {
            this.mInjector.getNotificationManager().cancelNotificationWithTag(packageName, id.mTag, id.mId, user.getIdentifier());
        } catch (RemoteException e) {
        } catch (Throwable th) {
            restoreCallingIdentity(identityToken);
            throw th;
        }
        restoreCallingIdentity(identityToken);
    }

    private boolean isPermittedForPackage(String packageName, int userId, String... permissions) {
        int opCode;
        long identity = Binder.clearCallingIdentity();
        try {
            int uid = this.mPackageManager.getPackageUidAsUser(packageName, userId);
            IPackageManager pm = ActivityThread.getPackageManager();
            for (String perm : permissions) {
                if (pm.checkPermission(perm, packageName, userId) == 0 && ((opCode = AppOpsManager.permissionToOpCode(perm)) == -1 || this.mAppOpsManager.checkOpNoThrow(opCode, uid, packageName) == 0)) {
                    Binder.restoreCallingIdentity(identity);
                    return true;
                }
            }
        } catch (PackageManager.NameNotFoundException | RemoteException e) {
        } catch (Throwable th) {
            Binder.restoreCallingIdentity(identity);
            throw th;
        }
        Binder.restoreCallingIdentity(identity);
        return false;
    }

    private boolean checkPermissionAndNote(String opPackageName, int callingUid, String... permissions) {
        for (String perm : permissions) {
            if (this.mContext.checkCallingOrSelfPermission(perm) == 0) {
                if (Log.isLoggable(TAG, 2)) {
                    Log.v(TAG, "  caller uid " + callingUid + " has " + perm);
                }
                int opCode = AppOpsManager.permissionToOpCode(perm);
                if (opCode == -1 || this.mAppOpsManager.noteOpNoThrow(opCode, callingUid, opPackageName) == 0) {
                    return true;
                }
            }
        }
        return false;
    }

    private int handleIncomingUser(int userId) {
        try {
            return ActivityManager.getService().handleIncomingUser(Binder.getCallingPid(), Binder.getCallingUid(), userId, true, true, "", (String) null);
        } catch (RemoteException e) {
            return userId;
        }
    }

    private boolean isPrivileged(int callingUid) {
        long identityToken = Binder.clearCallingIdentity();
        try {
            String[] packages = this.mPackageManager.getPackagesForUid(callingUid);
            if (packages == null) {
                Log.d(TAG, "No packages for callingUid " + callingUid);
                Binder.restoreCallingIdentity(identityToken);
                return false;
            }
            for (String name : packages) {
                PackageInfo packageInfo = this.mPackageManager.getPackageInfo(name, 0);
                if (!(packageInfo == null || (packageInfo.applicationInfo.privateFlags & 8) == 0)) {
                    Binder.restoreCallingIdentity(identityToken);
                    return true;
                }
            }
            Binder.restoreCallingIdentity(identityToken);
            return false;
        } catch (PackageManager.NameNotFoundException e) {
            Log.d(TAG, "Package not found " + e.getMessage());
        } catch (Throwable th) {
            Binder.restoreCallingIdentity(identityToken);
            throw th;
        }
    }

    private boolean permissionIsGranted(Account account, String authTokenType, int callerUid, int userId) {
        if (UserHandle.getAppId(callerUid) == 1000) {
            if (Log.isLoggable(TAG, 2)) {
                Log.v(TAG, "Access to " + account + " granted calling uid is system");
            }
            return true;
        } else if (isPrivileged(callerUid)) {
            if (Log.isLoggable(TAG, 2)) {
                Log.v(TAG, "Access to " + account + " granted calling uid " + callerUid + " privileged");
            }
            return true;
        } else if (account != null && isAccountManagedByCaller(account.type, callerUid, userId)) {
            if (Log.isLoggable(TAG, 2)) {
                Log.v(TAG, "Access to " + account + " granted calling uid " + callerUid + " manages the account");
            }
            return true;
        } else if (account != null && hasExplicitlyGrantedPermission(account, authTokenType, callerUid)) {
            if (Log.isLoggable(TAG, 2)) {
                Log.v(TAG, "Access to " + account + " granted calling uid " + callerUid + " user granted access");
            }
            return true;
        } else if (!Log.isLoggable(TAG, 2)) {
            return false;
        } else {
            Log.v(TAG, "Access to " + account + " not granted for uid " + callerUid);
            return false;
        }
    }

    private boolean isAccountVisibleToCaller(String accountType, int callingUid, int userId, String opPackageName) {
        if (accountType == null) {
            return false;
        }
        return getTypesVisibleToCaller(callingUid, userId, opPackageName).contains(accountType);
    }

    private boolean checkGetAccountsPermission(String packageName, int userId) {
        return isPermittedForPackage(packageName, userId, "android.permission.GET_ACCOUNTS", "android.permission.GET_ACCOUNTS_PRIVILEGED");
    }

    private boolean checkReadContactsPermission(String packageName, int userId) {
        return isPermittedForPackage(packageName, userId, "android.permission.READ_CONTACTS");
    }

    /* JADX INFO: finally extract failed */
    private boolean accountTypeManagesContacts(String accountType, int userId) {
        if (accountType == null) {
            return false;
        }
        long identityToken = Binder.clearCallingIdentity();
        try {
            Collection<RegisteredServicesCache.ServiceInfo<AuthenticatorDescription>> serviceInfos = this.mAuthenticatorCache.getAllServices(userId);
            Binder.restoreCallingIdentity(identityToken);
            for (RegisteredServicesCache.ServiceInfo<AuthenticatorDescription> serviceInfo : serviceInfos) {
                if (accountType.equals(((AuthenticatorDescription) serviceInfo.type).type)) {
                    return isPermittedForPackage(((AuthenticatorDescription) serviceInfo.type).packageName, userId, "android.permission.WRITE_CONTACTS");
                }
            }
            return false;
        } catch (Throwable th) {
            Binder.restoreCallingIdentity(identityToken);
            throw th;
        }
    }

    /* JADX INFO: finally extract failed */
    private int checkPackageSignature(String accountType, int callingUid, int userId) {
        if (accountType == null) {
            return 0;
        }
        long identityToken = Binder.clearCallingIdentity();
        try {
            Collection<RegisteredServicesCache.ServiceInfo<AuthenticatorDescription>> serviceInfos = this.mAuthenticatorCache.getAllServices(userId);
            Binder.restoreCallingIdentity(identityToken);
            PackageManagerInternal pmi = (PackageManagerInternal) LocalServices.getService(PackageManagerInternal.class);
            for (RegisteredServicesCache.ServiceInfo<AuthenticatorDescription> serviceInfo : serviceInfos) {
                if (accountType.equals(((AuthenticatorDescription) serviceInfo.type).type)) {
                    if (serviceInfo.uid == callingUid) {
                        return 2;
                    }
                    if (pmi.hasSignatureCapability(serviceInfo.uid, callingUid, 16)) {
                        return 1;
                    }
                }
            }
            return 0;
        } catch (Throwable th) {
            Binder.restoreCallingIdentity(identityToken);
            throw th;
        }
    }

    private boolean isAccountManagedByCaller(String accountType, int callingUid, int userId) {
        if (accountType == null) {
            return false;
        }
        return getTypesManagedByCaller(callingUid, userId).contains(accountType);
    }

    private List<String> getTypesVisibleToCaller(int callingUid, int userId, String opPackageName) {
        return getTypesForCaller(callingUid, userId, true);
    }

    private List<String> getTypesManagedByCaller(int callingUid, int userId) {
        return getTypesForCaller(callingUid, userId, false);
    }

    /* JADX INFO: finally extract failed */
    private List<String> getTypesForCaller(int callingUid, int userId, boolean isOtherwisePermitted) {
        List<String> managedAccountTypes = new ArrayList<>();
        long identityToken = Binder.clearCallingIdentity();
        try {
            Collection<RegisteredServicesCache.ServiceInfo<AuthenticatorDescription>> serviceInfos = this.mAuthenticatorCache.getAllServices(userId);
            Binder.restoreCallingIdentity(identityToken);
            PackageManagerInternal pmi = (PackageManagerInternal) LocalServices.getService(PackageManagerInternal.class);
            for (RegisteredServicesCache.ServiceInfo<AuthenticatorDescription> serviceInfo : serviceInfos) {
                if (isOtherwisePermitted || pmi.hasSignatureCapability(serviceInfo.uid, callingUid, 16) || AccountManagerServiceInjector.isTrustedAccountSignature(this.mPackageManager, ((AuthenticatorDescription) serviceInfo.type).type, serviceInfo.uid, callingUid)) {
                    managedAccountTypes.add(((AuthenticatorDescription) serviceInfo.type).type);
                }
            }
            return managedAccountTypes;
        } catch (Throwable th) {
            Binder.restoreCallingIdentity(identityToken);
            throw th;
        }
    }

    /* access modifiers changed from: private */
    public boolean isAccountPresentForCaller(String accountName, String accountType) {
        if (getUserAccountsForCaller().accountCache.containsKey(accountType)) {
            for (Account account : getUserAccountsForCaller().accountCache.get(accountType)) {
                if (account.name.equals(accountName)) {
                    return true;
                }
            }
        }
        return false;
    }

    private static void checkManageUsersPermission(String message) {
        if (ActivityManager.checkComponentPermission("android.permission.MANAGE_USERS", Binder.getCallingUid(), -1, true) != 0) {
            throw new SecurityException("You need MANAGE_USERS permission to: " + message);
        }
    }

    private static void checkManageOrCreateUsersPermission(String message) {
        if (ActivityManager.checkComponentPermission("android.permission.MANAGE_USERS", Binder.getCallingUid(), -1, true) != 0 && ActivityManager.checkComponentPermission("android.permission.CREATE_USERS", Binder.getCallingUid(), -1, true) != 0) {
            throw new SecurityException("You need MANAGE_USERS or CREATE_USERS permission to: " + message);
        }
    }

    /* Debug info: failed to restart local var, previous not found, register: 10 */
    private boolean hasExplicitlyGrantedPermission(Account account, String authTokenType, int callerUid) {
        long grantsCount;
        if (UserHandle.getAppId(callerUid) == 1000) {
            return true;
        }
        UserAccounts accounts = getUserAccounts(UserHandle.getUserId(callerUid));
        synchronized (accounts.dbLock) {
            synchronized (accounts.cacheLock) {
                if (authTokenType != null) {
                    grantsCount = accounts.accountsDb.findMatchingGrantsCount(callerUid, authTokenType, account);
                } else {
                    grantsCount = accounts.accountsDb.findMatchingGrantsCountAnyToken(callerUid, account);
                }
                boolean permissionGranted = grantsCount > 0;
                if (permissionGranted || !ActivityManager.isRunningInTestHarness()) {
                    return permissionGranted;
                }
                Log.d(TAG, "no credentials permission for usage of " + account.toSafeString() + ", " + authTokenType + " by uid " + callerUid + " but ignoring since device is in test harness.");
                return true;
            }
        }
    }

    private boolean isSystemUid(int callingUid) {
        String name;
        long ident = Binder.clearCallingIdentity();
        try {
            String[] packages = this.mPackageManager.getPackagesForUid(callingUid);
            if (packages != null) {
                int length = packages.length;
                for (int i = 0; i < length; i++) {
                    name = packages[i];
                    PackageInfo packageInfo = this.mPackageManager.getPackageInfo(name, 0);
                    if (!(packageInfo == null || (packageInfo.applicationInfo.flags & 1) == 0)) {
                        Binder.restoreCallingIdentity(ident);
                        return true;
                    }
                }
            } else {
                Log.w(TAG, "No known packages with uid " + callingUid);
            }
            Binder.restoreCallingIdentity(ident);
            return false;
        } catch (PackageManager.NameNotFoundException e) {
            Log.w(TAG, String.format("Could not find package [%s]", new Object[]{name}), e);
        } catch (Throwable th) {
            Binder.restoreCallingIdentity(ident);
            throw th;
        }
    }

    private void checkReadAccountsPermitted(int callingUid, String accountType, int userId, String opPackageName) {
        if (!isAccountVisibleToCaller(accountType, callingUid, userId, opPackageName)) {
            String msg = String.format("caller uid %s cannot access %s accounts", new Object[]{Integer.valueOf(callingUid), accountType});
            Log.w(TAG, "  " + msg);
            throw new SecurityException(msg);
        }
    }

    private boolean canUserModifyAccounts(int userId, int callingUid) {
        if (!isProfileOwner(callingUid) && getUserManager().getUserRestrictions(new UserHandle(userId)).getBoolean("no_modify_accounts")) {
            return false;
        }
        return true;
    }

    private boolean canUserModifyAccountsForType(int userId, String accountType, int callingUid) {
        String[] typesArray;
        if (isProfileOwner(callingUid) || (typesArray = ((DevicePolicyManager) this.mContext.getSystemService("device_policy")).getAccountTypesWithManagementDisabledAsUser(userId)) == null) {
            return true;
        }
        for (String forbiddenType : typesArray) {
            if (forbiddenType.equals(accountType)) {
                return false;
            }
        }
        return true;
    }

    private boolean isProfileOwner(int uid) {
        DevicePolicyManagerInternal dpmi = (DevicePolicyManagerInternal) LocalServices.getService(DevicePolicyManagerInternal.class);
        return dpmi != null && dpmi.isActiveAdminWithPolicy(uid, -1);
    }

    public void updateAppPermission(Account account, String authTokenType, int uid, boolean value) throws RemoteException {
        if (UserHandle.getAppId(getCallingUid()) != 1000) {
            throw new SecurityException();
        } else if (value) {
            grantAppPermission(account, authTokenType, uid);
        } else {
            revokeAppPermission(account, authTokenType, uid);
        }
    }

    /* Debug info: failed to restart local var, previous not found, register: 7 */
    /* access modifiers changed from: package-private */
    public void grantAppPermission(Account account, String authTokenType, int uid) {
        if (account == null || authTokenType == null) {
            Log.e(TAG, "grantAppPermission: called with invalid arguments", new Exception());
            return;
        }
        UserAccounts accounts = getUserAccounts(UserHandle.getUserId(uid));
        synchronized (accounts.dbLock) {
            synchronized (accounts.cacheLock) {
                long accountId = accounts.accountsDb.findDeAccountId(account);
                if (accountId >= 0) {
                    accounts.accountsDb.insertGrant(accountId, authTokenType, uid);
                }
                cancelNotification(getCredentialPermissionNotificationId(account, authTokenType, uid), UserHandle.of(accounts.userId));
                cancelAccountAccessRequestNotificationIfNeeded(account, uid, true);
            }
        }
        Iterator<AccountManagerInternal.OnAppPermissionChangeListener> it = this.mAppPermissionChangeListeners.iterator();
        while (it.hasNext()) {
            this.mHandler.post(new Runnable(it.next(), account, uid) {
                private final /* synthetic */ AccountManagerInternal.OnAppPermissionChangeListener f$0;
                private final /* synthetic */ Account f$1;
                private final /* synthetic */ int f$2;

                {
                    this.f$0 = r1;
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void run() {
                    this.f$0.onAppPermissionChanged(this.f$1, this.f$2);
                }
            });
        }
    }

    /* Debug info: failed to restart local var, previous not found, register: 11 */
    /* JADX INFO: finally extract failed */
    private void revokeAppPermission(Account account, String authTokenType, int uid) {
        if (account == null || authTokenType == null) {
            Log.e(TAG, "revokeAppPermission: called with invalid arguments", new Exception());
            return;
        }
        UserAccounts accounts = getUserAccounts(UserHandle.getUserId(uid));
        synchronized (accounts.dbLock) {
            synchronized (accounts.cacheLock) {
                accounts.accountsDb.beginTransaction();
                try {
                    long accountId = accounts.accountsDb.findDeAccountId(account);
                    if (accountId >= 0) {
                        accounts.accountsDb.deleteGrantsByAccountIdAuthTokenTypeAndUid(accountId, authTokenType, (long) uid);
                        accounts.accountsDb.setTransactionSuccessful();
                    }
                    accounts.accountsDb.endTransaction();
                    cancelNotification(getCredentialPermissionNotificationId(account, authTokenType, uid), UserHandle.of(accounts.userId));
                } catch (Throwable th) {
                    accounts.accountsDb.endTransaction();
                    throw th;
                }
            }
        }
        Iterator<AccountManagerInternal.OnAppPermissionChangeListener> it = this.mAppPermissionChangeListeners.iterator();
        while (it.hasNext()) {
            this.mHandler.post(new Runnable(it.next(), account, uid) {
                private final /* synthetic */ AccountManagerInternal.OnAppPermissionChangeListener f$0;
                private final /* synthetic */ Account f$1;
                private final /* synthetic */ int f$2;

                {
                    this.f$0 = r1;
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void run() {
                    this.f$0.onAppPermissionChanged(this.f$1, this.f$2);
                }
            });
        }
    }

    private void removeAccountFromCacheLocked(UserAccounts accounts, Account account) {
        Account[] oldAccountsForType = accounts.accountCache.get(account.type);
        if (oldAccountsForType != null) {
            ArrayList<Account> newAccountsList = new ArrayList<>();
            for (Account curAccount : oldAccountsForType) {
                if (!curAccount.equals(account)) {
                    newAccountsList.add(curAccount);
                }
            }
            if (newAccountsList.isEmpty()) {
                accounts.accountCache.remove(account.type);
            } else {
                accounts.accountCache.put(account.type, (Account[]) newAccountsList.toArray(new Account[newAccountsList.size()]));
            }
        }
        accounts.userDataCache.remove(account);
        accounts.authTokenCache.remove(account);
        accounts.previousNameCache.remove(account);
        accounts.visibilityCache.remove(account);
    }

    private Account insertAccountIntoCacheLocked(UserAccounts accounts, Account account) {
        String token;
        Account[] accountsForType = accounts.accountCache.get(account.type);
        int oldLength = accountsForType != null ? accountsForType.length : 0;
        Account[] newAccountsForType = new Account[(oldLength + 1)];
        if (accountsForType != null) {
            System.arraycopy(accountsForType, 0, newAccountsForType, 0, oldLength);
        }
        if (account.getAccessId() != null) {
            token = account.getAccessId();
        } else {
            token = UUID.randomUUID().toString();
        }
        newAccountsForType[oldLength] = new Account(account, token);
        accounts.accountCache.put(account.type, newAccountsForType);
        return newAccountsForType[oldLength];
    }

    private Account[] filterAccounts(UserAccounts accounts, Account[] unfiltered, int callingUid, String callingPackage, boolean includeManagedNotVisible) {
        String visibilityFilterPackage = callingPackage;
        if (visibilityFilterPackage == null) {
            visibilityFilterPackage = getPackageNameForUid(callingUid);
        }
        Map<Account, Integer> firstPass = new LinkedHashMap<>();
        for (Account account : unfiltered) {
            int visibility = resolveAccountVisibility(account, visibilityFilterPackage, accounts).intValue();
            if (visibility == 1 || visibility == 2 || (includeManagedNotVisible && visibility == 4)) {
                firstPass.put(account, Integer.valueOf(visibility));
            }
        }
        Map<Account, Integer> secondPass = filterSharedAccounts(accounts, firstPass, callingUid, callingPackage);
        return (Account[]) secondPass.keySet().toArray(new Account[secondPass.size()]);
    }

    private Map<Account, Integer> filterSharedAccounts(UserAccounts userAccounts, Map<Account, Integer> unfiltered, int callingUid, String callingPackage) {
        UserInfo user;
        String[] packages;
        int i = callingUid;
        String str = callingPackage;
        if (getUserManager() == null || userAccounts == null || userAccounts.userId < 0 || i == 1000 || (user = getUserManager().getUserInfo(userAccounts.userId)) == null || !user.isRestricted()) {
            return unfiltered;
        }
        String[] packages2 = this.mPackageManager.getPackagesForUid(i);
        int i2 = 0;
        if (packages2 == null) {
            packages = new String[0];
        } else {
            packages = packages2;
        }
        String visibleList = this.mContext.getResources().getString(17039694);
        for (String packageName : packages) {
            if (visibleList.contains(";" + packageName + ";")) {
                return unfiltered;
            }
        }
        Account[] sharedAccounts = getSharedAccountsAsUser(userAccounts.userId);
        if (ArrayUtils.isEmpty(sharedAccounts)) {
            return unfiltered;
        }
        String requiredAccountType = "";
        if (str == null) {
            int length = packages.length;
            int i3 = 0;
            while (true) {
                if (i3 < length) {
                    PackageInfo pi = this.mPackageManager.getPackageInfo(packages[i3], 0);
                    if (pi != null && pi.restrictedAccountType != null) {
                        requiredAccountType = pi.restrictedAccountType;
                        break;
                    }
                    i3++;
                } else {
                    break;
                }
            }
        } else {
            try {
                PackageInfo pi2 = this.mPackageManager.getPackageInfo(str, 0);
                if (!(pi2 == null || pi2.restrictedAccountType == null)) {
                    requiredAccountType = pi2.restrictedAccountType;
                }
            } catch (PackageManager.NameNotFoundException e) {
                Log.d(TAG, "Package not found " + e.getMessage());
            }
        }
        Map<Account, Integer> filtered = new LinkedHashMap<>();
        for (Map.Entry<Account, Integer> entry : unfiltered.entrySet()) {
            Account account = entry.getKey();
            if (account.type.equals(requiredAccountType)) {
                filtered.put(account, entry.getValue());
            } else {
                boolean found = false;
                int length2 = sharedAccounts.length;
                int i4 = i2;
                while (true) {
                    if (i4 >= length2) {
                        break;
                    } else if (sharedAccounts[i4].equals(account)) {
                        found = true;
                        break;
                    } else {
                        i4++;
                    }
                }
                if (!found) {
                    filtered.put(account, entry.getValue());
                }
            }
            i2 = 0;
        }
        return filtered;
    }

    /* access modifiers changed from: protected */
    public Account[] getAccountsFromCache(UserAccounts userAccounts, String accountType, int callingUid, String callingPackage, boolean includeManagedNotVisible) {
        Account[] accounts;
        Preconditions.checkState(!Thread.holdsLock(userAccounts.cacheLock), "Method should not be called with cacheLock");
        if (accountType != null) {
            synchronized (userAccounts.cacheLock) {
                accounts = userAccounts.accountCache.get(accountType);
            }
            if (accounts == null) {
                return EMPTY_ACCOUNT_ARRAY;
            }
            return filterAccounts(userAccounts, (Account[]) Arrays.copyOf(accounts, accounts.length), callingUid, callingPackage, includeManagedNotVisible);
        }
        int totalLength = 0;
        synchronized (userAccounts.cacheLock) {
            for (Account[] accounts2 : userAccounts.accountCache.values()) {
                totalLength += accounts2.length;
            }
            if (totalLength == 0) {
                Account[] accountArr = EMPTY_ACCOUNT_ARRAY;
                return accountArr;
            }
            Account[] accountsArray = new Account[totalLength];
            int totalLength2 = 0;
            for (Account[] accountsOfType : userAccounts.accountCache.values()) {
                System.arraycopy(accountsOfType, 0, accountsArray, totalLength2, accountsOfType.length);
                totalLength2 += accountsOfType.length;
            }
            return filterAccounts(userAccounts, accountsArray, callingUid, callingPackage, includeManagedNotVisible);
        }
    }

    /* access modifiers changed from: protected */
    public void writeUserDataIntoCacheLocked(UserAccounts accounts, Account account, String key, String value) {
        Map<String, String> userDataForAccount = (Map) accounts.userDataCache.get(account);
        if (userDataForAccount == null) {
            userDataForAccount = accounts.accountsDb.findUserExtrasForAccount(account);
            accounts.userDataCache.put(account, userDataForAccount);
        }
        if (value == null) {
            userDataForAccount.remove(key);
        } else {
            userDataForAccount.put(key, value);
        }
    }

    /* access modifiers changed from: protected */
    public String readCachedTokenInternal(UserAccounts accounts, Account account, String tokenType, String callingPackage, byte[] pkgSigDigest) {
        String str;
        synchronized (accounts.cacheLock) {
            str = accounts.accountTokenCaches.get(account, tokenType, callingPackage, pkgSigDigest);
        }
        return str;
    }

    /* access modifiers changed from: protected */
    public void writeAuthTokenIntoCacheLocked(UserAccounts accounts, Account account, String key, String value) {
        Map<String, String> authTokensForAccount = (Map) accounts.authTokenCache.get(account);
        if (authTokensForAccount == null) {
            authTokensForAccount = accounts.accountsDb.findAuthTokensByAccount(account);
            accounts.authTokenCache.put(account, authTokensForAccount);
        }
        if (value == null) {
            authTokensForAccount.remove(key);
        } else {
            authTokensForAccount.put(key, value);
        }
    }

    /* Debug info: failed to restart local var, previous not found, register: 4 */
    /* access modifiers changed from: protected */
    /* JADX WARNING: Code restructure failed: missing block: B:10:0x001a, code lost:
        monitor-enter(r1);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:12:?, code lost:
        r0 = r5.cacheLock;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:13:0x001d, code lost:
        monitor-enter(r0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:15:?, code lost:
        r2 = (java.util.Map) com.android.server.accounts.AccountManagerService.UserAccounts.access$1200(r5).get(r6);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:16:0x0028, code lost:
        if (r2 != null) goto L_0x0038;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:17:0x002a, code lost:
        r2 = r5.accountsDb.findAuthTokensByAccount(r6);
        com.android.server.accounts.AccountManagerService.UserAccounts.access$1200(r5).put(r6, r2);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:18:0x0038, code lost:
        r3 = r2.get(r7);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:19:0x003e, code lost:
        monitor-exit(r0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:21:?, code lost:
        monitor-exit(r1);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:22:0x0040, code lost:
        return r3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:9:0x0018, code lost:
        r1 = r5.dbLock;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public java.lang.String readAuthTokenInternal(com.android.server.accounts.AccountManagerService.UserAccounts r5, android.accounts.Account r6, java.lang.String r7) {
        /*
            r4 = this;
            java.lang.Object r0 = r5.cacheLock
            monitor-enter(r0)
            java.util.Map r1 = r5.authTokenCache     // Catch:{ all -> 0x0047 }
            java.lang.Object r1 = r1.get(r6)     // Catch:{ all -> 0x0047 }
            java.util.Map r1 = (java.util.Map) r1     // Catch:{ all -> 0x0047 }
            if (r1 == 0) goto L_0x0017
            java.lang.Object r2 = r1.get(r7)     // Catch:{ all -> 0x0047 }
            java.lang.String r2 = (java.lang.String) r2     // Catch:{ all -> 0x0047 }
            monitor-exit(r0)     // Catch:{ all -> 0x0047 }
            return r2
        L_0x0017:
            monitor-exit(r0)     // Catch:{ all -> 0x0047 }
            java.lang.Object r1 = r5.dbLock
            monitor-enter(r1)
            java.lang.Object r0 = r5.cacheLock     // Catch:{ all -> 0x0044 }
            monitor-enter(r0)     // Catch:{ all -> 0x0044 }
            java.util.Map r2 = r5.authTokenCache     // Catch:{ all -> 0x0041 }
            java.lang.Object r2 = r2.get(r6)     // Catch:{ all -> 0x0041 }
            java.util.Map r2 = (java.util.Map) r2     // Catch:{ all -> 0x0041 }
            if (r2 != 0) goto L_0x0038
            com.android.server.accounts.AccountsDb r3 = r5.accountsDb     // Catch:{ all -> 0x0041 }
            java.util.Map r3 = r3.findAuthTokensByAccount(r6)     // Catch:{ all -> 0x0041 }
            r2 = r3
            java.util.Map r3 = r5.authTokenCache     // Catch:{ all -> 0x0041 }
            r3.put(r6, r2)     // Catch:{ all -> 0x0041 }
        L_0x0038:
            java.lang.Object r3 = r2.get(r7)     // Catch:{ all -> 0x0041 }
            java.lang.String r3 = (java.lang.String) r3     // Catch:{ all -> 0x0041 }
            monitor-exit(r0)     // Catch:{ all -> 0x0041 }
            monitor-exit(r1)     // Catch:{ all -> 0x0044 }
            return r3
        L_0x0041:
            r2 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x0041 }
            throw r2     // Catch:{ all -> 0x0044 }
        L_0x0044:
            r0 = move-exception
            monitor-exit(r1)     // Catch:{ all -> 0x0044 }
            throw r0
        L_0x0047:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x0047 }
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.accounts.AccountManagerService.readAuthTokenInternal(com.android.server.accounts.AccountManagerService$UserAccounts, android.accounts.Account, java.lang.String):java.lang.String");
    }

    /* Debug info: failed to restart local var, previous not found, register: 4 */
    private String readUserDataInternal(UserAccounts accounts, Account account, String key) {
        Map<String, String> userDataForAccount;
        synchronized (accounts.cacheLock) {
            userDataForAccount = (Map) accounts.userDataCache.get(account);
        }
        if (userDataForAccount == null) {
            synchronized (accounts.dbLock) {
                synchronized (accounts.cacheLock) {
                    userDataForAccount = (Map) accounts.userDataCache.get(account);
                    if (userDataForAccount == null) {
                        userDataForAccount = accounts.accountsDb.findUserExtrasForAccount(account);
                        accounts.userDataCache.put(account, userDataForAccount);
                    }
                }
            }
        }
        return userDataForAccount.get(key);
    }

    private Context getContextForUser(UserHandle user) {
        try {
            return this.mContext.createPackageContextAsUser(this.mContext.getPackageName(), 0, user);
        } catch (PackageManager.NameNotFoundException e) {
            return this.mContext;
        }
    }

    /* access modifiers changed from: private */
    public void sendResponse(IAccountManagerResponse response, Bundle result) {
        try {
            response.onResult(result);
        } catch (RemoteException e) {
            if (Log.isLoggable(TAG, 2)) {
                Log.v(TAG, "failure while notifying response", e);
            }
        }
    }

    /* access modifiers changed from: private */
    public void sendErrorResponse(IAccountManagerResponse response, int errorCode, String errorMessage) {
        try {
            response.onError(errorCode, errorMessage);
        } catch (RemoteException e) {
            if (Log.isLoggable(TAG, 2)) {
                Log.v(TAG, "failure while notifying response", e);
            }
        }
    }

    private final class AccountManagerInternalImpl extends AccountManagerInternal {
        @GuardedBy({"mLock"})
        private AccountManagerBackupHelper mBackupHelper;
        private final Object mLock;

        private AccountManagerInternalImpl() {
            this.mLock = new Object();
        }

        /* Debug info: failed to restart local var, previous not found, register: 11 */
        public void requestAccountAccess(Account account, String packageName, int userId, RemoteCallback callback) {
            long identityToken;
            UserAccounts userAccounts;
            if (account == null) {
                Slog.w(AccountManagerService.TAG, "account cannot be null");
            } else if (packageName == null) {
                Slog.w(AccountManagerService.TAG, "packageName cannot be null");
            } else if (userId < 0) {
                Slog.w(AccountManagerService.TAG, "user id must be concrete");
            } else if (callback == null) {
                Slog.w(AccountManagerService.TAG, "callback cannot be null");
            } else {
                AccountManagerService accountManagerService = AccountManagerService.this;
                if (accountManagerService.resolveAccountVisibility(account, packageName, accountManagerService.getUserAccounts(userId)).intValue() == 3) {
                    Slog.w(AccountManagerService.TAG, "requestAccountAccess: account is hidden");
                } else if (AccountManagerService.this.hasAccountAccess(account, packageName, new UserHandle(userId))) {
                    Bundle result = new Bundle();
                    result.putBoolean("booleanResult", true);
                    callback.sendResult(result);
                } else {
                    try {
                        identityToken = Binder.clearCallingIdentity();
                        int uid = AccountManagerService.this.mPackageManager.getPackageUidAsUser(packageName, userId);
                        Binder.restoreCallingIdentity(identityToken);
                        Intent intent = AccountManagerService.this.newRequestAccountAccessIntent(account, packageName, uid, callback);
                        synchronized (AccountManagerService.this.mUsers) {
                            userAccounts = (UserAccounts) AccountManagerService.this.mUsers.get(userId);
                        }
                        SystemNotificationChannels.createAccountChannelForPackage(packageName, uid, AccountManagerService.this.mContext);
                        AccountManagerService.this.doNotification(userAccounts, account, (CharSequence) null, intent, packageName, userId);
                    } catch (PackageManager.NameNotFoundException e) {
                        Slog.e(AccountManagerService.TAG, "Unknown package " + packageName);
                    } catch (Throwable th) {
                        Binder.restoreCallingIdentity(identityToken);
                        throw th;
                    }
                }
            }
        }

        public void addOnAppPermissionChangeListener(AccountManagerInternal.OnAppPermissionChangeListener listener) {
            AccountManagerService.this.mAppPermissionChangeListeners.add(listener);
        }

        public boolean hasAccountAccess(Account account, int uid) {
            return AccountManagerService.this.hasAccountAccess(account, (String) null, uid);
        }

        public byte[] backupAccountAccessPermissions(int userId) {
            byte[] backupAccountAccessPermissions;
            synchronized (this.mLock) {
                if (this.mBackupHelper == null) {
                    this.mBackupHelper = new AccountManagerBackupHelper(AccountManagerService.this, this);
                }
                backupAccountAccessPermissions = this.mBackupHelper.backupAccountAccessPermissions(userId);
            }
            return backupAccountAccessPermissions;
        }

        public void restoreAccountAccessPermissions(byte[] data, int userId) {
            synchronized (this.mLock) {
                if (this.mBackupHelper == null) {
                    this.mBackupHelper = new AccountManagerBackupHelper(AccountManagerService.this, this);
                }
                this.mBackupHelper.restoreAccountAccessPermissions(data, userId);
            }
        }
    }

    @VisibleForTesting
    static class Injector {
        private final Context mContext;

        public Injector(Context context) {
            this.mContext = context;
        }

        /* access modifiers changed from: package-private */
        public Looper getMessageHandlerLooper() {
            ServiceThread serviceThread = new ServiceThread(AccountManagerService.TAG, -2, true);
            serviceThread.start();
            return serviceThread.getLooper();
        }

        /* access modifiers changed from: package-private */
        public Context getContext() {
            return this.mContext;
        }

        /* access modifiers changed from: package-private */
        public void addLocalService(AccountManagerInternal service) {
            LocalServices.addService(AccountManagerInternal.class, service);
        }

        /* access modifiers changed from: package-private */
        public String getDeDatabaseName(int userId) {
            return new File(Environment.getDataSystemDeDirectory(userId), "accounts_de.db").getPath();
        }

        /* access modifiers changed from: package-private */
        public String getCeDatabaseName(int userId) {
            return new File(Environment.getDataSystemCeDirectory(userId), "accounts_ce.db").getPath();
        }

        /* access modifiers changed from: package-private */
        public String getPreNDatabaseName(int userId) {
            File systemDir = Environment.getDataSystemDirectory();
            File databaseFile = new File(Environment.getUserSystemDirectory(userId), AccountManagerService.PRE_N_DATABASE_NAME);
            if (userId == 0) {
                File oldFile = new File(systemDir, AccountManagerService.PRE_N_DATABASE_NAME);
                if (oldFile.exists() && !databaseFile.exists()) {
                    File userDir = Environment.getUserSystemDirectory(userId);
                    if (!userDir.exists() && !userDir.mkdirs()) {
                        throw new IllegalStateException("User dir cannot be created: " + userDir);
                    } else if (!oldFile.renameTo(databaseFile)) {
                        throw new IllegalStateException("User dir cannot be migrated: " + databaseFile);
                    }
                }
            }
            return databaseFile.getPath();
        }

        /* access modifiers changed from: package-private */
        public IAccountAuthenticatorCache getAccountAuthenticatorCache() {
            return new AccountAuthenticatorCache(this.mContext);
        }

        /* access modifiers changed from: package-private */
        public INotificationManager getNotificationManager() {
            return NotificationManager.getService();
        }
    }

    private static class NotificationId {
        /* access modifiers changed from: private */
        public final int mId;
        final String mTag;

        NotificationId(String tag, int type) {
            this.mTag = tag;
            this.mId = type;
        }
    }
}
