package com.miui.gamebooster.a;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import com.miui.gamebooster.gamead.g;
import com.miui.gamebooster.globalgame.global.b;
import com.miui.gamebooster.globalgame.module.BannerCardBean;
import com.miui.gamebooster.globalgame.present.e;
import com.miui.gamebooster.globalgame.present.h;
import com.miui.gamebooster.globalgame.util.Utils;
import com.miui.gamebooster.m.na;
import com.miui.gamebooster.model.m;
import com.miui.gamebooster.ui.N;
import com.miui.gamebooster.viewPointwidget.c;
import com.miui.securitycenter.R;
import java.util.ArrayList;
import java.util.List;

public class v implements c, m, h {

    /* renamed from: a  reason: collision with root package name */
    private View f4067a;
    /* access modifiers changed from: private */

    /* renamed from: b  reason: collision with root package name */
    public View f4068b;
    /* access modifiers changed from: private */

    /* renamed from: c  reason: collision with root package name */
    public View f4069c;
    /* access modifiers changed from: private */

    /* renamed from: d  reason: collision with root package name */
    public View f4070d;
    /* access modifiers changed from: private */
    public View e;
    private View f;
    /* access modifiers changed from: private */
    public View g;
    /* access modifiers changed from: private */
    public ImageView h;
    /* access modifiers changed from: private */
    public ListView i;
    private g j;
    /* access modifiers changed from: private */
    public e k;
    /* access modifiers changed from: private */
    public boolean l = true;
    /* access modifiers changed from: private */
    public float m = 0.0f;
    private float n;
    /* access modifiers changed from: private */
    public float o = -1.0f;
    private float p;
    private boolean q = false;
    /* access modifiers changed from: private */
    public float r = 0.33333334f;
    /* access modifiers changed from: private */
    public Context s;

    public v(Activity activity, N.d dVar) {
        this.s = activity.getApplicationContext();
        this.j = new b(activity, new ArrayList(), dVar);
        this.k = new e(this.s, this);
    }

    /* access modifiers changed from: private */
    public void a(float f2, long j2, View... viewArr) {
        for (View view : viewArr) {
            if (view != null) {
                view.animate().y(f2).setDuration(j2).start();
            }
        }
    }

    /* access modifiers changed from: private */
    public void a(Context context, boolean z) {
        if (this.i != null) {
            int b2 = na.b(context);
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) this.i.getLayoutParams();
            if (!z) {
                b2 = 0;
            }
            layoutParams.bottomMargin = b2;
            this.i.setLayoutParams(layoutParams);
        }
    }

    private void a(View view) {
        this.f4068b = view.findViewById(R.id.guideRespondingCover);
        this.e = view.findViewById(R.id.feedContainer);
        this.f = view.findViewById(R.id.coloredBgView);
        this.f4069c = view.findViewById(R.id.arrowUp);
        this.f4070d = view.findViewById(R.id.feedTitle);
        this.h = (ImageView) view.findViewById(R.id.closeFeed);
        this.i = (ListView) view.findViewById(R.id.listView);
        this.g = new View(this.s);
        this.g.setVisibility(8);
        this.g.setBackgroundResource(R.color.feedWholeBgCoverColor);
        int a2 = Utils.a(view);
        if (a2 != -1) {
            ((ViewGroup) view.getParent()).addView(this.g, a2, new ViewGroup.LayoutParams(-1, -1));
        }
        this.f.setBackgroundColor(0);
        this.i.setAdapter(this.j);
        Utils.a(this.s, this.f4070d);
        a(this.s, true);
    }

    private void a(View view, long j2, boolean z) {
        int color = z ? this.s.getResources().getColor(R.color.baseBg) : 0;
        ValueAnimator ofInt = ValueAnimator.ofInt(z ? new int[]{0, 255} : new int[]{255, 0});
        ofInt.setDuration(j2);
        ofInt.addUpdateListener(new s(this, view));
        ofInt.addListener(new t(this, view, color));
        ofInt.start();
    }

    private void a(@Nullable Runnable runnable) {
        a(this.s, false);
        this.e.setVisibility(0);
        a(this.f, 500, true);
        this.f4069c.setVisibility(8);
        this.e.animate().withStartAction(new r(this, 500)).translationY(0.0f).setDuration(500).withEndAction(new p(this, runnable)).start();
        this.l = false;
        com.miui.gamebooster.i.a.b.f(this.s);
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Code restructure failed: missing block: B:9:0x001a, code lost:
        if (r2 != 3) goto L_0x011f;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean a(android.view.View r9, android.view.MotionEvent r10) {
        /*
            r8 = this;
            com.miui.gamebooster.globalgame.present.e r0 = r8.k
            r1 = 0
            if (r0 != 0) goto L_0x0006
            return r1
        L_0x0006:
            float r0 = r9.getY()
            float r2 = r8.o
            float r0 = r0 - r2
            int r2 = r10.getAction()
            r3 = 1
            if (r2 == 0) goto L_0x00eb
            if (r2 == r3) goto L_0x00cd
            r4 = 2
            if (r2 == r4) goto L_0x001e
            r10 = 3
            if (r2 == r10) goto L_0x00cd
            goto L_0x011f
        L_0x001e:
            float r2 = r9.getY()
            float r4 = r8.o
            int r2 = (r2 > r4 ? 1 : (r2 == r4 ? 0 : -1))
            if (r2 == 0) goto L_0x002a
            r2 = r3
            goto L_0x002b
        L_0x002a:
            r2 = r1
        L_0x002b:
            r8.q = r2
            float r2 = r9.getY()
            float r4 = r8.p
            int r2 = (r2 > r4 ? 1 : (r2 == r4 ? 0 : -1))
            if (r2 >= 0) goto L_0x0039
            goto L_0x011f
        L_0x0039:
            float r10 = r10.getRawY()
            float r2 = r8.n
            float r10 = r10 + r2
            float r2 = r8.p
            float r2 = java.lang.Math.max(r2, r10)
            float r4 = r8.o
            int r4 = (r2 > r4 ? 1 : (r2 == r4 ? 0 : -1))
            if (r4 <= 0) goto L_0x004e
            r4 = r3
            goto L_0x004f
        L_0x004e:
            r4 = r1
        L_0x004f:
            if (r4 == 0) goto L_0x0059
            float r5 = r8.o
            float r5 = r2 - r5
            r6 = 1056964608(0x3f000000, float:0.5)
            float r5 = r5 * r6
            float r2 = r2 - r5
        L_0x0059:
            r5 = 0
            android.view.View[] r7 = new android.view.View[r3]
            r7[r1] = r9
            r8.a((float) r2, (long) r5, (android.view.View[]) r7)
            if (r4 != 0) goto L_0x011f
            android.view.View r9 = r8.g
            r9.setVisibility(r1)
            com.miui.gamebooster.globalgame.present.e r9 = r8.k
            float r9 = r9.c()
            int r9 = (int) r9
            float r1 = r8.o
            float r1 = r10 - r1
            float r1 = java.lang.Math.abs(r1)
            float r9 = (float) r9
            float r2 = r8.r
            r4 = 1065353216(0x3f800000, float:1.0)
            float r2 = r4 - r2
            float r2 = r2 * r9
            float r1 = r1 / r2
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r5 = "alpha="
            r2.append(r5)
            r2.append(r1)
            java.lang.String r5 = ",gap="
            r2.append(r5)
            r2.append(r0)
            java.lang.String r0 = ",delta="
            r2.append(r0)
            float r0 = r8.o
            float r10 = r10 - r0
            float r10 = java.lang.Math.abs(r10)
            r2.append(r10)
            java.lang.String r10 = ", VAL="
            r2.append(r10)
            float r10 = r8.r
            float r10 = r4 - r10
            float r9 = r9 * r10
            r2.append(r9)
            java.lang.String r9 = r2.toString()
            com.miui.gamebooster.globalgame.util.b.c(r9)
            android.view.View r9 = r8.g
            float r10 = java.lang.Math.min(r1, r4)
            r9.setAlpha(r10)
            android.view.View r9 = r8.f4069c
            float r10 = java.lang.Math.min(r1, r4)
            float r4 = r4 - r10
            r9.setAlpha(r4)
            goto L_0x011f
        L_0x00cd:
            com.miui.gamebooster.a.o r10 = new com.miui.gamebooster.a.o
            r10.<init>(r8, r0, r9)
            r9 = -1027080192(0xffffffffc2c80000, float:-100.0)
            int r9 = (r0 > r9 ? 1 : (r0 == r9 ? 0 : -1))
            if (r9 < 0) goto L_0x00e6
            r9 = 0
            int r9 = (r0 > r9 ? 1 : (r0 == r9 ? 0 : -1))
            if (r9 != 0) goto L_0x00e2
            boolean r9 = r8.q
            if (r9 != 0) goto L_0x00e2
            goto L_0x00e6
        L_0x00e2:
            r10.run()
            goto L_0x011f
        L_0x00e6:
            r9 = 0
            r8.a((java.lang.Runnable) r9)
            return r1
        L_0x00eb:
            r8.q = r1
            float r0 = r8.o
            r1 = -1082130432(0xffffffffbf800000, float:-1.0)
            int r0 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r0 != 0) goto L_0x0114
            float r0 = r9.getY()
            r8.o = r0
            float r0 = r8.o
            com.miui.gamebooster.globalgame.present.e r1 = r8.k
            float r1 = r1.c()
            r2 = 1073741824(0x40000000, float:2.0)
            float r1 = r1 * r2
            r2 = 1077936128(0x40400000, float:3.0)
            float r1 = r1 / r2
            float r0 = r0 - r1
            android.content.Context r1 = r8.s
            int r1 = com.miui.gamebooster.globalgame.global.GlobalCardVH.gapItemVerticalPadding(r1)
            float r1 = (float) r1
            float r0 = r0 - r1
            r8.p = r0
        L_0x0114:
            float r9 = r9.getY()
            float r10 = r10.getRawY()
            float r9 = r9 - r10
            r8.n = r9
        L_0x011f:
            return r3
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.gamebooster.a.v.a(android.view.View, android.view.MotionEvent):boolean");
    }

    /* access modifiers changed from: private */
    public void e() {
        a(this.f, 250, false);
        this.e.animate().translationY(this.m).setDuration(500).withStartAction(new C0330h(this, 500)).withEndAction(new u(this)).start();
        com.miui.gamebooster.i.a.b.e(this.s);
    }

    private void f() {
        this.f4068b.setOnTouchListener(new k(this));
        this.i.setOnScrollListener(new l(this));
        this.h.setOnClickListener(new m(this));
    }

    public void a() {
        Utils.b(this.k);
    }

    public void a(ViewStub viewStub) {
        if (this.f4067a == null) {
            this.f4067a = viewStub.inflate();
            a(this.f4067a);
            f();
        }
    }

    public void a(List<BannerCardBean> list) {
        g gVar = this.j;
        if (gVar instanceof h) {
            ((h) gVar).a(list);
        }
        d();
    }

    public void b() {
        Utils.c(this.k);
    }

    public boolean c() {
        if (this.l || this.e.getVisibility() != 0 || this.i == null) {
            return false;
        }
        if (this.i.getChildCount() == 0) {
            e();
            return true;
        }
        if (this.i.getChildAt(0).getTop() == 0) {
            e();
        } else {
            this.i.smoothScrollToPosition(0);
        }
        return true;
    }

    public void d() {
        if (this.k != null) {
            j jVar = new j(this);
            if (this.e.getHeight() > 0) {
                jVar.run();
            } else {
                Utils.a(this.e, (Runnable) jVar);
            }
            com.miui.gamebooster.i.a.b.c(this.s);
        }
    }

    public void onDestroy() {
    }

    public void onPause() {
        Utils.a(this.k);
    }

    public void onStop() {
        Utils.d(this.k);
    }
}
