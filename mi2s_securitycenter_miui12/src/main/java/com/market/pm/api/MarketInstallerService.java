package com.market.pm.api;

import android.os.IBinder;
import b.a.c;
import com.market.pm.IMarketInstallerService;

public class MarketInstallerService extends c implements IMarketInstallerService, b {
    private IMarketInstallerService k;

    public void a() {
    }

    public void a(IBinder iBinder) {
        this.k = IMarketInstallerService.Stub.a(iBinder);
    }

    public IBinder asBinder() {
        return null;
    }
}
