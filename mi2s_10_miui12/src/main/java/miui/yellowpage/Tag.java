package miui.yellowpage;

import java.util.ArrayList;
import java.util.List;

public interface Tag {

    public interface Intent {
        public static final String ACTION_PICK = "com.miui.yellowpage.action.PICK";
        public static final String ACTION_VIEW_YELLOWPAGE = "com.miui.yellowpage.action.VIEW";
        public static final String ACTION_WEB_RESOURCES_UPDATED = "com.miui.yellowpage.web_page_resources_updated";
        public static final String EXTRA_MODULE_ID = "module_id";
        public static final String EXTRA_NOTICE_TYPE = "notice_type";
        public static final String EXTRA_PARAMS = "com.miui.yellowpage.extra.params";
        public static final String EXTRA_PICKER_TARGET = "picker_target";
        public static final String EXTRA_PICKER_TITLE = "picker_title";
        public static final String EXTRA_RESULT_BACKEND_DATA = "result_backend_data";
        public static final String EXTRA_RESULT_PRESENTATION = "result_presentation";
        public static final String EXTRA_SHOW_DEFAULT_TITLE = "show_default_title";
        public static final String EXTRA_SOURCE = "source";
        public static final String EXTRA_SOURCE_MODULE_ID = "mid";
        public static final String EXTRA_WEB_TITLE = "web_title";
        public static final String EXTRA_WEB_URL = "web_url";
        public static final String EXTRA_YELLOWPAGE_ID = "com.miui.yellowpage.extra.yid";
        public static final String EXTRA_YELLOW_PAGE_ID = "com.miui.yellowpage.extra.yid";
    }

    public interface TagPhone {
        public static final String ATD_CAT_ID = "atdCatId";
        public static final String BUILTIN = "buildIn";
        public static final int BUILTIN_FALSE = 0;
        public static final String BUILTIN_SYNC_ATD_DATA = "atd_data";
        public static final String BUILTIN_SYNC_HAS_MORE = "has_more";
        public static final int BUILTIN_TRUE = 1;
        public static final String CATEGORY_ID = "catId";
        public static final String CATEGORY_NAME = "catTitle";
        public static final String DELETED = "status";
        public static final int DELETED_FALSE = 1;
        public static final int DELETED_TRUE = 0;
        public static final String DURATION = "duration";
        public static final String FEEDBACK_COUNT = "feedbackCount";
        public static final String FLAG = "flag";
        public static final String IMEI = "device";
        public static final String KEY = "atd";
        public static final String MARKED_COUNT = "count";
        public static final String MARKED_NUMBER_FEEDBACK = "feedback";
        public static final String NORMALIZED_NUMBER = "norPhone";
        public static final String NUMBER = "phone";
        public static final String NUMBER_ALTERNATIVE = "markedNo";
        public static final String NUMBER_NORMALIZED_ALTERNATIVE = "norMarkedNo";
        public static final String PINYIN = "contactName_py";
        public static final String PROVIDER = "provider";
        public static final String T9_RANK = "t9rank";
        public static final String Tag = "contactName";
        public static final String VERSION = "version";
        public static final String VISIBLE = "hide";
        public static final int VISIBLE_FALSE = 1;
        public static final int VISIBLE_TRUE = 0;
    }

    public interface TagPreference {
        public static final String PREF_FORMAT = "com.miui.yellowpage_preferences.%s";
        public static final String SHOW_ROLLING_ADS = "pref_show_rolling_ads";
        public static final String SHOW_USER_NOTICE_MIPUB_UPLOAD = "pref_show_user_mipub_upload";
        public static final String SHOW_USER_NOTICE_UPDATE_YP_ONLINE = "pref_show_user_notice_yp_detail";
    }

    public interface TagSearch {

        public interface GeoInfo {
            public static final String CITY = "city";
            public static final String COUNTRY = "country";
            public static final String DISTRICT = "district";
            public static final String FEATURE = "feature";
            public static final String LOCATION_ID = "locId";
            public static final String PROVINCE = "province";
            public static final String STREET = "street";
            public static final String STREET_NUMBER = "street_number";
        }
    }

    public interface TagServicesData {
        public static final String AD_ON = "adOn";
        public static final String DATA_RECHARGE = "dataRecharge";
        public static final String GROUP_DATA = "data";
        public static final String GROUP_NAME = "name";
        public static final String GROUP_STYLE = "style";
        public static final String IS_MIFAMILY_ENTRANCE = "miFamily";
        public static final String NAV_GROUPS = "nav_groups";
        public static final String PARAM_TYPE = "type";
        public static final String PHONE_RECHAGE = "phoneRecharge";
        public static final String SEARCH_HINT = "searchHint";
        public static final String SERVICE_ACTIONS = "actions";
        public static final String SERVICE_EXTRA_DATA = "extraData";
        public static final String SERVICE_ICON = "icon";
        public static final String SERVICE_IS_HOT = "isHot";
        public static final String SERVICE_IS_NEW = "isNew";
        public static final String SERVICE_IS_PROMOTE = "isPromote";
        public static final String SERVICE_MID = "mid";
        public static final String SERVICE_NAME = "name";
        public static final String SHOW_PHONE_RECHARGE = "showPhoneRecharge";
        public static final int TYPE_AD_OFF = 0;
        public static final int TYPE_AD_ON = 1;
        public static final int TYPE_BANNER = 4;
        public static final int TYPE_NAV = 0;
    }

    public interface TagYellowPage {
        public static final String ADDRESS = "address";
        public static final String ALIAS = "aliasName";
        public static final String AUTH_ICON = "authIcon";
        public static final String AVERAGE_PRICE = "avg_price";
        public static final String BRIEF_INFO = "shInfo";
        public static final String BUILTIN = "buildIn";
        public static final int BUILTIN_FALSE = 0;
        public static final String BUILTIN_SYNC_HAS_MORE = "has_more";
        public static final String BUILTIN_SYNC_IMG_DOMAIN = "image_domain";
        public static final String BUILTIN_SYNC_YP_DATA = "ypdata";
        public static final int BUILTIN_TRUE = 1;
        public static final String CAT_ID = "catId";
        public static final String CREDITIMG = "creditImg";
        public static final String DELETED = "status";
        public static final int DELETED_FALSE = 1;
        public static final int DELETED_TRUE = 0;
        public static final String EXTRA_DATA = "extraData";
        public static final String EXTRA_INFO = "extraInfo";
        public static final String FIRMURL = "firmUrl";
        public static final String HOT = "hot";
        public static final String HOT_CAT_ID = "hotCatId";
        public static final int HOT_FALSE = 0;
        public static final String HOT_SORT = "hotSort";
        public static final int HOT_TRUE = 1;
        public static final String KEY = "yp";
        public static final String LATITUDE = "latitude";
        public static final String LOC_ID = "locId";
        public static final String LONGITUDE = "longitude";
        public static final String MIID = "miid";
        public static final String MI_SUB_ID = "miSubId";
        public static final String NAME = "sName";
        public static final int NOTICE_TYPE_MIPUB_UPLOAD = 1;
        public static final int NOTICE_TYPE_NETWORK_ALLOWED = 0;
        public static final String PHONE = "phone";
        public static final String PHOTO = "photo";
        public static final String PINYIN = "sName_py";
        public static final String PRESET = "buildIn";
        public static final int PRESET_TRUE = 1;
        public static final String PROVIDER = "provider";
        public static final String PROVIDER_LIST = "providerList";
        public static final String RATING = "avg_rating";
        public static final String SLOGAN = "slogan";
        public static final String SOCIAL = "snsInfo";
        public static final String SOURCE_ID = "sourceId";
        public static final String SOURCE_URL = "sourceUrl";
        public static final String SUSPECT = "needTips";
        public static final int SUSPECT_FLASE = 0;
        public static final int SUSPECT_TRUE = 1;
        public static final String THUMBNAIL = "thumbnail";
        public static final String TYPE = "sType";
        public static final int TYPE_BRANCH = 1;
        public static final int TYPE_MASTER = 2;
        public static final int TYPE_NORMAL = 0;
        public static final String URL = "site";
        public static final String VERSION = "max_version";
        public static final String YID = "sid";
    }

    public static final class TagWebService {
        public static final String KEY_GO_HOME = "goHome";
        public static final String KEY_IS_ON_LINE = "isOnLine";
        public static final String KEY_SHOW_TOAST = "showToast";
        private static List<String> sMethodList = new ArrayList();

        public interface CommonResult {
            public static final String RESULT_CODE = "code";
            public static final String RESULT_DATA = "data";
            public static final String RESULT_MESSAGE = "message";
            public static final String RESULT_MSG_ID = "msgid";
            public static final String RESULT_TYPE = "type";
            public static final String RESULT_TYPE_CALLBACK = "callback";
            public static final String RESULT_TYPE_EVENT = "event";
        }

        public interface ContentGetImage {
            public static final String METHOD = "getImage";
            public static final String PARAM_IMAGE_NAME = "name";
            public static final String PARAM_IMAGE_WIDTH = "width";
        }

        public interface ContentGetNavigationData {
            public static final String METHOD = "navigationData";
            public static final String PARAM_CALL_BACK = "callback";
        }

        public interface ContentGetProfileData {
            public static final String METHOD = "profileData";
            public static final String PARAM_CALL_BACK = "callback";
        }

        public interface ContentGetThumbnail {
            public static final String METHOD = "getThumbnail";
            public static final String PARAM_THUMBNAIL_NAME = "name";
        }

        public interface ContentGetYellowPageByCid {
            public static final String METHOD = "getPageByCid";
            public static final String PARAM_CALL_BACK = "callback";
            public static final String PARAM_HOT_CATEGORY_CID = "hotCatId";
        }

        public interface ContentLocalSearch {
            public static final String METHOD = "localSearch";
            public static final String PARAM_CALL_BACK = "callback";
            public static final String PARAM_KEYWORD = "keyword";
        }

        public interface ContentRemoteSearch {
            public static final String METHOD = "remoteSearch";
            public static final String PARAM_CALL_BACK = "callback";
            public static final String PARAM_KEYWORD = "keyword";
            public static final String PARAM_LOCATION_ID = "locationId";
            public static final String PARAM_PAGE = "page";
        }

        public interface RequestLocation {
            public static final String METHOD_GEO_LOCATION = "geolocation";
        }

        private TagWebService() {
        }

        static {
            sMethodList.add(ContentGetYellowPageByCid.METHOD);
            sMethodList.add(ContentGetThumbnail.METHOD);
            sMethodList.add(ContentGetImage.METHOD);
            sMethodList.add(ContentRemoteSearch.METHOD);
            sMethodList.add(ContentLocalSearch.METHOD);
            sMethodList.add(ContentGetNavigationData.METHOD);
            sMethodList.add(ContentGetProfileData.METHOD);
        }

        public static boolean hasContentMethod(String method) {
            return sMethodList.contains(method);
        }
    }
}
