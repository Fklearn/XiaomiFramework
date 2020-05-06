package com.miui.powerkeeper;

import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;

public interface IPowerKeeper extends IInterface {

    public static abstract class Stub extends Binder implements IPowerKeeper {

        private static class a implements IPowerKeeper {

            /* renamed from: a  reason: collision with root package name */
            private IBinder f7372a;

            a(IBinder iBinder) {
                this.f7372a = iBinder;
            }

            public int a(Bundle bundle, Bundle bundle2) {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.miui.powerkeeper.IPowerKeeper");
                    if (bundle != null) {
                        obtain.writeInt(1);
                        bundle.writeToParcel(obtain, 0);
                    } else {
                        obtain.writeInt(0);
                    }
                    this.f7372a.transact(3, obtain, obtain2, 0);
                    obtain2.readException();
                    int readInt = obtain2.readInt();
                    if (obtain2.readInt() != 0) {
                        bundle2.readFromParcel(obtain2);
                    }
                    return readInt;
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public IBinder asBinder() {
                return this.f7372a;
            }
        }

        public Stub() {
            attachInterface(this, "com.miui.powerkeeper.IPowerKeeper");
        }

        public static IPowerKeeper a(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface queryLocalInterface = iBinder.queryLocalInterface("com.miui.powerkeeper.IPowerKeeper");
            return (queryLocalInterface == null || !(queryLocalInterface instanceof IPowerKeeper)) ? new a(iBinder) : (IPowerKeeper) queryLocalInterface;
        }

        public IBinder asBinder() {
            return this;
        }

        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v5, resolved type: java.lang.Object} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v2, resolved type: android.os.Bundle} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v9, resolved type: java.lang.Object} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v5, resolved type: android.os.Bundle} */
        /* JADX WARNING: Multi-variable type inference failed */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public boolean onTransact(int r5, android.os.Parcel r6, android.os.Parcel r7, int r8) {
            /*
                r4 = this;
                r0 = 1
                java.lang.String r1 = "com.miui.powerkeeper.IPowerKeeper"
                if (r5 == r0) goto L_0x005f
                r2 = 2
                r3 = 0
                if (r5 == r2) goto L_0x0042
                r2 = 3
                if (r5 == r2) goto L_0x001a
                r2 = 1598968902(0x5f4e5446, float:1.4867585E19)
                if (r5 == r2) goto L_0x0016
                boolean r5 = super.onTransact(r5, r6, r7, r8)
                return r5
            L_0x0016:
                r7.writeString(r1)
                return r0
            L_0x001a:
                r6.enforceInterface(r1)
                int r5 = r6.readInt()
                if (r5 == 0) goto L_0x002c
                android.os.Parcelable$Creator r5 = android.os.Bundle.CREATOR
                java.lang.Object r5 = r5.createFromParcel(r6)
                r3 = r5
                android.os.Bundle r3 = (android.os.Bundle) r3
            L_0x002c:
                android.os.Bundle r5 = new android.os.Bundle
                r5.<init>()
                int r6 = r4.a(r3, r5)
                r7.writeNoException()
                r7.writeInt(r6)
                r7.writeInt(r0)
                r5.writeToParcel(r7, r0)
                return r0
            L_0x0042:
                r6.enforceInterface(r1)
                int r5 = r6.readInt()
                if (r5 == 0) goto L_0x0054
                android.os.Parcelable$Creator r5 = android.os.Bundle.CREATOR
                java.lang.Object r5 = r5.createFromParcel(r6)
                r3 = r5
                android.os.Bundle r3 = (android.os.Bundle) r3
            L_0x0054:
                int r5 = r4.b(r3)
                r7.writeNoException()
                r7.writeInt(r5)
                return r0
            L_0x005f:
                r6.enforceInterface(r1)
                int r5 = r6.readInt()
                r4.k(r5)
                r7.writeNoException()
                return r0
            */
            throw new UnsupportedOperationException("Method not decompiled: com.miui.powerkeeper.IPowerKeeper.Stub.onTransact(int, android.os.Parcel, android.os.Parcel, int):boolean");
        }
    }

    int a(Bundle bundle, Bundle bundle2);

    int b(Bundle bundle);

    void k(int i);
}
