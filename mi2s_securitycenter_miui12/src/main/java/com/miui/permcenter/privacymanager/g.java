package com.miui.permcenter.privacymanager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import com.miui.permcenter.privacymanager.behaviorrecord.o;
import com.miui.permission.PermissionContract;

public class g extends BroadcastReceiver {

    /* renamed from: a  reason: collision with root package name */
    private Handler f6485a;

    public g(Handler handler) {
        this.f6485a = handler;
    }

    public void onReceive(Context context, Intent intent) {
        Message obtainMessage;
        String action = intent.getAction();
        if (PermissionContract.ACTION_USING_PERMISSION_CHANGE.equals(action) || PermissionContract.ACTION_USING_STATUS_BAR_PERMISSION.equals(action)) {
            long longExtra = intent.getLongExtra(PermissionContract.Method.GetUsingPermissionList.EXTRA_PERMISSIONID, 0);
            String[] stringArrayExtra = intent.getStringArrayExtra("extra_data");
            int intExtra = intent.getIntExtra(PermissionContract.Method.GetUsingPermissionList.EXTRA_TYPE, 3);
            if (longExtra == 0 || stringArrayExtra == null) {
                Log.e("BehaviorRecord-Receiver", "Lack privacy info permId: " + longExtra + " ,usingList: " + stringArrayExtra);
                return;
            }
            Bundle bundle = new Bundle();
            bundle.putLong(PermissionContract.Method.GetUsingPermissionList.EXTRA_PERMISSIONID, longExtra);
            bundle.putInt(PermissionContract.Method.GetUsingPermissionList.EXTRA_TYPE, intExtra);
            bundle.putStringArray("extra_data", stringArrayExtra);
            if (intent.getIntExtra(PermissionContract.Method.GetUsingPermissionList.EXTRA_GROUND_STATE, 2) != 0 && o.f(longExtra) && o.d(context)) {
                Message obtainMessage2 = this.f6485a.obtainMessage(2457);
                obtainMessage2.setData(bundle);
                this.f6485a.sendMessage(obtainMessage2);
            }
            if (PermissionContract.ACTION_USING_STATUS_BAR_PERMISSION.equals(action) && o.h(longExtra)) {
                if (intExtra == 2 || intExtra == 3) {
                    obtainMessage = this.f6485a.obtainMessage(2456);
                    obtainMessage.setData(bundle);
                } else {
                    return;
                }
            } else {
                return;
            }
        } else if ("com.miui.action.sync_status_bar".equals(action)) {
            Log.i("BehaviorRecord-Receiver", "user close some monitor, sync now");
            obtainMessage = this.f6485a.obtainMessage(2455);
        } else {
            return;
        }
        this.f6485a.sendMessage(obtainMessage);
    }
}
