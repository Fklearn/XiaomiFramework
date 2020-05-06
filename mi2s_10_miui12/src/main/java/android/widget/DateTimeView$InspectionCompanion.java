package android.widget;

import android.view.inspector.InspectionCompanion;
import android.view.inspector.InspectionCompanion.UninitializedPropertyMapException;
import android.view.inspector.PropertyMapper;
import android.view.inspector.PropertyReader;

public final class DateTimeView$InspectionCompanion
  implements InspectionCompanion<DateTimeView>
{
  private boolean mPropertiesMapped = false;
  private int mShowReleativeId;
  
  public void mapProperties(PropertyMapper paramPropertyMapper)
  {
    this.mShowReleativeId = paramPropertyMapper.mapBoolean("showReleative", 0);
    this.mPropertiesMapped = true;
  }
  
  public void readProperties(DateTimeView paramDateTimeView, PropertyReader paramPropertyReader)
  {
    if (this.mPropertiesMapped)
    {
      paramPropertyReader.readBoolean(this.mShowReleativeId, paramDateTimeView.isShowRelativeTime());
      return;
    }
    throw new InspectionCompanion.UninitializedPropertyMapException();
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/widget/DateTimeView$InspectionCompanion.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */