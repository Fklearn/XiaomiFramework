package android.view.textservice;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import java.util.Arrays;

public final class SentenceSuggestionsInfo
  implements Parcelable
{
  public static final Parcelable.Creator<SentenceSuggestionsInfo> CREATOR = new Parcelable.Creator()
  {
    public SentenceSuggestionsInfo createFromParcel(Parcel paramAnonymousParcel)
    {
      return new SentenceSuggestionsInfo(paramAnonymousParcel);
    }
    
    public SentenceSuggestionsInfo[] newArray(int paramAnonymousInt)
    {
      return new SentenceSuggestionsInfo[paramAnonymousInt];
    }
  };
  private final int[] mLengths;
  private final int[] mOffsets;
  private final SuggestionsInfo[] mSuggestionsInfos;
  
  public SentenceSuggestionsInfo(Parcel paramParcel)
  {
    this.mSuggestionsInfos = new SuggestionsInfo[paramParcel.readInt()];
    paramParcel.readTypedArray(this.mSuggestionsInfos, SuggestionsInfo.CREATOR);
    this.mOffsets = new int[this.mSuggestionsInfos.length];
    paramParcel.readIntArray(this.mOffsets);
    this.mLengths = new int[this.mSuggestionsInfos.length];
    paramParcel.readIntArray(this.mLengths);
  }
  
  public SentenceSuggestionsInfo(SuggestionsInfo[] paramArrayOfSuggestionsInfo, int[] paramArrayOfInt1, int[] paramArrayOfInt2)
  {
    if ((paramArrayOfSuggestionsInfo != null) && (paramArrayOfInt1 != null) && (paramArrayOfInt2 != null))
    {
      if ((paramArrayOfSuggestionsInfo.length == paramArrayOfInt1.length) && (paramArrayOfInt1.length == paramArrayOfInt2.length))
      {
        int i = paramArrayOfSuggestionsInfo.length;
        this.mSuggestionsInfos = ((SuggestionsInfo[])Arrays.copyOf(paramArrayOfSuggestionsInfo, i));
        this.mOffsets = Arrays.copyOf(paramArrayOfInt1, i);
        this.mLengths = Arrays.copyOf(paramArrayOfInt2, i);
        return;
      }
      throw new IllegalArgumentException();
    }
    throw new NullPointerException();
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public int getLengthAt(int paramInt)
  {
    if (paramInt >= 0)
    {
      int[] arrayOfInt = this.mLengths;
      if (paramInt < arrayOfInt.length) {
        return arrayOfInt[paramInt];
      }
    }
    return -1;
  }
  
  public int getOffsetAt(int paramInt)
  {
    if (paramInt >= 0)
    {
      int[] arrayOfInt = this.mOffsets;
      if (paramInt < arrayOfInt.length) {
        return arrayOfInt[paramInt];
      }
    }
    return -1;
  }
  
  public int getSuggestionsCount()
  {
    return this.mSuggestionsInfos.length;
  }
  
  public SuggestionsInfo getSuggestionsInfoAt(int paramInt)
  {
    if (paramInt >= 0)
    {
      SuggestionsInfo[] arrayOfSuggestionsInfo = this.mSuggestionsInfos;
      if (paramInt < arrayOfSuggestionsInfo.length) {
        return arrayOfSuggestionsInfo[paramInt];
      }
    }
    return null;
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeInt(this.mSuggestionsInfos.length);
    paramParcel.writeTypedArray(this.mSuggestionsInfos, 0);
    paramParcel.writeIntArray(this.mOffsets);
    paramParcel.writeIntArray(this.mLengths);
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/view/textservice/SentenceSuggestionsInfo.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */