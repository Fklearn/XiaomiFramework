package com.miui.networkassistant.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import com.miui.securitycenter.R;
import miui.app.ActionBar;
import miui.app.Activity;

public class NetworkDiagnosticsTipActivity extends Activity {
    public static final String DETAIL_KEY_NAME = "detail";
    public static final String TITLE_KEY_NAME = "title";
    private TextView mDetailView;
    private String mTitle;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        Intent intent;
        NetworkDiagnosticsTipActivity.super.onCreate(bundle);
        setContentView(R.layout.activity_network_diagnostics_tip);
        this.mDetailView = (TextView) findViewById(R.id.network_diagnotics_detail);
        if (this.mDetailView != null && (intent = getIntent()) != null) {
            int intExtra = intent.getIntExtra(DETAIL_KEY_NAME, 0);
            if (intExtra != 0) {
                this.mDetailView.setText(intExtra);
            }
            ActionBar actionBar = getActionBar();
            if (actionBar != null) {
                actionBar.setTitle(intent.getStringExtra(TITLE_KEY_NAME));
            }
        }
    }
}
