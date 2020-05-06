package com.miui.powercenter.utils;

import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;
import b.c.a.b.a.b;
import b.c.a.b.f.d;
import miui.content.res.IconCustomizer;

class a extends d {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ ImageView f7295a;

    a(ImageView imageView) {
        this.f7295a = imageView;
    }

    public void a(String str, View view, Bitmap bitmap) {
        if (str.equals(this.f7295a.getTag())) {
            this.f7295a.setImageBitmap(bitmap);
        }
    }

    public void a(String str, View view, b bVar) {
        if (str.equals(this.f7295a.getTag())) {
            this.f7295a.setImageDrawable(IconCustomizer.generateIconStyleDrawable(this.f7295a.getContext().getPackageManager().getDefaultActivityIcon()));
        }
    }
}
