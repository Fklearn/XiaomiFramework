package com.miui.securityscan.i;

import android.content.Context;
import android.content.res.Resources;
import com.miui.securitycenter.R;
import com.miui.securityscan.scanner.C0568o;
import com.miui.securityscan.scanner.ScoreManager;
import miui.text.ExtraTextUtils;

public class o {
    public static String a(Context context) {
        if (context == null) {
            return "";
        }
        int j = ScoreManager.e().j();
        return context.getString(j == 100 ? R.string.action_button_text_100 : j >= 75 ? R.string.action_button_text_80_100 : R.string.action_button_text_60);
    }

    public static String a(Context context, C0568o oVar) {
        if (context == null) {
            return "";
        }
        ScoreManager e = ScoreManager.e();
        int i = n.f7738a[oVar.ordinal()];
        if (i == 1) {
            Resources resources = context.getResources();
            int size = (int) ((long) e.l().size());
            return resources.getQuantityString(R.plurals.system_check_content, size, new Object[]{Integer.valueOf(size)});
        } else if (i == 2) {
            return context.getString(R.string.memory_check_content, new Object[]{ExtraTextUtils.formatShortFileSize(context, e.c())});
        } else if (i != 3) {
            return "";
        } else {
            int m = e.m();
            return context.getResources().getQuantityString(R.plurals.cache_check_content, m, new Object[]{Integer.valueOf(m)});
        }
    }

    public static String b(Context context) {
        int i;
        if (context == null) {
            return "";
        }
        ScoreManager e = ScoreManager.e();
        if (e.r()) {
            i = R.string.examination_score_has_virus;
        } else {
            int j = e.j();
            int size = e.g().size();
            i = j == 100 ? R.string.examination_score_100 : j >= 80 ? R.string.examination_score_80_100 : j >= 60 ? R.string.examination_score_80_60 : R.string.examination_score_60_0;
        }
        return context.getString(i);
    }
}
