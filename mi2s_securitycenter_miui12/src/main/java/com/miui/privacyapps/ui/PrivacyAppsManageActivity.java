package com.miui.privacyapps.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import b.b.c.c.a;
import com.miui.applicationlock.ConfirmAccessControl;
import com.miui.applicationlock.PrivacyAppsConfirmAccessControl;
import com.miui.applicationlock.c.C0259c;
import com.miui.maml.elements.AdvancedSlider;
import com.miui.networkassistant.config.Constants;

public class PrivacyAppsManageActivity extends a {

    /* renamed from: a  reason: collision with root package name */
    private C0259c f7385a;

    /* renamed from: b  reason: collision with root package name */
    private boolean f7386b = true;

    /* renamed from: c  reason: collision with root package name */
    private boolean f7387c;

    /* renamed from: d  reason: collision with root package name */
    private final BroadcastReceiver f7388d = new c(this);

    public void finish() {
        setResult(!this.f7386b ? 0 : -1);
        PrivacyAppsManageActivity.super.finish();
    }

    /* access modifiers changed from: protected */
    public void onActivityResult(int i, int i2, Intent intent) {
        PrivacyAppsManageActivity.super.onActivityResult(i, i2, intent);
        if (i == 3) {
            setResult(i2);
            if (i2 == -1) {
                this.f7386b = true;
                return;
            }
            this.f7386b = false;
            finish();
        }
    }

    /* JADX WARNING: type inference failed for: r3v0, types: [b.b.c.c.a, android.content.Context, com.miui.privacyapps.ui.PrivacyAppsManageActivity, miui.app.Activity] */
    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        getFragmentManager().beginTransaction().replace(16908290, new n()).commit();
        if (bundle != null && bundle.containsKey(AdvancedSlider.STATE)) {
            this.f7386b = false;
        }
        this.f7385a = C0259c.b((Context) this);
        this.f7387c = getIntent().getBooleanExtra("enter_from_privacyapps_page", false);
        if (this.f7387c) {
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(Constants.System.ACTION_SCREEN_OFF);
            registerReceiver(this.f7388d, intentFilter);
        }
        setResult(-1);
    }

    /* access modifiers changed from: protected */
    public void onDestroy() {
        PrivacyAppsManageActivity.super.onDestroy();
        if (this.f7387c) {
            unregisterReceiver(this.f7388d);
        }
    }

    /* access modifiers changed from: protected */
    public void onSaveInstanceState(Bundle bundle) {
        PrivacyAppsManageActivity.super.onSaveInstanceState(bundle);
        bundle.putBoolean(AdvancedSlider.STATE, this.f7386b);
    }

    /* JADX WARNING: type inference failed for: r4v0, types: [android.content.Context, com.miui.privacyapps.ui.PrivacyAppsManageActivity, miui.app.Activity] */
    /* access modifiers changed from: protected */
    public void onStart() {
        Intent intent;
        PrivacyAppsManageActivity.super.onStart();
        if (!this.f7385a.d() || this.f7386b) {
            this.f7386b = true;
            return;
        }
        if (this.f7387c) {
            intent = new Intent(this, PrivacyAppsConfirmAccessControl.class);
        } else {
            intent = new Intent(this, ConfirmAccessControl.class);
            intent.putExtra("extra_data", "HappyCodingMain");
        }
        startActivityForResult(intent, 3);
    }

    /* access modifiers changed from: protected */
    public void onStop() {
        PrivacyAppsManageActivity.super.onStop();
        if (this.f7386b) {
            this.f7386b = false;
        }
    }
}
