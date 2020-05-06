package b.d.b.a.a;

import miui.cloud.sync.MiCloudStatusInfo;
import org.json.JSONArray;
import org.json.JSONObject;

public class a {
    public static MiCloudStatusInfo.ItemInfo a(MiCloudStatusInfo miCloudStatusInfo, JSONObject jSONObject) {
        if (jSONObject == null) {
            return null;
        }
        String optString = jSONObject.optString("Name");
        String optString2 = jSONObject.optString("LocalizedName");
        long optLong = jSONObject.optLong("Used");
        miCloudStatusInfo.getClass();
        return new MiCloudStatusInfo.ItemInfo(optString, optString2, optLong);
    }

    public static JSONObject a(MiCloudStatusInfo.ItemInfo itemInfo) {
        JSONObject jSONObject = new JSONObject();
        jSONObject.put("Name", itemInfo.getName());
        jSONObject.put("Used", itemInfo.getUsed());
        jSONObject.put("LocalizedName", itemInfo.getLocalizedName());
        return jSONObject;
    }

    public static JSONObject a(MiCloudStatusInfo.QuotaInfo quotaInfo) {
        JSONObject jSONObject = new JSONObject();
        jSONObject.put("Total", quotaInfo.getTotal());
        jSONObject.put("Used", quotaInfo.getUsed());
        jSONObject.put("Warn", quotaInfo.getWarn());
        jSONObject.put("YearlyPackageType", quotaInfo.getYearlyPackageType());
        jSONObject.put("YearlyPackageSize", quotaInfo.getYearlyPackageSize());
        jSONObject.put("YearlyPackageCreateTime", quotaInfo.getYearlyPackageCreateTime());
        jSONObject.put("YearlyPackageExpireTime", quotaInfo.getYearlyPackageExpireTime());
        JSONArray jSONArray = new JSONArray();
        for (MiCloudStatusInfo.ItemInfo a2 : quotaInfo.getItemInfoList()) {
            jSONArray.put(a(a2));
        }
        jSONObject.put("ItemInfoList", jSONArray);
        return jSONObject;
    }

    public static MiCloudStatusInfo.QuotaInfo b(MiCloudStatusInfo miCloudStatusInfo, JSONObject jSONObject) {
        JSONObject jSONObject2 = jSONObject;
        long optLong = jSONObject2.optLong("Total");
        long optLong2 = jSONObject2.optLong("Used");
        String optString = jSONObject2.optString("Warn");
        String optString2 = jSONObject2.optString("YearlyPackageType");
        long optLong3 = jSONObject2.optLong("YearlyPackageSize");
        long optLong4 = jSONObject2.optLong("YearlyPackageCreateTime");
        long optLong5 = jSONObject2.optLong("YearlyPackageExpireTime");
        miCloudStatusInfo.getClass();
        MiCloudStatusInfo.QuotaInfo quotaInfo = new MiCloudStatusInfo.QuotaInfo(optLong, optLong2, optString, optString2, optLong3, optLong4, optLong5);
        JSONArray optJSONArray = jSONObject2.optJSONArray("ItemInfoList");
        if (optJSONArray != null) {
            for (int i = 0; i < optJSONArray.length(); i++) {
                MiCloudStatusInfo.ItemInfo a2 = a(miCloudStatusInfo, optJSONArray.optJSONObject(i));
                if (a2 != null) {
                    quotaInfo.addItemInfo(a2);
                }
            }
        }
        return quotaInfo;
    }
}
