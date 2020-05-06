package com.android.server.location;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.os.SystemProperties;
import android.server.am.SplitScreenReporter;
import android.util.Log;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import miui.mqsas.sdk.MQSEventManagerDelegate;
import miui.os.Build;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public final class GnssCollectData {
    private static final boolean DEBUG = false;
    private static final boolean IS_STABLE_VERSION = Build.IS_STABLE_VERSION;
    public static final int STATE_FIX = 2;
    public static final int STATE_INIT = 0;
    public static final int STATE_LOSE = 4;
    public static final int STATE_SAVE = 5;
    public static final int STATE_START = 1;
    public static final int STATE_STOP = 3;
    public static final int STATE_UNKNOWN = 100;
    private static final String TAG = "GnssCD";
    private static String mCollectdataPath = "/data/mqsas/gps/gps-strength";
    public static int mCurrentState = 100;
    private static Handler mHandler;
    private static HandlerThread mHandlerThread;
    private static MQSEventManagerDelegate mMqsEventManagerDelegate = MQSEventManagerDelegate.getInstance();
    private static String mMqsGpsModuleId = "mqs_gps_data_63921000";
    private static GnssSessionInfo mSessionInfo = new GnssSessionInfo();
    private static JSONArray mjsonArray = new JSONArray();

    private GnssCollectData() {
    }

    private static boolean allowCollect() {
        return !IS_STABLE_VERSION && SystemProperties.get("persist.sys.mqs.gps", SplitScreenReporter.ACTION_ENTER_SPLIT).equals(SplitScreenReporter.ACTION_ENTER_SPLIT);
    }

    private static String packToJsonArray() {
        JSONObject jsonObj = new JSONObject();
        try {
            jsonObj.put("startTime", mSessionInfo.getStartTimeInHour());
            jsonObj.put("TTFF", mSessionInfo.getTtff());
            jsonObj.put("runTime", mSessionInfo.getRunTime());
            jsonObj.put("loseTimes", mSessionInfo.getLoseTimes());
            mjsonArray.put(jsonObj);
            return mjsonArray.toString();
        } catch (JSONException e) {
            Log.e(TAG, "JSON exception " + e);
            return null;
        }
    }

    private static void saveToFile(String messageToFile) {
        FileOutputStream out = null;
        try {
            File bigdataFile = new File(mCollectdataPath);
            if (!bigdataFile.exists()) {
                bigdataFile.getParentFile().mkdirs();
                bigdataFile.createNewFile();
            } else if (bigdataFile.length() / 1024 > 5) {
                bigdataFile.delete();
                bigdataFile.getParentFile().mkdirs();
                bigdataFile.createNewFile();
            }
            FileOutputStream out2 = new FileOutputStream(bigdataFile, true);
            out2.write(messageToFile.getBytes());
            try {
                out2.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (Exception e2) {
            e2.printStackTrace();
            if (out != null) {
                out.close();
            }
        } catch (Throwable th) {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e3) {
                    e3.printStackTrace();
                }
            }
            throw th;
        }
    }

    /* access modifiers changed from: private */
    public static void saveLog() {
        String output = packToJsonArray();
        if (mSessionInfo.checkValidity() && output != null && mjsonArray.length() > 20) {
            List<String> jsons = new ArrayList<>();
            for (int i = 0; i < mjsonArray.length(); i++) {
                jsons.add(mjsonArray.optJSONObject(i).toString());
            }
            mMqsEventManagerDelegate.reportEventsV2("GpsInfo", jsons, mMqsGpsModuleId, true);
            saveToFile(output);
            Log.d(TAG, "send to MQS & file");
            mjsonArray = new JSONArray();
        }
    }

    public static String getCurrentTime() {
        long mNow = System.currentTimeMillis();
        StringBuilder sb = new StringBuilder();
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(mNow);
        sb.append(String.format("%tm-%td %tH:%tM:%tS.%tL", new Object[]{c, c, c, c, c, c}));
        return sb.toString();
    }

    private static void sendMessage(int message, Object obj) {
        Handler handler = mHandler;
        if (handler == null) {
            Log.e(TAG, "mhandler is null  ");
            return;
        }
        mHandler.sendMessage(Message.obtain(handler, message, obj));
    }

    public static void savePoint(String currentpoint, String extraInfo) {
        if (!allowCollect()) {
            Log.d(TAG, "no GnssCD enabled");
        } else if (currentpoint.equals("INIT")) {
            startHandlerThread();
            setCurrentState(0);
        } else if (currentpoint.equals("START")) {
            int i = mCurrentState;
            if (i == 0 || i == 3 || i == 5) {
                mSessionInfo.newSessionReset();
                sendMessage(1, extraInfo);
            }
        } else if (currentpoint.equals("FIX")) {
            if (mCurrentState == 1) {
                sendMessage(2, extraInfo);
            }
        } else if (currentpoint.equals("STOP")) {
            int i2 = mCurrentState;
            if (i2 == 1 || i2 == 2 || i2 == 4) {
                sendMessage(3, extraInfo);
            }
        } else if (currentpoint.equals("LOSE")) {
            int i3 = mCurrentState;
            if (i3 == 2 || i3 == 4) {
                sendMessage(4, extraInfo);
            }
        }
    }

    private static void startHandlerThread() {
        mHandlerThread = new HandlerThread("GnssCD thread");
        mHandlerThread.start();
        mHandler = new Handler(mHandlerThread.getLooper()) {
            public void handleMessage(Message msg) {
                int message = msg.what;
                if (message == 1) {
                    GnssCollectData.saveStartStatus();
                    GnssCollectData.setCurrentState(1);
                } else if (message == 2) {
                    GnssCollectData.saveFixStatus();
                    GnssCollectData.setCurrentState(2);
                } else if (message == 3) {
                    GnssCollectData.saveStopStatus();
                    GnssCollectData.setCurrentState(3);
                    GnssCollectData.saveState();
                } else if (message == 4) {
                    GnssCollectData.saveLoseStatus();
                    GnssCollectData.setCurrentState(4);
                } else if (message == 5) {
                    GnssCollectData.saveLog();
                    GnssCollectData.setCurrentState(5);
                }
            }
        };
    }

    /* access modifiers changed from: private */
    public static void setCurrentState(int s) {
        mCurrentState = s;
    }

    /* access modifiers changed from: private */
    public static void saveStartStatus() {
        mSessionInfo.setStart();
    }

    /* access modifiers changed from: private */
    public static void saveFixStatus() {
        mSessionInfo.setTtffAuto();
    }

    /* access modifiers changed from: private */
    public static void saveLoseStatus() {
        mSessionInfo.setLostTimes();
    }

    /* access modifiers changed from: private */
    public static void saveStopStatus() {
        mSessionInfo.setEnd();
    }

    /* access modifiers changed from: private */
    public static void saveState() {
        sendMessage(5, (Object) null);
    }
}
