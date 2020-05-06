package com.miui.antivirus.model;

import b.b.b.o;
import com.miui.securitycenter.R;

public class e extends a {
    protected a i;
    protected c j;
    private String k;
    private String l;
    private String m;
    protected b mCardType;
    private String n;
    private o.f o;
    private String p;
    private String q;
    private o.g r;
    private boolean s = true;
    private int t;
    private String u;
    private boolean v;

    public enum a {
        SYSTEM,
        VIRUS,
        SIGN,
        SMS,
        AUTH,
        URL,
        MONITOR
    }

    public enum b {
        TOP,
        HEADER,
        DIVIDER,
        WIFI,
        APP,
        SAFE,
        BUTTON
    }

    public enum c {
        SYSTEM,
        SMS,
        APP,
        MONITOR
    }

    public e() {
    }

    public e(b bVar) {
        this.mCardType = bVar;
    }

    public void a(o.f fVar) {
        this.o = fVar;
    }

    public void a(o.g gVar) {
        this.r = gVar;
    }

    public void a(a aVar) {
        this.i = aVar;
    }

    public void a(b bVar) {
        this.mCardType = bVar;
    }

    public void a(c cVar) {
        this.j = cVar;
    }

    public void b(int i2) {
        this.t = i2;
    }

    public void b(String str) {
        this.l = str;
    }

    public void c(String str) {
        this.n = str;
    }

    public void d(String str) {
        this.f2747c = str;
    }

    public void d(boolean z) {
        this.e = z;
    }

    public void e(String str) {
        this.u = str;
    }

    public void e(boolean z) {
        this.s = z;
    }

    public boolean e() {
        return this.e;
    }

    public void f(String str) {
        this.k = str;
    }

    public void f(boolean z) {
        this.v = z;
    }

    public a g() {
        return this.i;
    }

    public void g(String str) {
        this.m = str;
    }

    public int getLayoutId() {
        switch (d.f2756a[this.mCardType.ordinal()]) {
            case 1:
                return R.layout.sp_scan_result_layout_top;
            case 2:
                return R.layout.sp_scan_result_header;
            case 3:
                return R.layout.sp_scan_result_button;
            case 4:
                return R.layout.sp_scan_result_line;
            case 5:
                return R.layout.sp_scan_result_wifi;
            case 6:
                return R.layout.sp_scan_result_view;
            case 7:
                return this.j == c.MONITOR ? R.layout.sp_scan_result_safe_monitor : R.layout.v_result_item_template_empty;
            default:
                return R.layout.v_result_item_template_empty;
        }
    }

    public String h() {
        return this.l;
    }

    public void h(String str) {
        this.f2746b = str;
    }

    public String i() {
        return this.n;
    }

    public void i(String str) {
        this.p = str;
    }

    public b j() {
        return this.mCardType;
    }

    public void j(String str) {
        this.q = str;
    }

    public String k() {
        return this.f2747c;
    }

    public String l() {
        return this.u;
    }

    public String m() {
        return this.k;
    }

    public c n() {
        return this.j;
    }

    public o.f o() {
        return this.o;
    }

    public o.g p() {
        return this.r;
    }

    public String q() {
        return this.m;
    }

    public String r() {
        return this.f2746b;
    }

    public int s() {
        return this.t;
    }

    public String t() {
        return this.p;
    }

    public String u() {
        return this.q;
    }

    public boolean v() {
        return this.s;
    }

    public boolean w() {
        return this.v;
    }
}
