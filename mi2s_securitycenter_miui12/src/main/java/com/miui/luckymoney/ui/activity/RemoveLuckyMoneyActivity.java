package com.miui.luckymoney.ui.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.IPackageDeleteObserver;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import b.b.c.j.B;
import b.b.c.j.g;
import b.b.c.j.x;
import b.b.o.b.a.a;
import com.miui.luckymoney.config.AppConstants;
import com.miui.securitycenter.R;

public class RemoveLuckyMoneyActivity extends Activity {
    private AlertDialog mDialog;

    /* access modifiers changed from: private */
    public void removeLuckyMoney() {
        if (x.h(this, AppConstants.Package.PACKAGE_NAME_HB)) {
            a.a(getPackageManager(), AppConstants.Package.PACKAGE_NAME_HB, (IPackageDeleteObserver) null, 0);
        }
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        if (Build.VERSION.SDK_INT != 26) {
            setRequestedOrientation(1);
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(View.inflate(this, R.layout.activity_remove_luckymoney, (ViewGroup) null));
        builder.setPositiveButton(R.string.dialog_remobe_luckymoney_ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                RemoveLuckyMoneyActivity.this.removeLuckyMoney();
                RemoveLuckyMoneyActivity removeLuckyMoneyActivity = RemoveLuckyMoneyActivity.this;
                g.b((Context) removeLuckyMoneyActivity, new Intent(removeLuckyMoneyActivity, LuckySettingActivity.class), B.b());
                RemoveLuckyMoneyActivity.this.finish();
            }
        });
        builder.setNegativeButton(R.string.dialog_remobe_luckymoney_cancle, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                RemoveLuckyMoneyActivity.this.finish();
            }
        });
        this.mDialog = builder.create();
        this.mDialog.setCancelable(false);
        this.mDialog.getWindow().setType(2003);
        this.mDialog.show();
    }

    /* access modifiers changed from: protected */
    public void onDestroy() {
        super.onDestroy();
    }
}
