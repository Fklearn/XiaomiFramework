package com.miui.powercenter.utils;

import android.bluetooth.BluetoothAdapter;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.PowerManager;
import android.provider.Settings;
import android.util.Log;
import b.b.c.j.B;
import b.b.o.f.c.a;
import b.b.o.g.c;
import b.b.o.g.e;
import com.google.android.exoplayer2.util.MimeTypes;
import com.miui.maml.elements.AdvancedSlider;
import miui.util.FeatureParser;

public class n {

    /* renamed from: a  reason: collision with root package name */
    private static n f7311a;

    /* renamed from: b  reason: collision with root package name */
    private static final String f7312b = FeatureParser.getString("auto_brightness_optimize_strategy");

    /* renamed from: c  reason: collision with root package name */
    private static final int f7313c = o();

    /* renamed from: d  reason: collision with root package name */
    private Context f7314d;
    private PowerManager e;
    private int f = 255;
    private int g = 255;

    private n(Context context) {
        this.f7314d = context;
        this.e = (PowerManager) this.f7314d.getSystemService("power");
        this.g = a(this.e);
        this.f = this.g - f7313c;
    }

    private int a(PowerManager powerManager) {
        try {
            return ((Integer) e.a((Object) powerManager, "getMaximumScreenBrightnessSetting", (Class<?>[]) null, new Object[0])).intValue();
        } catch (Exception e2) {
            Log.e("PowerTaskManager", "getMaximumScreenBrightnessSetting exception: ", e2);
            return 255;
        }
    }

    public static synchronized n a(Context context) {
        n nVar;
        synchronized (n.class) {
            if (f7311a == null) {
                f7311a = new n(context.getApplicationContext());
            }
            nVar = f7311a;
        }
        return nVar;
    }

    private void a(Context context, boolean z) {
        PowerManager powerManager = (PowerManager) context.getSystemService("power");
        try {
            e.a((Object) powerManager, "setAutoBrightnessCustomizing", (Class<?>[]) new Class[]{Boolean.TYPE}, Boolean.valueOf(z));
        } catch (Exception e2) {
            Log.e("PowerTaskManager", "setPineconeAutoBrightnessCustomizing", e2);
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:19:0x0057  */
    /* JADX WARNING: Removed duplicated region for block: B:25:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private boolean a(android.content.Context r8, int r9) {
        /*
            r7 = this;
            java.lang.String r0 = "vibrator"
            java.lang.Object r0 = r8.getSystemService(r0)
            android.os.Vibrator r0 = (android.os.Vibrator) r0
            boolean r0 = r0.hasVibrator()
            java.lang.String r1 = "android.provider.MiuiSettings$System"
            r2 = 0
            r3 = 1
            r4 = 0
            java.lang.Class r1 = java.lang.Class.forName(r1)     // Catch:{ Exception -> 0x0042 }
            java.lang.String r5 = "VIBRATE_IN_NORMAL"
            java.lang.Class<java.lang.String> r6 = java.lang.String.class
            java.lang.Object r1 = b.b.o.g.e.a((java.lang.Class<?>) r1, (java.lang.String) r5, r6)     // Catch:{ Exception -> 0x0042 }
            java.lang.String r1 = (java.lang.String) r1     // Catch:{ Exception -> 0x0042 }
            r4 = 2
            if (r4 == r9) goto L_0x004b
            java.lang.String r9 = "android.provider.MiuiSettings$System"
            java.lang.Class r9 = java.lang.Class.forName(r9)     // Catch:{ Exception -> 0x0040 }
            java.lang.String r4 = "VIBRATE_IN_SILENT"
            java.lang.Class<java.lang.String> r5 = java.lang.String.class
            java.lang.Object r9 = b.b.o.g.e.a((java.lang.Class<?>) r9, (java.lang.String) r4, r5)     // Catch:{ Exception -> 0x0040 }
            java.lang.String r9 = (java.lang.String) r9     // Catch:{ Exception -> 0x0040 }
            if (r0 == 0) goto L_0x003f
            android.content.ContentResolver r4 = r8.getContentResolver()     // Catch:{ Exception -> 0x0040 }
            int r8 = android.provider.Settings.System.getInt(r4, r9, r3)     // Catch:{ Exception -> 0x0040 }
            if (r8 != r3) goto L_0x003f
            r2 = r3
        L_0x003f:
            return r2
        L_0x0040:
            r9 = move-exception
            goto L_0x0044
        L_0x0042:
            r9 = move-exception
            r1 = r4
        L_0x0044:
            java.lang.String r4 = "PowerTaskManager"
            java.lang.String r5 = "isVibrateEnabled exception: "
            android.util.Log.d(r4, r5, r9)
        L_0x004b:
            if (r0 == 0) goto L_0x0058
            android.content.ContentResolver r8 = r8.getContentResolver()
            int r8 = android.provider.Settings.System.getInt(r8, r1, r2)
            if (r8 != r3) goto L_0x0058
            r2 = r3
        L_0x0058:
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.powercenter.utils.n.a(android.content.Context, int):boolean");
    }

    private static int o() {
        try {
            return Resources.getSystem().getInteger(((Integer) e.a(Class.forName("android.miui.R$integer"), "android_config_screenBrightnessSettingMinimum")).intValue());
        } catch (Exception e2) {
            Log.e("PowerTaskManager", "getMinimumLight exception: ", e2);
            return 0;
        }
    }

    private boolean p() {
        k a2 = k.a(this.f7314d);
        long a3 = a2.a("content://com.miui.networkassistant.provider/datausage_status", "total_limit");
        return a3 != 0 && a2.a("content://com.miui.networkassistant.provider/datausage_status", "month_used") - a3 > 0;
    }

    public void a(int i) {
        int i2;
        StringBuilder sb;
        String str;
        ContentResolver contentResolver = this.f7314d.getContentResolver();
        if (!"pinecone".equals(f7312b) || !m()) {
            i2 = i + f7313c;
            Settings.System.putInt(contentResolver, "screen_brightness", i2);
            sb = new StringBuilder();
            str = "setBrightness brightnessValue 2:";
        } else {
            a(this.f7314d, true);
            i2 = i + f7313c;
            try {
                e.a((Object) this.e, "setBacklightBrightness", (Class<?>[]) new Class[]{Integer.TYPE}, Integer.valueOf(i2));
            } catch (Exception e2) {
                Log.e("PowerTaskManager", "setBacklightBrightness", e2);
            }
            a(this.f7314d, false);
            sb = new StringBuilder();
            str = "setBrightness brightnessValue 1:";
        }
        sb.append(str);
        sb.append(i2);
        Log.d("PowerTaskManager", sb.toString());
    }

    public void a(boolean z) {
        Settings.Global.putInt(this.f7314d.getContentResolver(), "airplane_mode_on", z ? 1 : 0);
        Intent intent = new Intent("android.intent.action.AIRPLANE_MODE");
        intent.putExtra(AdvancedSlider.STATE, z);
        this.f7314d.sendBroadcastAsUser(intent, B.d());
    }

    public boolean a() {
        return Settings.System.getInt(this.f7314d.getContentResolver(), "airplane_mode_on", 0) != 0;
    }

    public void b(int i) {
        Settings.System.putInt(this.f7314d.getContentResolver(), "screen_brightness_mode", i);
    }

    public void b(boolean z) {
        BluetoothAdapter defaultAdapter = BluetoothAdapter.getDefaultAdapter();
        if (z) {
            defaultAdapter.enable();
        } else {
            defaultAdapter.disable();
        }
    }

    public boolean b() {
        return BluetoothAdapter.getDefaultAdapter().isEnabled();
    }

    public int c() {
        float f2 = (float) Settings.System.getInt(this.f7314d.getContentResolver(), "screen_brightness", 100);
        Log.d("PowerTaskManager", "getBrightness: " + f2);
        return ((int) f2) - f7313c;
    }

    public void c(int i) {
        boolean p = p();
        if (i != 0) {
            if (i == 1) {
                if (!p || a.a(this.f7314d).a()) {
                    a.a(this.f7314d).a(true);
                    return;
                }
            } else {
                return;
            }
        }
        a.a(this.f7314d).a(false);
    }

    public void c(boolean z) {
        Integer num;
        int i = 0;
        if (FeatureParser.getBoolean("support_new_silentmode", false)) {
            if (z) {
                try {
                    num = (Integer) e.a(Class.forName("android.provider.MiuiSettings$SilenceMode"), Integer.TYPE, "getLastestQuietMode", (Class<?>[]) new Class[]{Context.class}, this.f7314d);
                } catch (Exception e2) {
                    Log.d("PowerTaskManager", "setMute exception: ", e2);
                    return;
                }
            } else {
                num = (Integer) e.a(Class.forName("android.provider.MiuiSettings$SilenceMode"), "NORMAL", Integer.TYPE);
            }
            e.a(Class.forName("android.provider.MiuiSettings$SilenceMode"), (Class) null, "setSilenceMode", (Class<?>[]) new Class[]{Context.class, Integer.TYPE, Uri.class}, this.f7314d, Integer.valueOf(num.intValue()), null);
            return;
        }
        AudioManager audioManager = (AudioManager) this.f7314d.getSystemService(MimeTypes.BASE_TYPE_AUDIO);
        if (!z) {
            i = 2;
        } else if (a(this.f7314d, 0)) {
            i = 1;
        }
        audioManager.setRingerMode(i);
    }

    public int d() {
        return Settings.System.getInt(this.f7314d.getContentResolver(), "screen_brightness_mode", 0);
    }

    public void d(int i) {
        long j = ((long) i) * 1000;
        if (j == 0) {
            j = 2147483647L;
        }
        Settings.System.putLong(this.f7314d.getContentResolver(), "screen_off_timeout", j);
    }

    public void d(boolean z) {
        ContentResolver.setMasterSyncAutomatically(z);
    }

    public int e() {
        return this.f;
    }

    public void e(boolean z) {
        Settings.System.putInt(this.f7314d.getContentResolver(), "sound_effects_enabled", z ? 1 : 0);
        AudioManager audioManager = (AudioManager) this.f7314d.getSystemService(MimeTypes.BASE_TYPE_AUDIO);
        if (z) {
            audioManager.loadSoundEffects();
        } else {
            audioManager.unloadSoundEffects();
        }
    }

    public void f(boolean z) {
        Settings.System.putInt(this.f7314d.getContentResolver(), "haptic_feedback_enabled", z ? 1 : 0);
    }

    public boolean f() {
        try {
            return ((Boolean) e.a((Object) (ConnectivityManager) this.f7314d.getSystemService("connectivity"), "getMobileDataEnabled", (Class<?>[]) null, new Object[0])).booleanValue();
        } catch (Exception e2) {
            Log.e("PowerTaskManager", "", e2);
            return false;
        }
    }

    public int g() {
        return (int) (Settings.System.getLong(this.f7314d.getContentResolver(), "screen_off_timeout", 2147483647L) / 1000);
    }

    public void g(boolean z) {
        c.a a2 = c.a.a("miui.util.AudioManagerHelper");
        Class cls = Boolean.TYPE;
        a2.b("setVibrateSetting", new Class[]{Context.class, cls, cls}, this.f7314d, Boolean.valueOf(z), false);
    }

    public void h(boolean z) {
        ((WifiManager) this.f7314d.getSystemService("wifi")).setWifiEnabled(z);
    }

    public boolean h() {
        return ContentResolver.getMasterSyncAutomatically();
    }

    public boolean i() {
        return Settings.System.getInt(this.f7314d.getContentResolver(), "sound_effects_enabled", 0) != 0;
    }

    public boolean j() {
        return Settings.System.getInt(this.f7314d.getContentResolver(), "haptic_feedback_enabled", 1) != 0;
    }

    public boolean k() {
        String str;
        try {
            str = (String) e.a(Class.forName("android.provider.MiuiSettings$System"), "VIBRATE_IN_NORMAL", String.class);
        } catch (Exception e2) {
            Log.e("PowerTaskManager", "getVibrateEnabled exception: ", e2);
            str = "vibrate_in_normal";
        }
        return Settings.System.getInt(this.f7314d.getContentResolver(), str, 0) != 0;
    }

    public boolean l() {
        return ((WifiManager) this.f7314d.getSystemService("wifi")).isWifiEnabled();
    }

    public boolean m() {
        return d() == 1;
    }

    public boolean n() {
        int i;
        if (!FeatureParser.getBoolean("support_new_silentmode", false)) {
            return ((AudioManager) this.f7314d.getSystemService(MimeTypes.BASE_TYPE_AUDIO)).getRingerMode() != 2;
        }
        try {
            i = ((Integer) e.a(Class.forName("android.provider.MiuiSettings$SilenceMode"), Integer.TYPE, "getZenMode", (Class<?>[]) new Class[]{Context.class}, this.f7314d)).intValue();
        } catch (Exception e2) {
            Log.d("PowerTaskManager", "isMuted: ", e2);
            i = 0;
        }
        return i != 0;
    }
}
