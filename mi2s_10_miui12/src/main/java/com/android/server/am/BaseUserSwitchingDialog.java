package com.android.server.am;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.WindowManager;
import com.android.internal.annotations.GuardedBy;
import miui.app.AlertDialog;

class BaseUserSwitchingDialog extends AlertDialog {
    static final int MSG_START_USER = 1;
    private static final int WINDOW_SHOWN_TIMEOUT_MS = 3000;
    private final Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                BaseUserSwitchingDialog.this.startUser();
            }
        }
    };
    private final ActivityManagerService mService;
    @GuardedBy({"this"})
    private boolean mStartedUser;
    protected final int mUserId;

    public BaseUserSwitchingDialog(ActivityManagerService service, Context context, int styleId, int userId) {
        super(context, styleId);
        this.mService = service;
        this.mUserId = userId;
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        BaseUserSwitchingDialog.super.onCreate(savedInstanceState);
        setCancelable(false);
        getWindow().setType(2010);
        WindowManager.LayoutParams attrs = getWindow().getAttributes();
        attrs.privateFlags = 272;
        getWindow().setAttributes(attrs);
    }

    public void show() {
        BaseUserSwitchingDialog.super.show();
        UserSwitchingDialogInjector.switchUser(this.mHandler, this);
        Handler handler = this.mHandler;
        handler.sendMessageDelayed(handler.obtainMessage(1), 3000);
    }

    /* JADX WARNING: type inference failed for: r2v0, types: [com.android.server.am.BaseUserSwitchingDialog, android.app.Dialog] */
    /* access modifiers changed from: package-private */
    public void startUser() {
        synchronized (this) {
            if (!this.mStartedUser) {
                UserSwitchingDialogInjector.startUserInForeground(this.mService, this.mUserId, this);
                this.mStartedUser = true;
                UserSwitchingDialogInjector.finishSwitchUser(this);
                this.mHandler.removeMessages(1);
            }
        }
    }
}
