package com.miui.permcenter.privacymanager.behaviorrecord;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.os.UserHandle;
import android.provider.Settings;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import b.b.c.j.B;
import b.b.c.j.x;
import com.milink.api.v1.type.MilinkConfig;
import com.miui.appmanager.AppManageUtils;
import com.miui.appmanager.E;
import com.miui.luckymoney.config.AppConstants;
import com.miui.permcenter.a;
import com.miui.permcenter.l;
import com.miui.permcenter.n;
import com.miui.permcenter.privacymanager.a.b;
import com.miui.permcenter.privacymanager.behaviorrecord.PrivacyDetailActivity;
import com.miui.permission.PermissionContract;
import com.miui.permission.PermissionManager;
import com.miui.permission.RequiredPermissionsUtil;
import com.miui.securitycenter.R;
import com.miui.warningcenter.WarningCenterAlertAdapter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import miui.app.AlertDialog;
import miui.text.ChinesePinyinConverter;
import miui.util.Log;

public class o {

    /* renamed from: a  reason: collision with root package name */
    public static int f6454a = 50;

    /* renamed from: b  reason: collision with root package name */
    private static final String[] f6455b = {"com.android.thememanager", "com.miui.barcodescanner", "com.miui.dmregservice", "com.wdstechnology.android.kryten", "com.miui.notes", "com.miui.weather2", "com.xiaomi.gamecenter", "com.miui.fmradio", "com.android.email", "com.miui.video", "com.miui.player", "com.xiaomi.market", "com.xiaomi.jr", "com.xiaomi.vip", "com.mi.vtalk", "com.xiaomi.gamecenter.sdk.service", "com.mipay.wallet", "com.miui.tsmclient", "org.simalliance.openmobileapi.service", AppConstants.Package.PACKAGE_NAME_MITALK, "com.miui.yellowpage", "com.xiaomi.o2o", "com.miui.miuibbs", "com.xiaomi.pass", "com.xiaomi.mircs", "com.android.vending", "com.android.calculator2", "com.xiaomi.scanner", MilinkConfig.PACKAGE_NAME, "com.miui.sysbase", "com.miui.calculator", "com.miui.milivetalk", "com.miui.smsextra", "com.xiaomi.oga", "com.miui.contentextension", "com.miui.personalassistant", "com.android.storagemonitor", "com.xiaomi.gamecenter.pad", "com.miui.voicetrigger", "com.xiaomi.vipaccount", "com.google.android.gms", "com.miui.greenguard", "com.mobiletools.systemhelper", "com.miui.fm", "com.miui.smarttravel", "com.miui.cleanmaster", "com.miui.compass", "com.mfashiongallery.emag"};

    /* renamed from: c  reason: collision with root package name */
    private static final String[] f6456c = {"cn.xuexi.android", "android.permission.cts.appthataccesseslocation"};

    /* renamed from: d  reason: collision with root package name */
    private static List<String> f6457d = new ArrayList();
    private static List<String> e = new ArrayList();
    private static List<String> f = new ArrayList();
    private static Map<Long, Integer> g = new HashMap();
    private static Map<Long, Integer> h = new HashMap();
    private static Map<String, Integer> i = new HashMap();
    private static Map<Long, Integer> j = new HashMap();
    private static Map<Long, Integer> k = new HashMap();
    private static Map<String, Integer> l = new HashMap();
    private static Map<Long, Integer> m = new HashMap();
    private static Map<Long, Integer> n = new HashMap();
    /* access modifiers changed from: private */
    public static SparseArray<Integer> o = new SparseArray<>();
    private static Set<Long> p = new HashSet();

    static {
        for (String add : f6455b) {
            f6457d.add(add);
        }
        for (String add2 : f6456c) {
            f6457d.add(add2);
        }
        e.add("com.mi.health@131072");
        e.add("com.iflytek.inputmethod.miui@4611686018427387904");
        e.add("com.baidu.input_mi@4611686018427387904");
        e.add("com.sohu.inputmethod.sogou.xiaomi@4611686018427387904");
        f.add("com.miui.compass");
        f.add("com.miui.smarttravel");
        f.add("com.miui.weather2");
        f.add("com.xiaomi.shop");
        g.put(8L, Integer.valueOf(R.string.app_behavior_contacts));
        g.put(Long.valueOf(PermissionManager.PERM_ID_READCONTACT), Integer.valueOf(R.string.app_behavior_contacts));
        g.put(Long.valueOf(PermissionManager.PERM_ID_CALENDAR), Integer.valueOf(R.string.app_behavior_calendar));
        g.put(Long.valueOf(PermissionManager.PERM_ID_READSMS), Integer.valueOf(R.string.app_behavior_sms_read));
        g.put(1L, Integer.valueOf(R.string.app_behavior_sms_send));
        g.put(Long.valueOf(PermissionManager.PERM_ID_EXTERNAL_STORAGE), Integer.valueOf(R.string.app_behavior_storage));
        g.put(32L, Integer.valueOf(R.string.app_behavior_location));
        g.put(16L, Integer.valueOf(R.string.app_behavior_calllog));
        g.put(1073741824L, Integer.valueOf(R.string.app_behavior_calllog));
        g.put(64L, Integer.valueOf(R.string.app_behavior_phonestate));
        g.put(2L, Integer.valueOf(R.string.app_behavior_callphone));
        g.put(Long.valueOf(PermissionManager.PERM_ID_AUDIO_RECORDER), Integer.valueOf(R.string.app_behavior_audio));
        g.put(Long.valueOf(PermissionManager.PERM_ID_VIDEO_RECORDER), Integer.valueOf(R.string.app_behavior_camera));
        g.put(Long.valueOf(PermissionManager.PERM_ID_BODY_SENSORS), Integer.valueOf(R.string.app_behavior_sensor));
        g.put(Long.valueOf(PermissionManager.PERM_ID_AUTOSTART), Integer.valueOf(R.string.app_behavior_autostart));
        g.put(Long.valueOf(PermissionManager.PERM_ID_CLIPBOARD), Integer.valueOf(R.string.app_behavior_clipboard));
        g.put(Long.valueOf(PermissionManager.PERM_ID_REAL_READ_SMS), Integer.valueOf(R.string.app_behavior_sms_read));
        g.put(Long.valueOf(PermissionManager.PERM_ID_REAL_READ_CONTACTS), Integer.valueOf(R.string.app_behavior_contacts));
        g.put(Long.valueOf(PermissionManager.PERM_ID_REAL_READ_CALENDAR), Integer.valueOf(R.string.app_behavior_calendar));
        g.put(Long.valueOf(PermissionManager.PERM_ID_REAL_READ_CALL_LOG), Integer.valueOf(R.string.app_behavior_calllog));
        g.put(Long.valueOf(PermissionManager.PERM_ID_REAL_READ_PHONE_STATE), Integer.valueOf(R.string.app_behavior_phonestate));
        g.put(Long.valueOf(PermissionManager.PERM_ID_GET_INSTALLED_APPS), Integer.valueOf(R.string.app_behavior_get_install));
        i.put("com.miui.home", Integer.valueOf(R.string.app_behavior_start_from_launcher));
        i.put("notification", Integer.valueOf(R.string.app_behavior_start_from_notification));
        i.put("com.xiaomi.xmsf", Integer.valueOf(R.string.app_behavior_start_from_notification));
        i.put("recentTask", Integer.valueOf(R.string.app_behavior_start_from_recent));
        h.put(32L, Integer.valueOf(R.string.app_behavior_location_bg));
        h.put(Long.valueOf(PermissionManager.PERM_ID_AUDIO_RECORDER), Integer.valueOf(R.string.app_behavior_audio_bg));
        h.put(8L, Integer.valueOf(R.string.app_behavior_contacts_bg));
        h.put(Long.valueOf(PermissionManager.PERM_ID_READCONTACT), Integer.valueOf(R.string.app_behavior_contacts_bg));
        h.put(16L, Integer.valueOf(R.string.app_behavior_calllog_bg));
        h.put(1073741824L, Integer.valueOf(R.string.app_behavior_calllog_bg));
        h.put(Long.valueOf(PermissionManager.PERM_ID_CLIPBOARD), Integer.valueOf(R.string.app_behavior_clipboard_bg));
        j.put(8L, Integer.valueOf(R.string.app_behavior_contacts_single));
        j.put(Long.valueOf(PermissionManager.PERM_ID_READCONTACT), Integer.valueOf(R.string.app_behavior_contacts_single));
        j.put(Long.valueOf(PermissionManager.PERM_ID_CALENDAR), Integer.valueOf(R.string.app_behavior_calendar_single));
        j.put(Long.valueOf(PermissionManager.PERM_ID_READSMS), Integer.valueOf(R.string.app_behavior_sms_read_single));
        j.put(1L, Integer.valueOf(R.string.app_behavior_sms_send_single));
        j.put(Long.valueOf(PermissionManager.PERM_ID_EXTERNAL_STORAGE), Integer.valueOf(R.string.app_behavior_storage_single));
        j.put(32L, Integer.valueOf(R.string.app_behavior_location_single));
        j.put(16L, Integer.valueOf(R.string.app_behavior_calllog_single));
        j.put(1073741824L, Integer.valueOf(R.string.app_behavior_calllog_single));
        j.put(64L, Integer.valueOf(R.string.app_behavior_phonestate_single));
        j.put(2L, Integer.valueOf(R.string.app_behavior_callphone_single));
        j.put(Long.valueOf(PermissionManager.PERM_ID_AUDIO_RECORDER), Integer.valueOf(R.string.app_behavior_audio_single));
        j.put(Long.valueOf(PermissionManager.PERM_ID_VIDEO_RECORDER), Integer.valueOf(R.string.app_behavior_camera_single));
        j.put(Long.valueOf(PermissionManager.PERM_ID_BODY_SENSORS), Integer.valueOf(R.string.app_behavior_sensor_single));
        j.put(Long.valueOf(PermissionManager.PERM_ID_AUTOSTART), Integer.valueOf(R.string.app_behavior_autostart_single));
        j.put(Long.valueOf(PermissionManager.PERM_ID_CLIPBOARD), Integer.valueOf(R.string.app_behavior_clipboard_single));
        j.put(Long.valueOf(PermissionManager.PERM_ID_REAL_READ_SMS), Integer.valueOf(R.string.app_behavior_sms_read_single));
        j.put(Long.valueOf(PermissionManager.PERM_ID_REAL_READ_CONTACTS), Integer.valueOf(R.string.app_behavior_contacts_single));
        j.put(Long.valueOf(PermissionManager.PERM_ID_REAL_READ_CALENDAR), Integer.valueOf(R.string.app_behavior_calendar_single));
        j.put(Long.valueOf(PermissionManager.PERM_ID_REAL_READ_CALL_LOG), Integer.valueOf(R.string.app_behavior_calllog_single));
        j.put(Long.valueOf(PermissionManager.PERM_ID_REAL_READ_PHONE_STATE), Integer.valueOf(R.string.app_behavior_phonestate_single));
        j.put(Long.valueOf(PermissionManager.PERM_ID_GET_INSTALLED_APPS), Integer.valueOf(R.string.app_behavior_get_install_single));
        l.put("com.miui.home", Integer.valueOf(R.string.app_behavior_start_from_launcher_single));
        l.put("notification", Integer.valueOf(R.string.app_behavior_start_from_notification_single));
        l.put("com.xiaomi.xmsf", Integer.valueOf(R.string.app_behavior_start_from_notification_single));
        l.put("recentTask", Integer.valueOf(R.string.app_behavior_start_from_recent_single));
        k.put(32L, Integer.valueOf(R.string.app_behavior_location_bg_single));
        k.put(Long.valueOf(PermissionManager.PERM_ID_AUDIO_RECORDER), Integer.valueOf(R.string.app_behavior_audio_bg_single));
        k.put(8L, Integer.valueOf(R.string.app_behavior_contacts_bg_single));
        k.put(Long.valueOf(PermissionManager.PERM_ID_READCONTACT), Integer.valueOf(R.string.app_behavior_contacts_bg_single));
        k.put(16L, Integer.valueOf(R.string.app_behavior_calllog_bg_single));
        k.put(1073741824L, Integer.valueOf(R.string.app_behavior_calllog_bg_single));
        k.put(Long.valueOf(PermissionManager.PERM_ID_CLIPBOARD), Integer.valueOf(R.string.app_behavior_clipboard_bg_single));
        m.put(32L, Integer.valueOf(R.string.app_behavior_notification_title_just_location));
        m.put(Long.valueOf(PermissionManager.PERM_ID_AUDIO_RECORDER), Integer.valueOf(R.string.app_behavior_notification_title_just_audio));
        m.put(8L, Integer.valueOf(R.string.app_behavior_notification_title_contact_write));
        m.put(Long.valueOf(PermissionManager.PERM_ID_READCONTACT), Integer.valueOf(R.string.app_behavior_notification_title_contact_read));
        m.put(16L, Integer.valueOf(R.string.app_behavior_notification_title_calllog_write));
        m.put(1073741824L, Integer.valueOf(R.string.app_behavior_notification_title_calllog_read));
        m.put(Long.valueOf(PermissionManager.PERM_ID_CLIPBOARD), Integer.valueOf(R.string.app_behavior_notification_title_clipboard_write));
        n.put(32L, Integer.valueOf(R.string.app_behavior_notification_title_now_location));
        n.put(Long.valueOf(PermissionManager.PERM_ID_AUDIO_RECORDER), Integer.valueOf(R.string.app_behavior_notification_title_now_audio));
        o.put(3, Integer.valueOf(R.string.app_behavior_perm_change_tip_allow));
        o.put(2, Integer.valueOf(R.string.app_behavior_perm_change_tip_ask));
        o.put(1, Integer.valueOf(R.string.app_behavior_perm_change_tip_deny));
        o.put(6, Integer.valueOf(R.string.app_behavior_perm_change_tip_foreground));
        o.put(7, Integer.valueOf(R.string.app_behavior_perm_change_tip_virtual));
        p.add(32L);
        p.add(Long.valueOf(PermissionManager.PERM_ID_AUDIO_RECORDER));
        p.add(Long.valueOf(PermissionManager.PERM_ID_VIDEO_RECORDER));
    }

    public static int a(long j2, int i2) {
        return (i2 == 1 ? k : h).getOrDefault(Long.valueOf(j2), 0).intValue();
    }

    public static int a(String str, int i2) {
        return (i2 == 1 ? l : i).getOrDefault(str, 0).intValue();
    }

    public static int a(String str, String str2) {
        return (int) (Math.abs(a(str) - a(str2)) / 86400000);
    }

    private static int a(List<a> list, long j2) {
        if (list == null || list.size() == 0) {
            return 0;
        }
        int i2 = 0;
        for (a next : list) {
            if (next.f() != null && next.f().getOrDefault(Long.valueOf(j2), 0).intValue() == 3) {
                i2++;
            }
        }
        return i2;
    }

    public static long a(long j2) {
        if (j2 == PermissionManager.PERM_ID_REAL_READ_SMS) {
            return PermissionManager.PERM_ID_READSMS;
        }
        if (j2 == PermissionManager.PERM_ID_REAL_READ_CONTACTS) {
            return PermissionManager.PERM_ID_READCONTACT;
        }
        if (j2 == PermissionManager.PERM_ID_REAL_READ_CALENDAR) {
            return PermissionManager.PERM_ID_CALENDAR;
        }
        if (j2 == PermissionManager.PERM_ID_REAL_READ_CALL_LOG) {
            return 1073741824;
        }
        if (j2 == PermissionManager.PERM_ID_REAL_READ_PHONE_STATE) {
            return 64;
        }
        return j2;
    }

    public static long a(Context context, String str, int i2, long... jArr) {
        long j2;
        String[] stringArray;
        long[] jArr2 = jArr;
        if (jArr2 == null) {
            return 0;
        }
        try {
            int length = jArr2.length;
            j2 = 0;
            int i3 = 0;
            while (i3 < length) {
                try {
                    long j3 = jArr2[i3];
                    Bundle call = context.getContentResolver().call(PermissionContract.CONTENT_URI, String.valueOf(13), String.valueOf(j3), (Bundle) null);
                    if (call == null || (stringArray = call.getStringArray("extra_data")) == null) {
                        String str2 = str;
                        int i4 = i2;
                    } else {
                        int length2 = stringArray.length;
                        long j4 = j2;
                        int i5 = 0;
                        while (i5 < length2) {
                            try {
                                if (TextUtils.equals(stringArray[i5], i2 + "@" + str)) {
                                    j4 |= j3;
                                }
                                i5++;
                            } catch (Exception e2) {
                                e = e2;
                                j2 = j4;
                                e.printStackTrace();
                                return j2;
                            }
                        }
                        String str3 = str;
                        int i6 = i2;
                        j2 = j4;
                    }
                    i3++;
                } catch (Exception e3) {
                    e = e3;
                    e.printStackTrace();
                    return j2;
                }
            }
        } catch (Exception e4) {
            e = e4;
            j2 = 0;
            e.printStackTrace();
            return j2;
        }
        return j2;
    }

    public static long a(String str) {
        try {
            return new SimpleDateFormat("yyyy-MM-dd").parse(str).getTime();
        } catch (ParseException e2) {
            e2.printStackTrace();
            return -1;
        }
    }

    public static HashMap<Long, String> a(Context context, String str) {
        HashMap<Long, String> hashMap = new HashMap<>();
        String[] strArr = {"permissionId", PermissionContract.Method.SavePermissionDescription.EXTRA_DESCRIPTION};
        Cursor query = context.getContentResolver().query(PermissionContract.DESCRIPTION_URI, strArr, "locale = ? and pkgName = ?", new String[]{Locale.getDefault().getLanguage(), str}, (String) null);
        if (query != null) {
            while (query.moveToNext()) {
                try {
                    long j2 = query.getLong(query.getColumnIndex("permissionId"));
                    String string = query.getString(query.getColumnIndex(PermissionContract.Method.SavePermissionDescription.EXTRA_DESCRIPTION));
                    if (!TextUtils.isEmpty(string)) {
                        if (!TextUtils.equals(string, "null")) {
                            hashMap.put(Long.valueOf(j2), string);
                        }
                    }
                } finally {
                    query.close();
                }
            }
        }
        return hashMap;
    }

    public static HashMap<String, ArrayList<Integer>> a(Context context, List<com.miui.permcenter.privacymanager.a.a> list, boolean z) {
        HashMap<String, ArrayList<Integer>> hashMap = new HashMap<>();
        if (list != null) {
            boolean z2 = z;
            for (int i2 = 0; i2 < list.size(); i2++) {
                com.miui.permcenter.privacymanager.a.a aVar = list.get(i2);
                String str = "runtime_behavior";
                if (!z2 || !e(aVar.b())) {
                    str = b(context, aVar.b());
                    aVar.c(b.e);
                } else {
                    aVar.a(b.e);
                    if (aVar.m()) {
                        z2 = false;
                    }
                }
                ArrayList arrayList = hashMap.containsKey(str) ? hashMap.get(str) : new ArrayList();
                arrayList.add(Integer.valueOf(i2));
                hashMap.put(str, arrayList);
            }
        }
        return hashMap;
    }

    public static void a(Activity activity, com.miui.permcenter.privacymanager.a.a aVar, int i2, String str, PrivacyDetailActivity.i iVar) {
        Activity activity2 = activity;
        boolean z = (aVar.h() & 16) != 0;
        boolean z2 = x.i(activity2, aVar.f()) && RequiredPermissionsUtil.RUNTIME_PERMISSIONS.containsValue(Long.valueOf(aVar.i()));
        boolean z3 = (aVar.h() & 64) != 0;
        m mVar = new m(activity2, aVar, iVar);
        if (b((Context) activity)) {
            l.a(activity, aVar.f(), aVar.l(), aVar.i(), aVar.j(), i2, mVar, z, z2, aVar.e().toString(), str, z3);
            return;
        }
        String[] strArr = (String[]) n.a(activity2, aVar.i(), z, z2).toArray(new String[0]);
        SpannableString[] spannableStringArr = new SpannableString[strArr.length];
        for (int i3 = 0; i3 < strArr.length; i3++) {
            if (i3 == 0 && strArr.length == 4) {
                spannableStringArr[i3] = new SpannableString(strArr[i3] + "\n" + activity2.getString(R.string.app_behavior_perm_allow_always_danger_tip));
                spannableStringArr[i3].setSpan(new AbsoluteSizeSpan(36), strArr[i3].length(), spannableStringArr[i3].length(), 33);
            } else {
                spannableStringArr[i3] = new SpannableString(strArr[i3]);
            }
        }
        View inflate = LayoutInflater.from(activity).inflate(R.layout.dialog_permission_manager, (ViewGroup) null);
        TextView textView = (TextView) inflate.findViewById(R.id.dialog_permission_desc_title);
        TextView textView2 = (TextView) inflate.findViewById(R.id.dialog_permission_desc_content);
        ((TextView) inflate.findViewById(R.id.dialog_permission_name)).setText(aVar.j());
        ((TextView) inflate.findViewById(R.id.dialog_package_name)).setText(aVar.e());
        if (!TextUtils.isEmpty(str)) {
            textView2.setVisibility(0);
            textView.setVisibility(0);
            textView2.setText(str);
        }
        int i4 = i2;
        int a2 = n.a(aVar.i(), i4, z);
        AlertDialog.Builder builder = new AlertDialog.Builder(activity2);
        builder.setCustomTitle(inflate);
        builder.setSingleChoiceItems(spannableStringArr, a2, new n.b(activity, aVar.f(), i4, z, aVar.i(), mVar)).setNegativeButton(R.string.cancel, (DialogInterface.OnClickListener) null).show();
        int dimensionPixelSize = activity.getResources().getDimensionPixelSize(R.dimen.app_behavior_dialog_padding);
        inflate.setPadding(dimensionPixelSize, inflate.getPaddingTop(), dimensionPixelSize, inflate.getBottom());
    }

    public static boolean a(Context context) {
        return x.e(context, "com.lbe.security.miui") >= 115 && Build.VERSION.SDK_INT >= 28 && com.miui.common.persistence.b.a("app_behavior_enable", true);
    }

    public static boolean a(Context context, String str, int i2) {
        if (context == null) {
            return false;
        }
        String str2 = str.split(":")[0];
        if (f(str2)) {
            return true;
        }
        if (i2 == -2) {
            i2 = B.j();
        }
        PackageInfo a2 = b.b.o.b.a.a.a(str2, 128, i2);
        if (a2 != null) {
            return UserHandle.getAppId(a2.applicationInfo.uid) < 10000 || (a2.applicationInfo.flags & 1) != 0;
        }
        return false;
    }

    public static boolean a(Context context, String str, int i2, long j2) {
        if (a(str, j2)) {
            return true;
        }
        return a(context, str, i2);
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v31, resolved type: java.lang.String} */
    /* JADX WARNING: type inference failed for: r1v5, types: [java.lang.CharSequence] */
    /* JADX WARNING: type inference failed for: r1v23, types: [java.lang.String] */
    /* JADX WARNING: type inference failed for: r1v28 */
    /* JADX WARNING: Code restructure failed: missing block: B:84:0x01e2, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:85:0x01e3, code lost:
        r28 = r9;
        r17 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:91:0x01f9, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:92:0x01fa, code lost:
        r28 = r9;
     */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:18:0x00a6 A[SYNTHETIC, Splitter:B:18:0x00a6] */
    /* JADX WARNING: Removed duplicated region for block: B:91:0x01f9 A[ExcHandler: all (th java.lang.Throwable), PHI: r9 
      PHI: (r9v2 android.database.Cursor) = (r9v1 android.database.Cursor), (r9v3 android.database.Cursor), (r9v3 android.database.Cursor), (r9v3 android.database.Cursor), (r9v3 android.database.Cursor), (r9v3 android.database.Cursor), (r9v3 android.database.Cursor), (r9v1 android.database.Cursor) binds: [B:18:0x00a6, B:26:0x00be, B:29:0x00c4, B:39:0x00e8, B:58:0x011f, B:42:0x00ee, B:33:0x00da, B:21:0x00ac] A[DONT_GENERATE, DONT_INLINE], Splitter:B:39:0x00e8] */
    /* JADX WARNING: Removed duplicated region for block: B:96:0x0207  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static boolean a(android.content.Context r29, java.lang.String r30, int r31, @androidx.annotation.NonNull java.util.List<com.miui.permcenter.privacymanager.a.a> r32, int... r33) {
        /*
            r0 = r29
            r15 = r32
            r1 = r33
            boolean r16 = c((android.content.Context) r29)
            java.lang.String r2 = "pkgName"
            java.lang.String r3 = "calleePkg"
            java.lang.String r4 = "permissionId"
            java.lang.String r5 = "mode"
            java.lang.String r6 = "processState"
            java.lang.String r7 = "startTime"
            java.lang.String r8 = "endTime"
            java.lang.String r9 = "count"
            java.lang.String r10 = "user"
            java.lang.String[] r2 = new java.lang.String[]{r2, r3, r4, r5, r6, r7, r8, r9, r10}
            r14 = 2
            java.lang.String[] r3 = new java.lang.String[r14]
            r13 = 0
            r3[r13] = r30
            java.lang.String r4 = java.lang.String.valueOf(r31)
            r12 = 1
            r3[r12] = r4
            r11 = 4
            r10 = 3
            if (r16 == 0) goto L_0x0054
            int r3 = r2.length
            int r3 = r3 + r12
            java.lang.Object[] r2 = java.util.Arrays.copyOf(r2, r3)
            java.lang.String[] r2 = (java.lang.String[]) r2
            int r3 = r2.length
            int r3 = r3 - r12
            java.lang.String r4 = "calleeUser"
            r2[r3] = r4
            java.lang.String[] r3 = new java.lang.String[r11]
            r3[r13] = r30
            java.lang.String r4 = java.lang.String.valueOf(r31)
            r3[r12] = r4
            r3[r14] = r30
            java.lang.String r4 = java.lang.String.valueOf(r31)
            r3[r10] = r4
            java.lang.String r4 = "(pkgName == ? AND user == ? ) OR ( calleePkg == ? AND calleeUser  == ? )"
            goto L_0x0056
        L_0x0054:
            java.lang.String r4 = "pkgName == ? AND user == ?"
        L_0x0056:
            r19 = r2
            r21 = r3
            r20 = r4
            java.lang.String r2 = "endTime DESC , _id DESC"
            if (r1 == 0) goto L_0x0097
            int r3 = r1.length
            java.lang.String r4 = " LIMIT "
            if (r3 != r12) goto L_0x007c
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            r3.append(r2)
            r3.append(r4)
            r1 = r1[r13]
        L_0x0072:
            r3.append(r1)
            java.lang.String r1 = r3.toString()
            r22 = r1
            goto L_0x0099
        L_0x007c:
            int r3 = r1.length
            if (r3 != r14) goto L_0x0097
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            r3.append(r2)
            r3.append(r4)
            r2 = r1[r13]
            r3.append(r2)
            java.lang.String r2 = " OFFSET "
            r3.append(r2)
            r1 = r1[r12]
            goto L_0x0072
        L_0x0097:
            r22 = r2
        L_0x0099:
            r1 = 0
            android.content.ContentResolver r17 = r29.getContentResolver()     // Catch:{ Exception -> 0x0215 }
            android.net.Uri r18 = com.miui.permission.PermissionContract.RECORD_URI     // Catch:{ Exception -> 0x0215 }
            android.database.Cursor r9 = r17.query(r18, r19, r20, r21, r22)     // Catch:{ Exception -> 0x0215 }
            if (r9 == 0) goto L_0x0207
            int r2 = r32.size()     // Catch:{ Exception -> 0x01fd, all -> 0x01f9 }
            if (r2 <= 0) goto L_0x00bd
            java.lang.Object r1 = r15.get(r13)     // Catch:{ Exception -> 0x00b7, all -> 0x01f9 }
            com.miui.permcenter.privacymanager.a.a r1 = (com.miui.permcenter.privacymanager.a.a) r1     // Catch:{ Exception -> 0x00b7, all -> 0x01f9 }
            java.lang.String r1 = r1.b()     // Catch:{ Exception -> 0x00b7, all -> 0x01f9 }
            goto L_0x00bd
        L_0x00b7:
            r0 = move-exception
            r1 = r9
            r17 = r13
            goto L_0x021a
        L_0x00bd:
            r2 = r13
        L_0x00be:
            boolean r3 = r9.moveToNext()     // Catch:{ Exception -> 0x01f3, all -> 0x01f9 }
            if (r3 == 0) goto L_0x01ee
            java.lang.String r4 = r9.getString(r13)     // Catch:{ Exception -> 0x01e8, all -> 0x01f9 }
            java.lang.String r5 = r9.getString(r12)     // Catch:{ Exception -> 0x01e8, all -> 0x01f9 }
            long r6 = r9.getLong(r14)     // Catch:{ Exception -> 0x01e8, all -> 0x01f9 }
            r2 = 8
            int r8 = r9.getInt(r2)     // Catch:{ Exception -> 0x01e8, all -> 0x01f9 }
            if (r16 == 0) goto L_0x00e6
            r2 = 9
            int r2 = r9.getInt(r2)     // Catch:{ Exception -> 0x00e0, all -> 0x01f9 }
            r3 = r2
            goto L_0x00e8
        L_0x00e0:
            r0 = move-exception
            r1 = r9
            r17 = r12
            goto L_0x021a
        L_0x00e6:
            r3 = r31
        L_0x00e8:
            boolean r2 = c((java.lang.String) r5)     // Catch:{ Exception -> 0x01e8, all -> 0x01f9 }
            if (r2 == 0) goto L_0x0107
            java.util.Map<java.lang.Long, java.lang.Integer> r12 = g     // Catch:{ Exception -> 0x0101, all -> 0x01f9 }
            java.lang.Long r13 = java.lang.Long.valueOf(r6)     // Catch:{ Exception -> 0x0101, all -> 0x01f9 }
            boolean r12 = r12.containsKey(r13)     // Catch:{ Exception -> 0x0101, all -> 0x01f9 }
            if (r12 == 0) goto L_0x011b
            boolean r12 = a((android.content.Context) r0, (java.lang.String) r4, (int) r8)     // Catch:{ Exception -> 0x0101, all -> 0x01f9 }
            if (r12 == 0) goto L_0x0107
            goto L_0x011b
        L_0x0101:
            r0 = move-exception
            r1 = r9
            r17 = 1
            goto L_0x021a
        L_0x0107:
            if (r2 != 0) goto L_0x011f
            r12 = 2
            int r2 = (r6 > r12 ? 1 : (r6 == r12 ? 0 : -1))
            if (r2 == 0) goto L_0x011b
            boolean r2 = a((android.content.Context) r0, (java.lang.String) r5, (int) r3)     // Catch:{ Exception -> 0x0101, all -> 0x01f9 }
            if (r2 != 0) goto L_0x011b
            boolean r2 = b((java.lang.String) r4, (java.lang.String) r5)     // Catch:{ Exception -> 0x0101, all -> 0x01f9 }
            if (r2 == 0) goto L_0x011f
        L_0x011b:
            r2 = 1
            r12 = 1
            r13 = 0
            goto L_0x00be
        L_0x011f:
            int r12 = r9.getInt(r10)     // Catch:{ Exception -> 0x01e2, all -> 0x01f9 }
            int r13 = r9.getInt(r11)     // Catch:{ Exception -> 0x01e2, all -> 0x01f9 }
            r2 = 5
            java.lang.String r19 = r9.getString(r2)     // Catch:{ Exception -> 0x01e2, all -> 0x01f9 }
            r2 = 6
            java.lang.String r11 = r9.getString(r2)     // Catch:{ Exception -> 0x01e2, all -> 0x01f9 }
            boolean r21 = android.text.TextUtils.isEmpty(r1)     // Catch:{ Exception -> 0x01e2, all -> 0x01f9 }
            if (r21 == 0) goto L_0x0138
            r1 = r11
        L_0x0138:
            int r10 = a((java.lang.String) r1, (java.lang.String) r11)     // Catch:{ Exception -> 0x01e2, all -> 0x01f9 }
            if (r10 <= r2) goto L_0x0144
            r28 = r9
            r17 = 1
            goto L_0x020d
        L_0x0144:
            r22 = 32
            int r2 = (r6 > r22 ? 1 : (r6 == r22 ? 0 : -1))
            if (r2 != 0) goto L_0x014d
            r22 = 1
            goto L_0x0154
        L_0x014d:
            r2 = 7
            int r2 = r9.getInt(r2)     // Catch:{ Exception -> 0x01e2, all -> 0x01f9 }
            r22 = r2
        L_0x0154:
            if (r16 == 0) goto L_0x0185
            com.miui.permcenter.privacymanager.a.a r23 = new com.miui.permcenter.privacymanager.a.a     // Catch:{ Exception -> 0x01e2, all -> 0x01f9 }
            r24 = 1
            r25 = r1
            r1 = r23
            r2 = r29
            r26 = r3
            r3 = r30
            r27 = r8
            r8 = r12
            r28 = r9
            r9 = r13
            r21 = 3
            r10 = r19
            r20 = 4
            r17 = 1
            r12 = r22
            r18 = 0
            r13 = r27
            r27 = r14
            r14 = r26
            r0 = r15
            r15 = r24
            r1.<init>(r2, r3, r4, r5, r6, r8, r9, r10, r11, r12, r13, r14, r15)     // Catch:{ Exception -> 0x01e0, all -> 0x01de }
            r15 = r23
            goto L_0x01a7
        L_0x0185:
            r25 = r1
            r28 = r9
            r27 = r14
            r0 = r15
            r17 = 1
            r18 = 0
            r20 = 4
            r21 = 3
            com.miui.permcenter.privacymanager.a.a r15 = new com.miui.permcenter.privacymanager.a.a     // Catch:{ Exception -> 0x01e0, all -> 0x01de }
            r14 = 1
            r1 = r15
            r2 = r29
            r3 = r30
            r8 = r12
            r9 = r13
            r10 = r19
            r12 = r22
            r13 = r31
            r1.<init>(r2, r3, r4, r5, r6, r8, r9, r10, r11, r12, r13, r14)     // Catch:{ Exception -> 0x01e0, all -> 0x01de }
        L_0x01a7:
            int r1 = r32.size()     // Catch:{ Exception -> 0x01e0, all -> 0x01de }
            if (r1 != 0) goto L_0x01b4
            r0.add(r15)     // Catch:{ Exception -> 0x01e0, all -> 0x01de }
            r2 = r0
            r0 = r29
            goto L_0x01cc
        L_0x01b4:
            int r1 = r32.size()     // Catch:{ Exception -> 0x01e0, all -> 0x01de }
            int r1 = r1 + -1
            java.lang.Object r1 = r0.get(r1)     // Catch:{ Exception -> 0x01e0, all -> 0x01de }
            com.miui.permcenter.privacymanager.a.a r1 = (com.miui.permcenter.privacymanager.a.a) r1     // Catch:{ Exception -> 0x01e0, all -> 0x01de }
            r2 = r0
            r0 = r29
            boolean r1 = r1.a(r0, r15)     // Catch:{ Exception -> 0x01e0, all -> 0x01de }
            if (r1 != 0) goto L_0x01cc
            r2.add(r15)     // Catch:{ Exception -> 0x01e0, all -> 0x01de }
        L_0x01cc:
            r15 = r2
            r2 = r17
            r12 = r2
            r13 = r18
            r11 = r20
            r10 = r21
            r1 = r25
            r14 = r27
            r9 = r28
            goto L_0x00be
        L_0x01de:
            r0 = move-exception
            goto L_0x0225
        L_0x01e0:
            r0 = move-exception
            goto L_0x0204
        L_0x01e2:
            r0 = move-exception
            r28 = r9
            r17 = 1
            goto L_0x0204
        L_0x01e8:
            r0 = move-exception
            r28 = r9
            r17 = r12
            goto L_0x0204
        L_0x01ee:
            r28 = r9
            r17 = r2
            goto L_0x020d
        L_0x01f3:
            r0 = move-exception
            r28 = r9
            r17 = r2
            goto L_0x0204
        L_0x01f9:
            r0 = move-exception
            r28 = r9
            goto L_0x0225
        L_0x01fd:
            r0 = move-exception
            r28 = r9
            r18 = r13
            r17 = r18
        L_0x0204:
            r1 = r28
            goto L_0x021a
        L_0x0207:
            r28 = r9
            r18 = r13
            r17 = r18
        L_0x020d:
            miui.util.IOUtils.closeQuietly(r28)
            goto L_0x0224
        L_0x0211:
            r0 = move-exception
            r28 = r1
            goto L_0x0225
        L_0x0215:
            r0 = move-exception
            r18 = r13
            r17 = r18
        L_0x021a:
            java.lang.String r2 = "BehaviorRecord-Utils"
            java.lang.String r3 = "loadAppBehaviorByPkgNameAndUser error"
            miui.util.Log.e(r2, r3, r0)     // Catch:{ all -> 0x0211 }
            miui.util.IOUtils.closeQuietly(r1)
        L_0x0224:
            return r17
        L_0x0225:
            miui.util.IOUtils.closeQuietly(r28)
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.permcenter.privacymanager.behaviorrecord.o.a(android.content.Context, java.lang.String, int, java.util.List, int[]):boolean");
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v31, resolved type: java.lang.String} */
    /* JADX WARNING: type inference failed for: r1v5, types: [java.lang.CharSequence] */
    /* JADX WARNING: type inference failed for: r2v24, types: [int] */
    /* JADX WARNING: type inference failed for: r1v23, types: [java.lang.String] */
    /* JADX WARNING: type inference failed for: r1v28 */
    /* JADX WARNING: Code restructure failed: missing block: B:83:0x01a8, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:84:0x01a9, code lost:
        r26 = r11;
        r28 = r14;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:86:0x01b2, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:87:0x01b3, code lost:
        r26 = r11;
        r14 = r2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:88:0x01b7, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:89:0x01b8, code lost:
        r26 = r11;
     */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:17:0x007e A[SYNTHETIC, Splitter:B:17:0x007e] */
    /* JADX WARNING: Removed duplicated region for block: B:88:0x01b7 A[ExcHandler: all (th java.lang.Throwable), PHI: r11 
      PHI: (r11v1 android.database.Cursor) = (r11v0 android.database.Cursor), (r11v2 android.database.Cursor), (r11v2 android.database.Cursor), (r11v2 android.database.Cursor), (r11v2 android.database.Cursor), (r11v2 android.database.Cursor), (r11v2 android.database.Cursor), (r11v0 android.database.Cursor) binds: [B:17:0x007e, B:25:0x0095, B:28:0x009b, B:38:0x00bc, B:56:0x00ec, B:41:0x00c2, B:32:0x00b1, B:20:0x0084] A[DONT_GENERATE, DONT_INLINE], Splitter:B:25:0x0095] */
    /* JADX WARNING: Removed duplicated region for block: B:93:0x01c5  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static boolean a(android.content.Context r29, java.util.List<com.miui.permcenter.privacymanager.a.a> r30, int... r31) {
        /*
            r0 = r29
            r15 = r30
            r1 = r31
            java.lang.String r2 = "pkgName"
            java.lang.String r3 = "calleePkg"
            java.lang.String r4 = "permissionId"
            java.lang.String r5 = "mode"
            java.lang.String r6 = "processState"
            java.lang.String r7 = "startTime"
            java.lang.String r8 = "endTime"
            java.lang.String r9 = "count"
            java.lang.String r10 = "user"
            java.lang.String[] r2 = new java.lang.String[]{r2, r3, r4, r5, r6, r7, r8, r9, r10}
            boolean r16 = c((android.content.Context) r29)
            r14 = 1
            if (r16 == 0) goto L_0x0031
            int r3 = r2.length
            int r3 = r3 + r14
            java.lang.Object[] r2 = java.util.Arrays.copyOf(r2, r3)
            java.lang.String[] r2 = (java.lang.String[]) r2
            int r3 = r2.length
            int r3 = r3 - r14
            java.lang.String r4 = "calleeUser"
            r2[r3] = r4
        L_0x0031:
            r7 = r2
            java.lang.String r2 = "endTime DESC , _id DESC"
            r13 = 2
            r12 = 0
            if (r1 == 0) goto L_0x006e
            int r3 = r1.length
            java.lang.String r4 = " LIMIT "
            if (r3 != r14) goto L_0x0053
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            r3.append(r2)
            r3.append(r4)
            r1 = r1[r12]
        L_0x004a:
            r3.append(r1)
            java.lang.String r1 = r3.toString()
            r10 = r1
            goto L_0x006f
        L_0x0053:
            int r3 = r1.length
            if (r3 != r13) goto L_0x006e
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            r3.append(r2)
            r3.append(r4)
            r2 = r1[r12]
            r3.append(r2)
            java.lang.String r2 = " OFFSET "
            r3.append(r2)
            r1 = r1[r14]
            goto L_0x004a
        L_0x006e:
            r10 = r2
        L_0x006f:
            r1 = 0
            android.content.ContentResolver r5 = r29.getContentResolver()     // Catch:{ Exception -> 0x01d3 }
            android.net.Uri r6 = com.miui.permission.PermissionContract.RECORD_URI     // Catch:{ Exception -> 0x01d3 }
            r8 = 0
            r9 = 0
            android.database.Cursor r11 = r5.query(r6, r7, r8, r9, r10)     // Catch:{ Exception -> 0x01d3 }
            if (r11 == 0) goto L_0x01c5
            int r2 = r30.size()     // Catch:{ Exception -> 0x01bb, all -> 0x01b7 }
            if (r2 <= 0) goto L_0x0094
            java.lang.Object r1 = r15.get(r12)     // Catch:{ Exception -> 0x008f, all -> 0x01b7 }
            com.miui.permcenter.privacymanager.a.a r1 = (com.miui.permcenter.privacymanager.a.a) r1     // Catch:{ Exception -> 0x008f, all -> 0x01b7 }
            java.lang.String r1 = r1.b()     // Catch:{ Exception -> 0x008f, all -> 0x01b7 }
            goto L_0x0094
        L_0x008f:
            r0 = move-exception
            r1 = r11
            r14 = r12
            goto L_0x01d8
        L_0x0094:
            r2 = r12
        L_0x0095:
            boolean r3 = r11.moveToNext()     // Catch:{ Exception -> 0x01b2, all -> 0x01b7 }
            if (r3 == 0) goto L_0x01ae
            java.lang.String r4 = r11.getString(r12)     // Catch:{ Exception -> 0x01a8, all -> 0x01b7 }
            r2 = 8
            int r10 = r11.getInt(r2)     // Catch:{ Exception -> 0x01a8, all -> 0x01b7 }
            long r6 = r11.getLong(r13)     // Catch:{ Exception -> 0x01a8, all -> 0x01b7 }
            java.lang.String r5 = r11.getString(r14)     // Catch:{ Exception -> 0x01a8, all -> 0x01b7 }
            if (r16 == 0) goto L_0x00bb
            r2 = 9
            int r2 = r11.getInt(r2)     // Catch:{ Exception -> 0x00b7, all -> 0x01b7 }
            r9 = r2
            goto L_0x00bc
        L_0x00b7:
            r0 = move-exception
            r1 = r11
            goto L_0x01d8
        L_0x00bb:
            r9 = r12
        L_0x00bc:
            boolean r2 = c((java.lang.String) r5)     // Catch:{ Exception -> 0x01a8, all -> 0x01b7 }
            if (r2 == 0) goto L_0x00d5
            java.util.Map<java.lang.Long, java.lang.Integer> r3 = g     // Catch:{ Exception -> 0x00b7, all -> 0x01b7 }
            java.lang.Long r8 = java.lang.Long.valueOf(r6)     // Catch:{ Exception -> 0x00b7, all -> 0x01b7 }
            boolean r3 = r3.containsKey(r8)     // Catch:{ Exception -> 0x00b7, all -> 0x01b7 }
            if (r3 == 0) goto L_0x00e9
            boolean r3 = a((android.content.Context) r0, (java.lang.String) r4, (int) r10)     // Catch:{ Exception -> 0x00b7, all -> 0x01b7 }
            if (r3 == 0) goto L_0x00d5
            goto L_0x00e9
        L_0x00d5:
            if (r2 != 0) goto L_0x00eb
            r2 = 2
            int r2 = (r6 > r2 ? 1 : (r6 == r2 ? 0 : -1))
            if (r2 == 0) goto L_0x00e9
            boolean r2 = a((android.content.Context) r0, (java.lang.String) r5, (int) r9)     // Catch:{ Exception -> 0x00b7, all -> 0x01b7 }
            if (r2 != 0) goto L_0x00e9
            boolean r2 = b((java.lang.String) r4, (java.lang.String) r5)     // Catch:{ Exception -> 0x00b7, all -> 0x01b7 }
            if (r2 == 0) goto L_0x00eb
        L_0x00e9:
            r2 = r14
            goto L_0x0095
        L_0x00eb:
            r2 = 3
            int r8 = r11.getInt(r2)     // Catch:{ Exception -> 0x01a8, all -> 0x01b7 }
            r2 = 4
            int r17 = r11.getInt(r2)     // Catch:{ Exception -> 0x01a8, all -> 0x01b7 }
            r2 = 5
            java.lang.String r18 = r11.getString(r2)     // Catch:{ Exception -> 0x01a8, all -> 0x01b7 }
            r2 = 6
            java.lang.String r3 = r11.getString(r2)     // Catch:{ Exception -> 0x01a8, all -> 0x01b7 }
            boolean r19 = android.text.TextUtils.isEmpty(r1)     // Catch:{ Exception -> 0x01a8, all -> 0x01b7 }
            if (r19 == 0) goto L_0x0106
            r1 = r3
        L_0x0106:
            int r12 = a((java.lang.String) r1, (java.lang.String) r3)     // Catch:{ Exception -> 0x01a8, all -> 0x01b7 }
            if (r12 <= r2) goto L_0x0110
            r26 = r11
            goto L_0x01cb
        L_0x0110:
            r20 = 32
            int r2 = (r6 > r20 ? 1 : (r6 == r20 ? 0 : -1))
            if (r2 != 0) goto L_0x0118
            r12 = r14
            goto L_0x011e
        L_0x0118:
            r2 = 7
            int r2 = r11.getInt(r2)     // Catch:{ Exception -> 0x01a8, all -> 0x01b7 }
            r12 = r2
        L_0x011e:
            if (r16 == 0) goto L_0x014c
            com.miui.permcenter.privacymanager.a.a r20 = new com.miui.permcenter.privacymanager.a.a     // Catch:{ Exception -> 0x01a8, all -> 0x01b7 }
            r21 = 0
            r22 = r1
            r1 = r20
            r2 = r29
            r23 = r3
            r3 = r4
            r24 = r9
            r9 = r17
            r25 = r10
            r10 = r18
            r26 = r11
            r11 = r23
            r19 = 0
            r27 = r13
            r13 = r25
            r28 = r14
            r14 = r24
            r0 = r15
            r15 = r21
            r1.<init>(r2, r3, r4, r5, r6, r8, r9, r10, r11, r12, r13, r14, r15)     // Catch:{ Exception -> 0x01a2, all -> 0x01a0 }
            r15 = r20
            goto L_0x016d
        L_0x014c:
            r22 = r1
            r23 = r3
            r25 = r10
            r26 = r11
            r27 = r13
            r28 = r14
            r0 = r15
            r19 = 0
            com.miui.permcenter.privacymanager.a.a r15 = new com.miui.permcenter.privacymanager.a.a     // Catch:{ Exception -> 0x01a2, all -> 0x01a0 }
            r14 = 0
            r1 = r15
            r2 = r29
            r3 = r4
            r9 = r17
            r10 = r18
            r11 = r23
            r13 = r25
            r1.<init>(r2, r3, r4, r5, r6, r8, r9, r10, r11, r12, r13, r14)     // Catch:{ Exception -> 0x01a2, all -> 0x01a0 }
        L_0x016d:
            int r1 = r30.size()     // Catch:{ Exception -> 0x01a2, all -> 0x01a0 }
            if (r1 != 0) goto L_0x017a
            r0.add(r15)     // Catch:{ Exception -> 0x01a2, all -> 0x01a0 }
            r2 = r0
            r0 = r29
            goto L_0x0192
        L_0x017a:
            int r1 = r30.size()     // Catch:{ Exception -> 0x01a2, all -> 0x01a0 }
            int r1 = r1 + -1
            java.lang.Object r1 = r0.get(r1)     // Catch:{ Exception -> 0x01a2, all -> 0x01a0 }
            com.miui.permcenter.privacymanager.a.a r1 = (com.miui.permcenter.privacymanager.a.a) r1     // Catch:{ Exception -> 0x01a2, all -> 0x01a0 }
            r2 = r0
            r0 = r29
            boolean r1 = r1.a(r0, r15)     // Catch:{ Exception -> 0x01a2, all -> 0x01a0 }
            if (r1 != 0) goto L_0x0192
            r2.add(r15)     // Catch:{ Exception -> 0x01a2, all -> 0x01a0 }
        L_0x0192:
            r15 = r2
            r12 = r19
            r1 = r22
            r11 = r26
            r13 = r27
            r2 = r28
            r14 = r2
            goto L_0x0095
        L_0x01a0:
            r0 = move-exception
            goto L_0x01e3
        L_0x01a2:
            r0 = move-exception
            r1 = r26
            r14 = r28
            goto L_0x01d8
        L_0x01a8:
            r0 = move-exception
            r26 = r11
            r28 = r14
            goto L_0x01c2
        L_0x01ae:
            r26 = r11
            r14 = r2
            goto L_0x01cb
        L_0x01b2:
            r0 = move-exception
            r26 = r11
            r14 = r2
            goto L_0x01c2
        L_0x01b7:
            r0 = move-exception
            r26 = r11
            goto L_0x01e3
        L_0x01bb:
            r0 = move-exception
            r26 = r11
            r19 = r12
            r14 = r19
        L_0x01c2:
            r1 = r26
            goto L_0x01d8
        L_0x01c5:
            r26 = r11
            r19 = r12
            r14 = r19
        L_0x01cb:
            miui.util.IOUtils.closeQuietly(r26)
            goto L_0x01e2
        L_0x01cf:
            r0 = move-exception
            r26 = r1
            goto L_0x01e3
        L_0x01d3:
            r0 = move-exception
            r19 = r12
            r14 = r19
        L_0x01d8:
            java.lang.String r2 = "BehaviorRecord-Utils"
            java.lang.String r3 = "loadAllAppBehavior error"
            miui.util.Log.e(r2, r3, r0)     // Catch:{ all -> 0x01cf }
            miui.util.IOUtils.closeQuietly(r1)
        L_0x01e2:
            return r14
        L_0x01e3:
            miui.util.IOUtils.closeQuietly(r26)
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.permcenter.privacymanager.behaviorrecord.o.a(android.content.Context, java.util.List, int[]):boolean");
    }

    public static boolean a(String str, long j2) {
        List<String> list = e;
        return list.contains(str + "@" + j2);
    }

    public static int b(long j2) {
        if (g(j2)) {
            return n.get(Long.valueOf(j2)).intValue();
        }
        return 0;
    }

    public static int b(long j2, int i2) {
        return (i2 == 1 ? j : g).getOrDefault(Long.valueOf(j2), 0).intValue();
    }

    public static String b(Context context, String str) {
        if (str == null) {
            return null;
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat(context.getResources().getString(R.string.app_behavior_header_time));
        Calendar instance = Calendar.getInstance();
        instance.setTime(new Date(System.currentTimeMillis()));
        Calendar instance2 = Calendar.getInstance();
        try {
            Date parse = simpleDateFormat.parse(str);
            instance2.setTime(parse);
            if (instance.get(1) == instance2.get(1)) {
                int i2 = instance.get(6) - instance2.get(6);
                if (i2 == 0) {
                    return context.getResources().getString(R.string.app_behavior_time_today);
                }
                if (i2 == 1) {
                    return context.getResources().getString(R.string.app_behavior_time_yesterday);
                }
            }
            return simpleDateFormat2.format(parse);
        } catch (ParseException e2) {
            e2.printStackTrace();
            return null;
        }
    }

    public static String b(String str) {
        if (str == null) {
            return null;
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        try {
            return new SimpleDateFormat(WarningCenterAlertAdapter.FORMAT_TIME).format(simpleDateFormat.parse(str));
        } catch (ParseException e2) {
            e2.printStackTrace();
            return null;
        }
    }

    public static boolean b() {
        return com.miui.common.persistence.b.a("PrivacyList", (ArrayList<String>) new ArrayList()).size() > 0;
    }

    public static boolean b(Context context) {
        return x.e(context, "com.lbe.security.miui") >= 120 && com.miui.common.persistence.b.a("app_support_foreground", true);
    }

    public static boolean b(String str, int i2) {
        ArrayList<String> a2 = com.miui.common.persistence.b.a("PrivacyList", (ArrayList<String>) new ArrayList());
        return a2.contains(str + "@" + i2);
    }

    public static boolean b(String str, String str2) {
        if (!f.contains(str)) {
            return f.contains(str2) && !d(str);
        }
        return true;
    }

    public static int c(long j2) {
        if (f(j2)) {
            return m.get(Long.valueOf(j2)).intValue();
        }
        return 0;
    }

    public static void c() {
        if (com.miui.common.persistence.b.a("SpecialTreat", true)) {
            com.miui.common.persistence.b.b("0@com.tencent.mm", 1);
            com.miui.common.persistence.b.b("10@com.tencent.mm", 1);
            com.miui.common.persistence.b.b("999@com.tencent.mm", 1);
            com.miui.common.persistence.b.b("SpecialTreat", false);
            Log.i("BehaviorRecord-Utils", "setSpecialApp success");
        }
    }

    public static boolean c(Context context) {
        return x.e(context, "com.lbe.security.miui") >= 124 && com.miui.common.persistence.b.a("behavior_source_enable", true);
    }

    public static boolean c(Context context, String str) {
        if (context == null) {
            return false;
        }
        Intent intent = new Intent();
        intent.setAction("android.intent.action.MAIN");
        intent.addCategory("android.intent.category.LAUNCHER");
        intent.setPackage(str);
        List<ResolveInfo> queryIntentActivities = context.getPackageManager().queryIntentActivities(intent, 786496);
        return queryIntentActivities != null && queryIntentActivities.size() > 0;
    }

    public static boolean c(String str) {
        return TextUtils.isEmpty(str) || TextUtils.equals("null", str);
    }

    public static boolean d(long j2) {
        return g.containsKey(Long.valueOf(j2));
    }

    public static boolean d(Context context) {
        return Settings.Secure.getInt(context.getContentResolver(), "PERMISSION_USE_WARNING", 0) == 1;
    }

    public static boolean d(String str) {
        return i.containsKey(str);
    }

    public static HashMap<Long, Integer> e(Context context) {
        Context context2 = context;
        HashMap<Long, Integer> hashMap = new HashMap<>();
        ArrayList<a> a2 = n.a(context2, 32);
        ArrayList<a> a3 = n.a(context2, (long) PermissionManager.PERM_ID_READCONTACT);
        ArrayList<a> a4 = n.a(context2, 8);
        ArrayList<a> a5 = n.a(context2, 1073741824);
        ArrayList<a> a6 = n.a(context2, 16);
        ArrayList<a> a7 = n.a(context2, (long) PermissionManager.PERM_ID_AUDIO_RECORDER);
        ArrayList<a> a8 = n.a(context2, (long) PermissionManager.PERM_ID_EXTERNAL_STORAGE);
        hashMap.put(32L, Integer.valueOf(a((List<a>) a2, 32)));
        hashMap.put(8L, Integer.valueOf(a((List<a>) a4, 8) + a((List<a>) a3, (long) PermissionManager.PERM_ID_READCONTACT)));
        hashMap.put(16L, Integer.valueOf(a((List<a>) a6, 16) + a((List<a>) a5, 1073741824)));
        hashMap.put(Long.valueOf(PermissionManager.PERM_ID_AUDIO_RECORDER), Integer.valueOf(a((List<a>) a7, (long) PermissionManager.PERM_ID_AUDIO_RECORDER)));
        hashMap.put(Long.valueOf(PermissionManager.PERM_ID_EXTERNAL_STORAGE), Integer.valueOf(a((List<a>) a8, (long) PermissionManager.PERM_ID_EXTERNAL_STORAGE)));
        return hashMap;
    }

    public static boolean e(long j2) {
        return h.containsKey(Long.valueOf(j2));
    }

    public static boolean e(String str) {
        Calendar instance = Calendar.getInstance();
        instance.setTime(new Date(System.currentTimeMillis()));
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Calendar instance2 = Calendar.getInstance();
        try {
            instance2.setTime(simpleDateFormat.parse(str));
            if (instance.get(1) == instance2.get(1)) {
                return instance.get(6) - instance2.get(6) == 0;
            }
        } catch (ParseException e2) {
            e2.printStackTrace();
        }
        return false;
    }

    /* JADX WARNING: Removed duplicated region for block: B:43:0x0128 A[LOOP:1: B:41:0x0122->B:43:0x0128, LOOP_END] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static java.util.HashMap<java.lang.Long, java.util.ArrayList<com.miui.permcenter.privacymanager.a.d>> f(android.content.Context r23) {
        /*
            java.util.HashMap r1 = new java.util.HashMap
            r1.<init>()
            java.lang.String r2 = "permissionId"
            java.lang.String r3 = "pkgName"
            java.lang.String r4 = "calleePkg"
            java.lang.String r5 = "mode"
            java.lang.String r6 = "endTime"
            java.lang.String r7 = "count"
            java.lang.String r8 = "user"
            java.lang.String[] r11 = new java.lang.String[]{r2, r3, r4, r5, r6, r7, r8}
            java.lang.String r12 = "(permissionId == ? OR permissionId == ? OR permissionId == ? OR permissionId == ? OR permissionId == ? OR permissionId == ? OR permissionId == ? ) AND mode == ? AND calleePkg == 'null'"
            r0 = 8
            java.lang.String[] r13 = new java.lang.String[r0]
            r2 = 32
            java.lang.String r0 = java.lang.String.valueOf(r2)
            r4 = 0
            r13[r4] = r0
            r5 = 2147483648(0x80000000, double:1.0609978955E-314)
            java.lang.String r0 = java.lang.String.valueOf(r5)
            r7 = 1
            r13[r7] = r0
            r15 = 8
            java.lang.String r0 = java.lang.String.valueOf(r15)
            r8 = 2
            r13[r8] = r0
            r8 = 35184372088832(0x200000000000, double:1.73833895195875E-310)
            java.lang.String r0 = java.lang.String.valueOf(r8)
            r8 = 3
            r13[r8] = r0
            r8 = 131072(0x20000, double:6.47582E-319)
            java.lang.String r0 = java.lang.String.valueOf(r8)
            r8 = 4
            r13[r8] = r0
            r17 = 16
            java.lang.String r0 = java.lang.String.valueOf(r17)
            r14 = 5
            r13[r14] = r0
            r19 = 1073741824(0x40000000, double:5.304989477E-315)
            java.lang.String r0 = java.lang.String.valueOf(r19)
            r10 = 6
            r13[r10] = r0
            java.lang.String r0 = java.lang.String.valueOf(r4)
            r9 = 7
            r13[r9] = r0
            java.lang.String r0 = "endTime DESC"
            r21 = 0
            android.content.ContentResolver r9 = r23.getContentResolver()     // Catch:{ Exception -> 0x010a }
            android.net.Uri r22 = com.miui.permission.PermissionContract.RECORD_URI     // Catch:{ Exception -> 0x010a }
            r15 = r10
            r10 = r22
            r5 = r14
            r14 = r0
            android.database.Cursor r6 = r9.query(r10, r11, r12, r13, r14)     // Catch:{ Exception -> 0x010a }
            if (r6 == 0) goto L_0x0102
        L_0x0080:
            boolean r0 = r6.moveToNext()     // Catch:{ Exception -> 0x00fe, all -> 0x00fc }
            if (r0 == 0) goto L_0x0102
            java.lang.String r0 = r6.getString(r7)     // Catch:{ Exception -> 0x00fe, all -> 0x00fc }
            int r9 = r6.getInt(r15)     // Catch:{ Exception -> 0x00fe, all -> 0x00fc }
            java.lang.String r10 = r6.getString(r8)     // Catch:{ Exception -> 0x00fe, all -> 0x00fc }
            r11 = r23
            boolean r12 = a((android.content.Context) r11, (java.lang.String) r0, (int) r9)     // Catch:{ Exception -> 0x00fe, all -> 0x00fc }
            if (r12 != 0) goto L_0x00f6
            boolean r10 = e((java.lang.String) r10)     // Catch:{ Exception -> 0x00fe, all -> 0x00fc }
            if (r10 != 0) goto L_0x00a1
            goto L_0x0080
        L_0x00a1:
            long r12 = r6.getLong(r4)     // Catch:{ Exception -> 0x00fe, all -> 0x00fc }
            int r10 = (r12 > r2 ? 1 : (r12 == r2 ? 0 : -1))
            if (r10 != 0) goto L_0x00ab
            r10 = r7
            goto L_0x00af
        L_0x00ab:
            int r10 = r6.getInt(r5)     // Catch:{ Exception -> 0x00fe, all -> 0x00fc }
        L_0x00af:
            r21 = 2147483648(0x80000000, double:1.0609978955E-314)
            int r14 = (r12 > r21 ? 1 : (r12 == r21 ? 0 : -1))
            if (r14 != 0) goto L_0x00bb
            r12 = 8
            goto L_0x00c1
        L_0x00bb:
            int r14 = (r12 > r19 ? 1 : (r12 == r19 ? 0 : -1))
            if (r14 != 0) goto L_0x00c1
            r12 = r17
        L_0x00c1:
            java.lang.Long r14 = java.lang.Long.valueOf(r12)     // Catch:{ Exception -> 0x00fe, all -> 0x00fc }
            java.util.ArrayList r2 = new java.util.ArrayList     // Catch:{ Exception -> 0x00fe, all -> 0x00fc }
            r2.<init>()     // Catch:{ Exception -> 0x00fe, all -> 0x00fc }
            java.lang.Object r2 = r1.getOrDefault(r14, r2)     // Catch:{ Exception -> 0x00fe, all -> 0x00fc }
            java.util.ArrayList r2 = (java.util.ArrayList) r2     // Catch:{ Exception -> 0x00fe, all -> 0x00fc }
            com.miui.permcenter.privacymanager.a.d r3 = new com.miui.permcenter.privacymanager.a.d     // Catch:{ Exception -> 0x00fe, all -> 0x00fc }
            r3.<init>(r9, r0)     // Catch:{ Exception -> 0x00fe, all -> 0x00fc }
            int r0 = r2.indexOf(r3)     // Catch:{ Exception -> 0x00fe, all -> 0x00fc }
            r9 = -1
            if (r0 == r9) goto L_0x00e6
            java.lang.Object r0 = r2.get(r0)     // Catch:{ Exception -> 0x00fe, all -> 0x00fc }
            com.miui.permcenter.privacymanager.a.d r0 = (com.miui.permcenter.privacymanager.a.d) r0     // Catch:{ Exception -> 0x00fe, all -> 0x00fc }
            r0.a(r10)     // Catch:{ Exception -> 0x00fe, all -> 0x00fc }
            goto L_0x00ec
        L_0x00e6:
            r3.a(r10)     // Catch:{ Exception -> 0x00fe, all -> 0x00fc }
            r2.add(r3)     // Catch:{ Exception -> 0x00fe, all -> 0x00fc }
        L_0x00ec:
            java.lang.Long r0 = java.lang.Long.valueOf(r12)     // Catch:{ Exception -> 0x00fe, all -> 0x00fc }
            r1.put(r0, r2)     // Catch:{ Exception -> 0x00fe, all -> 0x00fc }
            r2 = 32
            goto L_0x0080
        L_0x00f6:
            r21 = 2147483648(0x80000000, double:1.0609978955E-314)
            goto L_0x0080
        L_0x00fc:
            r0 = move-exception
            goto L_0x014a
        L_0x00fe:
            r0 = move-exception
            r21 = r6
            goto L_0x010b
        L_0x0102:
            miui.util.IOUtils.closeQuietly(r6)
            goto L_0x0115
        L_0x0106:
            r0 = move-exception
            r6 = r21
            goto L_0x014a
        L_0x010a:
            r0 = move-exception
        L_0x010b:
            java.lang.String r2 = "BehaviorRecord-Utils"
            java.lang.String r3 = "loadPermStatistics error "
            miui.util.Log.e(r2, r3, r0)     // Catch:{ all -> 0x0106 }
            miui.util.IOUtils.closeQuietly(r21)
        L_0x0115:
            java.util.HashMap r0 = new java.util.HashMap
            r0.<init>()
            java.util.Set r1 = r1.entrySet()
            java.util.Iterator r1 = r1.iterator()
        L_0x0122:
            boolean r2 = r1.hasNext()
            if (r2 == 0) goto L_0x0149
            java.lang.Object r2 = r1.next()
            java.util.Map$Entry r2 = (java.util.Map.Entry) r2
            java.util.ArrayList r3 = new java.util.ArrayList
            java.lang.Object r4 = r2.getValue()
            java.util.Collection r4 = (java.util.Collection) r4
            r3.<init>(r4)
            com.miui.permcenter.privacymanager.behaviorrecord.n r4 = new com.miui.permcenter.privacymanager.behaviorrecord.n
            r4.<init>()
            java.util.Collections.sort(r3, r4)
            java.lang.Object r2 = r2.getKey()
            r0.put(r2, r3)
            goto L_0x0122
        L_0x0149:
            return r0
        L_0x014a:
            miui.util.IOUtils.closeQuietly(r6)
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.permcenter.privacymanager.behaviorrecord.o.f(android.content.Context):java.util.HashMap");
    }

    public static boolean f(long j2) {
        return m.containsKey(Long.valueOf(j2));
    }

    public static boolean f(String str) {
        return str != null && f6457d.contains(str);
    }

    public static E g(String str) {
        String f2;
        ArrayList arrayList;
        E e2 = new E();
        if (!(str == null || (f2 = AppManageUtils.f(str)) == null || (arrayList = ChinesePinyinConverter.getInstance().get(f2)) == null || arrayList.size() <= 0)) {
            Iterator it = arrayList.iterator();
            while (it.hasNext()) {
                ChinesePinyinConverter.Token token = (ChinesePinyinConverter.Token) it.next();
                String str2 = token.target;
                if (str2 != null && str2.length() > 0) {
                    e2.f3574a.append(token.target);
                    e2.f3575b.append(token.target.charAt(0));
                }
            }
        }
        return e2;
    }

    public static boolean g(long j2) {
        return n.containsKey(Long.valueOf(j2));
    }

    public static boolean h(long j2) {
        return p.contains(Long.valueOf(j2));
    }
}
