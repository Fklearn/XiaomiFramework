package android.widget;

import android.view.inspector.InspectionCompanion;
import android.view.inspector.InspectionCompanion.UninitializedPropertyMapException;
import android.view.inspector.PropertyMapper;
import android.view.inspector.PropertyReader;

public final class ToggleButton$InspectionCompanion
  implements InspectionCompanion<ToggleButton>
{
  private int mDisabledAlphaId;
  private boolean mPropertiesMapped = false;
  private int mTextOffId;
  private int mTextOnId;
  
  public void mapProperties(PropertyMapper paramPropertyMapper)
  {
    this.mDisabledAlphaId = paramPropertyMapper.mapFloat("disabledAlpha", 16842803);
    this.mTextOffId = paramPropertyMapper.mapObject("textOff", 16843045);
    this.mTextOnId = paramPropertyMapper.mapObject("textOn", 16843044);
    this.mPropertiesMapped = true;
  }
  
  public void readProperties(ToggleButton paramToggleButton, PropertyReader paramPropertyReader)
  {
    if (this.mPropertiesMapped)
    {
      paramPropertyReader.readFloat(this.mDisabledAlphaId, paramToggleButton.getDisabledAlpha());
      paramPropertyReader.readObject(this.mTextOffId, paramToggleButton.getTextOff());
      paramPropertyReader.readObject(this.mTextOnId, paramToggleButton.getTextOn());
      return;
    }
    throw new InspectionCompanion.UninitializedPropertyMapException();
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/widget/ToggleButton$InspectionCompanion.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */