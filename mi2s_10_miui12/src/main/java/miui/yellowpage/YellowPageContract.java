package miui.yellowpage;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.provider.ContactsContract;
import com.miui.internal.vip.utils.AuthHttpRequest;
import java.util.HashMap;
import java.util.Map;

public class YellowPageContract {
    public static final String AUTHORITY = "miui.yellowpage";
    protected static final String BASE_URI_STRING = "content://miui.yellowpage/";
    public static final Uri INVOACTION_URI = Uri.parse("content://miui.yellowpage.invocation/");

    public interface AntispamCategory {
        public static final String CID = "cid";
        public static final int CID_CUSTOM_START = 10000;
        public static final Uri CONTENT_URI = Uri.parse("content://miui.yellowpage/antispam_category");
        public static final String DIRECTORY = "antispam_category";
        public static final String ICON = "icon";
        public static final String NAMES = "names";
        public static final String ORDER = "display_order";
        public static final String TYPE = "type";
        public static final int TYPE_DO_NOT_SHOW_IN_MARK = 2;
        public static final int TYPE_SYSTEM = 0;
        public static final int TYPE_USER = 1;
    }

    public interface AntispamNumber extends BaseColumns {
        public static final String CID = "cid";
        public static final Uri CONTENT_MARK_NUMBER_URI = Uri.parse("content://miui.yellowpage/antispam_mark_number");
        public static final Uri CONTENT_PHONE_LOOKUP_SUSPECT_SERVICE_URI = Uri.parse("content://miui.yellowpage/antispam_number_suspect_service");
        public static final Uri CONTENT_PHONE_LOOKUP_URI = Uri.parse("content://miui.yellowpage/antispam_number_phone_lookup");
        public static final Uri CONTENT_PRESET_PHONE_LOOKUP_URI = Uri.parse("content://miui.yellowpage/antispam_number_preset_phone_lookup");
        public static final Uri CONTENT_UPDTATE_CALL_LOG_TAG_URI = Uri.parse("content://miui.yellowpage/antispam_update_call_log_tag");
        public static final Uri CONTENT_URI = Uri.parse("content://miui.yellowpage/antispam_number");
        public static final String DIRECTORY = "antispam_number";
        public static final String DIRECTORY_MARK_NUMBER = "antispam_mark_number";
        public static final String DIRECTORY_PHONE_LOOKUP = "antispam_number_phone_lookup";
        public static final String DIRECTORY_PHONE_LOOKUP_SUSPECT_SERVICE = "antispam_number_suspect_service";
        public static final String DIRECTORY_PRESET_PHONE_LOOKUP = "antispam_number_preset_phone_lookup";
        public static final String DIRECTORY_UPDATE_CALL_LOG_TAG = "antispam_update_call_log_tag";
        public static final String MARKED_COUNT = "marked_count";
        public static final String MIN_MATCH = "min_match";
        public static final String NORMALIZED_NUMBER = "normalized_number";
        public static final String NUMBER = "number";
        public static final String NUMBER_TYPE = "number_type";
        public static final int NUMBER_TYPE_BLACK = 0;
        public static final int NUMBER_TYPE_GREY = 2;
        public static final int NUMBER_TYPE_NORMAL = 3;
        public static final int NUMBER_TYPE_SUSPECT = 1;
        public static final String PID = "pid";
        public static final String TYPE = "type";
        public static final int TYPE_CLOUD = 2;
        public static final int TYPE_MARKED = 3;
        public static final int TYPE_NOT_IN_PRESET_FILE = 5;
        public static final int TYPE_ONE_RING_CALL = 4;
        public static final int TYPE_PRESET = 1;
        public static final String UPLOAD = "upload";
        public static final int UPLOADED_NO = 0;
        public static final int UPLOADED_YES = 1;
    }

    public interface AntispamWhiteList {
        public static final Uri CONTNET_URI = Uri.parse("content://miui.yellowpage/antispam_white_list");
        public static final String DIRECTORY = "antispam_white_list";
        public static final String NUMBER = "number";
    }

    public interface Cache {
        public static final String ACCOUNT_ID = "account_id";
        public static final String CACHE_KEY = "cache_key";
        public static final String CONTENT = "content";
        public static final Uri CONTENT_URI = Uri.parse("content://miui.yellowpage/cache");
        public static final String DIRECTORY = "cache";
        public static final String ETAG = "etag";
        public static final String REMARK = "remark";
    }

    public interface ExtraContacts {
        public static final String CLOUD_ANTISPAM_TYPE = "cloud_antispam_type";
        public static final String CLOUD_ANTISPAM_TYPE_TAG = "cloud_antispam_type_tag";
        public static final Uri CONTENT_URI_REBUILD_T9 = Uri.withAppendedPath(ContactsContract.AUTHORITY_URI, "build_yellowpage_t9_index");
        public static final String NUMBER_TYPE = "number_type";
        public static final int NUMBER_TYPE_NORMAL = 0;
        public static final int NUMBER_TYPE_YELLOWPAGE = 1;
    }

    public interface HotWord {
        public static final String CONTENT = "content";
        public static final Uri CONTENT_URI = Uri.parse("content://miui.yellowpage//hotword");
        public static final String DIRECTORY = "hotword";
        public static final String UPDATE_SUFFIX = "update";
    }

    public interface ImageLookup {
        public static final Uri CONTENT_URI_AVATAR_URL = Uri.parse("content://miui.yellowpage/image_avatar");
        public static final Uri CONTENT_URI_IMAGE = Uri.parse("content://miui.yellowpage/image");
        public static final Uri CONTENT_URI_IMAGE_CLOUD = Uri.parse("content://miui.yellowpage/image_cloud");
        public static final Uri CONTENT_URI_IMAGE_PHONE_AD = Uri.parse("content://miui.yellowpage/phone_ad_url");
        public static final Uri CONTENT_URI_IMAGE_URL = Uri.parse("content://miui.yellowpage/image_url");
        public static final String DIRECTORY_AVATAR_URL = "image_avatar";
        public static final String DIRECTORY_IMAGE = "image";
        public static final String DIRECTORY_IMAGE_CLOUD = "image_cloud";
        public static final String DIRECTORY_IMAGE_PHONE_AD = "phone_ad_url";
        public static final String DIRECTORY_IMAGE_URL = "image_url";
    }

    public interface Ivr {
        public static final Uri CONTENT_URI = Uri.parse("content://miui.yellowpage/ivr");
        public static final Uri CONTENT_URI_NIVR = Uri.parse("content://miui.yellowpage/non_ivr");
        public static final String DATA = "data";
        public static final String DIRECTORY = "ivr";
        public static final String DIRECTORY_NIVR = "non_ivr";
    }

    public interface Location {
        public static final Uri CONTENT_URI = Uri.parse("content://miui.yellowpage/current_location");
        public static final String DIRECTORY = "current_location";
        public static final String LOCATION_ID = "location_id";
        public static final String LOCATION_NAME = "location_name";
    }

    public interface Method {
        public static final String DEVICE_HASHED_DEVICE_INFO = "device.hashed_device_info";
        public static final String DEVICE_LANGUAGE = "device.language";
        public static final String DEVICE_SUPPORTS = "device.supports";
        public static final String DEVICE_UUID = "device.uuid";
        public static final String GEO_LOCATION = "geo_location";
        public static final String GEO_LOCATION_ID = "geo_location_id";
        public static final String IMAGE_DOMAIN = "image_domain";
        public static final String MODULE_TO_INTENT = "module.to_intent";
        public static final String REQUEST_DECRYPT_DATA = "request.decrypt";
        public static final String REQUEST_ENCRYPT_DATA = "request.encrypt";
        public static final String REQUEST_HTTP = "request.http";
        public static final String REQUEST_LOCAL_EXPRESS_INFO = "request.local.express.info";
        public static final String REQUEST_REMOTE_EXPRESS_INFO = "request.remote.express.info";
        public static final String REQUEST_REMOTE_NAVIGATION_INFO = "request.remote.navigation.info";
        public static final String REQUEST_REMOTE_NAVIGATION_UPDATE = "request.remote.navigation.update";
        public static final String REQUEST_REMOTE_YELLOWPAGE_BY_MIID = "request.yellowpage.remote.miid";
        public static final String REQUEST_SIGN = "request.sign";
        public static final String YELLOWPAGE_INSERT = "yellowpage.insert";
        public static final String YELLOWPAGE_IS_NETWORK_ALLOWED = "yellowpage.isnetworkallowed";
        public static final String YELLOWPAGE_MENU = "yellowpage.menu";
        public static final String YELLOWPAGE_PARSE_MENU = "yellowpage.parsemenu";
        public static final String YELLOWPAGE_PARSE_SUB_MENU = "yellowpage.parsesubmenu";
        public static final String YELLOWPAGE_SET_NETWORK_ALLOWED = "yellowpage.setnetworkallowed";
    }

    public interface MiPub {
        public static final Uri CONTENT_URI_SERVICE_NUMBER = Uri.parse("content://miui.yellowpage/service_number");
        public static final Uri CONTENT_URI_YELLOWPAGE = Uri.parse("content://miui.yellowpage/yellowpage");
        public static final String DIRECTORY = "service_number";
        public static final String DIRECTORY_YELLOWPAGE = "yellowpage";
    }

    public interface MipubPhoneEvent {
        public static final Uri CONTENT_URI_MIPUB_PHONE_EVENT = Uri.parse("content://miui.yellowpage/phone_event");
        public static final String DIRECTORY = "phone_event";
        public static final String EXTRA_DATA_CONNECTED = "connected";
        public static final String EXTRA_DATA_DIRECTION = "direction";
        public static final String EXTRA_DATA_NUMBER = "number";
        public static final String URI_PARAM_END_TIME = "extra_end_time";
        public static final String URI_PARAM_EXTRA_DATA = "extra_data";
        public static final String URI_PARAM_SOURCE = "extra_source";
        public static final String URI_PARAM_START_TIME = "extra_start_time";
        public static final String URI_PARAM_TYPE = "extra_type";
    }

    public interface Navigation {
        public static final String CONTENT = "content";
        public static final Uri CONTENT_BANNER_URI = Uri.parse("content://miui.yellowpage/navigation_banner");
        public static final Uri CONTENT_URI = Uri.parse("content://miui.yellowpage/navigation");
        public static final String DIRECTORY = "navigation";
        public static final String DIRECTORY_BANNER = "navigation_banner";
        public static final String UPDATE_SUFFIX = "update";

        public interface Version {
            public static final Uri CONTENT_URI = Uri.parse("content://miui.yellowpage/navigation/version");
            public static final String DIRECTORY = "navigation/version";
            public static final String VERSION = "version";
        }
    }

    public interface NavigationTab {
        public static final Uri CONTENT_URI = Uri.parse("content://miui.yellowpage/navigation_tab");
        public static final String DIRECTORY = "navigation_tab";
        public static final String TYPE = "type";
        public static final String VALUE = "value";
    }

    public interface NumberIdentify extends BaseColumns {
        public static final Uri CONTENT_URI = Uri.parse("content://miui.yellowpage//number_identify");
        public static final String DIRECTORY = "number_identify";
        public static final String LAST_UPDATE_TIME = "last_update_time";
        public static final String NORMALIZED_NUMBER = "normalized_number";
        public static final String NUMBER = "number";
        public static final String QUERY_PACKAGE = "query_package";
        public static final String QUERY_STATUS = "query_status";
        public static final int QUERY_STATUS_INVALID = 0;
        public static final int QUERY_STATUS_LATER_REMOTE_QUERIED = 3;
        public static final int QUERY_STATUS_LOCAL_QUERIED = 1;
        public static final int QUERY_STATUS_REMOTE_ERROR = 4;
        public static final int QUERY_STATUS_REMOTE_QUERIED = 2;
        public static final String TYPE = "type";
    }

    public static final class Permission implements BaseColumns {
        public static final int ALLOW = 3;
        public static final Uri CONTENT_URI = Uri.parse("content://miui.yellowpage/permission");
        public static final String DIRECTORY = "permission";
        public static final String HOST = "host";
        public static final String PERMISSION = "permission";
        public static final String STATE = "state";
        public static final int UNKNOWN = 0;
    }

    public interface PhoneLookup {
        public static final String ATD_COUNT = "atd_count";
        public static final String ATD_ID = "atd_category_id";
        public static final String ATD_PROVIDER = "atd_provider";
        public static final String CALL_MENU = "call_menu";
        public static final Uri CONTENT_FRAUD_VERIFY = Uri.parse("content://miui.yellowpage/phone_lookup_fraud_verify");
        public static final Uri CONTENT_NORMALIZED_NUMBER = Uri.parse("content://miui.yellowpage/phone_lookup_normalized_number");
        public static final Uri CONTENT_URI = Uri.parse("content://miui.yellowpage/phone_lookup");
        public static final Uri CONTENT_URI_CLOUD = Uri.parse("content://miui.yellowpage/phone_lookup_cloud");
        public static final String DIRECTORY = "phone_lookup";
        public static final String DIRECTORY_CLOUD = "phone_lookup_cloud";
        public static final String DIRECTORY_FRAUD_VERIFY = "phone_lookup_fraud_verify";
        public static final String DIRECTORY_NORMALIZED_NUMBER = "phone_lookup_normalized_number";
        public static final String FLAG = "flag";
        public static final String HIDE = "hide";
        public static final String MIN_MATCH = "min_match";
        public static final String NORMALIZED_NUMBER = "normalized_number";
        public static final String NUMBER = "number";
        public static final String PHOTO_URL = "photo_url";
        public static final String PROVIDER_ID = "provider_id";
        public static final String SLOGAN = "slogan";
        public static final String SUSPECT = "suspect";
        public static final String T9_RANK = "t9_rank";
        public static final String TAG = "tag";
        public static final String TAG_PINYIN = "tag_pinyin";
        public static final String THUMBNAIL_URL = "thumbnail_url";
        public static final String YELLOW_PAGE_NAME = "yellow_page_name";
        public static final String YELLOW_PAGE_NAME_PINYIN = "yellow_page_name_pinyin";
        public static final String YID = "yid";
    }

    public interface Profile {
        public static final String CONTENT = "content";
        public static final Uri CONTENT_URI = Uri.parse("content://miui.yellowpage//profile");
        public static final String DIRECTORY = "profile";
        public static final String UPDATE_SUFFIX = "update";
    }

    public interface Provider {
        public static final Uri CONTENT_URI = Uri.parse("content://miui.yellowpage/provider");
        public static final String DIRECTORY = "provider";
        public static final String ICON = "icon";
        public static final String ICON_BIG = "icon_big";
        public static final String ICON_URL = "icon_url";
        public static final String NAME = "name";
        public static final String PID = "pid";
        public static final int PID_DEFAULT = 0;
        public static final String PNAME_DEFAULT = "MIUI";
    }

    public interface Search {
        public static final String CONTENT = "content";
        public static final Uri CONTENT_UPDATE_LOCATION_URI = Uri.parse("content://miui.yellowpage/search_location_update");
        public static final Uri CONTENT_URI = Uri.parse("content://miui.yellowpage/search");
        public static final String DIRECTORY = "search";
        public static final String DIRECTORY_LOCATION_UPDATE = "search_location_update";
        public static final String LOCAL_SEARCH = "local";
        public static final String REMOTE_SEARCH = "remote";
        public static final String RESULT_STATE = "result_state";
        public static final int RESULT_STATE_NETWORK_ERROR = 3;
        public static final int RESULT_STATE_OK_CONTINUE = 1;
        public static final int RESULT_STATE_OK_FINISH = 2;
        public static final int RESULT_STATE_SERVICE_ERROR = 4;
        public static final int RESULT_STATE_UNDEFINED = 0;
        public static final int RESULT_STATE_UNKNOWN_ERROR = 5;
        public static final String RESULT_TYPE = "type";
        public static final int RESULT_TYPE_SERVICE = 1;
        public static final int RESULT_TYPE_YELLOW_PAGE = 0;
        public static final String SUGGESTION = "suggestion";
        public static final String TIPS = "tips";
        public static final String UPDATE_SUFFIX = "update";
    }

    public interface Settings {
        public static final Uri CONTENT_URI = Uri.parse("content://miui.yellowpage/settings");
        public static final String DIRECTORY = "settings";
        public static final String NEVER_REMIND_ENABLE_ANTISPAM = "never_remind_user_enable_antispam";
        public static final String ONLINE_FRAUD_ENABLE = "online_fraud_enable";
        public static final String ONLINE_IDENTIFY_ENABLE = "online_identify_enable";
        public static final String REMIND_USER_SUSPECT_NUMBER = "remind_user_suspect_number";
        public static final String YELLOWPAGE_AVAILABLE = "yellowpage_available";
    }

    public interface Statistic {
        public static final Uri CONTENT_URI = Uri.parse("content://miui.yellowpage/statistic");
        public static final String DATA = "data";
        public static final String DATE = "date";
        public static final String DIRECTORY = "statistic";
        public static final String DIRECTORY_CLICK_MODULE_ITEM = "click_module_item";
        public static final String DIRECTORY_CLICK_NAVIGATION_ITEM = "click_navigation_item";
        public static final String DIRECTORY_CLICK_SEARCH_ITEM = "click_search_item";
        public static final String DIRECTORY_CLICK_YELLOW_PAGE = "click_yellowpage";
        public static final String DIRECTORY_LOG_EVENT = "log_event";
        public static final String DIRECTORY_UPLOAD_DATA = "upload_data";
        public static final String DIRECTORY_VIEW_NORMAL_DISPLAY = "view_normal_display";
        public static final String DIRECTORY_VIEW_YELLOWPAGE = "view_yellowpage";
        public static final String DIRECTORY_VIEW_YELLOWPAGE_IN_PHONE_CALL = "view_yp_in_phone_call";
        public static final String ID = "_id";
    }

    public interface T9Lookup {
        public static final Uri CONTENT_URI = Uri.parse("content://miui.yellowpage//t9_lookup");
        public static final String CREDIT_IMG = "credit_img";
        public static final String DIRECTORY = "t9_lookup";
        public static final String NAME = "name";
        public static final String NUMBER = "number";
        public static final String PINYIN = "pinyin";
        public static final String T9_RANK = "t9_rank";
        public static final String TAG = "tag";
        public static final String YID = "yid";
    }

    public interface UserArea {
        public static final String AREA_CODE = "area_code";
        public static final Uri CONTENT_URI = Uri.parse("content://miui.yellowpage/user_area");
        public static final String DIRECTORY = "user_area";
    }

    public interface WebResource {
        public static final Uri CONTENT_URI = Uri.parse("content://miui.yellowpage//web_res");
        public static final String DIRECTORY = "web_res";
    }

    public interface YellowPage {
        public static final String CONTENT = "content";
        public static final Uri CONTENT_URI = Uri.parse("content://miui.yellowpage/yellow_page");
        public static final Uri CONTENT_URI_ALL = Uri.parse("content://miui.yellowpage//yellow_page_all");
        public static final String DIRECTORY = "yellow_page";
        public static final String DIRECTORY_ALL = "yellow_page_all";
        public static final String FAVORITE = "favorite";
        public static final String HOT_CATEGORY_ID = "hotCatId";
        public static final String HOT_SORT = "hotSort";
        public static final String LAST_USE_TIME = "last_use_time";
        public static final String MIID = "miid";
        public static final String SUBSCRIBE_STATUS = "subscribe_stats";
        public static final int SUBSCRIBE_STATUS_NORMAL = 1;
        public static final int SUBSCRIBE_STATUS_TO_SUBSCRIBE = 2;
        public static final int SUBSCRIBE_STATUS_TO_UNSUBSCIRBE = 3;
        public static final int SUBSCRIBE_STATUS_UNSUBSCRIBED = 4;
        public static final String TYPE = "type";
        public static final int TYPE_CLOUD = 3;
        public static final int TYPE_FREQUENT = 1;
        public static final int TYPE_NORMAL = 2;
        public static final String UPDATE_TIME = "update_time";
        public static final String YID = "yid";
    }

    public static final class HttpRequest {
        public static final String CONTENT = "content";
        public static final String PARAMS = "params";
        public static final String PARAM_INVOKE_REQUEST_LOCAL = "invoke_request_local";
        public static final String PARAM_INVOKE_REQUEST_SERVER = "invoke_request_server";
        public static final String PARAM_METHOD = "method";
        public static final String PARAM_SET_ATTACH_LOCATION = "set_attach_location";
        public static final String PARAM_SET_ATTACH_USER_INFO = "set_attach_user_info";
        public static final String PARAM_SET_REQUEST_CACHE = "set_request_cache";
        public static final String PARAM_SET_REQUEST_SERVER = "set_request_server";
        public static final String PARAM_SET_REQUEST_YELLOWPAGE = "set_request_yellowpage";
        public static final String PARAM_URL = "url";

        public static final class Helper {
            private Context mContext;
            private String mMethod = AuthHttpRequest.METHOD_GET;
            private Map<String, String> mParams;
            private boolean mRequestCache;
            private boolean mRequestServer;
            private boolean mRequestYellowPage;
            private boolean mRequireLocId;
            private boolean mRequireLogin;
            private String mUrl;

            public Helper(Context context, String url) {
                this.mContext = context;
                this.mUrl = url;
                this.mRequestCache = true;
                this.mRequestServer = true;
                this.mRequireLogin = false;
                this.mRequireLocId = false;
                this.mRequestYellowPage = false;
                this.mParams = new HashMap();
            }

            public Helper setRequireLogin(boolean requireLogin) {
                this.mRequireLogin = requireLogin;
                return this;
            }

            public Helper setRequireLocId(boolean requireLocId) {
                this.mRequireLocId = requireLocId;
                return this;
            }

            public Helper addParam(String key, String value) {
                this.mParams.put(key, value);
                return this;
            }

            public Helper setRequestCache(boolean request) {
                this.mRequestCache = request;
                return this;
            }

            public Helper setRequestServer(boolean request) {
                this.mRequestServer = request;
                return this;
            }

            public Helper setRequestYellowPage(boolean request) {
                this.mRequestYellowPage = request;
                return this;
            }

            private Bundle constructParams() {
                Bundle bundle = new Bundle();
                bundle.putString(HttpRequest.PARAM_METHOD, this.mMethod);
                bundle.putString("url", this.mUrl);
                bundle.putBoolean(HttpRequest.PARAM_SET_ATTACH_LOCATION, this.mRequireLocId);
                bundle.putBoolean(HttpRequest.PARAM_SET_ATTACH_USER_INFO, this.mRequireLogin);
                bundle.putBoolean(HttpRequest.PARAM_SET_REQUEST_CACHE, this.mRequestCache);
                bundle.putBoolean(HttpRequest.PARAM_SET_REQUEST_SERVER, this.mRequestServer);
                bundle.putBoolean(HttpRequest.PARAM_SET_REQUEST_YELLOWPAGE, this.mRequestYellowPage);
                for (String key : this.mParams.keySet()) {
                    bundle.putString(key, this.mParams.get(key));
                }
                return bundle;
            }

            private String getResult(Bundle params) {
                return InvocationHandler.invoke(this.mContext, Method.REQUEST_HTTP, (String) null, params).getString("data");
            }

            public String requestServer() {
                Bundle bundle = constructParams();
                bundle.putBoolean(HttpRequest.PARAM_INVOKE_REQUEST_SERVER, true);
                return getResult(bundle);
            }

            public String requestLocal() {
                Bundle bundle = constructParams();
                bundle.putBoolean(HttpRequest.PARAM_INVOKE_REQUEST_LOCAL, true);
                return getResult(bundle);
            }
        }
    }
}
