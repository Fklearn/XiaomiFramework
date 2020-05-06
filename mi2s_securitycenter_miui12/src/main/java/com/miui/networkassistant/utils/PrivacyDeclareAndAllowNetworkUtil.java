package com.miui.networkassistant.utils;

import android.app.Activity;
import android.content.DialogInterface;
import com.miui.securitycenter.R;
import com.miui.securitycenter.h;
import java.lang.ref.WeakReference;
import miui.app.AlertDialog;
import miui.os.Build;

public class PrivacyDeclareAndAllowNetworkUtil {

    private static class AllowNetworkDialogListener implements DialogInterface.OnClickListener {
        private WeakReference<Activity> mActivityRef;

        public AllowNetworkDialogListener(Activity activity) {
            this.mActivityRef = new WeakReference<>(activity);
        }

        public void onClick(DialogInterface dialogInterface, int i) {
            if (i == -1) {
                h.b(true);
            } else if (this.mActivityRef.get() != null) {
                ((Activity) this.mActivityRef.get()).finish();
            }
        }
    }

    public static boolean isAllowNetwork() {
        return h.i();
    }

    private static void showAllowNetworkDialog(Activity activity) {
        AllowNetworkDialogListener allowNetworkDialogListener = new AllowNetworkDialogListener(activity);
        new AlertDialog.Builder(activity).setTitle(R.string.sc_allow_network_dialog_tiltle).setMessage(R.string.sc_allow_network_dialog_message).setPositiveButton(17039370, allowNetworkDialogListener).setNegativeButton(17039360, allowNetworkDialogListener).setCancelable(false).create().show();
    }

    public static void showSecurityCenterAllowNetwork(Activity activity) {
        if (!Build.IS_INTERNATIONAL_BUILD && !isAllowNetwork()) {
            showAllowNetworkDialog(activity);
        }
    }
}
