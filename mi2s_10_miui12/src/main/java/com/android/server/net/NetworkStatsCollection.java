package com.android.server.net;

import android.net.NetworkIdentity;
import android.net.NetworkStats;
import android.net.NetworkStatsHistory;
import android.net.NetworkTemplate;
import android.os.Binder;
import android.telephony.SubscriptionPlan;
import android.util.ArrayMap;
import android.util.AtomicFile;
import android.util.IntArray;
import android.util.MathUtils;
import android.util.Range;
import android.util.Slog;
import android.util.proto.ProtoOutputStream;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.util.ArrayUtils;
import com.android.internal.util.FileRotator;
import com.android.internal.util.IndentingPrintWriter;
import com.android.server.job.controllers.JobStatus;
import com.google.android.collect.Lists;
import com.google.android.collect.Maps;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.ProtocolException;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Objects;
import libcore.io.IoUtils;

public class NetworkStatsCollection implements FileRotator.Reader {
    private static final int FILE_MAGIC = 1095648596;
    private static final int VERSION_NETWORK_INIT = 1;
    private static final int VERSION_UID_INIT = 1;
    private static final int VERSION_UID_WITH_IDENT = 2;
    private static final int VERSION_UID_WITH_SET = 4;
    private static final int VERSION_UID_WITH_TAG = 3;
    private static final int VERSION_UNIFIED_INIT = 16;
    private final long mBucketDuration;
    private boolean mDirty;
    private long mEndMillis;
    private long mStartMillis;
    private ArrayMap<Key, NetworkStatsHistory> mStats = new ArrayMap<>();
    private long mTotalBytes;

    public NetworkStatsCollection(long bucketDuration) {
        this.mBucketDuration = bucketDuration;
        reset();
    }

    public void clear() {
        reset();
    }

    public void reset() {
        this.mStats.clear();
        this.mStartMillis = JobStatus.NO_LATEST_RUNTIME;
        this.mEndMillis = Long.MIN_VALUE;
        this.mTotalBytes = 0;
        this.mDirty = false;
    }

    public long getStartMillis() {
        return this.mStartMillis;
    }

    public long getFirstAtomicBucketMillis() {
        long j = this.mStartMillis;
        if (j == JobStatus.NO_LATEST_RUNTIME) {
            return JobStatus.NO_LATEST_RUNTIME;
        }
        return j + this.mBucketDuration;
    }

    public long getEndMillis() {
        return this.mEndMillis;
    }

    public long getTotalBytes() {
        return this.mTotalBytes;
    }

    public boolean isDirty() {
        return this.mDirty;
    }

    public void clearDirty() {
        this.mDirty = false;
    }

    public boolean isEmpty() {
        return this.mStartMillis == JobStatus.NO_LATEST_RUNTIME && this.mEndMillis == Long.MIN_VALUE;
    }

    @VisibleForTesting
    public long roundUp(long time) {
        if (time == Long.MIN_VALUE || time == JobStatus.NO_LATEST_RUNTIME || time == -1) {
            return time;
        }
        long j = this.mBucketDuration;
        long mod = time % j;
        if (mod > 0) {
            return (time - mod) + j;
        }
        return time;
    }

    @VisibleForTesting
    public long roundDown(long time) {
        if (time == Long.MIN_VALUE || time == JobStatus.NO_LATEST_RUNTIME || time == -1) {
            return time;
        }
        long mod = time % this.mBucketDuration;
        if (mod > 0) {
            return time - mod;
        }
        return time;
    }

    @VisibleForTesting
    public static long multiplySafe(long value, long num, long den) {
        long den2 = den == 0 ? 1 : den;
        long x = value;
        long y = num;
        long r = x * y;
        if (((Math.abs(x) | Math.abs(y)) >>> 31) == 0) {
            long j = value;
            long j2 = x;
        } else if ((y == 0 || r / y == x) && !(x == Long.MIN_VALUE && y == -1)) {
            long j3 = value;
            long j4 = x;
        } else {
            long j5 = x;
            return (long) ((((double) num) / ((double) den2)) * ((double) value));
        }
        return r / den2;
    }

    public int[] getRelevantUids(int accessLevel) {
        return getRelevantUids(accessLevel, Binder.getCallingUid());
    }

    public int[] getRelevantUids(int accessLevel, int callerUid) {
        int j;
        IntArray uids = new IntArray();
        for (int i = 0; i < this.mStats.size(); i++) {
            Key key = this.mStats.keyAt(i);
            if (NetworkStatsAccess.isAccessibleToUser(key.uid, callerUid, accessLevel) && (j = uids.binarySearch(key.uid)) < 0) {
                uids.add(~j, key.uid);
            }
        }
        return uids.toArray();
    }

    public NetworkStatsHistory getHistory(NetworkTemplate template, SubscriptionPlan augmentPlan, int uid, int set, int tag, int fields, long start, long end, int accessLevel, int callerUid) {
        long augmentEnd;
        long collectEnd;
        long collectEnd2;
        long collectStart;
        long augmentEnd2;
        long augmentStart;
        long rawBytes;
        int i = uid;
        int i2 = fields;
        if (NetworkStatsAccess.isAccessibleToUser(i, callerUid, accessLevel)) {
            long j = this.mBucketDuration;
            int bucketEstimate = (int) MathUtils.constrain((end - start) / j, 0, 15552000000L / j);
            NetworkStatsHistory combined = new NetworkStatsHistory(this.mBucketDuration, bucketEstimate, i2);
            if (start == end) {
                return combined;
            }
            long augmentStart2 = -1;
            if (augmentPlan != null) {
                augmentEnd = augmentPlan.getDataUsageTime();
            } else {
                augmentEnd = -1;
            }
            long collectStart2 = start;
            long collectEnd3 = end;
            if (augmentEnd != -1) {
                Iterator<Range<ZonedDateTime>> it = augmentPlan.cycleIterator();
                while (true) {
                    if (!it.hasNext()) {
                        collectEnd = collectEnd3;
                        break;
                    }
                    Range<ZonedDateTime> cycle = it.next();
                    long cycleStart = cycle.getLower().toInstant().toEpochMilli();
                    long cycleEnd = cycle.getUpper().toInstant().toEpochMilli();
                    if (cycleStart <= augmentEnd && augmentEnd < cycleEnd) {
                        augmentStart2 = cycleStart;
                        collectStart2 = Long.min(collectStart2, augmentStart2);
                        collectEnd = Long.max(collectEnd3, augmentEnd);
                        break;
                    }
                    collectEnd3 = collectEnd3;
                }
            } else {
                collectEnd = collectEnd3;
            }
            if (augmentStart2 != -1) {
                long augmentStart3 = roundUp(augmentStart2);
                long augmentEnd3 = roundDown(augmentEnd);
                long collectStart3 = roundDown(collectStart2);
                augmentStart = augmentStart3;
                collectEnd2 = roundUp(collectEnd);
                augmentEnd2 = augmentEnd3;
                collectStart = collectStart3;
            } else {
                augmentStart = augmentStart2;
                collectEnd2 = collectEnd;
                augmentEnd2 = augmentEnd;
                collectStart = collectStart2;
            }
            for (int i3 = 0; i3 < this.mStats.size(); i3++) {
                Key key = this.mStats.keyAt(i3);
                if (key.uid == i) {
                    if (NetworkStats.setMatches(set, key.set)) {
                        if (key.tag == tag) {
                            if (templateMatches(template, key.ident)) {
                                combined.recordHistory(this.mStats.valueAt(i3), collectStart, collectEnd2);
                            }
                        }
                    }
                } else {
                    int i4 = set;
                }
                int i5 = tag;
            }
            int i6 = set;
            int i7 = tag;
            if (augmentStart == -1) {
                return combined;
            }
            NetworkStatsHistory.Entry entry = combined.getValues(augmentStart, augmentEnd2, (NetworkStatsHistory.Entry) null);
            if (entry.rxBytes == 0 || entry.txBytes == 0) {
                NetworkStatsHistory networkStatsHistory = combined;
                long j2 = augmentStart;
                long j3 = augmentEnd2;
                networkStatsHistory.recordData(j2, j3, new NetworkStats.Entry(1, 0, 1, 0, 0));
                networkStatsHistory.getValues(j2, j3, entry);
            }
            long rawBytes2 = entry.txBytes + entry.rxBytes;
            long rawRxBytes = entry.rxBytes;
            long rawTxBytes = entry.txBytes;
            long dataUsageBytes = augmentPlan.getDataUsageBytes();
            long j4 = rawBytes2;
            long targetRxBytes = multiplySafe(dataUsageBytes, rawRxBytes, j4);
            long targetTxBytes = multiplySafe(dataUsageBytes, rawTxBytes, j4);
            long beforeTotal = combined.getTotalBytes();
            int i8 = 0;
            while (i8 < combined.size()) {
                combined.getValues(i8, entry);
                if (entry.bucketStart >= augmentStart) {
                    rawBytes = rawBytes2;
                    if (entry.bucketStart + entry.bucketDuration <= augmentEnd2) {
                        entry.rxBytes = multiplySafe(targetRxBytes, entry.rxBytes, rawRxBytes);
                        entry.txBytes = multiplySafe(targetTxBytes, entry.txBytes, rawTxBytes);
                        entry.rxPackets = 0;
                        entry.txPackets = 0;
                        combined.setValues(i8, entry);
                    }
                } else {
                    rawBytes = rawBytes2;
                }
                i8++;
                int i9 = accessLevel;
                rawBytes2 = rawBytes;
            }
            long deltaTotal = combined.getTotalBytes() - beforeTotal;
            if (deltaTotal != 0) {
                Slog.d("NetworkStats", "Augmented network usage by " + deltaTotal + " bytes");
            }
            NetworkStatsHistory sliced = new NetworkStatsHistory(this.mBucketDuration, bucketEstimate, i2);
            long j5 = rawTxBytes;
            NetworkStatsHistory.Entry entry2 = entry;
            NetworkStatsHistory networkStatsHistory2 = combined;
            sliced.recordHistory(combined, start, end);
            return sliced;
        }
        throw new SecurityException("Network stats history of uid " + i + " is forbidden for caller " + callerUid);
    }

    public NetworkStats getSummary(NetworkTemplate template, long start, long end, int accessLevel, int callerUid) {
        long now = System.currentTimeMillis();
        NetworkStats stats = new NetworkStats(end - start, 24);
        if (start == end) {
            return stats;
        }
        NetworkStats.Entry entry = new NetworkStats.Entry();
        NetworkStatsHistory.Entry historyEntry = null;
        for (int i = 0; i < this.mStats.size(); i++) {
            Key key = this.mStats.keyAt(i);
            if (templateMatches(template, key.ident)) {
                if (NetworkStatsAccess.isAccessibleToUser(key.uid, callerUid, accessLevel) && key.set < 1000) {
                    historyEntry = this.mStats.valueAt(i).getValues(start, end, now, historyEntry);
                    entry.iface = NetworkStats.IFACE_ALL;
                    entry.uid = key.uid;
                    entry.set = key.set;
                    entry.tag = key.tag;
                    entry.defaultNetwork = key.ident.areAllMembersOnDefaultNetwork() ? 1 : 0;
                    entry.metered = key.ident.isAnyMemberMetered() ? 1 : 0;
                    entry.roaming = key.ident.isAnyMemberRoaming() ? 1 : 0;
                    entry.rxBytes = historyEntry.rxBytes;
                    entry.rxPackets = historyEntry.rxPackets;
                    entry.txBytes = historyEntry.txBytes;
                    entry.txPackets = historyEntry.txPackets;
                    entry.operations = historyEntry.operations;
                    if (!entry.isEmpty()) {
                        stats.combineValues(entry);
                    }
                }
            }
        }
        return stats;
    }

    public void recordData(NetworkIdentitySet ident, int uid, int set, int tag, long start, long end, NetworkStats.Entry entry) {
        NetworkStats.Entry entry2 = entry;
        NetworkStatsHistory history = findOrCreateHistory(ident, uid, set, tag);
        history.recordData(start, end, entry);
        noteRecordedHistory(history.getStart(), history.getEnd(), entry2.rxBytes + entry2.txBytes);
    }

    private void recordHistory(Key key, NetworkStatsHistory history) {
        if (history.size() != 0) {
            noteRecordedHistory(history.getStart(), history.getEnd(), history.getTotalBytes());
            NetworkStatsHistory target = this.mStats.get(key);
            if (target == null) {
                target = new NetworkStatsHistory(history.getBucketDuration());
                this.mStats.put(key, target);
            }
            target.recordEntireHistory(history);
        }
    }

    public void recordCollection(NetworkStatsCollection another) {
        for (int i = 0; i < another.mStats.size(); i++) {
            recordHistory(another.mStats.keyAt(i), another.mStats.valueAt(i));
        }
    }

    private NetworkStatsHistory findOrCreateHistory(NetworkIdentitySet ident, int uid, int set, int tag) {
        Key key = new Key(ident, uid, set, tag);
        NetworkStatsHistory existing = this.mStats.get(key);
        NetworkStatsHistory updated = null;
        if (existing == null) {
            updated = new NetworkStatsHistory(this.mBucketDuration, 10);
        } else {
            long bucketDuration = existing.getBucketDuration();
            long j = this.mBucketDuration;
            if (bucketDuration != j) {
                updated = new NetworkStatsHistory(existing, j);
            }
        }
        if (updated == null) {
            return existing;
        }
        this.mStats.put(key, updated);
        return updated;
    }

    public void read(InputStream in) throws IOException {
        read(new DataInputStream(in));
    }

    public void read(DataInputStream in) throws IOException {
        int magic = in.readInt();
        if (magic == FILE_MAGIC) {
            int version = in.readInt();
            if (version == 16) {
                int identSize = in.readInt();
                for (int i = 0; i < identSize; i++) {
                    NetworkIdentitySet ident = new NetworkIdentitySet(in);
                    int size = in.readInt();
                    for (int j = 0; j < size; j++) {
                        recordHistory(new Key(ident, in.readInt(), in.readInt(), in.readInt()), new NetworkStatsHistory(in));
                    }
                }
                return;
            }
            throw new ProtocolException("unexpected version: " + version);
        }
        throw new ProtocolException("unexpected magic: " + magic);
    }

    public void write(DataOutputStream out) throws IOException {
        HashMap<NetworkIdentitySet, ArrayList<Key>> keysByIdent = Maps.newHashMap();
        for (Key key : this.mStats.keySet()) {
            ArrayList<Key> keys = keysByIdent.get(key.ident);
            if (keys == null) {
                keys = Lists.newArrayList();
                keysByIdent.put(key.ident, keys);
            }
            keys.add(key);
        }
        out.writeInt(FILE_MAGIC);
        out.writeInt(16);
        out.writeInt(keysByIdent.size());
        for (NetworkIdentitySet ident : keysByIdent.keySet()) {
            ArrayList<Key> keys2 = keysByIdent.get(ident);
            ident.writeToStream(out);
            out.writeInt(keys2.size());
            Iterator<Key> it = keys2.iterator();
            while (it.hasNext()) {
                Key key2 = it.next();
                out.writeInt(key2.uid);
                out.writeInt(key2.set);
                out.writeInt(key2.tag);
                this.mStats.get(key2).writeToStream(out);
            }
        }
        out.flush();
    }

    /* Debug info: failed to restart local var, previous not found, register: 11 */
    @Deprecated
    public void readLegacyNetwork(File file) throws IOException {
        DataInputStream in = null;
        try {
            in = new DataInputStream(new BufferedInputStream(new AtomicFile(file).openRead()));
            int magic = in.readInt();
            if (magic == FILE_MAGIC) {
                int version = in.readInt();
                if (version == 1) {
                    int size = in.readInt();
                    for (int i = 0; i < size; i++) {
                        NetworkIdentitySet ident = new NetworkIdentitySet(in);
                        recordHistory(new Key(ident, -1, -1, 0), new NetworkStatsHistory(in));
                    }
                    IoUtils.closeQuietly(in);
                    return;
                }
                throw new ProtocolException("unexpected version: " + version);
            }
            throw new ProtocolException("unexpected magic: " + magic);
        } catch (FileNotFoundException e) {
        } catch (Throwable th) {
            IoUtils.closeQuietly((AutoCloseable) null);
            throw th;
        }
    }

    /* Debug info: failed to restart local var, previous not found, register: 16 */
    @Deprecated
    public void readLegacyUid(File file, boolean onlyTags) throws IOException {
        int set;
        DataInputStream in = null;
        try {
            in = new DataInputStream(new BufferedInputStream(new AtomicFile(file).openRead()));
            int magic = in.readInt();
            if (magic == FILE_MAGIC) {
                int version = in.readInt();
                if (version == 1) {
                    boolean z = onlyTags;
                } else if (version != 2) {
                    int i = 4;
                    if (version != 3) {
                        if (version != 4) {
                            throw new ProtocolException("unexpected version: " + version);
                        }
                    }
                    int identSize = in.readInt();
                    int i2 = 0;
                    while (i2 < identSize) {
                        NetworkIdentitySet ident = new NetworkIdentitySet(in);
                        int size = in.readInt();
                        int j = 0;
                        while (j < size) {
                            int uid = in.readInt();
                            boolean z2 = false;
                            if (version >= i) {
                                set = in.readInt();
                            } else {
                                set = 0;
                            }
                            int tag = in.readInt();
                            Key key = new Key(ident, uid, set, tag);
                            NetworkStatsHistory history = new NetworkStatsHistory(in);
                            if (tag == 0) {
                                z2 = true;
                            }
                            if (z2 != onlyTags) {
                                try {
                                    recordHistory(key, history);
                                } catch (FileNotFoundException e) {
                                } catch (Throwable th) {
                                    th = th;
                                    IoUtils.closeQuietly(in);
                                    throw th;
                                }
                            }
                            j++;
                            File file2 = file;
                            i = 4;
                        }
                        boolean z3 = onlyTags;
                        i2++;
                        File file3 = file;
                        i = 4;
                    }
                    boolean z4 = onlyTags;
                } else {
                    boolean z5 = onlyTags;
                }
                IoUtils.closeQuietly(in);
                return;
            }
            boolean z6 = onlyTags;
            throw new ProtocolException("unexpected magic: " + magic);
        } catch (FileNotFoundException e2) {
            boolean z7 = onlyTags;
        } catch (Throwable th2) {
            th = th2;
            boolean z8 = onlyTags;
            IoUtils.closeQuietly(in);
            throw th;
        }
    }

    public void removeUids(int[] uids) {
        ArrayList<Key> knownKeys = Lists.newArrayList();
        knownKeys.addAll(this.mStats.keySet());
        Iterator<Key> it = knownKeys.iterator();
        while (it.hasNext()) {
            Key key = it.next();
            if (ArrayUtils.contains(uids, key.uid)) {
                if (key.tag == 0) {
                    findOrCreateHistory(key.ident, -4, 0, 0).recordEntireHistory(this.mStats.get(key));
                }
                this.mStats.remove(key);
                this.mDirty = true;
            }
        }
    }

    private void noteRecordedHistory(long startMillis, long endMillis, long totalBytes) {
        if (startMillis < this.mStartMillis) {
            this.mStartMillis = startMillis;
        }
        if (endMillis > this.mEndMillis) {
            this.mEndMillis = endMillis;
        }
        this.mTotalBytes += totalBytes;
        this.mDirty = true;
    }

    private int estimateBuckets() {
        return (int) (Math.min(this.mEndMillis - this.mStartMillis, 3024000000L) / this.mBucketDuration);
    }

    private ArrayList<Key> getSortedKeys() {
        ArrayList<Key> keys = Lists.newArrayList();
        keys.addAll(this.mStats.keySet());
        Collections.sort(keys);
        return keys;
    }

    public void dump(IndentingPrintWriter pw) {
        Iterator<Key> it = getSortedKeys().iterator();
        while (it.hasNext()) {
            Key key = it.next();
            pw.print("ident=");
            pw.print(key.ident.toString());
            pw.print(" uid=");
            pw.print(key.uid);
            pw.print(" set=");
            pw.print(NetworkStats.setToString(key.set));
            pw.print(" tag=");
            pw.println(NetworkStats.tagToString(key.tag));
            pw.increaseIndent();
            this.mStats.get(key).dump(pw, true);
            pw.decreaseIndent();
        }
    }

    public void writeToProto(ProtoOutputStream proto, long tag) {
        long start = proto.start(tag);
        Iterator<Key> it = getSortedKeys().iterator();
        while (it.hasNext()) {
            Key key = it.next();
            long startStats = proto.start(2246267895809L);
            long startKey = proto.start(1146756268033L);
            key.ident.writeToProto(proto, 1146756268033L);
            proto.write(1120986464258L, key.uid);
            proto.write(1120986464259L, key.set);
            proto.write(1120986464260L, key.tag);
            proto.end(startKey);
            this.mStats.get(key).writeToProto(proto, 1146756268034L);
            proto.end(startStats);
        }
        proto.end(start);
    }

    public void dumpCheckin(PrintWriter pw, long start, long end) {
        PrintWriter printWriter = pw;
        long j = start;
        long j2 = end;
        dumpCheckin(printWriter, j, j2, NetworkTemplate.buildTemplateMobileWildcard(), "cell");
        PrintWriter printWriter2 = pw;
        long j3 = start;
        long j4 = end;
        dumpCheckin(printWriter2, j3, j4, NetworkTemplate.buildTemplateWifiWildcard(), "wifi");
        dumpCheckin(printWriter, j, j2, NetworkTemplate.buildTemplateEthernet(), "eth");
        dumpCheckin(printWriter2, j3, j4, NetworkTemplate.buildTemplateBluetooth(), "bt");
    }

    private void dumpCheckin(PrintWriter pw, long start, long end, NetworkTemplate groupTemplate, String groupPrefix) {
        NetworkStatsHistory groupHistory;
        PrintWriter printWriter = pw;
        ArrayMap<Key, NetworkStatsHistory> grouped = new ArrayMap<>();
        for (int i = 0; i < this.mStats.size(); i++) {
            Key key = this.mStats.keyAt(i);
            NetworkStatsHistory value = this.mStats.valueAt(i);
            if (templateMatches(groupTemplate, key.ident) && key.set < 1000) {
                Key groupKey = new Key((NetworkIdentitySet) null, key.uid, key.set, key.tag);
                NetworkStatsHistory groupHistory2 = grouped.get(groupKey);
                if (groupHistory2 == null) {
                    NetworkStatsHistory groupHistory3 = new NetworkStatsHistory(value.getBucketDuration());
                    grouped.put(groupKey, groupHistory3);
                    groupHistory = groupHistory3;
                } else {
                    groupHistory = groupHistory2;
                }
                groupHistory.recordHistory(value, start, end);
            }
        }
        NetworkTemplate networkTemplate = groupTemplate;
        for (int i2 = 0; i2 < grouped.size(); i2++) {
            Key key2 = grouped.keyAt(i2);
            NetworkStatsHistory value2 = grouped.valueAt(i2);
            if (value2.size() == 0) {
                String str = groupPrefix;
            } else {
                printWriter.print("c,");
                printWriter.print(groupPrefix);
                printWriter.print(',');
                printWriter.print(key2.uid);
                printWriter.print(',');
                printWriter.print(NetworkStats.setToCheckinString(key2.set));
                printWriter.print(',');
                printWriter.print(key2.tag);
                pw.println();
                value2.dumpCheckin(printWriter);
            }
        }
        String str2 = groupPrefix;
    }

    private static boolean templateMatches(NetworkTemplate template, NetworkIdentitySet identSet) {
        Iterator it = identSet.iterator();
        while (it.hasNext()) {
            if (template.matches((NetworkIdentity) it.next())) {
                return true;
            }
        }
        return false;
    }

    private static class Key implements Comparable<Key> {
        private final int hashCode;
        public final NetworkIdentitySet ident;
        public final int set;
        public final int tag;
        public final int uid;

        public Key(NetworkIdentitySet ident2, int uid2, int set2, int tag2) {
            this.ident = ident2;
            this.uid = uid2;
            this.set = set2;
            this.tag = tag2;
            this.hashCode = Objects.hash(new Object[]{ident2, Integer.valueOf(uid2), Integer.valueOf(set2), Integer.valueOf(tag2)});
        }

        public int hashCode() {
            return this.hashCode;
        }

        public boolean equals(Object obj) {
            if (!(obj instanceof Key)) {
                return false;
            }
            Key key = (Key) obj;
            if (this.uid == key.uid && this.set == key.set && this.tag == key.tag && Objects.equals(this.ident, key.ident)) {
                return true;
            }
            return false;
        }

        public int compareTo(Key another) {
            NetworkIdentitySet networkIdentitySet;
            int res = 0;
            NetworkIdentitySet networkIdentitySet2 = this.ident;
            if (!(networkIdentitySet2 == null || (networkIdentitySet = another.ident) == null)) {
                res = networkIdentitySet2.compareTo(networkIdentitySet);
            }
            if (res == 0) {
                res = Integer.compare(this.uid, another.uid);
            }
            if (res == 0) {
                res = Integer.compare(this.set, another.set);
            }
            if (res == 0) {
                return Integer.compare(this.tag, another.tag);
            }
            return res;
        }
    }
}
