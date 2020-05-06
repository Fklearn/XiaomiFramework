package com.miui.permcenter.install;

import android.app.KeyguardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.IMessenger;
import android.os.Message;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import b.b.o.g.e;
import com.google.android.exoplayer2.extractor.ts.PsExtractor;
import com.miui.networkassistant.provider.ProviderConstant;
import com.miui.permcenter.a.a;
import com.miui.permcenter.compact.AppOpsUtilsCompat;
import com.miui.securitycenter.R;
import miui.app.AlertActivity;

public class AdbInstallActivity extends AlertActivity implements DialogInterface.OnClickListener {

    /* renamed from: a  reason: collision with root package name */
    private int f6115a = 0;

    /* renamed from: b  reason: collision with root package name */
    private String f6116b;

    /* renamed from: c  reason: collision with root package name */
    private IMessenger f6117c;

    /* renamed from: d  reason: collision with root package name */
    private h f6118d;
    private d e;
    private CheckBox f;
    private ImageView g;
    private Drawable h;
    private TextView i;
    private Object j;
    private Button k;
    /* access modifiers changed from: private */
    public int l = 10;
    /* access modifiers changed from: private */
    public Handler m = new b(this);

    /* access modifiers changed from: private */
    public void a() {
        boolean isKeyguardLocked = ((KeyguardManager) getSystemService("keyguard")).isKeyguardLocked();
        if (isKeyguardLocked) {
            this.e.a(this.f6118d);
            this.e.f(this.f6118d.b());
            a.c(this.f6118d.c());
        }
        a(isKeyguardLocked);
        finish();
    }

    private void a(View view) {
        this.f = (CheckBox) view.findViewById(R.id.do_not_ask_checkbox);
        this.g = (ImageView) view.findViewById(R.id.icon);
        this.i = (TextView) view.findViewById(R.id.name);
        Class[] clsArr = {Integer.TYPE};
        try {
            Object obj = this.j;
            this.k = (Button) e.a(obj, "getButton", Class.forName("com.android.internal.app.AlertController"), (Class<?>[]) clsArr, -1);
        } catch (Exception e2) {
            Log.e("AdbInstallActivity", "initViews", e2);
        }
    }

    private void a(boolean z) {
        if (this.f.isChecked()) {
            this.f6118d.a(0);
            this.e.a(this.f6118d, this.h);
        }
        if (z || this.f.isChecked()) {
            this.e.i();
        }
    }

    private boolean a(String str) {
        try {
            getPackageManager().getPackageInfo(str, 0);
            return true;
        } catch (PackageManager.NameNotFoundException unused) {
            return false;
        }
    }

    /* access modifiers changed from: private */
    public void b() {
        Button button = this.k;
        if (button != null) {
            button.setText(getString(R.string.reject_countdown, new Object[]{Integer.valueOf(this.l)}));
        }
    }

    private void b(View view) {
        try {
            Object a2 = e.a((Object) this, (Class<?>) AlertActivity.class, "mAlertParams");
            this.j = e.a((Object) this, (Class<?>) AlertActivity.class, "mAlert");
            Class<? super Object> superclass = a2.getClass().getSuperclass();
            e.a(a2, (Class<?>) superclass, "mTitle", (Object) getString(R.string.install_title));
            e.a(a2, (Class<?>) superclass, "mView", (Object) view);
            e.a(a2, (Class<?>) superclass, "mNegativeButtonText", (Object) getString(R.string.continue_install));
            e.a(a2, (Class<?>) superclass, "mPositiveButtonText", (Object) getString(R.string.reject_countdown, new Object[]{Integer.valueOf(this.l)}));
            e.a(a2, (Class<?>) superclass, "mPositiveButtonListener", (Object) this);
            e.a(a2, (Class<?>) superclass, "mNegativeButtonListener", (Object) this);
        } catch (Exception e2) {
            Log.e("AdbInstallActivity", "setAlertParams", e2);
        }
    }

    static /* synthetic */ int c(AdbInstallActivity adbInstallActivity) {
        int i2 = adbInstallActivity.l;
        adbInstallActivity.l = i2 - 1;
        return i2;
    }

    public void finish() {
        AdbInstallActivity.super.finish();
        this.m.removeMessages(10);
    }

    public void onClick(DialogInterface dialogInterface, int i2) {
        if (i2 == -2) {
            a.a(this.f6118d.c(), false, this.f.isChecked());
            this.f6115a = -1;
        } else if (i2 == -1) {
            this.f6115a = 0;
            a(false);
            a.a(this.f6118d.c(), true, this.f.isChecked());
        }
    }

    /* JADX WARNING: type inference failed for: r13v0, types: [com.miui.permcenter.install.AdbInstallActivity, miui.app.AlertActivity, android.content.Context] */
    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        IBinder iBinder;
        AdbInstallActivity.super.onCreate(bundle);
        if (Build.VERSION.SDK_INT != 26) {
            setRequestedOrientation(1);
        }
        Intent intent = getIntent();
        Uri data = intent.getData();
        try {
            iBinder = (IBinder) e.a((Object) intent, "getIBinderExtra", (Class<?>[]) new Class[]{String.class}, "observer");
        } catch (Exception e2) {
            e2.printStackTrace();
            iBinder = null;
        }
        if (data == null || iBinder == null) {
            finish();
            return;
        }
        this.f6117c = IMessenger.Stub.asInterface(iBinder);
        this.e = d.a((Context) this);
        String path = data.getPath();
        PackageManager packageManager = getPackageManager();
        Bundle bundle2 = new Bundle();
        bundle2.putInt("flags", 128);
        Bundle call = getContentResolver().call(Uri.parse("content://guard"), "parseApk", path, bundle2);
        if (call == null) {
            Log.d("AdbInstallActivity", " parsePackage is null , path ：" + path);
            this.f6116b = "Failure [Invalid apk]";
            if (AppOpsUtilsCompat.isXOptMode() && this.e.e()) {
                this.f6115a = -1;
            }
            finish();
            return;
        }
        PackageInfo packageInfo = (PackageInfo) call.getParcelable("pkgInfo");
        String string = call.getString("label");
        Bitmap bitmap = (Bitmap) call.getParcelable(ProviderConstant.DataUsageNotiStatusColumns.COLUMN_ICON);
        this.h = bitmap == null ? packageManager.getDefaultActivityIcon() : new BitmapDrawable(getResources(), bitmap);
        String str = packageInfo.packageName;
        if (TextUtils.isEmpty(str)) {
            finish();
            return;
        }
        if (this.e.b(str)) {
            Bundle bundle3 = new Bundle();
            bundle3.putInt("flags", PsExtractor.AUDIO_STREAM);
            Bundle call2 = getContentResolver().call(Uri.parse("content://guard"), "parseApk", path, bundle3);
            if (call2 != null) {
                if (this.e.a((PackageInfo) call2.getParcelable("pkgInfo"))) {
                    this.f6115a = -1;
                    finish();
                    return;
                }
            }
        }
        if (!this.e.e()) {
            this.e.a(string);
            finish();
        } else if (!this.e.f()) {
            this.f6115a = -1;
            finish();
        } else {
            String scheme = data.getScheme();
            if (scheme == null || !"file".equals(scheme)) {
                finish();
            } else if (a(str)) {
                this.f6115a = -1;
                finish();
            } else {
                h d2 = this.e.d(str);
                this.f6118d = new h();
                this.f6118d.b(str);
                this.f6118d.a(string.toString());
                if (d2 == null || d2.a() == 1) {
                    View inflate = getLayoutInflater().inflate(R.layout.pm_adb_install_alert, (ViewGroup) null);
                    b(inflate);
                    setupAlert();
                    a(inflate);
                    this.g.setImageDrawable(this.h);
                    this.i.setText(string);
                    b();
                    this.m.sendEmptyMessageDelayed(10, 1600);
                    return;
                }
                if (!((KeyguardManager) getSystemService("keyguard")).isKeyguardLocked()) {
                    this.e.g(string);
                }
                finish();
                a.b(this.f6118d.c());
            }
        }
    }

    /* access modifiers changed from: protected */
    public void onDestroy() {
        AdbInstallActivity.super.onDestroy();
        this.m.removeMessages(10);
        try {
            if (this.f6117c != null) {
                Message message = new Message();
                message.what = this.f6115a;
                if (this.f6116b != null) {
                    Bundle bundle = new Bundle();
                    bundle.putString("msg", this.f6116b);
                    message.setData(bundle);
                }
                this.f6117c.send(message);
            }
        } catch (RemoteException unused) {
        }
    }
}
