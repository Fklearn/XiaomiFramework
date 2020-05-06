package com.miui.systemAdSolution.changeSkin;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import com.miui.systemAdSolution.common.AdTrackType;
import com.miui.systemAdSolution.common.EnumPracle;

public interface IChangeSkinService extends IInterface {

    public static abstract class Stub extends Binder implements IChangeSkinService {
        private static final String DESCRIPTOR = "com.miui.systemAdSolution.changeSkin.IChangeSkinService";
        static final int TRANSACTION_doTrack = 2;
        static final int TRANSACTION_exec = 3;
        static final int TRANSACTION_getSkinInfoByTagId = 1;

        private static class Proxy implements IChangeSkinService {
            private IBinder mRemote;

            Proxy(IBinder iBinder) {
                this.mRemote = iBinder;
            }

            public IBinder asBinder() {
                return this.mRemote;
            }

            public boolean doTrack(String str, String str2, AdTrackType adTrackType, String str3, long j, long j2, long j3) {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeString(str);
                    obtain.writeString(str2);
                    boolean z = true;
                    if (adTrackType != null) {
                        obtain.writeInt(1);
                        adTrackType.writeToParcel(obtain, 0);
                    } else {
                        obtain.writeInt(0);
                    }
                    obtain.writeString(str3);
                    obtain.writeLong(j);
                    obtain.writeLong(j2);
                    obtain.writeLong(j3);
                    this.mRemote.transact(2, obtain, obtain2, 0);
                    obtain2.readException();
                    if (obtain2.readInt() == 0) {
                        z = false;
                    }
                    return z;
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public String exec(EnumPracle enumPracle, String str) {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (enumPracle != null) {
                        obtain.writeInt(1);
                        enumPracle.writeToParcel(obtain, 0);
                    } else {
                        obtain.writeInt(0);
                    }
                    obtain.writeString(str);
                    this.mRemote.transact(3, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readString();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public String getInterfaceDescriptor() {
                return Stub.DESCRIPTOR;
            }

            public String getSkinInfoByTagId(String str, String str2) {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeString(str);
                    obtain.writeString(str2);
                    this.mRemote.transact(1, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readString();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }
        }

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IChangeSkinService asInterface(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface queryLocalInterface = iBinder.queryLocalInterface(DESCRIPTOR);
            return (queryLocalInterface == null || !(queryLocalInterface instanceof IChangeSkinService)) ? new Proxy(iBinder) : (IChangeSkinService) queryLocalInterface;
        }

        public IBinder asBinder() {
            return this;
        }

        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v0, resolved type: com.miui.systemAdSolution.common.EnumPracle} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v1, resolved type: com.miui.systemAdSolution.common.EnumPracle} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v2, resolved type: com.miui.systemAdSolution.common.AdTrackType} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v3, resolved type: com.miui.systemAdSolution.common.EnumPracle} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v4, resolved type: com.miui.systemAdSolution.common.EnumPracle} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v6, resolved type: com.miui.systemAdSolution.common.EnumPracle} */
        /* JADX WARNING: type inference failed for: r0v12, types: [com.miui.systemAdSolution.common.AdTrackType] */
        /* JADX WARNING: Multi-variable type inference failed */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public boolean onTransact(int r17, android.os.Parcel r18, android.os.Parcel r19, int r20) {
            /*
                r16 = this;
                r11 = r16
                r0 = r17
                r1 = r18
                r12 = r19
                r13 = 1
                java.lang.String r2 = "com.miui.systemAdSolution.changeSkin.IChangeSkinService"
                if (r0 == r13) goto L_0x0086
                r3 = 2
                r4 = 0
                if (r0 == r3) goto L_0x0043
                r3 = 3
                if (r0 == r3) goto L_0x0022
                r3 = 1598968902(0x5f4e5446, float:1.4867585E19)
                if (r0 == r3) goto L_0x001e
                boolean r0 = super.onTransact(r17, r18, r19, r20)
                return r0
            L_0x001e:
                r12.writeString(r2)
                return r13
            L_0x0022:
                r1.enforceInterface(r2)
                int r0 = r18.readInt()
                if (r0 == 0) goto L_0x0034
                android.os.Parcelable$Creator<com.miui.systemAdSolution.common.EnumPracle> r0 = com.miui.systemAdSolution.common.EnumPracle.CREATOR
                java.lang.Object r0 = r0.createFromParcel(r1)
                r4 = r0
                com.miui.systemAdSolution.common.EnumPracle r4 = (com.miui.systemAdSolution.common.EnumPracle) r4
            L_0x0034:
                java.lang.String r0 = r18.readString()
                java.lang.String r0 = r11.exec(r4, r0)
            L_0x003c:
                r19.writeNoException()
                r12.writeString(r0)
                return r13
            L_0x0043:
                r1.enforceInterface(r2)
                java.lang.String r2 = r18.readString()
                java.lang.String r3 = r18.readString()
                int r0 = r18.readInt()
                if (r0 == 0) goto L_0x005d
                android.os.Parcelable$Creator<com.miui.systemAdSolution.common.AdTrackType> r0 = com.miui.systemAdSolution.common.AdTrackType.CREATOR
                java.lang.Object r0 = r0.createFromParcel(r1)
                com.miui.systemAdSolution.common.AdTrackType r0 = (com.miui.systemAdSolution.common.AdTrackType) r0
                r4 = r0
            L_0x005d:
                java.lang.String r5 = r18.readString()
                long r6 = r18.readLong()
                long r8 = r18.readLong()
                long r14 = r18.readLong()
                r0 = r16
                r1 = r2
                r2 = r3
                r3 = r4
                r4 = r5
                r5 = r6
                r7 = r8
                r9 = r14
                boolean r0 = r0.doTrack(r1, r2, r3, r4, r5, r7, r9)
                r19.writeNoException()
                if (r0 == 0) goto L_0x0081
                r0 = r13
                goto L_0x0082
            L_0x0081:
                r0 = 0
            L_0x0082:
                r12.writeInt(r0)
                return r13
            L_0x0086:
                r1.enforceInterface(r2)
                java.lang.String r0 = r18.readString()
                java.lang.String r1 = r18.readString()
                java.lang.String r0 = r11.getSkinInfoByTagId(r0, r1)
                goto L_0x003c
            */
            throw new UnsupportedOperationException("Method not decompiled: com.miui.systemAdSolution.changeSkin.IChangeSkinService.Stub.onTransact(int, android.os.Parcel, android.os.Parcel, int):boolean");
        }
    }

    boolean doTrack(String str, String str2, AdTrackType adTrackType, String str3, long j, long j2, long j3);

    String exec(EnumPracle enumPracle, String str);

    String getSkinInfoByTagId(String str, String str2);
}
