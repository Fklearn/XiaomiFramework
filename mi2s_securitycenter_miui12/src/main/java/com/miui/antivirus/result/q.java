package com.miui.antivirus.result;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import b.b.b.a.b;
import b.b.c.j.r;
import com.miui.antivirus.activity.MainActivity;
import com.miui.applicationlock.c.y;
import com.miui.networkassistant.ui.activity.NetworkDiagnosticsTipActivity;
import com.miui.securitycenter.Application;
import com.miui.securitycenter.R;
import com.miui.warningcenter.mijia.MijiaAlertModel;
import com.xiaomi.ad.feedback.IAdFeedbackListener;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import miui.os.Build;
import miui.util.Log;
import org.json.JSONObject;

public class q extends C0244g {
    private String f;
    private String g;
    private String h;
    private String i;
    private String j;
    private String k;
    private boolean l;
    private boolean m;
    private int n = -1;
    private int o = 1;
    private String p;
    private List<q> q;

    public q() {
    }

    private q(JSONObject jSONObject) {
        this.h = jSONObject.optString("img");
        this.f = jSONObject.optString(NetworkDiagnosticsTipActivity.TITLE_KEY_NAME);
        this.g = jSONObject.optString("summary");
        this.j = jSONObject.optString("cornerTip");
        this.i = jSONObject.optString(MijiaAlertModel.KEY_URL);
        this.o = jSONObject.optInt("template");
        this.k = jSONObject.optString("button");
        this.p = jSONObject.optString("dataId");
        this.l = jSONObject.optBoolean("browserOpen", true);
        if (!Build.IS_INTERNATIONAL_BUILD) {
            this.m = jSONObject.optBoolean("showAdChoice", false);
        }
        String optString = jSONObject.optString("buttonColor");
        if (!TextUtils.isEmpty(optString)) {
            try {
                this.n = Color.parseColor(optString);
            } catch (Exception e) {
                Log.e("MiActivity", "msg", e);
            }
        }
    }

    public static q a(JSONObject jSONObject) {
        switch (jSONObject.optInt("template")) {
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
                q qVar = new q(jSONObject);
                if (TextUtils.isEmpty(qVar.i)) {
                    return null;
                }
                try {
                    List<ResolveInfo> queryIntentActivities = Application.d().getPackageManager().queryIntentActivities(Intent.parseUri(qVar.i, 0), 32);
                    if (queryIntentActivities == null || queryIntentActivities.size() == 0) {
                        return null;
                    }
                    return qVar;
                } catch (Exception e) {
                    Log.e("MiActivity", "msg", e);
                }
                break;
            default:
                return null;
        }
    }

    private void a(MainActivity mainActivity) {
        y b2 = y.b();
        o oVar = new o(this, new WeakReference(mainActivity));
        if (b2.a(mainActivity.getApplicationContext())) {
            b2.a(mainActivity.getApplicationContext(), (IAdFeedbackListener) oVar, "com.miui.securitycenter", Build.IS_INTERNATIONAL_BUILD ? "com.miui.securitycenter_globaladevent" : "com.miui.securitycenter_virusresult", "");
            return;
        }
        Log.e("MiActivity", "connect fail, maybe not support dislike window");
        b(mainActivity);
    }

    /* access modifiers changed from: private */
    public void b(MainActivity mainActivity) {
        new Handler(Looper.getMainLooper()).post(new p(this, mainActivity));
    }

    public void a(int i2) {
        this.o = i2;
    }

    public void a(int i2, View view, Context context, t tVar) {
        super.a(i2, view, context, tVar);
        int i3 = 0;
        if (this.o != 1001) {
            w wVar = (w) view.getTag();
            wVar.f2864b.setText(this.g);
            wVar.f2863a.setText(this.f);
            View view2 = wVar.e;
            if (view2 != null) {
                if (!this.m) {
                    i3 = 4;
                }
                view2.setVisibility(i3);
            }
            int i4 = this.o;
            if (i4 == 4 || i4 == 6) {
                r.a(this.h, wVar.f2865c, r.g, (int) R.drawable.icon_def);
            } else {
                r.a(this.h, wVar.f2865c, tVar.a());
            }
            Button button = wVar.f2866d;
            if (button != null) {
                button.setText(this.k);
                button.setTextColor(this.n);
            }
        } else {
            u uVar = (u) view.getTag();
            if (this.q.size() >= 3) {
                q qVar = this.q.get(0);
                uVar.f2858d.setText(qVar.e());
                if (!qVar.c().isEmpty() && uVar.g != null) {
                    r.a(qVar.c(), uVar.g, r.g, (int) R.drawable.icon_def);
                }
                q qVar2 = this.q.get(1);
                uVar.e.setText(qVar2.e());
                if (!qVar2.c().isEmpty() && uVar.h != null) {
                    r.a(qVar2.c(), uVar.h, r.g, (int) R.drawable.icon_def);
                }
                q qVar3 = this.q.get(2);
                uVar.f.setText(qVar3.e());
                if (!qVar3.c().isEmpty() && uVar.i != null) {
                    r.a(qVar3.c(), uVar.i, r.g, (int) R.drawable.icon_def);
                }
            } else {
                return;
            }
        }
        b.C0023b.e(this.p);
    }

    public void a(q qVar) {
        if (this.q == null) {
            this.q = new ArrayList();
        }
        this.q.add(qVar);
    }

    public String c() {
        return this.h;
    }

    public int d() {
        List<q> list = this.q;
        if (list == null) {
            return 0;
        }
        return list.size();
    }

    public String e() {
        return this.f;
    }

    public String f() {
        return this.i;
    }

    public int getLayoutId() {
        int i2 = this.o;
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

    /* JADX WARNING: Removed duplicated region for block: B:19:0x007b A[RETURN] */
    /* JADX WARNING: Removed duplicated region for block: B:20:0x007c  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onClick(android.view.View r8) {
        /*
            r7 = this;
            int r0 = r8.getId()
            r1 = 2131296620(0x7f09016c, float:1.8211162E38)
            if (r0 != r1) goto L_0x0013
            android.content.Context r8 = r8.getContext()
            com.miui.antivirus.activity.MainActivity r8 = (com.miui.antivirus.activity.MainActivity) r8
            r7.a((com.miui.antivirus.activity.MainActivity) r8)
            return
        L_0x0013:
            java.lang.String r0 = r7.i
            java.lang.String r1 = r7.p
            java.lang.String r2 = r7.e()
            boolean r3 = r7.l
            int r4 = r7.o
            r5 = 1001(0x3e9, float:1.403E-42)
            if (r4 != r5) goto L_0x0071
            r4 = 2131296984(0x7f0902d8, float:1.82119E38)
            android.view.View r4 = r8.findViewById(r4)
            if (r4 == 0) goto L_0x0042
            java.util.List<com.miui.antivirus.result.q> r0 = r7.q
            r1 = 0
            java.lang.Object r0 = r0.get(r1)
            com.miui.antivirus.result.q r0 = (com.miui.antivirus.result.q) r0
            java.lang.String r1 = r0.f()
            java.lang.String r2 = r0.e()
            java.lang.String r3 = r0.p
            boolean r0 = r0.l
            goto L_0x0075
        L_0x0042:
            r4 = 2131296985(0x7f0902d9, float:1.8211902E38)
            android.view.View r4 = r8.findViewById(r4)
            if (r4 == 0) goto L_0x0064
            java.util.List<com.miui.antivirus.result.q> r0 = r7.q
            r1 = 1
        L_0x004e:
            java.lang.Object r0 = r0.get(r1)
            com.miui.antivirus.result.q r0 = (com.miui.antivirus.result.q) r0
            java.lang.String r1 = r0.f()
            java.lang.String r2 = r0.p
            java.lang.String r3 = r0.e()
            boolean r0 = r0.l
            r6 = r3
            r3 = r2
            r2 = r6
            goto L_0x0075
        L_0x0064:
            r4 = 2131296986(0x7f0902da, float:1.8211904E38)
            android.view.View r4 = r8.findViewById(r4)
            if (r4 == 0) goto L_0x0071
            java.util.List<com.miui.antivirus.result.q> r0 = r7.q
            r1 = 2
            goto L_0x004e
        L_0x0071:
            r6 = r1
            r1 = r0
            r0 = r3
            r3 = r6
        L_0x0075:
            boolean r4 = android.text.TextUtils.isEmpty(r1)
            if (r4 == 0) goto L_0x007c
            return
        L_0x007c:
            android.content.Context r8 = r8.getContext()
            if (r0 != 0) goto L_0x008e
            java.lang.String r0 = "http"
            boolean r0 = r1.startsWith(r0)     // Catch:{ Exception -> 0x0092 }
            if (r0 == 0) goto L_0x008e
            com.miui.securityscan.i.i.c(r8, r1, r2)     // Catch:{ Exception -> 0x0092 }
            goto L_0x009a
        L_0x008e:
            com.miui.antivirus.result.C0244g.a(r8, r1)     // Catch:{ Exception -> 0x0092 }
            goto L_0x009a
        L_0x0092:
            r8 = move-exception
            java.lang.String r0 = "MiActivity"
            java.lang.String r1 = "msg"
            miui.util.Log.e(r0, r1, r8)
        L_0x009a:
            b.b.b.a.b.C0023b.d(r3)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.antivirus.result.q.onClick(android.view.View):void");
    }
}
