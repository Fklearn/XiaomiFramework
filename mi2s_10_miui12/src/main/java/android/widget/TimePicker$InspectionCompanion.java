package android.widget;

import android.util.SparseArray;
import android.view.inspector.InspectionCompanion;
import android.view.inspector.InspectionCompanion.UninitializedPropertyMapException;
import android.view.inspector.PropertyMapper;
import android.view.inspector.PropertyReader;
import java.util.Objects;

public final class TimePicker$InspectionCompanion
  implements InspectionCompanion<TimePicker>
{
  private int m24HourId;
  private int mHourId;
  private int mMinuteId;
  private boolean mPropertiesMapped = false;
  private int mTimePickerModeId;
  
  public void mapProperties(PropertyMapper paramPropertyMapper)
  {
    this.m24HourId = paramPropertyMapper.mapBoolean("24Hour", 0);
    this.mHourId = paramPropertyMapper.mapInt("hour", 0);
    this.mMinuteId = paramPropertyMapper.mapInt("minute", 0);
    SparseArray localSparseArray = new SparseArray();
    localSparseArray.put(1, "spinner");
    localSparseArray.put(2, "clock");
    Objects.requireNonNull(localSparseArray);
    this.mTimePickerModeId = paramPropertyMapper.mapIntEnum("timePickerMode", 16843956, new _..Lambda.QY3N4tzLteuFdjRnyJFCbR1ajSI(localSparseArray));
    this.mPropertiesMapped = true;
  }
  
  public void readProperties(TimePicker paramTimePicker, PropertyReader paramPropertyReader)
  {
    if (this.mPropertiesMapped)
    {
      paramPropertyReader.readBoolean(this.m24HourId, paramTimePicker.is24HourView());
      paramPropertyReader.readInt(this.mHourId, paramTimePicker.getHour());
      paramPropertyReader.readInt(this.mMinuteId, paramTimePicker.getMinute());
      paramPropertyReader.readIntEnum(this.mTimePickerModeId, paramTimePicker.getMode());
      return;
    }
    throw new InspectionCompanion.UninitializedPropertyMapException();
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/widget/TimePicker$InspectionCompanion.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */