package android.view.inputmethod;

import android.content.ClipDescription;
import android.content.ContentProvider;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.os.RemoteException;
import android.os.UserHandle;
import com.android.internal.inputmethod.IInputContentUriToken;
import com.android.internal.inputmethod.IInputContentUriToken.Stub;
import java.security.InvalidParameterException;

public final class InputContentInfo
  implements Parcelable
{
  public static final Parcelable.Creator<InputContentInfo> CREATOR = new Parcelable.Creator()
  {
    public InputContentInfo createFromParcel(Parcel paramAnonymousParcel)
    {
      return new InputContentInfo(paramAnonymousParcel, null);
    }
    
    public InputContentInfo[] newArray(int paramAnonymousInt)
    {
      return new InputContentInfo[paramAnonymousInt];
    }
  };
  private final Uri mContentUri;
  private final int mContentUriOwnerUserId;
  private final ClipDescription mDescription;
  private final Uri mLinkUri;
  private IInputContentUriToken mUriToken;
  
  public InputContentInfo(Uri paramUri, ClipDescription paramClipDescription)
  {
    this(paramUri, paramClipDescription, null);
  }
  
  public InputContentInfo(Uri paramUri1, ClipDescription paramClipDescription, Uri paramUri2)
  {
    validateInternal(paramUri1, paramClipDescription, paramUri2, true);
    this.mContentUri = paramUri1;
    this.mContentUriOwnerUserId = ContentProvider.getUserIdFromUri(this.mContentUri, UserHandle.myUserId());
    this.mDescription = paramClipDescription;
    this.mLinkUri = paramUri2;
  }
  
  private InputContentInfo(Parcel paramParcel)
  {
    this.mContentUri = ((Uri)Uri.CREATOR.createFromParcel(paramParcel));
    this.mContentUriOwnerUserId = paramParcel.readInt();
    this.mDescription = ((ClipDescription)ClipDescription.CREATOR.createFromParcel(paramParcel));
    this.mLinkUri = ((Uri)Uri.CREATOR.createFromParcel(paramParcel));
    if (paramParcel.readInt() == 1) {
      this.mUriToken = IInputContentUriToken.Stub.asInterface(paramParcel.readStrongBinder());
    } else {
      this.mUriToken = null;
    }
  }
  
  private static boolean validateInternal(Uri paramUri1, ClipDescription paramClipDescription, Uri paramUri2, boolean paramBoolean)
  {
    if (paramUri1 == null)
    {
      if (!paramBoolean) {
        return false;
      }
      throw new NullPointerException("contentUri");
    }
    if (paramClipDescription == null)
    {
      if (!paramBoolean) {
        return false;
      }
      throw new NullPointerException("description");
    }
    if (!"content".equals(paramUri1.getScheme()))
    {
      if (!paramBoolean) {
        return false;
      }
      throw new InvalidParameterException("contentUri must have content scheme");
    }
    if (paramUri2 != null)
    {
      paramUri1 = paramUri2.getScheme();
      if ((paramUri1 == null) || ((!paramUri1.equalsIgnoreCase("http")) && (!paramUri1.equalsIgnoreCase("https"))))
      {
        if (!paramBoolean) {
          return false;
        }
        throw new InvalidParameterException("linkUri must have either http or https scheme");
      }
    }
    return true;
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public Uri getContentUri()
  {
    if (this.mContentUriOwnerUserId != UserHandle.myUserId()) {
      return ContentProvider.maybeAddUserId(this.mContentUri, this.mContentUriOwnerUserId);
    }
    return this.mContentUri;
  }
  
  public ClipDescription getDescription()
  {
    return this.mDescription;
  }
  
  public Uri getLinkUri()
  {
    return this.mLinkUri;
  }
  
  public void releasePermission()
  {
    IInputContentUriToken localIInputContentUriToken = this.mUriToken;
    if (localIInputContentUriToken == null) {
      return;
    }
    try
    {
      localIInputContentUriToken.release();
    }
    catch (RemoteException localRemoteException)
    {
      localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public void requestPermission()
  {
    IInputContentUriToken localIInputContentUriToken = this.mUriToken;
    if (localIInputContentUriToken == null) {
      return;
    }
    try
    {
      localIInputContentUriToken.take();
    }
    catch (RemoteException localRemoteException)
    {
      localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public void setUriToken(IInputContentUriToken paramIInputContentUriToken)
  {
    if (this.mUriToken == null)
    {
      this.mUriToken = paramIInputContentUriToken;
      return;
    }
    throw new IllegalStateException("URI token is already set");
  }
  
  public boolean validate()
  {
    return validateInternal(this.mContentUri, this.mDescription, this.mLinkUri, false);
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    Uri.writeToParcel(paramParcel, this.mContentUri);
    paramParcel.writeInt(this.mContentUriOwnerUserId);
    this.mDescription.writeToParcel(paramParcel, paramInt);
    Uri.writeToParcel(paramParcel, this.mLinkUri);
    if (this.mUriToken != null)
    {
      paramParcel.writeInt(1);
      paramParcel.writeStrongBinder(this.mUriToken.asBinder());
    }
    else
    {
      paramParcel.writeInt(0);
    }
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/view/inputmethod/InputContentInfo.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */