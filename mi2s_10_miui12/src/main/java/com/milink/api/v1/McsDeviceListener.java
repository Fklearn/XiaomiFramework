package com.milink.api.v1;

import android.os.Handler;
import android.os.RemoteException;
import com.milink.api.v1.aidl.IMcsDeviceListener;
import com.milink.api.v1.type.DeviceType;

public class McsDeviceListener extends IMcsDeviceListener.Stub {
    /* access modifiers changed from: private */
    public MilinkClientManagerDelegate mDelegate = null;
    /* access modifiers changed from: private */
    public MiLinkClientDeviceListener mDeviceListener;
    private Handler mHandler = new Handler();

    public void setDeviceListener(MiLinkClientDeviceListener deviceListener) {
        this.mDeviceListener = deviceListener;
    }

    public void setDelegate(MilinkClientManagerDelegate delegate) {
        this.mDelegate = delegate;
    }

    public void onDeviceFound(final String deviceId, final String name, final String type) throws RemoteException {
        if (this.mDelegate != null) {
            this.mHandler.post(new Runnable() {
                public void run() {
                    if (McsDeviceListener.this.mDelegate == null) {
                        return;
                    }
                    if (DeviceType.AIRKAN.equals(type) || DeviceType.DLNA_TV.equals(type) || DeviceType.DLNA_SPEAKER.equals(type)) {
                        McsDeviceListener.this.mDelegate.onDeviceFound(deviceId, name, DeviceType.create(type));
                    }
                }
            });
        }
    }

    public void onDeviceFound2(String deviceId, String name, String type, String p2pMac, String wifiMac, String lastConnectTime) throws RemoteException {
        if (this.mDelegate != null) {
            final String str = type;
            final String str2 = deviceId;
            final String str3 = name;
            final String str4 = wifiMac;
            final String str5 = p2pMac;
            final String str6 = lastConnectTime;
            this.mHandler.post(new Runnable() {
                public void run() {
                    if (McsDeviceListener.this.mDelegate != null && (DeviceType.AIRKAN.equals(str) || DeviceType.DLNA_TV.equals(str) || DeviceType.DLNA_SPEAKER.equals(str))) {
                        McsDeviceListener.this.mDelegate.onDeviceFound(str2, str3, DeviceType.create(str));
                    }
                    if (McsDeviceListener.this.mDeviceListener == null) {
                        return;
                    }
                    if (str4 != null || str5 != null) {
                        MiLinkClientDevice device = new MiLinkClientDevice();
                        device.setDeviceId(str2);
                        device.setDeviceName(str3);
                        device.setDeviceType(str);
                        device.setP2pMac(str5);
                        device.setWifiMac(str4);
                        device.setLastConnectTime(str6);
                        McsDeviceListener.this.mDeviceListener.onDeviceFound(device);
                    }
                }
            });
        }
    }

    public void onDeviceLost(final String deviceId) throws RemoteException {
        if (this.mDelegate != null) {
            this.mHandler.post(new Runnable() {
                public void run() {
                    if (McsDeviceListener.this.mDelegate != null) {
                        McsDeviceListener.this.mDelegate.onDeviceLost(deviceId);
                    }
                    if (McsDeviceListener.this.mDeviceListener != null) {
                        MiLinkClientDevice device = new MiLinkClientDevice();
                        device.setDeviceId(deviceId);
                        McsDeviceListener.this.mDeviceListener.onDeviceLost(device);
                    }
                }
            });
        }
    }
}
