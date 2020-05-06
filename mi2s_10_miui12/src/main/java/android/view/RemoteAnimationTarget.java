package android.view;

import android.annotation.UnsupportedAppUsage;
import android.app.WindowConfiguration;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.util.proto.ProtoOutputStream;
import java.io.PrintWriter;
import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class RemoteAnimationTarget
  implements Parcelable
{
  public static final Parcelable.Creator<RemoteAnimationTarget> CREATOR = new Parcelable.Creator()
  {
    public RemoteAnimationTarget createFromParcel(Parcel paramAnonymousParcel)
    {
      return new RemoteAnimationTarget(paramAnonymousParcel);
    }
    
    public RemoteAnimationTarget[] newArray(int paramAnonymousInt)
    {
      return new RemoteAnimationTarget[paramAnonymousInt];
    }
  };
  public static final int MODE_CHANGING = 2;
  public static final int MODE_CLOSING = 1;
  public static final int MODE_OPENING = 0;
  @UnsupportedAppUsage
  public final Rect clipRect;
  @UnsupportedAppUsage
  public final Rect contentInsets;
  @UnsupportedAppUsage
  public boolean isNotInRecents;
  @UnsupportedAppUsage
  public final boolean isTranslucent;
  @UnsupportedAppUsage
  public final SurfaceControl leash;
  @UnsupportedAppUsage
  public final int mode;
  @UnsupportedAppUsage
  public final Point position;
  @UnsupportedAppUsage
  public final int prefixOrderIndex;
  @UnsupportedAppUsage
  public final Rect sourceContainerBounds;
  @UnsupportedAppUsage
  public final Rect startBounds;
  @UnsupportedAppUsage
  public final SurfaceControl startLeash;
  @UnsupportedAppUsage
  public final int taskId;
  @UnsupportedAppUsage
  public final WindowConfiguration windowConfiguration;
  
  public RemoteAnimationTarget(int paramInt1, int paramInt2, SurfaceControl paramSurfaceControl1, boolean paramBoolean1, Rect paramRect1, Rect paramRect2, int paramInt3, Point paramPoint, Rect paramRect3, WindowConfiguration paramWindowConfiguration, boolean paramBoolean2, SurfaceControl paramSurfaceControl2, Rect paramRect4)
  {
    this.mode = paramInt2;
    this.taskId = paramInt1;
    this.leash = paramSurfaceControl1;
    this.isTranslucent = paramBoolean1;
    this.clipRect = new Rect(paramRect1);
    this.contentInsets = new Rect(paramRect2);
    this.prefixOrderIndex = paramInt3;
    this.position = new Point(paramPoint);
    this.sourceContainerBounds = new Rect(paramRect3);
    this.windowConfiguration = paramWindowConfiguration;
    this.isNotInRecents = paramBoolean2;
    this.startLeash = paramSurfaceControl2;
    if (paramRect4 == null) {
      paramSurfaceControl1 = null;
    } else {
      paramSurfaceControl1 = new Rect(paramRect4);
    }
    this.startBounds = paramSurfaceControl1;
  }
  
  public RemoteAnimationTarget(Parcel paramParcel)
  {
    this.taskId = paramParcel.readInt();
    this.mode = paramParcel.readInt();
    this.leash = ((SurfaceControl)paramParcel.readParcelable(null));
    this.isTranslucent = paramParcel.readBoolean();
    this.clipRect = ((Rect)paramParcel.readParcelable(null));
    this.contentInsets = ((Rect)paramParcel.readParcelable(null));
    this.prefixOrderIndex = paramParcel.readInt();
    this.position = ((Point)paramParcel.readParcelable(null));
    this.sourceContainerBounds = ((Rect)paramParcel.readParcelable(null));
    this.windowConfiguration = ((WindowConfiguration)paramParcel.readParcelable(null));
    this.isNotInRecents = paramParcel.readBoolean();
    this.startLeash = ((SurfaceControl)paramParcel.readParcelable(null));
    this.startBounds = ((Rect)paramParcel.readParcelable(null));
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public void dump(PrintWriter paramPrintWriter, String paramString)
  {
    paramPrintWriter.print(paramString);
    paramPrintWriter.print("mode=");
    paramPrintWriter.print(this.mode);
    paramPrintWriter.print(" taskId=");
    paramPrintWriter.print(this.taskId);
    paramPrintWriter.print(" isTranslucent=");
    paramPrintWriter.print(this.isTranslucent);
    paramPrintWriter.print(" clipRect=");
    this.clipRect.printShortString(paramPrintWriter);
    paramPrintWriter.print(" contentInsets=");
    this.contentInsets.printShortString(paramPrintWriter);
    paramPrintWriter.print(" prefixOrderIndex=");
    paramPrintWriter.print(this.prefixOrderIndex);
    paramPrintWriter.print(" position=");
    this.position.printShortString(paramPrintWriter);
    paramPrintWriter.print(" sourceContainerBounds=");
    this.sourceContainerBounds.printShortString(paramPrintWriter);
    paramPrintWriter.println();
    paramPrintWriter.print(paramString);
    paramPrintWriter.print("windowConfiguration=");
    paramPrintWriter.println(this.windowConfiguration);
    paramPrintWriter.print(paramString);
    paramPrintWriter.print("leash=");
    paramPrintWriter.println(this.leash);
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeInt(this.taskId);
    paramParcel.writeInt(this.mode);
    paramParcel.writeParcelable(this.leash, 0);
    paramParcel.writeBoolean(this.isTranslucent);
    paramParcel.writeParcelable(this.clipRect, 0);
    paramParcel.writeParcelable(this.contentInsets, 0);
    paramParcel.writeInt(this.prefixOrderIndex);
    paramParcel.writeParcelable(this.position, 0);
    paramParcel.writeParcelable(this.sourceContainerBounds, 0);
    paramParcel.writeParcelable(this.windowConfiguration, 0);
    paramParcel.writeBoolean(this.isNotInRecents);
    paramParcel.writeParcelable(this.startLeash, 0);
    paramParcel.writeParcelable(this.startBounds, 0);
  }
  
  public void writeToProto(ProtoOutputStream paramProtoOutputStream, long paramLong)
  {
    paramLong = paramProtoOutputStream.start(paramLong);
    paramProtoOutputStream.write(1120986464257L, this.taskId);
    paramProtoOutputStream.write(1120986464258L, this.mode);
    this.leash.writeToProto(paramProtoOutputStream, 1146756268035L);
    paramProtoOutputStream.write(1133871366148L, this.isTranslucent);
    this.clipRect.writeToProto(paramProtoOutputStream, 1146756268037L);
    this.contentInsets.writeToProto(paramProtoOutputStream, 1146756268038L);
    paramProtoOutputStream.write(1120986464263L, this.prefixOrderIndex);
    this.position.writeToProto(paramProtoOutputStream, 1146756268040L);
    this.sourceContainerBounds.writeToProto(paramProtoOutputStream, 1146756268041L);
    this.windowConfiguration.writeToProto(paramProtoOutputStream, 1146756268042L);
    Object localObject = this.startLeash;
    if (localObject != null) {
      ((SurfaceControl)localObject).writeToProto(paramProtoOutputStream, 1146756268043L);
    }
    localObject = this.startBounds;
    if (localObject != null) {
      ((Rect)localObject).writeToProto(paramProtoOutputStream, 1146756268044L);
    }
    paramProtoOutputStream.end(paramLong);
  }
  
  @Retention(RetentionPolicy.SOURCE)
  public static @interface Mode {}
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/view/RemoteAnimationTarget.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */