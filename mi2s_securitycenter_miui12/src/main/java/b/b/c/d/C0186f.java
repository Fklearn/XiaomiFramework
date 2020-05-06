package b.b.c.d;

import android.content.Context;
import android.view.View;
import com.miui.luckymoney.config.Constants;
import com.miui.networkassistant.ui.activity.NetworkDiagnosticsTipActivity;
import com.miui.securitycenter.R;
import com.miui.securityscan.i.i;
import com.miui.warningcenter.mijia.MijiaAlertModel;
import org.json.JSONArray;
import org.json.JSONObject;

/* renamed from: b.b.c.d.f  reason: case insensitive filesystem */
public class C0186f extends C0185e {

    /* renamed from: d  reason: collision with root package name */
    private String f1676d;
    private String e;
    private String f;
    private String g;

    public C0186f(JSONObject jSONObject) {
        JSONArray optJSONArray = jSONObject.optJSONArray(Constants.JSON_KEY_MODULE);
        if (optJSONArray != null && optJSONArray.length() > 0) {
            JSONObject jSONObject2 = optJSONArray.getJSONObject(0);
            this.f1676d = jSONObject2.optString(NetworkDiagnosticsTipActivity.TITLE_KEY_NAME);
            this.e = jSONObject2.optString(MijiaAlertModel.KEY_URL);
        }
        this.f = jSONObject.optString("category");
    }

    public int a() {
        return R.layout.v_result_item_template_bottom;
    }

    public void a(int i, View view, Context context, C0191k kVar) {
        super.a(i, view, context, kVar);
        ((J) view.getTag()).f1660a.setText(this.f1676d);
    }

    public void b(String str) {
        this.g = str;
    }

    public void onClick(View view) {
        i.c(view.getContext(), this.e, this.f);
    }
}
