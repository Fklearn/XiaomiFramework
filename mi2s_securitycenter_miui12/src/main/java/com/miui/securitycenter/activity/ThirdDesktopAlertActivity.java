package com.miui.securitycenter.activity;

import android.os.Bundle;
import com.miui.securitycenter.R;
import miui.app.Activity;
import miui.app.AlertDialog;

public class ThirdDesktopAlertActivity extends Activity {
    /* JADX WARNING: type inference failed for: r2v0, types: [com.miui.securitycenter.activity.ThirdDesktopAlertActivity, android.content.Context, miui.app.Activity] */
    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        ThirdDesktopAlertActivity.super.onCreate(bundle);
        new AlertDialog.Builder(this).setTitle(R.string.third_desktop_dialog_title).setMessage(R.string.third_desktop_dialog_content).setPositiveButton(R.string.third_desktop_dialog_ok, new b(this)).setOnCancelListener(new a(this)).show();
    }
}
