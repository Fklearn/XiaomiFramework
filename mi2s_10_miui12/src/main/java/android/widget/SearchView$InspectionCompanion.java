package android.widget;

import android.view.inspector.InspectionCompanion;
import android.view.inspector.InspectionCompanion.UninitializedPropertyMapException;
import android.view.inspector.PropertyMapper;
import android.view.inspector.PropertyReader;

public final class SearchView$InspectionCompanion
  implements InspectionCompanion<SearchView>
{
  private int mIconifiedByDefaultId;
  private int mIconifiedId;
  private int mMaxWidthId;
  private boolean mPropertiesMapped = false;
  private int mQueryHintId;
  private int mQueryId;
  
  public void mapProperties(PropertyMapper paramPropertyMapper)
  {
    this.mIconifiedId = paramPropertyMapper.mapBoolean("iconified", 0);
    this.mIconifiedByDefaultId = paramPropertyMapper.mapBoolean("iconifiedByDefault", 16843514);
    this.mMaxWidthId = paramPropertyMapper.mapInt("maxWidth", 16843039);
    this.mQueryId = paramPropertyMapper.mapObject("query", 0);
    this.mQueryHintId = paramPropertyMapper.mapObject("queryHint", 16843608);
    this.mPropertiesMapped = true;
  }
  
  public void readProperties(SearchView paramSearchView, PropertyReader paramPropertyReader)
  {
    if (this.mPropertiesMapped)
    {
      paramPropertyReader.readBoolean(this.mIconifiedId, paramSearchView.isIconified());
      paramPropertyReader.readBoolean(this.mIconifiedByDefaultId, paramSearchView.isIconifiedByDefault());
      paramPropertyReader.readInt(this.mMaxWidthId, paramSearchView.getMaxWidth());
      paramPropertyReader.readObject(this.mQueryId, paramSearchView.getQuery());
      paramPropertyReader.readObject(this.mQueryHintId, paramSearchView.getQueryHint());
      return;
    }
    throw new InspectionCompanion.UninitializedPropertyMapException();
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/widget/SearchView$InspectionCompanion.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */