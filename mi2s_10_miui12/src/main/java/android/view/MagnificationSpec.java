package android.view;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.util.Pools.SynchronizedPool;

public class MagnificationSpec
  implements Parcelable
{
  public static final Parcelable.Creator<MagnificationSpec> CREATOR = new Parcelable.Creator()
  {
    public MagnificationSpec createFromParcel(Parcel paramAnonymousParcel)
    {
      MagnificationSpec localMagnificationSpec = MagnificationSpec.obtain();
      localMagnificationSpec.initFromParcel(paramAnonymousParcel);
      return localMagnificationSpec;
    }
    
    public MagnificationSpec[] newArray(int paramAnonymousInt)
    {
      return new MagnificationSpec[paramAnonymousInt];
    }
  };
  private static final int MAX_POOL_SIZE = 20;
  private static final Pools.SynchronizedPool<MagnificationSpec> sPool = new Pools.SynchronizedPool(20);
  public float offsetX;
  public float offsetY;
  public float scale = 1.0F;
  
  private void initFromParcel(Parcel paramParcel)
  {
    this.scale = paramParcel.readFloat();
    this.offsetX = paramParcel.readFloat();
    this.offsetY = paramParcel.readFloat();
  }
  
  public static MagnificationSpec obtain()
  {
    MagnificationSpec localMagnificationSpec = (MagnificationSpec)sPool.acquire();
    if (localMagnificationSpec == null) {
      localMagnificationSpec = new MagnificationSpec();
    }
    return localMagnificationSpec;
  }
  
  public static MagnificationSpec obtain(MagnificationSpec paramMagnificationSpec)
  {
    MagnificationSpec localMagnificationSpec = obtain();
    localMagnificationSpec.scale = paramMagnificationSpec.scale;
    localMagnificationSpec.offsetX = paramMagnificationSpec.offsetX;
    localMagnificationSpec.offsetY = paramMagnificationSpec.offsetY;
    return localMagnificationSpec;
  }
  
  public void clear()
  {
    this.scale = 1.0F;
    this.offsetX = 0.0F;
    this.offsetY = 0.0F;
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public boolean equals(Object paramObject)
  {
    boolean bool = true;
    if (this == paramObject) {
      return true;
    }
    if ((paramObject != null) && (getClass() == paramObject.getClass()))
    {
      paramObject = (MagnificationSpec)paramObject;
      if ((this.scale != ((MagnificationSpec)paramObject).scale) || (this.offsetX != ((MagnificationSpec)paramObject).offsetX) || (this.offsetY != ((MagnificationSpec)paramObject).offsetY)) {
        bool = false;
      }
      return bool;
    }
    return false;
  }
  
  public int hashCode()
  {
    float f = this.scale;
    int i = 0;
    int j;
    if (f != 0.0F) {
      j = Float.floatToIntBits(f);
    } else {
      j = 0;
    }
    f = this.offsetX;
    int k;
    if (f != 0.0F) {
      k = Float.floatToIntBits(f);
    } else {
      k = 0;
    }
    f = this.offsetY;
    if (f != 0.0F) {
      i = Float.floatToIntBits(f);
    }
    return (j * 31 + k) * 31 + i;
  }
  
  public void initialize(float paramFloat1, float paramFloat2, float paramFloat3)
  {
    if (paramFloat1 >= 1.0F)
    {
      this.scale = paramFloat1;
      this.offsetX = paramFloat2;
      this.offsetY = paramFloat3;
      return;
    }
    throw new IllegalArgumentException("Scale must be greater than or equal to one!");
  }
  
  public boolean isNop()
  {
    boolean bool;
    if ((this.scale == 1.0F) && (this.offsetX == 0.0F) && (this.offsetY == 0.0F)) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  public void recycle()
  {
    clear();
    sPool.release(this);
  }
  
  public void setTo(MagnificationSpec paramMagnificationSpec)
  {
    this.scale = paramMagnificationSpec.scale;
    this.offsetX = paramMagnificationSpec.offsetX;
    this.offsetY = paramMagnificationSpec.offsetY;
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("<scale:");
    localStringBuilder.append(Float.toString(this.scale));
    localStringBuilder.append(",offsetX:");
    localStringBuilder.append(Float.toString(this.offsetX));
    localStringBuilder.append(",offsetY:");
    localStringBuilder.append(Float.toString(this.offsetY));
    localStringBuilder.append(">");
    return localStringBuilder.toString();
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeFloat(this.scale);
    paramParcel.writeFloat(this.offsetX);
    paramParcel.writeFloat(this.offsetY);
    recycle();
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/view/MagnificationSpec.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */