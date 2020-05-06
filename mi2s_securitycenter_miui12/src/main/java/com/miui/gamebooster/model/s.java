package com.miui.gamebooster.model;

import android.content.Context;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;
import com.miui.gamebooster.a.C0328f;
import com.miui.gamebooster.a.I;
import com.miui.securitycenter.R;

public class s extends C0399e {

    /* renamed from: d  reason: collision with root package name */
    private String f4586d;
    private boolean e;

    public class a extends C0328f {

        /* renamed from: a  reason: collision with root package name */
        private Context f4587a;

        /* renamed from: b  reason: collision with root package name */
        private TextView f4588b;

        /* renamed from: c  reason: collision with root package name */
        private CheckBox f4589c;

        public a(View view) {
            super(view);
            this.f4587a = view.getContext();
            a(view);
        }

        public void a(View view) {
            this.f4588b = (TextView) view.findViewById(R.id.tv_title);
            this.f4589c = (CheckBox) view.findViewById(R.id.tv_checkall);
        }

        public void a(View view, int i, Object obj, I.a aVar) {
            s sVar = (s) obj;
            this.f4588b.setText(sVar.e());
            this.f4589c.setBackgroundResource(sVar.f() ? R.drawable.shape_gb_wonderful_video_check_all_selected : R.drawable.shape_gb_wonderful_video_check_all_nomal);
            this.f4589c.setTextColor(this.f4587a.getResources().getColor(sVar.f() ? R.color.gb_wonderful_video_item_all_checked : R.color.gb_wonderful_video_item_no_all_checked));
            this.f4589c.setVisibility(sVar.d() ? 0 : 8);
            this.f4589c.setOnClickListener(new r(this, sVar, aVar, i));
        }
    }

    public s() {
        super(R.layout.gb_wonderful_moment_video_list_header);
    }

    public C0328f a(View view) {
        return new a(view);
    }

    public void a(String str) {
        this.f4586d = str;
    }

    public void b(boolean z) {
        this.e = z;
    }

    public String e() {
        return this.f4586d;
    }

    public boolean f() {
        return this.e;
    }
}
