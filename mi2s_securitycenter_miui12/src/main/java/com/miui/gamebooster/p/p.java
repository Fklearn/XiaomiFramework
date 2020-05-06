package com.miui.gamebooster.p;

import android.os.CountDownTimer;
import android.widget.Toast;
import com.miui.gamebooster.widget.ProgressCircle;
import com.miui.securitycenter.R;

class p extends CountDownTimer {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ q f4734a;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    p(q qVar, long j, long j2) {
        super(j, j2);
        this.f4734a = qVar;
    }

    public void onFinish() {
        CountDownTimer unused = this.f4734a.f4736b.B = null;
        this.f4734a.f4735a.setVisibility(8);
        Toast.makeText(this.f4734a.f4736b.f4740d, this.f4734a.f4736b.f4740d.getString(R.string.gb_game_video_record_finish_tips), 0).show();
    }

    public void onTick(long j) {
        boolean z = false;
        this.f4734a.f4735a.setVisibility(0);
        ProgressCircle progressCircle = this.f4734a.f4735a;
        int i = (int) j;
        if (j / 32 <= 31) {
            z = true;
        }
        progressCircle.a(i, z);
    }
}
