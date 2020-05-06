package android.widget;

import android.view.inspector.InspectionCompanion;
import android.view.inspector.InspectionCompanion.UninitializedPropertyMapException;
import android.view.inspector.PropertyMapper;
import android.view.inspector.PropertyReader;

public final class Spinner$InspectionCompanion
  implements InspectionCompanion<Spinner>
{
  private int mDropDownHorizontalOffsetId;
  private int mDropDownVerticalOffsetId;
  private int mDropDownWidthId;
  private int mGravityId;
  private int mPopupBackgroundId;
  private int mPromptId;
  private boolean mPropertiesMapped = false;
  
  public void mapProperties(PropertyMapper paramPropertyMapper)
  {
    this.mDropDownHorizontalOffsetId = paramPropertyMapper.mapInt("dropDownHorizontalOffset", 16843436);
    this.mDropDownVerticalOffsetId = paramPropertyMapper.mapInt("dropDownVerticalOffset", 16843437);
    this.mDropDownWidthId = paramPropertyMapper.mapInt("dropDownWidth", 16843362);
    this.mGravityId = paramPropertyMapper.mapGravity("gravity", 16842927);
    this.mPopupBackgroundId = paramPropertyMapper.mapObject("popupBackground", 16843126);
    this.mPromptId = paramPropertyMapper.mapObject("prompt", 16843131);
    this.mPropertiesMapped = true;
  }
  
  public void readProperties(Spinner paramSpinner, PropertyReader paramPropertyReader)
  {
    if (this.mPropertiesMapped)
    {
      paramPropertyReader.readInt(this.mDropDownHorizontalOffsetId, paramSpinner.getDropDownHorizontalOffset());
      paramPropertyReader.readInt(this.mDropDownVerticalOffsetId, paramSpinner.getDropDownVerticalOffset());
      paramPropertyReader.readInt(this.mDropDownWidthId, paramSpinner.getDropDownWidth());
      paramPropertyReader.readGravity(this.mGravityId, paramSpinner.getGravity());
      paramPropertyReader.readObject(this.mPopupBackgroundId, paramSpinner.getPopupBackground());
      paramPropertyReader.readObject(this.mPromptId, paramSpinner.getPrompt());
      return;
    }
    throw new InspectionCompanion.UninitializedPropertyMapException();
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/widget/Spinner$InspectionCompanion.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */