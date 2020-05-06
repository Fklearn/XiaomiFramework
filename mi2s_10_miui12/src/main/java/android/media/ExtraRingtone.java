package android.media;

import android.content.Context;
import android.content.res.Resources;
import android.net.Uri;
import android.text.TextUtils;
import com.miui.system.internal.R;
import miui.cloud.backup.data.KeyStringSettingItem;
import miui.content.res.ThemeResources;
import miui.content.res.ThemeRuntimeManager;
import miui.os.Build;
import miui.os.FileUtils;
import miui.telephony.phonenumber.Prefix;
import miui.yellowpage.YellowPageContract;

public class ExtraRingtone {
    private static final String[] MEDIA_COLUMNS = {"_id", "_data", "title"};

    public static String getRingtoneTitle(Context context, Uri uri, boolean formatSystemRingtone) {
        if (Build.IS_MIUI) {
            return getRingtoneTitleMIUI(context, uri, formatSystemRingtone);
        }
        return getRingtoneTitleAndroid(context, uri);
    }

    private static String getRingtoneTitleMIUI(Context context, Uri uri, boolean formatSystemRingtone) {
        String title = getTitle(context, uri, true);
        if (uri == null || !formatSystemRingtone || !YellowPageContract.Settings.DIRECTORY.equals(uri.getAuthority())) {
            return title;
        }
        return context.getString(R.string.android_ringtone_default_with_actual, new Object[]{title});
    }

    /* JADX WARNING: Removed duplicated region for block: B:33:0x007d A[DONT_GENERATE] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static java.lang.String getTitle(android.content.Context r13, android.net.Uri r14, boolean r15) {
        /*
            r0 = 0
            android.content.ContentResolver r7 = r13.getContentResolver()
            r8 = 0
            r9 = -1
            r10 = 0
            if (r14 == 0) goto L_0x0087
            java.lang.String r11 = r14.getAuthority()
            java.lang.String r1 = "settings"
            boolean r1 = r1.equals(r11)
            if (r1 == 0) goto L_0x0026
            if (r15 == 0) goto L_0x0087
            int r1 = android.media.ExtraRingtoneManager.getDefaultSoundType(r14)
            android.net.Uri r1 = android.media.ExtraRingtoneManager.getRingtoneUri(r13, r1)
            java.lang.String r2 = getTitle(r13, r1, r10)
            return r2
        L_0x0026:
            r12 = 0
            java.lang.String r1 = "media"
            boolean r1 = r1.equals(r11)
            if (r1 == 0) goto L_0x003b
            java.lang.String[] r3 = MEDIA_COLUMNS
            r4 = 0
            r5 = 0
            r6 = 0
            r1 = r7
            r2 = r14
            android.database.Cursor r0 = r1.query(r2, r3, r4, r5, r6)
            r12 = 1
        L_0x003b:
            java.lang.String r1 = ""
            if (r0 == 0) goto L_0x0056
            int r2 = r0.getCount()     // Catch:{ all -> 0x0054 }
            r3 = 1
            if (r2 != r3) goto L_0x0056
            r0.moveToFirst()     // Catch:{ all -> 0x0054 }
            r2 = 2
            java.lang.String r2 = r0.getString(r2)     // Catch:{ all -> 0x0054 }
            if (r2 != 0) goto L_0x0052
            r8 = r1
            goto L_0x007b
        L_0x0052:
            r8 = r2
            goto L_0x007b
        L_0x0054:
            r1 = move-exception
            goto L_0x0081
        L_0x0056:
            if (r12 == 0) goto L_0x005a
            r8 = r1
            goto L_0x007b
        L_0x005a:
            java.lang.String r1 = r14.getPath()     // Catch:{ all -> 0x0054 }
            java.lang.String r1 = getSystemLocalizationFileName(r13, r1)     // Catch:{ all -> 0x0054 }
            r8 = r1
            if (r8 != 0) goto L_0x007b
            java.lang.String r1 = r14.getLastPathSegment()     // Catch:{ all -> 0x0054 }
            r8 = r1
            if (r8 == 0) goto L_0x0073
            java.lang.String r1 = "_&_"
            int r1 = r8.indexOf(r1)     // Catch:{ all -> 0x0054 }
            goto L_0x0074
        L_0x0073:
            r1 = r9
        L_0x0074:
            if (r1 <= 0) goto L_0x007b
            java.lang.String r2 = r8.substring(r10, r1)     // Catch:{ all -> 0x0054 }
            r8 = r2
        L_0x007b:
            if (r0 == 0) goto L_0x0087
            r0.close()
            goto L_0x0087
        L_0x0081:
            if (r0 == 0) goto L_0x0086
            r0.close()
        L_0x0086:
            throw r1
        L_0x0087:
            if (r8 != 0) goto L_0x0090
            int r1 = miui.system.R.string.android_ringtone_silent
            java.lang.String r1 = r13.getString(r1)
            goto L_0x00ac
        L_0x0090:
            int r1 = r8.length()
            if (r1 != 0) goto L_0x009d
            int r1 = miui.system.R.string.android_ringtone_unknown
            java.lang.String r1 = r13.getString(r1)
            goto L_0x00ac
        L_0x009d:
            java.lang.String r1 = "."
            int r1 = r8.lastIndexOf(r1)
            if (r1 != r9) goto L_0x00a7
            r2 = r8
            goto L_0x00ab
        L_0x00a7:
            java.lang.String r2 = r8.substring(r10, r1)
        L_0x00ab:
            r1 = r2
        L_0x00ac:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: android.media.ExtraRingtone.getTitle(android.content.Context, android.net.Uri, boolean):java.lang.String");
    }

    public static String getSystemLocalizationFileName(Context context, String path) {
        if (TextUtils.isEmpty(path) || !path.startsWith(ThemeRuntimeManager.BUILTIN_ROOT_PATH)) {
            return null;
        }
        Resources resources = context.getResources();
        int res = -1;
        try {
            res = resources.getIdentifier(FileUtils.getName(path).toLowerCase(), KeyStringSettingItem.TYPE, "miui.system");
        } catch (Exception e) {
        }
        if (res > 0) {
            return resources.getString(res);
        }
        return null;
    }

    private static String getRingtoneTitleAndroid(Context context, Uri uri) {
        Ringtone r = RingtoneManager.getRingtone(context, uri);
        String title = r != null ? r.getTitle(context) : null;
        if (title != null) {
            return title;
        }
        int resId = context.getResources().getIdentifier("ringtone_unknown", KeyStringSettingItem.TYPE, ThemeResources.FRAMEWORK_PACKAGE);
        if (resId > 0) {
            return context.getString(resId);
        }
        return Prefix.EMPTY;
    }
}
