package miuix.preference;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.SpinnerAdapter;
import androidx.annotation.NonNull;
import androidx.core.content.res.h;
import androidx.preference.A;
import androidx.preference.Preference;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import miui.external.adapter.SpinnerCheckableArrayAdapter;
import miui.external.adapter.SpinnerDoubleLineContentAdapter;
import miui.external.widget.Spinner;

public class DropDownPreference extends Preference {

    /* renamed from: a  reason: collision with root package name */
    private static final Class<?>[] f8877a = {Context.class, AttributeSet.class};

    /* renamed from: b  reason: collision with root package name */
    private static final CharSequence[] f8878b = new CharSequence[0];
    /* access modifiers changed from: private */

    /* renamed from: c  reason: collision with root package name */
    public ArrayAdapter f8879c;

    /* renamed from: d  reason: collision with root package name */
    private ArrayAdapter f8880d;
    private String e;
    private boolean f;
    /* access modifiers changed from: private */
    public Spinner g;
    private CharSequence[] h;
    /* access modifiers changed from: private */
    public CharSequence[] i;
    private Drawable[] j;
    /* access modifiers changed from: private */
    public Handler k;
    private final AdapterView.OnItemSelectedListener l;

    private static class SavedState extends Preference.BaseSavedState {
        public static final Parcelable.Creator<SavedState> CREATOR = new g();
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

    private static class a extends SpinnerDoubleLineContentAdapter {

        /* renamed from: a  reason: collision with root package name */
        private CharSequence[] f8881a;

        a(Context context, AttributeSet attributeSet, int i, int i2) {
            super(context, 0);
            int[] iArr;
            TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, z.DropDownPreference, i, i2);
            this.mEntries = h.d(obtainStyledAttributes, z.DropDownPreference_entries, 0);
            this.f8881a = h.d(obtainStyledAttributes, z.DropDownPreference_entryValues, 0);
            this.mSummaries = h.d(obtainStyledAttributes, z.DropDownPreference_entrySummaries, 0);
            int resourceId = obtainStyledAttributes.getResourceId(z.DropDownPreference_entryIcons, -1);
            obtainStyledAttributes.recycle();
            if (resourceId > 0) {
                TypedArray obtainTypedArray = context.getResources().obtainTypedArray(resourceId);
                iArr = new int[obtainTypedArray.length()];
                for (int i3 = 0; i3 < obtainTypedArray.length(); i3++) {
                    iArr[i3] = obtainTypedArray.getResourceId(i3, 0);
                }
                obtainTypedArray.recycle();
            } else {
                iArr = null;
            }
            setEntryIcons(iArr);
        }

        public void a(CharSequence[] charSequenceArr) {
            this.f8881a = charSequenceArr;
        }

        public CharSequence[] a() {
            return this.f8881a;
        }
    }

    private static class b implements SpinnerCheckableArrayAdapter.CheckedStateProvider {

        /* renamed from: a  reason: collision with root package name */
        private DropDownPreference f8882a;

        /* renamed from: b  reason: collision with root package name */
        private ArrayAdapter f8883b;

        public b(DropDownPreference dropDownPreference, ArrayAdapter arrayAdapter) {
            this.f8882a = dropDownPreference;
            this.f8883b = arrayAdapter;
        }

        public boolean isChecked(int i) {
            return TextUtils.equals(this.f8882a.c(), this.f8882a.i[i]);
        }
    }

    public DropDownPreference(Context context) {
        this(context, (AttributeSet) null);
    }

    public DropDownPreference(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, v.dropdownPreferenceStyle);
    }

    public DropDownPreference(Context context, AttributeSet attributeSet, int i2) {
        this(context, attributeSet, i2, 0);
    }

    public DropDownPreference(Context context, AttributeSet attributeSet, int i2, int i3) {
        super(context, attributeSet, i2, i3);
        this.k = new Handler();
        this.l = new c(this);
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, z.DropDownPreference, i2, i3);
        String string = obtainStyledAttributes.getString(z.DropDownPreference_adapter);
        obtainStyledAttributes.recycle();
        if (!TextUtils.isEmpty(string)) {
            this.f8880d = a(context, attributeSet, string);
        } else {
            this.f8880d = new a(context, attributeSet, i2, i3);
        }
        this.f8879c = a();
        e();
    }

    private ArrayAdapter a(Context context, AttributeSet attributeSet, String str) {
        try {
            Constructor<? extends U> constructor = context.getClassLoader().loadClass(str).asSubclass(ArrayAdapter.class).getConstructor(f8877a);
            Object[] objArr = {context, attributeSet};
            constructor.setAccessible(true);
            return (ArrayAdapter) constructor.newInstance(objArr);
        } catch (NoSuchMethodException e2) {
            throw new IllegalStateException("Error creating Adapter " + str, e2);
        } catch (InstantiationException | InvocationTargetException e3) {
            throw new IllegalStateException("Could not instantiate the Adapter: " + str, e3);
        } catch (IllegalAccessException e4) {
            throw new IllegalStateException("Can't access non-public constructor " + str, e4);
        } catch (ClassNotFoundException e5) {
            throw new IllegalStateException("Can't find Adapter: " + str, e5);
        }
    }

    private void a(Spinner spinner) {
        spinner.setClickable(false);
        spinner.setLongClickable(false);
        spinner.setContextClickable(false);
    }

    private int c(String str) {
        int i2 = 0;
        while (true) {
            CharSequence[] charSequenceArr = this.i;
            if (i2 >= charSequenceArr.length) {
                return -1;
            }
            if (TextUtils.equals(charSequenceArr[i2], str)) {
                return i2;
            }
            i2++;
        }
    }

    private void e() {
        Drawable[] drawableArr;
        ArrayAdapter arrayAdapter = this.f8880d;
        if (arrayAdapter instanceof a) {
            this.h = ((a) arrayAdapter).getEntries();
            this.i = ((a) this.f8880d).a();
            drawableArr = ((a) this.f8880d).getEntryIcons();
        } else {
            int count = arrayAdapter.getCount();
            this.h = new CharSequence[this.f8880d.getCount()];
            for (int i2 = 0; i2 < count; i2++) {
                this.h[i2] = this.f8880d.getItem(i2).toString();
            }
            this.i = this.h;
            drawableArr = null;
        }
        this.j = drawableArr;
    }

    public int a(String str) {
        return c(str);
    }

    /* access modifiers changed from: package-private */
    public ArrayAdapter a() {
        Context context = getContext();
        ArrayAdapter arrayAdapter = this.f8880d;
        return new SpinnerCheckableArrayAdapter(context, arrayAdapter, new b(this, arrayAdapter));
    }

    public void a(int i2) {
        b(this.i[i2].toString());
    }

    public void a(CharSequence[] charSequenceArr) {
        this.h = charSequenceArr;
        ArrayAdapter arrayAdapter = this.f8880d;
        if (arrayAdapter instanceof a) {
            ((a) arrayAdapter).setEntries(this.h);
        } else {
            arrayAdapter.clear();
            this.f8880d.addAll(charSequenceArr);
            this.i = this.h;
        }
        notifyChanged();
    }

    public void b(String str) {
        boolean z = !TextUtils.equals(this.e, str);
        if (z || !this.f) {
            this.e = str;
            this.f = true;
            persistString(str);
            if (z) {
                notifyChanged();
            }
        }
    }

    public void b(CharSequence[] charSequenceArr) {
        ArrayAdapter arrayAdapter = this.f8880d;
        if (arrayAdapter instanceof a) {
            ((a) arrayAdapter).a(charSequenceArr);
            this.f8879c.notifyDataSetChanged();
            this.i = charSequenceArr;
        }
    }

    public CharSequence[] b() {
        return this.h;
    }

    public String c() {
        return this.e;
    }

    public int d() {
        return a(this.e);
    }

    /* access modifiers changed from: protected */
    public void notifyChanged() {
        super.notifyChanged();
        if (this.f8879c != null) {
            this.k.post(new d(this));
        }
    }

    public void onBindViewHolder(A a2) {
        if (this.f8879c.getCount() > 0) {
            this.g = (Spinner) a2.itemView.findViewById(x.spinner);
            a(this.g);
            this.g.setAdapter((SpinnerAdapter) this.f8879c);
            this.g.setOnItemSelectedListener(this.l);
            this.g.setSelection(c(c()));
            this.g.setOnSpinnerDismissListener(new e(this, a2));
            a2.itemView.setOnTouchListener(new f(this));
        }
        super.onBindViewHolder(a2);
    }

    /* access modifiers changed from: protected */
    public void onClick() {
        this.g.performClick();
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
        savedState.mValue = c();
        return savedState;
    }

    /* access modifiers changed from: protected */
    public void onSetInitialValue(boolean z, Object obj) {
        b(getPersistedString((String) obj));
    }

    /* access modifiers changed from: protected */
    public boolean shouldPersist() {
        return true;
    }
}
