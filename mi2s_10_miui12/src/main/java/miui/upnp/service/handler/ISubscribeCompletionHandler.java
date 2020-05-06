package miui.upnp.service.handler;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import miui.upnp.typedef.device.invocation.SubscriptionInfo;
import miui.upnp.typedef.error.UpnpError;

public interface ISubscribeCompletionHandler extends IInterface {
    void onFailed(UpnpError upnpError) throws RemoteException;

    void onSucceed(SubscriptionInfo subscriptionInfo) throws RemoteException;

    public static class Default implements ISubscribeCompletionHandler {
        public void onSucceed(SubscriptionInfo subscription) throws RemoteException {
        }

        public void onFailed(UpnpError error) throws RemoteException {
        }

        public IBinder asBinder() {
            return null;
        }
    }

    public static abstract class Stub extends Binder implements ISubscribeCompletionHandler {
        private static final String DESCRIPTOR = "miui.upnp.service.handler.ISubscribeCompletionHandler";
        static final int TRANSACTION_onFailed = 2;
        static final int TRANSACTION_onSucceed = 1;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static ISubscribeCompletionHandler asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin == null || !(iin instanceof ISubscribeCompletionHandler)) {
                return new Proxy(obj);
            }
            return (ISubscribeCompletionHandler) iin;
        }

        public IBinder asBinder() {
            return this;
        }

        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            SubscriptionInfo _arg0;
            UpnpError _arg02;
            if (code == 1) {
                data.enforceInterface(DESCRIPTOR);
                if (data.readInt() != 0) {
                    _arg0 = SubscriptionInfo.CREATOR.createFromParcel(data);
                } else {
                    _arg0 = null;
                }
                onSucceed(_arg0);
                reply.writeNoException();
                return true;
            } else if (code == 2) {
                data.enforceInterface(DESCRIPTOR);
                if (data.readInt() != 0) {
                    _arg02 = UpnpError.CREATOR.createFromParcel(data);
                } else {
                    _arg02 = null;
                }
                onFailed(_arg02);
                reply.writeNoException();
                return true;
            } else if (code != 1598968902) {
                return super.onTransact(code, data, reply, flags);
            } else {
                reply.writeString(DESCRIPTOR);
                return true;
            }
        }

        private static class Proxy implements ISubscribeCompletionHandler {
            public static ISubscribeCompletionHandler sDefaultImpl;
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

            public void onSucceed(SubscriptionInfo subscription) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (subscription != null) {
                        _data.writeInt(1);
                        subscription.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    if (this.mRemote.transact(1, _data, _reply, 0) || Stub.getDefaultImpl() == null) {
                        _reply.readException();
                        _reply.recycle();
                        _data.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().onSucceed(subscription);
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void onFailed(UpnpError error) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (error != null) {
                        _data.writeInt(1);
                        error.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    if (this.mRemote.transact(2, _data, _reply, 0) || Stub.getDefaultImpl() == null) {
                        _reply.readException();
                        _reply.recycle();
                        _data.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().onFailed(error);
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }
        }

        public static boolean setDefaultImpl(ISubscribeCompletionHandler impl) {
            if (Proxy.sDefaultImpl != null || impl == null) {
                return false;
            }
            Proxy.sDefaultImpl = impl;
            return true;
        }

        public static ISubscribeCompletionHandler getDefaultImpl() {
            return Proxy.sDefaultImpl;
        }
    }
}
