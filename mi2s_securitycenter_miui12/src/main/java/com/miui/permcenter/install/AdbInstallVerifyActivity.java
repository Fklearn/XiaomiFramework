package com.miui.permcenter.install;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import b.b.c.h.j;
import b.b.o.g.e;
import com.miui.securitycenter.R;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import miui.app.AlertActivity;
import miui.os.Build;
import org.json.JSONObject;

public class AdbInstallVerifyActivity extends AlertActivity {

    /* renamed from: a  reason: collision with root package name */
    private static final String f6119a = (Build.IS_INTERNATIONAL_BUILD ? "https://srv.sec.intl.miui.com/data/adb" : "https://srv.sec.miui.com/data/adb");

    /* renamed from: b  reason: collision with root package name */
    protected String f6120b;

    /* renamed from: c  reason: collision with root package name */
    private boolean f6121c;

    /* renamed from: d  reason: collision with root package name */
    protected String f6122d = f6119a;
    protected a e;

    static class a extends AsyncTask<Void, Void, String> {

        /* renamed from: a  reason: collision with root package name */
        private WeakReference<AdbInstallVerifyActivity> f6123a;

        a(AdbInstallVerifyActivity adbInstallVerifyActivity) {
            this.f6123a = new WeakReference<>(adbInstallVerifyActivity);
        }

        /* access modifiers changed from: protected */
        /* renamed from: a */
        public String doInBackground(Void... voidArr) {
            AdbInstallVerifyActivity adbInstallVerifyActivity;
            if (isCancelled() || (adbInstallVerifyActivity = (AdbInstallVerifyActivity) this.f6123a.get()) == null) {
                return null;
            }
            try {
                HashMap hashMap = new HashMap();
                hashMap.put("xiaomi_id", TextUtils.isEmpty(adbInstallVerifyActivity.f6120b) ? "" : adbInstallVerifyActivity.f6120b);
                hashMap.put("time", String.valueOf(System.currentTimeMillis()));
                JSONObject jSONObject = new JSONObject(f.a(adbInstallVerifyActivity.getApplicationContext(), hashMap));
                JSONObject jSONObject2 = new JSONObject();
                jSONObject2.put("A", jSONObject);
                String a2 = f.a(adbInstallVerifyActivity.f6122d, new o().a(jSONObject2.toString()), new j("permcenter_adbinstallverifyactivity"));
                if (!TextUtils.isEmpty(a2)) {
                    JSONObject jSONObject3 = new JSONObject(a2);
                    if (jSONObject3.optInt("status", -1) == 0) {
                        return null;
                    }
                    return jSONObject3.optString("message");
                }
            } catch (Exception e) {
                Log.e("AdbInstallActivity", "request error", e);
            }
            return adbInstallVerifyActivity.getString(R.string.adb_install_open_error);
        }

        /* access modifiers changed from: protected */
        /* renamed from: a */
        public void onPostExecute(String str) {
            AdbInstallVerifyActivity adbInstallVerifyActivity = (AdbInstallVerifyActivity) this.f6123a.get();
            if (adbInstallVerifyActivity != null) {
                if (str == null) {
                    adbInstallVerifyActivity.c();
                } else if (!TextUtils.isEmpty(str)) {
                    Toast.makeText(adbInstallVerifyActivity.getApplicationContext(), str, 1).show();
                }
                adbInstallVerifyActivity.finish();
            }
        }
    }

    private void a(View view) {
        try {
            Object a2 = e.a((Object) this, (Class<?>) AlertActivity.class, "mAlertParams");
            e.a(a2, (Class<?>) a2.getClass().getSuperclass(), "mView", (Object) view);
        } catch (Exception e2) {
            Log.e("AdbInstallActivity", "setAlertParams error", e2);
        }
    }

    /* JADX WARNING: type inference failed for: r1v0, types: [android.content.Context, com.miui.permcenter.install.AdbInstallVerifyActivity] */
    private void a(boolean z) {
        d.a((Context) this).a(z);
    }

    private void d() {
        NetworkInfo activeNetworkInfo = ((ConnectivityManager) getSystemService("connectivity")).getActiveNetworkInfo();
        if (activeNetworkInfo == null || !activeNetworkInfo.isConnected()) {
            Toast.makeText(getApplicationContext(), R.string.adb_install_no_network, 1).show();
            finish();
            return;
        }
        this.e = new a(this);
        this.e.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);
    }

    /* JADX WARNING: type inference failed for: r2v0, types: [android.content.Context, miui.app.AlertActivity, com.miui.permcenter.install.AdbInstallVerifyActivity] */
    /* access modifiers changed from: protected */
    public void a() {
        a(View.inflate(this, R.layout.adb_install_progress, (ViewGroup) null));
        setupAlert();
        d();
    }

    /* JADX WARNING: type inference failed for: r3v0, types: [miui.app.AlertActivity, android.content.Context, com.miui.permcenter.install.AdbInstallVerifyActivity, android.app.Activity] */
    /* access modifiers changed from: protected */
    public void b() {
        this.f6121c = getIntent().getBooleanExtra("is_input", false);
        if (this.f6121c || !TextUtils.isEmpty(this.f6120b)) {
            a();
            return;
        }
        Toast.makeText(this, R.string.adb_login_xiaomi_account, 1).show();
        q.a(this, new Bundle());
        finish();
    }

    /* access modifiers changed from: protected */
    public void c() {
        if (this.f6121c) {
            AdbInputApplyActivity.a(true);
        } else {
            a(true);
        }
    }

    /* JADX WARNING: type inference failed for: r1v0, types: [miui.app.AlertActivity, android.content.Context, com.miui.permcenter.install.AdbInstallVerifyActivity] */
    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        AdbInstallVerifyActivity.super.onCreate(bundle);
        if (Build.VERSION.SDK_INT != 26) {
            setRequestedOrientation(1);
        }
        this.f6120b = q.a(this);
        b();
    }

    /* access modifiers changed from: protected */
    public void onDestroy() {
        AdbInstallVerifyActivity.super.onDestroy();
        a aVar = this.e;
        if (aVar != null) {
            aVar.cancel(true);
        }
    }
}
