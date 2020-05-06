package com.miui.common.customview.gif;

class e implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ GifImageView f3821a;

    e(GifImageView gifImageView) {
        this.f3821a = gifImageView;
    }

    public void run() {
        if (this.f3821a.f3804d && this.f3821a.f3802b != null && !this.f3821a.f3802b.isRecycled()) {
            GifImageView gifImageView = this.f3821a;
            gifImageView.setImageBitmap(gifImageView.f3802b);
        }
    }
}
