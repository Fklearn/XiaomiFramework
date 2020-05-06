package android.net;

import android.net.metrics.INetdEventListener;
import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

public interface IDnsResolver extends IInterface {
    public static final int DNS_RESOLVER_LOG_DEBUG = 1;
    public static final int DNS_RESOLVER_LOG_ERROR = 4;
    public static final int DNS_RESOLVER_LOG_INFO = 2;
    public static final int DNS_RESOLVER_LOG_VERBOSE = 0;
    public static final int DNS_RESOLVER_LOG_WARNING = 3;
    public static final int RESOLVER_PARAMS_BASE_TIMEOUT_MSEC = 4;
    public static final int RESOLVER_PARAMS_COUNT = 6;
    public static final int RESOLVER_PARAMS_MAX_SAMPLES = 3;
    public static final int RESOLVER_PARAMS_MIN_SAMPLES = 2;
    public static final int RESOLVER_PARAMS_RETRY_COUNT = 5;
    public static final int RESOLVER_PARAMS_SAMPLE_VALIDITY = 0;
    public static final int RESOLVER_PARAMS_SUCCESS_THRESHOLD = 1;
    public static final int RESOLVER_STATS_COUNT = 7;
    public static final int RESOLVER_STATS_ERRORS = 1;
    public static final int RESOLVER_STATS_INTERNAL_ERRORS = 3;
    public static final int RESOLVER_STATS_LAST_SAMPLE_TIME = 5;
    public static final int RESOLVER_STATS_RTT_AVG = 4;
    public static final int RESOLVER_STATS_SUCCESSES = 0;
    public static final int RESOLVER_STATS_TIMEOUTS = 2;
    public static final int RESOLVER_STATS_USABLE = 6;
    public static final int VERSION = 2;

    void createNetworkCache(int i) throws RemoteException;

    void destroyNetworkCache(int i) throws RemoteException;

    int getInterfaceVersion() throws RemoteException;

    String getPrefix64(int i) throws RemoteException;

    void getResolverInfo(int i, String[] strArr, String[] strArr2, String[] strArr3, int[] iArr, int[] iArr2, int[] iArr3) throws RemoteException;

    boolean isAlive() throws RemoteException;

    void registerEventListener(INetdEventListener iNetdEventListener) throws RemoteException;

    void setLogSeverity(int i) throws RemoteException;

    void setResolverConfiguration(ResolverParamsParcel resolverParamsParcel) throws RemoteException;

    void startPrefix64Discovery(int i) throws RemoteException;

    void stopPrefix64Discovery(int i) throws RemoteException;

    public static class Default implements IDnsResolver {
        public boolean isAlive() throws RemoteException {
            return false;
        }

        public void registerEventListener(INetdEventListener listener) throws RemoteException {
        }

        public void setResolverConfiguration(ResolverParamsParcel resolverParams) throws RemoteException {
        }

        public void getResolverInfo(int netId, String[] servers, String[] domains, String[] tlsServers, int[] params, int[] stats, int[] wait_for_pending_req_timeout_count) throws RemoteException {
        }

        public void startPrefix64Discovery(int netId) throws RemoteException {
        }

        public void stopPrefix64Discovery(int netId) throws RemoteException {
        }

        public String getPrefix64(int netId) throws RemoteException {
            return null;
        }

        public void createNetworkCache(int netId) throws RemoteException {
        }

        public void destroyNetworkCache(int netId) throws RemoteException {
        }

        public void setLogSeverity(int logSeverity) throws RemoteException {
        }

        public int getInterfaceVersion() {
            return -1;
        }

        public IBinder asBinder() {
            return null;
        }
    }

    public static abstract class Stub extends Binder implements IDnsResolver {
        private static final String DESCRIPTOR = "android.net.IDnsResolver";
        static final int TRANSACTION_createNetworkCache = 8;
        static final int TRANSACTION_destroyNetworkCache = 9;
        static final int TRANSACTION_getInterfaceVersion = 16777215;
        static final int TRANSACTION_getPrefix64 = 7;
        static final int TRANSACTION_getResolverInfo = 4;
        static final int TRANSACTION_isAlive = 1;
        static final int TRANSACTION_registerEventListener = 2;
        static final int TRANSACTION_setLogSeverity = 10;
        static final int TRANSACTION_setResolverConfiguration = 3;
        static final int TRANSACTION_startPrefix64Discovery = 5;
        static final int TRANSACTION_stopPrefix64Discovery = 6;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IDnsResolver asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin == null || !(iin instanceof IDnsResolver)) {
                return new Proxy(obj);
            }
            return (IDnsResolver) iin;
        }

        public IBinder asBinder() {
            return this;
        }

        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            ResolverParamsParcel _arg0;
            String[] _arg1;
            String[] _arg2;
            String[] _arg3;
            int[] _arg4;
            int[] _arg5;
            int[] _arg6;
            int i = code;
            Parcel parcel = data;
            Parcel parcel2 = reply;
            if (i == TRANSACTION_getInterfaceVersion) {
                parcel.enforceInterface(DESCRIPTOR);
                reply.writeNoException();
                parcel2.writeInt(getInterfaceVersion());
                return true;
            } else if (i != 1598968902) {
                switch (i) {
                    case 1:
                        parcel.enforceInterface(DESCRIPTOR);
                        boolean _result = isAlive();
                        reply.writeNoException();
                        parcel2.writeInt(_result);
                        return true;
                    case 2:
                        parcel.enforceInterface(DESCRIPTOR);
                        registerEventListener(INetdEventListener.Stub.asInterface(data.readStrongBinder()));
                        reply.writeNoException();
                        return true;
                    case 3:
                        parcel.enforceInterface(DESCRIPTOR);
                        if (data.readInt() != 0) {
                            _arg0 = ResolverParamsParcel.CREATOR.createFromParcel(parcel);
                        } else {
                            _arg0 = null;
                        }
                        setResolverConfiguration(_arg0);
                        reply.writeNoException();
                        return true;
                    case 4:
                        parcel.enforceInterface(DESCRIPTOR);
                        int _arg02 = data.readInt();
                        int _arg1_length = data.readInt();
                        if (_arg1_length < 0) {
                            _arg1 = null;
                        } else {
                            _arg1 = new String[_arg1_length];
                        }
                        int _arg2_length = data.readInt();
                        if (_arg2_length < 0) {
                            _arg2 = null;
                        } else {
                            _arg2 = new String[_arg2_length];
                        }
                        int _arg3_length = data.readInt();
                        if (_arg3_length < 0) {
                            _arg3 = null;
                        } else {
                            _arg3 = new String[_arg3_length];
                        }
                        int _arg4_length = data.readInt();
                        if (_arg4_length < 0) {
                            _arg4 = null;
                        } else {
                            _arg4 = new int[_arg4_length];
                        }
                        int _arg5_length = data.readInt();
                        if (_arg5_length < 0) {
                            _arg5 = null;
                        } else {
                            _arg5 = new int[_arg5_length];
                        }
                        int _arg6_length = data.readInt();
                        if (_arg6_length < 0) {
                            int i2 = _arg5_length;
                            _arg6 = null;
                        } else {
                            int i3 = _arg5_length;
                            _arg6 = new int[_arg6_length];
                        }
                        int[] _arg62 = _arg6;
                        int[] _arg42 = _arg4;
                        int i4 = _arg4_length;
                        String[] _arg32 = _arg3;
                        int i5 = _arg3_length;
                        int i6 = _arg6_length;
                        int i7 = _arg2_length;
                        int i8 = _arg02;
                        getResolverInfo(_arg02, _arg1, _arg2, _arg32, _arg42, _arg5, _arg62);
                        reply.writeNoException();
                        parcel2.writeStringArray(_arg1);
                        parcel2.writeStringArray(_arg2);
                        parcel2.writeStringArray(_arg32);
                        parcel2.writeIntArray(_arg42);
                        parcel2.writeIntArray(_arg5);
                        parcel2.writeIntArray(_arg62);
                        return true;
                    case 5:
                        parcel.enforceInterface(DESCRIPTOR);
                        startPrefix64Discovery(data.readInt());
                        reply.writeNoException();
                        return true;
                    case 6:
                        parcel.enforceInterface(DESCRIPTOR);
                        stopPrefix64Discovery(data.readInt());
                        reply.writeNoException();
                        return true;
                    case 7:
                        parcel.enforceInterface(DESCRIPTOR);
                        String _result2 = getPrefix64(data.readInt());
                        reply.writeNoException();
                        parcel2.writeString(_result2);
                        return true;
                    case 8:
                        parcel.enforceInterface(DESCRIPTOR);
                        createNetworkCache(data.readInt());
                        reply.writeNoException();
                        return true;
                    case 9:
                        parcel.enforceInterface(DESCRIPTOR);
                        destroyNetworkCache(data.readInt());
                        reply.writeNoException();
                        return true;
                    case 10:
                        parcel.enforceInterface(DESCRIPTOR);
                        setLogSeverity(data.readInt());
                        reply.writeNoException();
                        return true;
                    default:
                        return super.onTransact(code, data, reply, flags);
                }
            } else {
                parcel2.writeString(DESCRIPTOR);
                return true;
            }
        }

        private static class Proxy implements IDnsResolver {
            public static IDnsResolver sDefaultImpl;
            private int mCachedVersion = -1;
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

            public boolean isAlive() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    boolean z = false;
                    if (!this.mRemote.transact(1, _data, _reply, 0) && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().isAlive();
                    }
                    _reply.readException();
                    if (_reply.readInt() != 0) {
                        z = true;
                    }
                    boolean _result = z;
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void registerEventListener(INetdEventListener listener) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(listener != null ? listener.asBinder() : null);
                    if (this.mRemote.transact(2, _data, _reply, 0) || Stub.getDefaultImpl() == null) {
                        _reply.readException();
                        _reply.recycle();
                        _data.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().registerEventListener(listener);
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void setResolverConfiguration(ResolverParamsParcel resolverParams) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (resolverParams != null) {
                        _data.writeInt(1);
                        resolverParams.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    if (this.mRemote.transact(3, _data, _reply, 0) || Stub.getDefaultImpl() == null) {
                        _reply.readException();
                        _reply.recycle();
                        _data.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().setResolverConfiguration(resolverParams);
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void getResolverInfo(int netId, String[] servers, String[] domains, String[] tlsServers, int[] params, int[] stats, int[] wait_for_pending_req_timeout_count) throws RemoteException {
                Parcel _reply;
                String[] strArr = servers;
                String[] strArr2 = domains;
                String[] strArr3 = tlsServers;
                int[] iArr = params;
                int[] iArr2 = stats;
                int[] iArr3 = wait_for_pending_req_timeout_count;
                Parcel _data = Parcel.obtain();
                Parcel _reply2 = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(netId);
                    if (strArr == null) {
                        try {
                            _data.writeInt(-1);
                        } catch (Throwable th) {
                            th = th;
                            _reply = _reply2;
                        }
                    } else {
                        _data.writeInt(strArr.length);
                    }
                    if (strArr2 == null) {
                        _data.writeInt(-1);
                    } else {
                        _data.writeInt(strArr2.length);
                    }
                    if (strArr3 == null) {
                        _data.writeInt(-1);
                    } else {
                        _data.writeInt(strArr3.length);
                    }
                    if (iArr == null) {
                        _data.writeInt(-1);
                    } else {
                        _data.writeInt(iArr.length);
                    }
                    if (iArr2 == null) {
                        _data.writeInt(-1);
                    } else {
                        _data.writeInt(iArr2.length);
                    }
                    if (iArr3 == null) {
                        _data.writeInt(-1);
                    } else {
                        _data.writeInt(iArr3.length);
                    }
                    if (!this.mRemote.transact(4, _data, _reply2, 0)) {
                        try {
                            if (Stub.getDefaultImpl() != null) {
                                Parcel _reply3 = _reply2;
                                try {
                                    Stub.getDefaultImpl().getResolverInfo(netId, servers, domains, tlsServers, params, stats, wait_for_pending_req_timeout_count);
                                    _reply3.recycle();
                                    _data.recycle();
                                    return;
                                } catch (Throwable th2) {
                                    th = th2;
                                    _reply = _reply3;
                                    _reply.recycle();
                                    _data.recycle();
                                    throw th;
                                }
                            }
                        } catch (Throwable th3) {
                            th = th3;
                            _reply = _reply2;
                            _reply.recycle();
                            _data.recycle();
                            throw th;
                        }
                    }
                    Parcel _reply4 = _reply2;
                    try {
                        _reply4.readException();
                        _reply = _reply4;
                        try {
                            _reply.readStringArray(strArr);
                            _reply.readStringArray(strArr2);
                            _reply.readStringArray(strArr3);
                            _reply.readIntArray(iArr);
                            _reply.readIntArray(iArr2);
                            _reply.readIntArray(iArr3);
                            _reply.recycle();
                            _data.recycle();
                        } catch (Throwable th4) {
                            th = th4;
                            _reply.recycle();
                            _data.recycle();
                            throw th;
                        }
                    } catch (Throwable th5) {
                        th = th5;
                        _reply = _reply4;
                        _reply.recycle();
                        _data.recycle();
                        throw th;
                    }
                } catch (Throwable th6) {
                    th = th6;
                    _reply = _reply2;
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            public void startPrefix64Discovery(int netId) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(netId);
                    if (this.mRemote.transact(5, _data, _reply, 0) || Stub.getDefaultImpl() == null) {
                        _reply.readException();
                        _reply.recycle();
                        _data.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().startPrefix64Discovery(netId);
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void stopPrefix64Discovery(int netId) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(netId);
                    if (this.mRemote.transact(6, _data, _reply, 0) || Stub.getDefaultImpl() == null) {
                        _reply.readException();
                        _reply.recycle();
                        _data.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().stopPrefix64Discovery(netId);
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public String getPrefix64(int netId) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(netId);
                    if (!this.mRemote.transact(7, _data, _reply, 0) && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().getPrefix64(netId);
                    }
                    _reply.readException();
                    String _result = _reply.readString();
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void createNetworkCache(int netId) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(netId);
                    if (this.mRemote.transact(8, _data, _reply, 0) || Stub.getDefaultImpl() == null) {
                        _reply.readException();
                        _reply.recycle();
                        _data.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().createNetworkCache(netId);
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void destroyNetworkCache(int netId) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(netId);
                    if (this.mRemote.transact(9, _data, _reply, 0) || Stub.getDefaultImpl() == null) {
                        _reply.readException();
                        _reply.recycle();
                        _data.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().destroyNetworkCache(netId);
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void setLogSeverity(int logSeverity) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(logSeverity);
                    if (this.mRemote.transact(10, _data, _reply, 0) || Stub.getDefaultImpl() == null) {
                        _reply.readException();
                        _reply.recycle();
                        _data.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().setLogSeverity(logSeverity);
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public int getInterfaceVersion() throws RemoteException {
                if (this.mCachedVersion == -1) {
                    Parcel data = Parcel.obtain();
                    Parcel reply = Parcel.obtain();
                    try {
                        data.writeInterfaceToken(Stub.DESCRIPTOR);
                        this.mRemote.transact(Stub.TRANSACTION_getInterfaceVersion, data, reply, 0);
                        reply.readException();
                        this.mCachedVersion = reply.readInt();
                    } finally {
                        reply.recycle();
                        data.recycle();
                    }
                }
                return this.mCachedVersion;
            }
        }

        public static boolean setDefaultImpl(IDnsResolver impl) {
            if (Proxy.sDefaultImpl != null || impl == null) {
                return false;
            }
            Proxy.sDefaultImpl = impl;
            return true;
        }

        public static IDnsResolver getDefaultImpl() {
            return Proxy.sDefaultImpl;
        }
    }
}
