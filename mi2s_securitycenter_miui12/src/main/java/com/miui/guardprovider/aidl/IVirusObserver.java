package com.miui.guardprovider.aidl;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;

public interface IVirusObserver extends IInterface {

    public static abstract class Stub extends Binder implements IVirusObserver {

        private static class a implements IVirusObserver {

            /* renamed from: a  reason: collision with root package name */
            private IBinder f5468a;

            a(IBinder iBinder) {
                this.f5468a = iBinder;
            }

            public IBinder asBinder() {
                return this.f5468a;
            }
        }

        public Stub() {
            attachInterface(this, "com.miui.guardprovider.aidl.IVirusObserver");
        }

        public static IVirusObserver b(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface queryLocalInterface = iBinder.queryLocalInterface("com.miui.guardprovider.aidl.IVirusObserver");
            return (queryLocalInterface == null || !(queryLocalInterface instanceof IVirusObserver)) ? new a(iBinder) : (IVirusObserver) queryLocalInterface;
        }

        public IBinder asBinder() {
            return this;
        }

        public boolean onTransact(int i, Parcel parcel, Parcel parcel2, int i2) {
            if (i != 1598968902) {
                switch (i) {
                    case 1:
                        parcel.enforceInterface("com.miui.guardprovider.aidl.IVirusObserver");
                        d(parcel.readInt());
                        parcel2.writeNoException();
                        return true;
                    case 2:
                        parcel.enforceInterface("com.miui.guardprovider.aidl.IVirusObserver");
                        n(parcel.readInt());
                        parcel2.writeNoException();
                        return true;
                    case 3:
                        parcel.enforceInterface("com.miui.guardprovider.aidl.IVirusObserver");
                        a(parcel.readInt(), parcel.readString());
                        parcel2.writeNoException();
                        return true;
                    case 4:
                        parcel.enforceInterface("com.miui.guardprovider.aidl.IVirusObserver");
                        int readInt = parcel.readInt();
                        int readInt2 = parcel.readInt();
                        VirusInfo[] virusInfoArr = (VirusInfo[]) parcel.createTypedArray(VirusInfo.CREATOR);
                        a(readInt, readInt2, virusInfoArr);
                        parcel2.writeNoException();
                        parcel2.writeTypedArray(virusInfoArr, 1);
                        return true;
                    case 5:
                        parcel.enforceInterface("com.miui.guardprovider.aidl.IVirusObserver");
                        int readInt3 = parcel.readInt();
                        VirusInfo[] virusInfoArr2 = (VirusInfo[]) parcel.createTypedArray(VirusInfo.CREATOR);
                        a(readInt3, virusInfoArr2);
                        parcel2.writeNoException();
                        parcel2.writeTypedArray(virusInfoArr2, 1);
                        return true;
                    case 6:
                        parcel.enforceInterface("com.miui.guardprovider.aidl.IVirusObserver");
                        j(parcel.readInt());
                        parcel2.writeNoException();
                        return true;
                    case 7:
                        parcel.enforceInterface("com.miui.guardprovider.aidl.IVirusObserver");
                        a(parcel.readInt(), parcel.readInt());
                        parcel2.writeNoException();
                        return true;
                    case 8:
                        parcel.enforceInterface("com.miui.guardprovider.aidl.IVirusObserver");
                        a(parcel.readInt());
                        parcel2.writeNoException();
                        return true;
                    case 9:
                        parcel.enforceInterface("com.miui.guardprovider.aidl.IVirusObserver");
                        p(parcel.readInt());
                        parcel2.writeNoException();
                        return true;
                    case 10:
                        parcel.enforceInterface("com.miui.guardprovider.aidl.IVirusObserver");
                        c(parcel.readInt());
                        parcel2.writeNoException();
                        return true;
                    case 11:
                        parcel.enforceInterface("com.miui.guardprovider.aidl.IVirusObserver");
                        l(parcel.readInt());
                        parcel2.writeNoException();
                        return true;
                    case 12:
                        parcel.enforceInterface("com.miui.guardprovider.aidl.IVirusObserver");
                        a(parcel.readInt() != 0 ? UpdateInfo.CREATOR.createFromParcel(parcel) : null);
                        parcel2.writeNoException();
                        return true;
                    default:
                        return super.onTransact(i, parcel, parcel2, i2);
                }
            } else {
                parcel2.writeString("com.miui.guardprovider.aidl.IVirusObserver");
                return true;
            }
        }
    }

    void a(int i);

    void a(int i, int i2);

    void a(int i, int i2, VirusInfo[] virusInfoArr);

    void a(int i, String str);

    void a(int i, VirusInfo[] virusInfoArr);

    void a(UpdateInfo updateInfo);

    void c(int i);

    void d(int i);

    void j(int i);

    void l(int i);

    void n(int i);

    void p(int i);
}
