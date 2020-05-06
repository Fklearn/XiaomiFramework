package com.miui.applicationlock.c;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.ActivityOptions;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Build;
import android.os.Handler;
import android.os.Vibrator;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import b.b.c.j.B;
import b.b.c.j.d;
import b.b.c.j.g;
import b.b.c.j.v;
import b.b.c.j.y;
import b.b.o.b.a.a;
import b.b.o.g.e;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.upstream.HttpDataSource;
import com.google.android.exoplayer2.upstream.TransferListener;
import com.google.android.exoplayer2.util.Util;
import com.miui.applicationlock.C0312y;
import com.miui.applicationlock.MaskNotificationActivity;
import com.miui.applicationlock.a.h;
import com.miui.common.persistence.b;
import com.miui.gamebooster.globalgame.view.RoundedDrawable;
import com.miui.securitycenter.R;
import com.miui.securitycenter.p;
import com.miui.securityscan.f.c;
import com.miui.support.provider.f;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import miui.app.AlertDialog;
import miui.cloud.Constants;
import miui.security.SecurityManager;
import miui.util.ArraySet;

public class o {

    /* renamed from: a  reason: collision with root package name */
    public static final ArraySet<String> f3317a = new ArraySet<>();

    /* renamed from: b  reason: collision with root package name */
    public static final ArraySet<String> f3318b = new ArraySet<>();

    /* renamed from: c  reason: collision with root package name */
    public static final HashSet<String> f3319c = new HashSet<>();

    /* renamed from: d  reason: collision with root package name */
    public static final ArraySet<String> f3320d = new ArraySet<>();
    public static final DefaultBandwidthMeter e = new DefaultBandwidthMeter();
    private static boolean f = false;
    private static String g;

    static {
        f3317a.add("com.miui.klo.bugreport");
        f3317a.add("com.mfashiongallery.emag");
        f3317a.add("com.android.deskclock");
        f3317a.add("com.android.camera");
        f3317a.add("com.miui.antispam");
        f3317a.add("com.xiaomi.account");
        f3317a.add("com.miui.android.fashiongallery");
        f3320d.add("com.cleanmaster.security");
        f3320d.add("com.domobile.applock");
        f3320d.add("com.sp.protector.free");
        f3320d.add("com.martianmode.applock");
        f3320d.add("com.symantec.applock");
        f3320d.add("com.ivymobi.applock.free");
        f3320d.add("com.ushareit.lockit");
        f3320d.add("com.alpha.applock");
        f3320d.add("com.ehawk.antivirus.applock.wifi");
        f3320d.add("com.jb.security");
        f3318b.add("com.android.chrome");
        f3319c.add("com.android.calendar");
    }

    public static int a(int i) {
        if (i == 5) {
            return 30000;
        }
        if (i < 10 || i >= 30) {
            return (i < 30 || i >= 140) ? i >= 140 ? 86400000 : 0 : (int) (Math.pow(2.0d, ((double) (i - 30)) / 10.0d) * 30000.0d);
        }
        return 30000;
    }

    static long a(int i, int i2, int i3) {
        Calendar instance = Calendar.getInstance();
        instance.add(6, i);
        instance.set(11, i2);
        instance.set(12, i3);
        return instance.getTimeInMillis() - System.currentTimeMillis();
    }

    public static long a(String str) {
        return b.a("current_adinfo_id_" + str, 0);
    }

    public static Intent a(Context context, String str, String str2) {
        Intent intent = new Intent();
        intent.setComponent(new ComponentName(str, str2));
        if (!B.f()) {
            a(context, intent);
        }
        return intent;
    }

    public static Bitmap a(Bitmap bitmap, int i, int i2, int i3, int i4, boolean z) {
        try {
            Bitmap createBitmap = Bitmap.createBitmap(i, i2, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(createBitmap);
            Paint paint = new Paint();
            Rect rect = new Rect(0, 0, i, i2);
            RectF rectF = new RectF(rect);
            paint.setAntiAlias(true);
            canvas.drawARGB(0, 0, 0, 0);
            paint.setColor(i4);
            float f2 = (float) i3;
            canvas.drawRoundRect(rectF, f2, f2, paint);
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
            paint.setFilterBitmap(true);
            canvas.drawBitmap(bitmap, new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight()), rect, paint);
            if (z) {
                paint.setStrokeWidth(1.0f);
                paint.setStyle(Paint.Style.STROKE);
                paint.setColor(RoundedDrawable.DEFAULT_BORDER_COLOR);
                paint.setAlpha(76);
                canvas.drawCircle((float) (i / 2), (float) (i2 / 2), (float) (bitmap.getWidth() / 2), paint);
            }
            bitmap.recycle();
            return createBitmap;
        } catch (OutOfMemoryError unused) {
            return null;
        }
    }

    public static DataSource.Factory a(Context context, DefaultBandwidthMeter defaultBandwidthMeter) {
        return new DefaultDataSourceFactory(context, (TransferListener<? super DataSource>) defaultBandwidthMeter, (DataSource.Factory) b(context, defaultBandwidthMeter));
    }

    public static DataSource.Factory a(Context context, boolean z) {
        return a(context, z ? e : null);
    }

    public static List<ApplicationInfo> a(SecurityManager securityManager) {
        ArrayList arrayList = new ArrayList();
        for (ApplicationInfo next : c()) {
            if (securityManager.getApplicationAccessControlEnabledAsUser(next.packageName, B.c(next.uid))) {
                arrayList.add(next);
            }
        }
        return arrayList;
    }

    public static void a() {
        if (Build.VERSION.SDK_INT >= 24) {
            try {
                e.a(Class.forName("android.os.StrictMode"), "disableDeathOnFileUriExposure", (Class<?>[]) null, new Object[0]);
            } catch (Exception e2) {
                Log.w("AppLockUtils", "disableDeathOnFileUriExposure call ex", e2);
            }
        }
    }

    public static void a(int i, int i2) {
        if (i != 0) {
            ArrayList<String> a2 = b.a("applock_verify_and_activate_fingerprint_" + i2, (ArrayList<String>) new ArrayList());
            a2.add(String.valueOf(i));
            b.b("applock_verify_and_activate_fingerprint_" + i2, a2);
        }
    }

    public static void a(long j) {
        b.b("last_mini_card_pop_time", j);
    }

    public static void a(long j, Context context) {
        f.b(context.getContentResolver(), "applock_countDownTimer_deadline", j);
    }

    public static void a(Context context) {
        b.b("applock_verify_and_activate_fingerprint_" + g.a(context), (ArrayList<String>) new ArrayList());
    }

    public static void a(Context context, int i) {
        f.b(context.getContentResolver(), "applock_unlock_mode", i);
    }

    public static void a(Context context, int i, int i2, Intent intent, int i3, int i4, Bitmap bitmap) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService("notification");
        v.a(notificationManager, "com.miui.applicationlock", context.getResources().getString(R.string.app_name), 3);
        Notification build = v.a(context, "com.miui.applicationlock").setWhen(System.currentTimeMillis()).setContentTitle(context.getResources().getString(i)).setContentText(context.getResources().getString(i2)).setLargeIcon(bitmap).setSmallIcon(R.drawable.applock_small_icon).setContentIntent(PendingIntent.getActivity(context, i4, intent, 0)).build();
        build.flags |= 16;
        v.b(build, true);
        v.a(build, true);
        v.a(build, 0);
        notificationManager.notify(i3, build);
    }

    public static void a(Context context, int i, View view, int i2) {
        Toast toast = new Toast(context);
        try {
            e.a((Object) toast, "setType", (Class<?>[]) new Class[]{Integer.TYPE}, 2038);
        } catch (Exception e2) {
            Log.e("AppLockUtils", "setType", e2);
        }
        toast.setView(view);
        toast.setDuration(0);
        toast.setGravity(i, 0, context.getResources().getDimensionPixelSize(R.dimen.applock_toast_margin_top));
        a(toast, i2);
        toast.show();
    }

    private static void a(Context context, Intent intent) {
        intent.putExtra("com.android.settings.bgColor", context.getResources().getColor(R.color.second_space_setting_bg));
        intent.putExtra("com.android.settings.titleColor", context.getResources().getColor(17170443));
        intent.putExtra("com.android.settings.ConfirmLockPattern.header", context.getResources().getString(R.string.confirmSecondSpacePassword));
    }

    public static void a(Context context, String str) {
        if (i() < 2 && str != null && !j().equals(str)) {
            Intent intent = new Intent(context, MaskNotificationActivity.class);
            intent.putExtra("enter_way", "mask_notification_notify");
            a(context, R.string.notification_masked_item, R.string.notification_masked_subtitle, intent, 101, 2, BitmapFactory.decodeResource(context.getResources(), R.drawable.icon_card_app_lock));
            d(i() + 1);
            c(str);
        }
    }

    public static void a(DialogInterface dialogInterface, String str, int i) {
        if (!((AlertDialog) dialogInterface).isChecked()) {
            return;
        }
        if (i == 290261) {
            h.b("track_not_notify_selected", str);
        } else if (i == 290260) {
            h.c("track_not_notify_selected", str);
        }
    }

    public static void a(View view) {
        TranslateAnimation translateAnimation = new TranslateAnimation(0.0f, -10.0f, 0.0f, 0.0f);
        translateAnimation.setStartOffset(0);
        translateAnimation.setDuration(50);
        translateAnimation.setInterpolator(new DecelerateInterpolator());
        TranslateAnimation translateAnimation2 = new TranslateAnimation(-10.0f, 10.0f, 0.0f, 0.0f);
        translateAnimation2.setStartOffset(50);
        translateAnimation2.setDuration(80);
        translateAnimation2.setInterpolator(new AccelerateDecelerateInterpolator());
        TranslateAnimation translateAnimation3 = new TranslateAnimation(10.0f, 0.0f, 0.0f, 0.0f);
        translateAnimation3.setStartOffset(130);
        translateAnimation3.setDuration(50);
        translateAnimation3.setInterpolator(new AccelerateDecelerateInterpolator());
        AnimationSet animationSet = new AnimationSet(true);
        animationSet.setRepeatCount(2);
        animationSet.setRepeatMode(2);
        animationSet.addAnimation(translateAnimation);
        animationSet.addAnimation(translateAnimation2);
        animationSet.addAnimation(translateAnimation3);
        view.startAnimation(animationSet);
    }

    public static void a(AccessibilityManager accessibilityManager, String str) {
        if (accessibilityManager.isEnabled()) {
            AccessibilityEvent obtain = AccessibilityEvent.obtain();
            obtain.setEventType(16384);
            obtain.getText().add(str);
            accessibilityManager.sendAccessibilityEvent(obtain);
        }
    }

    private static void a(Toast toast, int i) {
        try {
            Field declaredField = toast.getClass().getDeclaredField("mTN");
            declaredField.setAccessible(true);
            Object obj = declaredField.get(toast);
            Field declaredField2 = obj.getClass().getDeclaredField("mParams");
            declaredField2.setAccessible(true);
            ((WindowManager.LayoutParams) declaredField2.get(obj)).windowAnimations = i;
            Log.d("AppLockUtils", "animation id: " + i);
        } catch (Exception e2) {
            Log.e("AppLockUtils", "reflect failed: ", e2);
        }
    }

    public static void a(String str, int i) {
        if (i == 290261) {
            h.b("track_dialog_behaviour", str);
        } else if (i == 290260) {
            h.c("track_dialog_behaviour", str);
        }
    }

    public static void a(String str, long j) {
        b.b("current_adinfo_id_" + str, j);
    }

    public static void a(String str, Activity activity) {
        if ("mixed".equals(str)) {
            ViewGroup viewGroup = (ViewGroup) activity.findViewById(R.id.mixed_password_keyboard_view);
            viewGroup.setBackgroundColor(0);
            ((EditText) activity.findViewById(R.id.miui_mixed_password_input_field)).setBackgroundResource(R.drawable.applock_btn_keyboard_key);
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                View childAt = viewGroup.getChildAt(i);
                if (childAt instanceof ViewGroup) {
                    int i2 = 0;
                    while (true) {
                        ViewGroup viewGroup2 = (ViewGroup) childAt;
                        if (i2 >= viewGroup2.getChildCount()) {
                            break;
                        }
                        View childAt2 = viewGroup2.getChildAt(i2);
                        if (childAt2.getClass() == x()) {
                            childAt2.setBackgroundResource(R.drawable.applock_btn_keyboard_key);
                        }
                        i2++;
                    }
                } else if (childAt.getClass() == x()) {
                    childAt.setBackgroundResource(R.drawable.applock_btn_keyboard_key);
                }
            }
            View findViewById = viewGroup.findViewById(f("btn_caps_lock"));
            View findViewById2 = viewGroup.findViewById(f("btn_letter_delete"));
            findViewById.setBackgroundResource(R.drawable.applock_btn_keyboard_caps_key);
            findViewById2.setBackgroundResource(R.drawable.applock_btn_keyboard_delete_key);
        }
    }

    public static void a(SecurityManager securityManager, String str, int i) {
        securityManager.removeAccessControlPassAsUser(str, i);
    }

    public static void a(boolean z) {
        b.b("post_scan_introduce_notification_count", z);
    }

    public static void a(boolean z, Window window) {
        if (z) {
            c.a(window);
        } else {
            c.b(window);
        }
    }

    public static boolean a(int i, SecurityManager securityManager, String str) {
        return i == 999 ? securityManager.checkAccessControlPassAsUser(str, 999) : securityManager.checkAccessControlPass(str);
    }

    public static boolean a(ActivityOptions activityOptions) {
        if (activityOptions == null) {
            return false;
        }
        try {
            if (Build.VERSION.SDK_INT < 24 || Build.VERSION.SDK_INT >= 28) {
                if (((Integer) e.a((Object) activityOptions, Integer.TYPE, "getLaunchWindowingMode", (Class<?>[]) null, new Object[0])).intValue() != 5) {
                    return false;
                }
            } else if (((Integer) e.a((Object) activityOptions, Integer.TYPE, "getLaunchStackId", (Class<?>[]) null, new Object[0])).intValue() != 2) {
                return false;
            }
            return true;
        } catch (Exception e2) {
            Log.e("AppLockUtils", "isMultiWindowMode exception:", e2);
            return false;
        }
    }

    public static boolean a(SecurityManager securityManager, String str) {
        return securityManager.checkAccessControlPass(str);
    }

    public static int b(String str) {
        return b.a("highlight_first_item_package_" + str, 0);
    }

    static long b(int i, int i2) {
        int i3;
        if (a(0, i, i2) > 0) {
            i3 = i + 1;
        } else if (a(0, i, i2) > 0 || a(0, i + 1, i2) < 0) {
            int i4 = i + 1;
            if (a(0, i4, i2) < 0) {
                return a(1, i4, i2);
            }
            return 0;
        } else {
            i3 = i + 2;
        }
        return a(0, i3, i2);
    }

    public static HttpDataSource.Factory b(Context context, DefaultBandwidthMeter defaultBandwidthMeter) {
        return new DefaultHttpDataSourceFactory(Util.getUserAgent(context, "ExoPlayerDemo"), defaultBandwidthMeter);
    }

    public static List<ApplicationInfo> b(SecurityManager securityManager) {
        ArrayList arrayList = new ArrayList();
        if (a(securityManager).size() != 0) {
            for (ApplicationInfo next : a(securityManager)) {
                if (securityManager.getApplicationMaskNotificationEnabledAsUser(next.packageName, B.c(next.uid))) {
                    arrayList.add(next);
                }
            }
        }
        return arrayList;
    }

    public static void b() {
        if (Build.VERSION.SDK_INT >= 24) {
            try {
                e.a(Class.forName("android.os.StrictMode"), "enableDeathOnFileUriExposure", (Class<?>[]) null, new Object[0]);
            } catch (Exception e2) {
                Log.w("AppLockUtils", "enableDeathOnFileUriExposure call ex", e2);
            }
        }
    }

    public static void b(int i) {
        b.b("applock_alarm_count", i);
    }

    public static void b(Context context) {
        ((SecurityManager) context.getSystemService("security")).setAccessControlPassword((String) null, (String) null);
    }

    public static void b(Context context, int i) {
        Settings.Secure.putInt(context.getContentResolver(), "privacy_password_finger_authentication_num", i);
    }

    public static void b(Context context, boolean z) {
        b(context, 0);
        if (Build.VERSION.SDK_INT >= 23 && z) {
            k(context);
        }
    }

    public static void b(View view) {
        TranslateAnimation translateAnimation = new TranslateAnimation(0.0f, -30.0f, 0.0f, 0.0f);
        translateAnimation.setStartOffset(0);
        translateAnimation.setDuration(50);
        translateAnimation.setInterpolator(new DecelerateInterpolator());
        TranslateAnimation translateAnimation2 = new TranslateAnimation(-30.0f, 30.0f, 0.0f, 0.0f);
        translateAnimation2.setStartOffset(50);
        translateAnimation2.setDuration(100);
        translateAnimation2.setInterpolator(new AccelerateDecelerateInterpolator());
        TranslateAnimation translateAnimation3 = new TranslateAnimation(30.0f, 0.0f, 0.0f, 0.0f);
        translateAnimation3.setStartOffset(150);
        translateAnimation3.setDuration(50);
        translateAnimation3.setInterpolator(new AccelerateDecelerateInterpolator());
        AnimationSet animationSet = new AnimationSet(true);
        animationSet.setRepeatCount(2);
        animationSet.setRepeatMode(2);
        animationSet.addAnimation(translateAnimation);
        animationSet.addAnimation(translateAnimation2);
        animationSet.addAnimation(translateAnimation3);
        view.startAnimation(animationSet);
    }

    public static void b(String str, int i) {
        b.b("highlight_first_item_package_" + str, i);
    }

    public static void b(SecurityManager securityManager, String str) {
        if (!TextUtils.isEmpty(str)) {
            securityManager.addAccessControlPass(str);
            new Handler().postDelayed(new m(securityManager, str), AdaptiveTrackSelection.DEFAULT_MIN_TIME_BETWEEN_BUFFER_REEVALUTATION_MS);
        }
    }

    public static void b(boolean z) {
        b.b("face_unlock_dialog_notify", z);
    }

    public static List<ApplicationInfo> c() {
        List<ApplicationInfo> a2;
        ArrayList arrayList = new ArrayList();
        List<ApplicationInfo> a3 = a.a(0, B.j());
        if (a3 == null) {
            return arrayList;
        }
        if (p.a() >= 8 && B.j() == 0 && (a2 = a.a(0, 999)) != null) {
            for (ApplicationInfo next : a2) {
                if (next != null && !f3318b.contains(next.packageName)) {
                    a3.add(next);
                }
            }
        }
        for (int i = 0; i < a3.size(); i++) {
            ApplicationInfo applicationInfo = a3.get(i);
            if (((applicationInfo.flags & 1) <= 0 || C0312y.f3467a.contains(applicationInfo.packageName)) && !f3317a.contains(applicationInfo.packageName)) {
                arrayList.add(applicationInfo);
            }
        }
        return arrayList;
    }

    public static void c(int i) {
        b.b("applock_LockModePrompt", i);
    }

    public static void c(Context context) {
        C0259c b2 = C0259c.b(context);
        b2.a(false);
        b2.a(1);
        b2.d(false);
        b2.c(false);
        b(context);
        c(context, true);
        b2.g(true);
        b2.b(false);
        b2.a((String) null);
        c(true);
        b(true);
        a(context);
        c((SecurityManager) context.getSystemService("security"));
        b.b("cancel_fingerprint_verify_times", 0);
        b.b("cancel_fingerprint_guide_times", 0);
        b.b("cancel_face_unlock_verify_times", 0);
        b.b("cancel_face_unlock_guide_times", 0);
        C0259c.a(context.getContentResolver(), g.a(context));
    }

    public static void c(Context context, int i) {
        Settings.Secure.putInt(context.getContentResolver(), "privacy_wrong_attempt_num", i);
    }

    public static void c(Context context, boolean z) {
        Settings.Secure.putInt(context.getContentResolver(), "com_miui_applicationlock_is_visible_pattern", z ? 1 : 0);
    }

    public static void c(String str) {
        b.b("masked_notification_package", str);
    }

    public static void c(SecurityManager securityManager) {
        securityManager.removeAccessControlPass("*");
        securityManager.removeAccessControlPassAsUser("*", 999);
    }

    public static void c(boolean z) {
        b.b("fingerprint_dialog_notify", z);
    }

    public static boolean c(int i, int i2) {
        if (i == 0) {
            return false;
        }
        ArrayList<String> a2 = b.a("applock_verify_and_activate_fingerprint_" + i2, (ArrayList<String>) new ArrayList());
        return a2 != null && a2.contains(String.valueOf(i));
    }

    public static int d() {
        return b.a("applock_alarm_count", 0);
    }

    public static Account d(Context context) {
        Account[] accountsByType = AccountManager.get(context).getAccountsByType(Constants.XIAOMI_ACCOUNT_TYPE);
        if (accountsByType == null || accountsByType.length <= 0) {
            return null;
        }
        return accountsByType[0];
    }

    public static void d(int i) {
        b.b("masked_notification_sum", i);
    }

    public static void d(String str) {
        g = str;
    }

    public static void d(boolean z) {
        b.b("key_first_open_applock_main", z);
    }

    public static boolean d(Context context, int i) {
        List<Integer> b2 = E.a(context).b();
        ArrayList<String> a2 = b.a("applock_verify_and_activate_fingerprint_" + i, (ArrayList<String>) new ArrayList());
        if (b2 == null || b2.size() != 1) {
            return false;
        }
        if (a2.size() != 0) {
            b.b("applock_verify_and_activate_fingerprint_" + i, (ArrayList<String>) new ArrayList());
        }
        a2.add(String.valueOf(b2.get(0)));
        b.b("applock_verify_and_activate_fingerprint_" + i, a2);
        return true;
    }

    public static int e(Context context) {
        return f.a(context.getContentResolver(), "applock_unlock_mode", 0);
    }

    public static void e(int i) {
        b.b("mini_card_pop_count", i);
    }

    public static void e(Context context, int i) {
        d.a(new n(context, i));
    }

    public static void e(String str) {
        ArrayList<String> a2 = b.a("flag_mini_card_show_packagenames", (ArrayList<String>) new ArrayList());
        if (!TextUtils.isEmpty(str) && !a2.contains(str)) {
            a2.add(str);
        }
        b.b("flag_mini_card_show_packagenames", a2);
    }

    public static void e(boolean z) {
        f = z;
    }

    public static boolean e() {
        return f;
    }

    private static int f(String str) {
        try {
            return ((Integer) e.a(Class.forName("android.miui.R$id"), str, Integer.TYPE)).intValue();
        } catch (Exception e2) {
            Log.e("AppLockUtils", "getResourceId exception: ", e2);
            return 0;
        }
    }

    public static List<String> f(Context context) {
        ArrayList arrayList = new ArrayList();
        if (C0259c.b(context).e()) {
            return arrayList;
        }
        List<ApplicationInfo> c2 = c();
        ArrayList arrayList2 = new ArrayList();
        ArrayList<String> arrayList3 = C0312y.f3468b;
        ArrayList arrayList4 = new ArrayList();
        for (ApplicationInfo next : c2) {
            String str = next.packageName;
            C0257a aVar = new C0257a(str, (Integer) null, str, next.uid);
            int indexOf = arrayList3.indexOf(str);
            if (indexOf != -1) {
                arrayList4.add(new Pair(aVar, Integer.valueOf(indexOf)));
            }
            arrayList2.add(aVar);
        }
        Collections.sort(arrayList4, new l());
        for (int i = 0; i < arrayList4.size(); i++) {
            arrayList.add(((C0257a) ((Pair) arrayList4.get(i)).first).e());
        }
        return arrayList;
    }

    public static void f(int i) {
        b.b("post_introduce_notification_count", i);
    }

    public static void f(boolean z) {
        b.b("applock_notifycation_clicked", z);
    }

    public static int[] f() {
        int[] iArr = new int[2];
        String a2 = y.a(Build.VERSION.SDK_INT >= 28 ? "persist.vendor.sys.fp.fod.location.X_Y" : "persist.sys.fp.fod.location.X_Y", "");
        if (!TextUtils.isEmpty(a2) && a2.contains(",")) {
            String[] split = a2.split(",");
            iArr[0] = Integer.parseInt(split[0]);
            iArr[1] = Integer.parseInt(split[1]);
        }
        return iArr;
    }

    public static int g(Context context) {
        return Settings.Secure.getInt(context.getContentResolver(), "privacy_password_finger_authentication_num", 0);
    }

    public static long g() {
        return b.a("last_mini_card_pop_time", 0);
    }

    public static void g(int i) {
        b.b("pre_introduce_notification_count", i);
    }

    public static void g(boolean z) {
        b.b("receive_recommendation", z);
    }

    public static int h() {
        return b.a("applock_LockModePrompt", 0);
    }

    public static int h(Context context) {
        return Settings.Secure.getInt(context.getContentResolver(), "privacy_wrong_attempt_num", 0);
    }

    public static int i() {
        return b.a("masked_notification_sum", 0);
    }

    public static boolean i(Context context) {
        return Settings.Secure.getInt(context.getContentResolver(), "com_miui_applicationlock_is_visible_pattern", 1) == 1;
    }

    public static String j() {
        return b.a("masked_notification_package", "");
    }

    public static void j(Context context) {
        Vibrator vibrator = (Vibrator) context.getSystemService("vibrator");
        if (vibrator.hasVibrator()) {
            vibrator.vibrate(200);
        }
    }

    public static int k() {
        return b.a("mini_card_pop_count", 0);
    }

    private static void k(Context context) {
        try {
            E.a(context.getApplicationContext()).a((byte[]) null);
        } catch (Exception e2) {
            Log.w("AppLockUtils", "Fail to resetTimeout", e2);
        }
    }

    public static int l() {
        return b.a("post_introduce_notification_count", 0);
    }

    public static int m() {
        return b.a("pre_introduce_notification_count", 0);
    }

    public static String n() {
        return TextUtils.isEmpty(g) ? "" : g;
    }

    public static ArrayList<String> o() {
        return b.a("flag_mini_card_show_packagenames", (ArrayList<String>) new ArrayList());
    }

    public static boolean p() {
        return b.a("post_scan_introduce_notification_count", true);
    }

    public static boolean q() {
        return b.a("face_unlock_dialog_notify", true);
    }

    public static boolean r() {
        return b.a("fingerprint_dialog_notify", true);
    }

    public static boolean s() {
        return b.a("key_first_open_applock_main", true);
    }

    public static boolean t() {
        return b.a("applock_notifycation_clicked", false);
    }

    public static boolean u() {
        return b.a("receive_recommendation", true);
    }

    public static boolean v() {
        return TextUtils.getLayoutDirectionFromLocale(Locale.getDefault()) == 1;
    }

    public static long w() {
        Calendar instance = Calendar.getInstance();
        instance.get(11);
        instance.add(6, 3);
        int nextInt = new Random().nextInt(90);
        instance.set(11, 19);
        instance.set(12, nextInt);
        instance.add(12, 30);
        return instance.getTimeInMillis() - System.currentTimeMillis();
    }

    private static Class x() {
        Class<TextView> cls = TextView.class;
        try {
            return Class.forName("miui.view.MiuiKeyBoardView$KeyButton");
        } catch (Exception e2) {
            Log.d("AppLockUtils", "getKeyButtonClass exception: ", e2);
            return cls;
        }
    }
}
