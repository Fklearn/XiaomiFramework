package com.market.pm;

import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.IInterface;
import android.os.ResultReceiver;

public interface IMarketInstallerService extends IInterface {

    public static abstract class Stub extends Binder implements IMarketInstallerService {

        private static class a implements IMarketInstallerService {

            /* renamed from: a  reason: collision with root package name */
            private IBinder f2220a;

            a(IBinder iBinder) {
                this.f2220a = iBinder;
            }

            public IBinder asBinder() {
                return this.f2220a;
            }
        }

        public Stub() {
            attachInterface(this, "com.market.pm.IMarketInstallerService");
        }

        public static IMarketInstallerService a(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface queryLocalInterface = iBinder.queryLocalInterface("com.market.pm.IMarketInstallerService");
            return (queryLocalInterface == null || !(queryLocalInterface instanceof IMarketInstallerService)) ? new a(iBinder) : (IMarketInstallerService) queryLocalInterface;
        }

        public IBinder asBinder() {
            return this;
        }

        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v1, resolved type: java.lang.Object} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v4, resolved type: android.os.Bundle} */
        /* JADX WARNING: Multi-variable type inference failed */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public boolean onTransact(int r4, android.os.Parcel r5, android.os.Parcel r6, int r7) {
            /*
                r3 = this;
                java.lang.String r0 = "com.market.pm.IMarketInstallerService"
                r1 = 1
                if (r4 == r1) goto L_0x0013
                r2 = 1598968902(0x5f4e5446, float:1.4867585E19)
                if (r4 == r2) goto L_0x000f
                boolean r4 = super.onTransact(r4, r5, r6, r7)
                return r4
            L_0x000f:
                r6.writeString(r0)
                return r1
            L_0x0013:
                r5.enforceInterface(r0)
                int r4 = r5.readInt()
                r7 = 0
                if (r4 == 0) goto L_0x0026
                android.os.Parcelable$Creator r4 = android.net.Uri.CREATOR
                java.lang.Object r4 = r4.createFromParcel(r5)
                android.net.Uri r4 = (android.net.Uri) r4
                goto L_0x0027
            L_0x0026:
                r4 = r7
            L_0x0027:
                int r0 = r5.readInt()
                if (r0 == 0) goto L_0x0036
                android.os.Parcelable$Creator r0 = android.os.ResultReceiver.CREATOR
                java.lang.Object r0 = r0.createFromParcel(r5)
                android.os.ResultReceiver r0 = (android.os.ResultReceiver) r0
                goto L_0x0037
            L_0x0036:
                r0 = r7
            L_0x0037:
                int r2 = r5.readInt()
                if (r2 == 0) goto L_0x0046
                android.os.Parcelable$Creator r7 = android.os.Bundle.CREATOR
                java.lang.Object r5 = r7.createFromParcel(r5)
                r7 = r5
                android.os.Bundle r7 = (android.os.Bundle) r7
            L_0x0046:
                r3.a(r4, r0, r7)
                r6.writeNoException()
                return r1
            */
            throw new UnsupportedOperationException("Method not decompiled: com.market.pm.IMarketInstallerService.Stub.onTransact(int, android.os.Parcel, android.os.Parcel, int):boolean");
        }
    }

    void a(Uri uri, ResultReceiver resultReceiver, Bundle bundle);
}
