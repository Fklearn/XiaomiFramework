package com.miui.permcenter.privacymanager.behaviorrecord;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageDeleteObserver;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.UserHandle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import b.b.c.j.C;
import b.b.c.j.r;
import b.b.c.j.x;
import com.miui.appmanager.AppManageUtils;
import com.miui.networkassistant.config.Constants;
import com.miui.permcenter.n;
import com.miui.permcenter.widget.b;
import com.miui.permission.PermissionContract;
import com.miui.permission.PermissionManager;
import com.miui.securitycenter.R;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import miui.app.Activity;
import miui.os.Build;
import miui.widget.GuidePopupWindow;
import miui.widget.ProgressBar;

public class PrivacyDetailActivity extends b.b.c.c.a {
    /* access modifiers changed from: private */
    public List<com.miui.permcenter.privacymanager.a.a> A;
    /* access modifiers changed from: private */
    public HashMap<String, ArrayList<Integer>> B;
    /* access modifiers changed from: private */
    public AtomicInteger C;
    /* access modifiers changed from: private */
    public volatile boolean D = true;
    /* access modifiers changed from: private */
    public f E;
    /* access modifiers changed from: private */
    public String F;
    private long G;
    /* access modifiers changed from: private */
    public int H;
    /* access modifiers changed from: private */
    public String I;
    /* access modifiers changed from: private */
    public ApplicationInfo J;
    private PackageInfo K;
    private DevicePolicyManager L;
    /* access modifiers changed from: private */
    public PackageManager M;
    private boolean N;
    private boolean O = false;
    private boolean P = false;
    /* access modifiers changed from: private */
    public Object Q;
    private d R;
    private h S;
    private int T = b.b.c.j.e.b();
    private HashSet<String> U;
    /* access modifiers changed from: private */
    public g V;
    private LoaderManager W;
    /* access modifiers changed from: private */
    public j X;
    private k Y;
    /* access modifiers changed from: private */
    public Map<Long, String> Z = new HashMap();

    /* renamed from: a  reason: collision with root package name */
    private ImageView f6400a;
    /* access modifiers changed from: private */
    public Map<Long, Integer> aa = new HashMap();

    /* renamed from: b  reason: collision with root package name */
    private ImageView f6401b;
    private Intent ba;

    /* renamed from: c  reason: collision with root package name */
    private GuidePopupWindow f6402c;
    private boolean ca = false;

    /* renamed from: d  reason: collision with root package name */
    private boolean f6403d;
    private View.OnClickListener da = new p(this);
    private com.miui.permcenter.privacymanager.a.c e;
    private RecyclerView.l ea = new q(this);
    private MenuItem f;
    private LoaderManager.LoaderCallbacks fa = new r(this);
    /* access modifiers changed from: private */
    public MenuItem g;
    private com.miui.permcenter.b.c ga = new t(this);
    /* access modifiers changed from: private */
    public MenuItem h;
    private com.miui.permcenter.b.c ha = new u(this);
    /* access modifiers changed from: private */
    public int i;
    private com.miui.permcenter.b.b ia = new v(this);
    private int j;
    /* access modifiers changed from: private */
    public i ja = new w(this);
    private boolean k = false;
    private ImageView l;
    private TextView m;
    private TextView n;
    /* access modifiers changed from: private */
    public LinearLayoutManager o;
    private com.miui.permcenter.privacymanager.g p;
    /* access modifiers changed from: private */
    public volatile long q = 0;
    private miuix.recyclerview.widget.RecyclerView r;
    private com.miui.permcenter.widget.c s;
    private z t;
    private l u;
    /* access modifiers changed from: private */
    public ProgressBar v;
    private View w;
    private miuix.recyclerview.widget.RecyclerView x;
    /* access modifiers changed from: private */
    public a y;
    private com.miui.permcenter.widget.b z;

    private static class a implements DialogInterface.OnClickListener {

        /* renamed from: a  reason: collision with root package name */
        private WeakReference<PrivacyDetailActivity> f6404a;

        public a(PrivacyDetailActivity privacyDetailActivity) {
            this.f6404a = new WeakReference<>(privacyDetailActivity);
        }

        public void onClick(DialogInterface dialogInterface, int i) {
            PrivacyDetailActivity privacyDetailActivity = (PrivacyDetailActivity) this.f6404a.get();
            if (privacyDetailActivity != null && !privacyDetailActivity.isFinishing() && !privacyDetailActivity.isDestroyed()) {
                privacyDetailActivity.w();
            }
        }
    }

    private static class b implements DialogInterface.OnClickListener {

        /* renamed from: a  reason: collision with root package name */
        private WeakReference<PrivacyDetailActivity> f6405a;

        public b(PrivacyDetailActivity privacyDetailActivity) {
            this.f6405a = new WeakReference<>(privacyDetailActivity);
        }

        public void onClick(DialogInterface dialogInterface, int i) {
            PrivacyDetailActivity privacyDetailActivity = (PrivacyDetailActivity) this.f6405a.get();
            if (privacyDetailActivity != null && !privacyDetailActivity.isFinishing() && !privacyDetailActivity.isDestroyed()) {
                privacyDetailActivity.c(1);
            }
        }
    }

    private static class c implements DialogInterface.OnClickListener {

        /* renamed from: a  reason: collision with root package name */
        private WeakReference<PrivacyDetailActivity> f6406a;

        public c(PrivacyDetailActivity privacyDetailActivity) {
            this.f6406a = new WeakReference<>(privacyDetailActivity);
        }

        public void onClick(DialogInterface dialogInterface, int i) {
            PrivacyDetailActivity privacyDetailActivity = (PrivacyDetailActivity) this.f6406a.get();
            if (privacyDetailActivity != null && !privacyDetailActivity.isFinishing() && privacyDetailActivity.isDestroyed()) {
                privacyDetailActivity.b(3);
            }
        }
    }

    private static class d extends AsyncTask<Void, Void, Void> {

        /* renamed from: a  reason: collision with root package name */
        private WeakReference<PrivacyDetailActivity> f6407a;

        /* renamed from: b  reason: collision with root package name */
        private int f6408b;

        public d(PrivacyDetailActivity privacyDetailActivity, int i) {
            this.f6407a = new WeakReference<>(privacyDetailActivity);
            this.f6408b = i;
        }

        /* access modifiers changed from: protected */
        /* renamed from: a */
        public Void doInBackground(Void... voidArr) {
            PrivacyDetailActivity privacyDetailActivity;
            if (isCancelled() || (privacyDetailActivity = (PrivacyDetailActivity) this.f6407a.get()) == null || privacyDetailActivity.isFinishing()) {
                return null;
            }
            privacyDetailActivity.M.setApplicationEnabledSetting(privacyDetailActivity.F, this.f6408b, 0);
            privacyDetailActivity.V.sendEmptyMessage(1);
            return null;
        }

        /* access modifiers changed from: protected */
        /* renamed from: a */
        public void onPostExecute(Void voidR) {
            MenuItem menuItem;
            boolean z;
            PrivacyDetailActivity privacyDetailActivity = (PrivacyDetailActivity) this.f6407a.get();
            if (privacyDetailActivity != null && !privacyDetailActivity.isFinishing()) {
                if (AppManageUtils.f3485c.contains(privacyDetailActivity.F)) {
                    menuItem = privacyDetailActivity.h;
                    z = true;
                } else {
                    menuItem = privacyDetailActivity.h;
                    z = false;
                }
                menuItem.setEnabled(z);
            }
        }
    }

    private static class e implements DialogInterface.OnClickListener {

        /* renamed from: a  reason: collision with root package name */
        private WeakReference<PrivacyDetailActivity> f6409a;

        public e(PrivacyDetailActivity privacyDetailActivity) {
            this.f6409a = new WeakReference<>(privacyDetailActivity);
        }

        public void onClick(DialogInterface dialogInterface, int i) {
            PrivacyDetailActivity privacyDetailActivity = (PrivacyDetailActivity) this.f6409a.get();
            if (privacyDetailActivity != null && !privacyDetailActivity.isFinishing() && !privacyDetailActivity.isDestroyed()) {
                privacyDetailActivity.o();
            }
        }
    }

    private static class f extends AsyncTask<Void, Void, List<com.miui.permcenter.privacymanager.a.a>> {

        /* renamed from: a  reason: collision with root package name */
        private WeakReference<PrivacyDetailActivity> f6410a;

        public f(PrivacyDetailActivity privacyDetailActivity) {
            this.f6410a = new WeakReference<>(privacyDetailActivity);
        }

        /* access modifiers changed from: protected */
        /* renamed from: a */
        public List<com.miui.permcenter.privacymanager.a.a> doInBackground(Void... voidArr) {
            PrivacyDetailActivity privacyDetailActivity = (PrivacyDetailActivity) this.f6410a.get();
            if (!isCancelled() && !privacyDetailActivity.isFinishing() && !privacyDetailActivity.isDestroyed()) {
                Log.i("BehaviorRecord-SINGLE", "Loading more doInBackground ...");
                boolean unused = privacyDetailActivity.D = false;
                privacyDetailActivity.a(o.f6454a);
            }
            return null;
        }

        /* access modifiers changed from: protected */
        /* renamed from: a */
        public void onPostExecute(List<com.miui.permcenter.privacymanager.a.a> list) {
            super.onPostExecute(list);
            PrivacyDetailActivity privacyDetailActivity = (PrivacyDetailActivity) this.f6410a.get();
            if (!isCancelled() && !privacyDetailActivity.isFinishing() && !privacyDetailActivity.isDestroyed()) {
                Log.i("BehaviorRecord-SINGLE", "Loading more over, refresh and removeFooterView ...");
                privacyDetailActivity.y.a(false);
                privacyDetailActivity.z();
            }
        }
    }

    private static class g extends Handler {

        /* renamed from: a  reason: collision with root package name */
        private Context f6411a;

        /* renamed from: b  reason: collision with root package name */
        private final WeakReference<PrivacyDetailActivity> f6412b;

        public g(PrivacyDetailActivity privacyDetailActivity) {
            this.f6411a = privacyDetailActivity.getApplicationContext();
            this.f6412b = new WeakReference<>(privacyDetailActivity);
        }

        public void handleMessage(Message message) {
            super.handleMessage(message);
            PrivacyDetailActivity privacyDetailActivity = (PrivacyDetailActivity) this.f6412b.get();
            if (privacyDetailActivity != null && !privacyDetailActivity.isFinishing()) {
                int i = message.what;
                boolean z = true;
                if (i == 1) {
                    ApplicationInfo applicationInfo = null;
                    try {
                        applicationInfo = AppManageUtils.a(privacyDetailActivity.Q, privacyDetailActivity.F, 128, privacyDetailActivity.H);
                    } catch (Exception e) {
                        Log.e("BehaviorRecord-SINGLE", "handle message get application info error", e);
                    }
                    if (applicationInfo != null) {
                        ApplicationInfo unused = privacyDetailActivity.J = applicationInfo;
                    }
                    if (applicationInfo == null || !applicationInfo.enabled) {
                        z = false;
                    }
                    int unused2 = privacyDetailActivity.i = z ? R.string.app_manager_disable_text : R.string.app_manager_enable_text;
                    privacyDetailActivity.h.setTitle(privacyDetailActivity.i);
                    com.miui.securityscan.i.c.a(this.f6411a, z ? R.string.app_manager_enabled : R.string.app_manager_disabled);
                } else if (i == 2457) {
                    Bundle data = message.getData();
                    privacyDetailActivity.a(data.getLong(PermissionContract.Method.GetUsingPermissionList.EXTRA_PERMISSIONID), data.getStringArray("extra_data"), data.getInt(PermissionContract.Method.GetUsingPermissionList.EXTRA_TYPE));
                }
            }
        }
    }

    private static class h extends IPackageDeleteObserver.Stub {
        /* access modifiers changed from: private */

        /* renamed from: a  reason: collision with root package name */
        public Context f6413a;

        /* renamed from: b  reason: collision with root package name */
        private WeakReference<PrivacyDetailActivity> f6414b;

        public h(PrivacyDetailActivity privacyDetailActivity) {
            this.f6413a = privacyDetailActivity.getApplicationContext();
            this.f6414b = new WeakReference<>(privacyDetailActivity);
        }

        public void packageDeleted(String str, int i) {
            PrivacyDetailActivity privacyDetailActivity;
            if (i == 1 && (privacyDetailActivity = (PrivacyDetailActivity) this.f6414b.get()) != null) {
                privacyDetailActivity.V.post(new y(this, privacyDetailActivity, str));
            }
        }
    }

    public interface i {
        void a(Long l, int i);
    }

    private static class j extends b.b.c.i.a<List<com.miui.permcenter.privacymanager.a.a>> {

        /* renamed from: b  reason: collision with root package name */
        private WeakReference<PrivacyDetailActivity> f6415b;

        public j(PrivacyDetailActivity privacyDetailActivity) {
            super(privacyDetailActivity.getApplicationContext());
            this.f6415b = new WeakReference<>(privacyDetailActivity);
        }

        public List<com.miui.permcenter.privacymanager.a.a> loadInBackground() {
            PrivacyDetailActivity privacyDetailActivity = (PrivacyDetailActivity) this.f6415b.get();
            if (privacyDetailActivity != null && !privacyDetailActivity.isFinishing() && !privacyDetailActivity.isDestroyed()) {
                AtomicInteger unused = privacyDetailActivity.C = new AtomicInteger(0);
                List unused2 = privacyDetailActivity.A = new CopyOnWriteArrayList();
                privacyDetailActivity.a(o.f6454a);
            }
            return null;
        }
    }

    private static class k extends AsyncTask<Void, Void, Void> {

        /* renamed from: a  reason: collision with root package name */
        private WeakReference<PrivacyDetailActivity> f6416a;

        k(PrivacyDetailActivity privacyDetailActivity) {
            this.f6416a = new WeakReference<>(privacyDetailActivity);
        }

        /* access modifiers changed from: protected */
        /* renamed from: a */
        public Void doInBackground(Void... voidArr) {
            Activity activity = (PrivacyDetailActivity) this.f6416a.get();
            if (!isCancelled() && activity != null && !activity.isFinishing() && !activity.isDestroyed()) {
                activity.Z.clear();
                activity.aa.clear();
                activity.Z.putAll(o.a((Context) activity, activity.F));
                HashMap<Long, Integer> a2 = n.a((Context) activity, activity.F);
                if (a2 != null) {
                    activity.aa.putAll(a2);
                }
                activity.aa.put(Long.valueOf(PermissionManager.PERM_ID_AUTOSTART), Integer.valueOf(AppManageUtils.a((Context) activity, activity.F) ? 3 : 1));
                if (!o.b((Context) activity) && activity.aa.containsKey(32L) && activity.aa.containsKey(Long.valueOf(PermissionManager.PERM_ID_BACKGROUND_LOCATION))) {
                    activity.aa.put(32L, Integer.valueOf(n.a(((Integer) activity.aa.get(32L)).intValue(), ((Integer) activity.aa.get(Long.valueOf(PermissionManager.PERM_ID_BACKGROUND_LOCATION))).intValue())));
                }
            }
            return null;
        }
    }

    private static class l extends AsyncTask<Void, Void, Void> {

        /* renamed from: a  reason: collision with root package name */
        private WeakReference<PrivacyDetailActivity> f6417a;

        public l(PrivacyDetailActivity privacyDetailActivity) {
            this.f6417a = new WeakReference<>(privacyDetailActivity);
        }

        /* access modifiers changed from: protected */
        /* renamed from: a */
        public Void doInBackground(Void... voidArr) {
            Activity activity = (PrivacyDetailActivity) this.f6417a.get();
            if (!isCancelled() && !activity.isDestroyed() && !activity.isFinishing()) {
                long unused = activity.q = o.a((Context) activity, activity.F, activity.H, 32, PermissionManager.PERM_ID_AUDIO_RECORDER);
            }
            return null;
        }

        /* access modifiers changed from: protected */
        /* renamed from: a */
        public void onPostExecute(Void voidR) {
            PrivacyDetailActivity privacyDetailActivity = (PrivacyDetailActivity) this.f6417a.get();
            if (!isCancelled() && !privacyDetailActivity.isDestroyed() && !privacyDetailActivity.isFinishing()) {
                privacyDetailActivity.y();
            }
        }
    }

    private static class m implements DialogInterface.OnClickListener {

        /* renamed from: a  reason: collision with root package name */
        private WeakReference<PrivacyDetailActivity> f6418a;

        /* renamed from: b  reason: collision with root package name */
        private int f6419b;

        public m(PrivacyDetailActivity privacyDetailActivity, int i) {
            this.f6418a = new WeakReference<>(privacyDetailActivity);
            this.f6419b = i;
        }

        public void onClick(DialogInterface dialogInterface, int i) {
            PrivacyDetailActivity privacyDetailActivity = (PrivacyDetailActivity) this.f6418a.get();
            if (privacyDetailActivity != null && !privacyDetailActivity.isFinishing() && !privacyDetailActivity.isDestroyed()) {
                privacyDetailActivity.a(privacyDetailActivity.F, privacyDetailActivity.H);
            }
        }
    }

    public static Intent a(String str, int i2, String str2) {
        Intent intent = new Intent("miui.intent.action.APP_PRIVACY_DETAIL");
        intent.putExtra("privacy_pkg_info", str);
        intent.putExtra("privacy_userid", i2);
        intent.putExtra("analytic", str2);
        return intent;
    }

    /* JADX WARNING: type inference failed for: r7v0, types: [android.content.Context, miui.app.Activity, com.miui.permcenter.privacymanager.behaviorrecord.PrivacyDetailActivity] */
    /* access modifiers changed from: private */
    public void a(int i2) {
        int size = this.A.size() + i2;
        while (this.A.size() < size) {
            Log.i("BehaviorRecord-SINGLE", "bulkLoad limit " + i2 + " , offset " + this.C.get());
            this.D = o.a((Context) this, this.F, this.H, this.A, i2, this.C.get());
            if (!this.D) {
                runOnUiThread(new x(this));
                return;
            }
            this.C.addAndGet(i2);
        }
    }

    /* access modifiers changed from: private */
    public void a(long j2, String[] strArr, int i2) {
        long j3;
        if (strArr != null) {
            for (String split : strArr) {
                String[] split2 = split.split("@");
                if (split2 == null || split2.length < 2) {
                    Log.i("BehaviorRecord-SINGLE", "Parsing failed for don't Recognize: ");
                } else {
                    int parseInt = Integer.parseInt(split2[0]);
                    String str = split2[1];
                    if (TextUtils.equals(str, this.F) && this.H == parseInt && i2 != 1 && (j2 == PermissionManager.PERM_ID_AUDIO_RECORDER || j2 == 32)) {
                        Log.i("BehaviorRecord-SINGLE", str + " is using " + j2 + " , its operationType: " + i2);
                        if (i2 == 2) {
                            j3 = this.q | j2;
                        } else {
                            if (i2 == 3) {
                                j3 = this.q ^ j2;
                            }
                            y();
                        }
                        this.q = j3;
                        y();
                    }
                }
            }
        }
    }

    /* JADX WARNING: type inference failed for: r3v0, types: [android.content.Context, com.miui.permcenter.privacymanager.behaviorrecord.PrivacyDetailActivity] */
    private void a(View view) {
        if (view != null && this.f6403d) {
            this.f6402c = new GuidePopupWindow(this);
            this.f6402c.setArrowMode(1);
            this.f6402c.setGuideText(R.string.app_behavior_monitor_tips_if_trust);
            this.f6402c.show(view, 0, 0, true);
            this.f6403d = false;
            com.miui.permcenter.privacymanager.a.c cVar = this.e;
            if (cVar != null) {
                cVar.b(6);
                com.miui.common.persistence.b.b(this.e.a(), this.e.b());
            }
        }
    }

    /* JADX WARNING: type inference failed for: r1v0, types: [android.content.Context, com.miui.permcenter.privacymanager.behaviorrecord.PrivacyDetailActivity] */
    private void a(CharSequence charSequence, CharSequence charSequence2) {
        new AlertDialog.Builder(this).setTitle(charSequence).setMessage(charSequence2).setPositiveButton(R.string.app_manager_disable_text, new a(this)).setNegativeButton(17039360, (DialogInterface.OnClickListener) null).show();
    }

    /* access modifiers changed from: private */
    public void a(String str, int i2) {
        if (this.N) {
            AppManageUtils.a(this.Q, str, this.K.versionCode, (IPackageDeleteObserver) this.S, i2, 0);
            return;
        }
        AppManageUtils.a(this.Q, str, this.K.versionCode, (IPackageDeleteObserver) this.S, i2, 0);
        if (b.b.o.b.a.a.a(this.Q, str)) {
            AppManageUtils.a(this.Q, str, this.K.versionCode, (IPackageDeleteObserver) null, 999, 0);
        }
    }

    /* access modifiers changed from: private */
    public void b(int i2) {
        this.R = new d(this, i2);
        this.R.execute(new Void[0]);
    }

    /* JADX WARNING: type inference failed for: r1v0, types: [android.content.Context, com.miui.permcenter.privacymanager.behaviorrecord.PrivacyDetailActivity] */
    private void b(CharSequence charSequence, CharSequence charSequence2) {
        new AlertDialog.Builder(this).setTitle(charSequence).setMessage(charSequence2).setPositiveButton(R.string.app_manager_unstall_application, new b(this)).setNegativeButton(17039360, (DialogInterface.OnClickListener) null).show();
    }

    /* JADX WARNING: type inference failed for: r4v0, types: [android.content.Context, com.miui.permcenter.privacymanager.behaviorrecord.PrivacyDetailActivity] */
    /* access modifiers changed from: private */
    public void c(int i2) {
        int i3 = R.string.uninstall_app_dialog_title;
        int i4 = R.string.uninstall_app_dialog_msg;
        if (i2 == 0) {
            i3 = R.string.app_manager_factory_reset_dlg_title;
            i4 = R.string.app_manager_factory_reset_dlg_msg;
        } else if (i2 == 1) {
            if (this.N) {
                i3 = R.string.app_manager_uninstall_xspace_app_dlg_title;
                i4 = R.string.app_manager_uninstall_xspace_app_dlg_msg;
            } else if (b.b.o.b.a.a.a(this.Q, this.F)) {
                i4 = R.string.app_manager_uninstall_with_xspace_app_dlg_msg;
            }
            if (!u()) {
                i3 = R.string.app_manager_uninstall_protected_dlg_title;
                i4 = R.string.app_manager_uninstall_protected_dlg_msg;
            }
        }
        new AlertDialog.Builder(this).setTitle(i3).setMessage(i4).setPositiveButton(17039370, new m(this, i2)).setNegativeButton(17039360, (DialogInterface.OnClickListener) null).show();
    }

    /* JADX WARNING: type inference failed for: r8v0, types: [android.content.Context, miui.app.Activity, com.miui.permcenter.privacymanager.behaviorrecord.PrivacyDetailActivity] */
    private void initData() {
        String str;
        String str2;
        this.F = this.ba.getStringExtra("privacy_pkg_info");
        this.G = this.ba.getLongExtra("privacy_using", 0);
        this.H = this.ba.getIntExtra("privacy_userid", UserHandle.myUserId());
        this.f6403d = this.ba.getBooleanExtra("privacy_guide", false);
        com.miui.permcenter.privacymanager.a.a("EnterSingleFrom", this.ba.getStringExtra("analytic"));
        ArrayList<String> a2 = com.miui.common.persistence.b.a("PrivacyList", (ArrayList<String>) new ArrayList());
        if (a2.remove(this.F + "@" + this.H)) {
            com.miui.common.persistence.b.b("PrivacyList", a2);
        }
        try {
            this.Q = b.b.o.g.e.a(Class.forName("android.content.pm.IPackageManager$Stub"), "asInterface", (Class<?>[]) new Class[]{IBinder.class}, (IBinder) b.b.o.g.e.a(Class.forName("android.os.ServiceManager"), "getService", (Class<?>[]) new Class[]{String.class}, "package"));
        } catch (Exception e2) {
            Log.e("BehaviorRecord-SINGLE", "reflect error while get package manager service", e2);
        }
        String str3 = this.F;
        if (str3 == null) {
            finish();
            return;
        }
        this.J = AppManageUtils.a(this.Q, str3, 128, this.H);
        if (this.J == null) {
            finish();
            return;
        }
        this.K = b.b.o.b.a.a.a(this.F, 128, this.H);
        if (this.K == null) {
            finish();
            return;
        }
        this.V = new g(this);
        v();
        this.M = getPackageManager();
        this.L = (DevicePolicyManager) getSystemService("device_policy");
        this.N = C.b(this.H);
        this.S = new h(this);
        String[] stringArray = getResources().getStringArray(R.array.always_enabled_app_list);
        this.U = new HashSet<>(stringArray.length);
        for (String add : stringArray) {
            this.U.add(add);
        }
        this.O = (this.J.flags & 1) != 0;
        this.P = (this.J.flags & 128) != 0 && !AppManageUtils.f.contains(this.F);
        this.I = x.j(this, this.F).toString();
        String str4 = this.I;
        if (str4 != null) {
            this.m.setText(str4);
        }
        if (this.f6403d) {
            this.e = new com.miui.permcenter.privacymanager.a.c(this.F, this.H);
        }
        if (this.H == 999) {
            str2 = this.F;
            str = "pkg_icon_xspace://";
        } else {
            str2 = this.F;
            str = "pkg_icon://";
        }
        r.a(str.concat(str2), this.l, r.f);
        this.y = new a(this, 1);
        this.y.a(this.ha);
        this.y.a(this.ia);
        this.x.a(this.ea);
        this.x.setAdapter(this.y);
        this.t = new z(this, this.I, this.G);
        this.t.a(this.ga);
        this.r.setAdapter(this.t);
        this.r.a((RecyclerView.f) this.s);
        this.W = getLoaderManager();
        LoaderManager loaderManager = this.W;
        if (loaderManager != null) {
            if (this.ca) {
                loaderManager.restartLoader(666, (Bundle) null, this.fa);
            } else {
                loaderManager.initLoader(666, (Bundle) null, this.fa);
            }
        }
        this.Y = new k(this);
        this.Y.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, new Void[0]);
    }

    /* access modifiers changed from: private */
    public void l() {
        this.k = false;
        this.g.setEnabled(false);
        this.n.setVisibility(8);
        y();
        LoaderManager loaderManager = this.W;
        if (loaderManager != null) {
            loaderManager.restartLoader(666, (Bundle) null, this.fa);
        }
    }

    /* JADX WARNING: type inference failed for: r4v0, types: [b.b.c.c.a, android.content.Context, miui.app.Activity, com.miui.permcenter.privacymanager.behaviorrecord.PrivacyDetailActivity] */
    private void m() {
        int i2;
        ImageView imageView;
        this.v = findViewById(R.id.behavior_loading);
        this.f6400a = (ImageView) findViewById(R.id.pm_activity_back);
        this.f6400a.setOnClickListener(this.da);
        this.f6401b = (ImageView) findViewById(R.id.pm_activity_more);
        if (isDarkModeEnable()) {
            imageView = this.f6401b;
            i2 = miui.R.drawable.icon_settings_dark;
        } else {
            imageView = this.f6401b;
            i2 = miui.R.drawable.icon_settings_light;
        }
        imageView.setImageResource(i2);
        this.f6401b.setOnClickListener(this.da);
        this.l = (ImageView) findViewById(R.id.app_info_icon);
        this.m = (TextView) findViewById(R.id.app_info_pkgname);
        this.n = (TextView) findViewById(R.id.app_info_isrunning);
        this.o = new LinearLayoutManager(this);
        this.w = findViewById(R.id.behavior_empty_view);
        this.x = (miuix.recyclerview.widget.RecyclerView) findViewById(R.id.privacy_list_data);
        this.x.setLayoutManager(this.o);
        this.r = (miuix.recyclerview.widget.RecyclerView) findViewById(R.id.privacy_list_using);
        this.r.setLayoutManager(new LinearLayoutManager(this));
        this.s = new com.miui.permcenter.widget.c(getResources().getDimensionPixelSize(R.dimen.view_dimen_50), getResources().getDimensionPixelSize(R.dimen.view_dimen_30));
    }

    /* JADX WARNING: type inference failed for: r5v0, types: [android.content.Context, com.miui.permcenter.privacymanager.behaviorrecord.PrivacyDetailActivity] */
    private void n() {
        if (this.g != null && this.h != null) {
            this.k = x.b(this).contains(this.F);
            this.g.setEnabled(this.k);
            this.n.setVisibility(this.k ? 0 : 8);
            q();
            if (b.b.c.j.e.c(this, this.F, this.H)) {
                Log.d("Enterprise", "Package " + this.F + " should keep alive");
                this.g.setEnabled(false);
            }
            if (b.b.c.j.e.b(this, this.F, this.H)) {
                Log.d("Enterprise", "Package " + this.F + " is protected from delete");
                this.h.setEnabled(false);
            }
        }
    }

    /* JADX WARNING: type inference failed for: r1v0, types: [android.content.Context, com.miui.permcenter.privacymanager.behaviorrecord.PrivacyDetailActivity] */
    /* access modifiers changed from: private */
    public void o() {
        x.b((Context) this, this.F);
        l();
    }

    private int p() {
        int i2 = this.T;
        return i2 > 8 ? R.drawable.action_button_stop_svg : i2 > 7 ? R.drawable.action_button_stop : R.drawable.action_button_stop_9;
    }

    private void q() {
        int i2;
        boolean z2 = false;
        if ("com.jeejen.family.miui".equals(this.F) || ((Build.IS_GLOBAL_BUILD && "com.amazon.appmanager".equals(this.F)) || AppManageUtils.a(this.L, this.F) || "com.xiaomi.mipicks".equals(this.F) || (Build.IS_INTERNATIONAL_BUILD && ("com.facemoji.lite.xiaomi".equals(this.F) || "com.kikaoem.xiaomi.qisiemoji.inputmethod".equals(this.F))))) {
            this.h.setVisible(false);
            return;
        }
        ApplicationInfo applicationInfo = this.J;
        if (applicationInfo != null) {
            if (this.P) {
                this.j = p();
                i2 = R.string.app_manager_factory_reset;
            } else {
                if (this.O) {
                    this.j = p();
                    boolean contains = AppManageUtils.f3485c.contains(this.F);
                    int i3 = R.string.app_manager_disable_text;
                    if (contains) {
                        if (!this.J.enabled) {
                            i3 = R.string.app_manager_enable_text;
                        }
                        this.i = i3;
                    } else {
                        try {
                            Intent intent = new Intent("android.intent.action.MAIN");
                            intent.addCategory("android.intent.category.HOME");
                            intent.setPackage(this.F);
                            List<ResolveInfo> queryIntentActivities = this.M.queryIntentActivities(intent, 0);
                            if ((queryIntentActivities == null || queryIntentActivities.size() <= 0) && !r()) {
                                if (!this.J.enabled) {
                                    this.i = R.string.app_manager_enable_text;
                                }
                            }
                            this.i = R.string.app_manager_disable_text;
                        } catch (Exception unused) {
                        }
                        this.h.setTitle(this.i);
                        this.h.setIcon(this.j);
                        this.h.setEnabled(z2);
                    }
                } else if (applicationInfo.enabled) {
                    this.j = R.drawable.app_manager_delete_icon;
                    i2 = R.string.app_manager_unstall_application;
                } else {
                    this.j = p();
                    this.i = R.string.app_manager_enable_text;
                }
                z2 = true;
                this.h.setTitle(this.i);
                this.h.setIcon(this.j);
                this.h.setEnabled(z2);
            }
            this.i = i2;
            z2 = true;
            this.h.setTitle(this.i);
            this.h.setIcon(this.j);
            this.h.setEnabled(z2);
        }
    }

    private boolean r() {
        try {
            PackageInfo packageInfo = this.M.getPackageInfo(Constants.System.ANDROID_PACKAGE_NAME, 64);
            if (this.K != null) {
                return (this.K.signatures != null && packageInfo.signatures[0].equals(this.K.signatures[0])) || this.U.contains(this.F);
            }
            return false;
        } catch (PackageManager.NameNotFoundException unused) {
            return false;
        }
    }

    /* JADX WARNING: type inference failed for: r3v0, types: [android.content.Context, miui.app.Activity, com.miui.permcenter.privacymanager.behaviorrecord.PrivacyDetailActivity] */
    private void s() {
        new AlertDialog.Builder(this).setTitle(getText(R.string.app_manager_force_stop_dlg_title)).setIconAttribute(16843605).setMessage(getText(R.string.app_manager_force_stop_dlg_text)).setPositiveButton(R.string.app_manager_dlg_ok, new e(this)).setNegativeButton(R.string.app_manager_dlg_cancel, (DialogInterface.OnClickListener) null).show();
    }

    private void t() {
        Intent intent = new Intent("miui.intent.action.APP_PERM_EDITOR_PRIVATE");
        intent.putExtra("extra_pkgname", this.F);
        startActivity(intent);
    }

    /* JADX WARNING: type inference failed for: r2v0, types: [android.content.Context, com.miui.permcenter.privacymanager.behaviorrecord.PrivacyDetailActivity] */
    private boolean u() {
        return this.O || !b.b.c.j.e.a((Context) this, this.F, 0);
    }

    private void v() {
        if (this.p == null) {
            this.p = new com.miui.permcenter.privacymanager.g(this.V);
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(PermissionContract.ACTION_USING_PERMISSION_CHANGE);
            intentFilter.addAction(PermissionContract.ACTION_USING_STATUS_BAR_PERMISSION);
            registerReceiver(this.p, intentFilter, "miui.permission.READ_AND_WIRTE_PERMISSION_MANAGER", (Handler) null);
        }
    }

    /* JADX WARNING: type inference failed for: r3v0, types: [android.content.Context, com.miui.permcenter.privacymanager.behaviorrecord.PrivacyDetailActivity] */
    /* access modifiers changed from: private */
    public void w() {
        new AlertDialog.Builder(this).setTitle(R.string.app_manager_disable_dlg_title).setMessage(R.string.app_manager_disable_dlg_text).setPositiveButton(17039370, new c(this)).setNegativeButton(17039360, (DialogInterface.OnClickListener) null).show();
    }

    /* JADX WARNING: Removed duplicated region for block: B:16:0x0049  */
    /* JADX WARNING: Removed duplicated region for block: B:32:0x0086  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void x() {
        /*
            r6 = this;
            boolean r0 = r6.P
            r1 = 0
            if (r0 == 0) goto L_0x000a
            r6.c((int) r1)
            goto L_0x0089
        L_0x000a:
            android.content.pm.ApplicationInfo r0 = r6.J
            r2 = 0
            if (r0 == 0) goto L_0x0040
            android.os.Bundle r0 = r0.metaData
            if (r0 == 0) goto L_0x0040
            android.content.pm.ApplicationInfo r0 = r6.J
            android.os.Bundle r0 = r0.metaData
            java.lang.String r3 = "app_description_title"
            int r0 = r0.getInt(r3)
            android.content.pm.ApplicationInfo r3 = r6.J
            android.os.Bundle r3 = r3.metaData
            java.lang.String r4 = "app_description_content"
            int r3 = r3.getInt(r4)
            if (r0 == 0) goto L_0x0040
            if (r3 == 0) goto L_0x0040
            android.content.pm.PackageManager r2 = r6.M
            java.lang.String r4 = r6.F
            android.content.pm.ApplicationInfo r5 = r6.J
            java.lang.CharSequence r2 = r2.getText(r4, r0, r5)
            android.content.pm.PackageManager r0 = r6.M
            java.lang.String r4 = r6.F
            android.content.pm.ApplicationInfo r5 = r6.J
            java.lang.CharSequence r0 = r0.getText(r4, r3, r5)
            goto L_0x0041
        L_0x0040:
            r0 = r2
        L_0x0041:
            android.content.pm.ApplicationInfo r3 = r6.J
            if (r3 == 0) goto L_0x0086
            boolean r3 = r3.enabled
            if (r3 == 0) goto L_0x0086
            boolean r1 = r6.O
            if (r1 == 0) goto L_0x0062
            boolean r1 = android.text.TextUtils.isEmpty(r2)
            if (r1 != 0) goto L_0x005e
            boolean r1 = android.text.TextUtils.isEmpty(r0)
            if (r1 == 0) goto L_0x005a
            goto L_0x005e
        L_0x005a:
            r6.a((java.lang.CharSequence) r2, (java.lang.CharSequence) r0)
            goto L_0x0089
        L_0x005e:
            r6.w()
            goto L_0x0089
        L_0x0062:
            boolean r1 = r6.u()
            r3 = 1
            if (r1 != 0) goto L_0x0082
            boolean r1 = android.text.TextUtils.isEmpty(r2)
            if (r1 != 0) goto L_0x0082
            boolean r1 = android.text.TextUtils.isEmpty(r0)
            if (r1 == 0) goto L_0x0076
            goto L_0x0082
        L_0x0076:
            java.lang.String r1 = r2.toString()
            java.lang.String r0 = r0.toString()
            r6.b((java.lang.CharSequence) r1, (java.lang.CharSequence) r0)
            goto L_0x0089
        L_0x0082:
            r6.c((int) r3)
            goto L_0x0089
        L_0x0086:
            r6.b((int) r1)
        L_0x0089:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.permcenter.privacymanager.behaviorrecord.PrivacyDetailActivity.x():void");
    }

    /* access modifiers changed from: private */
    public void y() {
        long j2;
        z zVar;
        if (this.n.getVisibility() == 0) {
            zVar = this.t;
            j2 = this.q;
        } else {
            zVar = this.t;
            j2 = 0;
        }
        zVar.a(j2);
    }

    /* JADX WARNING: type inference failed for: r5v0, types: [android.content.Context, miui.app.Activity, com.miui.permcenter.privacymanager.behaviorrecord.PrivacyDetailActivity] */
    /* access modifiers changed from: private */
    public void z() {
        boolean z2 = this.A.size() == 0;
        int i2 = 8;
        this.w.setVisibility(z2 ? 0 : 8);
        miuix.recyclerview.widget.RecyclerView recyclerView = this.x;
        if (!z2) {
            i2 = 0;
        }
        recyclerView.setVisibility(i2);
        if (!z2) {
            this.B = o.a((Context) this, this.A, this.k);
            this.y.a(this.A);
            this.x.b((RecyclerView.f) this.z);
            b.a a2 = b.a.a((com.miui.permcenter.b.a) new s(this));
            a2.b(getResources().getDimensionPixelSize(R.dimen.view_dimen_100));
            a2.a(0);
            this.z = a2.a();
            this.x.a((RecyclerView.f) this.z);
        }
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_privacy_layout);
        this.ba = getIntent();
        m();
        this.ca = bundle != null;
        initData();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        if (this.J != null) {
            this.f = menu.add(0, 1, 0, R.string.app_manager_permission_manager_title);
            this.f.setIcon(R.drawable.action_button_perm_bg);
            this.f.setShowAsAction(1);
            this.g = menu.add(0, 2, 0, R.string.app_behavior_kill_process);
            this.g.setIcon(R.drawable.app_manager_finish_icon);
            this.g.setEnabled(this.k);
            this.g.setShowAsAction(1);
            this.h = menu.add(0, 3, 0, R.string.app_manager_unstall_application);
            this.h.setIcon(R.drawable.app_manager_delete_icon);
            this.h.setEnabled(true);
            this.h.setShowAsAction(1);
            q();
        }
        return PrivacyDetailActivity.super.onCreateOptionsMenu(menu);
    }

    /* access modifiers changed from: protected */
    public void onDestroy() {
        PrivacyDetailActivity.super.onDestroy();
        k kVar = this.Y;
        if (kVar != null) {
            kVar.cancel(true);
        }
        d dVar = this.R;
        if (dVar != null) {
            dVar.cancel(true);
        }
        l lVar = this.u;
        if (lVar != null) {
            lVar.cancel(true);
        }
        com.miui.permcenter.privacymanager.g gVar = this.p;
        if (gVar != null) {
            unregisterReceiver(gVar);
        }
        LoaderManager loaderManager = this.W;
        if (loaderManager != null) {
            loaderManager.destroyLoader(666);
        }
    }

    /* access modifiers changed from: protected */
    public void onNewIntent(Intent intent) {
        PrivacyDetailActivity.super.onNewIntent(intent);
        this.ba = intent;
        this.ca = true;
        initData();
    }

    public boolean onOptionsItemSelected(MenuItem menuItem) {
        int itemId = menuItem.getItemId();
        if (itemId == 1) {
            t();
        } else if (itemId == 2) {
            s();
            return true;
        } else if (itemId == 3) {
            x();
            return true;
        }
        return PrivacyDetailActivity.super.onOptionsItemSelected(menuItem);
    }

    public boolean onPrepareOptionsMenu(Menu menu) {
        n();
        return PrivacyDetailActivity.super.onPrepareOptionsMenu(menu);
    }

    /* access modifiers changed from: protected */
    public void onResume() {
        com.miui.permcenter.privacymanager.a.c cVar;
        super.onResume();
        n();
        this.u = new l(this);
        this.u.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);
        if (this.f6403d && (cVar = this.e) != null) {
            cVar.d(com.miui.common.persistence.b.a(cVar.a(), 0));
            this.f6403d &= this.e.a(6);
        }
    }

    public void onWindowFocusChanged(boolean z2) {
        PrivacyDetailActivity.super.onWindowFocusChanged(z2);
        if (z2) {
            a((View) this.f6401b);
        }
    }
}
