package b.b.a.c;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import b.b.a.e.c;
import b.b.a.e.n;
import miui.telephony.SubscriptionManager;

public class a extends BroadcastReceiver {
    public void onReceive(Context context, Intent intent) {
        String str;
        String action = intent.getAction();
        int intExtra = intent.getIntExtra("notification_show_type", -1);
        int intExtra2 = intent.getIntExtra("key_block_log_type", 2);
        String stringExtra = intent.getStringExtra("notification_intercept_number");
        int intExtra3 = intent.getIntExtra("notification_block_type", -1);
        int b2 = n.b(context, SubscriptionManager.getDefault().getSlotIdForSubscription(intent.getIntExtra("key_sim_id", 0)));
        Log.d("AntiSpamReceiver", "Receive action = " + action + "; content = " + intExtra2 + "; category = " + intExtra + "; simId = " + b2);
        if ("miui.intent.action.FIREWALL_UPDATED".equals(action)) {
            c.b(context, true);
            int a2 = c.a(context, b2);
            if (a2 == 0 || (a2 == 1 && intExtra == 1)) {
                n.a(context, stringExtra, intExtra2, intExtra3);
            }
            if (intExtra2 == 2) {
                str = "sms";
            } else if (intExtra2 == 1) {
                str = "call";
            } else {
                return;
            }
            b.b.a.a.a.b(str);
        }
    }
}
