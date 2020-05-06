package com.miui.securitycenter.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import com.miui.securitycenter.a.a;
import com.miui.securitycenter.a.b;
import com.miui.securityscan.M;
import java.util.ArrayList;

public class ThirdMonitorProvider extends ContentProvider {

    /* renamed from: a  reason: collision with root package name */
    private static ArrayList<String> f7510a = new ArrayList<>();

    /* renamed from: b  reason: collision with root package name */
    private b f7511b;

    /* renamed from: c  reason: collision with root package name */
    private int f7512c;

    /* renamed from: d  reason: collision with root package name */
    private ArrayList<String> f7513d;
    private ArrayList<String> e;
    private ArrayList<String> f;
    private ArrayList<String> g;

    static {
        f7510a.add("com.miui.home");
        f7510a.add("com.jeejen.family");
        f7510a.add("com.jeejen.family.miui");
        f7510a.add("com.mi.android.globallauncher");
    }

    private void a() {
        ArrayList<String> arrayList;
        String string;
        SQLiteDatabase writableDatabase = this.f7511b.getWritableDatabase();
        SQLiteQueryBuilder sQLiteQueryBuilder = new SQLiteQueryBuilder();
        sQLiteQueryBuilder.setTables("THIRD_DESKTOP");
        Cursor query = sQLiteQueryBuilder.query(writableDatabase, new String[]{"package_name", "type"}, (String) null, (String[]) null, (String) null, (String) null, (String) null);
        this.g.clear();
        this.f.clear();
        if (query != null) {
            while (query.moveToNext()) {
                try {
                    int i = query.getInt(query.getColumnIndex("type"));
                    if (i == 3) {
                        arrayList = this.g;
                        string = query.getString(query.getColumnIndex("package_name"));
                    } else if (i == 2) {
                        arrayList = this.f;
                        string = query.getString(query.getColumnIndex("package_name"));
                    }
                    arrayList.add(string);
                } catch (Exception e2) {
                    Log.e("TDProvider", "cursor exception!", e2);
                } catch (Throwable th) {
                    query.close();
                    throw th;
                }
            }
            query.close();
        }
    }

    public int bulkInsert(Uri uri, ContentValues[] contentValuesArr) {
        int i;
        SQLiteDatabase writableDatabase = this.f7511b.getWritableDatabase();
        writableDatabase.beginTransaction();
        int i2 = 0;
        try {
            delete((Uri) null, (String) null, (String[]) null);
            int length = contentValuesArr.length;
            i = 0;
            while (i2 < length) {
                try {
                    insert(uri, contentValuesArr[i2]);
                    i++;
                    i2++;
                } catch (Exception e2) {
                    e = e2;
                    try {
                        Log.e("TDProvider", "bulk insert exception!", e);
                        writableDatabase.endTransaction();
                        return i;
                    } catch (Throwable th) {
                        writableDatabase.endTransaction();
                        throw th;
                    }
                }
            }
            writableDatabase.setTransactionSuccessful();
            this.f7512c = M.i();
        } catch (Exception e3) {
            e = e3;
            i = 0;
            Log.e("TDProvider", "bulk insert exception!", e);
            writableDatabase.endTransaction();
            return i;
        }
        writableDatabase.endTransaction();
        return i;
    }

    public Bundle call(String str, String str2, Bundle bundle) {
        ArrayList<String> arrayList;
        ArrayList<String> arrayList2;
        String string;
        Bundle bundle2 = new Bundle();
        if ("getModeAndList".equals(str)) {
            bundle2.putInt("mode", this.f7512c);
            if (this.f7513d.size() != 0 || this.e.size() > f7510a.size()) {
                bundle2.putStringArrayList("list", this.f7512c == 1 ? this.f7513d : this.e);
                return bundle2;
            }
            SQLiteDatabase writableDatabase = this.f7511b.getWritableDatabase();
            SQLiteQueryBuilder sQLiteQueryBuilder = new SQLiteQueryBuilder();
            sQLiteQueryBuilder.setTables("THIRD_DESKTOP");
            Cursor query = sQLiteQueryBuilder.query(writableDatabase, new String[]{"package_name", "type"}, (String) null, (String[]) null, (String) null, (String) null, (String) null);
            if (query != null) {
                while (query.moveToNext()) {
                    try {
                        if (query.getInt(query.getColumnIndex("type")) == 1) {
                            arrayList2 = this.f7513d;
                            string = query.getString(query.getColumnIndex("package_name"));
                        } else if (query.getInt(query.getColumnIndex("type")) == 0) {
                            arrayList2 = this.e;
                            string = query.getString(query.getColumnIndex("package_name"));
                        }
                        arrayList2.add(string);
                    } catch (Exception e2) {
                        Log.e("TDProvider", "cursor exception!", e2);
                    } catch (Throwable th) {
                        query.close();
                        throw th;
                    }
                }
                bundle2.putStringArrayList("list", this.f7512c == 1 ? this.f7513d : this.e);
                query.close();
            }
        } else {
            if ("getListForDescControl".equals(str)) {
                if (this.f.size() == 0) {
                    a();
                }
                arrayList = this.f;
            } else if ("getListForCTAEnable".equals(str)) {
                if (this.g.size() == 0) {
                    a();
                }
                arrayList = this.g;
            }
            bundle2.putStringArrayList("list", arrayList);
        }
        return bundle2;
    }

    public int delete(Uri uri, String str, String[] strArr) {
        this.f7511b.getWritableDatabase().delete("THIRD_DESKTOP", (String) null, (String[]) null);
        this.f7513d.clear();
        this.e.clear();
        this.e.addAll(f7510a);
        this.f.clear();
        this.g.clear();
        return 0;
    }

    public String getType(Uri uri) {
        return null;
    }

    public Uri insert(Uri uri, ContentValues contentValues) {
        ArrayList<String> arrayList;
        this.f7511b.getWritableDatabase().insert("THIRD_DESKTOP", (String) null, contentValues);
        if (contentValues.getAsInteger("type").intValue() == 1) {
            arrayList = this.f7513d;
        } else if (contentValues.getAsInteger("type").intValue() == 0) {
            arrayList = this.e;
        } else if (contentValues.getAsInteger("type").intValue() == 2) {
            arrayList = this.f;
        } else {
            if (contentValues.getAsInteger("type").intValue() == 3) {
                arrayList = this.g;
            }
            return null;
        }
        arrayList.add(contentValues.getAsString("package_name"));
        return null;
    }

    public boolean onCreate() {
        this.f7511b = a.a(getContext());
        this.f7512c = 0;
        try {
            this.f7512c = M.i();
        } catch (Exception e2) {
            Log.e("TDProvider", "call remote exception: " + e2);
        }
        this.f7513d = new ArrayList<>();
        this.e = new ArrayList<>();
        this.e.addAll(f7510a);
        this.f = new ArrayList<>();
        this.g = new ArrayList<>();
        return true;
    }

    public Cursor query(Uri uri, String[] strArr, String str, String[] strArr2, String str2) {
        return null;
    }

    public int update(Uri uri, ContentValues contentValues, String str, String[] strArr) {
        return 0;
    }
}
