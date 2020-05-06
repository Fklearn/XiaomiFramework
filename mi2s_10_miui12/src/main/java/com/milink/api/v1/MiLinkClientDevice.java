package com.milink.api.v1;

public class MiLinkClientDevice {
    private String mDeviceId;
    private String mDeviceName;
    private String mDeviceType;
    private String mLastConnectTime;
    private String mP2pMac;
    private String mWifiMac;

    public String getDeviceId() {
        return this.mDeviceId;
    }

    public void setDeviceId(String mDeviceId2) {
        this.mDeviceId = mDeviceId2;
    }

    public String getDeviceName() {
        return this.mDeviceName;
    }

    public void setDeviceName(String mDeviceName2) {
        this.mDeviceName = mDeviceName2;
    }

    public String getDeviceType() {
        return this.mDeviceType;
    }

    public void setDeviceType(String mDeviceType2) {
        this.mDeviceType = mDeviceType2;
    }

    public String getP2pMac() {
        return this.mP2pMac;
    }

    public void setP2pMac(String mP2pMac2) {
        this.mP2pMac = mP2pMac2;
    }

    public String getWifiMac() {
        return this.mWifiMac;
    }

    public void setWifiMac(String mWifiMac2) {
        this.mWifiMac = mWifiMac2;
    }

    public String getLastConnectTime() {
        return this.mLastConnectTime;
    }

    public void setLastConnectTime(String mLastConnectTime2) {
        this.mLastConnectTime = mLastConnectTime2;
    }

    public String toString() {
        return "MiLinkClientDevice{mDeviceId='" + this.mDeviceId + '\'' + ", mDeviceName='" + this.mDeviceName + '\'' + ", mDeviceType='" + this.mDeviceType + '\'' + ", mP2pMac='" + this.mP2pMac + '\'' + ", mWifiMac='" + this.mWifiMac + '\'' + ", mLastConnectTime='" + this.mLastConnectTime + '\'' + '}';
    }
}
