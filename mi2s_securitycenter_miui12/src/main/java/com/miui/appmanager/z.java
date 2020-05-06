package com.miui.appmanager;

import android.content.Intent;
import android.view.View;

class z implements View.OnClickListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ ApplicationsDetailsActivity f3734a;

    z(ApplicationsDetailsActivity applicationsDetailsActivity) {
        this.f3734a = applicationsDetailsActivity;
    }

    /* JADX WARNING: type inference failed for: r0v0, types: [com.miui.appmanager.ApplicationsDetailsActivity, android.content.Context] */
    public void onClick(View view) {
        Intent intent = new Intent(this.f3734a, AMAppInfomationActivity.class);
        intent.putExtra("am_app_pkgname", this.f3734a.aa);
        intent.putExtra("am_app_label", this.f3734a.ca);
        intent.putExtra("am_app_uid", this.f3734a.Q);
        this.f3734a.startActivity(intent);
    }
}
