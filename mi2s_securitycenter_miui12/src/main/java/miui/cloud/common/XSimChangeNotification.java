package miui.cloud.common;

import android.content.Intent;

public class XSimChangeNotification {
    public static final String BROADCAST_ACTION_CLOUDID_CHANGED = "com.xiaomi.X_SIM_CLOUDID_CHANGED";
    public static final String BROADCAST_ACTION_INSERTED = "com.xiaomi.X_SIM_INSERTED";
    public static final String BROADCAST_ACTION_NEW_CLOUDID = "com.xiaomi.X_SIM_NEW_CLOUDID";
    public static final String BROADCAST_ACTION_REMOVED = "com.xiaomi.X_SIM_REMOVED";
    public static final String BROADCAST_ACTION_SIMID_READY = "com.xiaomi.X_SIM_SIMID_READY";
    public static final String BROADCAST_EXTRA_KEY_CLOUD_ID = "cloud_id";
    public static final String BROADCAST_EXTRA_KEY_SIM_ID = "sim_id";
    public static final String BROADCAST_EXTRA_KEY_SLOT_ID = "slot_id";

    public static Intent getBroadcastCloudIdChangedIntent(int i, String str) {
        Intent intent = new Intent(BROADCAST_ACTION_CLOUDID_CHANGED);
        intent.putExtra("slot_id", i);
        intent.putExtra(BROADCAST_EXTRA_KEY_CLOUD_ID, str);
        return intent;
    }

    public static Intent getBroadcastNewCloudIdIntent(int i, String str) {
        Intent intent = new Intent(BROADCAST_ACTION_NEW_CLOUDID);
        intent.putExtra("slot_id", i);
        intent.putExtra(BROADCAST_EXTRA_KEY_CLOUD_ID, str);
        return intent;
    }

    public static Intent getBroadcastSIMIdReadyIntent(int i, String str) {
        Intent intent = new Intent(BROADCAST_ACTION_SIMID_READY);
        intent.putExtra("slot_id", i);
        intent.putExtra(BROADCAST_EXTRA_KEY_SIM_ID, str);
        return intent;
    }

    public static Intent getBroadcastSIMInsertedIntent(int i, String str) {
        Intent intent = new Intent(BROADCAST_ACTION_INSERTED);
        intent.putExtra("slot_id", i);
        intent.putExtra(BROADCAST_EXTRA_KEY_SIM_ID, str);
        return intent;
    }

    public static Intent getBroadcastSIMRemovedIntent(int i) {
        Intent intent = new Intent(BROADCAST_ACTION_REMOVED);
        intent.putExtra("slot_id", i);
        return intent;
    }
}
