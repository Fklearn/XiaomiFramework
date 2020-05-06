package com.milink.api.v1;

import com.milink.api.v1.type.DeviceType;
import com.milink.api.v1.type.ErrorCode;

public interface MilinkClientManagerDelegate {
    void onClose();

    void onConnected();

    void onConnectedFailed(ErrorCode errorCode);

    void onDeviceFound(String str, String str2, DeviceType deviceType);

    void onDeviceLost(String str);

    void onDisconnected();

    void onLoading();

    void onNextAudio(boolean z);

    void onOpen();

    void onPaused();

    void onPlaying();

    void onPrevAudio(boolean z);

    void onStopped();

    void onVolume(int i);
}
