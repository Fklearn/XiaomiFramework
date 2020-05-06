package com.miui.luckymoney.webapi;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.Log;
import com.google.android.exoplayer2.util.MimeTypes;
import com.miui.luckymoney.config.CommonConfig;
import com.miui.luckymoney.config.Constants;
import com.miui.luckymoney.ui.activity.FloatAssistantActivity;
import com.miui.luckymoney.utils.ResFileUtils;
import com.miui.networkassistant.provider.ProviderConstant;
import java.io.FileInputStream;
import miui.util.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;

public class FloatResourceHelper {
    private static final String TAG = "FloatResourceHelper";

    /* JADX WARNING: Code restructure failed: missing block: B:18:0x0059, code lost:
        r11 = r6.getString("iconLeft");
     */
    /* JADX WARNING: Code restructure failed: missing block: B:19:0x0065, code lost:
        r12 = r6.getString("iconRight");
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static void checkFloatTipsConfigLocalUpdate(android.content.Context r14) {
        /*
            com.miui.luckymoney.config.CommonConfig r0 = com.miui.luckymoney.config.CommonConfig.getInstance(r14)
            java.lang.String r1 = r0.getFloatTipsConfig()
            long r2 = java.lang.System.currentTimeMillis()
            if (r1 != 0) goto L_0x000f
            return
        L_0x000f:
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            java.lang.String r5 = "FloatTipsConfig:"
            r4.append(r5)
            r4.append(r1)
            java.lang.String r4 = r4.toString()
            java.lang.String r5 = "FloatResourceHelper"
            android.util.Log.d(r5, r4)
            org.json.JSONObject r4 = new org.json.JSONObject     // Catch:{ Exception -> 0x0088 }
            r4.<init>(r1)     // Catch:{ Exception -> 0x0088 }
            java.lang.String r1 = "contents"
            org.json.JSONArray r1 = r4.getJSONArray(r1)     // Catch:{ Exception -> 0x0088 }
            int r4 = r1.length()
            r5 = 0
        L_0x0035:
            r6 = 0
            r8 = 1
            r10 = 0
            if (r5 >= r4) goto L_0x006e
            org.json.JSONObject r6 = r1.optJSONObject(r5)
            if (r6 != 0) goto L_0x0043
            goto L_0x006b
        L_0x0043:
            java.lang.String r7 = "startTime"
            long r7 = r6.getLong(r7)     // Catch:{ JSONException -> 0x0067 }
            java.lang.String r9 = "stopTime"
            long r9 = r6.getLong(r9)     // Catch:{ JSONException -> 0x0067 }
            int r11 = (r7 > r2 ? 1 : (r7 == r2 ? 0 : -1))
            if (r11 >= 0) goto L_0x0057
            int r12 = (r9 > r2 ? 1 : (r9 == r2 ? 0 : -1))
            if (r12 > 0) goto L_0x0059
        L_0x0057:
            if (r11 <= 0) goto L_0x006b
        L_0x0059:
            java.lang.String r11 = "iconLeft"
            java.lang.String r11 = r6.getString(r11)     // Catch:{ JSONException -> 0x0067 }
            java.lang.String r12 = "iconRight"
            java.lang.String r1 = r6.getString(r12)     // Catch:{ JSONException -> 0x0067 }
            r12 = r1
            goto L_0x0072
        L_0x0067:
            r6 = move-exception
            r6.printStackTrace()
        L_0x006b:
            int r5 = r5 + 1
            goto L_0x0035
        L_0x006e:
            r11 = r10
            r12 = r11
            r9 = r8
            r7 = r6
        L_0x0072:
            long r1 = r0.getFloatTipsStartTime()
            long r3 = r0.getFloatTipsStopTime()
            int r0 = (r7 > r1 ? 1 : (r7 == r1 ? 0 : -1))
            if (r0 == 0) goto L_0x0087
            int r0 = (r9 > r3 ? 1 : (r9 == r3 ? 0 : -1))
            if (r0 == 0) goto L_0x0087
            r13 = 0
            r6 = r14
            updateFloatTips(r6, r7, r9, r11, r12, r13)
        L_0x0087:
            return
        L_0x0088:
            r14 = move-exception
            java.lang.String r0 = "parse json failed :"
            android.util.Log.d(r5, r0, r14)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.luckymoney.webapi.FloatResourceHelper.checkFloatTipsConfigLocalUpdate(android.content.Context):void");
    }

    /* JADX WARNING: Code restructure failed: missing block: B:38:0x00d1, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:39:0x00d2, code lost:
        r9 = r10;
        r5 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:64:0x0148, code lost:
        r0 = th;
     */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Removed duplicated region for block: B:64:0x0148 A[ExcHandler: all (th java.lang.Throwable), PHI: r15 
      PHI: (r15v10 java.io.FileInputStream) = (r15v7 java.io.FileInputStream), (r15v11 java.io.FileInputStream), (r15v11 java.io.FileInputStream), (r15v11 java.io.FileInputStream), (r15v11 java.io.FileInputStream), (r15v11 java.io.FileInputStream), (r15v7 java.io.FileInputStream), (r15v7 java.io.FileInputStream), (r15v7 java.io.FileInputStream), (r15v7 java.io.FileInputStream) binds: [B:22:0x008a, B:41:0x00d8, B:56:0x011f, B:46:0x00ec, B:52:0x010e, B:53:?, B:26:0x009c, B:27:?, B:29:0x00a0, B:30:?] A[DONT_GENERATE, DONT_INLINE], Splitter:B:22:0x008a] */
    /* JADX WARNING: Removed duplicated region for block: B:89:0x01b8  */
    /* JADX WARNING: Removed duplicated region for block: B:95:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static void initConfig(android.content.Context r21, com.miui.luckymoney.ui.activity.FloatAssistantActivity.Config r22, com.miui.luckymoney.ui.activity.FloatAssistantActivity.DefaultConfig r23, com.miui.luckymoney.ui.activity.FloatAssistantActivity.DefaultConfig r24) {
        /*
            r1 = r21
            r2 = r22
            r3 = r23
            r4 = r24
            java.lang.String r5 = "url"
            java.lang.String r6 = "Float_5"
            java.lang.String r7 = "FloatResourceHelper"
            com.miui.luckymoney.config.CommonConfig r0 = com.miui.luckymoney.config.CommonConfig.getInstance(r21)
            java.lang.String r0 = r0.getFloatAssistantConfig()
            com.miui.luckymoney.config.CommonConfig r8 = com.miui.luckymoney.config.CommonConfig.getInstance(r21)
            java.lang.String r8 = r8.getFloatActivityDefaultConfig()
            org.json.JSONObject r10 = new org.json.JSONObject     // Catch:{ Exception -> 0x0024 }
            r10.<init>(r0)     // Catch:{ Exception -> 0x0024 }
            goto L_0x002b
        L_0x0024:
            r0 = move-exception
            java.lang.String r10 = "parse config failed :"
            android.util.Log.d(r7, r10, r0)
            r10 = 0
        L_0x002b:
            org.json.JSONObject r0 = new org.json.JSONObject     // Catch:{ Exception -> 0x0032 }
            r0.<init>(r8)     // Catch:{ Exception -> 0x0032 }
            r8 = r0
            goto L_0x0039
        L_0x0032:
            r0 = move-exception
            java.lang.String r8 = "parse default config failed :"
            android.util.Log.d(r7, r8, r0)
            r8 = 0
        L_0x0039:
            if (r10 == 0) goto L_0x0182
            java.lang.String r0 = "contents"
            org.json.JSONArray r0 = r10.getJSONArray(r0)     // Catch:{ Exception -> 0x016a, all -> 0x0165 }
            int r10 = r0.length()     // Catch:{ Exception -> 0x016a, all -> 0x0165 }
            r13 = 0
            r14 = 0
            r15 = 0
        L_0x0048:
            if (r13 >= r10) goto L_0x015e
            org.json.JSONObject r9 = r0.getJSONObject(r13)     // Catch:{ Exception -> 0x015b, all -> 0x0158 }
            java.lang.String r11 = "startTime"
            r16 = r13
            long r12 = r9.getLong(r11)     // Catch:{ Exception -> 0x015b, all -> 0x0158 }
            java.lang.String r11 = "endTime"
            r17 = r10
            long r10 = r9.getLong(r11)     // Catch:{ Exception -> 0x015b, all -> 0x0158 }
            long r18 = java.lang.System.currentTimeMillis()     // Catch:{ Exception -> 0x015b, all -> 0x0158 }
            int r20 = (r18 > r12 ? 1 : (r18 == r12 ? 0 : -1))
            if (r20 < 0) goto L_0x014d
            int r18 = (r18 > r10 ? 1 : (r18 == r10 ? 0 : -1))
            if (r18 > 0) goto L_0x014d
            r18 = r0
            r0 = 0
            r2.isFour = r0     // Catch:{ Exception -> 0x015b, all -> 0x0158 }
            r2.startTime = r12     // Catch:{ Exception -> 0x015b, all -> 0x0158 }
            r2.endTime = r10     // Catch:{ Exception -> 0x015b, all -> 0x0158 }
            java.lang.String r10 = "text"
            java.lang.String r10 = r9.getString(r10)     // Catch:{ Exception -> 0x015b, all -> 0x0158 }
            r2.text = r10     // Catch:{ Exception -> 0x015b, all -> 0x0158 }
            java.io.FileInputStream r10 = new java.io.FileInputStream     // Catch:{ Exception -> 0x015b, all -> 0x0158 }
            java.lang.String r11 = "icon1"
            java.lang.String r11 = r9.getString(r11)     // Catch:{ Exception -> 0x015b, all -> 0x0158 }
            java.io.File r11 = com.miui.luckymoney.utils.ResFileUtils.getResFile(r1, r6, r11)     // Catch:{ Exception -> 0x015b, all -> 0x0158 }
            r10.<init>(r11)     // Catch:{ Exception -> 0x015b, all -> 0x0158 }
            android.graphics.Bitmap r11 = android.graphics.BitmapFactory.decodeStream(r10)     // Catch:{ Exception -> 0x014a, all -> 0x0148 }
            r2.icon1 = r11     // Catch:{ Exception -> 0x014a, all -> 0x0148 }
            miui.util.IOUtils.closeQuietly(r10)     // Catch:{ Exception -> 0x014a, all -> 0x0148 }
            java.lang.String r11 = "isFlashing"
            boolean r11 = r9.getBoolean(r11)     // Catch:{ Exception -> 0x014a, all -> 0x0148 }
            if (r11 == 0) goto L_0x00d6
            r11 = 1
            r2.isFlashing = r11     // Catch:{ Exception -> 0x00d1, all -> 0x0148 }
            java.lang.String r11 = "flashStartTime"
            long r11 = r9.getLong(r11)     // Catch:{ Exception -> 0x014a, all -> 0x0148 }
            r2.flashStartTime = r11     // Catch:{ Exception -> 0x014a, all -> 0x0148 }
            java.lang.String r11 = "flashEndTime"
            long r11 = r9.getLong(r11)     // Catch:{ Exception -> 0x014a, all -> 0x0148 }
            r2.flashEndTime = r11     // Catch:{ Exception -> 0x014a, all -> 0x0148 }
            java.io.FileInputStream r11 = new java.io.FileInputStream     // Catch:{ Exception -> 0x014a, all -> 0x0148 }
            java.lang.String r12 = "icon2"
            java.lang.String r12 = r9.getString(r12)     // Catch:{ Exception -> 0x014a, all -> 0x0148 }
            java.io.File r12 = com.miui.luckymoney.utils.ResFileUtils.getResFile(r1, r6, r12)     // Catch:{ Exception -> 0x014a, all -> 0x0148 }
            r11.<init>(r12)     // Catch:{ Exception -> 0x014a, all -> 0x0148 }
            android.graphics.Bitmap r12 = android.graphics.BitmapFactory.decodeStream(r11)     // Catch:{ Exception -> 0x00cc, all -> 0x00c8 }
            r2.icon2 = r12     // Catch:{ Exception -> 0x00cc, all -> 0x00c8 }
            miui.util.IOUtils.closeQuietly(r11)     // Catch:{ Exception -> 0x00cc, all -> 0x00c8 }
            r15 = r11
            goto L_0x00d6
        L_0x00c8:
            r0 = move-exception
            r15 = r11
            goto L_0x017b
        L_0x00cc:
            r0 = move-exception
            r9 = r10
            r15 = r11
            goto L_0x016d
        L_0x00d1:
            r0 = move-exception
            r9 = r10
            r5 = r11
            goto L_0x016e
        L_0x00d6:
            java.lang.String r11 = "type"
            java.lang.String r11 = r9.getString(r11)     // Catch:{ Exception -> 0x014a, all -> 0x0148 }
            r2.type = r11     // Catch:{ Exception -> 0x014a, all -> 0x0148 }
            java.lang.String r11 = "intent"
            java.lang.String r12 = r2.type     // Catch:{ Exception -> 0x014a, all -> 0x0148 }
            boolean r11 = r11.equals(r12)     // Catch:{ Exception -> 0x014a, all -> 0x0148 }
            java.lang.String r12 = "goto"
            if (r11 == 0) goto L_0x011f
            java.lang.String r11 = "errorText"
            android.content.res.Resources r13 = r21.getResources()     // Catch:{ Exception -> 0x014a, all -> 0x0148 }
            r14 = 2131758286(0x7f100cce, float:1.9147532E38)
            java.lang.String r13 = r13.getString(r14)     // Catch:{ Exception -> 0x014a, all -> 0x0148 }
            java.lang.String r11 = r9.optString(r11, r13)     // Catch:{ Exception -> 0x014a, all -> 0x0148 }
            r2.errorText = r11     // Catch:{ Exception -> 0x014a, all -> 0x0148 }
            org.json.JSONArray r11 = r9.getJSONArray(r12)     // Catch:{ Exception -> 0x014a, all -> 0x0148 }
            r13 = r0
        L_0x0102:
            int r14 = r11.length()     // Catch:{ Exception -> 0x014a, all -> 0x0148 }
            if (r13 >= r14) goto L_0x011f
            org.json.JSONObject r14 = r11.getJSONObject(r13)     // Catch:{ Exception -> 0x014a, all -> 0x0148 }
            java.lang.String r0 = "componentName"
            java.lang.String r0 = r14.getString(r0)     // Catch:{ Exception -> 0x011b, all -> 0x0148 }
            android.content.ComponentName r0 = android.content.ComponentName.unflattenFromString(r0)     // Catch:{ Exception -> 0x011b, all -> 0x0148 }
            java.util.ArrayList<android.content.ComponentName> r14 = r2.componentNames     // Catch:{ Exception -> 0x011b, all -> 0x0148 }
            r14.add(r0)     // Catch:{ Exception -> 0x011b, all -> 0x0148 }
        L_0x011b:
            int r13 = r13 + 1
            r0 = 0
            goto L_0x0102
        L_0x011f:
            java.lang.String r0 = r2.type     // Catch:{ Exception -> 0x014a, all -> 0x0148 }
            boolean r0 = r5.equals(r0)     // Catch:{ Exception -> 0x014a, all -> 0x0148 }
            if (r0 == 0) goto L_0x0145
            org.json.JSONObject r0 = r9.getJSONObject(r12)     // Catch:{ Exception -> 0x014a, all -> 0x0148 }
            java.lang.String r9 = r0.getString(r5)     // Catch:{ Exception -> 0x014a, all -> 0x0148 }
            r2.url = r9     // Catch:{ Exception -> 0x014a, all -> 0x0148 }
            java.lang.String r9 = "package"
            java.lang.String r0 = r0.optString(r9)     // Catch:{ Exception -> 0x014a, all -> 0x0148 }
            r2.packageName = r0     // Catch:{ Exception -> 0x014a, all -> 0x0148 }
            java.lang.String r0 = r2.packageName     // Catch:{ Exception -> 0x014a, all -> 0x0148 }
            int r0 = r0.length()     // Catch:{ Exception -> 0x014a, all -> 0x0148 }
            if (r0 != 0) goto L_0x0145
            r9 = 0
            r2.packageName = r9     // Catch:{ Exception -> 0x014a, all -> 0x0148 }
            goto L_0x0146
        L_0x0145:
            r9 = 0
        L_0x0146:
            r14 = r10
            goto L_0x0150
        L_0x0148:
            r0 = move-exception
            goto L_0x017b
        L_0x014a:
            r0 = move-exception
            r9 = r10
            goto L_0x016d
        L_0x014d:
            r18 = r0
            r9 = 0
        L_0x0150:
            int r13 = r16 + 1
            r10 = r17
            r0 = r18
            goto L_0x0048
        L_0x0158:
            r0 = move-exception
            r10 = r14
            goto L_0x017b
        L_0x015b:
            r0 = move-exception
            r9 = r14
            goto L_0x016d
        L_0x015e:
            miui.util.IOUtils.closeQuietly(r14)
        L_0x0161:
            miui.util.IOUtils.closeQuietly(r15)
            goto L_0x0182
        L_0x0165:
            r0 = move-exception
            r9 = 0
            r10 = r9
            r15 = r10
            goto L_0x017b
        L_0x016a:
            r0 = move-exception
            r9 = 0
            r15 = r9
        L_0x016d:
            r5 = 1
        L_0x016e:
            r2.isFour = r5     // Catch:{ all -> 0x0179 }
            java.lang.String r2 = "parse json array failed :"
            android.util.Log.d(r7, r2, r0)     // Catch:{ all -> 0x0179 }
            miui.util.IOUtils.closeQuietly(r9)
            goto L_0x0161
        L_0x0179:
            r0 = move-exception
            r10 = r9
        L_0x017b:
            miui.util.IOUtils.closeQuietly(r10)
            miui.util.IOUtils.closeQuietly(r15)
            throw r0
        L_0x0182:
            java.util.ArrayList<android.content.ComponentName> r0 = r3.componentNames
            android.content.ComponentName r2 = new android.content.ComponentName
            java.lang.String r5 = "com.tencent.mm"
            java.lang.String r6 = "com.tencent.mm.plugin.shakelucky.ui.ShakeLuckyUI"
            r2.<init>(r5, r6)
            r0.add(r2)
            java.util.ArrayList<android.content.ComponentName> r0 = r3.componentNames
            android.content.ComponentName r2 = new android.content.ComponentName
            java.lang.String r6 = "com.tencent.mm.plugin.shake.ui.ShakeReportUI"
            r2.<init>(r5, r6)
            r0.add(r2)
            java.util.ArrayList<android.content.ComponentName> r0 = r4.componentNames
            android.content.ComponentName r2 = new android.content.ComponentName
            java.lang.String r5 = "com.eg.android.AlipayGphone"
            java.lang.String r6 = "com.alipay.android.wallet.newyear.activity.MonkeyYearActivity"
            r2.<init>(r5, r6)
            r0.add(r2)
            java.util.ArrayList<android.content.ComponentName> r0 = r4.componentNames
            android.content.ComponentName r2 = new android.content.ComponentName
            java.lang.String r6 = "com.alipay.mobile.xiuxiu.ui.MainActivity"
            r2.<init>(r5, r6)
            r0.add(r2)
            if (r8 == 0) goto L_0x01e0
            java.lang.String r0 = "wechat"
            org.json.JSONObject r0 = r8.optJSONObject(r0)
            java.lang.String r2 = "alipay"
            org.json.JSONObject r2 = r8.optJSONObject(r2)
            android.content.res.Resources r5 = r21.getResources()
            r6 = 2131755806(0x7f10031e, float:1.9142502E38)
            java.lang.String r5 = r5.getString(r6)
            processDefaultConfig(r1, r0, r3, r5)
            android.content.res.Resources r0 = r21.getResources()
            r3 = 2131757828(0x7f100b04, float:1.9146603E38)
            java.lang.String r0 = r0.getString(r3)
            processDefaultConfig(r1, r2, r4, r0)
        L_0x01e0:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.luckymoney.webapi.FloatResourceHelper.initConfig(android.content.Context, com.miui.luckymoney.ui.activity.FloatAssistantActivity$Config, com.miui.luckymoney.ui.activity.FloatAssistantActivity$DefaultConfig, com.miui.luckymoney.ui.activity.FloatAssistantActivity$DefaultConfig):void");
    }

    private static void processDefaultConfig(Context context, JSONObject jSONObject, FloatAssistantActivity.DefaultConfig defaultConfig, String str) {
        if (jSONObject != null) {
            defaultConfig.text = jSONObject.optString(MimeTypes.BASE_TYPE_TEXT, str);
            JSONArray optJSONArray = jSONObject.optJSONArray("goto");
            int length = optJSONArray == null ? 0 : optJSONArray.length();
            boolean z = false;
            for (int i = 0; i < length; i++) {
                JSONObject optJSONObject = optJSONArray.optJSONObject(i);
                if (optJSONObject != null) {
                    String optString = optJSONObject.optString("componentName");
                    if (optString.length() != 0) {
                        if (!z) {
                            defaultConfig.componentNames.clear();
                            z = true;
                        }
                        defaultConfig.componentNames.add(ComponentName.unflattenFromString(optString));
                    }
                }
            }
            FileInputStream fileInputStream = null;
            try {
                String optString2 = jSONObject.optString(ProviderConstant.DataUsageNotiStatusColumns.COLUMN_ICON, (String) null);
                if (!TextUtils.isEmpty(optString2)) {
                    FileInputStream fileInputStream2 = new FileInputStream(ResFileUtils.getResFile(context, ResFileUtils.FLOAT34, optString2));
                    try {
                        defaultConfig.icon = BitmapFactory.decodeStream(fileInputStream2);
                        fileInputStream = fileInputStream2;
                    } catch (Exception e) {
                        e = e;
                        fileInputStream = fileInputStream2;
                        try {
                            Log.d(TAG, "parse item failed :", e);
                            IOUtils.closeQuietly(fileInputStream);
                        } catch (Throwable th) {
                            th = th;
                        }
                    } catch (Throwable th2) {
                        th = th2;
                        fileInputStream = fileInputStream2;
                        IOUtils.closeQuietly(fileInputStream);
                        throw th;
                    }
                }
            } catch (Exception e2) {
                e = e2;
                Log.d(TAG, "parse item failed :", e);
                IOUtils.closeQuietly(fileInputStream);
            }
            IOUtils.closeQuietly(fileInputStream);
        }
    }

    private static void updateFloatTips(Context context, long j, long j2, String str, String str2, boolean z) {
        Log.i(TAG, "updateFloatTips");
        CommonConfig instance = CommonConfig.getInstance(context);
        instance.setFloatTipsStartTime(j);
        instance.setFloatTipsStopTime(j2);
        instance.setFloatTipsImageLeft(str);
        instance.setFloatTipsImageRight(str2);
        instance.setFloatTipsUpdateTime(0);
        Intent intent = new Intent();
        intent.setAction(Constants.ACTION_REFRESH_FLOAT_TIPS_DAILY);
        ((AlarmManager) context.getSystemService("alarm")).cancel(PendingIntent.getBroadcast(context, 0, intent, 0));
    }
}
