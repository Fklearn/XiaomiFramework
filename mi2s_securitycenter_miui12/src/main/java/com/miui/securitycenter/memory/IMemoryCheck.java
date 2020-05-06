package com.miui.securitycenter.memory;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import com.miui.securitycenter.memory.IMemoryCleanupCallback;
import com.miui.securitycenter.memory.IMemoryScanCallback;
import java.util.List;
import java.util.Map;

public interface IMemoryCheck extends IInterface {

    public static abstract class Stub extends Binder implements IMemoryCheck {

        private static class a implements IMemoryCheck {

            /* renamed from: a  reason: collision with root package name */
            private IBinder f7486a;

            a(IBinder iBinder) {
                this.f7486a = iBinder;
            }

            public void a(IMemoryScanCallback iMemoryScanCallback) {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.miui.securitycenter.memory.IMemoryCheck");
                    obtain.writeStrongBinder(iMemoryScanCallback != null ? iMemoryScanCallback.asBinder() : null);
                    this.f7486a.transact(1, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public IBinder asBinder() {
                return this.f7486a;
            }

            public Map t() {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.miui.securitycenter.memory.IMemoryCheck");
                    this.f7486a.transact(9, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readHashMap(getClass().getClassLoader());
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }
        }

        public Stub() {
            attachInterface(this, "com.miui.securitycenter.memory.IMemoryCheck");
        }

        public static IMemoryCheck a(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface queryLocalInterface = iBinder.queryLocalInterface("com.miui.securitycenter.memory.IMemoryCheck");
            return (queryLocalInterface == null || !(queryLocalInterface instanceof IMemoryCheck)) ? new a(iBinder) : (IMemoryCheck) queryLocalInterface;
        }

        public IBinder asBinder() {
            return this;
        }

        public boolean onTransact(int i, Parcel parcel, Parcel parcel2, int i2) {
            if (i != 1598968902) {
                switch (i) {
                    case 1:
                        parcel.enforceInterface("com.miui.securitycenter.memory.IMemoryCheck");
                        a(IMemoryScanCallback.Stub.a(parcel.readStrongBinder()));
                        parcel2.writeNoException();
                        return true;
                    case 2:
                        parcel.enforceInterface("com.miui.securitycenter.memory.IMemoryCheck");
                        a(parcel.createStringArrayList(), IMemoryCleanupCallback.Stub.a(parcel.readStrongBinder()));
                        parcel2.writeNoException();
                        return true;
                    case 3:
                        parcel.enforceInterface("com.miui.securitycenter.memory.IMemoryCheck");
                        List<String> J = J();
                        parcel2.writeNoException();
                        parcel2.writeStringList(J);
                        return true;
                    case 4:
                        parcel.enforceInterface("com.miui.securitycenter.memory.IMemoryCheck");
                        int g = g(parcel.readString());
                        parcel2.writeNoException();
                        parcel2.writeInt(g);
                        return true;
                    case 5:
                        parcel.enforceInterface("com.miui.securitycenter.memory.IMemoryCheck");
                        c(parcel.readString(), parcel.readInt());
                        parcel2.writeNoException();
                        return true;
                    case 6:
                        parcel.enforceInterface("com.miui.securitycenter.memory.IMemoryCheck");
                        List<String> i3 = i(parcel.readInt());
                        parcel2.writeNoException();
                        parcel2.writeStringList(i3);
                        return true;
                    case 7:
                        parcel.enforceInterface("com.miui.securitycenter.memory.IMemoryCheck");
                        int b2 = b(parcel.readString(), parcel.readInt());
                        parcel2.writeNoException();
                        parcel2.writeInt(b2);
                        return true;
                    case 8:
                        parcel.enforceInterface("com.miui.securitycenter.memory.IMemoryCheck");
                        a(parcel.readString(), parcel.readInt(), parcel.readInt());
                        parcel2.writeNoException();
                        return true;
                    case 9:
                        parcel.enforceInterface("com.miui.securitycenter.memory.IMemoryCheck");
                        Map t = t();
                        parcel2.writeNoException();
                        parcel2.writeMap(t);
                        return true;
                    default:
                        return super.onTransact(i, parcel, parcel2, i2);
                }
            } else {
                parcel2.writeString("com.miui.securitycenter.memory.IMemoryCheck");
                return true;
            }
        }
    }

    List<String> J();

    void a(IMemoryScanCallback iMemoryScanCallback);

    void a(String str, int i, int i2);

    void a(List<String> list, IMemoryCleanupCallback iMemoryCleanupCallback);

    int b(String str, int i);

    void c(String str, int i);

    int g(String str);

    List<String> i(int i);

    Map t();
}
