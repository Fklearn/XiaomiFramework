package com.miui.gamebooster.p;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.ContentObserver;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import b.b.c.j.i;
import com.miui.common.persistence.b;
import com.miui.gamebooster.a.y;
import com.miui.gamebooster.customview.C0354x;
import com.miui.gamebooster.customview.GameBoxView;
import com.miui.gamebooster.d.d;
import com.miui.gamebooster.g.a;
import com.miui.gamebooster.m.C0373d;
import com.miui.gamebooster.m.C0388t;
import com.miui.gamebooster.m.ja;
import com.miui.gamebooster.m.na;
import com.miui.gamebooster.model.g;
import com.miui.gamebooster.service.GameBoxWindowManagerService;
import com.miui.gamebooster.videobox.adapter.k;
import com.miui.gamebooster.videobox.settings.f;
import com.miui.gamebooster.videobox.utils.e;
import com.miui.gamebooster.view.c;
import com.miui.gamebooster.view.k;
import com.miui.gamebooster.widget.ProgressCircle;
import com.miui.maml.folme.AnimatedProperty;
import com.miui.securitycenter.R;
import java.util.ArrayList;
import java.util.List;

public class r {

    /* renamed from: a  reason: collision with root package name */
    private static ArrayList<String> f4737a = new ArrayList<>();

    /* renamed from: b  reason: collision with root package name */
    private static ArrayList<String> f4738b = new ArrayList<>();
    private C0354x A;
    /* access modifiers changed from: private */
    public CountDownTimer B;
    private List<g> C = new ArrayList();
    private List<g> D;
    /* access modifiers changed from: private */

    /* renamed from: c  reason: collision with root package name */
    public GameBoxWindowManagerService f4739c;
    /* access modifiers changed from: private */

    /* renamed from: d  reason: collision with root package name */
    public Context f4740d;
    private View e;
    private View f;
    private WindowManager.LayoutParams g;
    private WindowManager h;
    private View i;
    private GridView j;
    private GameBoxView k;
    private RelativeLayout l;
    private LinearLayout m;
    private RelativeLayout n;
    private RelativeLayout o;
    private ImageButton p;
    private ImageButton q;
    private y r;
    private Handler s;
    private volatile boolean t;
    private boolean u;
    private boolean v;
    private ContentObserver w;
    private View x;
    private k y;
    private long z;

    static {
        f4737a.add("gemini");
        f4737a.add("cepheus");
        f4738b.add("scorpio");
        f4738b.add("lithium");
    }

    public r(GameBoxWindowManagerService gameBoxWindowManagerService, Handler handler) {
        if (C0388t.m()) {
            this.C.add(new g(d.QUICKWEIXIN, R.drawable.gamebox_wechat_button));
            this.C.add(new g(d.QUICKQQ, R.drawable.gamebox_qq_button));
        }
        this.C.add(new g(d.RECORD, R.drawable.gamebox_screenrecord_button_old));
        this.C.add(new g(d.QUICKSCREENSHOT, R.drawable.gamebox_screenshot_button_old));
        this.C.add(new g(d.ONEKEYCLEAN, R.drawable.gamebox_accelerate_button_old));
        this.C.add(new g(d.ANTIMSG, R.drawable.gamebox_dnd_button_old));
        this.D = new ArrayList();
        this.D.add(new g(d.RECORD, R.drawable.gamebox_screenrecord_button_old));
        this.D.add(new g(d.QUICKSCREENSHOT, R.drawable.gamebox_screenshot_button_old));
        this.D.add(new g(d.ANTIMSG, R.drawable.gamebox_dnd_button_old));
        this.f4739c = gameBoxWindowManagerService;
        this.f4740d = gameBoxWindowManagerService.getApplicationContext();
        this.s = handler;
        m();
    }

    /* JADX WARNING: Removed duplicated region for block: B:12:0x0098  */
    /* JADX WARNING: Removed duplicated region for block: B:13:0x009b  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void a(android.view.LayoutInflater r11, int r12, int r13) {
        /*
            r10 = this;
            android.content.Context r11 = r10.f4740d
            int r11 = com.miui.gamebooster.m.na.a((android.content.Context) r11)
            android.view.WindowManager$LayoutParams r0 = r10.g
            r1 = 2131821200(0x7f110290, float:1.9275136E38)
            r0.windowAnimations = r1
            com.miui.gamebooster.service.GameBoxWindowManagerService r0 = r10.f4739c
            java.lang.String r0 = r0.d()
            android.view.View r1 = new android.view.View
            android.content.Context r2 = r10.f4740d
            r1.<init>(r2)
            r10.e = r1
            android.view.View r1 = new android.view.View
            android.content.Context r2 = r10.f4740d
            r1.<init>(r2)
            r10.f = r1
            com.miui.gamebooster.g.a r1 = new com.miui.gamebooster.g.a
            android.content.Context r2 = r10.f4740d
            r3 = 1
            r1.<init>(r2, r11, r3)
            com.miui.gamebooster.g.a r2 = new com.miui.gamebooster.g.a
            android.content.Context r4 = r10.f4740d
            r5 = 0
            r2.<init>(r4, r11, r5)
            java.lang.String r4 = "intent_booster_type_video_all"
            boolean r4 = r4.equals(r0)
            r6 = 8
            if (r4 == 0) goto L_0x00a1
            android.view.View r4 = r10.e
            com.miui.gamebooster.customview.a.e r7 = new com.miui.gamebooster.customview.a.e
            r7.<init>(r10, r3)
            r4.setOnTouchListener(r7)
            android.view.View r4 = r10.f
            com.miui.gamebooster.customview.a.e r7 = new com.miui.gamebooster.customview.a.e
            r7.<init>(r10, r5)
            r4.setOnTouchListener(r7)
            android.view.View r4 = r10.f
            r4.setBackground(r2)
            android.view.View r4 = r10.e
            r4.setBackground(r1)
            boolean r4 = com.miui.gamebooster.videobox.settings.f.f()
            int r7 = com.miui.gamebooster.videobox.settings.f.e()
            java.lang.StringBuilder r8 = new java.lang.StringBuilder
            r8.<init>()
            java.lang.String r9 = "createTransparentLine: "
            r8.append(r9)
            r8.append(r7)
            java.lang.String r8 = r8.toString()
            java.lang.String r9 = "ToolBoxWindowManager"
            android.util.Log.i(r9, r8)
            if (r7 != 0) goto L_0x007f
            r7 = r3
            goto L_0x0080
        L_0x007f:
            r7 = r5
        L_0x0080:
            if (r4 == 0) goto L_0x0090
            if (r7 == 0) goto L_0x0085
            goto L_0x0093
        L_0x0085:
            r1.a(r5)
            android.view.WindowManager$LayoutParams r2 = r10.g
            r4 = 2131821202(0x7f110292, float:1.927514E38)
            r2.windowAnimations = r4
            goto L_0x0096
        L_0x0090:
            r1.a(r5)
        L_0x0093:
            r2.a(r5)
        L_0x0096:
            if (r7 == 0) goto L_0x009b
            android.view.View r2 = r10.f
            goto L_0x009d
        L_0x009b:
            android.view.View r2 = r10.e
        L_0x009d:
            r2.setVisibility(r6)
            goto L_0x00c2
        L_0x00a1:
            r2.a(r5)
            android.view.View r4 = r10.e
            com.miui.gamebooster.customview.a.d r7 = new com.miui.gamebooster.customview.a.d
            r7.<init>(r10, r3)
            r4.setOnTouchListener(r7)
            android.view.View r4 = r10.f
            com.miui.gamebooster.customview.a.d r7 = new com.miui.gamebooster.customview.a.d
            r7.<init>(r10, r5)
            r4.setOnTouchListener(r7)
            android.view.View r4 = r10.e
            r4.setBackground(r1)
            android.view.View r4 = r10.f
            r4.setBackground(r2)
        L_0x00c2:
            com.miui.gamebooster.service.GameBoxWindowManagerService r2 = r10.f4739c
            boolean r2 = r2.f
            if (r2 == 0) goto L_0x00e5
            java.lang.String r2 = "intent_booster_type_game"
            boolean r0 = r2.equals(r0)
            if (r0 == 0) goto L_0x00e5
            android.view.View r0 = r10.e
            r0.setVisibility(r6)
            android.os.Handler r0 = r10.s
            com.miui.gamebooster.p.h r2 = new com.miui.gamebooster.p.h
            r2.<init>(r10)
            r7 = 3040(0xbe0, double:1.502E-320)
            r0.postDelayed(r2, r7)
            com.miui.gamebooster.service.GameBoxWindowManagerService r0 = r10.f4739c
            r0.f = r5
        L_0x00e5:
            boolean r0 = b.b.c.j.i.e()
            if (r0 == 0) goto L_0x00f0
            android.view.WindowManager$LayoutParams r0 = r10.g
            com.miui.gamebooster.m.E.a((android.view.WindowManager.LayoutParams) r0)
        L_0x00f0:
            android.view.WindowManager$LayoutParams r0 = r10.g
            int r2 = r1.getIntrinsicWidth()
            int r2 = r2 + r6
            r0.width = r2
            android.view.WindowManager$LayoutParams r0 = r10.g
            int r1 = r1.getIntrinsicHeight()
            r0.height = r1
            android.view.WindowManager$LayoutParams r0 = r10.g
            r0.x = r5
            r0.y = r5
            if (r11 == 0) goto L_0x010f
            r0 = 180(0xb4, float:2.52E-43)
            if (r11 != r0) goto L_0x010e
            goto L_0x010f
        L_0x010e:
            r3 = r5
        L_0x010f:
            if (r3 == 0) goto L_0x0126
            android.content.Context r0 = r10.f4740d
            int r0 = com.miui.gamebooster.m.na.d(r0)
            android.view.WindowManager$LayoutParams r1 = r10.g
            double r2 = (double) r0
            r4 = 4599075939470750515(0x3fd3333333333333, double:0.3)
            double r2 = r2 * r4
            int r0 = (int) r2
            int r2 = r1.height
            int r0 = r0 - r2
            r1.y = r0
        L_0x0126:
            android.view.WindowManager r0 = r10.h
            android.view.View r1 = r10.e
            android.view.WindowManager$LayoutParams r2 = r10.g
            r0.addView(r1, r2)
            android.view.WindowManager$LayoutParams r0 = r10.g
            android.content.Context r1 = r10.f4740d
            int r1 = com.miui.gamebooster.m.na.e(r1)
            r0.x = r1
            r0 = 90
            if (r11 == r0) goto L_0x0142
            r0 = 270(0x10e, float:3.78E-43)
            if (r11 == r0) goto L_0x0142
            goto L_0x014a
        L_0x0142:
            android.view.WindowManager$LayoutParams r11 = r10.g
            int r0 = r11.x
            int r0 = r0 + r12
            int r0 = r0 + r13
            r11.x = r0
        L_0x014a:
            android.view.WindowManager r11 = r10.h
            android.view.View r12 = r10.f
            android.view.WindowManager$LayoutParams r13 = r10.g
            r11.addView(r12, r13)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.gamebooster.p.r.a(android.view.LayoutInflater, int, int):void");
    }

    private void a(View view, boolean z2) {
        Drawable background;
        if (view != null && (background = view.getBackground()) != null && (background instanceof a)) {
            ((a) background).a(z2);
            background.invalidateSelf();
            view.requestLayout();
        }
    }

    private void e(boolean z2) {
        Log.i("ToolBoxWindowManager", "createVideoBox: >>>>>>>>");
        if (!this.u) {
            this.u = true;
            WindowManager.LayoutParams layoutParams = this.g;
            layoutParams.type = 2003;
            layoutParams.format = -3;
            layoutParams.flags = 264;
            layoutParams.width = -1;
            layoutParams.height = -1;
            layoutParams.windowAnimations = z2 ? R.style.vtb_anim_view_left_exit : R.style.vtb_anim_view_right_exit;
            this.y = new k(this);
            this.x = this.y.a(this.f4740d, z2);
            this.x.setOnClickListener(new i(this));
            WindowManager.LayoutParams layoutParams2 = this.g;
            layoutParams2.gravity = 17;
            this.h.addView(this.x, layoutParams2);
            C0373d.a.c(f.a());
        }
    }

    /* access modifiers changed from: private */
    public String l() {
        String a2 = b.a("key_currentbooster_pkg_uid", (String) null);
        return a2.contains("com.tencent.tmgp.sgame") ? "kpl" : a2.contains("com.tencent.tmgp.pubgmhd") ? "pubg" : "";
    }

    private void m() {
        this.g = new WindowManager.LayoutParams();
        this.g.setTitle("FloatAssistantView");
        this.h = (WindowManager) this.f4740d.getSystemService("window");
    }

    /* access modifiers changed from: private */
    public void n() {
        if (e().e()) {
            boolean a2 = ja.a("key_gb_record_ai", e().c());
            boolean a3 = ja.a("key_gb_record_manual", e().c());
            String l2 = l();
            if (a2 && a3) {
                C0373d.c(l2);
                C0373d.f(l2);
                e().f();
            } else if (a2 || a3) {
                if (a2) {
                    C0373d.c(l2);
                    e().f();
                } else {
                    C0373d.b(l2);
                    e().a();
                }
                if (a3) {
                    C0373d.f(l2);
                } else {
                    C0373d.e(l2);
                    e().b();
                    return;
                }
            } else {
                C0373d.b(l2);
                C0373d.e(l2);
                e().b();
                e().a();
                return;
            }
            e().g();
            return;
        }
        e().i();
    }

    /* access modifiers changed from: private */
    public void o() {
        View view = this.e;
        if (view != null) {
            view.setVisibility(0);
            this.e.setAlpha(0.0f);
            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.e, "translationX", new float[]{-13.0f, 13.0f}), ObjectAnimator.ofFloat(this.e, AnimatedProperty.PROPERTY_NAME_ALPHA, new float[]{0.4f, 0.9f})});
            long j2 = (long) 880;
            animatorSet.setDuration(j2);
            AnimatorSet animatorSet2 = new AnimatorSet();
            animatorSet2.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.e, "translationX", new float[]{13.0f, 0.0f}), ObjectAnimator.ofFloat(this.e, AnimatedProperty.PROPERTY_NAME_ALPHA, new float[]{0.9f, 0.4f})});
            long j3 = (long) 560;
            animatorSet2.setDuration(j3);
            AnimatorSet animatorSet3 = new AnimatorSet();
            animatorSet3.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.e, "translationX", new float[]{0.0f, 13.0f}), ObjectAnimator.ofFloat(this.e, AnimatedProperty.PROPERTY_NAME_ALPHA, new float[]{0.4f, 0.9f})});
            String str = AnimatedProperty.PROPERTY_NAME_ALPHA;
            animatorSet3.setStartDelay((long) 1000);
            animatorSet3.setDuration(j2);
            AnimatorSet animatorSet4 = new AnimatorSet();
            animatorSet4.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.e, "translationX", new float[]{13.0f, 0.0f}), ObjectAnimator.ofFloat(this.e, str, new float[]{0.9f, 0.8f})});
            animatorSet4.setDuration(j3);
            AnimatorSet animatorSet5 = new AnimatorSet();
            animatorSet5.playSequentially(new Animator[]{animatorSet, animatorSet2, animatorSet3, animatorSet4});
            animatorSet5.start();
        }
    }

    public void a() {
        Context context = this.f4740d;
        if (context != null) {
            View inflate = View.inflate(context, R.layout.gb_gamebox_manual_record_float, (ViewGroup) null);
            k.a a2 = com.miui.gamebooster.view.k.a(this.f4740d.getApplicationContext());
            a2.a(inflate, this.f4740d.getResources().getDimensionPixelOffset(R.dimen.gb_wonderful_record_floatball_size), this.f4740d.getResources().getDimensionPixelOffset(R.dimen.gb_wonderful_record_floatball_size));
            a2.a(na.e(this.f4740d) - i.a(this.f4740d, 50.0f), (na.c(this.f4740d) / 2) - i.a(this.f4740d, 49.0f));
            a2.a();
            inflate.setOnClickListener(new q(this, (ProgressCircle) inflate.findViewById(R.id.progress_circle)));
        }
    }

    public void a(int i2, boolean z2, boolean z3) {
        GameBoxView gameBoxView = this.k;
        if (gameBoxView != null) {
            gameBoxView.a(i2, z2, !z3);
        }
    }

    public void a(long j2) {
        this.z = j2;
    }

    public void a(Context context) {
        if (this.A == null) {
            this.A = new C0354x(context, e().c());
        }
        C0354x xVar = this.A;
        xVar.a((C0354x.a) new g(this));
        xVar.show();
    }

    public void a(View view) {
        View view2;
        WindowManager windowManager = this.h;
        if (windowManager != null && (view2 = this.x) != null) {
            try {
                windowManager.removeView(view2);
                this.u = false;
                if (this.x != view) {
                    this.h.removeView(view);
                }
                this.x = null;
            } catch (Exception e2) {
                Log.e("ToolBoxWindowManager", "removeVideoToolBoxView: " + e2.toString());
            }
        }
    }

    public void a(boolean z2) {
        GridView gridView;
        int i2;
        Resources resources;
        int i3;
        Resources resources2;
        if (!this.u) {
            WindowManager.LayoutParams layoutParams = this.g;
            layoutParams.type = 2003;
            layoutParams.format = -3;
            layoutParams.flags = 264;
            layoutParams.gravity = 51;
            layoutParams.width = -1;
            layoutParams.height = -1;
            layoutParams.windowAnimations = R.style.gamebox_anim_view;
            layoutParams.windowAnimations = z2 ? R.style.gamebox_anim_view_left : R.style.gamebox_anim_view_right;
            this.i = LayoutInflater.from(this.f4740d).inflate(na.c() ? R.layout.gamebox_gridview_rtl : R.layout.gamebox_gridview, (ViewGroup) null);
            this.j = (GridView) this.i.findViewById(R.id.grid_view);
            this.l = (RelativeLayout) this.i.findViewById(R.id.box_bg);
            this.m = (LinearLayout) this.i.findViewById(R.id.gamebox);
            this.n = (RelativeLayout) this.i.findViewById(R.id.gb_box_buttonleft_container);
            this.o = (RelativeLayout) this.i.findViewById(R.id.gb_box_buttonright_container);
            this.p = (ImageButton) this.i.findViewById(R.id.gb_box_buttonleft);
            this.q = (ImageButton) this.i.findViewById(R.id.gb_box_buttonright);
            RelativeLayout.LayoutParams layoutParams2 = (RelativeLayout.LayoutParams) this.m.getLayoutParams();
            int dimensionPixelOffset = this.f4740d.getResources().getDimensionPixelOffset(R.dimen.gb_gamebox_padding);
            int dimensionPixelOffset2 = this.f4740d.getResources().getDimensionPixelOffset(R.dimen.gb_gamebox_notch_padding);
            if (i.e()) {
                int a2 = na.a(this.f4740d);
                if (a2 == 0) {
                    layoutParams2.setMargins(dimensionPixelOffset, dimensionPixelOffset2, dimensionPixelOffset, dimensionPixelOffset);
                } else if (a2 == 90) {
                    layoutParams2.setMargins(dimensionPixelOffset2, dimensionPixelOffset, dimensionPixelOffset, dimensionPixelOffset);
                } else if (a2 == 180) {
                    layoutParams2.setMargins(dimensionPixelOffset, dimensionPixelOffset, dimensionPixelOffset, dimensionPixelOffset2);
                } else if (a2 == 270) {
                    layoutParams2.setMargins(dimensionPixelOffset, dimensionPixelOffset, dimensionPixelOffset2, dimensionPixelOffset);
                }
            }
            if (z2) {
                if (na.c()) {
                    layoutParams2.addRule(9);
                }
                this.o.setVisibility(0);
                this.q.setOnClickListener(new l(this, z2));
                gridView = this.j;
                resources = this.f4740d.getResources();
                i2 = R.drawable.gamebox_panelbg_left;
            } else {
                layoutParams2.addRule(11);
                this.n.setVisibility(0);
                this.p.setOnClickListener(new m(this, z2));
                gridView = this.j;
                resources = this.f4740d.getResources();
                i2 = R.drawable.gamebox_panelbg_right;
            }
            gridView.setBackground(resources.getDrawable(i2));
            this.l.setOnClickListener(new n(this));
            this.r = new y(this.f4740d, this.C);
            this.j.setNumColumns(this.C.size() / 2);
            this.j.setAdapter(this.r);
            LinearLayout.LayoutParams layoutParams3 = (LinearLayout.LayoutParams) this.j.getLayoutParams();
            if (this.C.size() / 2 > 2) {
                resources2 = this.f4740d.getResources();
                i3 = R.dimen.gb_gamebox_width_six_item;
            } else {
                resources2 = this.f4740d.getResources();
                i3 = R.dimen.gb_gamebox_width_four_item;
            }
            layoutParams3.width = resources2.getDimensionPixelOffset(i3);
            this.j.setLayoutParams(layoutParams3);
            this.u = true;
            this.h.addView(this.i, this.g);
            this.j.setOnItemClickListener(new o(this));
        }
    }

    public void a(boolean z2, boolean z3) {
        Log.i("ToolBoxWindowManager", "chooseToCreate: left=" + z2 + "\tslide=" + z3);
        b.b.o.d.a.a(this.f4740d);
        if (C0388t.l() && "intent_booster_type_game".equals(this.f4739c.d())) {
            C0373d.j("game_toolbox");
            b(z2, z3);
        } else if (!e.a() || !"intent_booster_type_video_all".equals(this.f4739c.d())) {
            a(z2);
        } else {
            C0373d.j("video_toolbox_new");
            e(z2);
        }
    }

    public void a(boolean z2, boolean z3, View view) {
        if (this.f4740d != null) {
            String c2 = e().c();
            String h2 = b.b.l.b.b().h(c2);
            if (!b.b.l.b.b().i(c2) && !TextUtils.isEmpty(h2)) {
                View inflate = View.inflate(this.f4740d, R.layout.gb_gamebox_active_float, (ViewGroup) null);
                c.a aVar = new c.a();
                aVar.a(view);
                aVar.a(inflate, h2);
                aVar.a(z2, z3);
                aVar.a(2000);
                aVar.a((c.b) new k(this, c2));
                aVar.a();
            }
        }
    }

    public void b() {
        if (this.f4739c.e && !this.t) {
            WindowManager.LayoutParams layoutParams = this.g;
            layoutParams.type = 2003;
            layoutParams.format = -3;
            layoutParams.flags = 264;
            layoutParams.gravity = 51;
            layoutParams.width = -2;
            layoutParams.height = -2;
            LayoutInflater from = LayoutInflater.from(this.f4740d);
            boolean e2 = i.e();
            int i2 = 0;
            int dimensionPixelOffset = (!na.f(this.f4740d) || Build.VERSION.SDK_INT > 28) ? this.f4740d.getResources().getDimensionPixelOffset(b.b.c.b.a()) : 0;
            if (e2) {
                i2 = i.f(this.f4740d);
            }
            if (e2) {
                try {
                    b.b.o.g.e.a((Object) this.g, "extraFlags", (Object) 1792);
                } catch (Exception e3) {
                    Log.i("GameBoosterReflectUtils", e3.toString());
                }
            }
            a(from, dimensionPixelOffset, i2);
            this.t = true;
        }
    }

    public void b(int i2, boolean z2, boolean z3) {
        GameBoxView gameBoxView = this.k;
        if (gameBoxView != null) {
            gameBoxView.b(i2, z2, !z3);
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:14:0x002c, code lost:
        if (r0 != null) goto L_0x0033;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:16:0x0031, code lost:
        if (r0 != null) goto L_0x0033;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void b(boolean r5) {
        /*
            r4 = this;
            com.miui.gamebooster.service.GameBoxWindowManagerService r0 = r4.f4739c
            java.lang.String r0 = r0.d()
            java.lang.String r1 = "intent_booster_type_video_all"
            boolean r0 = r1.equals(r0)
            r1 = 0
            if (r0 == 0) goto L_0x002f
            boolean r0 = com.miui.gamebooster.videobox.settings.f.f()
            int r2 = com.miui.gamebooster.videobox.settings.f.e()
            if (r2 != 0) goto L_0x001b
            r2 = 1
            goto L_0x001c
        L_0x001b:
            r2 = 0
        L_0x001c:
            if (r0 == 0) goto L_0x0026
            if (r2 == 0) goto L_0x0026
            android.view.View r3 = r4.e
            if (r3 == 0) goto L_0x0026
            r1 = r3
            goto L_0x0034
        L_0x0026:
            if (r0 == 0) goto L_0x0034
            if (r2 != 0) goto L_0x0034
            android.view.View r0 = r4.f
            if (r0 == 0) goto L_0x0034
            goto L_0x0033
        L_0x002f:
            android.view.View r0 = r4.e
            if (r0 == 0) goto L_0x0034
        L_0x0033:
            r1 = r0
        L_0x0034:
            r4.a((android.view.View) r1, (boolean) r5)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.gamebooster.p.r.b(boolean):void");
    }

    /* JADX WARNING: Removed duplicated region for block: B:18:0x003e  */
    /* JADX WARNING: Removed duplicated region for block: B:26:0x0055  */
    /* JADX WARNING: Removed duplicated region for block: B:29:0x0064  */
    /* JADX WARNING: Removed duplicated region for block: B:34:0x00b0  */
    /* JADX WARNING: Removed duplicated region for block: B:36:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void b(boolean r5, boolean r6) {
        /*
            r4 = this;
            boolean r0 = r4.u
            if (r0 == 0) goto L_0x0005
            return
        L_0x0005:
            android.content.Context r0 = r4.f4740d
            int r0 = com.miui.gamebooster.m.na.a((android.content.Context) r0)
            r1 = 1
            r2 = 0
            if (r0 == 0) goto L_0x0024
            r3 = 90
            if (r0 == r3) goto L_0x0020
            r3 = 180(0xb4, float:2.52E-43)
            if (r0 == r3) goto L_0x0024
            r3 = 270(0x10e, float:3.78E-43)
            if (r0 == r3) goto L_0x001c
            goto L_0x0025
        L_0x001c:
            if (r6 == 0) goto L_0x0025
            r5 = r2
            goto L_0x0025
        L_0x0020:
            if (r6 == 0) goto L_0x0025
            r5 = r1
            goto L_0x0025
        L_0x0024:
            r2 = r1
        L_0x0025:
            android.view.WindowManager$LayoutParams r6 = r4.g
            r0 = 2003(0x7d3, float:2.807E-42)
            r6.type = r0
            r0 = -3
            r6.format = r0
            r0 = 264(0x108, float:3.7E-43)
            r6.flags = r0
            r0 = -1
            r6.width = r0
            r6.height = r0
            r0 = 2131821199(0x7f11028f, float:1.9275134E38)
            r6.windowAnimations = r0
            if (r2 == 0) goto L_0x0055
            if (r5 == 0) goto L_0x0043
            r0 = 19
            goto L_0x0045
        L_0x0043:
            r0 = 21
        L_0x0045:
            r6.gravity = r0
            android.view.WindowManager$LayoutParams r6 = r4.g
            if (r5 == 0) goto L_0x004f
            r0 = 2131821201(0x7f110291, float:1.9275138E38)
            goto L_0x0052
        L_0x004f:
            r0 = 2131821203(0x7f110293, float:1.9275143E38)
        L_0x0052:
            r6.windowAnimations = r0
            goto L_0x0059
        L_0x0055:
            r0 = 49
            r6.gravity = r0
        L_0x0059:
            android.content.Context r6 = r4.f4740d
            android.view.LayoutInflater r6 = android.view.LayoutInflater.from(r6)
            r0 = 2131493148(0x7f0c011c, float:1.8609768E38)
            if (r2 == 0) goto L_0x006d
            if (r5 == 0) goto L_0x006a
            r0 = 2131493150(0x7f0c011e, float:1.8609772E38)
            goto L_0x006d
        L_0x006a:
            r0 = 2131493152(0x7f0c0120, float:1.8609776E38)
        L_0x006d:
            r3 = 0
            android.view.View r6 = r6.inflate(r0, r3)
            r4.i = r6
            android.view.View r6 = r4.i
            r0 = 2131296867(0x7f090263, float:1.8211663E38)
            android.view.View r6 = r6.findViewById(r0)
            com.miui.gamebooster.customview.GameBoxView r6 = (com.miui.gamebooster.customview.GameBoxView) r6
            r4.k = r6
            com.miui.gamebooster.customview.GameBoxView r6 = r4.k
            r0 = r2 ^ 1
            r6.setmHorzontal(r0)
            com.miui.gamebooster.customview.GameBoxView r6 = r4.k
            r6.setIsLeftShow(r5)
            com.miui.gamebooster.customview.GameBoxView r6 = r4.k
            r6.a((com.miui.gamebooster.p.r) r4)
            com.miui.gamebooster.customview.GameBoxView r6 = r4.k
            com.miui.gamebooster.p.j r0 = new com.miui.gamebooster.p.j
            r0.<init>(r4)
            r6.setOnClickListener(r0)
            com.miui.gamebooster.customview.GameBoxView r6 = r4.k
            android.os.Handler r0 = r4.s
            r6.b((android.os.Handler) r0)
            r4.u = r1
            android.view.WindowManager r6 = r4.h
            android.view.View r0 = r4.i
            android.view.WindowManager$LayoutParams r1 = r4.g
            r6.addView(r0, r1)
            if (r2 == 0) goto L_0x00b5
            com.miui.gamebooster.customview.GameBoxView r6 = r4.k
            r6.a((boolean) r5)
        L_0x00b5:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.gamebooster.p.r.b(boolean, boolean):void");
    }

    public long c() {
        return this.z;
    }

    public void c(boolean z2) {
        C0373d.a(!z2);
    }

    public GameBoxView d() {
        return this.k;
    }

    public void d(boolean z2) {
        this.v = z2;
    }

    public GameBoxWindowManagerService e() {
        return this.f4739c;
    }

    public boolean f() {
        return this.v;
    }

    public void g() {
        C0354x xVar = this.A;
        if (xVar != null) {
            xVar.dismiss();
            this.A = null;
        }
    }

    public void h() {
        if (this.h != null && this.t) {
            View view = this.e;
            if (view != null) {
                try {
                    this.h.removeView(view);
                    this.h.removeView(this.f);
                    this.t = false;
                } catch (Exception e2) {
                    Log.e("ToolBoxWindowManager", e2.toString());
                }
            }
            LocalBroadcastManager.getInstance(this.f4740d).sendBroadcast(new Intent("GAMEBOX_WINDOW_REMOVED"));
        }
    }

    public void i() {
        GameBoxView gameBoxView = this.k;
        if (gameBoxView != null) {
            gameBoxView.a(this.s);
        }
        j();
        if (this.f4739c.e) {
            b();
        }
    }

    public void j() {
        if (!(this.h == null || this.i == null || !this.u)) {
            if (this.w != null) {
                this.f4740d.getContentResolver().unregisterContentObserver(this.w);
                this.w = null;
            }
            Log.i("ToolBoxWindowManager", "remove float view : " + this.i);
            try {
                if (this.k != null) {
                    this.k.a();
                    this.k.a(this.s);
                    this.k.setmFirstExpand(false);
                    this.k.setmSecondRecentExpand(false);
                    this.k.setmSecondFunctionExpand(false);
                }
                this.h.removeView(this.i);
                this.u = false;
            } catch (Exception e2) {
                Log.e("ToolBoxWindowManager", e2.toString());
            }
        }
        a(this.x);
    }

    public void k() {
        a(this.x);
        if (this.f4739c.e) {
            b();
        }
    }
}
