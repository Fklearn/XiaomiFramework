package android.widget;

import android.view.inspector.InspectionCompanion;
import android.view.inspector.InspectionCompanion.UninitializedPropertyMapException;
import android.view.inspector.PropertyMapper;
import android.view.inspector.PropertyReader;

public final class TextClock$InspectionCompanion
  implements InspectionCompanion<TextClock>
{
  private int mFormat12HourId;
  private int mFormat24HourId;
  private int mIs24HourModeEnabledId;
  private boolean mPropertiesMapped = false;
  private int mTimeZoneId;
  
  public void mapProperties(PropertyMapper paramPropertyMapper)
  {
    this.mFormat12HourId = paramPropertyMapper.mapObject("format12Hour", 16843722);
    this.mFormat24HourId = paramPropertyMapper.mapObject("format24Hour", 16843723);
    this.mIs24HourModeEnabledId = paramPropertyMapper.mapBoolean("is24HourModeEnabled", 0);
    this.mTimeZoneId = paramPropertyMapper.mapObject("timeZone", 16843724);
    this.mPropertiesMapped = true;
  }
  
  public void readProperties(TextClock paramTextClock, PropertyReader paramPropertyReader)
  {
    if (this.mPropertiesMapped)
    {
      paramPropertyReader.readObject(this.mFormat12HourId, paramTextClock.getFormat12Hour());
      paramPropertyReader.readObject(this.mFormat24HourId, paramTextClock.getFormat24Hour());
      paramPropertyReader.readBoolean(this.mIs24HourModeEnabledId, paramTextClock.is24HourModeEnabled());
      paramPropertyReader.readObject(this.mTimeZoneId, paramTextClock.getTimeZone());
      return;
    }
    throw new InspectionCompanion.UninitializedPropertyMapException();
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/widget/TextClock$InspectionCompanion.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */