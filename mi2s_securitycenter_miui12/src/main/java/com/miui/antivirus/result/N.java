package com.miui.antivirus.result;

import android.graphics.Color;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import b.b.c.j.r;
import com.miui.networkassistant.provider.ProviderConstant;
import com.miui.networkassistant.ui.activity.NetworkDiagnosticsTipActivity;
import com.miui.securitycenter.R;
import org.json.JSONObject;

public class N extends C0248k {
    private int A;
    private String x;
    private boolean y;
    private int z;

    public static N b(JSONObject jSONObject) {
        N n = null;
        if (jSONObject == null) {
            return null;
        }
        String optString = jSONObject.optString(ProviderConstant.DataUsageNotiStatusColumns.COLUMN_ICON);
        String optString2 = jSONObject.optString(NetworkDiagnosticsTipActivity.TITLE_KEY_NAME);
        String optString3 = jSONObject.optString("summary");
        String optString4 = jSONObject.optString("button");
        String optString5 = jSONObject.optString("buttonColor2");
        int optInt = jSONObject.optInt("showTime", 5);
        int i = -1;
        boolean z2 = false;
        if (!TextUtils.isEmpty(optString5)) {
            try {
                i = Color.parseColor(optString5);
                z2 = true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (!TextUtils.isEmpty(optString) && !TextUtils.isEmpty(optString2) && !TextUtils.isEmpty(optString3) && !TextUtils.isEmpty(optString4)) {
            n = new N();
            n.b(optString);
            n.d(optString2);
            n.e(optString3);
            n.a(optString4);
            n.e(optInt);
            if (z2) {
                n.d(i);
            }
        }
        return n;
    }

    public void a(View view) {
        ImageView imageView = (ImageView) view.findViewById(R.id.sidekick_icon);
        TextView textView = (TextView) view.findViewById(R.id.sidekick_ok);
        ((TextView) view.findViewById(R.id.sidekick_title)).setText(f());
        textView.setText(d());
        if (this.y) {
            textView.setTextColor(this.z);
        }
        r.a(e(), imageView);
    }

    public void d(int i) {
        this.z = i;
        this.y = true;
    }

    public void e(int i) {
        this.A = i;
    }

    public void e(String str) {
        this.x = str;
    }

    public String g() {
        return this.x;
    }

    public int h() {
        return this.A;
    }
}
