package com.miui.powercenter.a;

import android.content.Context;
import android.database.Cursor;
import com.miui.analytics.AnalyticsUtil;
import com.miui.powercenter.autotask.AutoTask;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import miui.io.IoUtils;

public class b {
    public static void a() {
        HashMap hashMap = new HashMap(1);
        hashMap.put("click_done", "change_premiss");
        a("auto_change_action", (Map<String, String>) hashMap);
    }

    /* JADX INFO: finally extract failed */
    public static void a(Context context) {
        int i;
        int i2;
        int i3;
        int i4;
        String str;
        String str2;
        int i5;
        int i6;
        int i7;
        int i8;
        int i9;
        int i10;
        int i11;
        int i12;
        int i13;
        int i14;
        int i15;
        int i16;
        int i17;
        String str3;
        int i18;
        int i19;
        int i20;
        int i21;
        int i22;
        int i23;
        int i24;
        int i25;
        int i26;
        int i27;
        int i28;
        int i29;
        int i30;
        int i31;
        int i32;
        int i33;
        int i34;
        int i35;
        String str4;
        int i36;
        int i37;
        int i38;
        List<AutoTask> list;
        List<AutoTask> initAutoTaskList = AutoTask.getInitAutoTaskList();
        Cursor query = context.getContentResolver().query(AutoTask.CONTENT_URI, AutoTask.QUERY_COLUMNS, (String) null, (String[]) null, (String) null);
        String str5 = "deleted";
        if (query != null) {
            try {
                str3 = "off";
                if (query.moveToFirst()) {
                    String str6 = str5;
                    str4 = str6;
                    String str7 = str3;
                    int i39 = 0;
                    i18 = 0;
                    int i40 = 0;
                    i36 = 0;
                    i16 = 0;
                    i15 = 0;
                    i14 = 0;
                    i13 = 0;
                    i12 = 0;
                    i9 = 0;
                    i35 = 0;
                    i34 = 0;
                    i33 = 0;
                    i32 = 0;
                    i31 = 0;
                    i30 = 0;
                    i29 = 0;
                    i28 = 0;
                    i27 = 0;
                    i26 = 0;
                    i25 = 0;
                    i24 = 0;
                    while (true) {
                        AutoTask autoTask = new AutoTask(query);
                        int i41 = i16;
                        int i42 = i39 + 1;
                        if (autoTask.getId() == 1 && initAutoTaskList.size() > 0) {
                            str6 = b(autoTask, initAutoTaskList.get(0));
                        }
                        if (autoTask.getId() == 2) {
                            i38 = i42;
                            if (initAutoTaskList.size() > 1) {
                                str4 = b(autoTask, initAutoTaskList.get(1));
                            }
                        } else {
                            i38 = i42;
                        }
                        int i43 = i12;
                        String str8 = str7;
                        if (autoTask.getId() > ((long) initAutoTaskList.size())) {
                            i40++;
                            if (autoTask.getEnabled()) {
                                i36++;
                            }
                            i24 = 1;
                        }
                        if (!autoTask.getEnabled()) {
                            list = initAutoTaskList;
                            i12 = i43;
                            i16 = i41;
                            str7 = str8;
                        } else {
                            i18++;
                            for (String next : autoTask.getConditionNames()) {
                                List<AutoTask> list2 = initAutoTaskList;
                                if ("hour_minute_duration".equals(next)) {
                                    i41++;
                                } else if ("hour_minute".equals(next)) {
                                    i43++;
                                } else if ("battery_level_down".equals(next)) {
                                    i9++;
                                }
                                initAutoTaskList = list2;
                            }
                            list = initAutoTaskList;
                            List<String> operationNames = autoTask.getOperationNames();
                            if (operationNames.size() == 1) {
                                i15++;
                            } else if (operationNames.size() > 1) {
                                i14++;
                            }
                            i13 += operationNames.size();
                            for (String next2 : operationNames) {
                                if ("airplane_mode".equals(next2)) {
                                    i35++;
                                } else if ("mute".equals(next2)) {
                                    i34++;
                                } else if ("gps".equals(next2)) {
                                    i33++;
                                } else if ("internet".equals(next2)) {
                                    i32++;
                                } else if ("wifi".equals(next2)) {
                                    i31++;
                                } else if ("synchronization".equals(next2)) {
                                    i30++;
                                } else if ("vibration".equals(next2)) {
                                    i29++;
                                } else if ("bluetooth".equals(next2)) {
                                    i28++;
                                } else if ("auto_brightness".equals(next2)) {
                                    i27++;
                                } else if ("brightness".equals(next2)) {
                                    i26++;
                                } else if ("auto_clean_memory".equals(next2)) {
                                    i25++;
                                }
                            }
                            str7 = "on";
                            i12 = i43;
                            i16 = i41;
                        }
                        if (!query.moveToNext()) {
                            break;
                        }
                        i39 = i38;
                        initAutoTaskList = list;
                    }
                    i37 = i40;
                    str3 = str7;
                    str5 = str6;
                    i19 = i38;
                } else {
                    str4 = str5;
                    i37 = 0;
                    i19 = 0;
                    i18 = 0;
                    i36 = 0;
                    i16 = 0;
                    i15 = 0;
                    i14 = 0;
                    i13 = 0;
                    i12 = 0;
                    i9 = 0;
                    i35 = 0;
                    i34 = 0;
                    i33 = 0;
                    i32 = 0;
                    i31 = 0;
                    i30 = 0;
                    i29 = 0;
                    i28 = 0;
                    i27 = 0;
                    i26 = 0;
                    i25 = 0;
                    i24 = 0;
                }
                IoUtils.closeQuietly(query);
                i3 = i37;
                str2 = str5;
                i2 = i36;
                str = str4;
                i22 = i35;
                i21 = i34;
                i20 = i33;
                i17 = i32;
                i11 = i31;
                i10 = i30;
                i = i29;
                i8 = i28;
                i7 = i27;
                i6 = i26;
                i5 = i25;
                i4 = i24;
            } catch (Throwable th) {
                IoUtils.closeQuietly(query);
                throw th;
            }
        } else {
            str3 = "none";
            str2 = str5;
            str = str2;
            i = 0;
            i22 = 0;
            i21 = 0;
            i20 = 0;
            i19 = 0;
            i18 = 0;
            i17 = 0;
            i16 = 0;
            i15 = 0;
            i14 = 0;
            i13 = 0;
            i12 = 0;
            i11 = 0;
            i10 = 0;
            i9 = 0;
            i8 = 0;
            i7 = 0;
            i6 = 0;
            i5 = 0;
            i4 = 0;
            i3 = 0;
            i2 = 0;
        }
        a("auto_toggle_total", str3);
        if (i19 > 0) {
            i23 = i10;
            a("auto_num", (long) i19);
        } else {
            i23 = i10;
        }
        if (i18 > 0) {
            a("auto_num_on", (long) i18);
        }
        if (i12 > 0) {
            a("auto_num_on_timing", (long) i12);
        }
        if (i9 > 0) {
            a("auto_num_on_low_power", (long) i9);
        }
        if (i16 > 0) {
            a("auto_num_on_timing_to_timing", (long) i16);
        }
        if (i15 > 0) {
            a("auto_num_on_single", (long) i15);
        }
        if (i14 > 0) {
            a("auto_num_on_multi", (long) i14);
        }
        if (i13 > 0) {
            a("auto_on_action_num", (long) i13);
        }
        if (i22 > 0) {
            a("auto_on_action_flight_mode", (long) i22);
        }
        if (i21 > 0) {
            a("auto_on_action_mute", (long) i21);
        }
        if (i20 > 0) {
            a("auto_on_action_gps", (long) i20);
        }
        if (i17 > 0) {
            a("auto_on_action_data", (long) i17);
        }
        if (i11 > 0) {
            a("auto_on_action_wlan", (long) i11);
        }
        if (i23 > 0) {
            a("auto_on_action_sync", (long) i23);
        }
        if (i > 0) {
            a("auto_on_action_vibrate", (long) i);
        }
        int i44 = i8;
        if (i44 > 0) {
            a("auto_on_action_bluetooth", (long) i44);
        }
        int i45 = i7;
        if (i45 > 0) {
            a("auto_on_action_abrightness", (long) i45);
        }
        int i46 = i6;
        if (i46 > 0) {
            a("auto_on_action_brightness", (long) i46);
        }
        int i47 = i5;
        if (i47 > 0) {
            a("auto_on_action_ls_cleanram", (long) i47);
        }
        a("auto_toggle_task1", str2);
        a("auto_toggle_task2", str);
        b("auto_toggle_custom", (long) i4);
        int i48 = i3;
        if (i48 > 0) {
            b("auto_custom_num", (long) i48);
        }
        b("auto_custom_on_num", (long) i2);
    }

    private static void a(String str) {
        HashMap hashMap = new HashMap(1);
        hashMap.put("module_click", str);
        a("auto_homepage_action", (Map<String, String>) hashMap);
    }

    private static void a(String str, long j) {
        AnalyticsUtil.recordCalculateEvent("powercenter_autotask", str, j, (Map<String, String>) null);
    }

    private static void a(String str, String str2) {
        AnalyticsUtil.recordStringPropertyEvent("powercenter_autotask", str, str2);
    }

    private static void a(String str, Map<String, String> map) {
        AnalyticsUtil.recordCountEvent("powercenter_autotask", str, map);
    }

    private static boolean a(AutoTask autoTask, AutoTask autoTask2) {
        return autoTask.conditionsEquals(autoTask2) && autoTask.operationsEquals(autoTask2);
    }

    private static String b(AutoTask autoTask, AutoTask autoTask2) {
        boolean z = !a(autoTask, autoTask2);
        return autoTask.getEnabled() ? z ? "changed_on" : "keep_on" : z ? "changed_off" : "keep_off";
    }

    public static void b() {
        HashMap hashMap = new HashMap(1);
        hashMap.put("click_done", "change_name");
        a("auto_change_action", (Map<String, String>) hashMap);
    }

    private static void b(String str, long j) {
        AnalyticsUtil.recordNumericEvent("powercenter_autotask", str, j);
    }

    public static void c() {
        HashMap hashMap = new HashMap(1);
        hashMap.put("click_done", "change_action");
        a("auto_change_action", (Map<String, String>) hashMap);
    }

    public static void d() {
        HashMap hashMap = new HashMap(1);
        hashMap.put("click_undone", "change_premiss");
        a("auto_change_action", (Map<String, String>) hashMap);
    }

    public static void e() {
        HashMap hashMap = new HashMap(1);
        hashMap.put("click_undone", "change_name");
        a("auto_change_action", (Map<String, String>) hashMap);
    }

    public static void f() {
        HashMap hashMap = new HashMap(1);
        hashMap.put("click_undone", "change_action");
        a("auto_change_action", (Map<String, String>) hashMap);
    }

    public static void g() {
        HashMap hashMap = new HashMap(1);
        hashMap.put("finish_or_not", "finish");
        a("auto_change_action", (Map<String, String>) hashMap);
    }

    public static void h() {
        HashMap hashMap = new HashMap(1);
        hashMap.put("finish_or_not", "not_finish");
        a("auto_change_action", (Map<String, String>) hashMap);
    }

    public static void i() {
        a("on_off");
    }

    public static void j() {
        a("task1");
    }

    public static void k() {
        a("task2");
    }

    public static void l() {
        a("task_custom");
    }

    public static void m() {
        a("new_task");
    }

    public static void n() {
        HashMap hashMap = new HashMap(1);
        hashMap.put("click_undone", "change_premiss");
        a("auto_new_action", (Map<String, String>) hashMap);
    }

    public static void o() {
        HashMap hashMap = new HashMap(1);
        hashMap.put("click_undone", "change_name");
        a("auto_new_action", (Map<String, String>) hashMap);
    }

    public static void p() {
        HashMap hashMap = new HashMap(1);
        hashMap.put("click_undone", "change_action");
        a("auto_new_action", (Map<String, String>) hashMap);
    }
}
