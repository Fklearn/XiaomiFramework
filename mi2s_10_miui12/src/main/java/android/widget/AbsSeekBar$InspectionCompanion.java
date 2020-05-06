package android.widget;

import android.view.inspector.InspectionCompanion;
import android.view.inspector.InspectionCompanion.UninitializedPropertyMapException;
import android.view.inspector.PropertyMapper;
import android.view.inspector.PropertyReader;

public final class AbsSeekBar$InspectionCompanion
  implements InspectionCompanion<AbsSeekBar>
{
  private boolean mPropertiesMapped = false;
  private int mThumbTintId;
  private int mThumbTintModeId;
  private int mTickMarkTintBlendModeId;
  private int mTickMarkTintId;
  private int mTickMarkTintModeId;
  
  public void mapProperties(PropertyMapper paramPropertyMapper)
  {
    this.mThumbTintId = paramPropertyMapper.mapObject("thumbTint", 16843889);
    this.mThumbTintModeId = paramPropertyMapper.mapObject("thumbTintMode", 16843890);
    this.mTickMarkTintId = paramPropertyMapper.mapObject("tickMarkTint", 16844043);
    this.mTickMarkTintBlendModeId = paramPropertyMapper.mapObject("tickMarkTintBlendMode", 7);
    this.mTickMarkTintModeId = paramPropertyMapper.mapObject("tickMarkTintMode", 16844044);
    this.mPropertiesMapped = true;
  }
  
  public void readProperties(AbsSeekBar paramAbsSeekBar, PropertyReader paramPropertyReader)
  {
    if (this.mPropertiesMapped)
    {
      paramPropertyReader.readObject(this.mThumbTintId, paramAbsSeekBar.getThumbTintList());
      paramPropertyReader.readObject(this.mThumbTintModeId, paramAbsSeekBar.getThumbTintMode());
      paramPropertyReader.readObject(this.mTickMarkTintId, paramAbsSeekBar.getTickMarkTintList());
      paramPropertyReader.readObject(this.mTickMarkTintBlendModeId, paramAbsSeekBar.getTickMarkTintBlendMode());
      paramPropertyReader.readObject(this.mTickMarkTintModeId, paramAbsSeekBar.getTickMarkTintMode());
      return;
    }
    throw new InspectionCompanion.UninitializedPropertyMapException();
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/widget/AbsSeekBar$InspectionCompanion.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */