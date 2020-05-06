package com.xiaomi.ad.feedback;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import com.xiaomi.ad.feedback.IAdFeedbackListener;
import java.util.List;

public interface IAdFeedbackService extends IInterface {

    public static abstract class Stub extends Binder implements IAdFeedbackService {
        private static final String DESCRIPTOR = "com.xiaomi.ad.feedback.IAdFeedbackService";
        static final int TRANSACTION_showFeedbackWindow = 1;
        static final int TRANSACTION_showFeedbackWindowAndTrackResult = 2;
        static final int TRANSACTION_showFeedbackWindowAndTrackResultForMultiAds = 3;

        private static class Proxy implements IAdFeedbackService {
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

            public void showFeedbackWindow(IAdFeedbackListener iAdFeedbackListener) {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeStrongBinder(iAdFeedbackListener != null ? iAdFeedbackListener.asBinder() : null);
                    this.mRemote.transact(1, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void showFeedbackWindowAndTrackResult(IAdFeedbackListener iAdFeedbackListener, String str, String str2, String str3) {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeStrongBinder(iAdFeedbackListener != null ? iAdFeedbackListener.asBinder() : null);
                    obtain.writeString(str);
                    obtain.writeString(str2);
                    obtain.writeString(str3);
                    this.mRemote.transact(2, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void showFeedbackWindowAndTrackResultForMultiAds(IAdFeedbackListener iAdFeedbackListener, String str, String str2, List<String> list) {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeStrongBinder(iAdFeedbackListener != null ? iAdFeedbackListener.asBinder() : null);
                    obtain.writeString(str);
                    obtain.writeString(str2);
                    obtain.writeStringList(list);
                    this.mRemote.transact(3, obtain, obtain2, 0);
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

        public static IAdFeedbackService asInterface(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface queryLocalInterface = iBinder.queryLocalInterface(DESCRIPTOR);
            return (queryLocalInterface == null || !(queryLocalInterface instanceof IAdFeedbackService)) ? new Proxy(iBinder) : (IAdFeedbackService) queryLocalInterface;
        }

        public IBinder asBinder() {
            return this;
        }

        public boolean onTransact(int i, Parcel parcel, Parcel parcel2, int i2) {
            if (i == 1) {
                parcel.enforceInterface(DESCRIPTOR);
                showFeedbackWindow(IAdFeedbackListener.Stub.asInterface(parcel.readStrongBinder()));
            } else if (i == 2) {
                parcel.enforceInterface(DESCRIPTOR);
                showFeedbackWindowAndTrackResult(IAdFeedbackListener.Stub.asInterface(parcel.readStrongBinder()), parcel.readString(), parcel.readString(), parcel.readString());
            } else if (i == 3) {
                parcel.enforceInterface(DESCRIPTOR);
                showFeedbackWindowAndTrackResultForMultiAds(IAdFeedbackListener.Stub.asInterface(parcel.readStrongBinder()), parcel.readString(), parcel.readString(), parcel.createStringArrayList());
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

    void showFeedbackWindow(IAdFeedbackListener iAdFeedbackListener);

    void showFeedbackWindowAndTrackResult(IAdFeedbackListener iAdFeedbackListener, String str, String str2, String str3);

    void showFeedbackWindowAndTrackResultForMultiAds(IAdFeedbackListener iAdFeedbackListener, String str, String str2, List<String> list);
}
