package miui.yellowpage;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.provider.Settings;
import android.text.TextUtils;
import android.widget.ImageView;
import com.miui.internal.yellowpage.ImageLoader;
import com.miui.internal.yellowpage.YellowPageAvatar;
import java.security.MessageDigest;
import miui.provider.ExtraTelephony;
import miui.util.HashUtils;
import miui.yellowpage.Tag;
import miui.yellowpage.YellowPageContract;

public class YellowPageImgLoader {
    private static final String YELLOWPAGE_PHOTO_DOWNLOAD_WIFI_ONLY = "yellowpage_photo_download_wifi_only";

    public static class Image {
        private ImageFormat mFormat;
        private ImageProcessor mImageProcesser;
        protected String mUrl;

        public enum ImageFormat {
            JPG,
            PNG
        }

        public interface ImageProcessor {
            Bitmap processImage(Bitmap bitmap);
        }

        public Image(String url) {
            this.mUrl = url;
            this.mFormat = ImageFormat.JPG;
        }

        public Image(String url, ImageFormat format) {
            this.mUrl = url;
            this.mFormat = format;
        }

        public String getUrl() {
            return this.mUrl;
        }

        public String getName() {
            return HashUtils.getSHA1(this.mUrl);
        }

        public ImageFormat getFormat() {
            return this.mFormat;
        }

        public boolean isValid() {
            return !TextUtils.isEmpty(this.mUrl);
        }

        public boolean equals(Object o) {
            if (o == this) {
                return true;
            }
            if (o instanceof Image) {
                return TextUtils.equals(((Image) o).mUrl, this.mUrl);
            }
            return false;
        }

        public int hashCode() {
            String str = this.mUrl;
            if (str == null) {
                return 0;
            }
            return str.hashCode();
        }

        public Bitmap proccessImage(Bitmap originImage) {
            ImageProcessor imageProcessor = this.mImageProcesser;
            if (imageProcessor != null) {
                return imageProcessor.processImage(originImage);
            }
            return originImage;
        }

        public void setImageProcessor(ImageProcessor processor) {
            this.mImageProcesser = processor;
        }
    }

    private YellowPageImgLoader() {
    }

    private static boolean isYellowPagePhotoDownloadWifiOnly(Context context) {
        return Settings.System.getInt(context.getContentResolver(), YELLOWPAGE_PHOTO_DOWNLOAD_WIFI_ONLY, 1) == 1;
    }

    public static Bitmap loadPhoto(Context context, long yid, boolean fetchRemote) {
        return ImageLoader.getInstance(context).loadImageBitmap(new YellowPageAvatar(context, String.valueOf(yid), YellowPageAvatar.YellowPageAvatarFormat.PHOTO_YID), fetchRemote && (!isYellowPagePhotoDownloadWifiOnly(context) || isWifiConnected(context)));
    }

    public static Bitmap loadPhotoByName(Context context, String name, boolean fetchRemote) {
        return ImageLoader.getInstance(context).loadImageBitmap(new YellowPageAvatar(context, name, YellowPageAvatar.YellowPageAvatarFormat.PHOTO_NAME), fetchRemote && (!isYellowPagePhotoDownloadWifiOnly(context) || isWifiConnected(context)));
    }

    public static byte[] loadThumbnailByName(Context context, String name, boolean fetchRemote, int defaultRes, int timeout) {
        return ImageLoader.getInstance(context).loadImageBytes(new YellowPageAvatar(context, name, YellowPageAvatar.YellowPageAvatarFormat.THUMBNAIL_NAME), fetchRemote, defaultRes, timeout);
    }

    public static byte[] loadThumbnailByName(Context context, String name, boolean fetchRemote) {
        return ImageLoader.getInstance(context).loadImageBytes(new YellowPageAvatar(context, name, YellowPageAvatar.YellowPageAvatarFormat.THUMBNAIL_NAME), fetchRemote, 0, 0);
    }

    public static void loadThumbnail(Context context, ImageView view, Image.ImageProcessor processor, long yid, int defaultThumbnailRes) {
        Image image = new YellowPageAvatar(context, String.valueOf(yid), YellowPageAvatar.YellowPageAvatarFormat.THUMBNAIL_YID);
        image.setImageProcessor(processor);
        ImageLoader.getInstance(context).loadImage(view, image, defaultThumbnailRes);
    }

    public static void loadThumbnail(Context context, ImageView view, Image.ImageProcessor processor, String number, int defaultThumbnailRes) {
        Image image = new YellowPageAvatar(context, number, YellowPageAvatar.YellowPageAvatarFormat.THUMBNAIL_NUMBER);
        image.setImageProcessor(processor);
        ImageLoader.getInstance(context).loadImage(view, image, defaultThumbnailRes);
    }

    public static void loadThumbnailByName(Context context, ImageView view, Image.ImageProcessor processor, String name, int defaultThumbnailRes) {
        Image image = new YellowPageAvatar(context, name, YellowPageAvatar.YellowPageAvatarFormat.THUMBNAIL_NAME);
        image.setImageProcessor(processor);
        ImageLoader.getInstance(context).loadImage(view, image, defaultThumbnailRes);
    }

    public static void loadImage(Context context, ImageView view, Image image, int defaultImageRes) {
        ImageLoader.getInstance(context).loadImage(view, image, defaultImageRes);
    }

    public static void pauseLoading(Context context) {
        ImageLoader.getInstance(context).pauseLoading();
    }

    public static void cancelLoading(Context context, ImageView view) {
        ImageLoader.getInstance(context).cancelRequest(view);
    }

    public static void resumeLoading(Context context) {
        ImageLoader.getInstance(context).resumeLoading();
    }

    public static String getDataSha1Digest(byte[] data) {
        if (data == null || data.length == 0) {
            return null;
        }
        try {
            MessageDigest md = MessageDigest.getInstance(HashUtils.SHA1);
            md.update(data);
            return getHexString(md.digest());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static String getHexString(byte[] b) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < b.length; i++) {
            int c = (b[i] & 240) >> 4;
            builder.append((char) ((c < 0 || c > 9) ? (c + 97) - 10 : c + 48));
            int c2 = b[i] & 15;
            builder.append((char) ((c2 < 0 || c2 > 9) ? (c2 + 97) - 10 : c2 + 48));
        }
        return builder.toString();
    }

    private static String getImageUrl(Context context, String name, int width, int height, Image.ImageFormat format) {
        Cursor cursor;
        Uri.Builder builder = Uri.withAppendedPath(YellowPageContract.ImageLookup.CONTENT_URI_IMAGE_URL, Uri.encode(name)).buildUpon();
        builder.appendQueryParameter(Tag.TagWebService.ContentGetImage.PARAM_IMAGE_WIDTH, String.valueOf(width));
        builder.appendQueryParameter("height", String.valueOf(height));
        builder.appendQueryParameter("format", format == Image.ImageFormat.JPG ? "jpg" : "png");
        Uri uri = builder.build();
        if (YellowPageUtils.isContentProviderInstalled(context, uri) && (cursor = context.getContentResolver().query(uri, (String[]) null, (String) null, (String[]) null, (String) null)) != null) {
            try {
                if (cursor.moveToFirst()) {
                    return cursor.getString(0);
                }
                cursor.close();
            } finally {
                cursor.close();
            }
        }
        return null;
    }

    public static void loadImage(Context context, ImageView view, Image.ImageProcessor processor, Image.ImageFormat format, String name, int width, int height, int defaultImageRes) {
        Image image = new Image(HostManager.getImageUrl(context, name, width, height, format), format);
        image.setImageProcessor(processor);
        ImageLoader.getInstance(context).loadImage(view, image, defaultImageRes);
    }

    public static Bitmap loadThumbnail(Context context, String number, boolean fetchRemote) {
        return ImageLoader.getInstance(context).loadImageBitmap(new YellowPageAvatar(context, number, YellowPageAvatar.YellowPageAvatarFormat.THUMBNAIL_NUMBER), fetchRemote);
    }

    public static Bitmap loadPhoneDisplayAd(Context context, long yid, String number, boolean isIncoming) {
        Bitmap bitmap;
        String url;
        boolean z = true;
        int callType = isIncoming ? 1 : 2;
        Uri.Builder builder = YellowPageContract.ImageLookup.CONTENT_URI_IMAGE_PHONE_AD.buildUpon();
        builder.appendQueryParameter("number", number);
        builder.appendQueryParameter("yid", String.valueOf(yid));
        builder.appendQueryParameter(ExtraTelephony.FirewallLog.CALL_TYPE, String.valueOf(callType));
        Uri uri = builder.build();
        if (!YellowPageUtils.isContentProviderInstalled(context, uri)) {
            return null;
        }
        Cursor cursor = context.getContentResolver().query(uri, (String[]) null, (String) null, (String[]) null, (String) null);
        String url2 = null;
        if (cursor != null) {
            try {
                if (cursor.moveToFirst()) {
                    url2 = cursor.getString(0);
                }
            } finally {
                cursor.close();
            }
        }
        if (!TextUtils.isEmpty(url2)) {
            Bitmap bitmap2 = ImageLoader.getInstance(context).loadImageBitmap(new Image(url2), true);
            url = Uri.parse(url2).getLastPathSegment();
            bitmap = bitmap2;
        } else {
            url = url2;
            bitmap = null;
        }
        String valueOf = String.valueOf(yid);
        if (bitmap == null) {
            z = false;
        }
        Uri uri2 = uri;
        YellowPageStatistic.viewYellowPageInPhoneCall(context, number, callType, true, valueOf, url, z);
        return bitmap;
    }

    private static boolean isWifiConnected(Context context) {
        ConnectivityManager connManager = (ConnectivityManager) context.getSystemService("connectivity");
        NetworkInfo networkInfo = connManager.getActiveNetworkInfo();
        return !connManager.isActiveNetworkMetered() && networkInfo != null && networkInfo.isConnected();
    }
}
