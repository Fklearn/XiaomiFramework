package miui.cloud.util;

import android.content.Context;
import miui.cloud.sync.MiCloudStatusInfo;

public class SyncStatusHelper {
    public static final int STATUS_ABNORMAL = -1;
    public static final int STATUS_FULL = 2;
    public static final int STATUS_LOW = 1;
    public static final int STATUS_NORMAL = 0;

    public static int getSyncStatus(Context context) {
        MiCloudStatusInfo.QuotaInfo quotaInfo;
        MiCloudStatusInfo fromUserData = MiCloudStatusInfo.fromUserData(context);
        if (fromUserData == null || (quotaInfo = fromUserData.getQuotaInfo()) == null) {
            return -1;
        }
        if (quotaInfo.isSpaceFull()) {
            return 2;
        }
        return quotaInfo.isSpaceLowPercent() ? 1 : 0;
    }
}
