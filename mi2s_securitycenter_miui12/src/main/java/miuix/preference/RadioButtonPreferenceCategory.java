package miuix.preference;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Checkable;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;

public class RadioButtonPreferenceCategory extends PreferenceCategory {
    private c j;
    private int k;
    private p l;

    class a extends c {

        /* renamed from: c  reason: collision with root package name */
        private RadioSetPreferenceCategory f8884c;

        a(RadioSetPreferenceCategory radioSetPreferenceCategory) {
            super(radioSetPreferenceCategory);
            this.f8884c = radioSetPreferenceCategory;
        }

        public Preference a() {
            return this.f8884c;
        }

        public void a(p pVar) {
            this.f8884c.a(pVar);
        }
    }

    class b extends c {

        /* renamed from: c  reason: collision with root package name */
        RadioButtonPreference f8886c;

        b(RadioButtonPreference radioButtonPreference) {
            super(radioButtonPreference);
            this.f8886c = radioButtonPreference;
        }

        public Preference a() {
            return this.f8886c;
        }

        public void a(p pVar) {
            this.f8886c.a(pVar);
        }
    }

    abstract class c implements Checkable {

        /* renamed from: a  reason: collision with root package name */
        Checkable f8888a;

        c(Checkable checkable) {
            this.f8888a = checkable;
        }

        /* access modifiers changed from: package-private */
        public abstract Preference a();

        /* access modifiers changed from: package-private */
        public abstract void a(p pVar);

        public boolean isChecked() {
            return this.f8888a.isChecked();
        }

        public void setChecked(boolean z) {
            this.f8888a.setChecked(z);
        }

        public void toggle() {
            setChecked(!isChecked());
        }
    }

    public RadioButtonPreferenceCategory(Context context) {
        this(context, (AttributeSet) null);
    }

    public RadioButtonPreferenceCategory(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, v.preferenceCategoryRadioStyle);
    }

    public RadioButtonPreferenceCategory(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.j = null;
        this.k = -1;
        this.l = new A(this);
    }

    /* access modifiers changed from: private */
    public void a(c cVar) {
        if (cVar.isChecked()) {
            int c2 = c();
            for (int i = 0; i < c2; i++) {
                if (a(i) == cVar.a()) {
                    this.k = i;
                    return;
                }
            }
        }
    }

    /* access modifiers changed from: private */
    public void b(c cVar) {
        if (cVar.isChecked()) {
            c cVar2 = this.j;
            if (!(cVar2 == null || cVar2.a() == cVar.a())) {
                this.j.setChecked(false);
            }
            this.j = cVar;
        }
    }

    /* access modifiers changed from: private */
    public c e(Preference preference) {
        if (preference instanceof RadioButtonPreference) {
            return new b((RadioButtonPreference) preference);
        }
        if (preference instanceof RadioSetPreferenceCategory) {
            return new a((RadioSetPreferenceCategory) preference);
        }
        throw new IllegalArgumentException("Only RadioButtonPreference or RadioSetPreferenceCategory can be added to RadioButtonPreferenceCategory");
    }

    public boolean b(Preference preference) {
        c e = e(preference);
        boolean b2 = super.b(preference);
        if (b2) {
            e.a(this.l);
        }
        if (e.isChecked()) {
            if (this.j == null) {
                this.j = e;
            } else {
                throw new IllegalStateException("Already has a checked item, please check state of new add preference");
            }
        }
        return b2;
    }

    public boolean d(Preference preference) {
        c e = e(preference);
        boolean d2 = super.d(preference);
        if (d2) {
            e.a((p) null);
            if (e.isChecked()) {
                e.setChecked(false);
                this.k = -1;
                this.j = null;
            }
        }
        return d2;
    }
}
