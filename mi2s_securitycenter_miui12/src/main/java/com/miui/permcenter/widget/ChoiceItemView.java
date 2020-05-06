package com.miui.permcenter.widget;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.miui.securitycenter.R;

public class ChoiceItemView extends FrameLayout {

    /* renamed from: a  reason: collision with root package name */
    private Context f6603a;

    /* renamed from: b  reason: collision with root package name */
    private RelativeLayout f6604b;

    /* renamed from: c  reason: collision with root package name */
    private TextView f6605c;

    /* renamed from: d  reason: collision with root package name */
    private TextView f6606d;
    private TextView e;
    private ImageView f;

    public ChoiceItemView(Context context) {
        this(context, (AttributeSet) null);
    }

    public ChoiceItemView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public ChoiceItemView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        LayoutInflater.from(context).inflate(R.layout.item_perm_select, this, true);
        this.f6603a = context;
        this.f6604b = (RelativeLayout) findViewById(R.id.item_group);
        this.f6604b.setMinimumHeight(getMinimumHeight());
        this.f6605c = (TextView) findViewById(R.id.item_title);
        this.f6606d = (TextView) findViewById(R.id.item_subtitle);
        this.e = (TextView) findViewById(R.id.item_tips);
        this.f = (ImageView) findViewById(R.id.item_selected);
        setBackground(getResources().getDrawable(R.drawable.pm_dialog_item_bg_selector));
    }

    public void a() {
        TextView textView = this.f6605c;
        if (textView != null) {
            textView.setTypeface(Typeface.DEFAULT_BOLD);
        }
    }

    public void a(int i, int i2, int i3) {
        setVisibility(i);
        this.f6604b.setPadding(i2, 0, i3, 0);
    }

    public void setSelectedVisible(boolean z) {
        this.f.setVisibility(z ? 0 : 8);
        setBackgroundColor(this.f6603a.getResources().getColor(R.color.perm_selected_light));
        this.f6605c.setTextColor(this.f6603a.getResources().getColor(R.color.tx_perm_selected));
        this.f6606d.setTextColor(this.f6603a.getResources().getColor(R.color.tx_perm_selected));
    }

    public void setSummary(String str) {
        this.f6606d.setText(str);
        this.f6606d.setVisibility(0);
    }

    public void setTips(String str) {
        this.e.setText(str);
        this.e.setTextColor(this.f6603a.getResources().getColor(R.color.tx_perm_selected));
        this.e.setVisibility(0);
    }

    public void setTitle(String str) {
        this.f6605c.setText(str);
    }
}
