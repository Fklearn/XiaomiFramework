package com.miui.networkassistant.ui.activity;

import android.app.Activity;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import com.miui.networkassistant.config.SimUserInfo;
import com.miui.networkassistant.dual.Sim;
import com.miui.networkassistant.model.TrafficUsedStatus;
import com.miui.networkassistant.service.ITrafficManageBinder;
import com.miui.networkassistant.service.wrapper.TmBinderCacher;
import com.miui.networkassistant.ui.dialog.CommonDialog;
import com.miui.networkassistant.utils.AnalyticsHelper;
import com.miui.networkassistant.utils.DeviceUtil;
import com.miui.networkassistant.utils.NotificationUtil;
import com.miui.securitycenter.R;

public class TrafficConfigAlertActivity extends Activity {
    public static final String BUNDLE_KEY_BODY = "bundle_key_body";
    public static final String BUNDLE_KEY_IMSI = "bundle_key_imsi";
    public static final String BUNDLE_KEY_IS_STABLE_PKG = "bundle_key_is_stable_pkg";
    public static final String BUNDLE_KEY_TRAFFIC_USED_STATUS = "bundle_key_traffic_used_status";
    private ServiceConnection mConn = new ServiceConnection() {
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            TrafficConfigAlertActivity trafficConfigAlertActivity = TrafficConfigAlertActivity.this;
            trafficConfigAlertActivity.mConnected = true;
            trafficConfigAlertActivity.mTrafficManageBinder = ITrafficManageBinder.Stub.asInterface(iBinder);
        }

        public void onServiceDisconnected(ComponentName componentName) {
            TrafficConfigAlertActivity trafficConfigAlertActivity = TrafficConfigAlertActivity.this;
            trafficConfigAlertActivity.mTrafficManageBinder = null;
            trafficConfigAlertActivity.mConnected = false;
        }
    };
    protected boolean mConnected;
    private CommonDialog mDialog;
    private CompoundButton.OnCheckedChangeListener mOnCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
        public void onCheckedChanged(CompoundButton compoundButton, boolean z) {
            TrafficConfigAlertActivity.this.mSimUserInfo.saveTrafficCorrectionAutoModify(z);
        }
    };
    private DialogInterface.OnClickListener mOnClickListener = new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialogInterface, int i) {
            if (i == -1) {
                boolean unused = TrafficConfigAlertActivity.this.mShouldSavingPkg = true;
                TrafficConfigAlertActivity.this.mSimUserInfo.setPackageChangeUpdateTime(0);
            } else if (i == -2) {
                TrafficConfigAlertActivity.this.mSimUserInfo.saveTrafficCorrectionAutoModify(false);
            }
            TrafficConfigAlertActivity.this.applyDataAndFinish();
            TrafficConfigAlertActivity.this.finish();
        }
    };
    /* access modifiers changed from: private */
    public boolean mShouldSavingPkg = false;
    /* access modifiers changed from: private */
    public SimUserInfo mSimUserInfo;
    protected int mSlotNum = 0;
    private boolean mStablePkgGet = false;
    protected ITrafficManageBinder mTrafficManageBinder;

    /* access modifiers changed from: private */
    public void applyDataAndFinish() {
        ITrafficManageBinder iTrafficManageBinder;
        Intent intent = getIntent();
        if (intent != null) {
            TrafficUsedStatus trafficUsedStatus = (TrafficUsedStatus) intent.getParcelableExtra(BUNDLE_KEY_TRAFFIC_USED_STATUS);
            if (this.mConnected && (iTrafficManageBinder = this.mTrafficManageBinder) != null) {
                try {
                    iTrafficManageBinder.applyCorrectedPkgsAndUsageValues(trafficUsedStatus, this.mShouldSavingPkg, this.mSlotNum);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }
        finish();
    }

    private void bindTrafficManageService() {
        TmBinderCacher.getInstance().bindTmService(this.mConn);
    }

    private void buildAlertDialog(String str, String str2) {
        View inflate = LayoutInflater.from(this).inflate(R.layout.dialog_traffic_config_alert, (ViewGroup) null);
        ((CheckBox) inflate.findViewById(R.id.checkbox_auto_modify)).setOnCheckedChangeListener(this.mOnCheckedChangeListener);
        this.mDialog.setTitle(str);
        this.mDialog.setMessage(str2);
        this.mDialog.setView(inflate);
        this.mDialog.show();
    }

    private void onPackageErrorAlert(Intent intent, String str) {
        buildAlertDialog(String.format(getString(R.string.traffic_limit_error_alert_title), new Object[]{str}), String.format("%s%s", new Object[]{intent.getStringExtra(BUNDLE_KEY_BODY), getString(R.string.traffic_limit_error_alert_package)}));
    }

    private void onStablePackageGet(Intent intent, String str) {
        buildAlertDialog(String.format(getString(R.string.traffic_config_alert_title), new Object[]{str}), String.format("%s%s", new Object[]{String.format(getString(R.string.traffic_config_alert_body), new Object[]{intent.getStringExtra(BUNDLE_KEY_BODY)}), getString(R.string.traffic_config_alert_package)}));
    }

    private void unbindTrafficManageService() {
        TmBinderCacher.getInstance().unbindTmService(this.mConn);
    }

    public void onCreate(Bundle bundle) {
        String str;
        super.onCreate(bundle);
        Intent intent = getIntent();
        if (intent == null) {
            finish();
            return;
        }
        this.mDialog = new CommonDialog(this, this.mOnClickListener);
        String stringExtra = intent.getStringExtra(BUNDLE_KEY_IMSI);
        if (!TextUtils.isEmpty(stringExtra)) {
            this.mSimUserInfo = SimUserInfo.getInstance(getApplicationContext(), stringExtra);
            bindTrafficManageService();
            if (DeviceUtil.IS_DUAL_CARD) {
                this.mSlotNum = 0;
                this.mSlotNum = intent.hasExtra(Sim.SIM_SLOT_NUM_TAG) ? intent.getIntExtra(Sim.SIM_SLOT_NUM_TAG, 0) : Sim.getCurrentActiveSlotNum();
                str = getString(this.mSlotNum == 0 ? R.string.dual_setting_simcard1 : R.string.dual_setting_simcard2);
            } else {
                str = "";
            }
            this.mStablePkgGet = intent.getBooleanExtra(BUNDLE_KEY_IS_STABLE_PKG, false);
            if (this.mStablePkgGet) {
                onStablePackageGet(intent, str);
            } else {
                onPackageErrorAlert(intent, str);
            }
            AnalyticsHelper.trackActiveNetworkAssistant(getApplicationContext());
            NotificationUtil.cancelNotification(getApplicationContext(), this.mSlotNum + 48);
            return;
        }
        finish();
    }

    /* access modifiers changed from: protected */
    public void onDestroy() {
        super.onDestroy();
        unbindTrafficManageService();
    }
}
