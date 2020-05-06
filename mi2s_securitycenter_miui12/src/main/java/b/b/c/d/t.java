package b.b.c.d;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.util.Log;
import b.b.b.a.c;
import com.miui.networkassistant.ui.activity.NetworkDiagnosticsTipActivity;
import com.miui.securitycenter.Application;
import com.miui.securitycenter.R;
import com.miui.warningcenter.mijia.MijiaAlertModel;

public class t {
    public static Drawable a() {
        return new c(Application.d().getResources().getDrawable(R.drawable.big_backgroud_def));
    }

    public static void a(Context context, String str) {
        try {
            Intent launchIntentForPackage = context.getPackageManager().getLaunchIntentForPackage(str);
            if (launchIntentForPackage != null) {
                context.startActivity(launchIntentForPackage);
            }
        } catch (Exception e) {
            Log.e("Utils", " startActivity error ", e);
        }
    }

    public static void a(Context context, String str, String str2) {
        Intent intent = new Intent("miui.intent.action.CLEAN_MASTER_SECURITY_WEB_VIEW");
        intent.putExtra(MijiaAlertModel.KEY_URL, str);
        intent.putExtra(NetworkDiagnosticsTipActivity.TITLE_KEY_NAME, str2);
        context.startActivity(intent);
    }

    public static Drawable b() {
        return new c(Application.d().getResources().getDrawable(R.drawable.small_backgroud_def));
    }
}
