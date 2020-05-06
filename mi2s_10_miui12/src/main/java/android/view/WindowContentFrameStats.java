package android.view;

import android.annotation.UnsupportedAppUsage;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

public final class WindowContentFrameStats
  extends FrameStats
  implements Parcelable
{
  public static final Parcelable.Creator<WindowContentFrameStats> CREATOR = new Parcelable.Creator()
  {
    public WindowContentFrameStats createFromParcel(Parcel paramAnonymousParcel)
    {
      return new WindowContentFrameStats(paramAnonymousParcel, null);
    }
    
    public WindowContentFrameStats[] newArray(int paramAnonymousInt)
    {
      return new WindowContentFrameStats[paramAnonymousInt];
    }
  };
  private long[] mFramesPostedTimeNano;
  private long[] mFramesReadyTimeNano;
  
  public WindowContentFrameStats() {}
  
  private WindowContentFrameStats(Parcel paramParcel)
  {
    this.mRefreshPeriodNano = paramParcel.readLong();
    this.mFramesPostedTimeNano = paramParcel.createLongArray();
    this.mFramesPresentedTimeNano = paramParcel.createLongArray();
    this.mFramesReadyTimeNano = paramParcel.createLongArray();
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public long getFramePostedTimeNano(int paramInt)
  {
    long[] arrayOfLong = this.mFramesPostedTimeNano;
    if (arrayOfLong != null) {
      return arrayOfLong[paramInt];
    }
    throw new IndexOutOfBoundsException();
  }
  
  public long getFrameReadyTimeNano(int paramInt)
  {
    long[] arrayOfLong = this.mFramesReadyTimeNano;
    if (arrayOfLong != null) {
      return arrayOfLong[paramInt];
    }
    throw new IndexOutOfBoundsException();
  }
  
  @UnsupportedAppUsage
  public void init(long paramLong, long[] paramArrayOfLong1, long[] paramArrayOfLong2, long[] paramArrayOfLong3)
  {
    this.mRefreshPeriodNano = paramLong;
    this.mFramesPostedTimeNano = paramArrayOfLong1;
    this.mFramesPresentedTimeNano = paramArrayOfLong2;
    this.mFramesReadyTimeNano = paramArrayOfLong3;
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder1 = new StringBuilder();
    localStringBuilder1.append("WindowContentFrameStats[");
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
    paramParcel.writeLongArray(this.mFramesPostedTimeNano);
    paramParcel.writeLongArray(this.mFramesPresentedTimeNano);
    paramParcel.writeLongArray(this.mFramesReadyTimeNano);
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/view/WindowContentFrameStats.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */