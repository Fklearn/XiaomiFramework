package com.android.server.am;

import android.content.Context;
import android.os.ServiceManager;

public class MiuiWarnings {
    private Context mContext;
    private ActivityManagerService mService;

    public interface WarningCallback {
        void onCallback(boolean z);
    }

    private static class NoPreloadHolder {
        /* access modifiers changed from: private */
        public static final MiuiWarnings INSTANCE = new MiuiWarnings();

        private NoPreloadHolder() {
        }
    }

    private MiuiWarnings() {
    }

    public static MiuiWarnings getInstance() {
        return NoPreloadHolder.INSTANCE;
    }

    public void init(Context context) {
        this.mContext = context;
    }

    /* JADX WARNING: type inference failed for: r0v4, types: [android.app.Dialog, com.android.server.am.MiuiWarningDialog] */
    public boolean showWarningDialog(String packageLabel, WarningCallback callback) {
        checkService();
        if (!this.mService.mAtmInternal.canShowErrorDialogs()) {
            return false;
        }
        new MiuiWarningDialog(packageLabel, this.mContext, callback).show();
        return true;
    }

    private void checkService() {
        if (this.mService == null) {
            this.mService = (ActivityManagerService) ServiceManager.getService("activity");
        }
    }
}
