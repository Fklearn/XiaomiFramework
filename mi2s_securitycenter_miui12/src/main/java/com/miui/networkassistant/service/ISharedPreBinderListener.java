package com.miui.networkassistant.service;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;

public interface ISharedPreBinderListener extends IInterface {

    public static abstract class Stub extends Binder implements ISharedPreBinderListener {
        private static final String DESCRIPTOR = "com.miui.networkassistant.service.ISharedPreBinderListener";
        static final int TRANSACTION_onPutBoolean = 5;
        static final int TRANSACTION_onPutFloat = 4;
        static final int TRANSACTION_onPutInt = 2;
        static final int TRANSACTION_onPutLong = 3;
        static final int TRANSACTION_onPutString = 1;

        private static class Proxy implements ISharedPreBinderListener {
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

            public boolean onPutBoolean(String str, boolean z) {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeString(str);
                    boolean z2 = true;
                    obtain.writeInt(z ? 1 : 0);
                    this.mRemote.transact(5, obtain, obtain2, 0);
                    obtain2.readException();
                    if (obtain2.readInt() == 0) {
                        z2 = false;
                    }
                    return z2;
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public boolean onPutFloat(String str, float f) {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeString(str);
                    obtain.writeFloat(f);
                    boolean z = false;
                    this.mRemote.transact(4, obtain, obtain2, 0);
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

            public boolean onPutInt(String str, int i) {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeString(str);
                    obtain.writeInt(i);
                    boolean z = false;
                    this.mRemote.transact(2, obtain, obtain2, 0);
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

            public boolean onPutLong(String str, long j) {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeString(str);
                    obtain.writeLong(j);
                    boolean z = false;
                    this.mRemote.transact(3, obtain, obtain2, 0);
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

            public boolean onPutString(String str, String str2) {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeString(str);
                    obtain.writeString(str2);
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
        }

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static ISharedPreBinderListener asInterface(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface queryLocalInterface = iBinder.queryLocalInterface(DESCRIPTOR);
            return (queryLocalInterface == null || !(queryLocalInterface instanceof ISharedPreBinderListener)) ? new Proxy(iBinder) : (ISharedPreBinderListener) queryLocalInterface;
        }

        public IBinder asBinder() {
            return this;
        }

        public boolean onTransact(int i, Parcel parcel, Parcel parcel2, int i2) {
            int i3 = 0;
            if (i == 1) {
                parcel.enforceInterface(DESCRIPTOR);
                boolean onPutString = onPutString(parcel.readString(), parcel.readString());
                parcel2.writeNoException();
                if (onPutString) {
                    i3 = 1;
                }
                parcel2.writeInt(i3);
                return true;
            } else if (i == 2) {
                parcel.enforceInterface(DESCRIPTOR);
                boolean onPutInt = onPutInt(parcel.readString(), parcel.readInt());
                parcel2.writeNoException();
                if (onPutInt) {
                    i3 = 1;
                }
                parcel2.writeInt(i3);
                return true;
            } else if (i == 3) {
                parcel.enforceInterface(DESCRIPTOR);
                boolean onPutLong = onPutLong(parcel.readString(), parcel.readLong());
                parcel2.writeNoException();
                if (onPutLong) {
                    i3 = 1;
                }
                parcel2.writeInt(i3);
                return true;
            } else if (i == 4) {
                parcel.enforceInterface(DESCRIPTOR);
                boolean onPutFloat = onPutFloat(parcel.readString(), parcel.readFloat());
                parcel2.writeNoException();
                if (onPutFloat) {
                    i3 = 1;
                }
                parcel2.writeInt(i3);
                return true;
            } else if (i == 5) {
                parcel.enforceInterface(DESCRIPTOR);
                boolean onPutBoolean = onPutBoolean(parcel.readString(), parcel.readInt() != 0);
                parcel2.writeNoException();
                if (onPutBoolean) {
                    i3 = 1;
                }
                parcel2.writeInt(i3);
                return true;
            } else if (i != 1598968902) {
                return super.onTransact(i, parcel, parcel2, i2);
            } else {
                parcel2.writeString(DESCRIPTOR);
                return true;
            }
        }
    }

    boolean onPutBoolean(String str, boolean z);

    boolean onPutFloat(String str, float f);

    boolean onPutInt(String str, int i);

    boolean onPutLong(String str, long j);

    boolean onPutString(String str, String str2);
}
