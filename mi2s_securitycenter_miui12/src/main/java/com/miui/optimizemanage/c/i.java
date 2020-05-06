package com.miui.optimizemanage.c;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import b.b.c.j.l;
import b.b.c.j.r;
import b.b.g.a;
import b.b.g.b;
import com.miui.common.card.FillParentDrawable;
import com.miui.optimizemanage.OptimizemanageMainActivity;
import com.miui.optimizemanage.n;
import com.miui.securitycenter.R;
import org.json.JSONObject;

public class i extends d implements a.C0028a {

    /* renamed from: c  reason: collision with root package name */
    private int f5906c;

    /* renamed from: d  reason: collision with root package name */
    private int f5907d;
    private int e;
    /* access modifiers changed from: private */
    public int f = -1;
    private int g;
    private int h;
    protected int i = 0;
    private boolean j;
    /* access modifiers changed from: private */
    public boolean k = false;
    /* access modifiers changed from: private */
    public String l;
    /* access modifiers changed from: private */
    public String m;
    /* access modifiers changed from: private */
    public String n;
    /* access modifiers changed from: private */
    public String o;
    /* access modifiers changed from: private */
    public String p;
    protected String[] q = new String[3];
    private JSONObject r;
    private transient Object s;
    private transient View t;

    private class a extends e {

        /* renamed from: a  reason: collision with root package name */
        private b f5908a;

        public a(View view) {
            super(view);
            this.f5908a = n.a(view, (d) i.this);
            l.a(view);
        }

        public void a(View view, d dVar, int i) {
            super.a(view, dVar, i);
            i iVar = (i) dVar;
            if (!i.this.k) {
                n.a(i.this.l);
                boolean unused = i.this.k = true;
            }
            b bVar = this.f5908a;
            if (bVar.j) {
                n.a(bVar, iVar.g(), iVar.l());
                this.f5908a.h.setBackgroundResource(R.drawable.card_bg_no_shadow_selector);
                this.f5908a.f1776a.setText(i.this.o);
                this.f5908a.e.setText(i.this.n);
                if (TextUtils.isEmpty(i.this.p)) {
                    this.f5908a.f1777b.setVisibility(8);
                } else {
                    this.f5908a.f1777b.setText(i.this.p);
                    this.f5908a.f1777b.setVisibility(0);
                }
                if (this.f5908a.f1779d != null) {
                    r.a(i.this.m, this.f5908a.f1779d, r.g, (int) R.drawable.card_icon_default);
                }
                if (this.f5908a.f1778c != null) {
                    r.a(iVar.k()[0], this.f5908a.f1778c, r.f1760d, (Drawable) new FillParentDrawable(view.getContext().getResources().getDrawable(R.drawable.big_backgroud_def)));
                }
                Context context = view.getContext();
                if (i.this.q()) {
                    this.f5908a.e.setBackground(com.miui.securitycenter.utils.b.a(context.getResources().getDimension(R.dimen.ad_g_big_button_corner_radius), i.this.d(), i.this.e()));
                }
                if (i.this.r()) {
                    this.f5908a.e.setTextColor(i.this.f);
                }
                n.a(context, this.f5908a.f, iVar.g(), iVar.l(), this.f5908a.i);
                this.f5908a.g.bringToFront();
                this.f5908a.g.setOnClickListener(new h(this, iVar));
            }
        }
    }

    public i(JSONObject jSONObject, int i2) {
        a(i2);
        this.r = jSONObject;
        a(jSONObject);
    }

    private void a(JSONObject jSONObject) {
        JSONObject optJSONObject = jSONObject.optJSONObject("extra");
        if (optJSONObject != null) {
            this.i = 0;
            String optString = optJSONObject.optString("buttonColor2");
            if (!TextUtils.isEmpty(optString)) {
                try {
                    this.f = Color.parseColor(optString);
                    this.i |= 8;
                } catch (Exception unused) {
                }
            }
            String optString2 = optJSONObject.optString("btnBgColorNormal2");
            String optString3 = optJSONObject.optString("btnBgColorPressed2");
            if (!TextUtils.isEmpty(optString2) && !TextUtils.isEmpty(optString3)) {
                try {
                    this.g = Color.parseColor(optString2);
                    this.h = Color.parseColor(optString3);
                    this.i |= 1;
                } catch (Exception unused2) {
                }
            }
        }
    }

    private void b(View view) {
        this.t = view;
        n.a(view.getContext(), this.s);
    }

    public e a(View view) {
        return new a(view);
    }

    public void a(int i2) {
        this.f5893b = i2;
    }

    public void a(Object obj) {
        this.s = obj;
    }

    public void a(String str) {
        this.n = str;
    }

    public void a(boolean z) {
        this.j = z;
    }

    public void a(String[] strArr) {
        this.q = strArr;
    }

    public void b(int i2) {
        this.f5907d = i2;
    }

    public void b(String str) {
        this.m = str;
    }

    public void c(int i2) {
        this.e = i2;
    }

    public void c(String str) {
        this.l = str;
    }

    public int d() {
        return this.g;
    }

    public void d(int i2) {
        this.f5906c = i2;
    }

    public void d(String str) {
        this.p = str;
    }

    public int e() {
        return this.h;
    }

    public void e(String str) {
        this.o = str;
    }

    public String f() {
        return this.n;
    }

    public int g() {
        return this.f5907d;
    }

    public String h() {
        return this.m;
    }

    public void h(i iVar) {
        if (iVar != null) {
            Log.d("OMGlobalAdvCardModel", "fill ad");
            a(iVar.a());
            a(iVar.l());
            c(iVar.i());
            b(iVar.h());
            a(iVar.f());
            e(iVar.p());
            d(iVar.n());
            a(iVar.k());
            b(iVar.g());
            a(true);
            b.b.g.a.a().a(iVar.l(), this);
        }
    }

    public int i() {
        return this.e;
    }

    public JSONObject j() {
        return this.r;
    }

    public String[] k() {
        return this.q;
    }

    public Object l() {
        return this.s;
    }

    public String m() {
        return this.l;
    }

    public String n() {
        return this.p;
    }

    public int o() {
        return this.f5906c;
    }

    public void onClick(View view) {
        super.onClick(view);
        OptimizemanageMainActivity optimizemanageMainActivity = (OptimizemanageMainActivity) view.getContext();
        if (n.a(view)) {
            b(view);
        }
    }

    public String p() {
        return this.o;
    }

    public boolean q() {
        return (this.i & 1) != 0;
    }

    public boolean r() {
        return (this.i & 8) != 0;
    }

    public boolean s() {
        return this.j && this.f5907d > 0;
    }
}
