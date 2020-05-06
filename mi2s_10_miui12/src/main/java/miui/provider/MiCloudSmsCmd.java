package miui.provider;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SystemIntent;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.os.UserHandle;
import android.text.TextUtils;
import android.util.Log;
import miui.content.ExtraIntent;

public class MiCloudSmsCmd {
    private static final String ACTIVATION_SMS_PREFIX = "AC/";
    private static final String ACTIVATION_SMS_PREFIX_2 = "ACT/";
    private static final int CMD_INDEX = 1;
    private static final int HEADER_INDEX = 0;
    private static final int MSG_ID_INDEX = 2;
    private static final String SEPARATOR = ",";
    private static final String SMS_CMD_HEADER = "mfc";
    private static final String SMS_CMD_HEADER_V1 = "mf";
    private static final String SMS_CMD_TAIL = "##";
    private static final String SMS_CMD_TAIL_V1 = "#";
    private static final String TAG = "MiCloudSmsCmd";
    private static final int TIME_INDEX = 3;
    public static final String TYPE_DISCARD_TOKEN = "d";
    public static final String TYPE_LOCATION = "l";
    public static final String TYPE_LOCK = "k";
    public static final String TYPE_NOISE = "n";
    public static final String TYPE_WIPE = "w";
    private static boolean[] sHexChars = new boolean[123];

    private MiCloudSmsCmd() {
    }

    public static boolean checkSmsCmd(Intent oriIntent, Context context, String address, String body, int slotId, int subId) {
        if (TextUtils.isEmpty(body)) {
            return false;
        }
        if (body.indexOf(SMS_CMD_HEADER) < 0 && body.indexOf(SMS_CMD_HEADER_V1) < 0) {
            return false;
        }
        if (body.indexOf(SMS_CMD_TAIL) < 0 && body.indexOf(SMS_CMD_TAIL_V1) < 0) {
            return false;
        }
        transferToFindDevice(oriIntent, context, address, body, slotId, subId);
        return true;
    }

    public static boolean checkSmsCmd(Context context, String address, String body, int slotId, int subId) {
        return checkSmsCmd((Intent) null, context, address, body, slotId, subId);
    }

    public static boolean checkSmsCmd(Context context, String address, String body, int slotId) {
        return checkSmsCmd(context, address, body, slotId, -1);
    }

    public static boolean checkSmsCmd(Context context, String address, String body) {
        return checkSmsCmd(context, address, body, -1, -1);
    }

    private static void transferToFindDevice(Intent oriIntent, Context context, String address, String body, int slotId, int subId) {
        Intent intent = oriIntent;
        final String str = address;
        final String str2 = body;
        final PowerManager.WakeLock wakeLock = ((PowerManager) context.getSystemService("power")).newWakeLock(1, MiCloudSmsCmd.class.getName());
        wakeLock.acquire();
        Intent intent2 = new Intent(ExtraIntent.INTENT_FIND_DEVICE_V2_CMD_RECEIVER);
        intent2.putExtra(ExtraIntent.EXTRA_FIND_DEVICE_V2_COMMAND_TYPE, "sms");
        intent2.putExtra(ExtraIntent.EXTRA_FROM_ADDRESS, str);
        intent2.putExtra(ExtraIntent.EXTRA_FROM_SLOT_ID, slotId);
        intent2.putExtra(ExtraIntent.EXTRA_FROM_SUB_ID, subId);
        intent2.putExtra(ExtraIntent.EXTRA_FIND_DEVICE_V2_COMMAND, str2);
        if (intent != null) {
            intent2.putExtra(ExtraIntent.EXTRA_FIND_DEVICE_INTERCEPT_SMS_INTENT, intent);
        }
        context.sendOrderedBroadcast(intent2, ExtraIntent.FIND_DEVICE_V2_RECEIVE_COMMAND_PERMISSION, new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                try {
                    if (getResultCode() == 0) {
                        MiCloudSmsCmd.transferToOldVersionFindDevice(context, str, str2);
                    }
                } finally {
                    wakeLock.release();
                }
            }
        }, (Handler) null, 0, (String) null, (Bundle) null);
    }

    /* access modifiers changed from: private */
    public static void transferToOldVersionFindDevice(Context context, String address, String body) {
        String pwd;
        String digest;
        String str = body;
        String[] params = str.split(SEPARATOR);
        if (params == null || params.length < 5 || params[0].indexOf(SMS_CMD_HEADER) < 0) {
            Log.e(TAG, "Bad find device command: " + str + ". ");
            return;
        }
        String cmd = params[1];
        String msgId = params[2];
        String time = params[3];
        if (TYPE_LOCK.equals(cmd)) {
            String pwd2 = params[4];
            digest = params[5];
            pwd = pwd2;
        } else {
            digest = params[4];
            pwd = null;
        }
        sendOldVersionCommandToFindDeviceService(context, msgId, time, cmd, pwd, digest.substring(0, digest.lastIndexOf(SMS_CMD_TAIL)), address);
    }

    private static void sendOldVersionCommandToFindDeviceService(Context context, String msgId, String time, String cmd, String pwd, String digest, String address) {
        Intent intent = new Intent();
        intent.putExtra(ExtraIntent.EXTRA_DEVICE_MSGID, msgId);
        intent.putExtra(ExtraIntent.EXTRA_DEVICE_TIME, time);
        intent.putExtra(ExtraIntent.EXTRA_DEVICE_DIGEST, digest);
        intent.putExtra(ExtraIntent.EXTRA_DEVICE_CMD, cmd);
        intent.putExtra(ExtraIntent.EXTRA_LOCK_DEVICE_PASSWORD, pwd);
        intent.putExtra(ExtraIntent.EXTRA_FROM_ADDRESS, address);
        String action = null;
        if (TYPE_LOCATION.equals(cmd)) {
            action = ExtraIntent.ACTION_REQUEST_LOCATION;
        } else if (TYPE_NOISE.equals(cmd)) {
            action = ExtraIntent.ACTION_NOISE;
        } else if (TYPE_LOCK.equals(cmd)) {
            action = ExtraIntent.ACTION_LOCK_DEVICE;
        } else if (TYPE_WIPE.equals(cmd)) {
            action = ExtraIntent.ACTION_WIPE_DATA;
        } else if (TYPE_DISCARD_TOKEN.equals(cmd)) {
            action = ExtraIntent.ACTION_DISCARD_FIND_DEVICE_TOKEN;
        }
        if (!TextUtils.isEmpty(action)) {
            intent.setAction(action);
            intent.setPackage("com.xiaomi.finddevice");
            if (context.startService(intent) == null) {
                intent.setPackage("com.miui.cloudservice");
                context.startService(intent);
            }
        }
    }

    public static boolean checkAndDispatchActivationSms(Context context, int simIndex, String address, String body) {
        int pos = body.indexOf(ACTIVATION_SMS_PREFIX);
        if (pos == -1) {
            return checkAndDispatchActivationSms2(context, simIndex, address, body);
        }
        Log.v(TAG, "checkAndDispatchActivationSms: The message looks like an activation");
        int vkey1Start = ACTIVATION_SMS_PREFIX.length() + pos;
        int colonStart = vkey1Start + 32;
        int msgIdStart = colonStart + 1;
        int msgIdEnd = msgIdStart + 11;
        if (body.length() < msgIdEnd) {
            Log.v(TAG, "checkAndDispatchActivationSms: length check failed, " + body.length() + " < " + msgIdEnd);
            return false;
        } else if (!isStrHex(body, vkey1Start, colonStart)) {
            Log.v(TAG, "checkAndDispatchActivationSms: left hex check failed");
            return false;
        } else if (body.charAt(colonStart) != ':') {
            Log.v(TAG, "checkAndDispatchActivationSms: colon check failed");
            return false;
        } else if (!isStrHex(body, msgIdStart, msgIdEnd)) {
            Log.v(TAG, "checkAndDispatchActivationSms: right hex check failed");
            return false;
        } else {
            Log.v(TAG, "checkAndDispatchActivationSms: activation SMS acknowledged, broadcasting...");
            Intent intent = new Intent("com.xiaomi.action.ACTIVATION_SMS_RECEIVED");
            intent.putExtra("extra_sim_index", simIndex);
            intent.putExtra("extra_address", address);
            intent.putExtra("extra_msg_id", body.substring(msgIdStart, msgIdEnd));
            intent.putExtra("extra_vkey1", body.substring(vkey1Start, colonStart));
            intent.setPackage(SystemIntent.ACTIVATE_SERVICE_HOST_PACKAGE_NEW);
            context.sendBroadcastAsUser(intent, getAllUser());
            return true;
        }
    }

    private static boolean checkAndDispatchActivationSms2(Context context, int simIndex, String address, String body) {
        int pos = body.indexOf(ACTIVATION_SMS_PREFIX_2);
        if (pos == -1) {
            return false;
        }
        Log.v(TAG, "checkAndDispatchActivationSmsi2: The message looks like an activation");
        int keyStart = ACTIVATION_SMS_PREFIX_2.length() + pos;
        int colonStart = keyStart + 32;
        int msgIdStart = colonStart + 1;
        int msgIdEnd = msgIdStart + 11;
        if (body.length() < msgIdEnd) {
            Log.v(TAG, "checkAndDispatchActivationSms2: length check failed, " + body.length() + " < " + msgIdEnd);
            return false;
        } else if (!isStrHex(body, keyStart, colonStart)) {
            Log.v(TAG, "checkAndDispatchActivationSms2: left hex check failed");
            return false;
        } else if (body.charAt(colonStart) != ':') {
            Log.v(TAG, "checkAndDispatchActivationSms2: colon check failed");
            return false;
        } else if (!isStrHex(body, msgIdStart, msgIdEnd)) {
            Log.v(TAG, "checkAndDispatchActivationSms2: right hex check failed");
            return false;
        } else {
            Log.v(TAG, "checkAndDispatchActivationSms2: activation SMS acknowledged, broadcasting...");
            Intent intent = new Intent("com.xiaomi.action.ACTIVATION_SMS_2_RECEIVED");
            intent.putExtra("extra_sim_index", simIndex);
            intent.putExtra("extra_address", address);
            intent.putExtra("extra_sms", body);
            intent.setPackage(SystemIntent.ACTIVATE_SERVICE_HOST_PACKAGE_NEW);
            context.sendBroadcastAsUser(intent, getAllUser());
            return true;
        }
    }

    private static UserHandle getAllUser() {
        try {
            return (UserHandle) UserHandle.class.getField("ALL").get((Object) null);
        } catch (NoSuchFieldException e) {
            throw new IllegalStateException("will not reach here", e);
        } catch (IllegalAccessException e2) {
            throw new IllegalStateException("will not reach here", e2);
        }
    }

    static {
        for (char c = 'A'; c <= 'Z'; c = (char) (c + 1)) {
            sHexChars[c] = true;
        }
        for (char c2 = 'a'; c2 <= 'z'; c2 = (char) (c2 + 1)) {
            sHexChars[c2] = true;
        }
        for (char c3 = '0'; c3 <= '9'; c3 = (char) (c3 + 1)) {
            sHexChars[c3] = true;
        }
    }

    private static boolean isStrHex(String s, int begin, int end) {
        for (int i = begin; i < end; i++) {
            char ch = s.charAt(i);
            boolean[] zArr = sHexChars;
            if (ch >= zArr.length || !zArr[ch]) {
                return false;
            }
        }
        return true;
    }
}
