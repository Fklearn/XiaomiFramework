package com.miui.powerkeeper.feedbackcontrol;

import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import com.miui.powerkeeper.feedbackcontrol.IFeedbackControlListener;

public interface IFeedbackControl extends IInterface {

    public static abstract class Stub extends Binder implements IFeedbackControl {

        private static class a implements IFeedbackControl {

            /* renamed from: a  reason: collision with root package name */
            private IBinder f7373a;

            a(IBinder iBinder) {
                this.f7373a = iBinder;
            }

            public void G() {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.miui.powerkeeper.feedbackcontrol.IFeedbackControl");
                    this.f7373a.transact(8, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public boolean O() {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.miui.powerkeeper.feedbackcontrol.IFeedbackControl");
                    boolean z = false;
                    this.f7373a.transact(6, obtain, obtain2, 0);
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

            public IBinder asBinder() {
                return this.f7373a;
            }

            public void b(boolean z) {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.miui.powerkeeper.feedbackcontrol.IFeedbackControl");
                    obtain.writeInt(z ? 1 : 0);
                    this.f7373a.transact(10, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void b(String[] strArr) {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.miui.powerkeeper.feedbackcontrol.IFeedbackControl");
                    obtain.writeStringArray(strArr);
                    this.f7373a.transact(11, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void c(int i, String str) {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.miui.powerkeeper.feedbackcontrol.IFeedbackControl");
                    obtain.writeInt(i);
                    obtain.writeString(str);
                    this.f7373a.transact(7, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public boolean p() {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.miui.powerkeeper.feedbackcontrol.IFeedbackControl");
                    boolean z = false;
                    this.f7373a.transact(9, obtain, obtain2, 0);
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

            public int x() {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.miui.powerkeeper.feedbackcontrol.IFeedbackControl");
                    this.f7373a.transact(12, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readInt();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }
        }

        public Stub() {
            attachInterface(this, "com.miui.powerkeeper.feedbackcontrol.IFeedbackControl");
        }

        public static IFeedbackControl a(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface queryLocalInterface = iBinder.queryLocalInterface("com.miui.powerkeeper.feedbackcontrol.IFeedbackControl");
            return (queryLocalInterface == null || !(queryLocalInterface instanceof IFeedbackControl)) ? new a(iBinder) : (IFeedbackControl) queryLocalInterface;
        }

        public IBinder asBinder() {
            return this;
        }

        public boolean onTransact(int i, Parcel parcel, Parcel parcel2, int i2) {
            if (i != 1598968902) {
                boolean z = false;
                switch (i) {
                    case 1:
                        parcel.enforceInterface("com.miui.powerkeeper.feedbackcontrol.IFeedbackControl");
                        h(parcel.readInt());
                        parcel2.writeNoException();
                        return true;
                    case 2:
                        parcel.enforceInterface("com.miui.powerkeeper.feedbackcontrol.IFeedbackControl");
                        int B = B();
                        parcel2.writeNoException();
                        parcel2.writeInt(B);
                        return true;
                    case 3:
                        parcel.enforceInterface("com.miui.powerkeeper.feedbackcontrol.IFeedbackControl");
                        b(IFeedbackControlListener.Stub.a(parcel.readStrongBinder()));
                        parcel2.writeNoException();
                        return true;
                    case 4:
                        parcel.enforceInterface("com.miui.powerkeeper.feedbackcontrol.IFeedbackControl");
                        a(IFeedbackControlListener.Stub.a(parcel.readStrongBinder()));
                        parcel2.writeNoException();
                        return true;
                    case 5:
                        parcel.enforceInterface("com.miui.powerkeeper.feedbackcontrol.IFeedbackControl");
                        Bundle v = v();
                        parcel2.writeNoException();
                        if (v != null) {
                            parcel2.writeInt(1);
                            v.writeToParcel(parcel2, 1);
                        } else {
                            parcel2.writeInt(0);
                        }
                        return true;
                    case 6:
                        parcel.enforceInterface("com.miui.powerkeeper.feedbackcontrol.IFeedbackControl");
                        boolean O = O();
                        parcel2.writeNoException();
                        if (O) {
                            z = true;
                        }
                        parcel2.writeInt(z ? 1 : 0);
                        return true;
                    case 7:
                        parcel.enforceInterface("com.miui.powerkeeper.feedbackcontrol.IFeedbackControl");
                        c(parcel.readInt(), parcel.readString());
                        parcel2.writeNoException();
                        return true;
                    case 8:
                        parcel.enforceInterface("com.miui.powerkeeper.feedbackcontrol.IFeedbackControl");
                        G();
                        parcel2.writeNoException();
                        return true;
                    case 9:
                        parcel.enforceInterface("com.miui.powerkeeper.feedbackcontrol.IFeedbackControl");
                        boolean p = p();
                        parcel2.writeNoException();
                        if (p) {
                            z = true;
                        }
                        parcel2.writeInt(z ? 1 : 0);
                        return true;
                    case 10:
                        parcel.enforceInterface("com.miui.powerkeeper.feedbackcontrol.IFeedbackControl");
                        if (parcel.readInt() != 0) {
                            z = true;
                        }
                        b(z);
                        parcel2.writeNoException();
                        return true;
                    case 11:
                        parcel.enforceInterface("com.miui.powerkeeper.feedbackcontrol.IFeedbackControl");
                        b(parcel.createStringArray());
                        parcel2.writeNoException();
                        return true;
                    case 12:
                        parcel.enforceInterface("com.miui.powerkeeper.feedbackcontrol.IFeedbackControl");
                        int x = x();
                        parcel2.writeNoException();
                        parcel2.writeInt(x);
                        return true;
                    default:
                        return super.onTransact(i, parcel, parcel2, i2);
                }
            } else {
                parcel2.writeString("com.miui.powerkeeper.feedbackcontrol.IFeedbackControl");
                return true;
            }
        }
    }

    int B();

    void G();

    boolean O();

    void a(IFeedbackControlListener iFeedbackControlListener);

    void b(IFeedbackControlListener iFeedbackControlListener);

    void b(boolean z);

    void b(String[] strArr);

    void c(int i, String str);

    void h(int i);

    boolean p();

    Bundle v();

    int x();
}
