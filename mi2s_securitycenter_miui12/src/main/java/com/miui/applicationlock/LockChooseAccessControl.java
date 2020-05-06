package com.miui.applicationlock;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import com.miui.applicationlock.c.C0259c;

public class LockChooseAccessControl extends ChooseAccessControl {
    private boolean v = false;
    private boolean w = false;
    private C0259c x;

    public void finish() {
        if (!this.w) {
            setResult((!this.v || !this.x.d()) ? 0 : -1);
        }
        LockChooseAccessControl.super.finish();
    }

    public void onActivityResult(int i, int i2, Intent intent) {
        super.onActivityResult(i, i2, intent);
        if (i == 3) {
            if (i2 == -1) {
                this.v = false;
            } else {
                finish();
            }
        }
    }

    public void onBackPressed() {
        finish();
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        if (!getPackageName().equals(getCallingPackage())) {
            finish();
            return;
        }
        this.x = C0259c.b(getApplicationContext());
        String stringExtra = getIntent().getStringExtra("extra_data");
        if (!TextUtils.isEmpty(stringExtra) && stringExtra.equals("forbide")) {
            this.w = true;
        }
    }

    /* JADX WARNING: type inference failed for: r3v0, types: [android.content.Context, com.miui.applicationlock.LockChooseAccessControl, miui.app.Activity] */
    /* access modifiers changed from: protected */
    public void onStart() {
        if (!this.w && this.x.d() && this.v) {
            Intent intent = new Intent(this, ConfirmAccessControl.class);
            intent.putExtra("extra_data", "HappyCodingMain");
            startActivityForResult(intent, 3);
        }
        LockChooseAccessControl.super.onStart();
    }

    /* access modifiers changed from: protected */
    public void onStop() {
        if (!this.v) {
            this.v = true;
        }
        LockChooseAccessControl.super.onStop();
    }
}
