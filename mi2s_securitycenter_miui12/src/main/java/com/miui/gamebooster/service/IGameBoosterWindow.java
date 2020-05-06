package com.miui.gamebooster.service;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;

public interface IGameBoosterWindow extends IInterface {

    public static abstract class Stub extends Binder implements IGameBoosterWindow {

        private static class a implements IGameBoosterWindow {

            /* renamed from: a  reason: collision with root package name */
            private IBinder f4784a;

            a(IBinder iBinder) {
                this.f4784a = iBinder;
            }

            public void a(boolean z, boolean z2) {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.miui.gamebooster.service.IGameBoosterWindow");
                    int i = 1;
                    obtain.writeInt(z ? 1 : 0);
                    if (!z2) {
                        i = 0;
                    }
                    obtain.writeInt(i);
                    this.f4784a.transact(2, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public IBinder asBinder() {
                return this.f4784a;
            }
        }

        public Stub() {
            attachInterface(this, "com.miui.gamebooster.service.IGameBoosterWindow");
        }

        public static IGameBoosterWindow a(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface queryLocalInterface = iBinder.queryLocalInterface("com.miui.gamebooster.service.IGameBoosterWindow");
            return (queryLocalInterface == null || !(queryLocalInterface instanceof IGameBoosterWindow)) ? new a(iBinder) : (IGameBoosterWindow) queryLocalInterface;
        }

        public IBinder asBinder() {
            return this;
        }

        public boolean onTransact(int i, Parcel parcel, Parcel parcel2, int i2) {
            if (i == 1) {
                parcel.enforceInterface("com.miui.gamebooster.service.IGameBoosterWindow");
                D();
                return true;
            } else if (i == 2) {
                parcel.enforceInterface("com.miui.gamebooster.service.IGameBoosterWindow");
                boolean z = false;
                boolean z2 = parcel.readInt() != 0;
                if (parcel.readInt() != 0) {
                    z = true;
                }
                a(z2, z);
                parcel2.writeNoException();
                return true;
            } else if (i != 1598968902) {
                return super.onTransact(i, parcel, parcel2, i2);
            } else {
                parcel2.writeString("com.miui.gamebooster.service.IGameBoosterWindow");
                return true;
            }
        }
    }

    void D();

    void a(boolean z, boolean z2);
}
