package com.miui.powercenter.autotask;

import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import com.miui.securitycenter.R;
import java.lang.ref.WeakReference;

public class AutoTaskEditActivity extends B {

    /* renamed from: c  reason: collision with root package name */
    View.OnClickListener f6677c = new b();

    private static class a implements DialogInterface.OnClickListener {

        /* renamed from: a  reason: collision with root package name */
        private final WeakReference<AutoTaskEditActivity> f6678a;

        private a(AutoTaskEditActivity autoTaskEditActivity) {
            this.f6678a = new WeakReference<>(autoTaskEditActivity);
        }

        public void onClick(DialogInterface dialogInterface, int i) {
            AutoTaskEditActivity autoTaskEditActivity = (AutoTaskEditActivity) this.f6678a.get();
            if (autoTaskEditActivity != null && -1 == i) {
                com.miui.powercenter.a.b.h();
                autoTaskEditActivity.q().c();
                autoTaskEditActivity.finish();
            }
        }
    }

    private static class b implements View.OnClickListener {

        /* renamed from: a  reason: collision with root package name */
        private final WeakReference<AutoTaskEditActivity> f6679a;

        private b(AutoTaskEditActivity autoTaskEditActivity) {
            this.f6679a = new WeakReference<>(autoTaskEditActivity);
        }

        public void onClick(View view) {
            AutoTaskEditActivity autoTaskEditActivity = (AutoTaskEditActivity) this.f6679a.get();
            if (autoTaskEditActivity != null) {
                if (view == autoTaskEditActivity.f6690a) {
                    if (autoTaskEditActivity.p()) {
                        com.miui.powercenter.a.b.g();
                        autoTaskEditActivity.finish();
                    }
                } else if (view == autoTaskEditActivity.f6691b) {
                    autoTaskEditActivity.n();
                }
            }
        }
    }

    /* access modifiers changed from: private */
    public boolean p() {
        return q().d();
    }

    /* access modifiers changed from: private */
    public C0488q q() {
        return (C0488q) getFragmentManager().findFragmentById(16908290);
    }

    /* access modifiers changed from: protected */
    public View.OnClickListener l() {
        return this.f6677c;
    }

    /* access modifiers changed from: protected */
    public String m() {
        return getResources().getString(R.string.auto_task_edit_activity_title);
    }

    /* JADX WARNING: type inference failed for: r2v0, types: [android.content.Context, miui.app.Activity, com.miui.powercenter.autotask.AutoTaskEditActivity] */
    /* access modifiers changed from: protected */
    public void n() {
        if (q().b()) {
            ea.a((Context) this, (DialogInterface.OnClickListener) new a());
        } else {
            finish();
        }
    }

    public Fragment o() {
        Bundle bundleExtra = getIntent().getBundleExtra("bundle");
        AutoTask autoTask = bundleExtra != null ? (AutoTask) bundleExtra.getParcelable("task") : null;
        C0488q qVar = new C0488q();
        qVar.b(autoTask);
        return qVar;
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        getFragmentManager().beginTransaction().replace(16908290, o()).commit();
    }
}
