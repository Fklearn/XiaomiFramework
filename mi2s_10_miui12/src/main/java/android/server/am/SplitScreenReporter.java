package android.server.am;

import android.app.ActivityManager;
import android.os.Build;
import android.util.Slog;
import miui.mqsas.sdk.MQSEventManagerDelegate;
import org.json.JSONException;
import org.json.JSONObject;

public class SplitScreenReporter {
    public static final String ACTION_ENTER_SPLIT = "1";
    public static final String ACTION_EXIT_SPLIT = "2";
    public static final String STR_ACTION = "action";
    public static final String STR_DEAL_TIME = "time";
    public static final String STR_PKG = "pkg";
    public static final String STR_SDK = "sdk";
    private static final String TAG = "SplitScreenReporter";
    private static long time = 0;

    private static void report(JSONObject jsonObject) {
        if (!checkIsMonkey()) {
            MQSEventManagerDelegate.getInstance().reportEventV2("multiWindow", jsonObject.toString(), "mqs_zjy_android_split_screen_12271000", false);
            Slog.d(TAG, "doInBackground excuted:" + jsonObject.toString() + "reportEventV2");
        }
    }

    public static void reportEnterSplitScreen(String pkg) {
        try {
            JSONObject baseJson = getBaseJson();
            baseJson.put(STR_ACTION, ACTION_ENTER_SPLIT);
            baseJson.put(STR_PKG, pkg);
            report(baseJson);
            time = System.currentTimeMillis();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void reportExitSplitScreen(String pkg) {
        try {
            JSONObject baseJson = getBaseJson();
            baseJson.put(STR_ACTION, ACTION_EXIT_SPLIT);
            baseJson.put(STR_PKG, pkg);
            if (time != 0) {
                baseJson.put(STR_DEAL_TIME, "" + (System.currentTimeMillis() - time));
            }
            report(baseJson);
            time = 0;
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private static JSONObject getBaseJson() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(STR_ACTION, "");
            jsonObject.put(STR_PKG, "");
            jsonObject.put(STR_DEAL_TIME, "");
            jsonObject.put(STR_SDK, Build.VERSION.SDK_INT);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    private static boolean checkIsMonkey() {
        return ActivityManager.isUserAMonkey();
    }
}
