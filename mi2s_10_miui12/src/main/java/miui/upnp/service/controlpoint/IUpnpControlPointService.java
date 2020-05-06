package miui.upnp.service.controlpoint;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import java.util.List;
import miui.upnp.service.handler.ICompletionHandler;
import miui.upnp.service.handler.IEventListener;
import miui.upnp.service.handler.IInvokeCompletionHandler;
import miui.upnp.service.handler.IScanListener;
import miui.upnp.service.handler.ISubscribeCompletionHandler;
import miui.upnp.typedef.device.invocation.ActionInfo;
import miui.upnp.typedef.device.invocation.SubscriptionInfo;
import miui.upnp.typedef.device.urn.Urn;

public interface IUpnpControlPointService extends IInterface {
    void invoke(ActionInfo actionInfo, IInvokeCompletionHandler iInvokeCompletionHandler) throws RemoteException;

    void start() throws RemoteException;

    void startScan(List<Urn> list, ICompletionHandler iCompletionHandler, IScanListener iScanListener) throws RemoteException;

    void stop() throws RemoteException;

    void stopScan(ICompletionHandler iCompletionHandler) throws RemoteException;

    void subscribe(SubscriptionInfo subscriptionInfo, ISubscribeCompletionHandler iSubscribeCompletionHandler, IEventListener iEventListener) throws RemoteException;

    void unsubscribe(SubscriptionInfo subscriptionInfo, ICompletionHandler iCompletionHandler) throws RemoteException;

    public static class Default implements IUpnpControlPointService {
        public void start() throws RemoteException {
        }

        public void stop() throws RemoteException {
        }

        public void startScan(List<Urn> list, ICompletionHandler handler, IScanListener listener) throws RemoteException {
        }

        public void stopScan(ICompletionHandler handler) throws RemoteException {
        }

        public void invoke(ActionInfo info, IInvokeCompletionHandler handler) throws RemoteException {
        }

        public void subscribe(SubscriptionInfo info, ISubscribeCompletionHandler handler, IEventListener listener) throws RemoteException {
        }

        public void unsubscribe(SubscriptionInfo info, ICompletionHandler handler) throws RemoteException {
        }

        public IBinder asBinder() {
            return null;
        }
    }

    public static abstract class Stub extends Binder implements IUpnpControlPointService {
        private static final String DESCRIPTOR = "miui.upnp.service.controlpoint.IUpnpControlPointService";
        static final int TRANSACTION_invoke = 5;
        static final int TRANSACTION_start = 1;
        static final int TRANSACTION_startScan = 3;
        static final int TRANSACTION_stop = 2;
        static final int TRANSACTION_stopScan = 4;
        static final int TRANSACTION_subscribe = 6;
        static final int TRANSACTION_unsubscribe = 7;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IUpnpControlPointService asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin == null || !(iin instanceof IUpnpControlPointService)) {
                return new Proxy(obj);
            }
            return (IUpnpControlPointService) iin;
        }

        public IBinder asBinder() {
            return this;
        }

        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            ActionInfo _arg0;
            SubscriptionInfo _arg02;
            SubscriptionInfo _arg03;
            if (code != 1598968902) {
                switch (code) {
                    case 1:
                        data.enforceInterface(DESCRIPTOR);
                        start();
                        reply.writeNoException();
                        return true;
                    case 2:
                        data.enforceInterface(DESCRIPTOR);
                        stop();
                        reply.writeNoException();
                        return true;
                    case 3:
                        data.enforceInterface(DESCRIPTOR);
                        startScan(data.createTypedArrayList(Urn.CREATOR), ICompletionHandler.Stub.asInterface(data.readStrongBinder()), IScanListener.Stub.asInterface(data.readStrongBinder()));
                        reply.writeNoException();
                        return true;
                    case 4:
                        data.enforceInterface(DESCRIPTOR);
                        stopScan(ICompletionHandler.Stub.asInterface(data.readStrongBinder()));
                        reply.writeNoException();
                        return true;
                    case 5:
                        data.enforceInterface(DESCRIPTOR);
                        if (data.readInt() != 0) {
                            _arg0 = ActionInfo.CREATOR.createFromParcel(data);
                        } else {
                            _arg0 = null;
                        }
                        invoke(_arg0, IInvokeCompletionHandler.Stub.asInterface(data.readStrongBinder()));
                        reply.writeNoException();
                        return true;
                    case 6:
                        data.enforceInterface(DESCRIPTOR);
                        if (data.readInt() != 0) {
                            _arg02 = SubscriptionInfo.CREATOR.createFromParcel(data);
                        } else {
                            _arg02 = null;
                        }
                        subscribe(_arg02, ISubscribeCompletionHandler.Stub.asInterface(data.readStrongBinder()), IEventListener.Stub.asInterface(data.readStrongBinder()));
                        reply.writeNoException();
                        return true;
                    case 7:
                        data.enforceInterface(DESCRIPTOR);
                        if (data.readInt() != 0) {
                            _arg03 = SubscriptionInfo.CREATOR.createFromParcel(data);
                        } else {
                            _arg03 = null;
                        }
                        unsubscribe(_arg03, ICompletionHandler.Stub.asInterface(data.readStrongBinder()));
                        reply.writeNoException();
                        return true;
                    default:
                        return super.onTransact(code, data, reply, flags);
                }
            } else {
                reply.writeString(DESCRIPTOR);
                return true;
            }
        }

        private static class Proxy implements IUpnpControlPointService {
            public static IUpnpControlPointService sDefaultImpl;
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

            public void start() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (this.mRemote.transact(1, _data, _reply, 0) || Stub.getDefaultImpl() == null) {
                        _reply.readException();
                        _reply.recycle();
                        _data.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().start();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void stop() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (this.mRemote.transact(2, _data, _reply, 0) || Stub.getDefaultImpl() == null) {
                        _reply.readException();
                        _reply.recycle();
                        _data.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().stop();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void startScan(List<Urn> types, ICompletionHandler handler, IScanListener listener) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedList(types);
                    IBinder iBinder = null;
                    _data.writeStrongBinder(handler != null ? handler.asBinder() : null);
                    if (listener != null) {
                        iBinder = listener.asBinder();
                    }
                    _data.writeStrongBinder(iBinder);
                    if (this.mRemote.transact(3, _data, _reply, 0) || Stub.getDefaultImpl() == null) {
                        _reply.readException();
                        _reply.recycle();
                        _data.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().startScan(types, handler, listener);
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void stopScan(ICompletionHandler handler) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(handler != null ? handler.asBinder() : null);
                    if (this.mRemote.transact(4, _data, _reply, 0) || Stub.getDefaultImpl() == null) {
                        _reply.readException();
                        _reply.recycle();
                        _data.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().stopScan(handler);
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void invoke(ActionInfo info, IInvokeCompletionHandler handler) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (info != null) {
                        _data.writeInt(1);
                        info.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeStrongBinder(handler != null ? handler.asBinder() : null);
                    if (this.mRemote.transact(5, _data, _reply, 0) || Stub.getDefaultImpl() == null) {
                        _reply.readException();
                        _reply.recycle();
                        _data.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().invoke(info, handler);
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void subscribe(SubscriptionInfo info, ISubscribeCompletionHandler handler, IEventListener listener) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (info != null) {
                        _data.writeInt(1);
                        info.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    IBinder iBinder = null;
                    _data.writeStrongBinder(handler != null ? handler.asBinder() : null);
                    if (listener != null) {
                        iBinder = listener.asBinder();
                    }
                    _data.writeStrongBinder(iBinder);
                    if (this.mRemote.transact(6, _data, _reply, 0) || Stub.getDefaultImpl() == null) {
                        _reply.readException();
                        _reply.recycle();
                        _data.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().subscribe(info, handler, listener);
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void unsubscribe(SubscriptionInfo info, ICompletionHandler handler) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (info != null) {
                        _data.writeInt(1);
                        info.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeStrongBinder(handler != null ? handler.asBinder() : null);
                    if (this.mRemote.transact(7, _data, _reply, 0) || Stub.getDefaultImpl() == null) {
                        _reply.readException();
                        _reply.recycle();
                        _data.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().unsubscribe(info, handler);
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }
        }

        public static boolean setDefaultImpl(IUpnpControlPointService impl) {
            if (Proxy.sDefaultImpl != null || impl == null) {
                return false;
            }
            Proxy.sDefaultImpl = impl;
            return true;
        }

        public static IUpnpControlPointService getDefaultImpl() {
            return Proxy.sDefaultImpl;
        }
    }
}
