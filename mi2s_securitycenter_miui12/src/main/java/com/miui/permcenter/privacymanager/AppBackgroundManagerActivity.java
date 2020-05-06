package com.miui.permcenter.privacymanager;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageDeleteObserver;
import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.UserHandle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import b.b.c.j.C;
import b.b.c.j.x;
import com.miui.securitycenter.R;
import java.lang.ref.WeakReference;
import miui.app.Activity;
import miui.app.AlertDialog;

public class AppBackgroundManagerActivity extends Activity {

    /* renamed from: a  reason: collision with root package name */
    private String f6309a;

    /* renamed from: b  reason: collision with root package name */
    private CharSequence f6310b;

    /* renamed from: c  reason: collision with root package name */
    private ApplicationInfo f6311c;

    /* renamed from: d  reason: collision with root package name */
    private PackageInfo f6312d;
    private AlertDialog e;
    private int f;
    private boolean g;
    /* access modifiers changed from: private */
    public Handler h;
    private b i;
    private TextView j;
    private CheckBox k;

    private static class a implements DialogInterface.OnClickListener {

        /* renamed from: a  reason: collision with root package name */
        private WeakReference<AppBackgroundManagerActivity> f6313a;

        /* renamed from: b  reason: collision with root package name */
        private boolean f6314b;

        public a(AppBackgroundManagerActivity appBackgroundManagerActivity, boolean z) {
            this.f6313a = new WeakReference<>(appBackgroundManagerActivity);
            this.f6314b = z;
        }

        public void onClick(DialogInterface dialogInterface, int i) {
            AppBackgroundManagerActivity appBackgroundManagerActivity = (AppBackgroundManagerActivity) this.f6313a.get();
            if (appBackgroundManagerActivity != null && !appBackgroundManagerActivity.isFinishing() && !appBackgroundManagerActivity.isDestroyed()) {
                if (this.f6314b) {
                    appBackgroundManagerActivity.c();
                } else {
                    appBackgroundManagerActivity.b();
                }
            }
        }
    }

    private static class b extends IPackageDeleteObserver.Stub {
        /* access modifiers changed from: private */

        /* renamed from: a  reason: collision with root package name */
        public Context f6315a;

        /* renamed from: b  reason: collision with root package name */
        private WeakReference<AppBackgroundManagerActivity> f6316b;

        public b(AppBackgroundManagerActivity appBackgroundManagerActivity) {
            this.f6315a = appBackgroundManagerActivity.getApplicationContext();
            this.f6316b = new WeakReference<>(appBackgroundManagerActivity);
        }

        public void packageDeleted(String str, int i) {
            AppBackgroundManagerActivity appBackgroundManagerActivity;
            if (i == 1 && (appBackgroundManagerActivity = (AppBackgroundManagerActivity) this.f6316b.get()) != null) {
                appBackgroundManagerActivity.h.post(new b(this, appBackgroundManagerActivity));
            }
        }
    }

    private void a() {
        AlertDialog alertDialog = this.e;
        if (alertDialog != null) {
            alertDialog.dismiss();
        }
    }

    /* access modifiers changed from: private */
    public void b() {
        a();
        finish();
    }

    /* JADX WARNING: type inference failed for: r1v0, types: [android.content.Context, com.miui.permcenter.privacymanager.AppBackgroundManagerActivity] */
    /* access modifiers changed from: private */
    public void c() {
        x.b((Context) this, this.f6312d.packageName);
        if (!this.k.isChecked()) {
            b();
        }
    }

    /* JADX WARNING: type inference failed for: r7v0, types: [android.content.Context, miui.app.Activity, com.miui.permcenter.privacymanager.AppBackgroundManagerActivity] */
    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        AppBackgroundManagerActivity.super.onCreate(bundle);
        Intent intent = getIntent();
        this.f6309a = intent.getData() != null ? intent.getData().getSchemeSpecificPart() : null;
        this.f = UserHandle.myUserId();
        this.f6312d = b.b.o.b.a.a.a(this.f6309a, 128, this.f);
        PackageInfo packageInfo = this.f6312d;
        if (packageInfo == null) {
            finish();
            return;
        }
        this.f6311c = packageInfo.applicationInfo;
        this.f6310b = this.f6311c.loadLabel(getPackageManager());
        this.g = C.b(this.f);
        this.h = new Handler();
        this.i = new b(this);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.app_running_tip_dialog_title));
        View inflate = getLayoutInflater().inflate(R.layout.dialog_background_app_manager, (ViewGroup) null);
        this.j = (TextView) inflate.findViewById(R.id.background_running_content);
        this.k = (CheckBox) inflate.findViewById(R.id.background_running_uninstall);
        this.j.setText(getString(R.string.app_running_tip_dialog_text, new Object[]{this.f6310b}));
        builder.setView(inflate);
        builder.setPositiveButton(R.string.app_running_tip_dialog_positive, new a(this, true));
        builder.setNegativeButton(R.string.app_running_tip_dialog_negative, new a(this, false));
        builder.setCancelable(false);
        this.e = builder.create();
    }

    /* access modifiers changed from: protected */
    public void onDestroy() {
        AppBackgroundManagerActivity.super.onDestroy();
        a();
    }

    /* access modifiers changed from: protected */
    public void onResume() {
        AppBackgroundManagerActivity.super.onResume();
        AlertDialog alertDialog = this.e;
        if (alertDialog != null) {
            alertDialog.show();
        }
    }
}
