package com.miui.permcenter.compact;

import android.content.Context;

public class EnterpriseCompat {
    public static final String TAG = "EnterpriseCompat";

    public static boolean shouldGrantPermission(Context context, String str) {
        Class[] clsArr = {Context.class, String.class};
        try {
            return ((Boolean) ReflectUtilHelper.callStaticObjectMethod(TAG, Class.forName("com.miui.enterprise.ApplicationHelper"), Boolean.TYPE, "shouldGrantPermission", clsArr, context, str)).booleanValue();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }
}
