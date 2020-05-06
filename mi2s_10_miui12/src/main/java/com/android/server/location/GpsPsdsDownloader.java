package com.android.server.location;

import android.net.TrafficStats;
import android.text.TextUtils;
import android.util.Log;
import java.util.Properties;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class GpsPsdsDownloader {
    private static final int CONNECTION_TIMEOUT_MS = ((int) TimeUnit.SECONDS.toMillis(30));
    private static final boolean DEBUG = Log.isLoggable(TAG, 3);
    private static final String DEFAULT_USER_AGENT = "Android";
    private static final long MAXIMUM_CONTENT_LENGTH_BYTES = 1000000;
    private static final int READ_TIMEOUT_MS = ((int) TimeUnit.SECONDS.toMillis(60));
    private static final String TAG = "GpsPsdsDownloader";
    private int mNextServerIndex;
    private final String[] mPsdsServers;
    private final String mUserAgent;

    GpsPsdsDownloader(Properties properties) {
        int count = 0;
        String server1 = properties.getProperty("XTRA_SERVER_1");
        String server2 = properties.getProperty("XTRA_SERVER_2");
        String server3 = properties.getProperty("XTRA_SERVER_3");
        count = server1 != null ? 0 + 1 : count;
        count = server2 != null ? count + 1 : count;
        count = server3 != null ? count + 1 : count;
        String agent = properties.getProperty("XTRA_USER_AGENT");
        if (TextUtils.isEmpty(agent)) {
            this.mUserAgent = DEFAULT_USER_AGENT;
        } else {
            this.mUserAgent = agent;
        }
        if (count == 0) {
            Log.e(TAG, "No PSDS servers were specified in the GPS configuration");
            this.mPsdsServers = null;
            return;
        }
        this.mPsdsServers = new String[count];
        int count2 = 0;
        if (server1 != null) {
            this.mPsdsServers[0] = server1;
            count2 = 0 + 1;
        }
        if (server2 != null) {
            this.mPsdsServers[count2] = server2;
            count2++;
        }
        if (server3 != null) {
            this.mPsdsServers[count2] = server3;
            count2++;
        }
        this.mNextServerIndex = new Random().nextInt(count2);
    }

    /* JADX INFO: finally extract failed */
    /* access modifiers changed from: package-private */
    public byte[] downloadPsdsData() {
        byte[] result = null;
        int startIndex = this.mNextServerIndex;
        if (this.mPsdsServers == null) {
            return null;
        }
        while (result == null) {
            int oldTag = TrafficStats.getAndSetThreadStatsTag(-188);
            try {
                result = doDownload(this.mPsdsServers[this.mNextServerIndex]);
                TrafficStats.setThreadStatsTag(oldTag);
                this.mNextServerIndex++;
                if (this.mNextServerIndex == this.mPsdsServers.length) {
                    this.mNextServerIndex = 0;
                }
                if (this.mNextServerIndex == startIndex) {
                    break;
                }
            } catch (Throwable th) {
                TrafficStats.setThreadStatsTag(oldTag);
                throw th;
            }
        }
        return result;
    }

    /* Debug info: failed to restart local var, previous not found, register: 13 */
    /* access modifiers changed from: protected */
    /* JADX WARNING: Code restructure failed: missing block: B:36:0x00b0, code lost:
        r6 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:37:0x00b1, code lost:
        if (r4 != null) goto L_0x00b3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:39:?, code lost:
        r4.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:43:0x00bb, code lost:
        throw r6;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public byte[] doDownload(java.lang.String r14) {
        /*
            r13 = this;
            boolean r0 = DEBUG
            java.lang.String r1 = "GpsPsdsDownloader"
            if (r0 == 0) goto L_0x001a
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r2 = "Downloading PSDS data from "
            r0.append(r2)
            r0.append(r14)
            java.lang.String r0 = r0.toString()
            android.util.Log.d(r1, r0)
        L_0x001a:
            r0 = 0
            r2 = 0
            java.net.URL r3 = new java.net.URL     // Catch:{ IOException -> 0x00be }
            r3.<init>(r14)     // Catch:{ IOException -> 0x00be }
            java.net.URLConnection r3 = r3.openConnection()     // Catch:{ IOException -> 0x00be }
            java.net.HttpURLConnection r3 = (java.net.HttpURLConnection) r3     // Catch:{ IOException -> 0x00be }
            r0 = r3
            java.lang.String r3 = "Accept"
            java.lang.String r4 = "*/*, application/vnd.wap.mms-message, application/vnd.wap.sic"
            r0.setRequestProperty(r3, r4)     // Catch:{ IOException -> 0x00be }
            java.lang.String r3 = "x-wap-profile"
            java.lang.String r4 = "http://www.openmobilealliance.org/tech/profiles/UAPROF/ccppschema-20021212#"
            r0.setRequestProperty(r3, r4)     // Catch:{ IOException -> 0x00be }
            int r3 = CONNECTION_TIMEOUT_MS     // Catch:{ IOException -> 0x00be }
            r0.setConnectTimeout(r3)     // Catch:{ IOException -> 0x00be }
            int r3 = READ_TIMEOUT_MS     // Catch:{ IOException -> 0x00be }
            r0.setReadTimeout(r3)     // Catch:{ IOException -> 0x00be }
            r0.connect()     // Catch:{ IOException -> 0x00be }
            int r3 = r0.getResponseCode()     // Catch:{ IOException -> 0x00be }
            r4 = 200(0xc8, float:2.8E-43)
            if (r3 == r4) goto L_0x006b
            boolean r4 = DEBUG     // Catch:{ IOException -> 0x00be }
            if (r4 == 0) goto L_0x0065
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ IOException -> 0x00be }
            r4.<init>()     // Catch:{ IOException -> 0x00be }
            java.lang.String r5 = "HTTP error downloading gps PSDS: "
            r4.append(r5)     // Catch:{ IOException -> 0x00be }
            r4.append(r3)     // Catch:{ IOException -> 0x00be }
            java.lang.String r4 = r4.toString()     // Catch:{ IOException -> 0x00be }
            android.util.Log.d(r1, r4)     // Catch:{ IOException -> 0x00be }
        L_0x0065:
            r0.disconnect()
            return r2
        L_0x006b:
            java.io.InputStream r4 = r0.getInputStream()     // Catch:{ IOException -> 0x00be }
            java.io.ByteArrayOutputStream r5 = new java.io.ByteArrayOutputStream     // Catch:{ all -> 0x00ae }
            r5.<init>()     // Catch:{ all -> 0x00ae }
            r6 = 1024(0x400, float:1.435E-42)
            byte[] r6 = new byte[r6]     // Catch:{ all -> 0x00ae }
        L_0x0078:
            int r7 = r4.read(r6)     // Catch:{ all -> 0x00ae }
            r8 = r7
            r9 = -1
            if (r7 == r9) goto L_0x00a2
            r7 = 0
            r5.write(r6, r7, r8)     // Catch:{ all -> 0x00ae }
            int r7 = r5.size()     // Catch:{ all -> 0x00ae }
            long r9 = (long) r7     // Catch:{ all -> 0x00ae }
            r11 = 1000000(0xf4240, double:4.940656E-318)
            int r7 = (r9 > r11 ? 1 : (r9 == r11 ? 0 : -1))
            if (r7 <= 0) goto L_0x0078
            boolean r7 = DEBUG     // Catch:{ all -> 0x00ae }
            if (r7 == 0) goto L_0x0099
            java.lang.String r7 = "PSDS file too large"
            android.util.Log.d(r1, r7)     // Catch:{ all -> 0x00ae }
        L_0x0099:
            r4.close()     // Catch:{ IOException -> 0x00be }
            r0.disconnect()
            return r2
        L_0x00a2:
            byte[] r7 = r5.toByteArray()     // Catch:{ all -> 0x00ae }
            r4.close()     // Catch:{ IOException -> 0x00be }
            r0.disconnect()
            return r7
        L_0x00ae:
            r5 = move-exception
            throw r5     // Catch:{ all -> 0x00b0 }
        L_0x00b0:
            r6 = move-exception
            if (r4 == 0) goto L_0x00bb
            r4.close()     // Catch:{ all -> 0x00b7 }
            goto L_0x00bb
        L_0x00b7:
            r7 = move-exception
            r5.addSuppressed(r7)     // Catch:{ IOException -> 0x00be }
        L_0x00bb:
            throw r6     // Catch:{ IOException -> 0x00be }
        L_0x00bc:
            r1 = move-exception
            goto L_0x00ce
        L_0x00be:
            r3 = move-exception
            boolean r4 = DEBUG     // Catch:{ all -> 0x00bc }
            if (r4 == 0) goto L_0x00c8
            java.lang.String r4 = "Error downloading gps PSDS: "
            android.util.Log.d(r1, r4, r3)     // Catch:{ all -> 0x00bc }
        L_0x00c8:
            if (r0 == 0) goto L_0x00cd
            r0.disconnect()
        L_0x00cd:
            return r2
        L_0x00ce:
            if (r0 == 0) goto L_0x00d3
            r0.disconnect()
        L_0x00d3:
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.location.GpsPsdsDownloader.doDownload(java.lang.String):byte[]");
    }
}
