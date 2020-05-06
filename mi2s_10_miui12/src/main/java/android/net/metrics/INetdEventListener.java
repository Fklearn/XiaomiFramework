package android.net.metrics;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

public interface INetdEventListener extends IInterface {
    public static final int DNS_REPORTED_IP_ADDRESSES_LIMIT = 10;
    public static final int EVENT_GETADDRINFO = 1;
    public static final int EVENT_GETHOSTBYADDR = 3;
    public static final int EVENT_GETHOSTBYNAME = 2;
    public static final int EVENT_RES_NSEND = 4;
    public static final int REPORTING_LEVEL_FULL = 2;
    public static final int REPORTING_LEVEL_METRICS = 1;
    public static final int REPORTING_LEVEL_NONE = 0;
    public static final int VERSION = 10000;

    int getInterfaceVersion() throws RemoteException;

    void onConnectEvent(int i, int i2, int i3, String str, int i4, int i5) throws RemoteException;

    void onDnsEvent(int i, int i2, int i3, int i4, String str, String[] strArr, int i5, int i6) throws RemoteException;

    void onNat64PrefixEvent(int i, boolean z, String str, int i2) throws RemoteException;

    void onPrivateDnsValidationEvent(int i, String str, String str2, boolean z) throws RemoteException;

    void onTcpSocketStatsEvent(int[] iArr, int[] iArr2, int[] iArr3, int[] iArr4, int[] iArr5) throws RemoteException;

    void onWakeupEvent(String str, int i, int i2, int i3, byte[] bArr, String str2, String str3, int i4, int i5, long j) throws RemoteException;

    public static class Default implements INetdEventListener {
        public void onDnsEvent(int netId, int eventType, int returnCode, int latencyMs, String hostname, String[] ipAddresses, int ipAddressesCount, int uid) throws RemoteException {
        }

        public void onPrivateDnsValidationEvent(int netId, String ipAddress, String hostname, boolean validated) throws RemoteException {
        }

        public void onConnectEvent(int netId, int error, int latencyMs, String ipAddr, int port, int uid) throws RemoteException {
        }

        public void onWakeupEvent(String prefix, int uid, int ethertype, int ipNextHeader, byte[] dstHw, String srcIp, String dstIp, int srcPort, int dstPort, long timestampNs) throws RemoteException {
        }

        public void onTcpSocketStatsEvent(int[] networkIds, int[] sentPackets, int[] lostPackets, int[] rttUs, int[] sentAckDiffMs) throws RemoteException {
        }

        public void onNat64PrefixEvent(int netId, boolean added, String prefixString, int prefixLength) throws RemoteException {
        }

        public int getInterfaceVersion() {
            return -1;
        }

        public IBinder asBinder() {
            return null;
        }
    }

    public static abstract class Stub extends Binder implements INetdEventListener {
        private static final String DESCRIPTOR = "android.net.metrics.INetdEventListener";
        static final int TRANSACTION_getInterfaceVersion = 16777215;
        static final int TRANSACTION_onConnectEvent = 3;
        static final int TRANSACTION_onDnsEvent = 1;
        static final int TRANSACTION_onNat64PrefixEvent = 6;
        static final int TRANSACTION_onPrivateDnsValidationEvent = 2;
        static final int TRANSACTION_onTcpSocketStatsEvent = 5;
        static final int TRANSACTION_onWakeupEvent = 4;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static INetdEventListener asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin == null || !(iin instanceof INetdEventListener)) {
                return new Proxy(obj);
            }
            return (INetdEventListener) iin;
        }

        public IBinder asBinder() {
            return this;
        }

        /*  JADX ERROR: NullPointerException in pass: CodeShrinkVisitor
            java.lang.NullPointerException
            	at jadx.core.dex.instructions.args.InsnArg.wrapInstruction(InsnArg.java:118)
            	at jadx.core.dex.visitors.shrink.CodeShrinkVisitor.inline(CodeShrinkVisitor.java:146)
            	at jadx.core.dex.visitors.shrink.CodeShrinkVisitor.shrinkBlock(CodeShrinkVisitor.java:71)
            	at jadx.core.dex.visitors.shrink.CodeShrinkVisitor.shrinkMethod(CodeShrinkVisitor.java:43)
            	at jadx.core.dex.visitors.shrink.CodeShrinkVisitor.visit(CodeShrinkVisitor.java:35)
            */
        public boolean onTransact(int r29, android.os.Parcel r30, android.os.Parcel r31, int r32) throws android.os.RemoteException {
            /*
                r28 = this;
                r12 = r28
                r13 = r29
                r14 = r30
                r15 = r31
                java.lang.String r10 = "android.net.metrics.INetdEventListener"
                r0 = 16777215(0xffffff, float:2.3509886E-38)
                r16 = 1
                if (r13 == r0) goto L_0x0123
                r0 = 1598968902(0x5f4e5446, float:1.4867585E19)
                if (r13 == r0) goto L_0x011e
                r0 = 0
                switch(r13) {
                    case 1: goto L_0x00e7;
                    case 2: goto L_0x00cb;
                    case 3: goto L_0x00a2;
                    case 4: goto L_0x005c;
                    case 5: goto L_0x003a;
                    case 6: goto L_0x001f;
                    default: goto L_0x001a;
                }
            L_0x001a:
                boolean r0 = super.onTransact(r29, r30, r31, r32)
                return r0
            L_0x001f:
                r14.enforceInterface(r10)
                int r1 = r30.readInt()
                int r2 = r30.readInt()
                if (r2 == 0) goto L_0x002e
                r0 = r16
            L_0x002e:
                java.lang.String r2 = r30.readString()
                int r3 = r30.readInt()
                r12.onNat64PrefixEvent(r1, r0, r2, r3)
                return r16
            L_0x003a:
                r14.enforceInterface(r10)
                int[] r6 = r30.createIntArray()
                int[] r7 = r30.createIntArray()
                int[] r8 = r30.createIntArray()
                int[] r9 = r30.createIntArray()
                int[] r11 = r30.createIntArray()
                r0 = r28
                r1 = r6
                r2 = r7
                r3 = r8
                r4 = r9
                r5 = r11
                r0.onTcpSocketStatsEvent(r1, r2, r3, r4, r5)
                return r16
            L_0x005c:
                r14.enforceInterface(r10)
                java.lang.String r17 = r30.readString()
                int r18 = r30.readInt()
                int r19 = r30.readInt()
                int r20 = r30.readInt()
                byte[] r21 = r30.createByteArray()
                java.lang.String r22 = r30.readString()
                java.lang.String r23 = r30.readString()
                int r24 = r30.readInt()
                int r25 = r30.readInt()
                long r26 = r30.readLong()
                r0 = r28
                r1 = r17
                r2 = r18
                r3 = r19
                r4 = r20
                r5 = r21
                r6 = r22
                r7 = r23
                r8 = r24
                r9 = r25
                r13 = r10
                r10 = r26
                r0.onWakeupEvent(r1, r2, r3, r4, r5, r6, r7, r8, r9, r10)
                return r16
            L_0x00a2:
                r13 = r10
                r14.enforceInterface(r13)
                int r7 = r30.readInt()
                int r8 = r30.readInt()
                int r9 = r30.readInt()
                java.lang.String r10 = r30.readString()
                int r11 = r30.readInt()
                int r17 = r30.readInt()
                r0 = r28
                r1 = r7
                r2 = r8
                r3 = r9
                r4 = r10
                r5 = r11
                r6 = r17
                r0.onConnectEvent(r1, r2, r3, r4, r5, r6)
                return r16
            L_0x00cb:
                r13 = r10
                r14.enforceInterface(r13)
                int r1 = r30.readInt()
                java.lang.String r2 = r30.readString()
                java.lang.String r3 = r30.readString()
                int r4 = r30.readInt()
                if (r4 == 0) goto L_0x00e3
                r0 = r16
            L_0x00e3:
                r12.onPrivateDnsValidationEvent(r1, r2, r3, r0)
                return r16
            L_0x00e7:
                r13 = r10
                r14.enforceInterface(r13)
                int r9 = r30.readInt()
                int r10 = r30.readInt()
                int r11 = r30.readInt()
                int r17 = r30.readInt()
                java.lang.String r18 = r30.readString()
                java.lang.String[] r19 = r30.createStringArray()
                int r20 = r30.readInt()
                int r21 = r30.readInt()
                r0 = r28
                r1 = r9
                r2 = r10
                r3 = r11
                r4 = r17
                r5 = r18
                r6 = r19
                r7 = r20
                r8 = r21
                r0.onDnsEvent(r1, r2, r3, r4, r5, r6, r7, r8)
                return r16
            L_0x011e:
                r13 = r10
                r15.writeString(r13)
                return r16
            L_0x0123:
                r13 = r10
                r14.enforceInterface(r13)
                r31.writeNoException()
                int r0 = r28.getInterfaceVersion()
                r15.writeInt(r0)
                return r16
            */
            throw new UnsupportedOperationException("Method not decompiled: android.net.metrics.INetdEventListener.Stub.onTransact(int, android.os.Parcel, android.os.Parcel, int):boolean");
        }

        private static class Proxy implements INetdEventListener {
            public static INetdEventListener sDefaultImpl;
            private int mCachedVersion = -1;
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

            public void onDnsEvent(int netId, int eventType, int returnCode, int latencyMs, String hostname, String[] ipAddresses, int ipAddressesCount, int uid) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    try {
                        _data.writeInt(netId);
                    } catch (Throwable th) {
                        th = th;
                        int i = eventType;
                        int i2 = returnCode;
                        int i3 = latencyMs;
                        String str = hostname;
                        _data.recycle();
                        throw th;
                    }
                    try {
                        _data.writeInt(eventType);
                        try {
                            _data.writeInt(returnCode);
                        } catch (Throwable th2) {
                            th = th2;
                            int i32 = latencyMs;
                            String str2 = hostname;
                            _data.recycle();
                            throw th;
                        }
                        try {
                            _data.writeInt(latencyMs);
                            try {
                                _data.writeString(hostname);
                                _data.writeStringArray(ipAddresses);
                                _data.writeInt(ipAddressesCount);
                                _data.writeInt(uid);
                                if (this.mRemote.transact(1, _data, (Parcel) null, 1) || Stub.getDefaultImpl() == null) {
                                    _data.recycle();
                                    return;
                                }
                                Stub.getDefaultImpl().onDnsEvent(netId, eventType, returnCode, latencyMs, hostname, ipAddresses, ipAddressesCount, uid);
                                _data.recycle();
                            } catch (Throwable th3) {
                                th = th3;
                                _data.recycle();
                                throw th;
                            }
                        } catch (Throwable th4) {
                            th = th4;
                            String str22 = hostname;
                            _data.recycle();
                            throw th;
                        }
                    } catch (Throwable th5) {
                        th = th5;
                        int i22 = returnCode;
                        int i322 = latencyMs;
                        String str222 = hostname;
                        _data.recycle();
                        throw th;
                    }
                } catch (Throwable th6) {
                    th = th6;
                    int i4 = netId;
                    int i5 = eventType;
                    int i222 = returnCode;
                    int i3222 = latencyMs;
                    String str2222 = hostname;
                    _data.recycle();
                    throw th;
                }
            }

            public void onPrivateDnsValidationEvent(int netId, String ipAddress, String hostname, boolean validated) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(netId);
                    _data.writeString(ipAddress);
                    _data.writeString(hostname);
                    _data.writeInt(validated ? 1 : 0);
                    if (this.mRemote.transact(2, _data, (Parcel) null, 1) || Stub.getDefaultImpl() == null) {
                        _data.recycle();
                    } else {
                        Stub.getDefaultImpl().onPrivateDnsValidationEvent(netId, ipAddress, hostname, validated);
                    }
                } finally {
                    _data.recycle();
                }
            }

            public void onConnectEvent(int netId, int error, int latencyMs, String ipAddr, int port, int uid) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    try {
                        _data.writeInt(netId);
                    } catch (Throwable th) {
                        th = th;
                        int i = error;
                        int i2 = latencyMs;
                        String str = ipAddr;
                        int i3 = port;
                        int i4 = uid;
                        _data.recycle();
                        throw th;
                    }
                    try {
                        _data.writeInt(error);
                        try {
                            _data.writeInt(latencyMs);
                            try {
                                _data.writeString(ipAddr);
                                try {
                                    _data.writeInt(port);
                                } catch (Throwable th2) {
                                    th = th2;
                                    int i42 = uid;
                                    _data.recycle();
                                    throw th;
                                }
                            } catch (Throwable th3) {
                                th = th3;
                                int i32 = port;
                                int i422 = uid;
                                _data.recycle();
                                throw th;
                            }
                        } catch (Throwable th4) {
                            th = th4;
                            String str2 = ipAddr;
                            int i322 = port;
                            int i4222 = uid;
                            _data.recycle();
                            throw th;
                        }
                        try {
                            _data.writeInt(uid);
                            try {
                                if (this.mRemote.transact(3, _data, (Parcel) null, 1) || Stub.getDefaultImpl() == null) {
                                    _data.recycle();
                                    return;
                                }
                                Stub.getDefaultImpl().onConnectEvent(netId, error, latencyMs, ipAddr, port, uid);
                                _data.recycle();
                            } catch (Throwable th5) {
                                th = th5;
                                _data.recycle();
                                throw th;
                            }
                        } catch (Throwable th6) {
                            th = th6;
                            _data.recycle();
                            throw th;
                        }
                    } catch (Throwable th7) {
                        th = th7;
                        int i22 = latencyMs;
                        String str22 = ipAddr;
                        int i3222 = port;
                        int i42222 = uid;
                        _data.recycle();
                        throw th;
                    }
                } catch (Throwable th8) {
                    th = th8;
                    int i5 = netId;
                    int i6 = error;
                    int i222 = latencyMs;
                    String str222 = ipAddr;
                    int i32222 = port;
                    int i422222 = uid;
                    _data.recycle();
                    throw th;
                }
            }

            public void onWakeupEvent(String prefix, int uid, int ethertype, int ipNextHeader, byte[] dstHw, String srcIp, String dstIp, int srcPort, int dstPort, long timestampNs) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    try {
                        _data.writeString(prefix);
                    } catch (Throwable th) {
                        th = th;
                        int i = uid;
                        _data.recycle();
                        throw th;
                    }
                    try {
                        _data.writeInt(uid);
                        _data.writeInt(ethertype);
                        _data.writeInt(ipNextHeader);
                        _data.writeByteArray(dstHw);
                        _data.writeString(srcIp);
                        _data.writeString(dstIp);
                        _data.writeInt(srcPort);
                        _data.writeInt(dstPort);
                        _data.writeLong(timestampNs);
                        if (this.mRemote.transact(4, _data, (Parcel) null, 1) || Stub.getDefaultImpl() == null) {
                            _data.recycle();
                            return;
                        }
                        Stub.getDefaultImpl().onWakeupEvent(prefix, uid, ethertype, ipNextHeader, dstHw, srcIp, dstIp, srcPort, dstPort, timestampNs);
                        _data.recycle();
                    } catch (Throwable th2) {
                        th = th2;
                        _data.recycle();
                        throw th;
                    }
                } catch (Throwable th3) {
                    th = th3;
                    String str = prefix;
                    int i2 = uid;
                    _data.recycle();
                    throw th;
                }
            }

            public void onTcpSocketStatsEvent(int[] networkIds, int[] sentPackets, int[] lostPackets, int[] rttUs, int[] sentAckDiffMs) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeIntArray(networkIds);
                    _data.writeIntArray(sentPackets);
                    _data.writeIntArray(lostPackets);
                    _data.writeIntArray(rttUs);
                    _data.writeIntArray(sentAckDiffMs);
                    if (this.mRemote.transact(5, _data, (Parcel) null, 1) || Stub.getDefaultImpl() == null) {
                        _data.recycle();
                    } else {
                        Stub.getDefaultImpl().onTcpSocketStatsEvent(networkIds, sentPackets, lostPackets, rttUs, sentAckDiffMs);
                    }
                } finally {
                    _data.recycle();
                }
            }

            public void onNat64PrefixEvent(int netId, boolean added, String prefixString, int prefixLength) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(netId);
                    _data.writeInt(added ? 1 : 0);
                    _data.writeString(prefixString);
                    _data.writeInt(prefixLength);
                    if (this.mRemote.transact(6, _data, (Parcel) null, 1) || Stub.getDefaultImpl() == null) {
                        _data.recycle();
                    } else {
                        Stub.getDefaultImpl().onNat64PrefixEvent(netId, added, prefixString, prefixLength);
                    }
                } finally {
                    _data.recycle();
                }
            }

            public int getInterfaceVersion() throws RemoteException {
                if (this.mCachedVersion == -1) {
                    Parcel data = Parcel.obtain();
                    Parcel reply = Parcel.obtain();
                    try {
                        data.writeInterfaceToken(Stub.DESCRIPTOR);
                        this.mRemote.transact(Stub.TRANSACTION_getInterfaceVersion, data, reply, 0);
                        reply.readException();
                        this.mCachedVersion = reply.readInt();
                    } finally {
                        reply.recycle();
                        data.recycle();
                    }
                }
                return this.mCachedVersion;
            }
        }

        public static boolean setDefaultImpl(INetdEventListener impl) {
            if (Proxy.sDefaultImpl != null || impl == null) {
                return false;
            }
            Proxy.sDefaultImpl = impl;
            return true;
        }

        public static INetdEventListener getDefaultImpl() {
            return Proxy.sDefaultImpl;
        }
    }
}
