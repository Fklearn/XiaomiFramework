package com.miui.antispam.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import b.b.a.d.b.i;

public class CallLogListActivity extends r {
    public void onBackPressed() {
        finish();
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        Intent intent = getIntent();
        if (intent == null) {
            finish();
            return;
        }
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setTitle("");
        getActionBar().setSubtitle("");
        Bundle bundle2 = new Bundle();
        bundle2.putString("number", intent.getStringExtra("number"));
        bundle2.putInt("number_presentation", intent.getIntExtra("number_presentation", 1));
        if (bundle == null) {
            i iVar = new i();
            iVar.setArguments(bundle2);
            getFragmentManager().beginTransaction().replace(16908290, iVar).commit();
        }
    }
}
