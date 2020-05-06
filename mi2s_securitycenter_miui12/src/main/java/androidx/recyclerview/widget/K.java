package androidx.recyclerview.widget;

import android.os.Parcel;
import android.os.Parcelable;
import androidx.recyclerview.widget.RecyclerView;

class K implements Parcelable.ClassLoaderCreator<RecyclerView.SavedState> {
    K() {
    }

    public RecyclerView.SavedState createFromParcel(Parcel parcel) {
        return new RecyclerView.SavedState(parcel, (ClassLoader) null);
    }

    public RecyclerView.SavedState createFromParcel(Parcel parcel, ClassLoader classLoader) {
        return new RecyclerView.SavedState(parcel, classLoader);
    }

    public RecyclerView.SavedState[] newArray(int i) {
        return new RecyclerView.SavedState[i];
    }
}
