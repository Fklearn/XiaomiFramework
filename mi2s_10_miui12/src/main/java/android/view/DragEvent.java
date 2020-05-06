package android.view;

import android.annotation.UnsupportedAppUsage;
import android.content.ClipData;
import android.content.ClipDescription;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import com.android.internal.view.IDragAndDropPermissions;
import com.android.internal.view.IDragAndDropPermissions.Stub;

public class DragEvent
  implements Parcelable
{
  public static final int ACTION_DRAG_ENDED = 4;
  public static final int ACTION_DRAG_ENTERED = 5;
  public static final int ACTION_DRAG_EXITED = 6;
  public static final int ACTION_DRAG_LOCATION = 2;
  public static final int ACTION_DRAG_STARTED = 1;
  public static final int ACTION_DROP = 3;
  public static final Parcelable.Creator<DragEvent> CREATOR = new Parcelable.Creator()
  {
    public DragEvent createFromParcel(Parcel paramAnonymousParcel)
    {
      DragEvent localDragEvent = DragEvent.obtain();
      localDragEvent.mAction = paramAnonymousParcel.readInt();
      localDragEvent.mX = paramAnonymousParcel.readFloat();
      localDragEvent.mY = paramAnonymousParcel.readFloat();
      boolean bool;
      if (paramAnonymousParcel.readInt() != 0) {
        bool = true;
      } else {
        bool = false;
      }
      localDragEvent.mDragResult = bool;
      if (paramAnonymousParcel.readInt() != 0) {
        localDragEvent.mClipData = ((ClipData)ClipData.CREATOR.createFromParcel(paramAnonymousParcel));
      }
      if (paramAnonymousParcel.readInt() != 0) {
        localDragEvent.mClipDescription = ((ClipDescription)ClipDescription.CREATOR.createFromParcel(paramAnonymousParcel));
      }
      if (paramAnonymousParcel.readInt() != 0) {
        localDragEvent.mDragAndDropPermissions = IDragAndDropPermissions.Stub.asInterface(paramAnonymousParcel.readStrongBinder());
      }
      return localDragEvent;
    }
    
    public DragEvent[] newArray(int paramAnonymousInt)
    {
      return new DragEvent[paramAnonymousInt];
    }
  };
  private static final int MAX_RECYCLED = 10;
  private static final boolean TRACK_RECYCLED_LOCATION = false;
  private static final Object gRecyclerLock = new Object();
  private static DragEvent gRecyclerTop;
  private static int gRecyclerUsed = 0;
  int mAction;
  @UnsupportedAppUsage
  ClipData mClipData;
  @UnsupportedAppUsage
  ClipDescription mClipDescription;
  IDragAndDropPermissions mDragAndDropPermissions;
  boolean mDragResult;
  boolean mEventHandlerWasCalled;
  Object mLocalState;
  private DragEvent mNext;
  private boolean mRecycled;
  private RuntimeException mRecycledLocation;
  float mX;
  float mY;
  
  static
  {
    gRecyclerTop = null;
  }
  
  private void init(int paramInt, float paramFloat1, float paramFloat2, ClipDescription paramClipDescription, ClipData paramClipData, IDragAndDropPermissions paramIDragAndDropPermissions, Object paramObject, boolean paramBoolean)
  {
    this.mAction = paramInt;
    this.mX = paramFloat1;
    this.mY = paramFloat2;
    this.mClipDescription = paramClipDescription;
    this.mClipData = paramClipData;
    this.mDragAndDropPermissions = paramIDragAndDropPermissions;
    this.mLocalState = paramObject;
    this.mDragResult = paramBoolean;
  }
  
  static DragEvent obtain()
  {
    return obtain(0, 0.0F, 0.0F, null, null, null, null, false);
  }
  
  public static DragEvent obtain(int paramInt, float paramFloat1, float paramFloat2, Object paramObject, ClipDescription paramClipDescription, ClipData paramClipData, IDragAndDropPermissions paramIDragAndDropPermissions, boolean paramBoolean)
  {
    synchronized (gRecyclerLock)
    {
      if (gRecyclerTop == null)
      {
        localDragEvent = new android/view/DragEvent;
        localDragEvent.<init>();
        localDragEvent.init(paramInt, paramFloat1, paramFloat2, paramClipDescription, paramClipData, paramIDragAndDropPermissions, paramObject, paramBoolean);
        return localDragEvent;
      }
      DragEvent localDragEvent = gRecyclerTop;
      gRecyclerTop = localDragEvent.mNext;
      gRecyclerUsed -= 1;
      localDragEvent.mRecycledLocation = null;
      localDragEvent.mRecycled = false;
      localDragEvent.mNext = null;
      localDragEvent.init(paramInt, paramFloat1, paramFloat2, paramClipDescription, paramClipData, paramIDragAndDropPermissions, paramObject, paramBoolean);
      return localDragEvent;
    }
  }
  
  @UnsupportedAppUsage
  public static DragEvent obtain(DragEvent paramDragEvent)
  {
    return obtain(paramDragEvent.mAction, paramDragEvent.mX, paramDragEvent.mY, paramDragEvent.mLocalState, paramDragEvent.mClipDescription, paramDragEvent.mClipData, paramDragEvent.mDragAndDropPermissions, paramDragEvent.mDragResult);
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public int getAction()
  {
    return this.mAction;
  }
  
  public ClipData getClipData()
  {
    return this.mClipData;
  }
  
  public ClipDescription getClipDescription()
  {
    return this.mClipDescription;
  }
  
  public IDragAndDropPermissions getDragAndDropPermissions()
  {
    return this.mDragAndDropPermissions;
  }
  
  public Object getLocalState()
  {
    return this.mLocalState;
  }
  
  public boolean getResult()
  {
    return this.mDragResult;
  }
  
  public float getX()
  {
    return this.mX;
  }
  
  public float getY()
  {
    return this.mY;
  }
  
  public final void recycle()
  {
    if (!this.mRecycled)
    {
      this.mRecycled = true;
      this.mClipData = null;
      this.mClipDescription = null;
      this.mLocalState = null;
      this.mEventHandlerWasCalled = false;
      synchronized (gRecyclerLock)
      {
        if (gRecyclerUsed < 10)
        {
          gRecyclerUsed += 1;
          this.mNext = gRecyclerTop;
          gRecyclerTop = this;
        }
        return;
      }
    }
    ??? = new StringBuilder();
    ((StringBuilder)???).append(toString());
    ((StringBuilder)???).append(" recycled twice!");
    throw new RuntimeException(((StringBuilder)???).toString());
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("DragEvent{");
    localStringBuilder.append(Integer.toHexString(System.identityHashCode(this)));
    localStringBuilder.append(" action=");
    localStringBuilder.append(this.mAction);
    localStringBuilder.append(" @ (");
    localStringBuilder.append(this.mX);
    localStringBuilder.append(", ");
    localStringBuilder.append(this.mY);
    localStringBuilder.append(") desc=");
    localStringBuilder.append(this.mClipDescription);
    localStringBuilder.append(" data=");
    localStringBuilder.append(this.mClipData);
    localStringBuilder.append(" local=");
    localStringBuilder.append(this.mLocalState);
    localStringBuilder.append(" result=");
    localStringBuilder.append(this.mDragResult);
    localStringBuilder.append("}");
    return localStringBuilder.toString();
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeInt(this.mAction);
    paramParcel.writeFloat(this.mX);
    paramParcel.writeFloat(this.mY);
    paramParcel.writeInt(this.mDragResult);
    if (this.mClipData == null)
    {
      paramParcel.writeInt(0);
    }
    else
    {
      paramParcel.writeInt(1);
      this.mClipData.writeToParcel(paramParcel, paramInt);
    }
    if (this.mClipDescription == null)
    {
      paramParcel.writeInt(0);
    }
    else
    {
      paramParcel.writeInt(1);
      this.mClipDescription.writeToParcel(paramParcel, paramInt);
    }
    if (this.mDragAndDropPermissions == null)
    {
      paramParcel.writeInt(0);
    }
    else
    {
      paramParcel.writeInt(1);
      paramParcel.writeStrongBinder(this.mDragAndDropPermissions.asBinder());
    }
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/view/DragEvent.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */