package com.miui.luckymoney.webapi;

import b.b.c.g.a;
import b.b.c.g.c;
import b.b.c.h.j;
import com.miui.luckymoney.config.Constants;
import com.miui.networkassistant.utils.DeviceUtil;
import java.util.ArrayList;
import java.util.List;

public class WebApiAccessHelper {
    private static String accessModuleByPOST(String str, String str2, j jVar) {
        List<c> baseParams = getBaseParams();
        baseParams.add(new c(Constants.JSON_KEY_MODULE, str2));
        return a.a(str, Constants.apiUrl, "21da76da-224c-2313-ac60-abcd70139283", baseParams, jVar);
    }

    private static List<c> getBaseParams() {
        ArrayList arrayList = new ArrayList();
        arrayList.add(new c(Constants.JSON_KEY_DEVICE, DeviceUtil.DEVICE_NAME));
        arrayList.add(new c(Constants.JSON_KEY_T, "stable"));
        arrayList.add(new c(Constants.JSON_KEY_IMEI, DeviceUtil.getImeiMd5()));
        arrayList.add(new c("region", DeviceUtil.getRegion()));
        arrayList.add(new c(Constants.JSON_KEY_MIUI_VERSION, DeviceUtil.MIUI_VERSION));
        arrayList.add(new c(Constants.JSON_KEY_CARRIER, DeviceUtil.CARRIER));
        arrayList.add(new c(Constants.JSON_KEY_APP_VERSION, DeviceUtil.getAppVersionCode()));
        arrayList.add(new c(Constants.JSON_KEY_DATA_VERSION, "100"));
        arrayList.add(new c(Constants.JSON_KEY_INIT_DEV, "false"));
        arrayList.add(new c(Constants.JSON_KEY_IS_DIFF, "true"));
        return arrayList;
    }

    public static FloatResourceResult updateFloatResourceConfig() {
        return new FloatResourceResult(accessModuleByPOST("update", Constants.JSON_KEY_MODULE_FLOATWINDOW, new j("luckymoney_floatwindow")));
    }

    public static LuckyAlarmResult updateLuckyAlarmConfig() {
        return new LuckyAlarmResult(accessModuleByPOST("update", Constants.JSON_KEY_MODULE_LUCKYALARM, new j("luckymoney_luckyalarm")));
    }

    public static MasterSwitchResult updateMasterSwitchConfig() {
        return new MasterSwitchResult(accessModuleByPOST("update", "masterSwitch", new j("luckymoney_masterswith")));
    }
}
