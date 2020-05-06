package com.market.sdk;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;

public interface IDesktopRecommendResponse extends IInterface {

    public static abstract class Stub extends Binder implements IDesktopRecommendResponse {

        private static class a implements IDesktopRecommendResponse {

            /* renamed from: a  reason: collision with root package name */
            private IBinder f2222a;

            a(IBinder iBinder) {
                this.f2222a = iBinder;
            }

            public IBinder asBinder() {
                return this.f2222a;
            }
        }

        public Stub() {
            attachInterface(this, "com.market.sdk.IDesktopRecommendResponse");
        }

        public static IDesktopRecommendResponse a(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface queryLocalInterface = iBinder.queryLocalInterface("com.market.sdk.IDesktopRecommendResponse");
            return (queryLocalInterface == null || !(queryLocalInterface instanceof IDesktopRecommendResponse)) ? new a(iBinder) : (IDesktopRecommendResponse) queryLocalInterface;
        }

        public IBinder asBinder() {
            return this;
        }

        public boolean onTransact(int i, Parcel parcel, Parcel parcel2, int i2) {
            if (i == 1) {
                parcel.enforceInterface("com.market.sdk.IDesktopRecommendResponse");
                a(parcel.readInt() != 0 ? DesktopRecommendInfo.CREATOR.createFromParcel(parcel) : null);
            } else if (i == 2) {
                parcel.enforceInterface("com.market.sdk.IDesktopRecommendResponse");
                h();
            } else if (i != 1598968902) {
                return super.onTransact(i, parcel, parcel2, i2);
            } else {
                parcel2.writeString("com.market.sdk.IDesktopRecommendResponse");
                return true;
            }
            parcel2.writeNoException();
            return true;
        }
    }

    void a(DesktopRecommendInfo desktopRecommendInfo);

    void h();
}
