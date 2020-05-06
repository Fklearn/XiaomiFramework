package com.miui.gamebooster.customview;

import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.miui.securitycenter.R;

public class VoiceModeView extends LinearLayout {

    /* renamed from: a  reason: collision with root package name */
    private Context f4158a;

    /* renamed from: b  reason: collision with root package name */
    private ImageView f4159b;

    /* renamed from: c  reason: collision with root package name */
    private TextView f4160c;
    /* access modifiers changed from: private */

    /* renamed from: d  reason: collision with root package name */
    public C0338g f4161d;
    private ValueAnimator e;
    private int f;

    public VoiceModeView(Context context) {
        super(context);
        a(context);
    }

    public VoiceModeView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        a(context);
    }

    public VoiceModeView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        a(context);
    }

    private void a() {
        ValueAnimator valueAnimator = this.e;
        if (valueAnimator != null && valueAnimator.isRunning()) {
            this.e.cancel();
        }
    }

    private void a(Context context) {
        this.f4158a = context;
        View inflate = LayoutInflater.from(this.f4158a).inflate(R.layout.gb_voice_changer_mode_selector_layout, (ViewGroup) null);
        addView(inflate);
        this.f4159b = (ImageView) inflate.findViewById(R.id.mode_icon);
        this.f4160c = (TextView) inflate.findViewById(R.id.mode_title);
        this.f4161d = new C0338g(context);
        this.f4159b.setBackground(this.f4161d);
    }

    private void b() {
        this.e = ValueAnimator.ofInt(new int[]{0, 1, 1, 0});
        this.e.setDuration(500);
        this.e.setRepeatCount(20);
        this.e.setRepeatMode(-1);
        this.e.addUpdateListener(new X(this));
        this.e.addListener(new Y(this));
        this.e.start();
    }

    public int getStatus() {
        return this.f;
    }

    public void setIonBgStatus(int i) {
        this.f = i;
        if (i == 0) {
            a();
            this.f4161d.d(0);
            this.f4159b.setBackground(this.f4161d);
            this.f4160c.setTextColor(this.f4158a.getResources().getColor(R.color.gb_vc_mode_title_color_normal));
        } else if (i == 1) {
            a();
            this.f4161d.d(1);
            this.f4159b.setBackground(this.f4161d);
            this.f4160c.setTextColor(this.f4158a.getResources().getColor(R.color.gb_vc_mode_title_color_selected));
            AlphaAnimation alphaAnimation = new AlphaAnimation(0.0f, 1.0f);
            alphaAnimation.setDuration(300);
            this.f4159b.startAnimation(alphaAnimation);
            this.f4160c.startAnimation(alphaAnimation);
        } else if (i == 2) {
            this.f4161d.d(2);
            b();
            this.f4159b.setBackground(this.f4161d);
        }
    }

    public void setModeTitle(int i) {
        this.f4160c.setText(i);
    }

    public void setNormalIconRes(int i) {
        this.f4161d.a(i);
    }

    public void setProgress(float f2) {
        this.f4161d.a(f2);
    }

    public void setSelectedIconRes(int i) {
        this.f4161d.c(i);
    }
}
