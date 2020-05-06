package com.miui.firstaidkit.model.performance;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.accessibility.AccessibilityManager;
import b.b.c.j.A;
import b.b.c.j.x;
import com.miui.securitycenter.R;
import com.miui.securityscan.model.AbsModel;
import java.util.ArrayList;
import java.util.List;

public class AuxiliaryFunctionModel extends AbsModel {
    private static final String TAG = "AuxiliaryFunctionModel";
    private static List<String> whiteList = new ArrayList();

    static {
        whiteList.add("com.baidu.input_mi");
    }

    public AuxiliaryFunctionModel(String str, Integer num) {
        super(str, num);
        setDelayOptimized(true);
        setTrackStr("auxiliary_function");
        setTrackIgnoreStr(getTrackStr() + "_ignore");
    }

    public String getButtonTitle() {
        return getContext().getString(R.string.first_aid_card_kadun_button3);
    }

    public String getDesc() {
        return "";
    }

    public int getIndex() {
        return 55;
    }

    public String getSummary() {
        return getContext().getString(R.string.first_aid_card_kadun_summary3);
    }

    public String getTitle() {
        return getContext().getString(R.string.first_aid_card_kadun_title);
    }

    public void optimize(Context context) {
        if (!x.a(context, new Intent("android.settings.ACCESSIBILITY_SETTINGS"), 100)) {
            A.a(context, (int) R.string.app_not_installed_toast);
        }
    }

    public void scan() {
        try {
            for (AccessibilityServiceInfo resolveInfo : ((AccessibilityManager) getContext().getSystemService("accessibility")).getEnabledAccessibilityServiceList(-1)) {
                String str = resolveInfo.getResolveInfo().serviceInfo.packageName;
                boolean i = x.i(getContext(), str);
                boolean contains = whiteList.contains(str);
                if (!i && !contains) {
                    setSafe(AbsModel.State.DANGER);
                    return;
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "scan", e);
        }
    }
}
