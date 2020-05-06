package com.miui.antispam.db.a;

import android.content.ContentProviderOperation;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.SparseArray;
import com.miui.antispam.db.b.b;
import com.miui.maml.elements.AdvancedSlider;
import java.util.List;
import miui.provider.ExtraTelephony;

public class c extends a<b> {
    public Uri a() {
        return ExtraTelephony.Phonelist.CONTENT_URI;
    }

    public b a(Cursor cursor) {
        b bVar = new b();
        bVar.a(cursor.getInt(cursor.getColumnIndex("_id")));
        bVar.c(cursor.getString(cursor.getColumnIndex("number")));
        bVar.a(cursor.getString(cursor.getColumnIndex("display_number")));
        bVar.b(cursor.getString(cursor.getColumnIndex("notes")));
        bVar.b(cursor.getInt(cursor.getColumnIndex(AdvancedSlider.STATE)));
        bVar.c(cursor.getInt(cursor.getColumnIndex("type")));
        return bVar;
    }

    public List<b> a(SQLiteDatabase sQLiteDatabase) {
        return b(sQLiteDatabase.rawQuery("select * from phone_list where number NOT LIKE '%*' AND number NOT LIKE '***%'", (String[]) null));
    }

    public void a(SparseArray<List<b>> sparseArray) {
        List<b> list = sparseArray.get(1);
        List<b> list2 = sparseArray.get(2);
        for (b bVar : sparseArray.get(0)) {
            this.f2340b.add(ContentProviderOperation.newInsert(a()).withValue("number", bVar.b()).withValue("type", Integer.valueOf(bVar.d())).withValue(AdvancedSlider.STATE, Integer.valueOf(bVar.c())).withValue("cloudUid", bVar.a()).build());
            if (this.f2340b.size() > 100) {
                this.f2340b.execute();
            }
        }
        for (b bVar2 : list) {
            this.f2340b.add(ContentProviderOperation.newUpdate(a()).withValue("number", bVar2.b()).withValue("type", Integer.valueOf(bVar2.d())).withValue(AdvancedSlider.STATE, Integer.valueOf(bVar2.c())).withSelection("cloudUid = ? ", new String[]{bVar2.a()}).build());
            if (this.f2340b.size() > 100) {
                this.f2340b.execute();
            }
        }
        for (b a2 : list2) {
            this.f2340b.add(ContentProviderOperation.newDelete(a()).withSelection("cloudUid = ? ", new String[]{a2.a()}).build());
            if (this.f2340b.size() > 100) {
                this.f2340b.execute();
            }
        }
        if (this.f2340b.size() > 0) {
            this.f2340b.execute();
        }
        if (!list.isEmpty()) {
            b.b.a.c.a(this.f2339a).a().a();
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:10:0x0028, code lost:
        if (r1 != null) goto L_0x0033;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:15:0x0031, code lost:
        if (r1 == null) goto L_0x0036;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:16:0x0033, code lost:
        r1.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:17:0x0036, code lost:
        return false;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean a(java.lang.String r10) {
        /*
            r9 = this;
            r0 = 0
            r1 = 0
            android.content.Context r2 = r9.f2339a     // Catch:{ Exception -> 0x002d }
            android.content.ContentResolver r3 = r2.getContentResolver()     // Catch:{ Exception -> 0x002d }
            android.net.Uri r4 = r9.a()     // Catch:{ Exception -> 0x002d }
            r5 = 0
            java.lang.String r6 = "cloudUid = ? "
            r2 = 1
            java.lang.String[] r7 = new java.lang.String[r2]     // Catch:{ Exception -> 0x002d }
            r7[r0] = r10     // Catch:{ Exception -> 0x002d }
            r8 = 0
            android.database.Cursor r1 = r3.query(r4, r5, r6, r7, r8)     // Catch:{ Exception -> 0x002d }
            if (r1 == 0) goto L_0x0028
            int r10 = r1.getCount()     // Catch:{ Exception -> 0x002d }
            if (r10 <= 0) goto L_0x0022
            r0 = r2
        L_0x0022:
            if (r1 == 0) goto L_0x0027
            r1.close()
        L_0x0027:
            return r0
        L_0x0028:
            if (r1 == 0) goto L_0x0036
            goto L_0x0033
        L_0x002b:
            r10 = move-exception
            goto L_0x0037
        L_0x002d:
            r10 = move-exception
            r10.printStackTrace()     // Catch:{ all -> 0x002b }
            if (r1 == 0) goto L_0x0036
        L_0x0033:
            r1.close()
        L_0x0036:
            return r0
        L_0x0037:
            if (r1 == 0) goto L_0x003c
            r1.close()
        L_0x003c:
            throw r10
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.antispam.db.a.c.a(java.lang.String):boolean");
    }
}
