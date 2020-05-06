package android.widget;

import android.view.inspector.InspectionCompanion;
import android.view.inspector.InspectionCompanion.UninitializedPropertyMapException;
import android.view.inspector.PropertyMapper;
import android.view.inspector.PropertyReader;

public final class CalendarView$InspectionCompanion
  implements InspectionCompanion<CalendarView>
{
  private int mDateTextAppearanceId;
  private int mFirstDayOfWeekId;
  private int mFocusedMonthDateColorId;
  private int mMaxDateId;
  private int mMinDateId;
  private boolean mPropertiesMapped = false;
  private int mSelectedDateVerticalBarId;
  private int mSelectedWeekBackgroundColorId;
  private int mShowWeekNumberId;
  private int mShownWeekCountId;
  private int mUnfocusedMonthDateColorId;
  private int mWeekDayTextAppearanceId;
  private int mWeekNumberColorId;
  private int mWeekSeparatorLineColorId;
  
  public void mapProperties(PropertyMapper paramPropertyMapper)
  {
    this.mDateTextAppearanceId = paramPropertyMapper.mapResourceId("dateTextAppearance", 16843593);
    this.mFirstDayOfWeekId = paramPropertyMapper.mapInt("firstDayOfWeek", 16843581);
    this.mFocusedMonthDateColorId = paramPropertyMapper.mapColor("focusedMonthDateColor", 16843587);
    this.mMaxDateId = paramPropertyMapper.mapLong("maxDate", 16843584);
    this.mMinDateId = paramPropertyMapper.mapLong("minDate", 16843583);
    this.mSelectedDateVerticalBarId = paramPropertyMapper.mapObject("selectedDateVerticalBar", 16843591);
    this.mSelectedWeekBackgroundColorId = paramPropertyMapper.mapColor("selectedWeekBackgroundColor", 16843586);
    this.mShowWeekNumberId = paramPropertyMapper.mapBoolean("showWeekNumber", 16843582);
    this.mShownWeekCountId = paramPropertyMapper.mapInt("shownWeekCount", 16843585);
    this.mUnfocusedMonthDateColorId = paramPropertyMapper.mapColor("unfocusedMonthDateColor", 16843588);
    this.mWeekDayTextAppearanceId = paramPropertyMapper.mapResourceId("weekDayTextAppearance", 16843592);
    this.mWeekNumberColorId = paramPropertyMapper.mapColor("weekNumberColor", 16843589);
    this.mWeekSeparatorLineColorId = paramPropertyMapper.mapColor("weekSeparatorLineColor", 16843590);
    this.mPropertiesMapped = true;
  }
  
  public void readProperties(CalendarView paramCalendarView, PropertyReader paramPropertyReader)
  {
    if (this.mPropertiesMapped)
    {
      paramPropertyReader.readResourceId(this.mDateTextAppearanceId, paramCalendarView.getDateTextAppearance());
      paramPropertyReader.readInt(this.mFirstDayOfWeekId, paramCalendarView.getFirstDayOfWeek());
      paramPropertyReader.readColor(this.mFocusedMonthDateColorId, paramCalendarView.getFocusedMonthDateColor());
      paramPropertyReader.readLong(this.mMaxDateId, paramCalendarView.getMaxDate());
      paramPropertyReader.readLong(this.mMinDateId, paramCalendarView.getMinDate());
      paramPropertyReader.readObject(this.mSelectedDateVerticalBarId, paramCalendarView.getSelectedDateVerticalBar());
      paramPropertyReader.readColor(this.mSelectedWeekBackgroundColorId, paramCalendarView.getSelectedWeekBackgroundColor());
      paramPropertyReader.readBoolean(this.mShowWeekNumberId, paramCalendarView.getShowWeekNumber());
      paramPropertyReader.readInt(this.mShownWeekCountId, paramCalendarView.getShownWeekCount());
      paramPropertyReader.readColor(this.mUnfocusedMonthDateColorId, paramCalendarView.getUnfocusedMonthDateColor());
      paramPropertyReader.readResourceId(this.mWeekDayTextAppearanceId, paramCalendarView.getWeekDayTextAppearance());
      paramPropertyReader.readColor(this.mWeekNumberColorId, paramCalendarView.getWeekNumberColor());
      paramPropertyReader.readColor(this.mWeekSeparatorLineColorId, paramCalendarView.getWeekSeparatorLineColor());
      return;
    }
    throw new InspectionCompanion.UninitializedPropertyMapException();
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/widget/CalendarView$InspectionCompanion.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */