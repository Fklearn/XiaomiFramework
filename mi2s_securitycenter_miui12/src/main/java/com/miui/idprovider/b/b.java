package com.miui.idprovider.b;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import com.miui.securitycenter.R;

public class b extends SQLiteOpenHelper {

    /* renamed from: a  reason: collision with root package name */
    private Context f5610a;

    public b(Context context) {
        super(context, "IdProvider", (SQLiteDatabase.CursorFactory) null, 1);
        this.f5610a = context.getApplicationContext();
    }

    public void onCreate(SQLiteDatabase sQLiteDatabase) {
        sQLiteDatabase.beginTransaction();
        try {
            sQLiteDatabase.execSQL(this.f5610a.getString(R.string.create_table_oaid));
            sQLiteDatabase.execSQL(this.f5610a.getString(R.string.create_table_vaid));
            sQLiteDatabase.execSQL(this.f5610a.getString(R.string.create_table_aaid));
            sQLiteDatabase.setTransactionSuccessful();
        } catch (Exception e) {
            Log.e("IdDbHelper", "Create Database Exception!", e);
        } catch (Throwable th) {
            sQLiteDatabase.endTransaction();
            throw th;
        }
        sQLiteDatabase.endTransaction();
    }

    public void onUpgrade(SQLiteDatabase sQLiteDatabase, int i, int i2) {
    }
}
