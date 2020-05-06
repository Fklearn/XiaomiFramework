package com.miui.permcenter.privacymanager.behaviorrecord;

import android.content.Intent;
import android.view.View;
import com.miui.securitycenter.R;

class p implements View.OnClickListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ PrivacyDetailActivity f6458a;

    p(PrivacyDetailActivity privacyDetailActivity) {
        this.f6458a = privacyDetailActivity;
    }

    /* JADX WARNING: type inference failed for: r0v0, types: [android.content.Context, com.miui.permcenter.privacymanager.behaviorrecord.PrivacyDetailActivity] */
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.pm_activity_back /*2131297459*/:
                this.f6458a.finish();
                return;
            case R.id.pm_activity_more /*2131297460*/:
                Intent intent = new Intent(this.f6458a, SingleAppPrivacyManagerActivity.class);
                intent.putExtra("am_app_pkgname", this.f6458a.F);
                intent.putExtra("am_app_label", this.f6458a.I);
                intent.putExtra("am_app_uid", this.f6458a.H);
                this.f6458a.startActivity(intent);
                return;
            default:
                return;
        }
    }
}
