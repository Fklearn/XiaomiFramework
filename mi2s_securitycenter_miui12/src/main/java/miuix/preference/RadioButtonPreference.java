package miuix.preference;

import android.content.Context;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Checkable;
import android.widget.CompoundButton;
import androidx.preference.A;
import androidx.preference.CheckBoxPreference;

public class RadioButtonPreference extends CheckBoxPreference implements Checkable {
    private p f;

    public RadioButtonPreference(Context context) {
        this(context, (AttributeSet) null);
    }

    public RadioButtonPreference(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, v.radioButtonPreferenceStyle);
    }

    public RadioButtonPreference(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    private void a(CompoundButton compoundButton) {
        if (Build.VERSION.SDK_INT >= 24) {
            Drawable buttonDrawable = compoundButton.getButtonDrawable();
            if (buttonDrawable instanceof StateListDrawable) {
                Drawable current = buttonDrawable.getCurrent();
                if (current instanceof AnimatedVectorDrawable) {
                    ((AnimatedVectorDrawable) current).start();
                }
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void a(p pVar) {
        this.f = pVar;
    }

    public boolean callChangeListener(Object obj) {
        p pVar = this.f;
        return (pVar != null ? pVar.a(this, obj) : true) && super.callChangeListener(obj);
    }

    /* access modifiers changed from: protected */
    public void notifyChanged() {
        super.notifyChanged();
        p pVar = this.f;
        if (pVar != null) {
            pVar.a(this);
        }
    }

    public void onBindViewHolder(A a2) {
        super.onBindViewHolder(a2);
        View view = a2.itemView;
        View findViewById = view.findViewById(16908310);
        if (findViewById != null && (findViewById instanceof Checkable)) {
            ((Checkable) findViewById).setChecked(isChecked());
        }
        View findViewById2 = view.findViewById(16908304);
        if (findViewById2 != null && (findViewById2 instanceof Checkable)) {
            ((Checkable) findViewById2).setChecked(isChecked());
        }
        View findViewById3 = view.findViewById(16908289);
        if (findViewById3 != null) {
            findViewById3.setImportantForAccessibility(2);
            if ((findViewById3 instanceof CompoundButton) && isChecked()) {
                a((CompoundButton) findViewById3);
            }
        }
    }

    public void toggle() {
        setChecked(!isChecked());
    }
}
