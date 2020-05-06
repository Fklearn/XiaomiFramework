package com.android.server.am;

import android.os.Handler;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.util.Slog;
import java.lang.reflect.Field;
import java.util.List;
import miui.util.ReflectionUtils;

class MiuiBroadcastQueue extends BroadcastQueue {
    public static final String DELADY_QUEUE_NAME = "longtime";
    public static final long DELAY_TIME = SystemProperties.getLong("persist.sys.m_b_delay", -1);
    private static final int MAX_COUNT_ONCE = 5;
    private static final String TAG = "MiuiBroadcastQueue";
    private Field fEnqueueClockTime;
    private long mLastProcessBroadcastTime = 0;
    private boolean mSlowQueue;
    private BaseMiuiBroadcastManager mbm;

    MiuiBroadcastQueue(ActivityManagerService service, Handler handler, String name, BroadcastConstants constants, boolean allowDelayBehindServices, BaseMiuiBroadcastManager bm) {
        super(service, handler, name, constants, allowDelayBehindServices);
        this.mbm = bm;
        this.mSlowQueue = this.mQueueName.equals(DELADY_QUEUE_NAME);
        if (BaseMiuiBroadcastManager.DEBUG_BROADCAST) {
            Slog.v(TAG, "init queue" + name);
        }
        try {
            this.fEnqueueClockTime = ReflectionUtils.findField(BroadcastRecord.class, "enqueueClockTime");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void scheduleBroadcastsLocked() {
        if (!this.mBroadcastsScheduled) {
            if (BaseMiuiBroadcastManager.DEBUG_BROADCAST) {
                Slog.v(TAG, "Schedule broadcasts [" + this.mQueueName + "]: current=" + this.mBroadcastsScheduled);
            }
            this.mHandler.sendMessageDelayed(this.mHandler.obtainMessage(200, this), isDelay() ? DELAY_TIME : 0);
            this.mBroadcastsScheduled = true;
        }
    }

    public void enqueueParallelBroadcastLocked(BroadcastRecord r) {
        List newList;
        if (this.mSlowQueue) {
            List receiveList = this.mbm.updateLongTimeBroadcastRecord(r, false);
            if (receiveList != null && receiveList.size() > 5) {
                for (int i = 0; i < receiveList.size() / 5; i++) {
                    if (i == (receiveList.size() / 5) - 1) {
                        newList = receiveList.subList(i * 5, receiveList.size());
                    } else {
                        newList = receiveList.subList(i * 5, (i * 5) + 5);
                    }
                    BroadcastRecord newR = this.mbm.createBroadcastRecord(this, r.intent, newList, r);
                    Slog.v(TAG, "Enqueueing parallel broadcast on slow queue" + newR);
                    this.mParallelBroadcasts.add(newR);
                    setEnqueueClockTime(newR);
                }
            } else if (receiveList != null && receiveList.size() > 0) {
                BroadcastRecord newR2 = this.mbm.createBroadcastRecord(this, r.intent, receiveList, r);
                Slog.v(TAG, "Enqueueing parallel broadcast on slow queue" + newR2);
                this.mParallelBroadcasts.add(newR2);
                setEnqueueClockTime(newR2);
            }
        } else {
            super.enqueueParallelBroadcastLocked(r);
        }
    }

    public void enqueueOrderedBroadcastLocked(BroadcastRecord r) {
        if (!this.mSlowQueue || r.ordered) {
            super.enqueueOrderedBroadcastLocked(r);
            return;
        }
        List receivers = this.mbm.updateLongTimeBroadcastRecord(r, true);
        if (receivers != null && receivers.size() > 0) {
            BroadcastRecord newR = this.mbm.createBroadcastRecord(this, r.intent, receivers, r);
            Slog.v(TAG, "Enqueueing order broadcast on slow queue" + newR);
            this.mDispatcher.enqueueOrderedBroadcastLocked(r);
            setEnqueueClockTime(newR);
        }
    }

    /* access modifiers changed from: package-private */
    public void processNextBroadcast(boolean fromMsg) {
        if (isDelay()) {
            long now = SystemClock.uptimeMillis();
            if (now - this.mLastProcessBroadcastTime >= DELAY_TIME || !fromMsg) {
                if (BaseMiuiBroadcastManager.DEBUG_BROADCAST) {
                    Slog.v(TAG, "process delay broadcast");
                }
                this.mLastProcessBroadcastTime = now;
                super.processNextBroadcast(fromMsg, true);
                if (BaseMiuiBroadcastManager.DEBUG_BROADCAST) {
                    Slog.v(TAG, "process delay broadcast cost " + (SystemClock.uptimeMillis() - now) + " ms");
                    return;
                }
                return;
            }
            this.mBroadcastsScheduled = false;
            scheduleBroadcastsLocked();
            return;
        }
        super.processNextBroadcast(fromMsg);
    }

    private boolean isDelay() {
        return this.mSlowQueue;
    }

    private void setEnqueueClockTime(BroadcastRecord r) {
        try {
            if (this.fEnqueueClockTime != null) {
                this.fEnqueueClockTime.setLong(r, System.currentTimeMillis());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
