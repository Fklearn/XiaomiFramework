package miui.upnp.service.host;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import miui.upnp.service.handler.IActionListener;
import miui.upnp.service.handler.ICompletionHandler;
import miui.upnp.typedef.device.Device;
import miui.upnp.typedef.device.invocation.EventInfo;

public interface IUpnpHostService extends IInterface {
    void register(Device device, ICompletionHandler iCompletionHandler, IActionListener iActionListener) throws RemoteException;

    void sendEvents(EventInfo eventInfo) throws RemoteException;

    void start() throws RemoteException;

    void stop() throws RemoteException;

    void unregister(Device device, ICompletionHandler iCompletionHandler) throws RemoteException;

    public static class Default implements IUpnpHostService {
        public void start() throws RemoteException {
        }

        public void stop() throws RemoteException {
        }

        public void register(Device device, ICompletionHandler handler, IActionListener listener) throws RemoteException {
        }

        public void unregister(Device device, ICompletionHandler handler) throws RemoteException {
        }

        public void sendEvents(EventInfo info) throws RemoteException {
        }

        public IBinder asBinder() {
            return null;
        }
    }

    public static abstract class Stub extends Binder implements IUpnpHostService {
        private static final String DESCRIPTOR = "miui.upnp.service.host.IUpnpHostService";
        static final int TRANSACTION_register = 3;
        static final int TRANSACTION_sendEvents = 5;
        static final int TRANSACTION_start = 1;
        static final int TRANSACTION_stop = 2;
        static final int TRANSACTION_unregister = 4;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IUpnpHostService asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin == null || !(iin instanceof IUpnpHostService)) {
                return new Proxy(obj);
            }
            return (IUpnpHostService) iin;
        }

        public IBinder asBinder() {
            return this;
        }

        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            Device _arg0;
            Device _arg02;
            EventInfo _arg03;
            if (code == 1) {
                data.enforceInterface(DESCRIPTOR);
                start();
                reply.writeNoException();
                return true;
            } else if (code == 2) {
                data.enforceInterface(DESCRIPTOR);
                stop();
                reply.writeNoException();
                return true;
            } else if (code == 3) {
                data.enforceInterface(DESCRIPTOR);
                if (data.readInt() != 0) {
                    _arg0 = Device.CREATOR.createFromParcel(data);
                } else {
                    _arg0 = null;
                }
                register(_arg0, ICompletionHandler.Stub.asInterface(data.readStrongBinder()), IActionListener.Stub.asInterface(data.readStrongBinder()));
                reply.writeNoException();
                return true;
            } else if (code == 4) {
                data.enforceInterface(DESCRIPTOR);
                if (data.readInt() != 0) {
                    _arg02 = Device.CREATOR.createFromParcel(data);
                } else {
                    _arg02 = null;
                }
                unregister(_arg02, ICompletionHandler.Stub.asInterface(data.readStrongBinder()));
                reply.writeNoException();
                return true;
            } else if (code == 5) {
                data.enforceInterface(DESCRIPTOR);
                if (data.readInt() != 0) {
                    _arg03 = EventInfo.CREATOR.createFromParcel(data);
                } else {
                    _arg03 = null;
                }
                sendEvents(_arg03);
                reply.writeNoException();
                return true;
            } else if (code != 1598968902) {
                return super.onTransact(code, data, reply, flags);
            } else {
                reply.writeString(DESCRIPTOR);
                return true;
            }
        }

        private static class Proxy implements IUpnpHostService {
            public static IUpnpHostService sDefaultImpl;
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

            public void register(Device device, ICompletionHandler handler, IActionListener listener) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (device != null) {
                        _data.writeInt(1);
                        device.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
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
                    Stub.getDefaultImpl().register(device, handler, listener);
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void unregister(Device device, ICompletionHandler handler) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (device != null) {
                        _data.writeInt(1);
                        device.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeStrongBinder(handler != null ? handler.asBinder() : null);
                    if (this.mRemote.transact(4, _data, _reply, 0) || Stub.getDefaultImpl() == null) {
                        _reply.readException();
                        _reply.recycle();
                        _data.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().unregister(device, handler);
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void sendEvents(EventInfo info) throws RemoteException {
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
                    if (this.mRemote.transact(5, _data, _reply, 0) || Stub.getDefaultImpl() == null) {
                        _reply.readException();
                        _reply.recycle();
                        _data.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().sendEvents(info);
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }
        }

        public static boolean setDefaultImpl(IUpnpHostService impl) {
            if (Proxy.sDefaultImpl != null || impl == null) {
                return false;
            }
            Proxy.sDefaultImpl = impl;
            return true;
        }

        public static IUpnpHostService getDefaultImpl() {
            return Proxy.sDefaultImpl;
        }
    }
}
