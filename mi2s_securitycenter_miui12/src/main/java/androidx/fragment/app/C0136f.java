package androidx.fragment.app;

import android.os.Parcel;
import android.os.Parcelable;
import androidx.fragment.app.Fragment;

/* renamed from: androidx.fragment.app.f  reason: case insensitive filesystem */
class C0136f implements Parcelable.ClassLoaderCreator<Fragment.SavedState> {
    C0136f() {
    }

    public Fragment.SavedState createFromParcel(Parcel parcel) {
        return new Fragment.SavedState(parcel, (ClassLoader) null);
    }

    public Fragment.SavedState createFromParcel(Parcel parcel, ClassLoader classLoader) {
        return new Fragment.SavedState(parcel, classLoader);
    }

    public Fragment.SavedState[] newArray(int i) {
        return new Fragment.SavedState[i];
    }
}
