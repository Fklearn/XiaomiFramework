package com.android.server.wifi.p2p;

import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pWfdInfo;
import android.util.Log;
import com.android.server.wifi.util.NativeUtil;
import java.util.ArrayList;
import libcore.util.HexEncoding;
import vendor.qti.hardware.wifi.supplicant.V2_0.ISupplicantVendorP2PIfaceCallback;

/* compiled from: SupplicantP2pIfaceCallback */
class SupplicantVendorP2pIfaceCallback extends ISupplicantVendorP2PIfaceCallback.Stub {
    private static final boolean DBG = true;
    private static final String TAG = "SupplicantVendorP2pIfaceCallback ";
    private final String mInterface;
    private final WifiP2pMonitor mMonitor;

    public SupplicantVendorP2pIfaceCallback(String iface, WifiP2pMonitor monitor) {
        this.mInterface = iface;
        this.mMonitor = monitor;
    }

    protected static void logd(String s) {
        Log.d(TAG, s);
    }

    public void onR2DeviceFound(byte[] srcAddress, byte[] p2pDeviceAddress, byte[] primaryDeviceType, String deviceName, short configMethods, byte deviceCapabilities, int groupCapabilities, byte[] wfdDeviceInfo, byte[] wfdR2DeviceInfo) {
        byte[] bArr = primaryDeviceType;
        String str = deviceName;
        byte[] bArr2 = wfdDeviceInfo;
        byte[] bArr3 = wfdR2DeviceInfo;
        WifiP2pDevice device = new WifiP2pDevice();
        device.deviceName = str;
        if (str == null) {
            Log.e(TAG, "Missing device name.");
            return;
        }
        try {
            device.deviceAddress = NativeUtil.macAddressFromByteArray(p2pDeviceAddress);
            try {
                device.primaryDeviceType = new String(HexEncoding.encode(bArr, 0, bArr.length));
                device.deviceCapability = deviceCapabilities;
                device.groupCapability = groupCapabilities;
                device.wpsConfigMethodsSupported = configMethods;
                device.status = 3;
                if (bArr2 != null && bArr2.length >= 6) {
                    device.wfdInfo = new WifiP2pWfdInfo((bArr2[0] << 8) + bArr2[1], (bArr2[2] << 8) + bArr2[3], (bArr2[4] << 8) + bArr2[5]);
                }
                if (bArr3 != null && bArr3.length >= 2) {
                    device.wfdInfo.setWfdR2Device((bArr3[0] << 8) + bArr3[1]);
                }
                logd("R2 Device discovered on " + this.mInterface + ": " + device + "R2 Info:" + bArr3);
                this.mMonitor.broadcastP2pDeviceFound(this.mInterface, device);
            } catch (Exception e) {
                short s = configMethods;
                byte b = deviceCapabilities;
                int i = groupCapabilities;
                Log.e(TAG, "Could not encode device primary type.", e);
            }
        } catch (Exception e2) {
            short s2 = configMethods;
            byte b2 = deviceCapabilities;
            int i2 = groupCapabilities;
            Log.e(TAG, "Could not decode device address.", e2);
        }
    }

    public void onVendorExtensionFound(ArrayList<Byte> arrayList, byte type) {
    }
}
