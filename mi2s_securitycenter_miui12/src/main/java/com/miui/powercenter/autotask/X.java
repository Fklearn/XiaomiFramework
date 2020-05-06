package com.miui.powercenter.autotask;

import android.content.Context;
import android.content.DialogInterface;
import com.miui.activityutil.o;
import com.miui.powercenter.utils.n;
import com.miui.securitycenter.R;
import java.util.Arrays;
import miui.app.AlertDialog;
import miuix.preference.DropDownPreference;
import miuix.preference.TextPreference;

public class X {

    public interface a {
        void a(String str);
    }

    public static int a(String str) {
        if ("airplane_mode".equals(str)) {
            return R.drawable.ic_airplane;
        }
        if ("internet".equals(str)) {
            return R.drawable.ic_mobile_data;
        }
        if ("wifi".equals(str)) {
            return R.drawable.ic_wifi;
        }
        if ("mute".equals(str)) {
            return R.drawable.ic_mute;
        }
        if ("vibration".equals(str)) {
            return R.drawable.ic_vibration;
        }
        if ("bluetooth".equals(str)) {
            return R.drawable.ic_bluetooth;
        }
        if ("auto_brightness".equals(str)) {
            return R.drawable.ic_auto_brightness;
        }
        if ("brightness".equals(str)) {
            return R.drawable.ic_brightness;
        }
        if ("gps".equals(str)) {
            return R.drawable.ic_gps_task;
        }
        if ("synchronization".equals(str)) {
            return R.drawable.ic_sync;
        }
        if ("auto_clean_memory".equals(str)) {
            return R.drawable.ic_memory_clean;
        }
        if ("save_mode".equals(str)) {
            return R.drawable.ic_battery_n;
        }
        throw new IllegalArgumentException("unknown icon " + str);
    }

    private static String a(Context context, int i) {
        if (i == 0) {
            return context.getString(R.string.deep_clean_never_memory_clean);
        }
        if (i == -1) {
            return context.getString(R.string.auto_task_operation_no_op);
        }
        return context.getResources().getQuantityString(R.plurals.deep_clean_auto_memory_clean, i, new Object[]{Integer.valueOf(i)});
    }

    public static String a(Context context, String str) {
        int i;
        if ("airplane_mode".equals(str)) {
            i = R.string.auto_task_operation_airplane_mode;
        } else if ("internet".equals(str)) {
            i = R.string.auto_task_operation_mobile_data;
        } else if ("wifi".equals(str)) {
            i = R.string.auto_task_operation_wifi;
        } else if ("mute".equals(str)) {
            i = R.string.auto_task_operation_mute;
        } else if ("vibration".equals(str)) {
            i = R.string.auto_task_operation_vibration;
        } else if ("bluetooth".equals(str)) {
            i = R.string.auto_task_operation_bluetooth;
        } else if ("auto_brightness".equals(str)) {
            i = R.string.auto_task_operation_auto_brightness;
        } else if ("brightness".equals(str)) {
            i = R.string.auto_task_operation_brightness;
        } else if ("gps".equals(str)) {
            i = R.string.auto_task_operation_gps;
        } else if ("synchronization".equals(str)) {
            i = R.string.auto_task_operation_sync;
        } else if ("auto_clean_memory".equals(str)) {
            i = R.string.auto_task_operation_memory_clean_lockscreen;
        } else if ("save_mode".equals(str)) {
            i = R.string.power_save_title_text;
        } else {
            throw new IllegalArgumentException("unknown operation name " + str);
        }
        return context.getString(i);
    }

    public static void a(Context context, AutoTask autoTask, a aVar) {
        new D(context, autoTask).a(aVar);
    }

    public static void a(Context context, AutoTask autoTask, String str, a aVar) {
        Integer num = (Integer) autoTask.getOperation(str);
        int i = num == null ? 2 : (!"gps".equals(str) ? num.intValue() != 1 : num.intValue() != 3) ? 1 : 0;
        C0473b.a(context, a(context, str), new String[]{context.getString(R.string.auto_task_operation_open), context.getString(R.string.auto_task_operation_close), context.getString(R.string.auto_task_operation_no_op)}, i, new V(i, autoTask, str, aVar));
    }

    public static void a(Context context, AutoTask autoTask, String str, Object obj, a aVar) {
        if (b(str)) {
            int i = obj.equals(o.f2309a) ? 1 : obj.equals(o.f2310b) ? 0 : 2;
            if (i == 2) {
                autoTask.removeOperation(str);
            } else {
                if ("gps".equals(str)) {
                    i = i == 1 ? 3 : AutoTask.GPS_OFF;
                }
                autoTask.setOperation(str, Integer.valueOf(i));
            }
            if (aVar != null) {
                aVar.a(str);
            }
        } else if ("auto_clean_memory".equals(str)) {
            int[] intArray = context.getResources().getIntArray(R.array.pc_time_choice_items);
            int[] copyOf = Arrays.copyOf(intArray, intArray.length + 1);
            copyOf[intArray.length] = -1;
            int i2 = copyOf[Integer.valueOf(obj.toString()).intValue()];
            if (i2 == -1) {
                autoTask.removeOperation("auto_clean_memory");
            } else {
                autoTask.setOperation("auto_clean_memory", Integer.valueOf(i2));
            }
            if (aVar != null) {
                aVar.a("auto_clean_memory");
            }
        }
    }

    public static void a(Context context, AutoTask autoTask, String str, DropDownPreference dropDownPreference) {
        Integer num = (Integer) autoTask.getOperation(str);
        if (b(str)) {
            int i = num == null ? 2 : (!"gps".equals(str) ? num.intValue() != 1 : num.intValue() != 3) ? 1 : 0;
            String[] strArr = {context.getString(R.string.auto_task_operation_open), context.getString(R.string.auto_task_operation_close), context.getString(R.string.auto_task_operation_no_op)};
            String[] strArr2 = {o.f2309a, o.f2310b, "2"};
            dropDownPreference.a((CharSequence[]) strArr);
            dropDownPreference.b((CharSequence[]) strArr2);
            dropDownPreference.a(i);
        } else if ("auto_clean_memory".equals(str)) {
            int[] intArray = context.getResources().getIntArray(R.array.pc_time_choice_items);
            int[] copyOf = Arrays.copyOf(intArray, intArray.length + 1);
            int i2 = -1;
            copyOf[intArray.length] = -1;
            if (autoTask.hasOperation("auto_clean_memory")) {
                i2 = ((Integer) autoTask.getOperation("auto_clean_memory")).intValue();
            }
            int i3 = 0;
            while (true) {
                if (i3 >= copyOf.length) {
                    i3 = 0;
                    break;
                } else if (copyOf[i3] == i2) {
                    break;
                } else {
                    i3++;
                }
            }
            String[] strArr3 = new String[copyOf.length];
            String[] strArr4 = new String[copyOf.length];
            for (int i4 = 0; i4 < strArr3.length; i4++) {
                strArr3[i4] = a(context, copyOf[i4]);
                strArr4[i4] = String.valueOf(i4);
            }
            dropDownPreference.a((CharSequence[]) strArr3);
            dropDownPreference.b((CharSequence[]) strArr4);
            dropDownPreference.a(i3);
        }
    }

    public static void a(DropDownPreference dropDownPreference, AutoTask autoTask, String str) {
        String string;
        Object operation = autoTask.getOperation(str);
        if (operation == null) {
            string = "";
        } else {
            int i = 0;
            if (b(str)) {
                Integer num = (Integer) operation;
                if (!"gps".equals(str) ? num.intValue() != 0 : num.intValue() != AutoTask.GPS_OFF) {
                    i = 1;
                }
                dropDownPreference.a(i);
                return;
            } else if ("auto_clean_memory".equals(str)) {
                Integer num2 = (Integer) operation;
                Context context = dropDownPreference.getContext();
                if (num2.intValue() == 0) {
                    string = context.getString(R.string.deep_clean_never_memory_clean);
                } else {
                    dropDownPreference.b(context.getResources().getQuantityString(R.plurals.deep_clean_auto_memory_clean, num2.intValue(), new Object[]{num2}));
                    return;
                }
            } else {
                return;
            }
        }
        dropDownPreference.b(string);
    }

    public static void a(TextPreference textPreference, AutoTask autoTask, String str) {
        String str2;
        String string;
        Object operation = autoTask.getOperation(str);
        if (operation == null) {
            string = "";
        } else if (b(str)) {
            Integer num = (Integer) operation;
            boolean equals = "gps".equals(str);
            int i = R.string.auto_task_operation_close;
            if (!equals ? num.intValue() != 0 : num.intValue() != AutoTask.GPS_OFF) {
                i = R.string.auto_task_operation_open;
            }
            textPreference.a(i);
            return;
        } else {
            if ("auto_clean_memory".equals(str)) {
                Integer num2 = (Integer) operation;
                Context context = textPreference.getContext();
                if (num2.intValue() == 0) {
                    string = context.getString(R.string.deep_clean_never_memory_clean);
                } else {
                    str2 = context.getResources().getQuantityString(R.plurals.deep_clean_auto_memory_clean, num2.intValue(), new Object[]{num2});
                }
            } else if ("brightness".equals(str)) {
                str2 = ((((Integer) operation).intValue() * 100) / n.a(textPreference.getContext()).e()) + "%";
            } else {
                return;
            }
            textPreference.a(str2);
            return;
        }
        textPreference.a(string);
    }

    public static void b(Context context, AutoTask autoTask, a aVar) {
        int[] intArray = context.getResources().getIntArray(R.array.pc_time_choice_items);
        int[] copyOf = Arrays.copyOf(intArray, intArray.length + 1);
        int i = -1;
        copyOf[intArray.length] = -1;
        if (autoTask.hasOperation("auto_clean_memory")) {
            i = ((Integer) autoTask.getOperation("auto_clean_memory")).intValue();
        }
        int i2 = 0;
        while (true) {
            if (i2 >= copyOf.length) {
                i2 = 0;
                break;
            } else if (copyOf[i2] == i) {
                break;
            } else {
                i2++;
            }
        }
        String[] strArr = new String[copyOf.length];
        for (int i3 = 0; i3 < strArr.length; i3++) {
            strArr[i3] = a(context, copyOf[i3]);
        }
        new AlertDialog.Builder(context).setTitle(context.getString(R.string.deep_clean_memory_clean_title)).setSingleChoiceItems(strArr, i2, new W(copyOf, autoTask, aVar)).setNegativeButton(context.getString(R.string.cancel), (DialogInterface.OnClickListener) null).show();
    }

    public static boolean b(String str) {
        return "airplane_mode".equals(str) || "wifi".equals(str) || "mute".equals(str) || "vibration".equals(str) || "bluetooth".equals(str) || "auto_brightness".equals(str) || "gps".equals(str) || "synchronization".equals(str) || "internet".equals(str) || "save_mode".equals(str);
    }
}
