package android.net.shared;

import android.content.Context;
import java.net.URL;

class MiuiNetworkMonitorUtils {
    private static final int SOCKET_TIMEOUT_MS = 10000;
    private static final String TAG = "MiuiNetworkMonitorUtils";
    private static final String[] sDefaultCandidates = {"http://info.3g.qq.com/", "http://m.baidu.com/", "http://m.sohu.com/"};
    private static final String[] sGlobalCandidates = {"http://www.google.com/", "http://www.facebook.com/", "http://www.youtube.com/"};

    MiuiNetworkMonitorUtils() {
    }

    static final URL getCaptivePortalServer(Context context, URL url) {
        return MiuiConfigCaptivePortal.getCaptivePortalServer(context, url.toString());
    }

    static final String getCaptivePortalServer(Context context, String server) {
        return MiuiConfigCaptivePortal.getCaptivePortalServer(server);
    }

    static final String getCaptivePortalServer(Context context, String url, boolean isHttps) {
        String server = MiuiConfigCaptivePortal.getCaptivePortalServer((String) null);
        if (server == null) {
            return url;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(isHttps ? "https" : "http");
        sb.append("://");
        sb.append(server);
        sb.append("/generate_204");
        return sb.toString();
    }
}
