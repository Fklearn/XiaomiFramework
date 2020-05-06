package com.miui.securityscan.model.manualitem;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import b.b.c.j.A;
import b.b.c.j.x;
import com.miui.cleanmaster.f;
import com.miui.cleanmaster.g;
import com.miui.securitycenter.R;
import com.miui.securityscan.model.AbsModel;
import com.miui.securityscan.scanner.C0569p;
import com.miui.securityscan.scanner.C0570q;
import com.miui.securityscan.scanner.ScoreManager;
import java.util.Map;
import miui.text.ExtraTextUtils;

public class GarbageCleanModel extends AbsModel {
    public static final long CLEAN_VALUE = 100000000;
    public static final long MB = 1000000;
    private ScoreManager mScoreManager = ScoreManager.e();

    public GarbageCleanModel(String str, Integer num) {
        super(str, num);
        setDelayOptimized(true);
        setTrackStr("garbage_clean");
    }

    public String getDesc() {
        return null;
    }

    public int getIndex() {
        return 38;
    }

    public String getSummary() {
        return getContext().getString(R.string.summary_garbage_clean);
    }

    public String getTitle() {
        return getContext().getString(R.string.title_garbage_clean, new Object[]{ExtraTextUtils.formatShortFileSize(getContext(), this.mScoreManager.a())});
    }

    public void optimize(Context context) {
        Intent intent = new Intent("miui.intent.action.GARBAGE_CLEANUP");
        intent.putExtra("enter_homepage_way", "00006");
        intent.putExtra("force_clean", true);
        if (f.a(context.getApplicationContext())) {
            if (!x.a(context, intent, 103)) {
                A.a(context, (int) R.string.app_not_installed_toast);
            }
        } else if (context instanceof Activity) {
            g.b((Activity) context, intent, 103, (Bundle) null);
        }
    }

    public void scan() {
        setSafe(this.mScoreManager.a() > CLEAN_VALUE ? AbsModel.State.DANGER : AbsModel.State.SAFE);
        if (isSafe() == AbsModel.State.SAFE) {
            C0570q.b().a(C0570q.a.CLEANUP, getItemKey(), new C0569p(getContext().getString(R.string.title_garbage_less), false));
            return;
        }
        Map<String, C0569p> a2 = C0570q.b().a(C0570q.a.CLEANUP);
        if (a2.containsKey(getItemKey())) {
            a2.remove(getItemKey());
        }
    }
}
