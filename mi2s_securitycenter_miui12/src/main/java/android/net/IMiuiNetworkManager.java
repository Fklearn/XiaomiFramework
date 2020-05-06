package android.net;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;

public interface IMiuiNetworkManager extends IInterface {

    public static abstract class Stub extends Binder implements IMiuiNetworkManager {
        private static final String DESCRIPTOR = "android.net.IMiuiNetworkManager";
        static final int TRANSACTION_setNetworkTrafficPolicy = 1;
        static final int TRANSACTION_setRpsStatus = 2;

        private static class Proxy implements IMiuiNetworkManager {
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

            public boolean setNetworkTrafficPolicy(int i) {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeInt(i);
                    boolean z = false;
                    this.mRemote.transact(1, obtain, obtain2, 0);
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

            public boolean setRpsStatus(boolean z) {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    boolean z2 = true;
                    obtain.writeInt(z ? 1 : 0);
                    this.mRemote.transact(2, obtain, obtain2, 0);
                    obtain2.readException();
                    if (obtain2.readInt() == 0) {
                        z2 = false;
                    }
                    return z2;
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }
        }

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IMiuiNetworkManager asInterface(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface queryLocalInterface = iBinder.queryLocalInterface(DESCRIPTOR);
            return (queryLocalInterface == null || !(queryLocalInterface instanceof IMiuiNetworkManager)) ? new Proxy(iBinder) : (IMiuiNetworkManager) queryLocalInterface;
        }

        public IBinder asBinder() {
            return this;
        }

        public boolean onTransact(int i, Parcel parcel, Parcel parcel2, int i2) {
            int i3 = 0;
            if (i == 1) {
                parcel.enforceInterface(DESCRIPTOR);
                boolean networkTrafficPolicy = setNetworkTrafficPolicy(parcel.readInt());
                parcel2.writeNoException();
                if (networkTrafficPolicy) {
                    i3 = 1;
                }
                parcel2.writeInt(i3);
                return true;
            } else if (i == 2) {
                parcel.enforceInterface(DESCRIPTOR);
                boolean rpsStatus = setRpsStatus(parcel.readInt() != 0);
                parcel2.writeNoException();
                if (rpsStatus) {
                    i3 = 1;
                }
                parcel2.writeInt(i3);
                return true;
            } else if (i != 1598968902) {
                return super.onTransact(i, parcel, parcel2, i2);
            } else {
                parcel2.writeString(DESCRIPTOR);
                return true;
            }
        }
    }

    boolean setNetworkTrafficPolicy(int i);

    boolean setRpsStatus(boolean z);
}
