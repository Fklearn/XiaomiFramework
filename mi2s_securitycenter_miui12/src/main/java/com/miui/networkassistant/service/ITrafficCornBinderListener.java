package com.miui.networkassistant.service;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import com.miui.networkassistant.model.TrafficUsedStatus;

public interface ITrafficCornBinderListener extends IInterface {

    public static abstract class Stub extends Binder implements ITrafficCornBinderListener {
        private static final String DESCRIPTOR = "com.miui.networkassistant.service.ITrafficCornBinderListener";
        static final int TRANSACTION_onTrafficCorrected = 1;

        private static class Proxy implements ITrafficCornBinderListener {
            private IBinder mRemote;

            Proxy(IBinder iBinder) {
                this.mRemote = iBinder;
            }

            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return Stub.DESCRIPTOR;
            }

            public void onTrafficCorrected(TrafficUsedStatus trafficUsedStatus) {
                Parcel obtain = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (trafficUsedStatus != null) {
                        obtain.writeInt(1);
                        trafficUsedStatus.writeToParcel(obtain, 0);
                    } else {
                        obtain.writeInt(0);
                    }
                    this.mRemote.transact(1, obtain, (Parcel) null, 1);
                } finally {
                    obtain.recycle();
                }
            }
        }

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static ITrafficCornBinderListener asInterface(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface queryLocalInterface = iBinder.queryLocalInterface(DESCRIPTOR);
            return (queryLocalInterface == null || !(queryLocalInterface instanceof ITrafficCornBinderListener)) ? new Proxy(iBinder) : (ITrafficCornBinderListener) queryLocalInterface;
        }

        public IBinder asBinder() {
            return this;
        }

        public boolean onTransact(int i, Parcel parcel, Parcel parcel2, int i2) {
            if (i == 1) {
                parcel.enforceInterface(DESCRIPTOR);
                onTrafficCorrected(parcel.readInt() != 0 ? TrafficUsedStatus.CREATOR.createFromParcel(parcel) : null);
                return true;
            } else if (i != 1598968902) {
                return super.onTransact(i, parcel, parcel2, i2);
            } else {
                parcel2.writeString(DESCRIPTOR);
                return true;
            }
        }
    }

    void onTrafficCorrected(TrafficUsedStatus trafficUsedStatus);
}
