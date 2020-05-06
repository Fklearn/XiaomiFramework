package com.miui.powercenter.utils;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.os.UserHandle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import b.b.o.g.e;
import com.miui.networkassistant.config.Constants;
import com.miui.powercenter.d.c;
import com.miui.powercenter.provider.PowerSaveService;
import com.miui.powercenter.y;
import com.miui.securitycenter.R;
import com.miui.superpower.b.k;
import com.xiaomi.stat.MiStat;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import miui.util.FeatureParser;
import miui.util.IOUtils;

public class o {

    /* renamed from: a  reason: collision with root package name */
    private static final String f7315a = Build.DEVICE;

    private static int a(File file) {
        FileInputStream fileInputStream = null;
        try {
            FileInputStream fileInputStream2 = new FileInputStream(file);
            try {
                byte[] a2 = a(fileInputStream2);
                if (a2 != null) {
                    int parseInt = Integer.parseInt(new String(a2).trim());
                    IOUtils.closeQuietly(fileInputStream2);
                    return parseInt;
                }
                IOUtils.closeQuietly(fileInputStream2);
                return 0;
            } catch (Exception e) {
                e = e;
                fileInputStream = fileInputStream2;
                try {
                    Log.e("", "readIntegerValue", e);
                    IOUtils.closeQuietly(fileInputStream);
                    return 0;
                } catch (Throwable th) {
                    th = th;
                    IOUtils.closeQuietly(fileInputStream);
                    throw th;
                }
            } catch (Throwable th2) {
                th = th2;
                fileInputStream = fileInputStream2;
                IOUtils.closeQuietly(fileInputStream);
                throw th;
            }
        } catch (Exception e2) {
            e = e2;
            Log.e("", "readIntegerValue", e);
            IOUtils.closeQuietly(fileInputStream);
            return 0;
        }
    }

    public static String a() {
        FileReader fileReader;
        BufferedReader bufferedReader;
        BufferedReader bufferedReader2 = null;
        try {
            File file = new File("/sys/class/power_supply/usb/type");
            File file2 = new File("sys/class/power_supply/usb/real_type");
            if (!c() && file2.exists()) {
                file = file2;
            }
            fileReader = new FileReader(file);
            try {
                bufferedReader = new BufferedReader(fileReader);
            } catch (FileNotFoundException | IOException e) {
                e = e;
                try {
                    Log.e("PowerUtils", "getChargerType", e);
                    IOUtils.closeQuietly(bufferedReader2);
                    IOUtils.closeQuietly(fileReader);
                    return "";
                } catch (Throwable th) {
                    th = th;
                    IOUtils.closeQuietly(bufferedReader2);
                    IOUtils.closeQuietly(fileReader);
                    throw th;
                }
            }
            try {
                String readLine = bufferedReader.readLine();
                IOUtils.closeQuietly(bufferedReader);
                IOUtils.closeQuietly(fileReader);
                return readLine;
            } catch (FileNotFoundException e2) {
                BufferedReader bufferedReader3 = bufferedReader;
                e = e2;
                bufferedReader2 = bufferedReader3;
                Log.e("PowerUtils", "getChargerType", e);
                IOUtils.closeQuietly(bufferedReader2);
                IOUtils.closeQuietly(fileReader);
                return "";
            } catch (IOException e3) {
                BufferedReader bufferedReader4 = bufferedReader;
                e = e3;
                bufferedReader2 = bufferedReader4;
                Log.e("PowerUtils", "getChargerType", e);
                IOUtils.closeQuietly(bufferedReader2);
                IOUtils.closeQuietly(fileReader);
                return "";
            } catch (Throwable th2) {
                th = th2;
                bufferedReader2 = bufferedReader;
                IOUtils.closeQuietly(bufferedReader2);
                IOUtils.closeQuietly(fileReader);
                throw th;
            }
        } catch (FileNotFoundException e4) {
            e = e4;
            fileReader = null;
            Log.e("PowerUtils", "getChargerType", e);
            IOUtils.closeQuietly(bufferedReader2);
            IOUtils.closeQuietly(fileReader);
            return "";
        } catch (IOException e5) {
            e = e5;
            fileReader = null;
            Log.e("PowerUtils", "getChargerType", e);
            IOUtils.closeQuietly(bufferedReader2);
            IOUtils.closeQuietly(fileReader);
            return "";
        } catch (Throwable th3) {
            th = th3;
            fileReader = null;
            IOUtils.closeQuietly(bufferedReader2);
            IOUtils.closeQuietly(fileReader);
            throw th;
        }
    }

    public static void a(Activity activity) {
        Resources resources;
        try {
            resources = activity.getPackageManager().getResourcesForApplication(Constants.System.ANDROID_PACKAGE_NAME);
        } catch (Exception e) {
            Log.e("PowerUtils", "setPendingTransition: ", e);
            resources = null;
        }
        if (resources != null) {
            activity.overridePendingTransition(resources.getIdentifier("activity_close_enter", "anim", Constants.System.ANDROID_PACKAGE_NAME), resources.getIdentifier("activity_close_exit", "anim", Constants.System.ANDROID_PACKAGE_NAME));
        }
    }

    public static void a(Context context) {
        Settings.System.putInt(context.getContentResolver(), "haptic_feedback_enabled", 0);
    }

    public static void a(Context context, int i) {
        Settings.Secure.putInt(context.getContentResolver(), "location_mode", i);
    }

    public static void a(Context context, String str) {
        Intent intent = new Intent(context, PowerSaveService.class);
        if (str != null) {
            intent.setAction(str);
        }
        context.startService(intent);
    }

    public static void a(Context context, boolean z) {
        Bundle bundle = new Bundle();
        bundle.putBoolean("POWER_SAVE_MODE_OPEN", z);
        context.getContentResolver().call(Uri.parse("content://com.miui.powercenter.powersaver"), "changePowerMode", (String) null, bundle);
    }

    public static void a(Context context, boolean z, boolean z2) {
        Bundle bundle = new Bundle();
        bundle.putBoolean("POWER_SUPERSAVE_MODE_OPEN", z);
        bundle.putBoolean("POWER_SUPERSAVE_MODE_FROMUSER", z2);
        context.getContentResolver().call(Uri.parse("content://com.miui.powercenter.powersaver"), "changeSuperPowerMode", (String) null, bundle);
    }

    public static boolean a(Intent intent) {
        if (intent == null) {
            Log.i("PowerUtils", "isInCharging intent null");
            return false;
        }
        int intExtra = intent.getIntExtra("status", 1);
        return (intExtra == 2 || intExtra == 5) && intent.getIntExtra("plugged", 0) != 0;
    }

    /* JADX INFO: finally extract failed */
    private static byte[] a(FileInputStream fileInputStream) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] bArr = new byte[1024];
        while (true) {
            try {
                int read = fileInputStream.read(bArr, 0, 1024);
                if (read > 0) {
                    byteArrayOutputStream.write(bArr, 0, read);
                } else {
                    byte[] byteArray = byteArrayOutputStream.toByteArray();
                    IOUtils.closeQuietly(byteArrayOutputStream);
                    return byteArray;
                }
            } catch (Exception unused) {
                IOUtils.closeQuietly(byteArrayOutputStream);
                return null;
            } catch (Throwable th) {
                IOUtils.closeQuietly(byteArrayOutputStream);
                throw th;
            }
        }
    }

    public static void b(Context context) {
        Settings.System.putInt(context.getContentResolver(), "wakeup_for_keyguard_notification", 0);
    }

    public static boolean b() {
        String a2 = a();
        if (TextUtils.isEmpty(a2)) {
            return false;
        }
        return a2.regionMatches(true, 0, "USB_HVDCP", 0, 9) || a2.equals("USB_PD");
    }

    public static int c(Context context) {
        if (FeatureParser.hasFeature("battery_capacity_typ", 3)) {
            Log.i("PowerUtils", "use feature battery typ capacity");
            return Integer.valueOf(FeatureParser.getString("battery_capacity_typ")).intValue();
        }
        Log.i("PowerUtils", "Device name:" + f7315a);
        if ("HM2014011".equals(f7315a) || "armani".equals(f7315a) || "HM2013022".equals(f7315a) || "HM2013023".equals(f7315a)) {
            return 2000;
        }
        if (!"HM2014501".equals(f7315a)) {
            if ("lcsh92_wet_jb9".equals(f7315a) || "lcsh92_wet_xm_td".equals(f7315a) || "lcsh92_wet_tdd".equals(f7315a)) {
                return 3100;
            }
            if ("gucci".equals(f7315a) || "dior".equals(f7315a)) {
                return 3200;
            }
            if (!"HM2014811".equals(f7315a) && !"HM2014812".equals(f7315a) && !"HM2014813".equals(f7315a) && !"HM2014817".equals(f7315a) && !"HM2014818".equals(f7315a) && !"HM2014819".equals(f7315a) && !"lte26007".equals(f7315a)) {
                if ("ferrari".equals(f7315a) || "rolex".equals(f7315a)) {
                    return 3120;
                }
                if ("scorpio".equals(f7315a)) {
                    return 4070;
                }
                if ("prada".equals(f7315a) || "markw".equals(f7315a) || "mido".equals(f7315a)) {
                    return 4100;
                }
                if (miui.os.Build.IS_MIFOUR) {
                    return 3080;
                }
                int i = 1000;
                try {
                    i = (int) ((Double) e.a((Object) Class.forName("com.android.internal.os.PowerProfile").getConstructor(new Class[]{Context.class}).newInstance(new Object[]{context}), "getBatteryCapacity", (Class<?>[]) null, new Object[0])).doubleValue();
                    Log.w("PowerUtils", "should not use this capacity value " + i);
                    return i;
                } catch (ClassNotFoundException | IllegalAccessException | InstantiationException | NoSuchMethodException | InvocationTargetException e) {
                    Log.e("PowerUtils", "PowerProfile", e);
                    return i;
                }
            }
        }
        return 2200;
    }

    public static boolean c() {
        if (!FeatureParser.hasFeature("is_xiaomi", 1)) {
            return false;
        }
        boolean z = FeatureParser.getBoolean("is_xiaomi", true);
        Log.i("PowerUtils", "has feature phone typ: " + z);
        return z;
    }

    public static int d(Context context) {
        int i;
        if ("nikel".equals(f7315a) || "hermes".equals(f7315a)) {
            return a(new File("/sys/bus/platform/drivers/battery_meter/battery_meter/FG_Current")) / 10;
        }
        if (Build.VERSION.SDK_INT >= 21) {
            i = 0;
            try {
                i = ((Integer) e.a((Object) (BatteryManager) context.getSystemService("batterymanager"), "getIntProperty", (Class<?>[]) new Class[]{Integer.TYPE}, 2)).intValue();
            } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
                Log.e("PowerUtils", "getBatteryCurrentNow", e);
            }
        } else {
            File file = new File("/sys/class/power_supply/battery/current_now");
            if (!file.exists()) {
                file = new File("/sys/class/power_supply/max170xx_battery/current_now");
            }
            i = a(file);
        }
        return "hennessy".equals(f7315a) ? i : i / 1000;
    }

    public static boolean d() {
        if (!FeatureParser.hasFeature("support_hangup_while_screen_off", 1)) {
            return false;
        }
        boolean z = FeatureParser.getBoolean("support_hangup_while_screen_off", true);
        Log.i("PowerUtils", "support hangup while screen off: " + z);
        return z;
    }

    public static int e(Context context) {
        Intent registerReceiver = context.registerReceiver((BroadcastReceiver) null, new IntentFilter("android.intent.action.BATTERY_CHANGED"));
        if (registerReceiver == null) {
            Log.i("PowerUtils", "getBatteryPercent null");
            return 0;
        }
        return (int) (((float) (registerReceiver.getIntExtra(MiStat.Param.LEVEL, -1) * 100)) / ((float) registerReceiver.getIntExtra("scale", -1)));
    }

    public static int f(Context context) {
        Intent registerReceiver = context.registerReceiver((BroadcastReceiver) null, new IntentFilter("android.intent.action.BATTERY_CHANGED"));
        if (registerReceiver != null) {
            return registerReceiver.getIntExtra("plugged", 0);
        }
        Log.i("PowerUtils", "getBatteryPluggedType null");
        return 0;
    }

    public static int g(Context context) {
        try {
            return Settings.Secure.getInt(context.getContentResolver(), "location_mode");
        } catch (Settings.SettingNotFoundException e) {
            Log.e("PowerUtils", "", e);
            return 0;
        }
    }

    public static String h(Context context) {
        try {
            Bundle call = context.getContentResolver().call(Uri.parse("content://com.miui.powerkeeper.configure/GlobalFeatureTable"), "GlobalFeatureTablequery", (String) null, (Bundle) null);
            return (call == null || !call.containsKey("userConfigureStatus")) ? "" : call.getString("userConfigureStatus");
        } catch (Exception | IllegalArgumentException e) {
            Log.e("PowerUtils", "getHideModeStatus", e);
            return "";
        }
    }

    public static String i(Context context) {
        int i;
        ArrayList arrayList = new ArrayList(7);
        arrayList.add(context.getResources().getString(R.string.power_save_ps_close_setting_sync));
        if (g.b() && g.a()) {
            arrayList.add(context.getResources().getString(R.string.power_save_ps_close_setting_5gnet));
        }
        if (Build.VERSION.SDK_INT <= 28) {
            arrayList.add(context.getResources().getString(R.string.power_save_ps_close_setting_gps));
        }
        if (!c.a()) {
            arrayList.add(context.getResources().getString(R.string.power_save_ps_close_setting_haptic));
        }
        arrayList.add(context.getResources().getString(R.string.power_save_ps_close_setting_pickupwakeup));
        arrayList.add(context.getResources().getString(R.string.power_save_ps_close_setting_fingeraod));
        int size = arrayList.size();
        if (size == 3) {
            i = R.string.power_save_ps_close_setting_3;
        } else if (size == 4) {
            i = R.string.power_save_ps_close_setting_4;
        } else if (size == 5) {
            i = R.string.power_save_ps_close_setting_5;
        } else if (size == 6) {
            i = R.string.power_save_ps_close_setting_6;
        } else if (size != 7) {
            return "";
        } else {
            i = R.string.power_save_ps_close_setting_7;
        }
        return context.getResources().getString(i, arrayList.toArray(new String[0]));
    }

    public static boolean j(Context context) {
        return Settings.System.getInt(context.getContentResolver(), "haptic_feedback_enabled", -1) != 0;
    }

    public static boolean k(Context context) {
        return a(context.registerReceiver((BroadcastReceiver) null, new IntentFilter("android.intent.action.BATTERY_CHANGED")));
    }

    public static boolean l(Context context) {
        return Settings.System.getInt(context.getContentResolver(), "POWER_SAVE_MODE_OPEN", 0) != 0;
    }

    public static boolean m(Context context) {
        return Settings.System.getInt(context.getContentResolver(), "power_supersave_mode_open", 0) != 0;
    }

    public static boolean n(Context context) {
        return FeatureParser.getBoolean("support_extreme_battery_saver", false) && !k.o(context) && UserHandle.myUserId() == 0;
    }

    public static boolean o(Context context) {
        return Settings.System.getInt(context.getContentResolver(), "wakeup_for_keyguard_notification", -1) != 0;
    }

    public static void p(Context context) {
        a(context, (String) null);
    }

    public static void q(Context context) {
        y.d(r(context) * 60);
    }

    private static int r(Context context) {
        int[] intArray = context.getResources().getIntArray(R.array.pc_time_choice_items);
        if (intArray.length >= 2) {
            return intArray[intArray.length - 2];
        }
        return 0;
    }
}
