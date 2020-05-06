package android.view;

import android.annotation.UnsupportedAppUsage;
import android.graphics.CanvasProperty;
import android.graphics.MiuiRecordingCanvas;
import android.graphics.Paint;

public abstract class DisplayListCanvas
  extends MiuiRecordingCanvas
{
  protected DisplayListCanvas(long paramLong)
  {
    super(paramLong);
  }
  
  @UnsupportedAppUsage
  public abstract void drawCircle(CanvasProperty<Float> paramCanvasProperty1, CanvasProperty<Float> paramCanvasProperty2, CanvasProperty<Float> paramCanvasProperty3, CanvasProperty<Paint> paramCanvasProperty);
  
  @UnsupportedAppUsage
  public abstract void drawRoundRect(CanvasProperty<Float> paramCanvasProperty1, CanvasProperty<Float> paramCanvasProperty2, CanvasProperty<Float> paramCanvasProperty3, CanvasProperty<Float> paramCanvasProperty4, CanvasProperty<Float> paramCanvasProperty5, CanvasProperty<Float> paramCanvasProperty6, CanvasProperty<Paint> paramCanvasProperty);
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/view/DisplayListCanvas.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */