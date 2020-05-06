package android.webkit;

import android.annotation.SystemApi;
import android.annotation.UnsupportedAppUsage;
import android.content.pm.Signature;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.util.Base64;

@SystemApi
public final class WebViewProviderInfo
  implements Parcelable
{
  public static final Parcelable.Creator<WebViewProviderInfo> CREATOR = new Parcelable.Creator()
  {
    public WebViewProviderInfo createFromParcel(Parcel paramAnonymousParcel)
    {
      return new WebViewProviderInfo(paramAnonymousParcel, null);
    }
    
    public WebViewProviderInfo[] newArray(int paramAnonymousInt)
    {
      return new WebViewProviderInfo[paramAnonymousInt];
    }
  };
  public final boolean availableByDefault;
  public final String description;
  public final boolean isFallback;
  public final String packageName;
  public final Signature[] signatures;
  
  @UnsupportedAppUsage
  private WebViewProviderInfo(Parcel paramParcel)
  {
    this.packageName = paramParcel.readString();
    this.description = paramParcel.readString();
    int i = paramParcel.readInt();
    boolean bool1 = true;
    boolean bool2;
    if (i > 0) {
      bool2 = true;
    } else {
      bool2 = false;
    }
    this.availableByDefault = bool2;
    if (paramParcel.readInt() > 0) {
      bool2 = bool1;
    } else {
      bool2 = false;
    }
    this.isFallback = bool2;
    this.signatures = ((Signature[])paramParcel.createTypedArray(Signature.CREATOR));
  }
  
  public WebViewProviderInfo(String paramString1, String paramString2, boolean paramBoolean1, boolean paramBoolean2, String[] paramArrayOfString)
  {
    this.packageName = paramString1;
    this.description = paramString2;
    this.availableByDefault = paramBoolean1;
    this.isFallback = paramBoolean2;
    if (paramArrayOfString == null)
    {
      this.signatures = new Signature[0];
    }
    else
    {
      this.signatures = new Signature[paramArrayOfString.length];
      for (int i = 0; i < paramArrayOfString.length; i++) {
        this.signatures[i] = new Signature(Base64.decode(paramArrayOfString[i], 0));
      }
    }
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeString(this.packageName);
    paramParcel.writeString(this.description);
    paramParcel.writeInt(this.availableByDefault);
    paramParcel.writeInt(this.isFallback);
    paramParcel.writeTypedArray(this.signatures, 0);
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/webkit/WebViewProviderInfo.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */