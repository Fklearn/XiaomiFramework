package com.miui.optimizemanage.c;

import android.view.View;
import android.widget.TextView;
import com.miui.networkassistant.ui.activity.NetworkDiagnosticsTipActivity;
import com.miui.securitycenter.R;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONObject;

public class l extends d {

    /* renamed from: c  reason: collision with root package name */
    private List<d> f5915c = new ArrayList();

    /* renamed from: d  reason: collision with root package name */
    private String f5916d;
    private boolean e;

    public static class a extends e {

        /* renamed from: a  reason: collision with root package name */
        TextView f5917a;

        public a(View view) {
            super(view);
            this.f5917a = (TextView) view.findViewById(R.id.title);
        }

        public void a(View view, d dVar, int i) {
            super.a(view, dVar, i);
            this.f5917a.setText(((l) dVar).e());
        }
    }

    public l(JSONObject jSONObject) {
        this.f5916d = jSONObject.optString(NetworkDiagnosticsTipActivity.TITLE_KEY_NAME);
        this.e = jSONObject.optBoolean("visible");
        a((int) R.layout.card_layout_list_title);
    }

    public e a(View view) {
        return new a(view);
    }

    public void a(d dVar) {
        this.f5915c.add(dVar);
    }

    public List<d> d() {
        return this.f5915c;
    }

    public String e() {
        return this.f5916d;
    }

    public boolean f() {
        return this.e;
    }
}
