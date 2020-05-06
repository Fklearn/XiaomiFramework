package org.egret.plugin.mi.java.egretruntimelauncher;

import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.egret.plugin.mi.android.util.launcher.ExecutorLab;
import org.egret.plugin.mi.android.util.launcher.FileUtil;
import org.egret.plugin.mi.android.util.launcher.Md5Util;
import org.egret.plugin.mi.android.util.launcher.NetClass;
import org.egret.plugin.mi.android.util.launcher.NetClass.OnNetListener;
import org.egret.plugin.mi.android.util.launcher.ZipClass;

public class EgretRuntimeLauncher
{
  public static int DEBUG_RUNTIME_DOWNLOAD = 0;
  public static final String EGRET_JSON = "egret.json";
  public static final String EGRET_ROOT = "egret";
  private static final String EGRET_RUNTIME_CACHE_ROOT = "update";
  public static final String EGRET_RUNTIME_SD_ROOT = "egret/runtime";
  private static final String EGRET_RUNTIME_VERSION_URL = "http://runtime.egret-labs.org/runtime.php";
  private static final String TAG = "EgretRuntimeLauncher";
  private File cacheRoot;
  private int downLoadSum;
  private EgretRuntimeDownloadListener downloadListener;
  private ArrayList<EgretRuntimeLibrary> downloaderList = new ArrayList();
  private int fileSizeSum;
  private File libraryRoot;
  private Handler mainHandler;
  private ConcurrentHashMap<String, Integer> mapFileSize = new ConcurrentHashMap();
  private EgretRuntimeVersion runtimeVersion = new EgretRuntimeVersion();
  private String runtimeVersionUrl;
  private File sdRoot;
  protected int updatedNumber;
  private String urlData;
  
  public EgretRuntimeLauncher(Context paramContext, String paramString)
  {
    this.mainHandler = new Handler(paramContext.getMainLooper());
    this.runtimeVersionUrl = "http://runtime.egret-labs.org/runtime.php";
    if (paramString != null) {
      paramContext = new File(paramString);
    } else {
      paramContext = null;
    }
    this.libraryRoot = paramContext;
    this.cacheRoot = new File(paramString, "update");
    this.sdRoot = getSdRoot();
    this.cacheRoot.mkdirs();
  }
  
  public EgretRuntimeLauncher(Context paramContext, String paramString1, String paramString2, String paramString3, int paramInt)
  {
    this(paramContext, paramString1);
    paramContext = new StringBuilder();
    paramContext.append("?appId=");
    paramContext.append(paramString2);
    paramContext.append("&appKey=");
    paramContext.append(paramString3);
    this.urlData = paramContext.toString();
    if (paramInt > 0)
    {
      paramContext = new StringBuilder();
      paramContext.append(this.urlData);
      paramContext.append("&dev=");
      paramContext.append(paramInt);
      this.urlData = paramContext.toString();
    }
    paramContext = new StringBuilder();
    paramContext.append(this.runtimeVersionUrl);
    paramContext.append(this.urlData);
    this.runtimeVersionUrl = paramContext.toString();
  }
  
  private boolean checkCache(Library paramLibrary)
  {
    if (!checkZipInRoot(paramLibrary, this.cacheRoot)) {
      return false;
    }
    paramLibrary = new File(this.cacheRoot, paramLibrary.getZipName());
    StringBuilder localStringBuilder;
    if (!new ZipClass().unzip(paramLibrary, this.libraryRoot))
    {
      localStringBuilder = new StringBuilder();
      localStringBuilder.append("fail to unzip ");
      localStringBuilder.append(paramLibrary.getAbsolutePath());
      Log.e("EgretRuntimeLauncher", localStringBuilder.toString());
      return false;
    }
    if (!paramLibrary.delete())
    {
      localStringBuilder = new StringBuilder();
      localStringBuilder.append("fail to delete ");
      localStringBuilder.append(paramLibrary.getAbsolutePath());
      Log.e("EgretRuntimeLauncher", localStringBuilder.toString());
      return false;
    }
    return true;
  }
  
  private boolean checkLocal(Library paramLibrary)
  {
    return isLatest(new File(this.libraryRoot, paramLibrary.getLibraryName()), paramLibrary.getLibraryCheckSum());
  }
  
  private boolean checkSd(Library paramLibrary)
  {
    if (!checkZipInRoot(paramLibrary, this.sdRoot)) {
      return false;
    }
    File localFile = new File(this.cacheRoot, paramLibrary.getZipName());
    if (!FileUtil.Copy(new File(this.sdRoot, paramLibrary.getZipName()), localFile)) {
      return false;
    }
    return checkCache(paramLibrary);
  }
  
  private boolean checkZipInRoot(Library paramLibrary, File paramFile)
  {
    return isLatest(new File(paramFile, paramLibrary.getZipName()), paramLibrary.getZipCheckSum());
  }
  
  private void fetchRemoteVersion()
  {
    ExecutorLab.getInstance().addTask(new Thread(new Runnable()
    {
      public void run()
      {
        new NetClass().getRequest(EgretRuntimeLauncher.this.runtimeVersionUrl, new NetClass.OnNetListener()
        {
          public void onError(String paramAnonymous2String)
          {
            EgretRuntimeLauncher.this.handleError(paramAnonymous2String);
          }
          
          public void onProgress(int paramAnonymous2Int1, int paramAnonymous2Int2) {}
          
          public void onSuccess(String paramAnonymous2String)
          {
            if (paramAnonymous2String == null)
            {
              EgretRuntimeLauncher.this.handleError("response content is null");
              return;
            }
            EgretRuntimeLauncher.this.parseRuntimeVersion(paramAnonymous2String);
          }
        });
      }
    }));
  }
  
  private int getFileSize(String paramString)
  {
    int i = 0;
    int j = 0;
    int k = j;
    int m = i;
    try
    {
      URL localURL = new java/net/URL;
      k = j;
      m = i;
      localURL.<init>(paramString);
      k = j;
      m = i;
      paramString = (HttpURLConnection)localURL.openConnection();
      k = j;
      m = i;
      j = paramString.getContentLength();
      k = j;
      m = j;
      paramString.disconnect();
      m = j;
    }
    catch (IOException paramString)
    {
      paramString.printStackTrace();
      m = k;
    }
    catch (MalformedURLException paramString)
    {
      paramString.printStackTrace();
    }
    return m;
  }
  
  private ArrayList<Library> getNeedUpdateLibraryList()
  {
    ArrayList localArrayList = new ArrayList();
    Iterator localIterator = this.runtimeVersion.getLibraryList().iterator();
    while (localIterator.hasNext())
    {
      Library localLibrary = (Library)localIterator.next();
      if ((!checkLocal(localLibrary)) && (!checkCache(localLibrary)) && (!checkSd(localLibrary))) {
        localArrayList.add(localLibrary);
      }
    }
    return localArrayList;
  }
  
  private File getSdRoot()
  {
    if ("mounted".equals(Environment.getExternalStorageState()))
    {
      File localFile = new File(Environment.getExternalStorageDirectory(), "egret/runtime");
      if ((localFile.exists()) || (localFile.mkdirs())) {
        return localFile;
      }
    }
    return null;
  }
  
  private void handleError(String paramString)
  {
    Object localObject = FileUtil.readFile(new File(this.libraryRoot, "egret.json"));
    if (localObject == null)
    {
      this.downloadListener.onError(paramString);
      ExecutorLab.releaseInstance();
      return;
    }
    this.runtimeVersion.fromString((String)localObject);
    localObject = this.runtimeVersion.getLibraryList();
    if (localObject == null) {
      return;
    }
    Iterator localIterator = ((ArrayList)localObject).iterator();
    while (localIterator.hasNext())
    {
      localObject = (Library)localIterator.next();
      if (!checkLocal((Library)localObject))
      {
        this.downloadListener.onError(paramString);
        ExecutorLab.releaseInstance();
        return;
      }
      if (!EgretRuntimeLoader.get().isLoaded()) {
        EgretRuntimeLoader.get().load(new File(this.libraryRoot, ((Library)localObject).getLibraryName()).getAbsolutePath());
      }
    }
    notifyLoadHandler();
  }
  
  private boolean isLatest(File paramFile, String paramString)
  {
    if (DEBUG_RUNTIME_DOWNLOAD > 0) {
      return false;
    }
    if (!paramFile.exists()) {
      return false;
    }
    if (Md5Util.checkMd5(paramFile, paramString)) {
      return true;
    }
    if (!paramFile.delete())
    {
      paramString = new StringBuilder();
      paramString.append("Fail to delete file: ");
      paramString.append(paramFile.getAbsolutePath());
      handleError(paramString.toString());
      ExecutorLab.releaseInstance();
    }
    return false;
  }
  
  private void loadLibrary()
  {
    if (!EgretRuntimeLoader.get().isLoaded())
    {
      Iterator localIterator = this.runtimeVersion.getLibraryList().iterator();
      while (localIterator.hasNext())
      {
        Library localLibrary = (Library)localIterator.next();
        EgretRuntimeLoader.get().load(new File(this.libraryRoot, localLibrary.getLibraryName()).getAbsolutePath());
      }
    }
    notifyLoadHandler();
  }
  
  private void notifyLoadHandler()
  {
    Runnable local3 = new Runnable()
    {
      public void run()
      {
        Class localClass = EgretRuntimeLoader.get().getEgretGameEngineClass();
        if (localClass == null)
        {
          EgretRuntimeLauncher.this.downloadListener.onError("fails to new game engine");
          ExecutorLab.releaseInstance();
          return;
        }
        EgretRuntimeLauncher.this.downloadListener.onSuccess(localClass);
      }
    };
    this.mainHandler.post(local3);
  }
  
  private void parseRuntimeVersion(String paramString)
  {
    this.runtimeVersion.fromString(paramString);
    FileUtil.writeFile(new File(this.libraryRoot, "egret.json"), paramString);
    updateLibrary();
  }
  
  private void updateDownLoadSum()
  {
    try
    {
      this.downLoadSum = 0;
      Object localObject1 = this.mapFileSize.entrySet().iterator();
      while (((Iterator)localObject1).hasNext())
      {
        Object localObject3 = (Map.Entry)((Iterator)localObject1).next();
        String str = (String)((Map.Entry)localObject3).getKey();
        Integer localInteger = (Integer)((Map.Entry)localObject3).getValue();
        localObject3 = new java/lang/StringBuilder;
        ((StringBuilder)localObject3).<init>();
        ((StringBuilder)localObject3).append("rt zipUrl progress key = ");
        ((StringBuilder)localObject3).append(str);
        ((StringBuilder)localObject3).append(" value = ");
        ((StringBuilder)localObject3).append(localInteger);
        Log.d("", ((StringBuilder)localObject3).toString());
        this.downLoadSum += localInteger.intValue();
      }
      localObject1 = new java/lang/StringBuilder;
      ((StringBuilder)localObject1).<init>();
      ((StringBuilder)localObject1).append("rt zipUrl progress downLoadSum = ");
      ((StringBuilder)localObject1).append(this.downLoadSum);
      Log.d("", ((StringBuilder)localObject1).toString());
      return;
    }
    finally {}
  }
  
  private void updateLibrary()
  {
    this.updatedNumber = 0;
    Object localObject1 = getNeedUpdateLibraryList();
    if (((ArrayList)localObject1).size() == 0) {
      updated();
    }
    final Object localObject2 = new StringBuilder();
    ((StringBuilder)localObject2).append("rt libraryList size: ");
    ((StringBuilder)localObject2).append(String.valueOf(((ArrayList)localObject1).size()));
    Log.d("EgretRuntimeLauncher", ((StringBuilder)localObject2).toString());
    int i = 0;
    localObject2 = ((ArrayList)localObject1).iterator();
    while (((Iterator)localObject2).hasNext()) {
      i += getFileSize(((Library)((Iterator)localObject2).next()).getUrl());
    }
    this.fileSizeSum = i;
    Iterator localIterator = ((ArrayList)localObject1).iterator();
    while (localIterator.hasNext())
    {
      localObject2 = (Library)localIterator.next();
      EgretRuntimeLibrary localEgretRuntimeLibrary = new EgretRuntimeLibrary((Library)localObject2, this.libraryRoot, this.cacheRoot, this.sdRoot);
      localEgretRuntimeLibrary.download(new EgretRuntimeLibrary.OnDownloadListener()
      {
        public void onError(String paramAnonymousString)
        {
          EgretRuntimeLauncher localEgretRuntimeLauncher = EgretRuntimeLauncher.this;
          StringBuilder localStringBuilder = new StringBuilder();
          localStringBuilder.append("Fail to download file: ");
          localStringBuilder.append(localObject2.getZipName());
          localStringBuilder.append(" detail: ");
          localStringBuilder.append(paramAnonymousString);
          localEgretRuntimeLauncher.handleError(localStringBuilder.toString());
          ExecutorLab.releaseInstance();
        }
        
        public void onProgress(int paramAnonymousInt1, int paramAnonymousInt2)
        {
          EgretRuntimeLauncher.this.mapFileSize.put(localObject2.getZipName(), Integer.valueOf(paramAnonymousInt1));
          EgretRuntimeLauncher.this.updateDownLoadSum();
          EgretRuntimeLauncher.this.downloadListener.onProgressTotal(EgretRuntimeLauncher.this.downLoadSum, EgretRuntimeLauncher.this.fileSizeSum);
        }
        
        public void onSuccess()
        {
          EgretRuntimeLauncher localEgretRuntimeLauncher = EgretRuntimeLauncher.this;
          localEgretRuntimeLauncher.updatedNumber += 1;
          EgretRuntimeLauncher.this.updated();
        }
      });
      this.downloaderList.add(localEgretRuntimeLibrary);
      localObject1 = new StringBuilder();
      ((StringBuilder)localObject1).append("addTask: ");
      ((StringBuilder)localObject1).append(((Library)localObject2).getZipName());
      Log.d("EgretRuntimeLauncher", ((StringBuilder)localObject1).toString());
      ExecutorLab.getInstance().addTask(localEgretRuntimeLibrary);
    }
  }
  
  private void updated()
  {
    if ((this.downloaderList.size() > 0) && (this.updatedNumber != this.downloaderList.size())) {
      return;
    }
    loadLibrary();
  }
  
  public void run(EgretRuntimeDownloadListener paramEgretRuntimeDownloadListener)
  {
    if ((this.runtimeVersionUrl != null) && (this.libraryRoot != null) && (paramEgretRuntimeDownloadListener != null))
    {
      Log.d("EgretRuntimeLauncher", "run");
      this.downloadListener = paramEgretRuntimeDownloadListener;
      fetchRemoteVersion();
      return;
    }
    Log.e("EgretRuntimeLauncher", "library root, url or listener may be null");
    paramEgretRuntimeDownloadListener.onError("library root, url or listener may be null");
    ExecutorLab.releaseInstance();
  }
  
  public void setRuntimeVersionUrl(String paramString)
  {
    if (this.urlData == null)
    {
      this.runtimeVersionUrl = paramString;
    }
    else
    {
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append(paramString);
      localStringBuilder.append(this.urlData);
      this.runtimeVersionUrl = localStringBuilder.toString();
    }
  }
  
  public void stop()
  {
    for (int i = 0; i < this.downloaderList.size(); i++) {
      ((EgretRuntimeLibrary)this.downloaderList.get(i)).stop();
    }
    ExecutorLab.releaseInstance();
  }
  
  public static abstract interface EgretRuntimeDownloadListener
  {
    public abstract void onError(String paramString);
    
    public abstract void onProgress(String paramString, int paramInt1, int paramInt2);
    
    public abstract void onProgressTotal(int paramInt1, int paramInt2);
    
    public abstract void onSuccess(Class<?> paramClass);
  }
  
  public class GameEngineMethod
  {
    public static final String CALL_EGRET_INTERFACE = "callEgretInterface";
    public static final String ENABLE_EGRET_RUNTIME_INTERFACE = "enableEgretRuntimeInterface";
    public static final String GAME_ENGINE_GET_VIEW = "game_engine_get_view";
    public static final String GAME_ENGINE_INIT = "game_engine_init";
    public static final String GAME_ENGINE_ON_PAUSE = "game_engine_onPause";
    public static final String GAME_ENGINE_ON_RESUME = "game_engine_onResume";
    public static final String GAME_ENGINE_ON_STOP = "game_engine_onStop";
    public static final String GAME_ENGINE_SET_LOADING_VIEW = "game_engine_set_loading_view";
    public static final String GAME_ENGINE_SET_OPTIONS = "game_engine_set_options";
    public static final String SET_RUNTIME_INTERFACE_SET = "setRuntimeInterfaceSet";
    
    public GameEngineMethod() {}
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes4-dex2jar.jar!/org/egret/plugin/mi/java/egretruntimelauncher/EgretRuntimeLauncher.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */