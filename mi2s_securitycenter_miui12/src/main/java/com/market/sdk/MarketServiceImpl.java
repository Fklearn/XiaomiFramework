package com.market.sdk;

import android.os.Bundle;
import android.os.ResultReceiver;
import com.market.sdk.IMarketService;
import java.util.List;

public class MarketServiceImpl extends IMarketService.Stub {
    public int a(String[] strArr) {
        return -1;
    }

    public ApkVerifyInfo a(String str, String str2, boolean z) {
        return null;
    }

    public void a(long j, String str, List<String> list, ResultReceiver resultReceiver) {
    }

    public void a(long j, String str, List<String> list, IDesktopRecommendResponse iDesktopRecommendResponse) {
    }

    public void a(Bundle bundle, ResultReceiver resultReceiver) {
    }

    public void a(ResultReceiver resultReceiver) {
    }

    public void a(String str, int i, int i2, IImageCallback iImageCallback) {
    }

    public void a(String str, String str2, IImageCallback iImageCallback) {
    }

    public void a(String[] strArr, ResultReceiver resultReceiver) {
        resultReceiver.send(-1, (Bundle) null);
    }

    public ApkVerifyInfo b(String str, String str2, boolean z) {
        return null;
    }

    public void b(ResultReceiver resultReceiver) {
        Bundle bundle = new Bundle();
        bundle.putString("whiteSet", "");
        resultReceiver.send(1, bundle);
    }

    public void c(String str, String str2) {
    }

    public boolean c(String str) {
        return true;
    }

    public String g() {
        return null;
    }

    public boolean i() {
        return false;
    }

    public String j() {
        return null;
    }
}
