package android.view.textservice;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import com.android.internal.util.ArrayUtils;

public final class SuggestionsInfo
  implements Parcelable
{
  public static final Parcelable.Creator<SuggestionsInfo> CREATOR = new Parcelable.Creator()
  {
    public SuggestionsInfo createFromParcel(Parcel paramAnonymousParcel)
    {
      return new SuggestionsInfo(paramAnonymousParcel);
    }
    
    public SuggestionsInfo[] newArray(int paramAnonymousInt)
    {
      return new SuggestionsInfo[paramAnonymousInt];
    }
  };
  private static final String[] EMPTY = (String[])ArrayUtils.emptyArray(String.class);
  public static final int RESULT_ATTR_HAS_RECOMMENDED_SUGGESTIONS = 4;
  public static final int RESULT_ATTR_IN_THE_DICTIONARY = 1;
  public static final int RESULT_ATTR_LOOKS_LIKE_TYPO = 2;
  private int mCookie;
  private int mSequence;
  private final String[] mSuggestions;
  private final int mSuggestionsAttributes;
  private final boolean mSuggestionsAvailable;
  
  public SuggestionsInfo(int paramInt, String[] paramArrayOfString)
  {
    this(paramInt, paramArrayOfString, 0, 0);
  }
  
  public SuggestionsInfo(int paramInt1, String[] paramArrayOfString, int paramInt2, int paramInt3)
  {
    if (paramArrayOfString == null)
    {
      this.mSuggestions = EMPTY;
      this.mSuggestionsAvailable = false;
    }
    else
    {
      this.mSuggestions = paramArrayOfString;
      this.mSuggestionsAvailable = true;
    }
    this.mSuggestionsAttributes = paramInt1;
    this.mCookie = paramInt2;
    this.mSequence = paramInt3;
  }
  
  public SuggestionsInfo(Parcel paramParcel)
  {
    this.mSuggestionsAttributes = paramParcel.readInt();
    this.mSuggestions = paramParcel.readStringArray();
    this.mCookie = paramParcel.readInt();
    this.mSequence = paramParcel.readInt();
    int i = paramParcel.readInt();
    boolean bool = true;
    if (i != 1) {
      bool = false;
    }
    this.mSuggestionsAvailable = bool;
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public int getCookie()
  {
    return this.mCookie;
  }
  
  public int getSequence()
  {
    return this.mSequence;
  }
  
  public String getSuggestionAt(int paramInt)
  {
    return this.mSuggestions[paramInt];
  }
  
  public int getSuggestionsAttributes()
  {
    return this.mSuggestionsAttributes;
  }
  
  public int getSuggestionsCount()
  {
    if (!this.mSuggestionsAvailable) {
      return -1;
    }
    return this.mSuggestions.length;
  }
  
  public void setCookieAndSequence(int paramInt1, int paramInt2)
  {
    this.mCookie = paramInt1;
    this.mSequence = paramInt2;
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeInt(this.mSuggestionsAttributes);
    paramParcel.writeStringArray(this.mSuggestions);
    paramParcel.writeInt(this.mCookie);
    paramParcel.writeInt(this.mSequence);
    paramParcel.writeInt(this.mSuggestionsAvailable);
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/view/textservice/SuggestionsInfo.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */