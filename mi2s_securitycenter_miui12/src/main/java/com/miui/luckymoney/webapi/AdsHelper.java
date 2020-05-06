package com.miui.luckymoney.webapi;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.Log;
import com.google.android.exoplayer2.util.MimeTypes;
import com.miui.luckymoney.config.CommonConfig;
import com.miui.luckymoney.config.Constants;
import com.miui.luckymoney.utils.ResFileUtils;
import com.miui.networkassistant.provider.ProviderConstant;
import java.io.FileInputStream;
import java.io.InputStream;
import miui.util.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;

public class AdsHelper {
    private static final String TAG = "com.miui.luckymoney.webapi.AdsHelper";

    public static class AdsItem {
        public long endTime;
        public Bitmap icon;
        public long startTime;
        public String text;
    }

    public static AdsItem getCurrentAdsItem(Context context) {
        FileInputStream fileInputStream;
        FileInputStream fileInputStream2 = null;
        try {
            String adsConfig = CommonConfig.getInstance(context).getAdsConfig();
            if (TextUtils.isEmpty(adsConfig)) {
                return null;
            }
            JSONArray optJSONArray = new JSONObject(adsConfig).optJSONArray(Constants.JSON_KEY_CONTENTS);
            long currentTimeMillis = System.currentTimeMillis();
            if (optJSONArray != null) {
                int length = optJSONArray.length();
                for (int i = 0; i < length; i++) {
                    JSONObject optJSONObject = optJSONArray.optJSONObject(i);
                    if (optJSONObject != null) {
                        try {
                            long optLong = optJSONObject.optLong("startTime", 0);
                            long optLong2 = optJSONObject.optLong("endTime", 0);
                            if (optLong >= currentTimeMillis || optLong2 <= currentTimeMillis) {
                                IOUtils.closeQuietly((InputStream) null);
                            } else {
                                AdsItem adsItem = new AdsItem();
                                adsItem.startTime = optLong;
                                adsItem.endTime = optLong2;
                                fileInputStream = new FileInputStream(ResFileUtils.getResFile(context, "Ads", optJSONObject.getString(ProviderConstant.DataUsageNotiStatusColumns.COLUMN_ICON)));
                                try {
                                    adsItem.icon = BitmapFactory.decodeStream(fileInputStream);
                                    adsItem.text = optJSONObject.optString(MimeTypes.BASE_TYPE_TEXT, "");
                                    IOUtils.closeQuietly(fileInputStream);
                                    return adsItem;
                                } catch (Exception e) {
                                    e = e;
                                }
                            }
                        } catch (Exception e2) {
                            e = e2;
                            fileInputStream = null;
                            try {
                                Log.d(TAG, "parse ad item failed : ", e);
                                IOUtils.closeQuietly(fileInputStream);
                            } catch (Throwable th) {
                                th = th;
                                fileInputStream2 = fileInputStream;
                                IOUtils.closeQuietly(fileInputStream2);
                                throw th;
                            }
                        } catch (Throwable th2) {
                            th = th2;
                            IOUtils.closeQuietly(fileInputStream2);
                            throw th;
                        }
                    }
                }
            }
            return null;
        } catch (Exception e3) {
            e3.printStackTrace();
            return null;
        }
    }
}
