package com.miui.antispam.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.provider.CallLog;
import android.util.Log;
import com.miui.maml.data.VariableNames;
import java.util.LinkedList;
import miui.provider.ExtraTelephony;
import miui.util.IOUtils;

class a implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ Context f2338a;

    a(Context context) {
        this.f2338a = context;
    }

    public void run() {
        Cursor cursor = null;
        try {
            Cursor cursor2 = this.f2338a.getContentResolver().query(ExtraTelephony.FirewallLog.CONTENT_URI, (String[]) null, "type = 1", (String[]) null, (String) null);
            if (cursor2 != null) {
                try {
                    LinkedList linkedList = new LinkedList();
                    while (cursor2.moveToNext()) {
                        ContentValues contentValues = new ContentValues();
                        contentValues.put("number", cursor2.getString(cursor2.getColumnIndex("number")));
                        contentValues.put("presentation", 1);
                        contentValues.put("type", Integer.valueOf(cursor2.getInt(cursor2.getColumnIndex("callType"))));
                        contentValues.put(VariableNames.VAR_DATE, Long.valueOf(cursor2.getLong(cursor2.getColumnIndex(VariableNames.VAR_DATE))));
                        contentValues.put("duration", Integer.valueOf(cursor2.getInt(cursor2.getColumnIndex("data1"))));
                        contentValues.put("firewalltype", Integer.valueOf(cursor2.getInt(cursor2.getColumnIndex("reason"))));
                        contentValues.put("forwarded_call", 0);
                        contentValues.put("simid", Long.valueOf(cursor2.getLong(cursor2.getColumnIndex("simid"))));
                        contentValues.put("phone_call_type", 0);
                        contentValues.put("features", 0);
                        linkedList.add(contentValues);
                    }
                    if (linkedList.size() > 0) {
                        this.f2338a.getContentResolver().bulkInsert(CallLog.Calls.CONTENT_URI, (ContentValues[]) linkedList.toArray(new ContentValues[linkedList.size()]));
                    }
                } catch (Exception e) {
                    e = e;
                    cursor = cursor2;
                    try {
                        Log.e("AntiSpamDB", "exception when migrate call logs from antispam to contacts ", e);
                        IOUtils.closeQuietly(cursor);
                    } catch (Throwable th) {
                        th = th;
                        cursor2 = cursor;
                        IOUtils.closeQuietly(cursor2);
                        throw th;
                    }
                } catch (Throwable th2) {
                    th = th2;
                    IOUtils.closeQuietly(cursor2);
                    throw th;
                }
            }
            this.f2338a.getContentResolver().delete(ExtraTelephony.FirewallLog.CONTENT_URI, "type = 1", (String[]) null);
            IOUtils.closeQuietly(cursor2);
        } catch (Exception e2) {
            e = e2;
            Log.e("AntiSpamDB", "exception when migrate call logs from antispam to contacts ", e);
            IOUtils.closeQuietly(cursor);
        }
    }
}
