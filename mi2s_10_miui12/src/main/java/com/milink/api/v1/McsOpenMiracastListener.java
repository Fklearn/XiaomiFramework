package com.milink.api.v1;

import android.os.RemoteException;
import com.milink.api.v1.aidl.IMcsOpenMiracastListener;

public class McsOpenMiracastListener extends IMcsOpenMiracastListener.Stub {
    private MiLinkClientOpenMiracastListener mOpenMiracastListener;

    public void setOpenMiracastListener(MiLinkClientOpenMiracastListener mOpenMiracastListener2) {
        this.mOpenMiracastListener = mOpenMiracastListener2;
    }

    public void openSuccess(String deviceName, String p2pMac, String wifiMac) throws RemoteException {
        MiLinkClientOpenMiracastListener miLinkClientOpenMiracastListener = this.mOpenMiracastListener;
        if (miLinkClientOpenMiracastListener != null) {
            miLinkClientOpenMiracastListener.openSuccess(deviceName, p2pMac, wifiMac);
        }
    }

    public void openFailure(String deviceName, String p2pMac, String wifiMac, String errorCode) throws RemoteException {
        MiLinkClientOpenMiracastListener miLinkClientOpenMiracastListener = this.mOpenMiracastListener;
        if (miLinkClientOpenMiracastListener != null) {
            miLinkClientOpenMiracastListener.openFailure(deviceName, p2pMac, wifiMac, errorCode);
        }
    }
}
