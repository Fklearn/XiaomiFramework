package com.miui.networkassistant.dual;

import android.content.Context;
import java.util.List;
import java.util.Map;
import miui.securitycenter.DualSim.DualSimInfoManagerWrapper;

public class DualSimInfoManager {

    public interface ISimInfoChangeListener extends DualSimInfoManagerWrapper.ISimInfoChangeWrapperListener {
    }

    public static List<Map<String, String>> getSimInfoList(Context context) {
        return DualSimInfoManagerWrapper.getSimInfoList(context);
    }

    public static void registerChangeListener(Context context, DualSimInfoManagerWrapper.ISimInfoChangeWrapperListener iSimInfoChangeWrapperListener) {
        DualSimInfoManagerWrapper.registerSimInfoChangeListener(context, iSimInfoChangeWrapperListener);
    }

    public static void unRegisterChangeListener(Context context, DualSimInfoManagerWrapper.ISimInfoChangeWrapperListener iSimInfoChangeWrapperListener) {
        DualSimInfoManagerWrapper.unRegisterSimInfoChangeListener(context, iSimInfoChangeWrapperListener);
    }
}
