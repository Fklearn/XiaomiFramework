package b.b.c.d;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import com.miui.networkassistant.ui.activity.NetworkDiagnosticsTipActivity;
import com.miui.securitycenter.Application;
import com.miui.securitycenter.R;
import com.miui.warningcenter.mijia.MijiaAlertModel;
import java.util.ArrayList;
import java.util.List;
import miui.util.Log;
import org.json.JSONObject;

public class r extends C0185e {

    /* renamed from: d  reason: collision with root package name */
    private String f1693d;
    private String e;
    private String f;
    private String g;
    private String h;
    private String i;
    private boolean j;
    private int k;
    private int l = 1;
    private String m;
    private List<r> n;

    public r() {
    }

    private r(JSONObject jSONObject) {
        this.f = jSONObject.optString("img");
        this.f1693d = jSONObject.optString(NetworkDiagnosticsTipActivity.TITLE_KEY_NAME);
        this.e = jSONObject.optString("summary");
        this.h = jSONObject.optString("cornerTip");
        this.g = jSONObject.optString(MijiaAlertModel.KEY_URL);
        this.l = jSONObject.optInt("template");
        this.i = jSONObject.optString("button");
        this.m = jSONObject.optString("dataId");
        this.j = jSONObject.optBoolean("browserOpen", true);
        String optString = jSONObject.optString("buttonColor");
        if (!TextUtils.isEmpty(optString)) {
            try {
                this.k = Color.parseColor(optString);
            } catch (Exception e2) {
                Log.e("MiActivity", "msg", e2);
            }
        }
    }

    public static r a(JSONObject jSONObject) {
        switch (jSONObject.optInt("template")) {
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
                r rVar = new r(jSONObject);
                if (TextUtils.isEmpty(rVar.g)) {
                    return null;
                }
                try {
                    List<ResolveInfo> queryIntentActivities = Application.d().getPackageManager().queryIntentActivities(Intent.parseUri(rVar.g, 0), 32);
                    if (queryIntentActivities == null || queryIntentActivities.size() == 0) {
                        return null;
                    }
                    return rVar;
                } catch (Exception e2) {
                    Log.e("MiActivity", "msg", e2);
                }
                break;
            default:
                return null;
        }
    }

    public int a() {
        int i2 = this.l;
        if (i2 == 1001) {
            return R.layout.v_result_item_template_25;
        }
        switch (i2) {
            case 2:
                return R.layout.v_result_item_template_20;
            case 3:
                return R.layout.v_result_item_template_21;
            case 4:
                return R.layout.v_result_item_template_27;
            case 5:
                return R.layout.v_result_item_template_28;
            case 6:
                return R.layout.v_result_item_template_29;
            case 7:
                return R.layout.v_result_item_template_30;
            default:
                return R.layout.v_result_item_template_2;
        }
    }

    public void a(int i2) {
        this.l = i2;
    }

    public void a(int i2, View view, Context context, C0191k kVar) {
        super.a(i2, view, context, kVar);
        if (this.l != 1001) {
            B b2 = (B) view.getTag();
            b2.f1637b.setText(this.e);
            b2.f1636a.setText(this.f1693d);
            if (this.l == 4) {
                b.b.c.j.r.a(this.f, b2.f1638c, b.b.c.j.r.g, (int) R.drawable.icon_def);
            } else {
                b.b.c.j.r.a(this.f, b2.f1638c, t.a());
            }
            Button button = b2.f1639d;
            if (button != null) {
                button.setText(this.i);
                if (this.l == 2) {
                    int i3 = this.k;
                    if (i3 == -1) {
                        i3 = R.color.btn_color_red;
                    }
                    button.setTextColor(i3);
                    return;
                }
                return;
            }
            return;
        }
        z zVar = (z) view.getTag();
        if (this.n.size() >= 3) {
            r rVar = this.n.get(0);
            zVar.f1707d.setText(rVar.d());
            if (!rVar.b().isEmpty() && zVar.g != null) {
                b.b.c.j.r.a(rVar.b(), zVar.g, b.b.c.j.r.g, (int) R.drawable.icon_def);
            }
            r rVar2 = this.n.get(1);
            zVar.e.setText(rVar2.d());
            if (!rVar2.b().isEmpty() && zVar.h != null) {
                b.b.c.j.r.a(rVar2.b(), zVar.h, b.b.c.j.r.g, (int) R.drawable.icon_def);
            }
            r rVar3 = this.n.get(2);
            zVar.f.setText(rVar3.d());
            if (!rVar3.b().isEmpty() && zVar.i != null) {
                b.b.c.j.r.a(rVar3.b(), zVar.i, b.b.c.j.r.g, (int) R.drawable.icon_def);
            }
        }
    }

    public void a(r rVar) {
        if (this.n == null) {
            this.n = new ArrayList();
        }
        this.n.add(rVar);
    }

    public String b() {
        return this.f;
    }

    public int c() {
        List<r> list = this.n;
        if (list == null) {
            return 0;
        }
        return list.size();
    }

    public String d() {
        return this.f1693d;
    }

    public String e() {
        return this.g;
    }

    /* JADX WARNING: Removed duplicated region for block: B:16:0x0053 A[RETURN] */
    /* JADX WARNING: Removed duplicated region for block: B:17:0x0054  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onClick(android.view.View r8) {
        /*
            r7 = this;
            java.lang.String r0 = r7.g
            java.lang.String r1 = r7.d()
            boolean r2 = r7.j
            int r3 = r7.l
            r4 = 0
            r5 = 1001(0x3e9, float:1.403E-42)
            if (r3 != r5) goto L_0x0049
            r3 = 2131296984(0x7f0902d8, float:1.82119E38)
            android.view.View r3 = r8.findViewById(r3)
            if (r3 == 0) goto L_0x002b
            java.util.List<b.b.c.d.r> r0 = r7.n
            java.lang.Object r0 = r0.get(r4)
        L_0x001e:
            b.b.c.d.r r0 = (b.b.c.d.r) r0
            java.lang.String r1 = r0.e()
            java.lang.String r2 = r0.d()
            boolean r0 = r0.j
            goto L_0x004d
        L_0x002b:
            r3 = 2131296985(0x7f0902d9, float:1.8211902E38)
            android.view.View r3 = r8.findViewById(r3)
            if (r3 == 0) goto L_0x003c
            java.util.List<b.b.c.d.r> r0 = r7.n
            r1 = 1
        L_0x0037:
            java.lang.Object r0 = r0.get(r1)
            goto L_0x001e
        L_0x003c:
            r3 = 2131296986(0x7f0902da, float:1.8211904E38)
            android.view.View r3 = r8.findViewById(r3)
            if (r3 == 0) goto L_0x0049
            java.util.List<b.b.c.d.r> r0 = r7.n
            r1 = 2
            goto L_0x0037
        L_0x0049:
            r6 = r1
            r1 = r0
            r0 = r2
            r2 = r6
        L_0x004d:
            boolean r3 = android.text.TextUtils.isEmpty(r1)
            if (r3 == 0) goto L_0x0054
            return
        L_0x0054:
            android.content.Context r8 = r8.getContext()
            java.lang.String r3 = "http"
            boolean r3 = r1.startsWith(r3)     // Catch:{ Exception -> 0x007a }
            if (r3 == 0) goto L_0x0075
            if (r0 != 0) goto L_0x0066
            b.b.c.d.t.a(r8, r1, r2)     // Catch:{ Exception -> 0x007a }
            goto L_0x0082
        L_0x0066:
            android.content.Intent r0 = new android.content.Intent     // Catch:{ Exception -> 0x007a }
            java.lang.String r2 = "android.intent.action.VIEW"
            android.net.Uri r1 = android.net.Uri.parse(r1)     // Catch:{ Exception -> 0x007a }
            r0.<init>(r2, r1)     // Catch:{ Exception -> 0x007a }
        L_0x0071:
            r8.startActivity(r0)     // Catch:{ Exception -> 0x007a }
            goto L_0x0082
        L_0x0075:
            android.content.Intent r0 = android.content.Intent.parseUri(r1, r4)     // Catch:{ Exception -> 0x007a }
            goto L_0x0071
        L_0x007a:
            r8 = move-exception
            java.lang.String r0 = "MiActivity"
            java.lang.String r1 = "msg"
            miui.util.Log.e(r0, r1, r8)
        L_0x0082:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: b.b.c.d.r.onClick(android.view.View):void");
    }
}
