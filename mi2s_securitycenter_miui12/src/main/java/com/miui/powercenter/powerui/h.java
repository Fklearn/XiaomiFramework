package com.miui.powercenter.powerui;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.hardware.display.DisplayManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;
import android.text.Html;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;
import b.b.c.j.B;
import com.miui.networkassistant.config.Constants;
import com.miui.powercenter.utils.o;
import com.miui.powercenter.utils.s;
import com.miui.powercenter.y;
import com.miui.securitycenter.R;
import com.miui.superpower.b.k;
import com.xiaomi.stat.MiStat;
import java.io.File;
import miui.os.Build;
import miui.util.FeatureParser;

public class h {
    /* access modifiers changed from: private */

    /* renamed from: a  reason: collision with root package name */
    public static final File f7149a = new File("/system/media/audio/ui/disconnect.ogg");
    /* access modifiers changed from: private */

    /* renamed from: b  reason: collision with root package name */
    public static final File f7150b = new File("/system/media/audio/ui/charge_wireless.ogg");
    /* access modifiers changed from: private */

    /* renamed from: c  reason: collision with root package name */
    public static final File f7151c = new File("/system/media/audio/ui/charging.ogg");

    /* renamed from: d  reason: collision with root package name */
    private Handler f7152d;
    /* access modifiers changed from: private */
    public Context e;
    private TelephonyManager f;
    private SharedPreferences g;
    private a h;
    private DisplayManager i;
    /* access modifiers changed from: private */
    public Display j;
    private int k = 100;
    /* access modifiers changed from: private */
    public int l = 0;
    private int m = 0;
    private int n = 30;
    private int[] o = new int[3];
    /* access modifiers changed from: private */
    public AlertDialog p;
    /* access modifiers changed from: private */
    public AlertDialog q;
    /* access modifiers changed from: private */
    public AlertDialog r;
    /* access modifiers changed from: private */
    public AlertDialog s;
    private boolean t = true;
    /* access modifiers changed from: private */
    public TextView u;
    private boolean v = true;
    private boolean w = false;
    private DisplayManager.DisplayListener x = new g(this);

    private class a extends BroadcastReceiver {

        /* renamed from: a  reason: collision with root package name */
        private Context f7153a;

        public a(Context context) {
            this.f7153a = context;
        }

        /* access modifiers changed from: private */
        public void a() {
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction("android.intent.action.CLOSE_SYSTEM_DIALOGS");
            this.f7153a.registerReceiver(this, intentFilter);
        }

        /* access modifiers changed from: private */
        public void b() {
            this.f7153a.unregisterReceiver(this);
        }

        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (!TextUtils.isEmpty(action) && "android.intent.action.CLOSE_SYSTEM_DIALOGS".equals(action)) {
                String stringExtra = intent.getStringExtra("reason");
                if ("homekey".equals(stringExtra) || "recentapps".equals(stringExtra)) {
                    h.this.h();
                    h.this.i();
                    h.this.j();
                    h.this.k();
                }
            }
        }
    }

    private final class b extends AsyncTask<Void, Void, Bundle> {
        private b() {
        }

        /* synthetic */ b(h hVar, a aVar) {
            this();
        }

        /* access modifiers changed from: protected */
        /* renamed from: a */
        public Bundle doInBackground(Void... voidArr) {
            long b2 = y.b();
            long d2 = y.d();
            int c2 = y.c();
            Bundle bundle = new Bundle();
            bundle.putLong("lastChargedTime", b2);
            bundle.putLong("drainedTime", d2);
            bundle.putInt("drainedPercent", c2);
            return bundle;
        }

        /* access modifiers changed from: protected */
        /* renamed from: a */
        public void onPostExecute(Bundle bundle) {
            View inflate = View.inflate(h.this.e, R.layout.extreme_power_save_view, (ViewGroup) null);
            h hVar = h.this;
            String a2 = hVar.a(hVar.e, bundle);
            h hVar2 = h.this;
            ((TextView) inflate.findViewById(R.id.expected_time)).setText(hVar2.a(hVar2.e.getString(R.string.battery_can_use_time, new Object[]{a2}), a2));
            AlertDialog create = new AlertDialog.Builder(h.this.e, 2131821043).setCancelable(true).setTitle(R.string.open_extreme_power_save_mode_title).setView(inflate).setNegativeButton(R.string.dlg_cancel, (DialogInterface.OnClickListener) null).setPositiveButton(R.string.dlg_confirm, new i(this)).create();
            h.this.h();
            create.getWindow().setType(2003);
            create.show();
            AlertDialog unused = h.this.s = create;
        }
    }

    private int a(int i2) {
        if (i2 >= this.n) {
            return 1;
        }
        int[] iArr = this.o;
        if (i2 >= iArr[0]) {
            return 0;
        }
        for (int length = iArr.length - 1; length >= 0; length--) {
            int[] iArr2 = this.o;
            if (i2 < iArr2[length] && iArr2[length] > 0) {
                return -1 - length;
            }
        }
        throw new RuntimeException("not possible!");
    }

    /* access modifiers changed from: private */
    public SpannableString a(String str, String str2) {
        int indexOf = str.indexOf(str2);
        SpannableString spannableString = new SpannableString(str);
        if (indexOf > 0) {
            spannableString.setSpan(new ForegroundColorSpan(ContextCompat.getColor(this.e, R.color.extreme_drained_time_color)), indexOf, str2.length() + indexOf, 33);
        }
        return spannableString;
    }

    /* access modifiers changed from: private */
    public String a(Context context, Bundle bundle) {
        long j2 = bundle.getLong("drainedTime");
        if (j2 <= 0) {
            return "-";
        }
        long b2 = k.b(j2);
        long a2 = k.a(j2);
        int i2 = (a2 > 0 ? 1 : (a2 == 0 ? 0 : -1));
        if (i2 > 0 && b2 > 0) {
            return context.getResources().getString(R.string.keyguard_charging_info_drained_time_format, new Object[]{context.getResources().getQuantityString(R.plurals.keyguard_charging_info_drained_hour_time_format, (int) a2, new Object[]{Long.valueOf(a2)}), context.getResources().getQuantityString(R.plurals.keyguard_charging_info_drained_min_time_format, (int) b2, new Object[]{Long.valueOf(b2)})});
        } else if (i2 > 0) {
            return context.getResources().getQuantityString(R.plurals.keyguard_charging_info_drained_hour_time_format, (int) a2, new Object[]{Long.valueOf(a2)});
        } else if (b2 <= 0) {
            return "-";
        } else {
            return context.getResources().getQuantityString(R.plurals.keyguard_charging_info_drained_min_time_format, (int) b2, new Object[]{Long.valueOf(b2)});
        }
    }

    /* access modifiers changed from: private */
    public void a(Uri uri) {
        if (Settings.System.getInt(this.e.getContentResolver(), "power_sounds_enabled", 1) == 1 && uri != null) {
            k.a(this.e, uri, 1);
        }
    }

    /* access modifiers changed from: private */
    public void a(boolean z) {
        Settings.Secure.putInt(this.e.getContentResolver(), "is_first_open_extreme_power_save", z ? 1 : 0);
    }

    private int b(int i2) {
        this.g.edit().putInt("level_old", i2).apply();
        return i2;
    }

    private boolean g() {
        return Settings.Secure.getInt(this.e.getContentResolver(), Constants.System.DEVICE_PROVISIONED, 0) != 0;
    }

    /* access modifiers changed from: private */
    public void h() {
        AlertDialog alertDialog = this.s;
        if (alertDialog != null) {
            alertDialog.dismiss();
        }
    }

    /* access modifiers changed from: private */
    public void i() {
        AlertDialog alertDialog = this.p;
        if (alertDialog != null) {
            alertDialog.dismiss();
        }
    }

    /* access modifiers changed from: private */
    public void j() {
        if (this.q != null) {
            Log.i("PowerNoticeUI", "closing low battery warning: level=" + this.k);
            this.q.dismiss();
        }
    }

    /* access modifiers changed from: private */
    public void k() {
        AlertDialog alertDialog = this.r;
        if (alertDialog != null) {
            alertDialog.dismiss();
        }
    }

    private int l() {
        return this.g.getInt("level_old", 100);
    }

    private void m() {
        k.c(this.e);
    }

    /* access modifiers changed from: private */
    public boolean n() {
        return Settings.Secure.getInt(this.e.getContentResolver(), "is_first_open_extreme_power_save", 1) == 1;
    }

    private boolean o() {
        return this.e.getResources().getConfiguration().orientation == 1 && this.v;
    }

    private boolean p() {
        return FeatureParser.getBoolean("support_extreme_battery_saver", false) && !k.o(this.e) && B.f();
    }

    /* access modifiers changed from: private */
    public void q() {
        String string = Settings.System.getString(this.e.getContentResolver(), "low_battery_sound");
        if (string != null) {
            Context context = this.e;
            k.a(context, Uri.parse("file://" + string), 1);
        }
    }

    private void r() {
        Log.d("PowerNoticeUI", "showing invalid charger dialog");
        j();
        h();
        AlertDialog create = new AlertDialog.Builder(this.e, 2131821043).setCancelable(true).setMessage(R.string.invalid_charger).setIconAttribute(16843605).setPositiveButton(17039370, (DialogInterface.OnClickListener) null).setOnDismissListener(new e(this)).create();
        create.getWindow().setType(2003);
        create.show();
        this.p = create;
    }

    private void s() {
        if (!o.m(this.e)) {
            if (!this.v || !o()) {
                k.a(this.e, this.k);
            }
        }
    }

    private void t() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.u == null ? "showing" : "updating");
        sb.append(" low battery warning: level=");
        sb.append(this.k);
        sb.append(" [");
        sb.append(a(this.k));
        sb.append("]");
        Log.i("PowerNoticeUI", sb.toString());
        if (!o()) {
            Log.i("PowerNoticeUI", "low battery dialog not shown");
        } else if (Settings.System.getInt(this.e.getContentResolver(), "low_battery_dialog_disabled", 0) != 1 && 1 != Settings.System.getInt(this.e.getContentResolver(), "vr_mode", 0) && this.f.getCallState() != 1 && g() && !o.m(this.e)) {
            int a2 = k.a(this.k);
            Context context = this.e;
            String string = context.getString(R.string.battery_low_percent_format_save_mode_closed, new Object[]{a2 + "%"});
            View inflate = View.inflate(this.e, R.layout.battery_low, (ViewGroup) null);
            this.u = (TextView) inflate.findViewById(R.id.level_percent);
            TextView textView = (TextView) inflate.findViewById(R.id.title);
            this.u.setText(string);
            textView.setText(R.string.battery_low_title);
            AlertDialog.Builder negativeButton = new AlertDialog.Builder(this.e, 2131821043).setCancelable(true).setView(inflate).setIconAttribute(16843605).setNegativeButton(R.string.save_mode_btn_ok, (DialogInterface.OnClickListener) null);
            if (!Build.IS_TABLET) {
                if (p() && this.k <= 5 && !k.d(this.e)) {
                    negativeButton.setPositiveButton(R.string.enable_extreme_power_save_mode, new b(this));
                } else if (!k.d(this.e) && !k.e(this.e)) {
                    int a3 = s.a(this.e, o.c(this.e), this.k, 1);
                    int i2 = a3 / 60;
                    int i3 = a3 % 60;
                    Context context2 = this.e;
                    String string2 = context2.getString(R.string.keyguard_charging_info_drained_time_format, new Object[]{context2.getResources().getQuantityString(R.plurals.keyguard_charging_info_drained_hour_time_format, i2, new Object[]{s.a(i2)}), this.e.getResources().getQuantityString(R.plurals.keyguard_charging_info_drained_min_time_format, i3, new Object[]{s.a(i3)})});
                    Context context3 = this.e;
                    String string3 = context3.getString(R.string.battery_low_percent_format_save_mode_open, new Object[]{a2 + "%", string2});
                    if (k.f(this.e)) {
                        k.g(this.e);
                        negativeButton.setTitle(R.string.power_center_scan_item_title_power_saver);
                        View inflate2 = View.inflate(this.e, R.layout.power_save_rich_detail_battery_low, (ViewGroup) null);
                        ((TextView) inflate2.findViewById(R.id.pc_noticeui_content4)).setText(o.i(this.e));
                        ((TextView) inflate2.findViewById(R.id.level_percent)).setText(Html.fromHtml(string3));
                        negativeButton.setView(inflate2);
                    } else {
                        this.u.setText(Html.fromHtml(string3));
                    }
                    textView.setText(R.string.battery_low_title_power_save);
                    negativeButton.setNegativeButton(R.string.battery_low_button_not_open_power_save, (DialogInterface.OnClickListener) null).setPositiveButton(R.string.battery_low_button_open_power_save, new c(this));
                }
            }
            AlertDialog create = negativeButton.create();
            create.setOnDismissListener(new d(this));
            create.getWindow().setType(2003);
            create.show();
            this.q = create;
            k.h(this.e);
        }
    }

    private void u() {
        Log.d("PowerNoticeUI", "showing low temperature dialog");
        j();
        i();
        h();
        AlertDialog create = new AlertDialog.Builder(this.e, 2131821043).setCancelable(true).setTitle(R.string.low_temperature_warning_title).setMessage(R.string.low_temperature_warning_message).setIconAttribute(16843605).setPositiveButton(R.string.low_temperature_button_ok, (DialogInterface.OnClickListener) null).setOnDismissListener(new f(this)).create();
        create.getWindow().setType(2010);
        create.show();
        this.r = create;
    }

    public void a(Context context) {
        this.e = context.getApplicationContext();
        if (Settings.Global.getInt(this.e.getContentResolver(), "sysui_powerui_enabled", 1) != 1) {
            this.w = true;
            k.a(this.e);
            this.h = new a(this.e);
            this.h.a();
            this.g = this.e.getSharedPreferences("power_battery_level", 0);
            this.j = ((WindowManager) context.getSystemService("window")).getDefaultDisplay();
            this.i = (DisplayManager) context.getSystemService("display");
            this.f = (TelephonyManager) context.getSystemService("phone");
            this.n = 30;
            int[] iArr = this.o;
            int[] iArr2 = k.f7160a;
            iArr[0] = iArr2[0];
            iArr[1] = iArr2[1];
            iArr[2] = iArr2[2];
            if (p()) {
                this.o[2] = 5;
            }
            HandlerThread handlerThread = new HandlerThread("PowerNoticeUI", 10);
            handlerThread.start();
            this.f7152d = new a(this, handlerThread.getLooper());
            this.i.registerDisplayListener(this.x, (Handler) null);
        }
    }

    public void a(Intent intent) {
        Handler handler;
        int i2;
        if (this.w) {
            int l2 = l();
            int intExtra = intent.getIntExtra(MiStat.Param.LEVEL, 100);
            b(intExtra);
            this.k = intExtra;
            boolean z = true;
            int intExtra2 = intent.getIntExtra("status", 1);
            int i3 = this.l;
            this.l = intent.getIntExtra("plugged", 1);
            int i4 = this.m;
            this.m = intent.getIntExtra("invalid_charger", 0);
            boolean z2 = this.l != 0;
            boolean z3 = i3 != 0;
            int a2 = a(l2);
            int a3 = a(this.k);
            int intExtra3 = intent.getIntExtra("temperature", 0);
            if (!this.t || this.r != null || intExtra3 > -80 || this.k > 50) {
                if (intExtra3 >= 0) {
                    this.t = true;
                    k();
                } else if (this.r != null) {
                    return;
                }
                if (i4 != 0 || this.m == 0) {
                    if (i4 != 0 && this.m == 0) {
                        i();
                    } else if (this.p != null) {
                        return;
                    }
                    if (!z2 && ((a3 < a2 || z3) && intExtra2 != 1 && a3 < 0)) {
                        j();
                        t();
                        s();
                        this.f7152d.removeMessages(1);
                        if (!o.m(this.e) && this.v && this.e.getResources().getConfiguration().orientation == 1) {
                            this.f7152d.obtainMessage(1).sendToTarget();
                        }
                        z = false;
                    } else if (z2 || (a3 > a2 && a3 > 0)) {
                        j();
                        h();
                        m();
                    }
                    if (z2 && !z3) {
                        handler = this.f7152d;
                        i2 = 3;
                    } else if (z && !z2 && z3) {
                        handler = this.f7152d;
                        i2 = 2;
                    } else {
                        return;
                    }
                    handler.removeMessages(i2);
                    this.f7152d.obtainMessage(i2).sendToTarget();
                    return;
                }
                Log.d("PowerNoticeUI", "showing invalid charger warning");
                r();
                return;
            }
            u();
            this.t = false;
        }
    }

    public final void d() {
        Log.d("PowerNoticeUI", "ScreenOffEvent");
        this.v = false;
    }

    public final void e() {
        Log.d("PowerNoticeUI", "ScreenOnEvent");
        this.v = true;
    }

    public void f() {
        a aVar = this.h;
        if (aVar != null) {
            aVar.b();
            this.h = null;
        }
        this.i.unregisterDisplayListener(this.x);
        this.w = false;
    }
}
