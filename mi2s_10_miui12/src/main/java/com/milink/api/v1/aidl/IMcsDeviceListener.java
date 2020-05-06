package com.milink.api.v1.aidl;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

public interface IMcsDeviceListener extends IInterface {
    void onDeviceFound(String str, String str2, String str3) throws RemoteException;

    void onDeviceFound2(String str, String str2, String str3, String str4, String str5, String str6) throws RemoteException;

    void onDeviceLost(String str) throws RemoteException;

    public static class Default implements IMcsDeviceListener {
        public void onDeviceFound(String deviceId, String name, String type) throws RemoteException {
        }

        public void onDeviceFound2(String deviceId, String name, String type, String p2pMac, String wifiMac, String lastConnectTime) throws RemoteException {
        }

        public void onDeviceLost(String deviceId) throws RemoteException {
        }

        public IBinder asBinder() {
            return null;
        }
    }

    public static abstract class Stub extends Binder implements IMcsDeviceListener {
        private static final String DESCRIPTOR = "com.milink.api.v1.aidl.IMcsDeviceListener";
        static final int TRANSACTION_onDeviceFound = 1;
        static final int TRANSACTION_onDeviceFound2 = 2;
        static final int TRANSACTION_onDeviceLost = 3;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IMcsDeviceListener asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin == null || !(iin instanceof IMcsDeviceListener)) {
                return new Proxy(obj);
            }
            return (IMcsDeviceListener) iin;
        }

        public IBinder asBinder() {
            return this;
        }

        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            int i = code;
            Parcel parcel = data;
            if (i == 1) {
                Parcel parcel2 = reply;
                parcel.enforceInterface(DESCRIPTOR);
                onDeviceFound(data.readString(), data.readString(), data.readString());
                reply.writeNoException();
                return true;
            } else if (i == 2) {
                Parcel parcel3 = reply;
                parcel.enforceInterface(DESCRIPTOR);
                onDeviceFound2(data.readString(), data.readString(), data.readString(), data.readString(), data.readString(), data.readString());
                reply.writeNoException();
                return true;
            } else if (i == 3) {
                Parcel parcel4 = reply;
                parcel.enforceInterface(DESCRIPTOR);
                onDeviceLost(data.readString());
                reply.writeNoException();
                return true;
            } else if (i != 1598968902) {
                return super.onTransact(code, data, reply, flags);
            } else {
                reply.writeString(DESCRIPTOR);
                return true;
            }
        }

        private static class Proxy implements IMcsDeviceListener {
            public static IMcsDeviceListener sDefaultImpl;
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

            public void onDeviceFound(String deviceId, String name, String type) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(deviceId);
                    _data.writeString(name);
                    _data.writeString(type);
                    if (this.mRemote.transact(1, _data, _reply, 0) || Stub.getDefaultImpl() == null) {
                        _reply.readException();
                        _reply.recycle();
                        _data.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().onDeviceFound(deviceId, name, type);
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void onDeviceFound2(String deviceId, String name, String type, String p2pMac, String wifiMac, String lastConnectTime) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    try {
                        _data.writeString(deviceId);
                    } catch (Throwable th) {
                        th = th;
                        String str = name;
                        String str2 = type;
                        String str3 = p2pMac;
                        String str4 = wifiMac;
                        String str5 = lastConnectTime;
                        _reply.recycle();
                        _data.recycle();
                        throw th;
                    }
                    try {
                        _data.writeString(name);
                        try {
                            _data.writeString(type);
                            try {
                                _data.writeString(p2pMac);
                            } catch (Throwable th2) {
                                th = th2;
                                String str42 = wifiMac;
                                String str52 = lastConnectTime;
                                _reply.recycle();
                                _data.recycle();
                                throw th;
                            }
                        } catch (Throwable th3) {
                            th = th3;
                            String str32 = p2pMac;
                            String str422 = wifiMac;
                            String str522 = lastConnectTime;
                            _reply.recycle();
                            _data.recycle();
                            throw th;
                        }
                    } catch (Throwable th4) {
                        th = th4;
                        String str22 = type;
                        String str322 = p2pMac;
                        String str4222 = wifiMac;
                        String str5222 = lastConnectTime;
                        _reply.recycle();
                        _data.recycle();
                        throw th;
                    }
                    try {
                        _data.writeString(wifiMac);
                        try {
                            _data.writeString(lastConnectTime);
                            if (this.mRemote.transact(2, _data, _reply, 0) || Stub.getDefaultImpl() == null) {
                                _reply.readException();
                                _reply.recycle();
                                _data.recycle();
                                return;
                            }
                            Stub.getDefaultImpl().onDeviceFound2(deviceId, name, type, p2pMac, wifiMac, lastConnectTime);
                            _reply.recycle();
                            _data.recycle();
                        } catch (Throwable th5) {
                            th = th5;
                            _reply.recycle();
                            _data.recycle();
                            throw th;
                        }
                    } catch (Throwable th6) {
                        th = th6;
                        String str52222 = lastConnectTime;
                        _reply.recycle();
                        _data.recycle();
                        throw th;
                    }
                } catch (Throwable th7) {
                    th = th7;
                    String str6 = deviceId;
                    String str7 = name;
                    String str222 = type;
                    String str3222 = p2pMac;
                    String str42222 = wifiMac;
                    String str522222 = lastConnectTime;
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            public void onDeviceLost(String deviceId) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(deviceId);
                    if (this.mRemote.transact(3, _data, _reply, 0) || Stub.getDefaultImpl() == null) {
                        _reply.readException();
                        _reply.recycle();
                        _data.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().onDeviceLost(deviceId);
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }
        }

        public static boolean setDefaultImpl(IMcsDeviceListener impl) {
            if (Proxy.sDefaultImpl != null || impl == null) {
                return false;
            }
            Proxy.sDefaultImpl = impl;
            return true;
        }

        public static IMcsDeviceListener getDefaultImpl() {
            return Proxy.sDefaultImpl;
        }
    }
}
