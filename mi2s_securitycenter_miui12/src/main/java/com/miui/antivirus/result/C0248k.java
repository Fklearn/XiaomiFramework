package com.miui.antivirus.result;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import b.b.c.j.r;
import com.miui.maml.elements.FunctionElement;
import com.miui.networkassistant.provider.ProviderConstant;
import com.miui.networkassistant.ui.activity.NetworkDiagnosticsTipActivity;
import com.miui.securitycenter.R;
import com.miui.warningcenter.mijia.MijiaAlertModel;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;

/* renamed from: com.miui.antivirus.result.k  reason: case insensitive filesystem */
public class C0248k extends C0244g implements Cloneable {
    private static final ArrayList<Integer> f = new ArrayList<>();
    private int g;
    private int h;
    private String i;
    private String j;
    private String k;
    private String l;
    private int m;
    private String n;
    private String o;
    private int p = -1;
    private int q = -1;
    private int r = -1;
    private boolean s = false;
    private boolean t = false;
    private boolean u;
    private String v;
    private List<C0248k> w = new ArrayList();

    static {
        f.add(43);
        f.add(25);
        f.add(28);
        f.add(31);
        f.add(34);
        f.add(44);
        f.add(42);
    }

    public C0248k() {
    }

    public C0248k(JSONObject jSONObject) {
        this.g = jSONObject.optInt("functionId");
        this.h = jSONObject.optInt("template");
        this.i = jSONObject.optString(ProviderConstant.DataUsageNotiStatusColumns.COLUMN_ICON);
        String a2 = C0250m.a(this.i);
        if (!TextUtils.isEmpty(a2)) {
            this.i = a2;
        }
        this.k = jSONObject.optString(NetworkDiagnosticsTipActivity.TITLE_KEY_NAME);
        this.l = jSONObject.optString("summary");
        this.o = jSONObject.optString("button");
        this.m = jSONObject.optInt("type");
        this.n = jSONObject.optString(MijiaAlertModel.KEY_URL);
        String optString = jSONObject.optString("buttonColor2");
        if (!TextUtils.isEmpty(optString)) {
            try {
                this.p = Color.parseColor(optString);
                this.s = true;
            } catch (Exception e) {
                Log.e(FunctionElement.TAG_NAME, "msg", e);
            }
        }
        String optString2 = jSONObject.optString("btnBgColorOpenN2");
        String optString3 = jSONObject.optString("btnBgColorOpenP2");
        if (!TextUtils.isEmpty(optString2) && !TextUtils.isEmpty(optString3)) {
            try {
                this.q = Color.parseColor(optString2);
                this.r = Color.parseColor(optString3);
                this.t = true;
            } catch (Exception unused) {
            }
        }
        JSONArray optJSONArray = jSONObject.optJSONArray("images");
        if (optJSONArray != null) {
            optJSONArray.length();
            if (optJSONArray.length() > 0) {
                this.j = optJSONArray.optString(0);
            }
        }
        this.v = jSONObject.optString("dataId");
        if (this.g == 31) {
            setTemporary(true);
        }
    }

    public static C0248k a(JSONObject jSONObject) {
        int optInt = jSONObject.optInt("functionId");
        if (!C0250m.b(optInt) || !f.contains(Integer.valueOf(optInt))) {
            return null;
        }
        int optInt2 = jSONObject.optInt("template");
        if (optInt2 == 1 || optInt2 == 2 || optInt2 == 3 || optInt2 == 5) {
            return new C0248k(jSONObject);
        }
        return null;
    }

    public static void a(Context context, String str, Bundle bundle) {
        try {
            Intent intent = new Intent(str);
            if (bundle != null) {
                intent.putExtras(bundle);
            }
            context.startActivity(intent);
        } catch (Exception e) {
            Log.e(FunctionElement.TAG_NAME, "viewActionActivity", e);
        }
    }

    private void a(TextView textView, TextView textView2, Button button, ImageView imageView, ImageView imageView2, boolean z, t tVar) {
        int i2;
        textView.setText(this.k);
        button.getContext().getResources();
        if (this.m != 1 || !this.u) {
            button.setText(this.o);
        } else {
            button.setText(R.string.close);
        }
        textView2.setText(this.l);
        if (imageView != null) {
            if (z) {
                r.a(this.i, imageView, r.g);
            } else {
                r.a(this.i, imageView, tVar.b());
            }
        }
        if (imageView2 != null) {
            String str = this.j;
            if (str == null || str.isEmpty()) {
                i2 = 8;
            } else {
                r.a(this.j, imageView2, tVar.b());
                i2 = 0;
            }
            imageView2.setVisibility(i2);
        }
    }

    private void a(v vVar) {
        List<String> b2 = C0250m.b();
        vVar.f2860b.setText(this.k);
        vVar.f2861c.setText(this.o);
        for (int i2 = 0; i2 < vVar.f2859a; i2++) {
            r.a("pkg_icon://" + b2.get(i2), vVar.f2862d[i2], r.f);
        }
    }

    public static void b(Context context, String str) {
        try {
            Intent intent = new Intent(str);
            if ("com.miui.gamebooster.action.ACCESS_MAINACTIVITY".equals(str)) {
                intent.putExtra("track_gamebooster_enter_way", "00004");
            }
            if ("miui.intent.action.POWER_MANAGER".equals(str)) {
                intent.putExtra("enter_homepage_way", "00003");
            }
            context.startActivity(intent);
        } catch (Exception e) {
            Log.e(FunctionElement.TAG_NAME, "viewActionActivity", e);
        }
    }

    public void a(int i2) {
        this.g = i2;
    }

    /* JADX WARNING: Removed duplicated region for block: B:21:0x0037  */
    /* JADX WARNING: Removed duplicated region for block: B:33:0x0079 A[SYNTHETIC, Splitter:B:33:0x0079] */
    /* JADX WARNING: Removed duplicated region for block: B:38:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void a(int r9, android.view.View r10, android.content.Context r11, com.miui.antivirus.result.t r12) {
        /*
            r8 = this;
            super.a(r9, r10, r11, r12)
            int r9 = r8.g
            r11 = 20
            if (r9 == r11) goto L_0x002d
            r11 = 25
            if (r9 == r11) goto L_0x002a
            r11 = 28
            if (r9 == r11) goto L_0x0027
            r11 = 34
            if (r9 == r11) goto L_0x0024
            r11 = 42
            if (r9 == r11) goto L_0x0021
            r11 = 43
            if (r9 == r11) goto L_0x001e
            goto L_0x0032
        L_0x001e:
            java.lang.String r9 = "v_rs_cleanmaster"
            goto L_0x002f
        L_0x0021:
            java.lang.String r9 = "v_rs_gamebooster"
            goto L_0x002f
        L_0x0024:
            java.lang.String r9 = "v_rs_applock"
            goto L_0x002f
        L_0x0027:
            java.lang.String r9 = "v_rs_app_manage"
            goto L_0x002f
        L_0x002a:
            java.lang.String r9 = "v_rs_auto_start"
            goto L_0x002f
        L_0x002d:
            java.lang.String r9 = "v_rs_power_optimazation"
        L_0x002f:
            b.b.b.a.b.C0023b.j(r9)
        L_0x0032:
            int r9 = r8.h
            r11 = 1
            if (r9 == r11) goto L_0x005d
            r0 = 2
            if (r9 == r0) goto L_0x004b
            r0 = 3
            if (r9 == r0) goto L_0x005d
            r12 = 5
            if (r9 == r12) goto L_0x0041
            goto L_0x0073
        L_0x0041:
            java.lang.Object r9 = r10.getTag()
            com.miui.antivirus.result.v r9 = (com.miui.antivirus.result.v) r9
            r8.a((com.miui.antivirus.result.v) r9)
            goto L_0x0073
        L_0x004b:
            java.lang.Object r9 = r10.getTag()
            com.miui.antivirus.result.z r9 = (com.miui.antivirus.result.z) r9
            android.widget.TextView r1 = r9.f2869b
            android.widget.TextView r2 = r9.f2870c
            android.widget.Button r3 = r9.f
            android.widget.ImageView r4 = r9.f2871d
            android.widget.ImageView r5 = r9.e
            r6 = 0
            goto L_0x006e
        L_0x005d:
            java.lang.Object r9 = r10.getTag()
            com.miui.antivirus.result.z r9 = (com.miui.antivirus.result.z) r9
            android.widget.TextView r1 = r9.f2869b
            android.widget.TextView r2 = r9.f2870c
            android.widget.Button r3 = r9.f
            android.widget.ImageView r4 = r9.f2871d
            android.widget.ImageView r5 = r9.e
            r6 = 1
        L_0x006e:
            r0 = r8
            r7 = r12
            r0.a(r1, r2, r3, r4, r5, r6, r7)
        L_0x0073:
            boolean r9 = b.b.c.j.A.a()
            if (r9 == 0) goto L_0x008b
            android.view.View[] r9 = new android.view.View[r11]     // Catch:{ Throwable -> 0x008b }
            r11 = 0
            r9[r11] = r10     // Catch:{ Throwable -> 0x008b }
            miui.animation.IFolme r9 = miui.animation.Folme.useAt(r9)     // Catch:{ Throwable -> 0x008b }
            miui.animation.ITouchStyle r9 = r9.touch()     // Catch:{ Throwable -> 0x008b }
            miui.animation.base.AnimConfig[] r11 = new miui.animation.base.AnimConfig[r11]     // Catch:{ Throwable -> 0x008b }
            r9.handleTouchOf(r10, r11)     // Catch:{ Throwable -> 0x008b }
        L_0x008b:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.antivirus.result.C0248k.a(int, android.view.View, android.content.Context, com.miui.antivirus.result.t):void");
    }

    public void a(String str) {
        this.o = str;
    }

    public void b(int i2) {
        this.m = i2;
    }

    public void b(String str) {
        this.i = str;
    }

    public int c() {
        return this.g;
    }

    public void c(int i2) {
        this.h = i2;
    }

    public void c(String str) {
        this.l = str;
    }

    public C0248k clone() {
        Object obj;
        try {
            obj = super.clone();
            try {
                return (C0248k) obj;
            } catch (Exception e) {
                e = e;
                Log.e(FunctionElement.TAG_NAME, "msg", e);
                return (C0248k) obj;
            }
        } catch (Exception e2) {
            e = e2;
            obj = null;
            Log.e(FunctionElement.TAG_NAME, "msg", e);
            return (C0248k) obj;
        }
    }

    public String d() {
        return this.o;
    }

    public void d(String str) {
        this.k = str;
    }

    public String e() {
        return this.i;
    }

    public String f() {
        return this.k;
    }

    public int getLayoutId() {
        int i2 = this.h;
        return i2 != 1 ? i2 != 2 ? (i2 == 3 || i2 != 5) ? R.layout.v_result_item_template_3 : R.layout.v_result_item_template_26 : R.layout.v_result_item_template_18 : R.layout.v_result_item_template_3;
    }

    /* JADX WARNING: type inference failed for: r4v3, types: [android.content.Context, com.miui.antivirus.activity.MainActivity] */
    /* JADX WARNING: Removed duplicated region for block: B:27:0x0086  */
    /* JADX WARNING: Removed duplicated region for block: B:29:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onClick(android.view.View r4) {
        /*
            r3 = this;
            int r0 = r3.g
            boolean r1 = r4 instanceof android.widget.Button
            if (r1 != 0) goto L_0x000d
            r1 = 2131296561(0x7f090131, float:1.8211042E38)
            android.view.View r4 = r4.findViewById(r1)
        L_0x000d:
            android.content.Context r4 = r4.getContext()
            com.miui.antivirus.activity.MainActivity r4 = (com.miui.antivirus.activity.MainActivity) r4
            r1 = 4
            if (r0 == r1) goto L_0x0079
            r1 = 20
            if (r0 == r1) goto L_0x0076
            r1 = 25
            if (r0 == r1) goto L_0x006e
            r1 = 28
            if (r0 == r1) goto L_0x005e
            r1 = 34
            if (r0 == r1) goto L_0x0047
            switch(r0) {
                case 42: goto L_0x003f;
                case 43: goto L_0x0032;
                case 44: goto L_0x002a;
                default: goto L_0x0029;
            }
        L_0x0029:
            return
        L_0x002a:
            java.lang.String r0 = "v_rs_power_optimazation"
            b.b.b.a.b.C0023b.i(r0)
            java.lang.String r0 = "miui.intent.action.POWER_MANAGER"
            goto L_0x007b
        L_0x0032:
            java.lang.String r0 = "v_rs_cleanmaster"
            b.b.b.a.b.C0023b.i(r0)
            android.content.Intent r0 = new android.content.Intent
            java.lang.String r1 = "miui.intent.action.GARBAGE_CLEANUP"
            r0.<init>(r1)
            goto L_0x006a
        L_0x003f:
            java.lang.String r0 = "v_rs_gamebooster"
            b.b.b.a.b.C0023b.i(r0)
            java.lang.String r0 = "com.miui.gamebooster.action.ACCESS_MAINACTIVITY"
            goto L_0x007b
        L_0x0047:
            java.lang.String r0 = "v_rs_applock"
            b.b.b.a.b.C0023b.i(r0)
            android.os.Bundle r0 = new android.os.Bundle
            r0.<init>()
            java.lang.String r1 = "enter_way"
            java.lang.String r2 = "00009"
            r0.putString(r1, r2)
            java.lang.String r1 = "com.miui.securitycenter.action.TRANSITION"
            a(r4, r1, r0)
            goto L_0x007e
        L_0x005e:
            java.lang.String r0 = "v_rs_app_manage"
            b.b.b.a.b.C0023b.i(r0)
            android.content.Intent r0 = new android.content.Intent
            java.lang.String r1 = "miui.intent.action.GARBAGE_UNINSTALL_APPS"
            r0.<init>(r1)
        L_0x006a:
            com.miui.cleanmaster.g.b(r4, r0)
            goto L_0x007e
        L_0x006e:
            java.lang.String r0 = "v_rs_auto_start"
            b.b.b.a.b.C0023b.i(r0)
            java.lang.String r0 = "miui.intent.action.OP_AUTO_START"
            goto L_0x007b
        L_0x0076:
            java.lang.String r0 = "com.miui.powercenter.PowerShutdownOnTime"
            goto L_0x007b
        L_0x0079:
            java.lang.String r0 = "miui.intent.action.GARBAGE_DEEPCLEAN"
        L_0x007b:
            b(r4, r0)
        L_0x007e:
            java.lang.String r4 = r3.v
            boolean r4 = android.text.TextUtils.isEmpty(r4)
            if (r4 == 0) goto L_0x008e
            int r4 = r3.g
            java.lang.String r4 = java.lang.String.valueOf(r4)
            r3.v = r4
        L_0x008e:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.antivirus.result.C0248k.onClick(android.view.View):void");
    }
}
