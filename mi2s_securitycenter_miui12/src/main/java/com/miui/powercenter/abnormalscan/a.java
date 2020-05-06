package com.miui.powercenter.abnormalscan;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import b.b.c.j.v;
import b.b.c.j.x;
import com.miui.powercenter.PowerCenter;
import com.miui.powercenter.utils.l;
import com.miui.securitycenter.R;
import java.util.ArrayList;
import java.util.Iterator;
import org.json.JSONArray;
import org.json.JSONObject;

public class a extends BroadcastReceiver {
    private static PendingIntent a(Context context, int i, ArrayList<AbScanModel> arrayList) {
        Intent intent = new Intent(context, PowerCenter.class);
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("abnormal_list", arrayList);
        intent.putExtra("abnormal_model", bundle);
        intent.putExtra("enter_homepage_way", "00004");
        return PendingIntent.getActivity(context, 0, intent, 134217728);
    }

    private void a(Context context, ArrayList<AbScanModel> arrayList) {
        if (arrayList != null && arrayList.get(0) != null && !TextUtils.isEmpty(arrayList.get(0).getAbnormalPkg())) {
            CharSequence j = x.j(context, arrayList.get(0).getAbnormalPkg());
            String string = context.getString(R.string.notification_battery_scan_consume_abnormal_title_set, new Object[]{j});
            if (arrayList.size() > 1) {
                string = context.getResources().getQuantityString(R.plurals.notification_battery_scan_consume_abnormal_title_split, arrayList.size(), new Object[]{j, Integer.valueOf(arrayList.size())});
            }
            String string2 = context.getString(R.string.notification_battery_scan_consume_abnormal_summary);
            Notification.Builder a2 = v.a(context, "com.miui.securitycenter");
            a2.setSmallIcon(R.drawable.powercenter_small_icon).setLargeIcon(l.a(context)).setContentTitle(string).setContentText(string2).setContentIntent(a(context, 0, arrayList)).setAutoCancel(true);
            Notification build = a2.build();
            b.b.o.a.a.a(build, true);
            NotificationManager notificationManager = (NotificationManager) context.getSystemService("notification");
            v.a(notificationManager, "com.miui.securitycenter", context.getResources().getString(R.string.notify_channel_name_security), 5);
            notificationManager.notify(R.string.notification_battery_consume_abnormal_title, build);
        }
    }

    public void a(Context context, String str) {
        boolean z;
        ArrayList arrayList = new ArrayList();
        try {
            JSONArray jSONArray = new JSONArray(str);
            int length = jSONArray.length();
            for (int i = 0; i < length; i++) {
                JSONObject jSONObject = jSONArray.getJSONObject(i);
                String next = jSONObject.keys().next();
                int optInt = jSONObject.optInt(next);
                Iterator it = arrayList.iterator();
                while (true) {
                    if (!it.hasNext()) {
                        z = true;
                        break;
                    }
                    AbScanModel abScanModel = (AbScanModel) it.next();
                    if (!TextUtils.isEmpty(next) && next.equals(abScanModel.getAbnormalPkg())) {
                        abScanModel.getAbnormalReason().add(Integer.valueOf(optInt));
                        z = false;
                        break;
                    }
                }
                if (z) {
                    ArrayList arrayList2 = new ArrayList();
                    arrayList2.add(Integer.valueOf(optInt));
                    arrayList.add(new AbScanModel(next, arrayList2));
                }
            }
        } catch (Exception e) {
            Log.e("AbPowerConsumeReceiver", "parse abnormal data exception: ", e);
        }
        a(context, (ArrayList<AbScanModel>) arrayList);
    }

    public void onReceive(Context context, Intent intent) {
        if (intent == null) {
            Log.d("AbPowerConsumeReceiver", "power notify intent is null");
        }
        if ("com.miui.powerkeeper.action.POWER_NOTIFY".equals(intent.getAction())) {
            String stringExtra = intent.getStringExtra("notify_info");
            Log.d("AbPowerConsumeReceiver", "power notify info is: " + stringExtra);
            a(context, stringExtra);
        }
    }
}
