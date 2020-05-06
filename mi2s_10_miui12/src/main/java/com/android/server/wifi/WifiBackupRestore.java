package com.android.server.wifi;

import android.net.IpConfiguration;
import android.net.wifi.WifiConfiguration;
import android.util.Log;
import android.util.SparseArray;
import com.android.internal.util.FastXmlSerializer;
import com.android.server.net.IpConfigStore;
import com.android.server.wifi.util.NativeUtil;
import com.android.server.wifi.util.WifiPermissionsUtil;
import com.android.server.wifi.util.XmlUtil;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.CharArrayReader;
import java.io.FileDescriptor;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import miui.telephony.phonenumber.Prefix;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

public class WifiBackupRestore {
    private static final float CURRENT_BACKUP_DATA_VERSION = 1.1f;
    private static final int INITIAL_BACKUP_DATA_VERSION = 1;
    private static final String PSK_MASK_LINE_MATCH_PATTERN = "<.*PreSharedKey.*>.*<.*>";
    private static final String PSK_MASK_REPLACE_PATTERN = "$1*$3";
    private static final String PSK_MASK_SEARCH_PATTERN = "(<.*PreSharedKey.*>)(.*)(<.*>)";
    private static final String TAG = "WifiBackupRestore";
    private static final String WEP_KEYS_MASK_LINE_END_MATCH_PATTERN = "</string-array>";
    private static final String WEP_KEYS_MASK_LINE_START_MATCH_PATTERN = "<string-array.*WEPKeys.*num=\"[0-9]\">";
    private static final String WEP_KEYS_MASK_REPLACE_PATTERN = "$1*$3";
    private static final String WEP_KEYS_MASK_SEARCH_PATTERN = "(<.*=)(.*)(/>)";
    private static final String XML_TAG_DOCUMENT_HEADER = "WifiBackupData";
    static final String XML_TAG_SECTION_HEADER_IP_CONFIGURATION = "IpConfiguration";
    static final String XML_TAG_SECTION_HEADER_NETWORK = "Network";
    static final String XML_TAG_SECTION_HEADER_NETWORK_LIST = "NetworkList";
    static final String XML_TAG_SECTION_HEADER_WIFI_CONFIGURATION = "WifiConfiguration";
    private static final String XML_TAG_VERSION = "Version";
    private byte[] mDebugLastBackupDataRestored;
    private byte[] mDebugLastBackupDataRetrieved;
    private byte[] mDebugLastSupplicantBackupDataRestored;
    private boolean mVerboseLoggingEnabled = false;
    private final WifiPermissionsUtil mWifiPermissionsUtil;

    public WifiBackupRestore(WifiPermissionsUtil wifiPermissionsUtil) {
        this.mWifiPermissionsUtil = wifiPermissionsUtil;
    }

    public byte[] retrieveBackupDataFromConfigurations(List<WifiConfiguration> configurations) {
        if (configurations == null) {
            Log.e(TAG, "Invalid configuration list received");
            return new byte[0];
        }
        try {
            XmlSerializer out = new FastXmlSerializer();
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            out.setOutput(outputStream, StandardCharsets.UTF_8.name());
            XmlUtil.writeDocumentStart(out, XML_TAG_DOCUMENT_HEADER);
            XmlUtil.writeNextValue(out, XML_TAG_VERSION, Float.valueOf(CURRENT_BACKUP_DATA_VERSION));
            writeNetworkConfigurationsToXml(out, configurations);
            XmlUtil.writeDocumentEnd(out, XML_TAG_DOCUMENT_HEADER);
            byte[] data = outputStream.toByteArray();
            if (this.mVerboseLoggingEnabled) {
                this.mDebugLastBackupDataRetrieved = data;
            }
            return data;
        } catch (XmlPullParserException e) {
            Log.e(TAG, "Error retrieving the backup data: " + e);
            return new byte[0];
        } catch (IOException e2) {
            Log.e(TAG, "Error retrieving the backup data: " + e2);
            return new byte[0];
        }
    }

    private void writeNetworkConfigurationsToXml(XmlSerializer out, List<WifiConfiguration> configurations) throws XmlPullParserException, IOException {
        XmlUtil.writeNextSectionStart(out, XML_TAG_SECTION_HEADER_NETWORK_LIST);
        for (WifiConfiguration configuration : configurations) {
            if (!configuration.isEnterprise() && !configuration.isPasspoint()) {
                if (!this.mWifiPermissionsUtil.checkConfigOverridePermission(configuration.creatorUid)) {
                    Log.d(TAG, "Ignoring network from an app with no config override permission: " + configuration.configKey());
                } else {
                    XmlUtil.writeNextSectionStart(out, XML_TAG_SECTION_HEADER_NETWORK);
                    writeNetworkConfigurationToXml(out, configuration);
                    XmlUtil.writeNextSectionEnd(out, XML_TAG_SECTION_HEADER_NETWORK);
                }
            }
        }
        XmlUtil.writeNextSectionEnd(out, XML_TAG_SECTION_HEADER_NETWORK_LIST);
    }

    private void writeNetworkConfigurationToXml(XmlSerializer out, WifiConfiguration configuration) throws XmlPullParserException, IOException {
        XmlUtil.writeNextSectionStart(out, XML_TAG_SECTION_HEADER_WIFI_CONFIGURATION);
        XmlUtil.WifiConfigurationXmlUtil.writeToXmlForBackup(out, configuration);
        XmlUtil.writeNextSectionEnd(out, XML_TAG_SECTION_HEADER_WIFI_CONFIGURATION);
        XmlUtil.writeNextSectionStart(out, XML_TAG_SECTION_HEADER_IP_CONFIGURATION);
        XmlUtil.IpConfigurationXmlUtil.writeToXml(out, configuration.getIpConfiguration());
        XmlUtil.writeNextSectionEnd(out, XML_TAG_SECTION_HEADER_IP_CONFIGURATION);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:17:0x0069, code lost:
        r5 = 1;
        r6 = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:25:0x0098, code lost:
        r2 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:26:0x0099, code lost:
        android.util.Log.e(TAG, "Error parsing the backup data: " + r2);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:27:0x00ad, code lost:
        return null;
     */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Removed duplicated region for block: B:25:0x0098 A[ExcHandler: IOException | ClassCastException | IllegalArgumentException | XmlPullParserException (r2v2 'e' java.lang.Exception A[CUSTOM_DECLARE]), Splitter:B:4:0x000a] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public java.util.List<android.net.wifi.WifiConfiguration> retrieveConfigurationsFromBackupData(byte[] r12) {
        /*
            r11 = this;
            r0 = 0
            java.lang.String r1 = "WifiBackupRestore"
            if (r12 == 0) goto L_0x00ae
            int r2 = r12.length
            if (r2 != 0) goto L_0x000a
            goto L_0x00ae
        L_0x000a:
            boolean r2 = r11.mVerboseLoggingEnabled     // Catch:{ IOException | ClassCastException | IllegalArgumentException | XmlPullParserException -> 0x0098 }
            if (r2 == 0) goto L_0x0010
            r11.mDebugLastBackupDataRestored = r12     // Catch:{ IOException | ClassCastException | IllegalArgumentException | XmlPullParserException -> 0x0098 }
        L_0x0010:
            org.xmlpull.v1.XmlPullParser r2 = android.util.Xml.newPullParser()     // Catch:{ IOException | ClassCastException | IllegalArgumentException | XmlPullParserException -> 0x0098 }
            java.io.ByteArrayInputStream r3 = new java.io.ByteArrayInputStream     // Catch:{ IOException | ClassCastException | IllegalArgumentException | XmlPullParserException -> 0x0098 }
            r3.<init>(r12)     // Catch:{ IOException | ClassCastException | IllegalArgumentException | XmlPullParserException -> 0x0098 }
            java.nio.charset.Charset r4 = java.nio.charset.StandardCharsets.UTF_8     // Catch:{ IOException | ClassCastException | IllegalArgumentException | XmlPullParserException -> 0x0098 }
            java.lang.String r4 = r4.name()     // Catch:{ IOException | ClassCastException | IllegalArgumentException | XmlPullParserException -> 0x0098 }
            r2.setInput(r3, r4)     // Catch:{ IOException | ClassCastException | IllegalArgumentException | XmlPullParserException -> 0x0098 }
            java.lang.String r4 = "WifiBackupData"
            com.android.server.wifi.util.XmlUtil.gotoDocumentStart(r2, r4)     // Catch:{ IOException | ClassCastException | IllegalArgumentException | XmlPullParserException -> 0x0098 }
            int r4 = r2.getDepth()     // Catch:{ IOException | ClassCastException | IllegalArgumentException | XmlPullParserException -> 0x0098 }
            r5 = -1
            r6 = -1
            java.lang.String r7 = "Version"
            java.lang.Object r7 = com.android.server.wifi.util.XmlUtil.readNextValueWithName(r2, r7)     // Catch:{ ClassCastException -> 0x0068, IOException | ClassCastException | IllegalArgumentException | XmlPullParserException -> 0x0098, IOException | ClassCastException | IllegalArgumentException | XmlPullParserException -> 0x0098 }
            java.lang.Float r7 = (java.lang.Float) r7     // Catch:{ ClassCastException -> 0x0068, IOException | ClassCastException | IllegalArgumentException | XmlPullParserException -> 0x0098, IOException | ClassCastException | IllegalArgumentException | XmlPullParserException -> 0x0098 }
            float r7 = r7.floatValue()     // Catch:{ ClassCastException -> 0x0068, IOException | ClassCastException | IllegalArgumentException | XmlPullParserException -> 0x0098, IOException | ClassCastException | IllegalArgumentException | XmlPullParserException -> 0x0098 }
            java.lang.Float r8 = new java.lang.Float     // Catch:{ ClassCastException -> 0x0068, IOException | ClassCastException | IllegalArgumentException | XmlPullParserException -> 0x0098, IOException | ClassCastException | IllegalArgumentException | XmlPullParserException -> 0x0098 }
            r8.<init>(r7)     // Catch:{ ClassCastException -> 0x0068, IOException | ClassCastException | IllegalArgumentException | XmlPullParserException -> 0x0098, IOException | ClassCastException | IllegalArgumentException | XmlPullParserException -> 0x0098 }
            java.lang.String r8 = r8.toString()     // Catch:{ ClassCastException -> 0x0068, IOException | ClassCastException | IllegalArgumentException | XmlPullParserException -> 0x0098, IOException | ClassCastException | IllegalArgumentException | XmlPullParserException -> 0x0098 }
            r9 = 46
            int r9 = r8.indexOf(r9)     // Catch:{ ClassCastException -> 0x0068, IOException | ClassCastException | IllegalArgumentException | XmlPullParserException -> 0x0098, IOException | ClassCastException | IllegalArgumentException | XmlPullParserException -> 0x0098 }
            r10 = -1
            if (r9 != r10) goto L_0x0052
            int r10 = java.lang.Integer.parseInt(r8)     // Catch:{ ClassCastException -> 0x0068, IOException | ClassCastException | IllegalArgumentException | XmlPullParserException -> 0x0098, IOException | ClassCastException | IllegalArgumentException | XmlPullParserException -> 0x0098 }
            r5 = r10
            r6 = 0
            goto L_0x0067
        L_0x0052:
            r10 = 0
            java.lang.String r10 = r8.substring(r10, r9)     // Catch:{ ClassCastException -> 0x0068, IOException | ClassCastException | IllegalArgumentException | XmlPullParserException -> 0x0098, IOException | ClassCastException | IllegalArgumentException | XmlPullParserException -> 0x0098 }
            int r10 = java.lang.Integer.parseInt(r10)     // Catch:{ ClassCastException -> 0x0068, IOException | ClassCastException | IllegalArgumentException | XmlPullParserException -> 0x0098, IOException | ClassCastException | IllegalArgumentException | XmlPullParserException -> 0x0098 }
            r5 = r10
            int r10 = r9 + 1
            java.lang.String r10 = r8.substring(r10)     // Catch:{ ClassCastException -> 0x0068, IOException | ClassCastException | IllegalArgumentException | XmlPullParserException -> 0x0098, IOException | ClassCastException | IllegalArgumentException | XmlPullParserException -> 0x0098 }
            int r10 = java.lang.Integer.parseInt(r10)     // Catch:{ ClassCastException -> 0x0068, IOException | ClassCastException | IllegalArgumentException | XmlPullParserException -> 0x0098, IOException | ClassCastException | IllegalArgumentException | XmlPullParserException -> 0x0098 }
            r6 = r10
        L_0x0067:
            goto L_0x006b
        L_0x0068:
            r7 = move-exception
            r5 = 1
            r6 = 0
        L_0x006b:
            java.lang.StringBuilder r7 = new java.lang.StringBuilder     // Catch:{ IOException | ClassCastException | IllegalArgumentException | XmlPullParserException -> 0x0098 }
            r7.<init>()     // Catch:{ IOException | ClassCastException | IllegalArgumentException | XmlPullParserException -> 0x0098 }
            java.lang.String r8 = "Version of backup data - major: "
            r7.append(r8)     // Catch:{ IOException | ClassCastException | IllegalArgumentException | XmlPullParserException -> 0x0098 }
            r7.append(r5)     // Catch:{ IOException | ClassCastException | IllegalArgumentException | XmlPullParserException -> 0x0098 }
            java.lang.String r8 = "; minor: "
            r7.append(r8)     // Catch:{ IOException | ClassCastException | IllegalArgumentException | XmlPullParserException -> 0x0098 }
            r7.append(r6)     // Catch:{ IOException | ClassCastException | IllegalArgumentException | XmlPullParserException -> 0x0098 }
            java.lang.String r7 = r7.toString()     // Catch:{ IOException | ClassCastException | IllegalArgumentException | XmlPullParserException -> 0x0098 }
            android.util.Log.d(r1, r7)     // Catch:{ IOException | ClassCastException | IllegalArgumentException | XmlPullParserException -> 0x0098 }
            com.android.server.wifi.WifiBackupDataParser r7 = r11.getWifiBackupDataParser(r5)     // Catch:{ IOException | ClassCastException | IllegalArgumentException | XmlPullParserException -> 0x0098 }
            if (r7 != 0) goto L_0x0093
            java.lang.String r8 = "Major version of backup data is unknown to this Android version; not restoring"
            android.util.Log.w(r1, r8)     // Catch:{ IOException | ClassCastException | IllegalArgumentException | XmlPullParserException -> 0x0098 }
            return r0
        L_0x0093:
            java.util.List r0 = r7.parseNetworkConfigurationsFromXml(r2, r4, r6)     // Catch:{ IOException | ClassCastException | IllegalArgumentException | XmlPullParserException -> 0x0098 }
            return r0
        L_0x0098:
            r2 = move-exception
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            java.lang.String r4 = "Error parsing the backup data: "
            r3.append(r4)
            r3.append(r2)
            java.lang.String r3 = r3.toString()
            android.util.Log.e(r1, r3)
            return r0
        L_0x00ae:
            java.lang.String r2 = "Invalid backup data received"
            android.util.Log.e(r1, r2)
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.wifi.WifiBackupRestore.retrieveConfigurationsFromBackupData(byte[]):java.util.List");
    }

    private WifiBackupDataParser getWifiBackupDataParser(int majorVersion) {
        if (majorVersion == 1) {
            return new WifiBackupDataV1Parser();
        }
        Log.e(TAG, "Unrecognized majorVersion of backup data: " + majorVersion);
        return null;
    }

    private String createLogFromBackupData(byte[] data) {
        StringBuilder sb = new StringBuilder();
        try {
            boolean wepKeysLine = false;
            String[] split = new String(data, StandardCharsets.UTF_8.name()).split("\n");
            int length = split.length;
            for (int i = 0; i < length; i++) {
                String line = split[i];
                if (line.matches(PSK_MASK_LINE_MATCH_PATTERN)) {
                    line = line.replaceAll(PSK_MASK_SEARCH_PATTERN, "$1*$3");
                }
                if (line.matches(WEP_KEYS_MASK_LINE_START_MATCH_PATTERN)) {
                    wepKeysLine = true;
                } else if (line.matches(WEP_KEYS_MASK_LINE_END_MATCH_PATTERN)) {
                    wepKeysLine = false;
                } else if (wepKeysLine) {
                    line = line.replaceAll(WEP_KEYS_MASK_SEARCH_PATTERN, "$1*$3");
                }
                sb.append(line);
                sb.append("\n");
            }
            return sb.toString();
        } catch (UnsupportedEncodingException e) {
            return Prefix.EMPTY;
        }
    }

    public List<WifiConfiguration> retrieveConfigurationsFromSupplicantBackupData(byte[] supplicantData, byte[] ipConfigData) {
        if (supplicantData == null || supplicantData.length == 0) {
            Log.e(TAG, "Invalid supplicant backup data received");
            return null;
        }
        if (this.mVerboseLoggingEnabled) {
            this.mDebugLastSupplicantBackupDataRestored = supplicantData;
        }
        SupplicantBackupMigration.SupplicantNetworks supplicantNetworks = new SupplicantBackupMigration.SupplicantNetworks();
        char[] restoredAsChars = new char[supplicantData.length];
        for (int i = 0; i < supplicantData.length; i++) {
            restoredAsChars[i] = (char) supplicantData[i];
        }
        supplicantNetworks.readNetworksFromStream(new BufferedReader(new CharArrayReader(restoredAsChars)));
        List<WifiConfiguration> configurations = supplicantNetworks.retrieveWifiConfigurations();
        if (ipConfigData == null || ipConfigData.length == 0) {
            Log.e(TAG, "Invalid ipconfig backup data received");
        } else {
            SparseArray<IpConfiguration> networks = IpConfigStore.readIpAndProxyConfigurations(new ByteArrayInputStream(ipConfigData));
            if (networks != null) {
                for (int i2 = 0; i2 < networks.size(); i2++) {
                    int id = networks.keyAt(i2);
                    for (WifiConfiguration configuration : configurations) {
                        if (configuration.configKey().hashCode() == id) {
                            configuration.setIpConfiguration(networks.valueAt(i2));
                        }
                    }
                }
            } else {
                Log.e(TAG, "Failed to parse ipconfig data");
            }
        }
        return configurations;
    }

    public void enableVerboseLogging(int verbose) {
        this.mVerboseLoggingEnabled = verbose > 0;
        if (!this.mVerboseLoggingEnabled) {
            this.mDebugLastBackupDataRetrieved = null;
            this.mDebugLastBackupDataRestored = null;
            this.mDebugLastSupplicantBackupDataRestored = null;
        }
    }

    public void dump(FileDescriptor fd, PrintWriter pw, String[] args) {
        pw.println("Dump of WifiBackupRestore");
        if (this.mDebugLastBackupDataRetrieved != null) {
            pw.println("Last backup data retrieved: " + createLogFromBackupData(this.mDebugLastBackupDataRetrieved));
        }
        if (this.mDebugLastBackupDataRestored != null) {
            pw.println("Last backup data restored: " + createLogFromBackupData(this.mDebugLastBackupDataRestored));
        }
        if (this.mDebugLastSupplicantBackupDataRestored != null) {
            pw.println("Last old backup data restored: " + SupplicantBackupMigration.createLogFromBackupData(this.mDebugLastSupplicantBackupDataRestored));
        }
    }

    public static class SupplicantBackupMigration {
        private static final String PSK_MASK_LINE_MATCH_PATTERN = ".*psk.*=.*";
        private static final String PSK_MASK_REPLACE_PATTERN = "$1*";
        private static final String PSK_MASK_SEARCH_PATTERN = "(.*psk.*=)(.*)";
        public static final String SUPPLICANT_KEY_CA_CERT = "ca_cert";
        public static final String SUPPLICANT_KEY_CA_PATH = "ca_path";
        public static final String SUPPLICANT_KEY_CLIENT_CERT = "client_cert";
        public static final String SUPPLICANT_KEY_EAP = "eap";
        public static final String SUPPLICANT_KEY_HIDDEN = "scan_ssid";
        public static final String SUPPLICANT_KEY_ID_STR = "id_str";
        public static final String SUPPLICANT_KEY_KEY_MGMT = "key_mgmt";
        public static final String SUPPLICANT_KEY_PSK = "psk";
        public static final String SUPPLICANT_KEY_SSID = "ssid";
        public static final String SUPPLICANT_KEY_WEP_KEY0 = WifiConfiguration.wepKeyVarNames[0];
        public static final String SUPPLICANT_KEY_WEP_KEY1 = WifiConfiguration.wepKeyVarNames[1];
        public static final String SUPPLICANT_KEY_WEP_KEY2 = WifiConfiguration.wepKeyVarNames[2];
        public static final String SUPPLICANT_KEY_WEP_KEY3 = WifiConfiguration.wepKeyVarNames[3];
        public static final String SUPPLICANT_KEY_WEP_KEY_IDX = "wep_tx_keyidx";
        private static final String WEP_KEYS_MASK_LINE_MATCH_PATTERN = (".*" + SUPPLICANT_KEY_WEP_KEY0.replace("0", Prefix.EMPTY) + ".*=.*");
        private static final String WEP_KEYS_MASK_REPLACE_PATTERN = "$1*";
        private static final String WEP_KEYS_MASK_SEARCH_PATTERN = ("(.*" + SUPPLICANT_KEY_WEP_KEY0.replace("0", Prefix.EMPTY) + ".*=)(.*)");

        public static String createLogFromBackupData(byte[] data) {
            StringBuilder sb = new StringBuilder();
            try {
                String[] split = new String(data, StandardCharsets.UTF_8.name()).split("\n");
                int length = split.length;
                for (int i = 0; i < length; i++) {
                    String line = split[i];
                    if (line.matches(PSK_MASK_LINE_MATCH_PATTERN)) {
                        line = line.replaceAll(PSK_MASK_SEARCH_PATTERN, "$1*");
                    }
                    if (line.matches(WEP_KEYS_MASK_LINE_MATCH_PATTERN)) {
                        line = line.replaceAll(WEP_KEYS_MASK_SEARCH_PATTERN, "$1*");
                    }
                    sb.append(line);
                    sb.append("\n");
                }
                return sb.toString();
            } catch (UnsupportedEncodingException e) {
                return Prefix.EMPTY;
            }
        }

        static class SupplicantNetwork {
            public boolean certUsed = false;
            public boolean isEap = false;
            private String mParsedHiddenLine;
            private String mParsedIdStrLine;
            /* access modifiers changed from: private */
            public String mParsedKeyMgmtLine;
            private String mParsedPskLine;
            /* access modifiers changed from: private */
            public String mParsedSSIDLine;
            private String[] mParsedWepKeyLines = new String[4];
            private String mParsedWepTxKeyIdxLine;

            SupplicantNetwork() {
            }

            public static SupplicantNetwork readNetworkFromStream(BufferedReader in) {
                String line;
                SupplicantNetwork n = new SupplicantNetwork();
                while (true) {
                    try {
                        if (!in.ready() || (line = in.readLine()) == null) {
                            break;
                        } else if (line.startsWith("}")) {
                            break;
                        } else {
                            n.parseLine(line);
                        }
                    } catch (IOException e) {
                        return null;
                    }
                }
                return n;
            }

            /* access modifiers changed from: package-private */
            public void parseLine(String line) {
                String line2 = line.trim();
                if (!line2.isEmpty()) {
                    if (line2.startsWith("ssid=")) {
                        this.mParsedSSIDLine = line2;
                    } else if (line2.startsWith("scan_ssid=")) {
                        this.mParsedHiddenLine = line2;
                    } else if (line2.startsWith("key_mgmt=")) {
                        this.mParsedKeyMgmtLine = line2;
                        if (line2.contains("EAP")) {
                            this.isEap = true;
                        }
                    } else if (line2.startsWith("client_cert=")) {
                        this.certUsed = true;
                    } else if (line2.startsWith("ca_cert=")) {
                        this.certUsed = true;
                    } else if (line2.startsWith("ca_path=")) {
                        this.certUsed = true;
                    } else if (line2.startsWith("eap=")) {
                        this.isEap = true;
                    } else if (line2.startsWith("psk=")) {
                        this.mParsedPskLine = line2;
                    } else {
                        if (line2.startsWith(SupplicantBackupMigration.SUPPLICANT_KEY_WEP_KEY0 + "=")) {
                            this.mParsedWepKeyLines[0] = line2;
                            return;
                        }
                        if (line2.startsWith(SupplicantBackupMigration.SUPPLICANT_KEY_WEP_KEY1 + "=")) {
                            this.mParsedWepKeyLines[1] = line2;
                            return;
                        }
                        if (line2.startsWith(SupplicantBackupMigration.SUPPLICANT_KEY_WEP_KEY2 + "=")) {
                            this.mParsedWepKeyLines[2] = line2;
                            return;
                        }
                        if (line2.startsWith(SupplicantBackupMigration.SUPPLICANT_KEY_WEP_KEY3 + "=")) {
                            this.mParsedWepKeyLines[3] = line2;
                        } else if (line2.startsWith("wep_tx_keyidx=")) {
                            this.mParsedWepTxKeyIdxLine = line2;
                        } else if (line2.startsWith("id_str=")) {
                            this.mParsedIdStrLine = line2;
                        }
                    }
                }
            }

            public WifiConfiguration createWifiConfiguration() {
                String idString;
                if (this.mParsedSSIDLine == null) {
                    return null;
                }
                WifiConfiguration configuration = new WifiConfiguration();
                String str = this.mParsedSSIDLine;
                configuration.SSID = str.substring(str.indexOf(61) + 1);
                String str2 = this.mParsedHiddenLine;
                if (str2 != null) {
                    configuration.hiddenSSID = Integer.parseInt(str2.substring(str2.indexOf(61) + 1)) != 0;
                }
                String str3 = this.mParsedKeyMgmtLine;
                if (str3 == null) {
                    configuration.allowedKeyManagement.set(1);
                    configuration.allowedKeyManagement.set(2);
                } else {
                    String[] typeStrings = str3.substring(str3.indexOf(61) + 1).split("\\s+");
                    for (String ktype : typeStrings) {
                        if (ktype.equals("NONE")) {
                            configuration.allowedKeyManagement.set(0);
                        } else if (ktype.equals("WPA-PSK")) {
                            configuration.allowedKeyManagement.set(1);
                        } else if (ktype.equals("WPA-EAP")) {
                            configuration.allowedKeyManagement.set(2);
                        } else if (ktype.equals("IEEE8021X")) {
                            configuration.allowedKeyManagement.set(3);
                        }
                    }
                }
                String bareKeyMgmt = this.mParsedPskLine;
                if (bareKeyMgmt != null) {
                    configuration.preSharedKey = bareKeyMgmt.substring(bareKeyMgmt.indexOf(61) + 1);
                }
                if (this.mParsedWepKeyLines[0] != null) {
                    String[] strArr = configuration.wepKeys;
                    String[] strArr2 = this.mParsedWepKeyLines;
                    strArr[0] = strArr2[0].substring(strArr2[0].indexOf(61) + 1);
                }
                if (this.mParsedWepKeyLines[1] != null) {
                    String[] strArr3 = configuration.wepKeys;
                    String[] strArr4 = this.mParsedWepKeyLines;
                    strArr3[1] = strArr4[1].substring(strArr4[1].indexOf(61) + 1);
                }
                if (this.mParsedWepKeyLines[2] != null) {
                    String[] strArr5 = configuration.wepKeys;
                    String[] strArr6 = this.mParsedWepKeyLines;
                    strArr5[2] = strArr6[2].substring(strArr6[2].indexOf(61) + 1);
                }
                if (this.mParsedWepKeyLines[3] != null) {
                    String[] strArr7 = configuration.wepKeys;
                    String[] strArr8 = this.mParsedWepKeyLines;
                    strArr7[3] = strArr8[3].substring(strArr8[3].indexOf(61) + 1);
                }
                String str4 = this.mParsedWepTxKeyIdxLine;
                if (str4 != null) {
                    configuration.wepTxKeyIndex = Integer.valueOf(str4.substring(str4.indexOf(61) + 1)).intValue();
                }
                String str5 = this.mParsedIdStrLine;
                if (!(str5 == null || (idString = str5.substring(str5.indexOf(61) + 1)) == null)) {
                    Map<String, String> extras = SupplicantStaNetworkHal.parseNetworkExtra(NativeUtil.removeEnclosingQuotes(idString));
                    if (extras == null) {
                        Log.e(WifiBackupRestore.TAG, "Error parsing network extras, ignoring network.");
                        return null;
                    }
                    String configKey = extras.get(SupplicantStaNetworkHal.ID_STRING_KEY_CONFIG_KEY);
                    if (configKey == null) {
                        Log.e(WifiBackupRestore.TAG, "Configuration key was not passed, ignoring network.");
                        return null;
                    }
                    if (!configKey.equals(configuration.configKey())) {
                        Log.w(WifiBackupRestore.TAG, "Configuration key does not match. Retrieved: " + configKey + ", Calculated: " + configuration.configKey());
                    }
                    if (Integer.parseInt(extras.get(SupplicantStaNetworkHal.ID_STRING_KEY_CREATOR_UID)) >= 10000) {
                        Log.d(WifiBackupRestore.TAG, "Ignoring network from non-system app: " + configuration.configKey());
                        return null;
                    }
                }
                return configuration;
            }
        }

        static class SupplicantNetworks {
            final ArrayList<SupplicantNetwork> mNetworks = new ArrayList<>(8);

            SupplicantNetworks() {
            }

            public void readNetworksFromStream(BufferedReader in) {
                while (in.ready()) {
                    try {
                        String line = in.readLine();
                        if (line != null && line.startsWith("network")) {
                            SupplicantNetwork net = SupplicantNetwork.readNetworkFromStream(in);
                            if (net == null) {
                                Log.e(WifiBackupRestore.TAG, "Error while parsing the network.");
                            } else {
                                if (!net.isEap) {
                                    if (!net.certUsed) {
                                        this.mNetworks.add(net);
                                    }
                                }
                                Log.d(WifiBackupRestore.TAG, "Skipping enterprise network for restore: " + net.mParsedSSIDLine + " / " + net.mParsedKeyMgmtLine);
                            }
                        }
                    } catch (IOException e) {
                        return;
                    }
                }
            }

            public List<WifiConfiguration> retrieveWifiConfigurations() {
                ArrayList<WifiConfiguration> wifiConfigurations = new ArrayList<>();
                Iterator<SupplicantNetwork> it = this.mNetworks.iterator();
                while (it.hasNext()) {
                    try {
                        WifiConfiguration wifiConfiguration = it.next().createWifiConfiguration();
                        if (wifiConfiguration != null) {
                            Log.v(WifiBackupRestore.TAG, "Parsed Configuration: " + wifiConfiguration.configKey());
                            wifiConfigurations.add(wifiConfiguration);
                        }
                    } catch (NumberFormatException e) {
                        Log.e(WifiBackupRestore.TAG, "Error parsing wifi configuration: " + e);
                        return null;
                    }
                }
                return wifiConfigurations;
            }
        }
    }
}
