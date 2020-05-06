package com.miui.antivirus.ui;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.PathInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import b.b.b.d.m;
import b.b.b.d.n;
import b.b.c.i.b;
import com.miui.antivirus.result.C0238a;
import com.miui.antivirus.result.K;
import com.miui.antivirus.result.ScanResultFrame;
import com.miui.antivirus.result.t;
import com.miui.antivirus.ui.MainHandleBar;
import com.miui.maml.folme.AnimatedProperty;
import com.miui.securitycenter.R;
import com.miui.securityscan.i.w;
import java.util.ArrayList;

public class MainActivityView extends RelativeLayout {

    /* renamed from: a  reason: collision with root package name */
    private Context f2922a;

    /* renamed from: b  reason: collision with root package name */
    private CustomActionBar f2923b;
    /* access modifiers changed from: private */

    /* renamed from: c  reason: collision with root package name */
    public MainContentFrame f2924c;
    /* access modifiers changed from: private */

    /* renamed from: d  reason: collision with root package name */
    public MainHandleBar f2925d;
    private ScanResultFrame e;
    /* access modifiers changed from: private */
    public b f;
    private RelativeLayout g;
    private ImageView h;
    private LinearLayout i;
    private K j;
    private t k;
    private ArrayList<C0238a> l;

    public MainActivityView(Context context) {
        this(context, (AttributeSet) null);
        this.f2922a = context;
    }

    public MainActivityView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.l = new ArrayList<>();
        this.f2922a = context;
    }

    private void a(int i2) {
        ((RelativeLayout.LayoutParams) this.g.getLayoutParams()).topMargin = i2;
    }

    public void a() {
        ObjectAnimator ofFloat = ObjectAnimator.ofFloat(this.f2925d, AnimatedProperty.PROPERTY_NAME_ALPHA, new float[]{1.0f, 0.3f, 0.0f});
        ObjectAnimator ofFloat2 = ObjectAnimator.ofFloat(this.f2925d, AnimatedProperty.PROPERTY_NAME_SCALE_X, new float[]{1.0f, 0.8f});
        ObjectAnimator ofFloat3 = ObjectAnimator.ofFloat(this.f2925d, AnimatedProperty.PROPERTY_NAME_SCALE_Y, new float[]{1.0f, 0.8f});
        ofFloat.setInterpolator(new AccelerateInterpolator(1.2f));
        ofFloat2.setInterpolator(new AccelerateInterpolator(1.2f));
        ofFloat3.setInterpolator(new AccelerateInterpolator(1.2f));
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(new Animator[]{ofFloat2, ofFloat3, ofFloat});
        animatorSet.setDuration(300);
        animatorSet.addListener(new g(this));
        animatorSet.start();
        this.f2923b.a(true);
    }

    public void a(int i2, boolean z, boolean z2) {
        this.f2924c.a(i2, Boolean.valueOf(z), z2);
    }

    public void a(m mVar) {
        this.f2924c.a(mVar);
    }

    public void a(t tVar, ArrayList<C0238a> arrayList) {
        this.k = tVar;
        this.l = arrayList;
    }

    public void a(MainHandleBar.b bVar, MainHandleBar.a aVar) {
        this.f2925d.a(bVar, aVar);
    }

    public void b() {
        if (this.e == null) {
            ((ViewStub) findViewById(R.id.v_scan_result_stub)).inflate();
            this.e = (ScanResultFrame) findViewById(R.id.virus_result_frame);
            int top = this.f2923b.getTop() + getResources().getDimensionPixelSize(R.dimen.activity_actionbar_height);
            ((ViewGroup.MarginLayoutParams) this.e.getLayoutParams()).topMargin = top;
            K.b bVar = new K.b(this.f2922a);
            bVar.a(this.e);
            bVar.a(this.k);
            bVar.a(this.l);
            this.j = bVar.a();
            this.j.a(this.f);
            int screenHeight = getScreenHeight();
            ScanResultFrame scanResultFrame = this.e;
            float f2 = (float) screenHeight;
            ObjectAnimator ofFloat = ObjectAnimator.ofFloat(scanResultFrame, "translationY", new float[]{scanResultFrame.getTranslationY() + f2, this.e.getTranslationY()});
            ofFloat.setInterpolator(new DecelerateInterpolator());
            ScanResultFrame scanResultFrame2 = this.e;
            scanResultFrame2.setTranslationY(scanResultFrame2.getTranslationY() + f2);
            int bottom = ((this.g.getBottom() - (this.g.getHeight() / 2)) + ((this.i.getBottom() - this.h.getBottom()) / 2)) - (top + (getResources().getDimensionPixelSize(R.dimen.antivirus_result_list_first_item_height) / 2));
            RelativeLayout relativeLayout = this.g;
            ObjectAnimator ofFloat2 = ObjectAnimator.ofFloat(relativeLayout, "translationY", new float[]{relativeLayout.getTranslationY(), this.g.getTranslationY() - ((float) bottom)});
            ofFloat2.setInterpolator(new PathInterpolator(0.6f, 0.35f, 0.19f, 1.0f));
            ValueAnimator ofFloat3 = ValueAnimator.ofFloat(new float[]{this.f2924c.getVideoScale(), 0.546f});
            ofFloat3.addUpdateListener(new h(this));
            ofFloat3.setDuration(200);
            ofFloat3.start();
            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.playTogether(new Animator[]{ofFloat, ofFloat2});
            animatorSet.setDuration(400);
            animatorSet.setStartDelay(200);
            animatorSet.start();
            w.a();
            w.a(this.f2924c);
        }
    }

    public void c() {
        this.f2924c.b();
    }

    public K getResultControl() {
        return this.j;
    }

    public int getScreenHeight() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) this.f2922a).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.heightPixels;
    }

    /* access modifiers changed from: protected */
    public void onFinishInflate() {
        Resources resources;
        int i2;
        super.onFinishInflate();
        this.f2924c = (MainContentFrame) findViewById(R.id.content_frame);
        this.f2925d = (MainHandleBar) findViewById(R.id.handle_bar);
        this.g = (RelativeLayout) findViewById(R.id.v_header_layout);
        this.h = (ImageView) findViewById(R.id.result_icon);
        this.i = (LinearLayout) findViewById(R.id.scan_result_layout);
        this.f2923b = (CustomActionBar) findViewById(R.id.actionbar);
        if (n.k(this.f2922a)) {
            resources = this.f2922a.getResources();
            i2 = R.dimen.antivirus_video_maigin_top;
        } else if (!n.c()) {
            resources = this.f2922a.getResources();
            i2 = R.dimen.antivirus_video_maigin_top_miui11;
        } else {
            resources = this.f2922a.getResources();
            i2 = R.dimen.antivirus_video_maigin_top_miui12;
        }
        a(resources.getDimensionPixelSize(i2));
    }

    public void setActionButtonText(CharSequence charSequence) {
        this.f2925d.setActionButtonText(charSequence);
    }

    public void setContentAlpha(float f2) {
        float f3 = (-1.2f * f2) + 1.0f;
        w.a(f2 < 1.0E-6f ? this.f2924c : this.e);
        this.f2924c.setHeaderLayoutAlpha(f3);
    }

    public void setContentProgressText(CharSequence charSequence) {
        this.f2924c.setProgressText(charSequence);
    }

    public void setContentSummary(CharSequence charSequence) {
        this.f2924c.setSummaryText(charSequence);
    }

    public void setEventHandler(b bVar) {
        this.f = bVar;
        this.f2924c.setEventHandler(bVar);
        this.f2925d.setEventHandler(bVar);
    }

    public void setHandleActionButtonEnabled(Boolean bool) {
        this.f2925d.setHandleActionButtonEnabled(bool);
    }

    public void setScanResult(CharSequence charSequence) {
        this.f2924c.setScanResult(charSequence);
    }
}
