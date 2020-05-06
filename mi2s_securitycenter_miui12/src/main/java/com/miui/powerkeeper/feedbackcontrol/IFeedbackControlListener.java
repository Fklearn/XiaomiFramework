package com.miui.powerkeeper.feedbackcontrol;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;

public interface IFeedbackControlListener extends IInterface {

    public static abstract class Stub extends Binder implements IFeedbackControlListener {

        private static class a implements IFeedbackControlListener {

            /* renamed from: a  reason: collision with root package name */
            private IBinder f7374a;

            a(IBinder iBinder) {
                this.f7374a = iBinder;
            }

            public IBinder asBinder() {
                return this.f7374a;
            }
        }

        public Stub() {
            attachInterface(this, "com.miui.powerkeeper.feedbackcontrol.IFeedbackControlListener");
        }

        public static IFeedbackControlListener a(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface queryLocalInterface = iBinder.queryLocalInterface("com.miui.powerkeeper.feedbackcontrol.IFeedbackControlListener");
            return (queryLocalInterface == null || !(queryLocalInterface instanceof IFeedbackControlListener)) ? new a(iBinder) : (IFeedbackControlListener) queryLocalInterface;
        }

        public IBinder asBinder() {
            return this;
        }

        public boolean onTransact(int i, Parcel parcel, Parcel parcel2, int i2) {
            if (i != 1598968902) {
                switch (i) {
                    case 1:
                        parcel.enforceInterface("com.miui.powerkeeper.feedbackcontrol.IFeedbackControlListener");
                        s();
                        return true;
                    case 2:
                        parcel.enforceInterface("com.miui.powerkeeper.feedbackcontrol.IFeedbackControlListener");
                        H();
                        return true;
                    case 3:
                        parcel.enforceInterface("com.miui.powerkeeper.feedbackcontrol.IFeedbackControlListener");
                        r();
                        return true;
                    case 4:
                        parcel.enforceInterface("com.miui.powerkeeper.feedbackcontrol.IFeedbackControlListener");
                        m();
                        return true;
                    case 5:
                        parcel.enforceInterface("com.miui.powerkeeper.feedbackcontrol.IFeedbackControlListener");
                        N();
                        return true;
                    case 6:
                        parcel.enforceInterface("com.miui.powerkeeper.feedbackcontrol.IFeedbackControlListener");
                        k();
                        return true;
                    case 7:
                        parcel.enforceInterface("com.miui.powerkeeper.feedbackcontrol.IFeedbackControlListener");
                        I();
                        return true;
                    case 8:
                        parcel.enforceInterface("com.miui.powerkeeper.feedbackcontrol.IFeedbackControlListener");
                        y();
                        return true;
                    default:
                        return super.onTransact(i, parcel, parcel2, i2);
                }
            } else {
                parcel2.writeString("com.miui.powerkeeper.feedbackcontrol.IFeedbackControlListener");
                return true;
            }
        }
    }

    void H();

    void I();

    void N();

    void k();

    void m();

    void r();

    void s();

    void y();
}
