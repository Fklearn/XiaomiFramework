package b.b.c.d;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import com.miui.networkassistant.ui.activity.NetworkDiagnosticsTipActivity;
import com.miui.securitycenter.R;
import java.util.ArrayList;
import java.util.List;
import miui.cloud.CloudPushConstants;
import org.json.JSONObject;

/* renamed from: b.b.c.d.g  reason: case insensitive filesystem */
public class C0187g extends C0185e {

    /* renamed from: d  reason: collision with root package name */
    private String f1677d;
    private String e;
    private String f;
    private int g = 1;
    private String h;
    private String i = CloudPushConstants.XML_ITEM;
    private int j = 1;
    private List<C0185e> k = new ArrayList();
    private int l;
    private boolean m;
    private String n;

    public C0187g(JSONObject jSONObject) {
        this.f1677d = jSONObject.optString(NetworkDiagnosticsTipActivity.TITLE_KEY_NAME);
        this.e = jSONObject.optString("summary");
        this.f = jSONObject.optString("category");
        this.h = jSONObject.optString("cornerTip");
        this.g = jSONObject.optInt("template");
        this.i = jSONObject.optString("rowType");
        this.j = jSONObject.optInt("perpage");
        this.n = jSONObject.optString("id");
        this.m = jSONObject.optBoolean("visible", true);
    }

    public static boolean a(int i2) {
        return i2 == 1 || i2 == 2 || i2 == 5;
    }

    public int a() {
        return R.layout.v_result_item_template_card_title_1;
    }

    public void a(int i2, View view, Context context, C0191k kVar) {
        super.a(i2, view, context, kVar);
        K k2 = (K) view.getTag();
        k2.f1661a.setText(this.f1677d);
        if (TextUtils.isEmpty(this.h)) {
            k2.f1662b.setVisibility(8);
        } else {
            k2.f1662b.setText(this.h);
            k2.f1662b.setVisibility(0);
        }
        if (this.g != 2 || this.k.size() <= this.j) {
            k2.f1664d.setVisibility(8);
            return;
        }
        k2.f1664d.setVisibility(0);
        k2.f1664d.setOnClickListener(this);
    }

    public void a(C0185e eVar) {
        this.k.add(eVar);
    }

    public String b() {
        return this.n;
    }

    public int c() {
        return this.j;
    }

    public int d() {
        return this.g;
    }

    public int e() {
        return this.k.size();
    }

    public boolean f() {
        return this.g == 2;
    }

    public boolean g() {
        return this.m;
    }

    public void onClick(View view) {
        if (this.g == 2) {
            ArrayList arrayList = new ArrayList();
            ArrayList arrayList2 = new ArrayList();
            int i2 = this.l;
            while (i2 < this.k.size() && i2 < this.l + this.j) {
                arrayList.add(this.k.get(i2));
                i2++;
            }
            this.l += this.j;
            if (this.l >= this.k.size()) {
                this.l = 0;
            }
            int i3 = this.l;
            while (i3 < this.k.size() && i3 < this.l + this.j) {
                arrayList2.add(this.k.get(i3));
                i3++;
            }
            this.f1674b.a(this, arrayList, arrayList2);
        }
    }
}
