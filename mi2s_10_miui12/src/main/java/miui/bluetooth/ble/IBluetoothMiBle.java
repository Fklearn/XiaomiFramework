package miui.bluetooth.ble;

import android.bluetooth.BluetoothDevice;
import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.ParcelUuid;
import android.os.RemoteException;
import java.util.List;
import miui.bluetooth.ble.IBluetoothMiBleCallback;
import miui.bluetooth.ble.IBluetoothMiBlePropertyCallback;

public interface IBluetoothMiBle extends IInterface {
    boolean authenticate(String str, ParcelUuid parcelUuid) throws RemoteException;

    boolean authorize(String str, ParcelUuid parcelUuid, String str2) throws RemoteException;

    void connect(String str, ParcelUuid parcelUuid) throws RemoteException;

    void disconnect(String str, ParcelUuid parcelUuid) throws RemoteException;

    byte[] encrypt(String str, ParcelUuid parcelUuid, byte[] bArr) throws RemoteException;

    List<BluetoothDevice> getConnectedDevices() throws RemoteException;

    byte[] getProperty(String str, ParcelUuid parcelUuid, int i) throws RemoteException;

    int getRssi(String str, ParcelUuid parcelUuid) throws RemoteException;

    int getServiceVersion() throws RemoteException;

    boolean isConnected(String str) throws RemoteException;

    void registerClient(IBinder iBinder, String str, ParcelUuid parcelUuid, IBluetoothMiBleCallback iBluetoothMiBleCallback) throws RemoteException;

    boolean registerPropertyCallback(String str, ParcelUuid parcelUuid, int i, IBluetoothMiBlePropertyCallback iBluetoothMiBlePropertyCallback) throws RemoteException;

    boolean setEncryptionKey(String str, ParcelUuid parcelUuid, byte[] bArr) throws RemoteException;

    boolean setProperty(String str, ParcelUuid parcelUuid, int i, byte[] bArr) throws RemoteException;

    boolean setRssiThreshold(String str, ParcelUuid parcelUuid, int i) throws RemoteException;

    boolean supportProperty(String str, int i) throws RemoteException;

    void unregisterClient(IBinder iBinder, String str, ParcelUuid parcelUuid) throws RemoteException;

    boolean unregisterPropertyCallback(String str, ParcelUuid parcelUuid, int i, IBluetoothMiBlePropertyCallback iBluetoothMiBlePropertyCallback) throws RemoteException;

    public static class Default implements IBluetoothMiBle {
        public void registerClient(IBinder token, String device, ParcelUuid clientId, IBluetoothMiBleCallback callback) throws RemoteException {
        }

        public void unregisterClient(IBinder token, String device, ParcelUuid clientId) throws RemoteException {
        }

        public boolean isConnected(String device) throws RemoteException {
            return false;
        }

        public void connect(String device, ParcelUuid clientId) throws RemoteException {
        }

        public void disconnect(String device, ParcelUuid clientId) throws RemoteException {
        }

        public List<BluetoothDevice> getConnectedDevices() throws RemoteException {
            return null;
        }

        public int getRssi(String device, ParcelUuid clientId) throws RemoteException {
            return 0;
        }

        public boolean supportProperty(String device, int property) throws RemoteException {
            return false;
        }

        public boolean registerPropertyCallback(String device, ParcelUuid clientId, int property, IBluetoothMiBlePropertyCallback callback) throws RemoteException {
            return false;
        }

        public boolean unregisterPropertyCallback(String device, ParcelUuid clientId, int property, IBluetoothMiBlePropertyCallback callback) throws RemoteException {
            return false;
        }

        public boolean setProperty(String device, ParcelUuid clientId, int property, byte[] data) throws RemoteException {
            return false;
        }

        public byte[] getProperty(String device, ParcelUuid clientId, int property) throws RemoteException {
            return null;
        }

        public boolean authorize(String device, ParcelUuid clientId, String key) throws RemoteException {
            return false;
        }

        public boolean setRssiThreshold(String device, ParcelUuid clientId, int rssi) throws RemoteException {
            return false;
        }

        public boolean authenticate(String device, ParcelUuid clientId) throws RemoteException {
            return false;
        }

        public boolean setEncryptionKey(String device, ParcelUuid clientId, byte[] key) throws RemoteException {
            return false;
        }

        public byte[] encrypt(String device, ParcelUuid clientId, byte[] text) throws RemoteException {
            return null;
        }

        public int getServiceVersion() throws RemoteException {
            return 0;
        }

        public IBinder asBinder() {
            return null;
        }
    }

    public static abstract class Stub extends Binder implements IBluetoothMiBle {
        private static final String DESCRIPTOR = "miui.bluetooth.ble.IBluetoothMiBle";
        static final int TRANSACTION_authenticate = 15;
        static final int TRANSACTION_authorize = 13;
        static final int TRANSACTION_connect = 4;
        static final int TRANSACTION_disconnect = 5;
        static final int TRANSACTION_encrypt = 17;
        static final int TRANSACTION_getConnectedDevices = 6;
        static final int TRANSACTION_getProperty = 12;
        static final int TRANSACTION_getRssi = 7;
        static final int TRANSACTION_getServiceVersion = 18;
        static final int TRANSACTION_isConnected = 3;
        static final int TRANSACTION_registerClient = 1;
        static final int TRANSACTION_registerPropertyCallback = 9;
        static final int TRANSACTION_setEncryptionKey = 16;
        static final int TRANSACTION_setProperty = 11;
        static final int TRANSACTION_setRssiThreshold = 14;
        static final int TRANSACTION_supportProperty = 8;
        static final int TRANSACTION_unregisterClient = 2;
        static final int TRANSACTION_unregisterPropertyCallback = 10;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IBluetoothMiBle asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin == null || !(iin instanceof IBluetoothMiBle)) {
                return new Proxy(obj);
            }
            return (IBluetoothMiBle) iin;
        }

        public IBinder asBinder() {
            return this;
        }

        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            ParcelUuid _arg2;
            ParcelUuid _arg22;
            ParcelUuid _arg1;
            ParcelUuid _arg12;
            ParcelUuid _arg13;
            ParcelUuid _arg14;
            ParcelUuid _arg15;
            ParcelUuid _arg16;
            ParcelUuid _arg17;
            ParcelUuid _arg18;
            ParcelUuid _arg19;
            ParcelUuid _arg110;
            ParcelUuid _arg111;
            ParcelUuid _arg112;
            if (code != 1598968902) {
                switch (code) {
                    case 1:
                        data.enforceInterface(DESCRIPTOR);
                        IBinder _arg0 = data.readStrongBinder();
                        String _arg113 = data.readString();
                        if (data.readInt() != 0) {
                            _arg2 = (ParcelUuid) ParcelUuid.CREATOR.createFromParcel(data);
                        } else {
                            _arg2 = null;
                        }
                        registerClient(_arg0, _arg113, _arg2, IBluetoothMiBleCallback.Stub.asInterface(data.readStrongBinder()));
                        reply.writeNoException();
                        return true;
                    case 2:
                        data.enforceInterface(DESCRIPTOR);
                        IBinder _arg02 = data.readStrongBinder();
                        String _arg114 = data.readString();
                        if (data.readInt() != 0) {
                            _arg22 = (ParcelUuid) ParcelUuid.CREATOR.createFromParcel(data);
                        } else {
                            _arg22 = null;
                        }
                        unregisterClient(_arg02, _arg114, _arg22);
                        reply.writeNoException();
                        return true;
                    case 3:
                        data.enforceInterface(DESCRIPTOR);
                        boolean _result = isConnected(data.readString());
                        reply.writeNoException();
                        reply.writeInt(_result);
                        return true;
                    case 4:
                        data.enforceInterface(DESCRIPTOR);
                        String _arg03 = data.readString();
                        if (data.readInt() != 0) {
                            _arg1 = (ParcelUuid) ParcelUuid.CREATOR.createFromParcel(data);
                        } else {
                            _arg1 = null;
                        }
                        connect(_arg03, _arg1);
                        reply.writeNoException();
                        return true;
                    case 5:
                        data.enforceInterface(DESCRIPTOR);
                        String _arg04 = data.readString();
                        if (data.readInt() != 0) {
                            _arg12 = (ParcelUuid) ParcelUuid.CREATOR.createFromParcel(data);
                        } else {
                            _arg12 = null;
                        }
                        disconnect(_arg04, _arg12);
                        reply.writeNoException();
                        return true;
                    case 6:
                        data.enforceInterface(DESCRIPTOR);
                        List<BluetoothDevice> _result2 = getConnectedDevices();
                        reply.writeNoException();
                        reply.writeTypedList(_result2);
                        return true;
                    case 7:
                        data.enforceInterface(DESCRIPTOR);
                        String _arg05 = data.readString();
                        if (data.readInt() != 0) {
                            _arg13 = (ParcelUuid) ParcelUuid.CREATOR.createFromParcel(data);
                        } else {
                            _arg13 = null;
                        }
                        int _result3 = getRssi(_arg05, _arg13);
                        reply.writeNoException();
                        reply.writeInt(_result3);
                        return true;
                    case 8:
                        data.enforceInterface(DESCRIPTOR);
                        boolean _result4 = supportProperty(data.readString(), data.readInt());
                        reply.writeNoException();
                        reply.writeInt(_result4);
                        return true;
                    case 9:
                        data.enforceInterface(DESCRIPTOR);
                        String _arg06 = data.readString();
                        if (data.readInt() != 0) {
                            _arg14 = (ParcelUuid) ParcelUuid.CREATOR.createFromParcel(data);
                        } else {
                            _arg14 = null;
                        }
                        boolean _result5 = registerPropertyCallback(_arg06, _arg14, data.readInt(), IBluetoothMiBlePropertyCallback.Stub.asInterface(data.readStrongBinder()));
                        reply.writeNoException();
                        reply.writeInt(_result5);
                        return true;
                    case 10:
                        data.enforceInterface(DESCRIPTOR);
                        String _arg07 = data.readString();
                        if (data.readInt() != 0) {
                            _arg15 = (ParcelUuid) ParcelUuid.CREATOR.createFromParcel(data);
                        } else {
                            _arg15 = null;
                        }
                        boolean _result6 = unregisterPropertyCallback(_arg07, _arg15, data.readInt(), IBluetoothMiBlePropertyCallback.Stub.asInterface(data.readStrongBinder()));
                        reply.writeNoException();
                        reply.writeInt(_result6);
                        return true;
                    case 11:
                        data.enforceInterface(DESCRIPTOR);
                        String _arg08 = data.readString();
                        if (data.readInt() != 0) {
                            _arg16 = (ParcelUuid) ParcelUuid.CREATOR.createFromParcel(data);
                        } else {
                            _arg16 = null;
                        }
                        boolean _result7 = setProperty(_arg08, _arg16, data.readInt(), data.createByteArray());
                        reply.writeNoException();
                        reply.writeInt(_result7);
                        return true;
                    case 12:
                        data.enforceInterface(DESCRIPTOR);
                        String _arg09 = data.readString();
                        if (data.readInt() != 0) {
                            _arg17 = (ParcelUuid) ParcelUuid.CREATOR.createFromParcel(data);
                        } else {
                            _arg17 = null;
                        }
                        byte[] _result8 = getProperty(_arg09, _arg17, data.readInt());
                        reply.writeNoException();
                        reply.writeByteArray(_result8);
                        return true;
                    case 13:
                        data.enforceInterface(DESCRIPTOR);
                        String _arg010 = data.readString();
                        if (data.readInt() != 0) {
                            _arg18 = (ParcelUuid) ParcelUuid.CREATOR.createFromParcel(data);
                        } else {
                            _arg18 = null;
                        }
                        boolean _result9 = authorize(_arg010, _arg18, data.readString());
                        reply.writeNoException();
                        reply.writeInt(_result9);
                        return true;
                    case 14:
                        data.enforceInterface(DESCRIPTOR);
                        String _arg011 = data.readString();
                        if (data.readInt() != 0) {
                            _arg19 = (ParcelUuid) ParcelUuid.CREATOR.createFromParcel(data);
                        } else {
                            _arg19 = null;
                        }
                        boolean _result10 = setRssiThreshold(_arg011, _arg19, data.readInt());
                        reply.writeNoException();
                        reply.writeInt(_result10);
                        return true;
                    case 15:
                        data.enforceInterface(DESCRIPTOR);
                        String _arg012 = data.readString();
                        if (data.readInt() != 0) {
                            _arg110 = (ParcelUuid) ParcelUuid.CREATOR.createFromParcel(data);
                        } else {
                            _arg110 = null;
                        }
                        boolean _result11 = authenticate(_arg012, _arg110);
                        reply.writeNoException();
                        reply.writeInt(_result11);
                        return true;
                    case 16:
                        data.enforceInterface(DESCRIPTOR);
                        String _arg013 = data.readString();
                        if (data.readInt() != 0) {
                            _arg111 = (ParcelUuid) ParcelUuid.CREATOR.createFromParcel(data);
                        } else {
                            _arg111 = null;
                        }
                        boolean _result12 = setEncryptionKey(_arg013, _arg111, data.createByteArray());
                        reply.writeNoException();
                        reply.writeInt(_result12);
                        return true;
                    case 17:
                        data.enforceInterface(DESCRIPTOR);
                        String _arg014 = data.readString();
                        if (data.readInt() != 0) {
                            _arg112 = (ParcelUuid) ParcelUuid.CREATOR.createFromParcel(data);
                        } else {
                            _arg112 = null;
                        }
                        byte[] _result13 = encrypt(_arg014, _arg112, data.createByteArray());
                        reply.writeNoException();
                        reply.writeByteArray(_result13);
                        return true;
                    case 18:
                        data.enforceInterface(DESCRIPTOR);
                        int _result14 = getServiceVersion();
                        reply.writeNoException();
                        reply.writeInt(_result14);
                        return true;
                    default:
                        return super.onTransact(code, data, reply, flags);
                }
            } else {
                reply.writeString(DESCRIPTOR);
                return true;
            }
        }

        private static class Proxy implements IBluetoothMiBle {
            public static IBluetoothMiBle sDefaultImpl;
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

            public void registerClient(IBinder token, String device, ParcelUuid clientId, IBluetoothMiBleCallback callback) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(token);
                    _data.writeString(device);
                    if (clientId != null) {
                        _data.writeInt(1);
                        clientId.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeStrongBinder(callback != null ? callback.asBinder() : null);
                    if (this.mRemote.transact(1, _data, _reply, 0) || Stub.getDefaultImpl() == null) {
                        _reply.readException();
                        _reply.recycle();
                        _data.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().registerClient(token, device, clientId, callback);
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void unregisterClient(IBinder token, String device, ParcelUuid clientId) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(token);
                    _data.writeString(device);
                    if (clientId != null) {
                        _data.writeInt(1);
                        clientId.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    if (this.mRemote.transact(2, _data, _reply, 0) || Stub.getDefaultImpl() == null) {
                        _reply.readException();
                        _reply.recycle();
                        _data.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().unregisterClient(token, device, clientId);
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public boolean isConnected(String device) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(device);
                    boolean z = false;
                    if (!this.mRemote.transact(3, _data, _reply, 0) && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().isConnected(device);
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

            public void connect(String device, ParcelUuid clientId) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(device);
                    if (clientId != null) {
                        _data.writeInt(1);
                        clientId.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    if (this.mRemote.transact(4, _data, _reply, 0) || Stub.getDefaultImpl() == null) {
                        _reply.readException();
                        _reply.recycle();
                        _data.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().connect(device, clientId);
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void disconnect(String device, ParcelUuid clientId) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(device);
                    if (clientId != null) {
                        _data.writeInt(1);
                        clientId.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    if (this.mRemote.transact(5, _data, _reply, 0) || Stub.getDefaultImpl() == null) {
                        _reply.readException();
                        _reply.recycle();
                        _data.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().disconnect(device, clientId);
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public List<BluetoothDevice> getConnectedDevices() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (!this.mRemote.transact(6, _data, _reply, 0) && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().getConnectedDevices();
                    }
                    _reply.readException();
                    List<BluetoothDevice> _result = _reply.createTypedArrayList(BluetoothDevice.CREATOR);
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public int getRssi(String device, ParcelUuid clientId) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(device);
                    if (clientId != null) {
                        _data.writeInt(1);
                        clientId.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    if (!this.mRemote.transact(7, _data, _reply, 0) && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().getRssi(device, clientId);
                    }
                    _reply.readException();
                    int _result = _reply.readInt();
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public boolean supportProperty(String device, int property) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(device);
                    _data.writeInt(property);
                    boolean z = false;
                    if (!this.mRemote.transact(8, _data, _reply, 0) && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().supportProperty(device, property);
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

            public boolean registerPropertyCallback(String device, ParcelUuid clientId, int property, IBluetoothMiBlePropertyCallback callback) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(device);
                    boolean _result = true;
                    if (clientId != null) {
                        _data.writeInt(1);
                        clientId.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeInt(property);
                    _data.writeStrongBinder(callback != null ? callback.asBinder() : null);
                    if (!this.mRemote.transact(9, _data, _reply, 0) && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().registerPropertyCallback(device, clientId, property, callback);
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

            public boolean unregisterPropertyCallback(String device, ParcelUuid clientId, int property, IBluetoothMiBlePropertyCallback callback) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(device);
                    boolean _result = true;
                    if (clientId != null) {
                        _data.writeInt(1);
                        clientId.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeInt(property);
                    _data.writeStrongBinder(callback != null ? callback.asBinder() : null);
                    if (!this.mRemote.transact(10, _data, _reply, 0) && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().unregisterPropertyCallback(device, clientId, property, callback);
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

            public boolean setProperty(String device, ParcelUuid clientId, int property, byte[] data) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(device);
                    boolean _result = true;
                    if (clientId != null) {
                        _data.writeInt(1);
                        clientId.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeInt(property);
                    _data.writeByteArray(data);
                    if (!this.mRemote.transact(11, _data, _reply, 0) && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().setProperty(device, clientId, property, data);
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

            public byte[] getProperty(String device, ParcelUuid clientId, int property) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(device);
                    if (clientId != null) {
                        _data.writeInt(1);
                        clientId.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeInt(property);
                    if (!this.mRemote.transact(12, _data, _reply, 0) && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().getProperty(device, clientId, property);
                    }
                    _reply.readException();
                    byte[] _result = _reply.createByteArray();
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public boolean authorize(String device, ParcelUuid clientId, String key) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(device);
                    boolean _result = true;
                    if (clientId != null) {
                        _data.writeInt(1);
                        clientId.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeString(key);
                    if (!this.mRemote.transact(13, _data, _reply, 0) && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().authorize(device, clientId, key);
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

            public boolean setRssiThreshold(String device, ParcelUuid clientId, int rssi) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(device);
                    boolean _result = true;
                    if (clientId != null) {
                        _data.writeInt(1);
                        clientId.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeInt(rssi);
                    if (!this.mRemote.transact(14, _data, _reply, 0) && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().setRssiThreshold(device, clientId, rssi);
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

            public boolean authenticate(String device, ParcelUuid clientId) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(device);
                    boolean _result = true;
                    if (clientId != null) {
                        _data.writeInt(1);
                        clientId.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    if (!this.mRemote.transact(15, _data, _reply, 0) && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().authenticate(device, clientId);
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

            public boolean setEncryptionKey(String device, ParcelUuid clientId, byte[] key) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(device);
                    boolean _result = true;
                    if (clientId != null) {
                        _data.writeInt(1);
                        clientId.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeByteArray(key);
                    if (!this.mRemote.transact(16, _data, _reply, 0) && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().setEncryptionKey(device, clientId, key);
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

            public byte[] encrypt(String device, ParcelUuid clientId, byte[] text) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(device);
                    if (clientId != null) {
                        _data.writeInt(1);
                        clientId.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeByteArray(text);
                    if (!this.mRemote.transact(17, _data, _reply, 0) && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().encrypt(device, clientId, text);
                    }
                    _reply.readException();
                    byte[] _result = _reply.createByteArray();
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public int getServiceVersion() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (!this.mRemote.transact(18, _data, _reply, 0) && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().getServiceVersion();
                    }
                    _reply.readException();
                    int _result = _reply.readInt();
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }
        }

        public static boolean setDefaultImpl(IBluetoothMiBle impl) {
            if (Proxy.sDefaultImpl != null || impl == null) {
                return false;
            }
            Proxy.sDefaultImpl = impl;
            return true;
        }

        public static IBluetoothMiBle getDefaultImpl() {
            return Proxy.sDefaultImpl;
        }
    }
}
