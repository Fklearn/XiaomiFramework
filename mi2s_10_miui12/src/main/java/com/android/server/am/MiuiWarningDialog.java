package com.android.server.am;

import android.content.Context;
import android.content.res.Resources;
import android.miui.R;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.BidiFormatter;
import android.view.WindowManager;
import android.widget.TextView;
import com.android.server.am.MiuiWarnings;

public class MiuiWarningDialog extends BaseErrorDialog {
    private static final int BUTTON_CANCEL = 2;
    private static final int BUTTON_OK = 1;
    private static final String TAG = "MiuiWarningDialog";
    /* access modifiers changed from: private */
    public MiuiWarnings.WarningCallback mCallback;
    private final Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int i = msg.what;
            if (i == 1) {
                MiuiWarningDialog.this.mCallback.onCallback(true);
            } else if (i == 2) {
                MiuiWarningDialog.this.mCallback.onCallback(false);
            }
        }
    };

    public MiuiWarningDialog(String packageLabel, Context context, MiuiWarnings.WarningCallback callback) {
        super(context);
        this.mCallback = callback;
        Resources res = context.getResources();
        BidiFormatter bidi = BidiFormatter.getInstance();
        setCancelable(false);
        setTitle(res.getString(R.string.miui_warning_tip_info, new Object[]{bidi.unicodeWrap(packageLabel)}));
        setButton(-1, res.getText(R.string.miui_warning_button_ok), this.mHandler.obtainMessage(1));
        setButton(-2, res.getText(R.string.miui_warning_button_cancel), this.mHandler.obtainMessage(2));
        getWindow().setType(2010);
        WindowManager.LayoutParams attrs = getWindow().getAttributes();
        attrs.setTitle("MiuiWarning:" + packageLabel);
        attrs.privateFlags = 272;
        getWindow().setAttributes(attrs);
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((TextView) findViewById(com.miui.internal.R.id.alertTitle)).setSingleLine(false);
    }
}
