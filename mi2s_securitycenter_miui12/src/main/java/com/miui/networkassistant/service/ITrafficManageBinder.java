package com.miui.networkassistant.service;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import com.miui.networkassistant.model.TrafficUsedStatus;
import com.miui.networkassistant.service.IAppMonitorBinder;
import com.miui.networkassistant.service.ISharedPreBinder;
import com.miui.networkassistant.service.ITrafficCornBinder;

public interface ITrafficManageBinder extends IInterface {

    public static abstract class Stub extends Binder implements ITrafficManageBinder {
        private static final String DESCRIPTOR = "com.miui.networkassistant.service.ITrafficManageBinder";
        static final int TRANSACTION_applyCorrectedPkgsAndUsageValues = 29;
        static final int TRANSACTION_clearDataUsageIgnore = 25;
        static final int TRANSACTION_forceCheckDailyLimitStatus = 19;
        static final int TRANSACTION_forceCheckLockScreenStatus = 21;
        static final int TRANSACTION_forceCheckRoamingDailyLimitStatus = 20;
        static final int TRANSACTION_forceCheckTethingSettingStatus = 22;
        static final int TRANSACTION_forceCheckTrafficStatus = 18;
        static final int TRANSACTION_getActiveSlotNum = 4;
        static final int TRANSACTION_getAppMonitorBinder = 2;
        static final int TRANSACTION_getCorrectedNormalAndLeisureMonthTotalUsed = 16;
        static final int TRANSACTION_getCorrectedNormalMonthDataUsageUsed = 14;
        static final int TRANSACTION_getCurrentMonthTotalPackage = 15;
        static final int TRANSACTION_getIgnoreAppCount = 27;
        static final int TRANSACTION_getNormalTodayDataUsageUsed = 12;
        static final int TRANSACTION_getSharedPreBinder = 1;
        static final int TRANSACTION_getTodayDataUsageUsed = 13;
        static final int TRANSACTION_getTrafficCornBinder = 3;
        static final int TRANSACTION_isDataUsageIgnore = 23;
        static final int TRANSACTION_isNeededPurchasePkg = 28;
        static final int TRANSACTION_manualCorrectLeisureDataUsage = 8;
        static final int TRANSACTION_manualCorrectNormalDataUsage = 7;
        static final int TRANSACTION_reloadIgnoreAppList = 26;
        static final int TRANSACTION_setDataUsageIgnore = 24;
        static final int TRANSACTION_startCorrection = 5;
        static final int TRANSACTION_startCorrectionDiagnostic = 31;
        static final int TRANSACTION_toggleDataUsageAutoCorrection = 10;
        static final int TRANSACTION_toggleDataUsageOverLimitStopNetwork = 9;
        static final int TRANSACTION_toggleLeisureDataUsageOverLimitWarning = 11;
        static final int TRANSACTION_updateGlobleDataUsage = 6;
        static final int TRANSACTION_updateTrafficCorrectonEngine = 30;
        static final int TRANSACTION_updateTrafficStatusMonitor = 17;

        private static class Proxy implements ITrafficManageBinder {
            private IBinder mRemote;

            Proxy(IBinder iBinder) {
                this.mRemote = iBinder;
            }

            public void applyCorrectedPkgsAndUsageValues(TrafficUsedStatus trafficUsedStatus, boolean z, int i) {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    int i2 = 1;
                    if (trafficUsedStatus != null) {
                        obtain.writeInt(1);
                        trafficUsedStatus.writeToParcel(obtain, 0);
                    } else {
                        obtain.writeInt(0);
                    }
                    if (!z) {
                        i2 = 0;
                    }
                    obtain.writeInt(i2);
                    obtain.writeInt(i);
                    this.mRemote.transact(29, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public IBinder asBinder() {
                return this.mRemote;
            }

            public void clearDataUsageIgnore(int i) {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeInt(i);
                    this.mRemote.transact(25, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void forceCheckDailyLimitStatus(int i) {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeInt(i);
                    this.mRemote.transact(19, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void forceCheckLockScreenStatus(int i) {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeInt(i);
                    this.mRemote.transact(21, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void forceCheckRoamingDailyLimitStatus(int i) {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeInt(i);
                    this.mRemote.transact(20, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void forceCheckTethingSettingStatus() {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(22, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void forceCheckTrafficStatus(int i) {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeInt(i);
                    this.mRemote.transact(18, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public int getActiveSlotNum() {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(4, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readInt();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public IAppMonitorBinder getAppMonitorBinder() {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(2, obtain, obtain2, 0);
                    obtain2.readException();
                    return IAppMonitorBinder.Stub.asInterface(obtain2.readStrongBinder());
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public long[] getCorrectedNormalAndLeisureMonthTotalUsed(int i) {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeInt(i);
                    this.mRemote.transact(16, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.createLongArray();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public long getCorrectedNormalMonthDataUsageUsed(int i) {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeInt(i);
                    this.mRemote.transact(14, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readLong();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public long getCurrentMonthTotalPackage(int i) {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeInt(i);
                    this.mRemote.transact(15, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readLong();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public int getIgnoreAppCount(int i) {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeInt(i);
                    this.mRemote.transact(27, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readInt();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public String getInterfaceDescriptor() {
                return Stub.DESCRIPTOR;
            }

            public long getNormalTodayDataUsageUsed(int i) {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeInt(i);
                    this.mRemote.transact(12, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readLong();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public ISharedPreBinder getSharedPreBinder(String str, ISharedPreBinderListener iSharedPreBinderListener) {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeString(str);
                    obtain.writeStrongBinder(iSharedPreBinderListener != null ? iSharedPreBinderListener.asBinder() : null);
                    this.mRemote.transact(1, obtain, obtain2, 0);
                    obtain2.readException();
                    return ISharedPreBinder.Stub.asInterface(obtain2.readStrongBinder());
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public long getTodayDataUsageUsed(int i) {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeInt(i);
                    this.mRemote.transact(13, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readLong();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public ITrafficCornBinder getTrafficCornBinder(int i) {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeInt(i);
                    this.mRemote.transact(3, obtain, obtain2, 0);
                    obtain2.readException();
                    return ITrafficCornBinder.Stub.asInterface(obtain2.readStrongBinder());
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public boolean isDataUsageIgnore(String str, int i) {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeString(str);
                    obtain.writeInt(i);
                    boolean z = false;
                    this.mRemote.transact(23, obtain, obtain2, 0);
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

            public boolean isNeededPurchasePkg(int i) {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeInt(i);
                    boolean z = false;
                    this.mRemote.transact(28, obtain, obtain2, 0);
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

            public void manualCorrectLeisureDataUsage(long j, int i) {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeLong(j);
                    obtain.writeInt(i);
                    this.mRemote.transact(8, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void manualCorrectNormalDataUsage(long j, int i) {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeLong(j);
                    obtain.writeInt(i);
                    this.mRemote.transact(7, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void reloadIgnoreAppList(int i) {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeInt(i);
                    this.mRemote.transact(26, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void setDataUsageIgnore(String str, boolean z, int i) {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeString(str);
                    obtain.writeInt(z ? 1 : 0);
                    obtain.writeInt(i);
                    this.mRemote.transact(24, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public boolean startCorrection(boolean z, int i, boolean z2, int i2) {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    boolean z3 = true;
                    obtain.writeInt(z ? 1 : 0);
                    obtain.writeInt(i);
                    obtain.writeInt(z2 ? 1 : 0);
                    obtain.writeInt(i2);
                    this.mRemote.transact(5, obtain, obtain2, 0);
                    obtain2.readException();
                    if (obtain2.readInt() == 0) {
                        z3 = false;
                    }
                    return z3;
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public boolean startCorrectionDiagnostic(int i) {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeInt(i);
                    boolean z = false;
                    this.mRemote.transact(31, obtain, obtain2, 0);
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

            public void toggleDataUsageAutoCorrection(boolean z, int i) {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeInt(z ? 1 : 0);
                    obtain.writeInt(i);
                    this.mRemote.transact(10, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void toggleDataUsageOverLimitStopNetwork(boolean z, int i) {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeInt(z ? 1 : 0);
                    obtain.writeInt(i);
                    this.mRemote.transact(9, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void toggleLeisureDataUsageOverLimitWarning(boolean z, int i) {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeInt(z ? 1 : 0);
                    obtain.writeInt(i);
                    this.mRemote.transact(11, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void updateGlobleDataUsage(int i) {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeInt(i);
                    this.mRemote.transact(6, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public boolean updateTrafficCorrectonEngine(int i, String str, String str2, String str3) {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeInt(i);
                    obtain.writeString(str);
                    obtain.writeString(str2);
                    obtain.writeString(str3);
                    boolean z = false;
                    this.mRemote.transact(30, obtain, obtain2, 0);
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

            public void updateTrafficStatusMonitor(int i) {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeInt(i);
                    this.mRemote.transact(17, obtain, obtain2, 0);
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

        public static ITrafficManageBinder asInterface(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface queryLocalInterface = iBinder.queryLocalInterface(DESCRIPTOR);
            return (queryLocalInterface == null || !(queryLocalInterface instanceof ITrafficManageBinder)) ? new Proxy(iBinder) : (ITrafficManageBinder) queryLocalInterface;
        }

        public IBinder asBinder() {
            return this;
        }

        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v12, resolved type: com.miui.networkassistant.model.TrafficUsedStatus} */
        /* JADX WARNING: type inference failed for: r0v1 */
        /* JADX WARNING: type inference failed for: r0v2, types: [android.os.IBinder] */
        /* JADX WARNING: type inference failed for: r0v4, types: [android.os.IBinder] */
        /* JADX WARNING: type inference failed for: r0v6, types: [android.os.IBinder] */
        /* JADX WARNING: type inference failed for: r0v16 */
        /* JADX WARNING: type inference failed for: r0v17 */
        /* JADX WARNING: type inference failed for: r0v18 */
        /* JADX WARNING: type inference failed for: r0v19 */
        /* JADX WARNING: Multi-variable type inference failed */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public boolean onTransact(int r5, android.os.Parcel r6, android.os.Parcel r7, int r8) {
            /*
                r4 = this;
                r0 = 1598968902(0x5f4e5446, float:1.4867585E19)
                r1 = 1
                java.lang.String r2 = "com.miui.networkassistant.service.ITrafficManageBinder"
                if (r5 == r0) goto L_0x0281
                r0 = 0
                r3 = 0
                switch(r5) {
                    case 1: goto L_0x0261;
                    case 2: goto L_0x024d;
                    case 3: goto L_0x0235;
                    case 4: goto L_0x0227;
                    case 5: goto L_0x01fc;
                    case 6: goto L_0x01ee;
                    case 7: goto L_0x01dc;
                    case 8: goto L_0x01ca;
                    case 9: goto L_0x01b5;
                    case 10: goto L_0x01a0;
                    case 11: goto L_0x018b;
                    case 12: goto L_0x0179;
                    case 13: goto L_0x0167;
                    case 14: goto L_0x0155;
                    case 15: goto L_0x0143;
                    case 16: goto L_0x0131;
                    case 17: goto L_0x0123;
                    case 18: goto L_0x0115;
                    case 19: goto L_0x0107;
                    case 20: goto L_0x00f9;
                    case 21: goto L_0x00eb;
                    case 22: goto L_0x00e1;
                    case 23: goto L_0x00c8;
                    case 24: goto L_0x00af;
                    case 25: goto L_0x00a1;
                    case 26: goto L_0x0093;
                    case 27: goto L_0x0081;
                    case 28: goto L_0x006c;
                    case 29: goto L_0x0048;
                    case 30: goto L_0x0027;
                    case 31: goto L_0x0012;
                    default: goto L_0x000d;
                }
            L_0x000d:
                boolean r5 = super.onTransact(r5, r6, r7, r8)
                return r5
            L_0x0012:
                r6.enforceInterface(r2)
                int r5 = r6.readInt()
                boolean r5 = r4.startCorrectionDiagnostic(r5)
                r7.writeNoException()
                if (r5 == 0) goto L_0x0023
                r3 = r1
            L_0x0023:
                r7.writeInt(r3)
                return r1
            L_0x0027:
                r6.enforceInterface(r2)
                int r5 = r6.readInt()
                java.lang.String r8 = r6.readString()
                java.lang.String r0 = r6.readString()
                java.lang.String r6 = r6.readString()
                boolean r5 = r4.updateTrafficCorrectonEngine(r5, r8, r0, r6)
                r7.writeNoException()
                if (r5 == 0) goto L_0x0044
                r3 = r1
            L_0x0044:
                r7.writeInt(r3)
                return r1
            L_0x0048:
                r6.enforceInterface(r2)
                int r5 = r6.readInt()
                if (r5 == 0) goto L_0x005a
                android.os.Parcelable$Creator<com.miui.networkassistant.model.TrafficUsedStatus> r5 = com.miui.networkassistant.model.TrafficUsedStatus.CREATOR
                java.lang.Object r5 = r5.createFromParcel(r6)
                r0 = r5
                com.miui.networkassistant.model.TrafficUsedStatus r0 = (com.miui.networkassistant.model.TrafficUsedStatus) r0
            L_0x005a:
                int r5 = r6.readInt()
                if (r5 == 0) goto L_0x0061
                r3 = r1
            L_0x0061:
                int r5 = r6.readInt()
                r4.applyCorrectedPkgsAndUsageValues(r0, r3, r5)
                r7.writeNoException()
                return r1
            L_0x006c:
                r6.enforceInterface(r2)
                int r5 = r6.readInt()
                boolean r5 = r4.isNeededPurchasePkg(r5)
                r7.writeNoException()
                if (r5 == 0) goto L_0x007d
                r3 = r1
            L_0x007d:
                r7.writeInt(r3)
                return r1
            L_0x0081:
                r6.enforceInterface(r2)
                int r5 = r6.readInt()
                int r5 = r4.getIgnoreAppCount(r5)
                r7.writeNoException()
                r7.writeInt(r5)
                return r1
            L_0x0093:
                r6.enforceInterface(r2)
                int r5 = r6.readInt()
                r4.reloadIgnoreAppList(r5)
                r7.writeNoException()
                return r1
            L_0x00a1:
                r6.enforceInterface(r2)
                int r5 = r6.readInt()
                r4.clearDataUsageIgnore(r5)
                r7.writeNoException()
                return r1
            L_0x00af:
                r6.enforceInterface(r2)
                java.lang.String r5 = r6.readString()
                int r8 = r6.readInt()
                if (r8 == 0) goto L_0x00bd
                r3 = r1
            L_0x00bd:
                int r6 = r6.readInt()
                r4.setDataUsageIgnore(r5, r3, r6)
                r7.writeNoException()
                return r1
            L_0x00c8:
                r6.enforceInterface(r2)
                java.lang.String r5 = r6.readString()
                int r6 = r6.readInt()
                boolean r5 = r4.isDataUsageIgnore(r5, r6)
                r7.writeNoException()
                if (r5 == 0) goto L_0x00dd
                r3 = r1
            L_0x00dd:
                r7.writeInt(r3)
                return r1
            L_0x00e1:
                r6.enforceInterface(r2)
                r4.forceCheckTethingSettingStatus()
                r7.writeNoException()
                return r1
            L_0x00eb:
                r6.enforceInterface(r2)
                int r5 = r6.readInt()
                r4.forceCheckLockScreenStatus(r5)
                r7.writeNoException()
                return r1
            L_0x00f9:
                r6.enforceInterface(r2)
                int r5 = r6.readInt()
                r4.forceCheckRoamingDailyLimitStatus(r5)
                r7.writeNoException()
                return r1
            L_0x0107:
                r6.enforceInterface(r2)
                int r5 = r6.readInt()
                r4.forceCheckDailyLimitStatus(r5)
                r7.writeNoException()
                return r1
            L_0x0115:
                r6.enforceInterface(r2)
                int r5 = r6.readInt()
                r4.forceCheckTrafficStatus(r5)
                r7.writeNoException()
                return r1
            L_0x0123:
                r6.enforceInterface(r2)
                int r5 = r6.readInt()
                r4.updateTrafficStatusMonitor(r5)
                r7.writeNoException()
                return r1
            L_0x0131:
                r6.enforceInterface(r2)
                int r5 = r6.readInt()
                long[] r5 = r4.getCorrectedNormalAndLeisureMonthTotalUsed(r5)
                r7.writeNoException()
                r7.writeLongArray(r5)
                return r1
            L_0x0143:
                r6.enforceInterface(r2)
                int r5 = r6.readInt()
                long r5 = r4.getCurrentMonthTotalPackage(r5)
                r7.writeNoException()
                r7.writeLong(r5)
                return r1
            L_0x0155:
                r6.enforceInterface(r2)
                int r5 = r6.readInt()
                long r5 = r4.getCorrectedNormalMonthDataUsageUsed(r5)
                r7.writeNoException()
                r7.writeLong(r5)
                return r1
            L_0x0167:
                r6.enforceInterface(r2)
                int r5 = r6.readInt()
                long r5 = r4.getTodayDataUsageUsed(r5)
                r7.writeNoException()
                r7.writeLong(r5)
                return r1
            L_0x0179:
                r6.enforceInterface(r2)
                int r5 = r6.readInt()
                long r5 = r4.getNormalTodayDataUsageUsed(r5)
                r7.writeNoException()
                r7.writeLong(r5)
                return r1
            L_0x018b:
                r6.enforceInterface(r2)
                int r5 = r6.readInt()
                if (r5 == 0) goto L_0x0195
                r3 = r1
            L_0x0195:
                int r5 = r6.readInt()
                r4.toggleLeisureDataUsageOverLimitWarning(r3, r5)
                r7.writeNoException()
                return r1
            L_0x01a0:
                r6.enforceInterface(r2)
                int r5 = r6.readInt()
                if (r5 == 0) goto L_0x01aa
                r3 = r1
            L_0x01aa:
                int r5 = r6.readInt()
                r4.toggleDataUsageAutoCorrection(r3, r5)
                r7.writeNoException()
                return r1
            L_0x01b5:
                r6.enforceInterface(r2)
                int r5 = r6.readInt()
                if (r5 == 0) goto L_0x01bf
                r3 = r1
            L_0x01bf:
                int r5 = r6.readInt()
                r4.toggleDataUsageOverLimitStopNetwork(r3, r5)
                r7.writeNoException()
                return r1
            L_0x01ca:
                r6.enforceInterface(r2)
                long r2 = r6.readLong()
                int r5 = r6.readInt()
                r4.manualCorrectLeisureDataUsage(r2, r5)
                r7.writeNoException()
                return r1
            L_0x01dc:
                r6.enforceInterface(r2)
                long r2 = r6.readLong()
                int r5 = r6.readInt()
                r4.manualCorrectNormalDataUsage(r2, r5)
                r7.writeNoException()
                return r1
            L_0x01ee:
                r6.enforceInterface(r2)
                int r5 = r6.readInt()
                r4.updateGlobleDataUsage(r5)
                r7.writeNoException()
                return r1
            L_0x01fc:
                r6.enforceInterface(r2)
                int r5 = r6.readInt()
                if (r5 == 0) goto L_0x0207
                r5 = r1
                goto L_0x0208
            L_0x0207:
                r5 = r3
            L_0x0208:
                int r8 = r6.readInt()
                int r0 = r6.readInt()
                if (r0 == 0) goto L_0x0214
                r0 = r1
                goto L_0x0215
            L_0x0214:
                r0 = r3
            L_0x0215:
                int r6 = r6.readInt()
                boolean r5 = r4.startCorrection(r5, r8, r0, r6)
                r7.writeNoException()
                if (r5 == 0) goto L_0x0223
                r3 = r1
            L_0x0223:
                r7.writeInt(r3)
                return r1
            L_0x0227:
                r6.enforceInterface(r2)
                int r5 = r4.getActiveSlotNum()
                r7.writeNoException()
                r7.writeInt(r5)
                return r1
            L_0x0235:
                r6.enforceInterface(r2)
                int r5 = r6.readInt()
                com.miui.networkassistant.service.ITrafficCornBinder r5 = r4.getTrafficCornBinder(r5)
                r7.writeNoException()
                if (r5 == 0) goto L_0x0249
                android.os.IBinder r0 = r5.asBinder()
            L_0x0249:
                r7.writeStrongBinder(r0)
                return r1
            L_0x024d:
                r6.enforceInterface(r2)
                com.miui.networkassistant.service.IAppMonitorBinder r5 = r4.getAppMonitorBinder()
                r7.writeNoException()
                if (r5 == 0) goto L_0x025d
                android.os.IBinder r0 = r5.asBinder()
            L_0x025d:
                r7.writeStrongBinder(r0)
                return r1
            L_0x0261:
                r6.enforceInterface(r2)
                java.lang.String r5 = r6.readString()
                android.os.IBinder r6 = r6.readStrongBinder()
                com.miui.networkassistant.service.ISharedPreBinderListener r6 = com.miui.networkassistant.service.ISharedPreBinderListener.Stub.asInterface(r6)
                com.miui.networkassistant.service.ISharedPreBinder r5 = r4.getSharedPreBinder(r5, r6)
                r7.writeNoException()
                if (r5 == 0) goto L_0x027d
                android.os.IBinder r0 = r5.asBinder()
            L_0x027d:
                r7.writeStrongBinder(r0)
                return r1
            L_0x0281:
                r7.writeString(r2)
                return r1
            */
            throw new UnsupportedOperationException("Method not decompiled: com.miui.networkassistant.service.ITrafficManageBinder.Stub.onTransact(int, android.os.Parcel, android.os.Parcel, int):boolean");
        }
    }

    void applyCorrectedPkgsAndUsageValues(TrafficUsedStatus trafficUsedStatus, boolean z, int i);

    void clearDataUsageIgnore(int i);

    void forceCheckDailyLimitStatus(int i);

    void forceCheckLockScreenStatus(int i);

    void forceCheckRoamingDailyLimitStatus(int i);

    void forceCheckTethingSettingStatus();

    void forceCheckTrafficStatus(int i);

    int getActiveSlotNum();

    IAppMonitorBinder getAppMonitorBinder();

    long[] getCorrectedNormalAndLeisureMonthTotalUsed(int i);

    long getCorrectedNormalMonthDataUsageUsed(int i);

    long getCurrentMonthTotalPackage(int i);

    int getIgnoreAppCount(int i);

    long getNormalTodayDataUsageUsed(int i);

    ISharedPreBinder getSharedPreBinder(String str, ISharedPreBinderListener iSharedPreBinderListener);

    long getTodayDataUsageUsed(int i);

    ITrafficCornBinder getTrafficCornBinder(int i);

    boolean isDataUsageIgnore(String str, int i);

    boolean isNeededPurchasePkg(int i);

    void manualCorrectLeisureDataUsage(long j, int i);

    void manualCorrectNormalDataUsage(long j, int i);

    void reloadIgnoreAppList(int i);

    void setDataUsageIgnore(String str, boolean z, int i);

    boolean startCorrection(boolean z, int i, boolean z2, int i2);

    boolean startCorrectionDiagnostic(int i);

    void toggleDataUsageAutoCorrection(boolean z, int i);

    void toggleDataUsageOverLimitStopNetwork(boolean z, int i);

    void toggleLeisureDataUsageOverLimitWarning(boolean z, int i);

    void updateGlobleDataUsage(int i);

    boolean updateTrafficCorrectonEngine(int i, String str, String str2, String str3);

    void updateTrafficStatusMonitor(int i);
}
