package com.miui.gamebooster.globalgame.view;

import android.os.Parcel;
import android.os.Parcelable;
import android.view.View;

class SavedState extends View.BaseSavedState {
    public static final Parcelable.Creator<SavedState> CREATOR = new f();
    private float rating;

    private SavedState(Parcel parcel) {
        super(parcel);
        this.rating = parcel.readFloat();
    }

    /* synthetic */ SavedState(Parcel parcel, f fVar) {
        this(parcel);
    }

    SavedState(Parcelable parcelable) {
        super(parcelable);
    }

    public float getRating() {
        return this.rating;
    }

    public void setRating(float f) {
        this.rating = f;
    }

    public void writeToParcel(Parcel parcel, int i) {
        super.writeToParcel(parcel, i);
        parcel.writeFloat(this.rating);
    }
}
