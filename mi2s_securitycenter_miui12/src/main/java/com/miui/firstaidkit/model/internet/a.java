package com.miui.firstaidkit.model.internet;

import android.util.Log;
import com.miui.securityscan.model.AbsModel;
import java.util.Set;

class a implements AbsModel.AbsModelDisplayListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ BackgroundConnectionModel f3966a;

    a(BackgroundConnectionModel backgroundConnectionModel) {
        this.f3966a = backgroundConnectionModel;
    }

    public void onAbsModelDisplay() {
        if (this.f3966a.canSaveCache) {
            Log.d("BackgroundConnectionModel", "onAbsModelDisplay callback");
            boolean unused = this.f3966a.canSaveCache = false;
            if (this.f3966a.canRecountTime && this.f3966a.valueSet != null && this.f3966a.valueSet.size() > 0) {
                this.f3966a.spfHelper.a("BackgroundConnectionModel_BG", (Set<String>) this.f3966a.valueSet);
            }
        }
    }
}
