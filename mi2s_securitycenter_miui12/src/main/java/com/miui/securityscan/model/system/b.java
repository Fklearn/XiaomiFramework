package com.miui.securityscan.model.system;

import android.app.Activity;
import android.content.DialogInterface;
import android.util.Log;
import com.miui.securitycenter.R;
import miui.app.AlertDialog;

class b implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ Activity f7804a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ CmccCheckModel f7805b;

    b(CmccCheckModel cmccCheckModel, Activity activity) {
        this.f7805b = cmccCheckModel;
        this.f7804a = activity;
    }

    public void run() {
        try {
            if (!this.f7804a.isFinishing()) {
                AlertDialog unused = this.f7805b.dialog = new AlertDialog.Builder(this.f7804a).setTitle(R.string.cmcc_app_check_dialog_title).setCancelable(false).setMessage(R.string.cmcc_app_check_dialog_message).setPositiveButton(R.string.cmcc_app_check_dialog_positive, new a(this)).setNegativeButton(R.string.cmcc_app_check_dialog_negative, (DialogInterface.OnClickListener) null).show();
            }
        } catch (Exception e) {
            Log.e("CmccCheckModel", "showRebootDialog error :", e);
        }
    }
}
