package com.miui.antispam.policy.a;

import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.Log;
import android.util.SparseArray;
import b.b.a.a;
import b.b.a.e.n;
import com.google.android.exoplayer2.upstream.DataSchemeDataSource;
import com.miui.activityutil.o;
import com.miui.antispam.policy.BlackAddressPolicy;
import com.miui.maml.elements.AdvancedSlider;
import java.io.Closeable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import miui.cloud.common.XSimChangeNotification;
import miui.os.Build;
import miui.provider.ExtraTelephony;
import miui.telephony.PhoneNumberUtils;
import miui.util.IOUtils;

public class d {

    /* renamed from: a  reason: collision with root package name */
    private SparseArray<SparseArray<Map<String, Integer>>> f2364a;

    /* renamed from: b  reason: collision with root package name */
    private SparseArray<SparseArray<List<String>>> f2365b;

    /* renamed from: c  reason: collision with root package name */
    private Context f2366c;
    /* access modifiers changed from: private */

    /* renamed from: d  reason: collision with root package name */
    public AtomicBoolean f2367d = new AtomicBoolean(false);
    /* access modifiers changed from: private */
    public AtomicBoolean e = new AtomicBoolean(false);

    public d(Context context) {
        this.f2366c = context;
    }

    private boolean a(e eVar, int i) {
        int i2;
        int i3 = (i == a.c.f1311a || i == a.c.f1312b) ? eVar.f2370c : 1;
        if (b(eVar.f2369b, eVar.f2371d, i, i3) || a(eVar.f2368a, eVar.f2371d, i, i3) || c(eVar.f2369b, eVar.f2371d, i, i3) || e(eVar.f2369b, eVar.f2371d, i, i3)) {
            return true;
        }
        if ((i == a.c.f1311a || i == a.c.f1312b) && (i2 = eVar.f2371d) == 1) {
            return c(eVar.f2368a, i2, i, i3);
        }
        return false;
    }

    private static String c(String str) {
        return str.replace("*", "[\\s\\S]*").replace("#", "[\\s\\S]").replace("+", "\\+");
    }

    /* access modifiers changed from: private */
    public void c() {
        this.f2365b = new SparseArray<>();
        this.f2365b.put(1, new SparseArray());
        this.f2365b.put(4, new SparseArray());
        this.f2365b.put(2, new SparseArray());
        this.f2365b.put(3, new SparseArray());
        this.f2365b.get(1).put(1, Collections.synchronizedList(new ArrayList()));
        this.f2365b.get(1).put(2, Collections.synchronizedList(new ArrayList()));
        this.f2365b.get(4).put(1, Collections.synchronizedList(new ArrayList()));
        this.f2365b.get(4).put(2, Collections.synchronizedList(new ArrayList()));
        this.f2365b.get(2).put(1, Collections.synchronizedList(new ArrayList()));
        this.f2365b.get(3).put(1, Collections.synchronizedList(new ArrayList()));
        Cursor cursor = null;
        try {
            cursor = this.f2366c.getContentResolver().query(ExtraTelephony.Keyword.CONTENT_URI, new String[]{"type", DataSchemeDataSource.SCHEME_DATA, XSimChangeNotification.BROADCAST_EXTRA_KEY_SIM_ID}, (String) null, (String[]) null, (String) null);
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    String string = cursor.getString(cursor.getColumnIndex(DataSchemeDataSource.SCHEME_DATA));
                    int i = cursor.getInt(cursor.getColumnIndex("type"));
                    ((List) this.f2365b.get(i).get(cursor.getInt(cursor.getColumnIndex(XSimChangeNotification.BROADCAST_EXTRA_KEY_SIM_ID)))).add(string);
                }
            }
        } catch (Exception e2) {
            this.e.set(true);
            Log.e("Judge", "Exception when initKeywordList !", e2);
        } catch (Throwable th) {
            IOUtils.closeQuietly((Closeable) null);
            throw th;
        }
        IOUtils.closeQuietly(cursor);
        Log.i("Judge", "Loading AntiSpam KeywordList");
    }

    private String d(String str, int i, int i2) {
        for (String str2 : (List) this.f2365b.get(i).get(i2)) {
            if (str.toLowerCase().contains(str2.toLowerCase())) {
                return str2;
            }
        }
        return null;
    }

    /* access modifiers changed from: private */
    public void d() {
        this.f2364a = new SparseArray<>();
        this.f2364a.put(a.c.f1311a, new SparseArray());
        this.f2364a.put(a.c.f1312b, new SparseArray());
        this.f2364a.put(a.c.f1313c, new SparseArray());
        this.f2364a.put(a.c.f1314d, new SparseArray());
        this.f2364a.put(a.c.e, new SparseArray());
        this.f2364a.put(a.c.f, new SparseArray());
        this.f2364a.put(a.c.g, new SparseArray());
        this.f2364a.get(a.c.f1311a).put(1, Collections.synchronizedMap(new ArrayMap()));
        this.f2364a.get(a.c.f1311a).put(2, Collections.synchronizedMap(new ArrayMap()));
        this.f2364a.get(a.c.f1312b).put(1, Collections.synchronizedMap(new ArrayMap()));
        this.f2364a.get(a.c.f1312b).put(2, Collections.synchronizedMap(new ArrayMap()));
        this.f2364a.get(a.c.f1313c).put(1, Collections.synchronizedMap(new ArrayMap()));
        this.f2364a.get(a.c.f1314d).put(1, Collections.synchronizedMap(new ArrayMap()));
        this.f2364a.get(a.c.e).put(1, Collections.synchronizedMap(new ArrayMap()));
        this.f2364a.get(a.c.f).put(1, Collections.synchronizedMap(new ArrayMap()));
        this.f2364a.get(a.c.g).put(1, Collections.synchronizedMap(new ArrayMap()));
        Cursor cursor = null;
        try {
            cursor = this.f2366c.getContentResolver().query(ExtraTelephony.Phonelist.CONTENT_URI, new String[]{"type", AdvancedSlider.STATE, "number", XSimChangeNotification.BROADCAST_EXTRA_KEY_SIM_ID}, "sync_dirty<> ? ", new String[]{String.valueOf(1)}, (String) null);
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    String string = cursor.getString(cursor.getColumnIndex("number"));
                    int i = cursor.getInt(cursor.getColumnIndex("type"));
                    int i2 = cursor.getInt(cursor.getColumnIndex(AdvancedSlider.STATE));
                    int i3 = cursor.getInt(cursor.getColumnIndex(XSimChangeNotification.BROADCAST_EXTRA_KEY_SIM_ID));
                    if (Build.IS_INTERNATIONAL_BUILD && string.startsWith(o.f2309a)) {
                        string = string.substring(1);
                    }
                    ((Map) this.f2364a.get(i).get(i3)).put(string, Integer.valueOf(i2));
                }
            }
        } catch (Exception e2) {
            this.e.set(true);
            Log.e("Judge", "Exception when initPhoneList !", e2);
        } catch (Throwable th) {
            IOUtils.closeQuietly((Closeable) null);
            throw th;
        }
        IOUtils.closeQuietly(cursor);
        Log.i("Judge", "Loading AntiSpam PhoneList");
    }

    private boolean e(String str, int i, int i2, int i3) {
        for (Map.Entry entry : ((Map) this.f2364a.get(i2).get(i3)).entrySet()) {
            int intValue = ((Integer) entry.getValue()).intValue();
            if ((intValue == 0 || intValue == i) && str.matches(c((String) entry.getKey()))) {
                return true;
            }
        }
        return false;
    }

    public String a(String str) {
        return d(str, 2, 1);
    }

    public String a(String str, int i) {
        return d(str, 1, i);
    }

    public void a() {
        new c(this).start();
    }

    public void a(String str, int i, int i2) {
        if (this.f2367d.get()) {
            ((List) this.f2365b.get(i).get(i2)).remove(str);
            if (a.f1308a) {
                Log.i("Judge", "delete keyword : data = " + str);
            }
        }
    }

    public boolean a(e eVar) {
        return b(eVar.f2369b, eVar.f2371d, a.c.f1311a, eVar.f2370c);
    }

    public boolean a(String str, int i, int i2, int i3) {
        String countryCode = PhoneNumberUtils.PhoneNumber.parse(str).getCountryCode();
        if (!TextUtils.isEmpty(countryCode)) {
            if (b("***" + countryCode + o.f2309a, i, i2, i3)) {
                return true;
            }
        }
        String locationAreaCode = PhoneNumberUtils.PhoneNumber.getLocationAreaCode(this.f2366c, str);
        if (BlackAddressPolicy.a.a(locationAreaCode)) {
            locationAreaCode = BlackAddressPolicy.a.a(this.f2366c, str, locationAreaCode);
        }
        Log.w("Judge", "number = " + n.a(str) + "; area = " + locationAreaCode);
        return b("***" + locationAreaCode, i, i2, i3);
    }

    public String b(String str) {
        return d(str, 3, 1);
    }

    public String b(String str, int i) {
        return d(str, 4, i);
    }

    public void b(String str, int i, int i2) {
        if (this.f2367d.get()) {
            List list = (List) this.f2365b.get(i).get(i2);
            if (!list.contains(str)) {
                list.add(str);
            }
            if (a.f1308a) {
                Log.i("Judge", "insert keyword : data = " + str);
            }
        }
    }

    public boolean b() {
        return this.f2367d.get();
    }

    public boolean b(e eVar) {
        return a(eVar, a.c.f1314d);
    }

    public boolean b(String str, int i, int i2, int i3) {
        Map map = (Map) this.f2364a.get(i2).get(i3);
        if (!map.containsKey(str)) {
            return false;
        }
        int intValue = ((Integer) map.get(str)).intValue();
        return intValue == 0 || intValue == i;
    }

    public void c(String str, int i, int i2) {
        if (this.f2367d.get()) {
            if (Build.IS_INTERNATIONAL_BUILD && str.startsWith(o.f2309a)) {
                str = str.substring(1);
            }
            ((Map) this.f2364a.get(i).get(i2)).remove(str);
            if (a.f1308a) {
                Log.i("Judge", "delete phone list : number = " + str);
            }
        }
    }

    public boolean c(e eVar) {
        return a(eVar, a.c.e);
    }

    public boolean c(String str, int i, int i2, int i3) {
        String str2 = "";
        for (int i4 = 0; i4 < str.length(); i4++) {
            str2 = str2 + str.charAt(i4);
            if (b(str2 + "*", i, i2, i3)) {
                return true;
            }
        }
        return false;
    }

    public void d(String str, int i, int i2, int i3) {
        if (this.f2367d.get()) {
            if (Build.IS_INTERNATIONAL_BUILD && str.startsWith(o.f2309a)) {
                str = str.substring(1);
            }
            ((Map) this.f2364a.get(i2).get(i3)).put(str, Integer.valueOf(i));
            if (a.f1308a) {
                Log.i("Judge", "insert phone list : number = " + str);
            }
        }
    }

    public boolean d(e eVar) {
        return a(eVar, a.c.f);
    }

    public boolean e(e eVar) {
        return a(eVar, a.c.g);
    }

    public boolean f(e eVar) {
        return a(eVar, a.c.f1312b);
    }
}
