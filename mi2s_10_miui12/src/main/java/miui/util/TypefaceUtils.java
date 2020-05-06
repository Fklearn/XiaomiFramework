package miui.util;

import android.app.UiModeManager;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.MiuiConfiguration;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.graphics.Typeface.Builder;
import android.os.Bundle;
import android.provider.Settings.System;
import android.util.ArrayMap;
import android.util.DisplayMetrics;
import android.util.SparseArray;
import java.io.File;
import java.lang.reflect.Field;
import java.util.Map;
import miui.os.Build;
import miui.os.SystemProperties;

public class TypefaceUtils
{
  private static final String BOLD = "bold";
  private static final int BOLD_WEIGHT_DELTA = 300;
  private static final String DEFAULT_FAMILY = "sans-serif";
  private static final int DEFAULT_IDX = 4;
  private static final int DEFAULT_SCALE = 50;
  private static final String DEMIBOLD = "demibold";
  private static final String EXTRALIGHT = "extralight";
  private static final String FONTS_FOLDER = "/data/system/theme/fonts/";
  private static final String[] FONT_NAME_DEFAULT = { "sans-serif" };
  private static final String[] FONT_NAME_MIPRO_MEDIUM = { "mipro-medium" };
  private static final String KEY_FONT_WEIGHT = "key_miui_font_weight_scale";
  private static final String KEY_USE_MIUI_FONT = "use_miui_font";
  private static final String MIPRO_FAMILY = "mipro";
  private static final String MITYPE_FAMILY = "mitype";
  private static final String MITYPE_MONO_FAMILY = "mitype-mono";
  private static final String MIUI_FAMILY = "miui";
  private static final String NORMAL = "normal";
  private static final String POSFIX_BLACK = "-black";
  private static final String POSFIX_LIGHT = "-light";
  private static final String POSFIX_MEDIUM = "-medium";
  private static final String POSFIX_THIN = "-thin";
  private static final String ROBOTO_BLACK = "Roboto-Black.ttf";
  private static final String ROBOTO_BOLD = "Roboto-Bold.ttf";
  private static final String ROBOTO_ITALIC = "Roboto-Italic.ttf";
  private static final String ROBOTO_ITALIC_BLACK = "Roboto-BlackItalic.ttf";
  private static final String ROBOTO_ITALIC_BOLD = "Roboto-BoldItalic.ttf";
  private static final String ROBOTO_ITALIC_LIGHT = "Roboto-LightItalic.ttf";
  private static final String ROBOTO_ITALIC_MEDIUM = "Roboto-MediumItalic.ttf";
  private static final String ROBOTO_ITALIC_THIN = "Roboto-ThinItalic.ttf";
  private static final String ROBOTO_LIGHT = "Roboto-Light.ttf";
  private static final String ROBOTO_MEDIUM = "Roboto-Medium.ttf";
  private static final String ROBOTO_REGULAR = "Roboto-Regular.ttf";
  private static final String ROBOTO_THIN = "Roboto-Thin.ttf";
  private static final String SEMIBOLD = "semibold";
  private static final String TAG = "TypefaceUtils";
  private static final boolean USING_VAR_FONT;
  private static final int WEIGHT_BOLD = 700;
  private static final int WEIGHT_DEMIBOLD = 550;
  private static final int WEIGHT_EXTRALIGHT = 200;
  private static final int WEIGHT_HEAVY = 900;
  private static final int WEIGHT_LIGHT = 300;
  private static final int WEIGHT_MEDIUM = 500;
  private static final int WEIGHT_NORMAL = 350;
  private static final int WEIGHT_REGULAR = 400;
  private static final int WEIGHT_SEMIBOLD = 600;
  private static final int WEIGHT_THIN = 100;
  private static final Field sFamilyNameField = getFamilyNameField();
  private static final Map<String, Boolean> sUsingMiuiFontMap = new ArrayMap();
  
  static
  {
    boolean bool;
    if ((sFamilyNameField != null) && (new File("system/fonts/MiLanProVF.ttf").exists())) {
      bool = true;
    } else {
      bool = false;
    }
    USING_VAR_FONT = bool;
  }
  
  private static boolean checkUsingThemeFont()
  {
    Object localObject = new File("/data/system/theme/fonts/");
    boolean bool1 = ((File)localObject).exists();
    boolean bool2 = false;
    if (bool1)
    {
      localObject = ((File)localObject).list();
      bool1 = bool2;
      if (localObject != null)
      {
        bool1 = bool2;
        if (localObject.length > 0) {
          bool1 = true;
        }
      }
      Holder.sIsUsingThemeFont = Boolean.valueOf(bool1);
    }
    else
    {
      Holder.sIsUsingThemeFont = Boolean.valueOf(false);
    }
    return Holder.sIsUsingThemeFont.booleanValue();
  }
  
  private static void clearThemeFont()
  {
    Holder.SYS_FONT_CACHE.clear();
    Holder.SYS_FONT_ITALIC_CACHE.clear();
    Holder.THEME_MIUI_FONT_CACHE.clear();
    Holder.THEME_MIUIEX_FONT_CACHE.clear();
  }
  
  private static String[] convertFontNames(String[] paramArrayOfString)
  {
    if (paramArrayOfString != null) {
      for (int i = 0; i < Holder.DEFAULT_NAME_MAP.length; i += 2) {
        for (int j = 0; j < paramArrayOfString.length; j++) {
          if (paramArrayOfString[j].contains(Holder.DEFAULT_NAME_MAP[i]))
          {
            paramArrayOfString = new StringBuilder();
            paramArrayOfString.append("mipro-");
            paramArrayOfString.append(Holder.DEFAULT_NAME_MAP[(i + 1)]);
            return new String[] { paramArrayOfString.toString() };
          }
        }
      }
    }
    return Holder.MIUI_NAMES;
  }
  
  private static Typeface convertMiuiFontToSysFont(Typeface paramTypeface, String[] paramArrayOfString)
  {
    return Typeface.create(convertToSysFontName(paramArrayOfString), paramTypeface.getStyle());
  }
  
  private static String convertToSysFontName(String[] paramArrayOfString)
  {
    int i = 0;
    while (i < paramArrayOfString.length)
    {
      if (paramArrayOfString[i].contains("extralight")) {
        return "sans-serif-thin";
      }
      if (paramArrayOfString[i].contains("normal")) {
        return "sans-serif-light";
      }
      if ((!paramArrayOfString[i].contains("demibold")) && (!paramArrayOfString[i].contains("semibold")))
      {
        if (paramArrayOfString[i].contains("bold")) {
          return "sans-serif-black";
        }
        i++;
      }
      else
      {
        return "sans-serif-medium";
      }
    }
    Object localObject1 = "sans-serif";
    i = 1;
    while (i < Holder.DEFAULT_NAME_MAP.length)
    {
      Object localObject2;
      for (int j = 0;; j++)
      {
        localObject2 = localObject1;
        if (j >= paramArrayOfString.length) {
          break;
        }
        if (paramArrayOfString[j].contains(Holder.DEFAULT_NAME_MAP[i]))
        {
          localObject1 = new StringBuilder();
          ((StringBuilder)localObject1).append("sans-serif-");
          ((StringBuilder)localObject1).append(Holder.DEFAULT_NAME_MAP[(i - 1)]);
          localObject2 = ((StringBuilder)localObject1).toString();
          break;
        }
      }
      i += 2;
      localObject1 = localObject2;
    }
    return (String)localObject1;
  }
  
  private static void createAndCacheFont(String paramString, int paramInt, String[] paramArrayOfString, SparseArray<Typeface> paramSparseArray, boolean paramBoolean)
  {
    if (paramSparseArray.indexOfKey(paramInt) < 0)
    {
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append("/data/system/theme/fonts/");
      localStringBuilder.append(paramString);
      paramString = new Typeface.Builder(localStringBuilder.toString()).setWeight(paramInt).setItalic(paramBoolean).build();
      setFontNames(paramString, paramArrayOfString);
      paramSparseArray.put(paramInt, paramString);
    }
  }
  
  private static Typeface doReplaceWithVarFont(Context paramContext, Typeface paramTypeface, float paramFloat, boolean paramBoolean)
  {
    String[] arrayOfString = getFontNamesByTypeface(paramTypeface, paramBoolean);
    if (paramContext == null) {
      paramFloat = -1.0F;
    } else {
      paramFloat /= paramContext.getResources().getDisplayMetrics().scaledDensity;
    }
    if (isNamesOf(arrayOfString, Holder.MIUI_NAMES)) {
      return getVarFont(paramContext, paramTypeface, paramFloat, paramBoolean, false, Holder.MIUI_VAR_FONT, arrayOfString);
    }
    if (isNamesOf(arrayOfString, new String[] { "mitype-mono" })) {
      return getVarFont(paramContext, paramTypeface, paramFloat, paramBoolean, true, Holder.MITYPE_MONO_VAR_FONT, arrayOfString);
    }
    if (isNamesOf(arrayOfString, new String[] { "mitype" })) {
      return getVarFont(paramContext, paramTypeface, paramFloat, paramBoolean, true, Holder.MITYPE_VAR_FONT, arrayOfString);
    }
    return null;
  }
  
  private static String[] getBoldFontNames(String[] paramArrayOfString)
  {
    int i = getFontNameIdx(paramArrayOfString) + 1;
    if (i < Holder.VF_NAME_ARRAY.length) {
      return Holder.MIUI_VF_NAME[i];
    }
    return paramArrayOfString;
  }
  
  private static Field getFamilyNameField()
  {
    try
    {
      Field localField = Typeface.class.getField("familyName");
      return localField;
    }
    catch (Exception localException)
    {
      Log.i("TypefaceUtils", "Typeface has no familyName field");
    }
    return null;
  }
  
  private static int getFontIndex(String[] paramArrayOfString1, String[] paramArrayOfString2)
  {
    if ((paramArrayOfString1 != null) && (paramArrayOfString1.length != 0))
    {
      for (int i = 0; i < paramArrayOfString2.length; i++) {
        for (int j = 0; j < paramArrayOfString1.length; j++) {
          if (paramArrayOfString1[j].contains(paramArrayOfString2[i])) {
            return i;
          }
        }
      }
      return -1;
    }
    return -1;
  }
  
  private static int getFontNameIdx(String[] paramArrayOfString)
  {
    String[] arrayOfString;
    if (isNamesOf(paramArrayOfString, new String[] { "miui" })) {
      arrayOfString = Holder.MIUI_NAME_ARRAY;
    } else {
      arrayOfString = Holder.VF_NAME_ARRAY;
    }
    return getNameIndex(paramArrayOfString, arrayOfString);
  }
  
  private static String[] getFontNames(Typeface paramTypeface)
  {
    String[] arrayOfString1 = new String[0];
    Field localField = sFamilyNameField;
    String[] arrayOfString2 = arrayOfString1;
    if (localField != null)
    {
      arrayOfString2 = arrayOfString1;
      if (paramTypeface != null) {
        try
        {
          arrayOfString2 = (String[])localField.get(paramTypeface);
        }
        catch (Exception paramTypeface)
        {
          Log.w("TypefaceUtils", "get familyName failed", paramTypeface);
          arrayOfString2 = arrayOfString1;
        }
      }
    }
    return arrayOfString2;
  }
  
  private static String[] getFontNamesByTypeface(Typeface paramTypeface, boolean paramBoolean)
  {
    Object localObject;
    if (paramTypeface == Typeface.DEFAULT_BOLD)
    {
      localObject = FONT_NAME_MIPRO_MEDIUM;
    }
    else
    {
      localObject = getInitFontNames(paramTypeface);
      paramTypeface = (Typeface)localObject;
      if (getFontIndex((String[])localObject, Holder.DEFAULT_FONT_NAMES) >= 0) {
        paramTypeface = convertFontNames((String[])localObject);
      }
      localObject = paramTypeface;
      if (paramBoolean)
      {
        localObject = paramTypeface;
        if (isNamesOf(paramTypeface, new String[] { "miui", "mipro" })) {
          localObject = getBoldFontNames(paramTypeface);
        }
      }
    }
    return (String[])localObject;
  }
  
  private static String[] getInitFontNames(Typeface paramTypeface)
  {
    if ((paramTypeface != null) && (paramTypeface != Typeface.DEFAULT)) {
      return getFontNames(paramTypeface);
    }
    return Holder.MIUI_NAMES;
  }
  
  private static int getNameIndex(String[] paramArrayOfString1, String[] paramArrayOfString2)
  {
    for (int i = 0; i < paramArrayOfString1.length; i++) {
      for (int j = 0; j < paramArrayOfString2.length; j++) {
        if (paramArrayOfString1[i].contains(paramArrayOfString2[j])) {
          return j;
        }
      }
    }
    return 4;
  }
  
  private static int getProperWeight(Typeface paramTypeface, String[] paramArrayOfString, boolean paramBoolean)
  {
    if (paramTypeface == null) {
      return 400;
    }
    int i = getFontNameIdx(paramArrayOfString);
    if ((paramBoolean) && (i < Holder.WEIGHT_KEYS.length - 1)) {
      i = Holder.WEIGHT_KEYS[(i + 1)];
    } else {
      i = Holder.WEIGHT_KEYS[i];
    }
    return i;
  }
  
  private static int getScaleWght(int paramInt1, int paramInt2, int paramInt3, boolean paramBoolean)
  {
    int[][] arrayOfInt = Holder.getRules(paramInt3);
    paramInt3 = Holder.getWeightIndex(paramInt1);
    if (paramInt3 >= 0) {
      return getWghtInRange(paramInt1, arrayOfInt[paramInt3], paramInt2, paramBoolean);
    }
    paramInt1 = Holder.getWeightIndex(400);
    return getWghtInRange(400, Holder.NORAML_RULES[paramInt1], paramInt2, paramBoolean);
  }
  
  private static Typeface getThemeFont(Typeface paramTypeface, int paramInt)
  {
    if (((Holder.sIsUsingThemeFont == null) && (checkUsingThemeFont())) || (Holder.sIsUsingThemeFont.booleanValue()))
    {
      loadAndSetThemeFont();
      return getThemeFontWithStyle(paramTypeface, paramInt);
    }
    clearThemeFont();
    return null;
  }
  
  private static Typeface getThemeFontWithStyle(int paramInt1, int paramInt2, int[] paramArrayOfInt, SparseArray<Typeface> paramSparseArray1, SparseArray<Typeface> paramSparseArray2)
  {
    int i = paramArrayOfInt[paramInt1];
    if ((paramInt2 & 0x1) != 0) {
      paramInt1 = i + 300;
    } else {
      paramInt1 = i;
    }
    if (((paramInt2 & 0x2) != 0) && (paramSparseArray2 != null)) {
      paramInt2 = 1;
    } else {
      paramInt2 = 0;
    }
    if (paramInt2 != 0) {
      paramSparseArray1 = paramSparseArray2;
    }
    if ((paramInt1 != i) && (paramSparseArray1.indexOfKey(paramInt1) >= 0)) {
      return (Typeface)paramSparseArray1.get(paramInt1);
    }
    if (paramSparseArray1.indexOfKey(i) >= 0) {
      return (Typeface)paramSparseArray1.get(i);
    }
    return null;
  }
  
  private static Typeface getThemeFontWithStyle(Typeface paramTypeface, int paramInt)
  {
    String[] arrayOfString;
    if (paramTypeface != null) {
      arrayOfString = getFontNames(paramTypeface);
    } else {
      arrayOfString = FONT_NAME_DEFAULT;
    }
    if (isNamesOf(arrayOfString, new String[] { "miui", "mipro", "mitype", "mitype-mono" })) {
      arrayOfString[0] = convertToSysFontName(arrayOfString);
    }
    int i = getFontIndex(arrayOfString, Holder.DEFAULT_FONT_NAMES);
    if (i >= 0) {
      return getThemeFontWithStyle(i, paramInt, Holder.SYS_FONT_WEIGHT, Holder.SYS_FONT_CACHE, Holder.SYS_FONT_ITALIC_CACHE);
    }
    i = getFontIndex(arrayOfString, Holder.THEME_MIUI_FONT_NAMES);
    if (i >= 0) {
      return getThemeFontWithStyle(i, paramInt, Holder.THEME_MIUI_FONT_WEIGHT, Holder.THEME_MIUI_FONT_CACHE, null);
    }
    i = getFontIndex(arrayOfString, Holder.THEME_MIUIEX_FONT_NAMES);
    if (i >= 0) {
      return getThemeFontWithStyle(i, paramInt, Holder.THEME_MIUIEX_FONT_WEIGHT, Holder.THEME_MIUIEX_FONT_CACHE, null);
    }
    return paramTypeface;
  }
  
  private static Typeface getVarFont(Context paramContext, Typeface paramTypeface1, float paramFloat, boolean paramBoolean1, boolean paramBoolean2, Typeface paramTypeface2, String[] paramArrayOfString)
  {
    int i;
    if (Holder.sFontScale < 0) {
      i = loadFontScaleSetting(paramContext);
    } else {
      i = Holder.sFontScale;
    }
    if (i < 0) {
      return paramTypeface1;
    }
    int j = getProperWeight(paramTypeface1, paramArrayOfString, paramBoolean1);
    int k = Holder.getWght(j, paramBoolean2);
    int m = Holder.getTextSizeGrade(paramFloat);
    paramTypeface1 = Holder.getCachedFont(k, i, m);
    paramContext = paramTypeface1;
    if (paramTypeface1 == null)
    {
      paramContext = TypefaceHelper.createVarFont(paramTypeface2, getScaleWght(j, i, m, paramBoolean2));
      setFontNames(paramContext, paramArrayOfString);
      Holder.cacheFont(paramContext, k, i, m);
    }
    return paramContext;
  }
  
  public static Typeface getVarFontWithStyle(Typeface paramTypeface, int paramInt)
  {
    Typeface localTypeface = getThemeFont(paramTypeface, paramInt);
    if (localTypeface != null) {
      return localTypeface;
    }
    if ((USING_VAR_FONT) && ((isUsingMiFont(paramTypeface)) || ((isMiuiOptimizeEnabled()) && (!Build.IS_INTERNATIONAL_BUILD))))
    {
      boolean bool = true;
      if (paramInt != 1) {
        bool = false;
      }
      localTypeface = doReplaceWithVarFont(null, paramTypeface, -1.0F, bool);
      if (localTypeface != null) {
        return localTypeface;
      }
    }
    return paramTypeface;
  }
  
  private static int getWghtInRange(int paramInt1, int[] paramArrayOfInt, int paramInt2, boolean paramBoolean)
  {
    int i = Holder.getWght(paramArrayOfInt[0], paramBoolean);
    int j = Holder.getWght(paramInt1, paramBoolean);
    int k = Holder.getWght(paramArrayOfInt[1], paramBoolean);
    paramInt1 = j;
    float f;
    if (paramInt2 < 50)
    {
      f = paramInt2 / 50.0F;
      paramInt1 = (int)((1.0F - f) * i + j * f);
    }
    else if (paramInt2 > 50)
    {
      f = (paramInt2 - 50) / 50.0F;
      paramInt1 = (int)((1.0F - f) * j + k * f);
    }
    return paramInt1;
  }
  
  public static void initSystemFont(Context paramContext)
  {
    if (checkUsingThemeFont())
    {
      loadAndSetThemeFont();
      return;
    }
    clearThemeFont();
    if ((USING_VAR_FONT) && (isMiuiOptimizeEnabled()))
    {
      updateUiMode(paramContext);
      loadFontScaleSetting(paramContext);
      updateDefaultFont();
      return;
    }
    restoreTypefaceDefault();
  }
  
  public static boolean isFontChanged(Configuration paramConfiguration)
  {
    boolean bool;
    if ((paramConfiguration.extraConfig.themeChangedFlags & 0x20000000) != 0L) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  private static boolean isMiuiOptimizeEnabled()
  {
    Holder.sIsUsingMiuiFonts = Boolean.valueOf(SystemProperties.getBoolean("persist.sys.miui_optimization", true));
    return Holder.sIsUsingMiuiFonts.booleanValue();
  }
  
  private static boolean isNamesOf(String[] paramArrayOfString1, String... paramVarArgs)
  {
    if ((paramArrayOfString1 != null) && (paramArrayOfString1.length > 0)) {
      for (int i = 0; i < paramVarArgs.length; i++) {
        for (int j = 0; j < paramArrayOfString1.length; j++) {
          if (paramArrayOfString1[j].startsWith(paramVarArgs[i])) {
            return true;
          }
        }
      }
    }
    return false;
  }
  
  public static boolean isUsingMiFont(Typeface paramTypeface)
  {
    return isNamesOf(getFontNames(paramTypeface), new String[] { "miui", "mipro", "mitype", "mitype-mono" });
  }
  
  private static void loadAndSetThemeFont()
  {
    if (Holder.SYS_FONT_CACHE.size() > 0) {
      return;
    }
    loadThemeFont(Holder.DEFAULT_FONT_NAMES, Holder.SYS_FONT_FILES, Holder.SYS_FONT_WEIGHT, Holder.SYS_FONT_CACHE, false);
    loadThemeFont(Holder.DEFAULT_FONT_NAMES, Holder.SYS_FONT_ITALIC_FILES, Holder.SYS_FONT_WEIGHT, Holder.SYS_FONT_ITALIC_CACHE, true);
    loadThemeFont(Holder.THEME_MIUI_FONT_NAMES, Holder.THEME_MIUI_FONT_FILES, Holder.THEME_MIUI_FONT_WEIGHT, Holder.THEME_MIUI_FONT_CACHE, false);
    loadThemeFont(Holder.THEME_MIUIEX_FONT_NAMES, Holder.THEME_MIUIEX_FONT_FILES, Holder.THEME_MIUIEX_FONT_WEIGHT, Holder.THEME_MIUIEX_FONT_CACHE, false);
    TypefaceHelper.updateDefaultFont((Typeface)Holder.SYS_FONT_CACHE.get(400));
    TypefaceHelper.updateDefaultWithStyle((Typeface)Holder.SYS_FONT_CACHE.get(400), (Typeface)Holder.SYS_FONT_CACHE.get(700), (Typeface)Holder.SYS_FONT_ITALIC_CACHE.get(400), (Typeface)Holder.SYS_FONT_ITALIC_CACHE.get(700));
  }
  
  public static int loadFontScaleSetting(Context paramContext)
  {
    if (paramContext != null) {
      Holder.sFontScale = Settings.System.getInt(paramContext.getContentResolver(), "key_miui_font_weight_scale", 50);
    }
    return Holder.sFontScale;
  }
  
  private static void loadThemeFont(String[] paramArrayOfString1, String[] paramArrayOfString2, int[] paramArrayOfInt, SparseArray<Typeface> paramSparseArray, boolean paramBoolean)
  {
    if (paramSparseArray.size() > 0) {
      return;
    }
    for (int i = 0; i < paramArrayOfString1.length; i++)
    {
      int j = paramArrayOfInt[i];
      String str = paramArrayOfString1[i];
      createAndCacheFont(paramArrayOfString2[i], j, new String[] { str }, paramSparseArray, paramBoolean);
    }
  }
  
  public static void onMiuiFontChanged(MiuiConfiguration paramMiuiConfiguration)
  {
    
    if (checkUsingThemeFont())
    {
      loadAndSetThemeFont();
      return;
    }
    if (isMiuiOptimizeEnabled())
    {
      if (paramMiuiConfiguration.extraData != null) {
        Holder.sFontScale = paramMiuiConfiguration.extraData.getInt("key_var_font_scale", 50);
      }
      updateDefaultFont();
    }
  }
  
  public static void onUiModeChange(int paramInt)
  {
    if ((paramInt & 0x30) == 32) {
      Holder.sUiMode = 2;
    } else {
      Holder.sUiMode = 1;
    }
  }
  
  public static Typeface replaceTypeface(Context paramContext, Typeface paramTypeface)
  {
    return replaceTypeface(paramContext, paramTypeface, -1.0F);
  }
  
  public static Typeface replaceTypeface(Context paramContext, Typeface paramTypeface, float paramFloat)
  {
    int i;
    if (paramTypeface != null) {
      i = paramTypeface.getStyle();
    } else {
      i = 0;
    }
    Object localObject = getThemeFont(paramTypeface, i);
    if (localObject != null) {
      return (Typeface)localObject;
    }
    localObject = getFontNames(paramTypeface);
    boolean bool = isNamesOf((String[])localObject, new String[] { "miui", "mipro", "mitype", "mitype-mono" });
    if (Build.IS_INTERNATIONAL_BUILD)
    {
      if (bool) {
        return convertMiuiFontToSysFont(paramTypeface, (String[])localObject);
      }
      return paramTypeface;
    }
    localObject = null;
    if ((Holder.isUsingMiuiFonts()) || (bool)) {
      if (USING_VAR_FONT)
      {
        updateUiMode(paramContext);
        localObject = replaceWithVarFont(paramContext, paramTypeface, paramFloat);
      }
      else
      {
        localObject = replaceWithMiuiFont(paramTypeface);
      }
    }
    if (localObject == null) {
      paramContext = paramTypeface;
    } else {
      paramContext = (Context)localObject;
    }
    return paramContext;
  }
  
  private static Typeface replaceWithMiuiFont(Typeface paramTypeface)
  {
    if ((paramTypeface != null) && (!Typeface.DEFAULT.equals(paramTypeface)) && (!Typeface.DEFAULT_BOLD.equals(paramTypeface)) && (!Typeface.SANS_SERIF.equals(paramTypeface))) {
      return null;
    }
    int i;
    if (paramTypeface == null) {
      i = 0;
    } else {
      i = paramTypeface.getStyle();
    }
    return Holder.MIUI_TYPEFACES[i];
  }
  
  private static Typeface replaceWithVarFont(Context paramContext, Typeface paramTypeface, float paramFloat)
  {
    if ((paramTypeface != null) && (paramTypeface.isItalic())) {
      return null;
    }
    boolean bool;
    if ((paramTypeface != null) && (paramTypeface.isBold())) {
      bool = true;
    } else {
      bool = false;
    }
    return doReplaceWithVarFont(paramContext, paramTypeface, paramFloat, bool);
  }
  
  private static void restoreTypefaceDefault()
  {
    TypefaceHelper.updateDefaultFont(Typeface.DEFAULT);
    Typeface localTypeface1 = Typeface.DEFAULT;
    Typeface localTypeface2 = Typeface.DEFAULT_BOLD;
    String str = (String)null;
    TypefaceHelper.updateDefaultWithStyle(localTypeface1, localTypeface2, Typeface.create(str, 2), Typeface.create(str, 3));
  }
  
  private static void setFontNames(Typeface paramTypeface, String[] paramArrayOfString)
  {
    try
    {
      sFamilyNameField.set(paramTypeface, paramArrayOfString);
    }
    catch (Exception paramTypeface)
    {
      Log.w("TypefaceUtils", "set familyName failed", paramTypeface);
    }
  }
  
  private static void updateDefaultFont()
  {
    Typeface localTypeface1 = getVarFont(null, null, 16.0F, false, false, Holder.MIUI_VAR_FONT, Holder.MIUI_NAMES);
    Typeface localTypeface2 = getVarFontWithStyle(localTypeface1, 1);
    TypefaceHelper.updateDefaultFont(localTypeface1);
    TypefaceHelper.updateDefaultWithStyle(localTypeface1, localTypeface2, localTypeface1, localTypeface2);
  }
  
  public static Typeface updateScaledFont(Typeface paramTypeface)
  {
    if (Holder.sDisableScaleUdpate) {
      return paramTypeface;
    }
    Object localObject = getThemeFont(paramTypeface, paramTypeface.getStyle());
    if (localObject != null) {
      return (Typeface)localObject;
    }
    String[] arrayOfString = getFontNames(paramTypeface);
    int i;
    if (getFontIndex(arrayOfString, Holder.DEFAULT_FONT_NAMES) >= 0) {
      i = 1;
    } else {
      i = 0;
    }
    if ((i == 0) && (!isNamesOf(arrayOfString, Holder.MIUI_NAMES))) {
      return paramTypeface;
    }
    localObject = arrayOfString;
    if (i != 0) {
      localObject = convertFontNames(arrayOfString);
    }
    return getVarFont(null, paramTypeface, -1.0F, false, false, Holder.MIUI_VAR_FONT, (String[])localObject);
  }
  
  private static void updateUiMode(Context paramContext)
  {
    paramContext = (UiModeManager)paramContext.getSystemService("uimode");
    if (paramContext != null) {
      Holder.sUiMode = paramContext.getNightMode();
    }
  }
  
  private static class Holder
  {
    private static final String[] DEFAULT_FONT_NAMES;
    private static final String[] DEFAULT_NAME_MAP;
    private static final SparseArray<FontCacheItem> FONT_CACHE;
    private static final int[][] LARGE_RULES;
    private static final String[] MITYPE_MONO_NAMES;
    private static final Typeface MITYPE_MONO_VAR_FONT;
    private static final String[] MITYPE_NAMES;
    private static final Typeface MITYPE_VAR_FONT;
    private static final int[] MITYPE_WGHT;
    private static final String[] MIUI_NAMES;
    private static final String[] MIUI_NAME_ARRAY;
    private static final Typeface[] MIUI_TYPEFACES;
    private static final Typeface MIUI_VAR_FONT;
    private static final String[][] MIUI_VF_NAME;
    private static final int[] MIUI_WGHT;
    private static final int[] MIUI_WGHT_DARKMODE;
    private static final int[][] NORAML_RULES;
    private static final int SIZE_GRADE_COUNT = 3;
    private static final int[][] SMALL_RULES;
    private static final SparseArray<Typeface> SYS_FONT_CACHE = new SparseArray();
    private static final String[] SYS_FONT_FILES;
    private static final SparseArray<Typeface> SYS_FONT_ITALIC_CACHE = new SparseArray();
    private static final String[] SYS_FONT_ITALIC_FILES;
    private static final int[] SYS_FONT_WEIGHT;
    private static final SparseArray<Typeface> THEME_MIUIEX_FONT_CACHE;
    private static final String[] THEME_MIUIEX_FONT_FILES;
    private static final String[] THEME_MIUIEX_FONT_NAMES;
    private static final int[] THEME_MIUIEX_FONT_WEIGHT;
    private static final SparseArray<Typeface> THEME_MIUI_FONT_CACHE = new SparseArray();
    private static final String[] THEME_MIUI_FONT_FILES;
    private static final String[] THEME_MIUI_FONT_NAMES;
    private static final int[] THEME_MIUI_FONT_WEIGHT;
    private static final String[] VF_NAME_ARRAY;
    private static final int[] WEIGHT_KEYS;
    private static boolean sDisableScaleUdpate;
    static int sFontScale = -1;
    static Boolean sIsUsingMiuiFonts;
    static Boolean sIsUsingThemeFont;
    static int sUiMode;
    
    static
    {
      THEME_MIUIEX_FONT_CACHE = new SparseArray();
      FONT_CACHE = new SparseArray();
      DEFAULT_FONT_NAMES = new String[] { "sans-serif", "sans-serif-thin", "sans-serif-light", "sans-serif-medium", "sans-serif-black", "sans-serif-regular", "arial", "helvetica", "tahoma", "verdana" };
      SYS_FONT_FILES = new String[] { "Roboto-Regular.ttf", "Roboto-Thin.ttf", "Roboto-Light.ttf", "Roboto-Medium.ttf", "Roboto-Black.ttf", "Roboto-Bold.ttf", "Roboto-Regular.ttf", "Roboto-Regular.ttf", "Roboto-Regular.ttf", "Roboto-Regular.ttf" };
      SYS_FONT_ITALIC_FILES = new String[] { "Roboto-Italic.ttf", "Roboto-ThinItalic.ttf", "Roboto-LightItalic.ttf", "Roboto-MediumItalic.ttf", "Roboto-BlackItalic.ttf", "Roboto-BoldItalic.ttf", "Roboto-Italic.ttf", "Roboto-Italic.ttf", "Roboto-Italic.ttf", "Roboto-Italic.ttf" };
      SYS_FONT_WEIGHT = new int[] { 400, 100, 300, 500, 900, 700, 400, 400, 400, 400 };
      THEME_MIUI_FONT_NAMES = new String[] { "miui-bold", "miui", "miui-regular" };
      THEME_MIUI_FONT_FILES = new String[] { "Miui-Bold.ttf", "Miui-Regular.ttf", "Miui-Regular.ttf" };
      THEME_MIUI_FONT_WEIGHT = new int[] { 700, 400, 400 };
      THEME_MIUIEX_FONT_NAMES = new String[] { "miuiex", "miuiex-regular", "miuiex-bold", "miuiex-light" };
      THEME_MIUIEX_FONT_FILES = new String[] { "MiuiEx-Regular.ttf", "MiuiEx-Regular.ttf", "MiuiEx-Bold.ttf", "MiuiEx-Light.ttf" };
      THEME_MIUIEX_FONT_WEIGHT = new int[] { 400, 400, 700, 300 };
      Object localObject2;
      if (TypefaceUtils.USING_VAR_FONT)
      {
        DEFAULT_NAME_MAP = new String[] { "thin", "thin", "light", "light", "medium", "medium", "black", "heavy" };
        VF_NAME_ARRAY = new String[] { "thin", "extralight", "light", "normal", "regular", "medium", "demibold", "semibold", "bold", "heavy" };
        MIUI_VF_NAME = new String[VF_NAME_ARRAY.length][1];
        for (int i = 0; i < VF_NAME_ARRAY.length; i++)
        {
          localObject1 = MIUI_VF_NAME;
          localObject2 = new StringBuilder();
          ((StringBuilder)localObject2).append("mipro-");
          ((StringBuilder)localObject2).append(VF_NAME_ARRAY[i]);
          localObject1[i] = { ((StringBuilder)localObject2).toString() };
        }
        MIUI_NAME_ARRAY = new String[] { "thin", "null", "light", "null", "regular", "bold" };
        WEIGHT_KEYS = new int[] { 100, 200, 300, 350, 400, 500, 550, 600, 700, 900 };
        MIUI_WGHT = new int[] { 150, 200, 250, 305, 340, 400, 480, 540, 630, 700 };
        MIUI_WGHT_DARKMODE = new int[] { 150, 175, 225, 285, 320, 360, 440, 490, 580, 650 };
        MITYPE_WGHT = new int[] { 10, 20, 30, 40, 50, 60, 70, 80, 90, 100 };
        for (i = 0;; i++)
        {
          localObject2 = MIUI_WGHT;
          if (i >= localObject2.length) {
            break;
          }
          FONT_CACHE.put(localObject2[i], new FontCacheItem());
        }
        for (i = 0;; i++)
        {
          localObject2 = MITYPE_WGHT;
          if (i >= localObject2.length) {
            break;
          }
          FONT_CACHE.put(localObject2[i], new FontCacheItem());
        }
        MIUI_NAMES = new String[] { "mipro", "miui" };
        MITYPE_NAMES = new String[] { "mitype" };
        MITYPE_MONO_NAMES = new String[] { "mitype-mono" };
        localObject2 = new int[] { 100, 500 };
        Object localObject1 = { 200, 500 };
        int[] arrayOfInt1 = { 300, 500 };
        int[] arrayOfInt2 = { 350, 550 };
        int[] arrayOfInt3 = { 400, 600 };
        int[] arrayOfInt4 = { 550, 700 };
        int[] arrayOfInt5 = { 700, 900 };
        SMALL_RULES = new int[][] { localObject2, localObject1, arrayOfInt1, { 350, 550 }, arrayOfInt2, arrayOfInt3, { 500, 700 }, arrayOfInt4, { 600, 700 }, arrayOfInt5 };
        NORAML_RULES = new int[][] { { 100, 500 }, { 100, 500 }, { 200, 550 }, { 300, 550 }, { 300, 600 }, { 350, 700 }, { 400, 700 }, { 500, 900 }, { 550, 900 }, { 600, 900 } };
        localObject2 = new int[] { 200, 400 };
        localObject1 = new int[] { 300, 550 };
        LARGE_RULES = new int[][] { { 100, 300 }, { 100, 350 }, localObject2, { 200, 500 }, localObject1, { 300, 600 }, { 350, 700 }, { 400, 900 }, { 500, 900 }, { 550, 900 } };
        MIUI_TYPEFACES = new Typeface[0];
        sDisableScaleUdpate = true;
        MIUI_VAR_FONT = Typeface.create("mipro", 0);
        MITYPE_VAR_FONT = Typeface.create("mitype", 0);
        MITYPE_MONO_VAR_FONT = Typeface.create("mitype-mono", 0);
        sDisableScaleUdpate = false;
      }
      else
      {
        localObject2 = new int[0];
        MITYPE_WGHT = (int[])localObject2;
        MIUI_WGHT_DARKMODE = (int[])localObject2;
        MIUI_WGHT = (int[])localObject2;
        WEIGHT_KEYS = (int[])localObject2;
        localObject2 = new String[0];
        MITYPE_MONO_NAMES = (String[])localObject2;
        MITYPE_NAMES = (String[])localObject2;
        MIUI_NAMES = (String[])localObject2;
        MIUI_NAME_ARRAY = (String[])localObject2;
        VF_NAME_ARRAY = (String[])localObject2;
        DEFAULT_NAME_MAP = (String[])localObject2;
        MIUI_VF_NAME = new String[0][0];
        localObject2 = new int[0][0];
        LARGE_RULES = (int[][])localObject2;
        NORAML_RULES = (int[][])localObject2;
        SMALL_RULES = (int[][])localObject2;
        MITYPE_MONO_VAR_FONT = null;
        MITYPE_VAR_FONT = null;
        MIUI_VAR_FONT = null;
        if (new File("system/fonts/Miui-Regular.ttf").exists())
        {
          MIUI_TYPEFACES = new Typeface[4];
          MIUI_TYPEFACES[0] = Typeface.create("miui", 0);
          MIUI_TYPEFACES[1] = Typeface.create("miui", 1);
          MIUI_TYPEFACES[2] = Typeface.create("miui", 2);
          MIUI_TYPEFACES[3] = Typeface.create("miui", 3);
        }
        else
        {
          MIUI_TYPEFACES = new Typeface[0];
        }
      }
    }
    
    static void cacheFont(Typeface paramTypeface, int paramInt1, int paramInt2, int paramInt3)
    {
      FontCacheItem localFontCacheItem1 = (FontCacheItem)FONT_CACHE.get(paramInt1);
      FontCacheItem localFontCacheItem2 = localFontCacheItem1;
      if (localFontCacheItem1 == null)
      {
        localFontCacheItem2 = new FontCacheItem();
        FONT_CACHE.put(paramInt1, localFontCacheItem2);
      }
      if (localFontCacheItem2.scale != paramInt2)
      {
        localFontCacheItem2.scale = paramInt2;
        localFontCacheItem2.clear();
      }
      localFontCacheItem2.setFont(paramTypeface, paramInt3);
    }
    
    static Typeface getCachedFont(int paramInt1, int paramInt2, int paramInt3)
    {
      FontCacheItem localFontCacheItem = (FontCacheItem)FONT_CACHE.get(paramInt1);
      if ((localFontCacheItem != null) && (localFontCacheItem.scale == paramInt2)) {
        return localFontCacheItem.getFont(paramInt3);
      }
      return null;
    }
    
    static int[][] getRules(int paramInt)
    {
      if (paramInt != 0)
      {
        if (paramInt != 1) {
          return SMALL_RULES;
        }
        return LARGE_RULES;
      }
      return NORAML_RULES;
    }
    
    static int getTextSizeGrade(float paramFloat)
    {
      if (paramFloat > 20.0F) {
        return 1;
      }
      if ((paramFloat > 0.0F) && (paramFloat < 12.0F)) {
        return 2;
      }
      return 0;
    }
    
    static int getWeightIndex(int paramInt)
    {
      for (int i = 0;; i++)
      {
        int[] arrayOfInt = WEIGHT_KEYS;
        if (i >= arrayOfInt.length) {
          break;
        }
        if (arrayOfInt[i] == paramInt) {
          return i;
        }
      }
      return -1;
    }
    
    static int getWght(int paramInt, boolean paramBoolean)
    {
      int[] arrayOfInt = getWghtArray(paramBoolean);
      paramInt = getWeightIndex(paramInt);
      if (paramInt >= 0) {
        return arrayOfInt[paramInt];
      }
      return getWght(400, paramBoolean);
    }
    
    private static int[] getWghtArray(boolean paramBoolean)
    {
      if (paramBoolean) {
        return MITYPE_WGHT;
      }
      if (sUiMode == 2) {
        return MIUI_WGHT_DARKMODE;
      }
      return MIUI_WGHT;
    }
    
    static boolean isUsingMiuiFonts()
    {
      Boolean localBoolean = sIsUsingMiuiFonts;
      boolean bool;
      if (localBoolean == null) {
        bool = TypefaceUtils.access$100();
      } else {
        bool = localBoolean.booleanValue();
      }
      return bool;
    }
    
    private static class FontCacheItem
    {
      Typeface[] cache = new Typeface[3];
      int scale;
      
      FontCacheItem()
      {
        for (int i = 0; i < 3; i++) {
          this.cache[i] = null;
        }
      }
      
      void clear()
      {
        for (int i = 0;; i++)
        {
          Typeface[] arrayOfTypeface = this.cache;
          if (i >= arrayOfTypeface.length) {
            break;
          }
          arrayOfTypeface[i] = null;
        }
      }
      
      Typeface getFont(int paramInt)
      {
        return this.cache[paramInt];
      }
      
      void setFont(Typeface paramTypeface, int paramInt)
      {
        this.cache[paramInt] = paramTypeface;
      }
    }
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes4-dex2jar.jar!/miui/util/TypefaceUtils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */