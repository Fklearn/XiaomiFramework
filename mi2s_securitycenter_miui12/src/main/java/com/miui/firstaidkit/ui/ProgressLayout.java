package com.miui.firstaidkit.ui;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.miui.firstaidkit.n;
import com.miui.securitycenter.R;
import miui.widget.ProgressBar;

public class ProgressLayout extends RelativeLayout implements View.OnClickListener {

    /* renamed from: a  reason: collision with root package name */
    private Context f3986a;

    /* renamed from: b  reason: collision with root package name */
    private Handler f3987b;

    /* renamed from: c  reason: collision with root package name */
    private ImageView f3988c;

    /* renamed from: d  reason: collision with root package name */
    private ImageView f3989d;
    private ImageView e;
    private ImageView f;
    private ImageView g;
    private ProgressBar h;
    private ProgressBar i;
    private ProgressBar j;
    private ProgressBar k;
    private ProgressBar l;

    public ProgressLayout(Context context) {
        this(context, (AttributeSet) null);
    }

    public ProgressLayout(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public ProgressLayout(Context context, AttributeSet attributeSet, int i2) {
        super(context, attributeSet, i2);
        this.f3986a = context;
    }

    public void a() {
        this.f3988c.setVisibility(8);
        this.f3989d.setVisibility(8);
        this.e.setVisibility(8);
        this.f.setVisibility(8);
        this.g.setVisibility(8);
        this.h.setVisibility(0);
        this.i.setVisibility(0);
        this.j.setVisibility(0);
        this.k.setVisibility(0);
        this.l.setVisibility(0);
    }

    public void a(Handler handler) {
        this.f3987b = handler;
    }

    public void a(n nVar, boolean z) {
        ImageView imageView;
        ImageView imageView2;
        int i2 = c.f3992a[nVar.ordinal()];
        if (i2 == 1) {
            this.f3988c.setVisibility(0);
            this.h.setVisibility(8);
            if (z) {
                imageView2 = this.f3988c;
            } else {
                imageView = this.f3988c;
                imageView.setImageResource(R.drawable.scan_state_safe);
                return;
            }
        } else if (i2 == 2) {
            this.f3989d.setVisibility(0);
            this.i.setVisibility(8);
            if (z) {
                imageView2 = this.f3989d;
            } else {
                imageView = this.f3989d;
                imageView.setImageResource(R.drawable.scan_state_safe);
                return;
            }
        } else if (i2 == 3) {
            this.e.setVisibility(0);
            this.j.setVisibility(8);
            if (z) {
                imageView2 = this.e;
            } else {
                imageView = this.e;
                imageView.setImageResource(R.drawable.scan_state_safe);
                return;
            }
        } else if (i2 == 4) {
            this.f.setVisibility(0);
            this.k.setVisibility(8);
            if (z) {
                imageView2 = this.f;
            } else {
                imageView = this.f;
                imageView.setImageResource(R.drawable.scan_state_safe);
                return;
            }
        } else if (i2 == 5) {
            this.g.setVisibility(0);
            this.l.setVisibility(8);
            if (z) {
                imageView2 = this.g;
            } else {
                imageView = this.g;
                imageView.setImageResource(R.drawable.scan_state_safe);
                return;
            }
        } else {
            return;
        }
        imageView2.setImageResource(R.drawable.scan_state_risky);
    }

    public void onClick(View view) {
        if (view.getId() == R.id.btn_stop) {
            this.f3987b.sendEmptyMessage(200);
        }
    }

    /* access modifiers changed from: protected */
    public void onFinishInflate() {
        super.onFinishInflate();
        ((Button) findViewById(R.id.btn_stop)).setOnClickListener(this);
        View findViewById = findViewById(R.id.item_1);
        View findViewById2 = findViewById(R.id.item_2);
        View findViewById3 = findViewById(R.id.item_3);
        View findViewById4 = findViewById(R.id.item_4);
        View findViewById5 = findViewById(R.id.item_5);
        ((TextView) findViewById.findViewById(R.id.tv_content)).setText(R.string.first_aid_item1_content);
        this.f3988c = (ImageView) findViewById.findViewById(R.id.iv_status);
        this.h = findViewById.findViewById(R.id.progressbar_status);
        ((TextView) findViewById2.findViewById(R.id.tv_content)).setText(R.string.first_aid_item2_content);
        this.f3989d = (ImageView) findViewById2.findViewById(R.id.iv_status);
        this.i = findViewById2.findViewById(R.id.progressbar_status);
        ((TextView) findViewById3.findViewById(R.id.tv_content)).setText(R.string.first_aid_item3_content);
        this.e = (ImageView) findViewById3.findViewById(R.id.iv_status);
        this.j = findViewById3.findViewById(R.id.progressbar_status);
        ((TextView) findViewById4.findViewById(R.id.tv_content)).setText(R.string.first_aid_item4_content);
        this.f = (ImageView) findViewById4.findViewById(R.id.iv_status);
        this.k = findViewById4.findViewById(R.id.progressbar_status);
        ((TextView) findViewById5.findViewById(R.id.tv_content)).setText(R.string.first_aid_item5_content);
        this.g = (ImageView) findViewById5.findViewById(R.id.iv_status);
        this.l = findViewById5.findViewById(R.id.progressbar_status);
    }
}
