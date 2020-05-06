package miui.vip;

import android.os.Parcel;
import android.os.Parcelable;

public class VipAchievement implements Parcelable {
    public static final Parcelable.Creator<VipAchievement> CREATOR = new Parcelable.Creator<VipAchievement>() {
        public VipAchievement createFromParcel(Parcel source) {
            return VipAchievement.readFromParcel(source);
        }

        public VipAchievement[] newArray(int size) {
            return new VipAchievement[size];
        }
    };
    public long id;
    public boolean isOwned;
    public String name;
    public String url;

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeString(this.name);
        dest.writeString(this.url);
        dest.writeInt(this.isOwned ? 1 : 0);
    }

    public static VipAchievement readFromParcel(Parcel source) {
        VipAchievement info = new VipAchievement();
        info.id = source.readLong();
        info.name = source.readString();
        info.url = source.readString();
        boolean z = true;
        if (source.readInt() != 1) {
            z = false;
        }
        info.isOwned = z;
        return info;
    }
}
