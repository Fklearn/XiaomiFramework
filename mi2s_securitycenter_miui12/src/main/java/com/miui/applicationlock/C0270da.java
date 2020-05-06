package com.miui.applicationlock;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.miui.applicationlock.ConfirmAccessControl;
import com.miui.applicationlock.c.o;

/* renamed from: com.miui.applicationlock.da  reason: case insensitive filesystem */
class C0270da extends AsyncTask<Void, Void, Void> {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ Uri f3341a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ PlayerView f3342b;

    /* renamed from: c  reason: collision with root package name */
    final /* synthetic */ ConfirmAccessControl f3343c;

    C0270da(ConfirmAccessControl confirmAccessControl, Uri uri, PlayerView playerView) {
        this.f3343c = confirmAccessControl;
        this.f3341a = uri;
        this.f3342b = playerView;
    }

    /* JADX WARNING: type inference failed for: r4v12, types: [android.content.Context, com.miui.applicationlock.ConfirmAccessControl] */
    /* access modifiers changed from: protected */
    /* renamed from: a */
    public Void doInBackground(Void... voidArr) {
        DefaultTrackSelector.Parameters unused = this.f3343c.Aa = new DefaultTrackSelector.ParametersBuilder().build();
        DefaultTrackSelector unused2 = this.f3343c.za = new DefaultTrackSelector((TrackSelection.Factory) new AdaptiveTrackSelection.Factory(o.e));
        this.f3343c.za.setParameters(this.f3343c.Aa);
        ConfirmAccessControl confirmAccessControl = this.f3343c;
        SimpleExoPlayer unused3 = confirmAccessControl.ya = ExoPlayerFactory.newSimpleInstance(confirmAccessControl.getApplicationContext(), (TrackSelector) this.f3343c.za);
        this.f3343c.ya.addListener(new ConfirmAccessControl.c(this.f3343c, (X) null));
        this.f3343c.ya.setPlayWhenReady(true);
        this.f3343c.ya.setVolume(this.f3343c.Ha ? 1.0f : 0.0f);
        ? r4 = this.f3343c;
        MediaSource unused4 = r4.Ba = new ExtractorMediaSource.Factory(o.a((Context) r4, true)).createMediaSource(this.f3341a);
        return null;
    }

    /* access modifiers changed from: protected */
    /* renamed from: a */
    public void onPostExecute(Void voidR) {
        SimpleExoPlayer simpleExoPlayer;
        long j;
        this.f3342b.requestFocus();
        this.f3342b.setUseController(false);
        this.f3342b.setResizeMode(3);
        this.f3342b.setPlayer(this.f3343c.ya);
        this.f3343c.ya.prepare(this.f3343c.Ba);
        if (this.f3343c.Ca) {
            simpleExoPlayer = this.f3343c.ya;
            j = this.f3343c.Ea - 500;
        } else {
            simpleExoPlayer = this.f3343c.ya;
            j = this.f3343c.Ea;
        }
        simpleExoPlayer.seekTo(j);
        if (this.f3343c.F() && !this.f3343c.Ia) {
            boolean unused = this.f3343c.Ia = false;
            this.f3343c.a(8);
        }
    }
}
