package androidx.preference;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.h;

public abstract class DialogPreference extends Preference {

    /* renamed from: a  reason: collision with root package name */
    private CharSequence f1010a;

    /* renamed from: b  reason: collision with root package name */
    private CharSequence f1011b;

    /* renamed from: c  reason: collision with root package name */
    private Drawable f1012c;

    /* renamed from: d  reason: collision with root package name */
    private CharSequence f1013d;
    private CharSequence e;
    private int f;

    public interface a {
        @Nullable
        <T extends Preference> T findPreference(@NonNull CharSequence charSequence);
    }

    public DialogPreference(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, h.a(context, B.dialogPreferenceStyle, 16842897));
    }

    public DialogPreference(Context context, AttributeSet attributeSet, int i) {
        this(context, attributeSet, i, 0);
    }

    public DialogPreference(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, I.DialogPreference, i, i2);
        this.f1010a = h.b(obtainStyledAttributes, I.DialogPreference_dialogTitle, I.DialogPreference_android_dialogTitle);
        if (this.f1010a == null) {
            this.f1010a = getTitle();
        }
        this.f1011b = h.b(obtainStyledAttributes, I.DialogPreference_dialogMessage, I.DialogPreference_android_dialogMessage);
        this.f1012c = h.a(obtainStyledAttributes, I.DialogPreference_dialogIcon, I.DialogPreference_android_dialogIcon);
        this.f1013d = h.b(obtainStyledAttributes, I.DialogPreference_positiveButtonText, I.DialogPreference_android_positiveButtonText);
        this.e = h.b(obtainStyledAttributes, I.DialogPreference_negativeButtonText, I.DialogPreference_android_negativeButtonText);
        this.f = h.b(obtainStyledAttributes, I.DialogPreference_dialogLayout, I.DialogPreference_android_dialogLayout, 0);
        obtainStyledAttributes.recycle();
    }

    public Drawable a() {
        return this.f1012c;
    }

    public int b() {
        return this.f;
    }

    public CharSequence c() {
        return this.f1011b;
    }

    public CharSequence d() {
        return this.f1010a;
    }

    public CharSequence e() {
        return this.e;
    }

    public CharSequence f() {
        return this.f1013d;
    }

    /* access modifiers changed from: protected */
    public void onClick() {
        getPreferenceManager().a((Preference) this);
    }
}
