package com.miui.securityadd.input;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import com.xiaomi.stat.MiStat;

public class InputProvider extends ContentProvider {
    /* access modifiers changed from: private */

    /* renamed from: a  reason: collision with root package name */
    public static boolean f7444a = true;
    /* access modifiers changed from: private */

    /* renamed from: b  reason: collision with root package name */
    public static boolean f7445b = true;

    /* renamed from: c  reason: collision with root package name */
    public static int f7446c = g.a();

    /* renamed from: d  reason: collision with root package name */
    private ContentObserver f7447d = new c(this, (Handler) null);
    private ContentObserver e = new d(this, (Handler) null);
    private ContentObserver f = new e(this, (Handler) null);

    private void a(Context context) {
        context.getContentResolver().registerContentObserver(Settings.Global.getUriFor("force_fsg_nav_bar"), false, this.f7447d);
        context.getContentResolver().registerContentObserver(Settings.Secure.getUriFor("full_screen_keyboard_left_function"), false, this.e);
        context.getContentResolver().registerContentObserver(Settings.Secure.getUriFor("full_screen_keyboard_right_function"), false, this.e);
        context.getContentResolver().registerContentObserver(Settings.Secure.getUriFor("enable_quick_paste_cloud"), false, this.f);
    }

    public Bundle call(String str, String str2, Bundle bundle) {
        Context context = getContext();
        if (bundle == null || context == null) {
            Log.e("InputProvider", "extras or context is null");
            bundle = null;
        } else if (TextUtils.equals(str, "saveClipboardString")) {
            return !g.a(str, context, (ContentProvider) this) ? super.call(str, str2, bundle) : g.b(context, bundle);
        } else {
            if (TextUtils.equals(str, "saveClipboardStringNew")) {
                return !g.a(str, context, (ContentProvider) this) ? super.call(str, str2, bundle) : g.d(context, bundle);
            }
            if (TextUtils.equals(str, "saveClipboardCipherText")) {
                return !g.a(str, context, (ContentProvider) this) ? super.call(str, str2, bundle) : g.c(context, bundle);
            }
            if (TextUtils.equals(str, "getClipboardList")) {
                return !g.a(str, context, (ContentProvider) this) ? super.call(str, str2, bundle) : g.b(context);
            }
            if (!TextUtils.equals(str, "clearOldClipboardContent")) {
                if (TextUtils.equals(str, "saveCloudClipboardContent")) {
                    Log.d("InputProvider", "receive content from mi cloud.");
                    if (TextUtils.equals(getCallingPackage(), "com.miui.micloudsync") || g.a(str, context, (ContentProvider) this)) {
                        String string = bundle.getString(MiStat.Param.CONTENT, "");
                        if (!TextUtils.isEmpty(string)) {
                            g.b(context, string, f7444a, f7445b);
                        }
                    }
                    return super.call(str, str2, bundle);
                } else if (TextUtils.equals(str, "setCloudClipboardContent")) {
                    Log.d("InputProvider", "receive content from mi cloud.");
                    if (TextUtils.equals(getCallingPackage(), "com.miui.micloudsync") || g.a(str, context, (ContentProvider) this)) {
                        String string2 = bundle.getString(MiStat.Param.CONTENT, "");
                        if (!TextUtils.isEmpty(string2)) {
                            g.a(context, string2, f7444a, f7445b);
                        }
                    }
                    return super.call(str, str2, bundle);
                } else if (!TextUtils.equals(str, "input_method_analytics")) {
                    return TextUtils.equals(str, "setClipboardTipsNeedShowFlag") ? !g.a(str, context, (ContentProvider) this) ? super.call(str, str2, bundle) : g.a(context, bundle) : TextUtils.equals(str, "getCloudContent") ? !g.a(str, context, (ContentProvider) this) ? super.call(str, str2, bundle) : g.c(context) : super.call(str, str2, bundle);
                } else {
                    g.a(bundle);
                }
                Log.e("InputProvider", "receive content from mi cloud is null.");
            } else if (!g.a(str, context, (ContentProvider) this)) {
                return super.call(str, str2, bundle);
            } else {
                g.a(context);
            }
        }
        return super.call(str, str2, bundle);
    }

    public int delete(Uri uri, String str, String[] strArr) {
        return 0;
    }

    public String getType(Uri uri) {
        return null;
    }

    public Uri insert(Uri uri, ContentValues contentValues) {
        return null;
    }

    public boolean onCreate() {
        Context context = getContext();
        if (context == null) {
            return false;
        }
        f7444a = g.j(context);
        f7445b = g.f(context).booleanValue();
        a(context);
        return false;
    }

    public Cursor query(Uri uri, String[] strArr, String str, String[] strArr2, String str2) {
        return null;
    }

    public int update(Uri uri, ContentValues contentValues, String str, String[] strArr) {
        return 0;
    }
}
