package com.android.server.am;

import android.content.Intent;
import java.util.List;

public class MiuiBroadcastManager extends BaseMiuiBroadcastManager {
    private static volatile MiuiBroadcastManager sInstance;
    static final Object sInstanceSync = new Object();

    static MiuiBroadcastManager getInstance(ActivityManagerService service) {
        if (sInstance == null) {
            synchronized (sInstanceSync) {
                if (sInstance == null) {
                    sInstance = new MiuiBroadcastManager(service);
                }
            }
        }
        return sInstance;
    }

    private MiuiBroadcastManager(ActivityManagerService service) {
        init(service);
    }

    static int getExtraQueueSize() {
        return BaseMiuiBroadcastManager.EXTRA_QUEUE_SIZE;
    }

    static boolean isExtraQueueEnabled() {
        return BaseMiuiBroadcastManager.ENABLE_EXTRA_QUEUES;
    }

    /* access modifiers changed from: package-private */
    public BroadcastQueue createBroadcastQueue(ActivityManagerService service, String name, BroadcastConstants constants, boolean allowDelayBehindServices) {
        return new BroadcastQueue(service, service.mHandler, name, constants, allowDelayBehindServices);
    }

    /* access modifiers changed from: package-private */
    public BroadcastRecord createBroadcastRecord(BroadcastQueue queue, Intent intent, List receivers, BroadcastRecord old) {
        BroadcastRecord broadcastRecord = old;
        return new BroadcastRecord(queue, intent, broadcastRecord.callerApp, broadcastRecord.callerPackage, broadcastRecord.callingPid, broadcastRecord.callingUid, false, broadcastRecord.resolvedType, broadcastRecord.requiredPermissions, broadcastRecord.appOp, broadcastRecord.options, receivers, broadcastRecord.resultTo, broadcastRecord.resultCode, broadcastRecord.resultData, broadcastRecord.resultExtras, broadcastRecord.ordered, broadcastRecord.sticky, broadcastRecord.initialSticky, broadcastRecord.userId, broadcastRecord.allowBackgroundActivityStarts, broadcastRecord.timeoutExempt);
    }
}
