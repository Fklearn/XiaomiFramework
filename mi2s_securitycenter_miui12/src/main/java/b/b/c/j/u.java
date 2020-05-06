package b.b.c.j;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import com.miui.networkassistant.provider.ProviderConstant;
import miui.util.IOUtils;

public final class u {

    public static class a {

        /* renamed from: a  reason: collision with root package name */
        public long f1762a;

        /* renamed from: b  reason: collision with root package name */
        public long f1763b;

        /* renamed from: c  reason: collision with root package name */
        public long f1764c;

        /* renamed from: d  reason: collision with root package name */
        public boolean f1765d;

        public String toString() {
            return "NaTrafficInfo{monthTotal=" + this.f1762a + ", monthUsed=" + this.f1763b + ", warningVal=" + this.f1764c + ", purchaseTip=" + this.f1765d + '}';
        }
    }

    public static long a(Context context) {
        if (!l(context)) {
            return -1;
        }
        String a2 = a(context, "content://com.miui.networkassistant.provider/datausage_status", "total_limit");
        if (!a2.isEmpty()) {
            return Long.parseLong(a2);
        }
        return 0;
    }

    private static String a(Context context, String str, String str2) {
        Cursor query = context.getContentResolver().query(Uri.parse(str), (String[]) null, (String) null, (String[]) null, (String) null);
        if (query != null) {
            try {
                if (query.moveToFirst()) {
                    return query.getString(query.getColumnIndex(str2));
                }
            } finally {
                IOUtils.closeQuietly(query);
            }
        }
        IOUtils.closeQuietly(query);
        return null;
    }

    public static long b(Context context) {
        if (!l(context)) {
            return -1;
        }
        String a2 = a(context, "content://com.miui.networkassistant.provider/na_settings_info", ProviderConstant.NASettingsInfoColumns.CORRECTION_TIME);
        if (!TextUtils.isEmpty(a2)) {
            return Long.parseLong(a2);
        }
        return 0;
    }

    public static a c(Context context) {
        Cursor query = context.getContentResolver().query(Uri.parse("content://com.miui.networkassistant.provider/datausage_status/securitycenter"), (String[]) null, (String) null, (String[]) null, (String) null);
        try {
            Log.i("NetworkAssistUtils", "get traffic info");
            if (query != null && query.moveToFirst()) {
                a aVar = new a();
                aVar.f1762a = query.getLong(query.getColumnIndex("total_limit"));
                aVar.f1763b = query.getLong(query.getColumnIndex("month_used"));
                aVar.f1764c = query.getLong(query.getColumnIndex(ProviderConstant.DataUsageStatusColumns.MONTH_WARNING));
                aVar.f1765d = Boolean.parseBoolean(query.getString(query.getColumnIndex(ProviderConstant.DataUsageStatusColumns.PURCHASE_TIPS_ENABLE)));
                Log.i("NetworkAssistUtils", "NaTrafficInfo: " + aVar.toString());
                IOUtils.closeQuietly(query);
                return aVar;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } catch (Throwable th) {
            IOUtils.closeQuietly(query);
            throw th;
        }
        IOUtils.closeQuietly(query);
        return null;
    }

    public static boolean d(Context context) {
        String a2 = a(context, "content://com.miui.networkassistant.provider/na_settings_info", ProviderConstant.NASettingsInfoColumns.AUTO_TRAFFIC_CORRECTION);
        return !TextUtils.isEmpty(a2) && Boolean.parseBoolean(a2);
    }

    public static boolean e(Context context) {
        String a2 = a(context, "content://com.miui.networkassistant.provider/na_settings_info", ProviderConstant.NASettingsInfoColumns.SHOW_STATUS_BAR_SETTED);
        return TextUtils.isEmpty(a2) || Long.parseLong(a2) == 1;
    }

    public static boolean f(Context context) {
        String a2 = a(context, "content://com.miui.networkassistant.provider/na_settings_info", ProviderConstant.NASettingsInfoColumns.OVERSEA_VERSION);
        return !TextUtils.isEmpty(a2) && Boolean.parseBoolean(a2);
    }

    public static boolean g(Context context) {
        String a2 = a(context, "content://com.miui.networkassistant.provider/na_settings_info", ProviderConstant.NASettingsInfoColumns.TC_DIAGNOSTIC);
        return !TextUtils.isEmpty(a2) && Boolean.parseBoolean(a2);
    }

    public static boolean h(Context context) {
        String a2 = a(context, "content://com.miui.networkassistant.provider/na_settings_info", ProviderConstant.NASettingsInfoColumns.OPERATOR_SETTED);
        return !TextUtils.isEmpty(a2) && Boolean.parseBoolean(a2);
    }

    public static boolean i(Context context) {
        String a2 = a(context, "content://com.miui.networkassistant.provider/na_settings_info", ProviderConstant.NASettingsInfoColumns.NEEDED_TRAFFIC_PURCHASE);
        if (!TextUtils.isEmpty(a2)) {
            return Boolean.parseBoolean(a2);
        }
        return false;
    }

    public static void j(Context context) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(ProviderConstant.NASettingsInfoColumns.AUTO_TRAFFIC_CORRECTION, true);
        context.getContentResolver().update(Uri.parse("content://com.miui.networkassistant.provider/na_settings_info"), contentValues, (String) null, (String[]) null);
    }

    public static void k(Context context) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(ProviderConstant.NASettingsInfoColumns.SHOW_STATUS_BAR_SETTED, 1);
        context.getContentResolver().update(Uri.parse("content://com.miui.networkassistant.provider/na_settings_info"), contentValues, (String) null, (String[]) null);
    }

    public static boolean l(Context context) {
        Cursor query = context.getContentResolver().query(Uri.parse("content://com.miui.networkassistant.provider/na_settings_info"), (String[]) null, (String) null, (String[]) null, (String) null);
        if (query == null) {
            return false;
        }
        query.close();
        return true;
    }
}
