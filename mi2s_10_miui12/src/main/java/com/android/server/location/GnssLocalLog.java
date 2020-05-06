package com.android.server.location;

import android.util.Log;
import java.io.PrintWriter;
import java.util.Calendar;
import java.util.Iterator;
import java.util.LinkedList;

public class GnssLocalLog {
    private static final String TAG = "GnssLocalLog";
    private String mGlpEn = "=MI GLP EN=";
    private LinkedList<String> mLog = new LinkedList<>();
    private int mMaxLines;
    private String mNmea = "=MI NMEA=";
    private long mNow;

    public GnssLocalLog(int maxLines) {
        this.mMaxLines = maxLines;
    }

    public int setLength(int length) {
        if (length > 0) {
            this.mMaxLines = length;
        }
        return length;
    }

    public void clearData() {
        this.mLog.clear();
    }

    public synchronized void log(String msg) {
        if (this.mMaxLines > 0) {
            this.mNow = System.currentTimeMillis();
            StringBuilder sb = new StringBuilder();
            Calendar c = Calendar.getInstance();
            c.setTimeInMillis(this.mNow);
            sb.append(String.format("%tm-%td %tH:%tM:%tS.%tL", new Object[]{c, c, c, c, c, c}));
            LinkedList<String> linkedList = this.mLog;
            linkedList.add(sb.toString() + " - " + msg);
            while (this.mLog.size() > this.mMaxLines) {
                this.mLog.remove();
            }
        }
    }

    public synchronized void dump(PrintWriter pw) {
        Iterator<String> itr = this.mLog.listIterator(0);
        while (itr.hasNext()) {
            try {
                String log = itr.next();
                if (isPresence(log, this.mGlpEn)) {
                    StringBuilder sb = new StringBuilder();
                    sb.append(this.mGlpEn);
                    sb.append(AESCrypt.encText(getTime(log, this.mGlpEn) + getRawString(log, this.mGlpEn)));
                    pw.println(sb.toString());
                } else if (isPresence(log, this.mNmea)) {
                    StringBuilder sb2 = new StringBuilder();
                    sb2.append(this.mNmea);
                    sb2.append(AESCrypt.encText(getTime(log, this.mNmea) + getRawString(log, this.mNmea)));
                    pw.println(sb2.toString());
                } else {
                    pw.println(log);
                }
            } catch (Exception e) {
                Log.e(TAG, "Error encountered on encrypt the log.", e);
            }
        }
        return;
    }

    private boolean isPresence(String max, String min) {
        return max.contains(min);
    }

    private String getRawString(String rawString, String keyWord) {
        return rawString.substring(rawString.indexOf(keyWord) + keyWord.length());
    }

    private String getTime(String raw, String keyWord) {
        int index = raw.indexOf(keyWord);
        if (index != -1) {
            return raw.substring(0, index);
        }
        Log.e(TAG, "no keyWord " + keyWord + " here\n");
        return "get time error ";
    }
}
