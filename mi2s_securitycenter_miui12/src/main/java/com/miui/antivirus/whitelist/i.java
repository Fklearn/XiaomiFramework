package com.miui.antivirus.whitelist;

import b.b.b.b;
import com.miui.antivirus.whitelist.j;

public class i {

    /* renamed from: a  reason: collision with root package name */
    private k f3042a;

    /* renamed from: b  reason: collision with root package name */
    private b.C0024b f3043b;

    /* renamed from: c  reason: collision with root package name */
    private String f3044c;

    /* renamed from: d  reason: collision with root package name */
    private String f3045d;
    private String e;
    private String f;
    private String g;
    private String h;
    private boolean i;

    public static i a(j.b bVar) {
        i iVar = new i();
        iVar.f3044c = bVar.f3053c;
        iVar.f3045d = bVar.e;
        iVar.e = bVar.f;
        iVar.f3042a = k.RISK_APP;
        iVar.f3043b = bVar.f3051a.equals("INSTALLED_APP") ? b.C0024b.INSTALLED_APP : b.C0024b.UNINSTALLED_APK;
        iVar.f = bVar.f3054d;
        iVar.g = bVar.g;
        iVar.h = bVar.h;
        return iVar;
    }

    public static i a(j.c cVar) {
        i iVar = new i();
        iVar.f3044c = cVar.f3057c;
        iVar.f3045d = cVar.e;
        iVar.e = cVar.f;
        iVar.f3042a = k.TROJAN;
        iVar.f = cVar.f3058d;
        iVar.g = cVar.g;
        iVar.h = cVar.h;
        iVar.f3043b = cVar.f3055a.equals("INSTALLED_APP") ? b.C0024b.INSTALLED_APP : b.C0024b.UNINSTALLED_APK;
        return iVar;
    }

    public String a() {
        return this.f3045d;
    }

    public void a(boolean z) {
        this.i = z;
    }

    public b.C0024b b() {
        return this.f3043b;
    }

    public String c() {
        return this.e;
    }

    public String d() {
        return this.f3044c;
    }

    public boolean e() {
        return this.i;
    }

    public String toString() {
        return "WhiteListType mWhiteListType = " + this.f3042a + " mTitle = " + this.f3044c + " mDirPath = " + this.f3045d + " mPkgName = " + this.e + " mIsChecked = " + this.i;
    }
}
