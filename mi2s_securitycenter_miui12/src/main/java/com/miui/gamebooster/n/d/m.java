package com.miui.gamebooster.n.d;

import android.view.View;
import com.miui.gamebooster.m.C0373d;
import com.miui.gamebooster.m.C0383n;
import com.miui.gamebooster.n.b.d;
import com.miui.gamebooster.n.c.a;
import com.miui.gamebooster.videobox.utils.e;
import com.miui.securitycenter.R;

public class m extends g {
    private String e;
    private String f;
    private long g;
    private int h = 0;
    private int i;

    public m(String str, String str2, int i2) {
        super(-1, R.drawable.gb_def_icon, a.RECOMMEND_APPS);
        this.i = i2;
        this.e = str;
        this.f = str2;
    }

    public void a(int i2) {
        this.h = i2;
    }

    public void a(long j) {
        this.g = j;
    }

    public void a(View view) {
        C0383n.a(view.getContext(), this.e, this.f, R.string.gamebox_app_not_find, e.c());
        d.a().a(this.e);
        C0373d.a.a(this.e);
    }

    public long e() {
        return this.g;
    }

    public String f() {
        return this.e;
    }

    public int g() {
        return this.i;
    }

    public int h() {
        return this.h;
    }

    public String toString() {
        return "VBRecommendAppModel{pkgName='" + this.e + '\'' + ", activityName='" + this.f + '\'' + ", clickedTime=" + this.g + ", weight=" + this.h + '}';
    }
}
