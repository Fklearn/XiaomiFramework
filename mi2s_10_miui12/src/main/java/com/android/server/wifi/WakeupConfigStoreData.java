package com.android.server.wifi;

import com.android.server.wifi.WifiConfigStore;
import com.android.server.wifi.util.XmlUtil;
import java.io.IOException;
import java.util.Collections;
import java.util.Set;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

public class WakeupConfigStoreData implements WifiConfigStore.StoreData {
    private static final String TAG = "WakeupConfigStoreData";
    private static final String XML_TAG_FEATURE_STATE_SECTION = "FeatureState";
    private static final String XML_TAG_IS_ACTIVE = "IsActive";
    private static final String XML_TAG_IS_ONBOARDED = "IsOnboarded";
    private static final String XML_TAG_NETWORK_SECTION = "Network";
    private static final String XML_TAG_NOTIFICATIONS_SHOWN = "NotificationsShown";
    private static final String XML_TAG_SECURITY = "Security";
    private static final String XML_TAG_SSID = "SSID";
    private boolean mHasBeenRead = false;
    private final DataSource<Boolean> mIsActiveDataSource;
    private final DataSource<Boolean> mIsOnboardedDataSource;
    private final DataSource<Set<ScanResultMatchInfo>> mNetworkDataSource;
    private final DataSource<Integer> mNotificationsDataSource;

    public interface DataSource<T> {
        T getData();

        void setData(T t);
    }

    public WakeupConfigStoreData(DataSource<Boolean> isActiveDataSource, DataSource<Boolean> isOnboardedDataSource, DataSource<Integer> notificationsDataSource, DataSource<Set<ScanResultMatchInfo>> networkDataSource) {
        this.mIsActiveDataSource = isActiveDataSource;
        this.mIsOnboardedDataSource = isOnboardedDataSource;
        this.mNotificationsDataSource = notificationsDataSource;
        this.mNetworkDataSource = networkDataSource;
    }

    public boolean hasBeenRead() {
        return this.mHasBeenRead;
    }

    public void serializeData(XmlSerializer out) throws XmlPullParserException, IOException {
        writeFeatureState(out);
        for (ScanResultMatchInfo scanResultMatchInfo : this.mNetworkDataSource.getData()) {
            writeNetwork(out, scanResultMatchInfo);
        }
    }

    private void writeFeatureState(XmlSerializer out) throws IOException, XmlPullParserException {
        XmlUtil.writeNextSectionStart(out, XML_TAG_FEATURE_STATE_SECTION);
        XmlUtil.writeNextValue(out, XML_TAG_IS_ACTIVE, this.mIsActiveDataSource.getData());
        XmlUtil.writeNextValue(out, XML_TAG_IS_ONBOARDED, this.mIsOnboardedDataSource.getData());
        XmlUtil.writeNextValue(out, XML_TAG_NOTIFICATIONS_SHOWN, this.mNotificationsDataSource.getData());
        XmlUtil.writeNextSectionEnd(out, XML_TAG_FEATURE_STATE_SECTION);
    }

    private void writeNetwork(XmlSerializer out, ScanResultMatchInfo scanResultMatchInfo) throws XmlPullParserException, IOException {
        XmlUtil.writeNextSectionStart(out, XML_TAG_NETWORK_SECTION);
        XmlUtil.writeNextValue(out, "SSID", scanResultMatchInfo.networkSsid);
        XmlUtil.writeNextValue(out, XML_TAG_SECURITY, Integer.valueOf(scanResultMatchInfo.networkType));
        XmlUtil.writeNextSectionEnd(out, XML_TAG_NETWORK_SECTION);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:13:0x0037, code lost:
        if (r4.equals(XML_TAG_FEATURE_STATE_SECTION) == false) goto L_0x0044;
     */
    /* JADX WARNING: Removed duplicated region for block: B:19:0x0047  */
    /* JADX WARNING: Removed duplicated region for block: B:21:0x0054  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void deserializeData(org.xmlpull.v1.XmlPullParser r9, int r10) throws org.xmlpull.v1.XmlPullParserException, java.io.IOException {
        /*
            r8 = this;
            boolean r0 = r8.mHasBeenRead
            r1 = 1
            if (r0 != 0) goto L_0x000e
            java.lang.String r0 = "WakeupConfigStoreData"
            java.lang.String r2 = "WifiWake user data has been read"
            android.util.Log.d(r0, r2)
            r8.mHasBeenRead = r1
        L_0x000e:
            if (r9 != 0) goto L_0x0011
            return
        L_0x0011:
            android.util.ArraySet r0 = new android.util.ArraySet
            r0.<init>()
            java.lang.String[] r2 = new java.lang.String[r1]
        L_0x0018:
            boolean r3 = com.android.server.wifi.util.XmlUtil.gotoNextSectionOrEnd(r9, r2, r10)
            if (r3 == 0) goto L_0x005b
            r3 = 0
            r4 = r2[r3]
            r5 = -1
            int r6 = r4.hashCode()
            r7 = -786828786(0xffffffffd119f20e, float:-4.1324438E10)
            if (r6 == r7) goto L_0x003a
            r7 = 1362433883(0x5135175b, float:4.8611308E10)
            if (r6 == r7) goto L_0x0031
        L_0x0030:
            goto L_0x0044
        L_0x0031:
            java.lang.String r6 = "FeatureState"
            boolean r4 = r4.equals(r6)
            if (r4 == 0) goto L_0x0030
            goto L_0x0045
        L_0x003a:
            java.lang.String r3 = "Network"
            boolean r3 = r4.equals(r3)
            if (r3 == 0) goto L_0x0030
            r3 = r1
            goto L_0x0045
        L_0x0044:
            r3 = r5
        L_0x0045:
            if (r3 == 0) goto L_0x0054
            if (r3 == r1) goto L_0x004a
            goto L_0x005a
        L_0x004a:
            int r3 = r10 + 1
            com.android.server.wifi.ScanResultMatchInfo r3 = r8.parseNetwork(r9, r3)
            r0.add(r3)
            goto L_0x005a
        L_0x0054:
            int r3 = r10 + 1
            r8.parseFeatureState(r9, r3)
        L_0x005a:
            goto L_0x0018
        L_0x005b:
            com.android.server.wifi.WakeupConfigStoreData$DataSource<java.util.Set<com.android.server.wifi.ScanResultMatchInfo>> r1 = r8.mNetworkDataSource
            r1.setData(r0)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.wifi.WakeupConfigStoreData.deserializeData(org.xmlpull.v1.XmlPullParser, int):void");
    }

    private void parseFeatureState(XmlPullParser in, int outerTagDepth) throws IOException, XmlPullParserException {
        boolean isActive = false;
        boolean isOnboarded = false;
        int notificationsShown = 0;
        while (!XmlUtil.isNextSectionEnd(in, outerTagDepth)) {
            String[] valueName = new String[1];
            Object value = XmlUtil.readCurrentValue(in, valueName);
            if (valueName[0] != null) {
                String str = valueName[0];
                char c = 65535;
                int hashCode = str.hashCode();
                if (hashCode != -1725092580) {
                    if (hashCode != -684272400) {
                        if (hashCode == 898665769 && str.equals(XML_TAG_NOTIFICATIONS_SHOWN)) {
                            c = 2;
                        }
                    } else if (str.equals(XML_TAG_IS_ACTIVE)) {
                        c = 0;
                    }
                } else if (str.equals(XML_TAG_IS_ONBOARDED)) {
                    c = 1;
                }
                if (c == 0) {
                    isActive = ((Boolean) value).booleanValue();
                } else if (c == 1) {
                    isOnboarded = ((Boolean) value).booleanValue();
                } else if (c == 2) {
                    notificationsShown = ((Integer) value).intValue();
                } else {
                    throw new XmlPullParserException("Unknown value found: " + valueName[0]);
                }
            } else {
                throw new XmlPullParserException("Missing value name");
            }
        }
        this.mIsActiveDataSource.setData(Boolean.valueOf(isActive));
        this.mIsOnboardedDataSource.setData(Boolean.valueOf(isOnboarded));
        this.mNotificationsDataSource.setData(Integer.valueOf(notificationsShown));
    }

    private ScanResultMatchInfo parseNetwork(XmlPullParser in, int outerTagDepth) throws IOException, XmlPullParserException {
        ScanResultMatchInfo scanResultMatchInfo = new ScanResultMatchInfo();
        while (!XmlUtil.isNextSectionEnd(in, outerTagDepth)) {
            String[] valueName = new String[1];
            Object value = XmlUtil.readCurrentValue(in, valueName);
            if (valueName[0] != null) {
                String str = valueName[0];
                char c = 65535;
                int hashCode = str.hashCode();
                if (hashCode != 2554747) {
                    if (hashCode == 1013767008 && str.equals(XML_TAG_SECURITY)) {
                        c = 1;
                    }
                } else if (str.equals("SSID")) {
                    c = 0;
                }
                if (c == 0) {
                    scanResultMatchInfo.networkSsid = (String) value;
                } else if (c == 1) {
                    scanResultMatchInfo.networkType = ((Integer) value).intValue();
                } else {
                    throw new XmlPullParserException("Unknown tag under WakeupConfigStoreData: " + valueName[0]);
                }
            } else {
                throw new XmlPullParserException("Missing value name");
            }
        }
        return scanResultMatchInfo;
    }

    public void resetData() {
        this.mNetworkDataSource.setData(Collections.emptySet());
        this.mIsActiveDataSource.setData(false);
        this.mIsOnboardedDataSource.setData(false);
        this.mNotificationsDataSource.setData(0);
    }

    public boolean hasNewDataToSerialize() {
        return true;
    }

    public String getName() {
        return TAG;
    }

    public int getStoreFileId() {
        return 1;
    }
}
