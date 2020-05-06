package com.android.server.wifi.hotspot2;

import android.net.wifi.hotspot2.PasspointConfiguration;
import android.net.wifi.hotspot2.pps.Credential;
import android.net.wifi.hotspot2.pps.HomeSp;
import android.net.wifi.hotspot2.pps.Policy;
import android.net.wifi.hotspot2.pps.UpdateParameter;
import com.android.internal.util.XmlUtils;
import com.android.server.wifi.util.XmlUtil;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

public class PasspointXmlUtils {
    private static final String XML_TAG_ABLE_TO_SHARE = "AbleToShare";
    private static final String XML_TAG_CERT_SHA256_FINGERPRINT = "CertSHA256Fingerprint";
    private static final String XML_TAG_CERT_TYPE = "CertType";
    private static final String XML_TAG_CHECK_AAA_SERVER_CERT_STATUS = "CheckAAAServerCertStatus";
    private static final String XML_TAG_COUNTRIES = "Countries";
    private static final String XML_TAG_CREATION_TIME = "CreationTime";
    private static final String XML_TAG_CREDENTIAL_PRIORITY = "CredentialPriority";
    private static final String XML_TAG_EAP_TYPE = "EAPType";
    private static final String XML_TAG_EXCLUDED_SSID_LIST = "ExcludedSSIDList";
    private static final String XML_TAG_EXPIRATION_TIME = "ExpirationTime";
    private static final String XML_TAG_FQDN = "FQDN";
    private static final String XML_TAG_FQDN_EXACT_MATCH = "FQDNExactMatch";
    private static final String XML_TAG_FRIENDLY_NAME = "FriendlyName";
    private static final String XML_TAG_FRIENDLY_NAME_LIST = "FriendlyNameList";
    private static final String XML_TAG_HOME_NETWORK_IDS = "HomeNetworkIDs";
    private static final String XML_TAG_ICON_URL = "IconURL";
    private static final String XML_TAG_IMSI = "IMSI";
    private static final String XML_TAG_MACHINE_MANAGED = "MachineManaged";
    private static final String XML_TAG_MATCH_ALL_OIS = "MatchAllOIs";
    private static final String XML_TAG_MATCH_ANY_OIS = "MatchAnyOIs";
    private static final String XML_TAG_MAXIMUM_BSS_LOAD_VALUE = "MaximumBSSLoadValue";
    private static final String XML_TAG_MIN_HOME_DOWNLINK_BANDWIDTH = "MinHomeDownlinkBandwidth";
    private static final String XML_TAG_MIN_HOME_UPLINK_BANDWIDTH = "MinHomeUplinkBandwidth";
    private static final String XML_TAG_MIN_ROAMING_DOWNLINK_BANDWIDTH = "MinRoamingDownlinkBandwidth";
    private static final String XML_TAG_MIN_ROAMING_UPLINK_BANDWIDTH = "MinRoamingUplinkBandwidth";
    private static final String XML_TAG_NON_EAP_INNER_METHOD = "NonEAPInnerMethod";
    private static final String XML_TAG_OTHER_HOME_PARTNERS = "OtherHomePartners";
    private static final String XML_TAG_PASSWORD = "Password";
    private static final String XML_TAG_PORTS = "Ports";
    private static final String XML_TAG_PRIORITY = "Priority";
    private static final String XML_TAG_PROTO = "Proto";
    private static final String XML_TAG_REALM = "Realm";
    private static final String XML_TAG_RESTRICTION = "Restriction";
    private static final String XML_TAG_ROAMING_CONSORTIUM_OIS = "RoamingConsortiumOIs";
    private static final String XML_TAG_SECTION_HEADER_CERT_CREDENTIAL = "CertCredential";
    private static final String XML_TAG_SECTION_HEADER_CREDENTIAL = "Credential";
    private static final String XML_TAG_SECTION_HEADER_HOMESP = "HomeSP";
    private static final String XML_TAG_SECTION_HEADER_POLICY = "Policy";
    private static final String XML_TAG_SECTION_HEADER_POLICY_UPDATE = "PolicyUpdate";
    private static final String XML_TAG_SECTION_HEADER_PREFERRED_ROAMING_PARTNER_LIST = "RoamingPartnerList";
    private static final String XML_TAG_SECTION_HEADER_PROTO_PORT = "ProtoPort";
    private static final String XML_TAG_SECTION_HEADER_REQUIRED_PROTO_PORT_MAP = "RequiredProtoPortMap";
    private static final String XML_TAG_SECTION_HEADER_ROAMING_PARTNER = "RoamingPartner";
    private static final String XML_TAG_SECTION_HEADER_SIM_CREDENTIAL = "SimCredential";
    private static final String XML_TAG_SECTION_HEADER_SUBSCRIPTION_UPDATE = "SubscriptionUpdate";
    private static final String XML_TAG_SECTION_HEADER_USER_CREDENTIAL = "UserCredential";
    private static final String XML_TAG_SERVER_URI = "ServerURI";
    private static final String XML_TAG_SOFT_TOKEN_APP = "SoftTokenApp";
    private static final String XML_TAG_SUBSCRIPTION_CREATION_TIME = "SubscriptionCreationTime";
    private static final String XML_TAG_SUBSCRIPTION_EXPIRATION_TIME = "SubscriptionExpirationTime";
    private static final String XML_TAG_SUBSCRIPTION_TYPE = "SubscriptionType";
    private static final String XML_TAG_TRUST_ROOT_CERT_LIST = "TrustRootCertList";
    private static final String XML_TAG_TRUST_ROOT_CERT_SHA256_FINGERPRINT = "TrustRootCertSHA256Fingerprint";
    private static final String XML_TAG_TRUST_ROOT_CERT_URL = "TrustRootCertURL";
    private static final String XML_TAG_UPDATE_IDENTIFIER = "UpdateIdentifier";
    private static final String XML_TAG_UPDATE_INTERVAL = "UpdateInterval";
    private static final String XML_TAG_UPDATE_METHOD = "UpdateMethod";
    private static final String XML_TAG_USAGE_LIMIT_DATA_LIMIT = "UsageLimitDataLimit";
    private static final String XML_TAG_USAGE_LIMIT_START_TIME = "UsageLimitStartTime";
    private static final String XML_TAG_USAGE_LIMIT_TIME_LIMIT = "UsageLimitTimeLimit";
    private static final String XML_TAG_USAGE_LIMIT_TIME_PERIOD = "UsageLimitTimePeriod";
    private static final String XML_TAG_USERNAME = "Username";

    public static void serializePasspointConfiguration(XmlSerializer out, PasspointConfiguration config) throws XmlPullParserException, IOException {
        XmlUtil.writeNextValue(out, XML_TAG_UPDATE_IDENTIFIER, Integer.valueOf(config.getUpdateIdentifier()));
        XmlUtil.writeNextValue(out, XML_TAG_CREDENTIAL_PRIORITY, Integer.valueOf(config.getCredentialPriority()));
        XmlUtil.writeNextValue(out, XML_TAG_TRUST_ROOT_CERT_LIST, config.getTrustRootCertList());
        XmlUtil.writeNextValue(out, XML_TAG_SUBSCRIPTION_CREATION_TIME, Long.valueOf(config.getSubscriptionCreationTimeInMillis()));
        XmlUtil.writeNextValue(out, XML_TAG_SUBSCRIPTION_EXPIRATION_TIME, Long.valueOf(config.getSubscriptionExpirationTimeInMillis()));
        XmlUtil.writeNextValue(out, XML_TAG_SUBSCRIPTION_TYPE, config.getSubscriptionType());
        XmlUtil.writeNextValue(out, XML_TAG_USAGE_LIMIT_TIME_PERIOD, Long.valueOf(config.getUsageLimitUsageTimePeriodInMinutes()));
        XmlUtil.writeNextValue(out, XML_TAG_USAGE_LIMIT_START_TIME, Long.valueOf(config.getUsageLimitStartTimeInMillis()));
        XmlUtil.writeNextValue(out, XML_TAG_USAGE_LIMIT_DATA_LIMIT, Long.valueOf(config.getUsageLimitDataLimit()));
        XmlUtil.writeNextValue(out, XML_TAG_USAGE_LIMIT_TIME_LIMIT, Long.valueOf(config.getUsageLimitTimeLimitInMinutes()));
        serializeHomeSp(out, config.getHomeSp());
        serializeCredential(out, config.getCredential());
        serializePolicy(out, config.getPolicy());
        serializeUpdateParameter(out, XML_TAG_SECTION_HEADER_SUBSCRIPTION_UPDATE, config.getSubscriptionUpdate());
        if (config.getServiceFriendlyNames() != null) {
            XmlUtil.writeNextValue(out, XML_TAG_FRIENDLY_NAME_LIST, config.getServiceFriendlyNames());
        }
    }

    public static PasspointConfiguration deserializePasspointConfiguration(XmlPullParser in, int outerTagDepth) throws XmlPullParserException, IOException {
        PasspointConfiguration config = new PasspointConfiguration();
        while (XmlUtils.nextElementWithin(in, outerTagDepth)) {
            char c = 65535;
            if (isValueElement(in)) {
                String[] name = new String[1];
                Object value = XmlUtil.readCurrentValue(in, name);
                String str = name[0];
                switch (str.hashCode()) {
                    case -1662505799:
                        if (str.equals(XML_TAG_SUBSCRIPTION_EXPIRATION_TIME)) {
                            c = 4;
                            break;
                        }
                        break;
                    case -1463836521:
                        if (str.equals(XML_TAG_USAGE_LIMIT_DATA_LIMIT)) {
                            c = 8;
                            break;
                        }
                        break;
                    case -1358509545:
                        if (str.equals(XML_TAG_SUBSCRIPTION_TYPE)) {
                            c = 5;
                            break;
                        }
                        break;
                    case -1114457047:
                        if (str.equals(XML_TAG_SUBSCRIPTION_CREATION_TIME)) {
                            c = 3;
                            break;
                        }
                        break;
                    case -1063797932:
                        if (str.equals(XML_TAG_USAGE_LIMIT_TIME_LIMIT)) {
                            c = 9;
                            break;
                        }
                        break;
                    case 550067826:
                        if (str.equals(XML_TAG_UPDATE_IDENTIFIER)) {
                            c = 0;
                            break;
                        }
                        break;
                    case 1083081909:
                        if (str.equals(XML_TAG_USAGE_LIMIT_START_TIME)) {
                            c = 7;
                            break;
                        }
                        break;
                    case 1492973896:
                        if (str.equals(XML_TAG_USAGE_LIMIT_TIME_PERIOD)) {
                            c = 6;
                            break;
                        }
                        break;
                    case 2017737531:
                        if (str.equals(XML_TAG_CREDENTIAL_PRIORITY)) {
                            c = 1;
                            break;
                        }
                        break;
                    case 2076972980:
                        if (str.equals(XML_TAG_FRIENDLY_NAME_LIST)) {
                            c = 10;
                            break;
                        }
                        break;
                    case 2125983292:
                        if (str.equals(XML_TAG_TRUST_ROOT_CERT_LIST)) {
                            c = 2;
                            break;
                        }
                        break;
                }
                switch (c) {
                    case 0:
                        config.setUpdateIdentifier(((Integer) value).intValue());
                        break;
                    case 1:
                        config.setCredentialPriority(((Integer) value).intValue());
                        break;
                    case 2:
                        config.setTrustRootCertList((Map) value);
                        break;
                    case 3:
                        config.setSubscriptionCreationTimeInMillis(((Long) value).longValue());
                        break;
                    case 4:
                        config.setSubscriptionExpirationTimeInMillis(((Long) value).longValue());
                        break;
                    case 5:
                        config.setSubscriptionType((String) value);
                        break;
                    case 6:
                        config.setUsageLimitUsageTimePeriodInMinutes(((Long) value).longValue());
                        break;
                    case 7:
                        config.setUsageLimitStartTimeInMillis(((Long) value).longValue());
                        break;
                    case 8:
                        config.setUsageLimitDataLimit(((Long) value).longValue());
                        break;
                    case 9:
                        config.setUsageLimitTimeLimitInMinutes(((Long) value).longValue());
                        break;
                    case 10:
                        config.setServiceFriendlyNames((Map) value);
                        break;
                    default:
                        throw new XmlPullParserException("Unknown value under PasspointConfiguration: " + in.getName());
                }
            } else {
                String name2 = in.getName();
                switch (name2.hashCode()) {
                    case -2127810660:
                        if (name2.equals(XML_TAG_SECTION_HEADER_HOMESP)) {
                            c = 0;
                            break;
                        }
                        break;
                    case -1898802862:
                        if (name2.equals(XML_TAG_SECTION_HEADER_POLICY)) {
                            c = 2;
                            break;
                        }
                        break;
                    case 162345062:
                        if (name2.equals(XML_TAG_SECTION_HEADER_SUBSCRIPTION_UPDATE)) {
                            c = 3;
                            break;
                        }
                        break;
                    case 1310049399:
                        if (name2.equals(XML_TAG_SECTION_HEADER_CREDENTIAL)) {
                            c = 1;
                            break;
                        }
                        break;
                }
                if (c == 0) {
                    config.setHomeSp(deserializeHomeSP(in, outerTagDepth + 1));
                } else if (c == 1) {
                    config.setCredential(deserializeCredential(in, outerTagDepth + 1));
                } else if (c == 2) {
                    config.setPolicy(deserializePolicy(in, outerTagDepth + 1));
                } else if (c == 3) {
                    config.setSubscriptionUpdate(deserializeUpdateParameter(in, outerTagDepth + 1));
                } else {
                    throw new XmlPullParserException("Unknown section under PasspointConfiguration: " + in.getName());
                }
            }
        }
        return config;
    }

    private static void serializeHomeSp(XmlSerializer out, HomeSp homeSp) throws XmlPullParserException, IOException {
        if (homeSp != null) {
            XmlUtil.writeNextSectionStart(out, XML_TAG_SECTION_HEADER_HOMESP);
            XmlUtil.writeNextValue(out, "FQDN", homeSp.getFqdn());
            XmlUtil.writeNextValue(out, XML_TAG_FRIENDLY_NAME, homeSp.getFriendlyName());
            XmlUtil.writeNextValue(out, XML_TAG_ICON_URL, homeSp.getIconUrl());
            XmlUtil.writeNextValue(out, XML_TAG_HOME_NETWORK_IDS, homeSp.getHomeNetworkIds());
            XmlUtil.writeNextValue(out, XML_TAG_MATCH_ALL_OIS, homeSp.getMatchAllOis());
            XmlUtil.writeNextValue(out, XML_TAG_MATCH_ANY_OIS, homeSp.getMatchAnyOis());
            XmlUtil.writeNextValue(out, XML_TAG_OTHER_HOME_PARTNERS, homeSp.getOtherHomePartners());
            XmlUtil.writeNextValue(out, "RoamingConsortiumOIs", homeSp.getRoamingConsortiumOis());
            XmlUtil.writeNextSectionEnd(out, XML_TAG_SECTION_HEADER_HOMESP);
        }
    }

    private static void serializeCredential(XmlSerializer out, Credential credential) throws XmlPullParserException, IOException {
        if (credential != null) {
            XmlUtil.writeNextSectionStart(out, XML_TAG_SECTION_HEADER_CREDENTIAL);
            XmlUtil.writeNextValue(out, "CreationTime", Long.valueOf(credential.getCreationTimeInMillis()));
            XmlUtil.writeNextValue(out, XML_TAG_EXPIRATION_TIME, Long.valueOf(credential.getExpirationTimeInMillis()));
            XmlUtil.writeNextValue(out, "Realm", credential.getRealm());
            XmlUtil.writeNextValue(out, XML_TAG_CHECK_AAA_SERVER_CERT_STATUS, Boolean.valueOf(credential.getCheckAaaServerCertStatus()));
            serializeUserCredential(out, credential.getUserCredential());
            serializeCertCredential(out, credential.getCertCredential());
            serializeSimCredential(out, credential.getSimCredential());
            XmlUtil.writeNextSectionEnd(out, XML_TAG_SECTION_HEADER_CREDENTIAL);
        }
    }

    private static void serializePolicy(XmlSerializer out, Policy policy) throws XmlPullParserException, IOException {
        if (policy != null) {
            XmlUtil.writeNextSectionStart(out, XML_TAG_SECTION_HEADER_POLICY);
            XmlUtil.writeNextValue(out, XML_TAG_MIN_HOME_DOWNLINK_BANDWIDTH, Long.valueOf(policy.getMinHomeDownlinkBandwidth()));
            XmlUtil.writeNextValue(out, XML_TAG_MIN_HOME_UPLINK_BANDWIDTH, Long.valueOf(policy.getMinHomeUplinkBandwidth()));
            XmlUtil.writeNextValue(out, XML_TAG_MIN_ROAMING_DOWNLINK_BANDWIDTH, Long.valueOf(policy.getMinRoamingDownlinkBandwidth()));
            XmlUtil.writeNextValue(out, XML_TAG_MIN_ROAMING_UPLINK_BANDWIDTH, Long.valueOf(policy.getMinRoamingUplinkBandwidth()));
            XmlUtil.writeNextValue(out, XML_TAG_EXCLUDED_SSID_LIST, policy.getExcludedSsidList());
            XmlUtil.writeNextValue(out, XML_TAG_MAXIMUM_BSS_LOAD_VALUE, Integer.valueOf(policy.getMaximumBssLoadValue()));
            serializeProtoPortMap(out, policy.getRequiredProtoPortMap());
            serializeUpdateParameter(out, XML_TAG_SECTION_HEADER_POLICY_UPDATE, policy.getPolicyUpdate());
            serializePreferredRoamingPartnerList(out, policy.getPreferredRoamingPartnerList());
            XmlUtil.writeNextSectionEnd(out, XML_TAG_SECTION_HEADER_POLICY);
        }
    }

    private static void serializeUserCredential(XmlSerializer out, Credential.UserCredential userCredential) throws XmlPullParserException, IOException {
        if (userCredential != null) {
            XmlUtil.writeNextSectionStart(out, XML_TAG_SECTION_HEADER_USER_CREDENTIAL);
            XmlUtil.writeNextValue(out, XML_TAG_USERNAME, userCredential.getUsername());
            XmlUtil.writeNextValue(out, "Password", userCredential.getPassword());
            XmlUtil.writeNextValue(out, XML_TAG_MACHINE_MANAGED, Boolean.valueOf(userCredential.getMachineManaged()));
            XmlUtil.writeNextValue(out, XML_TAG_SOFT_TOKEN_APP, userCredential.getSoftTokenApp());
            XmlUtil.writeNextValue(out, XML_TAG_ABLE_TO_SHARE, Boolean.valueOf(userCredential.getAbleToShare()));
            XmlUtil.writeNextValue(out, XML_TAG_EAP_TYPE, Integer.valueOf(userCredential.getEapType()));
            XmlUtil.writeNextValue(out, XML_TAG_NON_EAP_INNER_METHOD, userCredential.getNonEapInnerMethod());
            XmlUtil.writeNextSectionEnd(out, XML_TAG_SECTION_HEADER_USER_CREDENTIAL);
        }
    }

    private static void serializeCertCredential(XmlSerializer out, Credential.CertificateCredential certCredential) throws XmlPullParserException, IOException {
        if (certCredential != null) {
            XmlUtil.writeNextSectionStart(out, XML_TAG_SECTION_HEADER_CERT_CREDENTIAL);
            XmlUtil.writeNextValue(out, XML_TAG_CERT_TYPE, certCredential.getCertType());
            XmlUtil.writeNextValue(out, XML_TAG_CERT_SHA256_FINGERPRINT, certCredential.getCertSha256Fingerprint());
            XmlUtil.writeNextSectionEnd(out, XML_TAG_SECTION_HEADER_CERT_CREDENTIAL);
        }
    }

    private static void serializeSimCredential(XmlSerializer out, Credential.SimCredential simCredential) throws XmlPullParserException, IOException {
        if (simCredential != null) {
            XmlUtil.writeNextSectionStart(out, XML_TAG_SECTION_HEADER_SIM_CREDENTIAL);
            XmlUtil.writeNextValue(out, XML_TAG_IMSI, simCredential.getImsi());
            XmlUtil.writeNextValue(out, XML_TAG_EAP_TYPE, Integer.valueOf(simCredential.getEapType()));
            XmlUtil.writeNextSectionEnd(out, XML_TAG_SECTION_HEADER_SIM_CREDENTIAL);
        }
    }

    private static void serializePreferredRoamingPartnerList(XmlSerializer out, List<Policy.RoamingPartner> preferredRoamingPartnerList) throws XmlPullParserException, IOException {
        if (preferredRoamingPartnerList != null) {
            XmlUtil.writeNextSectionStart(out, XML_TAG_SECTION_HEADER_PREFERRED_ROAMING_PARTNER_LIST);
            for (Policy.RoamingPartner partner : preferredRoamingPartnerList) {
                XmlUtil.writeNextSectionStart(out, XML_TAG_SECTION_HEADER_ROAMING_PARTNER);
                XmlUtil.writeNextValue(out, "FQDN", partner.getFqdn());
                XmlUtil.writeNextValue(out, XML_TAG_FQDN_EXACT_MATCH, Boolean.valueOf(partner.getFqdnExactMatch()));
                XmlUtil.writeNextValue(out, XML_TAG_PRIORITY, Integer.valueOf(partner.getPriority()));
                XmlUtil.writeNextValue(out, XML_TAG_COUNTRIES, partner.getCountries());
                XmlUtil.writeNextSectionEnd(out, XML_TAG_SECTION_HEADER_ROAMING_PARTNER);
            }
            XmlUtil.writeNextSectionEnd(out, XML_TAG_SECTION_HEADER_PREFERRED_ROAMING_PARTNER_LIST);
        }
    }

    private static void serializeUpdateParameter(XmlSerializer out, String type, UpdateParameter param) throws XmlPullParserException, IOException {
        if (param != null) {
            XmlUtil.writeNextSectionStart(out, type);
            XmlUtil.writeNextValue(out, XML_TAG_UPDATE_INTERVAL, Long.valueOf(param.getUpdateIntervalInMinutes()));
            XmlUtil.writeNextValue(out, XML_TAG_UPDATE_METHOD, param.getUpdateMethod());
            XmlUtil.writeNextValue(out, XML_TAG_RESTRICTION, param.getRestriction());
            XmlUtil.writeNextValue(out, XML_TAG_SERVER_URI, param.getServerUri());
            XmlUtil.writeNextValue(out, XML_TAG_USERNAME, param.getUsername());
            XmlUtil.writeNextValue(out, "Password", param.getBase64EncodedPassword());
            XmlUtil.writeNextValue(out, XML_TAG_TRUST_ROOT_CERT_URL, param.getTrustRootCertUrl());
            XmlUtil.writeNextValue(out, XML_TAG_TRUST_ROOT_CERT_SHA256_FINGERPRINT, param.getTrustRootCertSha256Fingerprint());
            XmlUtil.writeNextSectionEnd(out, type);
        }
    }

    private static void serializeProtoPortMap(XmlSerializer out, Map<Integer, String> protoPortMap) throws XmlPullParserException, IOException {
        if (protoPortMap != null) {
            XmlUtil.writeNextSectionStart(out, XML_TAG_SECTION_HEADER_REQUIRED_PROTO_PORT_MAP);
            for (Map.Entry<Integer, String> entry : protoPortMap.entrySet()) {
                XmlUtil.writeNextSectionStart(out, XML_TAG_SECTION_HEADER_PROTO_PORT);
                XmlUtil.writeNextValue(out, XML_TAG_PROTO, entry.getKey());
                XmlUtil.writeNextValue(out, XML_TAG_PORTS, entry.getValue());
                XmlUtil.writeNextSectionEnd(out, XML_TAG_SECTION_HEADER_PROTO_PORT);
            }
            XmlUtil.writeNextSectionEnd(out, XML_TAG_SECTION_HEADER_REQUIRED_PROTO_PORT_MAP);
        }
    }

    /* JADX WARNING: Can't fix incorrect switch cases order */
    /* JADX WARNING: Code restructure failed: missing block: B:14:0x003c, code lost:
        if (r5.equals(XML_TAG_FRIENDLY_NAME) != false) goto L_0x0072;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static android.net.wifi.hotspot2.pps.HomeSp deserializeHomeSP(org.xmlpull.v1.XmlPullParser r8, int r9) throws org.xmlpull.v1.XmlPullParserException, java.io.IOException {
        /*
            android.net.wifi.hotspot2.pps.HomeSp r0 = new android.net.wifi.hotspot2.pps.HomeSp
            r0.<init>()
        L_0x0005:
            boolean r1 = com.android.server.wifi.util.XmlUtil.isNextSectionEnd(r8, r9)
            if (r1 != 0) goto L_0x00d0
            r1 = 1
            java.lang.String[] r2 = new java.lang.String[r1]
            java.lang.Object r3 = com.android.server.wifi.util.XmlUtil.readCurrentValue(r8, r2)
            r4 = 0
            r5 = r2[r4]
            if (r5 == 0) goto L_0x00c8
            r5 = r2[r4]
            r6 = -1
            int r7 = r5.hashCode()
            switch(r7) {
                case -1504997731: goto L_0x0067;
                case -1502763406: goto L_0x005d;
                case -991549930: goto L_0x0053;
                case -346924001: goto L_0x0049;
                case 2165397: goto L_0x003f;
                case 626253302: goto L_0x0036;
                case 1348385833: goto L_0x002c;
                case 1956561338: goto L_0x0022;
                default: goto L_0x0021;
            }
        L_0x0021:
            goto L_0x0071
        L_0x0022:
            java.lang.String r1 = "OtherHomePartners"
            boolean r1 = r5.equals(r1)
            if (r1 == 0) goto L_0x0021
            r1 = 7
            goto L_0x0072
        L_0x002c:
            java.lang.String r1 = "HomeNetworkIDs"
            boolean r1 = r5.equals(r1)
            if (r1 == 0) goto L_0x0021
            r1 = 3
            goto L_0x0072
        L_0x0036:
            java.lang.String r7 = "FriendlyName"
            boolean r5 = r5.equals(r7)
            if (r5 == 0) goto L_0x0021
            goto L_0x0072
        L_0x003f:
            java.lang.String r1 = "FQDN"
            boolean r1 = r5.equals(r1)
            if (r1 == 0) goto L_0x0021
            r1 = r4
            goto L_0x0072
        L_0x0049:
            java.lang.String r1 = "RoamingConsortiumOIs"
            boolean r1 = r5.equals(r1)
            if (r1 == 0) goto L_0x0021
            r1 = 6
            goto L_0x0072
        L_0x0053:
            java.lang.String r1 = "IconURL"
            boolean r1 = r5.equals(r1)
            if (r1 == 0) goto L_0x0021
            r1 = 2
            goto L_0x0072
        L_0x005d:
            java.lang.String r1 = "MatchAnyOIs"
            boolean r1 = r5.equals(r1)
            if (r1 == 0) goto L_0x0021
            r1 = 5
            goto L_0x0072
        L_0x0067:
            java.lang.String r1 = "MatchAllOIs"
            boolean r1 = r5.equals(r1)
            if (r1 == 0) goto L_0x0021
            r1 = 4
            goto L_0x0072
        L_0x0071:
            r1 = r6
        L_0x0072:
            switch(r1) {
                case 0: goto L_0x00bf;
                case 1: goto L_0x00b8;
                case 2: goto L_0x00b1;
                case 3: goto L_0x00aa;
                case 4: goto L_0x00a3;
                case 5: goto L_0x009c;
                case 6: goto L_0x0095;
                case 7: goto L_0x008e;
                default: goto L_0x0075;
            }
        L_0x0075:
            org.xmlpull.v1.XmlPullParserException r1 = new org.xmlpull.v1.XmlPullParserException
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>()
            java.lang.String r6 = "Unknown data under HomeSP: "
            r5.append(r6)
            r4 = r2[r4]
            r5.append(r4)
            java.lang.String r4 = r5.toString()
            r1.<init>(r4)
            throw r1
        L_0x008e:
            r1 = r3
            java.lang.String[] r1 = (java.lang.String[]) r1
            r0.setOtherHomePartners(r1)
            goto L_0x00c6
        L_0x0095:
            r1 = r3
            long[] r1 = (long[]) r1
            r0.setRoamingConsortiumOis(r1)
            goto L_0x00c6
        L_0x009c:
            r1 = r3
            long[] r1 = (long[]) r1
            r0.setMatchAnyOis(r1)
            goto L_0x00c6
        L_0x00a3:
            r1 = r3
            long[] r1 = (long[]) r1
            r0.setMatchAllOis(r1)
            goto L_0x00c6
        L_0x00aa:
            r1 = r3
            java.util.Map r1 = (java.util.Map) r1
            r0.setHomeNetworkIds(r1)
            goto L_0x00c6
        L_0x00b1:
            r1 = r3
            java.lang.String r1 = (java.lang.String) r1
            r0.setIconUrl(r1)
            goto L_0x00c6
        L_0x00b8:
            r1 = r3
            java.lang.String r1 = (java.lang.String) r1
            r0.setFriendlyName(r1)
            goto L_0x00c6
        L_0x00bf:
            r1 = r3
            java.lang.String r1 = (java.lang.String) r1
            r0.setFqdn(r1)
        L_0x00c6:
            goto L_0x0005
        L_0x00c8:
            org.xmlpull.v1.XmlPullParserException r1 = new org.xmlpull.v1.XmlPullParserException
            java.lang.String r4 = "Missing value name"
            r1.<init>(r4)
            throw r1
        L_0x00d0:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.wifi.hotspot2.PasspointXmlUtils.deserializeHomeSP(org.xmlpull.v1.XmlPullParser, int):android.net.wifi.hotspot2.pps.HomeSp");
    }

    private static Credential deserializeCredential(XmlPullParser in, int outerTagDepth) throws XmlPullParserException, IOException {
        Credential credential = new Credential();
        while (XmlUtils.nextElementWithin(in, outerTagDepth)) {
            char c = 65535;
            if (isValueElement(in)) {
                String[] name = new String[1];
                Object value = XmlUtil.readCurrentValue(in, name);
                String str = name[0];
                switch (str.hashCode()) {
                    case -1670320580:
                        if (str.equals(XML_TAG_EXPIRATION_TIME)) {
                            c = 1;
                            break;
                        }
                        break;
                    case 78834287:
                        if (str.equals("Realm")) {
                            c = 2;
                            break;
                        }
                        break;
                    case 646045490:
                        if (str.equals(XML_TAG_CHECK_AAA_SERVER_CERT_STATUS)) {
                            c = 3;
                            break;
                        }
                        break;
                    case 1750336108:
                        if (str.equals("CreationTime")) {
                            c = 0;
                            break;
                        }
                        break;
                }
                if (c == 0) {
                    credential.setCreationTimeInMillis(((Long) value).longValue());
                } else if (c == 1) {
                    credential.setExpirationTimeInMillis(((Long) value).longValue());
                } else if (c == 2) {
                    credential.setRealm((String) value);
                } else if (c == 3) {
                    credential.setCheckAaaServerCertStatus(((Boolean) value).booleanValue());
                } else {
                    throw new XmlPullParserException("Unknown value under Credential: " + name[0]);
                }
            } else {
                String name2 = in.getName();
                int hashCode = name2.hashCode();
                if (hashCode != -930907486) {
                    if (hashCode != 802017390) {
                        if (hashCode == 1771027899 && name2.equals(XML_TAG_SECTION_HEADER_CERT_CREDENTIAL)) {
                            c = 1;
                        }
                    } else if (name2.equals(XML_TAG_SECTION_HEADER_SIM_CREDENTIAL)) {
                        c = 2;
                    }
                } else if (name2.equals(XML_TAG_SECTION_HEADER_USER_CREDENTIAL)) {
                    c = 0;
                }
                if (c == 0) {
                    credential.setUserCredential(deserializeUserCredential(in, outerTagDepth + 1));
                } else if (c == 1) {
                    credential.setCertCredential(deserializeCertCredential(in, outerTagDepth + 1));
                } else if (c == 2) {
                    credential.setSimCredential(deserializeSimCredential(in, outerTagDepth + 1));
                } else {
                    throw new XmlPullParserException("Unknown section under Credential: " + in.getName());
                }
            }
        }
        return credential;
    }

    private static Policy deserializePolicy(XmlPullParser in, int outerTagDepth) throws XmlPullParserException, IOException {
        Policy policy = new Policy();
        while (XmlUtils.nextElementWithin(in, outerTagDepth)) {
            char c = 65535;
            if (isValueElement(in)) {
                String[] name = new String[1];
                Object value = XmlUtil.readCurrentValue(in, name);
                String str = name[0];
                switch (str.hashCode()) {
                    case -166875607:
                        if (str.equals(XML_TAG_MAXIMUM_BSS_LOAD_VALUE)) {
                            c = 5;
                            break;
                        }
                        break;
                    case 228300356:
                        if (str.equals(XML_TAG_MIN_HOME_DOWNLINK_BANDWIDTH)) {
                            c = 0;
                            break;
                        }
                        break;
                    case 399544491:
                        if (str.equals(XML_TAG_MIN_HOME_UPLINK_BANDWIDTH)) {
                            c = 1;
                            break;
                        }
                        break;
                    case 831300259:
                        if (str.equals(XML_TAG_EXCLUDED_SSID_LIST)) {
                            c = 4;
                            break;
                        }
                        break;
                    case 1768378798:
                        if (str.equals(XML_TAG_MIN_ROAMING_DOWNLINK_BANDWIDTH)) {
                            c = 2;
                            break;
                        }
                        break;
                    case 1934106261:
                        if (str.equals(XML_TAG_MIN_ROAMING_UPLINK_BANDWIDTH)) {
                            c = 3;
                            break;
                        }
                        break;
                }
                if (c == 0) {
                    policy.setMinHomeDownlinkBandwidth(((Long) value).longValue());
                } else if (c == 1) {
                    policy.setMinHomeUplinkBandwidth(((Long) value).longValue());
                } else if (c == 2) {
                    policy.setMinRoamingDownlinkBandwidth(((Long) value).longValue());
                } else if (c == 3) {
                    policy.setMinRoamingUplinkBandwidth(((Long) value).longValue());
                } else if (c == 4) {
                    policy.setExcludedSsidList((String[]) value);
                } else if (c == 5) {
                    policy.setMaximumBssLoadValue(((Integer) value).intValue());
                }
            } else {
                String name2 = in.getName();
                int hashCode = name2.hashCode();
                if (hashCode != -2125460531) {
                    if (hashCode != -1710886725) {
                        if (hashCode == -285225102 && name2.equals(XML_TAG_SECTION_HEADER_REQUIRED_PROTO_PORT_MAP)) {
                            c = 0;
                        }
                    } else if (name2.equals(XML_TAG_SECTION_HEADER_POLICY_UPDATE)) {
                        c = 1;
                    }
                } else if (name2.equals(XML_TAG_SECTION_HEADER_PREFERRED_ROAMING_PARTNER_LIST)) {
                    c = 2;
                }
                if (c == 0) {
                    policy.setRequiredProtoPortMap(deserializeProtoPortMap(in, outerTagDepth + 1));
                } else if (c == 1) {
                    policy.setPolicyUpdate(deserializeUpdateParameter(in, outerTagDepth + 1));
                } else if (c == 2) {
                    policy.setPreferredRoamingPartnerList(deserializePreferredRoamingPartnerList(in, outerTagDepth + 1));
                } else {
                    throw new XmlPullParserException("Unknown section under Policy: " + in.getName());
                }
            }
        }
        return policy;
    }

    /* JADX WARNING: Can't fix incorrect switch cases order */
    /* JADX WARNING: Code restructure failed: missing block: B:11:0x0032, code lost:
        if (r5.equals("Password") != false) goto L_0x0068;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static android.net.wifi.hotspot2.pps.Credential.UserCredential deserializeUserCredential(org.xmlpull.v1.XmlPullParser r8, int r9) throws org.xmlpull.v1.XmlPullParserException, java.io.IOException {
        /*
            android.net.wifi.hotspot2.pps.Credential$UserCredential r0 = new android.net.wifi.hotspot2.pps.Credential$UserCredential
            r0.<init>()
        L_0x0005:
            boolean r1 = com.android.server.wifi.util.XmlUtil.isNextSectionEnd(r8, r9)
            if (r1 != 0) goto L_0x00cb
            r1 = 1
            java.lang.String[] r2 = new java.lang.String[r1]
            java.lang.Object r3 = com.android.server.wifi.util.XmlUtil.readCurrentValue(r8, r2)
            r4 = 0
            r5 = r2[r4]
            if (r5 == 0) goto L_0x00c3
            r5 = r2[r4]
            r6 = -1
            int r7 = r5.hashCode()
            switch(r7) {
                case -1249356658: goto L_0x005d;
                case -201069322: goto L_0x0053;
                case -123996342: goto L_0x0049;
                case 193808112: goto L_0x003f;
                case 1045832056: goto L_0x0035;
                case 1281629883: goto L_0x002c;
                case 1410776018: goto L_0x0022;
                default: goto L_0x0021;
            }
        L_0x0021:
            goto L_0x0067
        L_0x0022:
            java.lang.String r1 = "SoftTokenApp"
            boolean r1 = r5.equals(r1)
            if (r1 == 0) goto L_0x0021
            r1 = 3
            goto L_0x0068
        L_0x002c:
            java.lang.String r7 = "Password"
            boolean r5 = r5.equals(r7)
            if (r5 == 0) goto L_0x0021
            goto L_0x0068
        L_0x0035:
            java.lang.String r1 = "MachineManaged"
            boolean r1 = r5.equals(r1)
            if (r1 == 0) goto L_0x0021
            r1 = 2
            goto L_0x0068
        L_0x003f:
            java.lang.String r1 = "NonEAPInnerMethod"
            boolean r1 = r5.equals(r1)
            if (r1 == 0) goto L_0x0021
            r1 = 6
            goto L_0x0068
        L_0x0049:
            java.lang.String r1 = "AbleToShare"
            boolean r1 = r5.equals(r1)
            if (r1 == 0) goto L_0x0021
            r1 = 4
            goto L_0x0068
        L_0x0053:
            java.lang.String r1 = "Username"
            boolean r1 = r5.equals(r1)
            if (r1 == 0) goto L_0x0021
            r1 = r4
            goto L_0x0068
        L_0x005d:
            java.lang.String r1 = "EAPType"
            boolean r1 = r5.equals(r1)
            if (r1 == 0) goto L_0x0021
            r1 = 5
            goto L_0x0068
        L_0x0067:
            r1 = r6
        L_0x0068:
            switch(r1) {
                case 0: goto L_0x00ba;
                case 1: goto L_0x00b3;
                case 2: goto L_0x00a8;
                case 3: goto L_0x00a1;
                case 4: goto L_0x0096;
                case 5: goto L_0x008b;
                case 6: goto L_0x0084;
                default: goto L_0x006b;
            }
        L_0x006b:
            org.xmlpull.v1.XmlPullParserException r1 = new org.xmlpull.v1.XmlPullParserException
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>()
            java.lang.String r6 = "Unknown value under UserCredential: "
            r5.append(r6)
            r4 = r2[r4]
            r5.append(r4)
            java.lang.String r4 = r5.toString()
            r1.<init>(r4)
            throw r1
        L_0x0084:
            r1 = r3
            java.lang.String r1 = (java.lang.String) r1
            r0.setNonEapInnerMethod(r1)
            goto L_0x00c1
        L_0x008b:
            r1 = r3
            java.lang.Integer r1 = (java.lang.Integer) r1
            int r1 = r1.intValue()
            r0.setEapType(r1)
            goto L_0x00c1
        L_0x0096:
            r1 = r3
            java.lang.Boolean r1 = (java.lang.Boolean) r1
            boolean r1 = r1.booleanValue()
            r0.setAbleToShare(r1)
            goto L_0x00c1
        L_0x00a1:
            r1 = r3
            java.lang.String r1 = (java.lang.String) r1
            r0.setSoftTokenApp(r1)
            goto L_0x00c1
        L_0x00a8:
            r1 = r3
            java.lang.Boolean r1 = (java.lang.Boolean) r1
            boolean r1 = r1.booleanValue()
            r0.setMachineManaged(r1)
            goto L_0x00c1
        L_0x00b3:
            r1 = r3
            java.lang.String r1 = (java.lang.String) r1
            r0.setPassword(r1)
            goto L_0x00c1
        L_0x00ba:
            r1 = r3
            java.lang.String r1 = (java.lang.String) r1
            r0.setUsername(r1)
        L_0x00c1:
            goto L_0x0005
        L_0x00c3:
            org.xmlpull.v1.XmlPullParserException r1 = new org.xmlpull.v1.XmlPullParserException
            java.lang.String r4 = "Missing value name"
            r1.<init>(r4)
            throw r1
        L_0x00cb:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.wifi.hotspot2.PasspointXmlUtils.deserializeUserCredential(org.xmlpull.v1.XmlPullParser, int):android.net.wifi.hotspot2.pps.Credential$UserCredential");
    }

    private static Credential.CertificateCredential deserializeCertCredential(XmlPullParser in, int outerTagDepth) throws XmlPullParserException, IOException {
        Credential.CertificateCredential certCredential = new Credential.CertificateCredential();
        while (!XmlUtil.isNextSectionEnd(in, outerTagDepth)) {
            String[] valueName = new String[1];
            Object value = XmlUtil.readCurrentValue(in, valueName);
            if (valueName[0] != null) {
                String str = valueName[0];
                char c = 65535;
                int hashCode = str.hashCode();
                if (hashCode != -673759330) {
                    if (hashCode == -285451687 && str.equals(XML_TAG_CERT_SHA256_FINGERPRINT)) {
                        c = 1;
                    }
                } else if (str.equals(XML_TAG_CERT_TYPE)) {
                    c = 0;
                }
                if (c == 0) {
                    certCredential.setCertType((String) value);
                } else if (c == 1) {
                    certCredential.setCertSha256Fingerprint((byte[]) value);
                } else {
                    throw new XmlPullParserException("Unknown value under CertCredential: " + valueName[0]);
                }
            } else {
                throw new XmlPullParserException("Missing value name");
            }
        }
        return certCredential;
    }

    private static Credential.SimCredential deserializeSimCredential(XmlPullParser in, int outerTagDepth) throws XmlPullParserException, IOException {
        Credential.SimCredential simCredential = new Credential.SimCredential();
        while (!XmlUtil.isNextSectionEnd(in, outerTagDepth)) {
            String[] valueName = new String[1];
            Object value = XmlUtil.readCurrentValue(in, valueName);
            if (valueName[0] != null) {
                String str = valueName[0];
                char c = 65535;
                int hashCode = str.hashCode();
                if (hashCode != -1249356658) {
                    if (hashCode == 2251386 && str.equals(XML_TAG_IMSI)) {
                        c = 0;
                    }
                } else if (str.equals(XML_TAG_EAP_TYPE)) {
                    c = 1;
                }
                if (c == 0) {
                    simCredential.setImsi((String) value);
                } else if (c == 1) {
                    simCredential.setEapType(((Integer) value).intValue());
                } else {
                    throw new XmlPullParserException("Unknown value under CertCredential: " + valueName[0]);
                }
            } else {
                throw new XmlPullParserException("Missing value name");
            }
        }
        return simCredential;
    }

    private static List<Policy.RoamingPartner> deserializePreferredRoamingPartnerList(XmlPullParser in, int outerTagDepth) throws XmlPullParserException, IOException {
        List<Policy.RoamingPartner> roamingPartnerList = new ArrayList<>();
        while (XmlUtil.gotoNextSectionWithNameOrEnd(in, XML_TAG_SECTION_HEADER_ROAMING_PARTNER, outerTagDepth)) {
            roamingPartnerList.add(deserializeRoamingPartner(in, outerTagDepth + 1));
        }
        return roamingPartnerList;
    }

    private static Policy.RoamingPartner deserializeRoamingPartner(XmlPullParser in, int outerTagDepth) throws XmlPullParserException, IOException {
        Policy.RoamingPartner partner = new Policy.RoamingPartner();
        while (!XmlUtil.isNextSectionEnd(in, outerTagDepth)) {
            String[] valueName = new String[1];
            Object value = XmlUtil.readCurrentValue(in, valueName);
            if (valueName[0] != null) {
                String str = valueName[0];
                char c = 65535;
                switch (str.hashCode()) {
                    case -1768941957:
                        if (str.equals(XML_TAG_FQDN_EXACT_MATCH)) {
                            c = 1;
                            break;
                        }
                        break;
                    case -1100816956:
                        if (str.equals(XML_TAG_PRIORITY)) {
                            c = 2;
                            break;
                        }
                        break;
                    case -938362220:
                        if (str.equals(XML_TAG_COUNTRIES)) {
                            c = 3;
                            break;
                        }
                        break;
                    case 2165397:
                        if (str.equals("FQDN")) {
                            c = 0;
                            break;
                        }
                        break;
                }
                if (c == 0) {
                    partner.setFqdn((String) value);
                } else if (c == 1) {
                    partner.setFqdnExactMatch(((Boolean) value).booleanValue());
                } else if (c == 2) {
                    partner.setPriority(((Integer) value).intValue());
                } else if (c == 3) {
                    partner.setCountries((String) value);
                } else {
                    throw new XmlPullParserException("Unknown value under RoamingPartner: " + valueName[0]);
                }
            } else {
                throw new XmlPullParserException("Missing value name");
            }
        }
        return partner;
    }

    /* JADX WARNING: Can't fix incorrect switch cases order */
    /* JADX WARNING: Code restructure failed: missing block: B:29:0x006e, code lost:
        if (r5.equals(XML_TAG_UPDATE_METHOD) != false) goto L_0x0072;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static android.net.wifi.hotspot2.pps.UpdateParameter deserializeUpdateParameter(org.xmlpull.v1.XmlPullParser r8, int r9) throws org.xmlpull.v1.XmlPullParserException, java.io.IOException {
        /*
            android.net.wifi.hotspot2.pps.UpdateParameter r0 = new android.net.wifi.hotspot2.pps.UpdateParameter
            r0.<init>()
        L_0x0005:
            boolean r1 = com.android.server.wifi.util.XmlUtil.isNextSectionEnd(r8, r9)
            if (r1 != 0) goto L_0x00d4
            r1 = 1
            java.lang.String[] r2 = new java.lang.String[r1]
            java.lang.Object r3 = com.android.server.wifi.util.XmlUtil.readCurrentValue(r8, r2)
            r4 = 0
            r5 = r2[r4]
            if (r5 == 0) goto L_0x00cc
            r5 = r2[r4]
            r6 = -1
            int r7 = r5.hashCode()
            switch(r7) {
                case -961491158: goto L_0x0068;
                case -287007905: goto L_0x005e;
                case -201069322: goto L_0x0054;
                case 106806188: goto L_0x004a;
                case 438596814: goto L_0x0040;
                case 1281629883: goto L_0x0036;
                case 1731155985: goto L_0x002c;
                case 1806520073: goto L_0x0022;
                default: goto L_0x0021;
            }
        L_0x0021:
            goto L_0x0071
        L_0x0022:
            java.lang.String r1 = "ServerURI"
            boolean r1 = r5.equals(r1)
            if (r1 == 0) goto L_0x0021
            r1 = 3
            goto L_0x0072
        L_0x002c:
            java.lang.String r1 = "TrustRootCertURL"
            boolean r1 = r5.equals(r1)
            if (r1 == 0) goto L_0x0021
            r1 = 6
            goto L_0x0072
        L_0x0036:
            java.lang.String r1 = "Password"
            boolean r1 = r5.equals(r1)
            if (r1 == 0) goto L_0x0021
            r1 = 5
            goto L_0x0072
        L_0x0040:
            java.lang.String r1 = "UpdateInterval"
            boolean r1 = r5.equals(r1)
            if (r1 == 0) goto L_0x0021
            r1 = r4
            goto L_0x0072
        L_0x004a:
            java.lang.String r1 = "Restriction"
            boolean r1 = r5.equals(r1)
            if (r1 == 0) goto L_0x0021
            r1 = 2
            goto L_0x0072
        L_0x0054:
            java.lang.String r1 = "Username"
            boolean r1 = r5.equals(r1)
            if (r1 == 0) goto L_0x0021
            r1 = 4
            goto L_0x0072
        L_0x005e:
            java.lang.String r1 = "TrustRootCertSHA256Fingerprint"
            boolean r1 = r5.equals(r1)
            if (r1 == 0) goto L_0x0021
            r1 = 7
            goto L_0x0072
        L_0x0068:
            java.lang.String r7 = "UpdateMethod"
            boolean r5 = r5.equals(r7)
            if (r5 == 0) goto L_0x0021
            goto L_0x0072
        L_0x0071:
            r1 = r6
        L_0x0072:
            switch(r1) {
                case 0: goto L_0x00bf;
                case 1: goto L_0x00b8;
                case 2: goto L_0x00b1;
                case 3: goto L_0x00aa;
                case 4: goto L_0x00a3;
                case 5: goto L_0x009c;
                case 6: goto L_0x0095;
                case 7: goto L_0x008e;
                default: goto L_0x0075;
            }
        L_0x0075:
            org.xmlpull.v1.XmlPullParserException r1 = new org.xmlpull.v1.XmlPullParserException
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>()
            java.lang.String r6 = "Unknown value under UpdateParameter: "
            r5.append(r6)
            r4 = r2[r4]
            r5.append(r4)
            java.lang.String r4 = r5.toString()
            r1.<init>(r4)
            throw r1
        L_0x008e:
            r1 = r3
            byte[] r1 = (byte[]) r1
            r0.setTrustRootCertSha256Fingerprint(r1)
            goto L_0x00ca
        L_0x0095:
            r1 = r3
            java.lang.String r1 = (java.lang.String) r1
            r0.setTrustRootCertUrl(r1)
            goto L_0x00ca
        L_0x009c:
            r1 = r3
            java.lang.String r1 = (java.lang.String) r1
            r0.setBase64EncodedPassword(r1)
            goto L_0x00ca
        L_0x00a3:
            r1 = r3
            java.lang.String r1 = (java.lang.String) r1
            r0.setUsername(r1)
            goto L_0x00ca
        L_0x00aa:
            r1 = r3
            java.lang.String r1 = (java.lang.String) r1
            r0.setServerUri(r1)
            goto L_0x00ca
        L_0x00b1:
            r1 = r3
            java.lang.String r1 = (java.lang.String) r1
            r0.setRestriction(r1)
            goto L_0x00ca
        L_0x00b8:
            r1 = r3
            java.lang.String r1 = (java.lang.String) r1
            r0.setUpdateMethod(r1)
            goto L_0x00ca
        L_0x00bf:
            r1 = r3
            java.lang.Long r1 = (java.lang.Long) r1
            long r4 = r1.longValue()
            r0.setUpdateIntervalInMinutes(r4)
        L_0x00ca:
            goto L_0x0005
        L_0x00cc:
            org.xmlpull.v1.XmlPullParserException r1 = new org.xmlpull.v1.XmlPullParserException
            java.lang.String r4 = "Missing value name"
            r1.<init>(r4)
            throw r1
        L_0x00d4:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.wifi.hotspot2.PasspointXmlUtils.deserializeUpdateParameter(org.xmlpull.v1.XmlPullParser, int):android.net.wifi.hotspot2.pps.UpdateParameter");
    }

    private static Map<Integer, String> deserializeProtoPortMap(XmlPullParser in, int outerTagDepth) throws XmlPullParserException, IOException {
        Map<Integer, String> protoPortMap = new HashMap<>();
        while (XmlUtil.gotoNextSectionWithNameOrEnd(in, XML_TAG_SECTION_HEADER_PROTO_PORT, outerTagDepth)) {
            protoPortMap.put(Integer.valueOf(((Integer) XmlUtil.readNextValueWithName(in, XML_TAG_PROTO)).intValue()), (String) XmlUtil.readNextValueWithName(in, XML_TAG_PORTS));
        }
        return protoPortMap;
    }

    private static boolean isValueElement(XmlPullParser in) {
        return in.getAttributeValue((String) null, "name") != null;
    }
}
