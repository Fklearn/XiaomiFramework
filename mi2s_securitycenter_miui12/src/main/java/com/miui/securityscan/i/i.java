package com.miui.securityscan.i;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.util.Log;
import com.miui.gamebooster.ui.GameVideoPlayActivity;
import com.miui.networkassistant.config.Constants;
import com.miui.networkassistant.provider.ProviderConstant;
import com.miui.networkassistant.ui.activity.NetworkDiagnosticsTipActivity;
import com.miui.warningcenter.mijia.MijiaAlertModel;
import java.io.File;
import java.util.List;

public class i {
    public static Uri a(Context context, File file) {
        try {
            return Build.VERSION.SDK_INT > 23 ? FileProvider.getUriForFile(context, ProviderConstant.AUTHORITY_FILE, file) : Uri.fromFile(file);
        } catch (Exception unused) {
            return null;
        }
    }

    public static void a(Context context, File file, String str, String str2, String str3) {
        if (file.exists()) {
            Intent intent = new Intent("android.intent.action.VIEW");
            if (Build.VERSION.SDK_INT > 23) {
                intent.setFlags(1);
            }
            if (Build.VERSION.SDK_INT > 25) {
                intent.addFlags(Integer.MIN_VALUE);
            }
            Uri a2 = a(context, file);
            if (TextUtils.isEmpty(str)) {
                str = "file/*";
            }
            intent.setDataAndType(a2, str);
            if (!TextUtils.isEmpty(str2) && !TextUtils.isEmpty(str3)) {
                intent.setClassName(str2, str3);
            }
            context.startActivity(intent);
        }
    }

    public static void a(Context context, String str) {
        try {
            Intent launchIntentForPackage = context.getPackageManager().getLaunchIntentForPackage(str);
            if (launchIntentForPackage != null) {
                context.startActivity(launchIntentForPackage);
            }
        } catch (Exception e) {
            Log.e("IntentUtil", " startActivity error ", e);
        }
    }

    public static void a(Context context, String str, String str2, boolean z) {
        if (!TextUtils.isEmpty(str)) {
            if (z) {
                a(context, new File(str), str2, context.getPackageName(), GameVideoPlayActivity.class.getName());
            } else {
                a(context, new File(str), str2, (String) null, (String) null);
            }
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:3:0x0004, code lost:
        r2 = r2.getPackageManager().queryIntentActivities(r3, 1);
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static boolean a(android.content.Context r2, android.content.Intent r3) {
        /*
            r0 = 0
            if (r3 != 0) goto L_0x0004
            return r0
        L_0x0004:
            android.content.pm.PackageManager r2 = r2.getPackageManager()
            r1 = 1
            java.util.List r2 = r2.queryIntentActivities(r3, r1)
            if (r2 == 0) goto L_0x0016
            boolean r2 = r2.isEmpty()
            if (r2 != 0) goto L_0x0016
            return r1
        L_0x0016:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.securityscan.i.i.a(android.content.Context, android.content.Intent):boolean");
    }

    public static boolean a(Context context, String str, String str2) {
        try {
            PackageManager packageManager = context.getPackageManager();
            Intent intent = new Intent("android.intent.action.VIEW");
            intent.addCategory(Constants.System.CATEGORY_DEFALUT);
            intent.addCategory("android.intent.category.BROWSABLE");
            intent.setData(Uri.parse(str));
            intent.setPackage(str2);
            List<ResolveInfo> queryIntentActivities = packageManager.queryIntentActivities(intent, 32);
            return queryIntentActivities != null && !queryIntentActivities.isEmpty();
        } catch (Exception e) {
            Log.e("IntentUtil", "isSupportOpen : ", e);
            return false;
        }
    }

    public static void b(Context context, String str, String str2) {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.setData(Uri.parse(str));
        if (!TextUtils.isEmpty(str2)) {
            intent.setPackage(str2);
        }
        context.startActivity(intent);
    }

    public static boolean b(Context context, String str) {
        if (TextUtils.isEmpty(str)) {
            return false;
        }
        if (str.startsWith("#Intent") && str.endsWith(TtmlNode.END)) {
            try {
                Intent parseUri = Intent.parseUri(str, 0);
                if (a(context, parseUri)) {
                    context.startActivity(parseUri);
                    return true;
                }
            } catch (Exception e) {
                Log.e("IntentUtil", "intent parseUri error : ", e);
            }
            return false;
        } else if (str.startsWith("http")) {
            try {
                if (miui.os.Build.IS_INTERNATIONAL_BUILD && a(context, str, "com.mi.globalbrowser")) {
                    b(context, str, "com.mi.globalbrowser");
                } else if (a(context, str, "com.android.browser")) {
                    b(context, str, "com.android.browser");
                } else {
                    c(context, str);
                }
                return true;
            } catch (Exception unused) {
            }
        } else {
            c(context, str);
            return true;
        }
    }

    public static void c(Context context, String str) {
        b(context, str, (String) null);
    }

    public static void c(Context context, String str, String str2) {
        Intent intent = new Intent("miui.intent.action.CLEAN_MASTER_SECURITY_WEB_VIEW");
        intent.putExtra(MijiaAlertModel.KEY_URL, str);
        intent.putExtra(NetworkDiagnosticsTipActivity.TITLE_KEY_NAME, str2);
        context.startActivity(intent);
    }
}
