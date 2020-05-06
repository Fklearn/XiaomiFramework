package com.miui.gamebooster.service;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;

public interface IGameBoosterTelecomeManager extends IInterface {

    public static abstract class Stub extends Binder implements IGameBoosterTelecomeManager {

        private static class a implements IGameBoosterTelecomeManager {

            /* renamed from: a  reason: collision with root package name */
            private IBinder f4783a;

            a(IBinder iBinder) {
                this.f4783a = iBinder;
            }

            public void P() {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.miui.gamebooster.service.IGameBoosterTelecomeManager");
                    this.f4783a.transact(1, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public IBinder asBinder() {
                return this.f4783a;
            }

            public void u() {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.miui.gamebooster.service.IGameBoosterTelecomeManager");
                    this.f4783a.transact(2, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }
        }

        public Stub() {
            attachInterface(this, "com.miui.gamebooster.service.IGameBoosterTelecomeManager");
        }

        public static IGameBoosterTelecomeManager a(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface queryLocalInterface = iBinder.queryLocalInterface("com.miui.gamebooster.service.IGameBoosterTelecomeManager");
            return (queryLocalInterface == null || !(queryLocalInterface instanceof IGameBoosterTelecomeManager)) ? new a(iBinder) : (IGameBoosterTelecomeManager) queryLocalInterface;
        }

        public IBinder asBinder() {
            return this;
        }

        public boolean onTransact(int i, Parcel parcel, Parcel parcel2, int i2) {
            if (i == 1) {
                parcel.enforceInterface("com.miui.gamebooster.service.IGameBoosterTelecomeManager");
                P();
            } else if (i == 2) {
                parcel.enforceInterface("com.miui.gamebooster.service.IGameBoosterTelecomeManager");
                u();
            } else if (i != 1598968902) {
                return super.onTransact(i, parcel, parcel2, i2);
            } else {
                parcel2.writeString("com.miui.gamebooster.service.IGameBoosterTelecomeManager");
                return true;
            }
            parcel2.writeNoException();
            return true;
        }
    }

    void P();

    void u();
}
