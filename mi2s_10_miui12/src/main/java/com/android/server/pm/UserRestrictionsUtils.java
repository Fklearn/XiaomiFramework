package com.android.server.pm;

import android.app.AppGlobals;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.RemoteException;
import android.os.UserHandle;
import android.os.UserManager;
import android.provider.Settings;
import android.util.Log;
import android.util.Slog;
import android.util.SparseArray;
import com.android.internal.util.Preconditions;
import com.google.android.collect.Sets;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Set;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

public class UserRestrictionsUtils {
    private static final Set<String> DEFAULT_ENABLED_FOR_DEVICE_OWNERS = Sets.newArraySet(new String[]{"no_add_managed_profile"});
    private static final Set<String> DEFAULT_ENABLED_FOR_MANAGED_PROFILES = Sets.newArraySet(new String[]{"no_bluetooth_sharing"});
    private static final Set<String> DEVICE_OWNER_ONLY_RESTRICTIONS = Sets.newArraySet(new String[]{"no_user_switch", "disallow_config_private_dns"});
    private static final Set<String> GLOBAL_RESTRICTIONS = Sets.newArraySet(new String[]{"no_adjust_volume", "no_bluetooth_sharing", "no_config_date_time", "no_system_error_dialogs", "no_run_in_background", "no_unmute_microphone", "disallow_unmute_device"});
    private static final Set<String> IMMUTABLE_BY_OWNERS = Sets.newArraySet(new String[]{"no_record_audio", "no_wallpaper", "no_oem_unlock"});
    private static final Set<String> NON_PERSIST_USER_RESTRICTIONS = Sets.newArraySet(new String[]{"no_record_audio"});
    private static final Set<String> PRIMARY_USER_ONLY_RESTRICTIONS = Sets.newArraySet(new String[]{"no_bluetooth", "no_usb_file_transfer", "no_config_tethering", "no_network_reset", "no_factory_reset", "no_add_user", "no_config_cell_broadcasts", "no_config_mobile_networks", "no_physical_media", "no_sms", "no_fun", "no_safe_boot", "no_create_windows", "no_data_roaming", "no_airplane_mode"});
    private static final Set<String> PROFILE_GLOBAL_RESTRICTIONS = Sets.newArraySet(new String[]{"ensure_verify_apps", "no_airplane_mode", "no_install_unknown_sources_globally"});
    private static final String TAG = "UserRestrictionsUtils";
    public static final Set<String> USER_RESTRICTIONS = newSetWithUniqueCheck(new String[]{"no_config_wifi", "no_config_locale", "no_modify_accounts", "no_install_apps", "no_uninstall_apps", "no_share_location", "no_install_unknown_sources", "no_install_unknown_sources_globally", "no_config_bluetooth", "no_bluetooth", "no_bluetooth_sharing", "no_usb_file_transfer", "no_config_credentials", "no_remove_user", "no_remove_managed_profile", "no_debugging_features", "no_config_vpn", "no_config_date_time", "no_config_tethering", "no_network_reset", "no_factory_reset", "no_add_user", "no_add_managed_profile", "ensure_verify_apps", "no_config_cell_broadcasts", "no_config_mobile_networks", "no_control_apps", "no_physical_media", "no_unmute_microphone", "no_adjust_volume", "no_outgoing_calls", "no_sms", "no_fun", "no_create_windows", "no_system_error_dialogs", "no_cross_profile_copy_paste", "no_outgoing_beam", "no_wallpaper", "no_safe_boot", "allow_parent_profile_app_linking", "no_record_audio", "no_camera", "no_run_in_background", "no_data_roaming", "no_set_user_icon", "no_set_wallpaper", "no_oem_unlock", "disallow_unmute_device", "no_autofill", "no_content_capture", "no_content_suggestions", "no_user_switch", "no_unified_password", "no_config_location", "no_airplane_mode", "no_config_brightness", "no_sharing_into_profile", "no_ambient_display", "no_config_screen_timeout", "no_printing", "disallow_config_private_dns"});

    private UserRestrictionsUtils() {
    }

    private static Set<String> newSetWithUniqueCheck(String[] strings) {
        Set<String> ret = Sets.newArraySet(strings);
        Preconditions.checkState(ret.size() == strings.length);
        return ret;
    }

    public static boolean isValidRestriction(String restriction) {
        if (USER_RESTRICTIONS.contains(restriction)) {
            return true;
        }
        int uid = Binder.getCallingUid();
        String[] pkgs = null;
        try {
            pkgs = AppGlobals.getPackageManager().getPackagesForUid(uid);
        } catch (RemoteException e) {
        }
        StringBuilder msg = new StringBuilder("Unknown restriction queried by uid ");
        msg.append(uid);
        if (pkgs != null && pkgs.length > 0) {
            msg.append(" (");
            msg.append(pkgs[0]);
            if (pkgs.length > 1) {
                msg.append(" et al");
            }
            msg.append(")");
        }
        msg.append(": ");
        msg.append(restriction);
        if (restriction == null || !isSystemApp(uid, pkgs)) {
            Slog.e(TAG, msg.toString());
        } else {
            Slog.wtf(TAG, msg.toString());
        }
        return false;
    }

    private static boolean isSystemApp(int uid, String[] packageList) {
        if (UserHandle.isCore(uid)) {
            return true;
        }
        if (packageList == null) {
            return false;
        }
        IPackageManager pm = AppGlobals.getPackageManager();
        for (int i = 0; i < packageList.length; i++) {
            try {
                ApplicationInfo appInfo = pm.getApplicationInfo(packageList[i], 794624, UserHandle.getUserId(uid));
                if (appInfo != null && appInfo.isSystemApp()) {
                    return true;
                }
            } catch (RemoteException e) {
            }
        }
        return false;
    }

    public static void writeRestrictions(XmlSerializer serializer, Bundle restrictions, String tag) throws IOException {
        if (restrictions != null) {
            serializer.startTag((String) null, tag);
            for (String key : restrictions.keySet()) {
                if (!NON_PERSIST_USER_RESTRICTIONS.contains(key)) {
                    if (!USER_RESTRICTIONS.contains(key)) {
                        Log.w(TAG, "Unknown user restriction detected: " + key);
                    } else if (restrictions.getBoolean(key)) {
                        serializer.attribute((String) null, key, "true");
                    }
                }
            }
            serializer.endTag((String) null, tag);
        }
    }

    public static void readRestrictions(XmlPullParser parser, Bundle restrictions) {
        restrictions.clear();
        for (String key : USER_RESTRICTIONS) {
            String value = parser.getAttributeValue((String) null, key);
            if (value != null) {
                restrictions.putBoolean(key, Boolean.parseBoolean(value));
            }
        }
    }

    public static Bundle readRestrictions(XmlPullParser parser) {
        Bundle result = new Bundle();
        readRestrictions(parser, result);
        return result;
    }

    public static Bundle nonNull(Bundle in) {
        return in != null ? in : new Bundle();
    }

    public static boolean isEmpty(Bundle in) {
        return in == null || in.size() == 0;
    }

    public static boolean contains(Bundle in, String restriction) {
        return in != null && in.getBoolean(restriction);
    }

    public static Bundle clone(Bundle in) {
        Bundle bundle;
        if (in == null) {
            bundle = new Bundle();
        }
        return bundle;
    }

    public static void merge(Bundle dest, Bundle in) {
        Preconditions.checkNotNull(dest);
        Preconditions.checkArgument(dest != in);
        if (in != null) {
            for (String key : in.keySet()) {
                if (in.getBoolean(key, false)) {
                    dest.putBoolean(key, true);
                }
            }
        }
    }

    public static Bundle mergeAll(SparseArray<Bundle> restrictions) {
        if (restrictions.size() == 0) {
            return null;
        }
        Bundle result = new Bundle();
        for (int i = 0; i < restrictions.size(); i++) {
            merge(result, restrictions.valueAt(i));
        }
        return result;
    }

    public static boolean canDeviceOwnerChange(String restriction) {
        return !IMMUTABLE_BY_OWNERS.contains(restriction);
    }

    public static boolean canProfileOwnerChange(String restriction, int userId) {
        return !IMMUTABLE_BY_OWNERS.contains(restriction) && !DEVICE_OWNER_ONLY_RESTRICTIONS.contains(restriction) && (userId == 0 || !PRIMARY_USER_ONLY_RESTRICTIONS.contains(restriction));
    }

    public static Set<String> getDefaultEnabledForDeviceOwner() {
        return DEFAULT_ENABLED_FOR_DEVICE_OWNERS;
    }

    public static Set<String> getDefaultEnabledForManagedProfiles() {
        return DEFAULT_ENABLED_FOR_MANAGED_PROFILES;
    }

    public static void sortToGlobalAndLocal(Bundle in, boolean isDeviceOwner, int cameraRestrictionScope, Bundle global, Bundle local) {
        if (cameraRestrictionScope == 2) {
            global.putBoolean("no_camera", true);
        } else if (cameraRestrictionScope == 1) {
            local.putBoolean("no_camera", true);
        }
        if (in != null && in.size() != 0) {
            for (String key : in.keySet()) {
                if (in.getBoolean(key)) {
                    if (isGlobal(isDeviceOwner, key)) {
                        global.putBoolean(key, true);
                    } else {
                        local.putBoolean(key, true);
                    }
                }
            }
        }
    }

    private static boolean isGlobal(boolean isDeviceOwner, String key) {
        return (isDeviceOwner && (PRIMARY_USER_ONLY_RESTRICTIONS.contains(key) || GLOBAL_RESTRICTIONS.contains(key))) || PROFILE_GLOBAL_RESTRICTIONS.contains(key) || DEVICE_OWNER_ONLY_RESTRICTIONS.contains(key);
    }

    public static boolean areEqual(Bundle a, Bundle b) {
        if (a == b) {
            return true;
        }
        if (isEmpty(a)) {
            return isEmpty(b);
        }
        if (isEmpty(b)) {
            return false;
        }
        for (String key : a.keySet()) {
            if (a.getBoolean(key) != b.getBoolean(key)) {
                return false;
            }
        }
        for (String key2 : b.keySet()) {
            if (a.getBoolean(key2) != b.getBoolean(key2)) {
                return false;
            }
        }
        return true;
    }

    public static void applyUserRestrictions(Context context, int userId, Bundle newRestrictions, Bundle prevRestrictions) {
        for (String key : USER_RESTRICTIONS) {
            boolean newValue = newRestrictions.getBoolean(key);
            if (newValue != prevRestrictions.getBoolean(key)) {
                applyUserRestriction(context, userId, key, newValue);
            }
        }
    }

    /* JADX WARNING: Can't fix incorrect switch cases order */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static void applyUserRestriction(android.content.Context r11, int r12, java.lang.String r13, boolean r14) {
        /*
            java.lang.String r0 = "1"
            java.lang.String r1 = "airplane_mode_on"
            android.content.ContentResolver r2 = r11.getContentResolver()
            long r3 = android.os.Binder.clearCallingIdentity()
            r5 = -1
            int r6 = r13.hashCode()     // Catch:{ all -> 0x01ba }
            java.lang.String r7 = "no_install_unknown_sources"
            java.lang.String r8 = "no_install_unknown_sources_globally"
            r9 = 1
            r10 = 0
            switch(r6) {
                case -1475388515: goto L_0x0088;
                case -1315771401: goto L_0x007e;
                case -1145953970: goto L_0x0076;
                case -1082175374: goto L_0x006a;
                case 387189153: goto L_0x0062;
                case 721128150: goto L_0x0057;
                case 866097556: goto L_0x004b;
                case 928851522: goto L_0x0040;
                case 995816019: goto L_0x0035;
                case 1095593830: goto L_0x002a;
                case 1760762284: goto L_0x001e;
                default: goto L_0x001c;
            }
        L_0x001c:
            goto L_0x0093
        L_0x001e:
            java.lang.String r6 = "no_debugging_features"
            boolean r6 = r13.equals(r6)     // Catch:{ all -> 0x01ba }
            if (r6 == 0) goto L_0x001c
            r5 = 2
            goto L_0x0093
        L_0x002a:
            java.lang.String r6 = "no_safe_boot"
            boolean r6 = r13.equals(r6)     // Catch:{ all -> 0x01ba }
            if (r6 == 0) goto L_0x001c
            r5 = 7
            goto L_0x0093
        L_0x0035:
            java.lang.String r6 = "no_share_location"
            boolean r6 = r13.equals(r6)     // Catch:{ all -> 0x01ba }
            if (r6 == 0) goto L_0x001c
            r5 = r9
            goto L_0x0093
        L_0x0040:
            java.lang.String r6 = "no_data_roaming"
            boolean r6 = r13.equals(r6)     // Catch:{ all -> 0x01ba }
            if (r6 == 0) goto L_0x001c
            r5 = r10
            goto L_0x0093
        L_0x004b:
            java.lang.String r6 = "no_config_location"
            boolean r6 = r13.equals(r6)     // Catch:{ all -> 0x01ba }
            if (r6 == 0) goto L_0x001c
            r5 = 10
            goto L_0x0093
        L_0x0057:
            java.lang.String r6 = "no_run_in_background"
            boolean r6 = r13.equals(r6)     // Catch:{ all -> 0x01ba }
            if (r6 == 0) goto L_0x001c
            r5 = 6
            goto L_0x0093
        L_0x0062:
            boolean r6 = r13.equals(r7)     // Catch:{ all -> 0x01ba }
            if (r6 == 0) goto L_0x001c
            r5 = 5
            goto L_0x0093
        L_0x006a:
            java.lang.String r6 = "no_airplane_mode"
            boolean r6 = r13.equals(r6)     // Catch:{ all -> 0x01ba }
            if (r6 == 0) goto L_0x001c
            r5 = 8
            goto L_0x0093
        L_0x0076:
            boolean r6 = r13.equals(r8)     // Catch:{ all -> 0x01ba }
            if (r6 == 0) goto L_0x001c
            r5 = 4
            goto L_0x0093
        L_0x007e:
            java.lang.String r6 = "ensure_verify_apps"
            boolean r6 = r13.equals(r6)     // Catch:{ all -> 0x01ba }
            if (r6 == 0) goto L_0x001c
            r5 = 3
            goto L_0x0093
        L_0x0088:
            java.lang.String r6 = "no_ambient_display"
            boolean r6 = r13.equals(r6)     // Catch:{ all -> 0x01ba }
            if (r6 == 0) goto L_0x001c
            r5 = 9
        L_0x0093:
            java.lang.String r6 = "0"
            switch(r5) {
                case 0: goto L_0x0176;
                case 1: goto L_0x016d;
                case 2: goto L_0x0163;
                case 3: goto L_0x014a;
                case 4: goto L_0x0142;
                case 5: goto L_0x0139;
                case 6: goto L_0x011e;
                case 7: goto L_0x010d;
                case 8: goto L_0x00df;
                case 9: goto L_0x00a9;
                case 10: goto L_0x009a;
                default: goto L_0x0098;
            }
        L_0x0098:
            goto L_0x01b5
        L_0x009a:
            if (r14 == 0) goto L_0x01b5
            android.content.ContentResolver r0 = r11.getContentResolver()     // Catch:{ all -> 0x01ba }
            java.lang.String r1 = "location_global_kill_switch"
            android.provider.Settings.Global.putString(r0, r1, r6)     // Catch:{ all -> 0x01ba }
            goto L_0x01b5
        L_0x00a9:
            if (r14 == 0) goto L_0x01b5
            android.content.ContentResolver r0 = r11.getContentResolver()     // Catch:{ all -> 0x01ba }
            java.lang.String r1 = "doze_enabled"
            android.provider.Settings.Secure.putIntForUser(r0, r1, r10, r12)     // Catch:{ all -> 0x01ba }
            android.content.ContentResolver r0 = r11.getContentResolver()     // Catch:{ all -> 0x01ba }
            java.lang.String r1 = "doze_always_on"
            android.provider.Settings.Secure.putIntForUser(r0, r1, r10, r12)     // Catch:{ all -> 0x01ba }
            android.content.ContentResolver r0 = r11.getContentResolver()     // Catch:{ all -> 0x01ba }
            java.lang.String r1 = "doze_pulse_on_pick_up"
            android.provider.Settings.Secure.putIntForUser(r0, r1, r10, r12)     // Catch:{ all -> 0x01ba }
            android.content.ContentResolver r0 = r11.getContentResolver()     // Catch:{ all -> 0x01ba }
            java.lang.String r1 = "doze_pulse_on_long_press"
            android.provider.Settings.Secure.putIntForUser(r0, r1, r10, r12)     // Catch:{ all -> 0x01ba }
            android.content.ContentResolver r0 = r11.getContentResolver()     // Catch:{ all -> 0x01ba }
            java.lang.String r1 = "doze_pulse_on_double_tap"
            android.provider.Settings.Secure.putIntForUser(r0, r1, r10, r12)     // Catch:{ all -> 0x01ba }
            goto L_0x01b5
        L_0x00df:
            if (r14 == 0) goto L_0x01b5
            android.content.ContentResolver r0 = r11.getContentResolver()     // Catch:{ all -> 0x01ba }
            int r0 = android.provider.Settings.Global.getInt(r0, r1, r10)     // Catch:{ all -> 0x01ba }
            if (r0 != r9) goto L_0x00ed
            goto L_0x00ee
        L_0x00ed:
            r9 = r10
        L_0x00ee:
            r0 = r9
            if (r0 == 0) goto L_0x010b
            android.content.ContentResolver r5 = r11.getContentResolver()     // Catch:{ all -> 0x01ba }
            android.provider.Settings.Global.putInt(r5, r1, r10)     // Catch:{ all -> 0x01ba }
            android.content.Intent r1 = new android.content.Intent     // Catch:{ all -> 0x01ba }
            java.lang.String r5 = "android.intent.action.AIRPLANE_MODE"
            r1.<init>(r5)     // Catch:{ all -> 0x01ba }
            java.lang.String r5 = "state"
            r1.putExtra(r5, r10)     // Catch:{ all -> 0x01ba }
            android.os.UserHandle r5 = android.os.UserHandle.ALL     // Catch:{ all -> 0x01ba }
            r11.sendBroadcastAsUser(r1, r5)     // Catch:{ all -> 0x01ba }
        L_0x010b:
            goto L_0x01b5
        L_0x010d:
            android.content.ContentResolver r0 = r11.getContentResolver()     // Catch:{ all -> 0x01ba }
            java.lang.String r1 = "safe_boot_disallowed"
            if (r14 == 0) goto L_0x0118
            goto L_0x0119
        L_0x0118:
            r9 = r10
        L_0x0119:
            android.provider.Settings.Global.putInt(r0, r1, r9)     // Catch:{ all -> 0x01ba }
            goto L_0x01b5
        L_0x011e:
            if (r14 == 0) goto L_0x01b5
            int r0 = android.app.ActivityManager.getCurrentUser()     // Catch:{ all -> 0x01ba }
            if (r0 == r12) goto L_0x0137
            if (r12 == 0) goto L_0x0137
            android.app.IActivityManager r1 = android.app.ActivityManager.getService()     // Catch:{ RemoteException -> 0x0131 }
            r5 = 0
            r1.stopUser(r12, r10, r5)     // Catch:{ RemoteException -> 0x0131 }
            goto L_0x0137
        L_0x0131:
            r1 = move-exception
            java.lang.RuntimeException r5 = r1.rethrowAsRuntimeException()     // Catch:{ all -> 0x01ba }
            throw r5     // Catch:{ all -> 0x01ba }
        L_0x0137:
            goto L_0x01b5
        L_0x0139:
            int r0 = getNewUserRestrictionSetting(r11, r12, r8, r14)     // Catch:{ all -> 0x01ba }
            setInstallMarketAppsRestriction(r2, r12, r0)     // Catch:{ all -> 0x01ba }
            goto L_0x01b5
        L_0x0142:
            int r0 = getNewUserRestrictionSetting(r11, r12, r7, r14)     // Catch:{ all -> 0x01ba }
            setInstallMarketAppsRestriction(r2, r12, r0)     // Catch:{ all -> 0x01ba }
            goto L_0x01b5
        L_0x014a:
            if (r14 == 0) goto L_0x01b5
            android.content.ContentResolver r1 = r11.getContentResolver()     // Catch:{ all -> 0x01ba }
            java.lang.String r5 = "package_verifier_enable"
            android.provider.Settings.Global.putStringForUser(r1, r5, r0, r12)     // Catch:{ all -> 0x01ba }
            android.content.ContentResolver r1 = r11.getContentResolver()     // Catch:{ all -> 0x01ba }
            java.lang.String r5 = "verifier_verify_adb_installs"
            android.provider.Settings.Global.putStringForUser(r1, r5, r0, r12)     // Catch:{ all -> 0x01ba }
            goto L_0x01b5
        L_0x0163:
            if (r14 == 0) goto L_0x01b5
            if (r12 != 0) goto L_0x01b5
            java.lang.String r0 = "adb_enabled"
            android.provider.Settings.Global.putStringForUser(r2, r0, r6, r12)     // Catch:{ all -> 0x01ba }
            goto L_0x01b5
        L_0x016d:
            if (r14 == 0) goto L_0x01b5
            java.lang.String r0 = "location_mode"
            android.provider.Settings.Secure.putIntForUser(r2, r0, r10, r12)     // Catch:{ all -> 0x01ba }
            goto L_0x01b5
        L_0x0176:
            if (r14 == 0) goto L_0x01b5
            java.lang.Class<android.telephony.SubscriptionManager> r0 = android.telephony.SubscriptionManager.class
            java.lang.Object r0 = r11.getSystemService(r0)     // Catch:{ all -> 0x01ba }
            android.telephony.SubscriptionManager r0 = (android.telephony.SubscriptionManager) r0     // Catch:{ all -> 0x01ba }
            java.util.List r1 = r0.getActiveSubscriptionInfoList()     // Catch:{ all -> 0x01ba }
            java.lang.String r5 = "data_roaming"
            if (r1 == 0) goto L_0x01b1
            java.util.Iterator r7 = r1.iterator()     // Catch:{ all -> 0x01ba }
        L_0x018d:
            boolean r8 = r7.hasNext()     // Catch:{ all -> 0x01ba }
            if (r8 == 0) goto L_0x01b1
            java.lang.Object r8 = r7.next()     // Catch:{ all -> 0x01ba }
            android.telephony.SubscriptionInfo r8 = (android.telephony.SubscriptionInfo) r8     // Catch:{ all -> 0x01ba }
            java.lang.StringBuilder r9 = new java.lang.StringBuilder     // Catch:{ all -> 0x01ba }
            r9.<init>()     // Catch:{ all -> 0x01ba }
            r9.append(r5)     // Catch:{ all -> 0x01ba }
            int r10 = r8.getSubscriptionId()     // Catch:{ all -> 0x01ba }
            r9.append(r10)     // Catch:{ all -> 0x01ba }
            java.lang.String r9 = r9.toString()     // Catch:{ all -> 0x01ba }
            android.provider.Settings.Global.putStringForUser(r2, r9, r6, r12)     // Catch:{ all -> 0x01ba }
            goto L_0x018d
        L_0x01b1:
            android.provider.Settings.Global.putStringForUser(r2, r5, r6, r12)     // Catch:{ all -> 0x01ba }
        L_0x01b5:
            android.os.Binder.restoreCallingIdentity(r3)
            return
        L_0x01ba:
            r0 = move-exception
            android.os.Binder.restoreCallingIdentity(r3)
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.pm.UserRestrictionsUtils.applyUserRestriction(android.content.Context, int, java.lang.String, boolean):void");
    }

    /* JADX WARNING: Can't fix incorrect switch cases order */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static boolean isSettingRestrictedForUser(android.content.Context r9, java.lang.String r10, int r11, java.lang.String r12, int r13) {
        /*
            com.android.internal.util.Preconditions.checkNotNull(r10)
            java.lang.Class<android.os.UserManager> r0 = android.os.UserManager.class
            java.lang.Object r0 = r9.getSystemService(r0)
            android.os.UserManager r0 = (android.os.UserManager) r0
            r1 = 0
            int r2 = r10.hashCode()
            r3 = 1
            r4 = 0
            switch(r2) {
                case -1796809747: goto L_0x0134;
                case -1500478207: goto L_0x0129;
                case -1490222856: goto L_0x011e;
                case -1115710219: goto L_0x0113;
                case -970351711: goto L_0x0109;
                case -693072130: goto L_0x00fd;
                case -623873498: goto L_0x00f3;
                case -416662510: goto L_0x00e8;
                case -101820922: goto L_0x00dd;
                case -32505807: goto L_0x00d1;
                case 58027029: goto L_0x00c4;
                case 258514750: goto L_0x00b7;
                case 683724341: goto L_0x00aa;
                case 720635155: goto L_0x009e;
                case 926123534: goto L_0x0092;
                case 1073289638: goto L_0x0086;
                case 1223734380: goto L_0x0079;
                case 1275530062: goto L_0x006d;
                case 1307734371: goto L_0x0060;
                case 1334097968: goto L_0x0054;
                case 1602982312: goto L_0x0048;
                case 1646894952: goto L_0x003c;
                case 1661297501: goto L_0x0030;
                case 1701140351: goto L_0x0024;
                case 1735689732: goto L_0x0017;
                default: goto L_0x0015;
            }
        L_0x0015:
            goto L_0x013f
        L_0x0017:
            java.lang.String r2 = "screen_brightness"
            boolean r2 = r10.equals(r2)
            if (r2 == 0) goto L_0x0015
            r2 = 18
            goto L_0x0140
        L_0x0024:
            java.lang.String r2 = "install_non_market_apps"
            boolean r2 = r10.equals(r2)
            if (r2 == 0) goto L_0x0015
            r2 = 2
            goto L_0x0140
        L_0x0030:
            java.lang.String r2 = "auto_time"
            boolean r2 = r10.equals(r2)
            if (r2 == 0) goto L_0x0015
            r2 = 20
            goto L_0x0140
        L_0x003c:
            java.lang.String r2 = "always_on_vpn_lockdown"
            boolean r2 = r10.equals(r2)
            if (r2 == 0) goto L_0x0015
            r2 = 8
            goto L_0x0140
        L_0x0048:
            java.lang.String r2 = "doze_pulse_on_pick_up"
            boolean r2 = r10.equals(r2)
            if (r2 == 0) goto L_0x0015
            r2 = 14
            goto L_0x0140
        L_0x0054:
            java.lang.String r2 = "always_on_vpn_lockdown_whitelist"
            boolean r2 = r10.equals(r2)
            if (r2 == 0) goto L_0x0015
            r2 = 9
            goto L_0x0140
        L_0x0060:
            java.lang.String r2 = "location_global_kill_switch"
            boolean r2 = r10.equals(r2)
            if (r2 == 0) goto L_0x0015
            r2 = 17
            goto L_0x0140
        L_0x006d:
            java.lang.String r2 = "auto_time_zone"
            boolean r2 = r10.equals(r2)
            if (r2 == 0) goto L_0x0015
            r2 = 21
            goto L_0x0140
        L_0x0079:
            java.lang.String r2 = "private_dns_specifier"
            boolean r2 = r10.equals(r2)
            if (r2 == 0) goto L_0x0015
            r2 = 24
            goto L_0x0140
        L_0x0086:
            java.lang.String r2 = "doze_pulse_on_double_tap"
            boolean r2 = r10.equals(r2)
            if (r2 == 0) goto L_0x0015
            r2 = 16
            goto L_0x0140
        L_0x0092:
            java.lang.String r2 = "airplane_mode_on"
            boolean r2 = r10.equals(r2)
            if (r2 == 0) goto L_0x0015
            r2 = 11
            goto L_0x0140
        L_0x009e:
            java.lang.String r2 = "package_verifier_enable"
            boolean r2 = r10.equals(r2)
            if (r2 == 0) goto L_0x0015
            r2 = 4
            goto L_0x0140
        L_0x00aa:
            java.lang.String r2 = "private_dns_mode"
            boolean r2 = r10.equals(r2)
            if (r2 == 0) goto L_0x0015
            r2 = 23
            goto L_0x0140
        L_0x00b7:
            java.lang.String r2 = "screen_off_timeout"
            boolean r2 = r10.equals(r2)
            if (r2 == 0) goto L_0x0015
            r2 = 22
            goto L_0x0140
        L_0x00c4:
            java.lang.String r2 = "safe_boot_disallowed"
            boolean r2 = r10.equals(r2)
            if (r2 == 0) goto L_0x0015
            r2 = 10
            goto L_0x0140
        L_0x00d1:
            java.lang.String r2 = "doze_pulse_on_long_press"
            boolean r2 = r10.equals(r2)
            if (r2 == 0) goto L_0x0015
            r2 = 15
            goto L_0x0140
        L_0x00dd:
            java.lang.String r2 = "doze_always_on"
            boolean r2 = r10.equals(r2)
            if (r2 == 0) goto L_0x0015
            r2 = 13
            goto L_0x0140
        L_0x00e8:
            java.lang.String r2 = "preferred_network_mode"
            boolean r2 = r10.equals(r2)
            if (r2 == 0) goto L_0x0015
            r2 = 6
            goto L_0x0140
        L_0x00f3:
            java.lang.String r2 = "always_on_vpn_app"
            boolean r2 = r10.equals(r2)
            if (r2 == 0) goto L_0x0015
            r2 = 7
            goto L_0x0140
        L_0x00fd:
            java.lang.String r2 = "screen_brightness_mode"
            boolean r2 = r10.equals(r2)
            if (r2 == 0) goto L_0x0015
            r2 = 19
            goto L_0x0140
        L_0x0109:
            java.lang.String r2 = "adb_enabled"
            boolean r2 = r10.equals(r2)
            if (r2 == 0) goto L_0x0015
            r2 = 3
            goto L_0x0140
        L_0x0113:
            java.lang.String r2 = "verifier_verify_adb_installs"
            boolean r2 = r10.equals(r2)
            if (r2 == 0) goto L_0x0015
            r2 = 5
            goto L_0x0140
        L_0x011e:
            java.lang.String r2 = "doze_enabled"
            boolean r2 = r10.equals(r2)
            if (r2 == 0) goto L_0x0015
            r2 = 12
            goto L_0x0140
        L_0x0129:
            java.lang.String r2 = "location_providers_allowed"
            boolean r2 = r10.equals(r2)
            if (r2 == 0) goto L_0x0015
            r2 = r3
            goto L_0x0140
        L_0x0134:
            java.lang.String r2 = "location_mode"
            boolean r2 = r10.equals(r2)
            if (r2 == 0) goto L_0x0015
            r2 = r4
            goto L_0x0140
        L_0x013f:
            r2 = -1
        L_0x0140:
            java.lang.String r5 = "1"
            java.lang.String r6 = "no_config_location"
            java.lang.String r7 = "0"
            r8 = 1000(0x3e8, float:1.401E-42)
            switch(r2) {
                case 0: goto L_0x021b;
                case 1: goto L_0x01fe;
                case 2: goto L_0x01f3;
                case 3: goto L_0x01e8;
                case 4: goto L_0x01de;
                case 5: goto L_0x01de;
                case 6: goto L_0x01da;
                case 7: goto L_0x01cb;
                case 8: goto L_0x01cb;
                case 9: goto L_0x01cb;
                case 10: goto L_0x01bf;
                case 11: goto L_0x01b3;
                case 12: goto L_0x01a7;
                case 13: goto L_0x01a7;
                case 14: goto L_0x01a7;
                case 15: goto L_0x01a7;
                case 16: goto L_0x01a7;
                case 17: goto L_0x019a;
                case 18: goto L_0x0192;
                case 19: goto L_0x0192;
                case 20: goto L_0x0172;
                case 21: goto L_0x016a;
                case 22: goto L_0x0162;
                case 23: goto L_0x015b;
                case 24: goto L_0x015b;
                default: goto L_0x014c;
            }
        L_0x014c:
            java.lang.String r2 = "data_roaming"
            boolean r2 = r10.startsWith(r2)
            if (r2 == 0) goto L_0x024c
            boolean r2 = r7.equals(r12)
            if (r2 == 0) goto L_0x0238
            return r4
        L_0x015b:
            if (r13 != r8) goto L_0x015e
            return r4
        L_0x015e:
            java.lang.String r2 = "disallow_config_private_dns"
            goto L_0x023c
        L_0x0162:
            if (r13 != r8) goto L_0x0165
            return r4
        L_0x0165:
            java.lang.String r2 = "no_config_screen_timeout"
            goto L_0x023c
        L_0x016a:
            if (r13 != r8) goto L_0x016d
            return r4
        L_0x016d:
            java.lang.String r2 = "no_config_date_time"
            goto L_0x023c
        L_0x0172:
            java.lang.Class<android.app.admin.DevicePolicyManager> r2 = android.app.admin.DevicePolicyManager.class
            java.lang.Object r2 = r9.getSystemService(r2)
            android.app.admin.DevicePolicyManager r2 = (android.app.admin.DevicePolicyManager) r2
            if (r2 == 0) goto L_0x0189
            boolean r5 = r2.getAutoTimeRequired()
            if (r5 == 0) goto L_0x0189
            boolean r5 = r7.equals(r12)
            if (r5 == 0) goto L_0x0189
            return r3
        L_0x0189:
            if (r13 != r8) goto L_0x018c
            return r4
        L_0x018c:
            java.lang.String r3 = "no_config_date_time"
            r2 = r3
            goto L_0x023c
        L_0x0192:
            if (r13 != r8) goto L_0x0195
            return r4
        L_0x0195:
            java.lang.String r2 = "no_config_brightness"
            goto L_0x023c
        L_0x019a:
            boolean r2 = r7.equals(r12)
            if (r2 == 0) goto L_0x01a1
            return r4
        L_0x01a1:
            java.lang.String r2 = "no_config_location"
            r1 = 1
            goto L_0x023c
        L_0x01a7:
            boolean r2 = r7.equals(r12)
            if (r2 == 0) goto L_0x01ae
            return r4
        L_0x01ae:
            java.lang.String r2 = "no_ambient_display"
            goto L_0x023c
        L_0x01b3:
            boolean r2 = r7.equals(r12)
            if (r2 == 0) goto L_0x01ba
            return r4
        L_0x01ba:
            java.lang.String r2 = "no_airplane_mode"
            goto L_0x023c
        L_0x01bf:
            boolean r2 = r5.equals(r12)
            if (r2 == 0) goto L_0x01c6
            return r4
        L_0x01c6:
            java.lang.String r2 = "no_safe_boot"
            goto L_0x023c
        L_0x01cb:
            int r2 = android.os.UserHandle.getAppId(r13)
            if (r2 == r8) goto L_0x01d9
            if (r2 != 0) goto L_0x01d4
            goto L_0x01d9
        L_0x01d4:
            java.lang.String r3 = "no_config_vpn"
            r2 = r3
            goto L_0x023c
        L_0x01d9:
            return r4
        L_0x01da:
            java.lang.String r2 = "no_config_mobile_networks"
            goto L_0x023c
        L_0x01de:
            boolean r2 = r5.equals(r12)
            if (r2 == 0) goto L_0x01e5
            return r4
        L_0x01e5:
            java.lang.String r2 = "ensure_verify_apps"
            goto L_0x023c
        L_0x01e8:
            boolean r2 = r7.equals(r12)
            if (r2 == 0) goto L_0x01ef
            return r4
        L_0x01ef:
            java.lang.String r2 = "no_debugging_features"
            goto L_0x023c
        L_0x01f3:
            boolean r2 = r7.equals(r12)
            if (r2 == 0) goto L_0x01fa
            return r4
        L_0x01fa:
            java.lang.String r2 = "no_install_unknown_sources"
            goto L_0x023c
        L_0x01fe:
            android.os.UserHandle r2 = android.os.UserHandle.of(r11)
            boolean r2 = r0.hasUserRestriction(r6, r2)
            if (r2 == 0) goto L_0x020c
            if (r13 == r8) goto L_0x020c
            return r3
        L_0x020c:
            if (r12 == 0) goto L_0x0217
            java.lang.String r2 = "-"
            boolean r2 = r12.startsWith(r2)
            if (r2 == 0) goto L_0x0217
            return r4
        L_0x0217:
            java.lang.String r2 = "no_share_location"
            goto L_0x023c
        L_0x021b:
            android.os.UserHandle r2 = android.os.UserHandle.of(r11)
            boolean r2 = r0.hasUserRestriction(r6, r2)
            if (r2 == 0) goto L_0x0229
            if (r13 == r8) goto L_0x0229
            return r3
        L_0x0229:
            java.lang.String r2 = java.lang.String.valueOf(r4)
            boolean r2 = r2.equals(r12)
            if (r2 == 0) goto L_0x0234
            return r4
        L_0x0234:
            java.lang.String r2 = "no_share_location"
            goto L_0x023c
        L_0x0238:
            java.lang.String r2 = "no_data_roaming"
        L_0x023c:
            if (r1 == 0) goto L_0x0243
            boolean r3 = r0.hasUserRestrictionOnAnyUser(r2)
            return r3
        L_0x0243:
            android.os.UserHandle r3 = android.os.UserHandle.of(r11)
            boolean r3 = r0.hasUserRestriction(r2, r3)
            return r3
        L_0x024c:
            return r4
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.pm.UserRestrictionsUtils.isSettingRestrictedForUser(android.content.Context, java.lang.String, int, java.lang.String, int):boolean");
    }

    public static void dumpRestrictions(PrintWriter pw, String prefix, Bundle restrictions) {
        boolean noneSet = true;
        if (restrictions != null) {
            for (String key : restrictions.keySet()) {
                if (restrictions.getBoolean(key, false)) {
                    pw.println(prefix + key);
                    noneSet = false;
                }
            }
            if (noneSet) {
                pw.println(prefix + "none");
                return;
            }
            return;
        }
        pw.println(prefix + "null");
    }

    public static void moveRestriction(String restrictionKey, SparseArray<Bundle> srcRestrictions, SparseArray<Bundle> destRestrictions) {
        int i = 0;
        while (i < srcRestrictions.size()) {
            int key = srcRestrictions.keyAt(i);
            Bundle from = srcRestrictions.valueAt(i);
            if (contains(from, restrictionKey)) {
                from.remove(restrictionKey);
                Bundle to = destRestrictions.get(key);
                if (to == null) {
                    to = new Bundle();
                    destRestrictions.append(key, to);
                }
                to.putBoolean(restrictionKey, true);
                if (from.isEmpty()) {
                    srcRestrictions.removeAt(i);
                    i--;
                }
            }
            i++;
        }
    }

    public static boolean restrictionsChanged(Bundle oldRestrictions, Bundle newRestrictions, String... restrictions) {
        if (restrictions.length == 0) {
            return areEqual(oldRestrictions, newRestrictions);
        }
        for (String restriction : restrictions) {
            if (oldRestrictions.getBoolean(restriction, false) != newRestrictions.getBoolean(restriction, false)) {
                return true;
            }
        }
        return false;
    }

    private static void setInstallMarketAppsRestriction(ContentResolver cr, int userId, int settingValue) {
        Settings.Secure.putIntForUser(cr, "install_non_market_apps", settingValue, userId);
    }

    private static int getNewUserRestrictionSetting(Context context, int userId, String userRestriction, boolean newValue) {
        return (newValue || UserManager.get(context).hasUserRestriction(userRestriction, UserHandle.of(userId))) ? 0 : 1;
    }
}
