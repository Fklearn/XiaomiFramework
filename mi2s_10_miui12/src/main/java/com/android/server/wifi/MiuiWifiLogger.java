package com.android.server.wifi;

import miui.mqsas.sdk.MQSEventManagerDelegate;

public class MiuiWifiLogger {
    private static int CONNECT_FAILURE_EVENT = 1;
    private static int DISCONNECT_EVENT = 0;
    private static int REPORT_REASON_UNEXPECTED_DISCONNECT = 5;
    private static int REPORT_REASON_USER_ACTION = 7;

    public static void reportDisconnectEvent(int reason) {
        MQSEventManagerDelegate.getInstance().reportConnectExceptionEvent(DISCONNECT_EVENT, reason, (String) null);
    }

    public static void reportConnectFailureEvent(int reason) {
        if (reason != REPORT_REASON_UNEXPECTED_DISCONNECT && reason != REPORT_REASON_USER_ACTION) {
            MQSEventManagerDelegate.getInstance().reportConnectExceptionEvent(CONNECT_FAILURE_EVENT, reason, (String) null);
        }
    }
}
