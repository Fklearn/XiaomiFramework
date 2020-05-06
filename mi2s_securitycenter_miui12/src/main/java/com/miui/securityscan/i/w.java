package com.miui.securityscan.i;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.content.Context;
import android.content.res.Resources;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.PathInterpolator;
import android.widget.TextView;
import b.b.c.a.c;
import com.miui.common.customview.OverScrollLayout;
import com.miui.maml.folme.AnimatedProperty;
import com.miui.securitycenter.R;

public class w {

    /* renamed from: a  reason: collision with root package name */
    private static int f7751a = -1;

    public static void a() {
        f7751a = -1;
    }

    public static void a(Context context, int i, TextView textView) {
        int i2;
        Resources resources;
        if (context != null) {
            if (i >= 80) {
                resources = context.getResources();
                i2 = R.color.main_btn_action_bule;
            } else {
                resources = context.getResources();
                i2 = R.color.main_btn_action_yellow;
            }
            textView.setTextColor(resources.getColor(i2));
        }
    }

    public static void a(Context context, View view, View view2) {
        view2.setVisibility(0);
        ObjectAnimator ofFloat = ObjectAnimator.ofFloat(view, AnimatedProperty.PROPERTY_NAME_ALPHA, new float[]{1.0f, 0.0f});
        ofFloat.setDuration(400);
        ObjectAnimator ofFloat2 = ObjectAnimator.ofFloat(view2, "translationY", new float[]{view2.getHeight() > 0 ? (float) view2.getHeight() : ((float) view.getHeight()) * 1.5f, 0.0f});
        ofFloat2.setDuration(400);
        ObjectAnimator ofFloat3 = ObjectAnimator.ofFloat(view, AnimatedProperty.PROPERTY_NAME_SCALE_X, new float[]{0.95f});
        ofFloat3.setDuration(400);
        ObjectAnimator ofFloat4 = ObjectAnimator.ofFloat(view, AnimatedProperty.PROPERTY_NAME_SCALE_Y, new float[]{0.95f});
        ofFloat4.setDuration(400);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setInterpolator(new PathInterpolator(0.6f, 0.35f, 0.19f, 1.0f));
        animatorSet.playTogether(new Animator[]{ofFloat, ofFloat2, ofFloat3, ofFloat4});
        animatorSet.addListener(new u(view));
        animatorSet.start();
    }

    public static void a(Context context, View view, View view2, boolean z) {
        if ((view.getVisibility() == 0 || view2.getVisibility() != 0) && context != null) {
            if (view instanceof OverScrollLayout) {
                ((OverScrollLayout) view).setIntercept(true);
            }
            if (view2 instanceof OverScrollLayout) {
                ((OverScrollLayout) view2).setIntercept(false);
            }
            view2.setAlpha(1.0f);
            if (z) {
                c cVar = new c(context);
                cVar.a(view, AnimationUtils.loadAnimation(context, R.anim.collapse_from_top), new v(view));
                cVar.a(view2, AnimationUtils.loadAnimation(context, R.anim.expand_to_top), (Animation.AnimationListener) null);
                cVar.a();
                return;
            }
            view2.setVisibility(0);
            view.setVisibility(8);
        }
    }

    public static void a(View view) {
        int id = view.getId();
        if (id == -1 || id != f7751a) {
            f7751a = id;
            view.bringToFront();
            ViewParent parent = view.getParent();
            if (parent != null && (parent instanceof View)) {
                ((View) parent).invalidate();
            }
        }
    }

    public static void a(View view, long j) {
        ObjectAnimator ofFloat = ObjectAnimator.ofFloat(view, AnimatedProperty.PROPERTY_NAME_ALPHA, new float[]{1.0f, 0.0f});
        ofFloat.setInterpolator(new PathInterpolator(0.6f, 0.35f, 0.19f, 1.0f));
        ofFloat.addListener(new r(view));
        ofFloat.setDuration(j);
        ofFloat.start();
    }

    public static void a(View view, long j, long j2) {
        view.setVisibility(0);
        ObjectAnimator ofFloat = ObjectAnimator.ofFloat(view, AnimatedProperty.PROPERTY_NAME_ALPHA, new float[]{0.0f, 1.0f});
        ofFloat.setInterpolator(new PathInterpolator(0.6f, 0.35f, 0.19f, 1.0f));
        ofFloat.setDuration(j);
        ofFloat.setStartDelay(j2);
        ofFloat.start();
    }

    public static void a(View view, long j, TimeInterpolator timeInterpolator) {
        view.setClickable(false);
        ObjectAnimator ofFloat = ObjectAnimator.ofFloat(view, AnimatedProperty.PROPERTY_NAME_ALPHA, new float[]{1.0f, 0.0f});
        if (timeInterpolator != null) {
            ofFloat.setInterpolator(timeInterpolator);
        }
        ofFloat.addListener(new t(view));
        ofFloat.setDuration(j);
        ofFloat.start();
    }

    public static void a(ViewGroup viewGroup, long j, float f, float f2, TimeInterpolator timeInterpolator) {
        ObjectAnimator ofFloat = ObjectAnimator.ofFloat(viewGroup, "translationY", new float[]{f, f2});
        if (timeInterpolator != null) {
            ofFloat.setInterpolator(timeInterpolator);
        }
        ofFloat.setDuration(j);
        ofFloat.start();
    }

    public static void b(Context context, int i, TextView textView) {
        int i2;
        Resources resources;
        if (context != null) {
            if (i >= 80) {
                resources = context.getResources();
                i2 = R.color.status_bar_bottom_textcolor_blue;
            } else {
                resources = context.getResources();
                i2 = R.color.status_bar_bottom_textcolor_yellow;
            }
            textView.setTextColor(resources.getColor(i2));
        }
    }

    public static void b(View view, long j, TimeInterpolator timeInterpolator) {
        view.setVisibility(0);
        ObjectAnimator ofFloat = ObjectAnimator.ofFloat(view, AnimatedProperty.PROPERTY_NAME_ALPHA, new float[]{0.0f, 1.0f});
        if (timeInterpolator != null) {
            ofFloat.setInterpolator(timeInterpolator);
        }
        ofFloat.addListener(new s(view));
        ofFloat.setDuration(j);
        ofFloat.start();
    }
}
