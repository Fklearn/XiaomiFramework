package android.view;

import android.annotation.UnsupportedAppUsage;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class InputEvent
  implements Parcelable
{
  public static final Parcelable.Creator<InputEvent> CREATOR = new Parcelable.Creator()
  {
    public InputEvent createFromParcel(Parcel paramAnonymousParcel)
    {
      int i = paramAnonymousParcel.readInt();
      if (i == 2) {
        return KeyEvent.createFromParcelBody(paramAnonymousParcel);
      }
      if (i == 1) {
        return MotionEvent.createFromParcelBody(paramAnonymousParcel);
      }
      throw new IllegalStateException("Unexpected input event type token in parcel.");
    }
    
    public InputEvent[] newArray(int paramAnonymousInt)
    {
      return new InputEvent[paramAnonymousInt];
    }
  };
  protected static final int PARCEL_TOKEN_KEY_EVENT = 2;
  protected static final int PARCEL_TOKEN_MOTION_EVENT = 1;
  private static final boolean TRACK_RECYCLED_LOCATION = false;
  private static final AtomicInteger mNextSeq = new AtomicInteger();
  protected boolean mRecycled;
  private RuntimeException mRecycledLocation;
  protected int mSeq = mNextSeq.getAndIncrement();
  
  public abstract void cancel();
  
  public abstract InputEvent copy();
  
  public int describeContents()
  {
    return 0;
  }
  
  public final InputDevice getDevice()
  {
    return InputDevice.getDevice(getDeviceId());
  }
  
  public abstract int getDeviceId();
  
  public abstract int getDisplayId();
  
  public abstract long getEventTime();
  
  public abstract long getEventTimeNano();
  
  @UnsupportedAppUsage
  public int getSequenceNumber()
  {
    return this.mSeq;
  }
  
  public abstract int getSource();
  
  public boolean isFromSource(int paramInt)
  {
    boolean bool;
    if ((getSource() & paramInt) == paramInt) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  public abstract boolean isTainted();
  
  protected void prepareForReuse()
  {
    this.mRecycled = false;
    this.mRecycledLocation = null;
    this.mSeq = mNextSeq.getAndIncrement();
  }
  
  public void recycle()
  {
    if (!this.mRecycled)
    {
      this.mRecycled = true;
      return;
    }
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append(toString());
    localStringBuilder.append(" recycled twice!");
    throw new RuntimeException(localStringBuilder.toString());
  }
  
  public void recycleIfNeededAfterDispatch()
  {
    recycle();
  }
  
  public abstract void setDisplayId(int paramInt);
  
  public abstract void setSource(int paramInt);
  
  public abstract void setTainted(boolean paramBoolean);
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/view/InputEvent.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */