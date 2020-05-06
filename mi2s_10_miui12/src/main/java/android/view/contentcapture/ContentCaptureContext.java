package android.view.contentcapture;

import android.annotation.SystemApi;
import android.content.ComponentName;
import android.content.LocusId;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import com.android.internal.util.Preconditions;
import java.io.PrintWriter;
import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public final class ContentCaptureContext
  implements Parcelable
{
  public static final Parcelable.Creator<ContentCaptureContext> CREATOR = new Parcelable.Creator()
  {
    public ContentCaptureContext createFromParcel(Parcel paramAnonymousParcel)
    {
      int i = paramAnonymousParcel.readInt();
      int j = 1;
      if (i != 1) {
        j = 0;
      }
      Object localObject2;
      if (j != 0)
      {
        localObject1 = (LocusId)paramAnonymousParcel.readParcelable(null);
        localObject2 = paramAnonymousParcel.readBundle();
        localObject1 = new ContentCaptureContext.Builder((LocusId)localObject1);
        if (localObject2 != null) {
          ((ContentCaptureContext.Builder)localObject1).setExtras((Bundle)localObject2);
        }
        localObject2 = new ContentCaptureContext((ContentCaptureContext.Builder)localObject1, null);
      }
      else
      {
        localObject2 = null;
      }
      Object localObject1 = (ComponentName)paramAnonymousParcel.readParcelable(null);
      if (localObject1 == null) {
        return (ContentCaptureContext)localObject2;
      }
      return new ContentCaptureContext((ContentCaptureContext)localObject2, (ComponentName)localObject1, paramAnonymousParcel.readInt(), paramAnonymousParcel.readInt(), paramAnonymousParcel.readInt());
    }
    
    public ContentCaptureContext[] newArray(int paramAnonymousInt)
    {
      return new ContentCaptureContext[paramAnonymousInt];
    }
  };
  @SystemApi
  public static final int FLAG_DISABLED_BY_APP = 1;
  @SystemApi
  public static final int FLAG_DISABLED_BY_FLAG_SECURE = 2;
  @SystemApi
  public static final int FLAG_RECONNECTED = 4;
  private final ComponentName mComponentName;
  private final int mDisplayId;
  private final Bundle mExtras;
  private final int mFlags;
  private final boolean mHasClientContext;
  private final LocusId mId;
  private int mParentSessionId = 0;
  private final int mTaskId;
  
  private ContentCaptureContext(Builder paramBuilder)
  {
    this.mHasClientContext = true;
    this.mExtras = paramBuilder.mExtras;
    this.mId = paramBuilder.mId;
    this.mComponentName = null;
    this.mFlags = 0;
    this.mTaskId = 0;
    this.mDisplayId = -1;
  }
  
  public ContentCaptureContext(ContentCaptureContext paramContentCaptureContext, int paramInt)
  {
    this.mHasClientContext = paramContentCaptureContext.mHasClientContext;
    this.mExtras = paramContentCaptureContext.mExtras;
    this.mId = paramContentCaptureContext.mId;
    this.mComponentName = paramContentCaptureContext.mComponentName;
    this.mTaskId = paramContentCaptureContext.mTaskId;
    paramContentCaptureContext.mFlags |= paramInt;
    this.mDisplayId = paramContentCaptureContext.mDisplayId;
  }
  
  public ContentCaptureContext(ContentCaptureContext paramContentCaptureContext, ComponentName paramComponentName, int paramInt1, int paramInt2, int paramInt3)
  {
    if (paramContentCaptureContext != null)
    {
      this.mHasClientContext = true;
      this.mExtras = paramContentCaptureContext.mExtras;
      this.mId = paramContentCaptureContext.mId;
    }
    else
    {
      this.mHasClientContext = false;
      this.mExtras = null;
      this.mId = null;
    }
    this.mComponentName = ((ComponentName)Preconditions.checkNotNull(paramComponentName));
    this.mTaskId = paramInt1;
    this.mDisplayId = paramInt2;
    this.mFlags = paramInt3;
  }
  
  public static ContentCaptureContext forLocusId(String paramString)
  {
    return new Builder(new LocusId(paramString)).build();
  }
  
  private boolean fromServer()
  {
    boolean bool;
    if (this.mComponentName != null) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public void dump(PrintWriter paramPrintWriter)
  {
    if (this.mComponentName != null)
    {
      paramPrintWriter.print("activity=");
      paramPrintWriter.print(this.mComponentName.flattenToShortString());
    }
    if (this.mId != null)
    {
      paramPrintWriter.print(", id=");
      this.mId.dump(paramPrintWriter);
    }
    paramPrintWriter.print(", taskId=");
    paramPrintWriter.print(this.mTaskId);
    paramPrintWriter.print(", displayId=");
    paramPrintWriter.print(this.mDisplayId);
    if (this.mParentSessionId != 0)
    {
      paramPrintWriter.print(", parentId=");
      paramPrintWriter.print(this.mParentSessionId);
    }
    if (this.mFlags > 0)
    {
      paramPrintWriter.print(", flags=");
      paramPrintWriter.print(this.mFlags);
    }
    if (this.mExtras != null) {
      paramPrintWriter.print(", hasExtras");
    }
  }
  
  @SystemApi
  public ComponentName getActivityComponent()
  {
    return this.mComponentName;
  }
  
  @SystemApi
  public int getDisplayId()
  {
    return this.mDisplayId;
  }
  
  public Bundle getExtras()
  {
    return this.mExtras;
  }
  
  @SystemApi
  public int getFlags()
  {
    return this.mFlags;
  }
  
  public LocusId getLocusId()
  {
    return this.mId;
  }
  
  @SystemApi
  public ContentCaptureSessionId getParentSessionId()
  {
    int i = this.mParentSessionId;
    ContentCaptureSessionId localContentCaptureSessionId;
    if (i == 0) {
      localContentCaptureSessionId = null;
    } else {
      localContentCaptureSessionId = new ContentCaptureSessionId(i);
    }
    return localContentCaptureSessionId;
  }
  
  @SystemApi
  public int getTaskId()
  {
    return this.mTaskId;
  }
  
  public void setParentSessionId(int paramInt)
  {
    this.mParentSessionId = paramInt;
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder("Context[");
    if (fromServer())
    {
      localStringBuilder.append("act=");
      localStringBuilder.append(ComponentName.flattenToShortString(this.mComponentName));
      localStringBuilder.append(", taskId=");
      localStringBuilder.append(this.mTaskId);
      localStringBuilder.append(", displayId=");
      localStringBuilder.append(this.mDisplayId);
      localStringBuilder.append(", flags=");
      localStringBuilder.append(this.mFlags);
    }
    else
    {
      localStringBuilder.append("id=");
      localStringBuilder.append(this.mId);
      if (this.mExtras != null) {
        localStringBuilder.append(", hasExtras");
      }
    }
    if (this.mParentSessionId != 0)
    {
      localStringBuilder.append(", parentId=");
      localStringBuilder.append(this.mParentSessionId);
    }
    localStringBuilder.append(']');
    return localStringBuilder.toString();
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeInt(this.mHasClientContext);
    if (this.mHasClientContext)
    {
      paramParcel.writeParcelable(this.mId, paramInt);
      paramParcel.writeBundle(this.mExtras);
    }
    paramParcel.writeParcelable(this.mComponentName, paramInt);
    if (fromServer())
    {
      paramParcel.writeInt(this.mTaskId);
      paramParcel.writeInt(this.mDisplayId);
      paramParcel.writeInt(this.mFlags);
    }
  }
  
  public static final class Builder
  {
    private boolean mDestroyed;
    private Bundle mExtras;
    private final LocusId mId;
    
    public Builder(LocusId paramLocusId)
    {
      this.mId = ((LocusId)Preconditions.checkNotNull(paramLocusId));
    }
    
    private void throwIfDestroyed()
    {
      Preconditions.checkState(this.mDestroyed ^ true, "Already called #build()");
    }
    
    public ContentCaptureContext build()
    {
      throwIfDestroyed();
      this.mDestroyed = true;
      return new ContentCaptureContext(this, null);
    }
    
    public Builder setExtras(Bundle paramBundle)
    {
      this.mExtras = ((Bundle)Preconditions.checkNotNull(paramBundle));
      throwIfDestroyed();
      return this;
    }
  }
  
  @Retention(RetentionPolicy.SOURCE)
  static @interface ContextCreationFlags {}
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/view/contentcapture/ContentCaptureContext.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */