package com.miui.warningcenter;

import android.os.Bundle;
import b.b.c.c.a;
import com.miui.warningcenter.analytics.AnalyticHelper;

public class WarningCenterMainActivity extends a {
    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        getFragmentManager().beginTransaction().replace(16908290, new WarningCenterMainFragment()).commit();
        AnalyticHelper.trackMainModuleShow(AnalyticHelper.WARNINGCENTER_MAIN);
    }
}
