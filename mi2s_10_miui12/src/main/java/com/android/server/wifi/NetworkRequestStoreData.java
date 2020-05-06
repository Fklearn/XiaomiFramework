package com.android.server.wifi;

import android.util.Log;
import com.android.server.wifi.WifiConfigStore;
import com.android.server.wifi.WifiNetworkFactory;
import com.android.server.wifi.util.XmlUtil;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

public class NetworkRequestStoreData implements WifiConfigStore.StoreData {
    private static final String TAG = "NetworkRequestStoreData";
    private static final String XML_TAG_ACCESS_POINT_BSSID = "BSSID";
    private static final String XML_TAG_ACCESS_POINT_NETWORK_TYPE = "NetworkType";
    private static final String XML_TAG_ACCESS_POINT_SSID = "SSID";
    private static final String XML_TAG_REQUESTOR_PACKAGE_NAME = "RequestorPackageName";
    private static final String XML_TAG_SECTION_HEADER_ACCESS_POINT = "AccessPoint";
    private static final String XML_TAG_SECTION_HEADER_APPROVED_ACCESS_POINTS_PER_APP = "ApprovedAccessPointsPerApp";
    private static final String XML_TAG_SECTION_HEADER_NETWORK_REQUEST_MAP = "NetworkRequestMap";
    private final DataSource mDataSource;

    public interface DataSource {
        void fromDeserialized(Map<String, Set<WifiNetworkFactory.AccessPoint>> map);

        boolean hasNewDataToSerialize();

        void reset();

        Map<String, Set<WifiNetworkFactory.AccessPoint>> toSerialize();
    }

    public NetworkRequestStoreData(DataSource dataSource) {
        this.mDataSource = dataSource;
    }

    public void serializeData(XmlSerializer out) throws XmlPullParserException, IOException {
        serializeApprovedAccessPointsMap(out, this.mDataSource.toSerialize());
    }

    public void deserializeData(XmlPullParser in, int outerTagDepth) throws XmlPullParserException, IOException {
        if (in != null) {
            this.mDataSource.fromDeserialized(parseApprovedAccessPointsMap(in, outerTagDepth));
        }
    }

    public void resetData() {
        this.mDataSource.reset();
    }

    public boolean hasNewDataToSerialize() {
        return this.mDataSource.hasNewDataToSerialize();
    }

    public String getName() {
        return XML_TAG_SECTION_HEADER_NETWORK_REQUEST_MAP;
    }

    public int getStoreFileId() {
        return 1;
    }

    private void serializeApprovedAccessPointsMap(XmlSerializer out, Map<String, Set<WifiNetworkFactory.AccessPoint>> approvedAccessPointsMap) throws XmlPullParserException, IOException {
        if (approvedAccessPointsMap != null) {
            for (Map.Entry<String, Set<WifiNetworkFactory.AccessPoint>> entry : approvedAccessPointsMap.entrySet()) {
                XmlUtil.writeNextSectionStart(out, XML_TAG_SECTION_HEADER_APPROVED_ACCESS_POINTS_PER_APP);
                XmlUtil.writeNextValue(out, XML_TAG_REQUESTOR_PACKAGE_NAME, entry.getKey());
                serializeApprovedAccessPoints(out, entry.getValue());
                XmlUtil.writeNextSectionEnd(out, XML_TAG_SECTION_HEADER_APPROVED_ACCESS_POINTS_PER_APP);
            }
        }
    }

    private void serializeApprovedAccessPoints(XmlSerializer out, Set<WifiNetworkFactory.AccessPoint> approvedAccessPoints) throws XmlPullParserException, IOException {
        for (WifiNetworkFactory.AccessPoint approvedAccessPoint : approvedAccessPoints) {
            serializeApprovedAccessPoint(out, approvedAccessPoint);
        }
    }

    private void serializeApprovedAccessPoint(XmlSerializer out, WifiNetworkFactory.AccessPoint approvedAccessPoint) throws XmlPullParserException, IOException {
        XmlUtil.writeNextSectionStart(out, XML_TAG_SECTION_HEADER_ACCESS_POINT);
        XmlUtil.writeNextValue(out, "SSID", approvedAccessPoint.ssid);
        XmlUtil.writeNextValue(out, "BSSID", approvedAccessPoint.bssid.toString());
        XmlUtil.writeNextValue(out, XML_TAG_ACCESS_POINT_NETWORK_TYPE, Integer.valueOf(approvedAccessPoint.networkType));
        XmlUtil.writeNextSectionEnd(out, XML_TAG_SECTION_HEADER_ACCESS_POINT);
    }

    private Map<String, Set<WifiNetworkFactory.AccessPoint>> parseApprovedAccessPointsMap(XmlPullParser in, int outerTagDepth) throws XmlPullParserException, IOException {
        Map<String, Set<WifiNetworkFactory.AccessPoint>> approvedAccessPointsMap = new HashMap<>();
        while (XmlUtil.gotoNextSectionWithNameOrEnd(in, XML_TAG_SECTION_HEADER_APPROVED_ACCESS_POINTS_PER_APP, outerTagDepth)) {
            try {
                approvedAccessPointsMap.put((String) XmlUtil.readNextValueWithName(in, XML_TAG_REQUESTOR_PACKAGE_NAME), parseApprovedAccessPoints(in, outerTagDepth + 1));
            } catch (RuntimeException e) {
                Log.e(TAG, "Failed to parse network suggestion. Skipping...", e);
            }
        }
        return approvedAccessPointsMap;
    }

    private Set<WifiNetworkFactory.AccessPoint> parseApprovedAccessPoints(XmlPullParser in, int outerTagDepth) throws XmlPullParserException, IOException {
        Set<WifiNetworkFactory.AccessPoint> approvedAccessPoints = new HashSet<>();
        while (XmlUtil.gotoNextSectionWithNameOrEnd(in, XML_TAG_SECTION_HEADER_ACCESS_POINT, outerTagDepth)) {
            try {
                approvedAccessPoints.add(parseApprovedAccessPoint(in, outerTagDepth + 1));
            } catch (RuntimeException e) {
                Log.e(TAG, "Failed to parse network suggestion. Skipping...", e);
            }
        }
        return approvedAccessPoints;
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v0, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v3, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v7, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v10, resolved type: java.lang.Integer} */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private com.android.server.wifi.WifiNetworkFactory.AccessPoint parseApprovedAccessPoint(org.xmlpull.v1.XmlPullParser r13, int r14) throws org.xmlpull.v1.XmlPullParserException, java.io.IOException {
        /*
            r12 = this;
            r0 = 0
            r1 = 0
            r2 = -1
        L_0x0003:
            boolean r3 = com.android.server.wifi.util.XmlUtil.isNextSectionEnd(r13, r14)
            r4 = -1
            if (r3 != 0) goto L_0x0086
            r3 = 1
            java.lang.String[] r5 = new java.lang.String[r3]
            java.lang.Object r6 = com.android.server.wifi.util.XmlUtil.readCurrentValue(r13, r5)
            r7 = 0
            r8 = r5[r7]
            if (r8 == 0) goto L_0x007e
            r8 = r5[r7]
            int r9 = r8.hashCode()
            r10 = -272744856(0xffffffffefbe3e68, float:-1.1775519E29)
            r11 = 2
            if (r9 == r10) goto L_0x0041
            r10 = 2554747(0x26fb7b, float:3.579963E-39)
            if (r9 == r10) goto L_0x0037
            r10 = 63507133(0x3c90abd, float:1.1816184E-36)
            if (r9 == r10) goto L_0x002d
        L_0x002c:
            goto L_0x004a
        L_0x002d:
            java.lang.String r9 = "BSSID"
            boolean r8 = r8.equals(r9)
            if (r8 == 0) goto L_0x002c
            r4 = r3
            goto L_0x004a
        L_0x0037:
            java.lang.String r9 = "SSID"
            boolean r8 = r8.equals(r9)
            if (r8 == 0) goto L_0x002c
            r4 = r7
            goto L_0x004a
        L_0x0041:
            java.lang.String r9 = "NetworkType"
            boolean r8 = r8.equals(r9)
            if (r8 == 0) goto L_0x002c
            r4 = r11
        L_0x004a:
            if (r4 == 0) goto L_0x0079
            if (r4 == r3) goto L_0x0071
            if (r4 != r11) goto L_0x0058
            r3 = r6
            java.lang.Integer r3 = (java.lang.Integer) r3
            int r2 = r3.intValue()
            goto L_0x007d
        L_0x0058:
            org.xmlpull.v1.XmlPullParserException r3 = new org.xmlpull.v1.XmlPullParserException
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            java.lang.String r8 = "Unknown value name found: "
            r4.append(r8)
            r7 = r5[r7]
            r4.append(r7)
            java.lang.String r4 = r4.toString()
            r3.<init>(r4)
            throw r3
        L_0x0071:
            r3 = r6
            java.lang.String r3 = (java.lang.String) r3
            android.net.MacAddress r1 = android.net.MacAddress.fromString(r3)
            goto L_0x007d
        L_0x0079:
            r0 = r6
            java.lang.String r0 = (java.lang.String) r0
        L_0x007d:
            goto L_0x0003
        L_0x007e:
            org.xmlpull.v1.XmlPullParserException r3 = new org.xmlpull.v1.XmlPullParserException
            java.lang.String r4 = "Missing value name"
            r3.<init>(r4)
            throw r3
        L_0x0086:
            if (r0 == 0) goto L_0x00a2
            if (r1 == 0) goto L_0x009a
            if (r2 == r4) goto L_0x0092
            com.android.server.wifi.WifiNetworkFactory$AccessPoint r3 = new com.android.server.wifi.WifiNetworkFactory$AccessPoint
            r3.<init>(r0, r1, r2)
            return r3
        L_0x0092:
            org.xmlpull.v1.XmlPullParserException r3 = new org.xmlpull.v1.XmlPullParserException
            java.lang.String r4 = "XML parsing of network type failed"
            r3.<init>(r4)
            throw r3
        L_0x009a:
            org.xmlpull.v1.XmlPullParserException r3 = new org.xmlpull.v1.XmlPullParserException
            java.lang.String r4 = "XML parsing of bssid failed"
            r3.<init>(r4)
            throw r3
        L_0x00a2:
            org.xmlpull.v1.XmlPullParserException r3 = new org.xmlpull.v1.XmlPullParserException
            java.lang.String r4 = "XML parsing of ssid failed"
            r3.<init>(r4)
            throw r3
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.wifi.NetworkRequestStoreData.parseApprovedAccessPoint(org.xmlpull.v1.XmlPullParser, int):com.android.server.wifi.WifiNetworkFactory$AccessPoint");
    }
}
