package com.miui.optimizecenter.storage;

import com.miui.optimizecenter.storage.model.StorageItemInfo;

public enum u {
    OTHER(StorageItemInfo.a(0)),
    APP_DATA(StorageItemInfo.a(1)),
    APP_SDCARD_DATA(StorageItemInfo.a(2)),
    PICTURE(StorageItemInfo.a(3)),
    AUDIO(StorageItemInfo.a(4)),
    VIDEO(StorageItemInfo.a(5)),
    APK(StorageItemInfo.a(6)),
    DOC(StorageItemInfo.a(7)),
    SYSTEM(StorageItemInfo.a(8));
    
    private StorageItemInfo k;

    private u(StorageItemInfo storageItemInfo) {
        this.k = storageItemInfo;
    }

    public StorageItemInfo a() {
        return this.k;
    }
}
