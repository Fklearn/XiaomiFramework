package com.miui.permcenter.privacymanager.a;

import android.content.Context;
import android.content.res.Resources;
import android.text.TextUtils;
import b.b.c.j.x;
import com.miui.appmanager.E;
import com.miui.permcenter.privacymanager.behaviorrecord.o;
import com.miui.permission.PermissionInfo;
import com.miui.permission.PermissionManager;
import com.miui.securitycenter.R;
import java.util.ArrayList;
import java.util.List;

public class a {

    /* renamed from: a  reason: collision with root package name */
    private String f6325a;

    /* renamed from: b  reason: collision with root package name */
    private String f6326b;

    /* renamed from: c  reason: collision with root package name */
    private String f6327c;

    /* renamed from: d  reason: collision with root package name */
    private long f6328d;
    private int e;
    private int f;
    private String g;
    private String h;
    private int i;
    private int j;
    private int k;
    private CharSequence l;
    private CharSequence m;
    private CharSequence n;
    private String o;
    private int p;
    private String q;
    private int r;
    private E s;
    private int t;
    private String u;
    private List<a> v;
    private boolean w;

    public a() {
        this.u = null;
        this.w = false;
        this.p = 0;
    }

    public a(Context context, String str, String str2, String str3, long j2, int i2, int i3, String str4, String str5, int i4, int i5, int i6) {
        this.u = null;
        this.w = false;
        if (o.d(j2) || !o.c(str3)) {
            this.f6325a = str;
            this.f6326b = str2;
            this.f6327c = str3;
            this.f6328d = j2;
            this.e = i2;
            this.f = i3;
            this.g = str4;
            this.h = str5;
            this.i = i4;
            this.j = i5;
            this.t = i6;
            this.k = 0;
            a(context);
            return;
        }
        throw new Exception("DescribeAppBehavior error for " + j2 + " , and " + str3);
    }

    public a(Context context, String str, String str2, String str3, long j2, int i2, int i3, String str4, String str5, int i4, int i5, int i6, int i7) {
        this.u = null;
        this.w = false;
        if (o.d(j2) || !o.c(str3)) {
            this.f6325a = str;
            this.f6326b = str2;
            this.f6327c = str3;
            this.f6328d = j2;
            this.e = i2;
            this.f = i3;
            this.g = str4;
            this.h = str5;
            this.i = i4;
            this.j = i5;
            this.t = i7;
            this.k = i6;
            a(context);
            return;
        }
        throw new Exception("DescribeAppBehavior error for " + j2 + " , and " + str3);
    }

    private void a(Context context) {
        int i2;
        String str;
        Resources resources;
        Object[] objArr;
        int i3;
        String string;
        Resources resources2;
        int i4;
        Object[] objArr2;
        this.l = x.j(context, this.f6325a);
        this.m = x.j(context, this.f6326b);
        boolean z = this.t == 1;
        if (!o.c(this.f6327c)) {
            this.n = x.j(context, this.f6327c);
            if (o.d(this.f6326b)) {
                a(b.f);
                this.w = true;
                if (z) {
                    string = context.getResources().getString(o.a(this.f6326b, this.t));
                    this.o = string;
                    str = this.o;
                } else {
                    resources2 = context.getResources();
                    i4 = o.a(this.f6326b, this.t);
                    objArr2 = new Object[]{this.n};
                }
            } else {
                if (o.a(context, this.f6325a, this.j) || (z && TextUtils.equals(this.f6325a, this.f6327c))) {
                    a(b.f);
                    this.w = true;
                    if (z) {
                        resources2 = context.getResources();
                        i4 = R.string.app_behavior_start_from_app_single;
                        objArr2 = new Object[]{this.m};
                    } else {
                        resources = context.getResources();
                        i3 = R.string.app_behavior_start_from_app;
                        objArr = new Object[]{this.n, this.m};
                    }
                } else {
                    a(b.f6330b);
                    if (z) {
                        resources2 = context.getResources();
                        i4 = R.string.app_behavior_wakepath_single;
                        objArr2 = new Object[]{this.n};
                    } else {
                        resources = context.getResources();
                        i3 = R.string.app_behavior_wakepath;
                        objArr = new Object[]{this.m, this.n};
                    }
                }
                string = resources.getString(i3, objArr);
                this.o = string;
                str = this.o;
            }
            string = resources2.getString(i4, objArr2);
            this.o = string;
            str = this.o;
        } else {
            PermissionInfo permissionForId = PermissionManager.getInstance(context).getPermissionForId(i());
            this.q = permissionForId.getName();
            this.r = permissionForId.getFlags();
            if (this.f6328d == PermissionManager.PERM_ID_AUTOSTART) {
                this.q = context.getResources().getString(R.string.app_behavior_autostart_single);
                i2 = o.b(this.f6328d, this.t);
                a(b.f6329a);
                if (this.e == 0) {
                    a(b.f);
                }
            } else {
                a(b.f6331c);
                if (this.f != 1 || !o.e(this.f6328d)) {
                    i2 = o.b(this.f6328d, this.t);
                } else {
                    a(b.f6332d);
                    i2 = o.a(this.f6328d, this.t);
                }
            }
            this.o = z ? context.getResources().getString(i2) : context.getResources().getString(i2, new Object[]{this.m});
            str = this.m + this.o;
        }
        this.s = o.g(str);
    }

    public String a(Resources resources) {
        Object[] objArr;
        int i2;
        String b2 = o.b(this.g);
        String b3 = o.b(this.h);
        if (PermissionManager.virtualMap.containsValue(Long.valueOf(this.f6328d))) {
            if (TextUtils.equals(b2, b3)) {
                int i3 = this.i;
                if (i3 > 1) {
                    return resources.getQuantityString(R.plurals.app_behavior_desc_virtual_minute, i3, new Object[]{b3, Integer.valueOf(i3)});
                }
                return resources.getString(R.string.app_behavior_desc_virtual_once, new Object[]{o.b(this.h)});
            }
            int i4 = this.i;
            if (i4 > 1) {
                return resources.getQuantityString(R.plurals.app_behavior_desc_virtual, i4, new Object[]{b2, b3, Integer.valueOf(i4)});
            }
            return resources.getString(R.string.app_behavior_desc_virtual_once, new Object[]{b3});
        } else if (this.i <= 1) {
            if (this.e == 0) {
                i2 = R.string.app_behavior_desc_allow_once;
                objArr = new Object[]{o.b(this.h)};
            } else {
                i2 = R.string.app_behavior_desc_deny_once;
                objArr = new Object[]{o.b(this.h)};
            }
            return resources.getString(i2, objArr);
        } else if (TextUtils.equals(b2, b3)) {
            int i5 = this.e == 0 ? R.plurals.app_behavior_desc_allow_minute : R.plurals.app_behavior_desc_deny_minute;
            int i6 = this.i;
            return resources.getQuantityString(i5, i6, new Object[]{b3, Integer.valueOf(i6)});
        } else {
            int i7 = this.e == 0 ? R.plurals.app_behavior_desc_allow : R.plurals.app_behavior_desc_deny;
            int i8 = this.i;
            return resources.getQuantityString(i7, i8, new Object[]{b2, b3, Integer.valueOf(i8)});
        }
    }

    public void a() {
        this.v = null;
        this.u = null;
        this.s = o.g(this.o);
    }

    public void a(int i2) {
        this.p = i2 | this.p;
    }

    public boolean a(Context context, a aVar) {
        int i2;
        Resources resources;
        Object[] objArr;
        int i3;
        if (!a(aVar)) {
            return false;
        }
        if (this.v == null) {
            this.v = new ArrayList();
        }
        this.v.add(aVar);
        if (this.t == 1) {
            resources = context.getResources();
            i2 = R.plurals.app_behavior_wakepath_multiple_single;
            i3 = this.v.size() + 1;
            objArr = new Object[]{Integer.valueOf(this.v.size() + 1)};
        } else {
            resources = context.getResources();
            i2 = R.plurals.app_behavior_wakepath_multiple;
            i3 = this.v.size() + 1;
            objArr = new Object[]{e(), Integer.valueOf(this.v.size() + 1)};
        }
        this.u = resources.getQuantityString(i2, i3, objArr);
        this.s = o.g(this.u);
        return true;
    }

    public boolean a(a aVar) {
        return aVar != null && aVar.t == 0 && this.t == 0 && aVar.b(b.f6330b) && b(b.f6330b) && aVar.b().equals(b()) && aVar.f6326b.equals(this.f6326b) && aVar.j == this.j && this.e != 0 && aVar.e != 0;
    }

    public boolean a(String str) {
        E e2;
        return this.m.toString().toLowerCase().contains(str.toLowerCase()) || (TextUtils.isEmpty(this.u) ? this.o : this.u).toLowerCase().contains(str.toLowerCase()) || ((e2 = this.s) != null && (e2.f3574a.toString().toLowerCase().contains(str.toLowerCase()) || this.s.f3575b.toString().toLowerCase().contains(str.toLowerCase())));
    }

    public String b() {
        return this.h;
    }

    public boolean b(int i2) {
        return (i2 & this.p) != 0;
    }

    public String c() {
        return o.b(this.h);
    }

    public void c(int i2) {
        this.p = (~i2) & this.p;
    }

    public int d() {
        return this.p;
    }

    public CharSequence e() {
        return this.w ? this.n : this.m;
    }

    public String f() {
        return this.w ? this.f6327c : this.f6326b;
    }

    public String g() {
        return TextUtils.isEmpty(this.u) ? this.o : this.u;
    }

    public int h() {
        return this.r;
    }

    public long i() {
        return o.a(this.f6328d);
    }

    public String j() {
        return this.q;
    }

    public List<a> k() {
        return this.v;
    }

    public int l() {
        return this.w ? this.k : this.j;
    }

    public boolean m() {
        return b(b.f);
    }

    public boolean n() {
        return PermissionManager.virtualMap.containsValue(Long.valueOf(this.f6328d));
    }
}
