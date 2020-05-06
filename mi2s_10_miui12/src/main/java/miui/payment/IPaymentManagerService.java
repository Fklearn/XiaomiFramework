package miui.payment;

import android.accounts.Account;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import miui.payment.IPaymentManagerResponse;

public interface IPaymentManagerService extends IInterface {
    void getMiliBalance(IPaymentManagerResponse iPaymentManagerResponse, Account account, String str, String str2) throws RemoteException;

    void payForOrder(IPaymentManagerResponse iPaymentManagerResponse, Account account, String str, Bundle bundle) throws RemoteException;

    void recharge(IPaymentManagerResponse iPaymentManagerResponse, Account account, String str, String str2) throws RemoteException;

    void showMiliCenter(IPaymentManagerResponse iPaymentManagerResponse, Account account) throws RemoteException;

    void showPayRecord(IPaymentManagerResponse iPaymentManagerResponse, Account account, String str, String str2) throws RemoteException;

    void showRechargeRecord(IPaymentManagerResponse iPaymentManagerResponse, Account account, String str, String str2) throws RemoteException;

    public static class Default implements IPaymentManagerService {
        public void payForOrder(IPaymentManagerResponse response, Account account, String order, Bundle extra) throws RemoteException {
        }

        public void showMiliCenter(IPaymentManagerResponse response, Account account) throws RemoteException {
        }

        public void showRechargeRecord(IPaymentManagerResponse response, Account account, String serviceId, String verify) throws RemoteException {
        }

        public void showPayRecord(IPaymentManagerResponse response, Account account, String serviceId, String verify) throws RemoteException {
        }

        public void recharge(IPaymentManagerResponse response, Account account, String serviceId, String verify) throws RemoteException {
        }

        public void getMiliBalance(IPaymentManagerResponse response, Account account, String serviceId, String verify) throws RemoteException {
        }

        public IBinder asBinder() {
            return null;
        }
    }

    public static abstract class Stub extends Binder implements IPaymentManagerService {
        private static final String DESCRIPTOR = "miui.payment.IPaymentManagerService";
        static final int TRANSACTION_getMiliBalance = 6;
        static final int TRANSACTION_payForOrder = 1;
        static final int TRANSACTION_recharge = 5;
        static final int TRANSACTION_showMiliCenter = 2;
        static final int TRANSACTION_showPayRecord = 4;
        static final int TRANSACTION_showRechargeRecord = 3;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IPaymentManagerService asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin == null || !(iin instanceof IPaymentManagerService)) {
                return new Proxy(obj);
            }
            return (IPaymentManagerService) iin;
        }

        public IBinder asBinder() {
            return this;
        }

        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            Account _arg1;
            Bundle _arg3;
            Account _arg12;
            Account _arg13;
            Account _arg14;
            Account _arg15;
            Account _arg16;
            if (code != 1598968902) {
                switch (code) {
                    case 1:
                        data.enforceInterface(DESCRIPTOR);
                        IPaymentManagerResponse _arg0 = IPaymentManagerResponse.Stub.asInterface(data.readStrongBinder());
                        if (data.readInt() != 0) {
                            _arg1 = (Account) Account.CREATOR.createFromParcel(data);
                        } else {
                            _arg1 = null;
                        }
                        String _arg2 = data.readString();
                        if (data.readInt() != 0) {
                            _arg3 = (Bundle) Bundle.CREATOR.createFromParcel(data);
                        } else {
                            _arg3 = null;
                        }
                        payForOrder(_arg0, _arg1, _arg2, _arg3);
                        reply.writeNoException();
                        return true;
                    case 2:
                        data.enforceInterface(DESCRIPTOR);
                        IPaymentManagerResponse _arg02 = IPaymentManagerResponse.Stub.asInterface(data.readStrongBinder());
                        if (data.readInt() != 0) {
                            _arg12 = (Account) Account.CREATOR.createFromParcel(data);
                        } else {
                            _arg12 = null;
                        }
                        showMiliCenter(_arg02, _arg12);
                        reply.writeNoException();
                        return true;
                    case 3:
                        data.enforceInterface(DESCRIPTOR);
                        IPaymentManagerResponse _arg03 = IPaymentManagerResponse.Stub.asInterface(data.readStrongBinder());
                        if (data.readInt() != 0) {
                            _arg13 = (Account) Account.CREATOR.createFromParcel(data);
                        } else {
                            _arg13 = null;
                        }
                        showRechargeRecord(_arg03, _arg13, data.readString(), data.readString());
                        reply.writeNoException();
                        return true;
                    case 4:
                        data.enforceInterface(DESCRIPTOR);
                        IPaymentManagerResponse _arg04 = IPaymentManagerResponse.Stub.asInterface(data.readStrongBinder());
                        if (data.readInt() != 0) {
                            _arg14 = (Account) Account.CREATOR.createFromParcel(data);
                        } else {
                            _arg14 = null;
                        }
                        showPayRecord(_arg04, _arg14, data.readString(), data.readString());
                        reply.writeNoException();
                        return true;
                    case 5:
                        data.enforceInterface(DESCRIPTOR);
                        IPaymentManagerResponse _arg05 = IPaymentManagerResponse.Stub.asInterface(data.readStrongBinder());
                        if (data.readInt() != 0) {
                            _arg15 = (Account) Account.CREATOR.createFromParcel(data);
                        } else {
                            _arg15 = null;
                        }
                        recharge(_arg05, _arg15, data.readString(), data.readString());
                        reply.writeNoException();
                        return true;
                    case 6:
                        data.enforceInterface(DESCRIPTOR);
                        IPaymentManagerResponse _arg06 = IPaymentManagerResponse.Stub.asInterface(data.readStrongBinder());
                        if (data.readInt() != 0) {
                            _arg16 = (Account) Account.CREATOR.createFromParcel(data);
                        } else {
                            _arg16 = null;
                        }
                        getMiliBalance(_arg06, _arg16, data.readString(), data.readString());
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

        private static class Proxy implements IPaymentManagerService {
            public static IPaymentManagerService sDefaultImpl;
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

            public void payForOrder(IPaymentManagerResponse response, Account account, String order, Bundle extra) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(response != null ? response.asBinder() : null);
                    if (account != null) {
                        _data.writeInt(1);
                        account.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeString(order);
                    if (extra != null) {
                        _data.writeInt(1);
                        extra.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    if (this.mRemote.transact(1, _data, _reply, 0) || Stub.getDefaultImpl() == null) {
                        _reply.readException();
                        _reply.recycle();
                        _data.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().payForOrder(response, account, order, extra);
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void showMiliCenter(IPaymentManagerResponse response, Account account) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(response != null ? response.asBinder() : null);
                    if (account != null) {
                        _data.writeInt(1);
                        account.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    if (this.mRemote.transact(2, _data, _reply, 0) || Stub.getDefaultImpl() == null) {
                        _reply.readException();
                        _reply.recycle();
                        _data.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().showMiliCenter(response, account);
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void showRechargeRecord(IPaymentManagerResponse response, Account account, String serviceId, String verify) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(response != null ? response.asBinder() : null);
                    if (account != null) {
                        _data.writeInt(1);
                        account.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeString(serviceId);
                    _data.writeString(verify);
                    if (this.mRemote.transact(3, _data, _reply, 0) || Stub.getDefaultImpl() == null) {
                        _reply.readException();
                        _reply.recycle();
                        _data.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().showRechargeRecord(response, account, serviceId, verify);
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void showPayRecord(IPaymentManagerResponse response, Account account, String serviceId, String verify) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(response != null ? response.asBinder() : null);
                    if (account != null) {
                        _data.writeInt(1);
                        account.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeString(serviceId);
                    _data.writeString(verify);
                    if (this.mRemote.transact(4, _data, _reply, 0) || Stub.getDefaultImpl() == null) {
                        _reply.readException();
                        _reply.recycle();
                        _data.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().showPayRecord(response, account, serviceId, verify);
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void recharge(IPaymentManagerResponse response, Account account, String serviceId, String verify) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(response != null ? response.asBinder() : null);
                    if (account != null) {
                        _data.writeInt(1);
                        account.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeString(serviceId);
                    _data.writeString(verify);
                    if (this.mRemote.transact(5, _data, _reply, 0) || Stub.getDefaultImpl() == null) {
                        _reply.readException();
                        _reply.recycle();
                        _data.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().recharge(response, account, serviceId, verify);
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void getMiliBalance(IPaymentManagerResponse response, Account account, String serviceId, String verify) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(response != null ? response.asBinder() : null);
                    if (account != null) {
                        _data.writeInt(1);
                        account.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeString(serviceId);
                    _data.writeString(verify);
                    if (this.mRemote.transact(6, _data, _reply, 0) || Stub.getDefaultImpl() == null) {
                        _reply.readException();
                        _reply.recycle();
                        _data.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().getMiliBalance(response, account, serviceId, verify);
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }
        }

        public static boolean setDefaultImpl(IPaymentManagerService impl) {
            if (Proxy.sDefaultImpl != null || impl == null) {
                return false;
            }
            Proxy.sDefaultImpl = impl;
            return true;
        }

        public static IPaymentManagerService getDefaultImpl() {
            return Proxy.sDefaultImpl;
        }
    }
}
