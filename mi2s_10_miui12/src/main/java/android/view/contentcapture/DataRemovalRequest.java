package android.view.contentcapture;

import android.app.ActivityThread;
import android.app.Application;
import android.content.LocusId;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.util.IntArray;
import com.android.internal.util.Preconditions;
import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;

public final class DataRemovalRequest
  implements Parcelable
{
  public static final Parcelable.Creator<DataRemovalRequest> CREATOR = new Parcelable.Creator()
  {
    public DataRemovalRequest createFromParcel(Parcel paramAnonymousParcel)
    {
      return new DataRemovalRequest(paramAnonymousParcel, null);
    }
    
    public DataRemovalRequest[] newArray(int paramAnonymousInt)
    {
      return new DataRemovalRequest[paramAnonymousInt];
    }
  };
  public static final int FLAG_IS_PREFIX = 1;
  private final boolean mForEverything;
  private ArrayList<LocusIdRequest> mLocusIdRequests;
  private final String mPackageName;
  
  private DataRemovalRequest(Parcel paramParcel)
  {
    this.mPackageName = paramParcel.readString();
    this.mForEverything = paramParcel.readBoolean();
    if (!this.mForEverything)
    {
      int i = paramParcel.readInt();
      this.mLocusIdRequests = new ArrayList(i);
      for (int j = 0; j < i; j++) {
        this.mLocusIdRequests.add(new LocusIdRequest((LocusId)paramParcel.readValue(null), paramParcel.readInt(), null));
      }
    }
  }
  
  private DataRemovalRequest(Builder paramBuilder)
  {
    this.mPackageName = ActivityThread.currentActivityThread().getApplication().getPackageName();
    this.mForEverything = paramBuilder.mForEverything;
    if (paramBuilder.mLocusIds != null)
    {
      int i = paramBuilder.mLocusIds.size();
      this.mLocusIdRequests = new ArrayList(i);
      for (int j = 0; j < i; j++) {
        this.mLocusIdRequests.add(new LocusIdRequest((LocusId)paramBuilder.mLocusIds.get(j), paramBuilder.mFlags.get(j), null));
      }
    }
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public List<LocusIdRequest> getLocusIdRequests()
  {
    return this.mLocusIdRequests;
  }
  
  public String getPackageName()
  {
    return this.mPackageName;
  }
  
  public boolean isForEverything()
  {
    return this.mForEverything;
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeString(this.mPackageName);
    paramParcel.writeBoolean(this.mForEverything);
    if (!this.mForEverything)
    {
      int i = this.mLocusIdRequests.size();
      paramParcel.writeInt(i);
      for (paramInt = 0; paramInt < i; paramInt++)
      {
        LocusIdRequest localLocusIdRequest = (LocusIdRequest)this.mLocusIdRequests.get(paramInt);
        paramParcel.writeValue(localLocusIdRequest.getLocusId());
        paramParcel.writeInt(localLocusIdRequest.getFlags());
      }
    }
  }
  
  public static final class Builder
  {
    private boolean mDestroyed;
    private IntArray mFlags;
    private boolean mForEverything;
    private ArrayList<LocusId> mLocusIds;
    
    private void throwIfDestroyed()
    {
      Preconditions.checkState(this.mDestroyed ^ true, "Already destroyed!");
    }
    
    public Builder addLocusId(LocusId paramLocusId, int paramInt)
    {
      throwIfDestroyed();
      Preconditions.checkState(this.mForEverything ^ true, "Already is for everything");
      Preconditions.checkNotNull(paramLocusId);
      if (this.mLocusIds == null)
      {
        this.mLocusIds = new ArrayList();
        this.mFlags = new IntArray();
      }
      this.mLocusIds.add(paramLocusId);
      this.mFlags.add(paramInt);
      return this;
    }
    
    public DataRemovalRequest build()
    {
      throwIfDestroyed();
      boolean bool;
      if ((!this.mForEverything) && (this.mLocusIds == null)) {
        bool = false;
      } else {
        bool = true;
      }
      Preconditions.checkState(bool, "must call either #forEverything() or add one #addLocusId()");
      this.mDestroyed = true;
      return new DataRemovalRequest(this, null);
    }
    
    public Builder forEverything()
    {
      throwIfDestroyed();
      boolean bool;
      if (this.mLocusIds == null) {
        bool = true;
      } else {
        bool = false;
      }
      Preconditions.checkState(bool, "Already added LocusIds");
      this.mForEverything = true;
      return this;
    }
  }
  
  @Retention(RetentionPolicy.SOURCE)
  static @interface Flags {}
  
  public final class LocusIdRequest
  {
    private final int mFlags;
    private final LocusId mLocusId;
    
    private LocusIdRequest(LocusId paramLocusId, int paramInt)
    {
      this.mLocusId = paramLocusId;
      this.mFlags = paramInt;
    }
    
    public int getFlags()
    {
      return this.mFlags;
    }
    
    public LocusId getLocusId()
    {
      return this.mLocusId;
    }
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/view/contentcapture/DataRemovalRequest.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */