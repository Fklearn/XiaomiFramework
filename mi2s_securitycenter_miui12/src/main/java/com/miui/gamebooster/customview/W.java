package com.miui.gamebooster.customview;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.google.android.exoplayer2.util.MimeTypes;
import com.miui.common.persistence.b;
import com.miui.gamebooster.m.C0373d;
import com.miui.gamebooster.m.C0393y;
import com.miui.gamebooster.m.ma;
import com.miui.securitycenter.R;

public class W implements View.OnClickListener, View.OnTouchListener {
    private Handler A;
    private LocalBroadcastManager B;
    private BroadcastReceiver C = new J(this);
    private Runnable D = new N(this);

    /* renamed from: a  reason: collision with root package name */
    private Context f4162a;

    /* renamed from: b  reason: collision with root package name */
    private WindowManager f4163b;

    /* renamed from: c  reason: collision with root package name */
    private AudioManager f4164c;

    /* renamed from: d  reason: collision with root package name */
    private WindowManager.LayoutParams f4165d;
    /* access modifiers changed from: private */
    public AuditionView e;
    private ImageView f;
    private View g;
    private View h;
    /* access modifiers changed from: private */
    public View i;
    /* access modifiers changed from: private */
    public View j;
    private VoiceModeView k;
    private VoiceModeView l;
    private VoiceModeView m;
    private VoiceModeView n;
    private VoiceModeView o;
    private VoiceModeView p;
    /* access modifiers changed from: private */
    public VoiceModeView q;
    /* access modifiers changed from: private */
    public TextView r;
    /* access modifiers changed from: private */
    public TextView s;
    /* access modifiers changed from: private */
    public TextView t;
    private ValueAnimator u;
    private ValueAnimator v;
    /* access modifiers changed from: private */
    public boolean w;
    /* access modifiers changed from: private */
    public boolean x = false;
    private boolean y = true;
    private int z;

    public W(Context context) {
        this.f4162a = context;
        this.f4165d = new WindowManager.LayoutParams();
        this.f4163b = (WindowManager) context.getSystemService("window");
        this.f4164c = (AudioManager) context.getSystemService(MimeTypes.BASE_TYPE_AUDIO);
        this.B = LocalBroadcastManager.getInstance(context);
        this.A = new Handler();
    }

    private void a(VoiceModeView voiceModeView, String str) {
        if (this.y) {
            int status = this.q.getStatus();
            if (voiceModeView != this.q) {
                if (status == 2) {
                    i();
                    this.e.b();
                }
                this.q.setIonBgStatus(0);
            }
            int status2 = voiceModeView.getStatus();
            if (status2 == 2) {
                i();
                this.e.b();
            } else if (status2 == 0) {
                a(str);
                voiceModeView.setIonBgStatus(1);
                this.q.setIonBgStatus(0);
                this.q = voiceModeView;
            }
            C0373d.l(str);
        }
    }

    private void a(String str) {
        String str2;
        ma.a(str);
        String a2 = b.a("key_currentbooster_pkg_uid", (String) null);
        int i2 = -1;
        if (a2 != null) {
            String[] split = a2.split(",");
            str2 = split[0];
            try {
                i2 = Integer.parseInt(split[1]);
            } catch (Exception unused) {
                Log.e("VoiceChangerWindow", "parseInt error while get uid");
            }
        } else {
            str2 = null;
        }
        if ("original".equals(str)) {
            ma.a(this.f4164c, this.f4162a, str2, i2);
            long b2 = ma.b();
            if (b2 != 0) {
                long currentTimeMillis = (System.currentTimeMillis() - b2) / 60000;
                C0373d.a(str, str2, currentTimeMillis + "");
                ma.b(ma.c() + currentTimeMillis);
                return;
            }
            return;
        }
        ma.a(this.f4164c, this.f4162a, str, str2, i2);
        a((int) R.string.gb_voice_change_settinged, true);
        ma.a(System.currentTimeMillis());
    }

    private void c() {
        ValueAnimator ofInt = ValueAnimator.ofInt(new int[]{0, this.z});
        ofInt.setDuration(350);
        ofInt.addUpdateListener(new D(this));
        ValueAnimator ofInt2 = ValueAnimator.ofInt(new int[]{0, 255});
        ofInt2.setDuration(350);
        ofInt2.addUpdateListener(new E(this));
        this.r.measure(View.MeasureSpec.makeMeasureSpec(0, 0), View.MeasureSpec.makeMeasureSpec(0, 0));
        ValueAnimator ofInt3 = ValueAnimator.ofInt(new int[]{this.r.getMeasuredHeight(), 0});
        ofInt3.setDuration(350);
        ofInt3.addUpdateListener(new F(this));
        ValueAnimator ofInt4 = ValueAnimator.ofInt(new int[]{255, 0});
        ofInt4.setDuration(350);
        ofInt4.addUpdateListener(new G(this));
        this.s.measure(View.MeasureSpec.makeMeasureSpec(0, 0), View.MeasureSpec.makeMeasureSpec(0, 0));
        ValueAnimator ofInt5 = ValueAnimator.ofInt(new int[]{this.s.getMeasuredHeight(), 0});
        ofInt5.setDuration(350);
        ofInt5.addUpdateListener(new H(this));
        ValueAnimator ofInt6 = ValueAnimator.ofInt(new int[]{255, 0});
        ofInt6.setDuration(350);
        ofInt6.addUpdateListener(new I(this));
        ValueAnimator ofInt7 = ValueAnimator.ofInt(new int[]{0, this.f4162a.getResources().getDimensionPixelSize(R.dimen.gb_vc_first_item_margin_top)});
        ofInt7.setDuration(350);
        ofInt7.addUpdateListener(new K(this));
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) this.i.getLayoutParams();
        ValueAnimator ofInt8 = ValueAnimator.ofInt(new int[]{layoutParams.topMargin, this.f4162a.getResources().getDimensionPixelSize(R.dimen.gb_vc_drop_item_margin_top)});
        ofInt8.setDuration(350);
        ofInt8.addUpdateListener(new L(this));
        ValueAnimator ofInt9 = ValueAnimator.ofInt(new int[]{layoutParams.bottomMargin, this.f4162a.getResources().getDimensionPixelSize(R.dimen.gb_vc_drop_item_margin_bottom)});
        ofInt9.setDuration(350);
        ofInt9.addUpdateListener(new M(this));
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(new Animator[]{ofInt, ofInt2, ofInt3, ofInt4, ofInt5, ofInt6, ofInt7, ofInt8, ofInt9});
        animatorSet.start();
    }

    private void d() {
        ValueAnimator ofInt = ValueAnimator.ofInt(new int[]{this.z, 0});
        ofInt.setDuration(350);
        ofInt.addUpdateListener(new Q(this));
        ValueAnimator ofInt2 = ValueAnimator.ofInt(new int[]{255, 0});
        ofInt2.setDuration(350);
        ofInt2.addUpdateListener(new S(this));
        this.r.measure(View.MeasureSpec.makeMeasureSpec(0, 0), View.MeasureSpec.makeMeasureSpec(0, 0));
        ValueAnimator ofInt3 = ValueAnimator.ofInt(new int[]{0, this.r.getMeasuredHeight()});
        ofInt3.setDuration(350);
        ofInt3.addUpdateListener(new T(this));
        ValueAnimator ofInt4 = ValueAnimator.ofInt(new int[]{0, 255});
        ofInt4.setDuration(350);
        ofInt4.addUpdateListener(new U(this));
        this.s.measure(View.MeasureSpec.makeMeasureSpec(0, 0), View.MeasureSpec.makeMeasureSpec(0, 0));
        ValueAnimator ofInt5 = ValueAnimator.ofInt(new int[]{0, this.s.getMeasuredHeight()});
        ofInt5.setDuration(350);
        ofInt5.addUpdateListener(new V(this));
        ValueAnimator ofInt6 = ValueAnimator.ofInt(new int[]{0, 255});
        ofInt6.setDuration(350);
        ofInt6.addUpdateListener(new z(this));
        ValueAnimator ofInt7 = ValueAnimator.ofInt(new int[]{((LinearLayout.LayoutParams) this.j.getLayoutParams()).topMargin, 0});
        ofInt7.setDuration(350);
        ofInt7.addUpdateListener(new A(this));
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) this.i.getLayoutParams();
        int dimensionPixelSize = this.f4162a.getResources().getDimensionPixelSize(R.dimen.gb_vc_audition_title_margin_top);
        int dimensionPixelSize2 = this.f4162a.getResources().getDimensionPixelSize(R.dimen.gb_vc_drop_layout_margin_bottom);
        ValueAnimator ofInt8 = ValueAnimator.ofInt(new int[]{layoutParams.topMargin, dimensionPixelSize});
        ofInt8.setDuration(350);
        ofInt8.addUpdateListener(new B(this));
        ValueAnimator ofInt9 = ValueAnimator.ofInt(new int[]{layoutParams.bottomMargin, dimensionPixelSize2});
        ofInt9.setDuration(350);
        ofInt9.addUpdateListener(new C(this));
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(new Animator[]{ofInt, ofInt2, ofInt3, ofInt4, ofInt5, ofInt6, ofInt7, ofInt8, ofInt9});
        animatorSet.start();
    }

    private void e() {
        boolean z2;
        if (this.w) {
            this.f.setImageResource(R.drawable.gb_voice_changer_down);
            d();
            z2 = false;
            this.e.setInstructSelected(false);
        } else {
            this.f.setImageResource(R.drawable.gb_voice_changer_up);
            c();
            z2 = true;
            this.e.setInstructSelected(true);
            ValueAnimator valueAnimator = this.u;
            if (valueAnimator != null && valueAnimator.isRunning()) {
                this.u.cancel();
            }
        }
        this.w = z2;
    }

    private void f() {
        this.g = LayoutInflater.from(this.f4162a).inflate(R.layout.gb_voice_changer_window_layout, (ViewGroup) null);
        C0393y.a(this.g, false);
        this.h = this.g.findViewById(R.id.view_layout);
        this.e = (AuditionView) this.g.findViewById(R.id.audition_view);
        this.e.setVoiceChangerWindow(this);
        this.i = this.g.findViewById(R.id.drop_layout);
        this.f = (ImageView) this.g.findViewById(R.id.drop_down);
        this.r = (TextView) this.g.findViewById(R.id.audition_title);
        this.s = (TextView) this.g.findViewById(R.id.window_title);
        this.j = this.g.findViewById(R.id.first_item);
        this.k = (VoiceModeView) this.g.findViewById(R.id.original_sound);
        this.k.setNormalIconRes(R.drawable.gb_vc_original_normal);
        this.k.setSelectedIconRes(R.drawable.gb_vc_original_selected);
        this.k.setModeTitle(R.string.gb_voice_changer_normal);
        this.l = (VoiceModeView) this.g.findViewById(R.id.loli_sound);
        this.l.setNormalIconRes(R.drawable.gb_vc_loli_normal);
        this.l.setSelectedIconRes(R.drawable.gb_vc_loli_selected);
        this.l.setModeTitle(R.string.gb_voice_changer_loli);
        this.m = (VoiceModeView) this.g.findViewById(R.id.lady_sound);
        this.m.setNormalIconRes(R.drawable.gb_vc_lady_normal);
        this.m.setSelectedIconRes(R.drawable.gb_vc_lady_selected);
        this.m.setModeTitle(R.string.gb_voice_changer_lady);
        this.n = (VoiceModeView) this.g.findViewById(R.id.cartoon_sound);
        this.n.setNormalIconRes(R.drawable.gb_vc_cartoon_normal);
        this.n.setSelectedIconRes(R.drawable.gb_vc_cartoon_selected);
        this.n.setModeTitle(R.string.gb_voice_changer_cartoon);
        this.o = (VoiceModeView) this.g.findViewById(R.id.robot_sound);
        this.o.setNormalIconRes(R.drawable.gb_vc_robot_normal);
        this.o.setSelectedIconRes(R.drawable.gb_vc_robot_selected);
        this.o.setModeTitle(R.string.gb_voice_changer_robot);
        this.p = (VoiceModeView) this.g.findViewById(R.id.men_sound);
        this.p.setNormalIconRes(R.drawable.gb_vc_men_normal);
        this.p.setSelectedIconRes(R.drawable.gb_vc_men_selected);
        this.p.setModeTitle(R.string.gb_voice_changer_men);
        this.t = (TextView) this.g.findViewById(R.id.vc_opened);
        this.k.setOnClickListener(this);
        this.l.setOnClickListener(this);
        this.m.setOnClickListener(this);
        this.n.setOnClickListener(this);
        this.o.setOnClickListener(this);
        this.p.setOnClickListener(this);
        this.i.setOnClickListener(this);
        this.g.setOnClickListener(this);
        this.h.setOnTouchListener(this);
        g();
        if (ma.e()) {
            h();
            ma.a(false);
        }
        this.e.measure(View.MeasureSpec.makeMeasureSpec(0, 0), View.MeasureSpec.makeMeasureSpec(0, 0));
        this.z = this.e.getMeasuredHeight();
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) this.e.getLayoutParams();
        layoutParams.height = 0;
        this.e.setLayoutParams(layoutParams);
    }

    private void g() {
        VoiceModeView voiceModeView;
        String d2 = ma.d();
        if (d2.equals("original")) {
            this.k.setIonBgStatus(1);
            voiceModeView = this.k;
        } else if (d2.equals("loli")) {
            this.l.setIonBgStatus(1);
            voiceModeView = this.l;
        } else if (d2.equals("lady")) {
            this.m.setIonBgStatus(1);
            voiceModeView = this.m;
        } else if (d2.equals("men")) {
            this.p.setIonBgStatus(1);
            voiceModeView = this.p;
        } else if (d2.equals("cartoon")) {
            this.n.setIonBgStatus(1);
            voiceModeView = this.n;
        } else if (d2.equals("robot")) {
            this.o.setIonBgStatus(1);
            voiceModeView = this.o;
        } else {
            return;
        }
        this.q = voiceModeView;
    }

    private void h() {
        this.u = ValueAnimator.ofFloat(new float[]{0.5f, 1.0f, 1.0f, 0.5f, 0.5f, 1.0f});
        this.u.setDuration(3000);
        this.u.setInterpolator(new LinearInterpolator());
        this.u.addUpdateListener(new O(this));
        this.u.start();
    }

    private void i() {
        ValueAnimator valueAnimator = this.v;
        if (valueAnimator != null && valueAnimator.isRunning()) {
            this.v.cancel();
        }
    }

    public void a() {
        WindowManager.LayoutParams layoutParams = this.f4165d;
        layoutParams.type = 2003;
        layoutParams.format = -3;
        layoutParams.flags = 264;
        layoutParams.gravity = 17;
        layoutParams.width = -1;
        layoutParams.height = -1;
        layoutParams.windowAnimations = R.style.gb_voicechanger_anim;
        f();
        this.f4163b.addView(this.g, this.f4165d);
        this.B.registerReceiver(this.C, new IntentFilter("GAMEBOX_WINDOW_REMOVED"));
    }

    public void a(int i2) {
        this.q.setIonBgStatus(i2);
    }

    public void a(int i2, boolean z2) {
        if (z2 || !this.x) {
            if (this.x) {
                this.t.setVisibility(8);
                this.A.removeCallbacks(this.D);
                this.x = false;
            }
            this.t.setText(i2);
            this.t.setVisibility(0);
            AlphaAnimation alphaAnimation = new AlphaAnimation(0.0f, 1.0f);
            alphaAnimation.setDuration(500);
            this.t.startAnimation(alphaAnimation);
            this.x = true;
            this.A.postDelayed(this.D, 1000);
        }
    }

    public void a(long j2) {
        this.q.setIonBgStatus(2);
        this.v = ValueAnimator.ofFloat(new float[]{0.0f, 100.0f});
        this.v.setDuration(j2);
        this.v.addUpdateListener(new P(this));
        this.v.start();
    }

    public void a(boolean z2) {
        this.y = z2;
    }

    public void b() {
        if (this.g != null) {
            this.e.a();
            this.f4163b.removeView(this.g);
            this.B.unregisterReceiver(this.C);
            this.A.removeCallbacksAndMessages((Object) null);
        }
    }

    public void onClick(View view) {
        String str;
        VoiceModeView voiceModeView;
        switch (view.getId()) {
            case R.id.cartoon_sound /*2131296587*/:
                voiceModeView = this.n;
                str = "cartoon";
                break;
            case R.id.drop_layout /*2131296733*/:
                e();
                break;
            case R.id.lady_sound /*2131297161*/:
                voiceModeView = this.m;
                str = "lady";
                break;
            case R.id.loli_sound /*2131297282*/:
                voiceModeView = this.l;
                str = "loli";
                break;
            case R.id.men_sound /*2131297316*/:
                voiceModeView = this.p;
                str = "men";
                break;
            case R.id.original_sound /*2131297414*/:
                voiceModeView = this.k;
                str = "original";
                break;
            case R.id.robot_sound /*2131297566*/:
                voiceModeView = this.o;
                str = "robot";
                break;
        }
        a(voiceModeView, str);
        if (view == this.g) {
            b();
        }
    }

    public boolean onTouch(View view, MotionEvent motionEvent) {
        return this.h == view;
    }
}
