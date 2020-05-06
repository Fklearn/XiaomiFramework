package com.miui.optimizecenter.garbagecheck;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import com.miui.optimizecenter.garbagecheck.IGarbageCleanupCallback;
import com.miui.optimizecenter.garbagecheck.IGarbageScanCallback;
import java.util.List;

public interface IGarbageCheck extends IInterface {

    public static abstract class Stub extends Binder implements IGarbageCheck {

        private static class a implements IGarbageCheck {

            /* renamed from: a  reason: collision with root package name */
            private IBinder f5662a;

            a(IBinder iBinder) {
                this.f5662a = iBinder;
            }

            public void a(IGarbageScanCallback iGarbageScanCallback) {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.miui.optimizecenter.garbagecheck.IGarbageCheck");
                    obtain.writeStrongBinder(iGarbageScanCallback != null ? iGarbageScanCallback.asBinder() : null);
                    this.f5662a.transact(1, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public IBinder asBinder() {
                return this.f5662a;
            }
        }

        public Stub() {
            attachInterface(this, "com.miui.optimizecenter.garbagecheck.IGarbageCheck");
        }

        public static IGarbageCheck a(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface queryLocalInterface = iBinder.queryLocalInterface("com.miui.optimizecenter.garbagecheck.IGarbageCheck");
            return (queryLocalInterface == null || !(queryLocalInterface instanceof IGarbageCheck)) ? new a(iBinder) : (IGarbageCheck) queryLocalInterface;
        }

        public IBinder asBinder() {
            return this;
        }

        public boolean onTransact(int i, Parcel parcel, Parcel parcel2, int i2) {
            if (i == 1) {
                parcel.enforceInterface("com.miui.optimizecenter.garbagecheck.IGarbageCheck");
                a(IGarbageScanCallback.Stub.a(parcel.readStrongBinder()));
            } else if (i == 2) {
                parcel.enforceInterface("com.miui.optimizecenter.garbagecheck.IGarbageCheck");
                a(parcel.createStringArrayList(), IGarbageCleanupCallback.Stub.a(parcel.readStrongBinder()));
            } else if (i == 3) {
                parcel.enforceInterface("com.miui.optimizecenter.garbagecheck.IGarbageCheck");
                cancel();
            } else if (i != 1598968902) {
                return super.onTransact(i, parcel, parcel2, i2);
            } else {
                parcel2.writeString("com.miui.optimizecenter.garbagecheck.IGarbageCheck");
                return true;
            }
            parcel2.writeNoException();
            return true;
        }
    }

    void a(IGarbageScanCallback iGarbageScanCallback);

    void a(List<String> list, IGarbageCleanupCallback iGarbageCleanupCallback);

    void cancel();
}
