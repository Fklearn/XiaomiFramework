package android.view.autofill;

import android.os.Looper;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.text.TextUtils;
import android.util.Log;
import com.android.internal.util.Preconditions;
import java.util.Objects;

public final class AutofillValue
  implements Parcelable
{
  public static final Parcelable.Creator<AutofillValue> CREATOR = new Parcelable.Creator()
  {
    public AutofillValue createFromParcel(Parcel paramAnonymousParcel)
    {
      return new AutofillValue(paramAnonymousParcel, null);
    }
    
    public AutofillValue[] newArray(int paramAnonymousInt)
    {
      return new AutofillValue[paramAnonymousInt];
    }
  };
  private static final String TAG = "AutofillValue";
  private final int mType;
  private final Object mValue;
  
  private AutofillValue(int paramInt, Object paramObject)
  {
    this.mType = paramInt;
    this.mValue = paramObject;
  }
  
  private AutofillValue(Parcel paramParcel)
  {
    this.mType = paramParcel.readInt();
    int i = this.mType;
    boolean bool = true;
    if (i != 1)
    {
      if (i != 2)
      {
        if (i != 3)
        {
          if (i == 4)
          {
            this.mValue = Long.valueOf(paramParcel.readLong());
          }
          else
          {
            paramParcel = new StringBuilder();
            paramParcel.append("type=");
            paramParcel.append(this.mType);
            paramParcel.append(" not valid");
            throw new IllegalArgumentException(paramParcel.toString());
          }
        }
        else {
          this.mValue = Integer.valueOf(paramParcel.readInt());
        }
      }
      else
      {
        if (paramParcel.readInt() == 0) {
          bool = false;
        }
        this.mValue = Boolean.valueOf(bool);
      }
    }
    else {
      this.mValue = paramParcel.readCharSequence();
    }
  }
  
  public static AutofillValue forDate(long paramLong)
  {
    return new AutofillValue(4, Long.valueOf(paramLong));
  }
  
  public static AutofillValue forList(int paramInt)
  {
    return new AutofillValue(3, Integer.valueOf(paramInt));
  }
  
  public static AutofillValue forText(CharSequence paramCharSequence)
  {
    if ((Helper.sVerbose) && (!Looper.getMainLooper().isCurrentThread()))
    {
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append("forText() not called on main thread: ");
      localStringBuilder.append(Thread.currentThread());
      Log.v("AutofillValue", localStringBuilder.toString());
    }
    if (paramCharSequence == null) {
      paramCharSequence = null;
    } else {
      paramCharSequence = new AutofillValue(1, TextUtils.trimNoCopySpans(paramCharSequence));
    }
    return paramCharSequence;
  }
  
  public static AutofillValue forToggle(boolean paramBoolean)
  {
    return new AutofillValue(2, Boolean.valueOf(paramBoolean));
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
    paramObject = (AutofillValue)paramObject;
    if (this.mType != ((AutofillValue)paramObject).mType) {
      return false;
    }
    if (isText()) {
      return this.mValue.toString().equals(((AutofillValue)paramObject).mValue.toString());
    }
    return Objects.equals(this.mValue, ((AutofillValue)paramObject).mValue);
  }
  
  public long getDateValue()
  {
    boolean bool = isDate();
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("value must be a date value, not type=");
    localStringBuilder.append(this.mType);
    Preconditions.checkState(bool, localStringBuilder.toString());
    return ((Long)this.mValue).longValue();
  }
  
  public int getListValue()
  {
    boolean bool = isList();
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("value must be a list value, not type=");
    localStringBuilder.append(this.mType);
    Preconditions.checkState(bool, localStringBuilder.toString());
    return ((Integer)this.mValue).intValue();
  }
  
  public CharSequence getTextValue()
  {
    boolean bool = isText();
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("value must be a text value, not type=");
    localStringBuilder.append(this.mType);
    Preconditions.checkState(bool, localStringBuilder.toString());
    return (CharSequence)this.mValue;
  }
  
  public boolean getToggleValue()
  {
    boolean bool = isToggle();
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("value must be a toggle value, not type=");
    localStringBuilder.append(this.mType);
    Preconditions.checkState(bool, localStringBuilder.toString());
    return ((Boolean)this.mValue).booleanValue();
  }
  
  public int hashCode()
  {
    return this.mType + this.mValue.hashCode();
  }
  
  public boolean isDate()
  {
    boolean bool;
    if (this.mType == 4) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  public boolean isEmpty()
  {
    boolean bool;
    if ((isText()) && (((CharSequence)this.mValue).length() == 0)) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  public boolean isList()
  {
    boolean bool;
    if (this.mType == 3) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  public boolean isText()
  {
    int i = this.mType;
    boolean bool = true;
    if (i != 1) {
      bool = false;
    }
    return bool;
  }
  
  public boolean isToggle()
  {
    boolean bool;
    if (this.mType == 2) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  public String toString()
  {
    if (!Helper.sDebug) {
      return super.toString();
    }
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("[type=");
    localStringBuilder.append(this.mType);
    localStringBuilder = localStringBuilder.append(", value=");
    if (isText()) {
      Helper.appendRedacted(localStringBuilder, (CharSequence)this.mValue);
    } else {
      localStringBuilder.append(this.mValue);
    }
    localStringBuilder.append(']');
    return localStringBuilder.toString();
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeInt(this.mType);
    paramInt = this.mType;
    if (paramInt != 1)
    {
      if (paramInt != 2)
      {
        if (paramInt != 3)
        {
          if (paramInt == 4) {
            paramParcel.writeLong(((Long)this.mValue).longValue());
          }
        }
        else {
          paramParcel.writeInt(((Integer)this.mValue).intValue());
        }
      }
      else {
        paramParcel.writeInt(((Boolean)this.mValue).booleanValue());
      }
    }
    else {
      paramParcel.writeCharSequence((CharSequence)this.mValue);
    }
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/view/autofill/AutofillValue.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */