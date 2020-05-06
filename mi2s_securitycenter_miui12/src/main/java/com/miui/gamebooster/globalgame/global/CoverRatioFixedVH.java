package com.miui.gamebooster.globalgame.global;

import android.support.annotation.Keep;
import android.view.View;
import android.widget.ImageView;
import com.miui.gamebooster.globalgame.util.Utils;
import com.miui.gamebooster.m.na;
import com.miui.securitycenter.R;

@Keep
public abstract class CoverRatioFixedVH extends GlobalCardVH {
    public ImageView cover;
    /* access modifiers changed from: private */
    public boolean coverHeightAdjusted;
    private float storedHeight = 0.0f;

    public void custom(View view, boolean z, boolean z2) {
        super.custom(view, z, z2);
        this.cover = (ImageView) view.findViewById(R.id.cover);
        if (this.cover != null) {
            if (this.storedHeight == 0.0f) {
                this.storedHeight = Utils.a(keyForStore());
            }
            if (this.storedHeight != 0.0f) {
                this.cover.getLayoutParams().height = (int) this.storedHeight;
                this.cover.requestLayout();
            } else if (!this.coverHeightAdjusted) {
                Utils.a((View) this.cover, (Runnable) new a(this, na.b()));
            }
        }
    }

    /* access modifiers changed from: protected */
    public abstract String keyForStore();

    /* access modifiers changed from: protected */
    public abstract float parseRatio();
}
