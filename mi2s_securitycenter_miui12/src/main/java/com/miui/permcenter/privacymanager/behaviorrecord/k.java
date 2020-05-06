package com.miui.permcenter.privacymanager.behaviorrecord;

import com.miui.permcenter.privacymanager.a;
import com.miui.permcenter.privacymanager.behaviorrecord.AppBehaviorRecordActivity;
import com.miui.permcenter.privacymanager.d;
import miui.widget.DropDownSingleChoiceMenu;

class k implements DropDownSingleChoiceMenu.OnMenuListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ AppBehaviorRecordActivity f6449a;

    k(AppBehaviorRecordActivity appBehaviorRecordActivity) {
        this.f6449a = appBehaviorRecordActivity;
    }

    public void onDismiss() {
        if (this.f6449a.r) {
            AppBehaviorRecordActivity appBehaviorRecordActivity = this.f6449a;
            new AppBehaviorRecordActivity.b(appBehaviorRecordActivity, appBehaviorRecordActivity.u).execute(new Void[0]);
            return;
        }
        this.f6449a.k.notifyDataSetChanged();
    }

    public void onItemSelected(DropDownSingleChoiceMenu dropDownSingleChoiceMenu, int i) {
        if (this.f6449a.u != i) {
            int unused = this.f6449a.u = i;
            boolean unused2 = this.f6449a.r = true;
            a.a(d.f6477a[this.f6449a.u]);
        }
    }

    public void onShow() {
    }
}
