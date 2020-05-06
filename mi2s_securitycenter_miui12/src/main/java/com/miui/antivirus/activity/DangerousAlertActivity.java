package com.miui.antivirus.activity;

import android.app.NotificationManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import b.b.b.a.b;
import b.b.o.g.d;
import com.miui.antivirus.model.DangerousInfo;
import com.miui.securitycenter.R;
import miui.app.AlertActivity;
import miui.util.Log;

public class DangerousAlertActivity extends AlertActivity implements DialogInterface.OnClickListener {

    /* renamed from: a  reason: collision with root package name */
    private DangerousInfo f2656a;

    /* renamed from: b  reason: collision with root package name */
    private PackageInfo f2657b;

    /* renamed from: c  reason: collision with root package name */
    private int f2658c;

    public void onClick(DialogInterface dialogInterface, int i) {
        try {
            String packageName = this.f2656a.getPackageName();
            ((NotificationManager) getSystemService("notification")).cancel(packageName, this.f2658c);
            PackageManager packageManager = getPackageManager();
            Class[] clsArr = new Class[3];
            clsArr[0] = String.class;
            clsArr[1] = Class.forName("android.content.pm.IPackageDeleteObserver");
            clsArr[2] = Integer.TYPE;
            d.a("DangerousAlertActivity", (Object) packageManager, "deletePackage", (Class<?>[]) clsArr, packageName, null, 0);
            b.a.c(packageName);
        } catch (Exception e) {
            Log.e("DangerousAlertActivity", "deletePackage", e);
        }
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        String str;
        DangerousAlertActivity.super.onCreate(bundle);
        if (Build.VERSION.SDK_INT != 26) {
            setRequestedOrientation(1);
        }
        Intent intent = getIntent();
        this.f2656a = (DangerousInfo) intent.getParcelableExtra("info");
        this.f2658c = intent.getIntExtra("notify_id", -1);
        try {
            this.f2657b = getPackageManager().getPackageInfo(this.f2656a.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException unused) {
            Log.info("DangerousAlertActivity", " NameNotFoundException : " + this.f2656a.getPackageName());
        }
        if (this.f2657b == null) {
            finish();
            return;
        }
        View inflate = getLayoutInflater().inflate(R.layout.v_dangerous_alert, (ViewGroup) null);
        TextView textView = (TextView) inflate.findViewById(R.id.msg);
        CharSequence loadLabel = this.f2657b.applicationInfo.loadLabel(getPackageManager());
        Resources resources = getResources();
        String msg = this.f2656a.getMsg();
        if (TextUtils.isEmpty(msg)) {
            str = resources.getString(R.string.uninstall_danagerous_msg, new Object[]{loadLabel});
        } else {
            str = "\"" + loadLabel + "\" " + msg;
        }
        textView.setText(str);
        Object a2 = d.a("DangerousAlertActivity", (Object) this, (Class<?>) AlertActivity.class, "mAlertParams");
        d.a("DangerousAlertActivity", a2, "mView", (Object) inflate);
        d.a("DangerousAlertActivity", a2, "mPositiveButtonText", (Object) getString(R.string.uninstall_danagerous_btn));
        d.a("DangerousAlertActivity", a2, "mNegativeButtonText", (Object) getString(R.string.continue_install));
        d.a("DangerousAlertActivity", a2, "mPositiveButtonListener", (Object) this);
        setupAlert();
        d.a("DangerousAlertActivity", (Object) getWindow(), "setCloseOnTouchOutside", (Class<?>[]) new Class[]{Boolean.TYPE}, false);
    }

    /* access modifiers changed from: protected */
    public void onPause() {
        DangerousAlertActivity.super.onPause();
    }

    /* access modifiers changed from: protected */
    public void onResume() {
        DangerousAlertActivity.super.onResume();
    }
}
