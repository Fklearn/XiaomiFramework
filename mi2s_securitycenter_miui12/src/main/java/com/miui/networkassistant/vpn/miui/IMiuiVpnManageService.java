package com.miui.networkassistant.vpn.miui;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import com.miui.networkassistant.vpn.miui.IMiuiVpnManageServiceCallback;
import java.util.List;

public interface IMiuiVpnManageService extends IInterface {

    public static abstract class Stub extends Binder implements IMiuiVpnManageService {
        private static final String DESCRIPTOR = "com.miui.networkassistant.vpn.miui.IMiuiVpnManageService";
        static final int TRANSACTION_connectVpn = 8;
        static final int TRANSACTION_disConnectVpn = 15;
        static final int TRANSACTION_getCoupons = 14;
        static final int TRANSACTION_getSetting = 10;
        static final int TRANSACTION_getSettingEx = 12;
        static final int TRANSACTION_getSupportApps = 4;
        static final int TRANSACTION_getSupportVpn = 3;
        static final int TRANSACTION_getVpnEnabled = 1;
        static final int TRANSACTION_init = 7;
        static final int TRANSACTION_refreshUserState = 9;
        static final int TRANSACTION_registerCallback = 5;
        static final int TRANSACTION_setSetting = 11;
        static final int TRANSACTION_setSettingEx = 13;
        static final int TRANSACTION_setVpnEnabled = 2;
        static final int TRANSACTION_unregisterCallback = 6;

        private static class Proxy implements IMiuiVpnManageService {
            private IBinder mRemote;

            Proxy(IBinder iBinder) {
                this.mRemote = iBinder;
            }

            public IBinder asBinder() {
                return this.mRemote;
            }

            public int connectVpn(String str) {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeString(str);
                    this.mRemote.transact(8, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readInt();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void disConnectVpn() {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(15, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void getCoupons() {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(14, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public String getInterfaceDescriptor() {
                return Stub.DESCRIPTOR;
            }

            public String getSetting(String str, String str2) {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeString(str);
                    obtain.writeString(str2);
                    this.mRemote.transact(10, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readString();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public String getSettingEx(String str, String str2, String str3) {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeString(str);
                    obtain.writeString(str2);
                    obtain.writeString(str3);
                    this.mRemote.transact(12, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readString();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public List<String> getSupportApps(String str) {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeString(str);
                    this.mRemote.transact(4, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.createStringArrayList();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public String getSupportVpn() {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(3, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readString();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public boolean getVpnEnabled(String str, String str2) {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeString(str);
                    obtain.writeString(str2);
                    boolean z = false;
                    this.mRemote.transact(1, obtain, obtain2, 0);
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

            public int init(String str) {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeString(str);
                    this.mRemote.transact(7, obtain, obtain2, 0);
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
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(9, obtain, obtain2, 0);
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
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeStrongBinder(iMiuiVpnManageServiceCallback != null ? iMiuiVpnManageServiceCallback.asBinder() : null);
                    this.mRemote.transact(5, obtain, obtain2, 0);
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
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeString(str);
                    obtain.writeString(str2);
                    boolean z = false;
                    this.mRemote.transact(11, obtain, obtain2, 0);
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

            public boolean setSettingEx(String str, String str2, String str3) {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeString(str);
                    obtain.writeString(str2);
                    obtain.writeString(str3);
                    boolean z = false;
                    this.mRemote.transact(13, obtain, obtain2, 0);
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

            public int setVpnEnabled(String str, String str2, boolean z) {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeString(str);
                    obtain.writeString(str2);
                    obtain.writeInt(z ? 1 : 0);
                    this.mRemote.transact(2, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readInt();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void unregisterCallback(IMiuiVpnManageServiceCallback iMiuiVpnManageServiceCallback) {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeStrongBinder(iMiuiVpnManageServiceCallback != null ? iMiuiVpnManageServiceCallback.asBinder() : null);
                    this.mRemote.transact(6, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }
        }

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IMiuiVpnManageService asInterface(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface queryLocalInterface = iBinder.queryLocalInterface(DESCRIPTOR);
            return (queryLocalInterface == null || !(queryLocalInterface instanceof IMiuiVpnManageService)) ? new Proxy(iBinder) : (IMiuiVpnManageService) queryLocalInterface;
        }

        public IBinder asBinder() {
            return this;
        }

        public boolean onTransact(int i, Parcel parcel, Parcel parcel2, int i2) {
            if (i != 1598968902) {
                boolean z = false;
                switch (i) {
                    case 1:
                        parcel.enforceInterface(DESCRIPTOR);
                        boolean vpnEnabled = getVpnEnabled(parcel.readString(), parcel.readString());
                        parcel2.writeNoException();
                        if (vpnEnabled) {
                            z = true;
                        }
                        parcel2.writeInt(z ? 1 : 0);
                        return true;
                    case 2:
                        parcel.enforceInterface(DESCRIPTOR);
                        String readString = parcel.readString();
                        String readString2 = parcel.readString();
                        if (parcel.readInt() != 0) {
                            z = true;
                        }
                        int vpnEnabled2 = setVpnEnabled(readString, readString2, z);
                        parcel2.writeNoException();
                        parcel2.writeInt(vpnEnabled2);
                        return true;
                    case 3:
                        parcel.enforceInterface(DESCRIPTOR);
                        String supportVpn = getSupportVpn();
                        parcel2.writeNoException();
                        parcel2.writeString(supportVpn);
                        return true;
                    case 4:
                        parcel.enforceInterface(DESCRIPTOR);
                        List<String> supportApps = getSupportApps(parcel.readString());
                        parcel2.writeNoException();
                        parcel2.writeStringList(supportApps);
                        return true;
                    case 5:
                        parcel.enforceInterface(DESCRIPTOR);
                        registerCallback(IMiuiVpnManageServiceCallback.Stub.asInterface(parcel.readStrongBinder()));
                        parcel2.writeNoException();
                        return true;
                    case 6:
                        parcel.enforceInterface(DESCRIPTOR);
                        unregisterCallback(IMiuiVpnManageServiceCallback.Stub.asInterface(parcel.readStrongBinder()));
                        parcel2.writeNoException();
                        return true;
                    case 7:
                        parcel.enforceInterface(DESCRIPTOR);
                        int init = init(parcel.readString());
                        parcel2.writeNoException();
                        parcel2.writeInt(init);
                        return true;
                    case 8:
                        parcel.enforceInterface(DESCRIPTOR);
                        int connectVpn = connectVpn(parcel.readString());
                        parcel2.writeNoException();
                        parcel2.writeInt(connectVpn);
                        return true;
                    case 9:
                        parcel.enforceInterface(DESCRIPTOR);
                        int refreshUserState = refreshUserState();
                        parcel2.writeNoException();
                        parcel2.writeInt(refreshUserState);
                        return true;
                    case 10:
                        parcel.enforceInterface(DESCRIPTOR);
                        String setting = getSetting(parcel.readString(), parcel.readString());
                        parcel2.writeNoException();
                        parcel2.writeString(setting);
                        return true;
                    case 11:
                        parcel.enforceInterface(DESCRIPTOR);
                        boolean setting2 = setSetting(parcel.readString(), parcel.readString());
                        parcel2.writeNoException();
                        if (setting2) {
                            z = true;
                        }
                        parcel2.writeInt(z ? 1 : 0);
                        return true;
                    case 12:
                        parcel.enforceInterface(DESCRIPTOR);
                        String settingEx = getSettingEx(parcel.readString(), parcel.readString(), parcel.readString());
                        parcel2.writeNoException();
                        parcel2.writeString(settingEx);
                        return true;
                    case 13:
                        parcel.enforceInterface(DESCRIPTOR);
                        boolean settingEx2 = setSettingEx(parcel.readString(), parcel.readString(), parcel.readString());
                        parcel2.writeNoException();
                        if (settingEx2) {
                            z = true;
                        }
                        parcel2.writeInt(z ? 1 : 0);
                        return true;
                    case 14:
                        parcel.enforceInterface(DESCRIPTOR);
                        getCoupons();
                        parcel2.writeNoException();
                        return true;
                    case 15:
                        parcel.enforceInterface(DESCRIPTOR);
                        disConnectVpn();
                        parcel2.writeNoException();
                        return true;
                    default:
                        return super.onTransact(i, parcel, parcel2, i2);
                }
            } else {
                parcel2.writeString(DESCRIPTOR);
                return true;
            }
        }
    }

    int connectVpn(String str);

    void disConnectVpn();

    void getCoupons();

    String getSetting(String str, String str2);

    String getSettingEx(String str, String str2, String str3);

    List<String> getSupportApps(String str);

    String getSupportVpn();

    boolean getVpnEnabled(String str, String str2);

    int init(String str);

    int refreshUserState();

    void registerCallback(IMiuiVpnManageServiceCallback iMiuiVpnManageServiceCallback);

    boolean setSetting(String str, String str2);

    boolean setSettingEx(String str, String str2, String str3);

    int setVpnEnabled(String str, String str2, boolean z);

    void unregisterCallback(IMiuiVpnManageServiceCallback iMiuiVpnManageServiceCallback);
}
