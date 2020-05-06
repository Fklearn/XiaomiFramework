package c.a.b.a;

import android.os.IBinder;
import android.os.Parcel;

class d implements b {
    d() {
    }

    public String a(IBinder iBinder, String str) {
        Parcel obtain = Parcel.obtain();
        Parcel obtain2 = Parcel.obtain();
        try {
            obtain.writeInterfaceToken("com.xiaomi.accountsdk.account.IXiaomiAccountService");
            obtain.writeString(str);
            iBinder.transact(10, obtain, obtain2, 0);
            obtain2.readException();
            return obtain2.readString();
        } finally {
            obtain2.recycle();
            obtain.recycle();
        }
    }

    public boolean a(IBinder iBinder, String str, String str2) {
        Parcel obtain = Parcel.obtain();
        Parcel obtain2 = Parcel.obtain();
        try {
            obtain.writeInterfaceToken("com.xiaomi.accountsdk.account.IXiaomiAccountService");
            obtain.writeString(str);
            obtain.writeString(str2);
            boolean z = false;
            iBinder.transact(11, obtain, obtain2, 0);
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
