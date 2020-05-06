package com.miui.networkassistant.utils;

import android.content.Context;
import android.content.res.Resources;
import com.miui.securitycenter.R;

public class TextPrepareUtil {
    public static String getDualCardSuffix(Context context, int i) {
        return context.getString(i == 0 ? R.string.dual_setting_simcard1 : R.string.dual_setting_simcard2);
    }

    public static String getDualCardTitle(Context context, CharSequence charSequence, int i) {
        return String.format("%s-%s", new Object[]{charSequence, getDualCardSuffix(context, i)});
    }

    public static String getOperatorName(Context context, String str, int i) {
        int i2;
        int operator = TelephonyUtil.getOperator(str, i);
        Resources resources = context.getResources();
        if (operator == 0) {
            i2 = R.string.operator_name_cmcc;
        } else if (operator == 1) {
            i2 = R.string.operator_name_unicom;
        } else if (operator == 2) {
            i2 = R.string.operator_name_telcom;
        } else if (operator != 4) {
            return "";
        } else {
            i2 = R.string.operator_name_mi_mobile;
        }
        return resources.getString(i2);
    }

    public static String getOperatorNumber(Context context, String str, int i) {
        int i2;
        int operator = TelephonyUtil.getOperator(str, i);
        Resources resources = context.getResources();
        if (operator == 0) {
            i2 = R.string.operator_number_cmcc;
        } else if (operator == 1) {
            i2 = R.string.operator_number_unicom;
        } else if (operator != 2) {
            return operator != 4 ? "" : "400-922-3838";
        } else {
            i2 = R.string.operator_number_telcom;
        }
        return resources.getString(i2);
    }

    public static String getOperatorTips(Context context, String str, int i) {
        String operatorName = getOperatorName(context, str, i);
        String operatorNumber = getOperatorNumber(context, str, i);
        return String.format(context.getString(R.string.tips_message), new Object[]{operatorName, operatorNumber});
    }

    public static String getPreAdjustTimeTips(Context context, long j, long j2) {
        Resources resources = context.getResources();
        int i = (int) ((j2 - j) / 86400000);
        long todayTimeMillis = DateUtil.getTodayTimeMillis();
        int i2 = (j > todayTimeMillis ? 1 : (j == todayTimeMillis ? 0 : -1));
        if (i2 >= 0) {
            return resources.getString(R.string.adjust_traffic_carrier_summary_today, new Object[]{DateUtil.formatDataTime(j, DateUtil.getDateFormat(3))});
        } else if (i2 >= 0 || j < todayTimeMillis - 86400000) {
            return i <= 3 ? resources.getString(R.string.adjust_traffic_carrier_summary_two_day_before) : i <= 7 ? resources.getString(R.string.adjust_traffic_carrier_summary_oneweek_before) : i < 30 ? resources.getString(R.string.adjust_traffic_carrier_summary_more_oneweek_before) : resources.getString(R.string.adjust_traffic_carrier_summary_more_onemonth_before);
        } else {
            return resources.getString(R.string.adjust_traffic_carrier_summary_yesterday, new Object[]{DateUtil.formatDataTime(j, DateUtil.getDateFormat(3))});
        }
    }
}
