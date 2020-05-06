package com.miui.securityscan.ui.main;

import android.animation.ValueAnimator;
import android.content.Context;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ConcatenatingMediaSource;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
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

public class MainVideoView extends FrameLayout {
    /* access modifiers changed from: private */

    /* renamed from: a  reason: collision with root package name */
    public ExoTextureView f7995a;

    /* renamed from: b  reason: collision with root package name */
    private View f7996b;

    /* renamed from: c  reason: collision with root package name */
    private SimpleExoPlayer f7997c;

    /* renamed from: d  reason: collision with root package name */
    private ExtractorMediaSource f7998d;
    private ConcatenatingMediaSource e;
    private a f = a.IDLE;

    enum a {
        SCAN,
        IDLE
    }

    public MainVideoView(@NonNull Context context) {
        super(context);
    }

    public MainVideoView(@NonNull Context context, @Nullable AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public MainVideoView(@NonNull Context context, @Nullable AttributeSet attributeSet, int i) {
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

    private void g() {
        this.f7997c = ExoPlayerFactory.newSimpleInstance(getContext(), (TrackSelector) new DefaultTrackSelector((TrackSelection.Factory) new AdaptiveTrackSelection.Factory(new DefaultBandwidthMeter())), (LoadControl) new DefaultLoadControl());
        this.f7995a.setPlayer(this.f7997c);
        this.f7998d = a((int) R.raw.security_scan_video);
        this.e = new ConcatenatingMediaSource();
    }

    public void a() {
        if (this.f == a.SCAN) {
            ConcatenatingMediaSource concatenatingMediaSource = this.e;
            concatenatingMediaSource.addMediaSource(concatenatingMediaSource.getSize(), (MediaSource) this.f7998d);
        }
    }

    public void a(float f2, float f3) {
        ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{f2, f3});
        ofFloat.setDuration(200);
        ofFloat.addUpdateListener(new a(this));
        ofFloat.start();
    }

    public void a(float f2, int i) {
        int i2;
        View view;
        if (i == 4) {
            if (f2 == 1.0f) {
                view = this.f7996b;
                i2 = R.drawable.securityscan_last_frame_red;
            } else {
                view = this.f7996b;
                i2 = R.drawable.securityscan_last_frame_blue;
            }
        } else if (f2 == 1.0f) {
            view = this.f7996b;
            i2 = R.drawable.securityscan_first_frame_red;
        } else {
            view = this.f7996b;
            i2 = R.drawable.securityscan_first_frame_blue;
        }
        view.setBackgroundResource(i2);
    }

    public void a(Player.EventListener eventListener) {
        SimpleExoPlayer simpleExoPlayer = this.f7997c;
        if (simpleExoPlayer != null) {
            simpleExoPlayer.removeListener(eventListener);
        }
    }

    public void b() {
        this.f7995a.a();
    }

    public void c() {
        this.f7995a.setAlpha(1.0f);
        this.f7996b.setAlpha(0.0f);
    }

    public void d() {
        this.f7996b.setAlpha(1.0f);
        this.f7995a.setAlpha(0.0f);
    }

    public void e() {
        if (this.f == a.IDLE) {
            try {
                this.e.addMediaSource(this.f7998d);
                this.e.addMediaSource(this.f7998d);
                this.f7997c.prepare(this.e);
                this.f7997c.setPlayWhenReady(true);
                this.f = a.SCAN;
            } catch (Exception e2) {
                Log.e("MainVideoView", "start play error", e2);
            }
        }
    }

    public void f() {
        if (this.f == a.SCAN) {
            while (this.f7997c.getCurrentWindowIndex() < this.e.getSize() - 1) {
                ConcatenatingMediaSource concatenatingMediaSource = this.e;
                concatenatingMediaSource.removeMediaSource(concatenatingMediaSource.getSize() - 1);
            }
            this.f = a.IDLE;
        }
    }

    public long getCurrentPosition() {
        return this.f7997c.getCurrentPosition();
    }

    public long getDuration() {
        return this.f7997c.getDuration();
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        SimpleExoPlayer simpleExoPlayer = this.f7997c;
        if (simpleExoPlayer != null) {
            simpleExoPlayer.stop();
            this.f7997c.release();
        }
    }

    /* access modifiers changed from: protected */
    public void onFinishInflate() {
        super.onFinishInflate();
        this.f7995a = (ExoTextureView) findViewById(R.id.texture_view);
        this.f7996b = findViewById(R.id.bg_view);
        this.f7995a.setAlpha(0.0f);
        g();
    }

    public void setEventListener(Player.EventListener eventListener) {
        this.f7997c.addListener(eventListener);
    }

    public void setPlaySpeed(float f2) {
        if (this.f7997c != null) {
            this.f7997c.setPlaybackParameters(new PlaybackParameters(f2));
        }
    }

    public void setRenderState(float f2) {
        this.f7995a.setRenderState(f2);
    }
}
