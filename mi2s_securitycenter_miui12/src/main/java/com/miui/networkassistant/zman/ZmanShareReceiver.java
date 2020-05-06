package com.miui.networkassistant.zman;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

public class ZmanShareReceiver extends BroadcastReceiver {
    private static final String ACTION_IMAGE_SHARED = "com.miui.zman.intent.action.SHARED";
    private static final String ACTION_SETTINGS_CHANGE = "com.miui.zman.intent.action.VIEW_CHANGE";
    private static final String ACTION_SETTINGS_SHOW = "com.miui.zman.intent.action.VIEW_SHOW";
    private static final String IS_MULTI_IMAGE = "is_multi_image";

    private void handleImageSharedAction(Context context, Intent intent) {
        boolean booleanExtra = intent.getBooleanExtra(IS_MULTI_IMAGE, true);
        boolean booleanExtra2 = intent.getBooleanExtra("param_image_have_location", true);
        boolean booleanExtra3 = intent.getBooleanExtra("param_image_have_camera", true);
        String stringExtra = intent.getStringExtra("param_src_packagename");
        if (booleanExtra) {
            ZmanHelper.trackSecuritySharedImagesEvent(context.getApplicationContext(), stringExtra, booleanExtra2, booleanExtra3);
        } else {
            ZmanHelper.trackSecuritySharedImageEvent(context.getApplicationContext(), stringExtra, booleanExtra2, booleanExtra3);
        }
    }

    private void handleOnceSettingsChange(Context context, Intent intent) {
        ZmanHelper.trackOnceSettingsChangeEvent(context.getApplicationContext(), intent.getStringExtra("param_src_packagename"));
    }

    private void handleViewShow(Context context, Intent intent) {
        String stringExtra = intent.getStringExtra("param_src_packagename");
        String stringExtra2 = intent.getStringExtra("view_key");
        if (!TextUtils.isEmpty(stringExtra2)) {
            ZmanHelper.trackViewShowEvent(context, stringExtra2, stringExtra);
        }
    }

    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (!TextUtils.isEmpty(action)) {
            char c2 = 65535;
            int hashCode = action.hashCode();
            if (hashCode != -1798851276) {
                if (hashCode != 268671144) {
                    if (hashCode == 1717254183 && action.equals(ACTION_SETTINGS_CHANGE)) {
                        c2 = 2;
                    }
                } else if (action.equals(ACTION_IMAGE_SHARED)) {
                    c2 = 0;
                }
            } else if (action.equals(ACTION_SETTINGS_SHOW)) {
                c2 = 1;
            }
            if (c2 == 0) {
                handleImageSharedAction(context, intent);
            } else if (c2 == 1) {
                handleViewShow(context, intent);
            } else if (c2 == 2) {
                handleOnceSettingsChange(context, intent);
            }
        }
    }
}
