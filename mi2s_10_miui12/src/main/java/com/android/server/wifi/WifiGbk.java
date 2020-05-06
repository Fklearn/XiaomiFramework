package com.android.server.wifi;

import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiSsid;
import android.util.Log;
import com.android.server.wifi.util.NativeUtil;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import miui.telephony.phonenumber.Prefix;

public class WifiGbk {
    private static final String BSSID_REGIX = "(?:[0-9a-fA-F]{2}:){5}[0-9a-fA-F]{2}";
    private static final boolean DBG = true;
    public static final int MAX_SSID_LENGTH = 32;
    public static final int MAX_SSID_UTF_LENGTH = 48;
    private static final int SCAN_CACHE_EXPIRATION_COUNT = 2;
    private static final String TAG = "WifiGbk";
    private static final ArrayList<BssCache> mBssCacheList = new ArrayList<>();
    private static final HashMap<String, Integer> mBssRandom = new HashMap<>();
    private static Object mLock = new Object();

    protected static void loge(String s) {
        Log.e(TAG, s);
    }

    protected static void logi(String s) {
        Log.i(TAG, s);
    }

    protected static void logd(String s) {
        Log.d(TAG, s);
    }

    private static int getBssRandom(String SSID, int security) {
        synchronized (mLock) {
            String key = BssCache.bssToString(SSID, security);
            Integer rb = mBssRandom.get(key);
            if (rb == null) {
                mBssRandom.put(key, 0);
                return 0;
            }
            int rbInt = rb.intValue() + 1;
            mBssRandom.put(key, Integer.valueOf(rbInt));
            return rbInt;
        }
    }

    private static boolean isValid(ScanResult result) {
        if (result == null || result.wifiSsid == null || result.BSSID == null) {
            logi("Invalid ScanResult - BSSID=" + result.BSSID + " SSID=" + result.SSID);
            return false;
        } else if (result.wifiSsid.isHidden()) {
            return false;
        } else {
            return true;
        }
    }

    private static boolean isValid(BssCache bss) {
        if (bss != null && bss.SSID != null && bss.BSSID != null) {
            return true;
        }
        logi("Invalid BssCache - BSSID=" + bss.BSSID + " SSID=" + bss.SSID);
        return false;
    }

    private static BssCache getBssCache(String BSSID, byte[] ssidBytes) {
        Iterator<BssCache> it = mBssCacheList.iterator();
        while (it.hasNext()) {
            BssCache bss = it.next();
            if (bss.matches(BSSID, ssidBytes)) {
                return bss;
            }
        }
        return null;
    }

    private static BssCache getBssCache(String BSSID, String SSID) {
        Iterator<BssCache> it = mBssCacheList.iterator();
        while (it.hasNext()) {
            BssCache bss = it.next();
            if (bss.matches(BSSID, SSID)) {
                return bss;
            }
        }
        return null;
    }

    private static BssCache getPreferredBssCache(String SSID, int security) {
        int gbkCount = 0;
        int utfCount = 0;
        BssCache gbkBss = null;
        BssCache utfBss = null;
        Iterator<BssCache> it = mBssCacheList.iterator();
        while (it.hasNext()) {
            BssCache bss = it.next();
            if (bss.matches(SSID, security)) {
                if (bss.isGbk) {
                    gbkCount++;
                    if (gbkBss == null) {
                        gbkBss = bss;
                    } else if (gbkBss.level > bss.level) {
                        gbkBss = bss;
                    }
                } else {
                    utfCount++;
                    if (utfBss == null) {
                        utfBss = bss;
                    } else if (utfBss.level > bss.level) {
                        utfBss = bss;
                    }
                }
            }
        }
        if (gbkCount == 0 || utfCount != 0) {
            if (!(gbkCount == 0 || utfCount == 0)) {
                int rand = getBssRandom(SSID, security);
                logd("getPreferredBssCache - ssid=" + SSID + " security=" + BssCache.securityToString(security) + " gbk=" + gbkCount + " utf=" + utfCount + " rand=" + rand);
                if (rand % 2 == 0) {
                    return gbkBss;
                }
            }
            return utfBss;
        }
        logd("getPreferredBssCache - ssid=" + SSID + " security=" + BssCache.securityToString(security) + " gbk=" + gbkCount + " utf=" + utfCount);
        return gbkBss;
    }

    private static boolean addOrUpdateBssCache(ScanResult result) {
        synchronized (mLock) {
            BssCache bss = getBssCache(result.BSSID, result.wifiSsid.getOctets());
            if (bss == null) {
                BssCache bss2 = new BssCache(result);
                if (isValid(bss2)) {
                    mBssCacheList.add(bss2);
                    logd("adding bss - " + bss2);
                }
            } else {
                bss.update(result);
            }
        }
        return true;
    }

    public static void ageBssCache() {
        synchronized (mLock) {
            Iterator<BssCache> it = mBssCacheList.iterator();
            while (it.hasNext()) {
                BssCache bss = it.next();
                bss.expire_count--;
                if (bss.expire_count <= 0) {
                    it.remove();
                    logd("removing bss - " + bss);
                }
            }
        }
    }

    public static void clearBssCache() {
        synchronized (mLock) {
            mBssCacheList.clear();
            mBssRandom.clear();
        }
    }

    public static boolean processScanResult(ScanResult result) {
        if (isValid(result) && !isAllAscii(result.wifiSsid.getOctets())) {
            return addOrUpdateBssCache(result);
        }
        return false;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:20:0x005b, code lost:
        return r6.SSID;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static java.lang.String getRealSsid(android.net.wifi.WifiConfiguration r6) {
        /*
            android.net.wifi.WifiConfiguration$NetworkSelectionStatus r0 = r6.getNetworkSelectionStatus()
            java.lang.String r0 = r0.getNetworkSelectionBSSID()
            if (r0 == 0) goto L_0x0014
            java.lang.String r1 = "(?:[0-9a-fA-F]{2}:){5}[0-9a-fA-F]{2}"
            boolean r1 = r0.matches(r1)
            if (r1 == 0) goto L_0x0014
            r1 = 1
            goto L_0x0015
        L_0x0014:
            r1 = 0
        L_0x0015:
            r2 = 0
            java.lang.Object r3 = mLock
            monitor-enter(r3)
            if (r1 == 0) goto L_0x0023
            java.lang.String r4 = r6.SSID     // Catch:{ all -> 0x005c }
            com.android.server.wifi.WifiGbk$BssCache r4 = getBssCache((java.lang.String) r0, (java.lang.String) r4)     // Catch:{ all -> 0x005c }
            r2 = r4
            goto L_0x002e
        L_0x0023:
            java.lang.String r4 = r6.SSID     // Catch:{ all -> 0x005c }
            int r5 = com.android.server.wifi.WifiGbk.BssCache.getSecurity((android.net.wifi.WifiConfiguration) r6)     // Catch:{ all -> 0x005c }
            com.android.server.wifi.WifiGbk$BssCache r4 = getPreferredBssCache(r4, r5)     // Catch:{ all -> 0x005c }
            r2 = r4
        L_0x002e:
            if (r2 == 0) goto L_0x0058
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ all -> 0x005c }
            r4.<init>()     // Catch:{ all -> 0x005c }
            java.lang.String r5 = "getRealSsid - BSSID="
            r4.append(r5)     // Catch:{ all -> 0x005c }
            r4.append(r0)     // Catch:{ all -> 0x005c }
            java.lang.String r5 = " - "
            r4.append(r5)     // Catch:{ all -> 0x005c }
            r4.append(r2)     // Catch:{ all -> 0x005c }
            java.lang.String r4 = r4.toString()     // Catch:{ all -> 0x005c }
            logi(r4)     // Catch:{ all -> 0x005c }
            boolean r4 = r2.isGbk     // Catch:{ all -> 0x005c }
            if (r4 == 0) goto L_0x0058
            byte[] r4 = r2.ssidBytes     // Catch:{ all -> 0x005c }
            java.lang.String r4 = com.android.server.wifi.util.NativeUtil.hexStringFromByteArray(r4)     // Catch:{ all -> 0x005c }
            monitor-exit(r3)     // Catch:{ all -> 0x005c }
            return r4
        L_0x0058:
            monitor-exit(r3)     // Catch:{ all -> 0x005c }
            java.lang.String r3 = r6.SSID
            return r3
        L_0x005c:
            r4 = move-exception
            monitor-exit(r3)     // Catch:{ all -> 0x005c }
            throw r4
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.wifi.WifiGbk.getRealSsid(android.net.wifi.WifiConfiguration):java.lang.String");
    }

    public static byte[] getRandUtfOrGbkBytes(String SSID) throws IllegalArgumentException {
        boolean utfSsidValid = false;
        boolean gbkSsidValid = false;
        byte[] utfBytes = NativeUtil.byteArrayFromArrayList(NativeUtil.decodeSsid(SSID));
        if (utfBytes == null || utfBytes.length > 48) {
            throw new IllegalArgumentException("Exceed max length 48, ssid=" + SSID);
        }
        if (utfBytes.length <= 32) {
            utfSsidValid = true;
        }
        byte[] gbkBytes = isAllAscii(utfBytes) ? null : getSsidBytes(SSID, "GBK");
        if (gbkBytes != null && gbkBytes.length <= 32) {
            gbkSsidValid = true;
        }
        if (!utfSsidValid && gbkSsidValid) {
            return gbkBytes;
        }
        if (utfSsidValid && !gbkSsidValid) {
            return utfBytes;
        }
        if (utfSsidValid && gbkSsidValid) {
            int rand = getBssRandom(SSID, 0);
            logd("getRandUtfOrGbkBytes - ssid=" + SSID + " rand=" + rand);
            return rand % 2 == 0 ? gbkBytes : utfBytes;
        }
        throw new IllegalArgumentException("No valid utfBytes or gbkBytes for ssid=" + SSID);
    }

    public static WifiSsid createWifiSsidFromByteArray(byte[] ssidBytes) {
        byte[] utfBytes;
        if (!isGbk(ssidBytes) || (utfBytes = toUtf(ssidBytes)) == null) {
            return WifiSsid.createFromByteArray(ssidBytes);
        }
        return WifiSsid.createFromByteArray(utfBytes);
    }

    public static boolean isAllAscii(byte[] ssidBytes) {
        if (ssidBytes == null) {
            return false;
        }
        for (byte b : ssidBytes) {
            if (b < 0) {
                return false;
            }
        }
        return true;
    }

    public static boolean isGbk(byte[] ssidBytes) {
        if (encodeSsid(ssidBytes, "UTF-8") == null && encodeSsid(ssidBytes, "GBK") != null) {
            return true;
        }
        return false;
    }

    public static String getEncoding(String str) {
        try {
            if (str.equals(new String(str.getBytes("GB2312"), "GB2312"))) {
                return "GB2312";
            }
        } catch (Exception e) {
        }
        try {
            if (str.equals(new String(str.getBytes("ISO-8859-1"), "ISO-8859-1"))) {
                return "ISO-8859-1";
            }
        } catch (Exception e2) {
        }
        try {
            if (str.equals(new String(str.getBytes("UTF-8"), "UTF-8"))) {
                return "UTF-8";
            }
        } catch (Exception e3) {
        }
        try {
            if (str.equals(new String(str.getBytes("GBK"), "GBK"))) {
                return "GBK";
            }
            return Prefix.EMPTY;
        } catch (Exception e4) {
            return Prefix.EMPTY;
        }
    }

    public static String subStringByU8(String str, int len) throws IOException {
        byte[] buf = str.getBytes("UTF-8");
        int count = 0;
        int x = len - 1;
        while (x >= 0 && buf[x] < 0) {
            count++;
            x--;
        }
        if (count % 3 == 0) {
            return new String(buf, 0, len, "UTF-8");
        }
        if (count % 3 == 1) {
            return new String(buf, 0, len - 1, "UTF-8");
        }
        return new String(buf, 0, len - 2, "UTF-8");
    }

    public static byte[] toUtf(byte[] gbkBytes) {
        String ssid = encodeSsid(gbkBytes, "GBK");
        if (ssid == null) {
            return null;
        }
        return getSsidBytes(ssid, "UTF-8");
    }

    public static byte[] toGbk(byte[] utfBytes) {
        String ssid = encodeSsid(utfBytes, "UTF-8");
        if (ssid == null) {
            return null;
        }
        return getSsidBytes(ssid, "GBK");
    }

    public static byte[] getSsidBytes(String ssid, String charsetName) {
        if (ssid == null) {
            return null;
        }
        byte[] ssidBytes = null;
        try {
            ssidBytes = NativeUtil.removeEnclosingQuotes(ssid).getBytes(charsetName);
        } catch (UnsupportedEncodingException e) {
        }
        int maxlen = "UTF-8".equals(charsetName) ? 48 : 32;
        if (ssidBytes.length <= maxlen) {
            return ssidBytes;
        }
        loge("getSsidBytes - converted SSID exceed max length " + maxlen + ", ssid=" + ssid);
        return null;
    }

    public static String encodeSsid(byte[] ssidBytes, String name) {
        String ssid = null;
        try {
            CharBuffer decoded = Charset.forName(name).newDecoder().decode(ByteBuffer.wrap(ssidBytes));
            ssid = "\"" + decoded.toString() + "\"";
        } catch (CharacterCodingException | UnsupportedCharsetException e) {
        }
        int maxlen = "UTF-8".equals(name) ? 48 : 32;
        if (ssid == null || ssid.length() <= maxlen + 2) {
            return ssid;
        }
        loge("encodeSsid - converted SSID exceed max length " + maxlen + ", ssid=" + ssid);
        return null;
    }

    private static class BssCache {
        public static final int SECURITY_EAP = 3;
        public static final int SECURITY_NONE = 0;
        public static final int SECURITY_PSK = 2;
        public static final int SECURITY_WEP = 1;
        public String BSSID;
        public String SSID;
        public int expire_count;
        public int frequency;
        boolean isGbk;
        public int level;
        public int security;
        public byte[] ssidBytes;

        public BssCache() {
        }

        public BssCache(ScanResult result) {
            this.ssidBytes = result.wifiSsid.getOctets();
            this.isGbk = WifiGbk.isGbk(this.ssidBytes);
            this.SSID = NativeUtil.addEnclosingQuotes(result.SSID);
            this.BSSID = result.BSSID;
            this.security = getSecurity(result);
            this.level = result.level;
            this.frequency = result.frequency;
            this.expire_count = 2;
            if (this.isGbk) {
                this.SSID = WifiGbk.encodeSsid(this.ssidBytes, "GBK");
                replaceSSIDinScanResult(result);
            }
        }

        public void update(ScanResult result) {
            if (matches(result.BSSID, result.wifiSsid.getOctets())) {
                this.security = getSecurity(result);
                this.level = result.level;
                this.frequency = result.frequency;
                this.expire_count = 2;
                if (this.isGbk) {
                    replaceSSIDinScanResult(result);
                }
            }
        }

        private boolean replaceSSIDinScanResult(ScanResult result) {
            byte[] utfBytes = WifiGbk.getSsidBytes(this.SSID, "UTF-8");
            String str = this.SSID;
            if (str == null || utfBytes == null) {
                WifiGbk.loge("replaceSSIDinScanResult fail - result=" + result);
                return false;
            }
            result.SSID = NativeUtil.removeEnclosingQuotes(str);
            result.wifiSsid = WifiSsid.createFromByteArray(utfBytes);
            return true;
        }

        public boolean matches(String BSSID2, byte[] ssidBytes2) {
            if (!this.BSSID.equals(BSSID2)) {
                return false;
            }
            return Arrays.equals(this.ssidBytes, ssidBytes2);
        }

        public boolean matches(String BSSID2, String SSID2) {
            if (!this.BSSID.equals(BSSID2)) {
                return false;
            }
            return this.SSID.equals(SSID2);
        }

        public boolean matches(String SSID2, int security2) {
            if (this.SSID.equals(SSID2) && this.security == security2) {
                return true;
            }
            return false;
        }

        public static int getSecurity(ScanResult result) {
            if (result.capabilities.contains("WEP")) {
                return 1;
            }
            if (result.capabilities.contains("PSK")) {
                return 2;
            }
            if (result.capabilities.contains("EAP")) {
                return 3;
            }
            return 0;
        }

        public static int getSecurity(WifiConfiguration config) {
            if (config.allowedKeyManagement.get(1)) {
                return 2;
            }
            if (config.allowedKeyManagement.get(2) || config.allowedKeyManagement.get(3)) {
                return 3;
            }
            if (config.wepKeys[0] != null) {
                return 1;
            }
            return 0;
        }

        public static String securityToString(int security2) {
            if (security2 == 0) {
                return "NONE";
            }
            if (security2 == 1) {
                return "WEP";
            }
            if (security2 == 2) {
                return "PSK";
            }
            if (security2 != 3) {
                return "?";
            }
            return "EAP";
        }

        public static String bssToString(String SSID2, int security2) {
            return SSID2 + securityToString(security2);
        }

        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("Bss(");
            StringBuilder builder = sb.append(this.SSID);
            if (this.BSSID != null) {
                builder.append(":");
                builder.append(this.BSSID);
            }
            builder.append(", isGbk=");
            builder.append(this.isGbk);
            builder.append(", security=");
            builder.append(securityToString(this.security));
            builder.append(", level=");
            builder.append(this.level);
            builder.append(", frequency=");
            builder.append(this.frequency);
            builder.append(')');
            return builder.toString();
        }
    }
}
