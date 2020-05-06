package com.milink.api.v1.aidl;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

public interface IMcsScanListCallback extends IInterface {
    void onConnectFail(String str, String str2) throws RemoteException;

    void onConnectSuccess(String str, String str2) throws RemoteException;

    void onSelectDevice(String str, String str2, String str3) throws RemoteException;

    public static class Default implements IMcsScanListCallback {
        public void onSelectDevice(String deviceId, String deviceName, String deviceType) throws RemoteException {
        }

        public void onConnectSuccess(String deviceId, String deviceName) throws RemoteException {
        }

        public void onConnectFail(String deviceId, String deviceName) throws RemoteException {
        }

        public IBinder asBinder() {
            return null;
        }
    }

    public static abstract class Stub extends Binder implements IMcsScanListCallback {
        private static final String DESCRIPTOR = "com.milink.api.v1.aidl.IMcsScanListCallback";
        static final int TRANSACTION_onConnectFail = 3;
        static final int TRANSACTION_onConnectSuccess = 2;
        static final int TRANSACTION_onSelectDevice = 1;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IMcsScanListCallback asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin == null || !(iin instanceof IMcsScanListCallback)) {
                return new Proxy(obj);
            }
            return (IMcsScanListCallback) iin;
        }

        public IBinder asBinder() {
            return this;
        }

        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            if (code == 1) {
                data.enforceInterface(DESCRIPTOR);
                onSelectDevice(data.readString(), data.readString(), data.readString());
                reply.writeNoException();
                return true;
            } else if (code == 2) {
                data.enforceInterface(DESCRIPTOR);
                onConnectSuccess(data.readString(), data.readString());
                reply.writeNoException();
                return true;
            } else if (code == 3) {
                data.enforceInterface(DESCRIPTOR);
                onConnectFail(data.readString(), data.readString());
                reply.writeNoException();
                return true;
            } else if (code != 1598968902) {
                return super.onTransact(code, data, reply, flags);
            } else {
                reply.writeString(DESCRIPTOR);
                return true;
            }
        }

        private static class Proxy implements IMcsScanListCallback {
            public static IMcsScanListCallback sDefaultImpl;
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

            public void onSelectDevice(String deviceId, String deviceName, String deviceType) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(deviceId);
                    _data.writeString(deviceName);
                    _data.writeString(deviceType);
                    if (this.mRemote.transact(1, _data, _reply, 0) || Stub.getDefaultImpl() == null) {
                        _reply.readException();
                        _reply.recycle();
                        _data.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().onSelectDevice(deviceId, deviceName, deviceType);
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void onConnectSuccess(String deviceId, String deviceName) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(deviceId);
                    _data.writeString(deviceName);
                    if (this.mRemote.transact(2, _data, _reply, 0) || Stub.getDefaultImpl() == null) {
                        _reply.readException();
                        _reply.recycle();
                        _data.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().onConnectSuccess(deviceId, deviceName);
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void onConnectFail(String deviceId, String deviceName) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(deviceId);
                    _data.writeString(deviceName);
                    if (this.mRemote.transact(3, _data, _reply, 0) || Stub.getDefaultImpl() == null) {
                        _reply.readException();
                        _reply.recycle();
                        _data.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().onConnectFail(deviceId, deviceName);
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }
        }

        public static boolean setDefaultImpl(IMcsScanListCallback impl) {
            if (Proxy.sDefaultImpl != null || impl == null) {
                return false;
            }
            Proxy.sDefaultImpl = impl;
            return true;
        }

        public static IMcsScanListCallback getDefaultImpl() {
            return Proxy.sDefaultImpl;
        }
    }
}
