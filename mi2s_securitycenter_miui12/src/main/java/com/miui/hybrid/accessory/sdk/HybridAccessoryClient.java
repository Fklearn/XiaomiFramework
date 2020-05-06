package com.miui.hybrid.accessory.sdk;

import android.content.Context;
import com.miui.hybrid.accessory.sdk.icondialog.a;
import java.util.List;
import java.util.Map;

public class HybridAccessoryClient {
    public static final String TAG = "HybridAccessoryClient";

    public static void showCreateIconDialog(Context context, List<String> list, long j, Map<String, String> map) {
        a.a(context, list, j, map);
    }

    public static void showCreateIconDialog(Context context, List<String> list, Map<String, String> map) {
        a.a(context, list, 1000, map);
    }
}
