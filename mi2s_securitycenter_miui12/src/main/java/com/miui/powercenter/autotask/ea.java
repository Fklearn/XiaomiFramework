package com.miui.powercenter.autotask;

import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import com.miui.powercenter.autotask.AutoTask;
import com.miui.powercenter.utils.u;
import com.miui.securitycenter.R;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import miui.app.AlertDialog;

public class ea {
    private static String a(int i) {
        return String.format("%02d:%02d", new Object[]{Integer.valueOf((i / 60) % 24), Integer.valueOf(i % 60)});
    }

    public static String a(Context context, AutoTask autoTask) {
        if (!TextUtils.isEmpty(autoTask.getName())) {
            return autoTask.getName();
        }
        String a2 = a(autoTask);
        return a(context, a2, autoTask.getCondition(a2));
    }

    private static String a(Context context, AutoTask autoTask, int i) {
        int i2;
        List<String> operationNames = autoTask.getOperationNames();
        ArrayList arrayList = new ArrayList();
        ArrayList arrayList2 = new ArrayList();
        ArrayList arrayList3 = new ArrayList();
        for (String next : operationNames) {
            Integer num = (Integer) autoTask.getOperation(next);
            if ("brightness".equals(next)) {
                arrayList2.add(next);
            } else if (!"gps".equals(next) ? num.intValue() != 0 : num.intValue() != AutoTask.GPS_OFF) {
                arrayList.add(next);
            } else {
                arrayList3.add(next);
            }
        }
        StringBuilder sb = new StringBuilder();
        if (!arrayList.isEmpty()) {
            String string = context.getString(R.string.auto_task_operation_open_string);
            StringBuilder sb2 = new StringBuilder();
            i2 = 0;
            for (int i3 = 0; i3 < arrayList.size() && i2 < i; i3++) {
                a(context, sb2, i3, (String) arrayList.get(i3));
                i2++;
            }
            sb.append(String.format(string, new Object[]{sb2.toString()}));
        } else {
            i2 = 0;
        }
        if (i2 < i && !arrayList2.isEmpty()) {
            String string2 = context.getString(R.string.auto_task_operation_modify_string);
            StringBuilder sb3 = new StringBuilder();
            int i4 = i2;
            for (int i5 = 0; i5 < arrayList2.size() && i4 < i; i5++) {
                a(context, sb3, i5, (String) arrayList2.get(i5));
                i4++;
            }
            if (!TextUtils.isEmpty(sb)) {
                sb.append(context.getString(R.string.auto_task_operation_string_split));
            }
            sb.append(String.format(string2, new Object[]{sb3.toString()}));
            i2 = i4;
        }
        if (i2 < i && !arrayList3.isEmpty()) {
            String string3 = context.getString(R.string.auto_task_operation_close_string);
            StringBuilder sb4 = new StringBuilder();
            int i6 = i2;
            for (int i7 = 0; i7 < arrayList3.size() && i6 < i; i7++) {
                a(context, sb4, i7, (String) arrayList3.get(i7));
                i6++;
            }
            if (!TextUtils.isEmpty(sb)) {
                sb.append(context.getString(R.string.auto_task_operation_string_split));
            }
            sb.append(String.format(string3, new Object[]{sb4.toString()}));
            i2 = i6;
        }
        int size = arrayList.size() + arrayList2.size() + arrayList3.size();
        if (i2 >= i && size > i) {
            sb.append(context.getString(R.string.auto_task_operation_string_etc));
        }
        return sb.toString();
    }

    public static String a(Context context, String str, Object obj) {
        String string;
        Locale locale;
        Object[] objArr;
        if ("battery_level_down".equals(str)) {
            string = context.getString(R.string.auto_atsk_condition_battery_level_text_down);
            locale = Locale.getDefault();
            objArr = new Object[]{u.a(context, ((Integer) obj).intValue())};
        } else if ("battery_level_up".equals(str)) {
            string = context.getString(R.string.auto_atsk_condition_battery_level_text_up);
            locale = Locale.getDefault();
            objArr = new Object[]{u.a(context, ((Integer) obj).intValue())};
        } else if ("hour_minute".equals(str)) {
            return String.format(context.getString(R.string.auto_task_condition_time_text), new Object[]{a(((Integer) obj).intValue())});
        } else if (!"hour_minute_duration".equals(str)) {
            return "";
        } else {
            String string2 = context.getString(R.string.auto_task_condition_time_duration_text);
            AutoTask.b bVar = new AutoTask.b(((Integer) obj).intValue());
            return String.format(string2, new Object[]{a(bVar.f6675a), a(bVar.f6676b)});
        }
        return String.format(locale, string, objArr);
    }

    public static String a(AutoTask autoTask) {
        List<String> conditionNames = autoTask.getConditionNames();
        return conditionNames.isEmpty() ? "" : conditionNames.get(0);
    }

    private static StringBuilder a(Context context, StringBuilder sb, int i, String str) {
        if (i > 0) {
            sb.append(context.getString(R.string.auto_task_operation_string_comma));
        }
        sb.append(X.a(context, str));
        return sb;
    }

    public static void a(Context context, DialogInterface.OnClickListener onClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.auto_task_edit_adandon_button_text);
        builder.setMessage(R.string.auto_task_edit_abandon_change);
        builder.setPositiveButton(R.string.auto_task_edit_adandon_button_text, onClickListener);
        builder.setNegativeButton(R.string.power_dialog_cancel, onClickListener);
        builder.show();
    }

    public static String b(Context context, AutoTask autoTask) {
        return a(context, autoTask, Integer.MAX_VALUE);
    }

    public static String c(Context context, AutoTask autoTask) {
        return a(context, autoTask, 2);
    }
}
