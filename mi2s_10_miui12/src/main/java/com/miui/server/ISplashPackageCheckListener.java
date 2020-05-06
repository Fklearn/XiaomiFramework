package com.miui.server;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import java.util.List;

public interface ISplashPackageCheckListener extends IInterface {
    void updateSplashPackageCheckInfo(SplashPackageCheckInfo splashPackageCheckInfo) throws RemoteException;

    void updateSplashPackageCheckInfoList(List<SplashPackageCheckInfo> list) throws RemoteException;

    public static abstract class Stub extends Binder implements ISplashPackageCheckListener {
        private static final String DESCRIPTOR = "com.miui.server.ISplashPackageCheckListener";
        static final int TRANSACTION_updateSplashPackageCheckInfo = 2;
        static final int TRANSACTION_updateSplashPackageCheckInfoList = 1;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static ISplashPackageCheckListener asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin == null || !(iin instanceof ISplashPackageCheckListener)) {
                return new Proxy(obj);
            }
            return (ISplashPackageCheckListener) iin;
        }

        public IBinder asBinder() {
            return this;
        }

        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            SplashPackageCheckInfo _arg0;
            if (code == 1) {
                data.enforceInterface(DESCRIPTOR);
                updateSplashPackageCheckInfoList(data.createTypedArrayList(SplashPackageCheckInfo.CREATOR));
                return true;
            } else if (code == 2) {
                data.enforceInterface(DESCRIPTOR);
                if (data.readInt() != 0) {
                    _arg0 = SplashPackageCheckInfo.CREATOR.createFromParcel(data);
                } else {
                    _arg0 = null;
                }
                updateSplashPackageCheckInfo(_arg0);
                return true;
            } else if (code != 1598968902) {
                return super.onTransact(code, data, reply, flags);
            } else {
                reply.writeString(DESCRIPTOR);
                return true;
            }
        }

        private static class Proxy implements ISplashPackageCheckListener {
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

            public void updateSplashPackageCheckInfoList(List<SplashPackageCheckInfo> splashPackageCheckInfos) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedList(splashPackageCheckInfos);
                    this.mRemote.transact(1, _data, (Parcel) null, 1);
                } finally {
                    _data.recycle();
                }
            }

            public void updateSplashPackageCheckInfo(SplashPackageCheckInfo splashPackageCheckInfo) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (splashPackageCheckInfo != null) {
                        _data.writeInt(1);
                        splashPackageCheckInfo.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    this.mRemote.transact(2, _data, (Parcel) null, 1);
                } finally {
                    _data.recycle();
                }
            }
        }
    }
}
