package com.miui.gamebooster.xunyou;

import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.miui.gamebooster.d.e;
import com.miui.securitycenter.R;

public class XunyouGiftItem extends RelativeLayout implements View.OnClickListener {

    /* renamed from: a  reason: collision with root package name */
    private ImageView f5399a;

    /* renamed from: b  reason: collision with root package name */
    private TextView f5400b;

    /* renamed from: c  reason: collision with root package name */
    private Button f5401c;

    /* renamed from: d  reason: collision with root package name */
    private int f5402d;
    private a e;

    public XunyouGiftItem(Context context) {
        super(context);
        a(context);
    }

    public XunyouGiftItem(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        a(context);
    }

    private void a(Context context) {
        LayoutInflater.from(context).inflate(R.layout.gb_gift_item, this, true);
        this.f5399a = (ImageView) findViewById(R.id.gift_item_icon);
        this.f5400b = (TextView) findViewById(R.id.gift_item_summary);
        this.f5401c = (Button) findViewById(R.id.gift_item_button);
        this.f5401c.setOnClickListener(this);
        this.f5401c.setText(getResources().getString(R.string.gb_sign_gift_button_text));
    }

    public void a(String str, int i) {
        this.f5400b.setText(str);
        this.f5402d = i;
    }

    public void onClick(View view) {
        if (view.getId() == R.id.gift_item_button) {
            this.e.a(String.valueOf(this.f5402d));
        }
    }

    public void setGiftCallBack(a aVar) {
        this.e = aVar;
    }

    public void setGiftStatus(e eVar) {
        TextView textView;
        int i;
        Resources resources = getResources();
        int i2 = j.f5415a[eVar.ordinal()];
        if (i2 == 1) {
            this.f5399a.setBackground(resources.getDrawable(R.drawable.gb_signed_gift_enabled));
            this.f5401c.setEnabled(true);
            this.f5401c.setBackground(resources.getDrawable(R.drawable.gb_signed_get_gift_button));
            this.f5401c.setTextColor(resources.getColor(R.color.color_gtb_xunyou_gift_dialog_btn_txt1));
            textView = this.f5400b;
            i = R.color.color_gtb_xunyou_gift_dialog_txt1;
        } else if (i2 == 2) {
            this.f5399a.setBackground(resources.getDrawable(R.drawable.gb_signed_gift_disabled));
            this.f5401c.setEnabled(false);
            this.f5401c.setBackground(resources.getDrawable(R.drawable.gb_signed_gift_enabled_bg));
            this.f5401c.setTextColor(resources.getColor(R.color.color_gtb_xunyou_gift_dialog_btn_txt2));
            textView = this.f5400b;
            i = R.color.color_gtb_xunyou_gift_dialog_txt2;
        } else {
            return;
        }
        textView.setTextColor(resources.getColor(i));
    }
}
