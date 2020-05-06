package miui.cloud.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import b.d.b.a.a.a;
import java.util.ArrayList;
import java.util.Map;
import miui.accounts.ExtraAccountManager;
import miui.cloud.Constants;
import org.json.JSONException;
import org.json.JSONObject;

public class MiCloudStatusInfo {
    private static final String TAG = "MiCloudStatusInfo";
    private QuotaInfo mQuotaInfo;
    private String mUserId;
    private boolean mVipEnable;

    public class ItemInfo {
        private String mLocalizedName;
        private String mName;
        private long mUsed;

        public ItemInfo(String str, String str2, long j) {
            this.mName = str;
            this.mLocalizedName = str2;
            this.mUsed = j;
        }

        public String getLocalizedName() {
            return this.mLocalizedName;
        }

        public String getName() {
            return this.mName;
        }

        public long getUsed() {
            return this.mUsed;
        }

        public String toString() {
            return "ItemInfo{mName=" + this.mName + ", mLocalizedName=" + this.mLocalizedName + ", mUsed='" + this.mUsed + '}';
        }
    }

    public class QuotaInfo {
        public static final String WARN_FULL = "full";
        public static final String WARN_LOW_PERCENT = "low_percent";
        public static final String WARN_NONE = "none";
        private ArrayList<ItemInfo> mItemInfoList = new ArrayList<>();
        private long mTotal;
        private long mUsed;
        private String mWarn;
        private long mYearlyPackageCreateTime;
        private long mYearlyPackageExpireTime;
        private long mYearlyPackageSize;
        private String mYearlyPackageType;

        public QuotaInfo(long j, long j2, String str, String str2, long j3, long j4, long j5) {
            this.mTotal = j;
            this.mUsed = j2;
            this.mWarn = str;
            this.mYearlyPackageType = str2;
            this.mYearlyPackageSize = j3;
            this.mYearlyPackageCreateTime = j4;
            this.mYearlyPackageExpireTime = j5;
        }

        public void addItemInfo(ItemInfo itemInfo) {
            this.mItemInfoList.add(itemInfo);
        }

        public ArrayList<ItemInfo> getItemInfoList() {
            return this.mItemInfoList;
        }

        public long getTotal() {
            return this.mTotal;
        }

        public long getUsed() {
            return this.mUsed;
        }

        public String getWarn() {
            return this.mWarn;
        }

        public long getYearlyPackageCreateTime() {
            return this.mYearlyPackageCreateTime;
        }

        public long getYearlyPackageExpireTime() {
            return this.mYearlyPackageExpireTime;
        }

        public long getYearlyPackageSize() {
            return this.mYearlyPackageSize;
        }

        public String getYearlyPackageType() {
            return this.mYearlyPackageType;
        }

        public boolean isSpaceFull() {
            return WARN_FULL.equals(getWarn());
        }

        public boolean isSpaceLowPercent() {
            return WARN_LOW_PERCENT.equals(getWarn());
        }

        public String toString() {
            return "QuotaInfo{mTotal=" + this.mTotal + ", mUsed=" + this.mUsed + ", mWarn='" + this.mWarn + '\'' + ", mYearlyPackageType='" + this.mYearlyPackageType + '\'' + ", mYearlyPackageSize=" + this.mYearlyPackageSize + ", mYearlyPackageCreateTime=" + this.mYearlyPackageCreateTime + ", mYearlyPackageExpireTime=" + this.mYearlyPackageExpireTime + ", mItemInfoList=" + this.mItemInfoList + '}';
        }
    }

    public MiCloudStatusInfo(String str) {
        this.mUserId = str;
    }

    public static MiCloudStatusInfo fromProviderOrNull(Context context, Account account) {
        Cursor query = context.getContentResolver().query(Uri.parse("content://com.miui.micloud/status_info"), (String[]) null, (String) null, (String[]) null, (String) null);
        if (query != null) {
            try {
                query.moveToFirst();
                String string = query.getString(query.getColumnIndex("column_status_info"));
                String string2 = query.getString(query.getColumnIndex("column_status_info_user_id"));
                if (!TextUtils.isEmpty(string) && TextUtils.equals(string2, account.name)) {
                    MiCloudStatusInfo miCloudStatusInfo = new MiCloudStatusInfo(account.name);
                    miCloudStatusInfo.parseQuotaString(string);
                    return miCloudStatusInfo;
                }
            } finally {
                if (query != null) {
                    query.close();
                }
            }
        }
        if (query != null) {
            query.close();
        }
        return null;
    }

    public static MiCloudStatusInfo fromUserData(Context context) {
        AccountManager accountManager = AccountManager.get(context);
        Account xiaomiAccount = ExtraAccountManager.getXiaomiAccount(context);
        if (xiaomiAccount == null) {
            return null;
        }
        String userData = accountManager.getUserData(xiaomiAccount, Constants.UserData.EXTRA_MICLOUD_STATUS_INFO_QUOTA);
        MiCloudStatusInfo miCloudStatusInfo = new MiCloudStatusInfo(xiaomiAccount.name);
        miCloudStatusInfo.parseQuotaString(userData);
        QuotaInfo quotaInfo = miCloudStatusInfo.getQuotaInfo();
        if (quotaInfo == null || quotaInfo.getWarn() == null) {
            Log.w(TAG, "deserialize failed");
            accountManager.setUserData(xiaomiAccount, Constants.UserData.EXTRA_MICLOUD_STATUS_INFO_QUOTA, "");
        }
        return miCloudStatusInfo;
    }

    private ItemInfo mapToItemInfo(String str, Map map) {
        Object obj = map.get("localized_name");
        String str2 = obj instanceof String ? (String) obj : "";
        Object obj2 = map.get("used");
        return new ItemInfo(str, str2, obj2 instanceof Integer ? (long) ((Integer) obj2).intValue() : obj2 instanceof Long ? ((Long) obj2).longValue() : 0);
    }

    /* JADX WARNING: Removed duplicated region for block: B:14:0x0042  */
    /* JADX WARNING: Removed duplicated region for block: B:15:0x0046  */
    /* JADX WARNING: Removed duplicated region for block: B:18:0x0051  */
    /* JADX WARNING: Removed duplicated region for block: B:19:0x0055  */
    /* JADX WARNING: Removed duplicated region for block: B:22:0x0060  */
    /* JADX WARNING: Removed duplicated region for block: B:23:0x0068  */
    /* JADX WARNING: Removed duplicated region for block: B:26:0x0073  */
    /* JADX WARNING: Removed duplicated region for block: B:27:0x007b  */
    /* JADX WARNING: Removed duplicated region for block: B:30:0x0086  */
    /* JADX WARNING: Removed duplicated region for block: B:33:0x00a0  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private miui.cloud.sync.MiCloudStatusInfo.QuotaInfo mapToQuotaInfo(java.util.Map r20) {
        /*
            r19 = this;
            r0 = r20
            java.lang.String r1 = "total"
            java.lang.Object r1 = r0.get(r1)
            boolean r2 = r1 instanceof java.lang.Long
            r3 = 0
            if (r2 == 0) goto L_0x0016
            java.lang.Long r1 = (java.lang.Long) r1
            long r1 = r1.longValue()
            r7 = r1
            goto L_0x0017
        L_0x0016:
            r7 = r3
        L_0x0017:
            java.lang.String r1 = "used"
            java.lang.Object r1 = r0.get(r1)
            boolean r2 = r1 instanceof java.lang.Integer
            if (r2 == 0) goto L_0x002a
            java.lang.Integer r1 = (java.lang.Integer) r1
            int r1 = r1.intValue()
            long r1 = (long) r1
        L_0x0028:
            r9 = r1
            goto L_0x0036
        L_0x002a:
            boolean r2 = r1 instanceof java.lang.Long
            if (r2 == 0) goto L_0x0035
            java.lang.Long r1 = (java.lang.Long) r1
            long r1 = r1.longValue()
            goto L_0x0028
        L_0x0035:
            r9 = r3
        L_0x0036:
            java.lang.String r1 = "warn"
            java.lang.Object r1 = r0.get(r1)
            boolean r2 = r1 instanceof java.lang.String
            java.lang.String r5 = ""
            if (r2 == 0) goto L_0x0046
            java.lang.String r1 = (java.lang.String) r1
            r11 = r1
            goto L_0x0047
        L_0x0046:
            r11 = r5
        L_0x0047:
            java.lang.String r1 = "yearlyPackageType"
            java.lang.Object r1 = r0.get(r1)
            boolean r2 = r1 instanceof java.lang.String
            if (r2 == 0) goto L_0x0055
            java.lang.String r1 = (java.lang.String) r1
            r12 = r1
            goto L_0x0056
        L_0x0055:
            r12 = r5
        L_0x0056:
            java.lang.String r1 = "yearlyPackageSize"
            java.lang.Object r1 = r0.get(r1)
            boolean r2 = r1 instanceof java.lang.Long
            if (r2 == 0) goto L_0x0068
            java.lang.Long r1 = (java.lang.Long) r1
            long r1 = r1.longValue()
            r13 = r1
            goto L_0x0069
        L_0x0068:
            r13 = r3
        L_0x0069:
            java.lang.String r1 = "yearlyPackageCreateTime"
            java.lang.Object r1 = r0.get(r1)
            boolean r2 = r1 instanceof java.lang.Long
            if (r2 == 0) goto L_0x007b
            java.lang.Long r1 = (java.lang.Long) r1
            long r1 = r1.longValue()
            r15 = r1
            goto L_0x007c
        L_0x007b:
            r15 = r3
        L_0x007c:
            java.lang.String r1 = "yearlyPackageExpireTime"
            java.lang.Object r1 = r0.get(r1)
            boolean r2 = r1 instanceof java.lang.Long
            if (r2 == 0) goto L_0x008c
            java.lang.Long r1 = (java.lang.Long) r1
            long r3 = r1.longValue()
        L_0x008c:
            r17 = r3
            miui.cloud.sync.MiCloudStatusInfo$QuotaInfo r1 = new miui.cloud.sync.MiCloudStatusInfo$QuotaInfo
            r5 = r1
            r6 = r19
            r5.<init>(r7, r9, r11, r12, r13, r15, r17)
            java.lang.String r2 = "items"
            java.lang.Object r0 = r0.get(r2)
            boolean r2 = r0 instanceof java.util.Map
            if (r2 == 0) goto L_0x00c6
            java.util.Map r0 = (java.util.Map) r0
            java.util.Set r2 = r0.keySet()
            java.util.Iterator r2 = r2.iterator()
        L_0x00aa:
            boolean r3 = r2.hasNext()
            if (r3 == 0) goto L_0x00c6
            java.lang.Object r3 = r2.next()
            java.lang.String r3 = (java.lang.String) r3
            java.lang.Object r4 = r0.get(r3)
            java.util.Map r4 = (java.util.Map) r4
            r5 = r19
            miui.cloud.sync.MiCloudStatusInfo$ItemInfo r3 = r5.mapToItemInfo(r3, r4)
            r1.addItemInfo(r3)
            goto L_0x00aa
        L_0x00c6:
            r5 = r19
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: miui.cloud.sync.MiCloudStatusInfo.mapToQuotaInfo(java.util.Map):miui.cloud.sync.MiCloudStatusInfo$QuotaInfo");
    }

    public QuotaInfo getQuotaInfo() {
        return this.mQuotaInfo;
    }

    public String getUserId() {
        return this.mUserId;
    }

    public boolean isVIPAvailable() {
        return this.mVipEnable;
    }

    public void parseMap(Map map) {
        Object obj = map.get("quota");
        if (obj instanceof Map) {
            this.mQuotaInfo = mapToQuotaInfo((Map) obj);
        }
        Object obj2 = map.get("VIPAvailable");
        if (obj2 instanceof Boolean) {
            this.mVipEnable = ((Boolean) obj2).booleanValue();
        }
    }

    public void parseQuotaString(String str) {
        if (TextUtils.isEmpty(str)) {
            Log.e(TAG, "parseQuotaString() quota is empty.");
            this.mQuotaInfo = null;
            return;
        }
        try {
            this.mQuotaInfo = a.b(this, new JSONObject(str));
        } catch (JSONException unused) {
            Log.e(TAG, "catch JSONException in parseQuotaString()");
            this.mQuotaInfo = null;
        }
    }

    public String parseToQuotaInfo() {
        QuotaInfo quotaInfo = this.mQuotaInfo;
        if (quotaInfo != null) {
            return a.a(quotaInfo).toString();
        }
        Log.e(TAG, "parseToQuotaInfo() mQuotaInfo is null.");
        return "";
    }
}
