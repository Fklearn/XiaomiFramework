package com.miui.optimizemanage.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.miui.securitycenter.R;

public class RunningProcessView extends LinearLayout {

    /* renamed from: a  reason: collision with root package name */
    private TextView f6023a;

    /* renamed from: b  reason: collision with root package name */
    private ProgressBar f6024b;

    /* renamed from: c  reason: collision with root package name */
    private ImageView f6025c;

    public RunningProcessView(Context context) {
        this(context, (AttributeSet) null);
    }

    public RunningProcessView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public RunningProcessView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        LayoutInflater.from(context).inflate(R.layout.om_running_process_layout, this, true);
        this.f6023a = (TextView) findViewById(R.id.title);
        this.f6024b = (ProgressBar) findViewById(R.id.progressbar_status);
        this.f6025c = (ImageView) findViewById(R.id.iv_status);
    }

    public void a() {
        this.f6024b.setVisibility(8);
        this.f6025c.setVisibility(0);
    }

    public void setTitle(String str) {
        this.f6023a.setText(str);
    }
}
