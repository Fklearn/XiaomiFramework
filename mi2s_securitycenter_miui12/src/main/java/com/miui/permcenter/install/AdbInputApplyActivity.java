package com.miui.permcenter.install;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import com.miui.activityutil.o;
import com.miui.permcenter.compact.SystemPropertiesCompat;
import com.miui.securitycenter.R;
import miui.app.Activity;

public class AdbInputApplyActivity extends Activity implements View.OnClickListener {

    /* renamed from: a  reason: collision with root package name */
    private TextView f6111a;

    /* renamed from: b  reason: collision with root package name */
    private Button f6112b;
    /* access modifiers changed from: private */

    /* renamed from: c  reason: collision with root package name */
    public Button f6113c;
    /* access modifiers changed from: private */

    /* renamed from: d  reason: collision with root package name */
    public int f6114d = 1;
    /* access modifiers changed from: private */
    public int e = 5;
    /* access modifiers changed from: private */
    public Handler f = new a(this);

    private String a(int i) {
        int i2;
        if (i == 1) {
            i2 = R.string.usb_adb_input_apply_step_1;
        } else if (i == 2) {
            i2 = R.string.usb_adb_input_apply_step_2;
        } else if (i != 3) {
            return null;
        } else {
            i2 = R.string.usb_adb_input_apply_step_3;
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

    public static void a(boolean z) {
        SystemPropertiesCompat.set("persist.security.adbinput", z ? o.f2310b : o.f2309a);
    }

    static /* synthetic */ int b(AdbInputApplyActivity adbInputApplyActivity) {
        int i = adbInputApplyActivity.e - 1;
        adbInputApplyActivity.e = i;
        return i;
    }

    public void finish() {
        setResult(-1, (Intent) null);
        AdbInputApplyActivity.super.finish();
        overridePendingTransition(0, 0);
    }

    /* access modifiers changed from: protected */
    public void onActivityResult(int i, int i2, Intent intent) {
        AdbInputApplyActivity.super.onActivityResult(i, i2, intent);
        if (i == 3) {
            finish();
        }
    }

    public void onBackPressed() {
    }

    /* JADX WARNING: type inference failed for: r5v0, types: [android.content.Context, miui.app.Activity, com.miui.permcenter.install.AdbInputApplyActivity] */
    public void onClick(View view) {
        Button button;
        Object[] objArr;
        int i;
        int id = view.getId();
        if (id == R.id.accept) {
            int i2 = this.f6114d;
            if (i2 == 3) {
                this.f.removeMessages(100);
                Intent intent = new Intent(this, AdbInstallVerifyActivity.class);
                intent.putExtra("is_input", true);
                startActivityForResult(intent, 3);
                return;
            }
            this.f6114d = i2 + 1;
            this.e = 5;
            this.f6111a.setText(a(this.f6114d));
            if (this.f6114d == 3) {
                button = this.f6113c;
                i = R.string.button_text_accept_timer;
                objArr = new Object[]{Integer.valueOf(this.e)};
            } else {
                button = this.f6113c;
                i = R.string.button_text_next_step_timer;
                objArr = new Object[]{Integer.valueOf(this.e)};
            }
            button.setText(getString(i, objArr));
            this.f6113c.setEnabled(false);
            this.f.removeMessages(100);
            this.f.sendEmptyMessageDelayed(100, 1000);
        } else if (id == R.id.reject) {
            this.f.removeMessages(100);
            a(false);
            finish();
        }
    }

    /* JADX WARNING: type inference failed for: r4v0, types: [android.content.Context, android.view.View$OnClickListener, miui.app.Activity, com.miui.permcenter.install.AdbInputApplyActivity] */
    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        AdbInputApplyActivity.super.onCreate(bundle);
        if (TextUtils.isEmpty(q.a(this))) {
            Intent intent = new Intent(this, AdbInstallVerifyActivity.class);
            intent.putExtra("is_input", true);
            startActivityForResult(intent, 3);
            return;
        }
        setContentView(R.layout.pm_activity_root_apply);
        if (Build.VERSION.SDK_INT != 26) {
            setRequestedOrientation(1);
        }
        a();
        this.f6111a = (TextView) findViewById(R.id.warning_info);
        this.f6112b = (Button) findViewById(R.id.reject);
        this.f6112b.setOnClickListener(this);
        this.f6113c = (Button) findViewById(R.id.accept);
        this.f6113c.setOnClickListener(this);
        this.f6111a.setText(a(this.f6114d));
        this.f6113c.setText(getString(R.string.button_text_next_step_timer, new Object[]{Integer.valueOf(this.e)}));
        this.f6113c.setEnabled(false);
        this.f.sendEmptyMessageDelayed(100, 1000);
    }

    /* access modifiers changed from: protected */
    public void onDestroy() {
        AdbInputApplyActivity.super.onDestroy();
        this.f.removeMessages(100);
    }
}
