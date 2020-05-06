package com.miui.optimizecenter.storage.view;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.Nullable;
import b.b.i.b.a;
import b.b.i.b.g;
import com.miui.securitycenter.R;

public class PreferenceItemView extends LinearLayout {

    /* renamed from: a  reason: collision with root package name */
    private TextView f5803a;

    /* renamed from: b  reason: collision with root package name */
    private TextView f5804b;

    /* renamed from: c  reason: collision with root package name */
    private ImageView f5805c;

    /* renamed from: d  reason: collision with root package name */
    private a f5806d = a.MOUNT;

    public PreferenceItemView(Context context) {
        super(context);
    }

    public PreferenceItemView(Context context, @Nullable AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public PreferenceItemView(Context context, @Nullable AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    public static PreferenceItemView a(Context context, ViewGroup viewGroup, a aVar) {
        PreferenceItemView preferenceItemView = (PreferenceItemView) LayoutInflater.from(context).inflate(R.layout.storage_main_item_item, viewGroup, false);
        preferenceItemView.setmItemType(aVar);
        return preferenceItemView;
    }

    public a getmItemType() {
        return this.f5806d;
    }

    /* access modifiers changed from: protected */
    public void onFinishInflate() {
        super.onFinishInflate();
        this.f5803a = (TextView) findViewById(R.id.tv_title);
        this.f5804b = (TextView) findViewById(R.id.tv_sub);
        this.f5805c = (ImageView) findViewById(R.id.iv_arrow);
        if (g.a()) {
            setBackgroundResource(R.drawable.list_item_background_folme);
            a.a(this);
            return;
        }
        setBackgroundResource(R.drawable.list_item_background_color_light);
    }

    public void setArrowVisible(int i) {
        g.a((View) this.f5805c, i);
    }

    public void setSummary(int i) {
        g.a(this.f5804b, i);
    }

    public void setSummary(String str) {
        g.a((View) this.f5804b, TextUtils.isEmpty(str) ? 8 : 0);
        g.a(this.f5804b, str);
    }

    public void setTitle(int i) {
        g.a(this.f5803a, i);
    }

    public void setTitle(String str) {
        g.a(this.f5803a, str);
    }

    public void setmItemType(a aVar) {
        this.f5806d = aVar;
    }
}
