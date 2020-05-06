package com.android.server.inputmethod;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.inputmethodservice.InputMethodAnalyticsUtil;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.Pair;
import android.view.inputmethod.InputMethodInfo;
import com.android.server.inputmethod.InputMethodSubtypeSwitchingController;
import com.android.server.inputmethod.InputMethodUtils;
import java.util.ArrayList;
import java.util.List;
import miui.os.Build;

public class InputMethodManagerServiceInjector {
    public static void enableSystemIMEsIfThereIsNoEnabledIME(ArrayList<InputMethodInfo> methodList, InputMethodUtils.InputMethodSettings settings) {
        if (!Build.IS_CM_CUSTOMIZATION_TEST && methodList != null && settings != null) {
            List<Pair<String, ArrayList<String>>> enabledInputMethodsList = settings.getEnabledInputMethodsAndSubtypeListLocked();
            InputMethodInfo systemInputMethod = null;
            for (int i = 0; i < methodList.size(); i++) {
                InputMethodInfo inputMethodInfo = methodList.get(i);
                if ((inputMethodInfo.getServiceInfo().applicationInfo.flags & 1) != 0) {
                    systemInputMethod = inputMethodInfo;
                }
                if (enabledInputMethodsList != null) {
                    for (Pair<String, ArrayList<String>> pair : enabledInputMethodsList) {
                        if (TextUtils.equals((CharSequence) pair.first, inputMethodInfo.getId())) {
                            return;
                        }
                    }
                    continue;
                }
            }
            if (systemInputMethod != null) {
                settings.appendAndPutEnabledInputMethodLocked(systemInputMethod.getId(), false);
            }
        }
    }

    public static boolean shouldResetIME(Intent intent, String[] packages, int uid, boolean doit) {
        return true;
    }

    public static boolean checkProcessRunning(String processName) {
        if (TextUtils.isEmpty(processName)) {
            return false;
        }
        try {
            for (ActivityManager.RunningAppProcessInfo info : ActivityManager.getService().getRunningAppProcesses()) {
                if (processName.equalsIgnoreCase(info.processName)) {
                    return true;
                }
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void onSwitchIME(Context context, InputMethodInfo curInputMethodInfo, String lastInputMethodId, List<InputMethodSubtypeSwitchingController.ImeSubtypeListItem> list, InputMethodUtils.InputMethodSettings inputMethodSettings, ArrayMap<String, InputMethodInfo> arrayMap) {
        if (!TextUtils.equals(curInputMethodInfo.getId(), lastInputMethodId)) {
            InputMethodAnalyticsUtil.addNotificationPanelRecord(context, curInputMethodInfo.getPackageName());
        }
    }
}
