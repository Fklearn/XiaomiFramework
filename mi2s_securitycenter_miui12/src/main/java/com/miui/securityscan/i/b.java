package com.miui.securityscan.i;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import java.util.concurrent.LinkedBlockingQueue;

public class b {

    private static final class a implements ServiceConnection {

        /* renamed from: a  reason: collision with root package name */
        boolean f7722a;

        /* renamed from: b  reason: collision with root package name */
        private final LinkedBlockingQueue<IBinder> f7723b;

        private a() {
            this.f7722a = false;
            this.f7723b = new LinkedBlockingQueue<>(1);
        }

        public IBinder a() {
            if (!this.f7722a) {
                this.f7722a = true;
                return this.f7723b.take();
            }
            throw new IllegalStateException();
        }

        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            try {
                this.f7723b.put(iBinder);
            } catch (InterruptedException unused) {
            }
        }

        public void onServiceDisconnected(ComponentName componentName) {
        }
    }

    /* renamed from: com.miui.securityscan.i.b$b  reason: collision with other inner class name */
    private static final class C0067b implements IInterface {

        /* renamed from: a  reason: collision with root package name */
        private IBinder f7724a;

        public C0067b(IBinder iBinder) {
            this.f7724a = iBinder;
        }

        public String a() {
            Parcel obtain = Parcel.obtain();
            Parcel obtain2 = Parcel.obtain();
            try {
                obtain.writeInterfaceToken("com.google.android.gms.ads.identifier.internal.IAdvertisingIdService");
                this.f7724a.transact(1, obtain, obtain2, 0);
                obtain2.readException();
                return obtain2.readString();
            } finally {
                obtain2.recycle();
                obtain.recycle();
            }
        }

        public IBinder asBinder() {
            return this.f7724a;
        }
    }

    public static String a(Context context) {
        a aVar = new a();
        Intent intent = new Intent("com.google.android.gms.ads.identifier.service.START");
        intent.setPackage("com.google.android.gms");
        if (context.bindService(intent, aVar, 1)) {
            try {
                return new C0067b(aVar.a()).a();
            } catch (Exception unused) {
            } finally {
                context.unbindService(aVar);
            }
        }
        return null;
    }
}
