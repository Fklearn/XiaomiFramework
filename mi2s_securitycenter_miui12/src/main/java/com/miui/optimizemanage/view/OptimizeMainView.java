package com.miui.optimizemanage.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.SimpleExoPlayer;
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
import com.miui.common.ui.ExoTextureView;
import com.miui.securitycenter.R;

public class OptimizeMainView extends FrameLayout {
    /* access modifiers changed from: private */

    /* renamed from: a  reason: collision with root package name */
    public ExoTextureView f6016a;

    /* renamed from: b  reason: collision with root package name */
    private SimpleExoPlayer f6017b;
    /* access modifiers changed from: private */

    /* renamed from: c  reason: collision with root package name */
    public ExtractorMediaSource f6018c;
    /* access modifiers changed from: private */

    /* renamed from: d  reason: collision with root package name */
    public ConcatenatingMediaSource f6019d;
    private int e = 0;
    /* access modifiers changed from: private */
    public a f = a.IDLE;

    enum a {
        SCAN,
        IDLE
    }

    public OptimizeMainView(@NonNull Context context) {
        super(context);
    }

    public OptimizeMainView(@NonNull Context context, @Nullable AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public OptimizeMainView(@NonNull Context context, @Nullable AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    private ExtractorMediaSource a(int i) {
        try {
            DataSpec dataSpec = new DataSpec(RawResourceDataSource.buildRawResourceUri(i));
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

    public static HttpDataSource.Factory b(Context context, DefaultBandwidthMeter defaultBandwidthMeter) {
        return new DefaultHttpDataSourceFactory(Util.getUserAgent(context, "ExoPlayerDemo"), defaultBandwidthMeter);
    }

    private void c() {
        this.f6017b = ExoPlayerFactory.newSimpleInstance(getContext(), (TrackSelector) new DefaultTrackSelector((TrackSelection.Factory) new AdaptiveTrackSelection.Factory(new DefaultBandwidthMeter())), (LoadControl) new DefaultLoadControl());
        this.f6016a.setPlayer(this.f6017b);
        try {
            this.f6018c = a((int) R.raw.optimizemanage_video);
            this.f6019d = new ConcatenatingMediaSource(this.f6018c, this.f6018c);
            this.f6017b.addListener(new a(this));
            this.f6017b.prepare(this.f6019d);
            a();
        } catch (Exception unused) {
        }
    }

    public void a() {
        if (this.f == a.IDLE) {
            this.f6017b.setPlayWhenReady(true);
            this.f = a.SCAN;
        }
    }

    public void b() {
        if (this.f == a.SCAN) {
            while (this.f6017b.getCurrentWindowIndex() < this.f6019d.getSize() - 1) {
                ConcatenatingMediaSource concatenatingMediaSource = this.f6019d;
                concatenatingMediaSource.removeMediaSource(concatenatingMediaSource.getSize() - 1);
            }
            this.f = a.IDLE;
        }
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        SimpleExoPlayer simpleExoPlayer = this.f6017b;
        if (simpleExoPlayer != null) {
            simpleExoPlayer.stop();
            this.f6017b.release();
        }
    }

    /* access modifiers changed from: protected */
    public void onFinishInflate() {
        super.onFinishInflate();
        this.f6016a = (ExoTextureView) findViewById(R.id.texture_view);
        this.f6016a.setRenderHue(200.0f);
        c();
    }

    public void setAnimProgress(float f2) {
        if (f2 >= 180.0f && this.e == 0) {
            this.e = 1;
            ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
            ofFloat.setDuration(200);
            ofFloat.addUpdateListener(new b(this));
            ofFloat.start();
        }
    }
}
