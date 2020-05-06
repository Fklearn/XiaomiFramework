package b.b.j.b;

import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import b.b.c.j.A;
import b.b.c.j.x;
import b.b.j.b.b;
import com.miui.cleanmaster.g;
import com.miui.common.card.functions.FuncTopBannerScrollData;
import com.miui.common.card.models.FunctionCardModel;
import com.miui.securitycenter.R;
import com.miui.securityscan.a.G;

class a implements View.OnClickListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ FuncTopBannerScrollData f1788a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ b.a f1789b;

    a(b.a aVar, FuncTopBannerScrollData funcTopBannerScrollData) {
        this.f1789b = aVar;
        this.f1788a = funcTopBannerScrollData;
    }

    public void onClick(View view) {
        String action = this.f1788a.getAction();
        if (!TextUtils.isEmpty(action)) {
            try {
                Intent parseUri = Intent.parseUri(action, 0);
                parseUri.putExtra("enter_homepage_way", "phone_manage");
                if ("#Intent;action=miui.intent.action.APP_MANAGER;end".equals(action)) {
                    parseUri.putExtra("enter_way", "com.miui.securitycenter");
                }
                if ("#Intent;action=miui.intent.action.KIDMODE_ENTRANCE;end".equals(action)) {
                    parseUri.putExtra("enter_kid_space_channel", "phonemanage_page");
                }
                if (FunctionCardModel.SHOW_ACTION_WHITE_LIST.contains(action)) {
                    g.b(this.f1789b.f1791a, parseUri);
                } else if (!x.c(this.f1789b.f1791a, parseUri)) {
                    A.a(this.f1789b.f1791a, (int) R.string.app_not_installed_toast);
                }
            } catch (Exception e) {
                Log.e("PhoneManageBannerModel", "onClick error:", e);
            }
        }
        String statKey = this.f1788a.getStatKey();
        if (!TextUtils.isEmpty(statKey)) {
            G.t(statKey);
        }
    }
}
