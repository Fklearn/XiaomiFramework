package com.miui.applicationlock;

import android.os.Bundle;
import com.miui.securitycenter.R;

public class PrivacyAppsConfirmAccessControl extends ConfirmAccessControl {
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.h.setImageResource(R.drawable.pa_confirm_icon);
        this.V = true;
        this.B = false;
        this.f3136b.setLightMode(this.B);
        if ("numeric".equals(this.ca)) {
            this.da.a(this.B);
        }
    }
}
