package com.miui.optimizecenter.garbagecheck;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;

public interface IGarbageCleanupCallback extends IInterface {

    public static abstract class Stub extends Binder implements IGarbageCleanupCallback {

        private static class a implements IGarbageCleanupCallback {

            /* renamed from: a  reason: collision with root package name */
            private IBinder f5663a;

            a(IBinder iBinder) {
                this.f5663a = iBinder;
            }

            public IBinder asBinder() {
                return this.f5663a;
            }
        }

        public Stub() {
            attachInterface(this, "com.miui.optimizecenter.garbagecheck.IGarbageCleanupCallback");
        }

        public static IGarbageCleanupCallback a(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface queryLocalInterface = iBinder.queryLocalInterface("com.miui.optimizecenter.garbagecheck.IGarbageCleanupCallback");
            return (queryLocalInterface == null || !(queryLocalInterface instanceof IGarbageCleanupCallback)) ? new a(iBinder) : (IGarbageCleanupCallback) queryLocalInterface;
        }

        public IBinder asBinder() {
            return this;
        }

        public boolean onTransact(int i, Parcel parcel, Parcel parcel2, int i2) {
            if (i == 1) {
                parcel.enforceInterface("com.miui.optimizecenter.garbagecheck.IGarbageCleanupCallback");
                boolean a2 = a(parcel.readString());
                parcel2.writeNoException();
                parcel2.writeInt(a2 ? 1 : 0);
                return true;
            } else if (i == 2) {
                parcel.enforceInterface("com.miui.optimizecenter.garbagecheck.IGarbageCleanupCallback");
                d();
                parcel2.writeNoException();
                return true;
            } else if (i != 1598968902) {
                return super.onTransact(i, parcel, parcel2, i2);
            } else {
                parcel2.writeString("com.miui.optimizecenter.garbagecheck.IGarbageCleanupCallback");
                return true;
            }
        }
    }

    boolean a(String str);

    void d();
}
