package android.view.inputmethod;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.text.TextUtils;

public final class CompletionInfo
  implements Parcelable
{
  public static final Parcelable.Creator<CompletionInfo> CREATOR = new Parcelable.Creator()
  {
    public CompletionInfo createFromParcel(Parcel paramAnonymousParcel)
    {
      return new CompletionInfo(paramAnonymousParcel, null);
    }
    
    public CompletionInfo[] newArray(int paramAnonymousInt)
    {
      return new CompletionInfo[paramAnonymousInt];
    }
  };
  private final long mId;
  private final CharSequence mLabel;
  private final int mPosition;
  private final CharSequence mText;
  
  public CompletionInfo(long paramLong, int paramInt, CharSequence paramCharSequence)
  {
    this.mId = paramLong;
    this.mPosition = paramInt;
    this.mText = paramCharSequence;
    this.mLabel = null;
  }
  
  public CompletionInfo(long paramLong, int paramInt, CharSequence paramCharSequence1, CharSequence paramCharSequence2)
  {
    this.mId = paramLong;
    this.mPosition = paramInt;
    this.mText = paramCharSequence1;
    this.mLabel = paramCharSequence2;
  }
  
  private CompletionInfo(Parcel paramParcel)
  {
    this.mId = paramParcel.readLong();
    this.mPosition = paramParcel.readInt();
    this.mText = ((CharSequence)TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(paramParcel));
    this.mLabel = ((CharSequence)TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(paramParcel));
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public long getId()
  {
    return this.mId;
  }
  
  public CharSequence getLabel()
  {
    return this.mLabel;
  }
  
  public int getPosition()
  {
    return this.mPosition;
  }
  
  public CharSequence getText()
  {
    return this.mText;
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("CompletionInfo{#");
    localStringBuilder.append(this.mPosition);
    localStringBuilder.append(" \"");
    localStringBuilder.append(this.mText);
    localStringBuilder.append("\" id=");
    localStringBuilder.append(this.mId);
    localStringBuilder.append(" label=");
    localStringBuilder.append(this.mLabel);
    localStringBuilder.append("}");
    return localStringBuilder.toString();
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeLong(this.mId);
    paramParcel.writeInt(this.mPosition);
    TextUtils.writeToParcel(this.mText, paramParcel, paramInt);
    TextUtils.writeToParcel(this.mLabel, paramParcel, paramInt);
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/view/inputmethod/CompletionInfo.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */