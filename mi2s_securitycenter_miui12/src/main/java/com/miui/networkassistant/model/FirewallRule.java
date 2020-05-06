package com.miui.networkassistant.model;

import android.os.Parcel;
import android.os.Parcelable;

public enum FirewallRule implements Parcelable {
    Init(-1),
    Allow(0),
    Restrict(1),
    Alert(2);
    
    public static final Parcelable.Creator<FirewallRule> CREATOR = null;
    private final int value;

    static {
        CREATOR = new Parcelable.Creator<FirewallRule>() {
            public FirewallRule createFromParcel(Parcel parcel) {
                return FirewallRule.parse(parcel.readInt());
            }

            public FirewallRule[] newArray(int i) {
                return new FirewallRule[i];
            }
        };
    }

    private FirewallRule(int i) {
        this.value = i;
    }

    public static FirewallRule parse(int i) {
        return i != -1 ? i != 0 ? i != 1 ? i != 2 ? Allow : Alert : Restrict : Allow : Init;
    }

    public int describeContents() {
        return 0;
    }

    public String toString() {
        return String.valueOf(this.value);
    }

    public int value() {
        return this.value;
    }

    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(this.value);
    }
}
