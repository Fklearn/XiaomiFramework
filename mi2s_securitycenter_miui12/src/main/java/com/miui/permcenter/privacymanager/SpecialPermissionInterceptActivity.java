package com.miui.permcenter.privacymanager;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import b.b.c.j.A;
import b.b.c.j.l;
import b.b.c.j.x;
import com.miui.permcenter.compact.SystemPropertiesCompat;
import com.miui.permission.PermissionContract;
import com.miui.securitycenter.R;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;
import miui.app.Activity;

public class SpecialPermissionInterceptActivity extends Activity implements View.OnClickListener {

    /* renamed from: a  reason: collision with root package name */
    private static final Map<String, b> f6317a = new HashMap();

    /* renamed from: b  reason: collision with root package name */
    private a f6318b;

    /* renamed from: c  reason: collision with root package name */
    private String f6319c;

    /* renamed from: d  reason: collision with root package name */
    private String f6320d;
    private String e;
    private TextView f;
    private LinearLayout g;
    private TextView h;
    private Button i;
    private Button j;
    private int k;

    private static class a extends Handler {

        /* renamed from: a  reason: collision with root package name */
        private WeakReference<SpecialPermissionInterceptActivity> f6321a;

        public a(SpecialPermissionInterceptActivity specialPermissionInterceptActivity) {
            this.f6321a = new WeakReference<>(specialPermissionInterceptActivity);
        }

        public void handleMessage(Message message) {
            super.handleMessage(message);
            SpecialPermissionInterceptActivity specialPermissionInterceptActivity = (SpecialPermissionInterceptActivity) this.f6321a.get();
            if (specialPermissionInterceptActivity != null && !specialPermissionInterceptActivity.isFinishing() && !specialPermissionInterceptActivity.isDestroyed()) {
                specialPermissionInterceptActivity.c();
            }
        }
    }

    public static class b {

        /* renamed from: a  reason: collision with root package name */
        int f6322a;

        /* renamed from: b  reason: collision with root package name */
        int f6323b;

        /* renamed from: c  reason: collision with root package name */
        int f6324c;

        public b(int i, int i2, int i3) {
            this.f6322a = i;
            this.f6323b = i2;
            this.f6324c = i3;
        }
    }

    static {
        f6317a.put("perm_install_unknown", new b(R.string.perm_intercept_unknown_title, R.array.perm_intercept_unknown_source, R.string.perm_intercept_content));
        f6317a.put("perm_notification", new b(R.string.perm_intercept_notification_title, R.array.perm_intercept_notification, R.string.perm_intercept_privacy_content));
        f6317a.put("perm_app_statistics", new b(R.string.perm_intercept_title, R.array.perm_intercept_statitics, R.string.perm_intercept_privacy_content));
        f6317a.put("miui_open_debug", new b(R.string.debug_open_title, R.array.debug_open_intercept, R.string.debug_open_final_tip));
        f6317a.put("miui_close_optimization", new b(R.string.miui_optimization_close_title, R.array.miui_optimization_close_intercept, R.string.miui_optimization_close_final_tip));
        f6317a.put("oaid_close", new b(0, R.string.oaid_reset_content_tip, R.string.oaid_reset_final_tip));
    }

    private void a() {
        try {
            Window window = getWindow();
            window.addFlags(Integer.MIN_VALUE);
            window.getDecorView().setSystemUiVisibility(768);
            window.getClass().getMethod("setNavigationBarColor", new Class[]{Integer.TYPE}).invoke(window, new Object[]{0});
        } catch (Exception unused) {
        }
    }

    private void a(boolean z) {
        if ("miui_open_debug".equals(this.f6320d) && z) {
            Settings.Global.putInt(getContentResolver(), "adb_enabled", z ? 1 : 0);
        } else if ("miui_close_optimization".equals(this.f6320d) && z) {
            SystemPropertiesCompat.set("persist.sys.miui_optimization", Boolean.valueOf(!z).toString());
        }
        setResult(z ? -1 : 0);
        finish();
    }

    /* JADX WARNING: type inference failed for: r11v0, types: [android.content.Context, com.miui.permcenter.privacymanager.SpecialPermissionInterceptActivity, miui.app.Activity] */
    private void b() {
        String[] strArr;
        if (this.f6320d.startsWith("perm") && !TextUtils.isEmpty(this.e)) {
            this.f.setText(getString(f6317a.get(this.f6320d).f6322a, new Object[]{x.j(this, this.f6319c)}));
            this.h.setText(getString(f6317a.get(this.f6320d).f6324c, new Object[]{this.e}));
        } else if (this.f6320d.startsWith("miui")) {
            this.f.setText(f6317a.get(this.f6320d).f6322a);
            this.h.setText(f6317a.get(this.f6320d).f6324c);
        }
        if ("oaid_close".equals(this.f6320d)) {
            this.f.setVisibility(4);
            this.h.setText(f6317a.get(this.f6320d).f6324c);
            strArr = new String[]{getString(f6317a.get(this.f6320d).f6323b)};
        } else {
            strArr = getResources().getStringArray(f6317a.get(this.f6320d).f6323b);
        }
        LayoutInflater from = LayoutInflater.from(this);
        for (String str : strArr) {
            View inflate = from.inflate(R.layout.pm_layout_permission_intercept_item, this.g, false);
            TextView textView = (TextView) inflate.findViewById(R.id.intercept_content_detail);
            if (strArr.length == 1) {
                inflate.findViewById(R.id.intercept_content_point).setVisibility(8);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(textView.getLayoutParams());
                layoutParams.leftMargin = 0;
                textView.setLayoutParams(layoutParams);
            }
            textView.setText(str);
            this.g.addView(inflate);
        }
        this.i.setText(getString(R.string.button_text_accept_timer, new Object[]{Integer.valueOf(this.k)}));
        this.f6318b.sendEmptyMessageDelayed(1, 1000);
    }

    /* access modifiers changed from: private */
    public void c() {
        this.k--;
        int i2 = this.k;
        if (i2 <= 0) {
            this.i.setText(R.string.button_text_accept);
            this.i.setEnabled(true);
            this.k = 0;
            return;
        }
        this.i.setText(getString(R.string.button_text_accept_timer, new Object[]{Integer.valueOf(i2)}));
        this.f6318b.removeMessages(1);
        this.f6318b.sendEmptyMessageDelayed(1, 1000);
    }

    public void onBackPressed() {
    }

    public void onClick(View view) {
        boolean z;
        int id = view.getId();
        if (id == R.id.intercept_warn_allow) {
            z = true;
        } else if (id == R.id.intercept_warn_deny) {
            z = false;
        } else {
            return;
        }
        a(z);
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        SpecialPermissionInterceptActivity.super.onCreate(bundle);
        Window window = getWindow();
        window.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.pm_intercept_bg_color)));
        window.setLayout(-1, -1);
        window.addFlags(4);
        setContentView(R.layout.pm_layout_permission_intercept);
        a();
        this.f6319c = getIntent().getStringExtra("pkgName");
        this.f6320d = getIntent().getStringExtra("permName");
        this.e = getIntent().getStringExtra(PermissionContract.Active.PERMISSION_DESC);
        if (!f6317a.containsKey(this.f6320d)) {
            finish();
            return;
        }
        if (bundle != null) {
            this.k = bundle.getInt("KET_STEP_COUNT", 5);
        } else {
            this.k = 5;
        }
        this.f6318b = new a(this);
        this.f = (TextView) findViewById(R.id.intercept_warn_content_start);
        this.g = (LinearLayout) findViewById(R.id.intercept_warn_content);
        this.h = (TextView) findViewById(R.id.intercept_warn_content_end);
        this.i = (Button) findViewById(R.id.intercept_warn_allow);
        this.j = (Button) findViewById(R.id.intercept_warn_deny);
        this.i.setEnabled(false);
        this.i.setOnClickListener(this);
        this.j.setOnClickListener(this);
        if (A.a()) {
            l.b(this.i);
            l.b(this.j);
        }
        b();
    }

    /* access modifiers changed from: protected */
    public void onDestroy() {
        SpecialPermissionInterceptActivity.super.onDestroy();
        a aVar = this.f6318b;
        if (aVar != null) {
            aVar.removeMessages(1);
        }
    }

    /* access modifiers changed from: protected */
    public void onNewIntent(Intent intent) {
        SpecialPermissionInterceptActivity.super.onNewIntent(intent);
    }

    /* access modifiers changed from: protected */
    public void onSaveInstanceState(Bundle bundle) {
        SpecialPermissionInterceptActivity.super.onSaveInstanceState(bundle);
        bundle.putInt("KET_STEP_COUNT", this.k);
    }
}
