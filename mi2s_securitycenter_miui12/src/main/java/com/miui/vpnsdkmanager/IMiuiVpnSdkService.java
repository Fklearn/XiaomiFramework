package com.miui.vpnsdkmanager;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import com.miui.networkassistant.vpn.miui.IMiuiVpnManageServiceCallback;
import java.util.List;

public interface IMiuiVpnSdkService extends IInterface {

    public static abstract class Stub extends Binder implements IMiuiVpnSdkService {

        private static class a implements IMiuiVpnSdkService {

            /* renamed from: a  reason: collision with root package name */
            private IBinder f8233a;

            a(IBinder iBinder) {
                this.f8233a = iBinder;
            }

            public int C() {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.miui.vpnsdkmanager.IMiuiVpnSdkService");
                    this.f8233a.transact(4, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readInt();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public int a(int i, List<String> list) {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.miui.vpnsdkmanager.IMiuiVpnSdkService");
                    obtain.writeInt(i);
                    obtain.writeStringList(list);
                    this.f8233a.transact(2, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readInt();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public IBinder asBinder() {
                return this.f8233a;
            }

            public void d(String str, int i) {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.miui.vpnsdkmanager.IMiuiVpnSdkService");
                    obtain.writeString(str);
                    obtain.writeInt(i);
                    this.f8233a.transact(10, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public int g(int i) {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.miui.vpnsdkmanager.IMiuiVpnSdkService");
                    obtain.writeInt(i);
                    this.f8233a.transact(3, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readInt();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void getCoupons() {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.miui.vpnsdkmanager.IMiuiVpnSdkService");
                    this.f8233a.transact(11, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public String getSetting(String str, String str2) {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.miui.vpnsdkmanager.IMiuiVpnSdkService");
                    obtain.writeString(str);
                    obtain.writeString(str2);
                    this.f8233a.transact(8, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readString();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public int o() {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.miui.vpnsdkmanager.IMiuiVpnSdkService");
                    this.f8233a.transact(6, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readInt();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public int refreshUserState() {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.miui.vpnsdkmanager.IMiuiVpnSdkService");
                    this.f8233a.transact(5, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readInt();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void registerCallback(IMiuiVpnManageServiceCallback iMiuiVpnManageServiceCallback) {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.miui.vpnsdkmanager.IMiuiVpnSdkService");
                    obtain.writeStrongBinder(iMiuiVpnManageServiceCallback != null ? iMiuiVpnManageServiceCallback.asBinder() : null);
                    this.f8233a.transact(1, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public boolean setSetting(String str, String str2) {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.miui.vpnsdkmanager.IMiuiVpnSdkService");
                    obtain.writeString(str);
                    obtain.writeString(str2);
                    boolean z = false;
                    this.f8233a.transact(9, obtain, obtain2, 0);
                    obtain2.readException();
                    if (obtain2.readInt() != 0) {
                        z = true;
                    }
                    return z;
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public int z() {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.miui.vpnsdkmanager.IMiuiVpnSdkService");
                    this.f8233a.transact(7, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readInt();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }
        }

        public Stub() {
            attachInterface(this, "com.miui.vpnsdkmanager.IMiuiVpnSdkService");
        }

        public static IMiuiVpnSdkService a(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface queryLocalInterface = iBinder.queryLocalInterface("com.miui.vpnsdkmanager.IMiuiVpnSdkService");
            return (queryLocalInterface == null || !(queryLocalInterface instanceof IMiuiVpnSdkService)) ? new a(iBinder) : (IMiuiVpnSdkService) queryLocalInterface;
        }

        public IBinder asBinder() {
            return this;
        }

        public boolean onTransact(int i, Parcel parcel, Parcel parcel2, int i2) {
            if (i != 1598968902) {
                switch (i) {
                    case 1:
                        parcel.enforceInterface("com.miui.vpnsdkmanager.IMiuiVpnSdkService");
                        registerCallback(IMiuiVpnManageServiceCallback.Stub.asInterface(parcel.readStrongBinder()));
                        parcel2.writeNoException();
                        return true;
                    case 2:
                        parcel.enforceInterface("com.miui.vpnsdkmanager.IMiuiVpnSdkService");
                        int a2 = a(parcel.readInt(), parcel.createStringArrayList());
                        parcel2.writeNoException();
                        parcel2.writeInt(a2);
                        return true;
                    case 3:
                        parcel.enforceInterface("com.miui.vpnsdkmanager.IMiuiVpnSdkService");
                        int g = g(parcel.readInt());
                        parcel2.writeNoException();
                        parcel2.writeInt(g);
                        return true;
                    case 4:
                        parcel.enforceInterface("com.miui.vpnsdkmanager.IMiuiVpnSdkService");
                        int C = C();
                        parcel2.writeNoException();
                        parcel2.writeInt(C);
                        return true;
                    case 5:
                        parcel.enforceInterface("com.miui.vpnsdkmanager.IMiuiVpnSdkService");
                        int refreshUserState = refreshUserState();
                        parcel2.writeNoException();
                        parcel2.writeInt(refreshUserState);
                        return true;
                    case 6:
                        parcel.enforceInterface("com.miui.vpnsdkmanager.IMiuiVpnSdkService");
                        int o = o();
                        parcel2.writeNoException();
                        parcel2.writeInt(o);
                        return true;
                    case 7:
                        parcel.enforceInterface("com.miui.vpnsdkmanager.IMiuiVpnSdkService");
                        int z = z();
                        parcel2.writeNoException();
                        parcel2.writeInt(z);
                        return true;
                    case 8:
                        parcel.enforceInterface("com.miui.vpnsdkmanager.IMiuiVpnSdkService");
                        String setting = getSetting(parcel.readString(), parcel.readString());
                        parcel2.writeNoException();
                        parcel2.writeString(setting);
                        return true;
                    case 9:
                        parcel.enforceInterface("com.miui.vpnsdkmanager.IMiuiVpnSdkService");
                        boolean setting2 = setSetting(parcel.readString(), parcel.readString());
                        parcel2.writeNoException();
                        parcel2.writeInt(setting2 ? 1 : 0);
                        return true;
                    case 10:
                        parcel.enforceInterface("com.miui.vpnsdkmanager.IMiuiVpnSdkService");
                        d(parcel.readString(), parcel.readInt());
                        parcel2.writeNoException();
                        return true;
                    case 11:
                        parcel.enforceInterface("com.miui.vpnsdkmanager.IMiuiVpnSdkService");
                        getCoupons();
                        parcel2.writeNoException();
                        return true;
                    default:
                        return super.onTransact(i, parcel, parcel2, i2);
                }
            } else {
                parcel2.writeString("com.miui.vpnsdkmanager.IMiuiVpnSdkService");
                return true;
            }
        }
    }

    int C();

    int a(int i, List<String> list);

    void d(String str, int i);

    int g(int i);

    void getCoupons();

    String getSetting(String str, String str2);

    int o();

    int refreshUserState();

    void registerCallback(IMiuiVpnManageServiceCallback iMiuiVpnManageServiceCallback);

    boolean setSetting(String str, String str2);

    int z();
}
