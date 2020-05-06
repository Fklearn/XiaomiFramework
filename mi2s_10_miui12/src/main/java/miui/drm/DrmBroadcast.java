package miui.drm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import miui.app.constants.ThemeManagerConstants;

public class DrmBroadcast {
    public static final String ACTION_CHECK_TIME_UP = "miui.intent.action.CHECK_TIME_UP";
    private static final long FIRST_DELAY_AFTER_BOOT = 1800000;
    private static final long INTERVAL = 7200000;
    private static final int REQUEST_CODE = 1;
    private static volatile DrmBroadcast sInstance;
    private static final Object sMutex = new Object();
    private Context mContext;

    public static DrmBroadcast getInstance(Context context) {
        if (sInstance == null) {
            synchronized (sMutex) {
                if (sInstance == null) {
                    sInstance = new DrmBroadcast(context);
                }
            }
        }
        return sInstance;
    }

    public void broadcast() {
        Intent intent = new Intent(ACTION_CHECK_TIME_UP);
        intent.setAction(ACTION_CHECK_TIME_UP);
        ((AlarmManager) this.mContext.getSystemService(ThemeManagerConstants.COMPONENT_CODE_ALARM)).setInexactRepeating(3, FIRST_DELAY_AFTER_BOOT, 7200000, PendingIntent.getBroadcast(this.mContext, 1, intent, 0));
    }

    private DrmBroadcast(Context context) {
        this.mContext = context.getApplicationContext();
        this.mContext.registerReceiver(new ThemeReceiver(), new IntentFilter(ACTION_CHECK_TIME_UP));
    }
}
