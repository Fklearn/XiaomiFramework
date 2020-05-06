package com.milink.api.v1;

public interface MiLinkClientDeviceListener {
    void onDeviceFound(MiLinkClientDevice miLinkClientDevice);

    void onDeviceLost(MiLinkClientDevice miLinkClientDevice);
}
