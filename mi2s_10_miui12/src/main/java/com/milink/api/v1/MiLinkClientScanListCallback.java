package com.milink.api.v1;

public interface MiLinkClientScanListCallback {
    void onConnectFail(String str, String str2);

    void onConnectSuccess(String str, String str2);

    void onSelectDevice(String str, String str2, String str3);
}
