package com.miui.gamebooster.gamead;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.GradientDrawable;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import b.b.c.j.r;
import com.miui.activityutil.o;
import com.miui.applicationlock.c.y;
import com.miui.common.customview.AdImageView;
import com.miui.gamebooster.customview.C0340i;
import com.miui.gamebooster.ui.GameBoosterRealMainActivity;
import com.miui.securitycenter.R;
import com.miui.securityscan.cards.g;
import com.miui.securityscan.cards.k;
import com.xiaomi.ad.feedback.IAdFeedbackListener;

public class d extends e {

    /* renamed from: a  reason: collision with root package name */
    private int f4298a;

    /* renamed from: b  reason: collision with root package name */
    private boolean f4299b;

    /* renamed from: c  reason: collision with root package name */
    private String f4300c;

    /* renamed from: d  reason: collision with root package name */
    private String f4301d;
    private String e;
    private int f;
    private String g;
    protected String[] h = new String[3];
    private int i;
    private String j;
    private String k;
    private String l;
    private String m;
    private String n;
    private String o;
    private String p;
    private int q = -1;
    private String r;
    private int s = -1;
    private int t = 1;
    private String u;
    private String[] v;
    private String[] w;

    private void a(Context context, Button button, int i2) {
        if (i2 != -1) {
            GradientDrawable gradientDrawable = new GradientDrawable();
            gradientDrawable.setCornerRadius(context.getResources().getDimension(R.dimen.am_action_btn_corner_radius));
            if (TextUtils.equals(o.f2309a, this.u)) {
                gradientDrawable.setStroke(1, i2);
                button.setTextColor(i2);
            } else {
                gradientDrawable.setColor(i2);
                button.setTextColor(-1);
            }
            button.setBackground(gradientDrawable);
        }
    }

    private void a(Context context, Button button, View view, boolean z) {
        boolean z2;
        int i2;
        Resources resources;
        int i3;
        int i4;
        boolean a2 = k.a(context).a(this.m);
        button.setBackgroundResource(R.drawable.gb_selector_btn_install_bg);
        boolean z3 = true;
        boolean z4 = false;
        if (a2) {
            if (TextUtils.isEmpty(this.r)) {
                button.setText(R.string.open_app);
            } else {
                button.setText(this.r);
            }
            z2 = true;
            z3 = false;
            z4 = true;
        } else {
            int b2 = g.a(context).b(this.m);
            if (b2 != -1) {
                if (b2 != 5) {
                    if (b2 == 10) {
                        i4 = R.string.connecting;
                    } else if (b2 != 1) {
                        if (b2 != 2) {
                            if (b2 != 3) {
                                a(context, button, this.q);
                                button.setText(this.p);
                                z2 = false;
                                z4 = true;
                            } else {
                                i4 = R.string.installing;
                            }
                        }
                    }
                    button.setText(i4);
                    z3 = false;
                    z2 = false;
                }
                int a3 = g.a(context).a(this.m);
                if (a3 != -1) {
                    button.setText(a3 + "%");
                    z3 = false;
                    z2 = false;
                }
            }
            button.setText(R.string.downloading);
            z3 = false;
            z2 = false;
        }
        int i5 = this.f;
        if (i5 == 18 || i5 == 21) {
            if (z4) {
                if (a2) {
                    i2 = this.s;
                    if (i2 == -1) {
                        resources = context.getResources();
                        i3 = R.color.btn_color_red;
                        button.setTextColor(resources.getColor(i3));
                    }
                } else if (!z3) {
                    i2 = this.q;
                    if (i2 == -1) {
                        resources = context.getResources();
                        i3 = R.color.btn_color_cyan;
                        button.setTextColor(resources.getColor(i3));
                    }
                }
                button.setTextColor(i2);
            } else {
                button.setTextColor(R.color.ad_button_connect);
            }
            if (view != null) {
                view.setEnabled(z4);
            }
        } else if (z) {
            button.setBackgroundResource(z2 ? R.drawable.common_button_cyan : R.drawable.common_button_blue2);
            button.setTextColor(z4 ? context.getResources().getColor(R.color.result_blue_button_text) : context.getResources().getColor(R.color.ad_button_connect));
        }
        button.setEnabled(z4);
    }

    private void a(GameBoosterRealMainActivity gameBoosterRealMainActivity) {
        y b2 = y.b();
        C0357b bVar = new C0357b(this, gameBoosterRealMainActivity);
        if (b2.a(gameBoosterRealMainActivity.getApplicationContext())) {
            b2.a(gameBoosterRealMainActivity.getApplicationContext(), (IAdFeedbackListener) bVar, "com.miui.securitycenter", "com.miui.securitycenter_gamebooster", b());
        } else {
            Log.e("GameAdAdapter", "connect fail,maybe not support dislike window");
        }
    }

    /* JADX WARNING: type inference failed for: r6v0, types: [android.content.Context, com.miui.gamebooster.ui.GameBoosterRealMainActivity, miui.app.Activity] */
    /* JADX WARNING: Unknown variable types count: 1 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void b(com.miui.gamebooster.ui.GameBoosterRealMainActivity r6) {
        /*
            r5 = this;
            java.lang.String r0 = "click"
            java.lang.String r1 = "button_click"
            com.miui.gamebooster.m.C0373d.a((java.lang.String) r0, (java.lang.String) r1)
            java.lang.String r0 = r5.l
            boolean r0 = com.miui.securityscan.i.i.b(r6, r0)
            if (r0 == 0) goto L_0x0010
            return
        L_0x0010:
            java.lang.String r0 = r5.m
            boolean r0 = android.text.TextUtils.isEmpty(r0)
            if (r0 == 0) goto L_0x001e
            java.lang.String r0 = r5.e
            com.miui.securityscan.i.i.b(r6, r0)
            return
        L_0x001e:
            com.miui.securityscan.cards.k r0 = com.miui.securityscan.cards.k.a((android.content.Context) r6)
            java.lang.String r1 = r5.m
            boolean r0 = r0.a((java.lang.String) r1)
            r1 = 1
            r2 = 0
            if (r0 == 0) goto L_0x0052
            com.miui.securitycenter.Application r0 = com.miui.securitycenter.Application.d()
            com.miui.gamebooster.c.a r0 = com.miui.gamebooster.c.a.a((android.content.Context) r0)
            boolean r0 = r0.k(r1)
            if (r0 != 0) goto L_0x0044
            java.lang.String r0 = r5.m
            android.os.UserHandle r1 = b.b.c.j.B.e(r2)
            com.miui.gamebooster.m.C0393y.a((android.content.Context) r6, (java.lang.String) r0, (android.os.UserHandle) r1)
            goto L_0x008e
        L_0x0044:
            com.miui.gamebooster.service.IGameBooster r0 = r6.n()
            java.lang.String r1 = r5.m
            android.os.UserHandle r2 = b.b.c.j.B.e(r2)
            com.miui.gamebooster.m.C0393y.a((android.content.Context) r6, (com.miui.gamebooster.service.IGameBooster) r0, (java.lang.String) r1, (android.os.UserHandle) r2)
            goto L_0x008e
        L_0x0052:
            boolean r0 = com.miui.securityscan.i.c.f(r6)
            if (r0 != 0) goto L_0x005f
            r0 = 2131758379(0x7f100d2b, float:1.914772E38)
            com.miui.securityscan.i.c.a((android.content.Context) r6, (int) r0)
            return
        L_0x005f:
            java.lang.String r0 = r5.k     // Catch:{ Exception -> 0x0076 }
            boolean r0 = android.text.TextUtils.isEmpty(r0)     // Catch:{ Exception -> 0x0076 }
            if (r0 != 0) goto L_0x0076
            java.lang.String r0 = r5.k     // Catch:{ Exception -> 0x0076 }
            java.lang.String r3 = "migamecenter:"
            boolean r0 = r0.startsWith(r3)     // Catch:{ Exception -> 0x0076 }
            if (r0 == 0) goto L_0x0076
            java.lang.String r0 = r5.k     // Catch:{ Exception -> 0x0076 }
            com.miui.securityscan.i.i.c(r6, r0)     // Catch:{ Exception -> 0x0076 }
        L_0x0076:
            android.content.res.Resources r0 = r6.getResources()
            r3 = 2131758030(0x7f100bce, float:1.9147012E38)
            java.lang.Object[] r1 = new java.lang.Object[r1]
            java.lang.String r4 = r5.f4301d
            r1[r2] = r4
            java.lang.String r0 = r0.getString(r3, r1)
            android.widget.Toast r6 = android.widget.Toast.makeText(r6, r0, r2)
            r6.show()
        L_0x008e:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.gamebooster.gamead.d.b(com.miui.gamebooster.ui.GameBoosterRealMainActivity):void");
    }

    /* JADX WARNING: type inference failed for: r7v0, types: [android.content.Context, com.miui.gamebooster.ui.GameBoosterRealMainActivity, miui.app.Activity] */
    /* JADX WARNING: Unknown variable types count: 1 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void c(com.miui.gamebooster.ui.GameBoosterRealMainActivity r7) {
        /*
            r6 = this;
            java.lang.String r0 = "click"
            java.lang.String r1 = "icon_click"
            com.miui.gamebooster.m.C0373d.a((java.lang.String) r0, (java.lang.String) r1)
            java.lang.String r0 = "AdvertisementGroup"
            java.lang.String r1 = "onAdvContentClick start"
            android.util.Log.d(r0, r1)
            java.lang.String r1 = r6.l
            boolean r1 = com.miui.securityscan.i.i.b(r7, r1)
            if (r1 == 0) goto L_0x0017
            return
        L_0x0017:
            java.lang.String r1 = r6.m
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 == 0) goto L_0x002a
            java.lang.String r1 = "onAdvContentClick open landingPageUrl"
            android.util.Log.d(r0, r1)
            java.lang.String r0 = r6.e
            com.miui.securityscan.i.i.b(r7, r0)
            return
        L_0x002a:
            java.lang.String r1 = "onAdvContentClick action view"
            android.util.Log.d(r0, r1)     // Catch:{ Exception -> 0x0059 }
            android.content.Intent r1 = new android.content.Intent     // Catch:{ Exception -> 0x0059 }
            java.lang.String r2 = "android.intent.action.VIEW"
            r1.<init>(r2)     // Catch:{ Exception -> 0x0059 }
            java.lang.String r2 = "mimarket://details?id=%s&back=true&ref=%s&ext_passback=%s"
            r3 = 3
            java.lang.Object[] r3 = new java.lang.Object[r3]     // Catch:{ Exception -> 0x0059 }
            r4 = 0
            java.lang.String r5 = r6.m     // Catch:{ Exception -> 0x0059 }
            r3[r4] = r5     // Catch:{ Exception -> 0x0059 }
            r4 = 1
            java.lang.String r5 = r6.o     // Catch:{ Exception -> 0x0059 }
            r3[r4] = r5     // Catch:{ Exception -> 0x0059 }
            r4 = 2
            java.lang.String r5 = r6.n     // Catch:{ Exception -> 0x0059 }
            r3[r4] = r5     // Catch:{ Exception -> 0x0059 }
            java.lang.String r2 = java.lang.String.format(r2, r3)     // Catch:{ Exception -> 0x0059 }
            android.net.Uri r2 = android.net.Uri.parse(r2)     // Catch:{ Exception -> 0x0059 }
            r1.setData(r2)     // Catch:{ Exception -> 0x0059 }
            r7.startActivity(r1)     // Catch:{ Exception -> 0x0059 }
            goto L_0x005f
        L_0x0059:
            r7 = move-exception
            java.lang.String r1 = "msg"
            android.util.Log.e(r0, r1, r7)
        L_0x005f:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.gamebooster.gamead.d.c(com.miui.gamebooster.ui.GameBoosterRealMainActivity):void");
    }

    /* access modifiers changed from: private */
    public void d(GameBoosterRealMainActivity gameBoosterRealMainActivity) {
        new Handler(Looper.getMainLooper()).post(new c(this, gameBoosterRealMainActivity));
    }

    public String[] a() {
        return this.w;
    }

    public String b() {
        return this.n;
    }

    public void bindView(int i2, View view, Context context, g gVar) {
        GameBoosterRealMainActivity gameBoosterRealMainActivity;
        ImageView imageView;
        super.bindView(i2, view, context, gVar);
        int i3 = this.f;
        if (i3 == 18) {
            C0340i iVar = (C0340i) view;
            iVar.setOnClickListener(this);
            iVar.getmButtonView().setOnClickListener(this);
            iVar.getmTextView().setText(this.f4300c);
            iVar.getmButtonLayout().setOnClickListener(this);
            iVar.getmImageView().setOnClickListener(this);
            r.a(this.g, iVar.getmImageView(), r.g, (int) R.drawable.icon_def);
            a(context, iVar.getmButtonView(), iVar.getmButtonLayout(), false);
            gameBoosterRealMainActivity = (GameBoosterRealMainActivity) context;
            imageView = iVar.getmImageView();
        } else if (i3 == 21) {
            h hVar = (h) view.getTag();
            r.a(this.h[0], hVar.f4306a, gVar.b());
            r.a(this.g, hVar.f4307b, r.g, (int) R.drawable.icon_def);
            hVar.f4308c.setText(this.f4301d);
            hVar.f.setOnClickListener(this);
            hVar.f4306a.setOnClickListener(this);
            view.setOnClickListener(this);
            hVar.e.setText(this.j);
            TextView textView = hVar.f4309d;
            textView.setText(String.format("%.1f", new Object[]{Float.valueOf((Float.valueOf((float) this.i).floatValue() / 1024.0f) / 1024.0f)}) + context.getResources().getString(R.string.gb_ad_game_size));
            a(context, hVar.f, (View) null, false);
            gameBoosterRealMainActivity = (GameBoosterRealMainActivity) context;
            imageView = hVar.f4307b;
        } else {
            return;
        }
        gameBoosterRealMainActivity.a((AdImageView) imageView, this.f4298a, this);
    }

    public int c() {
        return this.f4298a;
    }

    public boolean d() {
        return this.f4299b;
    }

    public String[] e() {
        return this.v;
    }

    public int getLayoutId() {
        int i2 = this.f;
        return i2 != 18 ? i2 != 21 ? R.layout.v_result_item_template_empty : R.layout.game_ad_big_image : R.layout.game_ad_tab_item;
    }

    public void onClick(View view) {
        GameBoosterRealMainActivity gameBoosterRealMainActivity = (GameBoosterRealMainActivity) view.getContext();
        int id = view.getId();
        if (id == R.id.ad_button || id == R.id.button_layout) {
            b(gameBoosterRealMainActivity);
        } else if (id != R.id.close) {
            c(gameBoosterRealMainActivity);
        } else {
            a(gameBoosterRealMainActivity);
        }
        if (view.getId() != R.id.close) {
            gameBoosterRealMainActivity.a("CLICK", this);
        }
    }
}
