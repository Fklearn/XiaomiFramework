package miui.vip;

import android.os.Parcel;
import android.os.Parcelable;

public class VipBanner implements Parcelable {
    public static final Parcelable.Creator<VipBanner> CREATOR = new Parcelable.Creator<VipBanner>() {
        public VipBanner createFromParcel(Parcel source) {
            return VipBanner.readFromParcel(source);
        }

        public VipBanner[] newArray(int size) {
            return new VipBanner[size];
        }
    };
    public String action;
    public String extraParams;
    public String icon;
    public long id;
    public String info;
    public String name;

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeString(this.icon);
        dest.writeString(this.name);
        dest.writeString(this.info);
        dest.writeString(this.action);
        dest.writeString(this.extraParams);
    }

    public static VipBanner readFromParcel(Parcel source) {
        VipBanner banner = new VipBanner();
        banner.id = source.readLong();
        banner.icon = source.readString();
        banner.name = source.readString();
        banner.info = source.readString();
        banner.action = source.readString();
        banner.extraParams = source.readString();
        return banner;
    }

    public String toString() {
        return "VipBanner{id=" + this.id + ", icon='" + this.icon + '\'' + ", name='" + this.name + '\'' + ", info='" + this.info + '\'' + ", action='" + this.action + '\'' + ", extraParams='" + this.extraParams + '\'' + '}';
    }
}
