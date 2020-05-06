package com.android.server.location;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.WorkSource;
import android.util.SparseArray;

public class LocationOpHandler extends Handler {
    private static final int MSG_DELAYED_LOCATION_OP = 1;
    private static final String TAG = "LocationOpHanlder";
    private final Context mContext;
    private SparseArray<LocationOpRecord> mLastLocationOps = new SparseArray<>();
    private final Object mLock = new Object();
    private final WifiManager mWifiManager;

    public LocationOpHandler(Context context, Looper looper) {
        super(looper);
        this.mContext = context;
        this.mWifiManager = (WifiManager) this.mContext.getSystemService("wifi");
    }

    public void handleMessage(Message msg) {
        if (msg.what == 1 && msg.arg1 == 2) {
            postWifiScanRequest(msg.arg2);
        }
    }

    private void postWifiScanRequest(int uid) {
        this.mWifiManager.startScan(new WorkSource(uid));
    }

    public boolean isFrequenctlyOp(int uid, int op, long optime, int minInterval) {
        if (op != 2 && op != 3) {
            return false;
        }
        boolean isFrequenctlyOp = false;
        synchronized (this.mLock) {
            LocationOpRecord lastOp = this.mLastLocationOps.get(op, (Object) null);
            if (lastOp != null && op == 2 && optime > lastOp.timestamp && optime < lastOp.timestamp + ((long) minInterval)) {
                isFrequenctlyOp = true;
            }
        }
        return isFrequenctlyOp;
    }

    public void setFollowupAction(int uid, int op, long optime, int minInterval) {
        synchronized (this.mLock) {
            if (op == 2) {
                long delay = (long) minInterval;
                LocationOpRecord lastOp = this.mLastLocationOps.get(op, (Object) null);
                if (lastOp != null && optime > lastOp.timestamp) {
                    delay -= optime - lastOp.timestamp;
                }
                sendMessageDelayed(Message.obtain(this, 1, op, uid), delay);
            }
        }
    }

    public void updateLastLocationOp(int uid, int op, long optime) {
        if (op == 2 || op == 3) {
            synchronized (this.mLock) {
                this.mLastLocationOps.put(op, new LocationOpRecord(uid, op, optime));
                if (op == 2) {
                    removeMessages(1);
                }
            }
        }
    }

    public class LocationOpRecord {
        int locationOp;
        long timestamp;
        int uid;

        public LocationOpRecord(int uid2, int locationOp2, long timestamp2) {
            this.uid = uid2;
            this.locationOp = locationOp2;
            this.timestamp = timestamp2;
        }
    }
}
