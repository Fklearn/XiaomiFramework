package com.miui.powercenter.savemode;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;
import androidx.preference.A;
import androidx.preference.Preference;
import com.miui.powercenter.utils.o;
import com.miui.securitycenter.R;

public class DetailPreference extends Preference {
    public DetailPreference(Context context) {
        super(context);
        a();
    }

    public DetailPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        a();
    }

    public DetailPreference(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        a();
    }

    public DetailPreference(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        a();
    }

    private void a() {
        setLayoutResource(R.layout.ps_settings_description);
    }

    public void onBindViewHolder(A a2) {
        ((TextView) a2.b((int) R.id.txt_description4)).setText(o.i(getContext()));
    }
}
