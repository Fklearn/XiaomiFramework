package com.miui.firstaidkit.d;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Build;
import android.os.UserHandle;
import android.util.Log;
import b.b.o.g.d;
import java.util.List;

public class a {
    public static List<ResolveInfo> a(PackageManager packageManager, Intent intent, int i, UserHandle userHandle) {
        Log.i("DeviceManagerUtil", "default");
        String str = "list is null";
        if (Build.VERSION.SDK_INT > 23) {
            Class cls = List.class;
            try {
                List<ResolveInfo> list = (List) d.b("DeviceManagerUtil", packageManager, cls, "queryBroadcastReceiversAsUser", new Class[]{Intent.class, Integer.TYPE, UserHandle.class}, intent, Integer.valueOf(i), userHandle);
                StringBuilder sb = new StringBuilder();
                sb.append("sdk_int > 23 list = ");
                if (list != null) {
                    str = list.toString();
                }
                sb.append(str);
                Log.d("DeviceManagerUtil", sb.toString());
                return list;
            } catch (Exception e) {
                Log.e("DeviceManagerUtil", "queryBroadcastReceivers error", e);
                return null;
            }
        } else {
            List<ResolveInfo> list2 = (List) d.b("DeviceManagerUtil", packageManager, List.class, "queryBroadcastReceivers", new Class[]{Intent.class, Integer.TYPE, Integer.TYPE}, intent, Integer.valueOf(i), Integer.valueOf(userHandle.getIdentifier()));
            StringBuilder sb2 = new StringBuilder();
            sb2.append("sdk_int <= 23 list = ");
            if (list2 != null) {
                str = list2.toString();
            }
            sb2.append(str);
            Log.d("DeviceManagerUtil", sb2.toString());
            return list2;
        }
    }
}
