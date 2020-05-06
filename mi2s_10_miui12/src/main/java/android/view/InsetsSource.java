package android.view;

import android.graphics.Insets;
import android.graphics.Rect;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import java.io.PrintWriter;

public class InsetsSource
  implements Parcelable
{
  public static final Parcelable.Creator<InsetsSource> CREATOR = new Parcelable.Creator()
  {
    public InsetsSource createFromParcel(Parcel paramAnonymousParcel)
    {
      return new InsetsSource(paramAnonymousParcel);
    }
    
    public InsetsSource[] newArray(int paramAnonymousInt)
    {
      return new InsetsSource[paramAnonymousInt];
    }
  };
  private final Rect mFrame;
  private final Rect mTmpFrame = new Rect();
  private final int mType;
  private boolean mVisible;
  
  public InsetsSource(int paramInt)
  {
    this.mType = paramInt;
    this.mFrame = new Rect();
  }
  
  public InsetsSource(Parcel paramParcel)
  {
    this.mType = paramParcel.readInt();
    this.mFrame = ((Rect)paramParcel.readParcelable(null));
    this.mVisible = paramParcel.readBoolean();
  }
  
  public InsetsSource(InsetsSource paramInsetsSource)
  {
    this.mType = paramInsetsSource.mType;
    this.mFrame = new Rect(paramInsetsSource.mFrame);
    this.mVisible = paramInsetsSource.mVisible;
  }
  
  public Insets calculateInsets(Rect paramRect, boolean paramBoolean)
  {
    if ((!paramBoolean) && (!this.mVisible)) {
      return Insets.NONE;
    }
    if (!this.mTmpFrame.setIntersect(this.mFrame, paramRect)) {
      return Insets.NONE;
    }
    if (this.mTmpFrame.width() == paramRect.width())
    {
      if (this.mTmpFrame.top == paramRect.top) {
        return Insets.of(0, this.mTmpFrame.height(), 0, 0);
      }
      return Insets.of(0, 0, 0, this.mTmpFrame.height());
    }
    if (this.mTmpFrame.height() == paramRect.height())
    {
      if (this.mTmpFrame.left == paramRect.left) {
        return Insets.of(this.mTmpFrame.width(), 0, 0, 0);
      }
      return Insets.of(0, 0, this.mTmpFrame.width(), 0);
    }
    return Insets.NONE;
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public void dump(String paramString, PrintWriter paramPrintWriter)
  {
    paramPrintWriter.print(paramString);
    paramPrintWriter.print("InsetsSource type=");
    paramPrintWriter.print(InsetsState.typeToString(this.mType));
    paramPrintWriter.print(" frame=");
    paramPrintWriter.print(this.mFrame.toShortString());
    paramPrintWriter.print(" visible=");
    paramPrintWriter.print(this.mVisible);
    paramPrintWriter.println();
  }
  
  public boolean equals(Object paramObject)
  {
    if (this == paramObject) {
      return true;
    }
    if ((paramObject != null) && (getClass() == paramObject.getClass()))
    {
      paramObject = (InsetsSource)paramObject;
      if (this.mType != ((InsetsSource)paramObject).mType) {
        return false;
      }
      if (this.mVisible != ((InsetsSource)paramObject).mVisible) {
        return false;
      }
      return this.mFrame.equals(((InsetsSource)paramObject).mFrame);
    }
    return false;
  }
  
  public Rect getFrame()
  {
    return this.mFrame;
  }
  
  public int getType()
  {
    return this.mType;
  }
  
  public int hashCode()
  {
    return (this.mType * 31 + this.mFrame.hashCode()) * 31 + this.mVisible;
  }
  
  public boolean isVisible()
  {
    return this.mVisible;
  }
  
  public void setFrame(Rect paramRect)
  {
    this.mFrame.set(paramRect);
  }
  
  public void setVisible(boolean paramBoolean)
  {
    this.mVisible = paramBoolean;
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeInt(this.mType);
    paramParcel.writeParcelable(this.mFrame, 0);
    paramParcel.writeBoolean(this.mVisible);
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/view/InsetsSource.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */