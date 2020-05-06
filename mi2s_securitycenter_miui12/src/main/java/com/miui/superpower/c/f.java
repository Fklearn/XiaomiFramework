package com.miui.superpower.c;

import b.b.c.g.a;
import b.b.c.g.c;
import b.b.c.h.j;
import com.miui.luckymoney.config.Constants;
import com.miui.networkassistant.utils.DeviceUtil;
import java.util.ArrayList;
import java.util.List;

public class f {
    public static b a() {
        return new b(a("update", "superPower"));
    }

    private static String a(String str, String str2) {
        List<c> b2 = b();
        b2.add(new c(Constants.JSON_KEY_MODULE, str2));
        return a.a(str, Constants.apiUrl, "21da76da-224c-2313-ac60-abcd70139283", b2, new j("superpower_accessmodulebypost"));
    }

    private static List<c> b() {
        ArrayList arrayList = new ArrayList();
        arrayList.add(new c(Constants.JSON_KEY_DEVICE, DeviceUtil.DEVICE_NAME));
        arrayList.add(new c(Constants.JSON_KEY_T, DeviceUtil.IS_STABLE_VERSION ? "stable" : "development"));
        arrayList.add(new c("region", DeviceUtil.getRegion()));
        arrayList.add(new c(Constants.JSON_KEY_MIUI_VERSION, DeviceUtil.MIUI_VERSION));
        arrayList.add(new c(Constants.JSON_KEY_CARRIER, DeviceUtil.CARRIER));
        arrayList.add(new c(Constants.JSON_KEY_APP_VERSION, DeviceUtil.getAppVersionCode()));
        arrayList.add(new c(Constants.JSON_KEY_DATA_VERSION, "100"));
        arrayList.add(new c(Constants.JSON_KEY_INIT_DEV, "false"));
        arrayList.add(new c(Constants.JSON_KEY_IS_DIFF, "true"));
        return arrayList;
    }
}
