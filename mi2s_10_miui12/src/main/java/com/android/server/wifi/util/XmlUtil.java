package com.android.server.wifi.util;

import android.net.IpConfiguration;
import android.net.LinkAddress;
import android.net.NetworkUtils;
import android.net.ProxyInfo;
import android.net.RouteInfo;
import android.net.StaticIpConfiguration;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiEnterpriseConfig;
import android.util.Log;
import com.android.internal.util.XmlUtils;
import com.android.server.wifi.WifiBackupRestore;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.util.Iterator;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

public class XmlUtil {
    private static final String TAG = "WifiXmlUtil";

    private static void gotoStartTag(XmlPullParser in) throws XmlPullParserException, IOException {
        int type = in.getEventType();
        while (type != 2 && type != 1) {
            type = in.next();
        }
    }

    private static void gotoEndTag(XmlPullParser in) throws XmlPullParserException, IOException {
        int type = in.getEventType();
        while (type != 3 && type != 1) {
            type = in.next();
        }
    }

    public static void gotoDocumentStart(XmlPullParser in, String headerName) throws XmlPullParserException, IOException {
        XmlUtils.beginDocument(in, headerName);
    }

    public static boolean gotoNextSectionOrEnd(XmlPullParser in, String[] headerName, int outerDepth) throws XmlPullParserException, IOException {
        if (!XmlUtils.nextElementWithin(in, outerDepth)) {
            return false;
        }
        headerName[0] = in.getName();
        return true;
    }

    public static boolean gotoNextSectionWithNameOrEnd(XmlPullParser in, String expectedName, int outerDepth) throws XmlPullParserException, IOException {
        String[] headerName = new String[1];
        if (!gotoNextSectionOrEnd(in, headerName, outerDepth)) {
            return false;
        }
        if (headerName[0].equals(expectedName)) {
            return true;
        }
        throw new XmlPullParserException("Next section name does not match expected name: " + expectedName);
    }

    public static void gotoNextSectionWithName(XmlPullParser in, String expectedName, int outerDepth) throws XmlPullParserException, IOException {
        if (!gotoNextSectionWithNameOrEnd(in, expectedName, outerDepth)) {
            throw new XmlPullParserException("Section not found. Expected: " + expectedName);
        }
    }

    public static boolean isNextSectionEnd(XmlPullParser in, int sectionDepth) throws XmlPullParserException, IOException {
        return !XmlUtils.nextElementWithin(in, sectionDepth);
    }

    public static Object readCurrentValue(XmlPullParser in, String[] valueName) throws XmlPullParserException, IOException {
        Object value = XmlUtils.readValueXml(in, valueName);
        gotoEndTag(in);
        return value;
    }

    public static Object readNextValueWithName(XmlPullParser in, String expectedName) throws XmlPullParserException, IOException {
        String[] valueName = new String[1];
        XmlUtils.nextElement(in);
        Object value = readCurrentValue(in, valueName);
        if (valueName[0].equals(expectedName)) {
            return value;
        }
        throw new XmlPullParserException("Value not found. Expected: " + expectedName + ", but got: " + valueName[0]);
    }

    public static void writeDocumentStart(XmlSerializer out, String headerName) throws IOException {
        out.startDocument((String) null, true);
        out.startTag((String) null, headerName);
    }

    public static void writeDocumentEnd(XmlSerializer out, String headerName) throws IOException {
        out.endTag((String) null, headerName);
        out.endDocument();
    }

    public static void writeNextSectionStart(XmlSerializer out, String headerName) throws IOException {
        out.startTag((String) null, headerName);
    }

    public static void writeNextSectionEnd(XmlSerializer out, String headerName) throws IOException {
        out.endTag((String) null, headerName);
    }

    public static void writeNextValue(XmlSerializer out, String name, Object value) throws XmlPullParserException, IOException {
        XmlUtils.writeValueXml(value, name, out);
    }

    public static class WifiConfigurationXmlUtil {
        public static final String XML_TAG_ALLOWED_AUTH_ALGOS = "AllowedAuthAlgos";
        public static final String XML_TAG_ALLOWED_GROUP_CIPHERS = "AllowedGroupCiphers";
        public static final String XML_TAG_ALLOWED_GROUP_MGMT_CIPHERS = "AllowedGroupMgmtCiphers";
        public static final String XML_TAG_ALLOWED_KEY_MGMT = "AllowedKeyMgmt";
        public static final String XML_TAG_ALLOWED_PAIRWISE_CIPHERS = "AllowedPairwiseCiphers";
        public static final String XML_TAG_ALLOWED_PROTOCOLS = "AllowedProtocols";
        public static final String XML_TAG_ALLOWED_SUITE_B_CIPHERS = "AllowedSuiteBCiphers";
        public static final String XML_TAG_BSSID = "BSSID";
        public static final String XML_TAG_CONFIG_KEY = "ConfigKey";
        public static final String XML_TAG_CREATION_TIME = "CreationTime";
        public static final String XML_TAG_CREATOR_NAME = "CreatorName";
        public static final String XML_TAG_CREATOR_UID = "CreatorUid";
        public static final String XML_TAG_DEFAULT_GW_MAC_ADDRESS = "DefaultGwMacAddress";
        public static final String XML_TAG_DPP_CONNECTOR = "DppConnector";
        public static final String XML_TAG_DPP_CSIGN = "DppCsign";
        public static final String XML_TAG_DPP_NETACCESSKEY = "DppNetAccessKey";
        public static final String XML_TAG_DPP_NETACCESSKEY_EXPIRY = "DppNetAccessKeyExpiry";
        public static final String XML_TAG_FQDN = "FQDN";
        public static final String XML_TAG_HIDDEN_SSID = "HiddenSSID";
        public static final String XML_TAG_IS_LEGACY_PASSPOINT_CONFIG = "IsLegacyPasspointConfig";
        public static final String XML_TAG_LAST_CONNECT_UID = "LastConnectUid";
        public static final String XML_TAG_LAST_UPDATE_NAME = "LastUpdateName";
        public static final String XML_TAG_LAST_UPDATE_UID = "LastUpdateUid";
        public static final String XML_TAG_LINKED_NETWORKS_LIST = "LinkedNetworksList";
        public static final String XML_TAG_MAC_RANDOMIZATION_SETTING = "MacRandomizationSetting";
        public static final String XML_TAG_METERED_HINT = "MeteredHint";
        public static final String XML_TAG_METERED_OVERRIDE = "MeteredOverride";
        public static final String XML_TAG_NO_INTERNET_ACCESS_EXPECTED = "NoInternetAccessExpected";
        public static final String XML_TAG_NUM_ASSOCIATION = "NumAssociation";
        public static final String XML_TAG_PRE_SHARED_KEY = "PreSharedKey";
        public static final String XML_TAG_PROVIDER_FRIENDLY_NAME = "ProviderFriendlyName";
        public static final String XML_TAG_RANDOMIZED_MAC_ADDRESS = "RandomizedMacAddress";
        public static final String XML_TAG_REQUIRE_PMF = "RequirePMF";
        public static final String XML_TAG_ROAMING_CONSORTIUM_OIS = "RoamingConsortiumOIs";
        public static final String XML_TAG_SHARED = "Shared";
        public static final String XML_TAG_SHARE_THIS_AP = "ShareThisAp";
        public static final String XML_TAG_SSID = "SSID";
        public static final String XML_TAG_STATUS = "Status";
        public static final String XML_TAG_USER_APPROVED = "UserApproved";
        public static final String XML_TAG_USE_EXTERNAL_SCORES = "UseExternalScores";
        public static final String XML_TAG_VALIDATED_INTERNET_ACCESS = "ValidatedInternetAccess";
        public static final String XML_TAG_WAPI_CERT_SEL = "WapiCertSel";
        public static final String XML_TAG_WAPI_CERT_SEL_MODE = "WapiCertSelMode";
        public static final String XML_TAG_WAPI_PSK = "WapiPsk";
        public static final String XML_TAG_WAPI_PSK_TYPE = "WapiPskType";
        public static final String XML_TAG_WEP_KEYS = "WEPKeys";
        public static final String XML_TAG_WEP_TX_KEY_INDEX = "WEPTxKeyIndex";

        private static void writeWepKeysToXml(XmlSerializer out, String[] wepKeys) throws XmlPullParserException, IOException {
            String[] wepKeysToWrite = new String[wepKeys.length];
            boolean hasWepKey = false;
            for (int i = 0; i < wepKeys.length; i++) {
                if (wepKeys[i] == null) {
                    wepKeysToWrite[i] = new String();
                } else {
                    wepKeysToWrite[i] = wepKeys[i];
                    hasWepKey = true;
                }
            }
            if (hasWepKey) {
                XmlUtil.writeNextValue(out, XML_TAG_WEP_KEYS, wepKeysToWrite);
            } else {
                XmlUtil.writeNextValue(out, XML_TAG_WEP_KEYS, (Object) null);
            }
        }

        public static void writeCommonElementsToXml(XmlSerializer out, WifiConfiguration configuration) throws XmlPullParserException, IOException {
            XmlUtil.writeNextValue(out, XML_TAG_CONFIG_KEY, configuration.configKey());
            XmlUtil.writeNextValue(out, XML_TAG_SSID, configuration.SSID);
            XmlUtil.writeNextValue(out, XML_TAG_BSSID, configuration.BSSID);
            XmlUtil.writeNextValue(out, XML_TAG_SHARE_THIS_AP, Boolean.valueOf(configuration.shareThisAp));
            XmlUtil.writeNextValue(out, XML_TAG_PRE_SHARED_KEY, configuration.preSharedKey);
            writeWepKeysToXml(out, configuration.wepKeys);
            XmlUtil.writeNextValue(out, XML_TAG_WEP_TX_KEY_INDEX, Integer.valueOf(configuration.wepTxKeyIndex));
            XmlUtil.writeNextValue(out, XML_TAG_HIDDEN_SSID, Boolean.valueOf(configuration.hiddenSSID));
            XmlUtil.writeNextValue(out, XML_TAG_REQUIRE_PMF, Boolean.valueOf(configuration.requirePMF));
            XmlUtil.writeNextValue(out, XML_TAG_ALLOWED_KEY_MGMT, configuration.allowedKeyManagement.toByteArray());
            XmlUtil.writeNextValue(out, XML_TAG_ALLOWED_PROTOCOLS, configuration.allowedProtocols.toByteArray());
            XmlUtil.writeNextValue(out, XML_TAG_ALLOWED_AUTH_ALGOS, configuration.allowedAuthAlgorithms.toByteArray());
            XmlUtil.writeNextValue(out, XML_TAG_ALLOWED_GROUP_CIPHERS, configuration.allowedGroupCiphers.toByteArray());
            XmlUtil.writeNextValue(out, XML_TAG_ALLOWED_PAIRWISE_CIPHERS, configuration.allowedPairwiseCiphers.toByteArray());
            XmlUtil.writeNextValue(out, XML_TAG_ALLOWED_GROUP_MGMT_CIPHERS, configuration.allowedGroupManagementCiphers.toByteArray());
            XmlUtil.writeNextValue(out, XML_TAG_ALLOWED_SUITE_B_CIPHERS, configuration.allowedSuiteBCiphers.toByteArray());
            if (configuration.allowedKeyManagement.get(190)) {
                XmlUtil.writeNextValue(out, XML_TAG_WAPI_PSK_TYPE, Integer.valueOf(configuration.wapiPskType));
                XmlUtil.writeNextValue(out, XML_TAG_WAPI_PSK, configuration.wapiPsk);
            } else if (configuration.allowedKeyManagement.get(191)) {
                XmlUtil.writeNextValue(out, XML_TAG_WAPI_CERT_SEL_MODE, Integer.valueOf(configuration.wapiCertSelMode));
                XmlUtil.writeNextValue(out, XML_TAG_WAPI_CERT_SEL, configuration.wapiCertSel);
            }
            XmlUtil.writeNextValue(out, XML_TAG_SHARED, Boolean.valueOf(configuration.shared));
        }

        public static void writeToXmlForBackup(XmlSerializer out, WifiConfiguration configuration) throws XmlPullParserException, IOException {
            writeCommonElementsToXml(out, configuration);
            XmlUtil.writeNextValue(out, XML_TAG_METERED_OVERRIDE, Integer.valueOf(configuration.meteredOverride));
        }

        public static void writeToXmlForConfigStore(XmlSerializer out, WifiConfiguration configuration) throws XmlPullParserException, IOException {
            writeCommonElementsToXml(out, configuration);
            XmlUtil.writeNextValue(out, XML_TAG_STATUS, Integer.valueOf(configuration.status));
            XmlUtil.writeNextValue(out, XML_TAG_FQDN, configuration.FQDN);
            XmlUtil.writeNextValue(out, XML_TAG_PROVIDER_FRIENDLY_NAME, configuration.providerFriendlyName);
            XmlUtil.writeNextValue(out, XML_TAG_LINKED_NETWORKS_LIST, configuration.linkedConfigurations);
            XmlUtil.writeNextValue(out, XML_TAG_DEFAULT_GW_MAC_ADDRESS, configuration.defaultGwMacAddress);
            XmlUtil.writeNextValue(out, XML_TAG_VALIDATED_INTERNET_ACCESS, Boolean.valueOf(configuration.validatedInternetAccess));
            XmlUtil.writeNextValue(out, XML_TAG_NO_INTERNET_ACCESS_EXPECTED, Boolean.valueOf(configuration.noInternetAccessExpected));
            XmlUtil.writeNextValue(out, XML_TAG_USER_APPROVED, Integer.valueOf(configuration.userApproved));
            XmlUtil.writeNextValue(out, XML_TAG_METERED_HINT, Boolean.valueOf(configuration.meteredHint));
            XmlUtil.writeNextValue(out, XML_TAG_METERED_OVERRIDE, Integer.valueOf(configuration.meteredOverride));
            XmlUtil.writeNextValue(out, XML_TAG_USE_EXTERNAL_SCORES, Boolean.valueOf(configuration.useExternalScores));
            XmlUtil.writeNextValue(out, XML_TAG_NUM_ASSOCIATION, Integer.valueOf(configuration.numAssociation));
            XmlUtil.writeNextValue(out, XML_TAG_CREATOR_UID, Integer.valueOf(configuration.creatorUid));
            XmlUtil.writeNextValue(out, XML_TAG_CREATOR_NAME, configuration.creatorName);
            XmlUtil.writeNextValue(out, XML_TAG_CREATION_TIME, configuration.creationTime);
            XmlUtil.writeNextValue(out, XML_TAG_LAST_UPDATE_UID, Integer.valueOf(configuration.lastUpdateUid));
            XmlUtil.writeNextValue(out, XML_TAG_LAST_UPDATE_NAME, configuration.lastUpdateName);
            XmlUtil.writeNextValue(out, XML_TAG_LAST_CONNECT_UID, Integer.valueOf(configuration.lastConnectUid));
            XmlUtil.writeNextValue(out, XML_TAG_IS_LEGACY_PASSPOINT_CONFIG, Boolean.valueOf(configuration.isLegacyPasspointConfig));
            XmlUtil.writeNextValue(out, XML_TAG_ROAMING_CONSORTIUM_OIS, configuration.roamingConsortiumIds);
            XmlUtil.writeNextValue(out, XML_TAG_RANDOMIZED_MAC_ADDRESS, configuration.getRandomizedMacAddress().toString());
            XmlUtil.writeNextValue(out, XML_TAG_MAC_RANDOMIZATION_SETTING, Integer.valueOf(configuration.macRandomizationSetting));
            XmlUtil.writeNextValue(out, XML_TAG_DPP_CONNECTOR, configuration.dppConnector);
            XmlUtil.writeNextValue(out, XML_TAG_DPP_NETACCESSKEY, configuration.dppNetAccessKey);
            XmlUtil.writeNextValue(out, XML_TAG_DPP_NETACCESSKEY_EXPIRY, Integer.valueOf(configuration.dppNetAccessKeyExpiry));
            XmlUtil.writeNextValue(out, XML_TAG_DPP_CSIGN, configuration.dppCsign);
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

        /* JADX WARNING: Can't fix incorrect switch cases order */
        /* JADX WARNING: Code restructure failed: missing block: B:77:0x0138, code lost:
            if (r7.equals(XML_TAG_SSID) != false) goto L_0x0248;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public static android.util.Pair<java.lang.String, android.net.wifi.WifiConfiguration> parseFromXml(org.xmlpull.v1.XmlPullParser r10, int r11) throws org.xmlpull.v1.XmlPullParserException, java.io.IOException {
            /*
                android.net.wifi.WifiConfiguration r0 = new android.net.wifi.WifiConfiguration
                r0.<init>()
                r1 = 0
                r2 = 0
            L_0x0007:
                boolean r3 = com.android.server.wifi.util.XmlUtil.isNextSectionEnd(r10, r11)
                r4 = 0
                if (r3 != 0) goto L_0x0422
                r3 = 1
                java.lang.String[] r5 = new java.lang.String[r3]
                java.lang.Object r6 = com.android.server.wifi.util.XmlUtil.readCurrentValue(r10, r5)
                r7 = r5[r4]
                if (r7 == 0) goto L_0x041a
                r7 = r5[r4]
                r8 = -1
                int r9 = r7.hashCode()
                switch(r9) {
                    case -2072453770: goto L_0x023c;
                    case -1882996619: goto L_0x0231;
                    case -1819699067: goto L_0x0226;
                    case -1808614382: goto L_0x021b;
                    case -1793081233: goto L_0x0210;
                    case -1704616680: goto L_0x0205;
                    case -1663465224: goto L_0x01fa;
                    case -1568560548: goto L_0x01ef;
                    case -1507887771: goto L_0x01e4;
                    case -1268502125: goto L_0x01d8;
                    case -1173588624: goto L_0x01cc;
                    case -1089007030: goto L_0x01c0;
                    case -922179420: goto L_0x01b4;
                    case -911845214: goto L_0x01a8;
                    case -365584394: goto L_0x019c;
                    case -346924001: goto L_0x0190;
                    case -244338402: goto L_0x0184;
                    case -183222721: goto L_0x0178;
                    case -181205965: goto L_0x016c;
                    case -135994866: goto L_0x0160;
                    case -94981986: goto L_0x0154;
                    case -51197516: goto L_0x0148;
                    case 2165397: goto L_0x013c;
                    case 2554747: goto L_0x0132;
                    case 63507133: goto L_0x0127;
                    case 581636034: goto L_0x011b;
                    case 682791106: goto L_0x010f;
                    case 736944625: goto L_0x0103;
                    case 770704307: goto L_0x00f7;
                    case 797043831: goto L_0x00ec;
                    case 943896851: goto L_0x00e0;
                    case 1035394844: goto L_0x00d4;
                    case 1199498141: goto L_0x00c9;
                    case 1350351025: goto L_0x00bd;
                    case 1476993207: goto L_0x00b1;
                    case 1682682729: goto L_0x00a5;
                    case 1749510156: goto L_0x009a;
                    case 1750336108: goto L_0x008e;
                    case 1851050768: goto L_0x0082;
                    case 1905126713: goto L_0x0077;
                    case 1955037270: goto L_0x006c;
                    case 1965854789: goto L_0x0061;
                    case 2018202939: goto L_0x0055;
                    case 2025240199: goto L_0x0049;
                    case 2026125174: goto L_0x003d;
                    case 2140053948: goto L_0x0031;
                    case 2143705732: goto L_0x0025;
                    default: goto L_0x0023;
                }
            L_0x0023:
                goto L_0x0247
            L_0x0025:
                java.lang.String r3 = "RequirePMF"
                boolean r3 = r7.equals(r3)
                if (r3 == 0) goto L_0x0023
                r3 = 8
                goto L_0x0248
            L_0x0031:
                java.lang.String r3 = "DppCsign"
                boolean r3 = r7.equals(r3)
                if (r3 == 0) goto L_0x0023
                r3 = 45
                goto L_0x0248
            L_0x003d:
                java.lang.String r3 = "AllowedSuiteBCiphers"
                boolean r3 = r7.equals(r3)
                if (r3 == 0) goto L_0x0023
                r3 = 15
                goto L_0x0248
            L_0x0049:
                java.lang.String r3 = "ProviderFriendlyName"
                boolean r3 = r7.equals(r3)
                if (r3 == 0) goto L_0x0023
                r3 = 23
                goto L_0x0248
            L_0x0055:
                java.lang.String r3 = "NumAssociation"
                boolean r3 = r7.equals(r3)
                if (r3 == 0) goto L_0x0023
                r3 = 32
                goto L_0x0248
            L_0x0061:
                java.lang.String r3 = "HiddenSSID"
                boolean r3 = r7.equals(r3)
                if (r3 == 0) goto L_0x0023
                r3 = 7
                goto L_0x0248
            L_0x006c:
                java.lang.String r3 = "WEPKeys"
                boolean r3 = r7.equals(r3)
                if (r3 == 0) goto L_0x0023
                r3 = 5
                goto L_0x0248
            L_0x0077:
                java.lang.String r3 = "WEPTxKeyIndex"
                boolean r3 = r7.equals(r3)
                if (r3 == 0) goto L_0x0023
                r3 = 6
                goto L_0x0248
            L_0x0082:
                java.lang.String r3 = "AllowedAuthAlgos"
                boolean r3 = r7.equals(r3)
                if (r3 == 0) goto L_0x0023
                r3 = 11
                goto L_0x0248
            L_0x008e:
                java.lang.String r3 = "CreationTime"
                boolean r3 = r7.equals(r3)
                if (r3 == 0) goto L_0x0023
                r3 = 35
                goto L_0x0248
            L_0x009a:
                java.lang.String r3 = "ShareThisAp"
                boolean r3 = r7.equals(r3)
                if (r3 == 0) goto L_0x0023
                r3 = 3
                goto L_0x0248
            L_0x00a5:
                java.lang.String r3 = "DppConnector"
                boolean r3 = r7.equals(r3)
                if (r3 == 0) goto L_0x0023
                r3 = 42
                goto L_0x0248
            L_0x00b1:
                java.lang.String r3 = "CreatorName"
                boolean r3 = r7.equals(r3)
                if (r3 == 0) goto L_0x0023
                r3 = 34
                goto L_0x0248
            L_0x00bd:
                java.lang.String r3 = "LastUpdateUid"
                boolean r3 = r7.equals(r3)
                if (r3 == 0) goto L_0x0023
                r3 = 36
                goto L_0x0248
            L_0x00c9:
                java.lang.String r3 = "ConfigKey"
                boolean r3 = r7.equals(r3)
                if (r3 == 0) goto L_0x0023
                r3 = r4
                goto L_0x0248
            L_0x00d4:
                java.lang.String r3 = "LinkedNetworksList"
                boolean r3 = r7.equals(r3)
                if (r3 == 0) goto L_0x0023
                r3 = 24
                goto L_0x0248
            L_0x00e0:
                java.lang.String r3 = "UseExternalScores"
                boolean r3 = r7.equals(r3)
                if (r3 == 0) goto L_0x0023
                r3 = 31
                goto L_0x0248
            L_0x00ec:
                java.lang.String r3 = "PreSharedKey"
                boolean r3 = r7.equals(r3)
                if (r3 == 0) goto L_0x0023
                r3 = 4
                goto L_0x0248
            L_0x00f7:
                java.lang.String r3 = "WapiCertSel"
                boolean r3 = r7.equals(r3)
                if (r3 == 0) goto L_0x0023
                r3 = 19
                goto L_0x0248
            L_0x0103:
                java.lang.String r3 = "AllowedGroupCiphers"
                boolean r3 = r7.equals(r3)
                if (r3 == 0) goto L_0x0023
                r3 = 12
                goto L_0x0248
            L_0x010f:
                java.lang.String r3 = "AllowedPairwiseCiphers"
                boolean r3 = r7.equals(r3)
                if (r3 == 0) goto L_0x0023
                r3 = 13
                goto L_0x0248
            L_0x011b:
                java.lang.String r3 = "UserApproved"
                boolean r3 = r7.equals(r3)
                if (r3 == 0) goto L_0x0023
                r3 = 28
                goto L_0x0248
            L_0x0127:
                java.lang.String r3 = "BSSID"
                boolean r3 = r7.equals(r3)
                if (r3 == 0) goto L_0x0023
                r3 = 2
                goto L_0x0248
            L_0x0132:
                java.lang.String r9 = "SSID"
                boolean r7 = r7.equals(r9)
                if (r7 == 0) goto L_0x0023
                goto L_0x0248
            L_0x013c:
                java.lang.String r3 = "FQDN"
                boolean r3 = r7.equals(r3)
                if (r3 == 0) goto L_0x0023
                r3 = 22
                goto L_0x0248
            L_0x0148:
                java.lang.String r3 = "MeteredOverride"
                boolean r3 = r7.equals(r3)
                if (r3 == 0) goto L_0x0023
                r3 = 30
                goto L_0x0248
            L_0x0154:
                java.lang.String r3 = "MacRandomizationSetting"
                boolean r3 = r7.equals(r3)
                if (r3 == 0) goto L_0x0023
                r3 = 46
                goto L_0x0248
            L_0x0160:
                java.lang.String r3 = "IsLegacyPasspointConfig"
                boolean r3 = r7.equals(r3)
                if (r3 == 0) goto L_0x0023
                r3 = 39
                goto L_0x0248
            L_0x016c:
                java.lang.String r3 = "AllowedProtocols"
                boolean r3 = r7.equals(r3)
                if (r3 == 0) goto L_0x0023
                r3 = 10
                goto L_0x0248
            L_0x0178:
                java.lang.String r3 = "WapiPskType"
                boolean r3 = r7.equals(r3)
                if (r3 == 0) goto L_0x0023
                r3 = 16
                goto L_0x0248
            L_0x0184:
                java.lang.String r3 = "NoInternetAccessExpected"
                boolean r3 = r7.equals(r3)
                if (r3 == 0) goto L_0x0023
                r3 = 27
                goto L_0x0248
            L_0x0190:
                java.lang.String r3 = "RoamingConsortiumOIs"
                boolean r3 = r7.equals(r3)
                if (r3 == 0) goto L_0x0023
                r3 = 40
                goto L_0x0248
            L_0x019c:
                java.lang.String r3 = "WapiCertSelMode"
                boolean r3 = r7.equals(r3)
                if (r3 == 0) goto L_0x0023
                r3 = 18
                goto L_0x0248
            L_0x01a8:
                java.lang.String r3 = "DppNetAccessKey"
                boolean r3 = r7.equals(r3)
                if (r3 == 0) goto L_0x0023
                r3 = 43
                goto L_0x0248
            L_0x01b4:
                java.lang.String r3 = "CreatorUid"
                boolean r3 = r7.equals(r3)
                if (r3 == 0) goto L_0x0023
                r3 = 33
                goto L_0x0248
            L_0x01c0:
                java.lang.String r3 = "LastUpdateName"
                boolean r3 = r7.equals(r3)
                if (r3 == 0) goto L_0x0023
                r3 = 37
                goto L_0x0248
            L_0x01cc:
                java.lang.String r3 = "AllowedGroupMgmtCiphers"
                boolean r3 = r7.equals(r3)
                if (r3 == 0) goto L_0x0023
                r3 = 14
                goto L_0x0248
            L_0x01d8:
                java.lang.String r3 = "ValidatedInternetAccess"
                boolean r3 = r7.equals(r3)
                if (r3 == 0) goto L_0x0023
                r3 = 26
                goto L_0x0248
            L_0x01e4:
                java.lang.String r3 = "WapiPsk"
                boolean r3 = r7.equals(r3)
                if (r3 == 0) goto L_0x0023
                r3 = 17
                goto L_0x0248
            L_0x01ef:
                java.lang.String r3 = "LastConnectUid"
                boolean r3 = r7.equals(r3)
                if (r3 == 0) goto L_0x0023
                r3 = 38
                goto L_0x0248
            L_0x01fa:
                java.lang.String r3 = "RandomizedMacAddress"
                boolean r3 = r7.equals(r3)
                if (r3 == 0) goto L_0x0023
                r3 = 41
                goto L_0x0248
            L_0x0205:
                java.lang.String r3 = "AllowedKeyMgmt"
                boolean r3 = r7.equals(r3)
                if (r3 == 0) goto L_0x0023
                r3 = 9
                goto L_0x0248
            L_0x0210:
                java.lang.String r3 = "MeteredHint"
                boolean r3 = r7.equals(r3)
                if (r3 == 0) goto L_0x0023
                r3 = 29
                goto L_0x0248
            L_0x021b:
                java.lang.String r3 = "Status"
                boolean r3 = r7.equals(r3)
                if (r3 == 0) goto L_0x0023
                r3 = 21
                goto L_0x0248
            L_0x0226:
                java.lang.String r3 = "Shared"
                boolean r3 = r7.equals(r3)
                if (r3 == 0) goto L_0x0023
                r3 = 20
                goto L_0x0248
            L_0x0231:
                java.lang.String r3 = "DppNetAccessKeyExpiry"
                boolean r3 = r7.equals(r3)
                if (r3 == 0) goto L_0x0023
                r3 = 44
                goto L_0x0248
            L_0x023c:
                java.lang.String r3 = "DefaultGwMacAddress"
                boolean r3 = r7.equals(r3)
                if (r3 == 0) goto L_0x0023
                r3 = 25
                goto L_0x0248
            L_0x0247:
                r3 = r8
            L_0x0248:
                switch(r3) {
                    case 0: goto L_0x0414;
                    case 1: goto L_0x040e;
                    case 2: goto L_0x0408;
                    case 3: goto L_0x03fe;
                    case 4: goto L_0x03f8;
                    case 5: goto L_0x03f2;
                    case 6: goto L_0x03e8;
                    case 7: goto L_0x03de;
                    case 8: goto L_0x03d4;
                    case 9: goto L_0x03ca;
                    case 10: goto L_0x03c0;
                    case 11: goto L_0x03b6;
                    case 12: goto L_0x03ac;
                    case 13: goto L_0x03a0;
                    case 14: goto L_0x0394;
                    case 15: goto L_0x0388;
                    case 16: goto L_0x037d;
                    case 17: goto L_0x0376;
                    case 18: goto L_0x036b;
                    case 19: goto L_0x0364;
                    case 20: goto L_0x0359;
                    case 21: goto L_0x034b;
                    case 22: goto L_0x0344;
                    case 23: goto L_0x033d;
                    case 24: goto L_0x0336;
                    case 25: goto L_0x032f;
                    case 26: goto L_0x0324;
                    case 27: goto L_0x0319;
                    case 28: goto L_0x030e;
                    case 29: goto L_0x0303;
                    case 30: goto L_0x02f8;
                    case 31: goto L_0x02ed;
                    case 32: goto L_0x02e2;
                    case 33: goto L_0x02d7;
                    case 34: goto L_0x02d0;
                    case 35: goto L_0x02c9;
                    case 36: goto L_0x02be;
                    case 37: goto L_0x02b7;
                    case 38: goto L_0x02ac;
                    case 39: goto L_0x02a1;
                    case 40: goto L_0x029a;
                    case 41: goto L_0x0289;
                    case 42: goto L_0x0293;
                    case 43: goto L_0x0282;
                    case 44: goto L_0x0277;
                    case 45: goto L_0x0270;
                    case 46: goto L_0x0264;
                    default: goto L_0x024b;
                }
            L_0x024b:
                org.xmlpull.v1.XmlPullParserException r3 = new org.xmlpull.v1.XmlPullParserException
                java.lang.StringBuilder r7 = new java.lang.StringBuilder
                r7.<init>()
                java.lang.String r8 = "Unknown value name found: "
                r7.append(r8)
                r4 = r5[r4]
                r7.append(r4)
                java.lang.String r4 = r7.toString()
                r3.<init>(r4)
                throw r3
            L_0x0264:
                r3 = r6
                java.lang.Integer r3 = (java.lang.Integer) r3
                int r3 = r3.intValue()
                r0.macRandomizationSetting = r3
                r2 = 1
                goto L_0x0418
            L_0x0270:
                r3 = r6
                java.lang.String r3 = (java.lang.String) r3
                r0.dppCsign = r3
                goto L_0x0418
            L_0x0277:
                r3 = r6
                java.lang.Integer r3 = (java.lang.Integer) r3
                int r3 = r3.intValue()
                r0.dppNetAccessKeyExpiry = r3
                goto L_0x0418
            L_0x0282:
                r3 = r6
                java.lang.String r3 = (java.lang.String) r3
                r0.dppNetAccessKey = r3
                goto L_0x0418
            L_0x0289:
                r3 = r6
                java.lang.String r3 = (java.lang.String) r3
                android.net.MacAddress r3 = android.net.MacAddress.fromString(r3)
                r0.setRandomizedMacAddress(r3)
            L_0x0293:
                r3 = r6
                java.lang.String r3 = (java.lang.String) r3
                r0.dppConnector = r3
                goto L_0x0418
            L_0x029a:
                r3 = r6
                long[] r3 = (long[]) r3
                r0.roamingConsortiumIds = r3
                goto L_0x0418
            L_0x02a1:
                r3 = r6
                java.lang.Boolean r3 = (java.lang.Boolean) r3
                boolean r3 = r3.booleanValue()
                r0.isLegacyPasspointConfig = r3
                goto L_0x0418
            L_0x02ac:
                r3 = r6
                java.lang.Integer r3 = (java.lang.Integer) r3
                int r3 = r3.intValue()
                r0.lastConnectUid = r3
                goto L_0x0418
            L_0x02b7:
                r3 = r6
                java.lang.String r3 = (java.lang.String) r3
                r0.lastUpdateName = r3
                goto L_0x0418
            L_0x02be:
                r3 = r6
                java.lang.Integer r3 = (java.lang.Integer) r3
                int r3 = r3.intValue()
                r0.lastUpdateUid = r3
                goto L_0x0418
            L_0x02c9:
                r3 = r6
                java.lang.String r3 = (java.lang.String) r3
                r0.creationTime = r3
                goto L_0x0418
            L_0x02d0:
                r3 = r6
                java.lang.String r3 = (java.lang.String) r3
                r0.creatorName = r3
                goto L_0x0418
            L_0x02d7:
                r3 = r6
                java.lang.Integer r3 = (java.lang.Integer) r3
                int r3 = r3.intValue()
                r0.creatorUid = r3
                goto L_0x0418
            L_0x02e2:
                r3 = r6
                java.lang.Integer r3 = (java.lang.Integer) r3
                int r3 = r3.intValue()
                r0.numAssociation = r3
                goto L_0x0418
            L_0x02ed:
                r3 = r6
                java.lang.Boolean r3 = (java.lang.Boolean) r3
                boolean r3 = r3.booleanValue()
                r0.useExternalScores = r3
                goto L_0x0418
            L_0x02f8:
                r3 = r6
                java.lang.Integer r3 = (java.lang.Integer) r3
                int r3 = r3.intValue()
                r0.meteredOverride = r3
                goto L_0x0418
            L_0x0303:
                r3 = r6
                java.lang.Boolean r3 = (java.lang.Boolean) r3
                boolean r3 = r3.booleanValue()
                r0.meteredHint = r3
                goto L_0x0418
            L_0x030e:
                r3 = r6
                java.lang.Integer r3 = (java.lang.Integer) r3
                int r3 = r3.intValue()
                r0.userApproved = r3
                goto L_0x0418
            L_0x0319:
                r3 = r6
                java.lang.Boolean r3 = (java.lang.Boolean) r3
                boolean r3 = r3.booleanValue()
                r0.noInternetAccessExpected = r3
                goto L_0x0418
            L_0x0324:
                r3 = r6
                java.lang.Boolean r3 = (java.lang.Boolean) r3
                boolean r3 = r3.booleanValue()
                r0.validatedInternetAccess = r3
                goto L_0x0418
            L_0x032f:
                r3 = r6
                java.lang.String r3 = (java.lang.String) r3
                r0.defaultGwMacAddress = r3
                goto L_0x0418
            L_0x0336:
                r3 = r6
                java.util.HashMap r3 = (java.util.HashMap) r3
                r0.linkedConfigurations = r3
                goto L_0x0418
            L_0x033d:
                r3 = r6
                java.lang.String r3 = (java.lang.String) r3
                r0.providerFriendlyName = r3
                goto L_0x0418
            L_0x0344:
                r3 = r6
                java.lang.String r3 = (java.lang.String) r3
                r0.FQDN = r3
                goto L_0x0418
            L_0x034b:
                r3 = r6
                java.lang.Integer r3 = (java.lang.Integer) r3
                int r3 = r3.intValue()
                if (r3 != 0) goto L_0x0355
                r3 = 2
            L_0x0355:
                r0.status = r3
                goto L_0x0418
            L_0x0359:
                r3 = r6
                java.lang.Boolean r3 = (java.lang.Boolean) r3
                boolean r3 = r3.booleanValue()
                r0.shared = r3
                goto L_0x0418
            L_0x0364:
                r3 = r6
                java.lang.String r3 = (java.lang.String) r3
                r0.wapiCertSel = r3
                goto L_0x0418
            L_0x036b:
                r3 = r6
                java.lang.Integer r3 = (java.lang.Integer) r3
                int r3 = r3.intValue()
                r0.wapiCertSelMode = r3
                goto L_0x0418
            L_0x0376:
                r3 = r6
                java.lang.String r3 = (java.lang.String) r3
                r0.wapiPsk = r3
                goto L_0x0418
            L_0x037d:
                r3 = r6
                java.lang.Integer r3 = (java.lang.Integer) r3
                int r3 = r3.intValue()
                r0.wapiPskType = r3
                goto L_0x0418
            L_0x0388:
                r3 = r6
                byte[] r3 = (byte[]) r3
                java.util.BitSet r4 = java.util.BitSet.valueOf(r3)
                r0.allowedSuiteBCiphers = r4
                goto L_0x0418
            L_0x0394:
                r3 = r6
                byte[] r3 = (byte[]) r3
                java.util.BitSet r4 = java.util.BitSet.valueOf(r3)
                r0.allowedGroupManagementCiphers = r4
                goto L_0x0418
            L_0x03a0:
                r3 = r6
                byte[] r3 = (byte[]) r3
                java.util.BitSet r4 = java.util.BitSet.valueOf(r3)
                r0.allowedPairwiseCiphers = r4
                goto L_0x0418
            L_0x03ac:
                r3 = r6
                byte[] r3 = (byte[]) r3
                java.util.BitSet r4 = java.util.BitSet.valueOf(r3)
                r0.allowedGroupCiphers = r4
                goto L_0x0418
            L_0x03b6:
                r3 = r6
                byte[] r3 = (byte[]) r3
                java.util.BitSet r4 = java.util.BitSet.valueOf(r3)
                r0.allowedAuthAlgorithms = r4
                goto L_0x0418
            L_0x03c0:
                r3 = r6
                byte[] r3 = (byte[]) r3
                java.util.BitSet r4 = java.util.BitSet.valueOf(r3)
                r0.allowedProtocols = r4
                goto L_0x0418
            L_0x03ca:
                r3 = r6
                byte[] r3 = (byte[]) r3
                java.util.BitSet r4 = java.util.BitSet.valueOf(r3)
                r0.allowedKeyManagement = r4
                goto L_0x0418
            L_0x03d4:
                r3 = r6
                java.lang.Boolean r3 = (java.lang.Boolean) r3
                boolean r3 = r3.booleanValue()
                r0.requirePMF = r3
                goto L_0x0418
            L_0x03de:
                r3 = r6
                java.lang.Boolean r3 = (java.lang.Boolean) r3
                boolean r3 = r3.booleanValue()
                r0.hiddenSSID = r3
                goto L_0x0418
            L_0x03e8:
                r3 = r6
                java.lang.Integer r3 = (java.lang.Integer) r3
                int r3 = r3.intValue()
                r0.wepTxKeyIndex = r3
                goto L_0x0418
            L_0x03f2:
                java.lang.String[] r3 = r0.wepKeys
                populateWepKeysFromXmlValue(r6, r3)
                goto L_0x0418
            L_0x03f8:
                r3 = r6
                java.lang.String r3 = (java.lang.String) r3
                r0.preSharedKey = r3
                goto L_0x0418
            L_0x03fe:
                r3 = r6
                java.lang.Boolean r3 = (java.lang.Boolean) r3
                boolean r3 = r3.booleanValue()
                r0.shareThisAp = r3
                goto L_0x0418
            L_0x0408:
                r3 = r6
                java.lang.String r3 = (java.lang.String) r3
                r0.BSSID = r3
                goto L_0x0418
            L_0x040e:
                r3 = r6
                java.lang.String r3 = (java.lang.String) r3
                r0.SSID = r3
                goto L_0x0418
            L_0x0414:
                r1 = r6
                java.lang.String r1 = (java.lang.String) r1
            L_0x0418:
                goto L_0x0007
            L_0x041a:
                org.xmlpull.v1.XmlPullParserException r3 = new org.xmlpull.v1.XmlPullParserException
                java.lang.String r4 = "Missing value name"
                r3.<init>(r4)
                throw r3
            L_0x0422:
                if (r2 != 0) goto L_0x0426
                r0.macRandomizationSetting = r4
            L_0x0426:
                android.util.Pair r3 = android.util.Pair.create(r1, r0)
                return r3
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.server.wifi.util.XmlUtil.WifiConfigurationXmlUtil.parseFromXml(org.xmlpull.v1.XmlPullParser, int):android.util.Pair");
        }
    }

    public static class IpConfigurationXmlUtil {
        public static final String XML_TAG_DNS_SERVER_ADDRESSES = "DNSServers";
        public static final String XML_TAG_GATEWAY_ADDRESS = "GatewayAddress";
        public static final String XML_TAG_IP_ASSIGNMENT = "IpAssignment";
        public static final String XML_TAG_LINK_ADDRESS = "LinkAddress";
        public static final String XML_TAG_LINK_PREFIX_LENGTH = "LinkPrefixLength";
        public static final String XML_TAG_PROXY_EXCLUSION_LIST = "ProxyExclusionList";
        public static final String XML_TAG_PROXY_HOST = "ProxyHost";
        public static final String XML_TAG_PROXY_PAC_FILE = "ProxyPac";
        public static final String XML_TAG_PROXY_PORT = "ProxyPort";
        public static final String XML_TAG_PROXY_SETTINGS = "ProxySettings";

        private static void writeStaticIpConfigurationToXml(XmlSerializer out, StaticIpConfiguration staticIpConfiguration) throws XmlPullParserException, IOException {
            if (staticIpConfiguration.ipAddress != null) {
                XmlUtil.writeNextValue(out, XML_TAG_LINK_ADDRESS, staticIpConfiguration.ipAddress.getAddress().getHostAddress());
                XmlUtil.writeNextValue(out, XML_TAG_LINK_PREFIX_LENGTH, Integer.valueOf(staticIpConfiguration.ipAddress.getPrefixLength()));
            } else {
                XmlUtil.writeNextValue(out, XML_TAG_LINK_ADDRESS, (Object) null);
                XmlUtil.writeNextValue(out, XML_TAG_LINK_PREFIX_LENGTH, (Object) null);
            }
            if (staticIpConfiguration.gateway != null) {
                XmlUtil.writeNextValue(out, XML_TAG_GATEWAY_ADDRESS, staticIpConfiguration.gateway.getHostAddress());
            } else {
                XmlUtil.writeNextValue(out, XML_TAG_GATEWAY_ADDRESS, (Object) null);
            }
            if (staticIpConfiguration.dnsServers != null) {
                String[] dnsServers = new String[staticIpConfiguration.dnsServers.size()];
                int dnsServerIdx = 0;
                Iterator it = staticIpConfiguration.dnsServers.iterator();
                while (it.hasNext()) {
                    dnsServers[dnsServerIdx] = ((InetAddress) it.next()).getHostAddress();
                    dnsServerIdx++;
                }
                XmlUtil.writeNextValue(out, XML_TAG_DNS_SERVER_ADDRESSES, dnsServers);
                return;
            }
            XmlUtil.writeNextValue(out, XML_TAG_DNS_SERVER_ADDRESSES, (Object) null);
        }

        public static void writeToXml(XmlSerializer out, IpConfiguration ipConfiguration) throws XmlPullParserException, IOException {
            XmlUtil.writeNextValue(out, XML_TAG_IP_ASSIGNMENT, ipConfiguration.ipAssignment.toString());
            if (AnonymousClass1.$SwitchMap$android$net$IpConfiguration$IpAssignment[ipConfiguration.ipAssignment.ordinal()] == 1) {
                writeStaticIpConfigurationToXml(out, ipConfiguration.getStaticIpConfiguration());
            }
            XmlUtil.writeNextValue(out, XML_TAG_PROXY_SETTINGS, ipConfiguration.proxySettings.toString());
            int i = AnonymousClass1.$SwitchMap$android$net$IpConfiguration$ProxySettings[ipConfiguration.proxySettings.ordinal()];
            if (i == 1) {
                XmlUtil.writeNextValue(out, XML_TAG_PROXY_HOST, ipConfiguration.httpProxy.getHost());
                XmlUtil.writeNextValue(out, XML_TAG_PROXY_PORT, Integer.valueOf(ipConfiguration.httpProxy.getPort()));
                XmlUtil.writeNextValue(out, XML_TAG_PROXY_EXCLUSION_LIST, ipConfiguration.httpProxy.getExclusionListAsString());
            } else if (i == 2) {
                XmlUtil.writeNextValue(out, XML_TAG_PROXY_PAC_FILE, ipConfiguration.httpProxy.getPacFileUrl().toString());
            }
        }

        private static StaticIpConfiguration parseStaticIpConfigurationFromXml(XmlPullParser in) throws XmlPullParserException, IOException {
            StaticIpConfiguration staticIpConfiguration = new StaticIpConfiguration();
            String linkAddressString = (String) XmlUtil.readNextValueWithName(in, XML_TAG_LINK_ADDRESS);
            Integer linkPrefixLength = (Integer) XmlUtil.readNextValueWithName(in, XML_TAG_LINK_PREFIX_LENGTH);
            if (!(linkAddressString == null || linkPrefixLength == null)) {
                LinkAddress linkAddress = new LinkAddress(NetworkUtils.numericToInetAddress(linkAddressString), linkPrefixLength.intValue());
                if (linkAddress.getAddress() instanceof Inet4Address) {
                    staticIpConfiguration.ipAddress = linkAddress;
                } else {
                    Log.w(XmlUtil.TAG, "Non-IPv4 address: " + linkAddress);
                }
            }
            String gatewayAddressString = (String) XmlUtil.readNextValueWithName(in, XML_TAG_GATEWAY_ADDRESS);
            if (gatewayAddressString != null) {
                InetAddress gateway = NetworkUtils.numericToInetAddress(gatewayAddressString);
                RouteInfo route = new RouteInfo((LinkAddress) null, gateway);
                if (route.isIPv4Default()) {
                    staticIpConfiguration.gateway = gateway;
                } else {
                    Log.w(XmlUtil.TAG, "Non-IPv4 default route: " + route);
                }
            }
            String[] dnsServerAddressesString = (String[]) XmlUtil.readNextValueWithName(in, XML_TAG_DNS_SERVER_ADDRESSES);
            if (dnsServerAddressesString != null) {
                for (String dnsServerAddressString : dnsServerAddressesString) {
                    staticIpConfiguration.dnsServers.add(NetworkUtils.numericToInetAddress(dnsServerAddressString));
                }
            }
            return staticIpConfiguration;
        }

        public static IpConfiguration parseFromXml(XmlPullParser in, int outerTagDepth) throws XmlPullParserException, IOException {
            IpConfiguration ipConfiguration = new IpConfiguration();
            IpConfiguration.IpAssignment ipAssignment = IpConfiguration.IpAssignment.valueOf((String) XmlUtil.readNextValueWithName(in, XML_TAG_IP_ASSIGNMENT));
            ipConfiguration.setIpAssignment(ipAssignment);
            int i = AnonymousClass1.$SwitchMap$android$net$IpConfiguration$IpAssignment[ipAssignment.ordinal()];
            if (i == 1) {
                ipConfiguration.setStaticIpConfiguration(parseStaticIpConfigurationFromXml(in));
            } else if (!(i == 2 || i == 3)) {
                throw new XmlPullParserException("Unknown ip assignment type: " + ipAssignment);
            }
            IpConfiguration.ProxySettings proxySettings = IpConfiguration.ProxySettings.valueOf((String) XmlUtil.readNextValueWithName(in, XML_TAG_PROXY_SETTINGS));
            ipConfiguration.setProxySettings(proxySettings);
            int i2 = AnonymousClass1.$SwitchMap$android$net$IpConfiguration$ProxySettings[proxySettings.ordinal()];
            if (i2 == 1) {
                ipConfiguration.setHttpProxy(new ProxyInfo((String) XmlUtil.readNextValueWithName(in, XML_TAG_PROXY_HOST), ((Integer) XmlUtil.readNextValueWithName(in, XML_TAG_PROXY_PORT)).intValue(), (String) XmlUtil.readNextValueWithName(in, XML_TAG_PROXY_EXCLUSION_LIST)));
            } else if (i2 == 2) {
                ipConfiguration.setHttpProxy(new ProxyInfo((String) XmlUtil.readNextValueWithName(in, XML_TAG_PROXY_PAC_FILE)));
            } else if (!(i2 == 3 || i2 == 4)) {
                throw new XmlPullParserException("Unknown proxy settings type: " + proxySettings);
            }
            return ipConfiguration;
        }
    }

    /* renamed from: com.android.server.wifi.util.XmlUtil$1  reason: invalid class name */
    static /* synthetic */ class AnonymousClass1 {
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

    public static class NetworkSelectionStatusXmlUtil {
        public static final String XML_TAG_CONNECT_CHOICE = "ConnectChoice";
        public static final String XML_TAG_CONNECT_CHOICE_TIMESTAMP = "ConnectChoiceTimeStamp";
        public static final String XML_TAG_DISABLE_REASON = "DisableReason";
        public static final String XML_TAG_HAS_EVER_CONNECTED = "HasEverConnected";
        public static final String XML_TAG_SELECTION_STATUS = "SelectionStatus";

        public static void writeToXml(XmlSerializer out, WifiConfiguration.NetworkSelectionStatus selectionStatus) throws XmlPullParserException, IOException {
            XmlUtil.writeNextValue(out, XML_TAG_SELECTION_STATUS, selectionStatus.getNetworkStatusString());
            XmlUtil.writeNextValue(out, XML_TAG_DISABLE_REASON, selectionStatus.getNetworkDisableReasonString());
            XmlUtil.writeNextValue(out, XML_TAG_CONNECT_CHOICE, selectionStatus.getConnectChoice());
            XmlUtil.writeNextValue(out, XML_TAG_CONNECT_CHOICE_TIMESTAMP, Long.valueOf(selectionStatus.getConnectChoiceTimestamp()));
            XmlUtil.writeNextValue(out, XML_TAG_HAS_EVER_CONNECTED, Boolean.valueOf(selectionStatus.getHasEverConnected()));
        }

        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v5, resolved type: java.lang.Object} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v3, resolved type: java.lang.String} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v3, resolved type: java.lang.String} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v3, resolved type: java.lang.String} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v5, resolved type: java.lang.Long} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v9, resolved type: java.lang.Boolean} */
        /* JADX WARNING: Multi-variable type inference failed */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public static android.net.wifi.WifiConfiguration.NetworkSelectionStatus parseFromXml(org.xmlpull.v1.XmlPullParser r13, int r14) throws org.xmlpull.v1.XmlPullParserException, java.io.IOException {
            /*
                android.net.wifi.WifiConfiguration$NetworkSelectionStatus r0 = new android.net.wifi.WifiConfiguration$NetworkSelectionStatus
                r0.<init>()
                java.lang.String r1 = ""
                java.lang.String r2 = ""
            L_0x0009:
                boolean r3 = com.android.server.wifi.util.XmlUtil.isNextSectionEnd(r13, r14)
                r4 = -1
                r5 = 1
                if (r3 != 0) goto L_0x00ac
                java.lang.String[] r3 = new java.lang.String[r5]
                java.lang.Object r6 = com.android.server.wifi.util.XmlUtil.readCurrentValue(r13, r3)
                r7 = 0
                r8 = r3[r7]
                if (r8 == 0) goto L_0x00a4
                r8 = r3[r7]
                int r9 = r8.hashCode()
                r10 = 4
                r11 = 3
                r12 = 2
                switch(r9) {
                    case -1529270479: goto L_0x0051;
                    case -822052309: goto L_0x0047;
                    case -808576245: goto L_0x003d;
                    case -85195988: goto L_0x0033;
                    case 1452117118: goto L_0x0029;
                    default: goto L_0x0028;
                }
            L_0x0028:
                goto L_0x005a
            L_0x0029:
                java.lang.String r9 = "SelectionStatus"
                boolean r8 = r8.equals(r9)
                if (r8 == 0) goto L_0x0028
                r4 = r7
                goto L_0x005a
            L_0x0033:
                java.lang.String r9 = "DisableReason"
                boolean r8 = r8.equals(r9)
                if (r8 == 0) goto L_0x0028
                r4 = r5
                goto L_0x005a
            L_0x003d:
                java.lang.String r9 = "ConnectChoice"
                boolean r8 = r8.equals(r9)
                if (r8 == 0) goto L_0x0028
                r4 = r12
                goto L_0x005a
            L_0x0047:
                java.lang.String r9 = "ConnectChoiceTimeStamp"
                boolean r8 = r8.equals(r9)
                if (r8 == 0) goto L_0x0028
                r4 = r11
                goto L_0x005a
            L_0x0051:
                java.lang.String r9 = "HasEverConnected"
                boolean r8 = r8.equals(r9)
                if (r8 == 0) goto L_0x0028
                r4 = r10
            L_0x005a:
                if (r4 == 0) goto L_0x009e
                if (r4 == r5) goto L_0x009a
                if (r4 == r12) goto L_0x0093
                if (r4 == r11) goto L_0x0088
                if (r4 != r10) goto L_0x006f
                r4 = r6
                java.lang.Boolean r4 = (java.lang.Boolean) r4
                boolean r4 = r4.booleanValue()
                r0.setHasEverConnected(r4)
                goto L_0x00a2
            L_0x006f:
                org.xmlpull.v1.XmlPullParserException r4 = new org.xmlpull.v1.XmlPullParserException
                java.lang.StringBuilder r5 = new java.lang.StringBuilder
                r5.<init>()
                java.lang.String r8 = "Unknown value name found: "
                r5.append(r8)
                r7 = r3[r7]
                r5.append(r7)
                java.lang.String r5 = r5.toString()
                r4.<init>(r5)
                throw r4
            L_0x0088:
                r4 = r6
                java.lang.Long r4 = (java.lang.Long) r4
                long r4 = r4.longValue()
                r0.setConnectChoiceTimestamp(r4)
                goto L_0x00a2
            L_0x0093:
                r4 = r6
                java.lang.String r4 = (java.lang.String) r4
                r0.setConnectChoice(r4)
                goto L_0x00a2
            L_0x009a:
                r2 = r6
                java.lang.String r2 = (java.lang.String) r2
                goto L_0x00a2
            L_0x009e:
                r1 = r6
                java.lang.String r1 = (java.lang.String) r1
            L_0x00a2:
                goto L_0x0009
            L_0x00a4:
                org.xmlpull.v1.XmlPullParserException r4 = new org.xmlpull.v1.XmlPullParserException
                java.lang.String r5 = "Missing value name"
                r4.<init>(r5)
                throw r4
            L_0x00ac:
                java.lang.String[] r3 = android.net.wifi.WifiConfiguration.NetworkSelectionStatus.QUALITY_NETWORK_SELECTION_STATUS
                java.util.List r3 = java.util.Arrays.asList(r3)
                int r3 = r3.indexOf(r1)
                java.lang.String[] r6 = android.net.wifi.WifiConfiguration.NetworkSelectionStatus.QUALITY_NETWORK_SELECTION_DISABLE_REASON
                java.util.List r6 = java.util.Arrays.asList(r6)
                int r6 = r6.indexOf(r2)
                if (r3 == r4) goto L_0x00c6
                if (r6 == r4) goto L_0x00c6
                if (r3 != r5) goto L_0x00c8
            L_0x00c6:
                r3 = 0
                r6 = 0
            L_0x00c8:
                r0.setNetworkSelectionStatus(r3)
                r0.setNetworkSelectionDisableReason(r6)
                return r0
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.server.wifi.util.XmlUtil.NetworkSelectionStatusXmlUtil.parseFromXml(org.xmlpull.v1.XmlPullParser, int):android.net.wifi.WifiConfiguration$NetworkSelectionStatus");
        }
    }

    public static class WifiEnterpriseConfigXmlUtil {
        public static final String XML_TAG_ALT_SUBJECT_MATCH = "AltSubjectMatch";
        public static final String XML_TAG_ANON_IDENTITY = "AnonIdentity";
        public static final String XML_TAG_CA_CERT = "CaCert";
        public static final String XML_TAG_CA_PATH = "CaPath";
        public static final String XML_TAG_CLIENT_CERT = "ClientCert";
        public static final String XML_TAG_DOM_SUFFIX_MATCH = "DomSuffixMatch";
        public static final String XML_TAG_EAP_METHOD = "EapMethod";
        public static final String XML_TAG_EAP_SIM_NUM = "sim_num";
        public static final String XML_TAG_ENGINE = "Engine";
        public static final String XML_TAG_ENGINE_ID = "EngineId";
        public static final String XML_TAG_IDENTITY = "Identity";
        public static final String XML_TAG_PASSWORD = "Password";
        public static final String XML_TAG_PHASE2_METHOD = "Phase2Method";
        public static final String XML_TAG_PLMN = "PLMN";
        public static final String XML_TAG_PRIVATE_KEY_ID = "PrivateKeyId";
        public static final String XML_TAG_REALM = "Realm";
        public static final String XML_TAG_SIMNUM = "SimNum";
        public static final String XML_TAG_SUBJECT_MATCH = "SubjectMatch";

        public static void writeToXml(XmlSerializer out, WifiEnterpriseConfig enterpriseConfig) throws XmlPullParserException, IOException {
            XmlUtil.writeNextValue(out, XML_TAG_IDENTITY, enterpriseConfig.getFieldValue("identity"));
            XmlUtil.writeNextValue(out, XML_TAG_ANON_IDENTITY, enterpriseConfig.getFieldValue("anonymous_identity"));
            XmlUtil.writeNextValue(out, XML_TAG_PASSWORD, enterpriseConfig.getFieldValue("password"));
            XmlUtil.writeNextValue(out, XML_TAG_CLIENT_CERT, enterpriseConfig.getFieldValue(WifiBackupRestore.SupplicantBackupMigration.SUPPLICANT_KEY_CLIENT_CERT));
            XmlUtil.writeNextValue(out, XML_TAG_CA_CERT, enterpriseConfig.getFieldValue(WifiBackupRestore.SupplicantBackupMigration.SUPPLICANT_KEY_CA_CERT));
            XmlUtil.writeNextValue(out, XML_TAG_SUBJECT_MATCH, enterpriseConfig.getFieldValue("subject_match"));
            XmlUtil.writeNextValue(out, XML_TAG_ENGINE, enterpriseConfig.getFieldValue("engine"));
            XmlUtil.writeNextValue(out, XML_TAG_ENGINE_ID, enterpriseConfig.getFieldValue("engine_id"));
            XmlUtil.writeNextValue(out, XML_TAG_PRIVATE_KEY_ID, enterpriseConfig.getFieldValue("key_id"));
            XmlUtil.writeNextValue(out, XML_TAG_ALT_SUBJECT_MATCH, enterpriseConfig.getFieldValue("altsubject_match"));
            XmlUtil.writeNextValue(out, XML_TAG_DOM_SUFFIX_MATCH, enterpriseConfig.getFieldValue("domain_suffix_match"));
            XmlUtil.writeNextValue(out, XML_TAG_CA_PATH, enterpriseConfig.getFieldValue(WifiBackupRestore.SupplicantBackupMigration.SUPPLICANT_KEY_CA_PATH));
            XmlUtil.writeNextValue(out, XML_TAG_EAP_METHOD, Integer.valueOf(enterpriseConfig.getEapMethod()));
            XmlUtil.writeNextValue(out, XML_TAG_PHASE2_METHOD, Integer.valueOf(enterpriseConfig.getPhase2Method()));
            XmlUtil.writeNextValue(out, XML_TAG_EAP_SIM_NUM, enterpriseConfig.getSimNum());
            XmlUtil.writeNextValue(out, XML_TAG_PLMN, enterpriseConfig.getPlmn());
            XmlUtil.writeNextValue(out, XML_TAG_REALM, enterpriseConfig.getRealm());
            XmlUtil.writeNextValue(out, XML_TAG_SIMNUM, enterpriseConfig.getSimNum());
        }

        /* JADX WARNING: Can't fix incorrect switch cases order */
        /* JADX WARNING: Code restructure failed: missing block: B:59:0x00e6, code lost:
            if (r5.equals(XML_TAG_ANON_IDENTITY) != false) goto L_0x00ea;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public static android.net.wifi.WifiEnterpriseConfig parseFromXml(org.xmlpull.v1.XmlPullParser r8, int r9) throws org.xmlpull.v1.XmlPullParserException, java.io.IOException {
            /*
                android.net.wifi.WifiEnterpriseConfig r0 = new android.net.wifi.WifiEnterpriseConfig
                r0.<init>()
            L_0x0005:
                boolean r1 = com.android.server.wifi.util.XmlUtil.isNextSectionEnd(r8, r9)
                if (r1 != 0) goto L_0x01ce
                r1 = 1
                java.lang.String[] r2 = new java.lang.String[r1]
                java.lang.Object r3 = com.android.server.wifi.util.XmlUtil.readCurrentValue(r8, r2)
                r4 = 0
                r5 = r2[r4]
                if (r5 == 0) goto L_0x01c6
                r5 = r2[r4]
                r6 = -1
                int r7 = r5.hashCode()
                switch(r7) {
                    case -1956487222: goto L_0x00e0;
                    case -1818452145: goto L_0x00d5;
                    case -1766961550: goto L_0x00ca;
                    case -1362213863: goto L_0x00c0;
                    case -1199574865: goto L_0x00b6;
                    case -596361182: goto L_0x00ab;
                    case -386463240: goto L_0x00a0;
                    case -71117602: goto L_0x0096;
                    case 2458781: goto L_0x008b;
                    case 11048245: goto L_0x007f;
                    case 78834287: goto L_0x0073;
                    case 1146405943: goto L_0x0067;
                    case 1281629883: goto L_0x005c;
                    case 1885134621: goto L_0x0051;
                    case 2009831362: goto L_0x0046;
                    case 2010214851: goto L_0x003a;
                    case 2080171618: goto L_0x002f;
                    case 2093372446: goto L_0x0023;
                    default: goto L_0x0021;
                }
            L_0x0021:
                goto L_0x00e9
            L_0x0023:
                java.lang.String r1 = "sim_num"
                boolean r1 = r5.equals(r1)
                if (r1 == 0) goto L_0x0021
                r1 = 14
                goto L_0x00ea
            L_0x002f:
                java.lang.String r1 = "Engine"
                boolean r1 = r5.equals(r1)
                if (r1 == 0) goto L_0x0021
                r1 = 6
                goto L_0x00ea
            L_0x003a:
                java.lang.String r1 = "CaPath"
                boolean r1 = r5.equals(r1)
                if (r1 == 0) goto L_0x0021
                r1 = 11
                goto L_0x00ea
            L_0x0046:
                java.lang.String r1 = "CaCert"
                boolean r1 = r5.equals(r1)
                if (r1 == 0) goto L_0x0021
                r1 = 4
                goto L_0x00ea
            L_0x0051:
                java.lang.String r1 = "EngineId"
                boolean r1 = r5.equals(r1)
                if (r1 == 0) goto L_0x0021
                r1 = 7
                goto L_0x00ea
            L_0x005c:
                java.lang.String r1 = "Password"
                boolean r1 = r5.equals(r1)
                if (r1 == 0) goto L_0x0021
                r1 = 2
                goto L_0x00ea
            L_0x0067:
                java.lang.String r1 = "PrivateKeyId"
                boolean r1 = r5.equals(r1)
                if (r1 == 0) goto L_0x0021
                r1 = 8
                goto L_0x00ea
            L_0x0073:
                java.lang.String r1 = "Realm"
                boolean r1 = r5.equals(r1)
                if (r1 == 0) goto L_0x0021
                r1 = 16
                goto L_0x00ea
            L_0x007f:
                java.lang.String r1 = "EapMethod"
                boolean r1 = r5.equals(r1)
                if (r1 == 0) goto L_0x0021
                r1 = 12
                goto L_0x00ea
            L_0x008b:
                java.lang.String r1 = "PLMN"
                boolean r1 = r5.equals(r1)
                if (r1 == 0) goto L_0x0021
                r1 = 15
                goto L_0x00ea
            L_0x0096:
                java.lang.String r1 = "Identity"
                boolean r1 = r5.equals(r1)
                if (r1 == 0) goto L_0x0021
                r1 = r4
                goto L_0x00ea
            L_0x00a0:
                java.lang.String r1 = "Phase2Method"
                boolean r1 = r5.equals(r1)
                if (r1 == 0) goto L_0x0021
                r1 = 13
                goto L_0x00ea
            L_0x00ab:
                java.lang.String r1 = "AltSubjectMatch"
                boolean r1 = r5.equals(r1)
                if (r1 == 0) goto L_0x0021
                r1 = 9
                goto L_0x00ea
            L_0x00b6:
                java.lang.String r1 = "ClientCert"
                boolean r1 = r5.equals(r1)
                if (r1 == 0) goto L_0x0021
                r1 = 3
                goto L_0x00ea
            L_0x00c0:
                java.lang.String r1 = "SubjectMatch"
                boolean r1 = r5.equals(r1)
                if (r1 == 0) goto L_0x0021
                r1 = 5
                goto L_0x00ea
            L_0x00ca:
                java.lang.String r1 = "DomSuffixMatch"
                boolean r1 = r5.equals(r1)
                if (r1 == 0) goto L_0x0021
                r1 = 10
                goto L_0x00ea
            L_0x00d5:
                java.lang.String r1 = "SimNum"
                boolean r1 = r5.equals(r1)
                if (r1 == 0) goto L_0x0021
                r1 = 17
                goto L_0x00ea
            L_0x00e0:
                java.lang.String r7 = "AnonIdentity"
                boolean r5 = r5.equals(r7)
                if (r5 == 0) goto L_0x0021
                goto L_0x00ea
            L_0x00e9:
                r1 = r6
            L_0x00ea:
                switch(r1) {
                    case 0: goto L_0x01bb;
                    case 1: goto L_0x01b2;
                    case 2: goto L_0x01a9;
                    case 3: goto L_0x01a0;
                    case 4: goto L_0x0197;
                    case 5: goto L_0x018e;
                    case 6: goto L_0x0185;
                    case 7: goto L_0x017c;
                    case 8: goto L_0x0173;
                    case 9: goto L_0x016a;
                    case 10: goto L_0x0161;
                    case 11: goto L_0x0158;
                    case 12: goto L_0x014c;
                    case 13: goto L_0x0140;
                    case 14: goto L_0x0127;
                    case 15: goto L_0x011f;
                    case 16: goto L_0x0117;
                    case 17: goto L_0x0106;
                    default: goto L_0x00ed;
                }
            L_0x00ed:
                org.xmlpull.v1.XmlPullParserException r1 = new org.xmlpull.v1.XmlPullParserException
                java.lang.StringBuilder r5 = new java.lang.StringBuilder
                r5.<init>()
                java.lang.String r6 = "Unknown value name found: "
                r5.append(r6)
                r4 = r2[r4]
                r5.append(r4)
                java.lang.String r4 = r5.toString()
                r1.<init>(r4)
                throw r1
            L_0x0106:
                r1 = r3
                java.lang.String r1 = (java.lang.String) r1     // Catch:{ NumberFormatException -> 0x010e }
                int r1 = java.lang.Integer.parseInt(r1)     // Catch:{ NumberFormatException -> 0x010e }
                goto L_0x0110
            L_0x010e:
                r1 = move-exception
                r1 = -1
            L_0x0110:
                if (r1 <= 0) goto L_0x01c4
                r0.setSimNum(r1)
                goto L_0x01c4
            L_0x0117:
                r1 = r3
                java.lang.String r1 = (java.lang.String) r1
                r0.setRealm(r1)
                goto L_0x01c4
            L_0x011f:
                r1 = r3
                java.lang.String r1 = (java.lang.String) r1
                r0.setPlmn(r1)
                goto L_0x01c4
            L_0x0127:
                if (r3 == 0) goto L_0x01c4
                r1 = r3
                java.lang.String r1 = (java.lang.String) r1
                java.lang.String r4 = ""
                boolean r1 = r4.equals(r1)
                if (r1 != 0) goto L_0x01c4
                r1 = r3
                java.lang.String r1 = (java.lang.String) r1
                int r1 = java.lang.Integer.parseInt(r1)
                r0.setSimNum(r1)
                goto L_0x01c4
            L_0x0140:
                r1 = r3
                java.lang.Integer r1 = (java.lang.Integer) r1
                int r1 = r1.intValue()
                r0.setPhase2Method(r1)
                goto L_0x01c4
            L_0x014c:
                r1 = r3
                java.lang.Integer r1 = (java.lang.Integer) r1
                int r1 = r1.intValue()
                r0.setEapMethod(r1)
                goto L_0x01c4
            L_0x0158:
                r1 = r3
                java.lang.String r1 = (java.lang.String) r1
                java.lang.String r4 = "ca_path"
                r0.setFieldValue(r4, r1)
                goto L_0x01c4
            L_0x0161:
                r1 = r3
                java.lang.String r1 = (java.lang.String) r1
                java.lang.String r4 = "domain_suffix_match"
                r0.setFieldValue(r4, r1)
                goto L_0x01c4
            L_0x016a:
                r1 = r3
                java.lang.String r1 = (java.lang.String) r1
                java.lang.String r4 = "altsubject_match"
                r0.setFieldValue(r4, r1)
                goto L_0x01c4
            L_0x0173:
                r1 = r3
                java.lang.String r1 = (java.lang.String) r1
                java.lang.String r4 = "key_id"
                r0.setFieldValue(r4, r1)
                goto L_0x01c4
            L_0x017c:
                r1 = r3
                java.lang.String r1 = (java.lang.String) r1
                java.lang.String r4 = "engine_id"
                r0.setFieldValue(r4, r1)
                goto L_0x01c4
            L_0x0185:
                r1 = r3
                java.lang.String r1 = (java.lang.String) r1
                java.lang.String r4 = "engine"
                r0.setFieldValue(r4, r1)
                goto L_0x01c4
            L_0x018e:
                r1 = r3
                java.lang.String r1 = (java.lang.String) r1
                java.lang.String r4 = "subject_match"
                r0.setFieldValue(r4, r1)
                goto L_0x01c4
            L_0x0197:
                r1 = r3
                java.lang.String r1 = (java.lang.String) r1
                java.lang.String r4 = "ca_cert"
                r0.setFieldValue(r4, r1)
                goto L_0x01c4
            L_0x01a0:
                r1 = r3
                java.lang.String r1 = (java.lang.String) r1
                java.lang.String r4 = "client_cert"
                r0.setFieldValue(r4, r1)
                goto L_0x01c4
            L_0x01a9:
                r1 = r3
                java.lang.String r1 = (java.lang.String) r1
                java.lang.String r4 = "password"
                r0.setFieldValue(r4, r1)
                goto L_0x01c4
            L_0x01b2:
                r1 = r3
                java.lang.String r1 = (java.lang.String) r1
                java.lang.String r4 = "anonymous_identity"
                r0.setFieldValue(r4, r1)
                goto L_0x01c4
            L_0x01bb:
                r1 = r3
                java.lang.String r1 = (java.lang.String) r1
                java.lang.String r4 = "identity"
                r0.setFieldValue(r4, r1)
            L_0x01c4:
                goto L_0x0005
            L_0x01c6:
                org.xmlpull.v1.XmlPullParserException r1 = new org.xmlpull.v1.XmlPullParserException
                java.lang.String r4 = "Missing value name"
                r1.<init>(r4)
                throw r1
            L_0x01ce:
                return r0
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.server.wifi.util.XmlUtil.WifiEnterpriseConfigXmlUtil.parseFromXml(org.xmlpull.v1.XmlPullParser, int):android.net.wifi.WifiEnterpriseConfig");
        }
    }
}
