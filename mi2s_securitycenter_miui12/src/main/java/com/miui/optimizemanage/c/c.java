package com.miui.optimizemanage.c;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import b.b.c.j.l;
import b.b.c.j.r;
import com.miui.applicationlock.c.y;
import com.miui.common.card.FillParentDrawable;
import com.miui.networkassistant.ui.activity.NetworkDiagnosticsTipActivity;
import com.miui.optimizemanage.OptimizemanageMainActivity;
import com.miui.securitycenter.R;
import com.miui.securitycenter.utils.b;
import com.miui.securityscan.i.i;
import com.miui.warningcenter.mijia.MijiaAlertModel;
import com.xiaomi.ad.feedback.IAdFeedbackListener;
import miui.os.Build;
import org.json.JSONObject;

public class c extends d {
    /* access modifiers changed from: private */

    /* renamed from: c  reason: collision with root package name */
    public String f5886c;
    /* access modifiers changed from: private */

    /* renamed from: d  reason: collision with root package name */
    public String f5887d;
    /* access modifiers changed from: private */
    public String e;
    private String f;
    /* access modifiers changed from: private */
    public String g;
    /* access modifiers changed from: private */
    public String h;
    private boolean i;
    /* access modifiers changed from: private */
    public int j = -1;
    /* access modifiers changed from: private */
    public boolean k = false;
    /* access modifiers changed from: private */
    public int l = -1;
    /* access modifiers changed from: private */
    public int m = -1;
    /* access modifiers changed from: private */
    public int n;
    /* access modifiers changed from: private */
    public boolean o = false;
    /* access modifiers changed from: private */
    public boolean p = false;
    /* access modifiers changed from: private */
    public boolean q = false;

    public static class a extends e {

        /* renamed from: a  reason: collision with root package name */
        ImageView f5888a;

        /* renamed from: b  reason: collision with root package name */
        TextView f5889b;

        /* renamed from: c  reason: collision with root package name */
        TextView f5890c;

        /* renamed from: d  reason: collision with root package name */
        Button f5891d;
        View e;

        public a(View view) {
            super(view);
            this.f5888a = (ImageView) view.findViewById(R.id.image1);
            this.f5889b = (TextView) view.findViewById(R.id.title);
            this.f5890c = (TextView) view.findViewById(R.id.summary);
            this.f5891d = (Button) view.findViewById(R.id.button);
            this.e = view.findViewById(R.id.close);
            this.f5888a.setColorFilter(view.getResources().getColor(R.color.result_banner_icon_bg));
            l.a(view);
        }

        public void a(View view, d dVar, int i) {
            super.a(view, dVar, i);
            c cVar = (c) dVar;
            Resources resources = view.getContext().getResources();
            int i2 = 4;
            if (cVar.n == 4 || cVar.n == 6) {
                r.a(cVar.e, this.f5888a, r.g, (int) R.drawable.icon_def);
            } else {
                r.a(cVar.e, this.f5888a, cVar.a(resources));
            }
            this.f5889b.setText(cVar.f5886c);
            View view2 = this.e;
            int i3 = 0;
            if (view2 != null) {
                if (cVar.q) {
                    i2 = 0;
                }
                view2.setVisibility(i2);
                this.e.setOnClickListener(cVar);
            }
            this.f5890c.setText(cVar.f5887d);
            TextView textView = this.f5890c;
            if (cVar.n == 7) {
                i3 = 8;
            }
            textView.setVisibility(i3);
            if (this.f5891d != null) {
                this.f5891d.setTextColor(cVar.k ? cVar.j : resources.getColor(R.color.result_small_button_text_color));
                this.f5891d.setText(cVar.g);
                this.f5891d.setOnClickListener(cVar);
                int a2 = cVar.n;
                Drawable a3 = cVar.o ? b.a(resources.getDimension(R.dimen.pc_scanning_result_fix_button_radius_size), cVar.l, cVar.m) : resources.getDrawable(R.drawable.scanresult_button_blue);
                if (a3 != null) {
                    this.f5891d.setBackground(a3);
                }
            }
            view.setOnClickListener(cVar);
            if (!cVar.p) {
                com.miui.optimizemanage.a.a.c(cVar.h);
                boolean unused = cVar.p = true;
            }
        }
    }

    public c(JSONObject jSONObject) {
        a(jSONObject);
        a(d());
    }

    public static c a(int i2, JSONObject jSONObject) {
        return new c(jSONObject);
    }

    /* access modifiers changed from: private */
    public void a(OptimizemanageMainActivity optimizemanageMainActivity) {
        new Handler(Looper.getMainLooper()).post(new b(this, optimizemanageMainActivity));
    }

    private void a(JSONObject jSONObject) {
        if (jSONObject != null) {
            this.e = jSONObject.optString("img");
            this.f5886c = jSONObject.optString(NetworkDiagnosticsTipActivity.TITLE_KEY_NAME);
            this.f5887d = jSONObject.optString("summary");
            this.f = jSONObject.optString(MijiaAlertModel.KEY_URL);
            this.n = jSONObject.optInt("template");
            this.g = jSONObject.optString("button");
            this.h = jSONObject.optString("dataId");
            this.i = jSONObject.optBoolean("browserOpen", true);
            this.q = jSONObject.optBoolean("showAdChoice", false);
            String optString = jSONObject.optString("buttonColor2");
            if (!TextUtils.isEmpty(optString)) {
                try {
                    this.j = Color.parseColor(optString);
                    this.k = true;
                } catch (Exception unused) {
                }
            }
            String optString2 = jSONObject.optString("btnBgColorOpenN2");
            String optString3 = jSONObject.optString("btnBgColorOpenP2");
            if (!TextUtils.isEmpty(optString2) && !TextUtils.isEmpty(optString3)) {
                try {
                    this.l = Color.parseColor(optString2);
                    this.m = Color.parseColor(optString3);
                    this.o = true;
                } catch (Exception unused2) {
                }
            }
        }
    }

    private void b(OptimizemanageMainActivity optimizemanageMainActivity) {
        y b2 = y.b();
        a aVar = new a(this, optimizemanageMainActivity);
        if (b2.a(optimizemanageMainActivity.getApplicationContext())) {
            b2.a(optimizemanageMainActivity.getApplicationContext(), (IAdFeedbackListener) aVar, "com.miui.securitycenter", Build.IS_INTERNATIONAL_BUILD ? "com.miui.securitycenter_globaladevent" : "com.miui.securitycenter_appmanager", (String) null);
        } else {
            Log.e("OMActivityCardModel", "connect fail, maybe not support dislike window");
        }
    }

    public Drawable a(Resources resources) {
        return new FillParentDrawable(resources.getDrawable(R.drawable.big_backgroud_def));
    }

    public e a(View view) {
        return new a(view);
    }

    public int d() {
        int i2 = this.n;
        if (i2 == 1) {
            return R.layout.om_result_activity_template_1;
        }
        if (i2 != 3) {
            if (i2 == 4 || i2 == 6) {
                return R.layout.om_result_activity_template_4;
            }
            if (i2 != 7) {
                return R.layout.om_result_activity_template_1;
            }
        }
        return R.layout.om_result_activity_template_3;
    }

    public void onClick(View view) {
        if (view.getId() == R.id.close) {
            b((OptimizemanageMainActivity) view.getContext());
        } else if (!TextUtils.isEmpty(this.f)) {
            try {
                if (this.i || !this.f.startsWith("http")) {
                    view.getContext().startActivity(Intent.parseUri(this.f, 0));
                } else {
                    i.c(view.getContext(), this.f, this.f5886c);
                }
            } catch (Exception unused) {
            }
            com.miui.optimizemanage.a.a.b(this.h);
        }
    }
}
