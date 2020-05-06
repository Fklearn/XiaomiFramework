package com.miui.gamebooster.globalgame.view;

import android.content.Context;
import android.support.annotation.Keep;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import com.market.sdk.utils.b;
import com.miui.gamebooster.globalgame.module.BannerCardBean;
import com.miui.gamebooster.globalgame.module.GameListItem;
import com.miui.securitycenter.R;
import java.util.List;

@Keep
public class GameItemHub extends FrameLayout {
    private static int[] ITEM_ID_LIST = {R.id.gameItemView00, R.id.gameItemView01, R.id.gameItemView02, R.id.gameItemView03};
    GameItemView[] itemArr;

    public GameItemHub(Context context) {
        this(context, (AttributeSet) null);
    }

    public GameItemHub(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public GameItemHub(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        LayoutInflater.from(context).inflate(R.layout.gbg_card_game_list_horizontal_hub_view, this, true);
        this.itemArr = new GameItemView[4];
        for (int i2 = 0; i2 < 4; i2++) {
            this.itemArr[i2] = (GameItemView) findViewById(ITEM_ID_LIST[i2]);
        }
    }

    public void update(Context context, View view, BannerCardBean bannerCardBean, List<GameListItem> list) {
        if (!b.a(list)) {
            int min = Math.min(list.size(), this.itemArr.length);
            for (int i = 0; i < min; i++) {
                this.itemArr[i].update(bannerCardBean, list.get(i), bannerCardBean.getButtonText());
            }
        }
    }

    public void vanishDetail() {
        for (GameItemView vanishDetail : this.itemArr) {
            vanishDetail.vanishDetail();
        }
    }
}
