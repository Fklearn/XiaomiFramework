package com.android.server;

import android.app.AppOpsManager;
import android.content.Context;
import android.net.IIpSecService;
import android.net.INetd;
import android.net.IpSecAlgorithm;
import android.net.IpSecConfig;
import android.net.IpSecTransformResponse;
import android.net.IpSecUdpEncapResponse;
import android.net.LinkAddress;
import android.net.Network;
import android.net.NetworkUtils;
import android.net.TrafficStats;
import android.net.util.NetdService;
import android.os.Binder;
import android.os.IBinder;
import android.os.ParcelFileDescriptor;
import android.os.RemoteException;
import android.os.ServiceSpecificException;
import android.system.ErrnoException;
import android.system.Os;
import android.system.OsConstants;
import android.text.TextUtils;
import android.util.Log;
import android.util.Slog;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.util.Preconditions;
import java.io.FileDescriptor;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import libcore.io.IoUtils;

public class IpSecService extends IIpSecService.Stub {
    /* access modifiers changed from: private */
    public static final int[] ADDRESS_FAMILIES = {OsConstants.AF_INET, OsConstants.AF_INET6};
    private static final boolean DBG = Log.isLoggable(TAG, 3);
    static final int FREE_PORT_MIN = 1024;
    private static final InetAddress INADDR_ANY;
    @VisibleForTesting
    static final int MAX_PORT_BIND_ATTEMPTS = 10;
    private static final int NETD_FETCH_TIMEOUT_MS = 5000;
    private static final String NETD_SERVICE_NAME = "netd";
    static final int PORT_MAX = 65535;
    private static final String TAG = "IpSecService";
    private static final String TUNNEL_OP = "android:manage_ipsec_tunnels";
    @VisibleForTesting
    static final int TUN_INTF_NETID_RANGE = 1024;
    @VisibleForTesting
    static final int TUN_INTF_NETID_START = 64512;
    private final Context mContext;
    @GuardedBy({"IpSecService.this"})
    private int mNextResourceId;
    private int mNextTunnelNetIdIndex;
    /* access modifiers changed from: private */
    public final IpSecServiceConfiguration mSrvConfig;
    private final SparseBooleanArray mTunnelNetIds;
    final UidFdTagger mUidFdTagger;
    @VisibleForTesting
    final UserResourceTracker mUserResourceTracker;

    @VisibleForTesting
    public interface IResource {
        void freeUnderlyingResources() throws RemoteException;

        void invalidate() throws RemoteException;
    }

    interface IpSecServiceConfiguration {
        public static final IpSecServiceConfiguration GETSRVINSTANCE = new IpSecServiceConfiguration() {
            public INetd getNetdInstance() throws RemoteException {
                INetd netd = NetdService.getInstance();
                if (netd != null) {
                    return netd;
                }
                throw new RemoteException("Failed to Get Netd Instance");
            }
        };

        INetd getNetdInstance() throws RemoteException;
    }

    @VisibleForTesting
    public interface UidFdTagger {
        void tag(FileDescriptor fileDescriptor, int i) throws IOException;
    }

    static {
        try {
            INADDR_ANY = InetAddress.getByAddress(new byte[]{0, 0, 0, 0});
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }

    @VisibleForTesting
    public class RefcountedResource<T extends IResource> implements IBinder.DeathRecipient {
        IBinder mBinder;
        private final List<RefcountedResource> mChildren;
        int mRefCount = 1;
        private final T mResource;

        RefcountedResource(T resource, IBinder binder, RefcountedResource... children) {
            synchronized (IpSecService.this) {
                this.mResource = resource;
                this.mChildren = new ArrayList(children.length);
                this.mBinder = binder;
                for (RefcountedResource child : children) {
                    this.mChildren.add(child);
                    child.mRefCount++;
                }
                try {
                    this.mBinder.linkToDeath(this, 0);
                } catch (RemoteException e) {
                    binderDied();
                    e.rethrowFromSystemServer();
                }
            }
        }

        public void binderDied() {
            synchronized (IpSecService.this) {
                try {
                    userRelease();
                } catch (Exception e) {
                    Log.e(IpSecService.TAG, "Failed to release resource: " + e);
                }
            }
        }

        public T getResource() {
            return this.mResource;
        }

        @GuardedBy({"IpSecService.this"})
        public void userRelease() throws RemoteException {
            IBinder iBinder = this.mBinder;
            if (iBinder != null) {
                iBinder.unlinkToDeath(this, 0);
                this.mBinder = null;
                this.mResource.invalidate();
                releaseReference();
            }
        }

        @GuardedBy({"IpSecService.this"})
        @VisibleForTesting
        public void releaseReference() throws RemoteException {
            this.mRefCount--;
            int i = this.mRefCount;
            if (i <= 0) {
                if (i >= 0) {
                    this.mResource.freeUnderlyingResources();
                    for (RefcountedResource<? extends IResource> child : this.mChildren) {
                        child.releaseReference();
                    }
                    this.mRefCount--;
                    return;
                }
                throw new IllegalStateException("Invalid operation - resource has already been released.");
            }
        }

        public String toString() {
            return "{mResource=" + this.mResource + ", mRefCount=" + this.mRefCount + ", mChildren=" + this.mChildren + "}";
        }
    }

    @VisibleForTesting
    static class ResourceTracker {
        int mCurrent = 0;
        private final int mMax;

        ResourceTracker(int max) {
            this.mMax = max;
        }

        /* access modifiers changed from: package-private */
        public boolean isAvailable() {
            return this.mCurrent < this.mMax;
        }

        /* access modifiers changed from: package-private */
        public void take() {
            if (!isAvailable()) {
                Log.wtf(IpSecService.TAG, "Too many resources allocated!");
            }
            this.mCurrent++;
        }

        /* access modifiers changed from: package-private */
        public void give() {
            if (this.mCurrent <= 0) {
                Log.wtf(IpSecService.TAG, "We've released this resource too many times");
            }
            this.mCurrent--;
        }

        public String toString() {
            return "{mCurrent=" + this.mCurrent + ", mMax=" + this.mMax + "}";
        }
    }

    @VisibleForTesting
    static final class UserRecord {
        public static final int MAX_NUM_ENCAP_SOCKETS = 2;
        public static final int MAX_NUM_SPIS = 8;
        public static final int MAX_NUM_TRANSFORMS = 4;
        public static final int MAX_NUM_TUNNEL_INTERFACES = 2;
        final RefcountedResourceArray<EncapSocketRecord> mEncapSocketRecords = new RefcountedResourceArray<>(EncapSocketRecord.class.getSimpleName());
        final ResourceTracker mSocketQuotaTracker = new ResourceTracker(2);
        final ResourceTracker mSpiQuotaTracker = new ResourceTracker(8);
        final RefcountedResourceArray<SpiRecord> mSpiRecords = new RefcountedResourceArray<>(SpiRecord.class.getSimpleName());
        final ResourceTracker mTransformQuotaTracker = new ResourceTracker(4);
        final RefcountedResourceArray<TransformRecord> mTransformRecords = new RefcountedResourceArray<>(TransformRecord.class.getSimpleName());
        final RefcountedResourceArray<TunnelInterfaceRecord> mTunnelInterfaceRecords = new RefcountedResourceArray<>(TunnelInterfaceRecord.class.getSimpleName());
        final ResourceTracker mTunnelQuotaTracker = new ResourceTracker(2);

        UserRecord() {
        }

        /* access modifiers changed from: package-private */
        public void removeSpiRecord(int resourceId) {
            this.mSpiRecords.remove(resourceId);
        }

        /* access modifiers changed from: package-private */
        public void removeTransformRecord(int resourceId) {
            this.mTransformRecords.remove(resourceId);
        }

        /* access modifiers changed from: package-private */
        public void removeTunnelInterfaceRecord(int resourceId) {
            this.mTunnelInterfaceRecords.remove(resourceId);
        }

        /* access modifiers changed from: package-private */
        public void removeEncapSocketRecord(int resourceId) {
            this.mEncapSocketRecords.remove(resourceId);
        }

        public String toString() {
            return "{mSpiQuotaTracker=" + this.mSpiQuotaTracker + ", mTransformQuotaTracker=" + this.mTransformQuotaTracker + ", mSocketQuotaTracker=" + this.mSocketQuotaTracker + ", mTunnelQuotaTracker=" + this.mTunnelQuotaTracker + ", mSpiRecords=" + this.mSpiRecords + ", mTransformRecords=" + this.mTransformRecords + ", mEncapSocketRecords=" + this.mEncapSocketRecords + ", mTunnelInterfaceRecords=" + this.mTunnelInterfaceRecords + "}";
        }
    }

    @VisibleForTesting
    static final class UserResourceTracker {
        private final SparseArray<UserRecord> mUserRecords = new SparseArray<>();

        UserResourceTracker() {
        }

        public UserRecord getUserRecord(int uid) {
            checkCallerUid(uid);
            UserRecord r = this.mUserRecords.get(uid);
            if (r != null) {
                return r;
            }
            UserRecord r2 = new UserRecord();
            this.mUserRecords.put(uid, r2);
            return r2;
        }

        private void checkCallerUid(int uid) {
            if (uid != Binder.getCallingUid() && 1000 != Binder.getCallingUid()) {
                throw new SecurityException("Attempted access of unowned resources");
            }
        }

        public String toString() {
            return this.mUserRecords.toString();
        }
    }

    private abstract class OwnedResourceRecord implements IResource {
        protected final int mResourceId;
        final int pid;
        final int uid;

        public abstract void freeUnderlyingResources() throws RemoteException;

        /* access modifiers changed from: protected */
        public abstract ResourceTracker getResourceTracker();

        public abstract void invalidate() throws RemoteException;

        OwnedResourceRecord(int resourceId) {
            if (resourceId != -1) {
                this.mResourceId = resourceId;
                this.pid = Binder.getCallingPid();
                this.uid = Binder.getCallingUid();
                getResourceTracker().take();
                return;
            }
            throw new IllegalArgumentException("Resource ID must not be INVALID_RESOURCE_ID");
        }

        /* access modifiers changed from: protected */
        public UserRecord getUserRecord() {
            return IpSecService.this.mUserResourceTracker.getUserRecord(this.uid);
        }

        public String toString() {
            return "{mResourceId=" + this.mResourceId + ", pid=" + this.pid + ", uid=" + this.uid + "}";
        }
    }

    static class RefcountedResourceArray<T extends IResource> {
        SparseArray<RefcountedResource<T>> mArray = new SparseArray<>();
        private final String mTypeName;

        public RefcountedResourceArray(String typeName) {
            this.mTypeName = typeName;
        }

        /* access modifiers changed from: package-private */
        public T getResourceOrThrow(int key) {
            return getRefcountedResourceOrThrow(key).getResource();
        }

        /* access modifiers changed from: package-private */
        public RefcountedResource<T> getRefcountedResourceOrThrow(int key) {
            RefcountedResource<T> resource = this.mArray.get(key);
            if (resource != null) {
                return resource;
            }
            throw new IllegalArgumentException(String.format("No such %s found for given id: %d", new Object[]{this.mTypeName, Integer.valueOf(key)}));
        }

        /* access modifiers changed from: package-private */
        public void put(int key, RefcountedResource<T> obj) {
            Preconditions.checkNotNull(obj, "Null resources cannot be added");
            this.mArray.put(key, obj);
        }

        /* access modifiers changed from: package-private */
        public void remove(int key) {
            this.mArray.remove(key);
        }

        public String toString() {
            return this.mArray.toString();
        }
    }

    private final class TransformRecord extends OwnedResourceRecord {
        private final IpSecConfig mConfig;
        private final EncapSocketRecord mSocket;
        private final SpiRecord mSpi;

        TransformRecord(int resourceId, IpSecConfig config, SpiRecord spi, EncapSocketRecord socket) {
            super(resourceId);
            this.mConfig = config;
            this.mSpi = spi;
            this.mSocket = socket;
            spi.setOwnedByTransform();
        }

        public IpSecConfig getConfig() {
            return this.mConfig;
        }

        public SpiRecord getSpiRecord() {
            return this.mSpi;
        }

        public EncapSocketRecord getSocketRecord() {
            return this.mSocket;
        }

        public void freeUnderlyingResources() {
            int spi = this.mSpi.getSpi();
            try {
                IpSecService.this.mSrvConfig.getNetdInstance().ipSecDeleteSecurityAssociation(this.uid, this.mConfig.getSourceAddress(), this.mConfig.getDestinationAddress(), spi, this.mConfig.getMarkValue(), this.mConfig.getMarkMask(), this.mConfig.getXfrmInterfaceId());
            } catch (RemoteException | ServiceSpecificException e) {
                Log.e(IpSecService.TAG, "Failed to delete SA with ID: " + this.mResourceId, e);
            }
            getResourceTracker().give();
        }

        public void invalidate() throws RemoteException {
            getUserRecord().removeTransformRecord(this.mResourceId);
        }

        /* access modifiers changed from: protected */
        public ResourceTracker getResourceTracker() {
            return getUserRecord().mTransformQuotaTracker;
        }

        public String toString() {
            return "{super=" + super.toString() + ", mSocket=" + this.mSocket + ", mSpi.mResourceId=" + this.mSpi.mResourceId + ", mConfig=" + this.mConfig + "}";
        }
    }

    private final class SpiRecord extends OwnedResourceRecord {
        private final String mDestinationAddress;
        private boolean mOwnedByTransform = false;
        private final String mSourceAddress;
        private int mSpi;

        SpiRecord(int resourceId, String sourceAddress, String destinationAddress, int spi) {
            super(resourceId);
            this.mSourceAddress = sourceAddress;
            this.mDestinationAddress = destinationAddress;
            this.mSpi = spi;
        }

        public void freeUnderlyingResources() {
            try {
                if (!this.mOwnedByTransform) {
                    IpSecService.this.mSrvConfig.getNetdInstance().ipSecDeleteSecurityAssociation(this.uid, this.mSourceAddress, this.mDestinationAddress, this.mSpi, 0, 0, 0);
                }
            } catch (RemoteException | ServiceSpecificException e) {
                Log.e(IpSecService.TAG, "Failed to delete SPI reservation with ID: " + this.mResourceId, e);
            }
            this.mSpi = 0;
            getResourceTracker().give();
        }

        public int getSpi() {
            return this.mSpi;
        }

        public String getDestinationAddress() {
            return this.mDestinationAddress;
        }

        public void setOwnedByTransform() {
            if (!this.mOwnedByTransform) {
                this.mOwnedByTransform = true;
                return;
            }
            throw new IllegalStateException("Cannot own an SPI twice!");
        }

        public boolean getOwnedByTransform() {
            return this.mOwnedByTransform;
        }

        public void invalidate() throws RemoteException {
            getUserRecord().removeSpiRecord(this.mResourceId);
        }

        /* access modifiers changed from: protected */
        public ResourceTracker getResourceTracker() {
            return getUserRecord().mSpiQuotaTracker;
        }

        public String toString() {
            return "{super=" + super.toString() + ", mSpi=" + this.mSpi + ", mSourceAddress=" + this.mSourceAddress + ", mDestinationAddress=" + this.mDestinationAddress + ", mOwnedByTransform=" + this.mOwnedByTransform + "}";
        }
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public int reserveNetId() {
        synchronized (this.mTunnelNetIds) {
            for (int i = 0; i < 1024; i++) {
                int netId = TUN_INTF_NETID_START + this.mNextTunnelNetIdIndex;
                int i2 = this.mNextTunnelNetIdIndex + 1;
                this.mNextTunnelNetIdIndex = i2;
                if (i2 >= 1024) {
                    this.mNextTunnelNetIdIndex = 0;
                }
                if (!this.mTunnelNetIds.get(netId)) {
                    this.mTunnelNetIds.put(netId, true);
                    return netId;
                }
            }
            throw new IllegalStateException("No free netIds to allocate");
        }
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public void releaseNetId(int netId) {
        synchronized (this.mTunnelNetIds) {
            this.mTunnelNetIds.delete(netId);
        }
    }

    private final class TunnelInterfaceRecord extends OwnedResourceRecord {
        private final int mIfId;
        private final int mIkey;
        /* access modifiers changed from: private */
        public final String mInterfaceName;
        private final String mLocalAddress;
        private final int mOkey;
        private final String mRemoteAddress;
        private final Network mUnderlyingNetwork;

        TunnelInterfaceRecord(int resourceId, String interfaceName, Network underlyingNetwork, String localAddr, String remoteAddr, int ikey, int okey, int intfId) {
            super(resourceId);
            this.mInterfaceName = interfaceName;
            this.mUnderlyingNetwork = underlyingNetwork;
            this.mLocalAddress = localAddr;
            this.mRemoteAddress = remoteAddr;
            this.mIkey = ikey;
            this.mOkey = okey;
            this.mIfId = intfId;
        }

        public void freeUnderlyingResources() {
            try {
                INetd netd = IpSecService.this.mSrvConfig.getNetdInstance();
                netd.ipSecRemoveTunnelInterface(this.mInterfaceName);
                for (int selAddrFamily : IpSecService.ADDRESS_FAMILIES) {
                    netd.ipSecDeleteSecurityPolicy(this.uid, selAddrFamily, 1, this.mOkey, -1, this.mIfId);
                    netd.ipSecDeleteSecurityPolicy(this.uid, selAddrFamily, 0, this.mIkey, -1, this.mIfId);
                }
            } catch (RemoteException | ServiceSpecificException e) {
                Log.e(IpSecService.TAG, "Failed to delete VTI with interface name: " + this.mInterfaceName + " and id: " + this.mResourceId, e);
            }
            getResourceTracker().give();
            IpSecService.this.releaseNetId(this.mIkey);
            IpSecService.this.releaseNetId(this.mOkey);
        }

        public String getInterfaceName() {
            return this.mInterfaceName;
        }

        public Network getUnderlyingNetwork() {
            return this.mUnderlyingNetwork;
        }

        public String getLocalAddress() {
            return this.mLocalAddress;
        }

        public String getRemoteAddress() {
            return this.mRemoteAddress;
        }

        public int getIkey() {
            return this.mIkey;
        }

        public int getOkey() {
            return this.mOkey;
        }

        public int getIfId() {
            return this.mIfId;
        }

        /* access modifiers changed from: protected */
        public ResourceTracker getResourceTracker() {
            return getUserRecord().mTunnelQuotaTracker;
        }

        public void invalidate() {
            getUserRecord().removeTunnelInterfaceRecord(this.mResourceId);
        }

        public String toString() {
            return "{super=" + super.toString() + ", mInterfaceName=" + this.mInterfaceName + ", mUnderlyingNetwork=" + this.mUnderlyingNetwork + ", mLocalAddress=" + this.mLocalAddress + ", mRemoteAddress=" + this.mRemoteAddress + ", mIkey=" + this.mIkey + ", mOkey=" + this.mOkey + "}";
        }
    }

    private final class EncapSocketRecord extends OwnedResourceRecord {
        private final int mPort;
        private FileDescriptor mSocket;

        EncapSocketRecord(int resourceId, FileDescriptor socket, int port) {
            super(resourceId);
            this.mSocket = socket;
            this.mPort = port;
        }

        public void freeUnderlyingResources() {
            Log.d(IpSecService.TAG, "Closing port " + this.mPort);
            IoUtils.closeQuietly(this.mSocket);
            this.mSocket = null;
            getResourceTracker().give();
        }

        public int getPort() {
            return this.mPort;
        }

        public FileDescriptor getFileDescriptor() {
            return this.mSocket;
        }

        /* access modifiers changed from: protected */
        public ResourceTracker getResourceTracker() {
            return getUserRecord().mSocketQuotaTracker;
        }

        public void invalidate() {
            getUserRecord().removeEncapSocketRecord(this.mResourceId);
        }

        public String toString() {
            return "{super=" + super.toString() + ", mSocket=" + this.mSocket + ", mPort=" + this.mPort + "}";
        }
    }

    private IpSecService(Context context) {
        this(context, IpSecServiceConfiguration.GETSRVINSTANCE);
    }

    static IpSecService create(Context context) throws InterruptedException {
        IpSecService service = new IpSecService(context);
        service.connectNativeNetdService();
        return service;
    }

    private AppOpsManager getAppOpsManager() {
        AppOpsManager appOps = (AppOpsManager) this.mContext.getSystemService("appops");
        if (appOps != null) {
            return appOps;
        }
        throw new RuntimeException("System Server couldn't get AppOps");
    }

    @VisibleForTesting
    public IpSecService(Context context, IpSecServiceConfiguration config) {
        this(context, config, $$Lambda$IpSecService$AnqunmSwm_yQvDDEPggokhVs5M.INSTANCE);
    }

    static /* synthetic */ void lambda$new$0(FileDescriptor fd, int uid) throws IOException {
        try {
            TrafficStats.setThreadStatsUid(uid);
            TrafficStats.tagFileDescriptor(fd);
        } finally {
            TrafficStats.clearThreadStatsUid();
        }
    }

    @VisibleForTesting
    public IpSecService(Context context, IpSecServiceConfiguration config, UidFdTagger uidFdTagger) {
        this.mNextResourceId = 1;
        this.mUserResourceTracker = new UserResourceTracker();
        this.mTunnelNetIds = new SparseBooleanArray();
        this.mNextTunnelNetIdIndex = 0;
        this.mContext = context;
        this.mSrvConfig = config;
        this.mUidFdTagger = uidFdTagger;
    }

    public void systemReady() {
        if (isNetdAlive()) {
            Slog.d(TAG, "IpSecService is ready");
        } else {
            Slog.wtf(TAG, "IpSecService not ready: failed to connect to NetD Native Service!");
        }
    }

    private void connectNativeNetdService() {
        new Thread() {
            public void run() {
                synchronized (IpSecService.this) {
                    NetdService.get(5000);
                }
            }
        }.start();
    }

    /* Debug info: failed to restart local var, previous not found, register: 2 */
    /* access modifiers changed from: package-private */
    /* JADX WARNING: Exception block dominator not found, dom blocks: [] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public synchronized boolean isNetdAlive() {
        /*
            r2 = this;
            monitor-enter(r2)
            r0 = 0
            com.android.server.IpSecService$IpSecServiceConfiguration r1 = r2.mSrvConfig     // Catch:{ RemoteException -> 0x0017, all -> 0x0014 }
            android.net.INetd r1 = r1.getNetdInstance()     // Catch:{ RemoteException -> 0x0012, all -> 0x0014 }
            if (r1 != 0) goto L_0x000c
            monitor-exit(r2)
            return r0
        L_0x000c:
            boolean r0 = r1.isAlive()     // Catch:{ RemoteException -> 0x0012, all -> 0x0014 }
            monitor-exit(r2)
            return r0
        L_0x0012:
            r1 = move-exception
            goto L_0x0018
        L_0x0014:
            r0 = move-exception
            monitor-exit(r2)
            throw r0
        L_0x0017:
            r1 = move-exception
        L_0x0018:
            monitor-exit(r2)
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.IpSecService.isNetdAlive():boolean");
    }

    private static void checkInetAddress(String inetAddress) {
        if (TextUtils.isEmpty(inetAddress)) {
            throw new IllegalArgumentException("Unspecified address");
        } else if (NetworkUtils.numericToInetAddress(inetAddress).isAnyLocalAddress()) {
            throw new IllegalArgumentException("Inappropriate wildcard address: " + inetAddress);
        }
    }

    private static void checkDirection(int direction) {
        if (direction != 0 && direction != 1) {
            throw new IllegalArgumentException("Invalid Direction: " + direction);
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:45:0x00a8 A[Catch:{ ServiceSpecificException -> 0x009e, RemoteException -> 0x0095 }] */
    /* JADX WARNING: Removed duplicated region for block: B:48:0x00b1 A[SYNTHETIC, Splitter:B:48:0x00b1] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public synchronized android.net.IpSecSpiResponse allocateSecurityParameterIndex(java.lang.String r17, int r18, android.os.IBinder r19) throws android.os.RemoteException {
        /*
            r16 = this;
            r7 = r16
            r8 = r18
            r9 = r19
            monitor-enter(r16)
            checkInetAddress(r17)     // Catch:{ all -> 0x00b2 }
            if (r8 <= 0) goto L_0x0019
            r0 = 256(0x100, float:3.59E-43)
            if (r8 < r0) goto L_0x0011
            goto L_0x0019
        L_0x0011:
            java.lang.IllegalArgumentException r0 = new java.lang.IllegalArgumentException     // Catch:{ all -> 0x00b2 }
            java.lang.String r1 = "ESP SPI must not be in the range of 0-255."
            r0.<init>(r1)     // Catch:{ all -> 0x00b2 }
            throw r0     // Catch:{ all -> 0x00b2 }
        L_0x0019:
            java.lang.String r0 = "Null Binder passed to allocateSecurityParameterIndex"
            com.android.internal.util.Preconditions.checkNotNull(r9, r0)     // Catch:{ all -> 0x00b2 }
            int r0 = android.os.Binder.getCallingUid()     // Catch:{ all -> 0x00b2 }
            r10 = r0
            com.android.server.IpSecService$UserResourceTracker r0 = r7.mUserResourceTracker     // Catch:{ all -> 0x00b2 }
            com.android.server.IpSecService$UserRecord r0 = r0.getUserRecord(r10)     // Catch:{ all -> 0x00b2 }
            r11 = r0
            int r0 = r7.mNextResourceId     // Catch:{ all -> 0x00b2 }
            int r1 = r0 + 1
            r7.mNextResourceId = r1     // Catch:{ all -> 0x00b2 }
            r12 = r0
            r1 = 0
            r13 = -1
            com.android.server.IpSecService$ResourceTracker r0 = r11.mSpiQuotaTracker     // Catch:{ ServiceSpecificException -> 0x009e, RemoteException -> 0x0095 }
            boolean r0 = r0.isAvailable()     // Catch:{ ServiceSpecificException -> 0x009e, RemoteException -> 0x0095 }
            if (r0 != 0) goto L_0x0043
            android.net.IpSecSpiResponse r0 = new android.net.IpSecSpiResponse     // Catch:{ ServiceSpecificException -> 0x009e, RemoteException -> 0x0095 }
            r2 = 1
            r0.<init>(r2, r13, r1)     // Catch:{ ServiceSpecificException -> 0x009e, RemoteException -> 0x0095 }
            monitor-exit(r16)
            return r0
        L_0x0043:
            com.android.server.IpSecService$IpSecServiceConfiguration r0 = r7.mSrvConfig     // Catch:{ ServiceSpecificException -> 0x009e, RemoteException -> 0x0095 }
            android.net.INetd r0 = r0.getNetdInstance()     // Catch:{ ServiceSpecificException -> 0x009e, RemoteException -> 0x0095 }
            java.lang.String r2 = ""
            r14 = r17
            int r0 = r0.ipSecAllocateSpi(r10, r2, r14, r8)     // Catch:{ ServiceSpecificException -> 0x0093, RemoteException -> 0x0091 }
            r15 = r0
            java.lang.String r0 = "IpSecService"
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ ServiceSpecificException -> 0x008f, RemoteException -> 0x008d }
            r1.<init>()     // Catch:{ ServiceSpecificException -> 0x008f, RemoteException -> 0x008d }
            java.lang.String r2 = "Allocated SPI "
            r1.append(r2)     // Catch:{ ServiceSpecificException -> 0x008f, RemoteException -> 0x008d }
            r1.append(r15)     // Catch:{ ServiceSpecificException -> 0x008f, RemoteException -> 0x008d }
            java.lang.String r1 = r1.toString()     // Catch:{ ServiceSpecificException -> 0x008f, RemoteException -> 0x008d }
            android.util.Log.d(r0, r1)     // Catch:{ ServiceSpecificException -> 0x008f, RemoteException -> 0x008d }
            com.android.server.IpSecService$RefcountedResourceArray<com.android.server.IpSecService$SpiRecord> r0 = r11.mSpiRecords     // Catch:{ ServiceSpecificException -> 0x008f, RemoteException -> 0x008d }
            com.android.server.IpSecService$RefcountedResource r6 = new com.android.server.IpSecService$RefcountedResource     // Catch:{ ServiceSpecificException -> 0x008f, RemoteException -> 0x008d }
            com.android.server.IpSecService$SpiRecord r5 = new com.android.server.IpSecService$SpiRecord     // Catch:{ ServiceSpecificException -> 0x008f, RemoteException -> 0x008d }
            java.lang.String r4 = ""
            r1 = r5
            r2 = r16
            r3 = r12
            r13 = r5
            r5 = r17
            r8 = r6
            r6 = r15
            r1.<init>(r3, r4, r5, r6)     // Catch:{ ServiceSpecificException -> 0x008f, RemoteException -> 0x008d }
            r1 = 0
            com.android.server.IpSecService$RefcountedResource[] r2 = new com.android.server.IpSecService.RefcountedResource[r1]     // Catch:{ ServiceSpecificException -> 0x008f, RemoteException -> 0x008d }
            r8.<init>(r13, r9, r2)     // Catch:{ ServiceSpecificException -> 0x008f, RemoteException -> 0x008d }
            r0.put(r12, r8)     // Catch:{ ServiceSpecificException -> 0x008f, RemoteException -> 0x008d }
            android.net.IpSecSpiResponse r0 = new android.net.IpSecSpiResponse     // Catch:{ all -> 0x00b2 }
            r0.<init>(r1, r12, r15)     // Catch:{ all -> 0x00b2 }
            monitor-exit(r16)
            return r0
        L_0x008d:
            r0 = move-exception
            goto L_0x0099
        L_0x008f:
            r0 = move-exception
            goto L_0x00a2
        L_0x0091:
            r0 = move-exception
            goto L_0x0098
        L_0x0093:
            r0 = move-exception
            goto L_0x00a1
        L_0x0095:
            r0 = move-exception
            r14 = r17
        L_0x0098:
            r15 = r1
        L_0x0099:
            java.lang.RuntimeException r1 = r0.rethrowFromSystemServer()     // Catch:{ all -> 0x00b2 }
            throw r1     // Catch:{ all -> 0x00b2 }
        L_0x009e:
            r0 = move-exception
            r14 = r17
        L_0x00a1:
            r15 = r1
        L_0x00a2:
            int r1 = r0.errorCode     // Catch:{ all -> 0x00b2 }
            int r2 = android.system.OsConstants.ENOENT     // Catch:{ all -> 0x00b2 }
            if (r1 != r2) goto L_0x00b1
            android.net.IpSecSpiResponse r1 = new android.net.IpSecSpiResponse     // Catch:{ all -> 0x00b2 }
            r2 = 2
            r3 = -1
            r1.<init>(r2, r3, r15)     // Catch:{ all -> 0x00b2 }
            monitor-exit(r16)
            return r1
        L_0x00b1:
            throw r0     // Catch:{ all -> 0x00b2 }
        L_0x00b2:
            r0 = move-exception
            monitor-exit(r16)
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.IpSecService.allocateSecurityParameterIndex(java.lang.String, int, android.os.IBinder):android.net.IpSecSpiResponse");
    }

    private void releaseResource(RefcountedResourceArray resArray, int resourceId) throws RemoteException {
        resArray.getRefcountedResourceOrThrow(resourceId).userRelease();
    }

    public synchronized void releaseSecurityParameterIndex(int resourceId) throws RemoteException {
        releaseResource(this.mUserResourceTracker.getUserRecord(Binder.getCallingUid()).mSpiRecords, resourceId);
    }

    private int bindToRandomPort(FileDescriptor sockFd) throws IOException {
        int i = 10;
        while (i > 0) {
            try {
                FileDescriptor probeSocket = Os.socket(OsConstants.AF_INET, OsConstants.SOCK_DGRAM, OsConstants.IPPROTO_UDP);
                Os.bind(probeSocket, INADDR_ANY, 0);
                int port = ((InetSocketAddress) Os.getsockname(probeSocket)).getPort();
                Os.close(probeSocket);
                Log.v(TAG, "Binding to port " + port);
                Os.bind(sockFd, INADDR_ANY, port);
                return port;
            } catch (ErrnoException e) {
                if (e.errno == OsConstants.EADDRINUSE) {
                    i--;
                } else {
                    throw e.rethrowAsIOException();
                }
            }
        }
        throw new IOException("Failed 10 attempts to bind to a port");
    }

    public synchronized IpSecUdpEncapResponse openUdpEncapsulationSocket(int port, IBinder binder) throws RemoteException {
        if (port == 0 || (port >= 1024 && port <= PORT_MAX)) {
            Preconditions.checkNotNull(binder, "Null Binder passed to openUdpEncapsulationSocket");
            int callingUid = Binder.getCallingUid();
            UserRecord userRecord = this.mUserResourceTracker.getUserRecord(callingUid);
            int resourceId = this.mNextResourceId;
            this.mNextResourceId = resourceId + 1;
            try {
                if (!userRecord.mSocketQuotaTracker.isAvailable()) {
                    return new IpSecUdpEncapResponse(1);
                }
                FileDescriptor sockFd = Os.socket(OsConstants.AF_INET, OsConstants.SOCK_DGRAM, OsConstants.IPPROTO_UDP);
                this.mUidFdTagger.tag(sockFd, callingUid);
                Os.setsockoptInt(sockFd, OsConstants.IPPROTO_UDP, OsConstants.UDP_ENCAP, OsConstants.UDP_ENCAP_ESPINUDP);
                this.mSrvConfig.getNetdInstance().ipSecSetEncapSocketOwner(new ParcelFileDescriptor(sockFd), callingUid);
                if (port != 0) {
                    Log.v(TAG, "Binding to port " + port);
                    Os.bind(sockFd, INADDR_ANY, port);
                } else {
                    port = bindToRandomPort(sockFd);
                }
                userRecord.mEncapSocketRecords.put(resourceId, new RefcountedResource(new EncapSocketRecord(resourceId, sockFd, port), binder, new RefcountedResource[0]));
                return new IpSecUdpEncapResponse(0, resourceId, port, sockFd);
            } catch (ErrnoException | IOException e) {
                IoUtils.closeQuietly((FileDescriptor) null);
                return new IpSecUdpEncapResponse(1);
            }
        } else {
            throw new IllegalArgumentException("Specified port number must be a valid non-reserved UDP port");
        }
    }

    public synchronized void closeUdpEncapsulationSocket(int resourceId) throws RemoteException {
        releaseResource(this.mUserResourceTracker.getUserRecord(Binder.getCallingUid()).mEncapSocketRecords, resourceId);
    }

    /* Debug info: failed to restart local var, previous not found, register: 33 */
    /* JADX WARNING: Code restructure failed: missing block: B:70:0x019a, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:71:0x019b, code lost:
        r3 = r5;
        r29 = r7;
        r30 = r8;
        r2 = r9;
        r16 = r10;
        r32 = r15;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:76:0x01b0, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:77:0x01b1, code lost:
        r3 = r5;
        r4 = r7;
        r1 = r8;
        r2 = r9;
        r16 = r10;
        r32 = r15;
     */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Removed duplicated region for block: B:70:0x019a A[ExcHandler: all (th java.lang.Throwable), Splitter:B:12:0x005f] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public synchronized android.net.IpSecTunnelInterfaceResponse createTunnelInterface(java.lang.String r34, java.lang.String r35, android.net.Network r36, android.os.IBinder r37, java.lang.String r38) {
        /*
            r33 = this;
            r11 = r33
            r12 = r37
            monitor-enter(r33)
            r13 = r38
            r11.enforceTunnelFeatureAndPermissions(r13)     // Catch:{ all -> 0x01c4 }
            java.lang.String r0 = "Null Binder passed to createTunnelInterface"
            com.android.internal.util.Preconditions.checkNotNull(r12, r0)     // Catch:{ all -> 0x01c4 }
            java.lang.String r0 = "No underlying network was specified"
            r14 = r36
            com.android.internal.util.Preconditions.checkNotNull(r14, r0)     // Catch:{ all -> 0x01c4 }
            checkInetAddress(r34)     // Catch:{ all -> 0x01c4 }
            checkInetAddress(r35)     // Catch:{ all -> 0x01c4 }
            int r0 = android.os.Binder.getCallingUid()     // Catch:{ all -> 0x01c4 }
            r15 = r0
            com.android.server.IpSecService$UserResourceTracker r0 = r11.mUserResourceTracker     // Catch:{ all -> 0x01c4 }
            com.android.server.IpSecService$UserRecord r0 = r0.getUserRecord(r15)     // Catch:{ all -> 0x01c4 }
            r10 = r0
            com.android.server.IpSecService$ResourceTracker r0 = r10.mTunnelQuotaTracker     // Catch:{ all -> 0x01c4 }
            boolean r0 = r0.isAvailable()     // Catch:{ all -> 0x01c4 }
            r1 = 1
            if (r0 != 0) goto L_0x0038
            android.net.IpSecTunnelInterfaceResponse r0 = new android.net.IpSecTunnelInterfaceResponse     // Catch:{ all -> 0x01c4 }
            r0.<init>(r1)     // Catch:{ all -> 0x01c4 }
            monitor-exit(r33)
            return r0
        L_0x0038:
            int r0 = r11.mNextResourceId     // Catch:{ all -> 0x01c4 }
            int r2 = r0 + 1
            r11.mNextResourceId = r2     // Catch:{ all -> 0x01c4 }
            r9 = r0
            int r0 = r33.reserveNetId()     // Catch:{ all -> 0x01c4 }
            r8 = r0
            int r0 = r33.reserveNetId()     // Catch:{ all -> 0x01c4 }
            r7 = r0
            java.lang.String r0 = "%s%d"
            r2 = 2
            java.lang.Object[] r2 = new java.lang.Object[r2]     // Catch:{ all -> 0x01c4 }
            java.lang.String r3 = "ipsec"
            r6 = 0
            r2[r6] = r3     // Catch:{ all -> 0x01c4 }
            java.lang.Integer r3 = java.lang.Integer.valueOf(r9)     // Catch:{ all -> 0x01c4 }
            r2[r1] = r3     // Catch:{ all -> 0x01c4 }
            java.lang.String r0 = java.lang.String.format(r0, r2)     // Catch:{ all -> 0x01c4 }
            r5 = r0
            com.android.server.IpSecService$IpSecServiceConfiguration r0 = r11.mSrvConfig     // Catch:{ RemoteException -> 0x01b0, all -> 0x019a }
            android.net.INetd r25 = r0.getNetdInstance()     // Catch:{ RemoteException -> 0x0190, all -> 0x019a }
            r26 = r5
            r27 = r34
            r28 = r35
            r29 = r8
            r30 = r7
            r31 = r9
            r25.ipSecAddTunnelInterface(r26, r27, r28, r29, r30, r31)     // Catch:{ RemoteException -> 0x0190, all -> 0x019a }
            int[] r0 = ADDRESS_FAMILIES     // Catch:{ RemoteException -> 0x0190, all -> 0x019a }
            int r4 = r0.length     // Catch:{ RemoteException -> 0x0190, all -> 0x019a }
            r2 = r6
        L_0x0078:
            if (r2 >= r4) goto L_0x0117
            r3 = r0[r2]     // Catch:{ RemoteException -> 0x010c, all -> 0x00ff }
            r16 = 1
            r17 = 0
            r18 = -1
            r1 = r25
            r26 = r2
            r2 = r15
            r27 = r4
            r4 = r16
            r28 = r5
            r5 = r34
            r6 = r35
            r29 = r7
            r7 = r17
            r30 = r8
            r8 = r29
            r31 = r9
            r9 = r18
            r13 = r10
            r10 = r31
            r1.ipSecAddSecurityPolicy(r2, r3, r4, r5, r6, r7, r8, r9, r10)     // Catch:{ RemoteException -> 0x00f0, all -> 0x00e5 }
            r18 = 0
            r21 = 0
            r23 = -1
            r32 = r15
            r15 = r25
            r16 = r32
            r17 = r3
            r19 = r35
            r20 = r34
            r22 = r30
            r24 = r31
            r15.ipSecAddSecurityPolicy(r16, r17, r18, r19, r20, r21, r22, r23, r24)     // Catch:{ RemoteException -> 0x00d8, all -> 0x00cf }
            int r2 = r26 + 1
            r10 = r13
            r4 = r27
            r5 = r28
            r7 = r29
            r8 = r30
            r9 = r31
            r15 = r32
            r6 = 0
            r13 = r38
            goto L_0x0078
        L_0x00cf:
            r0 = move-exception
            r16 = r13
            r3 = r28
            r2 = r31
            goto L_0x01a5
        L_0x00d8:
            r0 = move-exception
            r16 = r13
            r3 = r28
            r4 = r29
            r1 = r30
            r2 = r31
            goto L_0x01b9
        L_0x00e5:
            r0 = move-exception
            r32 = r15
            r16 = r13
            r3 = r28
            r2 = r31
            goto L_0x01a5
        L_0x00f0:
            r0 = move-exception
            r32 = r15
            r16 = r13
            r3 = r28
            r4 = r29
            r1 = r30
            r2 = r31
            goto L_0x01b9
        L_0x00ff:
            r0 = move-exception
            r29 = r7
            r30 = r8
            r32 = r15
            r3 = r5
            r2 = r9
            r16 = r10
            goto L_0x01a5
        L_0x010c:
            r0 = move-exception
            r32 = r15
            r3 = r5
            r4 = r7
            r1 = r8
            r2 = r9
            r16 = r10
            goto L_0x01b9
        L_0x0117:
            r28 = r5
            r29 = r7
            r30 = r8
            r31 = r9
            r13 = r10
            r32 = r15
            com.android.server.IpSecService$RefcountedResourceArray<com.android.server.IpSecService$TunnelInterfaceRecord> r0 = r13.mTunnelInterfaceRecords     // Catch:{ RemoteException -> 0x0184, all -> 0x017c }
            com.android.server.IpSecService$RefcountedResource r15 = new com.android.server.IpSecService$RefcountedResource     // Catch:{ RemoteException -> 0x0184, all -> 0x017c }
            com.android.server.IpSecService$TunnelInterfaceRecord r10 = new com.android.server.IpSecService$TunnelInterfaceRecord     // Catch:{ RemoteException -> 0x0184, all -> 0x017c }
            r1 = r10
            r2 = r33
            r3 = r31
            r4 = r28
            r5 = r36
            r6 = r34
            r7 = r35
            r8 = r30
            r9 = r29
            r16 = r13
            r13 = r10
            r10 = r31
            r1.<init>(r3, r4, r5, r6, r7, r8, r9, r10)     // Catch:{ RemoteException -> 0x0172, all -> 0x016c }
            r1 = 0
            com.android.server.IpSecService$RefcountedResource[] r2 = new com.android.server.IpSecService.RefcountedResource[r1]     // Catch:{ RemoteException -> 0x0172, all -> 0x016c }
            r15.<init>(r13, r12, r2)     // Catch:{ RemoteException -> 0x0172, all -> 0x016c }
            r2 = r31
            r0.put(r2, r15)     // Catch:{ RemoteException -> 0x0163, all -> 0x015f }
            android.net.IpSecTunnelInterfaceResponse r0 = new android.net.IpSecTunnelInterfaceResponse     // Catch:{ RemoteException -> 0x0163, all -> 0x015f }
            r3 = r28
            r0.<init>(r1, r2, r3)     // Catch:{ RemoteException -> 0x0158, all -> 0x0155 }
            monitor-exit(r33)
            return r0
        L_0x0155:
            r0 = move-exception
            goto L_0x01a5
        L_0x0158:
            r0 = move-exception
            r4 = r29
            r1 = r30
            goto L_0x01b9
        L_0x015f:
            r0 = move-exception
            r3 = r28
            goto L_0x01a5
        L_0x0163:
            r0 = move-exception
            r3 = r28
            r4 = r29
            r1 = r30
            goto L_0x01b9
        L_0x016c:
            r0 = move-exception
            r3 = r28
            r2 = r31
            goto L_0x01a5
        L_0x0172:
            r0 = move-exception
            r3 = r28
            r2 = r31
            r4 = r29
            r1 = r30
            goto L_0x01b9
        L_0x017c:
            r0 = move-exception
            r16 = r13
            r3 = r28
            r2 = r31
            goto L_0x01a5
        L_0x0184:
            r0 = move-exception
            r16 = r13
            r3 = r28
            r2 = r31
            r4 = r29
            r1 = r30
            goto L_0x01b9
        L_0x0190:
            r0 = move-exception
            r3 = r5
            r2 = r9
            r16 = r10
            r32 = r15
            r4 = r7
            r1 = r8
            goto L_0x01b9
        L_0x019a:
            r0 = move-exception
            r3 = r5
            r29 = r7
            r30 = r8
            r2 = r9
            r16 = r10
            r32 = r15
        L_0x01a5:
            r1 = r30
            r11.releaseNetId(r1)     // Catch:{ all -> 0x01c4 }
            r4 = r29
            r11.releaseNetId(r4)     // Catch:{ all -> 0x01c4 }
            throw r0     // Catch:{ all -> 0x01c4 }
        L_0x01b0:
            r0 = move-exception
            r3 = r5
            r4 = r7
            r1 = r8
            r2 = r9
            r16 = r10
            r32 = r15
        L_0x01b9:
            r11.releaseNetId(r1)     // Catch:{ all -> 0x01c4 }
            r11.releaseNetId(r4)     // Catch:{ all -> 0x01c4 }
            java.lang.RuntimeException r5 = r0.rethrowFromSystemServer()     // Catch:{ all -> 0x01c4 }
            throw r5     // Catch:{ all -> 0x01c4 }
        L_0x01c4:
            r0 = move-exception
            monitor-exit(r33)
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.IpSecService.createTunnelInterface(java.lang.String, java.lang.String, android.net.Network, android.os.IBinder, java.lang.String):android.net.IpSecTunnelInterfaceResponse");
    }

    public synchronized void addAddressToTunnelInterface(int tunnelResourceId, LinkAddress localAddr, String callingPackage) {
        enforceTunnelFeatureAndPermissions(callingPackage);
        try {
            this.mSrvConfig.getNetdInstance().interfaceAddAddress(this.mUserResourceTracker.getUserRecord(Binder.getCallingUid()).mTunnelInterfaceRecords.getResourceOrThrow(tunnelResourceId).mInterfaceName, localAddr.getAddress().getHostAddress(), localAddr.getPrefixLength());
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public synchronized void removeAddressFromTunnelInterface(int tunnelResourceId, LinkAddress localAddr, String callingPackage) {
        enforceTunnelFeatureAndPermissions(callingPackage);
        try {
            this.mSrvConfig.getNetdInstance().interfaceDelAddress(this.mUserResourceTracker.getUserRecord(Binder.getCallingUid()).mTunnelInterfaceRecords.getResourceOrThrow(tunnelResourceId).mInterfaceName, localAddr.getAddress().getHostAddress(), localAddr.getPrefixLength());
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public synchronized void deleteTunnelInterface(int resourceId, String callingPackage) throws RemoteException {
        enforceTunnelFeatureAndPermissions(callingPackage);
        releaseResource(this.mUserResourceTracker.getUserRecord(Binder.getCallingUid()).mTunnelInterfaceRecords, resourceId);
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public void validateAlgorithms(IpSecConfig config) throws IllegalArgumentException {
        IpSecAlgorithm auth = config.getAuthentication();
        IpSecAlgorithm crypt = config.getEncryption();
        IpSecAlgorithm aead = config.getAuthenticatedEncryption();
        boolean z = false;
        Preconditions.checkArgument((aead == null && crypt == null && auth == null) ? false : true, "No Encryption or Authentication algorithms specified");
        Preconditions.checkArgument(auth == null || auth.isAuthentication(), "Unsupported algorithm for Authentication");
        Preconditions.checkArgument(crypt == null || crypt.isEncryption(), "Unsupported algorithm for Encryption");
        Preconditions.checkArgument(aead == null || aead.isAead(), "Unsupported algorithm for Authenticated Encryption");
        if (aead == null || (auth == null && crypt == null)) {
            z = true;
        }
        Preconditions.checkArgument(z, "Authenticated Encryption is mutually exclusive with other Authentication or Encryption algorithms");
    }

    private int getFamily(String inetAddress) {
        int family = OsConstants.AF_UNSPEC;
        InetAddress checkAddress = NetworkUtils.numericToInetAddress(inetAddress);
        if (checkAddress instanceof Inet4Address) {
            return OsConstants.AF_INET;
        }
        if (checkAddress instanceof Inet6Address) {
            return OsConstants.AF_INET6;
        }
        return family;
    }

    private void checkIpSecConfig(IpSecConfig config) {
        UserRecord userRecord = this.mUserResourceTracker.getUserRecord(Binder.getCallingUid());
        int encapType = config.getEncapType();
        if (encapType != 0) {
            if (encapType == 1 || encapType == 2) {
                userRecord.mEncapSocketRecords.getResourceOrThrow(config.getEncapSocketResourceId());
                int port = config.getEncapRemotePort();
                if (port <= 0 || port > PORT_MAX) {
                    throw new IllegalArgumentException("Invalid remote UDP port: " + port);
                }
            } else {
                throw new IllegalArgumentException("Invalid Encap Type: " + config.getEncapType());
            }
        }
        validateAlgorithms(config);
        SpiRecord s = userRecord.mSpiRecords.getResourceOrThrow(config.getSpiResourceId());
        if (!s.getOwnedByTransform()) {
            if (TextUtils.isEmpty(config.getDestinationAddress())) {
                config.setDestinationAddress(s.getDestinationAddress());
            }
            if (config.getDestinationAddress().equals(s.getDestinationAddress())) {
                checkInetAddress(config.getDestinationAddress());
                checkInetAddress(config.getSourceAddress());
                String sourceAddress = config.getSourceAddress();
                String destinationAddress = config.getDestinationAddress();
                int sourceFamily = getFamily(sourceAddress);
                if (sourceFamily != getFamily(destinationAddress)) {
                    throw new IllegalArgumentException("Source address (" + sourceAddress + ") and destination address (" + destinationAddress + ") have different address families.");
                } else if (config.getEncapType() == 0 || sourceFamily == OsConstants.AF_INET) {
                    int mode = config.getMode();
                    if (mode == 0 || mode == 1) {
                        config.setMarkValue(0);
                        config.setMarkMask(0);
                        return;
                    }
                    throw new IllegalArgumentException("Invalid IpSecTransform.mode: " + config.getMode());
                } else {
                    throw new IllegalArgumentException("UDP Encapsulation is not supported for this address family");
                }
            } else {
                throw new IllegalArgumentException("Mismatched remote addresseses.");
            }
        } else {
            throw new IllegalStateException("SPI already in use; cannot be used in new Transforms");
        }
    }

    private void enforceTunnelFeatureAndPermissions(String callingPackage) {
        if (this.mContext.getPackageManager().hasSystemFeature("android.software.ipsec_tunnels")) {
            Preconditions.checkNotNull(callingPackage, "Null calling package cannot create IpSec tunnels");
            int noteOp = getAppOpsManager().noteOp(TUNNEL_OP, Binder.getCallingUid(), callingPackage);
            if (noteOp == 0) {
                return;
            }
            if (noteOp == 3) {
                this.mContext.enforceCallingOrSelfPermission("android.permission.MANAGE_IPSEC_TUNNELS", TAG);
                return;
            }
            throw new SecurityException("Request to ignore AppOps for non-legacy API");
        }
        throw new UnsupportedOperationException("IPsec Tunnel Mode requires PackageManager.FEATURE_IPSEC_TUNNELS");
    }

    private void createOrUpdateTransform(IpSecConfig c, int resourceId, SpiRecord spiRecord, EncapSocketRecord socketRecord) throws RemoteException {
        int encapRemotePort;
        int encapLocalPort;
        String cryptName;
        int encapType = c.getEncapType();
        if (encapType != 0) {
            encapLocalPort = socketRecord.getPort();
            encapRemotePort = c.getEncapRemotePort();
        } else {
            encapLocalPort = 0;
            encapRemotePort = 0;
        }
        IpSecAlgorithm auth = c.getAuthentication();
        IpSecAlgorithm crypt = c.getEncryption();
        IpSecAlgorithm authCrypt = c.getAuthenticatedEncryption();
        String str = "";
        if (crypt == null) {
            cryptName = authCrypt == null ? "ecb(cipher_null)" : str;
        } else {
            cryptName = crypt.getName();
        }
        INetd netdInstance = this.mSrvConfig.getNetdInstance();
        int callingUid = Binder.getCallingUid();
        int mode = c.getMode();
        String sourceAddress = c.getSourceAddress();
        String destinationAddress = c.getDestinationAddress();
        int i = c.getNetwork() != null ? c.getNetwork().netId : 0;
        int spi = spiRecord.getSpi();
        int markValue = c.getMarkValue();
        int markMask = c.getMarkMask();
        String name = auth != null ? auth.getName() : str;
        byte[] key = auth != null ? auth.getKey() : new byte[0];
        int truncationLengthBits = auth != null ? auth.getTruncationLengthBits() : 0;
        byte[] key2 = crypt != null ? crypt.getKey() : new byte[0];
        int truncationLengthBits2 = crypt != null ? crypt.getTruncationLengthBits() : 0;
        if (authCrypt != null) {
            str = authCrypt.getName();
        }
        netdInstance.ipSecAddSecurityAssociation(callingUid, mode, sourceAddress, destinationAddress, i, spi, markValue, markMask, name, key, truncationLengthBits, cryptName, key2, truncationLengthBits2, str, authCrypt != null ? authCrypt.getKey() : new byte[0], authCrypt != null ? authCrypt.getTruncationLengthBits() : 0, encapType, encapLocalPort, encapRemotePort, c.getXfrmInterfaceId());
    }

    public synchronized IpSecTransformResponse createTransform(IpSecConfig c, IBinder binder, String callingPackage) throws RemoteException {
        EncapSocketRecord socketRecord;
        IBinder iBinder = binder;
        synchronized (this) {
            Preconditions.checkNotNull(c);
            if (c.getMode() == 1) {
                enforceTunnelFeatureAndPermissions(callingPackage);
            } else {
                String str = callingPackage;
            }
            checkIpSecConfig(c);
            Preconditions.checkNotNull(iBinder, "Null Binder passed to createTransform");
            int i = this.mNextResourceId;
            this.mNextResourceId = i + 1;
            int resourceId = i;
            UserRecord userRecord = this.mUserResourceTracker.getUserRecord(Binder.getCallingUid());
            List<RefcountedResource> dependencies = new ArrayList<>();
            if (!userRecord.mTransformQuotaTracker.isAvailable()) {
                IpSecTransformResponse ipSecTransformResponse = new IpSecTransformResponse(1);
                return ipSecTransformResponse;
            }
            if (c.getEncapType() != 0) {
                RefcountedResource<EncapSocketRecord> refcountedSocketRecord = userRecord.mEncapSocketRecords.getRefcountedResourceOrThrow(c.getEncapSocketResourceId());
                dependencies.add(refcountedSocketRecord);
                socketRecord = refcountedSocketRecord.getResource();
            } else {
                socketRecord = null;
            }
            RefcountedResource<SpiRecord> refcountedSpiRecord = userRecord.mSpiRecords.getRefcountedResourceOrThrow(c.getSpiResourceId());
            dependencies.add(refcountedSpiRecord);
            SpiRecord spiRecord = refcountedSpiRecord.getResource();
            createOrUpdateTransform(c, resourceId, spiRecord, socketRecord);
            TransformRecord transformRecord = r1;
            UserRecord userRecord2 = userRecord;
            RefcountedResource<SpiRecord> refcountedResource = refcountedSpiRecord;
            RefcountedResourceArray<TransformRecord> refcountedResourceArray = userRecord.mTransformRecords;
            TransformRecord transformRecord2 = new TransformRecord(resourceId, c, spiRecord, socketRecord);
            refcountedResourceArray.put(resourceId, new RefcountedResource(transformRecord, iBinder, (RefcountedResource[]) dependencies.toArray(new RefcountedResource[dependencies.size()])));
            IpSecTransformResponse ipSecTransformResponse2 = new IpSecTransformResponse(0, resourceId);
            return ipSecTransformResponse2;
        }
    }

    public synchronized void deleteTransform(int resourceId) throws RemoteException {
        releaseResource(this.mUserResourceTracker.getUserRecord(Binder.getCallingUid()).mTransformRecords, resourceId);
    }

    public synchronized void applyTransportModeTransform(ParcelFileDescriptor socket, int direction, int resourceId) throws RemoteException {
        int callingUid = Binder.getCallingUid();
        checkDirection(direction);
        TransformRecord info = this.mUserResourceTracker.getUserRecord(callingUid).mTransformRecords.getResourceOrThrow(resourceId);
        if (info.pid == getCallingPid() && info.uid == callingUid) {
            IpSecConfig c = info.getConfig();
            Preconditions.checkArgument(c.getMode() == 0, "Transform mode was not Transport mode; cannot be applied to a socket");
            this.mSrvConfig.getNetdInstance().ipSecApplyTransportModeTransform(socket, callingUid, direction, c.getSourceAddress(), c.getDestinationAddress(), info.getSpiRecord().getSpi());
        } else {
            throw new SecurityException("Only the owner of an IpSec Transform may apply it!");
        }
    }

    public synchronized void removeTransportModeTransforms(ParcelFileDescriptor socket) throws RemoteException {
        this.mSrvConfig.getNetdInstance().ipSecRemoveTransportModeTransform(socket);
    }

    /* JADX WARNING: Removed duplicated region for block: B:50:0x0119  */
    /* JADX WARNING: Removed duplicated region for block: B:52:0x0123  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public synchronized void applyTunnelModeTransform(int r28, int r29, int r30, java.lang.String r31) throws android.os.RemoteException {
        /*
            r27 = this;
            r1 = r27
            r12 = r29
            r13 = r30
            monitor-enter(r27)
            r14 = r31
            r1.enforceTunnelFeatureAndPermissions(r14)     // Catch:{ all -> 0x0124 }
            checkDirection(r29)     // Catch:{ all -> 0x0124 }
            int r0 = android.os.Binder.getCallingUid()     // Catch:{ all -> 0x0124 }
            r15 = r0
            com.android.server.IpSecService$UserResourceTracker r0 = r1.mUserResourceTracker     // Catch:{ all -> 0x0124 }
            com.android.server.IpSecService$UserRecord r0 = r0.getUserRecord(r15)     // Catch:{ all -> 0x0124 }
            r11 = r0
            com.android.server.IpSecService$RefcountedResourceArray<com.android.server.IpSecService$TransformRecord> r0 = r11.mTransformRecords     // Catch:{ all -> 0x0124 }
            com.android.server.IpSecService$IResource r0 = r0.getResourceOrThrow(r13)     // Catch:{ all -> 0x0124 }
            com.android.server.IpSecService$TransformRecord r0 = (com.android.server.IpSecService.TransformRecord) r0     // Catch:{ all -> 0x0124 }
            r16 = r0
            com.android.server.IpSecService$RefcountedResourceArray<com.android.server.IpSecService$TunnelInterfaceRecord> r0 = r11.mTunnelInterfaceRecords     // Catch:{ all -> 0x0124 }
            r10 = r28
            com.android.server.IpSecService$IResource r0 = r0.getResourceOrThrow(r10)     // Catch:{ all -> 0x0124 }
            com.android.server.IpSecService$TunnelInterfaceRecord r0 = (com.android.server.IpSecService.TunnelInterfaceRecord) r0     // Catch:{ all -> 0x0124 }
            r17 = r0
            android.net.IpSecConfig r0 = r16.getConfig()     // Catch:{ all -> 0x0124 }
            r8 = r0
            int r0 = r8.getMode()     // Catch:{ all -> 0x0124 }
            r2 = 0
            r3 = 1
            if (r0 != r3) goto L_0x0041
            r0 = r3
            goto L_0x0042
        L_0x0041:
            r0 = r2
        L_0x0042:
            java.lang.String r4 = "Transform mode was not Tunnel mode; cannot be applied to a tunnel interface"
            com.android.internal.util.Preconditions.checkArgument(r0, r4)     // Catch:{ all -> 0x0124 }
            r0 = 0
            int r4 = r8.getEncapType()     // Catch:{ all -> 0x0124 }
            if (r4 == 0) goto L_0x005d
            com.android.server.IpSecService$RefcountedResourceArray<com.android.server.IpSecService$EncapSocketRecord> r4 = r11.mEncapSocketRecords     // Catch:{ all -> 0x0124 }
            int r5 = r8.getEncapSocketResourceId()     // Catch:{ all -> 0x0124 }
            com.android.server.IpSecService$IResource r4 = r4.getResourceOrThrow(r5)     // Catch:{ all -> 0x0124 }
            com.android.server.IpSecService$EncapSocketRecord r4 = (com.android.server.IpSecService.EncapSocketRecord) r4     // Catch:{ all -> 0x0124 }
            r0 = r4
            r7 = r0
            goto L_0x005e
        L_0x005d:
            r7 = r0
        L_0x005e:
            com.android.server.IpSecService$RefcountedResourceArray<com.android.server.IpSecService$SpiRecord> r0 = r11.mSpiRecords     // Catch:{ all -> 0x0124 }
            int r4 = r8.getSpiResourceId()     // Catch:{ all -> 0x0124 }
            com.android.server.IpSecService$IResource r0 = r0.getResourceOrThrow(r4)     // Catch:{ all -> 0x0124 }
            com.android.server.IpSecService$SpiRecord r0 = (com.android.server.IpSecService.SpiRecord) r0     // Catch:{ all -> 0x0124 }
            r6 = r0
            if (r12 != r3) goto L_0x0073
            int r0 = r17.getOkey()     // Catch:{ all -> 0x0124 }
            r9 = r0
            goto L_0x0078
        L_0x0073:
            int r0 = r17.getIkey()     // Catch:{ all -> 0x0124 }
            r9 = r0
        L_0x0078:
            r0 = 0
            int r4 = r17.getIfId()     // Catch:{ ServiceSpecificException -> 0x010d }
            r8.setXfrmInterfaceId(r4)     // Catch:{ ServiceSpecificException -> 0x010d }
            if (r12 != r3) goto L_0x009c
            android.net.Network r3 = r17.getUnderlyingNetwork()     // Catch:{ ServiceSpecificException -> 0x0094 }
            r8.setNetwork(r3)     // Catch:{ ServiceSpecificException -> 0x0094 }
            com.android.server.IpSecService$SpiRecord r3 = r16.getSpiRecord()     // Catch:{ ServiceSpecificException -> 0x0094 }
            int r3 = r3.getSpi()     // Catch:{ ServiceSpecificException -> 0x0094 }
            r0 = r3
            goto L_0x009c
        L_0x0094:
            r0 = move-exception
            r2 = r6
            r3 = r7
            r12 = r8
            r19 = r11
            goto L_0x0113
        L_0x009c:
            int[] r5 = ADDRESS_FAMILIES     // Catch:{ ServiceSpecificException -> 0x010d }
            int r3 = r5.length     // Catch:{ ServiceSpecificException -> 0x010d }
        L_0x009f:
            if (r2 >= r3) goto L_0x00fa
            r4 = r5[r2]     // Catch:{ ServiceSpecificException -> 0x00f3 }
            r18 = r2
            com.android.server.IpSecService$IpSecServiceConfiguration r2 = r1.mSrvConfig     // Catch:{ ServiceSpecificException -> 0x00f3 }
            android.net.INetd r2 = r2.getNetdInstance()     // Catch:{ ServiceSpecificException -> 0x00f3 }
            android.net.IpSecConfig r19 = r16.getConfig()     // Catch:{ ServiceSpecificException -> 0x00f3 }
            java.lang.String r19 = r19.getSourceAddress()     // Catch:{ ServiceSpecificException -> 0x00f3 }
            android.net.IpSecConfig r20 = r16.getConfig()     // Catch:{ ServiceSpecificException -> 0x00f3 }
            java.lang.String r20 = r20.getDestinationAddress()     // Catch:{ ServiceSpecificException -> 0x00f3 }
            r21 = -1
            int r22 = r8.getXfrmInterfaceId()     // Catch:{ ServiceSpecificException -> 0x00f3 }
            r23 = r3
            r3 = r15
            r24 = r5
            r5 = r29
            r25 = r6
            r6 = r19
            r26 = r7
            r7 = r20
            r12 = r8
            r8 = r0
            r10 = r21
            r19 = r11
            r11 = r22
            r2.ipSecUpdateSecurityPolicy(r3, r4, r5, r6, r7, r8, r9, r10, r11)     // Catch:{ ServiceSpecificException -> 0x00ed }
            int r2 = r18 + 1
            r10 = r28
            r8 = r12
            r11 = r19
            r3 = r23
            r5 = r24
            r6 = r25
            r7 = r26
            r12 = r29
            goto L_0x009f
        L_0x00ed:
            r0 = move-exception
            r2 = r25
            r3 = r26
            goto L_0x0113
        L_0x00f3:
            r0 = move-exception
            r12 = r8
            r19 = r11
            r2 = r6
            r3 = r7
            goto L_0x0113
        L_0x00fa:
            r25 = r6
            r26 = r7
            r12 = r8
            r19 = r11
            r2 = r25
            r3 = r26
            r1.createOrUpdateTransform(r12, r13, r2, r3)     // Catch:{ ServiceSpecificException -> 0x010b }
            monitor-exit(r27)
            return
        L_0x010b:
            r0 = move-exception
            goto L_0x0113
        L_0x010d:
            r0 = move-exception
            r2 = r6
            r3 = r7
            r12 = r8
            r19 = r11
        L_0x0113:
            int r4 = r0.errorCode     // Catch:{ all -> 0x0124 }
            int r5 = android.system.OsConstants.EINVAL     // Catch:{ all -> 0x0124 }
            if (r4 != r5) goto L_0x0123
            java.lang.IllegalArgumentException r4 = new java.lang.IllegalArgumentException     // Catch:{ all -> 0x0124 }
            java.lang.String r5 = r0.toString()     // Catch:{ all -> 0x0124 }
            r4.<init>(r5)     // Catch:{ all -> 0x0124 }
            throw r4     // Catch:{ all -> 0x0124 }
        L_0x0123:
            throw r0     // Catch:{ all -> 0x0124 }
        L_0x0124:
            r0 = move-exception
            monitor-exit(r27)
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.IpSecService.applyTunnelModeTransform(int, int, int, java.lang.String):void");
    }

    /* access modifiers changed from: protected */
    public synchronized void dump(FileDescriptor fd, PrintWriter pw, String[] args) {
        this.mContext.enforceCallingOrSelfPermission("android.permission.DUMP", TAG);
        pw.println("IpSecService dump:");
        StringBuilder sb = new StringBuilder();
        sb.append("NetdNativeService Connection: ");
        sb.append(isNetdAlive() ? "alive" : "dead");
        pw.println(sb.toString());
        pw.println();
        pw.println("mUserResourceTracker:");
        pw.println(this.mUserResourceTracker);
    }
}
