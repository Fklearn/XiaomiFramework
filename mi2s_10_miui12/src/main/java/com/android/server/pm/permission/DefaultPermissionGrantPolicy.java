package com.android.server.pm.permission;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManagerInternal;
import android.content.pm.ProviderInfo;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.UserHandle;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.ArraySet;
import android.util.Log;
import android.util.Slog;
import android.util.SparseIntArray;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.util.ArrayUtils;
import com.android.internal.util.XmlUtils;
import com.android.server.LocalServices;
import com.android.server.backup.UserBackupManagerService;
import com.android.server.pm.DumpState;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public final class DefaultPermissionGrantPolicy {
    private static final String ACTION_TRACK = "com.android.fitness.TRACK";
    private static final Set<String> ACTIVITY_RECOGNITION_PERMISSIONS = new ArraySet();
    private static final Set<String> ALWAYS_LOCATION_PERMISSIONS = new ArraySet();
    private static final String ATTR_FIXED = "fixed";
    private static final String ATTR_NAME = "name";
    private static final String ATTR_PACKAGE = "package";
    private static final String ATTR_WHITELISTED = "whitelisted";
    private static final String AUDIO_MIME_TYPE = "audio/mpeg";
    private static final Set<String> CALENDAR_PERMISSIONS = new ArraySet();
    private static final Set<String> CAMERA_PERMISSIONS = new ArraySet();
    private static final Set<String> CONTACTS_PERMISSIONS = new ArraySet();
    private static final boolean DEBUG = false;
    private static final int DEFAULT_INTENT_QUERY_FLAGS = 794624;
    private static final int DEFAULT_PACKAGE_INFO_QUERY_FLAGS = 536915968;
    private static final Set<String> MICROPHONE_PERMISSIONS = new ArraySet();
    private static final int MSG_READ_DEFAULT_PERMISSION_EXCEPTIONS = 1;
    private static final Set<String> PHONE_PERMISSIONS = new ArraySet();
    private static final Set<String> SENSORS_PERMISSIONS = new ArraySet();
    private static final Set<String> SMS_PERMISSIONS = new ArraySet();
    private static final Set<String> STORAGE_PERMISSIONS = new ArraySet();
    private static final String TAG = "DefaultPermGrantPolicy";
    private static final String TAG_EXCEPTION = "exception";
    private static final String TAG_EXCEPTIONS = "exceptions";
    private static final String TAG_PERMISSION = "permission";
    private final Context mContext;
    @GuardedBy({"mLock"})
    private SparseIntArray mDefaultPermissionsGrantedUsers = new SparseIntArray();
    private PackageManagerInternal.PackagesProvider mDialerAppPackagesProvider;
    /* access modifiers changed from: private */
    public ArrayMap<String, List<DefaultPermissionGrant>> mGrantExceptions;
    private final Handler mHandler;
    private PackageManagerInternal.PackagesProvider mLocationExtraPackagesProvider;
    private PackageManagerInternal.PackagesProvider mLocationPackagesProvider;
    /* access modifiers changed from: private */
    public final Object mLock = new Object();
    private final PermissionManagerService mPermissionManager;
    private final PackageManagerInternal mServiceInternal;
    private PackageManagerInternal.PackagesProvider mSimCallManagerPackagesProvider;
    private PackageManagerInternal.PackagesProvider mSmsAppPackagesProvider;
    private PackageManagerInternal.SyncAdapterPackagesProvider mSyncAdapterPackagesProvider;
    private PackageManagerInternal.PackagesProvider mUseOpenWifiAppPackagesProvider;
    private PackageManagerInternal.PackagesProvider mVoiceInteractionPackagesProvider;

    static {
        PHONE_PERMISSIONS.add("android.permission.READ_PHONE_STATE");
        PHONE_PERMISSIONS.add("android.permission.CALL_PHONE");
        PHONE_PERMISSIONS.add("android.permission.READ_CALL_LOG");
        PHONE_PERMISSIONS.add("android.permission.WRITE_CALL_LOG");
        PHONE_PERMISSIONS.add("com.android.voicemail.permission.ADD_VOICEMAIL");
        PHONE_PERMISSIONS.add("android.permission.USE_SIP");
        PHONE_PERMISSIONS.add("android.permission.PROCESS_OUTGOING_CALLS");
        CONTACTS_PERMISSIONS.add("android.permission.READ_CONTACTS");
        CONTACTS_PERMISSIONS.add("android.permission.WRITE_CONTACTS");
        CONTACTS_PERMISSIONS.add("android.permission.GET_ACCOUNTS");
        ALWAYS_LOCATION_PERMISSIONS.add("android.permission.ACCESS_FINE_LOCATION");
        ALWAYS_LOCATION_PERMISSIONS.add("android.permission.ACCESS_COARSE_LOCATION");
        ALWAYS_LOCATION_PERMISSIONS.add("android.permission.ACCESS_BACKGROUND_LOCATION");
        ACTIVITY_RECOGNITION_PERMISSIONS.add("android.permission.ACTIVITY_RECOGNITION");
        CALENDAR_PERMISSIONS.add("android.permission.READ_CALENDAR");
        CALENDAR_PERMISSIONS.add("android.permission.WRITE_CALENDAR");
        SMS_PERMISSIONS.add("android.permission.SEND_SMS");
        SMS_PERMISSIONS.add("android.permission.RECEIVE_SMS");
        SMS_PERMISSIONS.add("android.permission.READ_SMS");
        SMS_PERMISSIONS.add("android.permission.RECEIVE_WAP_PUSH");
        SMS_PERMISSIONS.add("android.permission.RECEIVE_MMS");
        SMS_PERMISSIONS.add("android.permission.READ_CELL_BROADCASTS");
        MICROPHONE_PERMISSIONS.add("android.permission.RECORD_AUDIO");
        CAMERA_PERMISSIONS.add("android.permission.CAMERA");
        SENSORS_PERMISSIONS.add("android.permission.BODY_SENSORS");
        STORAGE_PERMISSIONS.add("android.permission.READ_EXTERNAL_STORAGE");
        STORAGE_PERMISSIONS.add("android.permission.WRITE_EXTERNAL_STORAGE");
        STORAGE_PERMISSIONS.add("android.permission.ACCESS_MEDIA_LOCATION");
    }

    DefaultPermissionGrantPolicy(Context context, Looper looper, PermissionManagerService permissionManager) {
        this.mContext = context;
        this.mHandler = new Handler(looper) {
            public void handleMessage(Message msg) {
                if (msg.what == 1) {
                    synchronized (DefaultPermissionGrantPolicy.this.mLock) {
                        if (DefaultPermissionGrantPolicy.this.mGrantExceptions == null) {
                            ArrayMap unused = DefaultPermissionGrantPolicy.this.mGrantExceptions = DefaultPermissionGrantPolicy.this.readDefaultPermissionExceptionsLocked();
                        }
                    }
                }
            }
        };
        this.mPermissionManager = permissionManager;
        this.mServiceInternal = (PackageManagerInternal) LocalServices.getService(PackageManagerInternal.class);
    }

    public void setLocationPackagesProvider(PackageManagerInternal.PackagesProvider provider) {
        synchronized (this.mLock) {
            this.mLocationPackagesProvider = provider;
        }
    }

    public void setLocationExtraPackagesProvider(PackageManagerInternal.PackagesProvider provider) {
        synchronized (this.mLock) {
            this.mLocationExtraPackagesProvider = provider;
        }
    }

    public void setVoiceInteractionPackagesProvider(PackageManagerInternal.PackagesProvider provider) {
        synchronized (this.mLock) {
            this.mVoiceInteractionPackagesProvider = provider;
        }
    }

    public void setSmsAppPackagesProvider(PackageManagerInternal.PackagesProvider provider) {
        synchronized (this.mLock) {
            this.mSmsAppPackagesProvider = provider;
        }
    }

    public void setDialerAppPackagesProvider(PackageManagerInternal.PackagesProvider provider) {
        synchronized (this.mLock) {
            this.mDialerAppPackagesProvider = provider;
        }
    }

    public void setSimCallManagerPackagesProvider(PackageManagerInternal.PackagesProvider provider) {
        synchronized (this.mLock) {
            this.mSimCallManagerPackagesProvider = provider;
        }
    }

    public void setUseOpenWifiAppPackagesProvider(PackageManagerInternal.PackagesProvider provider) {
        synchronized (this.mLock) {
            this.mUseOpenWifiAppPackagesProvider = provider;
        }
    }

    public void setSyncAdapterPackagesProvider(PackageManagerInternal.SyncAdapterPackagesProvider provider) {
        synchronized (this.mLock) {
            this.mSyncAdapterPackagesProvider = provider;
        }
    }

    public boolean wereDefaultPermissionsGrantedSinceBoot(int userId) {
        boolean z;
        synchronized (this.mLock) {
            z = this.mDefaultPermissionsGrantedUsers.indexOfKey(userId) >= 0;
        }
        return z;
    }

    public void grantDefaultPermissions(int userId) {
        grantPermissionsToSysComponentsAndPrivApps(userId);
        grantDefaultSystemHandlerPermissions(userId);
        grantDefaultPermissionExceptions(userId);
        synchronized (this.mLock) {
            this.mDefaultPermissionsGrantedUsers.put(userId, userId);
        }
    }

    private void grantRuntimePermissionsForSystemPackage(int userId, PackageInfo pkg) {
        Set<String> permissions = new ArraySet<>();
        for (String permission : pkg.requestedPermissions) {
            BasePermission bp = this.mPermissionManager.getPermission(permission);
            if (bp != null && bp.isRuntime()) {
                permissions.add(permission);
            }
        }
        if (!permissions.isEmpty()) {
            grantRuntimePermissions(pkg, permissions, true, userId);
        }
    }

    public void scheduleReadDefaultPermissionExceptions() {
        this.mHandler.sendEmptyMessage(1);
    }

    private void grantPermissionsToSysComponentsAndPrivApps(int userId) {
        Log.i(TAG, "Granting permissions to platform components for user " + userId);
        for (PackageInfo pkg : this.mContext.getPackageManager().getInstalledPackagesAsUser(DEFAULT_PACKAGE_INFO_QUERY_FLAGS, 0)) {
            if (pkg != null && isSysComponentOrPersistentPlatformSignedPrivApp(pkg) && doesPackageSupportRuntimePermissions(pkg) && !ArrayUtils.isEmpty(pkg.requestedPermissions)) {
                grantRuntimePermissionsForSystemPackage(userId, pkg);
            }
        }
    }

    @SafeVarargs
    private final void grantIgnoringSystemPackage(String packageName, int userId, Set<String>... permissionGroups) {
        grantPermissionsToPackage(packageName, userId, true, true, permissionGroups);
    }

    @SafeVarargs
    private final void grantSystemFixedPermissionsToSystemPackage(String packageName, int userId, Set<String>... permissionGroups) {
        grantPermissionsToSystemPackage(packageName, userId, true, permissionGroups);
    }

    @SafeVarargs
    private final void grantPermissionsToSystemPackage(String packageName, int userId, Set<String>... permissionGroups) {
        grantPermissionsToSystemPackage(packageName, userId, false, permissionGroups);
    }

    @SafeVarargs
    private final void grantPermissionsToSystemPackage(String packageName, int userId, boolean systemFixed, Set<String>... permissionGroups) {
        if (isSystemPackage(packageName)) {
            grantPermissionsToPackage(getSystemPackageInfo(packageName), userId, systemFixed, false, true, permissionGroups);
        }
    }

    @SafeVarargs
    private final void grantPermissionsToPackage(String packageName, int userId, boolean ignoreSystemPackage, boolean whitelistRestrictedPermissions, Set<String>... permissionGroups) {
        grantPermissionsToPackage(getPackageInfo(packageName), userId, false, ignoreSystemPackage, whitelistRestrictedPermissions, permissionGroups);
    }

    @SafeVarargs
    private final void grantPermissionsToPackage(PackageInfo packageInfo, int userId, boolean systemFixed, boolean ignoreSystemPackage, boolean whitelistRestrictedPermissions, Set<String>... permissionGroups) {
        Set<String>[] setArr = permissionGroups;
        if (packageInfo != null && doesPackageSupportRuntimePermissions(packageInfo)) {
            for (Set<String> permissionGroup : setArr) {
                grantRuntimePermissions(packageInfo, permissionGroup, systemFixed, ignoreSystemPackage, whitelistRestrictedPermissions, userId);
            }
        }
    }

    private void grantDefaultSystemHandlerPermissions(int userId) {
        PackageManagerInternal.PackagesProvider locationPackagesProvider;
        PackageManagerInternal.PackagesProvider locationExtraPackagesProvider;
        PackageManagerInternal.PackagesProvider voiceInteractionPackagesProvider;
        PackageManagerInternal.PackagesProvider smsAppPackagesProvider;
        PackageManagerInternal.PackagesProvider dialerAppPackagesProvider;
        PackageManagerInternal.PackagesProvider simCallManagerPackagesProvider;
        PackageManagerInternal.PackagesProvider useOpenWifiAppPackagesProvider;
        PackageManagerInternal.SyncAdapterPackagesProvider syncAdapterPackagesProvider;
        String[] strArr;
        String browserPackage;
        int i;
        char c;
        int i2;
        char c2;
        int i3;
        int i4 = userId;
        Log.i(TAG, "Granting permissions to default platform handlers for user " + i4);
        synchronized (this.mLock) {
            locationPackagesProvider = this.mLocationPackagesProvider;
            locationExtraPackagesProvider = this.mLocationExtraPackagesProvider;
            voiceInteractionPackagesProvider = this.mVoiceInteractionPackagesProvider;
            smsAppPackagesProvider = this.mSmsAppPackagesProvider;
            dialerAppPackagesProvider = this.mDialerAppPackagesProvider;
            simCallManagerPackagesProvider = this.mSimCallManagerPackagesProvider;
            useOpenWifiAppPackagesProvider = this.mUseOpenWifiAppPackagesProvider;
            syncAdapterPackagesProvider = this.mSyncAdapterPackagesProvider;
        }
        String[] voiceInteractPackageNames = voiceInteractionPackagesProvider != null ? voiceInteractionPackagesProvider.getPackages(i4) : null;
        String[] locationPackageNames = locationPackagesProvider != null ? locationPackagesProvider.getPackages(i4) : null;
        String[] locationExtraPackageNames = locationExtraPackagesProvider != null ? locationExtraPackagesProvider.getPackages(i4) : null;
        String[] smsAppPackageNames = smsAppPackagesProvider != null ? smsAppPackagesProvider.getPackages(i4) : null;
        String[] dialerAppPackageNames = dialerAppPackagesProvider != null ? dialerAppPackagesProvider.getPackages(i4) : null;
        String[] simCallManagerPackageNames = simCallManagerPackagesProvider != null ? simCallManagerPackagesProvider.getPackages(i4) : null;
        String[] useOpenWifiAppPackageNames = useOpenWifiAppPackagesProvider != null ? useOpenWifiAppPackagesProvider.getPackages(i4) : null;
        String[] contactsSyncAdapterPackages = syncAdapterPackagesProvider != null ? syncAdapterPackagesProvider.getPackages("com.android.contacts", i4) : null;
        if (syncAdapterPackagesProvider != null) {
            PackageManagerInternal.PackagesProvider packagesProvider = locationPackagesProvider;
            strArr = syncAdapterPackagesProvider.getPackages("com.android.calendar", i4);
        } else {
            strArr = null;
        }
        String[] calendarSyncAdapterPackages = strArr;
        String[] locationPackageNames2 = locationPackageNames;
        String[] locationExtraPackageNames2 = locationExtraPackageNames;
        String[] voiceInteractPackageNames2 = voiceInteractPackageNames;
        grantSystemFixedPermissionsToSystemPackage(getKnownPackage(2, i4), i4, STORAGE_PERMISSIONS);
        String verifier = getKnownPackage(3, i4);
        PackageManagerInternal.PackagesProvider packagesProvider2 = locationExtraPackagesProvider;
        grantSystemFixedPermissionsToSystemPackage(verifier, i4, STORAGE_PERMISSIONS);
        grantPermissionsToSystemPackage(verifier, i4, PHONE_PERMISSIONS, SMS_PERMISSIONS);
        String verifier2 = verifier;
        grantPermissionsToSystemPackage(getKnownPackage(1, i4), i4, PHONE_PERMISSIONS, CONTACTS_PERMISSIONS, ALWAYS_LOCATION_PERMISSIONS, CAMERA_PERMISSIONS);
        grantPermissionsToSystemPackage(getDefaultSystemHandlerActivityPackage("android.media.action.IMAGE_CAPTURE", i4), i4, CAMERA_PERMISSIONS, MICROPHONE_PERMISSIONS, STORAGE_PERMISSIONS);
        grantPermissionsToSystemPackage(getDefaultSystemHandlerActivityPackage("android.provider.MediaStore.RECORD_SOUND", i4), i4, MICROPHONE_PERMISSIONS);
        grantSystemFixedPermissionsToSystemPackage(getDefaultProviderAuthorityPackage("media", i4), i4, STORAGE_PERMISSIONS, PHONE_PERMISSIONS);
        grantSystemFixedPermissionsToSystemPackage(getDefaultProviderAuthorityPackage("downloads", i4), i4, STORAGE_PERMISSIONS);
        grantSystemFixedPermissionsToSystemPackage(getDefaultSystemHandlerActivityPackage("android.intent.action.VIEW_DOWNLOADS", i4), i4, STORAGE_PERMISSIONS);
        grantSystemFixedPermissionsToSystemPackage(getDefaultProviderAuthorityPackage("com.android.externalstorage.documents", i4), i4, STORAGE_PERMISSIONS);
        grantSystemFixedPermissionsToSystemPackage(getDefaultSystemHandlerActivityPackage("android.credentials.INSTALL", i4), i4, STORAGE_PERMISSIONS);
        if (dialerAppPackageNames == null) {
            grantDefaultPermissionsToDefaultSystemDialerApp(getDefaultSystemHandlerActivityPackage("android.intent.action.DIAL", i4), i4);
        } else {
            for (String dialerAppPackageName : dialerAppPackageNames) {
                grantDefaultPermissionsToDefaultSystemDialerApp(dialerAppPackageName, i4);
            }
        }
        String[] simCallManagerPackageNames2 = simCallManagerPackageNames;
        if (simCallManagerPackageNames2 != null) {
            for (String simCallManagerPackageName : simCallManagerPackageNames2) {
                grantDefaultPermissionsToDefaultSystemSimCallManager(simCallManagerPackageName, i4);
            }
        }
        String[] useOpenWifiAppPackageNames2 = useOpenWifiAppPackageNames;
        if (useOpenWifiAppPackageNames2 != null) {
            int length = useOpenWifiAppPackageNames2.length;
            int i5 = 0;
            while (i5 < length) {
                grantDefaultPermissionsToDefaultSystemUseOpenWifiApp(useOpenWifiAppPackageNames2[i5], i4);
                i5++;
                dialerAppPackageNames = dialerAppPackageNames;
            }
        }
        if (smsAppPackageNames == null) {
            grantDefaultPermissionsToDefaultSystemSmsApp(getDefaultSystemHandlerActivityPackageForCategory("android.intent.category.APP_MESSAGING", i4), i4);
        } else {
            for (String smsPackage : smsAppPackageNames) {
                grantDefaultPermissionsToDefaultSystemSmsApp(smsPackage, i4);
            }
        }
        grantSystemFixedPermissionsToSystemPackage(getDefaultSystemHandlerActivityPackage("android.provider.Telephony.SMS_CB_RECEIVED", i4), i4, SMS_PERMISSIONS);
        grantPermissionsToSystemPackage(getDefaultSystemHandlerServicePackage("android.provider.Telephony.SMS_CARRIER_PROVISION", i4), i4, SMS_PERMISSIONS);
        grantPermissionsToSystemPackage(getDefaultSystemHandlerActivityPackageForCategory("android.intent.category.APP_CALENDAR", i4), i4, CALENDAR_PERMISSIONS, CONTACTS_PERMISSIONS);
        String calendarProvider = getDefaultProviderAuthorityPackage("com.android.calendar", i4);
        String[] strArr2 = smsAppPackageNames;
        grantPermissionsToSystemPackage(calendarProvider, i4, CONTACTS_PERMISSIONS, STORAGE_PERMISSIONS);
        grantSystemFixedPermissionsToSystemPackage(calendarProvider, i4, CALENDAR_PERMISSIONS);
        grantPermissionToEachSystemPackage(getHeadlessSyncAdapterPackages(calendarSyncAdapterPackages, i4), i4, CALENDAR_PERMISSIONS);
        String[] strArr3 = calendarSyncAdapterPackages;
        grantPermissionsToSystemPackage(getDefaultSystemHandlerActivityPackageForCategory("android.intent.category.APP_CONTACTS", i4), i4, CONTACTS_PERMISSIONS, PHONE_PERMISSIONS);
        grantPermissionToEachSystemPackage(getHeadlessSyncAdapterPackages(contactsSyncAdapterPackages, i4), i4, CONTACTS_PERMISSIONS);
        String contactsProviderPackage = getDefaultProviderAuthorityPackage("com.android.contacts", i4);
        grantSystemFixedPermissionsToSystemPackage(contactsProviderPackage, i4, CONTACTS_PERMISSIONS, PHONE_PERMISSIONS);
        grantPermissionsToSystemPackage(contactsProviderPackage, i4, STORAGE_PERMISSIONS);
        grantPermissionsToSystemPackage(getDefaultSystemHandlerActivityPackage("android.app.action.PROVISION_MANAGED_DEVICE", i4), i4, CONTACTS_PERMISSIONS);
        grantPermissionsToSystemPackage(getDefaultSystemHandlerActivityPackageForCategory("android.intent.category.APP_MAPS", i4), i4, ALWAYS_LOCATION_PERMISSIONS);
        grantPermissionsToSystemPackage(getDefaultSystemHandlerActivityPackageForCategory("android.intent.category.APP_GALLERY", i4), i4, STORAGE_PERMISSIONS);
        grantPermissionsToSystemPackage(getDefaultSystemHandlerActivityPackageForCategory("android.intent.category.APP_EMAIL", i4), i4, CONTACTS_PERMISSIONS, CALENDAR_PERMISSIONS);
        String browserPackage2 = getKnownPackage(4, i4);
        if (browserPackage2 == null) {
            String browserPackage3 = getDefaultSystemHandlerActivityPackageForCategory("android.intent.category.APP_BROWSER", i4);
            if (!isSystemPackage(browserPackage3)) {
                browserPackage = null;
            } else {
                browserPackage = browserPackage3;
            }
        } else {
            browserPackage = browserPackage2;
        }
        String[] strArr4 = contactsSyncAdapterPackages;
        String str = contactsProviderPackage;
        String[] locationExtraPackageNames3 = locationExtraPackageNames2;
        String str2 = verifier2;
        String[] locationPackageNames3 = locationPackageNames2;
        String str3 = calendarProvider;
        String[] strArr5 = simCallManagerPackageNames2;
        PackageManagerInternal.PackagesProvider packagesProvider3 = voiceInteractionPackagesProvider;
        String[] simCallManagerPackageNames3 = voiceInteractPackageNames2;
        String[] voiceInteractPackageNames3 = useOpenWifiAppPackageNames2;
        grantPermissionsToPackage(browserPackage, userId, false, true, ALWAYS_LOCATION_PERMISSIONS);
        int i6 = 6;
        if (simCallManagerPackageNames3 != null) {
            int length2 = simCallManagerPackageNames3.length;
            int i7 = 0;
            while (i7 < length2) {
                String voiceInteractPackageName = simCallManagerPackageNames3[i7];
                Set[] setArr = new Set[i6];
                setArr[0] = CONTACTS_PERMISSIONS;
                setArr[1] = CALENDAR_PERMISSIONS;
                setArr[2] = MICROPHONE_PERMISSIONS;
                setArr[3] = PHONE_PERMISSIONS;
                setArr[4] = SMS_PERMISSIONS;
                setArr[5] = ALWAYS_LOCATION_PERMISSIONS;
                grantPermissionsToSystemPackage(voiceInteractPackageName, i4, setArr);
                i7++;
                i6 = 6;
            }
            i = 2;
        } else {
            i = 2;
        }
        if (ActivityManager.isLowRamDeviceStatic()) {
            String defaultSystemHandlerActivityPackage = getDefaultSystemHandlerActivityPackage("android.search.action.GLOBAL_SEARCH", i4);
            Set[] setArr2 = new Set[i];
            setArr2[0] = MICROPHONE_PERMISSIONS;
            setArr2[1] = ALWAYS_LOCATION_PERMISSIONS;
            grantPermissionsToSystemPackage(defaultSystemHandlerActivityPackage, i4, setArr2);
        }
        grantPermissionsToSystemPackage(getDefaultSystemHandlerServicePackage(new Intent("android.speech.RecognitionService").addCategory("android.intent.category.DEFAULT"), i4), i4, MICROPHONE_PERMISSIONS);
        String[] locationPackageNames4 = locationPackageNames3;
        if (locationPackageNames4 != null) {
            for (String packageName : locationPackageNames4) {
                Set[] setArr3 = new Set[8];
                setArr3[0] = CONTACTS_PERMISSIONS;
                setArr3[1] = CALENDAR_PERMISSIONS;
                setArr3[i] = MICROPHONE_PERMISSIONS;
                setArr3[3] = PHONE_PERMISSIONS;
                setArr3[4] = SMS_PERMISSIONS;
                setArr3[5] = CAMERA_PERMISSIONS;
                setArr3[6] = SENSORS_PERMISSIONS;
                setArr3[7] = STORAGE_PERMISSIONS;
                grantPermissionsToSystemPackage(packageName, i4, setArr3);
                Set[] setArr4 = new Set[i];
                setArr4[0] = ALWAYS_LOCATION_PERMISSIONS;
                setArr4[1] = ACTIVITY_RECOGNITION_PERMISSIONS;
                grantSystemFixedPermissionsToSystemPackage(packageName, i4, setArr4);
            }
        }
        if (locationExtraPackageNames3 != null) {
            for (String packageName2 : locationExtraPackageNames3) {
                grantPermissionsToSystemPackage(packageName2, i4, ALWAYS_LOCATION_PERMISSIONS);
            }
        }
        Intent musicIntent = new Intent("android.intent.action.VIEW").addCategory("android.intent.category.DEFAULT").setDataAndType(Uri.fromFile(new File("foo.mp3")), AUDIO_MIME_TYPE);
        grantPermissionsToSystemPackage(getDefaultSystemHandlerActivityPackage(musicIntent, i4), i4, STORAGE_PERMISSIONS);
        String[] strArr6 = locationExtraPackageNames3;
        grantPermissionsToSystemPackage(getDefaultSystemHandlerActivityPackage(new Intent("android.intent.action.MAIN").addCategory("android.intent.category.HOME").addCategory("android.intent.category.LAUNCHER_APP"), i4), i4, ALWAYS_LOCATION_PERMISSIONS);
        if (this.mContext.getPackageManager().hasSystemFeature("android.hardware.type.watch", 0)) {
            String wearPackage = getDefaultSystemHandlerActivityPackageForCategory("android.intent.category.HOME_MAIN", i4);
            grantPermissionsToSystemPackage(wearPackage, i4, CONTACTS_PERMISSIONS, MICROPHONE_PERMISSIONS, ALWAYS_LOCATION_PERMISSIONS);
            c = 0;
            grantSystemFixedPermissionsToSystemPackage(wearPackage, i4, PHONE_PERMISSIONS);
            Intent intent = musicIntent;
            i2 = 1;
            grantPermissionsToSystemPackage(getDefaultSystemHandlerActivityPackage(ACTION_TRACK, i4), i4, SENSORS_PERMISSIONS, ALWAYS_LOCATION_PERMISSIONS);
        } else {
            c = 0;
            Intent intent2 = musicIntent;
            i2 = 1;
        }
        Set[] setArr5 = new Set[i2];
        setArr5[c] = ALWAYS_LOCATION_PERMISSIONS;
        grantSystemFixedPermissionsToSystemPackage("com.android.printspooler", i4, setArr5);
        String defaultSystemHandlerActivityPackage2 = getDefaultSystemHandlerActivityPackage("android.telephony.action.EMERGENCY_ASSISTANCE", i4);
        Set[] setArr6 = new Set[2];
        setArr6[c] = CONTACTS_PERMISSIONS;
        setArr6[1] = PHONE_PERMISSIONS;
        grantSystemFixedPermissionsToSystemPackage(defaultSystemHandlerActivityPackage2, i4, setArr6);
        grantPermissionsToSystemPackage(getDefaultSystemHandlerActivityPackage(new Intent("android.intent.action.VIEW").setType("vnd.android.cursor.item/ndef_msg"), i4), i4, CONTACTS_PERMISSIONS, PHONE_PERMISSIONS);
        grantSystemFixedPermissionsToSystemPackage(getDefaultSystemHandlerActivityPackage("android.os.storage.action.MANAGE_STORAGE", i4), i4, STORAGE_PERMISSIONS);
        grantSystemFixedPermissionsToSystemPackage("com.android.companiondevicemanager", i4, ALWAYS_LOCATION_PERMISSIONS);
        grantSystemFixedPermissionsToSystemPackage(getDefaultSystemHandlerActivityPackage("android.intent.action.RINGTONE_PICKER", i4), i4, STORAGE_PERMISSIONS);
        String textClassifierPackageName = this.mContext.getPackageManager().getSystemTextClassifierPackageName();
        if (!TextUtils.isEmpty(textClassifierPackageName)) {
            grantPermissionsToSystemPackage(textClassifierPackageName, i4, PHONE_PERMISSIONS, SMS_PERMISSIONS, CALENDAR_PERMISSIONS, ALWAYS_LOCATION_PERMISSIONS, CONTACTS_PERMISSIONS);
        }
        String attentionServicePackageName = this.mContext.getPackageManager().getAttentionServicePackageName();
        if (!TextUtils.isEmpty(attentionServicePackageName)) {
            i3 = 1;
            c2 = 0;
            grantPermissionsToSystemPackage(attentionServicePackageName, i4, CAMERA_PERMISSIONS);
        } else {
            i3 = 1;
            c2 = 0;
        }
        Set[] setArr7 = new Set[i3];
        setArr7[c2] = STORAGE_PERMISSIONS;
        grantSystemFixedPermissionsToSystemPackage(UserBackupManagerService.SHARED_BACKUP_AGENT_PACKAGE, i4, setArr7);
        String systemCaptionsServicePackageName = this.mContext.getPackageManager().getSystemCaptionsServicePackageName();
        if (!TextUtils.isEmpty(systemCaptionsServicePackageName)) {
            grantPermissionsToSystemPackage(systemCaptionsServicePackageName, i4, MICROPHONE_PERMISSIONS);
        }
    }

    private String getDefaultSystemHandlerActivityPackageForCategory(String category, int userId) {
        return getDefaultSystemHandlerActivityPackage(new Intent("android.intent.action.MAIN").addCategory(category), userId);
    }

    @SafeVarargs
    private final void grantPermissionToEachSystemPackage(ArrayList<String> packages, int userId, Set<String>... permissions) {
        if (packages != null) {
            int count = packages.size();
            for (int i = 0; i < count; i++) {
                grantPermissionsToSystemPackage(packages.get(i), userId, permissions);
            }
        }
    }

    private String getKnownPackage(int knownPkgId, int userId) {
        return this.mServiceInternal.getKnownPackageName(knownPkgId, userId);
    }

    private void grantDefaultPermissionsToDefaultSystemDialerApp(String dialerPackage, int userId) {
        if (dialerPackage != null) {
            if (this.mContext.getPackageManager().hasSystemFeature("android.hardware.type.watch", 0)) {
                grantSystemFixedPermissionsToSystemPackage(dialerPackage, userId, PHONE_PERMISSIONS);
            } else {
                grantPermissionsToSystemPackage(dialerPackage, userId, PHONE_PERMISSIONS);
            }
            grantPermissionsToSystemPackage(dialerPackage, userId, CONTACTS_PERMISSIONS, SMS_PERMISSIONS, MICROPHONE_PERMISSIONS, CAMERA_PERMISSIONS);
        }
    }

    private void grantDefaultPermissionsToDefaultSystemSmsApp(String smsPackage, int userId) {
        grantPermissionsToSystemPackage(smsPackage, userId, PHONE_PERMISSIONS, CONTACTS_PERMISSIONS, SMS_PERMISSIONS, STORAGE_PERMISSIONS, MICROPHONE_PERMISSIONS, CAMERA_PERMISSIONS);
    }

    private void grantDefaultPermissionsToDefaultSystemUseOpenWifiApp(String useOpenWifiPackage, int userId) {
        grantPermissionsToSystemPackage(useOpenWifiPackage, userId, ALWAYS_LOCATION_PERMISSIONS);
    }

    public void grantDefaultPermissionsToDefaultUseOpenWifiApp(String packageName, int userId) {
        Log.i(TAG, "Granting permissions to default Use Open WiFi app for user:" + userId);
        grantIgnoringSystemPackage(packageName, userId, ALWAYS_LOCATION_PERMISSIONS);
    }

    public void grantDefaultPermissionsToDefaultSimCallManager(String packageName, int userId) {
        if (packageName != null) {
            Log.i(TAG, "Granting permissions to sim call manager for user:" + userId);
            grantPermissionsToPackage(packageName, userId, false, true, PHONE_PERMISSIONS, MICROPHONE_PERMISSIONS);
        }
    }

    private void grantDefaultPermissionsToDefaultSystemSimCallManager(String packageName, int userId) {
        if (isSystemPackage(packageName)) {
            grantDefaultPermissionsToDefaultSimCallManager(packageName, userId);
        }
    }

    public void grantDefaultPermissionsToEnabledCarrierApps(String[] packageNames, int userId) {
        Log.i(TAG, "Granting permissions to enabled carrier apps for user:" + userId);
        if (packageNames != null) {
            for (String packageName : packageNames) {
                grantPermissionsToSystemPackage(packageName, userId, PHONE_PERMISSIONS, ALWAYS_LOCATION_PERMISSIONS, SMS_PERMISSIONS);
            }
        }
    }

    public void grantDefaultPermissionsToEnabledImsServices(String[] packageNames, int userId) {
        Log.i(TAG, "Granting permissions to enabled ImsServices for user:" + userId);
        if (packageNames != null) {
            for (String packageName : packageNames) {
                grantPermissionsToSystemPackage(packageName, userId, PHONE_PERMISSIONS, MICROPHONE_PERMISSIONS, ALWAYS_LOCATION_PERMISSIONS, CAMERA_PERMISSIONS, CONTACTS_PERMISSIONS);
            }
        }
    }

    public void grantDefaultPermissionsToEnabledTelephonyDataServices(String[] packageNames, int userId) {
        Log.i(TAG, "Granting permissions to enabled data services for user:" + userId);
        if (packageNames != null) {
            for (String packageName : packageNames) {
                grantSystemFixedPermissionsToSystemPackage(packageName, userId, PHONE_PERMISSIONS, ALWAYS_LOCATION_PERMISSIONS);
            }
        }
    }

    public void revokeDefaultPermissionsFromDisabledTelephonyDataServices(String[] packageNames, int userId) {
        Log.i(TAG, "Revoking permissions from disabled data services for user:" + userId);
        if (packageNames != null) {
            for (String packageName : packageNames) {
                PackageInfo pkg = getSystemPackageInfo(packageName);
                if (isSystemPackage(pkg) && doesPackageSupportRuntimePermissions(pkg)) {
                    revokeRuntimePermissions(packageName, PHONE_PERMISSIONS, true, userId);
                    revokeRuntimePermissions(packageName, ALWAYS_LOCATION_PERMISSIONS, true, userId);
                }
            }
        }
    }

    public void grantDefaultPermissionsToActiveLuiApp(String packageName, int userId) {
        Log.i(TAG, "Granting permissions to active LUI app for user:" + userId);
        grantSystemFixedPermissionsToSystemPackage(packageName, userId, CAMERA_PERMISSIONS);
    }

    public void revokeDefaultPermissionsFromLuiApps(String[] packageNames, int userId) {
        Log.i(TAG, "Revoke permissions from LUI apps for user:" + userId);
        if (packageNames != null) {
            for (String packageName : packageNames) {
                PackageInfo pkg = getSystemPackageInfo(packageName);
                if (isSystemPackage(pkg) && doesPackageSupportRuntimePermissions(pkg)) {
                    revokeRuntimePermissions(packageName, CAMERA_PERMISSIONS, true, userId);
                }
            }
        }
    }

    public void grantDefaultPermissionsToDefaultBrowser(String packageName, int userId) {
        Log.i(TAG, "Granting permissions to default browser for user:" + userId);
        grantPermissionsToSystemPackage(packageName, userId, ALWAYS_LOCATION_PERMISSIONS);
    }

    private String getDefaultSystemHandlerActivityPackage(String intentAction, int userId) {
        return getDefaultSystemHandlerActivityPackage(new Intent(intentAction), userId);
    }

    private String getDefaultSystemHandlerActivityPackage(Intent intent, int userId) {
        ResolveInfo handler = this.mContext.getPackageManager().resolveActivityAsUser(intent, DEFAULT_INTENT_QUERY_FLAGS, userId);
        if (handler == null || handler.activityInfo == null || this.mServiceInternal.isResolveActivityComponent(handler.activityInfo)) {
            return null;
        }
        String packageName = handler.activityInfo.packageName;
        if (isSystemPackage(packageName)) {
            return packageName;
        }
        return null;
    }

    private String getDefaultSystemHandlerServicePackage(String intentAction, int userId) {
        return getDefaultSystemHandlerServicePackage(new Intent(intentAction), userId);
    }

    private String getDefaultSystemHandlerServicePackage(Intent intent, int userId) {
        List<ResolveInfo> handlers = this.mContext.getPackageManager().queryIntentServicesAsUser(intent, DEFAULT_INTENT_QUERY_FLAGS, userId);
        if (handlers == null) {
            return null;
        }
        int handlerCount = handlers.size();
        for (int i = 0; i < handlerCount; i++) {
            String handlerPackage = handlers.get(i).serviceInfo.packageName;
            if (isSystemPackage(handlerPackage)) {
                return handlerPackage;
            }
        }
        return null;
    }

    private ArrayList<String> getHeadlessSyncAdapterPackages(String[] syncAdapterPackageNames, int userId) {
        ArrayList<String> syncAdapterPackages = new ArrayList<>();
        Intent homeIntent = new Intent("android.intent.action.MAIN").addCategory("android.intent.category.LAUNCHER");
        for (String syncAdapterPackageName : syncAdapterPackageNames) {
            homeIntent.setPackage(syncAdapterPackageName);
            if (this.mContext.getPackageManager().resolveActivityAsUser(homeIntent, DEFAULT_INTENT_QUERY_FLAGS, userId) == null && isSystemPackage(syncAdapterPackageName)) {
                syncAdapterPackages.add(syncAdapterPackageName);
            }
        }
        return syncAdapterPackages;
    }

    private String getDefaultProviderAuthorityPackage(String authority, int userId) {
        ProviderInfo provider = this.mContext.getPackageManager().resolveContentProviderAsUser(authority, DEFAULT_INTENT_QUERY_FLAGS, userId);
        if (provider != null) {
            return provider.packageName;
        }
        return null;
    }

    private boolean isSystemPackage(String packageName) {
        return isSystemPackage(getPackageInfo(packageName));
    }

    private boolean isSystemPackage(PackageInfo pkg) {
        if (pkg != null && pkg.applicationInfo.isSystemApp() && !isSysComponentOrPersistentPlatformSignedPrivApp(pkg)) {
            return true;
        }
        return false;
    }

    private void grantRuntimePermissions(PackageInfo pkg, Set<String> permissions, boolean systemFixed, int userId) {
        grantRuntimePermissions(pkg, permissions, systemFixed, false, true, userId);
    }

    private void revokeRuntimePermissions(String packageName, Set<String> permissions, boolean systemFixed, int userId) {
        String str = packageName;
        PackageInfo pkg = getSystemPackageInfo(packageName);
        if (!ArrayUtils.isEmpty(pkg.requestedPermissions)) {
            Set<String> revokablePermissions = new ArraySet<>(Arrays.asList(pkg.requestedPermissions));
            for (String permission : permissions) {
                if (revokablePermissions.contains(permission)) {
                    UserHandle user = UserHandle.of(userId);
                    int flags = this.mContext.getPackageManager().getPermissionFlags(permission, packageName, user);
                    if ((flags & 32) != 0 && (flags & 4) == 0) {
                        if ((flags & 16) == 0 || systemFixed) {
                            this.mContext.getPackageManager().revokeRuntimePermission(packageName, permission, user);
                            this.mContext.getPackageManager().updatePermissionFlags(permission, packageName, 32, 0, user);
                        }
                    }
                }
            }
        }
    }

    private boolean isFixedOrUserSet(int flags) {
        return (flags & 23) != 0;
    }

    private String getBackgroundPermission(String permission) {
        try {
            return this.mContext.getPackageManager().getPermissionInfo(permission, 0).backgroundPermission;
        } catch (PackageManager.NameNotFoundException e) {
            return null;
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:45:0x00e6  */
    /* JADX WARNING: Removed duplicated region for block: B:52:0x0103  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void grantRuntimePermissions(android.content.pm.PackageInfo r32, java.util.Set<java.lang.String> r33, boolean r34, boolean r35, boolean r36, int r37) {
        /*
            r31 = this;
            r1 = r31
            r2 = r32
            r3 = r33
            android.os.UserHandle r10 = android.os.UserHandle.of(r37)
            if (r2 != 0) goto L_0x000d
            return
        L_0x000d:
            java.lang.String[] r0 = r2.requestedPermissions
            boolean r4 = com.android.internal.util.ArrayUtils.isEmpty(r0)
            if (r4 == 0) goto L_0x0016
            return
        L_0x0016:
            java.lang.String r4 = r2.packageName
            android.content.pm.PackageInfo r4 = r1.getPackageInfo(r4)
            java.lang.String[] r11 = r4.requestedPermissions
            int r12 = r0.length
            r4 = 0
        L_0x0020:
            if (r4 >= r12) goto L_0x0030
            r5 = r0[r4]
            boolean r5 = com.android.internal.util.ArrayUtils.contains(r11, r5)
            if (r5 != 0) goto L_0x002d
            r5 = 0
            r0[r4] = r5
        L_0x002d:
            int r4 = r4 + 1
            goto L_0x0020
        L_0x0030:
            com.android.server.pm.permission.-$$Lambda$DefaultPermissionGrantPolicy$SHfHTWKpfBf_vZtWArm-FlNBI8k r4 = com.android.server.pm.permission.$$Lambda$DefaultPermissionGrantPolicy$SHfHTWKpfBf_vZtWArmFlNBI8k.INSTANCE
            java.lang.Object[] r4 = com.android.internal.util.ArrayUtils.filterNotNull(r0, r4)
            java.lang.String[] r4 = (java.lang.String[]) r4
            android.content.Context r0 = r1.mContext     // Catch:{ NameNotFoundException -> 0x02b8 }
            android.content.Context r5 = r1.mContext     // Catch:{ NameNotFoundException -> 0x02b8 }
            java.lang.String r5 = r5.getPackageName()     // Catch:{ NameNotFoundException -> 0x02b8 }
            r13 = 0
            android.content.Context r0 = r0.createPackageContextAsUser(r5, r13, r10)     // Catch:{ NameNotFoundException -> 0x02b8 }
            android.content.pm.PackageManager r0 = r0.getPackageManager()     // Catch:{ NameNotFoundException -> 0x02b8 }
            android.util.ArraySet r5 = new android.util.ArraySet
            r5.<init>(r3)
            r14 = r5
            android.content.pm.ApplicationInfo r15 = r2.applicationInfo
            r5 = 32
            if (r34 == 0) goto L_0x0058
            r5 = r5 | 16
        L_0x0058:
            android.content.Context r6 = r1.mContext
            java.lang.Class<android.permission.PermissionManager> r7 = android.permission.PermissionManager.class
            java.lang.Object r6 = r6.getSystemService(r7)
            android.permission.PermissionManager r6 = (android.permission.PermissionManager) r6
            java.util.List r9 = r6.getSplitPermissions()
            int r8 = r9.size()
            r6 = 0
        L_0x006b:
            if (r6 >= r8) goto L_0x009a
            java.lang.Object r7 = r9.get(r6)
            android.permission.PermissionManager$SplitPermissionInfo r7 = (android.permission.PermissionManager.SplitPermissionInfo) r7
            if (r15 == 0) goto L_0x0092
            int r13 = r15.targetSdkVersion
            r16 = r5
            int r5 = r7.getTargetSdk()
            if (r13 >= r5) goto L_0x0094
            java.lang.String r5 = r7.getSplitPermission()
            boolean r5 = r3.contains(r5)
            if (r5 == 0) goto L_0x0094
            java.util.List r5 = r7.getNewPermissions()
            r14.addAll(r5)
            goto L_0x0094
        L_0x0092:
            r16 = r5
        L_0x0094:
            int r6 = r6 + 1
            r5 = r16
            r13 = 0
            goto L_0x006b
        L_0x009a:
            r16 = r5
            r5 = 0
            if (r35 != 0) goto L_0x00d4
            if (r15 == 0) goto L_0x00d4
            boolean r6 = r15.isUpdatedSystemApp()
            if (r6 == 0) goto L_0x00d4
            android.content.pm.PackageManagerInternal r6 = r1.mServiceInternal
            java.lang.String r7 = r2.packageName
            java.lang.String r6 = r6.getDisabledSystemPackageName(r7)
            android.content.pm.PackageInfo r6 = r1.getSystemPackageInfo(r6)
            if (r6 == 0) goto L_0x00d4
            java.lang.String[] r7 = r6.requestedPermissions
            boolean r7 = com.android.internal.util.ArrayUtils.isEmpty(r7)
            if (r7 == 0) goto L_0x00be
            return
        L_0x00be:
            java.lang.String[] r7 = r6.requestedPermissions
            boolean r7 = java.util.Arrays.equals(r4, r7)
            if (r7 != 0) goto L_0x00d4
            android.util.ArraySet r7 = new android.util.ArraySet
            java.util.List r13 = java.util.Arrays.asList(r4)
            r7.<init>(r13)
            r5 = r7
            java.lang.String[] r4 = r6.requestedPermissions
            r13 = r4
            goto L_0x00d6
        L_0x00d4:
            r13 = r4
            r7 = r5
        L_0x00d6:
            int r6 = r13.length
            java.lang.String[] r5 = new java.lang.String[r6]
            r4 = 0
            r17 = 0
            r18 = 0
            r30 = r18
            r18 = r4
            r4 = r30
        L_0x00e4:
            if (r4 >= r6) goto L_0x0100
            r3 = r13[r4]
            java.lang.String r19 = r1.getBackgroundPermission(r3)
            if (r19 == 0) goto L_0x00f3
            r5[r18] = r3
            int r18 = r18 + 1
            goto L_0x00fb
        L_0x00f3:
            int r19 = r6 + -1
            int r19 = r19 - r17
            r5[r19] = r3
            int r17 = r17 + 1
        L_0x00fb:
            int r4 = r4 + 1
            r3 = r33
            goto L_0x00e4
        L_0x0100:
            r3 = 0
        L_0x0101:
            if (r3 >= r6) goto L_0x02b7
            r4 = r13[r3]
            if (r7 == 0) goto L_0x011e
            boolean r19 = r7.contains(r4)
            if (r19 != 0) goto L_0x011e
            r28 = r0
            r19 = r5
            r20 = r6
            r26 = r7
            r23 = r8
            r24 = r9
            r25 = r11
            r0 = 0
            goto L_0x02a5
        L_0x011e:
            boolean r19 = r14.contains(r4)
            if (r19 == 0) goto L_0x0295
            r19 = r5
            android.content.Context r5 = r1.mContext
            android.content.pm.PackageManager r5 = r5.getPackageManager()
            r20 = r6
            java.lang.String r6 = r2.packageName
            int r6 = r5.getPermissionFlags(r4, r6, r10)
            if (r34 == 0) goto L_0x013c
            r5 = r6 & 16
            if (r5 == 0) goto L_0x013c
            r5 = 1
            goto L_0x013d
        L_0x013c:
            r5 = 0
        L_0x013d:
            r21 = r5
            boolean r5 = r1.isFixedOrUserSet(r6)
            if (r5 == 0) goto L_0x015b
            if (r35 != 0) goto L_0x015b
            if (r21 == 0) goto L_0x014a
            goto L_0x015b
        L_0x014a:
            r28 = r0
            r22 = r6
            r26 = r7
            r23 = r8
            r24 = r9
            r25 = r11
            r9 = r37
            r11 = r4
            goto L_0x0277
        L_0x015b:
            r5 = r6 & 4
            if (r5 == 0) goto L_0x016c
            r28 = r0
            r26 = r7
            r23 = r8
            r24 = r9
            r25 = r11
            r0 = 0
            goto L_0x02a5
        L_0x016c:
            r5 = r6 & 14336(0x3800, float:2.0089E-41)
            r16 = r16 | r5
            if (r36 == 0) goto L_0x019a
            boolean r5 = r1.isPermissionRestricted(r4)
            if (r5 == 0) goto L_0x019a
            android.content.Context r5 = r1.mContext
            android.content.pm.PackageManager r5 = r5.getPackageManager()
            r22 = r6
            java.lang.String r6 = r2.packageName
            r23 = 4096(0x1000, float:5.74E-42)
            r24 = 4096(0x1000, float:5.74E-42)
            r25 = r4
            r4 = r5
            r5 = r25
            r26 = r7
            r7 = r23
            r23 = r8
            r8 = r24
            r24 = r9
            r9 = r10
            r4.updatePermissionFlags(r5, r6, r7, r8, r9)
            goto L_0x01a4
        L_0x019a:
            r25 = r4
            r22 = r6
            r26 = r7
            r23 = r8
            r24 = r9
        L_0x01a4:
            if (r21 == 0) goto L_0x01b8
            android.content.Context r4 = r1.mContext
            android.content.pm.PackageManager r4 = r4.getPackageManager()
            java.lang.String r6 = r2.packageName
            r8 = r22 & -17
            r5 = r25
            r7 = r22
            r9 = r10
            r4.updatePermissionFlags(r5, r6, r7, r8, r9)
        L_0x01b8:
            java.lang.String r4 = r2.packageName
            r9 = r25
            int r4 = r0.checkPermission(r9, r4)
            if (r4 == 0) goto L_0x01cd
            android.content.Context r4 = r1.mContext
            android.content.pm.PackageManager r4 = r4.getPackageManager()
            java.lang.String r5 = r2.packageName
            r4.grantRuntimePermission(r5, r9, r10)
        L_0x01cd:
            android.content.Context r4 = r1.mContext
            android.content.pm.PackageManager r4 = r4.getPackageManager()
            java.lang.String r6 = r2.packageName
            r5 = r9
            r7 = r16
            r8 = r16
            r25 = r11
            r11 = r9
            r9 = r10
            r4.updatePermissionFlags(r5, r6, r7, r8, r9)
            android.content.pm.ApplicationInfo r4 = r2.applicationInfo
            int r4 = r4.uid
            int r4 = android.os.UserHandle.getAppId(r4)
            r9 = r37
            int r4 = android.os.UserHandle.getUid(r9, r4)
            com.android.server.pm.permission.PermissionManagerService r5 = r1.mPermissionManager
            android.util.ArrayMap r5 = r5.getBackgroundPermissions()
            java.lang.Object r5 = r5.get(r11)
            java.util.List r5 = (java.util.List) r5
            if (r5 == 0) goto L_0x0239
            int r6 = r5.size()
            r7 = 0
        L_0x0202:
            if (r7 >= r6) goto L_0x0234
            java.lang.Object r8 = r5.get(r7)
            java.lang.String r8 = (java.lang.String) r8
            r27 = r5
            java.lang.String r5 = r2.packageName
            int r5 = r0.checkPermission(r8, r5)
            if (r5 != 0) goto L_0x022b
            android.content.Context r5 = r1.mContext
            r28 = r6
            java.lang.Class<android.app.AppOpsManager> r6 = android.app.AppOpsManager.class
            java.lang.Object r5 = r5.getSystemService(r6)
            android.app.AppOpsManager r5 = (android.app.AppOpsManager) r5
            java.lang.String r6 = android.app.AppOpsManager.permissionToOp(r8)
            r29 = r8
            r8 = 0
            r5.setUidMode(r6, r4, r8)
            goto L_0x023b
        L_0x022b:
            r28 = r6
            r29 = r8
            int r7 = r7 + 1
            r5 = r27
            goto L_0x0202
        L_0x0234:
            r27 = r5
            r28 = r6
            goto L_0x023b
        L_0x0239:
            r27 = r5
        L_0x023b:
            java.lang.String r5 = r1.getBackgroundPermission(r11)
            java.lang.String r6 = android.app.AppOpsManager.permissionToOp(r11)
            if (r5 != 0) goto L_0x025c
            if (r6 == 0) goto L_0x0258
            android.content.Context r7 = r1.mContext
            java.lang.Class<android.app.AppOpsManager> r8 = android.app.AppOpsManager.class
            java.lang.Object r7 = r7.getSystemService(r8)
            android.app.AppOpsManager r7 = (android.app.AppOpsManager) r7
            r8 = 0
            r7.setUidMode(r6, r4, r8)
            r28 = r0
            goto L_0x0277
        L_0x0258:
            r8 = 0
            r28 = r0
            goto L_0x0277
        L_0x025c:
            r8 = 0
            java.lang.String r7 = r2.packageName
            int r7 = r0.checkPermission(r5, r7)
            if (r7 != 0) goto L_0x0267
            r7 = 0
            goto L_0x0268
        L_0x0267:
            r7 = 4
        L_0x0268:
            android.content.Context r8 = r1.mContext
            r28 = r0
            java.lang.Class<android.app.AppOpsManager> r0 = android.app.AppOpsManager.class
            java.lang.Object r0 = r8.getSystemService(r0)
            android.app.AppOpsManager r0 = (android.app.AppOpsManager) r0
            r0.setUidMode(r6, r4, r7)
        L_0x0277:
            r0 = r22 & 32
            if (r0 == 0) goto L_0x0293
            r0 = r22 & 16
            if (r0 == 0) goto L_0x0293
            if (r34 != 0) goto L_0x0293
            android.content.Context r0 = r1.mContext
            android.content.pm.PackageManager r4 = r0.getPackageManager()
            java.lang.String r6 = r2.packageName
            r7 = 16
            r8 = 0
            r5 = r11
            r0 = 0
            r9 = r10
            r4.updatePermissionFlags(r5, r6, r7, r8, r9)
            goto L_0x02a5
        L_0x0293:
            r0 = 0
            goto L_0x02a5
        L_0x0295:
            r28 = r0
            r19 = r5
            r20 = r6
            r26 = r7
            r23 = r8
            r24 = r9
            r25 = r11
            r0 = 0
            r11 = r4
        L_0x02a5:
            int r3 = r3 + 1
            r5 = r19
            r6 = r20
            r8 = r23
            r9 = r24
            r11 = r25
            r7 = r26
            r0 = r28
            goto L_0x0101
        L_0x02b7:
            return
        L_0x02b8:
            r0 = move-exception
            r25 = r11
            java.lang.IllegalStateException r3 = new java.lang.IllegalStateException
            r3.<init>(r0)
            throw r3
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.pm.permission.DefaultPermissionGrantPolicy.grantRuntimePermissions(android.content.pm.PackageInfo, java.util.Set, boolean, boolean, boolean, int):void");
    }

    static /* synthetic */ String[] lambda$grantRuntimePermissions$0(int x$0) {
        return new String[x$0];
    }

    private PackageInfo getSystemPackageInfo(String pkg) {
        return getPackageInfo(pkg, DumpState.DUMP_DEXOPT);
    }

    private PackageInfo getPackageInfo(String pkg) {
        return getPackageInfo(pkg, 0);
    }

    private PackageInfo getPackageInfo(String pkg, int extraFlags) {
        if (pkg == null) {
            return null;
        }
        try {
            return this.mContext.getPackageManager().getPackageInfo(pkg, DEFAULT_PACKAGE_INFO_QUERY_FLAGS | extraFlags);
        } catch (PackageManager.NameNotFoundException e) {
            Slog.e(TAG, "PackageNot found: " + pkg, e);
            return null;
        }
    }

    private boolean isSysComponentOrPersistentPlatformSignedPrivApp(PackageInfo pkg) {
        if (UserHandle.getAppId(pkg.applicationInfo.uid) < 10000) {
            return true;
        }
        if (!pkg.applicationInfo.isPrivilegedApp()) {
            return false;
        }
        PackageInfo disabledPkg = getSystemPackageInfo(this.mServiceInternal.getDisabledSystemPackageName(pkg.applicationInfo.packageName));
        if (disabledPkg != null) {
            ApplicationInfo disabledPackageAppInfo = disabledPkg.applicationInfo;
            if (disabledPackageAppInfo != null && (disabledPackageAppInfo.flags & 8) == 0) {
                return false;
            }
        } else if ((pkg.applicationInfo.flags & 8) == 0) {
            return false;
        }
        return this.mServiceInternal.isPlatformSigned(pkg.packageName);
    }

    private void grantDefaultPermissionExceptions(int userId) {
        Set<String> permissions;
        this.mHandler.removeMessages(1);
        synchronized (this.mLock) {
            if (this.mGrantExceptions == null) {
                this.mGrantExceptions = readDefaultPermissionExceptionsLocked();
            }
        }
        int exceptionCount = this.mGrantExceptions.size();
        Set<String> permissions2 = null;
        for (int i = 0; i < exceptionCount; i++) {
            PackageInfo pkg = getSystemPackageInfo(this.mGrantExceptions.keyAt(i));
            List<DefaultPermissionGrant> permissionGrants = this.mGrantExceptions.valueAt(i);
            int permissionGrantCount = permissionGrants.size();
            for (int j = 0; j < permissionGrantCount; j++) {
                DefaultPermissionGrant permissionGrant = permissionGrants.get(j);
                if (!isPermissionDangerous(permissionGrant.name)) {
                    Log.w(TAG, "Ignoring permission " + permissionGrant.name + " which isn't dangerous");
                } else {
                    if (permissions2 == null) {
                        permissions = new ArraySet<>();
                    } else {
                        permissions2.clear();
                        permissions = permissions2;
                    }
                    permissions.add(permissionGrant.name);
                    grantRuntimePermissions(pkg, permissions, permissionGrant.fixed, permissionGrant.whitelisted, true, userId);
                    permissions2 = permissions;
                }
            }
        }
    }

    private File[] getDefaultPermissionFiles() {
        ArrayList<File> ret = new ArrayList<>();
        File dir = new File(Environment.getRootDirectory(), "etc/default-permissions");
        if (dir.isDirectory() && dir.canRead()) {
            Collections.addAll(ret, dir.listFiles());
        }
        File dir2 = new File(Environment.getVendorDirectory(), "etc/default-permissions");
        if (dir2.isDirectory() && dir2.canRead()) {
            Collections.addAll(ret, dir2.listFiles());
        }
        File dir3 = new File(Environment.getOdmDirectory(), "etc/default-permissions");
        if (dir3.isDirectory() && dir3.canRead()) {
            Collections.addAll(ret, dir3.listFiles());
        }
        File dir4 = new File(Environment.getProductDirectory(), "etc/default-permissions");
        if (dir4.isDirectory() && dir4.canRead()) {
            Collections.addAll(ret, dir4.listFiles());
        }
        File dir5 = new File(Environment.getProductServicesDirectory(), "etc/default-permissions");
        if (dir5.isDirectory() && dir5.canRead()) {
            Collections.addAll(ret, dir5.listFiles());
        }
        if (this.mContext.getPackageManager().hasSystemFeature("android.hardware.type.embedded", 0)) {
            File dir6 = new File(Environment.getOemDirectory(), "etc/default-permissions");
            if (dir6.isDirectory() && dir6.canRead()) {
                Collections.addAll(ret, dir6.listFiles());
            }
        }
        if (ret.isEmpty()) {
            return null;
        }
        return (File[]) ret.toArray(new File[0]);
    }

    /* Debug info: failed to restart local var, previous not found, register: 10 */
    /* access modifiers changed from: private */
    /* JADX WARNING: Code restructure failed: missing block: B:21:0x0087, code lost:
        r8 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:23:?, code lost:
        r5.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:27:0x0090, code lost:
        throw r8;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public android.util.ArrayMap<java.lang.String, java.util.List<com.android.server.pm.permission.DefaultPermissionGrantPolicy.DefaultPermissionGrant>> readDefaultPermissionExceptionsLocked() {
        /*
            r10 = this;
            java.io.File[] r0 = r10.getDefaultPermissionFiles()
            r1 = 0
            if (r0 != 0) goto L_0x000d
            android.util.ArrayMap r2 = new android.util.ArrayMap
            r2.<init>(r1)
            return r2
        L_0x000d:
            android.util.ArrayMap r2 = new android.util.ArrayMap
            r2.<init>()
            int r3 = r0.length
        L_0x0013:
            if (r1 >= r3) goto L_0x00aa
            r4 = r0[r1]
            java.lang.String r5 = r4.getPath()
            java.lang.String r6 = ".xml"
            boolean r5 = r5.endsWith(r6)
            java.lang.String r6 = "DefaultPermGrantPolicy"
            if (r5 != 0) goto L_0x004b
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>()
            java.lang.String r7 = "Non-xml file "
            r5.append(r7)
            r5.append(r4)
            java.lang.String r7 = " in "
            r5.append(r7)
            java.lang.String r7 = r4.getParent()
            r5.append(r7)
            java.lang.String r7 = " directory, ignoring"
            r5.append(r7)
            java.lang.String r5 = r5.toString()
            android.util.Slog.i(r6, r5)
            goto L_0x00a6
        L_0x004b:
            boolean r5 = r4.canRead()
            if (r5 != 0) goto L_0x006b
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>()
            java.lang.String r7 = "Default permissions file "
            r5.append(r7)
            r5.append(r4)
            java.lang.String r7 = " cannot be read"
            r5.append(r7)
            java.lang.String r5 = r5.toString()
            android.util.Slog.w(r6, r5)
            goto L_0x00a6
        L_0x006b:
            java.io.BufferedInputStream r5 = new java.io.BufferedInputStream     // Catch:{ IOException | XmlPullParserException -> 0x0091 }
            java.io.FileInputStream r7 = new java.io.FileInputStream     // Catch:{ IOException | XmlPullParserException -> 0x0091 }
            r7.<init>(r4)     // Catch:{ IOException | XmlPullParserException -> 0x0091 }
            r5.<init>(r7)     // Catch:{ IOException | XmlPullParserException -> 0x0091 }
            org.xmlpull.v1.XmlPullParser r7 = android.util.Xml.newPullParser()     // Catch:{ all -> 0x0085 }
            r8 = 0
            r7.setInput(r5, r8)     // Catch:{ all -> 0x0085 }
            r10.parse(r7, r2)     // Catch:{ all -> 0x0085 }
            r5.close()     // Catch:{ IOException | XmlPullParserException -> 0x0091 }
            goto L_0x00a6
        L_0x0085:
            r7 = move-exception
            throw r7     // Catch:{ all -> 0x0087 }
        L_0x0087:
            r8 = move-exception
            r5.close()     // Catch:{ all -> 0x008c }
            goto L_0x0090
        L_0x008c:
            r9 = move-exception
            r7.addSuppressed(r9)     // Catch:{ IOException | XmlPullParserException -> 0x0091 }
        L_0x0090:
            throw r8     // Catch:{ IOException | XmlPullParserException -> 0x0091 }
        L_0x0091:
            r5 = move-exception
            java.lang.StringBuilder r7 = new java.lang.StringBuilder
            r7.<init>()
            java.lang.String r8 = "Error reading default permissions file "
            r7.append(r8)
            r7.append(r4)
            java.lang.String r7 = r7.toString()
            android.util.Slog.w(r6, r7, r5)
        L_0x00a6:
            int r1 = r1 + 1
            goto L_0x0013
        L_0x00aa:
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.pm.permission.DefaultPermissionGrantPolicy.readDefaultPermissionExceptionsLocked():android.util.ArrayMap");
    }

    private void parse(XmlPullParser parser, Map<String, List<DefaultPermissionGrant>> outGrantExceptions) throws IOException, XmlPullParserException {
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
                if (TAG_EXCEPTIONS.equals(parser.getName())) {
                    parseExceptions(parser, outGrantExceptions);
                } else {
                    Log.e(TAG, "Unknown tag " + parser.getName());
                }
            }
        }
    }

    private void parseExceptions(XmlPullParser parser, Map<String, List<DefaultPermissionGrant>> outGrantExceptions) throws IOException, XmlPullParserException {
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
                if (TAG_EXCEPTION.equals(parser.getName())) {
                    String packageName = parser.getAttributeValue((String) null, "package");
                    List<DefaultPermissionGrant> packageExceptions = outGrantExceptions.get(packageName);
                    if (packageExceptions == null) {
                        PackageInfo packageInfo = getSystemPackageInfo(packageName);
                        if (packageInfo == null) {
                            Log.w(TAG, "No such package:" + packageName);
                            XmlUtils.skipCurrentTag(parser);
                        } else if (!isSystemPackage(packageInfo)) {
                            Log.w(TAG, "Unknown system package:" + packageName);
                            XmlUtils.skipCurrentTag(parser);
                        } else if (!doesPackageSupportRuntimePermissions(packageInfo)) {
                            Log.w(TAG, "Skipping non supporting runtime permissions package:" + packageName);
                            XmlUtils.skipCurrentTag(parser);
                        } else {
                            packageExceptions = new ArrayList<>();
                            outGrantExceptions.put(packageName, packageExceptions);
                        }
                    }
                    parsePermission(parser, packageExceptions);
                } else {
                    Log.e(TAG, "Unknown tag " + parser.getName() + "under <exceptions>");
                }
            }
        }
    }

    private void parsePermission(XmlPullParser parser, List<DefaultPermissionGrant> outPackageExceptions) throws IOException, XmlPullParserException {
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
                if (TAG_PERMISSION.contains(parser.getName())) {
                    String name = parser.getAttributeValue((String) null, "name");
                    if (name == null) {
                        Log.w(TAG, "Mandatory name attribute missing for permission tag");
                        XmlUtils.skipCurrentTag(parser);
                    } else {
                        outPackageExceptions.add(new DefaultPermissionGrant(name, XmlUtils.readBooleanAttribute(parser, ATTR_FIXED), XmlUtils.readBooleanAttribute(parser, ATTR_WHITELISTED)));
                    }
                } else {
                    Log.e(TAG, "Unknown tag " + parser.getName() + "under <exception>");
                }
            }
        }
    }

    private static boolean doesPackageSupportRuntimePermissions(PackageInfo pkg) {
        return pkg.applicationInfo != null && pkg.applicationInfo.targetSdkVersion > 22;
    }

    private boolean isPermissionRestricted(String name) {
        try {
            return this.mContext.getPackageManager().getPermissionInfo(name, 0).isRestricted();
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    private boolean isPermissionDangerous(String name) {
        try {
            if (this.mContext.getPackageManager().getPermissionInfo(name, 0).getProtection() == 1) {
                return true;
            }
            return false;
        } catch (PackageManager.NameNotFoundException e) {
            return true;
        }
    }

    private static final class DefaultPermissionGrant {
        final boolean fixed;
        final String name;
        final boolean whitelisted;

        public DefaultPermissionGrant(String name2, boolean fixed2, boolean whitelisted2) {
            this.name = name2;
            this.fixed = fixed2;
            this.whitelisted = whitelisted2;
        }
    }
}
