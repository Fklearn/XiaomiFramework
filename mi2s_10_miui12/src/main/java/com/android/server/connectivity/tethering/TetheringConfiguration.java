package com.android.server.connectivity.tethering;

import android.content.Context;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.util.SharedLog;
import android.os.SystemProperties;
import android.provider.Settings;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import com.android.internal.annotations.VisibleForTesting;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.StringJoiner;

public class TetheringConfiguration {
    private static final String[] EMPTY_STRING_ARRAY = new String[0];
    private static final String[] LEGACY_DHCP_DEFAULT_RANGE = {"192.168.42.2", "192.168.42.254", "192.168.43.2", "192.168.43.254", "192.168.44.2", "192.168.44.254", "192.168.45.2", "192.168.45.254", "192.168.46.2", "192.168.46.254", "192.168.47.2", "192.168.47.254", "192.168.48.2", "192.168.48.254", "192.168.49.2", "192.168.49.254"};
    private static final String TAG = TetheringConfiguration.class.getSimpleName();
    private static String fstInterfaceName = "bond0";
    private final String[] DEFAULT_IPV4_DNS = {"8.8.4.4", "8.8.8.8"};
    public final boolean chooseUpstreamAutomatically;
    public final String[] defaultIPv4DNS;
    public final boolean enableLegacyDhcpServer;
    public final boolean isDunRequired;
    public final String[] legacyDhcpRanges;
    public final Collection<Integer> preferredUpstreamIfaceTypes;
    public final String[] provisioningApp;
    public final String provisioningAppNoUi;
    public final int provisioningCheckPeriod;
    public final int subId;
    public final String[] tetherableBluetoothRegexs;
    public final String[] tetherableUsbRegexs;
    public final String[] tetherableWifiRegexs;

    public TetheringConfiguration(Context ctx, SharedLog log, int id) {
        SharedLog configLog = log.forSubComponent("config");
        this.subId = id;
        Resources res = getResources(ctx, this.subId);
        this.tetherableUsbRegexs = getResourceStringArray(res, 17236077);
        if (SystemProperties.getInt("persist.vendor.fst.softap.en", 0) == 1) {
            this.tetherableWifiRegexs = new String[]{fstInterfaceName};
        } else {
            this.tetherableWifiRegexs = getResourceStringArray(res, 17236078);
        }
        this.tetherableBluetoothRegexs = getResourceStringArray(res, 17236074);
        this.isDunRequired = checkDunRequired(ctx);
        this.chooseUpstreamAutomatically = getResourceBoolean(res, 17891555);
        this.preferredUpstreamIfaceTypes = getUpstreamIfaceTypes(res, this.isDunRequired);
        this.legacyDhcpRanges = getLegacyDhcpRanges(res);
        this.defaultIPv4DNS = copy(this.DEFAULT_IPV4_DNS);
        this.enableLegacyDhcpServer = getEnableLegacyDhcpServer(ctx);
        this.provisioningApp = getResourceStringArray(res, 17236042);
        this.provisioningAppNoUi = getProvisioningAppNoUi(res);
        this.provisioningCheckPeriod = getResourceInteger(res, 17694846, 0);
        configLog.log(toString());
    }

    public static void setFstInterfaceName(String name) {
        fstInterfaceName = name;
    }

    public static String getFstInterfaceName() {
        return fstInterfaceName;
    }

    public boolean isUsb(String iface) {
        return matchesDownstreamRegexs(iface, this.tetherableUsbRegexs);
    }

    public boolean isWifi(String iface) {
        return matchesDownstreamRegexs(iface, this.tetherableWifiRegexs);
    }

    public boolean isBluetooth(String iface) {
        return matchesDownstreamRegexs(iface, this.tetherableBluetoothRegexs);
    }

    public boolean hasMobileHotspotProvisionApp() {
        return !TextUtils.isEmpty(this.provisioningAppNoUi);
    }

    public void dump(PrintWriter pw) {
        pw.print("subId: ");
        pw.println(this.subId);
        dumpStringArray(pw, "tetherableUsbRegexs", this.tetherableUsbRegexs);
        dumpStringArray(pw, "tetherableWifiRegexs", this.tetherableWifiRegexs);
        dumpStringArray(pw, "tetherableBluetoothRegexs", this.tetherableBluetoothRegexs);
        pw.print("isDunRequired: ");
        pw.println(this.isDunRequired);
        pw.print("chooseUpstreamAutomatically: ");
        pw.println(this.chooseUpstreamAutomatically);
        dumpStringArray(pw, "preferredUpstreamIfaceTypes", preferredUpstreamNames(this.preferredUpstreamIfaceTypes));
        dumpStringArray(pw, "legacyDhcpRanges", this.legacyDhcpRanges);
        dumpStringArray(pw, "defaultIPv4DNS", this.defaultIPv4DNS);
        dumpStringArray(pw, "provisioningApp", this.provisioningApp);
        pw.print("provisioningAppNoUi: ");
        pw.println(this.provisioningAppNoUi);
        pw.print("enableLegacyDhcpServer: ");
        pw.println(this.enableLegacyDhcpServer);
    }

    public String toString() {
        StringJoiner sj = new StringJoiner(" ");
        sj.add(String.format("subId:%d", new Object[]{Integer.valueOf(this.subId)}));
        sj.add(String.format("tetherableUsbRegexs:%s", new Object[]{makeString(this.tetherableUsbRegexs)}));
        sj.add(String.format("tetherableWifiRegexs:%s", new Object[]{makeString(this.tetherableWifiRegexs)}));
        sj.add(String.format("tetherableBluetoothRegexs:%s", new Object[]{makeString(this.tetherableBluetoothRegexs)}));
        sj.add(String.format("isDunRequired:%s", new Object[]{Boolean.valueOf(this.isDunRequired)}));
        sj.add(String.format("chooseUpstreamAutomatically:%s", new Object[]{Boolean.valueOf(this.chooseUpstreamAutomatically)}));
        sj.add(String.format("preferredUpstreamIfaceTypes:%s", new Object[]{makeString(preferredUpstreamNames(this.preferredUpstreamIfaceTypes))}));
        sj.add(String.format("provisioningApp:%s", new Object[]{makeString(this.provisioningApp)}));
        sj.add(String.format("provisioningAppNoUi:%s", new Object[]{this.provisioningAppNoUi}));
        sj.add(String.format("enableLegacyDhcpServer:%s", new Object[]{Boolean.valueOf(this.enableLegacyDhcpServer)}));
        return String.format("TetheringConfiguration{%s}", new Object[]{sj.toString()});
    }

    private static void dumpStringArray(PrintWriter pw, String label, String[] values) {
        pw.print(label);
        pw.print(": ");
        if (values != null) {
            StringJoiner sj = new StringJoiner(", ", "[", "]");
            for (String value : values) {
                sj.add(value);
            }
            pw.print(sj.toString());
        } else {
            pw.print("null");
        }
        pw.println();
    }

    private static String makeString(String[] strings) {
        if (strings == null) {
            return "null";
        }
        StringJoiner sj = new StringJoiner(",", "[", "]");
        for (String s : strings) {
            sj.add(s);
        }
        return sj.toString();
    }

    private static String[] preferredUpstreamNames(Collection<Integer> upstreamTypes) {
        String[] upstreamNames = null;
        if (upstreamTypes != null) {
            upstreamNames = new String[upstreamTypes.size()];
            int i = 0;
            for (Integer netType : upstreamTypes) {
                upstreamNames[i] = ConnectivityManager.getNetworkTypeName(netType.intValue());
                i++;
            }
        }
        return upstreamNames;
    }

    public static boolean checkDunRequired(Context ctx) {
        TelephonyManager tm = (TelephonyManager) ctx.getSystemService("phone");
        if (tm != null) {
            return tm.getTetherApnRequired();
        }
        return false;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:6:0x001e, code lost:
        if (r8 != 5) goto L_0x0027;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static java.util.Collection<java.lang.Integer> getUpstreamIfaceTypes(android.content.res.Resources r9, boolean r10) {
        /*
            r0 = 17236076(0x107006c, float:2.4795887E-38)
            int[] r0 = r9.getIntArray(r0)
            java.util.ArrayList r1 = new java.util.ArrayList
            int r2 = r0.length
            r1.<init>(r2)
            int r2 = r0.length
            r3 = 0
            java.lang.Integer r4 = java.lang.Integer.valueOf(r3)
            r5 = r3
        L_0x0014:
            r6 = 4
            r7 = 5
            if (r5 >= r2) goto L_0x0031
            r8 = r0[r5]
            if (r8 == 0) goto L_0x0024
            if (r8 == r6) goto L_0x0021
            if (r8 == r7) goto L_0x0024
            goto L_0x0027
        L_0x0021:
            if (r10 != 0) goto L_0x0027
            goto L_0x002e
        L_0x0024:
            if (r10 == 0) goto L_0x0027
            goto L_0x002e
        L_0x0027:
            java.lang.Integer r6 = java.lang.Integer.valueOf(r8)
            r1.add(r6)
        L_0x002e:
            int r5 = r5 + 1
            goto L_0x0014
        L_0x0031:
            if (r10 == 0) goto L_0x0037
            appendIfNotPresent(r1, r6)
            goto L_0x0053
        L_0x0037:
            r2 = 2
            java.lang.Integer[] r2 = new java.lang.Integer[r2]
            r2[r3] = r4
            java.lang.Integer r3 = java.lang.Integer.valueOf(r7)
            r5 = 1
            r2[r5] = r3
            boolean r2 = containsOneOf(r1, r2)
            if (r2 != 0) goto L_0x0053
            r1.add(r4)
            java.lang.Integer r2 = java.lang.Integer.valueOf(r7)
            r1.add(r2)
        L_0x0053:
            r2 = 9
            prependIfNotPresent(r1, r2)
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.connectivity.tethering.TetheringConfiguration.getUpstreamIfaceTypes(android.content.res.Resources, boolean):java.util.Collection");
    }

    private static boolean matchesDownstreamRegexs(String iface, String[] regexs) {
        for (String regex : regexs) {
            if (iface.matches(regex)) {
                return true;
            }
        }
        return false;
    }

    private static String[] getLegacyDhcpRanges(Resources res) {
        String[] fromResource = getResourceStringArray(res, 17236075);
        if (fromResource.length <= 0 || fromResource.length % 2 != 0) {
            return copy(LEGACY_DHCP_DEFAULT_RANGE);
        }
        return fromResource;
    }

    private static String getProvisioningAppNoUi(Resources res) {
        try {
            return res.getString(17039780);
        } catch (Resources.NotFoundException e) {
            return "";
        }
    }

    private static boolean getResourceBoolean(Resources res, int resId) {
        try {
            return res.getBoolean(resId);
        } catch (Resources.NotFoundException e) {
            return false;
        }
    }

    private static String[] getResourceStringArray(Resources res, int resId) {
        try {
            String[] strArray = res.getStringArray(resId);
            return strArray != null ? strArray : EMPTY_STRING_ARRAY;
        } catch (Resources.NotFoundException e) {
            return EMPTY_STRING_ARRAY;
        }
    }

    private static int getResourceInteger(Resources res, int resId, int defaultValue) {
        try {
            return res.getInteger(resId);
        } catch (Resources.NotFoundException e) {
            return defaultValue;
        }
    }

    private static boolean getEnableLegacyDhcpServer(Context ctx) {
        if (Settings.Global.getInt(ctx.getContentResolver(), "tether_enable_legacy_dhcp_server", 0) != 0) {
            return true;
        }
        return false;
    }

    private Resources getResources(Context ctx, int subId2) {
        if (subId2 != -1) {
            return getResourcesForSubIdWrapper(ctx, subId2);
        }
        return ctx.getResources();
    }

    /* access modifiers changed from: protected */
    @VisibleForTesting
    public Resources getResourcesForSubIdWrapper(Context ctx, int subId2) {
        return SubscriptionManager.getResourcesForSubId(ctx, subId2);
    }

    private static String[] copy(String[] strarray) {
        return (String[]) Arrays.copyOf(strarray, strarray.length);
    }

    private static void prependIfNotPresent(ArrayList<Integer> list, int value) {
        if (!list.contains(Integer.valueOf(value))) {
            list.add(0, Integer.valueOf(value));
        }
    }

    private static void appendIfNotPresent(ArrayList<Integer> list, int value) {
        if (!list.contains(Integer.valueOf(value))) {
            list.add(Integer.valueOf(value));
        }
    }

    private static boolean containsOneOf(ArrayList<Integer> list, Integer... values) {
        for (Integer value : values) {
            if (list.contains(value)) {
                return true;
            }
        }
        return false;
    }
}
