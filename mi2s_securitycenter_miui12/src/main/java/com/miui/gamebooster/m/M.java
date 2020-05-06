package com.miui.gamebooster.m;

import com.google.android.exoplayer2.extractor.ts.TsExtractor;
import com.milink.api.v1.MiLinkClientScanListCallback;

class M implements MiLinkClientScanListCallback {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ N f4453a;

    M(N n) {
        this.f4453a = n;
    }

    public void onConnectFail(String str, String str2) {
        this.f4453a.g.sendEmptyMessage(TsExtractor.TS_STREAM_TYPE_HDMV_DTS);
    }

    public void onConnectSuccess(String str, String str2) {
        this.f4453a.g.sendEmptyMessage(TsExtractor.TS_STREAM_TYPE_AC3);
    }

    public void onSelectDevice(String str, String str2, String str3) {
    }
}
