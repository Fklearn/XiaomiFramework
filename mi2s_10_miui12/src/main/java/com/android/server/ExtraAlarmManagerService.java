package com.android.server;

import android.app.PendingIntent;
import android.content.Context;
import android.os.WorkSource;

public class ExtraAlarmManagerService {
    public static long recalculateWindowLength(Context context, int type, long triggerAtTime, long windowLength, long interval, PendingIntent operation, boolean isStandalone, WorkSource workSource) {
        return windowLength;
    }
}
