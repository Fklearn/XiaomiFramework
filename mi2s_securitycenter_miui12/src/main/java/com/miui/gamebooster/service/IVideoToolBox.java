package com.miui.gamebooster.service;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import java.util.List;

public interface IVideoToolBox extends IInterface {

    public static abstract class Stub extends Binder implements IVideoToolBox {

        private static class a implements IVideoToolBox {

            /* renamed from: a  reason: collision with root package name */
            private IBinder f4787a;

            a(IBinder iBinder) {
                this.f4787a = iBinder;
            }

            public IBinder asBinder() {
                return this.f4787a;
            }

            public void c(List<String> list) {
                Parcel obtain = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.miui.gamebooster.service.IVideoToolBox");
                    obtain.writeStringList(list);
                    this.f4787a.transact(1, obtain, (Parcel) null, 1);
                } finally {
                    obtain.recycle();
                }
            }
        }

        public Stub() {
            attachInterface(this, "com.miui.gamebooster.service.IVideoToolBox");
        }

        public static IVideoToolBox a(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface queryLocalInterface = iBinder.queryLocalInterface("com.miui.gamebooster.service.IVideoToolBox");
            return (queryLocalInterface == null || !(queryLocalInterface instanceof IVideoToolBox)) ? new a(iBinder) : (IVideoToolBox) queryLocalInterface;
        }

        public IBinder asBinder() {
            return this;
        }

        public boolean onTransact(int i, Parcel parcel, Parcel parcel2, int i2) {
            if (i == 1) {
                parcel.enforceInterface("com.miui.gamebooster.service.IVideoToolBox");
                c(parcel.createStringArrayList());
                return true;
            } else if (i != 1598968902) {
                return super.onTransact(i, parcel, parcel2, i2);
            } else {
                parcel2.writeString("com.miui.gamebooster.service.IVideoToolBox");
                return true;
            }
        }
    }

    void c(List<String> list);
}
