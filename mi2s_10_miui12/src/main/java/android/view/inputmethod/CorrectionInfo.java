package android.view.inputmethod;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.text.TextUtils;

public final class CorrectionInfo
  implements Parcelable
{
  public static final Parcelable.Creator<CorrectionInfo> CREATOR = new Parcelable.Creator()
  {
    public CorrectionInfo createFromParcel(Parcel paramAnonymousParcel)
    {
      return new CorrectionInfo(paramAnonymousParcel, null);
    }
    
    public CorrectionInfo[] newArray(int paramAnonymousInt)
    {
      return new CorrectionInfo[paramAnonymousInt];
    }
  };
  private final CharSequence mNewText;
  private final int mOffset;
  private final CharSequence mOldText;
  
  public CorrectionInfo(int paramInt, CharSequence paramCharSequence1, CharSequence paramCharSequence2)
  {
    this.mOffset = paramInt;
    this.mOldText = paramCharSequence1;
    this.mNewText = paramCharSequence2;
  }
  
  private CorrectionInfo(Parcel paramParcel)
  {
    this.mOffset = paramParcel.readInt();
    this.mOldText = ((CharSequence)TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(paramParcel));
    this.mNewText = ((CharSequence)TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(paramParcel));
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public CharSequence getNewText()
  {
    return this.mNewText;
  }
  
  public int getOffset()
  {
    return this.mOffset;
  }
  
  public CharSequence getOldText()
  {
    return this.mOldText;
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("CorrectionInfo{#");
    localStringBuilder.append(this.mOffset);
    localStringBuilder.append(" \"");
    localStringBuilder.append(this.mOldText);
    localStringBuilder.append("\" -> \"");
    localStringBuilder.append(this.mNewText);
    localStringBuilder.append("\"}");
    return localStringBuilder.toString();
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeInt(this.mOffset);
    TextUtils.writeToParcel(this.mOldText, paramParcel, paramInt);
    TextUtils.writeToParcel(this.mNewText, paramParcel, paramInt);
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/view/inputmethod/CorrectionInfo.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */