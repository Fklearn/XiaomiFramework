package com.miui.gamebooster.customview;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import com.miui.activityutil.o;
import com.miui.common.persistence.b;
import com.miui.gamebooster.m.ja;
import com.miui.gamebooster.widget.CheckBoxSettingItemView;
import com.miui.securitycenter.R;
import java.util.ArrayList;

/* renamed from: com.miui.gamebooster.customview.x  reason: case insensitive filesystem */
public class C0354x extends Dialog implements View.OnClickListener, CheckBoxSettingItemView.a, DialogInterface.OnCancelListener {

    /* renamed from: a  reason: collision with root package name */
    private final String f4236a;

    /* renamed from: b  reason: collision with root package name */
    private Context f4237b;

    /* renamed from: c  reason: collision with root package name */
    private CheckBoxSettingItemView f4238c;

    /* renamed from: d  reason: collision with root package name */
    private CheckBoxSettingItemView f4239d;
    private a e;

    /* renamed from: com.miui.gamebooster.customview.x$a */
    public interface a {
        void a();

        void onFinish();
    }

    public C0354x(@NonNull Context context, String str) {
        super(context);
        this.f4236a = str;
        a(context);
    }

    private void a(Context context) {
        this.f4237b = context;
    }

    public C0354x a(a aVar) {
        this.e = aVar;
        return this;
    }

    public ArrayList<String> a(String str, boolean z) {
        ArrayList<String> a2 = b.a(str, (ArrayList<String>) new ArrayList());
        if (a2 != null && a2.size() > 0) {
            int size = a2.size() - 1;
            while (true) {
                if (size < 0) {
                    break;
                }
                String str2 = a2.get(size);
                if (!TextUtils.isEmpty(str2) && str2.contains(this.f4236a)) {
                    a2.remove(size);
                    break;
                }
                size--;
            }
        } else {
            a2 = new ArrayList<>();
        }
        StringBuilder sb = new StringBuilder();
        sb.append(this.f4236a);
        sb.append(",");
        sb.append(z ? o.f2310b : o.f2309a);
        a2.add(sb.toString());
        return a2;
    }

    public void onCancel(DialogInterface dialogInterface) {
        a aVar = this.e;
        if (aVar != null) {
            aVar.onFinish();
        }
    }

    public void onCheckedChanged(View view, boolean z) {
        if (view == this.f4238c) {
            b.b("key_gb_record_ai", a("key_gb_record_ai", z));
        } else if (view == this.f4239d) {
            b.b("key_gb_record_manual", a("key_gb_record_manual", z));
            a aVar = this.e;
            if (aVar != null) {
                aVar.a();
            }
        }
    }

    public void onClick(View view) {
        if (R.id.tv_finish == view.getId()) {
            dismiss();
            a aVar = this.e;
            if (aVar != null) {
                aVar.onFinish();
            }
        }
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        requestWindowFeature(1);
        View inflate = LayoutInflater.from(this.f4237b).inflate(R.layout.gb_dialog_record_way_setting, (ViewGroup) null);
        this.f4238c = (CheckBoxSettingItemView) inflate.findViewById(R.id.aiSettingItem);
        this.f4238c.setOnCheckedChangeListener(this);
        this.f4239d = (CheckBoxSettingItemView) inflate.findViewById(R.id.manualSettingItem);
        this.f4239d.setOnCheckedChangeListener(this);
        this.f4238c.a(ja.a("key_gb_record_ai", this.f4236a), false, false);
        this.f4239d.a(ja.a("key_gb_record_manual", this.f4236a), false, false);
        View findViewById = inflate.findViewById(R.id.tv_finish);
        if (findViewById != null) {
            findViewById.setOnClickListener(this);
        }
        setContentView(inflate);
        setOnCancelListener(this);
        getWindow().setDimAmount(0.0f);
        Window window = getWindow();
        window.setBackgroundDrawableResource(17170445);
        WindowManager.LayoutParams attributes = window.getAttributes();
        attributes.width = -2;
        attributes.height = -2;
        attributes.gravity = 17;
        window.setAttributes(attributes);
        window.setType(2008);
    }
}
