package com.miui.securityscan.model.system;

import android.content.Context;
import android.util.Log;
import com.miui.securitycenter.R;
import com.miui.securityscan.model.AbsModel;
import com.miui.securityscan.scanner.C0569p;
import com.miui.securityscan.scanner.C0570q;
import com.miui.securityscan.scanner.O;
import com.miui.securityscan.scanner.ScoreManager;

public class VirusScanModel extends AbsModel {
    public static final String KEY_DEFAULT = "VIRUS";
    private static final String TAG = "VirusScanModel";
    private ScoreManager scoreManager = ScoreManager.e();

    public VirusScanModel(String str, Integer num) {
        super(str, num);
    }

    public String getDesc() {
        return getContext().getString(R.string.menu_text_antivirus);
    }

    public int getIndex() {
        return 0;
    }

    public String getSummary() {
        if (isSafe() != AbsModel.State.SAFE) {
            return getContext().getString(R.string.summary_virus);
        }
        return null;
    }

    public String getTitle() {
        return getContext().getString(isSafe() != AbsModel.State.SAFE ? R.string.title_virus_no : R.string.title_virus_yes);
    }

    public void optimize(Context context) {
        Log.d(TAG, "optimize start ");
        O.a(getContext()).a(this.scoreManager.n());
        setSafe(AbsModel.State.SAFE);
    }

    public void scan() {
        setSafe(this.scoreManager.s() ? AbsModel.State.SAFE : AbsModel.State.DANGER);
        Log.d(TAG, "isSafe = " + isSafe());
        if (isSafe() == AbsModel.State.SAFE) {
            C0570q.b().a(C0570q.a.SYSTEM, getItemKey(), new C0569p(getContext().getString(R.string.title_virus_yes), false));
        }
    }

    public void updateModelState(AbsModel.State state) {
        setSafe(state);
    }
}
