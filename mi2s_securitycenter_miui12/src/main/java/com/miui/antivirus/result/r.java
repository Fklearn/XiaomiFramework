package com.miui.antivirus.result;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import b.b.b.a.b;
import com.miui.networkassistant.ui.activity.NetworkDiagnosticsTipActivity;
import com.miui.securitycenter.Application;
import com.miui.securitycenter.R;
import com.miui.securityscan.i.i;
import com.miui.warningcenter.mijia.MijiaAlertModel;
import miui.util.Log;
import org.json.JSONArray;
import org.json.JSONObject;

public class r extends C0244g {
    private String f;
    private String g;
    private String h;
    private String i;
    private String j;
    private String k;
    private long l;
    private String[] m = new String[3];
    private int n;
    private String o;
    private String p;
    private String q;
    private String r;
    private String s;

    public r() {
    }

    public r(JSONObject jSONObject) {
        this.f = jSONObject.optString("newsId");
        this.g = jSONObject.optString(NetworkDiagnosticsTipActivity.TITLE_KEY_NAME);
        this.h = jSONObject.optString(MijiaAlertModel.KEY_URL);
        this.i = jSONObject.optString("summary");
        this.j = jSONObject.optString("source");
        this.l = jSONObject.optLong("newsDate");
        this.n = jSONObject.optInt("template");
        this.o = jSONObject.optString("cornerTip");
        this.k = jSONObject.optString("views");
        this.p = jSONObject.optString("dataId");
        this.s = jSONObject.optString("deeplink");
        JSONArray optJSONArray = jSONObject.optJSONArray("images");
        if (optJSONArray != null) {
            int length = optJSONArray.length();
            int i2 = 0;
            while (i2 < 3 && i2 < length) {
                this.m[i2] = optJSONArray.optString(i2);
                i2++;
            }
        }
    }

    private String c(String str) {
        int i2;
        if (!TextUtils.isEmpty(str)) {
            try {
                i2 = Integer.parseInt(str);
            } catch (Exception e) {
                Log.e("News", "msg", e);
            }
            return Application.d().getResources().getQuantityString(R.plurals.view_num, i2, new Object[]{Integer.valueOf(i2)});
        }
        i2 = 0;
        return Application.d().getResources().getQuantityString(R.plurals.view_num, i2, new Object[]{Integer.valueOf(i2)});
    }

    public void a(int i2, View view, Context context, t tVar) {
        super.a(i2, view, context, tVar);
        int i3 = this.n;
        if (i3 == 2) {
            B b2 = (B) view.getTag();
            b2.f2786b.setText(this.g);
            b.b.c.j.r.a(this.m[0], b2.f2785a, tVar.b());
            a(b2.e);
        } else if (i3 == 7) {
            x xVar = (x) view.getTag();
            xVar.f2868b.setText(this.g);
            b.b.c.j.r.a(this.m[0], xVar.f2867a, b.b.c.j.r.g, (int) R.drawable.icon_def);
        } else {
            return;
        }
        b.C0023b.l(c());
    }

    /* access modifiers changed from: package-private */
    public void a(TextView textView) {
        textView.setText(c(this.k));
    }

    public void a(String str) {
        this.q = str;
    }

    public void b(String str) {
        this.r = str;
    }

    public String c() {
        return this.f;
    }

    public int getLayoutId() {
        int i2 = this.n;
        return i2 != 2 ? i2 != 7 ? R.layout.v_result_item_template_empty : R.layout.v_result_item_template_31 : R.layout.v_result_item_template_4;
    }

    public void onClick(View view) {
        Context context = view.getContext();
        if (!(!TextUtils.isEmpty(this.s) ? C0244g.a(context, this.s) : false)) {
            i.c(context, this.h, this.r);
        }
        b.C0023b.k(this.f);
    }
}
