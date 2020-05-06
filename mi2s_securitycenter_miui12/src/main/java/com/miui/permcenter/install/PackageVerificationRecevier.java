package com.miui.permcenter.install;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IMessenger;
import android.text.TextUtils;
import android.util.Log;
import b.b.c.j.e;
import com.google.android.exoplayer2.extractor.ts.PsExtractor;
import com.miui.permcenter.compact.IntentCompat;
import com.miui.securityscan.i.c;
import java.io.PrintWriter;
import miui.os.Build;

public class PackageVerificationRecevier extends BroadcastReceiver {
    public static String a(String str) {
        if ("com.miui.weather2".equals(str) || "com.miui.calculator".equals(str)) {
            return "D4:5F:07:6F:E2:3A:1A:5B:7F:48:6E:3F:F4:15:47:A2:02:3D:BF:E1:FE:73:35:3B:1E:48:EB:DF:ED:72:CC:6F";
        }
        if ("com.duokan.reader".equals(str)) {
            return "88:7E:40:DA:D9:6C:D7:B4:CC:0A:59:67:2B:93:81:19:9F:7D:E2:04:15:B8:92:D7:06:89:5F:84:93:17:8E:2A";
        }
        if ("com.mfashiongallery.emag".equals(str)) {
            return ApkLoader.PLATFORM_SHA256;
        }
        if ("com.mi.misupport".equals(str)) {
            return "B0:31:FE:98:A4:DB:B0:D4:D8:26:61:78:7F:25:DE:64:31:82:B3:78:E9:EF:63:2D:8A:DE:A7:5A:AB:58:F2:D8";
        }
        if ("com.xiaomi.gamecenter.pad".equals(str)) {
            return ApkLoader.PLATFORM_SHA256;
        }
        if (Build.IS_INTERNATIONAL_BUILD && "com.facemoji.lite.xiaomi".equals(str)) {
            return "4E:5A:78:C0:45:03:16:E3:8A:FA:B3:EC:B6:BA:C9:93:2C:09:5F:2B:34:36:0C:40:12:98:57:B7:86:57:E2:4F";
        }
        if (!Build.IS_INTERNATIONAL_BUILD || !"com.kikaoem.xiaomi.qisiemoji.inputmethod".equals(str)) {
            return null;
        }
        return "A8:1B:81:11:A7:68:10:7F:F2:F8:BB:72:03:4B:D6:56:5E:1D:71:37:4A:43:60:60:E3:41:47:22:D1:96:8A:DB";
    }

    public static void a(PrintWriter printWriter) {
        printWriter.println("=======PackageVerificationRecevier Start========");
        printWriter.println("DEBUG_KEY:3E:9B:95:7F:60:04:4E:76:FD:9E:19:E2:F5:92:F0:39:35:27:B0:59:C5:7D:96:21:AB:CE:EA:13:29:A4:5E:EC");
        printWriter.println("=======PackageVerificationRecevier End========");
    }

    public static boolean a(Context context, PackageInfo packageInfo, String str) {
        String str2 = packageInfo.packageName;
        if (!e.a(context, str2, 0)) {
            return true;
        }
        try {
            if (context.getPackageManager().getPackageInfo(str2, 0) != null) {
                return true;
            }
        } catch (PackageManager.NameNotFoundException unused) {
        }
        Bundle bundle = new Bundle();
        bundle.putInt("flags", PsExtractor.AUDIO_STREAM);
        Bundle call = context.getContentResolver().call(Uri.parse("content://guard"), "parseApk", str, bundle);
        if (call == null) {
            Log.d("PackageVerifyedRecevier", "can't install , parseApk error : " + str);
            return false;
        }
        PackageInfo packageInfo2 = (PackageInfo) call.getParcelable("pkgInfo");
        try {
            if (new Signature("3082046c30820354a003020102020900e552a8ecb9011b7c300d06092a864886f70d0101050500308180310b300906035504061302434e3110300e060355040813074265696a696e673110300e060355040713074265696a696e67310f300d060355040a13065869616f6d69310d300b060355040b13044d495549310d300b060355040313044d495549311e301c06092a864886f70d010901160f6d697569407869616f6d692e636f6d301e170d3131313230363033323632365a170d3339303432333033323632365a308180310b300906035504061302434e3110300e060355040813074265696a696e673110300e060355040713074265696a696e67310f300d060355040a13065869616f6d69310d300b060355040b13044d495549310d300b060355040313044d495549311e301c06092a864886f70d010901160f6d697569407869616f6d692e636f6d30820120300d06092a864886f70d01010105000382010d00308201080282010100c786568a9aff253ad74c5d3e6fbffa12fed44cd3244f18960ec5511bb551e413115197234845112cc3df9bbacd3e0f4b3528cd87ed397d577dc9008e9cbc6a25fc0664d3a3f440243786db8b250d40f6f148c9a3cd6fbc2dd8d24039bd6a8972a1bdee28c308798bfa9bb3b549877b10f98e265f118c05f264537d95e29339157b9d2a31485e0c823521cca6d0b721a8432600076d669e20ac43aa588b52c11c2a51f04c6bb31ad6ae8573991afe8e4957d549591fcb83ec62d1da35b1727dc6b63001a5ef387b5a7186c1e68da1325772b5307b1bc739ef236b9efe06d52dcaf1e32768e3403e55e3ec56028cf5680cfb33971ccf7870572bc47d3e3affa385020103a381e83081e5301d0603551d0e0416041491ae2f8c72e305f92aa9f7452e2a3160b841a15c3081b50603551d230481ad3081aa801491ae2f8c72e305f92aa9f7452e2a3160b841a15ca18186a48183308180310b300906035504061302434e3110300e060355040813074265696a696e673110300e060355040713074265696a696e67310f300d060355040a13065869616f6d69310d300b060355040b13044d495549310d300b060355040313044d495549311e301c06092a864886f70d010901160f6d697569407869616f6d692e636f6d820900e552a8ecb9011b7c300c0603551d13040530030101ff300d06092a864886f70d010105050003820101003b3a699ceb497300f2ab86cbd41c513440bf60aa5c43984eb1da140ef30544d9fbbb3733df24b26f2703d7ffc645bf598a5e6023596a947e91731542f2c269d0816a69c92df9bfe8b1c9bc3c54c46c12355bb4629fe6020ca9d15f8d6155dc5586f5616db806ecea2d06bd83e32b5f13f5a04fe3e5aa514f05df3d555526c63d3d62acf00adee894b923c2698dc571bc52c756ffa7a2221d834d10cb7175c864c30872fe217c31442dff0040a67a2fb1c8ba63eac2d5ba3d8e76b4ff2a49b0db8a33ef4ae0dd0a840dd2a8714cb5531a56b786819ec9eb1051d91b23fde06bd9d0708f150c4f9efe6a416ca4a5e0c23a952af931ad3579fb4a8b19de98f64bd9").equals(packageInfo2.signatures[0])) {
                return true;
            }
            String a2 = c.a(packageInfo2);
            if (d.a(context).g() && "3E:9B:95:7F:60:04:4E:76:FD:9E:19:E2:F5:92:F0:39:35:27:B0:59:C5:7D:96:21:AB:CE:EA:13:29:A4:5E:EC".equals(a2)) {
                return true;
            }
            String a3 = a(str2);
            if (a3 == null) {
                Log.e("PackageVerifyedRecevier", "MIUILOG- not found signature pkg : " + str2);
            }
            if (a2.equals(a3)) {
                return true;
            }
            Log.d("PackageVerifyedRecevier", "can't install , signature verify fail pkg : " + str2);
            return false;
        } catch (Exception e) {
            Log.e("PackageVerifyedRecevier", "error", e);
            return true;
        }
    }

    /* access modifiers changed from: private */
    public static void b(Context context, String str, String str2) {
        if (f.a(context.getApplicationContext())) {
            if ("com.google.android.packageinstaller".equals(str) || "com.android.vending".equals(str)) {
                Intent intent = new Intent("com.miui.global.packageinstaller.action.verifypackage");
                if (TextUtils.isEmpty(str2)) {
                    str2 = "unKnown";
                }
                intent.putExtra("installing", str2);
                context.sendBroadcast(intent, "com.miui.securitycenter.permission.GLOBAL_PACKAGEINSTALLER");
            }
        }
    }

    public void onReceive(Context context, Intent intent) {
        new n(this, context, intent.getStringExtra("path"), intent.getStringExtra("installerPackage"), IMessenger.Stub.asInterface(IntentCompat.getIBinderExtra(intent, "observer"))).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);
    }
}
