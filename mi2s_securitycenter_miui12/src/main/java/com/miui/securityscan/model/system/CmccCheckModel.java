package com.miui.securityscan.model.system;

import android.app.Activity;
import android.content.Context;
import b.b.c.j.y;
import com.miui.activityutil.o;
import com.miui.securitycenter.R;
import com.miui.securitycenter.h;
import com.miui.securitycenter.utils.a;
import com.miui.securityscan.model.AbsModel;
import miui.app.AlertDialog;

public class CmccCheckModel extends AbsModel {
    private static final String TAG = "CmccCheckModel";
    /* access modifiers changed from: private */
    public AlertDialog dialog;

    public CmccCheckModel(String str, Integer num) {
        super(str, num);
        setScanHide(true);
    }

    private void showRebootDialog(Activity activity) {
        if (activity != null) {
            activity.runOnUiThread(new b(this, activity));
        }
    }

    /* access modifiers changed from: protected */
    public void finalize() {
        super.finalize();
        AlertDialog alertDialog = this.dialog;
        if (alertDialog != null && alertDialog.isShowing()) {
            this.dialog.dismiss();
        }
    }

    public String getDesc() {
        return null;
    }

    public int getIndex() {
        return 0;
    }

    public String getSummary() {
        if (isSafe() == AbsModel.State.SAFE) {
            return null;
        }
        return getContext().getString(R.string.summary_cmcc_app_check);
    }

    public String getTitle() {
        if (isSafe() == AbsModel.State.SAFE) {
            return null;
        }
        return getContext().getString(R.string.title_cmcc_app_check);
    }

    public void optimize(Context context) {
        y.b("persist.sys.func_limit_switch", o.f2310b);
        a.a(getContext());
        setSafe(AbsModel.State.SAFE);
        if (context instanceof Activity) {
            showRebootDialog((Activity) context);
        }
    }

    public void scan() {
        setSafe(!(a.e() && a.c() && h.c()) ? AbsModel.State.SAFE : AbsModel.State.DANGER);
    }
}
