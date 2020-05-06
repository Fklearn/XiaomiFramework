package com.miui.optimizecenter.garbagecheck;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;

public interface IGarbageScanCallback extends IInterface {

    public static abstract class Stub extends Binder implements IGarbageScanCallback {

        private static class a implements IGarbageScanCallback {

            /* renamed from: a  reason: collision with root package name */
            private IBinder f5664a;

            a(IBinder iBinder) {
                this.f5664a = iBinder;
            }

            public IBinder asBinder() {
                return this.f5664a;
            }
        }

        public Stub() {
            attachInterface(this, "com.miui.optimizecenter.garbagecheck.IGarbageScanCallback");
        }

        public static IGarbageScanCallback a(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface queryLocalInterface = iBinder.queryLocalInterface("com.miui.optimizecenter.garbagecheck.IGarbageScanCallback");
            return (queryLocalInterface == null || !(queryLocalInterface instanceof IGarbageScanCallback)) ? new a(iBinder) : (IGarbageScanCallback) queryLocalInterface;
        }

        public IBinder asBinder() {
            return this;
        }

        public boolean onTransact(int i, Parcel parcel, Parcel parcel2, int i2) {
            if (i != 1) {
                int i3 = 0;
                if (i == 2) {
                    parcel.enforceInterface("com.miui.optimizecenter.garbagecheck.IGarbageScanCallback");
                    boolean b2 = b(parcel.readString());
                    parcel2.writeNoException();
                    if (b2) {
                        i3 = 1;
                    }
                    parcel2.writeInt(i3);
                    return true;
                } else if (i == 3) {
                    parcel.enforceInterface("com.miui.optimizecenter.garbagecheck.IGarbageScanCallback");
                    boolean a2 = a(parcel.readString(), parcel.readString(), parcel.readString(), parcel.readLong(), parcel.readInt() != 0);
                    parcel2.writeNoException();
                    if (a2) {
                        i3 = 1;
                    }
                    parcel2.writeInt(i3);
                    return true;
                } else if (i == 4) {
                    parcel.enforceInterface("com.miui.optimizecenter.garbagecheck.IGarbageScanCallback");
                    c();
                } else if (i != 1598968902) {
                    return super.onTransact(i, parcel, parcel2, i2);
                } else {
                    parcel2.writeString("com.miui.optimizecenter.garbagecheck.IGarbageScanCallback");
                    return true;
                }
            } else {
                parcel.enforceInterface("com.miui.optimizecenter.garbagecheck.IGarbageScanCallback");
                b();
            }
            parcel2.writeNoException();
            return true;
        }
    }

    boolean a(String str, String str2, String str3, long j, boolean z);

    void b();

    boolean b(String str);

    void c();
}
