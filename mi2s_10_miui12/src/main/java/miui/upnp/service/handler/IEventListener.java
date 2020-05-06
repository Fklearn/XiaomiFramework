package miui.upnp.service.handler;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import java.util.List;
import miui.upnp.typedef.device.PropertyChanged;

public interface IEventListener extends IInterface {
    void onEvent(String str, List<PropertyChanged> list) throws RemoteException;

    void onSubscriptionExpired(String str) throws RemoteException;

    public static class Default implements IEventListener {
        public void onSubscriptionExpired(String serviceId) throws RemoteException {
        }

        public void onEvent(String serviceId, List<PropertyChanged> list) throws RemoteException {
        }

        public IBinder asBinder() {
            return null;
        }
    }

    public static abstract class Stub extends Binder implements IEventListener {
        private static final String DESCRIPTOR = "miui.upnp.service.handler.IEventListener";
        static final int TRANSACTION_onEvent = 2;
        static final int TRANSACTION_onSubscriptionExpired = 1;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IEventListener asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin == null || !(iin instanceof IEventListener)) {
                return new Proxy(obj);
            }
            return (IEventListener) iin;
        }

        public IBinder asBinder() {
            return this;
        }

        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            if (code == 1) {
                data.enforceInterface(DESCRIPTOR);
                onSubscriptionExpired(data.readString());
                reply.writeNoException();
                return true;
            } else if (code == 2) {
                data.enforceInterface(DESCRIPTOR);
                onEvent(data.readString(), data.createTypedArrayList(PropertyChanged.CREATOR));
                reply.writeNoException();
                return true;
            } else if (code != 1598968902) {
                return super.onTransact(code, data, reply, flags);
            } else {
                reply.writeString(DESCRIPTOR);
                return true;
            }
        }

        private static class Proxy implements IEventListener {
            public static IEventListener sDefaultImpl;
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

            public void onSubscriptionExpired(String serviceId) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(serviceId);
                    if (this.mRemote.transact(1, _data, _reply, 0) || Stub.getDefaultImpl() == null) {
                        _reply.readException();
                        _reply.recycle();
                        _data.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().onSubscriptionExpired(serviceId);
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void onEvent(String serviceId, List<PropertyChanged> list) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(serviceId);
                    _data.writeTypedList(list);
                    if (this.mRemote.transact(2, _data, _reply, 0) || Stub.getDefaultImpl() == null) {
                        _reply.readException();
                        _reply.recycle();
                        _data.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().onEvent(serviceId, list);
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }
        }

        public static boolean setDefaultImpl(IEventListener impl) {
            if (Proxy.sDefaultImpl != null || impl == null) {
                return false;
            }
            Proxy.sDefaultImpl = impl;
            return true;
        }

        public static IEventListener getDefaultImpl() {
            return Proxy.sDefaultImpl;
        }
    }
}
