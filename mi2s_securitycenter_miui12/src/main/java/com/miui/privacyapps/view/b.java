package com.miui.privacyapps.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.Button;
import com.miui.securitycenter.R;

public class b extends Dialog {

    /* renamed from: a  reason: collision with root package name */
    private Button f7430a;

    public b(Context context) {
        super(context);
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.privacy_apps_dialog_layout);
        this.f7430a = (Button) findViewById(R.id.pa_button);
        this.f7430a.setOnClickListener(new a(this));
    }
}
