package b.b.c.j;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.os.UserHandle;
import android.telecom.TelecomManager;
import android.util.Log;
import b.b.o.g.d;
import com.miui.maml.folme.AnimatedTarget;
import com.miui.networkassistant.config.Constants;
import com.miui.securityscan.model.manualitem.DefaultAppModel;
import java.lang.reflect.Method;
import java.util.List;

public class h {
    private static Intent a() {
        Intent intent = new Intent("android.intent.action.DIAL");
        intent.setData(Uri.fromParts("tel", "", (String) null));
        return intent;
    }

    private static String a(Context context) {
        ResolveInfo resolveActivity = context.getPackageManager().resolveActivity(a(), 0);
        if (resolveActivity != null) {
            return resolveActivity.activityInfo.packageName;
        }
        return null;
    }

    public static void a(Context context, String str) {
        if (Build.VERSION.SDK_INT > 25) {
            Log.i("DefaultAppUtil", "v26 setDefaultDialerApplication " + str);
            try {
                d.a("DefaultAppUtil", Class.forName("android.telecom.DefaultDialerManager"), "setDefaultDialerApplication", (Class<?>[]) new Class[]{Context.class, String.class}, context, str);
            } catch (Exception e) {
                Log.e("DefaultAppUtil", "v26 setDefaultDialerApplication error", e);
            }
        } else {
            String a2 = a(context);
            if (a2 != null) {
                context.getPackageManager().clearPackagePreferredActivities(a2);
            }
            try {
                TelecomManager telecomManager = (TelecomManager) d.a("DefaultAppUtil", Class.forName("android.telecom.TelecomManager"), TelecomManager.class, AnimatedTarget.STATE_TAG_FROM, (Class<?>[]) new Class[]{Context.class}, context);
                Method declaredMethod = telecomManager.getClass().getDeclaredMethod("setDefaultDialer", new Class[]{String.class});
                if (declaredMethod != null) {
                    declaredMethod.invoke(telecomManager, new Object[]{str});
                }
            } catch (Exception e2) {
                Log.e("setDefaultDialerApplication", "error :", e2);
            }
            List<ResolveInfo> queryIntentActivities = context.getPackageManager().queryIntentActivities(a(), 0);
            if (queryIntentActivities != null) {
                ComponentName[] componentNameArr = new ComponentName[queryIntentActivities.size()];
                ComponentName componentName = null;
                int i = Integer.MIN_VALUE;
                int i2 = 0;
                for (int i3 = 0; i3 < queryIntentActivities.size(); i3++) {
                    ResolveInfo resolveInfo = queryIntentActivities.get(i3);
                    ComponentName componentName2 = new ComponentName(resolveInfo.activityInfo.packageName, resolveInfo.activityInfo.name);
                    if (resolveInfo.activityInfo.packageName.equals(str) && (componentName == null || i < resolveInfo.priority)) {
                        i = resolveInfo.priority;
                        componentName = componentName2;
                    }
                    int i4 = resolveInfo.match;
                    if (i4 > i2) {
                        i2 = i4;
                    }
                    componentNameArr[i3] = componentName2;
                }
                if (componentName != null) {
                    IntentFilter intentFilter = new IntentFilter();
                    intentFilter.addAction("android.intent.action.DIAL");
                    intentFilter.addAction("android.intent.action.VIEW");
                    intentFilter.addCategory(Constants.System.CATEGORY_DEFALUT);
                    intentFilter.addDataScheme("tel");
                    context.getPackageManager().addPreferredActivity(intentFilter, i2, componentNameArr, componentName);
                }
            }
        }
    }

    public static boolean a(Context context, IntentFilter intentFilter, String str) {
        if (miui.os.Build.IS_INTERNATIONAL_BUILD && !x.h(context, "com.android.browser") && !x.h(context, "com.mi.globalbrowser")) {
            return true;
        }
        if (Build.VERSION.SDK_INT <= 25) {
            return DefaultAppModel.isDefaultMiuiApp(context, intentFilter, str);
        }
        try {
            String str2 = (String) d.a("DefaultAppUtil", (Object) context.getPackageManager(), String.class, "getDefaultBrowserPackageNameAsUser", (Class<?>[]) new Class[]{Integer.TYPE}, Integer.valueOf(UserHandle.myUserId()));
            Log.i("DefaultAppUtil", "v26 isDefaultBrowserApplication is " + str2);
            return str != null && str.equals(str2);
        } catch (Exception e) {
            Log.e("DefaultAppUtil", "v26 isDefaultBrowserApplication error", e);
            return true;
        }
    }

    public static boolean b(Context context, IntentFilter intentFilter, String str) {
        if (Build.VERSION.SDK_INT <= 25) {
            return DefaultAppModel.isDefaultMiuiApp(context, intentFilter, str);
        }
        try {
            String str2 = (String) d.a("DefaultAppUtil", Class.forName("android.telecom.DefaultDialerManager"), String.class, "getDefaultDialerApplication", (Class<?>[]) new Class[]{Context.class}, context);
            Log.i("DefaultAppUtil", "v26 defaultDialerApplication is " + str2);
            return str != null && str.equals(str2);
        } catch (Exception e) {
            Log.e("DefaultAppUtil", "v26 defaultDialerApplication error", e);
            return true;
        }
    }
}
