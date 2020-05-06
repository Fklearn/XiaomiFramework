package com.miui.permcenter.settings;

import android.app.Activity;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import b.b.c.c.a;
import com.miui.networkassistant.config.Constants;
import com.miui.securitycenter.R;
import miui.app.ActionBar;

public class PrivacySettingsActivity extends a {
    public void a(Activity activity) {
        Resources resources;
        try {
            resources = activity.getPackageManager().getResourcesForApplication(Constants.System.ANDROID_PACKAGE_NAME);
        } catch (Exception e) {
            Log.e("PrivacySettings", "setPendingTransition: ", e);
            resources = null;
        }
        if (resources != null) {
            activity.overridePendingTransition(resources.getIdentifier("activity_close_enter", "anim", Constants.System.ANDROID_PACKAGE_NAME), resources.getIdentifier("activity_close_exit", "anim", Constants.System.ANDROID_PACKAGE_NAME));
        }
    }

    /* JADX WARNING: type inference failed for: r0v0, types: [com.miui.permcenter.settings.PrivacySettingsActivity, miui.app.Activity, android.app.Activity] */
    public void onBackPressed() {
        PrivacySettingsActivity.super.onBackPressed();
        a(this);
    }

    /* JADX WARNING: type inference failed for: r6v0, types: [b.b.c.c.a, android.content.Context, com.miui.permcenter.settings.PrivacySettingsActivity, miui.app.Activity] */
    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        ActionBar actionBar = getActionBar();
        actionBar.setFragmentViewPagerMode(this, getFragmentManager());
        actionBar.addFragmentTab(j.f6519a, actionBar.newTab().setText(R.string.privacy_informed_title), j.class, (Bundle) null, false);
        actionBar.addFragmentTab(n.f6553a, actionBar.newTab().setText(R.string.privacy_manage_title), n.class, (Bundle) null, false);
        actionBar.addOnFragmentViewPagerChangeListener(new y(this));
        com.miui.permcenter.a.a.d("privacy_setting_informed");
        com.miui.permcenter.a.a.d("privacy_setting_main");
    }
}
