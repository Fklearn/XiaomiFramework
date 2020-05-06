package com.miui.luckymoney.ui.activity;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import com.miui.luckymoney.ui.view.PendingIntentRunnable;
import com.miui.luckymoney.utils.ScreenUtil;
import com.miui.securitycenter.R;

public class OpenLockScreenActivity extends Activity {
    public static String EXTRA_ACTION_INTENT = "action_intent";
    /* access modifiers changed from: private */
    public PendingIntent mActionIntent;
    /* access modifiers changed from: private */
    public boolean mActionStarted = false;

    private void startAction(int i) {
        new Handler().postDelayed(new Runnable() {
            public void run() {
                if (!OpenLockScreenActivity.this.mActionStarted) {
                    boolean unused = OpenLockScreenActivity.this.mActionStarted = true;
                    if (ScreenUtil.isScreenLocked(OpenLockScreenActivity.this.getApplicationContext())) {
                        OpenLockScreenActivity.this.sendBroadcast(new Intent(ScreenUtil.ACTION_SHOW_MIUI_SECURE_KEYGUARD));
                        int i = 0;
                        while (i < 300) {
                            long currentTimeMillis = System.currentTimeMillis();
                            try {
                                Thread.sleep(10);
                            } catch (InterruptedException unused2) {
                            }
                            int currentTimeMillis2 = (int) (System.currentTimeMillis() - currentTimeMillis);
                            if (currentTimeMillis2 < 0) {
                                currentTimeMillis2 = 0;
                            }
                            i += currentTimeMillis2;
                            if (!ScreenUtil.isScreenLocked(OpenLockScreenActivity.this.getApplicationContext())) {
                                break;
                            }
                        }
                    }
                    if (ScreenUtil.isScreenLocked(OpenLockScreenActivity.this.getApplicationContext())) {
                        OpenLockScreenActivity.this.finish();
                        ScreenUtil.unlockSecureMiuiKeyguard(OpenLockScreenActivity.this.getApplicationContext(), OpenLockScreenActivity.this.mActionIntent);
                        return;
                    }
                    if (OpenLockScreenActivity.this.mActionIntent != null) {
                        new PendingIntentRunnable(OpenLockScreenActivity.this.mActionIntent).run();
                    }
                    OpenLockScreenActivity.this.finish();
                    OpenLockScreenActivity.this.sendBroadcast(new Intent(ScreenUtil.ACTION_SHOW_MIUI_SECURE_KEYGUARD));
                    ScreenUtil.notifyKeyguardUnlocked();
                }
            }
        }, (long) i);
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        getWindow().addFlags(4718592);
        setContentView(R.layout.transparent_layout);
        this.mActionIntent = (PendingIntent) getIntent().getParcelableExtra(EXTRA_ACTION_INTENT);
    }

    /* access modifiers changed from: protected */
    public void onResume() {
        super.onResume();
        startAction(1000);
    }

    public void onWindowFocusChanged(boolean z) {
        super.onWindowFocusChanged(z);
        if (z) {
            startAction(0);
        }
    }
}
