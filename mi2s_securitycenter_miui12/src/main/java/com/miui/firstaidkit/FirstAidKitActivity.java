package com.miui.firstaidkit;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.PathInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import b.b.g.a;
import com.miui.common.card.CardViewAdapter;
import com.miui.common.card.models.AdvCardModel;
import com.miui.common.card.models.AdvInternationalCardModel;
import com.miui.common.card.models.AdvListTitleCardModel;
import com.miui.common.card.models.BaseCardModel;
import com.miui.common.customview.ActionBarContainer;
import com.miui.common.customview.AutoPasteListView;
import com.miui.firstaidkit.j;
import com.miui.firstaidkit.ui.FirstAidVideoView;
import com.miui.firstaidkit.ui.ProgressLayout;
import com.miui.maml.folme.AnimatedProperty;
import com.miui.optimizemanage.d.c;
import com.miui.securitycenter.R;
import com.miui.securityscan.C0534a;
import com.miui.securityscan.a.G;
import com.miui.securityscan.cards.g;
import com.miui.securityscan.cards.k;
import com.miui.securityscan.i.w;
import com.miui.securityscan.scanner.C0558e;
import com.miui.superpower.b.k;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import miui.app.Activity;
import miui.app.AlertDialog;
import miui.os.Build;

public class FirstAidKitActivity extends C0534a implements a.b {
    /* access modifiers changed from: private */
    public int A;

    /* renamed from: b  reason: collision with root package name */
    public b f3866b = new b(this);
    /* access modifiers changed from: private */

    /* renamed from: c  reason: collision with root package name */
    public j f3867c;
    /* access modifiers changed from: private */

    /* renamed from: d  reason: collision with root package name */
    public ProgressLayout f3868d;
    private AutoPasteListView e;
    public CardViewAdapter f;
    /* access modifiers changed from: private */
    public RelativeLayout g;
    private RelativeLayout h;
    /* access modifiers changed from: private */
    public FirstAidVideoView i;
    /* access modifiers changed from: private */
    public int j;
    /* access modifiers changed from: private */
    public int k;
    /* access modifiers changed from: private */
    public ImageView l;
    /* access modifiers changed from: private */
    public ImageView m;
    private TextView n;
    private ActionBarContainer o;
    /* access modifiers changed from: private */
    public Map<n, String> p;
    private Object q = new Object();
    private boolean r;
    private boolean s;
    public ArrayList<BaseCardModel> t;
    private com.miui.firstaidkit.c.a u;
    private c v;
    private a w;
    private b.b.g.a x;
    private AnimatorSet y;
    /* access modifiers changed from: private */
    public int z;

    private static class a implements Runnable {

        /* renamed from: a  reason: collision with root package name */
        private final WeakReference<FirstAidKitActivity> f3869a;

        public a(FirstAidKitActivity firstAidKitActivity) {
            this.f3869a = new WeakReference<>(firstAidKitActivity);
        }

        public void run() {
            FirstAidKitActivity firstAidKitActivity = (FirstAidKitActivity) this.f3869a.get();
            if (firstAidKitActivity != null) {
                firstAidKitActivity.q();
                firstAidKitActivity.m();
            }
        }
    }

    private static class b implements j.b {

        /* renamed from: a  reason: collision with root package name */
        private final WeakReference<FirstAidKitActivity> f3870a;

        public b(FirstAidKitActivity firstAidKitActivity) {
            this.f3870a = new WeakReference<>(firstAidKitActivity);
        }

        public void a(n nVar) {
            FirstAidKitActivity firstAidKitActivity = (FirstAidKitActivity) this.f3870a.get();
            if (firstAidKitActivity != null && !firstAidKitActivity.isFinishing() && !firstAidKitActivity.isDestroyed()) {
                firstAidKitActivity.p.put(nVar, "finish");
                if (firstAidKitActivity.p.get(n.PERFORMANCE) != null && firstAidKitActivity.p.get(n.INTERNET) != null && firstAidKitActivity.p.get(n.OPERATION) != null && firstAidKitActivity.p.get(n.CONSUME_POWER) != null && firstAidKitActivity.p.get(n.OTHER) != null) {
                    int unused = firstAidKitActivity.k = firstAidKitActivity.f3867c.b();
                    firstAidKitActivity.runOnUiThread(new f(firstAidKitActivity, firstAidKitActivity.k));
                }
            }
        }
    }

    private static class c implements com.miui.firstaidkit.a.b {

        /* renamed from: a  reason: collision with root package name */
        private final WeakReference<FirstAidKitActivity> f3871a;

        /* renamed from: b  reason: collision with root package name */
        private n f3872b;

        public c(FirstAidKitActivity firstAidKitActivity, n nVar) {
            this.f3871a = new WeakReference<>(firstAidKitActivity);
            this.f3872b = nVar;
        }

        public void a(int i) {
            FirstAidKitActivity firstAidKitActivity = (FirstAidKitActivity) this.f3871a.get();
            if (firstAidKitActivity != null && !firstAidKitActivity.isFinishing() && !firstAidKitActivity.isDestroyed()) {
                Log.d("FirstAidKitActivity", "refreshOptimizingUi onFinishScan");
                int unused = firstAidKitActivity.k = firstAidKitActivity.k + i;
                firstAidKitActivity.f3866b.post(new e(firstAidKitActivity, this.f3872b, i));
            }
        }

        public void a(C0558e eVar) {
        }
    }

    private static class d implements AutoPasteListView.b {

        /* renamed from: a  reason: collision with root package name */
        private final WeakReference<FirstAidKitActivity> f3873a;

        public d(FirstAidKitActivity firstAidKitActivity) {
            this.f3873a = new WeakReference<>(firstAidKitActivity);
        }

        public void a(float f) {
            FirstAidKitActivity firstAidKitActivity = (FirstAidKitActivity) this.f3873a.get();
            if (firstAidKitActivity != null && firstAidKitActivity.j == 1) {
                firstAidKitActivity.g.setAlpha((f * -1.2f) + 1.0f);
            }
        }
    }

    private static class e implements Runnable {

        /* renamed from: a  reason: collision with root package name */
        private final WeakReference<FirstAidKitActivity> f3874a;

        /* renamed from: b  reason: collision with root package name */
        private n f3875b;

        /* renamed from: c  reason: collision with root package name */
        private int f3876c;

        public e(FirstAidKitActivity firstAidKitActivity, n nVar, int i) {
            this.f3874a = new WeakReference<>(firstAidKitActivity);
            this.f3875b = nVar;
            this.f3876c = i;
        }

        public void run() {
            FirstAidKitActivity firstAidKitActivity = (FirstAidKitActivity) this.f3874a.get();
            if (firstAidKitActivity != null && !firstAidKitActivity.isFinishing() && !firstAidKitActivity.isDestroyed()) {
                firstAidKitActivity.f3868d.a(this.f3875b, this.f3876c > 0);
                Log.d("FirstAidKitActivity", "refreshOptimizingUi refreshOptimizingUi");
                firstAidKitActivity.n();
            }
        }
    }

    private static class f implements Runnable {

        /* renamed from: a  reason: collision with root package name */
        private final WeakReference<FirstAidKitActivity> f3877a;

        /* renamed from: b  reason: collision with root package name */
        private int f3878b;

        public f(FirstAidKitActivity firstAidKitActivity, int i) {
            this.f3877a = new WeakReference<>(firstAidKitActivity);
            this.f3878b = i;
        }

        public void run() {
            FirstAidKitActivity firstAidKitActivity = (FirstAidKitActivity) this.f3877a.get();
            if (firstAidKitActivity != null && !firstAidKitActivity.isFinishing() && !firstAidKitActivity.isDestroyed()) {
                firstAidKitActivity.b(this.f3878b);
                firstAidKitActivity.r();
            }
        }
    }

    private static class g implements c.a {

        /* renamed from: a  reason: collision with root package name */
        private WeakReference<FirstAidKitActivity> f3879a;

        /* renamed from: b  reason: collision with root package name */
        private float f3880b;

        public g(FirstAidKitActivity firstAidKitActivity, float f) {
            this.f3879a = new WeakReference<>(firstAidKitActivity);
            this.f3880b = f;
        }

        public void a(float f) {
            FirstAidKitActivity firstAidKitActivity = (FirstAidKitActivity) this.f3879a.get();
            if (firstAidKitActivity != null && !firstAidKitActivity.isFinishing() && !firstAidKitActivity.isDestroyed()) {
                firstAidKitActivity.i.setScaleX(f);
                firstAidKitActivity.i.setScaleY(f);
                float f2 = f <= 0.58f ? 1.0f : f * this.f3880b;
                firstAidKitActivity.l.setScaleX(f2);
                firstAidKitActivity.l.setScaleY(f2);
                firstAidKitActivity.m.setScaleX(f2);
                firstAidKitActivity.m.setScaleY(f2);
            }
        }
    }

    private static class h implements DialogInterface.OnClickListener {

        /* renamed from: a  reason: collision with root package name */
        private WeakReference<FirstAidKitActivity> f3881a;

        public h(FirstAidKitActivity firstAidKitActivity) {
            this.f3881a = new WeakReference<>(firstAidKitActivity);
        }

        public void onClick(DialogInterface dialogInterface, int i) {
            FirstAidKitActivity firstAidKitActivity = (FirstAidKitActivity) this.f3881a.get();
            if (firstAidKitActivity != null) {
                firstAidKitActivity.l();
                firstAidKitActivity.finish();
            }
        }
    }

    private static class i implements DialogInterface.OnClickListener {

        /* renamed from: a  reason: collision with root package name */
        private WeakReference<FirstAidKitActivity> f3882a;

        public i(FirstAidKitActivity firstAidKitActivity) {
            this.f3882a = new WeakReference<>(firstAidKitActivity);
        }

        public void onClick(DialogInterface dialogInterface, int i) {
            FirstAidKitActivity firstAidKitActivity = (FirstAidKitActivity) this.f3882a.get();
            if (firstAidKitActivity != null) {
                firstAidKitActivity.x();
            }
        }
    }

    private static class j implements c.b {

        /* renamed from: a  reason: collision with root package name */
        private WeakReference<FirstAidKitActivity> f3883a;

        public j(FirstAidKitActivity firstAidKitActivity) {
            this.f3883a = new WeakReference<>(firstAidKitActivity);
        }

        public void a(float f) {
            FirstAidKitActivity firstAidKitActivity = (FirstAidKitActivity) this.f3883a.get();
            if (firstAidKitActivity != null && !firstAidKitActivity.isFinishing() && !firstAidKitActivity.isDestroyed()) {
                int c2 = firstAidKitActivity.A;
                float f2 = (float) (c2 + ((int) (((float) (0 - c2)) * f)));
                firstAidKitActivity.l.setTranslationY(f2);
                firstAidKitActivity.m.setTranslationY(f2);
                firstAidKitActivity.i.setTranslationY((float) ((int) (((float) (-firstAidKitActivity.A)) * f)));
                firstAidKitActivity.a((int) (f * ((float) firstAidKitActivity.z)));
            }
        }
    }

    private void a(int i2, int i3, int i4) {
        this.A = i2;
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) this.i.getLayoutParams();
        layoutParams.topMargin = i3;
        this.i.setLayoutParams(layoutParams);
        RelativeLayout.LayoutParams layoutParams2 = (RelativeLayout.LayoutParams) this.f3868d.getLayoutParams();
        layoutParams2.topMargin = i4;
        this.f3868d.setLayoutParams(layoutParams2);
    }

    /* access modifiers changed from: private */
    public void b(int i2) {
        ImageView imageView;
        int i3;
        TextView textView;
        Resources resources;
        int i4;
        if (i2 > 0) {
            this.m.setImageResource(R.drawable.firstaid_result_icon_have_risk);
            this.n.setText(getResources().getQuantityString(R.plurals.first_aid_result_summary_exception, i2, new Object[]{Integer.valueOf(i2)}));
            textView = this.n;
            resources = getResources();
            i4 = R.color.first_aid_box_summary_textcolor2;
        } else {
            if (this.s) {
                this.n.setText(R.string.first_aid_result_summary_not_finished);
                imageView = this.m;
                i3 = R.drawable.firstaid_result_icon_not_complete;
            } else {
                this.n.setText(R.string.first_aid_result_summary_normal);
                imageView = this.m;
                i3 = R.drawable.firstaid_result_icon_compelete;
            }
            imageView.setImageResource(i3);
            textView = this.n;
            resources = getResources();
            i4 = R.color.first_aid_box_summary_textcolor;
        }
        textView.setTextColor(resources.getColor(i4));
    }

    private void initView() {
        AutoPasteListView autoPasteListView;
        this.f3868d = (ProgressLayout) findViewById(R.id.progressLayout);
        this.f3868d.a();
        this.f3868d.a(this.f3866b);
        this.g = (RelativeLayout) findViewById(R.id.ll_top_result);
        this.h = (RelativeLayout) findViewById(R.id.result_img_content);
        this.l = (ImageView) findViewById(R.id.iv_circle);
        this.m = (ImageView) findViewById(R.id.result_score_icon);
        this.n = (TextView) findViewById(R.id.tv_summary_result);
        this.e = (AutoPasteListView) findViewById(R.id.auto_paste_listview);
        int i2 = 0;
        this.e.setAlignItem(0);
        if (Build.IS_INTERNATIONAL_BUILD) {
            autoPasteListView = this.e;
            i2 = 2;
        } else {
            autoPasteListView = this.e;
        }
        autoPasteListView.setOverScrollMode(i2);
        this.e.setTopDraggable(true);
        this.e.setOnScrollPercentChangeListener(new d(this));
        this.e.setAdapter(this.f);
        this.i = (FirstAidVideoView) findViewById(R.id.ll_top_main);
    }

    /* access modifiers changed from: private */
    public void q() {
        this.i.b();
        PathInterpolator pathInterpolator = new PathInterpolator(0.6f, 0.35f, 0.19f, 1.0f);
        ObjectAnimator ofFloat = ObjectAnimator.ofFloat(this.i, AnimatedProperty.PROPERTY_NAME_ALPHA, new float[]{1.0f, 0.0f});
        ofFloat.setDuration(400);
        ofFloat.setInterpolator(pathInterpolator);
        this.g.setVisibility(0);
        this.n.setAlpha(0.0f);
        this.h.setAlpha(0.0f);
        this.l.setScaleX(1.71f);
        this.l.setScaleY(1.71f);
        this.l.setTranslationY((float) this.A);
        this.m.setScaleX(1.71f);
        this.m.setScaleY(1.71f);
        this.m.setTranslationY((float) this.A);
        com.miui.optimizemanage.d.c.e();
        com.miui.optimizemanage.d.c.a((c.b) new j(this));
        com.miui.optimizemanage.d.c.a(1.0f, 0.58f);
        com.miui.optimizemanage.d.c.a((c.a) new g(this, 1.71f));
        ObjectAnimator ofFloat2 = ObjectAnimator.ofFloat(this.h, AnimatedProperty.PROPERTY_NAME_ALPHA, new float[]{0.0f, 1.0f});
        ofFloat2.setDuration(400);
        ofFloat2.setInterpolator(pathInterpolator);
        ObjectAnimator ofFloat3 = ObjectAnimator.ofFloat(this.n, AnimatedProperty.PROPERTY_NAME_ALPHA, new float[]{0.0f, 1.0f});
        ofFloat3.setDuration(300);
        ofFloat3.setStartDelay(300);
        ofFloat3.setInterpolator(pathInterpolator);
        this.y = new AnimatorSet();
        this.y.playTogether(new Animator[]{ofFloat, ofFloat2, ofFloat3});
        this.y.start();
    }

    /* JADX WARNING: type inference failed for: r11v0, types: [com.miui.firstaidkit.FirstAidKitActivity, android.content.Context] */
    /* access modifiers changed from: private */
    /* JADX WARNING: Removed duplicated region for block: B:68:0x01c1  */
    /* JADX WARNING: Removed duplicated region for block: B:97:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void r() {
        /*
            r11 = this;
            com.miui.securityscan.cards.c.e()
            com.miui.securityscan.cards.c.c()
            java.util.ArrayList<com.miui.common.card.models.BaseCardModel> r0 = r11.t
            if (r0 == 0) goto L_0x01b2
            boolean r0 = r0.isEmpty()
            if (r0 != 0) goto L_0x01b2
            java.util.ArrayList r0 = new java.util.ArrayList
            java.util.ArrayList<com.miui.common.card.models.BaseCardModel> r1 = r11.t
            r0.<init>(r1)
            java.util.HashMap r1 = new java.util.HashMap
            r1.<init>()
            java.util.ArrayList r2 = new java.util.ArrayList
            r2.<init>()
            java.util.Iterator r3 = r0.iterator()
        L_0x0025:
            boolean r4 = r3.hasNext()
            r5 = -1
            if (r4 == 0) goto L_0x005d
            java.lang.Object r4 = r3.next()
            com.miui.common.card.models.BaseCardModel r4 = (com.miui.common.card.models.BaseCardModel) r4
            boolean r6 = r4 instanceof com.miui.common.card.models.AdvListTitleCardModel
            if (r6 == 0) goto L_0x0025
            r6 = r4
            com.miui.common.card.models.AdvListTitleCardModel r6 = (com.miui.common.card.models.AdvListTitleCardModel) r6
            java.util.List r7 = r6.getSubCardModelList()
            int r8 = r6.getPosition()
            if (r8 == r5) goto L_0x0025
            if (r7 == 0) goto L_0x0025
            boolean r5 = r7.isEmpty()
            if (r5 != 0) goto L_0x0025
            r2.add(r4)
            r2.addAll(r7)
            int r5 = r6.getPosition()
            java.lang.Integer r5 = java.lang.Integer.valueOf(r5)
            r1.put(r5, r4)
            goto L_0x0025
        L_0x005d:
            r0.removeAll(r2)
            java.util.ArrayList r2 = new java.util.ArrayList
            r2.<init>()
            java.util.Iterator r3 = r0.iterator()
        L_0x0069:
            boolean r4 = r3.hasNext()
            if (r4 == 0) goto L_0x0091
            java.lang.Object r4 = r3.next()
            com.miui.common.card.models.BaseCardModel r4 = (com.miui.common.card.models.BaseCardModel) r4
            boolean r6 = r4 instanceof com.miui.common.card.models.AdvCardModel
            if (r6 == 0) goto L_0x0069
            r6 = r4
            com.miui.common.card.models.AdvCardModel r6 = (com.miui.common.card.models.AdvCardModel) r6
            int r7 = r6.getPosition()
            if (r7 == r5) goto L_0x0069
            r2.add(r4)
            int r6 = r6.getPosition()
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            r1.put(r6, r4)
            goto L_0x0069
        L_0x0091:
            r0.removeAll(r2)
            java.util.ArrayList r2 = new java.util.ArrayList
            r2.<init>()
            java.util.Iterator r3 = r0.iterator()
        L_0x009d:
            boolean r4 = r3.hasNext()
            if (r4 == 0) goto L_0x00b1
            java.lang.Object r4 = r3.next()
            com.miui.common.card.models.BaseCardModel r4 = (com.miui.common.card.models.BaseCardModel) r4
            boolean r6 = r4 instanceof com.miui.common.card.models.LineCardModel
            if (r6 == 0) goto L_0x009d
            r2.add(r4)
            goto L_0x009d
        L_0x00b1:
            r0.removeAll(r2)
            r2 = 0
            r3 = r2
        L_0x00b6:
            int r4 = r0.size()
            if (r3 >= r4) goto L_0x00ca
            java.lang.Object r4 = r0.get(r3)
            com.miui.common.card.models.BaseCardModel r4 = (com.miui.common.card.models.BaseCardModel) r4
            boolean r4 = r4 instanceof com.miui.common.card.models.PlaceHolderCardModel
            if (r4 == 0) goto L_0x00c7
            goto L_0x00cb
        L_0x00c7:
            int r3 = r3 + 1
            goto L_0x00b6
        L_0x00ca:
            r3 = r5
        L_0x00cb:
            if (r3 == r5) goto L_0x01b2
            boolean r4 = r1.isEmpty()
            if (r4 != 0) goto L_0x01a4
            java.util.ArrayList r4 = com.miui.securityscan.cards.c.b((android.content.Context) r11)
            int r5 = r1.size()
            int r6 = r4.size()
            int r5 = r5 + r6
            java.lang.StringBuilder r6 = new java.lang.StringBuilder
            r6.<init>()
            java.lang.String r7 = "advMap size is "
            r6.append(r7)
            int r7 = r1.size()
            r6.append(r7)
            java.lang.String r7 = ",  models.size() is "
            r6.append(r7)
            int r7 = r4.size()
            r6.append(r7)
            java.lang.String r7 = ",  max size is  "
            r6.append(r7)
            r6.append(r5)
            java.lang.String r6 = r6.toString()
            java.lang.String r7 = "FirstAidKitActivity"
            android.util.Log.d(r7, r6)
            java.util.ArrayList r6 = new java.util.ArrayList
            r6.<init>()
            r8 = r2
        L_0x0114:
            if (r8 >= r5) goto L_0x0155
            java.lang.Integer r9 = java.lang.Integer.valueOf(r8)
            java.lang.Object r9 = r1.get(r9)
            com.miui.common.card.models.BaseCardModel r9 = (com.miui.common.card.models.BaseCardModel) r9
            if (r9 == 0) goto L_0x0140
            r6.add(r9)
            boolean r10 = r9 instanceof com.miui.common.card.models.AdvListTitleCardModel
            if (r10 == 0) goto L_0x0138
            com.miui.common.card.models.AdvListTitleCardModel r9 = (com.miui.common.card.models.AdvListTitleCardModel) r9
            java.util.List r9 = r9.getSubCardModelList()
            boolean r10 = r9.isEmpty()
            if (r10 != 0) goto L_0x0138
            r6.addAll(r9)
        L_0x0138:
            java.lang.Integer r9 = java.lang.Integer.valueOf(r8)
            r1.remove(r9)
            goto L_0x0152
        L_0x0140:
            int r9 = r4.size()
            if (r9 <= 0) goto L_0x0152
            java.lang.Object r9 = r4.get(r2)
            com.miui.common.card.models.BaseCardModel r9 = (com.miui.common.card.models.BaseCardModel) r9
            r6.add(r9)
            r4.remove(r9)
        L_0x0152:
            int r8 = r8 + 1
            goto L_0x0114
        L_0x0155:
            boolean r2 = r1.isEmpty()
            if (r2 != 0) goto L_0x01a8
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r4 = "advMap is not empty when for() finished, the map size is  "
            r2.append(r4)
            int r4 = r1.size()
            r2.append(r4)
            java.lang.String r2 = r2.toString()
            android.util.Log.d(r7, r2)
            java.util.Set r1 = r1.entrySet()
            java.util.Iterator r1 = r1.iterator()
        L_0x017b:
            boolean r2 = r1.hasNext()
            if (r2 == 0) goto L_0x01a8
            java.lang.Object r2 = r1.next()
            java.util.Map$Entry r2 = (java.util.Map.Entry) r2
            java.lang.Object r2 = r2.getValue()
            com.miui.common.card.models.BaseCardModel r2 = (com.miui.common.card.models.BaseCardModel) r2
            r6.add(r2)
            boolean r4 = r2 instanceof com.miui.common.card.models.AdvListTitleCardModel
            if (r4 == 0) goto L_0x017b
            com.miui.common.card.models.AdvListTitleCardModel r2 = (com.miui.common.card.models.AdvListTitleCardModel) r2
            java.util.List r2 = r2.getSubCardModelList()
            boolean r4 = r2.isEmpty()
            if (r4 != 0) goto L_0x017b
            r6.addAll(r2)
            goto L_0x017b
        L_0x01a4:
            java.util.ArrayList r6 = com.miui.securityscan.cards.c.b((android.content.Context) r11)
        L_0x01a8:
            r0.remove(r3)
            r0.addAll(r3, r6)
            com.miui.securityscan.cards.c.b((java.util.ArrayList<com.miui.common.card.models.BaseCardModel>) r0)
            goto L_0x01b5
        L_0x01b2:
            com.miui.securityscan.cards.c.a((android.content.Context) r11)
        L_0x01b5:
            java.util.ArrayList r0 = com.miui.securityscan.cards.c.g()
            r1 = 3
            com.miui.securityscan.cards.c.a((java.util.ArrayList<com.miui.common.card.models.BaseCardModel>) r0, (int) r1)
            com.miui.common.card.CardViewAdapter r1 = r11.f
            if (r1 == 0) goto L_0x01c9
            r1.setModelList(r0)
            com.miui.common.card.CardViewAdapter r0 = r11.f
            r0.notifyDataSetChanged()
        L_0x01c9:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.firstaidkit.FirstAidKitActivity.r():void");
    }

    /* JADX WARNING: type inference failed for: r4v0, types: [com.miui.firstaidkit.FirstAidKitActivity, miui.app.Activity, android.app.Activity] */
    private void s() {
        int dimensionPixelSize;
        int dimensionPixelSize2;
        int i2;
        this.o = (ActionBarContainer) findViewById(R.id.abc_action_bar);
        this.o.setTitle(getString(R.string.first_aid_activity_title));
        int b2 = b.b.c.j.e.b();
        Resources resources = getResources();
        if (b.b.c.j.e.b(this)) {
            this.o.setIsShowSecondTitle(false);
            dimensionPixelSize = resources.getDimensionPixelSize(R.dimen.firstaidkit_anim_transition_y_el_1920);
            dimensionPixelSize2 = resources.getDimensionPixelSize(R.dimen.firstaidkit_texture_view_margin_top_el_1920);
            i2 = R.dimen.firstaidkit_optimize_layout_margin_top_el_1920;
        } else if (b2 <= 9) {
            this.o.setIsShowSecondTitle(false);
            dimensionPixelSize = resources.getDimensionPixelSize(R.dimen.firstaidkit_anim_transition_y_v11);
            dimensionPixelSize2 = resources.getDimensionPixelSize(R.dimen.firstaidkit_texture_view_margin_top_v11);
            i2 = R.dimen.firstaidkit_optimize_layout_margin_top_v11;
        } else {
            dimensionPixelSize = resources.getDimensionPixelSize(R.dimen.firstaidkit_anim_transition_y);
            dimensionPixelSize2 = resources.getDimensionPixelSize(R.dimen.firstaidkit_texture_view_margin_top);
            i2 = R.dimen.firstaidkit_optimize_layout_margin_top;
        }
        a(dimensionPixelSize, dimensionPixelSize2, resources.getDimensionPixelSize(i2));
        this.o.setActionBarEventListener(new d(this));
    }

    private void t() {
        this.z = getResources().getDimensionPixelSize(R.dimen.activity_actionbar_transition_y);
    }

    private void u() {
        this.u = new com.miui.firstaidkit.c.a(this);
        this.u.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);
    }

    /* JADX WARNING: type inference failed for: r3v0, types: [com.miui.firstaidkit.FirstAidKitActivity, android.content.Context] */
    private void v() {
        new AlertDialog.Builder(this).setTitle(R.string.first_aid_dialog_title_stop_scan).setMessage(R.string.first_aid_dialog_msg_stop_scan).setPositiveButton(R.string.ok, new h(this)).setNegativeButton(R.string.cancel, (DialogInterface.OnClickListener) null).show();
    }

    private void w() {
        this.r = false;
        this.k = 0;
        this.f3867c.a((Handler) this.f3866b);
        n();
    }

    /* access modifiers changed from: private */
    public void x() {
        synchronized (this.q) {
            if (!this.r) {
                Log.d("FirstAidKitActivity", "stopScan");
                l();
                this.k = this.f3867c.b();
                this.s = true;
                y();
                this.r = true;
            }
        }
    }

    private void y() {
        b(this.k);
        this.f3866b.postDelayed(new a(this), 600);
    }

    public void a(int i2) {
        ActionBarContainer actionBarContainer = this.o;
        if (actionBarContainer != null) {
            actionBarContainer.a(i2);
        }
    }

    public void a(BaseCardModel baseCardModel, int i2) {
        BaseCardModel next;
        Log.d("FirstAidKitActivity", "removeMainPageSingleModel position:" + i2);
        if (baseCardModel != null && i2 == 3) {
            CardViewAdapter cardViewAdapter = this.f;
            if (cardViewAdapter != null) {
                com.miui.securityscan.cards.c.a((List<BaseCardModel>) cardViewAdapter.getModelList(), baseCardModel);
                this.f.notifyDataSetChanged();
            }
            ArrayList<BaseCardModel> arrayList = this.t;
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
                com.miui.securityscan.cards.c.a((List<BaseCardModel>) this.t, baseCardModel2);
            }
        }
    }

    public void a(BaseCardModel baseCardModel, List<BaseCardModel> list, int i2) {
        Log.d("FirstAidKitActivity", "removeMainPageGroupModel position:" + i2);
        if (baseCardModel != null && i2 == 3) {
            CardViewAdapter cardViewAdapter = this.f;
            if (cardViewAdapter != null) {
                com.miui.securityscan.cards.c.a((List<BaseCardModel>) cardViewAdapter.getModelList(), baseCardModel);
                this.f.getModelList().removeAll(list);
                this.f.notifyDataSetChanged();
            }
            ArrayList<BaseCardModel> arrayList = this.t;
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
                com.miui.securityscan.cards.c.a((List<BaseCardModel>) this.t, baseCardModel2);
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
                    Iterator<BaseCardModel> it2 = this.t.iterator();
                    while (it2.hasNext()) {
                        BaseCardModel next3 = it2.next();
                        if (next3 instanceof AdvCardModel) {
                            AdvCardModel advCardModel2 = (AdvCardModel) next3;
                            if ((!advCardModel2.isLocal() && arrayList2.contains(Integer.valueOf(advCardModel2.getId()))) || (advCardModel2.isLocal() && arrayList3.contains(advCardModel2.getDataId()))) {
                                arrayList4.add(next3);
                            }
                        }
                    }
                    this.t.removeAll(arrayList4);
                }
            }
        }
    }

    public void l() {
        j jVar = this.f3867c;
        if (jVar != null) {
            jVar.a();
        }
    }

    public void m() {
        this.j = 1;
        r();
        w.a(getApplicationContext(), (View) this.f3868d, (View) this.e);
        G.b();
    }

    public void n() {
        n c2 = this.f3867c.c();
        if (c2 == null) {
            synchronized (this.q) {
                if (!this.r) {
                    Log.d("FirstAidKitActivity", "refreshOptimizingUi turnToResult");
                    this.s = false;
                    y();
                    this.r = true;
                }
            }
            return;
        }
        Log.d("FirstAidKitActivity", "refreshOptimizingUi popOptimizeEntry");
        this.f3867c.a(c2, (com.miui.firstaidkit.a.b) new c(this, c2));
    }

    public void o() {
        this.p.clear();
        this.f3867c.a((j.b) new b(this), (Handler) this.f3866b);
    }

    /* access modifiers changed from: protected */
    public void onActivityResult(int i2, int i3, Intent intent) {
        FirstAidKitActivity.super.onActivityResult(i2, i3, intent);
        if (i2 == 100 && this.j == 1) {
            o();
        }
    }

    public void onBackPressed() {
        int i2 = this.j;
        if (i2 == 0) {
            v();
        } else if (i2 == 1) {
            finish();
        }
    }

    /* JADX WARNING: type inference failed for: r2v0, types: [b.b.c.c.a, com.miui.firstaidkit.FirstAidKitActivity, android.content.Context, miui.app.Activity, java.lang.Object] */
    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        String str;
        super.onCreate(bundle);
        setContentView(R.layout.m_activity_firstaidkit);
        k.a((Activity) this);
        String stringExtra = getIntent().getStringExtra("enter_homepage_way");
        if ("00001".equals(stringExtra)) {
            str = "firstaidkit_from_security_home";
        } else if ("00006".equals(stringExtra)) {
            str = "firstaidkit_from_security_result";
        } else {
            if (!TextUtils.isEmpty(stringExtra)) {
                str = "firstaidkit_channel_" + stringExtra;
            }
            this.x = b.b.g.a.a();
            this.x.a(this);
            this.v = new c(this);
            this.w = new a(this);
            this.j = 0;
            this.p = new HashMap();
            this.f3867c = j.a((Context) this);
            this.f = new CardViewAdapter(this, this.f3866b, 2);
            initView();
            s();
            t();
            w();
            u();
            com.miui.securityscan.cards.k.a((Context) this).a((k.a) this.w);
            com.miui.securityscan.cards.g.a((Context) this).b((g.a) this.v);
        }
        G.m(str);
        this.x = b.b.g.a.a();
        this.x.a(this);
        this.v = new c(this);
        this.w = new a(this);
        this.j = 0;
        this.p = new HashMap();
        this.f3867c = j.a((Context) this);
        this.f = new CardViewAdapter(this, this.f3866b, 2);
        initView();
        s();
        t();
        w();
        u();
        com.miui.securityscan.cards.k.a((Context) this).a((k.a) this.w);
        com.miui.securityscan.cards.g.a((Context) this).b((g.a) this.v);
    }

    /* JADX WARNING: type inference failed for: r3v0, types: [com.miui.firstaidkit.FirstAidKitActivity, android.content.Context, java.lang.Object, com.miui.securityscan.a] */
    /* access modifiers changed from: protected */
    public void onDestroy() {
        ArrayList<BaseCardModel> modelList;
        this.f3866b.removeCallbacksAndMessages((Object) null);
        this.x.c(this);
        CardViewAdapter cardViewAdapter = this.f;
        if (cardViewAdapter != null) {
            if (Build.IS_INTERNATIONAL_BUILD && (modelList = cardViewAdapter.getModelList()) != null) {
                for (BaseCardModel next : modelList) {
                    if (next instanceof AdvInternationalCardModel) {
                        AdvCardModel advCardModel = (AdvCardModel) next;
                        com.miui.securityscan.cards.h.a(advCardModel.getObject());
                        this.x.b(advCardModel.getObject());
                    }
                }
            }
            this.f.onDestroy();
        }
        super.onDestroy();
        com.miui.firstaidkit.c.a aVar = this.u;
        if (aVar != null) {
            aVar.cancel(true);
        }
        com.miui.securityscan.cards.k.a((Context) this).b((k.a) this.w);
        com.miui.securityscan.cards.g.a((Context) this).d(this.v);
        com.miui.securityscan.cards.c.i();
        com.miui.optimizemanage.d.c.d();
        com.miui.optimizemanage.d.c.c();
        AnimatorSet animatorSet = this.y;
        if (animatorSet != null && animatorSet.isRunning()) {
            this.y.cancel();
        }
    }

    /* access modifiers changed from: protected */
    public void onResume() {
        super.onResume();
        G.a();
        if (this.j == 1) {
            G.b();
        }
    }

    /* JADX WARNING: type inference failed for: r3v0, types: [com.miui.firstaidkit.FirstAidKitActivity, android.content.Context] */
    public void p() {
        new AlertDialog.Builder(this).setTitle(R.string.first_aid_dialog_title_stop_scan).setMessage(R.string.first_aid_dialog_msg_stop_scan).setPositiveButton(R.string.ok, new i(this)).setNegativeButton(R.string.cancel, (DialogInterface.OnClickListener) null).show();
    }
}
