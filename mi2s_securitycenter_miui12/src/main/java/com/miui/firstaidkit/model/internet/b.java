package com.miui.firstaidkit.model.internet;

import android.util.Log;
import com.miui.securityscan.model.AbsModel;
import java.util.Set;

class b implements AbsModel.AbsModelDisplayListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ RestrictDataUsageModel f3967a;

    b(RestrictDataUsageModel restrictDataUsageModel) {
        this.f3967a = restrictDataUsageModel;
    }

    public void onAbsModelDisplay() {
        if (this.f3967a.canSaveCache) {
            Log.d("RestrictDataUsageModel", "onAbsModelDisplay callback");
            boolean unused = this.f3967a.canSaveCache = false;
            if (this.f3967a.canRecountTime) {
                if (this.f3967a.wlanValueSet != null && this.f3967a.wlanValueSet.size() > 0) {
                    this.f3967a.spfHelper.a("RestrictDataUsageModel_Wlan", (Set<String>) this.f3967a.wlanValueSet);
                }
                if (this.f3967a.mobileValueSet != null && this.f3967a.mobileValueSet.size() > 0) {
                    this.f3967a.spfHelper.a("RestrictDataUsageModel_Mobile", (Set<String>) this.f3967a.mobileValueSet);
                }
            }
        }
    }
}
