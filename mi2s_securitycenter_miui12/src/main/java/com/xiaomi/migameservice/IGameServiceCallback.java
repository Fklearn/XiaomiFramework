package com.xiaomi.migameservice;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;

public interface IGameServiceCallback extends IInterface {

    public static abstract class Stub extends Binder implements IGameServiceCallback {

        private static class a implements IGameServiceCallback {

            /* renamed from: a  reason: collision with root package name */
            private IBinder f8336a;

            a(IBinder iBinder) {
                this.f8336a = iBinder;
            }

            public IBinder asBinder() {
                return this.f8336a;
            }
        }

        public Stub() {
            attachInterface(this, "com.xiaomi.migameservice.IGameServiceCallback");
        }

        public static IGameServiceCallback a(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface queryLocalInterface = iBinder.queryLocalInterface("com.xiaomi.migameservice.IGameServiceCallback");
            return (queryLocalInterface == null || !(queryLocalInterface instanceof IGameServiceCallback)) ? new a(iBinder) : (IGameServiceCallback) queryLocalInterface;
        }

        public IBinder asBinder() {
            return this;
        }

        public boolean onTransact(int i, Parcel parcel, Parcel parcel2, int i2) {
            if (i == 1) {
                parcel.enforceInterface("com.xiaomi.migameservice.IGameServiceCallback");
                b(parcel.readInt(), parcel.readString());
                return true;
            } else if (i != 1598968902) {
                return super.onTransact(i, parcel, parcel2, i2);
            } else {
                parcel2.writeString("com.xiaomi.migameservice.IGameServiceCallback");
                return true;
            }
        }
    }

    void b(int i, String str);
}
