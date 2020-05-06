package com.android.internal.net;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

public interface IOemNetdUnsolicitedEventListener extends IInterface {
    void onFirewallBlocked(int i, String str) throws RemoteException;

    void onRegistered() throws RemoteException;

    public static class Default implements IOemNetdUnsolicitedEventListener {
        public void onRegistered() throws RemoteException {
        }

        public void onFirewallBlocked(int code, String packageName) throws RemoteException {
        }

        public IBinder asBinder() {
            return null;
        }
    }

    public static abstract class Stub extends Binder implements IOemNetdUnsolicitedEventListener {
        private static final String DESCRIPTOR = "com.android.internal.net.IOemNetdUnsolicitedEventListener";
        static final int TRANSACTION_onFirewallBlocked = 2;
        static final int TRANSACTION_onRegistered = 1;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IOemNetdUnsolicitedEventListener asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin == null || !(iin instanceof IOemNetdUnsolicitedEventListener)) {
                return new Proxy(obj);
            }
            return (IOemNetdUnsolicitedEventListener) iin;
        }

        public IBinder asBinder() {
            return this;
        }

        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            if (code == 1) {
                data.enforceInterface(DESCRIPTOR);
                onRegistered();
                return true;
            } else if (code == 2) {
                data.enforceInterface(DESCRIPTOR);
                onFirewallBlocked(data.readInt(), data.readString());
                return true;
            } else if (code != 1598968902) {
                return super.onTransact(code, data, reply, flags);
            } else {
                reply.writeString(DESCRIPTOR);
                return true;
            }
        }

        private static class Proxy implements IOemNetdUnsolicitedEventListener {
            public static IOemNetdUnsolicitedEventListener sDefaultImpl;
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

            public void onRegistered() throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (this.mRemote.transact(1, _data, (Parcel) null, 1) || Stub.getDefaultImpl() == null) {
                        _data.recycle();
                    } else {
                        Stub.getDefaultImpl().onRegistered();
                    }
                } finally {
                    _data.recycle();
                }
            }

            public void onFirewallBlocked(int code, String packageName) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(code);
                    _data.writeString(packageName);
                    if (this.mRemote.transact(2, _data, (Parcel) null, 1) || Stub.getDefaultImpl() == null) {
                        _data.recycle();
                    } else {
                        Stub.getDefaultImpl().onFirewallBlocked(code, packageName);
                    }
                } finally {
                    _data.recycle();
                }
            }
        }

        public static boolean setDefaultImpl(IOemNetdUnsolicitedEventListener impl) {
            if (Proxy.sDefaultImpl != null || impl == null) {
                return false;
            }
            Proxy.sDefaultImpl = impl;
            return true;
        }

        public static IOemNetdUnsolicitedEventListener getDefaultImpl() {
            return Proxy.sDefaultImpl;
        }
    }
}
