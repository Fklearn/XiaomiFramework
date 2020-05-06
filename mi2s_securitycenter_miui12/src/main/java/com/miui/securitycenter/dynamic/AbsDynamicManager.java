package com.miui.securitycenter.dynamic;

public abstract class AbsDynamicManager<T> {
    private volatile T mService;

    /* access modifiers changed from: package-private */
    public final void attach(DynamicService dynamicService) {
        this.mService = dynamicService;
    }

    public T getService() {
        return this.mService;
    }
}
