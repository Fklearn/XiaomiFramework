package com.miui.gamebooster.service;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import java.util.List;

public interface IFreeformWindow extends IInterface {

    public static abstract class Stub extends Binder implements IFreeformWindow {

        private static class a implements IFreeformWindow {

            /* renamed from: a  reason: collision with root package name */
            private IBinder f4781a;

            a(IBinder iBinder) {
                this.f4781a = iBinder;
            }

            public IBinder asBinder() {
                return this.f4781a;
            }

            public void c(boolean z) {
                Parcel obtain = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.miui.gamebooster.service.IFreeformWindow");
                    obtain.writeInt(z ? 1 : 0);
                    this.f4781a.transact(2, obtain, (Parcel) null, 1);
                } finally {
                    obtain.recycle();
                }
            }

            public void d(List<String> list) {
                Parcel obtain = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.miui.gamebooster.service.IFreeformWindow");
                    obtain.writeStringList(list);
                    this.f4781a.transact(1, obtain, (Parcel) null, 1);
                } finally {
                    obtain.recycle();
                }
            }
        }

        public Stub() {
            attachInterface(this, "com.miui.gamebooster.service.IFreeformWindow");
        }

        public static IFreeformWindow a(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface queryLocalInterface = iBinder.queryLocalInterface("com.miui.gamebooster.service.IFreeformWindow");
            return (queryLocalInterface == null || !(queryLocalInterface instanceof IFreeformWindow)) ? new a(iBinder) : (IFreeformWindow) queryLocalInterface;
        }

        public IBinder asBinder() {
            return this;
        }

        public boolean onTransact(int i, Parcel parcel, Parcel parcel2, int i2) {
            if (i == 1) {
                parcel.enforceInterface("com.miui.gamebooster.service.IFreeformWindow");
                d(parcel.createStringArrayList());
                return true;
            } else if (i == 2) {
                parcel.enforceInterface("com.miui.gamebooster.service.IFreeformWindow");
                c(parcel.readInt() != 0);
                return true;
            } else if (i != 1598968902) {
                return super.onTransact(i, parcel, parcel2, i2);
            } else {
                parcel2.writeString("com.miui.gamebooster.service.IFreeformWindow");
                return true;
            }
        }
    }

    void c(boolean z);

    void d(List<String> list);
}
