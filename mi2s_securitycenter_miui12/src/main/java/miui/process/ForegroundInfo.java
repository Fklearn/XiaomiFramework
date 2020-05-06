package miui.process;

import android.os.Parcel;
import android.os.Parcelable;

public class ForegroundInfo implements Parcelable {
    public static final Parcelable.Creator<ForegroundInfo> CREATOR = new Parcelable.Creator<ForegroundInfo>() {
        public ForegroundInfo createFromParcel(Parcel parcel) {
            return new ForegroundInfo(parcel);
        }

        public ForegroundInfo[] newArray(int i) {
            return new ForegroundInfo[i];
        }
    };
    public String mForegroundPackageName;
    public int mForegroundPid = -1;
    public int mForegroundUid = -1;
    public String mLastForegroundPackageName;
    public int mLastForegroundPid = -1;
    public int mLastForegroundUid = -1;

    protected ForegroundInfo(Parcel parcel) {
    }

    public int describeContents() {
        return 0;
    }

    public boolean isColdStart() {
        return false;
    }

    public void writeToParcel(Parcel parcel, int i) {
    }
}
