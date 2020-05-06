package com.miui.gamebooster.xunyou;

import android.content.Context;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.miui.gamebooster.d.e;
import com.miui.gamebooster.k.b;
import com.miui.securitycenter.R;
import java.util.Iterator;

public class m extends RelativeLayout implements View.OnClickListener {

    /* renamed from: a  reason: collision with root package name */
    private XunyouGiftItem f5420a;

    /* renamed from: b  reason: collision with root package name */
    private XunyouGiftItem f5421b;

    /* renamed from: c  reason: collision with root package name */
    private XunyouGiftItem f5422c;

    /* renamed from: d  reason: collision with root package name */
    private LinearLayout f5423d;
    private TextView e;
    private TextView f;
    private Button g;
    private Button h;
    /* access modifiers changed from: private */
    public CheckBox i;
    private boolean j;
    private a k;

    public m(Context context) {
        super(context);
        a(context);
    }

    private void a(Context context) {
        LayoutInflater.from(context).inflate(R.layout.gb_gift_float_window, this);
        this.f5420a = (XunyouGiftItem) findViewById(R.id.gift_item_one);
        this.f5421b = (XunyouGiftItem) findViewById(R.id.gift_item_two);
        this.f5422c = (XunyouGiftItem) findViewById(R.id.gift_item_three);
        this.f5423d = (LinearLayout) findViewById(R.id.loading_gift);
        this.f = (TextView) findViewById(R.id.loading_gift_text);
        this.e = (TextView) findViewById(R.id.signed_days);
        this.i = (CheckBox) findViewById(R.id.gb_notification_checkbox);
        this.i.setOnCheckedChangeListener(new k(this, context));
        this.f5420a.a(getResources().getString(R.string.gb_xunyou_gift_type_one), 1);
        this.f5421b.a(getResources().getString(R.string.gb_xunyou_gift_type_two), 2);
        this.f5422c.a(getResources().getString(R.string.gb_xunyou_gift_type_three), 3);
        this.g = (Button) findViewById(R.id.sign_button);
        this.g.setOnClickListener(this);
        this.h = (Button) findViewById(R.id.close_dialog);
        this.h.setOnClickListener(this);
        c();
        new l(this, context).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);
    }

    public void a() {
        this.f.setText(getResources().getString(R.string.gb_signed_gift_loading_fail));
    }

    public void b() {
        this.f5420a.setVisibility(0);
        this.f5421b.setVisibility(0);
        this.f5422c.setVisibility(0);
        this.f5423d.setVisibility(8);
        c();
    }

    public void c() {
        int i2;
        Resources resources;
        Button button;
        XunyouGiftItem xunyouGiftItem;
        this.f5420a.setGiftStatus(e.have_get);
        this.f5421b.setGiftStatus(e.have_get);
        this.f5422c.setGiftStatus(e.have_get);
        Iterator<String> it = b.b().d().iterator();
        while (it.hasNext()) {
            int intValue = Integer.valueOf(it.next()).intValue();
            if (intValue == 1) {
                xunyouGiftItem = this.f5420a;
            } else if (intValue == 2) {
                xunyouGiftItem = this.f5421b;
            } else if (intValue == 3) {
                xunyouGiftItem = this.f5422c;
            }
            xunyouGiftItem.setGiftStatus(e.have_not_get);
        }
        if (b.b().a()) {
            this.g.setEnabled(false);
            this.g.setText(getResources().getString(R.string.gb_have_signed));
            button = this.g;
            resources = getResources();
            i2 = R.drawable.gb_signed_gift_enabled_bg;
        } else {
            this.g.setEnabled(true);
            this.g.setText(getResources().getString(R.string.gb_get_gift_right_now));
            button = this.g;
            resources = getResources();
            i2 = R.drawable.gb_signed_cancle_button;
        }
        button.setBackground(resources.getDrawable(i2));
    }

    public void d() {
        this.e.setText(String.valueOf(b.b().c()));
        c();
    }

    public void e() {
        this.f5420a.setVisibility(8);
        this.f5421b.setVisibility(8);
        this.f5422c.setVisibility(8);
        this.f5423d.setVisibility(0);
        this.f.setText(getResources().getString(R.string.gb_signed_gift_loading_summary));
    }

    public boolean getAdded() {
        return this.j;
    }

    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.close_dialog) {
            this.k.b();
        } else if (id == R.id.sign_button) {
            this.k.d();
        }
    }

    public void setAdded(Boolean bool) {
        this.j = bool.booleanValue();
    }

    public void setGiftCallBack(a aVar) {
        this.k = aVar;
        this.f5420a.setGiftCallBack(aVar);
        this.f5421b.setGiftCallBack(aVar);
        this.f5422c.setGiftCallBack(aVar);
    }
}
