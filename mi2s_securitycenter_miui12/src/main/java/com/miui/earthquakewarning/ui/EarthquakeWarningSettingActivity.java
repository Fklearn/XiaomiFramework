package com.miui.earthquakewarning.ui;

import android.os.Bundle;
import b.b.c.c.a;
import miui.os.Build;
import miui.widget.SlidingButton;

public class EarthquakeWarningSettingActivity extends a {
    private SlidingButton mSlideNormal;
    private SlidingButton mSlidePush;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        if (Build.IS_INTERNATIONAL_BUILD) {
            finish();
        } else {
            getFragmentManager().beginTransaction().replace(16908290, new EarthquakeWarningSettingFragment()).commit();
        }
    }
}
