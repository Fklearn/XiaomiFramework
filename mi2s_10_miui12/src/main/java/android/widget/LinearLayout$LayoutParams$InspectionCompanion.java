package android.widget;

import android.view.inspector.InspectionCompanion;
import android.view.inspector.InspectionCompanion.UninitializedPropertyMapException;
import android.view.inspector.PropertyMapper;
import android.view.inspector.PropertyReader;

public final class LinearLayout$LayoutParams$InspectionCompanion
  implements InspectionCompanion<LinearLayout.LayoutParams>
{
  private int mLayout_gravityId;
  private int mLayout_weightId;
  private boolean mPropertiesMapped = false;
  
  public void mapProperties(PropertyMapper paramPropertyMapper)
  {
    this.mLayout_gravityId = paramPropertyMapper.mapGravity("layout_gravity", 16842931);
    this.mLayout_weightId = paramPropertyMapper.mapFloat("layout_weight", 16843137);
    this.mPropertiesMapped = true;
  }
  
  public void readProperties(LinearLayout.LayoutParams paramLayoutParams, PropertyReader paramPropertyReader)
  {
    if (this.mPropertiesMapped)
    {
      paramPropertyReader.readGravity(this.mLayout_gravityId, paramLayoutParams.gravity);
      paramPropertyReader.readFloat(this.mLayout_weightId, paramLayoutParams.weight);
      return;
    }
    throw new InspectionCompanion.UninitializedPropertyMapException();
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/widget/LinearLayout$LayoutParams$InspectionCompanion.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */