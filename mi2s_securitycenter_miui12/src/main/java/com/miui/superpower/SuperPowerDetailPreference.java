package com.miui.superpower;

import android.content.Context;
import android.util.AttributeSet;
import androidx.preference.A;
import androidx.preference.Preference;
import com.miui.securitycenter.R;

public class SuperPowerDetailPreference extends Preference {
    public SuperPowerDetailPreference(Context context) {
        super(context);
        a();
    }

    public SuperPowerDetailPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        a();
    }

    public SuperPowerDetailPreference(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        a();
    }

    public SuperPowerDetailPreference(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        a();
    }

    private void a() {
        setLayoutResource(R.layout.ps_sp_settings_description);
    }

    public void onBindViewHolder(A a2) {
    }
}
