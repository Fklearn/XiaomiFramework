package com.miui.gamebooster.ui;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import b.b.c.i.b;
import b.b.c.j.i;
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
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.upstream.HttpDataSource;
import com.google.android.exoplayer2.upstream.TransferListener;
import com.google.android.exoplayer2.util.MimeTypes;
import com.google.android.exoplayer2.util.Util;
import com.miui.gamebooster.m.C0373d;
import com.miui.gamebooster.m.C0388t;
import com.miui.gamebooster.m.Q;
import com.miui.gamebooster.m.fa;
import com.miui.gamebooster.m.ia;
import com.miui.gamebooster.m.na;
import com.miui.gamebooster.model.q;
import com.miui.securitycenter.Application;
import com.miui.securitycenter.R;
import java.lang.ref.WeakReference;
import miui.os.Build;

public class GameVideoPlayActivity extends b.b.c.c.a implements View.OnClickListener, PlaybackPreparer {

    /* renamed from: a  reason: collision with root package name */
    private static final DefaultBandwidthMeter f4908a = new DefaultBandwidthMeter();

    /* renamed from: b  reason: collision with root package name */
    private PlayerView f4909b;

    /* renamed from: c  reason: collision with root package name */
    private ImageView f4910c;

    /* renamed from: d  reason: collision with root package name */
    protected float f4911d = 1.0f;
    private DataSource.Factory e;
    /* access modifiers changed from: private */
    public SimpleExoPlayer f;
    private DefaultTrackSelector g;
    private DefaultTrackSelector.Parameters h;
    /* access modifiers changed from: private */
    public TrackGroupArray i;
    protected boolean j;
    private boolean k = false;
    /* access modifiers changed from: private */
    public boolean l;
    private Uri m;
    private b n;
    protected AudioManager o;
    /* access modifiers changed from: private */
    public WeakReference<AudioManager.OnAudioFocusChangeListener> p;
    private AudioManager.OnAudioFocusChangeListener q = new C0451ta(this);
    private String r;
    private View s;

    private class a extends Player.DefaultEventListener {
        private a() {
        }

        /* synthetic */ a(GameVideoPlayActivity gameVideoPlayActivity, C0451ta taVar) {
            this();
        }

        public void onPlayerError(ExoPlaybackException exoPlaybackException) {
            int i = exoPlaybackException.type;
            if (i == 0) {
                GameVideoPlayActivity.this.f.setPlayWhenReady(false);
            } else if (i != 1) {
            }
            boolean unused = GameVideoPlayActivity.this.l = true;
            if (GameVideoPlayActivity.b(exoPlaybackException)) {
                GameVideoPlayActivity.this.o();
                GameVideoPlayActivity.this.l();
                return;
            }
            GameVideoPlayActivity.this.s();
        }

        public void onPlayerStateChanged(boolean z, int i) {
            if (z && i != 1 && i != 2) {
                if (i == 3) {
                    GameVideoPlayActivity gameVideoPlayActivity = GameVideoPlayActivity.this;
                    gameVideoPlayActivity.a(gameVideoPlayActivity.j);
                } else if (i == 4) {
                    GameVideoPlayActivity.this.f.seekToDefaultPosition();
                    GameVideoPlayActivity.this.f.setPlayWhenReady(false);
                }
            }
        }

        public void onPositionDiscontinuity(int i) {
            if (GameVideoPlayActivity.this.f.getPlaybackError() != null) {
                GameVideoPlayActivity.this.s();
            }
        }

        public void onTracksChanged(TrackGroupArray trackGroupArray, TrackSelectionArray trackSelectionArray) {
            if (trackGroupArray != GameVideoPlayActivity.this.i) {
                TrackGroupArray unused = GameVideoPlayActivity.this.i = trackGroupArray;
            }
        }
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
            return new HlsMediaSource(uri, this.e, (Handler) null, (MediaSourceEventListener) null);
        }
        if (inferContentType == 3) {
            return new ExtractorMediaSource(uri, this.e, new DefaultExtractorsFactory(), this.n, (ExtractorMediaSource.EventListener) null);
        }
        throw new IllegalStateException("Unsupported type: " + inferContentType);
    }

    /* JADX WARNING: type inference failed for: r2v0, types: [android.content.Context, com.miui.gamebooster.ui.GameVideoPlayActivity] */
    private DataSource.Factory a(DefaultBandwidthMeter defaultBandwidthMeter) {
        return new DefaultDataSourceFactory((Context) this, (TransferListener<? super DataSource>) defaultBandwidthMeter, (DataSource.Factory) b(defaultBandwidthMeter));
    }

    private void a(int i2) {
        View view = this.s;
        if (view != null) {
            view.setPadding(i2, 0, 0, 0);
        }
    }

    private DataSource.Factory b(boolean z) {
        return a(z ? f4908a : null);
    }

    /* JADX WARNING: type inference failed for: r2v0, types: [android.content.Context, com.miui.gamebooster.ui.GameVideoPlayActivity] */
    private HttpDataSource.Factory b(DefaultBandwidthMeter defaultBandwidthMeter) {
        return new DefaultHttpDataSourceFactory(Util.getUserAgent(this, "ExoPlayerDemo"), defaultBandwidthMeter);
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

    /* JADX WARNING: type inference failed for: r2v0, types: [android.content.Context, android.view.View$OnClickListener, miui.app.Activity, com.miui.gamebooster.ui.GameVideoPlayActivity] */
    private void initView() {
        this.s = findViewById(R.id.layout_bottom_control);
        View findViewById = findViewById(R.id.btn_close);
        if (findViewById != null) {
            if (na.c()) {
                findViewById.setRotation(180.0f);
            }
            findViewById.setOnClickListener(this);
        }
        this.f4909b = (PlayerView) findViewById(R.id.player_view);
        this.f4910c = (ImageView) findViewById(R.id.sounds_btn);
        ImageView imageView = this.f4910c;
        if (imageView != null) {
            imageView.setOnClickListener(this);
        }
        if (i.e()) {
            a(i.a((Context) this, 30.0f));
        }
        this.r = getIntent().getDataString();
        this.e = b(true);
        this.h = new DefaultTrackSelector.ParametersBuilder().build();
        l();
    }

    private boolean n() {
        if (this.o == null) {
            this.o = (AudioManager) Application.d().getSystemService(MimeTypes.BASE_TYPE_AUDIO);
        }
        AudioManager audioManager = this.o;
        return audioManager != null && audioManager.abandonAudioFocus(this.q) == 1;
    }

    /* access modifiers changed from: private */
    public void o() {
        q a2 = ia.a().a(this.r);
        a2.f4579a = true;
        a2.f4580b = -1;
        a2.f4581c = C.TIME_UNSET;
        ia.a().a(this.r, a2);
    }

    /* JADX WARNING: type inference failed for: r9v0, types: [android.app.Activity, com.miui.gamebooster.ui.GameVideoPlayActivity] */
    private void p() {
        if (!TextUtils.isEmpty(this.r)) {
            this.m = Uri.parse(this.r);
            if (!Util.maybeRequestReadExternalStoragePermission(this, this.m)) {
                MediaSource a2 = a(this.m, (String) null);
                q a3 = ia.a().a(this.r);
                boolean z = a3.f4580b != -1;
                if (z) {
                    this.f.seekTo(a3.f4580b, a3.f4581c);
                }
                this.f.prepare(a2, !z, false);
                this.l = false;
                this.k = true;
                this.j = com.miui.gamebooster.c.a.c();
                a(this.j);
            }
        }
    }

    private boolean q() {
        if (this.o == null) {
            this.o = (AudioManager) Application.d().getSystemService(MimeTypes.BASE_TYPE_AUDIO);
        }
        AudioManager audioManager = this.o;
        return audioManager != null && this.f4911d > 0.0f && audioManager.requestAudioFocus(this.q, 3, 1) == 1;
    }

    private void r() {
        SimpleExoPlayer simpleExoPlayer = this.f;
        if (simpleExoPlayer != null) {
            simpleExoPlayer.seekToDefaultPosition();
            this.f.setPlayWhenReady(true);
        }
    }

    /* access modifiers changed from: private */
    public void s() {
        if (this.f != null) {
            q a2 = ia.a().a(this.r);
            a2.f4579a = this.f.getPlayWhenReady();
            a2.f4580b = this.f.getCurrentWindowIndex();
            a2.f4581c = this.f.isCurrentWindowSeekable() ? Math.max(0, this.f.getCurrentPosition()) : C.TIME_UNSET;
            ia.a().a(this.r, a2);
        }
    }

    public void a(float f2) {
        SimpleExoPlayer simpleExoPlayer;
        this.f4911d = f2;
        if (this.f4911d < 0.0f) {
            this.f4911d = 0.0f;
        }
        if (this.f4911d > 1.0f) {
            this.f4911d = 1.0f;
        }
        if (this.k && (simpleExoPlayer = this.f) != null) {
            simpleExoPlayer.setVolume(this.f4911d);
            if (this.f4911d > 0.0f) {
                q();
            } else {
                n();
            }
        }
    }

    public void a(boolean z) {
        this.j = z;
        com.miui.gamebooster.c.a.R(this.j);
        a(this.j ? 1.0f : 0.0f);
        this.f4910c.setSelected(!z);
    }

    /* JADX WARNING: type inference failed for: r5v0, types: [android.content.Context, com.miui.gamebooster.ui.GameVideoPlayActivity] */
    public void l() {
        if (!this.k) {
            boolean z = this.f == null;
            if (z && this.f4909b != null) {
                this.g = new DefaultTrackSelector((TrackSelection.Factory) new AdaptiveTrackSelection.Factory(f4908a));
                this.g.setParameters(this.h);
                this.i = null;
                this.f = ExoPlayerFactory.newSimpleInstance((Context) this, (TrackSelector) this.g);
                this.f.addListener(new a(this, (C0451ta) null));
                this.f4909b.setControllerAutoShow(true);
                this.f4909b.setControllerHideOnTouch(true);
                this.f4909b.setPlayer(this.f);
                this.f.setPlayWhenReady(true);
            }
            if (z || this.l) {
                p();
            }
        }
    }

    public void m() {
        if (this.f != null) {
            q a2 = ia.a().a(this.r);
            a2.f4579a = this.f.getPlayWhenReady();
            ia.a().a(this.r, a2);
            s();
            DefaultTrackSelector defaultTrackSelector = this.g;
            if (defaultTrackSelector != null) {
                this.h = defaultTrackSelector.getParameters();
            }
            this.f.setPlayWhenReady(false);
            this.f.release();
            this.f = null;
            this.g = null;
            this.k = false;
        }
    }

    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.btn_close) {
            finish();
        } else if (id == R.id.sounds_btn) {
            a(!this.j);
        } else if (id == R.id.video_repeat) {
            r();
        }
    }

    /* JADX WARNING: type inference failed for: r1v0, types: [b.b.c.c.a, miui.app.Activity, android.app.Activity, com.miui.gamebooster.ui.GameVideoPlayActivity] */
    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        setTheme(R.style.GameLandscape);
        if (Build.IS_INTERNATIONAL_BUILD || !C0388t.t()) {
            finish();
            return;
        }
        super.onCreate(bundle);
        Q.a((Activity) this);
        setContentView(R.layout.gb_activity_wonderful_video_play);
        na.a((Activity) this);
        fa.a(this);
        initView();
        p();
    }

    /* access modifiers changed from: protected */
    public void onDestroy() {
        GameVideoPlayActivity.super.onDestroy();
        m();
    }

    /* access modifiers changed from: protected */
    public void onResume() {
        super.onResume();
        C0373d.c();
    }

    public void preparePlayback() {
        l();
    }
}
