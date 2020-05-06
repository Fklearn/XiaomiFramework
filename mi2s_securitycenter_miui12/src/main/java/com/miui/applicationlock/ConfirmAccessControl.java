package com.miui.applicationlock;

import android.animation.ValueAnimator;
import android.app.ActivityOptions;
import android.app.KeyguardManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.ContentObserver;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.os.UserHandle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.view.Window;
import android.view.accessibility.AccessibilityManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import b.b.c.j.B;
import b.b.c.j.i;
import b.b.c.j.r;
import b.b.n.e;
import b.b.n.g;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.miui.analytics.AnalyticsUtil;
import com.miui.applicationlock.a.h;
import com.miui.applicationlock.c.C;
import com.miui.applicationlock.c.C0259c;
import com.miui.applicationlock.c.E;
import com.miui.applicationlock.c.K;
import com.miui.applicationlock.c.o;
import com.miui.applicationlock.c.p;
import com.miui.applicationlock.c.q;
import com.miui.applicationlock.c.s;
import com.miui.applicationlock.c.z;
import com.miui.applicationlock.widget.C0308a;
import com.miui.applicationlock.widget.LockPatternView;
import com.miui.applicationlock.widget.MiuiNumericInputView;
import com.miui.applicationlock.widget.PasswordUnlockMediator;
import com.miui.applicationlock.widget.WrapMaml;
import com.miui.common.customview.gif.GifImageView;
import com.miui.gamebooster.globalgame.view.RoundedDrawable;
import com.miui.gamebooster.m.C0384o;
import com.miui.networkassistant.config.Constants;
import com.miui.securitycenter.R;
import com.miui.support.provider.f;
import com.miui.systemAdSolution.common.AdTrackType;
import java.lang.ref.WeakReference;
import miui.app.Activity;
import miui.app.AlertDialog;
import miui.security.SecurityManager;

public class ConfirmAccessControl extends b.b.c.c.a implements View.OnClickListener {

    /* renamed from: a  reason: collision with root package name */
    private static long f3135a;
    private IBinder A;
    /* access modifiers changed from: private */
    public DefaultTrackSelector.Parameters Aa;
    boolean B;
    /* access modifiers changed from: private */
    public MediaSource Ba;
    /* access modifiers changed from: private */
    public C0259c C;
    /* access modifiers changed from: private */
    public boolean Ca;
    private int D;
    private ViewGroup Da;
    private final Handler E = new Handler();
    /* access modifiers changed from: private */
    public long Ea;
    /* access modifiers changed from: private */
    public boolean F;
    private PlayerView Fa;
    /* access modifiers changed from: private */
    public GifImageView G;
    private Uri Ga;
    private boolean H;
    /* access modifiers changed from: private */
    public boolean Ha;
    private com.miui.applicationlock.b.b I;
    /* access modifiers changed from: private */
    public boolean Ia;
    private e J;
    private boolean Ja;
    private Runnable K;
    /* access modifiers changed from: private */
    public C Ka;
    private Runnable L;
    /* access modifiers changed from: private */
    public ImageView La;
    private KeyguardManager M;
    /* access modifiers changed from: private */
    public boolean Ma;
    /* access modifiers changed from: private */
    public AlertDialog N;
    private boolean Na = true;
    /* access modifiers changed from: private */
    public int O;
    private boolean Oa;
    /* access modifiers changed from: private */
    public int P;
    /* access modifiers changed from: private */
    public boolean Pa;
    private Resources Q;
    private View Qa;
    /* access modifiers changed from: private */
    public boolean R;
    private boolean Ra;
    /* access modifiers changed from: private */
    public ImageView S;
    private boolean Sa;
    /* access modifiers changed from: private */
    public int T;
    private boolean Ta = false;
    /* access modifiers changed from: private */
    public boolean U;
    private final BroadcastReceiver Ua = new X(this);
    public boolean V;
    private p Va = new C0280ia(this);
    /* access modifiers changed from: private */
    public boolean W;
    /* access modifiers changed from: private */
    public final z Wa = new b(this, (X) null);
    /* access modifiers changed from: private */
    public boolean X;
    private RelativeLayout Y;
    private RelativeLayout Z;
    private LinearLayout.LayoutParams aa;

    /* renamed from: b  reason: collision with root package name */
    C0308a f3136b;
    private EditText ba;
    /* access modifiers changed from: private */

    /* renamed from: c  reason: collision with root package name */
    public TextView f3137c;
    String ca;
    /* access modifiers changed from: private */

    /* renamed from: d  reason: collision with root package name */
    public TextView f3138d;
    MiuiNumericInputView da;
    /* access modifiers changed from: private */
    public TextView e;
    /* access modifiers changed from: private */
    public AccessibilityManager ea;
    private View f;
    private boolean fa;
    protected ImageView g;
    /* access modifiers changed from: private */
    public PasswordUnlockMediator ga;
    protected ImageView h;
    /* access modifiers changed from: private */
    public View ha;
    private LinearLayout i;
    /* access modifiers changed from: private */
    public TextView ia;
    private CountDownTimer j;
    private ImageView ja;
    private ContentObserver k;
    private TextView ka;
    /* access modifiers changed from: private */
    public Intent l;
    private int la;
    private ActivityOptions m;
    /* access modifiers changed from: private */
    public Button ma;
    private b.b.o.f.a.a n;
    private g.a na;
    private CharSequence o;
    private g.a oa;
    private CharSequence p;
    private g.a pa;
    protected String q;
    private PlayerView qa;
    /* access modifiers changed from: private */
    public boolean r;
    private ImageView ra;
    private SecurityManager s;
    private PlayerView sa;
    /* access modifiers changed from: private */
    public E t;
    private ImageView ta;
    private int u = 0;
    private ImageView ua;
    /* access modifiers changed from: private */
    public int v;
    private int va;
    private boolean w;
    private TextView wa;
    /* access modifiers changed from: private */
    public boolean x;
    private ImageView xa;
    private boolean y;
    /* access modifiers changed from: private */
    public SimpleExoPlayer ya;
    /* access modifiers changed from: private */
    public boolean z = false;
    /* access modifiers changed from: private */
    public DefaultTrackSelector za;

    private static class a implements q {

        /* renamed from: a  reason: collision with root package name */
        private final WeakReference<ConfirmAccessControl> f3139a;

        private a(ConfirmAccessControl confirmAccessControl) {
            this.f3139a = new WeakReference<>(confirmAccessControl);
        }

        /* synthetic */ a(ConfirmAccessControl confirmAccessControl, X x) {
            this(confirmAccessControl);
        }

        public void a() {
            Activity activity = (ConfirmAccessControl) this.f3139a.get();
            if (activity != null && activity.t() == 0) {
                int g = o.g((Context) activity) + 1;
                o.b((Context) activity, g);
                if (ConfirmAccessControl.i(activity) >= 5 || g >= 5) {
                    int unused = activity.T = 0;
                    activity.a(4, 0, 0);
                    activity.f3137c.setText(R.string.access_control_need_to_unlock_nofingerprint);
                    o.a(activity.ea, activity.getResources().getString(R.string.access_control_need_to_unlock_nofingerprint));
                    activity.t.a();
                    if (i.d()) {
                        activity.a(0, 8, 4, 4);
                        return;
                    }
                    return;
                }
                TextView d2 = (!i.d() || !activity.Ma) ? activity.f3137c : activity.ia;
                d2.setVisibility(0);
                d2.setText(R.string.lockpattern_access_need_to_unlock_wrong_fingerprint);
                o.b((View) d2);
                o.a(activity.ea, activity.getResources().getString(R.string.lockpattern_access_need_to_unlock_wrong_fingerprint));
                o.j(activity);
            }
        }

        public void a(int i) {
            ConfirmAccessControl confirmAccessControl = (ConfirmAccessControl) this.f3139a.get();
            if (confirmAccessControl != null && confirmAccessControl.t() == 0) {
                if (o.c(i, confirmAccessControl.O)) {
                    confirmAccessControl.a(false);
                    confirmAccessControl.t.a();
                    confirmAccessControl.N();
                    return;
                }
                boolean unused = confirmAccessControl.Pa = true;
                int unused2 = confirmAccessControl.P = i;
                confirmAccessControl.a(4, 0, 4);
                confirmAccessControl.f3137c.setText(R.string.access_control_failed_need_to_unlock_nofingerprint);
                o.a(confirmAccessControl.ea, confirmAccessControl.getResources().getString(R.string.access_control_failed_need_to_unlock_nofingerprint));
                confirmAccessControl.ga.setVisibility(0);
                if (i.d()) {
                    confirmAccessControl.ha.setVisibility(4);
                    confirmAccessControl.ia.setVisibility(4);
                }
            }
        }
    }

    private static class b implements z {

        /* renamed from: a  reason: collision with root package name */
        private final WeakReference<ConfirmAccessControl> f3140a;

        private b(ConfirmAccessControl confirmAccessControl) {
            this.f3140a = new WeakReference<>(confirmAccessControl);
        }

        /* synthetic */ b(ConfirmAccessControl confirmAccessControl, X x) {
            this(confirmAccessControl);
        }

        public void a() {
            ConfirmAccessControl confirmAccessControl = (ConfirmAccessControl) this.f3140a.get();
            if (confirmAccessControl != null) {
                Log.d("ConfirmAccessControl", " restartFaceUnlock ");
                if (!confirmAccessControl.Ma) {
                    confirmAccessControl.L();
                    confirmAccessControl.La.setVisibility(0);
                    if (confirmAccessControl.S != null) {
                        confirmAccessControl.S.setVisibility(4);
                    }
                    if (i.d() && confirmAccessControl.x) {
                        confirmAccessControl.ga.setVisibility(8);
                    }
                    confirmAccessControl.f3137c.setVisibility(0);
                    confirmAccessControl.f3137c.setText(R.string.face_unlock_face_start_title);
                }
            }
        }

        public void a(String str) {
            Log.d("ConfirmAccessControl", " onFaceHelp tip:" + str);
        }

        /* JADX WARNING: type inference failed for: r3v4, types: [android.content.Context, com.miui.applicationlock.ConfirmAccessControl] */
        public void a(boolean z) {
            TextView d2;
            int i;
            Log.d("ConfirmAccessControl", " onFaceAuthFailed ");
            ? r3 = (ConfirmAccessControl) this.f3140a.get();
            if (r3 != 0) {
                if (r3.z) {
                    d2 = r3.f3137c;
                    i = R.string.lockpattern_too_many_failed_confirmation_attempts_header;
                } else if (!r3.F) {
                    r3.p();
                    if (o.g((Context) r3) == 5) {
                        d2 = r3.f3137c;
                        i = R.string.access_control_need_to_unlock_nofingerprint;
                    } else {
                        return;
                    }
                } else {
                    return;
                }
                d2.setText(i);
            }
        }

        public void b() {
            ConfirmAccessControl confirmAccessControl = (ConfirmAccessControl) this.f3140a.get();
            if (confirmAccessControl != null) {
                confirmAccessControl.a(false);
                confirmAccessControl.N();
            }
        }

        public void c() {
            Log.d("ConfirmAccessControl", " onFaceLocked ");
        }

        public void d() {
            ConfirmAccessControl confirmAccessControl = (ConfirmAccessControl) this.f3140a.get();
            if (confirmAccessControl != null) {
                Log.d("ConfirmAccessControl", " onFaceStart ");
                confirmAccessControl.La.setVisibility(0);
                if (confirmAccessControl.S != null) {
                    confirmAccessControl.S.setVisibility(4);
                }
                confirmAccessControl.f3137c.setText(R.string.face_unlock_face_start_title);
            }
        }
    }

    private class c extends Player.DefaultEventListener {
        private c() {
        }

        /* synthetic */ c(ConfirmAccessControl confirmAccessControl, X x) {
            this();
        }

        public void onPlayerStateChanged(boolean z, int i) {
            if (i == 4) {
                boolean unused = ConfirmAccessControl.this.Ca = true;
            }
        }
    }

    protected enum d {
        NeedToUnlock,
        NeedToUnlockWrong,
        LockedOut
    }

    private void A() {
        this.Ka = C.a(getApplicationContext());
        this.y = this.C.h() && this.Ka.e() && this.Ka.a() && t() == 0;
        if (this.y) {
            this.La = (ImageView) findViewById(R.id.face_unlock_smile);
            this.La.setVisibility(0);
        }
    }

    private void B() {
        this.x = G();
        C();
        x();
        w();
    }

    /* JADX WARNING: type inference failed for: r5v0, types: [android.content.Context, com.miui.applicationlock.ConfirmAccessControl, android.view.View$OnClickListener, miui.app.Activity] */
    private void C() {
        int i2;
        if (this.x) {
            if (i.d()) {
                this.ha = findViewById(R.id.finger_password_switch);
                this.ha.setOnClickListener(this);
                this.ja = (ImageView) findViewById(R.id.fod_finger_icon);
                this.ka = (TextView) findViewById(R.id.fod_finger_title);
                this.ia = (TextView) findViewById(R.id.fod_finger_tips);
                DisplayMetrics displayMetrics = new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getRealMetrics(displayMetrics);
                int[] f2 = o.f();
                FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) this.ia.getLayoutParams();
                if (f2 != null) {
                    layoutParams.bottomMargin = (displayMetrics.heightPixels - f2[1]) + getResources().getDimensionPixelOffset(R.dimen.applock_fod_tips_margin_finger_icon);
                    if (!i.h(this)) {
                        i2 = layoutParams.bottomMargin - getResources().getDimensionPixelOffset(b.b.c.b.a());
                    }
                    this.ia.setLayoutParams(layoutParams);
                    return;
                }
                i2 = getResources().getDimensionPixelOffset(R.dimen.applock_fod_tips_margin_finger_icon) * 2;
                layoutParams.bottomMargin = i2;
                this.ia.setLayoutParams(layoutParams);
                return;
            }
            this.S = (ImageView) findViewById(R.id.fingerIcon);
        }
    }

    /* JADX WARNING: type inference failed for: r10v0, types: [android.content.Context, com.miui.applicationlock.ConfirmAccessControl, android.view.View$OnClickListener, miui.app.Activity, android.app.Activity] */
    private void D() {
        ImageView imageView;
        if (E()) {
            com.miui.applicationlock.b.b bVar = this.I;
            if (bVar != null && bVar.a() == 2) {
                this.g.setOnClickListener(this);
            }
            this.G = (GifImageView) findViewById(R.id.image_skin_flag_top);
            findViewById(R.id.expended_click_area).setOnClickListener(this);
            this.xa = (ImageView) findViewById(R.id.sign_red_point);
            if (this.J != null && this.I.f() && this.J.b("applicationlock", this.I.b())) {
                this.xa.setVisibility(0);
            }
            if (this.na != null) {
                ((ViewStub) findViewById(R.id.full_screen_ad_maml)).inflate();
                ((WrapMaml) findViewById(R.id.action_maml)).setLocalResourcePath(this.na.c());
                getWindow().getDecorView().setBackgroundColor(RoundedDrawable.DEFAULT_BORDER_COLOR);
                this.J.a("applicationlock", "com.miui.securitycenter_skinview", AdTrackType.Type.TRACK_VIEW, 211);
            }
            if (this.oa != null) {
                this.f.setBackgroundColor(RoundedDrawable.DEFAULT_BORDER_COLOR);
                ((ViewStub) findViewById(R.id.top_ad_video)).inflate();
                this.qa = (PlayerView) findViewById(R.id.top_ad_video_view);
                a(this.qa, Uri.parse(this.oa.c()));
                this.ra = (ImageView) findViewById(R.id.topOnOrMuteSwitch);
                this.ra.setOnClickListener(this);
                o.a(this.ca, (android.app.Activity) this);
                this.J.a("applicationlock", "com.miui.securitycenter_skinview", AdTrackType.Type.TRACK_VIEW, 212);
                this.Fa = this.qa;
                this.Ga = Uri.parse(this.oa.c());
                this.Qa.setVisibility(8);
            }
            if (this.pa != null) {
                ((ViewStub) findViewById(R.id.full_screen_ad_video)).inflate();
                if ("pattern".equals(this.ca)) {
                    this.ua = (ImageView) findViewById(R.id.full_screen_mute_button_backup);
                    this.ua.setVisibility(0);
                    imageView = this.ua;
                } else {
                    this.ta = (ImageView) findViewById(R.id.full_screen_on_or_mute_switch);
                    this.ta.setVisibility(0);
                    imageView = this.ta;
                }
                imageView.setOnClickListener(this);
                this.sa = (PlayerView) findViewById(R.id.full_screen_ad_player_view);
                this.sa.setShutterBackgroundColor(-1);
                a(this.sa, Uri.parse(this.pa.c()));
                o.a(this.ca, (android.app.Activity) this);
                this.J.a("applicationlock", "com.miui.securitycenter_skinview", AdTrackType.Type.TRACK_VIEW, 213);
                this.Fa = this.sa;
                this.Ga = Uri.parse(this.pa.c());
            }
            if (this.oa == null && H()) {
                LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) this.i.getLayoutParams();
                layoutParams.topMargin = getResources().getDimensionPixelSize(R.dimen.applock_textView_full_screen_margin_top);
                this.i.setLayoutParams(layoutParams);
            }
            if (H()) {
                if (!this.y || this.B) {
                    ((ViewGroup) findViewById(R.id.finger_password_switch)).setVisibility(8);
                } else {
                    this.La.setBackgroundResource(R.drawable.face_display);
                    this.f3137c.setTextColor(this.Q.getColor(R.color.unlock_text_dark));
                }
                if ("mixed".equals(this.ca)) {
                    EditText editText = (EditText) findViewById(R.id.miui_mixed_password_input_field);
                    LinearLayout.LayoutParams layoutParams2 = (LinearLayout.LayoutParams) editText.getLayoutParams();
                    layoutParams2.topMargin = 0;
                    editText.setLayoutParams(layoutParams2);
                }
            }
            com.miui.applicationlock.b.a.a(this, this.I, this.G, this.g);
            this.f3136b.a(this, this.I);
            g.a a2 = this.I.a(209);
            if (a2 != null) {
                this.H = a2.e();
            }
            this.J.a("applicationlock", "com.miui.securitycenter_skinview", AdTrackType.Type.TRACK_VIEW, -1);
            h.e();
        }
        if (this.G != null && com.miui.applicationlock.b.a.a(this.I) && !this.H) {
            new Handler().postDelayed(new C0268ca(this), 1000);
        }
    }

    private boolean E() {
        com.miui.applicationlock.b.b bVar = this.I;
        if (bVar == null) {
            return false;
        }
        this.J = bVar.d();
        return (this.J == null || this.I.c() == null || !this.I.e()) ? false : true;
    }

    /* access modifiers changed from: private */
    public boolean F() {
        try {
            return Build.VERSION.SDK_INT >= 24 && this.r && ((Boolean) b.b.o.g.e.a((Object) this, "isInMultiWindowMode", (Class<?>) android.app.Activity.class, (Class<?>[]) null, new Object[0])).booleanValue();
        } catch (Exception e2) {
            Log.e("ConfirmAccessControl", "isRealInMultiWindow", e2);
            return false;
        }
    }

    /* JADX WARNING: type inference failed for: r3v0, types: [android.content.Context, com.miui.applicationlock.ConfirmAccessControl] */
    private boolean G() {
        return o.g((Context) this) < 5 && this.t.d() && this.t.c() && this.C.i() && TransitionHelper.a(this);
    }

    private boolean H() {
        return !(this.na == null && this.oa == null && this.pa == null) && E();
    }

    private void I() {
        if (!this.C.d()) {
            a(true);
            if (this.r) {
                this.C.a(false);
            }
        }
    }

    private void J() {
        this.Sa = this.C.c() && (this.C.b() == null || !K.c(getApplicationContext()) || !TextUtils.equals(this.C.b(), K.a(getApplicationContext())));
        if (this.Sa) {
            AlertDialog alertDialog = this.N;
            if (alertDialog != null) {
                alertDialog.dismiss();
            }
            this.N = null;
            a(getResources().getString(R.string.password_promotion_not_login_xiaomi_account_message), getResources().getString(R.string.password_promotion_postbutton_text), 29000);
        }
    }

    private void K() {
        CountDownTimer countDownTimer = this.j;
        if (countDownTimer != null && this.z) {
            countDownTimer.cancel();
            q();
        }
    }

    /* access modifiers changed from: private */
    public void L() {
        if (!this.Ma || this.F) {
            this.Ka.a((Runnable) new Q(this));
        }
    }

    /* JADX WARNING: type inference failed for: r7v0, types: [android.content.Context, com.miui.applicationlock.ConfirmAccessControl, miui.app.Activity] */
    private void M() {
        M m2 = new M(this, 20000, 1000);
        AlertDialog create = new AlertDialog.Builder(this).setTitle(getResources().getString(R.string.reset_data_title)).setMessage(getResources().getString(R.string.reset_data_dialog_message)).setNegativeButton(getResources().getString(R.string.bind_xiaomi_account_cancel), new P(this, m2)).setPositiveButton(getResources().getString(R.string.reset_data_dialog_ok), new O(this)).setOnDismissListener(new N(this, m2)).create();
        create.show();
        this.ma = create.getButton(-1);
        this.ma.setClickable(false);
        m2.start();
    }

    /* access modifiers changed from: private */
    public void N() {
        this.v = 0;
        o.c(getApplicationContext(), 0);
        Log.i("ConfirmAccessControl", "clear wrong attempts: ");
    }

    /* JADX WARNING: type inference failed for: r2v0, types: [android.content.Context, com.miui.applicationlock.ConfirmAccessControl] */
    private void O() {
        View.OnClickListener onClickListener;
        TextView textView;
        if (this.C.b() == null || !K.c(this) || !TextUtils.equals(this.C.b(), K.a(this))) {
            textView = this.e;
            onClickListener = new C0296qa(this);
        } else {
            textView = this.e;
            onClickListener = new C0294pa(this);
        }
        textView.setOnClickListener(onClickListener);
    }

    /* access modifiers changed from: private */
    public void P() {
        Runnable runnable = this.K;
        if (runnable != null) {
            this.E.removeCallbacks(runnable);
        }
        this.w = false;
        f3135a = SystemClock.elapsedRealtime();
        E e2 = this.t;
        if (e2 != null) {
            e2.a();
        }
    }

    private void Q() {
        int i2;
        Resources resources;
        ImageView imageView;
        if (this.B) {
            resources = this.Q;
            i2 = R.color.unlock_text_light;
        } else {
            resources = this.Q;
            i2 = R.color.unlock_text_dark;
        }
        this.va = resources.getColor(i2);
        this.f3136b.setLightMode(this.B);
        ImageView imageView2 = this.La;
        if (imageView2 != null && this.B) {
            imageView2.setBackgroundResource(R.drawable.face_display_black);
        }
        if ("numeric".equals(this.ca)) {
            this.da.a(this.B);
        }
        this.f3136b.e();
        this.f3136b.setAppPage(true);
        o.a(this.B, getWindow());
        this.f3137c.setTextColor(this.va);
        this.f3138d.setTextColor(this.va);
        if (!"numeric".equals(this.ca)) {
            this.e.setTextColor(this.va);
            this.e.setVisibility(4);
        } else {
            O();
        }
        if ("mixed".equals(this.ca)) {
            this.ba.setTextColor(this.va);
            this.ba.setHintTextColor(getResources().getColor(R.color.applock_mix_edit_hint_color));
        }
        if (H()) {
            this.wa.setTextColor(this.va);
        }
        if (this.x && this.B && (imageView = this.S) != null) {
            imageView.setImageDrawable(this.Q.getDrawable(R.drawable.fingerprint_light));
        }
        x();
        if (!this.R) {
            b(this.B);
        }
    }

    /* access modifiers changed from: private */
    public void a(int i2) {
        PlayerView playerView = this.sa;
        if (playerView != null) {
            playerView.setVisibility(i2);
        }
        PlayerView playerView2 = this.qa;
        if (playerView2 != null) {
            playerView2.setVisibility(i2);
        }
    }

    /* access modifiers changed from: private */
    public void a(int i2, int i3, int i4) {
        if (this.S != null && !H()) {
            this.S.setVisibility(i2);
        }
        this.f3137c.setVisibility(i3);
        this.f3138d.setVisibility(i4);
    }

    /* access modifiers changed from: private */
    public void a(int i2, int i3, int i4, int i5) {
        this.ga.setVisibility(i2);
        this.ha.setVisibility(i3);
        this.ja.setVisibility(i4);
        if (this.B) {
            this.ja.setBackground(getResources().getDrawable(R.drawable.fod_split_finger_icon_small_dark));
        }
        this.ka.setTextColor(this.va);
        this.ia.setTextColor(this.va);
        this.ia.setVisibility(i5);
    }

    /* access modifiers changed from: private */
    public void a(long j2) {
        ImageView imageView;
        View view;
        if (!this.z) {
            a(d.LockedOut);
            if (!H()) {
                this.f.setBackgroundColor(1677721600);
            }
            P();
            Log.d("ConfirmAccessControl", "unregisterFingerprint 6");
            ImageView imageView2 = this.S;
            if (imageView2 != null) {
                imageView2.setVisibility(4);
            }
            if (i.d() && (view = this.ha) != null) {
                view.setClickable(false);
                this.ga.setVisibility(0);
            }
            if (this.y && (imageView = this.La) != null) {
                imageView.setVisibility(8);
                o();
            }
            this.j = new T(this, j2 - SystemClock.elapsedRealtime(), 1000);
            if (!this.z) {
                this.f3137c.setVisibility(0);
                this.f3137c.setText(R.string.lockpattern_too_many_failed_confirmation_attempts_header);
                this.j.start();
            }
        }
    }

    private void a(Intent intent) {
        this.l = null;
        this.A = null;
        boolean z2 = false;
        this.r = false;
        if (intent != null) {
            this.o = intent.getCharSequenceExtra("com.android.settings.ConfirmLockPattern.header_wrong");
            this.p = intent.getCharSequenceExtra("com.android.settings.ConfirmLockPattern.footer_wrong");
            this.q = intent.getStringExtra("android.intent.extra.shortcut.NAME");
            this.l = (Intent) intent.getParcelableExtra("android.intent.extra.INTENT");
            Bundle extras = intent.getExtras();
            if (!(Build.VERSION.SDK_INT <= 25 || extras == null || this.C.a() == 0)) {
                try {
                    this.m = (ActivityOptions) b.b.o.g.e.a(Class.forName("android.app.ActivityOptions"), ActivityOptions.class, "fromBundle", (Class<?>[]) new Class[]{Bundle.class}, extras);
                } catch (Exception e2) {
                    Log.e("ConfirmAccessControl", "fromBundle exception: ", e2);
                }
            }
            this.D = intent.getIntExtra("originating_uid", -1);
            try {
                this.A = (IBinder) b.b.o.g.e.a((Object) intent, IBinder.class, "getIBinderExtra", (Class<?>[]) new Class[]{String.class}, "android.app.extra.PROTECTED_APP_TOKEN");
            } catch (Exception e3) {
                Log.e("ConfirmAccessControl", "getIBinderExtra exception: ", e3);
            }
            this.r = "miui.intent.action.CHECK_ACCESS_CONTROL".equals(intent.getAction());
            r();
            if (!this.Oa) {
                m();
                if (C0312y.f3467a.contains(this.q) && !this.M.isKeyguardSecure()) {
                    try {
                        if (this.l != null && this.l.getBooleanExtra("StartActivityWhenLocked", false)) {
                            z2 = true;
                        }
                    } catch (Throwable th) {
                        Log.w("ConfirmAccessControl", "Fail to read StartActivityWhenLocked from intent", th);
                    }
                    Window window = getWindow();
                    if (z2) {
                        window.addFlags(524288);
                    } else {
                        window.clearFlags(524288);
                    }
                }
                if (!TextUtils.isEmpty(this.q)) {
                    h.i(this.q);
                }
            }
        }
    }

    private void a(Configuration configuration) {
        if (this.Da == null) {
            ((ViewStub) findViewById(R.id.full_screen_split_background)).inflate();
            this.Da = (ViewGroup) findViewById(R.id.split_screen_layout);
        }
        if (configuration.screenHeightDp >= getResources().getInteger(R.integer.applock_split_screen_height_full_screen)) {
            ViewGroup viewGroup = this.Da;
            if (viewGroup != null) {
                viewGroup.setVisibility(8);
                this.f.setVisibility(0);
                SimpleExoPlayer simpleExoPlayer = this.ya;
                if (simpleExoPlayer != null) {
                    simpleExoPlayer.setPlayWhenReady(true);
                }
            }
            this.Ia = true;
            this.R = false;
            a(0);
            return;
        }
        this.R = true;
        this.f.setVisibility(8);
        SimpleExoPlayer simpleExoPlayer2 = this.ya;
        if (simpleExoPlayer2 != null) {
            simpleExoPlayer2.setPlayWhenReady(false);
        }
    }

    private void a(Window window, float f2, float f3) {
        ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{f2, f3});
        ofFloat.cancel();
        ofFloat.setDuration(260);
        ofFloat.addUpdateListener(new C0284ka(this, window));
        ofFloat.start();
    }

    private void a(ImageView imageView) {
        int i2;
        SimpleExoPlayer simpleExoPlayer = this.ya;
        if (simpleExoPlayer != null && imageView != null) {
            if (simpleExoPlayer.getVolume() == 0.0f) {
                this.ya.setVolume(1.0f);
                this.Ha = true;
                i2 = R.drawable.sound_on;
            } else {
                this.ya.setVolume(0.0f);
                this.Ha = false;
                i2 = R.drawable.sound_off;
            }
            imageView.setBackgroundResource(i2);
        }
    }

    private void a(ImageView imageView, Drawable drawable) {
        if (this.D == 999) {
            r.a("pkg_icon_xspace://".concat(this.q), imageView, r.f);
        } else {
            imageView.setImageDrawable(drawable);
        }
    }

    private void a(PlayerView playerView, Uri uri) {
        new C0270da(this, uri, playerView).execute(new Void[0]);
    }

    /* access modifiers changed from: private */
    public void a(d dVar) {
        TextView textView;
        CharSequence charSequence;
        GifImageView gifImageView;
        int i2 = C0282ja.f3356a[dVar.ordinal()];
        if (i2 == 1) {
            this.f3136b.setEnabled(true);
            this.f3136b.g();
            if (this.r && !"numeric".equals(this.ca)) {
                this.e.setVisibility(4);
                GifImageView gifImageView2 = this.G;
                if (gifImageView2 != null) {
                    gifImageView2.setVisibility(0);
                }
            }
            View view = this.ha;
            if (view != null) {
                view.setClickable(true);
            }
        } else if (i2 == 2) {
            this.f3137c.setVisibility(0);
            CharSequence charSequence2 = this.o;
            if (charSequence2 != null) {
                this.f3137c.setText(charSequence2);
            } else {
                this.f3137c.setText(R.string.lockpattern_access_need_to_unlock_wrong_pattern);
            }
            if (this.p != null) {
                this.f3138d.setVisibility(0);
                textView = this.f3138d;
                charSequence = this.p;
            } else {
                textView = this.f3138d;
                charSequence = null;
            }
            textView.setText(charSequence);
            this.f3136b.setDisplayMode(LockPatternView.b.Wrong);
            this.f3136b.setEnabled(true);
            this.f3136b.g();
        } else if (i2 == 3) {
            this.f3136b.d();
            this.f3136b.setEnabled(false);
            this.e.setVisibility(0);
            O();
            if (this.r && (gifImageView = this.G) != null) {
                gifImageView.setVisibility(4);
            }
        }
        TextView textView2 = this.f3137c;
        textView2.announceForAccessibility(textView2.getText());
    }

    /* JADX WARNING: type inference failed for: r3v0, types: [android.content.Context, com.miui.applicationlock.ConfirmAccessControl, miui.app.Activity] */
    private void a(String str, Intent intent, int i2) {
        P();
        Log.d("ConfirmAccessControl", "unregisterFingerprint 9");
        new AlertDialog.Builder(this).setTitle(getResources().getString(R.string.password_forget_pattern_title)).setMessage(str).setNegativeButton(getResources().getString(R.string.bind_xiaomi_account_cancel), new C0278ha(this)).setPositiveButton(getResources().getString(i2), new C0276ga(this, intent)).setOnDismissListener(new C0274fa(this)).create().show();
    }

    /* JADX WARNING: type inference failed for: r5v0, types: [android.content.Context, com.miui.applicationlock.ConfirmAccessControl, miui.app.Activity] */
    private void a(String str, String str2, int i2) {
        P();
        Log.d("ConfirmAccessControl", "unregisterFingerprint 8");
        View inflate = getLayoutInflater().inflate(R.layout.confirm_bind_account_dialog, (ViewGroup) null);
        this.N = new AlertDialog.Builder(this).setTitle(R.string.password_promotion_title).setNegativeButton(getResources().getString(R.string.bind_xiaomi_account_cancel), new C0253aa(this, i2)).setPositiveButton(str2, new Z(this, i2)).setView(inflate).create();
        ((TextView) inflate.findViewById(R.id.confirm_bind_account_message)).setText(str);
        CheckBox checkBox = (CheckBox) inflate.findViewById(R.id.confirm_bind_account_checkBox);
        checkBox.setOnClickListener(new C0255ba(this, checkBox));
        if (this.N != null && this.Na && !isFinishing() && !isDestroyed()) {
            try {
                this.N.show();
            } catch (Exception e2) {
                Log.e("ConfirmAccessControl", "bind account dialog show failed", e2);
                AnalyticsUtil.trackException(e2);
            }
        }
    }

    /* JADX WARNING: type inference failed for: r12v0, types: [android.content.Context, com.miui.applicationlock.ConfirmAccessControl, miui.app.Activity] */
    /* access modifiers changed from: private */
    public void a(boolean z2) {
        Intent intent;
        int h2;
        TextView textView;
        if (this.r && (textView = this.f3137c) != null) {
            textView.setVisibility(0);
            this.f3137c.setText(R.string.access_control_app_is_launching);
            o.a(this.ea, getResources().getString(R.string.access_control_app_is_launching));
        }
        if (!z2 && this.r && this.C.a() == 1 && (h2 = o.h()) < 3) {
            Toast.makeText(this, R.string.after_the_lock_screen, 1).show();
            o.c(h2 + 1);
        }
        if (this.x || o.g((Context) this) == 5) {
            o.b((Context) this, true);
        }
        if (this.y) {
            this.Ka.d();
        }
        if (!TextUtils.isEmpty(this.q)) {
            if (this.D == 999) {
                this.s.addAccessControlPassForUser(this.q, 999);
            } else {
                this.s.addAccessControlPass(this.q);
            }
        }
        E e2 = this.t;
        if (e2 != null) {
            e2.a();
        }
        if (this.l != null && !isFinishing() && !isDestroyed()) {
            this.Ra = Build.VERSION.SDK_INT > 25 && o.a(this.m);
            Bundle bundle = (this.Ra ? this.m : ActivityOptions.makeCustomAnimation(this, R.anim.activity_open_enter, R.anim.activity_open_exit)).toBundle();
            o.a();
            if (Build.VERSION.SDK_INT > 25) {
                this.l.addFlags(Integer.MIN_VALUE);
            }
            try {
                if (this.D != -1) {
                    UserHandle e3 = B.e(this.D);
                    if (this.Ra) {
                        this.E.postDelayed(new U(this, bundle, e3), 500);
                    } else {
                        b.b.o.g.e.a((Object) getBaseContext(), "startActivityAsUser", (Class<?>[]) new Class[]{Intent.class, Bundle.class, UserHandle.class}, this.l, bundle, e3);
                    }
                } else if (this.Ra) {
                    this.E.postDelayed(new V(this, bundle), 500);
                } else {
                    startActivity(this.l, bundle);
                }
            } catch (Exception e4) {
                Log.e("ConfirmAccessControl", "start other app failed", e4);
                AnalyticsUtil.trackException(e4);
            } catch (Throwable th) {
                o.b();
                throw th;
            }
            o.b();
        }
        o.a(0, getApplicationContext());
        if (!this.r || (intent = this.l) == null || (intent.getFlags() & 1) != 1) {
            if (!this.r) {
                if (getIntent().getBooleanExtra("checkAccess_to_uncheck", false)) {
                    Intent intent2 = new Intent(this, ConfirmAccountActivity.class);
                    intent2.putExtra("account_dialog_extra_data", true);
                    startActivity(intent2);
                } else {
                    setResult(-1);
                }
            }
            finish();
        } else {
            this.E.postDelayed(new W(this), 300);
        }
        if (this.l == null && !TextUtils.isEmpty(this.q)) {
            overridePendingTransition(R.anim.activity_open_enter, R.anim.activity_open_exit);
        }
        o.a((Context) this, this.q);
    }

    /* access modifiers changed from: private */
    public void a(boolean z2, Intent intent) {
        if (z2 && intent != null) {
            startActivityForResult(intent, 29027);
        }
    }

    private void b(long j2) {
        Runnable runnable = this.L;
        if (runnable != null) {
            this.E.removeCallbacks(runnable);
        }
        Handler handler = this.E;
        S s2 = new S(this);
        this.L = s2;
        handler.postDelayed(s2, j2);
    }

    /* JADX WARNING: type inference failed for: r8v0, types: [android.content.Context, com.miui.applicationlock.ConfirmAccessControl] */
    /* access modifiers changed from: private */
    public void b(boolean z2) {
        AlertDialog alertDialog;
        boolean z3 = true;
        if (this.w || this.M.isKeyguardLocked() || o.g((Context) this) == 5 || (((alertDialog = this.N) != null && alertDialog.isShowing()) || this.T == 0 || t() != 0 || ((i.d() && this.ga.getVisibility() == 0) || (i.d() && this.x && F())))) {
            StringBuilder sb = new StringBuilder();
            sb.append("Return reason: isRegisterFingerprint: ");
            sb.append(this.w);
            sb.append(" isKeyguard: ");
            sb.append(this.M.isKeyguardLocked());
            sb.append(" wrongFingerAttempts: ");
            sb.append(o.g((Context) this));
            sb.append(" bindAccountDialog show: ");
            AlertDialog alertDialog2 = this.N;
            if (alertDialog2 == null || !alertDialog2.isShowing()) {
                z3 = false;
            }
            sb.append(z3);
            sb.append(" mUnlockMode: ");
            sb.append(this.T);
            sb.append(" attemptDeadLine: ");
            sb.append(t());
            sb.append(" mediator visible: ");
            sb.append(this.ga.getVisibility());
            sb.append(" is account dialog show: ");
            sb.append(this.Sa);
            Log.d("ConfirmAccessControl", sb.toString());
            return;
        }
        this.w = true;
        if (this.x) {
            try {
                E e2 = this.t;
                a aVar = new a(this, (X) null);
                if (z2) {
                    z3 = false;
                }
                e2.a((q) aVar, z3 ? 1 : 0);
                Log.d("ConfirmAccessControl", "registerFingerprint authenticateAppLock: " + this.q + ",userId: " + this.O);
            } catch (Exception e3) {
                e3.printStackTrace();
            }
        } else {
            this.T = 0;
            if (t() == 0) {
                this.f3137c.setText("");
            }
        }
    }

    /* access modifiers changed from: private */
    public void c(long j2) {
        Runnable runnable = this.K;
        if (runnable != null) {
            this.E.removeCallbacks(runnable);
        }
        Handler handler = this.E;
        C0272ea eaVar = new C0272ea(this);
        this.K = eaVar;
        handler.postDelayed(eaVar, j2);
    }

    static /* synthetic */ int i(ConfirmAccessControl confirmAccessControl) {
        int i2 = confirmAccessControl.u + 1;
        confirmAccessControl.u = i2;
        return i2;
    }

    static /* synthetic */ int m(ConfirmAccessControl confirmAccessControl) {
        int i2 = confirmAccessControl.v + 1;
        confirmAccessControl.v = i2;
        return i2;
    }

    /* JADX WARNING: type inference failed for: r2v0, types: [android.content.Context, com.miui.applicationlock.ConfirmAccessControl] */
    /* access modifiers changed from: private */
    public void p() {
        if (!this.Ma && !F()) {
            Log.d("ConfirmAccessControl", "faceToFingerConversion called: ");
            this.Ma = true;
            o();
            this.f3137c.setText("");
            this.La.setVisibility(4);
            if (this.x && this.S != null && !this.Pa && o.g((Context) this) != 5) {
                this.S.setVisibility(0);
            }
        }
    }

    /* JADX WARNING: type inference failed for: r4v0, types: [android.content.Context, com.miui.applicationlock.ConfirmAccessControl, miui.app.Activity] */
    /* access modifiers changed from: private */
    public void q() {
        int i2 = 0;
        this.z = false;
        if (!H()) {
            this.f.setBackgroundColor(0);
        }
        a(d.NeedToUnlock);
        o.a(0, getApplicationContext());
        if (this.T == 1) {
            if (!i.d()) {
                b(this.B);
            }
            if (!this.R) {
                this.f3138d.setText("");
                if (i.d()) {
                    i2 = 4;
                }
                a(i2, 4, 4);
            }
        } else if (this.R) {
        } else {
            if (o.g((Context) this) == 5) {
                this.f3137c.setText(R.string.access_control_need_to_unlock_nofingerprint);
                this.f3138d.setText(R.string.access_control_fingerprint_not_identified_msg);
                a(4, 0, 0);
                return;
            }
            this.f3138d.setVisibility(4);
            this.f3137c.setText(l());
        }
    }

    private void r() {
        if (G() && i.d() && C0259c.b(getContentResolver(), this.O)) {
            this.Oa = true;
            a(false);
            C0259c.a(getContentResolver(), this.O);
            o.a(getApplicationContext(), 48, getLayoutInflater().inflate(R.layout.applock_toast_quick_enter, (ViewGroup) null), R.style.applock_toast_quick_enter_animation);
        }
    }

    private String s() {
        return "access_control_lock_enabled";
    }

    /* access modifiers changed from: private */
    public long t() {
        long elapsedRealtime = SystemClock.elapsedRealtime();
        long a2 = f.a(getContentResolver(), "applock_countDownTimer_deadline", 0);
        if (a2 < elapsedRealtime || a2 > elapsedRealtime + 30000) {
            return 0;
        }
        return a2;
    }

    /* JADX WARNING: type inference failed for: r6v0, types: [android.content.Context, com.miui.applicationlock.ConfirmAccessControl, miui.app.Activity] */
    /* access modifiers changed from: private */
    public void u() {
        int i2;
        Intent intent;
        Resources resources;
        int i3;
        String string;
        if ("com.android.settings".equals(this.q)) {
            M();
            return;
        }
        if (o.a(this.s).size() == 0) {
            intent = new Intent(this, LockChooseAccessControl.class);
            intent.putExtra("forgot_password_reset", true);
            string = getResources().getString(R.string.password_forget_pattern_message_none);
            i2 = R.string.password_forget_pattern_reset;
        } else {
            int i4 = this.O;
            i2 = R.string.password_forget_pattern_confirm;
            if (i4 == 0) {
                intent = new Intent();
                intent.setClassName("com.android.settings", "com.android.settings.Settings$PrivacySettingsActivity");
                resources = getResources();
                i3 = R.string.password_forget_pattern_message;
            } else if (i4 == C0384o.a(getApplicationContext().getContentResolver(), "second_user_id", (int) UserHandle.USER_NULL, 0)) {
                intent = new Intent("miui.intent.action.PRIVATE_SPACE_SETTING");
                resources = getResources();
                i3 = R.string.password_xspace_forget_pattern_message;
            } else {
                return;
            }
            string = resources.getString(i3);
        }
        a(string, intent, i2);
    }

    private void v() {
        if (("laurel_sprout".equals(Build.DEVICE) || "laurus".equals(Build.DEVICE)) && i.d()) {
            getWindow().getDecorView().setSystemUiVisibility(4610);
        }
    }

    /* JADX WARNING: type inference failed for: r9v0, types: [android.content.Context, com.miui.applicationlock.ConfirmAccessControl] */
    private void w() {
        if (!this.x) {
            this.T = 0;
            this.f3137c.setText("");
            this.ga.setVisibility(0);
            if (o.g((Context) this) == 5) {
                this.f3137c.setText(R.string.access_control_need_to_unlock_nofingerprint);
            }
            if (!this.V && !"numeric".equals(this.ca)) {
                this.e.setVisibility(4);
            }
        } else if (t() == 0) {
            this.T = 1;
            if (!this.V && !"numeric".equals(this.ca)) {
                this.e.setVisibility(4);
            }
            this.f3137c.setText("");
            a(this.y ? 4 : 0, 0, 4);
        } else if (1 == o.e((Context) this)) {
            this.T = 1;
        }
    }

    private void x() {
        if (i.d() && this.x && !this.R) {
            if (t() == 0) {
                a(4, 0, 8, 0);
            } else {
                a(0, 4, 4, 4);
            }
        }
    }

    /* JADX WARNING: type inference failed for: r7v0, types: [android.content.Context, com.miui.applicationlock.ConfirmAccessControl, miui.app.Activity] */
    private void y() {
        View view;
        this.g = (ImageView) findViewById(R.id.imag_background);
        this.s = (SecurityManager) getSystemService("security");
        this.C = C0259c.b(getApplicationContext());
        this.t = E.a((Context) this);
        this.M = (KeyguardManager) getSystemService("keyguard");
        this.ea = (AccessibilityManager) getSystemService("accessibility");
        this.f = findViewById(R.id.backlayout);
        this.ca = this.s.getAccessControlPasswordType();
        this.ga = (PasswordUnlockMediator) findViewById(R.id.passwordMediator);
        this.aa = (LinearLayout.LayoutParams) this.h.getLayoutParams();
        this.i = (LinearLayout) findViewById(R.id.title_layout);
        this.Qa = findViewById(R.id.face_finger_set);
        if (TextUtils.isEmpty(this.ca)) {
            this.ca = "pattern";
        }
        this.ga.a(this.ca);
        this.f3136b = this.ga.getUnlockView();
        this.f3137c = (TextView) findViewById(R.id.headerText);
        this.f3138d = (TextView) findViewById(R.id.footerText);
        this.va = getResources().getColor(R.color.unlock_text_dark);
        if (this.V) {
            this.f3136b.setLightMode(false);
        }
        if (!"numeric".equals(this.ca)) {
            view = this.f3136b.findViewById(R.id.forgetPattern);
        } else {
            this.da = (MiuiNumericInputView) this.f3136b.findViewById(R.id.numeric_inputview);
            view = this.da.getForgetPasswordView();
        }
        this.e = (TextView) view;
        if ("mixed".equals(this.ca)) {
            this.ba = (EditText) this.f3136b.findViewById(R.id.miui_mixed_password_input_field);
        }
        this.f3136b.setApplockUnlockCallback(this.Va);
        this.O = b.b.c.j.g.a(getApplicationContext());
        this.n = b.b.o.f.a.a.a();
        if (s() != null) {
            this.k = new C0292oa(this, (Handler) null);
            getContentResolver().registerContentObserver(Settings.Secure.getUriFor(s()), true, this.k);
        }
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constants.System.ACTION_SCREEN_OFF);
        intentFilter.addAction(Constants.System.ACTION_SCREEN_ON);
        intentFilter.addAction(Constants.System.ACTION_USER_PRESENT);
        intentFilter.addAction("miui.intent.action.APP_LOCK_CLEAR_STATE");
        registerReceiver(this.Ua, intentFilter);
        this.fa = true;
        if (this.V) {
            if (!"numeric".equals(this.ca)) {
                this.e.setTextColor(this.Q.getColor(R.color.unlock_text_dark));
            }
            if ("mixed".equals(this.ca)) {
                this.ba.setTextColor(this.Q.getColor(R.color.unlock_text_dark));
                this.ba.setHintTextColor(this.Q.getColor(R.color.applock_mix_edit_hint_color));
            }
        }
        A();
        B();
    }

    /* JADX WARNING: type inference failed for: r5v0, types: [android.app.Activity] */
    /* access modifiers changed from: private */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void z() {
        /*
            r8 = this;
            miui.security.SecurityManager r0 = r8.s
            java.lang.String r1 = "com.xiaomi.account"
            boolean r0 = com.miui.applicationlock.c.o.a((miui.security.SecurityManager) r0, (java.lang.String) r1)
            if (r0 != 0) goto L_0x000f
            miui.security.SecurityManager r0 = r8.s
            com.miui.applicationlock.c.o.b((miui.security.SecurityManager) r0, (java.lang.String) r1)
        L_0x000f:
            android.os.Bundle r4 = new android.os.Bundle
            r4.<init>()
            android.content.Context r0 = r8.getApplicationContext()
            java.lang.String r0 = r0.getPackageName()
            java.lang.String r1 = "androidPackageName"
            r4.putString(r1, r0)
            android.content.Context r0 = r8.getApplicationContext()
            android.accounts.AccountManager r2 = android.accounts.AccountManager.get(r0)
            android.content.Context r0 = r8.getApplicationContext()
            android.accounts.Account r3 = com.miui.applicationlock.c.o.d((android.content.Context) r0)
            com.miui.applicationlock.Y r6 = new com.miui.applicationlock.Y
            r6.<init>(r8)
            r7 = 0
            r5 = r8
            r2.confirmCredentials(r3, r4, r5, r6, r7)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.applicationlock.ConfirmAccessControl.z():void");
    }

    public void finish() {
        ConfirmAccessControl.super.finish();
        if (this.w) {
            P();
            Log.d("ConfirmAccessControl", "unregisterFingerprint 5");
        }
        overridePendingTransition(0, 0);
    }

    /* access modifiers changed from: protected */
    public int l() {
        return this.R ? R.string.applock_header_text_less_than_half : (!this.x || this.T != 1) ? R.string.access_control_need_to_unlock_nofingerprint : R.string.confirm_fingerprint_msg;
    }

    /* JADX WARNING: type inference failed for: r6v0, types: [android.content.Context, com.miui.applicationlock.ConfirmAccessControl, miui.app.Activity] */
    /* access modifiers changed from: protected */
    public void m() {
        Drawable loadIcon;
        TextView textView;
        if (!TextUtils.isEmpty(this.q)) {
            ApplicationInfo applicationInfo = null;
            try {
                applicationInfo = getPackageManager().getApplicationInfo(this.q, 0);
            } catch (PackageManager.NameNotFoundException e2) {
                Log.e("ConfirmAccessControl", "Fail to get applicationInfo", e2);
            }
            if (applicationInfo != null && (loadIcon = applicationInfo.loadIcon(getPackageManager())) != null) {
                if (loadIcon instanceof BitmapDrawable) {
                    BitmapDrawable bitmapDrawable = (BitmapDrawable) applicationInfo.loadIcon(getPackageManager());
                } else {
                    new BitmapDrawable(r.a(applicationInfo.loadIcon(getPackageManager())));
                }
                this.I = new com.miui.applicationlock.b.b(this);
                this.na = this.I.a(211);
                this.oa = this.I.a(212);
                this.pa = this.I.a(213);
                if (H()) {
                    this.wa = (TextView) findViewById(R.id.app_name);
                }
                this.B = false;
                Q();
                D();
                if (H()) {
                    ImageView imageView = (ImageView) findViewById(R.id.backup_app_icon);
                    CharSequence loadLabel = applicationInfo.loadLabel(getPackageManager());
                    if (!TextUtils.isEmpty(loadLabel) && (textView = this.wa) != null) {
                        textView.setText(loadLabel.toString());
                    }
                    a(imageView, loadIcon);
                    this.h.setVisibility(8);
                    this.Qa.setVisibility(8);
                    ImageView imageView2 = this.S;
                    if (imageView2 != null) {
                        imageView2.setVisibility(8);
                        return;
                    }
                    return;
                }
                a(this.h, loadIcon);
            }
        }
    }

    public void n() {
        SimpleExoPlayer simpleExoPlayer = this.ya;
        if (simpleExoPlayer != null) {
            this.Ea = simpleExoPlayer.getCurrentPosition();
            this.ya.setPlayWhenReady(false);
            this.ya.release();
            if (this.Ja) {
                this.Ja = false;
            } else if (this.ya != null) {
                Log.d("ConfirmAccessControl", "currentPosition: " + this.ya.getCurrentPosition() + ", duration: " + this.ya.getDuration());
                h.a(this.ya.getCurrentPosition(), this.ya.getDuration());
            }
        }
    }

    public void o() {
        Runnable runnable = this.L;
        if (runnable != null) {
            this.E.removeCallbacks(runnable);
        }
        C0259c cVar = this.C;
        if (cVar != null && cVar.h()) {
            this.Ka.a((Runnable) new C0290na(this));
        }
    }

    /* JADX WARNING: type inference failed for: r2v0, types: [android.content.Context, com.miui.applicationlock.ConfirmAccessControl, miui.app.Activity] */
    public void onActivityResult(int i2, int i3, Intent intent) {
        ImageView imageView;
        ConfirmAccessControl.super.onActivityResult(i2, i3, intent);
        if (i2 == 29000) {
            this.W = false;
            if (i3 == -1) {
                Intent intent2 = new Intent(this, ConfirmAccountActivity.class);
                intent2.putExtra("account_dialog_extra_data", true);
                startActivity(intent2);
            }
        } else if (i2 != 29027) {
            if (i2 == 290262 && i3 == -1) {
                K();
                finish();
            }
        } else if (i3 == -1) {
            K();
            o.c(this.s);
            if (o.g((Context) this) != 0) {
                o.b((Context) this, true);
            }
            this.x = G();
            w();
            if (this.x && this.B && (imageView = this.S) != null) {
                imageView.setImageDrawable(this.Q.getDrawable(R.drawable.fingerprint_light));
            }
        }
    }

    public void onBackPressed() {
        if (this.r) {
            try {
                this.C.b(this.q);
                SecurityManager securityManager = this.s;
                String str = this.q;
                int i2 = 999;
                if (this.D != 999) {
                    i2 = B.j();
                }
                securityManager.finishAccessControl(str, i2);
                finish();
                if (this.A != null) {
                    this.n.a(this.A, 0, (Intent) null);
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        } else {
            ConfirmAccessControl.super.onBackPressed();
        }
    }

    /* JADX WARNING: type inference failed for: r2v0, types: [android.content.Context] */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onClick(android.view.View r9) {
        /*
            r8 = this;
            int r9 = r9.getId()
            switch(r9) {
                case 2131296789: goto L_0x0065;
                case 2131296801: goto L_0x0015;
                case 2131296848: goto L_0x000f;
                case 2131296849: goto L_0x000c;
                case 2131297010: goto L_0x0065;
                case 2131297867: goto L_0x0009;
                default: goto L_0x0007;
            }
        L_0x0007:
            goto L_0x007d
        L_0x0009:
            android.widget.ImageView r9 = r8.ra
            goto L_0x0011
        L_0x000c:
            android.widget.ImageView r9 = r8.ta
            goto L_0x0011
        L_0x000f:
            android.widget.ImageView r9 = r8.ua
        L_0x0011:
            r8.a((android.widget.ImageView) r9)
            goto L_0x007d
        L_0x0015:
            android.widget.ImageView r9 = r8.ja
            int r9 = r9.getVisibility()
            java.lang.String r0 = ""
            r1 = 0
            if (r9 != 0) goto L_0x003f
            android.widget.TextView r9 = r8.f3137c
            r9.setText(r0)
            android.widget.TextView r9 = r8.ka
            r0 = 2131756288(0x7f100500, float:1.914348E38)
            r9.setText(r0)
            boolean r9 = r8.R
            r0 = 8
            if (r9 == 0) goto L_0x0035
            r9 = r0
            goto L_0x0036
        L_0x0035:
            r9 = r1
        L_0x0036:
            r8.a((int) r0, (int) r1, (int) r0, (int) r9)
            boolean r9 = r8.B
            r8.b((boolean) r9)
            goto L_0x007d
        L_0x003f:
            android.widget.TextView r9 = r8.ka
            r2 = 2131756287(0x7f1004ff, float:1.9143477E38)
            r9.setText(r2)
            android.widget.ImageView r9 = r8.La
            r2 = 4
            if (r9 == 0) goto L_0x004f
            r9.setVisibility(r2)
        L_0x004f:
            r8.a((int) r1, (int) r1, (int) r1, (int) r2)
            r8.P()
            java.lang.String r9 = "ConfirmAccessControl"
            java.lang.String r1 = "unregisterFingerprint 7"
            android.util.Log.d(r9, r1)
            r8.o()
            android.widget.TextView r9 = r8.f3137c
            r9.setText(r0)
            goto L_0x007d
        L_0x0065:
            com.miui.applicationlock.b.b r9 = r8.I
            r0 = 209(0xd1, double:1.033E-321)
            b.b.n.g$a r5 = r9.a(r0)
            r3 = 209(0xd1, double:1.033E-321)
            java.lang.String r6 = "applicationlock"
            java.lang.String r7 = "com.miui.securitycenter_skinadinfo"
            r2 = r8
            b.b.n.l.a((android.content.Context) r2, (long) r3, (b.b.n.g.a) r5, (java.lang.String) r6, (java.lang.String) r7)
            com.miui.applicationlock.a.h.f()
            r9 = 1
            r8.Ja = r9
        L_0x007d:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.applicationlock.ConfirmAccessControl.onClick(android.view.View):void");
    }

    /* JADX WARNING: type inference failed for: r4v0, types: [b.b.c.c.a, android.content.Context, com.miui.applicationlock.ConfirmAccessControl, miui.app.Activity] */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        boolean z2 = true;
        if (Build.VERSION.SDK_INT != 26) {
            setRequestedOrientation(1);
        }
        try {
            String stringExtra = getIntent().getStringExtra("extra_data");
            if (!C0259c.b(getApplicationContext()).d()) {
                finish();
            }
            if (stringExtra == null || (!stringExtra.equals("HappyCoding") && !stringExtra.equals("HappyCodingMain"))) {
                z2 = false;
            }
            this.V = z2;
            this.Q = getResources();
            if (isDarkModeEnable()) {
                getWindow().setBackgroundDrawable(new ColorDrawable(RoundedDrawable.DEFAULT_BORDER_COLOR));
            } else if (Build.VERSION.SDK_INT > 28) {
                s.b().a((s.b) new C0286la(this));
            } else {
                Bundle extras = getIntent().getExtras();
                if (extras != null && extras.get("android.app.extra.PROTECTED_APP_TOKEN") == null) {
                    overridePendingTransition(R.anim.applock_confirm_open_anim, 0);
                }
                Window window = getWindow();
                window.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.applock_page_bg_color)));
                window.setLayout(-1, -1);
                window.addFlags(4);
                a(getWindow(), 0.5f, 1.0f);
            }
            if (this.V) {
                setContentView(R.layout.confirm_applock_pattern_securitycenter);
                TextView textView = (TextView) findViewById(R.id.confirm_access_back);
                textView.setOnClickListener(new C0288ma(this));
                textView.setContentDescription(getResources().getString(R.string.back_app_name));
                this.h = (ImageView) findViewById(R.id.icon1);
                this.h.setImageDrawable(this.Q.getDrawable(R.drawable.icon_app_lock_new));
                if (i.e()) {
                    View findViewById = findViewById(R.id.top_actionBar);
                    LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) findViewById.getLayoutParams();
                    this.la = i.f(this) - getResources().getDimensionPixelOffset(R.dimen.flat_screen_status_bar_height);
                    layoutParams.topMargin = getResources().getDimensionPixelOffset(R.dimen.back_button_alight_top) + this.la;
                    findViewById.setLayoutParams(layoutParams);
                }
                if (i.d()) {
                    C0259c.a(getContentResolver(), b.b.c.j.g.a(getApplicationContext()));
                }
            } else {
                setContentView(R.layout.confirm_applock_pattern);
                this.h = (ImageView) findViewById(R.id.icon1);
                this.h.setImageDrawable(getPackageManager().getApplicationIcon(getApplicationInfo()));
                this.Y = (RelativeLayout) findViewById(R.id.flag_top_layout);
                this.Z = (RelativeLayout) findViewById(R.id.top_header_layout);
                if (i.e()) {
                    ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) this.Z.getLayoutParams();
                    this.la = i.f(this) - getResources().getDimensionPixelOffset(R.dimen.flat_screen_status_bar_height);
                    marginLayoutParams.topMargin = getResources().getDimensionPixelOffset(R.dimen.flag_top_margin_layout) + this.la;
                    this.Z.setLayoutParams(marginLayoutParams);
                }
            }
            v();
            y();
            a(getIntent());
            if (!this.Oa) {
                I();
                this.v = o.h(this);
                h.b(this.V ? "sc_internal" : "from_app");
            }
        } catch (Exception e2) {
            finish();
            Log.e("ConfirmAccessControl", "parcel exception", e2);
        }
    }

    /* JADX WARNING: type inference failed for: r4v0, types: [android.content.Context, com.miui.applicationlock.ConfirmAccessControl, miui.app.Activity] */
    public void onDestroy() {
        if (this.k != null) {
            getContentResolver().unregisterContentObserver(this.k);
        }
        CountDownTimer countDownTimer = this.j;
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        GifImageView gifImageView = this.G;
        if (gifImageView != null) {
            gifImageView.b();
        }
        if (this.fa) {
            unregisterReceiver(this.Ua);
        }
        if (this.w) {
            P();
            Log.d("ConfirmAccessControl", "unregisterFingerprint 4");
        }
        if (t() != 0) {
            o.a((Context) this, this.T);
        }
        ConfirmAccessControl.super.onDestroy();
    }

    public void onPause() {
        super.onPause();
        this.Ta = false;
        Log.d("ConfirmAccessControl", "onPause: " + this.O);
        if (this.j != null) {
            this.z = false;
        }
        Log.d("ConfirmAccessControl", "unregisterFingerprint 9: " + this.O + "mStop: " + this.F);
    }

    public void onResume() {
        String str;
        super.onResume();
        this.Ta = true;
        Log.d("ConfirmAccessControl", "onResume: " + this.O);
        getWindow().addFlags(8192);
        String str2 = this.ca;
        if (str2 != null && !str2.equals(this.s.getAccessControlPasswordType())) {
            Log.d("ConfirmAccessControl", "onResume: return 1");
            finish();
        }
        if (!this.V && F()) {
            a(getResources().getConfiguration());
            if (this.R) {
                return;
            }
        }
        if (!this.C.e() && !this.C.d() && this.C.b() == null) {
            Log.d("ConfirmAccessControl", "onResume: return 2");
            a(false);
        } else if (!this.C.d()) {
            Log.d("ConfirmAccessControl", "onResume: return 3");
            finish();
        }
        if (this.r) {
            try {
                if (!this.C.e() || o.a(this.D, this.s, this.q)) {
                    finish();
                    Log.w("ConfirmAccessControl", "finish checkAccessControlPass " + this.q);
                    return;
                }
            } catch (Exception e2) {
                Log.e("ConfirmAccessControl", " onResume error ", e2);
            }
            if (this.C.c(this.q)) {
                finish();
                Log.w("ConfirmAccessControl", "finish CancelUnlock " + this.q);
                return;
            }
            IBinder binder = new Binder();
            try {
                binder = (IBinder) b.b.o.g.c.a(android.app.Activity.class, (Object) this, "getActivityToken", (Class<?>[]) null, new Object[0]);
            } catch (Exception e3) {
                Log.e("ConfirmAccessControl", "getActivity token exception: ", e3);
            }
            if (Boolean.valueOf(this.s.needFinishAccessControl(binder)).booleanValue()) {
                finish();
                Log.w("ConfirmAccessControl", "finish needFinishAccessControl " + this.q);
                return;
            }
        }
        long t2 = t();
        if (t2 != 0) {
            a(t2);
        } else if (!this.f3136b.isEnabled()) {
            N();
            a(d.NeedToUnlock);
        } else if (!this.f3136b.c()) {
            CountDownTimer countDownTimer = this.j;
            if (countDownTimer != null) {
                countDownTimer.cancel();
            }
            N();
            this.f.setBackgroundColor(0);
            a(d.NeedToUnlock);
            this.z = false;
            if (this.T == 0) {
                a(4, 0, 4);
                this.f3137c.setText(R.string.access_control_need_to_unlock_nofingerprint);
            }
        }
        if (this.Oa) {
            Log.d("ConfirmAccessControl", "onResume: return 6");
            return;
        }
        if (this.V || this.F) {
            if (SystemClock.elapsedRealtime() - f3135a > 150 || SystemClock.elapsedRealtime() - f3135a <= 0) {
                b(this.B);
                str = "onResume register";
            } else {
                c(150);
                str = "onResume register delay";
            }
            Log.d("ConfirmAccessControl", str);
        }
        if (this.y && !this.Ma) {
            if (this.M.isKeyguardLocked()) {
                b(150);
            } else {
                L();
            }
        }
        if (!this.R) {
            c(150);
        }
        if (!this.V) {
            J();
        }
    }

    /* access modifiers changed from: protected */
    public void onStart() {
        ConfirmAccessControl.super.onStart();
        Log.d("ConfirmAccessControl", "onStart: " + this.O);
        this.F = false;
        if (!this.r || t() != 0) {
            this.e.setVisibility(0);
            O();
        }
    }

    /* access modifiers changed from: protected */
    public void onStop() {
        e eVar;
        ConfirmAccessControl.super.onStop();
        Log.d("ConfirmAccessControl", "onStop: " + this.O);
        this.F = true;
        if (this.r && E() && (eVar = this.J) != null) {
            eVar.c("applicationlock", this.I.b());
            ImageView imageView = this.xa;
            if (imageView != null) {
                imageView.setVisibility(8);
            }
        }
        o();
        if (this.w) {
            P();
        }
        Log.d("ConfirmAccessControl", "unregisterFingerprint 3: " + this.O);
        if (i.d()) {
            C0259c.a(getContentResolver(), this.O);
        }
        AlertDialog alertDialog = this.N;
        if (alertDialog != null) {
            alertDialog.dismiss();
        }
        this.N = null;
    }

    public void onWindowFocusChanged(boolean z2) {
        Uri uri;
        ConfirmAccessControl.super.onWindowFocusChanged(z2);
        Log.d("ConfirmAccessControl", "onWindowFocusChanged: " + z2 + ",,, useId: " + this.O);
        this.Na = z2;
        o.a(this.B, getWindow());
        if (!z2 || this.R || this.W) {
            SimpleExoPlayer simpleExoPlayer = this.ya;
            if (simpleExoPlayer != null) {
                simpleExoPlayer.setPlayWhenReady(false);
            }
            n();
            P();
            Log.d("ConfirmAccessControl", "unregisterFingerprint 2");
            return;
        }
        v();
        PlayerView playerView = this.Fa;
        if (!(playerView == null || (uri = this.Ga) == null)) {
            a(playerView, uri);
        }
        Log.d("ConfirmAccessControl", "onWindowFocusChanged register finger");
        c(150);
    }
}
