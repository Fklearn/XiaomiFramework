package miui.vip;

import android.content.Context;
import android.content.Intent;
import com.miui.internal.vip.VipConstants;
import com.miui.internal.vip.VipServiceClient;
import com.miui.internal.vip.utils.Utils;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import miui.util.AppConstants;

public class VipService {
    public static final int ACCESS_DENIED = 1003;
    public static final String ACTION_VIP_ACHIEVEMENTS = "com.xiaomi.vip.action.VIP_ACHIEVEMENT_LIST";
    public static final String ACTION_VIP_AWARD = "com.xiaomi.vip.action.VIP_AWARD_LIST";
    public static final String ACTION_VIP_LEVEL_LIST = "com.xiaomi.vip.action.VIP_LEVEL_LIST";
    public static final String ACTION_VIP_TASK = "com.xiaomi.vip.action.VIP_TASK_LIST";
    public static final int AUTH_FAILURE = 1005;
    public static final int CIPHER_ERROR = 1002;
    public static final int DATA_FAILURE = 1006;
    public static final String EVENT_ERR_MSG = "VIP_EVENT_MSG";
    public static final String EVENT_RESULT = "VIP_EVENT_RESULT";
    public static final String EVENT_STATE = "VIP_EVENT_STATE";
    public static final String EVENT_TYPE = "VIP_EVENT_TYPE";
    public static final int INVALID_PARAMS = 10000;
    public static final int INVALID_RESPONSE = 1004;
    public static final int IO_ERROR = 1001;
    public static final int NOT_SUPPORT = 17777;
    public static final String REQUEST_ID = "VIP_REQUEST_ID";
    public static final String REQ_VIP_ICON = "request_vip_icon://";
    public static final int RETRY = 70000;
    public static final int SERVER_ERROR = 80000;
    public static final int SUCCESS = 0;
    public static final int TYPE_ACHIEVEMENTS = 16;
    public static final int TYPE_BANNERS = 64;
    public static final int TYPE_CONNECTED = 8;
    public static final int TYPE_DISCONNECTED = 9;
    public static final int TYPE_PHONE_LEVEL = 4;
    public static final int TYPE_USER_INFO = 1;
    public static final int TYPE_VIP_ICON = 32;
    public static final int UNKNOWN = 90000;
    public static final int USER_ERROR_BEGIN = 13333;
    public static final int USER_ERROR_END = 13433;
    public static final int USER_ERROR_TASK_ALREADY_FINISHED = 13337;
    public static final int USER_ERROR_TASK_ALREADY_STARTED = 13334;
    public static final int USER_ERROR_TASK_NOT_FINISHED = 13336;
    public static final int USER_ERROR_TASK_NOT_STARTED = 13335;
    public static final String VIP_EVENT_ACTION = "com.xiaomi.vip.action.VIP_EVENT";
    public static final String VIP_LEVEL_ICON = "level_icon";
    public static final int VIP_SERVICE_FAILURE = 1000;
    static final ExecutorService sThread = Executors.newSingleThreadExecutor();
    final VipServiceClient mService;

    public static void notifyEvent(Context ctx, String content) {
        Intent intent = new Intent(VipConstants.ACTION_EVENT);
        intent.setPackage(VipConstants.VIP_PACKAGE);
        intent.putExtra(VipConstants.PACKAGE_NAME, ctx.getPackageName());
        intent.putExtra(VipConstants.EVENT_VALUE, content);
        Utils.log("VipService.notifyEvent, packageName = %s, content = %s", ctx.getPackageName(), content);
        ctx.sendBroadcast(intent);
    }

    static class HOLDER {
        static final VipService service;

        HOLDER() {
        }

        static {
            VipService instance = null;
            try {
                instance = new VipService(AppConstants.getCurrentApplication().getApplicationContext());
            } catch (Throwable e) {
                Utils.log("HOLDER.init failed, %s", e);
            }
            service = instance;
        }
    }

    public static VipService instance() {
        return HOLDER.service;
    }

    VipService(Context ctx) {
        this.mService = new VipServiceClient(ctx);
    }

    public static void startVipActivity(Context ctx, String action) {
        Utils.startActivity(ctx, action, VipConstants.VIP_PACKAGE);
    }

    public void connect(final QueryCallback listener) {
        sThread.execute(new Runnable() {
            public void run() {
                VipService.this.mService.connect(listener);
            }
        });
    }

    public void disconnect(QueryCallback listener) {
        this.mService.disconnect(listener);
    }

    public boolean isConnected() {
        return this.mService.isConnected();
    }

    public void queryUserVipInfo() {
        sThread.execute(new Runnable() {
            public void run() {
                VipService.this.mService.queryUserVipInfo();
            }
        });
    }

    public void queryAchievements() {
        sThread.execute(new Runnable() {
            public void run() {
                VipService.this.mService.queryAchievements();
            }
        });
    }

    public void queryBanners() {
        sThread.execute(new Runnable() {
            public void run() {
                VipService.this.mService.queryBanners();
            }
        });
    }

    @Deprecated
    public void queryPhoneLevel(List<String> list) {
    }

    @Deprecated
    public void sendMessage(int type, String msg) {
    }

    /* access modifiers changed from: package-private */
    public void sendStatistic(final String data) {
        sThread.execute(new Runnable() {
            public void run() {
                VipService.this.mService.sendStatistic(data);
            }
        });
    }
}
