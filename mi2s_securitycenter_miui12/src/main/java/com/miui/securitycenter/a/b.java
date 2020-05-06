package com.miui.securitycenter.a;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;
import android.util.Log;
import com.miui.securitycenter.R;

public class b extends SQLiteOpenHelper {

    /* renamed from: a  reason: collision with root package name */
    private Context f7466a;

    public b(@Nullable Context context) {
        super(context, "ThirdDesktop", (SQLiteDatabase.CursorFactory) null, 1);
        this.f7466a = context.getApplicationContext();
    }

    public void onCreate(SQLiteDatabase sQLiteDatabase) {
        sQLiteDatabase.beginTransaction();
        try {
            sQLiteDatabase.execSQL(this.f7466a.getString(R.string.create_table_thirddesktop));
            sQLiteDatabase.setTransactionSuccessful();
        } catch (Exception e) {
            Log.e("ThirdDesktopDb", "exception when create antispam DB ", e);
        } catch (Throwable th) {
            sQLiteDatabase.endTransaction();
            throw th;
        }
        sQLiteDatabase.endTransaction();
    }

    public void onUpgrade(SQLiteDatabase sQLiteDatabase, int i, int i2) {
    }
}
