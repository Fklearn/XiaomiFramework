package com.miui.securityscan.h.a;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.text.Html;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.miui.securitycenter.R;

public class c extends LinearLayout implements Animation.AnimationListener {

    /* renamed from: a  reason: collision with root package name */
    private TextView f7717a;

    /* renamed from: b  reason: collision with root package name */
    private LinearLayout f7718b;

    /* renamed from: c  reason: collision with root package name */
    private AnimationSet f7719c = null;

    /* renamed from: d  reason: collision with root package name */
    private AlphaAnimation f7720d = null;
    /* access modifiers changed from: private */
    public a e;
    @SuppressLint({"HandlerLeak"})
    private Handler f = new b(this);

    public interface a {
        void a();
    }

    public c(Context context) {
        super(context);
        a();
        b();
    }

    private void a() {
        this.f7720d = new AlphaAnimation(1.0f, 0.0f);
        this.f7720d.setDuration(500);
        this.f7720d.setRepeatCount(0);
        this.f7720d.setFillAfter(true);
        this.f7720d.setInterpolator(new LinearInterpolator());
        this.f7720d.setAnimationListener(this);
        TranslateAnimation translateAnimation = new TranslateAnimation(1, 0.0f, 1, 0.0f, 1, 0.0f, 1, -1.0f);
        translateAnimation.setRepeatCount(0);
        translateAnimation.setFillAfter(true);
        this.f7719c = new AnimationSet(true);
        this.f7719c.setFillAfter(true);
        this.f7719c.setDuration(AdaptiveTrackSelection.DEFAULT_MIN_TIME_BETWEEN_BUFFER_REEVALUTATION_MS);
        this.f7719c.addAnimation(translateAnimation);
        this.f7719c.addAnimation(this.f7720d);
        this.f7719c.setAnimationListener(this);
        this.f7719c.setInterpolator(new LinearInterpolator());
        this.f7719c.setRepeatCount(0);
    }

    private void b() {
        setOrientation(1);
        setGravity(80);
        LinearLayout linearLayout = new LinearLayout(getContext());
        linearLayout.setOrientation(1);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(-1, 800);
        linearLayout.setGravity(80);
        layoutParams.bottomMargin = 100;
        addView(linearLayout, layoutParams);
        this.f7718b = new LinearLayout(getContext());
        this.f7718b.setOrientation(1);
        this.f7718b.setBackgroundResource(R.drawable.integral_pop_bg);
        this.f7718b.setGravity(16);
        LinearLayout.LayoutParams layoutParams2 = new LinearLayout.LayoutParams(-2, -2);
        layoutParams2.gravity = 1;
        linearLayout.addView(this.f7718b, layoutParams2);
        LinearLayout linearLayout2 = new LinearLayout(getContext());
        linearLayout2.setOrientation(0);
        LinearLayout.LayoutParams layoutParams3 = new LinearLayout.LayoutParams(-2, -2);
        layoutParams3.gravity = 1;
        this.f7718b.addView(linearLayout2, layoutParams3);
        this.f7717a = new TextView(getContext());
        this.f7717a.setGravity(17);
        this.f7717a.setShadowLayer(0.5f, 0.5f, 0.5f, -1438032317);
        this.f7717a.setTextColor(-1);
        linearLayout2.addView(this.f7717a, new LinearLayout.LayoutParams(-2, -2));
        this.f.sendEmptyMessageDelayed(1, 1000);
    }

    /* access modifiers changed from: private */
    public void c() {
        this.f7718b.setAnimation(this.f7719c);
        this.f7719c.start();
        this.f7718b.invalidate();
    }

    public void onAnimationEnd(Animation animation) {
        this.f.removeMessages(2);
        this.f.sendEmptyMessageDelayed(2, 500);
    }

    public void onAnimationRepeat(Animation animation) {
    }

    public void onAnimationStart(Animation animation) {
    }

    public void setAnimationListner(a aVar) {
        this.e = aVar;
    }

    public void setIntegral(int i) {
        int abs = Math.abs(i);
        String quantityString = getResources().getQuantityString(R.plurals.optimize_result_button_add_score, abs, new Object[]{Integer.valueOf(abs)});
        TextView textView = this.f7717a;
        textView.setText(Html.fromHtml("<font><b><big>" + quantityString + "</big></b></font>"));
    }
}
