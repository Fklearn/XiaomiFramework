package com.miui.antispam.db;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import b.b.a.e.n;
import b.b.c.j.d;
import com.miui.antispam.db.b.b;
import com.miui.securitycenter.R;
import java.util.List;

public class c extends SQLiteOpenHelper {

    /* renamed from: a  reason: collision with root package name */
    private Context f2349a;

    public c(Context context) {
        super(context, "AntiSpam", (SQLiteDatabase.CursorFactory) null, 10);
        this.f2349a = context.getApplicationContext();
    }

    private static void a(Context context) {
        d.a(new a(context));
    }

    private static void b(Context context) {
        d.a(new b(context));
    }

    public void onCreate(SQLiteDatabase sQLiteDatabase) {
        sQLiteDatabase.beginTransaction();
        try {
            sQLiteDatabase.execSQL(this.f2349a.getString(R.string.create_table_report_sms));
            sQLiteDatabase.execSQL(this.f2349a.getString(R.string.create_table_report_sms_pending));
            sQLiteDatabase.execSQL(this.f2349a.getString(R.string.create_talbe_fwlog));
            sQLiteDatabase.execSQL(this.f2349a.getString(R.string.create_table_phonelist));
            sQLiteDatabase.execSQL(this.f2349a.getString(R.string.create_table_keyword));
            sQLiteDatabase.execSQL(this.f2349a.getString(R.string.create_table_mode));
            sQLiteDatabase.execSQL(this.f2349a.getString(R.string.create_table_sim));
            sQLiteDatabase.setTransactionSuccessful();
        } catch (Exception e) {
            Log.e("AntiSpamDB", "exception when create antispam DB ", e);
        } catch (Throwable th) {
            sQLiteDatabase.endTransaction();
            throw th;
        }
        sQLiteDatabase.endTransaction();
    }

    public void onDowngrade(SQLiteDatabase sQLiteDatabase, int i, int i2) {
        sQLiteDatabase.beginTransaction();
        try {
            sQLiteDatabase.execSQL("DROP TABLE fwlog;");
            sQLiteDatabase.execSQL("DROP TABLE phone_list;");
            sQLiteDatabase.execSQL("DROP TABLE keyword;");
            sQLiteDatabase.execSQL("DROP TABLE mode;");
            sQLiteDatabase.execSQL("DROP TABLE sim;");
            sQLiteDatabase.execSQL("DROP TABLE reportSms;");
            sQLiteDatabase.execSQL("DROP TABLE reportSmsPending;");
            sQLiteDatabase.setTransactionSuccessful();
        } catch (SQLException e) {
            Log.e("AntiSpamDB", "exception when onDowngrade dropping tables", e);
        } catch (Throwable th) {
            sQLiteDatabase.endTransaction();
            throw th;
        }
        sQLiteDatabase.endTransaction();
        onCreate(sQLiteDatabase);
    }

    public void onUpgrade(SQLiteDatabase sQLiteDatabase, int i, int i2) {
        if (i == 1) {
            sQLiteDatabase.beginTransaction();
            try {
                sQLiteDatabase.execSQL(this.f2349a.getString(R.string.create_table_report_sms_pending));
                sQLiteDatabase.setTransactionSuccessful();
                sQLiteDatabase.endTransaction();
                i = 2;
            } catch (Exception e) {
                Log.e("AntiSpamDB", "exception when update antispam DB ", e);
                sQLiteDatabase.endTransaction();
            } catch (Throwable th) {
                sQLiteDatabase.endTransaction();
                throw th;
            }
        }
        if (i == 2) {
            sQLiteDatabase.beginTransaction();
            try {
                sQLiteDatabase.execSQL(this.f2349a.getString(R.string.create_talbe_fwlog));
                sQLiteDatabase.execSQL(this.f2349a.getString(R.string.create_table_phonelist));
                sQLiteDatabase.execSQL(this.f2349a.getString(R.string.create_table_keyword));
                sQLiteDatabase.execSQL(this.f2349a.getString(R.string.create_table_mode));
                sQLiteDatabase.execSQL(this.f2349a.getString(R.string.create_table_sim));
                sQLiteDatabase.setTransactionSuccessful();
                sQLiteDatabase.endTransaction();
                i = 3;
            } catch (Exception e2) {
                Log.e("AntiSpamDB", "exception when update antispam DB ", e2);
                sQLiteDatabase.endTransaction();
            } catch (Throwable th2) {
                sQLiteDatabase.endTransaction();
                throw th2;
            }
        }
        if (i == 3) {
            sQLiteDatabase.beginTransaction();
            try {
                List<b> a2 = new com.miui.antispam.db.a.c().a(sQLiteDatabase);
                for (int i3 = 0; i3 < a2.size(); i3++) {
                    String b2 = a2.get(i3).b();
                    sQLiteDatabase.execSQL("UPDATE phone_list SET number = '" + n.f(b2) + "' WHERE number = '" + b2 + "'");
                }
                sQLiteDatabase.setTransactionSuccessful();
                sQLiteDatabase.endTransaction();
                i = 4;
            } catch (Exception e3) {
                Log.e("AntiSpamDB", "exception when update antispam DB ", e3);
                sQLiteDatabase.endTransaction();
            } catch (Throwable th3) {
                sQLiteDatabase.endTransaction();
                throw th3;
            }
        }
        if (i == 4) {
            sQLiteDatabase.beginTransaction();
            try {
                sQLiteDatabase.execSQL("ALTER TABLE keyword ADD COLUMN type INTEGER NOT NULL DEFAULT 1");
                sQLiteDatabase.execSQL("UPDATE keyword SET type = 1");
                sQLiteDatabase.execSQL("ALTER TABLE keyword ADD COLUMN cloudUid TEXT DEFAULT NULL");
                sQLiteDatabase.execSQL("ALTER TABLE phone_list ADD COLUMN cloudUid TEXT DEFAULT NULL");
                sQLiteDatabase.setTransactionSuccessful();
                sQLiteDatabase.endTransaction();
                i = 5;
            } catch (Exception e4) {
                Log.e("AntiSpamDB", "exception when update antispam DB ", e4);
                sQLiteDatabase.endTransaction();
            } catch (Throwable th4) {
                sQLiteDatabase.endTransaction();
                throw th4;
            }
        }
        if (i == 5) {
            sQLiteDatabase.beginTransaction();
            try {
                sQLiteDatabase.execSQL("ALTER TABLE phone_list ADD COLUMN sync_dirty INTEGER NOT NULL DEFAULT 0");
                sQLiteDatabase.execSQL("ALTER TABLE phone_list ADD COLUMN e_tag TEXT DEFAULT NULL");
                sQLiteDatabase.execSQL("ALTER TABLE phone_list ADD COLUMN record_id TEXT DEFAULT NULL");
                sQLiteDatabase.execSQL("ALTER TABLE phone_list ADD COLUMN location INTEGER NOT NULL DEFAULT 0");
                sQLiteDatabase.setTransactionSuccessful();
                sQLiteDatabase.endTransaction();
                i = 6;
            } catch (Exception e5) {
                Log.e("AntiSpamDB", "exception when update antispam DB ", e5);
                sQLiteDatabase.endTransaction();
            } catch (Throwable th5) {
                sQLiteDatabase.endTransaction();
                throw th5;
            }
        }
        if (i == 6) {
            sQLiteDatabase.beginTransaction();
            try {
                sQLiteDatabase.execSQL("ALTER TABLE fwlog ADD COLUMN callType INTEGER NOT NULL DEFAULT 0");
                sQLiteDatabase.setTransactionSuccessful();
                sQLiteDatabase.endTransaction();
                i = 7;
            } catch (Exception e6) {
                Log.e("AntiSpamDB", "exception when update antispam DB ", e6);
                sQLiteDatabase.endTransaction();
            } catch (Throwable th6) {
                sQLiteDatabase.endTransaction();
                throw th6;
            }
        }
        if (i == 7) {
            sQLiteDatabase.beginTransaction();
            try {
                sQLiteDatabase.execSQL("ALTER TABLE phone_list ADD COLUMN sim_id INTEGER NOT NULL DEFAULT 1");
                sQLiteDatabase.execSQL("UPDATE phone_list SET sim_id = 1");
                sQLiteDatabase.execSQL("ALTER TABLE keyword ADD COLUMN sim_id INTEGER NOT NULL DEFAULT 1");
                sQLiteDatabase.execSQL("UPDATE keyword SET sim_id = 1");
                sQLiteDatabase.setTransactionSuccessful();
                sQLiteDatabase.endTransaction();
                i = 8;
            } catch (Exception e7) {
                Log.e("AntiSpamDB", "exception when update antispam DB ", e7);
                sQLiteDatabase.endTransaction();
            } catch (Throwable th7) {
                sQLiteDatabase.endTransaction();
                throw th7;
            }
        }
        if (i == 8) {
            try {
                a(this.f2349a);
                d.e(0);
                d.d(0);
                i = 9;
            } catch (Exception e8) {
                Log.e("AntiSpamDB", "exception when update antispam DB ", e8);
            }
        }
        if (i == 9) {
            try {
                b(this.f2349a);
            } catch (Exception e9) {
                Log.e("AntiSpamDB", "exception when update antispam DB ", e9);
            }
        }
    }
}
