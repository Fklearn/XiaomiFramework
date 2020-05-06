package com.android.server.wifi.p2p;

import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pGroup;
import android.net.wifi.p2p.WifiP2pGroupList;
import android.util.Log;
import com.android.server.wifi.Clock;
import com.android.server.wifi.nano.WifiMetricsProto;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import miui.provider.ExtraCalendarContracts;

public class WifiP2pMetrics {
    private static final boolean DBG = false;
    private static final int MAX_CONNECTION_EVENTS = 256;
    private static final int MAX_GROUP_EVENTS = 256;
    private static final String TAG = "WifiP2pMetrics";
    private Clock mClock;
    private final List<WifiMetricsProto.P2pConnectionEvent> mConnectionEventList = new ArrayList();
    private WifiMetricsProto.P2pConnectionEvent mCurrentConnectionEvent;
    private long mCurrentConnectionEventStartTime;
    private WifiMetricsProto.GroupEvent mCurrentGroupEvent;
    private long mCurrentGroupEventIdleStartTime;
    private long mCurrentGroupEventStartTime;
    private final List<WifiMetricsProto.GroupEvent> mGroupEventList = new ArrayList();
    private final Object mLock = new Object();
    private int mNumPersistentGroup;
    private final WifiMetricsProto.WifiP2pStats mWifiP2pStatsProto = new WifiMetricsProto.WifiP2pStats();

    public WifiP2pMetrics(Clock clock) {
        this.mClock = clock;
        this.mNumPersistentGroup = 0;
    }

    public void clear() {
        synchronized (this.mLock) {
            this.mConnectionEventList.clear();
            if (this.mCurrentConnectionEvent != null) {
                this.mConnectionEventList.add(this.mCurrentConnectionEvent);
            }
            this.mGroupEventList.clear();
            if (this.mCurrentGroupEvent != null) {
                this.mGroupEventList.add(this.mCurrentGroupEvent);
            }
            this.mWifiP2pStatsProto.clear();
        }
    }

    public WifiMetricsProto.WifiP2pStats consolidateProto() {
        WifiMetricsProto.WifiP2pStats wifiP2pStats;
        synchronized (this.mLock) {
            this.mWifiP2pStatsProto.numPersistentGroup = this.mNumPersistentGroup;
            int connectionEventCount = this.mConnectionEventList.size();
            if (this.mCurrentConnectionEvent != null) {
                connectionEventCount--;
            }
            this.mWifiP2pStatsProto.connectionEvent = new WifiMetricsProto.P2pConnectionEvent[connectionEventCount];
            for (int i = 0; i < connectionEventCount; i++) {
                this.mWifiP2pStatsProto.connectionEvent[i] = this.mConnectionEventList.get(i);
            }
            int groupEventCount = this.mGroupEventList.size();
            if (this.mCurrentGroupEvent != null) {
                groupEventCount--;
            }
            this.mWifiP2pStatsProto.groupEvent = new WifiMetricsProto.GroupEvent[groupEventCount];
            for (int i2 = 0; i2 < groupEventCount; i2++) {
                this.mWifiP2pStatsProto.groupEvent[i2] = this.mGroupEventList.get(i2);
            }
            wifiP2pStats = this.mWifiP2pStatsProto;
        }
        return wifiP2pStats;
    }

    public void dump(PrintWriter pw) {
        String str;
        String str2;
        PrintWriter printWriter = pw;
        synchronized (this.mLock) {
            printWriter.println("WifiP2pMetrics:");
            printWriter.println("mConnectionEvents:");
            for (WifiMetricsProto.P2pConnectionEvent event : this.mConnectionEventList) {
                StringBuilder sb = new StringBuilder();
                Calendar c = Calendar.getInstance();
                c.setTimeInMillis(event.startTimeMillis);
                sb.append("startTime=");
                if (event.startTimeMillis == 0) {
                    str2 = "            <null>";
                } else {
                    str2 = String.format("%tm-%td %tH:%tM:%tS.%tL", new Object[]{c, c, c, c, c, c});
                }
                sb.append(str2);
                sb.append(", connectionType=");
                int i = event.connectionType;
                if (i == 0) {
                    sb.append("FRESH");
                } else if (i == 1) {
                    sb.append("REINVOKE");
                } else if (i == 2) {
                    sb.append(ExtraCalendarContracts.ACCOUNT_TYPE_LOCAL);
                } else if (i != 3) {
                    sb.append("UNKNOWN");
                } else {
                    sb.append("FAST");
                }
                sb.append(", wpsMethod=");
                int i2 = event.wpsMethod;
                if (i2 == -1) {
                    sb.append("NA");
                } else if (i2 == 0) {
                    sb.append("PBC");
                } else if (i2 == 1) {
                    sb.append("DISPLAY");
                } else if (i2 == 2) {
                    sb.append("KEYPAD");
                } else if (i2 != 3) {
                    sb.append("UNKNOWN");
                } else {
                    sb.append("LABLE");
                }
                sb.append(", durationTakenToConnectMillis=");
                sb.append(event.durationTakenToConnectMillis);
                sb.append(", connectivityLevelFailureCode=");
                switch (event.connectivityLevelFailureCode) {
                    case 1:
                        sb.append("NONE");
                        break;
                    case 2:
                        sb.append("TIMEOUT");
                        break;
                    case 3:
                        sb.append("CANCEL");
                        break;
                    case 4:
                        sb.append("PROV_DISC_FAIL");
                        break;
                    case 5:
                        sb.append("INVITATION_FAIL");
                        break;
                    case 6:
                        sb.append("USER_REJECT");
                        break;
                    case 7:
                        sb.append("NEW_CONNECTION_ATTEMPT");
                        break;
                    default:
                        sb.append("UNKNOWN");
                        break;
                }
                if (event == this.mCurrentConnectionEvent) {
                    sb.append(" CURRENTLY OPEN EVENT");
                }
                printWriter.println(sb.toString());
            }
            printWriter.println("mGroupEvents:");
            for (WifiMetricsProto.GroupEvent event2 : this.mGroupEventList) {
                StringBuilder sb2 = new StringBuilder();
                Calendar c2 = Calendar.getInstance();
                c2.setTimeInMillis(event2.startTimeMillis);
                sb2.append("netId=");
                sb2.append(event2.netId);
                sb2.append(", startTime=");
                if (event2.startTimeMillis == 0) {
                    str = "            <null>";
                } else {
                    str = String.format("%tm-%td %tH:%tM:%tS.%tL", new Object[]{c2, c2, c2, c2, c2, c2});
                }
                sb2.append(str);
                sb2.append(", channelFrequency=");
                sb2.append(event2.channelFrequency);
                sb2.append(", groupRole=");
                if (event2.groupRole != 1) {
                    sb2.append("GroupOwner");
                } else {
                    sb2.append("GroupClient");
                }
                sb2.append(", numConnectedClients=");
                sb2.append(event2.numConnectedClients);
                sb2.append(", numCumulativeClients=");
                sb2.append(event2.numCumulativeClients);
                sb2.append(", sessionDurationMillis=");
                sb2.append(event2.sessionDurationMillis);
                sb2.append(", idleDurationMillis=");
                sb2.append(event2.idleDurationMillis);
                if (event2 == this.mCurrentGroupEvent) {
                    sb2.append(" CURRENTLY OPEN EVENT");
                }
                printWriter.println(sb2.toString());
            }
            printWriter.println("mWifiP2pStatsProto.numPersistentGroup=" + this.mNumPersistentGroup);
            printWriter.println("mWifiP2pStatsProto.numTotalPeerScans=" + this.mWifiP2pStatsProto.numTotalPeerScans);
            printWriter.println("mWifiP2pStatsProto.numTotalServiceScans=" + this.mWifiP2pStatsProto.numTotalServiceScans);
        }
    }

    public void incrementPeerScans() {
        synchronized (this.mLock) {
            this.mWifiP2pStatsProto.numTotalPeerScans++;
        }
    }

    public void incrementServiceScans() {
        synchronized (this.mLock) {
            this.mWifiP2pStatsProto.numTotalServiceScans++;
        }
    }

    public void updatePersistentGroup(WifiP2pGroupList groups) {
        synchronized (this.mLock) {
            this.mNumPersistentGroup = groups.getGroupList().size();
        }
    }

    public void startConnectionEvent(int connectionType, WifiP2pConfig config) {
        synchronized (this.mLock) {
            if (this.mCurrentConnectionEvent != null) {
                endConnectionEvent(7);
            }
            while (this.mConnectionEventList.size() >= 256) {
                this.mConnectionEventList.remove(0);
            }
            this.mCurrentConnectionEventStartTime = this.mClock.getElapsedSinceBootMillis();
            this.mCurrentConnectionEvent = new WifiMetricsProto.P2pConnectionEvent();
            this.mCurrentConnectionEvent.startTimeMillis = this.mClock.getWallClockMillis();
            this.mCurrentConnectionEvent.connectionType = connectionType;
            if (config != null) {
                this.mCurrentConnectionEvent.wpsMethod = config.wps.setup;
            }
            this.mConnectionEventList.add(this.mCurrentConnectionEvent);
        }
    }

    public void endConnectionEvent(int failure) {
        synchronized (this.mLock) {
            if (this.mCurrentConnectionEvent == null) {
                startConnectionEvent(1, (WifiP2pConfig) null);
            }
            this.mCurrentConnectionEvent.durationTakenToConnectMillis = (int) (this.mClock.getElapsedSinceBootMillis() - this.mCurrentConnectionEventStartTime);
            this.mCurrentConnectionEvent.connectivityLevelFailureCode = failure;
            this.mCurrentConnectionEvent = null;
        }
    }

    public void startGroupEvent(WifiP2pGroup group) {
        int i;
        if (group != null) {
            synchronized (this.mLock) {
                if (this.mCurrentGroupEvent != null) {
                    endGroupEvent();
                }
                while (true) {
                    i = 0;
                    if (this.mGroupEventList.size() < 256) {
                        break;
                    }
                    this.mGroupEventList.remove(0);
                }
                this.mCurrentGroupEventStartTime = this.mClock.getElapsedSinceBootMillis();
                if (group.getClientList().size() == 0) {
                    this.mCurrentGroupEventIdleStartTime = this.mClock.getElapsedSinceBootMillis();
                } else {
                    this.mCurrentGroupEventIdleStartTime = 0;
                }
                this.mCurrentGroupEvent = new WifiMetricsProto.GroupEvent();
                this.mCurrentGroupEvent.netId = group.getNetworkId();
                this.mCurrentGroupEvent.startTimeMillis = this.mClock.getWallClockMillis();
                this.mCurrentGroupEvent.numConnectedClients = group.getClientList().size();
                this.mCurrentGroupEvent.channelFrequency = group.getFrequency();
                WifiMetricsProto.GroupEvent groupEvent = this.mCurrentGroupEvent;
                if (!group.isGroupOwner()) {
                    i = 1;
                }
                groupEvent.groupRole = i;
                this.mGroupEventList.add(this.mCurrentGroupEvent);
            }
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:27:0x00a6, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void updateGroupEvent(android.net.wifi.p2p.WifiP2pGroup r13) {
        /*
            r12 = this;
            if (r13 != 0) goto L_0x0003
            return
        L_0x0003:
            java.lang.Object r0 = r12.mLock
            monitor-enter(r0)
            com.android.server.wifi.nano.WifiMetricsProto$GroupEvent r1 = r12.mCurrentGroupEvent     // Catch:{ all -> 0x00a7 }
            if (r1 != 0) goto L_0x0013
            java.lang.String r1 = "WifiP2pMetrics"
            java.lang.String r2 = "Cannot update group event due to no current group."
            android.util.Log.w(r1, r2)     // Catch:{ all -> 0x00a7 }
            monitor-exit(r0)     // Catch:{ all -> 0x00a7 }
            return
        L_0x0013:
            com.android.server.wifi.nano.WifiMetricsProto$GroupEvent r1 = r12.mCurrentGroupEvent     // Catch:{ all -> 0x00a7 }
            int r1 = r1.netId     // Catch:{ all -> 0x00a7 }
            int r2 = r13.getNetworkId()     // Catch:{ all -> 0x00a7 }
            if (r1 == r2) goto L_0x004a
            java.lang.String r1 = "WifiP2pMetrics"
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ all -> 0x00a7 }
            r2.<init>()     // Catch:{ all -> 0x00a7 }
            java.lang.String r3 = "Updating group id "
            r2.append(r3)     // Catch:{ all -> 0x00a7 }
            int r3 = r13.getNetworkId()     // Catch:{ all -> 0x00a7 }
            r2.append(r3)     // Catch:{ all -> 0x00a7 }
            java.lang.String r3 = " is different from current group id "
            r2.append(r3)     // Catch:{ all -> 0x00a7 }
            com.android.server.wifi.nano.WifiMetricsProto$GroupEvent r3 = r12.mCurrentGroupEvent     // Catch:{ all -> 0x00a7 }
            int r3 = r3.netId     // Catch:{ all -> 0x00a7 }
            r2.append(r3)     // Catch:{ all -> 0x00a7 }
            java.lang.String r3 = "."
            r2.append(r3)     // Catch:{ all -> 0x00a7 }
            java.lang.String r2 = r2.toString()     // Catch:{ all -> 0x00a7 }
            android.util.Log.w(r1, r2)     // Catch:{ all -> 0x00a7 }
            monitor-exit(r0)     // Catch:{ all -> 0x00a7 }
            return
        L_0x004a:
            java.util.Collection r1 = r13.getClientList()     // Catch:{ all -> 0x00a7 }
            int r1 = r1.size()     // Catch:{ all -> 0x00a7 }
            com.android.server.wifi.nano.WifiMetricsProto$GroupEvent r2 = r12.mCurrentGroupEvent     // Catch:{ all -> 0x00a7 }
            int r2 = r2.numConnectedClients     // Catch:{ all -> 0x00a7 }
            int r1 = r1 - r2
            com.android.server.wifi.nano.WifiMetricsProto$GroupEvent r2 = r12.mCurrentGroupEvent     // Catch:{ all -> 0x00a7 }
            java.util.Collection r3 = r13.getClientList()     // Catch:{ all -> 0x00a7 }
            int r3 = r3.size()     // Catch:{ all -> 0x00a7 }
            r2.numConnectedClients = r3     // Catch:{ all -> 0x00a7 }
            if (r1 <= 0) goto L_0x006c
            com.android.server.wifi.nano.WifiMetricsProto$GroupEvent r2 = r12.mCurrentGroupEvent     // Catch:{ all -> 0x00a7 }
            int r3 = r2.numCumulativeClients     // Catch:{ all -> 0x00a7 }
            int r3 = r3 + r1
            r2.numCumulativeClients = r3     // Catch:{ all -> 0x00a7 }
        L_0x006c:
            long r2 = r12.mCurrentGroupEventIdleStartTime     // Catch:{ all -> 0x00a7 }
            r4 = 0
            int r2 = (r2 > r4 ? 1 : (r2 == r4 ? 0 : -1))
            if (r2 <= 0) goto L_0x0093
            java.util.Collection r2 = r13.getClientList()     // Catch:{ all -> 0x00a7 }
            int r2 = r2.size()     // Catch:{ all -> 0x00a7 }
            if (r2 <= 0) goto L_0x00a5
            com.android.server.wifi.nano.WifiMetricsProto$GroupEvent r2 = r12.mCurrentGroupEvent     // Catch:{ all -> 0x00a7 }
            int r3 = r2.idleDurationMillis     // Catch:{ all -> 0x00a7 }
            long r6 = (long) r3     // Catch:{ all -> 0x00a7 }
            com.android.server.wifi.Clock r3 = r12.mClock     // Catch:{ all -> 0x00a7 }
            long r8 = r3.getElapsedSinceBootMillis()     // Catch:{ all -> 0x00a7 }
            long r10 = r12.mCurrentGroupEventIdleStartTime     // Catch:{ all -> 0x00a7 }
            long r8 = r8 - r10
            long r6 = r6 + r8
            int r3 = (int) r6     // Catch:{ all -> 0x00a7 }
            r2.idleDurationMillis = r3     // Catch:{ all -> 0x00a7 }
            r12.mCurrentGroupEventIdleStartTime = r4     // Catch:{ all -> 0x00a7 }
            goto L_0x00a5
        L_0x0093:
            java.util.Collection r2 = r13.getClientList()     // Catch:{ all -> 0x00a7 }
            int r2 = r2.size()     // Catch:{ all -> 0x00a7 }
            if (r2 != 0) goto L_0x00a5
            com.android.server.wifi.Clock r2 = r12.mClock     // Catch:{ all -> 0x00a7 }
            long r2 = r2.getElapsedSinceBootMillis()     // Catch:{ all -> 0x00a7 }
            r12.mCurrentGroupEventIdleStartTime = r2     // Catch:{ all -> 0x00a7 }
        L_0x00a5:
            monitor-exit(r0)     // Catch:{ all -> 0x00a7 }
            return
        L_0x00a7:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x00a7 }
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.wifi.p2p.WifiP2pMetrics.updateGroupEvent(android.net.wifi.p2p.WifiP2pGroup):void");
    }

    public void endGroupEvent() {
        synchronized (this.mLock) {
            if (this.mCurrentGroupEvent != null) {
                this.mCurrentGroupEvent.sessionDurationMillis = (int) (this.mClock.getElapsedSinceBootMillis() - this.mCurrentGroupEventStartTime);
                if (this.mCurrentGroupEventIdleStartTime > 0) {
                    WifiMetricsProto.GroupEvent groupEvent = this.mCurrentGroupEvent;
                    groupEvent.idleDurationMillis = (int) (((long) groupEvent.idleDurationMillis) + (this.mClock.getElapsedSinceBootMillis() - this.mCurrentGroupEventIdleStartTime));
                    this.mCurrentGroupEventIdleStartTime = 0;
                }
            } else {
                Log.e(TAG, "No current group!");
            }
            this.mCurrentGroupEvent = null;
        }
    }
}
