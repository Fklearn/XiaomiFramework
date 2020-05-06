package com.miui.antispam.db;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import b.b.a.e.n;
import com.miui.maml.elements.AdvancedSlider;
import java.io.Closeable;
import miui.cloud.common.XSimChangeNotification;
import miui.provider.ExtraTelephony;
import miui.util.IOUtils;

class b implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ Context f2341a;

    b(Context context) {
        this.f2341a = context;
    }

    public void run() {
        Cursor cursor = null;
        try {
            cursor = this.f2341a.getContentResolver().query(ExtraTelephony.Phonelist.CONTENT_URI, (String[]) null, "type = ? AND sync_dirty <> ? ", new String[]{"2", String.valueOf(1)}, (String) null);
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    n.b(this.f2341a, cursor.getString(cursor.getColumnIndex("number")), cursor.getInt(cursor.getColumnIndex(AdvancedSlider.STATE)), 1, cursor.getInt(cursor.getColumnIndex(XSimChangeNotification.BROADCAST_EXTRA_KEY_SIM_ID)));
                }
            }
        } catch (Exception e) {
            Log.e("AntiSpamDB", "exception when mutual exclude block list ", e);
        } catch (Throwable th) {
            IOUtils.closeQuietly((Closeable) null);
            throw th;
        }
        IOUtils.closeQuietly(cursor);
    }
}
