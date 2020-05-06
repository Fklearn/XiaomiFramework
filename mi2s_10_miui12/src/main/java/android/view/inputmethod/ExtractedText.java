package android.view.inputmethod;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.text.TextUtils;

public class ExtractedText
  implements Parcelable
{
  public static final Parcelable.Creator<ExtractedText> CREATOR = new Parcelable.Creator()
  {
    public ExtractedText createFromParcel(Parcel paramAnonymousParcel)
    {
      ExtractedText localExtractedText = new ExtractedText();
      localExtractedText.text = ((CharSequence)TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(paramAnonymousParcel));
      localExtractedText.startOffset = paramAnonymousParcel.readInt();
      localExtractedText.partialStartOffset = paramAnonymousParcel.readInt();
      localExtractedText.partialEndOffset = paramAnonymousParcel.readInt();
      localExtractedText.selectionStart = paramAnonymousParcel.readInt();
      localExtractedText.selectionEnd = paramAnonymousParcel.readInt();
      localExtractedText.flags = paramAnonymousParcel.readInt();
      localExtractedText.hint = ((CharSequence)TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(paramAnonymousParcel));
      return localExtractedText;
    }
    
    public ExtractedText[] newArray(int paramAnonymousInt)
    {
      return new ExtractedText[paramAnonymousInt];
    }
  };
  public static final int FLAG_SELECTING = 2;
  public static final int FLAG_SINGLE_LINE = 1;
  public int flags;
  public CharSequence hint;
  public int partialEndOffset;
  public int partialStartOffset;
  public int selectionEnd;
  public int selectionStart;
  public int startOffset;
  public CharSequence text;
  
  public int describeContents()
  {
    return 0;
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    TextUtils.writeToParcel(this.text, paramParcel, paramInt);
    paramParcel.writeInt(this.startOffset);
    paramParcel.writeInt(this.partialStartOffset);
    paramParcel.writeInt(this.partialEndOffset);
    paramParcel.writeInt(this.selectionStart);
    paramParcel.writeInt(this.selectionEnd);
    paramParcel.writeInt(this.flags);
    TextUtils.writeToParcel(this.hint, paramParcel, paramInt);
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/view/inputmethod/ExtractedText.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */