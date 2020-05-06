package com.miui.securityscan.ui.main;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;
import com.miui.common.customview.ScoreTextView;
import com.miui.maml.folme.AnimatedProperty;
import com.miui.securitycenter.R;

public class NativeInterstitialAdLayout extends LinearLayout {

    /* renamed from: a  reason: collision with root package name */
    private BallView f8002a;

    /* renamed from: b  reason: collision with root package name */
    private ImageView f8003b;

    /* renamed from: c  reason: collision with root package name */
    private ScoreTextView f8004c;

    public NativeInterstitialAdLayout(Context context) {
        this(context, (AttributeSet) null);
    }

    public NativeInterstitialAdLayout(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public NativeInterstitialAdLayout(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    public void a(int i) {
        ImageView imageView;
        int i2;
        this.f8004c.setScore(i);
        this.f8002a.a(i);
        this.f8002a.a();
        if (i >= 80) {
            imageView = this.f8003b;
            i2 = R.drawable.shape_interstitial_ad_blue;
        } else {
            imageView = this.f8003b;
            i2 = R.drawable.shape_interstitial_ad_yellow;
        }
        imageView.setImageResource(i2);
        ObjectAnimator.ofFloat(this.f8003b, AnimatedProperty.PROPERTY_NAME_SCALE_X, new float[]{1.0f, 0.507f}).setDuration(500).start();
        ObjectAnimator.ofFloat(this.f8003b, AnimatedProperty.PROPERTY_NAME_SCALE_Y, new float[]{1.0f, 0.507f}).setDuration(500).start();
    }

    /* access modifiers changed from: protected */
    public void onFinishInflate() {
        super.onFinishInflate();
        this.f8002a = (BallView) findViewById(R.id.interstitial_ad_ballview);
        this.f8003b = (ImageView) findViewById(R.id.interstitial_ad_color_circle);
        this.f8004c = (ScoreTextView) findViewById(R.id.interstitial_ad_score);
    }
}
