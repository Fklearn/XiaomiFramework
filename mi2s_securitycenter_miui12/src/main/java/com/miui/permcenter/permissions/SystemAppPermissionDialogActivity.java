package com.miui.permcenter.permissions;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import com.miui.networkassistant.config.Constants;
import com.miui.permcenter.n;
import com.miui.permission.RequiredPermissionsUtil;
import com.miui.securitycenter.R;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import miui.app.Activity;
import miui.app.AlertDialog;

public class SystemAppPermissionDialogActivity extends b.b.c.c.a {

    /* renamed from: a  reason: collision with root package name */
    private static final List<String> f6242a = new ArrayList();

    /* renamed from: b  reason: collision with root package name */
    private static final HashMap<String, String> f6243b = new HashMap<>();
    private int A;
    private a B;
    private b C;

    /* renamed from: c  reason: collision with root package name */
    private boolean f6244c;

    /* renamed from: d  reason: collision with root package name */
    private PackageInfo f6245d;
    private ApplicationInfo e;
    private CharSequence f;
    private String g;
    private String h;
    private boolean i;
    private String j;
    private String[] k;
    private String[] l;
    private HashSet<String> m;
    private boolean n;
    private String[] o;
    private String[] p;
    private String q;
    private String r;
    private String s;
    private AlertDialog t;
    private View u;
    private boolean v;
    private boolean w;
    private ScrollView x;
    public String y;
    /* access modifiers changed from: private */
    public String z;

    private static class a extends AsyncTask<Void, Void, Boolean> {

        /* renamed from: a  reason: collision with root package name */
        private WeakReference<SystemAppPermissionDialogActivity> f6246a;

        a(SystemAppPermissionDialogActivity systemAppPermissionDialogActivity) {
            this.f6246a = new WeakReference<>(systemAppPermissionDialogActivity);
        }

        /* access modifiers changed from: protected */
        /* renamed from: a */
        public Boolean doInBackground(Void... voidArr) {
            Activity activity = (SystemAppPermissionDialogActivity) this.f6246a.get();
            return Boolean.valueOf((isCancelled() || activity == null || activity.isDestroyed() || activity.isFinishing()) ? false : n.d(activity, activity.y));
        }

        /* access modifiers changed from: protected */
        /* renamed from: a */
        public void onPostExecute(Boolean bool) {
            super.onPostExecute(bool);
            SystemAppPermissionDialogActivity systemAppPermissionDialogActivity = (SystemAppPermissionDialogActivity) this.f6246a.get();
            if (systemAppPermissionDialogActivity != null) {
                if (bool.booleanValue()) {
                    systemAppPermissionDialogActivity.l();
                    return;
                }
                Log.e("SystemAppPDA", "can't launch this for out of whiteList, " + systemAppPermissionDialogActivity.y);
                systemAppPermissionDialogActivity.setResult(-2);
                systemAppPermissionDialogActivity.finish();
            }
        }
    }

    private static class b extends BroadcastReceiver {

        /* renamed from: a  reason: collision with root package name */
        SystemAppPermissionDialogActivity f6247a;

        public b(SystemAppPermissionDialogActivity systemAppPermissionDialogActivity) {
            this.f6247a = systemAppPermissionDialogActivity;
        }

        public void onReceive(Context context, Intent intent) {
            if (Constants.System.ACTION_LOCALE_CHANGED.equals(intent.getAction())) {
                this.f6247a.setResult(-3);
                this.f6247a.finish();
                Log.e("SystemAppPDA", "finish for local changed, need new intent");
            }
        }
    }

    static {
        f6242a.add("android.permission.ACCESS_FINE_LOCATION");
        f6242a.add("android.permission.ACCESS_COARSE_LOCATION");
        f6242a.add("android.permission.ACCESS_BACKGROUND_LOCATION");
        f6242a.add("android.permission.CAMERA");
        f6242a.add("android.permission.RECORD_AUDIO");
        f6242a.add("android.permission.READ_CONTACTS");
        f6242a.add("android.permission.WRITE_CONTACTS");
        f6242a.add("android.permission.READ_CALL_LOG");
        f6242a.add("android.permission.WRITE_CALL_LOG");
        f6242a.add("android.permission.SEND_SMS");
        f6242a.add("android.permission.READ_SMS");
        f6242a.add("android.permission.CALL_PHONE");
        f6242a.add("android.permission.BLUETOOTH_ADMIN");
        f6242a.add("android.permission.CHANGE_WIFI_STATE");
        f6242a.add("android.permission.NFC");
        f6243b.put("android.permission.READ_CALENDAR", "android.permission-group.CALENDAR");
        f6243b.put("android.permission.WRITE_CALENDAR", "android.permission-group.CALENDAR");
        f6243b.put("android.permission.READ_EXTERNAL_STORAGE", "android.permission-group.STORAGE");
        f6243b.put("android.permission.WRITE_EXTERNAL_STORAGE", "android.permission-group.STORAGE");
        f6243b.put("android.permission.ACCESS_MEDIA_LOCATION", "android.permission-group.STORAGE");
        f6243b.put("android.permission.ACCESS_FINE_LOCATION", "android.permission-group.LOCATION");
        f6243b.put("android.permission.ACCESS_COARSE_LOCATION", "android.permission-group.LOCATION");
        f6243b.put("android.permission.ACCESS_BACKGROUND_LOCATION", "android.permission-group.LOCATION");
        f6243b.put("android.permission.RECORD_AUDIO", "android.permission-group.MICROPHONE");
        f6243b.put("android.permission.CAMERA", "android.permission-group.CAMERA");
    }

    /* access modifiers changed from: private */
    public int a(ListView listView) {
        int makeMeasureSpec = View.MeasureSpec.makeMeasureSpec((listView.getMeasuredWidth() - listView.getPaddingStart()) - listView.getPaddingStart(), Integer.MIN_VALUE);
        int i2 = 0;
        for (int i3 = 0; i3 < listView.getCount(); i3++) {
            View view = listView.getAdapter().getView(i3, (View) null, listView);
            view.measure(makeMeasureSpec, makeMeasureSpec);
            i2 += view.getMeasuredHeight();
        }
        return i2 + (listView.getDividerHeight() * (listView.getCount() - 1)) + listView.getPaddingTop() + listView.getPaddingBottom();
    }

    /* JADX WARNING: type inference failed for: r13v0, types: [android.content.Context, miui.app.Activity, com.miui.permcenter.permissions.SystemAppPermissionDialogActivity] */
    /* JADX WARNING: Removed duplicated region for block: B:31:0x00e3  */
    /* JADX WARNING: Removed duplicated region for block: B:32:0x00ec  */
    /* JADX WARNING: Removed duplicated region for block: B:35:0x00f9  */
    /* JADX WARNING: Removed duplicated region for block: B:40:0x0116  */
    /* JADX WARNING: Removed duplicated region for block: B:45:0x012e  */
    /* JADX WARNING: Removed duplicated region for block: B:46:0x0132  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void initView() {
        /*
            r13 = this;
            boolean r0 = r13.f6244c
            if (r0 == 0) goto L_0x0016
            android.view.Window r0 = r13.getWindow()
            r1 = 524288(0x80000, float:7.34684E-40)
            r0.addFlags(r1)
            android.view.Window r0 = r13.getWindow()
            r1 = 2097152(0x200000, float:2.938736E-39)
            r0.addFlags(r1)
        L_0x0016:
            android.view.LayoutInflater r0 = r13.getLayoutInflater()
            r1 = 2131493029(0x7f0c00a5, float:1.8609527E38)
            r2 = 0
            android.view.View r0 = r0.inflate(r1, r2)
            r13.u = r0
            android.view.View r0 = r13.u
            r1 = 2131297600(0x7f090540, float:1.821315E38)
            android.view.View r0 = r0.findViewById(r1)
            android.widget.ScrollView r0 = (android.widget.ScrollView) r0
            r13.x = r0
            android.view.View r0 = r13.u
            r1 = 2131297297(0x7f090411, float:1.8212535E38)
            android.view.View r0 = r0.findViewById(r1)
            android.widget.TextView r0 = (android.widget.TextView) r0
            android.view.View r1 = r13.u
            r2 = 2131297383(0x7f090467, float:1.821271E38)
            android.view.View r1 = r1.findViewById(r2)
            android.widget.TextView r1 = (android.widget.TextView) r1
            android.view.View r2 = r13.u
            r3 = 2131296729(0x7f0901d9, float:1.8211383E38)
            android.view.View r2 = r2.findViewById(r3)
            android.widget.TextView r2 = (android.widget.TextView) r2
            android.view.View r3 = r13.u
            r4 = 2131297450(0x7f0904aa, float:1.8212845E38)
            android.view.View r3 = r3.findViewById(r4)
            com.miui.powercenter.view.ScrollListView r3 = (com.miui.powercenter.view.ScrollListView) r3
            java.lang.String[] r4 = r13.k
            r5 = 8
            r6 = 0
            if (r4 == 0) goto L_0x0067
            int r4 = r4.length
            if (r4 != 0) goto L_0x0089
        L_0x0067:
            java.util.HashSet<java.lang.String> r4 = r13.m
            int r4 = r4.size()
            if (r4 != 0) goto L_0x0089
            java.lang.String[] r4 = r13.l
            if (r4 == 0) goto L_0x0077
            int r4 = r4.length
            if (r4 == 0) goto L_0x0077
            goto L_0x0089
        L_0x0077:
            r3.setVisibility(r5)
            boolean r3 = r13.i
            if (r3 == 0) goto L_0x0086
            r3 = 2131758208(0x7f100c80, float:1.9147374E38)
        L_0x0081:
            java.lang.String r3 = r13.getString(r3)
            goto L_0x00db
        L_0x0086:
            java.lang.String r3 = ""
            goto L_0x00db
        L_0x0089:
            r3.setScrollEnable(r6)
            java.util.HashSet<java.lang.String> r4 = r13.m
            int r4 = r4.size()
            java.lang.String[] r4 = new java.lang.String[r4]
            java.lang.String[] r12 = r13.p
            if (r12 == 0) goto L_0x00a6
            com.miui.permcenter.f r4 = new com.miui.permcenter.f
            java.lang.String[] r9 = r13.k
            java.lang.String[] r10 = r13.o
            java.lang.String[] r11 = r13.l
            r7 = r4
            r8 = r13
            r7.<init>(r8, r9, r10, r11, r12)
            goto L_0x00b8
        L_0x00a6:
            com.miui.permcenter.f r7 = new com.miui.permcenter.f
            java.lang.String[] r8 = r13.k
            java.lang.String[] r9 = r13.o
            java.util.HashSet<java.lang.String> r10 = r13.m
            java.lang.Object[] r4 = r10.toArray(r4)
            java.lang.String[] r4 = (java.lang.String[]) r4
            r7.<init>(r13, r8, r9, r4)
            r4 = r7
        L_0x00b8:
            r3.setAdapter(r4)
            android.view.ViewTreeObserver r4 = r3.getViewTreeObserver()
            com.miui.permcenter.permissions.H r7 = new com.miui.permcenter.permissions.H
            r7.<init>(r13, r3)
            r4.addOnGlobalLayoutListener(r7)
            boolean r3 = r13.w
            if (r3 == 0) goto L_0x00cf
            r3 = 2131758196(0x7f100c74, float:1.914735E38)
            goto L_0x0081
        L_0x00cf:
            boolean r3 = r13.i
            if (r3 == 0) goto L_0x00d7
            r3 = 2131758206(0x7f100c7e, float:1.914737E38)
            goto L_0x0081
        L_0x00d7:
            r3 = 2131758207(0x7f100c7f, float:1.9147371E38)
            goto L_0x0081
        L_0x00db:
            java.lang.String r4 = r13.q
            boolean r4 = android.text.TextUtils.isEmpty(r4)
            if (r4 != 0) goto L_0x00ec
            r1.setVisibility(r6)
            java.lang.String r4 = r13.q
            r1.setText(r4)
            goto L_0x00ef
        L_0x00ec:
            r1.setVisibility(r5)
        L_0x00ef:
            java.lang.String r1 = r13.h
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            r4 = 2
            r7 = 1
            if (r1 == 0) goto L_0x0116
            r1 = 2131758199(0x7f100c77, float:1.9147355E38)
            java.lang.Object[] r8 = new java.lang.Object[r4]
            java.lang.String r9 = r13.j
            boolean r9 = android.text.TextUtils.isEmpty(r9)
            if (r9 == 0) goto L_0x0109
            java.lang.CharSequence r9 = r13.f
            goto L_0x010b
        L_0x0109:
            java.lang.String r9 = r13.j
        L_0x010b:
            r8[r6] = r9
            java.lang.String r9 = r13.g
            r8[r7] = r9
            java.lang.String r1 = r13.getString(r1, r8)
            goto L_0x0118
        L_0x0116:
            java.lang.String r1 = r13.h
        L_0x0118:
            r0.setText(r1)
            r0.append(r3)
            java.lang.String r0 = r13.r
            boolean r0 = android.text.TextUtils.isEmpty(r0)
            if (r0 == 0) goto L_0x0132
            java.lang.String r0 = r13.s
            boolean r0 = android.text.TextUtils.isEmpty(r0)
            if (r0 == 0) goto L_0x0132
            r2.setVisibility(r5)
            goto L_0x0193
        L_0x0132:
            java.lang.String r0 = r13.r
            boolean r0 = android.text.TextUtils.isEmpty(r0)
            if (r0 == 0) goto L_0x015b
            java.lang.String r0 = r13.q
            boolean r0 = android.text.TextUtils.isEmpty(r0)
            if (r0 == 0) goto L_0x0146
            r0 = 2131758209(0x7f100c81, float:1.9147376E38)
            goto L_0x0149
        L_0x0146:
            r0 = 2131758201(0x7f100c79, float:1.914736E38)
        L_0x0149:
            java.lang.Object[] r1 = new java.lang.Object[r7]
            java.lang.String r3 = r13.s
            r1[r6] = r3
        L_0x014f:
            java.lang.String r0 = r13.getString(r0, r1)
            android.text.Spanned r0 = android.text.Html.fromHtml(r0)
            r2.setText(r0)
            goto L_0x0193
        L_0x015b:
            java.lang.String r0 = r13.s
            boolean r0 = android.text.TextUtils.isEmpty(r0)
            if (r0 == 0) goto L_0x0179
            java.lang.String r0 = r13.q
            boolean r0 = android.text.TextUtils.isEmpty(r0)
            if (r0 == 0) goto L_0x016f
            r0 = 2131758215(0x7f100c87, float:1.9147388E38)
            goto L_0x0172
        L_0x016f:
            r0 = 2131758202(0x7f100c7a, float:1.9147361E38)
        L_0x0172:
            java.lang.Object[] r1 = new java.lang.Object[r7]
            java.lang.String r3 = r13.r
            r1[r6] = r3
            goto L_0x014f
        L_0x0179:
            java.lang.String r0 = r13.q
            boolean r0 = android.text.TextUtils.isEmpty(r0)
            if (r0 == 0) goto L_0x0185
            r0 = 2131758198(0x7f100c76, float:1.9147353E38)
            goto L_0x0188
        L_0x0185:
            r0 = 2131758200(0x7f100c78, float:1.9147357E38)
        L_0x0188:
            java.lang.Object[] r1 = new java.lang.Object[r4]
            java.lang.String r3 = r13.r
            r1[r6] = r3
            java.lang.String r3 = r13.s
            r1[r7] = r3
            goto L_0x014f
        L_0x0193:
            android.text.method.MovementMethod r0 = android.text.method.LinkMovementMethod.getInstance()
            r2.setMovementMethod(r0)
            com.miui.permcenter.permissions.SystemAppPermissionDialogActivity$b r0 = new com.miui.permcenter.permissions.SystemAppPermissionDialogActivity$b
            r0.<init>(r13)
            r13.C = r0
            android.content.IntentFilter r0 = new android.content.IntentFilter
            r0.<init>()
            java.lang.String r1 = "android.intent.action.LOCALE_CHANGED"
            r0.addAction(r1)
            com.miui.permcenter.permissions.SystemAppPermissionDialogActivity$b r1 = r13.C
            r13.registerReceiver(r1, r0)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.permcenter.permissions.SystemAppPermissionDialogActivity.initView():void");
    }

    /* JADX WARNING: type inference failed for: r3v0, types: [android.content.Context, miui.app.Activity, com.miui.permcenter.permissions.SystemAppPermissionDialogActivity] */
    /* access modifiers changed from: private */
    public void l() {
        m();
        initView();
        this.t = new AlertDialog.Builder(this).setTitle(R.string.system_permission_declare_title).setView(this.u).setPositiveButton(R.string.system_permission_declare_agree, new G(this)).setNegativeButton(this.v ? R.string.exit : R.string.system_permission_declare_disagree, new F(this)).setCancelable(false).create();
        this.t.show();
        Window window = this.t.getWindow();
        if (window != null) {
            this.A = window.getAttributes().width;
        }
        ((FrameLayout) this.u.getParent().getParent()).setPadding(0, 0, 0, 0);
        int dimensionPixelSize = getResources().getDimensionPixelSize(R.dimen.view_dimen_120);
        this.x.setPadding(dimensionPixelSize, 0, dimensionPixelSize, 0);
    }

    private void m() {
        String[] strArr;
        String[] strArr2;
        String[] strArr3;
        String[] strArr4;
        String[] strArr5;
        Intent intent = getIntent();
        this.z = intent.getAction();
        this.f6244c = intent.getBooleanExtra("show_locked", false);
        this.j = intent.getStringExtra("app_name");
        this.g = intent.getStringExtra("main_purpose");
        this.h = intent.getStringExtra("all_purpose");
        this.i = intent.getBooleanExtra("use_network", true);
        this.k = intent.getStringArrayExtra("runtime_perm");
        this.o = intent.getStringArrayExtra("runtime_perm_desc");
        this.l = intent.getStringArrayExtra("optional_perm");
        this.p = intent.getStringArrayExtra("optional_perm_desc");
        this.n = intent.getBooleanExtra("optional_perm_show", false);
        this.q = intent.getStringExtra("agree_desc");
        this.r = intent.getStringExtra("user_agreement");
        this.s = intent.getStringExtra("privacy_policy");
        this.v = intent.getBooleanExtra("mandatory_permission", true);
        this.w = intent.getBooleanExtra("theme_analytics", false);
        if (TextUtils.isEmpty(this.g + this.h) || !(((strArr = this.k) == null || (strArr5 = this.o) == null || strArr.length == strArr5.length) && ((strArr2 = this.l) == null || (strArr4 = this.p) == null || strArr2.length == strArr4.length))) {
            Log.e("SystemAppPDA", "lack of necessary information!");
        } else {
            try {
                this.f6245d = getPackageManager().getPackageInfo(this.y, 4224);
                this.e = getPackageManager().getApplicationInfo(this.y, 0);
                this.f = this.e.loadLabel(getPackageManager());
                if (this.i && (strArr3 = this.k) != null && strArr3.length == 1 && "android.permission.READ_PHONE_STATE".equals(strArr3[0]) && !this.v) {
                    this.w = true;
                }
                this.m = new HashSet<>();
                if (this.n && this.l == null) {
                    String[] strArr6 = this.k;
                    ArrayList arrayList = strArr6 != null ? new ArrayList(Arrays.asList(strArr6)) : new ArrayList();
                    PackageInfo packageInfo = this.f6245d;
                    if (packageInfo.requestedPermissions != null) {
                        List<String> retrieveRequiredPermissions = RequiredPermissionsUtil.retrieveRequiredPermissions(packageInfo.applicationInfo);
                        for (String str : this.f6245d.requestedPermissions) {
                            if (f6242a.contains(str) && !arrayList.contains(str) && (retrieveRequiredPermissions == null || !retrieveRequiredPermissions.contains(str))) {
                                String str2 = f6243b.get(str);
                                if (str2 == null) {
                                    this.m.add(str);
                                } else if (!arrayList.contains(str2)) {
                                    this.m.add(str2);
                                }
                            }
                        }
                        return;
                    }
                    return;
                }
                return;
            } catch (Exception e2) {
                Log.e("SystemAppPDA", "get application info exception!" + this.y, e2);
            }
        }
        setResult(-1);
        finish();
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.y = getCallingPackage();
        this.B = new a(this);
        this.B.execute(new Void[0]);
    }

    /* access modifiers changed from: protected */
    public void onDestroy() {
        SystemAppPermissionDialogActivity.super.onDestroy();
        a aVar = this.B;
        if (aVar != null) {
            aVar.cancel(true);
        }
        b bVar = this.C;
        if (bVar != null) {
            unregisterReceiver(bVar);
        }
    }

    /* access modifiers changed from: protected */
    public void onResume() {
        Window window;
        super.onResume();
        AlertDialog alertDialog = this.t;
        if (alertDialog != null && !alertDialog.isShowing()) {
            this.t.show();
            if (this.A > 0 && (window = this.t.getWindow()) != null) {
                WindowManager.LayoutParams attributes = window.getAttributes();
                attributes.width = this.A;
                window.setAttributes(attributes);
            }
        }
    }

    /* access modifiers changed from: protected */
    public void onStop() {
        SystemAppPermissionDialogActivity.super.onStop();
        AlertDialog alertDialog = this.t;
        if (alertDialog != null) {
            alertDialog.dismiss();
        }
    }
}
