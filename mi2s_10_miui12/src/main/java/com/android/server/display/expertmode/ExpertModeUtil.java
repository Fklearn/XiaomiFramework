package com.android.server.display.expertmode;

import android.content.Context;
import miui.hardware.display.DisplayFeatureManager;

public class ExpertModeUtil {
    public static final boolean SUPPORT_DISPLAY_EXPERT_MODE = ExpertData.SUPPORT_DISPLAY_EXPERT_MODE;

    private static void setExpertScreenMode(int cookie, int value) {
        DisplayFeatureManager.getInstance().setScreenEffect(26, value, cookie);
    }

    private static void setExpertScreenMode(ExpertData data) {
        if (data != null) {
            for (int cookie = 0; cookie < 9; cookie++) {
                setExpertScreenMode(cookie, data.getByCookie(cookie));
            }
        }
    }

    private static ExpertData getExpertData(Context context) {
        ExpertData data = ExpertData.getFromDatabase(context);
        if (data == null) {
            return ExpertData.getDefaultValue();
        }
        return data;
    }

    public static void updateExpertModeEffect(Context context) {
        setExpertScreenMode(getExpertData(context));
    }
}
