package com.miui.guardprovider.aidl;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;

public interface IURLScanServer extends IInterface {

    public static abstract class Stub extends Binder implements IURLScanServer {

        private static class a implements IURLScanServer {

            /* renamed from: a  reason: collision with root package name */
            private IBinder f5467a;

            a(IBinder iBinder) {
                this.f5467a = iBinder;
            }

            public IBinder asBinder() {
                return this.f5467a;
            }

            public int d(String str) {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.miui.guardprovider.aidl.IURLScanServer");
                    obtain.writeString(str);
                    this.f5467a.transact(2, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readInt();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public int f(String str, String str2) {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.miui.guardprovider.aidl.IURLScanServer");
                    obtain.writeString(str);
                    obtain.writeString(str2);
                    this.f5467a.transact(1, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readInt();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }
        }

        public Stub() {
            attachInterface(this, "com.miui.guardprovider.aidl.IURLScanServer");
        }

        public static IURLScanServer a(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface queryLocalInterface = iBinder.queryLocalInterface("com.miui.guardprovider.aidl.IURLScanServer");
            return (queryLocalInterface == null || !(queryLocalInterface instanceof IURLScanServer)) ? new a(iBinder) : (IURLScanServer) queryLocalInterface;
        }

        public IBinder asBinder() {
            return this;
        }

        public boolean onTransact(int i, Parcel parcel, Parcel parcel2, int i2) {
            int f;
            if (i == 1) {
                parcel.enforceInterface("com.miui.guardprovider.aidl.IURLScanServer");
                f = f(parcel.readString(), parcel.readString());
            } else if (i == 2) {
                parcel.enforceInterface("com.miui.guardprovider.aidl.IURLScanServer");
                f = d(parcel.readString());
            } else if (i != 1598968902) {
                return super.onTransact(i, parcel, parcel2, i2);
            } else {
                parcel2.writeString("com.miui.guardprovider.aidl.IURLScanServer");
                return true;
            }
            parcel2.writeNoException();
            parcel2.writeInt(f);
            return true;
        }
    }

    int d(String str);

    int f(String str, String str2);
}
