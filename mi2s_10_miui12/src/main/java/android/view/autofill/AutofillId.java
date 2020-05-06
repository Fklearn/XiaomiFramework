package android.view.autofill;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

public final class AutofillId
  implements Parcelable
{
  public static final Parcelable.Creator<AutofillId> CREATOR = new Parcelable.Creator()
  {
    public AutofillId createFromParcel(Parcel paramAnonymousParcel)
    {
      int i = paramAnonymousParcel.readInt();
      int j = paramAnonymousParcel.readInt();
      int k;
      if ((j & 0x4) != 0) {
        k = paramAnonymousParcel.readInt();
      } else {
        k = 0;
      }
      if ((j & 0x1) != 0) {
        return new AutofillId(j, i, paramAnonymousParcel.readInt(), k, null);
      }
      if ((j & 0x2) != 0) {
        return new AutofillId(j, i, paramAnonymousParcel.readLong(), k, null);
      }
      return new AutofillId(j, i, -1L, k, null);
    }
    
    public AutofillId[] newArray(int paramAnonymousInt)
    {
      return new AutofillId[paramAnonymousInt];
    }
  };
  private static final int FLAG_HAS_SESSION = 4;
  private static final int FLAG_IS_VIRTUAL_INT = 1;
  private static final int FLAG_IS_VIRTUAL_LONG = 2;
  public static final int NO_SESSION = 0;
  private int mFlags;
  private int mSessionId;
  private final int mViewId;
  private final int mVirtualIntId;
  private final long mVirtualLongId;
  
  public AutofillId(int paramInt)
  {
    this(0, paramInt, -1L, 0);
  }
  
  public AutofillId(int paramInt1, int paramInt2)
  {
    this(1, paramInt1, paramInt2, 0);
  }
  
  private AutofillId(int paramInt1, int paramInt2, long paramLong, int paramInt3)
  {
    this.mFlags = paramInt1;
    this.mViewId = paramInt2;
    if ((paramInt1 & 0x1) != 0) {
      paramInt2 = (int)paramLong;
    } else {
      paramInt2 = -1;
    }
    this.mVirtualIntId = paramInt2;
    if ((paramInt1 & 0x2) == 0) {
      paramLong = -1L;
    }
    this.mVirtualLongId = paramLong;
    this.mSessionId = paramInt3;
  }
  
  public AutofillId(AutofillId paramAutofillId, int paramInt)
  {
    this(1, paramAutofillId.mViewId, paramInt, 0);
  }
  
  public AutofillId(AutofillId paramAutofillId, long paramLong, int paramInt)
  {
    this(6, paramAutofillId.mViewId, paramLong, paramInt);
  }
  
  public static AutofillId withoutSession(AutofillId paramAutofillId)
  {
    return new AutofillId(paramAutofillId.mFlags & 0xFFFFFFFB, paramAutofillId.mViewId, paramAutofillId.mVirtualLongId, 0);
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public boolean equals(Object paramObject)
  {
    if (this == paramObject) {
      return true;
    }
    if (paramObject == null) {
      return false;
    }
    if (getClass() != paramObject.getClass()) {
      return false;
    }
    paramObject = (AutofillId)paramObject;
    if (this.mViewId != ((AutofillId)paramObject).mViewId) {
      return false;
    }
    if (this.mVirtualIntId != ((AutofillId)paramObject).mVirtualIntId) {
      return false;
    }
    if (this.mVirtualLongId != ((AutofillId)paramObject).mVirtualLongId) {
      return false;
    }
    return this.mSessionId == ((AutofillId)paramObject).mSessionId;
  }
  
  public boolean equalsIgnoreSession(AutofillId paramAutofillId)
  {
    if (this == paramAutofillId) {
      return true;
    }
    if (paramAutofillId == null) {
      return false;
    }
    if (this.mViewId != paramAutofillId.mViewId) {
      return false;
    }
    if (this.mVirtualIntId != paramAutofillId.mVirtualIntId) {
      return false;
    }
    return this.mVirtualLongId == paramAutofillId.mVirtualLongId;
  }
  
  public int getSessionId()
  {
    return this.mSessionId;
  }
  
  public int getViewId()
  {
    return this.mViewId;
  }
  
  public int getVirtualChildIntId()
  {
    return this.mVirtualIntId;
  }
  
  public long getVirtualChildLongId()
  {
    return this.mVirtualLongId;
  }
  
  public boolean hasSession()
  {
    boolean bool;
    if ((this.mFlags & 0x4) != 0) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  public int hashCode()
  {
    int i = this.mViewId;
    int j = this.mVirtualIntId;
    long l = this.mVirtualLongId;
    return (((1 * 31 + i) * 31 + j) * 31 + (int)(l ^ l >>> 32)) * 31 + this.mSessionId;
  }
  
  public boolean isNonVirtual()
  {
    boolean bool;
    if ((!isVirtualInt()) && (!isVirtualLong())) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  public boolean isVirtualInt()
  {
    int i = this.mFlags;
    boolean bool = true;
    if ((i & 0x1) == 0) {
      bool = false;
    }
    return bool;
  }
  
  public boolean isVirtualLong()
  {
    boolean bool;
    if ((this.mFlags & 0x2) != 0) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  public void resetSessionId()
  {
    this.mFlags &= 0xFFFFFFFB;
    this.mSessionId = 0;
  }
  
  public void setSessionId(int paramInt)
  {
    this.mFlags |= 0x4;
    this.mSessionId = paramInt;
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder().append(this.mViewId);
    if (isVirtualInt())
    {
      localStringBuilder.append(':');
      localStringBuilder.append(this.mVirtualIntId);
    }
    else if (isVirtualLong())
    {
      localStringBuilder.append(':');
      localStringBuilder.append(this.mVirtualLongId);
    }
    if (hasSession())
    {
      localStringBuilder.append('@');
      localStringBuilder.append(this.mSessionId);
    }
    return localStringBuilder.toString();
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeInt(this.mViewId);
    paramParcel.writeInt(this.mFlags);
    if (hasSession()) {
      paramParcel.writeInt(this.mSessionId);
    }
    if (isVirtualInt()) {
      paramParcel.writeInt(this.mVirtualIntId);
    } else if (isVirtualLong()) {
      paramParcel.writeLong(this.mVirtualLongId);
    }
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/view/autofill/AutofillId.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */