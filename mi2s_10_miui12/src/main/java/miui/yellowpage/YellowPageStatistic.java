package miui.yellowpage;

import android.content.ContentProviderClient;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.RemoteException;
import android.text.TextUtils;
import java.io.Serializable;
import miui.telephony.phonenumber.Prefix;
import miui.yellowpage.Tag;
import miui.yellowpage.YellowPageContract;

public class YellowPageStatistic {
    private static final String TAG = "YellowPageStatistic";

    public interface Display {
        public static final String CALL = "call";
        public static final String CATEGORY = "category";
        public static final String EXPRESS_INQUIRY = "expressInquery";
        public static final String EXPRESS_SEND = "expressSend";
        public static final String FLOW_OF_PACKAGES = "flowOfPackages";
        public static final String HOME = "home";
        public static final String NAVIGATION = "navigation";
        public static final String NEARBY_HOTCAT = "nearby_hotcat";
        public static final String NEARBY_YP = "nearby_yp";
        public static final String RECHARGE = "recharge";
        public static final String SEARCH_NAVIGATION = "search_navigation";
        public static final String SMS = "sms";
        public static final String USER_CENTER = "ucenter";
        public static final String WEB = "web";
    }

    private YellowPageStatistic() {
    }

    public static class StatsContext implements Serializable {
        public static final StatsContext EMPTY = new StatsContext(Prefix.EMPTY, 0);
        private static final long serialVersionUID = 998449268880523939L;
        private String mSource;
        private int mSourceModuleId;

        private StatsContext(String source, int srcModuleId) {
            this.mSource = source;
            this.mSourceModuleId = srcModuleId;
        }

        public String getSource() {
            return this.mSource;
        }

        public int getSourceModuleId() {
            return this.mSourceModuleId;
        }

        public static StatsContext parse(Intent intent) {
            int srcModuleId = 0;
            String source = Prefix.EMPTY;
            if (intent != null) {
                srcModuleId = intent.getIntExtra("mid", 0);
                source = intent.getStringExtra("source");
                if ((srcModuleId == 0 || TextUtils.isEmpty(source)) && YellowPageStatistic.isUriIntent(intent)) {
                    Uri data = intent.getData();
                    if (srcModuleId == 0) {
                        String srcModuleIdParam = data.getQueryParameter("mid");
                        if (!TextUtils.isEmpty(srcModuleIdParam) && TextUtils.isDigitsOnly(srcModuleIdParam)) {
                            srcModuleId = Integer.parseInt(srcModuleIdParam);
                        }
                    }
                    if (TextUtils.isEmpty(source)) {
                        source = data.getQueryParameter("source");
                    }
                }
            }
            Log.d(YellowPageStatistic.TAG, "mid: " + srcModuleId + ", source: " + source);
            return new StatsContext(source, srcModuleId);
        }

        public static StatsContext parse(Bundle bundle) {
            int srcModuleId = 0;
            String source = Prefix.EMPTY;
            if (bundle != null) {
                srcModuleId = bundle.getInt("mid", 0);
                source = bundle.getString("source");
            }
            return new StatsContext(source, srcModuleId);
        }

        public void attach(Bundle bundle) {
            if (bundle != null) {
                bundle.putString("source", this.mSource);
                bundle.putInt("mid", this.mSourceModuleId);
            }
        }

        public void attach(Intent intent) {
            if (intent != null) {
                intent.putExtra("source", this.mSource);
                intent.putExtra("mid", this.mSourceModuleId);
            }
        }
    }

    public static void viewYellowPageInPhoneCall(Context context, String number, int type, boolean hit, String yid, String displayAdName, boolean show) {
        final Context context2 = context;
        final String str = number;
        final int i = type;
        final boolean z = hit;
        final String str2 = yid;
        final String str3 = displayAdName;
        final boolean z2 = show;
        ThreadPool.execute(new Runnable() {
            public void run() {
                Uri uri = Uri.withAppendedPath(YellowPageContract.Statistic.CONTENT_URI, YellowPageContract.Statistic.DIRECTORY_VIEW_YELLOWPAGE_IN_PHONE_CALL);
                if (YellowPageUtils.isContentProviderInstalled(context2, uri)) {
                    ContentValues cv = new ContentValues();
                    cv.put("number", str);
                    cv.put("type", Integer.valueOf(i));
                    cv.put("hit", Boolean.valueOf(z));
                    cv.put("yid", str2);
                    cv.put("displayAdName", str3);
                    cv.put("show", z2 ? "1" : "0");
                    ContentProviderClient client = null;
                    try {
                        client = context2.getContentResolver().acquireUnstableContentProviderClient(uri);
                        client.insert(uri, cv);
                    } catch (RemoteException e) {
                        Log.e(YellowPageStatistic.TAG, Prefix.EMPTY, e);
                        if (client == null) {
                            return;
                        }
                    } catch (Throwable th) {
                        if (client != null) {
                            client.release();
                        }
                        throw th;
                    }
                    client.release();
                }
            }
        });
    }

    public static void viewNormalDisplay(final Context context, final String display, final String source, final int srcModuleId) {
        ThreadPool.execute(new Runnable() {
            public void run() {
                Uri uri = Uri.withAppendedPath(YellowPageContract.Statistic.CONTENT_URI, YellowPageContract.Statistic.DIRECTORY_VIEW_NORMAL_DISPLAY);
                if (YellowPageUtils.isContentProviderInstalled(context, uri)) {
                    ContentValues cv = new ContentValues();
                    cv.put("display", display);
                    cv.put("source", source);
                    cv.put("srcModuleId", Integer.valueOf(srcModuleId));
                    context.getContentResolver().insert(uri, cv);
                }
            }
        });
    }

    public static void clickNavigationItem(final Context context, final String moduleId) {
        ThreadPool.execute(new Runnable() {
            public void run() {
                Uri uri = Uri.withAppendedPath(YellowPageContract.Statistic.CONTENT_URI, YellowPageContract.Statistic.DIRECTORY_CLICK_NAVIGATION_ITEM);
                if (YellowPageUtils.isContentProviderInstalled(context, uri)) {
                    ContentValues cv = new ContentValues();
                    cv.put("moduleId", moduleId);
                    context.getContentResolver().insert(uri, cv);
                }
            }
        });
    }

    public static void clickModuleItem(Context context, String moduleId, String hotLinkUrl, String source, String display, int sourceModuleId) {
        final Context context2 = context;
        final String str = moduleId;
        final String str2 = hotLinkUrl;
        final String str3 = source;
        final String str4 = display;
        final int i = sourceModuleId;
        ThreadPool.execute(new Runnable() {
            public void run() {
                Uri uri = Uri.withAppendedPath(YellowPageContract.Statistic.CONTENT_URI, YellowPageContract.Statistic.DIRECTORY_CLICK_MODULE_ITEM);
                if (YellowPageUtils.isContentProviderInstalled(context2, uri)) {
                    ContentValues cv = new ContentValues();
                    cv.put("moduleId", str);
                    cv.put("hotLinkUrl", str2);
                    cv.put("source", str3);
                    cv.put("display", str4);
                    cv.put("sourceModuleId", Integer.valueOf(i));
                    context2.getContentResolver().insert(uri, cv);
                }
            }
        });
    }

    public static void clickYellowPage(Context context, String yid, String display, String source, int srcModuleId) {
        final Context context2 = context;
        final String str = yid;
        final String str2 = display;
        final String str3 = source;
        final int i = srcModuleId;
        ThreadPool.execute(new Runnable() {
            public void run() {
                Uri uri = Uri.withAppendedPath(YellowPageContract.Statistic.CONTENT_URI, YellowPageContract.Statistic.DIRECTORY_CLICK_YELLOW_PAGE);
                if (YellowPageUtils.isContentProviderInstalled(context2, uri)) {
                    ContentValues cv = new ContentValues();
                    cv.put("yid", str);
                    cv.put("display", str2);
                    cv.put("source", str3);
                    cv.put("srcModuleId", Integer.valueOf(i));
                    context2.getContentResolver().insert(uri, cv);
                }
            }
        });
    }

    public static void clickSearchItem(Context context, String type, String id, String keyword, String index) {
        final Context context2 = context;
        final String str = type;
        final String str2 = id;
        final String str3 = keyword;
        final String str4 = index;
        ThreadPool.execute(new Runnable() {
            public void run() {
                Uri uri = Uri.withAppendedPath(YellowPageContract.Statistic.CONTENT_URI, YellowPageContract.Statistic.DIRECTORY_CLICK_SEARCH_ITEM);
                if (YellowPageUtils.isContentProviderInstalled(context2, uri)) {
                    ContentValues cv = new ContentValues();
                    cv.put("type", str);
                    cv.put("id", str2);
                    cv.put("keyword", str3);
                    cv.put("index", str4);
                    context2.getContentResolver().insert(uri, cv);
                }
            }
        });
    }

    public static boolean uploadData(Context context) {
        Uri result;
        Uri uri = Uri.withAppendedPath(YellowPageContract.Statistic.CONTENT_URI, YellowPageContract.Statistic.DIRECTORY_UPLOAD_DATA);
        if (YellowPageUtils.isContentProviderInstalled(context, uri) && (result = context.getContentResolver().insert(uri, new ContentValues())) != null) {
            return "1".equals(result.getLastPathSegment());
        }
        return false;
    }

    public static void logEvent(Context context, String event, String display, String source, int srcModuleId, String values) {
        final Context context2 = context;
        final String str = event;
        final String str2 = display;
        final String str3 = source;
        final int i = srcModuleId;
        final String str4 = values;
        ThreadPool.execute(new Runnable() {
            public void run() {
                Uri uri = Uri.withAppendedPath(YellowPageContract.Statistic.CONTENT_URI, YellowPageContract.Statistic.DIRECTORY_LOG_EVENT);
                if (YellowPageUtils.isContentProviderInstalled(context2, uri)) {
                    ContentValues cv = new ContentValues();
                    cv.put(Tag.TagWebService.CommonResult.RESULT_TYPE_EVENT, str);
                    cv.put("display", str2);
                    cv.put("source", str3);
                    cv.put("srcModuleId", Integer.valueOf(i));
                    cv.put("values", str4);
                    context2.getContentResolver().insert(uri, cv);
                }
            }
        });
    }

    /* access modifiers changed from: private */
    public static boolean isUriIntent(Intent intent) {
        if (intent == null || !"android.intent.action.VIEW".equals(intent.getAction()) || intent.getData() == null) {
            return false;
        }
        return true;
    }
}
