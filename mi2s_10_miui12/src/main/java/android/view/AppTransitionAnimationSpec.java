package android.view;

import android.annotation.UnsupportedAppUsage;
import android.graphics.GraphicBuffer;
import android.graphics.Rect;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

public class AppTransitionAnimationSpec
  implements Parcelable
{
  public static final Parcelable.Creator<AppTransitionAnimationSpec> CREATOR = new Parcelable.Creator()
  {
    public AppTransitionAnimationSpec createFromParcel(Parcel paramAnonymousParcel)
    {
      return new AppTransitionAnimationSpec(paramAnonymousParcel);
    }
    
    public AppTransitionAnimationSpec[] newArray(int paramAnonymousInt)
    {
      return new AppTransitionAnimationSpec[paramAnonymousInt];
    }
  };
  public final GraphicBuffer buffer;
  public final Rect rect;
  public final int taskId;
  
  @UnsupportedAppUsage
  public AppTransitionAnimationSpec(int paramInt, GraphicBuffer paramGraphicBuffer, Rect paramRect)
  {
    this.taskId = paramInt;
    this.rect = paramRect;
    this.buffer = paramGraphicBuffer;
  }
  
  public AppTransitionAnimationSpec(Parcel paramParcel)
  {
    this.taskId = paramParcel.readInt();
    this.rect = ((Rect)paramParcel.readParcelable(null));
    this.buffer = ((GraphicBuffer)paramParcel.readParcelable(null));
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("{taskId: ");
    localStringBuilder.append(this.taskId);
    localStringBuilder.append(", buffer: ");
    localStringBuilder.append(this.buffer);
    localStringBuilder.append(", rect: ");
    localStringBuilder.append(this.rect);
    localStringBuilder.append("}");
    return localStringBuilder.toString();
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeInt(this.taskId);
    paramParcel.writeParcelable(this.rect, 0);
    paramParcel.writeParcelable(this.buffer, 0);
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/view/AppTransitionAnimationSpec.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */