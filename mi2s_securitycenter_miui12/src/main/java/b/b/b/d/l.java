package b.b.b.d;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

public class l {
    public static int a(Context context, String str) {
        try {
            return context.getPackageManager().getPackageInfo(str, 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            Log.e("PackageInfoUtils", e.toString());
            return -1;
        }
    }

    public static String b(Context context, String str) {
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(str, 64);
            return (packageInfo.signatures == null || packageInfo.signatures.length <= 0) ? "" : c.a(String.valueOf(packageInfo.signatures[0].toChars()));
        } catch (PackageManager.NameNotFoundException e) {
            Log.e("PackageInfoUtils", e.toString());
            return "";
        }
    }

    public static String c(Context context, String str) {
        Bundle bundle = new Bundle();
        bundle.putInt("flags", 64);
        Signature[] signatureArr = ((PackageInfo) context.getContentResolver().call(Uri.parse("content://guard"), "parseApk", str, bundle).getParcelable("pkgInfo")).signatures;
        return (signatureArr == null || signatureArr.length <= 0) ? "" : c.a(String.valueOf(signatureArr[0].toChars()));
    }
}
