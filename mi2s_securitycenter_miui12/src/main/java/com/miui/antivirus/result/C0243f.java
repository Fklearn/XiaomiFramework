package com.miui.antivirus.result;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import b.b.b.a.b;
import b.b.c.d.s;
import b.b.c.j.r;
import b.b.g.a;
import b.b.g.b;
import com.miui.antivirus.activity.MainActivity;
import com.miui.applicationlock.c.y;
import com.miui.common.customview.AdImageView;
import com.miui.maml.folme.AnimatedProperty;
import com.miui.networkassistant.ui.activity.NetworkDiagnosticsTipActivity;
import com.miui.securitycenter.Application;
import com.miui.securitycenter.R;
import com.miui.securitycenter.p;
import com.miui.securityscan.cards.g;
import com.miui.securityscan.cards.k;
import com.xiaomi.ad.feedback.IAdFeedbackListener;
import java.lang.ref.WeakReference;
import java.util.Locale;
import miui.os.Build;
import org.json.JSONArray;
import org.json.JSONObject;

/* renamed from: com.miui.antivirus.result.f  reason: case insensitive filesystem */
public class C0243f extends C0244g implements a.C0028a {
    private String A;
    private String B;
    private int C = -1;
    private String D;
    private int E = -1;
    private boolean F;
    private int G;
    private String[] H;
    private String[] I;
    /* access modifiers changed from: private */
    public transient Object J;
    private transient View K;
    private String L;
    private String M;
    private boolean N;
    private int O;
    private boolean P;
    private int f;
    private boolean g;
    private String h;
    private String i;
    private String j;
    private String k;
    /* access modifiers changed from: private */
    public int l;
    private String m;
    protected String[] n = new String[3];
    private long o;
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

    public C0243f() {
    }

    public C0243f(JSONObject jSONObject) {
        a(jSONObject);
    }

    public static C0243f a(JSONObject jSONObject, String str) {
        if (jSONObject == null) {
            return null;
        }
        int optInt = jSONObject.optInt("template");
        if (optInt == 3 || optInt == 4 || optInt == 5 || optInt == 25 || optInt == 31 || optInt == 40 || optInt == 10001 || optInt == 30001 || optInt == 30002) {
            return C0251n.a(jSONObject, str, optInt);
        }
        return null;
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

    private void a(Context context, Button button, boolean z2) {
        String str;
        int i2;
        boolean z3 = false;
        if (!k.a(context).a(this.s)) {
            int b2 = g.a(context).b(this.s);
            if (b2 != -1) {
                if (b2 != 5) {
                    if (b2 == 10) {
                        i2 = R.string.connecting;
                    } else if (b2 != 1) {
                        if (b2 != 2) {
                            if (b2 != 3) {
                                str = this.B;
                            } else {
                                i2 = R.string.installing;
                            }
                        }
                    }
                    button.setText(i2);
                    button.setEnabled(z3);
                }
                int a2 = g.a(context).a(this.s);
                if (a2 != -1) {
                    button.setText(a2 + "%");
                    button.setEnabled(z3);
                }
            }
            button.setText(R.string.downloading);
            button.setEnabled(z3);
        } else if (TextUtils.isEmpty(this.D)) {
            button.setText(R.string.open_app);
            z3 = true;
            button.setEnabled(z3);
        } else {
            str = this.D;
        }
        button.setText(str);
        z3 = true;
        button.setEnabled(z3);
    }

    private void a(View view) {
        this.K = view;
        C0251n.a(view.getContext(), this.J);
    }

    private void a(MainActivity mainActivity) {
        y b2 = y.b();
        C0241d dVar = new C0241d(this, new WeakReference(mainActivity));
        if (b2.a(mainActivity.getApplicationContext())) {
            b2.a(mainActivity.getApplicationContext(), (IAdFeedbackListener) dVar, "com.miui.securitycenter", Build.IS_INTERNATIONAL_BUILD ? "com.miui.securitycenter_globaladevent" : "com.miui.securitycenter_virusresult", f());
        } else {
            Log.e("Advertisement", "connect fail,maybe not support dislike window");
        }
    }

    private void a(MainActivity mainActivity, View view) {
        View inflate = mainActivity.getLayoutInflater().inflate(R.layout.result_unlike_pop_window, (ViewGroup) null);
        Resources resources = mainActivity.getResources();
        int i2 = resources.getDisplayMetrics().widthPixels;
        PopupWindow popupWindow = new PopupWindow(inflate, -2, -2);
        inflate.measure(View.MeasureSpec.makeMeasureSpec(i2, Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(10, Integer.MIN_VALUE));
        inflate.setOnClickListener(new C0240c(this, popupWindow, mainActivity));
        int measuredWidth = inflate.getMeasuredWidth();
        popupWindow.setOutsideTouchable(true);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        int[] iArr = new int[2];
        view.getLocationInWindow(iArr);
        popupWindow.showAtLocation(view, 0, iArr[0] - measuredWidth, iArr[1] - resources.getDimensionPixelOffset(R.dimen.result_popwindow_offset));
    }

    private void a(JSONObject jSONObject) {
        if (jSONObject != null) {
            this.f = jSONObject.optInt("id");
            this.h = jSONObject.optString("appName");
            if (TextUtils.isEmpty(this.h)) {
                this.h = jSONObject.optString(NetworkDiagnosticsTipActivity.TITLE_KEY_NAME);
            }
            this.i = jSONObject.optString("summary");
            this.j = jSONObject.optString("source");
            this.k = jSONObject.optString("landingPageUrl");
            this.l = jSONObject.optInt("template");
            this.o = jSONObject.optLong("allDownloadNum");
            this.p = a(this.o);
            this.m = jSONObject.optString("iconUrl");
            this.q = jSONObject.optString("actionUrl");
            this.r = jSONObject.optString("deeplink");
            this.s = jSONObject.optString("packageName");
            this.t = jSONObject.optString("ex");
            this.u = jSONObject.optString("appRef");
            this.v = jSONObject.optString("appClientId");
            this.w = jSONObject.optString("appSignature");
            this.x = jSONObject.optString("nonce");
            this.y = jSONObject.optString("appChannel");
            this.z = jSONObject.optString("floatCardData");
            this.g = jSONObject.optBoolean("local");
            JSONObject optJSONObject = jSONObject.optJSONObject("extra");
            if (optJSONObject != null) {
                this.B = optJSONObject.optString("button");
                String optString = optJSONObject.optString("buttonColor");
                if (!TextUtils.isEmpty(optString)) {
                    try {
                        this.C = Color.parseColor(optString);
                    } catch (Exception unused) {
                    }
                }
                String optString2 = optJSONObject.optString("buttonOpenColor");
                if (!TextUtils.isEmpty(optString2)) {
                    try {
                        this.E = Color.parseColor(optString2);
                    } catch (Exception unused2) {
                    }
                }
                this.D = optJSONObject.optString("buttonOpen");
                this.F = optJSONObject.optBoolean("autoOpen");
            }
            JSONArray optJSONArray = jSONObject.optJSONArray("imgUrls");
            if (optJSONArray != null) {
                int length = optJSONArray.length();
                int i2 = 0;
                while (i2 < 3 && i2 < length) {
                    this.n[i2] = optJSONArray.optString(i2);
                    i2++;
                }
            }
            this.G = jSONObject.optInt("targetType");
            JSONArray optJSONArray2 = jSONObject.optJSONArray("viewMonitorUrls");
            if (optJSONArray2 != null && optJSONArray2.length() > 0) {
                this.H = new String[optJSONArray2.length()];
                for (int i3 = 0; i3 < optJSONArray2.length(); i3++) {
                    this.H[i3] = optJSONArray2.optString(i3);
                }
            }
            JSONArray optJSONArray3 = jSONObject.optJSONArray("clickMonitorUrls");
            if (optJSONArray3 != null && optJSONArray3.length() > 0) {
                this.I = new String[optJSONArray3.length()];
                for (int i4 = 0; i4 < optJSONArray3.length(); i4++) {
                    this.I[i4] = optJSONArray3.optString(i4);
                }
            }
        }
    }

    /* JADX WARNING: type inference failed for: r11v0, types: [android.content.Context, com.miui.antivirus.activity.MainActivity, miui.app.Activity] */
    /* JADX WARNING: Unknown variable types count: 1 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void b(com.miui.antivirus.activity.MainActivity r11) {
        /*
            r10 = this;
            java.lang.String r0 = r10.r
            boolean r0 = com.miui.securityscan.i.i.b(r11, r0)
            if (r0 == 0) goto L_0x0009
            return
        L_0x0009:
            java.lang.String r0 = r10.s
            boolean r0 = android.text.TextUtils.isEmpty(r0)
            if (r0 == 0) goto L_0x0017
            java.lang.String r0 = r10.k
            com.miui.securityscan.i.i.b(r11, r0)
            return
        L_0x0017:
            com.miui.securityscan.cards.k r0 = com.miui.securityscan.cards.k.a((android.content.Context) r11)
            java.lang.String r1 = r10.s
            boolean r0 = r0.a((java.lang.String) r1)
            if (r0 == 0) goto L_0x0029
            java.lang.String r0 = r10.s
            com.miui.securityscan.i.i.a((android.content.Context) r11, (java.lang.String) r0)
            goto L_0x0076
        L_0x0029:
            boolean r0 = com.miui.securityscan.i.c.f(r11)
            if (r0 != 0) goto L_0x0036
            r0 = 2131758379(0x7f100d2b, float:1.914772E38)
            com.miui.securityscan.i.c.a((android.content.Context) r11, (int) r0)
            return
        L_0x0036:
            java.lang.String r2 = r10.s
            java.lang.String r3 = r10.u
            java.lang.String r4 = r10.t
            java.lang.String r5 = r10.v
            java.lang.String r6 = r10.w
            java.lang.String r7 = r10.x
            java.lang.String r8 = r10.y
            java.lang.String r9 = r10.z
            r1 = r11
            b.b.c.j.x.a(r1, r2, r3, r4, r5, r6, r7, r8, r9)
            com.miui.securityscan.cards.g r0 = com.miui.securityscan.cards.g.a((android.content.Context) r11)
            java.lang.String r1 = r10.s
            boolean r2 = r10.F
            r0.a((java.lang.String) r1, (boolean) r2)
            java.lang.String r1 = r10.s
            r2 = 10
            r0.a((java.lang.String) r1, (int) r2)
            android.content.res.Resources r0 = r11.getResources()
            r1 = 2131758030(0x7f100bce, float:1.9147012E38)
            r2 = 1
            java.lang.Object[] r2 = new java.lang.Object[r2]
            java.lang.String r3 = r10.h
            r4 = 0
            r2[r4] = r3
            java.lang.String r0 = r0.getString(r1, r2)
            android.widget.Toast r11 = android.widget.Toast.makeText(r11, r0, r4)
            r11.show()
        L_0x0076:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.antivirus.result.C0243f.b(com.miui.antivirus.activity.MainActivity):void");
    }

    /* JADX WARNING: type inference failed for: r3v0, types: [android.content.Context, com.miui.antivirus.activity.MainActivity] */
    /* JADX WARNING: Unknown variable types count: 1 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void c(com.miui.antivirus.activity.MainActivity r3) {
        /*
            r2 = this;
            java.lang.String r0 = "Advertisement"
            int r1 = r2.f
            java.lang.String r1 = java.lang.String.valueOf(r1)
            b.b.b.a.b.C0023b.f(r1)
            java.lang.String r1 = r2.r
            boolean r1 = com.miui.securityscan.i.i.b(r3, r1)
            if (r1 == 0) goto L_0x0014
            return
        L_0x0014:
            java.lang.String r1 = r2.s
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 == 0) goto L_0x0022
            java.lang.String r0 = r2.k
            com.miui.securityscan.i.i.b(r3, r0)
            return
        L_0x0022:
            java.lang.String r1 = r2.k     // Catch:{ Exception -> 0x0036 }
            boolean r1 = android.text.TextUtils.isEmpty(r1)     // Catch:{ Exception -> 0x0036 }
            if (r1 == 0) goto L_0x0030
            java.lang.String r3 = "landingPageUrl is empty"
            android.util.Log.d(r0, r3)     // Catch:{ Exception -> 0x0036 }
            return
        L_0x0030:
            java.lang.String r1 = r2.k     // Catch:{ Exception -> 0x0036 }
            com.miui.securityscan.i.i.c(r3, r1)     // Catch:{ Exception -> 0x0036 }
            goto L_0x003c
        L_0x0036:
            r3 = move-exception
            java.lang.String r1 = "msg"
            android.util.Log.e(r0, r1, r3)
        L_0x003c:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.antivirus.result.C0243f.c(com.miui.antivirus.activity.MainActivity):void");
    }

    /* access modifiers changed from: private */
    public void d(MainActivity mainActivity) {
        new Handler(Looper.getMainLooper()).post(new C0242e(this, mainActivity));
    }

    public void a(int i2) {
        this.O = i2;
    }

    public void a(int i2, View view, Context context, t tVar, ViewGroup viewGroup) {
        MainActivity mainActivity;
        ImageView imageView;
        String str;
        Drawable drawable;
        D d2;
        ImageView imageView2;
        super.a(i2, view, context, tVar);
        int dimensionPixelOffset = context.getResources().getDimensionPixelOffset(R.dimen.card_layout_line_height_half);
        view.setPadding(0, dimensionPixelOffset, 0, dimensionPixelOffset);
        int i3 = this.l;
        if (i3 == 3) {
            d2 = (D) view.getTag();
            d2.f2795a.setBackground(context.getResources().getDrawable(R.drawable.shape_result_card_border_whole));
            d2.f2793c.setText(this.i);
            d2.f2792b.setText(this.j);
            str = this.n[0];
            imageView2 = d2.f2794d;
            drawable = tVar.a();
        } else if (i3 != 4) {
            if (i3 == 5) {
                C c2 = (C) view.getTag();
                c2.f2795a.setBackground(context.getResources().getDrawable(R.drawable.shape_result_card_border_whole));
                c2.f2791d.setText(this.i);
                c2.f2790c.setText(this.h);
                r.a(this.m, c2.f2789b, r.g, (int) R.drawable.icon_def);
                a(context, c2.e, true);
                mainActivity = (MainActivity) context;
                imageView = c2.f2789b;
            } else if (i3 == 25) {
                y yVar = (y) view.getTag();
                yVar.f2795a.setBackground(context.getResources().getDrawable(R.drawable.shape_result_card_border_whole));
                yVar.f2870c.setText(this.i);
                yVar.f2869b.setText(this.h);
                r.a(this.n[0], yVar.e, tVar.a());
                r.a(this.m, yVar.f2871d, r.g, (int) R.drawable.icon_def);
                yVar.e.setVisibility(0);
                a(context, yVar.f, false);
                mainActivity = (MainActivity) context;
                imageView = yVar.f2871d;
            } else if (i3 == 31) {
                F f2 = (F) view.getTag();
                f2.f2795a.setBackground(context.getResources().getDrawable(R.drawable.shape_result_card_border_whole));
                f2.f2797c.setText(this.i);
                f2.f2796b.setText(this.h);
                a(context, f2.g, false);
                r.a(this.n[0], f2.f2798d, tVar.b());
                r.a(this.n[1], f2.e, tVar.b());
                r.a(this.n[2], f2.f, tVar.b());
                mainActivity = (MainActivity) context;
                imageView = f2.f2798d;
            } else if (i3 == 40) {
                A a2 = (A) view.getTag();
                a2.f2795a.setBackground(context.getResources().getDrawable(R.drawable.shape_result_card_border_whole));
                a2.f2782b.setText(this.j);
                a2.f2783c.setText(this.i);
                r.a(this.n[0], a2.e, tVar.b());
                r.a(this.n[1], a2.f, tVar.b());
                r.a(this.n[2], a2.g, tVar.b());
                if (this.o != -1) {
                    a2.f2784d.setVisibility(0);
                    a2.f2784d.setText(this.p);
                    s.a(a2.f2782b, this.j, this.p);
                } else {
                    a2.f2784d.setVisibility(8);
                }
                mainActivity = (MainActivity) context;
                imageView = a2.e;
            } else if (i3 == 10001 || i3 == 30001 || i3 == 30002) {
                b bVar = (b) view.getTag();
                C0251n.a(this.L);
                Log.d("Advertisement", "International Ads reportPV : " + this.L);
                if (!this.N || !bVar.j) {
                    view.setBackgroundResource(0);
                    return;
                }
                C0251n.a(bVar, this.O, this.J);
                bVar.h.setBackgroundResource(R.drawable.shape_result_card_border_whole);
                bVar.f1776a.setText(this.h);
                bVar.e.setText(this.M);
                if (TextUtils.isEmpty(this.i)) {
                    bVar.f1777b.setVisibility(8);
                } else {
                    bVar.f1777b.setText(this.i);
                    bVar.f1777b.setVisibility(0);
                }
                ImageView imageView3 = bVar.f1779d;
                if (imageView3 != null) {
                    r.a(this.n[0], imageView3, r.g, (int) R.drawable.icon_def);
                }
                ImageView imageView4 = bVar.f1778c;
                if (imageView4 != null) {
                    r.a(this.n[1], imageView4, tVar.a());
                }
                C0251n.a(context, bVar.f, this.O, this.J, bVar.i);
                bVar.g.bringToFront();
                bVar.g.setOnClickListener(this);
                if (this.P) {
                    view.measure(View.MeasureSpec.makeMeasureSpec(viewGroup.getWidth(), 1073741824), View.MeasureSpec.makeMeasureSpec(viewGroup.getHeight(), 0));
                    ObjectAnimator ofInt = ObjectAnimator.ofInt(view, new C0239b(this, "height"), new int[]{0, view.getMeasuredHeight()});
                    ofInt.setDuration(400);
                    ofInt.setInterpolator(new AccelerateDecelerateInterpolator());
                    ofInt.start();
                    ObjectAnimator ofFloat = ObjectAnimator.ofFloat(view, AnimatedProperty.PROPERTY_NAME_ALPHA, new float[]{0.0f, 1.0f});
                    ofFloat.setDuration(400);
                    ofFloat.setInterpolator(new AccelerateDecelerateInterpolator());
                    ofFloat.start();
                    this.P = false;
                    return;
                }
                return;
            } else {
                return;
            }
            mainActivity.a((AdImageView) imageView, i2, this);
            b.C0023b.g(String.valueOf(this.f));
        } else {
            d2 = (D) view.getTag();
            d2.f2795a.setBackground(context.getResources().getDrawable(R.drawable.shape_result_card_border_whole));
            d2.f2793c.setText(this.i);
            d2.f2792b.setText(this.j);
            str = this.n[0];
            imageView2 = d2.f2794d;
            drawable = tVar.b();
        }
        r.a(str, imageView2, drawable);
        mainActivity = (MainActivity) context;
        imageView = d2.f2794d;
        mainActivity.a((AdImageView) imageView, i2, this);
        b.C0023b.g(String.valueOf(this.f));
    }

    public void a(Object obj) {
        this.J = obj;
    }

    public void a(String str) {
        this.L = str;
    }

    public void a(String[] strArr) {
        this.n = strArr;
    }

    public void b(int i2) {
        this.f = i2;
    }

    public void b(String str) {
        this.M = str;
    }

    public String c() {
        return this.L;
    }

    public void c(C0243f fVar) {
        if (fVar != null) {
            Log.d("Advertisement", "fill ad");
            b(fVar.h());
            b(fVar.e());
            a(fVar.k());
            e(fVar.o());
            c(fVar.m());
            a(fVar.j());
            a(fVar.g());
            d(true);
            a.a().a(fVar.k(), this);
        }
    }

    public void c(String str) {
        this.i = str;
    }

    public void c(boolean z2) {
        this.P = z2;
    }

    public void d(String str) {
        this.A = str;
    }

    public void d(boolean z2) {
        this.N = z2;
    }

    public String[] d() {
        return this.I;
    }

    public String e() {
        return this.M;
    }

    public void e(String str) {
        this.h = str;
    }

    public String f() {
        return this.t;
    }

    public int g() {
        return this.O;
    }

    public int getLayoutId() {
        int i2 = this.l;
        return i2 != 3 ? i2 != 4 ? i2 != 5 ? i2 != 25 ? i2 != 31 ? i2 != 40 ? (i2 == 10001 || i2 == 30001 || i2 == 30002) ? C0251n.a(this.O) : R.layout.v_result_item_template_empty : R.layout.result_ad_template_40 : R.layout.result_ad_template_31 : R.layout.result_ad_template_25 : R.layout.v_result_item_template_5 : R.layout.result_ad_template_4 : R.layout.result_ad_template_3;
    }

    public int h() {
        return this.f;
    }

    public boolean i() {
        return this.g;
    }

    public String[] j() {
        return this.n;
    }

    public Object k() {
        return this.J;
    }

    public String l() {
        return this.s;
    }

    public String m() {
        return this.i;
    }

    public int n() {
        return this.l;
    }

    public String o() {
        return this.h;
    }

    public void onClick(View view) {
        MainActivity mainActivity = (MainActivity) view.getContext();
        int i2 = this.l;
        if (i2 != 10001 && i2 != 30001 && i2 != 30002) {
            int id = view.getId();
            if (id == R.id.button) {
                b(mainActivity);
            } else if (id != R.id.close) {
                c(mainActivity);
            } else if (p.a() < 5 || i()) {
                a(mainActivity, view);
            } else {
                a(mainActivity);
            }
            if (view.getId() != R.id.close) {
                K.a("CLICK", this);
            }
        } else if (C0251n.a(view)) {
            a(view);
        } else {
            C0251n.a(c(), this.J);
        }
    }

    public String[] p() {
        return this.H;
    }

    public boolean q() {
        return this.N && this.O > 0;
    }
}
