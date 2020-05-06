package android.view;

import android.view.inspector.InspectionCompanion;
import android.view.inspector.InspectionCompanion.UninitializedPropertyMapException;
import android.view.inspector.PropertyMapper;
import android.view.inspector.PropertyReader;

public final class ViewGroup$MarginLayoutParams$InspectionCompanion
  implements InspectionCompanion<ViewGroup.MarginLayoutParams>
{
  private int mLayout_marginBottomId;
  private int mLayout_marginLeftId;
  private int mLayout_marginRightId;
  private int mLayout_marginTopId;
  private boolean mPropertiesMapped = false;
  
  public void mapProperties(PropertyMapper paramPropertyMapper)
  {
    this.mLayout_marginBottomId = paramPropertyMapper.mapInt("layout_marginBottom", 16843002);
    this.mLayout_marginLeftId = paramPropertyMapper.mapInt("layout_marginLeft", 16842999);
    this.mLayout_marginRightId = paramPropertyMapper.mapInt("layout_marginRight", 16843001);
    this.mLayout_marginTopId = paramPropertyMapper.mapInt("layout_marginTop", 16843000);
    this.mPropertiesMapped = true;
  }
  
  public void readProperties(ViewGroup.MarginLayoutParams paramMarginLayoutParams, PropertyReader paramPropertyReader)
  {
    if (this.mPropertiesMapped)
    {
      paramPropertyReader.readInt(this.mLayout_marginBottomId, paramMarginLayoutParams.bottomMargin);
      paramPropertyReader.readInt(this.mLayout_marginLeftId, paramMarginLayoutParams.leftMargin);
      paramPropertyReader.readInt(this.mLayout_marginRightId, paramMarginLayoutParams.rightMargin);
      paramPropertyReader.readInt(this.mLayout_marginTopId, paramMarginLayoutParams.topMargin);
      return;
    }
    throw new InspectionCompanion.UninitializedPropertyMapException();
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/view/ViewGroup$MarginLayoutParams$InspectionCompanion.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */