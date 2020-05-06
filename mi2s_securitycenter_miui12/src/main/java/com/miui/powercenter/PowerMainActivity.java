package com.miui.powercenter;

import android.animation.ValueAnimator;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import b.b.c.j.A;
import b.b.c.j.C0195b;
import b.b.c.j.d;
import b.b.c.j.e;
import b.b.c.j.x;
import com.miui.common.customview.ScoreTextView;
import com.miui.powercenter.autotask.AutoTaskManageActivity;
import com.miui.powercenter.batteryhistory.C0501e;
import com.miui.powercenter.batteryhistory.C0514s;
import com.miui.powercenter.batteryhistory.C0520y;
import com.miui.powercenter.batteryhistory.aa;
import com.miui.powercenter.bootshutdown.PowerShutdownOnTime;
import com.miui.powercenter.mainui.MainBatteryView;
import com.miui.powercenter.utils.g;
import com.miui.powercenter.utils.o;
import com.miui.powercenter.utils.s;
import com.miui.powercenter.utils.t;
import com.miui.powercenter.utils.u;
import com.miui.securitycenter.R;
import com.miui.superpower.b.i;
import com.miui.superpower.b.k;
import com.xiaomi.stat.MiStat;
import java.lang.ref.WeakReference;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Locale;
import miui.app.ActionBar;
import miui.app.Activity;
import miui.view.animation.CubicEaseOutInterpolator;
import miui.widget.SlidingButton;

public class PowerMainActivity extends b.b.c.c.a implements View.OnClickListener {
    /* access modifiers changed from: private */

    /* renamed from: a  reason: collision with root package name */
    public MainBatteryView f6640a;

    /* renamed from: b  reason: collision with root package name */
    private RelativeLayout f6641b;

    /* renamed from: c  reason: collision with root package name */
    private b f6642c;

    /* renamed from: d  reason: collision with root package name */
    private Button f6643d;
    /* access modifiers changed from: private */
    public SlidingButton e;
    /* access modifiers changed from: private */
    public SlidingButton f;
    /* access modifiers changed from: private */
    public TextView g;
    /* access modifiers changed from: private */
    public TextView h;
    private TextView i;
    /* access modifiers changed from: private */
    public TextView j;
    /* access modifiers changed from: private */
    public ScoreTextView k;
    /* access modifiers changed from: private */
    public ImageView l;
    private RelativeLayout m;
    private RelativeLayout n;
    private RelativeLayout o;
    private RelativeLayout p;
    /* access modifiers changed from: private */
    public boolean q;
    /* access modifiers changed from: private */
    public boolean r = false;
    /* access modifiers changed from: private */
    public Context s;
    private boolean t;
    private long u = 0;
    private CompoundButton.OnCheckedChangeListener v = new j(this);

    private static class a extends AsyncTask<Void, Void, List<aa>> {

        /* renamed from: a  reason: collision with root package name */
        private final WeakReference<PowerMainActivity> f6644a;

        /* renamed from: b  reason: collision with root package name */
        private boolean f6645b;

        /* renamed from: c  reason: collision with root package name */
        private int f6646c;

        /* renamed from: d  reason: collision with root package name */
        private long f6647d;
        private long e;
        /* access modifiers changed from: private */
        public String f;

        private a(PowerMainActivity powerMainActivity, boolean z, int i) {
            this.f6644a = new WeakReference<>(powerMainActivity);
            this.f6645b = z;
            this.f6646c = i;
        }

        /* synthetic */ a(PowerMainActivity powerMainActivity, boolean z, int i, c cVar) {
            this(powerMainActivity, z, i);
        }

        /* JADX WARNING: type inference failed for: r8v3, types: [android.content.Context, com.miui.powercenter.PowerMainActivity] */
        /* access modifiers changed from: protected */
        /* renamed from: a */
        public List<aa> doInBackground(Void... voidArr) {
            long j;
            ? r8 = (PowerMainActivity) this.f6644a.get();
            if (r8 == 0) {
                return null;
            }
            List<aa> b2 = C0514s.c().b();
            if (this.f6645b) {
                int i = this.f6646c;
                if (i < 100) {
                    this.f6647d = (this.e * 100) / ((long) (100 - i));
                    j = C0501e.a((Context) r8, b2).f6879a;
                }
                return b2;
            }
            this.f6647d = 0;
            j = C0520y.a(r8, b2);
            this.e = j;
            return b2;
        }

        /* access modifiers changed from: protected */
        /* renamed from: a */
        public void onPostExecute(List<aa> list) {
            String str;
            ValueAnimator.AnimatorUpdateListener animatorUpdateListener;
            ValueAnimator valueAnimator;
            Activity activity = (PowerMainActivity) this.f6644a.get();
            if (activity != null) {
                if (this.f6645b) {
                    if (this.f6646c >= 100) {
                        str = activity.getString(R.string.menu_summary_power_manager_1);
                    } else if (!activity.r) {
                        this.f = s.a((Context) activity, this.f6647d);
                        activity.j.setText(this.f);
                        valueAnimator = ValueAnimator.ofFloat(new float[]{(float) this.f6647d, (float) this.e}).setDuration(1000);
                        valueAnimator.setInterpolator(new DecelerateInterpolator());
                        valueAnimator.setRepeatCount(0);
                        animatorUpdateListener = new k(this, activity);
                    } else {
                        str = s.a((Context) activity, this.e);
                    }
                    this.f = str;
                    activity.j.setText(this.f);
                    boolean unused = activity.r = true;
                } else if (!activity.r) {
                    this.f = s.b((Context) activity, this.f6647d);
                    activity.j.setText(this.f);
                    valueAnimator = ValueAnimator.ofFloat(new float[]{0.0f, (float) this.e}).setDuration(1000);
                    valueAnimator.setInterpolator(new DecelerateInterpolator());
                    valueAnimator.setRepeatCount(0);
                    animatorUpdateListener = new l(this, activity);
                } else {
                    str = s.b((Context) activity, this.e);
                    this.f = str;
                    activity.j.setText(this.f);
                    boolean unused2 = activity.r = true;
                }
                valueAnimator.addUpdateListener(animatorUpdateListener);
                valueAnimator.start();
                boolean unused3 = activity.r = true;
            }
        }
    }

    private class b extends BroadcastReceiver {

        /* renamed from: a  reason: collision with root package name */
        private int f6648a;

        /* renamed from: b  reason: collision with root package name */
        private boolean f6649b;

        private b() {
        }

        /* synthetic */ b(PowerMainActivity powerMainActivity, c cVar) {
            this();
        }

        public void a(boolean z) {
            this.f6649b = z;
        }

        public void onReceive(Context context, Intent intent) {
            a aVar;
            if ("android.intent.action.BATTERY_CHANGED".equals(intent.getAction())) {
                int intExtra = intent.getIntExtra(MiStat.Param.LEVEL, 0);
                int intExtra2 = intent.getIntExtra("scale", 0);
                if (intExtra2 != 0) {
                    int i = (intExtra * 100) / intExtra2;
                    boolean a2 = o.a(intent);
                    int i2 = this.f6648a;
                    if (i != i2 || a2 != this.f6649b || (i == 0 && i2 == 0)) {
                        int i3 = this.f6648a;
                        if (i != i3 || (i == 0 && i3 == 0)) {
                            PowerMainActivity.this.f6640a.setCurrentValue(i);
                        }
                        PowerMainActivity.this.f6640a.setChargingStatus(a2);
                        this.f6648a = i;
                        this.f6649b = a2;
                        if (!PowerMainActivity.this.q) {
                            PowerMainActivity.this.k.setText(u.a(0));
                            ValueAnimator duration = ValueAnimator.ofInt(new int[]{0, this.f6648a}).setDuration(1100);
                            duration.setInterpolator(new CubicEaseOutInterpolator());
                            duration.setRepeatCount(0);
                            duration.addUpdateListener(new m(this));
                            duration.start();
                            boolean unused = PowerMainActivity.this.q = true;
                        } else {
                            PowerMainActivity.this.k.setText(u.a(i));
                        }
                        if (this.f6649b) {
                            PowerMainActivity.this.l.setVisibility(0);
                        } else {
                            PowerMainActivity.this.l.setVisibility(4);
                        }
                        PowerMainActivity.this.g.setText(PowerMainActivity.a(PowerMainActivity.this.s, this.f6648a));
                        PowerMainActivity.this.h.setText(PowerMainActivity.a(PowerMainActivity.this.s));
                        aVar = new a(PowerMainActivity.this, this.f6649b, this.f6648a, (c) null);
                    } else {
                        return;
                    }
                } else {
                    return;
                }
            } else if ("miui.intent.action.POWER_SAVE_MODE_CHANGED".equals(intent.getAction())) {
                PowerMainActivity.this.f6640a.setSaveModeStatus(o.l(PowerMainActivity.this.s));
                PowerMainActivity.this.e.setChecked(o.l(PowerMainActivity.this.s));
                aVar = new a(PowerMainActivity.this, this.f6649b, this.f6648a, (c) null);
            } else {
                return;
            }
            aVar.execute(new Void[0]);
        }
    }

    public static String a(Context context) {
        int a2 = i.a(context, 0, 0);
        return String.format(Locale.getDefault(), context.getResources().getString(R.string.power_main_battery_last_hour_minute), new Object[]{Integer.valueOf(a2 / 60), Integer.valueOf(a2 % 60)});
    }

    public static String a(Context context, int i2) {
        int a2 = s.a(context, o.c(context), i2, 1);
        return String.format(Locale.getDefault(), context.getResources().getString(R.string.power_main_battery_last_hour_minute), new Object[]{Integer.valueOf(a2 / 60), Integer.valueOf(a2 % 60)});
    }

    /* access modifiers changed from: private */
    public void a(boolean z) {
        d.a(new d(this, z));
        com.miui.powercenter.a.a.g(z);
        com.miui.powercenter.a.a.b(o.e(this.s));
    }

    private void initData() {
        this.f6642c = new b(this, (c) null);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.BATTERY_CHANGED");
        intentFilter.addAction("miui.intent.action.POWER_SAVE_MODE_CHANGED");
        registerReceiver(this.f6642c, intentFilter);
        this.f6642c.a(o.k(this.s));
    }

    /* JADX WARNING: type inference failed for: r6v0, types: [android.content.Context, com.miui.powercenter.PowerMainActivity, android.view.View$OnClickListener, miui.app.Activity] */
    private void l() {
        this.f6641b = (RelativeLayout) findViewById(R.id.v_header_layout);
        this.g = (TextView) findViewById(R.id.power_save_summary);
        this.h = (TextView) findViewById(R.id.super_save_summary);
        this.f6640a = (MainBatteryView) findViewById(R.id.pc_power_view);
        int i2 = 0;
        this.f6640a.setWillNotDraw(false);
        this.k = (ScoreTextView) findViewById(R.id.number);
        this.k.setTypeface(t.b(this), 1);
        this.j = (TextView) findViewById(R.id.power_last);
        this.i = (TextView) findViewById(R.id.scan_percent);
        this.l = (ImageView) findViewById(R.id.flag_charging);
        this.f6643d = (Button) findViewById(R.id.save_power);
        this.e = findViewById(R.id.slide_power_save_mode);
        this.e.setOnPerformCheckedChangeListener(this.v);
        this.e.setChecked(o.l(this.s));
        this.f6640a.setSaveModeStatus(o.l(this.s));
        this.f6640a.setButtonStatus(this.f6643d);
        this.f = findViewById(R.id.slide_super_save_mode);
        this.f.setOnPerformCheckedChangeListener(this.v);
        if (!k.o(this)) {
            findViewById(R.id.super_save_container).setVisibility(8);
        }
        this.m = (RelativeLayout) findViewById(R.id.container_power_rank);
        this.n = (RelativeLayout) findViewById(R.id.container_auto_task);
        this.p = (RelativeLayout) findViewById(R.id.container_auto_shutdown);
        this.o = (RelativeLayout) findViewById(R.id.container_app_battery_saver);
        this.m.setOnClickListener(this);
        this.n.setOnClickListener(this);
        this.p.setOnClickListener(this);
        this.o.setOnClickListener(this);
        this.n.setVisibility(u.d() ? 0 : 8);
        this.o.setVisibility(u.d() ? 8 : 0);
        RelativeLayout relativeLayout = this.p;
        if (!u.e()) {
            i2 = 8;
        }
        relativeLayout.setVisibility(i2);
        if ("tr".equals(Locale.getDefault().getLanguage())) {
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) this.i.getLayoutParams();
            layoutParams.addRule(17);
            layoutParams.addRule(16, R.id.number);
            this.i.setLayoutParams(layoutParams);
        }
        this.f6643d.setOnClickListener(this);
        this.f6643d.setClickable(true);
    }

    /* access modifiers changed from: private */
    public String m() {
        int a2 = s.a(this.s, o.c(this.s), o.e(this.s), 1);
        int i2 = a2 / 60;
        int i3 = a2 % 60;
        Context context = this.s;
        String string = context.getString(R.string.keyguard_charging_info_drained_time_format, new Object[]{context.getResources().getQuantityString(R.plurals.keyguard_charging_info_drained_hour_time_format, i2, new Object[]{s.a(i2)}), this.s.getResources().getQuantityString(R.plurals.keyguard_charging_info_drained_min_time_format, i3, new Object[]{s.a(i3)})});
        return this.s.getString(R.string.power_center_dialog_msg_title, new Object[]{string});
    }

    /* JADX WARNING: type inference failed for: r3v0, types: [b.b.c.c.a, android.content.Context, com.miui.powercenter.PowerMainActivity, miui.app.Activity, android.app.Activity] */
    private void n() {
        ActionBar actionBar = getActionBar();
        ImageView imageView = new ImageView(this);
        imageView.setBackgroundResource(isDarkModeEnable() ? miui.R.drawable.icon_settings_dark : miui.R.drawable.icon_settings_light);
        imageView.setContentDescription(getString(R.string.activity_title_settings));
        imageView.setOnClickListener(new c(this));
        C0195b.a(actionBar, (View) imageView);
        if (e.a(this) <= 1920) {
            C0195b.a(actionBar, false);
            C0195b.a(actionBar, 0);
        }
    }

    private void o() {
        try {
            Intent parseUri = Intent.parseUri("#Intent;action=miui.intent.action.POWER_SCAN;end", 0);
            parseUri.putExtra("enter_homepage_way", "00001");
            parseUri.putExtra("track_gamebooster_enter_way", "00001");
            if (!x.c(this.s, parseUri)) {
                A.a(this.s, (int) R.string.app_not_installed_toast);
            }
        } catch (URISyntaxException e2) {
            e2.printStackTrace();
        }
    }

    /* JADX WARNING: type inference failed for: r2v0, types: [android.content.Context, com.miui.powercenter.PowerMainActivity, miui.app.Activity] */
    /* access modifiers changed from: private */
    public void p() {
        com.miui.powercenter.a.a.g("setting");
        startActivity(new Intent(this, PowerSettings.class));
    }

    /* JADX WARNING: type inference failed for: r1v0, types: [com.miui.powercenter.PowerMainActivity, miui.app.Activity, android.app.Activity] */
    public void onBackPressed() {
        PowerMainActivity.super.onBackPressed();
        if (this.t) {
            o.a((android.app.Activity) this);
        }
    }

    /* JADX WARNING: type inference failed for: r4v0, types: [com.miui.powercenter.PowerMainActivity, miui.app.Activity, android.app.Activity] */
    public void onClick(View view) {
        String str;
        if (view.getId() == R.id.settings) {
            p();
        } else if (view.getId() == R.id.back) {
            finish();
            if (this.t) {
                o.a((android.app.Activity) this);
            }
        } else if (view.getId() == R.id.save_power) {
            o();
        } else {
            if (view.getId() == R.id.container_power_rank) {
                if (System.currentTimeMillis() - this.u >= 500) {
                    this.u = System.currentTimeMillis();
                    view.getContext().startActivity(new Intent("android.intent.action.POWER_USAGE_SUMMARY"));
                    str = "expend_top";
                } else {
                    return;
                }
            } else if (view.getId() == R.id.container_auto_task) {
                view.getContext().startActivity(new Intent(view.getContext(), AutoTaskManageActivity.class));
                str = "auto_task";
            } else if (view.getId() == R.id.container_auto_shutdown) {
                view.getContext().startActivity(new Intent(view.getContext(), PowerShutdownOnTime.class));
                str = "power_on_off_plan";
            } else if (view.getId() == R.id.container_app_battery_saver) {
                try {
                    view.getContext().startActivity(new Intent("miui.intent.action.POWER_HIDE_MODE_APP_LIST"));
                    com.miui.powercenter.a.a.d("app_smart_save");
                    return;
                } catch (Exception e2) {
                    Log.d("PowerMainActivity", "can not find hide mode action", e2);
                    return;
                }
            } else {
                return;
            }
            com.miui.powercenter.a.a.d(str);
        }
    }

    /* JADX WARNING: type inference failed for: r3v0, types: [b.b.c.c.a, android.content.Context, com.miui.powercenter.PowerMainActivity, miui.app.Activity] */
    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.pc_activity_main_2);
        k.a((Activity) this);
        this.s = this;
        String stringExtra = getIntent().getStringExtra("enter_homepage_way");
        this.t = getIntent().getBooleanExtra("overried_transition", false);
        if (!TextUtils.isEmpty(stringExtra)) {
            com.miui.powercenter.a.a.a(stringExtra);
        }
        n();
        l();
        initData();
        if (u.b() && g.b()) {
            com.miui.powercenter.a.a.b();
        }
    }

    /* access modifiers changed from: protected */
    public void onDestroy() {
        PowerMainActivity.super.onDestroy();
        b bVar = this.f6642c;
        if (bVar != null) {
            unregisterReceiver(bVar);
        }
    }

    /* access modifiers changed from: protected */
    public void onPause() {
        super.onPause();
    }

    /* access modifiers changed from: protected */
    public void onResume() {
        super.onResume();
        SlidingButton slidingButton = this.f;
        if (slidingButton != null) {
            slidingButton.setOnPerformCheckedChangeListener((CompoundButton.OnCheckedChangeListener) null);
            this.f.setChecked(o.m(getApplicationContext()));
            this.f.setOnPerformCheckedChangeListener(this.v);
        }
    }
}
