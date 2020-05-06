package com.android.server.fingerprint;

import android.util.Slog;
import com.android.server.ScreenOnMonitor;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import miui.mqsas.sdk.MQSEventManagerDelegate;
import miui.util.FeatureParser;

public class FingerprintServiceInjector {
    private static boolean IS_RECORD_PERFORM = false;
    private static boolean IS_RECORD_SENSORINFO = false;
    private static final int RECORD_FAILED = 2;
    private static final int RECORD_PERFORM = 1;
    private static final String TAG = "FingerprintService";
    private static int sRecordFeature = -1;
    private static SimpleDateFormat sSdfTime = new SimpleDateFormat("yyyyMMdd-HH:mm:ss");
    private static StringBuffer sStrBuf = new StringBuffer();

    public static void reportFingerEvent(String packName, int authen) {
        initRecordFeature();
        StringBuffer sb = new StringBuffer();
        sb.append(packName);
        sb.append(",");
        sb.append(authen);
        if (IS_RECORD_SENSORINFO && sStrBuf.length() > 1) {
            sb.append(sStrBuf);
            initAcquiredInfo();
        }
        sb.append(",");
        sb.append(sSdfTime.format(Calendar.getInstance().getTime()));
        Slog.d(TAG, "info:" + sb.toString());
        MQSEventManagerDelegate.getInstance().reportSimpleEvent(16, sb.toString());
        if (IS_RECORD_PERFORM && authen == 1) {
            ScreenOnMonitor.getInstance().recordTime(5);
        }
    }

    public static void initAcquiredInfo() {
        if (IS_RECORD_SENSORINFO) {
            StringBuffer stringBuffer = sStrBuf;
            stringBuffer.delete(0, stringBuffer.length());
            sStrBuf.append("##");
        }
    }

    public static void recordAcquiredInfo(int acquiredInfo, int vendorCode) {
        if (IS_RECORD_SENSORINFO && isSensorInfo(acquiredInfo, vendorCode)) {
            StringBuffer stringBuffer = sStrBuf;
            stringBuffer.append("(");
            stringBuffer.append(acquiredInfo);
            stringBuffer.append("-");
            StringBuffer stringBuffer2 = sStrBuf;
            stringBuffer2.append(vendorCode);
            stringBuffer2.append(")");
        }
        if (IS_RECORD_PERFORM && acquiredInfo == 6 && vendorCode == 22) {
            Slog.d(TAG, "finger down, " + acquiredInfo + " " + vendorCode);
            ScreenOnMonitor.getInstance().recordTime(4);
        }
    }

    private static boolean isSensorInfo(int acquiredInfo, int vendorCode) {
        if (acquiredInfo == 0) {
            return false;
        }
        if (acquiredInfo != 6) {
            return true;
        }
        if (vendorCode == 21 || vendorCode == 22 || vendorCode == 23) {
            return false;
        }
        return true;
    }

    private static void initRecordFeature() {
        if (sRecordFeature == -1) {
            boolean z = false;
            sRecordFeature = FeatureParser.getInteger("type_mqs_finger_record", 0);
            IS_RECORD_PERFORM = (sRecordFeature & 1) == 1;
            if ((sRecordFeature & 2) == 2) {
                z = true;
            }
            IS_RECORD_SENSORINFO = z;
            Slog.d(TAG, "feature:" + sRecordFeature + " " + IS_RECORD_PERFORM + " " + IS_RECORD_SENSORINFO);
        }
    }
}
