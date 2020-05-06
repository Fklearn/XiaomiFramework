package miui.process;

import android.content.ComponentName;
import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;

public interface IActivityChangeListener extends IInterface {

    public static abstract class Stub extends Binder implements IActivityChangeListener {
        private static final String DESCRIPTOR = "miui.process.IActivityChangeListener";
        static final int TRANSACTION_onActivityChanged = 1;

        private static class Proxy implements IActivityChangeListener {
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

            public void onActivityChanged(ComponentName componentName, ComponentName componentName2) {
                Parcel obtain = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (componentName != null) {
                        obtain.writeInt(1);
                        componentName.writeToParcel(obtain, 0);
                    } else {
                        obtain.writeInt(0);
                    }
                    if (componentName2 != null) {
                        obtain.writeInt(1);
                        componentName2.writeToParcel(obtain, 0);
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

        public static IActivityChangeListener asInterface(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface queryLocalInterface = iBinder.queryLocalInterface(DESCRIPTOR);
            return (queryLocalInterface == null || !(queryLocalInterface instanceof IActivityChangeListener)) ? new Proxy(iBinder) : (IActivityChangeListener) queryLocalInterface;
        }

        public IBinder asBinder() {
            return this;
        }

        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v1, resolved type: java.lang.Object} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v4, resolved type: android.content.ComponentName} */
        /* JADX WARNING: Multi-variable type inference failed */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public boolean onTransact(int r4, android.os.Parcel r5, android.os.Parcel r6, int r7) {
            /*
                r3 = this;
                r0 = 1
                java.lang.String r1 = "miui.process.IActivityChangeListener"
                if (r4 == r0) goto L_0x0013
                r2 = 1598968902(0x5f4e5446, float:1.4867585E19)
                if (r4 == r2) goto L_0x000f
                boolean r4 = super.onTransact(r4, r5, r6, r7)
                return r4
            L_0x000f:
                r6.writeString(r1)
                return r0
            L_0x0013:
                r5.enforceInterface(r1)
                int r4 = r5.readInt()
                r6 = 0
                if (r4 == 0) goto L_0x0026
                android.os.Parcelable$Creator r4 = android.content.ComponentName.CREATOR
                java.lang.Object r4 = r4.createFromParcel(r5)
                android.content.ComponentName r4 = (android.content.ComponentName) r4
                goto L_0x0027
            L_0x0026:
                r4 = r6
            L_0x0027:
                int r7 = r5.readInt()
                if (r7 == 0) goto L_0x0036
                android.os.Parcelable$Creator r6 = android.content.ComponentName.CREATOR
                java.lang.Object r5 = r6.createFromParcel(r5)
                r6 = r5
                android.content.ComponentName r6 = (android.content.ComponentName) r6
            L_0x0036:
                r3.onActivityChanged(r4, r6)
                return r0
            */
            throw new UnsupportedOperationException("Method not decompiled: miui.process.IActivityChangeListener.Stub.onTransact(int, android.os.Parcel, android.os.Parcel, int):boolean");
        }
    }

    void onActivityChanged(ComponentName componentName, ComponentName componentName2);
}
