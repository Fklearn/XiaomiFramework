package com.miui.systemAdSolution.landingPage;

import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;

public interface ILandingPageService extends IInterface {

    public static abstract class Stub extends Binder implements ILandingPageService {
        private static final String DESCRIPTOR = "com.miui.systemAdSolution.landingPage.ILandingPageService";
        static final int TRANSACTION_cancleDownload = 5;
        static final int TRANSACTION_deeplinkStartApp = 1;
        static final int TRANSACTION_getDownloadId = 8;
        static final int TRANSACTION_getPackageInstallationStatus = 2;
        static final int TRANSACTION_getServiceVersion = 7;
        static final int TRANSACTION_registerListener = 6;
        static final int TRANSACTION_showAppDetailCard = 3;
        static final int TRANSACTION_startDownload = 4;
        static final int TRANSACTION_unregisterListener = 9;

        private static class Proxy implements ILandingPageService {
            private IBinder mRemote;

            Proxy(IBinder iBinder) {
                this.mRemote = iBinder;
            }

            public IBinder asBinder() {
                return this.mRemote;
            }

            public boolean cancleDownload(String str) {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeString(str);
                    boolean z = false;
                    this.mRemote.transact(5, obtain, obtain2, 0);
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

            public void deeplinkStartApp(String str, Bundle bundle) {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeString(str);
                    if (bundle != null) {
                        obtain.writeInt(1);
                        bundle.writeToParcel(obtain, 0);
                    } else {
                        obtain.writeInt(0);
                    }
                    this.mRemote.transact(1, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public long getDownloadId(String str) {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeString(str);
                    this.mRemote.transact(8, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readLong();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public String getInterfaceDescriptor() {
                return Stub.DESCRIPTOR;
            }

            public int getPackageInstallationStatus(String str) {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeString(str);
                    this.mRemote.transact(2, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readInt();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public int getServiceVersion() {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(7, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readInt();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void registerListener(String str, ILandingPageListener iLandingPageListener) {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeString(str);
                    obtain.writeStrongBinder(iLandingPageListener != null ? iLandingPageListener.asBinder() : null);
                    this.mRemote.transact(6, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void showAppDetailCard(String str, Bundle bundle) {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeString(str);
                    if (bundle != null) {
                        obtain.writeInt(1);
                        bundle.writeToParcel(obtain, 0);
                    } else {
                        obtain.writeInt(0);
                    }
                    this.mRemote.transact(3, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void startDownload(String str, Bundle bundle) {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeString(str);
                    if (bundle != null) {
                        obtain.writeInt(1);
                        bundle.writeToParcel(obtain, 0);
                    } else {
                        obtain.writeInt(0);
                    }
                    this.mRemote.transact(4, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void unregisterListener(String str, ILandingPageListener iLandingPageListener) {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeString(str);
                    obtain.writeStrongBinder(iLandingPageListener != null ? iLandingPageListener.asBinder() : null);
                    this.mRemote.transact(9, obtain, obtain2, 0);
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

        public static ILandingPageService asInterface(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface queryLocalInterface = iBinder.queryLocalInterface(DESCRIPTOR);
            return (queryLocalInterface == null || !(queryLocalInterface instanceof ILandingPageService)) ? new Proxy(iBinder) : (ILandingPageService) queryLocalInterface;
        }

        public IBinder asBinder() {
            return this;
        }

        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v1, resolved type: java.lang.Object} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v3, resolved type: android.os.Bundle} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v2, resolved type: java.lang.Object} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v6, resolved type: android.os.Bundle} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v3, resolved type: java.lang.Object} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v9, resolved type: android.os.Bundle} */
        /* JADX WARNING: Multi-variable type inference failed */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public boolean onTransact(int r4, android.os.Parcel r5, android.os.Parcel r6, int r7) {
            /*
                r3 = this;
                r0 = 1598968902(0x5f4e5446, float:1.4867585E19)
                r1 = 1
                java.lang.String r2 = "com.miui.systemAdSolution.landingPage.ILandingPageService"
                if (r4 == r0) goto L_0x00dd
                r0 = 0
                switch(r4) {
                    case 1: goto L_0x00c0;
                    case 2: goto L_0x00ae;
                    case 3: goto L_0x0091;
                    case 4: goto L_0x0074;
                    case 5: goto L_0x005d;
                    case 6: goto L_0x0047;
                    case 7: goto L_0x0039;
                    case 8: goto L_0x0027;
                    case 9: goto L_0x0011;
                    default: goto L_0x000c;
                }
            L_0x000c:
                boolean r4 = super.onTransact(r4, r5, r6, r7)
                return r4
            L_0x0011:
                r5.enforceInterface(r2)
                java.lang.String r4 = r5.readString()
                android.os.IBinder r5 = r5.readStrongBinder()
                com.miui.systemAdSolution.landingPage.ILandingPageListener r5 = com.miui.systemAdSolution.landingPage.ILandingPageListener.Stub.asInterface(r5)
                r3.unregisterListener(r4, r5)
                r6.writeNoException()
                return r1
            L_0x0027:
                r5.enforceInterface(r2)
                java.lang.String r4 = r5.readString()
                long r4 = r3.getDownloadId(r4)
                r6.writeNoException()
                r6.writeLong(r4)
                return r1
            L_0x0039:
                r5.enforceInterface(r2)
                int r4 = r3.getServiceVersion()
                r6.writeNoException()
                r6.writeInt(r4)
                return r1
            L_0x0047:
                r5.enforceInterface(r2)
                java.lang.String r4 = r5.readString()
                android.os.IBinder r5 = r5.readStrongBinder()
                com.miui.systemAdSolution.landingPage.ILandingPageListener r5 = com.miui.systemAdSolution.landingPage.ILandingPageListener.Stub.asInterface(r5)
                r3.registerListener(r4, r5)
                r6.writeNoException()
                return r1
            L_0x005d:
                r5.enforceInterface(r2)
                java.lang.String r4 = r5.readString()
                boolean r4 = r3.cancleDownload(r4)
                r6.writeNoException()
                if (r4 == 0) goto L_0x006f
                r4 = r1
                goto L_0x0070
            L_0x006f:
                r4 = 0
            L_0x0070:
                r6.writeInt(r4)
                return r1
            L_0x0074:
                r5.enforceInterface(r2)
                java.lang.String r4 = r5.readString()
                int r7 = r5.readInt()
                if (r7 == 0) goto L_0x008a
                android.os.Parcelable$Creator r7 = android.os.Bundle.CREATOR
                java.lang.Object r5 = r7.createFromParcel(r5)
                r0 = r5
                android.os.Bundle r0 = (android.os.Bundle) r0
            L_0x008a:
                r3.startDownload(r4, r0)
                r6.writeNoException()
                return r1
            L_0x0091:
                r5.enforceInterface(r2)
                java.lang.String r4 = r5.readString()
                int r7 = r5.readInt()
                if (r7 == 0) goto L_0x00a7
                android.os.Parcelable$Creator r7 = android.os.Bundle.CREATOR
                java.lang.Object r5 = r7.createFromParcel(r5)
                r0 = r5
                android.os.Bundle r0 = (android.os.Bundle) r0
            L_0x00a7:
                r3.showAppDetailCard(r4, r0)
                r6.writeNoException()
                return r1
            L_0x00ae:
                r5.enforceInterface(r2)
                java.lang.String r4 = r5.readString()
                int r4 = r3.getPackageInstallationStatus(r4)
                r6.writeNoException()
                r6.writeInt(r4)
                return r1
            L_0x00c0:
                r5.enforceInterface(r2)
                java.lang.String r4 = r5.readString()
                int r7 = r5.readInt()
                if (r7 == 0) goto L_0x00d6
                android.os.Parcelable$Creator r7 = android.os.Bundle.CREATOR
                java.lang.Object r5 = r7.createFromParcel(r5)
                r0 = r5
                android.os.Bundle r0 = (android.os.Bundle) r0
            L_0x00d6:
                r3.deeplinkStartApp(r4, r0)
                r6.writeNoException()
                return r1
            L_0x00dd:
                r6.writeString(r2)
                return r1
            */
            throw new UnsupportedOperationException("Method not decompiled: com.miui.systemAdSolution.landingPage.ILandingPageService.Stub.onTransact(int, android.os.Parcel, android.os.Parcel, int):boolean");
        }
    }

    boolean cancleDownload(String str);

    void deeplinkStartApp(String str, Bundle bundle);

    long getDownloadId(String str);

    int getPackageInstallationStatus(String str);

    int getServiceVersion();

    void registerListener(String str, ILandingPageListener iLandingPageListener);

    void showAppDetailCard(String str, Bundle bundle);

    void startDownload(String str, Bundle bundle);

    void unregisterListener(String str, ILandingPageListener iLandingPageListener);
}
