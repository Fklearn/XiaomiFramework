package com.miui.powercenter.quickoptimize;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import b.b.c.d.C0184d;
import b.b.c.d.C0185e;
import b.b.c.d.C0191k;
import b.b.c.i.b;
import b.b.c.j.i;
import b.b.o.g.c;
import com.miui.common.customview.AutoPasteListView;
import com.miui.maml.folme.AnimatedProperty;
import com.miui.powercenter.a.a;
import com.miui.powercenter.abnormalscan.e;
import com.miui.powercenter.deepsave.g;
import com.miui.powercenter.utils.s;
import com.miui.securitycenter.R;
import java.util.ArrayList;
import java.util.List;
import miui.os.Build;

public class ScanResultFrame extends RelativeLayout {
    /* access modifiers changed from: private */

    /* renamed from: a  reason: collision with root package name */
    public Context f7208a;
    /* access modifiers changed from: private */

    /* renamed from: b  reason: collision with root package name */
    public b f7209b;
    /* access modifiers changed from: private */

    /* renamed from: c  reason: collision with root package name */
    public AutoPasteListView f7210c;
    /* access modifiers changed from: private */

    /* renamed from: d  reason: collision with root package name */
    public r f7211d;
    /* access modifiers changed from: private */
    public e e;
    private C0191k f;
    private List<C0185e> g;
    private ViewGroup h;
    /* access modifiers changed from: private */
    public Button i;
    private boolean j = false;
    private List<m> k = new ArrayList();
    private List<m> l = new ArrayList();
    private AutoPasteListView m;
    /* access modifiers changed from: private */
    public AutoPasteListView n;
    private List<m> o = new ArrayList();
    private int p = 0;
    private Handler q = new C(this);
    private b r = new D(this);

    public ScanResultFrame(Context context) {
        super(context);
    }

    public ScanResultFrame(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    /* access modifiers changed from: private */
    public void a(m mVar) {
        Log.i("ScanResultFrame", "Fix one issue end");
        if (this.f7211d.getCountForSection(0) != 0) {
            mVar.f7233c = null;
            if (this.l.size() == 1) {
                h();
                return;
            }
            this.l.remove(mVar);
            this.q.sendMessage(Message.obtain());
        }
    }

    private void c() {
        this.m.setVisibility(0);
        i();
        ObjectAnimator ofFloat = ObjectAnimator.ofFloat(this.e == null ? this.f7210c : this.n, AnimatedProperty.PROPERTY_NAME_ALPHA, new float[]{1.0f, 0.0f});
        ObjectAnimator ofFloat2 = ObjectAnimator.ofFloat(this.e == null ? this.f7210c : this.n, AnimatedProperty.PROPERTY_NAME_SCALE_X, new float[]{1.0f, 0.5f});
        ObjectAnimator ofFloat3 = ObjectAnimator.ofFloat(this.e == null ? this.f7210c : this.n, AnimatedProperty.PROPERTY_NAME_SCALE_Y, new float[]{1.0f, 0.5f});
        ofFloat.setInterpolator(new AccelerateInterpolator(1.2f));
        ofFloat2.setInterpolator(new AccelerateInterpolator(1.2f));
        ofFloat3.setInterpolator(new AccelerateInterpolator(1.2f));
        ofFloat.setDuration(300);
        ofFloat2.setDuration(300);
        ofFloat3.setDuration(300);
        ofFloat3.addListener(new J(this));
        int screenHeight = getScreenHeight();
        AutoPasteListView autoPasteListView = this.m;
        ObjectAnimator ofFloat4 = ObjectAnimator.ofFloat(autoPasteListView, "translationY", new float[]{autoPasteListView.getTranslationY() + ((float) screenHeight), this.m.getTranslationY()});
        ofFloat4.setInterpolator(new DecelerateInterpolator());
        ofFloat4.setDuration(400);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(new Animator[]{ofFloat2, ofFloat3, ofFloat, ofFloat4});
        animatorSet.start();
    }

    private void d() {
        this.m.setVisibility(0);
        (this.e == null ? this.f7210c : this.n).setVisibility(8);
    }

    private void e() {
        this.h.setVisibility(8);
    }

    /* access modifiers changed from: private */
    public void f() {
        TimeInterpolator timeInterpolator;
        if (this.l.isEmpty()) {
            Log.d("ScanResultFrame", "Select task list is empty");
            return;
        }
        List<m> list = this.l;
        m mVar = list.get(list.size() - 1);
        if (v.b().a(this.f7208a, mVar) > 0) {
            if (mVar.f7231a == 1) {
                int childCount = this.f7210c.getChildCount();
                GridView gridView = null;
                for (int i2 = 0; i2 < childCount; i2++) {
                    gridView = (GridView) this.f7210c.getChildAt(i2).findViewById(R.id.child_list);
                    if (gridView != null && gridView.getVisibility() == 0) {
                        break;
                    }
                }
                if (gridView != null && gridView.getVisibility() == 0) {
                    ArrayList arrayList = new ArrayList();
                    for (int i3 = 0; i3 < gridView.getCount(); i3++) {
                        arrayList.add(ObjectAnimator.ofFloat((ImageView) gridView.getChildAt((gridView.getCount() - 1) - i3), AnimatedProperty.PROPERTY_NAME_ALPHA, new float[]{1.0f, 0.0f}));
                    }
                    try {
                        timeInterpolator = (TimeInterpolator) c.a(Class.forName("miui.maml.animation.interpolater.LinearInterpolater"), (Class<?>[]) null, new Object[0]);
                    } catch (Exception e2) {
                        Log.e("ScanResultFrame", "LinearInterpolater exception: ", e2);
                        timeInterpolator = null;
                    }
                    AnimatorSet animatorSet = new AnimatorSet();
                    animatorSet.playSequentially(arrayList);
                    if (timeInterpolator != null) {
                        animatorSet.setInterpolator(timeInterpolator);
                    }
                    animatorSet.setDuration(100);
                    animatorSet.addListener(new K(this, mVar));
                    animatorSet.start();
                    return;
                }
            }
            a(mVar);
        }
    }

    /* access modifiers changed from: private */
    public void g() {
        a.i("quick_optimize_now");
        this.j = false;
        this.k.clear();
        this.k.addAll(v.b().d());
        this.l.clear();
        this.f7211d.a(false);
        for (m next : this.k) {
            if (L.a(Integer.valueOf(next.f7231a))) {
                this.l.add(next);
                a.i(next.a());
            }
        }
        if (this.l.isEmpty()) {
            h();
        } else {
            f();
        }
    }

    private long getExtendBatteryTime() {
        long j2 = 0;
        for (m a2 : getSelectedNotFixed()) {
            j2 += o.a(this.f7208a, a2);
        }
        return j2;
    }

    private int getOptimizeListSelectedCount() {
        int i2 = 0;
        for (m mVar : this.o) {
            if (L.a(Integer.valueOf(mVar.f7231a))) {
                i2++;
            }
        }
        return i2;
    }

    private List<m> getSelectedNotFixed() {
        List<m> d2 = v.b().d();
        ArrayList arrayList = new ArrayList();
        for (m next : d2) {
            if (L.a(Integer.valueOf(next.f7231a))) {
                arrayList.add(next);
            }
        }
        return arrayList;
    }

    /* access modifiers changed from: private */
    public void h() {
        a.e(v.b().g());
        a.a((v.b().e() / 60) / 1000);
        this.f7209b.sendEmptyMessage(1054);
        e();
        c();
    }

    private void i() {
        this.f = new C0191k();
        this.g = g.b();
        this.f.a(this.g);
        this.m.setAdapter(this.f);
    }

    /* access modifiers changed from: private */
    public void j() {
        int i2;
        Button button;
        if (getOptimizeListSelectedCount() > 0) {
            if (this.p != 1) {
                long extendBatteryTime = getExtendBatteryTime();
                if (extendBatteryTime > 0) {
                    String d2 = s.d(this.f7208a, extendBatteryTime);
                    this.i.setText(getResources().getString(R.string.btn_text_optimize_extend_battery_time, new Object[]{d2}));
                    return;
                }
            }
            button = this.i;
            i2 = R.string.btn_text_quick_save_power;
        } else {
            button = this.i;
            i2 = R.string.btn_text_optimize_manually;
        }
        button.setText(i2);
    }

    public void a() {
        this.o.clear();
        this.o.addAll(v.b().d());
        this.i = (Button) findViewById(R.id.btn_text_quick_fix);
        this.h = (ViewGroup) findViewById(R.id.bottom_bar);
        if (this.e != null) {
            this.n = (AutoPasteListView) findViewById(R.id.abnormal_scan_list_view);
            this.n.setTopDraggable(true);
            this.n.setVisibility(0);
            this.n.setAdapter(this.e);
            this.n.setOnScrollPercentChangeListener(new E(this));
            this.i.setText(getResources().getText(R.string.pc_abnormal_scan_button_text));
            this.e.a((e.a) new F(this));
        } else {
            this.f7210c = (AutoPasteListView) findViewById(R.id.handle_item_list_view);
            if (Build.IS_INTERNATIONAL_BUILD) {
                this.f7210c.setOverScrollMode(2);
            }
            this.f7210c.setTopDraggable(true);
            this.f7210c.setAdapter(this.f7211d);
            this.f7210c.setVisibility(0);
            this.f7211d.a(this.r);
            this.f7210c.setOnScrollPercentChangeListener(new G(this));
            j();
        }
        this.m = (AutoPasteListView) findViewById(R.id.deep_save_list);
        if (Build.IS_INTERNATIONAL_BUILD) {
            this.m.setOverScrollMode(2);
        }
        this.m.setTopDraggable(true);
        this.m.setOnScrollPercentChangeListener(new H(this));
        this.i.setOnClickListener(new I(this));
        setPadding(getPaddingLeft(), i.f(getContext()), getPaddingRight(), getPaddingBottom());
    }

    public void a(int i2) {
        if (v.b().c() == 0 || i2 == 0) {
            this.f7210c.setVisibility(8);
            i();
            this.f7209b.sendEmptyMessage(1054);
            d();
            e();
        }
    }

    public void a(Context context, r rVar, e eVar) {
        this.f7208a = context;
        this.f7211d = rVar;
        this.e = eVar;
    }

    public void b() {
        List<C0185e> list = this.g;
        if (list != null && list.size() > 0) {
            for (C0185e next : this.g) {
                if (next instanceof C0184d) {
                    b.b.g.a.a().b(((C0184d) next).h());
                }
            }
        }
    }

    public List<C0185e> getModels() {
        return null;
    }

    public int getScreenHeight() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) this.f7208a).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.heightPixels;
    }

    /* access modifiers changed from: protected */
    public void onFinishInflate() {
        super.onFinishInflate();
    }

    public void setEventHandler(b bVar) {
        this.f7209b = bVar;
    }
}
