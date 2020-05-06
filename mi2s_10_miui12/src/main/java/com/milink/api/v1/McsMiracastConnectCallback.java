package com.milink.api.v1;

import android.os.RemoteException;
import com.milink.api.v1.aidl.IMcsMiracastConnectCallback;

public class McsMiracastConnectCallback extends IMcsMiracastConnectCallback.Stub {
    private MiLinkClientMiracastConnectCallback mCallback;

    public void setCallback(MiLinkClientMiracastConnectCallback callback) {
        this.mCallback = callback;
    }

    public void onConnectSuccess(String p2pMac) throws RemoteException {
        MiLinkClientMiracastConnectCallback miLinkClientMiracastConnectCallback = this.mCallback;
        if (miLinkClientMiracastConnectCallback != null) {
            miLinkClientMiracastConnectCallback.onConnectSuccess(p2pMac);
        }
    }

    public void onConnectFail(String p2pMac) throws RemoteException {
        MiLinkClientMiracastConnectCallback miLinkClientMiracastConnectCallback = this.mCallback;
        if (miLinkClientMiracastConnectCallback != null) {
            miLinkClientMiracastConnectCallback.onConnectFail(p2pMac);
        }
    }

    public void onConnecting(String p2pMac) throws RemoteException {
        MiLinkClientMiracastConnectCallback miLinkClientMiracastConnectCallback = this.mCallback;
        if (miLinkClientMiracastConnectCallback != null) {
            miLinkClientMiracastConnectCallback.onConnecting(p2pMac);
        }
    }

    public void onResult(int resultCode, String result, String p2pMac) throws RemoteException {
        MiLinkClientMiracastConnectCallback miLinkClientMiracastConnectCallback = this.mCallback;
        if (miLinkClientMiracastConnectCallback != null) {
            miLinkClientMiracastConnectCallback.onResult(resultCode, result, p2pMac);
        }
    }
}
