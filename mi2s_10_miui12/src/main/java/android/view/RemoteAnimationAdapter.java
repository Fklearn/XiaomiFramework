package android.view;

import android.annotation.UnsupportedAppUsage;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

public class RemoteAnimationAdapter
  implements Parcelable
{
  public static final Parcelable.Creator<RemoteAnimationAdapter> CREATOR = new Parcelable.Creator()
  {
    public RemoteAnimationAdapter createFromParcel(Parcel paramAnonymousParcel)
    {
      return new RemoteAnimationAdapter(paramAnonymousParcel);
    }
    
    public RemoteAnimationAdapter[] newArray(int paramAnonymousInt)
    {
      return new RemoteAnimationAdapter[paramAnonymousInt];
    }
  };
  private int mCallingPid;
  private final boolean mChangeNeedsSnapshot;
  private final long mDuration;
  private final IRemoteAnimationRunner mRunner;
  private final long mStatusBarTransitionDelay;
  
  public RemoteAnimationAdapter(Parcel paramParcel)
  {
    this.mRunner = IRemoteAnimationRunner.Stub.asInterface(paramParcel.readStrongBinder());
    this.mDuration = paramParcel.readLong();
    this.mStatusBarTransitionDelay = paramParcel.readLong();
    this.mChangeNeedsSnapshot = paramParcel.readBoolean();
  }
  
  @UnsupportedAppUsage
  public RemoteAnimationAdapter(IRemoteAnimationRunner paramIRemoteAnimationRunner, long paramLong1, long paramLong2)
  {
    this(paramIRemoteAnimationRunner, paramLong1, paramLong2, false);
  }
  
  @UnsupportedAppUsage
  public RemoteAnimationAdapter(IRemoteAnimationRunner paramIRemoteAnimationRunner, long paramLong1, long paramLong2, boolean paramBoolean)
  {
    this.mRunner = paramIRemoteAnimationRunner;
    this.mDuration = paramLong1;
    this.mChangeNeedsSnapshot = paramBoolean;
    this.mStatusBarTransitionDelay = paramLong2;
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public int getCallingPid()
  {
    return this.mCallingPid;
  }
  
  public boolean getChangeNeedsSnapshot()
  {
    return this.mChangeNeedsSnapshot;
  }
  
  public long getDuration()
  {
    return this.mDuration;
  }
  
  public IRemoteAnimationRunner getRunner()
  {
    return this.mRunner;
  }
  
  public long getStatusBarTransitionDelay()
  {
    return this.mStatusBarTransitionDelay;
  }
  
  public void setCallingPid(int paramInt)
  {
    this.mCallingPid = paramInt;
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeStrongInterface(this.mRunner);
    paramParcel.writeLong(this.mDuration);
    paramParcel.writeLong(this.mStatusBarTransitionDelay);
    paramParcel.writeBoolean(this.mChangeNeedsSnapshot);
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/view/RemoteAnimationAdapter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */