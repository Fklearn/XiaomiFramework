package com.miui.cleanmaster;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import com.miui.securitycenter.R;
import com.xiaomi.stat.MiStat;
import java.lang.ref.WeakReference;
import miui.app.AlertDialog;

public class LowMemoryIntentDispatchActivity extends Activity {

    public static class a implements DialogInterface.OnDismissListener {

        /* renamed from: a  reason: collision with root package name */
        private WeakReference<Activity> f3737a;

        public a(Activity activity) {
            this.f3737a = new WeakReference<>(activity);
        }

        public void onDismiss(DialogInterface dialogInterface) {
            Activity activity;
            WeakReference<Activity> weakReference = this.f3737a;
            if (weakReference != null && (activity = (Activity) weakReference.get()) != null) {
                activity.finish();
            }
        }
    }

    private void a() {
        Intent intent = new Intent("com.miui.cleanmaster.action.START_LOW_MEMORY_CLEAN");
        intent.setPackage("com.miui.cleanmaster");
        intent.putExtra(MiStat.Param.LEVEL, 3);
        startService(intent);
    }

    /* access modifiers changed from: private */
    public void b() {
        Intent intent = new Intent("miui.intent.action.GARBAGE_DEEPCLEAN");
        intent.putExtra("enter_homepage_way", "000029");
        intent.putExtra(MiStat.Param.LEVEL, 3);
        intent.setFlags(268435456);
        g.b(this, intent);
    }

    private void c() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.low_memory_warning_dialog_title);
        builder.setMessage(R.string.low_memory_warning_dialog_msg);
        builder.setNegativeButton(R.string.low_memory_warning_dialog_cancel_button, (DialogInterface.OnClickListener) null);
        builder.setPositiveButton(R.string.low_memory_warning_dialog_ok_button, new l(this));
        builder.setCancelable(false);
        AlertDialog create = builder.create();
        create.setOnDismissListener(new a(this));
        create.getWindow().setType(2003);
        try {
            create.show();
        } catch (Exception e) {
            Log.e("LowMemoryIntentDispatchActivity", "showLowMemoryWarningDialog error", e);
        }
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        Intent intent;
        super.onCreate(bundle);
        String action = getIntent().getAction();
        int intExtra = getIntent().getIntExtra(MiStat.Param.LEVEL, 0);
        if ("com.miui.securitycenter.LunchCleanMaster".equals(action)) {
            if (intExtra == 1 || intExtra == 2) {
                intent = new Intent("miui.intent.action.GARBAGE_CLEANUP");
            } else if (intExtra == 3) {
                if (f.a(this)) {
                    intent = new Intent("miui.intent.action.GARBAGE_DEEPCLEAN");
                } else {
                    Intent intent2 = new Intent();
                    intent2.setClassName("com.xiaomi.market", "com.xiaomi.market.ui.LocalAppsActivity");
                    try {
                        startActivity(intent2);
                    } catch (Exception unused) {
                    }
                }
            }
            intent.putExtra("enter_homepage_way", "00008");
            g.b(this, intent);
        } else if (!"com.miui.securitycenter.action.START_LOW_MEMORY_CLEAN".equals(action)) {
            return;
        } else {
            if (intExtra == 2) {
                c();
                return;
            } else if (intExtra == 3) {
                if (f.a(this)) {
                    a();
                }
            } else {
                return;
            }
        }
        finish();
    }
}
