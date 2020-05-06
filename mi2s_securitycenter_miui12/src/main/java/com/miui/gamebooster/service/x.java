package com.miui.gamebooster.service;

import android.database.Cursor;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.text.TextUtils;
import com.miui.gamebooster.service.GameBoosterTelecomManager;
import miui.util.IOUtils;

class x extends AsyncTask<Void, Void, String> {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ GameBoosterTelecomManager.b f4840a;

    x(GameBoosterTelecomManager.b bVar) {
        this.f4840a = bVar;
    }

    /* access modifiers changed from: protected */
    /* renamed from: a */
    public String doInBackground(Void... voidArr) {
        Cursor cursor;
        Cursor cursor2 = null;
        try {
            cursor = this.f4840a.f4771d.getContentResolver().query(ContactsContract.PhoneLookup.CONTENT_FILTER_URI.buildUpon().appendPath(this.f4840a.f4769b).build(), (String[]) null, (String) null, (String[]) null, (String) null);
            try {
                if (cursor.moveToFirst()) {
                    String string = cursor.getString(cursor.getColumnIndex("display_name"));
                    IOUtils.closeQuietly(cursor);
                    return string;
                }
            } catch (Exception unused) {
            } catch (Throwable th) {
                Cursor cursor3 = cursor;
                th = th;
                cursor2 = cursor3;
                IOUtils.closeQuietly(cursor2);
                throw th;
            }
        } catch (Exception unused2) {
            cursor = null;
        } catch (Throwable th2) {
            th = th2;
            IOUtils.closeQuietly(cursor2);
            throw th;
        }
        IOUtils.closeQuietly(cursor);
        return null;
    }

    /* access modifiers changed from: protected */
    /* renamed from: a */
    public void onPostExecute(String str) {
        super.onPostExecute(str);
        this.f4840a.a(str);
        if (!TextUtils.isEmpty(str) && this.f4840a.h && this.f4840a.f != null) {
            this.f4840a.f.setCallerName(str);
        }
    }
}
