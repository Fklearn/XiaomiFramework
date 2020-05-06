package com.miui.securityscan.shortcut;

import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.miui.securitycenter.R;
import com.miui.securityscan.shortcut.e;
import miui.content.res.IconCustomizer;
import miui.widget.SlidingButton;

public class ShortcutListItemView extends LinearLayout implements CompoundButton.OnCheckedChangeListener {

    /* renamed from: a  reason: collision with root package name */
    private e.a f7949a;

    /* renamed from: b  reason: collision with root package name */
    private ImageView f7950b;

    /* renamed from: c  reason: collision with root package name */
    private TextView f7951c;

    /* renamed from: d  reason: collision with root package name */
    private c f7952d;
    private SlidingButton e;

    public ShortcutListItemView(Context context) {
        this(context, (AttributeSet) null);
    }

    public ShortcutListItemView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public ShortcutListItemView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    public void a() {
        SlidingButton slidingButton = this.e;
        if (slidingButton != null) {
            this.e.setChecked(!slidingButton.isChecked());
        }
    }

    public void a(c cVar) {
        ImageView imageView;
        int i;
        TextView textView;
        int i2;
        ImageView imageView2;
        int i3;
        this.f7952d = cVar;
        this.f7949a = cVar.f7955a;
        this.e.setOnCheckedChangeListener((CompoundButton.OnCheckedChangeListener) null);
        Resources resources = getResources();
        this.e.setChecked(cVar.f7956b);
        switch (f.f7964a[cVar.f7955a.ordinal()]) {
            case 1:
                if (e.a()) {
                    imageView = this.f7950b;
                    i = R.drawable.ic_launcher_quick_clean_new;
                } else {
                    imageView = this.f7950b;
                    i = R.drawable.ic_launcher_quick_clean;
                }
                imageView.setImageDrawable(IconCustomizer.generateIconStyleDrawable(resources.getDrawable(i)));
                textView = this.f7951c;
                i2 = R.string.btn_text_quick_cleanup;
                break;
            case 2:
                this.f7950b.setImageDrawable(IconCustomizer.generateIconStyleDrawable(resources.getDrawable(R.drawable.ic_launcher_rubbish_clean)));
                textView = this.f7951c;
                i2 = R.string.activity_title_garbage_cleanup;
                break;
            case 3:
                this.f7950b.setImageDrawable(IconCustomizer.generateIconStyleDrawable(resources.getDrawable(R.drawable.ic_launcher_network_assistant)));
                textView = this.f7951c;
                i2 = R.string.activity_title_networkassistants;
                break;
            case 4:
                this.f7950b.setImageDrawable(IconCustomizer.generateIconStyleDrawable(resources.getDrawable(R.drawable.ic_launcher_anti_spam)));
                textView = this.f7951c;
                i2 = R.string.activity_title_antispam;
                break;
            case 5:
                this.f7950b.setImageDrawable(IconCustomizer.generateIconStyleDrawable(resources.getDrawable(R.drawable.ic_launcher_power_optimize)));
                textView = this.f7951c;
                i2 = R.string.activity_title_power_manager;
                break;
            case 6:
                this.f7950b.setImageDrawable(IconCustomizer.generateIconStyleDrawable(resources.getDrawable(R.drawable.ic_launcher_virus_scan)));
                textView = this.f7951c;
                i2 = R.string.activity_title_antivirus;
                break;
            case 7:
                this.f7950b.setImageDrawable(IconCustomizer.generateIconStyleDrawable(resources.getDrawable(R.drawable.ic_launcher_license_manage)));
                textView = this.f7951c;
                i2 = R.string.activity_title_license_manager;
                break;
            case 8:
                if (e.a()) {
                    imageView2 = this.f7950b;
                    i3 = R.drawable.icon_power_cleanup_new;
                } else {
                    imageView2 = this.f7950b;
                    i3 = R.drawable.icon_power_cleanup;
                }
                imageView2.setImageDrawable(IconCustomizer.generateIconStyleDrawable(resources.getDrawable(i3)));
                textView = this.f7951c;
                i2 = R.string.btn_text_power_cleanup;
                break;
            case 9:
                this.f7950b.setImageDrawable(IconCustomizer.generateIconStyleDrawable(resources.getDrawable(R.drawable.ic_launcher_network_diagnostics)));
                textView = this.f7951c;
                i2 = R.string.network_diagnostics;
                break;
            case 10:
                this.f7950b.setImageDrawable(IconCustomizer.generateIconStyleDrawable(resources.getDrawable(R.drawable.hongbao_launcher)));
                textView = this.f7951c;
                i2 = R.string.card_main_hbassistant_title;
                break;
            default:
                this.e.setOnCheckedChangeListener(this);
        }
        textView.setText(i2);
        this.e.setOnCheckedChangeListener(this);
    }

    public void onCheckedChanged(CompoundButton compoundButton, boolean z) {
        this.f7952d.f7956b = z;
        if (z) {
            e.a(getContext(), this.f7949a);
        } else {
            e.c(getContext(), this.f7949a);
        }
    }

    /* access modifiers changed from: protected */
    public void onFinishInflate() {
        super.onFinishInflate();
        this.f7950b = (ImageView) findViewById(R.id.icon);
        this.f7951c = (TextView) findViewById(R.id.title);
        this.e = findViewById(R.id.sliding_button);
    }
}
