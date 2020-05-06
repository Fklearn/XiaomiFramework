package com.milink.api.v1;

import com.milink.api.v1.type.MediaType;
import com.milink.api.v1.type.ReturnCode;
import com.milink.api.v1.type.SlideMode;

public interface IMilinkClientManager {
    void close();

    ReturnCode connect(String str, int i);

    ReturnCode connectWifiDisplay(String str, String str2, String str3, MiLinkClientMiracastConnectCallback miLinkClientMiracastConnectCallback);

    ReturnCode disconnect();

    ReturnCode disconnectWifiDisplay();

    ReturnCode dismissScanList();

    int getPlaybackDuration();

    int getPlaybackProgress();

    int getPlaybackRate();

    int getVolume();

    void open();

    ReturnCode rotatePhoto(String str, boolean z, float f);

    ReturnCode selectDevice(String str, String str2, String str3);

    void setDataSource(MilinkClientManagerDataSource milinkClientManagerDataSource);

    void setDelegate(MilinkClientManagerDelegate milinkClientManagerDelegate);

    void setDeviceListener(MiLinkClientDeviceListener miLinkClientDeviceListener);

    void setDeviceName(String str);

    ReturnCode setPlaybackProgress(int i);

    ReturnCode setPlaybackRate(int i);

    ReturnCode setVolume(int i);

    ReturnCode show(String str);

    ReturnCode showScanList(MiLinkClientScanListCallback miLinkClientScanListCallback, int i);

    ReturnCode startPlay(String str, String str2, int i, double d, MediaType mediaType);

    ReturnCode startPlay(String str, String str2, String str3, int i, double d, MediaType mediaType);

    ReturnCode startShow();

    ReturnCode startSlideshow(int i, SlideMode slideMode);

    ReturnCode startTvMiracast(String str, String str2, String str3, String str4, String str5, MiLinkClientOpenMiracastListener miLinkClientOpenMiracastListener);

    ReturnCode startWifiDisplayScan();

    ReturnCode stopPlay();

    ReturnCode stopShow();

    ReturnCode stopSlideshow();

    ReturnCode stopWifiDisplayScan();

    ReturnCode zoomPhoto(String str, int i, int i2, int i3, int i4, int i5, int i6, float f);
}
