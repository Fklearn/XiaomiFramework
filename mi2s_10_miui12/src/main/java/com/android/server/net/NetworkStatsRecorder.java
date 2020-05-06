package com.android.server.net;

import android.net.NetworkStats;
import android.net.NetworkTemplate;
import android.os.Binder;
import android.os.DropBoxManager;
import android.util.Log;
import android.util.MathUtils;
import android.util.proto.ProtoOutputStream;
import com.android.internal.net.VpnInfo;
import com.android.internal.util.FileRotator;
import com.android.internal.util.IndentingPrintWriter;
import com.android.internal.util.Preconditions;
import com.android.server.job.controllers.JobStatus;
import com.google.android.collect.Sets;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import libcore.io.IoUtils;

public class NetworkStatsRecorder {
    private static final boolean DUMP_BEFORE_DELETE = true;
    private static final boolean LOGD = false;
    private static final boolean LOGV = false;
    private static final String TAG = "NetworkStatsRecorder";
    private static final String TAG_NETSTATS_DUMP = "netstats_dump";
    private final long mBucketDuration;
    private WeakReference<NetworkStatsCollection> mComplete;
    private final String mCookie;
    private final DropBoxManager mDropBox;
    private NetworkStats mLastSnapshot;
    private final NetworkStats.NonMonotonicObserver<String> mObserver;
    private final boolean mOnlyTags;
    private final NetworkStatsCollection mPending;
    private final CombiningRewriter mPendingRewriter;
    private long mPersistThresholdBytes;
    private final FileRotator mRotator;
    private final NetworkStatsCollection mSinceBoot;

    public NetworkStatsRecorder() {
        this.mPersistThresholdBytes = 2097152;
        this.mRotator = null;
        this.mObserver = null;
        this.mDropBox = null;
        this.mCookie = null;
        this.mBucketDuration = 31449600000L;
        this.mOnlyTags = false;
        this.mPending = null;
        this.mSinceBoot = new NetworkStatsCollection(this.mBucketDuration);
        this.mPendingRewriter = null;
    }

    public NetworkStatsRecorder(FileRotator rotator, NetworkStats.NonMonotonicObserver<String> observer, DropBoxManager dropBox, String cookie, long bucketDuration, boolean onlyTags) {
        this.mPersistThresholdBytes = 2097152;
        this.mRotator = (FileRotator) Preconditions.checkNotNull(rotator, "missing FileRotator");
        this.mObserver = (NetworkStats.NonMonotonicObserver) Preconditions.checkNotNull(observer, "missing NonMonotonicObserver");
        this.mDropBox = (DropBoxManager) Preconditions.checkNotNull(dropBox, "missing DropBoxManager");
        this.mCookie = cookie;
        this.mBucketDuration = bucketDuration;
        this.mOnlyTags = onlyTags;
        this.mPending = new NetworkStatsCollection(bucketDuration);
        this.mSinceBoot = new NetworkStatsCollection(bucketDuration);
        this.mPendingRewriter = new CombiningRewriter(this.mPending);
    }

    public void setPersistThreshold(long thresholdBytes) {
        this.mPersistThresholdBytes = MathUtils.constrain(thresholdBytes, 1024, 104857600);
    }

    public void resetLocked() {
        this.mLastSnapshot = null;
        NetworkStatsCollection networkStatsCollection = this.mPending;
        if (networkStatsCollection != null) {
            networkStatsCollection.reset();
        }
        NetworkStatsCollection networkStatsCollection2 = this.mSinceBoot;
        if (networkStatsCollection2 != null) {
            networkStatsCollection2.reset();
        }
        WeakReference<NetworkStatsCollection> weakReference = this.mComplete;
        if (weakReference != null) {
            weakReference.clear();
        }
    }

    public NetworkStats.Entry getTotalSinceBootLocked(NetworkTemplate template) {
        return this.mSinceBoot.getSummary(template, Long.MIN_VALUE, JobStatus.NO_LATEST_RUNTIME, 3, Binder.getCallingUid()).getTotal((NetworkStats.Entry) null);
    }

    public NetworkStatsCollection getSinceBoot() {
        return this.mSinceBoot;
    }

    public NetworkStatsCollection getOrLoadCompleteLocked() {
        Preconditions.checkNotNull(this.mRotator, "missing FileRotator");
        WeakReference<NetworkStatsCollection> weakReference = this.mComplete;
        NetworkStatsCollection res = weakReference != null ? (NetworkStatsCollection) weakReference.get() : null;
        if (res != null) {
            return res;
        }
        NetworkStatsCollection res2 = loadLocked(Long.MIN_VALUE, JobStatus.NO_LATEST_RUNTIME);
        this.mComplete = new WeakReference<>(res2);
        return res2;
    }

    public NetworkStatsCollection getOrLoadPartialLocked(long start, long end) {
        Preconditions.checkNotNull(this.mRotator, "missing FileRotator");
        WeakReference<NetworkStatsCollection> weakReference = this.mComplete;
        NetworkStatsCollection res = weakReference != null ? (NetworkStatsCollection) weakReference.get() : null;
        if (res == null) {
            return loadLocked(start, end);
        }
        return res;
    }

    private NetworkStatsCollection loadLocked(long start, long end) {
        NetworkStatsCollection res = new NetworkStatsCollection(this.mBucketDuration);
        try {
            this.mRotator.readMatching(res, start, end);
            res.recordCollection(this.mPending);
        } catch (IOException e) {
            Log.wtf(TAG, "problem completely reading network stats", e);
            recoverFromWtf();
        } catch (OutOfMemoryError e2) {
            Log.wtf(TAG, "problem completely reading network stats", e2);
            recoverFromWtf();
        }
        return res;
    }

    public void recordSnapshotLocked(NetworkStats snapshot, Map<String, NetworkIdentitySet> ifaceIdent, VpnInfo[] vpnArray, long currentTimeMillis) {
        int i;
        NetworkStats.Entry entry;
        NetworkStats networkStats = snapshot;
        VpnInfo[] vpnInfoArr = vpnArray;
        HashSet<String> unknownIfaces = Sets.newHashSet();
        if (networkStats != null) {
            if (this.mLastSnapshot == null) {
                this.mLastSnapshot = networkStats;
                return;
            }
            WeakReference<NetworkStatsCollection> weakReference = this.mComplete;
            NetworkStatsCollection complete = weakReference != null ? (NetworkStatsCollection) weakReference.get() : null;
            NetworkStats delta = NetworkStats.subtract(networkStats, this.mLastSnapshot, this.mObserver, this.mCookie);
            long end = currentTimeMillis;
            long start = end - delta.getElapsedRealtime();
            if (vpnInfoArr != null) {
                for (VpnInfo info : vpnInfoArr) {
                    delta.migrateTun(info.ownerUid, info.vpnIface, info.primaryUnderlyingIface);
                }
            }
            NetworkStats.Entry entry2 = null;
            int i2 = 0;
            while (i2 < delta.size()) {
                NetworkStats.Entry entry3 = delta.getValues(i2, entry2);
                if (entry3.isNegative()) {
                    NetworkStats.NonMonotonicObserver<String> nonMonotonicObserver = this.mObserver;
                    if (nonMonotonicObserver != null) {
                        nonMonotonicObserver.foundNonMonotonic(delta, i2, this.mCookie);
                    }
                    entry3.rxBytes = Math.max(entry3.rxBytes, 0);
                    entry3.rxPackets = Math.max(entry3.rxPackets, 0);
                    entry3.txBytes = Math.max(entry3.txBytes, 0);
                    entry3.txPackets = Math.max(entry3.txPackets, 0);
                    entry3.operations = Math.max(entry3.operations, 0);
                }
                NetworkIdentitySet ident = ifaceIdent.get(entry3.iface);
                if (ident == null) {
                    unknownIfaces.add(entry3.iface);
                    entry = entry3;
                    i = i2;
                } else if (entry3.isEmpty()) {
                    entry = entry3;
                    i = i2;
                } else {
                    if ((entry3.tag == 0) != this.mOnlyTags) {
                        NetworkStatsCollection networkStatsCollection = this.mPending;
                        if (networkStatsCollection != null) {
                            entry = entry3;
                            i = i2;
                            networkStatsCollection.recordData(ident, entry3.uid, entry3.set, entry3.tag, start, end, entry);
                        } else {
                            entry = entry3;
                            i = i2;
                        }
                        NetworkStatsCollection networkStatsCollection2 = this.mSinceBoot;
                        if (networkStatsCollection2 != null) {
                            NetworkStats.Entry entry4 = entry;
                            networkStatsCollection2.recordData(ident, entry4.uid, entry4.set, entry4.tag, start, end, entry4);
                        }
                        if (complete != null) {
                            NetworkStats.Entry entry5 = entry;
                            complete.recordData(ident, entry5.uid, entry5.set, entry5.tag, start, end, entry5);
                        }
                    } else {
                        entry = entry3;
                        i = i2;
                    }
                }
                i2 = i + 1;
                entry2 = entry;
            }
            this.mLastSnapshot = networkStats;
        }
    }

    public void maybePersistLocked(long currentTimeMillis) {
        Preconditions.checkNotNull(this.mRotator, "missing FileRotator");
        if (this.mPending.getTotalBytes() >= this.mPersistThresholdBytes) {
            forcePersistLocked(currentTimeMillis);
        } else {
            this.mRotator.maybeRotate(currentTimeMillis);
        }
    }

    public void forcePersistLocked(long currentTimeMillis) {
        Preconditions.checkNotNull(this.mRotator, "missing FileRotator");
        if (this.mPending.isDirty()) {
            try {
                this.mRotator.rewriteActive(this.mPendingRewriter, currentTimeMillis);
                this.mRotator.maybeRotate(currentTimeMillis);
                this.mPending.reset();
            } catch (IOException e) {
                Log.wtf(TAG, "problem persisting pending stats", e);
                recoverFromWtf();
            } catch (OutOfMemoryError e2) {
                Log.wtf(TAG, "problem persisting pending stats", e2);
                recoverFromWtf();
            } catch (IllegalArgumentException e3) {
                Log.wtf(TAG, "problem persisting pending stats", e3);
                recoverFromWtf();
            }
        }
    }

    public void removeUidsLocked(int[] uids) {
        FileRotator fileRotator = this.mRotator;
        if (fileRotator != null) {
            try {
                fileRotator.rewriteAll(new RemoveUidRewriter(this.mBucketDuration, uids));
            } catch (IOException e) {
                Log.wtf(TAG, "problem removing UIDs " + Arrays.toString(uids), e);
                recoverFromWtf();
            } catch (OutOfMemoryError e2) {
                Log.wtf(TAG, "problem removing UIDs " + Arrays.toString(uids), e2);
                recoverFromWtf();
            }
        }
        NetworkStatsCollection networkStatsCollection = this.mPending;
        if (networkStatsCollection != null) {
            networkStatsCollection.removeUids(uids);
        }
        NetworkStatsCollection networkStatsCollection2 = this.mSinceBoot;
        if (networkStatsCollection2 != null) {
            networkStatsCollection2.removeUids(uids);
        }
        NetworkStats networkStats = this.mLastSnapshot;
        if (networkStats != null) {
            networkStats.removeUids(uids);
        }
        WeakReference<NetworkStatsCollection> weakReference = this.mComplete;
        NetworkStatsCollection complete = weakReference != null ? (NetworkStatsCollection) weakReference.get() : null;
        if (complete != null) {
            complete.removeUids(uids);
        }
    }

    private static class CombiningRewriter implements FileRotator.Rewriter {
        private final NetworkStatsCollection mCollection;

        public CombiningRewriter(NetworkStatsCollection collection) {
            this.mCollection = (NetworkStatsCollection) Preconditions.checkNotNull(collection, "missing NetworkStatsCollection");
        }

        public void reset() {
        }

        public void read(InputStream in) throws IOException {
            this.mCollection.read(in);
        }

        public boolean shouldWrite() {
            return true;
        }

        public void write(OutputStream out) throws IOException {
            this.mCollection.write(new DataOutputStream(out));
            this.mCollection.reset();
        }
    }

    public static class RemoveUidRewriter implements FileRotator.Rewriter {
        private final NetworkStatsCollection mTemp;
        private final int[] mUids;

        public RemoveUidRewriter(long bucketDuration, int[] uids) {
            this.mTemp = new NetworkStatsCollection(bucketDuration);
            this.mUids = uids;
        }

        public void reset() {
            this.mTemp.reset();
        }

        public void read(InputStream in) throws IOException {
            this.mTemp.read(in);
            this.mTemp.clearDirty();
            this.mTemp.removeUids(this.mUids);
        }

        public boolean shouldWrite() {
            return this.mTemp.isDirty();
        }

        public void write(OutputStream out) throws IOException {
            this.mTemp.write(new DataOutputStream(out));
        }
    }

    public void importLegacyNetworkLocked(File file) throws IOException {
        Preconditions.checkNotNull(this.mRotator, "missing FileRotator");
        this.mRotator.deleteAll();
        NetworkStatsCollection collection = new NetworkStatsCollection(this.mBucketDuration);
        collection.readLegacyNetwork(file);
        long startMillis = collection.getStartMillis();
        long endMillis = collection.getEndMillis();
        if (!collection.isEmpty()) {
            this.mRotator.rewriteActive(new CombiningRewriter(collection), startMillis);
            this.mRotator.maybeRotate(endMillis);
        }
    }

    public void importLegacyUidLocked(File file) throws IOException {
        Preconditions.checkNotNull(this.mRotator, "missing FileRotator");
        this.mRotator.deleteAll();
        NetworkStatsCollection collection = new NetworkStatsCollection(this.mBucketDuration);
        collection.readLegacyUid(file, this.mOnlyTags);
        long startMillis = collection.getStartMillis();
        long endMillis = collection.getEndMillis();
        if (!collection.isEmpty()) {
            this.mRotator.rewriteActive(new CombiningRewriter(collection), startMillis);
            this.mRotator.maybeRotate(endMillis);
        }
    }

    public void dumpLocked(IndentingPrintWriter pw, boolean fullHistory) {
        if (this.mPending != null) {
            pw.print("Pending bytes: ");
            pw.println(this.mPending.getTotalBytes());
        }
        if (fullHistory) {
            pw.println("Complete history:");
            getOrLoadCompleteLocked().dump(pw);
            return;
        }
        pw.println("History since boot:");
        this.mSinceBoot.dump(pw);
    }

    public void writeToProtoLocked(ProtoOutputStream proto, long tag) {
        long start = proto.start(tag);
        NetworkStatsCollection networkStatsCollection = this.mPending;
        if (networkStatsCollection != null) {
            proto.write(1112396529665L, networkStatsCollection.getTotalBytes());
        }
        getOrLoadCompleteLocked().writeToProto(proto, 1146756268034L);
        proto.end(start);
    }

    public void dumpCheckin(PrintWriter pw, long start, long end) {
        getOrLoadPartialLocked(start, end).dumpCheckin(pw, start, end);
    }

    private void recoverFromWtf() {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try {
            this.mRotator.dumpAll(os);
        } catch (IOException e) {
            os.reset();
        } catch (Throwable th) {
            IoUtils.closeQuietly(os);
            throw th;
        }
        IoUtils.closeQuietly(os);
        this.mDropBox.addData(TAG_NETSTATS_DUMP, os.toByteArray(), 0);
        this.mRotator.deleteAll();
    }
}
