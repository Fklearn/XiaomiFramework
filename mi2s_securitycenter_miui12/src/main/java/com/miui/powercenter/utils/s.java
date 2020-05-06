package com.miui.powercenter.utils;

import android.content.Context;
import android.content.res.Resources;
import b.b.c.j.f;
import com.miui.maml.folme.AnimatedProperty;
import com.miui.securitycenter.R;
import com.miui.superpower.b.b;
import com.xiaomi.stat.d;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import miui.cloud.CloudPushConstants;

public class s {
    public static int a(Context context, int i, int i2, int i3) {
        float f = 0.13f;
        if (i3 != 0) {
            if (i3 == 1) {
                f = 0.2f;
            } else if (i3 == 2) {
                f = 1.2f;
            }
        }
        if (i2 == 0) {
            i2 = o.e(context);
        }
        if (i == 0) {
            i = o.c(context);
        }
        return (int) ((((float) ((i * i2) * 60)) / ((f.c(context) ? b.b() : b.a()).floatValue() * 100.0f)) * f);
    }

    public static String a(int i) {
        return "<font color=\"#439DF8\">" + String.format(Locale.getDefault(), "%d", new Object[]{Integer.valueOf(i)}) + "</font>";
    }

    public static String a(int i, int i2) {
        return String.format("%02d:%02d", new Object[]{Integer.valueOf(i), Integer.valueOf(i2)});
    }

    public static String a(long j) {
        long j2;
        long j3;
        long j4;
        long j5;
        if (j == 0) {
            return "0ms";
        }
        if (j >= 86400000) {
            j2 = j / 86400000;
            j %= 86400000;
        } else {
            j2 = 0;
        }
        if (j >= 3600000) {
            j3 = j / 3600000;
            j %= 3600000;
        } else {
            j3 = 0;
        }
        if (j >= 60000) {
            j4 = j / 60000;
            j %= 60000;
        } else {
            j4 = 0;
        }
        if (j >= 1000) {
            j5 = j / 1000;
            j %= 1000;
        } else {
            j5 = 0;
        }
        StringBuilder sb = new StringBuilder();
        if (j2 > 0) {
            sb.append(j2);
            sb.append("d");
        }
        if (j3 > 0) {
            sb.append(j3);
            sb.append(AnimatedProperty.PROPERTY_NAME_H);
        }
        if (j4 > 0) {
            sb.append(j4);
            sb.append(d.V);
        }
        if (j5 > 0) {
            sb.append(j5);
            sb.append(CloudPushConstants.WATERMARK_TYPE.SUBSCRIPTION);
        }
        if (j > 0) {
            sb.append(j);
            sb.append(d.H);
        }
        return sb.toString();
    }

    public static String a(Context context, double d2) {
        Object[] objArr;
        String str;
        if (d2 > 1000000.0d) {
            objArr = new Object[]{Float.valueOf(((float) ((int) (d2 / 1000.0d))) / 1000.0f)};
            str = "%.2f MB";
        } else if (d2 > 1024.0d) {
            objArr = new Object[]{Float.valueOf(((float) ((int) (d2 / 10.0d))) / 100.0f)};
            str = "%.2f KB";
        } else {
            objArr = new Object[]{Integer.valueOf((int) d2)};
            str = "%d bytes";
        }
        return String.format(str, objArr);
    }

    public static String a(Context context, long j) {
        int d2 = d(j);
        int e = e(j);
        int f = f(j);
        if (d2 > 0) {
            if (e > 0) {
                return context.getResources().getString(R.string.pc_main_power_manager_day_hour, new Object[]{Integer.valueOf(d2), Integer.valueOf(e)});
            }
            return context.getResources().getQuantityString(R.plurals.pc_main_power_manager_day, d2, new Object[]{Integer.valueOf(d2)});
        } else if (e > 0) {
            if (f > 0) {
                return context.getResources().getString(R.string.pc_main_power_manager_hour_minute, new Object[]{Integer.valueOf(e), Integer.valueOf(f)});
            }
            return context.getResources().getQuantityString(R.plurals.pc_main_power_manager_hour, e, new Object[]{Integer.valueOf(e)});
        } else if (f < 0) {
            return "";
        } else {
            return context.getResources().getQuantityString(R.plurals.pc_main_power_manager_minute, f, new Object[]{Integer.valueOf(f)});
        }
    }

    public static String a(Context context, long j, long j2) {
        int i;
        Resources resources;
        Object[] objArr;
        String a2 = a((int) (j / 60000));
        int i2 = (int) (j2 / 3600000);
        int f = f(j2);
        if (i2 > 0) {
            String a3 = a(i2);
            if (f > 0) {
                String a4 = a(f);
                return context.getResources().getString(R.string.pc_main_battery_text_hour_minute, new Object[]{a2, a3, a4});
            }
            resources = context.getResources();
            i = R.string.pc_main_battery_text_hour;
            objArr = new Object[]{a2, a3};
        } else if (f <= 0) {
            return "";
        } else {
            String a5 = a(f);
            resources = context.getResources();
            i = R.string.pc_main_battery_text_minute;
            objArr = new Object[]{a2, a5};
        }
        return resources.getString(i, objArr);
    }

    public static String b(long j) {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date(j));
    }

    public static String b(Context context, double d2) {
        int i;
        int i2;
        int i3;
        int i4;
        Object[] objArr;
        String string;
        StringBuilder sb = new StringBuilder();
        int floor = (int) Math.floor(d2 / 1000.0d);
        if (floor > 86400) {
            i = floor / 86400;
            floor -= 86400 * i;
        } else {
            i = 0;
        }
        if (floor > 3600) {
            i2 = floor / 3600;
            floor -= i2 * 3600;
        } else {
            i2 = 0;
        }
        if (floor > 60) {
            i3 = floor / 60;
            floor -= i3 * 60;
        } else {
            i3 = 0;
        }
        if (i > 0) {
            string = context.getString(R.string.battery_history_days, new Object[]{Integer.valueOf(i), Integer.valueOf(i2), Integer.valueOf(i3), Integer.valueOf(floor)});
        } else if (i2 > 0) {
            string = context.getString(R.string.battery_history_hours, new Object[]{Integer.valueOf(i2), Integer.valueOf(i3), Integer.valueOf(floor)});
        } else {
            if (i3 > 0) {
                i4 = R.string.battery_history_minutes;
                objArr = new Object[]{Integer.valueOf(i3), Integer.valueOf(floor)};
            } else {
                i4 = R.string.battery_history_seconds;
                objArr = new Object[]{Integer.valueOf(floor)};
            }
            string = context.getString(i4, objArr);
        }
        sb.append(string);
        return sb.toString();
    }

    public static String b(Context context, long j) {
        int i = (int) (j / 3600000);
        int f = f(j);
        if (i > 0) {
            if (f > 0) {
                return context.getResources().getString(R.string.power_main_battery_last_hour_minute, new Object[]{Integer.valueOf(i), Integer.valueOf(f)});
            }
            return context.getResources().getQuantityString(R.plurals.power_main_battery_last_hour, i, new Object[]{Integer.valueOf(i)});
        } else if (f > 0) {
            return context.getResources().getQuantityString(R.plurals.power_main_battery_last_minute, f, new Object[]{Integer.valueOf(f)});
        } else {
            return context.getResources().getQuantityString(R.plurals.power_main_battery_last_minute, 0, new Object[]{0});
        }
    }

    public static String c(long j) {
        int d2 = d(j);
        float e = ((float) ((d2 * 24) + e(j))) + (((float) f(j)) / 60.0f);
        return String.format(Locale.getDefault(), "%.1f", new Object[]{Float.valueOf(e)});
    }

    public static String c(Context context, long j) {
        int i = (int) (j / 3600000);
        int f = f(j);
        if (i > 0) {
            String a2 = a(i);
            if (f > 0) {
                String a3 = a(f);
                return context.getResources().getString(R.string.pc_main_sub_battery_text_hour_minute, new Object[]{a2, a3});
            }
            return context.getResources().getQuantityString(R.plurals.pc_main_sub_battery_text_hour, i, new Object[]{a2});
        } else if (f <= 0) {
            return "";
        } else {
            String a4 = a(f);
            return context.getResources().getQuantityString(R.plurals.pc_main_sub_battery_text_minute, f, new Object[]{a4});
        }
    }

    private static int d(long j) {
        return (int) (j / 86400000);
    }

    public static String d(Context context, long j) {
        int d2 = d(j);
        int e = e(j);
        int f = f(j);
        String str = "";
        if (u.a(context.getResources().getConfiguration().locale.getLanguage())) {
            if (f > 0 && d2 == 0) {
                str = str + context.getResources().getQuantityString(R.plurals.power_center_battery_minute, f, new Object[]{Integer.valueOf(f)});
            }
            if (e > 0) {
                str = str + context.getResources().getQuantityString(R.plurals.power_center_battery_hour, e, new Object[]{Integer.valueOf(e)});
            }
            if (d2 <= 0) {
                return str;
            }
            return str + context.getResources().getQuantityString(R.plurals.power_center_battery_day, d2, new Object[]{Integer.valueOf(d2)});
        }
        if (d2 > 0) {
            str = str + context.getResources().getQuantityString(R.plurals.power_center_battery_day, d2, new Object[]{Integer.valueOf(d2)});
        }
        if (e > 0) {
            str = str + context.getResources().getQuantityString(R.plurals.power_center_battery_hour, e, new Object[]{Integer.valueOf(e)});
        }
        if (f <= 0 || d2 != 0) {
            return str;
        }
        return str + context.getResources().getQuantityString(R.plurals.power_center_battery_minute, f, new Object[]{Integer.valueOf(f)});
    }

    private static int e(long j) {
        return (int) ((j / 3600000) % 24);
    }

    private static int f(long j) {
        return (int) ((j / 60000) % 60);
    }
}
