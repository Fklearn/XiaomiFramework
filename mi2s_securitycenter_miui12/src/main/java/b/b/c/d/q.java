package b.b.c.d;

import android.content.Context;
import android.view.View;
import com.miui.securitycenter.R;
import org.json.JSONObject;

public class q extends C0185e {

    /* renamed from: d  reason: collision with root package name */
    protected C0185e f1692d;
    protected String e;

    public q(JSONObject jSONObject) {
        this.e = jSONObject.optString("id");
    }

    public int a() {
        C0185e eVar = this.f1692d;
        return eVar != null ? eVar.a() : R.layout.dm_result_item_template_empty;
    }

    public void a(int i, View view, Context context, C0191k kVar) {
        C0185e eVar = this.f1692d;
        if (eVar != null) {
            eVar.a(i, view, context, kVar);
        } else {
            super.a(i, view, context, kVar);
        }
    }

    public void a(C0185e eVar) {
        this.f1692d = eVar;
    }

    public void a(String str) {
        C0185e eVar = this.f1692d;
        if (eVar != null) {
            eVar.a(str);
        } else {
            super.a(str);
        }
    }

    public String b() {
        return this.e;
    }

    public void onClick(View view) {
        C0185e eVar = this.f1692d;
        if (eVar != null) {
            eVar.onClick(view);
        } else {
            super.onClick(view);
        }
    }
}
