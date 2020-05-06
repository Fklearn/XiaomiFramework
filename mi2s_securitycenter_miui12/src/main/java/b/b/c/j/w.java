package b.b.c.j;

import android.content.ComponentName;
import android.content.Context;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;
import com.xiaomi.market.IAppDownloadManager;
import org.json.JSONObject;

class w implements ServiceConnection {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ String f1767a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ String f1768b;

    /* renamed from: c  reason: collision with root package name */
    final /* synthetic */ String f1769c;

    /* renamed from: d  reason: collision with root package name */
    final /* synthetic */ String f1770d;
    final /* synthetic */ Context e;
    final /* synthetic */ String f;
    final /* synthetic */ String g;
    final /* synthetic */ String h;

    w(String str, String str2, String str3, String str4, Context context, String str5, String str6, String str7) {
        this.f1767a = str;
        this.f1768b = str2;
        this.f1769c = str3;
        this.f1770d = str4;
        this.e = context;
        this.f = str5;
        this.g = str6;
        this.h = str7;
    }

    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        try {
            IAppDownloadManager a2 = IAppDownloadManager.Stub.a(iBinder);
            Bundle bundle = new Bundle();
            bundle.putString("packageName", this.f1767a);
            bundle.putString("ref", this.f1768b);
            JSONObject jSONObject = new JSONObject();
            if (!TextUtils.isEmpty(this.f1769c)) {
                jSONObject.put("ext_apkChannel", this.f1769c);
            }
            jSONObject.put("ext_passback", this.f1770d);
            bundle.putString("extra_query_params", jSONObject.toString());
            bundle.putString("senderPackageName", this.e.getPackageName());
            bundle.putBoolean("show_cta", true);
            bundle.putString("appClientId", this.f);
            bundle.putString("appSignature", this.g);
            bundle.putString("nonce", this.h);
            a2.a(bundle);
        } catch (Exception e2) {
            Log.e("PackageUtils", "startAppDownloadNew exception", e2);
        } catch (Throwable th) {
            this.e.unbindService(this);
            throw th;
        }
        this.e.unbindService(this);
    }

    public void onServiceDisconnected(ComponentName componentName) {
    }
}
