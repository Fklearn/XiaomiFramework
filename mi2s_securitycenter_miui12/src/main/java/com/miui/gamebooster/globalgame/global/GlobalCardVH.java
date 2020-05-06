package com.miui.gamebooster.globalgame.global;

import android.content.Context;
import android.support.annotation.Keep;
import android.view.View;
import com.miui.securitycenter.R;

@Keep
public class GlobalCardVH {
    private int alter;
    public View rootView;
    private int[] verticalPadding = new int[2];

    public static int gapItemVerticalPadding(Context context) {
        return context.getResources().getDimensionPixelOffset(R.dimen.baseVerticalMarin) * 2;
    }

    public void custom(View view, boolean z, boolean z2) {
        this.rootView = view;
        this.alter = view.getContext().getResources().getDimensionPixelOffset(R.dimen.baseVerticalMarin);
        int[] iArr = this.verticalPadding;
        int i = this.alter;
        iArr[0] = i;
        iArr[1] = i;
        if (z || z2) {
            refreshPadding(z, z2);
        }
    }

    public void refreshPadding(boolean z, boolean z2) {
        View view = this.rootView;
        int paddingLeft = view.getPaddingLeft();
        int i = 0;
        int i2 = this.verticalPadding[0] + (z ? this.alter : 0);
        int paddingRight = this.rootView.getPaddingRight();
        int i3 = this.verticalPadding[1];
        if (z2) {
            i = this.alter;
        }
        view.setPadding(paddingLeft, i2, paddingRight, i3 + i);
    }
}
