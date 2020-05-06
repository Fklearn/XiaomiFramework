package com.miui.antispam.db.a;

import android.content.Context;
import android.database.Cursor;
import com.miui.securitycenter.Application;
import java.util.ArrayList;
import java.util.List;
import miui.provider.BatchOperation;

public abstract class a<T> {

    /* renamed from: a  reason: collision with root package name */
    protected Context f2339a = Application.d();

    /* renamed from: b  reason: collision with root package name */
    protected BatchOperation f2340b = new BatchOperation(this.f2339a.getContentResolver(), "antispam");

    public abstract T a(Cursor cursor);

    public List<T> b(Cursor cursor) {
        ArrayList arrayList = new ArrayList();
        if (cursor != null) {
            while (cursor.moveToNext()) {
                try {
                    arrayList.add(a(cursor));
                } finally {
                    cursor.close();
                }
            }
        }
        return arrayList;
    }
}
