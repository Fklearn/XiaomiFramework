package com.miui.networkassistant.vpn.miui;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import java.util.List;

public interface IMiuiVpnManageServiceCallback extends IInterface {

    public static abstract class Stub extends Binder implements IMiuiVpnManageServiceCallback {
        private static final String DESCRIPTOR = "com.miui.networkassistant.vpn.miui.IMiuiVpnManageServiceCallback";
        static final int TRANSACTION_isVpnConnected = 1;
        static final int TRANSACTION_onQueryCouponsResult = 3;
        static final int TRANSACTION_onVpnStateChanged = 2;

        private static class Proxy implements IMiuiVpnManageServiceCallback {
            private IBinder mRemote;

            Proxy(IBinder iBinder) {
                this.mRemote = iBinder;
            }

            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return Stub.DESCRIPTOR;
            }

            public boolean isVpnConnected() {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
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

            public void onQueryCouponsResult(int i, List<String> list) {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeInt(i);
                    obtain.writeStringList(list);
                    this.mRemote.transact(3, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void onVpnStateChanged(int i, int i2, String str) {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeInt(i);
                    obtain.writeInt(i2);
                    obtain.writeString(str);
                    this.mRemote.transact(2, obtain, obtain2, 0);
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

        public static IMiuiVpnManageServiceCallback asInterface(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface queryLocalInterface = iBinder.queryLocalInterface(DESCRIPTOR);
            return (queryLocalInterface == null || !(queryLocalInterface instanceof IMiuiVpnManageServiceCallback)) ? new Proxy(iBinder) : (IMiuiVpnManageServiceCallback) queryLocalInterface;
        }

        public IBinder asBinder() {
            return this;
        }

        public boolean onTransact(int i, Parcel parcel, Parcel parcel2, int i2) {
            if (i != 1) {
                if (i == 2) {
                    parcel.enforceInterface(DESCRIPTOR);
                    onVpnStateChanged(parcel.readInt(), parcel.readInt(), parcel.readString());
                } else if (i == 3) {
                    parcel.enforceInterface(DESCRIPTOR);
                    onQueryCouponsResult(parcel.readInt(), parcel.createStringArrayList());
                } else if (i != 1598968902) {
                    return super.onTransact(i, parcel, parcel2, i2);
                } else {
                    parcel2.writeString(DESCRIPTOR);
                    return true;
                }
                parcel2.writeNoException();
                return true;
            }
            parcel.enforceInterface(DESCRIPTOR);
            boolean isVpnConnected = isVpnConnected();
            parcel2.writeNoException();
            parcel2.writeInt(isVpnConnected ? 1 : 0);
            return true;
        }
    }

    boolean isVpnConnected();

    void onQueryCouponsResult(int i, List<String> list);

    void onVpnStateChanged(int i, int i2, String str);
}
