package com.miui.applicationlock;

import android.content.Intent;
import android.os.Bundle;

public class ResetChooseAccessControl extends ChooseAccessControl {
    public void onActivityResult(int i, int i2, Intent intent) {
        super.onActivityResult(i, i2, intent);
    }

    public void onBackPressed() {
        finish();
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
    }

    public void onSaveInstanceState(Bundle bundle) {
        ResetChooseAccessControl.super.onSaveInstanceState(bundle);
    }

    /* JADX WARNING: type inference failed for: r4v0, types: [android.content.Context, miui.app.Activity, com.miui.applicationlock.ResetChooseAccessControl] */
    /* access modifiers changed from: protected */
    public void onStop() {
        ResetChooseAccessControl.super.onStop();
        if (!getIntent().getBooleanExtra("forgot_password_reset", false)) {
            Intent intent = new Intent(this, ChooseLockTypeActivity.class);
            intent.putExtra("home_cancel_current_pwd_page", true);
            setResult(0, intent);
            finish();
        }
    }
}
