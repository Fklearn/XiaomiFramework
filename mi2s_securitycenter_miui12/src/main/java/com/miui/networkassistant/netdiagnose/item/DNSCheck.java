package com.miui.networkassistant.netdiagnose.item;

import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.text.TextUtils;
import android.util.Log;
import b.b.c.h.f;
import com.miui.networkassistant.netdiagnose.AbstractNetworkDiagoneItem;
import com.miui.networkassistant.netdiagnose.NetworkDiagnosticsUtils;
import com.miui.networkassistant.utils.AnalyticsHelper;
import com.miui.networkassistant.utils.DeviceUtil;
import com.miui.securitycenter.R;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import miui.securitycenter.NetworkUtils;

public class DNSCheck extends AbstractNetworkDiagoneItem {
    private static final String BAIDU_DNS = "180.76.76.76";
    private static final String CNNIC_DNS = "1.2.4.8";
    private static final String CNNIC_DNS2 = "202.98.0.68";
    private static final String CTCC_DNS = "114.114.114.114";
    private static final long DNS_CHECK_TIMEPUT = 5000;
    private static final String GOOGLE_DNS1 = "8.8.8.8";
    private static final String GOOGLE_DNS2 = "8.8.4.4";
    private static final String HOST_ADDR_BDD = "180.76.76.76";
    private static final String HOST_ADDR_BDN = "220.181.112.224";
    private static final String HOST_ADDR_CTCC = "114.114.114.114";
    private static final String HOST_ADDR_GGD = "8.8.8.8";
    private static final String HOST_ADDR_GGD2 = "8.8.4.4";
    private static final String HOST_ADDR_GGN = "216.58.221.100";
    private static final int REPEAT = 1;
    private static final String TAG = "NetworkDiagnostics_DNSCheck";
    private ArrayList<String> mCnDnsAddrList;
    private ArrayList<Long> mCnDnsReqTimeList;
    private ArrayList<String> mHostAddrList = new ArrayList<>();

    public DNSCheck(Context context) {
        super(context);
        this.mHostAddrList.add("8.8.8.8");
        this.mHostAddrList.add("8.8.4.4");
        this.mHostAddrList.add(HOST_ADDR_GGN);
        this.mHostAddrList.add("180.76.76.76");
        this.mHostAddrList.add(HOST_ADDR_BDN);
        this.mHostAddrList.add("114.114.114.114");
        this.mCnDnsAddrList = new ArrayList<>();
        this.mCnDnsAddrList.add("180.76.76.76");
        this.mCnDnsAddrList.add("114.114.114.114");
        this.mCnDnsAddrList.add(CNNIC_DNS);
        this.mCnDnsAddrList.add(CNNIC_DNS2);
        this.mCnDnsAddrList.add("8.8.8.8");
        this.mCnDnsReqTimeList = new ArrayList<>();
    }

    public void check() {
        int i = 0;
        Boolean bool = false;
        while (i < this.mHostAddrList.size()) {
            try {
                bool = NetworkDiagnosticsUtils.isIpAvailable(InetAddress.getByName(this.mHostAddrList.get(i)), 1);
                if (!bool.booleanValue() && f.a(this.mContext, InetAddress.getByName(this.mHostAddrList.get(i)), (Long) 5000L).booleanValue()) {
                    HashMap hashMap = new HashMap(1);
                    hashMap.put("pingDns", this.mHostAddrList.get(i));
                    AnalyticsHelper.trackNetworkDiagnosticsPingResultDiffSocket(hashMap);
                    bool = true;
                }
                Log.i(TAG, "check() ret=" + bool);
                if (bool.booleanValue()) {
                    break;
                }
                i++;
            } catch (Exception e) {
                Log.i(TAG, "an exception occurred.", e);
            }
        }
        this.mIsStatusNormal = !bool.booleanValue();
        if (!this.mIsStatusNormal && !networkChanged()) {
            HashMap hashMap2 = new HashMap(1);
            hashMap2.put(f.l(this.mContext) ? "wifi" : "other", "dns");
            AnalyticsHelper.trackNetworkDiagnosticsStep(hashMap2);
            if (DeviceUtil.IS_L_OR_LATER && f.l(this.mContext)) {
                hashMap2.clear();
                String d2 = f.d(this.mContext);
                if (d2 != null) {
                    hashMap2.put("dns", d2);
                    AnalyticsHelper.trackNetworkDiagnosticsDnsAssigment(hashMap2);
                }
            }
        }
    }

    public AbstractNetworkDiagoneItem.FixedResult fix() {
        InetAddress byName;
        int activeNetworkType = this.mDiagnosticsManager.getActiveNetworkType();
        String b2 = activeNetworkType != 1 ? f.b(this.mContext) : f.g(this.mContext);
        if (this.mDiagnosticsManager.getDiagnosingNetworkType() != activeNetworkType || !TextUtils.equals(this.mDiagnosticsManager.getDiagnosingNetworkInterface(), b2)) {
            Log.i(TAG, "network has chaged!");
            return AbstractNetworkDiagoneItem.FixedResult.NETWORKCHANGED;
        }
        WifiConfiguration a2 = f.a(this.mContext);
        if (a2 == null) {
            Log.i(TAG, "wifiConf is null!!");
        } else {
            try {
                InetAddress dns = getDNS(a2);
                String str = null;
                if (dns != null) {
                    str = dns.getHostAddress();
                }
                if (NetworkDiagnosticsUtils.isCnUser(this.mContext)) {
                    int indexOf = this.mCnDnsAddrList.indexOf(str);
                    this.mCnDnsReqTimeList.clear();
                    int i = 0;
                    for (int i2 = 0; i2 < this.mCnDnsAddrList.size(); i2++) {
                        long j = Long.MAX_VALUE;
                        if (i2 != indexOf) {
                            long currentTimeMillis = System.currentTimeMillis();
                            if (NetworkDiagnosticsUtils.isIpAvailable(InetAddress.getByName(this.mCnDnsAddrList.get(i2)), 1).booleanValue()) {
                                j = System.currentTimeMillis() - currentTimeMillis;
                            }
                        }
                        this.mCnDnsReqTimeList.add(Long.valueOf(j));
                    }
                    long longValue = this.mCnDnsReqTimeList.get(0).longValue();
                    for (int i3 = 1; i3 < this.mCnDnsReqTimeList.size(); i3++) {
                        if (this.mCnDnsReqTimeList.get(i3).longValue() < longValue) {
                            longValue = this.mCnDnsReqTimeList.get(i3).longValue();
                            i = i3;
                        }
                    }
                    if (i != indexOf) {
                        byName = InetAddress.getByName(this.mCnDnsAddrList.get(i));
                    }
                } else {
                    byName = TextUtils.equals(str, "8.8.4.4") ? InetAddress.getByName("8.8.8.8") : InetAddress.getByName("8.8.4.4");
                }
                setDNS(byName, a2);
            } catch (SecurityException e) {
                e.printStackTrace();
            } catch (IllegalArgumentException e2) {
                e2.printStackTrace();
            } catch (NoSuchFieldException e3) {
                e3.printStackTrace();
            } catch (IllegalAccessException e4) {
                e4.printStackTrace();
            } catch (UnknownHostException e5) {
                e5.printStackTrace();
            }
        }
        return AbstractNetworkDiagoneItem.FixedResult.SUCCESS;
    }

    public InetAddress getDNS(WifiConfiguration wifiConfiguration) {
        return f.a(this.mContext, wifiConfiguration);
    }

    public String getItemName() {
        return this.mContext.getResources().getString(R.string.dns_exception_title);
    }

    public String getItemSolution() {
        return this.mContext.getResources().getString(R.string.replace_dns);
    }

    public String getItemSummary() {
        return this.mContext.getResources().getString(R.string.dns_exception_summary);
    }

    public void setDNS(InetAddress inetAddress, WifiConfiguration wifiConfiguration) {
        NetworkUtils.saveWifiConfiguration(this.mContext, inetAddress, wifiConfiguration);
    }
}
