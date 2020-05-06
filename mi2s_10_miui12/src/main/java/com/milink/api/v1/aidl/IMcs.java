package com.milink.api.v1.aidl;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import com.milink.api.v1.aidl.IMcsDataSource;
import com.milink.api.v1.aidl.IMcsDelegate;
import com.milink.api.v1.aidl.IMcsDeviceListener;
import com.milink.api.v1.aidl.IMcsMiracastConnectCallback;
import com.milink.api.v1.aidl.IMcsOpenMiracastListener;
import com.milink.api.v1.aidl.IMcsScanListCallback;

public interface IMcs extends IInterface {
    int connect(String str, int i) throws RemoteException;

    int connectWifiDisplay(String str, String str2, String str3, IMcsMiracastConnectCallback iMcsMiracastConnectCallback) throws RemoteException;

    int disconnect() throws RemoteException;

    int disconnectWifiDisplay() throws RemoteException;

    void dismissScanList() throws RemoteException;

    int getPlaybackDuration() throws RemoteException;

    int getPlaybackProgress() throws RemoteException;

    int getPlaybackRate() throws RemoteException;

    int getVolume() throws RemoteException;

    int rotatePhoto(String str, boolean z, float f) throws RemoteException;

    void selectDevice(String str, String str2, String str3) throws RemoteException;

    void setDataSource(IMcsDataSource iMcsDataSource) throws RemoteException;

    void setDelegate(IMcsDelegate iMcsDelegate) throws RemoteException;

    void setDeviceListener(IMcsDeviceListener iMcsDeviceListener) throws RemoteException;

    void setDeviceName(String str) throws RemoteException;

    int setPlaybackProgress(int i) throws RemoteException;

    int setPlaybackRate(int i) throws RemoteException;

    int setVolume(int i) throws RemoteException;

    int show(String str) throws RemoteException;

    void showScanList(IMcsScanListCallback iMcsScanListCallback, int i) throws RemoteException;

    int startPlayAudio(String str, String str2, int i, double d) throws RemoteException;

    int startPlayAudioEx(String str, String str2, String str3, int i, double d) throws RemoteException;

    int startPlayVideo(String str, String str2, int i, double d) throws RemoteException;

    int startPlayVideoEx(String str, String str2, String str3, int i, double d) throws RemoteException;

    int startShow() throws RemoteException;

    int startSlideshow(int i, boolean z) throws RemoteException;

    int startTvMiracast(String str, String str2, String str3, String str4, String str5, IMcsOpenMiracastListener iMcsOpenMiracastListener) throws RemoteException;

    boolean startWifiDisplayScan() throws RemoteException;

    int stopPlay() throws RemoteException;

    int stopShow() throws RemoteException;

    int stopSlideshow() throws RemoteException;

    boolean stopWifiDisplayScan() throws RemoteException;

    void unsetDataSource(IMcsDataSource iMcsDataSource) throws RemoteException;

    void unsetDelegate(IMcsDelegate iMcsDelegate) throws RemoteException;

    void unsetDeviceListener(IMcsDeviceListener iMcsDeviceListener) throws RemoteException;

    int zoomPhoto(String str, int i, int i2, int i3, int i4, int i5, int i6, float f) throws RemoteException;

    public static class Default implements IMcs {
        public void setDeviceListener(IMcsDeviceListener listener) throws RemoteException {
        }

        public void unsetDeviceListener(IMcsDeviceListener listener) throws RemoteException {
        }

        public void setDataSource(IMcsDataSource dataSource) throws RemoteException {
        }

        public void unsetDataSource(IMcsDataSource dataSource) throws RemoteException {
        }

        public void setDelegate(IMcsDelegate delegate) throws RemoteException {
        }

        public void unsetDelegate(IMcsDelegate delegate) throws RemoteException {
        }

        public void setDeviceName(String deviceName) throws RemoteException {
        }

        public void showScanList(IMcsScanListCallback callback, int displayFlag) throws RemoteException {
        }

        public void dismissScanList() throws RemoteException {
        }

        public void selectDevice(String deviceId, String deviceName, String deviceType) throws RemoteException {
        }

        public boolean startWifiDisplayScan() throws RemoteException {
            return false;
        }

        public boolean stopWifiDisplayScan() throws RemoteException {
            return false;
        }

        public int connect(String deviceId, int timeout) throws RemoteException {
            return 0;
        }

        public int connectWifiDisplay(String deviceName, String p2pMac, String wifiMac, IMcsMiracastConnectCallback callback) throws RemoteException {
            return 0;
        }

        public int disconnectWifiDisplay() throws RemoteException {
            return 0;
        }

        public int startTvMiracast(String deviceName, String ip, String p2pMac, String wifiMac, String deviceType, IMcsOpenMiracastListener listener) throws RemoteException {
            return 0;
        }

        public int disconnect() throws RemoteException {
            return 0;
        }

        public int startShow() throws RemoteException {
            return 0;
        }

        public int show(String photoUri) throws RemoteException {
            return 0;
        }

        public int zoomPhoto(String photoUri, int x, int y, int screenWidth, int screenHeight, int orgPhotoWidth, int orgPhotoHeight, float scale) throws RemoteException {
            return 0;
        }

        public int stopShow() throws RemoteException {
            return 0;
        }

        public int startSlideshow(int duration, boolean isRecyle) throws RemoteException {
            return 0;
        }

        public int stopSlideshow() throws RemoteException {
            return 0;
        }

        public int startPlayVideo(String url, String title, int iPosition, double dPosition) throws RemoteException {
            return 0;
        }

        public int startPlayAudio(String url, String title, int iPosition, double dPosition) throws RemoteException {
            return 0;
        }

        public int stopPlay() throws RemoteException {
            return 0;
        }

        public int setPlaybackRate(int rate) throws RemoteException {
            return 0;
        }

        public int getPlaybackRate() throws RemoteException {
            return 0;
        }

        public int setPlaybackProgress(int position) throws RemoteException {
            return 0;
        }

        public int getPlaybackProgress() throws RemoteException {
            return 0;
        }

        public int getPlaybackDuration() throws RemoteException {
            return 0;
        }

        public int setVolume(int volume) throws RemoteException {
            return 0;
        }

        public int getVolume() throws RemoteException {
            return 0;
        }

        public int rotatePhoto(String photoUri, boolean flag, float degree) throws RemoteException {
            return 0;
        }

        public int startPlayVideoEx(String url, String title, String extra, int iPosition, double dPosition) throws RemoteException {
            return 0;
        }

        public int startPlayAudioEx(String url, String title, String extra, int iPosition, double dPosition) throws RemoteException {
            return 0;
        }

        public IBinder asBinder() {
            return null;
        }
    }

    public static abstract class Stub extends Binder implements IMcs {
        private static final String DESCRIPTOR = "com.milink.api.v1.aidl.IMcs";
        static final int TRANSACTION_connect = 13;
        static final int TRANSACTION_connectWifiDisplay = 14;
        static final int TRANSACTION_disconnect = 17;
        static final int TRANSACTION_disconnectWifiDisplay = 15;
        static final int TRANSACTION_dismissScanList = 9;
        static final int TRANSACTION_getPlaybackDuration = 31;
        static final int TRANSACTION_getPlaybackProgress = 30;
        static final int TRANSACTION_getPlaybackRate = 28;
        static final int TRANSACTION_getVolume = 33;
        static final int TRANSACTION_rotatePhoto = 34;
        static final int TRANSACTION_selectDevice = 10;
        static final int TRANSACTION_setDataSource = 3;
        static final int TRANSACTION_setDelegate = 5;
        static final int TRANSACTION_setDeviceListener = 1;
        static final int TRANSACTION_setDeviceName = 7;
        static final int TRANSACTION_setPlaybackProgress = 29;
        static final int TRANSACTION_setPlaybackRate = 27;
        static final int TRANSACTION_setVolume = 32;
        static final int TRANSACTION_show = 19;
        static final int TRANSACTION_showScanList = 8;
        static final int TRANSACTION_startPlayAudio = 25;
        static final int TRANSACTION_startPlayAudioEx = 36;
        static final int TRANSACTION_startPlayVideo = 24;
        static final int TRANSACTION_startPlayVideoEx = 35;
        static final int TRANSACTION_startShow = 18;
        static final int TRANSACTION_startSlideshow = 22;
        static final int TRANSACTION_startTvMiracast = 16;
        static final int TRANSACTION_startWifiDisplayScan = 11;
        static final int TRANSACTION_stopPlay = 26;
        static final int TRANSACTION_stopShow = 21;
        static final int TRANSACTION_stopSlideshow = 23;
        static final int TRANSACTION_stopWifiDisplayScan = 12;
        static final int TRANSACTION_unsetDataSource = 4;
        static final int TRANSACTION_unsetDelegate = 6;
        static final int TRANSACTION_unsetDeviceListener = 2;
        static final int TRANSACTION_zoomPhoto = 20;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IMcs asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin == null || !(iin instanceof IMcs)) {
                return new Proxy(obj);
            }
            return (IMcs) iin;
        }

        public IBinder asBinder() {
            return this;
        }

        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            int i = code;
            Parcel parcel = data;
            Parcel parcel2 = reply;
            if (i != 1598968902) {
                boolean _arg1 = false;
                switch (i) {
                    case 1:
                        parcel.enforceInterface(DESCRIPTOR);
                        setDeviceListener(IMcsDeviceListener.Stub.asInterface(data.readStrongBinder()));
                        reply.writeNoException();
                        return true;
                    case 2:
                        parcel.enforceInterface(DESCRIPTOR);
                        unsetDeviceListener(IMcsDeviceListener.Stub.asInterface(data.readStrongBinder()));
                        reply.writeNoException();
                        return true;
                    case 3:
                        parcel.enforceInterface(DESCRIPTOR);
                        setDataSource(IMcsDataSource.Stub.asInterface(data.readStrongBinder()));
                        reply.writeNoException();
                        return true;
                    case 4:
                        parcel.enforceInterface(DESCRIPTOR);
                        unsetDataSource(IMcsDataSource.Stub.asInterface(data.readStrongBinder()));
                        reply.writeNoException();
                        return true;
                    case 5:
                        parcel.enforceInterface(DESCRIPTOR);
                        setDelegate(IMcsDelegate.Stub.asInterface(data.readStrongBinder()));
                        reply.writeNoException();
                        return true;
                    case 6:
                        parcel.enforceInterface(DESCRIPTOR);
                        unsetDelegate(IMcsDelegate.Stub.asInterface(data.readStrongBinder()));
                        reply.writeNoException();
                        return true;
                    case 7:
                        parcel.enforceInterface(DESCRIPTOR);
                        setDeviceName(data.readString());
                        reply.writeNoException();
                        return true;
                    case 8:
                        parcel.enforceInterface(DESCRIPTOR);
                        showScanList(IMcsScanListCallback.Stub.asInterface(data.readStrongBinder()), data.readInt());
                        reply.writeNoException();
                        return true;
                    case 9:
                        parcel.enforceInterface(DESCRIPTOR);
                        dismissScanList();
                        reply.writeNoException();
                        return true;
                    case 10:
                        parcel.enforceInterface(DESCRIPTOR);
                        selectDevice(data.readString(), data.readString(), data.readString());
                        reply.writeNoException();
                        return true;
                    case 11:
                        parcel.enforceInterface(DESCRIPTOR);
                        boolean _result = startWifiDisplayScan();
                        reply.writeNoException();
                        parcel2.writeInt(_result);
                        return true;
                    case 12:
                        parcel.enforceInterface(DESCRIPTOR);
                        boolean _result2 = stopWifiDisplayScan();
                        reply.writeNoException();
                        parcel2.writeInt(_result2);
                        return true;
                    case 13:
                        parcel.enforceInterface(DESCRIPTOR);
                        int _result3 = connect(data.readString(), data.readInt());
                        reply.writeNoException();
                        parcel2.writeInt(_result3);
                        return true;
                    case 14:
                        parcel.enforceInterface(DESCRIPTOR);
                        int _result4 = connectWifiDisplay(data.readString(), data.readString(), data.readString(), IMcsMiracastConnectCallback.Stub.asInterface(data.readStrongBinder()));
                        reply.writeNoException();
                        parcel2.writeInt(_result4);
                        return true;
                    case 15:
                        parcel.enforceInterface(DESCRIPTOR);
                        int _result5 = disconnectWifiDisplay();
                        reply.writeNoException();
                        parcel2.writeInt(_result5);
                        return true;
                    case 16:
                        parcel.enforceInterface(DESCRIPTOR);
                        int _result6 = startTvMiracast(data.readString(), data.readString(), data.readString(), data.readString(), data.readString(), IMcsOpenMiracastListener.Stub.asInterface(data.readStrongBinder()));
                        reply.writeNoException();
                        parcel2.writeInt(_result6);
                        return true;
                    case 17:
                        parcel.enforceInterface(DESCRIPTOR);
                        int _result7 = disconnect();
                        reply.writeNoException();
                        parcel2.writeInt(_result7);
                        return true;
                    case 18:
                        parcel.enforceInterface(DESCRIPTOR);
                        int _result8 = startShow();
                        reply.writeNoException();
                        parcel2.writeInt(_result8);
                        return true;
                    case 19:
                        parcel.enforceInterface(DESCRIPTOR);
                        int _result9 = show(data.readString());
                        reply.writeNoException();
                        parcel2.writeInt(_result9);
                        return true;
                    case 20:
                        parcel.enforceInterface(DESCRIPTOR);
                        int _result10 = zoomPhoto(data.readString(), data.readInt(), data.readInt(), data.readInt(), data.readInt(), data.readInt(), data.readInt(), data.readFloat());
                        reply.writeNoException();
                        parcel2.writeInt(_result10);
                        return true;
                    case 21:
                        parcel.enforceInterface(DESCRIPTOR);
                        int _result11 = stopShow();
                        reply.writeNoException();
                        parcel2.writeInt(_result11);
                        return true;
                    case 22:
                        parcel.enforceInterface(DESCRIPTOR);
                        int _arg0 = data.readInt();
                        if (data.readInt() != 0) {
                            _arg1 = true;
                        }
                        int _result12 = startSlideshow(_arg0, _arg1);
                        reply.writeNoException();
                        parcel2.writeInt(_result12);
                        return true;
                    case 23:
                        parcel.enforceInterface(DESCRIPTOR);
                        int _result13 = stopSlideshow();
                        reply.writeNoException();
                        parcel2.writeInt(_result13);
                        return true;
                    case 24:
                        parcel.enforceInterface(DESCRIPTOR);
                        int _result14 = startPlayVideo(data.readString(), data.readString(), data.readInt(), data.readDouble());
                        reply.writeNoException();
                        parcel2.writeInt(_result14);
                        return true;
                    case 25:
                        parcel.enforceInterface(DESCRIPTOR);
                        int _result15 = startPlayAudio(data.readString(), data.readString(), data.readInt(), data.readDouble());
                        reply.writeNoException();
                        parcel2.writeInt(_result15);
                        return true;
                    case 26:
                        parcel.enforceInterface(DESCRIPTOR);
                        int _result16 = stopPlay();
                        reply.writeNoException();
                        parcel2.writeInt(_result16);
                        return true;
                    case 27:
                        parcel.enforceInterface(DESCRIPTOR);
                        int _result17 = setPlaybackRate(data.readInt());
                        reply.writeNoException();
                        parcel2.writeInt(_result17);
                        return true;
                    case 28:
                        parcel.enforceInterface(DESCRIPTOR);
                        int _result18 = getPlaybackRate();
                        reply.writeNoException();
                        parcel2.writeInt(_result18);
                        return true;
                    case 29:
                        parcel.enforceInterface(DESCRIPTOR);
                        int _result19 = setPlaybackProgress(data.readInt());
                        reply.writeNoException();
                        parcel2.writeInt(_result19);
                        return true;
                    case 30:
                        parcel.enforceInterface(DESCRIPTOR);
                        int _result20 = getPlaybackProgress();
                        reply.writeNoException();
                        parcel2.writeInt(_result20);
                        return true;
                    case 31:
                        parcel.enforceInterface(DESCRIPTOR);
                        int _result21 = getPlaybackDuration();
                        reply.writeNoException();
                        parcel2.writeInt(_result21);
                        return true;
                    case 32:
                        parcel.enforceInterface(DESCRIPTOR);
                        int _result22 = setVolume(data.readInt());
                        reply.writeNoException();
                        parcel2.writeInt(_result22);
                        return true;
                    case 33:
                        parcel.enforceInterface(DESCRIPTOR);
                        int _result23 = getVolume();
                        reply.writeNoException();
                        parcel2.writeInt(_result23);
                        return true;
                    case 34:
                        parcel.enforceInterface(DESCRIPTOR);
                        String _arg02 = data.readString();
                        if (data.readInt() != 0) {
                            _arg1 = true;
                        }
                        int _result24 = rotatePhoto(_arg02, _arg1, data.readFloat());
                        reply.writeNoException();
                        parcel2.writeInt(_result24);
                        return true;
                    case 35:
                        parcel.enforceInterface(DESCRIPTOR);
                        int _result25 = startPlayVideoEx(data.readString(), data.readString(), data.readString(), data.readInt(), data.readDouble());
                        reply.writeNoException();
                        parcel2.writeInt(_result25);
                        return true;
                    case 36:
                        parcel.enforceInterface(DESCRIPTOR);
                        int _result26 = startPlayAudioEx(data.readString(), data.readString(), data.readString(), data.readInt(), data.readDouble());
                        reply.writeNoException();
                        parcel2.writeInt(_result26);
                        return true;
                    default:
                        return super.onTransact(code, data, reply, flags);
                }
            } else {
                parcel2.writeString(DESCRIPTOR);
                return true;
            }
        }

        private static class Proxy implements IMcs {
            public static IMcs sDefaultImpl;
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

            public void setDeviceListener(IMcsDeviceListener listener) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(listener != null ? listener.asBinder() : null);
                    if (this.mRemote.transact(1, _data, _reply, 0) || Stub.getDefaultImpl() == null) {
                        _reply.readException();
                        _reply.recycle();
                        _data.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().setDeviceListener(listener);
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void unsetDeviceListener(IMcsDeviceListener listener) throws RemoteException {
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
                    Stub.getDefaultImpl().unsetDeviceListener(listener);
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void setDataSource(IMcsDataSource dataSource) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(dataSource != null ? dataSource.asBinder() : null);
                    if (this.mRemote.transact(3, _data, _reply, 0) || Stub.getDefaultImpl() == null) {
                        _reply.readException();
                        _reply.recycle();
                        _data.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().setDataSource(dataSource);
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void unsetDataSource(IMcsDataSource dataSource) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(dataSource != null ? dataSource.asBinder() : null);
                    if (this.mRemote.transact(4, _data, _reply, 0) || Stub.getDefaultImpl() == null) {
                        _reply.readException();
                        _reply.recycle();
                        _data.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().unsetDataSource(dataSource);
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void setDelegate(IMcsDelegate delegate) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(delegate != null ? delegate.asBinder() : null);
                    if (this.mRemote.transact(5, _data, _reply, 0) || Stub.getDefaultImpl() == null) {
                        _reply.readException();
                        _reply.recycle();
                        _data.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().setDelegate(delegate);
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void unsetDelegate(IMcsDelegate delegate) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(delegate != null ? delegate.asBinder() : null);
                    if (this.mRemote.transact(6, _data, _reply, 0) || Stub.getDefaultImpl() == null) {
                        _reply.readException();
                        _reply.recycle();
                        _data.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().unsetDelegate(delegate);
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void setDeviceName(String deviceName) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(deviceName);
                    if (this.mRemote.transact(7, _data, _reply, 0) || Stub.getDefaultImpl() == null) {
                        _reply.readException();
                        _reply.recycle();
                        _data.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().setDeviceName(deviceName);
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void showScanList(IMcsScanListCallback callback, int displayFlag) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(callback != null ? callback.asBinder() : null);
                    _data.writeInt(displayFlag);
                    if (this.mRemote.transact(8, _data, _reply, 0) || Stub.getDefaultImpl() == null) {
                        _reply.readException();
                        _reply.recycle();
                        _data.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().showScanList(callback, displayFlag);
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void dismissScanList() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (this.mRemote.transact(9, _data, _reply, 0) || Stub.getDefaultImpl() == null) {
                        _reply.readException();
                        _reply.recycle();
                        _data.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().dismissScanList();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void selectDevice(String deviceId, String deviceName, String deviceType) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(deviceId);
                    _data.writeString(deviceName);
                    _data.writeString(deviceType);
                    if (this.mRemote.transact(10, _data, _reply, 0) || Stub.getDefaultImpl() == null) {
                        _reply.readException();
                        _reply.recycle();
                        _data.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().selectDevice(deviceId, deviceName, deviceType);
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public boolean startWifiDisplayScan() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    boolean z = false;
                    if (!this.mRemote.transact(11, _data, _reply, 0) && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().startWifiDisplayScan();
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

            public boolean stopWifiDisplayScan() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    boolean z = false;
                    if (!this.mRemote.transact(12, _data, _reply, 0) && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().stopWifiDisplayScan();
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

            public int connect(String deviceId, int timeout) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(deviceId);
                    _data.writeInt(timeout);
                    if (!this.mRemote.transact(13, _data, _reply, 0) && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().connect(deviceId, timeout);
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

            public int connectWifiDisplay(String deviceName, String p2pMac, String wifiMac, IMcsMiracastConnectCallback callback) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(deviceName);
                    _data.writeString(p2pMac);
                    _data.writeString(wifiMac);
                    _data.writeStrongBinder(callback != null ? callback.asBinder() : null);
                    if (!this.mRemote.transact(14, _data, _reply, 0) && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().connectWifiDisplay(deviceName, p2pMac, wifiMac, callback);
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

            public int disconnectWifiDisplay() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (!this.mRemote.transact(15, _data, _reply, 0) && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().disconnectWifiDisplay();
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

            public int startTvMiracast(String deviceName, String ip, String p2pMac, String wifiMac, String deviceType, IMcsOpenMiracastListener listener) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    try {
                        _data.writeString(deviceName);
                    } catch (Throwable th) {
                        th = th;
                        String str = ip;
                        String str2 = p2pMac;
                        String str3 = wifiMac;
                        String str4 = deviceType;
                        _reply.recycle();
                        _data.recycle();
                        throw th;
                    }
                    try {
                        _data.writeString(ip);
                        try {
                            _data.writeString(p2pMac);
                            try {
                                _data.writeString(wifiMac);
                            } catch (Throwable th2) {
                                th = th2;
                                String str42 = deviceType;
                                _reply.recycle();
                                _data.recycle();
                                throw th;
                            }
                        } catch (Throwable th3) {
                            th = th3;
                            String str32 = wifiMac;
                            String str422 = deviceType;
                            _reply.recycle();
                            _data.recycle();
                            throw th;
                        }
                    } catch (Throwable th4) {
                        th = th4;
                        String str22 = p2pMac;
                        String str322 = wifiMac;
                        String str4222 = deviceType;
                        _reply.recycle();
                        _data.recycle();
                        throw th;
                    }
                    try {
                        _data.writeString(deviceType);
                        _data.writeStrongBinder(listener != null ? listener.asBinder() : null);
                        try {
                            if (this.mRemote.transact(16, _data, _reply, 0) || Stub.getDefaultImpl() == null) {
                                _reply.readException();
                                int _result = _reply.readInt();
                                _reply.recycle();
                                _data.recycle();
                                return _result;
                            }
                            int startTvMiracast = Stub.getDefaultImpl().startTvMiracast(deviceName, ip, p2pMac, wifiMac, deviceType, listener);
                            _reply.recycle();
                            _data.recycle();
                            return startTvMiracast;
                        } catch (Throwable th5) {
                            th = th5;
                            _reply.recycle();
                            _data.recycle();
                            throw th;
                        }
                    } catch (Throwable th6) {
                        th = th6;
                        _reply.recycle();
                        _data.recycle();
                        throw th;
                    }
                } catch (Throwable th7) {
                    th = th7;
                    String str5 = deviceName;
                    String str6 = ip;
                    String str222 = p2pMac;
                    String str3222 = wifiMac;
                    String str42222 = deviceType;
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            public int disconnect() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (!this.mRemote.transact(17, _data, _reply, 0) && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().disconnect();
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

            public int startShow() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (!this.mRemote.transact(18, _data, _reply, 0) && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().startShow();
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

            public int show(String photoUri) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(photoUri);
                    if (!this.mRemote.transact(19, _data, _reply, 0) && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().show(photoUri);
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

            public int zoomPhoto(String photoUri, int x, int y, int screenWidth, int screenHeight, int orgPhotoWidth, int orgPhotoHeight, float scale) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    try {
                        _data.writeString(photoUri);
                    } catch (Throwable th) {
                        th = th;
                        int i = x;
                        int i2 = y;
                        int i3 = screenWidth;
                        _reply.recycle();
                        _data.recycle();
                        throw th;
                    }
                    try {
                        _data.writeInt(x);
                    } catch (Throwable th2) {
                        th = th2;
                        int i22 = y;
                        int i32 = screenWidth;
                        _reply.recycle();
                        _data.recycle();
                        throw th;
                    }
                    try {
                        _data.writeInt(y);
                        try {
                            _data.writeInt(screenWidth);
                            _data.writeInt(screenHeight);
                            _data.writeInt(orgPhotoWidth);
                            _data.writeInt(orgPhotoHeight);
                            _data.writeFloat(scale);
                            if (this.mRemote.transact(20, _data, _reply, 0) || Stub.getDefaultImpl() == null) {
                                _reply.readException();
                                int _result = _reply.readInt();
                                _reply.recycle();
                                _data.recycle();
                                return _result;
                            }
                            int zoomPhoto = Stub.getDefaultImpl().zoomPhoto(photoUri, x, y, screenWidth, screenHeight, orgPhotoWidth, orgPhotoHeight, scale);
                            _reply.recycle();
                            _data.recycle();
                            return zoomPhoto;
                        } catch (Throwable th3) {
                            th = th3;
                            _reply.recycle();
                            _data.recycle();
                            throw th;
                        }
                    } catch (Throwable th4) {
                        th = th4;
                        int i322 = screenWidth;
                        _reply.recycle();
                        _data.recycle();
                        throw th;
                    }
                } catch (Throwable th5) {
                    th = th5;
                    String str = photoUri;
                    int i4 = x;
                    int i222 = y;
                    int i3222 = screenWidth;
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            public int stopShow() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (!this.mRemote.transact(21, _data, _reply, 0) && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().stopShow();
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

            public int startSlideshow(int duration, boolean isRecyle) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(duration);
                    _data.writeInt(isRecyle ? 1 : 0);
                    if (!this.mRemote.transact(22, _data, _reply, 0) && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().startSlideshow(duration, isRecyle);
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

            public int stopSlideshow() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (!this.mRemote.transact(23, _data, _reply, 0) && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().stopSlideshow();
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

            public int startPlayVideo(String url, String title, int iPosition, double dPosition) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(url);
                    _data.writeString(title);
                    _data.writeInt(iPosition);
                    _data.writeDouble(dPosition);
                    if (!this.mRemote.transact(24, _data, _reply, 0) && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().startPlayVideo(url, title, iPosition, dPosition);
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

            public int startPlayAudio(String url, String title, int iPosition, double dPosition) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(url);
                    _data.writeString(title);
                    _data.writeInt(iPosition);
                    _data.writeDouble(dPosition);
                    if (!this.mRemote.transact(25, _data, _reply, 0) && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().startPlayAudio(url, title, iPosition, dPosition);
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

            public int stopPlay() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (!this.mRemote.transact(26, _data, _reply, 0) && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().stopPlay();
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

            public int setPlaybackRate(int rate) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(rate);
                    if (!this.mRemote.transact(27, _data, _reply, 0) && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().setPlaybackRate(rate);
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

            public int getPlaybackRate() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (!this.mRemote.transact(28, _data, _reply, 0) && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().getPlaybackRate();
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

            public int setPlaybackProgress(int position) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(position);
                    if (!this.mRemote.transact(29, _data, _reply, 0) && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().setPlaybackProgress(position);
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

            public int getPlaybackProgress() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (!this.mRemote.transact(30, _data, _reply, 0) && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().getPlaybackProgress();
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

            public int getPlaybackDuration() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (!this.mRemote.transact(31, _data, _reply, 0) && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().getPlaybackDuration();
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

            public int setVolume(int volume) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(volume);
                    if (!this.mRemote.transact(32, _data, _reply, 0) && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().setVolume(volume);
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

            public int getVolume() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (!this.mRemote.transact(33, _data, _reply, 0) && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().getVolume();
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

            public int rotatePhoto(String photoUri, boolean flag, float degree) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(photoUri);
                    _data.writeInt(flag ? 1 : 0);
                    _data.writeFloat(degree);
                    if (!this.mRemote.transact(34, _data, _reply, 0) && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().rotatePhoto(photoUri, flag, degree);
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

            public int startPlayVideoEx(String url, String title, String extra, int iPosition, double dPosition) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    try {
                        _data.writeString(url);
                    } catch (Throwable th) {
                        th = th;
                        String str = title;
                        String str2 = extra;
                        int i = iPosition;
                        double d = dPosition;
                        _reply.recycle();
                        _data.recycle();
                        throw th;
                    }
                    try {
                        _data.writeString(title);
                    } catch (Throwable th2) {
                        th = th2;
                        String str22 = extra;
                        int i2 = iPosition;
                        double d2 = dPosition;
                        _reply.recycle();
                        _data.recycle();
                        throw th;
                    }
                    try {
                        _data.writeString(extra);
                        try {
                            _data.writeInt(iPosition);
                        } catch (Throwable th3) {
                            th = th3;
                            double d22 = dPosition;
                            _reply.recycle();
                            _data.recycle();
                            throw th;
                        }
                        try {
                            _data.writeDouble(dPosition);
                            if (this.mRemote.transact(35, _data, _reply, 0) || Stub.getDefaultImpl() == null) {
                                _reply.readException();
                                int _result = _reply.readInt();
                                _reply.recycle();
                                _data.recycle();
                                return _result;
                            }
                            int startPlayVideoEx = Stub.getDefaultImpl().startPlayVideoEx(url, title, extra, iPosition, dPosition);
                            _reply.recycle();
                            _data.recycle();
                            return startPlayVideoEx;
                        } catch (Throwable th4) {
                            th = th4;
                            _reply.recycle();
                            _data.recycle();
                            throw th;
                        }
                    } catch (Throwable th5) {
                        th = th5;
                        int i22 = iPosition;
                        double d222 = dPosition;
                        _reply.recycle();
                        _data.recycle();
                        throw th;
                    }
                } catch (Throwable th6) {
                    th = th6;
                    String str3 = url;
                    String str4 = title;
                    String str222 = extra;
                    int i222 = iPosition;
                    double d2222 = dPosition;
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            public int startPlayAudioEx(String url, String title, String extra, int iPosition, double dPosition) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    try {
                        _data.writeString(url);
                    } catch (Throwable th) {
                        th = th;
                        String str = title;
                        String str2 = extra;
                        int i = iPosition;
                        double d = dPosition;
                        _reply.recycle();
                        _data.recycle();
                        throw th;
                    }
                    try {
                        _data.writeString(title);
                    } catch (Throwable th2) {
                        th = th2;
                        String str22 = extra;
                        int i2 = iPosition;
                        double d2 = dPosition;
                        _reply.recycle();
                        _data.recycle();
                        throw th;
                    }
                    try {
                        _data.writeString(extra);
                        try {
                            _data.writeInt(iPosition);
                        } catch (Throwable th3) {
                            th = th3;
                            double d22 = dPosition;
                            _reply.recycle();
                            _data.recycle();
                            throw th;
                        }
                        try {
                            _data.writeDouble(dPosition);
                            if (this.mRemote.transact(36, _data, _reply, 0) || Stub.getDefaultImpl() == null) {
                                _reply.readException();
                                int _result = _reply.readInt();
                                _reply.recycle();
                                _data.recycle();
                                return _result;
                            }
                            int startPlayAudioEx = Stub.getDefaultImpl().startPlayAudioEx(url, title, extra, iPosition, dPosition);
                            _reply.recycle();
                            _data.recycle();
                            return startPlayAudioEx;
                        } catch (Throwable th4) {
                            th = th4;
                            _reply.recycle();
                            _data.recycle();
                            throw th;
                        }
                    } catch (Throwable th5) {
                        th = th5;
                        int i22 = iPosition;
                        double d222 = dPosition;
                        _reply.recycle();
                        _data.recycle();
                        throw th;
                    }
                } catch (Throwable th6) {
                    th = th6;
                    String str3 = url;
                    String str4 = title;
                    String str222 = extra;
                    int i222 = iPosition;
                    double d2222 = dPosition;
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }
        }

        public static boolean setDefaultImpl(IMcs impl) {
            if (Proxy.sDefaultImpl != null || impl == null) {
                return false;
            }
            Proxy.sDefaultImpl = impl;
            return true;
        }

        public static IMcs getDefaultImpl() {
            return Proxy.sDefaultImpl;
        }
    }
}
