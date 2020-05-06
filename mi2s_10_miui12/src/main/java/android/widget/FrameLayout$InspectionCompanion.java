package android.widget;

import android.view.inspector.InspectionCompanion;
import android.view.inspector.InspectionCompanion.UninitializedPropertyMapException;
import android.view.inspector.PropertyMapper;
import android.view.inspector.PropertyReader;

public final class FrameLayout$InspectionCompanion
  implements InspectionCompanion<FrameLayout>
{
  private int mMeasureAllChildrenId;
  private boolean mPropertiesMapped = false;
  
  public void mapProperties(PropertyMapper paramPropertyMapper)
  {
    this.mMeasureAllChildrenId = paramPropertyMapper.mapBoolean("measureAllChildren", 16843018);
    this.mPropertiesMapped = true;
  }
  
  public void readProperties(FrameLayout paramFrameLayout, PropertyReader paramPropertyReader)
  {
    if (this.mPropertiesMapped)
    {
      paramPropertyReader.readBoolean(this.mMeasureAllChildrenId, paramFrameLayout.getMeasureAllChildren());
      return;
    }
    throw new InspectionCompanion.UninitializedPropertyMapException();
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/widget/FrameLayout$InspectionCompanion.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */