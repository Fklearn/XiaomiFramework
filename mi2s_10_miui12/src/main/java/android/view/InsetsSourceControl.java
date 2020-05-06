package android.view;

import android.graphics.Point;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

public class InsetsSourceControl
  implements Parcelable
{
  public static final Parcelable.Creator<InsetsSourceControl> CREATOR = new Parcelable.Creator()
  {
    public InsetsSourceControl createFromParcel(Parcel paramAnonymousParcel)
    {
      return new InsetsSourceControl(paramAnonymousParcel);
    }
    
    public InsetsSourceControl[] newArray(int paramAnonymousInt)
    {
      return new InsetsSourceControl[paramAnonymousInt];
    }
  };
  private final SurfaceControl mLeash;
  private final Point mSurfacePosition;
  private final int mType;
  
  public InsetsSourceControl(int paramInt, SurfaceControl paramSurfaceControl, Point paramPoint)
  {
    this.mType = paramInt;
    this.mLeash = paramSurfaceControl;
    this.mSurfacePosition = paramPoint;
  }
  
  public InsetsSourceControl(Parcel paramParcel)
  {
    this.mType = paramParcel.readInt();
    this.mLeash = ((SurfaceControl)paramParcel.readParcelable(null));
    this.mSurfacePosition = ((Point)paramParcel.readParcelable(null));
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public SurfaceControl getLeash()
  {
    return this.mLeash;
  }
  
  public Point getSurfacePosition()
  {
    return this.mSurfacePosition;
  }
  
  public int getType()
  {
    return this.mType;
  }
  
  public boolean setSurfacePosition(int paramInt1, int paramInt2)
  {
    if (this.mSurfacePosition.equals(paramInt1, paramInt2)) {
      return false;
    }
    this.mSurfacePosition.set(paramInt1, paramInt2);
    return true;
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeInt(this.mType);
    paramParcel.writeParcelable(this.mLeash, 0);
    paramParcel.writeParcelable(this.mSurfacePosition, 0);
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/view/InsetsSourceControl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */