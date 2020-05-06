package com.miui.common.card.functions;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import b.b.c.j.A;
import b.b.c.j.x;
import com.miui.cleanmaster.g;
import com.miui.securitycenter.R;
import com.miui.securityscan.c.e;
import java.util.ArrayList;
import java.util.List;

public class CommonFunction extends BaseFunction {
    private static List<String> ACTION_WHITE_LIST = new ArrayList();
    private static final String TAG = "CommonFunction";
    private Intent intent;

    static {
        ACTION_WHITE_LIST.add("miui.intent.action.GARBAGE_CLEANUP");
        ACTION_WHITE_LIST.add("miui.intent.action.GARBAGE_DEEPCLEAN");
        ACTION_WHITE_LIST.add("miui.intent.action.GARBAGE_DEEPCLEAN_WECHAT");
    }

    public CommonFunction(Intent intent2) {
        this.intent = intent2;
    }

    public void onClick(View view) {
        if (this.intent != null) {
            Context context = view.getContext();
            e.a(context, "data_config").c("is_homepage_operated", true);
            this.intent.putExtra("enter_homepage_way", "00004");
            this.intent.putExtra("track_gamebooster_enter_way", "00001");
            String action = this.intent.getAction();
            if ("com.miui.securitycenter.action.TRANSITION".equals(action)) {
                this.intent.putExtra("enter_way", "00001");
            } else if ("#Intent;action=miui.intent.action.APP_MANAGER;end".equals(action)) {
                this.intent.putExtra("enter_way", "com.miui.securitycenter");
            }
            if (ACTION_WHITE_LIST.contains(action)) {
                g.b(context, this.intent);
            } else if (!x.c(context, this.intent)) {
                A.a(context, (int) R.string.app_not_installed_toast);
            }
        }
    }
}
