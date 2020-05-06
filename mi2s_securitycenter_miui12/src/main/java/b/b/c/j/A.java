package b.b.c.j;

import android.content.Context;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.widget.Toast;
import com.miui.securitycenter.Application;
import miui.util.AppConstants;

public class A {
    public static SpannableString a(String str, int i, String... strArr) {
        SpannableString spannableString = new SpannableString(str);
        try {
            for (String str2 : strArr) {
                int indexOf = str.indexOf(str2);
                spannableString.setSpan(new ForegroundColorSpan(i), indexOf, str2.length() + indexOf, 34);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return spannableString;
    }

    public static void a(Context context, int i) {
        Toast.makeText(context, i, 0).show();
    }

    public static void a(Context context, String str) {
        Toast.makeText(context, str, 0).show();
    }

    public static boolean a() {
        return AppConstants.getSdkLevel(Application.d(), "com.miui.core") >= 17;
    }
}
