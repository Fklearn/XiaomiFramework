package b.b.q;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Build;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import b.b.o.g.c;
import com.miui.networkassistant.config.Constants;
import java.util.ArrayList;
import java.util.List;

public class a extends BroadcastReceiver {
    public static String a() {
        try {
            c.a a2 = c.a.a("miui.process.ProcessManager");
            a2.b("getForegroundInfo", (Class<?>[]) null, new Object[0]);
            a2.e();
            a2.b("mForegroundPackageName");
            return a2.f();
        } catch (Exception e) {
            Log.e("home_receiver", "getForegroundPackageName exception: ", e);
            return null;
        }
    }

    public static List<String> a(Context context) {
        ArrayList arrayList = new ArrayList();
        PackageManager packageManager = context.getPackageManager();
        Intent intent = new Intent("android.intent.action.MAIN");
        intent.addCategory("android.intent.category.HOME");
        for (ResolveInfo next : packageManager.queryIntentActivities(intent, 0)) {
            if (!"com.android.settings".equals(next.activityInfo.packageName)) {
                arrayList.add(next.activityInfo.packageName);
            }
        }
        return arrayList;
    }

    public static boolean b(Context context) {
        String a2 = a();
        List<String> a3 = a(context);
        if (a3 == null || a3.isEmpty()) {
            return false;
        }
        return a3.contains(a2);
    }

    private boolean c(Context context) {
        String str = Build.VERSION.INCREMENTAL;
        ContentResolver contentResolver = context.getContentResolver();
        String string = Settings.Global.getString(contentResolver, "miui_new_version");
        if (TextUtils.isEmpty(string) || string.equals(str)) {
            return false;
        }
        return Settings.Global.getInt(contentResolver, "miui_update_ready", 0) == 1;
    }

    public void onReceive(Context context, Intent intent) {
        Log.d("home_receiver", "receive broadcast");
        if (Constants.System.ACTION_USER_PRESENT.equals(intent.getAction()) && c(context) && b(context)) {
            Intent intent2 = new Intent();
            intent2.setClassName("com.android.updater", "com.android.updater.UpdateService");
            intent2.putExtra("extra_command", 23);
            context.startService(intent2);
        }
    }
}
