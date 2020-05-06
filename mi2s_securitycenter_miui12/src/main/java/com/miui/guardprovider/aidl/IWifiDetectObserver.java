package com.miui.guardprovider.aidl;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;

public interface IWifiDetectObserver extends IInterface {

    public static abstract class Stub extends Binder implements IWifiDetectObserver {

        private static class a implements IWifiDetectObserver {

            /* renamed from: a  reason: collision with root package name */
            private IBinder f5469a;

            a(IBinder iBinder) {
                this.f5469a = iBinder;
            }

            public IBinder asBinder() {
                return this.f5469a;
            }
        }

        public Stub() {
            attachInterface(this, "com.miui.guardprovider.aidl.IWifiDetectObserver");
        }

        public static IWifiDetectObserver a(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface queryLocalInterface = iBinder.queryLocalInterface("com.miui.guardprovider.aidl.IWifiDetectObserver");
            return (queryLocalInterface == null || !(queryLocalInterface instanceof IWifiDetectObserver)) ? new a(iBinder) : (IWifiDetectObserver) queryLocalInterface;
        }

        public IBinder asBinder() {
            return this;
        }

        public boolean onTransact(int i, Parcel parcel, Parcel parcel2, int i2) {
            if (i == 1) {
                parcel.enforceInterface("com.miui.guardprovider.aidl.IWifiDetectObserver");
                o(parcel.readInt());
                return true;
            } else if (i != 1598968902) {
                return super.onTransact(i, parcel, parcel2, i2);
            } else {
                parcel2.writeString("com.miui.guardprovider.aidl.IWifiDetectObserver");
                return true;
            }
        }
    }

    void o(int i);
}
