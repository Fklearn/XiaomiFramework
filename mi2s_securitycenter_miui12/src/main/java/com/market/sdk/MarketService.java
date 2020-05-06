package com.market.sdk;

import android.os.IBinder;
import b.a.c;
import com.market.sdk.IMarketService;

public class MarketService extends c implements IMarketService {
    private IMarketService k;

    public void a() {
    }

    public void a(IBinder iBinder) {
        this.k = IMarketService.Stub.a(iBinder);
    }

    public IBinder asBinder() {
        return null;
    }
}
