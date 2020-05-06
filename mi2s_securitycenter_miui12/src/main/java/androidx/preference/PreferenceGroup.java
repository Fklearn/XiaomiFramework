package androidx.preference;

import a.c.i;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RestrictTo;
import androidx.core.content.res.h;
import androidx.preference.Preference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class PreferenceGroup extends Preference {

    /* renamed from: a  reason: collision with root package name */
    final i<String, Long> f1019a;

    /* renamed from: b  reason: collision with root package name */
    private final Handler f1020b;

    /* renamed from: c  reason: collision with root package name */
    private List<Preference> f1021c;

    /* renamed from: d  reason: collision with root package name */
    private boolean f1022d;
    private int e;
    private boolean f;
    private int g;
    private a h;
    private final Runnable i;

    static class SavedState extends Preference.BaseSavedState {
        public static final Parcelable.Creator<SavedState> CREATOR = new t();
        int mInitialExpandedChildrenCount;

        SavedState(Parcel parcel) {
            super(parcel);
            this.mInitialExpandedChildrenCount = parcel.readInt();
        }

        SavedState(Parcelable parcelable, int i) {
            super(parcelable);
            this.mInitialExpandedChildrenCount = i;
        }

        public void writeToParcel(Parcel parcel, int i) {
            super.writeToParcel(parcel, i);
            parcel.writeInt(this.mInitialExpandedChildrenCount);
        }
    }

    @RestrictTo({RestrictTo.a.LIBRARY_GROUP_PREFIX})
    public interface a {
        void a();
    }

    public interface b {
        int a(String str);

        int b(Preference preference);
    }

    public PreferenceGroup(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public PreferenceGroup(Context context, AttributeSet attributeSet, int i2) {
        this(context, attributeSet, i2, 0);
    }

    public PreferenceGroup(Context context, AttributeSet attributeSet, int i2, int i3) {
        super(context, attributeSet, i2, i3);
        this.f1019a = new i<>();
        this.f1020b = new Handler();
        this.f1022d = true;
        this.e = 0;
        this.f = false;
        this.g = Integer.MAX_VALUE;
        this.h = null;
        this.i = new s(this);
        this.f1021c = new ArrayList();
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, I.PreferenceGroup, i2, i3);
        int i4 = I.PreferenceGroup_orderingFromXml;
        this.f1022d = h.a(obtainStyledAttributes, i4, i4, true);
        if (obtainStyledAttributes.hasValue(I.PreferenceGroup_initialExpandedChildrenCount)) {
            int i5 = I.PreferenceGroup_initialExpandedChildrenCount;
            b(h.a(obtainStyledAttributes, i5, i5, Integer.MAX_VALUE));
        }
        obtainStyledAttributes.recycle();
    }

    private boolean e(Preference preference) {
        boolean remove;
        synchronized (this) {
            preference.onPrepareForRemoval();
            if (preference.getParent() == this) {
                preference.assignParent((PreferenceGroup) null);
            }
            remove = this.f1021c.remove(preference);
            if (remove) {
                String key = preference.getKey();
                if (key != null) {
                    this.f1019a.put(key, Long.valueOf(preference.getId()));
                    this.f1020b.removeCallbacks(this.i);
                    this.f1020b.post(this.i);
                }
                if (this.f) {
                    preference.onDetached();
                }
            }
        }
        return remove;
    }

    public int a() {
        return this.g;
    }

    public Preference a(int i2) {
        return this.f1021c.get(i2);
    }

    @Nullable
    public <T extends Preference> T a(@NonNull CharSequence charSequence) {
        T a2;
        if (charSequence == null) {
            throw new IllegalArgumentException("Key cannot be null");
        } else if (TextUtils.equals(getKey(), charSequence)) {
            return this;
        } else {
            int c2 = c();
            for (int i2 = 0; i2 < c2; i2++) {
                T a3 = a(i2);
                if (TextUtils.equals(a3.getKey(), charSequence)) {
                    return a3;
                }
                if ((a3 instanceof PreferenceGroup) && (a2 = ((PreferenceGroup) a3).a(charSequence)) != null) {
                    return a2;
                }
            }
            return null;
        }
    }

    public void a(Preference preference) {
        b(preference);
    }

    public void a(boolean z) {
        this.f1022d = z;
    }

    @Nullable
    @RestrictTo({RestrictTo.a.LIBRARY_GROUP_PREFIX})
    public a b() {
        return this.h;
    }

    public void b(int i2) {
        if (i2 != Integer.MAX_VALUE && !hasKey()) {
            Log.e("PreferenceGroup", getClass().getSimpleName() + " should have a key defined if it contains an expandable preference");
        }
        this.g = i2;
    }

    public boolean b(Preference preference) {
        long j;
        if (this.f1021c.contains(preference)) {
            return true;
        }
        if (preference.getKey() != null) {
            PreferenceGroup preferenceGroup = this;
            while (preferenceGroup.getParent() != null) {
                preferenceGroup = preferenceGroup.getParent();
            }
            String key = preference.getKey();
            if (preferenceGroup.a((CharSequence) key) != null) {
                Log.e("PreferenceGroup", "Found duplicated key: \"" + key + "\". This can cause unintended behaviour, please use unique keys for every preference.");
            }
        }
        if (preference.getOrder() == Integer.MAX_VALUE) {
            if (this.f1022d) {
                int i2 = this.e;
                this.e = i2 + 1;
                preference.setOrder(i2);
            }
            if (preference instanceof PreferenceGroup) {
                ((PreferenceGroup) preference).a(this.f1022d);
            }
        }
        int binarySearch = Collections.binarySearch(this.f1021c, preference);
        if (binarySearch < 0) {
            binarySearch = (binarySearch * -1) - 1;
        }
        if (!c(preference)) {
            return false;
        }
        synchronized (this) {
            this.f1021c.add(binarySearch, preference);
        }
        z preferenceManager = getPreferenceManager();
        String key2 = preference.getKey();
        if (key2 == null || !this.f1019a.containsKey(key2)) {
            j = preferenceManager.c();
        } else {
            j = this.f1019a.get(key2).longValue();
            this.f1019a.remove(key2);
        }
        preference.onAttachedToHierarchy(preferenceManager, j);
        preference.assignParent(this);
        if (this.f) {
            preference.onAttached();
        }
        notifyHierarchyChanged();
        return true;
    }

    public int c() {
        return this.f1021c.size();
    }

    /* access modifiers changed from: protected */
    public boolean c(Preference preference) {
        preference.onParentChanged(this, shouldDisableDependents());
        return true;
    }

    /* access modifiers changed from: protected */
    public boolean d() {
        return true;
    }

    public boolean d(Preference preference) {
        boolean e2 = e(preference);
        notifyHierarchyChanged();
        return e2;
    }

    /* access modifiers changed from: protected */
    public void dispatchRestoreInstanceState(Bundle bundle) {
        super.dispatchRestoreInstanceState(bundle);
        int c2 = c();
        for (int i2 = 0; i2 < c2; i2++) {
            a(i2).dispatchRestoreInstanceState(bundle);
        }
    }

    /* access modifiers changed from: protected */
    public void dispatchSaveInstanceState(Bundle bundle) {
        super.dispatchSaveInstanceState(bundle);
        int c2 = c();
        for (int i2 = 0; i2 < c2; i2++) {
            a(i2).dispatchSaveInstanceState(bundle);
        }
    }

    public void e() {
        synchronized (this) {
            List<Preference> list = this.f1021c;
            for (int size = list.size() - 1; size >= 0; size--) {
                e(list.get(0));
            }
        }
        notifyHierarchyChanged();
    }

    /* access modifiers changed from: package-private */
    public void f() {
        synchronized (this) {
            Collections.sort(this.f1021c);
        }
    }

    public void notifyDependencyChange(boolean z) {
        super.notifyDependencyChange(z);
        int c2 = c();
        for (int i2 = 0; i2 < c2; i2++) {
            a(i2).onParentChanged(this, z);
        }
    }

    public void onAttached() {
        super.onAttached();
        this.f = true;
        int c2 = c();
        for (int i2 = 0; i2 < c2; i2++) {
            a(i2).onAttached();
        }
    }

    public void onDetached() {
        super.onDetached();
        this.f = false;
        int c2 = c();
        for (int i2 = 0; i2 < c2; i2++) {
            a(i2).onDetached();
        }
    }

    /* access modifiers changed from: protected */
    public void onRestoreInstanceState(Parcelable parcelable) {
        if (parcelable == null || !parcelable.getClass().equals(SavedState.class)) {
            super.onRestoreInstanceState(parcelable);
            return;
        }
        SavedState savedState = (SavedState) parcelable;
        this.g = savedState.mInitialExpandedChildrenCount;
        super.onRestoreInstanceState(savedState.getSuperState());
    }

    /* access modifiers changed from: protected */
    public Parcelable onSaveInstanceState() {
        return new SavedState(super.onSaveInstanceState(), this.g);
    }
}
