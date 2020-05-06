package android.view.textclassifier;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.util.ArrayMap;
import com.android.internal.util.Preconditions;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

final class EntityConfidence
  implements Parcelable
{
  public static final Parcelable.Creator<EntityConfidence> CREATOR = new Parcelable.Creator()
  {
    public EntityConfidence createFromParcel(Parcel paramAnonymousParcel)
    {
      return new EntityConfidence(paramAnonymousParcel, null);
    }
    
    public EntityConfidence[] newArray(int paramAnonymousInt)
    {
      return new EntityConfidence[paramAnonymousInt];
    }
  };
  private final ArrayMap<String, Float> mEntityConfidence = new ArrayMap();
  private final ArrayList<String> mSortedEntities = new ArrayList();
  
  EntityConfidence() {}
  
  private EntityConfidence(Parcel paramParcel)
  {
    int i = paramParcel.readInt();
    this.mEntityConfidence.ensureCapacity(i);
    for (int j = 0; j < i; j++) {
      this.mEntityConfidence.put(paramParcel.readString(), Float.valueOf(paramParcel.readFloat()));
    }
    resetSortedEntitiesFromMap();
  }
  
  EntityConfidence(EntityConfidence paramEntityConfidence)
  {
    Preconditions.checkNotNull(paramEntityConfidence);
    this.mEntityConfidence.putAll(paramEntityConfidence.mEntityConfidence);
    this.mSortedEntities.addAll(paramEntityConfidence.mSortedEntities);
  }
  
  EntityConfidence(Map<String, Float> paramMap)
  {
    Preconditions.checkNotNull(paramMap);
    this.mEntityConfidence.ensureCapacity(paramMap.size());
    paramMap = paramMap.entrySet().iterator();
    while (paramMap.hasNext())
    {
      Map.Entry localEntry = (Map.Entry)paramMap.next();
      if (((Float)localEntry.getValue()).floatValue() > 0.0F) {
        this.mEntityConfidence.put((String)localEntry.getKey(), Float.valueOf(Math.min(1.0F, ((Float)localEntry.getValue()).floatValue())));
      }
    }
    resetSortedEntitiesFromMap();
  }
  
  private void resetSortedEntitiesFromMap()
  {
    this.mSortedEntities.clear();
    this.mSortedEntities.ensureCapacity(this.mEntityConfidence.size());
    this.mSortedEntities.addAll(this.mEntityConfidence.keySet());
    this.mSortedEntities.sort(new _..Lambda.EntityConfidence.YPh8hwgSYYK8OyQ1kFlQngc71Q0(this));
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public float getConfidenceScore(String paramString)
  {
    if (this.mEntityConfidence.containsKey(paramString)) {
      return ((Float)this.mEntityConfidence.get(paramString)).floatValue();
    }
    return 0.0F;
  }
  
  public List<String> getEntities()
  {
    return Collections.unmodifiableList(this.mSortedEntities);
  }
  
  public String toString()
  {
    return this.mEntityConfidence.toString();
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeInt(this.mEntityConfidence.size());
    Iterator localIterator = this.mEntityConfidence.entrySet().iterator();
    while (localIterator.hasNext())
    {
      Map.Entry localEntry = (Map.Entry)localIterator.next();
      paramParcel.writeString((String)localEntry.getKey());
      paramParcel.writeFloat(((Float)localEntry.getValue()).floatValue());
    }
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/view/textclassifier/EntityConfidence.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */