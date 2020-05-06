package miui.slide;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import miui.slide.ISlideChangeListener;

public interface ISlideManagerService extends IInterface {

    public static abstract class Stub extends Binder implements ISlideManagerService {
        private static final String DESCRIPTOR = "miui.slide.ISlideManagerService";
        static final int TRANSACTION_registerSlideChangeListener = 1;
        static final int TRANSACTION_unregisterSlideChangeListener = 2;

        private static class Proxy implements ISlideManagerService {
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

            public void registerSlideChangeListener(String str, ISlideChangeListener iSlideChangeListener) {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeString(str);
                    obtain.writeStrongBinder(iSlideChangeListener != null ? iSlideChangeListener.asBinder() : null);
                    this.mRemote.transact(1, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void unregisterSlideChangeListener(ISlideChangeListener iSlideChangeListener) {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeStrongBinder(iSlideChangeListener != null ? iSlideChangeListener.asBinder() : null);
                    this.mRemote.transact(2, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }
        }

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static ISlideManagerService asInterface(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface queryLocalInterface = iBinder.queryLocalInterface(DESCRIPTOR);
            return (queryLocalInterface == null || !(queryLocalInterface instanceof ISlideManagerService)) ? new Proxy(iBinder) : (ISlideManagerService) queryLocalInterface;
        }

        public IBinder asBinder() {
            return this;
        }

        public boolean onTransact(int i, Parcel parcel, Parcel parcel2, int i2) {
            if (i == 1) {
                parcel.enforceInterface(DESCRIPTOR);
                registerSlideChangeListener(parcel.readString(), ISlideChangeListener.Stub.asInterface(parcel.readStrongBinder()));
            } else if (i == 2) {
                parcel.enforceInterface(DESCRIPTOR);
                unregisterSlideChangeListener(ISlideChangeListener.Stub.asInterface(parcel.readStrongBinder()));
            } else if (i != 1598968902) {
                return super.onTransact(i, parcel, parcel2, i2);
            } else {
                parcel2.writeString(DESCRIPTOR);
                return true;
            }
            parcel2.writeNoException();
            return true;
        }
    }

    void registerSlideChangeListener(String str, ISlideChangeListener iSlideChangeListener);

    void unregisterSlideChangeListener(ISlideChangeListener iSlideChangeListener);
}
