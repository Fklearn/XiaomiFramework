package com.miui.securitycenter.memory;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;

public interface IMemoryCleanupCallback extends IInterface {

    public static abstract class Stub extends Binder implements IMemoryCleanupCallback {

        private static class a implements IMemoryCleanupCallback {

            /* renamed from: a  reason: collision with root package name */
            private IBinder f7487a;

            a(IBinder iBinder) {
                this.f7487a = iBinder;
            }

            public boolean a(String str) {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.miui.securitycenter.memory.IMemoryCleanupCallback");
                    obtain.writeString(str);
                    boolean z = false;
                    this.f7487a.transact(2, obtain, obtain2, 0);
                    obtain2.readException();
                    if (obtain2.readInt() != 0) {
                        z = true;
                    }
                    return z;
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public IBinder asBinder() {
                return this.f7487a;
            }

            public void d() {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.miui.securitycenter.memory.IMemoryCleanupCallback");
                    this.f7487a.transact(3, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void f() {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.miui.securitycenter.memory.IMemoryCleanupCallback");
                    this.f7487a.transact(1, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }
        }

        public Stub() {
            attachInterface(this, "com.miui.securitycenter.memory.IMemoryCleanupCallback");
        }

        public static IMemoryCleanupCallback a(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface queryLocalInterface = iBinder.queryLocalInterface("com.miui.securitycenter.memory.IMemoryCleanupCallback");
            return (queryLocalInterface == null || !(queryLocalInterface instanceof IMemoryCleanupCallback)) ? new a(iBinder) : (IMemoryCleanupCallback) queryLocalInterface;
        }

        public IBinder asBinder() {
            return this;
        }

        public boolean onTransact(int i, Parcel parcel, Parcel parcel2, int i2) {
            if (i == 1) {
                parcel.enforceInterface("com.miui.securitycenter.memory.IMemoryCleanupCallback");
                f();
            } else if (i == 2) {
                parcel.enforceInterface("com.miui.securitycenter.memory.IMemoryCleanupCallback");
                boolean a2 = a(parcel.readString());
                parcel2.writeNoException();
                parcel2.writeInt(a2 ? 1 : 0);
                return true;
            } else if (i == 3) {
                parcel.enforceInterface("com.miui.securitycenter.memory.IMemoryCleanupCallback");
                d();
            } else if (i != 1598968902) {
                return super.onTransact(i, parcel, parcel2, i2);
            } else {
                parcel2.writeString("com.miui.securitycenter.memory.IMemoryCleanupCallback");
                return true;
            }
            parcel2.writeNoException();
            return true;
        }
    }

    boolean a(String str);

    void d();

    void f();
}
