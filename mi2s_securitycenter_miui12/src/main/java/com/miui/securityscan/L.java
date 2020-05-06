package com.miui.securityscan;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.os.MessageQueue;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.view.animation.LinearInterpolator;
import android.view.animation.PathInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import b.b.c.j.e;
import b.b.g.a;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.miui.common.card.CardViewAdapter;
import com.miui.common.card.models.AdvCardModel;
import com.miui.common.card.models.AdvInternationalCardModel;
import com.miui.common.card.models.AdvListTitleCardModel;
import com.miui.common.card.models.BaseCardModel;
import com.miui.common.card.models.BottomPlaceCardModel;
import com.miui.common.card.models.ListTitleCheckboxCardModel;
import com.miui.common.customview.AutoPasteListView;
import com.miui.common.customview.OverScrollLayout;
import com.miui.common.customview.ScoreTextView;
import com.miui.common.customview.gif.GifImageView;
import com.miui.maml.folme.AnimatedProperty;
import com.miui.securitycenter.Application;
import com.miui.securitycenter.R;
import com.miui.securitycenter.f;
import com.miui.securitycenter.h;
import com.miui.securityscan.a.G;
import com.miui.securityscan.b.j;
import com.miui.securityscan.b.k;
import com.miui.securityscan.b.m;
import com.miui.securityscan.b.n;
import com.miui.securityscan.cards.g;
import com.miui.securityscan.cards.k;
import com.miui.securityscan.cards.p;
import com.miui.securityscan.cards.q;
import com.miui.securityscan.g.c;
import com.miui.securityscan.g.d;
import com.miui.securityscan.i.l;
import com.miui.securityscan.i.o;
import com.miui.securityscan.model.manualitem.GarbageCleanModel;
import com.miui.securityscan.model.system.VirusScanModel;
import com.miui.securityscan.scanner.C0557d;
import com.miui.securityscan.scanner.C0565l;
import com.miui.securityscan.scanner.C0566m;
import com.miui.securityscan.scanner.C0568o;
import com.miui.securityscan.scanner.C0569p;
import com.miui.securityscan.scanner.C0570q;
import com.miui.securityscan.scanner.C0572t;
import com.miui.securityscan.scanner.C0573u;
import com.miui.securityscan.scanner.O;
import com.miui.securityscan.scanner.ScoreManager;
import com.miui.securityscan.scanner.v;
import com.miui.securityscan.scanner.w;
import com.miui.securityscan.scanner.x;
import com.miui.securityscan.ui.main.MainVideoView;
import com.miui.securityscan.ui.main.NativeInterstitialAdLayout;
import com.miui.securityscan.ui.main.OptimizingBar;
import com.miui.securityscan.ui.settings.SettingsActivity;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import miui.app.AlertDialog;
import miui.os.Build;
import miui.text.ExtraTextUtils;

public class L extends C0542b implements View.OnClickListener, a.b {
    /* access modifiers changed from: private */

    /* renamed from: b  reason: collision with root package name */
    public static long f7557b;

    /* renamed from: c  reason: collision with root package name */
    public static ArrayList<BaseCardModel> f7558c;
    /* access modifiers changed from: private */
    public View A;
    /* access modifiers changed from: private */
    public boolean Aa;
    /* access modifiers changed from: private */
    public TextView B;
    private boolean Ba;
    /* access modifiers changed from: private */
    public Button C;
    private boolean Ca;
    /* access modifiers changed from: private */
    public GifImageView D;
    private c Da;
    /* access modifiers changed from: private */
    public Button E;
    private d Ea;
    public CardViewAdapter F;
    private com.miui.securityscan.g.b Fa;
    public CardViewAdapter G;
    /* access modifiers changed from: private */
    public boolean Ga;
    public TextView H;
    /* access modifiers changed from: private */
    public boolean Ha;
    /* access modifiers changed from: private */
    public TextView I;
    /* access modifiers changed from: private */
    public boolean Ia;
    public TextView J;
    public List<Integer> Ja;
    /* access modifiers changed from: private */
    public RelativeLayout K;
    private long Ka;
    public ScoreTextView L;
    private long La;
    public ScoreTextView M;
    private p Ma;
    public ArrayList<BaseCardModel> N;
    private q Na;
    public int O = 0;
    private k Oa;
    private long P;
    private b.b.g.a Pa;
    /* access modifiers changed from: private */
    public long Q;
    /* access modifiers changed from: private */
    public b.b.h.a Qa;
    /* access modifiers changed from: private */
    public long R;
    private boolean Ra;
    private long S;
    private View Sa;
    private long T;
    /* access modifiers changed from: private */
    public float Ta;
    private C0573u U;
    /* access modifiers changed from: private */
    public float Ua;
    private x V;
    private int Va;
    private m W;
    private List<Integer> Wa;
    private j X;
    private boolean Xa = false;
    public com.miui.securityscan.b.p Y;
    private ImageView Ya;
    private C0566m Z;
    /* access modifiers changed from: private */
    public View Za;
    /* access modifiers changed from: private */
    public MainVideoView _a;
    private com.miui.securityscan.b.d aa;
    private b ab;
    private C0572t ba;
    /* access modifiers changed from: private */
    public int bb = 0;
    private C0565l ca;
    /* access modifiers changed from: private */
    public int cb;

    /* renamed from: d  reason: collision with root package name */
    public boolean f7559d;
    public boolean da;
    private MessageQueue.IdleHandler db = new p(this);
    private boolean e;
    public boolean ea;
    private boolean f;
    public com.miui.securityscan.cards.d fa;
    /* access modifiers changed from: private */
    public C0557d g = C0557d.NORMAL;
    /* access modifiers changed from: private */
    public int ga;
    /* access modifiers changed from: private */
    public boolean h;
    private int ha;
    private boolean i;
    private LinearLayout ia;
    private O j;
    /* access modifiers changed from: private */
    public RelativeLayout ja;
    private com.miui.securityscan.f.b k;
    private Object ka = new Object();
    public ScoreManager l;
    /* access modifiers changed from: private */
    public Object la = new Object();
    public w m = new w(this);
    private Object ma = new Object();
    private RelativeLayout n;
    private Object na = new Object();
    private ViewStub o;
    /* access modifiers changed from: private */
    public OverScrollLayout oa;
    private ViewStub p;
    /* access modifiers changed from: private */
    public LinearLayout pa;
    private ViewStub q;
    /* access modifiers changed from: private */
    public int qa;
    private ViewStub r;
    public boolean ra;
    public OptimizingBar s;
    public boolean sa;
    public NativeInterstitialAdLayout t;
    public boolean ta;
    private AlertDialog u;
    public boolean ua;
    private AlertDialog v;
    public boolean va;
    /* access modifiers changed from: private */
    public boolean w;
    public boolean wa;
    /* access modifiers changed from: private */
    public boolean x;
    private boolean xa;
    /* access modifiers changed from: private */
    public AutoPasteListView y;
    /* access modifiers changed from: private */
    public ArrayList<BaseCardModel> ya;
    /* access modifiers changed from: private */
    public AutoPasteListView z;
    /* access modifiers changed from: private */
    public boolean za;

    class a implements Animator.AnimatorListener {
        a() {
        }

        public void onAnimationCancel(Animator animator) {
        }

        public void onAnimationEnd(Animator animator) {
            L.this.s.a(C0568o.CLEAR_ACCELERATION);
            Activity activity = L.this.getActivity();
            if (L.this.a(activity)) {
                C0568o oVar = C0568o.CLEAR_ACCELERATION;
                oVar.a(o.a(activity, oVar));
                Log.d("com.miui.securityscan.MainActivity", "ClearAccelerationListener  onAnimationEnd");
                L.this.p();
            }
        }

        public void onAnimationRepeat(Animator animator) {
        }

        public void onAnimationStart(Animator animator) {
            ValueAnimator ofInt = ValueAnimator.ofInt(new int[]{1, 100});
            ofInt.setDuration(AdaptiveTrackSelection.DEFAULT_MIN_TIME_BETWEEN_BUFFER_REEVALUTATION_MS);
            ofInt.addUpdateListener(new K(this));
            ofInt.setInterpolator(new LinearInterpolator());
            ofInt.start();
        }
    }

    private static class b implements Player.EventListener {

        /* renamed from: a  reason: collision with root package name */
        private WeakReference<L> f7561a;

        public b(L l) {
            this.f7561a = new WeakReference<>(l);
        }

        public void onLoadingChanged(boolean z) {
        }

        public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {
        }

        public void onPlayerError(ExoPlaybackException exoPlaybackException) {
        }

        public void onPlayerStateChanged(boolean z, int i) {
            L l = (L) this.f7561a.get();
            if (l != null && l.cb != i) {
                int unused = l.cb = i;
                if (l.cb == 3) {
                    l._a.c();
                    l._a.setPlaySpeed(1.0f);
                } else if (l.cb == 4) {
                    l._a.a((float) l.bb, l.cb);
                    l._a.d();
                }
            }
        }

        public void onPositionDiscontinuity(int i) {
            L l = (L) this.f7561a.get();
            if (l != null) {
                l._a.a();
            }
        }

        public void onRepeatModeChanged(int i) {
        }

        public void onSeekProcessed() {
        }

        public void onShuffleModeEnabledChanged(boolean z) {
        }

        public void onTimelineChanged(Timeline timeline, Object obj, int i) {
        }

        public void onTracksChanged(TrackGroupArray trackGroupArray, TrackSelectionArray trackSelectionArray) {
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:63:0x01bb  */
    /* JADX WARNING: Removed duplicated region for block: B:87:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void A() {
        /*
            r11 = this;
            com.miui.securityscan.cards.c.d()
            com.miui.securityscan.cards.c.b()
            java.util.ArrayList<com.miui.common.card.models.BaseCardModel> r0 = r11.N
            r1 = 1
            if (r0 == 0) goto L_0x01af
            boolean r0 = r0.isEmpty()
            if (r0 != 0) goto L_0x01af
            java.util.ArrayList r0 = new java.util.ArrayList
            java.util.ArrayList<com.miui.common.card.models.BaseCardModel> r2 = r11.N
            r0.<init>(r2)
            java.util.HashMap r2 = new java.util.HashMap
            r2.<init>()
            java.util.ArrayList r3 = new java.util.ArrayList
            r3.<init>()
            java.util.Iterator r4 = r0.iterator()
        L_0x0026:
            boolean r5 = r4.hasNext()
            r6 = -1
            if (r5 == 0) goto L_0x005e
            java.lang.Object r5 = r4.next()
            com.miui.common.card.models.BaseCardModel r5 = (com.miui.common.card.models.BaseCardModel) r5
            boolean r7 = r5 instanceof com.miui.common.card.models.AdvListTitleCardModel
            if (r7 == 0) goto L_0x0026
            r7 = r5
            com.miui.common.card.models.AdvListTitleCardModel r7 = (com.miui.common.card.models.AdvListTitleCardModel) r7
            java.util.List r8 = r7.getSubCardModelList()
            int r9 = r7.getPosition()
            if (r9 == r6) goto L_0x0026
            if (r8 == 0) goto L_0x0026
            boolean r6 = r8.isEmpty()
            if (r6 != 0) goto L_0x0026
            r3.add(r5)
            r3.addAll(r8)
            int r6 = r7.getPosition()
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            r2.put(r6, r5)
            goto L_0x0026
        L_0x005e:
            r0.removeAll(r3)
            java.util.ArrayList r3 = new java.util.ArrayList
            r3.<init>()
            java.util.Iterator r4 = r0.iterator()
        L_0x006a:
            boolean r5 = r4.hasNext()
            if (r5 == 0) goto L_0x0092
            java.lang.Object r5 = r4.next()
            com.miui.common.card.models.BaseCardModel r5 = (com.miui.common.card.models.BaseCardModel) r5
            boolean r7 = r5 instanceof com.miui.common.card.models.AdvCardModel
            if (r7 == 0) goto L_0x006a
            r7 = r5
            com.miui.common.card.models.AdvCardModel r7 = (com.miui.common.card.models.AdvCardModel) r7
            int r8 = r7.getPosition()
            if (r8 == r6) goto L_0x006a
            r3.add(r5)
            int r7 = r7.getPosition()
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            r2.put(r7, r5)
            goto L_0x006a
        L_0x0092:
            r0.removeAll(r3)
            r3 = 0
            r4 = r3
        L_0x0097:
            int r5 = r0.size()
            if (r4 >= r5) goto L_0x00ab
            java.lang.Object r5 = r0.get(r4)
            com.miui.common.card.models.BaseCardModel r5 = (com.miui.common.card.models.BaseCardModel) r5
            boolean r5 = r5 instanceof com.miui.common.card.models.PlaceHolderCardModel
            if (r5 == 0) goto L_0x00a8
            goto L_0x00ac
        L_0x00a8:
            int r4 = r4 + 1
            goto L_0x0097
        L_0x00ab:
            r4 = r6
        L_0x00ac:
            if (r4 == r6) goto L_0x01af
            boolean r5 = r2.isEmpty()
            if (r5 != 0) goto L_0x019f
            com.miui.securityscan.scanner.l r1 = r11.ca
            java.util.ArrayList r1 = com.miui.securityscan.cards.c.b(r1, r3)
            int r5 = r2.size()
            int r6 = r1.size()
            int r5 = r5 + r6
            java.lang.StringBuilder r6 = new java.lang.StringBuilder
            r6.<init>()
            java.lang.String r7 = "advMap size is "
            r6.append(r7)
            int r7 = r2.size()
            r6.append(r7)
            java.lang.String r7 = ",  models.size() is "
            r6.append(r7)
            int r7 = r1.size()
            r6.append(r7)
            java.lang.String r7 = ",  max size is  "
            r6.append(r7)
            r6.append(r5)
            java.lang.String r6 = r6.toString()
            java.lang.String r7 = "com.miui.securityscan.MainActivity"
            android.util.Log.d(r7, r6)
            java.util.ArrayList r6 = new java.util.ArrayList
            r6.<init>()
            r8 = r3
        L_0x00f7:
            if (r8 >= r5) goto L_0x0148
            java.lang.Integer r9 = java.lang.Integer.valueOf(r8)
            java.lang.Object r9 = r2.get(r9)
            com.miui.common.card.models.BaseCardModel r9 = (com.miui.common.card.models.BaseCardModel) r9
            if (r9 == 0) goto L_0x012b
            r6.add(r9)
            boolean r10 = r9 instanceof com.miui.common.card.models.AdvListTitleCardModel
            if (r10 == 0) goto L_0x011b
            com.miui.common.card.models.AdvListTitleCardModel r9 = (com.miui.common.card.models.AdvListTitleCardModel) r9
            java.util.List r9 = r9.getSubCardModelList()
            boolean r10 = r9.isEmpty()
            if (r10 != 0) goto L_0x011b
            r6.addAll(r9)
        L_0x011b:
            com.miui.common.card.models.LineCardModel r9 = new com.miui.common.card.models.LineCardModel
            r9.<init>()
            r6.add(r9)
            java.lang.Integer r9 = java.lang.Integer.valueOf(r8)
            r2.remove(r9)
            goto L_0x0145
        L_0x012b:
            int r9 = r1.size()
            if (r9 <= 0) goto L_0x0145
            java.lang.Object r9 = r1.get(r3)
            com.miui.common.card.models.BaseCardModel r9 = (com.miui.common.card.models.BaseCardModel) r9
            r6.add(r9)
            com.miui.common.card.models.LineCardModel r10 = new com.miui.common.card.models.LineCardModel
            r10.<init>()
            r6.add(r10)
            r1.remove(r9)
        L_0x0145:
            int r8 = r8 + 1
            goto L_0x00f7
        L_0x0148:
            boolean r1 = r2.isEmpty()
            if (r1 != 0) goto L_0x01a5
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r3 = "advMap is not empty when for() finished, the map size is  "
            r1.append(r3)
            int r3 = r2.size()
            r1.append(r3)
            java.lang.String r1 = r1.toString()
            android.util.Log.d(r7, r1)
            java.util.Set r1 = r2.entrySet()
            java.util.Iterator r1 = r1.iterator()
        L_0x016e:
            boolean r2 = r1.hasNext()
            if (r2 == 0) goto L_0x01a5
            java.lang.Object r2 = r1.next()
            java.util.Map$Entry r2 = (java.util.Map.Entry) r2
            java.lang.Object r2 = r2.getValue()
            com.miui.common.card.models.BaseCardModel r2 = (com.miui.common.card.models.BaseCardModel) r2
            r6.add(r2)
            boolean r3 = r2 instanceof com.miui.common.card.models.AdvListTitleCardModel
            if (r3 == 0) goto L_0x0196
            com.miui.common.card.models.AdvListTitleCardModel r2 = (com.miui.common.card.models.AdvListTitleCardModel) r2
            java.util.List r2 = r2.getSubCardModelList()
            boolean r3 = r2.isEmpty()
            if (r3 != 0) goto L_0x0196
            r6.addAll(r2)
        L_0x0196:
            com.miui.common.card.models.LineCardModel r2 = new com.miui.common.card.models.LineCardModel
            r2.<init>()
            r6.add(r2)
            goto L_0x016e
        L_0x019f:
            com.miui.securityscan.scanner.l r2 = r11.ca
            java.util.ArrayList r6 = com.miui.securityscan.cards.c.b(r2, r1)
        L_0x01a5:
            r0.remove(r4)
            r0.addAll(r4, r6)
            com.miui.securityscan.cards.c.a((java.util.ArrayList<com.miui.common.card.models.BaseCardModel>) r0)
            goto L_0x01b4
        L_0x01af:
            com.miui.securityscan.scanner.l r0 = r11.ca
            com.miui.securityscan.cards.c.a((com.miui.securityscan.scanner.O.a) r0, (boolean) r1)
        L_0x01b4:
            com.miui.securityscan.cards.c.a()
            com.miui.common.card.CardViewAdapter r0 = r11.G
            if (r0 == 0) goto L_0x01cd
            java.util.ArrayList r0 = com.miui.securityscan.cards.c.h()
            r1 = 2
            com.miui.securityscan.cards.c.a((java.util.ArrayList<com.miui.common.card.models.BaseCardModel>) r0, (int) r1)
            com.miui.common.card.CardViewAdapter r1 = r11.G
            r1.setModelList(r0)
            com.miui.common.card.CardViewAdapter r0 = r11.G
            r0.notifyDataSetChanged()
        L_0x01cd:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.securityscan.L.A():void");
    }

    private boolean B() {
        return this.i && this.h;
    }

    private void C() {
        int i2;
        Resources resources;
        if (e.b() <= 9) {
            resources = getResources();
            i2 = R.dimen.activity_actionbar_icon_margin_lr_v11;
        } else {
            resources = getResources();
            i2 = R.dimen.activity_actionbar_icon_margin_lr;
        }
        int dimensionPixelSize = resources.getDimensionPixelSize(i2);
        RelativeLayout relativeLayout = this.n;
        relativeLayout.setPaddingRelative(dimensionPixelSize, relativeLayout.getPaddingTop(), dimensionPixelSize, this.n.getPaddingBottom());
    }

    private void D() {
        Activity activity = getActivity();
        if (a(activity)) {
            this.Ja = new ArrayList();
            com.miui.securityscan.c.e.a((Context) activity, "data_config").c("is_homepage_operated", false);
            this.O = 0;
            this.B.setText(getString(R.string.security_center_slogan));
            boolean z2 = this.w;
            this.F = new CardViewAdapter(activity, this.m, 0);
            this.F.setNotifyOnChange(false);
        }
    }

    private void E() {
        if (this.p == null) {
            this.p = (ViewStub) this.Sa.findViewById(R.id.interstitial_ad_viewstub);
            this.p.setOnInflateListener(new u(this));
            this.p.inflate();
        }
    }

    private void F() {
        this.ga = getResources().getDimensionPixelSize(R.dimen.main_contentview_transition_y);
        this.ha = getResources().getDimensionPixelSize(R.dimen.main_contentview_transition_y_end);
    }

    private void G() {
        this.o = (ViewStub) this.Sa.findViewById(R.id.optmizing_bar_viewstub);
        this.o.setOnInflateListener(new J(this));
        this.o.inflate();
        Activity activity = getActivity();
        if (a(activity)) {
            this.G = new CardViewAdapter(activity, this.m, 1);
            this.r = (ViewStub) this.Sa.findViewById(R.id.sec_result_viewstub);
            this.r.setOnInflateListener(new C0546f(this));
            this.r.inflate();
        }
    }

    private void H() {
        this.U = new C0573u(this);
        this.V = new x(this);
        this.W = new m(this);
        this.X = new j(this);
        this.Y = new com.miui.securityscan.b.p(this);
        this.Z = new C0566m(this);
        this.aa = new com.miui.securityscan.b.d(this);
        this.ba = new C0572t(this);
        this.ca = new C0565l(this);
        this.Ma = new p(this);
        this.Na = new q(this);
        this.Oa = new k(this);
    }

    private boolean I() {
        Activity activity = getActivity();
        if (!a(activity)) {
            return false;
        }
        return !com.miui.securityscan.c.e.a((Context) activity, "data_config").a("is_homepage_operated", true) && !this.Ca;
    }

    private void J() {
        this.Fa = new com.miui.securityscan.g.b(this);
        this.Fa.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);
    }

    private void K() {
        this.Da = new c(this);
        this.Da.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);
    }

    private void L() {
        this.Ea = new d(this);
        this.Ea.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);
    }

    private void M() {
        Activity activity = getActivity();
        if (a(activity)) {
            try {
                this.Qa = new b.b.h.a(activity, "");
                this.Qa.b();
            } catch (Exception e2) {
                Log.e("com.miui.securityscan.MainActivity", "preLoadInterstitialAd", e2);
            }
        }
    }

    /* access modifiers changed from: private */
    public void N() {
        if (com.miui.securityscan.f.a.a(this.k)) {
            com.miui.securityscan.f.a.a((View) this.D);
        }
        PathInterpolator pathInterpolator = new PathInterpolator(0.4f, 0.48f, 0.25f, 1.0f);
        com.miui.securityscan.i.w.b((View) this.H, 400, (TimeInterpolator) pathInterpolator);
        com.miui.securityscan.i.w.a(this.K, 400, 0.0f, (float) (-this.ga), pathInterpolator);
        P();
        int q2 = q();
        G.e((long) q2);
        Activity activity = getActivity();
        if (a(activity)) {
            h.a((Context) activity, q2);
        }
        this.g = C0557d.PREDICT_SCANNED;
    }

    private void O() {
        new D(this).start();
    }

    private void P() {
        Activity activity = getActivity();
        if (a(activity)) {
            this.I.setText(o.b(activity));
            this.J.setText(o.b(activity));
            this.H.setText(o.a(activity));
        }
    }

    private void Q() {
        this.ab = new b(this);
        this._a.setEventListener(this.ab);
    }

    /* access modifiers changed from: private */
    public void R() {
        v c2 = this.j.c();
        Log.d("setPredictScore", "PredictScanItem: " + c2);
        if (c2 == null) {
            float f2 = 0.0f;
            if (this.S > 0) {
                f2 = ((float) (SystemClock.elapsedRealtime() - this.S)) / 1000.0f;
                G.g((long) Math.round(f2));
            }
            boolean z2 = ((double) f2) < 1.5d;
            long j2 = 1500;
            w wVar = this.m;
            q qVar = new q(this);
            if (!z2) {
                j2 = 0;
            }
            wVar.postDelayed(qVar, j2);
            this.m.postDelayed(new r(this), z2 ? 3300 : 1800);
            return;
        }
        this.j.a(c2, (O.d) this.Oa);
    }

    private void S() {
        G.o("scan");
        this.Ba = false;
        this.g = C0557d.PREDICT_SCANNING;
        this.sa = false;
        this.ta = false;
        this.I.setText(getString(R.string.hints_scanning_text));
        this.J.setText(getString(R.string.hints_scanning_text));
        this.S = SystemClock.elapsedRealtime();
        this.j.a((n) this.W, (O.e) this.U);
        R();
        this._a.e();
    }

    private void T() {
        PathInterpolator pathInterpolator = new PathInterpolator(0.6f, 0.35f, 0.19f, 1.0f);
        ObjectAnimator ofFloat = ObjectAnimator.ofFloat(this._a, AnimatedProperty.PROPERTY_NAME_ALPHA, new float[]{1.0f, 0.0f});
        ofFloat.setDuration(400);
        ofFloat.setInterpolator(pathInterpolator);
        ofFloat.start();
        this.Za.setAlpha(0.0f);
        this.Za.setVisibility(0);
        this.Ya.setScaleX(1.785f);
        this.Ya.setScaleY(1.785f);
        this.Ya.setTranslationY(78.0f);
        ObjectAnimator ofFloat2 = ObjectAnimator.ofFloat(this.Ya, AnimatedProperty.PROPERTY_NAME_SCALE_X, new float[]{1.785f, 1.0f});
        ofFloat2.setDuration(400);
        ofFloat2.setInterpolator(pathInterpolator);
        ObjectAnimator ofFloat3 = ObjectAnimator.ofFloat(this.Ya, AnimatedProperty.PROPERTY_NAME_SCALE_Y, new float[]{1.785f, 1.0f});
        ofFloat3.setDuration(400);
        ofFloat3.setInterpolator(pathInterpolator);
        ObjectAnimator ofFloat4 = ObjectAnimator.ofFloat(this.Ya, "translationY", new float[]{78.0f, 0.0f});
        ofFloat4.setDuration(400);
        ofFloat4.setInterpolator(pathInterpolator);
        ObjectAnimator ofFloat5 = ObjectAnimator.ofFloat(this.L, AnimatedProperty.PROPERTY_NAME_SCALE_X, new float[]{1.0f, 0.89f});
        ofFloat5.setDuration(300);
        ofFloat5.setInterpolator(pathInterpolator);
        ObjectAnimator ofFloat6 = ObjectAnimator.ofFloat(this.L, AnimatedProperty.PROPERTY_NAME_SCALE_Y, new float[]{1.0f, 0.89f});
        ofFloat6.setDuration(300);
        ofFloat6.setInterpolator(pathInterpolator);
        ObjectAnimator ofFloat7 = ObjectAnimator.ofFloat(this.Za, AnimatedProperty.PROPERTY_NAME_ALPHA, new float[]{0.0f, 1.0f});
        ofFloat7.setDuration(400);
        ofFloat7.setInterpolator(pathInterpolator);
        ofFloat7.start();
        ObjectAnimator ofFloat8 = ObjectAnimator.ofFloat(this._a, AnimatedProperty.PROPERTY_NAME_SCALE_X, new float[]{1.0f, 0.56f});
        ofFloat8.setDuration(400);
        ofFloat8.setInterpolator(pathInterpolator);
        ObjectAnimator ofFloat9 = ObjectAnimator.ofFloat(this._a, AnimatedProperty.PROPERTY_NAME_SCALE_Y, new float[]{1.0f, 0.56f});
        ofFloat9.setDuration(400);
        ofFloat9.setInterpolator(pathInterpolator);
        ObjectAnimator ofFloat10 = ObjectAnimator.ofFloat(this._a, "translationY", new float[]{0.0f, -78.0f});
        ofFloat10.setDuration(400);
        ofFloat10.setInterpolator(pathInterpolator);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(new Animator[]{ofFloat2, ofFloat3, ofFloat5, ofFloat6, ofFloat8, ofFloat9});
        animatorSet.start();
        AnimatorSet animatorSet2 = new AnimatorSet();
        animatorSet2.playTogether(new Animator[]{ofFloat4, ofFloat10});
        animatorSet2.start();
        Activity activity = getActivity();
        if (a(activity)) {
            com.miui.securityscan.i.w.a(activity.getApplicationContext(), (View) this.s, this.A);
        }
    }

    private void U() {
        PathInterpolator pathInterpolator = new PathInterpolator(0.6f, 0.35f, 0.19f, 1.0f);
        ObjectAnimator ofFloat = ObjectAnimator.ofFloat(this._a, AnimatedProperty.PROPERTY_NAME_ALPHA, new float[]{0.0f, 1.0f});
        ofFloat.setDuration(400);
        ofFloat.setInterpolator(pathInterpolator);
        ofFloat.start();
        ObjectAnimator ofFloat2 = ObjectAnimator.ofFloat(this.Ya, AnimatedProperty.PROPERTY_NAME_SCALE_X, new float[]{1.0f, 1.785f});
        ofFloat2.setDuration(400);
        ofFloat2.setInterpolator(pathInterpolator);
        ObjectAnimator ofFloat3 = ObjectAnimator.ofFloat(this.Ya, AnimatedProperty.PROPERTY_NAME_SCALE_Y, new float[]{1.0f, 1.785f});
        ofFloat3.setDuration(400);
        ofFloat3.setInterpolator(pathInterpolator);
        ObjectAnimator ofFloat4 = ObjectAnimator.ofFloat(this.L, AnimatedProperty.PROPERTY_NAME_SCALE_X, new float[]{0.89f, 1.0f});
        ofFloat4.setDuration(300);
        ofFloat4.setInterpolator(pathInterpolator);
        ObjectAnimator ofFloat5 = ObjectAnimator.ofFloat(this.L, AnimatedProperty.PROPERTY_NAME_SCALE_Y, new float[]{0.89f, 1.0f});
        ofFloat5.setDuration(300);
        ofFloat5.setInterpolator(pathInterpolator);
        ObjectAnimator ofFloat6 = ObjectAnimator.ofFloat(this.Za, AnimatedProperty.PROPERTY_NAME_ALPHA, new float[]{1.0f, 0.0f});
        ofFloat6.setDuration(200);
        ofFloat6.setInterpolator(pathInterpolator);
        ofFloat6.start();
        ofFloat6.addListener(new y(this));
        ObjectAnimator ofFloat7 = ObjectAnimator.ofFloat(this._a, AnimatedProperty.PROPERTY_NAME_SCALE_X, new float[]{0.56f, 1.0f});
        ofFloat7.setDuration(400);
        ofFloat7.setInterpolator(pathInterpolator);
        ObjectAnimator ofFloat8 = ObjectAnimator.ofFloat(this._a, AnimatedProperty.PROPERTY_NAME_SCALE_Y, new float[]{0.56f, 1.0f});
        ofFloat8.setDuration(400);
        ofFloat8.setInterpolator(pathInterpolator);
        ObjectAnimator ofFloat9 = ObjectAnimator.ofFloat(this._a, "translationY", new float[]{-78.0f, 0.0f});
        ofFloat9.setDuration(400);
        ofFloat9.setInterpolator(pathInterpolator);
        ofFloat9.start();
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(new Animator[]{ofFloat2, ofFloat3, ofFloat4, ofFloat5, ofFloat7, ofFloat8});
        animatorSet.start();
    }

    private void V() {
        b.b.c.j.d.a(new B(this));
    }

    private void a(Context context, com.miui.securityscan.f.b bVar) {
        if (M.m() && bVar.b()) {
            com.miui.securityscan.f.a.a(context, this.D, this.k);
            com.miui.securityscan.f.a.a(context);
            G.k();
        }
    }

    private void a(View view) {
        AutoPasteListView autoPasteListView;
        int i2;
        Activity activity = getActivity();
        if (a(activity)) {
            this.C = (Button) view.findViewById(R.id.btn_back);
            this.C.setOnClickListener(this);
            this.B = (TextView) view.findViewById(R.id.security_title);
            this.B.setAlpha(0.0f);
            this.D = (GifImageView) view.findViewById(R.id.iv_ads_link);
            this.D.setOnClickListener(this);
            this.E = (Button) view.findViewById(R.id.settings);
            this.E.setOnClickListener(this);
            this.ja = (RelativeLayout) view.findViewById(R.id.content_frame);
            this.n = (RelativeLayout) view.findViewById(R.id.rl_main_title);
            this.ia = (LinearLayout) view.findViewById(R.id.ll_main_refresh_root);
            this.Ya = (ImageView) view.findViewById(R.id.result_circle);
            this.Za = view.findViewById(R.id.result_score_content);
            this._a = (MainVideoView) view.findViewById(R.id.video_view);
            Q();
            C();
            this.oa = (OverScrollLayout) view.findViewById(R.id.main_refresh_root);
            this.oa.setReleaseListener(new E(this));
            this.y = (AutoPasteListView) view.findViewById(R.id.card_list);
            this.H = (TextView) view.findViewById(R.id.btn_action);
            this.H.setOnClickListener(this);
            this.I = (TextView) view.findViewById(R.id.status_bar);
            this.I.setText(getString(R.string.examination_score_100));
            this.J = (TextView) view.findViewById(R.id.status_bar_bottom);
            this.J.setText(getString(R.string.examination_score_100));
            this.L = (ScoreTextView) view.findViewById(R.id.score);
            this.M = (ScoreTextView) view.findViewById(R.id.result_score);
            this.L.setScore(100);
            com.miui.securityscan.i.w.a((Context) activity, 100, this.H);
            com.miui.securityscan.i.w.b((Context) activity, 100, this.J);
            this.K = (RelativeLayout) view.findViewById(R.id.content_main);
            int i3 = this.Va;
            if (i3 > 0) {
                this.y.setAlignHeight(i3);
            }
            List<Integer> list = this.Wa;
            if (list != null && list.size() > 0) {
                this.y.setItemHeightList(this.Wa);
            }
            this.y.setHeavySlideNoAnim(true);
            if (Build.IS_INTERNATIONAL_BUILD) {
                autoPasteListView = this.y;
                i2 = 2;
            } else {
                autoPasteListView = this.y;
                i2 = 0;
            }
            autoPasteListView.setOverScrollMode(i2);
            this.y.setMarginTopPixel(getResources().getDimensionPixelSize(R.dimen.main_card_list_margin_top));
            this.y.getViewTreeObserver().addOnGlobalLayoutListener(new F(this));
            this.y.setOnScrollListener(new G(this));
            this.y.setOnTouchListener(new H(this));
            this.y.setOnScrollPercentChangeListener(new I(this));
        }
    }

    private void a(C0568o oVar) {
        if (isAdded()) {
            Log.d("com.miui.securityscan.MainActivity", "refreshOptimizingUi  optimizeItem = " + getString(oVar.a()));
            if (oVar == C0568o.CLEAR_ACCELERATION) {
                this.s.a(oVar, (Animator.AnimatorListener) new a());
                this.s.a(oVar, getString(R.string.optmizingbar_title_acceleration));
                return;
            }
            this.s.a(oVar, (Animator.AnimatorListener) null);
            this.ba.a((WeakReference<C0568o>) new WeakReference(oVar));
            this.j.a(oVar, (O.d) this.ba);
        }
    }

    /* access modifiers changed from: private */
    public boolean a(Activity activity) {
        return activity != null && !activity.isDestroyed();
    }

    /* access modifiers changed from: private */
    public void c(boolean z2) {
        Activity activity = getActivity();
        if (a(activity)) {
            Context applicationContext = activity.getApplicationContext();
            l();
            com.miui.securityscan.cards.k.a(applicationContext).a((k.a) this.Ma);
            g.a(applicationContext).b((g.a) this.Na);
            if (z2) {
                S();
            }
            a(applicationContext, this.k);
            com.miui.securityscan.f.a.a(applicationContext, M.m());
            this.e = true;
        }
    }

    private void d(boolean z2) {
        int i2;
        String string = getString(17039360);
        Activity activity = getActivity();
        if (a(activity)) {
            if (!this.Ga && com.miui.securityscan.i.g.b()) {
                this.u = com.miui.securityscan.i.g.a(activity, getString(R.string.exit_dialog_garbage_clean_title), getString(R.string.exit_dialog_garbage_clean_message, new Object[]{com.miui.securityscan.i.g.a()}), getString(R.string.exit_dialog_garbage_clean_positive_button), string, new C0547g(this, activity), new C0548h(this, z2, activity));
                i2 = 3;
            } else if (!this.Ha && com.miui.securityscan.i.g.d()) {
                this.u = com.miui.securityscan.i.g.a(activity, getString(R.string.exit_dialog_scan_title), getString(R.string.exit_dialog_scan_message), getString(R.string.exit_dialog_scan_positive_button), string, new C0549i(this), new C0550j(this, z2, activity));
                i2 = 6;
            } else if (this.Ia || !com.miui.securityscan.i.g.c()) {
                if (z2) {
                    b();
                }
                this.m.removeCallbacksAndMessages((Object) null);
                activity.finish();
                return;
            } else {
                this.u = com.miui.securityscan.i.g.a(activity, getString(R.string.exit_dialog_release_storage_title), getString(R.string.exit_dialog_release_storage_message), getString(R.string.exit_dialog_release_storage_positive_button), string, new C0551k(this, activity), new C0552l(this, z2, activity));
                i2 = 9;
            }
            G.a(i2);
        }
    }

    /* access modifiers changed from: private */
    public void v() {
        Log.d("com.miui.securityscan.MainActivity", "backToNormalState() outside");
        NativeInterstitialAdLayout nativeInterstitialAdLayout = this.t;
        if (nativeInterstitialAdLayout != null) {
            nativeInterstitialAdLayout.setVisibility(8);
        }
        if (SystemClock.elapsedRealtime() - this.Ka >= 400) {
            Log.d("com.miui.securityscan.MainActivity", "backToNormalState() inside");
            this.g = C0557d.NORMAL;
            Button button = this.C;
            if (button != null) {
                button.setVisibility(8);
            }
            if (this.P > 0) {
                G.f((SystemClock.elapsedRealtime() - this.P) / 1000);
            }
            G.d((long) this.l.j());
            this.O = 0;
            a(this.O == 0, false);
            this.B.setText(getString(R.string.security_center_slogan));
            this.Q = SystemClock.elapsedRealtime();
            this.R = SystemClock.elapsedRealtime();
            this.B.setAlpha(0.0f);
            this.ja.setAlpha(1.0f);
            this.J.setVisibility(8);
            PathInterpolator pathInterpolator = new PathInterpolator(0.6f, 0.4f, 0.2f, 1.0f);
            Activity activity = getActivity();
            if (a(activity)) {
                U();
                com.miui.securityscan.i.w.a(activity.getApplicationContext(), this.A, this.oa, true);
                com.miui.securityscan.i.w.a(this.K, 400, 0.0f, (float) (-this.ga), pathInterpolator);
                com.miui.securityscan.i.w.b((View) this.H, 400, (TimeInterpolator) pathInterpolator);
                com.miui.securityscan.i.w.a((View) this.I, 300, 0);
                com.miui.securityscan.i.w.a((View) this.L, 300, 0);
                if (this.O != 1) {
                    V();
                }
                ((MainActivity) activity).a(true, true);
            }
        }
    }

    private void w() {
        c cVar = this.Da;
        if (cVar != null) {
            cVar.cancel(true);
        }
        d dVar = this.Ea;
        if (dVar != null) {
            dVar.cancel(true);
        }
        com.miui.securityscan.g.b bVar = this.Fa;
        if (bVar != null) {
            bVar.cancel(true);
        }
    }

    private void x() {
        long a2 = this.l.a();
        if (a2 > GarbageCleanModel.CLEAN_VALUE) {
            String string = getString(17039360);
            String string2 = getString(R.string.exit_dialog_garbage_clean_title);
            String string3 = getString(R.string.exit_dialog_garbage_clean_message, new Object[]{com.miui.securityscan.i.g.a(a2)});
            String string4 = getString(R.string.exit_dialog_garbage_clean_positive_button);
            Activity activity = getActivity();
            if (a(activity)) {
                this.v = com.miui.securityscan.i.g.a(activity, string2, string3, string4, string, new n(this, activity), new o(this));
                G.a(12);
                return;
            }
            return;
        }
        v();
    }

    private void y() {
        List<com.miui.antivirus.model.k> n2 = this.l.n();
        if (n2 == null || n2.isEmpty()) {
            this.l.b((List<com.miui.antivirus.model.k>) null);
            return;
        }
        this.l.b(n2);
        ArrayList arrayList = new ArrayList(n2);
        Activity activity = getActivity();
        if (a(activity)) {
            O.a(activity.getApplicationContext()).a((List<com.miui.antivirus.model.k>) arrayList);
        }
    }

    private void z() {
        this.P = SystemClock.elapsedRealtime();
        T();
        com.miui.securityscan.i.w.a((View) this.B, 400, 0);
        this.J.setAlpha(0.0f);
        com.miui.securityscan.i.w.a((View) this.J, 400, 300);
        com.miui.securityscan.i.w.a(this.I, 300);
        com.miui.securityscan.i.w.a(this.L, 300);
        P();
        this.O = 1;
        this.B.setText(getString(R.string.app_name_securitycenter));
        this.g = C0557d.SCANNED;
        this.z.setAdapter(this.G);
        A();
        G.i();
        this.Ka = SystemClock.elapsedRealtime();
        if (Build.IS_INTERNATIONAL_BUILD && i()) {
            com.miui.securityscan.i.w.a(this.t);
            this.t.setVisibility(0);
            this.t.a(this.l.j());
            this.m.postDelayed(new w(this), 1800);
            this.m.postDelayed(new x(this), 2200);
        }
    }

    public void a(int i2) {
        MainVideoView mainVideoView;
        float f2;
        if (i2 >= 80 || this.bb != 0) {
            if (i2 >= 80 && this.bb == 1) {
                this.bb = 0;
                mainVideoView = this._a;
                f2 = 0.0f;
            }
            this._a.a((float) this.bb, this.cb);
        }
        this.bb = 1;
        mainVideoView = this._a;
        f2 = 1.0f;
        mainVideoView.setRenderState(f2);
        this._a.a((float) this.bb, this.cb);
    }

    public void a(BaseCardModel baseCardModel) {
        CardViewAdapter cardViewAdapter = this.G;
        if (cardViewAdapter != null) {
            if (baseCardModel instanceof ListTitleCheckboxCardModel) {
                if (((ListTitleCheckboxCardModel) baseCardModel).isSafe()) {
                    cardViewAdapter = this.G;
                }
                this.G.notifyDataSetChanged();
            }
            com.miui.securityscan.cards.c.a((List<BaseCardModel>) cardViewAdapter.getModelList(), baseCardModel);
            this.G.notifyDataSetChanged();
        }
        q();
        Activity activity = getActivity();
        if (a(activity)) {
            if (this.I != null) {
                String b2 = o.b(activity);
                this.I.setText(b2);
                this.J.setText(b2);
            }
            this.H.setText(o.a(activity));
        }
    }

    public void a(BaseCardModel baseCardModel, int i2) {
        BaseCardModel next;
        Log.d("com.miui.securityscan.MainActivity", "removeMainPageSingleModel position:" + i2);
        if (baseCardModel != null) {
            if (i2 == 1) {
                CardViewAdapter cardViewAdapter = this.F;
                if (cardViewAdapter != null) {
                    com.miui.securityscan.cards.c.a((List<BaseCardModel>) cardViewAdapter.getModelList(), baseCardModel);
                    this.F.notifyDataSetChanged();
                }
            } else if (i2 == 2) {
                CardViewAdapter cardViewAdapter2 = this.G;
                if (cardViewAdapter2 != null) {
                    com.miui.securityscan.cards.c.a((List<BaseCardModel>) cardViewAdapter2.getModelList(), baseCardModel);
                    this.G.notifyDataSetChanged();
                }
                ArrayList<BaseCardModel> arrayList = this.N;
                if (arrayList != null && (baseCardModel instanceof AdvCardModel)) {
                    AdvCardModel advCardModel = (AdvCardModel) baseCardModel;
                    BaseCardModel baseCardModel2 = null;
                    Iterator<BaseCardModel> it = arrayList.iterator();
                    while (true) {
                        if (!it.hasNext()) {
                            break;
                        }
                        next = it.next();
                        if (next instanceof AdvCardModel) {
                            AdvCardModel advCardModel2 = (AdvCardModel) next;
                            if ((!advCardModel.isLocal() && advCardModel.getId() == advCardModel2.getId()) || (advCardModel.isLocal() && advCardModel.getDataId() != null && advCardModel.getDataId().equals(advCardModel2.getDataId()))) {
                                baseCardModel2 = next;
                            }
                        }
                    }
                    baseCardModel2 = next;
                    com.miui.securityscan.cards.c.a((List<BaseCardModel>) this.N, baseCardModel2);
                }
            }
        }
    }

    public void a(BaseCardModel baseCardModel, List<BaseCardModel> list, int i2) {
        Log.d("com.miui.securityscan.MainActivity", "removeMainPageGroupModel position:" + i2);
        if (baseCardModel != null) {
            if (i2 == 1) {
                CardViewAdapter cardViewAdapter = this.F;
                if (cardViewAdapter != null) {
                    com.miui.securityscan.cards.c.a((List<BaseCardModel>) cardViewAdapter.getModelList(), baseCardModel);
                    this.F.getModelList().removeAll(list);
                    this.F.notifyDataSetChanged();
                }
            } else if (i2 == 2) {
                CardViewAdapter cardViewAdapter2 = this.G;
                if (cardViewAdapter2 != null) {
                    com.miui.securityscan.cards.c.a((List<BaseCardModel>) cardViewAdapter2.getModelList(), baseCardModel);
                    this.G.getModelList().removeAll(list);
                    this.G.notifyDataSetChanged();
                }
                ArrayList<BaseCardModel> arrayList = this.N;
                if (arrayList != null && (baseCardModel instanceof AdvListTitleCardModel)) {
                    BaseCardModel baseCardModel2 = null;
                    Iterator<BaseCardModel> it = arrayList.iterator();
                    while (true) {
                        if (!it.hasNext()) {
                            break;
                        }
                        BaseCardModel next = it.next();
                        if ((next instanceof AdvListTitleCardModel) && ((AdvListTitleCardModel) baseCardModel).getId() == ((AdvListTitleCardModel) next).getId()) {
                            baseCardModel2 = next;
                            break;
                        }
                    }
                    com.miui.securityscan.cards.c.a((List<BaseCardModel>) this.N, baseCardModel2);
                    if (list != null) {
                        ArrayList arrayList2 = new ArrayList();
                        ArrayList arrayList3 = new ArrayList();
                        for (BaseCardModel next2 : list) {
                            if (next2 instanceof AdvCardModel) {
                                AdvCardModel advCardModel = (AdvCardModel) next2;
                                if (!advCardModel.isLocal()) {
                                    arrayList2.add(Integer.valueOf(advCardModel.getId()));
                                } else {
                                    arrayList3.add(advCardModel.getDataId());
                                }
                            }
                        }
                        ArrayList arrayList4 = new ArrayList();
                        Iterator<BaseCardModel> it2 = this.N.iterator();
                        while (it2.hasNext()) {
                            BaseCardModel next3 = it2.next();
                            if (next3 instanceof AdvCardModel) {
                                AdvCardModel advCardModel2 = (AdvCardModel) next3;
                                if ((!advCardModel2.isLocal() && arrayList2.contains(Integer.valueOf(advCardModel2.getId()))) || (advCardModel2.isLocal() && arrayList3.contains(advCardModel2.getDataId()))) {
                                    arrayList4.add(next3);
                                }
                            }
                        }
                        this.N.removeAll(arrayList4);
                    }
                }
            }
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:14:0x003e, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void a(java.util.ArrayList<com.miui.common.card.models.BaseCardModel> r4) {
        /*
            r3 = this;
            java.lang.Object r0 = r3.ka
            monitor-enter(r0)
            com.miui.common.card.CardViewAdapter r1 = r3.F     // Catch:{ all -> 0x003f }
            if (r1 == 0) goto L_0x003d
            if (r4 != 0) goto L_0x000a
            goto L_0x003d
        L_0x000a:
            r1 = 1
            r3.ea = r1     // Catch:{ all -> 0x003f }
            java.util.ArrayList r1 = new java.util.ArrayList     // Catch:{ all -> 0x003f }
            r1.<init>()     // Catch:{ all -> 0x003f }
            java.util.ArrayList r2 = com.miui.securityscan.cards.b.a()     // Catch:{ all -> 0x003f }
            r1.addAll(r2)     // Catch:{ all -> 0x003f }
            r1.addAll(r4)     // Catch:{ all -> 0x003f }
            java.util.ArrayList r4 = com.miui.securityscan.cards.c.c(r1)     // Catch:{ all -> 0x003f }
            boolean r1 = miui.os.Build.IS_INTERNATIONAL_BUILD     // Catch:{ all -> 0x003f }
            if (r1 != 0) goto L_0x002c
            com.miui.common.card.models.BottomPlaceCardModel r1 = new com.miui.common.card.models.BottomPlaceCardModel     // Catch:{ all -> 0x003f }
            r1.<init>()     // Catch:{ all -> 0x003f }
            r4.add(r1)     // Catch:{ all -> 0x003f }
        L_0x002c:
            com.miui.common.card.CardViewAdapter r1 = r3.F     // Catch:{ all -> 0x003f }
            r1.clear()     // Catch:{ all -> 0x003f }
            com.miui.common.card.CardViewAdapter r1 = r3.F     // Catch:{ all -> 0x003f }
            r1.addAll(r4)     // Catch:{ all -> 0x003f }
            com.miui.common.card.CardViewAdapter r4 = r3.F     // Catch:{ all -> 0x003f }
            r4.notifyDataSetChanged()     // Catch:{ all -> 0x003f }
            monitor-exit(r0)     // Catch:{ all -> 0x003f }
            return
        L_0x003d:
            monitor-exit(r0)     // Catch:{ all -> 0x003f }
            return
        L_0x003f:
            r4 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x003f }
            throw r4
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.securityscan.L.a(java.util.ArrayList):void");
    }

    public void a(boolean z2) {
        this.xa = z2;
    }

    public void a(boolean z2, boolean z3) {
        CardViewAdapter cardViewAdapter = this.F;
        if (cardViewAdapter != null && cardViewAdapter.isCanAutoScroll() != z2) {
            if (!z2) {
                this.F.resetViewPager();
            }
            this.F.setCanAutoScroll(z2);
            this.F.notifyDataSetChanged(z3);
        }
    }

    public void b() {
        synchronized (this.na) {
            if (this.j != null) {
                this.j.a();
            }
            this.g = C0557d.NORMAL;
        }
    }

    public void b(int i2) {
        if (i2 < 80 && this.bb == 0) {
            this.bb = 1;
            this._a.a(0.0f, 1.0f);
        } else if (i2 >= 80 && this.bb == 1) {
            this.bb = 0;
            this._a.a(1.0f, 0.0f);
        }
        this._a.a((float) this.bb, this.cb);
    }

    public void b(boolean z2) {
        CardViewAdapter cardViewAdapter = this.F;
        if (cardViewAdapter != null) {
            cardViewAdapter.setDefaultStatShow(z2);
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:16:0x0041, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void c() {
        /*
            r5 = this;
            java.lang.Object r0 = r5.ma
            monitor-enter(r0)
            long r1 = android.os.SystemClock.elapsedRealtime()     // Catch:{ all -> 0x0042 }
            long r3 = r5.La     // Catch:{ all -> 0x0042 }
            long r1 = r1 - r3
            r3 = 400(0x190, double:1.976E-321)
            int r1 = (r1 > r3 ? 1 : (r1 == r3 ? 0 : -1))
            if (r1 >= 0) goto L_0x0012
            monitor-exit(r0)     // Catch:{ all -> 0x0042 }
            return
        L_0x0012:
            int r1 = r5.O     // Catch:{ all -> 0x0042 }
            r2 = 1
            if (r1 == r2) goto L_0x0040
            boolean r1 = r5.wa     // Catch:{ all -> 0x0042 }
            if (r1 == 0) goto L_0x001c
            goto L_0x0040
        L_0x001c:
            r5.wa = r2     // Catch:{ all -> 0x0042 }
            com.miui.securityscan.ui.main.MainVideoView r1 = r5._a     // Catch:{ all -> 0x0042 }
            r1.f()     // Catch:{ all -> 0x0042 }
            com.miui.securityscan.scanner.O r1 = r5.j     // Catch:{ all -> 0x0042 }
            r1.a()     // Catch:{ all -> 0x0042 }
            com.miui.securityscan.b.d r1 = r5.aa     // Catch:{ all -> 0x0042 }
            r1.f7610b = r2     // Catch:{ all -> 0x0042 }
            com.miui.securityscan.ui.main.MainVideoView r1 = r5._a     // Catch:{ all -> 0x0042 }
            r2 = 1067869798(0x3fa66666, float:1.3)
            r1.setPlaySpeed(r2)     // Catch:{ all -> 0x0042 }
            com.miui.securityscan.scanner.w r1 = r5.m     // Catch:{ all -> 0x0042 }
            com.miui.securityscan.m r2 = new com.miui.securityscan.m     // Catch:{ all -> 0x0042 }
            r2.<init>(r5)     // Catch:{ all -> 0x0042 }
            r1.post(r2)     // Catch:{ all -> 0x0042 }
            monitor-exit(r0)     // Catch:{ all -> 0x0042 }
            return
        L_0x0040:
            monitor-exit(r0)     // Catch:{ all -> 0x0042 }
            return
        L_0x0042:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x0042 }
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.securityscan.L.c():void");
    }

    public void c(int i2) {
        if (!this.Ba) {
            V();
        }
    }

    public void d() {
        int i2;
        Intent intent;
        Activity activity = getActivity();
        if (a(activity)) {
            this.Xa = true;
            if (!h.i()) {
                if (!Build.IS_INTERNATIONAL_BUILD) {
                    intent = new Intent("miui.intent.action.SYSTEM_PERMISSION_DECLARE");
                    intent.putExtra("all_purpose", getResources().getString(R.string.cta_main_purpose));
                    intent.putExtra("agree_desc", getResources().getString(R.string.cta_agree_desc));
                    intent.putExtra("privacy_policy", l.a());
                    intent.putExtra("mandatory_permission", false);
                    intent.putExtra("runtime_perm", new String[]{"android.permission-group.LOCATION"});
                    intent.putExtra("runtime_perm_desc", new String[]{getResources().getString(R.string.cta_HIPS_Perm_Location_Desc)});
                    i2 = 300;
                } else {
                    intent = l.a(activity, (String) null, (String) null, (String) null, (String) null);
                    i2 = 200;
                }
                startActivityForResult(intent, i2);
            }
        }
    }

    public void e() {
        if (!this.Xa) {
            d();
        }
    }

    public void f() {
        if (this.ra) {
            this.sa = true;
            long j2 = 3033;
            long j3 = 0;
            try {
                j2 = this._a.getDuration();
            } catch (Exception e2) {
                Log.e("com.miui.securityscan.MainActivity", "mMainVideoView.getDuration ", e2);
            }
            this._a.f();
            try {
                j3 = this._a.getCurrentPosition();
            } catch (Exception e3) {
                Log.e("com.miui.securityscan.MainActivity", "mMainVideoView.getCurrentPosition ", e3);
            }
            this.m.postDelayed(new t(this), j2 - j3);
        }
    }

    public void g() {
        if (!this.wa && this.G != null && this.f7559d) {
            this.ua = true;
            this._a.f();
            z();
        }
    }

    public void h() {
        synchronized (this.ka) {
            ArrayList<BaseCardModel> arrayList = this.ya;
            if (!(arrayList == null || this.y == null || this.F == null)) {
                if (!Build.IS_INTERNATIONAL_BUILD) {
                    arrayList.add(new BottomPlaceCardModel());
                }
                this.F.clear();
                this.F.addAll(arrayList);
                this.y.setAdapter(this.F);
                if (arrayList.size() >= 3) {
                    this.y.setAlignItem(2);
                } else {
                    this.y.setAlignItem(0);
                }
                this.F.notifyDataSetChanged();
            }
        }
    }

    public boolean i() {
        b.b.h.a aVar = this.Qa;
        if (aVar == null) {
            return false;
        }
        return aVar.a();
    }

    public void j() {
        this.F.notifyAppManagerMenuChangeListener();
        this.F.notifyDataSetChanged(false);
    }

    public void k() {
        Activity activity = getActivity();
        if (a(activity)) {
            int i2 = C.f7545a[this.g.ordinal()];
            if (i2 == 1 || i2 == 2) {
                if (B() || !I()) {
                    this.m.removeCallbacksAndMessages((Object) null);
                } else {
                    d(false);
                    return;
                }
            } else if (i2 != 3) {
                if (i2 == 4) {
                    this.m.removeCallbacksAndMessages((Object) null);
                    if (!B()) {
                        c();
                        return;
                    }
                } else if (i2 == 5) {
                    this.m.removeCallbacksAndMessages((Object) null);
                    if (!B()) {
                        x();
                        return;
                    }
                } else {
                    return;
                }
            } else if (B() || !I()) {
                this.m.removeCallbacksAndMessages((Object) null);
                b();
            } else {
                d(true);
                return;
            }
            activity.finish();
        }
    }

    /* access modifiers changed from: protected */
    public void l() {
        Activity activity = getActivity();
        if (a(activity)) {
            this.q = (ViewStub) this.Sa.findViewById(R.id.main_refresh_item);
            this.q.setOnInflateListener(new A(this));
            this.q.inflate();
            b.b.c.i.d.a((Context) activity).a(20001);
            if (!this.Xa) {
                d();
            }
        }
    }

    /* access modifiers changed from: protected */
    public void m() {
        AlertDialog alertDialog = this.u;
        if (alertDialog != null && alertDialog.isShowing()) {
            this.u.dismiss();
        }
        AlertDialog alertDialog2 = this.v;
        if (alertDialog2 != null && alertDialog2.isShowing()) {
            this.v.dismiss();
        }
    }

    public void n() {
        Activity activity = getActivity();
        if (a(activity)) {
            this.xa = true;
            this.l.a((Context) activity);
            int j2 = this.l.j();
            this.L.setScore(j2);
            this.M.setScore(j2);
            com.miui.securityscan.i.w.a((Context) activity, j2, (TextView) this.M);
            com.miui.securityscan.i.w.a((Context) activity, j2, this.H);
            com.miui.securityscan.i.w.b((Context) activity, j2, this.J);
            a(j2);
            this._a.b();
            if (this.O != 2) {
                P();
            }
            if (M.k()) {
                M.b(false);
                this.Y.f7623b = true;
                O.a((Context) activity).a((n) this.Y);
            }
        }
    }

    public void o() {
        Activity activity = getActivity();
        if (a(activity) && activity.getIntent().getBooleanExtra("extra_auto_optimize", false)) {
            C0557d dVar = this.g;
            if (dVar == C0557d.NORMAL || dVar == C0557d.PREDICT_SCANNED || dVar == C0557d.PREDICT_SCANNING) {
                this.m.postDelayed(new z(this), 640);
            }
        }
    }

    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
        D();
        synchronized (this.la) {
            this.za = true;
            if (this.Aa) {
                h();
            }
        }
        H();
        Looper.myQueue().addIdleHandler(this.db);
        this.da = true;
        K();
        J();
    }

    /* JADX WARNING: Removed duplicated region for block: B:50:? A[RETURN, SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:52:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onActivityResult(int r8, int r9, android.content.Intent r10) {
        /*
            r7 = this;
            super.onActivityResult(r8, r9, r10)
            android.app.Activity r0 = r7.getActivity()
            boolean r1 = r7.a((android.app.Activity) r0)
            if (r1 != 0) goto L_0x000e
            return
        L_0x000e:
            r1 = 100
            r2 = 1
            if (r8 == r1) goto L_0x00a0
            r1 = 103(0x67, float:1.44E-43)
            r3 = -1
            r4 = 0
            if (r8 == r1) goto L_0x0066
            r10 = 200(0xc8, float:2.8E-43)
            if (r8 == r10) goto L_0x004b
            r10 = 300(0x12c, float:4.2E-43)
            if (r8 == r10) goto L_0x0023
            goto L_0x00b5
        L_0x0023:
            if (r9 == 0) goto L_0x0030
            if (r9 == r2) goto L_0x0028
            goto L_0x0039
        L_0x0028:
            android.content.Context r8 = r0.getApplicationContext()
            com.miui.securityscan.i.l.a(r8, r2)
            goto L_0x0037
        L_0x0030:
            android.content.Context r8 = r0.getApplicationContext()
            com.miui.securityscan.i.l.a(r8, r4)
        L_0x0037:
            r7.Xa = r4
        L_0x0039:
            com.miui.securityscan.scanner.O r8 = com.miui.securityscan.scanner.O.a((android.content.Context) r0)
            com.miui.securityscan.b.m r9 = r7.W
            r8.a((com.miui.securityscan.b.n) r9)
            boolean r8 = r7.h
            if (r8 == 0) goto L_0x00b5
        L_0x0046:
            r7.s()
            goto L_0x00b5
        L_0x004b:
            if (r9 != r3) goto L_0x0057
            android.content.Context r8 = r0.getApplicationContext()
            com.miui.securityscan.i.l.a(r8, r2)
        L_0x0054:
            r7.Xa = r4
            goto L_0x0061
        L_0x0057:
            if (r9 != 0) goto L_0x0061
            android.content.Context r8 = r0.getApplicationContext()
            com.miui.securityscan.i.l.a(r8, r4)
            goto L_0x0054
        L_0x0061:
            boolean r8 = r7.h
            if (r8 == 0) goto L_0x00b5
            goto L_0x0046
        L_0x0066:
            if (r9 != r3) goto L_0x0086
            if (r10 == 0) goto L_0x00b5
            java.lang.String r8 = "unClearedCacheSize"
            r3 = -1
            long r5 = r10.getLongExtra(r8, r3)
            int r9 = (r5 > r3 ? 1 : (r5 == r3 ? 0 : -1))
            if (r9 == 0) goto L_0x00b5
            com.miui.securityscan.scanner.ScoreManager r9 = r7.l
            if (r9 == 0) goto L_0x00b5
            android.view.View r1 = r7.A
            if (r1 == 0) goto L_0x00b5
            long r3 = r10.getLongExtra(r8, r3)
            r9.a((long) r3)
            goto L_0x00a8
        L_0x0086:
            if (r9 != 0) goto L_0x00b5
            if (r10 == 0) goto L_0x0092
            java.lang.String r8 = "isCleanCanceled"
            boolean r8 = r10.getBooleanExtra(r8, r4)
            if (r8 != 0) goto L_0x00b5
        L_0x0092:
            com.miui.securityscan.scanner.ScoreManager r8 = r7.l
            if (r8 == 0) goto L_0x00b5
            android.view.View r9 = r7.A
            if (r9 == 0) goto L_0x00b5
            r9 = 0
            r8.a((long) r9)
            goto L_0x00a8
        L_0x00a0:
            com.miui.securityscan.scanner.ScoreManager r8 = r7.l
            if (r8 == 0) goto L_0x00b5
            android.view.View r8 = r7.A
            if (r8 == 0) goto L_0x00b5
        L_0x00a8:
            com.miui.securityscan.b.p r8 = r7.Y
            r8.f7623b = r2
            com.miui.securityscan.scanner.O r8 = com.miui.securityscan.scanner.O.a((android.content.Context) r0)
            com.miui.securityscan.b.p r9 = r7.Y
            r8.a((com.miui.securityscan.b.n) r9)
        L_0x00b5:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.securityscan.L.onActivityResult(int, int, android.content.Intent):void");
    }

    public void onClick(View view) {
        String str;
        Activity activity = getActivity();
        if (a(activity)) {
            switch (view.getId()) {
                case R.id.btn_action /*2131296540*/:
                    this.Ca = true;
                    s();
                    str = "optimize";
                    break;
                case R.id.btn_back /*2131296541*/:
                    k();
                    return;
                case R.id.iv_ads_link /*2131297100*/:
                    this.Ca = true;
                    G.j();
                    b.b.n.l.a((Context) activity, 100, this.k.a(100), "securitycenterScan", "com.miui.securitycenter_skinindex");
                    return;
                case R.id.settings /*2131297651*/:
                    startActivity(new Intent(activity, SettingsActivity.class).putExtra(":miui:starting_window_label", getString(R.string.activity_title_settings)));
                    str = "securitysettings";
                    break;
                default:
                    return;
            }
            G.o(str);
        }
    }

    public void onCreate(Bundle bundle) {
        com.miui.securityscan.cards.h.a((Context) Application.d());
        f.a(Application.d());
        super.onCreate(bundle);
        O();
        if (bundle != null) {
            this.w = bundle.getBoolean("isSecondScreen", false);
            this.Va = bundle.getInt("align_height", 0);
            this.Wa = bundle.getIntegerArrayList("item_height_list");
            this.Xa = bundle.getBoolean("cta_dialog_show", false);
        }
        this.Ra = true;
        Activity activity = getActivity();
        if (a(activity)) {
            Intent intent = activity.getIntent();
            this.h = intent.getBooleanExtra("extra_auto_optimize", false);
            this.i = intent.getBooleanExtra("extra_back_finish", false);
            String stringExtra = intent.getStringExtra("enter_homepage_way");
            if (!TextUtils.isEmpty(stringExtra)) {
                G.m("security_scan_channel_" + stringExtra);
            }
            Uri data = intent.getData();
            if (data != null) {
                String queryParameter = data.getQueryParameter("enter_homepage_way");
                if (!TextUtils.isEmpty(queryParameter)) {
                    G.m("security_scan_channel_" + queryParameter);
                }
            }
            this.l = ScoreManager.e();
            this.j = O.a((Context) activity);
            this.k = com.miui.securityscan.f.b.a((Context) activity);
            this.Pa = b.b.g.a.a();
            this.Pa.a(this);
        }
    }

    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        this.Sa = layoutInflater.inflate(R.layout.m_securityscan_main_fragment, (ViewGroup) null);
        F();
        a(this.Sa);
        return this.Sa;
    }

    public void onDestroy() {
        ArrayList<BaseCardModel> modelList;
        CardViewAdapter cardViewAdapter = this.F;
        if (cardViewAdapter != null) {
            cardViewAdapter.onDestroy();
        }
        this.Pa.c(this);
        MainVideoView mainVideoView = this._a;
        if (mainVideoView != null) {
            mainVideoView.a((Player.EventListener) this.ab);
        }
        CardViewAdapter cardViewAdapter2 = this.G;
        if (cardViewAdapter2 != null) {
            if (Build.IS_INTERNATIONAL_BUILD && (modelList = cardViewAdapter2.getModelList()) != null) {
                for (BaseCardModel next : modelList) {
                    if (next instanceof AdvInternationalCardModel) {
                        AdvCardModel advCardModel = (AdvCardModel) next;
                        com.miui.securityscan.cards.h.a(advCardModel.getObject());
                        this.Pa.b(advCardModel.getObject());
                    }
                }
            }
            this.G.onDestroy();
        }
        GifImageView gifImageView = this.D;
        if (gifImageView != null) {
            gifImageView.b();
        }
        this.m.removeCallbacksAndMessages((Object) null);
        super.onDestroy();
        if (this.e) {
            m();
        }
        f7557b = System.currentTimeMillis();
        Activity activity = getActivity();
        if (a(activity)) {
            Context applicationContext = activity.getApplicationContext();
            com.miui.securityscan.cards.k.a(applicationContext).b((k.a) this.Ma);
            g.a(applicationContext).d(this.Na);
            com.miui.securityscan.f.b.a((Context) activity).a();
            com.miui.securityscan.i.w.a();
            com.miui.securityscan.i.d.a(applicationContext.getCacheDir());
            w();
        }
    }

    public void onPause() {
        super.onPause();
        a(false, false);
    }

    public void onResume() {
        super.onResume();
        G.f();
        a(this.O == 0, this.Ra);
        this.Ra = false;
        this.P = SystemClock.elapsedRealtime();
        this.Q = SystemClock.elapsedRealtime();
        this.R = SystemClock.elapsedRealtime();
        if (this.O == 1) {
            G.i();
        }
        if (this.xa) {
            if (this.O != 1) {
                V();
            }
            this.xa = false;
        }
        this.Ba = false;
    }

    public void onSaveInstanceState(Bundle bundle) {
        bundle.putBoolean("isSecondScreen", this.w);
        bundle.putInt("align_height", this.y.getAlignHeight());
        bundle.putIntegerArrayList("item_height_list", this.y.getItemHeightList());
        bundle.putBoolean("cta_dialog_show", this.Xa);
    }

    public void onStop() {
        super.onStop();
        this.Ba = true;
        GifImageView gifImageView = this.D;
        if (gifImageView != null) {
            gifImageView.a();
        }
        int i2 = this.O;
        if (i2 == 1) {
            if (this.P > 0) {
                G.f((SystemClock.elapsedRealtime() - this.P) / 1000);
            }
            G.d((long) this.l.j());
        } else if (i2 == 0 && this.Q > 0) {
            G.h((SystemClock.elapsedRealtime() - this.Q) / 1000);
        }
    }

    public void p() {
        C0568o b2 = this.j.b();
        if (b2 == null) {
            Log.d("refreshOptimizingUi", "refreshOptimizingUi  optimizeItem == null");
            if (this.T > 0) {
                long elapsedRealtime = (SystemClock.elapsedRealtime() - this.T) / 1000;
                Log.d("refreshOptimizingUi", "OptimizeTime :" + elapsedRealtime);
                G.c(elapsedRealtime);
            }
            Activity activity = getActivity();
            if (a(activity)) {
                G.b((long) this.L.getTextScore());
                b.b.b.p.a(0);
                b.b.b.p.b(0);
                int f2 = 100 - this.l.f();
                this.L.setScore(f2);
                this.M.setScore(f2);
                com.miui.securityscan.i.w.a((Context) activity, f2, (TextView) this.M);
                com.miui.securityscan.i.w.a((Context) activity, f2, this.H);
                com.miui.securityscan.i.w.b((Context) activity, f2, this.J);
                b(f2);
                h.a((Context) activity, f2);
                c(f2);
                C0570q.b().a(C0570q.a.CLEANUP, "CLEAN_UNUSED_MEMORY", new C0569p(getString(R.string.memory_clear_unused, new Object[]{ExtraTextUtils.formatShortFileSize(activity, this.l.c())}), false));
                int d2 = ScoreManager.e().d();
                if (d2 > 0) {
                    C0570q.b().a(C0570q.a.SYSTEM, VirusScanModel.KEY_DEFAULT, new C0569p(getResources().getQuantityString(R.plurals.title_virus_clean, d2, new Object[]{Integer.valueOf(d2)}), true));
                }
                this.m.postDelayed(new v(this), 200);
                return;
            }
            return;
        }
        a(b2);
    }

    public int q() {
        int j2 = this.l.j();
        this.L.setScore(j2);
        this.M.setScore(j2);
        Activity activity = getActivity();
        if (a(activity)) {
            com.miui.securityscan.i.w.a((Context) activity, j2, (TextView) this.M);
            com.miui.securityscan.i.w.a((Context) activity, j2, this.H);
            com.miui.securityscan.i.w.b((Context) activity, j2, this.J);
        }
        b(j2);
        c(j2);
        return j2;
    }

    public void r() {
        w wVar = this.m;
        if (wVar != null) {
            wVar.sendEmptyMessage(102);
        }
    }

    public void s() {
        if (this.O != 2) {
            Activity activity = getActivity();
            if (a(activity)) {
                ((MainActivity) activity).a(false, false);
                this._a.e();
                if (!this.e) {
                    c(false);
                    Looper.myQueue().removeIdleHandler(this.db);
                }
                if (!this.f) {
                    this.f = true;
                    G();
                }
                E();
                a(false, false);
                this.va = false;
                this.ua = false;
                this.wa = false;
                y();
                PathInterpolator pathInterpolator = new PathInterpolator(0.4f, 0.48f, 0.25f, 1.0f);
                com.miui.securityscan.i.w.a((View) this.H, 400, (TimeInterpolator) pathInterpolator);
                if (!this.h) {
                    com.miui.securityscan.i.w.a(this.K, 400, (float) (-this.ga), (float) (-this.ha), pathInterpolator);
                }
                this.s.a();
                this.C.setVisibility(0);
                com.miui.securityscan.i.w.a(activity.getApplicationContext(), this.oa, this.s, true);
                this.O = 2;
                if (this.Q > 0) {
                    G.h((SystemClock.elapsedRealtime() - this.Q) / 1000);
                }
                this.I.setText(getString(R.string.security_scan_optimizing));
                this.J.setText(getString(R.string.security_scan_optimizing));
                this.Ja.clear();
                this.aa.f7610b = false;
                this.T = SystemClock.elapsedRealtime();
                this.j.a((O.c) this.Z, (n) this.X, this.aa, (O.e) this.V);
                this.g = C0557d.SCANNING;
                p();
                L();
                if (Build.IS_INTERNATIONAL_BUILD) {
                    M();
                }
                this.La = SystemClock.elapsedRealtime();
            }
        }
    }

    public void t() {
        this.m.post(new s(this));
    }

    public int u() {
        int textScore = this.L.getTextScore();
        A();
        q();
        Activity activity = getActivity();
        if (!a(activity)) {
            return 0;
        }
        this.I.setText(o.b(activity));
        this.J.setText(o.b(activity));
        this.H.setText(o.a(activity));
        return this.L.getTextScore() - textScore;
    }
}
