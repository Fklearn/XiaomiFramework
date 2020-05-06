package com.miui.antispam.ui.activity;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import b.b.a.d.a.a;
import com.miui.antispam.ui.view.AntiSpamEditorTitleView;
import com.miui.antispam.ui.view.CheckListView;
import com.miui.securitycenter.R;
import java.util.ArrayList;
import java.util.List;
import miui.app.ActionBar;

public class AntiSpamAddressActivity extends r {

    /* renamed from: d  reason: collision with root package name */
    public a f2511d;
    public CheckListView e;
    private AntiSpamEditorTitleView f;
    /* access modifiers changed from: private */
    public CheckBox g;
    /* access modifiers changed from: private */
    public CheckBox h;
    /* access modifiers changed from: private */
    public View i;
    public AlertDialog j;
    public List<String> k = new ArrayList();
    public List<Integer> l = new ArrayList();

    /* JADX WARNING: type inference failed for: r6v0, types: [android.content.Context, com.miui.antispam.ui.activity.AntiSpamAddressActivity, com.miui.antispam.ui.activity.r, miui.app.Activity] */
    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.address_list);
        Intent intent = getIntent();
        boolean booleanExtra = intent.getBooleanExtra("is_black", true);
        int intExtra = intent.getIntExtra(AddAntiSpamActivity.g, 1);
        this.f = (AntiSpamEditorTitleView) getLayoutInflater().inflate(R.layout.antispam_new_editor_title_layout, (ViewGroup) null);
        this.f.getOk().setOnClickListener(new C0217k(this, booleanExtra, intExtra));
        this.f.getCancel().setOnClickListener(new C0218l(this));
        this.f.getTitle().setText(R.string.st_title_adress);
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setDisplayOptions(16, 29);
            actionBar.setCustomView(this.f, new ActionBar.LayoutParams(-1, -2));
        }
        this.f2511d = new a(this, this.f);
        this.e = (CheckListView) findViewById(R.id.list);
        this.e.setAdapter(this.f2511d);
        this.i = LayoutInflater.from(this).inflate(R.layout.sp_choose_mode, (ViewGroup) null);
        this.g = (CheckBox) this.i.findViewById(R.id.SMSpass);
        this.g.setText(booleanExtra ? R.string.st_message_SMS_AntiSpam : R.string.SMSpass);
        this.h = (CheckBox) this.i.findViewById(R.id.Phonepass);
        this.h.setText(booleanExtra ? R.string.st_message_phone_AntiSpam : R.string.Phonepass);
    }
}
