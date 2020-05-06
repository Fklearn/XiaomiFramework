package android.widget;

import android.view.inspector.InspectionCompanion;
import android.view.inspector.InspectionCompanion.UninitializedPropertyMapException;
import android.view.inspector.PropertyMapper;
import android.view.inspector.PropertyReader;

public final class Switch$InspectionCompanion
  implements InspectionCompanion<Switch>
{
  private boolean mPropertiesMapped = false;
  private int mShowTextId;
  private int mSplitTrackId;
  private int mSwitchMinWidthId;
  private int mSwitchPaddingId;
  private int mTextOffId;
  private int mTextOnId;
  private int mThumbId;
  private int mThumbTextPaddingId;
  private int mThumbTintBlendModeId;
  private int mThumbTintId;
  private int mThumbTintModeId;
  private int mTrackId;
  private int mTrackTintBlendModeId;
  private int mTrackTintId;
  private int mTrackTintModeId;
  
  public void mapProperties(PropertyMapper paramPropertyMapper)
  {
    this.mShowTextId = paramPropertyMapper.mapBoolean("showText", 16843949);
    this.mSplitTrackId = paramPropertyMapper.mapBoolean("splitTrack", 16843852);
    this.mSwitchMinWidthId = paramPropertyMapper.mapInt("switchMinWidth", 16843632);
    this.mSwitchPaddingId = paramPropertyMapper.mapInt("switchPadding", 16843633);
    this.mTextOffId = paramPropertyMapper.mapObject("textOff", 16843045);
    this.mTextOnId = paramPropertyMapper.mapObject("textOn", 16843044);
    this.mThumbId = paramPropertyMapper.mapObject("thumb", 16843074);
    this.mThumbTextPaddingId = paramPropertyMapper.mapInt("thumbTextPadding", 16843634);
    this.mThumbTintId = paramPropertyMapper.mapObject("thumbTint", 16843889);
    this.mThumbTintBlendModeId = paramPropertyMapper.mapObject("thumbTintBlendMode", 10);
    this.mThumbTintModeId = paramPropertyMapper.mapObject("thumbTintMode", 16843890);
    this.mTrackId = paramPropertyMapper.mapObject("track", 16843631);
    this.mTrackTintId = paramPropertyMapper.mapObject("trackTint", 16843993);
    this.mTrackTintBlendModeId = paramPropertyMapper.mapObject("trackTintBlendMode", 13);
    this.mTrackTintModeId = paramPropertyMapper.mapObject("trackTintMode", 16843994);
    this.mPropertiesMapped = true;
  }
  
  public void readProperties(Switch paramSwitch, PropertyReader paramPropertyReader)
  {
    if (this.mPropertiesMapped)
    {
      paramPropertyReader.readBoolean(this.mShowTextId, paramSwitch.getShowText());
      paramPropertyReader.readBoolean(this.mSplitTrackId, paramSwitch.getSplitTrack());
      paramPropertyReader.readInt(this.mSwitchMinWidthId, paramSwitch.getSwitchMinWidth());
      paramPropertyReader.readInt(this.mSwitchPaddingId, paramSwitch.getSwitchPadding());
      paramPropertyReader.readObject(this.mTextOffId, paramSwitch.getTextOff());
      paramPropertyReader.readObject(this.mTextOnId, paramSwitch.getTextOn());
      paramPropertyReader.readObject(this.mThumbId, paramSwitch.getThumbDrawable());
      paramPropertyReader.readInt(this.mThumbTextPaddingId, paramSwitch.getThumbTextPadding());
      paramPropertyReader.readObject(this.mThumbTintId, paramSwitch.getThumbTintList());
      paramPropertyReader.readObject(this.mThumbTintBlendModeId, paramSwitch.getThumbTintBlendMode());
      paramPropertyReader.readObject(this.mThumbTintModeId, paramSwitch.getThumbTintMode());
      paramPropertyReader.readObject(this.mTrackId, paramSwitch.getTrackDrawable());
      paramPropertyReader.readObject(this.mTrackTintId, paramSwitch.getTrackTintList());
      paramPropertyReader.readObject(this.mTrackTintBlendModeId, paramSwitch.getTrackTintBlendMode());
      paramPropertyReader.readObject(this.mTrackTintModeId, paramSwitch.getTrackTintMode());
      return;
    }
    throw new InspectionCompanion.UninitializedPropertyMapException();
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/widget/Switch$InspectionCompanion.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */