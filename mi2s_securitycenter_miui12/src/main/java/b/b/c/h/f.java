package b.b.c.h;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.LinkProperties;
import android.net.NetworkInfo;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;
import b.b.o.g.e;
import com.miui.networkassistant.utils.DeviceUtil;
import com.miui.securitycenter.h;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.util.List;
import miui.os.FileUtils;
import miui.securitycenter.NetworkUtils;
import miui.securitycenter.utils.MiAssistantUtil;
import miui.util.IOUtils;

public class f {

    /* renamed from: a  reason: collision with root package name */
    private static final String f1728a = "f";

    public enum a {
        Inited,
        Diconnected,
        WifiConnected,
        MobileConnected,
        EthernetConnected,
        BluetoothConnected,
        OtherConnected,
        WifiApConnected
    }

    public static WifiConfiguration a(Context context) {
        WifiManager wifiManager;
        WifiInfo connectionInfo;
        List<WifiConfiguration> configuredNetworks;
        if (context == null || (wifiManager = (WifiManager) context.getSystemService("wifi")) == null || (connectionInfo = wifiManager.getConnectionInfo()) == null || (configuredNetworks = wifiManager.getConfiguredNetworks()) == null) {
            return null;
        }
        for (WifiConfiguration next : configuredNetworks) {
            if (next.networkId == connectionInfo.getNetworkId()) {
                return next;
            }
        }
        return null;
    }

    public static File a(String str, String str2, j jVar) {
        BufferedInputStream bufferedInputStream = null;
        if (!h.i()) {
            return null;
        }
        HttpURLConnection httpURLConnection = (HttpURLConnection) new URL(str).openConnection();
        httpURLConnection.setConnectTimeout(5000);
        int i = -1;
        try {
            i = httpURLConnection.getResponseCode();
            String headerField = httpURLConnection.getHeaderField("Content-Disposition");
            String str3 = f1728a;
            Log.d(str3, "contentVal : " + headerField + ", responseCode : " + i);
            if (headerField == null || i != 200) {
                i.a(jVar, i, 0);
                IOUtils.closeQuietly((InputStream) null);
                httpURLConnection.disconnect();
                return null;
            }
            BufferedInputStream bufferedInputStream2 = new BufferedInputStream(httpURLConnection.getInputStream());
            try {
                File file = new File(str2, headerField.replaceAll(".*filename=", ""));
                FileUtils.copyToFile(bufferedInputStream2, file);
                i.a(jVar, i, 0);
                IOUtils.closeQuietly(bufferedInputStream2);
                httpURLConnection.disconnect();
                return file;
            } catch (Throwable th) {
                th = th;
                bufferedInputStream = bufferedInputStream2;
                i.a(jVar, i, 0);
                IOUtils.closeQuietly(bufferedInputStream);
                httpURLConnection.disconnect();
                throw th;
            }
        } catch (Throwable th2) {
            th = th2;
            i.a(jVar, i, 0);
            IOUtils.closeQuietly(bufferedInputStream);
            httpURLConnection.disconnect();
            throw th;
        }
    }

    public static Boolean a(Context context, InetAddress inetAddress, Long l) {
        if (!(context == null || inetAddress == null || !DeviceUtil.IS_M_OR_LATER)) {
            try {
                return (Boolean) e.a(Class.forName("miui.securitycenter.net.NetworkDiagnostics"), Boolean.class, "activeNetworkDnsCheck", (Class<?>[]) new Class[]{Context.class, InetAddress.class, Long.class}, context, inetAddress, l);
            } catch (Exception e) {
                Log.e(f1728a, "activeNetworkIcmpCheck, an exception occurred!", e);
            }
        }
        return false;
    }

    /* JADX WARNING: Removed duplicated region for block: B:18:0x0037  */
    /* JADX WARNING: Removed duplicated region for block: B:20:0x0059  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static java.net.InetAddress a(android.content.Context r3, android.net.wifi.WifiConfiguration r4) {
        /*
            r0 = 0
            if (r3 == 0) goto L_0x0061
            if (r4 != 0) goto L_0x0006
            goto L_0x0061
        L_0x0006:
            boolean r3 = com.miui.networkassistant.utils.DeviceUtil.IS_L_OR_LATER
            if (r3 == 0) goto L_0x001d
            java.lang.String r3 = "mIpConfiguration"
            java.lang.Object r3 = b.b.o.g.e.a((java.lang.Object) r4, (java.lang.String) r3)
            if (r3 == 0) goto L_0x002e
            java.lang.String r4 = "staticIpConfiguration"
            java.lang.Object r3 = b.b.o.g.e.a((java.lang.Object) r3, (java.lang.String) r4)
            if (r3 == 0) goto L_0x002e
            java.lang.String r4 = "dnsServers"
            goto L_0x0027
        L_0x001d:
            java.lang.String r3 = "linkProperties"
            java.lang.Object r3 = b.b.o.g.e.a((java.lang.Object) r4, (java.lang.String) r3)
            if (r3 == 0) goto L_0x002e
            java.lang.String r4 = "mDnses"
        L_0x0027:
            java.lang.Object r3 = b.b.o.g.e.a((java.lang.Object) r3, (java.lang.String) r4)
            java.util.ArrayList r3 = (java.util.ArrayList) r3
            goto L_0x002f
        L_0x002e:
            r3 = r0
        L_0x002f:
            if (r3 == 0) goto L_0x0059
            int r4 = r3.size()
            if (r4 <= 0) goto L_0x0059
            java.lang.String r4 = f1728a
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r1 = "getDns:"
            r0.append(r1)
            r1 = 0
            java.lang.Object r2 = r3.get(r1)
            r0.append(r2)
            java.lang.String r0 = r0.toString()
            android.util.Log.i(r4, r0)
            java.lang.Object r3 = r3.get(r1)
            java.net.InetAddress r3 = (java.net.InetAddress) r3
            return r3
        L_0x0059:
            java.lang.String r3 = f1728a
            java.lang.String r4 = "getDns: null"
        L_0x005d:
            android.util.Log.i(r3, r4)
            return r0
        L_0x0061:
            java.lang.String r3 = f1728a
            java.lang.String r4 = "getDns:  invalidate parameter!"
            goto L_0x005d
        */
        throw new UnsupportedOperationException("Method not decompiled: b.b.c.h.f.a(android.content.Context, android.net.wifi.WifiConfiguration):java.net.InetAddress");
    }

    public static boolean a(LinkProperties linkProperties) {
        if (linkProperties != null && DeviceUtil.IS_L_OR_LATER) {
            try {
                return ((Boolean) e.a((Object) linkProperties, Boolean.class, "hasGlobalIPv6Address", (Class<?>[]) null, new Object[0])).booleanValue();
            } catch (Exception e) {
                Log.e(f1728a, "hasGlobalIPv6Address, an exception occurred!", e);
            }
        }
        return false;
    }

    public static Boolean b(Context context, InetAddress inetAddress, Long l) {
        if (!(context == null || inetAddress == null || !DeviceUtil.IS_M_OR_LATER)) {
            try {
                return (Boolean) e.a(Class.forName("miui.securitycenter.net.NetworkDiagnostics"), Boolean.class, "activeNetworkIcmpCheck", (Class<?>[]) new Class[]{Context.class, InetAddress.class, Long.class}, context, inetAddress, l);
            } catch (Exception e) {
                Log.e(f1728a, "activeNetworkIcmpCheck, an exception occurred!", e);
            }
        }
        return false;
    }

    public static String b(Context context) {
        return MiAssistantUtil.getActiveInterfaceName();
    }

    public static boolean b(LinkProperties linkProperties) {
        if (linkProperties != null && DeviceUtil.IS_L_OR_LATER) {
            try {
                return ((Boolean) e.a((Object) linkProperties, Boolean.class, "hasIPv6DefaultRoute", (Class<?>[]) null, new Object[0])).booleanValue();
            } catch (Exception e) {
                Log.e(f1728a, "hasIPv6DefaultRoute, an exception occurred!", e);
            }
        }
        return false;
    }

    public static a c(Context context) {
        NetworkInfo activeNetworkInfo = ((ConnectivityManager) context.getSystemService("connectivity")).getActiveNetworkInfo();
        if (activeNetworkInfo == null || !activeNetworkInfo.isConnected()) {
            return k(context) ? a.WifiApConnected : a.Diconnected;
        }
        int type = activeNetworkInfo.getType();
        if (type != 0) {
            if (type == 1) {
                return a.WifiConnected;
            }
            if (!(type == 2 || type == 3 || type == 4 || type == 5)) {
                return type != 7 ? type != 9 ? a.OtherConnected : a.EthernetConnected : a.BluetoothConnected;
            }
        }
        return a.MobileConnected;
    }

    public static String d(Context context) {
        Object a2;
        if (DeviceUtil.IS_L_OR_LATER && context != null) {
            try {
                WifiConfiguration a3 = a(context);
                if (!(a3 == null || (a2 = e.a((Object) a3, Object.class, "getIpAssignment", (Class<?>[]) null, new Object[0])) == null)) {
                    return a2.toString();
                }
            } catch (Exception e) {
                Log.e(f1728a, "getIpAssignment, an exception occurred!", e);
            }
        }
        return null;
    }

    public static boolean e(Context context) {
        try {
            return ((Boolean) e.a((Object) (ConnectivityManager) context.getSystemService("connectivity"), "getMobileDataEnabled", (Class<?>[]) null, new Object[0])).booleanValue();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static String f(Context context) {
        return NetworkUtils.getMobileIface(context);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:2:0x000a, code lost:
        r1 = r1.getConnectionInfo();
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static java.lang.String g(android.content.Context r1) {
        /*
            java.lang.String r0 = "wifi"
            java.lang.Object r1 = r1.getSystemService(r0)
            android.net.wifi.WifiManager r1 = (android.net.wifi.WifiManager) r1
            if (r1 == 0) goto L_0x0015
            android.net.wifi.WifiInfo r1 = r1.getConnectionInfo()
            if (r1 == 0) goto L_0x0015
            java.lang.String r1 = r1.getSSID()
            goto L_0x0017
        L_0x0015:
            java.lang.String r1 = ""
        L_0x0017:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: b.b.c.h.f.g(android.content.Context):java.lang.String");
    }

    public static boolean h(Context context) {
        return c(context) == a.EthernetConnected;
    }

    public static boolean i(Context context) {
        return c(context) == a.MobileConnected;
    }

    public static boolean j(Context context) {
        return c(context) != a.Diconnected;
    }

    public static boolean k(Context context) {
        try {
            return ((Boolean) e.a((Object) (WifiManager) context.getSystemService("wifi"), "isWifiApEnabled", (Class<?>[]) null, new Object[0])).booleanValue();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean l(Context context) {
        return c(context) == a.WifiConnected;
    }

    public static boolean m(Context context) {
        return ((WifiManager) context.getSystemService("wifi")).isWifiEnabled();
    }
}
