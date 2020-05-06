package com.miui.firstaidkit.ui;

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

public class FirstAidVideoView extends FrameLayout {

    /* renamed from: a  reason: collision with root package name */
    private ExoTextureView f3979a;

    /* renamed from: b  reason: collision with root package name */
    private SimpleExoPlayer f3980b;
    /* access modifiers changed from: private */

    /* renamed from: c  reason: collision with root package name */
    public ExtractorMediaSource f3981c;
    /* access modifiers changed from: private */

    /* renamed from: d  reason: collision with root package name */
    public ConcatenatingMediaSource f3982d;
    /* access modifiers changed from: private */
    public a e = a.IDLE;

    enum a {
        SCAN,
        IDLE
    }

    public FirstAidVideoView(@NonNull Context context) {
        super(context);
    }

    public FirstAidVideoView(@NonNull Context context, @Nullable AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public FirstAidVideoView(@NonNull Context context, @Nullable AttributeSet attributeSet, int i) {
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
        this.f3980b = ExoPlayerFactory.newSimpleInstance(getContext(), (TrackSelector) new DefaultTrackSelector((TrackSelection.Factory) new AdaptiveTrackSelection.Factory(new DefaultBandwidthMeter())), (LoadControl) new DefaultLoadControl());
        this.f3979a.setPlayer(this.f3980b);
        try {
            this.f3981c = a((int) R.raw.firstaidkit_scan_video);
            this.f3982d = new ConcatenatingMediaSource(this.f3981c, this.f3981c);
            this.f3980b.addListener(new a(this));
            this.f3980b.prepare(this.f3982d);
            a();
        } catch (Exception unused) {
        }
    }

    public void a() {
        if (this.e == a.IDLE) {
            this.f3980b.setPlayWhenReady(true);
            this.e = a.SCAN;
        }
    }

    public void b() {
        if (this.e == a.SCAN) {
            while (this.f3980b.getCurrentWindowIndex() < this.f3982d.getSize() - 1) {
                ConcatenatingMediaSource concatenatingMediaSource = this.f3982d;
                concatenatingMediaSource.removeMediaSource(concatenatingMediaSource.getSize() - 1);
            }
            this.e = a.IDLE;
        }
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        SimpleExoPlayer simpleExoPlayer = this.f3980b;
        if (simpleExoPlayer != null) {
            simpleExoPlayer.stop();
            this.f3980b.release();
        }
    }

    /* access modifiers changed from: protected */
    public void onFinishInflate() {
        super.onFinishInflate();
        this.f3979a = (ExoTextureView) findViewById(R.id.texture_view);
        c();
    }
}
