package com.android.server.content;

import android.text.TextUtils;
import android.util.Log;
import android.util.Slog;
import java.io.IOException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

public class MiSyncPause {
    private static final long PAUSE_TIME_MAX_INTERVAL = 86400000;
    private static final long PAUSE_TIME_START_DELTA = 60000;
    private static final String TAG = "Sync";
    private static final int VERSION = 1;
    private static final String XML_ATTR_ACCOUNT_NAME = "account_name";
    private static final String XML_ATTR_PAUSE_END_TIME = "end_time";
    private static final String XML_ATTR_UID = "uid";
    private static final String XML_ATTR_VERSION = "version";
    public static final String XML_FILE_NAME = "mi_pause";
    public static final int XML_FILE_VERSION = 1;
    private static final String XML_TAG_ITEM = "sync_pause_item";
    private String mAccountName;
    private long mPauseEndTimeMills = 0;
    private long mPauseStartTimeMills = 0;
    private int mUid;

    public MiSyncPause(int uid, String accountName) {
        this.mUid = uid;
        this.mAccountName = accountName;
    }

    public int getUid() {
        return this.mUid;
    }

    public String getAccountName() {
        return this.mAccountName;
    }

    public long getPauseEndTime() {
        return this.mPauseEndTimeMills;
    }

    public long getResumeTimeLeft() {
        long currentTimeMills = System.currentTimeMillis();
        if (currentTimeMills <= this.mPauseStartTimeMills - 60000) {
            return 0;
        }
        long j = this.mPauseEndTimeMills;
        if (j <= currentTimeMills) {
            return 0;
        }
        return j - currentTimeMills;
    }

    public boolean setPauseToTime(long pauseTimeMillis) {
        if (pauseTimeMillis == 0) {
            if (Log.isLoggable(TAG, 3)) {
                Log.d(TAG, "Resume syncs");
            }
            this.mPauseStartTimeMills = 0;
            this.mPauseEndTimeMills = 0;
            return true;
        }
        long currentTimeMillis = System.currentTimeMillis();
        if (pauseTimeMillis >= currentTimeMillis && pauseTimeMillis - currentTimeMillis <= 86400000) {
            this.mPauseStartTimeMills = currentTimeMillis;
            this.mPauseEndTimeMills = pauseTimeMillis;
            return true;
        } else if (!Log.isLoggable(TAG, 3)) {
            return false;
        } else {
            Log.d(TAG, "Illegal time");
            return false;
        }
    }

    public void writeToXML(XmlSerializer out) throws IOException {
        out.startTag((String) null, XML_TAG_ITEM);
        out.attribute((String) null, XML_ATTR_VERSION, Integer.toString(1));
        out.attribute((String) null, "uid", Integer.toString(this.mUid));
        out.attribute((String) null, XML_ATTR_ACCOUNT_NAME, this.mAccountName);
        out.attribute((String) null, XML_ATTR_PAUSE_END_TIME, Long.toString(this.mPauseEndTimeMills));
        out.endTag((String) null, XML_TAG_ITEM);
    }

    public static MiSyncPause readFromXML(XmlPullParser parser) {
        if (!XML_TAG_ITEM.equals(parser.getName())) {
            return null;
        }
        String itemVersionString = parser.getAttributeValue((String) null, XML_ATTR_VERSION);
        if (TextUtils.isEmpty(itemVersionString)) {
            Slog.e(TAG, "the version in mi pause is null");
            return null;
        }
        try {
            if (Integer.parseInt(itemVersionString) < 1) {
                return null;
            }
            String uidString = parser.getAttributeValue((String) null, "uid");
            String accountName = parser.getAttributeValue((String) null, XML_ATTR_ACCOUNT_NAME);
            String pauseEndTimeMillsString = parser.getAttributeValue((String) null, XML_ATTR_PAUSE_END_TIME);
            if (TextUtils.isEmpty(uidString) || TextUtils.isEmpty(accountName) || TextUtils.isEmpty(pauseEndTimeMillsString)) {
                Slog.e(TAG, "the item in mi pause is null");
                return null;
            }
            try {
                int uid = Integer.parseInt(uidString);
                long pauseEndTimeMills = Long.parseLong(pauseEndTimeMillsString);
                MiSyncPause miSyncPause = new MiSyncPause(uid, accountName);
                miSyncPause.setPauseToTime(pauseEndTimeMills);
                return miSyncPause;
            } catch (NumberFormatException e) {
                Slog.e(TAG, "error parsing item for mi pause", e);
                return null;
            }
        } catch (NumberFormatException e2) {
            Slog.e(TAG, "error parsing version for mi pause", e2);
            return null;
        }
    }
}
