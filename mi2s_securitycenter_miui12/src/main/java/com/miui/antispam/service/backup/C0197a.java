package com.miui.antispam.service.backup;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.CancellationSignal;
import android.text.TextUtils;
import b.b.a.e.c;
import com.google.android.exoplayer2.upstream.DataSchemeDataSource;
import com.miui.activityutil.o;
import com.miui.antispam.db.d;
import com.miui.antispam.service.backup.B;
import com.miui.antispam.service.backup.C0203g;
import com.miui.antispam.service.backup.C0206j;
import com.miui.antispam.service.backup.E;
import com.miui.antispam.service.backup.m;
import com.miui.antispam.service.backup.p;
import com.miui.antispam.service.backup.s;
import com.miui.antispam.service.backup.v;
import com.miui.antispam.service.backup.y;
import com.miui.maml.elements.AdvancedSlider;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Vector;
import miui.cloud.common.XSimChangeNotification;
import miui.provider.ExtraTelephony;
import miui.telephony.PhoneNumberUtils;
import miui.util.IOUtils;

/* renamed from: com.miui.antispam.service.backup.a  reason: case insensitive filesystem */
public class C0197a {

    /* renamed from: a  reason: collision with root package name */
    private Context f2439a;

    /* renamed from: b  reason: collision with root package name */
    protected ContentResolver f2440b;

    /* renamed from: c  reason: collision with root package name */
    private HashMap<Integer, HashSet<String>> f2441c = new HashMap<>();

    /* renamed from: d  reason: collision with root package name */
    private HashMap<Integer, HashSet<String>> f2442d;
    private HashMap<Integer, HashSet<String>> e;
    private HashMap<Integer, HashSet<String>> f;
    private HashMap<Integer, HashMap<String, Integer>> g;

    public C0197a(Context context) {
        this.f2439a = context;
        this.f2440b = context.getContentResolver();
        this.f2441c.put(1, new HashSet());
        this.f2441c.put(2, new HashSet());
        this.f2442d = new HashMap<>();
        this.f2442d.put(1, new HashSet());
        this.f2442d.put(2, new HashSet());
        this.e = new HashMap<>();
        this.e.put(1, new HashSet());
        this.e.put(2, new HashSet());
        this.f = new HashMap<>();
        this.f.put(1, new HashSet());
        this.f.put(2, new HashSet());
        this.g = new HashMap<>();
        this.g.put(1, new HashMap());
        this.g.put(2, new HashMap());
        a(this.f2441c, ExtraTelephony.Phonelist.CONTENT_URI, new String[]{"number", XSimChangeNotification.BROADCAST_EXTRA_KEY_SIM_ID}, "type = ? AND sync_dirty <> ? ", new String[]{o.f2310b, String.valueOf(1)});
        a(this.f2442d, ExtraTelephony.Phonelist.CONTENT_URI, new String[]{"number", XSimChangeNotification.BROADCAST_EXTRA_KEY_SIM_ID}, "type = ? AND sync_dirty <> ? ", new String[]{"2", String.valueOf(1)});
        a(this.e, 1);
        a(this.f, 4);
        j();
    }

    private void a(HashMap<Integer, HashSet<String>> hashMap, int i) {
        Cursor cursor = null;
        try {
            cursor = this.f2440b.query(ExtraTelephony.Keyword.CONTENT_URI, new String[]{DataSchemeDataSource.SCHEME_DATA, XSimChangeNotification.BROADCAST_EXTRA_KEY_SIM_ID}, "type=?", new String[]{String.valueOf(i)}, (String) null);
            while (cursor != null && cursor.moveToNext()) {
                hashMap.get(Integer.valueOf(cursor.getInt(cursor.getColumnIndex(XSimChangeNotification.BROADCAST_EXTRA_KEY_SIM_ID)))).add(cursor.getString(cursor.getColumnIndex(DataSchemeDataSource.SCHEME_DATA)));
            }
        } finally {
            IOUtils.closeQuietly(cursor);
        }
    }

    private void a(HashMap<Integer, HashSet<String>> hashMap, Uri uri, String[] strArr, String str, String[] strArr2) {
        Cursor cursor = null;
        try {
            cursor = this.f2440b.query(uri, strArr, str, strArr2, (String) null);
            while (cursor != null && cursor.moveToNext()) {
                hashMap.get(Integer.valueOf(cursor.getInt(cursor.getColumnIndex(strArr[1])))).add(cursor.getString(cursor.getColumnIndex(strArr[0])));
            }
        } finally {
            IOUtils.closeQuietly(cursor);
        }
    }

    private void j() {
        int a2 = d.a(this.f2439a, "stranger_sms_mode", 1, 1);
        int a3 = d.a(this.f2439a, "stranger_call_mode", 1, 1);
        int a4 = d.a(this.f2439a, "contact_sms_mode", 1, 1);
        int a5 = d.a(this.f2439a, "contact_call_mode", 1, 1);
        int a6 = d.a(this.f2439a, "service_sms_mode", 1, 1);
        int a7 = d.a(this.f2439a, "empty_call_mode", 1, 1);
        int a8 = d.a(this.f2439a, "oversea_call_mode", 1, 1);
        int i = a6;
        int a9 = d.a(this.f2439a, "mms_mode", 1, 2);
        boolean z = !d.b(this.f2439a, 1);
        boolean z2 = !d.a(this.f2439a, 1);
        boolean z3 = !d.d(this.f2439a, 1);
        boolean z4 = !d.c(this.f2439a, 1);
        int a10 = d.a(this.f2439a, "stranger_sms_mode", 2, 1);
        int a11 = d.a(this.f2439a, "stranger_call_mode", 2, 1);
        int a12 = d.a(this.f2439a, "contact_sms_mode", 2, 1);
        int a13 = d.a(this.f2439a, "contact_call_mode", 2, 1);
        int a14 = d.a(this.f2439a, "service_sms_mode", 2, 1);
        int a15 = d.a(this.f2439a, "empty_call_mode", 2, 1);
        int a16 = d.a(this.f2439a, "oversea_call_mode", 2, 1);
        boolean z5 = !d.b(this.f2439a, 2);
        boolean z6 = !d.a(this.f2439a, 2);
        boolean z7 = !d.d(this.f2439a, 2);
        this.g.get(1).put("stranger_sms_mode", Integer.valueOf(a2));
        this.g.get(1).put("stranger_call_mode", Integer.valueOf(a3));
        this.g.get(1).put("contact_sms_mode", Integer.valueOf(a4));
        this.g.get(1).put("contact_call_mode", Integer.valueOf(a5));
        this.g.get(1).put("service_sms_mode", Integer.valueOf(i));
        this.g.get(1).put("empty_call_mode", Integer.valueOf(a7));
        this.g.get(1).put("oversea_call_mode", Integer.valueOf(a8));
        this.g.get(1).put("mms_mode", Integer.valueOf(a9));
        this.g.get(1).put("is_call_transfer_blocked", Integer.valueOf(d.b(1) ^ true ? 1 : 0));
        this.g.get(1).put("is_repeated_marked_number_permit", Integer.valueOf(d.c(1) ^ true ? 1 : 0));
        this.g.get(1).put("fraud_num_state", Integer.valueOf(z ? 1 : 0));
        this.g.get(1).put("agent_num_state", Integer.valueOf(z2 ? 1 : 0));
        this.g.get(1).put("sell_num_state", Integer.valueOf(z3 ? 1 : 0));
        this.g.get(1).put("harass_num_state", Integer.valueOf(z4 ? 1 : 0));
        this.g.get(2).put("stranger_sms_mode", Integer.valueOf(a10));
        this.g.get(2).put("stranger_call_mode", Integer.valueOf(a11));
        this.g.get(2).put("contact_sms_mode", Integer.valueOf(a12));
        this.g.get(2).put("contact_call_mode", Integer.valueOf(a13));
        this.g.get(2).put("service_sms_mode", Integer.valueOf(a14));
        this.g.get(2).put("empty_call_mode", Integer.valueOf(a15));
        this.g.get(2).put("oversea_call_mode", Integer.valueOf(a16));
        this.g.get(2).put("mms_mode", Integer.valueOf(d.a(this.f2439a, "mms_mode", 2, 2)));
        this.g.get(2).put("is_call_transfer_blocked", Integer.valueOf(d.b(2) ^ true ? 1 : 0));
        this.g.get(2).put("is_repeated_marked_number_permit", Integer.valueOf(d.c(2) ^ true ? 1 : 0));
        this.g.get(2).put("fraud_num_state", Integer.valueOf(z5 ? 1 : 0));
        this.g.get(2).put("agent_num_state", Integer.valueOf(z6 ? 1 : 0));
        this.g.get(2).put("sell_num_state", Integer.valueOf(z7 ? 1 : 0));
        this.g.get(2).put("harass_num_state", Integer.valueOf(d.c(this.f2439a, 2) ^ true ? 1 : 0));
    }

    public Vector<C0206j> a() {
        Vector<C0206j> vector = new Vector<>();
        Cursor cursor = null;
        try {
            cursor = this.f2440b.query(ExtraTelephony.Phonelist.CONTENT_URI, new String[]{"number", "notes", AdvancedSlider.STATE, XSimChangeNotification.BROADCAST_EXTRA_KEY_SIM_ID}, "type = ? AND sync_dirty <> ? ", new String[]{o.f2310b, String.valueOf(1)}, (String) null);
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    String string = cursor.getString(cursor.getColumnIndex("number"));
                    String string2 = cursor.getString(cursor.getColumnIndex("notes"));
                    int i = cursor.getInt(cursor.getColumnIndex(AdvancedSlider.STATE));
                    int i2 = cursor.getInt(cursor.getColumnIndex(XSimChangeNotification.BROADCAST_EXTRA_KEY_SIM_ID));
                    C0206j.a p = C0206j.p();
                    p.b(string);
                    p.b(i);
                    p.a(i2);
                    if (!TextUtils.isEmpty(string2)) {
                        p.a(string2);
                    }
                    vector.add(p.build());
                }
            }
            return vector;
        } finally {
            IOUtils.closeQuietly(cursor);
        }
    }

    public void a(B b2) {
        if (b2 != null) {
            boolean e2 = b2.e();
            c.a(this.f2439a, b2.d(), e2);
        }
    }

    public void a(C0203g gVar) {
        if (gVar != null) {
            c.d(this.f2439a, gVar.d());
        }
    }

    public void a(p pVar) {
        if (pVar != null) {
            c.a(this.f2439a, pVar.e(), pVar.d());
        }
    }

    public void a(v vVar) {
        if (vVar != null) {
            c.b(this.f2439a, "mark_guide_fraud", vVar.c());
            c.b(this.f2439a, "mark_guide_agent", vVar.a());
            c.b(this.f2439a, "mark_guide_sell", vVar.f());
        }
    }

    public void a(y yVar) {
        if (yVar != null) {
            c.a(this.f2439a, yVar.d());
        }
    }

    public boolean a(E e2) {
        String g2 = e2.g();
        String e3 = e2.e();
        int j = e2.j();
        int i = e2.i();
        if (TextUtils.isEmpty(g2)) {
            return false;
        }
        String dialableNumber = PhoneNumberUtils.PhoneNumber.getDialableNumber(g2);
        if (this.f2442d.get(Integer.valueOf(i)).contains(dialableNumber)) {
            return false;
        }
        ContentValues contentValues = new ContentValues();
        contentValues.put("number", dialableNumber);
        contentValues.put("notes", e3);
        contentValues.put(AdvancedSlider.STATE, Integer.valueOf(j));
        contentValues.put(XSimChangeNotification.BROADCAST_EXTRA_KEY_SIM_ID, Integer.valueOf(i));
        contentValues.put("type", "2");
        this.f2440b.insert(ExtraTelephony.Phonelist.CONTENT_URI, contentValues);
        return true;
    }

    public boolean a(C0206j jVar) {
        String f2 = jVar.f();
        String d2 = jVar.d();
        int i = jVar.i();
        int h = jVar.h();
        if (TextUtils.isEmpty(f2)) {
            return false;
        }
        String dialableNumber = PhoneNumberUtils.PhoneNumber.getDialableNumber(f2);
        if (this.f2441c.get(Integer.valueOf(h)).contains(dialableNumber)) {
            return false;
        }
        ContentValues contentValues = new ContentValues();
        contentValues.put("number", dialableNumber);
        contentValues.put("notes", d2);
        contentValues.put(AdvancedSlider.STATE, Integer.valueOf(i));
        contentValues.put(XSimChangeNotification.BROADCAST_EXTRA_KEY_SIM_ID, Integer.valueOf(h));
        contentValues.put("type", o.f2310b);
        this.f2440b.insert(ExtraTelephony.Phonelist.CONTENT_URI, contentValues);
        return true;
    }

    public boolean a(m mVar) {
        String c2 = mVar.c();
        int f2 = mVar.f();
        int g2 = mVar.g();
        if (TextUtils.isEmpty(c2)) {
            return false;
        }
        if (1 == g2 && this.e.get(Integer.valueOf(f2)).contains(c2)) {
            return false;
        }
        if (4 == g2 && this.f.get(Integer.valueOf(f2)).contains(c2)) {
            return false;
        }
        ContentValues contentValues = new ContentValues();
        contentValues.put(DataSchemeDataSource.SCHEME_DATA, c2);
        contentValues.put(XSimChangeNotification.BROADCAST_EXTRA_KEY_SIM_ID, Integer.valueOf(f2));
        contentValues.put("type", Integer.valueOf(g2));
        this.f2440b.insert(ExtraTelephony.Keyword.CONTENT_URI, contentValues);
        return true;
    }

    public boolean a(s sVar) {
        int f2 = sVar.f();
        String d2 = sVar.d();
        int g2 = sVar.g();
        boolean z = false;
        if (TextUtils.isEmpty(d2)) {
            return false;
        }
        if (this.g.containsKey(Integer.valueOf(f2)) && this.g.get(Integer.valueOf(f2)).containsKey(d2) && ((Integer) this.g.get(Integer.valueOf(f2)).get(d2)).intValue() == g2) {
            return false;
        }
        if ("stranger_sms_mode".equals(d2) || "stranger_call_mode".equals(d2) || "contact_sms_mode".equals(d2) || "contact_call_mode".equals(d2) || "service_sms_mode".equals(d2) || "empty_call_mode".equals(d2) || "oversea_call_mode".equals(d2) || "mms_mode".equals(d2)) {
            d.b(this.f2439a, d2, f2, g2);
        } else if ("is_call_transfer_blocked".equals(d2)) {
            if (g2 == 0) {
                z = true;
            }
            d.a(f2, z);
        } else if ("is_repeated_marked_number_permit".equals(d2)) {
            if (g2 == 0) {
                z = true;
            }
            d.b(f2, z);
        } else if ("fraud_num_state".equals(d2)) {
            Context context = this.f2439a;
            if (g2 == 0) {
                z = true;
            }
            d.b(context, f2, z);
        } else if ("agent_num_state".equals(d2)) {
            Context context2 = this.f2439a;
            if (g2 == 0) {
                z = true;
            }
            d.a(context2, f2, z);
        } else if ("sell_num_state".equals(d2)) {
            Context context3 = this.f2439a;
            if (g2 == 0) {
                z = true;
            }
            d.d(context3, f2, z);
        } else if ("harass_num_state".equals(d2)) {
            Context context4 = this.f2439a;
            if (g2 == 0) {
                z = true;
            }
            d.c(context4, f2, z);
        }
        return true;
    }

    public Vector<m> b() {
        Vector<m> vector = new Vector<>();
        Cursor cursor = null;
        try {
            cursor = this.f2440b.query(ExtraTelephony.Keyword.CONTENT_URI, (String[]) null, "type = ? OR type = ? ", new String[]{String.valueOf(1), String.valueOf(4)}, (String) null);
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    String string = cursor.getString(cursor.getColumnIndex(DataSchemeDataSource.SCHEME_DATA));
                    int i = cursor.getInt(cursor.getColumnIndex(XSimChangeNotification.BROADCAST_EXTRA_KEY_SIM_ID));
                    int i2 = cursor.getInt(cursor.getColumnIndex("type"));
                    if (!TextUtils.isEmpty(string)) {
                        m.a m = m.m();
                        m.a(string);
                        m.a(i);
                        m.b(i2);
                        vector.add(m.build());
                    }
                }
            }
            return vector;
        } finally {
            IOUtils.closeQuietly(cursor);
        }
    }

    public Vector<p> c() {
        Vector<p> vector = new Vector<>(2);
        p.a j = p.j();
        j.a(1);
        j.b(c.a(this.f2439a, 1));
        vector.add(j.build());
        p.a j2 = p.j();
        j2.a(2);
        j2.b(c.a(this.f2439a, 2));
        vector.add(j2.build());
        return vector;
    }

    public Vector<s> d() {
        Vector<s> vector = new Vector<>();
        for (Integer next : this.g.keySet()) {
            for (String str : this.g.get(next).keySet()) {
                s.a m = s.m();
                m.a(next.intValue());
                m.a(str);
                m.b(((Integer) this.g.get(next).get(str)).intValue());
                vector.add(m.build());
            }
        }
        return vector;
    }

    public Vector<B> e() {
        Vector<B> vector = new Vector<>(2);
        B.a j = B.j();
        j.a(1);
        j.a(c.b(this.f2439a, 1));
        vector.add(j.build());
        B.a j2 = B.j();
        j2.a(2);
        j2.a(c.b(this.f2439a, 2));
        vector.add(j2.build());
        return vector;
    }

    public Vector<E> f() {
        Vector<E> vector = new Vector<>();
        Cursor cursor = null;
        try {
            cursor = this.f2440b.query(ExtraTelephony.Phonelist.CONTENT_URI, new String[]{"number", "notes", AdvancedSlider.STATE, XSimChangeNotification.BROADCAST_EXTRA_KEY_SIM_ID}, "type = ? AND sync_dirty <> ? ", new String[]{"2", String.valueOf(1)}, (String) null, (CancellationSignal) null);
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    String string = cursor.getString(cursor.getColumnIndex("number"));
                    String string2 = cursor.getString(cursor.getColumnIndex("notes"));
                    int i = cursor.getInt(cursor.getColumnIndex(AdvancedSlider.STATE));
                    int i2 = cursor.getInt(cursor.getColumnIndex(XSimChangeNotification.BROADCAST_EXTRA_KEY_SIM_ID));
                    E.a r = E.r();
                    r.b(string);
                    r.c(i);
                    r.b(i2);
                    if (!TextUtils.isEmpty(string2)) {
                        r.a(string2);
                    }
                    vector.add(r.build());
                }
            }
            return vector;
        } finally {
            IOUtils.closeQuietly(cursor);
        }
    }

    public C0203g g() {
        C0203g.a h = C0203g.h();
        h.a(c.f(this.f2439a));
        return h.build();
    }

    public v h() {
        v.a l = v.l();
        l.b(c.a(this.f2439a, "mark_guide_fraud"));
        l.a(c.a(this.f2439a, "mark_guide_agent"));
        l.c(c.a(this.f2439a, "mark_guide_sell"));
        return l.build();
    }

    public y i() {
        y.a h = y.h();
        h.a(c.e(this.f2439a));
        return h.build();
    }
}
