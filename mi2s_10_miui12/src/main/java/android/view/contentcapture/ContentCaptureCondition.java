package android.view.contentcapture;

import android.content.LocusId;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.util.DebugUtils;
import com.android.internal.util.Preconditions;
import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public final class ContentCaptureCondition
  implements Parcelable
{
  public static final Parcelable.Creator<ContentCaptureCondition> CREATOR = new Parcelable.Creator()
  {
    public ContentCaptureCondition createFromParcel(Parcel paramAnonymousParcel)
    {
      return new ContentCaptureCondition((LocusId)paramAnonymousParcel.readParcelable(null), paramAnonymousParcel.readInt());
    }
    
    public ContentCaptureCondition[] newArray(int paramAnonymousInt)
    {
      return new ContentCaptureCondition[paramAnonymousInt];
    }
  };
  public static final int FLAG_IS_REGEX = 2;
  private final int mFlags;
  private final LocusId mLocusId;
  
  public ContentCaptureCondition(LocusId paramLocusId, int paramInt)
  {
    this.mLocusId = ((LocusId)Preconditions.checkNotNull(paramLocusId));
    this.mFlags = paramInt;
  }
  
  public int describeContents()
  {
    return 0;
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
    paramObject = (ContentCaptureCondition)paramObject;
    if (this.mFlags != ((ContentCaptureCondition)paramObject).mFlags) {
      return false;
    }
    LocusId localLocusId = this.mLocusId;
    if (localLocusId == null)
    {
      if (((ContentCaptureCondition)paramObject).mLocusId != null) {
        return false;
      }
    }
    else if (!localLocusId.equals(((ContentCaptureCondition)paramObject).mLocusId)) {
      return false;
    }
    return true;
  }
  
  public int getFlags()
  {
    return this.mFlags;
  }
  
  public LocusId getLocusId()
  {
    return this.mLocusId;
  }
  
  public int hashCode()
  {
    int i = this.mFlags;
    LocusId localLocusId = this.mLocusId;
    int j;
    if (localLocusId == null) {
      j = 0;
    } else {
      j = localLocusId.hashCode();
    }
    return (1 * 31 + i) * 31 + j;
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder(this.mLocusId.toString());
    if (this.mFlags != 0)
    {
      localStringBuilder.append(" (");
      localStringBuilder.append(DebugUtils.flagsToString(ContentCaptureCondition.class, "FLAG_", this.mFlags));
      localStringBuilder.append(')');
    }
    return localStringBuilder.toString();
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeParcelable(this.mLocusId, paramInt);
    paramParcel.writeInt(this.mFlags);
  }
  
  @Retention(RetentionPolicy.SOURCE)
  static @interface Flags {}
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/view/contentcapture/ContentCaptureCondition.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */