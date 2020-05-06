package miui.vip;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import java.util.List;

public interface IXiaomiVipService extends IInterface {
    List<VipAchievement> getAchievements() throws RemoteException;

    List<VipBanner> getBanners() throws RemoteException;

    VipUserInfo getCurUserInfo() throws RemoteException;

    List<VipPhoneLevel> getVipLevelByPhoneNumber(List<String> list, String str) throws RemoteException;

    boolean isAvailable() throws RemoteException;

    void sendStatistic(String str) throws RemoteException;

    public static class Default implements IXiaomiVipService {
        public VipUserInfo getCurUserInfo() throws RemoteException {
            return null;
        }

        public List<VipPhoneLevel> getVipLevelByPhoneNumber(List<String> list, String requestId) throws RemoteException {
            return null;
        }

        public boolean isAvailable() throws RemoteException {
            return false;
        }

        public List<VipAchievement> getAchievements() throws RemoteException {
            return null;
        }

        public List<VipBanner> getBanners() throws RemoteException {
            return null;
        }

        public void sendStatistic(String data) throws RemoteException {
        }

        public IBinder asBinder() {
            return null;
        }
    }

    public static abstract class Stub extends Binder implements IXiaomiVipService {
        private static final String DESCRIPTOR = "miui.vip.IXiaomiVipService";
        static final int TRANSACTION_getAchievements = 4;
        static final int TRANSACTION_getBanners = 5;
        static final int TRANSACTION_getCurUserInfo = 1;
        static final int TRANSACTION_getVipLevelByPhoneNumber = 2;
        static final int TRANSACTION_isAvailable = 3;
        static final int TRANSACTION_sendStatistic = 6;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IXiaomiVipService asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin == null || !(iin instanceof IXiaomiVipService)) {
                return new Proxy(obj);
            }
            return (IXiaomiVipService) iin;
        }

        public IBinder asBinder() {
            return this;
        }

        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            if (code != 1598968902) {
                switch (code) {
                    case 1:
                        data.enforceInterface(DESCRIPTOR);
                        VipUserInfo _result = getCurUserInfo();
                        reply.writeNoException();
                        if (_result != null) {
                            reply.writeInt(1);
                            _result.writeToParcel(reply, 1);
                        } else {
                            reply.writeInt(0);
                        }
                        return true;
                    case 2:
                        data.enforceInterface(DESCRIPTOR);
                        List<VipPhoneLevel> _result2 = getVipLevelByPhoneNumber(data.createStringArrayList(), data.readString());
                        reply.writeNoException();
                        reply.writeTypedList(_result2);
                        return true;
                    case 3:
                        data.enforceInterface(DESCRIPTOR);
                        boolean _result3 = isAvailable();
                        reply.writeNoException();
                        reply.writeInt(_result3);
                        return true;
                    case 4:
                        data.enforceInterface(DESCRIPTOR);
                        List<VipAchievement> _result4 = getAchievements();
                        reply.writeNoException();
                        reply.writeTypedList(_result4);
                        return true;
                    case 5:
                        data.enforceInterface(DESCRIPTOR);
                        List<VipBanner> _result5 = getBanners();
                        reply.writeNoException();
                        reply.writeTypedList(_result5);
                        return true;
                    case 6:
                        data.enforceInterface(DESCRIPTOR);
                        sendStatistic(data.readString());
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

        private static class Proxy implements IXiaomiVipService {
            public static IXiaomiVipService sDefaultImpl;
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

            public VipUserInfo getCurUserInfo() throws RemoteException {
                VipUserInfo _result;
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (!this.mRemote.transact(1, _data, _reply, 0) && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().getCurUserInfo();
                    }
                    _reply.readException();
                    if (_reply.readInt() != 0) {
                        _result = VipUserInfo.CREATOR.createFromParcel(_reply);
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

            public List<VipPhoneLevel> getVipLevelByPhoneNumber(List<String> phoneNumList, String requestId) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStringList(phoneNumList);
                    _data.writeString(requestId);
                    if (!this.mRemote.transact(2, _data, _reply, 0) && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().getVipLevelByPhoneNumber(phoneNumList, requestId);
                    }
                    _reply.readException();
                    List<VipPhoneLevel> _result = _reply.createTypedArrayList(VipPhoneLevel.CREATOR);
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public boolean isAvailable() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    boolean z = false;
                    if (!this.mRemote.transact(3, _data, _reply, 0) && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().isAvailable();
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

            public List<VipAchievement> getAchievements() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (!this.mRemote.transact(4, _data, _reply, 0) && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().getAchievements();
                    }
                    _reply.readException();
                    List<VipAchievement> _result = _reply.createTypedArrayList(VipAchievement.CREATOR);
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public List<VipBanner> getBanners() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (!this.mRemote.transact(5, _data, _reply, 0) && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().getBanners();
                    }
                    _reply.readException();
                    List<VipBanner> _result = _reply.createTypedArrayList(VipBanner.CREATOR);
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void sendStatistic(String data) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(data);
                    if (this.mRemote.transact(6, _data, _reply, 0) || Stub.getDefaultImpl() == null) {
                        _reply.readException();
                        _reply.recycle();
                        _data.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().sendStatistic(data);
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }
        }

        public static boolean setDefaultImpl(IXiaomiVipService impl) {
            if (Proxy.sDefaultImpl != null || impl == null) {
                return false;
            }
            Proxy.sDefaultImpl = impl;
            return true;
        }

        public static IXiaomiVipService getDefaultImpl() {
            return Proxy.sDefaultImpl;
        }
    }
}
