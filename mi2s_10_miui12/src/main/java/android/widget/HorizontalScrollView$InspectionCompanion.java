package android.widget;

import android.view.inspector.InspectionCompanion;
import android.view.inspector.InspectionCompanion.UninitializedPropertyMapException;
import android.view.inspector.PropertyMapper;
import android.view.inspector.PropertyReader;

public final class HorizontalScrollView$InspectionCompanion
  implements InspectionCompanion<HorizontalScrollView>
{
  private int mFillViewportId;
  private boolean mPropertiesMapped = false;
  
  public void mapProperties(PropertyMapper paramPropertyMapper)
  {
    this.mFillViewportId = paramPropertyMapper.mapBoolean("fillViewport", 16843130);
    this.mPropertiesMapped = true;
  }
  
  public void readProperties(HorizontalScrollView paramHorizontalScrollView, PropertyReader paramPropertyReader)
  {
    if (this.mPropertiesMapped)
    {
      paramPropertyReader.readBoolean(this.mFillViewportId, paramHorizontalScrollView.isFillViewport());
      return;
    }
    throw new InspectionCompanion.UninitializedPropertyMapException();
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/widget/HorizontalScrollView$InspectionCompanion.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */