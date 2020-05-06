package com.miui.securitycenter.dynamic;

public interface ServiceConnection {
    void onServiceConnected(Object obj);

    void onServiceConnectionFail(int i);
}
