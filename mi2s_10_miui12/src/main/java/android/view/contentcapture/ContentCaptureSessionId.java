package android.view.contentcapture;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import java.io.PrintWriter;

public final class ContentCaptureSessionId
  implements Parcelable
{
  public static final Parcelable.Creator<ContentCaptureSessionId> CREATOR = new Parcelable.Creator()
  {
    public ContentCaptureSessionId createFromParcel(Parcel paramAnonymousParcel)
    {
      return new ContentCaptureSessionId(paramAnonymousParcel.readInt());
    }
    
    public ContentCaptureSessionId[] newArray(int paramAnonymousInt)
    {
      return new ContentCaptureSessionId[paramAnonymousInt];
    }
  };
  private final int mValue;
  
  public ContentCaptureSessionId(int paramInt)
  {
    this.mValue = paramInt;
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public void dump(PrintWriter paramPrintWriter)
  {
    paramPrintWriter.print(this.mValue);
  }
  
  public boolean equals(Object paramObject)
  {
    if (this == paramObject) {
      return true;
    }
    if (paramObject == null) {
      return false;
    }
    if (getClass() != paramObject.getClass()) {
      return false;
    }
    paramObject = (ContentCaptureSessionId)paramObject;
    return this.mValue == ((ContentCaptureSessionId)paramObject).mValue;
  }
  
  public int getValue()
  {
    return this.mValue;
  }
  
  public int hashCode()
  {
    return 1 * 31 + this.mValue;
  }
  
  public String toString()
  {
    return Integer.toString(this.mValue);
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeInt(this.mValue);
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/view/contentcapture/ContentCaptureSessionId.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */