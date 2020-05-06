package com.miui.permcenter.install;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import com.miui.securitycenter.R;
import miui.app.Activity;

public class DeviceManagerApplyActivity extends Activity implements View.OnClickListener {

    /* renamed from: a  reason: collision with root package name */
    private TextView f6124a;

    /* renamed from: b  reason: collision with root package name */
    private Button f6125b;
    /* access modifiers changed from: private */

    /* renamed from: c  reason: collision with root package name */
    public Button f6126c;
    /* access modifiers changed from: private */

    /* renamed from: d  reason: collision with root package name */
    public int f6127d = 1;
    /* access modifiers changed from: private */
    public int e = 5;
    /* access modifiers changed from: private */
    public Handler f = new e(this);

    private String a(int i) {
        int i2;
        if (i == 1) {
            i2 = R.string.device_manager_apply_step_1;
        } else if (i == 2) {
            i2 = R.string.device_manager_apply_step_2;
        } else if (i != 3) {
            return null;
        } else {
            i2 = R.string.device_manager_apply_step_3;
        }
        return getString(i2);
    }

    private void a() {
        try {
            Window window = getWindow();
            window.addFlags(Integer.MIN_VALUE);
            window.getDecorView().setSystemUiVisibility(768);
            window.getClass().getMethod("setNavigationBarColor", new Class[]{Integer.TYPE}).invoke(window, new Object[]{0});
        } catch (Exception unused) {
        }
    }

    static /* synthetic */ int b(DeviceManagerApplyActivity deviceManagerApplyActivity) {
        int i = deviceManagerApplyActivity.e - 1;
        deviceManagerApplyActivity.e = i;
        return i;
    }

    public void finish() {
        DeviceManagerApplyActivity.super.finish();
        overridePendingTransition(0, 0);
    }

    /* access modifiers changed from: protected */
    public void onActivityResult(int i, int i2, Intent intent) {
        DeviceManagerApplyActivity.super.onActivityResult(i, i2, intent);
    }

    public void onBackPressed() {
    }

    public void onClick(View view) {
        Button button;
        Object[] objArr;
        int i;
        int id = view.getId();
        if (id == R.id.accept) {
            int i2 = this.f6127d;
            if (i2 == 3) {
                this.f.removeMessages(100);
                setResult(-1, (Intent) null);
            } else {
                this.f6127d = i2 + 1;
                this.e = 5;
                this.f6124a.setText(a(this.f6127d));
                if (this.f6127d == 3) {
                    button = this.f6126c;
                    i = R.string.button_text_accept_timer;
                    objArr = new Object[]{Integer.valueOf(this.e)};
                } else {
                    button = this.f6126c;
                    i = R.string.button_text_next_step_timer;
                    objArr = new Object[]{Integer.valueOf(this.e)};
                }
                button.setText(getString(i, objArr));
                this.f6126c.setEnabled(false);
                this.f.removeMessages(100);
                this.f.sendEmptyMessageDelayed(100, 1000);
                return;
            }
        } else if (id == R.id.reject) {
            this.f.removeMessages(100);
            setResult(0);
        } else {
            return;
        }
        finish();
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        DeviceManagerApplyActivity.super.onCreate(bundle);
        setContentView(R.layout.pm_activity_device_manager_apply);
        if (Build.VERSION.SDK_INT != 26) {
            setRequestedOrientation(1);
        }
        a();
        this.f6124a = (TextView) findViewById(R.id.warning_info);
        this.f6125b = (Button) findViewById(R.id.reject);
        this.f6125b.setOnClickListener(this);
        this.f6126c = (Button) findViewById(R.id.accept);
        this.f6126c.setOnClickListener(this);
        this.f6124a.setText(a(this.f6127d));
        this.f6126c.setText(getString(R.string.button_text_next_step_timer, new Object[]{Integer.valueOf(this.e)}));
        this.f6126c.setEnabled(false);
        this.f.sendEmptyMessageDelayed(100, 1000);
    }

    /* access modifiers changed from: protected */
    public void onDestroy() {
        DeviceManagerApplyActivity.super.onDestroy();
        this.f.removeMessages(100);
    }
}
