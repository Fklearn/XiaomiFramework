package android.widget;

import android.util.SparseArray;
import android.view.inspector.InspectionCompanion;
import android.view.inspector.InspectionCompanion.UninitializedPropertyMapException;
import android.view.inspector.PropertyMapper;
import android.view.inspector.PropertyReader;
import java.util.Objects;

public final class DatePicker$InspectionCompanion
  implements InspectionCompanion<DatePicker>
{
  private int mCalendarViewShownId;
  private int mDatePickerModeId;
  private int mDayOfMonthId;
  private int mFirstDayOfWeekId;
  private int mMaxDateId;
  private int mMinDateId;
  private int mMonthId;
  private boolean mPropertiesMapped = false;
  private int mSpinnersShownId;
  private int mYearId;
  
  public void mapProperties(PropertyMapper paramPropertyMapper)
  {
    this.mCalendarViewShownId = paramPropertyMapper.mapBoolean("calendarViewShown", 16843596);
    SparseArray localSparseArray = new SparseArray();
    localSparseArray.put(1, "spinner");
    localSparseArray.put(2, "calendar");
    Objects.requireNonNull(localSparseArray);
    this.mDatePickerModeId = paramPropertyMapper.mapIntEnum("datePickerMode", 16843955, new _..Lambda.QY3N4tzLteuFdjRnyJFCbR1ajSI(localSparseArray));
    this.mDayOfMonthId = paramPropertyMapper.mapInt("dayOfMonth", 0);
    this.mFirstDayOfWeekId = paramPropertyMapper.mapInt("firstDayOfWeek", 16843581);
    this.mMaxDateId = paramPropertyMapper.mapLong("maxDate", 16843584);
    this.mMinDateId = paramPropertyMapper.mapLong("minDate", 16843583);
    this.mMonthId = paramPropertyMapper.mapInt("month", 0);
    this.mSpinnersShownId = paramPropertyMapper.mapBoolean("spinnersShown", 16843595);
    this.mYearId = paramPropertyMapper.mapInt("year", 0);
    this.mPropertiesMapped = true;
  }
  
  public void readProperties(DatePicker paramDatePicker, PropertyReader paramPropertyReader)
  {
    if (this.mPropertiesMapped)
    {
      paramPropertyReader.readBoolean(this.mCalendarViewShownId, paramDatePicker.getCalendarViewShown());
      paramPropertyReader.readIntEnum(this.mDatePickerModeId, paramDatePicker.getMode());
      paramPropertyReader.readInt(this.mDayOfMonthId, paramDatePicker.getDayOfMonth());
      paramPropertyReader.readInt(this.mFirstDayOfWeekId, paramDatePicker.getFirstDayOfWeek());
      paramPropertyReader.readLong(this.mMaxDateId, paramDatePicker.getMaxDate());
      paramPropertyReader.readLong(this.mMinDateId, paramDatePicker.getMinDate());
      paramPropertyReader.readInt(this.mMonthId, paramDatePicker.getMonth());
      paramPropertyReader.readBoolean(this.mSpinnersShownId, paramDatePicker.getSpinnersShown());
      paramPropertyReader.readInt(this.mYearId, paramDatePicker.getYear());
      return;
    }
    throw new InspectionCompanion.UninitializedPropertyMapException();
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/widget/DatePicker$InspectionCompanion.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */