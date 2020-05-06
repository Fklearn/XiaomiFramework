package com.xiaomi.stat.c;

import android.os.IBinder;
import android.os.RemoteException;
import com.xiaomi.a.a.a.a;
import com.xiaomi.stat.b;
import com.xiaomi.stat.d.k;

class e implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ IBinder f8478a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ d f8479b;

    e(d dVar, IBinder iBinder) {
        this.f8479b = dVar;
        this.f8478a = iBinder;
    }

    public void run() {
        a a2 = a.C0069a.a(this.f8478a);
        try {
            if (!b.e()) {
                this.f8479b.f8475a[0] = a2.a(this.f8479b.f8476b, this.f8479b.f8477c);
            } else if (b.x()) {
                this.f8479b.f8475a[0] = a2.b(this.f8479b.f8476b, this.f8479b.f8477c);
            } else {
                this.f8479b.f8475a[0] = null;
            }
            k.b("UploadMode", " connected, do remote http post " + this.f8479b.f8475a[0]);
            synchronized (i.class) {
                try {
                    i.class.notify();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (RemoteException e2) {
            k.e("UploadMode", " error while uploading the data by IPC." + e2.toString());
            this.f8479b.f8475a[0] = null;
        }
    }
}
