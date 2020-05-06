package com.miui.permcenter.permissions;

import android.content.Context;
import android.preference.Preference;
import android.view.View;
import com.miui.securitycenter.R;

public class l extends Preference {
    public l(Context context) {
        super(context);
        setLayoutResource(R.layout.pm_app_permission_use_item_preference);
    }

    /* access modifiers changed from: protected */
    public void onBindView(View view) {
        super.onBindView(view);
    }
}
