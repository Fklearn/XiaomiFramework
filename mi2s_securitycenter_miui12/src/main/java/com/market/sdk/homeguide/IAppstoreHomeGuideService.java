package com.market.sdk.homeguide;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.ResultReceiver;

public interface IAppstoreHomeGuideService extends IInterface {

    public static abstract class Stub extends Binder implements IAppstoreHomeGuideService {
        public Stub() {
            attachInterface(this, "com.market.sdk.homeguide.IAppstoreHomeGuideService");
        }

        public IBinder asBinder() {
            return this;
        }

        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v1, resolved type: com.market.sdk.homeguide.HomeUserGuideData} */
        /* JADX WARNING: type inference failed for: r0v0 */
        /* JADX WARNING: type inference failed for: r0v4, types: [android.os.ResultReceiver] */
        /* JADX WARNING: type inference failed for: r0v7 */
        /* JADX WARNING: type inference failed for: r0v8 */
        /* JADX WARNING: Multi-variable type inference failed */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public boolean onTransact(int r5, android.os.Parcel r6, android.os.Parcel r7, int r8) {
            /*
                r4 = this;
                r0 = 0
                java.lang.String r1 = "com.market.sdk.homeguide.IAppstoreHomeGuideService"
                r2 = 1
                if (r5 == r2) goto L_0x0030
                r3 = 2
                if (r5 == r3) goto L_0x0017
                r0 = 1598968902(0x5f4e5446, float:1.4867585E19)
                if (r5 == r0) goto L_0x0013
                boolean r5 = super.onTransact(r5, r6, r7, r8)
                return r5
            L_0x0013:
                r7.writeString(r1)
                return r2
            L_0x0017:
                r6.enforceInterface(r1)
                int r5 = r6.readInt()
                if (r5 == 0) goto L_0x0029
                android.os.Parcelable$Creator r5 = android.os.ResultReceiver.CREATOR
                java.lang.Object r5 = r5.createFromParcel(r6)
                r0 = r5
                android.os.ResultReceiver r0 = (android.os.ResultReceiver) r0
            L_0x0029:
                r4.c(r0)
                r7.writeNoException()
                return r2
            L_0x0030:
                r6.enforceInterface(r1)
                int r5 = r6.readInt()
                if (r5 == 0) goto L_0x0042
                android.os.Parcelable$Creator<com.market.sdk.homeguide.HomeUserGuideData> r5 = com.market.sdk.homeguide.HomeUserGuideData.CREATOR
                java.lang.Object r5 = r5.createFromParcel(r6)
                r0 = r5
                com.market.sdk.homeguide.HomeUserGuideData r0 = (com.market.sdk.homeguide.HomeUserGuideData) r0
            L_0x0042:
                com.market.sdk.homeguide.HomeUserGuideResult r5 = r4.a(r0)
                r7.writeNoException()
                if (r5 == 0) goto L_0x0052
                r7.writeInt(r2)
                r5.writeToParcel(r7, r2)
                goto L_0x0056
            L_0x0052:
                r5 = 0
                r7.writeInt(r5)
            L_0x0056:
                return r2
            */
            throw new UnsupportedOperationException("Method not decompiled: com.market.sdk.homeguide.IAppstoreHomeGuideService.Stub.onTransact(int, android.os.Parcel, android.os.Parcel, int):boolean");
        }
    }

    HomeUserGuideResult a(HomeUserGuideData homeUserGuideData);

    void c(ResultReceiver resultReceiver);
}
