package com.miui.gamebooster.view;

import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.widget.ImageView;
import android.widget.TextView;
import com.miui.gamebooster.a.x;
import com.miui.gamebooster.globalgame.util.Utils;
import com.miui.gamebooster.m.na;
import com.miui.securitycenter.R;

public class n {

    /* renamed from: a  reason: collision with root package name */
    private static final float[][] f5312a = {new float[]{1.7777778f, 2.4776325f, 0.82218784f, 4.3530836f, 20.338982f, 11.641791f, 0.10897436f, 0.5277778f}, new float[]{1.0f, 2.0373514f, 0.7923033f, 5.872757f, 16.58986f, 17.333334f, 0.08589744f, 0.425f}};

    /* renamed from: b  reason: collision with root package name */
    private static float f5313b = 0.0f;

    /* renamed from: c  reason: collision with root package name */
    private static int f5314c;

    /* renamed from: d  reason: collision with root package name */
    private static int f5315d;

    public static class a extends ViewPager.SimpleOnPageChangeListener {

        /* renamed from: a  reason: collision with root package name */
        private x f5316a;

        /* renamed from: b  reason: collision with root package name */
        private TextView f5317b;

        /* renamed from: c  reason: collision with root package name */
        private View f5318c;

        public a(x xVar, TextView textView, View view) {
            this.f5316a = xVar;
            this.f5317b = textView;
            this.f5318c = view;
        }

        public void onPageSelected(int i) {
            ImageView a2;
            int size = this.f5316a.b().size();
            int i2 = 0;
            int i3 = 0;
            while (i3 < size) {
                x.b f = this.f5316a.f(i3);
                if (!(f == null || (a2 = x.a(f)) == null)) {
                    boolean z = i3 == i;
                    float f2 = 1.0f;
                    ViewPropertyAnimator scaleX = a2.animate().scaleX(z ? 1.0f : n.b());
                    if (!z) {
                        f2 = n.b();
                    }
                    scaleX.scaleY(f2).setDuration(500).start();
                    x.a(f, z);
                }
                i3++;
            }
            CharSequence b2 = this.f5316a.b(i);
            View view = this.f5318c;
            if (TextUtils.isEmpty(b2)) {
                i2 = 8;
            }
            view.setVisibility(i2);
            this.f5317b.setText((!TextUtils.isEmpty(b2) || this.f5317b.getContext() == null) ? b2.toString() : this.f5317b.getContext().getString(R.string.add_game));
            String c2 = this.f5316a.c(i);
            if (TextUtils.isEmpty(c2)) {
                c2 = "add_game_fake_pkg_name";
            }
            Utils.b(c2);
        }
    }

    public static class b implements View.OnTouchListener {
        /* access modifiers changed from: private */

        /* renamed from: a  reason: collision with root package name */
        public boolean f5319a = true;
        /* access modifiers changed from: private */

        /* renamed from: b  reason: collision with root package name */
        public boolean f5320b = false;
        /* access modifiers changed from: private */

        /* renamed from: c  reason: collision with root package name */
        public View f5321c;

        public b(View view) {
            this.f5321c = view;
        }

        /* JADX WARNING: Code restructure failed: missing block: B:6:0x0010, code lost:
            if (r5 != 3) goto L_0x0050;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public boolean onTouch(android.view.View r5, android.view.MotionEvent r6) {
            /*
                r4 = this;
                int r5 = r6.getAction()
                r0 = 250(0xfa, double:1.235E-321)
                r6 = 0
                r2 = 1
                if (r5 == 0) goto L_0x0026
                if (r5 == r2) goto L_0x0013
                r3 = 2
                if (r5 == r3) goto L_0x0026
                r2 = 3
                if (r5 == r2) goto L_0x0013
                goto L_0x0050
            L_0x0013:
                com.miui.gamebooster.view.p r5 = new com.miui.gamebooster.view.p
                r5.<init>(r4)
                boolean r2 = r4.f5320b
                if (r2 == 0) goto L_0x0022
                android.view.View r2 = r4.f5321c
                r2.postDelayed(r5, r0)
                goto L_0x0050
            L_0x0022:
                r5.run()
                goto L_0x0050
            L_0x0026:
                boolean r5 = r4.f5319a
                if (r5 != 0) goto L_0x002b
                goto L_0x0050
            L_0x002b:
                r5 = 1066192077(0x3f8ccccd, float:1.1)
                r4.f5320b = r2
                android.view.View r2 = r4.f5321c
                android.view.ViewPropertyAnimator r2 = r2.animate()
                android.view.ViewPropertyAnimator r2 = r2.scaleX(r5)
                android.view.ViewPropertyAnimator r5 = r2.scaleY(r5)
                android.view.ViewPropertyAnimator r5 = r5.setDuration(r0)
                com.miui.gamebooster.view.o r0 = new com.miui.gamebooster.view.o
                r0.<init>(r4)
                android.view.ViewPropertyAnimator r5 = r5.setListener(r0)
                r5.start()
                r4.f5319a = r6
            L_0x0050:
                return r6
            */
            throw new UnsupportedOperationException("Method not decompiled: com.miui.gamebooster.view.n.b.onTouch(android.view.View, android.view.MotionEvent):boolean");
        }
    }

    public static void a(View view) {
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        if (layoutParams instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) layoutParams;
            marginLayoutParams.setMargins(marginLayoutParams.leftMargin, (int) (((float) c()) * f()[6] * d()), marginLayoutParams.rightMargin, marginLayoutParams.bottomMargin);
            view.requestLayout();
        }
    }

    public static void a(View view, ViewPager viewPager, View view2) {
        Utils.b((View) viewPager, (Runnable) new l(e(), f(), c(), view, viewPager, view2));
    }

    public static void a(View view, View view2) {
        Utils.b(view2, (Runnable) new m(e(), f(), view2));
    }

    public static float b() {
        float f = f5313b;
        if (f != 0.0f) {
            return f;
        }
        f5313b = f()[2];
        return f5313b;
    }

    private static int c() {
        if (f5315d == 0) {
            f5315d = na.a();
        }
        return f5315d;
    }

    /* access modifiers changed from: private */
    public static float d() {
        return (((float) c()) / ((float) e())) / 2.17f;
    }

    private static int e() {
        if (f5314c == 0) {
            f5314c = na.b();
        }
        return f5314c;
    }

    private static float[] f() {
        return f5312a[com.miui.gamebooster.j.a.a()];
    }
}
