package android.webkit;

import android.annotation.UnsupportedAppUsage;
import android.content.pm.PackageInfo;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

public final class WebViewProviderResponse
  implements Parcelable
{
  public static final Parcelable.Creator<WebViewProviderResponse> CREATOR = new Parcelable.Creator()
  {
    public WebViewProviderResponse createFromParcel(Parcel paramAnonymousParcel)
    {
      return new WebViewProviderResponse(paramAnonymousParcel, null);
    }
    
    public WebViewProviderResponse[] newArray(int paramAnonymousInt)
    {
      return new WebViewProviderResponse[paramAnonymousInt];
    }
  };
  @UnsupportedAppUsage
  public final PackageInfo packageInfo;
  public final int status;
  
  public WebViewProviderResponse(PackageInfo paramPackageInfo, int paramInt)
  {
    this.packageInfo = paramPackageInfo;
    this.status = paramInt;
  }
  
  private WebViewProviderResponse(Parcel paramParcel)
  {
    this.packageInfo = ((PackageInfo)paramParcel.readTypedObject(PackageInfo.CREATOR));
    this.status = paramParcel.readInt();
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeTypedObject(this.packageInfo, paramInt);
    paramParcel.writeInt(this.status);
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/webkit/WebViewProviderResponse.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */