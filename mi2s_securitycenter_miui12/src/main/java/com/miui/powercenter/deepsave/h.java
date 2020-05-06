package com.miui.powercenter.deepsave;

import android.os.Parcel;
import android.os.Parcelable;

class h implements Parcelable.Creator<IdeaModel> {
    h() {
    }

    public IdeaModel createFromParcel(Parcel parcel) {
        return new IdeaModel(parcel);
    }

    public IdeaModel[] newArray(int i) {
        return new IdeaModel[i];
    }
}
