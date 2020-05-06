package com.android.internal.net;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import com.android.internal.net.IOemNetdUnsolicitedEventListener;

public interface IOemNetd extends IInterface {
    boolean addMiuiFirewallSharedUid(int i) throws RemoteException;

    boolean enableIptablesRestore(boolean z) throws RemoteException;

    boolean enableLimitter(boolean z) throws RemoteException;

    boolean enableQos(boolean z) throws RemoteException;

    boolean enableRps(String str, boolean z) throws RemoteException;

    boolean enableWmmer(boolean z) throws RemoteException;

    boolean initGmsChain(String str, int i, String str2) throws RemoteException;

    boolean isAlive() throws RemoteException;

    boolean listenUidDataActivity(int i, int i2, int i3, int i4, boolean z) throws RemoteException;

    void notifyFirewallBlocked(int i, String str) throws RemoteException;

    void registerOemUnsolicitedEventListener(IOemNetdUnsolicitedEventListener iOemNetdUnsolicitedEventListener) throws RemoteException;

    boolean setCurrentNetworkState(int i) throws RemoteException;

    void setGmsBlockerEnable(int i, boolean z) throws RemoteException;

    boolean setGmsChainState(String str, boolean z) throws RemoteException;

    boolean setLimit(boolean z, long j) throws RemoteException;

    boolean setMiuiFirewallRule(String str, int i, int i2, int i3) throws RemoteException;

    void setPidForPackage(String str, int i, int i2) throws RemoteException;

    boolean setQos(int i, int i2, int i3, boolean z) throws RemoteException;

    boolean updateIface(String str) throws RemoteException;

    boolean updateWmm(int i, int i2) throws RemoteException;

    boolean whiteListUid(int i, boolean z) throws RemoteException;

    public static class Default implements IOemNetd {
        public boolean isAlive() throws RemoteException {
            return false;
        }

        public boolean enableWmmer(boolean enabled) throws RemoteException {
            return false;
        }

        public boolean enableLimitter(boolean enabled) throws RemoteException {
            return false;
        }

        public boolean updateWmm(int uid, int wmm) throws RemoteException {
            return false;
        }

        public boolean whiteListUid(int uid, boolean add) throws RemoteException {
            return false;
        }

        public boolean setLimit(boolean enabled, long rate) throws RemoteException {
            return false;
        }

        public boolean enableIptablesRestore(boolean enabled) throws RemoteException {
            return false;
        }

        public boolean listenUidDataActivity(int protocol, int uid, int label, int timeout, boolean listen) throws RemoteException {
            return false;
        }

        public boolean updateIface(String iface) throws RemoteException {
            return false;
        }

        public boolean addMiuiFirewallSharedUid(int uid) throws RemoteException {
            return false;
        }

        public boolean setMiuiFirewallRule(String packageName, int uid, int rule, int type) throws RemoteException {
            return false;
        }

        public boolean setCurrentNetworkState(int state) throws RemoteException {
            return false;
        }

        public boolean enableRps(String iface, boolean enable) throws RemoteException {
            return false;
        }

        public void notifyFirewallBlocked(int code, String packageName) throws RemoteException {
        }

        public void setGmsBlockerEnable(int uid, boolean enable) throws RemoteException {
        }

        public boolean initGmsChain(String name, int uid, String rule) throws RemoteException {
            return false;
        }

        public boolean setGmsChainState(String name, boolean enable) throws RemoteException {
            return false;
        }

        public boolean enableQos(boolean enabled) throws RemoteException {
            return false;
        }

        public boolean setQos(int protocol, int uid, int tos, boolean add) throws RemoteException {
            return false;
        }

        public void setPidForPackage(String packageName, int pid, int uid) throws RemoteException {
        }

        public void registerOemUnsolicitedEventListener(IOemNetdUnsolicitedEventListener listener) throws RemoteException {
        }

        public IBinder asBinder() {
            return null;
        }
    }

    public static abstract class Stub extends Binder implements IOemNetd {
        private static final String DESCRIPTOR = "com.android.internal.net.IOemNetd";
        static final int TRANSACTION_addMiuiFirewallSharedUid = 10;
        static final int TRANSACTION_enableIptablesRestore = 7;
        static final int TRANSACTION_enableLimitter = 3;
        static final int TRANSACTION_enableQos = 18;
        static final int TRANSACTION_enableRps = 13;
        static final int TRANSACTION_enableWmmer = 2;
        static final int TRANSACTION_initGmsChain = 16;
        static final int TRANSACTION_isAlive = 1;
        static final int TRANSACTION_listenUidDataActivity = 8;
        static final int TRANSACTION_notifyFirewallBlocked = 14;
        static final int TRANSACTION_registerOemUnsolicitedEventListener = 21;
        static final int TRANSACTION_setCurrentNetworkState = 12;
        static final int TRANSACTION_setGmsBlockerEnable = 15;
        static final int TRANSACTION_setGmsChainState = 17;
        static final int TRANSACTION_setLimit = 6;
        static final int TRANSACTION_setMiuiFirewallRule = 11;
        static final int TRANSACTION_setPidForPackage = 20;
        static final int TRANSACTION_setQos = 19;
        static final int TRANSACTION_updateIface = 9;
        static final int TRANSACTION_updateWmm = 4;
        static final int TRANSACTION_whiteListUid = 5;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IOemNetd asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin == null || !(iin instanceof IOemNetd)) {
                return new Proxy(obj);
            }
            return (IOemNetd) iin;
        }

        public IBinder asBinder() {
            return this;
        }

        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            int i = code;
            Parcel parcel = data;
            Parcel parcel2 = reply;
            if (i != 1598968902) {
                boolean _arg3 = false;
                switch (i) {
                    case 1:
                        parcel.enforceInterface(DESCRIPTOR);
                        boolean _result = isAlive();
                        reply.writeNoException();
                        parcel2.writeInt(_result);
                        return true;
                    case 2:
                        parcel.enforceInterface(DESCRIPTOR);
                        if (data.readInt() != 0) {
                            _arg3 = true;
                        }
                        boolean _result2 = enableWmmer(_arg3);
                        reply.writeNoException();
                        parcel2.writeInt(_result2);
                        return true;
                    case 3:
                        parcel.enforceInterface(DESCRIPTOR);
                        if (data.readInt() != 0) {
                            _arg3 = true;
                        }
                        boolean _result3 = enableLimitter(_arg3);
                        reply.writeNoException();
                        parcel2.writeInt(_result3);
                        return true;
                    case 4:
                        parcel.enforceInterface(DESCRIPTOR);
                        boolean _result4 = updateWmm(data.readInt(), data.readInt());
                        reply.writeNoException();
                        parcel2.writeInt(_result4);
                        return true;
                    case 5:
                        parcel.enforceInterface(DESCRIPTOR);
                        int _arg0 = data.readInt();
                        if (data.readInt() != 0) {
                            _arg3 = true;
                        }
                        boolean _result5 = whiteListUid(_arg0, _arg3);
                        reply.writeNoException();
                        parcel2.writeInt(_result5);
                        return true;
                    case 6:
                        parcel.enforceInterface(DESCRIPTOR);
                        if (data.readInt() != 0) {
                            _arg3 = true;
                        }
                        boolean _result6 = setLimit(_arg3, data.readLong());
                        reply.writeNoException();
                        parcel2.writeInt(_result6);
                        return true;
                    case 7:
                        parcel.enforceInterface(DESCRIPTOR);
                        if (data.readInt() != 0) {
                            _arg3 = true;
                        }
                        boolean _result7 = enableIptablesRestore(_arg3);
                        reply.writeNoException();
                        parcel2.writeInt(_result7);
                        return true;
                    case 8:
                        parcel.enforceInterface(DESCRIPTOR);
                        boolean _result8 = listenUidDataActivity(data.readInt(), data.readInt(), data.readInt(), data.readInt(), data.readInt() != 0);
                        reply.writeNoException();
                        parcel2.writeInt(_result8);
                        return true;
                    case 9:
                        parcel.enforceInterface(DESCRIPTOR);
                        boolean _result9 = updateIface(data.readString());
                        reply.writeNoException();
                        parcel2.writeInt(_result9);
                        return true;
                    case 10:
                        parcel.enforceInterface(DESCRIPTOR);
                        boolean _result10 = addMiuiFirewallSharedUid(data.readInt());
                        reply.writeNoException();
                        parcel2.writeInt(_result10);
                        return true;
                    case 11:
                        parcel.enforceInterface(DESCRIPTOR);
                        boolean _result11 = setMiuiFirewallRule(data.readString(), data.readInt(), data.readInt(), data.readInt());
                        reply.writeNoException();
                        parcel2.writeInt(_result11);
                        return true;
                    case 12:
                        parcel.enforceInterface(DESCRIPTOR);
                        boolean _result12 = setCurrentNetworkState(data.readInt());
                        reply.writeNoException();
                        parcel2.writeInt(_result12);
                        return true;
                    case 13:
                        parcel.enforceInterface(DESCRIPTOR);
                        String _arg02 = data.readString();
                        if (data.readInt() != 0) {
                            _arg3 = true;
                        }
                        boolean _result13 = enableRps(_arg02, _arg3);
                        reply.writeNoException();
                        parcel2.writeInt(_result13);
                        return true;
                    case 14:
                        parcel.enforceInterface(DESCRIPTOR);
                        notifyFirewallBlocked(data.readInt(), data.readString());
                        reply.writeNoException();
                        return true;
                    case 15:
                        parcel.enforceInterface(DESCRIPTOR);
                        int _arg03 = data.readInt();
                        if (data.readInt() != 0) {
                            _arg3 = true;
                        }
                        setGmsBlockerEnable(_arg03, _arg3);
                        reply.writeNoException();
                        return true;
                    case 16:
                        parcel.enforceInterface(DESCRIPTOR);
                        boolean _result14 = initGmsChain(data.readString(), data.readInt(), data.readString());
                        reply.writeNoException();
                        parcel2.writeInt(_result14);
                        return true;
                    case 17:
                        parcel.enforceInterface(DESCRIPTOR);
                        String _arg04 = data.readString();
                        if (data.readInt() != 0) {
                            _arg3 = true;
                        }
                        boolean _result15 = setGmsChainState(_arg04, _arg3);
                        reply.writeNoException();
                        parcel2.writeInt(_result15);
                        return true;
                    case 18:
                        parcel.enforceInterface(DESCRIPTOR);
                        if (data.readInt() != 0) {
                            _arg3 = true;
                        }
                        boolean _result16 = enableQos(_arg3);
                        reply.writeNoException();
                        parcel2.writeInt(_result16);
                        return true;
                    case 19:
                        parcel.enforceInterface(DESCRIPTOR);
                        int _arg05 = data.readInt();
                        int _arg1 = data.readInt();
                        int _arg2 = data.readInt();
                        if (data.readInt() != 0) {
                            _arg3 = true;
                        }
                        boolean _result17 = setQos(_arg05, _arg1, _arg2, _arg3);
                        reply.writeNoException();
                        parcel2.writeInt(_result17);
                        return true;
                    case 20:
                        parcel.enforceInterface(DESCRIPTOR);
                        setPidForPackage(data.readString(), data.readInt(), data.readInt());
                        reply.writeNoException();
                        return true;
                    case 21:
                        parcel.enforceInterface(DESCRIPTOR);
                        registerOemUnsolicitedEventListener(IOemNetdUnsolicitedEventListener.Stub.asInterface(data.readStrongBinder()));
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

        private static class Proxy implements IOemNetd {
            public static IOemNetd sDefaultImpl;
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

            public boolean enableWmmer(boolean enabled) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    boolean _result = true;
                    _data.writeInt(enabled ? 1 : 0);
                    if (!this.mRemote.transact(2, _data, _reply, 0) && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().enableWmmer(enabled);
                    }
                    _reply.readException();
                    if (_reply.readInt() == 0) {
                        _result = false;
                    }
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public boolean enableLimitter(boolean enabled) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    boolean _result = true;
                    _data.writeInt(enabled ? 1 : 0);
                    if (!this.mRemote.transact(3, _data, _reply, 0) && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().enableLimitter(enabled);
                    }
                    _reply.readException();
                    if (_reply.readInt() == 0) {
                        _result = false;
                    }
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public boolean updateWmm(int uid, int wmm) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(uid);
                    _data.writeInt(wmm);
                    boolean z = false;
                    if (!this.mRemote.transact(4, _data, _reply, 0) && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().updateWmm(uid, wmm);
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

            public boolean whiteListUid(int uid, boolean add) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(uid);
                    boolean _result = true;
                    _data.writeInt(add ? 1 : 0);
                    if (!this.mRemote.transact(5, _data, _reply, 0) && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().whiteListUid(uid, add);
                    }
                    _reply.readException();
                    if (_reply.readInt() == 0) {
                        _result = false;
                    }
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public boolean setLimit(boolean enabled, long rate) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    boolean _result = true;
                    _data.writeInt(enabled ? 1 : 0);
                    _data.writeLong(rate);
                    if (!this.mRemote.transact(6, _data, _reply, 0) && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().setLimit(enabled, rate);
                    }
                    _reply.readException();
                    if (_reply.readInt() == 0) {
                        _result = false;
                    }
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public boolean enableIptablesRestore(boolean enabled) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    boolean _result = true;
                    _data.writeInt(enabled ? 1 : 0);
                    if (!this.mRemote.transact(7, _data, _reply, 0) && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().enableIptablesRestore(enabled);
                    }
                    _reply.readException();
                    if (_reply.readInt() == 0) {
                        _result = false;
                    }
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public boolean listenUidDataActivity(int protocol, int uid, int label, int timeout, boolean listen) throws RemoteException {
                boolean _result;
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    try {
                        _data.writeInt(protocol);
                    } catch (Throwable th) {
                        th = th;
                        int i = uid;
                        int i2 = label;
                        int i3 = timeout;
                        _reply.recycle();
                        _data.recycle();
                        throw th;
                    }
                    try {
                        _data.writeInt(uid);
                    } catch (Throwable th2) {
                        th = th2;
                        int i22 = label;
                        int i32 = timeout;
                        _reply.recycle();
                        _data.recycle();
                        throw th;
                    }
                    try {
                        _data.writeInt(label);
                        try {
                            _data.writeInt(timeout);
                            _result = true;
                            _data.writeInt(listen ? 1 : 0);
                        } catch (Throwable th3) {
                            th = th3;
                            _reply.recycle();
                            _data.recycle();
                            throw th;
                        }
                        try {
                            if (this.mRemote.transact(8, _data, _reply, 0) || Stub.getDefaultImpl() == null) {
                                _reply.readException();
                                if (_reply.readInt() == 0) {
                                    _result = false;
                                }
                                _reply.recycle();
                                _data.recycle();
                                return _result;
                            }
                            boolean listenUidDataActivity = Stub.getDefaultImpl().listenUidDataActivity(protocol, uid, label, timeout, listen);
                            _reply.recycle();
                            _data.recycle();
                            return listenUidDataActivity;
                        } catch (Throwable th4) {
                            th = th4;
                            _reply.recycle();
                            _data.recycle();
                            throw th;
                        }
                    } catch (Throwable th5) {
                        th = th5;
                        int i322 = timeout;
                        _reply.recycle();
                        _data.recycle();
                        throw th;
                    }
                } catch (Throwable th6) {
                    th = th6;
                    int i4 = protocol;
                    int i5 = uid;
                    int i222 = label;
                    int i3222 = timeout;
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            public boolean updateIface(String iface) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(iface);
                    boolean z = false;
                    if (!this.mRemote.transact(9, _data, _reply, 0) && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().updateIface(iface);
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

            public boolean addMiuiFirewallSharedUid(int uid) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(uid);
                    boolean z = false;
                    if (!this.mRemote.transact(10, _data, _reply, 0) && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().addMiuiFirewallSharedUid(uid);
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

            public boolean setMiuiFirewallRule(String packageName, int uid, int rule, int type) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    _data.writeInt(uid);
                    _data.writeInt(rule);
                    _data.writeInt(type);
                    boolean z = false;
                    if (!this.mRemote.transact(11, _data, _reply, 0) && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().setMiuiFirewallRule(packageName, uid, rule, type);
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

            public boolean setCurrentNetworkState(int state) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(state);
                    boolean z = false;
                    if (!this.mRemote.transact(12, _data, _reply, 0) && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().setCurrentNetworkState(state);
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

            public boolean enableRps(String iface, boolean enable) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(iface);
                    boolean _result = true;
                    _data.writeInt(enable ? 1 : 0);
                    if (!this.mRemote.transact(13, _data, _reply, 0) && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().enableRps(iface, enable);
                    }
                    _reply.readException();
                    if (_reply.readInt() == 0) {
                        _result = false;
                    }
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void notifyFirewallBlocked(int code, String packageName) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(code);
                    _data.writeString(packageName);
                    if (this.mRemote.transact(14, _data, _reply, 0) || Stub.getDefaultImpl() == null) {
                        _reply.readException();
                        _reply.recycle();
                        _data.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().notifyFirewallBlocked(code, packageName);
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void setGmsBlockerEnable(int uid, boolean enable) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(uid);
                    _data.writeInt(enable ? 1 : 0);
                    if (this.mRemote.transact(15, _data, _reply, 0) || Stub.getDefaultImpl() == null) {
                        _reply.readException();
                        _reply.recycle();
                        _data.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().setGmsBlockerEnable(uid, enable);
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public boolean initGmsChain(String name, int uid, String rule) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(name);
                    _data.writeInt(uid);
                    _data.writeString(rule);
                    boolean z = false;
                    if (!this.mRemote.transact(16, _data, _reply, 0) && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().initGmsChain(name, uid, rule);
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

            public boolean setGmsChainState(String name, boolean enable) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(name);
                    boolean _result = true;
                    _data.writeInt(enable ? 1 : 0);
                    if (!this.mRemote.transact(17, _data, _reply, 0) && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().setGmsChainState(name, enable);
                    }
                    _reply.readException();
                    if (_reply.readInt() == 0) {
                        _result = false;
                    }
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public boolean enableQos(boolean enabled) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    boolean _result = true;
                    _data.writeInt(enabled ? 1 : 0);
                    if (!this.mRemote.transact(18, _data, _reply, 0) && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().enableQos(enabled);
                    }
                    _reply.readException();
                    if (_reply.readInt() == 0) {
                        _result = false;
                    }
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public boolean setQos(int protocol, int uid, int tos, boolean add) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(protocol);
                    _data.writeInt(uid);
                    _data.writeInt(tos);
                    boolean _result = true;
                    _data.writeInt(add ? 1 : 0);
                    if (!this.mRemote.transact(19, _data, _reply, 0) && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().setQos(protocol, uid, tos, add);
                    }
                    _reply.readException();
                    if (_reply.readInt() == 0) {
                        _result = false;
                    }
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void setPidForPackage(String packageName, int pid, int uid) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    _data.writeInt(pid);
                    _data.writeInt(uid);
                    if (this.mRemote.transact(20, _data, _reply, 0) || Stub.getDefaultImpl() == null) {
                        _reply.readException();
                        _reply.recycle();
                        _data.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().setPidForPackage(packageName, pid, uid);
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void registerOemUnsolicitedEventListener(IOemNetdUnsolicitedEventListener listener) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(listener != null ? listener.asBinder() : null);
                    if (this.mRemote.transact(21, _data, _reply, 0) || Stub.getDefaultImpl() == null) {
                        _reply.readException();
                        _reply.recycle();
                        _data.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().registerOemUnsolicitedEventListener(listener);
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }
        }

        public static boolean setDefaultImpl(IOemNetd impl) {
            if (Proxy.sDefaultImpl != null || impl == null) {
                return false;
            }
            Proxy.sDefaultImpl = impl;
            return true;
        }

        public static IOemNetd getDefaultImpl() {
            return Proxy.sDefaultImpl;
        }
    }
}
