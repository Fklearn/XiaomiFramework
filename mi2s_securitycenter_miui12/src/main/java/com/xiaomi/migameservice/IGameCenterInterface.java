package com.xiaomi.migameservice;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import com.xiaomi.migameservice.IGameServiceCallback;

public interface IGameCenterInterface extends IInterface {

    public static abstract class Stub extends Binder implements IGameCenterInterface {

        private static class a implements IGameCenterInterface {

            /* renamed from: a  reason: collision with root package name */
            private IBinder f8335a;

            a(IBinder iBinder) {
                this.f8335a = iBinder;
            }

            public boolean a(int i, IGameServiceCallback iGameServiceCallback) {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.xiaomi.migameservice.IGameCenterInterface");
                    obtain.writeInt(i);
                    obtain.writeStrongBinder(iGameServiceCallback != null ? iGameServiceCallback.asBinder() : null);
                    boolean z = false;
                    this.f8335a.transact(1, obtain, obtain2, 0);
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

            public boolean a(IGameServiceCallback iGameServiceCallback) {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.xiaomi.migameservice.IGameCenterInterface");
                    obtain.writeStrongBinder(iGameServiceCallback != null ? iGameServiceCallback.asBinder() : null);
                    boolean z = false;
                    this.f8335a.transact(2, obtain, obtain2, 0);
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
                return this.f8335a;
            }

            public void h(String str) {
                Parcel obtain = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.xiaomi.migameservice.IGameCenterInterface");
                    obtain.writeString(str);
                    this.f8335a.transact(3, obtain, (Parcel) null, 1);
                } finally {
                    obtain.recycle();
                }
            }

            public void i(String str) {
                Parcel obtain = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.xiaomi.migameservice.IGameCenterInterface");
                    obtain.writeString(str);
                    this.f8335a.transact(5, obtain, (Parcel) null, 1);
                } finally {
                    obtain.recycle();
                }
            }

            public void m(String str) {
                Parcel obtain = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.xiaomi.migameservice.IGameCenterInterface");
                    obtain.writeString(str);
                    this.f8335a.transact(7, obtain, (Parcel) null, 1);
                } finally {
                    obtain.recycle();
                }
            }

            public boolean m(int i) {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.xiaomi.migameservice.IGameCenterInterface");
                    obtain.writeInt(i);
                    boolean z = false;
                    this.f8335a.transact(9, obtain, obtain2, 0);
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

            public void q() {
                Parcel obtain = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.xiaomi.migameservice.IGameCenterInterface");
                    this.f8335a.transact(6, obtain, (Parcel) null, 1);
                } finally {
                    obtain.recycle();
                }
            }

            public void w() {
                Parcel obtain = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.xiaomi.migameservice.IGameCenterInterface");
                    this.f8335a.transact(4, obtain, (Parcel) null, 1);
                } finally {
                    obtain.recycle();
                }
            }
        }

        public Stub() {
            attachInterface(this, "com.xiaomi.migameservice.IGameCenterInterface");
        }

        public static IGameCenterInterface a(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface queryLocalInterface = iBinder.queryLocalInterface("com.xiaomi.migameservice.IGameCenterInterface");
            return (queryLocalInterface == null || !(queryLocalInterface instanceof IGameCenterInterface)) ? new a(iBinder) : (IGameCenterInterface) queryLocalInterface;
        }

        public IBinder asBinder() {
            return this;
        }

        public boolean onTransact(int i, Parcel parcel, Parcel parcel2, int i2) {
            if (i != 1598968902) {
                int i3 = 0;
                switch (i) {
                    case 1:
                        parcel.enforceInterface("com.xiaomi.migameservice.IGameCenterInterface");
                        boolean a2 = a(parcel.readInt(), IGameServiceCallback.Stub.a(parcel.readStrongBinder()));
                        parcel2.writeNoException();
                        if (a2) {
                            i3 = 1;
                        }
                        parcel2.writeInt(i3);
                        return true;
                    case 2:
                        parcel.enforceInterface("com.xiaomi.migameservice.IGameCenterInterface");
                        boolean a3 = a(IGameServiceCallback.Stub.a(parcel.readStrongBinder()));
                        parcel2.writeNoException();
                        if (a3) {
                            i3 = 1;
                        }
                        parcel2.writeInt(i3);
                        return true;
                    case 3:
                        parcel.enforceInterface("com.xiaomi.migameservice.IGameCenterInterface");
                        h(parcel.readString());
                        return true;
                    case 4:
                        parcel.enforceInterface("com.xiaomi.migameservice.IGameCenterInterface");
                        w();
                        return true;
                    case 5:
                        parcel.enforceInterface("com.xiaomi.migameservice.IGameCenterInterface");
                        i(parcel.readString());
                        return true;
                    case 6:
                        parcel.enforceInterface("com.xiaomi.migameservice.IGameCenterInterface");
                        q();
                        return true;
                    case 7:
                        parcel.enforceInterface("com.xiaomi.migameservice.IGameCenterInterface");
                        m(parcel.readString());
                        return true;
                    case 8:
                        parcel.enforceInterface("com.xiaomi.migameservice.IGameCenterInterface");
                        M();
                        return true;
                    case 9:
                        parcel.enforceInterface("com.xiaomi.migameservice.IGameCenterInterface");
                        boolean m = m(parcel.readInt());
                        parcel2.writeNoException();
                        if (m) {
                            i3 = 1;
                        }
                        parcel2.writeInt(i3);
                        return true;
                    case 10:
                        parcel.enforceInterface("com.xiaomi.migameservice.IGameCenterInterface");
                        boolean E = E();
                        parcel2.writeNoException();
                        if (E) {
                            i3 = 1;
                        }
                        parcel2.writeInt(i3);
                        return true;
                    default:
                        return super.onTransact(i, parcel, parcel2, i2);
                }
            } else {
                parcel2.writeString("com.xiaomi.migameservice.IGameCenterInterface");
                return true;
            }
        }
    }

    boolean E();

    void M();

    boolean a(int i, IGameServiceCallback iGameServiceCallback);

    boolean a(IGameServiceCallback iGameServiceCallback);

    void h(String str);

    void i(String str);

    void m(String str);

    boolean m(int i);

    void q();

    void w();
}
