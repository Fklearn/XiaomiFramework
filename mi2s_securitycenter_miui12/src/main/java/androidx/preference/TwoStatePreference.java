package androidx.preference;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import androidx.preference.Preference;

public abstract class TwoStatePreference extends Preference {

    /* renamed from: a  reason: collision with root package name */
    protected boolean f1029a;

    /* renamed from: b  reason: collision with root package name */
    private CharSequence f1030b;

    /* renamed from: c  reason: collision with root package name */
    private CharSequence f1031c;

    /* renamed from: d  reason: collision with root package name */
    private boolean f1032d;
    private boolean e;

    static class SavedState extends Preference.BaseSavedState {
        public static final Parcelable.Creator<SavedState> CREATOR = new M();
        boolean mChecked;

        SavedState(Parcel parcel) {
            super(parcel);
            this.mChecked = parcel.readInt() != 1 ? false : true;
        }

        SavedState(Parcelable parcelable) {
            super(parcelable);
        }

        public void writeToParcel(Parcel parcel, int i) {
            super.writeToParcel(parcel, i);
            parcel.writeInt(this.mChecked ? 1 : 0);
        }
    }

    public TwoStatePreference(Context context) {
        this(context, (AttributeSet) null);
    }

    public TwoStatePreference(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public TwoStatePreference(Context context, AttributeSet attributeSet, int i) {
        this(context, attributeSet, i, 0);
    }

    public TwoStatePreference(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Removed duplicated region for block: B:15:0x002d  */
    /* JADX WARNING: Removed duplicated region for block: B:20:0x003f  */
    /* JADX WARNING: Removed duplicated region for block: B:23:0x0046  */
    /* JADX WARNING: Removed duplicated region for block: B:25:? A[RETURN, SYNTHETIC] */
    @androidx.annotation.RestrictTo({androidx.annotation.RestrictTo.a.LIBRARY})
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void a(android.view.View r5) {
        /*
            r4 = this;
            boolean r0 = r5 instanceof android.widget.TextView
            if (r0 != 0) goto L_0x0005
            return
        L_0x0005:
            android.widget.TextView r5 = (android.widget.TextView) r5
            r0 = 1
            boolean r1 = r4.f1029a
            r2 = 0
            if (r1 == 0) goto L_0x001c
            java.lang.CharSequence r1 = r4.f1030b
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x001c
            java.lang.CharSequence r0 = r4.f1030b
        L_0x0017:
            r5.setText(r0)
            r0 = r2
            goto L_0x002b
        L_0x001c:
            boolean r1 = r4.f1029a
            if (r1 != 0) goto L_0x002b
            java.lang.CharSequence r1 = r4.f1031c
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x002b
            java.lang.CharSequence r0 = r4.f1031c
            goto L_0x0017
        L_0x002b:
            if (r0 == 0) goto L_0x003b
            java.lang.CharSequence r1 = r4.getSummary()
            boolean r3 = android.text.TextUtils.isEmpty(r1)
            if (r3 != 0) goto L_0x003b
            r5.setText(r1)
            r0 = r2
        L_0x003b:
            r1 = 8
            if (r0 != 0) goto L_0x0040
            r1 = r2
        L_0x0040:
            int r0 = r5.getVisibility()
            if (r1 == r0) goto L_0x0049
            r5.setVisibility(r1)
        L_0x0049:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: androidx.preference.TwoStatePreference.a(android.view.View):void");
    }

    /* access modifiers changed from: protected */
    public void a(A a2) {
        a(a2.b(16908304));
    }

    public void a(CharSequence charSequence) {
        this.f1031c = charSequence;
        if (!isChecked()) {
            notifyChanged();
        }
    }

    public void a(boolean z) {
        this.e = z;
    }

    public void b(CharSequence charSequence) {
        this.f1030b = charSequence;
        if (isChecked()) {
            notifyChanged();
        }
    }

    public boolean isChecked() {
        return this.f1029a;
    }

    /* access modifiers changed from: protected */
    public void onClick() {
        super.onClick();
        boolean z = !isChecked();
        if (callChangeListener(Boolean.valueOf(z))) {
            setChecked(z);
        }
    }

    /* access modifiers changed from: protected */
    public Object onGetDefaultValue(TypedArray typedArray, int i) {
        return Boolean.valueOf(typedArray.getBoolean(i, false));
    }

    /* access modifiers changed from: protected */
    public void onRestoreInstanceState(Parcelable parcelable) {
        if (parcelable == null || !parcelable.getClass().equals(SavedState.class)) {
            super.onRestoreInstanceState(parcelable);
            return;
        }
        SavedState savedState = (SavedState) parcelable;
        super.onRestoreInstanceState(savedState.getSuperState());
        setChecked(savedState.mChecked);
    }

    /* access modifiers changed from: protected */
    public Parcelable onSaveInstanceState() {
        Parcelable onSaveInstanceState = super.onSaveInstanceState();
        if (isPersistent()) {
            return onSaveInstanceState;
        }
        SavedState savedState = new SavedState(onSaveInstanceState);
        savedState.mChecked = isChecked();
        return savedState;
    }

    /* access modifiers changed from: protected */
    public void onSetInitialValue(Object obj) {
        if (obj == null) {
            obj = false;
        }
        setChecked(getPersistedBoolean(((Boolean) obj).booleanValue()));
    }

    public void setChecked(boolean z) {
        boolean z2 = this.f1029a != z;
        if (z2 || !this.f1032d) {
            this.f1029a = z;
            this.f1032d = true;
            persistBoolean(z);
            if (z2) {
                notifyDependencyChange(shouldDisableDependents());
                notifyChanged();
            }
        }
    }

    public boolean shouldDisableDependents() {
        return (this.e ? this.f1029a : !this.f1029a) || super.shouldDisableDependents();
    }
}
