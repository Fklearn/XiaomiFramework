package com.android.server.connectivity;

import android.content.Context;
import android.net.shared.MiuiConfigCaptivePortal;

public class NetworkMonitorInjector {
    private static final int SOCKET_TIMEOUT_MS = 10000;
    private static final String TAG = "NetworkMonitorInjector";
    private static final String[] sDefaultCandidates = {"http://info.3g.qq.com/", "http://m.baidu.com/", "http://m.sohu.com/"};
    private static final String[] sGlobalCandidates = {"http://www.google.com/", "http://www.facebook.com/", "http://www.youtube.com/"};

    static final String getCaptivePortalServer(Context context, String url, boolean isHttps) {
        String server = MiuiConfigCaptivePortal.getCaptivePortalServer((String) null);
        if (server == null) {
            return url;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(isHttps ? "https" : "http");
        sb.append("://");
        sb.append(server);
        sb.append("/generate_204");
        return sb.toString();
    }

    /* JADX WARNING: Code restructure failed: missing block: B:15:0x00d3, code lost:
        if (r2 == null) goto L_0x00d6;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    static final int sendHttpProbe(android.net.Network r13, android.net.util.SharedLog r14) {
        /*
            java.lang.String r0 = "Probably not a portal: exception "
            java.util.Random r1 = new java.util.Random
            r1.<init>()
            r2 = 3
            int r1 = r1.nextInt(r2)
            r2 = 0
            r3 = 599(0x257, float:8.4E-43)
            boolean r4 = miui.os.Build.IS_INTERNATIONAL_BUILD
            if (r4 == 0) goto L_0x0016
            java.lang.String[] r4 = sGlobalCandidates
            goto L_0x0018
        L_0x0016:
            java.lang.String[] r4 = sDefaultCandidates
        L_0x0018:
            java.net.URL r5 = new java.net.URL     // Catch:{ MalformedURLException -> 0x00b9, IOException -> 0x007c }
            r6 = r4[r1]     // Catch:{ MalformedURLException -> 0x00b9, IOException -> 0x007c }
            r5.<init>(r6)     // Catch:{ MalformedURLException -> 0x00b9, IOException -> 0x007c }
            java.net.URLConnection r6 = r13.openConnection(r5)     // Catch:{ MalformedURLException -> 0x00b9, IOException -> 0x007c }
            java.net.HttpURLConnection r6 = (java.net.HttpURLConnection) r6     // Catch:{ MalformedURLException -> 0x00b9, IOException -> 0x007c }
            r2 = r6
            r6 = 0
            r2.setInstanceFollowRedirects(r6)     // Catch:{ MalformedURLException -> 0x00b9, IOException -> 0x007c }
            r7 = 10000(0x2710, float:1.4013E-41)
            r2.setConnectTimeout(r7)     // Catch:{ MalformedURLException -> 0x00b9, IOException -> 0x007c }
            r2.setReadTimeout(r7)     // Catch:{ MalformedURLException -> 0x00b9, IOException -> 0x007c }
            r2.setUseCaches(r6)     // Catch:{ MalformedURLException -> 0x00b9, IOException -> 0x007c }
            long r6 = android.os.SystemClock.elapsedRealtime()     // Catch:{ MalformedURLException -> 0x00b9, IOException -> 0x007c }
            int r8 = r2.getResponseCode()     // Catch:{ MalformedURLException -> 0x00b9, IOException -> 0x007c }
            r3 = r8
            long r8 = android.os.SystemClock.elapsedRealtime()     // Catch:{ MalformedURLException -> 0x00b9, IOException -> 0x007c }
            java.lang.StringBuilder r10 = new java.lang.StringBuilder     // Catch:{ MalformedURLException -> 0x00b9, IOException -> 0x007c }
            r10.<init>()     // Catch:{ MalformedURLException -> 0x00b9, IOException -> 0x007c }
            java.lang.String r11 = "PROBE_HTTP "
            r10.append(r11)     // Catch:{ MalformedURLException -> 0x00b9, IOException -> 0x007c }
            r10.append(r5)     // Catch:{ MalformedURLException -> 0x00b9, IOException -> 0x007c }
            java.lang.String r11 = " time="
            r10.append(r11)     // Catch:{ MalformedURLException -> 0x00b9, IOException -> 0x007c }
            long r11 = r8 - r6
            r10.append(r11)     // Catch:{ MalformedURLException -> 0x00b9, IOException -> 0x007c }
            java.lang.String r11 = "ms ret="
            r10.append(r11)     // Catch:{ MalformedURLException -> 0x00b9, IOException -> 0x007c }
            r10.append(r3)     // Catch:{ MalformedURLException -> 0x00b9, IOException -> 0x007c }
            java.lang.String r11 = " headers="
            r10.append(r11)     // Catch:{ MalformedURLException -> 0x00b9, IOException -> 0x007c }
            java.util.Map r11 = r2.getHeaderFields()     // Catch:{ MalformedURLException -> 0x00b9, IOException -> 0x007c }
            r10.append(r11)     // Catch:{ MalformedURLException -> 0x00b9, IOException -> 0x007c }
            java.lang.String r10 = r10.toString()     // Catch:{ MalformedURLException -> 0x00b9, IOException -> 0x007c }
            r14.log(r10)     // Catch:{ MalformedURLException -> 0x00b9, IOException -> 0x007c }
        L_0x0076:
            r2.disconnect()
            goto L_0x00d6
        L_0x007a:
            r0 = move-exception
            goto L_0x00e0
        L_0x007c:
            r5 = move-exception
            java.lang.StringBuilder r6 = new java.lang.StringBuilder     // Catch:{ all -> 0x007a }
            r6.<init>()     // Catch:{ all -> 0x007a }
            java.lang.String r7 = "NetworkMonitorInjector/"
            r6.append(r7)     // Catch:{ all -> 0x007a }
            java.lang.String r7 = r13.toString()     // Catch:{ all -> 0x007a }
            r6.append(r7)     // Catch:{ all -> 0x007a }
            java.lang.String r6 = r6.toString()     // Catch:{ all -> 0x007a }
            java.lang.StringBuilder r7 = new java.lang.StringBuilder     // Catch:{ all -> 0x007a }
            r7.<init>()     // Catch:{ all -> 0x007a }
            r7.append(r0)     // Catch:{ all -> 0x007a }
            r7.append(r5)     // Catch:{ all -> 0x007a }
            java.lang.String r7 = r7.toString()     // Catch:{ all -> 0x007a }
            android.util.Log.d(r6, r7)     // Catch:{ all -> 0x007a }
            java.lang.StringBuilder r6 = new java.lang.StringBuilder     // Catch:{ all -> 0x007a }
            r6.<init>()     // Catch:{ all -> 0x007a }
            r6.append(r0)     // Catch:{ all -> 0x007a }
            r6.append(r5)     // Catch:{ all -> 0x007a }
            java.lang.String r0 = r6.toString()     // Catch:{ all -> 0x007a }
            r14.log(r0)     // Catch:{ all -> 0x007a }
            if (r2 == 0) goto L_0x00d6
            goto L_0x0076
        L_0x00b9:
            r0 = move-exception
            java.lang.String r5 = "NetworkMonitorInjector"
            java.lang.StringBuilder r6 = new java.lang.StringBuilder     // Catch:{ all -> 0x007a }
            r6.<init>()     // Catch:{ all -> 0x007a }
            java.lang.String r7 = "Invalid probe URL: "
            r6.append(r7)     // Catch:{ all -> 0x007a }
            r7 = r4[r1]     // Catch:{ all -> 0x007a }
            r6.append(r7)     // Catch:{ all -> 0x007a }
            java.lang.String r6 = r6.toString()     // Catch:{ all -> 0x007a }
            android.util.Log.d(r5, r6)     // Catch:{ all -> 0x007a }
            if (r2 == 0) goto L_0x00d6
            goto L_0x0076
        L_0x00d6:
            r0 = 200(0xc8, float:2.8E-43)
            if (r3 != r0) goto L_0x00dd
            r0 = 204(0xcc, float:2.86E-43)
            goto L_0x00df
        L_0x00dd:
            r0 = 599(0x257, float:8.4E-43)
        L_0x00df:
            return r0
        L_0x00e0:
            if (r2 == 0) goto L_0x00e5
            r2.disconnect()
        L_0x00e5:
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.connectivity.NetworkMonitorInjector.sendHttpProbe(android.net.Network, android.net.util.SharedLog):int");
    }
}
