package b.b.b.d;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import com.google.android.exoplayer2.upstream.DataSchemeDataSource;
import org.json.JSONObject;

public class j {

    /* renamed from: a  reason: collision with root package name */
    public static final Uri f1532a = Uri.parse("content://com.miui.monthreport/report_json");

    public static void a(Context context, JSONObject jSONObject) {
        if (jSONObject != null) {
            ContentValues contentValues = new ContentValues();
            contentValues.put("pkgName", "com.miui.antivirus");
            contentValues.put("eventType", 801);
            contentValues.put("version", 3);
            contentValues.put(DataSchemeDataSource.SCHEME_DATA, jSONObject.toString());
            try {
                context.getContentResolver().insert(f1532a, contentValues);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
        }
    }
}
