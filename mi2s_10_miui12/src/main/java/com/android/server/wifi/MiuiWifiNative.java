package com.android.server.wifi;

import android.os.INetworkManagementService;
import android.os.ServiceManager;
import android.system.ErrnoException;
import android.system.Os;
import android.text.TextUtils;
import android.util.Log;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class MiuiWifiNative {
    private static final boolean DBG = true;
    public static final int POWER_SAVE_DUE_TO_DHCP = 1;
    public static final int POWER_SAVE_DUE_TO_GAME = 2;
    private static final String SOFTAP_CONFIG_FILE = "/data/misc/wifi/hostapd.conf";
    private static final String SOFTAP_DENY_MAC_FILE = "/data/misc/wifi/miui_hostapd.deny";
    private static final Object mLock = new Object();
    private static MiuiWifiNative mMiuiWifiNative = null;
    private static final String mTAG = "MiuiWifiNative";
    private boolean mPowerSaveNeedDisabled;

    private native void closeSapConnectionNative();

    private native boolean connectToSapNative();

    private native boolean doDriverBooleanCommandNative(String str, String str2);

    private native boolean doSapBooleanCommandNative(String str);

    private native int doSapIntCommandNative(String str);

    private native String doSapStringCommandNative(String str);

    private static native boolean getWlanStatisticsNative(String str, WifiLinkLayerStatics wifiLinkLayerStatics);

    private static native int registerNatives();

    static {
        registerNatives();
    }

    public static synchronized MiuiWifiNative getInstance() {
        MiuiWifiNative miuiWifiNative;
        synchronized (MiuiWifiNative.class) {
            if (mMiuiWifiNative == null) {
                mMiuiWifiNative = new MiuiWifiNative();
            }
            miuiWifiNative = mMiuiWifiNative;
        }
        return miuiWifiNative;
    }

    public WifiLinkLayerStatics getWlanStatistics(String iface) {
        WifiLinkLayerStatics statics = new WifiLinkLayerStatics();
        getWlanStatisticsNative(iface, statics);
        return statics;
    }

    public class WifiLinkLayerStatics {
        long ack_fail_cnt = -1;
        long fail_cnt = -1;
        long frm_dup_cnt = -1;
        long multiple_retry_cnt = -1;
        long retry_cnt = -1;
        long rts_fail_cnt = -1;
        long rts_succ_cnt = -1;
        long rx_bc_byte_cnt = -1;
        long rx_byte_cnt = -1;
        long rx_discard_cnt = -1;
        long rx_error_cnt = -1;
        long rx_frm_cnt = -1;
        long rx_mc_byte_cnt = -1;
        long rx_rate = -1;
        long rx_uc_byte_cnt = -1;
        long tx_bc_byte_cnt = -1;
        long tx_byte_cnt = -1;
        long tx_frm_cnt = -1;
        long tx_mc_byte_cnt = -1;
        long tx_rate = -1;
        long tx_uc_byte_cnt = -1;

        public WifiLinkLayerStatics() {
        }

        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("rty: " + this.retry_cnt + " ");
            sb.append("m_rty: " + this.multiple_retry_cnt + " ");
            sb.append("tx_frm: " + this.tx_frm_cnt + " ");
            sb.append("rx_frm: " + this.rx_frm_cnt + " ");
            sb.append("frm_dup: " + this.frm_dup_cnt + " ");
            sb.append("fail: " + this.fail_cnt + " ");
            sb.append("ack_fail: " + this.ack_fail_cnt + " ");
            sb.append("rx_discard: " + this.rx_discard_cnt + " ");
            sb.append("rx_error: " + this.rx_error_cnt + " ");
            sb.append("tx_byte: " + this.tx_byte_cnt + " ");
            sb.append("rx_byte: " + this.rx_byte_cnt + " ");
            return sb.toString();
        }
    }

    public boolean connectToSap() {
        boolean connectToSapNative;
        synchronized (mLock) {
            connectToSapNative = connectToSapNative();
        }
        return connectToSapNative;
    }

    public boolean doDriverBooleanCommand(String command, String iface) {
        boolean doDriverBooleanCommandNative;
        synchronized (mLock) {
            doDriverBooleanCommandNative = doDriverBooleanCommandNative(command, iface);
        }
        return doDriverBooleanCommandNative;
    }

    public boolean setCompatibleMode(boolean enabled, String iface) {
        if (enabled) {
            return doDriverBooleanCommand("SETCOMPATIBLEMODE 1", iface);
        }
        return doDriverBooleanCommand("SETCOMPATIBLEMODE 0", iface);
    }

    public boolean enableDataStallDetection(boolean enabled, String iface) {
        if (enabled) {
            return doDriverBooleanCommand("ENABLEDATASTALLDETECTION 1", iface);
        }
        return doDriverBooleanCommand("ENABLEDATASTALLDETECTION 0", iface);
    }

    public void closeSapConnection() {
        synchronized (mLock) {
            closeSapConnectionNative();
        }
    }

    private boolean doBooleanCommand(String command) {
        boolean result;
        Log.d(mTAG, "doBoolean: " + command);
        synchronized (mLock) {
            result = doSapBooleanCommandNative(command);
            Log.d(mTAG, command + ": returned " + result);
        }
        return result;
    }

    public boolean disassociate(String mac) {
        return doBooleanCommand("disassociate " + mac);
    }

    public boolean reloadDenyList() {
        return doBooleanCommand("SET deny_mac_file /data/misc/wifi/miui_hostapd.deny");
    }

    public boolean deauthenticate(String mac) {
        return doBooleanCommand("deauthenticate " + mac);
    }

    private void rename(File sFile, File dFile) {
        try {
            if (sFile.exists() && dFile.exists()) {
                String dPath = dFile.getAbsolutePath();
                sFile.renameTo(dFile);
                Os.chown(dPath, 1000, 1010);
                Os.chmod(dPath, 432);
            }
        } catch (ErrnoException e) {
            Log.e(mTAG, "Could not read " + null + ", " + e);
        }
    }

    private void ensureConfigFileExists() {
        if (!new File(SOFTAP_CONFIG_FILE).exists()) {
            INetworkManagementService.Stub.asInterface(ServiceManager.getService("network_management"));
        }
    }

    private void parseAndUpdateSoftapConfigfile(String filed, String value, boolean isRemove) {
        ensureConfigFileExists();
        boolean filedFind = false;
        BufferedReader reader = null;
        File tmpfile = null;
        BufferedWriter writer = null;
        try {
            File file = new File(SOFTAP_CONFIG_FILE);
            tmpfile = new File("/data/misc/wifi/hostapd.conf.tmp");
            reader = new BufferedReader(new FileReader(file));
            writer = new BufferedWriter(new FileWriter(tmpfile));
            for (String line = reader.readLine(); line != null; line = reader.readLine()) {
                if (line.startsWith(filed)) {
                    filedFind = true;
                    if (!isRemove) {
                        writer.write(filed + "=" + value + "\n");
                    }
                } else {
                    writer.write(line + "\n");
                }
            }
            if (!filedFind && !isRemove) {
                writer.write(filed + "=" + value + "\n");
            }
            writer.flush();
            rename(tmpfile, file);
            try {
                reader.close();
                writer.close();
                if (tmpfile.exists()) {
                    tmpfile.delete();
                }
            } catch (IOException e) {
            }
        } catch (FileNotFoundException e2) {
            Log.e(mTAG, "Could not open /data/misc/wifi/hostapd.conf, " + e2);
            if (reader != null) {
                reader.close();
            }
            if (writer != null) {
                writer.close();
            }
            if (tmpfile != null && tmpfile.exists()) {
                tmpfile.delete();
            }
        } catch (IOException e3) {
            Log.e(mTAG, "Could not read /data/misc/wifi/hostapd.conf, " + e3);
            if (reader != null) {
                reader.close();
            }
            if (writer != null) {
                writer.close();
            }
            if (tmpfile != null && tmpfile.exists()) {
                tmpfile.delete();
            }
        } catch (Throwable th) {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e4) {
                    throw th;
                }
            }
            if (writer != null) {
                writer.close();
            }
            if (tmpfile != null && tmpfile.exists()) {
                tmpfile.delete();
            }
            throw th;
        }
    }

    public void setHotSpotMaxNum(int num) {
        parseAndUpdateSoftapConfigfile("max_num_sta", String.valueOf(num), false);
    }

    private boolean isMacAddress(String mac) {
        return mac.matches("^[a-fA-F0-9]{2}+:[a-fA-F0-9]{2}+:[a-fA-F0-9]{2}+:[a-fA-F0-9]{2}+:[a-fA-F0-9]{2}+:[a-fA-F0-9]{2}$");
    }

    private void parsAndUpdateDenyMacFile(Set<String> macSet) {
        BufferedReader reader = null;
        File tmpfile = null;
        BufferedWriter writer = null;
        Set<String> tMacSet = new HashSet<>(macSet);
        try {
            File file = new File(SOFTAP_DENY_MAC_FILE);
            if (file.exists() || file.createNewFile()) {
                tmpfile = new File("/data/misc/wifi/miui_hostapd.deny.tmp");
                reader = new BufferedReader(new FileReader(file));
                writer = new BufferedWriter(new FileWriter(tmpfile));
                for (String line = reader.readLine(); line != null; line = reader.readLine()) {
                    if (line.startsWith("#")) {
                        writer.write(line + "\n");
                    } else if (!line.startsWith("-")) {
                        if (isMacAddress(line)) {
                            if (tMacSet.contains(line)) {
                                tMacSet.remove(line);
                                writer.write(line + "\n");
                            } else {
                                writer.write("-" + line + "\n");
                            }
                        }
                    }
                }
                for (String mac : tMacSet) {
                    writer.write(mac + "\n");
                }
                writer.flush();
                rename(tmpfile, file);
                try {
                    reader.close();
                    writer.close();
                    if (tmpfile.exists()) {
                        tmpfile.delete();
                    }
                } catch (IOException e) {
                }
            } else {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e2) {
                        return;
                    }
                }
                if (writer != null) {
                    writer.close();
                }
                if (tmpfile != null && tmpfile.exists()) {
                    tmpfile.delete();
                }
            }
        } catch (FileNotFoundException e3) {
            Log.e(mTAG, "Could not open /data/misc/wifi/hostapd.conf, " + e3);
            if (reader != null) {
                reader.close();
            }
            if (writer != null) {
                writer.close();
            }
            if (tmpfile != null && tmpfile.exists()) {
                tmpfile.delete();
            }
        } catch (IOException e4) {
            Log.e(mTAG, "Could not read /data/misc/wifi/hostapd.conf, " + e4);
            if (reader != null) {
                reader.close();
            }
            if (writer != null) {
                writer.close();
            }
            if (tmpfile != null && tmpfile.exists()) {
                tmpfile.delete();
            }
        } catch (Throwable th) {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e5) {
                    throw th;
                }
            }
            if (writer != null) {
                writer.close();
            }
            if (tmpfile != null && tmpfile.exists()) {
                tmpfile.delete();
            }
            throw th;
        }
    }

    public void setHotSpotDenyMac(Set<String> macSet) {
        parsAndUpdateDenyMacFile(macSet);
        reloadDenyList();
    }

    public void setHotSpotVendorSpecific(String vendorSpecificStr) {
        parseAndUpdateSoftapConfigfile("vendor_elements", vendorSpecificStr, TextUtils.isEmpty(vendorSpecificStr));
    }

    public void setPowerSaveByReason(int reason, boolean enabled) {
        Log.i(mTAG, "setPowerSaveByReason: " + reason + " | " + enabled + " | " + this.mPowerSaveNeedDisabled);
        if (reason == 2) {
            this.mPowerSaveNeedDisabled = !enabled;
        } else if (this.mPowerSaveNeedDisabled) {
            return;
        }
        SupplicantStaIfaceHalInjector.setPowerSave(enabled);
    }
}
