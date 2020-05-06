package androidx.preference;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.core.content.res.h;
import androidx.preference.Preference;

public class ListPreference extends DialogPreference {
    private CharSequence[] g;
    private CharSequence[] h;
    private String i;
    private boolean j;
    private String mSummary;

    private static class SavedState extends Preference.BaseSavedState {
        public static final Parcelable.Creator<SavedState> CREATOR = new C0151e();
        String mValue;

        SavedState(Parcel parcel) {
            super(parcel);
            this.mValue = parcel.readString();
        }

        SavedState(Parcelable parcelable) {
            super(parcelable);
        }

        public void writeToParcel(@NonNull Parcel parcel, int i) {
            super.writeToParcel(parcel, i);
            parcel.writeString(this.mValue);
        }
    }

    public static final class a implements Preference.e<ListPreference> {

        /* renamed from: a  reason: collision with root package name */
        private static a f1017a;

        private a() {
        }

        public static a a() {
            if (f1017a == null) {
                f1017a = new a();
            }
            return f1017a;
        }

        public CharSequence a(ListPreference listPreference) {
            return TextUtils.isEmpty(listPreference.h()) ? listPreference.getContext().getString(G.not_set) : listPreference.h();
        }
    }

    public ListPreference(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, h.a(context, B.dialogPreferenceStyle, 16842897));
    }

    public ListPreference(Context context, AttributeSet attributeSet, int i2) {
        this(context, attributeSet, i2, 0);
    }

    public ListPreference(Context context, AttributeSet attributeSet, int i2, int i3) {
        super(context, attributeSet, i2, i3);
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, I.ListPreference, i2, i3);
        this.g = h.d(obtainStyledAttributes, I.ListPreference_entries, I.ListPreference_android_entries);
        this.h = h.d(obtainStyledAttributes, I.ListPreference_entryValues, I.ListPreference_android_entryValues);
        int i4 = I.ListPreference_useSimpleSummaryProvider;
        if (h.a(obtainStyledAttributes, i4, i4, false)) {
            setSummaryProvider(a.a());
        }
        obtainStyledAttributes.recycle();
        TypedArray obtainStyledAttributes2 = context.obtainStyledAttributes(attributeSet, I.Preference, i2, i3);
        this.mSummary = h.b(obtainStyledAttributes2, I.Preference_summary, I.Preference_android_summary);
        obtainStyledAttributes2.recycle();
    }

    private int k() {
        return a(this.i);
    }

    public int a(String str) {
        CharSequence[] charSequenceArr;
        if (str == null || (charSequenceArr = this.h) == null) {
            return -1;
        }
        for (int length = charSequenceArr.length - 1; length >= 0; length--) {
            if (this.h[length].equals(str)) {
                return length;
            }
        }
        return -1;
    }

    public void a(CharSequence[] charSequenceArr) {
        this.h = charSequenceArr;
    }

    public void b(String str) {
        boolean z = !TextUtils.equals(this.i, str);
        if (z || !this.j) {
            this.i = str;
            this.j = true;
            persistString(str);
            if (z) {
                notifyChanged();
            }
        }
    }

    public CharSequence[] g() {
        return this.g;
    }

    public CharSequence getSummary() {
        if (getSummaryProvider() != null) {
            return getSummaryProvider().a(this);
        }
        Object h2 = h();
        CharSequence summary = super.getSummary();
        String str = this.mSummary;
        if (str == null) {
            return summary;
        }
        Object[] objArr = new Object[1];
        if (h2 == null) {
            h2 = "";
        }
        objArr[0] = h2;
        String format = String.format(str, objArr);
        if (TextUtils.equals(format, summary)) {
            return summary;
        }
        Log.w("ListPreference", "Setting a summary with a String formatting marker is no longer supported. You should use a SummaryProvider instead.");
        return format;
    }

    public CharSequence h() {
        CharSequence[] charSequenceArr;
        int k = k();
        if (k < 0 || (charSequenceArr = this.g) == null) {
            return null;
        }
        return charSequenceArr[k];
    }

    public CharSequence[] i() {
        return this.h;
    }

    public String j() {
        return this.i;
    }

    /* access modifiers changed from: protected */
    public Object onGetDefaultValue(TypedArray typedArray, int i2) {
        return typedArray.getString(i2);
    }

    /* access modifiers changed from: protected */
    public void onRestoreInstanceState(Parcelable parcelable) {
        if (parcelable == null || !parcelable.getClass().equals(SavedState.class)) {
            super.onRestoreInstanceState(parcelable);
            return;
        }
        SavedState savedState = (SavedState) parcelable;
        super.onRestoreInstanceState(savedState.getSuperState());
        b(savedState.mValue);
    }

    /* access modifiers changed from: protected */
    public Parcelable onSaveInstanceState() {
        Parcelable onSaveInstanceState = super.onSaveInstanceState();
        if (isPersistent()) {
            return onSaveInstanceState;
        }
        SavedState savedState = new SavedState(onSaveInstanceState);
        savedState.mValue = j();
        return savedState;
    }

    /* access modifiers changed from: protected */
    public void onSetInitialValue(Object obj) {
        b(getPersistedString((String) obj));
    }

    public void setSummary(CharSequence charSequence) {
        String charSequence2;
        super.setSummary(charSequence);
        if (charSequence == null && this.mSummary != null) {
            charSequence2 = null;
        } else if (charSequence != null && !charSequence.equals(this.mSummary)) {
            charSequence2 = charSequence.toString();
        } else {
            return;
        }
        this.mSummary = charSequence2;
    }
}
