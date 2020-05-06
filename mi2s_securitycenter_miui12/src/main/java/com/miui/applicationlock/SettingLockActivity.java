package com.miui.applicationlock;

import android.content.Intent;
import android.os.Bundle;
import miui.app.Activity;

public class SettingLockActivity extends Activity {

    /* renamed from: a  reason: collision with root package name */
    private bb f3215a;

    public interface a {
        void onWindowFocusChanged(boolean z);
    }

    public void finish() {
        bb bbVar = this.f3215a;
        if (bbVar != null) {
            setResult(!bbVar.j ? 0 : -1);
        }
        SettingLockActivity.super.finish();
    }

    public void onBackPressed() {
        bb bbVar = this.f3215a;
        if (bbVar != null) {
            bbVar.c();
        }
        SettingLockActivity.super.onBackPressed();
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        SettingLockActivity.super.onCreate(bundle);
        if (bundle == null) {
            if (this.f3215a == null) {
                this.f3215a = new bb();
            }
            getFragmentManager().beginTransaction().replace(16908290, this.f3215a).commit();
        }
        setResult(-1, new Intent());
    }

    /* access modifiers changed from: protected */
    public void onRestart() {
        SettingLockActivity.super.onRestart();
        bb bbVar = this.f3215a;
        if (bbVar != null) {
            bbVar.a();
        }
    }

    public void onWindowFocusChanged(boolean z) {
        SettingLockActivity.super.onWindowFocusChanged(z);
        bb bbVar = this.f3215a;
        if (bbVar != null && (bbVar instanceof a)) {
            bbVar.onWindowFocusChanged(z);
        }
    }
}
