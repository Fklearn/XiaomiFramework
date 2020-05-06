package b.b.c.d;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import b.b.c.j.r;
import com.miui.networkassistant.ui.activity.NetworkDiagnosticsTipActivity;
import com.miui.securitycenter.Application;
import com.miui.securitycenter.R;
import com.miui.warningcenter.mijia.MijiaAlertModel;
import miui.util.Log;
import org.json.JSONArray;
import org.json.JSONObject;

public class s extends C0185e {

    /* renamed from: d  reason: collision with root package name */
    private String f1694d;
    private String e;
    private String f;
    private String g;
    private String h;
    private String i;
    private long j;
    private String[] k = new String[3];
    private int l;
    private String m;
    private String n;
    private String o;
    private String p;

    public s() {
    }

    public s(JSONObject jSONObject) {
        this.f1694d = jSONObject.optString("newsId");
        this.e = jSONObject.optString(NetworkDiagnosticsTipActivity.TITLE_KEY_NAME);
        this.f = jSONObject.optString(MijiaAlertModel.KEY_URL);
        this.g = jSONObject.optString("summary");
        this.h = jSONObject.optString("source");
        this.j = jSONObject.optLong("newsDate");
        this.l = jSONObject.optInt("template");
        this.m = jSONObject.optString("cornerTip");
        this.i = jSONObject.optString("views");
        this.n = jSONObject.optString("dataId");
        JSONArray optJSONArray = jSONObject.optJSONArray("images");
        if (optJSONArray != null) {
            int length = optJSONArray.length();
            int i2 = 0;
            while (i2 < 3 && i2 < length) {
                this.k[i2] = optJSONArray.optString(i2);
                i2++;
            }
        }
    }

    private void a(TextView textView) {
        textView.setText(d(this.i));
    }

    public static void a(TextView textView, String str, String str2) {
        Drawable drawable;
        if (textView != null && str != null && !"".equals(str.trim())) {
            textView.setVisibility(0);
            if (!"".equals(str2.trim())) {
                drawable = Application.d().getResources().getDrawable(R.drawable.ico_vertical_line);
                drawable.setBounds(0, 0, 2, 30);
            } else {
                drawable = null;
            }
            textView.setText(str);
            textView.setCompoundDrawables((Drawable) null, (Drawable) null, drawable, (Drawable) null);
        } else if (textView != null) {
            textView.setVisibility(8);
        }
    }

    private String d(String str) {
        int i2;
        if (!TextUtils.isEmpty(str)) {
            try {
                i2 = Integer.parseInt(str);
            } catch (Exception e2) {
                Log.e("News", "msg", e2);
            }
            return Application.d().getResources().getQuantityString(R.plurals.view_num, i2, new Object[]{Integer.valueOf(i2)});
        }
        i2 = 0;
        return Application.d().getResources().getQuantityString(R.plurals.view_num, i2, new Object[]{Integer.valueOf(i2)});
    }

    public int a() {
        int i2 = this.l;
        return i2 != 2 ? i2 != 7 ? R.layout.v_result_item_template_empty : R.layout.v_result_item_template_31 : R.layout.v_result_item_template_4;
    }

    public void a(int i2, View view, Context context, C0191k kVar) {
        super.a(i2, view, context, kVar);
        int i3 = this.l;
        if (i3 == 2) {
            F f2 = (F) view.getTag();
            f2.f1650b.setText(this.e);
            r.a(this.k[0], f2.f1649a, t.b());
            a(f2.e);
        } else if (i3 == 7) {
            C c2 = (C) view.getTag();
            c2.f1641b.setText(this.e);
            r.a(this.k[0], c2.f1640a, r.g, (int) R.drawable.icon_def);
        }
    }

    public void b(String str) {
        this.o = str;
    }

    public void c(String str) {
        this.p = str;
    }

    public void onClick(View view) {
        t.a(view.getContext(), this.f, this.p);
    }
}
