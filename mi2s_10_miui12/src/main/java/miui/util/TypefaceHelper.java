package miui.util;

import android.graphics.Typeface;
import android.graphics.fonts.FontVariationAxis;
import android.util.Log;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;

public class TypefaceHelper
{
  private static final String TAG = "TypefaceHelper";
  private static Field sDefaultStyles = getDefaultWithStyle();
  private static Method sSetDefault = ;
  
  public static Typeface createVarFont(Typeface paramTypeface, int paramInt)
  {
    return Typeface.createFromTypefaceWithVariation(paramTypeface, Arrays.asList(new FontVariationAxis[] { new FontVariationAxis("wght", paramInt) }));
  }
  
  private static Field getDefaultWithStyle()
  {
    try
    {
      Field localField = Typeface.class.getDeclaredField("sDefaults");
      localField.setAccessible(true);
      return localField;
    }
    catch (Exception localException)
    {
      Log.w("TypefaceHelper", "TypefaceHelper.getDefaultWithStyle failed", localException);
    }
    return null;
  }
  
  private static Method getSetDefaultMethod()
  {
    try
    {
      Method localMethod = Typeface.class.getDeclaredMethod("setDefault", new Class[] { Typeface.class });
      localMethod.setAccessible(true);
      return localMethod;
    }
    catch (Exception localException)
    {
      Log.w("TypefaceHelper", "TypefaceHelper.getSetDefaultMethod failed", localException);
    }
    return null;
  }
  
  public static void updateDefaultFont(Typeface paramTypeface)
  {
    Method localMethod = sSetDefault;
    if (localMethod == null)
    {
      Log.w("TypefaceHelper", "TypefaceHelper.updateDefaultFont, sSetDefault is null");
      return;
    }
    try
    {
      localMethod.invoke(null, new Object[] { paramTypeface });
    }
    catch (Exception paramTypeface)
    {
      Log.w("TypefaceHelper", "TypefaceHelper.updateDefaultFont failed", paramTypeface);
    }
  }
  
  public static void updateDefaultWithStyle(Typeface paramTypeface1, Typeface paramTypeface2, Typeface paramTypeface3, Typeface paramTypeface4)
  {
    Field localField = sDefaultStyles;
    if (localField == null)
    {
      Log.w("TypefaceHelper", "TypefaceHelper.updateDefaultWithStyle, sDefaultStyles is null");
      return;
    }
    try
    {
      localField.set(null, new Typeface[] { paramTypeface1, paramTypeface2, paramTypeface3, paramTypeface4 });
    }
    catch (Exception paramTypeface1)
    {
      Log.w("TypefaceHelper", "TypefaceHelper.updateDefaultWithStyle failed", paramTypeface1);
    }
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes4-dex2jar.jar!/miui/util/TypefaceHelper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */