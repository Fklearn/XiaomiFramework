package com.miui.securitycenter.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.text.TextUtils;
import com.google.android.exoplayer2.C;
import com.xiaomi.security.devicecredential.SecurityDeviceCredentialManager;
import com.xiaomi.stat.d;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;
import miui.security.SecurityManager;
import miui.util.Log;

public class DeviceCredentialProvider extends ContentProvider {

    /* renamed from: a  reason: collision with root package name */
    private static final Map<String, String> f7502a = new HashMap();

    /* renamed from: b  reason: collision with root package name */
    private SecurityManager f7503b;

    static {
        f7502a.put("com.xiangkan.android", "E7:C0:85:D3:33:98:17:BD:13:7F:AD:9A:2B:11:AC:96:DD:1D:45:A4:00:F9:48:4B:3C:57:97:80:01:E4:F0:73");
        f7502a.put("com.xiaomi.jr", ApkLoader.PLATFORM_SHA256);
        f7502a.put("com.xiaomi.loan", "D9:92:69:71:E9:B8:49:B6:A6:52:64:CE:AD:4D:26:B9:1D:5F:95:82:08:ED:25:F1:73:7B:BC:17:70:27:8D:FF");
        f7502a.put("com.xiaomi.loanx", "D9:92:69:71:E9:B8:49:B6:A6:52:64:CE:AD:4D:26:B9:1D:5F:95:82:08:ED:25:F1:73:7B:BC:17:70:27:8D:FF");
        f7502a.put("com.wali.live", "B0:31:FE:98:A4:DB:B0:D4:D8:26:61:78:7F:25:DE:64:31:82:B3:78:E9:EF:63:2D:8A:DE:A7:5A:AB:58:F2:D8");
        f7502a.put("com.xiaomi.shop", ApkLoader.PLATFORM_SHA256);
        f7502a.put("com.xiaomi.youpin", "A8:C2:EE:61:F5:59:97:7A:39:B2:F6:EE:A7:5D:05:FE:4C:B4:D5:B8:0D:CA:F4:B0:CF:23:30:E9:BE:71:8D:34");
        f7502a.put("com.xiaomi.o2o", ApkLoader.PLATFORM_SHA256);
        f7502a.put("com.xiaomi.smarthome", "B0:31:FE:98:A4:DB:B0:D4:D8:26:61:78:7F:25:DE:64:31:82:B3:78:E9:EF:63:2D:8A:DE:A7:5A:AB:58:F2:D8");
        f7502a.put("com.duokan.reader", "88:7E:40:DA:D9:6C:D7:B4:CC:0A:59:67:2B:93:81:19:9F:7D:E2:04:15:B8:92:D7:06:89:5F:84:93:17:8E:2A");
        f7502a.put("com.duokan.fiction", "88:7E:40:DA:D9:6C:D7:B4:CC:0A:59:67:2B:93:81:19:9F:7D:E2:04:15:B8:92:D7:06:89:5F:84:93:17:8E:2A");
        f7502a.put("com.miui.miuibbs", ApkLoader.PLATFORM_SHA256);
        f7502a.put("com.kuaiest.video", "E1:D4:BB:28:5E:26:6D:A6:F5:FC:23:ED:9F:C1:03:CF:11:93:23:B2:E5:02:31:6A:EE:8C:7A:66:E5:A6:99:51");
        f7502a.put("com.xiaomi.antifake3", "D4:5F:07:6F:E2:3A:1A:5B:7F:48:6E:3F:F4:15:47:A2:02:3D:BF:E1:FE:73:35:3B:1E:48:EB:DF:ED:72:CC:6F");
    }

    private static PackageInfo a(Context context, String str) {
        try {
            return context.getPackageManager().getPackageInfo(str, 64);
        } catch (PackageManager.NameNotFoundException e) {
            Log.e("DeviceCredentialProvider", "getPackageVersionName failure", e);
            return null;
        }
    }

    private static String a(PackageInfo packageInfo) {
        try {
            MessageDigest instance = MessageDigest.getInstance("SHA256");
            instance.update(packageInfo.signatures[0].toByteArray());
            StringBuilder sb = new StringBuilder();
            byte[] digest = instance.digest();
            int length = digest.length;
            for (int i = 0; i < length; i++) {
                if (i > 0) {
                    sb.append(":");
                }
                sb.append(Integer.toString((digest[i] & 255) + 256, 16).substring(1));
            }
            return sb.toString().toUpperCase();
        } catch (Exception e) {
            Log.e("DeviceCredentialProvider", "getPackageSHA256 failure", e);
            return "";
        }
    }

    public static String a(String str) {
        try {
            return a(SecurityDeviceCredentialManager.sign(1, str.getBytes(C.UTF8_NAME), true)).toLowerCase();
        } catch (Exception e) {
            Log.e("DeviceCredentialProvider", " getFidNonceSign failure:  ", e);
            return "";
        }
    }

    private static String a(byte[] bArr) {
        char[] cArr = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bArr.length; i++) {
            sb.append(cArr[(bArr[i] >> 4) & 15]);
            sb.append(cArr[bArr[i] & 15]);
        }
        return sb.toString();
    }

    public Bundle call(String str, String str2, Bundle bundle) {
        String str3;
        String str4;
        Bundle bundle2 = new Bundle();
        try {
            int callingPid = Binder.getCallingPid();
            String packageNameByPid = this.f7503b.getPackageNameByPid(callingPid);
            Log.d("DeviceCredentialProvider", "callingPid is [" + callingPid + "] and the callingPackageName is [" + packageNameByPid + "]");
            try {
                if ((getContext().getPackageManager().getApplicationInfo(packageNameByPid, 0).flags & 1) == 0) {
                    String str5 = f7502a.get(packageNameByPid);
                    if (TextUtils.isEmpty(str5)) {
                        bundle2.putInt("error_code", -101);
                        Log.d("DeviceCredentialProvider", "current application has no permissions");
                        return bundle2;
                    } else if (!str5.equals(a(a(getContext(), packageNameByPid)))) {
                        bundle2.putInt("error_code", -103);
                        Log.d("DeviceCredentialProvider", "current application signature error");
                        return bundle2;
                    }
                }
            } catch (Exception e) {
                Log.e("DeviceCredentialProvider", "getApplicationInfo", e);
            }
            if ("getContentSign".equals(str)) {
                if (TextUtils.isEmpty(str2)) {
                    bundle2.putInt("error_code", -104);
                    str4 = "the parameters that need to be signed are empty";
                } else {
                    String a2 = a(str2);
                    if (TextUtils.isEmpty(a2)) {
                        bundle2.putInt("error_code", -105);
                        str4 = "signature failure,the device may not support";
                    } else {
                        Log.d("DeviceCredentialProvider", "signature success");
                        bundle2.putInt("error_code", 100);
                        bundle2.putString("sign", a2);
                    }
                }
                Log.d("DeviceCredentialProvider", str4);
            } else if ("getSecurityDeviceId".equals(str)) {
                try {
                    String securityDeviceId = SecurityDeviceCredentialManager.getSecurityDeviceId();
                    bundle2.putInt("error_code", 100);
                    bundle2.putString(d.g, securityDeviceId);
                    Log.d("DeviceCredentialProvider", "getSecurityDeviceId success");
                } catch (Exception e2) {
                    e = e2;
                    bundle2.putInt("error_code", -106);
                    str3 = "getSecurityDeviceId failure :";
                }
            } else if ("isThisDeviceSupported".equals(str)) {
                try {
                    boolean isThisDeviceSupported = SecurityDeviceCredentialManager.isThisDeviceSupported();
                    bundle2.putInt("error_code", 100);
                    bundle2.putBoolean("isSupport", isThisDeviceSupported);
                    Log.d("DeviceCredentialProvider", "isThisDeviceSupported success");
                } catch (Exception e3) {
                    e = e3;
                    bundle2.putInt("error_code", -107);
                    str3 = "isThisDeviceSupported failure :";
                }
            }
            return bundle2;
            Log.e("DeviceCredentialProvider", str3, e);
            return bundle2;
        } catch (Exception e4) {
            Log.e("DeviceCredentialProvider", "getPackageNameByPid failure,invalid package name", e4);
            bundle2.putInt("error_code", -100);
            return bundle2;
        }
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
        this.f7503b = (SecurityManager) getContext().getSystemService("security");
        return true;
    }

    public Cursor query(Uri uri, String[] strArr, String str, String[] strArr2, String str2) {
        return null;
    }

    public int update(Uri uri, ContentValues contentValues, String str, String[] strArr) {
        return 0;
    }
}
