package com.xiaomi.rcs.aidl;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;

public interface IRcsManager extends IInterface {

    public static abstract class Stub extends Binder implements IRcsManager {
        public Stub() {
            attachInterface(this, "com.xiaomi.rcs.aidl.IRcsManager");
        }

        public IBinder asBinder() {
            return this;
        }

        public boolean onTransact(int i, Parcel parcel, Parcel parcel2, int i2) {
            if (i == 1) {
                parcel.enforceInterface("com.xiaomi.rcs.aidl.IRcsManager");
                boolean Q = Q();
                parcel2.writeNoException();
                parcel2.writeInt(Q ? 1 : 0);
                return true;
            } else if (i == 2) {
                parcel.enforceInterface("com.xiaomi.rcs.aidl.IRcsManager");
                p(parcel.readString());
                parcel2.writeNoException();
                return true;
            } else if (i != 1598968902) {
                return super.onTransact(i, parcel, parcel2, i2);
            } else {
                parcel2.writeString("com.xiaomi.rcs.aidl.IRcsManager");
                return true;
            }
        }
    }

    boolean Q();

    void p(String str);
}
