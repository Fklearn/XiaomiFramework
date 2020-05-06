package com.miui.securitycenter.dynamic;

import com.miui.securitycenter.dynamic.app.AppActivityManager;
import java.util.HashMap;

public class ServiceRegistry {
    private static HashMap<String, Class<? extends AbsDynamicManager<?>>> sSerciceMap = new HashMap<>();

    static {
        sSerciceMap.put(DynamicContext.APP_ACTIVITY, AppActivityManager.class);
    }

    public static Class<? extends AbsDynamicManager<?>> getServcieClass(String str) {
        return sSerciceMap.get(str);
    }
}
