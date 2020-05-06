package com.android.server.pm;

import android.content.ComponentName;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.ComponentInfo;
import android.content.pm.IntentFilterVerificationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManagerInternal;
import android.content.pm.PackageParser;
import android.content.pm.PackageUserState;
import android.content.pm.PermissionInfo;
import android.content.pm.ResolveInfo;
import android.content.pm.Signature;
import android.content.pm.UserInfo;
import android.content.pm.VerifierDeviceIdentity;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.Environment;
import android.os.FileUtils;
import android.os.Handler;
import android.os.Message;
import android.os.PatternMatcher;
import android.os.SELinux;
import android.os.SystemClock;
import android.os.UserHandle;
import android.os.storage.StorageManager;
import android.server.am.SplitScreenReporter;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.ArraySet;
import android.util.AtomicFile;
import android.util.Log;
import android.util.Slog;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import android.util.SparseIntArray;
import android.util.SparseLongArray;
import android.util.Xml;
import android.util.proto.ProtoOutputStream;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.os.BackgroundThread;
import com.android.internal.util.ArrayUtils;
import com.android.internal.util.CollectionUtils;
import com.android.internal.util.IndentingPrintWriter;
import com.android.internal.util.JournaledFile;
import com.android.internal.util.XmlUtils;
import com.android.server.LocalServices;
import com.android.server.UiModeManagerService;
import com.android.server.pm.permission.BasePermission;
import com.android.server.pm.permission.PermissionSettings;
import com.android.server.pm.permission.PermissionsState;
import com.android.server.voiceinteraction.DatabaseHelper;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import libcore.io.IoUtils;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

public final class Settings {
    private static final String ATTR_APP_LINK_GENERATION = "app-link-generation";
    private static final String ATTR_BLOCKED = "blocked";
    @Deprecated
    private static final String ATTR_BLOCK_UNINSTALL = "blockUninstall";
    private static final String ATTR_CE_DATA_INODE = "ceDataInode";
    private static final String ATTR_DATABASE_VERSION = "databaseVersion";
    private static final String ATTR_DISTRACTION_FLAGS = "distraction_flags";
    private static final String ATTR_DOMAIN_VERIFICATON_STATE = "domainVerificationStatus";
    private static final String ATTR_ENABLED = "enabled";
    private static final String ATTR_ENABLED_CALLER = "enabledCaller";
    private static final String ATTR_ENFORCEMENT = "enforcement";
    private static final String ATTR_FINGERPRINT = "fingerprint";
    private static final String ATTR_FLAGS = "flags";
    private static final String ATTR_GRANTED = "granted";
    private static final String ATTR_HARMFUL_APP_WARNING = "harmful-app-warning";
    private static final String ATTR_HIDDEN = "hidden";
    private static final String ATTR_INSTALLED = "inst";
    private static final String ATTR_INSTALL_REASON = "install-reason";
    private static final String ATTR_INSTANT_APP = "instant-app";
    public static final String ATTR_NAME = "name";
    private static final String ATTR_NOT_LAUNCHED = "nl";
    public static final String ATTR_PACKAGE = "package";
    private static final String ATTR_PACKAGE_NAME = "packageName";
    private static final String ATTR_SDK_VERSION = "sdkVersion";
    private static final String ATTR_STOPPED = "stopped";
    private static final String ATTR_SUSPENDED = "suspended";
    private static final String ATTR_SUSPENDING_PACKAGE = "suspending-package";
    @Deprecated
    private static final String ATTR_SUSPEND_DIALOG_MESSAGE = "suspend_dialog_message";
    private static final String ATTR_VERSION = "version";
    private static final String ATTR_VIRTUAL_PRELOAD = "virtual-preload";
    private static final String ATTR_VOLUME_UUID = "volumeUuid";
    public static final int CURRENT_DATABASE_VERSION = 3;
    private static final boolean DEBUG_KERNEL = false;
    private static final boolean DEBUG_MU = false;
    private static final boolean DEBUG_PARSER = false;
    private static final boolean DEBUG_STOPPED = false;
    static final Object[] FLAG_DUMP_SPEC = {1, "SYSTEM", 2, "DEBUGGABLE", 4, "HAS_CODE", 8, "PERSISTENT", 16, "FACTORY_TEST", 32, "ALLOW_TASK_REPARENTING", 64, "ALLOW_CLEAR_USER_DATA", 128, "UPDATED_SYSTEM_APP", 256, "TEST_ONLY", 16384, "VM_SAFE_MODE", 32768, "ALLOW_BACKUP", 65536, "KILL_AFTER_RESTORE", 131072, "RESTORE_ANY_VERSION", Integer.valueOf(DumpState.DUMP_DOMAIN_PREFERRED), "EXTERNAL_STORAGE", Integer.valueOf(DumpState.DUMP_DEXOPT), "LARGE_HEAP"};
    private static int PRE_M_APP_INFO_FLAG_CANT_SAVE_STATE = 268435456;
    private static int PRE_M_APP_INFO_FLAG_HIDDEN = 134217728;
    private static int PRE_M_APP_INFO_FLAG_PRIVILEGED = 1073741824;
    private static final Object[] PRIVATE_FLAG_DUMP_SPEC = {1024, "PRIVATE_FLAG_ACTIVITIES_RESIZE_MODE_RESIZEABLE", 4096, "PRIVATE_FLAG_ACTIVITIES_RESIZE_MODE_RESIZEABLE_VIA_SDK_VERSION", 2048, "PRIVATE_FLAG_ACTIVITIES_RESIZE_MODE_UNRESIZEABLE", 134217728, "ALLOW_AUDIO_PLAYBACK_CAPTURE", 536870912, "PRIVATE_FLAG_REQUEST_LEGACY_EXTERNAL_STORAGE", 8192, "BACKUP_IN_FOREGROUND", 2, "CANT_SAVE_STATE", 32, "DEFAULT_TO_DEVICE_PROTECTED_STORAGE", 64, "DIRECT_BOOT_AWARE", 16, "HAS_DOMAIN_URLS", 1, "HIDDEN", 128, "EPHEMERAL", 32768, "ISOLATED_SPLIT_LOADING", 131072, "OEM", 256, "PARTIALLY_DIRECT_BOOT_AWARE", 8, "PRIVILEGED", 512, "REQUIRED_FOR_SYSTEM_USER", 16384, "STATIC_SHARED_LIBRARY", Integer.valueOf(DumpState.DUMP_DOMAIN_PREFERRED), "VENDOR", Integer.valueOf(DumpState.DUMP_FROZEN), "PRODUCT", Integer.valueOf(DumpState.DUMP_COMPILER_STATS), "PRODUCT_SERVICES", 65536, "VIRTUAL_PRELOAD", 1073741824, "ODM"};
    private static final String RUNTIME_PERMISSIONS_FILE_NAME = "runtime-permissions.xml";
    private static final String TAG = "PackageSettings";
    private static final String TAG_ALL_INTENT_FILTER_VERIFICATION = "all-intent-filter-verifications";
    private static final String TAG_BLOCK_UNINSTALL = "block-uninstall";
    private static final String TAG_BLOCK_UNINSTALL_PACKAGES = "block-uninstall-packages";
    private static final String TAG_CHILD_PACKAGE = "child-package";
    static final String TAG_CROSS_PROFILE_INTENT_FILTERS = "crossProfile-intent-filters";
    private static final String TAG_DEFAULT_APPS = "default-apps";
    private static final String TAG_DEFAULT_BROWSER = "default-browser";
    private static final String TAG_DEFAULT_DIALER = "default-dialer";
    private static final String TAG_DISABLED_COMPONENTS = "disabled-components";
    private static final String TAG_DOMAIN_VERIFICATION = "domain-verification";
    private static final String TAG_ENABLED_COMPONENTS = "enabled-components";
    public static final String TAG_ITEM = "item";
    private static final String TAG_PACKAGE = "pkg";
    private static final String TAG_PACKAGE_RESTRICTIONS = "package-restrictions";
    private static final String TAG_PERMISSIONS = "perms";
    private static final String TAG_PERSISTENT_PREFERRED_ACTIVITIES = "persistent-preferred-activities";
    private static final String TAG_READ_EXTERNAL_STORAGE = "read-external-storage";
    private static final String TAG_RUNTIME_PERMISSIONS = "runtime-permissions";
    private static final String TAG_SHARED_USER = "shared-user";
    private static final String TAG_SUSPENDED_APP_EXTRAS = "suspended-app-extras";
    private static final String TAG_SUSPENDED_DIALOG_INFO = "suspended-dialog-info";
    private static final String TAG_SUSPENDED_LAUNCHER_EXTRAS = "suspended-launcher-extras";
    private static final String TAG_USES_STATIC_LIB = "uses-static-lib";
    private static final String TAG_VERSION = "version";
    private static int mFirstAvailableUid = 0;
    private final ArrayList<SettingBase> mAppIds = new ArrayList<>();
    private final File mBackupSettingsFilename;
    private final File mBackupStoppedPackagesFilename;
    private final SparseArray<ArraySet<String>> mBlockUninstallPackages = new SparseArray<>();
    final SparseArray<CrossProfileIntentResolver> mCrossProfileIntentResolvers = new SparseArray<>();
    final SparseArray<String> mDefaultBrowserApp = new SparseArray<>();
    private final ArrayMap<String, PackageSetting> mDisabledSysPackages = new ArrayMap<>();
    final ArraySet<String> mInstallerPackages = new ArraySet<>();
    private final ArrayMap<String, KernelPackageState> mKernelMapping = new ArrayMap<>();
    private final File mKernelMappingFilename;
    public final KeySetManagerService mKeySetManagerService = new KeySetManagerService(this.mPackages);
    private final ArrayMap<Long, Integer> mKeySetRefs = new ArrayMap<>();
    private final Object mLock;
    final SparseIntArray mNextAppLinkGeneration = new SparseIntArray();
    private final SparseArray<SettingBase> mOtherAppIds = new SparseArray<>();
    private final File mPackageListFilename;
    final ArrayMap<String, PackageSetting> mPackages = new ArrayMap<>();
    private final ArrayList<Signature> mPastSignatures = new ArrayList<>();
    private final ArrayList<PackageSetting> mPendingPackages = new ArrayList<>();
    final PermissionSettings mPermissions;
    final SparseArray<PersistentPreferredIntentResolver> mPersistentPreferredActivities = new SparseArray<>();
    final SparseArray<PreferredIntentResolver> mPreferredActivities = new SparseArray<>();
    Boolean mReadExternalStorageEnforced;
    final StringBuilder mReadMessages = new StringBuilder();
    private final ArrayMap<String, String> mRenamedPackages = new ArrayMap<>();
    private final ArrayMap<String, IntentFilterVerificationInfo> mRestoredIntentFilterVerifications = new ArrayMap<>();
    private final RuntimePermissionPersistence mRuntimePermissionsPersistence;
    private final File mSettingsFilename;
    final ArrayMap<String, SharedUserSetting> mSharedUsers = new ArrayMap<>();
    private final File mStoppedPackagesFilename;
    private final File mSystemDir;
    private VerifierDeviceIdentity mVerifierDeviceIdentity;
    private ArrayMap<String, VersionInfo> mVersion = new ArrayMap<>();

    public static class DatabaseVersion {
        public static final int FIRST_VERSION = 1;
        public static final int SIGNATURE_END_ENTITY = 2;
        public static final int SIGNATURE_MALFORMED_RECOVER = 3;
    }

    private static final class KernelPackageState {
        int appId;
        int[] excludedUserIds;

        private KernelPackageState() {
        }
    }

    public static class VersionInfo {
        int databaseVersion;
        String fingerprint;
        int sdkVersion;

        public void forceCurrent() {
            this.sdkVersion = Build.VERSION.SDK_INT;
            this.databaseVersion = 3;
            this.fingerprint = Build.FINGERPRINT;
        }
    }

    Settings(File dataDir, PermissionSettings permission, Object lock) {
        this.mLock = lock;
        this.mPermissions = permission;
        this.mRuntimePermissionsPersistence = new RuntimePermissionPersistence(this.mLock);
        this.mSystemDir = new File(dataDir, "system");
        this.mSystemDir.mkdirs();
        FileUtils.setPermissions(this.mSystemDir.toString(), 509, -1, -1);
        this.mSettingsFilename = new File(this.mSystemDir, "packages.xml");
        this.mBackupSettingsFilename = new File(this.mSystemDir, "packages-backup.xml");
        this.mPackageListFilename = new File(this.mSystemDir, "packages.list");
        FileUtils.setPermissions(this.mPackageListFilename, 416, 1000, 1032);
        File kernelDir = new File("/config/sdcardfs");
        this.mKernelMappingFilename = kernelDir.exists() ? kernelDir : null;
        this.mStoppedPackagesFilename = new File(this.mSystemDir, "packages-stopped.xml");
        this.mBackupStoppedPackagesFilename = new File(this.mSystemDir, "packages-stopped-backup.xml");
    }

    /* access modifiers changed from: package-private */
    public PackageSetting getPackageLPr(String pkgName) {
        return this.mPackages.get(pkgName);
    }

    /* access modifiers changed from: package-private */
    public String getRenamedPackageLPr(String pkgName) {
        return this.mRenamedPackages.get(pkgName);
    }

    /* access modifiers changed from: package-private */
    public String addRenamedPackageLPw(String pkgName, String origPkgName) {
        return this.mRenamedPackages.put(pkgName, origPkgName);
    }

    public boolean canPropagatePermissionToInstantApp(String permName) {
        return this.mPermissions.canPropagatePermissionToInstantApp(permName);
    }

    /* access modifiers changed from: package-private */
    public void setInstallerPackageName(String pkgName, String installerPkgName) {
        PackageSetting p = this.mPackages.get(pkgName);
        if (p != null) {
            p.setInstallerPackageName(installerPkgName);
            if (installerPkgName != null) {
                this.mInstallerPackages.add(installerPkgName);
            }
        }
    }

    /* access modifiers changed from: package-private */
    public SharedUserSetting getSharedUserLPw(String name, int pkgFlags, int pkgPrivateFlags, boolean create) throws PackageManagerException {
        SharedUserSetting s = this.mSharedUsers.get(name);
        if (s == null && create) {
            s = new SharedUserSetting(name, pkgFlags, pkgPrivateFlags);
            s.userId = acquireAndRegisterNewAppIdLPw(s);
            if (s.userId >= 0) {
                Log.i("PackageManager", "New shared user " + name + ": id=" + s.userId);
                this.mSharedUsers.put(name, s);
            } else {
                throw new PackageManagerException(-4, "Creating shared user " + name + " failed");
            }
        }
        return s;
    }

    /* access modifiers changed from: package-private */
    public Collection<SharedUserSetting> getAllSharedUsersLPw() {
        return this.mSharedUsers.values();
    }

    /* access modifiers changed from: package-private */
    public boolean disableSystemPackageLPw(String name, boolean replaced) {
        PackageSetting disabled;
        PackageSetting p = this.mPackages.get(name);
        if (p == null) {
            Log.w("PackageManager", "Package " + name + " is not an installed package");
            return false;
        } else if (this.mDisabledSysPackages.get(name) != null || p.pkg == null || !p.pkg.isSystem() || p.pkg.isUpdatedSystemApp()) {
            return false;
        } else {
            if (!(p.pkg == null || p.pkg.applicationInfo == null)) {
                p.pkg.applicationInfo.flags |= 128;
            }
            if (replaced) {
                disabled = new PackageSetting(p);
            } else {
                disabled = p;
            }
            this.mDisabledSysPackages.put(name, disabled);
            return true;
        }
    }

    /* access modifiers changed from: package-private */
    public PackageSetting enableSystemPackageLPw(String name) {
        String str = name;
        PackageSetting p = this.mDisabledSysPackages.get(str);
        if (p == null) {
            Log.w("PackageManager", "Package " + str + " is not disabled");
            return null;
        }
        if (!(p.pkg == null || p.pkg.applicationInfo == null)) {
            p.pkg.applicationInfo.flags &= -129;
        }
        String str2 = p.realName;
        PackageSetting p2 = p;
        String str3 = str2;
        PackageSetting packageSetting = p2;
        PackageSetting ret = addPackageLPw(name, str3, p.codePath, p.resourcePath, p.legacyNativeLibraryPathString, p.primaryCpuAbiString, p.secondaryCpuAbiString, p.cpuAbiOverrideString, p.appId, p.versionCode, p.pkgFlags, p.pkgPrivateFlags, p.parentPackageName, p.childPackageNames, p2.usesStaticLibraries, p2.usesStaticLibrariesVersions);
        this.mDisabledSysPackages.remove(name);
        return ret;
    }

    /* access modifiers changed from: package-private */
    public boolean isDisabledSystemPackageLPr(String name) {
        return this.mDisabledSysPackages.containsKey(name);
    }

    /* access modifiers changed from: package-private */
    public void removeDisabledSystemPackageLPw(String name) {
        this.mDisabledSysPackages.remove(name);
    }

    /* access modifiers changed from: package-private */
    public PackageSetting addPackageLPw(String name, String realName, File codePath, File resourcePath, String legacyNativeLibraryPathString, String primaryCpuAbiString, String secondaryCpuAbiString, String cpuAbiOverrideString, int uid, long vc, int pkgFlags, int pkgPrivateFlags, String parentPackageName, List<String> childPackageNames, String[] usesStaticLibraries, long[] usesStaticLibraryNames) {
        String str = name;
        int i = uid;
        PackageSetting p = this.mPackages.get(str);
        if (p == null) {
            String str2 = name;
            PackageSetting packageSetting = p;
            int i2 = i;
            PackageSetting p2 = new PackageSetting(str2, realName, codePath, resourcePath, legacyNativeLibraryPathString, primaryCpuAbiString, secondaryCpuAbiString, cpuAbiOverrideString, vc, pkgFlags, pkgPrivateFlags, parentPackageName, childPackageNames, 0, usesStaticLibraries, usesStaticLibraryNames);
            p2.appId = i2;
            int i3 = i2;
            if (!registerExistingAppIdLPw(i3, p2, str2)) {
                return null;
            }
            this.mPackages.put(str2, p2);
            return p2;
        } else if (p.appId == i) {
            return p;
        } else {
            PackageManagerService.reportSettingsProblem(6, "Adding duplicate package, keeping first: " + str);
            return null;
        }
    }

    /* access modifiers changed from: package-private */
    public void addAppOpPackage(String permName, String packageName) {
        this.mPermissions.addAppOpPackage(permName, packageName);
    }

    /* access modifiers changed from: package-private */
    public SharedUserSetting addSharedUserLPw(String name, int uid, int pkgFlags, int pkgPrivateFlags) {
        SharedUserSetting s = this.mSharedUsers.get(name);
        if (s == null) {
            SharedUserSetting s2 = new SharedUserSetting(name, pkgFlags, pkgPrivateFlags);
            s2.userId = uid;
            if (!registerExistingAppIdLPw(uid, s2, name)) {
                return null;
            }
            this.mSharedUsers.put(name, s2);
            return s2;
        } else if (s.userId == uid) {
            return s;
        } else {
            PackageManagerService.reportSettingsProblem(6, "Adding duplicate shared user, keeping first: " + name);
            return null;
        }
    }

    /* access modifiers changed from: package-private */
    public void pruneSharedUsersLPw() {
        ArrayList<String> removeStage = new ArrayList<>();
        for (Map.Entry<String, SharedUserSetting> entry : this.mSharedUsers.entrySet()) {
            SharedUserSetting sus = entry.getValue();
            if (sus == null) {
                removeStage.add(entry.getKey());
            } else {
                Iterator<PackageSetting> iter = sus.packages.iterator();
                while (iter.hasNext()) {
                    if (this.mPackages.get(iter.next().name) == null) {
                        iter.remove();
                    }
                }
                if (sus.packages.size() == 0) {
                    removeStage.add(entry.getKey());
                }
            }
        }
        for (int i = 0; i < removeStage.size(); i++) {
            this.mSharedUsers.remove(removeStage.get(i));
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:23:0x00d3, code lost:
        if (isAdbInstallDisallowed(r66, r9.id) != false) goto L_0x00d8;
     */
    /* JADX WARNING: Removed duplicated region for block: B:34:0x00f2  */
    /* JADX WARNING: Removed duplicated region for block: B:35:0x00f5  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    static com.android.server.pm.PackageSetting createNewSetting(java.lang.String r46, com.android.server.pm.PackageSetting r47, com.android.server.pm.PackageSetting r48, java.lang.String r49, com.android.server.pm.SharedUserSetting r50, java.io.File r51, java.io.File r52, java.lang.String r53, java.lang.String r54, java.lang.String r55, long r56, int r58, int r59, android.os.UserHandle r60, boolean r61, boolean r62, boolean r63, java.lang.String r64, java.util.List<java.lang.String> r65, com.android.server.pm.UserManagerService r66, java.lang.String[] r67, long[] r68) {
        /*
            r0 = r47
            r1 = r48
            r2 = r50
            r15 = r58
            r14 = r60
            r12 = r65
            if (r0 == 0) goto L_0x005e
            com.android.server.pm.PackageSetting r3 = new com.android.server.pm.PackageSetting
            r13 = r46
            r3.<init>(r0, r13)
            if (r12 == 0) goto L_0x001e
            java.util.ArrayList r4 = new java.util.ArrayList
            r4.<init>(r12)
            goto L_0x001f
        L_0x001e:
            r4 = 0
        L_0x001f:
            r3.childPackageNames = r4
            r10 = r51
            r3.codePath = r10
            r9 = r53
            r3.legacyNativeLibraryPathString = r9
            r8 = r64
            r3.parentPackageName = r8
            r3.pkgFlags = r15
            r7 = r59
            r3.pkgPrivateFlags = r7
            r6 = r54
            r3.primaryCpuAbiString = r6
            r5 = r52
            r3.resourcePath = r5
            r4 = r55
            r3.secondaryCpuAbiString = r4
            com.android.server.pm.PackageSignatures r11 = new com.android.server.pm.PackageSignatures
            r11.<init>()
            r3.signatures = r11
            r11 = r56
            r3.versionCode = r11
            r11 = r67
            r3.usesStaticLibraries = r11
            r12 = r68
            r3.usesStaticLibrariesVersions = r12
            long r4 = r51.lastModified()
            r3.setTimeStamp(r4)
            r12 = r66
            r6 = r14
            goto L_0x0177
        L_0x005e:
            r13 = r46
            r10 = r51
            r9 = r53
            r6 = r54
            r7 = r59
            r8 = r64
            r11 = r67
            r12 = r68
            com.android.server.pm.PackageSetting r21 = new com.android.server.pm.PackageSetting
            r3 = r21
            r4 = 0
            r11 = r4
            r18 = 0
            r4 = r46
            r5 = r49
            r6 = r51
            r7 = r52
            r8 = r53
            r9 = r54
            r10 = r55
            r12 = r56
            r14 = r58
            r15 = r59
            r16 = r64
            r17 = r65
            r19 = r67
            r20 = r68
            r3.<init>(r4, r5, r6, r7, r8, r9, r10, r11, r12, r14, r15, r16, r17, r18, r19, r20)
            long r4 = r51.lastModified()
            r3.setTimeStamp(r4)
            r3.sharedUser = r2
            r4 = r58 & 1
            if (r4 != 0) goto L_0x0129
            java.util.List r4 = getAllUsers(r66)
            r5 = 0
            r6 = r60
            if (r6 == 0) goto L_0x00b0
            int r7 = r60.getIdentifier()
            goto L_0x00b1
        L_0x00b0:
            r7 = r5
        L_0x00b1:
            if (r4 == 0) goto L_0x0126
            if (r61 == 0) goto L_0x0126
            java.util.Iterator r8 = r4.iterator()
        L_0x00b9:
            boolean r9 = r8.hasNext()
            if (r9 == 0) goto L_0x0123
            java.lang.Object r9 = r8.next()
            android.content.pm.UserInfo r9 = (android.content.pm.UserInfo) r9
            r10 = 1
            if (r6 == 0) goto L_0x00df
            r11 = -1
            if (r7 != r11) goto L_0x00d6
            int r11 = r9.id
            r12 = r66
            boolean r11 = isAdbInstallDisallowed(r12, r11)
            if (r11 == 0) goto L_0x00dc
            goto L_0x00d8
        L_0x00d6:
            r12 = r66
        L_0x00d8:
            int r11 = r9.id
            if (r7 != r11) goto L_0x00dd
        L_0x00dc:
            goto L_0x00e1
        L_0x00dd:
            r11 = r5
            goto L_0x00e2
        L_0x00df:
            r12 = r66
        L_0x00e1:
            r11 = r10
        L_0x00e2:
            int r13 = r9.id
            r24 = 0
            r26 = 0
            if (r11 == 0) goto L_0x00f5
            int r14 = r9.id
            boolean r14 = com.android.server.pm.SettingsInjector.shouldInstallInXSpace(r6, r14)
            if (r14 == 0) goto L_0x00f5
            r27 = r10
            goto L_0x00f7
        L_0x00f5:
            r27 = r5
        L_0x00f7:
            r28 = 1
            r29 = 1
            r30 = 0
            r31 = 0
            r32 = 0
            r33 = 0
            r34 = 0
            r35 = 0
            r36 = 0
            r39 = 0
            r40 = 0
            r41 = 0
            r42 = 0
            r43 = 0
            r44 = 0
            r45 = 0
            r22 = r3
            r23 = r13
            r37 = r62
            r38 = r63
            r22.setUserState(r23, r24, r26, r27, r28, r29, r30, r31, r32, r33, r34, r35, r36, r37, r38, r39, r40, r41, r42, r43, r44, r45)
            goto L_0x00b9
        L_0x0123:
            r12 = r66
            goto L_0x012d
        L_0x0126:
            r12 = r66
            goto L_0x012d
        L_0x0129:
            r6 = r60
            r12 = r66
        L_0x012d:
            if (r2 == 0) goto L_0x0134
            int r4 = r2.userId
            r3.appId = r4
            goto L_0x0177
        L_0x0134:
            if (r1 == 0) goto L_0x0177
            com.android.server.pm.PackageSignatures r4 = new com.android.server.pm.PackageSignatures
            com.android.server.pm.PackageSignatures r5 = r1.signatures
            r4.<init>((com.android.server.pm.PackageSignatures) r5)
            r3.signatures = r4
            int r4 = r1.appId
            r3.appId = r4
            com.android.server.pm.permission.PermissionsState r4 = r3.getPermissionsState()
            com.android.server.pm.permission.PermissionsState r5 = r48.getPermissionsState()
            r4.copyFrom(r5)
            java.util.List r4 = getAllUsers(r66)
            if (r4 == 0) goto L_0x0177
            java.util.Iterator r5 = r4.iterator()
        L_0x0158:
            boolean r7 = r5.hasNext()
            if (r7 == 0) goto L_0x0177
            java.lang.Object r7 = r5.next()
            android.content.pm.UserInfo r7 = (android.content.pm.UserInfo) r7
            int r8 = r7.id
            android.util.ArraySet r9 = r1.getDisabledComponents(r8)
            r3.setDisabledComponentsCopy(r9, r8)
            android.util.ArraySet r9 = r1.getEnabledComponents(r8)
            r3.setEnabledComponentsCopy(r9, r8)
            goto L_0x0158
        L_0x0177:
            return r3
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.pm.Settings.createNewSetting(java.lang.String, com.android.server.pm.PackageSetting, com.android.server.pm.PackageSetting, java.lang.String, com.android.server.pm.SharedUserSetting, java.io.File, java.io.File, java.lang.String, java.lang.String, java.lang.String, long, int, int, android.os.UserHandle, boolean, boolean, boolean, java.lang.String, java.util.List, com.android.server.pm.UserManagerService, java.lang.String[], long[]):com.android.server.pm.PackageSetting");
    }

    static void updatePackageSetting(PackageSetting pkgSetting, PackageSetting disabledPkg, SharedUserSetting sharedUser, File codePath, File resourcePath, String legacyNativeLibraryPath, String primaryCpuAbi, String secondaryCpuAbi, int pkgFlags, int pkgPrivateFlags, List<String> childPkgNames, UserManagerService userManager, String[] usesStaticLibraries, long[] usesStaticLibrariesVersions) throws PackageManagerException {
        String str;
        PackageSetting packageSetting = pkgSetting;
        SharedUserSetting sharedUserSetting = sharedUser;
        File file = codePath;
        File file2 = resourcePath;
        List<String> list = childPkgNames;
        String[] strArr = usesStaticLibraries;
        long[] jArr = usesStaticLibrariesVersions;
        String pkgName = packageSetting.name;
        if (packageSetting.sharedUser != sharedUserSetting) {
            StringBuilder sb = new StringBuilder();
            sb.append("Package ");
            sb.append(pkgName);
            sb.append(" shared user changed from ");
            String str2 = "<nothing>";
            sb.append(packageSetting.sharedUser != null ? packageSetting.sharedUser.name : str2);
            sb.append(" to ");
            if (sharedUserSetting != null) {
                str2 = sharedUserSetting.name;
            }
            sb.append(str2);
            PackageManagerService.reportSettingsProblem(5, sb.toString());
            throw new PackageManagerException(-8, "Updating application package " + pkgName + " failed");
        }
        String str3 = " system";
        String str4 = "";
        if (!packageSetting.codePath.equals(file)) {
            boolean isSystem = pkgSetting.isSystem();
            StringBuilder sb2 = new StringBuilder();
            sb2.append("Update");
            str = str3;
            if (!isSystem) {
                str3 = str4;
            }
            sb2.append(str3);
            sb2.append(" package ");
            sb2.append(pkgName);
            sb2.append(" code path from ");
            sb2.append(packageSetting.codePathString);
            sb2.append(" to ");
            sb2.append(codePath.toString());
            sb2.append("; Retain data and using new");
            Slog.i("PackageManager", sb2.toString());
            if (!isSystem) {
                if ((pkgFlags & 1) == 0 || disabledPkg != null) {
                } else {
                    List<UserInfo> allUserInfos = getAllUsers(userManager);
                    if (allUserInfos != null) {
                        for (UserInfo userInfo : allUserInfos) {
                            List<UserInfo> allUserInfos2 = allUserInfos;
                            UserInfo userInfo2 = userInfo;
                            packageSetting.setInstalled(true, userInfo.id);
                            isSystem = isSystem;
                            allUserInfos = allUserInfos2;
                        }
                        boolean z = isSystem;
                    } else {
                        boolean z2 = isSystem;
                    }
                }
                packageSetting.legacyNativeLibraryPathString = legacyNativeLibraryPath;
            } else {
                String str5 = legacyNativeLibraryPath;
                boolean z3 = isSystem;
            }
            packageSetting.codePath = file;
            packageSetting.codePathString = codePath.toString();
        } else {
            String str6 = legacyNativeLibraryPath;
            str = str3;
        }
        if (!packageSetting.resourcePath.equals(file2)) {
            boolean isSystem2 = pkgSetting.isSystem();
            StringBuilder sb3 = new StringBuilder();
            sb3.append("Update");
            if (isSystem2) {
                str4 = str;
            }
            sb3.append(str4);
            sb3.append(" package ");
            sb3.append(pkgName);
            sb3.append(" resource path from ");
            sb3.append(packageSetting.resourcePathString);
            sb3.append(" to ");
            sb3.append(resourcePath.toString());
            sb3.append("; Retain data and using new");
            Slog.i("PackageManager", sb3.toString());
            packageSetting.resourcePath = file2;
            packageSetting.resourcePathString = resourcePath.toString();
        }
        packageSetting.pkgFlags &= -2;
        packageSetting.pkgPrivateFlags &= -1076756489;
        packageSetting.pkgFlags |= pkgFlags & 1;
        packageSetting.pkgPrivateFlags |= pkgPrivateFlags & 8;
        packageSetting.pkgPrivateFlags |= pkgPrivateFlags & 131072;
        packageSetting.pkgPrivateFlags |= pkgPrivateFlags & DumpState.DUMP_DOMAIN_PREFERRED;
        packageSetting.pkgPrivateFlags |= pkgPrivateFlags & DumpState.DUMP_FROZEN;
        packageSetting.pkgPrivateFlags |= pkgPrivateFlags & DumpState.DUMP_COMPILER_STATS;
        packageSetting.pkgPrivateFlags |= pkgPrivateFlags & 1073741824;
        packageSetting.primaryCpuAbiString = primaryCpuAbi;
        packageSetting.secondaryCpuAbiString = secondaryCpuAbi;
        if (list != null) {
            packageSetting.childPackageNames = new ArrayList(list);
        }
        if (strArr == null || jArr == null || strArr.length != jArr.length) {
            packageSetting.usesStaticLibraries = null;
            packageSetting.usesStaticLibrariesVersions = null;
            return;
        }
        packageSetting.usesStaticLibraries = strArr;
        packageSetting.usesStaticLibrariesVersions = jArr;
    }

    /* access modifiers changed from: package-private */
    public boolean registerAppIdLPw(PackageSetting p) throws PackageManagerException {
        boolean createdNew;
        if (p.appId == 0) {
            p.appId = acquireAndRegisterNewAppIdLPw(p);
            createdNew = true;
        } else {
            createdNew = registerExistingAppIdLPw(p.appId, p, p.name);
        }
        if (p.appId >= 0) {
            return createdNew;
        }
        PackageManagerService.reportSettingsProblem(5, "Package " + p.name + " could not be assigned a valid UID");
        throw new PackageManagerException(-4, "Package " + p.name + " could not be assigned a valid UID");
    }

    /* access modifiers changed from: package-private */
    public void writeUserRestrictionsLPw(PackageSetting newPackage, PackageSetting oldPackage) {
        List<UserInfo> allUsers;
        PackageUserState oldUserState;
        if (getPackageLPr(newPackage.name) != null && (allUsers = getAllUsers(UserManagerService.getInstance())) != null) {
            for (UserInfo user : allUsers) {
                if (oldPackage == null) {
                    oldUserState = PackageSettingBase.DEFAULT_USER_STATE;
                } else {
                    oldUserState = oldPackage.readUserState(user.id);
                }
                if (!oldUserState.equals(newPackage.readUserState(user.id))) {
                    writePackageRestrictionsLPr(user.id);
                }
            }
        }
    }

    static boolean isAdbInstallDisallowed(UserManagerService userManager, int userId) {
        return userManager.hasUserRestriction("no_debugging_features", userId);
    }

    /* access modifiers changed from: package-private */
    public void insertPackageSettingLPw(PackageSetting p, PackageParser.Package pkg) {
        if (p.signatures.mSigningDetails.signatures == null) {
            p.signatures.mSigningDetails = pkg.mSigningDetails;
        }
        if (p.sharedUser != null && p.sharedUser.signatures.mSigningDetails.signatures == null) {
            p.sharedUser.signatures.mSigningDetails = pkg.mSigningDetails;
        }
        addPackageSettingLPw(p, p.sharedUser);
    }

    private void addPackageSettingLPw(PackageSetting p, SharedUserSetting sharedUser) {
        this.mPackages.put(p.name, p);
        if (sharedUser != null) {
            if (p.sharedUser != null && p.sharedUser != sharedUser) {
                PackageManagerService.reportSettingsProblem(6, "Package " + p.name + " was user " + p.sharedUser + " but is now " + sharedUser + "; I am not changing its files so it will probably fail!");
                p.sharedUser.removePackage(p);
            } else if (p.appId != sharedUser.userId) {
                PackageManagerService.reportSettingsProblem(6, "Package " + p.name + " was user id " + p.appId + " but is now user " + sharedUser + " with id " + sharedUser.userId + "; I am not changing its files so it will probably fail!");
            }
            sharedUser.addPackage(p);
            p.sharedUser = sharedUser;
            p.appId = sharedUser.userId;
        }
        Object userIdPs = getSettingLPr(p.appId);
        if (sharedUser == null) {
            if (!(userIdPs == null || userIdPs == p)) {
                replaceAppIdLPw(p.appId, p);
            }
        } else if (!(userIdPs == null || userIdPs == sharedUser)) {
            replaceAppIdLPw(p.appId, sharedUser);
        }
        IntentFilterVerificationInfo ivi = this.mRestoredIntentFilterVerifications.get(p.name);
        if (ivi != null) {
            this.mRestoredIntentFilterVerifications.remove(p.name);
            p.setIntentFilterVerificationInfo(ivi);
        }
    }

    /* access modifiers changed from: package-private */
    public int updateSharedUserPermsLPw(PackageSetting deletedPs, int userId) {
        if (deletedPs == null || deletedPs.pkg == null) {
            Slog.i("PackageManager", "Trying to update info for null package. Just ignoring");
            return ScreenRotationAnimationInjector.BLACK_SURFACE_INVALID_POSITION;
        } else if (deletedPs.sharedUser == null) {
            return ScreenRotationAnimationInjector.BLACK_SURFACE_INVALID_POSITION;
        } else {
            SharedUserSetting sus = deletedPs.sharedUser;
            int affectedUserId = ScreenRotationAnimationInjector.BLACK_SURFACE_INVALID_POSITION;
            Iterator it = deletedPs.pkg.requestedPermissions.iterator();
            while (it.hasNext()) {
                String eachPerm = (String) it.next();
                BasePermission bp = this.mPermissions.getPermission(eachPerm);
                if (bp != null) {
                    boolean used = false;
                    Iterator<PackageSetting> it2 = sus.packages.iterator();
                    while (true) {
                        if (!it2.hasNext()) {
                            break;
                        }
                        PackageSetting pkg = it2.next();
                        if (pkg.pkg != null && !pkg.pkg.packageName.equals(deletedPs.pkg.packageName) && pkg.pkg.requestedPermissions.contains(eachPerm)) {
                            used = true;
                            break;
                        }
                    }
                    if (!used) {
                        PermissionsState permissionsState = sus.getPermissionsState();
                        PackageSetting disabledPs = getDisabledSystemPkgLPr(deletedPs.pkg.packageName);
                        if (!(disabledPs == null || disabledPs.pkg == null)) {
                            boolean reqByDisabledSysPkg = false;
                            Iterator it3 = disabledPs.pkg.requestedPermissions.iterator();
                            while (true) {
                                if (it3.hasNext()) {
                                    if (((String) it3.next()).equals(eachPerm)) {
                                        reqByDisabledSysPkg = true;
                                        break;
                                    }
                                } else {
                                    break;
                                }
                            }
                            if (reqByDisabledSysPkg) {
                            }
                        }
                        permissionsState.updatePermissionFlags(bp, userId, 64511, 0);
                        if (permissionsState.revokeInstallPermission(bp) == 1) {
                            affectedUserId = -1;
                        }
                        if (permissionsState.revokeRuntimePermission(bp, userId) == 1) {
                            if (affectedUserId == -10000) {
                                affectedUserId = userId;
                            } else if (affectedUserId != userId) {
                                affectedUserId = -1;
                            }
                        }
                    }
                }
            }
            return affectedUserId;
        }
    }

    /* access modifiers changed from: package-private */
    public int removePackageLPw(String name) {
        PackageSetting p = this.mPackages.get(name);
        if (p == null) {
            return -1;
        }
        this.mPackages.remove(name);
        removeInstallerPackageStatus(name);
        if (p.sharedUser != null) {
            p.sharedUser.removePackage(p);
            if (p.sharedUser.packages.size() != 0) {
                return -1;
            }
            PackageSetting disabledPs = getDisabledSystemPkgLPr(name);
            if (disabledPs != null && disabledPs.sharedUser != null && disabledPs.sharedUser.name.equals(p.sharedUser.name)) {
                return -1;
            }
            this.mSharedUsers.remove(p.sharedUser.name);
            removeAppIdLPw(p.sharedUser.userId);
            return p.sharedUser.userId;
        }
        removeAppIdLPw(p.appId);
        return p.appId;
    }

    private void removeInstallerPackageStatus(String packageName) {
        if (this.mInstallerPackages.contains(packageName)) {
            for (int i = 0; i < this.mPackages.size(); i++) {
                PackageSetting ps = this.mPackages.valueAt(i);
                String installerPackageName = ps.getInstallerPackageName();
                if (installerPackageName != null && installerPackageName.equals(packageName)) {
                    ps.setInstallerPackageName((String) null);
                    ps.isOrphaned = true;
                }
            }
            this.mInstallerPackages.remove(packageName);
        }
    }

    private boolean registerExistingAppIdLPw(int appId, SettingBase obj, Object name) {
        if (appId > 19999) {
            return false;
        }
        if (appId >= 10000) {
            int index = appId + ScreenRotationAnimationInjector.BLACK_SURFACE_INVALID_POSITION;
            for (int size = this.mAppIds.size(); index >= size; size++) {
                this.mAppIds.add((Object) null);
            }
            if (this.mAppIds.get(index) != null) {
                PackageManagerService.reportSettingsProblem(6, "Adding duplicate app id: " + appId + " name=" + name);
                return false;
            }
            this.mAppIds.set(index, obj);
            return true;
        } else if (this.mOtherAppIds.get(appId) != null) {
            PackageManagerService.reportSettingsProblem(6, "Adding duplicate shared id: " + appId + " name=" + name);
            return false;
        } else {
            this.mOtherAppIds.put(appId, obj);
            return true;
        }
    }

    public SettingBase getSettingLPr(int appId) {
        if (appId < 10000) {
            return this.mOtherAppIds.get(appId);
        }
        int size = this.mAppIds.size();
        int index = appId + ScreenRotationAnimationInjector.BLACK_SURFACE_INVALID_POSITION;
        if (index < size) {
            return this.mAppIds.get(index);
        }
        return null;
    }

    /* access modifiers changed from: package-private */
    public void removeAppIdLPw(int appId) {
        if (appId >= 10000) {
            int size = this.mAppIds.size();
            int index = appId + ScreenRotationAnimationInjector.BLACK_SURFACE_INVALID_POSITION;
            if (index < size) {
                this.mAppIds.set(index, (Object) null);
            }
        } else {
            this.mOtherAppIds.remove(appId);
        }
        setFirstAvailableUid(appId + 1);
    }

    private void replaceAppIdLPw(int appId, SettingBase obj) {
        if (appId >= 10000) {
            int size = this.mAppIds.size();
            int index = appId + ScreenRotationAnimationInjector.BLACK_SURFACE_INVALID_POSITION;
            if (index < size) {
                this.mAppIds.set(index, obj);
                return;
            }
            return;
        }
        this.mOtherAppIds.put(appId, obj);
    }

    /* access modifiers changed from: package-private */
    public PreferredIntentResolver editPreferredActivitiesLPw(int userId) {
        PreferredIntentResolver pir = this.mPreferredActivities.get(userId);
        if (pir != null) {
            return pir;
        }
        PreferredIntentResolver pir2 = new PreferredIntentResolver();
        this.mPreferredActivities.put(userId, pir2);
        return pir2;
    }

    /* access modifiers changed from: package-private */
    public PersistentPreferredIntentResolver editPersistentPreferredActivitiesLPw(int userId) {
        PersistentPreferredIntentResolver ppir = this.mPersistentPreferredActivities.get(userId);
        if (ppir != null) {
            return ppir;
        }
        PersistentPreferredIntentResolver ppir2 = new PersistentPreferredIntentResolver();
        this.mPersistentPreferredActivities.put(userId, ppir2);
        return ppir2;
    }

    /* access modifiers changed from: package-private */
    public CrossProfileIntentResolver editCrossProfileIntentResolverLPw(int userId) {
        CrossProfileIntentResolver cpir = this.mCrossProfileIntentResolvers.get(userId);
        if (cpir != null) {
            return cpir;
        }
        CrossProfileIntentResolver cpir2 = new CrossProfileIntentResolver();
        this.mCrossProfileIntentResolvers.put(userId, cpir2);
        return cpir2;
    }

    /* access modifiers changed from: package-private */
    public IntentFilterVerificationInfo getIntentFilterVerificationLPr(String packageName) {
        PackageSetting ps = this.mPackages.get(packageName);
        if (ps == null) {
            return null;
        }
        return ps.getIntentFilterVerificationInfo();
    }

    /* access modifiers changed from: package-private */
    public IntentFilterVerificationInfo createIntentFilterVerificationIfNeededLPw(String packageName, ArraySet<String> domains) {
        PackageSetting ps = this.mPackages.get(packageName);
        if (ps == null) {
            return null;
        }
        IntentFilterVerificationInfo ivi = ps.getIntentFilterVerificationInfo();
        if (ivi == null) {
            IntentFilterVerificationInfo ivi2 = new IntentFilterVerificationInfo(packageName, domains);
            ps.setIntentFilterVerificationInfo(ivi2);
            return ivi2;
        }
        ivi.setDomains(domains);
        return ivi;
    }

    /* access modifiers changed from: package-private */
    public int getIntentFilterVerificationStatusLPr(String packageName, int userId) {
        PackageSetting ps = this.mPackages.get(packageName);
        if (ps == null) {
            return 0;
        }
        return (int) (ps.getDomainVerificationStatusForUser(userId) >> 32);
    }

    /* access modifiers changed from: package-private */
    public boolean updateIntentFilterVerificationStatusLPw(String packageName, int status, int userId) {
        int alwaysGeneration;
        PackageSetting current = this.mPackages.get(packageName);
        if (current == null) {
            return false;
        }
        if (status == 2) {
            alwaysGeneration = this.mNextAppLinkGeneration.get(userId) + 1;
            this.mNextAppLinkGeneration.put(userId, alwaysGeneration);
        } else {
            alwaysGeneration = 0;
        }
        current.setDomainVerificationStatusForUser(status, alwaysGeneration, userId);
        return true;
    }

    /* access modifiers changed from: package-private */
    public List<IntentFilterVerificationInfo> getIntentFilterVerificationsLPr(String packageName) {
        if (packageName == null) {
            return Collections.emptyList();
        }
        ArrayList<IntentFilterVerificationInfo> result = new ArrayList<>();
        for (PackageSetting ps : this.mPackages.values()) {
            IntentFilterVerificationInfo ivi = ps.getIntentFilterVerificationInfo();
            if (ivi != null && !TextUtils.isEmpty(ivi.getPackageName()) && ivi.getPackageName().equalsIgnoreCase(packageName)) {
                result.add(ivi);
            }
        }
        return result;
    }

    /* access modifiers changed from: package-private */
    public boolean removeIntentFilterVerificationLPw(String packageName, int userId) {
        PackageSetting ps = this.mPackages.get(packageName);
        if (ps == null) {
            return false;
        }
        ps.clearDomainVerificationStatusForUser(userId);
        return true;
    }

    /* access modifiers changed from: package-private */
    public boolean removeIntentFilterVerificationLPw(String packageName, int[] userIds) {
        boolean result = false;
        for (int userId : userIds) {
            result |= removeIntentFilterVerificationLPw(packageName, userId);
        }
        return result;
    }

    /* access modifiers changed from: package-private */
    public String removeDefaultBrowserPackageNameLPw(int userId) {
        if (userId == -1) {
            return null;
        }
        return (String) this.mDefaultBrowserApp.removeReturnOld(userId);
    }

    private File getUserPackagesStateFile(int userId) {
        return new File(new File(new File(this.mSystemDir, DatabaseHelper.SoundModelContract.KEY_USERS), Integer.toString(userId)), "package-restrictions.xml");
    }

    /* access modifiers changed from: private */
    public File getUserRuntimePermissionsFile(int userId) {
        return new File(new File(new File(this.mSystemDir, DatabaseHelper.SoundModelContract.KEY_USERS), Integer.toString(userId)), RUNTIME_PERMISSIONS_FILE_NAME);
    }

    private File getUserPackagesStateBackupFile(int userId) {
        return new File(Environment.getUserSystemDirectory(userId), "package-restrictions-backup.xml");
    }

    /* access modifiers changed from: package-private */
    public void writeAllUsersPackageRestrictionsLPr() {
        List<UserInfo> users = getAllUsers(UserManagerService.getInstance());
        if (users != null) {
            for (UserInfo user : users) {
                writePackageRestrictionsLPr(user.id);
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void writeAllRuntimePermissionsLPr() {
        for (int userId : UserManagerService.getInstance().getUserIds()) {
            this.mRuntimePermissionsPersistence.writePermissionsForUserAsyncLPr(userId);
        }
    }

    /* access modifiers changed from: package-private */
    public boolean areDefaultRuntimePermissionsGrantedLPr(int userId) {
        return this.mRuntimePermissionsPersistence.areDefaultRuntimePermissionsGrantedLPr(userId);
    }

    /* access modifiers changed from: package-private */
    public void setRuntimePermissionsFingerPrintLPr(String fingerPrint, int userId) {
        this.mRuntimePermissionsPersistence.setRuntimePermissionsFingerPrintLPr(fingerPrint, userId);
    }

    /* access modifiers changed from: package-private */
    public int getDefaultRuntimePermissionsVersionLPr(int userId) {
        return this.mRuntimePermissionsPersistence.getVersionLPr(userId);
    }

    /* access modifiers changed from: package-private */
    public void setDefaultRuntimePermissionsVersionLPr(int version, int userId) {
        this.mRuntimePermissionsPersistence.setVersionLPr(version, userId);
    }

    public VersionInfo findOrCreateVersion(String volumeUuid) {
        VersionInfo ver = this.mVersion.get(volumeUuid);
        if (ver != null) {
            return ver;
        }
        VersionInfo ver2 = new VersionInfo();
        this.mVersion.put(volumeUuid, ver2);
        return ver2;
    }

    public VersionInfo getInternalVersion() {
        return this.mVersion.get(StorageManager.UUID_PRIVATE_INTERNAL);
    }

    public VersionInfo getExternalVersion() {
        return this.mVersion.get("primary_physical");
    }

    public void onVolumeForgotten(String fsUuid) {
        this.mVersion.remove(fsUuid);
    }

    /* access modifiers changed from: package-private */
    public void readPreferredActivitiesLPw(XmlPullParser parser, int userId) throws XmlPullParserException, IOException {
        int outerDepth = parser.getDepth();
        while (true) {
            int next = parser.next();
            int type = next;
            if (next == 1) {
                return;
            }
            if (type == 3 && parser.getDepth() <= outerDepth) {
                return;
            }
            if (!(type == 3 || type == 4)) {
                if (parser.getName().equals(TAG_ITEM)) {
                    PreferredActivity pa = new PreferredActivity(parser);
                    if (pa.mPref.getParseError() == null) {
                        editPreferredActivitiesLPw(userId).addFilter(pa);
                    } else {
                        PackageManagerService.reportSettingsProblem(5, "Error in package manager settings: <preferred-activity> " + pa.mPref.getParseError() + " at " + parser.getPositionDescription());
                    }
                } else {
                    PackageManagerService.reportSettingsProblem(5, "Unknown element under <preferred-activities>: " + parser.getName());
                    XmlUtils.skipCurrentTag(parser);
                }
            }
        }
    }

    private void readPersistentPreferredActivitiesLPw(XmlPullParser parser, int userId) throws XmlPullParserException, IOException {
        int outerDepth = parser.getDepth();
        while (true) {
            int next = parser.next();
            int type = next;
            if (next == 1) {
                return;
            }
            if (type == 3 && parser.getDepth() <= outerDepth) {
                return;
            }
            if (!(type == 3 || type == 4)) {
                if (parser.getName().equals(TAG_ITEM)) {
                    editPersistentPreferredActivitiesLPw(userId).addFilter(new PersistentPreferredActivity(parser));
                } else {
                    PackageManagerService.reportSettingsProblem(5, "Unknown element under <persistent-preferred-activities>: " + parser.getName());
                    XmlUtils.skipCurrentTag(parser);
                }
            }
        }
    }

    private void readCrossProfileIntentFiltersLPw(XmlPullParser parser, int userId) throws XmlPullParserException, IOException {
        int outerDepth = parser.getDepth();
        while (true) {
            int next = parser.next();
            int type = next;
            if (next == 1) {
                return;
            }
            if (type == 3 && parser.getDepth() <= outerDepth) {
                return;
            }
            if (!(type == 3 || type == 4)) {
                String tagName = parser.getName();
                if (tagName.equals(TAG_ITEM)) {
                    editCrossProfileIntentResolverLPw(userId).addFilter(new CrossProfileIntentFilter(parser));
                } else {
                    PackageManagerService.reportSettingsProblem(5, "Unknown element under crossProfile-intent-filters: " + tagName);
                    XmlUtils.skipCurrentTag(parser);
                }
            }
        }
    }

    private void readDomainVerificationLPw(XmlPullParser parser, PackageSettingBase packageSetting) throws XmlPullParserException, IOException {
        packageSetting.setIntentFilterVerificationInfo(new IntentFilterVerificationInfo(parser));
    }

    private void readRestoredIntentFilterVerifications(XmlPullParser parser) throws XmlPullParserException, IOException {
        int outerDepth = parser.getDepth();
        while (true) {
            int next = parser.next();
            int type = next;
            if (next == 1) {
                return;
            }
            if (type == 3 && parser.getDepth() <= outerDepth) {
                return;
            }
            if (!(type == 3 || type == 4)) {
                String tagName = parser.getName();
                if (tagName.equals(TAG_DOMAIN_VERIFICATION)) {
                    IntentFilterVerificationInfo ivi = new IntentFilterVerificationInfo(parser);
                    this.mRestoredIntentFilterVerifications.put(ivi.getPackageName(), ivi);
                } else {
                    Slog.w(TAG, "Unknown element: " + tagName);
                    XmlUtils.skipCurrentTag(parser);
                }
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void readDefaultAppsLPw(XmlPullParser parser, int userId) throws XmlPullParserException, IOException {
        int outerDepth = parser.getDepth();
        while (true) {
            int next = parser.next();
            int type = next;
            if (next == 1) {
                return;
            }
            if (type == 3 && parser.getDepth() <= outerDepth) {
                return;
            }
            if (!(type == 3 || type == 4)) {
                String tagName = parser.getName();
                if (tagName.equals(TAG_DEFAULT_BROWSER)) {
                    this.mDefaultBrowserApp.put(userId, parser.getAttributeValue((String) null, ATTR_PACKAGE_NAME));
                } else if (!tagName.equals(TAG_DEFAULT_DIALER)) {
                    PackageManagerService.reportSettingsProblem(5, "Unknown element under default-apps: " + parser.getName());
                    XmlUtils.skipCurrentTag(parser);
                }
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void readBlockUninstallPackagesLPw(XmlPullParser parser, int userId) throws XmlPullParserException, IOException {
        int outerDepth = parser.getDepth();
        ArraySet<String> packages = new ArraySet<>();
        while (true) {
            int next = parser.next();
            int type = next;
            if (next != 1 && (type != 3 || parser.getDepth() > outerDepth)) {
                if (!(type == 3 || type == 4)) {
                    if (parser.getName().equals(TAG_BLOCK_UNINSTALL)) {
                        packages.add(parser.getAttributeValue((String) null, ATTR_PACKAGE_NAME));
                    } else {
                        PackageManagerService.reportSettingsProblem(5, "Unknown element under block-uninstall-packages: " + parser.getName());
                        XmlUtils.skipCurrentTag(parser);
                    }
                }
            }
        }
        if (packages.isEmpty()) {
            this.mBlockUninstallPackages.remove(userId);
        } else {
            this.mBlockUninstallPackages.put(userId, packages);
        }
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v0, resolved type: com.android.server.pm.Settings} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v1, resolved type: com.android.server.pm.Settings} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v2, resolved type: com.android.server.pm.Settings} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v3, resolved type: com.android.server.pm.Settings} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v4, resolved type: com.android.server.pm.Settings} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v5, resolved type: com.android.server.pm.Settings} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v6, resolved type: com.android.server.pm.Settings} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v7, resolved type: com.android.server.pm.Settings} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v8, resolved type: com.android.server.pm.Settings} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v9, resolved type: com.android.server.pm.Settings} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v10, resolved type: com.android.server.pm.Settings} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v11, resolved type: com.android.server.pm.Settings} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v12, resolved type: com.android.server.pm.Settings} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v13, resolved type: com.android.server.pm.Settings} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v14, resolved type: com.android.server.pm.Settings} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v15, resolved type: com.android.server.pm.Settings} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v16, resolved type: com.android.server.pm.Settings} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v17, resolved type: com.android.server.pm.Settings} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v18, resolved type: com.android.server.pm.Settings} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v19, resolved type: com.android.server.pm.Settings} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v20, resolved type: com.android.server.pm.Settings} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v21, resolved type: com.android.server.pm.Settings} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v22, resolved type: com.android.server.pm.Settings} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v23, resolved type: com.android.server.pm.Settings} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v24, resolved type: com.android.server.pm.Settings} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v25, resolved type: com.android.server.pm.Settings} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v26, resolved type: com.android.server.pm.Settings} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v27, resolved type: com.android.server.pm.Settings} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v28, resolved type: com.android.server.pm.Settings} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v29, resolved type: com.android.server.pm.Settings} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v31, resolved type: com.android.server.pm.Settings} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v32, resolved type: com.android.server.pm.Settings} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v33, resolved type: com.android.server.pm.Settings} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v34, resolved type: com.android.server.pm.Settings} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v35, resolved type: com.android.server.pm.Settings} */
    /* JADX WARNING: type inference failed for: r3v15, types: [int] */
    /* JADX WARNING: type inference failed for: r3v16, types: [int] */
    /*  JADX ERROR: NullPointerException in pass: CodeShrinkVisitor
        java.lang.NullPointerException
        	at jadx.core.dex.instructions.args.InsnArg.wrapInstruction(InsnArg.java:118)
        	at jadx.core.dex.visitors.shrink.CodeShrinkVisitor.inline(CodeShrinkVisitor.java:146)
        	at jadx.core.dex.visitors.shrink.CodeShrinkVisitor.shrinkBlock(CodeShrinkVisitor.java:71)
        	at jadx.core.dex.visitors.shrink.CodeShrinkVisitor.shrinkMethod(CodeShrinkVisitor.java:43)
        	at jadx.core.dex.visitors.shrink.CodeShrinkVisitor.visit(CodeShrinkVisitor.java:35)
        */
    /* JADX WARNING: Multi-variable type inference failed */
    void readPackageRestrictionsLPr(int r58) {
        /*
            r57 = this;
            r1 = r57
            r3 = r58
            java.lang.String r15 = "pkg"
            r2 = 0
            java.io.File r14 = r57.getUserPackagesStateFile(r58)
            java.io.File r13 = r57.getUserPackagesStateBackupFile(r58)
            boolean r0 = r13.exists()
            java.lang.String r12 = "PackageManager"
            r6 = 4
            if (r0 == 0) goto L_0x004f
            java.io.FileInputStream r0 = new java.io.FileInputStream     // Catch:{ IOException -> 0x004b }
            r0.<init>(r13)     // Catch:{ IOException -> 0x004b }
            r2 = r0
            java.lang.StringBuilder r0 = r1.mReadMessages     // Catch:{ IOException -> 0x004b }
            java.lang.String r4 = "Reading from backup stopped packages file\n"
            r0.append(r4)     // Catch:{ IOException -> 0x004b }
            java.lang.String r0 = "Need to read from backup stopped packages file"
            com.android.server.pm.PackageManagerService.reportSettingsProblem(r6, r0)     // Catch:{ IOException -> 0x004b }
            boolean r0 = r14.exists()     // Catch:{ IOException -> 0x004b }
            if (r0 == 0) goto L_0x0048
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ IOException -> 0x004b }
            r0.<init>()     // Catch:{ IOException -> 0x004b }
            java.lang.String r4 = "Cleaning up stopped packages file "
            r0.append(r4)     // Catch:{ IOException -> 0x004b }
            r0.append(r14)     // Catch:{ IOException -> 0x004b }
            java.lang.String r0 = r0.toString()     // Catch:{ IOException -> 0x004b }
            android.util.Slog.w(r12, r0)     // Catch:{ IOException -> 0x004b }
            r14.delete()     // Catch:{ IOException -> 0x004b }
        L_0x0048:
            r26 = r2
            goto L_0x0051
        L_0x004b:
            r0 = move-exception
            r26 = r2
            goto L_0x0051
        L_0x004f:
            r26 = r2
        L_0x0051:
            java.lang.String r11 = "Error reading package manager stopped packages"
            r10 = 6
            java.lang.String r9 = "Error reading: "
            if (r26 != 0) goto L_0x0162
            boolean r0 = r14.exists()     // Catch:{ XmlPullParserException -> 0x0153, IOException -> 0x0143 }
            if (r0 != 0) goto L_0x00f9
            java.lang.StringBuilder r0 = r1.mReadMessages     // Catch:{ XmlPullParserException -> 0x00ea, IOException -> 0x00da }
            java.lang.String r2 = "No stopped packages file found\n"
            r0.append(r2)     // Catch:{ XmlPullParserException -> 0x00ea, IOException -> 0x00da }
            java.lang.String r0 = "No stopped packages file; assuming all started"
            com.android.server.pm.PackageManagerService.reportSettingsProblem(r6, r0)     // Catch:{ XmlPullParserException -> 0x00ea, IOException -> 0x00da }
            android.util.ArrayMap<java.lang.String, com.android.server.pm.PackageSetting> r0 = r1.mPackages     // Catch:{ XmlPullParserException -> 0x00ea, IOException -> 0x00da }
            java.util.Collection r0 = r0.values()     // Catch:{ XmlPullParserException -> 0x00ea, IOException -> 0x00da }
            java.util.Iterator r0 = r0.iterator()     // Catch:{ XmlPullParserException -> 0x00ea, IOException -> 0x00da }
        L_0x0074:
            boolean r2 = r0.hasNext()     // Catch:{ XmlPullParserException -> 0x00ea, IOException -> 0x00da }
            if (r2 == 0) goto L_0x00d9
            java.lang.Object r2 = r0.next()     // Catch:{ XmlPullParserException -> 0x00ea, IOException -> 0x00da }
            com.android.server.pm.PackageSetting r2 = (com.android.server.pm.PackageSetting) r2     // Catch:{ XmlPullParserException -> 0x00ea, IOException -> 0x00da }
            r4 = 0
            r6 = 0
            r7 = 1
            r8 = 0
            r15 = 0
            r27 = r9
            r9 = r15
            r10 = r15
            r28 = r11
            r11 = r15
            r29 = r12
            r12 = r15
            r15 = 0
            r30 = r13
            r13 = r15
            r31 = r14
            r14 = r15
            r16 = 0
            r17 = 0
            r18 = 0
            r19 = 0
            r20 = 0
            r21 = 0
            r22 = 0
            r23 = 0
            r24 = 0
            r25 = 0
            r3 = r58
            r2.setUserState(r3, r4, r6, r7, r8, r9, r10, r11, r12, r13, r14, r15, r16, r17, r18, r19, r20, r21, r22, r23, r24, r25)     // Catch:{ XmlPullParserException -> 0x00c9, IOException -> 0x00be }
            r3 = r58
            r9 = r27
            r11 = r28
            r12 = r29
            r13 = r30
            r14 = r31
            r10 = 6
            goto L_0x0074
        L_0x00be:
            r0 = move-exception
            r3 = r58
            r13 = r26
            r7 = r29
            r55 = r31
            goto L_0x066e
        L_0x00c9:
            r0 = move-exception
            r3 = r58
            r13 = r26
            r5 = r27
            r2 = r28
            r7 = r29
            r55 = r31
            r4 = 6
            goto L_0x06b1
        L_0x00d9:
            return
        L_0x00da:
            r0 = move-exception
            r27 = r9
            r28 = r11
            r30 = r13
            r3 = r58
            r7 = r12
            r55 = r14
            r13 = r26
            goto L_0x066e
        L_0x00ea:
            r0 = move-exception
            r30 = r13
            r3 = r58
            r5 = r9
            r4 = r10
            r2 = r11
            r7 = r12
            r55 = r14
            r13 = r26
            goto L_0x06b1
        L_0x00f9:
            r27 = r9
            r28 = r11
            r29 = r12
            r30 = r13
            r31 = r14
            java.io.FileInputStream r0 = new java.io.FileInputStream     // Catch:{ XmlPullParserException -> 0x0133, IOException -> 0x0128 }
            r14 = r31
            r0.<init>(r14)     // Catch:{ XmlPullParserException -> 0x0118, IOException -> 0x010d }
            r13 = r0
            goto L_0x016c
        L_0x010d:
            r0 = move-exception
            r3 = r58
            r55 = r14
            r13 = r26
            r7 = r29
            goto L_0x066e
        L_0x0118:
            r0 = move-exception
            r3 = r58
            r55 = r14
            r13 = r26
            r5 = r27
            r2 = r28
            r7 = r29
            r4 = 6
            goto L_0x06b1
        L_0x0128:
            r0 = move-exception
            r3 = r58
            r13 = r26
            r7 = r29
            r55 = r31
            goto L_0x066e
        L_0x0133:
            r0 = move-exception
            r3 = r58
            r13 = r26
            r5 = r27
            r2 = r28
            r7 = r29
            r55 = r31
            r4 = 6
            goto L_0x06b1
        L_0x0143:
            r0 = move-exception
            r27 = r9
            r28 = r11
            r30 = r13
            r3 = r58
            r7 = r12
            r55 = r14
            r13 = r26
            goto L_0x066e
        L_0x0153:
            r0 = move-exception
            r30 = r13
            r3 = r58
            r5 = r9
            r4 = r10
            r2 = r11
            r7 = r12
            r55 = r14
            r13 = r26
            goto L_0x06b1
        L_0x0162:
            r27 = r9
            r28 = r11
            r29 = r12
            r30 = r13
            r13 = r26
        L_0x016c:
            org.xmlpull.v1.XmlPullParser r0 = android.util.Xml.newPullParser()     // Catch:{ XmlPullParserException -> 0x06a3, IOException -> 0x0665 }
            java.nio.charset.Charset r2 = java.nio.charset.StandardCharsets.UTF_8     // Catch:{ XmlPullParserException -> 0x0657, IOException -> 0x0665 }
            java.lang.String r2 = r2.name()     // Catch:{ XmlPullParserException -> 0x0657, IOException -> 0x0665 }
            r0.setInput(r13, r2)     // Catch:{ XmlPullParserException -> 0x0657, IOException -> 0x0665 }
        L_0x0179:
            int r2 = r0.next()     // Catch:{ XmlPullParserException -> 0x0657, IOException -> 0x0665 }
            r3 = r2
            r12 = 2
            r11 = 1
            if (r2 == r12) goto L_0x0185
            if (r3 == r11) goto L_0x0185
            goto L_0x0179
        L_0x0185:
            if (r3 == r12) goto L_0x01ac
            java.lang.StringBuilder r2 = r1.mReadMessages     // Catch:{ XmlPullParserException -> 0x019e, IOException -> 0x0195 }
            java.lang.String r4 = "No start tag found in package restrictions file\n"
            r2.append(r4)     // Catch:{ XmlPullParserException -> 0x019e, IOException -> 0x0195 }
            r2 = 5
            java.lang.String r4 = "No start tag found in package manager stopped packages"
            com.android.server.pm.PackageManagerService.reportSettingsProblem(r2, r4)     // Catch:{ XmlPullParserException -> 0x019e, IOException -> 0x0195 }
            return
        L_0x0195:
            r0 = move-exception
            r3 = r58
            r55 = r14
            r7 = r29
            goto L_0x066e
        L_0x019e:
            r0 = move-exception
            r3 = r58
            r55 = r14
            r5 = r27
            r2 = r28
            r7 = r29
            r4 = 6
            goto L_0x06b1
        L_0x01ac:
            r2 = 0
            int r4 = r0.getDepth()     // Catch:{ XmlPullParserException -> 0x0657, IOException -> 0x0665 }
            r10 = r4
            r9 = 0
            r4 = r9
        L_0x01b4:
            int r5 = r0.next()     // Catch:{ XmlPullParserException -> 0x0657, IOException -> 0x0665 }
            r3 = r5
            if (r5 == r11) goto L_0x0630
            r8 = 3
            if (r3 != r8) goto L_0x01d3
            int r5 = r0.getDepth()     // Catch:{ XmlPullParserException -> 0x019e, IOException -> 0x0195 }
            if (r5 <= r10) goto L_0x01c5
            goto L_0x01d3
        L_0x01c5:
            r31 = r3
            r36 = r10
            r54 = r13
            r55 = r14
            r7 = r29
            r3 = r58
            goto L_0x063c
        L_0x01d3:
            if (r3 == r8) goto L_0x0604
            if (r3 != r6) goto L_0x01ef
            r31 = r3
            r42 = r6
            r53 = r9
            r36 = r10
            r48 = r11
            r52 = r12
            r54 = r13
            r55 = r14
            r56 = r15
            r7 = r29
            r3 = r58
            goto L_0x061a
        L_0x01ef:
            java.lang.String r5 = r0.getName()     // Catch:{ XmlPullParserException -> 0x0657, IOException -> 0x0665 }
            r7 = r5
            boolean r5 = r7.equals(r15)     // Catch:{ XmlPullParserException -> 0x0657, IOException -> 0x0665 }
            if (r5 == 0) goto L_0x0535
            java.lang.String r5 = "name"
            java.lang.String r5 = r0.getAttributeValue(r9, r5)     // Catch:{ XmlPullParserException -> 0x052a, IOException -> 0x051f }
            android.util.ArrayMap<java.lang.String, com.android.server.pm.PackageSetting> r6 = r1.mPackages     // Catch:{ XmlPullParserException -> 0x052a, IOException -> 0x051f }
            java.lang.Object r6 = r6.get(r5)     // Catch:{ XmlPullParserException -> 0x052a, IOException -> 0x051f }
            com.android.server.pm.PackageSetting r6 = (com.android.server.pm.PackageSetting) r6     // Catch:{ XmlPullParserException -> 0x052a, IOException -> 0x051f }
            r26 = r6
            if (r26 != 0) goto L_0x0241
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ XmlPullParserException -> 0x019e, IOException -> 0x0195 }
            r4.<init>()     // Catch:{ XmlPullParserException -> 0x019e, IOException -> 0x0195 }
            java.lang.String r6 = "No package known for stopped package "
            r4.append(r6)     // Catch:{ XmlPullParserException -> 0x019e, IOException -> 0x0195 }
            r4.append(r5)     // Catch:{ XmlPullParserException -> 0x019e, IOException -> 0x0195 }
            java.lang.String r4 = r4.toString()     // Catch:{ XmlPullParserException -> 0x019e, IOException -> 0x0195 }
            r6 = r29
            android.util.Slog.w(r6, r4)     // Catch:{ XmlPullParserException -> 0x0234, IOException -> 0x022c }
            com.android.internal.util.XmlUtils.skipCurrentTag(r0)     // Catch:{ XmlPullParserException -> 0x0234, IOException -> 0x022c }
            r29 = r6
            r4 = r26
            r6 = 4
            goto L_0x01b4
        L_0x022c:
            r0 = move-exception
            r3 = r58
            r7 = r6
            r55 = r14
            goto L_0x066e
        L_0x0234:
            r0 = move-exception
            r3 = r58
            r7 = r6
            r55 = r14
            r5 = r27
            r2 = r28
            r4 = 6
            goto L_0x06b1
        L_0x0241:
            r6 = r29
            java.lang.String r4 = "ceDataInode"
            r8 = 0
            long r8 = com.android.internal.util.XmlUtils.readLongAttribute(r0, r4, r8)     // Catch:{ XmlPullParserException -> 0x0513, IOException -> 0x0507 }
            r32 = r5
            r4 = r8
            java.lang.String r8 = "inst"
            boolean r8 = com.android.internal.util.XmlUtils.readBooleanAttribute(r0, r8, r11)     // Catch:{ XmlPullParserException -> 0x0513, IOException -> 0x0507 }
            r9 = r7
            r7 = r8
            java.lang.String r8 = "stopped"
            r12 = 0
            boolean r8 = com.android.internal.util.XmlUtils.readBooleanAttribute(r0, r8, r12)     // Catch:{ XmlPullParserException -> 0x0513, IOException -> 0x0507 }
            java.lang.String r11 = "nl"
            boolean r11 = com.android.internal.util.XmlUtils.readBooleanAttribute(r0, r11, r12)     // Catch:{ XmlPullParserException -> 0x0513, IOException -> 0x0507 }
            r34 = r9
            r12 = 0
            r9 = r11
            java.lang.String r11 = "blocked"
            java.lang.String r11 = r0.getAttributeValue(r12, r11)     // Catch:{ XmlPullParserException -> 0x0513, IOException -> 0x0507 }
            r29 = r11
            if (r29 != 0) goto L_0x0276
            r11 = 0
            goto L_0x027a
        L_0x0276:
            boolean r11 = java.lang.Boolean.parseBoolean(r29)     // Catch:{ XmlPullParserException -> 0x0513, IOException -> 0x0507 }
        L_0x027a:
            java.lang.String r12 = "hidden"
            r31 = r3
            r3 = 0
            java.lang.String r12 = r0.getAttributeValue(r3, r12)     // Catch:{ XmlPullParserException -> 0x0513, IOException -> 0x0507 }
            r35 = r12
            if (r35 != 0) goto L_0x028a
            r3 = r11
            goto L_0x028e
        L_0x028a:
            boolean r3 = java.lang.Boolean.parseBoolean(r35)     // Catch:{ XmlPullParserException -> 0x0513, IOException -> 0x0507 }
        L_0x028e:
            r36 = r10
            r10 = r3
            java.lang.String r3 = "distraction_flags"
            r12 = 0
            int r11 = com.android.internal.util.XmlUtils.readIntAttribute(r0, r3, r12)     // Catch:{ XmlPullParserException -> 0x0513, IOException -> 0x0507 }
            r3 = 1
            java.lang.String r3 = "suspended"
            boolean r3 = com.android.internal.util.XmlUtils.readBooleanAttribute(r0, r3, r12)     // Catch:{ XmlPullParserException -> 0x0513, IOException -> 0x0507 }
            r38 = r3
            java.lang.String r3 = "suspending-package"
            r12 = 0
            java.lang.String r3 = r0.getAttributeValue(r12, r3)     // Catch:{ XmlPullParserException -> 0x0513, IOException -> 0x0507 }
            java.lang.String r12 = "suspend_dialog_message"
            r19 = r6
            r6 = 0
            java.lang.String r12 = r0.getAttributeValue(r6, r12)     // Catch:{ XmlPullParserException -> 0x04fa, IOException -> 0x04ed }
            if (r38 == 0) goto L_0x02d5
            if (r3 != 0) goto L_0x02d5
            java.lang.String r6 = "android"
            r3 = r6
            r39 = r3
            goto L_0x02d7
        L_0x02be:
            r0 = move-exception
            r3 = r58
            r55 = r14
            r7 = r19
            goto L_0x066e
        L_0x02c7:
            r0 = move-exception
            r3 = r58
            r55 = r14
            r7 = r19
            r5 = r27
            r2 = r28
            r4 = 6
            goto L_0x06b1
        L_0x02d5:
            r39 = r3
        L_0x02d7:
            java.lang.String r3 = "blockUninstall"
            r6 = 0
            boolean r3 = com.android.internal.util.XmlUtils.readBooleanAttribute(r0, r3, r6)     // Catch:{ XmlPullParserException -> 0x04fa, IOException -> 0x04ed }
            r40 = r3
            java.lang.String r3 = "instant-app"
            boolean r17 = com.android.internal.util.XmlUtils.readBooleanAttribute(r0, r3, r6)     // Catch:{ XmlPullParserException -> 0x04fa, IOException -> 0x04ed }
            java.lang.String r3 = "virtual-preload"
            boolean r18 = com.android.internal.util.XmlUtils.readBooleanAttribute(r0, r3, r6)     // Catch:{ XmlPullParserException -> 0x04fa, IOException -> 0x04ed }
            java.lang.String r3 = "enabled"
            int r3 = com.android.internal.util.XmlUtils.readIntAttribute(r0, r3, r6)     // Catch:{ XmlPullParserException -> 0x04fa, IOException -> 0x04ed }
            r42 = r4
            r41 = r19
            r4 = 4
            r6 = r3
            java.lang.String r3 = "enabledCaller"
            r5 = 0
            java.lang.String r19 = r0.getAttributeValue(r5, r3)     // Catch:{ XmlPullParserException -> 0x04dc, IOException -> 0x04cf }
            java.lang.String r3 = "harmful-app-warning"
            java.lang.String r25 = r0.getAttributeValue(r5, r3)     // Catch:{ XmlPullParserException -> 0x04dc, IOException -> 0x04cf }
            java.lang.String r3 = "domainVerificationStatus"
            r5 = 0
            int r22 = com.android.internal.util.XmlUtils.readIntAttribute(r0, r3, r5)     // Catch:{ XmlPullParserException -> 0x04dc, IOException -> 0x04cf }
            java.lang.String r3 = "app-link-generation"
            int r3 = com.android.internal.util.XmlUtils.readIntAttribute(r0, r3, r5)     // Catch:{ XmlPullParserException -> 0x04dc, IOException -> 0x04cf }
            r5 = r3
            if (r5 <= r2) goto L_0x031b
            r2 = r5
            r44 = r2
            goto L_0x031d
        L_0x031b:
            r44 = r2
        L_0x031d:
            java.lang.String r2 = "install-reason"
            r3 = 0
            int r24 = com.android.internal.util.XmlUtils.readIntAttribute(r0, r2, r3)     // Catch:{ XmlPullParserException -> 0x04dc, IOException -> 0x04cf }
            r2 = 0
            r16 = 0
            r21 = 0
            r45 = 0
            r46 = 0
            int r47 = r0.getDepth()     // Catch:{ XmlPullParserException -> 0x04dc, IOException -> 0x04cf }
            r48 = r47
            r47 = r16
            r49 = r21
            r50 = r45
            r45 = r2
        L_0x033c:
            int r2 = r0.next()     // Catch:{ XmlPullParserException -> 0x04dc, IOException -> 0x04cf }
            r51 = r2
            r3 = 1
            if (r2 == r3) goto L_0x0424
            r3 = r51
            r2 = 3
            if (r3 != r2) goto L_0x0359
            int r4 = r0.getDepth()     // Catch:{ XmlPullParserException -> 0x0447, IOException -> 0x043e }
            r2 = r48
            if (r4 <= r2) goto L_0x0353
            goto L_0x035b
        L_0x0353:
            r48 = r2
            r31 = r3
            goto L_0x0426
        L_0x0359:
            r2 = r48
        L_0x035b:
            r4 = 3
            if (r3 == r4) goto L_0x041c
            r4 = 4
            if (r3 != r4) goto L_0x0367
            r48 = r2
            r31 = r3
            goto L_0x0420
        L_0x0367:
            java.lang.String r4 = r0.getName()     // Catch:{ XmlPullParserException -> 0x0447, IOException -> 0x043e }
            r31 = -1
            int r48 = r4.hashCode()     // Catch:{ XmlPullParserException -> 0x0447, IOException -> 0x043e }
            switch(r48) {
                case -2027581689: goto L_0x03aa;
                case -1963032286: goto L_0x039e;
                case -1592287551: goto L_0x0391;
                case -1422791362: goto L_0x0384;
                case 1660896545: goto L_0x0377;
                default: goto L_0x0374;
            }     // Catch:{ XmlPullParserException -> 0x0447, IOException -> 0x043e }
        L_0x0374:
            r48 = r2
            goto L_0x03b6
        L_0x0377:
            r48 = r2
            java.lang.String r2 = "suspended-dialog-info"
            boolean r2 = r4.equals(r2)     // Catch:{ XmlPullParserException -> 0x0447, IOException -> 0x043e }
            if (r2 == 0) goto L_0x03b6
            r2 = 4
            goto L_0x03b8
        L_0x0384:
            r48 = r2
            java.lang.String r2 = "suspended-launcher-extras"
            boolean r2 = r4.equals(r2)     // Catch:{ XmlPullParserException -> 0x0447, IOException -> 0x043e }
            if (r2 == 0) goto L_0x03b6
            r2 = 3
            goto L_0x03b8
        L_0x0391:
            r48 = r2
            java.lang.String r2 = "suspended-app-extras"
            boolean r2 = r4.equals(r2)     // Catch:{ XmlPullParserException -> 0x0447, IOException -> 0x043e }
            if (r2 == 0) goto L_0x03b6
            r2 = 2
            goto L_0x03b8
        L_0x039e:
            r48 = r2
            java.lang.String r2 = "enabled-components"
            boolean r2 = r4.equals(r2)     // Catch:{ XmlPullParserException -> 0x0447, IOException -> 0x043e }
            if (r2 == 0) goto L_0x03b6
            r2 = 0
            goto L_0x03b8
        L_0x03aa:
            r48 = r2
            java.lang.String r2 = "disabled-components"
            boolean r2 = r4.equals(r2)     // Catch:{ XmlPullParserException -> 0x0447, IOException -> 0x043e }
            if (r2 == 0) goto L_0x03b6
            r2 = 1
            goto L_0x03b8
        L_0x03b6:
            r2 = r31
        L_0x03b8:
            if (r2 == 0) goto L_0x040f
            r4 = 1
            if (r2 == r4) goto L_0x0406
            r4 = 2
            if (r2 == r4) goto L_0x03fd
            r4 = 3
            if (r2 == r4) goto L_0x03f4
            r4 = 4
            if (r2 == r4) goto L_0x03eb
            java.lang.String r2 = "PackageSettings"
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ XmlPullParserException -> 0x0447, IOException -> 0x043e }
            r4.<init>()     // Catch:{ XmlPullParserException -> 0x0447, IOException -> 0x043e }
            r31 = r3
            java.lang.String r3 = "Unknown tag "
            r4.append(r3)     // Catch:{ XmlPullParserException -> 0x0447, IOException -> 0x043e }
            java.lang.String r3 = r0.getName()     // Catch:{ XmlPullParserException -> 0x0447, IOException -> 0x043e }
            r4.append(r3)     // Catch:{ XmlPullParserException -> 0x0447, IOException -> 0x043e }
            java.lang.String r3 = " under tag "
            r4.append(r3)     // Catch:{ XmlPullParserException -> 0x0447, IOException -> 0x043e }
            r4.append(r15)     // Catch:{ XmlPullParserException -> 0x0447, IOException -> 0x043e }
            java.lang.String r3 = r4.toString()     // Catch:{ XmlPullParserException -> 0x0447, IOException -> 0x043e }
            android.util.Slog.wtf(r2, r3)     // Catch:{ XmlPullParserException -> 0x0447, IOException -> 0x043e }
            goto L_0x0418
        L_0x03eb:
            r31 = r3
            android.content.pm.SuspendDialogInfo r2 = android.content.pm.SuspendDialogInfo.restoreFromXml(r0)     // Catch:{ XmlPullParserException -> 0x0447, IOException -> 0x043e }
            r46 = r2
            goto L_0x0418
        L_0x03f4:
            r31 = r3
            android.os.PersistableBundle r2 = android.os.PersistableBundle.restoreFromXml(r0)     // Catch:{ XmlPullParserException -> 0x0447, IOException -> 0x043e }
            r50 = r2
            goto L_0x0418
        L_0x03fd:
            r31 = r3
            android.os.PersistableBundle r2 = android.os.PersistableBundle.restoreFromXml(r0)     // Catch:{ XmlPullParserException -> 0x0447, IOException -> 0x043e }
            r49 = r2
            goto L_0x0418
        L_0x0406:
            r31 = r3
            android.util.ArraySet r2 = r1.readComponentsLPr(r0)     // Catch:{ XmlPullParserException -> 0x0447, IOException -> 0x043e }
            r47 = r2
            goto L_0x0418
        L_0x040f:
            r31 = r3
            android.util.ArraySet r2 = r1.readComponentsLPr(r0)     // Catch:{ XmlPullParserException -> 0x0447, IOException -> 0x043e }
            r45 = r2
        L_0x0418:
            r3 = 0
            r4 = 4
            goto L_0x033c
        L_0x041c:
            r48 = r2
            r31 = r3
        L_0x0420:
            r3 = 0
            r4 = 4
            goto L_0x033c
        L_0x0424:
            r31 = r51
        L_0x0426:
            if (r46 != 0) goto L_0x0455
            boolean r2 = android.text.TextUtils.isEmpty(r12)     // Catch:{ XmlPullParserException -> 0x0447, IOException -> 0x043e }
            if (r2 != 0) goto L_0x0455
            android.content.pm.SuspendDialogInfo$Builder r2 = new android.content.pm.SuspendDialogInfo$Builder     // Catch:{ XmlPullParserException -> 0x0447, IOException -> 0x043e }
            r2.<init>()     // Catch:{ XmlPullParserException -> 0x0447, IOException -> 0x043e }
            android.content.pm.SuspendDialogInfo$Builder r2 = r2.setMessage(r12)     // Catch:{ XmlPullParserException -> 0x0447, IOException -> 0x043e }
            android.content.pm.SuspendDialogInfo r2 = r2.build()     // Catch:{ XmlPullParserException -> 0x0447, IOException -> 0x043e }
            r46 = r2
            goto L_0x0455
        L_0x043e:
            r0 = move-exception
            r3 = r58
        L_0x0441:
            r55 = r14
            r7 = r41
            goto L_0x066e
        L_0x0447:
            r0 = move-exception
            r3 = r58
            r55 = r14
            r5 = r27
            r2 = r28
            r7 = r41
            r4 = 6
            goto L_0x06b1
        L_0x0455:
            if (r40 == 0) goto L_0x0470
            r4 = r58
            r3 = r32
            r2 = 1
            r1.setBlockUninstallLPw(r4, r3, r2)     // Catch:{ XmlPullParserException -> 0x0463, IOException -> 0x0460 }
            goto L_0x0475
        L_0x0460:
            r0 = move-exception
            r3 = r4
            goto L_0x0441
        L_0x0463:
            r0 = move-exception
            r3 = r4
            r55 = r14
            r5 = r27
            r2 = r28
            r7 = r41
            r4 = 6
            goto L_0x06b1
        L_0x0470:
            r4 = r58
            r3 = r32
            r2 = 1
        L_0x0475:
            r16 = r2
            r32 = r48
            r2 = r26
            r48 = r16
            r37 = r31
            r31 = r3
            r3 = r58
            r51 = r12
            r52 = 2
            r53 = 0
            r12 = r38
            r54 = r13
            r13 = r39
            r55 = r14
            r14 = r46
            r56 = r15
            r15 = r49
            r16 = r50
            r20 = r45
            r21 = r47
            r23 = r5
            r1 = r4
            r33 = r5
            r4 = r42
            r42 = 4
            r2.setUserState(r3, r4, r6, r7, r8, r9, r10, r11, r12, r13, r14, r15, r16, r17, r18, r19, r20, r21, r22, r23, r24, r25)     // Catch:{ XmlPullParserException -> 0x04c0, IOException -> 0x04b6 }
            r3 = r1
            r4 = r26
            r31 = r37
            r7 = r41
            r2 = r44
            r1 = r57
            goto L_0x05c7
        L_0x04b6:
            r0 = move-exception
            r3 = r1
            r7 = r41
            r13 = r54
            r1 = r57
            goto L_0x066e
        L_0x04c0:
            r0 = move-exception
            r3 = r1
            r5 = r27
            r2 = r28
            r7 = r41
            r13 = r54
            r4 = 6
            r1 = r57
            goto L_0x06b1
        L_0x04cf:
            r0 = move-exception
            r54 = r13
            r55 = r14
            r1 = r57
            r3 = r58
            r7 = r41
            goto L_0x066e
        L_0x04dc:
            r0 = move-exception
            r54 = r13
            r55 = r14
            r1 = r57
            r3 = r58
            r5 = r27
            r2 = r28
            r7 = r41
            goto L_0x06b0
        L_0x04ed:
            r0 = move-exception
            r54 = r13
            r55 = r14
            r1 = r57
            r3 = r58
            r7 = r19
            goto L_0x066e
        L_0x04fa:
            r0 = move-exception
            r54 = r13
            r55 = r14
            r1 = r57
            r3 = r58
            r7 = r19
            goto L_0x0660
        L_0x0507:
            r0 = move-exception
            r54 = r13
            r55 = r14
            r1 = r57
            r3 = r58
            r7 = r6
            goto L_0x066e
        L_0x0513:
            r0 = move-exception
            r54 = r13
            r55 = r14
            r1 = r57
            r3 = r58
            r7 = r6
            goto L_0x0660
        L_0x051f:
            r0 = move-exception
            r54 = r13
            r55 = r14
            r1 = r57
            r3 = r58
            goto L_0x066c
        L_0x052a:
            r0 = move-exception
            r54 = r13
            r55 = r14
            r1 = r57
            r3 = r58
            goto L_0x06aa
        L_0x0535:
            r1 = r58
            r31 = r3
            r42 = r6
            r34 = r7
            r53 = r9
            r36 = r10
            r48 = r11
            r52 = r12
            r54 = r13
            r55 = r14
            r56 = r15
            r41 = r29
            java.lang.String r3 = "preferred-activities"
            r5 = r34
            boolean r3 = r5.equals(r3)     // Catch:{ XmlPullParserException -> 0x05f5, IOException -> 0x05ee }
            if (r3 == 0) goto L_0x056e
            r3 = r1
            r1 = r57
            r1.readPreferredActivitiesLPw(r0, r3)     // Catch:{ XmlPullParserException -> 0x0562, IOException -> 0x05dd }
            r7 = r41
            goto L_0x05c7
        L_0x0562:
            r0 = move-exception
            r5 = r27
            r2 = r28
            r7 = r41
            r13 = r54
            r4 = 6
            goto L_0x06b1
        L_0x056e:
            r3 = r1
            r1 = r57
            java.lang.String r6 = "persistent-preferred-activities"
            boolean r6 = r5.equals(r6)     // Catch:{ XmlPullParserException -> 0x05e2, IOException -> 0x05dd }
            if (r6 == 0) goto L_0x0580
            r1.readPersistentPreferredActivitiesLPw(r0, r3)     // Catch:{ XmlPullParserException -> 0x0562, IOException -> 0x05dd }
            r7 = r41
            goto L_0x05c7
        L_0x0580:
            java.lang.String r6 = "crossProfile-intent-filters"
            boolean r6 = r5.equals(r6)     // Catch:{ XmlPullParserException -> 0x05e2, IOException -> 0x05dd }
            if (r6 == 0) goto L_0x058e
            r1.readCrossProfileIntentFiltersLPw(r0, r3)     // Catch:{ XmlPullParserException -> 0x0562, IOException -> 0x05dd }
            r7 = r41
            goto L_0x05c7
        L_0x058e:
            java.lang.String r6 = "default-apps"
            boolean r6 = r5.equals(r6)     // Catch:{ XmlPullParserException -> 0x05e2, IOException -> 0x05dd }
            if (r6 == 0) goto L_0x059c
            r1.readDefaultAppsLPw(r0, r3)     // Catch:{ XmlPullParserException -> 0x0562, IOException -> 0x05dd }
            r7 = r41
            goto L_0x05c7
        L_0x059c:
            java.lang.String r6 = "block-uninstall-packages"
            boolean r6 = r5.equals(r6)     // Catch:{ XmlPullParserException -> 0x05e2, IOException -> 0x05dd }
            if (r6 == 0) goto L_0x05aa
            r1.readBlockUninstallPackagesLPw(r0, r3)     // Catch:{ XmlPullParserException -> 0x0562, IOException -> 0x05dd }
            r7 = r41
            goto L_0x05c7
        L_0x05aa:
            java.lang.StringBuilder r6 = new java.lang.StringBuilder     // Catch:{ XmlPullParserException -> 0x05e2, IOException -> 0x05dd }
            r6.<init>()     // Catch:{ XmlPullParserException -> 0x05e2, IOException -> 0x05dd }
            java.lang.String r7 = "Unknown element under <stopped-packages>: "
            r6.append(r7)     // Catch:{ XmlPullParserException -> 0x05e2, IOException -> 0x05dd }
            java.lang.String r7 = r0.getName()     // Catch:{ XmlPullParserException -> 0x05e2, IOException -> 0x05dd }
            r6.append(r7)     // Catch:{ XmlPullParserException -> 0x05e2, IOException -> 0x05dd }
            java.lang.String r6 = r6.toString()     // Catch:{ XmlPullParserException -> 0x05e2, IOException -> 0x05dd }
            r7 = r41
            android.util.Slog.w(r7, r6)     // Catch:{ XmlPullParserException -> 0x064e, IOException -> 0x064a }
            com.android.internal.util.XmlUtils.skipCurrentTag(r0)     // Catch:{ XmlPullParserException -> 0x064e, IOException -> 0x064a }
        L_0x05c7:
            r29 = r7
            r3 = r31
            r10 = r36
            r6 = r42
            r11 = r48
            r12 = r52
            r9 = r53
            r13 = r54
            r14 = r55
            r15 = r56
            goto L_0x01b4
        L_0x05dd:
            r0 = move-exception
            r7 = r41
            goto L_0x064b
        L_0x05e2:
            r0 = move-exception
            r7 = r41
            r5 = r27
            r2 = r28
            r13 = r54
            r4 = 6
            goto L_0x06b1
        L_0x05ee:
            r0 = move-exception
            r3 = r1
            r7 = r41
            r1 = r57
            goto L_0x064b
        L_0x05f5:
            r0 = move-exception
            r3 = r1
            r7 = r41
            r1 = r57
            r5 = r27
            r2 = r28
            r13 = r54
            r4 = 6
            goto L_0x06b1
        L_0x0604:
            r31 = r3
            r42 = r6
            r53 = r9
            r36 = r10
            r48 = r11
            r52 = r12
            r54 = r13
            r55 = r14
            r56 = r15
            r7 = r29
            r3 = r58
        L_0x061a:
            r29 = r7
            r3 = r31
            r10 = r36
            r6 = r42
            r11 = r48
            r12 = r52
            r9 = r53
            r13 = r54
            r14 = r55
            r15 = r56
            goto L_0x01b4
        L_0x0630:
            r31 = r3
            r36 = r10
            r54 = r13
            r55 = r14
            r7 = r29
            r3 = r58
        L_0x063c:
            r54.close()     // Catch:{ XmlPullParserException -> 0x064e, IOException -> 0x064a }
            android.util.SparseIntArray r5 = r1.mNextAppLinkGeneration     // Catch:{ XmlPullParserException -> 0x064e, IOException -> 0x064a }
            int r6 = r2 + 1
            r5.put(r3, r6)     // Catch:{ XmlPullParserException -> 0x064e, IOException -> 0x064a }
            r13 = r54
            goto L_0x06e1
        L_0x064a:
            r0 = move-exception
        L_0x064b:
            r13 = r54
            goto L_0x066e
        L_0x064e:
            r0 = move-exception
            r5 = r27
            r2 = r28
            r13 = r54
            r4 = 6
            goto L_0x06b1
        L_0x0657:
            r0 = move-exception
            r3 = r58
            r54 = r13
            r55 = r14
            r7 = r29
        L_0x0660:
            r5 = r27
            r2 = r28
            goto L_0x06b0
        L_0x0665:
            r0 = move-exception
            r3 = r58
            r54 = r13
            r55 = r14
        L_0x066c:
            r7 = r29
        L_0x066e:
            java.lang.StringBuilder r2 = r1.mReadMessages
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            r5 = r27
            r4.append(r5)
            java.lang.String r5 = r0.toString()
            r4.append(r5)
            java.lang.String r4 = r4.toString()
            r2.append(r4)
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r4 = "Error reading settings: "
            r2.append(r4)
            r2.append(r0)
            java.lang.String r2 = r2.toString()
            r4 = 6
            com.android.server.pm.PackageManagerService.reportSettingsProblem(r4, r2)
            r2 = r28
            android.util.Slog.wtf(r7, r2, r0)
            goto L_0x06e1
        L_0x06a3:
            r0 = move-exception
            r3 = r58
            r54 = r13
            r55 = r14
        L_0x06aa:
            r5 = r27
            r2 = r28
            r7 = r29
        L_0x06b0:
            r4 = 6
        L_0x06b1:
            java.lang.StringBuilder r6 = r1.mReadMessages
            java.lang.StringBuilder r8 = new java.lang.StringBuilder
            r8.<init>()
            r8.append(r5)
            java.lang.String r5 = r0.toString()
            r8.append(r5)
            java.lang.String r5 = r8.toString()
            r6.append(r5)
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>()
            java.lang.String r6 = "Error reading stopped packages: "
            r5.append(r6)
            r5.append(r0)
            java.lang.String r5 = r5.toString()
            com.android.server.pm.PackageManagerService.reportSettingsProblem(r4, r5)
            android.util.Slog.wtf(r7, r2, r0)
        L_0x06e1:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.pm.Settings.readPackageRestrictionsLPr(int):void");
    }

    /* access modifiers changed from: package-private */
    public void setBlockUninstallLPw(int userId, String packageName, boolean blockUninstall) {
        ArraySet<String> packages = this.mBlockUninstallPackages.get(userId);
        if (blockUninstall) {
            if (packages == null) {
                packages = new ArraySet<>();
                this.mBlockUninstallPackages.put(userId, packages);
            }
            packages.add(packageName);
        } else if (packages != null) {
            packages.remove(packageName);
            if (packages.isEmpty()) {
                this.mBlockUninstallPackages.remove(userId);
            }
        }
    }

    /* access modifiers changed from: package-private */
    public boolean getBlockUninstallLPr(int userId, String packageName) {
        ArraySet<String> packages = this.mBlockUninstallPackages.get(userId);
        if (packages == null) {
            return false;
        }
        return packages.contains(packageName);
    }

    private ArraySet<String> readComponentsLPr(XmlPullParser parser) throws IOException, XmlPullParserException {
        String componentName;
        ArraySet<String> components = null;
        int outerDepth = parser.getDepth();
        while (true) {
            int next = parser.next();
            int type = next;
            if (next == 1 || (type == 3 && parser.getDepth() <= outerDepth)) {
                return components;
            }
            if (!(type == 3 || type == 4 || !parser.getName().equals(TAG_ITEM) || (componentName = parser.getAttributeValue((String) null, ATTR_NAME)) == null)) {
                if (components == null) {
                    components = new ArraySet<>();
                }
                components.add(componentName);
            }
        }
        return components;
    }

    /* access modifiers changed from: package-private */
    public void writePreferredActivitiesLPr(XmlSerializer serializer, int userId, boolean full) throws IllegalArgumentException, IllegalStateException, IOException {
        serializer.startTag((String) null, "preferred-activities");
        PreferredIntentResolver pir = this.mPreferredActivities.get(userId);
        if (pir != null) {
            for (PreferredActivity pa : pir.filterSet()) {
                serializer.startTag((String) null, TAG_ITEM);
                pa.writeToXml(serializer, full);
                serializer.endTag((String) null, TAG_ITEM);
            }
        }
        serializer.endTag((String) null, "preferred-activities");
    }

    /* access modifiers changed from: package-private */
    public void writePersistentPreferredActivitiesLPr(XmlSerializer serializer, int userId) throws IllegalArgumentException, IllegalStateException, IOException {
        serializer.startTag((String) null, TAG_PERSISTENT_PREFERRED_ACTIVITIES);
        PersistentPreferredIntentResolver ppir = this.mPersistentPreferredActivities.get(userId);
        if (ppir != null) {
            for (PersistentPreferredActivity ppa : ppir.filterSet()) {
                serializer.startTag((String) null, TAG_ITEM);
                ppa.writeToXml(serializer);
                serializer.endTag((String) null, TAG_ITEM);
            }
        }
        serializer.endTag((String) null, TAG_PERSISTENT_PREFERRED_ACTIVITIES);
    }

    /* access modifiers changed from: package-private */
    public void writeCrossProfileIntentFiltersLPr(XmlSerializer serializer, int userId) throws IllegalArgumentException, IllegalStateException, IOException {
        serializer.startTag((String) null, TAG_CROSS_PROFILE_INTENT_FILTERS);
        CrossProfileIntentResolver cpir = this.mCrossProfileIntentResolvers.get(userId);
        if (cpir != null) {
            for (CrossProfileIntentFilter cpif : cpir.filterSet()) {
                serializer.startTag((String) null, TAG_ITEM);
                cpif.writeToXml(serializer);
                serializer.endTag((String) null, TAG_ITEM);
            }
        }
        serializer.endTag((String) null, TAG_CROSS_PROFILE_INTENT_FILTERS);
    }

    /* access modifiers changed from: package-private */
    public void writeDomainVerificationsLPr(XmlSerializer serializer, IntentFilterVerificationInfo verificationInfo) throws IllegalArgumentException, IllegalStateException, IOException {
        if (verificationInfo != null && verificationInfo.getPackageName() != null) {
            serializer.startTag((String) null, TAG_DOMAIN_VERIFICATION);
            verificationInfo.writeToXml(serializer);
            serializer.endTag((String) null, TAG_DOMAIN_VERIFICATION);
        }
    }

    /* access modifiers changed from: package-private */
    public void writeAllDomainVerificationsLPr(XmlSerializer serializer, int userId) throws IllegalArgumentException, IllegalStateException, IOException {
        serializer.startTag((String) null, TAG_ALL_INTENT_FILTER_VERIFICATION);
        int N = this.mPackages.size();
        for (int i = 0; i < N; i++) {
            IntentFilterVerificationInfo ivi = this.mPackages.valueAt(i).getIntentFilterVerificationInfo();
            if (ivi != null) {
                writeDomainVerificationsLPr(serializer, ivi);
            }
        }
        serializer.endTag((String) null, TAG_ALL_INTENT_FILTER_VERIFICATION);
    }

    /* access modifiers changed from: package-private */
    public void readAllDomainVerificationsLPr(XmlPullParser parser, int userId) throws XmlPullParserException, IOException {
        this.mRestoredIntentFilterVerifications.clear();
        int outerDepth = parser.getDepth();
        while (true) {
            int next = parser.next();
            int type = next;
            if (next == 1) {
                return;
            }
            if (type == 3 && parser.getDepth() <= outerDepth) {
                return;
            }
            if (!(type == 3 || type == 4)) {
                if (parser.getName().equals(TAG_DOMAIN_VERIFICATION)) {
                    IntentFilterVerificationInfo ivi = new IntentFilterVerificationInfo(parser);
                    String pkgName = ivi.getPackageName();
                    PackageSetting ps = this.mPackages.get(pkgName);
                    if (ps != null) {
                        ps.setIntentFilterVerificationInfo(ivi);
                    } else {
                        this.mRestoredIntentFilterVerifications.put(pkgName, ivi);
                    }
                } else {
                    PackageManagerService.reportSettingsProblem(5, "Unknown element under <all-intent-filter-verification>: " + parser.getName());
                    XmlUtils.skipCurrentTag(parser);
                }
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void writeDefaultAppsLPr(XmlSerializer serializer, int userId) throws IllegalArgumentException, IllegalStateException, IOException {
        serializer.startTag((String) null, TAG_DEFAULT_APPS);
        String defaultBrowser = this.mDefaultBrowserApp.get(userId);
        if (!TextUtils.isEmpty(defaultBrowser)) {
            serializer.startTag((String) null, TAG_DEFAULT_BROWSER);
            serializer.attribute((String) null, ATTR_PACKAGE_NAME, defaultBrowser);
            serializer.endTag((String) null, TAG_DEFAULT_BROWSER);
        }
        serializer.endTag((String) null, TAG_DEFAULT_APPS);
    }

    /* access modifiers changed from: package-private */
    public void writeBlockUninstallPackagesLPr(XmlSerializer serializer, int userId) throws IOException {
        ArraySet<String> packages = this.mBlockUninstallPackages.get(userId);
        if (packages != null) {
            serializer.startTag((String) null, TAG_BLOCK_UNINSTALL_PACKAGES);
            for (int i = 0; i < packages.size(); i++) {
                serializer.startTag((String) null, TAG_BLOCK_UNINSTALL);
                serializer.attribute((String) null, ATTR_PACKAGE_NAME, packages.valueAt(i));
                serializer.endTag((String) null, TAG_BLOCK_UNINSTALL);
            }
            serializer.endTag((String) null, TAG_BLOCK_UNINSTALL_PACKAGES);
        }
    }

    /* access modifiers changed from: package-private */
    /* JADX WARNING: Removed duplicated region for block: B:143:0x0309  */
    /* JADX WARNING: Removed duplicated region for block: B:149:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void writePackageRestrictionsLPr(int r27) {
        /*
            r26 = this;
            r1 = r26
            r2 = r27
            java.lang.String r3 = "disabled-components"
            java.lang.String r4 = "enabled-components"
            java.lang.String r5 = "suspended-launcher-extras"
            java.lang.String r6 = "suspended-app-extras"
            java.lang.String r7 = "suspended-dialog-info"
            java.lang.String r8 = "pkg"
            java.lang.String r9 = "package-restrictions"
            java.lang.String r10 = "name"
            long r11 = android.os.SystemClock.uptimeMillis()
            java.io.File r13 = r26.getUserPackagesStateFile(r27)
            java.io.File r14 = r26.getUserPackagesStateBackupFile(r27)
            java.io.File r0 = new java.io.File
            java.lang.String r15 = r13.getParent()
            r0.<init>(r15)
            r0.mkdirs()
            boolean r0 = r13.exists()
            java.lang.String r15 = "PackageManager"
            if (r0 == 0) goto L_0x0054
            boolean r0 = r14.exists()
            if (r0 != 0) goto L_0x004c
            boolean r0 = r13.renameTo(r14)
            if (r0 != 0) goto L_0x0054
            java.lang.String r0 = "Unable to backup user packages state file, current changes will be lost at reboot"
            android.util.Slog.wtf(r15, r0)
            return
        L_0x004c:
            r13.delete()
            java.lang.String r0 = "Preserving older stopped packages backup"
            android.util.Slog.w(r15, r0)
        L_0x0054:
            java.io.FileOutputStream r0 = new java.io.FileOutputStream     // Catch:{ IOException -> 0x02ed }
            r0.<init>(r13)     // Catch:{ IOException -> 0x02ed }
            r16 = r0
            java.io.BufferedOutputStream r0 = new java.io.BufferedOutputStream     // Catch:{ IOException -> 0x02ed }
            r17 = r15
            r15 = r16
            r0.<init>(r15)     // Catch:{ IOException -> 0x02e5 }
            r16 = r0
            com.android.internal.util.FastXmlSerializer r0 = new com.android.internal.util.FastXmlSerializer     // Catch:{ IOException -> 0x02e5 }
            r0.<init>()     // Catch:{ IOException -> 0x02e5 }
            r18 = r0
            java.nio.charset.Charset r0 = java.nio.charset.StandardCharsets.UTF_8     // Catch:{ IOException -> 0x02e5 }
            java.lang.String r0 = r0.name()     // Catch:{ IOException -> 0x02e5 }
            r19 = r11
            r11 = r16
            r12 = r18
            r12.setOutput(r11, r0)     // Catch:{ IOException -> 0x02df }
            r16 = r13
            r13 = 1
            java.lang.Boolean r0 = java.lang.Boolean.valueOf(r13)     // Catch:{ IOException -> 0x02db }
            r13 = 0
            r12.startDocument(r13, r0)     // Catch:{ IOException -> 0x02db }
            java.lang.String r0 = "http://xmlpull.org/v1/doc/features.html#indent-output"
            r13 = 1
            r12.setFeature(r0, r13)     // Catch:{ IOException -> 0x02db }
            r13 = 0
            r12.startTag(r13, r9)     // Catch:{ IOException -> 0x02db }
            android.util.ArrayMap<java.lang.String, com.android.server.pm.PackageSetting> r0 = r1.mPackages     // Catch:{ IOException -> 0x02db }
            java.util.Collection r0 = r0.values()     // Catch:{ IOException -> 0x02db }
            java.util.Iterator r13 = r0.iterator()     // Catch:{ IOException -> 0x02db }
        L_0x009c:
            boolean r0 = r13.hasNext()     // Catch:{ IOException -> 0x02db }
            if (r0 == 0) goto L_0x0288
            java.lang.Object r0 = r13.next()     // Catch:{ IOException -> 0x0280 }
            com.android.server.pm.PackageSetting r0 = (com.android.server.pm.PackageSetting) r0     // Catch:{ IOException -> 0x0280 }
            r21 = r0
            r22 = r13
            r13 = r21
            android.content.pm.PackageUserState r0 = r13.readUserState(r2)     // Catch:{ IOException -> 0x0280 }
            r21 = r0
            r23 = r14
            r14 = 0
            r12.startTag(r14, r8)     // Catch:{ IOException -> 0x0279 }
            java.lang.String r0 = r13.name     // Catch:{ IOException -> 0x0279 }
            r12.attribute(r14, r10, r0)     // Catch:{ IOException -> 0x0279 }
            r14 = r21
            long r0 = r14.ceDataInode     // Catch:{ IOException -> 0x0279 }
            r24 = 0
            int r0 = (r0 > r24 ? 1 : (r0 == r24 ? 0 : -1))
            if (r0 == 0) goto L_0x00d0
            java.lang.String r0 = "ceDataInode"
            long r1 = r14.ceDataInode     // Catch:{ IOException -> 0x0279 }
            com.android.internal.util.XmlUtils.writeLongAttribute(r12, r0, r1)     // Catch:{ IOException -> 0x0279 }
        L_0x00d0:
            boolean r0 = r14.installed     // Catch:{ IOException -> 0x0279 }
            if (r0 != 0) goto L_0x00dd
            java.lang.String r0 = "inst"
            java.lang.String r1 = "false"
            r2 = 0
            r12.attribute(r2, r0, r1)     // Catch:{ IOException -> 0x0279 }
        L_0x00dd:
            boolean r0 = r14.stopped     // Catch:{ IOException -> 0x0279 }
            java.lang.String r1 = "true"
            if (r0 == 0) goto L_0x00eb
            java.lang.String r0 = "stopped"
            r2 = 0
            r12.attribute(r2, r0, r1)     // Catch:{ IOException -> 0x0279 }
        L_0x00eb:
            boolean r0 = r14.notLaunched     // Catch:{ IOException -> 0x0279 }
            if (r0 == 0) goto L_0x00f6
            java.lang.String r0 = "nl"
            r2 = 0
            r12.attribute(r2, r0, r1)     // Catch:{ IOException -> 0x0279 }
        L_0x00f6:
            boolean r0 = r14.hidden     // Catch:{ IOException -> 0x0279 }
            if (r0 == 0) goto L_0x0101
            java.lang.String r0 = "hidden"
            r2 = 0
            r12.attribute(r2, r0, r1)     // Catch:{ IOException -> 0x0279 }
        L_0x0101:
            int r0 = r14.distractionFlags     // Catch:{ IOException -> 0x0279 }
            if (r0 == 0) goto L_0x0114
            java.lang.String r0 = "distraction_flags"
            int r2 = r14.distractionFlags     // Catch:{ IOException -> 0x0279 }
            java.lang.String r2 = java.lang.Integer.toString(r2)     // Catch:{ IOException -> 0x0279 }
            r21 = r15
            r15 = 0
            r12.attribute(r15, r0, r2)     // Catch:{ IOException -> 0x0279 }
            goto L_0x0116
        L_0x0114:
            r21 = r15
        L_0x0116:
            boolean r0 = r14.suspended     // Catch:{ IOException -> 0x0279 }
            if (r0 == 0) goto L_0x01a1
            java.lang.String r0 = "suspended"
            r2 = 0
            r12.attribute(r2, r0, r1)     // Catch:{ IOException -> 0x0279 }
            java.lang.String r0 = r14.suspendingPackage     // Catch:{ IOException -> 0x0279 }
            if (r0 == 0) goto L_0x012e
            java.lang.String r0 = "suspending-package"
            java.lang.String r2 = r14.suspendingPackage     // Catch:{ IOException -> 0x0279 }
            r15 = 0
            r12.attribute(r15, r0, r2)     // Catch:{ IOException -> 0x0279 }
        L_0x012e:
            android.content.pm.SuspendDialogInfo r0 = r14.dialogInfo     // Catch:{ IOException -> 0x0279 }
            if (r0 == 0) goto L_0x013f
            r2 = 0
            r12.startTag(r2, r7)     // Catch:{ IOException -> 0x0279 }
            android.content.pm.SuspendDialogInfo r0 = r14.dialogInfo     // Catch:{ IOException -> 0x0279 }
            r0.saveToXml(r12)     // Catch:{ IOException -> 0x0279 }
            r2 = 0
            r12.endTag(r2, r7)     // Catch:{ IOException -> 0x0279 }
        L_0x013f:
            android.os.PersistableBundle r0 = r14.suspendedAppExtras     // Catch:{ IOException -> 0x0279 }
            java.lang.String r2 = "PackageSettings"
            if (r0 == 0) goto L_0x0172
            r15 = 0
            r12.startTag(r15, r6)     // Catch:{ IOException -> 0x0279 }
            android.os.PersistableBundle r0 = r14.suspendedAppExtras     // Catch:{ XmlPullParserException -> 0x0151 }
            r0.saveToXml(r12)     // Catch:{ XmlPullParserException -> 0x0151 }
            r24 = r7
            goto L_0x016d
        L_0x0151:
            r0 = move-exception
            java.lang.StringBuilder r15 = new java.lang.StringBuilder     // Catch:{ IOException -> 0x0279 }
            r15.<init>()     // Catch:{ IOException -> 0x0279 }
            r24 = r7
            java.lang.String r7 = "Exception while trying to write suspendedAppExtras for "
            r15.append(r7)     // Catch:{ IOException -> 0x0279 }
            r15.append(r13)     // Catch:{ IOException -> 0x0279 }
            java.lang.String r7 = ". Will be lost on reboot"
            r15.append(r7)     // Catch:{ IOException -> 0x0279 }
            java.lang.String r7 = r15.toString()     // Catch:{ IOException -> 0x0279 }
            android.util.Slog.wtf(r2, r7, r0)     // Catch:{ IOException -> 0x0279 }
        L_0x016d:
            r7 = 0
            r12.endTag(r7, r6)     // Catch:{ IOException -> 0x0279 }
            goto L_0x0174
        L_0x0172:
            r24 = r7
        L_0x0174:
            android.os.PersistableBundle r0 = r14.suspendedLauncherExtras     // Catch:{ IOException -> 0x0279 }
            if (r0 == 0) goto L_0x01a3
            r7 = 0
            r12.startTag(r7, r5)     // Catch:{ IOException -> 0x0279 }
            android.os.PersistableBundle r0 = r14.suspendedLauncherExtras     // Catch:{ XmlPullParserException -> 0x0182 }
            r0.saveToXml(r12)     // Catch:{ XmlPullParserException -> 0x0182 }
            goto L_0x019c
        L_0x0182:
            r0 = move-exception
            java.lang.StringBuilder r7 = new java.lang.StringBuilder     // Catch:{ IOException -> 0x0279 }
            r7.<init>()     // Catch:{ IOException -> 0x0279 }
            java.lang.String r15 = "Exception while trying to write suspendedLauncherExtras for "
            r7.append(r15)     // Catch:{ IOException -> 0x0279 }
            r7.append(r13)     // Catch:{ IOException -> 0x0279 }
            java.lang.String r15 = ". Will be lost on reboot"
            r7.append(r15)     // Catch:{ IOException -> 0x0279 }
            java.lang.String r7 = r7.toString()     // Catch:{ IOException -> 0x0279 }
            android.util.Slog.wtf(r2, r7, r0)     // Catch:{ IOException -> 0x0279 }
        L_0x019c:
            r2 = 0
            r12.endTag(r2, r5)     // Catch:{ IOException -> 0x0279 }
            goto L_0x01a3
        L_0x01a1:
            r24 = r7
        L_0x01a3:
            boolean r0 = r14.instantApp     // Catch:{ IOException -> 0x0279 }
            if (r0 == 0) goto L_0x01ae
            java.lang.String r0 = "instant-app"
            r2 = 0
            r12.attribute(r2, r0, r1)     // Catch:{ IOException -> 0x0279 }
        L_0x01ae:
            boolean r0 = r14.virtualPreload     // Catch:{ IOException -> 0x0279 }
            if (r0 == 0) goto L_0x01b9
            java.lang.String r0 = "virtual-preload"
            r2 = 0
            r12.attribute(r2, r0, r1)     // Catch:{ IOException -> 0x0279 }
        L_0x01b9:
            int r0 = r14.enabled     // Catch:{ IOException -> 0x0279 }
            if (r0 == 0) goto L_0x01d5
            java.lang.String r0 = "enabled"
            int r1 = r14.enabled     // Catch:{ IOException -> 0x0279 }
            java.lang.String r1 = java.lang.Integer.toString(r1)     // Catch:{ IOException -> 0x0279 }
            r2 = 0
            r12.attribute(r2, r0, r1)     // Catch:{ IOException -> 0x0279 }
            java.lang.String r0 = r14.lastDisableAppCaller     // Catch:{ IOException -> 0x0279 }
            if (r0 == 0) goto L_0x01d5
            java.lang.String r0 = "enabledCaller"
            java.lang.String r1 = r14.lastDisableAppCaller     // Catch:{ IOException -> 0x0279 }
            r2 = 0
            r12.attribute(r2, r0, r1)     // Catch:{ IOException -> 0x0279 }
        L_0x01d5:
            int r0 = r14.domainVerificationStatus     // Catch:{ IOException -> 0x0279 }
            if (r0 == 0) goto L_0x01e0
            java.lang.String r0 = "domainVerificationStatus"
            int r1 = r14.domainVerificationStatus     // Catch:{ IOException -> 0x0279 }
            com.android.internal.util.XmlUtils.writeIntAttribute(r12, r0, r1)     // Catch:{ IOException -> 0x0279 }
        L_0x01e0:
            int r0 = r14.appLinkGeneration     // Catch:{ IOException -> 0x0279 }
            if (r0 == 0) goto L_0x01eb
            java.lang.String r0 = "app-link-generation"
            int r1 = r14.appLinkGeneration     // Catch:{ IOException -> 0x0279 }
            com.android.internal.util.XmlUtils.writeIntAttribute(r12, r0, r1)     // Catch:{ IOException -> 0x0279 }
        L_0x01eb:
            int r0 = r14.installReason     // Catch:{ IOException -> 0x0279 }
            if (r0 == 0) goto L_0x01fc
            java.lang.String r0 = "install-reason"
            int r1 = r14.installReason     // Catch:{ IOException -> 0x0279 }
            java.lang.String r1 = java.lang.Integer.toString(r1)     // Catch:{ IOException -> 0x0279 }
            r2 = 0
            r12.attribute(r2, r0, r1)     // Catch:{ IOException -> 0x0279 }
        L_0x01fc:
            java.lang.String r0 = r14.harmfulAppWarning     // Catch:{ IOException -> 0x0279 }
            if (r0 == 0) goto L_0x0208
            java.lang.String r0 = "harmful-app-warning"
            java.lang.String r1 = r14.harmfulAppWarning     // Catch:{ IOException -> 0x0279 }
            r2 = 0
            r12.attribute(r2, r0, r1)     // Catch:{ IOException -> 0x0279 }
        L_0x0208:
            android.util.ArraySet r0 = r14.enabledComponents     // Catch:{ IOException -> 0x0279 }
            boolean r0 = com.android.internal.util.ArrayUtils.isEmpty(r0)     // Catch:{ IOException -> 0x0279 }
            java.lang.String r1 = "item"
            if (r0 != 0) goto L_0x0239
            r2 = 0
            r12.startTag(r2, r4)     // Catch:{ IOException -> 0x0279 }
            android.util.ArraySet r0 = r14.enabledComponents     // Catch:{ IOException -> 0x0279 }
            java.util.Iterator r0 = r0.iterator()     // Catch:{ IOException -> 0x0279 }
        L_0x021d:
            boolean r2 = r0.hasNext()     // Catch:{ IOException -> 0x0279 }
            if (r2 == 0) goto L_0x0235
            java.lang.Object r2 = r0.next()     // Catch:{ IOException -> 0x0279 }
            java.lang.String r2 = (java.lang.String) r2     // Catch:{ IOException -> 0x0279 }
            r7 = 0
            r12.startTag(r7, r1)     // Catch:{ IOException -> 0x0279 }
            r12.attribute(r7, r10, r2)     // Catch:{ IOException -> 0x0279 }
            r12.endTag(r7, r1)     // Catch:{ IOException -> 0x0279 }
            goto L_0x021d
        L_0x0235:
            r2 = 0
            r12.endTag(r2, r4)     // Catch:{ IOException -> 0x0279 }
        L_0x0239:
            android.util.ArraySet r0 = r14.disabledComponents     // Catch:{ IOException -> 0x0279 }
            boolean r0 = com.android.internal.util.ArrayUtils.isEmpty(r0)     // Catch:{ IOException -> 0x0279 }
            if (r0 != 0) goto L_0x0267
            r2 = 0
            r12.startTag(r2, r3)     // Catch:{ IOException -> 0x0279 }
            android.util.ArraySet r0 = r14.disabledComponents     // Catch:{ IOException -> 0x0279 }
            java.util.Iterator r0 = r0.iterator()     // Catch:{ IOException -> 0x0279 }
        L_0x024b:
            boolean r2 = r0.hasNext()     // Catch:{ IOException -> 0x0279 }
            if (r2 == 0) goto L_0x0263
            java.lang.Object r2 = r0.next()     // Catch:{ IOException -> 0x0279 }
            java.lang.String r2 = (java.lang.String) r2     // Catch:{ IOException -> 0x0279 }
            r7 = 0
            r12.startTag(r7, r1)     // Catch:{ IOException -> 0x0279 }
            r12.attribute(r7, r10, r2)     // Catch:{ IOException -> 0x0279 }
            r12.endTag(r7, r1)     // Catch:{ IOException -> 0x0279 }
            goto L_0x024b
        L_0x0263:
            r1 = 0
            r12.endTag(r1, r3)     // Catch:{ IOException -> 0x0279 }
        L_0x0267:
            r1 = 0
            r12.endTag(r1, r8)     // Catch:{ IOException -> 0x0279 }
            r1 = r26
            r2 = r27
            r15 = r21
            r13 = r22
            r14 = r23
            r7 = r24
            goto L_0x009c
        L_0x0279:
            r0 = move-exception
            r1 = r26
            r2 = r27
            goto L_0x02f6
        L_0x0280:
            r0 = move-exception
            r23 = r14
            r1 = r26
            r2 = r27
            goto L_0x02de
        L_0x0288:
            r23 = r14
            r21 = r15
            r3 = 1
            r1 = r26
            r2 = r27
            r1.writePreferredActivitiesLPr(r12, r2, r3)     // Catch:{ IOException -> 0x02d9 }
            r1.writePersistentPreferredActivitiesLPr(r12, r2)     // Catch:{ IOException -> 0x02d9 }
            r1.writeCrossProfileIntentFiltersLPr(r12, r2)     // Catch:{ IOException -> 0x02d9 }
            r1.writeDefaultAppsLPr(r12, r2)     // Catch:{ IOException -> 0x02d9 }
            r1.writeBlockUninstallPackagesLPr(r12, r2)     // Catch:{ IOException -> 0x02d9 }
            r3 = 0
            r12.endTag(r3, r9)     // Catch:{ IOException -> 0x02d9 }
            r12.endDocument()     // Catch:{ IOException -> 0x02d9 }
            r11.flush()     // Catch:{ IOException -> 0x02d9 }
            android.os.FileUtils.sync(r21)     // Catch:{ IOException -> 0x02d9 }
            r11.close()     // Catch:{ IOException -> 0x02d9 }
            r23.delete()     // Catch:{ IOException -> 0x02d9 }
            java.lang.String r0 = r16.toString()     // Catch:{ IOException -> 0x02d9 }
            r3 = 432(0x1b0, float:6.05E-43)
            r4 = -1
            android.os.FileUtils.setPermissions(r0, r3, r4, r4)     // Catch:{ IOException -> 0x02d9 }
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ IOException -> 0x02d9 }
            r0.<init>()     // Catch:{ IOException -> 0x02d9 }
            java.lang.String r3 = "package-user-"
            r0.append(r3)     // Catch:{ IOException -> 0x02d9 }
            r0.append(r2)     // Catch:{ IOException -> 0x02d9 }
            java.lang.String r0 = r0.toString()     // Catch:{ IOException -> 0x02d9 }
            long r3 = android.os.SystemClock.uptimeMillis()     // Catch:{ IOException -> 0x02d9 }
            long r3 = r3 - r19
            com.android.internal.logging.EventLogTags.writeCommitSysConfigFile(r0, r3)     // Catch:{ IOException -> 0x02d9 }
            return
        L_0x02d9:
            r0 = move-exception
            goto L_0x02f6
        L_0x02db:
            r0 = move-exception
            r23 = r14
        L_0x02de:
            goto L_0x02f6
        L_0x02df:
            r0 = move-exception
            r16 = r13
            r23 = r14
            goto L_0x02f6
        L_0x02e5:
            r0 = move-exception
            r19 = r11
            r16 = r13
            r23 = r14
            goto L_0x02f6
        L_0x02ed:
            r0 = move-exception
            r19 = r11
            r16 = r13
            r23 = r14
            r17 = r15
        L_0x02f6:
            java.lang.String r3 = "Unable to write package manager user packages state,  current changes will be lost at reboot"
            r4 = r17
            android.util.Slog.wtf(r4, r3, r0)
            boolean r0 = r16.exists()
            if (r0 == 0) goto L_0x031f
            boolean r0 = r16.delete()
            if (r0 != 0) goto L_0x031f
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r3 = "Failed to clean up mangled file: "
            r0.append(r3)
            java.io.File r3 = r1.mStoppedPackagesFilename
            r0.append(r3)
            java.lang.String r0 = r0.toString()
            android.util.Log.i(r4, r0)
        L_0x031f:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.pm.Settings.writePackageRestrictionsLPr(int):void");
    }

    /* access modifiers changed from: package-private */
    public void readInstallPermissionsLPr(XmlPullParser parser, PermissionsState permissionsState) throws IOException, XmlPullParserException {
        XmlPullParser xmlPullParser = parser;
        PermissionsState permissionsState2 = permissionsState;
        int outerDepth = parser.getDepth();
        while (true) {
            int next = parser.next();
            int type = next;
            boolean granted = true;
            if (next == 1) {
                return;
            } else if (type == 3 && parser.getDepth() <= outerDepth) {
                return;
            } else if (type != 3) {
                if (type != 4) {
                    if (parser.getName().equals(TAG_ITEM)) {
                        String name = xmlPullParser.getAttributeValue((String) null, ATTR_NAME);
                        BasePermission bp = this.mPermissions.getPermission(name);
                        if (bp == null) {
                            Slog.w("PackageManager", "Unknown permission: " + name);
                            XmlUtils.skipCurrentTag(parser);
                        } else {
                            String grantedStr = xmlPullParser.getAttributeValue((String) null, ATTR_GRANTED);
                            int flags = 0;
                            if (grantedStr != null && !Boolean.parseBoolean(grantedStr)) {
                                granted = false;
                            }
                            String flagsStr = xmlPullParser.getAttributeValue((String) null, ATTR_FLAGS);
                            if (flagsStr != null) {
                                flags = Integer.parseInt(flagsStr, 16);
                            }
                            if (granted) {
                                if (permissionsState2.grantInstallPermission(bp) == -1) {
                                    Slog.w("PackageManager", "Permission already added: " + name);
                                    XmlUtils.skipCurrentTag(parser);
                                } else {
                                    permissionsState2.updatePermissionFlags(bp, -1, 64511, flags);
                                }
                            } else if (permissionsState2.revokeInstallPermission(bp) == -1) {
                                Slog.w("PackageManager", "Permission already added: " + name);
                                XmlUtils.skipCurrentTag(parser);
                            } else {
                                permissionsState2.updatePermissionFlags(bp, -1, 64511, flags);
                            }
                        }
                    } else {
                        Slog.w("PackageManager", "Unknown element under <permissions>: " + parser.getName());
                        XmlUtils.skipCurrentTag(parser);
                    }
                }
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void writePermissionsLPr(XmlSerializer serializer, List<PermissionsState.PermissionState> permissionStates) throws IOException {
        if (!permissionStates.isEmpty()) {
            serializer.startTag((String) null, TAG_PERMISSIONS);
            for (PermissionsState.PermissionState permissionState : permissionStates) {
                serializer.startTag((String) null, TAG_ITEM);
                serializer.attribute((String) null, ATTR_NAME, permissionState.getName());
                serializer.attribute((String) null, ATTR_GRANTED, String.valueOf(permissionState.isGranted()));
                serializer.attribute((String) null, ATTR_FLAGS, Integer.toHexString(permissionState.getFlags()));
                serializer.endTag((String) null, TAG_ITEM);
            }
            serializer.endTag((String) null, TAG_PERMISSIONS);
        }
    }

    /* access modifiers changed from: package-private */
    public void writeChildPackagesLPw(XmlSerializer serializer, List<String> childPackageNames) throws IOException {
        if (childPackageNames != null) {
            int childCount = childPackageNames.size();
            for (int i = 0; i < childCount; i++) {
                serializer.startTag((String) null, TAG_CHILD_PACKAGE);
                serializer.attribute((String) null, ATTR_NAME, childPackageNames.get(i));
                serializer.endTag((String) null, TAG_CHILD_PACKAGE);
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void readUsesStaticLibLPw(XmlPullParser parser, PackageSetting outPs) throws IOException, XmlPullParserException {
        int outerDepth = parser.getDepth();
        while (true) {
            int next = parser.next();
            int type = next;
            if (next == 1) {
                return;
            }
            if (type == 3 && parser.getDepth() <= outerDepth) {
                return;
            }
            if (!(type == 3 || type == 4)) {
                String libName = parser.getAttributeValue((String) null, ATTR_NAME);
                long libVersion = -1;
                try {
                    libVersion = Long.parseLong(parser.getAttributeValue((String) null, "version"));
                } catch (NumberFormatException e) {
                }
                if (libName != null && libVersion >= 0) {
                    outPs.usesStaticLibraries = (String[]) ArrayUtils.appendElement(String.class, outPs.usesStaticLibraries, libName);
                    outPs.usesStaticLibrariesVersions = ArrayUtils.appendLong(outPs.usesStaticLibrariesVersions, libVersion);
                }
                XmlUtils.skipCurrentTag(parser);
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void writeUsesStaticLibLPw(XmlSerializer serializer, String[] usesStaticLibraries, long[] usesStaticLibraryVersions) throws IOException {
        if (!ArrayUtils.isEmpty(usesStaticLibraries) && !ArrayUtils.isEmpty(usesStaticLibraryVersions) && usesStaticLibraries.length == usesStaticLibraryVersions.length) {
            int libCount = usesStaticLibraries.length;
            for (int i = 0; i < libCount; i++) {
                String libName = usesStaticLibraries[i];
                long libVersion = usesStaticLibraryVersions[i];
                serializer.startTag((String) null, TAG_USES_STATIC_LIB);
                serializer.attribute((String) null, ATTR_NAME, libName);
                serializer.attribute((String) null, "version", Long.toString(libVersion));
                serializer.endTag((String) null, TAG_USES_STATIC_LIB);
            }
        }
    }

    /* access modifiers changed from: package-private */
    /* JADX WARNING: Removed duplicated region for block: B:24:0x009e A[Catch:{ XmlPullParserException -> 0x0165, IOException -> 0x0133 }] */
    /* JADX WARNING: Removed duplicated region for block: B:26:0x00ac A[Catch:{ XmlPullParserException -> 0x0165, IOException -> 0x0133 }] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void readStoppedLPw() {
        /*
            r16 = this;
            r1 = r16
            r2 = 0
            java.io.File r0 = r1.mBackupStoppedPackagesFilename
            boolean r0 = r0.exists()
            r3 = 4
            java.lang.String r4 = "PackageManager"
            if (r0 == 0) goto L_0x0047
            java.io.FileInputStream r0 = new java.io.FileInputStream     // Catch:{ IOException -> 0x0046 }
            java.io.File r5 = r1.mBackupStoppedPackagesFilename     // Catch:{ IOException -> 0x0046 }
            r0.<init>(r5)     // Catch:{ IOException -> 0x0046 }
            r2 = r0
            java.lang.StringBuilder r0 = r1.mReadMessages     // Catch:{ IOException -> 0x0046 }
            java.lang.String r5 = "Reading from backup stopped packages file\n"
            r0.append(r5)     // Catch:{ IOException -> 0x0046 }
            java.lang.String r0 = "Need to read from backup stopped packages file"
            com.android.server.pm.PackageManagerService.reportSettingsProblem(r3, r0)     // Catch:{ IOException -> 0x0046 }
            java.io.File r0 = r1.mSettingsFilename     // Catch:{ IOException -> 0x0046 }
            boolean r0 = r0.exists()     // Catch:{ IOException -> 0x0046 }
            if (r0 == 0) goto L_0x0045
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ IOException -> 0x0046 }
            r0.<init>()     // Catch:{ IOException -> 0x0046 }
            java.lang.String r5 = "Cleaning up stopped packages file "
            r0.append(r5)     // Catch:{ IOException -> 0x0046 }
            java.io.File r5 = r1.mStoppedPackagesFilename     // Catch:{ IOException -> 0x0046 }
            r0.append(r5)     // Catch:{ IOException -> 0x0046 }
            java.lang.String r0 = r0.toString()     // Catch:{ IOException -> 0x0046 }
            android.util.Slog.w(r4, r0)     // Catch:{ IOException -> 0x0046 }
            java.io.File r0 = r1.mStoppedPackagesFilename     // Catch:{ IOException -> 0x0046 }
            r0.delete()     // Catch:{ IOException -> 0x0046 }
        L_0x0045:
            goto L_0x0047
        L_0x0046:
            r0 = move-exception
        L_0x0047:
            java.lang.String r5 = "Error reading package manager stopped packages"
            java.lang.String r7 = "Error reading: "
            r0 = 0
            if (r2 != 0) goto L_0x0088
            java.io.File r8 = r1.mStoppedPackagesFilename     // Catch:{ XmlPullParserException -> 0x0165, IOException -> 0x0133 }
            boolean r8 = r8.exists()     // Catch:{ XmlPullParserException -> 0x0165, IOException -> 0x0133 }
            if (r8 != 0) goto L_0x0080
            java.lang.StringBuilder r8 = r1.mReadMessages     // Catch:{ XmlPullParserException -> 0x0165, IOException -> 0x0133 }
            java.lang.String r9 = "No stopped packages file found\n"
            r8.append(r9)     // Catch:{ XmlPullParserException -> 0x0165, IOException -> 0x0133 }
            java.lang.String r8 = "No stopped packages file file; assuming all started"
            com.android.server.pm.PackageManagerService.reportSettingsProblem(r3, r8)     // Catch:{ XmlPullParserException -> 0x0165, IOException -> 0x0133 }
            android.util.ArrayMap<java.lang.String, com.android.server.pm.PackageSetting> r3 = r1.mPackages     // Catch:{ XmlPullParserException -> 0x0165, IOException -> 0x0133 }
            java.util.Collection r3 = r3.values()     // Catch:{ XmlPullParserException -> 0x0165, IOException -> 0x0133 }
            java.util.Iterator r3 = r3.iterator()     // Catch:{ XmlPullParserException -> 0x0165, IOException -> 0x0133 }
        L_0x006c:
            boolean r8 = r3.hasNext()     // Catch:{ XmlPullParserException -> 0x0165, IOException -> 0x0133 }
            if (r8 == 0) goto L_0x007f
            java.lang.Object r8 = r3.next()     // Catch:{ XmlPullParserException -> 0x0165, IOException -> 0x0133 }
            com.android.server.pm.PackageSetting r8 = (com.android.server.pm.PackageSetting) r8     // Catch:{ XmlPullParserException -> 0x0165, IOException -> 0x0133 }
            r8.setStopped(r0, r0)     // Catch:{ XmlPullParserException -> 0x0165, IOException -> 0x0133 }
            r8.setNotLaunched(r0, r0)     // Catch:{ XmlPullParserException -> 0x0165, IOException -> 0x0133 }
            goto L_0x006c
        L_0x007f:
            return
        L_0x0080:
            java.io.FileInputStream r8 = new java.io.FileInputStream     // Catch:{ XmlPullParserException -> 0x0165, IOException -> 0x0133 }
            java.io.File r9 = r1.mStoppedPackagesFilename     // Catch:{ XmlPullParserException -> 0x0165, IOException -> 0x0133 }
            r8.<init>(r9)     // Catch:{ XmlPullParserException -> 0x0165, IOException -> 0x0133 }
            r2 = r8
        L_0x0088:
            org.xmlpull.v1.XmlPullParser r8 = android.util.Xml.newPullParser()     // Catch:{ XmlPullParserException -> 0x0165, IOException -> 0x0133 }
            r9 = 0
            r8.setInput(r2, r9)     // Catch:{ XmlPullParserException -> 0x0165, IOException -> 0x0133 }
        L_0x0090:
            int r10 = r8.next()     // Catch:{ XmlPullParserException -> 0x0165, IOException -> 0x0133 }
            r11 = r10
            r12 = 2
            r13 = 1
            if (r10 == r12) goto L_0x009c
            if (r11 == r13) goto L_0x009c
            goto L_0x0090
        L_0x009c:
            if (r11 == r12) goto L_0x00ac
            java.lang.StringBuilder r0 = r1.mReadMessages     // Catch:{ XmlPullParserException -> 0x0165, IOException -> 0x0133 }
            java.lang.String r3 = "No start tag found in stopped packages file\n"
            r0.append(r3)     // Catch:{ XmlPullParserException -> 0x0165, IOException -> 0x0133 }
            r0 = 5
            java.lang.String r3 = "No start tag found in package manager stopped packages"
            com.android.server.pm.PackageManagerService.reportSettingsProblem(r0, r3)     // Catch:{ XmlPullParserException -> 0x0165, IOException -> 0x0133 }
            return
        L_0x00ac:
            int r10 = r8.getDepth()     // Catch:{ XmlPullParserException -> 0x0165, IOException -> 0x0133 }
        L_0x00b0:
            int r12 = r8.next()     // Catch:{ XmlPullParserException -> 0x0165, IOException -> 0x0133 }
            r11 = r12
            if (r12 == r13) goto L_0x012f
            r12 = 3
            if (r11 != r12) goto L_0x00c0
            int r14 = r8.getDepth()     // Catch:{ XmlPullParserException -> 0x0165, IOException -> 0x0133 }
            if (r14 <= r10) goto L_0x012f
        L_0x00c0:
            if (r11 == r12) goto L_0x012d
            if (r11 != r3) goto L_0x00c5
            goto L_0x012d
        L_0x00c5:
            java.lang.String r12 = r8.getName()     // Catch:{ XmlPullParserException -> 0x0165, IOException -> 0x0133 }
            java.lang.String r14 = "pkg"
            boolean r14 = r12.equals(r14)     // Catch:{ XmlPullParserException -> 0x0165, IOException -> 0x0133 }
            if (r14 == 0) goto L_0x0111
            java.lang.String r14 = "name"
            java.lang.String r14 = r8.getAttributeValue(r9, r14)     // Catch:{ XmlPullParserException -> 0x0165, IOException -> 0x0133 }
            android.util.ArrayMap<java.lang.String, com.android.server.pm.PackageSetting> r15 = r1.mPackages     // Catch:{ XmlPullParserException -> 0x0165, IOException -> 0x0133 }
            java.lang.Object r15 = r15.get(r14)     // Catch:{ XmlPullParserException -> 0x0165, IOException -> 0x0133 }
            com.android.server.pm.PackageSetting r15 = (com.android.server.pm.PackageSetting) r15     // Catch:{ XmlPullParserException -> 0x0165, IOException -> 0x0133 }
            if (r15 == 0) goto L_0x00f9
            r15.setStopped(r13, r0)     // Catch:{ XmlPullParserException -> 0x0165, IOException -> 0x0133 }
            java.lang.String r3 = "1"
            java.lang.String r6 = "nl"
            java.lang.String r6 = r8.getAttributeValue(r9, r6)     // Catch:{ XmlPullParserException -> 0x0165, IOException -> 0x0133 }
            boolean r3 = r3.equals(r6)     // Catch:{ XmlPullParserException -> 0x0165, IOException -> 0x0133 }
            if (r3 == 0) goto L_0x010d
            r15.setNotLaunched(r13, r0)     // Catch:{ XmlPullParserException -> 0x0165, IOException -> 0x0133 }
            goto L_0x010d
        L_0x00f9:
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ XmlPullParserException -> 0x0165, IOException -> 0x0133 }
            r3.<init>()     // Catch:{ XmlPullParserException -> 0x0165, IOException -> 0x0133 }
            java.lang.String r6 = "No package known for stopped package "
            r3.append(r6)     // Catch:{ XmlPullParserException -> 0x0165, IOException -> 0x0133 }
            r3.append(r14)     // Catch:{ XmlPullParserException -> 0x0165, IOException -> 0x0133 }
            java.lang.String r3 = r3.toString()     // Catch:{ XmlPullParserException -> 0x0165, IOException -> 0x0133 }
            android.util.Slog.w(r4, r3)     // Catch:{ XmlPullParserException -> 0x0165, IOException -> 0x0133 }
        L_0x010d:
            com.android.internal.util.XmlUtils.skipCurrentTag(r8)     // Catch:{ XmlPullParserException -> 0x0165, IOException -> 0x0133 }
            goto L_0x012c
        L_0x0111:
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ XmlPullParserException -> 0x0165, IOException -> 0x0133 }
            r3.<init>()     // Catch:{ XmlPullParserException -> 0x0165, IOException -> 0x0133 }
            java.lang.String r6 = "Unknown element under <stopped-packages>: "
            r3.append(r6)     // Catch:{ XmlPullParserException -> 0x0165, IOException -> 0x0133 }
            java.lang.String r6 = r8.getName()     // Catch:{ XmlPullParserException -> 0x0165, IOException -> 0x0133 }
            r3.append(r6)     // Catch:{ XmlPullParserException -> 0x0165, IOException -> 0x0133 }
            java.lang.String r3 = r3.toString()     // Catch:{ XmlPullParserException -> 0x0165, IOException -> 0x0133 }
            android.util.Slog.w(r4, r3)     // Catch:{ XmlPullParserException -> 0x0165, IOException -> 0x0133 }
            com.android.internal.util.XmlUtils.skipCurrentTag(r8)     // Catch:{ XmlPullParserException -> 0x0165, IOException -> 0x0133 }
        L_0x012c:
        L_0x012d:
            r3 = 4
            goto L_0x00b0
        L_0x012f:
            r2.close()     // Catch:{ XmlPullParserException -> 0x0165, IOException -> 0x0133 }
            goto L_0x0196
        L_0x0133:
            r0 = move-exception
            java.lang.StringBuilder r3 = r1.mReadMessages
            java.lang.StringBuilder r6 = new java.lang.StringBuilder
            r6.<init>()
            r6.append(r7)
            java.lang.String r7 = r0.toString()
            r6.append(r7)
            java.lang.String r6 = r6.toString()
            r3.append(r6)
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            java.lang.String r6 = "Error reading settings: "
            r3.append(r6)
            r3.append(r0)
            java.lang.String r3 = r3.toString()
            r6 = 6
            com.android.server.pm.PackageManagerService.reportSettingsProblem(r6, r3)
            android.util.Slog.wtf(r4, r5, r0)
            goto L_0x0197
        L_0x0165:
            r0 = move-exception
            java.lang.StringBuilder r3 = r1.mReadMessages
            java.lang.StringBuilder r6 = new java.lang.StringBuilder
            r6.<init>()
            r6.append(r7)
            java.lang.String r7 = r0.toString()
            r6.append(r7)
            java.lang.String r6 = r6.toString()
            r3.append(r6)
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            java.lang.String r6 = "Error reading stopped packages: "
            r3.append(r6)
            r3.append(r0)
            java.lang.String r3 = r3.toString()
            r6 = 6
            com.android.server.pm.PackageManagerService.reportSettingsProblem(r6, r3)
            android.util.Slog.wtf(r4, r5, r0)
        L_0x0196:
        L_0x0197:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.pm.Settings.readStoppedLPw():void");
    }

    /* access modifiers changed from: package-private */
    /* JADX WARNING: Removed duplicated region for block: B:66:0x025b  */
    /* JADX WARNING: Removed duplicated region for block: B:75:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void writeLPr() {
        /*
            r22 = this;
            r1 = r22
            java.lang.String r0 = "renamed-package"
            java.lang.String r2 = "restored-ivi"
            java.lang.String r3 = "shared-user"
            java.lang.String r4 = "permissions"
            java.lang.String r5 = "version"
            java.lang.String r6 = "read-external-storage"
            java.lang.String r7 = "permission-trees"
            java.lang.String r8 = "verifier"
            java.lang.String r9 = "packages"
            long r10 = android.os.SystemClock.uptimeMillis()
            java.io.File r12 = r1.mSettingsFilename
            boolean r12 = r12.exists()
            java.lang.String r13 = "PackageManager"
            if (r12 == 0) goto L_0x004d
            java.io.File r12 = r1.mBackupSettingsFilename
            boolean r12 = r12.exists()
            if (r12 != 0) goto L_0x0043
            java.io.File r12 = r1.mSettingsFilename
            java.io.File r14 = r1.mBackupSettingsFilename
            boolean r12 = r12.renameTo(r14)
            if (r12 != 0) goto L_0x004d
            java.lang.String r0 = "Unable to backup package manager settings,  current changes will be lost at reboot"
            android.util.Slog.wtf(r13, r0)
            return
        L_0x0043:
            java.io.File r12 = r1.mSettingsFilename
            r12.delete()
            java.lang.String r12 = "Preserving older settings backup"
            android.util.Slog.w(r13, r12)
        L_0x004d:
            java.util.ArrayList<android.content.pm.Signature> r12 = r1.mPastSignatures
            r12.clear()
            java.io.FileOutputStream r12 = new java.io.FileOutputStream     // Catch:{ IOException -> 0x023f }
            java.io.File r14 = r1.mSettingsFilename     // Catch:{ IOException -> 0x023f }
            r12.<init>(r14)     // Catch:{ IOException -> 0x023f }
            java.io.BufferedOutputStream r14 = new java.io.BufferedOutputStream     // Catch:{ IOException -> 0x023f }
            r14.<init>(r12)     // Catch:{ IOException -> 0x023f }
            com.android.internal.util.FastXmlSerializer r15 = new com.android.internal.util.FastXmlSerializer     // Catch:{ IOException -> 0x023f }
            r15.<init>()     // Catch:{ IOException -> 0x023f }
            java.nio.charset.Charset r16 = java.nio.charset.StandardCharsets.UTF_8     // Catch:{ IOException -> 0x023f }
            r17 = r13
            java.lang.String r13 = r16.name()     // Catch:{ IOException -> 0x023b }
            r15.setOutput(r14, r13)     // Catch:{ IOException -> 0x023b }
            r13 = 1
            r18 = r10
            java.lang.Boolean r10 = java.lang.Boolean.valueOf(r13)     // Catch:{ IOException -> 0x0239 }
            r11 = 0
            r15.startDocument(r11, r10)     // Catch:{ IOException -> 0x0239 }
            java.lang.String r10 = "http://xmlpull.org/v1/doc/features.html#indent-output"
            r15.setFeature(r10, r13)     // Catch:{ IOException -> 0x0239 }
            r15.startTag(r11, r9)     // Catch:{ IOException -> 0x0239 }
            r10 = 0
        L_0x0083:
            android.util.ArrayMap<java.lang.String, com.android.server.pm.Settings$VersionInfo> r13 = r1.mVersion     // Catch:{ IOException -> 0x0239 }
            int r13 = r13.size()     // Catch:{ IOException -> 0x0239 }
            if (r10 >= r13) goto L_0x00ca
            android.util.ArrayMap<java.lang.String, com.android.server.pm.Settings$VersionInfo> r13 = r1.mVersion     // Catch:{ IOException -> 0x0239 }
            java.lang.Object r13 = r13.keyAt(r10)     // Catch:{ IOException -> 0x0239 }
            java.lang.String r13 = (java.lang.String) r13     // Catch:{ IOException -> 0x0239 }
            android.util.ArrayMap<java.lang.String, com.android.server.pm.Settings$VersionInfo> r11 = r1.mVersion     // Catch:{ IOException -> 0x0239 }
            java.lang.Object r11 = r11.valueAt(r10)     // Catch:{ IOException -> 0x0239 }
            com.android.server.pm.Settings$VersionInfo r11 = (com.android.server.pm.Settings.VersionInfo) r11     // Catch:{ IOException -> 0x0239 }
            r20 = r12
            r12 = 0
            r15.startTag(r12, r5)     // Catch:{ IOException -> 0x0239 }
            java.lang.String r12 = "volumeUuid"
            com.android.internal.util.XmlUtils.writeStringAttribute(r15, r12, r13)     // Catch:{ IOException -> 0x0239 }
            java.lang.String r12 = "sdkVersion"
            r21 = r13
            int r13 = r11.sdkVersion     // Catch:{ IOException -> 0x0239 }
            com.android.internal.util.XmlUtils.writeIntAttribute(r15, r12, r13)     // Catch:{ IOException -> 0x0239 }
            java.lang.String r12 = "databaseVersion"
            int r13 = r11.databaseVersion     // Catch:{ IOException -> 0x0239 }
            com.android.internal.util.XmlUtils.writeIntAttribute(r15, r12, r13)     // Catch:{ IOException -> 0x0239 }
            java.lang.String r12 = "fingerprint"
            java.lang.String r13 = r11.fingerprint     // Catch:{ IOException -> 0x0239 }
            com.android.internal.util.XmlUtils.writeStringAttribute(r15, r12, r13)     // Catch:{ IOException -> 0x0239 }
            r12 = 0
            r15.endTag(r12, r5)     // Catch:{ IOException -> 0x0239 }
            int r10 = r10 + 1
            r12 = r20
            r11 = 0
            goto L_0x0083
        L_0x00ca:
            r20 = r12
            android.content.pm.VerifierDeviceIdentity r5 = r1.mVerifierDeviceIdentity     // Catch:{ IOException -> 0x0239 }
            if (r5 == 0) goto L_0x00e3
            r5 = 0
            r15.startTag(r5, r8)     // Catch:{ IOException -> 0x0239 }
            java.lang.String r5 = "device"
            android.content.pm.VerifierDeviceIdentity r10 = r1.mVerifierDeviceIdentity     // Catch:{ IOException -> 0x0239 }
            java.lang.String r10 = r10.toString()     // Catch:{ IOException -> 0x0239 }
            r11 = 0
            r15.attribute(r11, r5, r10)     // Catch:{ IOException -> 0x0239 }
            r15.endTag(r11, r8)     // Catch:{ IOException -> 0x0239 }
        L_0x00e3:
            java.lang.Boolean r5 = r1.mReadExternalStorageEnforced     // Catch:{ IOException -> 0x0239 }
            if (r5 == 0) goto L_0x0101
            r5 = 0
            r15.startTag(r5, r6)     // Catch:{ IOException -> 0x0239 }
            java.lang.String r5 = "enforcement"
            java.lang.Boolean r8 = r1.mReadExternalStorageEnforced     // Catch:{ IOException -> 0x0239 }
            boolean r8 = r8.booleanValue()     // Catch:{ IOException -> 0x0239 }
            if (r8 == 0) goto L_0x00f8
            java.lang.String r8 = "1"
            goto L_0x00fa
        L_0x00f8:
            java.lang.String r8 = "0"
        L_0x00fa:
            r10 = 0
            r15.attribute(r10, r5, r8)     // Catch:{ IOException -> 0x0239 }
            r15.endTag(r10, r6)     // Catch:{ IOException -> 0x0239 }
        L_0x0101:
            r5 = 0
            r15.startTag(r5, r7)     // Catch:{ IOException -> 0x0239 }
            com.android.server.pm.permission.PermissionSettings r5 = r1.mPermissions     // Catch:{ IOException -> 0x0239 }
            r5.writePermissionTrees(r15)     // Catch:{ IOException -> 0x0239 }
            r5 = 0
            r15.endTag(r5, r7)     // Catch:{ IOException -> 0x0239 }
            r15.startTag(r5, r4)     // Catch:{ IOException -> 0x0239 }
            com.android.server.pm.permission.PermissionSettings r5 = r1.mPermissions     // Catch:{ IOException -> 0x0239 }
            r5.writePermissions(r15)     // Catch:{ IOException -> 0x0239 }
            r5 = 0
            r15.endTag(r5, r4)     // Catch:{ IOException -> 0x0239 }
            android.util.ArrayMap<java.lang.String, com.android.server.pm.PackageSetting> r4 = r1.mPackages     // Catch:{ IOException -> 0x0239 }
            java.util.Collection r4 = r4.values()     // Catch:{ IOException -> 0x0239 }
            java.util.Iterator r4 = r4.iterator()     // Catch:{ IOException -> 0x0239 }
        L_0x0124:
            boolean r5 = r4.hasNext()     // Catch:{ IOException -> 0x0239 }
            if (r5 == 0) goto L_0x0134
            java.lang.Object r5 = r4.next()     // Catch:{ IOException -> 0x0239 }
            com.android.server.pm.PackageSetting r5 = (com.android.server.pm.PackageSetting) r5     // Catch:{ IOException -> 0x0239 }
            r1.writePackageLPr(r15, r5)     // Catch:{ IOException -> 0x0239 }
            goto L_0x0124
        L_0x0134:
            android.util.ArrayMap<java.lang.String, com.android.server.pm.PackageSetting> r4 = r1.mDisabledSysPackages     // Catch:{ IOException -> 0x0239 }
            java.util.Collection r4 = r4.values()     // Catch:{ IOException -> 0x0239 }
            java.util.Iterator r4 = r4.iterator()     // Catch:{ IOException -> 0x0239 }
        L_0x013e:
            boolean r5 = r4.hasNext()     // Catch:{ IOException -> 0x0239 }
            if (r5 == 0) goto L_0x014e
            java.lang.Object r5 = r4.next()     // Catch:{ IOException -> 0x0239 }
            com.android.server.pm.PackageSetting r5 = (com.android.server.pm.PackageSetting) r5     // Catch:{ IOException -> 0x0239 }
            r1.writeDisabledSysPackageLPr(r15, r5)     // Catch:{ IOException -> 0x0239 }
            goto L_0x013e
        L_0x014e:
            android.util.ArrayMap<java.lang.String, com.android.server.pm.SharedUserSetting> r4 = r1.mSharedUsers     // Catch:{ IOException -> 0x0239 }
            java.util.Collection r4 = r4.values()     // Catch:{ IOException -> 0x0239 }
            java.util.Iterator r4 = r4.iterator()     // Catch:{ IOException -> 0x0239 }
        L_0x0158:
            boolean r5 = r4.hasNext()     // Catch:{ IOException -> 0x0239 }
            if (r5 == 0) goto L_0x0198
            java.lang.Object r5 = r4.next()     // Catch:{ IOException -> 0x0239 }
            com.android.server.pm.SharedUserSetting r5 = (com.android.server.pm.SharedUserSetting) r5     // Catch:{ IOException -> 0x0239 }
            r6 = 0
            r15.startTag(r6, r3)     // Catch:{ IOException -> 0x0239 }
            java.lang.String r7 = "name"
            java.lang.String r8 = r5.name     // Catch:{ IOException -> 0x0239 }
            r15.attribute(r6, r7, r8)     // Catch:{ IOException -> 0x0239 }
            java.lang.String r6 = "userId"
            int r7 = r5.userId     // Catch:{ IOException -> 0x0239 }
            java.lang.String r7 = java.lang.Integer.toString(r7)     // Catch:{ IOException -> 0x0239 }
            r8 = 0
            r15.attribute(r8, r6, r7)     // Catch:{ IOException -> 0x0239 }
            com.android.server.pm.PackageSignatures r6 = r5.signatures     // Catch:{ IOException -> 0x0239 }
            java.lang.String r7 = "sigs"
            java.util.ArrayList<android.content.pm.Signature> r8 = r1.mPastSignatures     // Catch:{ IOException -> 0x0239 }
            r6.writeXml(r15, r7, r8)     // Catch:{ IOException -> 0x0239 }
            com.android.server.pm.permission.PermissionsState r6 = r5.getPermissionsState()     // Catch:{ IOException -> 0x0239 }
            java.util.List r6 = r6.getInstallPermissionStates()     // Catch:{ IOException -> 0x0239 }
            r1.writePermissionsLPr(r15, r6)     // Catch:{ IOException -> 0x0239 }
            r6 = 0
            r15.endTag(r6, r3)     // Catch:{ IOException -> 0x0239 }
            goto L_0x0158
        L_0x0198:
            android.util.ArrayMap<java.lang.String, java.lang.String> r3 = r1.mRenamedPackages     // Catch:{ IOException -> 0x0239 }
            int r3 = r3.size()     // Catch:{ IOException -> 0x0239 }
            if (r3 <= 0) goto L_0x01d9
            android.util.ArrayMap<java.lang.String, java.lang.String> r3 = r1.mRenamedPackages     // Catch:{ IOException -> 0x0239 }
            java.util.Set r3 = r3.entrySet()     // Catch:{ IOException -> 0x0239 }
            java.util.Iterator r3 = r3.iterator()     // Catch:{ IOException -> 0x0239 }
        L_0x01aa:
            boolean r4 = r3.hasNext()     // Catch:{ IOException -> 0x0239 }
            if (r4 == 0) goto L_0x01d9
            java.lang.Object r4 = r3.next()     // Catch:{ IOException -> 0x0239 }
            java.util.Map$Entry r4 = (java.util.Map.Entry) r4     // Catch:{ IOException -> 0x0239 }
            r5 = 0
            r15.startTag(r5, r0)     // Catch:{ IOException -> 0x0239 }
            java.lang.String r5 = "new"
            java.lang.Object r6 = r4.getKey()     // Catch:{ IOException -> 0x0239 }
            java.lang.String r6 = (java.lang.String) r6     // Catch:{ IOException -> 0x0239 }
            r7 = 0
            r15.attribute(r7, r5, r6)     // Catch:{ IOException -> 0x0239 }
            java.lang.String r5 = "old"
            java.lang.Object r6 = r4.getValue()     // Catch:{ IOException -> 0x0239 }
            java.lang.String r6 = (java.lang.String) r6     // Catch:{ IOException -> 0x0239 }
            r7 = 0
            r15.attribute(r7, r5, r6)     // Catch:{ IOException -> 0x0239 }
            r15.endTag(r7, r0)     // Catch:{ IOException -> 0x0239 }
            goto L_0x01aa
        L_0x01d9:
            android.util.ArrayMap<java.lang.String, android.content.pm.IntentFilterVerificationInfo> r0 = r1.mRestoredIntentFilterVerifications     // Catch:{ IOException -> 0x0239 }
            int r0 = r0.size()     // Catch:{ IOException -> 0x0239 }
            if (r0 <= 0) goto L_0x01fa
            r3 = 0
            r15.startTag(r3, r2)     // Catch:{ IOException -> 0x0239 }
            r3 = 0
        L_0x01e6:
            if (r3 >= r0) goto L_0x01f6
            android.util.ArrayMap<java.lang.String, android.content.pm.IntentFilterVerificationInfo> r4 = r1.mRestoredIntentFilterVerifications     // Catch:{ IOException -> 0x0239 }
            java.lang.Object r4 = r4.valueAt(r3)     // Catch:{ IOException -> 0x0239 }
            android.content.pm.IntentFilterVerificationInfo r4 = (android.content.pm.IntentFilterVerificationInfo) r4     // Catch:{ IOException -> 0x0239 }
            r1.writeDomainVerificationsLPr(r15, r4)     // Catch:{ IOException -> 0x0239 }
            int r3 = r3 + 1
            goto L_0x01e6
        L_0x01f6:
            r3 = 0
            r15.endTag(r3, r2)     // Catch:{ IOException -> 0x0239 }
        L_0x01fa:
            com.android.server.pm.KeySetManagerService r2 = r1.mKeySetManagerService     // Catch:{ IOException -> 0x0239 }
            r2.writeKeySetManagerServiceLPr(r15)     // Catch:{ IOException -> 0x0239 }
            r2 = 0
            r15.endTag(r2, r9)     // Catch:{ IOException -> 0x0239 }
            r15.endDocument()     // Catch:{ IOException -> 0x0239 }
            r14.flush()     // Catch:{ IOException -> 0x0239 }
            android.os.FileUtils.sync(r20)     // Catch:{ IOException -> 0x0239 }
            r14.close()     // Catch:{ IOException -> 0x0239 }
            java.io.File r2 = r1.mBackupSettingsFilename     // Catch:{ IOException -> 0x0239 }
            r2.delete()     // Catch:{ IOException -> 0x0239 }
            java.io.File r2 = r1.mSettingsFilename     // Catch:{ IOException -> 0x0239 }
            java.lang.String r2 = r2.toString()     // Catch:{ IOException -> 0x0239 }
            r3 = 432(0x1b0, float:6.05E-43)
            r4 = -1
            android.os.FileUtils.setPermissions(r2, r3, r4, r4)     // Catch:{ IOException -> 0x0239 }
            r22.writeKernelMappingLPr()     // Catch:{ IOException -> 0x0239 }
            r22.writePackageListLPr()     // Catch:{ IOException -> 0x0239 }
            r22.writeAllUsersPackageRestrictionsLPr()     // Catch:{ IOException -> 0x0239 }
            r22.writeAllRuntimePermissionsLPr()     // Catch:{ IOException -> 0x0239 }
            java.lang.String r2 = "package"
            long r3 = android.os.SystemClock.uptimeMillis()     // Catch:{ IOException -> 0x0239 }
            long r3 = r3 - r18
            com.android.internal.logging.EventLogTags.writeCommitSysConfigFile(r2, r3)     // Catch:{ IOException -> 0x0239 }
            return
        L_0x0239:
            r0 = move-exception
            goto L_0x0244
        L_0x023b:
            r0 = move-exception
            r18 = r10
            goto L_0x0244
        L_0x023f:
            r0 = move-exception
            r18 = r10
            r17 = r13
        L_0x0244:
            java.lang.String r2 = "Unable to write package manager settings, current changes will be lost at reboot"
            r3 = r17
            android.util.Slog.wtf(r3, r2, r0)
            java.io.File r0 = r1.mSettingsFilename
            boolean r0 = r0.exists()
            if (r0 == 0) goto L_0x0271
            java.io.File r0 = r1.mSettingsFilename
            boolean r0 = r0.delete()
            if (r0 != 0) goto L_0x0271
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r2 = "Failed to clean up mangled file: "
            r0.append(r2)
            java.io.File r2 = r1.mSettingsFilename
            r0.append(r2)
            java.lang.String r0 = r0.toString()
            android.util.Slog.wtf(r3, r0)
        L_0x0271:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.pm.Settings.writeLPr():void");
    }

    private void writeKernelRemoveUserLPr(int userId) {
        File file = this.mKernelMappingFilename;
        if (file != null) {
            writeIntToFile(new File(file, "remove_userid"), userId);
        }
    }

    /* access modifiers changed from: package-private */
    public void writeKernelMappingLPr() {
        File file = this.mKernelMappingFilename;
        if (file != null) {
            String[] known = file.list();
            ArraySet<String> knownSet = new ArraySet<>(known.length);
            for (String name : known) {
                knownSet.add(name);
            }
            for (PackageSetting ps : this.mPackages.values()) {
                knownSet.remove(ps.name);
                writeKernelMappingLPr(ps);
            }
            for (int i = 0; i < knownSet.size(); i++) {
                String name2 = knownSet.valueAt(i);
                this.mKernelMapping.remove(name2);
                new File(this.mKernelMappingFilename, name2).delete();
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void writeKernelMappingLPr(PackageSetting ps) {
        if (this.mKernelMappingFilename != null && ps != null && ps.name != null) {
            writeKernelMappingLPr(ps.name, ps.appId, ps.getNotInstalledUserIds());
        }
    }

    /* access modifiers changed from: package-private */
    public void writeKernelMappingLPr(String name, int appId, int[] excludedUserIds) {
        KernelPackageState cur = this.mKernelMapping.get(name);
        boolean userIdsChanged = false;
        boolean firstTime = cur == null;
        if (firstTime || !Arrays.equals(excludedUserIds, cur.excludedUserIds)) {
            userIdsChanged = true;
        }
        File dir = new File(this.mKernelMappingFilename, name);
        if (firstTime) {
            dir.mkdir();
            cur = new KernelPackageState();
            this.mKernelMapping.put(name, cur);
        }
        if (cur.appId != appId) {
            writeIntToFile(new File(dir, "appid"), appId);
        }
        if (userIdsChanged) {
            for (int i = 0; i < excludedUserIds.length; i++) {
                if (cur.excludedUserIds == null || !ArrayUtils.contains(cur.excludedUserIds, excludedUserIds[i])) {
                    writeIntToFile(new File(dir, "excluded_userids"), excludedUserIds[i]);
                }
            }
            if (cur.excludedUserIds != null) {
                for (int i2 = 0; i2 < cur.excludedUserIds.length; i2++) {
                    if (!ArrayUtils.contains(excludedUserIds, cur.excludedUserIds[i2])) {
                        writeIntToFile(new File(dir, "clear_userid"), cur.excludedUserIds[i2]);
                    }
                }
            }
            cur.excludedUserIds = excludedUserIds;
        }
    }

    private void writeIntToFile(File file, int value) {
        try {
            FileUtils.bytesToFile(file.getAbsolutePath(), Integer.toString(value).getBytes(StandardCharsets.US_ASCII));
        } catch (IOException e) {
            Slog.w(TAG, "Couldn't write " + value + " to " + file.getAbsolutePath());
        }
    }

    /* access modifiers changed from: package-private */
    public void writePackageListLPr() {
        writePackageListLPr(-1);
    }

    /* access modifiers changed from: package-private */
    public void writePackageListLPr(int creatingUserId) {
        String ctx = SELinux.fileSelabelLookup(this.mPackageListFilename.getAbsolutePath());
        if (ctx == null) {
            Slog.wtf(TAG, "Failed to get SELinux context for " + this.mPackageListFilename.getAbsolutePath());
        }
        if (!SELinux.setFSCreateContext(ctx)) {
            Slog.wtf(TAG, "Failed to set packages.list SELinux context");
        }
        try {
            writePackageListLPrInternal(creatingUserId);
        } finally {
            SELinux.setFSCreateContext((String) null);
        }
    }

    private void writePackageListLPrInternal(int creatingUserId) {
        List<UserInfo> users;
        String str;
        int i = creatingUserId;
        String str2 = " ";
        List<UserInfo> users2 = getUsers(UserManagerService.getInstance(), true);
        int[] userIds = new int[users2.size()];
        for (int i2 = 0; i2 < userIds.length; i2++) {
            userIds[i2] = users2.get(i2).id;
        }
        if (i != -1) {
            userIds = ArrayUtils.appendInt(userIds, i);
        }
        JournaledFile journal = new JournaledFile(this.mPackageListFilename, new File(this.mPackageListFilename.getAbsolutePath() + ".tmp"));
        BufferedWriter writer = null;
        try {
            FileOutputStream fstr = new FileOutputStream(journal.chooseForWrite());
            writer = new BufferedWriter(new OutputStreamWriter(fstr, Charset.defaultCharset()));
            FileUtils.setPermissions(fstr.getFD(), 416, 1000, 1032);
            StringBuilder sb = new StringBuilder();
            for (PackageSetting pkg : this.mPackages.values()) {
                if (pkg.pkg == null || pkg.pkg.applicationInfo == null) {
                    users = users2;
                    str = str2;
                } else if (pkg.pkg.applicationInfo.dataDir == null) {
                    users = users2;
                    str = str2;
                } else {
                    ApplicationInfo ai = pkg.pkg.applicationInfo;
                    String dataPath = ai.dataDir;
                    boolean isDebug = (ai.flags & 2) != 0;
                    int[] gids = pkg.getPermissionsState().computeGids(userIds);
                    List<UserInfo> users3 = users2;
                    try {
                        if (dataPath.indexOf(32) >= 0) {
                            int i3 = creatingUserId;
                            users2 = users3;
                        } else {
                            sb.setLength(0);
                            sb.append(ai.packageName);
                            sb.append(str2);
                            sb.append(ai.uid);
                            sb.append(isDebug ? " 1 " : " 0 ");
                            sb.append(dataPath);
                            sb.append(str2);
                            sb.append(ai.seInfo);
                            sb.append(str2);
                            if (gids != null && gids.length > 0) {
                                sb.append(gids[0]);
                                int i4 = 1;
                                while (true) {
                                    boolean isDebug2 = isDebug;
                                    if (i4 >= gids.length) {
                                        break;
                                    }
                                    sb.append(",");
                                    sb.append(gids[i4]);
                                    i4++;
                                    isDebug = isDebug2;
                                }
                            } else {
                                sb.append("none");
                            }
                            sb.append(str2);
                            sb.append(ai.isProfileableByShell() ? SplitScreenReporter.ACTION_ENTER_SPLIT : "0");
                            sb.append(str2);
                            sb.append(String.valueOf(ai.longVersionCode));
                            sb.append("\n");
                            writer.append(sb);
                            int i5 = creatingUserId;
                            str2 = str2;
                            users2 = users3;
                        }
                    } catch (Exception e) {
                        e = e;
                        Slog.wtf(TAG, "Failed to write packages.list", e);
                        IoUtils.closeQuietly(writer);
                        journal.rollback();
                    }
                }
                if (!PackageManagerService.PLATFORM_PACKAGE_NAME.equals(pkg.name)) {
                    Slog.w(TAG, "Skipping " + pkg + " due to missing metadata");
                    int i6 = creatingUserId;
                    str2 = str;
                    users2 = users;
                } else {
                    int i7 = creatingUserId;
                    str2 = str;
                    users2 = users;
                }
            }
            writer.flush();
            FileUtils.sync(fstr);
            writer.close();
            journal.commit();
        } catch (Exception e2) {
            e = e2;
            List<UserInfo> list = users2;
            Slog.wtf(TAG, "Failed to write packages.list", e);
            IoUtils.closeQuietly(writer);
            journal.rollback();
        }
    }

    /* access modifiers changed from: package-private */
    public void writeDisabledSysPackageLPr(XmlSerializer serializer, PackageSetting pkg) throws IOException {
        serializer.startTag((String) null, "updated-package");
        serializer.attribute((String) null, ATTR_NAME, pkg.name);
        if (pkg.realName != null) {
            serializer.attribute((String) null, "realName", pkg.realName);
        }
        serializer.attribute((String) null, "codePath", pkg.codePathString);
        serializer.attribute((String) null, "ft", Long.toHexString(pkg.timeStamp));
        serializer.attribute((String) null, "it", Long.toHexString(pkg.firstInstallTime));
        serializer.attribute((String) null, "ut", Long.toHexString(pkg.lastUpdateTime));
        serializer.attribute((String) null, "version", String.valueOf(pkg.versionCode));
        if (!pkg.resourcePathString.equals(pkg.codePathString)) {
            serializer.attribute((String) null, "resourcePath", pkg.resourcePathString);
        }
        if (pkg.legacyNativeLibraryPathString != null) {
            serializer.attribute((String) null, "nativeLibraryPath", pkg.legacyNativeLibraryPathString);
        }
        if (pkg.primaryCpuAbiString != null) {
            serializer.attribute((String) null, "primaryCpuAbi", pkg.primaryCpuAbiString);
        }
        if (pkg.secondaryCpuAbiString != null) {
            serializer.attribute((String) null, "secondaryCpuAbi", pkg.secondaryCpuAbiString);
        }
        if (pkg.cpuAbiOverrideString != null) {
            serializer.attribute((String) null, "cpuAbiOverride", pkg.cpuAbiOverrideString);
        }
        if (pkg.sharedUser == null) {
            serializer.attribute((String) null, "userId", Integer.toString(pkg.appId));
        } else {
            serializer.attribute((String) null, "sharedUserId", Integer.toString(pkg.appId));
        }
        if (pkg.parentPackageName != null) {
            serializer.attribute((String) null, "parentPackageName", pkg.parentPackageName);
        }
        writeChildPackagesLPw(serializer, pkg.childPackageNames);
        writeUsesStaticLibLPw(serializer, pkg.usesStaticLibraries, pkg.usesStaticLibrariesVersions);
        if (pkg.sharedUser == null) {
            writePermissionsLPr(serializer, pkg.getPermissionsState().getInstallPermissionStates());
        }
        serializer.endTag((String) null, "updated-package");
    }

    /* access modifiers changed from: package-private */
    public void writePackageLPr(XmlSerializer serializer, PackageSetting pkg) throws IOException {
        serializer.startTag((String) null, ATTR_PACKAGE);
        serializer.attribute((String) null, ATTR_NAME, pkg.name);
        if (pkg.realName != null) {
            serializer.attribute((String) null, "realName", pkg.realName);
        }
        serializer.attribute((String) null, "codePath", pkg.codePathString);
        if (!pkg.resourcePathString.equals(pkg.codePathString)) {
            serializer.attribute((String) null, "resourcePath", pkg.resourcePathString);
        }
        if (pkg.legacyNativeLibraryPathString != null) {
            serializer.attribute((String) null, "nativeLibraryPath", pkg.legacyNativeLibraryPathString);
        }
        if (pkg.primaryCpuAbiString != null) {
            serializer.attribute((String) null, "primaryCpuAbi", pkg.primaryCpuAbiString);
        }
        if (pkg.secondaryCpuAbiString != null) {
            serializer.attribute((String) null, "secondaryCpuAbi", pkg.secondaryCpuAbiString);
        }
        if (pkg.cpuAbiOverrideString != null) {
            serializer.attribute((String) null, "cpuAbiOverride", pkg.cpuAbiOverrideString);
        }
        serializer.attribute((String) null, "publicFlags", Integer.toString(pkg.pkgFlags));
        serializer.attribute((String) null, "privateFlags", Integer.toString(pkg.pkgPrivateFlags));
        serializer.attribute((String) null, "ft", Long.toHexString(pkg.timeStamp));
        serializer.attribute((String) null, "it", Long.toHexString(pkg.firstInstallTime));
        serializer.attribute((String) null, "ut", Long.toHexString(pkg.lastUpdateTime));
        serializer.attribute((String) null, "version", String.valueOf(pkg.versionCode));
        if (pkg.sharedUser == null) {
            serializer.attribute((String) null, "userId", Integer.toString(pkg.appId));
        } else {
            serializer.attribute((String) null, "sharedUserId", Integer.toString(pkg.appId));
        }
        if (pkg.uidError) {
            serializer.attribute((String) null, "uidError", "true");
        }
        if (pkg.installerPackageName != null) {
            serializer.attribute((String) null, "installer", pkg.installerPackageName);
        }
        if (pkg.isOrphaned) {
            serializer.attribute((String) null, "isOrphaned", "true");
        }
        if (pkg.volumeUuid != null) {
            serializer.attribute((String) null, ATTR_VOLUME_UUID, pkg.volumeUuid);
        }
        if (pkg.categoryHint != -1) {
            serializer.attribute((String) null, "categoryHint", Integer.toString(pkg.categoryHint));
        }
        if (pkg.parentPackageName != null) {
            serializer.attribute((String) null, "parentPackageName", pkg.parentPackageName);
        }
        if (pkg.updateAvailable) {
            serializer.attribute((String) null, "updateAvailable", "true");
        }
        writeChildPackagesLPw(serializer, pkg.childPackageNames);
        writeUsesStaticLibLPw(serializer, pkg.usesStaticLibraries, pkg.usesStaticLibrariesVersions);
        pkg.signatures.writeXml(serializer, "sigs", this.mPastSignatures);
        writePermissionsLPr(serializer, pkg.getPermissionsState().getInstallPermissionStates());
        writeSigningKeySetLPr(serializer, pkg.keySetData);
        writeUpgradeKeySetsLPr(serializer, pkg.keySetData);
        writeKeySetAliasesLPr(serializer, pkg.keySetData);
        writeDomainVerificationsLPr(serializer, pkg.verificationInfo);
        serializer.endTag((String) null, ATTR_PACKAGE);
    }

    /* access modifiers changed from: package-private */
    public void writeSigningKeySetLPr(XmlSerializer serializer, PackageKeySetData data) throws IOException {
        serializer.startTag((String) null, "proper-signing-keyset");
        serializer.attribute((String) null, "identifier", Long.toString(data.getProperSigningKeySet()));
        serializer.endTag((String) null, "proper-signing-keyset");
    }

    /* access modifiers changed from: package-private */
    public void writeUpgradeKeySetsLPr(XmlSerializer serializer, PackageKeySetData data) throws IOException {
        if (data.isUsingUpgradeKeySets()) {
            for (long id : data.getUpgradeKeySets()) {
                serializer.startTag((String) null, "upgrade-keyset");
                serializer.attribute((String) null, "identifier", Long.toString(id));
                serializer.endTag((String) null, "upgrade-keyset");
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void writeKeySetAliasesLPr(XmlSerializer serializer, PackageKeySetData data) throws IOException {
        for (Map.Entry<String, Long> e : data.getAliases().entrySet()) {
            serializer.startTag((String) null, "defined-keyset");
            serializer.attribute((String) null, "alias", e.getKey());
            serializer.attribute((String) null, "identifier", Long.toString(e.getValue().longValue()));
            serializer.endTag((String) null, "defined-keyset");
        }
    }

    /* access modifiers changed from: package-private */
    public void writePermissionLPr(XmlSerializer serializer, BasePermission bp) throws IOException {
        bp.writeLPr(serializer);
    }

    /* Debug info: failed to restart local var, previous not found, register: 16 */
    /* access modifiers changed from: package-private */
    /* JADX WARNING: Code restructure failed: missing block: B:145:0x03d0, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:146:0x03d1, code lost:
        r2 = r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:148:?, code lost:
        r1.mSettingsFilename.delete();
        r1.mReadMessages.append("Error reading: " + r2.toString());
        com.android.server.pm.PackageManagerService.reportSettingsProblem(6, "Error reading settings: " + r2);
        android.util.Slog.wtf("PackageManager", "Error reading package manager settings", r2);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:149:0x0422, code lost:
        throw new java.lang.IllegalStateException("Failed parsing settings file: " + r1.mSettingsFilename, r2);
     */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Removed duplicated region for block: B:145:0x03d0 A[ExcHandler: IOException | NumberFormatException | XmlPullParserException (r0v0 'e' java.lang.Exception A[CUSTOM_DECLARE]), PHI: r3 
      PHI: (r3v2 'str' java.io.FileInputStream) = (r3v3 'str' java.io.FileInputStream), (r3v3 'str' java.io.FileInputStream), (r3v3 'str' java.io.FileInputStream), (r3v3 'str' java.io.FileInputStream), (r3v3 'str' java.io.FileInputStream), (r3v1 'str' java.io.FileInputStream), (r3v1 'str' java.io.FileInputStream) binds: [B:26:0x00c3, B:88:0x01fd, B:92:0x0206, B:89:?, B:80:0x01ad, B:9:0x0064, B:15:0x008d] A[DONT_GENERATE, DONT_INLINE], Splitter:B:9:0x0064] */
    /* JADX WARNING: Removed duplicated region for block: B:23:0x00b0 A[Catch:{ IOException | NumberFormatException | XmlPullParserException -> 0x03d0 }] */
    /* JADX WARNING: Removed duplicated region for block: B:26:0x00c3 A[SYNTHETIC, Splitter:B:26:0x00c3] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean readLPw(java.util.List<android.content.pm.UserInfo> r17) {
        /*
            r16 = this;
            r1 = r16
            java.lang.String r2 = "No start tag found in package manager settings"
            r3 = 0
            java.io.File r4 = r1.mBackupSettingsFilename
            boolean r4 = r4.exists()
            r5 = 4
            java.lang.String r6 = "PackageManager"
            if (r4 == 0) goto L_0x0049
            java.io.FileInputStream r4 = new java.io.FileInputStream     // Catch:{ IOException -> 0x0048 }
            java.io.File r7 = r1.mBackupSettingsFilename     // Catch:{ IOException -> 0x0048 }
            r4.<init>(r7)     // Catch:{ IOException -> 0x0048 }
            r3 = r4
            java.lang.StringBuilder r4 = r1.mReadMessages     // Catch:{ IOException -> 0x0048 }
            java.lang.String r7 = "Reading from backup settings file\n"
            r4.append(r7)     // Catch:{ IOException -> 0x0048 }
            java.lang.String r4 = "Need to read from backup settings file"
            com.android.server.pm.PackageManagerService.reportSettingsProblem(r5, r4)     // Catch:{ IOException -> 0x0048 }
            java.io.File r4 = r1.mSettingsFilename     // Catch:{ IOException -> 0x0048 }
            boolean r4 = r4.exists()     // Catch:{ IOException -> 0x0048 }
            if (r4 == 0) goto L_0x0047
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ IOException -> 0x0048 }
            r4.<init>()     // Catch:{ IOException -> 0x0048 }
            java.lang.String r7 = "Cleaning up settings file "
            r4.append(r7)     // Catch:{ IOException -> 0x0048 }
            java.io.File r7 = r1.mSettingsFilename     // Catch:{ IOException -> 0x0048 }
            r4.append(r7)     // Catch:{ IOException -> 0x0048 }
            java.lang.String r4 = r4.toString()     // Catch:{ IOException -> 0x0048 }
            android.util.Slog.w(r6, r4)     // Catch:{ IOException -> 0x0048 }
            java.io.File r4 = r1.mSettingsFilename     // Catch:{ IOException -> 0x0048 }
            r4.delete()     // Catch:{ IOException -> 0x0048 }
        L_0x0047:
            goto L_0x0049
        L_0x0048:
            r0 = move-exception
        L_0x0049:
            java.util.ArrayList<com.android.server.pm.PackageSetting> r4 = r1.mPendingPackages
            r4.clear()
            java.util.ArrayList<android.content.pm.Signature> r4 = r1.mPastSignatures
            r4.clear()
            android.util.ArrayMap<java.lang.Long, java.lang.Integer> r4 = r1.mKeySetRefs
            r4.clear()
            android.util.ArraySet<java.lang.String> r4 = r1.mInstallerPackages
            r4.clear()
            r4 = 6
            java.lang.String r7 = "primary_physical"
            r8 = 0
            if (r3 != 0) goto L_0x0095
            java.io.File r9 = r1.mSettingsFilename     // Catch:{ IOException | NumberFormatException | XmlPullParserException -> 0x03d0 }
            boolean r9 = r9.exists()     // Catch:{ IOException | NumberFormatException | XmlPullParserException -> 0x03d0 }
            if (r9 != 0) goto L_0x008d
            java.lang.StringBuilder r2 = r1.mReadMessages     // Catch:{ IOException | NumberFormatException | XmlPullParserException -> 0x03d0 }
            java.lang.String r9 = "No settings file found\n"
            r2.append(r9)     // Catch:{ IOException | NumberFormatException | XmlPullParserException -> 0x03d0 }
            java.lang.String r2 = "No settings file; creating initial state"
            com.android.server.pm.PackageManagerService.reportSettingsProblem(r5, r2)     // Catch:{ IOException | NumberFormatException | XmlPullParserException -> 0x03d0 }
            java.lang.String r2 = android.os.storage.StorageManager.UUID_PRIVATE_INTERNAL     // Catch:{ IOException | NumberFormatException | XmlPullParserException -> 0x03d0 }
            com.android.server.pm.Settings$VersionInfo r2 = r1.findOrCreateVersion(r2)     // Catch:{ IOException | NumberFormatException | XmlPullParserException -> 0x03d0 }
            r2.forceCurrent()     // Catch:{ IOException | NumberFormatException | XmlPullParserException -> 0x03d0 }
            com.android.server.pm.Settings$VersionInfo r2 = r1.findOrCreateVersion(r7)     // Catch:{ IOException | NumberFormatException | XmlPullParserException -> 0x03d0 }
            r2.forceCurrent()     // Catch:{ IOException | NumberFormatException | XmlPullParserException -> 0x03d0 }
            libcore.io.IoUtils.closeQuietly(r3)
            return r8
        L_0x008d:
            java.io.FileInputStream r9 = new java.io.FileInputStream     // Catch:{ IOException | NumberFormatException | XmlPullParserException -> 0x03d0 }
            java.io.File r10 = r1.mSettingsFilename     // Catch:{ IOException | NumberFormatException | XmlPullParserException -> 0x03d0 }
            r9.<init>(r10)     // Catch:{ IOException | NumberFormatException | XmlPullParserException -> 0x03d0 }
            r3 = r9
        L_0x0095:
            org.xmlpull.v1.XmlPullParser r9 = android.util.Xml.newPullParser()     // Catch:{ IOException | NumberFormatException | XmlPullParserException -> 0x03d0 }
            java.nio.charset.Charset r10 = java.nio.charset.StandardCharsets.UTF_8     // Catch:{ IOException | NumberFormatException | XmlPullParserException -> 0x03d0 }
            java.lang.String r10 = r10.name()     // Catch:{ IOException | NumberFormatException | XmlPullParserException -> 0x03d0 }
            r9.setInput(r3, r10)     // Catch:{ IOException | NumberFormatException | XmlPullParserException -> 0x03d0 }
        L_0x00a2:
            int r10 = r9.next()     // Catch:{ IOException | NumberFormatException | XmlPullParserException -> 0x03d0 }
            r11 = r10
            r12 = 2
            r13 = 1
            if (r10 == r12) goto L_0x00ae
            if (r11 == r13) goto L_0x00ae
            goto L_0x00a2
        L_0x00ae:
            if (r11 == r12) goto L_0x00c3
            java.lang.StringBuilder r5 = r1.mReadMessages     // Catch:{ IOException | NumberFormatException | XmlPullParserException -> 0x03d0 }
            java.lang.String r7 = "No start tag found in settings file\n"
            r5.append(r7)     // Catch:{ IOException | NumberFormatException | XmlPullParserException -> 0x03d0 }
            r5 = 5
            com.android.server.pm.PackageManagerService.reportSettingsProblem(r5, r2)     // Catch:{ IOException | NumberFormatException | XmlPullParserException -> 0x03d0 }
            android.util.Slog.wtf(r6, r2)     // Catch:{ IOException | NumberFormatException | XmlPullParserException -> 0x03d0 }
            libcore.io.IoUtils.closeQuietly(r3)
            return r8
        L_0x00c3:
            int r2 = r9.getDepth()     // Catch:{ IOException | NumberFormatException | XmlPullParserException -> 0x03d0 }
        L_0x00c7:
            int r10 = r9.next()     // Catch:{ IOException | NumberFormatException | XmlPullParserException -> 0x03d0 }
            r11 = r10
            if (r10 == r13) goto L_0x029d
            r10 = 3
            if (r11 != r10) goto L_0x00d7
            int r12 = r9.getDepth()     // Catch:{ IOException | NumberFormatException | XmlPullParserException -> 0x03d0 }
            if (r12 <= r2) goto L_0x029d
        L_0x00d7:
            if (r11 == r10) goto L_0x0299
            if (r11 != r5) goto L_0x00dd
            goto L_0x0299
        L_0x00dd:
            java.lang.String r10 = r9.getName()     // Catch:{ IOException | NumberFormatException | XmlPullParserException -> 0x03d0 }
            java.lang.String r12 = "package"
            boolean r12 = r10.equals(r12)     // Catch:{ IOException | NumberFormatException | XmlPullParserException -> 0x03d0 }
            if (r12 == 0) goto L_0x00ef
            r1.readPackageLPw(r9)     // Catch:{ IOException | NumberFormatException | XmlPullParserException -> 0x03d0 }
            goto L_0x0298
        L_0x00ef:
            java.lang.String r12 = "permissions"
            boolean r12 = r10.equals(r12)     // Catch:{ IOException | NumberFormatException | XmlPullParserException -> 0x03d0 }
            if (r12 == 0) goto L_0x00ff
            com.android.server.pm.permission.PermissionSettings r12 = r1.mPermissions     // Catch:{ IOException | NumberFormatException | XmlPullParserException -> 0x03d0 }
            r12.readPermissions(r9)     // Catch:{ IOException | NumberFormatException | XmlPullParserException -> 0x03d0 }
            goto L_0x0298
        L_0x00ff:
            java.lang.String r12 = "permission-trees"
            boolean r12 = r10.equals(r12)     // Catch:{ IOException | NumberFormatException | XmlPullParserException -> 0x03d0 }
            if (r12 == 0) goto L_0x010f
            com.android.server.pm.permission.PermissionSettings r12 = r1.mPermissions     // Catch:{ IOException | NumberFormatException | XmlPullParserException -> 0x03d0 }
            r12.readPermissionTrees(r9)     // Catch:{ IOException | NumberFormatException | XmlPullParserException -> 0x03d0 }
            goto L_0x0298
        L_0x010f:
            java.lang.String r12 = "shared-user"
            boolean r12 = r10.equals(r12)     // Catch:{ IOException | NumberFormatException | XmlPullParserException -> 0x03d0 }
            if (r12 == 0) goto L_0x011d
            r1.readSharedUserLPw(r9)     // Catch:{ IOException | NumberFormatException | XmlPullParserException -> 0x03d0 }
            goto L_0x0298
        L_0x011d:
            java.lang.String r12 = "preferred-packages"
            boolean r12 = r10.equals(r12)     // Catch:{ IOException | NumberFormatException | XmlPullParserException -> 0x03d0 }
            if (r12 == 0) goto L_0x0128
            goto L_0x0298
        L_0x0128:
            java.lang.String r12 = "preferred-activities"
            boolean r12 = r10.equals(r12)     // Catch:{ IOException | NumberFormatException | XmlPullParserException -> 0x03d0 }
            if (r12 == 0) goto L_0x0136
            r1.readPreferredActivitiesLPw(r9, r8)     // Catch:{ IOException | NumberFormatException | XmlPullParserException -> 0x03d0 }
            goto L_0x0298
        L_0x0136:
            java.lang.String r12 = "persistent-preferred-activities"
            boolean r12 = r10.equals(r12)     // Catch:{ IOException | NumberFormatException | XmlPullParserException -> 0x03d0 }
            if (r12 == 0) goto L_0x0144
            r1.readPersistentPreferredActivitiesLPw(r9, r8)     // Catch:{ IOException | NumberFormatException | XmlPullParserException -> 0x03d0 }
            goto L_0x0298
        L_0x0144:
            java.lang.String r12 = "crossProfile-intent-filters"
            boolean r12 = r10.equals(r12)     // Catch:{ IOException | NumberFormatException | XmlPullParserException -> 0x03d0 }
            if (r12 == 0) goto L_0x0151
            r1.readCrossProfileIntentFiltersLPw(r9, r8)     // Catch:{ IOException | NumberFormatException | XmlPullParserException -> 0x03d0 }
            goto L_0x0298
        L_0x0151:
            java.lang.String r12 = "default-browser"
            boolean r12 = r10.equals(r12)     // Catch:{ IOException | NumberFormatException | XmlPullParserException -> 0x03d0 }
            if (r12 == 0) goto L_0x015e
            r1.readDefaultAppsLPw(r9, r8)     // Catch:{ IOException | NumberFormatException | XmlPullParserException -> 0x03d0 }
            goto L_0x0298
        L_0x015e:
            java.lang.String r12 = "updated-package"
            boolean r12 = r10.equals(r12)     // Catch:{ IOException | NumberFormatException | XmlPullParserException -> 0x03d0 }
            if (r12 == 0) goto L_0x016c
            r1.readDisabledSysPackageLPw(r9)     // Catch:{ IOException | NumberFormatException | XmlPullParserException -> 0x03d0 }
            goto L_0x0298
        L_0x016c:
            java.lang.String r12 = "renamed-package"
            boolean r12 = r10.equals(r12)     // Catch:{ IOException | NumberFormatException | XmlPullParserException -> 0x03d0 }
            r14 = 0
            if (r12 == 0) goto L_0x018f
            java.lang.String r12 = "new"
            java.lang.String r12 = r9.getAttributeValue(r14, r12)     // Catch:{ IOException | NumberFormatException | XmlPullParserException -> 0x03d0 }
            java.lang.String r15 = "old"
            java.lang.String r14 = r9.getAttributeValue(r14, r15)     // Catch:{ IOException | NumberFormatException | XmlPullParserException -> 0x03d0 }
            if (r12 == 0) goto L_0x018d
            if (r14 == 0) goto L_0x018d
            android.util.ArrayMap<java.lang.String, java.lang.String> r15 = r1.mRenamedPackages     // Catch:{ IOException | NumberFormatException | XmlPullParserException -> 0x03d0 }
            r15.put(r12, r14)     // Catch:{ IOException | NumberFormatException | XmlPullParserException -> 0x03d0 }
        L_0x018d:
            goto L_0x0298
        L_0x018f:
            java.lang.String r12 = "restored-ivi"
            boolean r12 = r10.equals(r12)     // Catch:{ IOException | NumberFormatException | XmlPullParserException -> 0x03d0 }
            if (r12 == 0) goto L_0x019d
            r1.readRestoredIntentFilterVerifications(r9)     // Catch:{ IOException | NumberFormatException | XmlPullParserException -> 0x03d0 }
            goto L_0x0298
        L_0x019d:
            java.lang.String r12 = "last-platform-version"
            boolean r12 = r10.equals(r12)     // Catch:{ IOException | NumberFormatException | XmlPullParserException -> 0x03d0 }
            java.lang.String r15 = "fingerprint"
            java.lang.String r5 = "external"
            java.lang.String r13 = "internal"
            if (r12 == 0) goto L_0x01ce
            java.lang.String r12 = android.os.storage.StorageManager.UUID_PRIVATE_INTERNAL     // Catch:{ IOException | NumberFormatException | XmlPullParserException -> 0x03d0 }
            com.android.server.pm.Settings$VersionInfo r12 = r1.findOrCreateVersion(r12)     // Catch:{ IOException | NumberFormatException | XmlPullParserException -> 0x03d0 }
            com.android.server.pm.Settings$VersionInfo r14 = r1.findOrCreateVersion(r7)     // Catch:{ IOException | NumberFormatException | XmlPullParserException -> 0x03d0 }
            int r13 = com.android.internal.util.XmlUtils.readIntAttribute(r9, r13, r8)     // Catch:{ IOException | NumberFormatException | XmlPullParserException -> 0x03d0 }
            r12.sdkVersion = r13     // Catch:{ IOException | NumberFormatException | XmlPullParserException -> 0x03d0 }
            int r5 = com.android.internal.util.XmlUtils.readIntAttribute(r9, r5, r8)     // Catch:{ IOException | NumberFormatException | XmlPullParserException -> 0x03d0 }
            r14.sdkVersion = r5     // Catch:{ IOException | NumberFormatException | XmlPullParserException -> 0x03d0 }
            java.lang.String r5 = com.android.internal.util.XmlUtils.readStringAttribute(r9, r15)     // Catch:{ IOException | NumberFormatException | XmlPullParserException -> 0x03d0 }
            r14.fingerprint = r5     // Catch:{ IOException | NumberFormatException | XmlPullParserException -> 0x03d0 }
            r12.fingerprint = r5     // Catch:{ IOException | NumberFormatException | XmlPullParserException -> 0x03d0 }
            goto L_0x0298
        L_0x01ce:
            java.lang.String r12 = "database-version"
            boolean r12 = r10.equals(r12)     // Catch:{ IOException | NumberFormatException | XmlPullParserException -> 0x03d0 }
            if (r12 == 0) goto L_0x01ee
            java.lang.String r12 = android.os.storage.StorageManager.UUID_PRIVATE_INTERNAL     // Catch:{ IOException | NumberFormatException | XmlPullParserException -> 0x03d0 }
            com.android.server.pm.Settings$VersionInfo r12 = r1.findOrCreateVersion(r12)     // Catch:{ IOException | NumberFormatException | XmlPullParserException -> 0x03d0 }
            com.android.server.pm.Settings$VersionInfo r14 = r1.findOrCreateVersion(r7)     // Catch:{ IOException | NumberFormatException | XmlPullParserException -> 0x03d0 }
            int r13 = com.android.internal.util.XmlUtils.readIntAttribute(r9, r13, r8)     // Catch:{ IOException | NumberFormatException | XmlPullParserException -> 0x03d0 }
            r12.databaseVersion = r13     // Catch:{ IOException | NumberFormatException | XmlPullParserException -> 0x03d0 }
            int r5 = com.android.internal.util.XmlUtils.readIntAttribute(r9, r5, r8)     // Catch:{ IOException | NumberFormatException | XmlPullParserException -> 0x03d0 }
            r14.databaseVersion = r5     // Catch:{ IOException | NumberFormatException | XmlPullParserException -> 0x03d0 }
            goto L_0x0298
        L_0x01ee:
            java.lang.String r5 = "verifier"
            boolean r5 = r10.equals(r5)     // Catch:{ IOException | NumberFormatException | XmlPullParserException -> 0x03d0 }
            if (r5 == 0) goto L_0x0220
            java.lang.String r5 = "device"
            java.lang.String r5 = r9.getAttributeValue(r14, r5)     // Catch:{ IOException | NumberFormatException | XmlPullParserException -> 0x03d0 }
            android.content.pm.VerifierDeviceIdentity r12 = android.content.pm.VerifierDeviceIdentity.parse(r5)     // Catch:{ IllegalArgumentException -> 0x0204, IOException | NumberFormatException | XmlPullParserException -> 0x03d0, IOException | NumberFormatException | XmlPullParserException -> 0x03d0 }
            r1.mVerifierDeviceIdentity = r12     // Catch:{ IllegalArgumentException -> 0x0204, IOException | NumberFormatException | XmlPullParserException -> 0x03d0, IOException | NumberFormatException | XmlPullParserException -> 0x03d0 }
            goto L_0x021e
        L_0x0204:
            r0 = move-exception
            r12 = r0
            java.lang.StringBuilder r13 = new java.lang.StringBuilder     // Catch:{ IOException | NumberFormatException | XmlPullParserException -> 0x03d0 }
            r13.<init>()     // Catch:{ IOException | NumberFormatException | XmlPullParserException -> 0x03d0 }
            java.lang.String r14 = "Discard invalid verifier device id: "
            r13.append(r14)     // Catch:{ IOException | NumberFormatException | XmlPullParserException -> 0x03d0 }
            java.lang.String r14 = r12.getMessage()     // Catch:{ IOException | NumberFormatException | XmlPullParserException -> 0x03d0 }
            r13.append(r14)     // Catch:{ IOException | NumberFormatException | XmlPullParserException -> 0x03d0 }
            java.lang.String r13 = r13.toString()     // Catch:{ IOException | NumberFormatException | XmlPullParserException -> 0x03d0 }
            android.util.Slog.w(r6, r13)     // Catch:{ IOException | NumberFormatException | XmlPullParserException -> 0x03d0 }
        L_0x021e:
            goto L_0x0298
        L_0x0220:
            java.lang.String r5 = "read-external-storage"
            boolean r5 = r5.equals(r10)     // Catch:{ IOException | NumberFormatException | XmlPullParserException -> 0x03d0 }
            if (r5 == 0) goto L_0x0240
            java.lang.String r5 = "enforcement"
            java.lang.String r5 = r9.getAttributeValue(r14, r5)     // Catch:{ IOException | NumberFormatException | XmlPullParserException -> 0x03d0 }
            java.lang.String r12 = "1"
            boolean r12 = r12.equals(r5)     // Catch:{ IOException | NumberFormatException | XmlPullParserException -> 0x03d0 }
            if (r12 == 0) goto L_0x023b
            java.lang.Boolean r12 = java.lang.Boolean.TRUE     // Catch:{ IOException | NumberFormatException | XmlPullParserException -> 0x03d0 }
            goto L_0x023d
        L_0x023b:
            java.lang.Boolean r12 = java.lang.Boolean.FALSE     // Catch:{ IOException | NumberFormatException | XmlPullParserException -> 0x03d0 }
        L_0x023d:
            r1.mReadExternalStorageEnforced = r12     // Catch:{ IOException | NumberFormatException | XmlPullParserException -> 0x03d0 }
            goto L_0x0298
        L_0x0240:
            java.lang.String r5 = "keyset-settings"
            boolean r5 = r10.equals(r5)     // Catch:{ IOException | NumberFormatException | XmlPullParserException -> 0x03d0 }
            if (r5 == 0) goto L_0x0251
            com.android.server.pm.KeySetManagerService r5 = r1.mKeySetManagerService     // Catch:{ IOException | NumberFormatException | XmlPullParserException -> 0x03d0 }
            android.util.ArrayMap<java.lang.Long, java.lang.Integer> r12 = r1.mKeySetRefs     // Catch:{ IOException | NumberFormatException | XmlPullParserException -> 0x03d0 }
            r5.readKeySetsLPw(r9, r12)     // Catch:{ IOException | NumberFormatException | XmlPullParserException -> 0x03d0 }
            goto L_0x0298
        L_0x0251:
            java.lang.String r5 = "version"
            boolean r5 = r5.equals(r10)     // Catch:{ IOException | NumberFormatException | XmlPullParserException -> 0x03d0 }
            if (r5 == 0) goto L_0x027d
            java.lang.String r5 = "volumeUuid"
            java.lang.String r5 = com.android.internal.util.XmlUtils.readStringAttribute(r9, r5)     // Catch:{ IOException | NumberFormatException | XmlPullParserException -> 0x03d0 }
            com.android.server.pm.Settings$VersionInfo r12 = r1.findOrCreateVersion(r5)     // Catch:{ IOException | NumberFormatException | XmlPullParserException -> 0x03d0 }
            java.lang.String r13 = "sdkVersion"
            int r13 = com.android.internal.util.XmlUtils.readIntAttribute(r9, r13)     // Catch:{ IOException | NumberFormatException | XmlPullParserException -> 0x03d0 }
            r12.sdkVersion = r13     // Catch:{ IOException | NumberFormatException | XmlPullParserException -> 0x03d0 }
            java.lang.String r13 = "databaseVersion"
            int r13 = com.android.internal.util.XmlUtils.readIntAttribute(r9, r13)     // Catch:{ IOException | NumberFormatException | XmlPullParserException -> 0x03d0 }
            r12.databaseVersion = r13     // Catch:{ IOException | NumberFormatException | XmlPullParserException -> 0x03d0 }
            java.lang.String r13 = com.android.internal.util.XmlUtils.readStringAttribute(r9, r15)     // Catch:{ IOException | NumberFormatException | XmlPullParserException -> 0x03d0 }
            r12.fingerprint = r13     // Catch:{ IOException | NumberFormatException | XmlPullParserException -> 0x03d0 }
            goto L_0x0298
        L_0x027d:
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ IOException | NumberFormatException | XmlPullParserException -> 0x03d0 }
            r5.<init>()     // Catch:{ IOException | NumberFormatException | XmlPullParserException -> 0x03d0 }
            java.lang.String r12 = "Unknown element under <packages>: "
            r5.append(r12)     // Catch:{ IOException | NumberFormatException | XmlPullParserException -> 0x03d0 }
            java.lang.String r12 = r9.getName()     // Catch:{ IOException | NumberFormatException | XmlPullParserException -> 0x03d0 }
            r5.append(r12)     // Catch:{ IOException | NumberFormatException | XmlPullParserException -> 0x03d0 }
            java.lang.String r5 = r5.toString()     // Catch:{ IOException | NumberFormatException | XmlPullParserException -> 0x03d0 }
            android.util.Slog.w(r6, r5)     // Catch:{ IOException | NumberFormatException | XmlPullParserException -> 0x03d0 }
            com.android.internal.util.XmlUtils.skipCurrentTag(r9)     // Catch:{ IOException | NumberFormatException | XmlPullParserException -> 0x03d0 }
        L_0x0298:
        L_0x0299:
            r5 = 4
            r13 = 1
            goto L_0x00c7
        L_0x029d:
            libcore.io.IoUtils.closeQuietly(r3)
            java.util.ArrayList<com.android.server.pm.PackageSetting> r2 = r1.mPendingPackages
            int r2 = r2.size()
            r5 = 0
        L_0x02a8:
            if (r5 >= r2) goto L_0x031d
            java.util.ArrayList<com.android.server.pm.PackageSetting> r6 = r1.mPendingPackages
            java.lang.Object r6 = r6.get(r5)
            com.android.server.pm.PackageSetting r6 = (com.android.server.pm.PackageSetting) r6
            int r7 = r6.getSharedUserId()
            com.android.server.pm.SettingBase r9 = r1.getSettingLPr(r7)
            boolean r10 = r9 instanceof com.android.server.pm.SharedUserSetting
            if (r10 == 0) goto L_0x02cb
            r10 = r9
            com.android.server.pm.SharedUserSetting r10 = (com.android.server.pm.SharedUserSetting) r10
            r6.sharedUser = r10
            int r11 = r10.userId
            r6.appId = r11
            r1.addPackageSettingLPw(r6, r10)
            goto L_0x031a
        L_0x02cb:
            java.lang.String r10 = " has shared uid "
            java.lang.String r11 = "Bad package setting: package "
            if (r9 == 0) goto L_0x02f6
            java.lang.StringBuilder r12 = new java.lang.StringBuilder
            r12.<init>()
            r12.append(r11)
            java.lang.String r11 = r6.name
            r12.append(r11)
            r12.append(r10)
            r12.append(r7)
            java.lang.String r10 = " that is not a shared uid\n"
            r12.append(r10)
            java.lang.String r10 = r12.toString()
            java.lang.StringBuilder r11 = r1.mReadMessages
            r11.append(r10)
            com.android.server.pm.PackageManagerService.reportSettingsProblem(r4, r10)
            goto L_0x031a
        L_0x02f6:
            java.lang.StringBuilder r12 = new java.lang.StringBuilder
            r12.<init>()
            r12.append(r11)
            java.lang.String r11 = r6.name
            r12.append(r11)
            r12.append(r10)
            r12.append(r7)
            java.lang.String r10 = " that is not defined\n"
            r12.append(r10)
            java.lang.String r10 = r12.toString()
            java.lang.StringBuilder r11 = r1.mReadMessages
            r11.append(r10)
            com.android.server.pm.PackageManagerService.reportSettingsProblem(r4, r10)
        L_0x031a:
            int r5 = r5 + 1
            goto L_0x02a8
        L_0x031d:
            java.util.ArrayList<com.android.server.pm.PackageSetting> r4 = r1.mPendingPackages
            r4.clear()
            java.io.File r4 = r1.mBackupStoppedPackagesFilename
            boolean r4 = r4.exists()
            if (r4 != 0) goto L_0x0349
            java.io.File r4 = r1.mStoppedPackagesFilename
            boolean r4 = r4.exists()
            if (r4 == 0) goto L_0x0333
            goto L_0x0349
        L_0x0333:
            java.util.Iterator r4 = r17.iterator()
        L_0x0337:
            boolean r5 = r4.hasNext()
            if (r5 == 0) goto L_0x0359
            java.lang.Object r5 = r4.next()
            android.content.pm.UserInfo r5 = (android.content.pm.UserInfo) r5
            int r6 = r5.id
            r1.readPackageRestrictionsLPr(r6)
            goto L_0x0337
        L_0x0349:
            r16.readStoppedLPw()
            java.io.File r4 = r1.mBackupStoppedPackagesFilename
            r4.delete()
            java.io.File r4 = r1.mStoppedPackagesFilename
            r4.delete()
            r1.writePackageRestrictionsLPr(r8)
        L_0x0359:
            java.util.Iterator r4 = r17.iterator()
        L_0x035d:
            boolean r5 = r4.hasNext()
            if (r5 == 0) goto L_0x0371
            java.lang.Object r5 = r4.next()
            android.content.pm.UserInfo r5 = (android.content.pm.UserInfo) r5
            com.android.server.pm.Settings$RuntimePermissionPersistence r6 = r1.mRuntimePermissionsPersistence
            int r7 = r5.id
            r6.readStateForUserSyncLPr(r7)
            goto L_0x035d
        L_0x0371:
            android.util.ArrayMap<java.lang.String, com.android.server.pm.PackageSetting> r4 = r1.mDisabledSysPackages
            java.util.Collection r4 = r4.values()
            java.util.Iterator r4 = r4.iterator()
        L_0x037b:
            boolean r5 = r4.hasNext()
            if (r5 == 0) goto L_0x0399
            java.lang.Object r5 = r4.next()
            com.android.server.pm.PackageSetting r5 = (com.android.server.pm.PackageSetting) r5
            int r6 = r5.appId
            com.android.server.pm.SettingBase r6 = r1.getSettingLPr(r6)
            if (r6 == 0) goto L_0x0398
            boolean r7 = r6 instanceof com.android.server.pm.SharedUserSetting
            if (r7 == 0) goto L_0x0398
            r7 = r6
            com.android.server.pm.SharedUserSetting r7 = (com.android.server.pm.SharedUserSetting) r7
            r5.sharedUser = r7
        L_0x0398:
            goto L_0x037b
        L_0x0399:
            java.lang.StringBuilder r5 = r1.mReadMessages
            java.lang.StringBuilder r6 = new java.lang.StringBuilder
            r6.<init>()
            java.lang.String r7 = "Read completed successfully: "
            r6.append(r7)
            android.util.ArrayMap<java.lang.String, com.android.server.pm.PackageSetting> r7 = r1.mPackages
            int r7 = r7.size()
            r6.append(r7)
            java.lang.String r7 = " packages, "
            r6.append(r7)
            android.util.ArrayMap<java.lang.String, com.android.server.pm.SharedUserSetting> r7 = r1.mSharedUsers
            int r7 = r7.size()
            r6.append(r7)
            java.lang.String r7 = " shared uids\n"
            r6.append(r7)
            java.lang.String r6 = r6.toString()
            r5.append(r6)
            r16.writeKernelMappingLPr()
            r5 = 1
            return r5
        L_0x03cd:
            r0 = move-exception
            r2 = r0
            goto L_0x0423
        L_0x03d0:
            r0 = move-exception
            r2 = r0
            java.io.File r5 = r1.mSettingsFilename     // Catch:{ all -> 0x03cd }
            r5.delete()     // Catch:{ all -> 0x03cd }
            java.lang.StringBuilder r5 = r1.mReadMessages     // Catch:{ all -> 0x03cd }
            java.lang.StringBuilder r7 = new java.lang.StringBuilder     // Catch:{ all -> 0x03cd }
            r7.<init>()     // Catch:{ all -> 0x03cd }
            java.lang.String r8 = "Error reading: "
            r7.append(r8)     // Catch:{ all -> 0x03cd }
            java.lang.String r8 = r2.toString()     // Catch:{ all -> 0x03cd }
            r7.append(r8)     // Catch:{ all -> 0x03cd }
            java.lang.String r7 = r7.toString()     // Catch:{ all -> 0x03cd }
            r5.append(r7)     // Catch:{ all -> 0x03cd }
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ all -> 0x03cd }
            r5.<init>()     // Catch:{ all -> 0x03cd }
            java.lang.String r7 = "Error reading settings: "
            r5.append(r7)     // Catch:{ all -> 0x03cd }
            r5.append(r2)     // Catch:{ all -> 0x03cd }
            java.lang.String r5 = r5.toString()     // Catch:{ all -> 0x03cd }
            com.android.server.pm.PackageManagerService.reportSettingsProblem(r4, r5)     // Catch:{ all -> 0x03cd }
            java.lang.String r4 = "Error reading package manager settings"
            android.util.Slog.wtf(r6, r4, r2)     // Catch:{ all -> 0x03cd }
            java.lang.IllegalStateException r4 = new java.lang.IllegalStateException     // Catch:{ all -> 0x03cd }
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ all -> 0x03cd }
            r5.<init>()     // Catch:{ all -> 0x03cd }
            java.lang.String r6 = "Failed parsing settings file: "
            r5.append(r6)     // Catch:{ all -> 0x03cd }
            java.io.File r6 = r1.mSettingsFilename     // Catch:{ all -> 0x03cd }
            r5.append(r6)     // Catch:{ all -> 0x03cd }
            java.lang.String r5 = r5.toString()     // Catch:{ all -> 0x03cd }
            r4.<init>(r5, r2)     // Catch:{ all -> 0x03cd }
            throw r4     // Catch:{ all -> 0x03cd }
        L_0x0423:
            libcore.io.IoUtils.closeQuietly(r3)
            throw r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.pm.Settings.readLPw(java.util.List):boolean");
    }

    /* access modifiers changed from: package-private */
    /* JADX WARNING: Removed duplicated region for block: B:67:0x0196 A[SYNTHETIC, Splitter:B:67:0x0196] */
    /* JADX WARNING: Removed duplicated region for block: B:97:0x01b7 A[SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void applyDefaultPreferredAppsLPw(int r18) {
        /*
            r17 = this;
            r1 = r17
            r2 = r18
            java.lang.String r3 = "Error reading apps file "
            java.lang.Class<android.content.pm.PackageManagerInternal> r0 = android.content.pm.PackageManagerInternal.class
            java.lang.Object r0 = com.android.server.LocalServices.getService(r0)
            r4 = r0
            android.content.pm.PackageManagerInternal r4 = (android.content.pm.PackageManagerInternal) r4
            android.util.ArrayMap<java.lang.String, com.android.server.pm.PackageSetting> r0 = r1.mPackages
            java.util.Collection r0 = r0.values()
            java.util.Iterator r0 = r0.iterator()
        L_0x0019:
            boolean r5 = r0.hasNext()
            r6 = 1
            if (r5 == 0) goto L_0x0058
            java.lang.Object r5 = r0.next()
            com.android.server.pm.PackageSetting r5 = (com.android.server.pm.PackageSetting) r5
            int r7 = r5.pkgFlags
            r6 = r6 & r7
            if (r6 == 0) goto L_0x0057
            android.content.pm.PackageParser$Package r6 = r5.pkg
            if (r6 == 0) goto L_0x0057
            android.content.pm.PackageParser$Package r6 = r5.pkg
            java.util.ArrayList r6 = r6.preferredActivityFilters
            if (r6 == 0) goto L_0x0057
            android.content.pm.PackageParser$Package r6 = r5.pkg
            java.util.ArrayList r6 = r6.preferredActivityFilters
            r7 = 0
        L_0x003a:
            int r8 = r6.size()
            if (r7 >= r8) goto L_0x0057
            java.lang.Object r8 = r6.get(r7)
            android.content.pm.PackageParser$ActivityIntentInfo r8 = (android.content.pm.PackageParser.ActivityIntentInfo) r8
            android.content.ComponentName r9 = new android.content.ComponentName
            java.lang.String r10 = r5.name
            android.content.pm.PackageParser$Activity r11 = r8.activity
            java.lang.String r11 = r11.className
            r9.<init>(r10, r11)
            r1.applyDefaultPreferredActivityLPw(r4, r8, r9, r2)
            int r7 = r7 + 1
            goto L_0x003a
        L_0x0057:
            goto L_0x0019
        L_0x0058:
            java.io.File r0 = new java.io.File
            java.io.File r5 = android.os.Environment.getRootDirectory()
            java.lang.String r7 = "etc/preferred-apps"
            r0.<init>(r5, r7)
            r5 = r0
            boolean r0 = r5.exists()
            if (r0 == 0) goto L_0x01c9
            boolean r0 = r5.isDirectory()
            if (r0 != 0) goto L_0x0074
            r16 = r4
            goto L_0x01cb
        L_0x0074:
            boolean r0 = r5.canRead()
            java.lang.String r7 = " cannot be read"
            java.lang.String r8 = "PackageSettings"
            if (r0 != 0) goto L_0x0096
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r3 = "Directory "
            r0.append(r3)
            r0.append(r5)
            r0.append(r7)
            java.lang.String r0 = r0.toString()
            android.util.Slog.w(r8, r0)
            return
        L_0x0096:
            java.io.File[] r9 = r5.listFiles()
            int r10 = r9.length
            r0 = 0
            r11 = r0
        L_0x009d:
            if (r11 >= r10) goto L_0x01c8
            r12 = r9[r11]
            java.lang.String r0 = r12.getPath()
            java.lang.String r13 = ".xml"
            boolean r0 = r0.endsWith(r13)
            if (r0 != 0) goto L_0x00d2
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r13 = "Non-xml file "
            r0.append(r13)
            r0.append(r12)
            java.lang.String r13 = " in "
            r0.append(r13)
            r0.append(r5)
            java.lang.String r13 = " directory, ignoring"
            r0.append(r13)
            java.lang.String r0 = r0.toString()
            android.util.Slog.i(r8, r0)
            r16 = r4
            goto L_0x01b7
        L_0x00d2:
            boolean r0 = r12.canRead()
            java.lang.String r13 = "Preferred apps file "
            if (r0 != 0) goto L_0x00f3
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            r0.append(r13)
            r0.append(r12)
            r0.append(r7)
            java.lang.String r0 = r0.toString()
            android.util.Slog.w(r8, r0)
            r16 = r4
            goto L_0x01b7
        L_0x00f3:
            r14 = 0
            java.io.BufferedInputStream r0 = new java.io.BufferedInputStream     // Catch:{ XmlPullParserException -> 0x019a, IOException -> 0x017e, all -> 0x0179 }
            java.io.FileInputStream r15 = new java.io.FileInputStream     // Catch:{ XmlPullParserException -> 0x019a, IOException -> 0x017e, all -> 0x0179 }
            r15.<init>(r12)     // Catch:{ XmlPullParserException -> 0x019a, IOException -> 0x017e, all -> 0x0179 }
            r0.<init>(r15)     // Catch:{ XmlPullParserException -> 0x019a, IOException -> 0x017e, all -> 0x0179 }
            r14 = r0
            org.xmlpull.v1.XmlPullParser r0 = android.util.Xml.newPullParser()     // Catch:{ XmlPullParserException -> 0x019a, IOException -> 0x017e, all -> 0x0179 }
            r15 = r0
            r0 = 0
            r15.setInput(r14, r0)     // Catch:{ XmlPullParserException -> 0x019a, IOException -> 0x017e, all -> 0x0179 }
        L_0x0108:
            int r0 = r15.next()     // Catch:{ XmlPullParserException -> 0x019a, IOException -> 0x017e, all -> 0x0179 }
            r16 = r0
            r6 = 2
            if (r0 == r6) goto L_0x011c
            r6 = r16
            r16 = r4
            r4 = 1
            if (r6 == r4) goto L_0x0121
            r6 = r4
            r4 = r16
            goto L_0x0108
        L_0x011c:
            r6 = r16
            r16 = r4
            r4 = 1
        L_0x0121:
            r0 = 2
            if (r6 == r0) goto L_0x0144
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ XmlPullParserException -> 0x0177, IOException -> 0x0175 }
            r0.<init>()     // Catch:{ XmlPullParserException -> 0x0177, IOException -> 0x0175 }
            r0.append(r13)     // Catch:{ XmlPullParserException -> 0x0177, IOException -> 0x0175 }
            r0.append(r12)     // Catch:{ XmlPullParserException -> 0x0177, IOException -> 0x0175 }
            java.lang.String r13 = " does not have start tag"
            r0.append(r13)     // Catch:{ XmlPullParserException -> 0x0177, IOException -> 0x0175 }
            java.lang.String r0 = r0.toString()     // Catch:{ XmlPullParserException -> 0x0177, IOException -> 0x0175 }
            android.util.Slog.w(r8, r0)     // Catch:{ XmlPullParserException -> 0x0177, IOException -> 0x0175 }
            r14.close()     // Catch:{ IOException -> 0x0141 }
        L_0x013f:
            goto L_0x01b7
        L_0x0141:
            r0 = move-exception
            goto L_0x01b7
        L_0x0144:
            java.lang.String r0 = "preferred-activities"
            java.lang.String r4 = r15.getName()     // Catch:{ XmlPullParserException -> 0x0177, IOException -> 0x0175 }
            boolean r0 = r0.equals(r4)     // Catch:{ XmlPullParserException -> 0x0177, IOException -> 0x0175 }
            if (r0 != 0) goto L_0x016d
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ XmlPullParserException -> 0x0177, IOException -> 0x0175 }
            r0.<init>()     // Catch:{ XmlPullParserException -> 0x0177, IOException -> 0x0175 }
            r0.append(r13)     // Catch:{ XmlPullParserException -> 0x0177, IOException -> 0x0175 }
            r0.append(r12)     // Catch:{ XmlPullParserException -> 0x0177, IOException -> 0x0175 }
            java.lang.String r4 = " does not start with 'preferred-activities'"
            r0.append(r4)     // Catch:{ XmlPullParserException -> 0x0177, IOException -> 0x0175 }
            java.lang.String r0 = r0.toString()     // Catch:{ XmlPullParserException -> 0x0177, IOException -> 0x0175 }
            android.util.Slog.w(r8, r0)     // Catch:{ XmlPullParserException -> 0x0177, IOException -> 0x0175 }
            r14.close()     // Catch:{ IOException -> 0x0141 }
            goto L_0x013f
        L_0x016d:
            r1.readDefaultPreferredActivitiesLPw(r15, r2)     // Catch:{ XmlPullParserException -> 0x0177, IOException -> 0x0175 }
            r14.close()     // Catch:{ IOException -> 0x01b6 }
            goto L_0x01b5
        L_0x0175:
            r0 = move-exception
            goto L_0x0181
        L_0x0177:
            r0 = move-exception
            goto L_0x019d
        L_0x0179:
            r0 = move-exception
            r16 = r4
            r3 = r0
            goto L_0x01c0
        L_0x017e:
            r0 = move-exception
            r16 = r4
        L_0x0181:
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ all -> 0x01be }
            r4.<init>()     // Catch:{ all -> 0x01be }
            r4.append(r3)     // Catch:{ all -> 0x01be }
            r4.append(r12)     // Catch:{ all -> 0x01be }
            java.lang.String r4 = r4.toString()     // Catch:{ all -> 0x01be }
            android.util.Slog.w(r8, r4, r0)     // Catch:{ all -> 0x01be }
            if (r14 == 0) goto L_0x01b7
            r14.close()     // Catch:{ IOException -> 0x01b6 }
            goto L_0x01b5
        L_0x019a:
            r0 = move-exception
            r16 = r4
        L_0x019d:
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ all -> 0x01be }
            r4.<init>()     // Catch:{ all -> 0x01be }
            r4.append(r3)     // Catch:{ all -> 0x01be }
            r4.append(r12)     // Catch:{ all -> 0x01be }
            java.lang.String r4 = r4.toString()     // Catch:{ all -> 0x01be }
            android.util.Slog.w(r8, r4, r0)     // Catch:{ all -> 0x01be }
            if (r14 == 0) goto L_0x01b7
            r14.close()     // Catch:{ IOException -> 0x01b6 }
        L_0x01b5:
            goto L_0x01b7
        L_0x01b6:
            r0 = move-exception
        L_0x01b7:
            int r11 = r11 + 1
            r4 = r16
            r6 = 1
            goto L_0x009d
        L_0x01be:
            r0 = move-exception
            r3 = r0
        L_0x01c0:
            if (r14 == 0) goto L_0x01c7
            r14.close()     // Catch:{ IOException -> 0x01c6 }
            goto L_0x01c7
        L_0x01c6:
            r0 = move-exception
        L_0x01c7:
            throw r3
        L_0x01c8:
            return
        L_0x01c9:
            r16 = r4
        L_0x01cb:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.pm.Settings.applyDefaultPreferredAppsLPw(int):void");
    }

    private void applyDefaultPreferredActivityLPw(PackageManagerInternal pmInternal, IntentFilter tmpPa, ComponentName cn, int userId) {
        int ischeme;
        boolean doAuth;
        IntentFilter intentFilter = tmpPa;
        Intent intent = new Intent();
        intent.setAction(intentFilter.getAction(0));
        int flags = 786432;
        for (int i = 0; i < tmpPa.countCategories(); i++) {
            String cat = intentFilter.getCategory(i);
            if (cat.equals("android.intent.category.DEFAULT")) {
                flags |= 65536;
            } else {
                intent.addCategory(cat);
            }
        }
        int dataSchemesCount = tmpPa.countDataSchemes();
        boolean hasSchemes = false;
        boolean doNonData = true;
        int ischeme2 = 0;
        while (ischeme2 < dataSchemesCount) {
            String scheme = intentFilter.getDataScheme(ischeme2);
            if (scheme != null && !scheme.isEmpty()) {
                hasSchemes = true;
            }
            int dataSchemeSpecificPartsCount = tmpPa.countDataSchemeSpecificParts();
            boolean doAuth2 = true;
            int issp = 0;
            while (issp < dataSchemeSpecificPartsCount) {
                Uri.Builder builder = new Uri.Builder();
                builder.scheme(scheme);
                PatternMatcher ssp = intentFilter.getDataSchemeSpecificPart(issp);
                builder.opaquePart(ssp.getPath());
                Intent finalIntent = new Intent(intent);
                finalIntent.setData(builder.build());
                Intent intent2 = finalIntent;
                Uri.Builder builder2 = builder;
                applyDefaultPreferredActivityLPw(pmInternal, finalIntent, flags, cn, scheme, ssp, (IntentFilter.AuthorityEntry) null, (PatternMatcher) null, userId);
                doAuth2 = false;
                issp++;
                scheme = scheme;
                dataSchemesCount = dataSchemesCount;
                dataSchemeSpecificPartsCount = dataSchemeSpecificPartsCount;
            }
            int i2 = issp;
            int i3 = dataSchemeSpecificPartsCount;
            String scheme2 = scheme;
            int dataSchemesCount2 = dataSchemesCount;
            int dataAuthoritiesCount = tmpPa.countDataAuthorities();
            int iauth = 0;
            while (iauth < dataAuthoritiesCount) {
                IntentFilter.AuthorityEntry auth = intentFilter.getDataAuthority(iauth);
                int dataPathsCount = tmpPa.countDataPaths();
                int ipath = 0;
                boolean doScheme = doAuth;
                boolean doAuth3 = true;
                while (ipath < dataPathsCount) {
                    Uri.Builder builder3 = new Uri.Builder();
                    builder3.scheme(scheme2);
                    if (auth.getHost() != null) {
                        builder3.authority(auth.getHost());
                    }
                    PatternMatcher path = intentFilter.getDataPath(ipath);
                    builder3.path(path.getPath());
                    Intent finalIntent2 = new Intent(intent);
                    finalIntent2.setData(builder3.build());
                    Intent intent3 = finalIntent2;
                    Uri.Builder builder4 = builder3;
                    applyDefaultPreferredActivityLPw(pmInternal, finalIntent2, flags, cn, scheme2, (PatternMatcher) null, auth, path, userId);
                    doScheme = false;
                    doAuth3 = false;
                    ipath++;
                    dataPathsCount = dataPathsCount;
                    iauth = iauth;
                    dataAuthoritiesCount = dataAuthoritiesCount;
                }
                int i4 = ipath;
                int i5 = dataPathsCount;
                int iauth2 = iauth;
                int dataAuthoritiesCount2 = dataAuthoritiesCount;
                if (doAuth3) {
                    Uri.Builder builder5 = new Uri.Builder();
                    builder5.scheme(scheme2);
                    if (auth.getHost() != null) {
                        builder5.authority(auth.getHost());
                    }
                    Intent finalIntent3 = new Intent(intent);
                    finalIntent3.setData(builder5.build());
                    Intent intent4 = finalIntent3;
                    Uri.Builder builder6 = builder5;
                    applyDefaultPreferredActivityLPw(pmInternal, finalIntent3, flags, cn, scheme2, (PatternMatcher) null, auth, (PatternMatcher) null, userId);
                    doAuth = false;
                } else {
                    doAuth = doScheme;
                }
                iauth = iauth2 + 1;
                dataAuthoritiesCount = dataAuthoritiesCount2;
            }
            int i6 = iauth;
            int i7 = dataAuthoritiesCount;
            if (doAuth) {
                Uri.Builder builder7 = new Uri.Builder();
                builder7.scheme(scheme2);
                Intent finalIntent4 = new Intent(intent);
                finalIntent4.setData(builder7.build());
                Intent intent5 = finalIntent4;
                Uri.Builder builder8 = builder7;
                applyDefaultPreferredActivityLPw(pmInternal, finalIntent4, flags, cn, scheme2, (PatternMatcher) null, (IntentFilter.AuthorityEntry) null, (PatternMatcher) null, userId);
            }
            doNonData = false;
            ischeme2++;
            dataSchemesCount = dataSchemesCount2;
        }
        boolean doNonData2 = doNonData;
        for (int idata = 0; idata < tmpPa.countDataTypes(); idata++) {
            String mimeType = intentFilter.getDataType(idata);
            if (hasSchemes) {
                Uri.Builder builder9 = new Uri.Builder();
                int ischeme3 = 0;
                while (ischeme3 < tmpPa.countDataSchemes()) {
                    String scheme3 = intentFilter.getDataScheme(ischeme3);
                    if (scheme3 == null || scheme3.isEmpty()) {
                        ischeme = ischeme3;
                    } else {
                        Intent finalIntent5 = new Intent(intent);
                        builder9.scheme(scheme3);
                        finalIntent5.setDataAndType(builder9.build(), mimeType);
                        Intent intent6 = finalIntent5;
                        String str = scheme3;
                        ischeme = ischeme3;
                        applyDefaultPreferredActivityLPw(pmInternal, finalIntent5, flags, cn, scheme3, (PatternMatcher) null, (IntentFilter.AuthorityEntry) null, (PatternMatcher) null, userId);
                    }
                    ischeme3 = ischeme + 1;
                }
                int i8 = ischeme3;
            } else {
                Intent finalIntent6 = new Intent(intent);
                finalIntent6.setType(mimeType);
                applyDefaultPreferredActivityLPw(pmInternal, finalIntent6, flags, cn, (String) null, (PatternMatcher) null, (IntentFilter.AuthorityEntry) null, (PatternMatcher) null, userId);
            }
            doNonData2 = false;
        }
        if (doNonData2) {
            applyDefaultPreferredActivityLPw(pmInternal, intent, flags, cn, (String) null, (PatternMatcher) null, (IntentFilter.AuthorityEntry) null, (PatternMatcher) null, userId);
        }
    }

    private void applyDefaultPreferredActivityLPw(PackageManagerInternal pmInternal, Intent intent, int flags, ComponentName cn, String scheme, PatternMatcher ssp, IntentFilter.AuthorityEntry auth, PatternMatcher path, int userId) {
        int systemMatch;
        Intent intent2 = intent;
        int i = flags;
        String str = scheme;
        IntentFilter.AuthorityEntry authorityEntry = auth;
        PatternMatcher patternMatcher = path;
        int numMatches = 0;
        List<ResolveInfo> ri = pmInternal.queryIntentActivities(intent2, i, Binder.getCallingUid(), 0);
        if (ri != null) {
            numMatches = ri.size();
        }
        if (numMatches <= 1) {
            Slog.w(TAG, "No potential matches found for " + intent2 + " while setting preferred " + cn.flattenToShortString());
            return;
        }
        boolean haveAct = false;
        ComponentName haveNonSys = null;
        ComponentName[] set = new ComponentName[ri.size()];
        int i2 = 0;
        int systemMatch2 = 0;
        while (true) {
            if (i2 >= numMatches) {
                break;
            }
            ActivityInfo ai = ri.get(i2).activityInfo;
            int numMatches2 = numMatches;
            set[i2] = new ComponentName(ai.packageName, ai.name);
            if ((ai.applicationInfo.flags & 1) == 0) {
                if (ri.get(i2).match >= 0) {
                    haveNonSys = set[i2];
                    break;
                }
            } else if (cn.getPackageName().equals(ai.packageName) && cn.getClassName().equals(ai.name)) {
                haveAct = true;
                systemMatch2 = ri.get(i2).match;
            }
            i2++;
            numMatches = numMatches2;
            PackageManagerInternal packageManagerInternal = pmInternal;
        }
        if (haveNonSys != null) {
            systemMatch = systemMatch2;
            if (0 < systemMatch) {
                haveNonSys = null;
            }
        } else {
            systemMatch = systemMatch2;
        }
        if (!haveAct || haveNonSys != null) {
            int i3 = userId;
            if (haveNonSys == null) {
                StringBuilder sb = new StringBuilder();
                sb.append("No component ");
                sb.append(cn.flattenToShortString());
                sb.append(" found setting preferred ");
                sb.append(intent2);
                sb.append("; possible matches are ");
                int i4 = 0;
                while (i4 < set.length) {
                    if (i4 > 0) {
                        sb.append(", ");
                    }
                    sb.append(set[i4].flattenToShortString());
                    i4++;
                    int i5 = flags;
                }
                Slog.w(TAG, sb.toString());
                return;
            }
            Slog.i(TAG, "Not setting preferred " + intent2 + "; found third party match " + haveNonSys.flattenToShortString());
            return;
        }
        IntentFilter filter = new IntentFilter();
        if (intent.getAction() != null) {
            filter.addAction(intent.getAction());
        }
        if (intent.getCategories() != null) {
            for (String cat : intent.getCategories()) {
                filter.addCategory(cat);
            }
        }
        if ((65536 & i) != 0) {
            filter.addCategory("android.intent.category.DEFAULT");
        }
        if (str != null) {
            filter.addDataScheme(str);
        }
        if (ssp != null) {
            filter.addDataSchemeSpecificPart(ssp.getPath(), ssp.getType());
        }
        if (authorityEntry != null) {
            filter.addDataAuthority(authorityEntry);
        }
        if (patternMatcher != null) {
            filter.addDataPath(patternMatcher);
        }
        if (intent.getType() != null) {
            try {
                filter.addDataType(intent.getType());
                ComponentName componentName = cn;
            } catch (IntentFilter.MalformedMimeTypeException e) {
                Slog.w(TAG, "Malformed mimetype " + intent.getType() + " for " + cn);
            }
        } else {
            ComponentName componentName2 = cn;
        }
        editPreferredActivitiesLPw(userId).addFilter(new PreferredActivity(filter, systemMatch, set, cn, true));
    }

    private void readDefaultPreferredActivitiesLPw(XmlPullParser parser, int userId) throws XmlPullParserException, IOException {
        PackageManagerInternal pmInternal = (PackageManagerInternal) LocalServices.getService(PackageManagerInternal.class);
        int outerDepth = parser.getDepth();
        while (true) {
            int next = parser.next();
            int type = next;
            if (next == 1) {
                return;
            }
            if (type == 3 && parser.getDepth() <= outerDepth) {
                return;
            }
            if (!(type == 3 || type == 4)) {
                if (parser.getName().equals(TAG_ITEM)) {
                    PreferredActivity tmpPa = new PreferredActivity(parser);
                    if (tmpPa.mPref.getParseError() == null) {
                        applyDefaultPreferredActivityLPw(pmInternal, tmpPa, tmpPa.mPref.mComponent, userId);
                    } else {
                        PackageManagerService.reportSettingsProblem(5, "Error in package manager settings: <preferred-activity> " + tmpPa.mPref.getParseError() + " at " + parser.getPositionDescription());
                    }
                } else {
                    PackageManagerService.reportSettingsProblem(5, "Unknown element under <preferred-activities>: " + parser.getName());
                    XmlUtils.skipCurrentTag(parser);
                }
            }
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:17:0x007b  */
    /* JADX WARNING: Removed duplicated region for block: B:18:0x0080  */
    /* JADX WARNING: Removed duplicated region for block: B:21:0x00bf A[SYNTHETIC, Splitter:B:21:0x00bf] */
    /* JADX WARNING: Removed duplicated region for block: B:24:0x00c9  */
    /* JADX WARNING: Removed duplicated region for block: B:31:0x00e4 A[SYNTHETIC, Splitter:B:31:0x00e4] */
    /* JADX WARNING: Removed duplicated region for block: B:36:0x00f5 A[SYNTHETIC, Splitter:B:36:0x00f5] */
    /* JADX WARNING: Removed duplicated region for block: B:41:0x0107  */
    /* JADX WARNING: Removed duplicated region for block: B:42:0x010c  */
    /* JADX WARNING: Removed duplicated region for block: B:45:0x0113  */
    /* JADX WARNING: Removed duplicated region for block: B:56:0x0137 A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:72:0x01a1 A[EDGE_INSN: B:75:0x01a1->B:72:0x01a1 ?: BREAK  
    EDGE_INSN: B:76:0x01a1->B:72:0x01a1 ?: BREAK  ] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void readDisabledSysPackageLPw(org.xmlpull.v1.XmlPullParser r39) throws org.xmlpull.v1.XmlPullParserException, java.io.IOException {
        /*
            r38 = this;
            r1 = r38
            r2 = r39
            java.lang.String r3 = "name"
            r4 = 0
            java.lang.String r14 = r2.getAttributeValue(r4, r3)
            java.lang.String r0 = "realName"
            java.lang.String r23 = r2.getAttributeValue(r4, r0)
            java.lang.String r0 = "codePath"
            java.lang.String r15 = r2.getAttributeValue(r4, r0)
            java.lang.String r0 = "resourcePath"
            java.lang.String r0 = r2.getAttributeValue(r4, r0)
            java.lang.String r5 = "requiredCpuAbi"
            java.lang.String r24 = r2.getAttributeValue(r4, r5)
            java.lang.String r5 = "nativeLibraryPath"
            java.lang.String r25 = r2.getAttributeValue(r4, r5)
            java.lang.String r5 = "parentPackageName"
            java.lang.String r26 = r2.getAttributeValue(r4, r5)
            java.lang.String r5 = "primaryCpuAbi"
            java.lang.String r5 = r2.getAttributeValue(r4, r5)
            java.lang.String r6 = "secondaryCpuAbi"
            java.lang.String r27 = r2.getAttributeValue(r4, r6)
            java.lang.String r6 = "cpuAbiOverride"
            java.lang.String r28 = r2.getAttributeValue(r4, r6)
            if (r5 != 0) goto L_0x0052
            if (r24 == 0) goto L_0x0052
            r5 = r24
            r29 = r5
            goto L_0x0054
        L_0x0052:
            r29 = r5
        L_0x0054:
            if (r0 != 0) goto L_0x0059
            r0 = r15
            r13 = r0
            goto L_0x005a
        L_0x0059:
            r13 = r0
        L_0x005a:
            java.lang.String r0 = "version"
            java.lang.String r30 = r2.getAttributeValue(r4, r0)
            r5 = 0
            if (r30 == 0) goto L_0x006e
            long r7 = java.lang.Long.parseLong(r30)     // Catch:{ NumberFormatException -> 0x006d }
            r5 = r7
            r31 = r5
            goto L_0x0070
        L_0x006d:
            r0 = move-exception
        L_0x006e:
            r31 = r5
        L_0x0070:
            r0 = 0
            r5 = 0
            r12 = 1
            r33 = r0 | 1
            boolean r0 = com.android.server.pm.PackageManagerService.locationIsPrivileged(r15)
            if (r0 == 0) goto L_0x0080
            r0 = r5 | 8
            r34 = r0
            goto L_0x0082
        L_0x0080:
            r34 = r5
        L_0x0082:
            com.android.server.pm.PackageSetting r0 = new com.android.server.pm.PackageSetting
            r5 = r0
            java.io.File r6 = new java.io.File
            r8 = r6
            r6.<init>(r15)
            java.io.File r6 = new java.io.File
            r9 = r6
            r6.<init>(r13)
            r19 = 0
            r20 = 0
            r21 = 0
            r22 = 0
            r6 = r14
            r7 = r23
            r10 = r25
            r11 = r29
            r12 = r27
            r35 = r13
            r13 = r28
            r36 = r14
            r37 = r15
            r14 = r31
            r16 = r33
            r17 = r34
            r18 = r26
            r5.<init>(r6, r7, r8, r9, r10, r11, r12, r13, r14, r16, r17, r18, r19, r20, r21, r22)
            java.lang.String r0 = "ft"
            java.lang.String r6 = r2.getAttributeValue(r4, r0)
            r7 = 16
            if (r6 == 0) goto L_0x00c9
            long r8 = java.lang.Long.parseLong(r6, r7)     // Catch:{ NumberFormatException -> 0x00c7 }
            r5.setTimeStamp(r8)     // Catch:{ NumberFormatException -> 0x00c7 }
            goto L_0x00c8
        L_0x00c7:
            r0 = move-exception
        L_0x00c8:
            goto L_0x00db
        L_0x00c9:
            java.lang.String r0 = "ts"
            java.lang.String r6 = r2.getAttributeValue(r4, r0)
            if (r6 == 0) goto L_0x00db
            long r8 = java.lang.Long.parseLong(r6)     // Catch:{ NumberFormatException -> 0x00da }
            r5.setTimeStamp(r8)     // Catch:{ NumberFormatException -> 0x00da }
            goto L_0x00db
        L_0x00da:
            r0 = move-exception
        L_0x00db:
            java.lang.String r0 = "it"
            java.lang.String r6 = r2.getAttributeValue(r4, r0)
            if (r6 == 0) goto L_0x00ec
            long r8 = java.lang.Long.parseLong(r6, r7)     // Catch:{ NumberFormatException -> 0x00eb }
            r5.firstInstallTime = r8     // Catch:{ NumberFormatException -> 0x00eb }
            goto L_0x00ec
        L_0x00eb:
            r0 = move-exception
        L_0x00ec:
            java.lang.String r0 = "ut"
            java.lang.String r6 = r2.getAttributeValue(r4, r0)
            if (r6 == 0) goto L_0x00fd
            long r7 = java.lang.Long.parseLong(r6, r7)     // Catch:{ NumberFormatException -> 0x00fc }
            r5.lastUpdateTime = r7     // Catch:{ NumberFormatException -> 0x00fc }
            goto L_0x00fd
        L_0x00fc:
            r0 = move-exception
        L_0x00fd:
            java.lang.String r0 = "userId"
            java.lang.String r0 = r2.getAttributeValue(r4, r0)
            r7 = 0
            if (r0 == 0) goto L_0x010c
            int r8 = java.lang.Integer.parseInt(r0)
            goto L_0x010d
        L_0x010c:
            r8 = r7
        L_0x010d:
            r5.appId = r8
            int r8 = r5.appId
            if (r8 > 0) goto L_0x0122
            java.lang.String r8 = "sharedUserId"
            java.lang.String r8 = r2.getAttributeValue(r4, r8)
            if (r8 == 0) goto L_0x0120
            int r7 = java.lang.Integer.parseInt(r8)
        L_0x0120:
            r5.appId = r7
        L_0x0122:
            int r7 = r39.getDepth()
        L_0x0126:
            int r8 = r39.next()
            r9 = r8
            r10 = 1
            if (r8 == r10) goto L_0x01a1
            r8 = 3
            if (r9 != r8) goto L_0x0137
            int r11 = r39.getDepth()
            if (r11 <= r7) goto L_0x01a1
        L_0x0137:
            if (r9 == r8) goto L_0x0126
            r8 = 4
            if (r9 != r8) goto L_0x013d
            goto L_0x0126
        L_0x013d:
            java.lang.String r8 = r39.getName()
            java.lang.String r11 = "perms"
            boolean r8 = r8.equals(r11)
            if (r8 == 0) goto L_0x0152
            com.android.server.pm.permission.PermissionsState r8 = r5.getPermissionsState()
            r1.readInstallPermissionsLPr(r2, r8)
            goto L_0x0126
        L_0x0152:
            java.lang.String r8 = r39.getName()
            java.lang.String r11 = "child-package"
            boolean r8 = r8.equals(r11)
            if (r8 == 0) goto L_0x0173
            java.lang.String r8 = r2.getAttributeValue(r4, r3)
            java.util.List r11 = r5.childPackageNames
            if (r11 != 0) goto L_0x016d
            java.util.ArrayList r11 = new java.util.ArrayList
            r11.<init>()
            r5.childPackageNames = r11
        L_0x016d:
            java.util.List r11 = r5.childPackageNames
            r11.add(r8)
            goto L_0x0126
        L_0x0173:
            java.lang.String r8 = r39.getName()
            java.lang.String r11 = "uses-static-lib"
            boolean r8 = r8.equals(r11)
            if (r8 == 0) goto L_0x0184
            r1.readUsesStaticLibLPw(r2, r5)
            goto L_0x0126
        L_0x0184:
            r8 = 5
            java.lang.StringBuilder r11 = new java.lang.StringBuilder
            r11.<init>()
            java.lang.String r12 = "Unknown element under <updated-package>: "
            r11.append(r12)
            java.lang.String r12 = r39.getName()
            r11.append(r12)
            java.lang.String r11 = r11.toString()
            com.android.server.pm.PackageManagerService.reportSettingsProblem(r8, r11)
            com.android.internal.util.XmlUtils.skipCurrentTag(r39)
            goto L_0x0126
        L_0x01a1:
            android.util.ArrayMap<java.lang.String, com.android.server.pm.PackageSetting> r3 = r1.mDisabledSysPackages
            r4 = r36
            r3.put(r4, r5)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.pm.Settings.readDisabledSysPackageLPw(org.xmlpull.v1.XmlPullParser):void");
    }

    /* JADX WARNING: Removed duplicated region for block: B:151:0x020f  */
    /* JADX WARNING: Removed duplicated region for block: B:158:0x021f A[SYNTHETIC, Splitter:B:158:0x021f] */
    /* JADX WARNING: Removed duplicated region for block: B:162:0x0236 A[Catch:{ NumberFormatException -> 0x0224 }] */
    /* JADX WARNING: Removed duplicated region for block: B:165:0x023a A[Catch:{ NumberFormatException -> 0x0224 }] */
    /* JADX WARNING: Removed duplicated region for block: B:166:0x023f  */
    /* JADX WARNING: Removed duplicated region for block: B:169:0x0244  */
    /* JADX WARNING: Removed duplicated region for block: B:170:0x0247  */
    /* JADX WARNING: Removed duplicated region for block: B:172:0x024b A[SYNTHETIC, Splitter:B:172:0x024b] */
    /* JADX WARNING: Removed duplicated region for block: B:177:0x0268  */
    /* JADX WARNING: Removed duplicated region for block: B:179:0x026c A[SYNTHETIC, Splitter:B:179:0x026c] */
    /* JADX WARNING: Removed duplicated region for block: B:194:0x02dd  */
    /* JADX WARNING: Removed duplicated region for block: B:363:0x09b6  */
    /* JADX WARNING: Removed duplicated region for block: B:385:0x0a4b  */
    /* JADX WARNING: Removed duplicated region for block: B:386:0x0a53  */
    /* JADX WARNING: Removed duplicated region for block: B:390:0x0a61  */
    /* JADX WARNING: Removed duplicated region for block: B:444:0x0c6b  */
    /* JADX WARNING: Removed duplicated region for block: B:446:0x0c62 A[SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void readPackageLPw(org.xmlpull.v1.XmlPullParser r79) throws org.xmlpull.v1.XmlPullParserException, java.io.IOException {
        /*
            r78 = this;
            r14 = r78
            r13 = r79
            java.lang.String r11 = " has bad userId "
            java.lang.String r12 = "name"
            java.lang.String r10 = " at "
            java.lang.String r7 = "Error in package manager settings: package "
            java.lang.String r5 = "true"
            r1 = 0
            r2 = 0
            r3 = 0
            r4 = 0
            r6 = 0
            r8 = 0
            r9 = 0
            r16 = 0
            r17 = 0
            r18 = 0
            r19 = 0
            r20 = 0
            r21 = 0
            r22 = 0
            r23 = 0
            r24 = 0
            r25 = 0
            r26 = -1
            r15 = 0
            r27 = 0
            r28 = 0
            r29 = 0
            r31 = 0
            r33 = 0
            r35 = 0
            r36 = 0
            r37 = 0
            r14 = 0
            java.lang.String r0 = r13.getAttributeValue(r14, r12)     // Catch:{ NumberFormatException -> 0x0955 }
            r1 = r0
            java.lang.String r0 = "realName"
            java.lang.String r0 = r13.getAttributeValue(r14, r0)     // Catch:{ NumberFormatException -> 0x0932 }
            r2 = r0
            java.lang.String r0 = "userId"
            java.lang.String r0 = r13.getAttributeValue(r14, r0)     // Catch:{ NumberFormatException -> 0x090b }
            r3 = r0
            java.lang.String r0 = "uidError"
            java.lang.String r0 = r13.getAttributeValue(r14, r0)     // Catch:{ NumberFormatException -> 0x08e4 }
            r39 = r0
            java.lang.String r0 = "sharedUserId"
            java.lang.String r0 = r13.getAttributeValue(r14, r0)     // Catch:{ NumberFormatException -> 0x08bf }
            r4 = r0
            java.lang.String r0 = "codePath"
            java.lang.String r0 = r13.getAttributeValue(r14, r0)     // Catch:{ NumberFormatException -> 0x0898 }
            r6 = r0
            java.lang.String r0 = "resourcePath"
            java.lang.String r0 = r13.getAttributeValue(r14, r0)     // Catch:{ NumberFormatException -> 0x086f }
            r40 = r0
            java.lang.String r0 = "requiredCpuAbi"
            java.lang.String r0 = r13.getAttributeValue(r14, r0)     // Catch:{ NumberFormatException -> 0x0848 }
            r41 = r0
            java.lang.String r0 = "parentPackageName"
            java.lang.String r15 = r13.getAttributeValue(r14, r0)     // Catch:{ NumberFormatException -> 0x0823 }
            java.lang.String r0 = "nativeLibraryPath"
            java.lang.String r0 = r13.getAttributeValue(r14, r0)     // Catch:{ NumberFormatException -> 0x0823 }
            r9 = r6
            r6 = r0
            java.lang.String r0 = "primaryCpuAbi"
            java.lang.String r0 = r13.getAttributeValue(r14, r0)     // Catch:{ NumberFormatException -> 0x07fa }
            r17 = r0
            java.lang.String r0 = "secondaryCpuAbi"
            java.lang.String r8 = r13.getAttributeValue(r14, r0)     // Catch:{ NumberFormatException -> 0x07fa }
            java.lang.String r0 = "cpuAbiOverride"
            java.lang.String r0 = r13.getAttributeValue(r14, r0)     // Catch:{ NumberFormatException -> 0x07d3 }
            r42 = r9
            r9 = r0
            java.lang.String r0 = "updateAvailable"
            java.lang.String r0 = r13.getAttributeValue(r14, r0)     // Catch:{ NumberFormatException -> 0x07b0 }
            r25 = r0
            if (r17 != 0) goto L_0x00b9
            if (r41 == 0) goto L_0x00b9
            r0 = r41
            r19 = r0
            goto L_0x00bb
        L_0x00b9:
            r19 = r17
        L_0x00bb:
            java.lang.String r0 = "version"
            java.lang.String r0 = r13.getAttributeValue(r14, r0)     // Catch:{ NumberFormatException -> 0x078f }
            r36 = r0
            if (r36 == 0) goto L_0x00ce
            long r16 = java.lang.Long.parseLong(r36)     // Catch:{ NumberFormatException -> 0x00cd }
            r37 = r16
            goto L_0x00ce
        L_0x00cd:
            r0 = move-exception
        L_0x00ce:
            java.lang.String r0 = "installer"
            java.lang.String r0 = r13.getAttributeValue(r14, r0)     // Catch:{ NumberFormatException -> 0x078f }
            r21 = r0
            java.lang.String r0 = "isOrphaned"
            java.lang.String r0 = r13.getAttributeValue(r14, r0)     // Catch:{ NumberFormatException -> 0x078f }
            r22 = r0
            java.lang.String r0 = "volumeUuid"
            java.lang.String r0 = r13.getAttributeValue(r14, r0)     // Catch:{ NumberFormatException -> 0x078f }
            r23 = r0
            java.lang.String r0 = "categoryHint"
            java.lang.String r0 = r13.getAttributeValue(r14, r0)     // Catch:{ NumberFormatException -> 0x078f }
            r24 = r0
            if (r24 == 0) goto L_0x00fb
            int r0 = java.lang.Integer.parseInt(r24)     // Catch:{ NumberFormatException -> 0x00fa }
            r26 = r0
            goto L_0x00fb
        L_0x00fa:
            r0 = move-exception
        L_0x00fb:
            java.lang.String r0 = "publicFlags"
            java.lang.String r0 = r13.getAttributeValue(r14, r0)     // Catch:{ NumberFormatException -> 0x078f }
            r20 = r0
            if (r20 == 0) goto L_0x0140
            int r0 = java.lang.Integer.parseInt(r20)     // Catch:{ NumberFormatException -> 0x010d }
            r27 = r0
            goto L_0x010e
        L_0x010d:
            r0 = move-exception
        L_0x010e:
            java.lang.String r0 = "privateFlags"
            java.lang.String r0 = r13.getAttributeValue(r14, r0)     // Catch:{ NumberFormatException -> 0x0128 }
            r16 = r0
            if (r16 == 0) goto L_0x0124
            int r0 = java.lang.Integer.parseInt(r16)     // Catch:{ NumberFormatException -> 0x0123 }
            r28 = r0
            r20 = r16
            goto L_0x01bb
        L_0x0123:
            r0 = move-exception
        L_0x0124:
            r20 = r16
            goto L_0x01bb
        L_0x0128:
            r0 = move-exception
            r71 = r5
            r16 = r6
            r14 = r7
            r74 = r12
            r61 = r29
            r63 = r31
            r65 = r33
            r6 = r42
            r7 = 5
            r5 = r78
            r30 = r2
            r2 = r11
            goto L_0x0975
        L_0x0140:
            java.lang.String r0 = "flags"
            java.lang.String r0 = r13.getAttributeValue(r14, r0)     // Catch:{ NumberFormatException -> 0x078f }
            r20 = r0
            if (r20 == 0) goto L_0x017e
            int r0 = java.lang.Integer.parseInt(r20)     // Catch:{ NumberFormatException -> 0x0151 }
            r27 = r0
            goto L_0x0152
        L_0x0151:
            r0 = move-exception
        L_0x0152:
            int r0 = PRE_M_APP_INFO_FLAG_HIDDEN     // Catch:{ NumberFormatException -> 0x0128 }
            r0 = r27 & r0
            if (r0 == 0) goto L_0x015a
            r28 = r28 | 1
        L_0x015a:
            int r0 = PRE_M_APP_INFO_FLAG_CANT_SAVE_STATE     // Catch:{ NumberFormatException -> 0x0128 }
            r0 = r27 & r0
            if (r0 == 0) goto L_0x0164
            r0 = r28 | 2
            r28 = r0
        L_0x0164:
            int r0 = PRE_M_APP_INFO_FLAG_PRIVILEGED     // Catch:{ NumberFormatException -> 0x0128 }
            r0 = r27 & r0
            if (r0 == 0) goto L_0x016e
            r0 = r28 | 8
            r28 = r0
        L_0x016e:
            int r0 = PRE_M_APP_INFO_FLAG_HIDDEN     // Catch:{ NumberFormatException -> 0x0128 }
            int r16 = PRE_M_APP_INFO_FLAG_CANT_SAVE_STATE     // Catch:{ NumberFormatException -> 0x0128 }
            r0 = r0 | r16
            int r16 = PRE_M_APP_INFO_FLAG_PRIVILEGED     // Catch:{ NumberFormatException -> 0x0128 }
            r0 = r0 | r16
            int r0 = ~r0
            r0 = r27 & r0
            r27 = r0
            goto L_0x01bb
        L_0x017e:
            java.lang.String r0 = "system"
            java.lang.String r0 = r13.getAttributeValue(r14, r0)     // Catch:{ NumberFormatException -> 0x078f }
            r16 = r0
            r14 = r16
            if (r14 == 0) goto L_0x01b5
            boolean r0 = r5.equalsIgnoreCase(r14)     // Catch:{ NumberFormatException -> 0x019b }
            if (r0 == 0) goto L_0x0193
            r0 = 1
            goto L_0x0194
        L_0x0193:
            r0 = 0
        L_0x0194:
            r0 = r27 | r0
            r27 = r0
            r20 = r14
            goto L_0x01bb
        L_0x019b:
            r0 = move-exception
            r71 = r5
            r16 = r6
            r74 = r12
            r20 = r14
            r61 = r29
            r63 = r31
            r65 = r33
            r6 = r42
            r5 = r78
            r30 = r2
            r14 = r7
            r2 = r11
            r7 = 5
            goto L_0x0975
        L_0x01b5:
            r0 = r27 | 1
            r27 = r0
            r20 = r14
        L_0x01bb:
            java.lang.String r0 = "ft"
            r14 = 0
            java.lang.String r0 = r13.getAttributeValue(r14, r0)     // Catch:{ NumberFormatException -> 0x078f }
            r14 = r0
            r44 = r5
            r5 = 16
            if (r14 == 0) goto L_0x01d4
            long r16 = java.lang.Long.parseLong(r14, r5)     // Catch:{ NumberFormatException -> 0x01d2 }
            r29 = r16
        L_0x01cf:
            r61 = r29
            goto L_0x01eb
        L_0x01d2:
            r0 = move-exception
            goto L_0x01cf
        L_0x01d4:
            java.lang.String r0 = "ts"
            r5 = 0
            java.lang.String r0 = r13.getAttributeValue(r5, r0)     // Catch:{ NumberFormatException -> 0x076e }
            r14 = r0
            if (r14 == 0) goto L_0x01e9
            long r17 = java.lang.Long.parseLong(r14)     // Catch:{ NumberFormatException -> 0x01e8 }
            r29 = r17
            r61 = r29
            goto L_0x01eb
        L_0x01e8:
            r0 = move-exception
        L_0x01e9:
            r61 = r29
        L_0x01eb:
            java.lang.String r0 = "it"
            r5 = 0
            java.lang.String r0 = r13.getAttributeValue(r5, r0)     // Catch:{ NumberFormatException -> 0x074d }
            r5 = r0
            if (r5 == 0) goto L_0x0202
            r14 = 16
            long r17 = java.lang.Long.parseLong(r5, r14)     // Catch:{ NumberFormatException -> 0x0201 }
            r31 = r17
            r63 = r31
            goto L_0x0204
        L_0x0201:
            r0 = move-exception
        L_0x0202:
            r63 = r31
        L_0x0204:
            java.lang.String r0 = "ut"
            r14 = 0
            java.lang.String r0 = r13.getAttributeValue(r14, r0)     // Catch:{ NumberFormatException -> 0x0728 }
            r5 = r0
            if (r5 == 0) goto L_0x021b
            r14 = 16
            long r16 = java.lang.Long.parseLong(r5, r14)     // Catch:{ NumberFormatException -> 0x021a }
            r33 = r16
            r65 = r33
            goto L_0x021d
        L_0x021a:
            r0 = move-exception
        L_0x021b:
            r65 = r33
        L_0x021d:
            if (r3 == 0) goto L_0x0236
            int r14 = java.lang.Integer.parseInt(r3)     // Catch:{ NumberFormatException -> 0x0224 }
            goto L_0x0237
        L_0x0224:
            r0 = move-exception
            r5 = r78
            r30 = r2
            r16 = r6
            r14 = r7
            r2 = r11
            r74 = r12
            r6 = r42
            r71 = r44
            r7 = 5
            goto L_0x0975
        L_0x0236:
            r14 = 0
        L_0x0237:
            r0 = r14
            if (r4 == 0) goto L_0x023f
            int r14 = java.lang.Integer.parseInt(r4)     // Catch:{ NumberFormatException -> 0x0224 }
            goto L_0x0240
        L_0x023f:
            r14 = 0
        L_0x0240:
            r29 = r14
            if (r40 != 0) goto L_0x0247
            r14 = r42
            goto L_0x0249
        L_0x0247:
            r14 = r40
        L_0x0249:
            if (r2 == 0) goto L_0x0268
            java.lang.String r16 = r2.intern()     // Catch:{ NumberFormatException -> 0x0254 }
            r2 = r16
            r30 = r2
            goto L_0x026a
        L_0x0254:
            r0 = move-exception
            r5 = r78
            r30 = r2
            r16 = r6
            r2 = r11
            r74 = r12
            r40 = r14
            r6 = r42
            r71 = r44
            r14 = r7
            r7 = 5
            goto L_0x0975
        L_0x0268:
            r30 = r2
        L_0x026a:
            if (r1 != 0) goto L_0x02dd
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ NumberFormatException -> 0x02c9 }
            r2.<init>()     // Catch:{ NumberFormatException -> 0x02c9 }
            r45 = r3
            java.lang.String r3 = "Error in package manager settings: <package> has no name at "
            r2.append(r3)     // Catch:{ NumberFormatException -> 0x02b5 }
            java.lang.String r3 = r79.getPositionDescription()     // Catch:{ NumberFormatException -> 0x02b5 }
            r2.append(r3)     // Catch:{ NumberFormatException -> 0x02b5 }
            java.lang.String r2 = r2.toString()     // Catch:{ NumberFormatException -> 0x02b5 }
            r3 = 5
            com.android.server.pm.PackageManagerService.reportSettingsProblem(r3, r2)     // Catch:{ NumberFormatException -> 0x02a1 }
            r5 = r78
            r3 = r1
            r70 = r4
            r4 = r10
            r74 = r12
            r71 = r44
            r68 = r45
            r16 = r61
            r10 = r63
            r12 = r65
            r1 = 5
            r77 = r14
            r14 = r7
            r7 = r77
            goto L_0x0695
        L_0x02a1:
            r0 = move-exception
            r5 = r78
            r16 = r6
            r2 = r11
            r74 = r12
            r40 = r14
            r6 = r42
            r71 = r44
            r14 = r7
            r7 = r3
            r3 = r45
            goto L_0x0975
        L_0x02b5:
            r0 = move-exception
            r5 = r78
            r16 = r6
            r2 = r11
            r74 = r12
            r40 = r14
            r6 = r42
            r71 = r44
            r3 = r45
            r14 = r7
            r7 = 5
            goto L_0x0975
        L_0x02c9:
            r0 = move-exception
            r45 = r3
            r5 = r78
            r16 = r6
            r2 = r11
            r74 = r12
            r40 = r14
            r6 = r42
            r71 = r44
            r14 = r7
            r7 = 5
            goto L_0x0975
        L_0x02dd:
            r45 = r3
            r3 = r42
            if (r3 != 0) goto L_0x0359
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ NumberFormatException -> 0x0344 }
            r2.<init>()     // Catch:{ NumberFormatException -> 0x0344 }
            r42 = r4
            java.lang.String r4 = "Error in package manager settings: <package> has no codePath at "
            r2.append(r4)     // Catch:{ NumberFormatException -> 0x032f }
            java.lang.String r4 = r79.getPositionDescription()     // Catch:{ NumberFormatException -> 0x032f }
            r2.append(r4)     // Catch:{ NumberFormatException -> 0x032f }
            java.lang.String r2 = r2.toString()     // Catch:{ NumberFormatException -> 0x032f }
            r4 = 5
            com.android.server.pm.PackageManagerService.reportSettingsProblem(r4, r2)     // Catch:{ NumberFormatException -> 0x031a }
            r5 = r78
            r74 = r12
            r70 = r42
            r71 = r44
            r68 = r45
            r16 = r61
            r12 = r65
            r42 = r3
            r3 = r1
            r1 = r4
            r4 = r10
            r10 = r63
            r77 = r14
            r14 = r7
            r7 = r77
            goto L_0x0695
        L_0x031a:
            r0 = move-exception
            r5 = r78
            r16 = r6
            r2 = r11
            r74 = r12
            r40 = r14
            r71 = r44
            r6 = r3
            r14 = r7
            r3 = r45
            r7 = r4
            r4 = r42
            goto L_0x0975
        L_0x032f:
            r0 = move-exception
            r5 = r78
            r16 = r6
            r2 = r11
            r74 = r12
            r40 = r14
            r4 = r42
            r71 = r44
            r6 = r3
            r14 = r7
            r3 = r45
            r7 = 5
            goto L_0x0975
        L_0x0344:
            r0 = move-exception
            r42 = r4
            r5 = r78
            r16 = r6
            r2 = r11
            r74 = r12
            r40 = r14
            r71 = r44
            r6 = r3
            r14 = r7
            r3 = r45
            r7 = 5
            goto L_0x0975
        L_0x0359:
            r42 = r4
            r4 = 5
            if (r0 <= 0) goto L_0x04a9
            java.lang.String r2 = r1.intern()     // Catch:{ NumberFormatException -> 0x047c }
            java.io.File r4 = new java.io.File     // Catch:{ NumberFormatException -> 0x047c }
            r4.<init>(r3)     // Catch:{ NumberFormatException -> 0x047c }
            r31 = r5
            java.io.File r5 = new java.io.File     // Catch:{ NumberFormatException -> 0x047c }
            r5.<init>(r14)     // Catch:{ NumberFormatException -> 0x047c }
            r16 = 0
            r17 = 0
            r18 = 0
            r67 = r1
            r1 = r78
            r69 = r3
            r68 = r45
            r3 = r30
            r70 = r42
            r32 = 5
            r71 = r44
            r72 = r7
            r7 = r19
            r73 = r10
            r10 = r0
            r75 = r11
            r74 = r12
            r11 = r37
            r13 = r27
            r76 = r14
            r14 = r28
            com.android.server.pm.PackageSetting r1 = r1.addPackageLPw(r2, r3, r4, r5, r6, r7, r8, r9, r10, r11, r13, r14, r15, r16, r17, r18)     // Catch:{ NumberFormatException -> 0x045e }
            if (r1 != 0) goto L_0x03df
            r2 = 6
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ NumberFormatException -> 0x03c5 }
            r3.<init>()     // Catch:{ NumberFormatException -> 0x03c5 }
            java.lang.String r4 = "Failure adding uid "
            r3.append(r4)     // Catch:{ NumberFormatException -> 0x03c5 }
            r3.append(r0)     // Catch:{ NumberFormatException -> 0x03c5 }
            java.lang.String r4 = " while parsing settings at "
            r3.append(r4)     // Catch:{ NumberFormatException -> 0x03c5 }
            java.lang.String r4 = r79.getPositionDescription()     // Catch:{ NumberFormatException -> 0x03c5 }
            r3.append(r4)     // Catch:{ NumberFormatException -> 0x03c5 }
            java.lang.String r3 = r3.toString()     // Catch:{ NumberFormatException -> 0x03c5 }
            com.android.server.pm.PackageManagerService.reportSettingsProblem(r2, r3)     // Catch:{ NumberFormatException -> 0x03c5 }
            r3 = r61
            r10 = r63
            r12 = r65
            goto L_0x03ec
        L_0x03c5:
            r0 = move-exception
            r5 = r78
            r35 = r1
            r16 = r6
            r1 = r67
            r3 = r68
            r6 = r69
            r4 = r70
            r14 = r72
            r10 = r73
            r2 = r75
            r40 = r76
            r7 = 5
            goto L_0x0975
        L_0x03df:
            r3 = r61
            r1.setTimeStamp(r3)     // Catch:{ NumberFormatException -> 0x043e }
            r10 = r63
            r1.firstInstallTime = r10     // Catch:{ NumberFormatException -> 0x041e }
            r12 = r65
            r1.lastUpdateTime = r12     // Catch:{ NumberFormatException -> 0x03fe }
        L_0x03ec:
            r5 = r78
            r2 = r1
            r16 = r3
            r3 = r67
            r42 = r69
            r14 = r72
            r4 = r73
            r7 = r76
            r1 = 5
            goto L_0x0697
        L_0x03fe:
            r0 = move-exception
            r5 = r78
            r35 = r1
            r61 = r3
            r16 = r6
            r63 = r10
            r65 = r12
            r1 = r67
            r3 = r68
            r6 = r69
            r4 = r70
            r14 = r72
            r10 = r73
            r2 = r75
            r40 = r76
            r7 = 5
            goto L_0x0975
        L_0x041e:
            r0 = move-exception
            r12 = r65
            r5 = r78
            r35 = r1
            r61 = r3
            r16 = r6
            r63 = r10
            r1 = r67
            r3 = r68
            r6 = r69
            r4 = r70
            r14 = r72
            r10 = r73
            r2 = r75
            r40 = r76
            r7 = 5
            goto L_0x0975
        L_0x043e:
            r0 = move-exception
            r10 = r63
            r12 = r65
            r5 = r78
            r35 = r1
            r61 = r3
            r16 = r6
            r1 = r67
            r3 = r68
            r6 = r69
            r4 = r70
            r14 = r72
            r10 = r73
            r2 = r75
            r40 = r76
            r7 = 5
            goto L_0x0975
        L_0x045e:
            r0 = move-exception
            r3 = r61
            r10 = r63
            r12 = r65
            r5 = r78
            r16 = r6
            r1 = r67
            r3 = r68
            r6 = r69
            r4 = r70
            r14 = r72
            r10 = r73
            r2 = r75
            r40 = r76
            r7 = 5
            goto L_0x0975
        L_0x047c:
            r0 = move-exception
            r67 = r1
            r69 = r3
            r73 = r10
            r75 = r11
            r74 = r12
            r76 = r14
            r70 = r42
            r71 = r44
            r68 = r45
            r3 = r61
            r10 = r63
            r12 = r65
            r5 = r78
            r16 = r6
            r14 = r7
            r3 = r68
            r6 = r69
            r4 = r70
            r10 = r73
            r2 = r75
            r40 = r76
            r7 = 5
            goto L_0x0975
        L_0x04a9:
            r67 = r1
            r69 = r3
            r31 = r5
            r72 = r7
            r73 = r10
            r75 = r11
            r74 = r12
            r76 = r14
            r70 = r42
            r71 = r44
            r68 = r45
            r3 = r61
            r10 = r63
            r12 = r65
            r1 = r70
            if (r1 == 0) goto L_0x0659
            if (r29 <= 0) goto L_0x05ba
            com.android.server.pm.PackageSetting r2 = new com.android.server.pm.PackageSetting     // Catch:{ NumberFormatException -> 0x0599 }
            java.lang.String r44 = r67.intern()     // Catch:{ NumberFormatException -> 0x0599 }
            java.io.File r5 = new java.io.File     // Catch:{ NumberFormatException -> 0x0599 }
            r7 = r69
            r5.<init>(r7)     // Catch:{ NumberFormatException -> 0x0578 }
            java.io.File r14 = new java.io.File     // Catch:{ NumberFormatException -> 0x0578 }
            r42 = r7
            r7 = r76
            r14.<init>(r7)     // Catch:{ NumberFormatException -> 0x055b }
            r57 = 0
            r59 = 0
            r60 = 0
            r43 = r2
            r45 = r30
            r46 = r5
            r47 = r14
            r48 = r6
            r49 = r19
            r50 = r8
            r51 = r9
            r52 = r37
            r54 = r27
            r55 = r28
            r56 = r15
            r58 = r29
            r43.<init>(r44, r45, r46, r47, r48, r49, r50, r51, r52, r54, r55, r56, r57, r58, r59, r60)     // Catch:{ NumberFormatException -> 0x055b }
            r2.setTimeStamp(r3)     // Catch:{ NumberFormatException -> 0x053c }
            r2.firstInstallTime = r10     // Catch:{ NumberFormatException -> 0x053c }
            r2.lastUpdateTime = r12     // Catch:{ NumberFormatException -> 0x053c }
            r5 = r78
            java.util.ArrayList<com.android.server.pm.PackageSetting> r14 = r5.mPendingPackages     // Catch:{ NumberFormatException -> 0x051f }
            r14.add(r2)     // Catch:{ NumberFormatException -> 0x051f }
            r70 = r1
            r16 = r3
            r3 = r67
            r14 = r72
            r4 = r73
            r1 = 5
            goto L_0x0697
        L_0x051f:
            r0 = move-exception
            r35 = r2
            r61 = r3
            r16 = r6
            r40 = r7
            r63 = r10
            r65 = r12
            r6 = r42
            r3 = r68
            r14 = r72
            r10 = r73
            r2 = r75
            r7 = 5
            r4 = r1
            r1 = r67
            goto L_0x0975
        L_0x053c:
            r0 = move-exception
            r5 = r78
            r35 = r2
            r61 = r3
            r16 = r6
            r40 = r7
            r63 = r10
            r65 = r12
            r6 = r42
            r3 = r68
            r14 = r72
            r10 = r73
            r2 = r75
            r7 = 5
            r4 = r1
            r1 = r67
            goto L_0x0975
        L_0x055b:
            r0 = move-exception
            r5 = r78
            r61 = r3
            r16 = r6
            r40 = r7
            r63 = r10
            r65 = r12
            r6 = r42
            r3 = r68
            r14 = r72
            r10 = r73
            r2 = r75
            r7 = 5
            r4 = r1
            r1 = r67
            goto L_0x0975
        L_0x0578:
            r0 = move-exception
            r5 = r78
            r42 = r7
            r7 = r76
            r61 = r3
            r16 = r6
            r40 = r7
            r63 = r10
            r65 = r12
            r6 = r42
            r3 = r68
            r14 = r72
            r10 = r73
            r2 = r75
            r7 = 5
            r4 = r1
            r1 = r67
            goto L_0x0975
        L_0x0599:
            r0 = move-exception
            r5 = r78
            r42 = r69
            r7 = r76
            r61 = r3
            r16 = r6
            r40 = r7
            r63 = r10
            r65 = r12
            r6 = r42
            r3 = r68
            r14 = r72
            r10 = r73
            r2 = r75
            r7 = 5
            r4 = r1
            r1 = r67
            goto L_0x0975
        L_0x05ba:
            r5 = r78
            r42 = r69
            r7 = r76
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ NumberFormatException -> 0x063b }
            r2.<init>()     // Catch:{ NumberFormatException -> 0x063b }
            r14 = r72
            r2.append(r14)     // Catch:{ NumberFormatException -> 0x0635 }
            r16 = r3
            r3 = r67
            r2.append(r3)     // Catch:{ NumberFormatException -> 0x061d }
            java.lang.String r4 = " has bad sharedId "
            r2.append(r4)     // Catch:{ NumberFormatException -> 0x061d }
            r2.append(r1)     // Catch:{ NumberFormatException -> 0x061d }
            r4 = r73
            r2.append(r4)     // Catch:{ NumberFormatException -> 0x0606 }
            r18 = r0
            java.lang.String r0 = r79.getPositionDescription()     // Catch:{ NumberFormatException -> 0x0606 }
            r2.append(r0)     // Catch:{ NumberFormatException -> 0x0606 }
            java.lang.String r0 = r2.toString()     // Catch:{ NumberFormatException -> 0x0606 }
            r2 = 5
            com.android.server.pm.PackageManagerService.reportSettingsProblem(r2, r0)     // Catch:{ NumberFormatException -> 0x05f4 }
            r70 = r1
            r1 = r2
            goto L_0x0695
        L_0x05f4:
            r0 = move-exception
            r40 = r7
            r63 = r10
            r65 = r12
            r61 = r16
            r7 = r2
            r10 = r4
            r16 = r6
            r6 = r42
            r2 = r75
            goto L_0x0617
        L_0x0606:
            r0 = move-exception
            r40 = r7
            r63 = r10
            r65 = r12
            r61 = r16
            r2 = r75
            r7 = 5
            r10 = r4
            r16 = r6
            r6 = r42
        L_0x0617:
            r4 = r1
            r1 = r3
            r3 = r68
            goto L_0x0975
        L_0x061d:
            r0 = move-exception
            r4 = r1
            r1 = r3
            r40 = r7
            r63 = r10
            r65 = r12
            r61 = r16
            r3 = r68
            r10 = r73
            r2 = r75
            r7 = 5
            r16 = r6
            r6 = r42
            goto L_0x0975
        L_0x0635:
            r0 = move-exception
            r16 = r3
            r3 = r67
            goto L_0x0642
        L_0x063b:
            r0 = move-exception
            r16 = r3
            r3 = r67
            r14 = r72
        L_0x0642:
            r4 = r1
            r1 = r3
            r40 = r7
            r63 = r10
            r65 = r12
            r61 = r16
            r3 = r68
            r10 = r73
            r2 = r75
            r7 = 5
            r16 = r6
            r6 = r42
            goto L_0x0975
        L_0x0659:
            r5 = r78
            r18 = r0
            r16 = r3
            r3 = r67
            r42 = r69
            r14 = r72
            r4 = r73
            r7 = r76
            r2 = 5
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ NumberFormatException -> 0x070d }
            r0.<init>()     // Catch:{ NumberFormatException -> 0x070d }
            r0.append(r14)     // Catch:{ NumberFormatException -> 0x070d }
            r0.append(r3)     // Catch:{ NumberFormatException -> 0x070d }
            r2 = r75
            r0.append(r2)     // Catch:{ NumberFormatException -> 0x06f8 }
            r70 = r1
            r1 = r68
            r0.append(r1)     // Catch:{ NumberFormatException -> 0x06e0 }
            r0.append(r4)     // Catch:{ NumberFormatException -> 0x06e0 }
            r68 = r1
            java.lang.String r1 = r79.getPositionDescription()     // Catch:{ NumberFormatException -> 0x06ca }
            r0.append(r1)     // Catch:{ NumberFormatException -> 0x06ca }
            java.lang.String r0 = r0.toString()     // Catch:{ NumberFormatException -> 0x06ca }
            r1 = 5
            com.android.server.pm.PackageManagerService.reportSettingsProblem(r1, r0)     // Catch:{ NumberFormatException -> 0x06b7 }
        L_0x0695:
            r2 = r35
        L_0x0697:
            r15 = r3
            r40 = r7
            r63 = r10
            r65 = r12
            r61 = r16
            r11 = r19
            r3 = r21
            r13 = r25
            r1 = r39
            r7 = r68
            r10 = r4
            r12 = r8
            r16 = r9
            r4 = r22
            r8 = r26
            r9 = r6
            r6 = r23
            goto L_0x09b4
        L_0x06b7:
            r0 = move-exception
            r40 = r7
            r63 = r10
            r65 = r12
            r61 = r16
            r7 = r1
            r1 = r3
            r10 = r4
            r16 = r6
            r6 = r42
            r3 = r68
            goto L_0x06dc
        L_0x06ca:
            r0 = move-exception
            r1 = r3
            r40 = r7
            r63 = r10
            r65 = r12
            r61 = r16
            r3 = r68
            r7 = 5
            r10 = r4
            r16 = r6
            r6 = r42
        L_0x06dc:
            r4 = r70
            goto L_0x0975
        L_0x06e0:
            r0 = move-exception
            r68 = r1
            r1 = r3
            r40 = r7
            r63 = r10
            r65 = r12
            r61 = r16
            r3 = r68
            r7 = 5
            r10 = r4
            r16 = r6
            r6 = r42
            r4 = r70
            goto L_0x0975
        L_0x06f8:
            r0 = move-exception
            r70 = r1
            r1 = r3
            r40 = r7
            r63 = r10
            r65 = r12
            r61 = r16
            r3 = r68
            r7 = 5
            r10 = r4
            r16 = r6
            r6 = r42
            goto L_0x0724
        L_0x070d:
            r0 = move-exception
            r70 = r1
            r1 = r2
            r2 = r75
            r40 = r7
            r63 = r10
            r65 = r12
            r61 = r16
            r7 = r1
            r1 = r3
            r10 = r4
            r16 = r6
            r6 = r42
            r3 = r68
        L_0x0724:
            r4 = r70
            goto L_0x0975
        L_0x0728:
            r0 = move-exception
            r5 = r78
            r68 = r3
            r70 = r4
            r14 = r7
            r4 = r10
            r74 = r12
            r71 = r44
            r16 = r61
            r7 = 5
            r3 = r1
            r1 = r2
            r2 = r11
            r10 = r63
            r30 = r1
            r1 = r3
            r65 = r33
            r3 = r68
            r10 = r4
            r16 = r6
            r6 = r42
            r4 = r70
            goto L_0x0975
        L_0x074d:
            r0 = move-exception
            r5 = r78
            r68 = r3
            r70 = r4
            r14 = r7
            r74 = r12
            r71 = r44
            r16 = r61
            r7 = 5
            r3 = r1
            r1 = r2
            r2 = r11
            r30 = r1
            r1 = r3
            r63 = r31
            r65 = r33
            r3 = r68
            r16 = r6
            r6 = r42
            goto L_0x0975
        L_0x076e:
            r0 = move-exception
            r5 = r78
            r68 = r3
            r70 = r4
            r14 = r7
            r74 = r12
            r71 = r44
            r7 = 5
            r3 = r1
            r1 = r2
            r2 = r11
            r16 = r6
            r61 = r29
            r63 = r31
            r65 = r33
            r6 = r42
            r30 = r1
            r1 = r3
            r3 = r68
            goto L_0x0975
        L_0x078f:
            r0 = move-exception
            r68 = r3
            r70 = r4
            r71 = r5
            r14 = r7
            r74 = r12
            r7 = 5
            r5 = r78
            r3 = r1
            r1 = r2
            r2 = r11
            r16 = r6
            r61 = r29
            r63 = r31
            r65 = r33
            r6 = r42
            r30 = r1
            r1 = r3
            r3 = r68
            goto L_0x0975
        L_0x07b0:
            r0 = move-exception
            r68 = r3
            r70 = r4
            r71 = r5
            r14 = r7
            r74 = r12
            r7 = 5
            r5 = r78
            r3 = r1
            r1 = r2
            r2 = r11
            r16 = r6
            r19 = r17
            r61 = r29
            r63 = r31
            r65 = r33
            r6 = r42
            r30 = r1
            r1 = r3
            r3 = r68
            goto L_0x0975
        L_0x07d3:
            r0 = move-exception
            r68 = r3
            r70 = r4
            r71 = r5
            r14 = r7
            r42 = r9
            r74 = r12
            r7 = 5
            r5 = r78
            r3 = r1
            r1 = r2
            r2 = r11
            r16 = r6
            r9 = r19
            r61 = r29
            r63 = r31
            r65 = r33
            r6 = r42
            r30 = r1
            r1 = r3
            r19 = r17
            r3 = r68
            goto L_0x0975
        L_0x07fa:
            r0 = move-exception
            r68 = r3
            r70 = r4
            r71 = r5
            r14 = r7
            r42 = r9
            r74 = r12
            r7 = 5
            r5 = r78
            r3 = r1
            r1 = r2
            r2 = r11
            r16 = r6
            r8 = r18
            r9 = r19
            r61 = r29
            r63 = r31
            r65 = r33
            r6 = r42
            r30 = r1
            r1 = r3
            r19 = r17
            r3 = r68
            goto L_0x0975
        L_0x0823:
            r0 = move-exception
            r68 = r3
            r70 = r4
            r71 = r5
            r42 = r6
            r14 = r7
            r74 = r12
            r7 = 5
            r5 = r78
            r3 = r1
            r1 = r2
            r2 = r11
            r8 = r18
            r9 = r19
            r61 = r29
            r63 = r31
            r65 = r33
            r30 = r1
            r1 = r3
            r19 = r17
            r3 = r68
            goto L_0x0975
        L_0x0848:
            r0 = move-exception
            r68 = r3
            r70 = r4
            r71 = r5
            r42 = r6
            r14 = r7
            r74 = r12
            r7 = 5
            r5 = r78
            r3 = r1
            r1 = r2
            r2 = r11
            r41 = r9
            r8 = r18
            r9 = r19
            r61 = r29
            r63 = r31
            r65 = r33
            r30 = r1
            r1 = r3
            r19 = r17
            r3 = r68
            goto L_0x0975
        L_0x086f:
            r0 = move-exception
            r68 = r3
            r70 = r4
            r71 = r5
            r42 = r6
            r14 = r7
            r74 = r12
            r7 = 5
            r5 = r78
            r3 = r1
            r1 = r2
            r2 = r11
            r40 = r8
            r41 = r9
            r8 = r18
            r9 = r19
            r61 = r29
            r63 = r31
            r65 = r33
            r30 = r1
            r1 = r3
            r19 = r17
            r3 = r68
            goto L_0x0975
        L_0x0898:
            r0 = move-exception
            r68 = r3
            r70 = r4
            r71 = r5
            r14 = r7
            r74 = r12
            r7 = 5
            r5 = r78
            r3 = r1
            r1 = r2
            r2 = r11
            r40 = r8
            r41 = r9
            r8 = r18
            r9 = r19
            r61 = r29
            r63 = r31
            r65 = r33
            r30 = r1
            r1 = r3
            r19 = r17
            r3 = r68
            goto L_0x0975
        L_0x08bf:
            r0 = move-exception
            r68 = r3
            r71 = r5
            r14 = r7
            r74 = r12
            r7 = 5
            r5 = r78
            r3 = r1
            r1 = r2
            r2 = r11
            r40 = r8
            r41 = r9
            r8 = r18
            r9 = r19
            r61 = r29
            r63 = r31
            r65 = r33
            r30 = r1
            r1 = r3
            r19 = r17
            r3 = r68
            goto L_0x0975
        L_0x08e4:
            r0 = move-exception
            r68 = r3
            r71 = r5
            r14 = r7
            r74 = r12
            r7 = 5
            r5 = r78
            r3 = r1
            r1 = r2
            r2 = r11
            r40 = r8
            r41 = r9
            r39 = r15
            r8 = r18
            r9 = r19
            r61 = r29
            r63 = r31
            r65 = r33
            r30 = r1
            r1 = r3
            r19 = r17
            r3 = r68
            goto L_0x0975
        L_0x090b:
            r0 = move-exception
            r71 = r5
            r14 = r7
            r74 = r12
            r7 = 5
            r5 = r78
            r77 = r3
            r3 = r1
            r1 = r2
            r2 = r11
            r11 = r77
            r40 = r8
            r41 = r9
            r39 = r15
            r8 = r18
            r9 = r19
            r61 = r29
            r63 = r31
            r65 = r33
            r30 = r1
            r1 = r3
            r3 = r11
            r19 = r17
            goto L_0x0975
        L_0x0932:
            r0 = move-exception
            r71 = r5
            r14 = r7
            r74 = r12
            r7 = 5
            r5 = r78
            r12 = r2
            r2 = r11
            r11 = r3
            r3 = r1
            r40 = r8
            r41 = r9
            r3 = r11
            r39 = r15
            r8 = r18
            r9 = r19
            r61 = r29
            r63 = r31
            r65 = r33
            r30 = r12
            r19 = r17
            goto L_0x0975
        L_0x0955:
            r0 = move-exception
            r71 = r5
            r14 = r7
            r74 = r12
            r7 = 5
            r5 = r78
            r12 = r2
            r2 = r11
            r11 = r3
            r40 = r8
            r41 = r9
            r39 = r15
            r8 = r18
            r9 = r19
            r61 = r29
            r63 = r31
            r65 = r33
            r30 = r12
            r19 = r17
        L_0x0975:
            java.lang.StringBuilder r11 = new java.lang.StringBuilder
            r11.<init>()
            r11.append(r14)
            r11.append(r1)
            r11.append(r2)
            r11.append(r3)
            r11.append(r10)
            java.lang.String r2 = r79.getPositionDescription()
            r11.append(r2)
            java.lang.String r2 = r11.toString()
            com.android.server.pm.PackageManagerService.reportSettingsProblem(r7, r2)
            r15 = r1
            r7 = r3
            r70 = r4
            r42 = r6
            r12 = r8
            r11 = r19
            r3 = r21
            r4 = r22
            r6 = r23
            r13 = r25
            r8 = r26
            r2 = r35
            r1 = r39
            r77 = r16
            r16 = r9
            r9 = r77
        L_0x09b4:
            if (r2 == 0) goto L_0x0c6b
            r5 = r71
            boolean r0 = r5.equals(r1)
            r2.uidError = r0
            r2.installerPackageName = r3
            boolean r0 = r5.equals(r4)
            r2.isOrphaned = r0
            r2.volumeUuid = r6
            r2.categoryHint = r8
            r2.legacyNativeLibraryPathString = r9
            r2.primaryCpuAbiString = r11
            r2.secondaryCpuAbiString = r12
            boolean r0 = r5.equals(r13)
            r2.updateAvailable = r0
            java.lang.String r0 = "enabled"
            r17 = r1
            r18 = r4
            r19 = r6
            r4 = 0
            r1 = r79
            java.lang.String r6 = r1.getAttributeValue(r4, r0)
            if (r6 == 0) goto L_0x0a43
            int r0 = java.lang.Integer.parseInt(r6)     // Catch:{ NumberFormatException -> 0x09f4 }
            r21 = r8
            r8 = 0
            r2.setEnabled(r0, r8, r4)     // Catch:{ NumberFormatException -> 0x09f2 }
        L_0x09f1:
            goto L_0x0a49
        L_0x09f2:
            r0 = move-exception
            goto L_0x09f8
        L_0x09f4:
            r0 = move-exception
            r21 = r8
            r8 = 0
        L_0x09f8:
            boolean r5 = r6.equalsIgnoreCase(r5)
            if (r5 == 0) goto L_0x0a03
            r5 = 1
            r2.setEnabled(r5, r8, r4)
            goto L_0x09f1
        L_0x0a03:
            r5 = 1
            java.lang.String r5 = "false"
            boolean r5 = r6.equalsIgnoreCase(r5)
            if (r5 == 0) goto L_0x0a11
            r5 = 2
            r2.setEnabled(r5, r8, r4)
            goto L_0x09f1
        L_0x0a11:
            java.lang.String r5 = "default"
            boolean r5 = r6.equalsIgnoreCase(r5)
            if (r5 == 0) goto L_0x0a1d
            r2.setEnabled(r8, r8, r4)
            goto L_0x09f1
        L_0x0a1d:
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>()
            r5.append(r14)
            r5.append(r15)
            java.lang.String r14 = " has bad enabled value: "
            r5.append(r14)
            r5.append(r7)
            r5.append(r10)
            java.lang.String r10 = r79.getPositionDescription()
            r5.append(r10)
            java.lang.String r5 = r5.toString()
            r10 = 5
            com.android.server.pm.PackageManagerService.reportSettingsProblem(r10, r5)
            goto L_0x09f1
        L_0x0a43:
            r21 = r8
            r8 = 0
            r2.setEnabled(r8, r8, r4)
        L_0x0a49:
            if (r3 == 0) goto L_0x0a53
            r5 = r78
            android.util.ArraySet<java.lang.String> r0 = r5.mInstallerPackages
            r0.add(r3)
            goto L_0x0a55
        L_0x0a53:
            r5 = r78
        L_0x0a55:
            int r0 = r79.getDepth()
        L_0x0a59:
            int r10 = r79.next()
            r14 = r10
            r4 = 1
            if (r10 == r4) goto L_0x0c62
            r4 = 3
            if (r14 != r4) goto L_0x0a71
            int r10 = r79.getDepth()
            if (r10 <= r0) goto L_0x0a6b
            goto L_0x0a71
        L_0x0a6b:
            r25 = r3
            r26 = r7
            goto L_0x0c6a
        L_0x0a71:
            if (r14 == r4) goto L_0x0c4a
            r4 = 4
            if (r14 != r4) goto L_0x0a78
            r4 = 0
            goto L_0x0a59
        L_0x0a78:
            java.lang.String r4 = r79.getName()
            java.lang.String r10 = "disabled-components"
            boolean r10 = r4.equals(r10)
            if (r10 == 0) goto L_0x0a97
            r5.readDisabledComponentsLPw(r2, r1, r8)
            r23 = r0
            r25 = r3
            r22 = r6
            r26 = r7
            r3 = r74
            r6 = 0
            r7 = 5
            r31 = 1
            goto L_0x0c3c
        L_0x0a97:
            java.lang.String r10 = "enabled-components"
            boolean r10 = r4.equals(r10)
            if (r10 == 0) goto L_0x0ab2
            r5.readEnabledComponentsLPw(r2, r1, r8)
            r23 = r0
            r25 = r3
            r22 = r6
            r26 = r7
            r3 = r74
            r6 = 0
            r7 = 5
            r31 = 1
            goto L_0x0c3c
        L_0x0ab2:
            java.lang.String r10 = "sigs"
            boolean r10 = r4.equals(r10)
            if (r10 == 0) goto L_0x0ad2
            com.android.server.pm.PackageSignatures r10 = r2.signatures
            java.util.ArrayList<android.content.pm.Signature> r8 = r5.mPastSignatures
            r10.readXml(r1, r8)
            r23 = r0
            r25 = r3
            r22 = r6
            r26 = r7
            r3 = r74
            r6 = 0
            r7 = 5
            r31 = 1
            goto L_0x0c3c
        L_0x0ad2:
            java.lang.String r8 = "perms"
            boolean r8 = r4.equals(r8)
            if (r8 == 0) goto L_0x0af6
            com.android.server.pm.permission.PermissionsState r8 = r2.getPermissionsState()
            r5.readInstallPermissionsLPr(r1, r8)
            r8 = 1
            r2.installPermissionsFixed = r8
            r23 = r0
            r25 = r3
            r22 = r6
            r26 = r7
            r31 = r8
            r3 = r74
            r6 = 0
            r7 = 5
            goto L_0x0c3c
        L_0x0af6:
            java.lang.String r8 = "proper-signing-keyset"
            boolean r8 = r4.equals(r8)
            java.lang.String r10 = "identifier"
            if (r8 == 0) goto L_0x0b51
            r8 = 0
            java.lang.String r10 = r1.getAttributeValue(r8, r10)
            r22 = r6
            r8 = r7
            long r6 = java.lang.Long.parseLong(r10)
            android.util.ArrayMap<java.lang.Long, java.lang.Integer> r10 = r5.mKeySetRefs
            r23 = r0
            java.lang.Long r0 = java.lang.Long.valueOf(r6)
            java.lang.Object r0 = r10.get(r0)
            java.lang.Integer r0 = (java.lang.Integer) r0
            if (r0 == 0) goto L_0x0b38
            android.util.ArrayMap<java.lang.Long, java.lang.Integer> r10 = r5.mKeySetRefs
            r25 = r3
            java.lang.Long r3 = java.lang.Long.valueOf(r6)
            int r26 = r0.intValue()
            r29 = 1
            int r26 = r26 + 1
            r31 = r0
            java.lang.Integer r0 = java.lang.Integer.valueOf(r26)
            r10.put(r3, r0)
            goto L_0x0b4b
        L_0x0b38:
            r31 = r0
            r25 = r3
            r29 = 1
            android.util.ArrayMap<java.lang.Long, java.lang.Integer> r0 = r5.mKeySetRefs
            java.lang.Long r3 = java.lang.Long.valueOf(r6)
            java.lang.Integer r10 = java.lang.Integer.valueOf(r29)
            r0.put(r3, r10)
        L_0x0b4b:
            com.android.server.pm.PackageKeySetData r0 = r2.keySetData
            r0.setProperSigningKeySet(r6)
            goto L_0x0b61
        L_0x0b51:
            r23 = r0
            r25 = r3
            r22 = r6
            r8 = r7
            java.lang.String r0 = "signing-keyset"
            boolean r0 = r4.equals(r0)
            if (r0 == 0) goto L_0x0b6b
        L_0x0b61:
            r26 = r8
            r3 = r74
            r6 = 0
            r7 = 5
            r31 = 1
            goto L_0x0c3c
        L_0x0b6b:
            java.lang.String r0 = "upgrade-keyset"
            boolean r0 = r4.equals(r0)
            if (r0 == 0) goto L_0x0b8c
            r3 = 0
            java.lang.String r0 = r1.getAttributeValue(r3, r10)
            long r6 = java.lang.Long.parseLong(r0)
            com.android.server.pm.PackageKeySetData r0 = r2.keySetData
            r0.addUpgradeKeySetById(r6)
            r26 = r8
            r3 = r74
            r6 = 0
            r7 = 5
            r31 = 1
            goto L_0x0c3c
        L_0x0b8c:
            java.lang.String r0 = "defined-keyset"
            boolean r0 = r4.equals(r0)
            if (r0 == 0) goto L_0x0be8
            r3 = 0
            java.lang.String r0 = r1.getAttributeValue(r3, r10)
            long r6 = java.lang.Long.parseLong(r0)
            java.lang.String r0 = "alias"
            java.lang.String r0 = r1.getAttributeValue(r3, r0)
            android.util.ArrayMap<java.lang.Long, java.lang.Integer> r3 = r5.mKeySetRefs
            java.lang.Long r10 = java.lang.Long.valueOf(r6)
            java.lang.Object r3 = r3.get(r10)
            java.lang.Integer r3 = (java.lang.Integer) r3
            if (r3 == 0) goto L_0x0bcb
            android.util.ArrayMap<java.lang.Long, java.lang.Integer> r10 = r5.mKeySetRefs
            r26 = r8
            java.lang.Long r8 = java.lang.Long.valueOf(r6)
            int r29 = r3.intValue()
            r31 = 1
            int r29 = r29 + 1
            r32 = r3
            java.lang.Integer r3 = java.lang.Integer.valueOf(r29)
            r10.put(r8, r3)
            goto L_0x0bde
        L_0x0bcb:
            r32 = r3
            r26 = r8
            r31 = 1
            android.util.ArrayMap<java.lang.Long, java.lang.Integer> r3 = r5.mKeySetRefs
            java.lang.Long r8 = java.lang.Long.valueOf(r6)
            java.lang.Integer r10 = java.lang.Integer.valueOf(r31)
            r3.put(r8, r10)
        L_0x0bde:
            com.android.server.pm.PackageKeySetData r3 = r2.keySetData
            r3.addDefinedKeySet(r6, r0)
            r3 = r74
            r6 = 0
            r7 = 5
            goto L_0x0c3c
        L_0x0be8:
            r26 = r8
            r31 = 1
            java.lang.String r0 = "domain-verification"
            boolean r0 = r4.equals(r0)
            if (r0 == 0) goto L_0x0bfc
            r5.readDomainVerificationLPw(r1, r2)
            r3 = r74
            r6 = 0
            r7 = 5
            goto L_0x0c3c
        L_0x0bfc:
            java.lang.String r0 = "child-package"
            boolean r0 = r4.equals(r0)
            if (r0 == 0) goto L_0x0c1d
            r3 = r74
            r6 = 0
            java.lang.String r0 = r1.getAttributeValue(r6, r3)
            java.util.List r7 = r2.childPackageNames
            if (r7 != 0) goto L_0x0c16
            java.util.ArrayList r7 = new java.util.ArrayList
            r7.<init>()
            r2.childPackageNames = r7
        L_0x0c16:
            java.util.List r7 = r2.childPackageNames
            r7.add(r0)
            r7 = 5
            goto L_0x0c3c
        L_0x0c1d:
            r3 = r74
            r6 = 0
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r7 = "Unknown element under <package>: "
            r0.append(r7)
            java.lang.String r7 = r79.getName()
            r0.append(r7)
            java.lang.String r0 = r0.toString()
            r7 = 5
            com.android.server.pm.PackageManagerService.reportSettingsProblem(r7, r0)
            com.android.internal.util.XmlUtils.skipCurrentTag(r79)
        L_0x0c3c:
            r74 = r3
            r4 = r6
            r6 = r22
            r0 = r23
            r3 = r25
            r7 = r26
            r8 = 0
            goto L_0x0a59
        L_0x0c4a:
            r23 = r0
            r25 = r3
            r22 = r6
            r26 = r7
            r3 = r74
            r6 = 0
            r7 = 5
            r31 = 1
            r4 = r6
            r6 = r22
            r3 = r25
            r7 = r26
            r8 = 0
            goto L_0x0a59
        L_0x0c62:
            r23 = r0
            r25 = r3
            r22 = r6
            r26 = r7
        L_0x0c6a:
            goto L_0x0c7c
        L_0x0c6b:
            r17 = r1
            r25 = r3
            r18 = r4
            r19 = r6
            r26 = r7
            r21 = r8
            r1 = r79
            com.android.internal.util.XmlUtils.skipCurrentTag(r79)
        L_0x0c7c:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.pm.Settings.readPackageLPw(org.xmlpull.v1.XmlPullParser):void");
    }

    private void readDisabledComponentsLPw(PackageSettingBase packageSetting, XmlPullParser parser, int userId) throws IOException, XmlPullParserException {
        int outerDepth = parser.getDepth();
        while (true) {
            int next = parser.next();
            int type = next;
            if (next == 1) {
                return;
            }
            if (type == 3 && parser.getDepth() <= outerDepth) {
                return;
            }
            if (!(type == 3 || type == 4)) {
                if (parser.getName().equals(TAG_ITEM)) {
                    String name = parser.getAttributeValue((String) null, ATTR_NAME);
                    if (name != null) {
                        packageSetting.addDisabledComponent(name.intern(), userId);
                    } else {
                        PackageManagerService.reportSettingsProblem(5, "Error in package manager settings: <disabled-components> has no name at " + parser.getPositionDescription());
                    }
                } else {
                    PackageManagerService.reportSettingsProblem(5, "Unknown element under <disabled-components>: " + parser.getName());
                }
                XmlUtils.skipCurrentTag(parser);
            }
        }
    }

    private void readEnabledComponentsLPw(PackageSettingBase packageSetting, XmlPullParser parser, int userId) throws IOException, XmlPullParserException {
        int outerDepth = parser.getDepth();
        while (true) {
            int next = parser.next();
            int type = next;
            if (next == 1) {
                return;
            }
            if (type == 3 && parser.getDepth() <= outerDepth) {
                return;
            }
            if (!(type == 3 || type == 4)) {
                if (parser.getName().equals(TAG_ITEM)) {
                    String name = parser.getAttributeValue((String) null, ATTR_NAME);
                    if (name != null) {
                        packageSetting.addEnabledComponent(name.intern(), userId);
                    } else {
                        PackageManagerService.reportSettingsProblem(5, "Error in package manager settings: <enabled-components> has no name at " + parser.getPositionDescription());
                    }
                } else {
                    PackageManagerService.reportSettingsProblem(5, "Unknown element under <enabled-components>: " + parser.getName());
                }
                XmlUtils.skipCurrentTag(parser);
            }
        }
    }

    private void readSharedUserLPw(XmlPullParser parser) throws XmlPullParserException, IOException {
        int pkgFlags = 0;
        SharedUserSetting su = null;
        try {
            String name = parser.getAttributeValue((String) null, ATTR_NAME);
            String idStr = parser.getAttributeValue((String) null, "userId");
            int userId = idStr != null ? Integer.parseInt(idStr) : 0;
            if ("true".equals(parser.getAttributeValue((String) null, "system"))) {
                pkgFlags = 0 | 1;
            }
            if (name == null) {
                PackageManagerService.reportSettingsProblem(5, "Error in package manager settings: <shared-user> has no name at " + parser.getPositionDescription());
            } else if (userId == 0) {
                PackageManagerService.reportSettingsProblem(5, "Error in package manager settings: shared-user " + name + " has bad userId " + idStr + " at " + parser.getPositionDescription());
            } else {
                SharedUserSetting addSharedUserLPw = addSharedUserLPw(name.intern(), userId, pkgFlags, 0);
                su = addSharedUserLPw;
                if (addSharedUserLPw == null) {
                    PackageManagerService.reportSettingsProblem(6, "Occurred while parsing settings at " + parser.getPositionDescription());
                }
            }
        } catch (NumberFormatException e) {
            PackageManagerService.reportSettingsProblem(5, "Error in package manager settings: package " + null + " has bad userId " + null + " at " + parser.getPositionDescription());
        }
        if (su != null) {
            int outerDepth = parser.getDepth();
            while (true) {
                int next = parser.next();
                int type = next;
                if (next == 1) {
                    return;
                }
                if (type == 3 && parser.getDepth() <= outerDepth) {
                    return;
                }
                if (!(type == 3 || type == 4)) {
                    String tagName = parser.getName();
                    if (tagName.equals("sigs")) {
                        su.signatures.readXml(parser, this.mPastSignatures);
                    } else if (tagName.equals(TAG_PERMISSIONS)) {
                        readInstallPermissionsLPr(parser, su.getPermissionsState());
                    } else {
                        PackageManagerService.reportSettingsProblem(5, "Unknown element under <shared-user>: " + parser.getName());
                        XmlUtils.skipCurrentTag(parser);
                    }
                }
            }
        } else {
            XmlUtils.skipCurrentTag(parser);
        }
    }

    /* access modifiers changed from: package-private */
    /* JADX WARNING: Code restructure failed: missing block: B:36:0x0090, code lost:
        r8 = 0;
     */
    /* JADX WARNING: Removed duplicated region for block: B:26:0x005f A[Catch:{ all -> 0x0089, all -> 0x00d3 }] */
    /* JADX WARNING: Removed duplicated region for block: B:28:0x0064 A[Catch:{ all -> 0x0089, all -> 0x00d3 }] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void createNewUserLI(com.android.server.pm.PackageManagerService r21, com.android.server.pm.Installer r22, int r23, java.lang.String[] r24) {
        /*
            r20 = this;
            r1 = r20
            r10 = r23
            android.util.ArrayMap<java.lang.String, com.android.server.pm.PackageSetting> r2 = r1.mPackages
            monitor-enter(r2)
            android.util.ArrayMap<java.lang.String, com.android.server.pm.PackageSetting> r0 = r1.mPackages     // Catch:{ all -> 0x00d3 }
            java.util.Collection r0 = r0.values()     // Catch:{ all -> 0x00d3 }
            int r3 = r0.size()     // Catch:{ all -> 0x00d3 }
            r11 = r3
            java.lang.String[] r3 = new java.lang.String[r11]     // Catch:{ all -> 0x00d3 }
            r12 = r3
            java.lang.String[] r3 = new java.lang.String[r11]     // Catch:{ all -> 0x00d3 }
            r13 = r3
            int[] r3 = new int[r11]     // Catch:{ all -> 0x00d3 }
            r14 = r3
            java.lang.String[] r3 = new java.lang.String[r11]     // Catch:{ all -> 0x00d3 }
            r15 = r3
            int[] r3 = new int[r11]     // Catch:{ all -> 0x00d3 }
            r16 = r3
            java.util.Iterator r3 = r0.iterator()     // Catch:{ all -> 0x00d3 }
            r4 = 0
        L_0x0027:
            if (r4 >= r11) goto L_0x008d
            java.lang.Object r5 = r3.next()     // Catch:{ all -> 0x0089 }
            com.android.server.pm.PackageSetting r5 = (com.android.server.pm.PackageSetting) r5     // Catch:{ all -> 0x0089 }
            android.content.pm.PackageParser$Package r6 = r5.pkg     // Catch:{ all -> 0x0089 }
            if (r6 == 0) goto L_0x0084
            android.content.pm.PackageParser$Package r6 = r5.pkg     // Catch:{ all -> 0x0089 }
            android.content.pm.ApplicationInfo r6 = r6.applicationInfo     // Catch:{ all -> 0x0089 }
            if (r6 != 0) goto L_0x003c
            r9 = r24
            goto L_0x0086
        L_0x003c:
            boolean r6 = r5.isSystem()     // Catch:{ all -> 0x0089 }
            if (r6 == 0) goto L_0x0056
            java.lang.String r6 = r5.name     // Catch:{ all -> 0x0089 }
            r9 = r24
            boolean r6 = com.android.internal.util.ArrayUtils.contains(r9, r6)     // Catch:{ all -> 0x00d3 }
            if (r6 != 0) goto L_0x0058
            android.content.pm.PackageParser$Package r6 = r5.pkg     // Catch:{ all -> 0x00d3 }
            android.content.pm.ApplicationInfo r6 = r6.applicationInfo     // Catch:{ all -> 0x00d3 }
            boolean r6 = r6.hiddenUntilInstalled     // Catch:{ all -> 0x00d3 }
            if (r6 != 0) goto L_0x0058
            r6 = 1
            goto L_0x0059
        L_0x0056:
            r9 = r24
        L_0x0058:
            r6 = 0
        L_0x0059:
            boolean r7 = com.android.server.pm.SettingsInjector.checkXSpaceApp(r5, r10)     // Catch:{ all -> 0x00d3 }
            if (r7 != 0) goto L_0x0062
            r5.setInstalled(r6, r10)     // Catch:{ all -> 0x00d3 }
        L_0x0062:
            if (r6 != 0) goto L_0x0067
            r1.writeKernelMappingLPr(r5)     // Catch:{ all -> 0x00d3 }
        L_0x0067:
            java.lang.String r7 = r5.volumeUuid     // Catch:{ all -> 0x00d3 }
            r12[r4] = r7     // Catch:{ all -> 0x00d3 }
            java.lang.String r7 = r5.name     // Catch:{ all -> 0x00d3 }
            r13[r4] = r7     // Catch:{ all -> 0x00d3 }
            int r7 = r5.appId     // Catch:{ all -> 0x00d3 }
            r14[r4] = r7     // Catch:{ all -> 0x00d3 }
            android.content.pm.PackageParser$Package r7 = r5.pkg     // Catch:{ all -> 0x00d3 }
            android.content.pm.ApplicationInfo r7 = r7.applicationInfo     // Catch:{ all -> 0x00d3 }
            java.lang.String r7 = r7.seInfo     // Catch:{ all -> 0x00d3 }
            r15[r4] = r7     // Catch:{ all -> 0x00d3 }
            android.content.pm.PackageParser$Package r7 = r5.pkg     // Catch:{ all -> 0x00d3 }
            android.content.pm.ApplicationInfo r7 = r7.applicationInfo     // Catch:{ all -> 0x00d3 }
            int r7 = r7.targetSdkVersion     // Catch:{ all -> 0x00d3 }
            r16[r4] = r7     // Catch:{ all -> 0x00d3 }
            goto L_0x0086
        L_0x0084:
            r9 = r24
        L_0x0086:
            int r4 = r4 + 1
            goto L_0x0027
        L_0x0089:
            r0 = move-exception
            r9 = r24
            goto L_0x00d4
        L_0x008d:
            r9 = r24
            monitor-exit(r2)     // Catch:{ all -> 0x00d3 }
            r0 = 0
            r8 = r0
        L_0x0092:
            if (r8 >= r11) goto L_0x00c6
            r0 = r13[r8]
            if (r0 != 0) goto L_0x009b
            r19 = r8
            goto L_0x00c1
        L_0x009b:
            r17 = 3
            r3 = r12[r8]     // Catch:{ InstallerException -> 0x00b7 }
            r4 = r13[r8]     // Catch:{ InstallerException -> 0x00b7 }
            r6 = 3
            r7 = r14[r8]     // Catch:{ InstallerException -> 0x00b7 }
            r0 = r15[r8]     // Catch:{ InstallerException -> 0x00b7 }
            r18 = r16[r8]     // Catch:{ InstallerException -> 0x00b7 }
            r2 = r22
            r5 = r23
            r19 = r8
            r8 = r0
            r9 = r18
            r2.createAppData(r3, r4, r5, r6, r7, r8, r9)     // Catch:{ InstallerException -> 0x00b5 }
            goto L_0x00c1
        L_0x00b5:
            r0 = move-exception
            goto L_0x00ba
        L_0x00b7:
            r0 = move-exception
            r19 = r8
        L_0x00ba:
            java.lang.String r2 = "PackageSettings"
            java.lang.String r3 = "Failed to prepare app data"
            android.util.Slog.w(r2, r3, r0)
        L_0x00c1:
            int r8 = r19 + 1
            r9 = r24
            goto L_0x0092
        L_0x00c6:
            r19 = r8
            android.util.ArrayMap<java.lang.String, com.android.server.pm.PackageSetting> r3 = r1.mPackages
            monitor-enter(r3)
            r1.applyDefaultPreferredAppsLPw(r10)     // Catch:{ all -> 0x00d0 }
            monitor-exit(r3)     // Catch:{ all -> 0x00d0 }
            return
        L_0x00d0:
            r0 = move-exception
            monitor-exit(r3)     // Catch:{ all -> 0x00d0 }
            throw r0
        L_0x00d3:
            r0 = move-exception
        L_0x00d4:
            monitor-exit(r2)     // Catch:{ all -> 0x00d3 }
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.pm.Settings.createNewUserLI(com.android.server.pm.PackageManagerService, com.android.server.pm.Installer, int, java.lang.String[]):void");
    }

    /* access modifiers changed from: package-private */
    public void removeUserLPw(int userId) {
        for (Map.Entry<String, PackageSetting> entry : this.mPackages.entrySet()) {
            entry.getValue().removeUser(userId);
        }
        this.mPreferredActivities.remove(userId);
        getUserPackagesStateFile(userId).delete();
        getUserPackagesStateBackupFile(userId).delete();
        removeCrossProfileIntentFiltersLPw(userId);
        this.mRuntimePermissionsPersistence.onUserRemovedLPw(userId);
        writePackageListLPr();
        writeKernelRemoveUserLPr(userId);
    }

    /* access modifiers changed from: package-private */
    public void removeCrossProfileIntentFiltersLPw(int userId) {
        synchronized (this.mCrossProfileIntentResolvers) {
            if (this.mCrossProfileIntentResolvers.get(userId) != null) {
                this.mCrossProfileIntentResolvers.remove(userId);
                writePackageRestrictionsLPr(userId);
            }
            int count = this.mCrossProfileIntentResolvers.size();
            for (int i = 0; i < count; i++) {
                int sourceUserId = this.mCrossProfileIntentResolvers.keyAt(i);
                CrossProfileIntentResolver cpir = this.mCrossProfileIntentResolvers.get(sourceUserId);
                boolean needsWriting = false;
                Iterator<CrossProfileIntentFilter> it = new ArraySet<>(cpir.filterSet()).iterator();
                while (it.hasNext()) {
                    CrossProfileIntentFilter cpif = it.next();
                    if (cpif.getTargetUserId() == userId) {
                        needsWriting = true;
                        cpir.removeFilter(cpif);
                    }
                }
                if (needsWriting) {
                    writePackageRestrictionsLPr(sourceUserId);
                }
            }
        }
    }

    private void setFirstAvailableUid(int uid) {
        if (uid > mFirstAvailableUid) {
            mFirstAvailableUid = uid;
        }
    }

    private int acquireAndRegisterNewAppIdLPw(SettingBase obj) {
        int size = this.mAppIds.size();
        for (int i = mFirstAvailableUid; i < size; i++) {
            if (this.mAppIds.get(i) == null) {
                this.mAppIds.set(i, obj);
                return i + 10000;
            }
        }
        if (size > 9999) {
            return -1;
        }
        this.mAppIds.add(obj);
        return size + 10000;
    }

    public VerifierDeviceIdentity getVerifierDeviceIdentityLPw() {
        if (this.mVerifierDeviceIdentity == null) {
            this.mVerifierDeviceIdentity = VerifierDeviceIdentity.generate();
            writeLPr();
        }
        return this.mVerifierDeviceIdentity;
    }

    /* access modifiers changed from: package-private */
    public boolean hasOtherDisabledSystemPkgWithChildLPr(String parentPackageName, String childPackageName) {
        int packageCount = this.mDisabledSysPackages.size();
        for (int i = 0; i < packageCount; i++) {
            PackageSetting disabledPs = this.mDisabledSysPackages.valueAt(i);
            if (disabledPs.childPackageNames != null && !disabledPs.childPackageNames.isEmpty() && !disabledPs.name.equals(parentPackageName)) {
                int childCount = disabledPs.childPackageNames.size();
                for (int j = 0; j < childCount; j++) {
                    if (((String) disabledPs.childPackageNames.get(j)).equals(childPackageName)) {
                        return true;
                    }
                }
                continue;
            }
        }
        return false;
    }

    public PackageSetting getDisabledSystemPkgLPr(String name) {
        return this.mDisabledSysPackages.get(name);
    }

    public PackageSetting getDisabledSystemPkgLPr(PackageSetting enabledPackageSetting) {
        if (enabledPackageSetting == null) {
            return null;
        }
        return getDisabledSystemPkgLPr(enabledPackageSetting.name);
    }

    public PackageSetting[] getChildSettingsLPr(PackageSetting parentPackageSetting) {
        if (parentPackageSetting == null || !parentPackageSetting.hasChildPackages()) {
            return null;
        }
        int childCount = parentPackageSetting.childPackageNames.size();
        PackageSetting[] children = new PackageSetting[childCount];
        for (int i = 0; i < childCount; i++) {
            children[i] = this.mPackages.get(parentPackageSetting.childPackageNames.get(i));
        }
        return children;
    }

    /* access modifiers changed from: package-private */
    public boolean isEnabledAndMatchLPr(ComponentInfo componentInfo, int flags, int userId) {
        PackageSetting ps = this.mPackages.get(componentInfo.packageName);
        if (ps == null) {
            return false;
        }
        return ps.readUserState(userId).isMatch(componentInfo, flags);
    }

    /* access modifiers changed from: package-private */
    public String getInstallerPackageNameLPr(String packageName) {
        PackageSetting pkg = this.mPackages.get(packageName);
        if (pkg != null) {
            return pkg.installerPackageName;
        }
        throw new IllegalArgumentException("Unknown package: " + packageName);
    }

    /* access modifiers changed from: package-private */
    public boolean isOrphaned(String packageName) {
        PackageSetting pkg = this.mPackages.get(packageName);
        if (pkg != null) {
            return pkg.isOrphaned;
        }
        throw new IllegalArgumentException("Unknown package: " + packageName);
    }

    /* access modifiers changed from: package-private */
    public int getApplicationEnabledSettingLPr(String packageName, int userId) {
        PackageSetting pkg = this.mPackages.get(packageName);
        if (pkg != null) {
            return pkg.getEnabled(userId);
        }
        throw new IllegalArgumentException("Unknown package: " + packageName);
    }

    /* access modifiers changed from: package-private */
    public int getComponentEnabledSettingLPr(ComponentName componentName, int userId) {
        PackageSetting pkg = this.mPackages.get(componentName.getPackageName());
        if (pkg != null) {
            return pkg.getCurrentEnabledStateLPr(componentName.getClassName(), userId);
        }
        throw new IllegalArgumentException("Unknown component: " + componentName);
    }

    /* access modifiers changed from: package-private */
    public boolean wasPackageEverLaunchedLPr(String packageName, int userId) {
        PackageSetting pkgSetting = this.mPackages.get(packageName);
        if (pkgSetting != null) {
            return !pkgSetting.getNotLaunched(userId);
        }
        throw new IllegalArgumentException("Unknown package: " + packageName);
    }

    /* access modifiers changed from: package-private */
    public boolean setPackageStoppedStateLPw(PackageManagerService pm, String packageName, boolean stopped, boolean allowedByPermission, int uid, int userId) {
        int appId = UserHandle.getAppId(uid);
        PackageSetting pkgSetting = this.mPackages.get(packageName);
        if (pkgSetting == null) {
            throw new IllegalArgumentException("Unknown package: " + packageName);
        } else if (!allowedByPermission && appId != pkgSetting.appId) {
            throw new SecurityException("Permission Denial: attempt to change stopped state from pid=" + Binder.getCallingPid() + ", uid=" + uid + ", package uid=" + pkgSetting.appId);
        } else if (pkgSetting.getStopped(userId) == stopped) {
            return false;
        } else {
            pkgSetting.setStopped(stopped, userId);
            if (!pkgSetting.getNotLaunched(userId)) {
                return true;
            }
            if (pkgSetting.installerPackageName != null) {
                pm.notifyFirstLaunch(pkgSetting.name, pkgSetting.installerPackageName, userId);
            }
            pkgSetting.setNotLaunched(false, userId);
            SettingsInjector.noftifyFirstLaunch(pm, pkgSetting, userId);
            return true;
        }
    }

    /* access modifiers changed from: package-private */
    public void setHarmfulAppWarningLPw(String packageName, CharSequence warning, int userId) {
        PackageSetting pkgSetting = this.mPackages.get(packageName);
        if (pkgSetting != null) {
            pkgSetting.setHarmfulAppWarning(userId, warning == null ? null : warning.toString());
            return;
        }
        throw new IllegalArgumentException("Unknown package: " + packageName);
    }

    /* access modifiers changed from: package-private */
    public String getHarmfulAppWarningLPr(String packageName, int userId) {
        PackageSetting pkgSetting = this.mPackages.get(packageName);
        if (pkgSetting != null) {
            return pkgSetting.getHarmfulAppWarning(userId);
        }
        throw new IllegalArgumentException("Unknown package: " + packageName);
    }

    private static List<UserInfo> getAllUsers(UserManagerService userManager) {
        return getUsers(userManager, false);
    }

    /* JADX INFO: finally extract failed */
    private static List<UserInfo> getUsers(UserManagerService userManager, boolean excludeDying) {
        long id = Binder.clearCallingIdentity();
        try {
            List<UserInfo> users = userManager.getUsers(excludeDying);
            Binder.restoreCallingIdentity(id);
            return users;
        } catch (NullPointerException e) {
            Binder.restoreCallingIdentity(id);
            return null;
        } catch (Throwable th) {
            Binder.restoreCallingIdentity(id);
            throw th;
        }
    }

    /* access modifiers changed from: package-private */
    public List<PackageSetting> getVolumePackagesLPr(String volumeUuid) {
        ArrayList<PackageSetting> res = new ArrayList<>();
        for (int i = 0; i < this.mPackages.size(); i++) {
            PackageSetting setting = this.mPackages.valueAt(i);
            if (Objects.equals(volumeUuid, setting.volumeUuid)) {
                res.add(setting);
            }
        }
        return res;
    }

    static void printFlags(PrintWriter pw, int val, Object[] spec) {
        pw.print("[ ");
        for (int i = 0; i < spec.length; i += 2) {
            if ((val & spec[i].intValue()) != 0) {
                pw.print(spec[i + 1]);
                pw.print(" ");
            }
        }
        pw.print("]");
    }

    /* access modifiers changed from: package-private */
    public void dumpVersionLPr(IndentingPrintWriter pw) {
        pw.increaseIndent();
        for (int i = 0; i < this.mVersion.size(); i++) {
            String volumeUuid = this.mVersion.keyAt(i);
            VersionInfo ver = this.mVersion.valueAt(i);
            if (Objects.equals(StorageManager.UUID_PRIVATE_INTERNAL, volumeUuid)) {
                pw.println("Internal:");
            } else if (Objects.equals("primary_physical", volumeUuid)) {
                pw.println("External:");
            } else {
                pw.println("UUID " + volumeUuid + ":");
            }
            pw.increaseIndent();
            pw.printPair(ATTR_SDK_VERSION, Integer.valueOf(ver.sdkVersion));
            pw.printPair(ATTR_DATABASE_VERSION, Integer.valueOf(ver.databaseVersion));
            pw.println();
            pw.printPair(ATTR_FINGERPRINT, ver.fingerprint);
            pw.println();
            pw.decreaseIndent();
        }
        pw.decreaseIndent();
    }

    /* access modifiers changed from: package-private */
    public void dumpPackageLPr(PrintWriter pw, String prefix, String checkinTag, ArraySet<String> permissionNames, PackageSetting ps, SimpleDateFormat sdf, Date date, List<UserInfo> users, boolean dumpAll, boolean dumpAllComponents) {
        String str;
        PrintWriter printWriter = pw;
        String str2 = prefix;
        String str3 = checkinTag;
        ArraySet<String> arraySet = permissionNames;
        PackageSetting packageSetting = ps;
        Date date2 = date;
        if (str3 != null) {
            printWriter.print(str3);
            printWriter.print(",");
            printWriter.print(packageSetting.realName != null ? packageSetting.realName : packageSetting.name);
            printWriter.print(",");
            printWriter.print(packageSetting.appId);
            printWriter.print(",");
            printWriter.print(packageSetting.versionCode);
            printWriter.print(",");
            printWriter.print(packageSetting.firstInstallTime);
            printWriter.print(",");
            printWriter.print(packageSetting.lastUpdateTime);
            printWriter.print(",");
            printWriter.print(packageSetting.installerPackageName != null ? packageSetting.installerPackageName : "?");
            pw.println();
            if (packageSetting.pkg != null) {
                printWriter.print(str3);
                printWriter.print("-");
                printWriter.print("splt,");
                printWriter.print("base,");
                printWriter.println(packageSetting.pkg.baseRevisionCode);
                if (packageSetting.pkg.splitNames != null) {
                    for (int i = 0; i < packageSetting.pkg.splitNames.length; i++) {
                        printWriter.print(str3);
                        printWriter.print("-");
                        printWriter.print("splt,");
                        printWriter.print(packageSetting.pkg.splitNames[i]);
                        printWriter.print(",");
                        printWriter.println(packageSetting.pkg.splitRevisionCodes[i]);
                    }
                }
            }
            for (UserInfo user : users) {
                printWriter.print(str3);
                printWriter.print("-");
                printWriter.print("usr");
                printWriter.print(",");
                printWriter.print(user.id);
                printWriter.print(",");
                printWriter.print(packageSetting.getInstalled(user.id) ? "I" : "i");
                printWriter.print(packageSetting.getHidden(user.id) ? "B" : "b");
                printWriter.print(packageSetting.getSuspended(user.id) ? "SU" : "su");
                printWriter.print(packageSetting.getStopped(user.id) ? "S" : "s");
                printWriter.print(packageSetting.getNotLaunched(user.id) ? "l" : "L");
                printWriter.print(packageSetting.getInstantApp(user.id) ? "IA" : "ia");
                printWriter.print(packageSetting.getVirtulalPreload(user.id) ? "VPI" : "vpi");
                printWriter.print(packageSetting.getHarmfulAppWarning(user.id) != null ? "HA" : "ha");
                printWriter.print(",");
                printWriter.print(packageSetting.getEnabled(user.id));
                String lastDisabledAppCaller = packageSetting.getLastDisabledAppCaller(user.id);
                printWriter.print(",");
                if (lastDisabledAppCaller != null) {
                    str = lastDisabledAppCaller;
                } else {
                    str = "?";
                }
                printWriter.print(str);
                printWriter.print(",");
                pw.println();
            }
            return;
        }
        pw.print(prefix);
        printWriter.print("Package [");
        printWriter.print(packageSetting.realName != null ? packageSetting.realName : packageSetting.name);
        printWriter.print("] (");
        printWriter.print(Integer.toHexString(System.identityHashCode(ps)));
        printWriter.println("):");
        if (packageSetting.realName != null) {
            pw.print(prefix);
            printWriter.print("  compat name=");
            printWriter.println(packageSetting.name);
        }
        pw.print(prefix);
        printWriter.print("  userId=");
        printWriter.println(packageSetting.appId);
        if (packageSetting.sharedUser != null) {
            pw.print(prefix);
            printWriter.print("  sharedUser=");
            printWriter.println(packageSetting.sharedUser);
        }
        pw.print(prefix);
        printWriter.print("  pkg=");
        printWriter.println(packageSetting.pkg);
        pw.print(prefix);
        printWriter.print("  codePath=");
        printWriter.println(packageSetting.codePathString);
        if (arraySet == null) {
            pw.print(prefix);
            printWriter.print("  resourcePath=");
            printWriter.println(packageSetting.resourcePathString);
            pw.print(prefix);
            printWriter.print("  legacyNativeLibraryDir=");
            printWriter.println(packageSetting.legacyNativeLibraryPathString);
            pw.print(prefix);
            printWriter.print("  primaryCpuAbi=");
            printWriter.println(packageSetting.primaryCpuAbiString);
            pw.print(prefix);
            printWriter.print("  secondaryCpuAbi=");
            printWriter.println(packageSetting.secondaryCpuAbiString);
        }
        pw.print(prefix);
        printWriter.print("  versionCode=");
        printWriter.print(packageSetting.versionCode);
        if (packageSetting.pkg != null) {
            printWriter.print(" minSdk=");
            printWriter.print(packageSetting.pkg.applicationInfo.minSdkVersion);
            printWriter.print(" targetSdk=");
            printWriter.print(packageSetting.pkg.applicationInfo.targetSdkVersion);
        }
        pw.println();
        if (packageSetting.pkg != null) {
            if (packageSetting.pkg.parentPackage != null) {
                PackageParser.Package parentPkg = packageSetting.pkg.parentPackage;
                PackageSetting pps = this.mPackages.get(parentPkg.packageName);
                if (pps == null || !pps.codePathString.equals(parentPkg.codePath)) {
                    pps = this.mDisabledSysPackages.get(parentPkg.packageName);
                }
                if (pps != null) {
                    pw.print(prefix);
                    printWriter.print("  parentPackage=");
                    printWriter.println(pps.realName != null ? pps.realName : pps.name);
                }
            } else if (packageSetting.pkg.childPackages != null) {
                pw.print(prefix);
                printWriter.print("  childPackages=[");
                int childCount = packageSetting.pkg.childPackages.size();
                for (int i2 = 0; i2 < childCount; i2++) {
                    PackageParser.Package childPkg = (PackageParser.Package) packageSetting.pkg.childPackages.get(i2);
                    PackageSetting cps = this.mPackages.get(childPkg.packageName);
                    if (cps == null || !cps.codePathString.equals(childPkg.codePath)) {
                        cps = this.mDisabledSysPackages.get(childPkg.packageName);
                    }
                    if (cps != null) {
                        if (i2 > 0) {
                            printWriter.print(", ");
                        }
                        printWriter.print(cps.realName != null ? cps.realName : cps.name);
                    }
                }
                printWriter.println("]");
            }
            pw.print(prefix);
            printWriter.print("  versionName=");
            printWriter.println(packageSetting.pkg.mVersionName);
            pw.print(prefix);
            printWriter.print("  splits=");
            dumpSplitNames(printWriter, packageSetting.pkg);
            pw.println();
            int apkSigningVersion = packageSetting.pkg.mSigningDetails.signatureSchemeVersion;
            pw.print(prefix);
            printWriter.print("  apkSigningVersion=");
            printWriter.println(apkSigningVersion);
            pw.print(prefix);
            printWriter.print("  applicationInfo=");
            printWriter.println(packageSetting.pkg.applicationInfo.toString());
            pw.print(prefix);
            printWriter.print("  flags=");
            printFlags(printWriter, packageSetting.pkg.applicationInfo.flags, FLAG_DUMP_SPEC);
            pw.println();
            if (packageSetting.pkg.applicationInfo.privateFlags != 0) {
                pw.print(prefix);
                printWriter.print("  privateFlags=");
                printFlags(printWriter, packageSetting.pkg.applicationInfo.privateFlags, PRIVATE_FLAG_DUMP_SPEC);
                pw.println();
            }
            pw.print(prefix);
            printWriter.print("  dataDir=");
            printWriter.println(packageSetting.pkg.applicationInfo.dataDir);
            pw.print(prefix);
            printWriter.print("  supportsScreens=[");
            boolean first = true;
            if ((packageSetting.pkg.applicationInfo.flags & 512) != 0) {
                if (1 == 0) {
                    printWriter.print(", ");
                }
                first = false;
                printWriter.print("small");
            }
            if ((packageSetting.pkg.applicationInfo.flags & 1024) != 0) {
                if (!first) {
                    printWriter.print(", ");
                }
                first = false;
                printWriter.print("medium");
            }
            if ((packageSetting.pkg.applicationInfo.flags & 2048) != 0) {
                if (!first) {
                    printWriter.print(", ");
                }
                first = false;
                printWriter.print("large");
            }
            if ((packageSetting.pkg.applicationInfo.flags & DumpState.DUMP_FROZEN) != 0) {
                if (!first) {
                    printWriter.print(", ");
                }
                first = false;
                printWriter.print("xlarge");
            }
            if ((packageSetting.pkg.applicationInfo.flags & 4096) != 0) {
                if (!first) {
                    printWriter.print(", ");
                }
                first = false;
                printWriter.print("resizeable");
            }
            if ((packageSetting.pkg.applicationInfo.flags & 8192) != 0) {
                if (!first) {
                    printWriter.print(", ");
                }
                printWriter.print("anyDensity");
            }
            printWriter.println("]");
            if (packageSetting.pkg.libraryNames != null && packageSetting.pkg.libraryNames.size() > 0) {
                pw.print(prefix);
                printWriter.println("  dynamic libraries:");
                for (int i3 = 0; i3 < packageSetting.pkg.libraryNames.size(); i3++) {
                    pw.print(prefix);
                    printWriter.print("    ");
                    printWriter.println((String) packageSetting.pkg.libraryNames.get(i3));
                }
            }
            if (packageSetting.pkg.staticSharedLibName != null) {
                pw.print(prefix);
                printWriter.println("  static library:");
                pw.print(prefix);
                printWriter.print("    ");
                printWriter.print("name:");
                printWriter.print(packageSetting.pkg.staticSharedLibName);
                printWriter.print(" version:");
                printWriter.println(packageSetting.pkg.staticSharedLibVersion);
            }
            if (packageSetting.pkg.usesLibraries != null && packageSetting.pkg.usesLibraries.size() > 0) {
                pw.print(prefix);
                printWriter.println("  usesLibraries:");
                for (int i4 = 0; i4 < packageSetting.pkg.usesLibraries.size(); i4++) {
                    pw.print(prefix);
                    printWriter.print("    ");
                    printWriter.println((String) packageSetting.pkg.usesLibraries.get(i4));
                }
            }
            if (packageSetting.pkg.usesStaticLibraries != null && packageSetting.pkg.usesStaticLibraries.size() > 0) {
                pw.print(prefix);
                printWriter.println("  usesStaticLibraries:");
                for (int i5 = 0; i5 < packageSetting.pkg.usesStaticLibraries.size(); i5++) {
                    pw.print(prefix);
                    printWriter.print("    ");
                    printWriter.print((String) packageSetting.pkg.usesStaticLibraries.get(i5));
                    printWriter.print(" version:");
                    printWriter.println(packageSetting.pkg.usesStaticLibrariesVersions[i5]);
                }
            }
            if (packageSetting.pkg.usesOptionalLibraries != null && packageSetting.pkg.usesOptionalLibraries.size() > 0) {
                pw.print(prefix);
                printWriter.println("  usesOptionalLibraries:");
                for (int i6 = 0; i6 < packageSetting.pkg.usesOptionalLibraries.size(); i6++) {
                    pw.print(prefix);
                    printWriter.print("    ");
                    printWriter.println((String) packageSetting.pkg.usesOptionalLibraries.get(i6));
                }
            }
            if (packageSetting.pkg.usesLibraryFiles != null && packageSetting.pkg.usesLibraryFiles.length > 0) {
                pw.print(prefix);
                printWriter.println("  usesLibraryFiles:");
                for (String println : packageSetting.pkg.usesLibraryFiles) {
                    pw.print(prefix);
                    printWriter.print("    ");
                    printWriter.println(println);
                }
            }
        }
        pw.print(prefix);
        printWriter.print("  timeStamp=");
        date2.setTime(packageSetting.timeStamp);
        printWriter.println(sdf.format(date));
        pw.print(prefix);
        printWriter.print("  firstInstallTime=");
        date2.setTime(packageSetting.firstInstallTime);
        printWriter.println(sdf.format(date));
        pw.print(prefix);
        printWriter.print("  lastUpdateTime=");
        date2.setTime(packageSetting.lastUpdateTime);
        printWriter.println(sdf.format(date));
        if (packageSetting.installerPackageName != null) {
            pw.print(prefix);
            printWriter.print("  installerPackageName=");
            printWriter.println(packageSetting.installerPackageName);
        }
        if (packageSetting.volumeUuid != null) {
            pw.print(prefix);
            printWriter.print("  volumeUuid=");
            printWriter.println(packageSetting.volumeUuid);
        }
        pw.print(prefix);
        printWriter.print("  signatures=");
        printWriter.println(packageSetting.signatures);
        pw.print(prefix);
        printWriter.print("  installPermissionsFixed=");
        printWriter.print(packageSetting.installPermissionsFixed);
        pw.println();
        pw.print(prefix);
        printWriter.print("  pkgFlags=");
        printFlags(printWriter, packageSetting.pkgFlags, FLAG_DUMP_SPEC);
        pw.println();
        if (!(packageSetting.pkg == null || packageSetting.pkg.mOverlayTarget == null)) {
            pw.print(prefix);
            printWriter.print("  overlayTarget=");
            printWriter.println(packageSetting.pkg.mOverlayTarget);
            pw.print(prefix);
            printWriter.print("  overlayCategory=");
            printWriter.println(packageSetting.pkg.mOverlayCategory);
        }
        if (!(packageSetting.pkg == null || packageSetting.pkg.permissions == null || packageSetting.pkg.permissions.size() <= 0)) {
            ArrayList<PackageParser.Permission> perms = packageSetting.pkg.permissions;
            pw.print(prefix);
            printWriter.println("  declared permissions:");
            for (int i7 = 0; i7 < perms.size(); i7++) {
                PackageParser.Permission perm = perms.get(i7);
                if (arraySet == null || arraySet.contains(perm.info.name)) {
                    pw.print(prefix);
                    printWriter.print("    ");
                    printWriter.print(perm.info.name);
                    printWriter.print(": prot=");
                    printWriter.print(PermissionInfo.protectionToString(perm.info.protectionLevel));
                    if ((perm.info.flags & 1) != 0) {
                        printWriter.print(", COSTS_MONEY");
                    }
                    if ((perm.info.flags & 2) != 0) {
                        printWriter.print(", HIDDEN");
                    }
                    if ((perm.info.flags & 1073741824) != 0) {
                        printWriter.print(", INSTALLED");
                    }
                    pw.println();
                }
            }
        }
        if ((arraySet != null || dumpAll) && packageSetting.pkg != null && packageSetting.pkg.requestedPermissions != null && packageSetting.pkg.requestedPermissions.size() > 0) {
            ArrayList<String> perms2 = packageSetting.pkg.requestedPermissions;
            pw.print(prefix);
            printWriter.println("  requested permissions:");
            for (int i8 = 0; i8 < perms2.size(); i8++) {
                String perm2 = perms2.get(i8);
                if (arraySet == null || arraySet.contains(perm2)) {
                    pw.print(prefix);
                    printWriter.print("    ");
                    printWriter.print(perm2);
                    BasePermission bp = this.mPermissions.getPermission(perm2);
                    if (bp == null || !bp.isHardOrSoftRestricted()) {
                        pw.println();
                    } else {
                        printWriter.println(": restricted=true");
                    }
                }
            }
        }
        if (packageSetting.sharedUser == null || arraySet != null || dumpAll) {
            dumpInstallPermissionsLPr(printWriter, str2 + "  ", arraySet, ps.getPermissionsState());
        }
        if (dumpAllComponents) {
            dumpComponents(printWriter, str2 + "  ", packageSetting);
        }
        for (UserInfo user2 : users) {
            pw.print(prefix);
            printWriter.print("  User ");
            printWriter.print(user2.id);
            printWriter.print(": ");
            printWriter.print("ceDataInode=");
            printWriter.print(packageSetting.getCeDataInode(user2.id));
            printWriter.print(" installed=");
            printWriter.print(packageSetting.getInstalled(user2.id));
            printWriter.print(" hidden=");
            printWriter.print(packageSetting.getHidden(user2.id));
            printWriter.print(" suspended=");
            printWriter.print(packageSetting.getSuspended(user2.id));
            if (packageSetting.getSuspended(user2.id)) {
                PackageUserState pus = packageSetting.readUserState(user2.id);
                printWriter.print(" suspendingPackage=");
                printWriter.print(pus.suspendingPackage);
                printWriter.print(" dialogInfo=");
                printWriter.print(pus.dialogInfo);
            }
            printWriter.print(" stopped=");
            printWriter.print(packageSetting.getStopped(user2.id));
            printWriter.print(" notLaunched=");
            printWriter.print(packageSetting.getNotLaunched(user2.id));
            printWriter.print(" enabled=");
            printWriter.print(packageSetting.getEnabled(user2.id));
            printWriter.print(" instant=");
            printWriter.print(packageSetting.getInstantApp(user2.id));
            printWriter.print(" virtual=");
            printWriter.println(packageSetting.getVirtulalPreload(user2.id));
            String[] overlayPaths = packageSetting.getOverlayPaths(user2.id);
            if (overlayPaths != null && overlayPaths.length > 0) {
                pw.print(prefix);
                printWriter.println("  overlay paths:");
                for (String path : overlayPaths) {
                    pw.print(prefix);
                    printWriter.print("    ");
                    printWriter.println(path);
                }
            }
            String lastDisabledAppCaller2 = packageSetting.getLastDisabledAppCaller(user2.id);
            if (lastDisabledAppCaller2 != null) {
                pw.print(prefix);
                printWriter.print("    lastDisabledCaller: ");
                printWriter.println(lastDisabledAppCaller2);
            }
            if (packageSetting.sharedUser == null) {
                PermissionsState permissionsState = ps.getPermissionsState();
                dumpGidsLPr(printWriter, str2 + "    ", permissionsState.computeGids(user2.id));
                PermissionsState permissionsState2 = permissionsState;
                String str4 = lastDisabledAppCaller2;
                String[] strArr = overlayPaths;
                dumpRuntimePermissionsLPr(pw, str2 + "    ", permissionNames, permissionsState.getRuntimePermissionStates(user2.id), dumpAll);
            } else {
                String[] strArr2 = overlayPaths;
            }
            String harmfulAppWarning = packageSetting.getHarmfulAppWarning(user2.id);
            if (harmfulAppWarning != null) {
                pw.print(prefix);
                printWriter.print("      harmfulAppWarning: ");
                printWriter.println(harmfulAppWarning);
            }
            if (arraySet == null) {
                ArraySet<String> cmp = packageSetting.getDisabledComponents(user2.id);
                if (cmp != null && cmp.size() > 0) {
                    pw.print(prefix);
                    printWriter.println("    disabledComponents:");
                    Iterator<String> it = cmp.iterator();
                    while (it.hasNext()) {
                        pw.print(prefix);
                        printWriter.print("      ");
                        printWriter.println(it.next());
                    }
                }
                ArraySet<String> cmp2 = packageSetting.getEnabledComponents(user2.id);
                if (cmp2 != null && cmp2.size() > 0) {
                    pw.print(prefix);
                    printWriter.println("    enabledComponents:");
                    Iterator<String> it2 = cmp2.iterator();
                    while (it2.hasNext()) {
                        pw.print(prefix);
                        printWriter.print("      ");
                        printWriter.println(it2.next());
                    }
                }
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void dumpPackagesLPr(PrintWriter pw, String packageName, ArraySet<String> permissionNames, DumpState dumpState, boolean checkin) {
        boolean printedSomething;
        PrintWriter printWriter = pw;
        String str = packageName;
        ArraySet<String> arraySet = permissionNames;
        DumpState dumpState2 = dumpState;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        boolean printedSomething2 = false;
        boolean dumpAllComponents = dumpState2.isOptionEnabled(2);
        List<UserInfo> users = getAllUsers(UserManagerService.getInstance());
        Iterator<PackageSetting> it = this.mPackages.values().iterator();
        while (true) {
            String str2 = null;
            if (!it.hasNext()) {
                break;
            }
            PackageSetting ps = it.next();
            if ((str == null || str.equals(ps.realName) || str.equals(ps.name)) && (arraySet == null || ps.getPermissionsState().hasRequestedPermission(arraySet))) {
                if (!checkin && str != null) {
                    dumpState2.setSharedUser(ps.sharedUser);
                }
                if (checkin || printedSomething2) {
                    printedSomething = printedSomething2;
                } else {
                    if (dumpState.onTitlePrinted()) {
                        pw.println();
                    }
                    printWriter.println("Packages:");
                    printedSomething = true;
                }
                if (checkin) {
                    str2 = "pkg";
                }
                PackageSetting packageSetting = ps;
                dumpPackageLPr(pw, "  ", str2, permissionNames, ps, sdf, date, users, str != null, dumpAllComponents);
                printedSomething2 = printedSomething;
            }
        }
        boolean printedSomething3 = false;
        if (this.mRenamedPackages.size() > 0 && arraySet == null) {
            for (Map.Entry<String, String> e : this.mRenamedPackages.entrySet()) {
                if (str == null || str.equals(e.getKey()) || str.equals(e.getValue())) {
                    if (!checkin) {
                        if (!printedSomething3) {
                            if (dumpState.onTitlePrinted()) {
                                pw.println();
                            }
                            printWriter.println("Renamed packages:");
                            printedSomething3 = true;
                        }
                        printWriter.print("  ");
                    } else {
                        printWriter.print("ren,");
                    }
                    printWriter.print(e.getKey());
                    printWriter.print(checkin ? " -> " : ",");
                    printWriter.println(e.getValue());
                }
            }
        }
        boolean printedSomething4 = false;
        if (this.mDisabledSysPackages.size() > 0 && arraySet == null) {
            for (PackageSetting ps2 : this.mDisabledSysPackages.values()) {
                if (str == null || str.equals(ps2.realName) || str.equals(ps2.name)) {
                    if (!checkin && !printedSomething4) {
                        if (dumpState.onTitlePrinted()) {
                            pw.println();
                        }
                        printWriter.println("Hidden system packages:");
                        printedSomething4 = true;
                    }
                    dumpPackageLPr(pw, "  ", checkin ? "dis" : null, permissionNames, ps2, sdf, date, users, str != null, dumpAllComponents);
                    printWriter = pw;
                    str = packageName;
                    DumpState dumpState3 = dumpState;
                }
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void dumpPackagesProto(ProtoOutputStream proto) {
        List<UserInfo> users = getAllUsers(UserManagerService.getInstance());
        int count = this.mPackages.size();
        for (int i = 0; i < count; i++) {
            this.mPackages.valueAt(i).writeToProto(proto, 2246267895813L, users);
        }
    }

    /* access modifiers changed from: package-private */
    public void dumpPermissionsLPr(PrintWriter pw, String packageName, ArraySet<String> permissionNames, DumpState dumpState) {
        this.mPermissions.dumpPermissions(pw, packageName, permissionNames, this.mReadExternalStorageEnforced == Boolean.TRUE, dumpState);
    }

    /* access modifiers changed from: package-private */
    public void dumpSharedUsersLPr(PrintWriter pw, String packageName, ArraySet<String> permissionNames, DumpState dumpState, boolean checkin) {
        boolean printedSomething;
        PermissionsState permissionsState;
        int[] iArr;
        int i;
        int i2;
        PrintWriter printWriter = pw;
        ArraySet<String> arraySet = permissionNames;
        boolean printedSomething2 = false;
        for (SharedUserSetting su : this.mSharedUsers.values()) {
            if ((packageName == null || su == dumpState.getSharedUser()) && (arraySet == null || su.getPermissionsState().hasRequestedPermission(arraySet))) {
                if (!checkin) {
                    if (!printedSomething2) {
                        if (dumpState.onTitlePrinted()) {
                            pw.println();
                        }
                        printWriter.println("Shared users:");
                        printedSomething = true;
                    } else {
                        printedSomething = printedSomething2;
                    }
                    printWriter.print("  SharedUser [");
                    printWriter.print(su.name);
                    printWriter.print("] (");
                    printWriter.print(Integer.toHexString(System.identityHashCode(su)));
                    printWriter.println("):");
                    printWriter.print("    ");
                    printWriter.print("userId=");
                    printWriter.println(su.userId);
                    printWriter.print("    ");
                    printWriter.println("Packages");
                    int numPackages = su.packages.size();
                    for (int i3 = 0; i3 < numPackages; i3++) {
                        PackageSetting ps = su.packages.valueAt(i3);
                        if (ps != null) {
                            printWriter.print("    " + "  ");
                            printWriter.println(ps.toString());
                        } else {
                            printWriter.print("    " + "  ");
                            printWriter.println("NULL?!");
                        }
                    }
                    if (dumpState.isOptionEnabled(4)) {
                        printedSomething2 = printedSomething;
                    } else {
                        PermissionsState permissionsState2 = su.getPermissionsState();
                        dumpInstallPermissionsLPr(printWriter, "    ", arraySet, permissionsState2);
                        int[] userIds = UserManagerService.getInstance().getUserIds();
                        int length = userIds.length;
                        int i4 = 0;
                        while (i4 < length) {
                            int userId = userIds[i4];
                            int[] gids = permissionsState2.computeGids(userId);
                            List<PermissionsState.PermissionState> permissions = permissionsState2.getRuntimePermissionStates(userId);
                            if (!ArrayUtils.isEmpty(gids) || !permissions.isEmpty()) {
                                printWriter.print("    ");
                                i2 = i4;
                                printWriter.print("User ");
                                printWriter.print(userId);
                                printWriter.println(": ");
                                dumpGidsLPr(printWriter, "    " + "  ", gids);
                                int[] iArr2 = gids;
                                int i5 = userId;
                                i = length;
                                iArr = userIds;
                                permissionsState = permissionsState2;
                                dumpRuntimePermissionsLPr(pw, "    " + "  ", permissionNames, permissions, packageName != null);
                            } else {
                                i2 = i4;
                                i = length;
                                iArr = userIds;
                                permissionsState = permissionsState2;
                            }
                            i4 = i2 + 1;
                            length = i;
                            userIds = iArr;
                            permissionsState2 = permissionsState;
                        }
                        printedSomething2 = printedSomething;
                    }
                } else {
                    DumpState dumpState2 = dumpState;
                    printWriter.print("suid,");
                    printWriter.print(su.userId);
                    printWriter.print(",");
                    printWriter.println(su.name);
                }
            }
        }
        DumpState dumpState3 = dumpState;
    }

    /* access modifiers changed from: package-private */
    public void dumpSharedUsersProto(ProtoOutputStream proto) {
        int count = this.mSharedUsers.size();
        for (int i = 0; i < count; i++) {
            this.mSharedUsers.valueAt(i).writeToProto(proto, 2246267895814L);
        }
    }

    /* access modifiers changed from: package-private */
    public void dumpReadMessagesLPr(PrintWriter pw, DumpState dumpState) {
        pw.println("Settings parse messages:");
        pw.print(this.mReadMessages.toString());
    }

    private static void dumpSplitNames(PrintWriter pw, PackageParser.Package pkg) {
        if (pkg == null) {
            pw.print(UiModeManagerService.Shell.NIGHT_MODE_STR_UNKNOWN);
            return;
        }
        pw.print("[");
        pw.print("base");
        if (pkg.baseRevisionCode != 0) {
            pw.print(":");
            pw.print(pkg.baseRevisionCode);
        }
        if (pkg.splitNames != null) {
            for (int i = 0; i < pkg.splitNames.length; i++) {
                pw.print(", ");
                pw.print(pkg.splitNames[i]);
                if (pkg.splitRevisionCodes[i] != 0) {
                    pw.print(":");
                    pw.print(pkg.splitRevisionCodes[i]);
                }
            }
        }
        pw.print("]");
    }

    /* access modifiers changed from: package-private */
    public void dumpGidsLPr(PrintWriter pw, String prefix, int[] gids) {
        if (!ArrayUtils.isEmpty(gids)) {
            pw.print(prefix);
            pw.print("gids=");
            pw.println(PackageManagerService.arrayToString(gids));
        }
    }

    /* access modifiers changed from: package-private */
    public void dumpRuntimePermissionsLPr(PrintWriter pw, String prefix, ArraySet<String> permissionNames, List<PermissionsState.PermissionState> permissionStates, boolean dumpAll) {
        if (!permissionStates.isEmpty() || dumpAll) {
            pw.print(prefix);
            pw.println("runtime permissions:");
            for (PermissionsState.PermissionState permissionState : permissionStates) {
                if (permissionNames == null || permissionNames.contains(permissionState.getName())) {
                    pw.print(prefix);
                    pw.print("  ");
                    pw.print(permissionState.getName());
                    pw.print(": granted=");
                    pw.print(permissionState.isGranted());
                    pw.println(permissionFlagsToString(", flags=", permissionState.getFlags()));
                }
            }
        }
    }

    private static String permissionFlagsToString(String prefix, int flags) {
        StringBuilder flagsString = null;
        while (flags != 0) {
            if (flagsString == null) {
                flagsString = new StringBuilder();
                flagsString.append(prefix);
                flagsString.append("[ ");
            }
            int flag = 1 << Integer.numberOfTrailingZeros(flags);
            flags &= ~flag;
            flagsString.append(PackageManager.permissionFlagToString(flag));
            if (flags != 0) {
                flagsString.append('|');
            }
        }
        if (flagsString == null) {
            return "";
        }
        flagsString.append(']');
        return flagsString.toString();
    }

    /* access modifiers changed from: package-private */
    public void dumpInstallPermissionsLPr(PrintWriter pw, String prefix, ArraySet<String> permissionNames, PermissionsState permissionsState) {
        List<PermissionsState.PermissionState> permissionStates = permissionsState.getInstallPermissionStates();
        if (!permissionStates.isEmpty()) {
            pw.print(prefix);
            pw.println("install permissions:");
            for (PermissionsState.PermissionState permissionState : permissionStates) {
                if (permissionNames == null || permissionNames.contains(permissionState.getName())) {
                    pw.print(prefix);
                    pw.print("  ");
                    pw.print(permissionState.getName());
                    pw.print(": granted=");
                    pw.print(permissionState.isGranted());
                    pw.println(permissionFlagsToString(", flags=", permissionState.getFlags()));
                }
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void dumpComponents(PrintWriter pw, String prefix, PackageSetting ps) {
        PrintWriter printWriter = pw;
        String str = prefix;
        PackageSetting packageSetting = ps;
        dumpComponents(printWriter, str, packageSetting, "activities:", ps.pkg.activities);
        dumpComponents(printWriter, str, packageSetting, "services:", ps.pkg.services);
        dumpComponents(printWriter, str, packageSetting, "receivers:", ps.pkg.receivers);
        dumpComponents(printWriter, str, packageSetting, "providers:", ps.pkg.providers);
        dumpComponents(printWriter, str, packageSetting, "instrumentations:", ps.pkg.instrumentation);
    }

    /* access modifiers changed from: package-private */
    public void dumpComponents(PrintWriter pw, String prefix, PackageSetting ps, String label, List<? extends PackageParser.Component<?>> list) {
        int size = CollectionUtils.size(list);
        if (size != 0) {
            pw.print(prefix);
            pw.println(label);
            for (int i = 0; i < size; i++) {
                pw.print(prefix);
                pw.print("  ");
                pw.println(((PackageParser.Component) list.get(i)).getComponentName().flattenToShortString());
            }
        }
    }

    public void writeRuntimePermissionsForUserLPr(int userId, boolean sync) {
        if (sync) {
            this.mRuntimePermissionsPersistence.writePermissionsForUserSyncLPr(userId);
        } else {
            this.mRuntimePermissionsPersistence.writePermissionsForUserAsyncLPr(userId);
        }
    }

    private final class RuntimePermissionPersistence {
        private static final int INITIAL_VERSION = 0;
        private static final long MAX_WRITE_PERMISSIONS_DELAY_MILLIS = 2000;
        private static final int UPGRADE_VERSION = -1;
        private static final long WRITE_PERMISSIONS_DELAY_MILLIS = 200;
        @GuardedBy({"mLock"})
        private final SparseBooleanArray mDefaultPermissionsGranted = new SparseBooleanArray();
        @GuardedBy({"mLock"})
        private final SparseArray<String> mFingerprints = new SparseArray<>();
        private final Handler mHandler = new MyHandler();
        @GuardedBy({"mLock"})
        private final SparseLongArray mLastNotWrittenMutationTimesMillis = new SparseLongArray();
        private final Object mPersistenceLock;
        @GuardedBy({"mLock"})
        private final SparseIntArray mVersions = new SparseIntArray();
        @GuardedBy({"mLock"})
        private final SparseBooleanArray mWriteScheduled = new SparseBooleanArray();

        public RuntimePermissionPersistence(Object persistenceLock) {
            this.mPersistenceLock = persistenceLock;
        }

        /* access modifiers changed from: package-private */
        @GuardedBy({"Settings.this.mLock"})
        public int getVersionLPr(int userId) {
            return this.mVersions.get(userId, 0);
        }

        /* access modifiers changed from: package-private */
        @GuardedBy({"Settings.this.mLock"})
        public void setVersionLPr(int version, int userId) {
            this.mVersions.put(userId, version);
            writePermissionsForUserAsyncLPr(userId);
        }

        @GuardedBy({"Settings.this.mLock"})
        public boolean areDefaultRuntimePermissionsGrantedLPr(int userId) {
            return this.mDefaultPermissionsGranted.get(userId);
        }

        @GuardedBy({"Settings.this.mLock"})
        public void setRuntimePermissionsFingerPrintLPr(String fingerPrint, int userId) {
            this.mFingerprints.put(userId, fingerPrint);
            writePermissionsForUserAsyncLPr(userId);
        }

        public void writePermissionsForUserSyncLPr(int userId) {
            this.mHandler.removeMessages(userId);
            writePermissionsSync(userId);
        }

        @GuardedBy({"Settings.this.mLock"})
        public void writePermissionsForUserAsyncLPr(int userId) {
            long currentTimeMillis = SystemClock.uptimeMillis();
            if (this.mWriteScheduled.get(userId)) {
                this.mHandler.removeMessages(userId);
                long lastNotWrittenMutationTimeMillis = this.mLastNotWrittenMutationTimesMillis.get(userId);
                if (currentTimeMillis - lastNotWrittenMutationTimeMillis >= MAX_WRITE_PERMISSIONS_DELAY_MILLIS) {
                    this.mHandler.obtainMessage(userId).sendToTarget();
                    return;
                }
                long writeDelayMillis = Math.min(WRITE_PERMISSIONS_DELAY_MILLIS, Math.max((MAX_WRITE_PERMISSIONS_DELAY_MILLIS + lastNotWrittenMutationTimeMillis) - currentTimeMillis, 0));
                this.mHandler.sendMessageDelayed(this.mHandler.obtainMessage(userId), writeDelayMillis);
                return;
            }
            this.mLastNotWrittenMutationTimesMillis.put(userId, currentTimeMillis);
            this.mHandler.sendMessageDelayed(this.mHandler.obtainMessage(userId), WRITE_PERMISSIONS_DELAY_MILLIS);
            this.mWriteScheduled.put(userId, true);
        }

        /* access modifiers changed from: private */
        public void writePermissionsSync(int userId) {
            int i = userId;
            File access$200 = Settings.this.getUserRuntimePermissionsFile(i);
            AtomicFile destination = new AtomicFile(access$200, "package-perms-" + i);
            ArrayMap arrayMap = new ArrayMap();
            ArrayMap arrayMap2 = new ArrayMap();
            synchronized (this.mPersistenceLock) {
                this.mWriteScheduled.delete(i);
                int packageCount = Settings.this.mPackages.size();
                for (int i2 = 0; i2 < packageCount; i2++) {
                    String packageName = Settings.this.mPackages.keyAt(i2);
                    PackageSetting packageSetting = Settings.this.mPackages.valueAt(i2);
                    if (packageSetting.sharedUser == null) {
                        List<PermissionsState.PermissionState> permissionsStates = packageSetting.getPermissionsState().getRuntimePermissionStates(i);
                        if (!permissionsStates.isEmpty()) {
                            arrayMap.put(packageName, permissionsStates);
                        }
                    }
                }
                int sharedUserCount = Settings.this.mSharedUsers.size();
                for (int i3 = 0; i3 < sharedUserCount; i3++) {
                    String sharedUserName = Settings.this.mSharedUsers.keyAt(i3);
                    List<PermissionsState.PermissionState> permissionsStates2 = Settings.this.mSharedUsers.valueAt(i3).getPermissionsState().getRuntimePermissionStates(i);
                    if (!permissionsStates2.isEmpty()) {
                        arrayMap2.put(sharedUserName, permissionsStates2);
                    }
                }
            }
            FileOutputStream out = null;
            try {
                out = destination.startWrite();
                XmlSerializer serializer = Xml.newSerializer();
                serializer.setOutput(out, StandardCharsets.UTF_8.name());
                serializer.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);
                serializer.startDocument((String) null, true);
                serializer.startTag((String) null, Settings.TAG_RUNTIME_PERMISSIONS);
                serializer.attribute((String) null, "version", Integer.toString(this.mVersions.get(i, 0)));
                String fingerprint = this.mFingerprints.get(i);
                if (fingerprint != null) {
                    serializer.attribute((String) null, Settings.ATTR_FINGERPRINT, fingerprint);
                }
                int packageCount2 = arrayMap.size();
                for (int i4 = 0; i4 < packageCount2; i4++) {
                    serializer.startTag((String) null, "pkg");
                    serializer.attribute((String) null, Settings.ATTR_NAME, (String) arrayMap.keyAt(i4));
                    writePermissions(serializer, (List) arrayMap.valueAt(i4));
                    serializer.endTag((String) null, "pkg");
                }
                int i5 = arrayMap2.size();
                for (int i6 = 0; i6 < i5; i6++) {
                    serializer.startTag((String) null, Settings.TAG_SHARED_USER);
                    serializer.attribute((String) null, Settings.ATTR_NAME, (String) arrayMap2.keyAt(i6));
                    writePermissions(serializer, (List) arrayMap2.valueAt(i6));
                    serializer.endTag((String) null, Settings.TAG_SHARED_USER);
                }
                serializer.endTag((String) null, Settings.TAG_RUNTIME_PERMISSIONS);
                serializer.endDocument();
                destination.finishWrite(out);
                if (Build.FINGERPRINT.equals(fingerprint)) {
                    this.mDefaultPermissionsGranted.put(i, true);
                }
            } catch (Throwable th) {
                IoUtils.closeQuietly((AutoCloseable) null);
                throw th;
            }
            IoUtils.closeQuietly(out);
        }

        /* access modifiers changed from: private */
        @GuardedBy({"Settings.this.mLock"})
        public void onUserRemovedLPw(int userId) {
            this.mHandler.removeMessages(userId);
            for (PackageSetting sb : Settings.this.mPackages.values()) {
                revokeRuntimePermissionsAndClearFlags(sb, userId);
            }
            for (SharedUserSetting sb2 : Settings.this.mSharedUsers.values()) {
                revokeRuntimePermissionsAndClearFlags(sb2, userId);
            }
            this.mDefaultPermissionsGranted.delete(userId);
            this.mVersions.delete(userId);
            this.mFingerprints.remove(userId);
        }

        private void revokeRuntimePermissionsAndClearFlags(SettingBase sb, int userId) {
            PermissionsState permissionsState = sb.getPermissionsState();
            for (PermissionsState.PermissionState permissionState : permissionsState.getRuntimePermissionStates(userId)) {
                BasePermission bp = Settings.this.mPermissions.getPermission(permissionState.getName());
                if (bp != null) {
                    permissionsState.revokeRuntimePermission(bp, userId);
                    permissionsState.updatePermissionFlags(bp, userId, 64511, 0);
                }
            }
        }

        public void deleteUserRuntimePermissionsFile(int userId) {
            Settings.this.getUserRuntimePermissionsFile(userId).delete();
        }

        /* Debug info: failed to restart local var, previous not found, register: 7 */
        @GuardedBy({"Settings.this.mLock"})
        public void readStateForUserSyncLPr(int userId) {
            File permissionsFile = Settings.this.getUserRuntimePermissionsFile(userId);
            if (permissionsFile.exists()) {
                AtomicFile destination = new AtomicFile(permissionsFile);
                try {
                    FileInputStream in = destination.openRead();
                    try {
                        XmlPullParser parser = Xml.newPullParser();
                        parser.setInput(in, (String) null);
                        parseRuntimePermissionsLPr(parser, userId);
                        IoUtils.closeQuietly(in);
                    } catch (IOException | XmlPullParserException e) {
                        destination.delete();
                        throw new IllegalStateException("Failed parsing permissions file: " + permissionsFile, e);
                    } catch (Throwable th) {
                        IoUtils.closeQuietly(in);
                        throw th;
                    }
                } catch (FileNotFoundException e2) {
                    Slog.i("PackageManager", "No permissions state");
                }
            }
        }

        /* JADX WARNING: Removed duplicated region for block: B:28:0x005a  */
        /* JADX WARNING: Removed duplicated region for block: B:39:0x00c7  */
        @com.android.internal.annotations.GuardedBy({"Settings.this.mLock"})
        /* Code decompiled incorrectly, please refer to instructions dump. */
        private void parseRuntimePermissionsLPr(org.xmlpull.v1.XmlPullParser r9, int r10) throws java.io.IOException, org.xmlpull.v1.XmlPullParserException {
            /*
                r8 = this;
                int r0 = r9.getDepth()
            L_0x0004:
                int r1 = r9.next()
                r2 = r1
                r3 = 1
                if (r1 == r3) goto L_0x00ec
                r1 = 3
                if (r2 != r1) goto L_0x0015
                int r4 = r9.getDepth()
                if (r4 <= r0) goto L_0x00ec
            L_0x0015:
                if (r2 == r1) goto L_0x0004
                r1 = 4
                if (r2 != r1) goto L_0x001b
                goto L_0x0004
            L_0x001b:
                java.lang.String r1 = r9.getName()
                int r4 = r1.hashCode()
                r5 = 111052(0x1b1cc, float:1.55617E-40)
                r6 = 2
                r7 = -1
                if (r4 == r5) goto L_0x004b
                r5 = 160289295(0x98dd20f, float:3.4142053E-33)
                if (r4 == r5) goto L_0x0040
                r5 = 485578803(0x1cf15833, float:1.5970841E-21)
                if (r4 == r5) goto L_0x0035
            L_0x0034:
                goto L_0x0056
            L_0x0035:
                java.lang.String r4 = "shared-user"
                boolean r1 = r1.equals(r4)
                if (r1 == 0) goto L_0x0034
                r1 = r6
                goto L_0x0057
            L_0x0040:
                java.lang.String r4 = "runtime-permissions"
                boolean r1 = r1.equals(r4)
                if (r1 == 0) goto L_0x0034
                r1 = 0
                goto L_0x0057
            L_0x004b:
                java.lang.String r4 = "pkg"
                boolean r1 = r1.equals(r4)
                if (r1 == 0) goto L_0x0034
                r1 = r3
                goto L_0x0057
            L_0x0056:
                r1 = r7
            L_0x0057:
                r4 = 0
                if (r1 == 0) goto L_0x00c7
                java.lang.String r5 = "PackageManager"
                java.lang.String r7 = "name"
                if (r1 == r3) goto L_0x0096
                if (r1 == r6) goto L_0x0065
                goto L_0x00ea
            L_0x0065:
                java.lang.String r1 = r9.getAttributeValue(r4, r7)
                com.android.server.pm.Settings r3 = com.android.server.pm.Settings.this
                android.util.ArrayMap<java.lang.String, com.android.server.pm.SharedUserSetting> r3 = r3.mSharedUsers
                java.lang.Object r3 = r3.get(r1)
                com.android.server.pm.SharedUserSetting r3 = (com.android.server.pm.SharedUserSetting) r3
                if (r3 != 0) goto L_0x008e
                java.lang.StringBuilder r4 = new java.lang.StringBuilder
                r4.<init>()
                java.lang.String r6 = "Unknown shared user:"
                r4.append(r6)
                r4.append(r1)
                java.lang.String r4 = r4.toString()
                android.util.Slog.w(r5, r4)
                com.android.internal.util.XmlUtils.skipCurrentTag(r9)
                goto L_0x0004
            L_0x008e:
                com.android.server.pm.permission.PermissionsState r4 = r3.getPermissionsState()
                r8.parsePermissionsLPr(r9, r4, r10)
                goto L_0x00ea
            L_0x0096:
                java.lang.String r1 = r9.getAttributeValue(r4, r7)
                com.android.server.pm.Settings r3 = com.android.server.pm.Settings.this
                android.util.ArrayMap<java.lang.String, com.android.server.pm.PackageSetting> r3 = r3.mPackages
                java.lang.Object r3 = r3.get(r1)
                com.android.server.pm.PackageSetting r3 = (com.android.server.pm.PackageSetting) r3
                if (r3 != 0) goto L_0x00bf
                java.lang.StringBuilder r4 = new java.lang.StringBuilder
                r4.<init>()
                java.lang.String r6 = "Unknown package:"
                r4.append(r6)
                r4.append(r1)
                java.lang.String r4 = r4.toString()
                android.util.Slog.w(r5, r4)
                com.android.internal.util.XmlUtils.skipCurrentTag(r9)
                goto L_0x0004
            L_0x00bf:
                com.android.server.pm.permission.PermissionsState r4 = r3.getPermissionsState()
                r8.parsePermissionsLPr(r9, r4, r10)
                goto L_0x00ea
            L_0x00c7:
                java.lang.String r1 = "version"
                int r1 = com.android.internal.util.XmlUtils.readIntAttribute(r9, r1, r7)
                android.util.SparseIntArray r3 = r8.mVersions
                r3.put(r10, r1)
                java.lang.String r3 = "fingerprint"
                java.lang.String r3 = r9.getAttributeValue(r4, r3)
                android.util.SparseArray<java.lang.String> r4 = r8.mFingerprints
                r4.put(r10, r3)
                java.lang.String r4 = android.os.Build.FINGERPRINT
                boolean r4 = r4.equals(r3)
                android.util.SparseBooleanArray r5 = r8.mDefaultPermissionsGranted
                r5.put(r10, r4)
            L_0x00ea:
                goto L_0x0004
            L_0x00ec:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.server.pm.Settings.RuntimePermissionPersistence.parseRuntimePermissionsLPr(org.xmlpull.v1.XmlPullParser, int):void");
        }

        private void parsePermissionsLPr(XmlPullParser parser, PermissionsState permissionsState, int userId) throws IOException, XmlPullParserException {
            int outerDepth = parser.getDepth();
            while (true) {
                int next = parser.next();
                int type = next;
                boolean granted = true;
                if (next == 1) {
                    return;
                }
                if (type == 3 && parser.getDepth() <= outerDepth) {
                    return;
                }
                if (!(type == 3 || type == 4)) {
                    String name = parser.getName();
                    char c = 65535;
                    int flags = 0;
                    if (name.hashCode() == 3242771 && name.equals(Settings.TAG_ITEM)) {
                        c = 0;
                    }
                    if (c == 0) {
                        String name2 = parser.getAttributeValue((String) null, Settings.ATTR_NAME);
                        BasePermission bp = Settings.this.mPermissions.getPermission(name2);
                        if (bp == null) {
                            Slog.w("PackageManager", "Unknown permission:" + name2);
                            XmlUtils.skipCurrentTag(parser);
                        } else {
                            String grantedStr = parser.getAttributeValue((String) null, Settings.ATTR_GRANTED);
                            if (grantedStr != null && !Boolean.parseBoolean(grantedStr)) {
                                granted = false;
                            }
                            String flagsStr = parser.getAttributeValue((String) null, Settings.ATTR_FLAGS);
                            if (flagsStr != null) {
                                flags = Integer.parseInt(flagsStr, 16);
                            }
                            if (granted) {
                                permissionsState.grantRuntimePermission(bp, userId);
                                permissionsState.updatePermissionFlags(bp, userId, 64511, flags);
                            } else {
                                permissionsState.updatePermissionFlags(bp, userId, 64511, flags);
                            }
                        }
                    }
                }
            }
        }

        private void writePermissions(XmlSerializer serializer, List<PermissionsState.PermissionState> permissionStates) throws IOException {
            for (PermissionsState.PermissionState permissionState : permissionStates) {
                serializer.startTag((String) null, Settings.TAG_ITEM);
                serializer.attribute((String) null, Settings.ATTR_NAME, permissionState.getName());
                serializer.attribute((String) null, Settings.ATTR_GRANTED, String.valueOf(permissionState.isGranted()));
                serializer.attribute((String) null, Settings.ATTR_FLAGS, Integer.toHexString(permissionState.getFlags()));
                serializer.endTag((String) null, Settings.TAG_ITEM);
            }
        }

        private final class MyHandler extends Handler {
            public MyHandler() {
                super(BackgroundThread.getHandler().getLooper());
            }

            public void handleMessage(Message message) {
                int userId = message.what;
                Runnable callback = (Runnable) message.obj;
                RuntimePermissionPersistence.this.writePermissionsSync(userId);
                if (callback != null) {
                    callback.run();
                }
            }
        }
    }
}
