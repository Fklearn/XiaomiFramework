package android.view.autofill;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;

class ParcelableMap
  extends HashMap<AutofillId, AutofillValue>
  implements Parcelable
{
  public static final Parcelable.Creator<ParcelableMap> CREATOR = new Parcelable.Creator()
  {
    public ParcelableMap createFromParcel(Parcel paramAnonymousParcel)
    {
      int i = paramAnonymousParcel.readInt();
      ParcelableMap localParcelableMap = new ParcelableMap(i);
      for (int j = 0; j < i; j++) {
        localParcelableMap.put((AutofillId)paramAnonymousParcel.readParcelable(null), (AutofillValue)paramAnonymousParcel.readParcelable(null));
      }
      return localParcelableMap;
    }
    
    public ParcelableMap[] newArray(int paramAnonymousInt)
    {
      return new ParcelableMap[paramAnonymousInt];
    }
  };
  
  ParcelableMap(int paramInt)
  {
    super(paramInt);
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeInt(size());
    Iterator localIterator = entrySet().iterator();
    while (localIterator.hasNext())
    {
      Map.Entry localEntry = (Map.Entry)localIterator.next();
      paramParcel.writeParcelable((Parcelable)localEntry.getKey(), 0);
      paramParcel.writeParcelable((Parcelable)localEntry.getValue(), 0);
    }
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/view/autofill/ParcelableMap.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */