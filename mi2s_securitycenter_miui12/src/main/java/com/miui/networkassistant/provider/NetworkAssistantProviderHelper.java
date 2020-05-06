package com.miui.networkassistant.provider;

import android.content.Context;
import android.net.Uri;
import android.os.RemoteException;
import android.support.v4.content.FileProvider;
import android.util.Log;
import com.miui.activityutil.o;
import com.miui.networkassistant.config.Constants;
import com.miui.networkassistant.config.SimUserInfo;
import com.miui.networkassistant.service.ITrafficManageBinder;
import com.miui.networkassistant.traffic.statistic.LeisureTrafficHelper;
import com.miui.networkassistant.utils.BitmapUtil;
import com.miui.networkassistant.utils.FormatBytesUtil;
import com.miui.securitycenter.R;
import java.io.File;

public class NetworkAssistantProviderHelper {
    private static final String TAG = "NAProvider";

    private NetworkAssistantProviderHelper() {
    }

    public static String[] getBillTextInfo(Context context, SimUserInfo simUserInfo, String str) {
        String[] strArr = new String[4];
        if (simUserInfo.isBillPackageEffective()) {
            strArr[0] = context.getString(R.string.main_bill_remained);
            strArr[1] = String.valueOf(((float) simUserInfo.getBillPackageRemained()) / 100.0f);
            strArr[2] = context.getString(R.string.yuan);
        }
        try {
            strArr[3] = imageProvider(context, true, simUserInfo.getSlotNum(), R.drawable.status_bar_bill, str);
        } catch (Exception unused) {
            Log.e(TAG, "getBillTextInfo FileProvider Exception");
        }
        return strArr;
    }

    public static String[] getNoSimIcon(Context context, String str) {
        String[] strArr = new String[2];
        try {
            strArr[0] = imageProvider(context, false, 0, R.drawable.status_bar_no_sim, str);
            strArr[1] = imageProvider(context, true, 0, R.drawable.status_bar_bill, str);
        } catch (Exception unused) {
            Log.e(TAG, "getNoSimIcon FileProvider Exception");
        }
        return strArr;
    }

    public static long[] getTrafficBaseInfo(SimUserInfo simUserInfo, ITrafficManageBinder iTrafficManageBinder) {
        long[] jArr = new long[3];
        try {
            if (!simUserInfo.isLeisureDataUsageEffective() || !LeisureTrafficHelper.isLeisureTime(simUserInfo)) {
                if (!simUserInfo.isNotLimitCardEnable()) {
                    jArr[0] = iTrafficManageBinder.getCurrentMonthTotalPackage(simUserInfo.getSlotNum());
                }
                jArr[1] = iTrafficManageBinder.getCorrectedNormalMonthDataUsageUsed(simUserInfo.getSlotNum());
            } else {
                jArr[0] = iTrafficManageBinder.getCurrentMonthTotalPackage(simUserInfo.getSlotNum());
                jArr[1] = iTrafficManageBinder.getCorrectedNormalAndLeisureMonthTotalUsed(simUserInfo.getSlotNum())[1];
            }
            jArr[2] = iTrafficManageBinder.getTodayDataUsageUsed(simUserInfo.getSlotNum());
            long j = 0;
            if (jArr[0] >= 0) {
                j = jArr[0];
            }
            jArr[0] = j;
        } catch (RemoteException e) {
            Log.i(TAG, "query data usage ", e);
        }
        return jArr;
    }

    public static String[] getTrafficTextInfo(Context context, SimUserInfo simUserInfo, long[] jArr, String str) {
        Context context2 = context;
        String[] strArr = {"", "", "", "", "", o.f2309a, ""};
        long j = jArr[0];
        long j2 = jArr[1];
        long j3 = jArr[2];
        int slotNum = simUserInfo.getSlotNum();
        strArr[5] = toUriIntent("miui.intent.action.NETWORKASSISTANT_ENTRANCE", slotNum);
        strArr[4] = String.valueOf(simUserInfo.getDataUsageCorrectedTime());
        boolean isBrandSetted = simUserInfo.isBrandSetted();
        int i = R.drawable.status_bar_traffic_used;
        if (!isBrandSetted) {
            strArr[5] = toUriIntent(Constants.App.ACTION_NETWORK_ASSISTANT_MONTH_PACKAGE_SETTING, slotNum);
            strArr[0] = context2.getString(R.string.pref_data_usage_not_set);
            strArr[1] = "--";
            strArr[2] = FormatBytesUtil.getMBString(context);
            i = R.drawable.status_bar_no_sim;
        } else if (simUserInfo.isNotLimitCardEnable()) {
            String[] trafficFormat = FormatBytesUtil.trafficFormat(context2, j2);
            strArr[0] = context2.getString(R.string.main_month_total_used);
            strArr[1] = trafficFormat[0];
            strArr[2] = trafficFormat[1];
        } else if (simUserInfo.isTotalDataUsageSetted()) {
            String[] trafficFormat2 = FormatBytesUtil.trafficFormat(context2, j3);
            strArr[0] = context2.getString(R.string.main_today_used);
            strArr[1] = trafficFormat2[0];
            strArr[2] = trafficFormat2[1];
        } else {
            long j4 = j - j2;
            if (j4 >= 0) {
                String[] trafficFormat3 = FormatBytesUtil.trafficFormat(context2, j4);
                strArr[0] = context2.getString(R.string.main_primary_message_traffic_remain);
                strArr[1] = trafficFormat3[0];
                strArr[2] = trafficFormat3[1];
            } else {
                String[] trafficFormat4 = FormatBytesUtil.trafficFormat(context2, j2 - j);
                strArr[0] = context2.getString(R.string.main_primary_message_traffic_overlimit);
                strArr[1] = trafficFormat4[0];
                strArr[2] = trafficFormat4[1];
                i = R.drawable.status_bar_over;
            }
        }
        try {
            strArr[3] = imageProvider(context2, false, slotNum, i, str);
        } catch (Exception unused) {
            Log.e(TAG, "getTrafficTextInfo FileProvider Exception");
        }
        return strArr;
    }

    private static String imageProvider(Context context, boolean z, int i, int i2, String str) {
        String format = String.format(z ? "tmp_bill%s.png" : "tmp_traffic%s.png", new Object[]{Integer.valueOf(i)});
        File file = new File(context.getCacheDir(), "na_files");
        BitmapUtil.saveDrawableResToFile(context, file, format, i2);
        Uri uriForFile = FileProvider.getUriForFile(context, ProviderConstant.AUTHORITY_FILE, new File(file, format));
        context.grantUriPermission(str, uriForFile, 1);
        return uriForFile.toString();
    }

    public static String toUriIntent(String str, int i) {
        return String.format("intent:#Intent;action=%s;package=com.miui.securitycenter;i.sim_slot_num_tag=%d;end", new Object[]{str, Integer.valueOf(i)});
    }
}
