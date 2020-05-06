package com.miui.optimizemanage;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.Fragment;
import android.app.LoaderManager;
import android.content.Context;
import android.content.Loader;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import b.b.c.j.e;
import b.b.g.a;
import com.miui.common.customview.AutoPasteListView;
import com.miui.maml.folme.AnimatedProperty;
import com.miui.optimizemanage.c.d;
import com.miui.optimizemanage.c.f;
import com.miui.optimizemanage.c.i;
import com.miui.optimizemanage.c.j;
import com.miui.optimizemanage.c.m;
import com.miui.optimizemanage.c.n;
import com.miui.optimizemanage.d.c;
import com.miui.optimizemanage.memoryclean.LockAppManageActivity;
import com.miui.optimizemanage.memoryclean.b;
import com.miui.securitycenter.R;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import miui.os.Build;

public class v extends Fragment implements LoaderManager.LoaderCallbacks<f>, a.b {

    /* renamed from: a  reason: collision with root package name */
    private AutoPasteListView f6006a;
    /* access modifiers changed from: private */

    /* renamed from: b  reason: collision with root package name */
    public TextView f6007b;
    /* access modifiers changed from: private */

    /* renamed from: c  reason: collision with root package name */
    public TextView f6008c;
    /* access modifiers changed from: private */

    /* renamed from: d  reason: collision with root package name */
    public ImageView f6009d;
    /* access modifiers changed from: private */
    public ImageView e;
    private View f;
    private View g;
    /* access modifiers changed from: private */
    public RelativeLayout h;
    /* access modifiers changed from: private */
    public m i;
    private b.b.g.a j;
    /* access modifiers changed from: private */
    public boolean k = false;
    private boolean l;
    private int m = 0;
    /* access modifiers changed from: private */
    public List<d> n = new ArrayList();
    private LockAppManageActivity.a o;

    private static class a implements LockAppManageActivity.a {

        /* renamed from: a  reason: collision with root package name */
        private WeakReference<v> f6010a;

        /* renamed from: b  reason: collision with root package name */
        private Context f6011b;

        public a(v vVar) {
            this.f6010a = new WeakReference<>(vVar);
            if (vVar != null) {
                this.f6011b = vVar.getActivity().getApplicationContext();
            }
        }

        public void a() {
            v vVar = (v) this.f6010a.get();
            Context context = this.f6011b;
            if (context != null && vVar != null) {
                List<com.miui.optimizemanage.memoryclean.a> b2 = b.b(context);
                ArrayList arrayList = new ArrayList(vVar.n);
                for (int i = 0; i < arrayList.size(); i++) {
                    d dVar = (d) arrayList.get(i);
                    if (dVar instanceof n) {
                        if (b2.size() > 0) {
                            ((n) dVar).a(this.f6011b, b2);
                        } else {
                            arrayList.remove(i);
                            vVar.i.clear();
                            vVar.i.addAll(arrayList);
                        }
                        vVar.i.notifyDataSetChanged();
                        vVar.n.clear();
                        vVar.n.addAll(arrayList);
                        return;
                    }
                }
            }
        }
    }

    private String a() {
        long a2 = e.a();
        long c2 = e.c();
        String string = getResources().getString(R.string.om_memory_clean_memory_info_text, new Object[]{b.b.c.j.n.a((Context) getActivity(), a2, false), b.b.c.j.n.a((Context) getActivity(), c2, true)});
        String string2 = getResources().getString(R.string.optimize_result_available_memory_info);
        return string2 + " " + string;
    }

    private void a(List<d> list) {
        i a2;
        if (!list.isEmpty()) {
            for (int i2 = 0; i2 < list.size(); i2++) {
                d dVar = list.get(i2);
                if (dVar instanceof i) {
                    i iVar = (i) dVar;
                    if (!iVar.s() && (a2 = n.a(iVar.m(), iVar.j(), iVar.o())) != null && a2.s()) {
                        iVar.h(a2);
                    }
                }
            }
        }
    }

    private int b() {
        int i2;
        Resources resources;
        Activity activity = getActivity();
        int a2 = activity != null ? e.a(activity) : 0;
        if (e.b() <= 9 || a2 <= 1920) {
            resources = getResources();
            i2 = R.dimen.om_clean_transition_y_v11;
        } else {
            resources = getResources();
            i2 = R.dimen.om_clean_transition_y;
        }
        return resources.getDimensionPixelSize(i2);
    }

    private void c() {
        int b2 = b();
        this.f6009d.setScaleX(1.7f);
        this.f6009d.setScaleY(1.7f);
        float f2 = (float) b2;
        this.f6009d.setTranslationY(f2);
        this.e.setScaleX(1.7f);
        this.e.setScaleY(1.7f);
        this.e.setTranslationY(f2);
        this.f.setAlpha(0.0f);
        c.a((c.a) new r(this));
        c.a((c.b) new s(this, b2));
        ObjectAnimator ofFloat = ObjectAnimator.ofFloat(this.f, AnimatedProperty.PROPERTY_NAME_ALPHA, new float[]{0.0f, 1.0f});
        ofFloat.setInterpolator(new AccelerateDecelerateInterpolator());
        ofFloat.setDuration(400);
        ValueAnimator ofFloat2 = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
        ofFloat2.setDuration(300);
        ofFloat2.setStartDelay(300);
        ofFloat2.setInterpolator(new AccelerateDecelerateInterpolator());
        ofFloat2.addUpdateListener(new t(this));
        ObjectAnimator ofFloat3 = ObjectAnimator.ofFloat(this.f6006a, "translationY", new float[]{1543.0f, 0.0f});
        ofFloat3.setDuration(600);
        ofFloat3.setInterpolator(new com.miui.optimizemanage.view.d());
        this.f6008c.setAlpha(0.0f);
        this.f6007b.setAlpha(0.0f);
        ValueAnimator ofFloat4 = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
        ofFloat4.setDuration(300);
        ofFloat4.setStartDelay(300);
        ofFloat4.addUpdateListener(new u(this));
        ofFloat.start();
        ofFloat2.start();
        ofFloat3.start();
        ofFloat4.start();
    }

    private void d() {
        this.f6009d.setScaleY(0.0f);
        this.f6009d.setScaleX(0.0f);
        this.e.setScaleX(0.0f);
        this.e.setScaleY(0.0f);
        ObjectAnimator ofFloat = ObjectAnimator.ofFloat(this.f6009d, AnimatedProperty.PROPERTY_NAME_SCALE_X, new float[]{0.0f, 1.0f});
        ofFloat.setStartDelay(0);
        ofFloat.setDuration(400);
        ofFloat.setInterpolator(new AccelerateDecelerateInterpolator());
        ofFloat.start();
        ObjectAnimator ofFloat2 = ObjectAnimator.ofFloat(this.f6009d, AnimatedProperty.PROPERTY_NAME_SCALE_Y, new float[]{0.0f, 1.0f});
        ofFloat2.setDuration(400);
        ofFloat2.setStartDelay(0);
        ofFloat2.setInterpolator(new AccelerateDecelerateInterpolator());
        ofFloat2.start();
        ObjectAnimator ofFloat3 = ObjectAnimator.ofFloat(this.e, AnimatedProperty.PROPERTY_NAME_SCALE_X, new float[]{0.0f, 1.0f});
        ofFloat3.setStartDelay(0);
        ofFloat3.setDuration(400);
        ofFloat3.setInterpolator(new AccelerateDecelerateInterpolator());
        ofFloat3.start();
        ObjectAnimator ofFloat4 = ObjectAnimator.ofFloat(this.e, AnimatedProperty.PROPERTY_NAME_SCALE_Y, new float[]{0.0f, 1.0f});
        ofFloat4.setDuration(400);
        ofFloat4.setStartDelay(0);
        ofFloat4.setInterpolator(new AccelerateDecelerateInterpolator());
        ofFloat4.start();
        ObjectAnimator ofFloat5 = ObjectAnimator.ofFloat(this.f6009d, AnimatedProperty.PROPERTY_NAME_ALPHA, new float[]{0.0f, 1.0f});
        ofFloat5.setDuration(400);
        ofFloat5.setInterpolator(new AccelerateDecelerateInterpolator());
        ofFloat5.start();
        ObjectAnimator ofFloat6 = ObjectAnimator.ofFloat(this.f6008c, AnimatedProperty.PROPERTY_NAME_ALPHA, new float[]{0.0f, 1.0f});
        ofFloat6.setDuration(400);
        ofFloat6.setStartDelay(200);
        this.f6008c.setAlpha(0.0f);
        ofFloat6.setInterpolator(new AccelerateDecelerateInterpolator());
        ofFloat6.start();
        ObjectAnimator ofFloat7 = ObjectAnimator.ofFloat(this.f6007b, AnimatedProperty.PROPERTY_NAME_ALPHA, new float[]{0.0f, 1.0f});
        ofFloat7.setDuration(400);
        ofFloat7.setStartDelay(200);
        this.f6007b.setAlpha(0.0f);
        ofFloat7.setInterpolator(new AccelerateDecelerateInterpolator());
        ofFloat7.start();
        ObjectAnimator ofFloat8 = ObjectAnimator.ofFloat(this.f6006a, "translationY", new float[]{1543.0f, 0.0f});
        ofFloat8.setDuration(600);
        ofFloat8.setInterpolator(new com.miui.optimizemanage.view.d());
        ofFloat8.start();
    }

    /* renamed from: a */
    public void onLoadFinished(Loader<f> loader, f fVar) {
        List<d> e2 = fVar != null ? fVar.e() : null;
        if (fVar == null || e2.isEmpty() || (!Build.IS_INTERNATIONAL_BUILD && !fVar.a())) {
            this.n.clear();
            this.n.addAll(f.a((Context) getActivity()));
        } else {
            this.n.clear();
            this.n.addAll(f.f5895b);
            List<com.miui.optimizemanage.memoryclean.a> b2 = b.b(getActivity());
            if (!b2.isEmpty()) {
                this.n.add(f.a((Context) getActivity(), b2));
            }
            this.n.addAll(e2);
            if (Build.IS_INTERNATIONAL_BUILD) {
                a(this.n);
            }
        }
        this.i.clear();
        this.i.addAll(this.n);
        this.i.notifyDataSetChanged();
    }

    public void a(com.miui.optimizemanage.c.c cVar) {
        ArrayList arrayList = new ArrayList(this.n);
        int indexOf = arrayList.indexOf(cVar);
        if (indexOf > 0 && indexOf < arrayList.size() - 1) {
            int i2 = indexOf - 1;
            int i3 = indexOf + 1;
            if (i2 >= 0 && i3 < arrayList.size() && (arrayList.get(i2) instanceof j) && (arrayList.get(i3) instanceof j)) {
                arrayList.remove(i2);
            }
        }
        arrayList.remove(cVar);
        this.i.clear();
        this.i.addAll(arrayList);
        this.i.notifyDataSetChanged();
        this.n.clear();
        this.n.addAll(arrayList);
    }

    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        Bundle arguments = getArguments();
        if (arguments != null) {
            this.l = arguments.getBoolean("do_clean_anim", false);
        }
    }

    public Loader<f> onCreateLoader(int i2, Bundle bundle) {
        return new q(this, getActivity());
    }

    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        AutoPasteListView autoPasteListView;
        int i2 = 0;
        this.g = layoutInflater.inflate(R.layout.om_clean_result_layout, (ViewGroup) null, false);
        this.h = (RelativeLayout) this.g.findViewById(R.id.clean_finish_layout);
        this.f6009d = (ImageView) this.g.findViewById(R.id.clean_finish_icon);
        this.e = (ImageView) this.g.findViewById(R.id.clean_little_icon);
        this.f6006a = (AutoPasteListView) this.g.findViewById(R.id.clean_result);
        this.f6006a.setAlignItem(0);
        if (Build.IS_INTERNATIONAL_BUILD) {
            autoPasteListView = this.f6006a;
            i2 = 2;
        } else {
            autoPasteListView = this.f6006a;
        }
        autoPasteListView.setOverScrollMode(i2);
        this.f6006a.setTopDraggable(true);
        this.f6006a.setOnScrollPercentChangeListener(new p(this));
        this.i = new m(getActivity());
        this.f6006a.setAdapter(this.i);
        this.f6007b = (TextView) this.g.findViewById(R.id.clean_memory_text);
        this.f6007b.setText(a());
        this.f6008c = (TextView) this.g.findViewById(R.id.clean_finish_text);
        this.f = this.g.findViewById(R.id.result_icon_content);
        if (((OptimizemanageMainActivity) getActivity()).f5862b) {
            this.f6008c.setText(R.string.om_not_finish_speedboost);
            this.f6007b.setVisibility(8);
        } else {
            this.f6008c.setText(R.string.memory_clean_no_need_clean);
        }
        Loader loader = getLoaderManager().getLoader(324);
        LoaderManager loaderManager = getLoaderManager();
        loaderManager.initLoader(324, (Bundle) null, this);
        if (!(Build.VERSION.SDK_INT < 24 || bundle == null || loader == null)) {
            loaderManager.restartLoader(324, (Bundle) null, this);
        }
        this.o = new a(this);
        LockAppManageActivity.a(this.o);
        this.j = b.b.g.a.a();
        this.j.a(this);
        return this.g;
    }

    public void onDestroy() {
        super.onDestroy();
        this.j.c(this);
        LockAppManageActivity.b(this.o);
        for (int i2 = 0; i2 < this.n.size(); i2++) {
            d dVar = this.n.get(i2);
            if (dVar != null && (dVar instanceof i)) {
                i iVar = (i) dVar;
                if (iVar.s()) {
                    n.a(iVar.l());
                    this.j.b(iVar.l());
                }
            }
        }
        c.c();
        c.d();
    }

    public void onLoaderReset(Loader<f> loader) {
    }

    public void onViewCreated(View view, Bundle bundle) {
        super.onViewCreated(view, bundle);
        if (this.l) {
            c();
        } else {
            d();
        }
    }
}
