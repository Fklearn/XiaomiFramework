package b.b.c.d;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import b.b.c.j.r;
import b.b.c.j.x;
import b.b.g.a;
import b.b.g.b;
import com.miui.applicationlock.c.y;
import com.miui.common.customview.AdImageView;
import com.miui.networkassistant.ui.activity.NetworkDiagnosticsTipActivity;
import com.miui.securitycenter.Application;
import com.miui.securitycenter.R;
import com.miui.securityscan.a.C0536b;
import com.miui.securityscan.cards.g;
import com.miui.securityscan.cards.k;
import com.miui.securityscan.i.c;
import com.miui.securityscan.i.i;
import com.xiaomi.ad.feedback.IAdFeedbackListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import miui.os.Build;
import org.json.JSONArray;
import org.json.JSONObject;

/* renamed from: b.b.c.d.d  reason: case insensitive filesystem */
public class C0184d extends C0185e implements a.C0028a {
    private int A = -1;
    private String B;
    private int C = -1;
    private boolean D;
    private int E;
    private String[] F;
    private String[] G;
    /* access modifiers changed from: private */
    public transient Object H;
    private String I;
    private String J;
    private boolean K;
    private int L;
    private Handler M = new C0181a(this, Looper.getMainLooper());

    /* renamed from: d  reason: collision with root package name */
    private int f1672d;
    private String e;
    private String f;
    private String g;
    private String h;
    /* access modifiers changed from: private */
    public int i;
    private String j;
    protected String[] k = new String[3];
    private long l;
    private int m;
    private String n;
    private String o;
    private String p;
    private String q;
    private String r;
    private String s;
    private String t;
    private String u;
    private String v;
    private String w;
    private String x;
    private String y;
    private String z;

    public C0184d() {
    }

    public C0184d(JSONObject jSONObject) {
        a(jSONObject);
    }

    public static C0184d a(long j2, JSONObject jSONObject, String str) {
        if (jSONObject == null) {
            return null;
        }
        int optInt = jSONObject.optInt("template");
        if (optInt != 3 && optInt != 4 && optInt != 5 && optInt != 25 && optInt != 31 && optInt != 40 && optInt != 10001 && optInt != 30001 && optInt != 30002) {
            return null;
        }
        Log.d("Advertisement", "mTagId: " + str);
        return o.a(j2, jSONObject, str, optInt);
    }

    public static String a(long j2) {
        Resources resources = Application.d().getResources();
        String language = Locale.getDefault().getLanguage();
        if (j2 < 10000 || !language.equals("zh")) {
            int i2 = (int) j2;
            return resources.getQuantityString(R.plurals.people, i2, new Object[]{Integer.valueOf(i2)});
        }
        int i3 = (int) (j2 / 10000);
        return resources.getQuantityString(R.plurals.people_million, i3, new Object[]{Integer.valueOf(i3)});
    }

    private void a(Context context) {
        if (!i.b(context, this.p)) {
            if (TextUtils.isEmpty(this.q)) {
                i.b(context, this.h);
            } else if (k.a(context).a(this.q)) {
                t.a(context, this.q);
            } else if (!c.f(context)) {
                c.a(context, (int) R.string.toast_network_eror);
            } else {
                x.a(context, this.q, this.s, this.r, this.t, this.u, this.v, this.w, this.y);
                g a2 = g.a(context);
                a2.a(this.q, this.D);
                a2.a(this.q, 10);
                Toast.makeText(context, context.getResources().getString(R.string.start_downloading_app, new Object[]{this.e}), 0).show();
            }
        }
    }

    private void a(Context context, Button button, boolean z2) {
        int color;
        Resources resources;
        int i2;
        int i3;
        int i4;
        boolean a2 = k.a(context).a(this.q);
        boolean z3 = true;
        boolean z4 = false;
        if (a2) {
            if (TextUtils.isEmpty(this.B)) {
                button.setText(R.string.open_app);
            } else {
                button.setText(this.B);
            }
            z4 = true;
        } else {
            int b2 = g.a(context).b(this.q);
            if (!(b2 == -1 || b2 == 5)) {
                if (b2 == 10) {
                    i4 = R.string.connecting;
                } else if (!(b2 == 1 || b2 == 2)) {
                    if (b2 != 3) {
                        button.setText(this.z);
                        z4 = true;
                        z3 = false;
                    } else {
                        i4 = R.string.installing;
                    }
                }
                button.setText(i4);
                z3 = false;
            }
            button.setText(R.string.downloading);
            z3 = false;
        }
        if (this.i == 25) {
            if (z4) {
                if (a2) {
                    i3 = this.C;
                    if (i3 == -1) {
                        resources = context.getResources();
                        i2 = R.color.btn_color_red;
                    }
                } else {
                    i3 = this.A;
                    if (i3 == -1) {
                        resources = context.getResources();
                        i2 = R.color.btn_color_cyan;
                    }
                }
                button.setTextColor(i3);
            } else {
                button.setTextColor(R.color.ad_button_connect);
            }
            button.setEnabled(z4);
        }
        if (z2) {
            button.setBackgroundResource(z3 ? R.drawable.common_button_cyan : R.drawable.common_button_blue2);
            if (z4) {
                resources = context.getResources();
                i2 = R.color.result_blue_button_text;
            } else {
                color = context.getResources().getColor(R.color.ad_button_connect);
                button.setTextColor(color);
            }
        }
        button.setEnabled(z4);
        color = resources.getColor(i2);
        button.setTextColor(color);
        button.setEnabled(z4);
    }

    private void a(View view) {
        o.a(view.getContext(), this.H);
    }

    private void b(Context context) {
        if (!i.b(context, this.p)) {
            if (TextUtils.isEmpty(this.q)) {
                i.b(context, this.h);
                return;
            }
            try {
                Intent intent = new Intent("android.intent.action.VIEW");
                intent.setData(Uri.parse(String.format("mimarket://details?id=%s&back=true&ref=%s&ext_passback=%s", new Object[]{this.q, this.s, this.r})));
                context.startActivity(intent);
            } catch (Exception e2) {
                Log.e("Advertisement", "msg", e2);
            }
        }
    }

    private void c(Context context) {
        y b2 = y.b();
        C0182b bVar = new C0182b(this);
        if (b2.a(context.getApplicationContext())) {
            b2.a(context.getApplicationContext(), (IAdFeedbackListener) bVar, "com.miui.securitycenter", Build.IS_INTERNATIONAL_BUILD ? "com.miui.securitycenter_globaladevent" : "com.miui.securitycenter_datamodel", d());
        } else {
            Log.e("Advertisement", "connect fail, maybe not support dislike window");
        }
    }

    /* access modifiers changed from: private */
    public void o() {
        new Handler(Looper.getMainLooper()).post(new C0183c(this));
    }

    public int a() {
        int i2 = this.i;
        return i2 != 3 ? i2 != 4 ? i2 != 5 ? i2 != 25 ? i2 != 31 ? i2 != 40 ? (i2 == 10001 || i2 == 30001 || i2 == 30002) ? o.a(this.L) : R.layout.v_result_item_template_empty : R.layout.result_ad_template_40 : R.layout.result_ad_template_31 : R.layout.result_ad_template_25 : R.layout.result_ad_template_5 : R.layout.result_ad_template_4 : R.layout.result_ad_template_3;
    }

    public void a(int i2) {
        this.L = i2;
    }

    public void a(int i2, View view, Context context, C0191k kVar) {
        ImageView imageView;
        super.a(i2, view, context, kVar);
        int dimensionPixelOffset = context.getResources().getDimensionPixelOffset(R.dimen.card_layout_line_height_half);
        boolean z2 = false;
        view.setPadding(0, dimensionPixelOffset, 0, dimensionPixelOffset);
        int i3 = this.i;
        if (i3 == 3) {
            H h2 = (H) view.getTag();
            h2.f1659a.setBackground(context.getResources().getDrawable(R.drawable.shape_result_card_border_whole));
            h2.f1657c.setText(this.f);
            h2.f1656b.setText(this.g);
            r.a(this.k[0], h2.f1658d, t.a());
            imageView = h2.f1658d;
        } else if (i3 == 4) {
            x xVar = (x) view.getTag();
            xVar.f1659a.setBackground(context.getResources().getDrawable(R.drawable.shape_result_card_border_whole));
            xVar.f1700d.setText(this.f);
            xVar.f1699c.setText(this.g);
            r.a(this.k[0], xVar.f1698b, t.b());
            imageView = xVar.f1698b;
        } else if (i3 == 5) {
            G g2 = (G) view.getTag();
            g2.f1659a.setBackground(context.getResources().getDrawable(R.drawable.shape_result_card_border_whole));
            g2.f1655d.setText(this.f);
            g2.f1654c.setText(this.e);
            r.a(this.j, g2.f1653b, r.g, (int) R.drawable.icon_def);
            a(context, g2.e, true);
            imageView = g2.f1653b;
        } else if (i3 == 25) {
            D d2 = (D) view.getTag();
            d2.f1659a.setBackground(context.getResources().getDrawable(R.drawable.shape_result_card_border_whole));
            d2.e.setText(this.f);
            d2.f1644d.setText(this.e);
            r.a(this.k[0], d2.f1643c, t.a());
            r.a(this.j, d2.f1642b, r.g, (int) R.drawable.icon_def);
            Button button = d2.f;
            if (this.i != 25) {
                z2 = true;
            }
            a(context, button, z2);
            imageView = d2.f1642b;
        } else if (i3 == 31) {
            u uVar = (u) view.getTag();
            uVar.f1659a.setBackground(context.getResources().getDrawable(R.drawable.shape_result_card_border_whole));
            uVar.f1695b.setText(this.e);
            uVar.f1696c.setText(this.f);
            r.a(this.k[0], uVar.f1697d, t.b());
            r.a(this.k[1], uVar.e, t.b());
            r.a(this.k[2], uVar.f, t.b());
            a(context, uVar.g, false);
            imageView = uVar.f1697d;
        } else if (i3 == 40) {
            y yVar = (y) view.getTag();
            yVar.f1659a.setBackground(context.getResources().getDrawable(R.drawable.shape_result_card_border_whole));
            yVar.f1701b.setText(this.g);
            yVar.f1702c.setText(this.f);
            yVar.f1703d.setText(this.n);
            s.a(yVar.f1701b, this.g, this.n);
            r.a(this.k[0], yVar.e, t.b());
            r.a(this.k[1], yVar.f, t.b());
            r.a(this.k[2], yVar.g, t.b());
            imageView = yVar.e;
        } else if (i3 == 10001 || i3 == 30001 || i3 == 30002) {
            b bVar = (b) view.getTag();
            o.a(i());
            Log.d("Advertisement", "International Ads reportPV : " + i());
            if (!this.K || !bVar.j) {
                view.setBackgroundResource(0);
                return;
            }
            o.a(bVar, this.L, this.H);
            bVar.h.setBackgroundResource(R.drawable.card_bg_no_shadow_selector);
            bVar.f1776a.setText(this.e);
            bVar.e.setText(this.I);
            if (TextUtils.isEmpty(this.f)) {
                bVar.f1777b.setVisibility(8);
            } else {
                bVar.f1777b.setText(this.f);
                bVar.f1777b.setVisibility(0);
            }
            ImageView imageView2 = bVar.f1779d;
            if (imageView2 != null) {
                r.a(this.k[0], imageView2, r.g, (int) R.drawable.icon_def);
            }
            ImageView imageView3 = bVar.f1778c;
            if (imageView3 != null) {
                r.a(this.k[1], imageView3, t.a());
            }
            o.a(context, bVar.f, this.L, this.H, bVar.i);
            bVar.g.setOnClickListener(this);
            return;
        } else {
            return;
        }
        a((AdImageView) imageView, i2, this);
    }

    public void a(AdImageView adImageView, int i2, C0184d dVar) {
        adImageView.a(this.M, i2, dVar);
    }

    public void a(Object obj) {
        this.H = obj;
    }

    public void a(String str, C0184d dVar) {
        ArrayList arrayList = new ArrayList();
        arrayList.add(new C0536b.C0066b(str, dVar));
        C0536b.a((Context) Application.d(), (List<Object>) arrayList);
    }

    public void a(JSONObject jSONObject) {
        if (jSONObject != null) {
            this.f1672d = jSONObject.optInt("id");
            this.e = jSONObject.optString("appName");
            if (TextUtils.isEmpty(this.e)) {
                this.e = jSONObject.optString(NetworkDiagnosticsTipActivity.TITLE_KEY_NAME);
            }
            this.f = jSONObject.optString("summary");
            this.g = jSONObject.optString("source");
            this.h = jSONObject.optString("landingPageUrl");
            this.i = jSONObject.optInt("template");
            this.l = jSONObject.optLong("allDownloadNum");
            this.m = jSONObject.optInt("appRatingScore", -1);
            this.n = a(this.l);
            this.j = jSONObject.optString("iconUrl");
            this.o = jSONObject.optString("actionUrl");
            this.p = jSONObject.optString("deeplink");
            this.q = jSONObject.optString("packageName");
            this.r = jSONObject.optString("ex");
            this.s = jSONObject.optString("appRef");
            this.t = jSONObject.optString("appClientId");
            this.u = jSONObject.optString("appSignature");
            this.v = jSONObject.optString("nonce");
            this.w = jSONObject.optString("appChannel");
            this.y = jSONObject.optString("floatCardData");
            JSONObject optJSONObject = jSONObject.optJSONObject("extra");
            if (optJSONObject != null) {
                this.z = optJSONObject.optString("button");
                String optString = optJSONObject.optString("buttonColor");
                if (!TextUtils.isEmpty(optString)) {
                    try {
                        this.A = Color.parseColor(optString);
                    } catch (Exception unused) {
                    }
                }
                String optString2 = optJSONObject.optString("buttonOpenColor");
                if (!TextUtils.isEmpty(optString2)) {
                    try {
                        this.C = Color.parseColor(optString2);
                    } catch (Exception unused2) {
                    }
                }
                this.B = optJSONObject.optString("buttonOpen");
                this.D = optJSONObject.optBoolean("autoOpen");
            }
            JSONArray optJSONArray = jSONObject.optJSONArray("imgUrls");
            if (optJSONArray != null) {
                int length = optJSONArray.length();
                int i2 = 0;
                while (i2 < 3 && i2 < length) {
                    this.k[i2] = optJSONArray.optString(i2);
                    i2++;
                }
            }
            this.E = jSONObject.optInt("targetType");
            JSONArray optJSONArray2 = jSONObject.optJSONArray("viewMonitorUrls");
            if (optJSONArray2 != null && optJSONArray2.length() > 0) {
                this.F = new String[optJSONArray2.length()];
                for (int i3 = 0; i3 < optJSONArray2.length(); i3++) {
                    this.F[i3] = optJSONArray2.optString(i3);
                }
            }
            JSONArray optJSONArray3 = jSONObject.optJSONArray("clickMonitorUrls");
            if (optJSONArray3 != null && optJSONArray3.length() > 0) {
                this.G = new String[optJSONArray3.length()];
                for (int i4 = 0; i4 < optJSONArray3.length(); i4++) {
                    this.G[i4] = optJSONArray3.optString(i4);
                }
            }
        }
    }

    public void a(boolean z2) {
        this.K = z2;
    }

    public void a(String[] strArr) {
        this.k = strArr;
    }

    public void b(int i2) {
        this.f1672d = i2;
    }

    public void b(String str) {
        this.I = str;
    }

    public String[] b() {
        return this.G;
    }

    public String c() {
        return this.I;
    }

    public void c(String str) {
        this.J = str;
    }

    public String d() {
        return this.r;
    }

    public void d(C0184d dVar) {
        if (dVar != null) {
            Log.d("Advertisement", "fill ad");
            b(dVar.f());
            b(dVar.c());
            a(dVar.h());
            f(dVar.l());
            d(dVar.j());
            a(dVar.g());
            a(dVar.e());
            a(true);
            a.a().a(dVar.h(), this);
        }
    }

    public void d(String str) {
        this.f = str;
    }

    public int e() {
        return this.L;
    }

    public void e(String str) {
        this.x = str;
    }

    public int f() {
        return this.f1672d;
    }

    public void f(String str) {
        this.e = str;
    }

    public String[] g() {
        return this.k;
    }

    public Object h() {
        return this.H;
    }

    public String i() {
        return this.J;
    }

    public String j() {
        return this.f;
    }

    public int k() {
        return this.i;
    }

    public String l() {
        return this.e;
    }

    public String[] m() {
        return this.F;
    }

    public boolean n() {
        return this.K && this.L > 0;
    }

    public void onClick(View view) {
        Context context = view.getContext();
        int i2 = this.i;
        if (i2 != 10001 && i2 != 30001 && i2 != 30002) {
            int id = view.getId();
            if (id == R.id.button) {
                a(context);
            } else if (id != R.id.close) {
                b(context);
            } else {
                c(context);
            }
        } else if (o.a(view)) {
            a(view);
        } else {
            o.a(i(), this.H);
        }
    }
}
