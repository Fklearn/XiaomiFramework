package com.miui.systemAdSolution.landingPage;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;

public interface ILandingPageListener extends IInterface {

    public static abstract class Stub extends Binder implements ILandingPageListener {
        private static final String DESCRIPTOR = "com.miui.systemAdSolution.landingPage.ILandingPageListener";
        static final int TRANSACTION_onDeeplinkFail = 10;
        static final int TRANSACTION_onDeeplinkSuccess = 9;
        static final int TRANSACTION_onDownloadCancel = 6;
        static final int TRANSACTION_onDownloadFail = 3;
        static final int TRANSACTION_onDownloadPause = 4;
        static final int TRANSACTION_onDownloadProgress = 5;
        static final int TRANSACTION_onDownloadStart = 1;
        static final int TRANSACTION_onDownloadSuccess = 2;
        static final int TRANSACTION_onH5Fail = 12;
        static final int TRANSACTION_onH5Success = 11;
        static final int TRANSACTION_onInstallFail = 8;
        static final int TRANSACTION_onInstallSuccess = 7;
        static final int TRANSACTION_onLanuchAppFail = 14;
        static final int TRANSACTION_onLanuchAppSuccess = 13;

        private static class Proxy implements ILandingPageListener {
            private IBinder mRemote;

            Proxy(IBinder iBinder) {
                this.mRemote = iBinder;
            }

            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return Stub.DESCRIPTOR;
            }

            public void onDeeplinkFail() {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(10, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void onDeeplinkSuccess() {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(9, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void onDownloadCancel() {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(6, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void onDownloadFail(String str) {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeString(str);
                    this.mRemote.transact(3, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void onDownloadPause(String str) {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeString(str);
                    this.mRemote.transact(4, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void onDownloadProgress(int i) {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeInt(i);
                    this.mRemote.transact(5, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void onDownloadStart() {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(1, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void onDownloadSuccess() {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(2, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void onH5Fail() {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(12, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void onH5Success() {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(11, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void onInstallFail(String str) {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeString(str);
                    this.mRemote.transact(8, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void onInstallSuccess() {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(7, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void onLanuchAppFail() {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(14, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void onLanuchAppSuccess() {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(13, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }
        }

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static ILandingPageListener asInterface(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface queryLocalInterface = iBinder.queryLocalInterface(DESCRIPTOR);
            return (queryLocalInterface == null || !(queryLocalInterface instanceof ILandingPageListener)) ? new Proxy(iBinder) : (ILandingPageListener) queryLocalInterface;
        }

        public IBinder asBinder() {
            return this;
        }

        public boolean onTransact(int i, Parcel parcel, Parcel parcel2, int i2) {
            if (i != 1598968902) {
                switch (i) {
                    case 1:
                        parcel.enforceInterface(DESCRIPTOR);
                        onDownloadStart();
                        break;
                    case 2:
                        parcel.enforceInterface(DESCRIPTOR);
                        onDownloadSuccess();
                        break;
                    case 3:
                        parcel.enforceInterface(DESCRIPTOR);
                        onDownloadFail(parcel.readString());
                        break;
                    case 4:
                        parcel.enforceInterface(DESCRIPTOR);
                        onDownloadPause(parcel.readString());
                        break;
                    case 5:
                        parcel.enforceInterface(DESCRIPTOR);
                        onDownloadProgress(parcel.readInt());
                        break;
                    case 6:
                        parcel.enforceInterface(DESCRIPTOR);
                        onDownloadCancel();
                        break;
                    case 7:
                        parcel.enforceInterface(DESCRIPTOR);
                        onInstallSuccess();
                        break;
                    case 8:
                        parcel.enforceInterface(DESCRIPTOR);
                        onInstallFail(parcel.readString());
                        break;
                    case 9:
                        parcel.enforceInterface(DESCRIPTOR);
                        onDeeplinkSuccess();
                        break;
                    case 10:
                        parcel.enforceInterface(DESCRIPTOR);
                        onDeeplinkFail();
                        break;
                    case 11:
                        parcel.enforceInterface(DESCRIPTOR);
                        onH5Success();
                        break;
                    case 12:
                        parcel.enforceInterface(DESCRIPTOR);
                        onH5Fail();
                        break;
                    case 13:
                        parcel.enforceInterface(DESCRIPTOR);
                        onLanuchAppSuccess();
                        break;
                    case 14:
                        parcel.enforceInterface(DESCRIPTOR);
                        onLanuchAppFail();
                        break;
                    default:
                        return super.onTransact(i, parcel, parcel2, i2);
                }
                parcel2.writeNoException();
                return true;
            }
            parcel2.writeString(DESCRIPTOR);
            return true;
        }
    }

    void onDeeplinkFail();

    void onDeeplinkSuccess();

    void onDownloadCancel();

    void onDownloadFail(String str);

    void onDownloadPause(String str);

    void onDownloadProgress(int i);

    void onDownloadStart();

    void onDownloadSuccess();

    void onH5Fail();

    void onH5Success();

    void onInstallFail(String str);

    void onInstallSuccess();

    void onLanuchAppFail();

    void onLanuchAppSuccess();
}
