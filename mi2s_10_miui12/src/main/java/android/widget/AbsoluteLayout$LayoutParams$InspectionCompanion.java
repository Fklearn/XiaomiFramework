package android.widget;

import android.view.inspector.InspectionCompanion;
import android.view.inspector.InspectionCompanion.UninitializedPropertyMapException;
import android.view.inspector.PropertyMapper;
import android.view.inspector.PropertyReader;

public final class AbsoluteLayout$LayoutParams$InspectionCompanion
  implements InspectionCompanion<AbsoluteLayout.LayoutParams>
{
  private int mLayout_xId;
  private int mLayout_yId;
  private boolean mPropertiesMapped = false;
  
  public void mapProperties(PropertyMapper paramPropertyMapper)
  {
    this.mLayout_xId = paramPropertyMapper.mapInt("layout_x", 16843135);
    this.mLayout_yId = paramPropertyMapper.mapInt("layout_y", 16843136);
    this.mPropertiesMapped = true;
  }
  
  public void readProperties(AbsoluteLayout.LayoutParams paramLayoutParams, PropertyReader paramPropertyReader)
  {
    if (this.mPropertiesMapped)
    {
      paramPropertyReader.readInt(this.mLayout_xId, paramLayoutParams.x);
      paramPropertyReader.readInt(this.mLayout_yId, paramLayoutParams.y);
      return;
    }
    throw new InspectionCompanion.UninitializedPropertyMapException();
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/widget/AbsoluteLayout$LayoutParams$InspectionCompanion.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */