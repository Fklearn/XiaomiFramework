package com.miui.gamebooster.service;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.service.notification.StatusBarNotification;

public interface INotificationListenerCallback extends IInterface {

    public static abstract class Stub extends Binder implements INotificationListenerCallback {
        private static final String DESCRIPTOR = "com.miui.gamebooster.service.INotificationListenerCallback";
        static final int TRANSACTION_onNotificationPostedCallBack = 1;
        static final int TRANSACTION_onNotificationRemovedCallBack = 2;

        private static class a implements INotificationListenerCallback {

            /* renamed from: a  reason: collision with root package name */
            private IBinder f4785a;

            a(IBinder iBinder) {
                this.f4785a = iBinder;
            }

            public IBinder asBinder() {
                return this.f4785a;
            }

            public void onNotificationPostedCallBack(StatusBarNotification statusBarNotification) {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (statusBarNotification != null) {
                        obtain.writeInt(1);
                        statusBarNotification.writeToParcel(obtain, 0);
                    } else {
                        obtain.writeInt(0);
                    }
                    this.f4785a.transact(1, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void onNotificationRemovedCallBack(StatusBarNotification statusBarNotification) {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (statusBarNotification != null) {
                        obtain.writeInt(1);
                        statusBarNotification.writeToParcel(obtain, 0);
                    } else {
                        obtain.writeInt(0);
                    }
                    this.f4785a.transact(2, obtain, obtain2, 0);
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

        public static INotificationListenerCallback asInterface(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface queryLocalInterface = iBinder.queryLocalInterface(DESCRIPTOR);
            return (queryLocalInterface == null || !(queryLocalInterface instanceof INotificationListenerCallback)) ? new a(iBinder) : (INotificationListenerCallback) queryLocalInterface;
        }

        public IBinder asBinder() {
            return this;
        }

        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v3, resolved type: java.lang.Object} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v2, resolved type: android.service.notification.StatusBarNotification} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v6, resolved type: java.lang.Object} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v5, resolved type: android.service.notification.StatusBarNotification} */
        /* JADX WARNING: Multi-variable type inference failed */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public boolean onTransact(int r5, android.os.Parcel r6, android.os.Parcel r7, int r8) {
            /*
                r4 = this;
                r0 = 0
                r1 = 1
                java.lang.String r2 = "com.miui.gamebooster.service.INotificationListenerCallback"
                if (r5 == r1) goto L_0x0030
                r3 = 2
                if (r5 == r3) goto L_0x0017
                r0 = 1598968902(0x5f4e5446, float:1.4867585E19)
                if (r5 == r0) goto L_0x0013
                boolean r5 = super.onTransact(r5, r6, r7, r8)
                return r5
            L_0x0013:
                r7.writeString(r2)
                return r1
            L_0x0017:
                r6.enforceInterface(r2)
                int r5 = r6.readInt()
                if (r5 == 0) goto L_0x0029
                android.os.Parcelable$Creator r5 = android.service.notification.StatusBarNotification.CREATOR
                java.lang.Object r5 = r5.createFromParcel(r6)
                r0 = r5
                android.service.notification.StatusBarNotification r0 = (android.service.notification.StatusBarNotification) r0
            L_0x0029:
                r4.onNotificationRemovedCallBack(r0)
            L_0x002c:
                r7.writeNoException()
                return r1
            L_0x0030:
                r6.enforceInterface(r2)
                int r5 = r6.readInt()
                if (r5 == 0) goto L_0x0042
                android.os.Parcelable$Creator r5 = android.service.notification.StatusBarNotification.CREATOR
                java.lang.Object r5 = r5.createFromParcel(r6)
                r0 = r5
                android.service.notification.StatusBarNotification r0 = (android.service.notification.StatusBarNotification) r0
            L_0x0042:
                r4.onNotificationPostedCallBack(r0)
                goto L_0x002c
            */
            throw new UnsupportedOperationException("Method not decompiled: com.miui.gamebooster.service.INotificationListenerCallback.Stub.onTransact(int, android.os.Parcel, android.os.Parcel, int):boolean");
        }
    }

    void onNotificationPostedCallBack(StatusBarNotification statusBarNotification);

    void onNotificationRemovedCallBack(StatusBarNotification statusBarNotification);
}
