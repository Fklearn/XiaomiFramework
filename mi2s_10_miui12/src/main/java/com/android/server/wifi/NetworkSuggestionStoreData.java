package com.android.server.wifi;

import android.net.wifi.WifiNetworkSuggestion;
import android.util.Log;
import com.android.server.wifi.WifiConfigStore;
import com.android.server.wifi.WifiNetworkSuggestionsManager;
import com.android.server.wifi.util.XmlUtil;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

public class NetworkSuggestionStoreData implements WifiConfigStore.StoreData {
    private static final String TAG = "NetworkSuggestionStoreData";
    private static final String XML_TAG_IS_APP_INTERACTION_REQUIRED = "IsAppInteractionRequired";
    private static final String XML_TAG_IS_USER_INTERACTION_REQUIRED = "IsUserInteractionRequired";
    private static final String XML_TAG_SECTION_HEADER_NETWORK_SUGGESTION = "NetworkSuggestion";
    private static final String XML_TAG_SECTION_HEADER_NETWORK_SUGGESTION_MAP = "NetworkSuggestionMap";
    private static final String XML_TAG_SECTION_HEADER_NETWORK_SUGGESTION_PER_APP = "NetworkSuggestionPerApp";
    private static final String XML_TAG_SECTION_HEADER_WIFI_CONFIGURATION = "WifiConfiguration";
    private static final String XML_TAG_SECTION_HEADER_WIFI_ENTERPRISE_CONFIGURATION = "WifiEnterpriseConfiguration";
    private static final String XML_TAG_SUGGESTOR_HAS_USER_APPROVED = "SuggestorHasUserApproved";
    private static final String XML_TAG_SUGGESTOR_MAX_SIZE = "SuggestorMaxSize";
    private static final String XML_TAG_SUGGESTOR_PACKAGE_NAME = "SuggestorPackageName";
    private static final String XML_TAG_SUGGESTOR_UID = "SuggestorUid";
    private final DataSource mDataSource;

    public interface DataSource {
        void fromDeserialized(Map<String, WifiNetworkSuggestionsManager.PerAppInfo> map);

        boolean hasNewDataToSerialize();

        void reset();

        Map<String, WifiNetworkSuggestionsManager.PerAppInfo> toSerialize();
    }

    public NetworkSuggestionStoreData(DataSource dataSource) {
        this.mDataSource = dataSource;
    }

    public void serializeData(XmlSerializer out) throws XmlPullParserException, IOException {
        serializeNetworkSuggestionsMap(out, this.mDataSource.toSerialize());
    }

    public void deserializeData(XmlPullParser in, int outerTagDepth) throws XmlPullParserException, IOException {
        if (in != null) {
            this.mDataSource.fromDeserialized(parseNetworkSuggestionsMap(in, outerTagDepth));
        }
    }

    public void resetData() {
        this.mDataSource.reset();
    }

    public boolean hasNewDataToSerialize() {
        return this.mDataSource.hasNewDataToSerialize();
    }

    public String getName() {
        return XML_TAG_SECTION_HEADER_NETWORK_SUGGESTION_MAP;
    }

    public int getStoreFileId() {
        return 2;
    }

    private void serializeNetworkSuggestionsMap(XmlSerializer out, Map<String, WifiNetworkSuggestionsManager.PerAppInfo> networkSuggestionsMap) throws XmlPullParserException, IOException {
        if (networkSuggestionsMap != null) {
            for (Map.Entry<String, WifiNetworkSuggestionsManager.PerAppInfo> entry : networkSuggestionsMap.entrySet()) {
                boolean hasUserApproved = entry.getValue().hasUserApproved;
                int maxSize = entry.getValue().maxSize;
                Set<WifiNetworkSuggestionsManager.ExtendedWifiNetworkSuggestion> networkSuggestions = entry.getValue().extNetworkSuggestions;
                XmlUtil.writeNextSectionStart(out, XML_TAG_SECTION_HEADER_NETWORK_SUGGESTION_PER_APP);
                XmlUtil.writeNextValue(out, XML_TAG_SUGGESTOR_PACKAGE_NAME, entry.getKey());
                XmlUtil.writeNextValue(out, XML_TAG_SUGGESTOR_HAS_USER_APPROVED, Boolean.valueOf(hasUserApproved));
                XmlUtil.writeNextValue(out, XML_TAG_SUGGESTOR_MAX_SIZE, Integer.valueOf(maxSize));
                serializeExtNetworkSuggestions(out, networkSuggestions);
                XmlUtil.writeNextSectionEnd(out, XML_TAG_SECTION_HEADER_NETWORK_SUGGESTION_PER_APP);
            }
        }
    }

    private void serializeExtNetworkSuggestions(XmlSerializer out, Set<WifiNetworkSuggestionsManager.ExtendedWifiNetworkSuggestion> extNetworkSuggestions) throws XmlPullParserException, IOException {
        for (WifiNetworkSuggestionsManager.ExtendedWifiNetworkSuggestion extNetworkSuggestion : extNetworkSuggestions) {
            serializeNetworkSuggestion(out, extNetworkSuggestion.wns);
        }
    }

    private void serializeNetworkSuggestion(XmlSerializer out, WifiNetworkSuggestion suggestion) throws XmlPullParserException, IOException {
        XmlUtil.writeNextSectionStart(out, XML_TAG_SECTION_HEADER_NETWORK_SUGGESTION);
        XmlUtil.writeNextSectionStart(out, XML_TAG_SECTION_HEADER_WIFI_CONFIGURATION);
        XmlUtil.WifiConfigurationXmlUtil.writeToXmlForConfigStore(out, suggestion.wifiConfiguration);
        XmlUtil.writeNextSectionEnd(out, XML_TAG_SECTION_HEADER_WIFI_CONFIGURATION);
        if (!(suggestion.wifiConfiguration.enterpriseConfig == null || suggestion.wifiConfiguration.enterpriseConfig.getEapMethod() == -1)) {
            XmlUtil.writeNextSectionStart(out, XML_TAG_SECTION_HEADER_WIFI_ENTERPRISE_CONFIGURATION);
            XmlUtil.WifiEnterpriseConfigXmlUtil.writeToXml(out, suggestion.wifiConfiguration.enterpriseConfig);
            XmlUtil.writeNextSectionEnd(out, XML_TAG_SECTION_HEADER_WIFI_ENTERPRISE_CONFIGURATION);
        }
        XmlUtil.writeNextValue(out, XML_TAG_IS_APP_INTERACTION_REQUIRED, Boolean.valueOf(suggestion.isAppInteractionRequired));
        XmlUtil.writeNextValue(out, XML_TAG_IS_USER_INTERACTION_REQUIRED, Boolean.valueOf(suggestion.isUserInteractionRequired));
        XmlUtil.writeNextValue(out, XML_TAG_SUGGESTOR_UID, Integer.valueOf(suggestion.suggestorUid));
        XmlUtil.writeNextValue(out, XML_TAG_SUGGESTOR_PACKAGE_NAME, suggestion.suggestorPackageName);
        XmlUtil.writeNextSectionEnd(out, XML_TAG_SECTION_HEADER_NETWORK_SUGGESTION);
    }

    private Map<String, WifiNetworkSuggestionsManager.PerAppInfo> parseNetworkSuggestionsMap(XmlPullParser in, int outerTagDepth) throws XmlPullParserException, IOException {
        Map<String, WifiNetworkSuggestionsManager.PerAppInfo> networkSuggestionsMap = new HashMap<>();
        while (XmlUtil.gotoNextSectionWithNameOrEnd(in, XML_TAG_SECTION_HEADER_NETWORK_SUGGESTION_PER_APP, outerTagDepth)) {
            try {
                String packageName = (String) XmlUtil.readNextValueWithName(in, XML_TAG_SUGGESTOR_PACKAGE_NAME);
                boolean hasUserApproved = ((Boolean) XmlUtil.readNextValueWithName(in, XML_TAG_SUGGESTOR_HAS_USER_APPROVED)).booleanValue();
                int maxSize = ((Integer) XmlUtil.readNextValueWithName(in, XML_TAG_SUGGESTOR_MAX_SIZE)).intValue();
                WifiNetworkSuggestionsManager.PerAppInfo perAppInfo = new WifiNetworkSuggestionsManager.PerAppInfo(packageName);
                Set<WifiNetworkSuggestionsManager.ExtendedWifiNetworkSuggestion> extNetworkSuggestions = parseExtNetworkSuggestions(in, outerTagDepth + 1, perAppInfo);
                perAppInfo.hasUserApproved = hasUserApproved;
                perAppInfo.maxSize = maxSize;
                perAppInfo.extNetworkSuggestions.addAll(extNetworkSuggestions);
                networkSuggestionsMap.put(packageName, perAppInfo);
            } catch (RuntimeException e) {
                Log.e(TAG, "Failed to parse network suggestion. Skipping...", e);
            }
        }
        return networkSuggestionsMap;
    }

    private Set<WifiNetworkSuggestionsManager.ExtendedWifiNetworkSuggestion> parseExtNetworkSuggestions(XmlPullParser in, int outerTagDepth, WifiNetworkSuggestionsManager.PerAppInfo perAppInfo) throws XmlPullParserException, IOException {
        Set<WifiNetworkSuggestionsManager.ExtendedWifiNetworkSuggestion> extNetworkSuggestions = new HashSet<>();
        while (XmlUtil.gotoNextSectionWithNameOrEnd(in, XML_TAG_SECTION_HEADER_NETWORK_SUGGESTION, outerTagDepth)) {
            try {
                extNetworkSuggestions.add(WifiNetworkSuggestionsManager.ExtendedWifiNetworkSuggestion.fromWns(parseNetworkSuggestion(in, outerTagDepth + 1), perAppInfo));
            } catch (RuntimeException e) {
                Log.e(TAG, "Failed to parse network suggestion. Skipping...", e);
            }
        }
        return extNetworkSuggestions;
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v5, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v17, resolved type: java.lang.Boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v20, resolved type: java.lang.Boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v23, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v27, resolved type: java.lang.String} */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private android.net.wifi.WifiNetworkSuggestion parseNetworkSuggestion(org.xmlpull.v1.XmlPullParser r17, int r18) throws org.xmlpull.v1.XmlPullParserException, java.io.IOException {
        /*
            r16 = this;
            r0 = r17
            r1 = 0
            r2 = 0
            r3 = 0
            r4 = 0
            r5 = -1
            r6 = 0
            r11 = r2
            r9 = r3
            r10 = r4
            r8 = r5
            r12 = r6
        L_0x000d:
            boolean r2 = com.android.internal.util.XmlUtils.nextElementWithin(r17, r18)
            r3 = -1
            if (r2 == 0) goto L_0x010e
            r2 = 0
            java.lang.String r4 = "name"
            java.lang.String r2 = r0.getAttributeValue(r2, r4)
            r4 = 0
            r5 = 1
            if (r2 == 0) goto L_0x009a
            java.lang.String[] r2 = new java.lang.String[r5]
            java.lang.Object r6 = com.android.server.wifi.util.XmlUtil.readCurrentValue(r0, r2)
            r7 = r2[r4]
            int r13 = r7.hashCode()
            r14 = 3
            r15 = 2
            switch(r13) {
                case -1365630628: goto L_0x004f;
                case -914503382: goto L_0x0045;
                case -879551014: goto L_0x003b;
                case 129648265: goto L_0x0031;
                default: goto L_0x0030;
            }
        L_0x0030:
            goto L_0x0058
        L_0x0031:
            java.lang.String r13 = "SuggestorUid"
            boolean r7 = r7.equals(r13)
            if (r7 == 0) goto L_0x0030
            r3 = r15
            goto L_0x0058
        L_0x003b:
            java.lang.String r13 = "IsAppInteractionRequired"
            boolean r7 = r7.equals(r13)
            if (r7 == 0) goto L_0x0030
            r3 = r4
            goto L_0x0058
        L_0x0045:
            java.lang.String r13 = "SuggestorPackageName"
            boolean r7 = r7.equals(r13)
            if (r7 == 0) goto L_0x0030
            r3 = r14
            goto L_0x0058
        L_0x004f:
            java.lang.String r13 = "IsUserInteractionRequired"
            boolean r7 = r7.equals(r13)
            if (r7 == 0) goto L_0x0030
            r3 = r5
        L_0x0058:
            if (r3 == 0) goto L_0x0090
            if (r3 == r5) goto L_0x0087
            if (r3 == r15) goto L_0x007e
            if (r3 != r14) goto L_0x0065
            r3 = r6
            java.lang.String r3 = (java.lang.String) r3
            r12 = r3
            goto L_0x0098
        L_0x0065:
            org.xmlpull.v1.XmlPullParserException r3 = new org.xmlpull.v1.XmlPullParserException
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>()
            java.lang.String r7 = "Unknown value name found: "
            r5.append(r7)
            r4 = r2[r4]
            r5.append(r4)
            java.lang.String r4 = r5.toString()
            r3.<init>(r4)
            throw r3
        L_0x007e:
            r3 = r6
            java.lang.Integer r3 = (java.lang.Integer) r3
            int r3 = r3.intValue()
            r8 = r3
            goto L_0x0098
        L_0x0087:
            r3 = r6
            java.lang.Boolean r3 = (java.lang.Boolean) r3
            boolean r3 = r3.booleanValue()
            r10 = r3
            goto L_0x0098
        L_0x0090:
            r3 = r6
            java.lang.Boolean r3 = (java.lang.Boolean) r3
            boolean r3 = r3.booleanValue()
            r9 = r3
        L_0x0098:
            goto L_0x000d
        L_0x009a:
            java.lang.String r2 = r17.getName()
            if (r2 == 0) goto L_0x0106
            int r6 = r2.hashCode()
            r7 = 46473153(0x2c51fc1, float:2.8964774E-37)
            if (r6 == r7) goto L_0x00b9
            r4 = 1285464096(0x4c9ea020, float:8.316544E7)
            if (r6 == r4) goto L_0x00af
        L_0x00ae:
            goto L_0x00c2
        L_0x00af:
            java.lang.String r4 = "WifiEnterpriseConfiguration"
            boolean r4 = r2.equals(r4)
            if (r4 == 0) goto L_0x00ae
            r3 = r5
            goto L_0x00c2
        L_0x00b9:
            java.lang.String r6 = "WifiConfiguration"
            boolean r6 = r2.equals(r6)
            if (r6 == 0) goto L_0x00ae
            r3 = r4
        L_0x00c2:
            if (r3 == 0) goto L_0x00f3
            if (r3 != r5) goto L_0x00d8
            if (r11 != 0) goto L_0x00d0
            int r3 = r18 + 1
            android.net.wifi.WifiEnterpriseConfig r3 = com.android.server.wifi.util.XmlUtil.WifiEnterpriseConfigXmlUtil.parseFromXml(r0, r3)
            r11 = r3
            goto L_0x00fc
        L_0x00d0:
            org.xmlpull.v1.XmlPullParserException r3 = new org.xmlpull.v1.XmlPullParserException
            java.lang.String r4 = "Detected duplicate tag for: WifiEnterpriseConfiguration"
            r3.<init>(r4)
            throw r3
        L_0x00d8:
            org.xmlpull.v1.XmlPullParserException r3 = new org.xmlpull.v1.XmlPullParserException
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            java.lang.String r5 = "Unknown tag under NetworkSuggestion: "
            r4.append(r5)
            java.lang.String r5 = r17.getName()
            r4.append(r5)
            java.lang.String r4 = r4.toString()
            r3.<init>(r4)
            throw r3
        L_0x00f3:
            if (r1 != 0) goto L_0x00fe
            int r3 = r18 + 1
            android.util.Pair r1 = com.android.server.wifi.util.XmlUtil.WifiConfigurationXmlUtil.parseFromXml(r0, r3)
        L_0x00fc:
            goto L_0x000d
        L_0x00fe:
            org.xmlpull.v1.XmlPullParserException r3 = new org.xmlpull.v1.XmlPullParserException
            java.lang.String r4 = "Detected duplicate tag for: WifiConfiguration"
            r3.<init>(r4)
            throw r3
        L_0x0106:
            org.xmlpull.v1.XmlPullParserException r3 = new org.xmlpull.v1.XmlPullParserException
            java.lang.String r4 = "Unexpected null under NetworkSuggestion"
            r3.<init>(r4)
            throw r3
        L_0x010e:
            if (r1 == 0) goto L_0x013d
            java.lang.Object r2 = r1.second
            if (r2 == 0) goto L_0x013d
            if (r8 == r3) goto L_0x0135
            if (r12 == 0) goto L_0x012d
            java.lang.Object r2 = r1.second
            r13 = r2
            android.net.wifi.WifiConfiguration r13 = (android.net.wifi.WifiConfiguration) r13
            if (r11 == 0) goto L_0x0121
            r13.enterpriseConfig = r11
        L_0x0121:
            android.net.wifi.WifiNetworkSuggestion r14 = new android.net.wifi.WifiNetworkSuggestion
            r2 = r14
            r3 = r13
            r4 = r9
            r5 = r10
            r6 = r8
            r7 = r12
            r2.<init>(r3, r4, r5, r6, r7)
            return r14
        L_0x012d:
            org.xmlpull.v1.XmlPullParserException r2 = new org.xmlpull.v1.XmlPullParserException
            java.lang.String r3 = "XML parsing of suggestor package name failed"
            r2.<init>(r3)
            throw r2
        L_0x0135:
            org.xmlpull.v1.XmlPullParserException r2 = new org.xmlpull.v1.XmlPullParserException
            java.lang.String r3 = "XML parsing of suggestor uid failed"
            r2.<init>(r3)
            throw r2
        L_0x013d:
            org.xmlpull.v1.XmlPullParserException r2 = new org.xmlpull.v1.XmlPullParserException
            java.lang.String r3 = "XML parsing of wifi configuration failed"
            r2.<init>(r3)
            throw r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.wifi.NetworkSuggestionStoreData.parseNetworkSuggestion(org.xmlpull.v1.XmlPullParser, int):android.net.wifi.WifiNetworkSuggestion");
    }
}
