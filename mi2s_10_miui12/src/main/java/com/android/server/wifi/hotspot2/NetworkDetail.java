package com.android.server.wifi.hotspot2;

import com.android.server.wifi.hotspot2.anqp.ANQPElement;
import com.android.server.wifi.hotspot2.anqp.Constants;
import com.android.server.wifi.hotspot2.anqp.RawByteElement;
import com.android.server.wifi.util.InformationElementUtil;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.Map;
import miui.telephony.phonenumber.Prefix;

public class NetworkDetail {
    private static final boolean DBG = false;
    private static final String TAG = "NetworkDetail:";
    private final Map<Constants.ANQPElementType, ANQPElement> mANQPElements;
    private final int mAnqpDomainID;
    private final int mAnqpOICount;
    private final Ant mAnt;
    private final long mBSSID;
    private final int mCapacity;
    private final int mCenterfreq0;
    private final int mCenterfreq1;
    private final int mChannelUtilization;
    private final int mChannelWidth;
    private int mDtimInterval = -1;
    private final InformationElementUtil.ExtendedCapabilities mExtendedCapabilities;
    private final long mHESSID;
    private final HSRelease mHSRelease;
    private final boolean mInternet;
    private final boolean mIsHiddenSsid;
    private final int mMaxRate;
    private final int mPrimaryFreq;
    private final long[] mRoamingConsortiums;
    private final String mSSID;
    private final int mStationCount;
    private final int mWifiMode;

    public enum Ant {
        Private,
        PrivateWithGuest,
        ChargeablePublic,
        FreePublic,
        Personal,
        EmergencyOnly,
        Resvd6,
        Resvd7,
        Resvd8,
        Resvd9,
        Resvd10,
        Resvd11,
        Resvd12,
        Resvd13,
        TestOrExperimental,
        Wildcard
    }

    public enum HSRelease {
        R1,
        R2,
        Unknown
    }

    /* JADX WARNING: Removed duplicated region for block: B:104:0x0269  */
    /* JADX WARNING: Removed duplicated region for block: B:105:0x0277  */
    /* JADX WARNING: Removed duplicated region for block: B:57:0x0113  */
    /* JADX WARNING: Removed duplicated region for block: B:59:0x0119  */
    /* JADX WARNING: Removed duplicated region for block: B:85:0x017f  */
    /* JADX WARNING: Removed duplicated region for block: B:88:0x01c3  */
    /* JADX WARNING: Removed duplicated region for block: B:89:0x01d6  */
    /* JADX WARNING: Removed duplicated region for block: B:92:0x01ed  */
    /* JADX WARNING: Removed duplicated region for block: B:95:0x01fa  */
    /* JADX WARNING: Removed duplicated region for block: B:96:0x0215  */
    /* JADX WARNING: Removed duplicated region for block: B:99:0x021f  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public NetworkDetail(java.lang.String r26, android.net.wifi.ScanResult.InformationElement[] r27, java.util.List<java.lang.String> r28, int r29) {
        /*
            r25 = this;
            r1 = r25
            r2 = r27
            r25.<init>()
            r0 = -1
            r1.mDtimInterval = r0
            if (r2 == 0) goto L_0x0280
            long r3 = com.android.server.wifi.hotspot2.Utils.parseMac(r26)
            r1.mBSSID = r3
            r3 = 0
            r4 = 0
            r5 = 0
            com.android.server.wifi.util.InformationElementUtil$BssLoad r0 = new com.android.server.wifi.util.InformationElementUtil$BssLoad
            r0.<init>()
            r6 = r0
            com.android.server.wifi.util.InformationElementUtil$Interworking r0 = new com.android.server.wifi.util.InformationElementUtil$Interworking
            r0.<init>()
            r7 = r0
            com.android.server.wifi.util.InformationElementUtil$RoamingConsortium r0 = new com.android.server.wifi.util.InformationElementUtil$RoamingConsortium
            r0.<init>()
            r8 = r0
            com.android.server.wifi.util.InformationElementUtil$Vsa r0 = new com.android.server.wifi.util.InformationElementUtil$Vsa
            r0.<init>()
            r9 = r0
            com.android.server.wifi.util.InformationElementUtil$HtOperation r0 = new com.android.server.wifi.util.InformationElementUtil$HtOperation
            r0.<init>()
            r10 = r0
            com.android.server.wifi.util.InformationElementUtil$VhtOperation r0 = new com.android.server.wifi.util.InformationElementUtil$VhtOperation
            r0.<init>()
            r11 = r0
            com.android.server.wifi.util.InformationElementUtil$ExtendedCapabilities r0 = new com.android.server.wifi.util.InformationElementUtil$ExtendedCapabilities
            r0.<init>()
            r12 = r0
            com.android.server.wifi.util.InformationElementUtil$TrafficIndicationMap r0 = new com.android.server.wifi.util.InformationElementUtil$TrafficIndicationMap
            r0.<init>()
            r13 = r0
            com.android.server.wifi.util.InformationElementUtil$SupportedRates r0 = new com.android.server.wifi.util.InformationElementUtil$SupportedRates
            r0.<init>()
            r14 = r0
            com.android.server.wifi.util.InformationElementUtil$SupportedRates r0 = new com.android.server.wifi.util.InformationElementUtil$SupportedRates
            r0.<init>()
            r15 = r0
            r16 = 0
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            r17 = r0
            r18 = r3
            int r0 = r2.length     // Catch:{ ArrayIndexOutOfBoundsException | IllegalArgumentException | BufferUnderflowException -> 0x00ee }
            r21 = r5
            r5 = 0
        L_0x0060:
            if (r5 >= r0) goto L_0x00e7
            r22 = r2[r5]     // Catch:{ ArrayIndexOutOfBoundsException | IllegalArgumentException | BufferUnderflowException -> 0x00e1 }
            r23 = r22
            r3 = r23
            r23 = r0
            int r0 = r3.id     // Catch:{ ArrayIndexOutOfBoundsException | IllegalArgumentException | BufferUnderflowException -> 0x00e1 }
            java.lang.Integer r0 = java.lang.Integer.valueOf(r0)     // Catch:{ ArrayIndexOutOfBoundsException | IllegalArgumentException | BufferUnderflowException -> 0x00e1 }
            r2 = r17
            r2.add(r0)     // Catch:{ ArrayIndexOutOfBoundsException | IllegalArgumentException | BufferUnderflowException -> 0x00dd }
            int r0 = r3.id     // Catch:{ ArrayIndexOutOfBoundsException | IllegalArgumentException | BufferUnderflowException -> 0x00dd }
            if (r0 == 0) goto L_0x00ca
            r17 = r4
            r4 = 1
            if (r0 == r4) goto L_0x00c6
            r4 = 5
            if (r0 == r4) goto L_0x00c2
            r4 = 11
            if (r0 == r4) goto L_0x00be
            r4 = 50
            if (r0 == r4) goto L_0x00ba
            r4 = 61
            if (r0 == r4) goto L_0x00b6
            r4 = 107(0x6b, float:1.5E-43)
            if (r0 == r4) goto L_0x00b2
            r4 = 111(0x6f, float:1.56E-43)
            if (r0 == r4) goto L_0x00ae
            r4 = 127(0x7f, float:1.78E-43)
            if (r0 == r4) goto L_0x00aa
            r4 = 192(0xc0, float:2.69E-43)
            if (r0 == r4) goto L_0x00a6
            r4 = 221(0xdd, float:3.1E-43)
            if (r0 == r4) goto L_0x00a2
            goto L_0x00d0
        L_0x00a2:
            r9.from(r3)     // Catch:{ ArrayIndexOutOfBoundsException | IllegalArgumentException | BufferUnderflowException -> 0x00db }
            goto L_0x00d0
        L_0x00a6:
            r11.from(r3)     // Catch:{ ArrayIndexOutOfBoundsException | IllegalArgumentException | BufferUnderflowException -> 0x00db }
            goto L_0x00d0
        L_0x00aa:
            r12.from(r3)     // Catch:{ ArrayIndexOutOfBoundsException | IllegalArgumentException | BufferUnderflowException -> 0x00db }
            goto L_0x00d0
        L_0x00ae:
            r8.from(r3)     // Catch:{ ArrayIndexOutOfBoundsException | IllegalArgumentException | BufferUnderflowException -> 0x00db }
            goto L_0x00d0
        L_0x00b2:
            r7.from(r3)     // Catch:{ ArrayIndexOutOfBoundsException | IllegalArgumentException | BufferUnderflowException -> 0x00db }
            goto L_0x00d0
        L_0x00b6:
            r10.from(r3)     // Catch:{ ArrayIndexOutOfBoundsException | IllegalArgumentException | BufferUnderflowException -> 0x00db }
            goto L_0x00d0
        L_0x00ba:
            r15.from(r3)     // Catch:{ ArrayIndexOutOfBoundsException | IllegalArgumentException | BufferUnderflowException -> 0x00db }
            goto L_0x00d0
        L_0x00be:
            r6.from(r3)     // Catch:{ ArrayIndexOutOfBoundsException | IllegalArgumentException | BufferUnderflowException -> 0x00db }
            goto L_0x00d0
        L_0x00c2:
            r13.from(r3)     // Catch:{ ArrayIndexOutOfBoundsException | IllegalArgumentException | BufferUnderflowException -> 0x00db }
            goto L_0x00d0
        L_0x00c6:
            r14.from(r3)     // Catch:{ ArrayIndexOutOfBoundsException | IllegalArgumentException | BufferUnderflowException -> 0x00db }
            goto L_0x00d0
        L_0x00ca:
            r17 = r4
            byte[] r0 = r3.bytes     // Catch:{ ArrayIndexOutOfBoundsException | IllegalArgumentException | BufferUnderflowException -> 0x00db }
            r21 = r0
        L_0x00d0:
            int r5 = r5 + 1
            r4 = r17
            r0 = r23
            r17 = r2
            r2 = r27
            goto L_0x0060
        L_0x00db:
            r0 = move-exception
            goto L_0x00f5
        L_0x00dd:
            r0 = move-exception
            r17 = r4
            goto L_0x00f5
        L_0x00e1:
            r0 = move-exception
            r2 = r17
            r17 = r4
            goto L_0x00f5
        L_0x00e7:
            r2 = r17
            r17 = r4
            r3 = r21
            goto L_0x0117
        L_0x00ee:
            r0 = move-exception
            r2 = r17
            r17 = r4
            r21 = r5
        L_0x00f5:
            java.lang.Class r3 = r25.getClass()
            java.lang.String r3 = com.android.server.wifi.hotspot2.Utils.hs2LogTag(r3)
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            java.lang.String r5 = "Caught "
            r4.append(r5)
            r4.append(r0)
            java.lang.String r4 = r4.toString()
            android.util.Log.d(r3, r4)
            if (r21 == 0) goto L_0x0277
            r16 = r0
            r3 = r21
        L_0x0117:
            if (r3 == 0) goto L_0x017f
            java.nio.charset.Charset r0 = java.nio.charset.StandardCharsets.UTF_8
            java.nio.charset.CharsetDecoder r4 = r0.newDecoder()
            java.nio.ByteBuffer r0 = java.nio.ByteBuffer.wrap(r3)     // Catch:{ CharacterCodingException -> 0x012d }
            java.nio.CharBuffer r0 = r4.decode(r0)     // Catch:{ CharacterCodingException -> 0x012d }
            java.lang.String r5 = r0.toString()     // Catch:{ CharacterCodingException -> 0x012d }
            r0 = r5
            goto L_0x0130
        L_0x012d:
            r0 = move-exception
            r5 = 0
            r0 = r5
        L_0x0130:
            if (r0 != 0) goto L_0x0166
            java.lang.String r5 = "GBK"
            java.lang.String r5 = com.android.server.wifi.WifiGbk.encodeSsid(r3, r5)
            if (r5 == 0) goto L_0x0141
            java.lang.String r0 = com.android.server.wifi.util.NativeUtil.removeEnclosingQuotes(r5)
            r21 = r4
            goto L_0x016a
        L_0x0141:
            boolean r18 = r12.isStrictUtf8()
            if (r18 == 0) goto L_0x015a
            if (r16 != 0) goto L_0x014e
            r18 = r0
            r21 = r4
            goto L_0x015e
        L_0x014e:
            r18 = r0
            java.lang.IllegalArgumentException r0 = new java.lang.IllegalArgumentException
            r21 = r4
            java.lang.String r4 = "Failed to decode SSID in dubious IE string"
            r0.<init>(r4)
            throw r0
        L_0x015a:
            r18 = r0
            r21 = r4
        L_0x015e:
            java.lang.String r0 = new java.lang.String
            java.nio.charset.Charset r4 = java.nio.charset.StandardCharsets.ISO_8859_1
            r0.<init>(r3, r4)
            goto L_0x016a
        L_0x0166:
            r18 = r0
            r21 = r4
        L_0x016a:
            r4 = 1
            int r5 = r3.length
            r18 = r0
            r0 = 0
        L_0x016f:
            if (r0 >= r5) goto L_0x017c
            byte r17 = r3[r0]
            if (r17 == 0) goto L_0x0179
            r4 = 0
            r0 = r18
            goto L_0x0183
        L_0x0179:
            int r0 = r0 + 1
            goto L_0x016f
        L_0x017c:
            r0 = r18
            goto L_0x0183
        L_0x017f:
            r4 = r17
            r0 = r18
        L_0x0183:
            r1.mSSID = r0
            r5 = r2
            r21 = r3
            long r2 = r7.hessid
            r1.mHESSID = r2
            r1.mIsHiddenSsid = r4
            int r2 = r6.stationCount
            r1.mStationCount = r2
            int r2 = r6.channelUtilization
            r1.mChannelUtilization = r2
            int r2 = r6.capacity
            r1.mCapacity = r2
            com.android.server.wifi.hotspot2.NetworkDetail$Ant r2 = r7.ant
            r1.mAnt = r2
            boolean r2 = r7.internet
            r1.mInternet = r2
            com.android.server.wifi.hotspot2.NetworkDetail$HSRelease r2 = r9.hsRelease
            r1.mHSRelease = r2
            int r2 = r9.anqpDomainID
            r1.mAnqpDomainID = r2
            int r2 = r8.anqpOICount
            r1.mAnqpOICount = r2
            long[] r2 = r8.getRoamingConsortiums()
            r1.mRoamingConsortiums = r2
            r1.mExtendedCapabilities = r12
            r2 = 0
            r1.mANQPElements = r2
            r2 = r29
            r1.mPrimaryFreq = r2
            boolean r3 = r11.isValid()
            if (r3 == 0) goto L_0x01d6
            int r3 = r11.getChannelWidth()
            r1.mChannelWidth = r3
            int r3 = r11.getCenterFreq0()
            r1.mCenterfreq0 = r3
            int r3 = r11.getCenterFreq1()
            r1.mCenterfreq1 = r3
            goto L_0x01e7
        L_0x01d6:
            int r3 = r10.getChannelWidth()
            r1.mChannelWidth = r3
            int r3 = r1.mPrimaryFreq
            int r3 = r10.getCenterFreq0(r3)
            r1.mCenterfreq0 = r3
            r3 = 0
            r1.mCenterfreq1 = r3
        L_0x01e7:
            boolean r3 = r13.isValid()
            if (r3 == 0) goto L_0x01f1
            int r3 = r13.mDtimPeriod
            r1.mDtimInterval = r3
        L_0x01f1:
            r3 = 0
            r17 = 0
            boolean r18 = r15.isValid()
            if (r18 == 0) goto L_0x0215
            r18 = r0
            java.util.ArrayList<java.lang.Integer> r0 = r15.mRates
            java.util.ArrayList<java.lang.Integer> r2 = r15.mRates
            int r2 = r2.size()
            r20 = 1
            int r2 = r2 + -1
            java.lang.Object r0 = r0.get(r2)
            java.lang.Integer r0 = (java.lang.Integer) r0
            int r17 = r0.intValue()
            r0 = r17
            goto L_0x0219
        L_0x0215:
            r18 = r0
            r0 = r17
        L_0x0219:
            boolean r2 = r14.isValid()
            if (r2 == 0) goto L_0x0269
            java.util.ArrayList<java.lang.Integer> r2 = r14.mRates
            r17 = r3
            java.util.ArrayList<java.lang.Integer> r3 = r14.mRates
            int r3 = r3.size()
            r20 = 1
            int r3 = r3 + -1
            java.lang.Object r2 = r2.get(r3)
            java.lang.Integer r2 = (java.lang.Integer) r2
            int r3 = r2.intValue()
            if (r3 <= r0) goto L_0x023b
            r2 = r3
            goto L_0x023c
        L_0x023b:
            r2 = r0
        L_0x023c:
            r1.mMaxRate = r2
            int r2 = r1.mPrimaryFreq
            r20 = r0
            int r0 = r1.mMaxRate
            r17 = r3
            boolean r3 = r11.isValid()
            r23 = r4
            r19 = 61
            java.lang.Integer r4 = java.lang.Integer.valueOf(r19)
            boolean r4 = r5.contains(r4)
            r19 = 42
            r24 = r6
            java.lang.Integer r6 = java.lang.Integer.valueOf(r19)
            boolean r6 = r5.contains(r6)
            int r0 = com.android.server.wifi.util.InformationElementUtil.WifiMode.determineMode(r2, r0, r3, r4, r6)
            r1.mWifiMode = r0
            goto L_0x0276
        L_0x0269:
            r20 = r0
            r17 = r3
            r23 = r4
            r24 = r6
            r2 = 0
            r1.mWifiMode = r2
            r1.mMaxRate = r2
        L_0x0276:
            return
        L_0x0277:
            r5 = r2
            java.lang.IllegalArgumentException r2 = new java.lang.IllegalArgumentException
            java.lang.String r3 = "Malformed IE string (no SSID)"
            r2.<init>(r3, r0)
            throw r2
        L_0x0280:
            java.lang.IllegalArgumentException r0 = new java.lang.IllegalArgumentException
            java.lang.String r2 = "Null information elements"
            r0.<init>(r2)
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.wifi.hotspot2.NetworkDetail.<init>(java.lang.String, android.net.wifi.ScanResult$InformationElement[], java.util.List, int):void");
    }

    private static ByteBuffer getAndAdvancePayload(ByteBuffer data, int plLength) {
        ByteBuffer payload = data.duplicate().order(data.order());
        payload.limit(payload.position() + plLength);
        data.position(data.position() + plLength);
        return payload;
    }

    private NetworkDetail(NetworkDetail base, Map<Constants.ANQPElementType, ANQPElement> anqpElements) {
        this.mSSID = base.mSSID;
        this.mIsHiddenSsid = base.mIsHiddenSsid;
        this.mBSSID = base.mBSSID;
        this.mHESSID = base.mHESSID;
        this.mStationCount = base.mStationCount;
        this.mChannelUtilization = base.mChannelUtilization;
        this.mCapacity = base.mCapacity;
        this.mAnt = base.mAnt;
        this.mInternet = base.mInternet;
        this.mHSRelease = base.mHSRelease;
        this.mAnqpDomainID = base.mAnqpDomainID;
        this.mAnqpOICount = base.mAnqpOICount;
        this.mRoamingConsortiums = base.mRoamingConsortiums;
        this.mExtendedCapabilities = new InformationElementUtil.ExtendedCapabilities(base.mExtendedCapabilities);
        this.mANQPElements = anqpElements;
        this.mChannelWidth = base.mChannelWidth;
        this.mPrimaryFreq = base.mPrimaryFreq;
        this.mCenterfreq0 = base.mCenterfreq0;
        this.mCenterfreq1 = base.mCenterfreq1;
        this.mDtimInterval = base.mDtimInterval;
        this.mWifiMode = base.mWifiMode;
        this.mMaxRate = base.mMaxRate;
    }

    public NetworkDetail complete(Map<Constants.ANQPElementType, ANQPElement> anqpElements) {
        return new NetworkDetail(this, anqpElements);
    }

    public boolean queriable(List<Constants.ANQPElementType> queryElements) {
        return this.mAnt != null && (Constants.hasBaseANQPElements(queryElements) || (Constants.hasR2Elements(queryElements) && this.mHSRelease == HSRelease.R2));
    }

    public boolean has80211uInfo() {
        return (this.mAnt == null && this.mRoamingConsortiums == null && this.mHSRelease == null) ? false : true;
    }

    public boolean hasInterworking() {
        return this.mAnt != null;
    }

    public String getSSID() {
        return this.mSSID;
    }

    public String getTrimmedSSID() {
        if (this.mSSID == null) {
            return Prefix.EMPTY;
        }
        for (int n = 0; n < this.mSSID.length(); n++) {
            if (this.mSSID.charAt(n) != 0) {
                return this.mSSID;
            }
        }
        return Prefix.EMPTY;
    }

    public long getHESSID() {
        return this.mHESSID;
    }

    public long getBSSID() {
        return this.mBSSID;
    }

    public int getStationCount() {
        return this.mStationCount;
    }

    public int getChannelUtilization() {
        return this.mChannelUtilization;
    }

    public int getCapacity() {
        return this.mCapacity;
    }

    public boolean isInterworking() {
        return this.mAnt != null;
    }

    public Ant getAnt() {
        return this.mAnt;
    }

    public boolean isInternet() {
        return this.mInternet;
    }

    public HSRelease getHSRelease() {
        return this.mHSRelease;
    }

    public int getAnqpDomainID() {
        return this.mAnqpDomainID;
    }

    public byte[] getOsuProviders() {
        ANQPElement osuProviders;
        Map<Constants.ANQPElementType, ANQPElement> map = this.mANQPElements;
        if (map == null || (osuProviders = map.get(Constants.ANQPElementType.HSOSUProviders)) == null) {
            return null;
        }
        return ((RawByteElement) osuProviders).getPayload();
    }

    public int getAnqpOICount() {
        return this.mAnqpOICount;
    }

    public long[] getRoamingConsortiums() {
        return this.mRoamingConsortiums;
    }

    public Map<Constants.ANQPElementType, ANQPElement> getANQPElements() {
        return this.mANQPElements;
    }

    public int getChannelWidth() {
        return this.mChannelWidth;
    }

    public int getCenterfreq0() {
        return this.mCenterfreq0;
    }

    public int getCenterfreq1() {
        return this.mCenterfreq1;
    }

    public int getWifiMode() {
        return this.mWifiMode;
    }

    public int getDtimInterval() {
        return this.mDtimInterval;
    }

    public boolean is80211McResponderSupport() {
        return this.mExtendedCapabilities.is80211McRTTResponder();
    }

    public boolean isSSID_UTF8() {
        return this.mExtendedCapabilities.isStrictUtf8();
    }

    public boolean equals(Object thatObject) {
        if (this == thatObject) {
            return true;
        }
        if (thatObject == null || getClass() != thatObject.getClass()) {
            return false;
        }
        NetworkDetail that = (NetworkDetail) thatObject;
        if (!getSSID().equals(that.getSSID()) || getBSSID() != that.getBSSID()) {
            return false;
        }
        return true;
    }

    public int hashCode() {
        long j = this.mBSSID;
        return (((this.mSSID.hashCode() * 31) + ((int) (j >>> 32))) * 31) + ((int) j);
    }

    public String toString() {
        return String.format("NetworkInfo{SSID='%s', HESSID=%x, BSSID=%x, StationCount=%d, ChannelUtilization=%d, Capacity=%d, Ant=%s, Internet=%s, HSRelease=%s, AnqpDomainID=%d, AnqpOICount=%d, RoamingConsortiums=%s}", new Object[]{this.mSSID, Long.valueOf(this.mHESSID), Long.valueOf(this.mBSSID), Integer.valueOf(this.mStationCount), Integer.valueOf(this.mChannelUtilization), Integer.valueOf(this.mCapacity), this.mAnt, Boolean.valueOf(this.mInternet), this.mHSRelease, Integer.valueOf(this.mAnqpDomainID), Integer.valueOf(this.mAnqpOICount), Utils.roamingConsortiumsToString(this.mRoamingConsortiums)});
    }

    public String toKeyString() {
        if (this.mHESSID != 0) {
            return String.format("'%s':%012x (%012x)", new Object[]{this.mSSID, Long.valueOf(this.mBSSID), Long.valueOf(this.mHESSID)});
        }
        return String.format("'%s':%012x", new Object[]{this.mSSID, Long.valueOf(this.mBSSID)});
    }

    public String getBSSIDString() {
        return toMACString(this.mBSSID);
    }

    public boolean isBeaconFrame() {
        return this.mDtimInterval > 0;
    }

    public boolean isHiddenBeaconFrame() {
        return isBeaconFrame() && this.mIsHiddenSsid;
    }

    public static String toMACString(long mac) {
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (int n = 5; n >= 0; n--) {
            if (first) {
                first = false;
            } else {
                sb.append(':');
            }
            sb.append(String.format("%02x", new Object[]{Long.valueOf((mac >>> (n * 8)) & 255)}));
        }
        return sb.toString();
    }
}
