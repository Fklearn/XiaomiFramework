package androidx.drawerlayout.widget;

import android.os.Parcel;
import android.os.Parcelable;

class a implements Parcelable.ClassLoaderCreator<DrawerLayout$SavedState> {
    a() {
    }

    public DrawerLayout$SavedState createFromParcel(Parcel parcel) {
        return new DrawerLayout$SavedState(parcel, (ClassLoader) null);
    }

    public DrawerLayout$SavedState createFromParcel(Parcel parcel, ClassLoader classLoader) {
        return new DrawerLayout$SavedState(parcel, classLoader);
    }

    public DrawerLayout$SavedState[] newArray(int i) {
        return new DrawerLayout$SavedState[i];
    }
}
