package miui.yellowpage;

import android.content.Context;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import miui.os.Build;
import miui.yellowpage.YellowPageImgLoader;

@Deprecated
public class HostManager {
    protected static final String BASE_URL = (Build.IS_INTERNATIONAL_BUILD ? GLOBAL_BASE_URL : FORMAL_BASE_URL);
    private static final String DIRECTORY_IMAGE_JPG = "/thumbnail/jpeg/w%dh%d/";
    private static final String DIRECTORY_IMAGE_PHOTO = "/thumbnail/jpeg/h%d/";
    private static final String DIRECTORY_IMAGE_PNG = "/thumbnail/png/w%d/";
    private static final String DIRECTORY_IMAGE_THUMBNAIL = "/thumbnail/jpeg/w100/";
    private static final String FORMAL_BASE_URL = "https://api.huangye.miui.com";
    private static final String GLOBAL_BASE_URL = "https://global.api.huangye.miui.com";
    protected static final String URL_DEFAULT_IMAGE_BASE = "http://file.market.xiaomi.com";
    protected static final String URL_SPBOOK_BASE = (BASE_URL + "/spbook");
    protected static final String URL_YELLOW_PAGE_BASE = (URL_SPBOOK_BASE + "/yellowpage");
    private static int sDisplayHeight;
    private static String sImageDomain;

    private HostManager() {
    }

    public static String getBaseUrl() {
        return BASE_URL;
    }

    public static String getYellowPageBaseUrl() {
        return URL_YELLOW_PAGE_BASE;
    }

    public static String getSpbookBaseUrl() {
        return URL_SPBOOK_BASE;
    }

    public static String getDefaultImageBase() {
        return URL_DEFAULT_IMAGE_BASE;
    }

    public static String getImageUrl(Context context, String name, int width, int height, YellowPageImgLoader.Image.ImageFormat format) {
        return getImageUrl(getImageDomain(context), name, width, height, format);
    }

    private static String getImageDomain(Context context) {
        String str = sImageDomain;
        if (str != null) {
            return str;
        }
        sImageDomain = URL_DEFAULT_IMAGE_BASE;
        String domain = InvocationHandler.invoke(context, "image_domain").getString("domain");
        if (!TextUtils.isEmpty(domain)) {
            sImageDomain = domain;
            if (!sImageDomain.startsWith("http://")) {
                sImageDomain = "http://" + sImageDomain;
            }
        }
        return sImageDomain;
    }

    public static String getYellowPageThumbnail(Context context, String name) {
        if (TextUtils.isEmpty(name)) {
            return null;
        }
        return getImageDomain(context) + DIRECTORY_IMAGE_THUMBNAIL + name;
    }

    public static String getYellowPagePhotoUrl(Context context, String name) {
        if (TextUtils.isEmpty(name)) {
            return null;
        }
        return getImageDomain(context) + String.format(DIRECTORY_IMAGE_PHOTO, new Object[]{Integer.valueOf(getScreenHeight(context))}) + name;
    }

    private static String getImageUrl(String imageDomain, String name, int width, int height, YellowPageImgLoader.Image.ImageFormat format) {
        if (TextUtils.isEmpty(name) || width <= 0 || height <= 0 || TextUtils.isEmpty(imageDomain)) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(imageDomain);
        sb.append(String.format(format == YellowPageImgLoader.Image.ImageFormat.PNG ? DIRECTORY_IMAGE_PNG : DIRECTORY_IMAGE_JPG, new Object[]{Integer.valueOf(width), Integer.valueOf(height)}));
        sb.append(name);
        return sb.toString();
    }

    private static int getScreenHeight(Context context) {
        if (sDisplayHeight == 0) {
            DisplayMetrics dm = new DisplayMetrics();
            ((WindowManager) context.getSystemService("window")).getDefaultDisplay().getMetrics(dm);
            sDisplayHeight = dm.heightPixels;
        }
        return sDisplayHeight;
    }
}
