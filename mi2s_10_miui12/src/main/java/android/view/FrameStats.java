package android.view;

public abstract class FrameStats
{
  public static final long UNDEFINED_TIME_NANO = -1L;
  protected long[] mFramesPresentedTimeNano;
  protected long mRefreshPeriodNano;
  
  public final long getEndTimeNano()
  {
    if (getFrameCount() <= 0) {
      return -1L;
    }
    long[] arrayOfLong = this.mFramesPresentedTimeNano;
    return arrayOfLong[(arrayOfLong.length - 1)];
  }
  
  public final int getFrameCount()
  {
    long[] arrayOfLong = this.mFramesPresentedTimeNano;
    int i;
    if (arrayOfLong != null) {
      i = arrayOfLong.length;
    } else {
      i = 0;
    }
    return i;
  }
  
  public final long getFramePresentedTimeNano(int paramInt)
  {
    long[] arrayOfLong = this.mFramesPresentedTimeNano;
    if (arrayOfLong != null) {
      return arrayOfLong[paramInt];
    }
    throw new IndexOutOfBoundsException();
  }
  
  public final long getRefreshPeriodNano()
  {
    return this.mRefreshPeriodNano;
  }
  
  public final long getStartTimeNano()
  {
    if (getFrameCount() <= 0) {
      return -1L;
    }
    return this.mFramesPresentedTimeNano[0];
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/view/FrameStats.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */