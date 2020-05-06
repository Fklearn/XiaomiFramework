package com.miui.gamebooster.customview.a;

import android.view.View;
import android.view.ViewConfiguration;
import android.widget.LinearLayout;
import com.miui.gamebooster.customview.GameBoxView;
import com.miui.gamebooster.m.C0388t;
import com.miui.gamebooster.p.r;
import com.miui.gamebooster.widget.GbSlideOutLayout;
import com.miui.securitycenter.Application;

public class d extends a implements View.OnTouchListener {
    /* access modifiers changed from: private */
    public r f;
    private int g;
    private int h = 600;
    private int i;
    private int j = -70;
    private int k;
    private boolean l;
    private boolean m = true;
    private boolean n;
    /* access modifiers changed from: private */
    public boolean o;
    /* access modifiers changed from: private */
    public boolean p;
    private boolean q;
    /* access modifiers changed from: private */
    public LinearLayout r;
    private boolean s = C0388t.l();

    public d(r rVar, boolean z) {
        this.e = ViewConfiguration.get(Application.d()).getScaledTouchSlop();
        this.f = rVar;
        this.p = z;
    }

    private boolean a(GbSlideOutLayout.a aVar, int i2, int i3) {
        GameBoxView d2;
        r rVar = this.f;
        if (rVar == null || (d2 = rVar.d()) == null) {
            return false;
        }
        return d2.a(aVar, i2, i3);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:5:0x003a, code lost:
        if (r12 != 3) goto L_0x0358;
     */
    /* JADX WARNING: Removed duplicated region for block: B:61:0x014b  */
    /* JADX WARNING: Removed duplicated region for block: B:62:0x0151  */
    /* JADX WARNING: Removed duplicated region for block: B:70:0x016c  */
    /* JADX WARNING: Removed duplicated region for block: B:76:0x018e  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean onTouch(android.view.View r12, android.view.MotionEvent r13) {
        /*
            r11 = this;
            int r12 = r13.getAction()
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r1 = "onTouch: mSupportGameTurbo="
            r0.append(r1)
            boolean r1 = r11.s
            r0.append(r1)
            java.lang.String r1 = "\tmAdded="
            r0.append(r1)
            boolean r1 = r11.l
            r0.append(r1)
            java.lang.String r1 = "\tmToolBoxLayoutManager"
            r0.append(r1)
            com.miui.gamebooster.p.r r1 = r11.f
            r0.append(r1)
            java.lang.String r0 = r0.toString()
            java.lang.String r1 = "GameBoxTouchListener"
            android.util.Log.i(r1, r0)
            r0 = 2
            r2 = 1
            r3 = 0
            if (r12 == 0) goto L_0x0299
            if (r12 == r2) goto L_0x0195
            if (r12 == r0) goto L_0x003e
            r1 = 3
            if (r12 == r1) goto L_0x0195
            goto L_0x0358
        L_0x003e:
            float r12 = r13.getX()
            int r12 = (int) r12
            float r1 = r13.getY()
            int r1 = (int) r1
            boolean r4 = r11.s
            r5 = 4611686018427387904(0x4000000000000000, double:2.0)
            if (r4 != 0) goto L_0x006b
            int r4 = r11.f4170b
            int r4 = r12 - r4
            int r4 = java.lang.Math.abs(r4)
            double r7 = (double) r4
            double r7 = java.lang.Math.pow(r7, r5)
            int r4 = r11.f4169a
            int r4 = r1 - r4
            int r4 = java.lang.Math.abs(r4)
            double r9 = (double) r4
            double r4 = java.lang.Math.pow(r9, r5)
            double r7 = r7 + r4
            int r4 = (int) r7
            goto L_0x008e
        L_0x006b:
            int r4 = r11.f4172d
            int r4 = r12 - r4
            int r4 = java.lang.Math.abs(r4)
            double r7 = (double) r4
            double r7 = java.lang.Math.pow(r7, r5)
            int r4 = r11.f4171c
            int r4 = r1 - r4
            int r4 = java.lang.Math.abs(r4)
            double r9 = (double) r4
            double r4 = java.lang.Math.pow(r9, r5)
            double r7 = r7 + r4
            r4 = 0
            double r7 = r7 + r4
            double r4 = java.lang.Math.sqrt(r7)
            int r4 = (int) r4
        L_0x008e:
            r11.g = r4
            boolean r4 = r11.s
            if (r4 == 0) goto L_0x0358
            com.miui.gamebooster.widget.GbSlideOutLayout$a r4 = com.miui.gamebooster.widget.GbSlideOutLayout.a.EVENT_MOVE
            float r5 = r13.getRawX()
            int r5 = (int) r5
            float r13 = r13.getRawY()
            int r13 = (int) r13
            boolean r13 = r11.a(r4, r5, r13)
            int r4 = r11.f4172d
            int r4 = r12 - r4
            int r5 = r11.f4171c
            int r5 = r1 - r5
            boolean r6 = r11.p
            if (r6 == 0) goto L_0x00d0
            if (r5 < 0) goto L_0x00c3
            if (r4 >= 0) goto L_0x00c1
            int r4 = java.lang.Math.abs(r4)
            int r5 = java.lang.Math.abs(r5)
            if (r4 > r5) goto L_0x00bf
            goto L_0x00c1
        L_0x00bf:
            r4 = r3
            goto L_0x00ec
        L_0x00c1:
            r4 = r2
            goto L_0x00ec
        L_0x00c3:
            if (r4 <= 0) goto L_0x00bf
            int r4 = java.lang.Math.abs(r4)
            int r5 = java.lang.Math.abs(r5)
            if (r4 <= r5) goto L_0x00bf
            goto L_0x00c1
        L_0x00d0:
            if (r5 < 0) goto L_0x00df
            if (r4 <= 0) goto L_0x00c1
            int r4 = java.lang.Math.abs(r4)
            int r5 = java.lang.Math.abs(r5)
            if (r4 > r5) goto L_0x00bf
            goto L_0x00c1
        L_0x00df:
            if (r4 >= 0) goto L_0x00bf
            int r4 = java.lang.Math.abs(r4)
            int r5 = java.lang.Math.abs(r5)
            if (r4 <= r5) goto L_0x00bf
            goto L_0x00c1
        L_0x00ec:
            if (r4 == 0) goto L_0x00ff
            int r5 = r11.k
            if (r5 >= 0) goto L_0x00ff
            int r13 = r11.g
            int r5 = r5 + r13
            r11.k = r5
            int r13 = r11.k
            if (r13 <= 0) goto L_0x00fc
            r13 = r3
        L_0x00fc:
            r11.k = r13
            goto L_0x013f
        L_0x00ff:
            if (r4 != 0) goto L_0x0116
            int r5 = r11.k
            int r6 = r11.h
            int r7 = -r6
            if (r5 <= r7) goto L_0x0116
            if (r13 != 0) goto L_0x0116
            int r13 = r11.g
            int r5 = r5 - r13
            r11.k = r5
            int r13 = r11.k
            int r0 = -r6
            if (r13 >= r0) goto L_0x00fc
            int r13 = -r6
            goto L_0x00fc
        L_0x0116:
            if (r4 == 0) goto L_0x013f
            int r13 = r11.k
            if (r13 != 0) goto L_0x013f
            boolean r13 = r11.o
            if (r13 == 0) goto L_0x013f
            int r13 = r11.j
            int r4 = r11.i
            if (r13 > r4) goto L_0x013f
            boolean r13 = r11.q
            if (r13 == 0) goto L_0x012e
            boolean r13 = r11.m
            if (r13 != 0) goto L_0x013f
        L_0x012e:
            int r13 = r11.j
            int r4 = r11.g
            int r4 = r4 / r0
            int r13 = r13 + r4
            r11.j = r13
            int r13 = r11.j
            int r0 = r11.i
            if (r13 <= r0) goto L_0x013d
            r13 = r0
        L_0x013d:
            r11.j = r13
        L_0x013f:
            android.widget.LinearLayout r13 = r11.r
            android.view.ViewGroup$LayoutParams r13 = r13.getLayoutParams()
            android.widget.RelativeLayout$LayoutParams r13 = (android.widget.RelativeLayout.LayoutParams) r13
            boolean r0 = r11.o
            if (r0 == 0) goto L_0x0151
            int r0 = r11.k
            r13.setMargins(r3, r0, r3, r3)
            goto L_0x0160
        L_0x0151:
            boolean r0 = r11.p
            if (r0 == 0) goto L_0x015b
            int r0 = r11.k
            r13.setMargins(r0, r3, r3, r3)
            goto L_0x0160
        L_0x015b:
            int r0 = r11.k
            r13.setMargins(r3, r3, r0, r3)
        L_0x0160:
            r11.f4172d = r12
            r11.f4171c = r1
            boolean r12 = r11.o
            if (r12 == 0) goto L_0x018e
            int r12 = r11.j
            if (r12 <= 0) goto L_0x018e
            boolean r12 = r11.p
            if (r12 == 0) goto L_0x0181
            boolean r12 = r11.m
            if (r12 != 0) goto L_0x0358
            com.miui.gamebooster.p.r r12 = r11.f
            com.miui.gamebooster.customview.GameBoxView r12 = r12.d()
            int r13 = r11.j
            r12.b((int) r13)
            goto L_0x0358
        L_0x0181:
            com.miui.gamebooster.p.r r12 = r11.f
            com.miui.gamebooster.customview.GameBoxView r12 = r12.d()
            int r13 = r11.j
            r12.a((int) r13)
            goto L_0x0358
        L_0x018e:
            android.widget.LinearLayout r12 = r11.r
            r12.setLayoutParams(r13)
            goto L_0x0358
        L_0x0195:
            boolean r12 = r11.s
            if (r12 == 0) goto L_0x0282
            com.miui.gamebooster.widget.GbSlideOutLayout$a r12 = com.miui.gamebooster.widget.GbSlideOutLayout.a.EVENT_UP
            float r1 = r13.getRawX()
            int r1 = (int) r1
            float r13 = r13.getRawY()
            int r13 = (int) r13
            r11.a(r12, r1, r13)
            int r12 = r11.k
            int r13 = r11.h
            int r1 = r13 / 2
            int r12 = r12 + r1
            if (r12 <= 0) goto L_0x01b3
            r12 = r3
            goto L_0x01b4
        L_0x01b3:
            int r12 = -r13
        L_0x01b4:
            com.miui.gamebooster.p.r r13 = r11.f
            boolean r1 = r11.o
            if (r1 != 0) goto L_0x01bd
            java.lang.String r1 = "translationX"
            goto L_0x01bf
        L_0x01bd:
            java.lang.String r1 = "translationY"
        L_0x01bf:
            float[] r4 = new float[r0]
            int r5 = r11.k
            float r5 = (float) r5
            r4[r3] = r5
            float r5 = (float) r12
            r4[r2] = r5
            android.animation.ObjectAnimator r13 = android.animation.ObjectAnimator.ofFloat(r13, r1, r4)
            android.view.animation.LinearInterpolator r1 = new android.view.animation.LinearInterpolator
            r1.<init>()
            r13.setInterpolator(r1)
            int r1 = r11.k
            int r1 = r1 - r12
            int r1 = java.lang.Math.abs(r1)
            int r4 = r11.h
            int r4 = r4 / r0
            int r1 = r1 / r4
            int r1 = r1 * 1000
            long r0 = (long) r1
            r13.setDuration(r0)
            com.miui.gamebooster.customview.a.b r0 = new com.miui.gamebooster.customview.a.b
            r0.<init>(r11)
            r13.addUpdateListener(r0)
            com.miui.gamebooster.customview.a.c r0 = new com.miui.gamebooster.customview.a.c
            r0.<init>(r11, r12)
            r13.addListener(r0)
            r13.start()
            r11.g = r3
            r11.l = r3
            boolean r13 = r11.o
            if (r13 == 0) goto L_0x0275
            int r13 = r11.j
            r0 = 100
            if (r13 <= r0) goto L_0x0248
            boolean r13 = r11.p
            if (r13 == 0) goto L_0x0228
            boolean r13 = r11.m
            if (r13 != 0) goto L_0x0228
            com.miui.gamebooster.p.r r13 = r11.f
            com.miui.gamebooster.customview.GameBoxView r13 = r13.d()
            com.miui.gamebooster.customview.GameBoxFunctionItemView r13 = r13.getmLeftArrow()
            int r0 = r11.j
            r13.j = r0
            com.miui.gamebooster.p.r r13 = r11.f
            com.miui.gamebooster.customview.GameBoxView r13 = r13.d()
            com.miui.gamebooster.customview.GameBoxFunctionItemView r13 = r13.getmLeftArrow()
            goto L_0x0244
        L_0x0228:
            boolean r13 = r11.p
            if (r13 != 0) goto L_0x026d
            com.miui.gamebooster.p.r r13 = r11.f
            com.miui.gamebooster.customview.GameBoxView r13 = r13.d()
            com.miui.gamebooster.customview.GameBoxFunctionItemView r13 = r13.getmRightArrow()
            int r0 = r11.j
            r13.j = r0
            com.miui.gamebooster.p.r r13 = r11.f
            com.miui.gamebooster.customview.GameBoxView r13 = r13.d()
            com.miui.gamebooster.customview.GameBoxFunctionItemView r13 = r13.getmRightArrow()
        L_0x0244:
            r13.callOnClick()
            goto L_0x026d
        L_0x0248:
            if (r13 <= 0) goto L_0x026d
            boolean r13 = r11.p
            if (r13 == 0) goto L_0x025e
            boolean r13 = r11.m
            if (r13 != 0) goto L_0x025e
            com.miui.gamebooster.p.r r13 = r11.f
            com.miui.gamebooster.customview.GameBoxView r13 = r13.d()
            int r0 = r11.j
            r13.b((int) r0, (boolean) r3, (boolean) r2)
            goto L_0x026d
        L_0x025e:
            boolean r13 = r11.p
            if (r13 != 0) goto L_0x026d
            com.miui.gamebooster.p.r r13 = r11.f
            com.miui.gamebooster.customview.GameBoxView r13 = r13.d()
            int r0 = r11.j
            r13.a((int) r0, (boolean) r3, (boolean) r2)
        L_0x026d:
            r11.m = r2
            r13 = -70
            r11.j = r13
            r11.n = r3
        L_0x0275:
            if (r12 != 0) goto L_0x0358
            com.miui.gamebooster.p.r r12 = r11.f
            if (r12 == 0) goto L_0x0358
            boolean r13 = r11.o
            r12.c((boolean) r13)
            goto L_0x0358
        L_0x0282:
            int r12 = r11.g
            int r13 = r11.e
            int r13 = r13 * r13
            if (r12 <= r13) goto L_0x0358
            com.miui.gamebooster.p.r r12 = r11.f
            if (r12 == 0) goto L_0x0358
            r12.h()
            com.miui.gamebooster.p.r r12 = r11.f
            boolean r13 = r11.p
            r12.a((boolean) r13, (boolean) r3)
            goto L_0x0358
        L_0x0299:
            float r12 = r13.getX()
            int r12 = (int) r12
            r11.f4170b = r12
            float r12 = r13.getY()
            int r12 = (int) r12
            r11.f4169a = r12
            float r12 = r13.getRawX()
            com.miui.securitycenter.Application r4 = com.miui.securitycenter.Application.d()
            int r4 = com.miui.gamebooster.m.na.e(r4)
            int r4 = r4 / r0
            float r4 = (float) r4
            int r12 = (r12 > r4 ? 1 : (r12 == r4 ? 0 : -1))
            if (r12 >= 0) goto L_0x02bb
            r12 = r2
            goto L_0x02bc
        L_0x02bb:
            r12 = r3
        L_0x02bc:
            r11.q = r12
            boolean r12 = r11.s
            if (r12 == 0) goto L_0x0358
            com.miui.gamebooster.widget.GbSlideOutLayout$a r12 = com.miui.gamebooster.widget.GbSlideOutLayout.a.EVENT_DOWN
            float r4 = r13.getRawX()
            int r4 = (int) r4
            float r13 = r13.getRawY()
            int r13 = (int) r13
            r11.a(r12, r4, r13)
            int r12 = r11.f4170b
            r11.f4172d = r12
            int r12 = r11.f4169a
            r11.f4171c = r12
            boolean r12 = r11.l
            if (r12 != 0) goto L_0x0358
            com.miui.gamebooster.p.r r12 = r11.f
            if (r12 == 0) goto L_0x0358
            r11.l = r2
            boolean r13 = r11.p
            r12.a((boolean) r13, (boolean) r3)
            com.miui.gamebooster.p.r r12 = r11.f
            com.miui.gamebooster.customview.GameBoxView r12 = r12.d()
            android.widget.LinearLayout r12 = r12.getmGameBoosterFirstLevel()
            r11.r = r12
            com.miui.gamebooster.p.r r12 = r11.f
            com.miui.gamebooster.customview.GameBoxView r12 = r12.d()
            boolean r12 = r12.b()
            r11.o = r12
            com.miui.gamebooster.p.r r12 = r11.f
            com.miui.gamebooster.customview.GameBoxView r12 = r12.d()
            int r12 = r12.getmSecondHeight()
            r11.i = r12
            android.widget.LinearLayout r12 = r11.r
            android.view.ViewGroup$LayoutParams r12 = r12.getLayoutParams()
            android.widget.RelativeLayout$LayoutParams r12 = (android.widget.RelativeLayout.LayoutParams) r12
            int r13 = r11.h
            int r13 = -r13
            int r13 = r13 / r0
            r11.k = r13
            boolean r13 = r11.o
            if (r13 == 0) goto L_0x032e
            int r13 = r11.k
            r12.setMargins(r3, r13, r3, r3)
            com.miui.gamebooster.f.c r13 = com.miui.gamebooster.f.c.a()
            boolean r13 = r13.b()
            r11.m = r13
            goto L_0x033d
        L_0x032e:
            boolean r13 = r11.p
            if (r13 == 0) goto L_0x0338
            int r13 = r11.k
            r12.setMargins(r13, r3, r3, r3)
            goto L_0x033d
        L_0x0338:
            int r13 = r11.k
            r12.setMargins(r3, r3, r13, r3)
        L_0x033d:
            android.widget.LinearLayout r13 = r11.r
            r13.setLayoutParams(r12)
            java.lang.StringBuilder r12 = new java.lang.StringBuilder
            r12.<init>()
            java.lang.String r13 = "ACTION_DOWN updateY"
            r12.append(r13)
            int r13 = r11.k
            r12.append(r13)
            java.lang.String r12 = r12.toString()
            android.util.Log.i(r1, r12)
        L_0x0358:
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.gamebooster.customview.a.d.onTouch(android.view.View, android.view.MotionEvent):boolean");
    }
}
