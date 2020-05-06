package com.miui.securityscan.ui.settings;

import android.os.Bundle;
import android.text.TextUtils;
import androidx.annotation.Nullable;
import b.b.c.c.a;
import com.miui.securitycenter.R;

public class SettingsActivity extends a {

    /* renamed from: a  reason: collision with root package name */
    private final String f8012a = "miui.intent.action.APP_SETTINGS";

    /* access modifiers changed from: protected */
    public void onCreate(@Nullable Bundle bundle) {
        super.onCreate(bundle);
        if (!TextUtils.equals("miui.intent.action.APP_SETTINGS", getIntent().getAction())) {
            setTitle(R.string.activity_title_settings);
        }
        if (bundle == null) {
            getFragmentManager().beginTransaction().replace(16908290, new b()).commit();
        }
    }
}
