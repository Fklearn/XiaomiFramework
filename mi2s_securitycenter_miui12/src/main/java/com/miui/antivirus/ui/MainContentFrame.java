package com.miui.antivirus.ui;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import b.b.b.d.m;
import b.b.c.i.b;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.analytics.AnalyticsListener;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ConcatenatingMediaSource;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DataSpec;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.upstream.HttpDataSource;
import com.google.android.exoplayer2.upstream.RawResourceDataSource;
import com.google.android.exoplayer2.upstream.TransferListener;
import com.google.android.exoplayer2.util.Util;
import com.miui.common.customview.ScoreTextView;
import com.miui.common.ui.ExoTextureView;
import com.miui.maml.folme.AnimatedProperty;
import com.miui.securitycenter.R;
import com.miui.superpower.b.k;
import java.util.Locale;

public class MainContentFrame extends RelativeLayout implements View.OnClickListener {

    /* renamed from: a  reason: collision with root package name */
    private b f2926a;

    /* renamed from: b  reason: collision with root package name */
    private RelativeLayout f2927b;
    /* access modifiers changed from: private */

    /* renamed from: c  reason: collision with root package name */
    public ImageView f2928c;
    /* access modifiers changed from: private */

    /* renamed from: d  reason: collision with root package name */
    public ImageView f2929d;
    /* access modifiers changed from: private */
    public ScoreTextView e;
    /* access modifiers changed from: private */
    public TextView f;
    /* access modifiers changed from: private */
    public TextView g;
    /* access modifiers changed from: private */
    public TextView h;
    /* access modifiers changed from: private */
    public ImageView i;
    /* access modifiers changed from: private */
    public ExoTextureView j;
    private SimpleExoPlayer k;
    /* access modifiers changed from: private */
    public ConcatenatingMediaSource l;
    /* access modifiers changed from: private */
    public ExtractorMediaSource m;
    /* access modifiers changed from: private */
    public boolean n;
    private m o;
    private AnalyticsListener p;

    public MainContentFrame(Context context) {
        this(context, (AttributeSet) null);
    }

    public MainContentFrame(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public MainContentFrame(Context context, AttributeSet attributeSet, int i2) {
        super(context, attributeSet, i2);
        this.o = m.SAFE;
        this.p = new k(this);
        c();
    }

    private ExtractorMediaSource a(int i2) {
        try {
            DataSpec dataSpec = new DataSpec(RawResourceDataSource.buildRawResourceUri(i2));
            RawResourceDataSource rawResourceDataSource = new RawResourceDataSource(getContext());
            rawResourceDataSource.open(dataSpec);
            return new ExtractorMediaSource(rawResourceDataSource.getUri(), a(getContext(), true), new DefaultExtractorsFactory(), (Handler) null, (ExtractorMediaSource.EventListener) null);
        } catch (Exception unused) {
            return null;
        }
    }

    public static DataSource.Factory a(Context context, DefaultBandwidthMeter defaultBandwidthMeter) {
        return new DefaultDataSourceFactory(context, (TransferListener<? super DataSource>) defaultBandwidthMeter, (DataSource.Factory) b(context, defaultBandwidthMeter));
    }

    public static DataSource.Factory a(Context context, boolean z) {
        return a(context, z ? new DefaultBandwidthMeter() : null);
    }

    /* access modifiers changed from: private */
    public void a(boolean z, int i2, boolean z2) {
        TextView textView;
        String quantityString;
        this.n = true;
        if (z2) {
            textView = this.g;
            quantityString = getContext().getString(R.string.hints_scan_result_no_finish);
        } else if (z) {
            textView = this.g;
            quantityString = getContext().getString(R.string.hints_scan_result_phone_safe);
        } else {
            textView = this.g;
            quantityString = getResources().getQuantityString(R.plurals.hints_scan_danger_result_with_number, i2, new Object[]{Integer.valueOf(i2)});
        }
        textView.setText(quantityString);
        TextView textView2 = this.g;
        textView2.setContentDescription(textView2.getText());
        if (z2) {
            this.i.setImageResource(z ? R.drawable.v_scan_again_arrow_safe : R.drawable.v_scan_again_arrow_risk);
            this.i.setContentDescription(getContext().getString(R.string.hints_scan_result_no_finish));
        }
        if (z) {
            this.f2928c.setImageResource(z2 ? R.drawable.scan_result_interrupted : R.drawable.scan_result_safe);
            this.g.setTextColor(getResources().getColor(R.color.v_activity_main_title_safe));
        } else {
            this.g.setTextColor(getResources().getColor(R.color.v_activity_main_title_risky));
            this.f2928c.setImageResource(R.drawable.scan_result_risk);
        }
        ObjectAnimator a2 = a(this.f2928c, 0.0f, 1.0f, 2, 0, 1000);
        long j2 = (long) 500;
        ObjectAnimator a3 = a(this.g, 0.0f, 1.0f, 2, 0, j2);
        long j3 = (long) 500;
        a3.setStartDelay(j3);
        ObjectAnimator a4 = a(this.i, 0.0f, 1.0f, 2, 0, j2);
        a4.setStartDelay(j3);
        ObjectAnimator a5 = a(this.f2929d, 0.0f, 1.0f, 2, 0, 1000);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(new Animator[]{a2, a3, a4, a5});
        animatorSet.start();
    }

    public static HttpDataSource.Factory b(Context context, DefaultBandwidthMeter defaultBandwidthMeter) {
        return new DefaultHttpDataSourceFactory(Util.getUserAgent(context, "ExoPlayerDemo"), defaultBandwidthMeter);
    }

    private void c() {
        this.k = ExoPlayerFactory.newSimpleInstance(getContext(), (TrackSelector) new DefaultTrackSelector((TrackSelection.Factory) new AdaptiveTrackSelection.Factory(new DefaultBandwidthMeter())), (LoadControl) new DefaultLoadControl());
    }

    private void d() {
        try {
            ExtractorMediaSource a2 = a((int) R.raw.antivirus_animation1);
            this.m = a((int) R.raw.antivirus_animation2);
            this.l = new ConcatenatingMediaSource(a2, this.m);
            this.k.setPlayWhenReady(true);
            this.k.prepare(this.l);
            this.k.addAnalyticsListener(this.p);
        } catch (Exception e2) {
            Log.e("TAGTAG", "play video exception: ", e2);
        }
    }

    public ObjectAnimator a(View view, float f2, float f3, int i2, int i3, long j2) {
        ObjectAnimator ofFloat = ObjectAnimator.ofFloat(view, AnimatedProperty.PROPERTY_NAME_ALPHA, new float[]{f2, f3});
        ofFloat.setRepeatMode(i2);
        ofFloat.setRepeatCount(i3);
        ofFloat.setDuration(j2);
        return ofFloat;
    }

    public void a() {
        this.f2928c.setVisibility(8);
        this.f2929d.setVisibility(8);
        this.e.setVisibility(0);
        this.f.setVisibility(0);
    }

    public void a(float f2) {
        this.j.setScaleX(f2);
        this.j.setScaleY(f2);
    }

    public void a(int i2, Boolean bool, boolean z) {
        boolean z2 = i2 == 0;
        if (z) {
            ObjectAnimator a2 = a(this.e, 1.0f, 0.0f, 2, 0, 500);
            ObjectAnimator a3 = a(this.f, 1.0f, 0.0f, 2, 0, 500);
            ObjectAnimator a4 = a(this.h, 1.0f, 0.0f, 2, 0, 500);
            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.playTogether(new Animator[]{a2, a3, a4});
            animatorSet.addListener(new i(this, bool, z2, i2));
            animatorSet.start();
            return;
        }
        a(z2, i2, bool.booleanValue());
    }

    public void a(m mVar) {
        if (mVar != this.o) {
            int i2 = l.f2972a[mVar.ordinal()];
            if (i2 != 1) {
                if (i2 == 2 || i2 == 3) {
                    ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
                    ofFloat.setDuration(500);
                    ofFloat.addUpdateListener(new j(this));
                    ofFloat.start();
                }
            }
            this.o = mVar;
        }
    }

    public void b() {
    }

    public float getVideoScale() {
        return this.j.getScaleX();
    }

    public void onClick(View view) {
        if (view.getId() == R.id.result_text || view.getId() == R.id.arrow) {
            this.f2926a.sendEmptyMessage(1037);
        }
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        SimpleExoPlayer simpleExoPlayer = this.k;
        if (simpleExoPlayer != null) {
            simpleExoPlayer.stop();
            this.k.release();
            this.k = null;
        }
    }

    /* access modifiers changed from: protected */
    public void onFinishInflate() {
        super.onFinishInflate();
        this.e = (ScoreTextView) findViewById(R.id.number);
        this.e.setTypeface(Typeface.createFromAsset(getContext().getAssets(), "fonts/Mitype-DemiBold.otf"));
        this.e.setNumber(0);
        this.f2928c = (ImageView) findViewById(R.id.result_icon);
        this.f2929d = (ImageView) findViewById(R.id.result_icon_shadow);
        this.j = (ExoTextureView) findViewById(R.id.animation_view);
        this.f2927b = (RelativeLayout) findViewById(R.id.v_header_layout);
        this.f = (TextView) findViewById(R.id.scan_percent);
        if ("tr".equals(Locale.getDefault().getLanguage())) {
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) this.f.getLayoutParams();
            layoutParams.removeRule(17);
            layoutParams.addRule(16, R.id.number);
            layoutParams.setMarginEnd(layoutParams.getMarginStart());
            layoutParams.setMarginStart(0);
            this.f.setLayoutParams(layoutParams);
        }
        this.g = (TextView) findViewById(R.id.result_text);
        this.g.setOnClickListener(this);
        if (k.a() > 8) {
            this.g.setTypeface(Typeface.create("mipro", 1));
        }
        this.h = (TextView) findViewById(R.id.result_summary);
        this.i = (ImageView) findViewById(R.id.arrow);
        this.i.setOnClickListener(this);
        a();
        this.j.setPlayer(this.k);
        d();
    }

    public void setEventHandler(b bVar) {
        this.f2926a = bVar;
    }

    public void setHeaderLayoutAlpha(float f2) {
        this.f2927b.setAlpha(f2);
    }

    public void setProgressText(CharSequence charSequence) {
        this.e.setText(charSequence);
    }

    public void setScanResult(CharSequence charSequence) {
    }

    public void setSummaryText(CharSequence charSequence) {
        this.h.setText(charSequence);
    }
}
