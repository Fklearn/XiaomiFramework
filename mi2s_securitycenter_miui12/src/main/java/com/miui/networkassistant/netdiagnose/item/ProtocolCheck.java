package com.miui.networkassistant.netdiagnose.item;

import android.content.Context;
import android.net.wifi.WifiManager;
import b.b.c.h.f;
import com.miui.networkassistant.netdiagnose.AbstractNetworkDiagoneItem;
import com.miui.securitycenter.R;
import java.net.InetAddress;

public class ProtocolCheck extends AbstractNetworkDiagoneItem {
    private static final long ICMP_CHECK_TIMEPUT = 5000;
    private static final String TAG = "NetworkDiagnostics";
    private InetAddress mLocalIpAddress;
    private boolean mPingLocalRet = false;
    boolean mPingLoopBackRet = false;
    private String mSummary = "";

    public ProtocolCheck(Context context) {
        super(context);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:51:0x0130, code lost:
        if (r14.mPingLocalRet == false) goto L_0x0155;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:57:0x0153, code lost:
        if (r14.mPingLocalRet == false) goto L_0x0155;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:58:0x0155, code lost:
        r0 = r14.mContext.getResources();
     */
    /* JADX WARNING: Removed duplicated region for block: B:68:0x017d  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void check() {
        /*
            r14 = this;
            r0 = 0
            r14.mLocalIpAddress = r0
            boolean r1 = com.miui.networkassistant.netdiagnose.NetworkDiagnosticsUtils.isIgnoreIcmp()
            java.lang.String r2 = "NetworkDiagnostics"
            r3 = 1
            if (r1 == 0) goto L_0x0018
            r14.mPingLoopBackRet = r3
            r14.mIsStatusNormal = r3
            r14.mPingLocalRet = r3
            java.lang.String r0 = "check(). system ignore all ICMP Echo requests."
            android.util.Log.i(r2, r0)
            return
        L_0x0018:
            r1 = 0
            android.net.ConnectivityManager r4 = r14.mCm     // Catch:{ Exception -> 0x0026 }
            java.lang.String r5 = "getActiveLinkProperties"
            java.lang.Object[] r6 = new java.lang.Object[r1]     // Catch:{ Exception -> 0x0026 }
            java.lang.Object r4 = b.b.o.g.e.a((java.lang.Object) r4, (java.lang.String) r5, (java.lang.Class<?>[]) r0, (java.lang.Object[]) r6)     // Catch:{ Exception -> 0x0026 }
            android.net.LinkProperties r4 = (android.net.LinkProperties) r4     // Catch:{ Exception -> 0x0026 }
            goto L_0x002b
        L_0x0026:
            r4 = move-exception
            r4.printStackTrace()
            r4 = r0
        L_0x002b:
            java.lang.String r5 = "LPv4"
            java.lang.String r6 = "wifi"
            java.lang.String r7 = "LPv6"
            java.lang.String r8 = "other"
            if (r4 == 0) goto L_0x009f
            java.util.HashMap r9 = new java.util.HashMap
            r9.<init>(r3)
            boolean r10 = b.b.c.h.f.a((android.net.LinkProperties) r4)
            if (r10 != 0) goto L_0x005b
            boolean r10 = b.b.c.h.f.b((android.net.LinkProperties) r4)
            if (r10 == 0) goto L_0x0047
            goto L_0x005b
        L_0x0047:
            android.content.Context r10 = r14.mContext
            boolean r10 = b.b.c.h.f.l(r10)
            if (r10 == 0) goto L_0x0053
            r9.put(r6, r5)
            goto L_0x0056
        L_0x0053:
            r9.put(r8, r5)
        L_0x0056:
            com.miui.networkassistant.utils.AnalyticsHelper.trackNetworkDiagnosticsGlobalAddrFamily(r9)
            r9 = r1
            goto L_0x006e
        L_0x005b:
            android.content.Context r10 = r14.mContext
            boolean r10 = b.b.c.h.f.l(r10)
            if (r10 == 0) goto L_0x0067
            r9.put(r6, r7)
            goto L_0x006a
        L_0x0067:
            r9.put(r8, r7)
        L_0x006a:
            com.miui.networkassistant.utils.AnalyticsHelper.trackNetworkDiagnosticsCnAddrFamily(r9)
            r9 = r3
        L_0x006e:
            java.lang.StringBuilder r10 = new java.lang.StringBuilder
            r10.<init>()
            java.lang.String r11 = "check() hasGlobalIPv6Address="
            r10.append(r11)
            boolean r11 = b.b.c.h.f.a((android.net.LinkProperties) r4)
            r10.append(r11)
            java.lang.String r10 = r10.toString()
            android.util.Log.i(r2, r10)
            java.lang.StringBuilder r10 = new java.lang.StringBuilder
            r10.<init>()
            java.lang.String r11 = "check() hasIPv6DefaultRoute="
            r10.append(r11)
            boolean r4 = b.b.c.h.f.b((android.net.LinkProperties) r4)
            r10.append(r4)
            java.lang.String r4 = r10.toString()
            android.util.Log.i(r2, r4)
            goto L_0x00a5
        L_0x009f:
            java.lang.String r4 = "check() getActiveLinkProperties return null"
            android.util.Log.i(r2, r4)
            r9 = r1
        L_0x00a5:
            java.lang.Class<java.net.Inet6Address> r4 = java.net.Inet6Address.class
            java.lang.String r10 = "LOOPBACK"
            java.lang.Object r4 = b.b.o.g.e.a((java.lang.Class<?>) r4, (java.lang.String) r10)     // Catch:{ Exception -> 0x021e }
            java.net.InetAddress r4 = (java.net.InetAddress) r4     // Catch:{ Exception -> 0x021e }
            java.lang.Boolean r4 = com.miui.networkassistant.netdiagnose.NetworkDiagnosticsUtils.isIpAvailable(r4, r3)
            boolean r4 = r4.booleanValue()
            r14.mPingLoopBackRet = r4
            boolean r4 = r14.mPingLoopBackRet
            r10 = 5000(0x1388, double:2.4703E-320)
            java.lang.String r12 = "loopBack"
            if (r4 != 0) goto L_0x00e3
            android.content.Context r4 = r14.mContext
            java.lang.Long r13 = java.lang.Long.valueOf(r10)
            java.lang.Boolean r0 = b.b.c.h.f.b(r4, r0, r13)
            boolean r0 = r0.booleanValue()
            if (r0 == 0) goto L_0x00e3
            java.util.HashMap r0 = new java.util.HashMap
            r0.<init>(r3)
            if (r9 == 0) goto L_0x00da
            r4 = r7
            goto L_0x00db
        L_0x00da:
            r4 = r5
        L_0x00db:
            r0.put(r12, r4)
            com.miui.networkassistant.utils.AnalyticsHelper.trackNetworkDiagnosticsPingResultDiffSocket(r0)
            r14.mPingLoopBackRet = r3
        L_0x00e3:
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r4 = "check() ping LOOPBACK ret="
            r0.append(r4)
            boolean r4 = r14.mPingLoopBackRet
            r0.append(r4)
            java.lang.String r0 = r0.toString()
            android.util.Log.i(r2, r0)
            boolean r0 = r14.mPingLoopBackRet
            java.lang.String r4 = "pingSelf"
            if (r0 == 0) goto L_0x019d
            if (r9 == 0) goto L_0x0109
            int r0 = b.b.c.c.f1620c
            java.net.InetAddress r0 = com.miui.networkassistant.netdiagnose.NetworkDiagnosticsUtils.getCurrentNetworkIp(r0)
            r14.mLocalIpAddress = r0
        L_0x0109:
            java.net.InetAddress r0 = r14.mLocalIpAddress
            if (r0 != 0) goto L_0x0115
            int r0 = b.b.c.c.f1619b
            java.net.InetAddress r0 = com.miui.networkassistant.netdiagnose.NetworkDiagnosticsUtils.getCurrentNetworkIp(r0)
            r14.mLocalIpAddress = r0
        L_0x0115:
            android.content.Context r0 = r14.mContext
            boolean r0 = b.b.c.h.f.l(r0)
            r13 = 2131756665(0x7f100679, float:1.9144244E38)
            if (r0 == 0) goto L_0x0143
            java.net.InetAddress r0 = r14.mLocalIpAddress
            if (r0 == 0) goto L_0x0133
            java.lang.Boolean r0 = com.miui.networkassistant.netdiagnose.NetworkDiagnosticsUtils.isIpAvailable(r0, r3)
            boolean r0 = r0.booleanValue()
            r14.mPingLocalRet = r0
            boolean r0 = r14.mPingLocalRet
            if (r0 != 0) goto L_0x015e
            goto L_0x0155
        L_0x0133:
            android.content.Context r0 = r14.mContext
            android.content.res.Resources r0 = r0.getResources()
            r13 = 2131756625(0x7f100651, float:1.9144163E38)
        L_0x013c:
            java.lang.String r0 = r0.getString(r13)
            r14.mSummary = r0
            goto L_0x015e
        L_0x0143:
            java.net.InetAddress r0 = r14.mLocalIpAddress
            if (r0 == 0) goto L_0x015c
            java.lang.Boolean r0 = com.miui.networkassistant.netdiagnose.NetworkDiagnosticsUtils.isIpAvailable(r0, r3)
            boolean r0 = r0.booleanValue()
            r14.mPingLocalRet = r0
            boolean r0 = r14.mPingLocalRet
            if (r0 != 0) goto L_0x015e
        L_0x0155:
            android.content.Context r0 = r14.mContext
            android.content.res.Resources r0 = r0.getResources()
            goto L_0x013c
        L_0x015c:
            r14.mPingLocalRet = r3
        L_0x015e:
            boolean r0 = r14.mPingLocalRet
            if (r0 != 0) goto L_0x0186
            java.net.InetAddress r0 = r14.mLocalIpAddress
            if (r0 == 0) goto L_0x0186
            android.content.Context r13 = r14.mContext
            java.lang.Long r10 = java.lang.Long.valueOf(r10)
            java.lang.Boolean r0 = b.b.c.h.f.b(r13, r0, r10)
            boolean r0 = r0.booleanValue()
            if (r0 == 0) goto L_0x0186
            java.util.HashMap r0 = new java.util.HashMap
            r0.<init>(r3)
            if (r9 == 0) goto L_0x017e
            r5 = r7
        L_0x017e:
            r0.put(r4, r5)
            com.miui.networkassistant.utils.AnalyticsHelper.trackNetworkDiagnosticsPingResultDiffSocket(r0)
            r14.mPingLocalRet = r3
        L_0x0186:
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r5 = "check() ping self ret="
            r0.append(r5)
            boolean r5 = r14.mPingLocalRet
            r0.append(r5)
            java.lang.String r0 = r0.toString()
            android.util.Log.i(r2, r0)
            goto L_0x01ae
        L_0x019d:
            r14.mPingLocalRet = r3
            android.content.Context r0 = r14.mContext
            android.content.res.Resources r0 = r0.getResources()
            r5 = 2131757669(0x7f100a65, float:1.914628E38)
            java.lang.String r0 = r0.getString(r5)
            r14.mSummary = r0
        L_0x01ae:
            boolean r0 = r14.mPingLoopBackRet
            if (r0 == 0) goto L_0x01b7
            boolean r0 = r14.mPingLocalRet
            if (r0 == 0) goto L_0x01b7
            r1 = r3
        L_0x01b7:
            r14.mIsStatusNormal = r1
            boolean r0 = r14.mIsStatusNormal
            if (r0 != 0) goto L_0x021d
            java.lang.String r0 = "busybox ifconfig"
            com.miui.networkassistant.netdiagnose.NetworkDiagnosticsUtils.doExec(r0)
            java.lang.String r0 = "getprop | grep -i dhcp"
            com.miui.networkassistant.netdiagnose.NetworkDiagnosticsUtils.doExec(r0)
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r1 = "check() ping "
            r0.append(r1)
            java.net.InetAddress r1 = r14.mLocalIpAddress
            r0.append(r1)
            java.lang.String r1 = " ret="
            r0.append(r1)
            boolean r1 = r14.mPingLocalRet
            r0.append(r1)
            java.lang.String r0 = r0.toString()
            android.util.Log.i(r2, r0)
            android.content.Context r0 = r14.mContext
            com.miui.networkassistant.netdiagnose.NetworkDiagnosticsUtils.printNetworkInfo(r0)
            boolean r0 = r14.networkChanged()
            if (r0 != 0) goto L_0x021d
            java.util.HashMap r0 = new java.util.HashMap
            r0.<init>(r3)
            boolean r1 = r14.mPingLoopBackRet
            if (r1 != 0) goto L_0x020b
            android.content.Context r1 = r14.mContext
            boolean r1 = b.b.c.h.f.l(r1)
            if (r1 == 0) goto L_0x0207
            r0.put(r6, r12)
            goto L_0x021a
        L_0x0207:
            r0.put(r8, r12)
            goto L_0x021a
        L_0x020b:
            android.content.Context r1 = r14.mContext
            boolean r1 = b.b.c.h.f.l(r1)
            if (r1 == 0) goto L_0x0217
            r0.put(r6, r4)
            goto L_0x021a
        L_0x0217:
            r0.put(r8, r4)
        L_0x021a:
            com.miui.networkassistant.utils.AnalyticsHelper.trackNetworkDiagnosticsStep(r0)
        L_0x021d:
            return
        L_0x021e:
            r0 = move-exception
            r0.printStackTrace()
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.networkassistant.netdiagnose.item.ProtocolCheck.check():void");
    }

    public AbstractNetworkDiagoneItem.FixedResult fix() {
        if (!f.l(this.mContext)) {
            return AbstractNetworkDiagoneItem.FixedResult.FAILED;
        }
        WifiManager wifiManager = (WifiManager) this.mContext.getSystemService("wifi");
        if (wifiManager != null) {
            wifiManager.disconnect();
            wifiManager.reassociate();
        }
        return AbstractNetworkDiagoneItem.FixedResult.SUCCESS;
    }

    public String getItemName() {
        return this.mContext.getResources().getString(R.string.protocol_exception_title);
    }

    public String getItemSolution() {
        return (this.mPingLoopBackRet && !this.mPingLocalRet && this.mLocalIpAddress == null) ? this.mContext.getResources().getString(R.string.renew_ip) : "";
    }

    public String getItemSummary() {
        return this.mSummary;
    }
}
