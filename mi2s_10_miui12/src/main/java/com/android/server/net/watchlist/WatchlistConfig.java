package com.android.server.net.watchlist;

import android.os.FileUtils;
import android.util.Log;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.util.HexDump;
import java.io.File;
import java.io.FileDescriptor;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.zip.CRC32;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

class WatchlistConfig {
    private static final String NETWORK_WATCHLIST_DB_FOR_TEST_PATH = "/data/misc/network_watchlist/network_watchlist_for_test.xml";
    private static final String NETWORK_WATCHLIST_DB_PATH = "/data/misc/network_watchlist/network_watchlist.xml";
    private static final String TAG = "WatchlistConfig";
    private static final WatchlistConfig sInstance = new WatchlistConfig();
    private volatile CrcShaDigests mDomainDigests;
    private volatile CrcShaDigests mIpDigests;
    private boolean mIsSecureConfig;
    private File mXmlFile;

    private static class XmlTags {
        private static final String CRC32_DOMAIN = "crc32-domain";
        private static final String CRC32_IP = "crc32-ip";
        private static final String HASH = "hash";
        private static final String SHA256_DOMAIN = "sha256-domain";
        private static final String SHA256_IP = "sha256-ip";
        private static final String WATCHLIST_CONFIG = "watchlist-config";

        private XmlTags() {
        }
    }

    private static class CrcShaDigests {
        final HarmfulDigests crc32Digests;
        final HarmfulDigests sha256Digests;

        public CrcShaDigests(HarmfulDigests crc32Digests2, HarmfulDigests sha256Digests2) {
            this.crc32Digests = crc32Digests2;
            this.sha256Digests = sha256Digests2;
        }
    }

    public static WatchlistConfig getInstance() {
        return sInstance;
    }

    private WatchlistConfig() {
        this(new File(NETWORK_WATCHLIST_DB_PATH));
    }

    @VisibleForTesting
    protected WatchlistConfig(File xmlFile) {
        this.mIsSecureConfig = true;
        this.mXmlFile = xmlFile;
        reloadConfig();
    }

    /* Debug info: failed to restart local var, previous not found, register: 15 */
    /* JADX WARNING: Can't fix incorrect switch cases order */
    /* JADX WARNING: Code restructure failed: missing block: B:40:0x00e2, code lost:
        r3 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:42:?, code lost:
        r2.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:46:0x00eb, code lost:
        throw r3;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void reloadConfig() {
        /*
            r15 = this;
            java.lang.String r0 = "watchlist-config"
            java.lang.String r1 = "WatchlistConfig"
            java.io.File r2 = r15.mXmlFile
            boolean r2 = r2.exists()
            if (r2 != 0) goto L_0x000e
            return
        L_0x000e:
            java.io.FileInputStream r2 = new java.io.FileInputStream     // Catch:{ IOException | IllegalStateException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x00ec }
            java.io.File r3 = r15.mXmlFile     // Catch:{ IOException | IllegalStateException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x00ec }
            r2.<init>(r3)     // Catch:{ IOException | IllegalStateException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x00ec }
            java.util.ArrayList r3 = new java.util.ArrayList     // Catch:{ all -> 0x00e0 }
            r3.<init>()     // Catch:{ all -> 0x00e0 }
            java.util.ArrayList r4 = new java.util.ArrayList     // Catch:{ all -> 0x00e0 }
            r4.<init>()     // Catch:{ all -> 0x00e0 }
            java.util.ArrayList r5 = new java.util.ArrayList     // Catch:{ all -> 0x00e0 }
            r5.<init>()     // Catch:{ all -> 0x00e0 }
            java.util.ArrayList r6 = new java.util.ArrayList     // Catch:{ all -> 0x00e0 }
            r6.<init>()     // Catch:{ all -> 0x00e0 }
            org.xmlpull.v1.XmlPullParser r7 = android.util.Xml.newPullParser()     // Catch:{ all -> 0x00e0 }
            java.nio.charset.Charset r8 = java.nio.charset.StandardCharsets.UTF_8     // Catch:{ all -> 0x00e0 }
            java.lang.String r8 = r8.name()     // Catch:{ all -> 0x00e0 }
            r7.setInput(r2, r8)     // Catch:{ all -> 0x00e0 }
            r7.nextTag()     // Catch:{ all -> 0x00e0 }
            r8 = 0
            r9 = 2
            r7.require(r9, r8, r0)     // Catch:{ all -> 0x00e0 }
        L_0x003e:
            int r10 = r7.nextTag()     // Catch:{ all -> 0x00e0 }
            r11 = 3
            if (r10 != r9) goto L_0x00b1
            java.lang.String r10 = r7.getName()     // Catch:{ all -> 0x00e0 }
            r12 = -1
            int r13 = r10.hashCode()     // Catch:{ all -> 0x00e0 }
            r14 = 1
            switch(r13) {
                case -1862636386: goto L_0x0073;
                case -14835926: goto L_0x0068;
                case 835385997: goto L_0x005d;
                case 1718657537: goto L_0x0053;
                default: goto L_0x0052;
            }     // Catch:{ all -> 0x00e0 }
        L_0x0052:
            goto L_0x007c
        L_0x0053:
            java.lang.String r13 = "crc32-ip"
            boolean r13 = r10.equals(r13)     // Catch:{ all -> 0x00e0 }
            if (r13 == 0) goto L_0x0052
            r12 = r14
            goto L_0x007c
        L_0x005d:
            java.lang.String r13 = "sha256-ip"
            boolean r13 = r10.equals(r13)     // Catch:{ all -> 0x00e0 }
            if (r13 == 0) goto L_0x0052
            r12 = r11
            goto L_0x007c
        L_0x0068:
            java.lang.String r13 = "sha256-domain"
            boolean r13 = r10.equals(r13)     // Catch:{ all -> 0x00e0 }
            if (r13 == 0) goto L_0x0052
            r12 = r9
            goto L_0x007c
        L_0x0073:
            java.lang.String r13 = "crc32-domain"
            boolean r13 = r10.equals(r13)     // Catch:{ all -> 0x00e0 }
            if (r13 == 0) goto L_0x0052
            r12 = 0
        L_0x007c:
            if (r12 == 0) goto L_0x00ac
            if (r12 == r14) goto L_0x00a8
            if (r12 == r9) goto L_0x00a4
            if (r12 == r11) goto L_0x00a0
            java.lang.StringBuilder r11 = new java.lang.StringBuilder     // Catch:{ all -> 0x00e0 }
            r11.<init>()     // Catch:{ all -> 0x00e0 }
            java.lang.String r12 = "Unknown element: "
            r11.append(r12)     // Catch:{ all -> 0x00e0 }
            java.lang.String r12 = r7.getName()     // Catch:{ all -> 0x00e0 }
            r11.append(r12)     // Catch:{ all -> 0x00e0 }
            java.lang.String r11 = r11.toString()     // Catch:{ all -> 0x00e0 }
            android.util.Log.w(r1, r11)     // Catch:{ all -> 0x00e0 }
            com.android.internal.util.XmlUtils.skipCurrentTag(r7)     // Catch:{ all -> 0x00e0 }
            goto L_0x00b0
        L_0x00a0:
            r15.parseHashes(r7, r10, r6)     // Catch:{ all -> 0x00e0 }
            goto L_0x00b0
        L_0x00a4:
            r15.parseHashes(r7, r10, r4)     // Catch:{ all -> 0x00e0 }
            goto L_0x00b0
        L_0x00a8:
            r15.parseHashes(r7, r10, r5)     // Catch:{ all -> 0x00e0 }
            goto L_0x00b0
        L_0x00ac:
            r15.parseHashes(r7, r10, r3)     // Catch:{ all -> 0x00e0 }
        L_0x00b0:
            goto L_0x003e
        L_0x00b1:
            r7.require(r11, r8, r0)     // Catch:{ all -> 0x00e0 }
            com.android.server.net.watchlist.WatchlistConfig$CrcShaDigests r0 = new com.android.server.net.watchlist.WatchlistConfig$CrcShaDigests     // Catch:{ all -> 0x00e0 }
            com.android.server.net.watchlist.HarmfulDigests r8 = new com.android.server.net.watchlist.HarmfulDigests     // Catch:{ all -> 0x00e0 }
            r8.<init>(r3)     // Catch:{ all -> 0x00e0 }
            com.android.server.net.watchlist.HarmfulDigests r9 = new com.android.server.net.watchlist.HarmfulDigests     // Catch:{ all -> 0x00e0 }
            r9.<init>(r4)     // Catch:{ all -> 0x00e0 }
            r0.<init>(r8, r9)     // Catch:{ all -> 0x00e0 }
            r15.mDomainDigests = r0     // Catch:{ all -> 0x00e0 }
            com.android.server.net.watchlist.WatchlistConfig$CrcShaDigests r0 = new com.android.server.net.watchlist.WatchlistConfig$CrcShaDigests     // Catch:{ all -> 0x00e0 }
            com.android.server.net.watchlist.HarmfulDigests r8 = new com.android.server.net.watchlist.HarmfulDigests     // Catch:{ all -> 0x00e0 }
            r8.<init>(r5)     // Catch:{ all -> 0x00e0 }
            com.android.server.net.watchlist.HarmfulDigests r9 = new com.android.server.net.watchlist.HarmfulDigests     // Catch:{ all -> 0x00e0 }
            r9.<init>(r6)     // Catch:{ all -> 0x00e0 }
            r0.<init>(r8, r9)     // Catch:{ all -> 0x00e0 }
            r15.mIpDigests = r0     // Catch:{ all -> 0x00e0 }
            java.lang.String r0 = "Reload watchlist done"
            android.util.Log.i(r1, r0)     // Catch:{ all -> 0x00e0 }
            r2.close()     // Catch:{ IOException | IllegalStateException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x00ec }
            goto L_0x00f2
        L_0x00e0:
            r0 = move-exception
            throw r0     // Catch:{ all -> 0x00e2 }
        L_0x00e2:
            r3 = move-exception
            r2.close()     // Catch:{ all -> 0x00e7 }
            goto L_0x00eb
        L_0x00e7:
            r4 = move-exception
            r0.addSuppressed(r4)     // Catch:{ IOException | IllegalStateException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x00ec }
        L_0x00eb:
            throw r3     // Catch:{ IOException | IllegalStateException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x00ec }
        L_0x00ec:
            r0 = move-exception
            java.lang.String r2 = "Failed parsing xml"
            android.util.Slog.e(r1, r2, r0)
        L_0x00f2:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.net.watchlist.WatchlistConfig.reloadConfig():void");
    }

    private void parseHashes(XmlPullParser parser, String tagName, List<byte[]> hashList) throws IOException, XmlPullParserException {
        parser.require(2, (String) null, tagName);
        while (parser.nextTag() == 2) {
            parser.require(2, (String) null, "hash");
            byte[] hash = HexDump.hexStringToByteArray(parser.nextText());
            parser.require(3, (String) null, "hash");
            hashList.add(hash);
        }
        parser.require(3, (String) null, tagName);
    }

    public boolean containsDomain(String domain) {
        CrcShaDigests domainDigests = this.mDomainDigests;
        if (domainDigests == null) {
            return false;
        }
        if (!domainDigests.crc32Digests.contains(getCrc32(domain))) {
            return false;
        }
        return domainDigests.sha256Digests.contains(getSha256(domain));
    }

    public boolean containsIp(String ip) {
        CrcShaDigests ipDigests = this.mIpDigests;
        if (ipDigests == null) {
            return false;
        }
        if (!ipDigests.crc32Digests.contains(getCrc32(ip))) {
            return false;
        }
        return ipDigests.sha256Digests.contains(getSha256(ip));
    }

    private byte[] getCrc32(String str) {
        CRC32 crc = new CRC32();
        crc.update(str.getBytes());
        long tmp = crc.getValue();
        return new byte[]{(byte) ((int) ((tmp >> 24) & 255)), (byte) ((int) ((tmp >> 16) & 255)), (byte) ((int) ((tmp >> 8) & 255)), (byte) ((int) (tmp & 255))};
    }

    private byte[] getSha256(String str) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA256");
            messageDigest.update(str.getBytes());
            return messageDigest.digest();
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
    }

    public boolean isConfigSecure() {
        return this.mIsSecureConfig;
    }

    public byte[] getWatchlistConfigHash() {
        if (!this.mXmlFile.exists()) {
            return null;
        }
        try {
            return DigestUtils.getSha256Hash(this.mXmlFile);
        } catch (IOException | NoSuchAlgorithmException e) {
            Log.e(TAG, "Unable to get watchlist config hash", e);
            return null;
        }
    }

    public void setTestMode(InputStream testConfigInputStream) throws IOException {
        Log.i(TAG, "Setting watchlist testing config");
        FileUtils.copyToFileOrThrow(testConfigInputStream, new File(NETWORK_WATCHLIST_DB_FOR_TEST_PATH));
        this.mIsSecureConfig = false;
        this.mXmlFile = new File(NETWORK_WATCHLIST_DB_FOR_TEST_PATH);
        reloadConfig();
    }

    public void removeTestModeConfig() {
        try {
            File f = new File(NETWORK_WATCHLIST_DB_FOR_TEST_PATH);
            if (f.exists()) {
                f.delete();
            }
        } catch (Exception e) {
            Log.e(TAG, "Unable to delete test config");
        }
    }

    public void dump(FileDescriptor fd, PrintWriter pw, String[] args) {
        byte[] hash = getWatchlistConfigHash();
        StringBuilder sb = new StringBuilder();
        sb.append("Watchlist config hash: ");
        sb.append(hash != null ? HexDump.toHexString(hash) : null);
        pw.println(sb.toString());
        pw.println("Domain CRC32 digest list:");
        if (this.mDomainDigests != null) {
            this.mDomainDigests.crc32Digests.dump(fd, pw, args);
        }
        pw.println("Domain SHA256 digest list:");
        if (this.mDomainDigests != null) {
            this.mDomainDigests.sha256Digests.dump(fd, pw, args);
        }
        pw.println("Ip CRC32 digest list:");
        if (this.mIpDigests != null) {
            this.mIpDigests.crc32Digests.dump(fd, pw, args);
        }
        pw.println("Ip SHA256 digest list:");
        if (this.mIpDigests != null) {
            this.mIpDigests.sha256Digests.dump(fd, pw, args);
        }
    }
}
