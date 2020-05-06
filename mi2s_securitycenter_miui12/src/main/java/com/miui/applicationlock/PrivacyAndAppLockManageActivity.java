package com.miui.applicationlock;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import b.b.c.c.a;
import b.b.c.j.B;
import com.miui.applicationlock.a.h;
import com.miui.applicationlock.c.C0259c;
import com.miui.appmanager.AppManageUtils;
import com.miui.maml.elements.AdvancedSlider;
import com.miui.privacyapps.ui.n;
import com.miui.securitycenter.R;
import miui.app.ActionBar;

public class PrivacyAndAppLockManageActivity extends a implements View.OnClickListener {

    /* renamed from: a  reason: collision with root package name */
    private ImageView f3204a;

    /* renamed from: b  reason: collision with root package name */
    private C0259c f3205b;

    /* renamed from: c  reason: collision with root package name */
    public boolean f3206c = false;
    /* access modifiers changed from: private */

    /* renamed from: d  reason: collision with root package name */
    public boolean f3207d = false;

    public void a(boolean z) {
        this.f3206c = z;
    }

    public void onActivityResult(int i, int i2, Intent intent) {
        if (i == 3) {
            if (i2 == -1) {
                this.f3206c = true;
            } else {
                finish();
            }
        }
    }

    /* JADX WARNING: type inference failed for: r2v0, types: [android.content.Context, com.miui.applicationlock.PrivacyAndAppLockManageActivity, miui.app.Activity] */
    public void onClick(View view) {
        if (view == this.f3204a) {
            Intent intent = new Intent(this, SettingLockActivity.class);
            intent.putExtra("extra_data", "ChooseAppToLock");
            startActivityForResult(intent, 3);
            h.e("settings");
        }
    }

    /* JADX WARNING: type inference failed for: r9v0, types: [b.b.c.c.a, android.content.Context, com.miui.applicationlock.PrivacyAndAppLockManageActivity, miui.app.Activity] */
    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        String stringExtra = getIntent().getStringExtra("extra_data");
        if ((bundle == null || !bundle.containsKey(AdvancedSlider.STATE)) && stringExtra.equals("not_home_start")) {
            this.f3206c = true;
        } else {
            this.f3206c = false;
        }
        this.f3205b = C0259c.b(getApplicationContext());
        if (!this.f3205b.d()) {
            this.f3206c = true;
        }
        ActionBar actionBar = getActionBar();
        actionBar.setFragmentViewPagerMode(this, getFragmentManager());
        actionBar.addFragmentTab("AppLockManageFragment", actionBar.newTab().setText(R.string.app_name).setTag(0), C0312y.class, (Bundle) null, false);
        if (AppManageUtils.a((Context) this, B.j())) {
            actionBar.addFragmentTab("PrivacyAppsManageFragment", actionBar.newTab().setText(R.string.privacy_apps).setTag(1), n.class, (Bundle) null, false);
            actionBar.addOnFragmentViewPagerChangeListener(new Da(this, actionBar));
        }
        onCustomizeActionBar(actionBar);
    }

    /* JADX WARNING: type inference failed for: r2v0, types: [android.content.Context, android.view.View$OnClickListener, com.miui.applicationlock.PrivacyAndAppLockManageActivity, miui.app.Activity] */
    /* access modifiers changed from: protected */
    public void onCustomizeActionBar(ActionBar actionBar) {
        actionBar.setDisplayOptions(16, 16);
        this.f3204a = new ImageView(this);
        this.f3204a.setContentDescription(getString(R.string.Setting_lock));
        this.f3204a.setBackgroundResource(R.drawable.applock_settings);
        this.f3204a.setOnClickListener(this);
        actionBar.setEndView(this.f3204a);
    }

    /* JADX WARNING: type inference failed for: r3v0, types: [b.b.c.c.a, android.content.Context, com.miui.applicationlock.PrivacyAndAppLockManageActivity, miui.app.Activity] */
    public void onResume() {
        super.onResume();
        if (!this.f3205b.d()) {
            finish();
        }
        if (!this.f3205b.d() || this.f3206c) {
            this.f3206c = true;
            return;
        }
        Intent intent = new Intent(this, ConfirmAccessControl.class);
        intent.putExtra("extra_data", "HappyCodingMain");
        startActivityForResult(intent, 3);
    }

    /* access modifiers changed from: protected */
    public void onSaveInstanceState(Bundle bundle) {
        PrivacyAndAppLockManageActivity.super.onSaveInstanceState(bundle);
        bundle.putBoolean(AdvancedSlider.STATE, this.f3206c);
    }

    /* access modifiers changed from: protected */
    public void onStop() {
        PrivacyAndAppLockManageActivity.super.onStop();
        if (this.f3206c) {
            this.f3206c = false;
        }
    }
}
