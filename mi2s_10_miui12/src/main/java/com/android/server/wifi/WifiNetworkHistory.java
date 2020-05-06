package com.android.server.wifi;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.os.Environment;
import android.util.Log;
import com.android.server.net.DelayedDiskWrite;
import java.io.DataOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.util.BitSet;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import miui.telephony.phonenumber.Prefix;

public class WifiNetworkHistory {
    private static final String AUTH_KEY = "AUTH";
    private static final String BSSID_KEY = "BSSID";
    private static final String BSSID_KEY_END = "/BSSID";
    private static final String BSSID_STATUS_KEY = "BSSID_STATUS";
    private static final String CHOICE_KEY = "CHOICE";
    private static final String CHOICE_TIME_KEY = "CHOICE_TIME";
    private static final String CONFIG_BSSID_KEY = "CONFIG_BSSID";
    static final String CONFIG_KEY = "CONFIG";
    private static final String CONNECT_UID_KEY = "CONNECT_UID_KEY";
    private static final String CREATION_TIME_KEY = "CREATION_TIME";
    private static final String CREATOR_NAME_KEY = "CREATOR_NAME";
    static final String CREATOR_UID_KEY = "CREATOR_UID_KEY";
    private static final String DATE_KEY = "DATE";
    private static final boolean DBG = true;
    private static final String DEFAULT_GW_KEY = "DEFAULT_GW";
    private static final String DELETED_EPHEMERAL_KEY = "DELETED_EPHEMERAL";
    private static final String DID_SELF_ADD_KEY = "DID_SELF_ADD";
    private static final String EPHEMERAL_KEY = "EPHEMERAL";
    private static final String FQDN_KEY = "FQDN";
    private static final String FREQ_KEY = "FREQ";
    private static final String HAS_EVER_CONNECTED_KEY = "HAS_EVER_CONNECTED";
    private static final String LINK_KEY = "LINK";
    private static final String METERED_HINT_KEY = "METERED_HINT";
    private static final String METERED_OVERRIDE_KEY = "METERED_OVERRIDE";
    private static final String MILLI_KEY = "MILLI";
    static final String NETWORK_HISTORY_CONFIG_FILE = (Environment.getDataDirectory() + "/misc/wifi/networkHistory.txt");
    private static final String NETWORK_ID_KEY = "ID";
    private static final String NETWORK_SELECTION_DISABLE_REASON_KEY = "NETWORK_SELECTION_DISABLE_REASON";
    private static final String NETWORK_SELECTION_STATUS_KEY = "NETWORK_SELECTION_STATUS";
    private static final String NL = "\n";
    private static final String NO_INTERNET_ACCESS_EXPECTED_KEY = "NO_INTERNET_ACCESS_EXPECTED";
    private static final String NO_INTERNET_ACCESS_REPORTS_KEY = "NO_INTERNET_ACCESS_REPORTS";
    private static final String NUM_ASSOCIATION_KEY = "NUM_ASSOCIATION";
    private static final String PEER_CONFIGURATION_KEY = "PEER_CONFIGURATION";
    private static final String PRIORITY_KEY = "PRIORITY";
    private static final String RSSI_KEY = "RSSI";
    private static final String SCORER_OVERRIDE_AND_SWITCH_KEY = "SCORER_OVERRIDE_AND_SWITCH";
    private static final String SCORER_OVERRIDE_KEY = "SCORER_OVERRIDE";
    private static final String SELF_ADDED_KEY = "SELF_ADDED";
    private static final String SEPARATOR = ":  ";
    static final String SHARED_KEY = "SHARED";
    private static final String SSID_KEY = "SSID";
    public static final String TAG = "WifiNetworkHistory";
    private static final String UPDATE_NAME_KEY = "UPDATE_NAME";
    private static final String UPDATE_TIME_KEY = "UPDATE_TIME";
    private static final String UPDATE_UID_KEY = "UPDATE_UID";
    private static final String USER_APPROVED_KEY = "USER_APPROVED";
    private static final String USE_EXTERNAL_SCORES_KEY = "USE_EXTERNAL_SCORES";
    private static final String VALIDATED_INTERNET_ACCESS_KEY = "VALIDATED_INTERNET_ACCESS";
    private static final boolean VDBG = true;
    Context mContext;
    HashSet<String> mLostConfigsDbg = new HashSet<>();
    protected final DelayedDiskWrite mWriter;

    public WifiNetworkHistory(Context c, DelayedDiskWrite writer) {
        this.mContext = c;
        this.mWriter = writer;
    }

    public void writeKnownNetworkHistory(final List<WifiConfiguration> networks, final ConcurrentHashMap<Integer, ScanDetailCache> scanDetailCaches, final Set<String> deletedEphemeralSSIDs) {
        this.mWriter.write(NETWORK_HISTORY_CONFIG_FILE, new DelayedDiskWrite.Writer() {
            public void onWriteCalled(DataOutputStream out) throws IOException {
                String disableTime;
                for (WifiConfiguration config : networks) {
                    WifiConfiguration.NetworkSelectionStatus status = config.getNetworkSelectionStatus();
                    int numlink = 0;
                    if (config.linkedConfigurations != null) {
                        numlink = config.linkedConfigurations.size();
                    }
                    if (config.getNetworkSelectionStatus().isNetworkEnabled()) {
                        disableTime = Prefix.EMPTY;
                    } else {
                        disableTime = "Disable time: " + DateFormat.getInstance().format(Long.valueOf(config.getNetworkSelectionStatus().getDisableTime()));
                    }
                    WifiNetworkHistory.this.logd("saving network history: " + config.configKey() + " gw: " + config.defaultGwMacAddress + " Network Selection-status: " + status.getNetworkStatusString() + disableTime + " ephemeral=" + config.ephemeral + " choice:" + status.getConnectChoice() + " link:" + numlink + " status:" + config.status + " nid:" + config.networkId + " hasEverConnected: " + status.getHasEverConnected());
                    if (WifiNetworkHistory.this.isValid(config)) {
                        if (config.SSID == null) {
                            WifiNetworkHistory.this.logv("writeKnownNetworkHistory trying to write config with null SSID");
                        } else {
                            WifiNetworkHistory.this.logv("writeKnownNetworkHistory write config " + config.configKey());
                            out.writeUTF("CONFIG:  " + config.configKey() + WifiNetworkHistory.NL);
                            if (config.SSID != null) {
                                out.writeUTF("SSID:  " + config.SSID + WifiNetworkHistory.NL);
                            }
                            if (config.BSSID != null) {
                                out.writeUTF("CONFIG_BSSID:  " + config.BSSID + WifiNetworkHistory.NL);
                            } else {
                                out.writeUTF("CONFIG_BSSID:  null\n");
                            }
                            if (config.FQDN != null) {
                                out.writeUTF("FQDN:  " + config.FQDN + WifiNetworkHistory.NL);
                            }
                            out.writeUTF("PRIORITY:  " + Integer.toString(config.priority) + WifiNetworkHistory.NL);
                            out.writeUTF("ID:  " + Integer.toString(config.networkId) + WifiNetworkHistory.NL);
                            out.writeUTF("SELF_ADDED:  " + Boolean.toString(config.selfAdded) + WifiNetworkHistory.NL);
                            out.writeUTF("DID_SELF_ADD:  " + Boolean.toString(config.didSelfAdd) + WifiNetworkHistory.NL);
                            out.writeUTF("NO_INTERNET_ACCESS_REPORTS:  " + Integer.toString(config.numNoInternetAccessReports) + WifiNetworkHistory.NL);
                            out.writeUTF("VALIDATED_INTERNET_ACCESS:  " + Boolean.toString(config.validatedInternetAccess) + WifiNetworkHistory.NL);
                            out.writeUTF("NO_INTERNET_ACCESS_EXPECTED:  " + Boolean.toString(config.noInternetAccessExpected) + WifiNetworkHistory.NL);
                            out.writeUTF("EPHEMERAL:  " + Boolean.toString(config.ephemeral) + WifiNetworkHistory.NL);
                            out.writeUTF("METERED_HINT:  " + Boolean.toString(config.meteredHint) + WifiNetworkHistory.NL);
                            out.writeUTF("METERED_OVERRIDE:  " + Integer.toString(config.meteredOverride) + WifiNetworkHistory.NL);
                            out.writeUTF("USE_EXTERNAL_SCORES:  " + Boolean.toString(config.useExternalScores) + WifiNetworkHistory.NL);
                            if (config.creationTime != null) {
                                out.writeUTF("CREATION_TIME:  " + config.creationTime + WifiNetworkHistory.NL);
                            }
                            if (config.updateTime != null) {
                                out.writeUTF("UPDATE_TIME:  " + config.updateTime + WifiNetworkHistory.NL);
                            }
                            if (config.peerWifiConfiguration != null) {
                                out.writeUTF("PEER_CONFIGURATION:  " + config.peerWifiConfiguration + WifiNetworkHistory.NL);
                            }
                            out.writeUTF("SCORER_OVERRIDE:  " + Integer.toString(config.numScorerOverride) + WifiNetworkHistory.NL);
                            out.writeUTF("SCORER_OVERRIDE_AND_SWITCH:  " + Integer.toString(config.numScorerOverrideAndSwitchedNetwork) + WifiNetworkHistory.NL);
                            out.writeUTF("NUM_ASSOCIATION:  " + Integer.toString(config.numAssociation) + WifiNetworkHistory.NL);
                            out.writeUTF("CREATOR_UID_KEY:  " + Integer.toString(config.creatorUid) + WifiNetworkHistory.NL);
                            out.writeUTF("CONNECT_UID_KEY:  " + Integer.toString(config.lastConnectUid) + WifiNetworkHistory.NL);
                            out.writeUTF("UPDATE_UID:  " + Integer.toString(config.lastUpdateUid) + WifiNetworkHistory.NL);
                            out.writeUTF("CREATOR_NAME:  " + config.creatorName + WifiNetworkHistory.NL);
                            out.writeUTF("UPDATE_NAME:  " + config.lastUpdateName + WifiNetworkHistory.NL);
                            out.writeUTF("USER_APPROVED:  " + Integer.toString(config.userApproved) + WifiNetworkHistory.NL);
                            out.writeUTF("SHARED:  " + Boolean.toString(config.shared) + WifiNetworkHistory.NL);
                            out.writeUTF("AUTH:  " + WifiNetworkHistory.makeString(config.allowedKeyManagement, WifiConfiguration.KeyMgmt.strings) + WifiNetworkHistory.NL);
                            out.writeUTF("NETWORK_SELECTION_STATUS:  " + status.getNetworkSelectionStatus() + WifiNetworkHistory.NL);
                            out.writeUTF("NETWORK_SELECTION_DISABLE_REASON:  " + status.getNetworkSelectionDisableReason() + WifiNetworkHistory.NL);
                            if (status.getConnectChoice() != null) {
                                out.writeUTF("CHOICE:  " + status.getConnectChoice() + WifiNetworkHistory.NL);
                                out.writeUTF("CHOICE_TIME:  " + status.getConnectChoiceTimestamp() + WifiNetworkHistory.NL);
                            }
                            if (config.linkedConfigurations != null) {
                                WifiNetworkHistory.this.log("writeKnownNetworkHistory write linked " + config.linkedConfigurations.size());
                                for (String key : config.linkedConfigurations.keySet()) {
                                    out.writeUTF("LINK:  " + key + WifiNetworkHistory.NL);
                                }
                            }
                            String macAddress = config.defaultGwMacAddress;
                            if (macAddress != null) {
                                out.writeUTF("DEFAULT_GW:  " + macAddress + WifiNetworkHistory.NL);
                            }
                            if (WifiNetworkHistory.this.getScanDetailCache(config, scanDetailCaches) != null) {
                                for (ScanDetail scanDetail : WifiNetworkHistory.this.getScanDetailCache(config, scanDetailCaches).values()) {
                                    ScanResult result = scanDetail.getScanResult();
                                    out.writeUTF("BSSID:  " + result.BSSID + WifiNetworkHistory.NL);
                                    out.writeUTF("FREQ:  " + Integer.toString(result.frequency) + WifiNetworkHistory.NL);
                                    out.writeUTF("RSSI:  " + Integer.toString(result.level) + WifiNetworkHistory.NL);
                                    out.writeUTF("/BSSID\n");
                                }
                            }
                            out.writeUTF("HAS_EVER_CONNECTED:  " + Boolean.toString(status.getHasEverConnected()) + WifiNetworkHistory.NL);
                            out.writeUTF(WifiNetworkHistory.NL);
                            out.writeUTF(WifiNetworkHistory.NL);
                            out.writeUTF(WifiNetworkHistory.NL);
                        }
                    }
                }
                Set set = deletedEphemeralSSIDs;
                if (set != null && set.size() > 0) {
                    for (String ssid : deletedEphemeralSSIDs) {
                        out.writeUTF(WifiNetworkHistory.DELETED_EPHEMERAL_KEY);
                        out.writeUTF(ssid);
                        out.writeUTF(WifiNetworkHistory.NL);
                    }
                }
            }
        });
    }

    /* Debug info: failed to restart local var, previous not found, register: 29 */
    /* JADX WARNING: Can't fix incorrect switch cases order */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void readNetworkHistory(java.util.Map<java.lang.String, android.net.wifi.WifiConfiguration> r30, java.util.Map<java.lang.Integer, com.android.server.wifi.ScanDetailCache> r31, java.util.Set<java.lang.String> r32) {
        /*
            r29 = this;
            r1 = r29
            r2 = r31
            java.lang.String r3 = "WifiNetworkHistory"
            java.io.DataInputStream r0 = new java.io.DataInputStream     // Catch:{ EOFException -> 0x0599, FileNotFoundException -> 0x0583, NumberFormatException -> 0x056d, IOException -> 0x0557 }
            java.io.BufferedInputStream r4 = new java.io.BufferedInputStream     // Catch:{ EOFException -> 0x0599, FileNotFoundException -> 0x0583, NumberFormatException -> 0x056d, IOException -> 0x0557 }
            java.io.FileInputStream r5 = new java.io.FileInputStream     // Catch:{ EOFException -> 0x0599, FileNotFoundException -> 0x0583, NumberFormatException -> 0x056d, IOException -> 0x0557 }
            java.lang.String r6 = NETWORK_HISTORY_CONFIG_FILE     // Catch:{ EOFException -> 0x0599, FileNotFoundException -> 0x0583, NumberFormatException -> 0x056d, IOException -> 0x0557 }
            r5.<init>(r6)     // Catch:{ EOFException -> 0x0599, FileNotFoundException -> 0x0583, NumberFormatException -> 0x056d, IOException -> 0x0557 }
            r4.<init>(r5)     // Catch:{ EOFException -> 0x0599, FileNotFoundException -> 0x0583, NumberFormatException -> 0x056d, IOException -> 0x0557 }
            r0.<init>(r4)     // Catch:{ EOFException -> 0x0599, FileNotFoundException -> 0x0583, NumberFormatException -> 0x056d, IOException -> 0x0557 }
            r4 = r0
            r0 = 0
            r5 = 0
            r6 = 0
            r7 = 0
            r8 = 0
            int r10 = android.net.wifi.WifiConfiguration.INVALID_RSSI     // Catch:{ all -> 0x0546 }
            r11 = 0
            r12 = 0
            r13 = r12
        L_0x0023:
            java.lang.String r14 = r4.readUTF()     // Catch:{ all -> 0x0546 }
            if (r14 == 0) goto L_0x053a
            boolean r15 = r14.isEmpty()     // Catch:{ all -> 0x0546 }
            if (r15 == 0) goto L_0x0033
            r26 = r4
            goto L_0x0542
        L_0x0033:
            r15 = 58
            int r15 = r14.indexOf(r15)     // Catch:{ all -> 0x0546 }
            if (r15 >= 0) goto L_0x003c
            goto L_0x0023
        L_0x003c:
            r12 = 0
            java.lang.String r16 = r14.substring(r12, r15)     // Catch:{ all -> 0x0546 }
            java.lang.String r16 = r16.trim()     // Catch:{ all -> 0x0546 }
            r24 = r16
            int r12 = r15 + 1
            java.lang.String r12 = r14.substring(r12)     // Catch:{ all -> 0x0546 }
            java.lang.String r12 = r12.trim()     // Catch:{ all -> 0x0546 }
            r25 = r7
            java.lang.String r7 = "CONFIG"
            r26 = r4
            r4 = r24
            boolean r7 = r4.equals(r7)     // Catch:{ all -> 0x0537 }
            if (r7 == 0) goto L_0x00dc
            r7 = r30
            java.lang.Object r16 = r7.get(r12)     // Catch:{ all -> 0x0537 }
            android.net.wifi.WifiConfiguration r16 = (android.net.wifi.WifiConfiguration) r16     // Catch:{ all -> 0x0537 }
            r13 = r16
            if (r13 != 0) goto L_0x009c
            java.lang.StringBuilder r7 = new java.lang.StringBuilder     // Catch:{ all -> 0x0537 }
            r7.<init>()     // Catch:{ all -> 0x0537 }
            r17 = r14
            java.lang.String r14 = "readNetworkHistory didnt find netid for hash="
            r7.append(r14)     // Catch:{ all -> 0x0537 }
            int r14 = r12.hashCode()     // Catch:{ all -> 0x0537 }
            java.lang.String r14 = java.lang.Integer.toString(r14)     // Catch:{ all -> 0x0537 }
            r7.append(r14)     // Catch:{ all -> 0x0537 }
            java.lang.String r14 = " key: "
            r7.append(r14)     // Catch:{ all -> 0x0537 }
            r7.append(r12)     // Catch:{ all -> 0x0537 }
            java.lang.String r7 = r7.toString()     // Catch:{ all -> 0x0537 }
            android.util.Log.e(r3, r7)     // Catch:{ all -> 0x0537 }
            java.util.HashSet<java.lang.String> r7 = r1.mLostConfigsDbg     // Catch:{ all -> 0x0537 }
            r7.add(r12)     // Catch:{ all -> 0x0537 }
            r7 = r25
            r4 = r26
            r12 = 0
            goto L_0x0023
        L_0x009c:
            r17 = r14
            java.lang.String r7 = r13.creatorName     // Catch:{ all -> 0x0537 }
            if (r7 == 0) goto L_0x00a6
            java.lang.String r7 = r13.lastUpdateName     // Catch:{ all -> 0x0537 }
            if (r7 != 0) goto L_0x00d8
        L_0x00a6:
            android.content.Context r7 = r1.mContext     // Catch:{ all -> 0x0537 }
            android.content.pm.PackageManager r7 = r7.getPackageManager()     // Catch:{ all -> 0x0537 }
            r14 = 1000(0x3e8, float:1.401E-42)
            java.lang.String r7 = r7.getNameForUid(r14)     // Catch:{ all -> 0x0537 }
            r13.creatorName = r7     // Catch:{ all -> 0x0537 }
            java.lang.String r7 = r13.creatorName     // Catch:{ all -> 0x0537 }
            r13.lastUpdateName = r7     // Catch:{ all -> 0x0537 }
            java.lang.StringBuilder r7 = new java.lang.StringBuilder     // Catch:{ all -> 0x0537 }
            r7.<init>()     // Catch:{ all -> 0x0537 }
            java.lang.String r14 = "Upgrading network "
            r7.append(r14)     // Catch:{ all -> 0x0537 }
            int r14 = r13.networkId     // Catch:{ all -> 0x0537 }
            r7.append(r14)     // Catch:{ all -> 0x0537 }
            java.lang.String r14 = " to "
            r7.append(r14)     // Catch:{ all -> 0x0537 }
            java.lang.String r14 = r13.creatorName     // Catch:{ all -> 0x0537 }
            r7.append(r14)     // Catch:{ all -> 0x0537 }
            java.lang.String r7 = r7.toString()     // Catch:{ all -> 0x0537 }
            android.util.Log.w(r3, r7)     // Catch:{ all -> 0x0537 }
        L_0x00d8:
            r7 = r25
            goto L_0x0532
        L_0x00dc:
            r17 = r14
            if (r13 == 0) goto L_0x0528
            android.net.wifi.WifiConfiguration$NetworkSelectionStatus r7 = r13.getNetworkSelectionStatus()     // Catch:{ all -> 0x0537 }
            int r14 = r4.hashCode()     // Catch:{ all -> 0x0537 }
            r18 = -1
            r19 = r15
            switch(r14) {
                case -1946896213: goto L_0x029d;
                case -1866906821: goto L_0x0292;
                case -1865201711: goto L_0x0288;
                case -1850236827: goto L_0x027d;
                case -1842190690: goto L_0x0272;
                case -1646996988: goto L_0x0268;
                case -1103645511: goto L_0x025d;
                case -1025978637: goto L_0x0252;
                case -944985731: goto L_0x0247;
                case -552300306: goto L_0x023c;
                case -534910080: goto L_0x0231;
                case -375674826: goto L_0x0225;
                case 2090926: goto L_0x0219;
                case 2165397: goto L_0x020e;
                case 2166392: goto L_0x0202;
                case 2336762: goto L_0x01f6;
                case 2525271: goto L_0x01ea;
                case 2554747: goto L_0x01df;
                case 63507133: goto L_0x01d3;
                case 89250059: goto L_0x01c7;
                case 190453690: goto L_0x01bb;
                case 417823927: goto L_0x01af;
                case 501180973: goto L_0x01a4;
                case 740738782: goto L_0x0198;
                case 782347629: goto L_0x018c;
                case 783161389: goto L_0x0180;
                case 1163774926: goto L_0x0175;
                case 1187625059: goto L_0x0169;
                case 1366197215: goto L_0x015d;
                case 1409077230: goto L_0x0151;
                case 1477121648: goto L_0x0145;
                case 1608881217: goto L_0x0139;
                case 1609067651: goto L_0x012d;
                case 1614319275: goto L_0x0121;
                case 1928336648: goto L_0x0115;
                case 1946216573: goto L_0x0109;
                case 1987072417: goto L_0x00fd;
                case 2026657853: goto L_0x00f1;
                default: goto L_0x00ef;
            }     // Catch:{ all -> 0x0537 }
        L_0x00ef:
            goto L_0x02a8
        L_0x00f1:
            java.lang.String r14 = "USE_EXTERNAL_SCORES"
            boolean r14 = r4.equals(r14)     // Catch:{ all -> 0x0537 }
            if (r14 == 0) goto L_0x00ef
            r14 = 14
            goto L_0x02aa
        L_0x00fd:
            java.lang.String r14 = "CHOICE"
            boolean r14 = r4.equals(r14)     // Catch:{ all -> 0x0537 }
            if (r14 == 0) goto L_0x00ef
            r14 = 24
            goto L_0x02aa
        L_0x0109:
            java.lang.String r14 = "CREATOR_UID_KEY"
            boolean r14 = r4.equals(r14)     // Catch:{ all -> 0x0537 }
            if (r14 == 0) goto L_0x00ef
            r14 = 15
            goto L_0x02aa
        L_0x0115:
            java.lang.String r14 = "NUM_ASSOCIATION"
            boolean r14 = r4.equals(r14)     // Catch:{ all -> 0x0537 }
            if (r14 == 0) goto L_0x00ef
            r14 = 18
            goto L_0x02aa
        L_0x0121:
            java.lang.String r14 = "CHOICE_TIME"
            boolean r14 = r4.equals(r14)     // Catch:{ all -> 0x0537 }
            if (r14 == 0) goto L_0x00ef
            r14 = 25
            goto L_0x02aa
        L_0x012d:
            java.lang.String r14 = "UPDATE_TIME"
            boolean r14 = r4.equals(r14)     // Catch:{ all -> 0x0537 }
            if (r14 == 0) goto L_0x00ef
            r14 = 10
            goto L_0x02aa
        L_0x0139:
            java.lang.String r14 = "UPDATE_NAME"
            boolean r14 = r4.equals(r14)     // Catch:{ all -> 0x0537 }
            if (r14 == 0) goto L_0x00ef
            r14 = 34
            goto L_0x02aa
        L_0x0145:
            java.lang.String r14 = "SCORER_OVERRIDE_AND_SWITCH"
            boolean r14 = r4.equals(r14)     // Catch:{ all -> 0x0537 }
            if (r14 == 0) goto L_0x00ef
            r14 = 17
            goto L_0x02aa
        L_0x0151:
            java.lang.String r14 = "/BSSID"
            boolean r14 = r4.equals(r14)     // Catch:{ all -> 0x0537 }
            if (r14 == 0) goto L_0x00ef
            r14 = 31
            goto L_0x02aa
        L_0x015d:
            java.lang.String r14 = "NETWORK_SELECTION_DISABLE_REASON"
            boolean r14 = r4.equals(r14)     // Catch:{ all -> 0x0537 }
            if (r14 == 0) goto L_0x00ef
            r14 = 23
            goto L_0x02aa
        L_0x0169:
            java.lang.String r14 = "METERED_OVERRIDE"
            boolean r14 = r4.equals(r14)     // Catch:{ all -> 0x0537 }
            if (r14 == 0) goto L_0x00ef
            r14 = 13
            goto L_0x02aa
        L_0x0175:
            java.lang.String r14 = "DEFAULT_GW"
            boolean r14 = r4.equals(r14)     // Catch:{ all -> 0x0537 }
            if (r14 == 0) goto L_0x00ef
            r14 = 3
            goto L_0x02aa
        L_0x0180:
            java.lang.String r14 = "CREATION_TIME"
            boolean r14 = r4.equals(r14)     // Catch:{ all -> 0x0537 }
            if (r14 == 0) goto L_0x00ef
            r14 = 9
            goto L_0x02aa
        L_0x018c:
            java.lang.String r14 = "HAS_EVER_CONNECTED"
            boolean r14 = r4.equals(r14)     // Catch:{ all -> 0x0537 }
            if (r14 == 0) goto L_0x00ef
            r14 = 37
            goto L_0x02aa
        L_0x0198:
            java.lang.String r14 = "CREATOR_NAME"
            boolean r14 = r4.equals(r14)     // Catch:{ all -> 0x0537 }
            if (r14 == 0) goto L_0x00ef
            r14 = 33
            goto L_0x02aa
        L_0x01a4:
            java.lang.String r14 = "SELF_ADDED"
            boolean r14 = r4.equals(r14)     // Catch:{ all -> 0x0537 }
            if (r14 == 0) goto L_0x00ef
            r14 = 4
            goto L_0x02aa
        L_0x01af:
            java.lang.String r14 = "DELETED_EPHEMERAL"
            boolean r14 = r4.equals(r14)     // Catch:{ all -> 0x0537 }
            if (r14 == 0) goto L_0x00ef
            r14 = 32
            goto L_0x02aa
        L_0x01bb:
            java.lang.String r14 = "UPDATE_UID"
            boolean r14 = r4.equals(r14)     // Catch:{ all -> 0x0537 }
            if (r14 == 0) goto L_0x00ef
            r14 = 20
            goto L_0x02aa
        L_0x01c7:
            java.lang.String r14 = "SCORER_OVERRIDE"
            boolean r14 = r4.equals(r14)     // Catch:{ all -> 0x0537 }
            if (r14 == 0) goto L_0x00ef
            r14 = 16
            goto L_0x02aa
        L_0x01d3:
            java.lang.String r14 = "BSSID"
            boolean r14 = r4.equals(r14)     // Catch:{ all -> 0x0537 }
            if (r14 == 0) goto L_0x00ef
            r14 = 27
            goto L_0x02aa
        L_0x01df:
            java.lang.String r14 = "SSID"
            boolean r14 = r4.equals(r14)     // Catch:{ all -> 0x0537 }
            if (r14 == 0) goto L_0x00ef
            r14 = 0
            goto L_0x02aa
        L_0x01ea:
            java.lang.String r14 = "RSSI"
            boolean r14 = r4.equals(r14)     // Catch:{ all -> 0x0537 }
            if (r14 == 0) goto L_0x00ef
            r14 = 28
            goto L_0x02aa
        L_0x01f6:
            java.lang.String r14 = "LINK"
            boolean r14 = r4.equals(r14)     // Catch:{ all -> 0x0537 }
            if (r14 == 0) goto L_0x00ef
            r14 = 26
            goto L_0x02aa
        L_0x0202:
            java.lang.String r14 = "FREQ"
            boolean r14 = r4.equals(r14)     // Catch:{ all -> 0x0537 }
            if (r14 == 0) goto L_0x00ef
            r14 = 29
            goto L_0x02aa
        L_0x020e:
            java.lang.String r14 = "FQDN"
            boolean r14 = r4.equals(r14)     // Catch:{ all -> 0x0537 }
            if (r14 == 0) goto L_0x00ef
            r14 = 2
            goto L_0x02aa
        L_0x0219:
            java.lang.String r14 = "DATE"
            boolean r14 = r4.equals(r14)     // Catch:{ all -> 0x0537 }
            if (r14 == 0) goto L_0x00ef
            r14 = 30
            goto L_0x02aa
        L_0x0225:
            java.lang.String r14 = "NETWORK_SELECTION_STATUS"
            boolean r14 = r4.equals(r14)     // Catch:{ all -> 0x0537 }
            if (r14 == 0) goto L_0x00ef
            r14 = 22
            goto L_0x02aa
        L_0x0231:
            java.lang.String r14 = "CONFIG_BSSID"
            boolean r14 = r4.equals(r14)     // Catch:{ all -> 0x0537 }
            if (r14 == 0) goto L_0x00ef
            r14 = 1
            goto L_0x02aa
        L_0x023c:
            java.lang.String r14 = "DID_SELF_ADD"
            boolean r14 = r4.equals(r14)     // Catch:{ all -> 0x0537 }
            if (r14 == 0) goto L_0x00ef
            r14 = 5
            goto L_0x02aa
        L_0x0247:
            java.lang.String r14 = "EPHEMERAL"
            boolean r14 = r4.equals(r14)     // Catch:{ all -> 0x0537 }
            if (r14 == 0) goto L_0x00ef
            r14 = 11
            goto L_0x02aa
        L_0x0252:
            java.lang.String r14 = "NO_INTERNET_ACCESS_EXPECTED"
            boolean r14 = r4.equals(r14)     // Catch:{ all -> 0x0537 }
            if (r14 == 0) goto L_0x00ef
            r14 = 8
            goto L_0x02aa
        L_0x025d:
            java.lang.String r14 = "PEER_CONFIGURATION"
            boolean r14 = r4.equals(r14)     // Catch:{ all -> 0x0537 }
            if (r14 == 0) goto L_0x00ef
            r14 = 21
            goto L_0x02aa
        L_0x0268:
            java.lang.String r14 = "NO_INTERNET_ACCESS_REPORTS"
            boolean r14 = r4.equals(r14)     // Catch:{ all -> 0x0537 }
            if (r14 == 0) goto L_0x00ef
            r14 = 6
            goto L_0x02aa
        L_0x0272:
            java.lang.String r14 = "METERED_HINT"
            boolean r14 = r4.equals(r14)     // Catch:{ all -> 0x0537 }
            if (r14 == 0) goto L_0x00ef
            r14 = 12
            goto L_0x02aa
        L_0x027d:
            java.lang.String r14 = "SHARED"
            boolean r14 = r4.equals(r14)     // Catch:{ all -> 0x0537 }
            if (r14 == 0) goto L_0x00ef
            r14 = 36
            goto L_0x02aa
        L_0x0288:
            java.lang.String r14 = "VALIDATED_INTERNET_ACCESS"
            boolean r14 = r4.equals(r14)     // Catch:{ all -> 0x0537 }
            if (r14 == 0) goto L_0x00ef
            r14 = 7
            goto L_0x02aa
        L_0x0292:
            java.lang.String r14 = "CONNECT_UID_KEY"
            boolean r14 = r4.equals(r14)     // Catch:{ all -> 0x0537 }
            if (r14 == 0) goto L_0x00ef
            r14 = 19
            goto L_0x02aa
        L_0x029d:
            java.lang.String r14 = "USER_APPROVED"
            boolean r14 = r4.equals(r14)     // Catch:{ all -> 0x0537 }
            if (r14 == 0) goto L_0x00ef
            r14 = 35
            goto L_0x02aa
        L_0x02a8:
            r14 = r18
        L_0x02aa:
            java.lang.String r15 = "null"
            switch(r14) {
                case 0: goto L_0x04f8;
                case 1: goto L_0x04e6;
                case 2: goto L_0x04d4;
                case 3: goto L_0x04ca;
                case 4: goto L_0x04bc;
                case 5: goto L_0x04ae;
                case 6: goto L_0x04a0;
                case 7: goto L_0x0492;
                case 8: goto L_0x0484;
                case 9: goto L_0x047a;
                case 10: goto L_0x0470;
                case 11: goto L_0x0462;
                case 12: goto L_0x0454;
                case 13: goto L_0x0446;
                case 14: goto L_0x0438;
                case 15: goto L_0x042a;
                case 16: goto L_0x041c;
                case 17: goto L_0x040e;
                case 18: goto L_0x0400;
                case 19: goto L_0x03f2;
                case 20: goto L_0x03e4;
                case 21: goto L_0x03da;
                case 22: goto L_0x03c7;
                case 23: goto L_0x03b8;
                case 24: goto L_0x03ad;
                case 25: goto L_0x039e;
                case 26: goto L_0x0380;
                case 27: goto L_0x036a;
                case 28: goto L_0x0359;
                case 29: goto L_0x0348;
                case 30: goto L_0x0340;
                case 31: goto L_0x02fc;
                case 32: goto L_0x02e2;
                case 33: goto L_0x02dc;
                case 34: goto L_0x02d6;
                case 35: goto L_0x02cc;
                case 36: goto L_0x02c2;
                case 37: goto L_0x02b7;
                default: goto L_0x02af;
            }
        L_0x02af:
            r28 = r17
            r24 = r19
            r17 = r0
            goto L_0x052e
        L_0x02b7:
            boolean r14 = java.lang.Boolean.parseBoolean(r12)     // Catch:{ all -> 0x0537 }
            r7.setHasEverConnected(r14)     // Catch:{ all -> 0x0537 }
            r17 = r0
            goto L_0x052e
        L_0x02c2:
            boolean r14 = java.lang.Boolean.parseBoolean(r12)     // Catch:{ all -> 0x0537 }
            r13.shared = r14     // Catch:{ all -> 0x0537 }
            r17 = r0
            goto L_0x052e
        L_0x02cc:
            int r14 = java.lang.Integer.parseInt(r12)     // Catch:{ all -> 0x0537 }
            r13.userApproved = r14     // Catch:{ all -> 0x0537 }
            r17 = r0
            goto L_0x052e
        L_0x02d6:
            r13.lastUpdateName = r12     // Catch:{ all -> 0x0537 }
            r17 = r0
            goto L_0x052e
        L_0x02dc:
            r13.creatorName = r12     // Catch:{ all -> 0x0537 }
            r17 = r0
            goto L_0x052e
        L_0x02e2:
            boolean r14 = android.text.TextUtils.isEmpty(r12)     // Catch:{ all -> 0x02f7 }
            if (r14 != 0) goto L_0x02f1
            r14 = r32
            r14.add(r12)     // Catch:{ all -> 0x0537 }
            r17 = r0
            goto L_0x052e
        L_0x02f1:
            r14 = r32
            r17 = r0
            goto L_0x052e
        L_0x02f7:
            r0 = move-exception
            r14 = r32
            goto L_0x0538
        L_0x02fc:
            r14 = r32
            if (r0 == 0) goto L_0x0338
            if (r5 == 0) goto L_0x0338
            com.android.server.wifi.ScanDetailCache r15 = r1.getScanDetailCache(r13, r2)     // Catch:{ all -> 0x0537 }
            if (r15 == 0) goto L_0x0330
            android.net.wifi.WifiSsid r15 = android.net.wifi.WifiSsid.createFromAsciiEncoded(r5)     // Catch:{ all -> 0x0537 }
            r24 = r19
            com.android.server.wifi.ScanDetail r27 = new com.android.server.wifi.ScanDetail     // Catch:{ all -> 0x0537 }
            r20 = 0
            r28 = r17
            r14 = r27
            r16 = r0
            r17 = r11
            r18 = r10
            r19 = r6
            r22 = r8
            r14.<init>(r15, r16, r17, r18, r19, r20, r22)     // Catch:{ all -> 0x0537 }
            r14 = r27
            r17 = r0
            com.android.server.wifi.ScanDetailCache r0 = r1.getScanDetailCache(r13, r2)     // Catch:{ all -> 0x0537 }
            r0.put(r14)     // Catch:{ all -> 0x0537 }
            goto L_0x052e
        L_0x0330:
            r28 = r17
            r24 = r19
            r17 = r0
            goto L_0x052e
        L_0x0338:
            r28 = r17
            r24 = r19
            r17 = r0
            goto L_0x052e
        L_0x0340:
            r28 = r17
            r24 = r19
            r17 = r0
            goto L_0x052e
        L_0x0348:
            r28 = r17
            r24 = r19
            r17 = r0
            int r0 = java.lang.Integer.parseInt(r12)     // Catch:{ all -> 0x0537 }
            r6 = r0
            r0 = r17
            r7 = r25
            goto L_0x0532
        L_0x0359:
            r28 = r17
            r24 = r19
            r17 = r0
            int r0 = java.lang.Integer.parseInt(r12)     // Catch:{ all -> 0x0537 }
            r10 = r0
            r0 = r17
            r7 = r25
            goto L_0x0532
        L_0x036a:
            r28 = r17
            r24 = r19
            r17 = r0
            r0 = 0
            r5 = 0
            r14 = 0
            r6 = 0
            r8 = 0
            int r15 = android.net.wifi.WifiConfiguration.INVALID_RSSI     // Catch:{ all -> 0x0537 }
            r10 = r15
            java.lang.String r15 = ""
            r11 = r15
            r7 = r0
            r0 = r14
            goto L_0x0532
        L_0x0380:
            r28 = r17
            r24 = r19
            r17 = r0
            java.util.HashMap r0 = r13.linkedConfigurations     // Catch:{ all -> 0x0537 }
            if (r0 != 0) goto L_0x0393
            java.util.HashMap r0 = new java.util.HashMap     // Catch:{ all -> 0x0537 }
            r0.<init>()     // Catch:{ all -> 0x0537 }
            r13.linkedConfigurations = r0     // Catch:{ all -> 0x0537 }
            goto L_0x052e
        L_0x0393:
            java.util.HashMap r0 = r13.linkedConfigurations     // Catch:{ all -> 0x0537 }
            java.lang.Integer r14 = java.lang.Integer.valueOf(r18)     // Catch:{ all -> 0x0537 }
            r0.put(r12, r14)     // Catch:{ all -> 0x0537 }
            goto L_0x052e
        L_0x039e:
            r28 = r17
            r24 = r19
            r17 = r0
            long r14 = java.lang.Long.parseLong(r12)     // Catch:{ all -> 0x0537 }
            r7.setConnectChoiceTimestamp(r14)     // Catch:{ all -> 0x0537 }
            goto L_0x052e
        L_0x03ad:
            r28 = r17
            r24 = r19
            r17 = r0
            r7.setConnectChoice(r12)     // Catch:{ all -> 0x0537 }
            goto L_0x052e
        L_0x03b8:
            r28 = r17
            r24 = r19
            r17 = r0
            int r0 = java.lang.Integer.parseInt(r12)     // Catch:{ all -> 0x0537 }
            r7.setNetworkSelectionDisableReason(r0)     // Catch:{ all -> 0x0537 }
            goto L_0x052e
        L_0x03c7:
            r28 = r17
            r24 = r19
            r17 = r0
            int r0 = java.lang.Integer.parseInt(r12)     // Catch:{ all -> 0x0537 }
            r14 = 1
            if (r0 != r14) goto L_0x03d5
            r0 = 0
        L_0x03d5:
            r7.setNetworkSelectionStatus(r0)     // Catch:{ all -> 0x0537 }
            goto L_0x052e
        L_0x03da:
            r28 = r17
            r24 = r19
            r17 = r0
            r13.peerWifiConfiguration = r12     // Catch:{ all -> 0x0537 }
            goto L_0x052e
        L_0x03e4:
            r28 = r17
            r24 = r19
            r17 = r0
            int r0 = java.lang.Integer.parseInt(r12)     // Catch:{ all -> 0x0537 }
            r13.lastUpdateUid = r0     // Catch:{ all -> 0x0537 }
            goto L_0x052e
        L_0x03f2:
            r28 = r17
            r24 = r19
            r17 = r0
            int r0 = java.lang.Integer.parseInt(r12)     // Catch:{ all -> 0x0537 }
            r13.lastConnectUid = r0     // Catch:{ all -> 0x0537 }
            goto L_0x052e
        L_0x0400:
            r28 = r17
            r24 = r19
            r17 = r0
            int r0 = java.lang.Integer.parseInt(r12)     // Catch:{ all -> 0x0537 }
            r13.numAssociation = r0     // Catch:{ all -> 0x0537 }
            goto L_0x052e
        L_0x040e:
            r28 = r17
            r24 = r19
            r17 = r0
            int r0 = java.lang.Integer.parseInt(r12)     // Catch:{ all -> 0x0537 }
            r13.numScorerOverrideAndSwitchedNetwork = r0     // Catch:{ all -> 0x0537 }
            goto L_0x052e
        L_0x041c:
            r28 = r17
            r24 = r19
            r17 = r0
            int r0 = java.lang.Integer.parseInt(r12)     // Catch:{ all -> 0x0537 }
            r13.numScorerOverride = r0     // Catch:{ all -> 0x0537 }
            goto L_0x052e
        L_0x042a:
            r28 = r17
            r24 = r19
            r17 = r0
            int r0 = java.lang.Integer.parseInt(r12)     // Catch:{ all -> 0x0537 }
            r13.creatorUid = r0     // Catch:{ all -> 0x0537 }
            goto L_0x052e
        L_0x0438:
            r28 = r17
            r24 = r19
            r17 = r0
            boolean r0 = java.lang.Boolean.parseBoolean(r12)     // Catch:{ all -> 0x0537 }
            r13.useExternalScores = r0     // Catch:{ all -> 0x0537 }
            goto L_0x052e
        L_0x0446:
            r28 = r17
            r24 = r19
            r17 = r0
            int r0 = java.lang.Integer.parseInt(r12)     // Catch:{ all -> 0x0537 }
            r13.meteredOverride = r0     // Catch:{ all -> 0x0537 }
            goto L_0x052e
        L_0x0454:
            r28 = r17
            r24 = r19
            r17 = r0
            boolean r0 = java.lang.Boolean.parseBoolean(r12)     // Catch:{ all -> 0x0537 }
            r13.meteredHint = r0     // Catch:{ all -> 0x0537 }
            goto L_0x052e
        L_0x0462:
            r28 = r17
            r24 = r19
            r17 = r0
            boolean r0 = java.lang.Boolean.parseBoolean(r12)     // Catch:{ all -> 0x0537 }
            r13.ephemeral = r0     // Catch:{ all -> 0x0537 }
            goto L_0x052e
        L_0x0470:
            r28 = r17
            r24 = r19
            r17 = r0
            r13.updateTime = r12     // Catch:{ all -> 0x0537 }
            goto L_0x052e
        L_0x047a:
            r28 = r17
            r24 = r19
            r17 = r0
            r13.creationTime = r12     // Catch:{ all -> 0x0537 }
            goto L_0x052e
        L_0x0484:
            r28 = r17
            r24 = r19
            r17 = r0
            boolean r0 = java.lang.Boolean.parseBoolean(r12)     // Catch:{ all -> 0x0537 }
            r13.noInternetAccessExpected = r0     // Catch:{ all -> 0x0537 }
            goto L_0x052e
        L_0x0492:
            r28 = r17
            r24 = r19
            r17 = r0
            boolean r0 = java.lang.Boolean.parseBoolean(r12)     // Catch:{ all -> 0x0537 }
            r13.validatedInternetAccess = r0     // Catch:{ all -> 0x0537 }
            goto L_0x052e
        L_0x04a0:
            r28 = r17
            r24 = r19
            r17 = r0
            int r0 = java.lang.Integer.parseInt(r12)     // Catch:{ all -> 0x0537 }
            r13.numNoInternetAccessReports = r0     // Catch:{ all -> 0x0537 }
            goto L_0x052e
        L_0x04ae:
            r28 = r17
            r24 = r19
            r17 = r0
            boolean r0 = java.lang.Boolean.parseBoolean(r12)     // Catch:{ all -> 0x0537 }
            r13.didSelfAdd = r0     // Catch:{ all -> 0x0537 }
            goto L_0x052e
        L_0x04bc:
            r28 = r17
            r24 = r19
            r17 = r0
            boolean r0 = java.lang.Boolean.parseBoolean(r12)     // Catch:{ all -> 0x0537 }
            r13.selfAdded = r0     // Catch:{ all -> 0x0537 }
            goto L_0x052e
        L_0x04ca:
            r28 = r17
            r24 = r19
            r17 = r0
            r13.defaultGwMacAddress = r12     // Catch:{ all -> 0x0537 }
            goto L_0x052e
        L_0x04d4:
            r28 = r17
            r24 = r19
            r17 = r0
            boolean r0 = r12.equals(r15)     // Catch:{ all -> 0x0537 }
            if (r0 == 0) goto L_0x04e2
            r0 = 0
            goto L_0x04e3
        L_0x04e2:
            r0 = r12
        L_0x04e3:
            r13.FQDN = r0     // Catch:{ all -> 0x0537 }
            goto L_0x052e
        L_0x04e6:
            r28 = r17
            r24 = r19
            r17 = r0
            boolean r0 = r12.equals(r15)     // Catch:{ all -> 0x0537 }
            if (r0 == 0) goto L_0x04f4
            r0 = 0
            goto L_0x04f5
        L_0x04f4:
            r0 = r12
        L_0x04f5:
            r13.BSSID = r0     // Catch:{ all -> 0x0537 }
            goto L_0x052e
        L_0x04f8:
            r28 = r17
            r24 = r19
            r17 = r0
            boolean r0 = r13.isPasspoint()     // Catch:{ all -> 0x0537 }
            if (r0 == 0) goto L_0x0505
            goto L_0x052e
        L_0x0505:
            r0 = r12
            java.lang.String r5 = r13.SSID     // Catch:{ all -> 0x0537 }
            if (r5 == 0) goto L_0x0520
            java.lang.String r5 = r13.SSID     // Catch:{ all -> 0x0537 }
            boolean r5 = r5.equals(r0)     // Catch:{ all -> 0x0537 }
            if (r5 != 0) goto L_0x0520
            java.lang.String r5 = "Error parsing network history file, mismatched SSIDs"
            r1.loge(r5)     // Catch:{ all -> 0x0537 }
            r5 = 0
            r0 = 0
            r13 = r5
            r7 = r25
            r5 = r0
            r0 = r17
            goto L_0x0532
        L_0x0520:
            r13.SSID = r0     // Catch:{ all -> 0x0537 }
            r5 = r0
            r0 = r17
            r7 = r25
            goto L_0x0532
        L_0x0528:
            r24 = r15
            r28 = r17
            r17 = r0
        L_0x052e:
            r0 = r17
            r7 = r25
        L_0x0532:
            r4 = r26
            r12 = 0
            goto L_0x0023
        L_0x0537:
            r0 = move-exception
        L_0x0538:
            r4 = r0
            goto L_0x054a
        L_0x053a:
            r17 = r0
            r26 = r4
            r25 = r7
            r28 = r14
        L_0x0542:
            r26.close()     // Catch:{ EOFException -> 0x0599, FileNotFoundException -> 0x0583, NumberFormatException -> 0x056d, IOException -> 0x0557 }
            goto L_0x059a
        L_0x0546:
            r0 = move-exception
            r26 = r4
            r4 = r0
        L_0x054a:
            throw r4     // Catch:{ all -> 0x054b }
        L_0x054b:
            r0 = move-exception
            r5 = r0
            r26.close()     // Catch:{ all -> 0x0551 }
            goto L_0x0556
        L_0x0551:
            r0 = move-exception
            r6 = r0
            r4.addSuppressed(r6)     // Catch:{ EOFException -> 0x0599, FileNotFoundException -> 0x0583, NumberFormatException -> 0x056d, IOException -> 0x0557 }
        L_0x0556:
            throw r5     // Catch:{ EOFException -> 0x0599, FileNotFoundException -> 0x0583, NumberFormatException -> 0x056d, IOException -> 0x0557 }
        L_0x0557:
            r0 = move-exception
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            java.lang.String r5 = "readNetworkHistory: failed to read, "
            r4.append(r5)
            r4.append(r0)
            java.lang.String r4 = r4.toString()
            android.util.Log.e(r3, r4, r0)
            goto L_0x059b
        L_0x056d:
            r0 = move-exception
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            java.lang.String r5 = "readNetworkHistory: failed to parse, "
            r4.append(r5)
            r4.append(r0)
            java.lang.String r4 = r4.toString()
            android.util.Log.e(r3, r4, r0)
            goto L_0x059a
        L_0x0583:
            r0 = move-exception
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            java.lang.String r5 = "readNetworkHistory: no config file, "
            r4.append(r5)
            r4.append(r0)
            java.lang.String r4 = r4.toString()
            android.util.Log.i(r3, r4)
            goto L_0x059a
        L_0x0599:
            r0 = move-exception
        L_0x059a:
        L_0x059b:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.wifi.WifiNetworkHistory.readNetworkHistory(java.util.Map, java.util.Map, java.util.Set):void");
    }

    public boolean isValid(WifiConfiguration config) {
        if (config.allowedKeyManagement == null) {
            return false;
        }
        if (config.allowedKeyManagement.cardinality() > 1) {
            if (config.allowedKeyManagement.cardinality() != 2 || !config.allowedKeyManagement.get(2)) {
                return false;
            }
            if (config.allowedKeyManagement.get(3) || config.allowedKeyManagement.get(1)) {
                return true;
            }
            return false;
        }
        return true;
    }

    /* access modifiers changed from: private */
    public static String makeString(BitSet set, String[] strings) {
        StringBuffer buf = new StringBuffer();
        int nextSetBit = -1;
        BitSet set2 = set.get(0, strings.length);
        while (true) {
            int nextSetBit2 = set2.nextSetBit(nextSetBit + 1);
            nextSetBit = nextSetBit2;
            if (nextSetBit2 == -1) {
                break;
            }
            buf.append(strings[nextSetBit].replace('_', '-'));
            buf.append(' ');
        }
        if (set2.cardinality() > 0) {
            buf.setLength(buf.length() - 1);
        }
        return buf.toString();
    }

    /* access modifiers changed from: protected */
    public void logv(String s) {
        Log.v(TAG, s);
    }

    /* access modifiers changed from: protected */
    public void logd(String s) {
        Log.d(TAG, s);
    }

    /* access modifiers changed from: protected */
    public void log(String s) {
        Log.d(TAG, s);
    }

    /* access modifiers changed from: protected */
    public void loge(String s) {
        loge(s, false);
    }

    /* access modifiers changed from: protected */
    public void loge(String s, boolean stack) {
        if (stack) {
            Log.e(TAG, s + " stack:" + Thread.currentThread().getStackTrace()[2].getMethodName() + " - " + Thread.currentThread().getStackTrace()[3].getMethodName() + " - " + Thread.currentThread().getStackTrace()[4].getMethodName() + " - " + Thread.currentThread().getStackTrace()[5].getMethodName());
            return;
        }
        Log.e(TAG, s);
    }

    /* access modifiers changed from: private */
    public ScanDetailCache getScanDetailCache(WifiConfiguration config, Map<Integer, ScanDetailCache> scanDetailCaches) {
        if (config == null || scanDetailCaches == null) {
            return null;
        }
        ScanDetailCache cache = scanDetailCaches.get(Integer.valueOf(config.networkId));
        if (cache != null || config.networkId == -1) {
            return cache;
        }
        ScanDetailCache cache2 = new ScanDetailCache(config, WifiConfigManager.SCAN_CACHE_ENTRIES_MAX_SIZE, 128);
        scanDetailCaches.put(Integer.valueOf(config.networkId), cache2);
        return cache2;
    }
}
