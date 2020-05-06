package com.miui.networkassistant.service;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import com.miui.networkassistant.service.ITrafficCornBinderListener;
import java.util.Map;

public interface ITrafficCornBinder extends IInterface {

    public static abstract class Stub extends Binder implements ITrafficCornBinder {
        private static final String DESCRIPTOR = "com.miui.networkassistant.service.ITrafficCornBinder";
        static final int TRANSACTION_getBrands = 4;
        static final int TRANSACTION_getCities = 2;
        static final int TRANSACTION_getInstructions = 5;
        static final int TRANSACTION_getOperators = 3;
        static final int TRANSACTION_getProvinceCodeByCityCode = 9;
        static final int TRANSACTION_getProvinces = 1;
        static final int TRANSACTION_getTcType = 8;
        static final int TRANSACTION_isConfigUpdated = 7;
        static final int TRANSACTION_isFinished = 6;
        static final int TRANSACTION_registerLisener = 10;
        static final int TRANSACTION_unRegisterLisener = 11;

        private static class Proxy implements ITrafficCornBinder {
            private IBinder mRemote;

            Proxy(IBinder iBinder) {
                this.mRemote = iBinder;
            }

            public IBinder asBinder() {
                return this.mRemote;
            }

            public Map getBrands(String str) {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeString(str);
                    this.mRemote.transact(4, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readHashMap(getClass().getClassLoader());
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public Map getCities(int i) {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeInt(i);
                    this.mRemote.transact(2, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readHashMap(getClass().getClassLoader());
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public Map getInstructions(int i) {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeInt(i);
                    this.mRemote.transact(5, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readHashMap(getClass().getClassLoader());
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public String getInterfaceDescriptor() {
                return Stub.DESCRIPTOR;
            }

            public Map getOperators() {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(3, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readHashMap(getClass().getClassLoader());
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public int getProvinceCodeByCityCode(int i) {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeInt(i);
                    this.mRemote.transact(9, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readInt();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public Map getProvinces() {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(1, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readHashMap(getClass().getClassLoader());
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public int getTcType() {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(8, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readInt();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public boolean isConfigUpdated() {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
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

            public boolean isFinished() {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    boolean z = false;
                    this.mRemote.transact(6, obtain, obtain2, 0);
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

            public void registerLisener(ITrafficCornBinderListener iTrafficCornBinderListener) {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeStrongBinder(iTrafficCornBinderListener != null ? iTrafficCornBinderListener.asBinder() : null);
                    this.mRemote.transact(10, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void unRegisterLisener(ITrafficCornBinderListener iTrafficCornBinderListener) {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeStrongBinder(iTrafficCornBinderListener != null ? iTrafficCornBinderListener.asBinder() : null);
                    this.mRemote.transact(11, obtain, obtain2, 0);
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

        public static ITrafficCornBinder asInterface(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface queryLocalInterface = iBinder.queryLocalInterface(DESCRIPTOR);
            return (queryLocalInterface == null || !(queryLocalInterface instanceof ITrafficCornBinder)) ? new Proxy(iBinder) : (ITrafficCornBinder) queryLocalInterface;
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
                        Map provinces = getProvinces();
                        parcel2.writeNoException();
                        parcel2.writeMap(provinces);
                        return true;
                    case 2:
                        parcel.enforceInterface(DESCRIPTOR);
                        Map cities = getCities(parcel.readInt());
                        parcel2.writeNoException();
                        parcel2.writeMap(cities);
                        return true;
                    case 3:
                        parcel.enforceInterface(DESCRIPTOR);
                        Map operators = getOperators();
                        parcel2.writeNoException();
                        parcel2.writeMap(operators);
                        return true;
                    case 4:
                        parcel.enforceInterface(DESCRIPTOR);
                        Map brands = getBrands(parcel.readString());
                        parcel2.writeNoException();
                        parcel2.writeMap(brands);
                        return true;
                    case 5:
                        parcel.enforceInterface(DESCRIPTOR);
                        Map instructions = getInstructions(parcel.readInt());
                        parcel2.writeNoException();
                        parcel2.writeMap(instructions);
                        return true;
                    case 6:
                        parcel.enforceInterface(DESCRIPTOR);
                        boolean isFinished = isFinished();
                        parcel2.writeNoException();
                        if (isFinished) {
                            i3 = 1;
                        }
                        parcel2.writeInt(i3);
                        return true;
                    case 7:
                        parcel.enforceInterface(DESCRIPTOR);
                        boolean isConfigUpdated = isConfigUpdated();
                        parcel2.writeNoException();
                        if (isConfigUpdated) {
                            i3 = 1;
                        }
                        parcel2.writeInt(i3);
                        return true;
                    case 8:
                        parcel.enforceInterface(DESCRIPTOR);
                        int tcType = getTcType();
                        parcel2.writeNoException();
                        parcel2.writeInt(tcType);
                        return true;
                    case 9:
                        parcel.enforceInterface(DESCRIPTOR);
                        int provinceCodeByCityCode = getProvinceCodeByCityCode(parcel.readInt());
                        parcel2.writeNoException();
                        parcel2.writeInt(provinceCodeByCityCode);
                        return true;
                    case 10:
                        parcel.enforceInterface(DESCRIPTOR);
                        registerLisener(ITrafficCornBinderListener.Stub.asInterface(parcel.readStrongBinder()));
                        parcel2.writeNoException();
                        return true;
                    case 11:
                        parcel.enforceInterface(DESCRIPTOR);
                        unRegisterLisener(ITrafficCornBinderListener.Stub.asInterface(parcel.readStrongBinder()));
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

    Map getBrands(String str);

    Map getCities(int i);

    Map getInstructions(int i);

    Map getOperators();

    int getProvinceCodeByCityCode(int i);

    Map getProvinces();

    int getTcType();

    boolean isConfigUpdated();

    boolean isFinished();

    void registerLisener(ITrafficCornBinderListener iTrafficCornBinderListener);

    void unRegisterLisener(ITrafficCornBinderListener iTrafficCornBinderListener);
}
