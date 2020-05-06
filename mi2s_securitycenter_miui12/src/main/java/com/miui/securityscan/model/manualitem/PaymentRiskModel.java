package com.miui.securityscan.model.manualitem;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import b.b.b.d.m;
import b.b.b.p;
import com.miui.securitycenter.R;
import com.miui.securityscan.model.AbsModel;
import com.miui.securityscan.scanner.C0569p;
import com.miui.securityscan.scanner.C0570q;
import java.util.Map;

public class PaymentRiskModel extends AbsModel {
    private static final String ACTION_SECURITY_SCAN = "miui.intent.action.ANTI_VIRUS";

    public PaymentRiskModel(String str, Integer num) {
        super(str, num);
        setDelayOptimized(true);
        setTrackStr("payment_risk");
    }

    public String getDesc() {
        return null;
    }

    public int getIndex() {
        return 34;
    }

    public String getSummary() {
        return getContext().getString(R.string.summary_payment_risk);
    }

    public String getTitle() {
        return getContext().getString(R.string.title_payment_risk);
    }

    public void optimize(Context context) {
        if (context instanceof Activity) {
            ((Activity) context).startActivityForResult(new Intent(ACTION_SECURITY_SCAN), 100);
        }
    }

    public void scan() {
        setSafe(p.d() == m.SAFE ? AbsModel.State.SAFE : AbsModel.State.DANGER);
        if (isSafe() == AbsModel.State.SAFE) {
            C0570q.b().a(C0570q.a.SECURITY, getItemKey(), new C0569p(getContext().getString(R.string.title_payment_protection), false));
            return;
        }
        Map<String, C0569p> a2 = C0570q.b().a(C0570q.a.SECURITY);
        if (a2.containsKey(getItemKey())) {
            a2.remove(getItemKey());
        }
    }
}
