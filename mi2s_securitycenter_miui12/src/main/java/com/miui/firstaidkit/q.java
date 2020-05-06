package com.miui.firstaidkit;

import android.widget.CompoundButton;
import com.miui.firstaidkit.FirstAidKitWhiteListActivity;
import com.miui.securityscan.model.AbsModel;

class q implements CompoundButton.OnCheckedChangeListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ FirstAidKitWhiteListActivity.c f3978a;

    q(FirstAidKitWhiteListActivity.c cVar) {
        this.f3978a = cVar;
    }

    public void onCheckedChanged(CompoundButton compoundButton, boolean z) {
        AbsModel absModel = (AbsModel) compoundButton.getTag();
        if (absModel != null) {
            if (z) {
                this.f3978a.f3892b.add(absModel.getItemKey());
            } else {
                this.f3978a.f3892b.remove(absModel.getItemKey());
            }
            FirstAidKitWhiteListActivity.this.f3887d.setEnabled(!this.f3978a.f3892b.isEmpty());
        }
    }
}
