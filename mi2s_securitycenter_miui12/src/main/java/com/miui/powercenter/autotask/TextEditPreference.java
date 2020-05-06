package com.miui.powercenter.autotask;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.widget.EditText;
import androidx.preference.A;
import androidx.preference.Preference;
import com.miui.securitycenter.R;

public class TextEditPreference extends Preference {

    /* renamed from: a  reason: collision with root package name */
    private EditText f6728a;
    /* access modifiers changed from: private */

    /* renamed from: b  reason: collision with root package name */
    public String f6729b = "";
    /* access modifiers changed from: private */

    /* renamed from: c  reason: collision with root package name */
    public a f6730c;

    /* renamed from: d  reason: collision with root package name */
    private TextWatcher f6731d = new da(this);

    public interface a {
        void a(Editable editable);
    }

    public TextEditPreference(Context context) {
        super(context, (AttributeSet) null);
        a();
    }

    public TextEditPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        a();
    }

    private void a() {
        setLayoutResource(R.layout.pc_auto_task_title_edit_view);
    }

    private void b() {
        EditText editText = this.f6728a;
        if (editText != null) {
            editText.removeTextChangedListener(this.f6731d);
            this.f6728a.setText(this.f6729b);
            this.f6728a.addTextChangedListener(this.f6731d);
        }
    }

    public void a(a aVar) {
        this.f6730c = aVar;
    }

    public void a(String str) {
        this.f6729b = str;
        b();
    }

    public void onBindViewHolder(A a2) {
        a2.itemView.setBackgroundColor(0);
        this.f6728a = (EditText) a2.b((int) R.id.edit);
        this.f6728a.setSelectAllOnFocus(true);
        b();
    }
}
