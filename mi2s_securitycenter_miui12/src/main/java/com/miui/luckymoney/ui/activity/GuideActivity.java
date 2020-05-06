package com.miui.luckymoney.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import com.miui.luckymoney.stats.MiStatUtil;
import com.miui.securitycenter.R;
import com.miui.superpower.b.k;
import miui.app.Activity;

public class GuideActivity extends BaseMiuiActivity {
    private Button btnOpen;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        GuideActivity.super.onCreate(bundle);
        setContentView(R.layout.activity_guide);
        k.a((Activity) this);
        this.btnOpen = (Button) findViewById(R.id.btnOpen);
        this.btnOpen.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                GuideActivity.this.setResult(204);
                MiStatUtil.recordGuidePage(true);
                GuideActivity.this.finish();
            }
        });
        MiStatUtil.recordGuidePage(false);
    }
}
