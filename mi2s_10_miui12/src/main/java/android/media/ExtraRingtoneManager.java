package android.media;

import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.provider.MiuiSettings;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import miui.app.constants.ThemeManagerConstants;
import miui.content.res.ThemeNativeUtils;
import miui.content.res.ThemeResources;
import miui.system.R;
import miui.telephony.phonenumber.Prefix;
import miui.yellowpage.YellowPageContract;

public class ExtraRingtoneManager {
    public static final String ACTION_MIUI_RINGTONE_PICKER = "miui.intent.action.RINGTONE_PICKER";
    private static final Uri ACTUAL_DEFAULT_RINGTONE_BASE_URI = Uri.parse("file://" + ThemeResources.THEME_MAGIC_PATH + "ringtones");
    public static final String EXTRA_RINGTONE_URI_LIST = "miui.intent.extra.ringtone.EXTRA_RINGTONE_URI_LIST";
    private static final String TAG = "ExtraRingtoneManager";
    private static final String TARGET_USER = "root";
    public static final int TYPE_CALENDAR = 4096;
    public static final int TYPE_MUSIC = 32;
    public static final int TYPE_RINGTONE_SLOT_1 = 64;
    public static final int TYPE_RINGTONE_SLOT_2 = 128;
    public static final int TYPE_SMS_DELIVERED_SLOT_1 = 256;
    public static final int TYPE_SMS_DELIVERED_SLOT_2 = 512;
    public static final int TYPE_SMS_DELIVERED_SOUND = 8;
    public static final int TYPE_SMS_RECEIVED_SLOT_1 = 1024;
    public static final int TYPE_SMS_RECEIVED_SLOT_2 = 2048;
    public static final int TYPE_SMS_RECEIVED_SOUND = 16;
    private static ArrayList<SoundItem> sRingtoneList = new ArrayList<>();

    static {
        addSoundItem(1, "ringtone.mp3", Settings.System.DEFAULT_RINGTONE_URI, ThemeManagerConstants.COMPONENT_CODE_RINGTONE, R.string.def_ringtone);
        addSoundItem(2, "notification.mp3", Settings.System.DEFAULT_NOTIFICATION_URI, "notification_sound", R.string.def_notification_sound);
        addSoundItem(4, "alarm.mp3", Settings.System.DEFAULT_ALARM_ALERT_URI, "alarm_alert", R.string.def_alarm_alert);
        addSoundItem(4096, "calendar.mp3", MiuiSettings.System.DEFAULT_CALENDAR_ALERT_URI, "calendar_alert", R.string.def_notification_sound);
        addSoundItem(8, "sms_delivered_sound.mp3", MiuiSettings.System.DEFAULT_SMS_DELIVERED_RINGTONE_URI, "sms_delivered_sound", R.string.def_sms_delivered_sound);
        addSoundItem(16, "sms_received_sound.mp3", MiuiSettings.System.DEFAULT_SMS_RECEIVED_RINGTONE_URI, "sms_received_sound", R.string.def_sms_received_sound);
        addSoundItem(64, "ringtone_slot_1.mp3", MiuiSettings.System.DEFAULT_RINGTONE_URI_SLOT_1, "ringtone_sound_slot_1", R.string.def_ringtone_slot_1);
        addSoundItem(128, "ringtone_slot_2.mp3", MiuiSettings.System.DEFAULT_RINGTONE_URI_SLOT_2, "ringtone_sound_slot_2", R.string.def_ringtone_slot_2);
        addSoundItem(1024, "sms_received_slot_1.mp3", MiuiSettings.System.DEFAULT_SMS_RECEIVED_SOUND_URI_SLOT_1, "sms_received_sound_slot_1", R.string.def_sms_received_sound_slot_1);
        addSoundItem(2048, "sms_received_slot_2.mp3", MiuiSettings.System.DEFAULT_SMS_RECEIVED_SOUND_URI_SLOT_2, "sms_received_sound_slot_2", R.string.def_sms_received_sound_slot_2);
        addSoundItem(256, "sms_delivered_slot_1.mp3", MiuiSettings.System.DEFAULT_SMS_DELIVERED_SOUND_URI_SLOT_1, "sms_delivered_sound_slot_1", R.string.def_sms_delivered_sound_slot_1);
        addSoundItem(512, "sms_delivered_slot_2.mp3", MiuiSettings.System.DEFAULT_SMS_DELIVERED_SOUND_URI_SLOT_2, "sms_delivered_sound_slot_2", R.string.def_sms_delivered_sound_slot_2);
    }

    private ExtraRingtoneManager() {
    }

    public static int getDefaultSoundType(Uri defaultSoundUri) {
        if (defaultSoundUri == null) {
            return -1;
        }
        if (defaultSoundUri.equals(Settings.System.DEFAULT_RINGTONE_URI)) {
            return 1;
        }
        if (defaultSoundUri.equals(Settings.System.DEFAULT_NOTIFICATION_URI)) {
            return 2;
        }
        if (defaultSoundUri.equals(Settings.System.DEFAULT_ALARM_ALERT_URI)) {
            return 4;
        }
        if (defaultSoundUri.equals(MiuiSettings.System.DEFAULT_CALENDAR_ALERT_URI)) {
            return 4096;
        }
        if (defaultSoundUri.equals(MiuiSettings.System.DEFAULT_RINGTONE_URI_SLOT_1)) {
            return 64;
        }
        if (defaultSoundUri.equals(MiuiSettings.System.DEFAULT_RINGTONE_URI_SLOT_2)) {
            return 128;
        }
        if (defaultSoundUri.equals(MiuiSettings.System.DEFAULT_SMS_RECEIVED_RINGTONE_URI)) {
            return 16;
        }
        if (defaultSoundUri.equals(MiuiSettings.System.DEFAULT_SMS_RECEIVED_SOUND_URI_SLOT_1)) {
            return 1024;
        }
        if (defaultSoundUri.equals(MiuiSettings.System.DEFAULT_SMS_RECEIVED_SOUND_URI_SLOT_2)) {
            return 2048;
        }
        if (defaultSoundUri.equals(MiuiSettings.System.DEFAULT_SMS_DELIVERED_RINGTONE_URI)) {
            return 8;
        }
        if (defaultSoundUri.equals(MiuiSettings.System.DEFAULT_SMS_DELIVERED_SOUND_URI_SLOT_1)) {
            return 256;
        }
        if (defaultSoundUri.equals(MiuiSettings.System.DEFAULT_SMS_DELIVERED_SOUND_URI_SLOT_2)) {
            return 512;
        }
        return -1;
    }

    public static void saveDefaultSound(Context context, int type, Uri uri) {
        String setting = getSettingForType(type);
        if (setting != null && type != getDefaultSoundType(uri)) {
            String path = Prefix.EMPTY;
            if (uri != null) {
                if ("media".equals(uri.getAuthority())) {
                    path = resolveSoundPath(context, uri);
                    if (path != null) {
                        uri = Uri.fromFile(new File(path));
                    }
                } else if ("file".equals(uri.getScheme())) {
                    path = uri.getPath();
                }
            }
            copySound(context, path, type);
            Settings.System.putString(context.getContentResolver(), setting, uri != null ? uri.toString() : null);
        }
    }

    public static String resolveRingtonePath(Context context, Uri uri) {
        return resolveSoundPath(context, uri);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:14:0x002f, code lost:
        if (r1 == null) goto L_0x0032;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:15:0x0032, code lost:
        return r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:8:0x0023, code lost:
        if (r1 != null) goto L_0x0025;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:9:0x0025, code lost:
        r1.close();
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static java.lang.String resolveSoundPath(android.content.Context r8, android.net.Uri r9) {
        /*
            r0 = 0
            r1 = 0
            android.content.ContentResolver r2 = r8.getContentResolver()     // Catch:{ Exception -> 0x002b }
            java.lang.String r3 = "_data"
            java.lang.String[] r4 = new java.lang.String[]{r3}     // Catch:{ Exception -> 0x002b }
            r5 = 0
            r6 = 0
            r7 = 0
            r3 = r9
            android.database.Cursor r3 = r2.query(r3, r4, r5, r6, r7)     // Catch:{ Exception -> 0x002b }
            r1 = r3
            if (r1 == 0) goto L_0x0023
            boolean r3 = r1.moveToFirst()     // Catch:{ Exception -> 0x002b }
            if (r3 == 0) goto L_0x0023
            r3 = 0
            java.lang.String r3 = r1.getString(r3)     // Catch:{ Exception -> 0x002b }
            r0 = r3
        L_0x0023:
            if (r1 == 0) goto L_0x0032
        L_0x0025:
            r1.close()
            goto L_0x0032
        L_0x0029:
            r2 = move-exception
            goto L_0x0033
        L_0x002b:
            r2 = move-exception
            r2.printStackTrace()     // Catch:{ all -> 0x0029 }
            if (r1 == 0) goto L_0x0032
            goto L_0x0025
        L_0x0032:
            return r0
        L_0x0033:
            if (r1 == 0) goto L_0x0038
            r1.close()
        L_0x0038:
            throw r2
        */
        throw new UnsupportedOperationException("Method not decompiled: android.media.ExtraRingtoneManager.resolveSoundPath(android.content.Context, android.net.Uri):java.lang.String");
    }

    public static Uri getDefaultSoundInternalUri(int type) {
        Iterator<SoundItem> it = sRingtoneList.iterator();
        while (it.hasNext()) {
            SoundItem ringtoneItem = it.next();
            if (type == ringtoneItem.mRingtoneType) {
                return ringtoneItem.mActualDefaultRingtoneUri;
            }
        }
        return null;
    }

    public static Uri getRingtoneUri(Context context, int type) {
        return getDefaultSoundSettingUri(context, type);
    }

    public static Uri getDefaultSoundSettingUri(Context context, int type) {
        String uriString;
        String setting = getSettingForType(type);
        if (setting == null || (uriString = Settings.System.getString(context.getContentResolver(), setting)) == null) {
            return null;
        }
        return Uri.parse(uriString);
    }

    public static Uri getDefaultSoundActualUri(Context context, int type) {
        if (getDefaultSoundSettingUri(context, type) == null) {
            return null;
        }
        if (Build.VERSION.SDK_INT >= 24) {
            return getDefaultRingtoneUri(type);
        }
        return getActualDefaultRingtoneUri(context, type);
    }

    public static Uri getActualDefaultRingtoneUri(Context context, int type) {
        Uri directUri;
        if (!isValidType(type)) {
            Log.e(TAG, "getActualDefaultRingtoneUri type is invalid, type = " + type);
            return null;
        }
        Uri defaultSettingUri = getDefaultSoundSettingUri(context, type);
        if (defaultSettingUri == null) {
            return defaultSettingUri;
        }
        boolean isCycle = false;
        if (!"file".equals(defaultSettingUri.getScheme())) {
            if (!isSystemSettingsUri(defaultSettingUri)) {
                return defaultSettingUri;
            }
            int directType = getDefaultSoundType(defaultSettingUri);
            if (!isValidType(directType) || (directUri = getDefaultSoundSettingUri(context, directType)) == null) {
                return null;
            }
            if ("file".equals(directUri.getScheme())) {
                defaultSettingUri = directUri;
                type = directType;
            } else if (!isSystemSettingsUri(directUri)) {
                return directUri;
            } else {
                isCycle = true;
            }
        }
        ArrayList<String> effectPathList = new ArrayList<>();
        if (!isCycle) {
            String backupPath = getDefaultSoundInternalUri(type).getPath();
            effectPathList.add(backupPath);
            if (Build.VERSION.SDK_INT > 22) {
                effectPathList.add(backupPath.replace("/data/system/theme_magic", "/data/system"));
            }
            effectPathList.add(defaultSettingUri.getPath());
        }
        effectPathList.add(getBuildInRingtonePath(context, getBuildInRingtonePathRes(type)));
        Iterator<String> it = effectPathList.iterator();
        while (it.hasNext()) {
            String path = it.next();
            if (!TextUtils.isEmpty(path) && new File(path).exists()) {
                return Uri.parse("file://" + path);
            }
        }
        return null;
    }

    public static String getDefaultSoundName(Context context, int type) {
        return ExtraRingtone.getRingtoneTitle(context, getDefaultSoundSettingUri(context, type), true);
    }

    public static boolean isExtraCases(Uri soundUri) {
        return "file".equals(soundUri.getScheme());
    }

    public static Uri getUriForExtraCases(Uri soundUri, int ringtoneType) {
        if (!isExtraCases(soundUri)) {
            return soundUri;
        }
        Uri actualSoundUri = getDefaultSoundInternalUri(ringtoneType);
        if (new File(actualSoundUri.getPath()).exists()) {
            return actualSoundUri;
        }
        return soundUri;
    }

    private static void copySound(Context context, String path, int type) {
        String internalPath = getDefaultSoundInternalUri(type).getPath();
        if (TextUtils.isEmpty(path) || !new File(path).exists()) {
            ThemeNativeUtils.remove(internalPath);
            return;
        }
        String dir = ACTUAL_DEFAULT_RINGTONE_BASE_URI.getPath();
        if (!new File(dir).exists()) {
            ThemeNativeUtils.mkdirs(dir);
            ThemeNativeUtils.updateFilePermissionWithThemeContext(dir);
        }
        try {
            ThemeNativeUtils.copy(new File(path).getCanonicalPath(), internalPath);
            ThemeNativeUtils.updateFilePermissionWithThemeContext(internalPath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String getSettingForType(int type) {
        Iterator<SoundItem> it = sRingtoneList.iterator();
        while (it.hasNext()) {
            SoundItem ringtoneItem = it.next();
            if ((ringtoneItem.mRingtoneType & type) != 0) {
                return ringtoneItem.mSettingType;
            }
        }
        return null;
    }

    private static Uri getDefaultRingtoneUri(int type) {
        Iterator<SoundItem> it = sRingtoneList.iterator();
        while (it.hasNext()) {
            SoundItem ringtoneItem = it.next();
            if (type == ringtoneItem.mRingtoneType) {
                return ringtoneItem.mDefaultRingtoneUri;
            }
        }
        Log.e(TAG, "getDefaultRingtoneUri type is invalid, type = " + type);
        return null;
    }

    private static boolean isValidType(int type) {
        Iterator<SoundItem> it = sRingtoneList.iterator();
        while (it.hasNext()) {
            if (type == it.next().mRingtoneType) {
                return true;
            }
        }
        return false;
    }

    private static int getBuildInRingtonePathRes(int type) {
        Iterator<SoundItem> it = sRingtoneList.iterator();
        while (it.hasNext()) {
            SoundItem ringtoneItem = it.next();
            if (type == ringtoneItem.mRingtoneType) {
                return ringtoneItem.buildInPathRes;
            }
        }
        return -1;
    }

    private static void addSoundItem(int type, String name, Uri defaultUri, String key, int id) {
        sRingtoneList.add(new SoundItem(type, Uri.withAppendedPath(ACTUAL_DEFAULT_RINGTONE_BASE_URI, name), defaultUri, key, id));
    }

    private static class SoundItem {
        int buildInPathRes;
        Uri mActualDefaultRingtoneUri;
        Uri mDefaultRingtoneUri;
        int mRingtoneType;
        String mSettingType;

        public SoundItem(int ringtoneType, Uri actualDefaultRingtoneUri, Uri defaultRingtoneUri, String settingType, int id) {
            this.mRingtoneType = ringtoneType;
            this.mActualDefaultRingtoneUri = actualDefaultRingtoneUri;
            this.mDefaultRingtoneUri = defaultRingtoneUri;
            this.mSettingType = settingType;
            this.buildInPathRes = id;
        }
    }

    private static String getBuildInRingtonePath(Context context, int id) {
        if (id <= 0) {
            return null;
        }
        try {
            return context.getString(id);
        } catch (Exception e) {
            Log.e(TAG, e.toString());
            return null;
        }
    }

    private static boolean isSystemSettingsUri(Uri uri) {
        return uri != null && "content".equals(uri.getScheme()) && YellowPageContract.Settings.DIRECTORY.equals(uri.getAuthority());
    }
}
