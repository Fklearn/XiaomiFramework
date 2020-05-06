package android.view;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

public abstract class DisplayAddress
  implements Parcelable
{
  public static Network fromMacAddress(String paramString)
  {
    return new Network(paramString, null);
  }
  
  public static Physical fromPhysicalDisplayId(long paramLong)
  {
    return new Physical(paramLong, null);
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public static final class Network
    extends DisplayAddress
  {
    public static final Parcelable.Creator<Network> CREATOR = new Parcelable.Creator()
    {
      public DisplayAddress.Network createFromParcel(Parcel paramAnonymousParcel)
      {
        return new DisplayAddress.Network(paramAnonymousParcel.readString(), null);
      }
      
      public DisplayAddress.Network[] newArray(int paramAnonymousInt)
      {
        return new DisplayAddress.Network[paramAnonymousInt];
      }
    };
    private final String mMacAddress;
    
    private Network(String paramString)
    {
      this.mMacAddress = paramString;
    }
    
    public boolean equals(Object paramObject)
    {
      boolean bool;
      if (((paramObject instanceof Network)) && (this.mMacAddress.equals(((Network)paramObject).mMacAddress))) {
        bool = true;
      } else {
        bool = false;
      }
      return bool;
    }
    
    public int hashCode()
    {
      return this.mMacAddress.hashCode();
    }
    
    public String toString()
    {
      return this.mMacAddress;
    }
    
    public void writeToParcel(Parcel paramParcel, int paramInt)
    {
      paramParcel.writeString(this.mMacAddress);
    }
  }
  
  public static final class Physical
    extends DisplayAddress
  {
    public static final Parcelable.Creator<Physical> CREATOR = new Parcelable.Creator()
    {
      public DisplayAddress.Physical createFromParcel(Parcel paramAnonymousParcel)
      {
        return new DisplayAddress.Physical(paramAnonymousParcel.readLong(), null);
      }
      
      public DisplayAddress.Physical[] newArray(int paramAnonymousInt)
      {
        return new DisplayAddress.Physical[paramAnonymousInt];
      }
    };
    private static final int MODEL_SHIFT = 8;
    private static final int PORT_MASK = 255;
    private static final long UNKNOWN_MODEL = 0L;
    private final long mPhysicalDisplayId;
    
    private Physical(long paramLong)
    {
      this.mPhysicalDisplayId = paramLong;
    }
    
    public boolean equals(Object paramObject)
    {
      boolean bool;
      if (((paramObject instanceof Physical)) && (this.mPhysicalDisplayId == ((Physical)paramObject).mPhysicalDisplayId)) {
        bool = true;
      } else {
        bool = false;
      }
      return bool;
    }
    
    public Long getModel()
    {
      long l = this.mPhysicalDisplayId >>> 8;
      Long localLong;
      if (l == 0L) {
        localLong = null;
      } else {
        localLong = Long.valueOf(l);
      }
      return localLong;
    }
    
    public byte getPort()
    {
      return (byte)(int)this.mPhysicalDisplayId;
    }
    
    public int hashCode()
    {
      return Long.hashCode(this.mPhysicalDisplayId);
    }
    
    public String toString()
    {
      Object localObject = new StringBuilder("{");
      ((StringBuilder)localObject).append("port=");
      StringBuilder localStringBuilder = ((StringBuilder)localObject).append(getPort() & 0xFF);
      localObject = getModel();
      if (localObject != null)
      {
        localStringBuilder.append(", model=0x");
        localStringBuilder.append(Long.toHexString(((Long)localObject).longValue()));
      }
      localStringBuilder.append("}");
      return localStringBuilder.toString();
    }
    
    public void writeToParcel(Parcel paramParcel, int paramInt)
    {
      paramParcel.writeLong(this.mPhysicalDisplayId);
    }
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/view/DisplayAddress.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */