package miui.provider;

import android.net.Uri;

public class VoipContract {
    public static final String ACTIVATE_VOIP_ACTION = "com.miui.voip.action.ACTIVATE_VOIP";
    public static final String ACTIVATE_VOIP_ROUTER_ACTION = "com.miui.voip.action.ACTIVATE_VOIP_ROUTER";
    public static final int AUDIO_CALL_TYPE = 0;
    public static final String AUTHORITY = "com.miui.voip";
    public static final Uri AUTHORITY_URI = Uri.parse("content://com.miui.voip");
    public static final String EXTRA_ACTIVATE_SIM_INDEX = "extra_activate_sim_index";
    public static final String EXTRA_CALL_TYPE = "extra_call_type";
    public static final String EXTRA_CONTACT_ID = "extra_contact_id";
    public static final String EXTRA_CONTACT_NAME = "extra_contact_name";
    public static final String EXTRA_MAKE_VOIP_CALL_FROM = "extra_make_voip_call_from";
    public static final String EXTRA_NUMBERS = "extra_numbers";
    public static final String EXTRA_TURN_ON_VOIP_FROM = "extra_turn_on_voip_from";
    public static final String LOAD_USER_DATA_ACTION = "com.miui.voip.action.LOAD_USER_DATA";
    public static final String LOAD_USER_DATA_ACTION_IMMEDIATELY = "com.miui.voip.action.LOAD_USER_DATA_IMMEDIATELY";
    public static final int MAKE_CALL_FROM_AUTO = 6;
    public static final int MAKE_CALL_FROM_CONTACT_LIST = 4;
    public static final int MAKE_CALL_FROM_SHORTCUT = 5;
    public static final String MAKE_VOIP_CALL_ACTION = "com.miui.voip.action.MAKE_VOIP_CALL";
    public static final int MAKE_VOIP_CALL_FROM_CALL_LOG = 1;
    public static final int MAKE_VOIP_CALL_FROM_CONTACT_DETAIL = 0;
    public static final int MAKE_VOIP_CALL_FROM_MMS = 3;
    public static final int MAKE_VOIP_CALL_FROM_T9 = 2;
    public static final String REACTIVATE_VOIP_ACTION = "com.miui.voip.action.REACTIVATE_VOIP";
    public static final Uri STATUS_URI = Uri.withAppendedPath(AUTHORITY_URI, "status");
    public static final String TURN_OFF_VOIP_ACTION = "com.miui.voip.action.TURN_OFF_VOIP";
    public static final String TURN_ON_VOIP_ACTION = "com.miui.voip.action.TURN_ON_VOIP";
    public static final int TURN_ON_VOIP_FROM_ACCOUNT = 4;
    public static final int TURN_ON_VOIP_FROM_CONTACTS = 5;
    public static final int TURN_ON_VOIP_FROM_GUIDE = 0;
    public static final int TURN_ON_VOIP_FROM_MMS = 2;
    public static final int TURN_ON_VOIP_FROM_NOTIFICATION = 3;
    public static final int TURN_ON_VOIP_FROM_SETTING = 1;
    public static final int TURN_ON_VOIP_FROM_SHORTCUT = 6;
    public static final int TURN_ON_VOIP_FROM_WEB = 7;
    public static final Uri USERS_URI = Uri.withAppendedPath(AUTHORITY_URI, "users");
    public static final int VIDEO_CALL_TYPE = 1;
    public static final String VIEW_CONTACTS_ACTION = "com.miui.voip.action.VIEW_CONTACTS";
    public static final String VOIP_CALL_STATE_CHANGE_ACTION = "com.miui.voip.action.CALL_STATE_CHANGED";
    public static final String VOIP_STATUS_CHANGE_ACTION = "com.miui.voip.action.STATUS_CHANGED";

    public interface UsersColumn {
        public static final String ACTIVATED_NUMBER = "activated_number";
        public static final int ACTIVATED_NUMBER_INDEX = 0;
        public static final String[] PROJECTION = {ACTIVATED_NUMBER};
    }

    public interface VoipStatusColumns {
        public static final String ACCOUNT_ACTIVATED = "account_activated";
        public static final int ACCOUNT_ACTIVATED_INDEX = 0;
        public static final String AUDIO_ONLINE = "audio_online";
        public static final int AUDIO_ONLINE_INDEX = 2;
        public static final String AUDIO_SUPPORT = "audio_support";
        public static final int AUDIO_SUPPORT_INDEX = 1;
        public static final String DESCRIPTION = "description";
        public static final int DESCRIPTION_INDEX = 7;
        public static final String INTENT_ACTION = "intent_action";
        public static final int INTENT_ACTION_INDEX = 5;
        public static final String[] PROJECTION = {ACCOUNT_ACTIVATED, AUDIO_SUPPORT, AUDIO_ONLINE, VIDEO_SUPPORT, VIDEO_ONLINE, INTENT_ACTION, "title", "description"};
        public static final String TITLE = "title";
        public static final int TITLE_INDEX = 6;
        public static final String VIDEO_ONLINE = "video_online";
        public static final int VIDEO_ONLINE_INDEX = 4;
        public static final String VIDEO_SUPPORT = "video_support";
        public static final int VIDEO_SUPPORT_INDEX = 3;
    }
}
