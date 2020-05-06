package android.view;

import android.annotation.UnsupportedAppUsage;
import android.app.WindowConfiguration.ActivityType;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.util.ArraySet;
import android.util.SparseArray;

public class RemoteAnimationDefinition
  implements Parcelable
{
  public static final Parcelable.Creator<RemoteAnimationDefinition> CREATOR = new Parcelable.Creator()
  {
    public RemoteAnimationDefinition createFromParcel(Parcel paramAnonymousParcel)
    {
      return new RemoteAnimationDefinition(paramAnonymousParcel);
    }
    
    public RemoteAnimationDefinition[] newArray(int paramAnonymousInt)
    {
      return new RemoteAnimationDefinition[paramAnonymousInt];
    }
  };
  private final SparseArray<RemoteAnimationAdapterEntry> mTransitionAnimationMap;
  
  @UnsupportedAppUsage
  public RemoteAnimationDefinition()
  {
    this.mTransitionAnimationMap = new SparseArray();
  }
  
  public RemoteAnimationDefinition(Parcel paramParcel)
  {
    int i = paramParcel.readInt();
    this.mTransitionAnimationMap = new SparseArray(i);
    for (int j = 0; j < i; j++)
    {
      int k = paramParcel.readInt();
      RemoteAnimationAdapterEntry localRemoteAnimationAdapterEntry = (RemoteAnimationAdapterEntry)paramParcel.readTypedObject(RemoteAnimationAdapterEntry.CREATOR);
      this.mTransitionAnimationMap.put(k, localRemoteAnimationAdapterEntry);
    }
  }
  
  @UnsupportedAppUsage
  public void addRemoteAnimation(int paramInt1, @WindowConfiguration.ActivityType int paramInt2, RemoteAnimationAdapter paramRemoteAnimationAdapter)
  {
    this.mTransitionAnimationMap.put(paramInt1, new RemoteAnimationAdapterEntry(paramRemoteAnimationAdapter, paramInt2));
  }
  
  @UnsupportedAppUsage
  public void addRemoteAnimation(int paramInt, RemoteAnimationAdapter paramRemoteAnimationAdapter)
  {
    addRemoteAnimation(paramInt, 0, paramRemoteAnimationAdapter);
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public RemoteAnimationAdapter getAdapter(int paramInt, ArraySet<Integer> paramArraySet)
  {
    RemoteAnimationAdapterEntry localRemoteAnimationAdapterEntry = (RemoteAnimationAdapterEntry)this.mTransitionAnimationMap.get(paramInt);
    if (localRemoteAnimationAdapterEntry == null) {
      return null;
    }
    if ((localRemoteAnimationAdapterEntry.activityTypeFilter != 0) && (!paramArraySet.contains(Integer.valueOf(localRemoteAnimationAdapterEntry.activityTypeFilter)))) {
      return null;
    }
    return localRemoteAnimationAdapterEntry.adapter;
  }
  
  public boolean hasTransition(int paramInt, ArraySet<Integer> paramArraySet)
  {
    boolean bool;
    if (getAdapter(paramInt, paramArraySet) != null) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  public void setCallingPid(int paramInt)
  {
    for (int i = this.mTransitionAnimationMap.size() - 1; i >= 0; i--) {
      ((RemoteAnimationAdapterEntry)this.mTransitionAnimationMap.valueAt(i)).adapter.setCallingPid(paramInt);
    }
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    int i = this.mTransitionAnimationMap.size();
    paramParcel.writeInt(i);
    for (int j = 0; j < i; j++)
    {
      paramParcel.writeInt(this.mTransitionAnimationMap.keyAt(j));
      paramParcel.writeTypedObject((RemoteAnimationAdapterEntry)this.mTransitionAnimationMap.valueAt(j), paramInt);
    }
  }
  
  private static class RemoteAnimationAdapterEntry
    implements Parcelable
  {
    private static final Parcelable.Creator<RemoteAnimationAdapterEntry> CREATOR = new Parcelable.Creator()
    {
      public RemoteAnimationDefinition.RemoteAnimationAdapterEntry createFromParcel(Parcel paramAnonymousParcel)
      {
        return new RemoteAnimationDefinition.RemoteAnimationAdapterEntry(paramAnonymousParcel, null);
      }
      
      public RemoteAnimationDefinition.RemoteAnimationAdapterEntry[] newArray(int paramAnonymousInt)
      {
        return new RemoteAnimationDefinition.RemoteAnimationAdapterEntry[paramAnonymousInt];
      }
    };
    @WindowConfiguration.ActivityType
    final int activityTypeFilter;
    final RemoteAnimationAdapter adapter;
    
    private RemoteAnimationAdapterEntry(Parcel paramParcel)
    {
      this.adapter = ((RemoteAnimationAdapter)paramParcel.readParcelable(RemoteAnimationAdapter.class.getClassLoader()));
      this.activityTypeFilter = paramParcel.readInt();
    }
    
    RemoteAnimationAdapterEntry(RemoteAnimationAdapter paramRemoteAnimationAdapter, int paramInt)
    {
      this.adapter = paramRemoteAnimationAdapter;
      this.activityTypeFilter = paramInt;
    }
    
    public int describeContents()
    {
      return 0;
    }
    
    public void writeToParcel(Parcel paramParcel, int paramInt)
    {
      paramParcel.writeParcelable(this.adapter, paramInt);
      paramParcel.writeInt(this.activityTypeFilter);
    }
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/view/RemoteAnimationDefinition.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */