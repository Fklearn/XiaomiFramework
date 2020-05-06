package android.net.ip;

import android.net.IpPrefix;
import android.net.LinkAddress;
import android.net.NetworkUtils;
import android.net.TrafficStats;
import android.net.util.InterfaceParams;
import android.system.ErrnoException;
import android.system.Os;
import android.system.OsConstants;
import android.system.StructTimeval;
import android.util.Log;
import com.android.internal.annotations.GuardedBy;
import java.io.FileDescriptor;
import java.io.IOException;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import libcore.io.IoBridge;

public class RouterAdvertisementDaemon {
    private static final byte[] ALL_NODES = {-1, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1};
    private static final int DAY_IN_SECONDS = 86400;
    private static final int DEFAULT_LIFETIME = 3600;
    private static final byte ICMPV6_ND_ROUTER_ADVERT = asByte(134);
    /* access modifiers changed from: private */
    public static final byte ICMPV6_ND_ROUTER_SOLICIT = asByte(133);
    private static final int MAX_RTR_ADV_INTERVAL_SEC = 600;
    private static final int MAX_URGENT_RTR_ADVERTISEMENTS = 5;
    private static final int MIN_DELAY_BETWEEN_RAS_SEC = 3;
    private static final int MIN_RA_HEADER_SIZE = 16;
    private static final int MIN_RTR_ADV_INTERVAL_SEC = 300;
    /* access modifiers changed from: private */
    public static final String TAG = RouterAdvertisementDaemon.class.getSimpleName();
    /* access modifiers changed from: private */
    public final InetSocketAddress mAllNodes;
    /* access modifiers changed from: private */
    @GuardedBy({"mLock"})
    public final DeprecatedInfoTracker mDeprecatedInfoTracker;
    private final InterfaceParams mInterface;
    /* access modifiers changed from: private */
    public final Object mLock = new Object();
    private volatile MulticastTransmitter mMulticastTransmitter;
    @GuardedBy({"mLock"})
    private final byte[] mRA = new byte[1280];
    /* access modifiers changed from: private */
    @GuardedBy({"mLock"})
    public int mRaLength;
    @GuardedBy({"mLock"})
    private RaParams mRaParams;
    /* access modifiers changed from: private */
    public volatile FileDescriptor mSocket;
    private volatile UnicastResponder mUnicastResponder;

    public static class RaParams {
        static final byte DEFAULT_HOPLIMIT = 65;
        public HashSet<Inet6Address> dnses;
        public boolean hasDefaultRoute;
        public byte hopLimit;
        public int mtu;
        public HashSet<IpPrefix> prefixes;

        public RaParams() {
            this.hasDefaultRoute = false;
            this.hopLimit = DEFAULT_HOPLIMIT;
            this.mtu = 1280;
            this.prefixes = new HashSet<>();
            this.dnses = new HashSet<>();
        }

        public RaParams(RaParams other) {
            this.hasDefaultRoute = other.hasDefaultRoute;
            this.hopLimit = other.hopLimit;
            this.mtu = other.mtu;
            this.prefixes = (HashSet) other.prefixes.clone();
            this.dnses = (HashSet) other.dnses.clone();
        }

        public static RaParams getDeprecatedRaParams(RaParams oldRa, RaParams newRa) {
            RaParams newlyDeprecated = new RaParams();
            if (oldRa != null) {
                Iterator<IpPrefix> it = oldRa.prefixes.iterator();
                while (it.hasNext()) {
                    IpPrefix ipp = it.next();
                    if (newRa == null || !newRa.prefixes.contains(ipp)) {
                        newlyDeprecated.prefixes.add(ipp);
                    }
                }
                Iterator<Inet6Address> it2 = oldRa.dnses.iterator();
                while (it2.hasNext()) {
                    Inet6Address dns = it2.next();
                    if (newRa == null || !newRa.dnses.contains(dns)) {
                        newlyDeprecated.dnses.add(dns);
                    }
                }
            }
            return newlyDeprecated;
        }
    }

    private static class DeprecatedInfoTracker {
        private final HashMap<Inet6Address, Integer> mDnses;
        private final HashMap<IpPrefix, Integer> mPrefixes;

        private DeprecatedInfoTracker() {
            this.mPrefixes = new HashMap<>();
            this.mDnses = new HashMap<>();
        }

        /* access modifiers changed from: package-private */
        public Set<IpPrefix> getPrefixes() {
            return this.mPrefixes.keySet();
        }

        /* access modifiers changed from: package-private */
        public void putPrefixes(Set<IpPrefix> prefixes) {
            for (IpPrefix ipp : prefixes) {
                this.mPrefixes.put(ipp, 5);
            }
        }

        /* access modifiers changed from: package-private */
        public void removePrefixes(Set<IpPrefix> prefixes) {
            for (IpPrefix ipp : prefixes) {
                this.mPrefixes.remove(ipp);
            }
        }

        /* access modifiers changed from: package-private */
        public Set<Inet6Address> getDnses() {
            return this.mDnses.keySet();
        }

        /* access modifiers changed from: package-private */
        public void putDnses(Set<Inet6Address> dnses) {
            for (Inet6Address dns : dnses) {
                this.mDnses.put(dns, 5);
            }
        }

        /* access modifiers changed from: package-private */
        public void removeDnses(Set<Inet6Address> dnses) {
            for (Inet6Address dns : dnses) {
                this.mDnses.remove(dns);
            }
        }

        /* access modifiers changed from: package-private */
        public boolean isEmpty() {
            return this.mPrefixes.isEmpty() && this.mDnses.isEmpty();
        }

        /* access modifiers changed from: private */
        public boolean decrementCounters() {
            return decrementCounter(this.mPrefixes) | decrementCounter(this.mDnses);
        }

        private <T> boolean decrementCounter(HashMap<T, Integer> map) {
            boolean removed = false;
            Iterator<Map.Entry<T, Integer>> it = map.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<T, Integer> kv = it.next();
                if (kv.getValue().intValue() == 0) {
                    it.remove();
                    removed = true;
                } else {
                    kv.setValue(Integer.valueOf(kv.getValue().intValue() - 1));
                }
            }
            return removed;
        }
    }

    public RouterAdvertisementDaemon(InterfaceParams ifParams) {
        this.mInterface = ifParams;
        this.mAllNodes = new InetSocketAddress(getAllNodesForScopeId(this.mInterface.index), 0);
        this.mDeprecatedInfoTracker = new DeprecatedInfoTracker();
    }

    public void buildNewRa(RaParams deprecatedParams, RaParams newParams) {
        synchronized (this.mLock) {
            if (deprecatedParams != null) {
                try {
                    this.mDeprecatedInfoTracker.putPrefixes(deprecatedParams.prefixes);
                    this.mDeprecatedInfoTracker.putDnses(deprecatedParams.dnses);
                } catch (Throwable th) {
                    while (true) {
                        throw th;
                    }
                }
            }
            if (newParams != null) {
                this.mDeprecatedInfoTracker.removePrefixes(newParams.prefixes);
                this.mDeprecatedInfoTracker.removeDnses(newParams.dnses);
            }
            this.mRaParams = newParams;
            assembleRaLocked();
        }
        maybeNotifyMulticastTransmitter();
    }

    public boolean start() {
        if (!createSocket()) {
            return false;
        }
        this.mMulticastTransmitter = new MulticastTransmitter();
        this.mMulticastTransmitter.start();
        this.mUnicastResponder = new UnicastResponder();
        this.mUnicastResponder.start();
        return true;
    }

    public void stop() {
        closeSocket();
        maybeNotifyMulticastTransmitter();
        this.mMulticastTransmitter = null;
        this.mUnicastResponder = null;
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Removed duplicated region for block: B:14:0x0024 A[Catch:{ BufferOverflowException -> 0x001e }] */
    /* JADX WARNING: Removed duplicated region for block: B:15:0x0029 A[Catch:{ BufferOverflowException -> 0x001e }] */
    /* JADX WARNING: Removed duplicated region for block: B:18:0x0041 A[Catch:{ BufferOverflowException -> 0x001e }] */
    /* JADX WARNING: Removed duplicated region for block: B:28:0x0098 A[Catch:{ BufferOverflowException -> 0x001e }, LOOP:1: B:26:0x0092->B:28:0x0098, LOOP_END] */
    /* JADX WARNING: Removed duplicated region for block: B:31:0x00b5 A[Catch:{ BufferOverflowException -> 0x001e }] */
    /* JADX WARNING: Removed duplicated region for block: B:35:0x00d8  */
    /* JADX WARNING: Removed duplicated region for block: B:39:? A[RETURN, SYNTHETIC] */
    @com.android.internal.annotations.GuardedBy({"mLock"})
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void assembleRaLocked() {
        /*
            r8 = this;
            byte[] r0 = r8.mRA
            java.nio.ByteBuffer r0 = java.nio.ByteBuffer.wrap(r0)
            java.nio.ByteOrder r1 = java.nio.ByteOrder.BIG_ENDIAN
            r0.order(r1)
            android.net.ip.RouterAdvertisementDaemon$RaParams r1 = r8.mRaParams
            r2 = 1
            r3 = 0
            if (r1 == 0) goto L_0x0013
            r1 = r2
            goto L_0x0014
        L_0x0013:
            r1 = r3
        L_0x0014:
            r4 = 0
            if (r1 == 0) goto L_0x0021
            android.net.ip.RouterAdvertisementDaemon$RaParams r5 = r8.mRaParams     // Catch:{ BufferOverflowException -> 0x001e }
            boolean r5 = r5.hasDefaultRoute     // Catch:{ BufferOverflowException -> 0x001e }
            if (r5 == 0) goto L_0x0021
            goto L_0x0022
        L_0x001e:
            r2 = move-exception
            goto L_0x00c0
        L_0x0021:
            r2 = r3
        L_0x0022:
            if (r1 == 0) goto L_0x0029
            android.net.ip.RouterAdvertisementDaemon$RaParams r5 = r8.mRaParams     // Catch:{ BufferOverflowException -> 0x001e }
            byte r5 = r5.hopLimit     // Catch:{ BufferOverflowException -> 0x001e }
            goto L_0x002b
        L_0x0029:
            r5 = 65
        L_0x002b:
            putHeader(r0, r2, r5)     // Catch:{ BufferOverflowException -> 0x001e }
            android.net.util.InterfaceParams r2 = r8.mInterface     // Catch:{ BufferOverflowException -> 0x001e }
            android.net.MacAddress r2 = r2.macAddr     // Catch:{ BufferOverflowException -> 0x001e }
            byte[] r2 = r2.toByteArray()     // Catch:{ BufferOverflowException -> 0x001e }
            putSlla(r0, r2)     // Catch:{ BufferOverflowException -> 0x001e }
            int r2 = r0.position()     // Catch:{ BufferOverflowException -> 0x001e }
            r8.mRaLength = r2     // Catch:{ BufferOverflowException -> 0x001e }
            if (r1 == 0) goto L_0x0088
            android.net.ip.RouterAdvertisementDaemon$RaParams r2 = r8.mRaParams     // Catch:{ BufferOverflowException -> 0x001e }
            int r2 = r2.mtu     // Catch:{ BufferOverflowException -> 0x001e }
            putMtu(r0, r2)     // Catch:{ BufferOverflowException -> 0x001e }
            int r2 = r0.position()     // Catch:{ BufferOverflowException -> 0x001e }
            r8.mRaLength = r2     // Catch:{ BufferOverflowException -> 0x001e }
            android.net.ip.RouterAdvertisementDaemon$RaParams r2 = r8.mRaParams     // Catch:{ BufferOverflowException -> 0x001e }
            java.util.HashSet<android.net.IpPrefix> r2 = r2.prefixes     // Catch:{ BufferOverflowException -> 0x001e }
            java.util.Iterator r2 = r2.iterator()     // Catch:{ BufferOverflowException -> 0x001e }
        L_0x0056:
            boolean r5 = r2.hasNext()     // Catch:{ BufferOverflowException -> 0x001e }
            r6 = 3600(0xe10, float:5.045E-42)
            if (r5 == 0) goto L_0x006f
            java.lang.Object r5 = r2.next()     // Catch:{ BufferOverflowException -> 0x001e }
            android.net.IpPrefix r5 = (android.net.IpPrefix) r5     // Catch:{ BufferOverflowException -> 0x001e }
            putPio(r0, r5, r6, r6)     // Catch:{ BufferOverflowException -> 0x001e }
            int r6 = r0.position()     // Catch:{ BufferOverflowException -> 0x001e }
            r8.mRaLength = r6     // Catch:{ BufferOverflowException -> 0x001e }
            r4 = 1
            goto L_0x0056
        L_0x006f:
            android.net.ip.RouterAdvertisementDaemon$RaParams r2 = r8.mRaParams     // Catch:{ BufferOverflowException -> 0x001e }
            java.util.HashSet<java.net.Inet6Address> r2 = r2.dnses     // Catch:{ BufferOverflowException -> 0x001e }
            int r2 = r2.size()     // Catch:{ BufferOverflowException -> 0x001e }
            if (r2 <= 0) goto L_0x0088
            android.net.ip.RouterAdvertisementDaemon$RaParams r2 = r8.mRaParams     // Catch:{ BufferOverflowException -> 0x001e }
            java.util.HashSet<java.net.Inet6Address> r2 = r2.dnses     // Catch:{ BufferOverflowException -> 0x001e }
            putRdnss(r0, r2, r6)     // Catch:{ BufferOverflowException -> 0x001e }
            int r2 = r0.position()     // Catch:{ BufferOverflowException -> 0x001e }
            r8.mRaLength = r2     // Catch:{ BufferOverflowException -> 0x001e }
            r2 = 1
            r4 = r2
        L_0x0088:
            android.net.ip.RouterAdvertisementDaemon$DeprecatedInfoTracker r2 = r8.mDeprecatedInfoTracker     // Catch:{ BufferOverflowException -> 0x001e }
            java.util.Set r2 = r2.getPrefixes()     // Catch:{ BufferOverflowException -> 0x001e }
            java.util.Iterator r2 = r2.iterator()     // Catch:{ BufferOverflowException -> 0x001e }
        L_0x0092:
            boolean r5 = r2.hasNext()     // Catch:{ BufferOverflowException -> 0x001e }
            if (r5 == 0) goto L_0x00a9
            java.lang.Object r5 = r2.next()     // Catch:{ BufferOverflowException -> 0x001e }
            android.net.IpPrefix r5 = (android.net.IpPrefix) r5     // Catch:{ BufferOverflowException -> 0x001e }
            putPio(r0, r5, r3, r3)     // Catch:{ BufferOverflowException -> 0x001e }
            int r6 = r0.position()     // Catch:{ BufferOverflowException -> 0x001e }
            r8.mRaLength = r6     // Catch:{ BufferOverflowException -> 0x001e }
            r4 = 1
            goto L_0x0092
        L_0x00a9:
            android.net.ip.RouterAdvertisementDaemon$DeprecatedInfoTracker r2 = r8.mDeprecatedInfoTracker     // Catch:{ BufferOverflowException -> 0x001e }
            java.util.Set r2 = r2.getDnses()     // Catch:{ BufferOverflowException -> 0x001e }
            boolean r5 = r2.isEmpty()     // Catch:{ BufferOverflowException -> 0x001e }
            if (r5 != 0) goto L_0x00bf
            putRdnss(r0, r2, r3)     // Catch:{ BufferOverflowException -> 0x001e }
            int r5 = r0.position()     // Catch:{ BufferOverflowException -> 0x001e }
            r8.mRaLength = r5     // Catch:{ BufferOverflowException -> 0x001e }
            r4 = 1
        L_0x00bf:
            goto L_0x00d6
        L_0x00c0:
            java.lang.String r5 = TAG
            java.lang.StringBuilder r6 = new java.lang.StringBuilder
            r6.<init>()
            java.lang.String r7 = "Could not construct new RA: "
            r6.append(r7)
            r6.append(r2)
            java.lang.String r6 = r6.toString()
            android.util.Log.e(r5, r6)
        L_0x00d6:
            if (r4 != 0) goto L_0x00da
            r8.mRaLength = r3
        L_0x00da:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: android.net.ip.RouterAdvertisementDaemon.assembleRaLocked():void");
    }

    private void maybeNotifyMulticastTransmitter() {
        MulticastTransmitter m = this.mMulticastTransmitter;
        if (m != null) {
            m.hup();
        }
    }

    private static Inet6Address getAllNodesForScopeId(int scopeId) {
        try {
            return Inet6Address.getByAddress("ff02::1", ALL_NODES, scopeId);
        } catch (UnknownHostException uhe) {
            String str = TAG;
            Log.wtf(str, "Failed to construct ff02::1 InetAddress: " + uhe);
            return null;
        }
    }

    private static byte asByte(int value) {
        return (byte) value;
    }

    private static short asShort(int value) {
        return (short) value;
    }

    private static void putHeader(ByteBuffer ra, boolean hasDefaultRoute, byte hopLimit) {
        ra.put(ICMPV6_ND_ROUTER_ADVERT).put(asByte(0)).putShort(asShort(0)).put(hopLimit).put(hasDefaultRoute ? asByte(8) : asByte(0)).putShort(hasDefaultRoute ? asShort(DEFAULT_LIFETIME) : asShort(0)).putInt(0).putInt(0);
    }

    private static void putSlla(ByteBuffer ra, byte[] slla) {
        if (slla != null && slla.length == 6) {
            ra.put((byte) 1).put((byte) 1).put(slla);
        }
    }

    private static void putExpandedFlagsOption(ByteBuffer ra) {
        ra.put((byte) 26).put((byte) 1).putShort(asShort(0)).putInt(0);
    }

    private static void putMtu(ByteBuffer ra, int mtu) {
        ByteBuffer putShort = ra.put((byte) 5).put((byte) 1).putShort(asShort(0));
        int i = 1280;
        if (mtu >= 1280) {
            i = mtu;
        }
        putShort.putInt(i);
    }

    private static void putPio(ByteBuffer ra, IpPrefix ipp, int validTime, int preferredTime) {
        int prefixLength = ipp.getPrefixLength();
        if (prefixLength == 64) {
            if (validTime < 0) {
                validTime = 0;
            }
            if (preferredTime < 0) {
                preferredTime = 0;
            }
            if (preferredTime > validTime) {
                preferredTime = validTime;
            }
            ra.put((byte) 3).put((byte) 4).put(asByte(prefixLength)).put(asByte(192)).putInt(validTime).putInt(preferredTime).putInt(0).put(ipp.getAddress().getAddress());
        }
    }

    private static void putRio(ByteBuffer ra, IpPrefix ipp) {
        int prefixLength = ipp.getPrefixLength();
        if (prefixLength <= 64) {
            int i = 8;
            byte RIO_NUM_8OCTETS = asByte(prefixLength == 0 ? 1 : prefixLength <= 8 ? 2 : 3);
            byte[] addr = ipp.getAddress().getAddress();
            ra.put((byte) 24).put(RIO_NUM_8OCTETS).put(asByte(prefixLength)).put(asByte(24)).putInt(DEFAULT_LIFETIME);
            if (prefixLength > 0) {
                if (prefixLength > 64) {
                    i = 16;
                }
                ra.put(addr, 0, i);
            }
        }
    }

    private static void putRdnss(ByteBuffer ra, Set<Inet6Address> dnses, int lifetime) {
        HashSet<Inet6Address> filteredDnses = new HashSet<>();
        for (Inet6Address dns : dnses) {
            if (new LinkAddress(dns, 64).isGlobalPreferred()) {
                filteredDnses.add(dns);
            }
        }
        if (!filteredDnses.isEmpty()) {
            ra.put((byte) 25).put(asByte((dnses.size() * 2) + 1)).putShort(asShort(0)).putInt(lifetime);
            Iterator<Inet6Address> it = filteredDnses.iterator();
            while (it.hasNext()) {
                ra.put(it.next().getAddress());
            }
        }
    }

    private boolean createSocket() {
        int oldTag = TrafficStats.getAndSetThreadStatsTag(-510);
        try {
            this.mSocket = Os.socket(OsConstants.AF_INET6, OsConstants.SOCK_RAW, OsConstants.IPPROTO_ICMPV6);
            Os.setsockoptTimeval(this.mSocket, OsConstants.SOL_SOCKET, OsConstants.SO_SNDTIMEO, StructTimeval.fromMillis(300));
            Os.setsockoptIfreq(this.mSocket, OsConstants.SOL_SOCKET, OsConstants.SO_BINDTODEVICE, this.mInterface.name);
            NetworkUtils.protectFromVpn(this.mSocket);
            NetworkUtils.setupRaSocket(this.mSocket, this.mInterface.index);
            TrafficStats.setThreadStatsTag(oldTag);
            return true;
        } catch (ErrnoException | IOException e) {
            String str = TAG;
            Log.e(str, "Failed to create RA daemon socket: " + e);
            TrafficStats.setThreadStatsTag(oldTag);
            return false;
        } catch (Throwable th) {
            TrafficStats.setThreadStatsTag(oldTag);
            throw th;
        }
    }

    private void closeSocket() {
        if (this.mSocket != null) {
            try {
                IoBridge.closeAndSignalBlockedThreads(this.mSocket);
            } catch (IOException e) {
            }
        }
        this.mSocket = null;
    }

    /* access modifiers changed from: private */
    public boolean isSocketValid() {
        FileDescriptor s = this.mSocket;
        return s != null && s.valid();
    }

    private boolean isSuitableDestination(InetSocketAddress dest) {
        if (this.mAllNodes.equals(dest)) {
            return true;
        }
        InetAddress destip = dest.getAddress();
        if (!(destip instanceof Inet6Address) || !destip.isLinkLocalAddress() || ((Inet6Address) destip).getScopeId() != this.mInterface.index) {
            return false;
        }
        return true;
    }

    /* Debug info: failed to restart local var, previous not found, register: 7 */
    /* access modifiers changed from: private */
    public void maybeSendRA(InetSocketAddress dest) {
        if (dest == null || !isSuitableDestination(dest)) {
            dest = this.mAllNodes;
        }
        try {
            synchronized (this.mLock) {
                if (this.mRaLength >= 16) {
                    Os.sendto(this.mSocket, this.mRA, 0, this.mRaLength, 0, dest);
                    String str = TAG;
                    Log.d(str, "RA sendto " + dest.getAddress().getHostAddress());
                }
            }
        } catch (ErrnoException | SocketException e) {
            if (isSocketValid()) {
                String str2 = TAG;
                Log.e(str2, "sendto error: " + e);
            }
        }
    }

    private final class UnicastResponder extends Thread {
        private final byte[] mSolication;
        private final InetSocketAddress solicitor;

        private UnicastResponder() {
            this.solicitor = new InetSocketAddress();
            this.mSolication = new byte[1280];
        }

        public void run() {
            while (RouterAdvertisementDaemon.this.isSocketValid()) {
                try {
                    if (Os.recvfrom(RouterAdvertisementDaemon.this.mSocket, this.mSolication, 0, this.mSolication.length, 0, this.solicitor) >= 1 && this.mSolication[0] == RouterAdvertisementDaemon.ICMPV6_ND_ROUTER_SOLICIT) {
                        RouterAdvertisementDaemon.this.maybeSendRA(this.solicitor);
                    }
                } catch (ErrnoException | SocketException e) {
                    if (RouterAdvertisementDaemon.this.isSocketValid()) {
                        String access$600 = RouterAdvertisementDaemon.TAG;
                        Log.e(access$600, "recvfrom error: " + e);
                    }
                }
            }
        }
    }

    private final class MulticastTransmitter extends Thread {
        private final Random mRandom;
        private final AtomicInteger mUrgentAnnouncements;

        private MulticastTransmitter() {
            this.mRandom = new Random();
            this.mUrgentAnnouncements = new AtomicInteger(0);
        }

        public void run() {
            while (RouterAdvertisementDaemon.this.isSocketValid()) {
                try {
                    Thread.sleep(getNextMulticastTransmitDelayMs());
                } catch (InterruptedException e) {
                }
                RouterAdvertisementDaemon routerAdvertisementDaemon = RouterAdvertisementDaemon.this;
                routerAdvertisementDaemon.maybeSendRA(routerAdvertisementDaemon.mAllNodes);
                synchronized (RouterAdvertisementDaemon.this.mLock) {
                    if (RouterAdvertisementDaemon.this.mDeprecatedInfoTracker.decrementCounters()) {
                        RouterAdvertisementDaemon.this.assembleRaLocked();
                    }
                }
            }
        }

        public void hup() {
            this.mUrgentAnnouncements.set(4);
            interrupt();
        }

        /* JADX WARNING: Code restructure failed: missing block: B:15:0x002e, code lost:
            if (r4.mUrgentAnnouncements.getAndDecrement() > 0) goto L_?;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:16:0x0030, code lost:
            if (r0 == false) goto L_0x0033;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:18:0x003c, code lost:
            return r4.mRandom.nextInt(300) + 300;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:26:?, code lost:
            return 3;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:27:?, code lost:
            return 3;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        private int getNextMulticastTransmitDelaySec() {
            /*
                r4 = this;
                r0 = 0
                android.net.ip.RouterAdvertisementDaemon r1 = android.net.ip.RouterAdvertisementDaemon.this
                java.lang.Object r1 = r1.mLock
                monitor-enter(r1)
                android.net.ip.RouterAdvertisementDaemon r2 = android.net.ip.RouterAdvertisementDaemon.this     // Catch:{ all -> 0x003f }
                int r2 = r2.mRaLength     // Catch:{ all -> 0x003f }
                r3 = 16
                if (r2 >= r3) goto L_0x0017
                r2 = 86400(0x15180, float:1.21072E-40)
                monitor-exit(r1)     // Catch:{ all -> 0x003f }
                return r2
            L_0x0017:
                android.net.ip.RouterAdvertisementDaemon r2 = android.net.ip.RouterAdvertisementDaemon.this     // Catch:{ all -> 0x003f }
                android.net.ip.RouterAdvertisementDaemon$DeprecatedInfoTracker r2 = r2.mDeprecatedInfoTracker     // Catch:{ all -> 0x003f }
                boolean r2 = r2.isEmpty()     // Catch:{ all -> 0x003f }
                if (r2 != 0) goto L_0x0025
                r2 = 1
                goto L_0x0026
            L_0x0025:
                r2 = 0
            L_0x0026:
                r0 = r2
                monitor-exit(r1)     // Catch:{ all -> 0x003f }
                java.util.concurrent.atomic.AtomicInteger r1 = r4.mUrgentAnnouncements
                int r1 = r1.getAndDecrement()
                if (r1 > 0) goto L_0x003d
                if (r0 == 0) goto L_0x0033
                goto L_0x003d
            L_0x0033:
                java.util.Random r2 = r4.mRandom
                r3 = 300(0x12c, float:4.2E-43)
                int r2 = r2.nextInt(r3)
                int r2 = r2 + r3
                return r2
            L_0x003d:
                r2 = 3
                return r2
            L_0x003f:
                r2 = move-exception
                monitor-exit(r1)     // Catch:{ all -> 0x003f }
                throw r2
            */
            throw new UnsupportedOperationException("Method not decompiled: android.net.ip.RouterAdvertisementDaemon.MulticastTransmitter.getNextMulticastTransmitDelaySec():int");
        }

        private long getNextMulticastTransmitDelayMs() {
            return ((long) getNextMulticastTransmitDelaySec()) * 1000;
        }
    }
}
