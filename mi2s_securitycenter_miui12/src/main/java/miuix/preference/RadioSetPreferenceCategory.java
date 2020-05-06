package miuix.preference;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.Checkable;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;

public class RadioSetPreferenceCategory extends PreferenceCategory implements Checkable {
    /* access modifiers changed from: private */
    public p j;
    private p k;
    private boolean l;
    private boolean m;
    private String n;
    private RadioButtonPreference o;

    public RadioSetPreferenceCategory(Context context) {
        this(context, (AttributeSet) null);
    }

    public RadioSetPreferenceCategory(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, v.preferenceCategoryCheckableStyle);
    }

    public RadioSetPreferenceCategory(Context context, AttributeSet attributeSet, int i) {
        this(context, attributeSet, i, 0);
    }

    public RadioSetPreferenceCategory(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        this.k = new B(this);
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, z.RadioSetPreferenceCategory, i, i2);
        this.n = obtainStyledAttributes.getString(z.RadioSetPreferenceCategory_primaryKey);
        obtainStyledAttributes.recycle();
    }

    /* access modifiers changed from: package-private */
    public void a(p pVar) {
        this.j = pVar;
    }

    public boolean b(Preference preference) {
        String str = this.n;
        if (str == null) {
            if (c() == 0) {
                if (!(preference instanceof RadioButtonPreference)) {
                    throw new IllegalArgumentException("The first preference must be RadioButtonPreference, if primary key is empty");
                }
            }
            return super.b(preference);
        }
        if (str.equals(preference.getKey())) {
            RadioButtonPreference radioButtonPreference = this.o;
            if (radioButtonPreference != null && radioButtonPreference != preference) {
                throw new IllegalArgumentException("must not have two primary preference");
            } else if (!(preference instanceof RadioButtonPreference)) {
                throw new IllegalArgumentException("Primary preference must be RadioButtonPreference");
            }
        }
        return super.b(preference);
        this.o = (RadioButtonPreference) preference;
        this.o.a(this.k);
        return super.b(preference);
    }

    public boolean isChecked() {
        return this.l;
    }

    public void setChecked(boolean z) {
        if ((this.l != z) || !this.m) {
            this.l = z;
            this.m = true;
        }
    }

    public void toggle() {
        setChecked(!isChecked());
    }
}
