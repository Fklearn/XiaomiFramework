package com.miui.server;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import com.miui.server.ISplashPackageCheckListener;

public interface ISplashScreenService extends IInterface {
    void activityIdle(ActivityInfo activityInfo) throws RemoteException;

    void destroyActivity(ActivityInfo activityInfo) throws RemoteException;

    Intent requestSplashScreen(Intent intent, ActivityInfo activityInfo) throws RemoteException;

    void setSplashPackageListener(ISplashPackageCheckListener iSplashPackageCheckListener) throws RemoteException;

    public static abstract class Stub extends Binder implements ISplashScreenService {
        private static final String DESCRIPTOR = "com.miui.server.ISplashScreenService";
        static final int TRANSACTION_activityIdle = 2;
        static final int TRANSACTION_destroyActivity = 3;
        static final int TRANSACTION_requestSplashScreen = 1;
        static final int TRANSACTION_setSplashPackageListener = 4;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static ISplashScreenService asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin == null || !(iin instanceof ISplashScreenService)) {
                return new Proxy(obj);
            }
            return (ISplashScreenService) iin;
        }

        public IBinder asBinder() {
            return this;
        }

        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            if (code == 1) {
                data.enforceInterface(DESCRIPTOR);
                Intent _result = requestSplashScreen((Intent) Intent.CREATOR.createFromParcel(data), (ActivityInfo) ActivityInfo.CREATOR.createFromParcel(data));
                reply.writeNoException();
                if (_result != null) {
                    reply.writeInt(1);
                    _result.writeToParcel(reply, 1);
                } else {
                    reply.writeInt(0);
                }
                return true;
            } else if (code == 2) {
                data.enforceInterface(DESCRIPTOR);
                activityIdle((ActivityInfo) ActivityInfo.CREATOR.createFromParcel(data));
                reply.writeNoException();
                return true;
            } else if (code == 3) {
                data.enforceInterface(DESCRIPTOR);
                destroyActivity((ActivityInfo) ActivityInfo.CREATOR.createFromParcel(data));
                return true;
            } else if (code == 4) {
                data.enforceInterface(DESCRIPTOR);
                setSplashPackageListener(ISplashPackageCheckListener.Stub.asInterface(data.readStrongBinder()));
                return true;
            } else if (code != 1598968902) {
                return super.onTransact(code, data, reply, flags);
            } else {
                reply.writeString(DESCRIPTOR);
                return true;
            }
        }

        private static class Proxy implements ISplashScreenService {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            public IBinder asBinder() {
                return this.mRemote;
            }

            public Intent requestSplashScreen(Intent intent, ActivityInfo aInfo) throws RemoteException {
                Intent _result;
                Parcel _data = Parcel.obtain();
                _data.writeInterfaceToken(Stub.DESCRIPTOR);
                intent.writeToParcel(_data, 0);
                aInfo.writeToParcel(_data, 0);
                Parcel _reply = Parcel.obtain();
                try {
                    this.mRemote.transact(1, _data, _reply, 0);
                    _reply.readException();
                    if (_reply.readInt() != 0) {
                        _result = (Intent) Intent.CREATOR.createFromParcel(_reply);
                    } else {
                        _result = null;
                    }
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void activityIdle(ActivityInfo aInfo) throws RemoteException {
                Parcel _data = Parcel.obtain();
                _data.writeInterfaceToken(Stub.DESCRIPTOR);
                aInfo.writeToParcel(_data, 0);
                Parcel _reply = Parcel.obtain();
                try {
                    this.mRemote.transact(2, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void destroyActivity(ActivityInfo aInfo) throws RemoteException {
                Parcel _data = Parcel.obtain();
                _data.writeInterfaceToken(Stub.DESCRIPTOR);
                aInfo.writeToParcel(_data, 0);
                try {
                    this.mRemote.transact(3, _data, (Parcel) null, 1);
                } finally {
                    _data.recycle();
                }
            }

            public void setSplashPackageListener(ISplashPackageCheckListener listener) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(listener != null ? listener.asBinder() : null);
                    this.mRemote.transact(4, _data, (Parcel) null, 1);
                } finally {
                    _data.recycle();
                }
            }
        }
    }
}
