package miui.util;

public class ExquisiteModeUtils
{
  public static final float DEFAULT_EXQUISITE_SCALE_VALUE;
  public static final float DEFAULT_MIUI_SCALE_VALUE = 1.0F;
  public static final String MIUI_SCALE_FIELD_NAME = "miuiScale";
  public static final boolean SUPPORT_EXQUISITE_MODE;
  
  static
  {
    boolean bool = true;
    DEFAULT_EXQUISITE_SCALE_VALUE = FeatureParser.getInteger("exquisite_mode_target_density", 1) * 1.0F / FeatureParser.getInteger("exquisite_mode_origin_density", 1);
    if (DEFAULT_EXQUISITE_SCALE_VALUE == 1.0F) {
      bool = false;
    }
    SUPPORT_EXQUISITE_MODE = bool;
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes4-dex2jar.jar!/miui/util/ExquisiteModeUtils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */