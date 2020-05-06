package com.miui.powercenter.autotask;

import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import com.miui.securitycenter.R;
import java.lang.ref.WeakReference;

public class OperationEditActivity extends B {

    /* renamed from: c  reason: collision with root package name */
    View.OnClickListener f6722c = new a(this, (J) null);

    private static class a implements View.OnClickListener {

        /* renamed from: a  reason: collision with root package name */
        private final WeakReference<OperationEditActivity> f6723a;

        private a(OperationEditActivity operationEditActivity) {
            this.f6723a = new WeakReference<>(operationEditActivity);
        }

        /* synthetic */ a(OperationEditActivity operationEditActivity, J j) {
            this(operationEditActivity);
        }

        public void onClick(View view) {
            OperationEditActivity operationEditActivity = (OperationEditActivity) this.f6723a.get();
            if (operationEditActivity != null) {
                if (view == operationEditActivity.f6690a) {
                    operationEditActivity.p();
                    operationEditActivity.finish();
                } else if (view == operationEditActivity.f6691b) {
                    operationEditActivity.n();
                }
            }
        }
    }

    /* access modifiers changed from: private */
    public void p() {
        q().c();
    }

    private U q() {
        return (U) getFragmentManager().findFragmentById(16908290);
    }

    /* access modifiers changed from: protected */
    public View.OnClickListener l() {
        return this.f6722c;
    }

    /* access modifiers changed from: protected */
    public String m() {
        return getString(R.string.auto_task_edit_choose_operation_title);
    }

    /* JADX WARNING: type inference failed for: r1v0, types: [com.miui.powercenter.autotask.OperationEditActivity, android.content.Context, miui.app.Activity] */
    /* access modifiers changed from: protected */
    public void n() {
        if (q().b()) {
            ea.a((Context) this, (DialogInterface.OnClickListener) new J(this));
        } else {
            finish();
        }
    }

    public Fragment o() {
        Bundle bundleExtra = getIntent().getBundleExtra("bundle");
        AutoTask autoTask = bundleExtra != null ? (AutoTask) bundleExtra.getParcelable("task") : null;
        U u = new U();
        u.b(autoTask);
        return u;
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        getFragmentManager().beginTransaction().replace(16908290, o()).commit();
    }
}
