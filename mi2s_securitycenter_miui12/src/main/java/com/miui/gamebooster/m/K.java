package com.miui.gamebooster.m;

import android.util.Log;
import com.milink.api.v1.MilinkClientManagerDelegate;
import com.milink.api.v1.type.DeviceType;
import com.milink.api.v1.type.ErrorCode;

class K implements MilinkClientManagerDelegate {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ N f4451a;

    K(N n) {
        this.f4451a = n;
    }

    public void onClose() {
        boolean unused = this.f4451a.l = false;
        Log.i("MiLinkUtils", "onClose");
    }

    public void onConnected() {
        Log.i("MiLinkUtils", "onConnected");
    }

    public void onConnectedFailed(ErrorCode errorCode) {
        Log.i("MiLinkUtils", "onConnectedFailed");
    }

    public void onDeviceFound(String str, String str2, DeviceType deviceType) {
        Log.i("MiLinkUtils", "onDeviceFound");
    }

    public void onDeviceLost(String str) {
        Log.i("MiLinkUtils", "onDeviceLost");
    }

    public void onDisconnected() {
        Log.i("MiLinkUtils", "onDisconnected");
    }

    public void onLoading() {
        Log.i("MiLinkUtils", "onLoading");
    }

    public void onNextAudio(boolean z) {
        Log.i("MiLinkUtils", "onNextAudio");
    }

    public void onOpen() {
        boolean unused = this.f4451a.l = true;
        this.f4451a.h();
    }

    public void onPaused() {
        Log.i("MiLinkUtils", "onPaused");
    }

    public void onPlaying() {
        Log.i("MiLinkUtils", "onPlaying");
    }

    public void onPrevAudio(boolean z) {
        Log.i("MiLinkUtils", "onPrevAudio");
    }

    public void onStopped() {
        Log.i("MiLinkUtils", "onStopped");
    }

    public void onVolume(int i) {
        Log.i("MiLinkUtils", "onVolume");
    }
}
