package com.milink.api.v1;

import android.os.RemoteException;
import com.milink.api.v1.aidl.IMcsScanListCallback;

public class McsScanListCallback extends IMcsScanListCallback.Stub {
    private MiLinkClientScanListCallback mCallback;

    public void setCallback(MiLinkClientScanListCallback mCallback2) {
        this.mCallback = mCallback2;
    }

    public void onSelectDevice(String deviceId, String deviceName, String deviceType) throws RemoteException {
        MiLinkClientScanListCallback miLinkClientScanListCallback = this.mCallback;
        if (miLinkClientScanListCallback != null) {
            miLinkClientScanListCallback.onSelectDevice(deviceId, deviceName, deviceType);
        }
    }

    public void onConnectSuccess(String deviceId, String deviceName) throws RemoteException {
        MiLinkClientScanListCallback miLinkClientScanListCallback = this.mCallback;
        if (miLinkClientScanListCallback != null) {
            miLinkClientScanListCallback.onConnectSuccess(deviceId, deviceName);
        }
    }

    public void onConnectFail(String deviceId, String deviceName) throws RemoteException {
        MiLinkClientScanListCallback miLinkClientScanListCallback = this.mCallback;
        if (miLinkClientScanListCallback != null) {
            miLinkClientScanListCallback.onConnectFail(deviceId, deviceName);
        }
    }
}
