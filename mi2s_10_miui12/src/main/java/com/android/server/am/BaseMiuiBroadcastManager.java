package com.android.server.am;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.os.RemoteException;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.util.ArraySet;
import android.util.Slog;
import com.android.server.job.controllers.JobStatus;
import java.util.ArrayList;
import java.util.List;

public abstract class BaseMiuiBroadcastManager {
    public static final boolean DEBUG_BROADCAST = SystemProperties.getBoolean("debug.broadcast.log", false);
    private static final boolean DEBUG_BROADCAST_BACKGROUND = (DEBUG_BROADCAST);
    static final boolean ENABLE_DELAY_QUEUE;
    static final boolean ENABLE_EXTRA_QUEUES = SystemProperties.getBoolean("persist.sys.m_b_enable", true);
    static final int EXTRA_QUEUE_SIZE;
    private static final int FLAG_RECEIVER_LONGTIME = 2;
    private static final int FLAG_RECEIVER_SYSTEM_APP = 1;
    private static final int QUEUE_CONTROL_FLAGS = 3;
    private static final String TAG_BROADCAST = "BaseMiuiBroadcastManager";
    private static final String[] sLongTimeAction = {"android.net.conn.CONNECTIVITY_CHANGE", "android.intent.action.PACKAGE_ADDED", "android.intent.action.PACKAGE_REMOVED", "android.net.wifi.STATE_CHANGE", "android.net.wifi.SCAN_RESULTS", "com.xiaomi.push.channel_closed", "com.miui.core.intent.ACTION_DUMP_CACHED_LOG", "android.intent.action.BATTERY_CHANGED"};
    private BroadcastQueue mBgLtBroadcastQueue;
    private BroadcastQueue mBgSysBroadcastQueue;
    private BroadcastQueue mFgSysBroadcastQueue;
    private ActivityManagerService mService;

    /* access modifiers changed from: package-private */
    public abstract BroadcastQueue createBroadcastQueue(ActivityManagerService activityManagerService, String str, BroadcastConstants broadcastConstants, boolean z);

    /* access modifiers changed from: package-private */
    public abstract BroadcastRecord createBroadcastRecord(BroadcastQueue broadcastQueue, Intent intent, List list, BroadcastRecord broadcastRecord);

    static {
        int i = 0;
        boolean z = true;
        if (MiuiBroadcastQueue.DELAY_TIME <= 0) {
            z = false;
        }
        ENABLE_DELAY_QUEUE = z;
        if (ENABLE_EXTRA_QUEUES) {
            i = ENABLE_DELAY_QUEUE ? 3 : 2;
        }
        EXTRA_QUEUE_SIZE = i;
    }

    /* access modifiers changed from: package-private */
    public void init(ActivityManagerService service) {
        this.mService = service;
        if (ENABLE_EXTRA_QUEUES) {
            long startTime = SystemClock.uptimeMillis();
            BroadcastConstants foreConstants = new BroadcastConstants("bcast_fg_constants");
            foreConstants.TIMEOUT = JobStatus.DEFAULT_TRIGGER_UPDATE_DELAY;
            BroadcastConstants backConstants = new BroadcastConstants("bcast_bg_constants");
            backConstants.TIMEOUT = 60000;
            this.mFgSysBroadcastQueue = createBroadcastQueue(service, "fg_sys", foreConstants, false);
            this.mBgSysBroadcastQueue = createBroadcastQueue(service, "bg_sys", backConstants, true);
            if (ENABLE_DELAY_QUEUE) {
                BroadcastConstants bgLtConstants = new BroadcastConstants("bcast_bg_constants");
                bgLtConstants.TIMEOUT = 600000;
                this.mBgLtBroadcastQueue = new MiuiBroadcastQueue(service, service.mHandler, MiuiBroadcastQueue.DELADY_QUEUE_NAME, bgLtConstants, true, this);
            }
            Slog.d(TAG_BROADCAST, "init extra BroadcastQueues in " + (SystemClock.uptimeMillis() - startTime) + "ms.");
        }
    }

    /* access modifiers changed from: package-private */
    public boolean isFgBroadcastQueue(BroadcastQueue queue) {
        return queue == this.mService.mFgBroadcastQueue || queue == this.mFgSysBroadcastQueue;
    }

    /* access modifiers changed from: package-private */
    public boolean isFgBroadcastQueue(ArraySet<BroadcastQueue> queues) {
        return queues.contains(this.mService.mFgBroadcastQueue) || queues.contains(this.mFgSysBroadcastQueue);
    }

    /* access modifiers changed from: package-private */
    public boolean initExtraQuqueIfNeed(int startIndex) {
        if (!ENABLE_EXTRA_QUEUES) {
            return false;
        }
        this.mService.mBroadcastQueues[startIndex] = this.mFgSysBroadcastQueue;
        this.mService.mBroadcastQueues[startIndex + 1] = this.mBgSysBroadcastQueue;
        if (!ENABLE_DELAY_QUEUE) {
            return true;
        }
        this.mService.mBroadcastQueues[startIndex + 2] = this.mBgLtBroadcastQueue;
        return true;
    }

    /* access modifiers changed from: package-private */
    public boolean broadcastIntentLocked(boolean parallel, boolean replacePending, BroadcastRecord record) {
        Intent intent = record.intent;
        boolean ordered = record.ordered;
        List receivers = record.receivers;
        if (!ENABLE_EXTRA_QUEUES || ordered) {
            return false;
        }
        if (DEBUG_BROADCAST) {
            Slog.d(TAG_BROADCAST, "ready to send broadcast : " + intent);
        }
        int flags = intent.getFlags();
        if ((flags & 3) != 0) {
            intent.setFlags(flags & -4);
        }
        if (isLongTimeAction(record)) {
            intent.addFlags(2);
            return false;
        }
        sendBroadcastDirect(parallel, replacePending, intent, receivers, record);
        return true;
    }

    private boolean isLongTimeAction(BroadcastRecord r) {
        if (!ENABLE_DELAY_QUEUE) {
            return false;
        }
        Intent intent = r.intent;
        ProcessRecord pr = r.callerApp;
        if (pr == null || pr.curAdj <= 200) {
            for (String action : sLongTimeAction) {
                if (action.equals(intent.getAction())) {
                    if (DEBUG_BROADCAST) {
                        Slog.v(TAG_BROADCAST, "is long time action" + intent);
                    }
                    return true;
                }
            }
            return false;
        }
        if (DEBUG_BROADCAST) {
            Slog.v(TAG_BROADCAST, "the caller " + pr + " is background with" + intent);
        }
        return true;
    }

    private void realSendBroadcastLocked(boolean parallel, boolean replacePending, Intent intent, List receivers, BroadcastRecord record) {
        Intent intent2 = intent;
        boolean replaced = true;
        BroadcastQueue queue = broadcastQueueForIntent(intent2, true);
        BroadcastRecord r = createBroadcastRecord(queue, intent2, receivers, record);
        if (!parallel) {
            if (DEBUG_BROADCAST) {
                Slog.v(TAG_BROADCAST, "Enqueueing ordered broadcast : prev had ");
            }
            if (DEBUG_BROADCAST) {
                Slog.i(TAG_BROADCAST, "Enqueueing broadcast " + r.intent.getAction());
            }
            BroadcastRecord oldRecord = replacePending ? queue.replaceOrderedBroadcastLocked(r) : null;
            if (oldRecord == null) {
                queue.enqueueOrderedBroadcastLocked(r);
                queue.scheduleBroadcastsLocked();
            } else if (oldRecord.resultTo != null) {
                try {
                    broadcastQueueForIntent(oldRecord.intent, true).performReceiveLocked(oldRecord.callerApp, oldRecord.resultTo, oldRecord.intent, 0, (String) null, (Bundle) null, false, false, oldRecord.userId);
                } catch (RemoteException e) {
                    Slog.w(TAG_BROADCAST, "Failure [" + queue.mQueueName + "] sending broadcast result of " + intent2, e);
                }
            }
        } else {
            if (DEBUG_BROADCAST) {
                Slog.v(TAG_BROADCAST, "Enqueueing parallel broadcast " + r);
            }
            if (!replacePending || queue.replaceParallelBroadcastLocked(r) == null) {
                replaced = false;
            }
            if (!replaced) {
                queue.enqueueParallelBroadcastLocked(r);
                queue.scheduleBroadcastsLocked();
            }
        }
    }

    private void sendBroadcastDirect(boolean parallel, boolean replacePending, Intent intent, List receivers, BroadcastRecord record) {
        ArrayList arrayList = new ArrayList();
        List systemReceivers = new ArrayList();
        try {
            int size = receivers.size();
            for (int i = 0; i < size; i++) {
                Object receiver = receivers.get(i);
                if (receiver != null && (receiver instanceof BroadcastFilter)) {
                    BroadcastFilter bf = (BroadcastFilter) receiver;
                    if (!(bf.receiverList == null || bf.receiverList.app == null || bf.receiverList.app.info == null)) {
                        addReceiverToListByFlag(arrayList, systemReceivers, bf, bf.receiverList.app.info.flags);
                    }
                } else if (receiver != null) {
                    ResolveInfo ri = (ResolveInfo) receiver;
                    addReceiverToListByFlag(arrayList, systemReceivers, ri, ri.activityInfo.applicationInfo.flags);
                }
            }
            if (arrayList.size() > 0) {
                realSendBroadcastLocked(parallel, replacePending, new Intent(intent), arrayList, record);
            }
            if (systemReceivers.size() > 0) {
                Intent newIntent = new Intent(intent);
                newIntent.addFlags(1);
                realSendBroadcastLocked(parallel, replacePending, newIntent, systemReceivers, record);
            }
        } catch (Exception e) {
        }
    }

    private void addReceiverToListByFlag(List nonSystemReceivers, List systemReceivers, Object receiver, int flags) {
        if ((flags & 1) == 0) {
            nonSystemReceivers.add(receiver);
            if (DEBUG_BROADCAST) {
                Slog.i(TAG_BROADCAST, "add app " + receiver + " to nonSystemReceivers");
                return;
            }
            return;
        }
        if (DEBUG_BROADCAST) {
            Slog.i(TAG_BROADCAST, "add app " + receiver + " to systemReceivers");
        }
        systemReceivers.add(receiver);
    }

    private BroadcastQueue broadcastQueueForIntent(Intent intent, boolean noDelay) {
        if (DEBUG_BROADCAST) {
            Slog.d(TAG_BROADCAST, "ready to broadcastQueueForIntentt : " + intent);
        }
        return broadcastQueueByFlag(intent.getFlags(), noDelay);
    }

    /* access modifiers changed from: package-private */
    public BroadcastQueue broadcastQueueByFlag(int flags) {
        return broadcastQueueByFlag(flags, false);
    }

    private BroadcastQueue broadcastQueueByFlag(int flags, boolean noDelay) {
        boolean isFg = (268435456 & flags) != 0;
        if (DEBUG_BROADCAST_BACKGROUND) {
            StringBuilder sb = new StringBuilder();
            sb.append("Broadcast  on ");
            sb.append(isFg ? "foreground" : "background");
            sb.append(" queue, flags : ");
            sb.append(flags);
            Slog.i(TAG_BROADCAST, sb.toString());
        }
        if (ENABLE_EXTRA_QUEUES) {
            if (ENABLE_DELAY_QUEUE && !noDelay && (flags & 2) != 0) {
                return this.mBgLtBroadcastQueue;
            }
            if ((flags & 1) != 0) {
                return isFg ? this.mFgSysBroadcastQueue : this.mBgSysBroadcastQueue;
            }
            if (isFg) {
                return this.mService.mFgBroadcastQueue;
            }
            return this.mService.mBgBroadcastQueue;
        } else if (isFg) {
            return this.mService.mFgBroadcastQueue;
        } else {
            return this.mService.mBgBroadcastQueue;
        }
    }

    public List updateLongTimeBroadcastRecord(BroadcastRecord r, boolean order) {
        List bgReceivers = new ArrayList();
        ArrayList arrayList = new ArrayList();
        List receivers = r.receivers;
        if (receivers == null) {
            return null;
        }
        int size = receivers.size();
        for (int i = 0; i < size; i++) {
            Object receiver = receivers.get(i);
            if (receiver != null && (receiver instanceof BroadcastFilter)) {
                BroadcastFilter bf = (BroadcastFilter) receiver;
                if (bf.receiverList == null || bf.receiverList.app == null) {
                    if (bf.receiverList != null) {
                        if (DEBUG_BROADCAST) {
                            Slog.i(TAG_BROADCAST, "add app " + receiver + " to longtime queue");
                        }
                        bgReceivers.add(bf);
                    }
                } else if (isTopApp(bf.receiverList.app.info)) {
                    if (DEBUG_BROADCAST) {
                        Slog.i(TAG_BROADCAST, "add app " + receiver + " to foreground queue");
                    }
                    arrayList.add(bf);
                } else {
                    if (DEBUG_BROADCAST) {
                        Slog.i(TAG_BROADCAST, "add app " + receiver + " to longtime queue");
                    }
                    bgReceivers.add(bf);
                }
            } else if (receiver != null) {
                if (isTopApp(((ResolveInfo) receiver).activityInfo.applicationInfo)) {
                    if (DEBUG_BROADCAST) {
                        Slog.i(TAG_BROADCAST, "resolve " + receiver + " to fregournd queue");
                    }
                    arrayList.add(receiver);
                } else {
                    if (DEBUG_BROADCAST) {
                        Slog.i(TAG_BROADCAST, "resolve " + receiver + " to longtime queue");
                    }
                    bgReceivers.add(receiver);
                }
            }
        }
        if (arrayList.size() > 0) {
            r.intent.setFlags(r.intent.getFlags() & -3);
            sendBroadcastDirect(!order, false, r.intent, arrayList, r);
        }
        r.intent.setFlags(r.intent.getFlags() | 2);
        return bgReceivers;
    }

    private boolean isTopApp(ApplicationInfo info) {
        if ((info.flags & 8) != 0) {
            return true;
        }
        if (MiuiSysUserServiceHelper.sTopPackage == null || !MiuiSysUserServiceHelper.sTopPackage.equals(info.packageName)) {
            return false;
        }
        return true;
    }
}
