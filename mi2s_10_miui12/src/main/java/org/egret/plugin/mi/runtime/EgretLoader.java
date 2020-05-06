package org.egret.plugin.mi.runtime;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.webkit.JavascriptInterface;
import java.io.File;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;
import org.egret.plugin.mi.java.egretruntimelauncher.EgretRuntimeLauncher;
import org.egret.plugin.mi.java.egretruntimelauncher.EgretRuntimeLauncher.EgretRuntimeDownloadListener;

public class EgretLoader
{
  private static final String LOGTAG = "EgretLoader";
  private static final String MI_APPID = "2000";
  private static final String MI_APPKEY = "3321031F35156D389B0B272910695E3F";
  private Activity activity;
  private Object gameEngine;
  private EgretRuntimeLauncher launcher;
  private HashMap<String, String> options;
  
  public EgretLoader(Context paramContext)
  {
    Log.d("EgretLoader", "EgretLoader(Context context)");
    if (checkContext(paramContext))
    {
      this.options = new HashMap();
      this.activity = ((Activity)paramContext);
    }
  }
  
  private Object callGameEngineMethod(String paramString)
  {
    return callGameEngineMethod(paramString, null, null);
  }
  
  private Object callGameEngineMethod(String paramString, Class<?>[] paramArrayOfClass, Object[] paramArrayOfObject)
  {
    if (paramString != null)
    {
      Object localObject = this.gameEngine;
      if (localObject != null) {
        try
        {
          paramString = localObject.getClass().getDeclaredMethod(paramString, paramArrayOfClass).invoke(this.gameEngine, paramArrayOfObject);
          return paramString;
        }
        catch (Exception paramString)
        {
          paramString.printStackTrace();
          return null;
        }
      }
    }
    return null;
  }
  
  private void callInitRuntime()
  {
    Activity localActivity = this.activity;
    callGameEngineMethod("game_engine_init", new Class[] { Context.class }, new Object[] { localActivity });
  }
  
  private void callSetGameOptions()
  {
    HashMap localHashMap = new HashMap();
    Iterator localIterator = this.options.entrySet().iterator();
    while (localIterator.hasNext())
    {
      Map.Entry localEntry = (Map.Entry)localIterator.next();
      localHashMap.put((String)localEntry.getKey(), localEntry.getValue());
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append((String)localEntry.getKey());
      localStringBuilder.append(": ");
      localStringBuilder.append((String)localEntry.getValue());
      Log.d("EgretLoader", localStringBuilder.toString());
    }
    callGameEngineMethod("game_engine_set_options", new Class[] { HashMap.class }, new Object[] { localHashMap });
  }
  
  private void callSetRuntimeView()
  {
    View localView = (View)callGameEngineMethod("game_engine_get_view");
    if (localView != null)
    {
      setScreenOrientation();
      this.activity.setContentView(localView);
    }
  }
  
  private boolean checkContext(Context paramContext)
  {
    if (!Activity.class.isInstance(paramContext)) {
      return false;
    }
    try
    {
      paramContext.getClass().getMethod("setEgretRuntimeListener", new Class[] { Object.class }).invoke(paramContext, new Object[] { this });
      return true;
    }
    catch (Exception paramContext) {}
    return false;
  }
  
  private boolean checkEgretGameEngine()
  {
    if (this.gameEngine == null)
    {
      Log.d("EgretLoader", "Egret game engine is null");
      return true;
    }
    return false;
  }
  
  private void setScreenOrientation()
  {
    if ((this.options.containsKey("egret.runtime.screenOrientation")) && (((String)this.options.get("egret.runtime.screenOrientation")).equals("landscape"))) {
      this.activity.setRequestedOrientation(0);
    } else {
      this.activity.setRequestedOrientation(1);
    }
  }
  
  private void startGame(Class<?> paramClass)
  {
    try
    {
      this.gameEngine = paramClass.newInstance();
      this.activity.runOnUiThread(new Runnable()
      {
        public void run()
        {
          EgretLoader.this.startGameEngine();
        }
      });
      return;
    }
    catch (Exception paramClass)
    {
      paramClass.printStackTrace();
    }
  }
  
  private void startGameEngine()
  {
    callSetGameOptions();
    callInitRuntime();
    callSetRuntimeView();
  }
  
  public boolean checkEgretContext()
  {
    if (this.activity == null)
    {
      Log.d("EgretLoader", "The context is not activity");
      return true;
    }
    return false;
  }
  
  @JavascriptInterface
  public void init(String paramString)
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("init: ");
    localStringBuilder.append(paramString);
    Log.d("EgretLoader", localStringBuilder.toString());
  }
  
  public void onPause()
  {
    Log.d("EgretLoader", "onPause()");
    if ((!checkEgretContext()) && (!checkEgretGameEngine()))
    {
      callGameEngineMethod("game_engine_onPause");
      return;
    }
  }
  
  public void onResume()
  {
    Log.d("EgretLoader", "onResume()");
    if ((!checkEgretContext()) && (!checkEgretGameEngine()))
    {
      callGameEngineMethod("game_engine_onResume");
      return;
    }
  }
  
  public void onStop()
  {
    Log.d("EgretLoader", "stop()");
    if ((!checkEgretContext()) && (!checkEgretGameEngine()))
    {
      callGameEngineMethod("game_engine_onStop");
      this.gameEngine = null;
      return;
    }
  }
  
  @JavascriptInterface
  public void setOption(String paramString1, String paramString2)
  {
    Object localObject = new StringBuilder();
    ((StringBuilder)localObject).append("setOption: ");
    ((StringBuilder)localObject).append(paramString1);
    ((StringBuilder)localObject).append("->");
    ((StringBuilder)localObject).append(paramString2);
    Log.d("EgretLoader", ((StringBuilder)localObject).toString());
    if (checkEgretContext()) {
      return;
    }
    if (paramString1.equals("gameId"))
    {
      localObject = "egret.runtime.gameId";
    }
    else
    {
      localObject = paramString1;
      if (paramString1.equals("gameUrl"))
      {
        paramString1 = "egret.runtime.loaderUrl";
        localObject = paramString1;
        if (!this.options.containsKey("egret.runtime.updateUrl"))
        {
          this.options.put("egret.runtime.updateUrl", paramString2);
          localObject = paramString1;
        }
      }
    }
    this.options.put(localObject, paramString2);
  }
  
  @JavascriptInterface
  public void setScreenOrientation(String paramString)
  {
    if (paramString.equals("landscape")) {
      this.options.put("egret.runtime.screenOrientation", "landscape");
    } else {
      this.options.put("egret.runtime.screenOrientation", "portrait");
    }
  }
  
  @JavascriptInterface
  public void start(String paramString)
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("start: ");
    localStringBuilder.append(paramString);
    Log.d("EgretLoader", localStringBuilder.toString());
    if (checkEgretContext()) {
      return;
    }
    paramString = new File(this.activity.getFilesDir(), "egret").getAbsolutePath();
    this.options.put("egret.runtime.egretRoot", paramString);
    this.options.put("egret.runtime.libraryLoaderType", "2");
    this.launcher = new EgretRuntimeLauncher(this.activity, paramString, "2000", "3321031F35156D389B0B272910695E3F", 0);
    this.launcher.run(new EgretRuntimeLauncher.EgretRuntimeDownloadListener()
    {
      public void onError(String paramAnonymousString) {}
      
      public void onProgress(String paramAnonymousString, int paramAnonymousInt1, int paramAnonymousInt2)
      {
        StringBuilder localStringBuilder = new StringBuilder();
        localStringBuilder.append(paramAnonymousString);
        localStringBuilder.append(": ");
        localStringBuilder.append(String.valueOf(paramAnonymousInt1));
        localStringBuilder.append("/");
        localStringBuilder.append(String.valueOf(paramAnonymousInt2));
        Log.d("EgretLoader", localStringBuilder.toString());
      }
      
      public void onProgressTotal(int paramAnonymousInt1, int paramAnonymousInt2)
      {
        StringBuilder localStringBuilder = new StringBuilder();
        localStringBuilder.append("progress: ");
        localStringBuilder.append(String.valueOf(paramAnonymousInt1));
        localStringBuilder.append("/");
        localStringBuilder.append(String.valueOf(paramAnonymousInt2));
        Log.d("EgretLoader", localStringBuilder.toString());
      }
      
      public void onSuccess(Class<?> paramAnonymousClass)
      {
        EgretLoader.this.startGame(paramAnonymousClass);
      }
    });
  }
  
  public void stopEgretRuntime()
  {
    onStop();
  }
  
  private class GameEngineMethod
  {
    public static final String CALL_INTERFACE = "callEgretInterface";
    public static final String ENABLE_INTERFACE = "enableEgretRuntimeInterface";
    public static final String GET_VIEW = "game_engine_get_view";
    public static final String INIT = "game_engine_init";
    public static final String ON_PAUSE = "game_engine_onPause";
    public static final String ON_RESUME = "game_engine_onResume";
    public static final String SET_INTERFACE = "setRuntimeInterfaceSet";
    public static final String SET_OPTIONS = "game_engine_set_options";
    public static final String STOP = "game_engine_onStop";
    
    private GameEngineMethod() {}
  }
  
  private class GameOptionName
  {
    public static final String EGRET_ROOT = "egret.runtime.egretRoot";
    public static final String GAME_ID = "egret.runtime.gameId";
    public static final String LOADER_TYPE = "egret.runtime.libraryLoaderType";
    public static final String LOADER_URL = "egret.runtime.loaderUrl";
    public static final String ORIENTATION = "egret.runtime.screenOrientation";
    public static final String UPDATE_URL = "egret.runtime.updateUrl";
    
    private GameOptionName() {}
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes4-dex2jar.jar!/org/egret/plugin/mi/runtime/EgretLoader.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */