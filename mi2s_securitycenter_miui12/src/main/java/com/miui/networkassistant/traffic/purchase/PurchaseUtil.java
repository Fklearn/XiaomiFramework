package com.miui.networkassistant.traffic.purchase;

import android.content.Context;
import com.miui.securitycenter.R;
import miui.provider.ExtraNetwork;

public class PurchaseUtil {
    public static final String URL_PURCHASE_PACKAGE_LIST = "https://api.miui.security.xiaomi.com/views/netassist/productlist.html";

    public static void launchUrl(Context context, String str, int i) {
        Context context2 = context;
        ExtraNetwork.navigateToRichWebActivity(context2, String.format("%s?slotid=%s", new Object[]{URL_PURCHASE_PACKAGE_LIST, Integer.valueOf(i)}), context.getResources().getString(R.string.main_toolbar_purchase), true, str, false);
    }

    public static void launchUrl(Context context, String str, String str2) {
        ExtraNetwork.navigateToRichWebActivity(context, str, context.getResources().getString(R.string.main_toolbar_purchase), true, str2, true);
    }

    public static void launchUrl(Context context, String str, String str2, boolean z) {
        ExtraNetwork.navigateToRichWebActivity(context, str, str2, z, "", false);
    }
}
