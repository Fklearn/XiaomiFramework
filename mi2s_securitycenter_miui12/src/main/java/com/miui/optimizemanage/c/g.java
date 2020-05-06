package com.miui.optimizemanage.c;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import b.b.c.j.l;
import b.b.c.j.r;
import com.miui.networkassistant.provider.ProviderConstant;
import com.miui.networkassistant.ui.activity.NetworkDiagnosticsTipActivity;
import com.miui.securitycenter.R;
import com.miui.securitycenter.utils.b;
import com.miui.securityscan.cards.d;
import org.json.JSONObject;

public class g extends d {

    /* renamed from: c  reason: collision with root package name */
    public String f5898c;

    /* renamed from: d  reason: collision with root package name */
    public String f5899d;
    public String e;
    public String f;
    /* access modifiers changed from: private */
    public String g;
    /* access modifiers changed from: private */
    public String h;
    private int i;
    protected int j = -1;
    /* access modifiers changed from: private */
    public int k = -1;
    /* access modifiers changed from: private */
    public int l = -1;
    /* access modifiers changed from: private */
    public boolean m = false;
    /* access modifiers changed from: private */
    public boolean n = false;
    private boolean o = false;
    /* access modifiers changed from: private */
    public boolean p = false;

    public static class a extends e {

        /* renamed from: a  reason: collision with root package name */
        ImageView f5900a;

        /* renamed from: b  reason: collision with root package name */
        TextView f5901b;

        /* renamed from: c  reason: collision with root package name */
        TextView f5902c;

        /* renamed from: d  reason: collision with root package name */
        Button f5903d;

        public a(View view) {
            super(view);
            this.f5900a = (ImageView) view.findViewById(R.id.icon);
            this.f5901b = (TextView) view.findViewById(R.id.title);
            this.f5902c = (TextView) view.findViewById(R.id.summary);
            this.f5903d = (Button) view.findViewById(R.id.button);
            this.f5900a.setColorFilter(view.getResources().getColor(R.color.result_banner_icon_bg));
            l.a(view);
        }

        public void a(View view, d dVar, int i) {
            super.a(view, dVar, i);
            g gVar = (g) dVar;
            r.a(gVar.e(), this.f5900a, r.g, (int) R.drawable.card_icon_default);
            this.f5901b.setText(gVar.g());
            this.f5902c.setText(gVar.f());
            this.f5903d.setText(gVar.d());
            Resources resources = view.getContext().getResources();
            float dimension = resources.getDimension(R.dimen.pc_scanning_result_fix_button_radius_size);
            this.f5903d.setTextColor(gVar.m ? gVar.j : resources.getColor(R.color.result_small_button_text_color));
            Drawable a2 = gVar.n ? b.a(dimension, gVar.k, gVar.l) : resources.getDrawable(R.drawable.scanresult_button_blue);
            if (a2 != null) {
                this.f5903d.setBackground(a2);
            }
            view.setOnClickListener(gVar);
            this.f5903d.setOnClickListener(gVar);
            if (!gVar.p) {
                if (TextUtils.isEmpty(gVar.h)) {
                    String unused = gVar.h = String.valueOf(gVar.g);
                }
                com.miui.optimizemanage.a.a.c(gVar.h);
                boolean unused2 = gVar.p = true;
            }
        }
    }

    public g(String str, String str2, String str3, String str4, String str5) {
        a((int) R.layout.om_result_function_template_1);
        this.f5898c = str;
        this.f5899d = str2;
        this.e = str3;
        this.f = str4;
        this.g = str5;
    }

    public g(JSONObject jSONObject) {
        a(jSONObject);
        a((int) R.layout.om_result_function_template_1);
    }

    public static g a(int i2, JSONObject jSONObject) {
        g gVar = jSONObject != null ? new g(jSONObject) : null;
        if (gVar == null || !gVar.o) {
            return null;
        }
        return gVar;
    }

    private void a(Context context) {
        try {
            if (!"miui.intent.action.GARBAGE_UNINSTALL_APPS".equals(this.g)) {
                if (!"miui.intent.action.GARBAGE_CLEANUP".equals(this.g)) {
                    context.startActivity(new Intent(this.g));
                    return;
                }
            }
            com.miui.cleanmaster.g.b(context, new Intent(this.g));
        } catch (Exception e2) {
            Log.e("OMFunctionCardModel", "FunctionCardModel start action error", e2);
        }
    }

    private void a(JSONObject jSONObject) {
        this.i = jSONObject.optInt("template");
        this.h = jSONObject.optString("dataId");
        this.f5899d = jSONObject.optString(NetworkDiagnosticsTipActivity.TITLE_KEY_NAME);
        this.e = jSONObject.optString("summary");
        this.f5898c = jSONObject.optString(ProviderConstant.DataUsageNotiStatusColumns.COLUMN_ICON);
        this.f = jSONObject.optString("button");
        String optString = jSONObject.optString("buttonColor2");
        if (!TextUtils.isEmpty(optString)) {
            try {
                this.j = Color.parseColor(optString);
                this.m = true;
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        String optString2 = jSONObject.optString("btnBgColorOpenN2");
        String optString3 = jSONObject.optString("btnBgColorOpenP2");
        if (!TextUtils.isEmpty(optString2) && !TextUtils.isEmpty(optString3)) {
            try {
                this.k = Color.parseColor(optString2);
                this.l = Color.parseColor(optString3);
                this.n = true;
            } catch (Exception unused) {
            }
        }
        try {
            Intent parseUri = Intent.parseUri(jSONObject.optString("action"), 0);
            String action = parseUri.getAction();
            if (d.a(parseUri) || "miui.intent.action.GARBAGE_UNINSTALL_APPS".equals(action) || "miui.intent.action.GARBAGE_CLEANUP".equals(action)) {
                this.g = action;
                this.o = true;
            }
        } catch (Exception e3) {
            Log.e("OMFunctionCardModel", "parse function data error", e3);
        }
    }

    public e a(View view) {
        return new a(view);
    }

    public String d() {
        return this.f;
    }

    public String e() {
        return this.f5898c;
    }

    public String f() {
        return this.e;
    }

    public String g() {
        return this.f5899d;
    }

    public void onClick(View view) {
        super.onClick(view);
        a(view.getContext());
        if (TextUtils.isEmpty(this.h)) {
            this.h = String.valueOf(this.g);
        }
        com.miui.optimizemanage.a.a.b(this.h);
    }
}
