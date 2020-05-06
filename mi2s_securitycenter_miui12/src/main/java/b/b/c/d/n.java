package b.b.c.d;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.IntentFilter;
import android.os.Bundle;
import android.provider.MiuiSettings;
import android.provider.Settings;
import android.util.Log;
import com.miui.applicationlock.c.o;
import com.miui.gamebooster.m.C0381l;
import com.miui.networkassistant.config.Constants;
import com.miui.powercenter.quickoptimize.s;
import com.miui.powercenter.y;
import com.miui.securitycenter.Application;
import com.miui.securitycenter.h;
import com.miui.securitycenter.p;
import com.miui.securityscan.M;
import com.miui.support.provider.a;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import miui.cloud.external.CloudSysHelper;
import miui.os.Build;

public class n {

    /* renamed from: a  reason: collision with root package name */
    private static final HashMap<String, String> f1690a = new HashMap<>();

    /* renamed from: b  reason: collision with root package name */
    private static List<String> f1691b = new ArrayList();

    static {
        f1690a.put("http://sec-cdn.static.xiaomi.net/secStatic/icon/ziqidongguanli.png", "assets://img/ziqidongguanli.png");
        f1690a.put("https://sec-cdn.static.xiaomi.net/secStatic/proj/xiezai.png", "assets://img/xiezai.png");
    }

    public static String a(String str) {
        return f1690a.get(str);
    }

    public static List<String> a() {
        if (f1691b.size() == 0) {
            f1691b = o.f((Context) Application.d());
        }
        return f1691b;
    }

    public static boolean a(int i) {
        Application d2 = Application.d();
        if (i != 1) {
            if (i != 2) {
                if (i != 5) {
                    if (i != 6) {
                        if (i != 11) {
                            if (i != 23) {
                                if (i != 27) {
                                    if (i != 34) {
                                        if (i == 16) {
                                            try {
                                                if (Settings.System.getInt(Application.d().getContentResolver(), Constants.System.STATUS_BAR_SHOW_NETWORK_SPEED) == 1) {
                                                    return false;
                                                }
                                            } catch (Settings.SettingNotFoundException e) {
                                                e.printStackTrace();
                                            }
                                        } else if (i != 17) {
                                            switch (i) {
                                                case 42:
                                                    if (Build.IS_INTERNATIONAL_BUILD || !C0381l.b(Application.d())) {
                                                        return false;
                                                    }
                                                    break;
                                                case 44:
                                                    if (!b(Application.d())) {
                                                        return false;
                                                    }
                                                    break;
                                            }
                                        } else if (a.a(Application.d())) {
                                            return false;
                                        }
                                    } else if (!a((Context) Application.d())) {
                                        return false;
                                    }
                                } else if (!a(d2, "support_network_controller")) {
                                    return false;
                                }
                            } else if (e(Application.d())) {
                                return false;
                            }
                        } else if (d(Application.d())) {
                            return false;
                        }
                    } else if (h.f((Context) Application.d())) {
                        return false;
                    }
                } else if (h.k(Application.d())) {
                    return false;
                }
            } else if (h.i()) {
                return false;
            }
        } else if (a(d2.getContentResolver())) {
            return false;
        }
        return true;
    }

    public static boolean a(ContentResolver contentResolver) {
        return MiuiSettings.System.getBoolean(contentResolver, "extra_show_security_notification", false);
    }

    public static boolean a(Context context) {
        if (p.a() < 1) {
            return false;
        }
        f1691b = o.f((Context) Application.d());
        Log.i("appsArrayList Number", String.valueOf(f1691b.size()));
        return f1691b.size() >= 4;
    }

    public static boolean a(Context context, String str) {
        try {
            Bundle bundle = context.getPackageManager().getApplicationInfo("com.miui.securitycenter", 128).metaData;
            if (bundle != null) {
                return bundle.containsKey(str);
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean b(Context context) {
        s.a a2 = s.a();
        long currentTimeMillis = System.currentTimeMillis() - M.a(0);
        if (!(currentTimeMillis < 600000 && currentTimeMillis > 0) || !a2.f7256a || a2.f7257b != 0) {
            return c(context) >= y.e();
        }
        return false;
    }

    private static int c(Context context) {
        return context.registerReceiver((BroadcastReceiver) null, new IntentFilter("android.intent.action.BATTERY_CHANGED")).getIntExtra("temperature", 0) / 10;
    }

    private static boolean d(Context context) {
        return Settings.System.getInt(context.getContentResolver(), Constants.System.STATUS_BAR_SHOW_NETWORK_ASSISTANT, 0) == 1;
    }

    private static boolean e(Context context) {
        return CloudSysHelper.isXiaomiAccountPresent(context);
    }
}
