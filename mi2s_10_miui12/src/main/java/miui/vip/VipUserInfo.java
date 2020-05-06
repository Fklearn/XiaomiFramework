package miui.vip;

import android.os.Parcel;
import android.os.Parcelable;
import miui.telephony.phonenumber.Prefix;

public class VipUserInfo implements Parcelable {
    public static final Parcelable.Creator<VipUserInfo> CREATOR = new Parcelable.Creator<VipUserInfo>() {
        public VipUserInfo createFromParcel(Parcel source) {
            return VipUserInfo.readFromParcel(source);
        }

        public VipUserInfo[] newArray(int size) {
            return new VipUserInfo[size];
        }
    };
    public String badgeTxt = Prefix.EMPTY;
    public int dailyTaskLimit;
    public int hasNewAwards;
    public int level;
    public String levelTxt = Prefix.EMPTY;
    public long registerTime;
    public int score;
    public int scoreToNextLevel;
    public String taskTxt = Prefix.EMPTY;
    public String timezone;
    public int todayCompleteTaskCount;
    public int todayScore;
    public int totalScore;
    public int userId;

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.userId);
        dest.writeInt(this.level);
        dest.writeInt(this.score);
        dest.writeInt(this.scoreToNextLevel);
        dest.writeInt(this.totalScore);
        dest.writeInt(this.todayScore);
        dest.writeInt(this.dailyTaskLimit);
        dest.writeInt(this.todayCompleteTaskCount);
        dest.writeString(this.timezone);
        dest.writeString(this.levelTxt);
        dest.writeString(this.taskTxt);
        dest.writeString(this.badgeTxt);
        dest.writeLong(this.registerTime);
        dest.writeInt(this.hasNewAwards);
    }

    public static VipUserInfo readFromParcel(Parcel source) {
        VipUserInfo user = new VipUserInfo();
        user.userId = source.readInt();
        user.level = source.readInt();
        user.score = source.readInt();
        user.scoreToNextLevel = source.readInt();
        user.totalScore = source.readInt();
        user.todayScore = source.readInt();
        user.dailyTaskLimit = source.readInt();
        user.todayCompleteTaskCount = source.readInt();
        user.timezone = source.readString();
        user.levelTxt = source.readString();
        user.taskTxt = source.readString();
        user.badgeTxt = source.readString();
        user.registerTime = source.readLong();
        user.hasNewAwards = source.readInt();
        return user;
    }

    public String toString() {
        return "VipUserInfo{userId=" + this.userId + ", level=" + this.level + ", score=" + this.score + ", scoreToNextLevel=" + this.scoreToNextLevel + ", totalScore=" + this.totalScore + ", todayScore=" + this.todayScore + ", dailyTaskLimit=" + this.dailyTaskLimit + ", todayCompleteTaskCount=" + this.todayCompleteTaskCount + ", timezone='" + this.timezone + '\'' + ", levelTxt='" + this.levelTxt + '\'' + ", taskTxt='" + this.taskTxt + '\'' + ", badgeTxt='" + this.badgeTxt + '\'' + ", registerTime=" + this.registerTime + ", hasNewAwards=" + this.hasNewAwards + '}';
    }
}
