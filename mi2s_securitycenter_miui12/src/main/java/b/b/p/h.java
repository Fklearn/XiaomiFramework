package b.b.p;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

public class h extends BroadcastReceiver {
    public void onReceive(Context context, Intent intent) {
        if (intent != null && "miui.intent.action.ad.UNIFIED_AD_UPDATING".equals(intent.getAction())) {
            String stringExtra = intent.getStringExtra("key_tag_id");
            if (!TextUtils.isEmpty(stringExtra)) {
                f.a(context).b(stringExtra);
                Log.d("RemoteUnifiedAdService", "Receive broadCast, TagId:" + stringExtra);
            }
        }
    }
}
