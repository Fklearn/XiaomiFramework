package com.xiaomi.a.a.a;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import java.util.Map;

public interface a extends IInterface {

    /* renamed from: com.xiaomi.a.a.a.a$a  reason: collision with other inner class name */
    public static abstract class C0069a extends Binder implements a {

        /* renamed from: com.xiaomi.a.a.a.a$a$a  reason: collision with other inner class name */
        private static class C0070a implements a {

            /* renamed from: a  reason: collision with root package name */
            private IBinder f8240a;

            C0070a(IBinder iBinder) {
                this.f8240a = iBinder;
            }

            public String a(String str, Map map) {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.xiaomi.xmsf.push.service.IHttpService");
                    obtain.writeString(str);
                    obtain.writeMap(map);
                    this.f8240a.transact(1, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readString();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public IBinder asBinder() {
                return this.f8240a;
            }

            public String b(String str, Map map) {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.xiaomi.xmsf.push.service.IHttpService");
                    obtain.writeString(str);
                    obtain.writeMap(map);
                    this.f8240a.transact(2, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readString();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }
        }

        public C0069a() {
            attachInterface(this, "com.xiaomi.xmsf.push.service.IHttpService");
        }

        public static a a(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface queryLocalInterface = iBinder.queryLocalInterface("com.xiaomi.xmsf.push.service.IHttpService");
            return (queryLocalInterface == null || !(queryLocalInterface instanceof a)) ? new C0070a(iBinder) : (a) queryLocalInterface;
        }

        public IBinder asBinder() {
            return this;
        }

        public boolean onTransact(int i, Parcel parcel, Parcel parcel2, int i2) {
            String a2;
            if (i == 1) {
                parcel.enforceInterface("com.xiaomi.xmsf.push.service.IHttpService");
                a2 = a(parcel.readString(), parcel.readHashMap(C0069a.class.getClassLoader()));
            } else if (i == 2) {
                parcel.enforceInterface("com.xiaomi.xmsf.push.service.IHttpService");
                a2 = b(parcel.readString(), parcel.readHashMap(C0069a.class.getClassLoader()));
            } else if (i != 1598968902) {
                return super.onTransact(i, parcel, parcel2, i2);
            } else {
                parcel2.writeString("com.xiaomi.xmsf.push.service.IHttpService");
                return true;
            }
            parcel2.writeNoException();
            parcel2.writeString(a2);
            return true;
        }
    }

    String a(String str, Map map);

    String b(String str, Map map);
}
