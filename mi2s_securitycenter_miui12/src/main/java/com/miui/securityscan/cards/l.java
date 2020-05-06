package com.miui.securityscan.cards;

import android.graphics.drawable.Drawable;
import com.miui.common.card.FillParentDrawable;
import com.miui.common.card.models.FunctionCardModel;
import com.miui.securitycenter.R;
import java.util.Map;

class l implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ n f7661a;

    l(n nVar) {
        this.f7661a = nVar;
    }

    public void run() {
        Map<String, Integer> map = FunctionCardModel.LOCAL_FUNCTION_ICONS;
        this.f7661a.x.clear();
        for (Map.Entry<String, Integer> value : map.entrySet()) {
            int intValue = ((Integer) value.getValue()).intValue();
            Drawable drawable = this.f7661a.g.getResources().getDrawable(intValue);
            synchronized (this.f7661a.y) {
                this.f7661a.x.put(Integer.valueOf(intValue), drawable);
            }
        }
        Drawable drawable2 = this.f7661a.g.getResources().getDrawable(R.drawable.card_icon_default);
        FillParentDrawable fillParentDrawable = new FillParentDrawable(this.f7661a.g.getResources().getDrawable(R.drawable.big_banner_background_default));
        synchronized (this.f7661a.y) {
            this.f7661a.x.put(Integer.valueOf(R.drawable.card_icon_default), drawable2);
            this.f7661a.x.put(Integer.valueOf(R.drawable.big_banner_background_default), fillParentDrawable);
        }
    }
}
