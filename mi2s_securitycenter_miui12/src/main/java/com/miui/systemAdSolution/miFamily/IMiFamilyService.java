package com.miui.systemAdSolution.miFamily;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;

public interface IMiFamilyService extends IInterface {

    public static abstract class Stub extends Binder implements IMiFamilyService {
        private static final String DESCRIPTOR = "com.miui.systemAdSolution.miFamily.IMiFamilyService";
        static final int TRANSACTION_getMiFamilyLogoPath = 3;
        static final int TRANSACTION_getMiFamilyTitle = 4;
        static final int TRANSACTION_getMiFamilyUrl = 2;
        static final int TRANSACTION_showMiFamily = 1;
        static final int TRANSACTION_trackClick = 6;
        static final int TRANSACTION_trackView = 5;

        private static class Proxy implements IMiFamilyService {
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

            public String getMiFamilyLogoPath() {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(3, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readString();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public String getMiFamilyTitle() {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(4, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readString();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public String getMiFamilyUrl(String str) {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeString(str);
                    this.mRemote.transact(2, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readString();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public boolean showMiFamily() {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    boolean z = false;
                    this.mRemote.transact(1, obtain, obtain2, 0);
                    obtain2.readException();
                    if (obtain2.readInt() != 0) {
                        z = true;
                    }
                    return z;
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void trackClick(String str) {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeString(str);
                    this.mRemote.transact(6, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void trackView(String str) {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeString(str);
                    this.mRemote.transact(5, obtain, obtain2, 0);
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

        public static IMiFamilyService asInterface(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface queryLocalInterface = iBinder.queryLocalInterface(DESCRIPTOR);
            return (queryLocalInterface == null || !(queryLocalInterface instanceof IMiFamilyService)) ? new Proxy(iBinder) : (IMiFamilyService) queryLocalInterface;
        }

        public IBinder asBinder() {
            return this;
        }

        public boolean onTransact(int i, Parcel parcel, Parcel parcel2, int i2) {
            if (i != 1598968902) {
                switch (i) {
                    case 1:
                        parcel.enforceInterface(DESCRIPTOR);
                        boolean showMiFamily = showMiFamily();
                        parcel2.writeNoException();
                        parcel2.writeInt(showMiFamily ? 1 : 0);
                        return true;
                    case 2:
                        parcel.enforceInterface(DESCRIPTOR);
                        String miFamilyUrl = getMiFamilyUrl(parcel.readString());
                        parcel2.writeNoException();
                        parcel2.writeString(miFamilyUrl);
                        return true;
                    case 3:
                        parcel.enforceInterface(DESCRIPTOR);
                        String miFamilyLogoPath = getMiFamilyLogoPath();
                        parcel2.writeNoException();
                        parcel2.writeString(miFamilyLogoPath);
                        return true;
                    case 4:
                        parcel.enforceInterface(DESCRIPTOR);
                        String miFamilyTitle = getMiFamilyTitle();
                        parcel2.writeNoException();
                        parcel2.writeString(miFamilyTitle);
                        return true;
                    case 5:
                        parcel.enforceInterface(DESCRIPTOR);
                        trackView(parcel.readString());
                        parcel2.writeNoException();
                        return true;
                    case 6:
                        parcel.enforceInterface(DESCRIPTOR);
                        trackClick(parcel.readString());
                        parcel2.writeNoException();
                        return true;
                    default:
                        return super.onTransact(i, parcel, parcel2, i2);
                }
            } else {
                parcel2.writeString(DESCRIPTOR);
                return true;
            }
        }
    }

    String getMiFamilyLogoPath();

    String getMiFamilyTitle();

    String getMiFamilyUrl(String str);

    boolean showMiFamily();

    void trackClick(String str);

    void trackView(String str);
}
