package com.miui.powercenter.autotask;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class U extends C0496z {

    /* renamed from: c  reason: collision with root package name */
    private A<U> f6732c;

    public void b(AutoTask autoTask) {
        a(autoTask);
    }

    public boolean c() {
        Activity activity = getActivity();
        if (!b()) {
            activity.setResult(0);
            return false;
        }
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putParcelable("task", this.f6776b);
        intent.putExtra("bundle", bundle);
        activity.setResult(-1, intent);
        return true;
    }

    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
        this.f6732c.b(bundle);
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.f6732c = a() ? new T(this.f6775a, this.f6776b) : new N(this.f6775a, this.f6776b);
        this.f6732c.a(this);
        this.f6732c.a(bundle);
    }
}
