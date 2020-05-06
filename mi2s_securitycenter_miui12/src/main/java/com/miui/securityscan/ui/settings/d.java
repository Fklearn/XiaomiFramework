package com.miui.securityscan.ui.settings;

import android.widget.CompoundButton;
import com.miui.securityscan.model.AbsModel;
import com.miui.securityscan.ui.settings.WhiteListActivity;

class d implements CompoundButton.OnCheckedChangeListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ WhiteListActivity.c f8039a;

    d(WhiteListActivity.c cVar) {
        this.f8039a = cVar;
    }

    public void onCheckedChanged(CompoundButton compoundButton, boolean z) {
        AbsModel absModel = (AbsModel) compoundButton.getTag();
        if (absModel != null) {
            if (z) {
                this.f8039a.f8021b.add(absModel.getItemKey());
            } else {
                this.f8039a.f8021b.remove(absModel.getItemKey());
            }
            WhiteListActivity.this.f8016d.setEnabled(!this.f8039a.f8021b.isEmpty());
        }
    }
}
