package com.miui.superpower;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.widget.ImageView;
import com.miui.securitycenter.R;

class c extends Handler {

    /* renamed from: a  reason: collision with root package name */
    int f8095a = 0;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ SuperPowerProgressActivity f8096b;

    c(SuperPowerProgressActivity superPowerProgressActivity) {
        this.f8096b = superPowerProgressActivity;
    }

    /* JADX WARNING: type inference failed for: r6v26, types: [android.content.Context, com.miui.superpower.SuperPowerProgressActivity] */
    public void handleMessage(Message message) {
        ImageView imageView;
        int i = message.what;
        if (i != -1) {
            if (i == 1) {
                this.f8096b.g.setTextColor(this.f8096b.getResources().getColor(R.color.superpower_progress_loadingdone_text_color));
                this.f8096b.f8047c.clearAnimation();
                imageView = this.f8096b.f8047c;
            } else if (i == 2) {
                this.f8096b.h.setTextColor(this.f8096b.getResources().getColor(R.color.superpower_progress_loadingdone_text_color));
                this.f8096b.f8048d.clearAnimation();
                imageView = this.f8096b.f8048d;
            } else if (i != 3) {
                if (i == 4) {
                    this.f8096b.j.clearAnimation();
                    Intent intent = new Intent("android.intent.action.MAIN");
                    intent.setFlags(268435456);
                    intent.addCategory("android.intent.category.HOME");
                    this.f8096b.startActivity(intent);
                    this.f8096b.finish();
                    return;
                }
                return;
            } else if (!a.b((Context) this.f8096b)) {
                this.f8095a = 3;
                return;
            } else {
                this.f8096b.i.setTextColor(this.f8096b.getResources().getColor(R.color.superpower_progress_loadingdone_text_color));
                this.f8096b.e.clearAnimation();
                imageView = this.f8096b.e;
            }
            imageView.setImageResource(R.drawable.superpower_ic_loading_done);
            return;
        }
        this.f8096b.f.setTextColor(this.f8096b.getResources().getColor(R.color.superpower_progress_loadingdone_text_color));
        this.f8096b.f8046b.clearAnimation();
        this.f8096b.f8046b.setImageResource(R.drawable.superpower_ic_loading_done);
        Message obtain = Message.obtain();
        obtain.what = this.f8095a;
        this.f8096b.l.sendMessage(obtain);
        this.f8095a++;
        this.f8096b.l.sendEmptyMessageDelayed(-1, 1250);
    }
}
