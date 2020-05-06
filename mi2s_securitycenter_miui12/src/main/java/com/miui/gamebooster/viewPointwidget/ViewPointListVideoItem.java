package com.miui.gamebooster.viewPointwidget;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import b.b.c.h.f;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.PlaybackPreparer;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.BehindLiveWindowException;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.MediaSourceEventListener;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.MappingTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.PlayerControlView;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.upstream.HttpDataSource;
import com.google.android.exoplayer2.upstream.TransferListener;
import com.google.android.exoplayer2.util.MimeTypes;
import com.google.android.exoplayer2.util.Util;
import com.miui.gamebooster.customview.DataNetVideoPlayBtn;
import com.miui.gamebooster.gamead.A;
import com.miui.gamebooster.gamead.ViewPointVideoInfo;
import com.miui.gamebooster.m.ha;
import com.miui.gamebooster.m.ia;
import com.miui.gamebooster.model.q;
import com.miui.maml.folme.AnimatedProperty;
import com.miui.securitycenter.Application;
import com.miui.securitycenter.R;
import java.lang.ref.WeakReference;

public class ViewPointListVideoItem extends RelativeLayout implements View.OnClickListener, PlaybackPreparer, PlayerControlView.VisibilityListener, b {
    /* access modifiers changed from: private */

    /* renamed from: a  reason: collision with root package name */
    public static final String f5334a = "com.miui.gamebooster.viewPointwidget.ViewPointListVideoItem";

    /* renamed from: b  reason: collision with root package name */
    private static final DefaultBandwidthMeter f5335b = new DefaultBandwidthMeter();
    private Uri A;
    private Activity B;
    private b.b.c.i.b C;
    private Context D;
    protected AudioManager E;
    /* access modifiers changed from: private */
    public WeakReference<AudioManager.OnAudioFocusChangeListener> F;
    private AudioManager.OnAudioFocusChangeListener G = new g(this);
    private IntentFilter H;
    private a I;

    /* renamed from: c  reason: collision with root package name */
    protected TextView f5336c;

    /* renamed from: d  reason: collision with root package name */
    protected TextView f5337d;
    protected RelativeLayout e;
    protected ImageView f;
    protected ViewPointVideoInfo g;
    protected A h;
    protected ShowTextCountTextView i;
    protected DataNetVideoPlayBtn j;
    protected ObjectAnimator k;
    protected ImageView l;
    protected ImageView m;
    protected RelativeLayout n;
    protected int o;
    protected int p;
    protected float q = 1.0f;
    private PlayerView r;
    private DataSource.Factory s;
    /* access modifiers changed from: private */
    public SimpleExoPlayer t;
    /* access modifiers changed from: private */
    public DefaultTrackSelector u;
    private DefaultTrackSelector.Parameters v;
    /* access modifiers changed from: private */
    public boolean w;
    /* access modifiers changed from: private */
    public TrackGroupArray x;
    protected boolean y;
    private boolean z = false;

    class a extends BroadcastReceiver {
        a() {
        }

        public void onReceive(Context context, Intent intent) {
            NetworkInfo activeNetworkInfo = ((ConnectivityManager) context.getSystemService("connectivity")).getActiveNetworkInfo();
            if (activeNetworkInfo != null && activeNetworkInfo.isAvailable()) {
                int type = activeNetworkInfo.getType();
                if (type == 0) {
                    ViewPointListVideoItem.this.j.a();
                    ViewPointListVideoItem.this.g();
                } else if (type == 1) {
                    ViewPointListVideoItem.this.e();
                }
            }
        }
    }

    private class b extends Player.DefaultEventListener {
        private b() {
        }

        /* synthetic */ b(ViewPointListVideoItem viewPointListVideoItem, g gVar) {
            this();
        }

        public void onPlayerError(ExoPlaybackException exoPlaybackException) {
            String c2 = ViewPointListVideoItem.f5334a;
            Log.e(c2, "ExoPlaybackException: " + exoPlaybackException.type);
            int i = exoPlaybackException.type;
            if (i == 0) {
                ViewPointListVideoItem.this.t.setPlayWhenReady(false);
            } else if (i != 1) {
            }
            boolean unused = ViewPointListVideoItem.this.w = true;
            if (ViewPointListVideoItem.b(exoPlaybackException)) {
                ViewPointListVideoItem.this.i();
                ViewPointListVideoItem.this.d();
                return;
            }
            ViewPointListVideoItem.this.m();
        }

        public void onPlayerStateChanged(boolean z, int i) {
            if (z) {
                if (i == 1) {
                    Log.e(ViewPointListVideoItem.f5334a, "onInfo: STATE_IDLE");
                } else if (i == 2) {
                    Log.e(ViewPointListVideoItem.f5334a, "onInfo: STATE_BUFFERING");
                    ViewPointListVideoItem.this.setLoadingView(0);
                    ViewPointListVideoItem.this.setBannerVisibility(0);
                    ViewPointListVideoItem.this.setPlayBtnVisibility(8);
                } else if (i == 3) {
                    Log.e(ViewPointListVideoItem.f5334a, "onInfo: STATE_READY");
                    ViewPointListVideoItem.this.setBannerVisibility(8);
                    ViewPointListVideoItem viewPointListVideoItem = ViewPointListVideoItem.this;
                    viewPointListVideoItem.setSoundsOn(viewPointListVideoItem.y);
                } else if (i == 4) {
                    Log.e(ViewPointListVideoItem.f5334a, "onInfo: STATE_ENDED");
                    if (f.l(Application.d())) {
                        ViewPointListVideoItem.this.setBannerVisibility(0);
                        ViewPointListVideoItem.this.setPlayBtnVisibility(8);
                        ViewPointListVideoItem.this.setLoadingView(8);
                        ViewPointListVideoItem.this.f5337d.setVisibility(0);
                        ViewPointListVideoItem.this.t.seekToDefaultPosition();
                        ViewPointListVideoItem.this.t.setPlayWhenReady(false);
                        return;
                    }
                    ViewPointListVideoItem.this.setBannerVisibility(0);
                    ViewPointListVideoItem.this.j.a();
                }
            }
        }

        public void onPositionDiscontinuity(int i) {
            if (ViewPointListVideoItem.this.t.getPlaybackError() != null) {
                ViewPointListVideoItem.this.m();
            }
        }

        public void onTracksChanged(TrackGroupArray trackGroupArray, TrackSelectionArray trackSelectionArray) {
            if (trackGroupArray != ViewPointListVideoItem.this.x) {
                MappingTrackSelector.MappedTrackInfo currentMappedTrackInfo = ViewPointListVideoItem.this.u.getCurrentMappedTrackInfo();
                if (currentMappedTrackInfo != null) {
                    currentMappedTrackInfo.getTrackTypeRendererSupport(2);
                    currentMappedTrackInfo.getTrackTypeRendererSupport(1);
                }
                TrackGroupArray unused = ViewPointListVideoItem.this.x = trackGroupArray;
            }
        }
    }

    public ViewPointListVideoItem(@NonNull Context context) {
        super(context);
        this.D = context;
    }

    public ViewPointListVideoItem(@NonNull Context context, @Nullable AttributeSet attributeSet) {
        super(context, attributeSet);
        this.D = context;
    }

    private MediaSource a(Uri uri, String str) {
        String str2;
        if (!TextUtils.isEmpty(str)) {
            str2 = "." + str;
        } else {
            str2 = uri.getLastPathSegment();
        }
        int inferContentType = Util.inferContentType(str2);
        if (inferContentType == 2) {
            return new HlsMediaSource(uri, this.s, (Handler) null, (MediaSourceEventListener) null);
        }
        if (inferContentType == 3) {
            return new ExtractorMediaSource(uri, this.s, new DefaultExtractorsFactory(), this.C, (ExtractorMediaSource.EventListener) null);
        }
        throw new IllegalStateException("Unsupported type: " + inferContentType);
    }

    private DataSource.Factory a(DefaultBandwidthMeter defaultBandwidthMeter) {
        return new DefaultDataSourceFactory(getContext(), (TransferListener<? super DataSource>) defaultBandwidthMeter, (DataSource.Factory) b(defaultBandwidthMeter));
    }

    private DataSource.Factory a(boolean z2) {
        return a(z2 ? f5335b : null);
    }

    private HttpDataSource.Factory b(DefaultBandwidthMeter defaultBandwidthMeter) {
        return new DefaultHttpDataSourceFactory(Util.getUserAgent(getContext(), "ExoPlayerDemo"), defaultBandwidthMeter);
    }

    /* access modifiers changed from: private */
    public static boolean b(ExoPlaybackException exoPlaybackException) {
        if (exoPlaybackException.type != 0) {
            return false;
        }
        for (Throwable sourceException = exoPlaybackException.getSourceException(); sourceException != null; sourceException = sourceException.getCause()) {
            if (sourceException instanceof BehindLiveWindowException) {
                return true;
            }
        }
        return false;
    }

    private boolean h() {
        if (this.E == null) {
            this.E = (AudioManager) Application.d().getSystemService(MimeTypes.BASE_TYPE_AUDIO);
        }
        AudioManager audioManager = this.E;
        return audioManager != null && audioManager.abandonAudioFocus(this.G) == 1;
    }

    /* access modifiers changed from: private */
    public void i() {
        q a2 = ia.a().a(this.g.getUrl());
        a2.f4579a = true;
        a2.f4580b = -1;
        a2.f4581c = C.TIME_UNSET;
        ia.a().a(this.g.getUrl(), a2);
    }

    private void j() {
        Log.e(f5334a, "loadUrl: ");
        this.A = Uri.parse(ha.a(this.g.getUrl()));
        if (!Util.maybeRequestReadExternalStoragePermission(this.B, this.A)) {
            MediaSource a2 = a(this.A, (String) null);
            q a3 = ia.a().a(this.g.getUrl());
            boolean z2 = a3.f4580b != -1;
            if (z2) {
                this.t.seekTo(a3.f4580b, a3.f4581c);
            }
            this.t.prepare(a2, !z2, false);
            this.w = false;
            this.z = true;
            this.y = com.miui.gamebooster.c.a.c();
            setSoundsOn(this.y);
        }
    }

    private boolean k() {
        if (this.E == null) {
            this.E = (AudioManager) Application.d().getSystemService(MimeTypes.BASE_TYPE_AUDIO);
        }
        AudioManager audioManager = this.E;
        return audioManager != null && this.q > 0.0f && audioManager.requestAudioFocus(this.G, 3, 1) == 1;
    }

    private void l() {
        if (f.j(this.D)) {
            if (this.t == null || !f.l(Application.d())) {
                this.j.a();
            } else {
                this.t.seekToDefaultPosition();
                this.t.setPlayWhenReady(true);
            }
            this.f5337d.setVisibility(8);
            return;
        }
        Context context = this.D;
        Toast.makeText(context, context.getResources().getString(R.string.game_video_network_eror), 0).show();
    }

    /* access modifiers changed from: private */
    public void m() {
        if (this.t != null) {
            q a2 = ia.a().a(this.g.getUrl());
            a2.f4579a = this.t.getPlayWhenReady();
            a2.f4580b = this.t.getCurrentWindowIndex();
            a2.f4581c = this.t.isCurrentWindowSeekable() ? Math.max(0, this.t.getCurrentPosition()) : C.TIME_UNSET;
            ia.a().a(this.g.getUrl(), a2);
        }
    }

    private void n() {
        DefaultTrackSelector defaultTrackSelector = this.u;
        if (defaultTrackSelector != null) {
            this.v = defaultTrackSelector.getParameters();
        }
    }

    /* access modifiers changed from: private */
    public void setLoadingView(int i2) {
        ViewPointVideoInfo viewPointVideoInfo = this.g;
        if (viewPointVideoInfo == null || TextUtils.isEmpty(viewPointVideoInfo.getUrl())) {
            ObjectAnimator objectAnimator = this.k;
            if (objectAnimator != null && objectAnimator.isRunning()) {
                this.k.cancel();
            }
            this.l.setVisibility(8);
        }
        if (i2 == 0) {
            ObjectAnimator objectAnimator2 = this.k;
            if (objectAnimator2 != null && !objectAnimator2.isRunning()) {
                this.k.start();
            }
            this.j.setVisibility(8);
            this.l.setVisibility(i2);
            return;
        }
        this.l.setVisibility(i2);
        ObjectAnimator objectAnimator3 = this.k;
        if (objectAnimator3 != null && objectAnimator3.isRunning()) {
            this.k.cancel();
        }
    }

    public void a() {
        if (Build.VERSION.SDK_INT <= 23 || this.t == null) {
            e();
        }
    }

    public void b() {
        if (Build.VERSION.SDK_INT > 23) {
            e();
        }
    }

    public void d() {
        if (!this.z) {
            Log.e(f5334a, "initializePlayer: ");
            boolean z2 = this.t == null;
            if (z2) {
                Log.e(f5334a, "initializePlayer: needNewPlayer");
                this.u = new DefaultTrackSelector((TrackSelection.Factory) new AdaptiveTrackSelection.Factory(f5335b));
                this.u.setParameters(this.v);
                this.x = null;
                this.t = ExoPlayerFactory.newSimpleInstance(getContext(), (TrackSelector) this.u);
                this.t.addListener(new b(this, (g) null));
                this.r.setPlayer(this.t);
                this.t.setPlayWhenReady(true);
            }
            if ((z2 || this.w) && f.l(Application.d())) {
                j();
                return;
            }
            this.j.a();
            setBannerVisibility(0);
        }
    }

    public void e() {
        if (this.t == null || !f.l(this.B)) {
            setBannerVisibility(0);
            setLoadingView(8);
            return;
        }
        Log.e(f5334a, "playVideo: ");
        this.t.setPlayWhenReady(true);
        if (!this.z) {
            j();
        }
    }

    public void f() {
        if (this.t != null) {
            setLoadingView(8);
            String str = f5334a;
            Log.e(str, "releasePlayer: " + this.g);
            q a2 = ia.a().a(this.g.getUrl());
            a2.f4579a = this.t.getPlayWhenReady();
            ia.a().a(this.g.getUrl(), a2);
            n();
            m();
            this.t.setPlayWhenReady(false);
            this.t.release();
            this.t = null;
            this.u = null;
            this.z = false;
        }
    }

    public void g() {
        if (this.t != null) {
            Log.e(f5334a, "stopVideo: ");
            this.t.setPlayWhenReady(false);
            setBannerVisibility(0);
            setPlayBtnVisibility(8);
            this.f5337d.setVisibility(8);
        }
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.H = new IntentFilter();
        this.H.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        this.I = new a();
        this.D.registerReceiver(this.I, this.H);
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.content /*2131296669*/:
            case R.id.jump_view /*2131297158*/:
                Log.i(f5334a, view.toString());
                A a2 = this.h;
                if (a2 != null) {
                    a2.a();
                    throw null;
                }
                return;
            case R.id.sounds_btn /*2131297705*/:
                setSoundsOn(!this.y);
                return;
            case R.id.video_play_btn /*2131298017*/:
                if (!f.j(this.D)) {
                    Context context = this.D;
                    Toast.makeText(context, context.getResources().getString(R.string.game_video_network_eror), 0).show();
                    return;
                } else if (this.t != null) {
                    setLoadingView(0);
                    setPlayBtnVisibility(8);
                    j();
                    return;
                } else {
                    d();
                    return;
                }
            case R.id.video_repeat /*2131298018*/:
                l();
                return;
            default:
                return;
        }
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        setLoadingView(8);
        f();
        this.D.unregisterReceiver(this.I);
    }

    /* access modifiers changed from: protected */
    public void onFinishInflate() {
        super.onFinishInflate();
        this.o = getResources().getDimensionPixelSize(R.dimen.view_dimen_1000);
        this.p = getResources().getDimensionPixelSize(R.dimen.view_dimen_561);
        this.f5336c = (TextView) findViewById(R.id.title);
        this.f5337d = (TextView) findViewById(R.id.video_repeat);
        this.e = (RelativeLayout) findViewById(R.id.video_container);
        this.f = (ImageView) findViewById(R.id.banner);
        this.i = (ShowTextCountTextView) findViewById(R.id.content);
        this.m = (ImageView) findViewById(R.id.sounds_btn);
        this.i.setTexColor(getResources().getColor(R.color.color_black_trans_40));
        this.j = (DataNetVideoPlayBtn) findViewById(R.id.video_play_btn);
        this.n = (RelativeLayout) findViewById(R.id.jump_view);
        this.r = (PlayerView) findViewById(R.id.player_view);
        this.r.setControllerVisibilityListener(this);
        this.r.requestFocus();
        if (Build.VERSION.SDK_INT < 26) {
            ((SurfaceView) this.r.getVideoSurfaceView()).getHolder().setFormat(-3);
        }
        this.j.setOnClickListener(this);
        this.m.setOnClickListener(this);
        this.i.setOnClickListener(this);
        this.n.setOnClickListener(this);
        this.f5337d.setOnClickListener(this);
        this.s = a(true);
        this.v = new DefaultTrackSelector.ParametersBuilder().build();
        this.l = (ImageView) findViewById(R.id.loading);
        this.k = ObjectAnimator.ofFloat(this.l, AnimatedProperty.PROPERTY_NAME_ROTATION, new float[]{0.0f, 719.0f});
        this.k.setDuration(3000);
        this.k.setRepeatCount(-1);
        this.k.setInterpolator(new LinearInterpolator());
    }

    public void onPause() {
        if (Build.VERSION.SDK_INT <= 23) {
            f();
        }
    }

    public void onStop() {
        if (Build.VERSION.SDK_INT > 23) {
            f();
        }
    }

    public void onVisibilityChange(int i2) {
    }

    public void preparePlayback() {
        d();
    }

    public void setBannerVisibility(int i2) {
        int i3 = 8;
        if (i2 != 0) {
            setLoadingView(8);
        }
        this.f5337d.setVisibility(8);
        setPlayBtnVisibility(i2);
        this.f.setVisibility(i2);
        ImageView imageView = this.m;
        if (i2 != 0) {
            i3 = 0;
        }
        imageView.setVisibility(i3);
    }

    public void setPlayBtnVisibility(int i2) {
        ViewPointVideoInfo viewPointVideoInfo = this.g;
        if (viewPointVideoInfo == null || TextUtils.isEmpty(viewPointVideoInfo.getUrl())) {
            this.j.setVisibility(8);
            return;
        }
        if (i2 == 0) {
            this.j.setSize((long) this.g.getSize());
        }
        this.j.setVisibility(i2);
    }

    public void setSoundsOn(boolean z2) {
        this.y = z2;
        com.miui.gamebooster.c.a.R(this.y);
        setVolumePre(this.y ? 1.0f : 0.0f);
        this.m.setSelected(!z2);
    }

    public void setVolumePre(float f2) {
        SimpleExoPlayer simpleExoPlayer;
        this.q = f2;
        if (this.q < 0.0f) {
            this.q = 0.0f;
        }
        if (this.q > 1.0f) {
            this.q = 1.0f;
        }
        if (this.z && (simpleExoPlayer = this.t) != null) {
            simpleExoPlayer.setVolume(this.q);
            if (this.q > 0.0f) {
                k();
            } else {
                h();
            }
        }
    }
}
