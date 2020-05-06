package android.widget;

import android.view.inspector.InspectionCompanion;
import android.view.inspector.InspectionCompanion.UninitializedPropertyMapException;
import android.view.inspector.PropertyMapper;
import android.view.inspector.PropertyReader;

public final class FrameLayout$LayoutParams$InspectionCompanion
  implements InspectionCompanion<FrameLayout.LayoutParams>
{
  private int mLayout_gravityId;
  private boolean mPropertiesMapped = false;
  
  public void mapProperties(PropertyMapper paramPropertyMapper)
  {
    this.mLayout_gravityId = paramPropertyMapper.mapGravity("layout_gravity", 16842931);
    this.mPropertiesMapped = true;
  }
  
  public void readProperties(FrameLayout.LayoutParams paramLayoutParams, PropertyReader paramPropertyReader)
  {
    if (this.mPropertiesMapped)
    {
      paramPropertyReader.readGravity(this.mLayout_gravityId, paramLayoutParams.gravity);
      return;
    }
    throw new InspectionCompanion.UninitializedPropertyMapException();
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/widget/FrameLayout$LayoutParams$InspectionCompanion.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */