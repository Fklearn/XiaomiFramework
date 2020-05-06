package com.miui.gamebooster.a;

import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.miui.securitycenter.R;

class A extends RecyclerView.u {

    /* renamed from: a  reason: collision with root package name */
    ImageView f3996a;

    /* renamed from: b  reason: collision with root package name */
    TextView f3997b;

    /* renamed from: c  reason: collision with root package name */
    CheckBox f3998c;
    View itemView;

    public A(View view) {
        super(view);
        this.f3996a = (ImageView) view.findViewById(R.id.app_icon);
        this.f3997b = (TextView) view.findViewById(R.id.app_name);
        this.f3998c = (CheckBox) view.findViewById(R.id.reply_switch);
        this.itemView = view;
    }
}
