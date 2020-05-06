package com.miui.permcenter.permissions;

import android.content.Context;
import android.graphics.drawable.Drawable;
import androidx.preference.PreferenceCategory;

class A extends PreferenceCategory {
    final /* synthetic */ int j;
    final /* synthetic */ int k;
    final /* synthetic */ C l;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    A(C c2, Context context, int i, int i2) {
        super(context);
        this.l = c2;
        this.j = i;
        this.k = i2;
    }

    public void onBindViewHolder(androidx.preference.A a2) {
        super.onBindViewHolder(a2);
        if (a2.getLayoutPosition() == 0) {
            a2.itemView.setBackground((Drawable) null);
            a2.itemView.setPadding(a2.itemView.getPaddingLeft(), this.j, a2.itemView.getPaddingRight(), this.k);
        }
    }
}
