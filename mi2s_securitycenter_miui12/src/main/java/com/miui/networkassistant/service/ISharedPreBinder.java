package com.miui.networkassistant.service;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;

public interface ISharedPreBinder extends IInterface {

    public static abstract class Stub extends Binder implements ISharedPreBinder {
        private static final String DESCRIPTOR = "com.miui.networkassistant.service.ISharedPreBinder";
        static final int TRANSACTION_getBoolean = 10;
        static final int TRANSACTION_getFloat = 8;
        static final int TRANSACTION_getInt = 4;
        static final int TRANSACTION_getLong = 6;
        static final int TRANSACTION_getString = 2;
        static final int TRANSACTION_putBoolean = 9;
        static final int TRANSACTION_putFloat = 7;
        static final int TRANSACTION_putInt = 3;
        static final int TRANSACTION_putLong = 5;
        static final int TRANSACTION_putString = 1;

        private static class Proxy implements ISharedPreBinder {
            private IBinder mRemote;

            Proxy(IBinder iBinder) {
                this.mRemote = iBinder;
            }

            public IBinder asBinder() {
                return this.mRemote;
            }

            public boolean getBoolean(String str, boolean z) {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeString(str);
                    boolean z2 = true;
                    obtain.writeInt(z ? 1 : 0);
                    this.mRemote.transact(10, obtain, obtain2, 0);
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

            public float getFloat(String str, float f) {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeString(str);
                    obtain.writeFloat(f);
                    this.mRemote.transact(8, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readFloat();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public int getInt(String str, int i) {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeString(str);
                    obtain.writeInt(i);
                    this.mRemote.transact(4, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readInt();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public String getInterfaceDescriptor() {
                return Stub.DESCRIPTOR;
            }

            public long getLong(String str, long j) {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeString(str);
                    obtain.writeLong(j);
                    this.mRemote.transact(6, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readLong();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public String getString(String str, String str2) {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeString(str);
                    obtain.writeString(str2);
                    this.mRemote.transact(2, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readString();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public boolean putBoolean(String str, boolean z) {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeString(str);
                    boolean z2 = true;
                    obtain.writeInt(z ? 1 : 0);
                    this.mRemote.transact(9, obtain, obtain2, 0);
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

            public boolean putFloat(String str, float f) {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeString(str);
                    obtain.writeFloat(f);
                    boolean z = false;
                    this.mRemote.transact(7, obtain, obtain2, 0);
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

            public boolean putInt(String str, int i) {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeString(str);
                    obtain.writeInt(i);
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

            public boolean putLong(String str, long j) {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeString(str);
                    obtain.writeLong(j);
                    boolean z = false;
                    this.mRemote.transact(5, obtain, obtain2, 0);
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

            public boolean putString(String str, String str2) {
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

        public static ISharedPreBinder asInterface(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface queryLocalInterface = iBinder.queryLocalInterface(DESCRIPTOR);
            return (queryLocalInterface == null || !(queryLocalInterface instanceof ISharedPreBinder)) ? new Proxy(iBinder) : (ISharedPreBinder) queryLocalInterface;
        }

        public IBinder asBinder() {
            return this;
        }

        public boolean onTransact(int i, Parcel parcel, Parcel parcel2, int i2) {
            if (i != 1598968902) {
                int i3 = 0;
                switch (i) {
                    case 1:
                        parcel.enforceInterface(DESCRIPTOR);
                        boolean putString = putString(parcel.readString(), parcel.readString());
                        parcel2.writeNoException();
                        if (putString) {
                            i3 = 1;
                        }
                        parcel2.writeInt(i3);
                        return true;
                    case 2:
                        parcel.enforceInterface(DESCRIPTOR);
                        String string = getString(parcel.readString(), parcel.readString());
                        parcel2.writeNoException();
                        parcel2.writeString(string);
                        return true;
                    case 3:
                        parcel.enforceInterface(DESCRIPTOR);
                        boolean putInt = putInt(parcel.readString(), parcel.readInt());
                        parcel2.writeNoException();
                        if (putInt) {
                            i3 = 1;
                        }
                        parcel2.writeInt(i3);
                        return true;
                    case 4:
                        parcel.enforceInterface(DESCRIPTOR);
                        int i4 = getInt(parcel.readString(), parcel.readInt());
                        parcel2.writeNoException();
                        parcel2.writeInt(i4);
                        return true;
                    case 5:
                        parcel.enforceInterface(DESCRIPTOR);
                        boolean putLong = putLong(parcel.readString(), parcel.readLong());
                        parcel2.writeNoException();
                        if (putLong) {
                            i3 = 1;
                        }
                        parcel2.writeInt(i3);
                        return true;
                    case 6:
                        parcel.enforceInterface(DESCRIPTOR);
                        long j = getLong(parcel.readString(), parcel.readLong());
                        parcel2.writeNoException();
                        parcel2.writeLong(j);
                        return true;
                    case 7:
                        parcel.enforceInterface(DESCRIPTOR);
                        boolean putFloat = putFloat(parcel.readString(), parcel.readFloat());
                        parcel2.writeNoException();
                        if (putFloat) {
                            i3 = 1;
                        }
                        parcel2.writeInt(i3);
                        return true;
                    case 8:
                        parcel.enforceInterface(DESCRIPTOR);
                        float f = getFloat(parcel.readString(), parcel.readFloat());
                        parcel2.writeNoException();
                        parcel2.writeFloat(f);
                        return true;
                    case 9:
                        parcel.enforceInterface(DESCRIPTOR);
                        boolean putBoolean = putBoolean(parcel.readString(), parcel.readInt() != 0);
                        parcel2.writeNoException();
                        if (putBoolean) {
                            i3 = 1;
                        }
                        parcel2.writeInt(i3);
                        return true;
                    case 10:
                        parcel.enforceInterface(DESCRIPTOR);
                        boolean z = getBoolean(parcel.readString(), parcel.readInt() != 0);
                        parcel2.writeNoException();
                        if (z) {
                            i3 = 1;
                        }
                        parcel2.writeInt(i3);
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

    boolean getBoolean(String str, boolean z);

    float getFloat(String str, float f);

    int getInt(String str, int i);

    long getLong(String str, long j);

    String getString(String str, String str2);

    boolean putBoolean(String str, boolean z);

    boolean putFloat(String str, float f);

    boolean putInt(String str, int i);

    boolean putLong(String str, long j);

    boolean putString(String str, String str2);
}
