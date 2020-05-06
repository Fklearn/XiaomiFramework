package org.egret.plugin.mi.java.egretruntimelauncher;

import android.util.Log;
import dalvik.system.DexClassLoader;
import java.io.File;

public class EgretRuntimeLoader
{
  private static final String GAME_ENGINE_CLASS = "org.egret.egretframeworknative.engine.EgretGameEngine";
  private static final String TAG = "EgretRuntimeLoader";
  private static EgretRuntimeLoader instance = null;
  private Class<?> egretGameEngineClass = null;
  private boolean loaded = false;
  
  public static EgretRuntimeLoader get()
  {
    if (instance == null) {
      instance = new EgretRuntimeLoader();
    }
    return instance;
  }
  
  public Class<?> getEgretGameEngineClass()
  {
    return this.egretGameEngineClass;
  }
  
  public boolean isLoaded()
  {
    return this.loaded;
  }
  
  public void load(String paramString)
  {
    this.loaded = true;
    if (paramString.endsWith(".jar")) {
      loadJar(paramString);
    }
  }
  
  public void loadJar(String paramString)
  {
    File localFile = new File(paramString);
    localFile.setExecutable(true);
    Object localObject = new StringBuilder();
    ((StringBuilder)localObject).append("loadJar: ");
    ((StringBuilder)localObject).append(paramString);
    ((StringBuilder)localObject).append(": ");
    ((StringBuilder)localObject).append(String.valueOf(localFile.exists()));
    Log.d("EgretRuntimeLoader", ((StringBuilder)localObject).toString());
    try
    {
      localObject = new dalvik/system/DexClassLoader;
      localFile = new java/io/File;
      localFile.<init>(paramString);
      ((DexClassLoader)localObject).<init>(paramString, localFile.getParent(), null, getClass().getClassLoader());
      if (this.egretGameEngineClass == null) {
        this.egretGameEngineClass = ((DexClassLoader)localObject).loadClass("org.egret.egretframeworknative.engine.EgretGameEngine");
      }
    }
    catch (Exception paramString)
    {
      Log.e("Loader", "need dex format jar");
      paramString.printStackTrace();
    }
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes4-dex2jar.jar!/org/egret/plugin/mi/java/egretruntimelauncher/EgretRuntimeLoader.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */