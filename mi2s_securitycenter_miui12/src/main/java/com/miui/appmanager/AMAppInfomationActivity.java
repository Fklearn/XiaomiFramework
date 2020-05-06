package com.miui.appmanager;

import android.content.Intent;
import android.os.Bundle;
import b.b.c.c.a;
import com.miui.warningcenter.mijia.MijiaAlertModel;

public class AMAppInfomationActivity extends a {
    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        Intent intent = getIntent();
        String stringExtra = intent.getStringExtra("am_app_pkgname");
        String stringExtra2 = intent.getStringExtra("am_app_label");
        int intExtra = intent.getIntExtra("am_app_uid", -1);
        com.miui.appmanager.b.a aVar = new com.miui.appmanager.b.a();
        Bundle bundle2 = new Bundle();
        bundle2.putString("am_app_pkgname", stringExtra);
        bundle2.putString("am_app_label", stringExtra2);
        bundle2.putInt(MijiaAlertModel.KEY_UID, intExtra);
        aVar.setArguments(bundle2);
        getFragmentManager().beginTransaction().replace(16908290, aVar).commit();
    }
}
