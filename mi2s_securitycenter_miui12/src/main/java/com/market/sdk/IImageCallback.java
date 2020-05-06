package com.market.sdk;

import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;

public interface IImageCallback extends IInterface {

    public static abstract class Stub extends Binder implements IImageCallback {

        private static class a implements IImageCallback {

            /* renamed from: a  reason: collision with root package name */
            private IBinder f2223a;

            a(IBinder iBinder) {
                this.f2223a = iBinder;
            }

            public IBinder asBinder() {
                return this.f2223a;
            }
        }

        public Stub() {
            attachInterface(this, "com.market.sdk.IImageCallback");
        }

        public static IImageCallback a(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface queryLocalInterface = iBinder.queryLocalInterface("com.market.sdk.IImageCallback");
            return (queryLocalInterface == null || !(queryLocalInterface instanceof IImageCallback)) ? new a(iBinder) : (IImageCallback) queryLocalInterface;
        }

        public IBinder asBinder() {
            return this;
        }

        public boolean onTransact(int i, Parcel parcel, Parcel parcel2, int i2) {
            if (i == 1) {
                parcel.enforceInterface("com.market.sdk.IImageCallback");
                a(parcel.readString(), parcel.readInt() != 0 ? (Uri) Uri.CREATOR.createFromParcel(parcel) : null);
            } else if (i == 2) {
                parcel.enforceInterface("com.market.sdk.IImageCallback");
                j(parcel.readString());
            } else if (i != 1598968902) {
                return super.onTransact(i, parcel, parcel2, i2);
            } else {
                parcel2.writeString("com.market.sdk.IImageCallback");
                return true;
            }
            parcel2.writeNoException();
            return true;
        }
    }

    void a(String str, Uri uri);

    void j(String str);
}
