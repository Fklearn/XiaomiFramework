package com.miui.daemon.performance.server;

import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.ParcelFileDescriptor;
import android.os.RemoteException;
import android.os.statistics.E2EScenario;
import android.os.statistics.E2EScenarioPayload;
import android.os.statistics.E2EScenarioSettings;
import java.util.List;

public interface IMiuiPerfService extends IInterface {
    void abortMatchingScenario(E2EScenario e2EScenario, String str, long j, int i, int i2, String str2, String str3) throws RemoteException;

    void abortSpecificScenario(Bundle bundle, long j, int i, int i2, String str, String str2) throws RemoteException;

    Bundle beginScenario(E2EScenario e2EScenario, E2EScenarioSettings e2EScenarioSettings, String str, E2EScenarioPayload e2EScenarioPayload, long j, int i, int i2, String str2, String str3, boolean z) throws RemoteException;

    void dump(String[] strArr) throws RemoteException;

    void finishMatchingScenario(E2EScenario e2EScenario, String str, E2EScenarioPayload e2EScenarioPayload, long j, int i, int i2, String str2, String str3) throws RemoteException;

    void finishSpecificScenario(Bundle bundle, E2EScenarioPayload e2EScenarioPayload, long j, int i, int i2, String str, String str2) throws RemoteException;

    ParcelFileDescriptor getPerfEventSocketFd() throws RemoteException;

    void markPerceptibleJank(Bundle bundle) throws RemoteException;

    void reportActivityLaunchRecords(List<Bundle> list) throws RemoteException;

    void reportExcessiveCpuUsageRecords(List<Bundle> list) throws RemoteException;

    void reportNotificationClick(String str, Intent intent, long j) throws RemoteException;

    void reportProcessCleanEvent(Bundle bundle) throws RemoteException;

    void setSchedFgPid(int i) throws RemoteException;

    public static abstract class Stub extends Binder implements IMiuiPerfService {
        private static final String DESCRIPTOR = "com.miui.daemon.performance.server.IMiuiPerfService";
        static final int TRANSACTION_abortMatchingScenario = 7;
        static final int TRANSACTION_abortSpecificScenario = 8;
        static final int TRANSACTION_beginScenario = 6;
        static final int TRANSACTION_dump = 3;
        static final int TRANSACTION_finishMatchingScenario = 9;
        static final int TRANSACTION_finishSpecificScenario = 10;
        static final int TRANSACTION_getPerfEventSocketFd = 5;
        static final int TRANSACTION_markPerceptibleJank = 1;
        static final int TRANSACTION_reportActivityLaunchRecords = 2;
        static final int TRANSACTION_reportExcessiveCpuUsageRecords = 11;
        static final int TRANSACTION_reportNotificationClick = 12;
        static final int TRANSACTION_reportProcessCleanEvent = 13;
        static final int TRANSACTION_setSchedFgPid = 4;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IMiuiPerfService asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin == null || !(iin instanceof IMiuiPerfService)) {
                return new Proxy(obj);
            }
            return (IMiuiPerfService) iin;
        }

        public IBinder asBinder() {
            return this;
        }

        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v5, resolved type: java.lang.Object} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v2, resolved type: android.os.Bundle} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v26, resolved type: java.lang.Object} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v11, resolved type: android.os.Bundle} */
        /* JADX WARNING: Multi-variable type inference failed */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public boolean onTransact(int r27, android.os.Parcel r28, android.os.Parcel r29, int r30) throws android.os.RemoteException {
            /*
                r26 = this;
                r12 = r26
                r13 = r27
                r14 = r28
                r15 = r29
                r0 = 1598968902(0x5f4e5446, float:1.4867585E19)
                java.lang.String r1 = "com.miui.daemon.performance.server.IMiuiPerfService"
                r10 = 1
                if (r13 == r0) goto L_0x0205
                r0 = 0
                r2 = 0
                switch(r13) {
                    case 1: goto L_0x01e8;
                    case 2: goto L_0x01d7;
                    case 3: goto L_0x01c8;
                    case 4: goto L_0x01b9;
                    case 5: goto L_0x01a1;
                    case 6: goto L_0x0145;
                    case 7: goto L_0x010e;
                    case 8: goto L_0x00e1;
                    case 9: goto L_0x009f;
                    case 10: goto L_0x0068;
                    case 11: goto L_0x0058;
                    case 12: goto L_0x0036;
                    case 13: goto L_0x001a;
                    default: goto L_0x0015;
                }
            L_0x0015:
                boolean r0 = super.onTransact(r27, r28, r29, r30)
                return r0
            L_0x001a:
                r14.enforceInterface(r1)
                int r0 = r28.readInt()
                if (r0 == 0) goto L_0x002d
                android.os.Parcelable$Creator r0 = android.os.Bundle.CREATOR
                java.lang.Object r0 = r0.createFromParcel(r14)
                r2 = r0
                android.os.Bundle r2 = (android.os.Bundle) r2
                goto L_0x002e
            L_0x002d:
            L_0x002e:
                r0 = r2
                r12.reportProcessCleanEvent(r0)
                r29.writeNoException()
                return r10
            L_0x0036:
                r14.enforceInterface(r1)
                java.lang.String r0 = r28.readString()
                int r1 = r28.readInt()
                if (r1 == 0) goto L_0x004c
                android.os.Parcelable$Creator r1 = android.content.Intent.CREATOR
                java.lang.Object r1 = r1.createFromParcel(r14)
                android.content.Intent r1 = (android.content.Intent) r1
                goto L_0x004d
            L_0x004c:
                r1 = 0
            L_0x004d:
                long r2 = r28.readLong()
                r12.reportNotificationClick(r0, r1, r2)
                r29.writeNoException()
                return r10
            L_0x0058:
                r14.enforceInterface(r1)
                android.os.Parcelable$Creator r0 = android.os.Bundle.CREATOR
                java.util.ArrayList r0 = r14.createTypedArrayList(r0)
                r12.reportExcessiveCpuUsageRecords(r0)
                r29.writeNoException()
                return r10
            L_0x0068:
                r14.enforceInterface(r1)
                android.os.Bundle r9 = r28.readBundle()
                android.os.Parcelable r0 = r14.readParcelable(r2)
                r11 = r0
                android.os.statistics.E2EScenarioPayload r11 = (android.os.statistics.E2EScenarioPayload) r11
                long r16 = r28.readLong()
                int r18 = r28.readInt()
                int r19 = r28.readInt()
                java.lang.String r20 = r28.readString()
                java.lang.String r21 = r28.readString()
                r0 = r26
                r1 = r9
                r2 = r11
                r3 = r16
                r5 = r18
                r6 = r19
                r7 = r20
                r8 = r21
                r0.finishSpecificScenario(r1, r2, r3, r5, r6, r7, r8)
                r29.writeNoException()
                return r10
            L_0x009f:
                r14.enforceInterface(r1)
                android.os.Parcelable r0 = r14.readParcelable(r2)
                r11 = r0
                android.os.statistics.E2EScenario r11 = (android.os.statistics.E2EScenario) r11
                java.lang.String r16 = r28.readString()
                android.os.Parcelable r0 = r14.readParcelable(r2)
                r17 = r0
                android.os.statistics.E2EScenarioPayload r17 = (android.os.statistics.E2EScenarioPayload) r17
                long r18 = r28.readLong()
                int r20 = r28.readInt()
                int r21 = r28.readInt()
                java.lang.String r22 = r28.readString()
                java.lang.String r23 = r28.readString()
                r0 = r26
                r1 = r11
                r2 = r16
                r3 = r17
                r4 = r18
                r6 = r20
                r7 = r21
                r8 = r22
                r9 = r23
                r0.finishMatchingScenario(r1, r2, r3, r4, r6, r7, r8, r9)
                r29.writeNoException()
                return r10
            L_0x00e1:
                r14.enforceInterface(r1)
                android.os.Bundle r8 = r28.readBundle()
                long r16 = r28.readLong()
                int r9 = r28.readInt()
                int r11 = r28.readInt()
                java.lang.String r18 = r28.readString()
                java.lang.String r19 = r28.readString()
                r0 = r26
                r1 = r8
                r2 = r16
                r4 = r9
                r5 = r11
                r6 = r18
                r7 = r19
                r0.abortSpecificScenario(r1, r2, r4, r5, r6, r7)
                r29.writeNoException()
                return r10
            L_0x010e:
                r14.enforceInterface(r1)
                android.os.Parcelable r0 = r14.readParcelable(r2)
                r9 = r0
                android.os.statistics.E2EScenario r9 = (android.os.statistics.E2EScenario) r9
                java.lang.String r11 = r28.readString()
                long r16 = r28.readLong()
                int r18 = r28.readInt()
                int r19 = r28.readInt()
                java.lang.String r20 = r28.readString()
                java.lang.String r21 = r28.readString()
                r0 = r26
                r1 = r9
                r2 = r11
                r3 = r16
                r5 = r18
                r6 = r19
                r7 = r20
                r8 = r21
                r0.abortMatchingScenario(r1, r2, r3, r5, r6, r7, r8)
                r29.writeNoException()
                return r10
            L_0x0145:
                r14.enforceInterface(r1)
                android.os.Parcelable r1 = r14.readParcelable(r2)
                r16 = r1
                android.os.statistics.E2EScenario r16 = (android.os.statistics.E2EScenario) r16
                android.os.Parcelable r1 = r14.readParcelable(r2)
                r17 = r1
                android.os.statistics.E2EScenarioSettings r17 = (android.os.statistics.E2EScenarioSettings) r17
                java.lang.String r18 = r28.readString()
                android.os.Parcelable r1 = r14.readParcelable(r2)
                r19 = r1
                android.os.statistics.E2EScenarioPayload r19 = (android.os.statistics.E2EScenarioPayload) r19
                long r20 = r28.readLong()
                int r22 = r28.readInt()
                int r23 = r28.readInt()
                java.lang.String r24 = r28.readString()
                java.lang.String r25 = r28.readString()
                int r1 = r28.readInt()
                if (r1 != r10) goto L_0x0180
                r11 = r10
                goto L_0x0181
            L_0x0180:
                r11 = r0
            L_0x0181:
                r0 = r26
                r1 = r16
                r2 = r17
                r3 = r18
                r4 = r19
                r5 = r20
                r7 = r22
                r8 = r23
                r9 = r24
                r13 = r10
                r10 = r25
                android.os.Bundle r0 = r0.beginScenario(r1, r2, r3, r4, r5, r7, r8, r9, r10, r11)
                r29.writeNoException()
                r15.writeBundle(r0)
                return r13
            L_0x01a1:
                r13 = r10
                r14.enforceInterface(r1)
                android.os.ParcelFileDescriptor r1 = r26.getPerfEventSocketFd()
                r29.writeNoException()
                if (r1 == 0) goto L_0x01b5
                r15.writeInt(r13)
                r1.writeToParcel(r15, r13)
                goto L_0x01b8
            L_0x01b5:
                r15.writeInt(r0)
            L_0x01b8:
                return r13
            L_0x01b9:
                r13 = r10
                r14.enforceInterface(r1)
                int r0 = r28.readInt()
                r12.setSchedFgPid(r0)
                r29.writeNoException()
                return r13
            L_0x01c8:
                r13 = r10
                r14.enforceInterface(r1)
                java.lang.String[] r0 = r28.createStringArray()
                r12.dump(r0)
                r29.writeNoException()
                return r13
            L_0x01d7:
                r13 = r10
                r14.enforceInterface(r1)
                android.os.Parcelable$Creator r0 = android.os.Bundle.CREATOR
                java.util.ArrayList r0 = r14.createTypedArrayList(r0)
                r12.reportActivityLaunchRecords(r0)
                r29.writeNoException()
                return r13
            L_0x01e8:
                r13 = r10
                r14.enforceInterface(r1)
                int r0 = r28.readInt()
                if (r0 == 0) goto L_0x01fc
                android.os.Parcelable$Creator r0 = android.os.Bundle.CREATOR
                java.lang.Object r0 = r0.createFromParcel(r14)
                r2 = r0
                android.os.Bundle r2 = (android.os.Bundle) r2
                goto L_0x01fd
            L_0x01fc:
            L_0x01fd:
                r0 = r2
                r12.markPerceptibleJank(r0)
                r29.writeNoException()
                return r13
            L_0x0205:
                r13 = r10
                r15.writeString(r1)
                return r13
            */
            throw new UnsupportedOperationException("Method not decompiled: com.miui.daemon.performance.server.IMiuiPerfService.Stub.onTransact(int, android.os.Parcel, android.os.Parcel, int):boolean");
        }

        private static class Proxy implements IMiuiPerfService {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return Stub.DESCRIPTOR;
            }

            public void markPerceptibleJank(Bundle bundle) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (bundle != null) {
                        _data.writeInt(1);
                        bundle.writeToParcel(_data, 0);
                    }
                    this.mRemote.transact(1, _data, _reply, 1);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void reportActivityLaunchRecords(List<Bundle> launchRecords) throws RemoteException {
                Parcel _data = Parcel.obtain();
                _data.writeInterfaceToken(Stub.DESCRIPTOR);
                _data.writeTypedList(launchRecords);
                Parcel _reply = Parcel.obtain();
                try {
                    this.mRemote.transact(2, _data, _reply, 1);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void dump(String[] args) throws RemoteException {
                Parcel _data = Parcel.obtain();
                _data.writeInterfaceToken(Stub.DESCRIPTOR);
                _data.writeStringArray(args);
                Parcel _reply = Parcel.obtain();
                try {
                    this.mRemote.transact(3, _data, _reply, 1);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void setSchedFgPid(int pid) throws RemoteException {
                Parcel _data = Parcel.obtain();
                _data.writeInterfaceToken(Stub.DESCRIPTOR);
                _data.writeInt(pid);
                Parcel _reply = Parcel.obtain();
                try {
                    this.mRemote.transact(4, _data, _reply, 1);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public ParcelFileDescriptor getPerfEventSocketFd() throws RemoteException {
                ParcelFileDescriptor _result;
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(5, _data, _reply, 0);
                    _reply.readException();
                    if (_reply.readInt() != 0) {
                        _result = (ParcelFileDescriptor) ParcelFileDescriptor.CREATOR.createFromParcel(_reply);
                    } else {
                        _result = null;
                    }
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public Bundle beginScenario(E2EScenario scenario, E2EScenarioSettings settings, String tag, E2EScenarioPayload payload, long uptimeMillis, int pid, int tid, String processName, String packageName, boolean needResultBundle) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    try {
                        _data.writeParcelable(scenario, 0);
                        try {
                            _data.writeParcelable(settings, 0);
                            try {
                                _data.writeString(tag);
                                try {
                                    _data.writeParcelable(payload, 0);
                                } catch (Throwable th) {
                                    th = th;
                                    long j = uptimeMillis;
                                    int i = pid;
                                    int i2 = tid;
                                    String str = processName;
                                    String str2 = packageName;
                                    _reply.recycle();
                                    _data.recycle();
                                    throw th;
                                }
                            } catch (Throwable th2) {
                                th = th2;
                                E2EScenarioPayload e2EScenarioPayload = payload;
                                long j2 = uptimeMillis;
                                int i3 = pid;
                                int i22 = tid;
                                String str3 = processName;
                                String str22 = packageName;
                                _reply.recycle();
                                _data.recycle();
                                throw th;
                            }
                        } catch (Throwable th3) {
                            th = th3;
                            String str4 = tag;
                            E2EScenarioPayload e2EScenarioPayload2 = payload;
                            long j22 = uptimeMillis;
                            int i32 = pid;
                            int i222 = tid;
                            String str32 = processName;
                            String str222 = packageName;
                            _reply.recycle();
                            _data.recycle();
                            throw th;
                        }
                        try {
                            _data.writeLong(uptimeMillis);
                            try {
                                _data.writeInt(pid);
                                try {
                                    _data.writeInt(tid);
                                } catch (Throwable th4) {
                                    th = th4;
                                    String str322 = processName;
                                    String str2222 = packageName;
                                    _reply.recycle();
                                    _data.recycle();
                                    throw th;
                                }
                            } catch (Throwable th5) {
                                th = th5;
                                int i2222 = tid;
                                String str3222 = processName;
                                String str22222 = packageName;
                                _reply.recycle();
                                _data.recycle();
                                throw th;
                            }
                        } catch (Throwable th6) {
                            th = th6;
                            int i322 = pid;
                            int i22222 = tid;
                            String str32222 = processName;
                            String str222222 = packageName;
                            _reply.recycle();
                            _data.recycle();
                            throw th;
                        }
                        try {
                            _data.writeString(processName);
                            try {
                                _data.writeString(packageName);
                                _data.writeInt(needResultBundle ? 1 : 0);
                            } catch (Throwable th7) {
                                th = th7;
                                _reply.recycle();
                                _data.recycle();
                                throw th;
                            }
                            try {
                                this.mRemote.transact(6, _data, _reply, 0);
                                _reply.readException();
                                Bundle _result = _reply.readBundle();
                                _reply.recycle();
                                _data.recycle();
                                return _result;
                            } catch (Throwable th8) {
                                th = th8;
                                _reply.recycle();
                                _data.recycle();
                                throw th;
                            }
                        } catch (Throwable th9) {
                            th = th9;
                            String str2222222 = packageName;
                            _reply.recycle();
                            _data.recycle();
                            throw th;
                        }
                    } catch (Throwable th10) {
                        th = th10;
                        E2EScenarioSettings e2EScenarioSettings = settings;
                        String str42 = tag;
                        E2EScenarioPayload e2EScenarioPayload22 = payload;
                        long j222 = uptimeMillis;
                        int i3222 = pid;
                        int i222222 = tid;
                        String str322222 = processName;
                        String str22222222 = packageName;
                        _reply.recycle();
                        _data.recycle();
                        throw th;
                    }
                } catch (Throwable th11) {
                    th = th11;
                    E2EScenario e2EScenario = scenario;
                    E2EScenarioSettings e2EScenarioSettings2 = settings;
                    String str422 = tag;
                    E2EScenarioPayload e2EScenarioPayload222 = payload;
                    long j2222 = uptimeMillis;
                    int i32222 = pid;
                    int i2222222 = tid;
                    String str3222222 = processName;
                    String str222222222 = packageName;
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            public void abortMatchingScenario(E2EScenario scenario, String tag, long uptimeMillis, int pid, int tid, String processName, String packageName) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeParcelable(scenario, 0);
                    _data.writeString(tag);
                    _data.writeLong(uptimeMillis);
                    _data.writeInt(pid);
                    _data.writeInt(tid);
                    _data.writeString(processName);
                    _data.writeString(packageName);
                    this.mRemote.transact(7, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void abortSpecificScenario(Bundle scenarioBundle, long uptimeMillis, int pid, int tid, String processName, String packageName) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeBundle(scenarioBundle);
                    _data.writeLong(uptimeMillis);
                    _data.writeInt(pid);
                    _data.writeInt(tid);
                    _data.writeString(processName);
                    _data.writeString(packageName);
                    this.mRemote.transact(8, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void finishMatchingScenario(E2EScenario scenario, String tag, E2EScenarioPayload payload, long uptimeMillis, int pid, int tid, String processName, String packageName) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeParcelable(scenario, 0);
                    _data.writeString(tag);
                    _data.writeParcelable(payload, 0);
                    _data.writeLong(uptimeMillis);
                    _data.writeInt(pid);
                    _data.writeInt(tid);
                    _data.writeString(processName);
                    _data.writeString(packageName);
                    this.mRemote.transact(9, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void finishSpecificScenario(Bundle scenarioBundle, E2EScenarioPayload payload, long uptimeMillis, int pid, int tid, String processName, String packageName) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeBundle(scenarioBundle);
                    _data.writeParcelable(payload, 0);
                    _data.writeLong(uptimeMillis);
                    _data.writeInt(pid);
                    _data.writeInt(tid);
                    _data.writeString(processName);
                    _data.writeString(packageName);
                    this.mRemote.transact(10, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void reportExcessiveCpuUsageRecords(List<Bundle> records) throws RemoteException {
                Parcel _data = Parcel.obtain();
                _data.writeInterfaceToken(Stub.DESCRIPTOR);
                _data.writeTypedList(records);
                Parcel _reply = Parcel.obtain();
                try {
                    this.mRemote.transact(11, _data, _reply, 1);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void reportNotificationClick(String postPackage, Intent intent, long uptimeMillis) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(postPackage);
                    if (intent != null) {
                        _data.writeInt(1);
                        intent.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeLong(uptimeMillis);
                    this.mRemote.transact(12, _data, _reply, 1);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void reportProcessCleanEvent(Bundle bundle) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (bundle != null) {
                        _data.writeInt(1);
                        bundle.writeToParcel(_data, 0);
                    }
                    this.mRemote.transact(13, _data, _reply, 1);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }
        }
    }
}
