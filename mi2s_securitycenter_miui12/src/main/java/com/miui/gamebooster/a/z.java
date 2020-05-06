package com.miui.gamebooster.a;

import android.view.View;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.miui.gamebooster.view.QRSlidingButton;
import com.miui.securitycenter.R;

class z extends RecyclerView.u {

    /* renamed from: a  reason: collision with root package name */
    QRSlidingButton f4084a;

    /* renamed from: b  reason: collision with root package name */
    TextView f4085b;

    /* renamed from: c  reason: collision with root package name */
    View f4086c;

    public z(View view) {
        super(view);
        this.f4084a = (QRSlidingButton) view.findViewById(R.id.quick_replay_switch);
        this.f4085b = (TextView) view.findViewById(R.id.all_check_title);
        this.f4086c = view.findViewById(R.id.switch_layout);
    }
}
