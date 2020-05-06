package android.view.textservice;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.SpellCheckSpan;

public final class TextInfo
  implements Parcelable
{
  public static final Parcelable.Creator<TextInfo> CREATOR = new Parcelable.Creator()
  {
    public TextInfo createFromParcel(Parcel paramAnonymousParcel)
    {
      return new TextInfo(paramAnonymousParcel);
    }
    
    public TextInfo[] newArray(int paramAnonymousInt)
    {
      return new TextInfo[paramAnonymousInt];
    }
  };
  private static final int DEFAULT_COOKIE = 0;
  private static final int DEFAULT_SEQUENCE_NUMBER = 0;
  private final CharSequence mCharSequence;
  private final int mCookie;
  private final int mSequenceNumber;
  
  public TextInfo(Parcel paramParcel)
  {
    this.mCharSequence = ((CharSequence)TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(paramParcel));
    this.mCookie = paramParcel.readInt();
    this.mSequenceNumber = paramParcel.readInt();
  }
  
  public TextInfo(CharSequence paramCharSequence, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    if (!TextUtils.isEmpty(paramCharSequence))
    {
      SpannableStringBuilder localSpannableStringBuilder = new SpannableStringBuilder(paramCharSequence, paramInt1, paramInt2);
      paramCharSequence = (SpellCheckSpan[])localSpannableStringBuilder.getSpans(0, localSpannableStringBuilder.length(), SpellCheckSpan.class);
      for (paramInt1 = 0; paramInt1 < paramCharSequence.length; paramInt1++) {
        localSpannableStringBuilder.removeSpan(paramCharSequence[paramInt1]);
      }
      this.mCharSequence = localSpannableStringBuilder;
      this.mCookie = paramInt3;
      this.mSequenceNumber = paramInt4;
      return;
    }
    throw new IllegalArgumentException("charSequence is empty");
  }
  
  public TextInfo(String paramString)
  {
    this(paramString, 0, getStringLengthOrZero(paramString), 0, 0);
  }
  
  public TextInfo(String paramString, int paramInt1, int paramInt2)
  {
    this(paramString, 0, getStringLengthOrZero(paramString), paramInt1, paramInt2);
  }
  
  private static int getStringLengthOrZero(String paramString)
  {
    int i;
    if (TextUtils.isEmpty(paramString)) {
      i = 0;
    } else {
      i = paramString.length();
    }
    return i;
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public CharSequence getCharSequence()
  {
    return this.mCharSequence;
  }
  
  public int getCookie()
  {
    return this.mCookie;
  }
  
  public int getSequence()
  {
    return this.mSequenceNumber;
  }
  
  public String getText()
  {
    CharSequence localCharSequence = this.mCharSequence;
    if (localCharSequence == null) {
      return null;
    }
    return localCharSequence.toString();
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    TextUtils.writeToParcel(this.mCharSequence, paramParcel, paramInt);
    paramParcel.writeInt(this.mCookie);
    paramParcel.writeInt(this.mSequenceNumber);
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/view/textservice/TextInfo.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */