package android.widget;

import android.view.inspector.InspectionCompanion;
import android.view.inspector.InspectionCompanion.UninitializedPropertyMapException;
import android.view.inspector.PropertyMapper;
import android.view.inspector.PropertyReader;

public final class Toolbar$InspectionCompanion
  implements InspectionCompanion<Toolbar>
{
  private int mCollapseContentDescriptionId;
  private int mCollapseIconId;
  private int mContentInsetEndId;
  private int mContentInsetEndWithActionsId;
  private int mContentInsetLeftId;
  private int mContentInsetRightId;
  private int mContentInsetStartId;
  private int mContentInsetStartWithNavigationId;
  private int mLogoDescriptionId;
  private int mLogoId;
  private int mNavigationContentDescriptionId;
  private int mNavigationIconId;
  private int mPopupThemeId;
  private boolean mPropertiesMapped = false;
  private int mSubtitleId;
  private int mTitleId;
  private int mTitleMarginBottomId;
  private int mTitleMarginEndId;
  private int mTitleMarginStartId;
  private int mTitleMarginTopId;
  
  public void mapProperties(PropertyMapper paramPropertyMapper)
  {
    this.mCollapseContentDescriptionId = paramPropertyMapper.mapObject("collapseContentDescription", 16843984);
    this.mCollapseIconId = paramPropertyMapper.mapObject("collapseIcon", 16844031);
    this.mContentInsetEndId = paramPropertyMapper.mapInt("contentInsetEnd", 16843860);
    this.mContentInsetEndWithActionsId = paramPropertyMapper.mapInt("contentInsetEndWithActions", 16844067);
    this.mContentInsetLeftId = paramPropertyMapper.mapInt("contentInsetLeft", 16843861);
    this.mContentInsetRightId = paramPropertyMapper.mapInt("contentInsetRight", 16843862);
    this.mContentInsetStartId = paramPropertyMapper.mapInt("contentInsetStart", 16843859);
    this.mContentInsetStartWithNavigationId = paramPropertyMapper.mapInt("contentInsetStartWithNavigation", 16844066);
    this.mLogoId = paramPropertyMapper.mapObject("logo", 16843454);
    this.mLogoDescriptionId = paramPropertyMapper.mapObject("logoDescription", 16844009);
    this.mNavigationContentDescriptionId = paramPropertyMapper.mapObject("navigationContentDescription", 16843969);
    this.mNavigationIconId = paramPropertyMapper.mapObject("navigationIcon", 16843968);
    this.mPopupThemeId = paramPropertyMapper.mapInt("popupTheme", 16843945);
    this.mSubtitleId = paramPropertyMapper.mapObject("subtitle", 16843473);
    this.mTitleId = paramPropertyMapper.mapObject("title", 16843233);
    this.mTitleMarginBottomId = paramPropertyMapper.mapInt("titleMarginBottom", 16844028);
    this.mTitleMarginEndId = paramPropertyMapper.mapInt("titleMarginEnd", 16844026);
    this.mTitleMarginStartId = paramPropertyMapper.mapInt("titleMarginStart", 16844025);
    this.mTitleMarginTopId = paramPropertyMapper.mapInt("titleMarginTop", 16844027);
    this.mPropertiesMapped = true;
  }
  
  public void readProperties(Toolbar paramToolbar, PropertyReader paramPropertyReader)
  {
    if (this.mPropertiesMapped)
    {
      paramPropertyReader.readObject(this.mCollapseContentDescriptionId, paramToolbar.getCollapseContentDescription());
      paramPropertyReader.readObject(this.mCollapseIconId, paramToolbar.getCollapseIcon());
      paramPropertyReader.readInt(this.mContentInsetEndId, paramToolbar.getContentInsetEnd());
      paramPropertyReader.readInt(this.mContentInsetEndWithActionsId, paramToolbar.getContentInsetEndWithActions());
      paramPropertyReader.readInt(this.mContentInsetLeftId, paramToolbar.getContentInsetLeft());
      paramPropertyReader.readInt(this.mContentInsetRightId, paramToolbar.getContentInsetRight());
      paramPropertyReader.readInt(this.mContentInsetStartId, paramToolbar.getContentInsetStart());
      paramPropertyReader.readInt(this.mContentInsetStartWithNavigationId, paramToolbar.getContentInsetStartWithNavigation());
      paramPropertyReader.readObject(this.mLogoId, paramToolbar.getLogo());
      paramPropertyReader.readObject(this.mLogoDescriptionId, paramToolbar.getLogoDescription());
      paramPropertyReader.readObject(this.mNavigationContentDescriptionId, paramToolbar.getNavigationContentDescription());
      paramPropertyReader.readObject(this.mNavigationIconId, paramToolbar.getNavigationIcon());
      paramPropertyReader.readInt(this.mPopupThemeId, paramToolbar.getPopupTheme());
      paramPropertyReader.readObject(this.mSubtitleId, paramToolbar.getSubtitle());
      paramPropertyReader.readObject(this.mTitleId, paramToolbar.getTitle());
      paramPropertyReader.readInt(this.mTitleMarginBottomId, paramToolbar.getTitleMarginBottom());
      paramPropertyReader.readInt(this.mTitleMarginEndId, paramToolbar.getTitleMarginEnd());
      paramPropertyReader.readInt(this.mTitleMarginStartId, paramToolbar.getTitleMarginStart());
      paramPropertyReader.readInt(this.mTitleMarginTopId, paramToolbar.getTitleMarginTop());
      return;
    }
    throw new InspectionCompanion.UninitializedPropertyMapException();
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/widget/Toolbar$InspectionCompanion.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */