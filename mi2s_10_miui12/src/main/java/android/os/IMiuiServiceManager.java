package android.os;

public interface IMiuiServiceManager extends IInterface {
    void addService(String str, IBinder iBinder) throws RemoteException;

    IBinder getService(String str) throws RemoteException;

    String[] listServices() throws RemoteException;

    public static class Default implements IMiuiServiceManager {
        public IBinder getService(String name) throws RemoteException {
            return null;
        }

        public void addService(String name, IBinder service) throws RemoteException {
        }

        public String[] listServices() throws RemoteException {
            return null;
        }

        public IBinder asBinder() {
            return null;
        }
    }

    public static abstract class Stub extends Binder implements IMiuiServiceManager {
        private static final String DESCRIPTOR = "android.os.IMiuiServiceManager";
        static final int TRANSACTION_addService = 2;
        static final int TRANSACTION_getService = 1;
        static final int TRANSACTION_listServices = 3;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IMiuiServiceManager asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin == null || !(iin instanceof IMiuiServiceManager)) {
                return new Proxy(obj);
            }
            return (IMiuiServiceManager) iin;
        }

        public IBinder asBinder() {
            return this;
        }

        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            if (code == 1) {
                data.enforceInterface(DESCRIPTOR);
                IBinder _result = getService(data.readString());
                reply.writeNoException();
                reply.writeStrongBinder(_result);
                return true;
            } else if (code == 2) {
                data.enforceInterface(DESCRIPTOR);
                addService(data.readString(), data.readStrongBinder());
                reply.writeNoException();
                return true;
            } else if (code == 3) {
                data.enforceInterface(DESCRIPTOR);
                String[] _result2 = listServices();
                reply.writeNoException();
                reply.writeStringArray(_result2);
                return true;
            } else if (code != 1598968902) {
                return super.onTransact(code, data, reply, flags);
            } else {
                reply.writeString(DESCRIPTOR);
                return true;
            }
        }

        private static class Proxy implements IMiuiServiceManager {
            public static IMiuiServiceManager sDefaultImpl;
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

            public IBinder getService(String name) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(name);
                    if (!this.mRemote.transact(1, _data, _reply, 0) && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().getService(name);
                    }
                    _reply.readException();
                    IBinder _result = _reply.readStrongBinder();
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void addService(String name, IBinder service) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(name);
                    _data.writeStrongBinder(service);
                    if (this.mRemote.transact(2, _data, _reply, 0) || Stub.getDefaultImpl() == null) {
                        _reply.readException();
                        _reply.recycle();
                        _data.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().addService(name, service);
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public String[] listServices() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (!this.mRemote.transact(3, _data, _reply, 0) && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().listServices();
                    }
                    _reply.readException();
                    String[] _result = _reply.createStringArray();
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }
        }

        public static boolean setDefaultImpl(IMiuiServiceManager impl) {
            if (Proxy.sDefaultImpl != null || impl == null) {
                return false;
            }
            Proxy.sDefaultImpl = impl;
            return true;
        }

        public static IMiuiServiceManager getDefaultImpl() {
            return Proxy.sDefaultImpl;
        }
    }
}
