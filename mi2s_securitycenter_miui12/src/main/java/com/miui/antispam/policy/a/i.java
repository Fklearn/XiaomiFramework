package com.miui.antispam.policy.a;

import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.RemoteException;
import android.util.Log;
import com.miui.guardprovider.aidl.IURLScanServer;
import java.util.concurrent.CountDownLatch;

public class i {

    /* renamed from: a  reason: collision with root package name */
    public static String f2381a = "URLFilterManager";

    /* renamed from: b  reason: collision with root package name */
    private Context f2382b;
    /* access modifiers changed from: private */

    /* renamed from: c  reason: collision with root package name */
    public CountDownLatch f2383c;
    /* access modifiers changed from: private */

    /* renamed from: d  reason: collision with root package name */
    public IURLScanServer f2384d;
    private ServiceConnection e = new h(this);

    public i(Context context) {
        this.f2382b = context.getApplicationContext();
        this.f2383c = new CountDownLatch(1);
        b();
        try {
            this.f2383c.await();
        } catch (InterruptedException e2) {
            Log.e(f2381a, "exception when create URLFilterManager: ", e2);
        }
    }

    private void b() {
        Intent intent = new Intent("com.miui.guardprovider.action.urlscan");
        intent.setPackage("com.miui.guardprovider");
        this.f2382b.bindService(intent, this.e, 1);
    }

    private void c() {
        if (this.f2384d != null) {
            this.f2384d = null;
            this.f2382b.unbindService(this.e);
            Log.i(f2381a, "unbindGuardProviderService");
        }
    }

    public int a(String str) {
        int i = -1;
        try {
            if (this.f2384d != null) {
                i = this.f2384d.f("AVL", str);
            }
        } catch (RemoteException e2) {
            String str2 = f2381a;
            Log.e(str2, "exception in scanURL : " + e2.toString());
        } catch (Throwable th) {
            c();
            throw th;
        }
        c();
        String str3 = f2381a;
        Log.i(str3, "scanURL result = " + i);
        return i;
    }

    /* JADX INFO: finally extract failed */
    public void a() {
        int i;
        String str;
        String str2;
        try {
            i = this.f2384d != null ? this.f2384d.d("AVL") : -1;
            c();
        } catch (RemoteException e2) {
            Log.e(f2381a, "exception in updateURLRule : " + e2.toString());
            c();
            i = -1;
        } catch (Throwable th) {
            c();
            throw th;
        }
        if (i == -1) {
            str = f2381a;
            str2 = "AVL URL rules update failed !";
        } else if (i == 0 || i == 1) {
            str = f2381a;
            str2 = "AVL URL rules update success !";
        } else {
            return;
        }
        Log.i(str, str2);
    }
}
