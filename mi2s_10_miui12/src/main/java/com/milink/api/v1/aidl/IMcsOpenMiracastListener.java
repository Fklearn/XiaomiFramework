package com.milink.api.v1.aidl;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

public interface IMcsOpenMiracastListener extends IInterface {
    void openFailure(String str, String str2, String str3, String str4) throws RemoteException;

    void openSuccess(String str, String str2, String str3) throws RemoteException;

    public static class Default implements IMcsOpenMiracastListener {
        public void openSuccess(String deviceName, String p2pMac, String wifiMac) throws RemoteException {
        }

        public void openFailure(String deviceName, String p2pMac, String wifiMac, String errorCode) throws RemoteException {
        }

        public IBinder asBinder() {
            return null;
        }
    }

    public static abstract class Stub extends Binder implements IMcsOpenMiracastListener {
        private static final String DESCRIPTOR = "com.milink.api.v1.aidl.IMcsOpenMiracastListener";
        static final int TRANSACTION_openFailure = 2;
        static final int TRANSACTION_openSuccess = 1;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IMcsOpenMiracastListener asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin == null || !(iin instanceof IMcsOpenMiracastListener)) {
                return new Proxy(obj);
            }
            return (IMcsOpenMiracastListener) iin;
        }

        public IBinder asBinder() {
            return this;
        }

        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            if (code == 1) {
                data.enforceInterface(DESCRIPTOR);
                openSuccess(data.readString(), data.readString(), data.readString());
                reply.writeNoException();
                return true;
            } else if (code == 2) {
                data.enforceInterface(DESCRIPTOR);
                openFailure(data.readString(), data.readString(), data.readString(), data.readString());
                reply.writeNoException();
                return true;
            } else if (code != 1598968902) {
                return super.onTransact(code, data, reply, flags);
            } else {
                reply.writeString(DESCRIPTOR);
                return true;
            }
        }

        private static class Proxy implements IMcsOpenMiracastListener {
            public static IMcsOpenMiracastListener sDefaultImpl;
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return Stub.DESCRIPTOR;
            }

            public void openSuccess(String deviceName, String p2pMac, String wifiMac) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(deviceName);
                    _data.writeString(p2pMac);
                    _data.writeString(wifiMac);
                    if (this.mRemote.transact(1, _data, _reply, 0) || Stub.getDefaultImpl() == null) {
                        _reply.readException();
                        _reply.recycle();
                        _data.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().openSuccess(deviceName, p2pMac, wifiMac);
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void openFailure(String deviceName, String p2pMac, String wifiMac, String errorCode) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(deviceName);
                    _data.writeString(p2pMac);
                    _data.writeString(wifiMac);
                    _data.writeString(errorCode);
                    if (this.mRemote.transact(2, _data, _reply, 0) || Stub.getDefaultImpl() == null) {
                        _reply.readException();
                        _reply.recycle();
                        _data.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().openFailure(deviceName, p2pMac, wifiMac, errorCode);
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }
        }

        public static boolean setDefaultImpl(IMcsOpenMiracastListener impl) {
            if (Proxy.sDefaultImpl != null || impl == null) {
                return false;
            }
            Proxy.sDefaultImpl = impl;
            return true;
        }

        public static IMcsOpenMiracastListener getDefaultImpl() {
            return Proxy.sDefaultImpl;
        }
    }
}
