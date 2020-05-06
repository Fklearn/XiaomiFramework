package com.miui.networkassistant.netdiagnose;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.DhcpInfo;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.provider.Settings;
import android.telephony.ServiceState;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import b.b.c.c;
import b.b.o.g.e;
import com.miui.activityutil.o;
import com.miui.networkassistant.config.SimUserInfo;
import com.miui.networkassistant.dual.SimCardHelper;
import com.miui.networkassistant.utils.DeviceUtil;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class NetworkDiagnosticsUtils {
    private static final String CN_CAPTIVE_PORTAL_SERVER = (DeviceUtil.IS_INTERNATIONAL_BUILD ? "https://connect.intl.rom.miui.com/generate_204" : "https://connect.rom.miui.com/generate_204");
    private static final boolean DBG = false;
    private static final String DEFAULT_CPATIVE_PORTAL_SERVER = "https://clients3.google.com/generate_204";
    public static final String MIDROP_APHOST_STATE_KEY = "sys_midrop_aphost";
    public static final String MIDROP_APHOST_STATE_RUNNING = "running";
    private static final int NETWORK_CLASS_2_G = 1;
    private static final int NETWORK_CLASS_3_G = 2;
    private static final int SIGNAL_GOOD = 1;
    private static final int SIGNAL_MODERATE = 2;
    private static final int SIGNAL_POOR = 3;
    private static final int SOCKET_TIMEOUT_MS = 10000;
    private static final String TAG = "NetworkDiagnostics_Utils";
    public static final String WIFI_MAC_XY_YDXJ = "04:E6:76";

    public enum NetworkState {
        UNKNOWN,
        CANCELLED,
        CONNECTED,
        BLOCKED,
        CAPTIVEPORTAL
    }

    private static class PingIpAddrTask implements Callable<Boolean> {
        InetAddress mAddress;
        int mCount;

        public PingIpAddrTask(InetAddress inetAddress, int i) {
            this.mAddress = inetAddress;
            this.mCount = i;
            if (this.mCount <= 0) {
                this.mCount = 1;
            }
        }

        public Boolean call() {
            return Boolean.valueOf(NetworkDiagnosticsUtils.pingIpAddr(this.mAddress, this.mCount));
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:43:0x0130, code lost:
        if (r1 != null) goto L_0x0132;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:44:0x0132, code lost:
        r1.disconnect();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:48:0x0154, code lost:
        if (r1 != null) goto L_0x0132;
     */
    /* JADX WARNING: Removed duplicated region for block: B:30:0x00f3  */
    /* JADX WARNING: Removed duplicated region for block: B:52:0x0162  */
    /* JADX WARNING: Unknown top exception splitter block from list: {B:40:0x0106=Splitter:B:40:0x0106, B:45:0x0136=Splitter:B:45:0x0136} */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static final com.miui.networkassistant.netdiagnose.NetworkDiagnosticsUtils.NetworkState CheckNetworkState(android.content.Context r11, java.lang.String r12) {
        /*
            java.lang.String r0 = "networkassistant_networkdiagnosticsutils"
            boolean r1 = com.miui.networkassistant.utils.PrivacyDeclareAndAllowNetworkUtil.isAllowNetwork()
            java.lang.String r2 = "NetworkDiagnostics_Utils"
            if (r1 != 0) goto L_0x0012
            java.lang.String r11 = "privacy not allow"
            android.util.Log.i(r2, r11)
            com.miui.networkassistant.netdiagnose.NetworkDiagnosticsUtils$NetworkState r11 = com.miui.networkassistant.netdiagnose.NetworkDiagnosticsUtils.NetworkState.CONNECTED
            return r11
        L_0x0012:
            com.miui.networkassistant.netdiagnose.NetworkDiagnosticsUtils$NetworkState r1 = com.miui.networkassistant.netdiagnose.NetworkDiagnosticsUtils.NetworkState.UNKNOWN
            r1 = 0
            r3 = -1
            r4 = 0
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ SocketTimeoutException -> 0x0104, IOException -> 0x0102 }
            r5.<init>()     // Catch:{ SocketTimeoutException -> 0x0104, IOException -> 0x0102 }
            java.lang.String r6 = "CheckNetworkState:server="
            r5.append(r6)     // Catch:{ SocketTimeoutException -> 0x0104, IOException -> 0x0102 }
            r5.append(r12)     // Catch:{ SocketTimeoutException -> 0x0104, IOException -> 0x0102 }
            java.lang.String r5 = r5.toString()     // Catch:{ SocketTimeoutException -> 0x0104, IOException -> 0x0102 }
            android.util.Log.d(r2, r5)     // Catch:{ SocketTimeoutException -> 0x0104, IOException -> 0x0102 }
            java.net.URL r5 = new java.net.URL     // Catch:{ SocketTimeoutException -> 0x0104, IOException -> 0x0102 }
            r5.<init>(r12)     // Catch:{ SocketTimeoutException -> 0x0104, IOException -> 0x0102 }
            java.net.URLConnection r12 = r5.openConnection()     // Catch:{ SocketTimeoutException -> 0x0104, IOException -> 0x0102 }
            java.net.HttpURLConnection r12 = (java.net.HttpURLConnection) r12     // Catch:{ SocketTimeoutException -> 0x0104, IOException -> 0x0102 }
            r12.setInstanceFollowRedirects(r4)     // Catch:{ SocketTimeoutException -> 0x00fc, IOException -> 0x00f9, all -> 0x00f7 }
            r1 = 10000(0x2710, float:1.4013E-41)
            r12.setConnectTimeout(r1)     // Catch:{ SocketTimeoutException -> 0x00fc, IOException -> 0x00f9, all -> 0x00f7 }
            r12.setReadTimeout(r1)     // Catch:{ SocketTimeoutException -> 0x00fc, IOException -> 0x00f9, all -> 0x00f7 }
            r12.setUseCaches(r4)     // Catch:{ SocketTimeoutException -> 0x00fc, IOException -> 0x00f9, all -> 0x00f7 }
            long r6 = android.os.SystemClock.elapsedRealtime()     // Catch:{ SocketTimeoutException -> 0x00fc, IOException -> 0x00f9, all -> 0x00f7 }
            r12.getInputStream()     // Catch:{ SocketTimeoutException -> 0x00fc, IOException -> 0x00f9, all -> 0x00f7 }
            long r8 = android.os.SystemClock.elapsedRealtime()     // Catch:{ SocketTimeoutException -> 0x00fc, IOException -> 0x00f9, all -> 0x00f7 }
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ SocketTimeoutException -> 0x00fc, IOException -> 0x00f9, all -> 0x00f7 }
            r1.<init>()     // Catch:{ SocketTimeoutException -> 0x00fc, IOException -> 0x00f9, all -> 0x00f7 }
            java.lang.String r10 = "CheckNetworkState: getInputStream() response time:"
            r1.append(r10)     // Catch:{ SocketTimeoutException -> 0x00fc, IOException -> 0x00f9, all -> 0x00f7 }
            long r8 = r8 - r6
            r1.append(r8)     // Catch:{ SocketTimeoutException -> 0x00fc, IOException -> 0x00f9, all -> 0x00f7 }
            java.lang.String r1 = r1.toString()     // Catch:{ SocketTimeoutException -> 0x00fc, IOException -> 0x00f9, all -> 0x00f7 }
            android.util.Log.d(r2, r1)     // Catch:{ SocketTimeoutException -> 0x00fc, IOException -> 0x00f9, all -> 0x00f7 }
            long r8 = android.os.SystemClock.elapsedRealtime()     // Catch:{ SocketTimeoutException -> 0x00fc, IOException -> 0x00f9, all -> 0x00f7 }
            int r3 = r12.getResponseCode()     // Catch:{ SocketTimeoutException -> 0x00fc, IOException -> 0x00f9, all -> 0x00f7 }
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ SocketTimeoutException -> 0x00fc, IOException -> 0x00f9, all -> 0x00f7 }
            r1.<init>()     // Catch:{ SocketTimeoutException -> 0x00fc, IOException -> 0x00f9, all -> 0x00f7 }
            java.lang.String r10 = "CheckNetworkState: fetch response time:"
            r1.append(r10)     // Catch:{ SocketTimeoutException -> 0x00fc, IOException -> 0x00f9, all -> 0x00f7 }
            long r8 = r8 - r6
            r1.append(r8)     // Catch:{ SocketTimeoutException -> 0x00fc, IOException -> 0x00f9, all -> 0x00f7 }
            java.lang.String r6 = " rspCode="
            r1.append(r6)     // Catch:{ SocketTimeoutException -> 0x00fc, IOException -> 0x00f9, all -> 0x00f7 }
            r1.append(r3)     // Catch:{ SocketTimeoutException -> 0x00fc, IOException -> 0x00f9, all -> 0x00f7 }
            java.lang.String r1 = r1.toString()     // Catch:{ SocketTimeoutException -> 0x00fc, IOException -> 0x00f9, all -> 0x00f7 }
            android.util.Log.d(r2, r1)     // Catch:{ SocketTimeoutException -> 0x00fc, IOException -> 0x00f9, all -> 0x00f7 }
            r1 = 204(0xcc, float:2.86E-43)
            r6 = 200(0xc8, float:2.8E-43)
            if (r3 != r6) goto L_0x009b
            int r7 = r12.getContentLength()     // Catch:{ SocketTimeoutException -> 0x00fc, IOException -> 0x00f9, all -> 0x00f7 }
            if (r7 != 0) goto L_0x009b
            java.lang.String r7 = "CheckNetworkState:Empty 200 response interpreted as 204 response."
            android.util.Log.d(r2, r7)     // Catch:{ SocketTimeoutException -> 0x00fc, IOException -> 0x00f9, all -> 0x00f7 }
            r3 = r1
        L_0x009b:
            if (r3 == r1) goto L_0x00c9
            r1 = 599(0x257, float:8.4E-43)
            if (r3 != r1) goto L_0x00a2
            goto L_0x00c9
        L_0x00a2:
            r11 = 302(0x12e, float:4.23E-43)
            if (r3 == r11) goto L_0x00ac
            if (r3 != r6) goto L_0x00a9
            goto L_0x00ac
        L_0x00a9:
            com.miui.networkassistant.netdiagnose.NetworkDiagnosticsUtils$NetworkState r11 = com.miui.networkassistant.netdiagnose.NetworkDiagnosticsUtils.NetworkState.BLOCKED     // Catch:{ SocketTimeoutException -> 0x00fc, IOException -> 0x00f9, all -> 0x00f7 }
            goto L_0x00e9
        L_0x00ac:
            java.lang.String r11 = "Location"
            java.lang.String r11 = r12.getHeaderField(r11)     // Catch:{ SocketTimeoutException -> 0x00fc, IOException -> 0x00f9, all -> 0x00f7 }
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ SocketTimeoutException -> 0x00fc, IOException -> 0x00f9, all -> 0x00f7 }
            r1.<init>()     // Catch:{ SocketTimeoutException -> 0x00fc, IOException -> 0x00f9, all -> 0x00f7 }
            java.lang.String r5 = "CheckNetworkState: CaptivePortal Location=:"
            r1.append(r5)     // Catch:{ SocketTimeoutException -> 0x00fc, IOException -> 0x00f9, all -> 0x00f7 }
            r1.append(r11)     // Catch:{ SocketTimeoutException -> 0x00fc, IOException -> 0x00f9, all -> 0x00f7 }
            java.lang.String r11 = r1.toString()     // Catch:{ SocketTimeoutException -> 0x00fc, IOException -> 0x00f9, all -> 0x00f7 }
            android.util.Log.d(r2, r11)     // Catch:{ SocketTimeoutException -> 0x00fc, IOException -> 0x00f9, all -> 0x00f7 }
            com.miui.networkassistant.netdiagnose.NetworkDiagnosticsUtils$NetworkState r11 = com.miui.networkassistant.netdiagnose.NetworkDiagnosticsUtils.NetworkState.CAPTIVEPORTAL     // Catch:{ SocketTimeoutException -> 0x00fc, IOException -> 0x00f9, all -> 0x00f7 }
            goto L_0x00e9
        L_0x00c9:
            com.miui.networkassistant.netdiagnose.NetworkDiagnosticsUtils$NetworkState r1 = com.miui.networkassistant.netdiagnose.NetworkDiagnosticsUtils.NetworkState.CONNECTED     // Catch:{ SocketTimeoutException -> 0x00fc, IOException -> 0x00f9, all -> 0x00f7 }
            boolean r6 = b.b.c.h.f.l(r11)     // Catch:{ SocketTimeoutException -> 0x00fc, IOException -> 0x00f9, all -> 0x00f7 }
            if (r6 == 0) goto L_0x00e8
            java.lang.String r5 = r5.getHost()     // Catch:{ SocketTimeoutException -> 0x00fc, IOException -> 0x00f9, all -> 0x00f7 }
            java.lang.String r5 = convertHostToIpAddr(r5)     // Catch:{ SocketTimeoutException -> 0x00fc, IOException -> 0x00f9, all -> 0x00f7 }
            java.net.InetAddress r11 = getGateway(r11)     // Catch:{ SocketTimeoutException -> 0x00fc, IOException -> 0x00f9, all -> 0x00f7 }
            java.lang.String r11 = r11.getHostAddress()     // Catch:{ SocketTimeoutException -> 0x00fc, IOException -> 0x00f9, all -> 0x00f7 }
            boolean r11 = android.text.TextUtils.equals(r5, r11)     // Catch:{ SocketTimeoutException -> 0x00fc, IOException -> 0x00f9, all -> 0x00f7 }
            if (r11 == 0) goto L_0x00e8
            goto L_0x00a9
        L_0x00e8:
            r11 = r1
        L_0x00e9:
            b.b.c.h.j r1 = new b.b.c.h.j
            r1.<init>(r0)
            b.b.c.h.i.a(r1, r3, r4)
            if (r12 == 0) goto L_0x0157
            r12.disconnect()
            goto L_0x0157
        L_0x00f7:
            r11 = move-exception
            goto L_0x0158
        L_0x00f9:
            r11 = move-exception
            r1 = r12
            goto L_0x0106
        L_0x00fc:
            r11 = move-exception
            r1 = r12
            goto L_0x0136
        L_0x00ff:
            r11 = move-exception
            r12 = r1
            goto L_0x0158
        L_0x0102:
            r11 = move-exception
            goto L_0x0106
        L_0x0104:
            r11 = move-exception
            goto L_0x0136
        L_0x0106:
            java.lang.StringBuilder r12 = new java.lang.StringBuilder     // Catch:{ all -> 0x00ff }
            r12.<init>()     // Catch:{ all -> 0x00ff }
            java.lang.String r5 = "CheckNetworkState:IOException:"
            r12.append(r5)     // Catch:{ all -> 0x00ff }
            r12.append(r11)     // Catch:{ all -> 0x00ff }
            java.lang.String r5 = " ioe.Message() ="
            r12.append(r5)     // Catch:{ all -> 0x00ff }
            java.lang.String r11 = r11.getMessage()     // Catch:{ all -> 0x00ff }
            r12.append(r11)     // Catch:{ all -> 0x00ff }
            java.lang.String r11 = r12.toString()     // Catch:{ all -> 0x00ff }
            android.util.Log.d(r2, r11)     // Catch:{ all -> 0x00ff }
            com.miui.networkassistant.netdiagnose.NetworkDiagnosticsUtils$NetworkState r11 = com.miui.networkassistant.netdiagnose.NetworkDiagnosticsUtils.NetworkState.BLOCKED     // Catch:{ all -> 0x00ff }
            b.b.c.h.j r12 = new b.b.c.h.j
            r12.<init>(r0)
            b.b.c.h.i.a(r12, r3, r4)
            if (r1 == 0) goto L_0x0157
        L_0x0132:
            r1.disconnect()
            goto L_0x0157
        L_0x0136:
            java.lang.StringBuilder r12 = new java.lang.StringBuilder     // Catch:{ all -> 0x00ff }
            r12.<init>()     // Catch:{ all -> 0x00ff }
            java.lang.String r5 = "CheckNetworkState:SocketTimeoutException:"
            r12.append(r5)     // Catch:{ all -> 0x00ff }
            r12.append(r11)     // Catch:{ all -> 0x00ff }
            java.lang.String r11 = r12.toString()     // Catch:{ all -> 0x00ff }
            android.util.Log.d(r2, r11)     // Catch:{ all -> 0x00ff }
            com.miui.networkassistant.netdiagnose.NetworkDiagnosticsUtils$NetworkState r11 = com.miui.networkassistant.netdiagnose.NetworkDiagnosticsUtils.NetworkState.BLOCKED     // Catch:{ all -> 0x00ff }
            b.b.c.h.j r12 = new b.b.c.h.j
            r12.<init>(r0)
            b.b.c.h.i.a(r12, r3, r4)
            if (r1 == 0) goto L_0x0157
            goto L_0x0132
        L_0x0157:
            return r11
        L_0x0158:
            b.b.c.h.j r1 = new b.b.c.h.j
            r1.<init>(r0)
            b.b.c.h.i.a(r1, r3, r4)
            if (r12 == 0) goto L_0x0165
            r12.disconnect()
        L_0x0165:
            throw r11
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.networkassistant.netdiagnose.NetworkDiagnosticsUtils.CheckNetworkState(android.content.Context, java.lang.String):com.miui.networkassistant.netdiagnose.NetworkDiagnosticsUtils$NetworkState");
    }

    public static String convertHostToIpAddr(String str) {
        try {
            InetAddress byName = InetAddress.getByName(str);
            if (byName == null) {
                return "";
            }
            Log.i(TAG, "convertHostToIpAddr " + str + ":" + byName.getHostAddress());
            return byName.getHostAddress();
        } catch (Exception e) {
            Log.e(TAG, "convertHostToIpAddr", e);
            return "";
        }
    }

    public static void doExec(String str) {
        StringBuffer stringBuffer = new StringBuffer();
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(Runtime.getRuntime().exec(str).getInputStream()));
            while (true) {
                String readLine = bufferedReader.readLine();
                if (readLine != null) {
                    stringBuffer.append(readLine + "\n");
                } else {
                    Log.i(TAG, "doExec " + str + ":\n" + stringBuffer.toString());
                    return;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static final String getCaptivePortalServer(Context context) {
        return CN_CAPTIVE_PORTAL_SERVER;
    }

    /* JADX WARNING: Removed duplicated region for block: B:11:0x001d  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static java.lang.String getConfFile(java.lang.String r3) {
        /*
            r0 = 0
            java.io.BufferedReader r1 = new java.io.BufferedReader     // Catch:{ all -> 0x001a }
            java.io.FileReader r2 = new java.io.FileReader     // Catch:{ all -> 0x001a }
            r2.<init>(r3)     // Catch:{ all -> 0x001a }
            r1.<init>(r2)     // Catch:{ all -> 0x001a }
            java.lang.String r3 = r1.readLine()     // Catch:{ all -> 0x0017 }
            java.lang.String r3 = r3.trim()     // Catch:{ all -> 0x0017 }
            r1.close()
            return r3
        L_0x0017:
            r3 = move-exception
            r0 = r1
            goto L_0x001b
        L_0x001a:
            r3 = move-exception
        L_0x001b:
            if (r0 == 0) goto L_0x0020
            r0.close()
        L_0x0020:
            throw r3
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.networkassistant.netdiagnose.NetworkDiagnosticsUtils.getConfFile(java.lang.String):java.lang.String");
    }

    public static InetAddress getCurrentNetworkIp(int i) {
        try {
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            while (networkInterfaces.hasMoreElements()) {
                NetworkInterface nextElement = networkInterfaces.nextElement();
                if (nextElement.isUp()) {
                    Enumeration<InetAddress> inetAddresses = nextElement.getInetAddresses();
                    while (inetAddresses.hasMoreElements()) {
                        InetAddress nextElement2 = inetAddresses.nextElement();
                        if (!nextElement2.isLoopbackAddress() && !nextElement2.isLinkLocalAddress()) {
                            if (i == c.f1620c && (nextElement2 instanceof Inet6Address)) {
                                return nextElement2;
                            }
                            if (i == c.f1619b && (nextElement2 instanceof Inet4Address)) {
                                return nextElement2;
                            }
                        }
                    }
                    continue;
                }
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static int getDataRat(ServiceState serviceState) {
        int i = 0;
        try {
            i = ((Integer) e.a((Object) serviceState, "getDataNetworkType", (Class<?>[]) null, new Object[0])).intValue();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.i(TAG, "getDataRat=" + i);
        return i;
    }

    public static final String getDefaultCaptivePortalServer() {
        return DEFAULT_CPATIVE_PORTAL_SERVER;
    }

    public static InetAddress getGateway(Context context) {
        try {
            WifiManager wifiManager = (WifiManager) context.getSystemService("wifi");
            if (wifiManager != null) {
                if (wifiManager.isWifiEnabled()) {
                    DhcpInfo dhcpInfo = wifiManager.getDhcpInfo();
                    if (dhcpInfo == null) {
                        return null;
                    }
                    return intToInetAddress(dhcpInfo.gateway);
                }
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static int getLteRsrqLevel(int i) {
        if (i >= -10) {
            return 1;
        }
        if (i >= -15) {
            return 2;
        }
        return i >= -20 ? 3 : -1;
    }

    public static int getSignalLevel(SignalStrength signalStrength) {
        return signalStrength.getLevel();
    }

    public static int getSignalLteRsrq(SignalStrength signalStrength) {
        try {
            return ((Integer) e.a((Object) signalStrength, "getLteRsrq", (Class<?>[]) null, new Object[0])).intValue();
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public static int getWifiState(Context context) {
        WifiManager wifiManager = (WifiManager) context.getSystemService("wifi");
        if (wifiManager == null) {
            return 4;
        }
        return wifiManager.getWifiState();
    }

    public static InetAddress intToInetAddress(int i) {
        try {
            return InetAddress.getByAddress(new byte[]{(byte) (i & 255), (byte) ((i >> 8) & 255), (byte) ((i >> 16) & 255), (byte) ((i >> 24) & 255)});
        } catch (UnknownHostException unused) {
            throw new AssertionError();
        }
    }

    public static String intToStrIpAddress(int i) {
        return String.format(Locale.US, "%d.%d.%d.%d", new Object[]{Integer.valueOf(i & 255), Integer.valueOf((i >> 8) & 255), Integer.valueOf((i >> 16) & 255), Integer.valueOf((i >> 24) & 255)});
    }

    public static boolean isCnUser(Context context) {
        SimUserInfo instance;
        boolean z = !DeviceUtil.IS_INTERNATIONAL_BUILD;
        SimCardHelper instance2 = SimCardHelper.getInstance(context);
        if (instance2 == null || !instance2.isSimInserted() || (instance = SimUserInfo.getInstance(context, instance2.getCurrentMobileSlotNum())) == null || !instance.isOversea()) {
            return z;
        }
        return false;
    }

    public static boolean isIgnoreCheckWifiState(Context context) {
        WifiInfo connectionInfo;
        String str;
        if (((ConnectivityManager) context.getSystemService("connectivity")).isActiveNetworkMetered()) {
            str = "metered network!!";
        } else if (isMiDropApHostRunning(context)) {
            str = "Midrop ap host!!!!";
        } else {
            WifiManager wifiManager = (WifiManager) context.getSystemService("wifi");
            if (!(wifiManager == null || (connectionInfo = wifiManager.getConnectionInfo()) == null)) {
                String bssid = connectionInfo.getBSSID();
                if (!TextUtils.isEmpty(bssid) && bssid.toUpperCase().startsWith(WIFI_MAC_XY_YDXJ)) {
                    str = "xiaoyi ap host!!!!";
                }
            }
            return false;
        }
        Log.i(TAG, str);
        return true;
    }

    public static boolean isIgnoreIcmp() {
        try {
            return !TextUtils.equals(o.f2309a, getConfFile("/proc/sys/net/ipv4/icmp_echo_ignore_all"));
        } catch (Exception e) {
            Log.e(TAG, "isIgnoreIcmp", e);
            return true;
        }
    }

    public static Boolean isIpAvailable(InetAddress inetAddress, int i) {
        try {
            PingIpAddrTask pingIpAddrTask = new PingIpAddrTask(inetAddress, i);
            ExecutorService newSingleThreadExecutor = Executors.newSingleThreadExecutor();
            List invokeAll = newSingleThreadExecutor.invokeAll(Arrays.asList(new Callable[]{pingIpAddrTask}), 3000, TimeUnit.MILLISECONDS);
            newSingleThreadExecutor.shutdown();
            Future future = (Future) invokeAll.get(0);
            if (future.isCancelled()) {
                return false;
            }
            return (Boolean) future.get();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean isMiDropApHostRunning(Context context) {
        return MIDROP_APHOST_STATE_RUNNING.equals(Settings.System.getString(context.getContentResolver(), MIDROP_APHOST_STATE_KEY));
    }

    public static boolean isNonEutran(int i) {
        int i2;
        try {
            i2 = ((Integer) e.a((Class<?>) TelephonyManager.class, "getNetworkClass", (Class<?>[]) new Class[]{Integer.TYPE}, Integer.valueOf(i))).intValue();
        } catch (Exception e) {
            e.printStackTrace();
            i2 = 0;
        }
        return i2 == 1 || i2 == 2;
    }

    public static boolean isPoorLteRsrq(int i) {
        Log.i(TAG, "isPoorLteRsrq getLteRsrqLevel(lteRsrq)=" + getLteRsrqLevel(i) + ",lteRsrq=" + i);
        return 3 == getLteRsrqLevel(i);
    }

    public static boolean pingIpAddr(InetAddress inetAddress, int i) {
        StringBuilder sb;
        StringBuilder sb2;
        if (inetAddress == null) {
            return false;
        }
        try {
            if (inetAddress instanceof Inet4Address) {
                sb2 = new StringBuilder();
                sb2.append("ping -c ");
                sb2.append(i);
                sb2.append(" -w 5 ");
                sb2.append(inetAddress.getHostAddress());
            } else {
                sb2 = new StringBuilder();
                sb2.append("ping6 -c ");
                sb2.append(i);
                sb2.append(" -w 5 ");
                sb2.append(inetAddress.getHostAddress());
            }
            return Runtime.getRuntime().exec(sb2.toString()).waitFor() == 0;
        } catch (IOException e) {
            e = e;
            sb = new StringBuilder();
            sb.append("pingIpAddr an exception occurred.");
            sb.append(e);
            Log.d(TAG, sb.toString());
            return false;
        } catch (InterruptedException e2) {
            e = e2;
            sb = new StringBuilder();
            sb.append("pingIpAddr an exception occurred.");
            sb.append(e);
            Log.d(TAG, sb.toString());
            return false;
        }
    }

    public static void printNetworkInfo(Context context) {
        NetworkInfo activeNetworkInfo;
        new StringBuffer();
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService("connectivity");
        if (connectivityManager != null && (activeNetworkInfo = connectivityManager.getActiveNetworkInfo()) != null) {
            Log.i(TAG, "activeNetInfo:\n " + activeNetworkInfo.toString());
        }
    }
}
