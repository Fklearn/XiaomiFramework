package miui.mipub;

@Deprecated
public final class MipubConstants {

    @Deprecated
    public interface FollowState {
        public static final int MIPUB_FOLLOWED = 1;
        public static final int MIPUB_FORBID_FOLLOW = -1;
        public static final int MIPUB_UNFOLLOWED = 0;
    }

    @Deprecated
    public interface Intent {
        public static final String ACTION_LOGIN_DEVICE_CHANGED = "com.miui.yellowpage.action.LOGIN_DEVICE_CHANGED";
        public static final String ACTION_ROUTE_HMS_WEB_MESSAGE = "com.miui.yellowpage.action.ROUTE_HMS_WEB_MSG";
        public static final String ACTION_VIEW_MIXIN = "com.miui.mipub.action.VIEW_MIPUB_MSG";
        public static final String ACTION_YELLOWPAGE_LOGIN = "com.miui.yellowpage.action.YELLOWPAGE_LOGIN";
        public static final int DEVICE_LOGIN_STATE_LOGIN = 1;
        public static final int DEVICE_LOGIN_STATE_LOGOUT = 0;
        public static final String EXTRA_DEVICE_LOGIN_STATE = "device_login_state";
        public static final String EXTRA_MIPUB_MENU = "menu";
        public static final String EXTRA_MIPUB_MENU_ORIGIN = "menu_origin";
        public static final String EXTRA_MIPUB_MESSAGE = "message";
        public static final String EXTRA_MIPUB_MIID = "mi_id";
    }
}
