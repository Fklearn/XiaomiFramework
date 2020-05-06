package com.miui.powercenter.autotask;

import android.widget.CompoundButton;
import com.miui.powercenter.a.b;
import com.miui.powercenter.autotask.AutoTaskManageActivity;

/* renamed from: com.miui.powercenter.autotask.w  reason: case insensitive filesystem */
class C0493w implements CompoundButton.OnCheckedChangeListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ AutoTask f6770a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ AutoTaskManageActivity.a f6771b;

    C0493w(AutoTaskManageActivity.a aVar, AutoTask autoTask) {
        this.f6771b = aVar;
        this.f6770a = autoTask;
    }

    public void onCheckedChanged(CompoundButton compoundButton, boolean z) {
        if (this.f6770a.getEnabled() != z) {
            C0489s.a(compoundButton.getContext(), this.f6770a.getId(), z);
            if (!z) {
                C0495y.a(compoundButton.getContext());
            }
            b.i();
        }
    }
}
