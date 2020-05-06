package com.miui.securitycenter.dynamic.app;

import com.miui.securitycenter.dynamic.AbsDynamicManager;

public class AppActivityManager extends AbsDynamicManager<IAppActivity> implements IAppActivity {
    public void init() {
        ((IAppActivity) getService()).init();
    }
}
