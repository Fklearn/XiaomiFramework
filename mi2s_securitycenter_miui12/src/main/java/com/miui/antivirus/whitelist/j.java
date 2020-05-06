package com.miui.antivirus.whitelist;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;
import b.b.b.d.i;
import b.b.b.d.n;
import b.b.b.o;
import b.b.c.j.x;
import com.miui.antivirus.model.e;
import com.miui.securitycenter.R;
import java.io.Closeable;
import java.util.ArrayList;
import java.util.List;
import miui.util.IOUtils;

public class j {

    /* renamed from: a  reason: collision with root package name */
    private static j f3046a;

    /* renamed from: b  reason: collision with root package name */
    private static ContentResolver f3047b;

    /* renamed from: c  reason: collision with root package name */
    private Context f3048c;

    private class a extends AsyncTask<Void, Void, Void> {

        /* renamed from: a  reason: collision with root package name */
        private e f3049a;

        public a(e eVar) {
            this.f3049a = eVar;
        }

        /* access modifiers changed from: protected */
        /* renamed from: a */
        public Void doInBackground(Void... voidArr) {
            String a2 = n.a(this.f3049a.q());
            if (a2 == null) {
                a2 = this.f3049a.l();
            }
            String str = a2;
            if (str == null) {
                return null;
            }
            j.this.a(this.f3049a.o() == o.f.INSTALLED_APP ? "INSTALLED_APP" : "UNINSTALLED_APK", this.f3049a.p() == o.g.RISK ? "riskapp" : "trojan", this.f3049a.h(), this.f3049a.t(), this.f3049a.q(), this.f3049a.m(), this.f3049a.u(), str);
            return null;
        }

        /* access modifiers changed from: protected */
        /* renamed from: a */
        public void onPostExecute(Void voidR) {
        }
    }

    public static class b {

        /* renamed from: a  reason: collision with root package name */
        public String f3051a;

        /* renamed from: b  reason: collision with root package name */
        public String f3052b;

        /* renamed from: c  reason: collision with root package name */
        public String f3053c;

        /* renamed from: d  reason: collision with root package name */
        public String f3054d;
        public String e;
        public String f;
        public String g;
        public String h;
    }

    public static class c {

        /* renamed from: a  reason: collision with root package name */
        public String f3055a;

        /* renamed from: b  reason: collision with root package name */
        public String f3056b;

        /* renamed from: c  reason: collision with root package name */
        public String f3057c;

        /* renamed from: d  reason: collision with root package name */
        public String f3058d;
        public String e;
        public String f;
        public String g;
        public String h;
    }

    private j(Context context) {
        this.f3048c = context;
        f3047b = context.getApplicationContext().getContentResolver();
    }

    public static synchronized j a(Context context) {
        j jVar;
        synchronized (j.class) {
            if (f3046a == null) {
                f3046a = new j(context.getApplicationContext());
            }
            jVar = f3046a;
        }
        return jVar;
    }

    private static boolean c(e eVar) {
        String a2 = i.a(eVar.q());
        if (a2 == null) {
            return false;
        }
        String[] strArr = {a2};
        Cursor cursor = null;
        try {
            cursor = f3047b.query(f.f3036a, (String[]) null, "virus_md5=?", strArr, (String) null);
            if (cursor != null && cursor.moveToFirst()) {
                IOUtils.closeQuietly(cursor);
                return true;
            }
        } catch (Exception e) {
            Log.e("WhiteListManager", "msg", e);
        } catch (Throwable th) {
            IOUtils.closeQuietly((Closeable) null);
            throw th;
        }
        IOUtils.closeQuietly(cursor);
        return false;
    }

    public int a(String[] strArr) {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("dir_path = ? ");
        for (int i = 1; i < strArr.length; i++) {
            stringBuffer.append(" or dir_path = ? ");
        }
        return f3047b.delete(f.f3036a, stringBuffer.toString(), strArr);
    }

    public long a(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("scan_result_type", str2);
        contentValues.put("scan_item_type", str);
        contentValues.put("app_label", str3);
        contentValues.put("desc", str4);
        contentValues.put("dir_path", str5);
        contentValues.put("pkg_name", str6);
        contentValues.put("virus_name", str7);
        contentValues.put("virus_md5", str8);
        f3047b.insert(f.f3036a, contentValues);
        return -1;
    }

    public List<b> a() {
        ArrayList arrayList = new ArrayList();
        Cursor cursor = null;
        try {
            cursor = f3047b.query(f.f3036a, (String[]) null, "scan_result_type=?", new String[]{"riskapp"}, (String) null);
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    b bVar = new b();
                    bVar.f3053c = cursor.getString(cursor.getColumnIndex("app_label"));
                    bVar.f3054d = cursor.getString(cursor.getColumnIndex("desc"));
                    bVar.e = cursor.getString(cursor.getColumnIndex("dir_path"));
                    bVar.f = cursor.getString(cursor.getColumnIndex("pkg_name"));
                    bVar.g = cursor.getString(cursor.getColumnIndex("virus_name"));
                    bVar.f3051a = cursor.getString(cursor.getColumnIndex("scan_item_type"));
                    bVar.f3052b = cursor.getString(cursor.getColumnIndex("scan_result_type"));
                    bVar.h = cursor.getString(cursor.getColumnIndex("virus_md5"));
                    if (x.h(this.f3048c, bVar.f) || bVar.h.equals(i.a(bVar.e))) {
                        arrayList.add(bVar);
                    }
                }
            }
        } catch (Exception e) {
            Log.e("WhiteListManager", "msg", e);
        } catch (Throwable th) {
            IOUtils.closeQuietly((Closeable) null);
            throw th;
        }
        IOUtils.closeQuietly(cursor);
        return arrayList;
    }

    public void a(e eVar) {
        if (eVar != null) {
            if (c(eVar)) {
                Toast.makeText(this.f3048c, R.string.toast_add_virus_white_list_no_need_repeat, 0).show();
            } else {
                new a(eVar).execute(new Void[0]);
            }
        }
    }

    public List<c> b() {
        ArrayList arrayList = new ArrayList();
        Cursor cursor = null;
        try {
            cursor = f3047b.query(f.f3036a, (String[]) null, "scan_result_type=?", new String[]{"trojan"}, (String) null);
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    c cVar = new c();
                    cVar.f3057c = cursor.getString(cursor.getColumnIndex("app_label"));
                    cVar.f3058d = cursor.getString(cursor.getColumnIndex("desc"));
                    cVar.e = cursor.getString(cursor.getColumnIndex("dir_path"));
                    cVar.f = cursor.getString(cursor.getColumnIndex("pkg_name"));
                    cVar.g = cursor.getString(cursor.getColumnIndex("virus_name"));
                    cVar.f3055a = cursor.getString(cursor.getColumnIndex("scan_item_type"));
                    cVar.f3056b = cursor.getString(cursor.getColumnIndex("scan_result_type"));
                    cVar.h = cursor.getString(cursor.getColumnIndex("virus_md5"));
                    if (x.h(this.f3048c, cVar.f) || cVar.h.equals(i.a(cVar.e))) {
                        arrayList.add(cVar);
                    }
                }
            }
        } catch (Exception e) {
            Log.e("WhiteListManager", "msg", e);
        } catch (Throwable th) {
            IOUtils.closeQuietly((Closeable) null);
            throw th;
        }
        IOUtils.closeQuietly(cursor);
        return arrayList;
    }

    public void b(e eVar) {
        if (eVar != null && !c(eVar)) {
            new a(eVar).execute(new Void[0]);
        }
    }

    public int c() {
        Long l = new Long(0);
        Cursor cursor = null;
        try {
            cursor = f3047b.query(f.f3036a, (String[]) null, (String) null, (String[]) null, (String) null);
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    String string = cursor.getString(cursor.getColumnIndex("pkg_name"));
                    String string2 = cursor.getString(cursor.getColumnIndex("virus_md5"));
                    String string3 = cursor.getString(cursor.getColumnIndex("dir_path"));
                    if (x.h(this.f3048c, string) || string2.equals(i.a(string3))) {
                        l = Long.valueOf(l.longValue() + 1);
                    }
                }
            }
        } catch (Exception e) {
            Log.e("WhiteListManager", "msg", e);
        } catch (Throwable th) {
            IOUtils.closeQuietly((Closeable) null);
            throw th;
        }
        IOUtils.closeQuietly(cursor);
        return l.intValue();
    }
}
