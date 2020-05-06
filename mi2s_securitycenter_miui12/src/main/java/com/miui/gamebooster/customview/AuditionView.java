package com.miui.gamebooster.customview;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.app.NotificationManager;
import android.content.Context;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import b.b.o.g.e;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.util.MimeTypes;
import com.miui.gamebooster.encoder.SoundSupport;
import com.miui.gamebooster.m.ma;
import com.miui.securitycenter.R;
import java.util.Locale;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class AuditionView extends LinearLayout implements View.OnTouchListener {

    /* renamed from: a  reason: collision with root package name */
    private static final int[] f4103a = {1, 0, 5};
    /* access modifiers changed from: private */
    public String A;
    /* access modifiers changed from: private */
    public c B;
    /* access modifiers changed from: private */
    public Runnable C = new C0332a(this);
    /* access modifiers changed from: private */
    public BlockingQueue<short[]> D = new LinkedBlockingQueue();
    /* access modifiers changed from: private */

    /* renamed from: b  reason: collision with root package name */
    public TextView f4104b;
    /* access modifiers changed from: private */

    /* renamed from: c  reason: collision with root package name */
    public RecordVolumView f4105c;
    /* access modifiers changed from: private */

    /* renamed from: d  reason: collision with root package name */
    public ImageView f4106d;
    /* access modifiers changed from: private */
    public W e;
    /* access modifiers changed from: private */
    public Context f;
    /* access modifiers changed from: private */
    public AudioManager g;
    private AudioRecord h;
    private b i;
    /* access modifiers changed from: private */
    public AudioTrack j;
    /* access modifiers changed from: private */
    public a k;
    /* access modifiers changed from: private */
    public SoundSupport l;
    /* access modifiers changed from: private */
    public C0353w m;
    private AnimatorSet n;
    private ValueAnimator o;
    /* access modifiers changed from: private */
    public boolean p;
    /* access modifiers changed from: private */
    public boolean q = false;
    private boolean r;
    /* access modifiers changed from: private */
    public boolean s;
    /* access modifiers changed from: private */
    public int t;
    /* access modifiers changed from: private */
    public int u;
    /* access modifiers changed from: private */
    public int v;
    /* access modifiers changed from: private */
    public int w;
    /* access modifiers changed from: private */
    public int x;
    /* access modifiers changed from: private */
    public long y = 0;
    private long z;

    private class a extends Thread {

        /* renamed from: a  reason: collision with root package name */
        private short[] f4107a = new short[0];

        /* renamed from: b  reason: collision with root package name */
        private BlockingQueue<short[]> f4108b = new LinkedBlockingQueue();

        /* renamed from: c  reason: collision with root package name */
        private boolean f4109c;

        public a() {
            AuditionView.this.l.setMode((float) a(ma.d()));
        }

        private int a(String str) {
            if (str.equals("original")) {
                return 0;
            }
            if (str.equals("loli")) {
                return 3;
            }
            if (str.equals("lady")) {
                return 2;
            }
            if (str.equals("men")) {
                return 1;
            }
            if (str.equals("cartoon")) {
                return 4;
            }
            return str.equals("robot") ? 5 : 0;
        }

        private void b() {
            short[] take = this.f4108b.take();
            if (take != this.f4107a && AuditionView.this.l != null) {
                AuditionView.this.l.putSamples(take);
            }
        }

        private void c() {
            short[] receiveSamples;
            while (AuditionView.this.l != null && (receiveSamples = AuditionView.this.l.receiveSamples(1024)) != null) {
                try {
                    AuditionView.this.D.put(receiveSamples);
                } catch (Exception unused) {
                    Log.e("AuditionView", "effect samples buffer queue put error");
                }
            }
        }

        public void a() {
            this.f4109c = true;
            a(this.f4107a);
        }

        public void a(short[] sArr) {
            try {
                this.f4108b.put(sArr);
            } catch (InterruptedException unused) {
                Log.e("AuditionView", "effect buffer queue put error");
            }
        }

        public void run() {
            super.run();
            while (true) {
                if (!this.f4109c || !this.f4108b.isEmpty()) {
                    try {
                        b();
                    } catch (Exception e) {
                        Log.e("AuditionView", "audio effect process buffer error", e);
                    }
                    c();
                } else {
                    return;
                }
            }
        }
    }

    class b extends Thread {

        /* renamed from: a  reason: collision with root package name */
        private AudioRecord f4111a;

        public b(AudioRecord audioRecord) {
            this.f4111a = audioRecord;
        }

        public void run() {
            AudioRecord audioRecord;
            super.run();
            if (this.f4111a == null) {
                AudioRecord unused = AuditionView.this.e();
            }
            while (AuditionView.this.p && (audioRecord = this.f4111a) != null) {
                short[] sArr = new short[1024];
                if (audioRecord.read(sArr, 0, 1024) > 0) {
                    AuditionView.this.k.a(sArr);
                }
            }
        }
    }

    private class c extends Handler {
        private c() {
        }

        /* synthetic */ c(AuditionView auditionView, C0332a aVar) {
            this();
        }

        public void handleMessage(Message message) {
            super.handleMessage(message);
            int i = message.what;
            if (i == 1) {
                AuditionView.this.f4104b.setText(R.string.gb_audition_play_instruction);
                AuditionView.this.e.a(AuditionView.this.y);
            } else if (i == 2) {
                int unused = AuditionView.this.w = 0;
                AuditionView.this.f4104b.setText(AuditionView.this.A);
                AuditionView.this.e.a(1);
            } else if (i == 3) {
                AuditionView.this.f4105c.setTime(AuditionView.this.w / 1000);
                if (AuditionView.this.w < 10000 && AuditionView.this.p) {
                    AuditionView.this.B.postDelayed(AuditionView.this.C, 1000);
                }
            } else if (i == 4) {
                AuditionView.this.f4105c.setVoice(message.getData().getDouble("voice_percent"));
            } else if (i == 5) {
                AuditionView.this.f4105c.setTime(AuditionView.this.w / 1000);
                AuditionView.this.k();
            }
        }
    }

    private class d implements Runnable {
        private d() {
        }

        /* synthetic */ d(AuditionView auditionView, C0332a aVar) {
            this();
        }

        public void run() {
            if (!AuditionView.this.s) {
                AuditionView.this.g.setStreamVolume(3, 0, 4);
                AuditionView auditionView = AuditionView.this;
                int unused = auditionView.u = auditionView.g.getStreamVolume(AuditionView.this.x);
                AuditionView.this.g.setStreamVolume(AuditionView.this.x, AuditionView.this.g.getStreamMaxVolume(AuditionView.this.x), 4);
            }
            int minBufferSize = AudioTrack.getMinBufferSize(44100, 4, 2);
            AuditionView auditionView2 = AuditionView.this;
            AudioTrack unused2 = auditionView2.j = new AudioTrack(auditionView2.x, 44100, 4, 2, minBufferSize, 1);
            AuditionView.this.j.setVolume(1.0f);
            AuditionView.this.j.play();
            AuditionView.this.B.sendEmptyMessage(1);
            while (!AuditionView.this.p && AuditionView.this.D.size() > 0 && !AuditionView.this.q) {
                try {
                    short[] sArr = (short[]) AuditionView.this.D.take();
                    if (sArr == null || AuditionView.this.j.getPlayState() != 3) {
                        AuditionView.this.D.clear();
                    } else {
                        AuditionView.this.j.write(sArr, 0, sArr.length);
                    }
                } catch (Exception e) {
                    Log.e("AuditionView", "tracker write error", e);
                }
            }
            AuditionView.this.D.clear();
            boolean unused3 = AuditionView.this.q = true;
            AuditionView.this.B.sendEmptyMessage(2);
            if (AuditionView.this.j.getState() != 0 && AuditionView.this.j.getPlayState() == 3) {
                AuditionView.this.j.stop();
                AuditionView.this.j.release();
            }
            if (!AuditionView.this.s) {
                AuditionView.this.g.setStreamVolume(3, AuditionView.this.t, 4);
                AuditionView.this.g.setStreamVolume(0, AuditionView.this.v, 4);
                AuditionView.this.g.setStreamVolume(AuditionView.this.x, AuditionView.this.u, 4);
            }
        }
    }

    public AuditionView(Context context) {
        super(context);
        a(context);
    }

    public AuditionView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        a(context);
    }

    public AuditionView(Context context, AttributeSet attributeSet, int i2) {
        super(context, attributeSet, i2);
        a(context);
    }

    private void a(Context context) {
        this.f = context;
        this.h = e();
        this.g = (AudioManager) this.f.getSystemService(MimeTypes.BASE_TYPE_AUDIO);
        this.B = new c(this, (C0332a) null);
        f();
        this.s = g();
        if (!this.s) {
            this.t = this.g.getStreamVolume(3);
            this.v = this.g.getStreamVolume(0);
            this.u = this.g.getStreamVolume(this.x);
        }
        View inflate = LayoutInflater.from(context).inflate(R.layout.gb_voice_changer_audition_layout, this, false);
        addView(inflate);
        this.f4104b = (TextView) inflate.findViewById(R.id.instruction);
        this.A = getResources().getString(R.string.gb_audition_instruction, new Object[]{String.format(Locale.getDefault(), "%d", new Object[]{0}), String.format(Locale.getDefault(), "%d", new Object[]{10})});
        this.f4104b.setText(this.A);
        this.f4106d = (ImageView) inflate.findViewById(R.id.record_icon);
        this.f4105c = (RecordVolumView) inflate.findViewById(R.id.recording_title);
        this.f4106d.setOnTouchListener(this);
        this.m = new C0353w(this.f);
        this.f4106d.setImageDrawable(this.m);
    }

    private void c() {
        ValueAnimator valueAnimator = this.o;
        if (valueAnimator != null && valueAnimator.isRunning()) {
            this.o.cancel();
        }
    }

    private void d() {
        AnimatorSet animatorSet = this.n;
        if (animatorSet != null && animatorSet.isRunning()) {
            this.n.cancel();
        }
    }

    /* access modifiers changed from: private */
    public AudioRecord e() {
        int minBufferSize = AudioRecord.getMinBufferSize(44100, 16, 2);
        int i2 = 25600;
        if (25600 < minBufferSize) {
            i2 = ((minBufferSize / 1024) + 1) * 1024 * 2;
        }
        AudioRecord audioRecord = null;
        for (int audioRecord2 : f4103a) {
            AudioRecord audioRecord3 = new AudioRecord(audioRecord2, 44100, 16, 2, i2);
            if (audioRecord3.getState() != 1) {
                audioRecord3.release();
                audioRecord = null;
            } else {
                audioRecord = audioRecord3;
            }
            if (audioRecord != null) {
                break;
            }
        }
        return audioRecord;
    }

    private void f() {
        try {
            this.x = ((Integer) e.a((Class<?>) AudioManager.class, "STREAM_VOICEASSIST")).intValue();
        } catch (Exception unused) {
            this.x = 1;
            Log.e("AuditionView", "get stream voiceassist failed");
        }
    }

    private boolean g() {
        return Build.VERSION.SDK_INT >= 23 && ((NotificationManager) this.f.getSystemService("notification")).getCurrentInterruptionFilter() == 2;
    }

    private void h() {
        ValueAnimator ofInt = ValueAnimator.ofInt(new int[]{255, 153, 153, 255});
        ofInt.setDuration(500);
        ofInt.setRepeatCount(20);
        ofInt.setRepeatMode(2);
        ofInt.addUpdateListener(new C0334c(this));
        ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{1.0f, 1.1f, 1.1f, 1.0f});
        ofFloat.setDuration(500);
        ofFloat.setRepeatCount(20);
        ofFloat.setRepeatMode(2);
        ofFloat.addUpdateListener(new C0335d(this));
        this.n = new AnimatorSet();
        this.n.setInterpolator(new LinearInterpolator());
        this.n.playTogether(new Animator[]{ofInt, ofFloat});
        this.n.addListener(new C0336e(this));
        this.n.start();
    }

    private void i() {
        this.o = ValueAnimator.ofFloat(new float[]{1.0f, 5.0f, 9.0f, 9.0f, 5.0f, 1.0f});
        this.o.setDuration(AdaptiveTrackSelection.DEFAULT_MIN_TIME_BETWEEN_BUFFER_REEVALUTATION_MS);
        this.o.setRepeatCount(5);
        this.o.setRepeatMode(2);
        this.o.addUpdateListener(new C0333b(this));
        this.o.start();
    }

    private void j() {
        Animation loadAnimation = AnimationUtils.loadAnimation(this.f, R.anim.gb_record_view_exit);
        this.f4105c.startAnimation(loadAnimation);
        loadAnimation.setAnimationListener(new C0337f(this));
    }

    /* access modifiers changed from: private */
    public void k() {
        this.y = System.currentTimeMillis() - this.y;
        this.p = false;
        this.e.a(true);
        this.q = false;
        j();
        this.m.a(false);
        d();
        c();
        this.k.a();
        AudioRecord audioRecord = this.h;
        if (audioRecord != null) {
            audioRecord.stop();
        }
        this.l.release();
        new Thread(new d(this, (C0332a) null)).start();
    }

    public void a() {
        this.q = true;
        AudioTrack audioTrack = this.j;
        if (!(audioTrack == null || audioTrack.getPlayState() == 0 || this.j.getPlayState() != 3)) {
            this.j.stop();
            this.j.release();
        }
        AudioRecord audioRecord = this.h;
        if (audioRecord != null) {
            audioRecord.release();
        }
        if (!this.s) {
            this.g.setStreamVolume(3, this.t, 4);
            this.g.setStreamVolume(0, this.v, 4);
            this.g.setStreamVolume(this.x, this.u, 4);
        }
        this.B.removeCallbacksAndMessages((Object) null);
    }

    public void b() {
        this.q = true;
        AudioTrack audioTrack = this.j;
        if (audioTrack != null) {
            if (audioTrack.getState() != 0 && this.j.getPlayState() == 3) {
                this.j.stop();
                this.j.release();
            }
            this.D.clear();
            this.B.sendEmptyMessage(2);
            if (!this.s) {
                this.g.setStreamVolume(3, this.t, 4);
                this.g.setStreamVolume(0, this.v, 4);
                this.g.setStreamVolume(this.x, this.u, 4);
            }
        }
    }

    public boolean onTouch(View view, MotionEvent motionEvent) {
        AudioTrack audioTrack;
        if (view == this.f4106d) {
            int action = motionEvent.getAction();
            if (action == 0) {
                if (this.z == 0) {
                    this.z = System.currentTimeMillis() - 1000;
                }
                long currentTimeMillis = System.currentTimeMillis() - this.z;
                this.z = System.currentTimeMillis();
                if (currentTimeMillis < 1000) {
                    this.r = true;
                    this.e.a((int) R.string.gb_operat_frequently, false);
                } else {
                    this.r = false;
                    AudioTrack audioTrack2 = this.j;
                    if (audioTrack2 != null && audioTrack2.getPlayState() == 3) {
                        b();
                    }
                    if (!this.s) {
                        this.t = this.g.getStreamVolume(3);
                        this.v = this.g.getStreamVolume(0);
                        this.g.setStreamVolume(3, 0, 4);
                        this.g.setStreamVolume(0, 0, 4);
                    }
                    this.y = System.currentTimeMillis();
                    this.p = true;
                    this.e.a(false);
                    this.l = new SoundSupport(44100, 1);
                    this.m.a(true);
                    this.D.clear();
                    AudioRecord audioRecord = this.h;
                    if (audioRecord != null) {
                        audioRecord.startRecording();
                    }
                    this.f4105c.setVisibility(0);
                    this.f4105c.startAnimation(AnimationUtils.loadAnimation(this.f, R.anim.gb_record_view_enter));
                    this.f4104b.setVisibility(8);
                    this.f4105c.setTime(0);
                    this.B.postDelayed(this.C, 1000);
                    this.k = new a();
                    this.i = new b(this.h);
                    this.k.start();
                    this.i.start();
                    h();
                    i();
                }
            } else if (action == 1 && !this.r && ((audioTrack = this.j) == null || audioTrack.getPlayState() != 3)) {
                this.B.removeCallbacks(this.C);
                k();
            }
        }
        return true;
    }

    public void setInstructSelected(boolean z2) {
        this.f4104b.setSelected(z2);
    }

    public void setVoiceChangerWindow(W w2) {
        this.e = w2;
    }
}
