package android.view.textclassifier;

import android.provider.DeviceConfig;
import android.util.ArrayMap;
import android.util.KeyValueListParser;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.annotations.VisibleForTesting.Visibility;
import com.android.internal.util.Preconditions;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

@VisibleForTesting(visibility=VisibleForTesting.Visibility.PACKAGE)
public final class ConfigParser
{
  static final boolean ENABLE_DEVICE_CONFIG = true;
  private static final String STRING_LIST_DELIMITER = ":";
  private static final String TAG = "ConfigParser";
  @GuardedBy({"mLock"})
  private final Map<String, Object> mCache = new ArrayMap();
  private final Supplier<String> mLegacySettingsSupplier;
  private final Object mLock = new Object();
  @GuardedBy({"mLock"})
  private KeyValueListParser mSettingsParser;
  
  public ConfigParser(Supplier<String> paramSupplier)
  {
    this.mLegacySettingsSupplier = ((Supplier)Preconditions.checkNotNull(paramSupplier));
  }
  
  private static float[] getDeviceConfigFloatArray(String paramString, float[] paramArrayOfFloat)
  {
    return parse(DeviceConfig.getString("textclassifier", paramString, null), paramArrayOfFloat);
  }
  
  private static List<String> getDeviceConfigStringList(String paramString, List<String> paramList)
  {
    return parse(DeviceConfig.getString("textclassifier", paramString, null), paramList);
  }
  
  private KeyValueListParser getLegacySettings()
  {
    synchronized (this.mLock)
    {
      if (this.mSettingsParser == null)
      {
        localObject2 = (String)this.mLegacySettingsSupplier.get();
        try
        {
          KeyValueListParser localKeyValueListParser = new android/util/KeyValueListParser;
          localKeyValueListParser.<init>(',');
          this.mSettingsParser = localKeyValueListParser;
          this.mSettingsParser.setString((String)localObject2);
        }
        catch (IllegalArgumentException localIllegalArgumentException)
        {
          StringBuilder localStringBuilder = new java/lang/StringBuilder;
          localStringBuilder.<init>();
          localStringBuilder.append("Bad text_classifier_constants: ");
          localStringBuilder.append((String)localObject2);
          Log.w("ConfigParser", localStringBuilder.toString());
        }
      }
      Object localObject2 = this.mSettingsParser;
      return (KeyValueListParser)localObject2;
    }
  }
  
  private float[] getSettingsFloatArray(String paramString, float[] paramArrayOfFloat)
  {
    return parse(this.mSettingsParser.getString(paramString, null), paramArrayOfFloat);
  }
  
  private List<String> getSettingsStringList(String paramString, List<String> paramList)
  {
    return parse(this.mSettingsParser.getString(paramString, null), paramList);
  }
  
  private static List<String> parse(String paramString, List<String> paramList)
  {
    if (paramString != null) {
      return Collections.unmodifiableList(Arrays.asList(paramString.split(":")));
    }
    return paramList;
  }
  
  private static float[] parse(String paramString, float[] paramArrayOfFloat)
  {
    if (paramString != null)
    {
      String[] arrayOfString = paramString.split(":");
      if (arrayOfString.length != paramArrayOfFloat.length) {
        return paramArrayOfFloat;
      }
      paramString = new float[arrayOfString.length];
      int i = 0;
      while (i < arrayOfString.length) {
        try
        {
          paramString[i] = Float.parseFloat(arrayOfString[i]);
          i++;
        }
        catch (NumberFormatException paramString)
        {
          return paramArrayOfFloat;
        }
      }
      return paramString;
    }
    return paramArrayOfFloat;
  }
  
  public boolean getBoolean(String paramString, boolean paramBoolean)
  {
    synchronized (this.mLock)
    {
      Object localObject2 = this.mCache.get(paramString);
      if ((localObject2 instanceof Boolean))
      {
        paramBoolean = ((Boolean)localObject2).booleanValue();
        return paramBoolean;
      }
      paramBoolean = DeviceConfig.getBoolean("textclassifier", paramString, getLegacySettings().getBoolean(paramString, paramBoolean));
      this.mCache.put(paramString, Boolean.valueOf(paramBoolean));
      return paramBoolean;
    }
  }
  
  public float getFloat(String paramString, float paramFloat)
  {
    synchronized (this.mLock)
    {
      Object localObject2 = this.mCache.get(paramString);
      if ((localObject2 instanceof Float))
      {
        paramFloat = ((Float)localObject2).floatValue();
        return paramFloat;
      }
      paramFloat = DeviceConfig.getFloat("textclassifier", paramString, getLegacySettings().getFloat(paramString, paramFloat));
      this.mCache.put(paramString, Float.valueOf(paramFloat));
      return paramFloat;
    }
  }
  
  public float[] getFloatArray(String paramString, float[] paramArrayOfFloat)
  {
    synchronized (this.mLock)
    {
      Object localObject2 = this.mCache.get(paramString);
      if ((localObject2 instanceof float[]))
      {
        paramString = (float[])localObject2;
        return paramString;
      }
      paramArrayOfFloat = getDeviceConfigFloatArray(paramString, getSettingsFloatArray(paramString, paramArrayOfFloat));
      this.mCache.put(paramString, paramArrayOfFloat);
      return paramArrayOfFloat;
    }
  }
  
  public int getInt(String paramString, int paramInt)
  {
    synchronized (this.mLock)
    {
      Object localObject2 = this.mCache.get(paramString);
      if ((localObject2 instanceof Integer))
      {
        paramInt = ((Integer)localObject2).intValue();
        return paramInt;
      }
      paramInt = DeviceConfig.getInt("textclassifier", paramString, getLegacySettings().getInt(paramString, paramInt));
      this.mCache.put(paramString, Integer.valueOf(paramInt));
      return paramInt;
    }
  }
  
  public String getString(String paramString1, String paramString2)
  {
    synchronized (this.mLock)
    {
      Object localObject2 = this.mCache.get(paramString1);
      if ((localObject2 instanceof String))
      {
        paramString1 = (String)localObject2;
        return paramString1;
      }
      paramString2 = DeviceConfig.getString("textclassifier", paramString1, getLegacySettings().getString(paramString1, paramString2));
      this.mCache.put(paramString1, paramString2);
      return paramString2;
    }
  }
  
  public List<String> getStringList(String paramString, List<String> paramList)
  {
    synchronized (this.mLock)
    {
      Object localObject2 = this.mCache.get(paramString);
      if ((localObject2 instanceof List))
      {
        List localList = (List)localObject2;
        if (localList.isEmpty())
        {
          paramString = Collections.emptyList();
          return paramString;
        }
        if ((localList.get(0) instanceof String))
        {
          paramString = (List)localObject2;
          return paramString;
        }
      }
      paramList = getDeviceConfigStringList(paramString, getSettingsStringList(paramString, paramList));
      this.mCache.put(paramString, paramList);
      return paramList;
    }
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/view/textclassifier/ConfigParser.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */