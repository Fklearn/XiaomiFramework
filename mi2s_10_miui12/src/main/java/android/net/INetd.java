package android.net;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.ParcelFileDescriptor;
import android.os.RemoteException;

public interface INetd extends IInterface {
    public static final int CONF = 1;
    public static final int FIREWALL_BLACKLIST = 1;
    public static final int FIREWALL_CHAIN_DOZABLE = 1;
    public static final int FIREWALL_CHAIN_NONE = 0;
    public static final int FIREWALL_CHAIN_POWERSAVE = 3;
    public static final int FIREWALL_CHAIN_STANDBY = 2;
    public static final int FIREWALL_RULE_ALLOW = 1;
    public static final int FIREWALL_RULE_DENY = 2;
    public static final int FIREWALL_WHITELIST = 0;
    public static final String IF_FLAG_BROADCAST = "broadcast";
    public static final String IF_FLAG_LOOPBACK = "loopback";
    public static final String IF_FLAG_MULTICAST = "multicast";
    public static final String IF_FLAG_POINTOPOINT = "point-to-point";
    public static final String IF_FLAG_RUNNING = "running";
    public static final String IF_STATE_DOWN = "down";
    public static final String IF_STATE_UP = "up";
    public static final String IPSEC_INTERFACE_PREFIX = "ipsec";
    public static final int IPV4 = 4;
    public static final int IPV6 = 6;
    public static final int IPV6_ADDR_GEN_MODE_DEFAULT = 0;
    public static final int IPV6_ADDR_GEN_MODE_EUI64 = 0;
    public static final int IPV6_ADDR_GEN_MODE_NONE = 1;
    public static final int IPV6_ADDR_GEN_MODE_RANDOM = 3;
    public static final int IPV6_ADDR_GEN_MODE_STABLE_PRIVACY = 2;
    public static final int LOCAL_NET_ID = 99;
    public static final int NEIGH = 2;
    public static final String NEXTHOP_NONE = "";
    public static final String NEXTHOP_THROW = "throw";
    public static final String NEXTHOP_UNREACHABLE = "unreachable";
    public static final int NO_PERMISSIONS = 0;
    public static final int PENALTY_POLICY_ACCEPT = 1;
    public static final int PENALTY_POLICY_LOG = 2;
    public static final int PENALTY_POLICY_REJECT = 3;
    public static final int PERMISSION_INTERNET = 4;
    public static final int PERMISSION_NETWORK = 1;
    public static final int PERMISSION_NONE = 0;
    public static final int PERMISSION_SYSTEM = 2;
    public static final int PERMISSION_UNINSTALLED = -1;
    public static final int PERMISSION_UPDATE_DEVICE_STATS = 8;
    public static final int VERSION = 2;

    void bandwidthAddNaughtyApp(int i) throws RemoteException;

    void bandwidthAddNiceApp(int i) throws RemoteException;

    boolean bandwidthEnableDataSaver(boolean z) throws RemoteException;

    void bandwidthRemoveInterfaceAlert(String str) throws RemoteException;

    void bandwidthRemoveInterfaceQuota(String str) throws RemoteException;

    void bandwidthRemoveNaughtyApp(int i) throws RemoteException;

    void bandwidthRemoveNiceApp(int i) throws RemoteException;

    void bandwidthSetGlobalAlert(long j) throws RemoteException;

    void bandwidthSetInterfaceAlert(String str, long j) throws RemoteException;

    void bandwidthSetInterfaceQuota(String str, long j) throws RemoteException;

    String clatdStart(String str, String str2) throws RemoteException;

    void clatdStop(String str) throws RemoteException;

    void firewallAddUidInterfaceRules(String str, int[] iArr) throws RemoteException;

    void firewallEnableChildChain(int i, boolean z) throws RemoteException;

    void firewallRemoveUidInterfaceRules(int[] iArr) throws RemoteException;

    boolean firewallReplaceUidChain(String str, boolean z, int[] iArr) throws RemoteException;

    void firewallSetFirewallType(int i) throws RemoteException;

    void firewallSetInterfaceRule(String str, int i) throws RemoteException;

    void firewallSetUidRule(int i, int i2, int i3) throws RemoteException;

    int getInterfaceVersion() throws RemoteException;

    IBinder getOemNetd() throws RemoteException;

    String getProcSysNet(int i, int i2, String str, String str2) throws RemoteException;

    void idletimerAddInterface(String str, int i, String str2) throws RemoteException;

    void idletimerRemoveInterface(String str, int i, String str2) throws RemoteException;

    void interfaceAddAddress(String str, String str2, int i) throws RemoteException;

    void interfaceClearAddrs(String str) throws RemoteException;

    void interfaceDelAddress(String str, String str2, int i) throws RemoteException;

    InterfaceConfigurationParcel interfaceGetCfg(String str) throws RemoteException;

    String[] interfaceGetList() throws RemoteException;

    void interfaceSetCfg(InterfaceConfigurationParcel interfaceConfigurationParcel) throws RemoteException;

    void interfaceSetEnableIPv6(String str, boolean z) throws RemoteException;

    void interfaceSetIPv6PrivacyExtensions(String str, boolean z) throws RemoteException;

    void interfaceSetMtu(String str, int i) throws RemoteException;

    void ipSecAddSecurityAssociation(int i, int i2, String str, String str2, int i3, int i4, int i5, int i6, String str3, byte[] bArr, int i7, String str4, byte[] bArr2, int i8, String str5, byte[] bArr3, int i9, int i10, int i11, int i12, int i13) throws RemoteException;

    void ipSecAddSecurityPolicy(int i, int i2, int i3, String str, String str2, int i4, int i5, int i6, int i7) throws RemoteException;

    void ipSecAddTunnelInterface(String str, String str2, String str3, int i, int i2, int i3) throws RemoteException;

    int ipSecAllocateSpi(int i, String str, String str2, int i2) throws RemoteException;

    void ipSecApplyTransportModeTransform(ParcelFileDescriptor parcelFileDescriptor, int i, int i2, String str, String str2, int i3) throws RemoteException;

    void ipSecDeleteSecurityAssociation(int i, String str, String str2, int i2, int i3, int i4, int i5) throws RemoteException;

    void ipSecDeleteSecurityPolicy(int i, int i2, int i3, int i4, int i5, int i6) throws RemoteException;

    void ipSecRemoveTransportModeTransform(ParcelFileDescriptor parcelFileDescriptor) throws RemoteException;

    void ipSecRemoveTunnelInterface(String str) throws RemoteException;

    void ipSecSetEncapSocketOwner(ParcelFileDescriptor parcelFileDescriptor, int i) throws RemoteException;

    void ipSecUpdateSecurityPolicy(int i, int i2, int i3, String str, String str2, int i4, int i5, int i6, int i7) throws RemoteException;

    void ipSecUpdateTunnelInterface(String str, String str2, String str3, int i, int i2, int i3) throws RemoteException;

    void ipfwdAddInterfaceForward(String str, String str2) throws RemoteException;

    void ipfwdDisableForwarding(String str) throws RemoteException;

    void ipfwdEnableForwarding(String str) throws RemoteException;

    boolean ipfwdEnabled() throws RemoteException;

    String[] ipfwdGetRequesterList() throws RemoteException;

    void ipfwdRemoveInterfaceForward(String str, String str2) throws RemoteException;

    boolean isAlive() throws RemoteException;

    void networkAddInterface(int i, String str) throws RemoteException;

    void networkAddLegacyRoute(int i, String str, String str2, String str3, int i2) throws RemoteException;

    void networkAddRoute(int i, String str, String str2, String str3) throws RemoteException;

    void networkAddUidRanges(int i, UidRangeParcel[] uidRangeParcelArr) throws RemoteException;

    boolean networkCanProtect(int i) throws RemoteException;

    void networkClearDefault() throws RemoteException;

    void networkClearPermissionForUser(int[] iArr) throws RemoteException;

    void networkCreatePhysical(int i, int i2) throws RemoteException;

    void networkCreateVpn(int i, boolean z) throws RemoteException;

    void networkDestroy(int i) throws RemoteException;

    int networkGetDefault() throws RemoteException;

    void networkRejectNonSecureVpn(boolean z, UidRangeParcel[] uidRangeParcelArr) throws RemoteException;

    void networkRemoveInterface(int i, String str) throws RemoteException;

    void networkRemoveLegacyRoute(int i, String str, String str2, String str3, int i2) throws RemoteException;

    void networkRemoveRoute(int i, String str, String str2, String str3) throws RemoteException;

    void networkRemoveUidRanges(int i, UidRangeParcel[] uidRangeParcelArr) throws RemoteException;

    void networkSetDefault(int i) throws RemoteException;

    void networkSetPermissionForNetwork(int i, int i2) throws RemoteException;

    void networkSetPermissionForUser(int i, int[] iArr) throws RemoteException;

    void networkSetProtectAllow(int i) throws RemoteException;

    void networkSetProtectDeny(int i) throws RemoteException;

    void registerUnsolicitedEventListener(INetdUnsolicitedEventListener iNetdUnsolicitedEventListener) throws RemoteException;

    void setIPv6AddrGenMode(String str, int i) throws RemoteException;

    void setProcSysNet(int i, int i2, String str, String str2, String str3) throws RemoteException;

    void setTcpRWmemorySize(String str, String str2) throws RemoteException;

    void socketDestroy(UidRangeParcel[] uidRangeParcelArr, int[] iArr) throws RemoteException;

    void strictUidCleartextPenalty(int i, int i2) throws RemoteException;

    void tetherAddForward(String str, String str2) throws RemoteException;

    boolean tetherApplyDnsInterfaces() throws RemoteException;

    String[] tetherDnsList() throws RemoteException;

    void tetherDnsSet(int i, String[] strArr) throws RemoteException;

    TetherStatsParcel[] tetherGetStats() throws RemoteException;

    void tetherInterfaceAdd(String str) throws RemoteException;

    String[] tetherInterfaceList() throws RemoteException;

    void tetherInterfaceRemove(String str) throws RemoteException;

    boolean tetherIsEnabled() throws RemoteException;

    void tetherRemoveForward(String str, String str2) throws RemoteException;

    void tetherStart(String[] strArr) throws RemoteException;

    void tetherStop() throws RemoteException;

    void trafficSetNetPermForUids(int i, int[] iArr) throws RemoteException;

    void trafficSwapActiveStatsMap() throws RemoteException;

    void wakeupAddInterface(String str, String str2, int i, int i2) throws RemoteException;

    void wakeupDelInterface(String str, String str2, int i, int i2) throws RemoteException;

    public static class Default implements INetd {
        public boolean isAlive() throws RemoteException {
            return false;
        }

        public boolean firewallReplaceUidChain(String chainName, boolean isWhitelist, int[] uids) throws RemoteException {
            return false;
        }

        public boolean bandwidthEnableDataSaver(boolean enable) throws RemoteException {
            return false;
        }

        public void networkCreatePhysical(int netId, int permission) throws RemoteException {
        }

        public void networkCreateVpn(int netId, boolean secure) throws RemoteException {
        }

        public void networkDestroy(int netId) throws RemoteException {
        }

        public void networkAddInterface(int netId, String iface) throws RemoteException {
        }

        public void networkRemoveInterface(int netId, String iface) throws RemoteException {
        }

        public void networkAddUidRanges(int netId, UidRangeParcel[] uidRanges) throws RemoteException {
        }

        public void networkRemoveUidRanges(int netId, UidRangeParcel[] uidRanges) throws RemoteException {
        }

        public void networkRejectNonSecureVpn(boolean add, UidRangeParcel[] uidRanges) throws RemoteException {
        }

        public void socketDestroy(UidRangeParcel[] uidRanges, int[] exemptUids) throws RemoteException {
        }

        public boolean tetherApplyDnsInterfaces() throws RemoteException {
            return false;
        }

        public TetherStatsParcel[] tetherGetStats() throws RemoteException {
            return null;
        }

        public void interfaceAddAddress(String ifName, String addrString, int prefixLength) throws RemoteException {
        }

        public void interfaceDelAddress(String ifName, String addrString, int prefixLength) throws RemoteException {
        }

        public String getProcSysNet(int ipversion, int which, String ifname, String parameter) throws RemoteException {
            return null;
        }

        public void setProcSysNet(int ipversion, int which, String ifname, String parameter, String value) throws RemoteException {
        }

        public void ipSecSetEncapSocketOwner(ParcelFileDescriptor socket, int newUid) throws RemoteException {
        }

        public int ipSecAllocateSpi(int transformId, String sourceAddress, String destinationAddress, int spi) throws RemoteException {
            return 0;
        }

        public void ipSecAddSecurityAssociation(int transformId, int mode, String sourceAddress, String destinationAddress, int underlyingNetId, int spi, int markValue, int markMask, String authAlgo, byte[] authKey, int authTruncBits, String cryptAlgo, byte[] cryptKey, int cryptTruncBits, String aeadAlgo, byte[] aeadKey, int aeadIcvBits, int encapType, int encapLocalPort, int encapRemotePort, int interfaceId) throws RemoteException {
        }

        public void ipSecDeleteSecurityAssociation(int transformId, String sourceAddress, String destinationAddress, int spi, int markValue, int markMask, int interfaceId) throws RemoteException {
        }

        public void ipSecApplyTransportModeTransform(ParcelFileDescriptor socket, int transformId, int direction, String sourceAddress, String destinationAddress, int spi) throws RemoteException {
        }

        public void ipSecRemoveTransportModeTransform(ParcelFileDescriptor socket) throws RemoteException {
        }

        public void ipSecAddSecurityPolicy(int transformId, int selAddrFamily, int direction, String tmplSrcAddress, String tmplDstAddress, int spi, int markValue, int markMask, int interfaceId) throws RemoteException {
        }

        public void ipSecUpdateSecurityPolicy(int transformId, int selAddrFamily, int direction, String tmplSrcAddress, String tmplDstAddress, int spi, int markValue, int markMask, int interfaceId) throws RemoteException {
        }

        public void ipSecDeleteSecurityPolicy(int transformId, int selAddrFamily, int direction, int markValue, int markMask, int interfaceId) throws RemoteException {
        }

        public void ipSecAddTunnelInterface(String deviceName, String localAddress, String remoteAddress, int iKey, int oKey, int interfaceId) throws RemoteException {
        }

        public void ipSecUpdateTunnelInterface(String deviceName, String localAddress, String remoteAddress, int iKey, int oKey, int interfaceId) throws RemoteException {
        }

        public void ipSecRemoveTunnelInterface(String deviceName) throws RemoteException {
        }

        public void wakeupAddInterface(String ifName, String prefix, int mark, int mask) throws RemoteException {
        }

        public void wakeupDelInterface(String ifName, String prefix, int mark, int mask) throws RemoteException {
        }

        public void setIPv6AddrGenMode(String ifName, int mode) throws RemoteException {
        }

        public void idletimerAddInterface(String ifName, int timeout, String classLabel) throws RemoteException {
        }

        public void idletimerRemoveInterface(String ifName, int timeout, String classLabel) throws RemoteException {
        }

        public void strictUidCleartextPenalty(int uid, int policyPenalty) throws RemoteException {
        }

        public String clatdStart(String ifName, String nat64Prefix) throws RemoteException {
            return null;
        }

        public void clatdStop(String ifName) throws RemoteException {
        }

        public boolean ipfwdEnabled() throws RemoteException {
            return false;
        }

        public String[] ipfwdGetRequesterList() throws RemoteException {
            return null;
        }

        public void ipfwdEnableForwarding(String requester) throws RemoteException {
        }

        public void ipfwdDisableForwarding(String requester) throws RemoteException {
        }

        public void ipfwdAddInterfaceForward(String fromIface, String toIface) throws RemoteException {
        }

        public void ipfwdRemoveInterfaceForward(String fromIface, String toIface) throws RemoteException {
        }

        public void bandwidthSetInterfaceQuota(String ifName, long bytes) throws RemoteException {
        }

        public void bandwidthRemoveInterfaceQuota(String ifName) throws RemoteException {
        }

        public void bandwidthSetInterfaceAlert(String ifName, long bytes) throws RemoteException {
        }

        public void bandwidthRemoveInterfaceAlert(String ifName) throws RemoteException {
        }

        public void bandwidthSetGlobalAlert(long bytes) throws RemoteException {
        }

        public void bandwidthAddNaughtyApp(int uid) throws RemoteException {
        }

        public void bandwidthRemoveNaughtyApp(int uid) throws RemoteException {
        }

        public void bandwidthAddNiceApp(int uid) throws RemoteException {
        }

        public void bandwidthRemoveNiceApp(int uid) throws RemoteException {
        }

        public void tetherStart(String[] dhcpRanges) throws RemoteException {
        }

        public void tetherStop() throws RemoteException {
        }

        public boolean tetherIsEnabled() throws RemoteException {
            return false;
        }

        public void tetherInterfaceAdd(String ifName) throws RemoteException {
        }

        public void tetherInterfaceRemove(String ifName) throws RemoteException {
        }

        public String[] tetherInterfaceList() throws RemoteException {
            return null;
        }

        public void tetherDnsSet(int netId, String[] dnsAddrs) throws RemoteException {
        }

        public String[] tetherDnsList() throws RemoteException {
            return null;
        }

        public void networkAddRoute(int netId, String ifName, String destination, String nextHop) throws RemoteException {
        }

        public void networkRemoveRoute(int netId, String ifName, String destination, String nextHop) throws RemoteException {
        }

        public void networkAddLegacyRoute(int netId, String ifName, String destination, String nextHop, int uid) throws RemoteException {
        }

        public void networkRemoveLegacyRoute(int netId, String ifName, String destination, String nextHop, int uid) throws RemoteException {
        }

        public int networkGetDefault() throws RemoteException {
            return 0;
        }

        public void networkSetDefault(int netId) throws RemoteException {
        }

        public void networkClearDefault() throws RemoteException {
        }

        public void networkSetPermissionForNetwork(int netId, int permission) throws RemoteException {
        }

        public void networkSetPermissionForUser(int permission, int[] uids) throws RemoteException {
        }

        public void networkClearPermissionForUser(int[] uids) throws RemoteException {
        }

        public void trafficSetNetPermForUids(int permission, int[] uids) throws RemoteException {
        }

        public void networkSetProtectAllow(int uid) throws RemoteException {
        }

        public void networkSetProtectDeny(int uid) throws RemoteException {
        }

        public boolean networkCanProtect(int uid) throws RemoteException {
            return false;
        }

        public void firewallSetFirewallType(int firewalltype) throws RemoteException {
        }

        public void firewallSetInterfaceRule(String ifName, int firewallRule) throws RemoteException {
        }

        public void firewallSetUidRule(int childChain, int uid, int firewallRule) throws RemoteException {
        }

        public void firewallEnableChildChain(int childChain, boolean enable) throws RemoteException {
        }

        public String[] interfaceGetList() throws RemoteException {
            return null;
        }

        public InterfaceConfigurationParcel interfaceGetCfg(String ifName) throws RemoteException {
            return null;
        }

        public void interfaceSetCfg(InterfaceConfigurationParcel cfg) throws RemoteException {
        }

        public void interfaceSetIPv6PrivacyExtensions(String ifName, boolean enable) throws RemoteException {
        }

        public void interfaceClearAddrs(String ifName) throws RemoteException {
        }

        public void interfaceSetEnableIPv6(String ifName, boolean enable) throws RemoteException {
        }

        public void interfaceSetMtu(String ifName, int mtu) throws RemoteException {
        }

        public void tetherAddForward(String intIface, String extIface) throws RemoteException {
        }

        public void tetherRemoveForward(String intIface, String extIface) throws RemoteException {
        }

        public void setTcpRWmemorySize(String rmemValues, String wmemValues) throws RemoteException {
        }

        public void registerUnsolicitedEventListener(INetdUnsolicitedEventListener listener) throws RemoteException {
        }

        public void firewallAddUidInterfaceRules(String ifName, int[] uids) throws RemoteException {
        }

        public void firewallRemoveUidInterfaceRules(int[] uids) throws RemoteException {
        }

        public void trafficSwapActiveStatsMap() throws RemoteException {
        }

        public IBinder getOemNetd() throws RemoteException {
            return null;
        }

        public int getInterfaceVersion() {
            return -1;
        }

        public IBinder asBinder() {
            return null;
        }
    }

    public static abstract class Stub extends Binder implements INetd {
        private static final String DESCRIPTOR = "android.net.INetd";
        static final int TRANSACTION_bandwidthAddNaughtyApp = 50;
        static final int TRANSACTION_bandwidthAddNiceApp = 52;
        static final int TRANSACTION_bandwidthEnableDataSaver = 3;
        static final int TRANSACTION_bandwidthRemoveInterfaceAlert = 48;
        static final int TRANSACTION_bandwidthRemoveInterfaceQuota = 46;
        static final int TRANSACTION_bandwidthRemoveNaughtyApp = 51;
        static final int TRANSACTION_bandwidthRemoveNiceApp = 53;
        static final int TRANSACTION_bandwidthSetGlobalAlert = 49;
        static final int TRANSACTION_bandwidthSetInterfaceAlert = 47;
        static final int TRANSACTION_bandwidthSetInterfaceQuota = 45;
        static final int TRANSACTION_clatdStart = 37;
        static final int TRANSACTION_clatdStop = 38;
        static final int TRANSACTION_firewallAddUidInterfaceRules = 91;
        static final int TRANSACTION_firewallEnableChildChain = 79;
        static final int TRANSACTION_firewallRemoveUidInterfaceRules = 92;
        static final int TRANSACTION_firewallReplaceUidChain = 2;
        static final int TRANSACTION_firewallSetFirewallType = 76;
        static final int TRANSACTION_firewallSetInterfaceRule = 77;
        static final int TRANSACTION_firewallSetUidRule = 78;
        static final int TRANSACTION_getInterfaceVersion = 16777215;
        static final int TRANSACTION_getOemNetd = 94;
        static final int TRANSACTION_getProcSysNet = 17;
        static final int TRANSACTION_idletimerAddInterface = 34;
        static final int TRANSACTION_idletimerRemoveInterface = 35;
        static final int TRANSACTION_interfaceAddAddress = 15;
        static final int TRANSACTION_interfaceClearAddrs = 84;
        static final int TRANSACTION_interfaceDelAddress = 16;
        static final int TRANSACTION_interfaceGetCfg = 81;
        static final int TRANSACTION_interfaceGetList = 80;
        static final int TRANSACTION_interfaceSetCfg = 82;
        static final int TRANSACTION_interfaceSetEnableIPv6 = 85;
        static final int TRANSACTION_interfaceSetIPv6PrivacyExtensions = 83;
        static final int TRANSACTION_interfaceSetMtu = 86;
        static final int TRANSACTION_ipSecAddSecurityAssociation = 21;
        static final int TRANSACTION_ipSecAddSecurityPolicy = 25;
        static final int TRANSACTION_ipSecAddTunnelInterface = 28;
        static final int TRANSACTION_ipSecAllocateSpi = 20;
        static final int TRANSACTION_ipSecApplyTransportModeTransform = 23;
        static final int TRANSACTION_ipSecDeleteSecurityAssociation = 22;
        static final int TRANSACTION_ipSecDeleteSecurityPolicy = 27;
        static final int TRANSACTION_ipSecRemoveTransportModeTransform = 24;
        static final int TRANSACTION_ipSecRemoveTunnelInterface = 30;
        static final int TRANSACTION_ipSecSetEncapSocketOwner = 19;
        static final int TRANSACTION_ipSecUpdateSecurityPolicy = 26;
        static final int TRANSACTION_ipSecUpdateTunnelInterface = 29;
        static final int TRANSACTION_ipfwdAddInterfaceForward = 43;
        static final int TRANSACTION_ipfwdDisableForwarding = 42;
        static final int TRANSACTION_ipfwdEnableForwarding = 41;
        static final int TRANSACTION_ipfwdEnabled = 39;
        static final int TRANSACTION_ipfwdGetRequesterList = 40;
        static final int TRANSACTION_ipfwdRemoveInterfaceForward = 44;
        static final int TRANSACTION_isAlive = 1;
        static final int TRANSACTION_networkAddInterface = 7;
        static final int TRANSACTION_networkAddLegacyRoute = 64;
        static final int TRANSACTION_networkAddRoute = 62;
        static final int TRANSACTION_networkAddUidRanges = 9;
        static final int TRANSACTION_networkCanProtect = 75;
        static final int TRANSACTION_networkClearDefault = 68;
        static final int TRANSACTION_networkClearPermissionForUser = 71;
        static final int TRANSACTION_networkCreatePhysical = 4;
        static final int TRANSACTION_networkCreateVpn = 5;
        static final int TRANSACTION_networkDestroy = 6;
        static final int TRANSACTION_networkGetDefault = 66;
        static final int TRANSACTION_networkRejectNonSecureVpn = 11;
        static final int TRANSACTION_networkRemoveInterface = 8;
        static final int TRANSACTION_networkRemoveLegacyRoute = 65;
        static final int TRANSACTION_networkRemoveRoute = 63;
        static final int TRANSACTION_networkRemoveUidRanges = 10;
        static final int TRANSACTION_networkSetDefault = 67;
        static final int TRANSACTION_networkSetPermissionForNetwork = 69;
        static final int TRANSACTION_networkSetPermissionForUser = 70;
        static final int TRANSACTION_networkSetProtectAllow = 73;
        static final int TRANSACTION_networkSetProtectDeny = 74;
        static final int TRANSACTION_registerUnsolicitedEventListener = 90;
        static final int TRANSACTION_setIPv6AddrGenMode = 33;
        static final int TRANSACTION_setProcSysNet = 18;
        static final int TRANSACTION_setTcpRWmemorySize = 89;
        static final int TRANSACTION_socketDestroy = 12;
        static final int TRANSACTION_strictUidCleartextPenalty = 36;
        static final int TRANSACTION_tetherAddForward = 87;
        static final int TRANSACTION_tetherApplyDnsInterfaces = 13;
        static final int TRANSACTION_tetherDnsList = 61;
        static final int TRANSACTION_tetherDnsSet = 60;
        static final int TRANSACTION_tetherGetStats = 14;
        static final int TRANSACTION_tetherInterfaceAdd = 57;
        static final int TRANSACTION_tetherInterfaceList = 59;
        static final int TRANSACTION_tetherInterfaceRemove = 58;
        static final int TRANSACTION_tetherIsEnabled = 56;
        static final int TRANSACTION_tetherRemoveForward = 88;
        static final int TRANSACTION_tetherStart = 54;
        static final int TRANSACTION_tetherStop = 55;
        static final int TRANSACTION_trafficSetNetPermForUids = 72;
        static final int TRANSACTION_trafficSwapActiveStatsMap = 93;
        static final int TRANSACTION_wakeupAddInterface = 31;
        static final int TRANSACTION_wakeupDelInterface = 32;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static INetd asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin == null || !(iin instanceof INetd)) {
                return new Proxy(obj);
            }
            return (INetd) iin;
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
        public boolean onTransact(int r45, android.os.Parcel r46, android.os.Parcel r47, int r48) throws android.os.RemoteException {
            /*
                r44 = this;
                r15 = r44
                r14 = r45
                r13 = r46
                r12 = r47
                java.lang.String r11 = "android.net.INetd"
                r0 = 16777215(0xffffff, float:2.3509886E-38)
                r10 = 1
                if (r14 == r0) goto L_0x08e4
                r0 = 1598968902(0x5f4e5446, float:1.4867585E19)
                if (r14 == r0) goto L_0x08dd
                r0 = 0
                switch(r14) {
                    case 1: goto L_0x08ca;
                    case 2: goto L_0x08a8;
                    case 3: goto L_0x088e;
                    case 4: goto L_0x0877;
                    case 5: goto L_0x085d;
                    case 6: goto L_0x084a;
                    case 7: goto L_0x0833;
                    case 8: goto L_0x081c;
                    case 9: goto L_0x0801;
                    case 10: goto L_0x07e6;
                    case 11: goto L_0x07c8;
                    case 12: goto L_0x07ad;
                    case 13: goto L_0x079a;
                    case 14: goto L_0x0787;
                    case 15: goto L_0x076c;
                    case 16: goto L_0x0751;
                    case 17: goto L_0x072e;
                    case 18: goto L_0x0704;
                    case 19: goto L_0x06e1;
                    case 20: goto L_0x06b8;
                    case 21: goto L_0x062a;
                    case 22: goto L_0x05f6;
                    case 23: goto L_0x05bb;
                    case 24: goto L_0x05a1;
                    case 25: goto L_0x055f;
                    case 26: goto L_0x051d;
                    case 27: goto L_0x04f0;
                    case 28: goto L_0x04c3;
                    case 29: goto L_0x0496;
                    case 30: goto L_0x0488;
                    case 31: goto L_0x046e;
                    case 32: goto L_0x0454;
                    case 33: goto L_0x0442;
                    case 34: goto L_0x042c;
                    case 35: goto L_0x0416;
                    case 36: goto L_0x0404;
                    case 37: goto L_0x03ee;
                    case 38: goto L_0x03e0;
                    case 39: goto L_0x03d2;
                    case 40: goto L_0x03c4;
                    case 41: goto L_0x03b6;
                    case 42: goto L_0x03a8;
                    case 43: goto L_0x0396;
                    case 44: goto L_0x0384;
                    case 45: goto L_0x0372;
                    case 46: goto L_0x0364;
                    case 47: goto L_0x0352;
                    case 48: goto L_0x0344;
                    case 49: goto L_0x0336;
                    case 50: goto L_0x0328;
                    case 51: goto L_0x031a;
                    case 52: goto L_0x030c;
                    case 53: goto L_0x02fe;
                    case 54: goto L_0x02f0;
                    case 55: goto L_0x02e6;
                    case 56: goto L_0x02d8;
                    case 57: goto L_0x02ca;
                    case 58: goto L_0x02bc;
                    case 59: goto L_0x02ae;
                    case 60: goto L_0x029c;
                    case 61: goto L_0x028e;
                    case 62: goto L_0x0274;
                    case 63: goto L_0x025a;
                    case 64: goto L_0x0234;
                    case 65: goto L_0x020e;
                    case 66: goto L_0x0200;
                    case 67: goto L_0x01f2;
                    case 68: goto L_0x01e8;
                    case 69: goto L_0x01d6;
                    case 70: goto L_0x01c4;
                    case 71: goto L_0x01b6;
                    case 72: goto L_0x01a4;
                    case 73: goto L_0x0196;
                    case 74: goto L_0x0188;
                    case 75: goto L_0x0176;
                    case 76: goto L_0x0168;
                    case 77: goto L_0x0156;
                    case 78: goto L_0x0140;
                    case 79: goto L_0x012b;
                    case 80: goto L_0x011d;
                    case 81: goto L_0x0102;
                    case 82: goto L_0x00e8;
                    case 83: goto L_0x00d3;
                    case 84: goto L_0x00c5;
                    case 85: goto L_0x00b0;
                    case 86: goto L_0x009e;
                    case 87: goto L_0x008c;
                    case 88: goto L_0x007a;
                    case 89: goto L_0x0068;
                    case 90: goto L_0x0056;
                    case 91: goto L_0x0044;
                    case 92: goto L_0x0036;
                    case 93: goto L_0x002c;
                    case 94: goto L_0x001e;
                    default: goto L_0x0019;
                }
            L_0x0019:
                boolean r0 = super.onTransact(r45, r46, r47, r48)
                return r0
            L_0x001e:
                r13.enforceInterface(r11)
                android.os.IBinder r0 = r44.getOemNetd()
                r47.writeNoException()
                r12.writeStrongBinder(r0)
                return r10
            L_0x002c:
                r13.enforceInterface(r11)
                r44.trafficSwapActiveStatsMap()
                r47.writeNoException()
                return r10
            L_0x0036:
                r13.enforceInterface(r11)
                int[] r0 = r46.createIntArray()
                r15.firewallRemoveUidInterfaceRules(r0)
                r47.writeNoException()
                return r10
            L_0x0044:
                r13.enforceInterface(r11)
                java.lang.String r0 = r46.readString()
                int[] r1 = r46.createIntArray()
                r15.firewallAddUidInterfaceRules(r0, r1)
                r47.writeNoException()
                return r10
            L_0x0056:
                r13.enforceInterface(r11)
                android.os.IBinder r0 = r46.readStrongBinder()
                android.net.INetdUnsolicitedEventListener r0 = android.net.INetdUnsolicitedEventListener.Stub.asInterface(r0)
                r15.registerUnsolicitedEventListener(r0)
                r47.writeNoException()
                return r10
            L_0x0068:
                r13.enforceInterface(r11)
                java.lang.String r0 = r46.readString()
                java.lang.String r1 = r46.readString()
                r15.setTcpRWmemorySize(r0, r1)
                r47.writeNoException()
                return r10
            L_0x007a:
                r13.enforceInterface(r11)
                java.lang.String r0 = r46.readString()
                java.lang.String r1 = r46.readString()
                r15.tetherRemoveForward(r0, r1)
                r47.writeNoException()
                return r10
            L_0x008c:
                r13.enforceInterface(r11)
                java.lang.String r0 = r46.readString()
                java.lang.String r1 = r46.readString()
                r15.tetherAddForward(r0, r1)
                r47.writeNoException()
                return r10
            L_0x009e:
                r13.enforceInterface(r11)
                java.lang.String r0 = r46.readString()
                int r1 = r46.readInt()
                r15.interfaceSetMtu(r0, r1)
                r47.writeNoException()
                return r10
            L_0x00b0:
                r13.enforceInterface(r11)
                java.lang.String r1 = r46.readString()
                int r2 = r46.readInt()
                if (r2 == 0) goto L_0x00be
                r0 = r10
            L_0x00be:
                r15.interfaceSetEnableIPv6(r1, r0)
                r47.writeNoException()
                return r10
            L_0x00c5:
                r13.enforceInterface(r11)
                java.lang.String r0 = r46.readString()
                r15.interfaceClearAddrs(r0)
                r47.writeNoException()
                return r10
            L_0x00d3:
                r13.enforceInterface(r11)
                java.lang.String r1 = r46.readString()
                int r2 = r46.readInt()
                if (r2 == 0) goto L_0x00e1
                r0 = r10
            L_0x00e1:
                r15.interfaceSetIPv6PrivacyExtensions(r1, r0)
                r47.writeNoException()
                return r10
            L_0x00e8:
                r13.enforceInterface(r11)
                int r0 = r46.readInt()
                if (r0 == 0) goto L_0x00fa
                android.os.Parcelable$Creator<android.net.InterfaceConfigurationParcel> r0 = android.net.InterfaceConfigurationParcel.CREATOR
                java.lang.Object r0 = r0.createFromParcel(r13)
                android.net.InterfaceConfigurationParcel r0 = (android.net.InterfaceConfigurationParcel) r0
                goto L_0x00fb
            L_0x00fa:
                r0 = 0
            L_0x00fb:
                r15.interfaceSetCfg(r0)
                r47.writeNoException()
                return r10
            L_0x0102:
                r13.enforceInterface(r11)
                java.lang.String r1 = r46.readString()
                android.net.InterfaceConfigurationParcel r2 = r15.interfaceGetCfg(r1)
                r47.writeNoException()
                if (r2 == 0) goto L_0x0119
                r12.writeInt(r10)
                r2.writeToParcel(r12, r10)
                goto L_0x011c
            L_0x0119:
                r12.writeInt(r0)
            L_0x011c:
                return r10
            L_0x011d:
                r13.enforceInterface(r11)
                java.lang.String[] r0 = r44.interfaceGetList()
                r47.writeNoException()
                r12.writeStringArray(r0)
                return r10
            L_0x012b:
                r13.enforceInterface(r11)
                int r1 = r46.readInt()
                int r2 = r46.readInt()
                if (r2 == 0) goto L_0x0139
                r0 = r10
            L_0x0139:
                r15.firewallEnableChildChain(r1, r0)
                r47.writeNoException()
                return r10
            L_0x0140:
                r13.enforceInterface(r11)
                int r0 = r46.readInt()
                int r1 = r46.readInt()
                int r2 = r46.readInt()
                r15.firewallSetUidRule(r0, r1, r2)
                r47.writeNoException()
                return r10
            L_0x0156:
                r13.enforceInterface(r11)
                java.lang.String r0 = r46.readString()
                int r1 = r46.readInt()
                r15.firewallSetInterfaceRule(r0, r1)
                r47.writeNoException()
                return r10
            L_0x0168:
                r13.enforceInterface(r11)
                int r0 = r46.readInt()
                r15.firewallSetFirewallType(r0)
                r47.writeNoException()
                return r10
            L_0x0176:
                r13.enforceInterface(r11)
                int r0 = r46.readInt()
                boolean r1 = r15.networkCanProtect(r0)
                r47.writeNoException()
                r12.writeInt(r1)
                return r10
            L_0x0188:
                r13.enforceInterface(r11)
                int r0 = r46.readInt()
                r15.networkSetProtectDeny(r0)
                r47.writeNoException()
                return r10
            L_0x0196:
                r13.enforceInterface(r11)
                int r0 = r46.readInt()
                r15.networkSetProtectAllow(r0)
                r47.writeNoException()
                return r10
            L_0x01a4:
                r13.enforceInterface(r11)
                int r0 = r46.readInt()
                int[] r1 = r46.createIntArray()
                r15.trafficSetNetPermForUids(r0, r1)
                r47.writeNoException()
                return r10
            L_0x01b6:
                r13.enforceInterface(r11)
                int[] r0 = r46.createIntArray()
                r15.networkClearPermissionForUser(r0)
                r47.writeNoException()
                return r10
            L_0x01c4:
                r13.enforceInterface(r11)
                int r0 = r46.readInt()
                int[] r1 = r46.createIntArray()
                r15.networkSetPermissionForUser(r0, r1)
                r47.writeNoException()
                return r10
            L_0x01d6:
                r13.enforceInterface(r11)
                int r0 = r46.readInt()
                int r1 = r46.readInt()
                r15.networkSetPermissionForNetwork(r0, r1)
                r47.writeNoException()
                return r10
            L_0x01e8:
                r13.enforceInterface(r11)
                r44.networkClearDefault()
                r47.writeNoException()
                return r10
            L_0x01f2:
                r13.enforceInterface(r11)
                int r0 = r46.readInt()
                r15.networkSetDefault(r0)
                r47.writeNoException()
                return r10
            L_0x0200:
                r13.enforceInterface(r11)
                int r0 = r44.networkGetDefault()
                r47.writeNoException()
                r12.writeInt(r0)
                return r10
            L_0x020e:
                r13.enforceInterface(r11)
                int r6 = r46.readInt()
                java.lang.String r7 = r46.readString()
                java.lang.String r8 = r46.readString()
                java.lang.String r9 = r46.readString()
                int r16 = r46.readInt()
                r0 = r44
                r1 = r6
                r2 = r7
                r3 = r8
                r4 = r9
                r5 = r16
                r0.networkRemoveLegacyRoute(r1, r2, r3, r4, r5)
                r47.writeNoException()
                return r10
            L_0x0234:
                r13.enforceInterface(r11)
                int r6 = r46.readInt()
                java.lang.String r7 = r46.readString()
                java.lang.String r8 = r46.readString()
                java.lang.String r9 = r46.readString()
                int r16 = r46.readInt()
                r0 = r44
                r1 = r6
                r2 = r7
                r3 = r8
                r4 = r9
                r5 = r16
                r0.networkAddLegacyRoute(r1, r2, r3, r4, r5)
                r47.writeNoException()
                return r10
            L_0x025a:
                r13.enforceInterface(r11)
                int r0 = r46.readInt()
                java.lang.String r1 = r46.readString()
                java.lang.String r2 = r46.readString()
                java.lang.String r3 = r46.readString()
                r15.networkRemoveRoute(r0, r1, r2, r3)
                r47.writeNoException()
                return r10
            L_0x0274:
                r13.enforceInterface(r11)
                int r0 = r46.readInt()
                java.lang.String r1 = r46.readString()
                java.lang.String r2 = r46.readString()
                java.lang.String r3 = r46.readString()
                r15.networkAddRoute(r0, r1, r2, r3)
                r47.writeNoException()
                return r10
            L_0x028e:
                r13.enforceInterface(r11)
                java.lang.String[] r0 = r44.tetherDnsList()
                r47.writeNoException()
                r12.writeStringArray(r0)
                return r10
            L_0x029c:
                r13.enforceInterface(r11)
                int r0 = r46.readInt()
                java.lang.String[] r1 = r46.createStringArray()
                r15.tetherDnsSet(r0, r1)
                r47.writeNoException()
                return r10
            L_0x02ae:
                r13.enforceInterface(r11)
                java.lang.String[] r0 = r44.tetherInterfaceList()
                r47.writeNoException()
                r12.writeStringArray(r0)
                return r10
            L_0x02bc:
                r13.enforceInterface(r11)
                java.lang.String r0 = r46.readString()
                r15.tetherInterfaceRemove(r0)
                r47.writeNoException()
                return r10
            L_0x02ca:
                r13.enforceInterface(r11)
                java.lang.String r0 = r46.readString()
                r15.tetherInterfaceAdd(r0)
                r47.writeNoException()
                return r10
            L_0x02d8:
                r13.enforceInterface(r11)
                boolean r0 = r44.tetherIsEnabled()
                r47.writeNoException()
                r12.writeInt(r0)
                return r10
            L_0x02e6:
                r13.enforceInterface(r11)
                r44.tetherStop()
                r47.writeNoException()
                return r10
            L_0x02f0:
                r13.enforceInterface(r11)
                java.lang.String[] r0 = r46.createStringArray()
                r15.tetherStart(r0)
                r47.writeNoException()
                return r10
            L_0x02fe:
                r13.enforceInterface(r11)
                int r0 = r46.readInt()
                r15.bandwidthRemoveNiceApp(r0)
                r47.writeNoException()
                return r10
            L_0x030c:
                r13.enforceInterface(r11)
                int r0 = r46.readInt()
                r15.bandwidthAddNiceApp(r0)
                r47.writeNoException()
                return r10
            L_0x031a:
                r13.enforceInterface(r11)
                int r0 = r46.readInt()
                r15.bandwidthRemoveNaughtyApp(r0)
                r47.writeNoException()
                return r10
            L_0x0328:
                r13.enforceInterface(r11)
                int r0 = r46.readInt()
                r15.bandwidthAddNaughtyApp(r0)
                r47.writeNoException()
                return r10
            L_0x0336:
                r13.enforceInterface(r11)
                long r0 = r46.readLong()
                r15.bandwidthSetGlobalAlert(r0)
                r47.writeNoException()
                return r10
            L_0x0344:
                r13.enforceInterface(r11)
                java.lang.String r0 = r46.readString()
                r15.bandwidthRemoveInterfaceAlert(r0)
                r47.writeNoException()
                return r10
            L_0x0352:
                r13.enforceInterface(r11)
                java.lang.String r0 = r46.readString()
                long r1 = r46.readLong()
                r15.bandwidthSetInterfaceAlert(r0, r1)
                r47.writeNoException()
                return r10
            L_0x0364:
                r13.enforceInterface(r11)
                java.lang.String r0 = r46.readString()
                r15.bandwidthRemoveInterfaceQuota(r0)
                r47.writeNoException()
                return r10
            L_0x0372:
                r13.enforceInterface(r11)
                java.lang.String r0 = r46.readString()
                long r1 = r46.readLong()
                r15.bandwidthSetInterfaceQuota(r0, r1)
                r47.writeNoException()
                return r10
            L_0x0384:
                r13.enforceInterface(r11)
                java.lang.String r0 = r46.readString()
                java.lang.String r1 = r46.readString()
                r15.ipfwdRemoveInterfaceForward(r0, r1)
                r47.writeNoException()
                return r10
            L_0x0396:
                r13.enforceInterface(r11)
                java.lang.String r0 = r46.readString()
                java.lang.String r1 = r46.readString()
                r15.ipfwdAddInterfaceForward(r0, r1)
                r47.writeNoException()
                return r10
            L_0x03a8:
                r13.enforceInterface(r11)
                java.lang.String r0 = r46.readString()
                r15.ipfwdDisableForwarding(r0)
                r47.writeNoException()
                return r10
            L_0x03b6:
                r13.enforceInterface(r11)
                java.lang.String r0 = r46.readString()
                r15.ipfwdEnableForwarding(r0)
                r47.writeNoException()
                return r10
            L_0x03c4:
                r13.enforceInterface(r11)
                java.lang.String[] r0 = r44.ipfwdGetRequesterList()
                r47.writeNoException()
                r12.writeStringArray(r0)
                return r10
            L_0x03d2:
                r13.enforceInterface(r11)
                boolean r0 = r44.ipfwdEnabled()
                r47.writeNoException()
                r12.writeInt(r0)
                return r10
            L_0x03e0:
                r13.enforceInterface(r11)
                java.lang.String r0 = r46.readString()
                r15.clatdStop(r0)
                r47.writeNoException()
                return r10
            L_0x03ee:
                r13.enforceInterface(r11)
                java.lang.String r0 = r46.readString()
                java.lang.String r1 = r46.readString()
                java.lang.String r2 = r15.clatdStart(r0, r1)
                r47.writeNoException()
                r12.writeString(r2)
                return r10
            L_0x0404:
                r13.enforceInterface(r11)
                int r0 = r46.readInt()
                int r1 = r46.readInt()
                r15.strictUidCleartextPenalty(r0, r1)
                r47.writeNoException()
                return r10
            L_0x0416:
                r13.enforceInterface(r11)
                java.lang.String r0 = r46.readString()
                int r1 = r46.readInt()
                java.lang.String r2 = r46.readString()
                r15.idletimerRemoveInterface(r0, r1, r2)
                r47.writeNoException()
                return r10
            L_0x042c:
                r13.enforceInterface(r11)
                java.lang.String r0 = r46.readString()
                int r1 = r46.readInt()
                java.lang.String r2 = r46.readString()
                r15.idletimerAddInterface(r0, r1, r2)
                r47.writeNoException()
                return r10
            L_0x0442:
                r13.enforceInterface(r11)
                java.lang.String r0 = r46.readString()
                int r1 = r46.readInt()
                r15.setIPv6AddrGenMode(r0, r1)
                r47.writeNoException()
                return r10
            L_0x0454:
                r13.enforceInterface(r11)
                java.lang.String r0 = r46.readString()
                java.lang.String r1 = r46.readString()
                int r2 = r46.readInt()
                int r3 = r46.readInt()
                r15.wakeupDelInterface(r0, r1, r2, r3)
                r47.writeNoException()
                return r10
            L_0x046e:
                r13.enforceInterface(r11)
                java.lang.String r0 = r46.readString()
                java.lang.String r1 = r46.readString()
                int r2 = r46.readInt()
                int r3 = r46.readInt()
                r15.wakeupAddInterface(r0, r1, r2, r3)
                r47.writeNoException()
                return r10
            L_0x0488:
                r13.enforceInterface(r11)
                java.lang.String r0 = r46.readString()
                r15.ipSecRemoveTunnelInterface(r0)
                r47.writeNoException()
                return r10
            L_0x0496:
                r13.enforceInterface(r11)
                java.lang.String r7 = r46.readString()
                java.lang.String r8 = r46.readString()
                java.lang.String r9 = r46.readString()
                int r16 = r46.readInt()
                int r17 = r46.readInt()
                int r18 = r46.readInt()
                r0 = r44
                r1 = r7
                r2 = r8
                r3 = r9
                r4 = r16
                r5 = r17
                r6 = r18
                r0.ipSecUpdateTunnelInterface(r1, r2, r3, r4, r5, r6)
                r47.writeNoException()
                return r10
            L_0x04c3:
                r13.enforceInterface(r11)
                java.lang.String r7 = r46.readString()
                java.lang.String r8 = r46.readString()
                java.lang.String r9 = r46.readString()
                int r16 = r46.readInt()
                int r17 = r46.readInt()
                int r18 = r46.readInt()
                r0 = r44
                r1 = r7
                r2 = r8
                r3 = r9
                r4 = r16
                r5 = r17
                r6 = r18
                r0.ipSecAddTunnelInterface(r1, r2, r3, r4, r5, r6)
                r47.writeNoException()
                return r10
            L_0x04f0:
                r13.enforceInterface(r11)
                int r7 = r46.readInt()
                int r8 = r46.readInt()
                int r9 = r46.readInt()
                int r16 = r46.readInt()
                int r17 = r46.readInt()
                int r18 = r46.readInt()
                r0 = r44
                r1 = r7
                r2 = r8
                r3 = r9
                r4 = r16
                r5 = r17
                r6 = r18
                r0.ipSecDeleteSecurityPolicy(r1, r2, r3, r4, r5, r6)
                r47.writeNoException()
                return r10
            L_0x051d:
                r13.enforceInterface(r11)
                int r16 = r46.readInt()
                int r17 = r46.readInt()
                int r18 = r46.readInt()
                java.lang.String r19 = r46.readString()
                java.lang.String r20 = r46.readString()
                int r21 = r46.readInt()
                int r22 = r46.readInt()
                int r23 = r46.readInt()
                int r24 = r46.readInt()
                r0 = r44
                r1 = r16
                r2 = r17
                r3 = r18
                r4 = r19
                r5 = r20
                r6 = r21
                r7 = r22
                r8 = r23
                r9 = r24
                r0.ipSecUpdateSecurityPolicy(r1, r2, r3, r4, r5, r6, r7, r8, r9)
                r47.writeNoException()
                return r10
            L_0x055f:
                r13.enforceInterface(r11)
                int r16 = r46.readInt()
                int r17 = r46.readInt()
                int r18 = r46.readInt()
                java.lang.String r19 = r46.readString()
                java.lang.String r20 = r46.readString()
                int r21 = r46.readInt()
                int r22 = r46.readInt()
                int r23 = r46.readInt()
                int r24 = r46.readInt()
                r0 = r44
                r1 = r16
                r2 = r17
                r3 = r18
                r4 = r19
                r5 = r20
                r6 = r21
                r7 = r22
                r8 = r23
                r9 = r24
                r0.ipSecAddSecurityPolicy(r1, r2, r3, r4, r5, r6, r7, r8, r9)
                r47.writeNoException()
                return r10
            L_0x05a1:
                r13.enforceInterface(r11)
                int r0 = r46.readInt()
                if (r0 == 0) goto L_0x05b3
                android.os.Parcelable$Creator r0 = android.os.ParcelFileDescriptor.CREATOR
                java.lang.Object r0 = r0.createFromParcel(r13)
                android.os.ParcelFileDescriptor r0 = (android.os.ParcelFileDescriptor) r0
                goto L_0x05b4
            L_0x05b3:
                r0 = 0
            L_0x05b4:
                r15.ipSecRemoveTransportModeTransform(r0)
                r47.writeNoException()
                return r10
            L_0x05bb:
                r13.enforceInterface(r11)
                int r0 = r46.readInt()
                if (r0 == 0) goto L_0x05ce
                android.os.Parcelable$Creator r0 = android.os.ParcelFileDescriptor.CREATOR
                java.lang.Object r0 = r0.createFromParcel(r13)
                android.os.ParcelFileDescriptor r0 = (android.os.ParcelFileDescriptor) r0
                r7 = r0
                goto L_0x05d0
            L_0x05ce:
                r0 = 0
                r7 = r0
            L_0x05d0:
                int r8 = r46.readInt()
                int r9 = r46.readInt()
                java.lang.String r16 = r46.readString()
                java.lang.String r17 = r46.readString()
                int r18 = r46.readInt()
                r0 = r44
                r1 = r7
                r2 = r8
                r3 = r9
                r4 = r16
                r5 = r17
                r6 = r18
                r0.ipSecApplyTransportModeTransform(r1, r2, r3, r4, r5, r6)
                r47.writeNoException()
                return r10
            L_0x05f6:
                r13.enforceInterface(r11)
                int r8 = r46.readInt()
                java.lang.String r9 = r46.readString()
                java.lang.String r16 = r46.readString()
                int r17 = r46.readInt()
                int r18 = r46.readInt()
                int r19 = r46.readInt()
                int r20 = r46.readInt()
                r0 = r44
                r1 = r8
                r2 = r9
                r3 = r16
                r4 = r17
                r5 = r18
                r6 = r19
                r7 = r20
                r0.ipSecDeleteSecurityAssociation(r1, r2, r3, r4, r5, r6, r7)
                r47.writeNoException()
                return r10
            L_0x062a:
                r13.enforceInterface(r11)
                int r22 = r46.readInt()
                r1 = r22
                int r23 = r46.readInt()
                r2 = r23
                java.lang.String r24 = r46.readString()
                r3 = r24
                java.lang.String r25 = r46.readString()
                r4 = r25
                int r26 = r46.readInt()
                r5 = r26
                int r27 = r46.readInt()
                r6 = r27
                int r28 = r46.readInt()
                r7 = r28
                int r29 = r46.readInt()
                r8 = r29
                java.lang.String r30 = r46.readString()
                r9 = r30
                byte[] r31 = r46.createByteArray()
                r0 = r10
                r10 = r31
                int r32 = r46.readInt()
                r33 = r11
                r11 = r32
                java.lang.String r34 = r46.readString()
                r12 = r34
                byte[] r35 = r46.createByteArray()
                r13 = r35
                int r36 = r46.readInt()
                r14 = r36
                java.lang.String r37 = r46.readString()
                r15 = r37
                byte[] r38 = r46.createByteArray()
                r16 = r38
                int r39 = r46.readInt()
                r17 = r39
                int r40 = r46.readInt()
                r18 = r40
                int r41 = r46.readInt()
                r19 = r41
                int r42 = r46.readInt()
                r20 = r42
                int r43 = r46.readInt()
                r21 = r43
                r0 = r44
                r0.ipSecAddSecurityAssociation(r1, r2, r3, r4, r5, r6, r7, r8, r9, r10, r11, r12, r13, r14, r15, r16, r17, r18, r19, r20, r21)
                r47.writeNoException()
                r6 = 1
                return r6
            L_0x06b8:
                r6 = r10
                r33 = r11
                r7 = r46
                r8 = r33
                r7.enforceInterface(r8)
                int r0 = r46.readInt()
                java.lang.String r1 = r46.readString()
                java.lang.String r2 = r46.readString()
                int r3 = r46.readInt()
                r9 = r44
                int r4 = r9.ipSecAllocateSpi(r0, r1, r2, r3)
                r47.writeNoException()
                r10 = r47
                r10.writeInt(r4)
                return r6
            L_0x06e1:
                r6 = r10
                r8 = r11
                r10 = r12
                r7 = r13
                r9 = r15
                r7.enforceInterface(r8)
                int r0 = r46.readInt()
                if (r0 == 0) goto L_0x06f8
                android.os.Parcelable$Creator r0 = android.os.ParcelFileDescriptor.CREATOR
                java.lang.Object r0 = r0.createFromParcel(r7)
                android.os.ParcelFileDescriptor r0 = (android.os.ParcelFileDescriptor) r0
                goto L_0x06f9
            L_0x06f8:
                r0 = 0
            L_0x06f9:
                int r1 = r46.readInt()
                r9.ipSecSetEncapSocketOwner(r0, r1)
                r47.writeNoException()
                return r6
            L_0x0704:
                r6 = r10
                r8 = r11
                r10 = r12
                r7 = r13
                r9 = r15
                r7.enforceInterface(r8)
                int r11 = r46.readInt()
                int r12 = r46.readInt()
                java.lang.String r13 = r46.readString()
                java.lang.String r14 = r46.readString()
                java.lang.String r15 = r46.readString()
                r0 = r44
                r1 = r11
                r2 = r12
                r3 = r13
                r4 = r14
                r5 = r15
                r0.setProcSysNet(r1, r2, r3, r4, r5)
                r47.writeNoException()
                return r6
            L_0x072e:
                r6 = r10
                r8 = r11
                r10 = r12
                r7 = r13
                r9 = r15
                r7.enforceInterface(r8)
                int r0 = r46.readInt()
                int r1 = r46.readInt()
                java.lang.String r2 = r46.readString()
                java.lang.String r3 = r46.readString()
                java.lang.String r4 = r9.getProcSysNet(r0, r1, r2, r3)
                r47.writeNoException()
                r10.writeString(r4)
                return r6
            L_0x0751:
                r6 = r10
                r8 = r11
                r10 = r12
                r7 = r13
                r9 = r15
                r7.enforceInterface(r8)
                java.lang.String r0 = r46.readString()
                java.lang.String r1 = r46.readString()
                int r2 = r46.readInt()
                r9.interfaceDelAddress(r0, r1, r2)
                r47.writeNoException()
                return r6
            L_0x076c:
                r6 = r10
                r8 = r11
                r10 = r12
                r7 = r13
                r9 = r15
                r7.enforceInterface(r8)
                java.lang.String r0 = r46.readString()
                java.lang.String r1 = r46.readString()
                int r2 = r46.readInt()
                r9.interfaceAddAddress(r0, r1, r2)
                r47.writeNoException()
                return r6
            L_0x0787:
                r6 = r10
                r8 = r11
                r10 = r12
                r7 = r13
                r9 = r15
                r7.enforceInterface(r8)
                android.net.TetherStatsParcel[] r0 = r44.tetherGetStats()
                r47.writeNoException()
                r10.writeTypedArray(r0, r6)
                return r6
            L_0x079a:
                r6 = r10
                r8 = r11
                r10 = r12
                r7 = r13
                r9 = r15
                r7.enforceInterface(r8)
                boolean r0 = r44.tetherApplyDnsInterfaces()
                r47.writeNoException()
                r10.writeInt(r0)
                return r6
            L_0x07ad:
                r6 = r10
                r8 = r11
                r10 = r12
                r7 = r13
                r9 = r15
                r7.enforceInterface(r8)
                android.os.Parcelable$Creator<android.net.UidRangeParcel> r0 = android.net.UidRangeParcel.CREATOR
                java.lang.Object[] r0 = r7.createTypedArray(r0)
                android.net.UidRangeParcel[] r0 = (android.net.UidRangeParcel[]) r0
                int[] r1 = r46.createIntArray()
                r9.socketDestroy(r0, r1)
                r47.writeNoException()
                return r6
            L_0x07c8:
                r6 = r10
                r8 = r11
                r10 = r12
                r7 = r13
                r9 = r15
                r7.enforceInterface(r8)
                int r1 = r46.readInt()
                if (r1 == 0) goto L_0x07d7
                r0 = r6
            L_0x07d7:
                android.os.Parcelable$Creator<android.net.UidRangeParcel> r1 = android.net.UidRangeParcel.CREATOR
                java.lang.Object[] r1 = r7.createTypedArray(r1)
                android.net.UidRangeParcel[] r1 = (android.net.UidRangeParcel[]) r1
                r9.networkRejectNonSecureVpn(r0, r1)
                r47.writeNoException()
                return r6
            L_0x07e6:
                r6 = r10
                r8 = r11
                r10 = r12
                r7 = r13
                r9 = r15
                r7.enforceInterface(r8)
                int r0 = r46.readInt()
                android.os.Parcelable$Creator<android.net.UidRangeParcel> r1 = android.net.UidRangeParcel.CREATOR
                java.lang.Object[] r1 = r7.createTypedArray(r1)
                android.net.UidRangeParcel[] r1 = (android.net.UidRangeParcel[]) r1
                r9.networkRemoveUidRanges(r0, r1)
                r47.writeNoException()
                return r6
            L_0x0801:
                r6 = r10
                r8 = r11
                r10 = r12
                r7 = r13
                r9 = r15
                r7.enforceInterface(r8)
                int r0 = r46.readInt()
                android.os.Parcelable$Creator<android.net.UidRangeParcel> r1 = android.net.UidRangeParcel.CREATOR
                java.lang.Object[] r1 = r7.createTypedArray(r1)
                android.net.UidRangeParcel[] r1 = (android.net.UidRangeParcel[]) r1
                r9.networkAddUidRanges(r0, r1)
                r47.writeNoException()
                return r6
            L_0x081c:
                r6 = r10
                r8 = r11
                r10 = r12
                r7 = r13
                r9 = r15
                r7.enforceInterface(r8)
                int r0 = r46.readInt()
                java.lang.String r1 = r46.readString()
                r9.networkRemoveInterface(r0, r1)
                r47.writeNoException()
                return r6
            L_0x0833:
                r6 = r10
                r8 = r11
                r10 = r12
                r7 = r13
                r9 = r15
                r7.enforceInterface(r8)
                int r0 = r46.readInt()
                java.lang.String r1 = r46.readString()
                r9.networkAddInterface(r0, r1)
                r47.writeNoException()
                return r6
            L_0x084a:
                r6 = r10
                r8 = r11
                r10 = r12
                r7 = r13
                r9 = r15
                r7.enforceInterface(r8)
                int r0 = r46.readInt()
                r9.networkDestroy(r0)
                r47.writeNoException()
                return r6
            L_0x085d:
                r6 = r10
                r8 = r11
                r10 = r12
                r7 = r13
                r9 = r15
                r7.enforceInterface(r8)
                int r1 = r46.readInt()
                int r2 = r46.readInt()
                if (r2 == 0) goto L_0x0870
                r0 = r6
            L_0x0870:
                r9.networkCreateVpn(r1, r0)
                r47.writeNoException()
                return r6
            L_0x0877:
                r6 = r10
                r8 = r11
                r10 = r12
                r7 = r13
                r9 = r15
                r7.enforceInterface(r8)
                int r0 = r46.readInt()
                int r1 = r46.readInt()
                r9.networkCreatePhysical(r0, r1)
                r47.writeNoException()
                return r6
            L_0x088e:
                r6 = r10
                r8 = r11
                r10 = r12
                r7 = r13
                r9 = r15
                r7.enforceInterface(r8)
                int r1 = r46.readInt()
                if (r1 == 0) goto L_0x089d
                r0 = r6
            L_0x089d:
                boolean r1 = r9.bandwidthEnableDataSaver(r0)
                r47.writeNoException()
                r10.writeInt(r1)
                return r6
            L_0x08a8:
                r6 = r10
                r8 = r11
                r10 = r12
                r7 = r13
                r9 = r15
                r7.enforceInterface(r8)
                java.lang.String r1 = r46.readString()
                int r2 = r46.readInt()
                if (r2 == 0) goto L_0x08bb
                r0 = r6
            L_0x08bb:
                int[] r2 = r46.createIntArray()
                boolean r3 = r9.firewallReplaceUidChain(r1, r0, r2)
                r47.writeNoException()
                r10.writeInt(r3)
                return r6
            L_0x08ca:
                r6 = r10
                r8 = r11
                r10 = r12
                r7 = r13
                r9 = r15
                r7.enforceInterface(r8)
                boolean r0 = r44.isAlive()
                r47.writeNoException()
                r10.writeInt(r0)
                return r6
            L_0x08dd:
                r6 = r10
                r8 = r11
                r10 = r12
                r10.writeString(r8)
                return r6
            L_0x08e4:
                r6 = r10
                r8 = r11
                r10 = r12
                r7 = r13
                r9 = r15
                r7.enforceInterface(r8)
                r47.writeNoException()
                int r0 = r44.getInterfaceVersion()
                r10.writeInt(r0)
                return r6
            */
            throw new UnsupportedOperationException("Method not decompiled: android.net.INetd.Stub.onTransact(int, android.os.Parcel, android.os.Parcel, int):boolean");
        }

        private static class Proxy implements INetd {
            public static INetd sDefaultImpl;
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

            public boolean isAlive() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    boolean z = false;
                    if (!this.mRemote.transact(1, _data, _reply, 0) && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().isAlive();
                    }
                    _reply.readException();
                    if (_reply.readInt() != 0) {
                        z = true;
                    }
                    boolean _result = z;
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public boolean firewallReplaceUidChain(String chainName, boolean isWhitelist, int[] uids) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(chainName);
                    boolean _result = true;
                    _data.writeInt(isWhitelist ? 1 : 0);
                    _data.writeIntArray(uids);
                    if (!this.mRemote.transact(2, _data, _reply, 0) && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().firewallReplaceUidChain(chainName, isWhitelist, uids);
                    }
                    _reply.readException();
                    if (_reply.readInt() == 0) {
                        _result = false;
                    }
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public boolean bandwidthEnableDataSaver(boolean enable) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    boolean _result = true;
                    _data.writeInt(enable ? 1 : 0);
                    if (!this.mRemote.transact(3, _data, _reply, 0) && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().bandwidthEnableDataSaver(enable);
                    }
                    _reply.readException();
                    if (_reply.readInt() == 0) {
                        _result = false;
                    }
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void networkCreatePhysical(int netId, int permission) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(netId);
                    _data.writeInt(permission);
                    if (this.mRemote.transact(4, _data, _reply, 0) || Stub.getDefaultImpl() == null) {
                        _reply.readException();
                        _reply.recycle();
                        _data.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().networkCreatePhysical(netId, permission);
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void networkCreateVpn(int netId, boolean secure) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(netId);
                    _data.writeInt(secure ? 1 : 0);
                    if (this.mRemote.transact(5, _data, _reply, 0) || Stub.getDefaultImpl() == null) {
                        _reply.readException();
                        _reply.recycle();
                        _data.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().networkCreateVpn(netId, secure);
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void networkDestroy(int netId) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(netId);
                    if (this.mRemote.transact(6, _data, _reply, 0) || Stub.getDefaultImpl() == null) {
                        _reply.readException();
                        _reply.recycle();
                        _data.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().networkDestroy(netId);
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void networkAddInterface(int netId, String iface) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(netId);
                    _data.writeString(iface);
                    if (this.mRemote.transact(7, _data, _reply, 0) || Stub.getDefaultImpl() == null) {
                        _reply.readException();
                        _reply.recycle();
                        _data.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().networkAddInterface(netId, iface);
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void networkRemoveInterface(int netId, String iface) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(netId);
                    _data.writeString(iface);
                    if (this.mRemote.transact(8, _data, _reply, 0) || Stub.getDefaultImpl() == null) {
                        _reply.readException();
                        _reply.recycle();
                        _data.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().networkRemoveInterface(netId, iface);
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void networkAddUidRanges(int netId, UidRangeParcel[] uidRanges) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(netId);
                    _data.writeTypedArray(uidRanges, 0);
                    if (this.mRemote.transact(9, _data, _reply, 0) || Stub.getDefaultImpl() == null) {
                        _reply.readException();
                        _reply.recycle();
                        _data.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().networkAddUidRanges(netId, uidRanges);
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void networkRemoveUidRanges(int netId, UidRangeParcel[] uidRanges) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(netId);
                    _data.writeTypedArray(uidRanges, 0);
                    if (this.mRemote.transact(10, _data, _reply, 0) || Stub.getDefaultImpl() == null) {
                        _reply.readException();
                        _reply.recycle();
                        _data.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().networkRemoveUidRanges(netId, uidRanges);
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void networkRejectNonSecureVpn(boolean add, UidRangeParcel[] uidRanges) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(add ? 1 : 0);
                    _data.writeTypedArray(uidRanges, 0);
                    if (this.mRemote.transact(11, _data, _reply, 0) || Stub.getDefaultImpl() == null) {
                        _reply.readException();
                        _reply.recycle();
                        _data.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().networkRejectNonSecureVpn(add, uidRanges);
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void socketDestroy(UidRangeParcel[] uidRanges, int[] exemptUids) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedArray(uidRanges, 0);
                    _data.writeIntArray(exemptUids);
                    if (this.mRemote.transact(12, _data, _reply, 0) || Stub.getDefaultImpl() == null) {
                        _reply.readException();
                        _reply.recycle();
                        _data.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().socketDestroy(uidRanges, exemptUids);
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public boolean tetherApplyDnsInterfaces() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    boolean z = false;
                    if (!this.mRemote.transact(13, _data, _reply, 0) && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().tetherApplyDnsInterfaces();
                    }
                    _reply.readException();
                    if (_reply.readInt() != 0) {
                        z = true;
                    }
                    boolean _result = z;
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public TetherStatsParcel[] tetherGetStats() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (!this.mRemote.transact(14, _data, _reply, 0) && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().tetherGetStats();
                    }
                    _reply.readException();
                    TetherStatsParcel[] _result = (TetherStatsParcel[]) _reply.createTypedArray(TetherStatsParcel.CREATOR);
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void interfaceAddAddress(String ifName, String addrString, int prefixLength) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(ifName);
                    _data.writeString(addrString);
                    _data.writeInt(prefixLength);
                    if (this.mRemote.transact(15, _data, _reply, 0) || Stub.getDefaultImpl() == null) {
                        _reply.readException();
                        _reply.recycle();
                        _data.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().interfaceAddAddress(ifName, addrString, prefixLength);
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void interfaceDelAddress(String ifName, String addrString, int prefixLength) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(ifName);
                    _data.writeString(addrString);
                    _data.writeInt(prefixLength);
                    if (this.mRemote.transact(16, _data, _reply, 0) || Stub.getDefaultImpl() == null) {
                        _reply.readException();
                        _reply.recycle();
                        _data.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().interfaceDelAddress(ifName, addrString, prefixLength);
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public String getProcSysNet(int ipversion, int which, String ifname, String parameter) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(ipversion);
                    _data.writeInt(which);
                    _data.writeString(ifname);
                    _data.writeString(parameter);
                    if (!this.mRemote.transact(17, _data, _reply, 0) && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().getProcSysNet(ipversion, which, ifname, parameter);
                    }
                    _reply.readException();
                    String _result = _reply.readString();
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void setProcSysNet(int ipversion, int which, String ifname, String parameter, String value) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(ipversion);
                    _data.writeInt(which);
                    _data.writeString(ifname);
                    _data.writeString(parameter);
                    _data.writeString(value);
                    if (this.mRemote.transact(18, _data, _reply, 0) || Stub.getDefaultImpl() == null) {
                        _reply.readException();
                        _reply.recycle();
                        _data.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().setProcSysNet(ipversion, which, ifname, parameter, value);
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void ipSecSetEncapSocketOwner(ParcelFileDescriptor socket, int newUid) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (socket != null) {
                        _data.writeInt(1);
                        socket.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeInt(newUid);
                    if (this.mRemote.transact(19, _data, _reply, 0) || Stub.getDefaultImpl() == null) {
                        _reply.readException();
                        _reply.recycle();
                        _data.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().ipSecSetEncapSocketOwner(socket, newUid);
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public int ipSecAllocateSpi(int transformId, String sourceAddress, String destinationAddress, int spi) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(transformId);
                    _data.writeString(sourceAddress);
                    _data.writeString(destinationAddress);
                    _data.writeInt(spi);
                    if (!this.mRemote.transact(20, _data, _reply, 0) && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().ipSecAllocateSpi(transformId, sourceAddress, destinationAddress, spi);
                    }
                    _reply.readException();
                    int _result = _reply.readInt();
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void ipSecAddSecurityAssociation(int transformId, int mode, String sourceAddress, String destinationAddress, int underlyingNetId, int spi, int markValue, int markMask, String authAlgo, byte[] authKey, int authTruncBits, String cryptAlgo, byte[] cryptKey, int cryptTruncBits, String aeadAlgo, byte[] aeadKey, int aeadIcvBits, int encapType, int encapLocalPort, int encapRemotePort, int interfaceId) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(transformId);
                    _data.writeInt(mode);
                    _data.writeString(sourceAddress);
                    _data.writeString(destinationAddress);
                    _data.writeInt(underlyingNetId);
                    _data.writeInt(spi);
                    _data.writeInt(markValue);
                    _data.writeInt(markMask);
                    _data.writeString(authAlgo);
                    _data.writeByteArray(authKey);
                    _data.writeInt(authTruncBits);
                    _data.writeString(cryptAlgo);
                    _data.writeByteArray(cryptKey);
                    _data.writeInt(cryptTruncBits);
                    _data.writeString(aeadAlgo);
                    _data.writeByteArray(aeadKey);
                    _data.writeInt(aeadIcvBits);
                    _data.writeInt(encapType);
                    _data.writeInt(encapLocalPort);
                    _data.writeInt(encapRemotePort);
                    _data.writeInt(interfaceId);
                    if (this.mRemote.transact(21, _data, _reply, 0) || Stub.getDefaultImpl() == null) {
                        _reply.readException();
                        _reply.recycle();
                        _data.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().ipSecAddSecurityAssociation(transformId, mode, sourceAddress, destinationAddress, underlyingNetId, spi, markValue, markMask, authAlgo, authKey, authTruncBits, cryptAlgo, cryptKey, cryptTruncBits, aeadAlgo, aeadKey, aeadIcvBits, encapType, encapLocalPort, encapRemotePort, interfaceId);
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void ipSecDeleteSecurityAssociation(int transformId, String sourceAddress, String destinationAddress, int spi, int markValue, int markMask, int interfaceId) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    try {
                        _data.writeInt(transformId);
                    } catch (Throwable th) {
                        th = th;
                        String str = sourceAddress;
                        String str2 = destinationAddress;
                        int i = spi;
                        int i2 = markValue;
                        _reply.recycle();
                        _data.recycle();
                        throw th;
                    }
                    try {
                        _data.writeString(sourceAddress);
                    } catch (Throwable th2) {
                        th = th2;
                        String str22 = destinationAddress;
                        int i3 = spi;
                        int i22 = markValue;
                        _reply.recycle();
                        _data.recycle();
                        throw th;
                    }
                    try {
                        _data.writeString(destinationAddress);
                        try {
                            _data.writeInt(spi);
                        } catch (Throwable th3) {
                            th = th3;
                            int i222 = markValue;
                            _reply.recycle();
                            _data.recycle();
                            throw th;
                        }
                        try {
                            _data.writeInt(markValue);
                            _data.writeInt(markMask);
                            _data.writeInt(interfaceId);
                            if (this.mRemote.transact(22, _data, _reply, 0) || Stub.getDefaultImpl() == null) {
                                _reply.readException();
                                _reply.recycle();
                                _data.recycle();
                                return;
                            }
                            Stub.getDefaultImpl().ipSecDeleteSecurityAssociation(transformId, sourceAddress, destinationAddress, spi, markValue, markMask, interfaceId);
                            _reply.recycle();
                            _data.recycle();
                        } catch (Throwable th4) {
                            th = th4;
                            _reply.recycle();
                            _data.recycle();
                            throw th;
                        }
                    } catch (Throwable th5) {
                        th = th5;
                        int i32 = spi;
                        int i2222 = markValue;
                        _reply.recycle();
                        _data.recycle();
                        throw th;
                    }
                } catch (Throwable th6) {
                    th = th6;
                    int i4 = transformId;
                    String str3 = sourceAddress;
                    String str222 = destinationAddress;
                    int i322 = spi;
                    int i22222 = markValue;
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            public void ipSecApplyTransportModeTransform(ParcelFileDescriptor socket, int transformId, int direction, String sourceAddress, String destinationAddress, int spi) throws RemoteException {
                ParcelFileDescriptor parcelFileDescriptor = socket;
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (parcelFileDescriptor != null) {
                        _data.writeInt(1);
                        parcelFileDescriptor.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    try {
                        _data.writeInt(transformId);
                    } catch (Throwable th) {
                        th = th;
                        int i = direction;
                        String str = sourceAddress;
                        String str2 = destinationAddress;
                        int i2 = spi;
                        _reply.recycle();
                        _data.recycle();
                        throw th;
                    }
                    try {
                        _data.writeInt(direction);
                    } catch (Throwable th2) {
                        th = th2;
                        String str3 = sourceAddress;
                        String str22 = destinationAddress;
                        int i22 = spi;
                        _reply.recycle();
                        _data.recycle();
                        throw th;
                    }
                    try {
                        _data.writeString(sourceAddress);
                        try {
                            _data.writeString(destinationAddress);
                        } catch (Throwable th3) {
                            th = th3;
                            int i222 = spi;
                            _reply.recycle();
                            _data.recycle();
                            throw th;
                        }
                        try {
                            _data.writeInt(spi);
                            if (this.mRemote.transact(23, _data, _reply, 0) || Stub.getDefaultImpl() == null) {
                                _reply.readException();
                                _reply.recycle();
                                _data.recycle();
                                return;
                            }
                            Stub.getDefaultImpl().ipSecApplyTransportModeTransform(socket, transformId, direction, sourceAddress, destinationAddress, spi);
                            _reply.recycle();
                            _data.recycle();
                        } catch (Throwable th4) {
                            th = th4;
                            _reply.recycle();
                            _data.recycle();
                            throw th;
                        }
                    } catch (Throwable th5) {
                        th = th5;
                        String str222 = destinationAddress;
                        int i2222 = spi;
                        _reply.recycle();
                        _data.recycle();
                        throw th;
                    }
                } catch (Throwable th6) {
                    th = th6;
                    int i3 = transformId;
                    int i4 = direction;
                    String str32 = sourceAddress;
                    String str2222 = destinationAddress;
                    int i22222 = spi;
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            public void ipSecRemoveTransportModeTransform(ParcelFileDescriptor socket) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (socket != null) {
                        _data.writeInt(1);
                        socket.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    if (this.mRemote.transact(24, _data, _reply, 0) || Stub.getDefaultImpl() == null) {
                        _reply.readException();
                        _reply.recycle();
                        _data.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().ipSecRemoveTransportModeTransform(socket);
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void ipSecAddSecurityPolicy(int transformId, int selAddrFamily, int direction, String tmplSrcAddress, String tmplDstAddress, int spi, int markValue, int markMask, int interfaceId) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    try {
                        _data.writeInt(transformId);
                    } catch (Throwable th) {
                        th = th;
                        int i = selAddrFamily;
                        int i2 = direction;
                        _reply.recycle();
                        _data.recycle();
                        throw th;
                    }
                    try {
                        _data.writeInt(selAddrFamily);
                    } catch (Throwable th2) {
                        th = th2;
                        int i22 = direction;
                        _reply.recycle();
                        _data.recycle();
                        throw th;
                    }
                    try {
                        _data.writeInt(direction);
                        _data.writeString(tmplSrcAddress);
                        _data.writeString(tmplDstAddress);
                        _data.writeInt(spi);
                        _data.writeInt(markValue);
                        _data.writeInt(markMask);
                        _data.writeInt(interfaceId);
                        if (this.mRemote.transact(25, _data, _reply, 0) || Stub.getDefaultImpl() == null) {
                            _reply.readException();
                            _reply.recycle();
                            _data.recycle();
                            return;
                        }
                        Stub.getDefaultImpl().ipSecAddSecurityPolicy(transformId, selAddrFamily, direction, tmplSrcAddress, tmplDstAddress, spi, markValue, markMask, interfaceId);
                        _reply.recycle();
                        _data.recycle();
                    } catch (Throwable th3) {
                        th = th3;
                        _reply.recycle();
                        _data.recycle();
                        throw th;
                    }
                } catch (Throwable th4) {
                    th = th4;
                    int i3 = transformId;
                    int i4 = selAddrFamily;
                    int i222 = direction;
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            public void ipSecUpdateSecurityPolicy(int transformId, int selAddrFamily, int direction, String tmplSrcAddress, String tmplDstAddress, int spi, int markValue, int markMask, int interfaceId) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    try {
                        _data.writeInt(transformId);
                    } catch (Throwable th) {
                        th = th;
                        int i = selAddrFamily;
                        int i2 = direction;
                        _reply.recycle();
                        _data.recycle();
                        throw th;
                    }
                    try {
                        _data.writeInt(selAddrFamily);
                    } catch (Throwable th2) {
                        th = th2;
                        int i22 = direction;
                        _reply.recycle();
                        _data.recycle();
                        throw th;
                    }
                    try {
                        _data.writeInt(direction);
                        _data.writeString(tmplSrcAddress);
                        _data.writeString(tmplDstAddress);
                        _data.writeInt(spi);
                        _data.writeInt(markValue);
                        _data.writeInt(markMask);
                        _data.writeInt(interfaceId);
                        if (this.mRemote.transact(Stub.TRANSACTION_ipSecUpdateSecurityPolicy, _data, _reply, 0) || Stub.getDefaultImpl() == null) {
                            _reply.readException();
                            _reply.recycle();
                            _data.recycle();
                            return;
                        }
                        Stub.getDefaultImpl().ipSecUpdateSecurityPolicy(transformId, selAddrFamily, direction, tmplSrcAddress, tmplDstAddress, spi, markValue, markMask, interfaceId);
                        _reply.recycle();
                        _data.recycle();
                    } catch (Throwable th3) {
                        th = th3;
                        _reply.recycle();
                        _data.recycle();
                        throw th;
                    }
                } catch (Throwable th4) {
                    th = th4;
                    int i3 = transformId;
                    int i4 = selAddrFamily;
                    int i222 = direction;
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            public void ipSecDeleteSecurityPolicy(int transformId, int selAddrFamily, int direction, int markValue, int markMask, int interfaceId) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    try {
                        _data.writeInt(transformId);
                    } catch (Throwable th) {
                        th = th;
                        int i = selAddrFamily;
                        int i2 = direction;
                        int i3 = markValue;
                        int i4 = markMask;
                        int i5 = interfaceId;
                        _reply.recycle();
                        _data.recycle();
                        throw th;
                    }
                    try {
                        _data.writeInt(selAddrFamily);
                        try {
                            _data.writeInt(direction);
                            try {
                                _data.writeInt(markValue);
                            } catch (Throwable th2) {
                                th = th2;
                                int i42 = markMask;
                                int i52 = interfaceId;
                                _reply.recycle();
                                _data.recycle();
                                throw th;
                            }
                        } catch (Throwable th3) {
                            th = th3;
                            int i32 = markValue;
                            int i422 = markMask;
                            int i522 = interfaceId;
                            _reply.recycle();
                            _data.recycle();
                            throw th;
                        }
                    } catch (Throwable th4) {
                        th = th4;
                        int i22 = direction;
                        int i322 = markValue;
                        int i4222 = markMask;
                        int i5222 = interfaceId;
                        _reply.recycle();
                        _data.recycle();
                        throw th;
                    }
                    try {
                        _data.writeInt(markMask);
                        try {
                            _data.writeInt(interfaceId);
                            if (this.mRemote.transact(Stub.TRANSACTION_ipSecDeleteSecurityPolicy, _data, _reply, 0) || Stub.getDefaultImpl() == null) {
                                _reply.readException();
                                _reply.recycle();
                                _data.recycle();
                                return;
                            }
                            Stub.getDefaultImpl().ipSecDeleteSecurityPolicy(transformId, selAddrFamily, direction, markValue, markMask, interfaceId);
                            _reply.recycle();
                            _data.recycle();
                        } catch (Throwable th5) {
                            th = th5;
                            _reply.recycle();
                            _data.recycle();
                            throw th;
                        }
                    } catch (Throwable th6) {
                        th = th6;
                        int i52222 = interfaceId;
                        _reply.recycle();
                        _data.recycle();
                        throw th;
                    }
                } catch (Throwable th7) {
                    th = th7;
                    int i6 = transformId;
                    int i7 = selAddrFamily;
                    int i222 = direction;
                    int i3222 = markValue;
                    int i42222 = markMask;
                    int i522222 = interfaceId;
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            public void ipSecAddTunnelInterface(String deviceName, String localAddress, String remoteAddress, int iKey, int oKey, int interfaceId) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    try {
                        _data.writeString(deviceName);
                    } catch (Throwable th) {
                        th = th;
                        String str = localAddress;
                        String str2 = remoteAddress;
                        int i = iKey;
                        int i2 = oKey;
                        int i3 = interfaceId;
                        _reply.recycle();
                        _data.recycle();
                        throw th;
                    }
                    try {
                        _data.writeString(localAddress);
                        try {
                            _data.writeString(remoteAddress);
                            try {
                                _data.writeInt(iKey);
                            } catch (Throwable th2) {
                                th = th2;
                                int i22 = oKey;
                                int i32 = interfaceId;
                                _reply.recycle();
                                _data.recycle();
                                throw th;
                            }
                        } catch (Throwable th3) {
                            th = th3;
                            int i4 = iKey;
                            int i222 = oKey;
                            int i322 = interfaceId;
                            _reply.recycle();
                            _data.recycle();
                            throw th;
                        }
                    } catch (Throwable th4) {
                        th = th4;
                        String str22 = remoteAddress;
                        int i42 = iKey;
                        int i2222 = oKey;
                        int i3222 = interfaceId;
                        _reply.recycle();
                        _data.recycle();
                        throw th;
                    }
                    try {
                        _data.writeInt(oKey);
                        try {
                            _data.writeInt(interfaceId);
                            if (this.mRemote.transact(28, _data, _reply, 0) || Stub.getDefaultImpl() == null) {
                                _reply.readException();
                                _reply.recycle();
                                _data.recycle();
                                return;
                            }
                            Stub.getDefaultImpl().ipSecAddTunnelInterface(deviceName, localAddress, remoteAddress, iKey, oKey, interfaceId);
                            _reply.recycle();
                            _data.recycle();
                        } catch (Throwable th5) {
                            th = th5;
                            _reply.recycle();
                            _data.recycle();
                            throw th;
                        }
                    } catch (Throwable th6) {
                        th = th6;
                        int i32222 = interfaceId;
                        _reply.recycle();
                        _data.recycle();
                        throw th;
                    }
                } catch (Throwable th7) {
                    th = th7;
                    String str3 = deviceName;
                    String str4 = localAddress;
                    String str222 = remoteAddress;
                    int i422 = iKey;
                    int i22222 = oKey;
                    int i322222 = interfaceId;
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            public void ipSecUpdateTunnelInterface(String deviceName, String localAddress, String remoteAddress, int iKey, int oKey, int interfaceId) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    try {
                        _data.writeString(deviceName);
                    } catch (Throwable th) {
                        th = th;
                        String str = localAddress;
                        String str2 = remoteAddress;
                        int i = iKey;
                        int i2 = oKey;
                        int i3 = interfaceId;
                        _reply.recycle();
                        _data.recycle();
                        throw th;
                    }
                    try {
                        _data.writeString(localAddress);
                        try {
                            _data.writeString(remoteAddress);
                            try {
                                _data.writeInt(iKey);
                            } catch (Throwable th2) {
                                th = th2;
                                int i22 = oKey;
                                int i32 = interfaceId;
                                _reply.recycle();
                                _data.recycle();
                                throw th;
                            }
                        } catch (Throwable th3) {
                            th = th3;
                            int i4 = iKey;
                            int i222 = oKey;
                            int i322 = interfaceId;
                            _reply.recycle();
                            _data.recycle();
                            throw th;
                        }
                    } catch (Throwable th4) {
                        th = th4;
                        String str22 = remoteAddress;
                        int i42 = iKey;
                        int i2222 = oKey;
                        int i3222 = interfaceId;
                        _reply.recycle();
                        _data.recycle();
                        throw th;
                    }
                    try {
                        _data.writeInt(oKey);
                        try {
                            _data.writeInt(interfaceId);
                            if (this.mRemote.transact(29, _data, _reply, 0) || Stub.getDefaultImpl() == null) {
                                _reply.readException();
                                _reply.recycle();
                                _data.recycle();
                                return;
                            }
                            Stub.getDefaultImpl().ipSecUpdateTunnelInterface(deviceName, localAddress, remoteAddress, iKey, oKey, interfaceId);
                            _reply.recycle();
                            _data.recycle();
                        } catch (Throwable th5) {
                            th = th5;
                            _reply.recycle();
                            _data.recycle();
                            throw th;
                        }
                    } catch (Throwable th6) {
                        th = th6;
                        int i32222 = interfaceId;
                        _reply.recycle();
                        _data.recycle();
                        throw th;
                    }
                } catch (Throwable th7) {
                    th = th7;
                    String str3 = deviceName;
                    String str4 = localAddress;
                    String str222 = remoteAddress;
                    int i422 = iKey;
                    int i22222 = oKey;
                    int i322222 = interfaceId;
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            public void ipSecRemoveTunnelInterface(String deviceName) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(deviceName);
                    if (this.mRemote.transact(30, _data, _reply, 0) || Stub.getDefaultImpl() == null) {
                        _reply.readException();
                        _reply.recycle();
                        _data.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().ipSecRemoveTunnelInterface(deviceName);
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void wakeupAddInterface(String ifName, String prefix, int mark, int mask) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(ifName);
                    _data.writeString(prefix);
                    _data.writeInt(mark);
                    _data.writeInt(mask);
                    if (this.mRemote.transact(31, _data, _reply, 0) || Stub.getDefaultImpl() == null) {
                        _reply.readException();
                        _reply.recycle();
                        _data.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().wakeupAddInterface(ifName, prefix, mark, mask);
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void wakeupDelInterface(String ifName, String prefix, int mark, int mask) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(ifName);
                    _data.writeString(prefix);
                    _data.writeInt(mark);
                    _data.writeInt(mask);
                    if (this.mRemote.transact(32, _data, _reply, 0) || Stub.getDefaultImpl() == null) {
                        _reply.readException();
                        _reply.recycle();
                        _data.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().wakeupDelInterface(ifName, prefix, mark, mask);
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void setIPv6AddrGenMode(String ifName, int mode) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(ifName);
                    _data.writeInt(mode);
                    if (this.mRemote.transact(33, _data, _reply, 0) || Stub.getDefaultImpl() == null) {
                        _reply.readException();
                        _reply.recycle();
                        _data.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().setIPv6AddrGenMode(ifName, mode);
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void idletimerAddInterface(String ifName, int timeout, String classLabel) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(ifName);
                    _data.writeInt(timeout);
                    _data.writeString(classLabel);
                    if (this.mRemote.transact(34, _data, _reply, 0) || Stub.getDefaultImpl() == null) {
                        _reply.readException();
                        _reply.recycle();
                        _data.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().idletimerAddInterface(ifName, timeout, classLabel);
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void idletimerRemoveInterface(String ifName, int timeout, String classLabel) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(ifName);
                    _data.writeInt(timeout);
                    _data.writeString(classLabel);
                    if (this.mRemote.transact(35, _data, _reply, 0) || Stub.getDefaultImpl() == null) {
                        _reply.readException();
                        _reply.recycle();
                        _data.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().idletimerRemoveInterface(ifName, timeout, classLabel);
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void strictUidCleartextPenalty(int uid, int policyPenalty) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(uid);
                    _data.writeInt(policyPenalty);
                    if (this.mRemote.transact(36, _data, _reply, 0) || Stub.getDefaultImpl() == null) {
                        _reply.readException();
                        _reply.recycle();
                        _data.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().strictUidCleartextPenalty(uid, policyPenalty);
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public String clatdStart(String ifName, String nat64Prefix) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(ifName);
                    _data.writeString(nat64Prefix);
                    if (!this.mRemote.transact(37, _data, _reply, 0) && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().clatdStart(ifName, nat64Prefix);
                    }
                    _reply.readException();
                    String _result = _reply.readString();
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void clatdStop(String ifName) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(ifName);
                    if (this.mRemote.transact(38, _data, _reply, 0) || Stub.getDefaultImpl() == null) {
                        _reply.readException();
                        _reply.recycle();
                        _data.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().clatdStop(ifName);
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public boolean ipfwdEnabled() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    boolean z = false;
                    if (!this.mRemote.transact(39, _data, _reply, 0) && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().ipfwdEnabled();
                    }
                    _reply.readException();
                    if (_reply.readInt() != 0) {
                        z = true;
                    }
                    boolean _result = z;
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public String[] ipfwdGetRequesterList() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (!this.mRemote.transact(40, _data, _reply, 0) && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().ipfwdGetRequesterList();
                    }
                    _reply.readException();
                    String[] _result = _reply.createStringArray();
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void ipfwdEnableForwarding(String requester) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(requester);
                    if (this.mRemote.transact(41, _data, _reply, 0) || Stub.getDefaultImpl() == null) {
                        _reply.readException();
                        _reply.recycle();
                        _data.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().ipfwdEnableForwarding(requester);
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void ipfwdDisableForwarding(String requester) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(requester);
                    if (this.mRemote.transact(42, _data, _reply, 0) || Stub.getDefaultImpl() == null) {
                        _reply.readException();
                        _reply.recycle();
                        _data.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().ipfwdDisableForwarding(requester);
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void ipfwdAddInterfaceForward(String fromIface, String toIface) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(fromIface);
                    _data.writeString(toIface);
                    if (this.mRemote.transact(43, _data, _reply, 0) || Stub.getDefaultImpl() == null) {
                        _reply.readException();
                        _reply.recycle();
                        _data.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().ipfwdAddInterfaceForward(fromIface, toIface);
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void ipfwdRemoveInterfaceForward(String fromIface, String toIface) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(fromIface);
                    _data.writeString(toIface);
                    if (this.mRemote.transact(44, _data, _reply, 0) || Stub.getDefaultImpl() == null) {
                        _reply.readException();
                        _reply.recycle();
                        _data.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().ipfwdRemoveInterfaceForward(fromIface, toIface);
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void bandwidthSetInterfaceQuota(String ifName, long bytes) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(ifName);
                    _data.writeLong(bytes);
                    if (this.mRemote.transact(45, _data, _reply, 0) || Stub.getDefaultImpl() == null) {
                        _reply.readException();
                        _reply.recycle();
                        _data.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().bandwidthSetInterfaceQuota(ifName, bytes);
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void bandwidthRemoveInterfaceQuota(String ifName) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(ifName);
                    if (this.mRemote.transact(46, _data, _reply, 0) || Stub.getDefaultImpl() == null) {
                        _reply.readException();
                        _reply.recycle();
                        _data.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().bandwidthRemoveInterfaceQuota(ifName);
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void bandwidthSetInterfaceAlert(String ifName, long bytes) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(ifName);
                    _data.writeLong(bytes);
                    if (this.mRemote.transact(47, _data, _reply, 0) || Stub.getDefaultImpl() == null) {
                        _reply.readException();
                        _reply.recycle();
                        _data.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().bandwidthSetInterfaceAlert(ifName, bytes);
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void bandwidthRemoveInterfaceAlert(String ifName) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(ifName);
                    if (this.mRemote.transact(48, _data, _reply, 0) || Stub.getDefaultImpl() == null) {
                        _reply.readException();
                        _reply.recycle();
                        _data.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().bandwidthRemoveInterfaceAlert(ifName);
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void bandwidthSetGlobalAlert(long bytes) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeLong(bytes);
                    if (this.mRemote.transact(49, _data, _reply, 0) || Stub.getDefaultImpl() == null) {
                        _reply.readException();
                        _reply.recycle();
                        _data.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().bandwidthSetGlobalAlert(bytes);
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void bandwidthAddNaughtyApp(int uid) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(uid);
                    if (this.mRemote.transact(50, _data, _reply, 0) || Stub.getDefaultImpl() == null) {
                        _reply.readException();
                        _reply.recycle();
                        _data.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().bandwidthAddNaughtyApp(uid);
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void bandwidthRemoveNaughtyApp(int uid) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(uid);
                    if (this.mRemote.transact(51, _data, _reply, 0) || Stub.getDefaultImpl() == null) {
                        _reply.readException();
                        _reply.recycle();
                        _data.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().bandwidthRemoveNaughtyApp(uid);
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void bandwidthAddNiceApp(int uid) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(uid);
                    if (this.mRemote.transact(52, _data, _reply, 0) || Stub.getDefaultImpl() == null) {
                        _reply.readException();
                        _reply.recycle();
                        _data.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().bandwidthAddNiceApp(uid);
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void bandwidthRemoveNiceApp(int uid) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(uid);
                    if (this.mRemote.transact(53, _data, _reply, 0) || Stub.getDefaultImpl() == null) {
                        _reply.readException();
                        _reply.recycle();
                        _data.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().bandwidthRemoveNiceApp(uid);
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void tetherStart(String[] dhcpRanges) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStringArray(dhcpRanges);
                    if (this.mRemote.transact(54, _data, _reply, 0) || Stub.getDefaultImpl() == null) {
                        _reply.readException();
                        _reply.recycle();
                        _data.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().tetherStart(dhcpRanges);
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void tetherStop() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (this.mRemote.transact(55, _data, _reply, 0) || Stub.getDefaultImpl() == null) {
                        _reply.readException();
                        _reply.recycle();
                        _data.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().tetherStop();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public boolean tetherIsEnabled() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    boolean z = false;
                    if (!this.mRemote.transact(56, _data, _reply, 0) && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().tetherIsEnabled();
                    }
                    _reply.readException();
                    if (_reply.readInt() != 0) {
                        z = true;
                    }
                    boolean _result = z;
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void tetherInterfaceAdd(String ifName) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(ifName);
                    if (this.mRemote.transact(Stub.TRANSACTION_tetherInterfaceAdd, _data, _reply, 0) || Stub.getDefaultImpl() == null) {
                        _reply.readException();
                        _reply.recycle();
                        _data.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().tetherInterfaceAdd(ifName);
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void tetherInterfaceRemove(String ifName) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(ifName);
                    if (this.mRemote.transact(58, _data, _reply, 0) || Stub.getDefaultImpl() == null) {
                        _reply.readException();
                        _reply.recycle();
                        _data.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().tetherInterfaceRemove(ifName);
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public String[] tetherInterfaceList() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (!this.mRemote.transact(59, _data, _reply, 0) && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().tetherInterfaceList();
                    }
                    _reply.readException();
                    String[] _result = _reply.createStringArray();
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void tetherDnsSet(int netId, String[] dnsAddrs) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(netId);
                    _data.writeStringArray(dnsAddrs);
                    if (this.mRemote.transact(60, _data, _reply, 0) || Stub.getDefaultImpl() == null) {
                        _reply.readException();
                        _reply.recycle();
                        _data.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().tetherDnsSet(netId, dnsAddrs);
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public String[] tetherDnsList() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (!this.mRemote.transact(61, _data, _reply, 0) && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().tetherDnsList();
                    }
                    _reply.readException();
                    String[] _result = _reply.createStringArray();
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void networkAddRoute(int netId, String ifName, String destination, String nextHop) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(netId);
                    _data.writeString(ifName);
                    _data.writeString(destination);
                    _data.writeString(nextHop);
                    if (this.mRemote.transact(62, _data, _reply, 0) || Stub.getDefaultImpl() == null) {
                        _reply.readException();
                        _reply.recycle();
                        _data.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().networkAddRoute(netId, ifName, destination, nextHop);
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void networkRemoveRoute(int netId, String ifName, String destination, String nextHop) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(netId);
                    _data.writeString(ifName);
                    _data.writeString(destination);
                    _data.writeString(nextHop);
                    if (this.mRemote.transact(Stub.TRANSACTION_networkRemoveRoute, _data, _reply, 0) || Stub.getDefaultImpl() == null) {
                        _reply.readException();
                        _reply.recycle();
                        _data.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().networkRemoveRoute(netId, ifName, destination, nextHop);
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void networkAddLegacyRoute(int netId, String ifName, String destination, String nextHop, int uid) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(netId);
                    _data.writeString(ifName);
                    _data.writeString(destination);
                    _data.writeString(nextHop);
                    _data.writeInt(uid);
                    if (this.mRemote.transact(64, _data, _reply, 0) || Stub.getDefaultImpl() == null) {
                        _reply.readException();
                        _reply.recycle();
                        _data.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().networkAddLegacyRoute(netId, ifName, destination, nextHop, uid);
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void networkRemoveLegacyRoute(int netId, String ifName, String destination, String nextHop, int uid) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(netId);
                    _data.writeString(ifName);
                    _data.writeString(destination);
                    _data.writeString(nextHop);
                    _data.writeInt(uid);
                    if (this.mRemote.transact(65, _data, _reply, 0) || Stub.getDefaultImpl() == null) {
                        _reply.readException();
                        _reply.recycle();
                        _data.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().networkRemoveLegacyRoute(netId, ifName, destination, nextHop, uid);
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public int networkGetDefault() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (!this.mRemote.transact(66, _data, _reply, 0) && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().networkGetDefault();
                    }
                    _reply.readException();
                    int _result = _reply.readInt();
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void networkSetDefault(int netId) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(netId);
                    if (this.mRemote.transact(67, _data, _reply, 0) || Stub.getDefaultImpl() == null) {
                        _reply.readException();
                        _reply.recycle();
                        _data.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().networkSetDefault(netId);
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void networkClearDefault() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (this.mRemote.transact(68, _data, _reply, 0) || Stub.getDefaultImpl() == null) {
                        _reply.readException();
                        _reply.recycle();
                        _data.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().networkClearDefault();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void networkSetPermissionForNetwork(int netId, int permission) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(netId);
                    _data.writeInt(permission);
                    if (this.mRemote.transact(69, _data, _reply, 0) || Stub.getDefaultImpl() == null) {
                        _reply.readException();
                        _reply.recycle();
                        _data.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().networkSetPermissionForNetwork(netId, permission);
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void networkSetPermissionForUser(int permission, int[] uids) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(permission);
                    _data.writeIntArray(uids);
                    if (this.mRemote.transact(70, _data, _reply, 0) || Stub.getDefaultImpl() == null) {
                        _reply.readException();
                        _reply.recycle();
                        _data.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().networkSetPermissionForUser(permission, uids);
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void networkClearPermissionForUser(int[] uids) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeIntArray(uids);
                    if (this.mRemote.transact(71, _data, _reply, 0) || Stub.getDefaultImpl() == null) {
                        _reply.readException();
                        _reply.recycle();
                        _data.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().networkClearPermissionForUser(uids);
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void trafficSetNetPermForUids(int permission, int[] uids) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(permission);
                    _data.writeIntArray(uids);
                    if (this.mRemote.transact(72, _data, _reply, 0) || Stub.getDefaultImpl() == null) {
                        _reply.readException();
                        _reply.recycle();
                        _data.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().trafficSetNetPermForUids(permission, uids);
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void networkSetProtectAllow(int uid) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(uid);
                    if (this.mRemote.transact(73, _data, _reply, 0) || Stub.getDefaultImpl() == null) {
                        _reply.readException();
                        _reply.recycle();
                        _data.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().networkSetProtectAllow(uid);
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void networkSetProtectDeny(int uid) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(uid);
                    if (this.mRemote.transact(74, _data, _reply, 0) || Stub.getDefaultImpl() == null) {
                        _reply.readException();
                        _reply.recycle();
                        _data.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().networkSetProtectDeny(uid);
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public boolean networkCanProtect(int uid) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(uid);
                    boolean z = false;
                    if (!this.mRemote.transact(75, _data, _reply, 0) && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().networkCanProtect(uid);
                    }
                    _reply.readException();
                    if (_reply.readInt() != 0) {
                        z = true;
                    }
                    boolean _result = z;
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void firewallSetFirewallType(int firewalltype) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(firewalltype);
                    if (this.mRemote.transact(76, _data, _reply, 0) || Stub.getDefaultImpl() == null) {
                        _reply.readException();
                        _reply.recycle();
                        _data.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().firewallSetFirewallType(firewalltype);
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void firewallSetInterfaceRule(String ifName, int firewallRule) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(ifName);
                    _data.writeInt(firewallRule);
                    if (this.mRemote.transact(77, _data, _reply, 0) || Stub.getDefaultImpl() == null) {
                        _reply.readException();
                        _reply.recycle();
                        _data.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().firewallSetInterfaceRule(ifName, firewallRule);
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void firewallSetUidRule(int childChain, int uid, int firewallRule) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(childChain);
                    _data.writeInt(uid);
                    _data.writeInt(firewallRule);
                    if (this.mRemote.transact(78, _data, _reply, 0) || Stub.getDefaultImpl() == null) {
                        _reply.readException();
                        _reply.recycle();
                        _data.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().firewallSetUidRule(childChain, uid, firewallRule);
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void firewallEnableChildChain(int childChain, boolean enable) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(childChain);
                    _data.writeInt(enable ? 1 : 0);
                    if (this.mRemote.transact(79, _data, _reply, 0) || Stub.getDefaultImpl() == null) {
                        _reply.readException();
                        _reply.recycle();
                        _data.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().firewallEnableChildChain(childChain, enable);
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public String[] interfaceGetList() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (!this.mRemote.transact(80, _data, _reply, 0) && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().interfaceGetList();
                    }
                    _reply.readException();
                    String[] _result = _reply.createStringArray();
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public InterfaceConfigurationParcel interfaceGetCfg(String ifName) throws RemoteException {
                InterfaceConfigurationParcel _result;
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(ifName);
                    if (!this.mRemote.transact(81, _data, _reply, 0) && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().interfaceGetCfg(ifName);
                    }
                    _reply.readException();
                    if (_reply.readInt() != 0) {
                        _result = InterfaceConfigurationParcel.CREATOR.createFromParcel(_reply);
                    } else {
                        _result = null;
                    }
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void interfaceSetCfg(InterfaceConfigurationParcel cfg) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (cfg != null) {
                        _data.writeInt(1);
                        cfg.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    if (this.mRemote.transact(82, _data, _reply, 0) || Stub.getDefaultImpl() == null) {
                        _reply.readException();
                        _reply.recycle();
                        _data.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().interfaceSetCfg(cfg);
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void interfaceSetIPv6PrivacyExtensions(String ifName, boolean enable) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(ifName);
                    _data.writeInt(enable ? 1 : 0);
                    if (this.mRemote.transact(83, _data, _reply, 0) || Stub.getDefaultImpl() == null) {
                        _reply.readException();
                        _reply.recycle();
                        _data.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().interfaceSetIPv6PrivacyExtensions(ifName, enable);
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void interfaceClearAddrs(String ifName) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(ifName);
                    if (this.mRemote.transact(84, _data, _reply, 0) || Stub.getDefaultImpl() == null) {
                        _reply.readException();
                        _reply.recycle();
                        _data.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().interfaceClearAddrs(ifName);
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void interfaceSetEnableIPv6(String ifName, boolean enable) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(ifName);
                    _data.writeInt(enable ? 1 : 0);
                    if (this.mRemote.transact(85, _data, _reply, 0) || Stub.getDefaultImpl() == null) {
                        _reply.readException();
                        _reply.recycle();
                        _data.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().interfaceSetEnableIPv6(ifName, enable);
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void interfaceSetMtu(String ifName, int mtu) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(ifName);
                    _data.writeInt(mtu);
                    if (this.mRemote.transact(86, _data, _reply, 0) || Stub.getDefaultImpl() == null) {
                        _reply.readException();
                        _reply.recycle();
                        _data.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().interfaceSetMtu(ifName, mtu);
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void tetherAddForward(String intIface, String extIface) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(intIface);
                    _data.writeString(extIface);
                    if (this.mRemote.transact(87, _data, _reply, 0) || Stub.getDefaultImpl() == null) {
                        _reply.readException();
                        _reply.recycle();
                        _data.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().tetherAddForward(intIface, extIface);
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void tetherRemoveForward(String intIface, String extIface) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(intIface);
                    _data.writeString(extIface);
                    if (this.mRemote.transact(Stub.TRANSACTION_tetherRemoveForward, _data, _reply, 0) || Stub.getDefaultImpl() == null) {
                        _reply.readException();
                        _reply.recycle();
                        _data.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().tetherRemoveForward(intIface, extIface);
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void setTcpRWmemorySize(String rmemValues, String wmemValues) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(rmemValues);
                    _data.writeString(wmemValues);
                    if (this.mRemote.transact(Stub.TRANSACTION_setTcpRWmemorySize, _data, _reply, 0) || Stub.getDefaultImpl() == null) {
                        _reply.readException();
                        _reply.recycle();
                        _data.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().setTcpRWmemorySize(rmemValues, wmemValues);
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void registerUnsolicitedEventListener(INetdUnsolicitedEventListener listener) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(listener != null ? listener.asBinder() : null);
                    if (this.mRemote.transact(Stub.TRANSACTION_registerUnsolicitedEventListener, _data, _reply, 0) || Stub.getDefaultImpl() == null) {
                        _reply.readException();
                        _reply.recycle();
                        _data.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().registerUnsolicitedEventListener(listener);
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void firewallAddUidInterfaceRules(String ifName, int[] uids) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(ifName);
                    _data.writeIntArray(uids);
                    if (this.mRemote.transact(Stub.TRANSACTION_firewallAddUidInterfaceRules, _data, _reply, 0) || Stub.getDefaultImpl() == null) {
                        _reply.readException();
                        _reply.recycle();
                        _data.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().firewallAddUidInterfaceRules(ifName, uids);
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void firewallRemoveUidInterfaceRules(int[] uids) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeIntArray(uids);
                    if (this.mRemote.transact(Stub.TRANSACTION_firewallRemoveUidInterfaceRules, _data, _reply, 0) || Stub.getDefaultImpl() == null) {
                        _reply.readException();
                        _reply.recycle();
                        _data.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().firewallRemoveUidInterfaceRules(uids);
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void trafficSwapActiveStatsMap() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (this.mRemote.transact(Stub.TRANSACTION_trafficSwapActiveStatsMap, _data, _reply, 0) || Stub.getDefaultImpl() == null) {
                        _reply.readException();
                        _reply.recycle();
                        _data.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().trafficSwapActiveStatsMap();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public IBinder getOemNetd() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (!this.mRemote.transact(Stub.TRANSACTION_getOemNetd, _data, _reply, 0) && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().getOemNetd();
                    }
                    _reply.readException();
                    IBinder _result = _reply.readStrongBinder();
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } finally {
                    _reply.recycle();
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

        public static boolean setDefaultImpl(INetd impl) {
            if (Proxy.sDefaultImpl != null || impl == null) {
                return false;
            }
            Proxy.sDefaultImpl = impl;
            return true;
        }

        public static INetd getDefaultImpl() {
            return Proxy.sDefaultImpl;
        }
    }
}
