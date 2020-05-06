package android.view;

import android.annotation.UnsupportedAppUsage;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

public final class WindowAnimationFrameStats
  extends FrameStats
  implements Parcelable
{
  public static final Parcelable.Creator<WindowAnimationFrameStats> CREATOR = new Parcelable.Creator()
  {
    public WindowAnimationFrameStats createFromParcel(Parcel paramAnonymousParcel)
    {
      return new WindowAnimationFrameStats(paramAnonymousParcel, null);
    }
    
    public WindowAnimationFrameStats[] newArray(int paramAnonymousInt)
    {
      return new WindowAnimationFrameStats[paramAnonymousInt];
    }
  };
  
  public WindowAnimationFrameStats() {}
  
  private WindowAnimationFrameStats(Parcel paramParcel)
  {
    this.mRefreshPeriodNano = paramParcel.readLong();
    this.mFramesPresentedTimeNano = paramParcel.createLongArray();
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  @UnsupportedAppUsage
  public void init(long paramLong, long[] paramArrayOfLong)
  {
    this.mRefreshPeriodNano = paramLong;
    this.mFramesPresentedTimeNano = paramArrayOfLong;
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder1 = new StringBuilder();
    localStringBuilder1.append("WindowAnimationFrameStats[");
    StringBuilder localStringBuilder2 = new StringBuilder();
    localStringBuilder2.append("frameCount:");
    localStringBuilder2.append(getFrameCount());
    localStringBuilder1.append(localStringBuilder2.toString());
    localStringBuilder2 = new StringBuilder();
    localStringBuilder2.append(", fromTimeNano:");
    localStringBuilder2.append(getStartTimeNano());
    localStringBuilder1.append(localStringBuilder2.toString());
    localStringBuilder2 = new StringBuilder();
    localStringBuilder2.append(", toTimeNano:");
    localStringBuilder2.append(getEndTimeNano());
    localStringBuilder1.append(localStringBuilder2.toString());
    localStringBuilder1.append(']');
    return localStringBuilder1.toString();
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeLong(this.mRefreshPeriodNano);
    paramParcel.writeLongArray(this.mFramesPresentedTimeNano);
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/view/WindowAnimationFrameStats.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */