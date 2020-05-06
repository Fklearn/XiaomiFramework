package com.miui.powercenter.quickoptimize;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import b.b.c.i.b;
import b.b.c.j.e;
import com.miui.common.customview.ScoreTextView;
import com.miui.common.ui.MediaTextureView;
import com.miui.maml.folme.AnimatedProperty;
import com.miui.networkassistant.utils.TypefaceHelper;
import com.miui.powercenter.utils.u;
import com.miui.securitycenter.R;
import java.io.IOException;
import java.util.Locale;

public class MainContentFrame extends RelativeLayout {

    /* renamed from: a  reason: collision with root package name */
    private b f7203a;

    /* renamed from: b  reason: collision with root package name */
    private RelativeLayout f7204b;

    /* renamed from: c  reason: collision with root package name */
    private RelativeLayout f7205c;

    /* renamed from: d  reason: collision with root package name */
    private TextView f7206d;
    private RelativeLayout e;
    private RelativeLayout f;
    /* access modifiers changed from: private */
    public ScoreTextView g;
    /* access modifiers changed from: private */
    public TextView h;
    private TextView i;
    private TextView j;
    private TextView k;
    /* access modifiers changed from: private */
    public TextView l;
    private TextView m;
    /* access modifiers changed from: private */
    public MediaTextureView n;
    private MediaPlayer o;
    private Context p;
    private Activity q;
    private boolean r;
    private int s;
    private boolean t;

    public MainContentFrame(Context context) {
        this(context, (AttributeSet) null);
    }

    public MainContentFrame(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public MainContentFrame(Context context, AttributeSet attributeSet, int i2) {
        super(context, attributeSet, i2);
        this.p = context;
        this.o = new MediaPlayer();
        try {
            this.o.setDataSource(context, Uri.parse("android.resource://" + this.p.getPackageName() + "/" + R.raw.animatorview));
        } catch (Exception unused) {
        }
    }

    /* access modifiers changed from: private */
    public void b(boolean z, int i2) {
        this.j.setText(z ? R.string.hints_scan_result_phone_safe : R.string.power_center_hints_scan_danger_result);
        this.j.setTextColor(getResources().getColor(R.color.pc_main_scan_texttitle_color));
        ObjectAnimator a2 = a(this.f, 0.0f, 1.0f, 2, 0, 400);
        ObjectAnimator a3 = a(this.j, 0.0f, 1.0f, 2, 0, 400);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(new Animator[]{a2, a3});
        animatorSet.addListener(new C0524c(this));
        animatorSet.start();
    }

    public ObjectAnimator a(long j2) {
        return a(this.n, 1.0f, 0.0f, 2, 0, j2);
    }

    public ObjectAnimator a(View view, float f2, float f3, int i2, int i3, long j2) {
        ObjectAnimator ofFloat = ObjectAnimator.ofFloat(view, AnimatedProperty.PROPERTY_NAME_ALPHA, new float[]{f2, f3});
        ofFloat.setRepeatMode(i2);
        ofFloat.setRepeatCount(i3);
        ofFloat.setDuration(j2);
        return ofFloat;
    }

    public void a() {
        this.f.setAlpha(0.0f);
        this.g.setVisibility(0);
        this.h.setVisibility(0);
    }

    public void a(int i2, boolean z) {
        boolean z2 = i2 == 0;
        if (z) {
            ObjectAnimator a2 = a(this.g, 1.0f, 0.0f, 2, 0, 800);
            ObjectAnimator a3 = a(this.h, 1.0f, 0.0f, 2, 0, 800);
            ObjectAnimator a4 = a(this.l, 1.0f, 0.0f, 2, 0, 800);
            ObjectAnimator a5 = a(this.i, 1.0f, 0.0f, 2, 0, 800);
            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.playTogether(new Animator[]{a2, a3, a4, a5});
            animatorSet.addListener(new C0523b(this, z2, i2));
            animatorSet.start();
            return;
        }
        b(z2, i2);
    }

    public void a(boolean z, int i2) {
        this.r = z;
        if (this.r) {
            this.f7205c = (RelativeLayout) findViewById(R.id.abnormal_scan_display);
            this.f7206d = (TextView) findViewById(R.id.abnormal_scan_result_title);
            this.f7206d.setText(getResources().getQuantityString(R.plurals.pc_abnormal_scan_result_model_result_title, i2, new Object[]{Integer.valueOf(i2)}));
            this.f7205c.setVisibility(0);
            this.f7204b.setVisibility(8);
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:10:0x00a2  */
    /* JADX WARNING: Removed duplicated region for block: B:11:0x00a5  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void b() {
        /*
            r14 = this;
            android.widget.RelativeLayout r0 = r14.f7204b
            r1 = 8
            r0.setVisibility(r1)
            com.miui.common.ui.MediaTextureView r0 = r14.n
            r0.setVisibility(r1)
            android.widget.RelativeLayout r0 = r14.e
            r8 = 0
            r0.setVisibility(r8)
            com.miui.powercenter.quickoptimize.v r0 = com.miui.powercenter.quickoptimize.v.b()
            int r0 = r0.g()
            com.miui.powercenter.quickoptimize.v r1 = com.miui.powercenter.quickoptimize.v.b()
            int r1 = r1.c()
            com.miui.powercenter.batteryhistory.s r2 = com.miui.powercenter.batteryhistory.C0514s.c()
            java.util.List r2 = r2.b()
            android.content.Context r3 = r14.p
            long r2 = com.miui.powercenter.batteryhistory.C0520y.a(r3, r2)
            android.content.Context r4 = r14.p
            java.lang.String r4 = com.miui.powercenter.utils.s.c(r4, r2)
            boolean r5 = r14.r
            r9 = 1
            r6 = 2131757677(0x7f100a6d, float:1.9146297E38)
            if (r5 != 0) goto L_0x007d
            if (r0 <= 0) goto L_0x0072
            android.widget.TextView r1 = r14.k
            android.content.Context r4 = r14.p
            android.content.res.Resources r4 = r4.getResources()
            r5 = 2131624054(0x7f0e0076, float:1.8875277E38)
            java.lang.Object[] r6 = new java.lang.Object[r9]
            java.lang.Integer r7 = java.lang.Integer.valueOf(r0)
            r6[r8] = r7
            java.lang.String r0 = r4.getQuantityString(r5, r0, r6)
            r1.setText(r0)
            com.miui.powercenter.quickoptimize.v r0 = com.miui.powercenter.quickoptimize.v.b()
            long r0 = r0.e()
            android.content.Context r4 = r14.p
            java.lang.String r0 = com.miui.powercenter.utils.s.a(r4, r0, r2)
            android.widget.TextView r1 = r14.m
            android.text.Spanned r0 = android.text.Html.fromHtml(r0)
            r1.setText(r0)
            goto L_0x008b
        L_0x0072:
            if (r1 <= 0) goto L_0x007d
            android.widget.TextView r0 = r14.k
            r1 = 2131757674(0x7f100a6a, float:1.914629E38)
            r0.setText(r1)
            goto L_0x0082
        L_0x007d:
            android.widget.TextView r0 = r14.k
            r0.setText(r6)
        L_0x0082:
            android.widget.TextView r0 = r14.m
            android.text.Spanned r1 = android.text.Html.fromHtml(r4)
            r0.setText(r1)
        L_0x008b:
            android.animation.AnimatorSet r10 = new android.animation.AnimatorSet
            r10.<init>()
            android.widget.RelativeLayout r1 = r14.e
            r2 = 0
            r3 = 1065353216(0x3f800000, float:1.0)
            r4 = 2
            r5 = 0
            r6 = 1000(0x3e8, double:4.94E-321)
            r0 = r14
            android.animation.ObjectAnimator r11 = r0.a(r1, r2, r3, r4, r5, r6)
            boolean r0 = r14.r
            if (r0 == 0) goto L_0x00a5
            android.widget.RelativeLayout r0 = r14.f7205c
            goto L_0x00a7
        L_0x00a5:
            android.widget.RelativeLayout r0 = r14.f7204b
        L_0x00a7:
            r1 = r0
            r2 = 1065353216(0x3f800000, float:1.0)
            r3 = 0
            r4 = 2
            r5 = 0
            r6 = 500(0x1f4, double:2.47E-321)
            r0 = r14
            android.animation.ObjectAnimator r12 = r0.a(r1, r2, r3, r4, r5, r6)
            android.widget.TextView r1 = r14.k
            r2 = 0
            r3 = 1065353216(0x3f800000, float:1.0)
            android.animation.ObjectAnimator r13 = r0.a(r1, r2, r3, r4, r5, r6)
            android.widget.TextView r1 = r14.m
            android.animation.ObjectAnimator r0 = r0.a(r1, r2, r3, r4, r5, r6)
            r1 = 5
            android.animation.Animator[] r1 = new android.animation.Animator[r1]
            r1[r8] = r11
            r1[r9] = r12
            r2 = 2
            r1[r2] = r13
            r2 = 3
            r1[r2] = r0
            r0 = 4
            r2 = 500(0x1f4, double:2.47E-321)
            android.animation.ObjectAnimator r2 = r14.a((long) r2)
            r1[r0] = r2
            r10.playTogether(r1)
            r10.start()
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.powercenter.quickoptimize.MainContentFrame.b():void");
    }

    public void c() {
        if (!this.t) {
            ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{0.0f, 1.2f});
            ofFloat.setRepeatCount(0);
            ofFloat.setDuration(500);
            ofFloat.addUpdateListener(new C0525d(this));
            ofFloat.start();
            this.t = true;
        }
    }

    public int getNotchOffset() {
        return this.s;
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        MediaPlayer mediaPlayer = this.o;
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            this.o.release();
            this.o = null;
        }
    }

    /* access modifiers changed from: protected */
    public void onFinishInflate() {
        super.onFinishInflate();
        this.g = (ScoreTextView) findViewById(R.id.number);
        this.g.setText(u.a(0));
        this.g.setTypeface(TypefaceHelper.getMiuiThinTypeface(this.p), 1);
        Context context = this.p;
        if (context != null) {
            this.q = (Activity) context;
        }
        int a2 = e.a(this.q);
        int b2 = e.b();
        if (a2 <= 1920 || b2 <= 9) {
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) this.g.getLayoutParams();
            layoutParams.topMargin = getResources().getDimensionPixelSize(R.dimen.view_dimen_550);
            this.g.setLayoutParams(layoutParams);
        }
        this.n = (MediaTextureView) findViewById(R.id.animation_view);
        this.f = (RelativeLayout) findViewById(R.id.layout_risk_icon);
        this.f7204b = (RelativeLayout) findViewById(R.id.v_header_layout);
        this.e = (RelativeLayout) findViewById(R.id.pc_final_scan_result);
        this.h = (TextView) findViewById(R.id.scan_percent);
        this.i = (TextView) findViewById(R.id.result_text);
        this.j = (TextView) findViewById(R.id.scan_result_page_text);
        this.k = (TextView) findViewById(R.id.final_result_title);
        this.l = (TextView) findViewById(R.id.result_summary);
        this.m = (TextView) findViewById(R.id.final_result_summary);
        this.o.setLooping(true);
        this.n.setPlayer(this.o);
        if ("tr".equals(Locale.getDefault().getLanguage())) {
            RelativeLayout.LayoutParams layoutParams2 = (RelativeLayout.LayoutParams) this.h.getLayoutParams();
            layoutParams2.addRule(17);
            layoutParams2.addRule(16, R.id.number);
            this.h.setLayoutParams(layoutParams2);
        }
        a();
        try {
            this.o.prepare();
            this.o.start();
        } catch (IOException e2) {
            e2.printStackTrace();
        }
    }

    public void setEventHandler(b bVar) {
        this.f7203a = bVar;
    }

    public void setFinalResultIconAlpha(float f2) {
        this.e.setAlpha(f2);
    }

    public void setHeaderLayoutAlpha(float f2) {
        (!this.r ? this.f7204b : this.f7205c).setAlpha(f2);
    }

    public void setProgressText(CharSequence charSequence) {
        this.g.setText(charSequence);
    }

    public void setScanResult(CharSequence charSequence) {
        this.i.setText(charSequence);
    }

    public void setSummaryText(CharSequence charSequence) {
        this.l.setText(charSequence);
    }
}
