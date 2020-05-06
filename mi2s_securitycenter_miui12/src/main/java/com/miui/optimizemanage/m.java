package com.miui.optimizemanage;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.LoaderManager;
import android.content.Context;
import android.content.Loader;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.UserHandle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import b.b.c.j.n;
import com.miui.appmanager.AppManageUtils;
import com.miui.maml.folme.AnimatedProperty;
import com.miui.optimizemanage.d.c;
import com.miui.optimizemanage.d.e;
import com.miui.optimizemanage.memoryclean.j;
import com.miui.optimizemanage.memoryclean.k;
import com.miui.optimizemanage.memoryclean.l;
import com.miui.optimizemanage.settings.c;
import com.miui.optimizemanage.view.OptimizeMainView;
import com.miui.optimizemanage.view.RunningProcessView;
import com.miui.securitycenter.Application;
import com.miui.securitycenter.R;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class m extends Fragment implements LoaderManager.LoaderCallbacks<List<j>>, View.OnClickListener {
    /* access modifiers changed from: private */

    /* renamed from: a  reason: collision with root package name */
    public TextView f5941a;
    /* access modifiers changed from: private */

    /* renamed from: b  reason: collision with root package name */
    public TextView f5942b;
    /* access modifiers changed from: private */

    /* renamed from: c  reason: collision with root package name */
    public TextView f5943c;

    /* renamed from: d  reason: collision with root package name */
    private Button f5944d;
    /* access modifiers changed from: private */
    public RunningProcessView e;
    /* access modifiers changed from: private */
    public RunningProcessView f;
    /* access modifiers changed from: private */
    public RunningProcessView g;
    /* access modifiers changed from: private */
    public View h;
    private View i;
    /* access modifiers changed from: private */
    public OptimizeMainView j;
    private Object k;
    /* access modifiers changed from: private */
    public k l;
    /* access modifiers changed from: private */
    public l m;
    private AnimatorSet n;
    /* access modifiers changed from: private */
    public OptimizemanageMainActivity o;
    public boolean p = false;
    private int q;
    /* access modifiers changed from: private */
    public int r;
    /* access modifiers changed from: private */
    public int s;
    private int t;
    /* access modifiers changed from: private */
    public int u;
    /* access modifiers changed from: private */
    public int v;
    private ArrayList<j> w = new ArrayList<>();

    public class a implements Interpolator {
        public a() {
        }

        public float getInterpolation(float f) {
            return f * f * f * f;
        }
    }

    /* access modifiers changed from: private */
    public void a() {
        ArrayList arrayList = new ArrayList();
        arrayList.addAll(this.m.a((Context) getActivity()));
        ArrayList arrayList2 = new ArrayList();
        ArrayList arrayList3 = new ArrayList();
        Iterator<j> it = this.w.iterator();
        while (it.hasNext()) {
            j next = it.next();
            if (next.e) {
                arrayList2.add(next.f5972a);
            } else {
                for (int i2 = 0; i2 < next.i.size(); i2++) {
                    if (next.i.get(i2).intValue() > 0) {
                        arrayList3.add(next.i.get(i2));
                    }
                }
            }
        }
        e.a((ArrayList<String>) arrayList, true, (List<Integer>) arrayList3);
        c.b(System.currentTimeMillis());
        this.l.b(arrayList2);
        c.c(System.currentTimeMillis());
    }

    private void a(int i2, int i3, int i4) {
        this.v = i2;
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) this.i.getLayoutParams();
        layoutParams.topMargin = i3;
        this.i.setLayoutParams(layoutParams);
        RelativeLayout.LayoutParams layoutParams2 = (RelativeLayout.LayoutParams) this.h.getLayoutParams();
        layoutParams2.topMargin = i4;
        this.h.setLayoutParams(layoutParams2);
    }

    /* access modifiers changed from: private */
    public void a(long j2) {
        Activity activity = getActivity();
        if (activity != null && !activity.isFinishing()) {
            String[] c2 = n.c(activity, Math.abs(j2), 0);
            this.f5941a.setText(c2[0]);
            this.f5942b.setText(c2[1]);
        }
    }

    /* access modifiers changed from: private */
    public void a(boolean z) {
        v vVar = new v();
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction beginTransaction = fragmentManager.beginTransaction();
        Bundle bundle = new Bundle();
        bundle.putBoolean("do_clean_anim", z);
        vVar.setArguments(bundle);
        if (fragmentManager.findFragmentById(R.id.result_content) == null) {
            beginTransaction.add(R.id.result_content, vVar, "result_fragment");
            beginTransaction.commitAllowingStateLoss();
            if (z) {
                new Handler().postDelayed(new k(this), 1000);
                this.j.b();
                b();
                return;
            }
            OptimizemanageMainActivity optimizemanageMainActivity = this.o;
            if (optimizemanageMainActivity != null) {
                optimizemanageMainActivity.l();
            }
            FragmentTransaction beginTransaction2 = fragmentManager.beginTransaction();
            beginTransaction2.remove(this);
            beginTransaction2.commitAllowingStateLoss();
        }
    }

    private boolean a(String str, int i2) {
        try {
            return (AppManageUtils.a(this.k, str, 0, i2).flags & 1) != 0;
        } catch (Exception unused) {
            return false;
        }
    }

    private void b() {
        ObjectAnimator ofFloat = ObjectAnimator.ofFloat(this.j, AnimatedProperty.PROPERTY_NAME_ALPHA, new float[]{1.0f, 0.0f});
        ofFloat.setDuration(400);
        ofFloat.setInterpolator(new AccelerateDecelerateInterpolator());
        com.miui.optimizemanage.d.c.e();
        com.miui.optimizemanage.d.c.a((c.b) new l(this));
        com.miui.optimizemanage.d.c.a(1.0f, 0.58f);
        com.miui.optimizemanage.d.c.a((c.a) new a(this));
        ValueAnimator ofFloat2 = ValueAnimator.ofFloat(new float[]{1.0f, 0.0f});
        ofFloat2.setDuration(300);
        ofFloat2.setInterpolator(new AccelerateDecelerateInterpolator());
        ofFloat2.addUpdateListener(new b(this));
        ObjectAnimator ofFloat3 = ObjectAnimator.ofFloat(this.h, AnimatedProperty.PROPERTY_NAME_SCALE_X, new float[]{1.0f, 0.9f});
        ofFloat3.setDuration(400);
        ofFloat3.setInterpolator(new a());
        ObjectAnimator ofFloat4 = ObjectAnimator.ofFloat(this.h, AnimatedProperty.PROPERTY_NAME_SCALE_Y, new float[]{1.0f, 0.9f});
        ofFloat4.setDuration(400);
        ofFloat4.setInterpolator(new a());
        View view = this.h;
        ObjectAnimator ofFloat5 = ObjectAnimator.ofFloat(view, "translationY", new float[]{0.0f, (float) view.getHeight()});
        ofFloat5.setDuration(400);
        ofFloat5.setInterpolator(new a());
        ofFloat.start();
        ofFloat2.start();
        ofFloat3.start();
        ofFloat4.start();
        ofFloat5.start();
    }

    private int c() {
        if (this.w.isEmpty()) {
            return 0;
        }
        Iterator<j> it = this.w.iterator();
        int i2 = 0;
        int i3 = 0;
        while (it.hasNext()) {
            j next = it.next();
            if (!next.e) {
                if (next.j) {
                    i2++;
                }
                i3 = (int) (((long) i3) + next.f5975d);
            }
        }
        if (i2 == 0) {
            return 0;
        }
        return i3;
    }

    private void d() {
        int dimensionPixelSize;
        int dimensionPixelSize2;
        int i2;
        Activity activity = getActivity();
        int a2 = activity != null ? b.b.c.j.e.a(activity) : 0;
        Resources resources = getResources();
        int b2 = b.b.c.j.e.b();
        if (a2 <= 1920) {
            dimensionPixelSize = resources.getDimensionPixelSize(R.dimen.om_clean_transition_y_el_1920);
            dimensionPixelSize2 = resources.getDimensionPixelSize(R.dimen.om_clean_fragment_content_margin_top_el_1920);
            i2 = R.dimen.om_optimize_layout_margin_top_el_1920;
        } else if (b2 <= 9) {
            dimensionPixelSize = resources.getDimensionPixelSize(R.dimen.om_clean_transition_y_v11);
            dimensionPixelSize2 = resources.getDimensionPixelSize(R.dimen.om_clean_fragment_content_margin_top_v11);
            i2 = R.dimen.om_optimize_layout_margin_top_v11;
        } else {
            dimensionPixelSize = resources.getDimensionPixelSize(R.dimen.om_clean_transition_y);
            dimensionPixelSize2 = resources.getDimensionPixelSize(R.dimen.om_clean_fragment_content_margin_top);
            i2 = R.dimen.om_optimize_layout_margin_top;
        }
        a(dimensionPixelSize, dimensionPixelSize2, resources.getDimensionPixelSize(i2));
    }

    private void e() {
        ArrayList arrayList = new ArrayList();
        int a2 = com.miui.optimizemanage.settings.c.a();
        ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{0.0f, 360.0f, 720.0f, 1440.0f});
        long j2 = (long) a2;
        ofFloat.setDuration(j2);
        ofFloat.setInterpolator(new AccelerateDecelerateInterpolator());
        ofFloat.addUpdateListener(new d(this));
        ofFloat.addListener(new f(this));
        arrayList.add(ofFloat);
        ValueAnimator ofInt = ValueAnimator.ofInt(new int[]{this.t, 1048576});
        ofInt.setDuration(j2);
        ofInt.addUpdateListener(new g(this));
        arrayList.add(ofInt);
        int i2 = this.q;
        int i3 = this.r;
        float f2 = (float) (i2 + i3 + this.s);
        float f3 = ((float) i3) / f2;
        float f4 = (float) a2;
        long j3 = (long) ((((float) i2) / f2) * f4);
        long j4 = (long) (f3 * f4);
        if (i2 > 0) {
            ValueAnimator ofInt2 = ValueAnimator.ofInt(new int[]{i2, 0});
            ofInt2.setDuration(j3);
            ofInt2.addListener(new h(this));
            arrayList.add(ofInt2);
        } else {
            this.e.a();
        }
        int i4 = this.r;
        if (i4 > 0) {
            ValueAnimator ofInt3 = ValueAnimator.ofInt(new int[]{i4, 0});
            ofInt3.setDuration(j4);
            ofInt3.setStartDelay(j3);
            ofInt3.addListener(new i(this));
            arrayList.add(ofInt3);
        }
        int i5 = this.s;
        if (i5 > 0) {
            ValueAnimator ofInt4 = ValueAnimator.ofInt(new int[]{i5, 0});
            ofInt4.setDuration((j2 - j3) - j4);
            ofInt4.setStartDelay(j3 + j4);
            ofInt4.addListener(new j(this));
            arrayList.add(ofInt4);
        }
        this.n = new AnimatorSet();
        this.n.playTogether(arrayList);
        this.n.start();
    }

    private void f() {
        AnimatorSet animatorSet = this.n;
        if (animatorSet != null && animatorSet.isRunning()) {
            this.n.cancel();
        }
    }

    private void g() {
        this.q = 0;
        this.r = 0;
        this.s = 0;
        Iterator<j> it = this.w.iterator();
        while (it.hasNext()) {
            j next = it.next();
            if (next.e) {
                this.q++;
            } else if (a(next.f5972a, UserHandle.getUserId(next.f5973b))) {
                this.r++;
            } else {
                this.s++;
            }
        }
    }

    /* renamed from: a */
    public void onLoadFinished(Loader<List<j>> loader, List<j> list) {
        Activity activity = getActivity();
        if (activity == null) {
            return;
        }
        if (activity != null && activity.isFinishing()) {
            return;
        }
        if (list == null || list.isEmpty() || this.p) {
            a(false);
            return;
        }
        this.w.clear();
        this.w.addAll(list);
        this.t = c();
        a((long) this.t);
        g();
        e();
    }

    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
        d();
    }

    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.o = (OptimizemanageMainActivity) activity;
    }

    public void onClick(View view) {
        if (view == this.f5944d) {
            ((OptimizemanageMainActivity) getActivity()).f5862b = true;
            this.p = true;
            getLoaderManager().destroyLoader(323);
            f();
            a(true);
            com.miui.optimizemanage.a.a.d("speedboost_stop");
        }
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
    }

    public Loader<List<j>> onCreateLoader(int i2, Bundle bundle) {
        return new c(this, getActivity());
    }

    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View inflate = layoutInflater.inflate(R.layout.om_clean_fragment_layout, (ViewGroup) null);
        this.l = new k();
        this.m = new l(Application.d());
        try {
            IBinder iBinder = (IBinder) b.b.o.g.e.a(Class.forName("android.os.ServiceManager"), "getService", (Class<?>[]) new Class[]{String.class}, "package");
            this.k = b.b.o.g.e.a(Class.forName("android.content.pm.IPackageManager$Stub"), "asInterface", (Class<?>[]) new Class[]{IBinder.class}, iBinder);
        } catch (Exception e2) {
            Log.e("CleanFragment", "reflect error get package manager service", e2);
        }
        this.f5941a = (TextView) inflate.findViewById(R.id.memory_size);
        this.f5942b = (TextView) inflate.findViewById(R.id.unit_flag);
        this.f5943c = (TextView) inflate.findViewById(R.id.memory_clean_summary);
        this.f5944d = (Button) inflate.findViewById(R.id.button_stop);
        this.f5944d.setOnClickListener(this);
        this.e = (RunningProcessView) inflate.findViewById(R.id.locked);
        this.e.setTitle(getString(R.string.om_running_locked_apps));
        this.f = (RunningProcessView) inflate.findViewById(R.id.system);
        this.f.setTitle(getString(R.string.om_running_system_apps));
        this.g = (RunningProcessView) inflate.findViewById(R.id.third);
        this.g.setTitle(getString(R.string.om_running_third_apps));
        this.h = inflate.findViewById(R.id.items_content);
        this.j = (OptimizeMainView) inflate.findViewById(R.id.ll_top_main);
        this.i = inflate.findViewById(R.id.header_content);
        Loader loader = getLoaderManager().getLoader(323);
        LoaderManager loaderManager = getLoaderManager();
        loaderManager.initLoader(323, (Bundle) null, this);
        if (!(Build.VERSION.SDK_INT < 24 || bundle == null || loader == null)) {
            loaderManager.restartLoader(323, (Bundle) null, this);
        }
        a(0);
        this.u = getResources().getDimensionPixelSize(R.dimen.activity_actionbar_transition_y);
        return inflate;
    }

    public void onDestroy() {
        super.onDestroy();
        f();
        com.miui.optimizemanage.d.c.c();
        com.miui.optimizemanage.d.c.d();
    }

    public void onDetach() {
        super.onDetach();
    }

    public void onLoaderReset(Loader<List<j>> loader) {
    }

    public void onViewCreated(View view, Bundle bundle) {
        super.onViewCreated(view, bundle);
    }
}
