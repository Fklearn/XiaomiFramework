package com.android.server.wifi.hotspot2;

import com.android.server.wifi.SIMAccessor;
import com.android.server.wifi.WifiConfigStore;
import com.android.server.wifi.WifiKeyStore;
import com.android.server.wifi.util.XmlUtil;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

public class PasspointConfigUserStoreData implements WifiConfigStore.StoreData {
    private static final String XML_TAG_CA_CERTIFICATE_ALIAS = "CaCertificateAlias";
    private static final String XML_TAG_CA_CERTIFICATE_ALIASES = "CaCertificateAliases";
    private static final String XML_TAG_CLIENT_CERTIFICATE_ALIAS = "ClientCertificateAlias";
    private static final String XML_TAG_CLIENT_PRIVATE_KEY_ALIAS = "ClientPrivateKeyAlias";
    private static final String XML_TAG_CREATOR_UID = "CreatorUID";
    private static final String XML_TAG_HAS_EVER_CONNECTED = "HasEverConnected";
    private static final String XML_TAG_PACKAGE_NAME = "PackageName";
    private static final String XML_TAG_PROVIDER_ID = "ProviderID";
    private static final String XML_TAG_REMEDIATION_CA_CERTIFICATE_ALIAS = "RemediationCaCertificateAlias";
    private static final String XML_TAG_SECTION_HEADER_PASSPOINT_CONFIGURATION = "Configuration";
    private static final String XML_TAG_SECTION_HEADER_PASSPOINT_CONFIG_DATA = "PasspointConfigData";
    private static final String XML_TAG_SECTION_HEADER_PASSPOINT_PROVIDER = "Provider";
    private static final String XML_TAG_SECTION_HEADER_PASSPOINT_PROVIDER_LIST = "ProviderList";
    private final DataSource mDataSource;
    private final WifiKeyStore mKeyStore;
    private final SIMAccessor mSimAccessor;

    public interface DataSource {
        List<PasspointProvider> getProviders();

        void setProviders(List<PasspointProvider> list);
    }

    PasspointConfigUserStoreData(WifiKeyStore keyStore, SIMAccessor simAccessor, DataSource dataSource) {
        this.mKeyStore = keyStore;
        this.mSimAccessor = simAccessor;
        this.mDataSource = dataSource;
    }

    public void serializeData(XmlSerializer out) throws XmlPullParserException, IOException {
        serializeUserData(out);
    }

    public void deserializeData(XmlPullParser in, int outerTagDepth) throws XmlPullParserException, IOException {
        if (in != null) {
            deserializeUserData(in, outerTagDepth);
        }
    }

    public void resetData() {
        this.mDataSource.setProviders(new ArrayList());
    }

    public boolean hasNewDataToSerialize() {
        return true;
    }

    public String getName() {
        return XML_TAG_SECTION_HEADER_PASSPOINT_CONFIG_DATA;
    }

    public int getStoreFileId() {
        return 1;
    }

    private void serializeUserData(XmlSerializer out) throws XmlPullParserException, IOException {
        serializeProviderList(out, this.mDataSource.getProviders());
    }

    private void serializeProviderList(XmlSerializer out, List<PasspointProvider> providerList) throws XmlPullParserException, IOException {
        if (providerList != null) {
            XmlUtil.writeNextSectionStart(out, XML_TAG_SECTION_HEADER_PASSPOINT_PROVIDER_LIST);
            for (PasspointProvider provider : providerList) {
                if (!provider.isEphemeral()) {
                    serializeProvider(out, provider);
                }
            }
            XmlUtil.writeNextSectionEnd(out, XML_TAG_SECTION_HEADER_PASSPOINT_PROVIDER_LIST);
        }
    }

    private void serializeProvider(XmlSerializer out, PasspointProvider provider) throws XmlPullParserException, IOException {
        XmlUtil.writeNextSectionStart(out, XML_TAG_SECTION_HEADER_PASSPOINT_PROVIDER);
        XmlUtil.writeNextValue(out, XML_TAG_PROVIDER_ID, Long.valueOf(provider.getProviderId()));
        XmlUtil.writeNextValue(out, XML_TAG_CREATOR_UID, Integer.valueOf(provider.getCreatorUid()));
        if (provider.getPackageName() != null) {
            XmlUtil.writeNextValue(out, XML_TAG_PACKAGE_NAME, provider.getPackageName());
        }
        XmlUtil.writeNextValue(out, XML_TAG_CA_CERTIFICATE_ALIASES, provider.getCaCertificateAliases());
        XmlUtil.writeNextValue(out, XML_TAG_CLIENT_CERTIFICATE_ALIAS, provider.getClientCertificateAlias());
        XmlUtil.writeNextValue(out, XML_TAG_CLIENT_PRIVATE_KEY_ALIAS, provider.getClientPrivateKeyAlias());
        XmlUtil.writeNextValue(out, "HasEverConnected", Boolean.valueOf(provider.getHasEverConnected()));
        if (provider.getConfig() != null) {
            XmlUtil.writeNextSectionStart(out, XML_TAG_SECTION_HEADER_PASSPOINT_CONFIGURATION);
            PasspointXmlUtils.serializePasspointConfiguration(out, provider.getConfig());
            XmlUtil.writeNextSectionEnd(out, XML_TAG_SECTION_HEADER_PASSPOINT_CONFIGURATION);
        }
        XmlUtil.writeNextValue(out, XML_TAG_REMEDIATION_CA_CERTIFICATE_ALIAS, provider.getRemediationCaCertificateAlias());
        XmlUtil.writeNextSectionEnd(out, XML_TAG_SECTION_HEADER_PASSPOINT_PROVIDER);
    }

    private void deserializeUserData(XmlPullParser in, int outerTagDepth) throws XmlPullParserException, IOException {
        String[] headerName = new String[1];
        while (XmlUtil.gotoNextSectionOrEnd(in, headerName, outerTagDepth)) {
            String str = headerName[0];
            char c = 65535;
            if (str.hashCode() == -254992817 && str.equals(XML_TAG_SECTION_HEADER_PASSPOINT_PROVIDER_LIST)) {
                c = 0;
            }
            if (c == 0) {
                this.mDataSource.setProviders(deserializeProviderList(in, outerTagDepth + 1));
            } else {
                throw new XmlPullParserException("Unknown Passpoint user store data " + headerName[0]);
            }
        }
    }

    private List<PasspointProvider> deserializeProviderList(XmlPullParser in, int outerTagDepth) throws XmlPullParserException, IOException {
        List<PasspointProvider> providerList = new ArrayList<>();
        while (XmlUtil.gotoNextSectionWithNameOrEnd(in, XML_TAG_SECTION_HEADER_PASSPOINT_PROVIDER, outerTagDepth)) {
            providerList.add(deserializeProvider(in, outerTagDepth + 1));
        }
        return providerList;
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v2, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v10, resolved type: java.lang.Long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v12, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v3, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v5, resolved type: java.util.List} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v3, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v3, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v3, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v3, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v14, resolved type: java.lang.Boolean} */
    /* JADX WARNING: Can't fix incorrect switch cases order */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private com.android.server.wifi.hotspot2.PasspointProvider deserializeProvider(org.xmlpull.v1.XmlPullParser r30, int r31) throws org.xmlpull.v1.XmlPullParserException, java.io.IOException {
        /*
            r29 = this;
            r0 = r29
            r1 = r30
            r2 = -9223372036854775808
            r4 = -2147483648(0xffffffff80000000, float:-0.0)
            r5 = 0
            r6 = 0
            r7 = 0
            r8 = 0
            r9 = 0
            r10 = 0
            r11 = 0
            r26 = 0
            r12 = 0
            r27 = r11
            r11 = r4
            r4 = r12
        L_0x0016:
            boolean r12 = com.android.internal.util.XmlUtils.nextElementWithin(r30, r31)
            r13 = 0
            r14 = 1
            if (r12 == 0) goto L_0x00fd
            r12 = 0
            java.lang.String r15 = "name"
            java.lang.String r12 = r1.getAttributeValue(r12, r15)
            if (r12 == 0) goto L_0x00ce
            java.lang.String[] r12 = new java.lang.String[r14]
            java.lang.Object r15 = com.android.server.wifi.util.XmlUtil.readCurrentValue(r1, r12)
            r14 = r12[r13]
            r17 = -1
            int r18 = r14.hashCode()
            switch(r18) {
                case -2096352532: goto L_0x008a;
                case -1882773911: goto L_0x0080;
                case -1718339631: goto L_0x0076;
                case -1709680548: goto L_0x006c;
                case -1529270479: goto L_0x0061;
                case -922180444: goto L_0x0057;
                case -603932412: goto L_0x004d;
                case 801332119: goto L_0x0043;
                case 1281023621: goto L_0x0039;
                default: goto L_0x0038;
            }
        L_0x0038:
            goto L_0x0094
        L_0x0039:
            java.lang.String r13 = "CaCertificateAliases"
            boolean r13 = r14.equals(r13)
            if (r13 == 0) goto L_0x0038
            r13 = 3
            goto L_0x0096
        L_0x0043:
            java.lang.String r13 = "CaCertificateAlias"
            boolean r13 = r14.equals(r13)
            if (r13 == 0) goto L_0x0038
            r13 = 4
            goto L_0x0096
        L_0x004d:
            java.lang.String r13 = "ClientCertificateAlias"
            boolean r13 = r14.equals(r13)
            if (r13 == 0) goto L_0x0038
            r13 = 5
            goto L_0x0096
        L_0x0057:
            java.lang.String r13 = "CreatorUID"
            boolean r13 = r14.equals(r13)
            if (r13 == 0) goto L_0x0038
            r13 = 1
            goto L_0x0096
        L_0x0061:
            java.lang.String r13 = "HasEverConnected"
            boolean r13 = r14.equals(r13)
            if (r13 == 0) goto L_0x0038
            r13 = 8
            goto L_0x0096
        L_0x006c:
            java.lang.String r13 = "RemediationCaCertificateAlias"
            boolean r13 = r14.equals(r13)
            if (r13 == 0) goto L_0x0038
            r13 = 7
            goto L_0x0096
        L_0x0076:
            java.lang.String r13 = "PackageName"
            boolean r13 = r14.equals(r13)
            if (r13 == 0) goto L_0x0038
            r13 = 2
            goto L_0x0096
        L_0x0080:
            java.lang.String r13 = "ClientPrivateKeyAlias"
            boolean r13 = r14.equals(r13)
            if (r13 == 0) goto L_0x0038
            r13 = 6
            goto L_0x0096
        L_0x008a:
            java.lang.String r13 = "ProviderID"
            boolean r13 = r14.equals(r13)
            if (r13 == 0) goto L_0x0038
            r13 = 0
            goto L_0x0096
        L_0x0094:
            r13 = r17
        L_0x0096:
            switch(r13) {
                case 0: goto L_0x00c4;
                case 1: goto L_0x00bc;
                case 2: goto L_0x00b8;
                case 3: goto L_0x00b4;
                case 4: goto L_0x00b0;
                case 5: goto L_0x00ac;
                case 6: goto L_0x00a8;
                case 7: goto L_0x00a4;
                case 8: goto L_0x009a;
                default: goto L_0x0099;
            }
        L_0x0099:
            goto L_0x00cc
        L_0x009a:
            r13 = r15
            java.lang.Boolean r13 = (java.lang.Boolean) r13
            boolean r13 = r13.booleanValue()
            r27 = r13
            goto L_0x00cc
        L_0x00a4:
            r9 = r15
            java.lang.String r9 = (java.lang.String) r9
            goto L_0x00cc
        L_0x00a8:
            r8 = r15
            java.lang.String r8 = (java.lang.String) r8
            goto L_0x00cc
        L_0x00ac:
            r7 = r15
            java.lang.String r7 = (java.lang.String) r7
            goto L_0x00cc
        L_0x00b0:
            r6 = r15
            java.lang.String r6 = (java.lang.String) r6
            goto L_0x00cc
        L_0x00b4:
            r5 = r15
            java.util.List r5 = (java.util.List) r5
            goto L_0x00cc
        L_0x00b8:
            r10 = r15
            java.lang.String r10 = (java.lang.String) r10
            goto L_0x00cc
        L_0x00bc:
            r13 = r15
            java.lang.Integer r13 = (java.lang.Integer) r13
            int r11 = r13.intValue()
            goto L_0x00cc
        L_0x00c4:
            r13 = r15
            java.lang.Long r13 = (java.lang.Long) r13
            long r2 = r13.longValue()
        L_0x00cc:
            goto L_0x0016
        L_0x00ce:
            java.lang.String r12 = r30.getName()
            java.lang.String r13 = "Configuration"
            boolean r12 = android.text.TextUtils.equals(r12, r13)
            if (r12 == 0) goto L_0x00e2
            int r12 = r31 + 1
            android.net.wifi.hotspot2.PasspointConfiguration r4 = com.android.server.wifi.hotspot2.PasspointXmlUtils.deserializePasspointConfiguration(r1, r12)
            goto L_0x0016
        L_0x00e2:
            org.xmlpull.v1.XmlPullParserException r12 = new org.xmlpull.v1.XmlPullParserException
            java.lang.StringBuilder r13 = new java.lang.StringBuilder
            r13.<init>()
            java.lang.String r14 = "Unexpected section under Provider: "
            r13.append(r14)
            java.lang.String r14 = r30.getName()
            r13.append(r14)
            java.lang.String r13 = r13.toString()
            r12.<init>(r13)
            throw r12
        L_0x00fd:
            r12 = -9223372036854775808
            int r12 = (r2 > r12 ? 1 : (r2 == r12 ? 0 : -1))
            if (r12 == 0) goto L_0x0145
            if (r5 == 0) goto L_0x0110
            if (r6 != 0) goto L_0x0108
            goto L_0x0110
        L_0x0108:
            org.xmlpull.v1.XmlPullParserException r12 = new org.xmlpull.v1.XmlPullParserException
            java.lang.String r13 = "Should not have valid entry for caCertificateAliases and caCertificateAlias at the same time"
            r12.<init>(r13)
            throw r12
        L_0x0110:
            if (r6 == 0) goto L_0x011c
            r12 = 1
            java.lang.String[] r12 = new java.lang.String[r12]
            r13 = 0
            r12[r13] = r6
            java.util.List r5 = java.util.Arrays.asList(r12)
        L_0x011c:
            if (r4 == 0) goto L_0x013d
            com.android.server.wifi.hotspot2.PasspointProvider r28 = new com.android.server.wifi.hotspot2.PasspointProvider
            com.android.server.wifi.WifiKeyStore r14 = r0.mKeyStore
            com.android.server.wifi.SIMAccessor r15 = r0.mSimAccessor
            r12 = r28
            r13 = r4
            r16 = r2
            r18 = r11
            r19 = r10
            r20 = r5
            r21 = r7
            r22 = r8
            r23 = r9
            r24 = r27
            r25 = r26
            r12.<init>(r13, r14, r15, r16, r18, r19, r20, r21, r22, r23, r24, r25)
            return r28
        L_0x013d:
            org.xmlpull.v1.XmlPullParserException r12 = new org.xmlpull.v1.XmlPullParserException
            java.lang.String r13 = "Missing Passpoint configuration"
            r12.<init>(r13)
            throw r12
        L_0x0145:
            org.xmlpull.v1.XmlPullParserException r12 = new org.xmlpull.v1.XmlPullParserException
            java.lang.String r13 = "Missing provider ID"
            r12.<init>(r13)
            throw r12
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.wifi.hotspot2.PasspointConfigUserStoreData.deserializeProvider(org.xmlpull.v1.XmlPullParser, int):com.android.server.wifi.hotspot2.PasspointProvider");
    }
}
