package com.android.server.net;

import android.net.NetworkStats;
import android.os.Build;
import android.os.INetworkManagementService;
import android.os.RemoteException;
import android.text.format.DateFormat;
import android.util.ArrayMap;
import android.util.LocalLog;
import android.util.Log;
import com.google.android.collect.Lists;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Objects;

public class NetworkStatsActualCollection {
    private static final boolean DEBUG = Build.IS_DEBUGGABLE;
    private static final String TAG = "NetworkStatsActualCollection";
    private static NetworkStats mAllUidNetworkStats = null;
    private static final LocalLog mNetworkCollectionLog = new LocalLog(DEBUG ? 500 : 200);
    private static String mRecordTime;

    private NetworkStatsActualCollection() {
    }

    public static void updateNetworkStats(INetworkManagementService networkManager, boolean screenOn) {
        NetworkStats subtractAllUidNetworkStats;
        if (networkManager != null) {
            try {
                NetworkStats newAllUidNetworkStats = networkManager.getNetworkStatsDetail();
                if (newAllUidNetworkStats != null) {
                    boolean isFirstRecord = true;
                    if (mAllUidNetworkStats == null) {
                        subtractAllUidNetworkStats = newAllUidNetworkStats;
                    } else {
                        subtractAllUidNetworkStats = newAllUidNetworkStats.subtract(mAllUidNetworkStats);
                        isFirstRecord = false;
                    }
                    if (subtractAllUidNetworkStats != null) {
                        recordNetworkData(screenOn, isFirstRecord, getNetworkStatsMap(subtractAllUidNetworkStats));
                        mAllUidNetworkStats = newAllUidNetworkStats;
                    }
                }
            } catch (RemoteException e) {
                recordNetworkDataError(screenOn);
            } catch (IllegalStateException e2) {
                recordNetworkDataError(screenOn);
            } catch (Exception e3) {
                Log.d(TAG, "update network stats error");
            }
        }
    }

    private static ArrayMap<Key, NetworkStatsHistory> getNetworkStatsMap(NetworkStats allUidNetworkStats) {
        ArrayMap<Key, NetworkStatsHistory> statsMap = new ArrayMap<>();
        if (allUidNetworkStats == null) {
            return statsMap;
        }
        int networkStatsSize = allUidNetworkStats.size();
        NetworkStats.Entry recycle = new NetworkStats.Entry();
        for (int index = 0; index < networkStatsSize; index++) {
            NetworkStats.Entry item = allUidNetworkStats.getValues(index, recycle);
            if (item.tag == 0) {
                statsMap.put(new Key(item), new NetworkStatsHistory(item));
            }
        }
        return statsMap;
    }

    private static void recordNetworkData(boolean screenOn, boolean firstRecord, ArrayMap<Key, NetworkStatsHistory> subStats) {
        StringBuilder strb = new StringBuilder(2048);
        String currentTime = DateFormat.format("yyyy-MM-dd-HH-mm-ss", System.currentTimeMillis()).toString();
        if (DEBUG) {
            Log.d(TAG, "Screen state " + screenOn + ", firstRecord = " + firstRecord);
        }
        int status = getStatus(screenOn, firstRecord);
        if (status == 0) {
            strb.append("[screen_on (" + mRecordTime + "--" + currentTime + ")]");
        } else if (status == 1) {
            strb.append("[first record screen off (" + currentTime + ")]");
        } else if (status == 2) {
            strb.append("[screen_off (" + mRecordTime + "--" + currentTime + ")]");
        } else if (status == 3) {
            strb.append("[first record screen on (" + currentTime + ")]");
        }
        strb.append("\n");
        mRecordTime = currentTime;
        Iterator<Key> it = getSortedKeys(subStats).iterator();
        while (it.hasNext()) {
            Key key = it.next();
            NetworkStatsHistory lastItem = subStats.get(key);
            if (lastItem.rxBytes != 0 || lastItem.rxPackets != 0 || lastItem.txBytes != 0 || lastItem.txPackets != 0) {
                strb.append(" uid=");
                strb.append(key.uid);
                strb.append(" set=");
                strb.append(NetworkStats.setToString(key.set));
                strb.append(" tag=");
                strb.append(NetworkStats.tagToString(key.tag));
                strb.append(" if=");
                strb.append(key.iface);
                strb.append("  {[");
                strb.append("subrb=");
                strb.append(lastItem.rxBytes);
                strb.append(" subrp=");
                strb.append(lastItem.rxPackets);
                strb.append(" subtb=");
                strb.append(lastItem.txBytes);
                strb.append(" subtp=");
                strb.append(lastItem.txPackets);
                strb.append("]}\n");
            }
        }
        mNetworkCollectionLog.log(strb.toString());
    }

    private static int getStatus(boolean screenOn, boolean firstRecord) {
        return (int) ((screenOn ? 2 : 0) | firstRecord);
    }

    private static class NetworkStatsHistory {
        long rxBytes;
        long rxPackets;
        long txBytes;
        long txPackets;

        public NetworkStatsHistory(NetworkStats.Entry entry) {
            this.rxBytes = entry.rxBytes;
            this.rxPackets = entry.rxPackets;
            this.txBytes = entry.txBytes;
            this.txPackets = entry.txPackets;
        }
    }

    private static class Key implements Comparable<Key> {
        private final int hashCode;
        public final String iface;
        public final int set;
        public final int tag;
        public final int uid;

        public Key(NetworkStats.Entry entry) {
            this.uid = entry.uid;
            this.set = entry.set;
            this.tag = entry.tag;
            this.iface = entry.iface;
            this.hashCode = Objects.hash(new Object[]{Integer.valueOf(entry.uid), Integer.valueOf(entry.set), entry.iface});
        }

        public int hashCode() {
            return this.hashCode;
        }

        public boolean equals(Object obj) {
            if (!(obj instanceof Key)) {
                return false;
            }
            Key key = (Key) obj;
            if (this.uid == key.uid && this.set == key.set && this.iface.equals(key.iface)) {
                return true;
            }
            return false;
        }

        public int compareTo(Key another) {
            int res = Integer.compare(this.uid, another.uid);
            if (res == 0) {
                res = Integer.compare(this.set, another.set);
            }
            if (res == 0) {
                return this.iface.compareTo(another.iface);
            }
            return res;
        }
    }

    private static ArrayList<Key> getSortedKeys(ArrayMap<Key, NetworkStatsHistory> stats) {
        ArrayList<Key> keys = Lists.newArrayList();
        keys.addAll(stats.keySet());
        Collections.sort(keys);
        return keys;
    }

    public static void dump(FileDescriptor fd, PrintWriter writer, String[] args) {
        StringBuilder sb = new StringBuilder(2048);
        ArrayMap<Key, NetworkStatsHistory> newStats = getNetworkStatsMap(mAllUidNetworkStats);
        sb.append("UID stats:\n");
        Iterator<Key> it = getSortedKeys(newStats).iterator();
        while (it.hasNext()) {
            Key key = it.next();
            NetworkStatsHistory item = newStats.get(key);
            if (item.rxBytes != 0 || item.rxPackets != 0 || item.txBytes != 0 || item.txPackets != 0) {
                sb.append(" uid=");
                sb.append(key.uid);
                sb.append(" set=");
                sb.append(NetworkStats.setToString(key.set));
                sb.append(" tag=");
                sb.append(NetworkStats.tagToString(key.tag));
                sb.append(" if=");
                sb.append(key.iface);
                sb.append(" {[");
                sb.append("rb=");
                sb.append(item.rxBytes);
                sb.append(" rp=");
                sb.append(item.rxPackets);
                sb.append(" tb=");
                sb.append(item.txBytes);
                sb.append(" tp=");
                sb.append(item.txPackets);
                sb.append("]}\n");
            }
        }
        mNetworkCollectionLog.dump(fd, writer, args);
        writer.println(sb.toString());
    }

    public static void recordNetworkDataError(boolean screenOn) {
        StringBuilder sb = new StringBuilder(256);
        String currentTime = DateFormat.format("yyyy-MM-dd-HH-mm-ss", System.currentTimeMillis()).toString();
        if (DEBUG) {
            Log.d(TAG, "recordNetworkDataError Screen state " + screenOn);
        }
        if (screenOn) {
            sb.append("[screen_off (" + mRecordTime + "--" + currentTime + ")]");
        } else {
            sb.append("[screen_on (" + mRecordTime + "--" + currentTime + ")]");
        }
        sb.append("Failed to parse network stats");
        sb.append("\n");
        mAllUidNetworkStats = null;
        mRecordTime = currentTime;
        mNetworkCollectionLog.log(sb.toString());
    }
}
