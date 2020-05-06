package com.android.server;

import com.miui.server.AccessController;
import miui.os.Build;

public class TelephonyRegistryInjector {
    public static boolean isDummySubId(String callingPackage, int subId) {
        if ((!Build.IS_CM_CUSTOMIZATION_TEST && !Build.IS_CT_CUSTOMIZATION_TEST) || !AccessController.PACKAGE_SYSTEMUI.equals(callingPackage)) {
            return false;
        }
        if (subId == -2 || subId == -3) {
            return true;
        }
        return false;
    }

    public static boolean idMatchForDummy(String callingPackage, int rSubId, int rPhoneId, int phoneId) {
        if (!isDummySubId(callingPackage, rSubId) || rPhoneId != phoneId) {
            return false;
        }
        return true;
    }
}
