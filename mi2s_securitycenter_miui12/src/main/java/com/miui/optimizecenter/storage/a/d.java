package com.miui.optimizecenter.storage.a;

import com.miui.securitycenter.R;

public enum d {
    HEADER(0),
    TOTAL_SIZE(1, R.string.storage_app_detail_total),
    APP_SIZE(1, R.string.storage_app_detail_system),
    CACHE_SIZE(1, R.string.storage_app_detail_cache),
    USER_DATA_SIZE(1, R.string.storage_app_detail_sdcard),
    APP_CLEANER(1, R.string.storage_app_detail_wechat_cleaner),
    APP_WECHAT_CLEANER(1, R.string.storage_app_detail_wechat_session),
    CLEAR_ALL_DATA(1, R.string.storage_app_detail_clear_all_data),
    CLEAR_CACHE(1, R.string.storage_app_detail_clear_cache),
    MANAGER_STORAGE_SELF(1, R.string.storage_app_detail_manage_space),
    LINE(2);
    
    private int m;
    private int n;

    private d(int i) {
        this.m = i;
    }

    private d(int i, int i2) {
        this.m = i;
        this.n = i2;
    }

    public int a() {
        return this.n;
    }

    public int b() {
        return this.m;
    }
}
