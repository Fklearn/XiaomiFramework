package com.android.server;

import java.util.HashMap;

class AppOpsService {
    static final HashMap<Integer, String> sOpInControl = new HashMap<>();

    AppOpsService() {
    }

    static {
        sOpInControl.put(20, "android.permission.SEND_SMS");
        sOpInControl.put(15, "android.permission.WRITE_SMS");
        sOpInControl.put(22, "android.permission.WRITE_SMS");
        sOpInControl.put(10010, "android.permission.WRITE_SMS");
        sOpInControl.put(14, "android.permission.READ_SMS");
        sOpInControl.put(16, "android.permission.RECEIVE_SMS");
        sOpInControl.put(17, "android.permission.RECEIVE_SMS");
        sOpInControl.put(21, "android.permission.READ_SMS");
        sOpInControl.put(10004, "android.permission.INTERNET");
        sOpInControl.put(10011, "android.permission.WRITE_SMS");
        sOpInControl.put(10006, "android.permission.WRITE_SMS");
        sOpInControl.put(18, "android.permission.RECEIVE_MMS");
        sOpInControl.put(19, "android.permission.RECEIVE_MMS");
        sOpInControl.put(13, "android.permission.CALL_PHONE");
        sOpInControl.put(54, "android.permission.PROCESS_OUTGOING_CALLS");
        sOpInControl.put(5, "android.permission.WRITE_CONTACTS");
        sOpInControl.put(4, "android.permission.READ_CONTACTS");
        sOpInControl.put(7, "android.permission.WRITE_CALL_LOG");
        sOpInControl.put(10013, "android.permission.WRITE_CALL_LOG");
        sOpInControl.put(6, "android.permission.READ_CALL_LOG");
        sOpInControl.put(0, "android.permission.ACCESS_COARSE_LOCATION");
        sOpInControl.put(1, "android.permission.ACCESS_FINE_LOCATION");
        sOpInControl.put(2, "android.permission.ACCESS_FINE_LOCATION");
        sOpInControl.put(10, "android.permission.ACCESS_COARSE_LOCATION");
        sOpInControl.put(12, "android.permission.ACCESS_COARSE_LOCATION");
        sOpInControl.put(41, "android.permission.ACCESS_FINE_LOCATION");
        sOpInControl.put(42, "android.permission.ACCESS_FINE_LOCATION");
        sOpInControl.put(51, "android.permission.READ_PHONE_STATE");
        sOpInControl.put(8, "android.permission.READ_CALENDAR");
        sOpInControl.put(9, "android.permission.WRITE_CALENDAR");
        sOpInControl.put(10015, "ACCESS_XIAOMI_ACCOUNT");
        sOpInControl.put(62, "android.permission.GET_ACCOUNTS");
        sOpInControl.put(52, "com.android.voicemail.permission.ADD_VOICEMAIL");
        sOpInControl.put(53, "android.permission.USE_SIP");
        sOpInControl.put(26, "android.permission.CAMERA");
        sOpInControl.put(27, "android.permission.RECORD_AUDIO");
        sOpInControl.put(59, "android.permission.READ_EXTERNAL_STORAGE");
        sOpInControl.put(60, "android.permission.WRITE_EXTERNAL_STORAGE");
        sOpInControl.put(23, "android.permission.WRITE_SETTINGS");
        sOpInControl.put(10003, "android.permission.CHANGE_NETWORK_STATE");
        sOpInControl.put(10001, "android.permission.CHANGE_WIFI_STATE");
        sOpInControl.put(10002, "android.permission.BLUETOOTH_ADMIN");
        sOpInControl.put(10016, "android.permission.NFC");
        sOpInControl.put(10017, "com.android.launcher.permission.INSTALL_SHORTCUT");
        sOpInControl.put(24, "android.permission.SYSTEM_ALERT_WINDOW");
        sOpInControl.put(56, "android.permission.BODY_SENSORS");
        sOpInControl.put(10008, "AUTO_START");
    }
}
