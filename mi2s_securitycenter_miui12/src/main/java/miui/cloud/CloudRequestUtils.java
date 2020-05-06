package miui.cloud;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Looper;
import android.util.Log;
import b.d.b.c.a;
import b.d.b.c.c;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import miui.telephony.CloudTelephonyManager;

public class CloudRequestUtils {
    private static final String TAG = "CloudRequestUtils";
    public static final String URL_CALL_LOG_BASE;
    public static final String URL_CONTACT_BASE;
    public static final String URL_DEV_BASE = (c.f2136a ? "http://api.device.preview.n.xiaomi.net" : "http://api.device.xiaomi.net");
    public static final String URL_DEV_SETTING = "/api/user/device/setting";
    public static final String URL_FIND_DEVICE_BASE;
    public static final String URL_GALLERY_BASE;
    public static final String URL_MICARD_BASE = (c.f2136a ? "http://micardapi.micloud.preview.n.xiaomi.net" : "http://micardapi.micloud.xiaomi.net");
    public static final String URL_MICLOUD_STATUS_BASE = (c.f2136a ? "http://statusapi.micloud.preview.n.xiaomi.net" : "http://statusapi.micloud.xiaomi.net");
    public static final String URL_MMS_BASE;
    public static final String URL_MUSIC_BASE;
    public static final String URL_NOTE_BASE;
    public static final String URL_RICH_MEDIA_BASE = (c.f2136a ? "http://api.micloud.preview.n.xiaomi.net" : "http://fileapi.micloud.xiaomi.net");
    public static final String URL_WIFI_BASE;
    public static final String URL_WIFI_SHARE_BASE;
    private static String sUserAgent;

    private static class ConnectivityResumedReceiver extends BroadcastReceiver {
        private final Context mContext;
        private final AsyncFuture<Boolean> mFuture;

        private static final class AsyncFuture<V> extends FutureTask<V> {
            public AsyncFuture() {
                super(new Callable<V>() {
                    public V call() {
                        throw new IllegalStateException("this should never be called");
                    }
                });
            }

            public void setResult(V v) {
                set(v);
            }
        }

        private ConnectivityResumedReceiver(Context context) {
            this.mFuture = new AsyncFuture<>();
            this.mContext = context;
        }

        public void await() {
            if (CloudRequestUtils.isNetworkConnected(this.mContext)) {
                this.mFuture.setResult(true);
            }
            this.mFuture.get();
        }

        public void onReceive(Context context, Intent intent) {
            if (!intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false)) {
                Log.i(CloudRequestUtils.TAG, "connectivity resumed");
                this.mFuture.setResult(true);
            }
        }
    }

    static {
        String str = "http://micloud.preview.n.xiaomi.net";
        URL_CONTACT_BASE = c.f2136a ? str : "http://contactapi.micloud.xiaomi.net";
        URL_MMS_BASE = c.f2136a ? str : "http://smsapi.micloud.xiaomi.net";
        URL_GALLERY_BASE = c.f2136a ? str : "http://galleryapi.micloud.xiaomi.net";
        URL_FIND_DEVICE_BASE = c.f2136a ? str : "http://findapi.micloud.xiaomi.net";
        URL_WIFI_BASE = c.f2136a ? str : "http://wifiapi.micloud.xiaomi.net";
        URL_NOTE_BASE = c.f2136a ? str : "http://noteapi.micloud.xiaomi.net";
        URL_MUSIC_BASE = c.f2136a ? str : "http://musicapi.micloud.xiaomi.net";
        URL_CALL_LOG_BASE = c.f2136a ? str : "http://phonecallapi.micloud.xiaomi.net";
        if (!c.f2136a) {
            str = "http://wifisharingapi.micloud.xiaomi.net";
        }
        URL_WIFI_SHARE_BASE = str;
    }

    private static String convertObsoleteLanguageCodeToNew(String str) {
        if (str == null) {
            return null;
        }
        return "iw".equals(str) ? "he" : "in".equals(str) ? "id" : "ji".equals(str) ? "yi" : str;
    }

    private static void ensureNotOnMainThread(Context context) {
        Looper myLooper = Looper.myLooper();
        if (myLooper != null && myLooper == context.getMainLooper()) {
            throw new IllegalStateException("calling this from your main thread can lead to deadlock");
        }
    }

    public static String getHashedDeviceId(Context context) {
        return a.a(CloudTelephonyManager.blockingGetDeviceId(context));
    }

    public static String getResourceId(Context context) {
        return getHashedDeviceId(context);
    }

    /* JADX WARNING: Removed duplicated region for block: B:18:0x007a  */
    /* JADX WARNING: Removed duplicated region for block: B:21:0x0091  */
    /* JADX WARNING: Removed duplicated region for block: B:25:0x00a5  */
    /* JADX WARNING: Removed duplicated region for block: B:26:0x00ad  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static java.lang.String getUserAgent() {
        /*
            java.lang.String r0 = sUserAgent
            if (r0 != 0) goto L_0x00b6
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r2 = android.os.Build.MODEL
            r1.append(r2)
            java.lang.String r2 = "/"
            r1.append(r2)
            java.lang.String r1 = r1.toString()
            r0.append(r1)
            java.lang.String r1 = "ro.product.mod_device"
            java.lang.String r1 = miui.cloud.os.SystemProperties.get(r1)
            boolean r2 = android.text.TextUtils.isEmpty(r1)
            if (r2 != 0) goto L_0x002c
            goto L_0x002e
        L_0x002c:
            java.lang.String r1 = android.os.Build.MODEL
        L_0x002e:
            r0.append(r1)
            java.lang.String r1 = "; MIUI/"
            r0.append(r1)
            java.lang.String r1 = android.os.Build.VERSION.INCREMENTAL
            r0.append(r1)
            java.lang.String r1 = " E/"
            r0.append(r1)
            java.lang.String r1 = "ro.miui.ui.version.name"
            java.lang.String r1 = miui.cloud.os.SystemProperties.get(r1)
            r0.append(r1)
            java.lang.String r1 = " B/"
            r0.append(r1)
            boolean r1 = miui.os.Build.IS_ALPHA_BUILD
            java.lang.String r2 = "null"
            if (r1 == 0) goto L_0x005a
            java.lang.String r1 = "A"
        L_0x0056:
            r0.append(r1)
            goto L_0x006b
        L_0x005a:
            boolean r1 = miui.os.Build.IS_DEVELOPMENT_VERSION
            if (r1 == 0) goto L_0x0061
            java.lang.String r1 = "D"
            goto L_0x0056
        L_0x0061:
            boolean r1 = miui.os.Build.IS_STABLE_VERSION
            if (r1 == 0) goto L_0x0068
            java.lang.String r1 = "S"
            goto L_0x0056
        L_0x0068:
            r0.append(r2)
        L_0x006b:
            java.lang.String r1 = " L/"
            r0.append(r1)
            java.util.Locale r1 = java.util.Locale.getDefault()
            java.lang.String r3 = r1.getLanguage()
            if (r3 == 0) goto L_0x0091
            java.lang.String r3 = convertObsoleteLanguageCodeToNew(r3)
            r0.append(r3)
            java.lang.String r1 = r1.getCountry()
            if (r1 == 0) goto L_0x0096
            java.lang.String r3 = "-"
            r0.append(r3)
            java.lang.String r1 = r1.toUpperCase()
            goto L_0x0093
        L_0x0091:
            java.lang.String r1 = "EN"
        L_0x0093:
            r0.append(r1)
        L_0x0096:
            java.lang.String r1 = " LO/"
            r0.append(r1)
            java.lang.String r1 = miui.os.Build.getRegion()
            boolean r3 = android.text.TextUtils.isEmpty(r1)
            if (r3 != 0) goto L_0x00ad
            java.lang.String r1 = r1.toUpperCase()
            r0.append(r1)
            goto L_0x00b0
        L_0x00ad:
            r0.append(r2)
        L_0x00b0:
            java.lang.String r0 = r0.toString()
            sUserAgent = r0
        L_0x00b6:
            java.lang.String r0 = sUserAgent
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: miui.cloud.CloudRequestUtils.getUserAgent():java.lang.String");
    }

    public static boolean isNetworkConnected(Context context) {
        NetworkInfo activeNetworkInfo = ((ConnectivityManager) context.getSystemService("connectivity")).getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static void waitUntilNetworkConnected(Context context) {
        ensureNotOnMainThread(context);
        IntentFilter intentFilter = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
        ConnectivityResumedReceiver connectivityResumedReceiver = new ConnectivityResumedReceiver(context);
        context.registerReceiver(connectivityResumedReceiver, intentFilter);
        try {
            connectivityResumedReceiver.await();
        } catch (ExecutionException unused) {
        } catch (Throwable th) {
            context.unregisterReceiver(connectivityResumedReceiver);
            throw th;
        }
        context.unregisterReceiver(connectivityResumedReceiver);
    }
}
