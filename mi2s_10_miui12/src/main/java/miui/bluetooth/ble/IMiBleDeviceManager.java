package miui.bluetooth.ble;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.ParcelUuid;
import android.os.RemoteException;
import java.util.List;
import java.util.Map;
import miui.bluetooth.ble.IBleEventCallback;
import miui.bluetooth.ble.IScanDeviceCallback;

public interface IMiBleDeviceManager extends IInterface {
    boolean deleteSettings(String str) throws RemoteException;

    List<String> getBoundDevices() throws RemoteException;

    Map getDeviceSettings(String str) throws RemoteException;

    int getDeviceType(String str) throws RemoteException;

    String getRegisterAppForBleEvent(String str, int i) throws RemoteException;

    ScanResult getScanResult(String str) throws RemoteException;

    int getServiceVersion() throws RemoteException;

    int getSettingInteger(String str, String str2) throws RemoteException;

    String getSettingString(String str, String str2) throws RemoteException;

    boolean registerAppForBleEvent(String str, int i) throws RemoteException;

    boolean registerBleEventListener(String str, int i, IBleEventCallback iBleEventCallback) throws RemoteException;

    boolean setSettingInteger(String str, String str2, int i) throws RemoteException;

    boolean setSettingString(String str, String str2, String str3) throws RemoteException;

    boolean setToken(String str, byte[] bArr) throws RemoteException;

    boolean startScanDevice(IBinder iBinder, ParcelUuid parcelUuid, int i, IScanDeviceCallback iScanDeviceCallback) throws RemoteException;

    void stopScanDevice(ParcelUuid parcelUuid) throws RemoteException;

    boolean unregisterAppForBleEvent(String str, int i) throws RemoteException;

    boolean unregisterBleEventListener(String str, int i, IBleEventCallback iBleEventCallback) throws RemoteException;

    public static class Default implements IMiBleDeviceManager {
        public boolean setSettingString(String device, String setting, String value) throws RemoteException {
            return false;
        }

        public String getSettingString(String device, String setting) throws RemoteException {
            return null;
        }

        public boolean setSettingInteger(String device, String setting, int value) throws RemoteException {
            return false;
        }

        public int getSettingInteger(String device, String setting) throws RemoteException {
            return 0;
        }

        public Map getDeviceSettings(String device) throws RemoteException {
            return null;
        }

        public boolean deleteSettings(String device) throws RemoteException {
            return false;
        }

        public int getDeviceType(String device) throws RemoteException {
            return 0;
        }

        public boolean startScanDevice(IBinder token, ParcelUuid clientId, int property, IScanDeviceCallback callback) throws RemoteException {
            return false;
        }

        public void stopScanDevice(ParcelUuid clientId) throws RemoteException {
        }

        public boolean registerBleEventListener(String device, int event, IBleEventCallback callback) throws RemoteException {
            return false;
        }

        public boolean unregisterBleEventListener(String device, int event, IBleEventCallback callback) throws RemoteException {
            return false;
        }

        public int getServiceVersion() throws RemoteException {
            return 0;
        }

        public List<String> getBoundDevices() throws RemoteException {
            return null;
        }

        public boolean registerAppForBleEvent(String device, int event) throws RemoteException {
            return false;
        }

        public boolean unregisterAppForBleEvent(String device, int event) throws RemoteException {
            return false;
        }

        public String getRegisterAppForBleEvent(String device, int event) throws RemoteException {
            return null;
        }

        public ScanResult getScanResult(String device) throws RemoteException {
            return null;
        }

        public boolean setToken(String device, byte[] token) throws RemoteException {
            return false;
        }

        public IBinder asBinder() {
            return null;
        }
    }

    public static abstract class Stub extends Binder implements IMiBleDeviceManager {
        private static final String DESCRIPTOR = "miui.bluetooth.ble.IMiBleDeviceManager";
        static final int TRANSACTION_deleteSettings = 6;
        static final int TRANSACTION_getBoundDevices = 13;
        static final int TRANSACTION_getDeviceSettings = 5;
        static final int TRANSACTION_getDeviceType = 7;
        static final int TRANSACTION_getRegisterAppForBleEvent = 16;
        static final int TRANSACTION_getScanResult = 17;
        static final int TRANSACTION_getServiceVersion = 12;
        static final int TRANSACTION_getSettingInteger = 4;
        static final int TRANSACTION_getSettingString = 2;
        static final int TRANSACTION_registerAppForBleEvent = 14;
        static final int TRANSACTION_registerBleEventListener = 10;
        static final int TRANSACTION_setSettingInteger = 3;
        static final int TRANSACTION_setSettingString = 1;
        static final int TRANSACTION_setToken = 18;
        static final int TRANSACTION_startScanDevice = 8;
        static final int TRANSACTION_stopScanDevice = 9;
        static final int TRANSACTION_unregisterAppForBleEvent = 15;
        static final int TRANSACTION_unregisterBleEventListener = 11;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IMiBleDeviceManager asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin == null || !(iin instanceof IMiBleDeviceManager)) {
                return new Proxy(obj);
            }
            return (IMiBleDeviceManager) iin;
        }

        public IBinder asBinder() {
            return this;
        }

        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            ParcelUuid _arg1;
            ParcelUuid _arg0;
            if (code != 1598968902) {
                switch (code) {
                    case 1:
                        data.enforceInterface(DESCRIPTOR);
                        boolean _result = setSettingString(data.readString(), data.readString(), data.readString());
                        reply.writeNoException();
                        reply.writeInt(_result);
                        return true;
                    case 2:
                        data.enforceInterface(DESCRIPTOR);
                        String _result2 = getSettingString(data.readString(), data.readString());
                        reply.writeNoException();
                        reply.writeString(_result2);
                        return true;
                    case 3:
                        data.enforceInterface(DESCRIPTOR);
                        boolean _result3 = setSettingInteger(data.readString(), data.readString(), data.readInt());
                        reply.writeNoException();
                        reply.writeInt(_result3);
                        return true;
                    case 4:
                        data.enforceInterface(DESCRIPTOR);
                        int _result4 = getSettingInteger(data.readString(), data.readString());
                        reply.writeNoException();
                        reply.writeInt(_result4);
                        return true;
                    case 5:
                        data.enforceInterface(DESCRIPTOR);
                        Map _result5 = getDeviceSettings(data.readString());
                        reply.writeNoException();
                        reply.writeMap(_result5);
                        return true;
                    case 6:
                        data.enforceInterface(DESCRIPTOR);
                        boolean _result6 = deleteSettings(data.readString());
                        reply.writeNoException();
                        reply.writeInt(_result6);
                        return true;
                    case 7:
                        data.enforceInterface(DESCRIPTOR);
                        int _result7 = getDeviceType(data.readString());
                        reply.writeNoException();
                        reply.writeInt(_result7);
                        return true;
                    case 8:
                        data.enforceInterface(DESCRIPTOR);
                        IBinder _arg02 = data.readStrongBinder();
                        if (data.readInt() != 0) {
                            _arg1 = (ParcelUuid) ParcelUuid.CREATOR.createFromParcel(data);
                        } else {
                            _arg1 = null;
                        }
                        boolean _result8 = startScanDevice(_arg02, _arg1, data.readInt(), IScanDeviceCallback.Stub.asInterface(data.readStrongBinder()));
                        reply.writeNoException();
                        reply.writeInt(_result8);
                        return true;
                    case 9:
                        data.enforceInterface(DESCRIPTOR);
                        if (data.readInt() != 0) {
                            _arg0 = (ParcelUuid) ParcelUuid.CREATOR.createFromParcel(data);
                        } else {
                            _arg0 = null;
                        }
                        stopScanDevice(_arg0);
                        reply.writeNoException();
                        return true;
                    case 10:
                        data.enforceInterface(DESCRIPTOR);
                        boolean _result9 = registerBleEventListener(data.readString(), data.readInt(), IBleEventCallback.Stub.asInterface(data.readStrongBinder()));
                        reply.writeNoException();
                        reply.writeInt(_result9);
                        return true;
                    case 11:
                        data.enforceInterface(DESCRIPTOR);
                        boolean _result10 = unregisterBleEventListener(data.readString(), data.readInt(), IBleEventCallback.Stub.asInterface(data.readStrongBinder()));
                        reply.writeNoException();
                        reply.writeInt(_result10);
                        return true;
                    case 12:
                        data.enforceInterface(DESCRIPTOR);
                        int _result11 = getServiceVersion();
                        reply.writeNoException();
                        reply.writeInt(_result11);
                        return true;
                    case 13:
                        data.enforceInterface(DESCRIPTOR);
                        List<String> _result12 = getBoundDevices();
                        reply.writeNoException();
                        reply.writeStringList(_result12);
                        return true;
                    case 14:
                        data.enforceInterface(DESCRIPTOR);
                        boolean _result13 = registerAppForBleEvent(data.readString(), data.readInt());
                        reply.writeNoException();
                        reply.writeInt(_result13);
                        return true;
                    case 15:
                        data.enforceInterface(DESCRIPTOR);
                        boolean _result14 = unregisterAppForBleEvent(data.readString(), data.readInt());
                        reply.writeNoException();
                        reply.writeInt(_result14);
                        return true;
                    case 16:
                        data.enforceInterface(DESCRIPTOR);
                        String _result15 = getRegisterAppForBleEvent(data.readString(), data.readInt());
                        reply.writeNoException();
                        reply.writeString(_result15);
                        return true;
                    case 17:
                        data.enforceInterface(DESCRIPTOR);
                        ScanResult _result16 = getScanResult(data.readString());
                        reply.writeNoException();
                        if (_result16 != null) {
                            reply.writeInt(1);
                            _result16.writeToParcel(reply, 1);
                        } else {
                            reply.writeInt(0);
                        }
                        return true;
                    case 18:
                        data.enforceInterface(DESCRIPTOR);
                        boolean _result17 = setToken(data.readString(), data.createByteArray());
                        reply.writeNoException();
                        reply.writeInt(_result17);
                        return true;
                    default:
                        return super.onTransact(code, data, reply, flags);
                }
            } else {
                reply.writeString(DESCRIPTOR);
                return true;
            }
        }

        private static class Proxy implements IMiBleDeviceManager {
            public static IMiBleDeviceManager sDefaultImpl;
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

            public boolean setSettingString(String device, String setting, String value) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(device);
                    _data.writeString(setting);
                    _data.writeString(value);
                    boolean z = false;
                    if (!this.mRemote.transact(1, _data, _reply, 0) && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().setSettingString(device, setting, value);
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

            public String getSettingString(String device, String setting) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(device);
                    _data.writeString(setting);
                    if (!this.mRemote.transact(2, _data, _reply, 0) && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().getSettingString(device, setting);
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

            public boolean setSettingInteger(String device, String setting, int value) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(device);
                    _data.writeString(setting);
                    _data.writeInt(value);
                    boolean z = false;
                    if (!this.mRemote.transact(3, _data, _reply, 0) && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().setSettingInteger(device, setting, value);
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

            public int getSettingInteger(String device, String setting) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(device);
                    _data.writeString(setting);
                    if (!this.mRemote.transact(4, _data, _reply, 0) && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().getSettingInteger(device, setting);
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

            public Map getDeviceSettings(String device) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(device);
                    if (!this.mRemote.transact(5, _data, _reply, 0) && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().getDeviceSettings(device);
                    }
                    _reply.readException();
                    Map _result = _reply.readHashMap(getClass().getClassLoader());
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public boolean deleteSettings(String device) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(device);
                    boolean z = false;
                    if (!this.mRemote.transact(6, _data, _reply, 0) && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().deleteSettings(device);
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

            public int getDeviceType(String device) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(device);
                    if (!this.mRemote.transact(7, _data, _reply, 0) && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().getDeviceType(device);
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

            public boolean startScanDevice(IBinder token, ParcelUuid clientId, int property, IScanDeviceCallback callback) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(token);
                    boolean _result = true;
                    if (clientId != null) {
                        _data.writeInt(1);
                        clientId.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeInt(property);
                    _data.writeStrongBinder(callback != null ? callback.asBinder() : null);
                    if (!this.mRemote.transact(8, _data, _reply, 0) && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().startScanDevice(token, clientId, property, callback);
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

            public void stopScanDevice(ParcelUuid clientId) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (clientId != null) {
                        _data.writeInt(1);
                        clientId.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    if (this.mRemote.transact(9, _data, _reply, 0) || Stub.getDefaultImpl() == null) {
                        _reply.readException();
                        _reply.recycle();
                        _data.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().stopScanDevice(clientId);
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public boolean registerBleEventListener(String device, int event, IBleEventCallback callback) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(device);
                    _data.writeInt(event);
                    _data.writeStrongBinder(callback != null ? callback.asBinder() : null);
                    boolean z = false;
                    if (!this.mRemote.transact(10, _data, _reply, 0) && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().registerBleEventListener(device, event, callback);
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

            public boolean unregisterBleEventListener(String device, int event, IBleEventCallback callback) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(device);
                    _data.writeInt(event);
                    _data.writeStrongBinder(callback != null ? callback.asBinder() : null);
                    boolean z = false;
                    if (!this.mRemote.transact(11, _data, _reply, 0) && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().unregisterBleEventListener(device, event, callback);
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

            public int getServiceVersion() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (!this.mRemote.transact(12, _data, _reply, 0) && Stub.getDefaultImpl() != null) {
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

            public List<String> getBoundDevices() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (!this.mRemote.transact(13, _data, _reply, 0) && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().getBoundDevices();
                    }
                    _reply.readException();
                    List<String> _result = _reply.createStringArrayList();
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public boolean registerAppForBleEvent(String device, int event) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(device);
                    _data.writeInt(event);
                    boolean z = false;
                    if (!this.mRemote.transact(14, _data, _reply, 0) && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().registerAppForBleEvent(device, event);
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

            public boolean unregisterAppForBleEvent(String device, int event) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(device);
                    _data.writeInt(event);
                    boolean z = false;
                    if (!this.mRemote.transact(15, _data, _reply, 0) && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().unregisterAppForBleEvent(device, event);
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

            public String getRegisterAppForBleEvent(String device, int event) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(device);
                    _data.writeInt(event);
                    if (!this.mRemote.transact(16, _data, _reply, 0) && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().getRegisterAppForBleEvent(device, event);
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

            public ScanResult getScanResult(String device) throws RemoteException {
                ScanResult _result;
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(device);
                    if (!this.mRemote.transact(17, _data, _reply, 0) && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().getScanResult(device);
                    }
                    _reply.readException();
                    if (_reply.readInt() != 0) {
                        _result = ScanResult.CREATOR.createFromParcel(_reply);
                    } else {
                        _result = null;
                    }
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public boolean setToken(String device, byte[] token) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(device);
                    _data.writeByteArray(token);
                    boolean z = false;
                    if (!this.mRemote.transact(18, _data, _reply, 0) && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().setToken(device, token);
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
        }

        public static boolean setDefaultImpl(IMiBleDeviceManager impl) {
            if (Proxy.sDefaultImpl != null || impl == null) {
                return false;
            }
            Proxy.sDefaultImpl = impl;
            return true;
        }

        public static IMiBleDeviceManager getDefaultImpl() {
            return Proxy.sDefaultImpl;
        }
    }
}
