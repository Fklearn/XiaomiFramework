package androidx.preference;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;
import androidx.preference.Preference;

public class SeekBarPreference extends Preference {

    /* renamed from: a  reason: collision with root package name */
    int f1023a;

    /* renamed from: b  reason: collision with root package name */
    int f1024b;

    /* renamed from: c  reason: collision with root package name */
    private int f1025c;

    /* renamed from: d  reason: collision with root package name */
    private int f1026d;
    boolean e;
    SeekBar f;
    private TextView g;
    boolean h;
    private boolean i;
    boolean j;
    private SeekBar.OnSeekBarChangeListener k;
    private View.OnKeyListener l;

    private static class SavedState extends Preference.BaseSavedState {
        public static final Parcelable.Creator<SavedState> CREATOR = new L();
        int mMax;
        int mMin;
        int mSeekBarValue;

        SavedState(Parcel parcel) {
            super(parcel);
            this.mSeekBarValue = parcel.readInt();
            this.mMin = parcel.readInt();
            this.mMax = parcel.readInt();
        }

        SavedState(Parcelable parcelable) {
            super(parcelable);
        }

        public void writeToParcel(Parcel parcel, int i) {
            super.writeToParcel(parcel, i);
            parcel.writeInt(this.mSeekBarValue);
            parcel.writeInt(this.mMin);
            parcel.writeInt(this.mMax);
        }
    }

    public SeekBarPreference(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, B.seekBarPreferenceStyle);
    }

    public SeekBarPreference(Context context, AttributeSet attributeSet, int i2) {
        this(context, attributeSet, i2, 0);
    }

    public SeekBarPreference(Context context, AttributeSet attributeSet, int i2, int i3) {
        super(context, attributeSet, i2, i3);
        this.k = new J(this);
        this.l = new K(this);
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, I.SeekBarPreference, i2, i3);
        this.f1024b = obtainStyledAttributes.getInt(I.SeekBarPreference_min, 0);
        a(obtainStyledAttributes.getInt(I.SeekBarPreference_android_max, 100));
        b(obtainStyledAttributes.getInt(I.SeekBarPreference_seekBarIncrement, 0));
        this.h = obtainStyledAttributes.getBoolean(I.SeekBarPreference_adjustable, true);
        this.i = obtainStyledAttributes.getBoolean(I.SeekBarPreference_showSeekBarValue, false);
        this.j = obtainStyledAttributes.getBoolean(I.SeekBarPreference_updatesContinuously, false);
        obtainStyledAttributes.recycle();
    }

    private void a(int i2, boolean z) {
        int i3 = this.f1024b;
        if (i2 < i3) {
            i2 = i3;
        }
        int i4 = this.f1025c;
        if (i2 > i4) {
            i2 = i4;
        }
        if (i2 != this.f1023a) {
            this.f1023a = i2;
            d(this.f1023a);
            persistInt(i2);
            if (z) {
                notifyChanged();
            }
        }
    }

    public final void a(int i2) {
        int i3 = this.f1024b;
        if (i2 < i3) {
            i2 = i3;
        }
        if (i2 != this.f1025c) {
            this.f1025c = i2;
            notifyChanged();
        }
    }

    /* access modifiers changed from: package-private */
    public void a(SeekBar seekBar) {
        int progress = this.f1024b + seekBar.getProgress();
        if (progress == this.f1023a) {
            return;
        }
        if (callChangeListener(Integer.valueOf(progress))) {
            a(progress, false);
            return;
        }
        seekBar.setProgress(this.f1023a - this.f1024b);
        d(this.f1023a);
    }

    public final void b(int i2) {
        if (i2 != this.f1026d) {
            this.f1026d = Math.min(this.f1025c - this.f1024b, Math.abs(i2));
            notifyChanged();
        }
    }

    public void c(int i2) {
        a(i2, true);
    }

    /* access modifiers changed from: package-private */
    public void d(int i2) {
        TextView textView = this.g;
        if (textView != null) {
            textView.setText(String.valueOf(i2));
        }
    }

    public void onBindViewHolder(A a2) {
        super.onBindViewHolder(a2);
        a2.itemView.setOnKeyListener(this.l);
        this.f = (SeekBar) a2.b(E.seekbar);
        this.g = (TextView) a2.b(E.seekbar_value);
        if (this.i) {
            this.g.setVisibility(0);
        } else {
            this.g.setVisibility(8);
            this.g = null;
        }
        SeekBar seekBar = this.f;
        if (seekBar == null) {
            Log.e("SeekBarPreference", "SeekBar view is null in onBindViewHolder.");
            return;
        }
        seekBar.setOnSeekBarChangeListener(this.k);
        this.f.setMax(this.f1025c - this.f1024b);
        int i2 = this.f1026d;
        if (i2 != 0) {
            this.f.setKeyProgressIncrement(i2);
        } else {
            this.f1026d = this.f.getKeyProgressIncrement();
        }
        this.f.setProgress(this.f1023a - this.f1024b);
        d(this.f1023a);
        this.f.setEnabled(isEnabled());
    }

    /* access modifiers changed from: protected */
    public Object onGetDefaultValue(TypedArray typedArray, int i2) {
        return Integer.valueOf(typedArray.getInt(i2, 0));
    }

    /* access modifiers changed from: protected */
    public void onRestoreInstanceState(Parcelable parcelable) {
        if (!parcelable.getClass().equals(SavedState.class)) {
            super.onRestoreInstanceState(parcelable);
            return;
        }
        SavedState savedState = (SavedState) parcelable;
        super.onRestoreInstanceState(savedState.getSuperState());
        this.f1023a = savedState.mSeekBarValue;
        this.f1024b = savedState.mMin;
        this.f1025c = savedState.mMax;
        notifyChanged();
    }

    /* access modifiers changed from: protected */
    public Parcelable onSaveInstanceState() {
        Parcelable onSaveInstanceState = super.onSaveInstanceState();
        if (isPersistent()) {
            return onSaveInstanceState;
        }
        SavedState savedState = new SavedState(onSaveInstanceState);
        savedState.mSeekBarValue = this.f1023a;
        savedState.mMin = this.f1024b;
        savedState.mMax = this.f1025c;
        return savedState;
    }

    /* access modifiers changed from: protected */
    public void onSetInitialValue(Object obj) {
        if (obj == null) {
            obj = 0;
        }
        c(getPersistedInt(((Integer) obj).intValue()));
    }
}
