package android.widget;

import android.view.inspector.InspectionCompanion;
import android.view.inspector.InspectionCompanion.UninitializedPropertyMapException;
import android.view.inspector.PropertyMapper;
import android.view.inspector.PropertyReader;

public final class ViewFlipper$InspectionCompanion
  implements InspectionCompanion<ViewFlipper>
{
  private int mAutoStartId;
  private int mFlipIntervalId;
  private int mFlippingId;
  private boolean mPropertiesMapped = false;
  
  public void mapProperties(PropertyMapper paramPropertyMapper)
  {
    this.mAutoStartId = paramPropertyMapper.mapBoolean("autoStart", 16843445);
    this.mFlipIntervalId = paramPropertyMapper.mapInt("flipInterval", 16843129);
    this.mFlippingId = paramPropertyMapper.mapBoolean("flipping", 0);
    this.mPropertiesMapped = true;
  }
  
  public void readProperties(ViewFlipper paramViewFlipper, PropertyReader paramPropertyReader)
  {
    if (this.mPropertiesMapped)
    {
      paramPropertyReader.readBoolean(this.mAutoStartId, paramViewFlipper.isAutoStart());
      paramPropertyReader.readInt(this.mFlipIntervalId, paramViewFlipper.getFlipInterval());
      paramPropertyReader.readBoolean(this.mFlippingId, paramViewFlipper.isFlipping());
      return;
    }
    throw new InspectionCompanion.UninitializedPropertyMapException();
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/widget/ViewFlipper$InspectionCompanion.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */