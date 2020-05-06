package com.miui.gamebooster.n.d;

import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import com.miui.gamebooster.d;
import com.miui.gamebooster.videobox.adapter.f;

class e implements d {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ f.c f4693a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ g f4694b;

    e(g gVar, f.c cVar) {
        this.f4694b = gVar;
        this.f4693a = cVar;
    }

    public void a() {
        Log.i("VBFunction", "disConnect: " + this.f4693a.f5158b);
        ImageView imageView = this.f4693a.f5158b;
        if (imageView != null) {
            imageView.setSelected(false);
        }
        TextView textView = this.f4693a.f5160d;
        if (textView != null) {
            textView.setSelected(false);
        }
    }

    public void b() {
        Log.i("VBFunction", "connectSuccess: " + this.f4693a.f5158b);
        ImageView imageView = this.f4693a.f5158b;
        if (imageView != null) {
            imageView.setSelected(true);
        }
        TextView textView = this.f4693a.f5160d;
        if (textView != null) {
            textView.setSelected(true);
        }
    }
}
