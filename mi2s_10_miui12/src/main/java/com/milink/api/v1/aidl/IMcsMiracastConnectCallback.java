package com.milink.api.v1.aidl;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

public interface IMcsMiracastConnectCallback extends IInterface {
    void onConnectFail(String str) throws RemoteException;

    void onConnectSuccess(String str) throws RemoteException;

    void onConnecting(String str) throws RemoteException;

    void onResult(int i, String str, String str2) throws RemoteException;

    public static class Default implements IMcsMiracastConnectCallback {
        public void onConnectSuccess(String p2pMac) throws RemoteException {
        }

        public void onConnectFail(String p2pMac) throws RemoteException {
        }

        public void onConnecting(String p2pMac) throws RemoteException {
        }

        public void onResult(int resultCode, String result, String p2pMac) throws RemoteException {
        }

        public IBinder asBinder() {
            return null;
        }
    }

    public static abstract class Stub extends Binder implements IMcsMiracastConnectCallback {
        private static final String DESCRIPTOR = "com.milink.api.v1.aidl.IMcsMiracastConnectCallback";
        static final int TRANSACTION_onConnectFail = 2;
        static final int TRANSACTION_onConnectSuccess = 1;
        static final int TRANSACTION_onConnecting = 3;
        static final int TRANSACTION_onResult = 4;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IMcsMiracastConnectCallback asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin == null || !(iin instanceof IMcsMiracastConnectCallback)) {
                return new Proxy(obj);
            }
            return (IMcsMiracastConnectCallback) iin;
        }

        public IBinder asBinder() {
            return this;
        }

        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            if (code == 1) {
                data.enforceInterface(DESCRIPTOR);
                onConnectSuccess(data.readString());
                reply.writeNoException();
                return true;
            } else if (code == 2) {
                data.enforceInterface(DESCRIPTOR);
                onConnectFail(data.readString());
                reply.writeNoException();
                return true;
            } else if (code == 3) {
                data.enforceInterface(DESCRIPTOR);
                onConnecting(data.readString());
                reply.writeNoException();
                return true;
            } else if (code == 4) {
                data.enforceInterface(DESCRIPTOR);
                onResult(data.readInt(), data.readString(), data.readString());
                reply.writeNoException();
                return true;
            } else if (code != 1598968902) {
                return super.onTransact(code, data, reply, flags);
            } else {
                reply.writeString(DESCRIPTOR);
                return true;
            }
        }

        private static class Proxy implements IMcsMiracastConnectCallback {
            public static IMcsMiracastConnectCallback sDefaultImpl;
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

            public void onConnectSuccess(String p2pMac) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(p2pMac);
                    if (this.mRemote.transact(1, _data, _reply, 0) || Stub.getDefaultImpl() == null) {
                        _reply.readException();
                        _reply.recycle();
                        _data.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().onConnectSuccess(p2pMac);
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void onConnectFail(String p2pMac) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(p2pMac);
                    if (this.mRemote.transact(2, _data, _reply, 0) || Stub.getDefaultImpl() == null) {
                        _reply.readException();
                        _reply.recycle();
                        _data.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().onConnectFail(p2pMac);
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void onConnecting(String p2pMac) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(p2pMac);
                    if (this.mRemote.transact(3, _data, _reply, 0) || Stub.getDefaultImpl() == null) {
                        _reply.readException();
                        _reply.recycle();
                        _data.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().onConnecting(p2pMac);
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void onResult(int resultCode, String result, String p2pMac) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(resultCode);
                    _data.writeString(result);
                    _data.writeString(p2pMac);
                    if (this.mRemote.transact(4, _data, _reply, 0) || Stub.getDefaultImpl() == null) {
                        _reply.readException();
                        _reply.recycle();
                        _data.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().onResult(resultCode, result, p2pMac);
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }
        }

        public static boolean setDefaultImpl(IMcsMiracastConnectCallback impl) {
            if (Proxy.sDefaultImpl != null || impl == null) {
                return false;
            }
            Proxy.sDefaultImpl = impl;
            return true;
        }

        public static IMcsMiracastConnectCallback getDefaultImpl() {
            return Proxy.sDefaultImpl;
        }
    }
}
