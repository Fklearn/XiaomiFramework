package miui.vip;

import android.os.Parcel;
import android.os.Parcelable;

public class VipPhoneLevel implements Parcelable {
    public static final Parcelable.Creator<VipPhoneLevel> CREATOR = new Parcelable.Creator<VipPhoneLevel>() {
        public VipPhoneLevel createFromParcel(Parcel source) {
            VipPhoneLevel pl = new VipPhoneLevel();
            pl.phone = source.readString();
            pl.mid = source.readString();
            pl.midUpdateTime = source.readLong();
            pl.level = source.readInt();
            pl.levelUpdateTime = source.readLong();
            return pl;
        }

        public VipPhoneLevel[] newArray(int size) {
            return new VipPhoneLevel[size];
        }
    };
    public static final String UNKNOWN_ID = "-1";
    public static final int UNKNOWN_INT = -1;
    public int level = 1;
    public long levelUpdateTime = 0;
    public String mid;
    public long midUpdateTime = 0;
    public String phone;

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.phone);
        dest.writeString(this.mid);
        dest.writeLong(this.midUpdateTime);
        dest.writeInt(this.level);
        dest.writeLong(this.levelUpdateTime);
    }
}
