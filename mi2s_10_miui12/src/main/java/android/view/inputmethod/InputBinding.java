package android.view.inputmethod;

import android.os.IBinder;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

public final class InputBinding
  implements Parcelable
{
  public static final Parcelable.Creator<InputBinding> CREATOR = new Parcelable.Creator()
  {
    public InputBinding createFromParcel(Parcel paramAnonymousParcel)
    {
      return new InputBinding(paramAnonymousParcel);
    }
    
    public InputBinding[] newArray(int paramAnonymousInt)
    {
      return new InputBinding[paramAnonymousInt];
    }
  };
  static final String TAG = "InputBinding";
  final InputConnection mConnection;
  final IBinder mConnectionToken;
  final int mPid;
  final int mUid;
  
  InputBinding(Parcel paramParcel)
  {
    this.mConnection = null;
    this.mConnectionToken = paramParcel.readStrongBinder();
    this.mUid = paramParcel.readInt();
    this.mPid = paramParcel.readInt();
  }
  
  public InputBinding(InputConnection paramInputConnection, IBinder paramIBinder, int paramInt1, int paramInt2)
  {
    this.mConnection = paramInputConnection;
    this.mConnectionToken = paramIBinder;
    this.mUid = paramInt1;
    this.mPid = paramInt2;
  }
  
  public InputBinding(InputConnection paramInputConnection, InputBinding paramInputBinding)
  {
    this.mConnection = paramInputConnection;
    this.mConnectionToken = paramInputBinding.getConnectionToken();
    this.mUid = paramInputBinding.getUid();
    this.mPid = paramInputBinding.getPid();
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public InputConnection getConnection()
  {
    return this.mConnection;
  }
  
  public IBinder getConnectionToken()
  {
    return this.mConnectionToken;
  }
  
  public int getPid()
  {
    return this.mPid;
  }
  
  public int getUid()
  {
    return this.mUid;
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("InputBinding{");
    localStringBuilder.append(this.mConnectionToken);
    localStringBuilder.append(" / uid ");
    localStringBuilder.append(this.mUid);
    localStringBuilder.append(" / pid ");
    localStringBuilder.append(this.mPid);
    localStringBuilder.append("}");
    return localStringBuilder.toString();
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeStrongBinder(this.mConnectionToken);
    paramParcel.writeInt(this.mUid);
    paramParcel.writeInt(this.mPid);
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/view/inputmethod/InputBinding.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */