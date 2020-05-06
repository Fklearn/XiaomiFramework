package miui.content.res;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.MiuiConfiguration;
import android.text.TextUtils;
import android.util.Log;
import miui.ddm.DdmHandleAppName;

public class ThemeFontChangeHelper {
    private static int sWebViewThemeChanged = -1;

    public static void markWebViewCreated(Context context) {
        MiuiConfiguration c = GlobalConfiguration.getExtraConfig(context.getResources().getConfiguration());
        if (!c.checkFontChange(sWebViewThemeChanged)) {
            sWebViewThemeChanged = c.themeChanged;
        }
    }

    public static void quitProcessIfNeed(Configuration newConfig) {
        if (newConfig != null && !TextUtils.equals("system_process", DdmHandleAppName.getAppName()) && GlobalConfiguration.getExtraConfig(newConfig).checkFontChange(sWebViewThemeChanged)) {
            Log.d("ThemeFontChangeHelper", "app has checked webview-font change and process will restart.");
            System.exit(0);
        }
    }
}
