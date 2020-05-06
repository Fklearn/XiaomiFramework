package com.miui.appmanager;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import b.b.c.c.a;
import com.miui.appmanager.b.c;
import com.miui.warningcenter.mijia.MijiaAlertModel;

public class AMAppStorageDetailsActivity extends a {

    /* renamed from: a  reason: collision with root package name */
    private c f3482a;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        Intent intent = getIntent();
        String stringExtra = intent.getStringExtra("package_name");
        int intExtra = intent.getIntExtra(MijiaAlertModel.KEY_UID, -1);
        this.f3482a = new c();
        Bundle bundle2 = new Bundle();
        bundle2.putString("package_name", stringExtra);
        bundle2.putInt(MijiaAlertModel.KEY_UID, intExtra);
        this.f3482a.setArguments(bundle2);
        getFragmentManager().beginTransaction().replace(16908290, this.f3482a).commit();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        c cVar = this.f3482a;
        if (cVar != null) {
            cVar.a(menu);
        }
        return AMAppStorageDetailsActivity.super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(MenuItem menuItem) {
        c cVar = this.f3482a;
        if (cVar != null) {
            cVar.a(menuItem);
        }
        return AMAppStorageDetailsActivity.super.onOptionsItemSelected(menuItem);
    }

    public boolean onPrepareOptionsMenu(Menu menu) {
        c cVar = this.f3482a;
        if (cVar != null) {
            cVar.b(menu);
        }
        return AMAppStorageDetailsActivity.super.onPrepareOptionsMenu(menu);
    }
}
