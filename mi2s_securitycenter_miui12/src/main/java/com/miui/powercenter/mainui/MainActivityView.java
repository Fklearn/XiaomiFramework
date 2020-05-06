package com.miui.powercenter.mainui;

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
import android.view.animation.PathInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import b.b.c.i.b;
import b.b.c.j.i;
import com.miui.antivirus.result.C0238a;
import com.miui.common.customview.ActionBarContainer;
import com.miui.common.ui.MediaTextureView;
import com.miui.maml.folme.AnimatedProperty;
import com.miui.powercenter.abnormalscan.e;
import com.miui.powercenter.quickoptimize.B;
import com.miui.powercenter.quickoptimize.MainContentFrame;
import com.miui.powercenter.quickoptimize.ScanResultFrame;
import com.miui.powercenter.quickoptimize.r;
import com.miui.powercenter.view.MainHandleBar;
import com.miui.securitycenter.R;
import java.util.ArrayList;
import miui.view.animation.CubicEaseOutInterpolator;
import miui.view.animation.SineEaseInOutInterpolator;

public class MainActivityView extends RelativeLayout {

    /* renamed from: a  reason: collision with root package name */
    private Context f7109a;

    /* renamed from: b  reason: collision with root package name */
    private Activity f7110b;

    /* renamed from: c  reason: collision with root package name */
    private MainContentFrame f7111c;
    /* access modifiers changed from: private */

    /* renamed from: d  reason: collision with root package name */
    public MainHandleBar f7112d;
    private ScanResultFrame e;
    /* access modifiers changed from: private */
    public b f;
    private RelativeLayout g;
    private MediaTextureView h;
    private ImageView i;
    private RelativeLayout j;
    private B k;
    private r l;
    private e m;
    /* access modifiers changed from: private */
    public ActionBarContainer n;
    private ArrayList<C0238a> o;
    private int p;

    public MainActivityView(Context context) {
        this(context, (AttributeSet) null);
        this.f7109a = context;
    }

    public MainActivityView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.o = new ArrayList<>();
        this.f7109a = context;
    }

    private void f() {
        ViewStub viewStub = (ViewStub) findViewById(R.id.content_container);
        viewStub.inflate();
        if (i.e()) {
            int notchOffset = this.f7111c.getNotchOffset();
            ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) viewStub.getLayoutParams();
            marginLayoutParams.topMargin += notchOffset;
            viewStub.setLayoutParams(marginLayoutParams);
        }
    }

    public void a() {
        ObjectAnimator ofFloat = ObjectAnimator.ofFloat(this.f7112d, AnimatedProperty.PROPERTY_NAME_ALPHA, new float[]{1.0f, 0.0f});
        ObjectAnimator ofFloat2 = ObjectAnimator.ofFloat(this.f7112d, AnimatedProperty.PROPERTY_NAME_SCALE_X, new float[]{1.0f, 0.5f});
        ObjectAnimator ofFloat3 = ObjectAnimator.ofFloat(this.f7112d, AnimatedProperty.PROPERTY_NAME_SCALE_Y, new float[]{1.0f, 0.5f});
        ObjectAnimator ofFloat4 = ObjectAnimator.ofFloat(this.h, AnimatedProperty.PROPERTY_NAME_ALPHA, new float[]{1.0f, 0.0f});
        ObjectAnimator ofFloat5 = ObjectAnimator.ofFloat(this.h, AnimatedProperty.PROPERTY_NAME_SCALE_X, new float[]{1.0f, 0.56f});
        ObjectAnimator ofFloat6 = ObjectAnimator.ofFloat(this.h, AnimatedProperty.PROPERTY_NAME_SCALE_Y, new float[]{1.0f, 0.56f});
        ofFloat.setInterpolator(new AccelerateInterpolator(1.2f));
        ofFloat2.setInterpolator(new AccelerateInterpolator(1.2f));
        ofFloat3.setInterpolator(new AccelerateInterpolator(1.2f));
        ofFloat5.setInterpolator(new CubicEaseOutInterpolator());
        ofFloat6.setInterpolator(new CubicEaseOutInterpolator());
        findViewById(R.id.number).getLocationOnScreen(new int[2]);
        int height = ((this.g.getHeight() - getResources().getDimensionPixelSize(R.dimen.antivirus_result_list_first_item_height)) / 2) + (((this.j.getBottom() - this.i.getBottom()) / 3) * 2);
        RelativeLayout relativeLayout = this.g;
        float f2 = (float) height;
        ObjectAnimator ofFloat7 = ObjectAnimator.ofFloat(relativeLayout, "translationY", new float[]{relativeLayout.getTranslationY(), (this.g.getTranslationY() - f2) + ((float) this.p)});
        ofFloat7.setInterpolator(new PathInterpolator(0.6f, 0.35f, 0.19f, 1.0f));
        ofFloat7.setDuration(400);
        ObjectAnimator ofFloat8 = ObjectAnimator.ofFloat(this.h, "translationY", new float[]{this.g.getTranslationY(), (this.g.getTranslationY() - f2) + ((float) this.p)});
        ofFloat8.setInterpolator(new SineEaseInOutInterpolator());
        ValueAnimator ofInt = ValueAnimator.ofInt(new int[]{0, getResources().getDimensionPixelSize(R.dimen.view_dimen_120)});
        ofInt.setDuration(200);
        ofInt.setRepeatCount(0);
        ofInt.addUpdateListener(new b(this));
        ofInt.start();
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(new Animator[]{ofFloat2, ofFloat3, ofFloat, ofFloat4, ofFloat5, ofFloat6, ofFloat8});
        animatorSet.setDuration(400);
        ofFloat7.start();
        animatorSet.addListener(new c(this));
        animatorSet.start();
    }

    public void a(int i2) {
        ActionBarContainer actionBarContainer = this.n;
        if (actionBarContainer != null) {
            actionBarContainer.a(i2);
        }
    }

    public void a(int i2, boolean z) {
        this.f7111c.a(i2, z);
    }

    public void a(r rVar, e eVar, ArrayList<C0238a> arrayList) {
        this.l = rVar;
        this.m = eVar;
        this.o = arrayList;
    }

    public void a(MainHandleBar.b bVar, MainHandleBar.a aVar) {
        this.f7112d.a(bVar, aVar);
    }

    public void a(boolean z, int i2) {
        this.f7111c.a(z, i2);
    }

    public void b() {
        ScanResultFrame scanResultFrame = this.e;
        if (scanResultFrame != null) {
            scanResultFrame.b();
        }
    }

    public boolean c() {
        if (this.e == null) {
            f();
            this.e = (ScanResultFrame) findViewById(R.id.pc_content_container);
            B.a aVar = new B.a(this.f7109a);
            aVar.a(this.e);
            aVar.a(this.l, this.m);
            this.k = aVar.a();
            this.k.a(this.f);
            if (this.m == null) {
                this.k.a();
                int screenHeight = getScreenHeight();
                ScanResultFrame scanResultFrame = this.e;
                ObjectAnimator ofFloat = ObjectAnimator.ofFloat(scanResultFrame, "translationY", new float[]{scanResultFrame.getTranslationY() + ((float) screenHeight), this.e.getTranslationY()});
                ofFloat.setInterpolator(new PathInterpolator(0.6f, 0.35f, 0.19f, 1.0f));
                AnimatorSet animatorSet = new AnimatorSet();
                animatorSet.playTogether(new Animator[]{ofFloat});
                animatorSet.setDuration(400);
                animatorSet.start();
            }
        }
        return true;
    }

    public void d() {
        this.f7111c.c();
    }

    public void e() {
        this.f7111c.b();
    }

    public B getResultControl() {
        return this.k;
    }

    public int getScreenHeight() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) this.f7109a).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.heightPixels;
    }

    /* access modifiers changed from: protected */
    public void onFinishInflate() {
        Resources resources;
        int i2;
        super.onFinishInflate();
        this.f7111c = (MainContentFrame) findViewById(R.id.content_frame);
        this.f7112d = (MainHandleBar) findViewById(R.id.handle_bar);
        this.g = (RelativeLayout) findViewById(R.id.v_header_layout);
        this.h = (MediaTextureView) findViewById(R.id.animation_view);
        this.i = (ImageView) findViewById(R.id.ic_result_icon_image);
        this.j = (RelativeLayout) findViewById(R.id.scan_result_layout);
        this.n = (ActionBarContainer) findViewById(R.id.abc_action_bar);
        Context context = this.f7109a;
        if (context != null) {
            this.f7110b = (Activity) context;
        }
        int a2 = b.b.c.j.e.a(this.f7110b);
        int b2 = b.b.c.j.e.b();
        if (a2 <= 1920) {
            resources = getResources();
            i2 = R.dimen.pc_list_preferred_button_height;
        } else if (b2 <= 9) {
            resources = getResources();
            i2 = R.dimen.pc_list_preferred_button_height_v11;
        } else {
            resources = getResources();
            i2 = R.dimen.pc_list_preferred_button_height_v12;
        }
        this.p = resources.getDimensionPixelSize(i2);
    }

    public void setActionButtonText(CharSequence charSequence) {
        this.f7112d.setActionButtonText(charSequence);
    }

    public void setContentAlpha(float f2) {
        this.f7111c.setHeaderLayoutAlpha((f2 * -1.2f) + 1.0f);
    }

    public void setContentProgressText(CharSequence charSequence) {
        this.f7111c.setProgressText(charSequence);
    }

    public void setContentSummary(CharSequence charSequence) {
        this.f7111c.setSummaryText(charSequence);
    }

    public void setEventHandler(b bVar) {
        this.f = bVar;
        this.f7111c.setEventHandler(bVar);
        this.f7112d.setEventHandler(bVar);
    }

    public void setFinalResultAlpha(float f2) {
        this.f7111c.setFinalResultIconAlpha((f2 * -1.2f) + 1.0f);
    }

    public void setHandleActionButtonEnabled(Boolean bool) {
        this.f7112d.setHandleActionButtonEnabled(bool);
    }

    public void setHandleBarVisibility(int i2) {
        this.f7112d.setVisibility(i2);
    }

    public void setScanResult(CharSequence charSequence) {
        this.f7111c.setScanResult(charSequence);
    }
}
