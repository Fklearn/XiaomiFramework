package com.android.server.wifi;

import android.net.IpConfiguration;
import android.net.wifi.WifiConfiguration;
import android.util.Log;
import android.util.Pair;
import com.android.server.wifi.util.XmlUtil;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

class WifiBackupDataV1Parser implements WifiBackupDataParser {
    private static final int HIGHEST_SUPPORTED_MINOR_VERSION = 1;
    private static final Set<String> IP_CONFIGURATION_MINOR_V0_V1_SUPPORTED_TAGS = new HashSet(Arrays.asList(new String[]{XmlUtil.IpConfigurationXmlUtil.XML_TAG_IP_ASSIGNMENT, XmlUtil.IpConfigurationXmlUtil.XML_TAG_LINK_ADDRESS, XmlUtil.IpConfigurationXmlUtil.XML_TAG_LINK_PREFIX_LENGTH, XmlUtil.IpConfigurationXmlUtil.XML_TAG_GATEWAY_ADDRESS, XmlUtil.IpConfigurationXmlUtil.XML_TAG_DNS_SERVER_ADDRESSES, XmlUtil.IpConfigurationXmlUtil.XML_TAG_PROXY_SETTINGS, XmlUtil.IpConfigurationXmlUtil.XML_TAG_PROXY_HOST, XmlUtil.IpConfigurationXmlUtil.XML_TAG_PROXY_PORT, XmlUtil.IpConfigurationXmlUtil.XML_TAG_PROXY_EXCLUSION_LIST, XmlUtil.IpConfigurationXmlUtil.XML_TAG_PROXY_PAC_FILE}));
    private static final String TAG = "WifiBackupDataV1Parser";
    /* access modifiers changed from: private */
    public static final Set<String> WIFI_CONFIGURATION_MINOR_V0_SUPPORTED_TAGS = new HashSet(Arrays.asList(new String[]{XmlUtil.WifiConfigurationXmlUtil.XML_TAG_CONFIG_KEY, XmlUtil.WifiConfigurationXmlUtil.XML_TAG_SSID, XmlUtil.WifiConfigurationXmlUtil.XML_TAG_BSSID, XmlUtil.WifiConfigurationXmlUtil.XML_TAG_PRE_SHARED_KEY, XmlUtil.WifiConfigurationXmlUtil.XML_TAG_WEP_KEYS, XmlUtil.WifiConfigurationXmlUtil.XML_TAG_WEP_TX_KEY_INDEX, XmlUtil.WifiConfigurationXmlUtil.XML_TAG_HIDDEN_SSID, XmlUtil.WifiConfigurationXmlUtil.XML_TAG_REQUIRE_PMF, XmlUtil.WifiConfigurationXmlUtil.XML_TAG_ALLOWED_KEY_MGMT, XmlUtil.WifiConfigurationXmlUtil.XML_TAG_ALLOWED_PROTOCOLS, XmlUtil.WifiConfigurationXmlUtil.XML_TAG_ALLOWED_AUTH_ALGOS, XmlUtil.WifiConfigurationXmlUtil.XML_TAG_ALLOWED_GROUP_CIPHERS, XmlUtil.WifiConfigurationXmlUtil.XML_TAG_ALLOWED_PAIRWISE_CIPHERS, XmlUtil.WifiConfigurationXmlUtil.XML_TAG_SHARED}));
    private static final Set<String> WIFI_CONFIGURATION_MINOR_V1_SUPPORTED_TAGS = new HashSet<String>() {
        {
            addAll(WifiBackupDataV1Parser.WIFI_CONFIGURATION_MINOR_V0_SUPPORTED_TAGS);
            add(XmlUtil.WifiConfigurationXmlUtil.XML_TAG_METERED_OVERRIDE);
        }
    };

    WifiBackupDataV1Parser() {
    }

    public List<WifiConfiguration> parseNetworkConfigurationsFromXml(XmlPullParser in, int outerTagDepth, int minorVersion) throws XmlPullParserException, IOException {
        if (minorVersion > 1) {
            minorVersion = 1;
        }
        XmlUtil.gotoNextSectionWithName(in, "NetworkList", outerTagDepth);
        int networkListTagDepth = outerTagDepth + 1;
        List<WifiConfiguration> configurations = new ArrayList<>();
        while (XmlUtil.gotoNextSectionWithNameOrEnd(in, "Network", networkListTagDepth)) {
            WifiConfiguration configuration = parseNetworkConfigurationFromXml(in, minorVersion, networkListTagDepth);
            if (configuration != null) {
                Log.v(TAG, "Parsed Configuration: " + configuration.configKey());
                configurations.add(configuration);
            }
        }
        return configurations;
    }

    private WifiConfiguration parseNetworkConfigurationFromXml(XmlPullParser in, int minorVersion, int outerTagDepth) throws XmlPullParserException, IOException {
        int networkTagDepth = outerTagDepth + 1;
        XmlUtil.gotoNextSectionWithName(in, "WifiConfiguration", networkTagDepth);
        int configTagDepth = networkTagDepth + 1;
        WifiConfiguration configuration = parseWifiConfigurationFromXml(in, configTagDepth, minorVersion);
        if (configuration == null) {
            return null;
        }
        XmlUtil.gotoNextSectionWithName(in, "IpConfiguration", networkTagDepth);
        configuration.setIpConfiguration(parseIpConfigurationFromXml(in, configTagDepth, minorVersion));
        return configuration;
    }

    private WifiConfiguration parseWifiConfigurationFromXml(XmlPullParser in, int outerTagDepth, int minorVersion) throws XmlPullParserException, IOException {
        Pair<String, WifiConfiguration> parsedConfig = parseWifiConfigurationFromXmlInternal(in, outerTagDepth, minorVersion);
        if (parsedConfig == null || parsedConfig.first == null || parsedConfig.second == null) {
            return null;
        }
        String configKeyParsed = (String) parsedConfig.first;
        WifiConfiguration configuration = (WifiConfiguration) parsedConfig.second;
        String configKeyCalculated = configuration.configKey();
        if (!configKeyParsed.equals(configKeyCalculated)) {
            Log.w(TAG, "Configuration key does not match. Retrieved: " + configKeyParsed + ", Calculated: " + configKeyCalculated);
        }
        return configuration;
    }

    private static void clearAnyKnownIssuesInParsedConfiguration(WifiConfiguration config) {
        if (config.allowedKeyManagement.length() > WifiConfiguration.KeyMgmt.strings.length) {
            config.allowedKeyManagement.clear(WifiConfiguration.KeyMgmt.strings.length, config.allowedKeyManagement.length());
        }
        if (config.allowedProtocols.length() > WifiConfiguration.Protocol.strings.length) {
            config.allowedProtocols.clear(WifiConfiguration.Protocol.strings.length, config.allowedProtocols.length());
        }
        if (config.allowedAuthAlgorithms.length() > WifiConfiguration.AuthAlgorithm.strings.length) {
            config.allowedAuthAlgorithms.clear(WifiConfiguration.AuthAlgorithm.strings.length, config.allowedAuthAlgorithms.length());
        }
        if (config.allowedGroupCiphers.length() > WifiConfiguration.GroupCipher.strings.length) {
            config.allowedGroupCiphers.clear(WifiConfiguration.GroupCipher.strings.length, config.allowedGroupCiphers.length());
        }
        if (config.allowedPairwiseCiphers.length() > WifiConfiguration.PairwiseCipher.strings.length) {
            config.allowedPairwiseCiphers.clear(WifiConfiguration.PairwiseCipher.strings.length, config.allowedPairwiseCiphers.length());
        }
    }

    /* JADX WARNING: Can't fix incorrect switch cases order */
    /* JADX WARNING: Code restructure failed: missing block: B:41:0x00bb, code lost:
        if (r7.equals(com.android.server.wifi.util.XmlUtil.WifiConfigurationXmlUtil.XML_TAG_SSID) != false) goto L_0x00eb;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static android.util.Pair<java.lang.String, android.net.wifi.WifiConfiguration> parseWifiConfigurationFromXmlInternal(org.xmlpull.v1.XmlPullParser r10, int r11, int r12) throws org.xmlpull.v1.XmlPullParserException, java.io.IOException {
        /*
            android.net.wifi.WifiConfiguration r0 = new android.net.wifi.WifiConfiguration
            r0.<init>()
            r1 = 0
            java.util.Set r2 = getSupportedWifiConfigurationTags(r12)
        L_0x000a:
            boolean r3 = com.android.server.wifi.util.XmlUtil.isNextSectionEnd(r10, r11)
            if (r3 != 0) goto L_0x0194
            r3 = 1
            java.lang.String[] r4 = new java.lang.String[r3]
            java.lang.Object r5 = com.android.server.wifi.util.XmlUtil.readCurrentValue(r10, r4)
            r6 = 0
            r7 = r4[r6]
            if (r7 == 0) goto L_0x018c
            boolean r8 = r2.contains(r7)
            if (r8 != 0) goto L_0x003e
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            java.lang.String r6 = "Unsupported tag + \""
            r3.append(r6)
            r3.append(r7)
            java.lang.String r6 = "\" found in <WifiConfiguration> section, ignoring."
            r3.append(r6)
            java.lang.String r3 = r3.toString()
            java.lang.String r6 = "WifiBackupDataV1Parser"
            android.util.Log.w(r6, r3)
            goto L_0x000a
        L_0x003e:
            r8 = -1
            int r9 = r7.hashCode()
            switch(r9) {
                case -1819699067: goto L_0x00df;
                case -1704616680: goto L_0x00d4;
                case -181205965: goto L_0x00c9;
                case -51197516: goto L_0x00be;
                case 2554747: goto L_0x00b5;
                case 63507133: goto L_0x00ab;
                case 682791106: goto L_0x00a0;
                case 736944625: goto L_0x0095;
                case 797043831: goto L_0x008b;
                case 1199498141: goto L_0x0080;
                case 1851050768: goto L_0x0074;
                case 1905126713: goto L_0x0069;
                case 1955037270: goto L_0x005e;
                case 1965854789: goto L_0x0053;
                case 2143705732: goto L_0x0048;
                default: goto L_0x0046;
            }
        L_0x0046:
            goto L_0x00ea
        L_0x0048:
            java.lang.String r3 = "RequirePMF"
            boolean r3 = r7.equals(r3)
            if (r3 == 0) goto L_0x0046
            r3 = 7
            goto L_0x00eb
        L_0x0053:
            java.lang.String r3 = "HiddenSSID"
            boolean r3 = r7.equals(r3)
            if (r3 == 0) goto L_0x0046
            r3 = 6
            goto L_0x00eb
        L_0x005e:
            java.lang.String r3 = "WEPKeys"
            boolean r3 = r7.equals(r3)
            if (r3 == 0) goto L_0x0046
            r3 = 4
            goto L_0x00eb
        L_0x0069:
            java.lang.String r3 = "WEPTxKeyIndex"
            boolean r3 = r7.equals(r3)
            if (r3 == 0) goto L_0x0046
            r3 = 5
            goto L_0x00eb
        L_0x0074:
            java.lang.String r3 = "AllowedAuthAlgos"
            boolean r3 = r7.equals(r3)
            if (r3 == 0) goto L_0x0046
            r3 = 10
            goto L_0x00eb
        L_0x0080:
            java.lang.String r3 = "ConfigKey"
            boolean r3 = r7.equals(r3)
            if (r3 == 0) goto L_0x0046
            r3 = r6
            goto L_0x00eb
        L_0x008b:
            java.lang.String r3 = "PreSharedKey"
            boolean r3 = r7.equals(r3)
            if (r3 == 0) goto L_0x0046
            r3 = 3
            goto L_0x00eb
        L_0x0095:
            java.lang.String r3 = "AllowedGroupCiphers"
            boolean r3 = r7.equals(r3)
            if (r3 == 0) goto L_0x0046
            r3 = 11
            goto L_0x00eb
        L_0x00a0:
            java.lang.String r3 = "AllowedPairwiseCiphers"
            boolean r3 = r7.equals(r3)
            if (r3 == 0) goto L_0x0046
            r3 = 12
            goto L_0x00eb
        L_0x00ab:
            java.lang.String r3 = "BSSID"
            boolean r3 = r7.equals(r3)
            if (r3 == 0) goto L_0x0046
            r3 = 2
            goto L_0x00eb
        L_0x00b5:
            java.lang.String r9 = "SSID"
            boolean r9 = r7.equals(r9)
            if (r9 == 0) goto L_0x0046
            goto L_0x00eb
        L_0x00be:
            java.lang.String r3 = "MeteredOverride"
            boolean r3 = r7.equals(r3)
            if (r3 == 0) goto L_0x0046
            r3 = 14
            goto L_0x00eb
        L_0x00c9:
            java.lang.String r3 = "AllowedProtocols"
            boolean r3 = r7.equals(r3)
            if (r3 == 0) goto L_0x0046
            r3 = 9
            goto L_0x00eb
        L_0x00d4:
            java.lang.String r3 = "AllowedKeyMgmt"
            boolean r3 = r7.equals(r3)
            if (r3 == 0) goto L_0x0046
            r3 = 8
            goto L_0x00eb
        L_0x00df:
            java.lang.String r3 = "Shared"
            boolean r3 = r7.equals(r3)
            if (r3 == 0) goto L_0x0046
            r3 = 13
            goto L_0x00eb
        L_0x00ea:
            r3 = r8
        L_0x00eb:
            switch(r3) {
                case 0: goto L_0x0186;
                case 1: goto L_0x0180;
                case 2: goto L_0x017a;
                case 3: goto L_0x0174;
                case 4: goto L_0x016e;
                case 5: goto L_0x0164;
                case 6: goto L_0x015a;
                case 7: goto L_0x0150;
                case 8: goto L_0x0146;
                case 9: goto L_0x013c;
                case 10: goto L_0x0132;
                case 11: goto L_0x0128;
                case 12: goto L_0x011d;
                case 13: goto L_0x0112;
                case 14: goto L_0x0107;
                default: goto L_0x00ee;
            }
        L_0x00ee:
            org.xmlpull.v1.XmlPullParserException r3 = new org.xmlpull.v1.XmlPullParserException
            java.lang.StringBuilder r8 = new java.lang.StringBuilder
            r8.<init>()
            java.lang.String r9 = "Unknown value name found: "
            r8.append(r9)
            r6 = r4[r6]
            r8.append(r6)
            java.lang.String r6 = r8.toString()
            r3.<init>(r6)
            throw r3
        L_0x0107:
            r3 = r5
            java.lang.Integer r3 = (java.lang.Integer) r3
            int r3 = r3.intValue()
            r0.meteredOverride = r3
            goto L_0x018a
        L_0x0112:
            r3 = r5
            java.lang.Boolean r3 = (java.lang.Boolean) r3
            boolean r3 = r3.booleanValue()
            r0.shared = r3
            goto L_0x018a
        L_0x011d:
            r3 = r5
            byte[] r3 = (byte[]) r3
            java.util.BitSet r6 = java.util.BitSet.valueOf(r3)
            r0.allowedPairwiseCiphers = r6
            goto L_0x018a
        L_0x0128:
            r3 = r5
            byte[] r3 = (byte[]) r3
            java.util.BitSet r6 = java.util.BitSet.valueOf(r3)
            r0.allowedGroupCiphers = r6
            goto L_0x018a
        L_0x0132:
            r3 = r5
            byte[] r3 = (byte[]) r3
            java.util.BitSet r6 = java.util.BitSet.valueOf(r3)
            r0.allowedAuthAlgorithms = r6
            goto L_0x018a
        L_0x013c:
            r3 = r5
            byte[] r3 = (byte[]) r3
            java.util.BitSet r6 = java.util.BitSet.valueOf(r3)
            r0.allowedProtocols = r6
            goto L_0x018a
        L_0x0146:
            r3 = r5
            byte[] r3 = (byte[]) r3
            java.util.BitSet r6 = java.util.BitSet.valueOf(r3)
            r0.allowedKeyManagement = r6
            goto L_0x018a
        L_0x0150:
            r3 = r5
            java.lang.Boolean r3 = (java.lang.Boolean) r3
            boolean r3 = r3.booleanValue()
            r0.requirePMF = r3
            goto L_0x018a
        L_0x015a:
            r3 = r5
            java.lang.Boolean r3 = (java.lang.Boolean) r3
            boolean r3 = r3.booleanValue()
            r0.hiddenSSID = r3
            goto L_0x018a
        L_0x0164:
            r3 = r5
            java.lang.Integer r3 = (java.lang.Integer) r3
            int r3 = r3.intValue()
            r0.wepTxKeyIndex = r3
            goto L_0x018a
        L_0x016e:
            java.lang.String[] r3 = r0.wepKeys
            populateWepKeysFromXmlValue(r5, r3)
            goto L_0x018a
        L_0x0174:
            r3 = r5
            java.lang.String r3 = (java.lang.String) r3
            r0.preSharedKey = r3
            goto L_0x018a
        L_0x017a:
            r3 = r5
            java.lang.String r3 = (java.lang.String) r3
            r0.BSSID = r3
            goto L_0x018a
        L_0x0180:
            r3 = r5
            java.lang.String r3 = (java.lang.String) r3
            r0.SSID = r3
            goto L_0x018a
        L_0x0186:
            r1 = r5
            java.lang.String r1 = (java.lang.String) r1
        L_0x018a:
            goto L_0x000a
        L_0x018c:
            org.xmlpull.v1.XmlPullParserException r3 = new org.xmlpull.v1.XmlPullParserException
            java.lang.String r6 = "Missing value name"
            r3.<init>(r6)
            throw r3
        L_0x0194:
            clearAnyKnownIssuesInParsedConfiguration(r0)
            android.util.Pair r3 = android.util.Pair.create(r1, r0)
            return r3
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.wifi.WifiBackupDataV1Parser.parseWifiConfigurationFromXmlInternal(org.xmlpull.v1.XmlPullParser, int, int):android.util.Pair");
    }

    private static Set<String> getSupportedWifiConfigurationTags(int minorVersion) {
        if (minorVersion == 0) {
            return WIFI_CONFIGURATION_MINOR_V0_SUPPORTED_TAGS;
        }
        if (minorVersion == 1) {
            return WIFI_CONFIGURATION_MINOR_V1_SUPPORTED_TAGS;
        }
        Log.e(TAG, "Invalid minorVersion: " + minorVersion);
        return Collections.emptySet();
    }

    private static void populateWepKeysFromXmlValue(Object value, String[] wepKeys) throws XmlPullParserException, IOException {
        String[] wepKeysInData = (String[]) value;
        if (wepKeysInData != null) {
            if (wepKeysInData.length == wepKeys.length) {
                for (int i = 0; i < wepKeys.length; i++) {
                    if (wepKeysInData[i].isEmpty()) {
                        wepKeys[i] = null;
                    } else {
                        wepKeys[i] = wepKeysInData[i];
                    }
                }
                return;
            }
            throw new XmlPullParserException("Invalid Wep Keys length: " + wepKeysInData.length);
        }
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r17v1, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v8, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v10, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v12, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v14, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v16, resolved type: java.lang.String[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v18, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v20, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v22, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v25, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v27, resolved type: java.lang.String} */
    /* JADX WARNING: Can't fix incorrect switch cases order */
    /* JADX WARNING: Code restructure failed: missing block: B:35:0x00ad, code lost:
        if (r13.equals(com.android.server.wifi.util.XmlUtil.IpConfigurationXmlUtil.XML_TAG_LINK_ADDRESS) != false) goto L_0x00bb;
     */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static android.net.IpConfiguration parseIpConfigurationFromXml(org.xmlpull.v1.XmlPullParser r23, int r24, int r25) throws org.xmlpull.v1.XmlPullParserException, java.io.IOException {
        /*
            java.util.Set r0 = getSupportedIpConfigurationTags(r25)
            r1 = 0
            r2 = 0
            r3 = 0
            r4 = 0
            r5 = 0
            r6 = 0
            r7 = 0
            r8 = -1
            r9 = 0
            r10 = 0
        L_0x000e:
            boolean r11 = com.android.server.wifi.util.XmlUtil.isNextSectionEnd(r23, r24)
            java.lang.String r14 = "WifiBackupDataV1Parser"
            r16 = 0
            r12 = 1
            if (r11 != 0) goto L_0x0124
            java.lang.String[] r11 = new java.lang.String[r12]
            r15 = r23
            java.lang.Object r17 = com.android.server.wifi.util.XmlUtil.readCurrentValue(r15, r11)
            r13 = r11[r16]
            if (r13 == 0) goto L_0x011a
            boolean r18 = r0.contains(r13)
            if (r18 != 0) goto L_0x0049
            java.lang.StringBuilder r12 = new java.lang.StringBuilder
            r12.<init>()
            r18 = r0
            java.lang.String r0 = "Unsupported tag + \""
            r12.append(r0)
            r12.append(r13)
            java.lang.String r0 = "\" found in <IpConfiguration> section, ignoring."
            r12.append(r0)
            java.lang.String r0 = r12.toString()
            android.util.Log.w(r14, r0)
            r0 = r18
            goto L_0x000e
        L_0x0049:
            r18 = r0
            int r0 = r13.hashCode()
            switch(r0) {
                case -1747338169: goto L_0x00b0;
                case -1520820614: goto L_0x00a7;
                case -1464842926: goto L_0x009d;
                case -920546460: goto L_0x0092;
                case 162774900: goto L_0x0087;
                case 858907952: goto L_0x007d;
                case 1527606550: goto L_0x0073;
                case 1527844847: goto L_0x0069;
                case 1940148190: goto L_0x005e;
                case 1968819345: goto L_0x0054;
                default: goto L_0x0052;
            }
        L_0x0052:
            goto L_0x00ba
        L_0x0054:
            java.lang.String r0 = "ProxySettings"
            boolean r0 = r13.equals(r0)
            if (r0 == 0) goto L_0x0052
            r12 = 5
            goto L_0x00bb
        L_0x005e:
            java.lang.String r0 = "ProxyExclusionList"
            boolean r0 = r13.equals(r0)
            if (r0 == 0) goto L_0x0052
            r12 = 8
            goto L_0x00bb
        L_0x0069:
            java.lang.String r0 = "ProxyPort"
            boolean r0 = r13.equals(r0)
            if (r0 == 0) goto L_0x0052
            r12 = 7
            goto L_0x00bb
        L_0x0073:
            java.lang.String r0 = "ProxyHost"
            boolean r0 = r13.equals(r0)
            if (r0 == 0) goto L_0x0052
            r12 = 6
            goto L_0x00bb
        L_0x007d:
            java.lang.String r0 = "GatewayAddress"
            boolean r0 = r13.equals(r0)
            if (r0 == 0) goto L_0x0052
            r12 = 3
            goto L_0x00bb
        L_0x0087:
            java.lang.String r0 = "IpAssignment"
            boolean r0 = r13.equals(r0)
            if (r0 == 0) goto L_0x0052
            r12 = r16
            goto L_0x00bb
        L_0x0092:
            java.lang.String r0 = "ProxyPac"
            boolean r0 = r13.equals(r0)
            if (r0 == 0) goto L_0x0052
            r12 = 9
            goto L_0x00bb
        L_0x009d:
            java.lang.String r0 = "LinkPrefixLength"
            boolean r0 = r13.equals(r0)
            if (r0 == 0) goto L_0x0052
            r12 = 2
            goto L_0x00bb
        L_0x00a7:
            java.lang.String r0 = "LinkAddress"
            boolean r0 = r13.equals(r0)
            if (r0 == 0) goto L_0x0052
            goto L_0x00bb
        L_0x00b0:
            java.lang.String r0 = "DNSServers"
            boolean r0 = r13.equals(r0)
            if (r0 == 0) goto L_0x0052
            r12 = 4
            goto L_0x00bb
        L_0x00ba:
            r12 = -1
        L_0x00bb:
            switch(r12) {
                case 0: goto L_0x0111;
                case 1: goto L_0x010b;
                case 2: goto L_0x0105;
                case 3: goto L_0x00ff;
                case 4: goto L_0x00f9;
                case 5: goto L_0x00f3;
                case 6: goto L_0x00ed;
                case 7: goto L_0x00e3;
                case 8: goto L_0x00dd;
                case 9: goto L_0x00d7;
                default: goto L_0x00be;
            }
        L_0x00be:
            org.xmlpull.v1.XmlPullParserException r0 = new org.xmlpull.v1.XmlPullParserException
            java.lang.StringBuilder r12 = new java.lang.StringBuilder
            r12.<init>()
            java.lang.String r14 = "Unknown value name found: "
            r12.append(r14)
            r14 = r11[r16]
            r12.append(r14)
            java.lang.String r12 = r12.toString()
            r0.<init>(r12)
            throw r0
        L_0x00d7:
            r0 = r17
            java.lang.String r0 = (java.lang.String) r0
            r10 = r0
            goto L_0x0116
        L_0x00dd:
            r0 = r17
            java.lang.String r0 = (java.lang.String) r0
            r9 = r0
            goto L_0x0116
        L_0x00e3:
            r0 = r17
            java.lang.Integer r0 = (java.lang.Integer) r0
            int r0 = r0.intValue()
            r8 = r0
            goto L_0x0116
        L_0x00ed:
            r0 = r17
            java.lang.String r0 = (java.lang.String) r0
            r7 = r0
            goto L_0x0116
        L_0x00f3:
            r0 = r17
            java.lang.String r0 = (java.lang.String) r0
            r6 = r0
            goto L_0x0116
        L_0x00f9:
            r0 = r17
            java.lang.String[] r0 = (java.lang.String[]) r0
            r5 = r0
            goto L_0x0116
        L_0x00ff:
            r0 = r17
            java.lang.String r0 = (java.lang.String) r0
            r4 = r0
            goto L_0x0116
        L_0x0105:
            r0 = r17
            java.lang.Integer r0 = (java.lang.Integer) r0
            r3 = r0
            goto L_0x0116
        L_0x010b:
            r0 = r17
            java.lang.String r0 = (java.lang.String) r0
            r2 = r0
            goto L_0x0116
        L_0x0111:
            r0 = r17
            java.lang.String r0 = (java.lang.String) r0
            r1 = r0
        L_0x0116:
            r0 = r18
            goto L_0x000e
        L_0x011a:
            r18 = r0
            org.xmlpull.v1.XmlPullParserException r0 = new org.xmlpull.v1.XmlPullParserException
            java.lang.String r12 = "Missing value name"
            r0.<init>(r12)
            throw r0
        L_0x0124:
            r15 = r23
            r18 = r0
            android.net.IpConfiguration r0 = new android.net.IpConfiguration
            r0.<init>()
            if (r1 == 0) goto L_0x0268
            android.net.IpConfiguration$IpAssignment r11 = android.net.IpConfiguration.IpAssignment.valueOf(r1)
            r0.setIpAssignment(r11)
            int[] r13 = com.android.server.wifi.WifiBackupDataV1Parser.AnonymousClass2.$SwitchMap$android$net$IpConfiguration$IpAssignment
            int r17 = r11.ordinal()
            r13 = r13[r17]
            if (r13 == r12) goto L_0x0164
            r12 = 2
            if (r13 == r12) goto L_0x015e
            r12 = 3
            if (r13 != r12) goto L_0x0147
            goto L_0x015e
        L_0x0147:
            org.xmlpull.v1.XmlPullParserException r12 = new org.xmlpull.v1.XmlPullParserException
            java.lang.StringBuilder r13 = new java.lang.StringBuilder
            r13.<init>()
            java.lang.String r14 = "Unknown ip assignment type: "
            r13.append(r14)
            r13.append(r11)
            java.lang.String r13 = r13.toString()
            r12.<init>(r13)
            throw r12
        L_0x015e:
            r19 = r1
            r20 = r2
            goto L_0x01ee
        L_0x0164:
            android.net.StaticIpConfiguration r12 = new android.net.StaticIpConfiguration
            r12.<init>()
            if (r2 == 0) goto L_0x019f
            if (r3 == 0) goto L_0x019f
            android.net.LinkAddress r13 = new android.net.LinkAddress
            r19 = r1
            java.net.InetAddress r1 = android.net.NetworkUtils.numericToInetAddress(r2)
            r20 = r2
            int r2 = r3.intValue()
            r13.<init>(r1, r2)
            r1 = r13
            java.net.InetAddress r2 = r1.getAddress()
            boolean r2 = r2 instanceof java.net.Inet4Address
            if (r2 == 0) goto L_0x018a
            r12.ipAddress = r1
            goto L_0x01a3
        L_0x018a:
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r13 = "Non-IPv4 address: "
            r2.append(r13)
            r2.append(r1)
            java.lang.String r2 = r2.toString()
            android.util.Log.w(r14, r2)
            goto L_0x01a3
        L_0x019f:
            r19 = r1
            r20 = r2
        L_0x01a3:
            if (r4 == 0) goto L_0x01d0
            r1 = 0
            java.net.InetAddress r2 = android.net.NetworkUtils.numericToInetAddress(r4)
            android.net.RouteInfo r13 = new android.net.RouteInfo
            r13.<init>(r1, r2)
            boolean r21 = r13.isIPv4Default()
            if (r21 == 0) goto L_0x01b8
            r12.gateway = r2
            goto L_0x01d0
        L_0x01b8:
            r21 = r1
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r22 = r2
            java.lang.String r2 = "Non-IPv4 default route: "
            r1.append(r2)
            r1.append(r13)
            java.lang.String r1 = r1.toString()
            android.util.Log.w(r14, r1)
        L_0x01d0:
            if (r5 == 0) goto L_0x01ea
            int r1 = r5.length
            r2 = r16
        L_0x01d5:
            if (r2 >= r1) goto L_0x01ea
            r13 = r5[r2]
            java.net.InetAddress r14 = android.net.NetworkUtils.numericToInetAddress(r13)
            r16 = r1
            java.util.ArrayList r1 = r12.dnsServers
            r1.add(r14)
            int r2 = r2 + 1
            r1 = r16
            goto L_0x01d5
        L_0x01ea:
            r0.setStaticIpConfiguration(r12)
        L_0x01ee:
            if (r6 == 0) goto L_0x0260
            android.net.IpConfiguration$ProxySettings r1 = android.net.IpConfiguration.ProxySettings.valueOf(r6)
            r0.setProxySettings(r1)
            int[] r2 = com.android.server.wifi.WifiBackupDataV1Parser.AnonymousClass2.$SwitchMap$android$net$IpConfiguration$ProxySettings
            int r12 = r1.ordinal()
            r2 = r2[r12]
            r12 = 1
            if (r2 == r12) goto L_0x0237
            r12 = 2
            if (r2 == r12) goto L_0x0224
            r12 = 3
            if (r2 == r12) goto L_0x0223
            r12 = 4
            if (r2 != r12) goto L_0x020c
            goto L_0x0223
        L_0x020c:
            org.xmlpull.v1.XmlPullParserException r2 = new org.xmlpull.v1.XmlPullParserException
            java.lang.StringBuilder r12 = new java.lang.StringBuilder
            r12.<init>()
            java.lang.String r13 = "Unknown proxy settings type: "
            r12.append(r13)
            r12.append(r1)
            java.lang.String r12 = r12.toString()
            r2.<init>(r12)
            throw r2
        L_0x0223:
            goto L_0x0247
        L_0x0224:
            if (r10 == 0) goto L_0x022f
            android.net.ProxyInfo r2 = new android.net.ProxyInfo
            r2.<init>(r10)
            r0.setHttpProxy(r2)
            goto L_0x0247
        L_0x022f:
            org.xmlpull.v1.XmlPullParserException r2 = new org.xmlpull.v1.XmlPullParserException
            java.lang.String r12 = "ProxyPac was missing in IpConfiguration section"
            r2.<init>(r12)
            throw r2
        L_0x0237:
            if (r7 == 0) goto L_0x0258
            r2 = -1
            if (r8 == r2) goto L_0x0250
            if (r9 == 0) goto L_0x0248
            android.net.ProxyInfo r2 = new android.net.ProxyInfo
            r2.<init>(r7, r8, r9)
            r0.setHttpProxy(r2)
        L_0x0247:
            return r0
        L_0x0248:
            org.xmlpull.v1.XmlPullParserException r2 = new org.xmlpull.v1.XmlPullParserException
            java.lang.String r12 = "ProxyExclusionList was missing in IpConfiguration section"
            r2.<init>(r12)
            throw r2
        L_0x0250:
            org.xmlpull.v1.XmlPullParserException r2 = new org.xmlpull.v1.XmlPullParserException
            java.lang.String r12 = "ProxyPort was missing in IpConfiguration section"
            r2.<init>(r12)
            throw r2
        L_0x0258:
            org.xmlpull.v1.XmlPullParserException r2 = new org.xmlpull.v1.XmlPullParserException
            java.lang.String r12 = "ProxyHost was missing in IpConfiguration section"
            r2.<init>(r12)
            throw r2
        L_0x0260:
            org.xmlpull.v1.XmlPullParserException r1 = new org.xmlpull.v1.XmlPullParserException
            java.lang.String r2 = "ProxySettings was missing in IpConfiguration section"
            r1.<init>(r2)
            throw r1
        L_0x0268:
            r19 = r1
            r20 = r2
            org.xmlpull.v1.XmlPullParserException r1 = new org.xmlpull.v1.XmlPullParserException
            java.lang.String r2 = "IpAssignment was missing in IpConfiguration section"
            r1.<init>(r2)
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.wifi.WifiBackupDataV1Parser.parseIpConfigurationFromXml(org.xmlpull.v1.XmlPullParser, int, int):android.net.IpConfiguration");
    }

    /* renamed from: com.android.server.wifi.WifiBackupDataV1Parser$2  reason: invalid class name */
    static /* synthetic */ class AnonymousClass2 {
        static final /* synthetic */ int[] $SwitchMap$android$net$IpConfiguration$IpAssignment = new int[IpConfiguration.IpAssignment.values().length];
        static final /* synthetic */ int[] $SwitchMap$android$net$IpConfiguration$ProxySettings = new int[IpConfiguration.ProxySettings.values().length];

        static {
            try {
                $SwitchMap$android$net$IpConfiguration$ProxySettings[IpConfiguration.ProxySettings.STATIC.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$android$net$IpConfiguration$ProxySettings[IpConfiguration.ProxySettings.PAC.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$android$net$IpConfiguration$ProxySettings[IpConfiguration.ProxySettings.NONE.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$android$net$IpConfiguration$ProxySettings[IpConfiguration.ProxySettings.UNASSIGNED.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
            try {
                $SwitchMap$android$net$IpConfiguration$IpAssignment[IpConfiguration.IpAssignment.STATIC.ordinal()] = 1;
            } catch (NoSuchFieldError e5) {
            }
            try {
                $SwitchMap$android$net$IpConfiguration$IpAssignment[IpConfiguration.IpAssignment.DHCP.ordinal()] = 2;
            } catch (NoSuchFieldError e6) {
            }
            try {
                $SwitchMap$android$net$IpConfiguration$IpAssignment[IpConfiguration.IpAssignment.UNASSIGNED.ordinal()] = 3;
            } catch (NoSuchFieldError e7) {
            }
        }
    }

    private static Set<String> getSupportedIpConfigurationTags(int minorVersion) {
        if (minorVersion == 0 || minorVersion == 1) {
            return IP_CONFIGURATION_MINOR_V0_V1_SUPPORTED_TAGS;
        }
        Log.e(TAG, "Invalid minorVersion: " + minorVersion);
        return Collections.emptySet();
    }
}
