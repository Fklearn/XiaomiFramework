package android.view.inputmethod;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

public class ExtractedTextRequest
  implements Parcelable
{
  public static final Parcelable.Creator<ExtractedTextRequest> CREATOR = new Parcelable.Creator()
  {
    public ExtractedTextRequest createFromParcel(Parcel paramAnonymousParcel)
    {
      ExtractedTextRequest localExtractedTextRequest = new ExtractedTextRequest();
      localExtractedTextRequest.token = paramAnonymousParcel.readInt();
      localExtractedTextRequest.flags = paramAnonymousParcel.readInt();
      localExtractedTextRequest.hintMaxLines = paramAnonymousParcel.readInt();
      localExtractedTextRequest.hintMaxChars = paramAnonymousParcel.readInt();
      return localExtractedTextRequest;
    }
    
    public ExtractedTextRequest[] newArray(int paramAnonymousInt)
    {
      return new ExtractedTextRequest[paramAnonymousInt];
    }
  };
  public int flags;
  public int hintMaxChars;
  public int hintMaxLines;
  public int token;
  
  public int describeContents()
  {
    return 0;
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeInt(this.token);
    paramParcel.writeInt(this.flags);
    paramParcel.writeInt(this.hintMaxLines);
    paramParcel.writeInt(this.hintMaxChars);
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/view/inputmethod/ExtractedTextRequest.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */