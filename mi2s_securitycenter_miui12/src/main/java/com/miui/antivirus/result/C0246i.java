package com.miui.antivirus.result;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import com.miui.antivirus.activity.MainActivity;
import com.miui.networkassistant.ui.activity.NetworkDiagnosticsTipActivity;
import com.miui.securitycenter.R;
import java.util.ArrayList;
import java.util.List;
import miui.cloud.CloudPushConstants;
import org.json.JSONObject;

/* renamed from: com.miui.antivirus.result.i  reason: case insensitive filesystem */
public class C0246i extends C0244g {
    private String f;
    private String g;
    private String h;
    private int i = 1;
    private String j;
    private String k = CloudPushConstants.XML_ITEM;
    private int l = 1;
    private List<C0244g> m = new ArrayList();
    private int n;
    private boolean o;
    private String p;

    public C0246i(JSONObject jSONObject) {
        this.f = jSONObject.optString(NetworkDiagnosticsTipActivity.TITLE_KEY_NAME);
        this.g = jSONObject.optString("summary");
        this.h = jSONObject.optString("category");
        this.j = jSONObject.optString("cornerTip");
        this.i = jSONObject.optInt("template");
        this.k = jSONObject.optString("rowType");
        this.l = jSONObject.optInt("perpage");
        this.p = jSONObject.optString("id");
        this.o = jSONObject.optBoolean("visible", true);
        this.f2836d = true;
        this.e = false;
    }

    public static boolean a(int i2) {
        return i2 == 1 || i2 == 2 || i2 == 5;
    }

    public void a(int i2, View view, Context context, t tVar) {
        super.a(i2, view, context, tVar);
        H h2 = (H) view.getTag();
        h2.f2800a.setText(this.f);
        if (TextUtils.isEmpty(this.j)) {
            h2.f2801b.setVisibility(8);
        } else {
            h2.f2801b.setText(this.j);
            h2.f2801b.setVisibility(0);
        }
        if (this.i != 2 || this.m.size() <= this.l) {
            h2.f2803d.setVisibility(8);
            return;
        }
        h2.f2803d.setVisibility(0);
        h2.f2803d.setOnClickListener(this);
    }

    public void a(C0244g gVar) {
        this.m.add(gVar);
    }

    public String c() {
        return this.p;
    }

    public int d() {
        return this.l;
    }

    public int e() {
        return this.i;
    }

    public int f() {
        return this.m.size();
    }

    public boolean g() {
        return this.i == 2;
    }

    public int getLayoutId() {
        return R.layout.v_result_item_template_card_title_1;
    }

    public boolean h() {
        return this.o;
    }

    public void onClick(View view) {
        if (this.i == 2) {
            ArrayList arrayList = new ArrayList();
            ArrayList arrayList2 = new ArrayList();
            int i2 = this.n;
            while (i2 < this.m.size() && i2 < this.n + this.l) {
                arrayList.add(this.m.get(i2));
                i2++;
            }
            this.n += this.l;
            if (this.n >= this.m.size()) {
                this.n = 0;
            }
            int i3 = this.n;
            while (i3 < this.m.size() && i3 < this.n + this.l) {
                arrayList2.add(this.m.get(i3));
                i3++;
            }
            ((MainActivity) view.getContext()).a((C0244g) this, (List<C0244g>) arrayList, (List<C0244g>) arrayList2);
        }
    }
}
