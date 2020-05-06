package com.miui.antispam.db.a;

import android.content.ContentProviderOperation;
import android.database.Cursor;
import android.net.Uri;
import android.util.SparseArray;
import b.b.a.c;
import com.google.android.exoplayer2.upstream.DataSchemeDataSource;
import com.miui.antispam.db.b.a;
import java.util.List;
import miui.provider.ExtraTelephony;

public class b extends a<a> {
    public Uri a() {
        return ExtraTelephony.Keyword.CONTENT_URI;
    }

    public a a(Cursor cursor) {
        return null;
    }

    public void a(SparseArray<List<a>> sparseArray) {
        List<a> list = sparseArray.get(1);
        List<a> list2 = sparseArray.get(2);
        for (a aVar : sparseArray.get(0)) {
            this.f2340b.add(ContentProviderOperation.newInsert(a()).withValue(DataSchemeDataSource.SCHEME_DATA, aVar.a()).withValue("type", Integer.valueOf(aVar.b())).withValue("cloudUid", aVar.c()).build());
            if (this.f2340b.size() > 100) {
                this.f2340b.execute();
            }
        }
        for (a aVar2 : list) {
            this.f2340b.add(ContentProviderOperation.newUpdate(a()).withValue(DataSchemeDataSource.SCHEME_DATA, aVar2.a()).withValue("type", Integer.valueOf(aVar2.b())).withSelection("cloudUid = ? ", new String[]{aVar2.c()}).build());
            if (this.f2340b.size() > 100) {
                this.f2340b.execute();
            }
        }
        for (a c2 : list2) {
            this.f2340b.add(ContentProviderOperation.newDelete(a()).withSelection("cloudUid = ? ", new String[]{c2.c()}).build());
            if (this.f2340b.size() > 100) {
                this.f2340b.execute();
            }
        }
        if (this.f2340b.size() > 0) {
            this.f2340b.execute();
        }
        if (!list.isEmpty()) {
            c.a(this.f2339a).a().a();
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
        throw new UnsupportedOperationException("Method not decompiled: com.miui.antispam.db.a.b.a(java.lang.String):boolean");
    }
}
