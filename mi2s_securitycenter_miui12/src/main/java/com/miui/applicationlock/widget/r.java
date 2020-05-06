package com.miui.applicationlock.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.media.MediaFormat;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;
import android.util.Pair;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.MediaController;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Vector;

public class r extends SurfaceView implements MediaController.MediaPlayerControl {
    private MediaPlayer.OnInfoListener A = new n(this);
    private MediaPlayer.OnErrorListener B = new o(this);
    private MediaPlayer.OnBufferingUpdateListener C = new p(this);
    SurfaceHolder.Callback D = new q(this);

    /* renamed from: a  reason: collision with root package name */
    private String f3451a = "VideoView";

    /* renamed from: b  reason: collision with root package name */
    private Uri f3452b;

    /* renamed from: c  reason: collision with root package name */
    private Map<String, String> f3453c;
    /* access modifiers changed from: private */

    /* renamed from: d  reason: collision with root package name */
    public int f3454d = 0;
    /* access modifiers changed from: private */
    public int e = 0;
    /* access modifiers changed from: private */
    public SurfaceHolder f = null;
    /* access modifiers changed from: private */
    public MediaPlayer g = null;
    private int h;
    /* access modifiers changed from: private */
    public int i;
    /* access modifiers changed from: private */
    public int j;
    /* access modifiers changed from: private */
    public int k;
    /* access modifiers changed from: private */
    public int l;
    /* access modifiers changed from: private */
    public MediaController m;
    /* access modifiers changed from: private */
    public MediaPlayer.OnCompletionListener n;
    /* access modifiers changed from: private */
    public MediaPlayer.OnPreparedListener o;
    /* access modifiers changed from: private */
    public int p;
    /* access modifiers changed from: private */
    public MediaPlayer.OnErrorListener q;
    /* access modifiers changed from: private */
    public MediaPlayer.OnInfoListener r;
    /* access modifiers changed from: private */
    public int s;
    /* access modifiers changed from: private */
    public boolean t;
    /* access modifiers changed from: private */
    public boolean u;
    /* access modifiers changed from: private */
    public boolean v;
    private Vector<Pair<InputStream, MediaFormat>> w;
    MediaPlayer.OnVideoSizeChangedListener x = new k(this);
    MediaPlayer.OnPreparedListener y = new l(this);
    private MediaPlayer.OnCompletionListener z = new m(this);

    public r(Context context) {
        super(context);
        c();
    }

    /* access modifiers changed from: private */
    public void a(boolean z2) {
        MediaPlayer mediaPlayer = this.g;
        if (mediaPlayer != null) {
            mediaPlayer.reset();
            this.g.release();
            this.g = null;
            this.w.clear();
            this.f3454d = 0;
            if (z2) {
                this.e = 0;
            }
        }
    }

    private void b() {
        MediaController mediaController;
        if (this.g != null && (mediaController = this.m) != null) {
            mediaController.setMediaPlayer(this);
            this.m.setAnchorView(getParent() instanceof View ? (View) getParent() : this);
            this.m.setEnabled(d());
        }
    }

    private void c() {
        this.i = 0;
        this.j = 0;
        getHolder().addCallback(this.D);
        getHolder().setType(3);
        setFocusable(true);
        setFocusableInTouchMode(true);
        requestFocus();
        this.w = new Vector<>();
        this.f3454d = 0;
        this.e = 0;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:2:0x0005, code lost:
        r0 = r3.f3454d;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private boolean d() {
        /*
            r3 = this;
            android.media.MediaPlayer r0 = r3.g
            r1 = 1
            if (r0 == 0) goto L_0x000f
            int r0 = r3.f3454d
            r2 = -1
            if (r0 == r2) goto L_0x000f
            if (r0 == 0) goto L_0x000f
            if (r0 == r1) goto L_0x000f
            goto L_0x0010
        L_0x000f:
            r1 = 0
        L_0x0010:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.applicationlock.widget.r.d():boolean");
    }

    /* access modifiers changed from: private */
    public void e() {
        MediaPlayer.OnErrorListener onErrorListener;
        MediaPlayer mediaPlayer;
        if (this.f3452b != null && this.f != null) {
            a(false);
            try {
                this.g = new MediaPlayer();
                getContext();
                if (this.h != 0) {
                    this.g.setAudioSessionId(this.h);
                } else {
                    this.h = this.g.getAudioSessionId();
                }
                this.g.setOnPreparedListener(this.y);
                this.g.setOnVideoSizeChangedListener(this.x);
                this.g.setOnCompletionListener(this.z);
                this.g.setOnErrorListener(this.B);
                this.g.setOnInfoListener(this.A);
                this.g.setOnBufferingUpdateListener(this.C);
                this.p = 0;
                this.g.setDataSource(getContext(), this.f3452b, this.f3453c);
                this.g.setDisplay(this.f);
                this.g.setAudioStreamType(3);
                this.g.setScreenOnWhilePlaying(true);
                this.g.prepareAsync();
                this.f3454d = 1;
                b();
                this.w.clear();
                return;
            } catch (IOException e2) {
                String str = this.f3451a;
                Log.w(str, "Unable to open content: " + this.f3452b, e2);
                this.f3454d = -1;
                this.e = -1;
                onErrorListener = this.B;
                mediaPlayer = this.g;
            } catch (IllegalArgumentException e3) {
                String str2 = this.f3451a;
                Log.w(str2, "Unable to open content: " + this.f3452b, e3);
                this.f3454d = -1;
                this.e = -1;
                onErrorListener = this.B;
                mediaPlayer = this.g;
            } catch (Throwable th) {
                this.w.clear();
                throw th;
            }
        } else {
            return;
        }
        onErrorListener.onError(mediaPlayer, 1, 0);
        this.w.clear();
    }

    private void f() {
        if (this.m.isShowing()) {
            this.m.hide();
        } else {
            this.m.show();
        }
    }

    public void a() {
        MediaPlayer mediaPlayer = this.g;
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            this.g.release();
            this.g = null;
            this.f3454d = 0;
            this.e = 0;
        }
    }

    public void a(Uri uri, Map<String, String> map) {
        this.f3452b = uri;
        this.f3453c = map;
        this.s = 0;
        e();
        requestLayout();
        invalidate();
    }

    public boolean canPause() {
        return this.t;
    }

    public boolean canSeekBackward() {
        return this.u;
    }

    public boolean canSeekForward() {
        return this.v;
    }

    public void draw(Canvas canvas) {
        super.draw(canvas);
    }

    public int getAudioSessionId() {
        if (this.h == 0) {
            MediaPlayer mediaPlayer = new MediaPlayer();
            this.h = mediaPlayer.getAudioSessionId();
            mediaPlayer.release();
        }
        return this.h;
    }

    public int getBufferPercentage() {
        if (this.g != null) {
            return this.p;
        }
        return 0;
    }

    public int getCurrentPosition() {
        if (d()) {
            return this.g.getCurrentPosition();
        }
        return 0;
    }

    public int getDuration() {
        if (d()) {
            return this.g.getDuration();
        }
        return -1;
    }

    public boolean isPlaying() {
        return d() && this.g.isPlaying();
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

    public void onInitializeAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
        super.onInitializeAccessibilityEvent(accessibilityEvent);
        accessibilityEvent.setClassName(r.class.getName());
    }

    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
        super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
        accessibilityNodeInfo.setClassName(r.class.getName());
    }

    public boolean onKeyDown(int i2, KeyEvent keyEvent) {
        boolean z2 = (i2 == 4 || i2 == 24 || i2 == 25 || i2 == 164 || i2 == 82 || i2 == 5 || i2 == 6) ? false : true;
        if (d() && z2 && this.m != null) {
            if (i2 == 79 || i2 == 85) {
                if (this.g.isPlaying()) {
                    pause();
                    this.m.show();
                } else {
                    start();
                    this.m.hide();
                }
                return true;
            } else if (i2 == 126) {
                if (!this.g.isPlaying()) {
                    start();
                    this.m.hide();
                }
                return true;
            } else if (i2 == 86 || i2 == 127) {
                if (this.g.isPlaying()) {
                    pause();
                    this.m.show();
                }
                return true;
            } else {
                f();
            }
        }
        return super.onKeyDown(i2, keyEvent);
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean z2, int i2, int i3, int i4, int i5) {
        super.onLayout(z2, i2, i3, i4, i5);
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i2, int i3) {
        int i4;
        int i5;
        int defaultSize = SurfaceView.getDefaultSize(this.i, i2);
        int defaultSize2 = SurfaceView.getDefaultSize(this.j, i3);
        if (this.i > 0 && this.j > 0) {
            int mode = View.MeasureSpec.getMode(i2);
            i4 = View.MeasureSpec.getSize(i2);
            int mode2 = View.MeasureSpec.getMode(i3);
            int size = View.MeasureSpec.getSize(i3);
            if (mode == 1073741824 && mode2 == 1073741824) {
                int i6 = this.i;
                int i7 = i6 * size;
                int i8 = this.j;
                if (i7 < i4 * i8) {
                    defaultSize = (i6 * size) / i8;
                    defaultSize2 = size;
                } else if (i6 * size > i4 * i8) {
                    i5 = (i8 * i4) / i6;
                    setMeasuredDimension(i4, i5);
                }
            } else if (mode == 1073741824) {
                int i9 = (this.j * i4) / this.i;
                if (mode2 != Integer.MIN_VALUE || i9 <= size) {
                    i5 = i9;
                    setMeasuredDimension(i4, i5);
                }
            } else if (mode2 == 1073741824) {
                int i10 = (this.i * size) / this.j;
                if (mode != Integer.MIN_VALUE || i10 <= i4) {
                    i4 = i10;
                }
            } else {
                int i11 = this.i;
                int i12 = this.j;
                if (mode2 != Integer.MIN_VALUE || i12 <= size) {
                    i5 = i12;
                } else {
                    i11 = (i11 * size) / i12;
                    i5 = size;
                }
                if (mode != Integer.MIN_VALUE || i11 <= i4) {
                    i4 = i11;
                } else {
                    i5 = (this.j * i4) / this.i;
                }
                setMeasuredDimension(i4, i5);
            }
            i5 = size;
            setMeasuredDimension(i4, i5);
        }
        i4 = defaultSize;
        setMeasuredDimension(i4, i5);
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        if (!d() || this.m == null) {
            return false;
        }
        f();
        return false;
    }

    public boolean onTrackballEvent(MotionEvent motionEvent) {
        if (!d() || this.m == null) {
            return false;
        }
        f();
        return false;
    }

    public void pause() {
        if (d() && this.g.isPlaying()) {
            this.g.pause();
            this.f3454d = 4;
        }
        this.e = 4;
    }

    public void seekTo(int i2) {
        if (d()) {
            this.g.seekTo(i2);
            i2 = 0;
        }
        this.s = i2;
    }

    public void setMediaController(MediaController mediaController) {
        MediaController mediaController2 = this.m;
        if (mediaController2 != null) {
            mediaController2.hide();
        }
        this.m = mediaController;
        b();
    }

    public void setOnCompletionListener(MediaPlayer.OnCompletionListener onCompletionListener) {
        this.n = onCompletionListener;
    }

    public void setOnErrorListener(MediaPlayer.OnErrorListener onErrorListener) {
        this.q = onErrorListener;
    }

    public void setOnInfoListener(MediaPlayer.OnInfoListener onInfoListener) {
        this.r = onInfoListener;
    }

    public void setOnPreparedListener(MediaPlayer.OnPreparedListener onPreparedListener) {
        this.o = onPreparedListener;
    }

    public void setVideoPath(String str) {
        setVideoURI(Uri.parse(str));
    }

    public void setVideoURI(Uri uri) {
        a(uri, (Map<String, String>) null);
    }

    public void start() {
        if (d()) {
            this.g.start();
            this.f3454d = 3;
        }
        this.e = 3;
    }
}
