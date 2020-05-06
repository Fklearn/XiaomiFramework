package com.miui.antivirus.result;

import android.content.Context;
import android.view.View;
import com.miui.luckymoney.config.Constants;
import com.miui.networkassistant.ui.activity.NetworkDiagnosticsTipActivity;
import com.miui.securitycenter.R;
import com.miui.securityscan.i.i;
import com.miui.warningcenter.mijia.MijiaAlertModel;
import org.json.JSONArray;
import org.json.JSONObject;

/* renamed from: com.miui.antivirus.result.h  reason: case insensitive filesystem */
public class C0245h extends C0244g {
    private String f;
    private String g;
    private String h;
    private String i;

    public C0245h(JSONObject jSONObject) {
        JSONArray optJSONArray = jSONObject.optJSONArray(Constants.JSON_KEY_MODULE);
        if (optJSONArray != null && optJSONArray.length() > 0) {
            JSONObject jSONObject2 = optJSONArray.getJSONObject(0);
            this.f = jSONObject2.optString(NetworkDiagnosticsTipActivity.TITLE_KEY_NAME);
            this.g = jSONObject2.optString(MijiaAlertModel.KEY_URL);
        }
        this.h = jSONObject.optString("category");
        this.f2836d = false;
        this.e = true;
    }

    public void a(int i2, View view, Context context, t tVar) {
        super.a(i2, view, context, tVar);
        ((G) view.getTag()).f2799a.setText(this.f);
    }

    public void a(String str) {
        this.i = str;
    }

    public int getLayoutId() {
        return R.layout.v_result_item_template_bottom;
    }

    public void onClick(View view) {
        i.c(view.getContext(), this.g, this.h);
    }
}
