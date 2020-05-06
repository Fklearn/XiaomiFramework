package com.android.server.notification;

import android.content.Context;
import android.content.Intent;
import com.android.server.am.AutoStartManagerService;

public class ManagedServicesInjector {
    public static boolean canBindService(Context context, Intent service, int userId) {
        return AutoStartManagerService.isAllowStartService(context, service, userId);
    }
}
