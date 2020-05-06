package com.miui.gamebooster.videobox.adapter;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ListView;
import b.b.c.j.i;
import com.miui.gamebooster.m.C0373d;
import com.miui.gamebooster.m.C0393y;
import com.miui.gamebooster.m.na;
import com.miui.gamebooster.n.c.a;
import com.miui.gamebooster.n.d.b;
import com.miui.gamebooster.n.d.g;
import com.miui.gamebooster.p.r;
import com.miui.gamebooster.videobox.view.DetailSettingsLayout;
import com.miui.gamebooster.videobox.view.SettingsDescLayout;
import com.miui.maml.folme.AnimatedProperty;
import com.miui.securitycenter.R;

public class k implements b.a, DetailSettingsLayout.a, SettingsDescLayout.a {

    /* renamed from: a  reason: collision with root package name */
    private ViewGroup f5177a;

    /* renamed from: b  reason: collision with root package name */
    private ViewGroup f5178b;

    /* renamed from: c  reason: collision with root package name */
    private ListView f5179c;

    /* renamed from: d  reason: collision with root package name */
    private h f5180d;
    private DetailSettingsLayout e;
    private SettingsDescLayout f;
    private r g;
    private AnimatorSet h;

    public k(r rVar) {
        this.g = rVar;
    }

    private void a(Context context) {
        int f2;
        if (context != null && this.f5178b != null && i.e() && (f2 = i.f(context)) > 0) {
            int a2 = na.a(context);
            if (a2 == 90 || a2 == 270) {
                FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) this.f5178b.getLayoutParams();
                int dimensionPixelSize = context.getResources().getDimensionPixelSize(R.dimen.videobox_main_ms) + f2;
                layoutParams.setMarginStart(dimensionPixelSize);
                layoutParams.setMarginEnd(dimensionPixelSize);
                this.f5178b.setLayoutParams(layoutParams);
            }
        }
    }

    private void a(View view, View view2) {
        AnimatorSet animatorSet = this.h;
        if (animatorSet != null) {
            animatorSet.cancel();
        }
        com.miui.gamebooster.n.a.b bVar = new com.miui.gamebooster.n.a.b();
        bVar.a(0.8f);
        bVar.b(0.5f);
        ObjectAnimator ofFloat = ObjectAnimator.ofFloat(this.f5178b, AnimatedProperty.PROPERTY_NAME_SCALE_X, new float[]{1.0f, 0.97f, 1.0f});
        ObjectAnimator ofFloat2 = ObjectAnimator.ofFloat(this.f5178b, AnimatedProperty.PROPERTY_NAME_SCALE_Y, new float[]{1.0f, 0.97f, 1.0f});
        AnimatorSet animatorSet2 = new AnimatorSet();
        animatorSet2.setDuration((long) 1000);
        animatorSet2.setInterpolator(bVar);
        animatorSet2.play(ofFloat).with(ofFloat2);
        AnimatorSet animatorSet3 = new AnimatorSet();
        com.miui.gamebooster.n.a.b bVar2 = new com.miui.gamebooster.n.a.b();
        bVar2.a(0.9f);
        bVar2.b(0.15f);
        ObjectAnimator ofFloat3 = ObjectAnimator.ofFloat(view, AnimatedProperty.PROPERTY_NAME_ALPHA, new float[]{0.0f, 1.0f});
        ObjectAnimator ofFloat4 = ObjectAnimator.ofFloat(view2, AnimatedProperty.PROPERTY_NAME_ALPHA, new float[]{1.0f, 0.0f});
        ofFloat3.setStartDelay(100);
        long j = (long) 800;
        ofFloat3.setDuration(j);
        ofFloat4.setDuration(j);
        animatorSet3.play(ofFloat3).with(ofFloat4);
        animatorSet3.addListener(new i(this, view, view2));
        this.h = new AnimatorSet();
        this.h.play(animatorSet3).with(animatorSet2);
        this.h.start();
    }

    private void a(b bVar) {
        if (bVar instanceof g) {
            a d2 = ((g) bVar).d();
            switch (j.f5176a[d2.ordinal()]) {
                case 1:
                case 2:
                case 3:
                case 4:
                case 5:
                    r rVar = this.g;
                    if (rVar != null) {
                        rVar.k();
                        return;
                    }
                    return;
                case 6:
                case 7:
                case 8:
                    this.e.setFunctionType(d2);
                    a((View) this.e, (View) this.f5179c);
                    return;
                default:
                    return;
            }
        }
    }

    public View a(Context context, boolean z) {
        if (this.f5177a == null) {
            this.f5177a = (ViewGroup) LayoutInflater.from(context).inflate(R.layout.videobox_main_layout, (ViewGroup) null);
            C0393y.a((View) this.f5177a, false);
            this.f5179c = (ListView) this.f5177a.findViewById(R.id.lv_main_container);
            this.e = (DetailSettingsLayout) this.f5177a.findViewById(R.id.second_main_root);
            this.e.setmOnDetailEventListener(this);
            this.f = (SettingsDescLayout) this.f5177a.findViewById(R.id.sdl_desc_root);
            this.f.setOnDescBackListener(this);
            this.f5178b = (ViewGroup) this.f5177a.findViewById(R.id.main_content);
            a(context);
            this.f5180d = new h(context, this);
            if (this.f5180d.a()) {
                ViewGroup.LayoutParams layoutParams = this.f5178b.getLayoutParams();
                layoutParams.height = context.getResources().getDimensionPixelSize(R.dimen.videobox_main_h_style2);
                this.f5178b.setLayoutParams(layoutParams);
            }
            this.f5179c.setAdapter(this.f5180d);
        }
        boolean c2 = na.c();
        FrameLayout.LayoutParams layoutParams2 = (FrameLayout.LayoutParams) this.f5178b.getLayoutParams();
        int i = 8388611;
        if (!z ? !c2 : c2) {
            i = 8388613;
        }
        layoutParams2.gravity = i | 16;
        this.f5178b.setLayoutParams(layoutParams2);
        return this.f5177a;
    }

    public void a(a aVar) {
        h hVar = this.f5180d;
        if (hVar != null) {
            hVar.notifyDataSetChanged();
        }
        a((View) this.f5179c, (View) this.e);
    }

    public void a(b bVar, View view) {
        long j;
        r rVar;
        boolean z = bVar instanceof g;
        if (!z || ((g) bVar).d() != a.DISPLAY_STYLE) {
            rVar = this.g;
            j = 0;
        } else {
            rVar = this.g;
            j = System.currentTimeMillis();
        }
        rVar.a(j);
        a(bVar);
        bVar.a(view);
        if (z) {
            C0373d.a.a(((g) bVar).d());
        }
    }

    public void b(a aVar) {
        a((View) this.e, (View) this.f);
    }

    public void c(a aVar) {
        this.f.setFunctionType(aVar);
        a((View) this.f, (View) this.e);
    }
}
