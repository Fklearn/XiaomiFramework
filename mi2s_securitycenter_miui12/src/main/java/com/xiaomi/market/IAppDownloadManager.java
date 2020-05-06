package com.xiaomi.market;

import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;

public interface IAppDownloadManager extends IInterface {

    public static abstract class Stub extends Binder implements IAppDownloadManager {

        private static class a implements IAppDownloadManager {

            /* renamed from: a  reason: collision with root package name */
            private IBinder f8331a;

            a(IBinder iBinder) {
                this.f8331a = iBinder;
            }

            public void a(Bundle bundle) {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.xiaomi.market.IAppDownloadManager");
                    if (bundle != null) {
                        obtain.writeInt(1);
                        bundle.writeToParcel(obtain, 0);
                    } else {
                        obtain.writeInt(0);
                    }
                    this.f8331a.transact(1, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void a(String str, int i) {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.xiaomi.market.IAppDownloadManager");
                    obtain.writeString(str);
                    obtain.writeInt(i);
                    this.f8331a.transact(8, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public IBinder asBinder() {
                return this.f8331a;
            }

            public void c(Uri uri) {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.xiaomi.market.IAppDownloadManager");
                    if (uri != null) {
                        obtain.writeInt(1);
                        uri.writeToParcel(obtain, 0);
                    } else {
                        obtain.writeInt(0);
                    }
                    this.f8331a.transact(5, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }
        }

        public Stub() {
            attachInterface(this, "com.xiaomi.market.IAppDownloadManager");
        }

        public static IAppDownloadManager a(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface queryLocalInterface = iBinder.queryLocalInterface("com.xiaomi.market.IAppDownloadManager");
            return (queryLocalInterface == null || !(queryLocalInterface instanceof IAppDownloadManager)) ? new a(iBinder) : (IAppDownloadManager) queryLocalInterface;
        }

        public IBinder asBinder() {
            return this;
        }

        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v1, resolved type: android.os.Bundle} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v4, resolved type: android.net.Uri} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v7, resolved type: android.net.Uri} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v10, resolved type: android.net.Uri} */
        /* JADX WARNING: type inference failed for: r3v0 */
        /* JADX WARNING: type inference failed for: r3v13 */
        /* JADX WARNING: type inference failed for: r3v14 */
        /* JADX WARNING: type inference failed for: r3v15 */
        /* JADX WARNING: type inference failed for: r3v16 */
        /* JADX WARNING: Multi-variable type inference failed */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public boolean onTransact(int r5, android.os.Parcel r6, android.os.Parcel r7, int r8) {
            /*
                r4 = this;
                r0 = 1598968902(0x5f4e5446, float:1.4867585E19)
                java.lang.String r1 = "com.xiaomi.market.IAppDownloadManager"
                r2 = 1
                if (r5 == r0) goto L_0x00c8
                r0 = 0
                r3 = 0
                switch(r5) {
                    case 1: goto L_0x00b1;
                    case 2: goto L_0x0098;
                    case 3: goto L_0x007f;
                    case 4: goto L_0x0066;
                    case 5: goto L_0x0050;
                    case 6: goto L_0x003a;
                    case 7: goto L_0x0024;
                    case 8: goto L_0x0012;
                    default: goto L_0x000d;
                }
            L_0x000d:
                boolean r5 = super.onTransact(r5, r6, r7, r8)
                return r5
            L_0x0012:
                r6.enforceInterface(r1)
                java.lang.String r5 = r6.readString()
                int r6 = r6.readInt()
                r4.a((java.lang.String) r5, (int) r6)
            L_0x0020:
                r7.writeNoException()
                return r2
            L_0x0024:
                r6.enforceInterface(r1)
                int r5 = r6.readInt()
                if (r5 == 0) goto L_0x0036
                android.os.Parcelable$Creator r5 = android.net.Uri.CREATOR
                java.lang.Object r5 = r5.createFromParcel(r6)
                r3 = r5
                android.net.Uri r3 = (android.net.Uri) r3
            L_0x0036:
                r4.a((android.net.Uri) r3)
                goto L_0x0020
            L_0x003a:
                r6.enforceInterface(r1)
                int r5 = r6.readInt()
                if (r5 == 0) goto L_0x004c
                android.os.Parcelable$Creator r5 = android.net.Uri.CREATOR
                java.lang.Object r5 = r5.createFromParcel(r6)
                r3 = r5
                android.net.Uri r3 = (android.net.Uri) r3
            L_0x004c:
                r4.b(r3)
                goto L_0x0020
            L_0x0050:
                r6.enforceInterface(r1)
                int r5 = r6.readInt()
                if (r5 == 0) goto L_0x0062
                android.os.Parcelable$Creator r5 = android.net.Uri.CREATOR
                java.lang.Object r5 = r5.createFromParcel(r6)
                r3 = r5
                android.net.Uri r3 = (android.net.Uri) r3
            L_0x0062:
                r4.c(r3)
                goto L_0x0020
            L_0x0066:
                r6.enforceInterface(r1)
                java.lang.String r5 = r6.readString()
                java.lang.String r6 = r6.readString()
                boolean r5 = r4.b(r5, r6)
                r7.writeNoException()
                if (r5 == 0) goto L_0x007b
                r0 = r2
            L_0x007b:
                r7.writeInt(r0)
                return r2
            L_0x007f:
                r6.enforceInterface(r1)
                java.lang.String r5 = r6.readString()
                java.lang.String r6 = r6.readString()
                boolean r5 = r4.a((java.lang.String) r5, (java.lang.String) r6)
                r7.writeNoException()
                if (r5 == 0) goto L_0x0094
                r0 = r2
            L_0x0094:
                r7.writeInt(r0)
                return r2
            L_0x0098:
                r6.enforceInterface(r1)
                java.lang.String r5 = r6.readString()
                java.lang.String r6 = r6.readString()
                boolean r5 = r4.d(r5, r6)
                r7.writeNoException()
                if (r5 == 0) goto L_0x00ad
                r0 = r2
            L_0x00ad:
                r7.writeInt(r0)
                return r2
            L_0x00b1:
                r6.enforceInterface(r1)
                int r5 = r6.readInt()
                if (r5 == 0) goto L_0x00c3
                android.os.Parcelable$Creator r5 = android.os.Bundle.CREATOR
                java.lang.Object r5 = r5.createFromParcel(r6)
                r3 = r5
                android.os.Bundle r3 = (android.os.Bundle) r3
            L_0x00c3:
                r4.a((android.os.Bundle) r3)
                goto L_0x0020
            L_0x00c8:
                r7.writeString(r1)
                return r2
            */
            throw new UnsupportedOperationException("Method not decompiled: com.xiaomi.market.IAppDownloadManager.Stub.onTransact(int, android.os.Parcel, android.os.Parcel, int):boolean");
        }
    }

    void a(Uri uri);

    void a(Bundle bundle);

    void a(String str, int i);

    boolean a(String str, String str2);

    void b(Uri uri);

    boolean b(String str, String str2);

    void c(Uri uri);

    boolean d(String str, String str2);
}
