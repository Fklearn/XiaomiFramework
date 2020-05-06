package com.miui.networkassistant.service;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import com.miui.networkassistant.model.AppInfo;
import com.miui.networkassistant.service.IAppMonitorBinderListener;
import java.util.List;
import java.util.Map;

public interface IAppMonitorBinder extends IInterface {

    public static abstract class Stub extends Binder implements IAppMonitorBinder {
        private static final String DESCRIPTOR = "com.miui.networkassistant.service.IAppMonitorBinder";
        static final int TRANSACTION_getAppInfoByPackageName = 6;
        static final int TRANSACTION_getFilteredAppInfosList = 5;
        static final int TRANSACTION_getNetworkAccessedAppList = 1;
        static final int TRANSACTION_getNetworkAccessedAppsMap = 4;
        static final int TRANSACTION_getNonSystemAppList = 3;
        static final int TRANSACTION_getSystemAppList = 2;
        static final int TRANSACTION_registerLisener = 7;
        static final int TRANSACTION_unRegisterLisener = 8;

        private static class Proxy implements IAppMonitorBinder {
            private IBinder mRemote;

            Proxy(IBinder iBinder) {
                this.mRemote = iBinder;
            }

            public IBinder asBinder() {
                return this.mRemote;
            }

            public AppInfo getAppInfoByPackageName(String str) {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeString(str);
                    this.mRemote.transact(6, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readInt() != 0 ? AppInfo.CREATOR.createFromParcel(obtain2) : null;
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public List getFilteredAppInfosList() {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(5, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readArrayList(getClass().getClassLoader());
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public String getInterfaceDescriptor() {
                return Stub.DESCRIPTOR;
            }

            public List getNetworkAccessedAppList() {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(1, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readArrayList(getClass().getClassLoader());
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public Map getNetworkAccessedAppsMap() {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(4, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readHashMap(getClass().getClassLoader());
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public List getNonSystemAppList() {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(3, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readArrayList(getClass().getClassLoader());
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public List getSystemAppList() {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(2, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readArrayList(getClass().getClassLoader());
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void registerLisener(IAppMonitorBinderListener iAppMonitorBinderListener) {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeStrongBinder(iAppMonitorBinderListener != null ? iAppMonitorBinderListener.asBinder() : null);
                    this.mRemote.transact(7, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void unRegisterLisener(IAppMonitorBinderListener iAppMonitorBinderListener) {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeStrongBinder(iAppMonitorBinderListener != null ? iAppMonitorBinderListener.asBinder() : null);
                    this.mRemote.transact(8, obtain, obtain2, 0);
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

        public static IAppMonitorBinder asInterface(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface queryLocalInterface = iBinder.queryLocalInterface(DESCRIPTOR);
            return (queryLocalInterface == null || !(queryLocalInterface instanceof IAppMonitorBinder)) ? new Proxy(iBinder) : (IAppMonitorBinder) queryLocalInterface;
        }

        public IBinder asBinder() {
            return this;
        }

        public boolean onTransact(int i, Parcel parcel, Parcel parcel2, int i2) {
            if (i != 1598968902) {
                switch (i) {
                    case 1:
                        parcel.enforceInterface(DESCRIPTOR);
                        List networkAccessedAppList = getNetworkAccessedAppList();
                        parcel2.writeNoException();
                        parcel2.writeList(networkAccessedAppList);
                        return true;
                    case 2:
                        parcel.enforceInterface(DESCRIPTOR);
                        List systemAppList = getSystemAppList();
                        parcel2.writeNoException();
                        parcel2.writeList(systemAppList);
                        return true;
                    case 3:
                        parcel.enforceInterface(DESCRIPTOR);
                        List nonSystemAppList = getNonSystemAppList();
                        parcel2.writeNoException();
                        parcel2.writeList(nonSystemAppList);
                        return true;
                    case 4:
                        parcel.enforceInterface(DESCRIPTOR);
                        Map networkAccessedAppsMap = getNetworkAccessedAppsMap();
                        parcel2.writeNoException();
                        parcel2.writeMap(networkAccessedAppsMap);
                        return true;
                    case 5:
                        parcel.enforceInterface(DESCRIPTOR);
                        List filteredAppInfosList = getFilteredAppInfosList();
                        parcel2.writeNoException();
                        parcel2.writeList(filteredAppInfosList);
                        return true;
                    case 6:
                        parcel.enforceInterface(DESCRIPTOR);
                        AppInfo appInfoByPackageName = getAppInfoByPackageName(parcel.readString());
                        parcel2.writeNoException();
                        if (appInfoByPackageName != null) {
                            parcel2.writeInt(1);
                            appInfoByPackageName.writeToParcel(parcel2, 1);
                        } else {
                            parcel2.writeInt(0);
                        }
                        return true;
                    case 7:
                        parcel.enforceInterface(DESCRIPTOR);
                        registerLisener(IAppMonitorBinderListener.Stub.asInterface(parcel.readStrongBinder()));
                        parcel2.writeNoException();
                        return true;
                    case 8:
                        parcel.enforceInterface(DESCRIPTOR);
                        unRegisterLisener(IAppMonitorBinderListener.Stub.asInterface(parcel.readStrongBinder()));
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

    AppInfo getAppInfoByPackageName(String str);

    List getFilteredAppInfosList();

    List getNetworkAccessedAppList();

    Map getNetworkAccessedAppsMap();

    List getNonSystemAppList();

    List getSystemAppList();

    void registerLisener(IAppMonitorBinderListener iAppMonitorBinderListener);

    void unRegisterLisener(IAppMonitorBinderListener iAppMonitorBinderListener);
}
