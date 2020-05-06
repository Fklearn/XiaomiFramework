package com.miui.gamebooster.service;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import java.util.List;

public interface IGameBooster extends IInterface {

    public static abstract class Stub extends Binder implements IGameBooster {

        private static class a implements IGameBooster {

            /* renamed from: a  reason: collision with root package name */
            private IBinder f4782a;

            a(IBinder iBinder) {
                this.f4782a = iBinder;
            }

            public void A() {
                Parcel obtain = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.miui.gamebooster.service.IGameBooster");
                    this.f4782a.transact(6, obtain, (Parcel) null, 1);
                } finally {
                    obtain.recycle();
                }
            }

            public void K() {
                Parcel obtain = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.miui.gamebooster.service.IGameBooster");
                    this.f4782a.transact(7, obtain, (Parcel) null, 1);
                } finally {
                    obtain.recycle();
                }
            }

            public IBinder asBinder() {
                return this.f4782a;
            }

            public void b(int i) {
                Parcel obtain = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.miui.gamebooster.service.IGameBooster");
                    obtain.writeInt(i);
                    this.f4782a.transact(8, obtain, (Parcel) null, 1);
                } finally {
                    obtain.recycle();
                }
            }

            public void b(List<String> list) {
                Parcel obtain = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.miui.gamebooster.service.IGameBooster");
                    obtain.writeStringList(list);
                    this.f4782a.transact(5, obtain, (Parcel) null, 1);
                } finally {
                    obtain.recycle();
                }
            }

            public void f(String str) {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.miui.gamebooster.service.IGameBooster");
                    obtain.writeString(str);
                    this.f4782a.transact(1, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void n() {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.miui.gamebooster.service.IGameBooster");
                    this.f4782a.transact(3, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }
        }

        public Stub() {
            attachInterface(this, "com.miui.gamebooster.service.IGameBooster");
        }

        public static IGameBooster a(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface queryLocalInterface = iBinder.queryLocalInterface("com.miui.gamebooster.service.IGameBooster");
            return (queryLocalInterface == null || !(queryLocalInterface instanceof IGameBooster)) ? new a(iBinder) : (IGameBooster) queryLocalInterface;
        }

        public IBinder asBinder() {
            return this;
        }

        public boolean onTransact(int i, Parcel parcel, Parcel parcel2, int i2) {
            if (i != 1598968902) {
                switch (i) {
                    case 1:
                        parcel.enforceInterface("com.miui.gamebooster.service.IGameBooster");
                        f(parcel.readString());
                        parcel2.writeNoException();
                        return true;
                    case 2:
                        parcel.enforceInterface("com.miui.gamebooster.service.IGameBooster");
                        String F = F();
                        parcel2.writeNoException();
                        parcel2.writeString(F);
                        return true;
                    case 3:
                        parcel.enforceInterface("com.miui.gamebooster.service.IGameBooster");
                        n();
                        parcel2.writeNoException();
                        return true;
                    case 4:
                        parcel.enforceInterface("com.miui.gamebooster.service.IGameBooster");
                        L();
                        return true;
                    case 5:
                        parcel.enforceInterface("com.miui.gamebooster.service.IGameBooster");
                        b((List<String>) parcel.createStringArrayList());
                        return true;
                    case 6:
                        parcel.enforceInterface("com.miui.gamebooster.service.IGameBooster");
                        A();
                        return true;
                    case 7:
                        parcel.enforceInterface("com.miui.gamebooster.service.IGameBooster");
                        K();
                        return true;
                    case 8:
                        parcel.enforceInterface("com.miui.gamebooster.service.IGameBooster");
                        b(parcel.readInt());
                        return true;
                    default:
                        return super.onTransact(i, parcel, parcel2, i2);
                }
            } else {
                parcel2.writeString("com.miui.gamebooster.service.IGameBooster");
                return true;
            }
        }
    }

    void A();

    String F();

    void K();

    void L();

    void b(int i);

    void b(List<String> list);

    void f(String str);

    void n();
}
