package android.widget;

import android.view.inspector.InspectionCompanion;
import android.view.inspector.InspectionCompanion.UninitializedPropertyMapException;
import android.view.inspector.PropertyMapper;
import android.view.inspector.PropertyReader;

public final class Chronometer$InspectionCompanion
  implements InspectionCompanion<Chronometer>
{
  private int mCountDownId;
  private int mFormatId;
  private boolean mPropertiesMapped = false;
  
  public void mapProperties(PropertyMapper paramPropertyMapper)
  {
    this.mCountDownId = paramPropertyMapper.mapBoolean("countDown", 16844059);
    this.mFormatId = paramPropertyMapper.mapObject("format", 16843013);
    this.mPropertiesMapped = true;
  }
  
  public void readProperties(Chronometer paramChronometer, PropertyReader paramPropertyReader)
  {
    if (this.mPropertiesMapped)
    {
      paramPropertyReader.readBoolean(this.mCountDownId, paramChronometer.isCountDown());
      paramPropertyReader.readObject(this.mFormatId, paramChronometer.getFormat());
      return;
    }
    throw new InspectionCompanion.UninitializedPropertyMapException();
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/widget/Chronometer$InspectionCompanion.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */