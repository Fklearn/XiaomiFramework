package android.view;

import android.graphics.RecordingCanvas;
import android.graphics.Rect;

public abstract interface WindowCallbacks
{
  public static final int RESIZE_MODE_DOCKED_DIVIDER = 1;
  public static final int RESIZE_MODE_FREEFORM = 0;
  public static final int RESIZE_MODE_INVALID = -1;
  
  public abstract boolean onContentDrawn(int paramInt1, int paramInt2, int paramInt3, int paramInt4);
  
  public abstract void onPostDraw(RecordingCanvas paramRecordingCanvas);
  
  public abstract void onRequestDraw(boolean paramBoolean);
  
  public abstract void onWindowDragResizeEnd();
  
  public abstract void onWindowDragResizeStart(Rect paramRect1, boolean paramBoolean, Rect paramRect2, Rect paramRect3, int paramInt);
  
  public abstract void onWindowSizeIsChanging(Rect paramRect1, boolean paramBoolean, Rect paramRect2, Rect paramRect3);
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/view/WindowCallbacks.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */