package b.b.a.e;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import miui.provider.ExtraTelephony;
import miui.util.IOUtils;

class l extends AsyncTask<Void, Void, Void> {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ String f1450a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ Context f1451b;

    /* renamed from: c  reason: collision with root package name */
    final /* synthetic */ int f1452c;

    /* renamed from: d  reason: collision with root package name */
    final /* synthetic */ String f1453d;

    l(String str, Context context, int i, String str2) {
        this.f1450a = str;
        this.f1451b = context;
        this.f1452c = i;
        this.f1453d = str2;
    }

    /* access modifiers changed from: protected */
    /* renamed from: a */
    public Void doInBackground(Void... voidArr) {
        ContentValues contentValues = new ContentValues();
        String f = n.f(this.f1450a);
        Cursor query = this.f1451b.getContentResolver().query(Uri.withAppendedPath(ExtraTelephony.FirewallLog.CONTENT_URI, f), (String[]) null, "type = 2", (String[]) null, (String) null);
        if (query != null) {
            try {
                if (query.moveToFirst()) {
                    long j = query.getLong(0);
                    contentValues.put("reason", Integer.valueOf(this.f1452c));
                    contentValues.put("data1", this.f1453d);
                    this.f1451b.getContentResolver().update(ContentUris.withAppendedId(ExtraTelephony.FirewallLog.CONTENT_URI, j), contentValues, (String) null, (String[]) null);
                    IOUtils.closeQuietly(query);
                    return null;
                }
            } catch (Exception e) {
                Log.e("AntiSpamUtils", "Cursor exception when add message intercept log! ", e);
            } catch (Throwable th) {
                IOUtils.closeQuietly(query);
                throw th;
            }
        }
        contentValues.put("number", f);
        contentValues.put("read", 1);
        contentValues.put("type", 2);
        contentValues.put("reason", Integer.valueOf(this.f1452c));
        contentValues.put("data1", this.f1453d);
        this.f1451b.getContentResolver().insert(ExtraTelephony.FirewallLog.CONTENT_URI, contentValues);
        IOUtils.closeQuietly(query);
        return null;
    }
}
