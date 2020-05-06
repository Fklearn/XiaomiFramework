package com.xiaomi.accountsdk.account;

import android.accounts.Account;
import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.ParcelFileDescriptor;

public interface IXiaomiAccountService extends IInterface {

    public static abstract class Stub extends Binder implements IXiaomiAccountService {

        private static class a implements IXiaomiAccountService {

            /* renamed from: a  reason: collision with root package name */
            private IBinder f8241a;

            a(IBinder iBinder) {
                this.f8241a = iBinder;
            }

            public IBinder asBinder() {
                return this.f8241a;
            }

            public ParcelFileDescriptor c(Account account) {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.xiaomi.accountsdk.account.IXiaomiAccountService");
                    if (account != null) {
                        obtain.writeInt(1);
                        account.writeToParcel(obtain, 0);
                    } else {
                        obtain.writeInt(0);
                    }
                    this.f8241a.transact(6, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readInt() != 0 ? (ParcelFileDescriptor) ParcelFileDescriptor.CREATOR.createFromParcel(obtain2) : null;
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }
        }

        public Stub() {
            attachInterface(this, "com.xiaomi.accountsdk.account.IXiaomiAccountService");
        }

        public static IXiaomiAccountService a(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface queryLocalInterface = iBinder.queryLocalInterface("com.xiaomi.accountsdk.account.IXiaomiAccountService");
            return (queryLocalInterface == null || !(queryLocalInterface instanceof IXiaomiAccountService)) ? new a(iBinder) : (IXiaomiAccountService) queryLocalInterface;
        }

        public IBinder asBinder() {
            return this;
        }

        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v4, resolved type: java.lang.Object} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v2, resolved type: android.accounts.Account} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v8, resolved type: java.lang.Object} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v5, resolved type: android.accounts.Account} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v12, resolved type: java.lang.Object} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v8, resolved type: android.accounts.Account} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v16, resolved type: java.lang.Object} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v11, resolved type: android.accounts.Account} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v20, resolved type: java.lang.Object} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v14, resolved type: android.accounts.Account} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v24, resolved type: java.lang.Object} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v17, resolved type: android.accounts.Account} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v1, resolved type: java.lang.Object} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v20, resolved type: android.accounts.Account} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v36, resolved type: java.lang.Object} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v23, resolved type: android.accounts.Account} */
        /* JADX WARNING: Multi-variable type inference failed */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public boolean onTransact(int r5, android.os.Parcel r6, android.os.Parcel r7, int r8) {
            /*
                r4 = this;
                r0 = 1598968902(0x5f4e5446, float:1.4867585E19)
                r1 = 1
                java.lang.String r2 = "com.xiaomi.accountsdk.account.IXiaomiAccountService"
                if (r5 == r0) goto L_0x014b
                r0 = 0
                r3 = 0
                switch(r5) {
                    case 1: goto L_0x0133;
                    case 2: goto L_0x011b;
                    case 3: goto L_0x0103;
                    case 4: goto L_0x00eb;
                    case 5: goto L_0x00d3;
                    case 6: goto L_0x00ad;
                    case 7: goto L_0x007e;
                    case 8: goto L_0x0063;
                    case 9: goto L_0x003d;
                    case 10: goto L_0x002b;
                    case 11: goto L_0x0012;
                    default: goto L_0x000d;
                }
            L_0x000d:
                boolean r5 = super.onTransact(r5, r6, r7, r8)
                return r5
            L_0x0012:
                r6.enforceInterface(r2)
                java.lang.String r5 = r6.readString()
                java.lang.String r6 = r6.readString()
                boolean r5 = r4.e(r5, r6)
                r7.writeNoException()
                if (r5 == 0) goto L_0x0027
                r0 = r1
            L_0x0027:
                r7.writeInt(r0)
                return r1
            L_0x002b:
                r6.enforceInterface(r2)
                java.lang.String r5 = r6.readString()
                java.lang.String r5 = r4.n(r5)
            L_0x0036:
                r7.writeNoException()
                r7.writeString(r5)
                return r1
            L_0x003d:
                r6.enforceInterface(r2)
                int r5 = r6.readInt()
                if (r5 == 0) goto L_0x004f
                android.os.Parcelable$Creator r5 = android.accounts.Account.CREATOR
                java.lang.Object r5 = r5.createFromParcel(r6)
                r3 = r5
                android.accounts.Account r3 = (android.accounts.Account) r3
            L_0x004f:
                java.lang.String r5 = r6.readString()
                java.lang.String r8 = r6.readString()
                int r6 = r6.readInt()
                if (r6 == 0) goto L_0x005e
                r0 = r1
            L_0x005e:
                java.lang.String r5 = r4.a(r3, r5, r8, r0)
                goto L_0x0036
            L_0x0063:
                r6.enforceInterface(r2)
                java.lang.String r5 = r6.readString()
                android.os.ParcelFileDescriptor r5 = r4.l(r5)
                r7.writeNoException()
                if (r5 == 0) goto L_0x007a
                r7.writeInt(r1)
                r5.writeToParcel(r7, r1)
                goto L_0x007d
            L_0x007a:
                r7.writeInt(r0)
            L_0x007d:
                return r1
            L_0x007e:
                r6.enforceInterface(r2)
                int r5 = r6.readInt()
                if (r5 == 0) goto L_0x0089
                r5 = r1
                goto L_0x008a
            L_0x0089:
                r5 = r0
            L_0x008a:
                int r8 = r6.readInt()
                if (r8 == 0) goto L_0x0099
                android.os.Parcelable$Creator r8 = android.accounts.Account.CREATOR
                java.lang.Object r6 = r8.createFromParcel(r6)
                r3 = r6
                android.accounts.Account r3 = (android.accounts.Account) r3
            L_0x0099:
                com.xiaomi.accountsdk.account.XiaomiAccount r5 = r4.a(r5, r3)
                r7.writeNoException()
                if (r5 == 0) goto L_0x00a9
                r7.writeInt(r1)
                r5.writeToParcel(r7, r1)
                goto L_0x00ac
            L_0x00a9:
                r7.writeInt(r0)
            L_0x00ac:
                return r1
            L_0x00ad:
                r6.enforceInterface(r2)
                int r5 = r6.readInt()
                if (r5 == 0) goto L_0x00bf
                android.os.Parcelable$Creator r5 = android.accounts.Account.CREATOR
                java.lang.Object r5 = r5.createFromParcel(r6)
                r3 = r5
                android.accounts.Account r3 = (android.accounts.Account) r3
            L_0x00bf:
                android.os.ParcelFileDescriptor r5 = r4.c(r3)
                r7.writeNoException()
                if (r5 == 0) goto L_0x00cf
                r7.writeInt(r1)
                r5.writeToParcel(r7, r1)
                goto L_0x00d2
            L_0x00cf:
                r7.writeInt(r0)
            L_0x00d2:
                return r1
            L_0x00d3:
                r6.enforceInterface(r2)
                int r5 = r6.readInt()
                if (r5 == 0) goto L_0x00e5
                android.os.Parcelable$Creator r5 = android.accounts.Account.CREATOR
                java.lang.Object r5 = r5.createFromParcel(r6)
                r3 = r5
                android.accounts.Account r3 = (android.accounts.Account) r3
            L_0x00e5:
                java.lang.String r5 = r4.a(r3)
                goto L_0x0036
            L_0x00eb:
                r6.enforceInterface(r2)
                int r5 = r6.readInt()
                if (r5 == 0) goto L_0x00fd
                android.os.Parcelable$Creator r5 = android.accounts.Account.CREATOR
                java.lang.Object r5 = r5.createFromParcel(r6)
                r3 = r5
                android.accounts.Account r3 = (android.accounts.Account) r3
            L_0x00fd:
                java.lang.String r5 = r4.f(r3)
                goto L_0x0036
            L_0x0103:
                r6.enforceInterface(r2)
                int r5 = r6.readInt()
                if (r5 == 0) goto L_0x0115
                android.os.Parcelable$Creator r5 = android.accounts.Account.CREATOR
                java.lang.Object r5 = r5.createFromParcel(r6)
                r3 = r5
                android.accounts.Account r3 = (android.accounts.Account) r3
            L_0x0115:
                java.lang.String r5 = r4.d(r3)
                goto L_0x0036
            L_0x011b:
                r6.enforceInterface(r2)
                int r5 = r6.readInt()
                if (r5 == 0) goto L_0x012d
                android.os.Parcelable$Creator r5 = android.accounts.Account.CREATOR
                java.lang.Object r5 = r5.createFromParcel(r6)
                r3 = r5
                android.accounts.Account r3 = (android.accounts.Account) r3
            L_0x012d:
                java.lang.String r5 = r4.b(r3)
                goto L_0x0036
            L_0x0133:
                r6.enforceInterface(r2)
                int r5 = r6.readInt()
                if (r5 == 0) goto L_0x0145
                android.os.Parcelable$Creator r5 = android.accounts.Account.CREATOR
                java.lang.Object r5 = r5.createFromParcel(r6)
                r3 = r5
                android.accounts.Account r3 = (android.accounts.Account) r3
            L_0x0145:
                java.lang.String r5 = r4.e(r3)
                goto L_0x0036
            L_0x014b:
                r7.writeString(r2)
                return r1
            */
            throw new UnsupportedOperationException("Method not decompiled: com.xiaomi.accountsdk.account.IXiaomiAccountService.Stub.onTransact(int, android.os.Parcel, android.os.Parcel, int):boolean");
        }
    }

    XiaomiAccount a(boolean z, Account account);

    String a(Account account);

    String a(Account account, String str, String str2, boolean z);

    String b(Account account);

    ParcelFileDescriptor c(Account account);

    String d(Account account);

    String e(Account account);

    boolean e(String str, String str2);

    String f(Account account);

    ParcelFileDescriptor l(String str);

    String n(String str);
}
