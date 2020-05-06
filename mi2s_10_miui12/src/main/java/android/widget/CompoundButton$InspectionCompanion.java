package android.widget;

import android.view.inspector.InspectionCompanion;
import android.view.inspector.InspectionCompanion.UninitializedPropertyMapException;
import android.view.inspector.PropertyMapper;
import android.view.inspector.PropertyReader;

public final class CompoundButton$InspectionCompanion
  implements InspectionCompanion<CompoundButton>
{
  private int mButtonBlendModeId;
  private int mButtonId;
  private int mButtonTintId;
  private int mButtonTintModeId;
  private int mCheckedId;
  private boolean mPropertiesMapped = false;
  
  public void mapProperties(PropertyMapper paramPropertyMapper)
  {
    this.mButtonId = paramPropertyMapper.mapObject("button", 16843015);
    this.mButtonBlendModeId = paramPropertyMapper.mapObject("buttonBlendMode", 3);
    this.mButtonTintId = paramPropertyMapper.mapObject("buttonTint", 16843887);
    this.mButtonTintModeId = paramPropertyMapper.mapObject("buttonTintMode", 16843888);
    this.mCheckedId = paramPropertyMapper.mapBoolean("checked", 16843014);
    this.mPropertiesMapped = true;
  }
  
  public void readProperties(CompoundButton paramCompoundButton, PropertyReader paramPropertyReader)
  {
    if (this.mPropertiesMapped)
    {
      paramPropertyReader.readObject(this.mButtonId, paramCompoundButton.getButtonDrawable());
      paramPropertyReader.readObject(this.mButtonBlendModeId, paramCompoundButton.getButtonTintBlendMode());
      paramPropertyReader.readObject(this.mButtonTintId, paramCompoundButton.getButtonTintList());
      paramPropertyReader.readObject(this.mButtonTintModeId, paramCompoundButton.getButtonTintMode());
      paramPropertyReader.readBoolean(this.mCheckedId, paramCompoundButton.isChecked());
      return;
    }
    throw new InspectionCompanion.UninitializedPropertyMapException();
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/widget/CompoundButton$InspectionCompanion.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */