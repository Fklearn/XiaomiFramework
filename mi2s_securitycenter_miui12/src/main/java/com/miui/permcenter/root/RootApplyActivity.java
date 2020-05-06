package com.miui.permcenter.root;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import b.b.c.c.a;
import b.b.c.j.x;
import com.miui.permission.PermissionManager;
import com.miui.securitycenter.R;

public class RootApplyActivity extends a implements View.OnClickListener {

    /* renamed from: a  reason: collision with root package name */
    private TextView f6488a;

    /* renamed from: b  reason: collision with root package name */
    private Button f6489b;
    /* access modifiers changed from: private */

    /* renamed from: c  reason: collision with root package name */
    public Button f6490c;

    /* renamed from: d  reason: collision with root package name */
    private String f6491d;
    private CharSequence e;
    /* access modifiers changed from: private */
    public int f = 1;
    /* access modifiers changed from: private */
    public int g = 5;
    /* access modifiers changed from: private */
    public Handler h = new a(this);

    private String a(int i, CharSequence charSequence) {
        int i2;
        Object[] objArr;
        if (i == 1) {
            i2 = R.string.root_apply_step_1;
            objArr = new Object[]{charSequence};
        } else if (i == 2) {
            i2 = R.string.root_apply_step_2;
            objArr = new Object[]{charSequence};
        } else if (i == 3) {
            i2 = R.string.root_apply_step_3;
            objArr = new Object[]{charSequence};
        } else if (i == 4) {
            i2 = R.string.root_apply_step_4;
            objArr = new Object[]{charSequence};
        } else if (i != 5) {
            return null;
        } else {
            i2 = R.string.root_apply_step_5;
            objArr = new Object[]{charSequence};
        }
        return getString(i2, objArr);
    }

    static /* synthetic */ int b(RootApplyActivity rootApplyActivity) {
        int i = rootApplyActivity.g - 1;
        rootApplyActivity.g = i;
        return i;
    }

    /* JADX WARNING: type inference failed for: r7v0, types: [android.content.Context, miui.app.Activity, com.miui.permcenter.root.RootApplyActivity] */
    private void l() {
        PermissionManager.getInstance(this).setApplicationPermission(512, 3, this.f6491d);
        Toast.makeText(this, getString(R.string.toast_root_apply_accept, new Object[]{this.e}), 0).show();
    }

    /* JADX WARNING: type inference failed for: r7v0, types: [android.content.Context, miui.app.Activity, com.miui.permcenter.root.RootApplyActivity] */
    private void m() {
        PermissionManager.getInstance(this).setApplicationPermission(512, 1, this.f6491d);
        Toast.makeText(this, getString(R.string.toast_root_apply_reject, new Object[]{this.e}), 0).show();
    }

    public void finish() {
        setResult(-1, (Intent) null);
        RootApplyActivity.super.finish();
        overridePendingTransition(0, 0);
    }

    public void onBackPressed() {
    }

    public void onClick(View view) {
        Button button;
        Object[] objArr;
        int i;
        int id = view.getId();
        if (id == R.id.accept) {
            int i2 = this.f;
            if (i2 == 5) {
                this.h.removeMessages(100);
                l();
            } else {
                this.f = i2 + 1;
                this.g = 5;
                this.f6488a.setText(a(this.f, this.e));
                if (this.f == 5) {
                    button = this.f6490c;
                    i = R.string.button_text_accept_timer;
                    objArr = new Object[]{Integer.valueOf(this.g)};
                } else {
                    button = this.f6490c;
                    i = R.string.button_text_next_step_timer;
                    objArr = new Object[]{Integer.valueOf(this.g)};
                }
                button.setText(getString(i, objArr));
                this.f6490c.setEnabled(false);
                this.h.removeMessages(100);
                this.h.sendEmptyMessageDelayed(100, 1000);
                return;
            }
        } else if (id == R.id.reject) {
            this.h.removeMessages(100);
            m();
        } else {
            return;
        }
        finish();
    }

    /* JADX WARNING: type inference failed for: r4v0, types: [b.b.c.c.a, android.content.Context, android.view.View$OnClickListener, miui.app.Activity, com.miui.permcenter.root.RootApplyActivity] */
    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.pm_activity_root_apply);
        if (Build.VERSION.SDK_INT != 26) {
            setRequestedOrientation(1);
        }
        this.f6491d = getIntent().getStringExtra("extra_pkgname");
        if (TextUtils.isEmpty(this.f6491d)) {
            finish();
            return;
        }
        this.e = x.j(this, this.f6491d);
        this.f6488a = (TextView) findViewById(R.id.warning_info);
        this.f6489b = (Button) findViewById(R.id.reject);
        this.f6489b.setOnClickListener(this);
        this.f6490c = (Button) findViewById(R.id.accept);
        this.f6490c.setOnClickListener(this);
        this.f6488a.setText(a(this.f, this.e));
        this.f6490c.setText(getString(R.string.button_text_next_step_timer, new Object[]{Integer.valueOf(this.g)}));
        this.f6490c.setEnabled(false);
        this.h.sendEmptyMessageDelayed(100, 1000);
    }

    /* access modifiers changed from: protected */
    public void onDestroy() {
        this.h.removeMessages(100);
        RootApplyActivity.super.onDestroy();
    }
}
