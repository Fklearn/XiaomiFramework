package com.miui.guardprovider.aidl;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import com.miui.guardprovider.aidl.IVirusObserver;
import com.miui.guardprovider.aidl.IWifiDetectObserver;

public interface IAntiVirusServer extends IInterface {

    public static abstract class Stub extends Binder implements IAntiVirusServer {

        private static class a implements IAntiVirusServer {

            /* renamed from: a  reason: collision with root package name */
            private IBinder f5466a;

            a(IBinder iBinder) {
                this.f5466a = iBinder;
            }

            public int a(IVirusObserver iVirusObserver) {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.miui.guardprovider.aidl.IAntiVirusServer");
                    obtain.writeStrongBinder(iVirusObserver != null ? iVirusObserver.asBinder() : null);
                    this.f5466a.transact(7, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readInt();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public int a(String str, IWifiDetectObserver iWifiDetectObserver) {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.miui.guardprovider.aidl.IAntiVirusServer");
                    obtain.writeString(str);
                    obtain.writeStrongBinder(iWifiDetectObserver != null ? iWifiDetectObserver.asBinder() : null);
                    this.f5466a.transact(13, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readInt();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public int a(String str, IWifiDetectObserver iWifiDetectObserver, String str2) {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.miui.guardprovider.aidl.IAntiVirusServer");
                    obtain.writeString(str);
                    obtain.writeStrongBinder(iWifiDetectObserver != null ? iWifiDetectObserver.asBinder() : null);
                    obtain.writeString(str2);
                    this.f5466a.transact(14, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readInt();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public int a(String[] strArr, IVirusObserver iVirusObserver, boolean z) {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.miui.guardprovider.aidl.IAntiVirusServer");
                    obtain.writeStringArray(strArr);
                    obtain.writeStrongBinder(iVirusObserver != null ? iVirusObserver.asBinder() : null);
                    obtain.writeInt(z ? 1 : 0);
                    this.f5466a.transact(1, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readInt();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void a(String str, boolean z) {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.miui.guardprovider.aidl.IAntiVirusServer");
                    obtain.writeString(str);
                    obtain.writeInt(z ? 1 : 0);
                    this.f5466a.transact(2, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public IBinder asBinder() {
                return this.f5466a;
            }

            public int b(String str, IWifiDetectObserver iWifiDetectObserver) {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.miui.guardprovider.aidl.IAntiVirusServer");
                    obtain.writeString(str);
                    obtain.writeStrongBinder(iWifiDetectObserver != null ? iWifiDetectObserver.asBinder() : null);
                    this.f5466a.transact(12, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readInt();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public int c(String str, IWifiDetectObserver iWifiDetectObserver) {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.miui.guardprovider.aidl.IAntiVirusServer");
                    obtain.writeString(str);
                    obtain.writeStrongBinder(iWifiDetectObserver != null ? iWifiDetectObserver.asBinder() : null);
                    this.f5466a.transact(15, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readInt();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void e(int i) {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.miui.guardprovider.aidl.IAntiVirusServer");
                    obtain.writeInt(i);
                    this.f5466a.transact(5, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }
        }

        public Stub() {
            attachInterface(this, "com.miui.guardprovider.aidl.IAntiVirusServer");
        }

        public static IAntiVirusServer a(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface queryLocalInterface = iBinder.queryLocalInterface("com.miui.guardprovider.aidl.IAntiVirusServer");
            return (queryLocalInterface == null || !(queryLocalInterface instanceof IAntiVirusServer)) ? new a(iBinder) : (IAntiVirusServer) queryLocalInterface;
        }

        public IBinder asBinder() {
            return this;
        }

        public boolean onTransact(int i, Parcel parcel, Parcel parcel2, int i2) {
            if (i != 1598968902) {
                boolean z = false;
                switch (i) {
                    case 1:
                        parcel.enforceInterface("com.miui.guardprovider.aidl.IAntiVirusServer");
                        String[] createStringArray = parcel.createStringArray();
                        IVirusObserver b2 = IVirusObserver.Stub.b(parcel.readStrongBinder());
                        if (parcel.readInt() != 0) {
                            z = true;
                        }
                        int a2 = a(createStringArray, b2, z);
                        parcel2.writeNoException();
                        parcel2.writeInt(a2);
                        return true;
                    case 2:
                        parcel.enforceInterface("com.miui.guardprovider.aidl.IAntiVirusServer");
                        String readString = parcel.readString();
                        if (parcel.readInt() != 0) {
                            z = true;
                        }
                        a(readString, z);
                        parcel2.writeNoException();
                        return true;
                    case 3:
                        parcel.enforceInterface("com.miui.guardprovider.aidl.IAntiVirusServer");
                        boolean o = o(parcel.readString());
                        parcel2.writeNoException();
                        if (o) {
                            z = true;
                        }
                        parcel2.writeInt(z ? 1 : 0);
                        return true;
                    case 4:
                        parcel.enforceInterface("com.miui.guardprovider.aidl.IAntiVirusServer");
                        boolean k = k(parcel.readString());
                        parcel2.writeNoException();
                        if (k) {
                            z = true;
                        }
                        parcel2.writeInt(z ? 1 : 0);
                        return true;
                    case 5:
                        parcel.enforceInterface("com.miui.guardprovider.aidl.IAntiVirusServer");
                        e(parcel.readInt());
                        parcel2.writeNoException();
                        return true;
                    case 6:
                        parcel.enforceInterface("com.miui.guardprovider.aidl.IAntiVirusServer");
                        int b3 = b(IVirusObserver.Stub.b(parcel.readStrongBinder()));
                        parcel2.writeNoException();
                        parcel2.writeInt(b3);
                        return true;
                    case 7:
                        parcel.enforceInterface("com.miui.guardprovider.aidl.IAntiVirusServer");
                        int a3 = a(IVirusObserver.Stub.b(parcel.readStrongBinder()));
                        parcel2.writeNoException();
                        parcel2.writeInt(a3);
                        return true;
                    case 8:
                        parcel.enforceInterface("com.miui.guardprovider.aidl.IAntiVirusServer");
                        f(parcel.readInt());
                        parcel2.writeNoException();
                        return true;
                    case 9:
                        parcel.enforceInterface("com.miui.guardprovider.aidl.IAntiVirusServer");
                        String version = getVersion();
                        parcel2.writeNoException();
                        parcel2.writeString(version);
                        return true;
                    case 10:
                        parcel.enforceInterface("com.miui.guardprovider.aidl.IAntiVirusServer");
                        String e = e(parcel.readString());
                        parcel2.writeNoException();
                        parcel2.writeString(e);
                        return true;
                    case 11:
                        parcel.enforceInterface("com.miui.guardprovider.aidl.IAntiVirusServer");
                        String[] l = l();
                        parcel2.writeNoException();
                        parcel2.writeStringArray(l);
                        return true;
                    case 12:
                        parcel.enforceInterface("com.miui.guardprovider.aidl.IAntiVirusServer");
                        int b4 = b(parcel.readString(), IWifiDetectObserver.Stub.a(parcel.readStrongBinder()));
                        parcel2.writeNoException();
                        parcel2.writeInt(b4);
                        return true;
                    case 13:
                        parcel.enforceInterface("com.miui.guardprovider.aidl.IAntiVirusServer");
                        int a4 = a(parcel.readString(), IWifiDetectObserver.Stub.a(parcel.readStrongBinder()));
                        parcel2.writeNoException();
                        parcel2.writeInt(a4);
                        return true;
                    case 14:
                        parcel.enforceInterface("com.miui.guardprovider.aidl.IAntiVirusServer");
                        int a5 = a(parcel.readString(), IWifiDetectObserver.Stub.a(parcel.readStrongBinder()), parcel.readString());
                        parcel2.writeNoException();
                        parcel2.writeInt(a5);
                        return true;
                    case 15:
                        parcel.enforceInterface("com.miui.guardprovider.aidl.IAntiVirusServer");
                        int c2 = c(parcel.readString(), IWifiDetectObserver.Stub.a(parcel.readStrongBinder()));
                        parcel2.writeNoException();
                        parcel2.writeInt(c2);
                        return true;
                    default:
                        return super.onTransact(i, parcel, parcel2, i2);
                }
            } else {
                parcel2.writeString("com.miui.guardprovider.aidl.IAntiVirusServer");
                return true;
            }
        }
    }

    int a(IVirusObserver iVirusObserver);

    int a(String str, IWifiDetectObserver iWifiDetectObserver);

    int a(String str, IWifiDetectObserver iWifiDetectObserver, String str2);

    int a(String[] strArr, IVirusObserver iVirusObserver, boolean z);

    void a(String str, boolean z);

    int b(IVirusObserver iVirusObserver);

    int b(String str, IWifiDetectObserver iWifiDetectObserver);

    int c(String str, IWifiDetectObserver iWifiDetectObserver);

    String e(String str);

    void e(int i);

    void f(int i);

    String getVersion();

    boolean k(String str);

    String[] l();

    boolean o(String str);
}
