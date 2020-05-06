package miui.util;

import android.content.Context;
import android.provider.MiuiSettings.System;
import android.provider.Settings.System;
import android.text.TextUtils;
import miui.securityspace.CrossUserUtils;
import org.json.JSONException;
import org.json.JSONObject;

public class AutoDisableScreenButtonsHelper
{
  public static final String CLOUD_SETTING = "auto_disable_screen_button_cloud_setting";
  public static final int ENABLE_ASK = 1;
  public static final int ENABLE_AUTO = 2;
  public static final String MODULE_AUTO_DIS_NAV_BTN = "AutoDisableNavigationButton1";
  public static final int NO = 3;
  public static final int NONE = 0;
  private static final String TAG = "AutoDisableHelper";
  private static JSONObject mCloudJson;
  private static JSONObject mUserJson;
  
  private static void checkJson(Context paramContext)
  {
    if (paramContext == null) {
      return;
    }
    if (mUserJson == null)
    {
      String str = MiuiSettings.System.getStringForUser(paramContext.getContentResolver(), "auto_disable_screen_button", CrossUserUtils.getCurrentUserId());
      if (str == null) {
        mUserJson = new JSONObject();
      } else {
        updateUserJson(str);
      }
    }
    if (mCloudJson == null) {
      updateCloudJson(Settings.System.getString(paramContext.getContentResolver(), "auto_disable_screen_button_cloud_setting"));
    }
  }
  
  public static int getAppFlag(Context paramContext, String paramString)
  {
    paramContext = getValue(paramContext, paramString);
    int i;
    if (paramContext == null) {
      i = 3;
    } else {
      i = ((Integer)paramContext).intValue();
    }
    return i;
  }
  
  public static Object getValue(Context paramContext, String paramString)
  {
    checkJson(paramContext);
    try
    {
      if ((mUserJson != null) && (mUserJson.has(paramString))) {
        return mUserJson.get(paramString);
      }
      if ((mCloudJson != null) && (mCloudJson.has(paramString)))
      {
        paramContext = mCloudJson.get(paramString);
        return paramContext;
      }
    }
    catch (JSONException paramContext)
    {
      paramContext.printStackTrace();
    }
    return null;
  }
  
  public static void setFlag(Context paramContext, String paramString, int paramInt)
  {
    setValue(paramContext, paramString, Integer.valueOf(paramInt));
  }
  
  public static void setValue(Context paramContext, String paramString, Object paramObject)
  {
    checkJson(paramContext);
    JSONObject localJSONObject = mUserJson;
    if ((localJSONObject != null) && (paramContext != null))
    {
      try
      {
        localJSONObject.put(paramString, paramObject);
      }
      catch (JSONException paramString)
      {
        paramString.printStackTrace();
      }
      MiuiSettings.System.putStringForUser(paramContext.getContentResolver(), "auto_disable_screen_button", mUserJson.toString(), CrossUserUtils.getCurrentUserId());
    }
  }
  
  public static void updateCloudJson(String paramString)
  {
    if (!TextUtils.isEmpty(paramString))
    {
      JSONObject localJSONObject = mCloudJson;
      if ((localJSONObject == null) || (!paramString.equals(localJSONObject.toString())))
      {
        try
        {
          localJSONObject = new org/json/JSONObject;
          localJSONObject.<init>(paramString);
          mCloudJson = localJSONObject;
        }
        catch (JSONException paramString)
        {
          paramString.printStackTrace();
        }
        return;
      }
    }
  }
  
  public static void updateUserJson(String paramString)
  {
    if (!TextUtils.isEmpty(paramString))
    {
      JSONObject localJSONObject = mUserJson;
      if ((localJSONObject == null) || (!paramString.equals(localJSONObject.toString())))
      {
        try
        {
          localJSONObject = new org/json/JSONObject;
          localJSONObject.<init>(paramString);
          mUserJson = localJSONObject;
        }
        catch (JSONException paramString)
        {
          paramString.printStackTrace();
        }
        return;
      }
    }
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes4-dex2jar.jar!/miui/util/AutoDisableScreenButtonsHelper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */