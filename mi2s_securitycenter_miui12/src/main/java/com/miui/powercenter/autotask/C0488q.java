package com.miui.powercenter.autotask;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import com.miui.powercenter.a.b;
import com.miui.securitycenter.R;

/* renamed from: com.miui.powercenter.autotask.q  reason: case insensitive filesystem */
public class C0488q extends C0496z {

    /* renamed from: c  reason: collision with root package name */
    A<C0488q> f6763c;

    public void b(AutoTask autoTask) {
        a(autoTask);
    }

    public void c() {
        boolean z = this.f6775a.getId() <= 0;
        if (!this.f6775a.getName().equals(this.f6776b.getName())) {
            if (z) {
                b.o();
            } else {
                b.e();
            }
        }
        if (!this.f6775a.conditionsEquals(this.f6776b)) {
            if (z) {
                b.n();
            } else {
                b.d();
            }
        }
        if (this.f6775a.operationsEquals(this.f6776b)) {
            return;
        }
        if (z) {
            b.p();
        } else {
            b.f();
        }
    }

    public boolean d() {
        Activity activity;
        Activity activity2;
        int i;
        if (this.f6776b.isConditionEmpty()) {
            activity = getActivity();
            activity2 = getActivity();
            i = R.string.auto_task_edit_condition_title;
        } else if (this.f6776b.isOperationEmpty()) {
            activity = getActivity();
            activity2 = getActivity();
            i = R.string.auto_task_edit_choose_new_operation;
        } else {
            Activity activity3 = getActivity();
            if (!b()) {
                activity3.setResult(0);
                return true;
            }
            if (this.f6775a.getId() > 0) {
                if (!this.f6775a.getName().equals(this.f6776b.getName())) {
                    b.b();
                }
                if (!this.f6775a.conditionsEquals(this.f6776b)) {
                    b.a();
                }
                if (!this.f6775a.operationsEquals(this.f6776b)) {
                    b.c();
                }
            }
            this.f6776b.setEnabled(true);
            this.f6776b.setStarted(false);
            this.f6776b.removeAllRestoreOperation();
            C0489s.a((Context) getActivity(), this.f6776b);
            activity3.setResult(-1);
            return true;
        }
        C0473b.a(activity, activity2.getString(i));
        return false;
    }

    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
        this.f6763c.b(bundle);
    }

    public void onActivityResult(int i, int i2, Intent intent) {
        super.onActivityResult(i, i2, intent);
        this.f6763c.a(i, i2, intent);
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.f6763c = a() ? new C0487p(this.f6775a, this.f6776b) : new C0485n(this.f6775a, this.f6776b);
        this.f6763c.a(this);
        this.f6763c.a(bundle);
    }
}
