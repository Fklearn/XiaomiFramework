package com.miui.antivirus.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import b.b.b.o;
import b.b.c.i.b;
import b.b.c.j.r;
import com.miui.antivirus.model.e;
import com.miui.securitycenter.R;
import java.io.File;
import miui.text.ExtraTextUtils;

public class s extends AlertDialog {

    /* renamed from: a  reason: collision with root package name */
    private ImageView f2994a;

    /* renamed from: b  reason: collision with root package name */
    private TextView f2995b;

    /* renamed from: c  reason: collision with root package name */
    private TextView f2996c;

    /* renamed from: d  reason: collision with root package name */
    private TextView f2997d;
    private TextView e;
    private Button f;

    protected s(Context context, e eVar, b bVar) {
        super(context);
        Object[] objArr;
        int i;
        TextView textView;
        View inflate = View.inflate(context, R.layout.v_activity_risk_detail, (ViewGroup) null);
        this.f2994a = (ImageView) inflate.findViewById(R.id.icon);
        this.f2995b = (TextView) inflate.findViewById(R.id.title);
        this.f2996c = (TextView) inflate.findViewById(R.id.summary);
        this.f2997d = (TextView) inflate.findViewById(R.id.risk_name);
        this.e = (TextView) inflate.findViewById(R.id.risk_descx);
        this.f = (Button) inflate.findViewById(R.id.clear);
        File file = new File(eVar.q());
        if (eVar.o() == o.f.INSTALLED_APP) {
            r.a("pkg_icon://" + eVar.m(), this.f2994a, r.f);
            if (file.exists()) {
                textView = this.f2996c;
                i = R.string.hints_virus_app_summary;
                objArr = new Object[]{ExtraTextUtils.formatFileSize(context, file.length())};
            }
            this.f2995b.setText(eVar.h());
            this.f2997d.setText(eVar.u());
            this.e.setText(eVar.t());
            setCustomTitle(inflate);
            this.f.setOnClickListener(new r(this, o.a(context), eVar, bVar));
        }
        r.a("apk_icon://" + eVar.q(), this.f2994a, r.f);
        if (file.exists()) {
            textView = this.f2996c;
            i = R.string.hints_virus_apk_summary;
            objArr = new Object[]{ExtraTextUtils.formatFileSize(context, file.length())};
        }
        this.f2995b.setText(eVar.h());
        this.f2997d.setText(eVar.u());
        this.e.setText(eVar.t());
        setCustomTitle(inflate);
        this.f.setOnClickListener(new r(this, o.a(context), eVar, bVar));
        textView.setText(context.getString(i, objArr));
        this.f2995b.setText(eVar.h());
        this.f2997d.setText(eVar.u());
        this.e.setText(eVar.t());
        setCustomTitle(inflate);
        this.f.setOnClickListener(new r(this, o.a(context), eVar, bVar));
    }
}
