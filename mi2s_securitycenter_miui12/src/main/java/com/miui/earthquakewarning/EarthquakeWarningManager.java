package com.miui.earthquakewarning;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Location;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;
import b.b.c.j.x;
import com.google.android.exoplayer2.upstream.DataSchemeDataSource;
import com.miui.earthquakewarning.analytics.AnalyticHelper;
import com.miui.earthquakewarning.model.LocationModel;
import com.miui.earthquakewarning.model.SignatureReuslt;
import com.miui.earthquakewarning.model.UserQuakeItem;
import com.miui.earthquakewarning.model.WarningModel;
import com.miui.earthquakewarning.model.WhiteListResult;
import com.miui.earthquakewarning.service.EarthquakeWarningService;
import com.miui.earthquakewarning.service.ManageDataTask;
import com.miui.earthquakewarning.service.RequestSignatureTask;
import com.miui.earthquakewarning.service.RequestWhiteListTask;
import com.miui.earthquakewarning.service.UpdateAreaCodeManager;
import com.miui.earthquakewarning.ui.EarthquakeWarningAlertActivity;
import com.miui.earthquakewarning.utils.FileUtils;
import com.miui.earthquakewarning.utils.LocationUtils;
import com.miui.earthquakewarning.utils.MD5Util;
import com.miui.earthquakewarning.utils.NotificationUtil;
import com.miui.earthquakewarning.utils.Utils;
import com.miui.networkassistant.config.Constants;
import com.miui.push.b;
import com.miui.securitycenter.Application;
import com.miui.securitycenter.R;
import java.util.ArrayList;
import java.util.List;
import miui.os.Build;
import org.json.JSONArray;
import org.json.JSONObject;

public class EarthquakeWarningManager {
    private static final String AUTHORITY = "com.miui.earthquakewarning.EarthquakeContentProvider";
    private static final String AUTONAVI_PACKAGENAME = "com.autonavi.minimap";
    private static final String AUTONAVI_SEARTH_URI = "androidamap://poi?sourceApplication=com.miui.earthquakewarning&keywords=应急避难场所&dev=0";
    private static final String BAIDUMAP_PACKAGENAME = "com.baidu.BaiduMap";
    private static final String BAIDUMAP_SEARTH_URI = "baidumap://map/place/nearby?query=应急避难场所&src=com.miui.earthquakewarning";
    private static final Uri EARTHQUAKE_URI = Uri.parse("content://com.miui.earthquakewarning.EarthquakeContentProvider/earthquake");
    private static final String TAG = "EarthquakeManager";
    private static volatile EarthquakeWarningManager instance;
    /* access modifiers changed from: private */
    public long eventId = -1;
    private Context mContext = Application.d();

    private EarthquakeWarningManager() {
    }

    private Bitmap getBitmap(Context context) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;
        return BitmapFactory.decodeResource(context.getResources(), R.drawable.ew_push_icon, options).copy(Bitmap.Config.ARGB_8888, true);
    }

    public static EarthquakeWarningManager getInstance() {
        if (instance == null) {
            synchronized (EarthquakeWarningManager.class) {
                if (instance == null) {
                    instance = new EarthquakeWarningManager();
                }
            }
        }
        return instance;
    }

    /* access modifiers changed from: private */
    public void getLocationStep(final UserQuakeItem userQuakeItem, final Context context) {
        LocationUtils.getAdminAreaLocation2(Application.d(), new LocationUtils.LocationResultListener() {
            public void locationFail() {
                Log.e(EarthquakeWarningManager.TAG, "locate failed");
                AnalyticHelper.trackPushActionModuleClick(AnalyticHelper.PUSH_ERROR_LOCATION_FAILED);
            }

            public void locationSuccess(Location location) {
                LocationModel locationModel = new LocationModel();
                locationModel.setLatitude(location.getLatitude());
                locationModel.setLongitude(location.getLongitude());
                userQuakeItem.setLocation(locationModel);
                if (!userQuakeItem.calIC(context)) {
                    Log.i(EarthquakeWarningManager.TAG, "show failed : push_error_time_long");
                    AnalyticHelper.trackPushActionModuleClick(AnalyticHelper.PUSH_ERROR_TIME_LONG);
                    return;
                }
                if (userQuakeItem.getIntensity() >= 2.0f) {
                    new ManageDataTask(context, userQuakeItem).execute(new String[0]);
                    long unused = EarthquakeWarningManager.this.eventId = userQuakeItem.getEventID();
                } else if (EarthquakeWarningManager.this.eventId <= 0 || EarthquakeWarningManager.this.eventId != userQuakeItem.getEventID()) {
                    if (Utils.isLowEarthquakeWarningOpen()) {
                        new ManageDataTask(context, userQuakeItem).execute(new String[0]);
                        LocationUtils.getGeoArea(context, location.getLatitude(), location.getLongitude(), new LocationUtils.AreaResultListener() {
                            public void areaFail() {
                                AnonymousClass4 r0 = AnonymousClass4.this;
                                EarthquakeWarningManager.this.showLowEarthquakeWarning(context, userQuakeItem.getEpiLocation().getPlace(), String.format("%.1f", new Object[]{Float.valueOf(userQuakeItem.getIntensity())}), userQuakeItem, (String) null);
                            }

                            public void areaSuccess(Address address) {
                                StringBuilder sb;
                                String subLocality;
                                String subLocality2;
                                if (TextUtils.isEmpty(address.getSubLocality())) {
                                    sb = new StringBuilder();
                                    sb.append(address.getSubAdminArea());
                                    subLocality = address.getLocality();
                                } else if (TextUtils.isEmpty(address.getLocality())) {
                                    subLocality2 = address.getSubLocality();
                                    String str = subLocality2;
                                    AnonymousClass4 r7 = AnonymousClass4.this;
                                    EarthquakeWarningManager.this.showLowEarthquakeWarning(context, userQuakeItem.getEpiLocation().getPlace(), String.format("%.1f", new Object[]{Float.valueOf(userQuakeItem.getIntensity())}), userQuakeItem, str);
                                } else {
                                    sb = new StringBuilder();
                                    sb.append(address.getLocality());
                                    subLocality = address.getSubLocality();
                                }
                                sb.append(subLocality);
                                subLocality2 = sb.toString();
                                String str2 = subLocality2;
                                AnonymousClass4 r72 = AnonymousClass4.this;
                                EarthquakeWarningManager.this.showLowEarthquakeWarning(context, userQuakeItem.getEpiLocation().getPlace(), String.format("%.1f", new Object[]{Float.valueOf(userQuakeItem.getIntensity())}), userQuakeItem, str2);
                            }
                        });
                    }
                    AnalyticHelper.trackPushActionModuleClick(AnalyticHelper.PUSH_ERROR_INTENSITY_LOW);
                    return;
                } else {
                    new ManageDataTask(context, userQuakeItem).execute(new String[0]);
                }
                EarthquakeWarningManager.showAlarmNotification(context, userQuakeItem);
            }
        });
    }

    private void getWhiteList(final Context context, final UserQuakeItem userQuakeItem) {
        if (userQuakeItem.getType() == 1) {
            String accountId = Utils.getAccountId(context);
            if (TextUtils.isEmpty(accountId)) {
                Log.e(TAG, "no mi account id");
                return;
            }
            RequestWhiteListTask requestWhiteListTask = new RequestWhiteListTask();
            requestWhiteListTask.setListener(new RequestWhiteListTask.Listener() {
                public void onPost(WhiteListResult whiteListResult) {
                    if (whiteListResult != null && whiteListResult.getCode() == 200 && whiteListResult.getData() != null && whiteListResult.getData().isCheckResult()) {
                        EarthquakeWarningManager.this.getLocationStep(userQuakeItem, context);
                    }
                }
            });
            requestWhiteListTask.execute(new String[]{accountId, "miuisec"});
            return;
        }
        getLocationStep(userQuakeItem, context);
    }

    /* access modifiers changed from: private */
    public void matchSignature(Context context, String str, int i, UserQuakeItem userQuakeItem, String str2, SignatureReuslt signatureReuslt) {
        boolean z;
        StringBuilder sb;
        String str3;
        UserQuakeItem userQuakeItem2 = userQuakeItem;
        SignatureReuslt signatureReuslt2 = new SignatureReuslt();
        if (signatureReuslt != null) {
            signatureReuslt2 = signatureReuslt;
        }
        if (!TextUtils.isEmpty(str2)) {
            try {
                JSONObject jSONObject = new JSONObject(str2);
                signatureReuslt2.setCode(jSONObject.optInt("code", -1));
                JSONArray jSONArray = jSONObject.getJSONArray("datas");
                ArrayList arrayList = new ArrayList();
                for (int i2 = 0; i2 < jSONArray.length(); i2++) {
                    JSONObject jSONObject2 = jSONArray.getJSONObject(i2);
                    SignatureReuslt.DatasBean datasBean = new SignatureReuslt.DatasBean();
                    datasBean.setChannel(jSONObject2.optString("channel"));
                    JSONArray jSONArray2 = jSONObject2.getJSONArray(DataSchemeDataSource.SCHEME_DATA);
                    ArrayList arrayList2 = new ArrayList();
                    for (int i3 = 0; i3 < jSONArray2.length(); i3++) {
                        JSONObject jSONObject3 = jSONArray2.getJSONObject(i3);
                        SignatureReuslt.DataBean dataBean = new SignatureReuslt.DataBean();
                        dataBean.setCode(jSONObject3.optInt("code"));
                        dataBean.setDistrict(jSONObject3.optString("district"));
                        JSONArray optJSONArray = jSONObject3.optJSONArray("signs");
                        ArrayList arrayList3 = new ArrayList();
                        for (int i4 = 0; i4 < optJSONArray.length(); i4++) {
                            arrayList3.add(optJSONArray.getString(i4));
                        }
                        dataBean.setSigns(arrayList3);
                        arrayList2.add(dataBean);
                    }
                    datasBean.setData(arrayList2);
                    arrayList.add(datasBean);
                }
                signatureReuslt2.setDatas(arrayList);
            } catch (Exception e) {
                AnalyticHelper.trackPushActionModuleClick(AnalyticHelper.PUSH_ERROR_PARSE_SIGNATURE);
                Log.e(TAG, "parse json failed :", e);
            }
        }
        if (signatureReuslt2.getCode() != 0) {
        } else if (signatureReuslt2.getDatas() != null) {
            int i5 = 0;
            while (true) {
                if (i5 >= signatureReuslt2.getDatas().size()) {
                    z = false;
                    break;
                }
                if (str.equals(signatureReuslt2.getDatas().get(i5).getChannel())) {
                    List<SignatureReuslt.DataBean> data = signatureReuslt2.getDatas().get(i5).getData();
                    int i6 = i;
                    int i7 = 1;
                    int i8 = 2;
                    z = false;
                    while (i8 >= 0) {
                        int i9 = 0;
                        while (true) {
                            if (i9 >= data.size()) {
                                break;
                            } else if (i6 == data.get(i9).getCode()) {
                                String str4 = "";
                                for (int i10 = 0; i10 < data.get(i9).getSigns().size(); i10++) {
                                    if (i10 < data.get(i9).getSigns().size() - 1) {
                                        sb = new StringBuilder();
                                        sb.append(str4);
                                        sb.append(data.get(i9).getSigns().get(i10));
                                        str3 = "\n";
                                    } else {
                                        sb = new StringBuilder();
                                        sb.append(str4);
                                        str3 = data.get(i9).getSigns().get(i10);
                                    }
                                    sb.append(str3);
                                    str4 = sb.toString();
                                }
                                userQuakeItem2.setSignatureText(str4);
                                z = true;
                                i8 = 0;
                            } else {
                                i9++;
                            }
                        }
                        int pow = (int) Math.pow(100.0d, (double) i7);
                        i6 = (i6 / pow) * pow;
                        i7++;
                        i8--;
                    }
                } else {
                    i5++;
                }
            }
            if (z) {
                getWhiteList(context, userQuakeItem2);
                return;
            }
            Log.i(TAG, "show failed : push_error_no_sign_area");
            AnalyticHelper.trackPushActionModuleClick(AnalyticHelper.PUSH_ERROR_NO_SIGN_AREA);
        } else {
            Log.e(TAG, "no sign area");
        }
    }

    public static UserQuakeItem parseQuake(JSONObject jSONObject) {
        try {
            UserQuakeItem userQuakeItem = new UserQuakeItem();
            userQuakeItem.setIndex(jSONObject.getInt("index"));
            userQuakeItem.setMagnitude((float) jSONObject.getDouble(WarningModel.Columns.MAGNITUDE));
            userQuakeItem.getEpiLocation().setLongitude((double) ((float) jSONObject.getDouble(WarningModel.Columns.LONGITUDE)));
            userQuakeItem.getEpiLocation().setLatitude((double) ((float) jSONObject.getDouble(WarningModel.Columns.LATITUDE)));
            userQuakeItem.getEpiLocation().setPlace(jSONObject.getString(WarningModel.Columns.EPICENTER));
            userQuakeItem.setDepth((float) jSONObject.getDouble(WarningModel.Columns.DEPTH));
            userQuakeItem.setType(jSONObject.getInt("type"));
            userQuakeItem.setStartTime(jSONObject.getLong("startTime"));
            userQuakeItem.setUpdateTime(jSONObject.getLong("updateTime"));
            userQuakeItem.setXmUpdateTime(jSONObject.getLong("xmUpdateTime"));
            userQuakeItem.setChannel(jSONObject.getString("channel"));
            userQuakeItem.setEventID(jSONObject.optLong("eventId"));
            userQuakeItem.getReceiveOneMinLater();
            return userQuakeItem;
        } catch (Exception unused) {
            Log.e(TAG, "receive error earthquake warning message");
            return null;
        }
    }

    private void requestSignature(Context context, String str, int i, UserQuakeItem userQuakeItem) {
        RequestSignatureTask requestSignatureTask = new RequestSignatureTask();
        final Context context2 = context;
        final String str2 = str;
        final int i2 = i;
        final UserQuakeItem userQuakeItem2 = userQuakeItem;
        requestSignatureTask.setListener(new RequestSignatureTask.Listener() {
            public void onPost(SignatureReuslt signatureReuslt) {
                EarthquakeWarningManager.this.matchSignature(context2, str2, i2, userQuakeItem2, (String) null, signatureReuslt);
            }
        });
        requestSignatureTask.execute(new String[]{str});
    }

    private void requestSignatureBefore(Context context, String str, int i, UserQuakeItem userQuakeItem) {
        String signatureFromData = FileUtils.getSignatureFromData(context);
        if (TextUtils.isEmpty(signatureFromData)) {
            requestSignature(context, str, i, userQuakeItem);
        } else {
            matchSignature(context, str, i, userQuakeItem, signatureFromData, (SignatureReuslt) null);
        }
    }

    public static void showAlarmNotification(Context context, UserQuakeItem userQuakeItem) {
        Intent intent = new Intent(context, EarthquakeWarningAlertActivity.class);
        intent.putExtra("UserQuakeItem", userQuakeItem);
        intent.addFlags(268435456);
        context.startActivity(intent);
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Removed duplicated region for block: B:30:0x019c  */
    /* JADX WARNING: Removed duplicated region for block: B:33:0x01cb  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void showLowEarthquakeWarning(android.content.Context r16, java.lang.String r17, java.lang.String r18, com.miui.earthquakewarning.model.UserQuakeItem r19, java.lang.String r20) {
        /*
            r15 = this;
            r0 = r16
            r1 = r17
            java.lang.String r2 = "com.miui.securitycenter"
            android.app.Notification$Builder r3 = b.b.c.j.v.a((android.content.Context) r0, (java.lang.String) r2)
            r4 = 2131231157(0x7f0801b5, float:1.8078387E38)
            r3.setSmallIcon(r4)
            java.util.Locale r4 = java.util.Locale.getDefault()
            java.lang.String r4 = r4.toString()
            java.lang.String r5 = "zh_CN"
            boolean r4 = r5.equalsIgnoreCase(r4)
            r5 = 2131756088(0x7f100438, float:1.9143074E38)
            r6 = 0
            r7 = 2131756116(0x7f100454, float:1.914313E38)
            r8 = 2131756117(0x7f100455, float:1.9143132E38)
            r9 = 2
            r10 = 1
            r11 = 0
            if (r4 == 0) goto L_0x0098
            java.text.SimpleDateFormat r4 = new java.text.SimpleDateFormat
            java.lang.String r12 = "HH:mm"
            r4.<init>(r12)
            long r12 = r19.getStartTime()
            java.lang.Long r12 = java.lang.Long.valueOf(r12)
            java.lang.String r4 = r4.format(r12)
            java.lang.Object[] r12 = new java.lang.Object[r10]
            float r13 = r19.getMagnitude()
            java.lang.Float r13 = java.lang.Float.valueOf(r13)
            r12[r11] = r13
            java.lang.String r13 = "%.1f"
            java.lang.String r12 = java.lang.String.format(r13, r12)
            java.lang.Object[] r13 = new java.lang.Object[r9]
            java.lang.StringBuilder r14 = new java.lang.StringBuilder
            r14.<init>()
            r14.append(r4)
            r14.append(r1)
            java.lang.String r1 = r14.toString()
            r13[r11] = r1
            r13[r10] = r12
            java.lang.String r1 = r0.getString(r8, r13)
            r3.setContentTitle(r1)
            boolean r1 = android.text.TextUtils.isEmpty(r20)
            if (r1 == 0) goto L_0x0077
            java.lang.String r1 = ""
            goto L_0x0079
        L_0x0077:
            r1 = r20
        L_0x0079:
            java.lang.Object[] r4 = new java.lang.Object[r9]
            r4[r11] = r18
            r4[r10] = r1
            java.lang.String r4 = r0.getString(r7, r4)
            float r7 = r19.getIntensity()
            int r6 = (r7 > r6 ? 1 : (r7 == r6 ? 0 : -1))
            if (r6 != 0) goto L_0x00bb
            android.content.res.Resources r4 = r16.getResources()
            java.lang.Object[] r6 = new java.lang.Object[r10]
            r6[r11] = r1
            java.lang.String r4 = r4.getString(r5, r6)
            goto L_0x00bb
        L_0x0098:
            java.lang.Object[] r4 = new java.lang.Object[r10]
            r4[r11] = r1
            java.lang.String r1 = r0.getString(r8, r4)
            r3.setContentTitle(r1)
            java.lang.Object[] r1 = new java.lang.Object[r10]
            r1[r11] = r18
            java.lang.String r4 = r0.getString(r7, r1)
            float r1 = r19.getIntensity()
            int r1 = (r1 > r6 ? 1 : (r1 == r6 ? 0 : -1))
            if (r1 != 0) goto L_0x00bb
            android.content.res.Resources r1 = r16.getResources()
            java.lang.String r4 = r1.getString(r5)
        L_0x00bb:
            r3.setContentText(r4)
            r3.setShowWhen(r11)
            r3.setAutoCancel(r10)
            android.graphics.Bitmap r1 = r15.getBitmap(r16)
            r3.setLargeIcon(r1)
            int r1 = android.os.Build.VERSION.SDK_INT
            r4 = -1
            r5 = 26
            if (r1 >= r5) goto L_0x00d8
            r3.setPriority(r9)
            r3.setDefaults(r4)
        L_0x00d8:
            r1 = 0
            boolean r6 = com.miui.earthquakewarning.utils.Utils.supportMap(r16)
            if (r6 == 0) goto L_0x0193
            android.content.Intent r6 = new android.content.Intent     // Catch:{ Exception -> 0x018a }
            java.lang.String r7 = "com.miui.earthquake.detail"
            r6.<init>(r7)     // Catch:{ Exception -> 0x018a }
            android.os.Bundle r1 = new android.os.Bundle     // Catch:{ Exception -> 0x018b }
            r1.<init>()     // Catch:{ Exception -> 0x018b }
            java.lang.String r7 = "magnitude"
            float r8 = r19.getMagnitude()     // Catch:{ Exception -> 0x018b }
            r1.putFloat(r7, r8)     // Catch:{ Exception -> 0x018b }
            java.lang.String r7 = "longitude"
            com.miui.earthquakewarning.model.LocationModel r8 = r19.getEpiLocation()     // Catch:{ Exception -> 0x018b }
            double r8 = r8.getLongitude()     // Catch:{ Exception -> 0x018b }
            r1.putDouble(r7, r8)     // Catch:{ Exception -> 0x018b }
            java.lang.String r7 = "latitude"
            com.miui.earthquakewarning.model.LocationModel r8 = r19.getEpiLocation()     // Catch:{ Exception -> 0x018b }
            double r8 = r8.getLatitude()     // Catch:{ Exception -> 0x018b }
            r1.putDouble(r7, r8)     // Catch:{ Exception -> 0x018b }
            java.lang.String r7 = "distance"
            float r8 = r19.getDistance()     // Catch:{ Exception -> 0x018b }
            double r8 = (double) r8     // Catch:{ Exception -> 0x018b }
            r1.putDouble(r7, r8)     // Catch:{ Exception -> 0x018b }
            java.lang.String r7 = "myLongitude"
            com.miui.earthquakewarning.model.LocationModel r8 = r19.getLocation()     // Catch:{ Exception -> 0x018b }
            double r8 = r8.getLongitude()     // Catch:{ Exception -> 0x018b }
            r1.putDouble(r7, r8)     // Catch:{ Exception -> 0x018b }
            java.lang.String r7 = "myLatitude"
            com.miui.earthquakewarning.model.LocationModel r8 = r19.getLocation()     // Catch:{ Exception -> 0x018b }
            double r8 = r8.getLatitude()     // Catch:{ Exception -> 0x018b }
            r1.putDouble(r7, r8)     // Catch:{ Exception -> 0x018b }
            java.lang.String r7 = "intensity"
            float r8 = r19.getIntensity()     // Catch:{ Exception -> 0x018b }
            r1.putFloat(r7, r8)     // Catch:{ Exception -> 0x018b }
            java.lang.String r7 = "epicenter"
            com.miui.earthquakewarning.model.LocationModel r8 = r19.getEpiLocation()     // Catch:{ Exception -> 0x018b }
            java.lang.String r8 = r8.getPlace()     // Catch:{ Exception -> 0x018b }
            r1.putString(r7, r8)     // Catch:{ Exception -> 0x018b }
            java.lang.String r7 = "startTime"
            long r8 = r19.getStartTime()     // Catch:{ Exception -> 0x018b }
            r1.putLong(r7, r8)     // Catch:{ Exception -> 0x018b }
            java.lang.String r7 = "warnTime"
            int r8 = r19.getCountdown()     // Catch:{ Exception -> 0x018b }
            r1.putInt(r7, r8)     // Catch:{ Exception -> 0x018b }
            java.lang.String r7 = "isAll"
            r1.putBoolean(r7, r11)     // Catch:{ Exception -> 0x018b }
            java.lang.String r7 = r19.getSignatureText()     // Catch:{ Exception -> 0x018b }
            boolean r7 = android.text.TextUtils.isEmpty(r7)     // Catch:{ Exception -> 0x018b }
            if (r7 != 0) goto L_0x0181
            android.content.res.Resources r7 = r16.getResources()     // Catch:{ Exception -> 0x018b }
            r8 = 2131756039(0x7f100407, float:1.9142974E38)
            java.lang.Object[] r9 = new java.lang.Object[r10]     // Catch:{ Exception -> 0x018b }
            java.util.List r12 = r19.getSignature()     // Catch:{ Exception -> 0x018b }
            r9[r11] = r12     // Catch:{ Exception -> 0x018b }
            java.lang.String r7 = r7.getString(r8, r9)     // Catch:{ Exception -> 0x018b }
            java.lang.String r8 = "signature"
            r1.putString(r8, r7)     // Catch:{ Exception -> 0x018b }
        L_0x0181:
            r6.putExtras(r1)     // Catch:{ Exception -> 0x018b }
            java.lang.String r1 = "com.miui.securityadd"
            r6.setPackage(r1)     // Catch:{ Exception -> 0x018b }
            goto L_0x019a
        L_0x018a:
            r6 = r1
        L_0x018b:
            java.lang.String r1 = "EarthquakeManager"
            java.lang.String r7 = "can not find detail page"
            android.util.Log.e(r1, r7)
            goto L_0x019a
        L_0x0193:
            android.content.Intent r6 = new android.content.Intent
            java.lang.Class<com.miui.earthquakewarning.ui.EarthquakeWarningListActivity> r1 = com.miui.earthquakewarning.ui.EarthquakeWarningListActivity.class
            r6.<init>(r0, r1)
        L_0x019a:
            if (r6 != 0) goto L_0x01a3
            android.content.Intent r6 = new android.content.Intent
            java.lang.Class<com.miui.earthquakewarning.ui.EarthquakeWarningListActivity> r1 = com.miui.earthquakewarning.ui.EarthquakeWarningListActivity.class
            r6.<init>(r0, r1)
        L_0x01a3:
            r1 = 268435456(0x10000000, float:2.5243549E-29)
            r6.setFlags(r1)
            java.util.Random r1 = new java.util.Random
            r1.<init>()
            int r1 = r1.nextInt()
            android.app.PendingIntent r1 = android.app.PendingIntent.getActivity(r0, r1, r6, r11)
            r3.setContentIntent(r1)
            android.app.Notification r1 = r3.build()
            b.b.o.a.a.a((android.app.Notification) r1, (boolean) r10)
            java.lang.String r3 = "notification"
            java.lang.Object r3 = r0.getSystemService(r3)
            android.app.NotificationManager r3 = (android.app.NotificationManager) r3
            int r6 = android.os.Build.VERSION.SDK_INT
            if (r6 >= r5) goto L_0x01cd
            r1.defaults = r4
        L_0x01cd:
            android.content.res.Resources r0 = r16.getResources()
            r4 = 2131756996(0x7f1007c4, float:1.9144915E38)
            java.lang.String r0 = r0.getString(r4)
            r4 = 5
            b.b.c.j.v.a((android.app.NotificationManager) r3, (java.lang.String) r2, (java.lang.String) r0, (int) r4)
            java.lang.Long r0 = new java.lang.Long
            r2 = r15
            long r4 = r2.eventId
            r0.<init>(r4)
            int r0 = r0.intValue()
            r3.notify(r0, r1)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.earthquakewarning.EarthquakeWarningManager.showLowEarthquakeWarning(android.content.Context, java.lang.String, java.lang.String, com.miui.earthquakewarning.model.UserQuakeItem, java.lang.String):void");
    }

    public void closeEarthquakeWarning() {
        Utils.toggle(false);
        unsetTopicForPush(this.mContext, String.valueOf(Utils.getPreviousAreaCode()));
    }

    public void closeEarthquakeWarning(Context context) {
        Utils.toggle(false);
        unsetTopicForPush(this.mContext, String.valueOf(Utils.getPreviousAreaCode()));
        context.stopService(new Intent(context, EarthquakeWarningService.class));
    }

    public void openEarthquakeWarning(Context context) {
        Utils.toggle(true);
        NotificationUtil.setGpsStatus(this.mContext);
        UpdateAreaCodeManager.getInstance().uploadSettings(this.mContext);
        requestSignature();
        context.startService(new Intent(context, EarthquakeWarningService.class));
    }

    public void registerForPush(Context context) {
        b.a(context).a();
    }

    public void requestSignature() {
        if (Utils.isEarthquakeWarningOpen()) {
            Log.d(TAG, "request Signature each day");
            RequestSignatureTask requestSignatureTask = new RequestSignatureTask();
            requestSignatureTask.setListener(new RequestSignatureTask.Listener() {
                public void onPost(SignatureReuslt signatureReuslt) {
                }
            });
            requestSignatureTask.execute(new String[]{"ICL"});
        }
    }

    public void searchSafePlace(Context context) {
        StringBuilder sb;
        String str;
        if (x.h(context, AUTONAVI_PACKAGENAME)) {
            try {
                Intent intent = new Intent();
                intent.setAction("android.intent.action.VIEW");
                intent.addCategory(Constants.System.CATEGORY_DEFALUT);
                intent.setData(Uri.parse(AUTONAVI_SEARTH_URI));
                intent.setFlags(268435456);
                context.startActivity(intent);
                return;
            } catch (Exception e) {
                e = e;
                sb = new StringBuilder();
                str = "start amap error: ";
            }
        } else if (x.h(context, BAIDUMAP_PACKAGENAME)) {
            try {
                Intent intent2 = new Intent();
                intent2.setData(Uri.parse(BAIDUMAP_SEARTH_URI));
                intent2.setFlags(268435456);
                context.startActivity(intent2);
                return;
            } catch (Exception e2) {
                e = e2;
                sb = new StringBuilder();
                str = "start baidumap error: ";
            }
        } else {
            Toast.makeText(context, context.getString(R.string.ew_safe_place_no_maps_tips), 0).show();
            return;
        }
        sb.append(str);
        sb.append(e);
        Log.e(TAG, sb.toString());
    }

    public void setTopicForPush(Context context, String str) {
        b.a(context).a(context, MD5Util.encode(str + Constants.UUID_CITY_CODE), (String) null);
    }

    public void showWarningInfo(Context context, JSONObject jSONObject) {
        if (!Build.IS_INTERNATIONAL_BUILD) {
            int previousAreaCode = Utils.getPreviousAreaCode();
            int previousAreaDistricCode = Utils.getPreviousAreaDistricCode();
            if (!Utils.isEarthquakeWarningOpen()) {
                if (previousAreaCode > 0) {
                    Utils.setPreviousAreaCode(0);
                    Utils.setPreviousAreaDistrictCode(0);
                    unsetTopicForPush(context, String.valueOf(previousAreaCode));
                }
                context.stopService(new Intent(context, EarthquakeWarningService.class));
                AnalyticHelper.trackPushActionModuleClick(AnalyticHelper.PUSH_ERROR_NOT_OPEN);
                return;
            }
            NotificationUtil.setGpsStatus(context);
            UserQuakeItem parseQuake = parseQuake(jSONObject);
            if (parseQuake.getType() > 1 || parseQuake.getType() < 0) {
                Log.i(TAG, "show failed : push_error_illgal_type");
                AnalyticHelper.trackPushActionModuleClick(AnalyticHelper.PUSH_ERROR_ILLGAL_TYPE);
                return;
            }
            requestSignatureBefore(context, parseQuake.getChannel(), previousAreaDistricCode, parseQuake);
        }
    }

    public void unsetTopicForPush(Context context, String str) {
        b.a(context).c(context, MD5Util.encode(str + Constants.UUID_CITY_CODE), (String) null);
    }
}
