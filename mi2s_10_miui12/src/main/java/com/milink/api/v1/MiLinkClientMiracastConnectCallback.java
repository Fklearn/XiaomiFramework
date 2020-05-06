package com.milink.api.v1;

public interface MiLinkClientMiracastConnectCallback {
    void onConnectFail(String str);

    void onConnectSuccess(String str);

    void onConnecting(String str);

    void onResult(int i, String str, String str2);
}
