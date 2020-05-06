package com.miui.securitycenter.memory;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import java.util.ArrayList;
import java.util.List;

public interface IMemoryScanCallback extends IInterface {

    public static abstract class Stub extends Binder implements IMemoryScanCallback {

        private static class a implements IMemoryScanCallback {

            /* renamed from: a  reason: collision with root package name */
            private IBinder f7488a;

            a(IBinder iBinder) {
                this.f7488a = iBinder;
            }

            public void a(List<MemoryModel> list) {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.miui.securitycenter.memory.IMemoryScanCallback");
                    obtain.writeTypedList(list);
                    this.f7488a.transact(4, obtain, obtain2, 0);
                    obtain2.readException();
                    obtain2.readTypedList(list, MemoryModel.CREATOR);
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public IBinder asBinder() {
                return this.f7488a;
            }

            public void b() {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.miui.securitycenter.memory.IMemoryScanCallback");
                    this.f7488a.transact(1, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public boolean b(String str) {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.miui.securitycenter.memory.IMemoryScanCallback");
                    obtain.writeString(str);
                    boolean z = false;
                    this.f7488a.transact(3, obtain, obtain2, 0);
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

            public boolean e() {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.miui.securitycenter.memory.IMemoryScanCallback");
                    boolean z = false;
                    this.f7488a.transact(2, obtain, obtain2, 0);
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
        }

        public Stub() {
            attachInterface(this, "com.miui.securitycenter.memory.IMemoryScanCallback");
        }

        public static IMemoryScanCallback a(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface queryLocalInterface = iBinder.queryLocalInterface("com.miui.securitycenter.memory.IMemoryScanCallback");
            return (queryLocalInterface == null || !(queryLocalInterface instanceof IMemoryScanCallback)) ? new a(iBinder) : (IMemoryScanCallback) queryLocalInterface;
        }

        public IBinder asBinder() {
            return this;
        }

        public boolean onTransact(int i, Parcel parcel, Parcel parcel2, int i2) {
            if (i != 1) {
                int i3 = 0;
                if (i == 2) {
                    parcel.enforceInterface("com.miui.securitycenter.memory.IMemoryScanCallback");
                    boolean e = e();
                    parcel2.writeNoException();
                    if (e) {
                        i3 = 1;
                    }
                    parcel2.writeInt(i3);
                    return true;
                } else if (i == 3) {
                    parcel.enforceInterface("com.miui.securitycenter.memory.IMemoryScanCallback");
                    boolean b2 = b(parcel.readString());
                    parcel2.writeNoException();
                    if (b2) {
                        i3 = 1;
                    }
                    parcel2.writeInt(i3);
                    return true;
                } else if (i == 4) {
                    parcel.enforceInterface("com.miui.securitycenter.memory.IMemoryScanCallback");
                    ArrayList<MemoryModel> createTypedArrayList = parcel.createTypedArrayList(MemoryModel.CREATOR);
                    a(createTypedArrayList);
                    parcel2.writeNoException();
                    parcel2.writeTypedList(createTypedArrayList);
                    return true;
                } else if (i != 1598968902) {
                    return super.onTransact(i, parcel, parcel2, i2);
                } else {
                    parcel2.writeString("com.miui.securitycenter.memory.IMemoryScanCallback");
                    return true;
                }
            } else {
                parcel.enforceInterface("com.miui.securitycenter.memory.IMemoryScanCallback");
                b();
                parcel2.writeNoException();
                return true;
            }
        }
    }

    void a(List<MemoryModel> list);

    void b();

    boolean b(String str);

    boolean e();
}
