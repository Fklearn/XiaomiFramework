package com.miui.antispam.ui.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import b.b.a.a;
import b.b.a.e.c;
import b.b.a.e.n;
import com.miui.maml.elements.AdvancedSlider;
import com.miui.securitycenter.R;
import miui.cloud.common.XSimChangeNotification;
import miui.provider.ExtraTelephony;

public class AddAntiSpamActivity extends r {

    /* renamed from: d  reason: collision with root package name */
    public static String f2508d = "mode";
    public static String e = "state";
    public static String f = "address_code";
    public static String g = "sim_id";
    public static String h = "is_add_complete";
    public static String i = "needConfirm";

    private class a implements DialogInterface.OnClickListener {
        private a() {
        }

        /* synthetic */ a(AddAntiSpamActivity addAntiSpamActivity, C0207a aVar) {
            this();
        }

        public void onClick(DialogInterface dialogInterface, int i) {
            AddAntiSpamActivity.this.finish();
        }
    }

    /* JADX WARNING: type inference failed for: r10v0, types: [android.content.Context, com.miui.antispam.ui.activity.AddAntiSpamActivity] */
    /* access modifiers changed from: private */
    public void a(int i2, String[] strArr, int[] iArr, int i3, int i4) {
        if (strArr != null && strArr.length != 0) {
            int i5 = i4 + 1;
            if (!c.b((Context) this, i5)) {
                c.a((Context) this, i5, true);
            }
            new C0209c(this, strArr, i2, iArr, i3, i4).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null});
        }
    }

    /* JADX WARNING: type inference failed for: r11v0, types: [android.content.Context, miui.app.Activity, com.miui.antispam.ui.activity.AddAntiSpamActivity] */
    /* access modifiers changed from: private */
    public void a(String str, int i2, int i3, int i4, int i5) {
        String str2;
        int i6 = i2;
        int i7 = i3;
        int i8 = i4;
        int i9 = i5;
        if (!TextUtils.isEmpty(str)) {
            if (i9 == 0) {
                if ((i7 == a.c.f1311a || i7 == a.c.f1312b) && !c.e(this)) {
                    a(str, i2, i3, i4, 2);
                }
                a(str, i2, i3, i4, 1);
                return;
            }
            if (i6 == -1) {
                str2 = str;
            } else {
                str2 = "***" + i2;
            }
            int i10 = 1;
            if (1 == i7) {
                i10 = 2;
            }
            if (!n.a(this, str2, i4, i3, i9)) {
                ContentValues contentValues = new ContentValues();
                contentValues.put("number", str2);
                contentValues.put(AdvancedSlider.STATE, Integer.valueOf(i4));
                contentValues.put("type", Integer.valueOf(i3));
                contentValues.put(XSimChangeNotification.BROADCAST_EXTRA_KEY_SIM_ID, Integer.valueOf(i5));
                if (i6 != -1) {
                    String str3 = str;
                    contentValues.put("notes", str);
                }
                getContentResolver().insert(ExtraTelephony.Phonelist.CONTENT_URI, contentValues);
            }
            n.b(this, str2, i4, i10, i9);
        }
    }

    /* JADX WARNING: type inference failed for: r10v0, types: [android.content.Context, com.miui.antispam.ui.activity.r, miui.app.Activity, android.app.Activity, com.miui.antispam.ui.activity.AddAntiSpamActivity] */
    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        overridePendingTransition(0, 0);
        if (n.b((Activity) this)) {
            finish();
            return;
        }
        if (Build.VERSION.SDK_INT != 26) {
            setRequestedOrientation(1);
        }
        Intent intent = getIntent();
        int intExtra = intent.getIntExtra(e, 0);
        int intExtra2 = intent.getIntExtra(f2508d, 0);
        int intExtra3 = intent.getIntExtra(g, 0);
        String[] stringArrayExtra = intent.getStringArrayExtra("numbers");
        int[] intArrayExtra = intent.getIntArrayExtra(f);
        if (intent.getBooleanExtra(i, false)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.dlg_add_blacklist_title);
            builder.setMessage(R.string.dlg_add_blacklist);
            builder.setPositiveButton(17039370, new C0207a(this, intExtra2, stringArrayExtra, intArrayExtra, intExtra, intExtra3));
            builder.setNegativeButton(17039360, new a(this, (C0207a) null));
            builder.setOnCancelListener(new C0208b(this));
            builder.create().show();
            return;
        }
        a(intExtra2, stringArrayExtra, intArrayExtra, intExtra, intExtra3);
    }
}
