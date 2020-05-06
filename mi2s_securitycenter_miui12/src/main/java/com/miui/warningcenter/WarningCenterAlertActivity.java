package com.miui.warningcenter;

import android.app.StatusBarManager;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import androidxc.recyclerview.widget.LinearLayoutManager;
import androidxc.recyclerview.widget.RecyclerView;
import b.b.c.c.a;
import b.b.o.g.e;
import com.miui.applicationlock.c.s;
import com.miui.earthquakewarning.utils.NotificationUtil;
import com.miui.gamebooster.m.C0384o;
import com.miui.networkassistant.config.Constants;
import com.miui.securitycenter.R;
import com.miui.warningcenter.WarningCenterAlertRecyclerAdapter;
import com.miui.warningcenter.analytics.AnalyticHelper;
import com.miui.warningcenter.mijia.MijiaAlertModel;
import com.miui.warningcenter.mijia.MijiaPlaySound;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

public class WarningCenterAlertActivity extends a {
    private static final String FORCE_FSG_NAV_BAR = "force_fsg_nav_bar";
    private static final String SCREEN_BUTTONS_STATE = "screen_buttons_state";
    private static final String TAG = "WarningCenterAlertAct";
    private boolean isPreviousGestureNav = false;
    private WarningCenterAlertRecyclerAdapter mAdapter;
    private CountDownTimer mCountDownTimer;
    private LinearLayoutManager mLayoutManager;
    private RecyclerView mListView;
    /* access modifiers changed from: private */
    public MijiaPlaySound mPlaySound;
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            if (Constants.System.ACTION_SCREEN_OFF.equals(intent.getAction())) {
                NotificationUtil.muteVolume(context);
            }
        }
    };

    public static void enableStatusBar(Context context, boolean z) {
        ((StatusBarManager) context.getSystemService("statusbar")).disable(!z ? 18939904 : 0);
    }

    public static boolean isGestureNavBar(Context context) {
        Boolean bool = (Boolean) C0384o.a("android.provider.MiuiSettings$Global", "getBoolean", context.getContentResolver(), (String) C0384o.b("android.provider.MiuiSettings$Global", FORCE_FSG_NAV_BAR));
        if (bool != null) {
            return bool.booleanValue();
        }
        return false;
    }

    public static void setGestureNavBar(Context context) {
        try {
            e.a(Class.forName("android.provider.MiuiSettings$Global"), Boolean.TYPE, "putBoolean", (Class<?>[]) new Class[]{ContentResolver.class, String.class, Boolean.TYPE}, context.getContentResolver(), FORCE_FSG_NAV_BAR, false);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e2) {
            e2.printStackTrace();
        } catch (InvocationTargetException e3) {
            e3.printStackTrace();
        } catch (ClassNotFoundException e4) {
            e4.printStackTrace();
        }
    }

    private void startCountDown() {
        this.mCountDownTimer = new CountDownTimer(15000, 1000) {
            public void onFinish() {
                if (WarningCenterAlertActivity.this.mPlaySound != null) {
                    WarningCenterAlertActivity.this.mPlaySound.stop();
                }
            }

            public void onTick(long j) {
            }
        };
        this.mCountDownTimer.start();
    }

    public boolean dispatchKeyEvent(KeyEvent keyEvent) {
        if (handleKeyEvent(keyEvent)) {
            return true;
        }
        return WarningCenterAlertActivity.super.dispatchKeyEvent(keyEvent);
    }

    public boolean handleKeyEvent(KeyEvent keyEvent) {
        int keyCode = keyEvent.getKeyCode();
        return keyCode == 24 || keyCode == 25 || keyCode == 27 || keyCode == 80 || keyCode == 164;
    }

    public void onBackPressed() {
    }

    /* JADX WARNING: type inference failed for: r4v0, types: [b.b.c.c.a, android.content.Context, miui.app.Activity, com.miui.warningcenter.WarningCenterAlertActivity, android.app.Activity] */
    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        Window window = getWindow();
        if (Build.VERSION.SDK_INT > 28) {
            s.b().a((s.b) new s.b() {
                /* JADX WARNING: Code restructure failed: missing block: B:6:?, code lost:
                    return;
                 */
                /* Code decompiled incorrectly, please refer to instructions dump. */
                public void onRequestBgResult(android.graphics.drawable.BitmapDrawable r3) {
                    /*
                        r2 = this;
                        com.miui.warningcenter.WarningCenterAlertActivity r0 = com.miui.warningcenter.WarningCenterAlertActivity.this
                        android.view.Window r0 = r0.getWindow()
                        if (r0 == 0) goto L_0x0014
                        if (r3 != 0) goto L_0x0011
                        android.graphics.drawable.ColorDrawable r3 = new android.graphics.drawable.ColorDrawable
                        r1 = -16777216(0xffffffffff000000, float:-1.7014118E38)
                        r3.<init>(r1)
                    L_0x0011:
                        r0.setBackgroundDrawable(r3)
                    L_0x0014:
                        return
                    */
                    throw new UnsupportedOperationException("Method not decompiled: com.miui.warningcenter.WarningCenterAlertActivity.AnonymousClass1.onRequestBgResult(android.graphics.drawable.BitmapDrawable):void");
                }
            });
        } else {
            getWindow().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.ew_alert_bg_color)));
            getWindow().setLayout(-1, -1);
            getWindow().addFlags(4);
        }
        NotificationUtil.setMaxVolume(this);
        window.addFlags(6815873);
        setContentView(R.layout.warning_center_activity_alert);
        if (Build.VERSION.SDK_INT != 26) {
            setRequestedOrientation(1);
        }
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            MijiaAlertModel mijiaAlertModel = (MijiaAlertModel) extras.getSerializable("MijiaAlertModel");
            if (mijiaAlertModel == null) {
                finish();
                return;
            }
            this.mPlaySound = new MijiaPlaySound(this);
            this.mPlaySound.playSound(R.raw.mijia_push_alert_p0);
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(Constants.System.ACTION_SCREEN_OFF);
            registerReceiver(this.mReceiver, intentFilter);
            this.mListView = (RecyclerView) findViewById(R.id.list_view);
            ArrayList arrayList = new ArrayList();
            this.mAdapter = new WarningCenterAlertRecyclerAdapter(this);
            this.mAdapter.setDatas(arrayList);
            this.mAdapter.setListener(new WarningCenterAlertRecyclerAdapter.Listener() {
                public void onItemClick(int i, MijiaAlertModel mijiaAlertModel) {
                    try {
                        WarningCenterAlertActivity.this.startActivity(new Intent("android.intent.action.VIEW", Uri.parse(mijiaAlertModel.getUrl())));
                        AnalyticHelper.trackMijiaModuleClick(AnalyticHelper.MIJIA_ALERT_JUMP_MIJIA);
                        WarningCenterAlertActivity.this.finish();
                    } catch (Exception e) {
                        Log.e(WarningCenterAlertActivity.TAG, "navigate to mijia error", e);
                    }
                }
            });
            this.mLayoutManager = new LinearLayoutManager(this);
            this.mLayoutManager.j(1);
            this.mListView.setLayoutManager(this.mLayoutManager);
            this.mListView.setAdapter(this.mAdapter);
            findViewById(R.id.warning_close).setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    AnalyticHelper.trackMijiaModuleClick(AnalyticHelper.MIJIA_ALERT_CLOSE);
                    WarningCenterAlertActivity.this.finish();
                }
            });
            this.mAdapter.addData(mijiaAlertModel);
            this.mLayoutManager.h(0);
            AnalyticHelper.trackMijiaModuleClick(AnalyticHelper.MIJIA_ALERT_RECEIVE);
            startCountDown();
            return;
        }
        finish();
    }

    /* JADX WARNING: type inference failed for: r4v0, types: [android.content.Context, miui.app.Activity, com.miui.warningcenter.WarningCenterAlertActivity] */
    /* access modifiers changed from: protected */
    public void onDestroy() {
        WarningCenterAlertActivity.super.onDestroy();
        MijiaPlaySound mijiaPlaySound = this.mPlaySound;
        if (mijiaPlaySound != null) {
            mijiaPlaySound.stop();
        }
        MijiaPlaySound mijiaPlaySound2 = this.mPlaySound;
        if (mijiaPlaySound2 != null) {
            mijiaPlaySound2.release();
        }
        CountDownTimer countDownTimer = this.mCountDownTimer;
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        unregisterReceiver(this.mReceiver);
        Settings.Secure.putInt(getContentResolver(), SCREEN_BUTTONS_STATE, 0);
        enableStatusBar(this, true);
        if (this.isPreviousGestureNav) {
            Settings.Global.putInt(getContentResolver(), FORCE_FSG_NAV_BAR, 1);
        }
        this.isPreviousGestureNav = false;
        NotificationUtil.resetVolume(this);
    }

    /* access modifiers changed from: protected */
    public void onNewIntent(Intent intent) {
        MijiaAlertModel mijiaAlertModel;
        WarningCenterAlertActivity.super.onNewIntent(intent);
        Bundle extras = intent.getExtras();
        if (extras != null && (mijiaAlertModel = (MijiaAlertModel) extras.get("MijiaAlertModel")) != null) {
            this.mAdapter.addData(mijiaAlertModel);
            this.mLayoutManager.h(0);
            AnalyticHelper.trackMijiaModuleClick(AnalyticHelper.MIJIA_ALERT_RECEIVE);
        }
    }

    /* JADX WARNING: type inference failed for: r3v0, types: [b.b.c.c.a, android.content.Context, miui.app.Activity, com.miui.warningcenter.WarningCenterAlertActivity] */
    /* access modifiers changed from: protected */
    public void onResume() {
        super.onResume();
        enableStatusBar(this, false);
        if (this.isPreviousGestureNav) {
            Settings.Global.putInt(getContentResolver(), FORCE_FSG_NAV_BAR, 0);
        }
        Settings.Secure.putInt(getContentResolver(), SCREEN_BUTTONS_STATE, 1);
    }

    /* JADX WARNING: type inference failed for: r3v0, types: [android.content.Context, miui.app.Activity, com.miui.warningcenter.WarningCenterAlertActivity] */
    /* access modifiers changed from: protected */
    public void onStop() {
        WarningCenterAlertActivity.super.onStop();
        Settings.Secure.putInt(getContentResolver(), SCREEN_BUTTONS_STATE, 0);
        enableStatusBar(this, true);
        if (this.isPreviousGestureNav) {
            Settings.Global.putInt(getContentResolver(), FORCE_FSG_NAV_BAR, 1);
        }
    }
}
