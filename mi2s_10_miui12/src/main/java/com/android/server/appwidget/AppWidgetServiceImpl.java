package com.android.server.appwidget;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.AppGlobals;
import android.app.AppOpsManager;
import android.app.IApplicationThread;
import android.app.IServiceConnection;
import android.app.KeyguardManager;
import android.app.PendingIntent;
import android.app.admin.DevicePolicyManagerInternal;
import android.appwidget.AppWidgetManagerInternal;
import android.appwidget.AppWidgetProviderInfo;
import android.appwidget.PendingHostUpdate;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.ServiceConnection;
import android.content.pm.ActivityInfo;
import android.content.pm.IPackageManager;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManagerInternal;
import android.content.pm.ParceledListSlice;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.content.pm.ShortcutServiceInternal;
import android.content.pm.UserInfo;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Binder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.os.RemoteException;
import android.os.SystemClock;
import android.os.Trace;
import android.os.UserHandle;
import android.os.UserManager;
import android.server.am.SplitScreenReporter;
import android.text.TextUtils;
import android.util.ArraySet;
import android.util.AtomicFile;
import android.util.LongSparseArray;
import android.util.Pair;
import android.util.Slog;
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.util.SparseLongArray;
import android.util.Xml;
import android.util.proto.ProtoOutputStream;
import android.view.Display;
import android.view.WindowManager;
import android.widget.RemoteViews;
import com.android.internal.appwidget.IAppWidgetHost;
import com.android.internal.appwidget.IAppWidgetService;
import com.android.internal.os.BackgroundThread;
import com.android.internal.os.SomeArgs;
import com.android.internal.util.ArrayUtils;
import com.android.internal.util.DumpUtils;
import com.android.internal.util.FastXmlSerializer;
import com.android.internal.widget.IRemoteViewsFactory;
import com.android.server.LocalServices;
import com.android.server.WidgetBackupProvider;
import com.android.server.pm.DumpState;
import com.android.server.pm.Settings;
import com.android.server.policy.IconUtilities;
import com.android.server.utils.PriorityDump;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

class AppWidgetServiceImpl extends IAppWidgetService.Stub implements WidgetBackupProvider, DevicePolicyManagerInternal.OnCrossProfileWidgetProvidersChangeListener {
    private static final int CURRENT_VERSION = 1;
    /* access modifiers changed from: private */
    public static boolean DEBUG = false;
    private static final int ID_PROVIDER_CHANGED = 1;
    private static final int ID_VIEWS_UPDATE = 0;
    private static final int KEYGUARD_HOST_ID = 1262836039;
    private static final int LOADED_PROFILE_ID = -1;
    private static final int MIN_UPDATE_PERIOD;
    private static final String NEW_KEYGUARD_HOST_PACKAGE = "com.android.keyguard";
    private static final String OLD_KEYGUARD_HOST_PACKAGE = "android";
    private static final String STATE_FILENAME = "appwidgets.xml";
    private static final String TAG = "AppWidgetServiceImpl";
    private static final int TAG_UNDEFINED = -1;
    private static final int UNKNOWN_UID = -1;
    private static final int UNKNOWN_USER_ID = -10;
    private static final AtomicLong UPDATE_COUNTER = new AtomicLong();
    private AlarmManager mAlarmManager;
    /* access modifiers changed from: private */
    public AppOpsManager mAppOpsManager;
    private BackupRestoreController mBackupRestoreController;
    private final BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            int userId = intent.getIntExtra("android.intent.extra.user_handle", ScreenRotationAnimationInjector.BLACK_SURFACE_INVALID_POSITION);
            if (AppWidgetServiceImpl.DEBUG) {
                Slog.i(AppWidgetServiceImpl.TAG, "Received broadcast: " + action + " on user " + userId);
            }
            char c = 65535;
            switch (action.hashCode()) {
                case -1238404651:
                    if (action.equals("android.intent.action.MANAGED_PROFILE_UNAVAILABLE")) {
                        c = 2;
                        break;
                    }
                    break;
                case -1001645458:
                    if (action.equals("android.intent.action.PACKAGES_SUSPENDED")) {
                        c = 3;
                        break;
                    }
                    break;
                case -864107122:
                    if (action.equals("android.intent.action.MANAGED_PROFILE_AVAILABLE")) {
                        c = 1;
                        break;
                    }
                    break;
                case 158859398:
                    if (action.equals("android.intent.action.CONFIGURATION_CHANGED")) {
                        c = 0;
                        break;
                    }
                    break;
                case 1290767157:
                    if (action.equals("android.intent.action.PACKAGES_UNSUSPENDED")) {
                        c = 4;
                        break;
                    }
                    break;
            }
            if (c == 0) {
                AppWidgetServiceImpl.this.onConfigurationChanged();
            } else if (c == 1 || c == 2) {
                synchronized (AppWidgetServiceImpl.this.mLock) {
                    AppWidgetServiceImpl.this.reloadWidgetsMaskedState(userId);
                }
            } else if (c == 3) {
                AppWidgetServiceImpl.this.onPackageBroadcastReceived(intent, getSendingUserId());
                AppWidgetServiceImpl.this.updateWidgetPackageSuspensionMaskedState(intent, true, getSendingUserId());
            } else if (c != 4) {
                AppWidgetServiceImpl.this.onPackageBroadcastReceived(intent, getSendingUserId());
            } else {
                AppWidgetServiceImpl.this.onPackageBroadcastReceived(intent, getSendingUserId());
                AppWidgetServiceImpl.this.updateWidgetPackageSuspensionMaskedState(intent, false, getSendingUserId());
            }
        }
    };
    private Handler mCallbackHandler;
    /* access modifiers changed from: private */
    public final Context mContext;
    /* access modifiers changed from: private */
    public DevicePolicyManagerInternal mDevicePolicyManagerInternal;
    /* access modifiers changed from: private */
    public final ArrayList<Host> mHosts = new ArrayList<>();
    private IconUtilities mIconUtilities;
    private KeyguardManager mKeyguardManager;
    private final SparseIntArray mLoadedUserIds = new SparseIntArray();
    private Locale mLocale;
    /* access modifiers changed from: private */
    public final Object mLock = new Object();
    private int mMaxWidgetBitmapMemory;
    private final SparseIntArray mNextAppWidgetIds = new SparseIntArray();
    /* access modifiers changed from: private */
    public IPackageManager mPackageManager;
    private PackageManagerInternal mPackageManagerInternal;
    /* access modifiers changed from: private */
    public final ArraySet<Pair<Integer, String>> mPackagesWithBindWidgetPermission = new ArraySet<>();
    /* access modifiers changed from: private */
    public final ArrayList<Provider> mProviders = new ArrayList<>();
    private final HashMap<Pair<Integer, Intent.FilterComparison>, HashSet<Integer>> mRemoteViewsServicesAppWidgets = new HashMap<>();
    private boolean mSafeMode;
    private Handler mSaveStateHandler;
    /* access modifiers changed from: private */
    public SecurityPolicy mSecurityPolicy;
    /* access modifiers changed from: private */
    public UserManager mUserManager;
    private final SparseArray<ArraySet<String>> mWidgetPackages = new SparseArray<>();
    /* access modifiers changed from: private */
    public final ArrayList<Widget> mWidgets = new ArrayList<>();

    static {
        int i = 0;
        if (!DEBUG) {
            i = 1800000;
        }
        MIN_UPDATE_PERIOD = i;
    }

    AppWidgetServiceImpl(Context context) {
        this.mContext = context;
    }

    public void onStart() {
        this.mPackageManager = AppGlobals.getPackageManager();
        this.mAlarmManager = (AlarmManager) this.mContext.getSystemService("alarm");
        this.mUserManager = (UserManager) this.mContext.getSystemService("user");
        this.mAppOpsManager = (AppOpsManager) this.mContext.getSystemService("appops");
        this.mKeyguardManager = (KeyguardManager) this.mContext.getSystemService("keyguard");
        this.mDevicePolicyManagerInternal = (DevicePolicyManagerInternal) LocalServices.getService(DevicePolicyManagerInternal.class);
        this.mPackageManagerInternal = (PackageManagerInternal) LocalServices.getService(PackageManagerInternal.class);
        this.mSaveStateHandler = BackgroundThread.getHandler();
        this.mCallbackHandler = new CallbackHandler(this.mContext.getMainLooper());
        this.mBackupRestoreController = new BackupRestoreController();
        this.mSecurityPolicy = new SecurityPolicy();
        this.mIconUtilities = new IconUtilities(this.mContext);
        computeMaximumWidgetBitmapMemory();
        registerBroadcastReceiver();
        registerOnCrossProfileProvidersChangedListener();
        LocalServices.addService(AppWidgetManagerInternal.class, new AppWidgetManagerLocal());
    }

    private void computeMaximumWidgetBitmapMemory() {
        Display display = ((WindowManager) this.mContext.getSystemService("window")).getDefaultDisplay();
        Point size = new Point();
        display.getRealSize(size);
        this.mMaxWidgetBitmapMemory = size.x * 6 * size.y;
    }

    private void registerBroadcastReceiver() {
        IntentFilter configFilter = new IntentFilter();
        configFilter.addAction("android.intent.action.CONFIGURATION_CHANGED");
        this.mContext.registerReceiverAsUser(this.mBroadcastReceiver, UserHandle.ALL, configFilter, (String) null, (Handler) null);
        IntentFilter packageFilter = new IntentFilter();
        packageFilter.addAction("android.intent.action.PACKAGE_ADDED");
        packageFilter.addAction("android.intent.action.PACKAGE_CHANGED");
        packageFilter.addAction("android.intent.action.PACKAGE_REMOVED");
        packageFilter.addDataScheme(Settings.ATTR_PACKAGE);
        this.mContext.registerReceiverAsUser(this.mBroadcastReceiver, UserHandle.ALL, packageFilter, (String) null, (Handler) null);
        IntentFilter sdFilter = new IntentFilter();
        sdFilter.addAction("android.intent.action.EXTERNAL_APPLICATIONS_AVAILABLE");
        sdFilter.addAction("android.intent.action.EXTERNAL_APPLICATIONS_UNAVAILABLE");
        this.mContext.registerReceiverAsUser(this.mBroadcastReceiver, UserHandle.ALL, sdFilter, (String) null, (Handler) null);
        IntentFilter offModeFilter = new IntentFilter();
        offModeFilter.addAction("android.intent.action.MANAGED_PROFILE_AVAILABLE");
        offModeFilter.addAction("android.intent.action.MANAGED_PROFILE_UNAVAILABLE");
        this.mContext.registerReceiverAsUser(this.mBroadcastReceiver, UserHandle.ALL, offModeFilter, (String) null, (Handler) null);
        IntentFilter suspendPackageFilter = new IntentFilter();
        suspendPackageFilter.addAction("android.intent.action.PACKAGES_SUSPENDED");
        suspendPackageFilter.addAction("android.intent.action.PACKAGES_UNSUSPENDED");
        this.mContext.registerReceiverAsUser(this.mBroadcastReceiver, UserHandle.ALL, suspendPackageFilter, (String) null, (Handler) null);
    }

    private void registerOnCrossProfileProvidersChangedListener() {
        DevicePolicyManagerInternal devicePolicyManagerInternal = this.mDevicePolicyManagerInternal;
        if (devicePolicyManagerInternal != null) {
            devicePolicyManagerInternal.addOnCrossProfileWidgetProvidersChangeListener(this);
        }
    }

    public void setSafeMode(boolean safeMode) {
        this.mSafeMode = safeMode;
    }

    /* access modifiers changed from: private */
    public void onConfigurationChanged() {
        Locale locale;
        if (DEBUG) {
            Slog.i(TAG, "onConfigurationChanged()");
        }
        Locale revised = Locale.getDefault();
        if (revised == null || (locale = this.mLocale) == null || !revised.equals(locale)) {
            this.mLocale = revised;
            synchronized (this.mLock) {
                SparseIntArray changedGroups = null;
                ArrayList<Provider> installedProviders = new ArrayList<>(this.mProviders);
                HashSet<ProviderId> removedProviders = new HashSet<>();
                for (int i = installedProviders.size() - 1; i >= 0; i--) {
                    Provider provider = installedProviders.get(i);
                    int userId = provider.getUserId();
                    if (this.mUserManager.isUserUnlockingOrUnlocked(userId)) {
                        if (!isProfileWithLockedParent(userId)) {
                            ensureGroupStateLoadedLocked(userId);
                            if (!removedProviders.contains(provider.id) && updateProvidersForPackageLocked(provider.id.componentName.getPackageName(), provider.getUserId(), removedProviders)) {
                                if (changedGroups == null) {
                                    changedGroups = new SparseIntArray();
                                }
                                int groupId = this.mSecurityPolicy.getGroupParent(provider.getUserId());
                                changedGroups.put(groupId, groupId);
                            }
                        }
                    }
                }
                if (changedGroups != null) {
                    int groupCount = changedGroups.size();
                    for (int i2 = 0; i2 < groupCount; i2++) {
                        saveGroupStateAsync(changedGroups.get(i2));
                    }
                }
            }
        }
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Can't fix incorrect switch cases order */
    /* JADX WARNING: Code restructure failed: missing block: B:71:0x00e8, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:73:0x00ea, code lost:
        return;
     */
    /* JADX WARNING: Removed duplicated region for block: B:69:0x00e1  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onPackageBroadcastReceived(android.content.Intent r13, int r14) {
        /*
            r12 = this;
            java.lang.String r0 = r13.getAction()
            r1 = 0
            r2 = 0
            r3 = 0
            int r4 = r0.hashCode()
            r5 = 3
            r6 = 2
            r7 = 1
            r8 = 0
            switch(r4) {
                case -1403934493: goto L_0x0031;
                case -1338021860: goto L_0x0027;
                case -1001645458: goto L_0x001d;
                case 1290767157: goto L_0x0013;
                default: goto L_0x0012;
            }
        L_0x0012:
            goto L_0x003b
        L_0x0013:
            java.lang.String r4 = "android.intent.action.PACKAGES_UNSUSPENDED"
            boolean r4 = r0.equals(r4)
            if (r4 == 0) goto L_0x0012
            r4 = r7
            goto L_0x003c
        L_0x001d:
            java.lang.String r4 = "android.intent.action.PACKAGES_SUSPENDED"
            boolean r4 = r0.equals(r4)
            if (r4 == 0) goto L_0x0012
            r4 = r8
            goto L_0x003c
        L_0x0027:
            java.lang.String r4 = "android.intent.action.EXTERNAL_APPLICATIONS_AVAILABLE"
            boolean r4 = r0.equals(r4)
            if (r4 == 0) goto L_0x0012
            r4 = r6
            goto L_0x003c
        L_0x0031:
            java.lang.String r4 = "android.intent.action.EXTERNAL_APPLICATIONS_UNAVAILABLE"
            boolean r4 = r0.equals(r4)
            if (r4 == 0) goto L_0x0012
            r4 = r5
            goto L_0x003c
        L_0x003b:
            r4 = -1
        L_0x003c:
            if (r4 == 0) goto L_0x006b
            if (r4 == r7) goto L_0x006b
            if (r4 == r6) goto L_0x0063
            if (r4 == r5) goto L_0x0064
            android.net.Uri r4 = r13.getData()
            if (r4 != 0) goto L_0x004b
            return
        L_0x004b:
            java.lang.String r5 = r4.getSchemeSpecificPart()
            if (r5 != 0) goto L_0x0052
            return
        L_0x0052:
            java.lang.String[] r6 = new java.lang.String[r7]
            r6[r8] = r5
            java.lang.String r9 = "android.intent.action.PACKAGE_ADDED"
            boolean r1 = r9.equals(r0)
            java.lang.String r9 = "android.intent.action.PACKAGE_CHANGED"
            boolean r2 = r9.equals(r0)
            goto L_0x0073
        L_0x0063:
            r1 = 1
        L_0x0064:
            java.lang.String r4 = "android.intent.extra.changed_package_list"
            java.lang.String[] r6 = r13.getStringArrayExtra(r4)
            goto L_0x0073
        L_0x006b:
            java.lang.String r4 = "android.intent.extra.changed_package_list"
            java.lang.String[] r6 = r13.getStringArrayExtra(r4)
            r2 = 1
        L_0x0073:
            if (r6 == 0) goto L_0x00ee
            int r4 = r6.length
            if (r4 != 0) goto L_0x007a
            goto L_0x00ee
        L_0x007a:
            java.lang.Object r4 = r12.mLock
            monitor-enter(r4)
            android.os.UserManager r5 = r12.mUserManager     // Catch:{ all -> 0x00eb }
            boolean r5 = r5.isUserUnlockingOrUnlocked(r14)     // Catch:{ all -> 0x00eb }
            if (r5 == 0) goto L_0x00e9
            boolean r5 = r12.isProfileWithLockedParent(r14)     // Catch:{ all -> 0x00eb }
            if (r5 == 0) goto L_0x008c
            goto L_0x00e9
        L_0x008c:
            r12.ensureGroupStateLoadedLocked(r14, r8)     // Catch:{ all -> 0x00eb }
            android.os.Bundle r5 = r13.getExtras()     // Catch:{ all -> 0x00eb }
            if (r1 != 0) goto L_0x00b5
            if (r2 == 0) goto L_0x0098
            goto L_0x00b5
        L_0x0098:
            if (r5 == 0) goto L_0x00a5
            java.lang.String r9 = "android.intent.extra.REPLACING"
            boolean r9 = r5.getBoolean(r9, r8)     // Catch:{ all -> 0x00eb }
            if (r9 != 0) goto L_0x00a3
            goto L_0x00a5
        L_0x00a3:
            r7 = r8
            goto L_0x00a6
        L_0x00a5:
        L_0x00a6:
            if (r7 == 0) goto L_0x00df
            int r9 = r6.length     // Catch:{ all -> 0x00eb }
        L_0x00a9:
            if (r8 >= r9) goto L_0x00df
            r10 = r6[r8]     // Catch:{ all -> 0x00eb }
            boolean r11 = r12.removeHostsAndProvidersForPackageLocked(r10, r14)     // Catch:{ all -> 0x00eb }
            r3 = r3 | r11
            int r8 = r8 + 1
            goto L_0x00a9
        L_0x00b5:
            if (r1 == 0) goto L_0x00c2
            if (r5 == 0) goto L_0x00c1
            java.lang.String r9 = "android.intent.extra.REPLACING"
            boolean r9 = r5.getBoolean(r9, r8)     // Catch:{ all -> 0x00eb }
            if (r9 != 0) goto L_0x00c2
        L_0x00c1:
            goto L_0x00c3
        L_0x00c2:
            r7 = r8
        L_0x00c3:
            int r9 = r6.length     // Catch:{ all -> 0x00eb }
        L_0x00c4:
            if (r8 >= r9) goto L_0x00de
            r10 = r6[r8]     // Catch:{ all -> 0x00eb }
            r11 = 0
            boolean r11 = r12.updateProvidersForPackageLocked(r10, r14, r11)     // Catch:{ all -> 0x00eb }
            r3 = r3 | r11
            if (r7 == 0) goto L_0x00db
            if (r14 != 0) goto L_0x00db
            int r11 = r12.getUidForPackage(r10, r14)     // Catch:{ all -> 0x00eb }
            if (r11 < 0) goto L_0x00db
            r12.resolveHostUidLocked(r10, r11)     // Catch:{ all -> 0x00eb }
        L_0x00db:
            int r8 = r8 + 1
            goto L_0x00c4
        L_0x00de:
        L_0x00df:
            if (r3 == 0) goto L_0x00e7
            r12.saveGroupStateAsync(r14)     // Catch:{ all -> 0x00eb }
            r12.scheduleNotifyGroupHostsForProvidersChangedLocked(r14)     // Catch:{ all -> 0x00eb }
        L_0x00e7:
            monitor-exit(r4)     // Catch:{ all -> 0x00eb }
            return
        L_0x00e9:
            monitor-exit(r4)     // Catch:{ all -> 0x00eb }
            return
        L_0x00eb:
            r5 = move-exception
            monitor-exit(r4)     // Catch:{ all -> 0x00eb }
            throw r5
        L_0x00ee:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.appwidget.AppWidgetServiceImpl.onPackageBroadcastReceived(android.content.Intent, int):void");
    }

    /* access modifiers changed from: package-private */
    public void reloadWidgetsMaskedStateForGroup(int userId) {
        if (this.mUserManager.isUserUnlockingOrUnlocked(userId)) {
            synchronized (this.mLock) {
                reloadWidgetsMaskedState(userId);
                for (int profileId : this.mUserManager.getEnabledProfileIds(userId)) {
                    reloadWidgetsMaskedState(profileId);
                }
            }
        }
    }

    /* access modifiers changed from: private */
    public void reloadWidgetsMaskedState(int userId) {
        boolean suspended;
        long identity = Binder.clearCallingIdentity();
        try {
            UserInfo user = this.mUserManager.getUserInfo(userId);
            boolean lockedProfile = !this.mUserManager.isUserUnlockingOrUnlocked(userId);
            boolean quietProfile = user.isQuietModeEnabled();
            int N = this.mProviders.size();
            for (int i = 0; i < N; i++) {
                Provider provider = this.mProviders.get(i);
                if (provider.getUserId() == userId) {
                    boolean changed = provider.setMaskedByLockedProfileLocked(lockedProfile) | provider.setMaskedByQuietProfileLocked(quietProfile);
                    try {
                        suspended = this.mPackageManager.isPackageSuspendedForUser(provider.info.provider.getPackageName(), provider.getUserId());
                    } catch (IllegalArgumentException e) {
                        suspended = false;
                    }
                    changed |= provider.setMaskedBySuspendedPackageLocked(suspended);
                    if (changed) {
                        if (provider.isMaskedLocked()) {
                            maskWidgetsViewsLocked(provider, (Widget) null);
                        } else {
                            unmaskWidgetsViewsLocked(provider);
                        }
                    }
                }
            }
            Binder.restoreCallingIdentity(identity);
        } catch (RemoteException e2) {
            Slog.e(TAG, "Failed to query application info", e2);
        } catch (Throwable th) {
            Binder.restoreCallingIdentity(identity);
            throw th;
        }
    }

    /* access modifiers changed from: private */
    public void updateWidgetPackageSuspensionMaskedState(Intent intent, boolean suspended, int profileId) {
        String[] packagesArray = intent.getStringArrayExtra("android.intent.extra.changed_package_list");
        if (packagesArray != null) {
            Set<String> packages = new ArraySet<>(Arrays.asList(packagesArray));
            synchronized (this.mLock) {
                int N = this.mProviders.size();
                for (int i = 0; i < N; i++) {
                    Provider provider = this.mProviders.get(i);
                    if (provider.getUserId() == profileId) {
                        if (packages.contains(provider.info.provider.getPackageName())) {
                            if (provider.setMaskedBySuspendedPackageLocked(suspended)) {
                                if (provider.isMaskedLocked()) {
                                    maskWidgetsViewsLocked(provider, (Widget) null);
                                } else {
                                    unmaskWidgetsViewsLocked(provider);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private Bitmap createMaskedWidgetBitmap(String providerPackage, int providerUserId) {
        long identity = Binder.clearCallingIdentity();
        try {
            PackageManager pm = this.mContext.createPackageContextAsUser(providerPackage, 0, UserHandle.of(providerUserId)).getPackageManager();
            Drawable icon = pm.getApplicationInfo(providerPackage, 0).loadUnbadgedIcon(pm).mutate();
            icon.setColorFilter(this.mIconUtilities.getDisabledColorFilter());
            return this.mIconUtilities.createIconBitmap(icon);
        } catch (PackageManager.NameNotFoundException e) {
            Slog.e(TAG, "Fail to get application icon", e);
            return null;
        } finally {
            Binder.restoreCallingIdentity(identity);
        }
    }

    private RemoteViews createMaskedWidgetRemoteViews(Bitmap icon, boolean showBadge, PendingIntent onClickIntent) {
        RemoteViews views = new RemoteViews(this.mContext.getPackageName(), 17367365);
        if (icon != null) {
            views.setImageViewBitmap(16909607, icon);
        }
        if (!showBadge) {
            views.setViewVisibility(16909608, 4);
        }
        if (onClickIntent != null) {
            views.setOnClickPendingIntent(16909609, onClickIntent);
        }
        return views;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:3:0x000f, code lost:
        r5 = r2.info.provider.getPackageName();
        r6 = r17.getUserId();
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void maskWidgetsViewsLocked(com.android.server.appwidget.AppWidgetServiceImpl.Provider r17, com.android.server.appwidget.AppWidgetServiceImpl.Widget r18) {
        /*
            r16 = this;
            r1 = r16
            r2 = r17
            r3 = r18
            java.util.ArrayList<com.android.server.appwidget.AppWidgetServiceImpl$Widget> r0 = r2.widgets
            int r4 = r0.size()
            if (r4 != 0) goto L_0x000f
            return
        L_0x000f:
            android.appwidget.AppWidgetProviderInfo r0 = r2.info
            android.content.ComponentName r0 = r0.provider
            java.lang.String r5 = r0.getPackageName()
            int r6 = r17.getUserId()
            android.graphics.Bitmap r7 = r1.createMaskedWidgetBitmap(r5, r6)
            if (r7 != 0) goto L_0x0022
            return
        L_0x0022:
            long r8 = android.os.Binder.clearCallingIdentity()
            boolean r0 = r2.maskedBySuspendedPackage     // Catch:{ all -> 0x00aa }
            if (r0 == 0) goto L_0x0056
            android.os.UserManager r0 = r1.mUserManager     // Catch:{ all -> 0x00aa }
            android.content.pm.UserInfo r0 = r0.getUserInfo(r6)     // Catch:{ all -> 0x00aa }
            boolean r10 = r0.isManagedProfile()     // Catch:{ all -> 0x00aa }
            android.content.pm.PackageManagerInternal r11 = r1.mPackageManagerInternal     // Catch:{ all -> 0x00aa }
            java.lang.String r11 = r11.getSuspendingPackage(r5, r6)     // Catch:{ all -> 0x00aa }
            java.lang.String r12 = "android"
            boolean r12 = r12.equals(r11)     // Catch:{ all -> 0x00aa }
            if (r12 == 0) goto L_0x004a
            android.app.admin.DevicePolicyManagerInternal r12 = r1.mDevicePolicyManagerInternal     // Catch:{ all -> 0x00aa }
            r13 = 1
            android.content.Intent r12 = r12.createShowAdminSupportIntent(r6, r13)     // Catch:{ all -> 0x00aa }
            goto L_0x0055
        L_0x004a:
            android.content.pm.PackageManagerInternal r12 = r1.mPackageManagerInternal     // Catch:{ all -> 0x00aa }
            android.content.pm.SuspendDialogInfo r12 = r12.getSuspendedDialogInfo(r5, r6)     // Catch:{ all -> 0x00aa }
            android.content.Intent r13 = com.android.internal.app.SuspendedAppActivity.createSuspendedAppInterceptIntent(r5, r11, r12, r6)     // Catch:{ all -> 0x00aa }
            r12 = r13
        L_0x0055:
            goto L_0x0071
        L_0x0056:
            boolean r0 = r2.maskedByQuietProfile     // Catch:{ all -> 0x00aa }
            if (r0 == 0) goto L_0x0061
            r10 = 1
            android.content.Intent r0 = com.android.internal.app.UnlaunchableAppActivity.createInQuietModeDialogIntent(r6)     // Catch:{ all -> 0x00aa }
            r12 = r0
            goto L_0x0071
        L_0x0061:
            r10 = 1
            android.app.KeyguardManager r0 = r1.mKeyguardManager     // Catch:{ all -> 0x00aa }
            r11 = 0
            android.content.Intent r0 = r0.createConfirmDeviceCredentialIntent(r11, r11, r6)     // Catch:{ all -> 0x00aa }
            r12 = r0
            if (r12 == 0) goto L_0x0071
            r0 = 276824064(0x10800000, float:5.0487098E-29)
            r12.setFlags(r0)     // Catch:{ all -> 0x00aa }
        L_0x0071:
            r0 = 0
        L_0x0072:
            if (r0 >= r4) goto L_0x00a5
            java.util.ArrayList<com.android.server.appwidget.AppWidgetServiceImpl$Widget> r11 = r2.widgets     // Catch:{ all -> 0x00aa }
            java.lang.Object r11 = r11.get(r0)     // Catch:{ all -> 0x00aa }
            com.android.server.appwidget.AppWidgetServiceImpl$Widget r11 = (com.android.server.appwidget.AppWidgetServiceImpl.Widget) r11     // Catch:{ all -> 0x00aa }
            if (r3 == 0) goto L_0x0081
            if (r3 == r11) goto L_0x0081
            goto L_0x00a0
        L_0x0081:
            r13 = 0
            if (r12 == 0) goto L_0x008f
            android.content.Context r14 = r1.mContext     // Catch:{ all -> 0x00aa }
            int r15 = r11.appWidgetId     // Catch:{ all -> 0x00aa }
            r2 = 134217728(0x8000000, float:3.85186E-34)
            android.app.PendingIntent r2 = android.app.PendingIntent.getActivity(r14, r15, r12, r2)     // Catch:{ all -> 0x00aa }
            r13 = r2
        L_0x008f:
            android.widget.RemoteViews r2 = r1.createMaskedWidgetRemoteViews(r7, r10, r13)     // Catch:{ all -> 0x00aa }
            boolean r14 = r11.replaceWithMaskedViewsLocked(r2)     // Catch:{ all -> 0x00aa }
            if (r14 == 0) goto L_0x00a0
            android.widget.RemoteViews r14 = r11.getEffectiveViewsLocked()     // Catch:{ all -> 0x00aa }
            r1.scheduleNotifyUpdateAppWidgetLocked(r11, r14)     // Catch:{ all -> 0x00aa }
        L_0x00a0:
            int r0 = r0 + 1
            r2 = r17
            goto L_0x0072
        L_0x00a5:
            android.os.Binder.restoreCallingIdentity(r8)
            return
        L_0x00aa:
            r0 = move-exception
            android.os.Binder.restoreCallingIdentity(r8)
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.appwidget.AppWidgetServiceImpl.maskWidgetsViewsLocked(com.android.server.appwidget.AppWidgetServiceImpl$Provider, com.android.server.appwidget.AppWidgetServiceImpl$Widget):void");
    }

    private void unmaskWidgetsViewsLocked(Provider provider) {
        int widgetCount = provider.widgets.size();
        for (int j = 0; j < widgetCount; j++) {
            Widget widget = provider.widgets.get(j);
            if (widget.clearMaskedViewsLocked()) {
                scheduleNotifyUpdateAppWidgetLocked(widget, widget.getEffectiveViewsLocked());
            }
        }
    }

    private void resolveHostUidLocked(String pkg, int uid) {
        int N = this.mHosts.size();
        int i = 0;
        while (i < N) {
            Host host = this.mHosts.get(i);
            if (host.id.uid != -1 || !pkg.equals(host.id.packageName)) {
                i++;
            } else {
                if (DEBUG) {
                    Slog.i(TAG, "host " + host.id + " resolved to uid " + uid);
                }
                host.id = new HostId(uid, host.id.hostId, host.id.packageName);
                return;
            }
        }
    }

    /* access modifiers changed from: private */
    public void ensureGroupStateLoadedLocked(int userId) {
        ensureGroupStateLoadedLocked(userId, true);
    }

    /* access modifiers changed from: private */
    public void ensureGroupStateLoadedLocked(int userId, boolean enforceUserUnlockingOrUnlocked) {
        if (enforceUserUnlockingOrUnlocked && !isUserRunningAndUnlocked(userId)) {
            throw new IllegalStateException("User " + userId + " must be unlocked for widgets to be available");
        } else if (!enforceUserUnlockingOrUnlocked || !isProfileWithLockedParent(userId)) {
            int[] profileIds = this.mSecurityPolicy.getEnabledGroupProfileIds(userId);
            int newMemberCount = 0;
            for (int i = 0; i < profileIdCount; i++) {
                if (this.mLoadedUserIds.indexOfKey(profileIds[i]) >= 0) {
                    profileIds[i] = -1;
                } else {
                    newMemberCount++;
                }
            }
            if (newMemberCount > 0) {
                int newMemberIndex = 0;
                int[] newProfileIds = new int[newMemberCount];
                for (int profileId : profileIds) {
                    if (profileId != -1) {
                        this.mLoadedUserIds.put(profileId, profileId);
                        newProfileIds[newMemberIndex] = profileId;
                        newMemberIndex++;
                    }
                }
                clearProvidersAndHostsTagsLocked();
                loadGroupWidgetProvidersLocked(newProfileIds);
                loadGroupStateLocked(newProfileIds);
            }
        } else {
            throw new IllegalStateException("Profile " + userId + " must have unlocked parent");
        }
    }

    private boolean isUserRunningAndUnlocked(int userId) {
        return this.mUserManager.isUserUnlockingOrUnlocked(userId);
    }

    public void dump(FileDescriptor fd, PrintWriter pw, String[] args) {
        if (DumpUtils.checkDumpPermission(this.mContext, TAG, pw)) {
            synchronized (this.mLock) {
                if (args.length <= 0 || !PriorityDump.PROTO_ARG.equals(args[0])) {
                    dumpInternal(pw);
                } else {
                    dumpProto(fd);
                }
            }
        }
    }

    private void dumpProto(FileDescriptor fd) {
        Slog.i(TAG, "dump proto for " + this.mWidgets.size() + " widgets");
        ProtoOutputStream proto = new ProtoOutputStream(fd);
        int N = this.mWidgets.size();
        for (int i = 0; i < N; i++) {
            dumpProtoWidget(proto, this.mWidgets.get(i));
        }
        proto.flush();
    }

    private void dumpProtoWidget(ProtoOutputStream proto, Widget widget) {
        if (widget.host == null || widget.provider == null) {
            Slog.d(TAG, "skip dumping widget because host or provider is null: widget.host=" + widget.host + " widget.provider=" + widget.provider);
            return;
        }
        long token = proto.start(2246267895809L);
        boolean z = true;
        proto.write(1133871366145L, widget.host.getUserId() != widget.provider.getUserId());
        if (widget.host.callbacks != null) {
            z = false;
        }
        proto.write(1133871366146L, z);
        proto.write(1138166333443L, widget.host.id.packageName);
        proto.write(1138166333444L, widget.provider.id.componentName.getPackageName());
        proto.write(1138166333445L, widget.provider.id.componentName.getClassName());
        if (widget.options != null) {
            proto.write(1120986464262L, widget.options.getInt("appWidgetMinWidth", 0));
            proto.write(1120986464263L, widget.options.getInt("appWidgetMinHeight", 0));
            proto.write(1120986464264L, widget.options.getInt("appWidgetMaxWidth", 0));
            proto.write(1120986464265L, widget.options.getInt("appWidgetMaxHeight", 0));
        }
        proto.end(token);
    }

    private void dumpInternal(PrintWriter pw) {
        int N = this.mProviders.size();
        pw.println("Providers:");
        for (int i = 0; i < N; i++) {
            dumpProvider(this.mProviders.get(i), i, pw);
        }
        int N2 = this.mWidgets.size();
        pw.println(" ");
        pw.println("Widgets:");
        for (int i2 = 0; i2 < N2; i2++) {
            dumpWidget(this.mWidgets.get(i2), i2, pw);
        }
        int N3 = this.mHosts.size();
        pw.println(" ");
        pw.println("Hosts:");
        for (int i3 = 0; i3 < N3; i3++) {
            dumpHost(this.mHosts.get(i3), i3, pw);
        }
        int N4 = this.mPackagesWithBindWidgetPermission.size();
        pw.println(" ");
        pw.println("Grants:");
        for (int i4 = 0; i4 < N4; i4++) {
            dumpGrant(this.mPackagesWithBindWidgetPermission.valueAt(i4), i4, pw);
        }
    }

    public ParceledListSlice<PendingHostUpdate> startListening(IAppWidgetHost callbacks, String callingPackage, int hostId, int[] appWidgetIds) {
        HostId id;
        String str = callingPackage;
        int[] iArr = appWidgetIds;
        int userId = UserHandle.getCallingUserId();
        if (DEBUG) {
            Slog.i(TAG, "startListening() " + userId);
        }
        this.mSecurityPolicy.enforceCallFromPackage(str);
        synchronized (this.mLock) {
            try {
                if (this.mSecurityPolicy.isInstantAppLocked(str, userId)) {
                    Slog.w(TAG, "Instant package " + str + " cannot host app widgets");
                    ParceledListSlice<PendingHostUpdate> emptyList = ParceledListSlice.emptyList();
                    return emptyList;
                }
                ensureGroupStateLoadedLocked(userId);
                try {
                    HostId id2 = new HostId(Binder.getCallingUid(), hostId, str);
                    Host host = lookupOrAddHostLocked(id2);
                    host.callbacks = callbacks;
                    long updateSequenceNo = UPDATE_COUNTER.incrementAndGet();
                    int N = iArr.length;
                    ArrayList<PendingHostUpdate> outUpdates = new ArrayList<>(N);
                    LongSparseArray<PendingHostUpdate> updatesMap = new LongSparseArray<>();
                    int i = 0;
                    while (i < N) {
                        if (host.getPendingUpdatesForId(iArr[i], updatesMap)) {
                            int M = updatesMap.size();
                            id = id2;
                            int j = 0;
                            while (j < M) {
                                outUpdates.add(updatesMap.valueAt(j));
                                j++;
                            }
                        } else {
                            id = id2;
                        }
                        i++;
                        id2 = id;
                    }
                    host.lastWidgetUpdateSequenceNo = updateSequenceNo;
                    ParceledListSlice<PendingHostUpdate> parceledListSlice = new ParceledListSlice<>(outUpdates);
                    return parceledListSlice;
                } catch (Throwable th) {
                    th = th;
                    throw th;
                }
            } catch (Throwable th2) {
                th = th2;
                IAppWidgetHost iAppWidgetHost = callbacks;
                int i2 = hostId;
                throw th;
            }
        }
    }

    public void stopListening(String callingPackage, int hostId) {
        int userId = UserHandle.getCallingUserId();
        if (DEBUG) {
            Slog.i(TAG, "stopListening() " + userId);
        }
        this.mSecurityPolicy.enforceCallFromPackage(callingPackage);
        synchronized (this.mLock) {
            ensureGroupStateLoadedLocked(userId, false);
            Host host = lookupHostLocked(new HostId(Binder.getCallingUid(), hostId, callingPackage));
            if (host != null) {
                host.callbacks = null;
                pruneHostLocked(host);
            }
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:18:0x00a8, code lost:
        return r2;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public int allocateAppWidgetId(java.lang.String r10, int r11) {
        /*
            r9 = this;
            int r0 = android.os.UserHandle.getCallingUserId()
            boolean r1 = DEBUG
            if (r1 == 0) goto L_0x001e
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r2 = "allocateAppWidgetId() "
            r1.append(r2)
            r1.append(r0)
            java.lang.String r1 = r1.toString()
            java.lang.String r2 = "AppWidgetServiceImpl"
            android.util.Slog.i(r2, r1)
        L_0x001e:
            com.android.server.appwidget.AppWidgetServiceImpl$SecurityPolicy r1 = r9.mSecurityPolicy
            r1.enforceCallFromPackage(r10)
            java.lang.Object r1 = r9.mLock
            monitor-enter(r1)
            com.android.server.appwidget.AppWidgetServiceImpl$SecurityPolicy r2 = r9.mSecurityPolicy     // Catch:{ all -> 0x00a9 }
            boolean r2 = r2.isInstantAppLocked(r10, r0)     // Catch:{ all -> 0x00a9 }
            if (r2 == 0) goto L_0x004c
            java.lang.String r2 = "AppWidgetServiceImpl"
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ all -> 0x00a9 }
            r3.<init>()     // Catch:{ all -> 0x00a9 }
            java.lang.String r4 = "Instant package "
            r3.append(r4)     // Catch:{ all -> 0x00a9 }
            r3.append(r10)     // Catch:{ all -> 0x00a9 }
            java.lang.String r4 = " cannot host app widgets"
            r3.append(r4)     // Catch:{ all -> 0x00a9 }
            java.lang.String r3 = r3.toString()     // Catch:{ all -> 0x00a9 }
            android.util.Slog.w(r2, r3)     // Catch:{ all -> 0x00a9 }
            r2 = 0
            monitor-exit(r1)     // Catch:{ all -> 0x00a9 }
            return r2
        L_0x004c:
            r9.ensureGroupStateLoadedLocked(r0)     // Catch:{ all -> 0x00a9 }
            android.util.SparseIntArray r2 = r9.mNextAppWidgetIds     // Catch:{ all -> 0x00a9 }
            int r2 = r2.indexOfKey(r0)     // Catch:{ all -> 0x00a9 }
            if (r2 >= 0) goto L_0x005d
            android.util.SparseIntArray r2 = r9.mNextAppWidgetIds     // Catch:{ all -> 0x00a9 }
            r3 = 1
            r2.put(r0, r3)     // Catch:{ all -> 0x00a9 }
        L_0x005d:
            int r2 = r9.incrementAndGetAppWidgetIdLocked(r0)     // Catch:{ all -> 0x00a9 }
            com.android.server.appwidget.AppWidgetServiceImpl$HostId r3 = new com.android.server.appwidget.AppWidgetServiceImpl$HostId     // Catch:{ all -> 0x00a9 }
            int r4 = android.os.Binder.getCallingUid()     // Catch:{ all -> 0x00a9 }
            r3.<init>(r4, r11, r10)     // Catch:{ all -> 0x00a9 }
            com.android.server.appwidget.AppWidgetServiceImpl$Host r4 = r9.lookupOrAddHostLocked(r3)     // Catch:{ all -> 0x00a9 }
            com.android.server.appwidget.AppWidgetServiceImpl$Widget r5 = new com.android.server.appwidget.AppWidgetServiceImpl$Widget     // Catch:{ all -> 0x00a9 }
            r6 = 0
            r5.<init>()     // Catch:{ all -> 0x00a9 }
            r5.appWidgetId = r2     // Catch:{ all -> 0x00a9 }
            r5.host = r4     // Catch:{ all -> 0x00a9 }
            java.util.ArrayList<com.android.server.appwidget.AppWidgetServiceImpl$Widget> r6 = r4.widgets     // Catch:{ all -> 0x00a9 }
            r6.add(r5)     // Catch:{ all -> 0x00a9 }
            r9.addWidgetLocked(r5)     // Catch:{ all -> 0x00a9 }
            r9.saveGroupStateAsync(r0)     // Catch:{ all -> 0x00a9 }
            boolean r6 = DEBUG     // Catch:{ all -> 0x00a9 }
            if (r6 == 0) goto L_0x00a7
            java.lang.String r6 = "AppWidgetServiceImpl"
            java.lang.StringBuilder r7 = new java.lang.StringBuilder     // Catch:{ all -> 0x00a9 }
            r7.<init>()     // Catch:{ all -> 0x00a9 }
            java.lang.String r8 = "Allocated widget id "
            r7.append(r8)     // Catch:{ all -> 0x00a9 }
            r7.append(r2)     // Catch:{ all -> 0x00a9 }
            java.lang.String r8 = " for host "
            r7.append(r8)     // Catch:{ all -> 0x00a9 }
            com.android.server.appwidget.AppWidgetServiceImpl$HostId r8 = r4.id     // Catch:{ all -> 0x00a9 }
            r7.append(r8)     // Catch:{ all -> 0x00a9 }
            java.lang.String r7 = r7.toString()     // Catch:{ all -> 0x00a9 }
            android.util.Slog.i(r6, r7)     // Catch:{ all -> 0x00a9 }
        L_0x00a7:
            monitor-exit(r1)     // Catch:{ all -> 0x00a9 }
            return r2
        L_0x00a9:
            r2 = move-exception
            monitor-exit(r1)     // Catch:{ all -> 0x00a9 }
            throw r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.appwidget.AppWidgetServiceImpl.allocateAppWidgetId(java.lang.String, int):int");
    }

    /* JADX WARNING: Code restructure failed: missing block: B:14:0x0063, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void deleteAppWidgetId(java.lang.String r7, int r8) {
        /*
            r6 = this;
            int r0 = android.os.UserHandle.getCallingUserId()
            boolean r1 = DEBUG
            if (r1 == 0) goto L_0x001e
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r2 = "deleteAppWidgetId() "
            r1.append(r2)
            r1.append(r0)
            java.lang.String r1 = r1.toString()
            java.lang.String r2 = "AppWidgetServiceImpl"
            android.util.Slog.i(r2, r1)
        L_0x001e:
            com.android.server.appwidget.AppWidgetServiceImpl$SecurityPolicy r1 = r6.mSecurityPolicy
            r1.enforceCallFromPackage(r7)
            java.lang.Object r1 = r6.mLock
            monitor-enter(r1)
            r6.ensureGroupStateLoadedLocked(r0)     // Catch:{ all -> 0x0064 }
            int r2 = android.os.Binder.getCallingUid()     // Catch:{ all -> 0x0064 }
            com.android.server.appwidget.AppWidgetServiceImpl$Widget r2 = r6.lookupWidgetLocked(r8, r2, r7)     // Catch:{ all -> 0x0064 }
            if (r2 != 0) goto L_0x0036
            monitor-exit(r1)     // Catch:{ all -> 0x0064 }
            return
        L_0x0036:
            r6.deleteAppWidgetLocked(r2)     // Catch:{ all -> 0x0064 }
            r6.saveGroupStateAsync(r0)     // Catch:{ all -> 0x0064 }
            boolean r3 = DEBUG     // Catch:{ all -> 0x0064 }
            if (r3 == 0) goto L_0x0062
            java.lang.String r3 = "AppWidgetServiceImpl"
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ all -> 0x0064 }
            r4.<init>()     // Catch:{ all -> 0x0064 }
            java.lang.String r5 = "Deleted widget id "
            r4.append(r5)     // Catch:{ all -> 0x0064 }
            r4.append(r8)     // Catch:{ all -> 0x0064 }
            java.lang.String r5 = " for host "
            r4.append(r5)     // Catch:{ all -> 0x0064 }
            com.android.server.appwidget.AppWidgetServiceImpl$Host r5 = r2.host     // Catch:{ all -> 0x0064 }
            com.android.server.appwidget.AppWidgetServiceImpl$HostId r5 = r5.id     // Catch:{ all -> 0x0064 }
            r4.append(r5)     // Catch:{ all -> 0x0064 }
            java.lang.String r4 = r4.toString()     // Catch:{ all -> 0x0064 }
            android.util.Slog.i(r3, r4)     // Catch:{ all -> 0x0064 }
        L_0x0062:
            monitor-exit(r1)     // Catch:{ all -> 0x0064 }
            return
        L_0x0064:
            r2 = move-exception
            monitor-exit(r1)     // Catch:{ all -> 0x0064 }
            throw r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.appwidget.AppWidgetServiceImpl.deleteAppWidgetId(java.lang.String, int):void");
    }

    public boolean hasBindAppWidgetPermission(String packageName, int grantId) {
        if (DEBUG) {
            Slog.i(TAG, "hasBindAppWidgetPermission() " + UserHandle.getCallingUserId());
        }
        this.mSecurityPolicy.enforceModifyAppWidgetBindPermissions(packageName);
        synchronized (this.mLock) {
            ensureGroupStateLoadedLocked(grantId);
            if (getUidForPackage(packageName, grantId) < 0) {
                return false;
            }
            boolean contains = this.mPackagesWithBindWidgetPermission.contains(Pair.create(Integer.valueOf(grantId), packageName));
            return contains;
        }
    }

    public void setBindAppWidgetPermission(String packageName, int grantId, boolean grantPermission) {
        if (DEBUG) {
            Slog.i(TAG, "setBindAppWidgetPermission() " + UserHandle.getCallingUserId());
        }
        this.mSecurityPolicy.enforceModifyAppWidgetBindPermissions(packageName);
        synchronized (this.mLock) {
            ensureGroupStateLoadedLocked(grantId);
            if (getUidForPackage(packageName, grantId) >= 0) {
                Pair<Integer, String> packageId = Pair.create(Integer.valueOf(grantId), packageName);
                if (grantPermission) {
                    this.mPackagesWithBindWidgetPermission.add(packageId);
                } else {
                    this.mPackagesWithBindWidgetPermission.remove(packageId);
                }
                saveGroupStateAsync(grantId);
            }
        }
    }

    /* Debug info: failed to restart local var, previous not found, register: 19 */
    public IntentSender createAppWidgetConfigIntentSender(String callingPackage, int appWidgetId, int intentFlags) {
        long identity;
        String str = callingPackage;
        int i = appWidgetId;
        int userId = UserHandle.getCallingUserId();
        if (DEBUG) {
            Slog.i(TAG, "createAppWidgetConfigIntentSender() " + userId);
        }
        this.mSecurityPolicy.enforceCallFromPackage(str);
        synchronized (this.mLock) {
            try {
                ensureGroupStateLoadedLocked(userId);
                Widget widget = lookupWidgetLocked(i, Binder.getCallingUid(), str);
                if (widget != null) {
                    Provider provider = widget.provider;
                    if (provider != null) {
                        int secureFlags = intentFlags & -196;
                        try {
                            Intent intent = new Intent("android.appwidget.action.APPWIDGET_CONFIGURE");
                            intent.putExtra("appWidgetId", i);
                            intent.setComponent(provider.info.configure);
                            intent.setFlags(secureFlags);
                            identity = Binder.clearCallingIdentity();
                            IntentSender intentSender = PendingIntent.getActivityAsUser(this.mContext, 0, intent, 1409286144, (Bundle) null, new UserHandle(provider.getUserId())).getIntentSender();
                            Binder.restoreCallingIdentity(identity);
                            return intentSender;
                        } catch (Throwable th) {
                            th = th;
                            throw th;
                        }
                    } else {
                        int i2 = intentFlags;
                        throw new IllegalArgumentException("Widget not bound " + i);
                    }
                } else {
                    int i3 = intentFlags;
                    throw new IllegalArgumentException("Bad widget id " + i);
                }
            } catch (Throwable th2) {
                th = th2;
                int i4 = intentFlags;
                throw th;
            }
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:55:0x0182, code lost:
        return true;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean bindAppWidgetId(java.lang.String r17, int r18, int r19, android.content.ComponentName r20, android.os.Bundle r21) {
        /*
            r16 = this;
            r1 = r16
            r2 = r17
            r3 = r18
            r4 = r19
            r5 = r20
            int r6 = android.os.UserHandle.getCallingUserId()
            boolean r0 = DEBUG
            if (r0 == 0) goto L_0x0028
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r7 = "bindAppWidgetId() "
            r0.append(r7)
            r0.append(r6)
            java.lang.String r0 = r0.toString()
            java.lang.String r7 = "AppWidgetServiceImpl"
            android.util.Slog.i(r7, r0)
        L_0x0028:
            com.android.server.appwidget.AppWidgetServiceImpl$SecurityPolicy r0 = r1.mSecurityPolicy
            r0.enforceCallFromPackage(r2)
            com.android.server.appwidget.AppWidgetServiceImpl$SecurityPolicy r0 = r1.mSecurityPolicy
            boolean r0 = r0.isEnabledGroupProfile(r4)
            r7 = 0
            if (r0 != 0) goto L_0x0037
            return r7
        L_0x0037:
            com.android.server.appwidget.AppWidgetServiceImpl$SecurityPolicy r0 = r1.mSecurityPolicy
            java.lang.String r8 = r20.getPackageName()
            boolean r0 = r0.isProviderInCallerOrInProfileAndWhitelListed(r8, r4)
            if (r0 != 0) goto L_0x0044
            return r7
        L_0x0044:
            java.lang.Object r8 = r1.mLock
            monitor-enter(r8)
            r1.ensureGroupStateLoadedLocked(r6)     // Catch:{ all -> 0x0183 }
            com.android.server.appwidget.AppWidgetServiceImpl$SecurityPolicy r0 = r1.mSecurityPolicy     // Catch:{ all -> 0x0183 }
            boolean r0 = r0.hasCallerBindPermissionOrBindWhiteListedLocked(r2)     // Catch:{ all -> 0x0183 }
            if (r0 != 0) goto L_0x0054
            monitor-exit(r8)     // Catch:{ all -> 0x0183 }
            return r7
        L_0x0054:
            int r0 = android.os.Binder.getCallingUid()     // Catch:{ all -> 0x0183 }
            com.android.server.appwidget.AppWidgetServiceImpl$Widget r0 = r1.lookupWidgetLocked(r3, r0, r2)     // Catch:{ all -> 0x0183 }
            if (r0 != 0) goto L_0x0077
            java.lang.String r9 = "AppWidgetServiceImpl"
            java.lang.StringBuilder r10 = new java.lang.StringBuilder     // Catch:{ all -> 0x0183 }
            r10.<init>()     // Catch:{ all -> 0x0183 }
            java.lang.String r11 = "Bad widget id "
            r10.append(r11)     // Catch:{ all -> 0x0183 }
            r10.append(r3)     // Catch:{ all -> 0x0183 }
            java.lang.String r10 = r10.toString()     // Catch:{ all -> 0x0183 }
            android.util.Slog.e(r9, r10)     // Catch:{ all -> 0x0183 }
            monitor-exit(r8)     // Catch:{ all -> 0x0183 }
            return r7
        L_0x0077:
            com.android.server.appwidget.AppWidgetServiceImpl$Provider r9 = r0.provider     // Catch:{ all -> 0x0183 }
            if (r9 == 0) goto L_0x009f
            java.lang.String r9 = "AppWidgetServiceImpl"
            java.lang.StringBuilder r10 = new java.lang.StringBuilder     // Catch:{ all -> 0x0183 }
            r10.<init>()     // Catch:{ all -> 0x0183 }
            java.lang.String r11 = "Widget id "
            r10.append(r11)     // Catch:{ all -> 0x0183 }
            r10.append(r3)     // Catch:{ all -> 0x0183 }
            java.lang.String r11 = " already bound to: "
            r10.append(r11)     // Catch:{ all -> 0x0183 }
            com.android.server.appwidget.AppWidgetServiceImpl$Provider r11 = r0.provider     // Catch:{ all -> 0x0183 }
            com.android.server.appwidget.AppWidgetServiceImpl$ProviderId r11 = r11.id     // Catch:{ all -> 0x0183 }
            r10.append(r11)     // Catch:{ all -> 0x0183 }
            java.lang.String r10 = r10.toString()     // Catch:{ all -> 0x0183 }
            android.util.Slog.e(r9, r10)     // Catch:{ all -> 0x0183 }
            monitor-exit(r8)     // Catch:{ all -> 0x0183 }
            return r7
        L_0x009f:
            java.lang.String r9 = r20.getPackageName()     // Catch:{ all -> 0x0183 }
            int r9 = r1.getUidForPackage(r9, r4)     // Catch:{ all -> 0x0183 }
            if (r9 >= 0) goto L_0x00cd
            java.lang.String r10 = "AppWidgetServiceImpl"
            java.lang.StringBuilder r11 = new java.lang.StringBuilder     // Catch:{ all -> 0x0183 }
            r11.<init>()     // Catch:{ all -> 0x0183 }
            java.lang.String r12 = "Package "
            r11.append(r12)     // Catch:{ all -> 0x0183 }
            java.lang.String r12 = r20.getPackageName()     // Catch:{ all -> 0x0183 }
            r11.append(r12)     // Catch:{ all -> 0x0183 }
            java.lang.String r12 = " not installed  for profile "
            r11.append(r12)     // Catch:{ all -> 0x0183 }
            r11.append(r4)     // Catch:{ all -> 0x0183 }
            java.lang.String r11 = r11.toString()     // Catch:{ all -> 0x0183 }
            android.util.Slog.e(r10, r11)     // Catch:{ all -> 0x0183 }
            monitor-exit(r8)     // Catch:{ all -> 0x0183 }
            return r7
        L_0x00cd:
            com.android.server.appwidget.AppWidgetServiceImpl$ProviderId r10 = new com.android.server.appwidget.AppWidgetServiceImpl$ProviderId     // Catch:{ all -> 0x0183 }
            r11 = 0
            r10.<init>(r9, r5)     // Catch:{ all -> 0x0183 }
            com.android.server.appwidget.AppWidgetServiceImpl$Provider r11 = r1.lookupProviderLocked(r10)     // Catch:{ all -> 0x0183 }
            if (r11 != 0) goto L_0x00f9
            java.lang.String r12 = "AppWidgetServiceImpl"
            java.lang.StringBuilder r13 = new java.lang.StringBuilder     // Catch:{ all -> 0x0183 }
            r13.<init>()     // Catch:{ all -> 0x0183 }
            java.lang.String r14 = "No widget provider "
            r13.append(r14)     // Catch:{ all -> 0x0183 }
            r13.append(r5)     // Catch:{ all -> 0x0183 }
            java.lang.String r14 = " for profile "
            r13.append(r14)     // Catch:{ all -> 0x0183 }
            r13.append(r4)     // Catch:{ all -> 0x0183 }
            java.lang.String r13 = r13.toString()     // Catch:{ all -> 0x0183 }
            android.util.Slog.e(r12, r13)     // Catch:{ all -> 0x0183 }
            monitor-exit(r8)     // Catch:{ all -> 0x0183 }
            return r7
        L_0x00f9:
            boolean r12 = r11.zombie     // Catch:{ all -> 0x0183 }
            if (r12 == 0) goto L_0x0115
            java.lang.String r12 = "AppWidgetServiceImpl"
            java.lang.StringBuilder r13 = new java.lang.StringBuilder     // Catch:{ all -> 0x0183 }
            r13.<init>()     // Catch:{ all -> 0x0183 }
            java.lang.String r14 = "Can't bind to a 3rd party provider in safe mode "
            r13.append(r14)     // Catch:{ all -> 0x0183 }
            r13.append(r11)     // Catch:{ all -> 0x0183 }
            java.lang.String r13 = r13.toString()     // Catch:{ all -> 0x0183 }
            android.util.Slog.e(r12, r13)     // Catch:{ all -> 0x0183 }
            monitor-exit(r8)     // Catch:{ all -> 0x0183 }
            return r7
        L_0x0115:
            r0.provider = r11     // Catch:{ all -> 0x0183 }
            if (r21 == 0) goto L_0x011e
            android.os.Bundle r12 = cloneIfLocalBinder((android.os.Bundle) r21)     // Catch:{ all -> 0x0183 }
            goto L_0x0123
        L_0x011e:
            android.os.Bundle r12 = new android.os.Bundle     // Catch:{ all -> 0x0183 }
            r12.<init>()     // Catch:{ all -> 0x0183 }
        L_0x0123:
            r0.options = r12     // Catch:{ all -> 0x0183 }
            android.os.Bundle r12 = r0.options     // Catch:{ all -> 0x0183 }
            java.lang.String r13 = "appWidgetCategory"
            boolean r12 = r12.containsKey(r13)     // Catch:{ all -> 0x0183 }
            r13 = 1
            if (r12 != 0) goto L_0x0137
            android.os.Bundle r12 = r0.options     // Catch:{ all -> 0x0183 }
            java.lang.String r14 = "appWidgetCategory"
            r12.putInt(r14, r13)     // Catch:{ all -> 0x0183 }
        L_0x0137:
            java.util.ArrayList<com.android.server.appwidget.AppWidgetServiceImpl$Widget> r12 = r11.widgets     // Catch:{ all -> 0x0183 }
            r12.add(r0)     // Catch:{ all -> 0x0183 }
            r1.onWidgetProviderAddedOrChangedLocked(r0)     // Catch:{ all -> 0x0183 }
            java.util.ArrayList<com.android.server.appwidget.AppWidgetServiceImpl$Widget> r12 = r11.widgets     // Catch:{ all -> 0x0183 }
            int r12 = r12.size()     // Catch:{ all -> 0x0183 }
            if (r12 != r13) goto L_0x014a
            r1.sendEnableIntentLocked(r11)     // Catch:{ all -> 0x0183 }
        L_0x014a:
            int[] r14 = new int[r13]     // Catch:{ all -> 0x0183 }
            r14[r7] = r3     // Catch:{ all -> 0x0183 }
            r1.sendUpdateIntentLocked(r11, r14)     // Catch:{ all -> 0x0183 }
            java.util.ArrayList<com.android.server.appwidget.AppWidgetServiceImpl$Widget> r7 = r11.widgets     // Catch:{ all -> 0x0183 }
            int[] r7 = getWidgetIds(r7)     // Catch:{ all -> 0x0183 }
            r1.registerForBroadcastsLocked(r11, r7)     // Catch:{ all -> 0x0183 }
            r1.saveGroupStateAsync(r6)     // Catch:{ all -> 0x0183 }
            boolean r7 = DEBUG     // Catch:{ all -> 0x0183 }
            if (r7 == 0) goto L_0x0181
            java.lang.String r7 = "AppWidgetServiceImpl"
            java.lang.StringBuilder r14 = new java.lang.StringBuilder     // Catch:{ all -> 0x0183 }
            r14.<init>()     // Catch:{ all -> 0x0183 }
            java.lang.String r15 = "Bound widget "
            r14.append(r15)     // Catch:{ all -> 0x0183 }
            r14.append(r3)     // Catch:{ all -> 0x0183 }
            java.lang.String r15 = " to provider "
            r14.append(r15)     // Catch:{ all -> 0x0183 }
            com.android.server.appwidget.AppWidgetServiceImpl$ProviderId r15 = r11.id     // Catch:{ all -> 0x0183 }
            r14.append(r15)     // Catch:{ all -> 0x0183 }
            java.lang.String r14 = r14.toString()     // Catch:{ all -> 0x0183 }
            android.util.Slog.i(r7, r14)     // Catch:{ all -> 0x0183 }
        L_0x0181:
            monitor-exit(r8)     // Catch:{ all -> 0x0183 }
            return r13
        L_0x0183:
            r0 = move-exception
            monitor-exit(r8)     // Catch:{ all -> 0x0183 }
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.appwidget.AppWidgetServiceImpl.bindAppWidgetId(java.lang.String, int, int, android.content.ComponentName, android.os.Bundle):boolean");
    }

    public int[] getAppWidgetIds(ComponentName componentName) {
        int userId = UserHandle.getCallingUserId();
        if (DEBUG) {
            Slog.i(TAG, "getAppWidgetIds() " + userId);
        }
        this.mSecurityPolicy.enforceCallFromPackage(componentName.getPackageName());
        synchronized (this.mLock) {
            ensureGroupStateLoadedLocked(userId);
            Provider provider = lookupProviderLocked(new ProviderId(Binder.getCallingUid(), componentName));
            if (provider != null) {
                int[] widgetIds = getWidgetIds(provider.widgets);
                return widgetIds;
            }
            int[] iArr = new int[0];
            return iArr;
        }
    }

    public int[] getAppWidgetIdsForHost(String callingPackage, int hostId) {
        int userId = UserHandle.getCallingUserId();
        if (DEBUG) {
            Slog.i(TAG, "getAppWidgetIdsForHost() " + userId);
        }
        this.mSecurityPolicy.enforceCallFromPackage(callingPackage);
        synchronized (this.mLock) {
            ensureGroupStateLoadedLocked(userId);
            Host host = lookupHostLocked(new HostId(Binder.getCallingUid(), hostId, callingPackage));
            if (host != null) {
                int[] widgetIds = getWidgetIds(host.widgets);
                return widgetIds;
            }
            int[] iArr = new int[0];
            return iArr;
        }
    }

    /* Debug info: failed to restart local var, previous not found, register: 23 */
    public boolean bindRemoteViewsService(String callingPackage, int appWidgetId, Intent intent, IApplicationThread caller, IBinder activtiyToken, IServiceConnection connection, int flags) {
        Widget widget;
        int i = appWidgetId;
        Intent intent2 = intent;
        int userId = UserHandle.getCallingUserId();
        if (DEBUG) {
            Slog.i(TAG, "bindRemoteViewsService() " + userId);
        }
        synchronized (this.mLock) {
            try {
                ensureGroupStateLoadedLocked(userId);
                try {
                    Widget widget2 = lookupWidgetLocked(i, Binder.getCallingUid(), callingPackage);
                    if (widget2 == null) {
                        int i2 = userId;
                        Widget widget3 = widget2;
                        throw new IllegalArgumentException("Bad widget id");
                    } else if (widget2.provider != null) {
                        ComponentName componentName = intent.getComponent();
                        String providerPackage = widget2.provider.id.componentName.getPackageName();
                        String servicePackage = componentName.getPackageName();
                        if (servicePackage.equals(providerPackage)) {
                            this.mSecurityPolicy.enforceServiceExistsAndRequiresBindRemoteViewsPermission(componentName, widget2.provider.getUserId());
                            long callingIdentity = Binder.clearCallingIdentity();
                            try {
                                String str = servicePackage;
                                String str2 = providerPackage;
                                ComponentName componentName2 = componentName;
                                int i3 = userId;
                                widget = widget2;
                            } catch (RemoteException e) {
                                String str3 = servicePackage;
                                String str4 = providerPackage;
                                ComponentName componentName3 = componentName;
                                int i4 = userId;
                                Widget widget4 = widget2;
                                Binder.restoreCallingIdentity(callingIdentity);
                                return false;
                            } catch (Throwable th) {
                                th = th;
                                String str5 = servicePackage;
                                String str6 = providerPackage;
                                ComponentName componentName4 = componentName;
                                int i5 = userId;
                                Widget widget5 = widget2;
                                Binder.restoreCallingIdentity(callingIdentity);
                                throw th;
                            }
                            try {
                                if (ActivityManager.getService().bindService(caller, activtiyToken, intent, intent2.resolveTypeIfNeeded(this.mContext.getContentResolver()), connection, flags, this.mContext.getOpPackageName(), widget2.provider.getUserId()) != 0) {
                                    incrementAppWidgetServiceRefCount(i, Pair.create(Integer.valueOf(widget.provider.id.uid), new Intent.FilterComparison(intent2)));
                                    Binder.restoreCallingIdentity(callingIdentity);
                                    return true;
                                }
                                Binder.restoreCallingIdentity(callingIdentity);
                            } catch (RemoteException e2) {
                                Binder.restoreCallingIdentity(callingIdentity);
                                return false;
                            } catch (Throwable th2) {
                                th = th2;
                                throw th;
                            }
                        } else {
                            String str7 = servicePackage;
                            String str8 = providerPackage;
                            ComponentName componentName5 = componentName;
                            int i6 = userId;
                            Widget widget6 = widget2;
                            throw new SecurityException("The taget service not in the same package as the widget provider");
                        }
                    } else {
                        int i7 = userId;
                        Widget widget7 = widget2;
                        throw new IllegalArgumentException("No provider for widget " + i);
                    }
                } catch (Throwable th3) {
                    th = th3;
                    int i8 = userId;
                    throw th;
                }
            } catch (Throwable th4) {
                th = th4;
                String str9 = callingPackage;
                int i82 = userId;
                throw th;
            }
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:14:0x005d, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void deleteHost(java.lang.String r8, int r9) {
        /*
            r7 = this;
            int r0 = android.os.UserHandle.getCallingUserId()
            boolean r1 = DEBUG
            if (r1 == 0) goto L_0x001e
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r2 = "deleteHost() "
            r1.append(r2)
            r1.append(r0)
            java.lang.String r1 = r1.toString()
            java.lang.String r2 = "AppWidgetServiceImpl"
            android.util.Slog.i(r2, r1)
        L_0x001e:
            com.android.server.appwidget.AppWidgetServiceImpl$SecurityPolicy r1 = r7.mSecurityPolicy
            r1.enforceCallFromPackage(r8)
            java.lang.Object r1 = r7.mLock
            monitor-enter(r1)
            r7.ensureGroupStateLoadedLocked(r0)     // Catch:{ all -> 0x005e }
            com.android.server.appwidget.AppWidgetServiceImpl$HostId r2 = new com.android.server.appwidget.AppWidgetServiceImpl$HostId     // Catch:{ all -> 0x005e }
            int r3 = android.os.Binder.getCallingUid()     // Catch:{ all -> 0x005e }
            r2.<init>(r3, r9, r8)     // Catch:{ all -> 0x005e }
            com.android.server.appwidget.AppWidgetServiceImpl$Host r3 = r7.lookupHostLocked(r2)     // Catch:{ all -> 0x005e }
            if (r3 != 0) goto L_0x003a
            monitor-exit(r1)     // Catch:{ all -> 0x005e }
            return
        L_0x003a:
            r7.deleteHostLocked(r3)     // Catch:{ all -> 0x005e }
            r7.saveGroupStateAsync(r0)     // Catch:{ all -> 0x005e }
            boolean r4 = DEBUG     // Catch:{ all -> 0x005e }
            if (r4 == 0) goto L_0x005c
            java.lang.String r4 = "AppWidgetServiceImpl"
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ all -> 0x005e }
            r5.<init>()     // Catch:{ all -> 0x005e }
            java.lang.String r6 = "Deleted host "
            r5.append(r6)     // Catch:{ all -> 0x005e }
            com.android.server.appwidget.AppWidgetServiceImpl$HostId r6 = r3.id     // Catch:{ all -> 0x005e }
            r5.append(r6)     // Catch:{ all -> 0x005e }
            java.lang.String r5 = r5.toString()     // Catch:{ all -> 0x005e }
            android.util.Slog.i(r4, r5)     // Catch:{ all -> 0x005e }
        L_0x005c:
            monitor-exit(r1)     // Catch:{ all -> 0x005e }
            return
        L_0x005e:
            r2 = move-exception
            monitor-exit(r1)     // Catch:{ all -> 0x005e }
            throw r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.appwidget.AppWidgetServiceImpl.deleteHost(java.lang.String, int):void");
    }

    public void deleteAllHosts() {
        int userId = UserHandle.getCallingUserId();
        if (DEBUG) {
            Slog.i(TAG, "deleteAllHosts() " + userId);
        }
        synchronized (this.mLock) {
            ensureGroupStateLoadedLocked(userId);
            boolean changed = false;
            for (int i = this.mHosts.size() - 1; i >= 0; i--) {
                Host host = this.mHosts.get(i);
                if (host.id.uid == Binder.getCallingUid()) {
                    deleteHostLocked(host);
                    changed = true;
                    if (DEBUG) {
                        Slog.i(TAG, "Deleted host " + host.id);
                    }
                }
            }
            if (changed) {
                saveGroupStateAsync(userId);
            }
        }
    }

    public AppWidgetProviderInfo getAppWidgetInfo(String callingPackage, int appWidgetId) {
        int userId = UserHandle.getCallingUserId();
        if (DEBUG) {
            Slog.i(TAG, "getAppWidgetInfo() " + userId);
        }
        this.mSecurityPolicy.enforceCallFromPackage(callingPackage);
        synchronized (this.mLock) {
            ensureGroupStateLoadedLocked(userId);
            Widget widget = lookupWidgetLocked(appWidgetId, Binder.getCallingUid(), callingPackage);
            if (widget == null || widget.provider == null || widget.provider.zombie) {
                return null;
            }
            AppWidgetProviderInfo cloneIfLocalBinder = cloneIfLocalBinder(widget.provider.info);
            return cloneIfLocalBinder;
        }
    }

    public RemoteViews getAppWidgetViews(String callingPackage, int appWidgetId) {
        int userId = UserHandle.getCallingUserId();
        if (DEBUG) {
            Slog.i(TAG, "getAppWidgetViews() " + userId);
        }
        this.mSecurityPolicy.enforceCallFromPackage(callingPackage);
        synchronized (this.mLock) {
            ensureGroupStateLoadedLocked(userId);
            Widget widget = lookupWidgetLocked(appWidgetId, Binder.getCallingUid(), callingPackage);
            if (widget == null) {
                return null;
            }
            RemoteViews cloneIfLocalBinder = cloneIfLocalBinder(widget.getEffectiveViewsLocked());
            return cloneIfLocalBinder;
        }
    }

    public void updateAppWidgetOptions(String callingPackage, int appWidgetId, Bundle options) {
        int userId = UserHandle.getCallingUserId();
        if (DEBUG) {
            Slog.i(TAG, "updateAppWidgetOptions() " + userId);
        }
        this.mSecurityPolicy.enforceCallFromPackage(callingPackage);
        synchronized (this.mLock) {
            ensureGroupStateLoadedLocked(userId);
            Widget widget = lookupWidgetLocked(appWidgetId, Binder.getCallingUid(), callingPackage);
            if (widget != null) {
                widget.options.putAll(options);
                sendOptionsChangedIntentLocked(widget);
                saveGroupStateAsync(userId);
            }
        }
    }

    public Bundle getAppWidgetOptions(String callingPackage, int appWidgetId) {
        int userId = UserHandle.getCallingUserId();
        if (DEBUG) {
            Slog.i(TAG, "getAppWidgetOptions() " + userId);
        }
        this.mSecurityPolicy.enforceCallFromPackage(callingPackage);
        synchronized (this.mLock) {
            ensureGroupStateLoadedLocked(userId);
            Widget widget = lookupWidgetLocked(appWidgetId, Binder.getCallingUid(), callingPackage);
            if (widget == null || widget.options == null) {
                Bundle bundle = Bundle.EMPTY;
                return bundle;
            }
            Bundle cloneIfLocalBinder = cloneIfLocalBinder(widget.options);
            return cloneIfLocalBinder;
        }
    }

    public void updateAppWidgetIds(String callingPackage, int[] appWidgetIds, RemoteViews views) {
        if (DEBUG) {
            Slog.i(TAG, "updateAppWidgetIds() " + UserHandle.getCallingUserId());
        }
        updateAppWidgetIds(callingPackage, appWidgetIds, views, false);
    }

    public void partiallyUpdateAppWidgetIds(String callingPackage, int[] appWidgetIds, RemoteViews views) {
        if (DEBUG) {
            Slog.i(TAG, "partiallyUpdateAppWidgetIds() " + UserHandle.getCallingUserId());
        }
        updateAppWidgetIds(callingPackage, appWidgetIds, views, true);
    }

    public void notifyAppWidgetViewDataChanged(String callingPackage, int[] appWidgetIds, int viewId) {
        int userId = UserHandle.getCallingUserId();
        if (DEBUG) {
            Slog.i(TAG, "notifyAppWidgetViewDataChanged() " + userId);
        }
        this.mSecurityPolicy.enforceCallFromPackage(callingPackage);
        if (appWidgetIds != null && appWidgetIds.length != 0) {
            synchronized (this.mLock) {
                ensureGroupStateLoadedLocked(userId);
                for (int appWidgetId : appWidgetIds) {
                    Widget widget = lookupWidgetLocked(appWidgetId, Binder.getCallingUid(), callingPackage);
                    if (widget != null) {
                        scheduleNotifyAppWidgetViewDataChanged(widget, viewId);
                    }
                }
            }
        }
    }

    public void updateAppWidgetProvider(ComponentName componentName, RemoteViews views) {
        int userId = UserHandle.getCallingUserId();
        if (DEBUG) {
            Slog.i(TAG, "updateAppWidgetProvider() " + userId);
        }
        this.mSecurityPolicy.enforceCallFromPackage(componentName.getPackageName());
        synchronized (this.mLock) {
            ensureGroupStateLoadedLocked(userId);
            ProviderId providerId = new ProviderId(Binder.getCallingUid(), componentName);
            Provider provider = lookupProviderLocked(providerId);
            if (provider == null) {
                Slog.w(TAG, "Provider doesn't exist " + providerId);
                return;
            }
            ArrayList<Widget> instances = provider.widgets;
            int N = instances.size();
            for (int i = 0; i < N; i++) {
                updateAppWidgetInstanceLocked(instances.get(i), views, false);
            }
        }
    }

    /* Debug info: failed to restart local var, previous not found, register: 11 */
    public void updateAppWidgetProviderInfo(ComponentName componentName, String metadataKey) {
        int userId = UserHandle.getCallingUserId();
        if (DEBUG) {
            Slog.i(TAG, "updateAppWidgetProvider() " + userId);
        }
        this.mSecurityPolicy.enforceCallFromPackage(componentName.getPackageName());
        synchronized (this.mLock) {
            ensureGroupStateLoadedLocked(userId);
            ProviderId providerId = new ProviderId(Binder.getCallingUid(), componentName);
            Provider provider = lookupProviderLocked(providerId);
            if (provider == null) {
                throw new IllegalArgumentException(componentName + " is not a valid AppWidget provider");
            } else if (!Objects.equals(provider.infoTag, metadataKey)) {
                String keyToUse = metadataKey == null ? "android.appwidget.provider" : metadataKey;
                AppWidgetProviderInfo info = parseAppWidgetProviderInfo(providerId, provider.info.providerInfo, keyToUse);
                if (info != null) {
                    provider.info = info;
                    provider.infoTag = metadataKey;
                    int N = provider.widgets.size();
                    for (int i = 0; i < N; i++) {
                        Widget widget = provider.widgets.get(i);
                        scheduleNotifyProviderChangedLocked(widget);
                        updateAppWidgetInstanceLocked(widget, widget.views, false);
                    }
                    saveGroupStateAsync(userId);
                    scheduleNotifyGroupHostsForProvidersChangedLocked(userId);
                    return;
                }
                throw new IllegalArgumentException("Unable to parse " + keyToUse + " meta-data to a valid AppWidget provider");
            }
        }
    }

    public boolean isRequestPinAppWidgetSupported() {
        synchronized (this.mLock) {
            if (!this.mSecurityPolicy.isCallerInstantAppLocked()) {
                return ((ShortcutServiceInternal) LocalServices.getService(ShortcutServiceInternal.class)).isRequestPinItemSupported(UserHandle.getCallingUserId(), 2);
            }
            Slog.w(TAG, "Instant uid " + Binder.getCallingUid() + " query information about app widgets");
            return false;
        }
    }

    public boolean requestPinAppWidget(String callingPackage, ComponentName componentName, Bundle extras, IntentSender resultSender) {
        int callingUid = Binder.getCallingUid();
        int userId = UserHandle.getUserId(callingUid);
        if (DEBUG) {
            Slog.i(TAG, "requestPinAppWidget() " + userId);
        }
        synchronized (this.mLock) {
            ensureGroupStateLoadedLocked(userId);
            Provider provider = lookupProviderLocked(new ProviderId(callingUid, componentName));
            if (provider != null) {
                if (!provider.zombie) {
                    AppWidgetProviderInfo info = provider.info;
                    if ((info.widgetCategory & 1) == 0) {
                        return false;
                    }
                    return ((ShortcutServiceInternal) LocalServices.getService(ShortcutServiceInternal.class)).requestPinAppWidget(callingPackage, info, extras, resultSender, userId);
                }
            }
            return false;
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:28:0x0092  */
    /* JADX WARNING: Removed duplicated region for block: B:29:0x0093  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public android.content.pm.ParceledListSlice<android.appwidget.AppWidgetProviderInfo> getInstalledProvidersForProfile(int r12, int r13, java.lang.String r14) {
        /*
            r11 = this;
            int r0 = android.os.UserHandle.getCallingUserId()
            boolean r1 = DEBUG
            if (r1 == 0) goto L_0x001e
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r2 = "getInstalledProvidersForProfiles() "
            r1.append(r2)
            r1.append(r0)
            java.lang.String r1 = r1.toString()
            java.lang.String r2 = "AppWidgetServiceImpl"
            android.util.Slog.i(r2, r1)
        L_0x001e:
            com.android.server.appwidget.AppWidgetServiceImpl$SecurityPolicy r1 = r11.mSecurityPolicy
            boolean r1 = r1.isEnabledGroupProfile(r13)
            if (r1 != 0) goto L_0x0028
            r1 = 0
            return r1
        L_0x0028:
            java.lang.Object r1 = r11.mLock
            monitor-enter(r1)
            com.android.server.appwidget.AppWidgetServiceImpl$SecurityPolicy r2 = r11.mSecurityPolicy     // Catch:{ all -> 0x00be }
            boolean r2 = r2.isCallerInstantAppLocked()     // Catch:{ all -> 0x00be }
            if (r2 == 0) goto L_0x0058
            java.lang.String r2 = "AppWidgetServiceImpl"
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ all -> 0x00be }
            r3.<init>()     // Catch:{ all -> 0x00be }
            java.lang.String r4 = "Instant uid "
            r3.append(r4)     // Catch:{ all -> 0x00be }
            int r4 = android.os.Binder.getCallingUid()     // Catch:{ all -> 0x00be }
            r3.append(r4)     // Catch:{ all -> 0x00be }
            java.lang.String r4 = " cannot access widget providers"
            r3.append(r4)     // Catch:{ all -> 0x00be }
            java.lang.String r3 = r3.toString()     // Catch:{ all -> 0x00be }
            android.util.Slog.w(r2, r3)     // Catch:{ all -> 0x00be }
            android.content.pm.ParceledListSlice r2 = android.content.pm.ParceledListSlice.emptyList()     // Catch:{ all -> 0x00be }
            monitor-exit(r1)     // Catch:{ all -> 0x00be }
            return r2
        L_0x0058:
            r11.ensureGroupStateLoadedLocked(r0)     // Catch:{ all -> 0x00be }
            java.util.ArrayList r2 = new java.util.ArrayList     // Catch:{ all -> 0x00be }
            r2.<init>()     // Catch:{ all -> 0x00be }
            java.util.ArrayList<com.android.server.appwidget.AppWidgetServiceImpl$Provider> r3 = r11.mProviders     // Catch:{ all -> 0x00be }
            int r3 = r3.size()     // Catch:{ all -> 0x00be }
            r4 = 0
        L_0x0067:
            if (r4 >= r3) goto L_0x00b7
            java.util.ArrayList<com.android.server.appwidget.AppWidgetServiceImpl$Provider> r5 = r11.mProviders     // Catch:{ all -> 0x00be }
            java.lang.Object r5 = r5.get(r4)     // Catch:{ all -> 0x00be }
            com.android.server.appwidget.AppWidgetServiceImpl$Provider r5 = (com.android.server.appwidget.AppWidgetServiceImpl.Provider) r5     // Catch:{ all -> 0x00be }
            android.appwidget.AppWidgetProviderInfo r6 = r5.info     // Catch:{ all -> 0x00be }
            if (r14 == 0) goto L_0x0086
            com.android.server.appwidget.AppWidgetServiceImpl$ProviderId r7 = r5.id     // Catch:{ all -> 0x00be }
            android.content.ComponentName r7 = r7.componentName     // Catch:{ all -> 0x00be }
            java.lang.String r7 = r7.getPackageName()     // Catch:{ all -> 0x00be }
            boolean r7 = r7.equals(r14)     // Catch:{ all -> 0x00be }
            if (r7 == 0) goto L_0x0084
            goto L_0x0086
        L_0x0084:
            r7 = 0
            goto L_0x0087
        L_0x0086:
            r7 = 1
        L_0x0087:
            boolean r8 = r5.zombie     // Catch:{ all -> 0x00be }
            if (r8 != 0) goto L_0x00b4
            int r8 = r6.widgetCategory     // Catch:{ all -> 0x00be }
            r8 = r8 & r12
            if (r8 == 0) goto L_0x00b4
            if (r7 != 0) goto L_0x0093
            goto L_0x00b4
        L_0x0093:
            android.os.UserHandle r8 = r6.getProfile()     // Catch:{ all -> 0x00be }
            int r8 = r8.getIdentifier()     // Catch:{ all -> 0x00be }
            if (r8 != r13) goto L_0x00b4
            com.android.server.appwidget.AppWidgetServiceImpl$SecurityPolicy r9 = r11.mSecurityPolicy     // Catch:{ all -> 0x00be }
            com.android.server.appwidget.AppWidgetServiceImpl$ProviderId r10 = r5.id     // Catch:{ all -> 0x00be }
            android.content.ComponentName r10 = r10.componentName     // Catch:{ all -> 0x00be }
            java.lang.String r10 = r10.getPackageName()     // Catch:{ all -> 0x00be }
            boolean r9 = r9.isProviderInCallerOrInProfileAndWhitelListed(r10, r8)     // Catch:{ all -> 0x00be }
            if (r9 == 0) goto L_0x00b4
            android.appwidget.AppWidgetProviderInfo r9 = cloneIfLocalBinder((android.appwidget.AppWidgetProviderInfo) r6)     // Catch:{ all -> 0x00be }
            r2.add(r9)     // Catch:{ all -> 0x00be }
        L_0x00b4:
            int r4 = r4 + 1
            goto L_0x0067
        L_0x00b7:
            android.content.pm.ParceledListSlice r4 = new android.content.pm.ParceledListSlice     // Catch:{ all -> 0x00be }
            r4.<init>(r2)     // Catch:{ all -> 0x00be }
            monitor-exit(r1)     // Catch:{ all -> 0x00be }
            return r4
        L_0x00be:
            r2 = move-exception
            monitor-exit(r1)     // Catch:{ all -> 0x00be }
            throw r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.appwidget.AppWidgetServiceImpl.getInstalledProvidersForProfile(int, int, java.lang.String):android.content.pm.ParceledListSlice");
    }

    private void updateAppWidgetIds(String callingPackage, int[] appWidgetIds, RemoteViews views, boolean partially) {
        int userId = UserHandle.getCallingUserId();
        if (appWidgetIds != null && appWidgetIds.length != 0) {
            this.mSecurityPolicy.enforceCallFromPackage(callingPackage);
            synchronized (this.mLock) {
                ensureGroupStateLoadedLocked(userId);
                for (int appWidgetId : appWidgetIds) {
                    Widget widget = lookupWidgetLocked(appWidgetId, Binder.getCallingUid(), callingPackage);
                    if (widget != null) {
                        updateAppWidgetInstanceLocked(widget, views, partially);
                    }
                }
            }
        }
    }

    /* access modifiers changed from: private */
    public int incrementAndGetAppWidgetIdLocked(int userId) {
        int appWidgetId = peekNextAppWidgetIdLocked(userId) + 1;
        this.mNextAppWidgetIds.put(userId, appWidgetId);
        return appWidgetId;
    }

    private void setMinAppWidgetIdLocked(int userId, int minWidgetId) {
        if (peekNextAppWidgetIdLocked(userId) < minWidgetId) {
            this.mNextAppWidgetIds.put(userId, minWidgetId);
        }
    }

    private int peekNextAppWidgetIdLocked(int userId) {
        if (this.mNextAppWidgetIds.indexOfKey(userId) < 0) {
            return 1;
        }
        return this.mNextAppWidgetIds.get(userId);
    }

    /* access modifiers changed from: private */
    public Host lookupOrAddHostLocked(HostId id) {
        Host host = lookupHostLocked(id);
        if (host != null) {
            return host;
        }
        Host host2 = new Host();
        host2.id = id;
        this.mHosts.add(host2);
        return host2;
    }

    private void deleteHostLocked(Host host) {
        for (int i = host.widgets.size() - 1; i >= 0; i--) {
            deleteAppWidgetLocked(host.widgets.remove(i));
        }
        this.mHosts.remove(host);
        host.callbacks = null;
    }

    private void deleteAppWidgetLocked(Widget widget) {
        decrementAppWidgetServiceRefCount(widget);
        Host host = widget.host;
        host.widgets.remove(widget);
        pruneHostLocked(host);
        removeWidgetLocked(widget);
        Provider provider = widget.provider;
        if (provider != null) {
            provider.widgets.remove(widget);
            if (!provider.zombie) {
                sendDeletedIntentLocked(widget);
                if (provider.widgets.isEmpty()) {
                    cancelBroadcastsLocked(provider);
                    sendDisabledIntentLocked(provider);
                }
            }
        }
    }

    private void cancelBroadcastsLocked(Provider provider) {
        if (DEBUG) {
            Slog.i(TAG, "cancelBroadcastsLocked() for " + provider);
        }
        if (provider.broadcast != null) {
            this.mSaveStateHandler.post(new Runnable(provider.broadcast) {
                private final /* synthetic */ PendingIntent f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    AppWidgetServiceImpl.this.lambda$cancelBroadcastsLocked$0$AppWidgetServiceImpl(this.f$1);
                }
            });
            provider.broadcast = null;
        }
    }

    public /* synthetic */ void lambda$cancelBroadcastsLocked$0$AppWidgetServiceImpl(PendingIntent broadcast) {
        this.mAlarmManager.cancel(broadcast);
        broadcast.cancel();
    }

    private void destroyRemoteViewsService(final Intent intent, Widget widget) {
        ServiceConnection conn = new ServiceConnection() {
            public void onServiceConnected(ComponentName name, IBinder service) {
                try {
                    IRemoteViewsFactory.Stub.asInterface(service).onDestroy(intent);
                } catch (RemoteException re) {
                    Slog.e(AppWidgetServiceImpl.TAG, "Error calling remove view factory", re);
                }
                AppWidgetServiceImpl.this.mContext.unbindService(this);
            }

            public void onServiceDisconnected(ComponentName name) {
            }
        };
        long token = Binder.clearCallingIdentity();
        try {
            this.mContext.bindServiceAsUser(intent, conn, 33554433, widget.provider.info.getProfile());
        } finally {
            Binder.restoreCallingIdentity(token);
        }
    }

    private void incrementAppWidgetServiceRefCount(int appWidgetId, Pair<Integer, Intent.FilterComparison> serviceId) {
        HashSet<Integer> appWidgetIds;
        if (this.mRemoteViewsServicesAppWidgets.containsKey(serviceId)) {
            appWidgetIds = this.mRemoteViewsServicesAppWidgets.get(serviceId);
        } else {
            appWidgetIds = new HashSet<>();
            this.mRemoteViewsServicesAppWidgets.put(serviceId, appWidgetIds);
        }
        appWidgetIds.add(Integer.valueOf(appWidgetId));
    }

    /* access modifiers changed from: private */
    public void decrementAppWidgetServiceRefCount(Widget widget) {
        Iterator<Pair<Integer, Intent.FilterComparison>> it = this.mRemoteViewsServicesAppWidgets.keySet().iterator();
        while (it.hasNext()) {
            Pair<Integer, Intent.FilterComparison> key = it.next();
            HashSet<Integer> ids = this.mRemoteViewsServicesAppWidgets.get(key);
            if (ids.remove(Integer.valueOf(widget.appWidgetId)) && ids.isEmpty()) {
                destroyRemoteViewsService(((Intent.FilterComparison) key.second).getIntent(), widget);
                it.remove();
            }
        }
    }

    /* access modifiers changed from: private */
    public void saveGroupStateAsync(int groupId) {
        this.mSaveStateHandler.post(new SaveStateRunnable(groupId));
    }

    private void updateAppWidgetInstanceLocked(Widget widget, RemoteViews views, boolean isPartialUpdate) {
        if (widget != null && widget.provider != null && !widget.provider.zombie && !widget.host.zombie) {
            if (!isPartialUpdate || widget.views == null) {
                widget.views = views;
            } else {
                widget.views.mergeRemoteViews(views);
            }
            if (!(UserHandle.getAppId(Binder.getCallingUid()) == 1000 || widget.views == null)) {
                int estimateMemoryUsage = widget.views.estimateMemoryUsage();
                int memoryUsage = estimateMemoryUsage;
                if (estimateMemoryUsage > this.mMaxWidgetBitmapMemory) {
                    widget.views = null;
                    throw new IllegalArgumentException("RemoteViews for widget update exceeds maximum bitmap memory usage (used: " + memoryUsage + ", max: " + this.mMaxWidgetBitmapMemory + ")");
                }
            }
            scheduleNotifyUpdateAppWidgetLocked(widget, widget.getEffectiveViewsLocked());
        }
    }

    private void scheduleNotifyAppWidgetViewDataChanged(Widget widget, int viewId) {
        if (viewId != 0 && viewId != 1) {
            long requestId = UPDATE_COUNTER.incrementAndGet();
            if (widget != null) {
                widget.updateSequenceNos.put(viewId, requestId);
            }
            if (widget != null && widget.host != null && !widget.host.zombie && widget.host.callbacks != null && widget.provider != null && !widget.provider.zombie) {
                SomeArgs args = SomeArgs.obtain();
                args.arg1 = widget.host;
                args.arg2 = widget.host.callbacks;
                args.arg3 = Long.valueOf(requestId);
                args.argi1 = widget.appWidgetId;
                args.argi2 = viewId;
                this.mCallbackHandler.obtainMessage(4, args).sendToTarget();
            }
        }
    }

    /* access modifiers changed from: private */
    public void handleNotifyAppWidgetViewDataChanged(Host host, IAppWidgetHost callbacks, int appWidgetId, int viewId, long requestId) {
        try {
            callbacks.viewDataChanged(appWidgetId, viewId);
            host.lastWidgetUpdateSequenceNo = requestId;
        } catch (RemoteException e) {
            callbacks = null;
        }
        synchronized (this.mLock) {
            if (callbacks == null) {
                host.callbacks = null;
                for (Pair<Integer, Intent.FilterComparison> key : this.mRemoteViewsServicesAppWidgets.keySet()) {
                    if (this.mRemoteViewsServicesAppWidgets.get(key).contains(Integer.valueOf(appWidgetId))) {
                        bindService(((Intent.FilterComparison) key.second).getIntent(), new ServiceConnection() {
                            public void onServiceConnected(ComponentName name, IBinder service) {
                                try {
                                    IRemoteViewsFactory.Stub.asInterface(service).onDataSetChangedAsync();
                                } catch (RemoteException e) {
                                    Slog.e(AppWidgetServiceImpl.TAG, "Error calling onDataSetChangedAsync()", e);
                                }
                                AppWidgetServiceImpl.this.mContext.unbindService(this);
                            }

                            public void onServiceDisconnected(ComponentName name) {
                            }
                        }, new UserHandle(UserHandle.getUserId(((Integer) key.first).intValue())));
                    }
                }
            }
        }
    }

    private void scheduleNotifyUpdateAppWidgetLocked(Widget widget, RemoteViews updateViews) {
        long requestId = UPDATE_COUNTER.incrementAndGet();
        if (widget != null) {
            widget.updateSequenceNos.put(0, requestId);
        }
        if (widget != null && widget.provider != null && !widget.provider.zombie && widget.host.callbacks != null && !widget.host.zombie) {
            SomeArgs args = SomeArgs.obtain();
            args.arg1 = widget.host;
            args.arg2 = widget.host.callbacks;
            args.arg3 = updateViews != null ? updateViews.clone() : null;
            args.arg4 = Long.valueOf(requestId);
            args.argi1 = widget.appWidgetId;
            this.mCallbackHandler.obtainMessage(1, args).sendToTarget();
        }
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: No exception handlers in catch block: Catch:{  } */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void handleNotifyUpdateAppWidget(com.android.server.appwidget.AppWidgetServiceImpl.Host r6, com.android.internal.appwidget.IAppWidgetHost r7, int r8, android.widget.RemoteViews r9, long r10) {
        /*
            r5 = this;
            r7.updateAppWidget(r8, r9)     // Catch:{ RemoteException -> 0x0006 }
            r6.lastWidgetUpdateSequenceNo = r10     // Catch:{ RemoteException -> 0x0006 }
            goto L_0x0026
        L_0x0006:
            r0 = move-exception
            java.lang.Object r1 = r5.mLock
            monitor-enter(r1)
            java.lang.String r2 = "AppWidgetServiceImpl"
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ all -> 0x0027 }
            r3.<init>()     // Catch:{ all -> 0x0027 }
            java.lang.String r4 = "Widget host dead: "
            r3.append(r4)     // Catch:{ all -> 0x0027 }
            com.android.server.appwidget.AppWidgetServiceImpl$HostId r4 = r6.id     // Catch:{ all -> 0x0027 }
            r3.append(r4)     // Catch:{ all -> 0x0027 }
            java.lang.String r3 = r3.toString()     // Catch:{ all -> 0x0027 }
            android.util.Slog.e(r2, r3, r0)     // Catch:{ all -> 0x0027 }
            r2 = 0
            r6.callbacks = r2     // Catch:{ all -> 0x0027 }
            monitor-exit(r1)     // Catch:{ all -> 0x0027 }
        L_0x0026:
            return
        L_0x0027:
            r2 = move-exception
            monitor-exit(r1)     // Catch:{ all -> 0x0027 }
            throw r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.appwidget.AppWidgetServiceImpl.handleNotifyUpdateAppWidget(com.android.server.appwidget.AppWidgetServiceImpl$Host, com.android.internal.appwidget.IAppWidgetHost, int, android.widget.RemoteViews, long):void");
    }

    private void scheduleNotifyProviderChangedLocked(Widget widget) {
        long requestId = UPDATE_COUNTER.incrementAndGet();
        if (widget != null) {
            widget.updateSequenceNos.clear();
            widget.updateSequenceNos.append(1, requestId);
        }
        if (widget != null && widget.provider != null && !widget.provider.zombie && widget.host.callbacks != null && !widget.host.zombie) {
            SomeArgs args = SomeArgs.obtain();
            args.arg1 = widget.host;
            args.arg2 = widget.host.callbacks;
            args.arg3 = widget.provider.info;
            args.arg4 = Long.valueOf(requestId);
            args.argi1 = widget.appWidgetId;
            this.mCallbackHandler.obtainMessage(2, args).sendToTarget();
        }
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: No exception handlers in catch block: Catch:{  } */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void handleNotifyProviderChanged(com.android.server.appwidget.AppWidgetServiceImpl.Host r6, com.android.internal.appwidget.IAppWidgetHost r7, int r8, android.appwidget.AppWidgetProviderInfo r9, long r10) {
        /*
            r5 = this;
            r7.providerChanged(r8, r9)     // Catch:{ RemoteException -> 0x0006 }
            r6.lastWidgetUpdateSequenceNo = r10     // Catch:{ RemoteException -> 0x0006 }
            goto L_0x0026
        L_0x0006:
            r0 = move-exception
            java.lang.Object r1 = r5.mLock
            monitor-enter(r1)
            java.lang.String r2 = "AppWidgetServiceImpl"
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ all -> 0x0027 }
            r3.<init>()     // Catch:{ all -> 0x0027 }
            java.lang.String r4 = "Widget host dead: "
            r3.append(r4)     // Catch:{ all -> 0x0027 }
            com.android.server.appwidget.AppWidgetServiceImpl$HostId r4 = r6.id     // Catch:{ all -> 0x0027 }
            r3.append(r4)     // Catch:{ all -> 0x0027 }
            java.lang.String r3 = r3.toString()     // Catch:{ all -> 0x0027 }
            android.util.Slog.e(r2, r3, r0)     // Catch:{ all -> 0x0027 }
            r2 = 0
            r6.callbacks = r2     // Catch:{ all -> 0x0027 }
            monitor-exit(r1)     // Catch:{ all -> 0x0027 }
        L_0x0026:
            return
        L_0x0027:
            r2 = move-exception
            monitor-exit(r1)     // Catch:{ all -> 0x0027 }
            throw r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.appwidget.AppWidgetServiceImpl.handleNotifyProviderChanged(com.android.server.appwidget.AppWidgetServiceImpl$Host, com.android.internal.appwidget.IAppWidgetHost, int, android.appwidget.AppWidgetProviderInfo, long):void");
    }

    private void scheduleNotifyGroupHostsForProvidersChangedLocked(int userId) {
        int[] profileIds = this.mSecurityPolicy.getEnabledGroupProfileIds(userId);
        for (int i = this.mHosts.size() - 1; i >= 0; i--) {
            Host host = this.mHosts.get(i);
            boolean hostInGroup = false;
            int M = profileIds.length;
            int j = 0;
            while (true) {
                if (j >= M) {
                    break;
                }
                if (host.getUserId() == profileIds[j]) {
                    hostInGroup = true;
                    break;
                }
                j++;
            }
            if (hostInGroup && host != null && !host.zombie && host.callbacks != null) {
                SomeArgs args = SomeArgs.obtain();
                args.arg1 = host;
                args.arg2 = host.callbacks;
                this.mCallbackHandler.obtainMessage(3, args).sendToTarget();
            }
        }
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: No exception handlers in catch block: Catch:{  } */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void handleNotifyProvidersChanged(com.android.server.appwidget.AppWidgetServiceImpl.Host r6, com.android.internal.appwidget.IAppWidgetHost r7) {
        /*
            r5 = this;
            r7.providersChanged()     // Catch:{ RemoteException -> 0x0004 }
            goto L_0x0024
        L_0x0004:
            r0 = move-exception
            java.lang.Object r1 = r5.mLock
            monitor-enter(r1)
            java.lang.String r2 = "AppWidgetServiceImpl"
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ all -> 0x0025 }
            r3.<init>()     // Catch:{ all -> 0x0025 }
            java.lang.String r4 = "Widget host dead: "
            r3.append(r4)     // Catch:{ all -> 0x0025 }
            com.android.server.appwidget.AppWidgetServiceImpl$HostId r4 = r6.id     // Catch:{ all -> 0x0025 }
            r3.append(r4)     // Catch:{ all -> 0x0025 }
            java.lang.String r3 = r3.toString()     // Catch:{ all -> 0x0025 }
            android.util.Slog.e(r2, r3, r0)     // Catch:{ all -> 0x0025 }
            r2 = 0
            r6.callbacks = r2     // Catch:{ all -> 0x0025 }
            monitor-exit(r1)     // Catch:{ all -> 0x0025 }
        L_0x0024:
            return
        L_0x0025:
            r2 = move-exception
            monitor-exit(r1)     // Catch:{ all -> 0x0025 }
            throw r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.appwidget.AppWidgetServiceImpl.handleNotifyProvidersChanged(com.android.server.appwidget.AppWidgetServiceImpl$Host, com.android.internal.appwidget.IAppWidgetHost):void");
    }

    private static boolean isLocalBinder() {
        return Process.myPid() == Binder.getCallingPid();
    }

    /* access modifiers changed from: private */
    public static RemoteViews cloneIfLocalBinder(RemoteViews rv) {
        if (!isLocalBinder() || rv == null) {
            return rv;
        }
        return rv.clone();
    }

    private static AppWidgetProviderInfo cloneIfLocalBinder(AppWidgetProviderInfo info) {
        if (!isLocalBinder() || info == null) {
            return info;
        }
        return info.clone();
    }

    private static Bundle cloneIfLocalBinder(Bundle bundle) {
        if (!isLocalBinder() || bundle == null) {
            return bundle;
        }
        return (Bundle) bundle.clone();
    }

    private Widget lookupWidgetLocked(int appWidgetId, int uid, String packageName) {
        int N = this.mWidgets.size();
        for (int i = 0; i < N; i++) {
            Widget widget = this.mWidgets.get(i);
            if (widget.appWidgetId == appWidgetId && this.mSecurityPolicy.canAccessAppWidget(widget, uid, packageName)) {
                return widget;
            }
        }
        return null;
    }

    private Provider lookupProviderLocked(ProviderId id) {
        int N = this.mProviders.size();
        for (int i = 0; i < N; i++) {
            Provider provider = this.mProviders.get(i);
            if (provider.id.equals(id)) {
                return provider;
            }
        }
        return null;
    }

    private Host lookupHostLocked(HostId hostId) {
        int N = this.mHosts.size();
        for (int i = 0; i < N; i++) {
            Host host = this.mHosts.get(i);
            if (host.id.equals(hostId)) {
                return host;
            }
        }
        return null;
    }

    private void pruneHostLocked(Host host) {
        if (host.widgets.size() == 0 && host.callbacks == null) {
            if (DEBUG) {
                Slog.i(TAG, "Pruning host " + host.id);
            }
            this.mHosts.remove(host);
        }
    }

    private void loadGroupWidgetProvidersLocked(int[] profileIds) {
        List<ResolveInfo> allReceivers = null;
        Intent intent = new Intent("android.appwidget.action.APPWIDGET_UPDATE");
        for (int profileId : profileIds) {
            List<ResolveInfo> receivers = queryIntentReceivers(intent, profileId);
            if (receivers != null && !receivers.isEmpty()) {
                if (allReceivers == null) {
                    allReceivers = new ArrayList<>();
                }
                allReceivers.addAll(receivers);
            }
        }
        int N = allReceivers == null ? 0 : allReceivers.size();
        for (int i = 0; i < N; i++) {
            addProviderLocked(allReceivers.get(i));
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:6:0x0016, code lost:
        r0 = new android.content.ComponentName(r9.activityInfo.packageName, r9.activityInfo.name);
        r2 = new com.android.server.appwidget.AppWidgetServiceImpl.ProviderId(r9.activityInfo.applicationInfo.uid, r0, (com.android.server.appwidget.AppWidgetServiceImpl.AnonymousClass1) null);
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private boolean addProviderLocked(android.content.pm.ResolveInfo r9) {
        /*
            r8 = this;
            android.content.pm.ActivityInfo r0 = r9.activityInfo
            android.content.pm.ApplicationInfo r0 = r0.applicationInfo
            int r0 = r0.flags
            r1 = 262144(0x40000, float:3.67342E-40)
            r0 = r0 & r1
            r1 = 0
            if (r0 == 0) goto L_0x000d
            return r1
        L_0x000d:
            android.content.pm.ActivityInfo r0 = r9.activityInfo
            boolean r0 = r0.isEnabled()
            if (r0 != 0) goto L_0x0016
            return r1
        L_0x0016:
            android.content.ComponentName r0 = new android.content.ComponentName
            android.content.pm.ActivityInfo r2 = r9.activityInfo
            java.lang.String r2 = r2.packageName
            android.content.pm.ActivityInfo r3 = r9.activityInfo
            java.lang.String r3 = r3.name
            r0.<init>(r2, r3)
            com.android.server.appwidget.AppWidgetServiceImpl$ProviderId r2 = new com.android.server.appwidget.AppWidgetServiceImpl$ProviderId
            android.content.pm.ActivityInfo r3 = r9.activityInfo
            android.content.pm.ApplicationInfo r3 = r3.applicationInfo
            int r3 = r3.uid
            r4 = 0
            r2.<init>(r3, r0)
            com.android.server.appwidget.AppWidgetServiceImpl$Provider r3 = r8.parseProviderInfoXml(r2, r9, r4)
            if (r3 == 0) goto L_0x007a
            com.android.server.appwidget.AppWidgetServiceImpl$Provider r5 = r8.lookupProviderLocked(r2)
            if (r5 != 0) goto L_0x0046
            com.android.server.appwidget.AppWidgetServiceImpl$ProviderId r6 = new com.android.server.appwidget.AppWidgetServiceImpl$ProviderId
            r7 = -1
            r6.<init>(r7, r0)
            r4 = r6
            com.android.server.appwidget.AppWidgetServiceImpl$Provider r5 = r8.lookupProviderLocked(r4)
        L_0x0046:
            if (r5 == 0) goto L_0x0073
            boolean r4 = r5.zombie
            if (r4 == 0) goto L_0x0078
            boolean r4 = r8.mSafeMode
            if (r4 != 0) goto L_0x0078
            r5.id = r2
            r5.zombie = r1
            android.appwidget.AppWidgetProviderInfo r1 = r3.info
            r5.info = r1
            boolean r1 = DEBUG
            if (r1 == 0) goto L_0x0078
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r4 = "Provider placeholder now reified: "
            r1.append(r4)
            r1.append(r5)
            java.lang.String r1 = r1.toString()
            java.lang.String r4 = "AppWidgetServiceImpl"
            android.util.Slog.i(r4, r1)
            goto L_0x0078
        L_0x0073:
            java.util.ArrayList<com.android.server.appwidget.AppWidgetServiceImpl$Provider> r1 = r8.mProviders
            r1.add(r3)
        L_0x0078:
            r1 = 1
            return r1
        L_0x007a:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.appwidget.AppWidgetServiceImpl.addProviderLocked(android.content.pm.ResolveInfo):boolean");
    }

    private void deleteWidgetsLocked(Provider provider, int userId) {
        for (int i = provider.widgets.size() - 1; i >= 0; i--) {
            Widget widget = provider.widgets.get(i);
            if (userId == -1 || userId == widget.host.getUserId()) {
                provider.widgets.remove(i);
                updateAppWidgetInstanceLocked(widget, (RemoteViews) null, false);
                widget.host.widgets.remove(widget);
                removeWidgetLocked(widget);
                widget.provider = null;
                pruneHostLocked(widget.host);
                widget.host = null;
            }
        }
    }

    private void deleteProviderLocked(Provider provider) {
        deleteWidgetsLocked(provider, -1);
        this.mProviders.remove(provider);
        cancelBroadcastsLocked(provider);
    }

    private void sendEnableIntentLocked(Provider p) {
        Intent intent = new Intent("android.appwidget.action.APPWIDGET_ENABLED");
        intent.setComponent(p.info.provider);
        sendBroadcastAsUser(intent, p.info.getProfile());
    }

    private void sendUpdateIntentLocked(Provider provider, int[] appWidgetIds) {
        Intent intent = new Intent("android.appwidget.action.APPWIDGET_UPDATE");
        intent.putExtra("appWidgetIds", appWidgetIds);
        intent.setComponent(provider.info.provider);
        sendBroadcastAsUser(intent, provider.info.getProfile());
    }

    private void sendDeletedIntentLocked(Widget widget) {
        Intent intent = new Intent("android.appwidget.action.APPWIDGET_DELETED");
        intent.setComponent(widget.provider.info.provider);
        intent.putExtra("appWidgetId", widget.appWidgetId);
        sendBroadcastAsUser(intent, widget.provider.info.getProfile());
    }

    private void sendDisabledIntentLocked(Provider provider) {
        Intent intent = new Intent("android.appwidget.action.APPWIDGET_DISABLED");
        intent.setComponent(provider.info.provider);
        sendBroadcastAsUser(intent, provider.info.getProfile());
    }

    public void sendOptionsChangedIntentLocked(Widget widget) {
        Intent intent = new Intent("android.appwidget.action.APPWIDGET_UPDATE_OPTIONS");
        intent.setComponent(widget.provider.info.provider);
        intent.putExtra("appWidgetId", widget.appWidgetId);
        intent.putExtra("appWidgetOptions", widget.options);
        sendBroadcastAsUser(intent, widget.provider.info.getProfile());
    }

    private void registerForBroadcastsLocked(Provider provider, int[] appWidgetIds) {
        if (provider.info.updatePeriodMillis > 0) {
            boolean alreadyRegistered = provider.broadcast != null;
            Intent intent = new Intent("android.appwidget.action.APPWIDGET_UPDATE");
            intent.putExtra("appWidgetIds", appWidgetIds);
            intent.setComponent(provider.info.provider);
            long token = Binder.clearCallingIdentity();
            try {
                provider.broadcast = PendingIntent.getBroadcastAsUser(this.mContext, 1, intent, 134217728, provider.info.getProfile());
                if (!alreadyRegistered) {
                    this.mSaveStateHandler.post(new Runnable((long) Math.max(provider.info.updatePeriodMillis, MIN_UPDATE_PERIOD), provider.broadcast) {
                        private final /* synthetic */ long f$1;
                        private final /* synthetic */ PendingIntent f$2;

                        {
                            this.f$1 = r2;
                            this.f$2 = r4;
                        }

                        public final void run() {
                            AppWidgetServiceImpl.this.lambda$registerForBroadcastsLocked$1$AppWidgetServiceImpl(this.f$1, this.f$2);
                        }
                    });
                }
            } finally {
                Binder.restoreCallingIdentity(token);
            }
        }
    }

    public /* synthetic */ void lambda$registerForBroadcastsLocked$1$AppWidgetServiceImpl(long period, PendingIntent broadcast) {
        this.mAlarmManager.setInexactRepeating(2, SystemClock.elapsedRealtime() + period, period, broadcast);
    }

    private static int[] getWidgetIds(ArrayList<Widget> widgets) {
        int instancesSize = widgets.size();
        int[] appWidgetIds = new int[instancesSize];
        for (int i = 0; i < instancesSize; i++) {
            appWidgetIds[i] = widgets.get(i).appWidgetId;
        }
        return appWidgetIds;
    }

    private static void dumpProvider(Provider provider, int index, PrintWriter pw) {
        AppWidgetProviderInfo info = provider.info;
        pw.print("  [");
        pw.print(index);
        pw.print("] provider ");
        pw.println(provider.id);
        pw.print("    min=(");
        pw.print(info.minWidth);
        pw.print("x");
        pw.print(info.minHeight);
        pw.print(")   minResize=(");
        pw.print(info.minResizeWidth);
        pw.print("x");
        pw.print(info.minResizeHeight);
        pw.print(") updatePeriodMillis=");
        pw.print(info.updatePeriodMillis);
        pw.print(" resizeMode=");
        pw.print(info.resizeMode);
        pw.print(" widgetCategory=");
        pw.print(info.widgetCategory);
        pw.print(" autoAdvanceViewId=");
        pw.print(info.autoAdvanceViewId);
        pw.print(" initialLayout=#");
        pw.print(Integer.toHexString(info.initialLayout));
        pw.print(" initialKeyguardLayout=#");
        pw.print(Integer.toHexString(info.initialKeyguardLayout));
        pw.print(" zombie=");
        pw.println(provider.zombie);
    }

    private static void dumpHost(Host host, int index, PrintWriter pw) {
        pw.print("  [");
        pw.print(index);
        pw.print("] hostId=");
        pw.println(host.id);
        pw.print("    callbacks=");
        pw.println(host.callbacks);
        pw.print("    widgets.size=");
        pw.print(host.widgets.size());
        pw.print(" zombie=");
        pw.println(host.zombie);
    }

    private static void dumpGrant(Pair<Integer, String> grant, int index, PrintWriter pw) {
        pw.print("  [");
        pw.print(index);
        pw.print(']');
        pw.print(" user=");
        pw.print(grant.first);
        pw.print(" package=");
        pw.println((String) grant.second);
    }

    private static void dumpWidget(Widget widget, int index, PrintWriter pw) {
        pw.print("  [");
        pw.print(index);
        pw.print("] id=");
        pw.println(widget.appWidgetId);
        pw.print("    host=");
        pw.println(widget.host.id);
        if (widget.provider != null) {
            pw.print("    provider=");
            pw.println(widget.provider.id);
        }
        if (widget.host != null) {
            pw.print("    host.callbacks=");
            pw.println(widget.host.callbacks);
        }
        if (widget.views != null) {
            pw.print("    views=");
            pw.println(widget.views);
        }
    }

    /* access modifiers changed from: private */
    public static void serializeProvider(XmlSerializer out, Provider p) throws IOException {
        out.startTag((String) null, "p");
        out.attribute((String) null, SplitScreenReporter.STR_PKG, p.info.provider.getPackageName());
        out.attribute((String) null, "cl", p.info.provider.getClassName());
        out.attribute((String) null, "tag", Integer.toHexString(p.tag));
        if (!TextUtils.isEmpty(p.infoTag)) {
            out.attribute((String) null, "info_tag", p.infoTag);
        }
        out.endTag((String) null, "p");
    }

    /* access modifiers changed from: private */
    public static void serializeHost(XmlSerializer out, Host host) throws IOException {
        out.startTag((String) null, "h");
        out.attribute((String) null, SplitScreenReporter.STR_PKG, host.id.packageName);
        out.attribute((String) null, "id", Integer.toHexString(host.id.hostId));
        out.attribute((String) null, "tag", Integer.toHexString(host.tag));
        out.endTag((String) null, "h");
    }

    /* access modifiers changed from: private */
    public static void serializeAppWidget(XmlSerializer out, Widget widget) throws IOException {
        out.startTag((String) null, "g");
        out.attribute((String) null, "id", Integer.toHexString(widget.appWidgetId));
        out.attribute((String) null, "rid", Integer.toHexString(widget.restoredId));
        out.attribute((String) null, "h", Integer.toHexString(widget.host.tag));
        if (widget.provider != null) {
            out.attribute((String) null, "p", Integer.toHexString(widget.provider.tag));
        }
        if (widget.options != null) {
            int minWidth = widget.options.getInt("appWidgetMinWidth");
            int minHeight = widget.options.getInt("appWidgetMinHeight");
            int maxWidth = widget.options.getInt("appWidgetMaxWidth");
            int maxHeight = widget.options.getInt("appWidgetMaxHeight");
            int i = 0;
            out.attribute((String) null, "min_width", Integer.toHexString(minWidth > 0 ? minWidth : 0));
            out.attribute((String) null, "min_height", Integer.toHexString(minHeight > 0 ? minHeight : 0));
            out.attribute((String) null, "max_width", Integer.toHexString(maxWidth > 0 ? maxWidth : 0));
            if (maxHeight > 0) {
                i = maxHeight;
            }
            out.attribute((String) null, "max_height", Integer.toHexString(i));
            out.attribute((String) null, "host_category", Integer.toHexString(widget.options.getInt("appWidgetCategory")));
        }
        out.endTag((String) null, "g");
    }

    public List<String> getWidgetParticipants(int userId) {
        return this.mBackupRestoreController.getWidgetParticipants(userId);
    }

    public byte[] getWidgetState(String packageName, int userId) {
        return this.mBackupRestoreController.getWidgetState(packageName, userId);
    }

    public void restoreStarting(int userId) {
        this.mBackupRestoreController.restoreStarting(userId);
    }

    public void restoreWidgetState(String packageName, byte[] restoredState, int userId) {
        this.mBackupRestoreController.restoreWidgetState(packageName, restoredState, userId);
    }

    public void restoreFinished(int userId) {
        this.mBackupRestoreController.restoreFinished(userId);
    }

    private Provider parseProviderInfoXml(ProviderId providerId, ResolveInfo ri, Provider oldProvider) {
        AppWidgetProviderInfo info = null;
        if (oldProvider != null && !TextUtils.isEmpty(oldProvider.infoTag)) {
            info = parseAppWidgetProviderInfo(providerId, ri.activityInfo, oldProvider.infoTag);
        }
        if (info == null) {
            info = parseAppWidgetProviderInfo(providerId, ri.activityInfo, "android.appwidget.provider");
        }
        if (info == null) {
            return null;
        }
        Provider provider = new Provider();
        provider.id = providerId;
        provider.info = info;
        return provider;
    }

    /* Debug info: failed to restart local var, previous not found, register: 20 */
    /* JADX WARNING: Code restructure failed: missing block: B:53:0x0169, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:55:?, code lost:
        android.os.Binder.restoreCallingIdentity(r15);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:56:0x016d, code lost:
        throw r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:57:0x016e, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:58:0x016f, code lost:
        r4 = r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:60:?, code lost:
        throw r4;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:61:0x0171, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:62:0x0172, code lost:
        r7 = r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:63:0x0173, code lost:
        if (r8 != null) goto L_0x0175;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:65:?, code lost:
        $closeResource(r4, r8);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:66:0x0178, code lost:
        throw r7;
     */
    /* JADX WARNING: Exception block dominator not found, dom blocks: [B:5:0x001b, B:25:0x0094] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private android.appwidget.AppWidgetProviderInfo parseAppWidgetProviderInfo(com.android.server.appwidget.AppWidgetServiceImpl.ProviderId r21, android.content.pm.ActivityInfo r22, java.lang.String r23) {
        /*
            r20 = this;
            r1 = r20
            r2 = r21
            r3 = r22
            r4 = r23
            java.lang.String r5 = " for user "
            java.lang.String r6 = "AppWidgetServiceImpl"
            r7 = 0
            android.content.Context r0 = r1.mContext     // Catch:{ NameNotFoundException | IOException | XmlPullParserException -> 0x0179 }
            android.content.pm.PackageManager r0 = r0.getPackageManager()     // Catch:{ NameNotFoundException | IOException | XmlPullParserException -> 0x0179 }
            android.content.res.XmlResourceParser r0 = r3.loadXmlMetaData(r0, r4)     // Catch:{ NameNotFoundException | IOException | XmlPullParserException -> 0x0179 }
            r8 = r0
            if (r8 != 0) goto L_0x0043
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ all -> 0x016e }
            r0.<init>()     // Catch:{ all -> 0x016e }
            java.lang.String r9 = "No "
            r0.append(r9)     // Catch:{ all -> 0x016e }
            r0.append(r4)     // Catch:{ all -> 0x016e }
            java.lang.String r9 = " meta-data for AppWidget provider '"
            r0.append(r9)     // Catch:{ all -> 0x016e }
            r0.append(r2)     // Catch:{ all -> 0x016e }
            r9 = 39
            r0.append(r9)     // Catch:{ all -> 0x016e }
            java.lang.String r0 = r0.toString()     // Catch:{ all -> 0x016e }
            android.util.Slog.w(r6, r0)     // Catch:{ all -> 0x016e }
            if (r8 == 0) goto L_0x0042
            $closeResource(r7, r8)     // Catch:{ NameNotFoundException | IOException | XmlPullParserException -> 0x0179 }
        L_0x0042:
            return r7
        L_0x0043:
            android.util.AttributeSet r0 = android.util.Xml.asAttributeSet(r8)     // Catch:{ all -> 0x016e }
            r9 = r0
        L_0x0048:
            int r0 = r8.next()     // Catch:{ all -> 0x016e }
            r10 = r0
            r11 = 2
            r12 = 1
            if (r0 == r12) goto L_0x0054
            if (r10 == r11) goto L_0x0054
            goto L_0x0048
        L_0x0054:
            java.lang.String r0 = r8.getName()     // Catch:{ all -> 0x016e }
            r13 = r0
            java.lang.String r0 = "appwidget-provider"
            boolean r0 = r0.equals(r13)     // Catch:{ all -> 0x016e }
            if (r0 != 0) goto L_0x0084
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ all -> 0x016e }
            r0.<init>()     // Catch:{ all -> 0x016e }
            java.lang.String r11 = "Meta-data does not start with appwidget-provider tag for AppWidget provider "
            r0.append(r11)     // Catch:{ all -> 0x016e }
            android.content.ComponentName r11 = r2.componentName     // Catch:{ all -> 0x016e }
            r0.append(r11)     // Catch:{ all -> 0x016e }
            r0.append(r5)     // Catch:{ all -> 0x016e }
            int r11 = r2.uid     // Catch:{ all -> 0x016e }
            r0.append(r11)     // Catch:{ all -> 0x016e }
            java.lang.String r0 = r0.toString()     // Catch:{ all -> 0x016e }
            android.util.Slog.w(r6, r0)     // Catch:{ all -> 0x016e }
            $closeResource(r7, r8)     // Catch:{ NameNotFoundException | IOException | XmlPullParserException -> 0x0179 }
            return r7
        L_0x0084:
            android.appwidget.AppWidgetProviderInfo r0 = new android.appwidget.AppWidgetProviderInfo     // Catch:{ all -> 0x016e }
            r0.<init>()     // Catch:{ all -> 0x016e }
            r14 = r0
            android.content.ComponentName r0 = r2.componentName     // Catch:{ all -> 0x016e }
            r14.provider = r0     // Catch:{ all -> 0x016e }
            r14.providerInfo = r3     // Catch:{ all -> 0x016e }
            long r15 = android.os.Binder.clearCallingIdentity()     // Catch:{ all -> 0x016e }
            android.content.Context r0 = r1.mContext     // Catch:{ all -> 0x0169 }
            android.content.pm.PackageManager r0 = r0.getPackageManager()     // Catch:{ all -> 0x0169 }
            int r7 = r2.uid     // Catch:{ all -> 0x0169 }
            int r7 = android.os.UserHandle.getUserId(r7)     // Catch:{ all -> 0x0169 }
            java.lang.String r11 = r3.packageName     // Catch:{ all -> 0x0169 }
            r12 = 0
            android.content.pm.ApplicationInfo r11 = r0.getApplicationInfoAsUser(r11, r12, r7)     // Catch:{ all -> 0x0169 }
            android.content.res.Resources r18 = r0.getResourcesForApplication(r11)     // Catch:{ all -> 0x0169 }
            r0 = r18
            android.os.Binder.restoreCallingIdentity(r15)     // Catch:{ all -> 0x016e }
            int[] r7 = com.android.internal.R.styleable.AppWidgetProviderInfo     // Catch:{ all -> 0x016e }
            android.content.res.TypedArray r7 = r0.obtainAttributes(r9, r7)     // Catch:{ all -> 0x016e }
            android.util.TypedValue r11 = r7.peekValue(r12)     // Catch:{ all -> 0x016e }
            if (r11 == 0) goto L_0x00c1
            int r12 = r11.data     // Catch:{ all -> 0x016e }
            goto L_0x00c2
        L_0x00c1:
            r12 = 0
        L_0x00c2:
            r14.minWidth = r12     // Catch:{ all -> 0x016e }
            r12 = 1
            android.util.TypedValue r19 = r7.peekValue(r12)     // Catch:{ all -> 0x016e }
            r11 = r19
            if (r11 == 0) goto L_0x00d0
            int r12 = r11.data     // Catch:{ all -> 0x016e }
            goto L_0x00d1
        L_0x00d0:
            r12 = 0
        L_0x00d1:
            r14.minHeight = r12     // Catch:{ all -> 0x016e }
            r12 = 8
            android.util.TypedValue r12 = r7.peekValue(r12)     // Catch:{ all -> 0x016e }
            r11 = r12
            if (r11 == 0) goto L_0x00df
            int r12 = r11.data     // Catch:{ all -> 0x016e }
            goto L_0x00e1
        L_0x00df:
            int r12 = r14.minWidth     // Catch:{ all -> 0x016e }
        L_0x00e1:
            r14.minResizeWidth = r12     // Catch:{ all -> 0x016e }
            r12 = 9
            android.util.TypedValue r12 = r7.peekValue(r12)     // Catch:{ all -> 0x016e }
            r11 = r12
            if (r11 == 0) goto L_0x00ef
            int r12 = r11.data     // Catch:{ all -> 0x016e }
            goto L_0x00f1
        L_0x00ef:
            int r12 = r14.minHeight     // Catch:{ all -> 0x016e }
        L_0x00f1:
            r14.minResizeHeight = r12     // Catch:{ all -> 0x016e }
            r17 = r0
            r0 = 0
            r12 = 2
            int r12 = r7.getInt(r12, r0)     // Catch:{ all -> 0x016e }
            r14.updatePeriodMillis = r12     // Catch:{ all -> 0x016e }
            r12 = 3
            int r12 = r7.getResourceId(r12, r0)     // Catch:{ all -> 0x016e }
            r14.initialLayout = r12     // Catch:{ all -> 0x016e }
            r12 = 10
            int r12 = r7.getResourceId(r12, r0)     // Catch:{ all -> 0x016e }
            r14.initialKeyguardLayout = r12     // Catch:{ all -> 0x016e }
            r0 = 4
            java.lang.String r0 = r7.getString(r0)     // Catch:{ all -> 0x016e }
            if (r0 == 0) goto L_0x0120
            android.content.ComponentName r12 = new android.content.ComponentName     // Catch:{ all -> 0x016e }
            android.content.ComponentName r4 = r2.componentName     // Catch:{ all -> 0x016e }
            java.lang.String r4 = r4.getPackageName()     // Catch:{ all -> 0x016e }
            r12.<init>(r4, r0)     // Catch:{ all -> 0x016e }
            r14.configure = r12     // Catch:{ all -> 0x016e }
        L_0x0120:
            android.content.Context r4 = r1.mContext     // Catch:{ all -> 0x016e }
            android.content.pm.PackageManager r4 = r4.getPackageManager()     // Catch:{ all -> 0x016e }
            java.lang.CharSequence r4 = r3.loadLabel(r4)     // Catch:{ all -> 0x016e }
            java.lang.String r4 = r4.toString()     // Catch:{ all -> 0x016e }
            r14.label = r4     // Catch:{ all -> 0x016e }
            int r4 = r22.getIconResource()     // Catch:{ all -> 0x016e }
            r14.icon = r4     // Catch:{ all -> 0x016e }
            r4 = 5
            r12 = 0
            int r4 = r7.getResourceId(r4, r12)     // Catch:{ all -> 0x016e }
            r14.previewImage = r4     // Catch:{ all -> 0x016e }
            r4 = 6
            r12 = -1
            int r4 = r7.getResourceId(r4, r12)     // Catch:{ all -> 0x016e }
            r14.autoAdvanceViewId = r4     // Catch:{ all -> 0x016e }
            r4 = 7
            r12 = 0
            int r4 = r7.getInt(r4, r12)     // Catch:{ all -> 0x016e }
            r14.resizeMode = r4     // Catch:{ all -> 0x016e }
            r4 = 11
            r12 = 1
            int r4 = r7.getInt(r4, r12)     // Catch:{ all -> 0x016e }
            r14.widgetCategory = r4     // Catch:{ all -> 0x016e }
            r4 = 12
            r12 = 0
            int r4 = r7.getInt(r4, r12)     // Catch:{ all -> 0x016e }
            r14.widgetFeatures = r4     // Catch:{ all -> 0x016e }
            r7.recycle()     // Catch:{ all -> 0x016e }
            r4 = 0
            $closeResource(r4, r8)     // Catch:{ NameNotFoundException | IOException | XmlPullParserException -> 0x0179 }
            return r14
        L_0x0169:
            r0 = move-exception
            android.os.Binder.restoreCallingIdentity(r15)     // Catch:{ all -> 0x016e }
            throw r0     // Catch:{ all -> 0x016e }
        L_0x016e:
            r0 = move-exception
            r4 = r0
            throw r4     // Catch:{ all -> 0x0171 }
        L_0x0171:
            r0 = move-exception
            r7 = r0
            if (r8 == 0) goto L_0x0178
            $closeResource(r4, r8)     // Catch:{ NameNotFoundException | IOException | XmlPullParserException -> 0x0179 }
        L_0x0178:
            throw r7     // Catch:{ NameNotFoundException | IOException | XmlPullParserException -> 0x0179 }
        L_0x0179:
            r0 = move-exception
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            java.lang.String r7 = "XML parsing failed for AppWidget provider "
            r4.append(r7)
            android.content.ComponentName r7 = r2.componentName
            r4.append(r7)
            r4.append(r5)
            int r5 = r2.uid
            r4.append(r5)
            java.lang.String r4 = r4.toString()
            android.util.Slog.w(r6, r4, r0)
            r4 = 0
            return r4
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.appwidget.AppWidgetServiceImpl.parseAppWidgetProviderInfo(com.android.server.appwidget.AppWidgetServiceImpl$ProviderId, android.content.pm.ActivityInfo, java.lang.String):android.appwidget.AppWidgetProviderInfo");
    }

    private static /* synthetic */ void $closeResource(Throwable x0, AutoCloseable x1) {
        if (x0 != null) {
            try {
                x1.close();
            } catch (Throwable th) {
                x0.addSuppressed(th);
            }
        } else {
            x1.close();
        }
    }

    /* access modifiers changed from: private */
    public int getUidForPackage(String packageName, int userId) {
        PackageInfo pkgInfo = null;
        long identity = Binder.clearCallingIdentity();
        try {
            pkgInfo = this.mPackageManager.getPackageInfo(packageName, 0, userId);
        } catch (RemoteException e) {
        } catch (Throwable th) {
            Binder.restoreCallingIdentity(identity);
            throw th;
        }
        Binder.restoreCallingIdentity(identity);
        if (pkgInfo == null || pkgInfo.applicationInfo == null) {
            return -1;
        }
        return pkgInfo.applicationInfo.uid;
    }

    private ActivityInfo getProviderInfo(ComponentName componentName, int userId) {
        Intent intent = new Intent("android.appwidget.action.APPWIDGET_UPDATE");
        intent.setComponent(componentName);
        List<ResolveInfo> receivers = queryIntentReceivers(intent, userId);
        if (!receivers.isEmpty()) {
            return receivers.get(0).activityInfo;
        }
        return null;
    }

    private List<ResolveInfo> queryIntentReceivers(Intent intent, int userId) {
        long identity = Binder.clearCallingIdentity();
        int flags = 128 | 268435456;
        try {
            if (isProfileWithUnlockedParent(userId)) {
                flags |= 786432;
            }
            return this.mPackageManager.queryIntentReceivers(intent, intent.resolveTypeIfNeeded(this.mContext.getContentResolver()), flags | 1024, userId).getList();
        } catch (RemoteException e) {
            return Collections.emptyList();
        } finally {
            Binder.restoreCallingIdentity(identity);
        }
    }

    /* access modifiers changed from: package-private */
    public void handleUserUnlocked(int userId) {
        if (!isProfileWithLockedParent(userId)) {
            if (!this.mUserManager.isUserUnlockingOrUnlocked(userId)) {
                Slog.w(TAG, "User " + userId + " is no longer unlocked - exiting");
                return;
            }
            long time = SystemClock.elapsedRealtime();
            synchronized (this.mLock) {
                Trace.traceBegin(64, "appwidget ensure");
                ensureGroupStateLoadedLocked(userId);
                Trace.traceEnd(64);
                Trace.traceBegin(64, "appwidget reload");
                reloadWidgetsMaskedStateForGroup(this.mSecurityPolicy.getGroupParent(userId));
                Trace.traceEnd(64);
                int N = this.mProviders.size();
                for (int i = 0; i < N; i++) {
                    Provider provider = this.mProviders.get(i);
                    if (provider.getUserId() == userId) {
                        if (provider.widgets.size() > 0) {
                            Trace.traceBegin(64, "appwidget init " + provider.info.provider.getPackageName());
                            sendEnableIntentLocked(provider);
                            int[] appWidgetIds = getWidgetIds(provider.widgets);
                            sendUpdateIntentLocked(provider, appWidgetIds);
                            registerForBroadcastsLocked(provider, appWidgetIds);
                            Trace.traceEnd(64);
                        }
                    }
                }
            }
            Slog.i(TAG, "Processing of handleUserUnlocked u" + userId + " took " + (SystemClock.elapsedRealtime() - time) + " ms");
        }
    }

    /* Debug info: failed to restart local var, previous not found, register: 10 */
    /* JADX WARNING: Code restructure failed: missing block: B:16:0x0024, code lost:
        r9 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:17:0x0025, code lost:
        if (r7 != null) goto L_0x0027;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:19:?, code lost:
        $closeResource(r8, r7);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:20:0x002a, code lost:
        throw r9;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void loadGroupStateLocked(int[] r11) {
        /*
            r10 = this;
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            r1 = 0
            int r2 = r11.length
            r3 = 0
        L_0x0008:
            java.lang.String r4 = "AppWidgetServiceImpl"
            if (r3 >= r2) goto L_0x0043
            r5 = r11[r3]
            android.util.AtomicFile r6 = getSavedStateFile(r5)
            java.io.FileInputStream r7 = r6.openRead()     // Catch:{ IOException -> 0x002b }
            r8 = 0
            int r9 = r10.readProfileStateFromFileLocked(r7, r5, r0)     // Catch:{ all -> 0x0022 }
            r1 = r9
            if (r7 == 0) goto L_0x0021
            $closeResource(r8, r7)     // Catch:{ IOException -> 0x002b }
        L_0x0021:
            goto L_0x0040
        L_0x0022:
            r8 = move-exception
            throw r8     // Catch:{ all -> 0x0024 }
        L_0x0024:
            r9 = move-exception
            if (r7 == 0) goto L_0x002a
            $closeResource(r8, r7)     // Catch:{ IOException -> 0x002b }
        L_0x002a:
            throw r9     // Catch:{ IOException -> 0x002b }
        L_0x002b:
            r7 = move-exception
            java.lang.StringBuilder r8 = new java.lang.StringBuilder
            r8.<init>()
            java.lang.String r9 = "Failed to read state: "
            r8.append(r9)
            r8.append(r7)
            java.lang.String r8 = r8.toString()
            android.util.Slog.w(r4, r8)
        L_0x0040:
            int r3 = r3 + 1
            goto L_0x0008
        L_0x0043:
            if (r1 < 0) goto L_0x005b
            r10.bindLoadedWidgetsLocked(r0)
            r10.performUpgradeLocked(r1)
            r3 = 0
        L_0x004c:
            if (r3 >= r2) goto L_0x005a
            r4 = r11[r3]
            android.content.Context r5 = r10.mContext
            java.util.ArrayList<com.android.server.appwidget.AppWidgetServiceImpl$Provider> r6 = r10.mProviders
            com.android.server.appwidget.AppWidgetServiceImplInjector.updateWidgetPackagesLocked(r5, r6, r4)
            int r3 = r3 + 1
            goto L_0x004c
        L_0x005a:
            goto L_0x0081
        L_0x005b:
            java.lang.String r3 = "Failed to read state, clearing widgets and hosts."
            android.util.Slog.w(r4, r3)
            r10.clearWidgetsLocked()
            java.util.ArrayList<com.android.server.appwidget.AppWidgetServiceImpl$Host> r3 = r10.mHosts
            r3.clear()
            java.util.ArrayList<com.android.server.appwidget.AppWidgetServiceImpl$Provider> r3 = r10.mProviders
            int r3 = r3.size()
            r4 = 0
        L_0x006f:
            if (r4 >= r3) goto L_0x0081
            java.util.ArrayList<com.android.server.appwidget.AppWidgetServiceImpl$Provider> r5 = r10.mProviders
            java.lang.Object r5 = r5.get(r4)
            com.android.server.appwidget.AppWidgetServiceImpl$Provider r5 = (com.android.server.appwidget.AppWidgetServiceImpl.Provider) r5
            java.util.ArrayList<com.android.server.appwidget.AppWidgetServiceImpl$Widget> r5 = r5.widgets
            r5.clear()
            int r4 = r4 + 1
            goto L_0x006f
        L_0x0081:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.appwidget.AppWidgetServiceImpl.loadGroupStateLocked(int[]):void");
    }

    private void bindLoadedWidgetsLocked(List<LoadedWidgetState> loadedWidgets) {
        for (int i = loadedWidgets.size() - 1; i >= 0; i--) {
            LoadedWidgetState loadedWidget = loadedWidgets.remove(i);
            Widget widget = loadedWidget.widget;
            widget.provider = findProviderByTag(loadedWidget.providerTag);
            if (widget.provider != null) {
                widget.host = findHostByTag(loadedWidget.hostTag);
                if (widget.host != null) {
                    widget.provider.widgets.add(widget);
                    widget.host.widgets.add(widget);
                    addWidgetLocked(widget);
                }
            }
        }
    }

    private Provider findProviderByTag(int tag) {
        if (tag < 0) {
            return null;
        }
        int providerCount = this.mProviders.size();
        for (int i = 0; i < providerCount; i++) {
            Provider provider = this.mProviders.get(i);
            if (provider.tag == tag) {
                return provider;
            }
        }
        return null;
    }

    private Host findHostByTag(int tag) {
        if (tag < 0) {
            return null;
        }
        int hostCount = this.mHosts.size();
        for (int i = 0; i < hostCount; i++) {
            Host host = this.mHosts.get(i);
            if (host.tag == tag) {
                return host;
            }
        }
        return null;
    }

    /* access modifiers changed from: package-private */
    public void addWidgetLocked(Widget widget) {
        this.mWidgets.add(widget);
        onWidgetProviderAddedOrChangedLocked(widget);
    }

    /* access modifiers changed from: package-private */
    public void onWidgetProviderAddedOrChangedLocked(Widget widget) {
        if (widget.provider != null) {
            int userId = widget.provider.getUserId();
            ArraySet<String> packages = this.mWidgetPackages.get(userId);
            if (packages == null) {
                SparseArray<ArraySet<String>> sparseArray = this.mWidgetPackages;
                ArraySet<String> arraySet = new ArraySet<>();
                packages = arraySet;
                sparseArray.put(userId, arraySet);
            }
            packages.add(widget.provider.info.provider.getPackageName());
            if (widget.provider.isMaskedLocked()) {
                maskWidgetsViewsLocked(widget.provider, widget);
            } else {
                boolean unused = widget.clearMaskedViewsLocked();
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void removeWidgetLocked(Widget widget) {
        this.mWidgets.remove(widget);
        onWidgetRemovedLocked(widget);
    }

    private void onWidgetRemovedLocked(Widget widget) {
        if (widget.provider != null) {
            int userId = widget.provider.getUserId();
            String packageName = widget.provider.info.provider.getPackageName();
            ArraySet<String> packages = this.mWidgetPackages.get(userId);
            if (packages != null) {
                int N = this.mWidgets.size();
                int i = 0;
                while (i < N) {
                    Widget w = this.mWidgets.get(i);
                    if (w.provider == null || w.provider.getUserId() != userId || !packageName.equals(w.provider.info.provider.getPackageName())) {
                        i++;
                    } else {
                        return;
                    }
                }
                packages.remove(packageName);
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void clearWidgetsLocked() {
        this.mWidgets.clear();
        onWidgetsClearedLocked();
    }

    private void onWidgetsClearedLocked() {
        this.mWidgetPackages.clear();
    }

    public boolean isBoundWidgetPackage(String packageName, int userId) {
        if (Binder.getCallingUid() == 1000) {
            synchronized (this.mLock) {
                ArraySet<String> packages = this.mWidgetPackages.get(userId);
                if (packages == null) {
                    return false;
                }
                boolean contains = packages.contains(packageName);
                return contains;
            }
        }
        throw new SecurityException("Only the system process can call this");
    }

    /* access modifiers changed from: private */
    public void saveStateLocked(int userId) {
        tagProvidersAndHosts();
        for (int profileId : this.mSecurityPolicy.getEnabledGroupProfileIds(userId)) {
            AtomicFile file = getSavedStateFile(profileId);
            try {
                FileOutputStream stream = file.startWrite();
                if (writeProfileStateToFileLocked(stream, profileId)) {
                    file.finishWrite(stream);
                } else {
                    file.failWrite(stream);
                    Slog.w(TAG, "Failed to save state, restoring backup.");
                }
            } catch (IOException e) {
                Slog.w(TAG, "Failed open state file for write: " + e);
            }
            AppWidgetServiceImplInjector.updateWidgetPackagesLocked(this.mContext, this.mProviders, profileId);
        }
    }

    private void tagProvidersAndHosts() {
        int providerCount = this.mProviders.size();
        for (int i = 0; i < providerCount; i++) {
            this.mProviders.get(i).tag = i;
        }
        int hostCount = this.mHosts.size();
        for (int i2 = 0; i2 < hostCount; i2++) {
            this.mHosts.get(i2).tag = i2;
        }
    }

    private void clearProvidersAndHostsTagsLocked() {
        int providerCount = this.mProviders.size();
        for (int i = 0; i < providerCount; i++) {
            this.mProviders.get(i).tag = -1;
        }
        int hostCount = this.mHosts.size();
        for (int i2 = 0; i2 < hostCount; i2++) {
            this.mHosts.get(i2).tag = -1;
        }
    }

    private boolean writeProfileStateToFileLocked(FileOutputStream stream, int userId) {
        try {
            XmlSerializer out = new FastXmlSerializer();
            out.setOutput(stream, StandardCharsets.UTF_8.name());
            out.startDocument((String) null, true);
            out.startTag((String) null, "gs");
            out.attribute((String) null, "version", String.valueOf(1));
            int N = this.mProviders.size();
            for (int i = 0; i < N; i++) {
                Provider provider = this.mProviders.get(i);
                if (provider.getUserId() == userId) {
                    if (provider.shouldBePersisted()) {
                        serializeProvider(out, provider);
                    }
                }
            }
            int N2 = this.mHosts.size();
            for (int i2 = 0; i2 < N2; i2++) {
                Host host = this.mHosts.get(i2);
                if (host.getUserId() == userId) {
                    serializeHost(out, host);
                }
            }
            int N3 = this.mWidgets.size();
            for (int i3 = 0; i3 < N3; i3++) {
                Widget widget = this.mWidgets.get(i3);
                if (widget.host.getUserId() == userId) {
                    serializeAppWidget(out, widget);
                }
            }
            Iterator<Pair<Integer, String>> it = this.mPackagesWithBindWidgetPermission.iterator();
            while (it.hasNext()) {
                Pair<Integer, String> binding = it.next();
                if (((Integer) binding.first).intValue() == userId) {
                    out.startTag((String) null, "b");
                    out.attribute((String) null, "packageName", (String) binding.second);
                    out.endTag((String) null, "b");
                }
            }
            out.endTag((String) null, "gs");
            out.endDocument();
            return true;
        } catch (IOException e) {
            Slog.w(TAG, "Failed to write state: " + e);
            return false;
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:128:0x02b1, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:129:0x02b2, code lost:
        r1 = r26;
        r2 = r0;
        r18 = r5;
     */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Removed duplicated region for block: B:124:0x029f A[LOOP:0: B:4:0x001c->B:124:0x029f, LOOP_END] */
    /* JADX WARNING: Removed duplicated region for block: B:128:0x02b1 A[ExcHandler: IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException (r0v0 'e' java.lang.Exception A[CUSTOM_DECLARE]), PHI: r5 
      PHI: (r5v1 'version' int) = (r5v0 'version' int), (r5v0 'version' int), (r5v2 'version' int), (r5v2 'version' int) binds: [B:1:0x000b, B:2:?, B:11:0x0033, B:13:0x0037] A[DONT_GENERATE, DONT_INLINE], Splitter:B:1:0x000b] */
    /* JADX WARNING: Removed duplicated region for block: B:132:0x029e A[SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private int readProfileStateFromFileLocked(java.io.FileInputStream r24, int r25, java.util.List<com.android.server.appwidget.AppWidgetServiceImpl.LoadedWidgetState> r26) {
        /*
            r23 = this;
            r1 = r23
            r2 = r25
            java.lang.String r3 = "h"
            java.lang.String r4 = "p"
            r5 = -1
            r6 = -1
            org.xmlpull.v1.XmlPullParser r7 = android.util.Xml.newPullParser()     // Catch:{ IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x02b1 }
            java.nio.charset.Charset r8 = java.nio.charset.StandardCharsets.UTF_8     // Catch:{ IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x02b1 }
            java.lang.String r8 = r8.name()     // Catch:{ IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x02b1 }
            r9 = r24
            r7.setInput(r9, r8)     // Catch:{ IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x02b1 }
            r8 = -1
            r10 = r6
        L_0x001c:
            int r11 = r7.next()     // Catch:{ IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x02aa }
            r12 = 2
            if (r11 != r12) goto L_0x0292
            java.lang.String r12 = r7.getName()     // Catch:{ IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x02aa }
            java.lang.String r14 = "gs"
            boolean r14 = r14.equals(r12)     // Catch:{ IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x02aa }
            r15 = 0
            if (r14 == 0) goto L_0x0046
            java.lang.String r14 = "version"
            java.lang.String r14 = r7.getAttributeValue(r15, r14)     // Catch:{ IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x02b1 }
            int r15 = java.lang.Integer.parseInt(r14)     // Catch:{ NumberFormatException -> 0x003d, IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x02b1, IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x02b1, IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x02b1 }
            r5 = r15
            goto L_0x0040
        L_0x003d:
            r0 = move-exception
            r15 = r0
            r5 = 0
        L_0x0040:
            r1 = r26
            r16 = r3
            goto L_0x029a
        L_0x0046:
            boolean r14 = r4.equals(r12)     // Catch:{ IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x02aa }
            java.lang.String r6 = "tag"
            java.lang.String r13 = "pkg"
            if (r14 == 0) goto L_0x0118
            int r8 = r8 + 1
            r14 = 0
            java.lang.String r13 = r7.getAttributeValue(r14, r13)     // Catch:{ IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x0111 }
            java.lang.String r15 = "cl"
            java.lang.String r15 = r7.getAttributeValue(r14, r15)     // Catch:{ IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x0111 }
            r14 = r15
            java.lang.String r15 = r1.getCanonicalPackageName(r13, r14, r2)     // Catch:{ IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x0111 }
            r13 = r15
            if (r13 != 0) goto L_0x006d
            r18 = r5
            r19 = r8
            goto L_0x0107
        L_0x006d:
            int r15 = r1.getUidForPackage(r13, r2)     // Catch:{ IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x0111 }
            if (r15 >= 0) goto L_0x0079
            r18 = r5
            r19 = r8
            goto L_0x0107
        L_0x0079:
            r18 = r5
            android.content.ComponentName r5 = new android.content.ComponentName     // Catch:{ IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x028d }
            r5.<init>(r13, r14)     // Catch:{ IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x028d }
            android.content.pm.ActivityInfo r19 = r1.getProviderInfo(r5, r2)     // Catch:{ IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x028d }
            r20 = r19
            r19 = r8
            r8 = r20
            if (r8 != 0) goto L_0x008e
            goto L_0x0107
        L_0x008e:
            com.android.server.appwidget.AppWidgetServiceImpl$ProviderId r9 = new com.android.server.appwidget.AppWidgetServiceImpl$ProviderId     // Catch:{ IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x028d }
            r20 = r13
            r13 = 0
            r9.<init>(r15, r5)     // Catch:{ IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x028d }
            com.android.server.appwidget.AppWidgetServiceImpl$Provider r13 = r1.lookupProviderLocked(r9)     // Catch:{ IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x028d }
            if (r13 != 0) goto L_0x00c9
            r21 = r5
            boolean r5 = r1.mSafeMode     // Catch:{ IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x028d }
            if (r5 == 0) goto L_0x00c6
            com.android.server.appwidget.AppWidgetServiceImpl$Provider r5 = new com.android.server.appwidget.AppWidgetServiceImpl$Provider     // Catch:{ IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x028d }
            r5.<init>()     // Catch:{ IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x028d }
            r13 = r5
            android.appwidget.AppWidgetProviderInfo r5 = new android.appwidget.AppWidgetProviderInfo     // Catch:{ IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x028d }
            r5.<init>()     // Catch:{ IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x028d }
            r13.info = r5     // Catch:{ IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x028d }
            android.appwidget.AppWidgetProviderInfo r5 = r13.info     // Catch:{ IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x028d }
            r22 = r14
            android.content.ComponentName r14 = r9.componentName     // Catch:{ IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x028d }
            r5.provider = r14     // Catch:{ IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x028d }
            android.appwidget.AppWidgetProviderInfo r5 = r13.info     // Catch:{ IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x028d }
            r5.providerInfo = r8     // Catch:{ IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x028d }
            r5 = 1
            r13.zombie = r5     // Catch:{ IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x028d }
            r13.id = r9     // Catch:{ IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x028d }
            java.util.ArrayList<com.android.server.appwidget.AppWidgetServiceImpl$Provider> r5 = r1.mProviders     // Catch:{ IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x028d }
            r5.add(r13)     // Catch:{ IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x028d }
            goto L_0x00cd
        L_0x00c6:
            r22 = r14
            goto L_0x00cd
        L_0x00c9:
            r21 = r5
            r22 = r14
        L_0x00cd:
            r5 = 0
            java.lang.String r6 = r7.getAttributeValue(r5, r6)     // Catch:{ IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x028d }
            r5 = r6
            boolean r6 = android.text.TextUtils.isEmpty(r5)     // Catch:{ IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x028d }
            if (r6 != 0) goto L_0x00e0
            r6 = 16
            int r6 = java.lang.Integer.parseInt(r5, r6)     // Catch:{ IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x028d }
            goto L_0x00e2
        L_0x00e0:
            r6 = r19
        L_0x00e2:
            r13.tag = r6     // Catch:{ IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x028d }
            java.lang.String r14 = "info_tag"
            r17 = r5
            r5 = 0
            java.lang.String r5 = r7.getAttributeValue(r5, r14)     // Catch:{ IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x028d }
            r13.infoTag = r5     // Catch:{ IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x028d }
            java.lang.String r5 = r13.infoTag     // Catch:{ IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x028d }
            boolean r5 = android.text.TextUtils.isEmpty(r5)     // Catch:{ IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x028d }
            if (r5 != 0) goto L_0x0106
            boolean r5 = r1.mSafeMode     // Catch:{ IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x028d }
            if (r5 != 0) goto L_0x0106
            java.lang.String r5 = r13.infoTag     // Catch:{ IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x028d }
            android.appwidget.AppWidgetProviderInfo r5 = r1.parseAppWidgetProviderInfo(r9, r8, r5)     // Catch:{ IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x028d }
            if (r5 == 0) goto L_0x0106
            r13.info = r5     // Catch:{ IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x028d }
        L_0x0106:
        L_0x0107:
            r1 = r26
            r16 = r3
            r5 = r18
            r8 = r19
            goto L_0x029a
        L_0x0111:
            r0 = move-exception
            r18 = r5
            r1 = r26
            goto L_0x02af
        L_0x0118:
            r18 = r5
            boolean r5 = r3.equals(r12)     // Catch:{ IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x028d }
            java.lang.String r9 = "id"
            if (r5 == 0) goto L_0x0176
            int r10 = r10 + 1
            com.android.server.appwidget.AppWidgetServiceImpl$Host r5 = new com.android.server.appwidget.AppWidgetServiceImpl$Host     // Catch:{ IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x028d }
            r14 = 0
            r5.<init>()     // Catch:{ IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x028d }
            java.lang.String r13 = r7.getAttributeValue(r14, r13)     // Catch:{ IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x028d }
            int r14 = r1.getUidForPackage(r13, r2)     // Catch:{ IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x028d }
            if (r14 >= 0) goto L_0x0138
            r15 = 1
            r5.zombie = r15     // Catch:{ IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x028d }
        L_0x0138:
            boolean r15 = r5.zombie     // Catch:{ IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x028d }
            if (r15 == 0) goto L_0x0140
            boolean r15 = r1.mSafeMode     // Catch:{ IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x028d }
            if (r15 == 0) goto L_0x016e
        L_0x0140:
            r15 = 0
            java.lang.String r9 = r7.getAttributeValue(r15, r9)     // Catch:{ IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x028d }
            r15 = 16
            int r9 = java.lang.Integer.parseInt(r9, r15)     // Catch:{ IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x028d }
            r15 = 0
            java.lang.String r6 = r7.getAttributeValue(r15, r6)     // Catch:{ IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x028d }
            boolean r15 = android.text.TextUtils.isEmpty(r6)     // Catch:{ IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x028d }
            if (r15 != 0) goto L_0x015d
            r15 = 16
            int r15 = java.lang.Integer.parseInt(r6, r15)     // Catch:{ IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x028d }
            goto L_0x015e
        L_0x015d:
            r15 = r10
        L_0x015e:
            r5.tag = r15     // Catch:{ IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x028d }
            r16 = r6
            com.android.server.appwidget.AppWidgetServiceImpl$HostId r6 = new com.android.server.appwidget.AppWidgetServiceImpl$HostId     // Catch:{ IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x028d }
            r6.<init>(r14, r9, r13)     // Catch:{ IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x028d }
            r5.id = r6     // Catch:{ IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x028d }
            java.util.ArrayList<com.android.server.appwidget.AppWidgetServiceImpl$Host> r6 = r1.mHosts     // Catch:{ IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x028d }
            r6.add(r5)     // Catch:{ IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x028d }
        L_0x016e:
            r1 = r26
            r16 = r3
            r5 = r18
            goto L_0x029a
        L_0x0176:
            java.lang.String r5 = "b"
            boolean r5 = r5.equals(r12)     // Catch:{ IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x028d }
            if (r5 == 0) goto L_0x019f
            java.lang.String r5 = "packageName"
            r6 = 0
            java.lang.String r5 = r7.getAttributeValue(r6, r5)     // Catch:{ IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x028d }
            int r6 = r1.getUidForPackage(r5, r2)     // Catch:{ IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x028d }
            if (r6 < 0) goto L_0x0199
            java.lang.Integer r9 = java.lang.Integer.valueOf(r25)     // Catch:{ IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x028d }
            android.util.Pair r9 = android.util.Pair.create(r9, r5)     // Catch:{ IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x028d }
            android.util.ArraySet<android.util.Pair<java.lang.Integer, java.lang.String>> r13 = r1.mPackagesWithBindWidgetPermission     // Catch:{ IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x028d }
            r13.add(r9)     // Catch:{ IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x028d }
        L_0x0199:
            r1 = r26
            r16 = r3
            goto L_0x0298
        L_0x019f:
            java.lang.String r5 = "g"
            boolean r5 = r5.equals(r12)     // Catch:{ IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x028d }
            if (r5 == 0) goto L_0x0288
            com.android.server.appwidget.AppWidgetServiceImpl$Widget r5 = new com.android.server.appwidget.AppWidgetServiceImpl$Widget     // Catch:{ IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x028d }
            r6 = 0
            r5.<init>()     // Catch:{ IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x028d }
            java.lang.String r9 = r7.getAttributeValue(r6, r9)     // Catch:{ IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x028d }
            r6 = 16
            int r9 = java.lang.Integer.parseInt(r9, r6)     // Catch:{ IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x028d }
            r5.appWidgetId = r9     // Catch:{ IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x028d }
            int r6 = r5.appWidgetId     // Catch:{ IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x028d }
            r9 = 1
            int r6 = r6 + r9
            r1.setMinAppWidgetIdLocked(r2, r6)     // Catch:{ IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x028d }
            java.lang.String r6 = "rid"
            r9 = 0
            java.lang.String r6 = r7.getAttributeValue(r9, r6)     // Catch:{ IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x028d }
            if (r6 != 0) goto L_0x01cd
            r9 = 0
            r13 = r9
            goto L_0x01d3
        L_0x01cd:
            r9 = 16
            int r13 = java.lang.Integer.parseInt(r6, r9)     // Catch:{ IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x028d }
        L_0x01d3:
            r5.restoredId = r13     // Catch:{ IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x028d }
            android.os.Bundle r9 = new android.os.Bundle     // Catch:{ IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x028d }
            r9.<init>()     // Catch:{ IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x028d }
            java.lang.String r13 = "min_width"
            r14 = 0
            java.lang.String r13 = r7.getAttributeValue(r14, r13)     // Catch:{ IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x028d }
            if (r13 == 0) goto L_0x01ef
            java.lang.String r14 = "appWidgetMinWidth"
            r15 = 16
            int r2 = java.lang.Integer.parseInt(r13, r15)     // Catch:{ IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x028d }
            r9.putInt(r14, r2)     // Catch:{ IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x028d }
        L_0x01ef:
            java.lang.String r2 = "min_height"
            r14 = 0
            java.lang.String r2 = r7.getAttributeValue(r14, r2)     // Catch:{ IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x028d }
            if (r2 == 0) goto L_0x0207
            java.lang.String r14 = "appWidgetMinHeight"
            r19 = r6
            r15 = 16
            int r6 = java.lang.Integer.parseInt(r2, r15)     // Catch:{ IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x028d }
            r9.putInt(r14, r6)     // Catch:{ IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x028d }
            goto L_0x0209
        L_0x0207:
            r19 = r6
        L_0x0209:
            java.lang.String r6 = "max_width"
            r14 = 0
            java.lang.String r6 = r7.getAttributeValue(r14, r6)     // Catch:{ IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x028d }
            if (r6 == 0) goto L_0x0221
            java.lang.String r14 = "appWidgetMaxWidth"
            r20 = r2
            r15 = 16
            int r2 = java.lang.Integer.parseInt(r6, r15)     // Catch:{ IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x028d }
            r9.putInt(r14, r2)     // Catch:{ IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x028d }
            goto L_0x0223
        L_0x0221:
            r20 = r2
        L_0x0223:
            java.lang.String r2 = "max_height"
            r14 = 0
            java.lang.String r2 = r7.getAttributeValue(r14, r2)     // Catch:{ IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x028d }
            if (r2 == 0) goto L_0x023b
            java.lang.String r14 = "appWidgetMaxHeight"
            r21 = r6
            r15 = 16
            int r6 = java.lang.Integer.parseInt(r2, r15)     // Catch:{ IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x028d }
            r9.putInt(r14, r6)     // Catch:{ IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x028d }
            goto L_0x023d
        L_0x023b:
            r21 = r6
        L_0x023d:
            java.lang.String r6 = "host_category"
            r14 = 0
            java.lang.String r6 = r7.getAttributeValue(r14, r6)     // Catch:{ IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x028d }
            if (r6 == 0) goto L_0x0255
            java.lang.String r14 = "appWidgetCategory"
            r22 = r2
            r15 = 16
            int r2 = java.lang.Integer.parseInt(r6, r15)     // Catch:{ IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x028d }
            r9.putInt(r14, r2)     // Catch:{ IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x028d }
            goto L_0x0257
        L_0x0255:
            r22 = r2
        L_0x0257:
            r5.options = r9     // Catch:{ IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x028d }
            r2 = 0
            java.lang.String r14 = r7.getAttributeValue(r2, r3)     // Catch:{ IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x028d }
            r15 = 16
            int r14 = java.lang.Integer.parseInt(r14, r15)     // Catch:{ IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x028d }
            java.lang.String r15 = r7.getAttributeValue(r2, r4)     // Catch:{ IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x028d }
            if (r15 == 0) goto L_0x0277
            java.lang.String r2 = r7.getAttributeValue(r2, r4)     // Catch:{ IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x028d }
            r16 = r3
            r3 = 16
            int r2 = java.lang.Integer.parseInt(r2, r3)     // Catch:{ IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x028d }
            goto L_0x027a
        L_0x0277:
            r16 = r3
            r2 = -1
        L_0x027a:
            com.android.server.appwidget.AppWidgetServiceImpl$LoadedWidgetState r3 = new com.android.server.appwidget.AppWidgetServiceImpl$LoadedWidgetState     // Catch:{ IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x028d }
            r3.<init>(r5, r14, r2)     // Catch:{ IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x028d }
            r1 = r26
            r1.add(r3)     // Catch:{ IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x0286 }
            goto L_0x0298
        L_0x0286:
            r0 = move-exception
            goto L_0x0290
        L_0x0288:
            r1 = r26
            r16 = r3
            goto L_0x0298
        L_0x028d:
            r0 = move-exception
            r1 = r26
        L_0x0290:
            r2 = r0
            goto L_0x02b7
        L_0x0292:
            r1 = r26
            r16 = r3
            r18 = r5
        L_0x0298:
            r5 = r18
        L_0x029a:
            r2 = 1
            if (r11 != r2) goto L_0x029f
            return r5
        L_0x029f:
            r1 = r23
            r9 = r24
            r2 = r25
            r3 = r16
            r6 = -1
            goto L_0x001c
        L_0x02aa:
            r0 = move-exception
            r1 = r26
            r18 = r5
        L_0x02af:
            r2 = r0
            goto L_0x02b7
        L_0x02b1:
            r0 = move-exception
            r1 = r26
            r2 = r0
            r18 = r5
        L_0x02b7:
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            java.lang.String r4 = "failed parsing "
            r3.append(r4)
            r3.append(r2)
            java.lang.String r3 = r3.toString()
            java.lang.String r4 = "AppWidgetServiceImpl"
            android.util.Slog.w(r4, r3)
            r3 = -1
            return r3
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.appwidget.AppWidgetServiceImpl.readProfileStateFromFileLocked(java.io.FileInputStream, int, java.util.List):int");
    }

    private void performUpgradeLocked(int fromVersion) {
        int uid;
        if (fromVersion < 1) {
            Slog.v(TAG, "Upgrading widget database from " + fromVersion + " to " + 1);
        }
        int version = fromVersion;
        if (version == 0) {
            Host host = lookupHostLocked(new HostId(Process.myUid(), KEYGUARD_HOST_ID, "android"));
            if (host != null && (uid = getUidForPackage(NEW_KEYGUARD_HOST_PACKAGE, 0)) >= 0) {
                host.id = new HostId(uid, KEYGUARD_HOST_ID, NEW_KEYGUARD_HOST_PACKAGE);
            }
            version = 1;
        }
        if (version != 1) {
            throw new IllegalStateException("Failed to upgrade widget database");
        }
    }

    private static File getStateFile(int userId) {
        return new File(Environment.getUserSystemDirectory(userId), STATE_FILENAME);
    }

    private static AtomicFile getSavedStateFile(int userId) {
        File dir = Environment.getUserSystemDirectory(userId);
        File settingsFile = getStateFile(userId);
        if (!settingsFile.exists() && userId == 0) {
            if (!dir.exists()) {
                dir.mkdirs();
            }
            new File("/data/system/appwidgets.xml").renameTo(settingsFile);
        }
        return new AtomicFile(settingsFile);
    }

    /* access modifiers changed from: package-private */
    public void onUserStopped(int userId) {
        synchronized (this.mLock) {
            boolean crossProfileWidgetsChanged = false;
            int i = this.mWidgets.size() - 1;
            while (true) {
                boolean providerInUser = false;
                if (i < 0) {
                    break;
                }
                Widget widget = this.mWidgets.get(i);
                boolean hostInUser = widget.host.getUserId() == userId;
                boolean hasProvider = widget.provider != null;
                if (hasProvider && widget.provider.getUserId() == userId) {
                    providerInUser = true;
                }
                if (hostInUser && (!hasProvider || providerInUser)) {
                    removeWidgetLocked(widget);
                    widget.host.widgets.remove(widget);
                    widget.host = null;
                    if (hasProvider) {
                        widget.provider.widgets.remove(widget);
                        widget.provider = null;
                    }
                }
                i--;
            }
            for (int i2 = this.mHosts.size() - 1; i2 >= 0; i2--) {
                Host host = this.mHosts.get(i2);
                if (host.getUserId() == userId) {
                    crossProfileWidgetsChanged |= !host.widgets.isEmpty();
                    deleteHostLocked(host);
                }
            }
            for (int i3 = this.mPackagesWithBindWidgetPermission.size() - 1; i3 >= 0; i3--) {
                if (((Integer) this.mPackagesWithBindWidgetPermission.valueAt(i3).first).intValue() == userId) {
                    this.mPackagesWithBindWidgetPermission.removeAt(i3);
                }
            }
            int userIndex = this.mLoadedUserIds.indexOfKey(userId);
            if (userIndex >= 0) {
                this.mLoadedUserIds.removeAt(userIndex);
            }
            int nextIdIndex = this.mNextAppWidgetIds.indexOfKey(userId);
            if (nextIdIndex >= 0) {
                this.mNextAppWidgetIds.removeAt(nextIdIndex);
            }
            if (crossProfileWidgetsChanged) {
                saveGroupStateAsync(userId);
            }
        }
    }

    private boolean updateProvidersForPackageLocked(String packageName, int userId, Set<ProviderId> removedProviders) {
        int N;
        Intent intent;
        List<ResolveInfo> broadcastReceivers;
        boolean providersUpdated;
        Intent intent2;
        List<ResolveInfo> broadcastReceivers2;
        int N2;
        String str = packageName;
        int i = userId;
        Set<ProviderId> set = removedProviders;
        boolean providersUpdated2 = false;
        HashSet<ProviderId> keep = new HashSet<>();
        Intent intent3 = new Intent("android.appwidget.action.APPWIDGET_UPDATE");
        intent3.setPackage(str);
        List<ResolveInfo> broadcastReceivers3 = queryIntentReceivers(intent3, i);
        int N3 = broadcastReceivers3 == null ? 0 : broadcastReceivers3.size();
        int i2 = 0;
        while (i2 < N3) {
            ResolveInfo ri = broadcastReceivers3.get(i2);
            ActivityInfo ai = ri.activityInfo;
            if ((ai.applicationInfo.flags & DumpState.DUMP_DOMAIN_PREFERRED) != 0) {
                providersUpdated = providersUpdated2;
                intent2 = intent3;
                broadcastReceivers2 = broadcastReceivers3;
                N2 = N3;
            } else if (str.equals(ai.packageName)) {
                providersUpdated = providersUpdated2;
                ProviderId providerId = new ProviderId(ai.applicationInfo.uid, new ComponentName(ai.packageName, ai.name));
                Provider provider = lookupProviderLocked(providerId);
                if (provider != null) {
                    Provider parsed = parseProviderInfoXml(providerId, ri, provider);
                    if (parsed != null) {
                        keep.add(providerId);
                        provider.info = parsed.info;
                        int M = provider.widgets.size();
                        if (M > 0) {
                            int[] appWidgetIds = getWidgetIds(provider.widgets);
                            cancelBroadcastsLocked(provider);
                            registerForBroadcastsLocked(provider, appWidgetIds);
                            intent = intent3;
                            int j = 0;
                            while (j < M) {
                                List<ResolveInfo> broadcastReceivers4 = broadcastReceivers3;
                                Widget widget = provider.widgets.get(j);
                                widget.views = null;
                                scheduleNotifyProviderChangedLocked(widget);
                                j++;
                                broadcastReceivers3 = broadcastReceivers4;
                                N3 = N3;
                            }
                            broadcastReceivers = broadcastReceivers3;
                            N = N3;
                            sendUpdateIntentLocked(provider, appWidgetIds);
                        } else {
                            intent = intent3;
                            broadcastReceivers = broadcastReceivers3;
                            N = N3;
                        }
                    } else {
                        intent = intent3;
                        broadcastReceivers = broadcastReceivers3;
                        N = N3;
                    }
                    providersUpdated2 = true;
                } else if (addProviderLocked(ri)) {
                    keep.add(providerId);
                    providersUpdated2 = true;
                    intent = intent3;
                    broadcastReceivers = broadcastReceivers3;
                    N = N3;
                } else {
                    intent2 = intent3;
                    broadcastReceivers2 = broadcastReceivers3;
                    N2 = N3;
                }
                i2++;
                broadcastReceivers3 = broadcastReceivers;
                intent3 = intent;
                N3 = N;
            } else {
                providersUpdated = providersUpdated2;
                intent2 = intent3;
                broadcastReceivers2 = broadcastReceivers3;
                N2 = N3;
            }
            providersUpdated2 = providersUpdated;
            i2++;
            broadcastReceivers3 = broadcastReceivers;
            intent3 = intent;
            N3 = N;
        }
        boolean providersUpdated3 = providersUpdated2;
        Intent intent4 = intent3;
        List<ResolveInfo> list = broadcastReceivers3;
        int i3 = N3;
        for (int i4 = this.mProviders.size() - 1; i4 >= 0; i4--) {
            Provider provider2 = this.mProviders.get(i4);
            if (str.equals(provider2.info.provider.getPackageName()) && provider2.getUserId() == i && !keep.contains(provider2.id)) {
                if (set != null) {
                    set.add(provider2.id);
                }
                deleteProviderLocked(provider2);
                providersUpdated3 = true;
            }
        }
        return providersUpdated3;
    }

    private void removeWidgetsForPackageLocked(String pkgName, int userId, int parentUserId) {
        int N = this.mProviders.size();
        for (int i = 0; i < N; i++) {
            Provider provider = this.mProviders.get(i);
            if (pkgName.equals(provider.info.provider.getPackageName()) && provider.getUserId() == userId && provider.widgets.size() > 0) {
                deleteWidgetsLocked(provider, parentUserId);
            }
        }
    }

    private boolean removeProvidersForPackageLocked(String pkgName, int userId) {
        boolean removed = false;
        for (int i = this.mProviders.size() - 1; i >= 0; i--) {
            Provider provider = this.mProviders.get(i);
            if (pkgName.equals(provider.info.provider.getPackageName()) && provider.getUserId() == userId) {
                deleteProviderLocked(provider);
                removed = true;
            }
        }
        return removed;
    }

    private boolean removeHostsAndProvidersForPackageLocked(String pkgName, int userId) {
        boolean removed = removeProvidersForPackageLocked(pkgName, userId);
        for (int i = this.mHosts.size() - 1; i >= 0; i--) {
            Host host = this.mHosts.get(i);
            if (pkgName.equals(host.id.packageName) && host.getUserId() == userId) {
                deleteHostLocked(host);
                removed = true;
            }
        }
        return removed;
    }

    private String getCanonicalPackageName(String packageName, String className, int userId) {
        long identity = Binder.clearCallingIdentity();
        try {
            AppGlobals.getPackageManager().getReceiverInfo(new ComponentName(packageName, className), 0, userId);
            return packageName;
        } catch (RemoteException e) {
            String[] packageNames = this.mContext.getPackageManager().currentToCanonicalPackageNames(new String[]{packageName});
            if (packageNames != null && packageNames.length > 0) {
                return packageNames[0];
            }
            Binder.restoreCallingIdentity(identity);
            return null;
        } finally {
            Binder.restoreCallingIdentity(identity);
        }
    }

    /* access modifiers changed from: private */
    public void sendBroadcastAsUser(Intent intent, UserHandle userHandle) {
        long identity = Binder.clearCallingIdentity();
        try {
            this.mContext.sendBroadcastAsUser(intent, userHandle);
        } finally {
            Binder.restoreCallingIdentity(identity);
        }
    }

    private void bindService(Intent intent, ServiceConnection connection, UserHandle userHandle) {
        long token = Binder.clearCallingIdentity();
        try {
            this.mContext.bindServiceAsUser(intent, connection, 33554433, userHandle);
        } finally {
            Binder.restoreCallingIdentity(token);
        }
    }

    private void unbindService(ServiceConnection connection) {
        long token = Binder.clearCallingIdentity();
        try {
            this.mContext.unbindService(connection);
        } finally {
            Binder.restoreCallingIdentity(token);
        }
    }

    public void onCrossProfileWidgetProvidersChanged(int userId, List<String> packages) {
        int parentId = this.mSecurityPolicy.getProfileParent(userId);
        if (parentId != userId) {
            synchronized (this.mLock) {
                boolean providersChanged = false;
                ArraySet<String> previousPackages = new ArraySet<>();
                int providerCount = this.mProviders.size();
                for (int i = 0; i < providerCount; i++) {
                    Provider provider = this.mProviders.get(i);
                    if (provider.getUserId() == userId) {
                        previousPackages.add(provider.id.componentName.getPackageName());
                    }
                }
                int i2 = packages.size();
                for (int i3 = 0; i3 < i2; i3++) {
                    String packageName = packages.get(i3);
                    previousPackages.remove(packageName);
                    providersChanged |= updateProvidersForPackageLocked(packageName, userId, (Set<ProviderId>) null);
                }
                int i4 = previousPackages.size();
                for (int i5 = 0; i5 < i4; i5++) {
                    removeWidgetsForPackageLocked(previousPackages.valueAt(i5), userId, parentId);
                }
                if (providersChanged || i4 > 0) {
                    saveGroupStateAsync(userId);
                    scheduleNotifyGroupHostsForProvidersChangedLocked(userId);
                }
            }
        }
    }

    private boolean isProfileWithLockedParent(int userId) {
        UserInfo parentInfo;
        long token = Binder.clearCallingIdentity();
        try {
            UserInfo userInfo = this.mUserManager.getUserInfo(userId);
            if (userInfo == null || !userInfo.isManagedProfile() || (parentInfo = this.mUserManager.getProfileParent(userId)) == null || isUserRunningAndUnlocked(parentInfo.getUserHandle().getIdentifier())) {
                Binder.restoreCallingIdentity(token);
                return false;
            }
            return true;
        } finally {
            Binder.restoreCallingIdentity(token);
        }
    }

    private boolean isProfileWithUnlockedParent(int userId) {
        UserInfo parentInfo;
        UserInfo userInfo = this.mUserManager.getUserInfo(userId);
        if (userInfo == null || !userInfo.isManagedProfile() || (parentInfo = this.mUserManager.getProfileParent(userId)) == null || !this.mUserManager.isUserUnlockingOrUnlocked(parentInfo.getUserHandle())) {
            return false;
        }
        return true;
    }

    private final class CallbackHandler extends Handler {
        public static final int MSG_NOTIFY_PROVIDERS_CHANGED = 3;
        public static final int MSG_NOTIFY_PROVIDER_CHANGED = 2;
        public static final int MSG_NOTIFY_UPDATE_APP_WIDGET = 1;
        public static final int MSG_NOTIFY_VIEW_DATA_CHANGED = 4;

        public CallbackHandler(Looper looper) {
            super(looper, (Handler.Callback) null, false);
        }

        public void handleMessage(Message message) {
            int i = message.what;
            if (i == 1) {
                SomeArgs args = (SomeArgs) message.obj;
                long requestId = ((Long) args.arg4).longValue();
                int appWidgetId = args.argi1;
                args.recycle();
                AppWidgetServiceImpl.this.handleNotifyUpdateAppWidget((Host) args.arg1, (IAppWidgetHost) args.arg2, appWidgetId, (RemoteViews) args.arg3, requestId);
            } else if (i == 2) {
                SomeArgs args2 = (SomeArgs) message.obj;
                long requestId2 = ((Long) args2.arg4).longValue();
                int appWidgetId2 = args2.argi1;
                args2.recycle();
                AppWidgetServiceImpl.this.handleNotifyProviderChanged((Host) args2.arg1, (IAppWidgetHost) args2.arg2, appWidgetId2, (AppWidgetProviderInfo) args2.arg3, requestId2);
            } else if (i == 3) {
                SomeArgs args3 = (SomeArgs) message.obj;
                args3.recycle();
                AppWidgetServiceImpl.this.handleNotifyProvidersChanged((Host) args3.arg1, (IAppWidgetHost) args3.arg2);
            } else if (i == 4) {
                SomeArgs args4 = (SomeArgs) message.obj;
                long requestId3 = ((Long) args4.arg3).longValue();
                int appWidgetId3 = args4.argi1;
                int viewId = args4.argi2;
                args4.recycle();
                AppWidgetServiceImpl.this.handleNotifyAppWidgetViewDataChanged((Host) args4.arg1, (IAppWidgetHost) args4.arg2, appWidgetId3, viewId, requestId3);
            }
        }
    }

    private final class SecurityPolicy {
        private SecurityPolicy() {
        }

        public boolean isEnabledGroupProfile(int profileId) {
            return isParentOrProfile(UserHandle.getCallingUserId(), profileId) && isProfileEnabled(profileId);
        }

        public int[] getEnabledGroupProfileIds(int userId) {
            int parentId = getGroupParent(userId);
            long identity = Binder.clearCallingIdentity();
            try {
                return AppWidgetServiceImpl.this.mUserManager.getEnabledProfileIds(parentId);
            } finally {
                Binder.restoreCallingIdentity(identity);
            }
        }

        /* Debug info: failed to restart local var, previous not found, register: 7 */
        public void enforceServiceExistsAndRequiresBindRemoteViewsPermission(ComponentName componentName, int userId) {
            long identity = Binder.clearCallingIdentity();
            try {
                ServiceInfo serviceInfo = AppWidgetServiceImpl.this.mPackageManager.getServiceInfo(componentName, 4096, userId);
                if (serviceInfo == null) {
                    throw new SecurityException("Service " + componentName + " not installed for user " + userId);
                } else if ("android.permission.BIND_REMOTEVIEWS".equals(serviceInfo.permission)) {
                    Binder.restoreCallingIdentity(identity);
                } else {
                    throw new SecurityException("Service " + componentName + " in user " + userId + "does not require " + "android.permission.BIND_REMOTEVIEWS");
                }
            } catch (RemoteException e) {
            } catch (Throwable th) {
                Binder.restoreCallingIdentity(identity);
                throw th;
            }
        }

        public void enforceModifyAppWidgetBindPermissions(String packageName) {
            Context access$1400 = AppWidgetServiceImpl.this.mContext;
            access$1400.enforceCallingPermission("android.permission.MODIFY_APPWIDGET_BIND_PERMISSIONS", "hasBindAppWidgetPermission packageName=" + packageName);
        }

        public boolean isCallerInstantAppLocked() {
            int callingUid = Binder.getCallingUid();
            long identity = Binder.clearCallingIdentity();
            try {
                String[] uidPackages = AppWidgetServiceImpl.this.mPackageManager.getPackagesForUid(callingUid);
                if (!ArrayUtils.isEmpty(uidPackages)) {
                    boolean isInstantApp = AppWidgetServiceImpl.this.mPackageManager.isInstantApp(uidPackages[0], UserHandle.getCallingUserId());
                    Binder.restoreCallingIdentity(identity);
                    return isInstantApp;
                }
            } catch (RemoteException e) {
            } catch (Throwable th) {
                Binder.restoreCallingIdentity(identity);
                throw th;
            }
            Binder.restoreCallingIdentity(identity);
            return false;
        }

        /* JADX INFO: finally extract failed */
        public boolean isInstantAppLocked(String packageName, int userId) {
            long identity = Binder.clearCallingIdentity();
            try {
                boolean isInstantApp = AppWidgetServiceImpl.this.mPackageManager.isInstantApp(packageName, userId);
                Binder.restoreCallingIdentity(identity);
                return isInstantApp;
            } catch (RemoteException e) {
                Binder.restoreCallingIdentity(identity);
                return false;
            } catch (Throwable th) {
                Binder.restoreCallingIdentity(identity);
                throw th;
            }
        }

        public void enforceCallFromPackage(String packageName) {
            AppWidgetServiceImpl.this.mAppOpsManager.checkPackage(Binder.getCallingUid(), packageName);
        }

        public boolean hasCallerBindPermissionOrBindWhiteListedLocked(String packageName) {
            try {
                AppWidgetServiceImpl.this.mContext.enforceCallingOrSelfPermission("android.permission.BIND_APPWIDGET", (String) null);
                return true;
            } catch (SecurityException e) {
                if (!isCallerBindAppWidgetWhiteListedLocked(packageName)) {
                    return false;
                }
                return true;
            }
        }

        private boolean isCallerBindAppWidgetWhiteListedLocked(String packageName) {
            int userId = UserHandle.getCallingUserId();
            if (AppWidgetServiceImpl.this.getUidForPackage(packageName, userId) >= 0) {
                synchronized (AppWidgetServiceImpl.this.mLock) {
                    AppWidgetServiceImpl.this.ensureGroupStateLoadedLocked(userId);
                    if (AppWidgetServiceImpl.this.mPackagesWithBindWidgetPermission.contains(Pair.create(Integer.valueOf(userId), packageName))) {
                        return true;
                    }
                    return false;
                }
            }
            throw new IllegalArgumentException("No package " + packageName + " for user " + userId);
        }

        public boolean canAccessAppWidget(Widget widget, int uid, String packageName) {
            if (isHostInPackageForUid(widget.host, uid, packageName) || isProviderInPackageForUid(widget.provider, uid, packageName) || isHostAccessingProvider(widget.host, widget.provider, uid, packageName)) {
                return true;
            }
            int userId = UserHandle.getUserId(uid);
            if ((widget.host.getUserId() == userId || (widget.provider != null && widget.provider.getUserId() == userId)) && AppWidgetServiceImpl.this.mContext.checkCallingPermission("android.permission.BIND_APPWIDGET") == 0) {
                return true;
            }
            return false;
        }

        private boolean isParentOrProfile(int parentId, int profileId) {
            if (parentId == profileId || getProfileParent(profileId) == parentId) {
                return true;
            }
            return false;
        }

        public boolean isProviderInCallerOrInProfileAndWhitelListed(String packageName, int profileId) {
            int callerId = UserHandle.getCallingUserId();
            if (profileId == callerId) {
                return true;
            }
            if (getProfileParent(profileId) != callerId) {
                return false;
            }
            return isProviderWhiteListed(packageName, profileId);
        }

        public boolean isProviderWhiteListed(String packageName, int profileId) {
            if (AppWidgetServiceImpl.this.mDevicePolicyManagerInternal == null) {
                return false;
            }
            return AppWidgetServiceImpl.this.mDevicePolicyManagerInternal.getCrossProfileWidgetProviders(profileId).contains(packageName);
        }

        public int getProfileParent(int profileId) {
            long identity = Binder.clearCallingIdentity();
            try {
                UserInfo parent = AppWidgetServiceImpl.this.mUserManager.getProfileParent(profileId);
                if (parent != null) {
                    return parent.getUserHandle().getIdentifier();
                }
                Binder.restoreCallingIdentity(identity);
                return -10;
            } finally {
                Binder.restoreCallingIdentity(identity);
            }
        }

        public int getGroupParent(int profileId) {
            int parentId = AppWidgetServiceImpl.this.mSecurityPolicy.getProfileParent(profileId);
            return parentId != -10 ? parentId : profileId;
        }

        public boolean isHostInPackageForUid(Host host, int uid, String packageName) {
            return host.id.uid == uid && host.id.packageName.equals(packageName);
        }

        public boolean isProviderInPackageForUid(Provider provider, int uid, String packageName) {
            return provider != null && provider.id.uid == uid && provider.id.componentName.getPackageName().equals(packageName);
        }

        public boolean isHostAccessingProvider(Host host, Provider provider, int uid, String packageName) {
            return host.id.uid == uid && provider != null && provider.id.componentName.getPackageName().equals(packageName);
        }

        /* JADX INFO: finally extract failed */
        private boolean isProfileEnabled(int profileId) {
            long identity = Binder.clearCallingIdentity();
            try {
                UserInfo userInfo = AppWidgetServiceImpl.this.mUserManager.getUserInfo(profileId);
                if (userInfo == null || !userInfo.isEnabled()) {
                    Binder.restoreCallingIdentity(identity);
                    return false;
                }
                Binder.restoreCallingIdentity(identity);
                return true;
            } catch (Throwable userInfo2) {
                Binder.restoreCallingIdentity(identity);
                throw userInfo2;
            }
        }
    }

    static final class Provider {
        PendingIntent broadcast;
        ProviderId id;
        AppWidgetProviderInfo info;
        String infoTag;
        boolean maskedByLockedProfile;
        boolean maskedByQuietProfile;
        boolean maskedBySuspendedPackage;
        int tag = -1;
        ArrayList<Widget> widgets = new ArrayList<>();
        boolean zombie;

        Provider() {
        }

        public int getUserId() {
            return UserHandle.getUserId(this.id.uid);
        }

        public boolean isInPackageForUser(String packageName, int userId) {
            return getUserId() == userId && this.id.componentName.getPackageName().equals(packageName);
        }

        public boolean hostedByPackageForUser(String packageName, int userId) {
            int N = this.widgets.size();
            for (int i = 0; i < N; i++) {
                Widget widget = this.widgets.get(i);
                if (packageName.equals(widget.host.id.packageName) && widget.host.getUserId() == userId) {
                    return true;
                }
            }
            return false;
        }

        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("Provider{");
            sb.append(this.id);
            sb.append(this.zombie ? " Z" : "");
            sb.append('}');
            return sb.toString();
        }

        public boolean setMaskedByQuietProfileLocked(boolean masked) {
            boolean oldState = this.maskedByQuietProfile;
            this.maskedByQuietProfile = masked;
            return masked != oldState;
        }

        public boolean setMaskedByLockedProfileLocked(boolean masked) {
            boolean oldState = this.maskedByLockedProfile;
            this.maskedByLockedProfile = masked;
            return masked != oldState;
        }

        public boolean setMaskedBySuspendedPackageLocked(boolean masked) {
            boolean oldState = this.maskedBySuspendedPackage;
            this.maskedBySuspendedPackage = masked;
            return masked != oldState;
        }

        public boolean isMaskedLocked() {
            return this.maskedByQuietProfile || this.maskedByLockedProfile || this.maskedBySuspendedPackage;
        }

        public boolean shouldBePersisted() {
            return !this.widgets.isEmpty() || !TextUtils.isEmpty(this.infoTag);
        }
    }

    private static final class ProviderId {
        final ComponentName componentName;
        final int uid;

        private ProviderId(int uid2, ComponentName componentName2) {
            this.uid = uid2;
            this.componentName = componentName2;
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || getClass() != obj.getClass()) {
                return false;
            }
            ProviderId other = (ProviderId) obj;
            if (this.uid != other.uid) {
                return false;
            }
            ComponentName componentName2 = this.componentName;
            if (componentName2 == null) {
                if (other.componentName != null) {
                    return false;
                }
            } else if (!componentName2.equals(other.componentName)) {
                return false;
            }
            return true;
        }

        public int hashCode() {
            int i = this.uid * 31;
            ComponentName componentName2 = this.componentName;
            return i + (componentName2 != null ? componentName2.hashCode() : 0);
        }

        public String toString() {
            return "ProviderId{user:" + UserHandle.getUserId(this.uid) + ", app:" + UserHandle.getAppId(this.uid) + ", cmp:" + this.componentName + '}';
        }
    }

    private static final class Host {
        IAppWidgetHost callbacks;
        HostId id;
        long lastWidgetUpdateSequenceNo;
        int tag;
        ArrayList<Widget> widgets;
        boolean zombie;

        private Host() {
            this.widgets = new ArrayList<>();
            this.tag = -1;
        }

        public int getUserId() {
            return UserHandle.getUserId(this.id.uid);
        }

        public boolean isInPackageForUser(String packageName, int userId) {
            return getUserId() == userId && this.id.packageName.equals(packageName);
        }

        /* access modifiers changed from: private */
        public boolean hostsPackageForUser(String pkg, int userId) {
            int N = this.widgets.size();
            for (int i = 0; i < N; i++) {
                Provider provider = this.widgets.get(i).provider;
                if (provider != null && provider.getUserId() == userId && provider.info != null && pkg.equals(provider.info.provider.getPackageName())) {
                    return true;
                }
            }
            return false;
        }

        public boolean getPendingUpdatesForId(int appWidgetId, LongSparseArray<PendingHostUpdate> outUpdates) {
            PendingHostUpdate update;
            long updateSequenceNo = this.lastWidgetUpdateSequenceNo;
            int N = this.widgets.size();
            for (int i = 0; i < N; i++) {
                Widget widget = this.widgets.get(i);
                if (widget.appWidgetId == appWidgetId) {
                    outUpdates.clear();
                    for (int j = widget.updateSequenceNos.size() - 1; j >= 0; j--) {
                        long requestId = widget.updateSequenceNos.valueAt(j);
                        if (requestId > updateSequenceNo) {
                            int id2 = widget.updateSequenceNos.keyAt(j);
                            if (id2 == 0) {
                                update = PendingHostUpdate.updateAppWidget(appWidgetId, AppWidgetServiceImpl.cloneIfLocalBinder(widget.getEffectiveViewsLocked()));
                            } else if (id2 != 1) {
                                update = PendingHostUpdate.viewDataChanged(appWidgetId, id2);
                            } else {
                                update = PendingHostUpdate.providerChanged(appWidgetId, widget.provider.info);
                            }
                            outUpdates.put(requestId, update);
                        }
                    }
                    return true;
                }
            }
            return false;
        }

        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("Host{");
            sb.append(this.id);
            sb.append(this.zombie ? " Z" : "");
            sb.append('}');
            return sb.toString();
        }
    }

    private static final class HostId {
        final int hostId;
        final String packageName;
        final int uid;

        public HostId(int uid2, int hostId2, String packageName2) {
            this.uid = uid2;
            this.hostId = hostId2;
            this.packageName = packageName2;
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || getClass() != obj.getClass()) {
                return false;
            }
            HostId other = (HostId) obj;
            if (this.uid != other.uid || this.hostId != other.hostId) {
                return false;
            }
            String str = this.packageName;
            if (str == null) {
                if (other.packageName != null) {
                    return false;
                }
            } else if (!str.equals(other.packageName)) {
                return false;
            }
            return true;
        }

        public int hashCode() {
            int result = ((this.uid * 31) + this.hostId) * 31;
            String str = this.packageName;
            return result + (str != null ? str.hashCode() : 0);
        }

        public String toString() {
            return "HostId{user:" + UserHandle.getUserId(this.uid) + ", app:" + UserHandle.getAppId(this.uid) + ", hostId:" + this.hostId + ", pkg:" + this.packageName + '}';
        }
    }

    private static final class Widget {
        int appWidgetId;
        Host host;
        RemoteViews maskedViews;
        Bundle options;
        Provider provider;
        int restoredId;
        SparseLongArray updateSequenceNos;
        RemoteViews views;

        private Widget() {
            this.updateSequenceNos = new SparseLongArray(2);
        }

        public String toString() {
            return "AppWidgetId{" + this.appWidgetId + ':' + this.host + ':' + this.provider + '}';
        }

        /* access modifiers changed from: private */
        public boolean replaceWithMaskedViewsLocked(RemoteViews views2) {
            this.maskedViews = views2;
            return true;
        }

        /* access modifiers changed from: private */
        public boolean clearMaskedViewsLocked() {
            if (this.maskedViews == null) {
                return false;
            }
            this.maskedViews = null;
            return true;
        }

        public RemoteViews getEffectiveViewsLocked() {
            RemoteViews remoteViews = this.maskedViews;
            return remoteViews != null ? remoteViews : this.views;
        }
    }

    private class LoadedWidgetState {
        final int hostTag;
        final int providerTag;
        final Widget widget;

        public LoadedWidgetState(Widget widget2, int hostTag2, int providerTag2) {
            this.widget = widget2;
            this.hostTag = hostTag2;
            this.providerTag = providerTag2;
        }
    }

    private final class SaveStateRunnable implements Runnable {
        final int mUserId;

        public SaveStateRunnable(int userId) {
            this.mUserId = userId;
        }

        public void run() {
            synchronized (AppWidgetServiceImpl.this.mLock) {
                AppWidgetServiceImpl.this.ensureGroupStateLoadedLocked(this.mUserId, false);
                AppWidgetServiceImpl.this.saveStateLocked(this.mUserId);
            }
        }
    }

    private final class BackupRestoreController {
        private static final boolean DEBUG = true;
        private static final String TAG = "BackupRestoreController";
        private static final int WIDGET_STATE_VERSION = 2;
        private final HashSet<String> mPrunedApps;
        private final HashMap<Host, ArrayList<RestoreUpdateRecord>> mUpdatesByHost;
        private final HashMap<Provider, ArrayList<RestoreUpdateRecord>> mUpdatesByProvider;

        private BackupRestoreController() {
            this.mPrunedApps = new HashSet<>();
            this.mUpdatesByProvider = new HashMap<>();
            this.mUpdatesByHost = new HashMap<>();
        }

        public List<String> getWidgetParticipants(int userId) {
            Slog.i(TAG, "Getting widget participants for user: " + userId);
            HashSet<String> packages = new HashSet<>();
            synchronized (AppWidgetServiceImpl.this.mLock) {
                int N = AppWidgetServiceImpl.this.mWidgets.size();
                for (int i = 0; i < N; i++) {
                    Widget widget = (Widget) AppWidgetServiceImpl.this.mWidgets.get(i);
                    if (isProviderAndHostInUser(widget, userId)) {
                        packages.add(widget.host.id.packageName);
                        Provider provider = widget.provider;
                        if (provider != null) {
                            packages.add(provider.id.componentName.getPackageName());
                        }
                    }
                }
            }
            return new ArrayList(packages);
        }

        public byte[] getWidgetState(String backedupPackage, int userId) {
            Slog.i(TAG, "Getting widget state for user: " + userId);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            synchronized (AppWidgetServiceImpl.this.mLock) {
                if (!packageNeedsWidgetBackupLocked(backedupPackage, userId)) {
                    return null;
                }
                try {
                    XmlSerializer out = new FastXmlSerializer();
                    out.setOutput(stream, StandardCharsets.UTF_8.name());
                    out.startDocument((String) null, true);
                    out.startTag((String) null, "ws");
                    out.attribute((String) null, "version", String.valueOf(2));
                    out.attribute((String) null, SplitScreenReporter.STR_PKG, backedupPackage);
                    int index = 0;
                    int N = AppWidgetServiceImpl.this.mProviders.size();
                    for (int i = 0; i < N; i++) {
                        Provider provider = (Provider) AppWidgetServiceImpl.this.mProviders.get(i);
                        if (provider.shouldBePersisted() && (provider.isInPackageForUser(backedupPackage, userId) || provider.hostedByPackageForUser(backedupPackage, userId))) {
                            provider.tag = index;
                            AppWidgetServiceImpl.serializeProvider(out, provider);
                            index++;
                        }
                    }
                    int N2 = AppWidgetServiceImpl.this.mHosts.size();
                    int index2 = 0;
                    for (int i2 = 0; i2 < N2; i2++) {
                        Host host = (Host) AppWidgetServiceImpl.this.mHosts.get(i2);
                        if (!host.widgets.isEmpty() && (host.isInPackageForUser(backedupPackage, userId) || host.hostsPackageForUser(backedupPackage, userId))) {
                            host.tag = index2;
                            AppWidgetServiceImpl.serializeHost(out, host);
                            index2++;
                        }
                    }
                    int N3 = AppWidgetServiceImpl.this.mWidgets.size();
                    for (int i3 = 0; i3 < N3; i3++) {
                        Widget widget = (Widget) AppWidgetServiceImpl.this.mWidgets.get(i3);
                        Provider provider2 = widget.provider;
                        if (widget.host.isInPackageForUser(backedupPackage, userId) || (provider2 != null && provider2.isInPackageForUser(backedupPackage, userId))) {
                            AppWidgetServiceImpl.serializeAppWidget(out, widget);
                        }
                    }
                    out.endTag((String) null, "ws");
                    out.endDocument();
                    return stream.toByteArray();
                } catch (IOException e) {
                    Slog.w(TAG, "Unable to save widget state for " + backedupPackage);
                    return null;
                }
            }
        }

        public void restoreStarting(int userId) {
            Slog.i(TAG, "Restore starting for user: " + userId);
            synchronized (AppWidgetServiceImpl.this.mLock) {
                this.mPrunedApps.clear();
                this.mUpdatesByProvider.clear();
                this.mUpdatesByHost.clear();
            }
        }

        /* Debug info: failed to restart local var, previous not found, register: 21 */
        public void restoreWidgetState(String packageName, byte[] restoredState, int userId) {
            ArrayList<Host> restoredHosts;
            ArrayList<Provider> restoredProviders;
            ByteArrayInputStream stream;
            Provider p;
            String str = packageName;
            int i = userId;
            Slog.i(TAG, "Restoring widget state for user:" + i + " package: " + str);
            ByteArrayInputStream stream2 = new ByteArrayInputStream(restoredState);
            try {
                ArrayList<Provider> restoredProviders2 = new ArrayList<>();
                ArrayList<Host> restoredHosts2 = new ArrayList<>();
                XmlPullParser parser = Xml.newPullParser();
                parser.setInput(stream2, StandardCharsets.UTF_8.name());
                synchronized (AppWidgetServiceImpl.this.mLock) {
                    while (true) {
                        try {
                            int type = parser.next();
                            if (type == 2) {
                                String tag = parser.getName();
                                if ("ws".equals(tag)) {
                                    try {
                                        String version = parser.getAttributeValue((String) null, "version");
                                        if (Integer.parseInt(version) > 2) {
                                            Slog.w(TAG, "Unable to process state version " + version);
                                            AppWidgetServiceImpl.this.saveGroupStateAsync(i);
                                            return;
                                        } else if (!str.equals(parser.getAttributeValue((String) null, SplitScreenReporter.STR_PKG))) {
                                            Slog.w(TAG, "Package mismatch in ws");
                                            AppWidgetServiceImpl.this.saveGroupStateAsync(i);
                                            return;
                                        } else {
                                            stream = stream2;
                                            restoredProviders = restoredProviders2;
                                            restoredHosts = restoredHosts2;
                                        }
                                    } catch (Throwable th) {
                                        th = th;
                                        ByteArrayInputStream byteArrayInputStream = stream2;
                                        ArrayList<Provider> arrayList = restoredProviders2;
                                        ArrayList<Host> arrayList2 = restoredHosts2;
                                    }
                                } else if ("p".equals(tag)) {
                                    try {
                                        ComponentName componentName = new ComponentName(parser.getAttributeValue((String) null, SplitScreenReporter.STR_PKG), parser.getAttributeValue((String) null, "cl"));
                                        Provider p2 = findProviderLocked(componentName, i);
                                        if (p2 == null) {
                                            p = new Provider();
                                            p.id = new ProviderId(-1, componentName);
                                            p.info = new AppWidgetProviderInfo();
                                            p.info.provider = componentName;
                                            p.zombie = true;
                                            AppWidgetServiceImpl.this.mProviders.add(p);
                                        } else {
                                            p = p2;
                                        }
                                        StringBuilder sb = new StringBuilder();
                                        stream = stream2;
                                        try {
                                            sb.append("   provider ");
                                            sb.append(p.id);
                                            Slog.i(TAG, sb.toString());
                                            restoredProviders2.add(p);
                                            restoredProviders = restoredProviders2;
                                            restoredHosts = restoredHosts2;
                                        } catch (Throwable th2) {
                                            th = th2;
                                            ArrayList<Provider> arrayList3 = restoredProviders2;
                                            ArrayList<Host> arrayList4 = restoredHosts2;
                                            try {
                                                throw th;
                                            } catch (IOException | XmlPullParserException e) {
                                            }
                                        }
                                    } catch (Throwable th3) {
                                        th = th3;
                                        ByteArrayInputStream byteArrayInputStream2 = stream2;
                                        ArrayList<Provider> arrayList5 = restoredProviders2;
                                        ArrayList<Host> arrayList6 = restoredHosts2;
                                        throw th;
                                    }
                                } else {
                                    stream = stream2;
                                    if ("h".equals(tag)) {
                                        String pkg = parser.getAttributeValue((String) null, SplitScreenReporter.STR_PKG);
                                        Host h = AppWidgetServiceImpl.this.lookupOrAddHostLocked(new HostId(AppWidgetServiceImpl.this.getUidForPackage(pkg, i), Integer.parseInt(parser.getAttributeValue((String) null, "id"), 16), pkg));
                                        restoredHosts2.add(h);
                                        StringBuilder sb2 = new StringBuilder();
                                        String str2 = pkg;
                                        sb2.append("   host[");
                                        sb2.append(restoredHosts2.size());
                                        sb2.append("]: {");
                                        sb2.append(h.id);
                                        sb2.append("}");
                                        Slog.i(TAG, sb2.toString());
                                        restoredProviders = restoredProviders2;
                                        restoredHosts = restoredHosts2;
                                    } else if ("g".equals(tag)) {
                                        int restoredId = Integer.parseInt(parser.getAttributeValue((String) null, "id"), 16);
                                        Host host = restoredHosts2.get(Integer.parseInt(parser.getAttributeValue((String) null, "h"), 16));
                                        Provider p3 = null;
                                        String prov = parser.getAttributeValue((String) null, "p");
                                        if (prov != null) {
                                            p3 = restoredProviders2.get(Integer.parseInt(prov, 16));
                                        }
                                        try {
                                            pruneWidgetStateLocked(host.id.packageName, i);
                                            if (p3 != null) {
                                                pruneWidgetStateLocked(p3.id.componentName.getPackageName(), i);
                                            }
                                            Widget id = findRestoredWidgetLocked(restoredId, host, p3);
                                            if (id == null) {
                                                Widget widget = id;
                                                id = new Widget();
                                                id.appWidgetId = AppWidgetServiceImpl.this.incrementAndGetAppWidgetIdLocked(i);
                                                id.restoredId = restoredId;
                                                id.options = parseWidgetIdOptions(parser);
                                                id.host = host;
                                                id.host.widgets.add(id);
                                                id.provider = p3;
                                                if (id.provider != null) {
                                                    id.provider.widgets.add(id);
                                                }
                                                restoredProviders = restoredProviders2;
                                                try {
                                                    StringBuilder sb3 = new StringBuilder();
                                                    restoredHosts = restoredHosts2;
                                                    sb3.append("New restored id ");
                                                    sb3.append(restoredId);
                                                    sb3.append(" now ");
                                                    sb3.append(id);
                                                    Slog.i(TAG, sb3.toString());
                                                    AppWidgetServiceImpl.this.addWidgetLocked(id);
                                                } catch (Throwable th4) {
                                                    th = th4;
                                                    throw th;
                                                }
                                            } else {
                                                Widget widget2 = id;
                                                restoredProviders = restoredProviders2;
                                                restoredHosts = restoredHosts2;
                                            }
                                            if (id.provider == null || id.provider.info == null) {
                                                Slog.w(TAG, "Missing provider for restored widget " + id);
                                            } else {
                                                stashProviderRestoreUpdateLocked(id.provider, restoredId, id.appWidgetId);
                                            }
                                            stashHostRestoreUpdateLocked(id.host, restoredId, id.appWidgetId);
                                            Slog.i(TAG, "   instance: " + restoredId + " -> " + id.appWidgetId + " :: p=" + id.provider);
                                        } catch (Throwable th5) {
                                            th = th5;
                                            ArrayList<Provider> arrayList7 = restoredProviders2;
                                            ArrayList<Host> arrayList8 = restoredHosts2;
                                            throw th;
                                        }
                                    } else {
                                        restoredProviders = restoredProviders2;
                                        restoredHosts = restoredHosts2;
                                    }
                                }
                            } else {
                                stream = stream2;
                                restoredProviders = restoredProviders2;
                                restoredHosts = restoredHosts2;
                            }
                            if (type != 1) {
                                byte[] bArr = restoredState;
                                stream2 = stream;
                                restoredProviders2 = restoredProviders;
                                restoredHosts2 = restoredHosts;
                            }
                        } catch (Throwable th6) {
                            th = th6;
                            ByteArrayInputStream byteArrayInputStream3 = stream2;
                            ArrayList<Provider> arrayList9 = restoredProviders2;
                            ArrayList<Host> arrayList10 = restoredHosts2;
                            throw th;
                        }
                    }
                    AppWidgetServiceImpl.this.saveGroupStateAsync(i);
                }
            } catch (IOException | XmlPullParserException e2) {
                ByteArrayInputStream byteArrayInputStream4 = stream2;
                try {
                    Slog.w(TAG, "Unable to restore widget state for " + str);
                    AppWidgetServiceImpl.this.saveGroupStateAsync(i);
                } catch (Throwable th7) {
                    th = th7;
                }
            } catch (Throwable th8) {
                th = th8;
                ByteArrayInputStream byteArrayInputStream5 = stream2;
                AppWidgetServiceImpl.this.saveGroupStateAsync(i);
                throw th;
            }
        }

        public void restoreFinished(int userId) {
            int i = userId;
            Slog.i(TAG, "restoreFinished for " + i);
            UserHandle userHandle = new UserHandle(i);
            synchronized (AppWidgetServiceImpl.this.mLock) {
                Iterator<Map.Entry<Provider, ArrayList<RestoreUpdateRecord>>> it = this.mUpdatesByProvider.entrySet().iterator();
                while (true) {
                    boolean z = true;
                    if (!it.hasNext()) {
                        break;
                    }
                    Map.Entry next = it.next();
                    Provider provider = (Provider) next.getKey();
                    ArrayList arrayList = (ArrayList) next.getValue();
                    int pending = countPendingUpdates(arrayList);
                    Slog.i(TAG, "Provider " + provider + " pending: " + pending);
                    if (pending > 0) {
                        int[] oldIds = new int[pending];
                        int[] newIds = new int[pending];
                        int N = arrayList.size();
                        int i2 = 0;
                        int nextPending = 0;
                        while (i2 < N) {
                            RestoreUpdateRecord r = (RestoreUpdateRecord) arrayList.get(i2);
                            if (!r.notified) {
                                r.notified = z;
                                oldIds[nextPending] = r.oldId;
                                newIds[nextPending] = r.newId;
                                nextPending++;
                                Slog.i(TAG, "   " + r.oldId + " => " + r.newId);
                            }
                            i2++;
                            z = true;
                        }
                        int i3 = N;
                        sendWidgetRestoreBroadcastLocked("android.appwidget.action.APPWIDGET_RESTORED", provider, (Host) null, oldIds, newIds, userHandle);
                    }
                }
                for (Map.Entry<Host, ArrayList<RestoreUpdateRecord>> e : this.mUpdatesByHost.entrySet()) {
                    Host host = e.getKey();
                    if (host.id.uid != -1) {
                        ArrayList<RestoreUpdateRecord> updates = e.getValue();
                        int pending2 = countPendingUpdates(updates);
                        Slog.i(TAG, "Host " + host + " pending: " + pending2);
                        if (pending2 > 0) {
                            int[] oldIds2 = new int[pending2];
                            int[] newIds2 = new int[pending2];
                            int N2 = updates.size();
                            int nextPending2 = 0;
                            for (int i4 = 0; i4 < N2; i4++) {
                                RestoreUpdateRecord r2 = updates.get(i4);
                                if (!r2.notified) {
                                    r2.notified = true;
                                    oldIds2[nextPending2] = r2.oldId;
                                    newIds2[nextPending2] = r2.newId;
                                    nextPending2++;
                                    Slog.i(TAG, "   " + r2.oldId + " => " + r2.newId);
                                }
                            }
                            int i5 = N2;
                            sendWidgetRestoreBroadcastLocked("android.appwidget.action.APPWIDGET_HOST_RESTORED", (Provider) null, host, oldIds2, newIds2, userHandle);
                        }
                    }
                    int i6 = userId;
                }
            }
        }

        private Provider findProviderLocked(ComponentName componentName, int userId) {
            int providerCount = AppWidgetServiceImpl.this.mProviders.size();
            for (int i = 0; i < providerCount; i++) {
                Provider provider = (Provider) AppWidgetServiceImpl.this.mProviders.get(i);
                if (provider.getUserId() == userId && provider.id.componentName.equals(componentName)) {
                    return provider;
                }
            }
            return null;
        }

        private Widget findRestoredWidgetLocked(int restoredId, Host host, Provider p) {
            Slog.i(TAG, "Find restored widget: id=" + restoredId + " host=" + host + " provider=" + p);
            if (p == null || host == null) {
                return null;
            }
            int N = AppWidgetServiceImpl.this.mWidgets.size();
            int i = 0;
            while (i < N) {
                Widget widget = (Widget) AppWidgetServiceImpl.this.mWidgets.get(i);
                if (widget.restoredId != restoredId || !widget.host.id.equals(host.id) || !widget.provider.id.equals(p.id)) {
                    i++;
                } else {
                    Slog.i(TAG, "   Found at " + i + " : " + widget);
                    return widget;
                }
            }
            return null;
        }

        private boolean packageNeedsWidgetBackupLocked(String packageName, int userId) {
            int N = AppWidgetServiceImpl.this.mWidgets.size();
            for (int i = 0; i < N; i++) {
                Widget widget = (Widget) AppWidgetServiceImpl.this.mWidgets.get(i);
                if (isProviderAndHostInUser(widget, userId)) {
                    if (widget.host.isInPackageForUser(packageName, userId)) {
                        return true;
                    }
                    Provider provider = widget.provider;
                    if (provider != null && provider.isInPackageForUser(packageName, userId)) {
                        return true;
                    }
                }
            }
            return false;
        }

        private void stashProviderRestoreUpdateLocked(Provider provider, int oldId, int newId) {
            ArrayList<RestoreUpdateRecord> r = this.mUpdatesByProvider.get(provider);
            if (r == null) {
                r = new ArrayList<>();
                this.mUpdatesByProvider.put(provider, r);
            } else if (alreadyStashed(r, oldId, newId)) {
                Slog.i(TAG, "ID remap " + oldId + " -> " + newId + " already stashed for " + provider);
                return;
            }
            r.add(new RestoreUpdateRecord(oldId, newId));
        }

        private boolean alreadyStashed(ArrayList<RestoreUpdateRecord> stash, int oldId, int newId) {
            int N = stash.size();
            for (int i = 0; i < N; i++) {
                RestoreUpdateRecord r = stash.get(i);
                if (r.oldId == oldId && r.newId == newId) {
                    return true;
                }
            }
            return false;
        }

        private void stashHostRestoreUpdateLocked(Host host, int oldId, int newId) {
            ArrayList<RestoreUpdateRecord> r = this.mUpdatesByHost.get(host);
            if (r == null) {
                r = new ArrayList<>();
                this.mUpdatesByHost.put(host, r);
            } else if (alreadyStashed(r, oldId, newId)) {
                Slog.i(TAG, "ID remap " + oldId + " -> " + newId + " already stashed for " + host);
                return;
            }
            r.add(new RestoreUpdateRecord(oldId, newId));
        }

        private void sendWidgetRestoreBroadcastLocked(String action, Provider provider, Host host, int[] oldIds, int[] newIds, UserHandle userHandle) {
            Intent intent = new Intent(action);
            intent.putExtra("appWidgetOldIds", oldIds);
            intent.putExtra("appWidgetIds", newIds);
            if (provider != null) {
                intent.setComponent(provider.info.provider);
                AppWidgetServiceImpl.this.sendBroadcastAsUser(intent, userHandle);
            }
            if (host != null) {
                intent.setComponent((ComponentName) null);
                intent.setPackage(host.id.packageName);
                intent.putExtra("hostId", host.id.hostId);
                AppWidgetServiceImpl.this.sendBroadcastAsUser(intent, userHandle);
            }
        }

        private void pruneWidgetStateLocked(String pkg, int userId) {
            if (!this.mPrunedApps.contains(pkg)) {
                Slog.i(TAG, "pruning widget state for restoring package " + pkg);
                for (int i = AppWidgetServiceImpl.this.mWidgets.size() + -1; i >= 0; i--) {
                    Widget widget = (Widget) AppWidgetServiceImpl.this.mWidgets.get(i);
                    Host host = widget.host;
                    Provider provider = widget.provider;
                    if (host.hostsPackageForUser(pkg, userId) || (provider != null && provider.isInPackageForUser(pkg, userId))) {
                        host.widgets.remove(widget);
                        provider.widgets.remove(widget);
                        AppWidgetServiceImpl.this.decrementAppWidgetServiceRefCount(widget);
                        AppWidgetServiceImpl.this.removeWidgetLocked(widget);
                    }
                }
                this.mPrunedApps.add(pkg);
                return;
            }
            Slog.i(TAG, "already pruned " + pkg + ", continuing normally");
        }

        private boolean isProviderAndHostInUser(Widget widget, int userId) {
            return widget.host.getUserId() == userId && (widget.provider == null || widget.provider.getUserId() == userId);
        }

        private Bundle parseWidgetIdOptions(XmlPullParser parser) {
            Bundle options = new Bundle();
            String minWidthString = parser.getAttributeValue((String) null, "min_width");
            if (minWidthString != null) {
                options.putInt("appWidgetMinWidth", Integer.parseInt(minWidthString, 16));
            }
            String minHeightString = parser.getAttributeValue((String) null, "min_height");
            if (minHeightString != null) {
                options.putInt("appWidgetMinHeight", Integer.parseInt(minHeightString, 16));
            }
            String maxWidthString = parser.getAttributeValue((String) null, "max_width");
            if (maxWidthString != null) {
                options.putInt("appWidgetMaxWidth", Integer.parseInt(maxWidthString, 16));
            }
            String maxHeightString = parser.getAttributeValue((String) null, "max_height");
            if (maxHeightString != null) {
                options.putInt("appWidgetMaxHeight", Integer.parseInt(maxHeightString, 16));
            }
            String categoryString = parser.getAttributeValue((String) null, "host_category");
            if (categoryString != null) {
                options.putInt("appWidgetCategory", Integer.parseInt(categoryString, 16));
            }
            return options;
        }

        private int countPendingUpdates(ArrayList<RestoreUpdateRecord> updates) {
            int pending = 0;
            int N = updates.size();
            for (int i = 0; i < N; i++) {
                if (!updates.get(i).notified) {
                    pending++;
                }
            }
            return pending;
        }

        private class RestoreUpdateRecord {
            public int newId;
            public boolean notified = false;
            public int oldId;

            public RestoreUpdateRecord(int theOldId, int theNewId) {
                this.oldId = theOldId;
                this.newId = theNewId;
            }
        }
    }

    private class AppWidgetManagerLocal extends AppWidgetManagerInternal {
        private AppWidgetManagerLocal() {
        }

        public ArraySet<String> getHostedWidgetPackages(int uid) {
            ArraySet<String> widgetPackages;
            synchronized (AppWidgetServiceImpl.this.mLock) {
                widgetPackages = null;
                int widgetCount = AppWidgetServiceImpl.this.mWidgets.size();
                for (int i = 0; i < widgetCount; i++) {
                    Widget widget = (Widget) AppWidgetServiceImpl.this.mWidgets.get(i);
                    if (widget.host.id.uid == uid) {
                        if (widgetPackages == null) {
                            widgetPackages = new ArraySet<>();
                        }
                        widgetPackages.add(widget.provider.id.componentName.getPackageName());
                    }
                }
            }
            return widgetPackages;
        }

        public void unlockUser(int userId) {
            AppWidgetServiceImpl.this.handleUserUnlocked(userId);
        }
    }
}
