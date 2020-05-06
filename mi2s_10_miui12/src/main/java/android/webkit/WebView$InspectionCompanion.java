package android.webkit;

import android.util.SparseArray;
import android.view.inspector.InspectionCompanion;
import android.view.inspector.InspectionCompanion.UninitializedPropertyMapException;
import android.view.inspector.PropertyMapper;
import android.view.inspector.PropertyReader;
import java.util.Objects;

public final class WebView$InspectionCompanion
  implements InspectionCompanion<WebView>
{
  private int mContentHeightId;
  private int mFaviconId;
  private int mOriginalUrlId;
  private int mProgressId;
  private boolean mPropertiesMapped = false;
  private int mRendererPriorityWaivedWhenNotVisibleId;
  private int mRendererRequestedPriorityId;
  private int mTitleId;
  private int mUrlId;
  
  public void mapProperties(PropertyMapper paramPropertyMapper)
  {
    this.mContentHeightId = paramPropertyMapper.mapInt("contentHeight", 0);
    this.mFaviconId = paramPropertyMapper.mapObject("favicon", 0);
    this.mOriginalUrlId = paramPropertyMapper.mapObject("originalUrl", 0);
    this.mProgressId = paramPropertyMapper.mapInt("progress", 0);
    this.mRendererPriorityWaivedWhenNotVisibleId = paramPropertyMapper.mapBoolean("rendererPriorityWaivedWhenNotVisible", 0);
    SparseArray localSparseArray = new SparseArray();
    localSparseArray.put(0, "waived");
    localSparseArray.put(1, "bound");
    localSparseArray.put(2, "important");
    Objects.requireNonNull(localSparseArray);
    this.mRendererRequestedPriorityId = paramPropertyMapper.mapIntEnum("rendererRequestedPriority", 0, new _..Lambda.QY3N4tzLteuFdjRnyJFCbR1ajSI(localSparseArray));
    this.mTitleId = paramPropertyMapper.mapObject("title", 0);
    this.mUrlId = paramPropertyMapper.mapObject("url", 0);
    this.mPropertiesMapped = true;
  }
  
  public void readProperties(WebView paramWebView, PropertyReader paramPropertyReader)
  {
    if (this.mPropertiesMapped)
    {
      paramPropertyReader.readInt(this.mContentHeightId, paramWebView.getContentHeight());
      paramPropertyReader.readObject(this.mFaviconId, paramWebView.getFavicon());
      paramPropertyReader.readObject(this.mOriginalUrlId, paramWebView.getOriginalUrl());
      paramPropertyReader.readInt(this.mProgressId, paramWebView.getProgress());
      paramPropertyReader.readBoolean(this.mRendererPriorityWaivedWhenNotVisibleId, paramWebView.getRendererPriorityWaivedWhenNotVisible());
      paramPropertyReader.readIntEnum(this.mRendererRequestedPriorityId, paramWebView.getRendererRequestedPriority());
      paramPropertyReader.readObject(this.mTitleId, paramWebView.getTitle());
      paramPropertyReader.readObject(this.mUrlId, paramWebView.getUrl());
      return;
    }
    throw new InspectionCompanion.UninitializedPropertyMapException();
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/webkit/WebView$InspectionCompanion.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */