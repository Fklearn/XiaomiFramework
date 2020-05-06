package com.miui.gamebooster.service;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import com.miui.gamebooster.service.INotificationListenerCallback;

public interface ISecurityCenterNotificationListener extends IInterface {

    public static abstract class Stub extends Binder implements ISecurityCenterNotificationListener {

        private static class a implements ISecurityCenterNotificationListener {

            /* renamed from: a  reason: collision with root package name */
            private IBinder f4786a;

            a(IBinder iBinder) {
                this.f4786a = iBinder;
            }

            public void a(INotificationListenerCallback iNotificationListenerCallback) {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.miui.gamebooster.service.ISecurityCenterNotificationListener");
                    obtain.writeStrongBinder(iNotificationListenerCallback != null ? iNotificationListenerCallback.asBinder() : null);
                    this.f4786a.transact(2, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public IBinder asBinder() {
                return this.f4786a;
            }

            public void b(INotificationListenerCallback iNotificationListenerCallback) {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.miui.gamebooster.service.ISecurityCenterNotificationListener");
                    obtain.writeStrongBinder(iNotificationListenerCallback != null ? iNotificationListenerCallback.asBinder() : null);
                    this.f4786a.transact(1, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }
        }

        public Stub() {
            attachInterface(this, "com.miui.gamebooster.service.ISecurityCenterNotificationListener");
        }

        public static ISecurityCenterNotificationListener a(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface queryLocalInterface = iBinder.queryLocalInterface("com.miui.gamebooster.service.ISecurityCenterNotificationListener");
            return (queryLocalInterface == null || !(queryLocalInterface instanceof ISecurityCenterNotificationListener)) ? new a(iBinder) : (ISecurityCenterNotificationListener) queryLocalInterface;
        }

        public IBinder asBinder() {
            return this;
        }

        public boolean onTransact(int i, Parcel parcel, Parcel parcel2, int i2) {
            if (i == 1) {
                parcel.enforceInterface("com.miui.gamebooster.service.ISecurityCenterNotificationListener");
                b(INotificationListenerCallback.Stub.asInterface(parcel.readStrongBinder()));
            } else if (i == 2) {
                parcel.enforceInterface("com.miui.gamebooster.service.ISecurityCenterNotificationListener");
                a(INotificationListenerCallback.Stub.asInterface(parcel.readStrongBinder()));
            } else if (i != 1598968902) {
                return super.onTransact(i, parcel, parcel2, i2);
            } else {
                parcel2.writeString("com.miui.gamebooster.service.ISecurityCenterNotificationListener");
                return true;
            }
            parcel2.writeNoException();
            return true;
        }
    }

    void a(INotificationListenerCallback iNotificationListenerCallback);

    void b(INotificationListenerCallback iNotificationListenerCallback);
}
