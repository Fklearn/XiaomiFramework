package com.miui.networkassistant.ui.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.os.Bundle;
import com.miui.networkassistant.ui.dialog.CommonDialog;
import com.miui.networkassistant.utils.DeviceUtil;
import com.miui.securitycenter.R;

public class NetworkStatsExceptionAlertActivity extends Activity {
    private static final String REBOOT_REASON = "network stats exception,reboot by security center";
    private CommonDialog mCommonDialog;
    private DialogInterface.OnClickListener mOnClickListener = new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialogInterface, int i) {
            if (i == -1) {
                DeviceUtil.rebootPhone(NetworkStatsExceptionAlertActivity.this.getApplicationContext(), NetworkStatsExceptionAlertActivity.REBOOT_REASON);
            }
            NetworkStatsExceptionAlertActivity.this.finish();
        }
    };

    private void buildAlertDialog() {
        this.mCommonDialog = new CommonDialog(this, this.mOnClickListener);
        String string = getString(R.string.exception_titile);
        String string2 = getString(R.string.exception_dialog_message);
        this.mCommonDialog.setPostiveText(getString(R.string.exception_dialog_ok_button));
        this.mCommonDialog.setTitle(string);
        this.mCommonDialog.setMessage(string2);
        this.mCommonDialog.show();
    }

    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        CommonDialog commonDialog = this.mCommonDialog;
        if (commonDialog != null) {
            commonDialog.dismiss();
        }
        buildAlertDialog();
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        buildAlertDialog();
    }
}
