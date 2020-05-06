package com.market.sdk;

import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.IInterface;
import android.os.ResultReceiver;
import java.util.List;

public interface IMarketService extends IInterface {

    public static abstract class Stub extends Binder implements IMarketService {

        private static class a implements IMarketService {

            /* renamed from: a  reason: collision with root package name */
            private IBinder f2224a;

            a(IBinder iBinder) {
                this.f2224a = iBinder;
            }

            public IBinder asBinder() {
                return this.f2224a;
            }
        }

        public Stub() {
            attachInterface(this, "com.market.sdk.IMarketService");
        }

        public static IMarketService a(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface queryLocalInterface = iBinder.queryLocalInterface("com.market.sdk.IMarketService");
            return (queryLocalInterface == null || !(queryLocalInterface instanceof IMarketService)) ? new a(iBinder) : (IMarketService) queryLocalInterface;
        }

        public IBinder asBinder() {
            return this;
        }

        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v18, resolved type: java.lang.Object} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v4, resolved type: android.os.ResultReceiver} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v15, resolved type: java.lang.Object} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v7, resolved type: android.os.ResultReceiver} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v16, resolved type: java.lang.Object} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v10, resolved type: android.os.ResultReceiver} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v17, resolved type: java.lang.Object} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v13, resolved type: android.os.ResultReceiver} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v29, resolved type: java.lang.Object} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v16, resolved type: android.os.ResultReceiver} */
        /* JADX WARNING: Multi-variable type inference failed */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public boolean onTransact(int r11, android.os.Parcel r12, android.os.Parcel r13, int r14) {
            /*
                r10 = this;
                r0 = 1598968902(0x5f4e5446, float:1.4867585E19)
                java.lang.String r1 = "com.market.sdk.IMarketService"
                r2 = 1
                if (r11 == r0) goto L_0x01c6
                r0 = 0
                r3 = 0
                switch(r11) {
                    case 1: goto L_0x019e;
                    case 2: goto L_0x0176;
                    case 3: goto L_0x0165;
                    case 4: goto L_0x0153;
                    case 5: goto L_0x0139;
                    case 6: goto L_0x011b;
                    case 7: goto L_0x00f8;
                    case 8: goto L_0x00e3;
                    case 9: goto L_0x00d5;
                    case 10: goto L_0x00c7;
                    case 11: goto L_0x00b5;
                    case 12: goto L_0x009c;
                    case 13: goto L_0x007f;
                    case 14: goto L_0x0054;
                    case 15: goto L_0x002b;
                    case 16: goto L_0x0012;
                    default: goto L_0x000d;
                }
            L_0x000d:
                boolean r11 = super.onTransact(r11, r12, r13, r14)
                return r11
            L_0x0012:
                r12.enforceInterface(r1)
                int r11 = r12.readInt()
                if (r11 == 0) goto L_0x0024
                android.os.Parcelable$Creator r11 = android.os.ResultReceiver.CREATOR
                java.lang.Object r11 = r11.createFromParcel(r12)
                r0 = r11
                android.os.ResultReceiver r0 = (android.os.ResultReceiver) r0
            L_0x0024:
                r10.a((android.os.ResultReceiver) r0)
                r13.writeNoException()
                return r2
            L_0x002b:
                r12.enforceInterface(r1)
                int r11 = r12.readInt()
                if (r11 == 0) goto L_0x003d
                android.os.Parcelable$Creator r11 = android.os.Bundle.CREATOR
                java.lang.Object r11 = r11.createFromParcel(r12)
                android.os.Bundle r11 = (android.os.Bundle) r11
                goto L_0x003e
            L_0x003d:
                r11 = r0
            L_0x003e:
                int r14 = r12.readInt()
                if (r14 == 0) goto L_0x004d
                android.os.Parcelable$Creator r14 = android.os.ResultReceiver.CREATOR
                java.lang.Object r12 = r14.createFromParcel(r12)
                r0 = r12
                android.os.ResultReceiver r0 = (android.os.ResultReceiver) r0
            L_0x004d:
                r10.a((android.os.Bundle) r11, (android.os.ResultReceiver) r0)
                r13.writeNoException()
                return r2
            L_0x0054:
                r12.enforceInterface(r1)
                long r4 = r12.readLong()
                java.lang.String r6 = r12.readString()
                java.util.ArrayList r11 = r12.createStringArrayList()
                int r14 = r12.readInt()
                if (r14 == 0) goto L_0x0072
                android.os.Parcelable$Creator r14 = android.os.ResultReceiver.CREATOR
                java.lang.Object r12 = r14.createFromParcel(r12)
                r0 = r12
                android.os.ResultReceiver r0 = (android.os.ResultReceiver) r0
            L_0x0072:
                r8 = r0
                r3 = r10
                r7 = r11
                r3.a((long) r4, (java.lang.String) r6, (java.util.List<java.lang.String>) r7, (android.os.ResultReceiver) r8)
                r13.writeNoException()
                r13.writeStringList(r11)
                return r2
            L_0x007f:
                r12.enforceInterface(r1)
                java.lang.String[] r11 = r12.createStringArray()
                int r14 = r12.readInt()
                if (r14 == 0) goto L_0x0095
                android.os.Parcelable$Creator r14 = android.os.ResultReceiver.CREATOR
                java.lang.Object r12 = r14.createFromParcel(r12)
                r0 = r12
                android.os.ResultReceiver r0 = (android.os.ResultReceiver) r0
            L_0x0095:
                r10.a((java.lang.String[]) r11, (android.os.ResultReceiver) r0)
                r13.writeNoException()
                return r2
            L_0x009c:
                r12.enforceInterface(r1)
                int r11 = r12.readInt()
                if (r11 == 0) goto L_0x00ae
                android.os.Parcelable$Creator r11 = android.os.ResultReceiver.CREATOR
                java.lang.Object r11 = r11.createFromParcel(r12)
                r0 = r11
                android.os.ResultReceiver r0 = (android.os.ResultReceiver) r0
            L_0x00ae:
                r10.b(r0)
                r13.writeNoException()
                return r2
            L_0x00b5:
                r12.enforceInterface(r1)
                java.lang.String[] r11 = r12.createStringArray()
                int r11 = r10.a((java.lang.String[]) r11)
                r13.writeNoException()
                r13.writeInt(r11)
                return r2
            L_0x00c7:
                r12.enforceInterface(r1)
                java.lang.String r11 = r10.g()
                r13.writeNoException()
                r13.writeString(r11)
                return r2
            L_0x00d5:
                r12.enforceInterface(r1)
                java.lang.String r11 = r10.j()
                r13.writeNoException()
                r13.writeString(r11)
                return r2
            L_0x00e3:
                r12.enforceInterface(r1)
                java.lang.String r11 = r12.readString()
                boolean r11 = r10.c(r11)
                r13.writeNoException()
                if (r11 == 0) goto L_0x00f4
                r3 = r2
            L_0x00f4:
                r13.writeInt(r3)
                return r2
            L_0x00f8:
                r12.enforceInterface(r1)
                long r5 = r12.readLong()
                java.lang.String r7 = r12.readString()
                java.util.ArrayList r11 = r12.createStringArrayList()
                android.os.IBinder r12 = r12.readStrongBinder()
                com.market.sdk.IDesktopRecommendResponse r9 = com.market.sdk.IDesktopRecommendResponse.Stub.a(r12)
                r4 = r10
                r8 = r11
                r4.a((long) r5, (java.lang.String) r7, (java.util.List<java.lang.String>) r8, (com.market.sdk.IDesktopRecommendResponse) r9)
                r13.writeNoException()
                r13.writeStringList(r11)
                return r2
            L_0x011b:
                r12.enforceInterface(r1)
                java.lang.String r11 = r12.readString()
                int r14 = r12.readInt()
                int r0 = r12.readInt()
                android.os.IBinder r12 = r12.readStrongBinder()
                com.market.sdk.IImageCallback r12 = com.market.sdk.IImageCallback.Stub.a(r12)
                r10.a((java.lang.String) r11, (int) r14, (int) r0, (com.market.sdk.IImageCallback) r12)
                r13.writeNoException()
                return r2
            L_0x0139:
                r12.enforceInterface(r1)
                java.lang.String r11 = r12.readString()
                java.lang.String r14 = r12.readString()
                android.os.IBinder r12 = r12.readStrongBinder()
                com.market.sdk.IImageCallback r12 = com.market.sdk.IImageCallback.Stub.a(r12)
                r10.a((java.lang.String) r11, (java.lang.String) r14, (com.market.sdk.IImageCallback) r12)
                r13.writeNoException()
                return r2
            L_0x0153:
                r12.enforceInterface(r1)
                java.lang.String r11 = r12.readString()
                java.lang.String r12 = r12.readString()
                r10.c(r11, r12)
                r13.writeNoException()
                return r2
            L_0x0165:
                r12.enforceInterface(r1)
                boolean r11 = r10.i()
                r13.writeNoException()
                if (r11 == 0) goto L_0x0172
                r3 = r2
            L_0x0172:
                r13.writeInt(r3)
                return r2
            L_0x0176:
                r12.enforceInterface(r1)
                java.lang.String r11 = r12.readString()
                java.lang.String r14 = r12.readString()
                int r12 = r12.readInt()
                if (r12 == 0) goto L_0x0189
                r12 = r2
                goto L_0x018a
            L_0x0189:
                r12 = r3
            L_0x018a:
                com.market.sdk.ApkVerifyInfo r11 = r10.a((java.lang.String) r11, (java.lang.String) r14, (boolean) r12)
                r13.writeNoException()
                if (r11 == 0) goto L_0x019a
                r13.writeInt(r2)
                r11.writeToParcel(r13, r2)
                goto L_0x019d
            L_0x019a:
                r13.writeInt(r3)
            L_0x019d:
                return r2
            L_0x019e:
                r12.enforceInterface(r1)
                java.lang.String r11 = r12.readString()
                java.lang.String r14 = r12.readString()
                int r12 = r12.readInt()
                if (r12 == 0) goto L_0x01b1
                r12 = r2
                goto L_0x01b2
            L_0x01b1:
                r12 = r3
            L_0x01b2:
                com.market.sdk.ApkVerifyInfo r11 = r10.b(r11, r14, r12)
                r13.writeNoException()
                if (r11 == 0) goto L_0x01c2
                r13.writeInt(r2)
                r11.writeToParcel(r13, r2)
                goto L_0x01c5
            L_0x01c2:
                r13.writeInt(r3)
            L_0x01c5:
                return r2
            L_0x01c6:
                r13.writeString(r1)
                return r2
            */
            throw new UnsupportedOperationException("Method not decompiled: com.market.sdk.IMarketService.Stub.onTransact(int, android.os.Parcel, android.os.Parcel, int):boolean");
        }
    }

    int a(String[] strArr);

    ApkVerifyInfo a(String str, String str2, boolean z);

    void a(long j, String str, List<String> list, ResultReceiver resultReceiver);

    void a(long j, String str, List<String> list, IDesktopRecommendResponse iDesktopRecommendResponse);

    void a(Bundle bundle, ResultReceiver resultReceiver);

    void a(ResultReceiver resultReceiver);

    void a(String str, int i, int i2, IImageCallback iImageCallback);

    void a(String str, String str2, IImageCallback iImageCallback);

    void a(String[] strArr, ResultReceiver resultReceiver);

    ApkVerifyInfo b(String str, String str2, boolean z);

    void b(ResultReceiver resultReceiver);

    void c(String str, String str2);

    boolean c(String str);

    String g();

    boolean i();

    String j();
}
